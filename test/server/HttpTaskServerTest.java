package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import server.adapters.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpServer server;
    InMemoryTaskManager manager;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();
    HttpClient client = HttpClient.newHttpClient();


    @BeforeEach
    public void init() throws IOException {
        HttpTaskServer serverTask = new HttpTaskServer();
        serverTask.start();
        server = serverTask.getServer();
        manager = serverTask.getManager();
    }

    @AfterEach
    public void stop() {
        server.stop(0);
    }


    public void addTasks() {
        Task buySock = new Task("Купить носки", "Закончились носки");
        manager.createTask(buySock);

        Task makeDinner = new Task("Сделать ужин", "Хочется кушать",
                LocalDateTime.now().plus(Duration.ofDays(1)), Duration.ofHours(1));
        manager.createTask(makeDinner);

        Epic goToShop = new Epic("Сходить в магазин", "Купить продукты");
        manager.createEpic(goToShop);

        Subtask buyMilk = new Subtask("Купить молоко", "Молоко кончается", 3,
                LocalDateTime.now().plus(Duration.ofHours(5)), Duration.ofHours(5));
        Subtask buyMeat = new Subtask("Купить мясо", "Кончается мясо", 3,
                LocalDateTime.now(), Duration.ofHours(3));
        manager.createSubtask(buyMilk);
        manager.createSubtask(buyMeat);
    }

    @DisplayName("Add tasks")
    @Test
    public void shouldBeAddTasks() {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task testTask = new Task("test", "test");
        testTask.setId(1);

        String jsonTask1 = gson.toJson(testTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Add task [status code]");
            assertEquals(testTask, manager.getTask(1), "Task equals");

            HttpResponse<String> responseNew = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(500, responseNew.statusCode(), "The task is already there");
            assertEquals(1, manager.getAllTasks().size(), "The task is already there");

        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Add epic & subtasks")
    @Test
    public void shouldBeAddEpicAndSubtasks() {
        URI uriEpics = URI.create("http://localhost:8080/epics");
        URI uriSubtasks = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("epic", "test");
        epic.setId(1);
        Subtask subtask1 = new Subtask("subtask1", "test", 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("subtask2", "test", 1);
        subtask2.setId(3);

        String jsonEpic = gson.toJson(epic);
        String jsonSubtask1 = gson.toJson(subtask1);
        String jsonSubtask2 = gson.toJson(subtask2);

        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(uriEpics)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        HttpRequest requestSubtask1 = HttpRequest.newBuilder()
                .uri(uriSubtasks)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1))
                .build();

        HttpRequest requestSubtask2 = HttpRequest.newBuilder()
                .uri(uriSubtasks)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask2))
                .build();

        try {
            HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseEpic.statusCode(), "Add epic [status code]");
            assertEquals(epic, manager.getEpic(1), "Epic equals");

            HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseSubtask1.statusCode(), "Add subtask1 [status code]");
            assertEquals(subtask1, manager.getSubtask(2), "Subtask1 equals");

            HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseSubtask2.statusCode(), "Add subtask2 [status code]");
            assertEquals(subtask2, manager.getSubtask(3), "Subtask2 equals");

        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Update task")
    @Test
    public void shouldBeUpdateTask() {
        addTasks();
        URI uri = URI.create("http://localhost:8080/tasks/1");
        Task buySock = new Task("Купить носки", "Закончились носки");
        buySock.setId(1);
        buySock.setStatus(Status.DONE);

        String jsonTask = gson.toJson(buySock);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseUpdate.statusCode(), "Update test status code");
            assertEquals(buySock, manager.getTask(1), "Update test task");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Update subtask")
    @Test
    public void shouldBeUpdateSubtask() {
        addTasks();
        URI uri = URI.create("http://localhost:8080/subtasks/4");
        Subtask subtask = new Subtask("Купить молоко", "Молоко кончается", 3,
                LocalDateTime.now().plus(Duration.ofHours(5)), Duration.ofHours(5));
        subtask.setId(4);
        subtask.setStatus(Status.DONE);
        String jsonSubtask = gson.toJson(subtask);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, responseUpdate.statusCode(), "Update subtask status code");
            assertEquals(subtask, manager.getSubtask(4), "Update subtask task");
            assertEquals(Status.IN_PROGRESS, manager.getEpic(3).getStatus(), "Update epic status");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Update epic")
    @Test
    public void shouldBeUpdateEpic() {
        addTasks();
        URI uri = URI.create("http://localhost:8080/epics/3");
        Epic epic = new Epic("Сходить в магазин", "Купить продукты");
        epic.setId(3);
        epic.setStatus(Status.IN_PROGRESS);

        String jsonEpic = gson.toJson(epic);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseUpdate.statusCode(), "Update status code");
            assertEquals(epic, manager.getEpic(3), "Update epic");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Delete task")
    @Test
    public void shouldBeDeleteTask() {
        addTasks();

        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestDeleteTask = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestDeleteTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, responseUpdate.statusCode(), "Delete task status code");
            assertEquals(1, manager.getAllTasks().size());
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Delete subtask")
    @Test
    public void shouldBeDeleteSubtask() {
        addTasks();

        URI uri = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest requestDeleteTask = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestDeleteTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, responseUpdate.statusCode(), "Delete subtask status code");
            assertEquals(1, manager.getAllSubtasks().size());
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Delete epic")
    @Test
    public void shouldBeDeleteEpic() {
        addTasks();

        URI uri = URI.create("http://localhost:8080/epics/3");
        HttpRequest requestDeleteEpic = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        try {
            HttpResponse<String> responseUpdate = client.send(requestDeleteEpic, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, responseUpdate.statusCode(), "Delete subtask status code");
            assertEquals(0, manager.getAllEpics().size(), "Delete epic");
            assertEquals(0, manager.getAllSubtasks().size(), "Delete all subtask for epic");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Get tasks")
    @Test
    public void shouldBeGetTaskAndTasks() {
        addTasks();

        URI uriTask = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGetTask = HttpRequest.newBuilder()
                .uri(uriTask)
                .GET()
                .build();

        URI uriTasks = URI.create("http://localhost:8080/tasks");
        HttpRequest requestGetAllTasks = HttpRequest.newBuilder()
                .uri(uriTasks)
                .GET()
                .build();
        try {
            HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseGetTasks = client.send(requestGetAllTasks, HttpResponse.BodyHandlers.ofString());

            Task task = gson.fromJson(responseGetTask.body(), Task.class);
            List<Task> tasks = gson.fromJson(responseGetTasks.body(), new ListTasksTypeToken().getType());

            assertEquals(200, responseGetTask.statusCode(), "Get task status code");
            assertEquals(200, responseGetTasks.statusCode(), "Get tasks status code");
            assertEquals(task, manager.getTask(1), "Get task");
            assertEquals(tasks, manager.getAllTasks(), "Get all tasks");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListTasksTypeToken extends TypeToken<List<Task>> {

    }

    @DisplayName("Get subtasks")
    @Test
    public void shouldBeGetSubtasks() {
        addTasks();

        URI uriSubtask = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest requestGetSubtask = HttpRequest.newBuilder()
                .uri(uriSubtask)
                .GET()
                .build();

        URI uriSubtasks = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestGetAllSubtasks = HttpRequest.newBuilder()
                .uri(uriSubtasks)
                .GET()
                .build();

        URI epicSubtasks = URI.create("http://localhost:8080/epics/3/subtasks");
        HttpRequest requestGetEpicSubtasks = HttpRequest.newBuilder()
                .uri(epicSubtasks)
                .GET()
                .build();
        try {
            HttpResponse<String> responseGetSubtask = client.send(requestGetSubtask, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseGetSubtasks = client.send(requestGetAllSubtasks, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseGetEpicSubtasks = client.send(requestGetEpicSubtasks, HttpResponse.BodyHandlers.ofString());

            Subtask subtask = gson.fromJson(responseGetSubtask.body(), Subtask.class);
            List<Subtask> subtasksList = gson.fromJson(responseGetSubtasks.body(), new ListSubtasksTypeToken().getType());
            List<Subtask> epicSubtasksList = gson.fromJson(responseGetEpicSubtasks.body(), new ListSubtasksTypeToken().getType());

            assertEquals(200, responseGetSubtask.statusCode(), "Get subtask status code");
            assertEquals(200, responseGetSubtasks.statusCode(), "Get subtasks status code");
            assertEquals(200, responseGetEpicSubtasks.statusCode(), "Get epic subtasks status code");

            assertEquals(subtask, manager.getSubtask(4), "Get subtask");
            assertEquals(subtasksList, manager.getAllSubtasks(), "Get all subtasks");
            assertEquals(epicSubtasksList, manager.getSubtasksByEpic(manager.getEpic(3)), "Get epic subtasks");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListSubtasksTypeToken extends TypeToken<List<Subtask>> {

    }

    @DisplayName("Get epics")
    @Test
    public void shouldBeGetEpics() {
        addTasks();

        URI uriEpic = URI.create("http://localhost:8080/epics/3");
        HttpRequest requestGetEpic = HttpRequest.newBuilder()
                .uri(uriEpic)
                .GET()
                .build();

        URI uriEpics = URI.create("http://localhost:8080/epics");
        HttpRequest requestGetAllEpics = HttpRequest.newBuilder()
                .uri(uriEpics)
                .GET()
                .build();
        try {
            HttpResponse<String> responseGetEpic = client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseGetEpics = client.send(requestGetAllEpics, HttpResponse.BodyHandlers.ofString());

            Epic epic = gson.fromJson(responseGetEpic.body(), Epic.class);
            List<Task> epics = gson.fromJson(responseGetEpics.body(), new ListEpicsTypeToken().getType());

            assertEquals(200, responseGetEpic.statusCode(), "Get task status code");
            assertEquals(200, responseGetEpics.statusCode(), "Get tasks status code");
            assertEquals(epic, manager.getEpic(3), "Get epic");
            assertEquals(epics, manager.getAllEpics(), "Get all epics");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListEpicsTypeToken extends TypeToken<List<Epic>> {

    }

    @DisplayName("Get history and prioritized list")
    @Test
    public void shouldBeGetHistory() {
        addTasks();

        URI uriHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestGetHistory = HttpRequest.newBuilder()
                .uri(uriHistory)
                .GET()
                .build();

        URI uriPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestGetPrioritized = HttpRequest.newBuilder()
                .uri(uriPrioritized)
                .GET()
                .build();
        try {
            HttpResponse<String> responseGetHistory = client.send(requestGetHistory, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseGetPrioritized = client.send(requestGetPrioritized, HttpResponse.BodyHandlers.ofString());

            List<Task> history = gson.fromJson(responseGetHistory.body(), new ListTasksTypeToken().getType());
            List<Task> prioritized = gson.fromJson(responseGetPrioritized.body(), new ListTasksTypeToken().getType());

            assertEquals(200, responseGetHistory.statusCode(), "Get history status code");
            assertEquals(200, responseGetPrioritized.statusCode(), "Get prioritized status code");
            assertEquals(history, manager.historyManager.getHistory(), "Get history");
            assertEquals(prioritized, manager.getPriorityTask().values(), "Get prioritized");
        } catch (IOException | InterruptedException ignored) {
        }
    }


}
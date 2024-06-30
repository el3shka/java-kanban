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
        Task buySock = new Task("Купить носки в подарок", "Носков не нашлось :(");
        manager.createTask(buySock);

        Task makeDinner = new Task("Приготовить ужин", "Время ужина",
                LocalDateTime.now().plus(Duration.ofDays(1)), Duration.ofHours(1));
        manager.createTask(makeDinner);

        Epic goToShop = new Epic("Сходить в магазин", "Купить продукты");
        manager.createEpic(goToShop);

        Subtask buyMilk = new Subtask("Купить молоко", "Молока нет", 3,
                LocalDateTime.now().plus(Duration.ofHours(5)), Duration.ofHours(5));
        Subtask buyMeat = new Subtask("Купить мясо", "Кончилось мясо", 3,
                LocalDateTime.now(), Duration.ofHours(3));
        manager.createSubtask(buyMilk);
        manager.createSubtask(buyMeat);
    }

    @DisplayName("Добавляем таск")
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
            assertEquals(201, response.statusCode(), "Добавляем таск Статус код");
            assertEquals(testTask, manager.getTask(1), "Таск существует");

            HttpResponse<String> responseNew = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(500, responseNew.statusCode(), "Таск уже олреади!");
            assertEquals(1, manager.getAllTasks().size(), "Таск уже олреади!");

        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Добавляем эпик и подзадачи")
    @Test
    public void shouldBeAddEpicAndSubtasks() {
        URI uriEpics = URI.create("http://localhost:8080/epics");
        URI uriSubtasks = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("epic", "test");
        epic.setId(1);
        Subtask subtask1 = new Subtask("Подзадача1", "test", 1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask("Подзадача2", "test", 1);
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
            assertEquals(201, responseEpic.statusCode(), "Добавляем эпик Статус код");
            assertEquals(epic, manager.getEpic(1), "Эпик существует");

            HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseSubtask1.statusCode(), "Добавляем подзадачу1 Статус код");
            assertEquals(subtask1, manager.getSubtask(2), "Подзадача1 существует");

            HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseSubtask2.statusCode(), "Добавляем подзадачу2 Статус код");
            assertEquals(subtask2, manager.getSubtask(3), "Подзадача2 существует");

        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Обновялем таск")
    @Test
    public void shouldBeUpdateTask() {
        addTasks();
        URI uri = URI.create("http://localhost:8080/tasks/1");
        Task buySock = new Task("Купить носки в подарок", "Носков не нашлось!");
        buySock.setId(1);
        buySock.setStatus(Status.DONE);

        String jsonTask = gson.toJson(buySock);

        HttpRequest requestUpdateTask = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        try {
            HttpResponse<String> responseUpdate = client.send(requestUpdateTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseUpdate.statusCode(), "Обновляем тест статус код");
            assertEquals(buySock, manager.getTask(1), "Обновляем тест таск");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Обновляем подзадачу")
    @Test
    public void shouldBeUpdateSubtask() {
        addTasks();
        URI uri = URI.create("http://localhost:8080/subtasks/4");
        Subtask subtask = new Subtask("Купить молоко", "Молока нет", 3,
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

            assertEquals(201, responseUpdate.statusCode(), "Обновляем подзадачу статус код");
            assertEquals(subtask, manager.getSubtask(4), "Update subtask task");
            assertEquals(Status.IN_PROGRESS, manager.getEpic(3).getStatus(), "Обновляем статус эпика");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Обновляем эпик")
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
            assertEquals(201, responseUpdate.statusCode(), "Обновляем статус код");
            assertEquals(epic, manager.getEpic(3), "Обновляем эпик");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Удаляем таск")
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
            assertEquals(200, responseUpdate.statusCode(), "Удаление таска статус код");
            assertEquals(1, manager.getAllTasks().size());
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Удаляем подзадачу")
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
            assertEquals(200, responseUpdate.statusCode(), "Удаление подзадачи статус код");
            assertEquals(1, manager.getAllSubtasks().size());
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Удаление эпика")
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
            assertEquals(200, responseUpdate.statusCode(), "Удаление подзадачи статус код");
            assertEquals(0, manager.getAllEpics().size(), "Эпик удален");
            assertEquals(0, manager.getAllSubtasks().size(), "Удалены все подзадачи эпика");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    @DisplayName("Получение тасков")
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

            assertEquals(200, responseGetTask.statusCode(), "Получение статус кода таска");
            assertEquals(200, responseGetTasks.statusCode(), "Получение статус кода таска");
            assertEquals(task, manager.getTask(1), "Получаем таск");
            assertEquals(tasks, manager.getAllTasks(), "Получаем все таски");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListTasksTypeToken extends TypeToken<List<Task>> {

    }

    @DisplayName("Получение подзадач")
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

            assertEquals(200, responseGetSubtask.statusCode(), "Получение статус кода подзадачи");
            assertEquals(200, responseGetSubtasks.statusCode(), "Получение статус кода подзадачи");
            assertEquals(200, responseGetEpicSubtasks.statusCode(), "Получение статус кода эпика");

            assertEquals(subtask, manager.getSubtask(4), "Получение подзадачи");
            assertEquals(subtasksList, manager.getAllSubtasks(), "Получаем все подзадачи");
            assertEquals(epicSubtasksList, manager.getSubtasksByEpic(manager.getEpic(3)), "Получаем эпик подзадачи");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListSubtasksTypeToken extends TypeToken<List<Subtask>> {

    }

    @DisplayName("Получаем эпики")
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

            assertEquals(200, responseGetEpic.statusCode(), "Получаем статус код таска");
            assertEquals(200, responseGetEpics.statusCode(), "Получаем статус код таска");
            assertEquals(epic, manager.getEpic(3), "Получаем эпик");
            assertEquals(epics, manager.getAllEpics(), "Получаем все эпики");
        } catch (IOException | InterruptedException ignored) {
        }
    }

    static class ListEpicsTypeToken extends TypeToken<List<Epic>> {

    }

    @DisplayName("Получаем историю и приоритезированный список")
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

            assertEquals(200, responseGetHistory.statusCode(), "Получаем статус код истории");
            assertEquals(200, responseGetPrioritized.statusCode(), "Получаем приоритезированный статус код");
            assertEquals(history, manager.historyManager.getHistory(), "Получаем историю");
            assertEquals(prioritized, manager.getPriorityTask().values(), "Получаем приоритезацию");
        } catch (IOException | InterruptedException ignored) {
        }
    }


}
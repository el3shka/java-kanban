package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exceptions.NotFoundException;
import exceptions.NullTaskException;
import server.adapters.*;
import model.Task;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class TasksHandler implements HttpHandler {
    private final InMemoryTaskManager manager;
    private final Gson gson;

    public TasksHandler(InMemoryTaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {

        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                handlerGetTasks(exchange, path);
                break;
            case "POST":
                handlerPostTasks(exchange, path);
                break;
            case "DELETE":
                handlerDeleteTasks(exchange, path);

        }
    }

    private void handlerGetTasks(HttpExchange exchange, String[] path) {
        switch (path.length) {
            case 2:
                List<Task> tasks = manager.getAllTasks();
                String jsonTasks = gson.toJson(tasks);
                writeResponse(jsonTasks, exchange, 200);
                break;
            case 3:
                try {
                    Task task = manager.getTask(Integer.parseInt(path[2]));
                    String jsonTask = gson.toJson(task);
                    writeResponse(jsonTask, exchange, 200);
                } catch (NumberFormatException e) {
                    writeResponse("ID типа должен быть INTEGER", exchange, 400);
                } catch (NotFoundException e) {
                    writeResponse("Таск не найден", exchange, 404);
                }
                break;
            default:
                writeResponse("Некорректный запрос", exchange, 400);
        }
    }

    private void handlerPostTasks(HttpExchange exchange, String[] path) {
        Task task;

        try (InputStream inputStream = exchange.getRequestBody()) {
            String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            task = gson.fromJson(jsonTask, Task.class);

        } catch (IOException e) {
            writeResponse("Некорректный запрос", exchange, 400);
            return;
        } catch (NullTaskException e) {
            writeResponse("Internal Server Error", exchange, 500);
            return;
        }

        switch (path.length) {
            // create
            case 2:
                try {
                    manager.getTask(task.getId());
                    writeResponse("Internal Server Error", exchange, 500);
                } catch (NotFoundException e) {
                    manager.createTask(task);
                    writeResponse("Успешное создание таска", exchange, 201);
                }
                break;
            // update
            case 3:
                try {
                    manager.updateTask(task);
                    writeResponse("Таск успешно удален", exchange, 201);
                } catch (NullTaskException e) {
                    writeResponse("Internal Server Error", exchange, 500);
                }
                break;
            default:
                writeResponse("Некорректный запрос", exchange, 400);
        }
    }

    private void handlerDeleteTasks(HttpExchange exchange, String[] path) {

        try {
            manager.removeTask(Integer.parseInt(path[2]));
        } catch (NotFoundException ignored) {
        }
        writeResponse("Таск успешно удален", exchange, 200);
    }

    private void writeResponse(String body, HttpExchange exchange, int code) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(code, body.getBytes().length);
            os.write(body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
    }
}
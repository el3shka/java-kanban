package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exceptions.NotFoundException;
import exceptions.NullTaskException;
import model.Epic;
import model.Subtask;
import server.adapters.*;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


class EpicsHandler implements HttpHandler {
    private final InMemoryTaskManager manager;
    private final Gson gson;

    public EpicsHandler(InMemoryTaskManager manager) {
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
                handlerGetEpics(exchange, path);
                break;
            case "POST":
                handlerPostEpics(exchange, path);
                break;
            case "DELETE":
                handlerDeleteEpics(exchange, path);
                break;
        }
    }

    private void handlerGetEpics(HttpExchange exchange, String[] path) {
        switch (path.length) {
            case 2:
                List<Epic> epics = manager.getAllEpics();
                String jsonEpics = gson.toJson(epics);
                writeResponse(jsonEpics, exchange, 200);
                break;
            case 3:
                try {
                    Epic epic = manager.getEpic(Integer.parseInt(path[2]));
                    String jsonEpic = gson.toJson(epic);
                    writeResponse(jsonEpic, exchange, 200);
                } catch (NumberFormatException e) {
                    writeResponse("ID epic need to be Integer", exchange, 400);
                } catch (NotFoundException e) {
                    writeResponse("EPIC not found", exchange, 404);
                }
                break;
            case 4:
                if (!path[3].equals("subtasks")) {
                    writeResponse("Bad or wrong request", exchange, 400);
                    break;
                }
                try {
                    Epic epic = manager.getEpic(Integer.parseInt(path[2]));
                    List<Subtask> subtasks = manager.getSubtasksByEpic(epic);
                    String jsonSubtasks = gson.toJson(subtasks);
                    writeResponse(jsonSubtasks, exchange, 200);
                } catch (NotFoundException ignored) {
                    writeResponse("EPIC not found", exchange, 404);
                }
                break;
            default:
                writeResponse("Bad or wrong request", exchange, 400);
        }
    }

    private void handlerPostEpics(HttpExchange exchange, String[] path) {
        Epic epic;

        try (InputStream inputStream = exchange.getRequestBody()) {
            String jsonEpic = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            epic = gson.fromJson(jsonEpic, Epic.class);

        } catch (IOException e) {
            writeResponse("Bad or wrong request", exchange, 400);
            return;
        } catch (NullTaskException e) {
            writeResponse("Internal Server Error", exchange, 500);
            return;
        }

        switch (path.length) {
            // create
            case 2:
                try {
                    manager.getEpic(epic.getId());
                    writeResponse("Internal Server Error", exchange, 500);
                } catch (NotFoundException e) {
                    manager.createEpic(epic);
                    writeResponse("Epic created with success", exchange, 201);
                }
                break;
            // update
            case 3:
                try {
                    manager.updateEpic(epic);
                    writeResponse("Epic updated with success", exchange, 201);
                } catch (NullTaskException e) {
                    writeResponse("Internal Server Error", exchange, 500);
                }
                break;
            default:
                writeResponse("Bad or wrong request", exchange, 400);
        }
    }

    private void handlerDeleteEpics(HttpExchange exchange, String[] path) {
        try {
            manager.removeEpic(Integer.parseInt(path[2]));
            writeResponse("Successfully delete Epic", exchange, 200);
        } catch (NotFoundException ignored) {
        }
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
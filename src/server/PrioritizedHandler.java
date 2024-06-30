package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import server.adapters.*;
import model.Task;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


class PrioritizedHandler implements HttpHandler {
    private final InMemoryTaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(InMemoryTaskManager manager) {
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

        switch (method) {
            case "GET":
                handlerGetPrioritized(exchange);
            case "POST":
                handlerPostPrioritized(exchange);
            case "DELETE":
                handlerDeletePrioritized(exchange);
        }
    }

    private void handlerGetPrioritized(HttpExchange exchange) {
        List<Task> prioritizedTasks = (List<Task>) manager.getPriorityTask().values();
        String jsonPrioritizedTasks = gson.toJson(prioritizedTasks);
        writeResponse(jsonPrioritizedTasks, exchange, 200);
    }


    private void handlerPostPrioritized(HttpExchange exchange) {
        writeResponse("Не найден", exchange, 404);
    }


    private void handlerDeletePrioritized(HttpExchange exchange) {
        writeResponse("Не найден", exchange, 404);
    }


    private void writeResponse(String body, HttpExchange exchange, int code) {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(code, 0);
            os.write(body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
    }
}
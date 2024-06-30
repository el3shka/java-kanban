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


class HistoryHandler implements HttpHandler {
    private final InMemoryTaskManager manager;
    private final Gson gson;

    public HistoryHandler(InMemoryTaskManager manager) {
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
                handlerGetHistory(exchange);
            case "POST":
                handlerPostHistory(exchange);
            case "DELETE":
                handlerDeleteHistory(exchange);
        }
    }

    private void handlerGetHistory(HttpExchange exchange) {
        List<Task> history = manager.historyManager.getHistory();
        String jsonHistory = gson.toJson(history);
        writeResponse(jsonHistory, exchange, 200);
    }


    private void handlerPostHistory(HttpExchange exchange) {
        writeResponse("Не найден", exchange, 404);
    }


    private void handlerDeleteHistory(HttpExchange exchange) {
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

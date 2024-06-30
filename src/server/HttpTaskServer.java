package server;

import com.sun.net.httpserver.HttpServer;
import service.InMemoryTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final InMemoryTaskManager manager;
    private final int port = 8080;
    private HttpServer server;

    public HttpTaskServer() {
        this.manager = Managers.getDefault();
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));

        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public InMemoryTaskManager getManager() {
        return manager;
    }

    public HttpServer getServer() {
        return server;
    }
}

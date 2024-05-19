package service;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getHistoryManager());
    }

    public static InMemoryHistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}

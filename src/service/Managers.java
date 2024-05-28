package service;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getHistoryManager());
    }

    public static InMemoryHistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(getHistoryManager(), file);
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager(getHistoryManager(), new File("resources/backup_output.csv"));
    }

}

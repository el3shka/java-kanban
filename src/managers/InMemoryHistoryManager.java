package managers;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> taskHistory = new ArrayList<>();
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskHistory.size() == 10) {
                taskHistory.remove(9);
            }
            taskHistory.add(task);
        }
    }
}
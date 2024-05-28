package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @DisplayName("HistoryManager don't change task")
    @Test
    void shouldBeTaskInHistoryWithoutChanges() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("a", "b");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0), "HistoryManager is not allowed to modify the task");
    }

    @DisplayName("Don't add duplicate")
    @Test
    void shouldBeReturnSize1IfAddTheSameTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("a", "b");
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @DisplayName("Chronology list returned")
    @Test
    void shouldBeReturnArrayListChronologyHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        Task task3 = new Task("Task3", "Desc3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);             // head
        historyManager.add(task2);             // medium
        historyManager.add(task3);             // tail            [task1, task2, task3]
        historyManager.remove(task1.getId());  // delete head     [task2, task3]
        historyManager.add(task1);             // new tail        [task2, task3, task1]
        historyManager.remove(task3.getId());  // delete medium   [task2, task1]
        historyManager.add(task3);             // add tail        [task2, task1, task3]
        historyManager.remove(task3.getId());  // remove tail     [task2, task1]
        List<Task> histFromManager = historyManager.getHistory();

        List<Task> hist = new ArrayList<>();
        hist.add(task2);
        hist.add(task1);
        assertEquals(hist, histFromManager);

    }


}

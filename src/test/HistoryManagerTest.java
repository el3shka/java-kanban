package test;

import managers.HistoryManager;
import managers.Managers;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task task = new Task("Задача", "Описание задачи");
    @BeforeEach
    void createObjectsToTestHistory() {
        historyManager = Managers.getDefaultHistory();

    }
    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {
        historyManager.add(new Task("Задача_1", "Описание задачи_1"));
        historyManager.add(new Task("Задача_1", "Описание задачи_1"));
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(historyManager.getHistory().get(0).getName(), historyManager.getHistory().get(1).getName());
        assertEquals(historyManager.getHistory().get(0).getDescription(), historyManager.getHistory().get(1).getDescription());
    }
}
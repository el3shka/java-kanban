package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @DisplayName("HistoryManager не изменил таск")
    @Test
    void shouldBeTaskInHistoryWithoutChanges() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task("a", "b");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0), "HistoryManager не разрешено изменять таск");
    }

    @DisplayName("Дубли не добавлены")
    @Test
    void shouldBeReturnSize1IfAddTheSameTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("a", "b");
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }
       
    @DisplayName("Получение хронологии")
    @Test
    void shouldBeReturnArrayListChronologyHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");
        Task task3 = new Task("Задача_3", "Описание_3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);             // node.head
        historyManager.add(task2);             // node.medium
        historyManager.add(task3);             // node.tail            [task1, task2, task3]
        historyManager.remove(task1.getId());  // delete node.head     [task2, task3]
        historyManager.add(task1);             // new node.tail        [task2, task3, task1]
        historyManager.remove(task3.getId());  // delete node.medium   [task2, task1]
        historyManager.add(task3);             // add node.tail        [task2, task1, task3]
        historyManager.remove(task3.getId());  // remove node.tail     [task2, task1]
        List<Task> histFromManager = historyManager.getHistory();

        List<Task> hist = new ArrayList<>();
        hist.add(task2);
        hist.add(task1);
        assertEquals(hist, histFromManager);
    }


}
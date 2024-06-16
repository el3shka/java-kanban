package service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class ManagersTest {
    @DisplayName("Return init and working InMemoryHistoryManager")
    @Test
    void shouldBeReturnInitAndWorkInMemoryHistoryManager() {
        InMemoryHistoryManager historyManager = Managers.getHistoryManager();
        historyManager.add(new Task("A", "b"));
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(),
                "Manager return not working InMemoryHistoryManager");
    }

    @DisplayName("Return init and working InMemoryTaskManager")
    @Test
    void shouldBeReturnInitAndWorkInMemoryTaskManager() {
        InMemoryTaskManager testTaskManager = Managers.getDefault();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

        Assertions.assertEquals(testTaskManager.allTask, taskManager.allTask,
                "Managers not return InMemoryTaskManager from getDefault");
        Assertions.assertEquals(testTaskManager.allSubtask, taskManager.allSubtask,
                "Managers not return InMemoryTaskManager from getDefault");
        Assertions.assertEquals(testTaskManager.allEpics, taskManager.allEpics,
                "Managers not return InMemoryTaskManager from getDefault");
    }
}

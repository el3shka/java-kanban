package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager manager;

    @BeforeEach
    void beforeEach() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);

        Task task = new Task("a", "b");
        manager.createTask(task);
        Epic epic = new Epic("a", "b");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("a", "b", epic);
        manager.createSubtask(subtask);
    }

    @DisplayName("Возврат корректировки id")
    @Test
    void shouldReturnIdCreatedTasks() {
        assertEquals(1, manager.getTask(1).getId(), "Id task not 1");
        assertEquals(2, manager.getEpic(2).getId(), "Id epic not 2");
        assertEquals(3, manager.getSubtask(3).getId(), "Id subtask not 3");
    }

    @DisplayName("Возврат корректировки статуса после апдейта")
    @Test
    void shouldReturnNewIdTaskAfterUpdate() {
        Task testTask = manager.getTask(1);
        Epic testEpic = manager.getEpic(2);
        Subtask testSubtask = manager.getSubtask(3);

        manager.inDone(testTask);
        manager.updateTask(testTask);

        manager.inProgress(testSubtask);
        manager.updateSubtask(testSubtask);

        manager.updateEpic(testEpic);

        assertEquals(Status.DONE, manager.getTask(1).getStatus(),
                "Обновление статуса для таска не работает");
        assertEquals(Status.IN_PROGRESS, manager.getEpic(2).getStatus(),
                "Обновление статуса для эпика не работает");
        assertEquals(Status.IN_PROGRESS, manager.getSubtask(3).getStatus(),
                "Обновление статуса для подзадачи не работает");
    }

    @DisplayName("Правильное удаление таска")
    @Test
    void shouldBeNullTaskAfterRemoveTask() {
        manager.removeTask(1);
        assertNull(manager.allTask.get(1), "Таск не удален");
    }

    @DisplayName("Правильное удаление подзадачи")
    @Test
    void shouldBeNullSubtaskAfterRemoveSubtask() {
        Subtask subtask = manager.getSubtask(3);

        Epic epic = manager.getEpic(subtask.getEpicId());

        manager.removeSubtask(3);
        assertNull(manager.allSubtask.get(3), "Подзадача не удалена из всех подзадач");
        assertFalse(manager.getSubtasksByEpic(epic).contains(subtask), "Подзадача не удалена из эпика");
    }

    @DisplayName("Правильное удаление эпика")
    @Test
    void shouldBeNullEpicAfterRemoveEpic() {
        manager.removeEpic(2);
        assertNull(manager.getEpic(2), "Эпик не удален");
    }

}
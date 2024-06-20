package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;

import java.util.List;


class EpicTest {
    static Epic epic;
    static Subtask subtaskA;
    static Subtask subtaskB;

    @BeforeAll
    static void beforeAll() {
        epic = new Epic("a", "b");
        epic.setId(1);
        subtaskA = new Subtask("a", "a", 1);
        subtaskA.setId(2);

        subtaskB = new Subtask("b", "b", 1);
        subtaskB.setId(2);
    }

    @DisplayName("Эпик с таким же id - существует")
    @Test
    void shouldBeEqualsEpicsWithTheSameId() {
        Epic testEpic = new Epic("z", "x");
        testEpic.setId(epic.getId());
        Assertions.assertEquals(epic, testEpic, "Эпик с таким же id не существует");
    }

    @DisplayName("Эпик не может быть подзадачей")
    @Test
    void shouldFalseIfEpicIsSubtaskYourself() {
        epic.addSubtaskId(epic.getId());
        List<Integer> result = epic.getSubtaskIds();
        Assertions.assertFalse(result.contains(epic.getId()), "Эпик не может быть подзадачей самого себя");
    }

    @DisplayName("Epic Статус")
    @Test
    void shouldBeChangeStatusFromSubtasks() {
        InMemoryTaskManager manager = Managers.getDefault();
        manager.createEpic(epic);
        manager.createSubtask(subtaskA);
        manager.createSubtask(subtaskB);
        Assertions.assertEquals(Status.NEW, manager.getEpic(1).getStatus(),
                "Epic в NEW");

        subtaskA.setStatus(Status.DONE);
        manager.updateSubtask(subtaskA);
        Assertions.assertEquals(Status.IN_PROGRESS, manager.getEpic(1).getStatus(),
                "Epic в IN_PROGRESS");

        subtaskA.setStatus(Status.DONE);
        subtaskB.setStatus(Status.DONE);
        manager.updateSubtask(subtaskA);
        manager.updateSubtask(subtaskB);
        Assertions.assertEquals(Status.DONE, manager.getEpic(1).getStatus(),
                "Epic в DONE");

        subtaskA.setStatus(Status.IN_PROGRESS);
        subtaskB.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtaskA);
        manager.updateSubtask(subtaskB);
        Assertions.assertEquals(Status.IN_PROGRESS, manager.getEpic(1).getStatus(),
                "Epic в IN PROGRESS");
    }

}

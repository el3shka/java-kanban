package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    private static Subtask subtask;

    @BeforeAll
    static void beforeAll() {
        Epic epic = new Epic("a", "b");
        epic.setId(1);
        subtask = new Subtask("a", "b", 1);
        subtask.setId(2);
    }

    @DisplayName("Сабтаск с таким же id - существует")
    @Test
    void shouldBeEqualsSubtaskWithTheSameId() {
        Epic tempEpic = new Epic("b", "d");
        tempEpic.setId(3);
        Subtask testSubtask = new Subtask("z", "x", 3);
        testSubtask.setId(2);

        Assertions.assertEquals(subtask, testSubtask, "Подзадачи с одинаковым id не равны");
    }

    @DisplayName("Подзадача не может быть эпиком по id")
    @Test
    void shouldNotEqualsSubtaskIdAndEpicId() {
        subtask.setId(1);
        Assertions.assertNotEquals(subtask.getEpicId(), subtask.getId(), "Подзадача не может быть эпиком");
    }
}
package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;


class EpicTest {
    static Epic epic;
    static Subtask subtask;

    @BeforeAll
    static void beforeAll() {
        epic = new Epic("a", "b");
        epic.setId(1);
        subtask = new Subtask("z", "x", epic);
        subtask.setId(2);
    }

    @DisplayName("Эпик с таким же id - существует")
    @Test
    void shouldBeEqualsEpicsWithTheSameId() {
        Epic testEpic = new Epic("z", "x");
        testEpic.setId(epic.getId());
        Assertions.assertEquals(epic, testEpic, "Эпики с одинаковым id не равны");
    }

    @DisplayName("Эпик не может быть сабтаском")
    @Test
    void shouldFalseIfEpicIsSubtaskYourself() {
        epic.addSubtaskId(epic.getId());
        List<Integer> result = epic.getSubtaskIds();
        Assertions.assertFalse(result.contains(epic.getId()), "Эпик не может быть сабтаском");
    }

}
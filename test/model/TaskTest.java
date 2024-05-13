package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTest {
     private static Task task;

    @BeforeAll
    static void beforeAll() {
        task = new Task("a", "b");
        task.setId(1);
    }

    @DisplayName("Таск с таким же id - существует")
    @Test
    void shouldBeEqualsTaskWithTheSameId() {
        Task testTask = new Task("z", "x");
        testTask.setId(1);
        Assertions.assertEquals(testTask, task);
    }
}
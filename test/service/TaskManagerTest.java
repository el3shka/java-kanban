package service;

import exceptions.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @DisplayName("Create and return task")
    @Test
    public void shouldCreateAndReturnedTask() {
        Task task = new Task("task", "test");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(1);
        assertNotNull(savedTask, "Task not found.");
        assertEquals(task, savedTask, "Tasks not equals");
    }

    @DisplayName("Create and return epic")
    @Test
    public void shouldCreateAndReturnedEpic() {
        Epic epic = new Epic("epic", "test");
        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpic(1);

        assertNotNull(savedEpic, "Epic not found");
        assertEquals(epic, savedEpic, "Epics not equals");
    }

    @DisplayName("Create and return subtask")
    @Test
    public void shouldCreateSubtask() {
        Epic epic = new Epic("epic", "test");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtask", "test", 1);
        taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(2);
        assertNotNull(savedSubtask, "subtask not found");
        assertEquals(subtask, savedSubtask, "subtask not equals");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "subtask not found");
        assertEquals(1, subtasks.size(), "Uncorrected subtasks count");
        assertEquals(subtask, subtasks.getFirst(), "Subtask not equals");

        List<Integer> idList = epic.getSubtaskIds();
        int id = idList.getFirst();
        assertEquals(taskManager.getSubtask(id), subtask, "subtask not equals");
        assertEquals(taskManager.getEpic(subtask.getEpicId()), epic, "epic subtask not equals");
    }

    @DisplayName("UpdateTask")
    @Test
    public void shouldUpdateTask() {
        Task testTask1 = new Task("task", "test", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(testTask1);
        testTask1.setId(1);
        testTask1.setStatus(Status.DONE);
        testTask1.setDuration(Duration.ZERO);
        testTask1.setStartTime(LocalDateTime.now().plus(Duration.ofDays(1)));
        taskManager.updateTask(testTask1);

        assertEquals(testTask1, taskManager.getTask(1), "update task not equals");
        testTask1.setId(10);
        assertThrows(NullTaskException.class, () -> taskManager.updateTask(testTask1),
                "NullTaskException task");
        assertThrows(NotFoundException.class, () -> taskManager.getTask(testTask1.getId()),
                "NotFoundTaskException task");
    }

    @DisplayName("Get and update subtask")
    @Test
    public void shouldGetAndUpdateSubtask(){
        Epic epic = new Epic("epic", "test");
        Subtask subtask = new Subtask("subtask", "test", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(2), "Get subtask");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(2), "Update subtask");
    }

    @DisplayName("Get and update epic")
    @Test
    public void shouldGetAndUpdateEpic() {
        Epic epic = new Epic("epic", "test");
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(1), "Get epic");

        epic.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        assertEquals(epic, taskManager.getEpic(1), "Update epic");
    }

    @DisplayName("Exception get not exist epic")
    @Test
    public void shouldReturnExceptionGetEpic() {
        Epic epic = new Epic("epic", "test");
        taskManager.createEpic(epic);
        assertThrows(NotFoundException.class, () -> taskManager.getEpic(10));
    }

    @DisplayName("Remove subtasks after removed epic")
    @Test
    public void shouldRemoveSubtaskAfterRemoveEpic() {
        Epic epic = new Epic("epic", "test");
        Subtask subtask1 = new Subtask("subtask1", "test", 1);
        Subtask subtask2 = new Subtask("subtask2", "test", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getAllSubtasks().size(), "Subtasks removed after remove Epic");
    }

    @DisplayName("Removed all tasks")
    @Test
    public void shouldRemovedAllTasks() {
        Task task1 = new Task("task1", "test");
        Task task2 = new Task("task2", "test");
        Epic epic = new Epic("epic", "test");
        Subtask subtask1 = new Subtask("subtask1", "test", 1);
        Subtask subtask2 = new Subtask("subtask2", "test", 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "task allRemoved");

        taskManager.removeAllSubtask();
        assertEquals(0, taskManager.getSubtasksByEpic(taskManager.getEpic(1)).size());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @DisplayName("Check crossing")
    @Test
    public void shouldCrossing() {
        Epic epic = new Epic("epic", "test");
        epic.setId(1);
        taskManager.createEpic(epic);
        Task testTask = new Task("task", "test", LocalDateTime.now(), Duration.ofHours(1));
        taskManager.createTask(testTask);
        Task crossTask = new Task("task1", "test", LocalDateTime.now(), Duration.ofHours(1));
        assertThrows(CrossingTaskException.class, () -> taskManager.createTask(crossTask), "task is crossing");

        Subtask crossSubtask = new Subtask("subtask", "test", 1,
                LocalDateTime.now(), Duration.ofDays(1));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        crossSubtask.setStartTime(LocalDateTime.now().minus(Duration.ofHours(2)));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        crossSubtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        Task notCrossTask = new Task("task2", "test",
                LocalDateTime.now().plus(Duration.ofDays(1)), Duration.ofMinutes(30));
        taskManager.createTask(notCrossTask);
        assertEquals(notCrossTask, taskManager.getTask(7));

        Subtask notCrossSubtask = new Subtask("subtask", "test", 1,
                LocalDateTime.now().plus(Duration.ofDays(2)), Duration.ofMinutes(50));
        taskManager.createSubtask(notCrossSubtask);
        assertEquals(notCrossSubtask, taskManager.getSubtask(8));
    }
}

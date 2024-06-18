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

    @DisplayName("Создание и возврат Таска")
    @Test
    public void shouldCreateAndReturnedTask() {
        Task task = new Task("Таск", "Тест");
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(1);
        assertNotNull(savedTask, "Таск не найден.");
        assertEquals(task, savedTask, "Таски не существует");
    }

    @DisplayName("Создание и возврат Эпика")
    @Test
    public void shouldCreateAndReturnedEpic() {
        Epic epic = new Epic("Эпик", "Тест");
        taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpic(1);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не существует");
    }

    @DisplayName("Создание и возврат Подзадачи")
    @Test
    public void shouldCreateSubtask() {
        Epic epic = new Epic("Эпик", "Тест");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Позадача", "Тест", 1);
        taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(2);
        assertNotNull(savedSubtask, "Позадача не найдена");
        assertEquals(subtask, savedSubtask, "Позадача не существует");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Позадача не найдена");
        assertEquals(1, subtasks.size(), "Неккорректный подсчет подзадач");
        assertEquals(subtask, subtasks.getFirst(), "Позадача не равны");

        List<Integer> idList = epic.getSubtaskIds();
        int id = idList.getFirst();
        assertEquals(taskManager.getSubtask(id), subtask, "Позадача не равны");
        assertEquals(taskManager.getEpic(subtask.getEpicId()), epic, "Эпик подзадачи не равен");
    }

    @DisplayName("Обновление Таска")
    @Test
    public void shouldUpdateTask() {
        Task testTask1 = new Task("Таск", "Тест", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(testTask1);
        testTask1.setId(1);
        testTask1.setStatus(Status.DONE);
        testTask1.setDuration(Duration.ZERO);
        testTask1.setStartTime(LocalDateTime.now().plus(Duration.ofDays(1)));
        taskManager.updateTask(testTask1);

        assertEquals(testTask1, taskManager.getTask(1), "обновляемый таск не найден");
        testTask1.setId(10);
        assertThrows(NullTaskException.class, () -> taskManager.updateTask(testTask1),
                "NullTaskException Таск");
        assertThrows(NotFoundException.class, () -> taskManager.getTask(testTask1.getId()),
                "NotFoundTaskException Таск");
    }

    @DisplayName("Получение и обновление Подзадачи")
    @Test
    public void shouldGetAndUpdateSubtask(){
        Epic epic = new Epic("Эпик", "Тест");
        Subtask subtask = new Subtask("Подзадача", "Тест", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(2), "Получение Подзадачи");
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtask(2), "Апдейт Подзадачи");
    }

    @DisplayName("Получение и обновление Эпика")
    @Test
    public void shouldGetAndUpdateEpic() {
        Epic epic = new Epic("Эпик", "Тест");
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpic(1), "Получение Эпика");

        epic.setStatus(Status.DONE);
        taskManager.updateEpic(epic);
        assertEquals(epic, taskManager.getEpic(1), "Апдейт Эпика");
    }

    @DisplayName("Exception при отсутствии Эпика")
    @Test
    public void shouldReturnExceptionGetEpic() {
        Epic epic = new Epic("Эпик", "Тест");
        taskManager.createEpic(epic);
        assertThrows(NotFoundException.class, () -> taskManager.getEpic(10));
    }

    @DisplayName("Удаление подзадач после удаления Эпиков")
    @Test
    public void shouldRemoveSubtaskAfterRemoveEpic() {
        Epic epic = new Epic("Эпик", "Тест");
        Subtask subtask1 = new Subtask("Подзадача1", "Тест", 1);
        Subtask subtask2 = new Subtask("Подзадача2", "Тест", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeEpic(1);
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи удалены после удаления Эпиков");
    }

    @DisplayName("Удаление всех тасков")
    @Test
    public void shouldRemovedAllTasks() {
        Task task1 = new Task("Таск1", "Тест");
        Task task2 = new Task("Таск2", "Тест");
        Epic epic = new Epic("Эпик", "Тест");
        Subtask subtask1 = new Subtask("Подзадача1", "Тест", 1);
        Subtask subtask2 = new Subtask("Подзадача2", "Тест", 1);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Таски все удалены");

        taskManager.removeAllSubtask();
        assertEquals(0, taskManager.getSubtasksByEpic(taskManager.getEpic(1)).size());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @DisplayName("Чек пересечений")
    @Test
    public void shouldCrossing() {
        Epic epic = new Epic("Эпик", "Тест");
        epic.setId(1);
        taskManager.createEpic(epic);
        Task testTask = new Task("Таск", "Тест", LocalDateTime.now(), Duration.ofHours(1));
        taskManager.createTask(testTask);
        Task crossTask = new Task("Таск1", "Тест", LocalDateTime.now(), Duration.ofHours(1));
        assertThrows(CrossingTaskException.class, () -> taskManager.createTask(crossTask), "Таски пересекаются");

        Subtask crossSubtask = new Subtask("Подзадача", "Тест", 1,
                LocalDateTime.now(), Duration.ofDays(1));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        crossSubtask.setStartTime(LocalDateTime.now().minus(Duration.ofHours(2)));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        crossSubtask.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        assertThrows(CrossingTaskException.class, () -> taskManager.createSubtask(crossSubtask));

        Task notCrossTask = new Task("Таск2", "Тест",
                LocalDateTime.now().plus(Duration.ofDays(1)), Duration.ofMinutes(30));
        taskManager.createTask(notCrossTask);
        assertEquals(notCrossTask, taskManager.getTask(7));

        Subtask notCrossSubtask = new Subtask("Подзадача", "Тест", 1,
                LocalDateTime.now().plus(Duration.ofDays(2)), Duration.ofMinutes(50));
        taskManager.createSubtask(notCrossSubtask);
        assertEquals(notCrossSubtask, taskManager.getSubtask(8));
    }
}

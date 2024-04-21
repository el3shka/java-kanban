package test;

import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    protected Task task;
    protected Epic epic;
    protected Subtask subTask;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach(){
        taskManager = Managers.getDefault();
        task = new Task("Тест задача", "Тест описание задачи");
        epic = new Epic("Тест эпик", "Тест описание эпика");
        subTask = new Subtask("Тест подзадача", "Тест описание подзадачи", epic.getId());
    }

    @Test
    void addCreateTask() {
        Task task = new Task("Тест добавляем задачу", "Тест добавляем описание задачи");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задачи не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(task.getId(), savedTask.getId(), "Id не совпадают.");
        assertEquals(task.getName(), savedTask.getName(), "Имена у задач не совпадают.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания у задач не совпадают.");
        assertEquals(task.getTaskStatus(), savedTask.getTaskStatus(), "Статусы у задач не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не пришли :(");
        assertEquals(1, tasks.size(), "Некорректное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addCreateEpic() {
        Epic epic = new Epic("Тест добавляем эпик", "Тест добавляем описание эпику");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавляем подзадачу", "Тест добавляем описание подзадаче",  epicId);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(epic.getId(), savedEpic.getId(), "Id у эпиков не совпадают.");
        assertEquals(epic.getName(), savedEpic.getName(), "Имена у эпиков не совпадают.");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Описания у эпиков не совпадают.");
        assertEquals(epic.getTaskStatus(), savedEpic.getTaskStatus(), "Статусы у эпиков не совпадают.");
        assertEquals(epic.getSubtasksId(), savedEpic.getSubtasksId(), "Подзадачи у эпиков не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпиков нет :(");
        assertEquals(1, epics.size(), "Некорректное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addCreateSubTask() {
        Epic epic = new Epic("Тест добавляем эпик", "Тест добавляем описание эпику");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавляем подзадачу", "Тест добавляем описание подзадаче", epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        final Subtask savedSubTask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubTask, "Подзадачи нет :(");
        assertEquals(subtask, savedSubTask, "Подзадачи не совпадают.");
        assertEquals(subtask.getId(), savedSubTask.getId(), "Id у подзадач не совпадают.");
        assertEquals(subtask.getName(), savedSubTask.getName(), "Имена у подзадач не совпадают.");
        assertEquals(subtask.getDescription(), savedSubTask.getDescription(), "Описания у подзадач не совпадают.");
        assertEquals(subtask.getTaskStatus(), savedSubTask.getTaskStatus(), "Статусы у подзадач не совпадают.");
        assertEquals(subtask.getEpicId(), savedSubTask.getEpicId(), "Эпики у подзадач не совпадают.");


        final List<Subtask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "Подзадач нет :(");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }
    @Test
    void addSubTaskAsEpic() {
        Epic epic = new Epic("Тест добавляем эпик", "Тест добавляем описание эпику");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавляем подзадачу_1", "Тест добавляем описание подзадаче_1", epicId);
        final int subtaskId = taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("Тест добавляем подзадачу_2", "Тест добавляем описание подзадаче_2", subtaskId);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final Subtask savedSubTask = taskManager.getSubtask(subtaskId2);

        assertNull(savedSubTask, "Подзадача была добавлена как эпик.");
        final List<Subtask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "Подзадач нет :(");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач.");

    }
    @Test
    void addEpicAsSubTask() {
        Epic epic = new Epic("Тест добавляем эпик_1", "Тест добавляем описание эпику_1");
        final int epicId = taskManager.createEpic(epic);
        Object epic2 = new Epic("ест добавляем эпик_2", "Тест добавляем описание эпику_2");
        taskManager.createEpic(epic);

        assertThrows(ClassCastException.class, () -> taskManager.createSubtask((Subtask) epic2));
    }
    @Test
    void getDefaultManagers() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }
}
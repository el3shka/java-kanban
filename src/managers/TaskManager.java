package managers;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubTasks();

    models.Task getTask(Integer id);
    Subtask getSubtask(Integer id);
    Epic getEpic(Integer id);
    void deleteTask(Integer id);
    void deleteSubtask(Integer id);
    void deleteEpic(Integer id);
    int createTask(models.Task t);
    int createSubtask(Subtask s);
    int createEpic(Epic e);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubTasks();
    List<Subtask> getSubtasksByEpic(Epic epic);
    List<Task> getHistory();
}
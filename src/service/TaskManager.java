package service;

import org.jetbrains.annotations.NotNull;
import model.Epic;
import model.Subtask;
import model.Task;
import java.util.HashMap;
import java.util.TreeSet;

import java.util.ArrayList;

/*
public interface TaskManager {
    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    ArrayList<Task> getById(int id);

    boolean createTask(@NotNull Task task);

    Task updateTask(@NotNull Task task);

    void deleteSubtaskById(int id);

    void deleteEpicById(int epicId);

    boolean deleteEpicOrSubtask(int id);

    ArrayList<Subtask> getAllSubtaskEpic(@NotNull int groupId);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    ArrayList<Task> getHistory();

    void removeHistory(int superId);

    void clearHistory();
    ArrayList<Task> getPrioritizedTasks();
}
*/


public interface TaskManager {
    void delAll();
    Task getById(int id);
    void create (Task task);
    void updateTask (Task task);
    void updateTask (Subtask sub);
    void delById(int id);
    HashMap<Integer, Task> getTaskList();
    HashMap<Integer, Epic> getEpicList();
    HashMap<Integer, Subtask> getSubTaskList();
    HashMap<Integer, Subtask> getSubTaskList(Epic epic);
    // history save release?
   // HistoryManager getHistory();
    TreeSet<Task> getPrioritizedTasks();
}
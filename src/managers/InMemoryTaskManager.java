package managers;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected Integer id = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    private Integer getId() {
        return ++id;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtasksId();
            epic.setTaskStatus(Status.NEW);
        }
    }

    @Override
    public Task getTask(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(Integer id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) tasks.remove(id);
    }

    @Override
    public void deleteSubtask(Integer id) {
        if (subTasks.containsKey(id)) {
            Subtask subtask = subTasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            subTasks.remove(id);
            if (epic != null) {
                epic.deleteSubtaskId(id);
                updateStatusEpic(epic);
            }
        }
    }

    @Override
    public void deleteEpic(Integer id) {
        if (epics.containsKey(id)) {
            for (Integer s : epics.get(id).getSubtasksId()) {
                subTasks.remove(s);
            }
            epics.remove(id);
        }
    }

    @Override
    public int createTask(Task t) {
        id = getId();
        t.setId(id);
        tasks.put(id, t);
        return id;
    }

    @Override
    public int createSubtask(Subtask s) {
        Epic epic = epics.get(s.getEpicId());
        if (epic != null ) {
            id = getId();
            s.setId(id);
            subTasks.put(id, s);
            epic.addSubtaskId(s.getId());
            updateStatusEpic(epic);
            return id;
        }
        return -1;
    }

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtasksId().size() == 0) {
                epic.setTaskStatus(Status.NEW);
            } else {
                int cDone = 0;
                int cNew = 0;

                for (int subtaskId : epic.getSubtasksId()) {
                    if (subTasks.get(subtaskId).getTaskStatus() == Status.DONE) {
                        cDone++;
                    }
                    if (subTasks.get(subtaskId).getTaskStatus() == Status.NEW) {
                        cNew++;
                    }
                }

                if (cDone == epic.getSubtasksId().size()) {
                    epic.setTaskStatus(Status.DONE);
                } else if (cNew == epic.getSubtasksId().size()) {
                    epic.setTaskStatus(Status.NEW);
                } else {
                    epic.setTaskStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    @Override
    public int createEpic(Epic e) {
        id = getId();
        e.setId(id);
        epics.put(id, e);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic old = epics.get(epic.getId());
            old.setDescription(epic.getDescription());
            old.setName(epic.getName());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (subTasks.containsKey(subtask.getId()) && epic != null) {
            subTasks.put(subtask.getId(), subtask);
            updateStatusEpic(epic);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subTasks = new ArrayList<>();
        for (Integer id : epic.getSubtasksId()) {
            subTasks.add(getSubtask(id));
        }
        return subTasks;
    }
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
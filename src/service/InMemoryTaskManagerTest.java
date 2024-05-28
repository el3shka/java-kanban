package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int taskCounts = 1;
    Map<Integer, Task> allTask = new HashMap<>();
    Map<Integer, Subtask> allSubtask = new HashMap<>();
    Map<Integer, Epic> allEpics = new HashMap<>();
    public HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry : allTask.entrySet()) {
            tasks.add(getTask(entry.getKey()));
        }
        return tasks;

    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> entry : allSubtask.entrySet()) {
            subtasks.add(getSubtask(entry.getKey()));
        }
        return subtasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epics = new ArrayList<>();
        for (Map.Entry<Integer, Epic> entry : allEpics.entrySet()) {
            epics.add(getEpic(entry.getKey()));
        }
        return epics;
    }

    @Override
    public void removeAllTasks() {
        for (Map.Entry<Integer, Task> entry : allTask.entrySet()) {
            historyManager.remove(entry.getKey());
        }
        allTask.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (Map.Entry<Integer, Subtask> entry : allSubtask.entrySet()) {
            historyManager.remove(entry.getKey());
        }
        allSubtask.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Map.Entry<Integer, Epic> entry : allEpics.entrySet()) {
            historyManager.remove(entry.getKey());
        }
        allEpics.clear();
        removeAllSubtask();
    }

    @Override
    public Task getTask(int id) {
        if (allTask.get(id) == null) return null;

        Task selectTask = allTask.get(id);
        Task returnTask = new Task(selectTask.getName(), selectTask.getDescription());
        returnTask.setId(selectTask.getId());
        returnTask.setStatus(selectTask.getStatus());
        historyManager.add(returnTask);
        return returnTask;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (allSubtask.get(id) == null) return null;

        Subtask selectSubtask = allSubtask.get(id);
        Subtask returnSubtask = new Subtask(selectSubtask.getName(), selectSubtask.getDescription(), selectSubtask.getEpicId());
        returnSubtask.setId(selectSubtask.getId());
        returnSubtask.setStatus(selectSubtask.getStatus());
        historyManager.add(returnSubtask);
        return returnSubtask;
    }

    @Override
    public Epic getEpic(int id) {
        if (allEpics.get(id) == null) return null;

        Epic selectEpic = allEpics.get(id);
        Epic returnEpic = new Epic(selectEpic.getName(), selectEpic.getDescription());
        returnEpic.setId(selectEpic.getId());
        returnEpic.setStatus(selectEpic.getStatus());
        if (selectEpic.getSubtaskIds().isEmpty()) {
            historyManager.add(returnEpic);
            return returnEpic;
        }

        for (Integer subtaskId : selectEpic.getSubtaskIds()) {
            returnEpic.addSubtaskId(subtaskId);
        }
        historyManager.add(returnEpic);
        return returnEpic;
    }

    @Override
    public Integer generateId() {
        return taskCounts++;
    }

    @Override
    public void createTask(Task task) {
        int id = generateId();
        task.setId(id);
        allTask.put(id, task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic tempEpic = allEpics.get(subtask.getEpicId());
        if (tempEpic == null) return;

        int id = generateId();
        subtask.setId(id);
        tempEpic.addSubtaskId(id);
        allSubtask.put(id, subtask);
        updateEpicStatus(tempEpic);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        allEpics.put(id, epic);
    }

    @Override
    public void updateTask(Task task) {
        if (allTask.get(task.getId()) == null) return;
        allTask.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (allSubtask.get(subtask.getId()) == null) return;
        allSubtask.put(subtask.getId(), subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (allEpics.get(epic.getId()) == null) return;
        allEpics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void removeTask(int id) {
        allTask.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = allSubtask.get(id);
        Epic epic = allEpics.get(subtask.getEpicId());
        epic.removeSubtaskId(subtask.getId());
        updateEpic(epic);
        allSubtask.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic selectedEpic = allEpics.get(id);
        if (selectedEpic.getSubtaskIds().isEmpty()) {
            allEpics.remove(id);
            historyManager.remove(id);
            return;
        }
        for (Integer subtaskId : selectedEpic.getSubtaskIds()) { // remove all subtasks epic
            allSubtask.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        allEpics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        Epic selectedEpic = allEpics.get(epic.getId());
        ArrayList<Integer> subtaskIds = selectedEpic.getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            subtasksByEpic.add(allSubtask.get(subtaskId));
        }
        return subtasksByEpic;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;
        Epic selectedEpic = allEpics.get(epic.getId());
        ArrayList<Integer> selectedSubtaskId = selectedEpic.getSubtaskIds();
        for (Integer subtaskId : selectedSubtaskId) {
            Status statusSubtask = allSubtask.get(subtaskId).getStatus();
            if (Status.NEW.equals(statusSubtask)) {
                isNew = true;
            } else if (Status.IN_PROGRESS.equals(statusSubtask)) {
                isInProgress = true;
            } else if (Status.DONE.equals(statusSubtask)) {
                isDone = true;
            }
        }
        if (!isNew && !isInProgress && isDone) {
            selectedEpic.setStatus(Status.DONE);
        } else if (isNew && !isInProgress && !isDone) {
            selectedEpic.setStatus(Status.NEW);
        } else {
            selectedEpic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void inProgress(Task task) {
        task.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public void inDone(Task task) {
        task.setStatus(Status.DONE);
    }
}

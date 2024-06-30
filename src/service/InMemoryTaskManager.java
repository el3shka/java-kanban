package service;

import exceptions.*;
import model.*;

import java.util.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int taskCounts = 1;
    TreeMap<LocalDateTime, Task> priorityTasks = new TreeMap<>(Comparator.comparing(LocalDateTime::getNano));
    Map<Integer, Task> allTask = new HashMap<>();
    Map<Integer, Subtask> allSubtask = new HashMap<>();
    Map<Integer, Epic> allEpics = new HashMap<>();
    public HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public TreeMap<LocalDateTime, Task> getPriorityTask() {
        return new TreeMap<>(priorityTasks);
    }

    private boolean isCrossing(Task task) {
        if (task == null) throw new NullTaskException("Задача null при чек кроссинге");
        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime taskEndTime = task.getStartTime().plus(task.getDuration());

        TreeMap<LocalDateTime, Task> priorityTasks = getPriorityTask();
        if (priorityTasks.isEmpty()) return true;

        for (Task priorityTask : priorityTasks.values()) {
            if (task.equals(priorityTask)) return true;
            if (task.getStartTime().equals(priorityTask.getStartTime())) return false;
            LocalDateTime tempStartTime = priorityTask.getStartTime();
            LocalDateTime tempEndTime = priorityTask.getStartTime().plus(priorityTask.getDuration());
            if (taskStartTime.isAfter(tempStartTime) && taskStartTime.isBefore(tempEndTime)) return false;
            if (taskStartTime.isBefore(tempStartTime) && taskEndTime.isAfter(tempStartTime)) return false;
        }
        return true;
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
        List<Subtask> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> entry : allSubtask.entrySet()) {
            historyManager.remove(entry.getKey());
            toRemove.add(entry.getValue());
        }

        for (Subtask subtask : toRemove) {
            removeSubtask(subtask.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        List<Epic> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Epic> entry : allEpics.entrySet()) {
            historyManager.remove(entry.getKey());
            toRemove.add(entry.getValue());
        }
        removeAllSubtask();

        for (Epic epic : toRemove) {
            removeEpic(epic.getId());
        }
    }

    @Override
    public Task getTask(int id) {
        if (allTask.get(id) == null) throw new NotFoundException("Задача не найдена");

        Task selectTask = allTask.get(id);
        Task returnTask = new Task(selectTask.getName(), selectTask.getDescription(),
                selectTask.getStartTime(), selectTask.getDuration());
        returnTask.setId(selectTask.getId());
        returnTask.setStatus(selectTask.getStatus());
        historyManager.add(returnTask);
        return returnTask;
    }

    @Override
    public Subtask getSubtask(int id) {
        if (allSubtask.get(id) == null) throw new NotFoundException("Подзадача не найдена");

        Subtask selectSubtask = allSubtask.get(id);
        Subtask returnSubtask = new Subtask(selectSubtask.getName(), selectSubtask.getDescription(),
                selectSubtask.getEpicId(), selectSubtask.getStartTime(), selectSubtask.getDuration());
        returnSubtask.setId(selectSubtask.getId());
        returnSubtask.setStatus(selectSubtask.getStatus());
        historyManager.add(returnSubtask);
        return returnSubtask;
    }

    @Override
    public Epic getEpic(int id) {
        if (allEpics.get(id) == null) throw new NotFoundException("Эпик не найден");

        Epic selectEpic = allEpics.get(id);
        Epic returnEpic = new Epic(selectEpic.getName(), selectEpic.getDescription(),
                selectEpic.getStartTime(), selectEpic.getDuration());
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
        if (task == null) throw new NullTaskException("Null задача при ее создании");

        int id = generateId();
        task.setId(id);
        allTask.put(id, task);

        if (task.getStartTime() != null) {
            if (isCrossing(task)) {
                priorityTasks.put(task.getStartTime(), task);
            } else throw new CrossingTaskException(String.format("Task %s is crossing", task.getId()));

        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic tempEpic = allEpics.get(subtask.getEpicId());

        if (tempEpic == null) throw new NullTaskException("Null temp Эпик при создании подзадачи"); //RU lang problem...

        int id = generateId();
        subtask.setId(id);
        allSubtask.put(id, subtask);
        tempEpic.addSubtaskId(id);
        updateEpicStatus(tempEpic);

        if (subtask.getStartTime() != null) {
            if (isCrossing(subtask)) {
                priorityTasks.put(subtask.getStartTime(), subtask);
                updateTimeAndDurationEpic(tempEpic);
            } else throw new CrossingTaskException(String.format("Subtask %s is crossing", subtask.getId()));

        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic == null) throw new NullTaskException("Null Эпик при его создании");
        int id = generateId();
        epic.setId(id);
        allEpics.put(id, epic);
    }

    @Override
    public void updateTask(Task task) {
        if (allTask.get(task.getId()) == null) throw new NullTaskException("Апдейт, Задача null");
        allTask.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (allSubtask.get(subtask.getId()) == null) throw new NullTaskException("Апдейт, Подзадач null");
        allSubtask.put(subtask.getId(), subtask);
        Epic tempEpic = getEpic(subtask.getEpicId());
        updateEpicStatus(tempEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (allEpics.get(epic.getId()) == null) throw new NullTaskException("Апдейт, Эпик null");
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

        return epic.getSubtaskIds().stream()
                .map(subtaskId -> allSubtask.get(subtaskId))
                .collect(Collectors.toList());
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

    public void updateTimeAndDurationEpic(Epic epic) {
        Optional<Epic> selectEpicOptional = Optional.ofNullable(allEpics.get(epic.getId()));
        if (!selectEpicOptional.isPresent()) throw new NotFoundException("Не найден Эпик по ID");

        Epic selectEpic = selectEpicOptional.get();
        LocalDateTime startTimeEpic;
        LocalDateTime endTimeEpic;
        Duration duration = Duration.ZERO;

        List<Subtask> subtasks = selectEpic.getSubtaskIds().stream()
                .map(subtaskId -> allSubtask.get(subtaskId))
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .sorted(Comparator.comparing(subtask -> subtask.getStartTime().getNano()))
                .toList();

        if (subtasks.isEmpty()) {
            selectEpic.setStartTime(null);
            selectEpic.setEndTime(null);
            selectEpic.setDuration(null);
            return;
        }
        startTimeEpic = subtasks.getLast().getStartTime();
        endTimeEpic = subtasks.getFirst().getStartTime().plus(subtasks.getLast().getDuration());
        selectEpic.setStartTime(startTimeEpic);
        for (Subtask subtask : subtasks) duration = duration.plus(subtask.getDuration());
        selectEpic.setEndTime(endTimeEpic);
        selectEpic.setDuration(duration);
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

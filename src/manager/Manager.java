package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class Manager {
    private static int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    //Замечение 1 - на проверку
    private int generateId() {
        return ++id;
    }

    public int createTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        tasks.put(newTaskId, task);
        return newTaskId;
    }

    public int createEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
        return newEpicId;
    }



    public int createSubtask(Subtask subtask) {
        // как я понял, получение эпика надо перенести сюда?
        // проверку на boolean - убрал
        Epic epic = epics.get(subtask.getEpicId());
        if (epics != null) {
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
            subtasks.put(newSubtaskId, subtask);
            epic.setSubtaskIds(newSubtaskId);
            updateStatusEpic(epic);
            return newSubtaskId;
        } else {
            System.out.println("EPIC NOT FOUND");
            return -1;
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("TASK NOT FOUND");
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("EPIC NOT FOUND");
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtaskId(subtask.getId());      //FIX
            updateStatusEpic(epic);
            subtasks.remove(id);
        } else {
            System.out.println("SUBTASK NOT FOUND");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("TASK LIST IS EMPTY");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("EPIC LIST IS EMPTY");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("SUBTASKS LIST IS EMPTY");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (Integer subId : epic.getSubtaskIds()) {    //Переделал перебор пот (Integer subid ...)
                subtasksNew.add(subtasks.get(subId));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("TASK NOT FOUND");
        }
    }


    //еще замечание на проверку.
    public void updateEpic(Epic epic) {
        // Проверяем, содержится ли эпик с указанным id в хранилище
        if (epics.containsKey(epic.getId())) {
            // Получаем ссылку на существующий эпик в хранилище
            Epic existingEpic = epics.get(epic.getId());
            // Обновляем только поля name и description
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            // Вызываем метод для обновления статуса эпика
            updateStatusEpic(existingEpic);
        } else {
            // Выводим сообщение об ошибке, если эпик не найден
            System.out.println("EPIC NOT FOUND");
        }
    }

    //Заприватил метод
    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                List<Subtask> subtasksNew = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (Integer subId : epic.getSubtaskIds())
                {
                    subtasksNew.add(subtasks.get(subId));
                }

                for (Subtask subtask : subtasksNew) {
                    if (subtask.getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        countNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("EPIC NOT FOUND");
        }
    }

    //пока оставил старый код...
    /*
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Subtask not found");
        }
    }
*/
    // вроде получилось.....
    // тут есть сомнения конечно на счет корректности, т.к. глаз уже замылился
    public void updateSubtask(Subtask newSubtask) {
        if (subtasks.containsKey(newSubtask.getId())) {
            Subtask existingSubtask = subtasks.get(newSubtask.getId());
            if (existingSubtask.getEpicId() != newSubtask.getEpicId()) {
                System.out.println("Epic id already exists in another subtask");
            } else {
                existingSubtask.setStatus(Status.IN_PROGRESS);
                System.out.println("Subtask updated successfully");
            }
        } else {
            System.out.println("Subtask not found");
        }
    }

    public void printTasks() {
        if (tasks.size() == 0) {
            System.out.println("TASK LIST IS EMPTY");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println("Task{" +
                    "description='" + task.getDescription() + '\'' +
                    ", id=" + task.getId() +
                    ", name='" + task.getName() + '\'' +
                    ", status=" + task.getStatus() +
                    '}');
        }
    }

    public void printEpics() {
        if (epics.size() == 0) {
            System.out.println("EPIC LIST IS EMPTY");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println("Epic{" +
                    "subtasksIds=" + epic.getSubtaskIds() +
                    ", description='" + epic.getDescription() + '\'' +
                    ", id=" + epic.getId() +
                    ", name='" + epic.getName() + '\'' +
                    ", status=" + epic.getStatus() +
                    '}');
        }
    }

    public void printSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("SUBTASKS LIST IS EMPTY");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask{" +
                    "epicId=" + subtask.getEpicId() +
                    ", description='" + subtask.getDescription() + '\'' +
                    ", id=" + subtask.getId() +
                    ", name='" + subtask.getName() + '\'' +
                    ", status=" + subtask.getStatus() +
                    '}');
        }
    }
}
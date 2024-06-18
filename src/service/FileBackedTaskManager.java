package service;

import exceptions.*;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File fileInfoInManager;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        fileInfoInManager = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(file);
        int maxCount = 0;
        try (
                BufferedReader readerTask = Files.newBufferedReader(Paths.get(file.toURI()), StandardCharsets.UTF_8)

        ) {
            while (readerTask.ready()) {
                String line = readerTask.readLine();
                if (line.equals("История:")) break;
                if (line.equals("id,type,name,status,description,start time,duration,epic")) continue;

                Task tempTaskFromFile = fromString(line);

                switch (tempTaskFromFile.getType()) {
                    case TaskType.TASK:
                        fileBackedTaskManager.allTask.put(tempTaskFromFile.getId(), tempTaskFromFile);
                        if (maxCount <= tempTaskFromFile.getId()) maxCount = tempTaskFromFile.getId();
                        break;
                    case TaskType.SUBTASK:
                        fileBackedTaskManager.allSubtask.put(tempTaskFromFile.getId(), (Subtask) tempTaskFromFile);
                        if (maxCount <= tempTaskFromFile.getId()) maxCount = tempTaskFromFile.getId();
                        break;
                    case TaskType.EPIC:
                        fileBackedTaskManager.allEpics.put(tempTaskFromFile.getId(), (Epic) tempTaskFromFile);
                        if (maxCount <= tempTaskFromFile.getId()) maxCount = tempTaskFromFile.getId();
                }
            }
            fileBackedTaskManager.taskCounts = ++maxCount;

            StringBuilder historyString = new StringBuilder();
            //history
            while (readerTask.ready()) {
                historyString.append(readerTask.readLine());
            }
            List<Task> historyTasks = historyFromString(historyString.toString());
            for (Task historyTask : historyTasks) {
                fileBackedTaskManager.historyManager.add(historyTask);
            }

        } catch (FileNotFoundException e) {
            throw new ManagerNotFileFound("Файл не найден в FileBackedTaskManager");
        } catch (IOException e) {
            throw new ManagerSaveException("IO problem");
        }

        // fill epic subtasks
        if (!fileBackedTaskManager.allSubtask.isEmpty()) {
            for (Subtask subtask : fileBackedTaskManager.allSubtask.values()) {
                Epic tempEpic = fileBackedTaskManager.allEpics.get(subtask.getEpicId());
                tempEpic.addSubtaskId(subtask.getId());
            }
        }
        if (!fileBackedTaskManager.allEpics.isEmpty()) {
            for (Epic epic : fileBackedTaskManager.allEpics.values()) {
                fileBackedTaskManager.updateTimeAndDurationEpic(epic);
            }
        }

        return fileBackedTaskManager;
    }

    private void save() {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileInfoInManager.toURI()), StandardCharsets.UTF_8)
        ) {
            writer.write("id,type,name,status,description,start time,duration,epic\n");
            for (Epic epicToSave : allEpics.values()) {
                writer.write(epicToSave.toString() + "\n");
            }
            for (Subtask subtaskToSave : allSubtask.values()) {
                writer.write(subtaskToSave.toString() + "\n");
            }
            for (Task taskToSave : allTask.values()) {
                writer.write(taskToSave.toString() + "\n");
            }
            writer.write("История:\n" + historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private static Task fromString(String value) {
        LocalDateTime startTimeFromString = null;
        Duration duration = null;

        String[] infoForTask = value.split(",");
        String type = infoForTask[1];
        if (!infoForTask[5].equals("null") && !infoForTask[6].equals("null")) {
            startTimeFromString = LocalDateTime.parse(infoForTask[5], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            duration = Duration.of(Long.parseLong(infoForTask[6]), ChronoUnit.MINUTES);
        }

        switch (TaskType.valueOf(type)) {
            case TaskType.TASK:
                Task taskFromString = new Task(infoForTask[2], infoForTask[4], startTimeFromString, duration);
                taskFromString.setId(Integer.parseInt(infoForTask[0]));
                taskFromString.setStatus(Status.valueOf(infoForTask[3]));
                return taskFromString;
            case TaskType.EPIC:
                Epic epicFromString = new Epic(infoForTask[2], infoForTask[4], startTimeFromString, duration);
                epicFromString.setId(Integer.parseInt(infoForTask[0]));
                epicFromString.setStatus(Status.valueOf(infoForTask[3]));
                return epicFromString;
            case TaskType.SUBTASK:
                Subtask subtaskFromString = new Subtask(infoForTask[2], infoForTask[4],
                        Integer.parseInt(infoForTask[7]), startTimeFromString, duration);
                subtaskFromString.setId(Integer.parseInt(infoForTask[0]));
                subtaskFromString.setStatus(Status.valueOf(infoForTask[3]));
                return subtaskFromString;
        }
        throw new ManagerTypeTaskException("Ошибка типа задачи");
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder stringHistoryBuilder = new StringBuilder();
        for (Task taskToString : manager.getHistory()) {
            stringHistoryBuilder.append(taskToString.toString()).append("\n");
        }
        return stringHistoryBuilder.toString();
    }

    private static List<Task> historyFromString(String value) {
        String[] lines = value.split("\n");
        List<Task> taskFromString = new LinkedList<>();
        for (String line : lines) {
            taskFromString.add(fromString(line));
        }
        return taskFromString;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tempTask = super.getAllTasks();
        save();
        return tempTask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> tempSubtask = super.getAllSubtasks();
        save();
        return tempSubtask;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> tempEpic = super.getAllEpics();
        save();
        return tempEpic;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpics() {
        save();
        super.removeAllEpics();
    }

    @Override
    public Task getTask(int id) {
        Task tempTask = super.getTask(id);
        save();
        return tempTask;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask tempSubtask = super.getSubtask(id);
        save();
        return tempSubtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic tempEpic = super.getEpic(id);
        save();
        return tempEpic;
    }

    @Override
    public Integer generateId() {
        Integer id = super.generateId();
        save();
        return id;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> tempSubtasks = super.getSubtasksByEpic(epic);
        save();
        return tempSubtasks;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void inProgress(Task task) {
        super.inProgress(task);
        save();
    }

    @Override
    public void inDone(Task task) {
        super.inDone(task);
        save();
    }
}

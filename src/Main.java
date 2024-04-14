import manager.Manager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.lang.*;

// цитата из мема - Миша все фигня, давай по-новой
// надеюсь все получилось :)

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("!!! TASK !!!");
        System.out.println(">>> CREATING TASK <<<");
        manager.createTask(new Task("Description-1", "Task-1", Status.NEW));
        manager.createTask(new Task("Description-2", "Task-2", Status.NEW));
        manager.printTasks();
        System.out.println(">>> GETTING ALL TASK <<<");
        List<Task> taskList = manager.getAllTasks();
        System.out.println(taskList);
        System.out.println(">>> GETTiNG TASK BY ID <<<");
        Task task = manager.getTaskById(1);
        System.out.println(task);
        System.out.println(">>> UPDATING TASK <<<");
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        System.out.println(task);
        System.out.println();

        System.out.println("!!! EPIC !!!");
        System.out.println(">>> CREATING EPIC <<<");
        manager.createEpic(new Epic("Description-1", "Epic-1", Status.NEW));
        manager.createEpic(new Epic("Description-2", "Epic-2", Status.NEW));
        manager.createEpic(new Epic("Description-2", "Epic-3", Status.NEW));
        manager.printEpics();
        System.out.println(">>> GETTING ALL EPICS <<<");
        List<Epic> epics = manager.getAllEpics();
        System.out.println(epics);
        System.out.println(">>> GETTING EPIC BY ID <<<");
        Epic epic = manager.getEpicById(3);
        System.out.println(epic);
        System.out.println(">>> UPDATING EPIC <<<");
        epic.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epic);
        Epic epic3 = manager.getEpicById(3);
        System.out.println(epic3);
        System.out.println();

        System.out.println("!!! SUBTASK !!!");
        System.out.println(">>> CREATING SUBTASK <<<");
        manager.createSubtask(new Subtask("Description-1", "Subtask-1", Status.NEW, 3));
        manager.createSubtask(new Subtask("Description-2", "Subtask-2", Status.NEW, 3));
        manager.createSubtask(new Subtask("Description-3", "Subtask-3", Status.NEW, 4));
        manager.createSubtask(new Subtask("Description-4", "Subtask-4", Status.NEW, 4));
        manager.printSubtasks();
        System.out.println(">>> GETTING ALL SUBTASKS BY EPIC ID <<<");
        List<Subtask> subtasksByEpicId = manager.getAllSubtasksByEpicId(3);
        System.out.println(subtasksByEpicId);
        System.out.println(">>> GETTING ALL SUBTASKS <<<");
        List<Subtask> subtasks = manager.getAllSubtasks();
        System.out.println(subtasks);
        System.out.println(">>> GETTING SUBTASK BY ID <<<");
        Subtask subtask = manager.getSubtaskById(7);
        System.out.println(subtask);
        System.out.println(">>> UPDATING SUBTASK <<<");
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        System.out.println(subtask);
        System.out.println();
        System.out.println("!!! DELETING !!!");
        System.out.println(">>> DELETING TASK BY ID <<<");
        manager.deleteTaskById(1);
        System.out.println(taskList);
        System.out.println(">>> DELETING ALL TASKS <<<");
        manager.deleteAllTasks();
        manager.printTasks();
        System.out.println(">>> DELETING SUBTASK BY ID <<<");
        manager.deleteSubtaskById(5);
        manager.printSubtasks();
        System.out.println(">>> DELETING ALL SUBTASKS <<<");
        epic.deleteAllSubtasks(epics);
        manager.printSubtasks();
        System.out.println(">>> DELETING EPIC BY ID <<<");
        manager.deleteEpicById(4);
        manager.printEpics();
        System.out.println(">>> DELETING ALL EPICS <<<");
        manager.deleteAllEpics();
        manager.printEpics();

        /*
        Subtask subtaskToUpdate = manager.getSubtaskById(subtask.getId());
        if (subtaskToUpdate != null) {
            subtaskToUpdate.setStatus(Status.IN_PROGRESS);
            manager.updateSubtask(subtaskToUpdate);
        } else {
            System.out.println("Subtask not found");
        }
         */

    }
}
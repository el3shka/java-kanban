import model.*;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    private static FileBackedTaskManager manager;

    public static void main(String[] args) {
        System.out.println(">>> !!!!! СТАРТУЕМ !!!!! <<<");

        manager = Managers.getFileBackedTaskManager();
        addAllTask();
        printAllTasks();
        File fileBackup = new File("resources/backup_output.csv");

        TaskManager loadFromFileManager = FileBackedTaskManager.loadFromFile(fileBackup);
        System.out.println(manager.getPriorityTask());
        System.out.println(">>> !!!!! КОНЕЦ !!!!! <<<");
    }

    private static void addAllTask() {
        Task buySock = new Task("Купить носки в подарок", "Носков не нашлось :(");
        manager.createTask(buySock);

        Task makeDinner = new Task("Приготовить ужин", "Время ужина",
                LocalDateTime.now().plus(Duration.ofDays(1)), Duration.ofHours(1));
        manager.createTask(makeDinner);

        Epic goToShop = new Epic("Сходить в магазин", "Купить продукты");
        manager.createEpic(goToShop);

        Subtask buyMilk = new Subtask("Купить молоко", "Молока нет", 3,
                LocalDateTime.now().plus(Duration.ofHours(5)), Duration.ofHours(5));
        Subtask buyMeat = new Subtask("Купить мясо", "Кончилось мясо", 3,
                LocalDateTime.now(), Duration.ofHours(3));
        manager.createSubtask(buyMilk);
        manager.createSubtask(buyMeat);
    }

    private static void printAllTasks() {
        System.out.println("Список Задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.toString());
        }
        System.out.println("Список Эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.toString());

            for (Task task : manager.getSubtasksByEpic(epic)) {
                System.out.println("=======> " + task.toString());
            }
        }
        System.out.println("Список Подзадач:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask.toString());
        }

        System.out.println("Получаем историю:");
        for (Task task : manager.historyManager.getHistory()) {
            System.out.println(task.toString());
        }
    }
}

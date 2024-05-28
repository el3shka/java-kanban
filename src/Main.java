//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.File;
import java.util.Iterator;
import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {
    private static FileBackedTaskManager manager;

    public Main() {
    }

    public static void main(String[] args) {
        System.out.println(">>> !!!!! СТАРТУЕМ !!!!! <<<");
        manager = Managers.getFileBackedTaskManager();
        addAllTask();
        printAllTasks();
        System.setProperty("file.encoding", "UTF-8");
        File fileBackup = new File("resources/backup_output.csv");
        TaskManager loadFromFileManager = FileBackedTaskManager.loadFromFile(fileBackup);
        System.out.println(">>> !!!!! КОНЕЦ !!!!! <<<");
    }

    private static void addAllTask() {
        Task buySock = new Task("Купить носки в подарок", "Носков не нашлось :(");
        manager.createTask(buySock);
        Task makeDinner = new Task("Приготовить ужин", "Время ужина");
        manager.createTask(makeDinner);
        Epic goToShop = new Epic("Сходить в магазин", "Купить продукты");
        manager.createEpic(goToShop);
        Subtask buyMilk = new Subtask("Купить молоко", "Молока нет", 3);
        Subtask buyMeat = new Subtask("Купить мясо", "Кончилось мясо", 3);
        manager.createSubtask(buyMilk);
        manager.createSubtask(buyMeat);
    }

    private static void printAllTasks() {
        System.out.println("Список задач:");
        Iterator var0 = manager.getAllTasks().iterator();

        Task task;
        while(var0.hasNext()) {
            task = (Task)var0.next();
            System.out.println(task);
        }

        System.out.println("Список эпиков:");
        var0 = manager.getAllEpics().iterator();

        while(var0.hasNext()) {
            Epic epic = (Epic)var0.next();
            System.out.println(epic);
            Iterator var2 = manager.getSubtasksByEpic(epic).iterator();

            while(var2.hasNext()) {
                Task task = (Task)var2.next();
                System.out.println("=====> " + String.valueOf(task));
            }
        }

        System.out.println("Список подзадач:");
        var0 = manager.getAllSubtasks().iterator();

        while(var0.hasNext()) {
            task = (Task)var0.next();
            System.out.println(task);
        }

        System.out.println("Получаем историю:");
        var0 = manager.historyManager.getHistory().iterator();

        while(var0.hasNext()) {
            task = (Task)var0.next();
            System.out.println(task);
        }

    }
}

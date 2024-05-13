import model.*;
import service.InMemoryTaskManager;
import service.Managers;

//new branch version
public class Main {
    private static InMemoryTaskManager manager;

    public static void main(String[] args) {
        System.out.println(">>> !!!!! СТАРТУЕМ !!!!! <<<");

        manager = Managers.getDefault();
        addAllTask();
        printAllTasks();
    }

    private static void addAllTask() {
        Task buySock = new Task("Купить носки в подарок", "Носков не нашлось :(");
        manager.createTask(buySock);

        Task makeDinner = new Task("Приготовить ужин", "Время ужина");
        manager.createTask(makeDinner);

        Epic goToShop = new Epic("Сходить в магазин", "Купить продукты");
        manager.createEpic(goToShop);

        Subtask buyMilk = new Subtask("Купить молоко", "Молока нет", goToShop);
        Subtask buyMeat = new Subtask("Купить мясо", "Кончилось мясо", goToShop);
        manager.createSubtask(buyMilk);
        manager.createSubtask(buyMeat);
    }

    private static void printAllTasks() {
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpic(epic)) {
                System.out.println("=====> " + task);
            }
        }
        System.out.println("Список подзадач:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("Получаем историю:");
        for (Task task : manager.historyManager.getHistory()) {
            System.out.println(task);
        }
    }
}


/*
import managers.InMemoryTaskManager;
import managers.TaskManager;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;
public class Main {

    public static void main(String[] args) {
        System.out.println(">>> !!!!! СТАРТУЕМ !!!!! <<<");
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 =new Task("Первая", "Описание первой задачи");
        taskManager.createTask(task1);
        Task task2 =new Task("Вторая", "Описание второй задачи");
        taskManager.createTask(task2);
        Epic epic1 =new Epic("Первый Эпик", "Описание первого эпика");
        Epic epic2 =new Epic("Второй Эпик", "Описание второго эпика");
        Integer id1 = taskManager.createEpic(epic1);
        Integer id2 = taskManager.createEpic(epic2);
        printAllTasks(taskManager);
        Subtask sub1 = new Subtask("Подзадача_1", "Описание Подзадача_1", id1);
        taskManager.createSubtask(sub1);
        Subtask sub2 = new Subtask("Подзадача_2", "Описание Подзадача_2", id1);
        taskManager.createSubtask(sub2);
        Subtask sub3 = new Subtask("Подзадача_3", "Описание Подзадача_3", id2);
        taskManager.createSubtask(sub3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        printAllTasks(taskManager);
        System.out.println(epic2.getTaskStatus());
        sub3.setTaskStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(sub3);
        System.out.println(epic2.getTaskStatus());
        sub3.setTaskStatus(Status.DONE);
        taskManager.updateSubtask(sub3);
        System.out.println(epic2.getTaskStatus());
        taskManager.deleteSubtask(sub3.getId());
        System.out.println(epic2.getTaskStatus());
        printAllTasks(taskManager);
    }
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpic((Epic) epic)) {
                System.out.println("=====>>>>> " + task);
            }
        }
        System.out.println("Список подзадач:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("Получаем историю:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
*/

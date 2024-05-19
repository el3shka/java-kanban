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

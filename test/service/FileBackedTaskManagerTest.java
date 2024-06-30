package service;

import exceptions.ManagerSaveException;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class FileBackedTaskManagerTest {

    @DisplayName("Сохранение и загрузка в файл")
    @Test
    void loadFromFile() {
        try {
            File fileTest = File.createTempFile("backup_output", "csv");
            FileBackedTaskManager manager = Managers.getFileBackedTaskManager(fileTest);
            addAllTask(manager);
            addHistoryAllTasks(manager);

            FileBackedTaskManager loadFromFileManager = FileBackedTaskManager.loadFromFile(fileTest);

            Assertions.assertEquals(manager.taskCounts, loadFromFileManager.taskCounts,
                    "Ошибка при подсчете задач (генерация id)");
            Assertions.assertEquals(manager.getAllEpics(), loadFromFileManager.getAllEpics(),
                    "Эпики не равны");
            Assertions.assertEquals(manager.getAllTasks(), loadFromFileManager.getAllTasks(),
                    "Таски не равны");
            Assertions.assertEquals(manager.getAllSubtasks(), loadFromFileManager.getAllSubtasks(),
                    "Подзадачи не равны");
            Assertions.assertEquals(manager.historyManager.getHistory(), loadFromFileManager.historyManager.getHistory(),
                    "История не равна");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка с IO");
        }
        System.out.println("!!! >>>>> КОНЕЦ <<<<< !!!");
    }

     private void addAllTask(FileBackedTaskManager manager) {
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

    private void addHistoryAllTasks(FileBackedTaskManager manager) {
        System.out.println("Список задач:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Список эпиков:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpic(epic)) {
                System.out.println("====> " + task);
            }
        }
        System.out.println("Список подзадач:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}
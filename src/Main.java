import model.Epic;
import model.StatusTask;
import model.Subtask;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Epic epic1 = new Epic("Сделать зарядку", "пройтись по списку");
            Subtask subtask1 = new Subtask("5 ажуманий", "без астнаовки", StatusTask.NEW);
            Subtask subtask2 = new Subtask("10 приседаний", "без астнаовки", StatusTask.NEW);
            Subtask subtask3 = new Subtask("15 кувырков", "без астнаовки", StatusTask.NEW);

            taskServer.getManager().createTask(epic1);
            taskServer.getManager().createTask(subtask1);
            taskServer.getManager().createTask(subtask2);
            taskServer.getManager().createTask(subtask3);
            taskServer.getManager().getAllTasks();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
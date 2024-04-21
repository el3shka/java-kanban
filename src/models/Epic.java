package models;

import java.util.ArrayList;
public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }
    public void addSubtaskId(Integer id) {
        subtasksId.add(id);
    }
    public void deleteSubtaskId(Integer id) {
        subtasksId.remove(id);

    }
    public void deleteSubtasksId() {
        subtasksId.clear();
    }
    @Override
    public String toString() {
        return "Epic (" + "id = " + id +
                "; name = " + name +
                "; description = " + description +
                "; status = " + taskStatus +
                "; subtasksId = " + subtasksId.toString() +
                ")\n";
    }
}
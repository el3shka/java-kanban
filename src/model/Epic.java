package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
    }

    public void addSubtaskId(Integer id) {
        if (id.equals(this.getId())) {
            return;
        }
        subtaskIds.add(id);
    }

    public void removeSubtaskId(Integer id) {
        subtaskIds.remove(id);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

}
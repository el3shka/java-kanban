package tasks;

import status.Status;

import java.util.*;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();


    public Epic(String description, String name, Status status) {
        super(description, name, status);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds); //FIX
    }

    //тогда думаю так верно будет очистить все хранилище.
    public void deleteAllSubtasks() {
        subtaskIds.clear();
    }

    public void setSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
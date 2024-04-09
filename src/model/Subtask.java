package model;


import java.time.LocalDateTime;
import java.util.Objects;


public class Subtask extends Task {
    private final int epicId;

    public Subtask(String description, String name, StatusTask status, int epicId) {
        super(description, name, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() { return TaskType.SUBTASK; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        if (getStartTime() != null) {
            return getId() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + "," + getDescription() + "," +
                    getStartTime().format(getFormatter()) + "," + this.getEndTime().format(getFormatter()) + ","
                    + getDuration() + "," + epicId + "\n";
        } else {
            return "startTime = null";
        }
    }
}
package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
        setStartTime(null);
        setDuration(null);
    }

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
        this.setStartTime(startTime);
        this.setDuration(duration);
    }

    @Override
    public void setId(int id) {
        if (id != epicId) {
            super.setId(id);
        }
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() + "," + epicId;
    }
}

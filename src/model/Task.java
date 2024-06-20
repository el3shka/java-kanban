package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private Duration duration;
    private LocalDateTime startTime;
    protected TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
        type = TaskType.TASK;
        duration = null;
        startTime = null;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
        type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;

    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (startTime != null && duration != null) {
            return String.join(",", Integer.toString(id),
                    type.toString(), name, status.toString(), description, startTime.format(dateTimeFormatter),
                    Long.toString(duration.toMinutes()));
        } else {
            return String.join(",", Integer.toString(id),
                    type.toString(), name, status.toString(), description, "null",
                    "null");
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

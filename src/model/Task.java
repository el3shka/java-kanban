package model;

import main.status.Status;
import main.util.TaskType;

import java.time.Duration;
import java.util.Objects;

public class Task {
    private String description;
    private Integer id;
    private String name;
    private Status status;


    public Task(String description, String name, Status status) {
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public Task(String description, String name, StatusTask status) {
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name) &&
                status == task.status && Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }

    @Override
    public String toString() {
        if (startTime != null) {
            return id + "," + TaskType.TASK + "," + name + "," + status + "," + description + "," +
                    startTime.format(FORMATTER) + "," + this.getEndTime().format(FORMATTER) + "," + duration + ",\n";
        } else {
            return "startTime = null";
        }
    }
}

package model;

import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;

    public TaskType getType() {
        return type;
    }

    protected TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
        this.type = TaskType.TASK;
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
        return String.join(",", Integer.toString(id),
                type.toString(), name, status.toString(), description);
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

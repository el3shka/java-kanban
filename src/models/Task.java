package models;

import java.util.Objects;

public  class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status taskStatus;

    public Task (String name, String description){
        this.name = name;
        this.description = description;
        this.taskStatus = Status.NEW;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task (" + "id = " + id +
                "; name = " + name +
                "; description = " + description +
                "; status = " + taskStatus +
                ")\n";
    }

}
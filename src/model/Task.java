package model;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Task {
    private static int sumTasks;
    private String name;
    private Integer id;
    private String description;
    private StatusTask status = StatusTask.NEW;


    Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = sumTasks++;
    }

    public void minusSumTasks() {
        sumTasks--;
    }

    public Integer getSuperId() {
        return id;
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

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && Objects.equals(id, task.id) && description.equals(task.description) && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, description, status);
    }

    public boolean isTaskCopy(Task task) {
        if (this.getName().equals(task.getName()) && this.getDescription().equals(task.getDescription())) {
            return true;
        }
        return false;
    }

    public abstract LocalDateTime getStartTime();

    public int compareTime(Task task) {
        if (task.getStartTime() == null && this.getStartTime() == null) return 0;
        if (task.getStartTime() == null) return -1;
        if (this.getStartTime() == null) return 1;

        return this.getStartTime().compareTo(task.getStartTime());
    }
}
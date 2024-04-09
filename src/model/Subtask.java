package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId = 0;


    public Subtask(String name, String description, int duration) {
        super(name, description, duration);
    }

    public Subtask(String name, String description, int duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

/*
public class Subtask extends Task{
    private int subTaskId;
    private LocalDateTime startTime;
    private Duration duration;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, StatusTask status) {
        super(name, description);
        setStatus(status);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", subTaskId=" + subTaskId +
                ", id=" + getSuperId() +
                ", status='" + this.getStatus() + '\'' +
                ", startTime='" + this.getStartTime() + '\'' +
                ", duration='" + this.getDuration() + '\'' +
                '}';
    }

    public void setSubtaskId(int id) {
        this.subTaskId = id;
    }

    public int getSubtaskId() {
        return subTaskId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }
}
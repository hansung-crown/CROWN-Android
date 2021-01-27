package com.example.crown_doorlock;

public class LogData {
    private String name;
    private String time;
    private String success;
    private String imageName;

    public LogData(String name, String time, String imageName) {
        this.name = name;
        this.time = time;
        this.success = "";
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getSuccess() {
        return success;
    }

    public String getImageName() { return imageName; }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setImageName(String imageName) { this.imageName = imageName; }
}
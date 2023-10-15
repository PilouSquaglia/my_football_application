package com.example.matchservice.model;

public class Team {
    private int id;
    private String name;
    private String division;
    private int points;

    public Team() {
    }

    public Team(int id, String name, String division, int points) {
        this.id = id;
        this.name = name;
        this.division = division;
        this.points = points;
    }

    public int getId() {
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

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}




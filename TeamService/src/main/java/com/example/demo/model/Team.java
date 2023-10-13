package com.example.demo.model;

public class Team {
    private int id;
    private String name;
    private String division;

    public Team() {
    }

    public Team(int id, String name, String division) {
        this.id = id;
        this.name = name;
        this.division = division;
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
}




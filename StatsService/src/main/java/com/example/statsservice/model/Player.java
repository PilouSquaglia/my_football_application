package com.example.statsservice.model;

public class Player {
    private int id;
    private String name;
    private int teamId;
    private String team;

    public Player() {
    }

    public Player(int id, String name, int teamId) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
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

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}

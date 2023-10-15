package com.example.matchservice.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Match {
    private int id;

    private int[] teamArrayId = new int[2];
    private List<Team> teamArray = new ArrayList<>(Arrays.asList(new Team[2]));

    public Match() {
    }

    public Match(int id, int[] teamArrayId) {
        this.id = id;
        this.teamArrayId = teamArrayId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Team> getTeamArray() {
        return teamArray;
    }

    public void setTeamArray(List<Team> teamArray) {
        this.teamArray = teamArray;
    }

    public int[] getTeamArrayId() {
        return teamArrayId;
    }

    public void setTeamArrayId(int[] teamArrayId) {
        this.teamArrayId = teamArrayId;
    }
}

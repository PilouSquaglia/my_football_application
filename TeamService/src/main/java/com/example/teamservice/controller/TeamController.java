package com.example.teamservice.controller;


import com.example.teamservice.exception.TeamNotFoundException;
import com.example.teamservice.model.Team;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/teams")
@Tag(name = "Teams Management System", description = "Operations related to teams management")
public class TeamController {

    private List<Team> teams = new ArrayList<>();
    private int nextTeamId = 1;

    public TeamController() {
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Team(nextTeamId++, "SCB", "Ligue 1"));
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Team(nextTeamId++, "AJB", "U19"));
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Team(nextTeamId++, "Liverpool", "BPL"));
    }

    @Operation(summary = "Get all teams", description = "Get a list of all teams.")
    @GetMapping("/all")
    public List<Team> getAllTeams() {
        return teams;
    }

    @Operation(summary = "Get team by Id", description = "Get a team by its ID.")
    @HystrixCommand(fallbackMethod = "getTeamByIdFallback")
    @GetMapping("/{id}")
    public Team getTeamById(@PathVariable int id) {
        Optional<Team> team = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (team.isPresent()) {
            return team.get();
        } else {
            throw new TeamNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public Team getTeamByIdFallback(int id) {
        return new Team(-1, "Équipe non trouvée", "Inconnu");
    }

    @Operation(summary = "Create team", description = "Create a new team.")
    @HystrixCommand(fallbackMethod = "createTeamFallback")
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        team.setId(nextTeamId++);
        teams.add(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    public ResponseEntity<Team> createTeamFallback(Team team) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Team(-1, "Erreur de création", "Inconnu"));
    }

    @Operation(summary = "Update team", description = "Update an existing team by its ID.")
//    @HystrixCommand(fallbackMethod = "updateTeamFallback")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable int id, @RequestBody Team updatedTeam) {
        Optional<Team> existingTeam = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (existingTeam.isPresent()) {
            Team teamToUpdate = existingTeam.get();
            teamToUpdate.setName(updatedTeam.getName());
            teamToUpdate.setDivision(updatedTeam.getDivision());
            return ResponseEntity.ok(teamToUpdate);
        } else {
            throw new TeamNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> updateTeamFallback(int id, Team updatedTeam) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de mise à jour de l'équipe avec l'ID : " + id);
    }

    @Operation(summary = "Delete team", description = "Delete a team by its ID.")
    @HystrixCommand(fallbackMethod = "deleteTeamFallback")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
        Optional<Team> team = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (team.isPresent()) {
            teams.remove(team.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new TeamNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> deleteTeamFallback(int id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de suppression de l'équipe avec l'ID : " + id);
    }
}


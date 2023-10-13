package com.example.teams_service.controller;

import com.example.teams_service.exception.TeamNotFoundException;
import com.example.teams_service.model.Team;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
@Api(value = "Teams Management System", description = "Operations related to teams management")
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

    @ApiOperation(value = "Get all teams", response = Team.class, tags = "getTeams")
    @GetMapping("/all")
    public List<Team> getAllTeams() {
        return teams;
    }

    @ApiOperation(value = "Get team by Id", response = Team.class, tags = "getTeamById")
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
        return new Team(-1, "Équipe par défaut", "Division par défaut");
    }

    @ApiOperation(value = "Create team", response = Team.class, tags = "createTeam")
    @HystrixCommand(fallbackMethod = "createTeamFallback")
    @PostMapping
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        team.setId(nextTeamId++);
        teams.add(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    public ResponseEntity<String> createTeamFallback(@RequestBody Team team) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'équipe");
    }

    @ApiOperation(value = "Update team", response = Team.class, tags = "updateTeam")
    @HystrixCommand(fallbackMethod = "updateTeamFallback")
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

    public ResponseEntity<String> updateTeamFallback(@PathVariable int id, @RequestBody Team updatedTeam) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour de l'équipe");
    }

    @ApiOperation(value = "Delete team", response = Team.class, tags = "deleteTeam")
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

    public ResponseEntity<String> deleteTeamFallback(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de l'équipe");
    }

}


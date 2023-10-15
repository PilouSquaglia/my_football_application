package com.example.playerservice.controller;


import com.example.playerservice.exception.PlayerNotFoundException;
import com.example.playerservice.model.Player;
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
public class PlayerController {

    private List<Player> teams = new ArrayList<>();
    private int nextTeamId = 1;

    public PlayerController() {
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Player(nextTeamId++, "Pilou", "Ligue 1"));
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Player(nextTeamId++, "Focardi", "U19"));
        System.out.println("nextTeamId: " + nextTeamId);
        teams.add(new Player(nextTeamId++, "Double J", "BPL"));
    }

    @Operation(summary = "Get all teams", description = "Get a list of all teams.")
    @GetMapping("/all")
    public List<Player> getAllTeams() {
        return teams;
    }

    @Operation(summary = "Get team by Id", description = "Get a team by its ID.")
    @HystrixCommand(fallbackMethod = "getTeamByIdFallback")
    @GetMapping("/{id}")
    public Player getTeamById(@PathVariable int id) {
        Optional<Player> team = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (team.isPresent()) {
            return team.get();
        } else {
            throw new PlayerNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public Player getTeamByIdFallback(int id) {
        return new Player(-1, "Équipe non trouvée", "Inconnu");
    }

    @Operation(summary = "Create team", description = "Create a new team.")
    @HystrixCommand(fallbackMethod = "createTeamFallback")
    @PostMapping
    public ResponseEntity<Player> createTeam(@RequestBody Player team) {
        team.setId(nextTeamId++);
        teams.add(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(team);
    }

    public ResponseEntity<Player> createTeamFallback(Player team) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Player(-1, "Erreur de création", "Inconnu"));
    }

    @Operation(summary = "Update team", description = "Update an existing team by its ID.")
//    @HystrixCommand(fallbackMethod = "updateTeamFallback")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable int id, @RequestBody Player updatedTeam) {
        Optional<Player> existingTeam = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (existingTeam.isPresent()) {
            Player teamToUpdate = existingTeam.get();
            teamToUpdate.setName(updatedTeam.getName());
            teamToUpdate.setTeam(updatedTeam.getTeam());
            return ResponseEntity.ok(teamToUpdate);
        } else {
            throw new PlayerNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> updateTeamFallback(int id, Player updatedTeam) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de mise à jour de l'équipe avec l'ID : " + id);
    }

    @Operation(summary = "Delete team", description = "Delete a team by its ID.")
    @HystrixCommand(fallbackMethod = "deleteTeamFallback")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable int id) {
        Optional<Player> team = teams.stream().filter(t -> t.getId() == id).findFirst();
        if (team.isPresent()) {
            teams.remove(team.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new PlayerNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> deleteTeamFallback(int id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de suppression de l'équipe avec l'ID : " + id);
    }
}


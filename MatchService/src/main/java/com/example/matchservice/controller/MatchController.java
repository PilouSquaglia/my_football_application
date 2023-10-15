package com.example.matchservice.controller;


import com.example.matchservice.exception.MatchNotFoundException;
import com.example.matchservice.model.Match;
import com.example.matchservice.model.Team;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/matchs")
@Tag(name = "Matchs Management System", description = "Operations related to matchs management")
public class MatchController {

    private String teamServiceUrl = "http://localhost:8011";

    @Autowired
    private final RestTemplate restTemplate;
    private List<Match> matchs = new ArrayList<>();
    private int nextMatchId = 1;

    public MatchController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        matchs.add(new Match(nextMatchId++,new int[]{1,  2}));
        matchs.add(new Match(nextMatchId++,new int[]{3, 4}));
        matchs.add(new Match(nextMatchId++,new int[]{5, 6}));
        matchs.add(new Match(nextMatchId++,new int[]{7, 8}));
    }

    @Operation(summary = "Get all matchs", description = "Get a list of all matchs.")
    @GetMapping("/all")
        public List<Match> getAllMatchs() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Match match : matchs) {
            int[] teamIds = match.getTeamArrayId();

            List<Team> teamArray = new ArrayList<>();

            for (int teamId : teamIds) {
                String teamApiUrl = teamServiceUrl + "/teams/" + teamId;

                try {
                    Team team = restTemplate.getForObject(teamApiUrl, Team.class);

                    if (team != null) {
                        teamArray.add(team);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            match.setTeamArray(teamArray);
        }

        return matchs;
    }

    @Operation(summary = "Get match by Id", description = "Get a match by its ID.")
    @HystrixCommand(fallbackMethod = "getMatchByIdFallback")
    @GetMapping("/{id}")
    public Match getMatchById(@PathVariable int id) {
        RestTemplate restTemplate = new RestTemplate();

        Optional<Match> match = matchs.stream().filter(t -> t.getId() == id).findFirst();

        if (match.isPresent()) {
            int[] teamIds = match.get().getTeamArrayId();

            List<Team> teams = new ArrayList<>();

            for (int teamId : teamIds) {
                String teamApiUrl = teamServiceUrl + "/teams/" + teamId;

                try {
                    Team team = restTemplate.getForObject(teamApiUrl, Team.class);
                    teams.add(team);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            match.get().setTeamArray(teams);
            return match.get();
        } else {
            throw new MatchNotFoundException("Match non trouvé avec l'ID : " + id);
        }
    }

    public Match getMatchByIdFallback(int id) {
        return new Match(-1, new int[]{-1,  -1});
    }

    @Operation(summary = "Create match", description = "Create a new match.")
    @HystrixCommand(fallbackMethod = "createMatchFallback")
    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        match.setId(nextMatchId++);
        matchs.add(match);
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    public ResponseEntity<Match> createMatchFallback(Match match) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Match(-1, new int[]{-1,  -1}));
    }

    @Operation(summary = "Update match", description = "Update an existing match by its ID.")
//    @HystrixCommand(fallbackMethod = "updateMatchFallback")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatch(@PathVariable int id, @RequestBody Match updatedMatch) {
        Optional<Match> existingMatch = matchs.stream().filter(t -> t.getId() == id).findFirst();
        if (existingMatch.isPresent()) {
            Match matchToUpdate = existingMatch.get();
            matchToUpdate.setTeamArray(updatedMatch.getTeamArray());
            return ResponseEntity.ok(matchToUpdate);
        } else {
            throw new MatchNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> updateMatchFallback(int id, Match updatedMatch) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de mise à jour de l'équipe avec l'ID : " + id);
    }

    @Operation(summary = "Delete match", description = "Delete a match by its ID.")
    @HystrixCommand(fallbackMethod = "deleteMatchFallback")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable int id) {
        Optional<Match> match = matchs.stream().filter(t -> t.getId() == id).findFirst();
        if (match.isPresent()) {
            matchs.remove(match.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new MatchNotFoundException("Équipe non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> deleteMatchFallback(int id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de suppression de l'équipe avec l'ID : " + id);
    }
}


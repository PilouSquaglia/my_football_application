package com.example.playerservice.controller;


import com.example.playerservice.exception.PlayerNotFoundException;
import com.example.playerservice.model.Player;
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
@RequestMapping(value = "/players")
@Tag(name = "Players Management System", description = "Operations related to players management")
public class PlayerController {

    private String teamServiceUrl = "http://localhost:8011";

    @Autowired
    private final RestTemplate restTemplate;
    private List<Player> players = new ArrayList<>();
    private int nextPlayerId = 1;

    public PlayerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        players.add(new Player(nextPlayerId++, "Pilou", 2));
        players.add(new Player(nextPlayerId++, "Focardi", 1));
        players.add(new Player(nextPlayerId++, "Double J", 3));
    }

    @Operation(summary = "Get all players", description = "Get a list of all players.")
    @GetMapping("/all")
        public List<Player> getAllPlayers() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Player player : players) {
            int teamId = player.getTeamId();
            String teamApiUrl = teamServiceUrl + "/teams/" + teamId;

            try {
                String teamJson = restTemplate.getForObject(teamApiUrl, String.class);
                Map<String, Object> teamData = objectMapper.readValue(teamJson, new TypeReference<Map<String, Object>>() {});
                String teamName = (String) teamData.get("name");
                player.setTeam(teamName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return players;
    }

    @Operation(summary = "Get player by Id", description = "Get a player by its ID.")
    @HystrixCommand(fallbackMethod = "getPlayerByIdFallback")
    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable int id) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        Optional<Player> player = players.stream().filter(t -> t.getId() == id).findFirst();
        int teamId = player.get().getTeamId();
        String teamApiUrl = teamServiceUrl + "/teams/" + teamId;

        try {
            String teamJson = restTemplate.getForObject(teamApiUrl, String.class);
            Map<String, Object> teamData = objectMapper.readValue(teamJson, new TypeReference<Map<String, Object>>() {});
            String teamName = (String) teamData.get("name");
            player.get().setTeam(teamName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException("Joueur non trouvée avec l'ID : " + id);
        }
    }

    public Player getPlayerByIdFallback(int id) {
        return new Player(-1, "Joueur non trouvée", -1);
    }

    @Operation(summary = "Create player", description = "Create a new player.")
    @HystrixCommand(fallbackMethod = "createPlayerFallback")
    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        player.setId(nextPlayerId++);
        players.add(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    public ResponseEntity<Player> createPlayerFallback(Player player) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Player(-1, "Erreur de création", -1));
    }

    @Operation(summary = "Update player", description = "Update an existing player by its ID.")
//    @HystrixCommand(fallbackMethod = "updatePlayerFallback")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable int id, @RequestBody Player updatedPlayer) {
        Optional<Player> existingPlayer = players.stream().filter(t -> t.getId() == id).findFirst();
        if (existingPlayer.isPresent()) {
            Player playerToUpdate = existingPlayer.get();
            playerToUpdate.setName(updatedPlayer.getName());
            playerToUpdate.setTeamId(updatedPlayer.getTeamId());
            return ResponseEntity.ok(playerToUpdate);
        } else {
            throw new PlayerNotFoundException("Joueur non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> updatePlayerFallback(int id, Player updatedPlayer) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de mise à jour du Joueur avec l'ID : " + id);
    }

    @Operation(summary = "Delete player", description = "Delete a player by its ID.")
    @HystrixCommand(fallbackMethod = "deletePlayerFallback")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable int id) {
        Optional<Player> player = players.stream().filter(t -> t.getId() == id).findFirst();
        if (player.isPresent()) {
            players.remove(player.get());
            return ResponseEntity.noContent().build();
        } else {
            throw new PlayerNotFoundException("Joueur non trouvée avec l'ID : " + id);
        }
    }

    public ResponseEntity<?> deletePlayerFallback(int id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de suppression du Joueur avec l'ID : " + id);
    }
}


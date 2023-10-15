package com.example.statsservice.controller;


import com.example.statsservice.model.Player;
import com.example.statsservice.model.Team;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@Tag(name = "Stats Management System", description = "Operations related to stats management")
public class StatController {

    private String teamServiceUrl = "http://localhost:8011";
    private String teamServiceUrl2 = "http://localhost:8012";

    @Autowired
    private final RestTemplate restTemplate;
    public StatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Operation(summary = "Get team stat by Id", description = "Get a team stat by its ID.")
    @HystrixCommand(fallbackMethod = "getPlayerStatByIdFallback")
    @GetMapping("player-stats/{id}")
    public Player getPlayerStatById(@PathVariable int id) {
        String teamApiUrl = teamServiceUrl2 + "/players/" + id;

        Player player = null;
        try {
            player = restTemplate.getForObject(teamApiUrl, Player.class);
            return player;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public Player getPlayerStatByIdFallback(int id) {
        return new Player(-1, "Joueur non trouvée", -1);
    }

    @Operation(summary = "Get team stat by Id", description = "Get a team stat by its ID.")
    @HystrixCommand(fallbackMethod = "getTeamStatByIdFallback")
    @GetMapping("team-stats/{id}")
    public Team getTeamStatById(@PathVariable int id) {
        String teamApiUrl = teamServiceUrl + "/teams/" + id;

        Team team = null;
        try {
            team = restTemplate.getForObject(teamApiUrl, Team.class);
            return team;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return team;
    }
    
    public Team getTeamStatByIdFallback(int id) {
        return new Team(-1, "Équipe non trouvée", "Inconnu", -1);
    }


}


package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.Player1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
@Profile(Roles.PLAYER_1)
public class Player1Controller {
    @Autowired
    private Player1Service player1Service;
    @Autowired
    private ApplicationContext ctx;
    @Value("${opponent.host}")
    private String opponentHost;

    @PostConstruct
    private void setDevHosts() {
        if (ctx.getEnvironment().getActiveProfiles().length > 1)
            opponentHost = "http://localhost:" + Integer.valueOf(Objects.requireNonNull(ctx.getEnvironment().getProperty("server.port")));
    }


    @GetMapping(Roles.PLAYER_1 + "/{value}")
    public List<GameEvent> startGame(@PathVariable Integer value) {
        LinkedList<GameEvent> result = new LinkedList<>();
        GameEvent gameEvent = value == 0 ? player1Service.startGame() : player1Service.startGame(value);
        result.add(gameEvent);
        while (Objects.requireNonNull(gameEvent).getType() != GameEvent.TYPE.END_GAME) {
            if (gameEvent.getPlayer() == GameEvent.PLAYER.PLAYER_1)
                gameEvent = new RestTemplate().postForEntity(opponentHost + "/" + Roles.PLAYER_2, gameEvent, GameEvent.class).getBody();
            else
                gameEvent = player1Service.nextMove(gameEvent);
            result.add(gameEvent);
        }
        return result;
    }

}

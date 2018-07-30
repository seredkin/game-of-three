package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.Player1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(ServiceRoles.PLAYER_1)
@Profile(ServiceRoles.PLAYER_1)
public class Player1Controller implements PlayerController {
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

    @GetMapping("/start")
    public GameEvent startTheGameWithRandomValue() {
        return player1Service.startGame();
    }

    @GetMapping("/start/{value}")
    public GameEvent startTheGame(@PathVariable Integer value) {

        return player1Service.startGame(value);

    }

    @GetMapping("/play")
    public List<GameEvent> playTheGameWithRandomValue() {
        return this.playTheGameWithGivenValue(0);
    }

    @GetMapping("/play/{value}")
    public List<GameEvent> playTheGameWithGivenValue(@PathVariable Integer value) {
        LinkedList<GameEvent> result = new LinkedList<>();
        GameEvent gameEvent = value == 0 ? player1Service.startGame() : player1Service.startGame(value);
        result.add(gameEvent);
        while (Objects.requireNonNull(gameEvent).getType() != GameEvent.TYPE.END_GAME) {
            if (gameEvent.getPlayer() == GameEvent.PLAYER.PLAYER_1)
                gameEvent = callOpponentNext(gameEvent);
            else
                gameEvent = player1Service.nextMove(gameEvent);
            result.add(gameEvent);
        }
        return result;
    }

    private GameEvent callOpponentNext(GameEvent gameEvent) {
        return new RestTemplate().postForEntity(opponentHost + "/" + ServiceRoles.PLAYER_2, gameEvent, GameEvent.class).getBody();
    }

    /* Emulates delays in the communication with the remote party */
    @GetMapping("/async/play/{value}")
    public Flux<GameEvent> playGame(@RequestParam(required = false, defaultValue = "56") Integer value) {
        Flux<Long> delay = Flux.interval(Duration.ofMillis(50));

        Flux<GameEvent> gameEventFlux = Flux.fromStream(playTheGameWithGivenValue(value).stream());

        return gameEventFlux.zipWith(delay, (gameEvent, aLong) -> gameEvent);
    }

    @Override
    @PostMapping(ServiceRoles.PLAYER_1)
    public GameEvent nextMove(@RequestBody GameEvent gameEvent) {
        return player1Service.nextMove(gameEvent);
    }

}

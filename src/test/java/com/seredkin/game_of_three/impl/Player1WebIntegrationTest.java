package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.seredkin.game_of_three.api.GameEvent.TYPE.END_GAME;
import static com.seredkin.game_of_three.api.GameEvent.TYPE.NEXT_MOVE;
import static com.seredkin.game_of_three.api.GameEvent.TYPE.START_GAME;
import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_1;
import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
/*As with IntegrationTests profile we run apps on the same host, we match the default 'opponentHost' value in application.yml  */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, value = "10002")
@ActiveProfiles({PLAYER_1, PLAYER_2})
@Slf4j
public class Player1WebIntegrationTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void contextLoads() {
        assertThat(ctx.getEnvironment().getActiveProfiles().length, equalTo(2));
    }


    @Test
    public void payer1StartGame() {
        WebTestClient webClient = WebTestClient.bindToApplicationContext(ctx).build();
        GameEvent gameEvent = webClient.get().uri("/" + PLAYER_1 + "/56").exchange().expectStatus().is2xxSuccessful().expectBody(GameEvent.class).returnResult().getResponseBody();

        assertThat(gameEvent, notNullValue());
        assertThat(gameEvent.getMoveValue(), equalTo(56));
        assertThat(gameEvent.getType(), equalTo(START_GAME));
    }

    @Test
    public void player1PlayTheGameSync() {
        WebTestClient webClient = WebTestClient.bindToApplicationContext(ctx).build();
        List<GameEvent> gameEvents = webClient
                .get().uri("/" + PLAYER_1 + "/play/56")
                .exchange().expectBodyList(GameEvent.class).returnResult().getResponseBody();
        assertThat(gameEvents, notNullValue());
        ConcurrentLinkedDeque<GameEvent> eventQueue = new ConcurrentLinkedDeque<>(gameEvents);
        assertThat(eventQueue.removeLast().getType(), equalTo(END_GAME));
        assertThat(eventQueue.removeFirst().getType(), equalTo(START_GAME));

        final GameEvent.TYPE[] types = eventQueue.stream().map(GameEvent::getType).distinct().toArray(GameEvent.TYPE[]::new);
        assertThat(types.length, equalTo(1));
        assertThat(types[0], equalTo(NEXT_MOVE));

    }

    @Test
    public void player1PlayTheGameAsync() {
        WebTestClient webClient = WebTestClient.bindToApplicationContext(ctx).build();
        ConcurrentLinkedDeque<GameEvent> remoteGameEvents = new ConcurrentLinkedDeque<>();
        webClient
                .get().uri("/" + PLAYER_1 + "/async/play/56")
                .exchange().returnResult(GameEvent.class).consumeWith(
                gameEventFluxExchangeResult -> Optional.ofNullable(
                        gameEventFluxExchangeResult.getResponseBody().collectList().block())
                        .map(remoteGameEvents::addAll)
                        .orElseThrow(() -> new IllegalArgumentException("Remote result is null")));

        assertThat(remoteGameEvents.size(), equalTo(5));
        assertThat(remoteGameEvents.getLast().getType(), equalTo(END_GAME));
    }

}

package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static com.seredkin.game_of_three.api.GameEvent.TYPE.NEXT_MOVE;
import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(PLAYER_2)
public class Player2WebIntegrationTest {

    static final GameEvent START_EVENT_56 = GameEvent.builder()
            .gameId(UUID.randomUUID().toString())
            .type(GameEvent.TYPE.START_GAME)
            .moveValue(56)
            .previousMoveValue(0)
            .player(GameEvent.PLAYER.PLAYER_1)
            .build();
    @Autowired
    private ApplicationContext ctx;

    @Test
    public void contextLoads() {
        assertThat(ctx.getEnvironment().getActiveProfiles()[0], equalTo(PLAYER_2));
    }

    @Test
    public void player2RemoteNextMove() {
        WebTestClient webClient = WebTestClient.bindToApplicationContext(ctx).build();
        GameEvent gameEvent = webClient
                .post().uri("/" + PLAYER_2).syncBody(START_EVENT_56)
                .exchange().expectBody(GameEvent.class).returnResult().getResponseBody();
        assertThat(gameEvent, notNullValue());
        assertThat(gameEvent.getType(), equalTo(NEXT_MOVE));
    }

}

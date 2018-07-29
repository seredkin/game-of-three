package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.Player1Service;
import com.seredkin.game_of_three.api.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.UUID;

import static com.seredkin.game_of_three.api.GameEvent.TYPE.END_GAME;
import static com.seredkin.game_of_three.api.GameEvent.TYPE.NEXT_MOVE;
import static com.seredkin.game_of_three.api.GameEvent.TYPE.START_GAME;
import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_1;
import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = {PLAYER_1, PLAYER_2})
@Slf4j
public class GameOfThreeComponentTests {


    @Autowired
    @Qualifier(PLAYER_1)
    private Player1Service player1Service;

    @Autowired
    @Qualifier(PLAYER_2)
    private PlayerService player2Service;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void contextLoadsProfiles() {
        assertThat(Arrays.asList(ctx.getEnvironment().getActiveProfiles()).containsAll(Arrays.asList(ServiceRoles.PLAYER_1, ServiceRoles.PLAYER_2)), equalTo(true));
    }

    @Test
    public void referenceGameFlow() {
        int refStartValue = 56;
        GameEvent startGameEvent = player1Service.startGame(refStartValue);
        assertThat(startGameEvent.getType(), equalTo(START_GAME));
        assertThat(startGameEvent.getMoveValue(), equalTo(refStartValue));
        GameEvent player2move1 = player2Service.nextMove(startGameEvent);
        assertThat(player2move1.getMoveValue(), equalTo(19));
        assertThat(player2move1.getPreviousMoveValue(), equalTo(refStartValue));
        GameEvent player1move2 = player1Service.nextMove(player2move1);
        assertThat(player1move2.getMoveValue(), equalTo(6));
        assertThat(player1move2.getPreviousMoveValue(), equalTo(player2move1.getMoveValue()));
        GameEvent player2move2 = player2Service.nextMove(player1move2);
        assertThat(player2move2.getMoveValue(), equalTo(2));
        assertThat(player2move2.getPreviousMoveValue(), equalTo(player1move2.getMoveValue()));
        GameEvent endGameEvent = player1Service.nextMove(player2move2);
        assertThat(endGameEvent.getType(), equalTo(END_GAME));
        assertThat(endGameEvent.getMoveValue(), equalTo(1));
    }

    @Test
    public void randomGame() {
        GameEvent gameEvent = player1Service.startGame();
        while (gameEvent.getType() != END_GAME) {
            gameEvent = (gameEvent.getPlayer() == GameEvent.PLAYER.PLAYER_2 ? player1Service : player2Service).nextMove(gameEvent);
        }
        assertThat(gameEvent.getMoveValue(), equalTo(1));
        assertThat(gameEvent.getType(), equalTo(END_GAME));
    }

    @Test
    public void maxValGame() {
        GameEvent gameEvent = player1Service.startGame(Integer.MAX_VALUE - 1);
        assertThat(gameEvent.getType(), equalTo(START_GAME));
        log.info(gameEvent.toString());
        while (gameEvent.getType() != END_GAME) {
            gameEvent = (gameEvent.getPlayer() == GameEvent.PLAYER.PLAYER_2 ? player1Service : player2Service).nextMove(gameEvent);
            log.info(gameEvent.toString());
        }
        assertThat(gameEvent.getMoveValue(), equalTo(1));
        assertThat(gameEvent.getType(), equalTo(END_GAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationExMinVal() {
        GameEvent gameEvent = player1Service.startGame();
        player2Service.nextMove(GameEvent.builder().gameId(gameEvent.getGameId())
                .player(GameEvent.PLAYER.PLAYER_1)
                .type(NEXT_MOVE)
                .previousMoveValue(gameEvent.getMoveValue())
                .moveValue(0).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationExMaxVal() {
        player1Service.startGame(Integer.MAX_VALUE);

    }

    @Test(expected = IllegalArgumentException.class)
    public void validationExSameService() {
        player2Service.nextMove(
                GameEvent.builder()
                        .gameId(UUID.randomUUID().toString())
                        .player(GameEvent.PLAYER.PLAYER_2)
                        .type(NEXT_MOVE)
                        .previousMoveValue(1)
                        .moveValue(1).build()
        );
    }

    @Test(expected = NullPointerException.class)
    public void nullValuesEx() {
        player2Service.nextMove(null);
    }

}

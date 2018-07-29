package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.Player1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_1;

@Service(value = PLAYER_1)
@Profile(PLAYER_1)
@Slf4j
class Player1ServiceImpl extends BasePlayerService implements Player1Service {

    final Random r = new Random();

    @Override
    public GameEvent startGame() {
        return this.startGame(r.nextInt(Integer.MAX_VALUE-4) + 3);
    }

    @Override
    public GameEvent startGame(int firstMoveValue) {
        return GameEvent.builder()
                .gameId(UUID.randomUUID().toString())
                .player(GameEvent.PLAYER.PLAYER_1)
                .type(GameEvent.TYPE.START_GAME)
                .previousMoveValue(0)
                .moveValue(validateValueRange(firstMoveValue)).build();
    }

    @Override
    public GameEvent.PLAYER getPlayerRole() {
        return GameEvent.PLAYER.PLAYER_1;
    }
}

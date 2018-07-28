package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.PlayerService;

import java.util.Objects;

abstract class BasePlayerService implements PlayerService {
    @Override
    public GameEvent nextMove(final GameEvent opponentsMove) {
        final int inVal = validateOpponentMove(opponentsMove).getMoveValue();
        //if opponent responds with 3 we win
        if (inVal <= 3) {
            return GameEvent.builder()
                    .gameId(opponentsMove.getGameId())
                    .previousEventId(opponentsMove.getEventId())
                    .player(getPlayerRole())
                    .type(GameEvent.TYPE.END_GAME)
                    .previousMoveValue(opponentsMove.getMoveValue())
                    .moveValue(1).build();
        }
        int outVal;
        if (inVal % 3 == 0) {
            outVal = inVal / 3;
        } else if ((inVal + 1) % 3 == 0) {
            outVal = (inVal + 1) / 3;
        } else {
            outVal = (inVal - 1) / 3;
        }
        return GameEvent.builder()
                .gameId(opponentsMove.getGameId())
                .previousEventId(opponentsMove.getEventId())
                .player(getPlayerRole())
                .type(GameEvent.TYPE.NEXT_MOVE)
                .previousMoveValue(inVal)
                .moveValue(outVal).build();
    }

    @Override
    public GameEvent validateOpponentMove(GameEvent opponentsMove) {
        if (Objects.requireNonNull(opponentsMove).getPlayer() == getPlayerRole()) {
            throw new IllegalArgumentException("Received a move from the same player type: " + getPlayerRole());
        }
        validateValueRange(opponentsMove.getMoveValue());
        return opponentsMove;
    }

    @Override
    public int validateValueRange(final int moveValue) {
        if (moveValue < 1 || moveValue == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    "Move's value %d is out of range (%d, %d)",
                    moveValue, 0, Integer.MAX_VALUE));
        }
        return moveValue;
    }
}

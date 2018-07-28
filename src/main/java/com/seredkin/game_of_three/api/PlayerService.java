package com.seredkin.game_of_three.api;


/** Defines common functions for all player roles */
public interface PlayerService {

    GameEvent.PLAYER getPlayerRole();

    /** A move with a division by 3 and returning NEXT_MOVE or END_GAME event type */
    GameEvent nextMove(GameEvent opponentsMove);

    /** Generic validation logic */
    GameEvent validateOpponentMove(GameEvent opponentsMove);

    /** Basic value range check for (0, Integer.MaxValue) */
    int validateValueRange(int moveValue);
}

package com.seredkin.game_of_three.api;

public interface Player1Service extends PlayerService {
    GameEvent startGame();

    GameEvent startGame(int firstMoveValue);

}

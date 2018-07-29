package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import com.seredkin.game_of_three.api.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile(ServiceRoles.PLAYER_2)
public class Player2Controller {
    @Autowired @Qualifier(ServiceRoles.PLAYER_2)
    private PlayerService player2Service;

    @PostMapping(ServiceRoles.PLAYER_2)
    public GameEvent nextMove(@RequestBody GameEvent gameEvent) {
        return player2Service.nextMove(gameEvent);
    }
}

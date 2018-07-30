package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PlayerController {
    @PostMapping(ServiceRoles.PLAYER_2)
    GameEvent nextMove(@RequestBody GameEvent gameEvent);
}

package com.seredkin.game_of_three.impl;

import com.seredkin.game_of_three.api.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.seredkin.game_of_three.impl.ServiceRoles.PLAYER_2;

@Service(value = PLAYER_2)
@Profile(PLAYER_2)
@Slf4j
class Player2ServiceImpl extends BasePlayerService {

    @Override
    public GameEvent.PLAYER getPlayerRole() {
        return GameEvent.PLAYER.PLAYER_2;
    }

}

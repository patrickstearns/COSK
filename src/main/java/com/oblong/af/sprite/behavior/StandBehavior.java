package com.oblong.af.sprite.behavior;

import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;

public class StandBehavior extends AbstractActorBehavior {

    public StandBehavior(){
        super(0, 0);
    }

    public void determineMovement(Actor actor, Player player, int tick) {
        stand(actor);
    }
}

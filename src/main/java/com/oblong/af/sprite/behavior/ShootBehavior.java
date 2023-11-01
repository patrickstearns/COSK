package com.oblong.af.sprite.behavior;

import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;

public class ShootBehavior extends AbstractActorBehavior {

    public ShootBehavior(int sightRadius){
        super(sightRadius, 0);
    }

    public void determineMovement(Actor actor, Player player, int tick) {
        stand(actor);
    }
}

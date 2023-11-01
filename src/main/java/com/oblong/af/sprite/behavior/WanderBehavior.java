package com.oblong.af.sprite.behavior;

import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;

public class WanderBehavior extends AbstractActorBehavior {

    public WanderBehavior(int sightRadius, int wanderRadius){
        super(sightRadius, wanderRadius);
    }

    public void determineMovement(Actor actor, Player player, int tick) {
        wander(actor, tick);
        actor.setRunning(false);
    }
}

package com.oblong.af.sprite.behavior;

import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;

import java.awt.geom.Point2D;

public class ChaseAndShootBehavior extends AbstractActorBehavior {

    public ChaseAndShootBehavior(int sightRadius, int wanderRadius){
        super(sightRadius, wanderRadius);
    }

    public void determineMovement(Actor actor, Player player, int tick) {
        if (player.isDead()){
            wander(actor, tick);
            actor.setRunning(false);
        }

        if (tick%10 == 0){
            Point2D.Float actorP = new Point2D.Float(actor.getX(), actor.getY());
            Point2D.Float origP = new Point2D.Float(actor.getSpriteTemplate().getOriginalX(), actor.getSpriteTemplate().getOriginalY());
            Point2D.Float playerP = new Point2D.Float(player.getX(), player.getY());

            int distToPlayer = (int)actorP.distance(playerP);
            int distToOrigin = (int)actorP.distance(origP);

            //if within sight distance of player and 4x wander distance to original point, give chase
            if (distToPlayer < getSightRadius() && distToOrigin < 4*getWanderRadius()){
                double xDiff = playerP.getX()-actorP.getX();
                double yDiff = playerP.getY()-actorP.getY();
                if (distToPlayer < getSightRadius()/2){ //run away
                    playerP.x = (float)(actorP.x-xDiff);
                    playerP.y = (float)(actorP.y-yDiff);
                }
                else { //try to get a line on the target
                    if (xDiff > yDiff){ //get a horizontal line
                        if (xDiff > 30) playerP.x = actorP.x;
                    }
                    else{ //get a vertical line
                        if (yDiff > 30) playerP.y = actorP.y;
                    }
                }

                moveToPoint(actor, playerP);
                actor.setRunning(true);
            }
            //if not chasing and outside of wander radius from origin, return to within sight radius
            else if (distToOrigin > getWanderRadius()){
                if (tick % 10 == 0){
                    moveToPoint(actor, origP);
                    actor.setRunning(false);
                }
            }
            //otherwise wander around
            else {
                wander(actor, tick);
                actor.setRunning(false);
            }
        }
    }
}

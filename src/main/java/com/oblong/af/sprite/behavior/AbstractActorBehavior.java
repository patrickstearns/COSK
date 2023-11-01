package com.oblong.af.sprite.behavior;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractActorBehavior {

    private int sightRadius, wanderRadius;

    public AbstractActorBehavior(int sightRadius, int wanderRadius){
        this.sightRadius = sightRadius;
        this.wanderRadius = wanderRadius;
    }
    
    public abstract void determineMovement(Actor actor, Player player, int tick);

    public int getSightRadius(){ return sightRadius; }
    public void setSightRadius(int sightRadius){ this.sightRadius = sightRadius; }

    public int getWanderRadius(){ return wanderRadius; }
    public void setWanderRadius(int wanderRadius){ this.wanderRadius = wanderRadius; }

    public void move(Actor actor){
        if (actor.isDead()) actor.setMoving(false);
        else{
            actor.getBehavior().determineMovement(actor, actor.getScene().player, actor.getTick());
            Actor.AbilitySlot useAbilitySlot = determineUseAbilitySlot(actor, actor.getScene().player, actor.getTick());
            AreaScene scene = actor.getScene();
            if (useAbilitySlot != null){
//                if (useAbilitySlot.ability.isDirected()){
                    Point wCenter = new Point((int)(actor.getX()+actor.getWidth()/2), (int)(actor.getY()+actor.getHeight()/2));
                    Point tCenter = new Point((int)(scene.player.getX()+scene.player.getWidth()/2),
                            (int)(scene.player.getY()+scene.player.getHeight()/2));

                    int xDiff = Math.abs(wCenter.x-tCenter.x);
                    int yDiff = Math.abs(wCenter.y-tCenter.y);
                    Facing f;
                    if (xDiff >= yDiff){
                        if (wCenter.x-tCenter.x < 0) f = Facing.RIGHT;
                        else f = Facing.LEFT;
                    }
                    else{
                        if (wCenter.y-tCenter.y < 0) f = Facing.DOWN;
                        else f = Facing.UP;
                    }

                    actor.setFacing(f);
//                }
                actor.tap(useAbilitySlot);
            }
        }
    }

    protected void stand(Actor actor){ actor.setMoving(false); }

    protected void wander(Actor actor, int tick){
        Point2D.Float actorP = new Point2D.Float(actor.getX(), actor.getY());
        Point2D.Float origP = new Point2D.Float(actor.getSpriteTemplate().getOriginalX(), actor.getSpriteTemplate().getOriginalY());
        int distToOrigin = (int)actorP.distance(origP);

        if (tick % 10 == 0){
            //if we're outside of our wander radius, move toward our center point
            if (distToOrigin > getWanderRadius()) moveToPoint(actor, origP);
                //otherwise possibly stop moving for a moment
            else if (Math.random() < 0.2f) actor.setMoving(false);
                //elsewise pick a direction to go in
            else{
                actor.setHeading(Math.toRadians((Math.random()*10000)%360));
                actor.setMoving(true);
            }
        }
    }

    protected void moveToPoint(Actor actor, Point2D.Float target){
        actor.setHeading(Math.atan2(target.getX()-actor.getX(), target.getY()-target.getY()));
        actor.setMoving(true);
    }

    protected double getProjectileHeading(Actor actor, Player player){
        double xDiff = actor.getScene().player.getX()-actor.getX();
        double yDiff = actor.getScene().player.getY()-actor.getY();

        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        atan += Math.random()*0.4-0.2;
        return atan;
    }

    public Actor.AbilitySlot determineUseAbilitySlot(Actor actor, Player player, int tick){
        List<Actor.AbilitySlot> usableSlots = new ArrayList<Actor.AbilitySlot>();

        for (Actor.AbilitySlot slot: new ArrayList<Actor.AbilitySlot>(Arrays.asList(actor.getAbilitySlots()))){
            if (!actor.abilitySlotActive(slot)) continue;
            else if (!actor.abilityEnabled(slot.ability)) continue;
            else if (Point.distance(actor.getX(), actor.getY(), player.getX(), player.getY()) > 128)
                continue;
            usableSlots.add(slot);
        }

        Actor.AbilitySlot ret = usableSlots.get(0);

        return ret;
    }

}

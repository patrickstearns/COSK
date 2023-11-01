package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

public class FireField extends Prop {

    private int timeToLive, noParentTime = -1;
    private Prop parent;
    private boolean persists;

    public FireField(String id, AreaScene scene, int timeToLive, Prop parent, boolean persists, int noParentTime) {
        super(id, scene, 6, 4);
        setSheet(Art.objects16x32);
        this.timeToLive = timeToLive;
        this.persists = persists;
        this.parent = parent;
        this.noParentTime = noParentTime;

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setBlockableByScreenEdge(false);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(false);

        setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Fire)));
    }

    public void move(){
        super.move();

        timeToLive--;
        int xOff = 0, yOff = 0;
        if (timeToLive < 6){
            xOff = 6-timeToLive;
            yOff = 1;
        }
        else xOff = timeToLive%6;

        setXPic(getOxPic()+xOff);
        setYPic(getOyPic() + yOff);

        if (timeToLive <= 0){
            getScene().removeSprite(this);
        }

        if (noParentTime > 0 && getTick() >= noParentTime) parent = null;

        //if on water we go out nearly immediately
        if (getTileBehaviorsUnderSprite().contains(Block.Trait.Water) && timeToLive > 6)
            timeToLive = 6;

        poof(1);

        if (getTick()%30==1)
            getScene().getSound().play(Art.getSample("sizzle.wav"), this, 1, 1, 1);
    }

    public void die(){
        super.die();
        setDeadCounter(1);
        poof(40);
    }

    private void poof(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            getScene().addSprite(new Puff(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset)));
        }
    }

    protected void collided(Prop with){
        if (with != parent && !(with instanceof FireField)){
            if (!persists) die();
            with.damage(getImpactDamageAttributes());
        }
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

}

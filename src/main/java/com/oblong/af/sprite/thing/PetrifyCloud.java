package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class PetrifyCloud extends Prop {

    private int timeToLive;
    private Prop parent;
    private boolean persists;

    public PetrifyCloud(AreaScene scene, int timeToLive, Prop parent, boolean persists) {
        super("PetrifyCloud", scene, 0, 8);
        setSheet(Art.effects32x32);
        setWPic(32);
        setHPic(32);
        setWidth(32);
        setHeight(32);

        this.timeToLive = timeToLive;
        this.persists = persists;
        this.parent = parent;

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

        setImpactDamageAttributes(new DamageAttributes(0, Arrays.asList(Attribute.Petrify)));
        setDesaturated(true);
    }

    public void move(){
        super.move();

        int xOff = getTick()%4;

        setXPic(getOxPic()+xOff);
        setYPic(9);

        if (getTick() > timeToLive){
            getScene().removeSprite(this);
        }

        if (getTick() > timeToLive/2f){
            float r = (timeToLive-getTick())/(timeToLive/2f);
            setFadeRatio(r);
        }

        if (getSpeed() > 0) setSpeed(getSpeed()*0.90f);
    }

    public void die(){
        super.die();
        setDeadCounter(1);
    }

    protected void collided(Prop with){
        if (with != parent && !(with instanceof PetrifyCloud)){
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

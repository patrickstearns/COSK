package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Rats wander around unless the player gets too close, then they run away.
 *
 * For rats, "angered" is really "surprised and running away"
 */

public class Rat extends Prop {

    private int moveTime = 0, maxMoveTime = 100;
    private int lastHp;
    private boolean angered = false;

    public Rat(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.rat);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(-6);
        setWidth(8);
        setHeight(8);
        moveTime = maxMoveTime;

        setImpactDamageAttributes(new DamageAttributes(0, Arrays.asList(Attribute.Knockback)));

        lastHp = getHp();
        setCanBeKnockedback(true);
        setShadowVisible(false);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.SummonRat);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Haste);
        }
    }

    public void calcPic(){
        if (!isDead()){
            int xOff = 0, yOff = 0;

            double h = Math.toDegrees(getHeading());
            while (h < 0) h += 360;
            if (h <= 22.5) xOff = 0;
            else if (h <= 67.5) xOff = 7;
            else if (h <= 112.5) xOff = 6;
            else if (h <= 159.5) xOff = 5;
            else if (h <= 204.5) xOff = 4;
            else if (h <= 249.5) xOff = 3;
            else if (h <= 293.5) xOff = 2;
            else if (h <= 337.5) xOff = 1;
            else xOff = 0;

            if ((getTick()/2)%2 == 0) yOff = 1;

            setXPic(getOxPic() + xOff);
            setYPic(getOyPic() + yOff);
        }
    }

    private double headingTowardPlayer(){
        if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) return Math.random()*2*Math.PI;

        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (getHp() < lastHp){
                if (!angered) setSurprisedTime(16);
                angered = true;
            }

            double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
            if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
            int sightRadius = 90;

            //if really close get angry
            if (distance < sightRadius && !angered){
                angered = true;
                setSurprisedTime(16);
            }

            //if far off, stop being angry
            if (distance > sightRadius*2 && angered){
                angered = false;
            }

            //if player is too close, shock if we can or move away
            if (distance < sightRadius){
                double h = headingTowardPlayer()+Math.PI-0.2f+0.4f*Math.random();
                if (h > 2*Math.PI) h -= 2*Math.PI;
                setHeading(h);
                setMoving(true);
                setSpeed(3f);
            }
            else {
                double h = getHeading()-0.2f+0.4f*Math.random();
                if (h > 2*Math.PI) h -= 2*Math.PI;
                setHeading(h);
                setSpeed(1.5f);
                setMoving(true);
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
        }
        calcPic();

        super.move();

        lastHp = getHp();

        if (Math.random() < 0.05)
            getScene().getSound().play(Art.getSample("rat.wav"), this, 1, 1, 1);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }
}

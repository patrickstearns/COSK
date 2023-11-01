package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Rabides hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to tongue him to death.
 */

public class Rabide extends Prop {

    private int hopTime = 0, maxHopTime = 6;
    private int stopTime = 0, maxStopTime = 10;
    private int attackTime = 0, maxAttackTime = 8;
    private int damageTime = 0, maxDamageTime = 8;
    private int colorYOffset = 0;
    private boolean angered = false;

    public Rabide(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.rabides);
        setWPic(32);
        setHPic(48);
        setXPicO(0);
        setYPicO(-8);
        stopTime = maxStopTime;

        int blarg = (int)((Math.random()*100)%3);
        colorYOffset = 4*blarg;

        setCanBeKnockedback(true);
        setHalfSize(true);
        setShadowVisible(false);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AffinityGem);

        double randpow = Math.random();
        if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Speed);
        }
        else if (randpow < 0.2){
            setPowerupDrop(Powerups.HpPotion);
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;
        switch(getFacing()){
            case LEFT: yOff = 2; break;
            case RIGHT: yOff = 3; break;
            case UP: yOff = 1; break;
            case DOWN: default: yOff = 0; break;
        }

        if (isDead()){
            xOff = 3;
            yOff = 1;
        }
        else if (damageTime > 0){
            xOff = 3;
            yOff = 0;
        }
        else if (stopTime > 0) xOff = 0;
        else if (hopTime > 0) xOff = (int)(2*((float)hopTime/(float)maxHopTime));
        else if (attackTime > 0)
            if (attackTime < maxAttackTime/2) xOff = 4+(int)(4*((float)attackTime/(float)maxAttackTime));
            else xOff = 8-(int)(3*((float)attackTime/(float)maxAttackTime));
        if (xOff > 7) xOff = 7;

        if (xOff == 7 && getFacing() == Facing.LEFT) setXPicO(6);
        else if (xOff == 7 && getFacing() == Facing.RIGHT) setXPicO(-6);
        else setXPicO(0);

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
    }

    protected float determineSpeed(){
        if (hopTime > 0) return 4;
        else return 0;
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
            if (damageTime > 0){
                damageTime--;
            }
            else if (hopTime > 0){
                hopTime--;

                if (hopTime == maxHopTime-1)
                    getScene().getSound().play(Art.getSample("boing.wav"), this, 0.5f, 1, 1);

                if (hopTime == 0){
                    if (angered) stopTime = 2;
                    else stopTime = maxStopTime+(int)(Math.random()*10);
                }
            }
            else if (stopTime > 0){
                stopTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (angered && distance < 36d){
                    attackTime = maxAttackTime;
                    stopTime = 0;
                }
                else if (angered && distance > 128d){
                    angered = false;
                    stopTime = 1;
                }
                else if (stopTime == 0){
                    hopTime = maxHopTime;
                    if (angered) setHeading(headingTowardPlayer());
                    else setHeading(Math.random()*(Math.PI*2));
                }
            }
            else if (attackTime > 0){
                attackTime--;

                setHeading(headingTowardPlayer());

                if (attackTime == maxAttackTime/2){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                    if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                    if (distance < 36)
                        getScene().player.damage(new DamageAttributes(1, Arrays.asList(Attribute.Physical, Attribute.Water)));
                    getScene().getSound().play(Art.getSample("bite.wav"), this, 1, 1, 1);
                }

                if (attackTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    hopTime = maxHopTime;
                }
            }
        }
        else {
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            stopTime = maxStopTime;
            hopTime = 0;
            attackTime = 0;
        }

        setMoving(hopTime > 0 && damageTime == 0);

        super.move();

        calcPic();
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp){
            damageTime = maxDamageTime;
            if (!angered) setSurprisedTime(16);
            angered = true;
        }
    }

    public void setMiniboss(boolean miniboss){
        super.setMiniboss(miniboss);
        if (miniboss){
            angered = true;
            setSurprisedTime(8);
        }
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }
}

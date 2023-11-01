package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Mantises buzz around, stopping and blinking for random amounts of time between moving.  If activated, they chase the player and
 *   attempt to attack him to death.
 */

public class Mantis extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int attackTime = 0, maxAttackTime = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;

    public Mantis(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.mantis);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(-8);
        setSpeed(2);
        moveTime = maxMoveTime;
        setCanBeKnockedback(true);
        setHeading(Math.random()*2*Math.PI);
        setFlying(true);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.FullHpPotion);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.PoisonBreath);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.PoisonSting);
        }
        else if (randpow < 0.2){
            setPowerupDrop(Powerups.PoisonPotion);
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;

        if (getXa() < 0){
            if (getYa() < 0) yOff = 2;
            else yOff = 0;
        }
        else{
            if (getYa() < 0) yOff = 3;
            else yOff = 1;
        }

        //if we're attacking, usually
        if (getXa() == 0 && getYa() == 0){
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT: yOff = 0; break;
                case RIGHT: yOff = 3; break;
                case UP: yOff = 2; break;
                case DOWN: yOff = 1; break;
            }
        }

        if (isDead() || damageTime > 0){
            xOff = 5;
            yOff = 0;
        }
        else if (attackTime > maxAttackTime/2) xOff = 4;
        else xOff = (getTick()/2)%3;

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
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

    public float determineSpeed(){ return angered ? 5 : 1; }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (damageTime > 0){
                damageTime--;
            }
            else if (moveTime > 0){
                moveTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (angered && distance < 36d){
                    attackTime = maxAttackTime;
                    moveTime = 0;
                }
                else if (angered && distance > 128d && !isMiniboss()){
                    angered = false;
                    moveTime = maxMoveTime;
                    setHeading(Math.random()*(Math.PI*2));
                }
                else{
                    if (angered) setHeading(headingTowardPlayer());
                }
            }
            else if (attackTime > 0){
                attackTime--;

                setHeading(headingTowardPlayer());

                if (attackTime == maxAttackTime-1)
                    getScene().getSound().play(Art.getSample("weaponWhiff.wav"), this, 1, 1, 1);

                if (attackTime == maxAttackTime/2){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                    if (distance < 36)
                        getScene().player.damage(new DamageAttributes(1, Arrays.asList(Attribute.Physical, Attribute.Water)));

                }

                if (attackTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    moveTime = maxMoveTime;
                }
            }
            else{
                setMovementLockedCounter(0);
                setXa(0);
                setYa(0);
                setMovementLockedCounter(2);
                moveTime = maxMoveTime;
                setHeading(Math.random()*(Math.PI*2));
            }

            setMoving(moveTime > 0 && damageTime == 0);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            attackTime = 0;
        }

        super.move();

        if (getTick()%12 == 0)
            getScene().getSound().play(Art.getSample("mantis.wav"), this, 0.3f, 1, 1);

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
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }
}

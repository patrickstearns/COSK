package com.oblong.af.sprite.enemy;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.TailedBolt;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Spinnyblobs hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to spin attack him to death.
 */

public class Spikehog extends Prop {

    private int stopTime = 0, maxStopTime = 10;
    private int attackTime = 0, maxAttackTime = 9;
    private int shootTime = 0, maxShootTime = 16;
    private int spinDownTime = 0, spinUpTime = 0, maxSpinTime = 12;
    private int damageTime = 0, maxDamageTime = 8;
    private int spinDelayCounter = 0, maxSpinDelayCounter = 16;
    private int colorYOffset = 0;
    private boolean angered = false;

    public Spikehog(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.spikehog);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(0);
        stopTime = maxStopTime;
        setShadowVisible(false);
        setCanBeKnockedback(true);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AbilityGem);
        setMinibossAbilityDrop(Ability.FingersOfTheEarth);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Tunnel);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.EarthRend);
        }
        else if (randpow < 0.2){
            setPowerupDrop(Powerups.HpPotion);
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;
        switch(getFacing()){
            case LEFT: yOff = 1; break;
            case RIGHT: yOff = 0; break;
            case UP: yOff = 2; break;
            case DOWN: default: yOff = 3; break;
        }

        if (isDead()){
            xOff = 3;
            yOff = 4;
        }
        else if (damageTime > 0){
            xOff = 2;
            yOff = 4;
        }
        else if (spinUpTime > 0){
            if (spinUpTime > maxSpinTime-3){
                xOff = 3-(maxSpinTime-spinUpTime);
                yOff = 5;
            }
            else {
                xOff = spinUpTime%2;
                yOff = 4;
            }
        }
        else if (spinDownTime > 0){
            if (spinDownTime > 3){
                xOff = spinDownTime%2;
                yOff = 4;
            }
            else{
                xOff = 3-spinDownTime;
                yOff = 5;
            }
        }
        else if (stopTime > 0) xOff = 0;
        else if (attackTime > 0)
            xOff = 1+(maxAttackTime-attackTime)/3;

        setVisible(spinDelayCounter == 0);

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
    }

    protected float determineSpeed(){
        return 0;
    }

    private double headingTowardPlayer(){
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
            else if (spinDownTime > 0){
                spinDownTime--;

                if (spinDownTime % 2 == 0)
                    getScene().getSound().play(Art.getSample("rip.wav"), this, 1, 1, 1);

                if (spinDownTime == 0){
                    spinDelayCounter = maxSpinDelayCounter;
                }
            }
            else if (spinDelayCounter > 0){
                spinDelayCounter--;

                setCollidable(false);

                boolean invisible = false;
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) invisible = true;

                if (spinDelayCounter == 0){
                    //teleport to just in front of player
                    int newX, newY;
                    if (angered && !invisible){
                        Actor player = getScene().player;
                        newX = (int)(player.getX()+player.getXa()*4);
                        newY = (int)(player.getY()+player.getYa()*4);
                        if (player.getXa() == 0 && player.getYa() == 0) newY = (int)(player.getY()+24);
                    }
                    else{
                        newX = (int)(getX()+Math.cos(getHeading())*(64+Math.random()*64));
                        newY = (int)(getY()+Math.sin(getHeading())*(64+Math.random()*64));
                    }
                    setX(newX);
                    setY(newY);
                    setXOld(newX);
                    setYOld(newY);

                    //don't let them come up inside walls n' such
                    if (getScene().areaGroup.getCurrentArea().getTileset().getBlockFootprint(
                            getScene().areaGroup.getBlock((int)(getX()/16), (int)(getY()/16), Area.Layer.Main).blockId) != Block.BlockFootprint.None){
                        resetToLastUnblockedPosition();
                    }

                    spinUpTime = maxSpinTime;
                    setCollidable(true);
                }
            }
            else if (spinUpTime > 0){
                spinUpTime--;

                if (spinUpTime%2 == 0)
                    getScene().getSound().play(Art.getSample("rip.wav"), this, 1, 1, 1);

                if (spinUpTime == 0){
                    stopTime = maxStopTime;
                }
            }
            else if (stopTime > 0){
                stopTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (getScene().player.isDead()) distance = Double.MAX_VALUE;

                if (distance < 36d) angered = true;

                if (angered && distance < 36d){
                    attackTime = maxAttackTime;
                    stopTime = 0;
                }
                else if (stopTime == 0){
                    if (angered && distance < 72d){
                        setHeading(headingTowardPlayer());
                        spinDownTime = maxSpinTime;
                        stopTime = 0;
                    }
                    else if (distance > 128d){
                        if (!isMiniboss()) angered = false;
                        shootTime = maxShootTime;
                    }
                    else{
                        stopTime = maxStopTime;
                        if (angered) setHeading(headingTowardPlayer());
                        else{
                            setHeading(Math.random()*(Math.PI*2));
                            if (Math.random() < 0.2){
                                spinDownTime = maxSpinTime;
                            }
                        }
                    }
                }
            }
            else if (shootTime > 0){
                shootTime--;

                if (shootTime %6 == 0){
                    double sh = headingTowardPlayer()-Math.PI/8d+Math.random()*Math.PI/4d;
                    getScene().addSprite(TailedBolt.createSpikehogSpine(getScene(), this, sh));
                }

                if (shootTime == 0)
                    spinDownTime = maxSpinTime;
            }
            else if (attackTime > 0){
                attackTime--;

                setHeading(headingTowardPlayer());

                if (attackTime == 1){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                    if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                    if (distance < 36)
                        getScene().player.damage(new DamageAttributes(1, Arrays.asList(Attribute.Physical)));

                }

                if (attackTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    stopTime = maxStopTime;
                }
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            stopTime = maxStopTime;
            attackTime = 0;
            spinUpTime = 0;
            spinDownTime = 0;
        }

        if (spinDownTime > 0 || spinUpTime > 0){
            setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Wind, Attribute.Knockback)));
        }
        else{
            setImpactDamageAttributes(null);
        }

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
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }
}
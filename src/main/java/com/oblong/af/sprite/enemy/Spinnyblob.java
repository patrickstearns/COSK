package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Spinnyblobs hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to spin attack him to death.
 */

public class Spinnyblob extends Prop {

    private int hopTime = 0, maxHopTime = 6;
    private int stopTime = 0, maxStopTime = 10;
    private int attackTime = 0, maxAttackTime = 8;
    private int spinTime = 0, maxSpinTime = 6;
    private int damageTime = 0, maxDamageTime = 8;
    private int colorYOffset = 0;
    private boolean angered = false;

    public Spinnyblob(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.spinnyblob);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(0);
        stopTime = maxStopTime;
        setCanBeKnockedback(true);
        setShadowVisible(false);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AffinityGem);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.FierySpin);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Twirl);
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
            case UP: yOff = 3; break;
            case DOWN: default: yOff = 2; break;
        }

        if (isDead()){
            xOff = 4;
            yOff = 4;
        }
        else if (damageTime > 0){
            xOff = 4;
            yOff = 4;
        }
        else if (spinTime > 0){
            xOff = spinTime%4;
            yOff = 4;
        }
        else if (stopTime > 0) xOff = 0;
        else if (hopTime > 0) xOff = (int)(3*((float)hopTime/(float)maxHopTime));
        else if (attackTime > 0)
            if (attackTime < maxAttackTime/2) xOff = 4+(int)(2*((float)attackTime/(float)maxAttackTime));
            else xOff = 6-(int)(2*((float)attackTime/(float)maxAttackTime));
        if (xOff > 5) xOff = 5;

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
    }

    protected float determineSpeed(){
        if (spinTime > 0) return 6;
        else if (hopTime > 0) return 4;
        else return 0;
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
            else if (spinTime > 0){
                spinTime--;

                if (spinTime == maxSpinTime-1)
                    getScene().getSound().play(Art.getSample("wind.wav"), this, 1, 1, 1);

                if (spinTime == 0){
                    stopTime = maxStopTime;
                }
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
                    if (angered && distance < 72d){
                        setHeading(headingTowardPlayer());
                        spinTime = maxSpinTime;
                        stopTime = 0;
                    }
                    else{
                        hopTime = maxHopTime;
                        if (angered) setHeading(headingTowardPlayer());
                        else setHeading(Math.random()*(Math.PI*2));
                    }
                }
            }
            else if (attackTime > 0){
                attackTime--;

                setHeading(headingTowardPlayer());

                if (attackTime == maxAttackTime/2){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
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
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            stopTime = maxStopTime;
            hopTime = 0;
            attackTime = 0;
            spinTime = 0;
        }

        if (spinTime > 0) setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Wind)));
        else setImpactDamageAttributes(null);

        setMoving((hopTime > 0 || spinTime > 0) && damageTime == 0);

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

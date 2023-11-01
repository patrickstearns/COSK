package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.thing.FireField;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Spinnyblobs hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to spin attack him to death.
 */

public class Rollerpede extends Prop {

    private int stopTime = 0, maxStopTime = 20;
    private int attackTime = 0, maxAttackTime = 8;
    private int spinTime = 0, maxSpinTime = 8;
    private int damageTime = 0, maxDamageTime = 16;
    private int colorYOffset = 0;
    private boolean angered = false, rolling = false;
    private boolean fiery;

    public Rollerpede(String id, AreaScene scene, int oxPic, int oyPic, boolean fiery) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.rollerpede);
        setWPic(48);
        setHPic(48);
        setXPicO(0);
        setYPicO(-6);
        setWidth(24);
        stopTime = maxStopTime;

        this.fiery = fiery;
        colorYOffset = 0;//fiery ? 5 : 0;

        setCanBeKnockedback(true);
        setShadowVisible(false);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.HpMaxUpPotion);

        double randpow = Math.random();
        if (fiery){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.BigHpPotion);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Firefoot);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.BigHpPotion);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Charge);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;
        switch(getFacing()){
            case LEFT: yOff = 3; break;
            case RIGHT: yOff = 2; break;
            case UP: yOff = 0; break;
            case DOWN: default: yOff = 1; break;
        }

        if (isDead()){
            xOff = 5;
            yOff = 4;
        }
        else if (spinTime > 0){
            xOff = (spinTime/2)%4;
            yOff = 4;
        }
        else if (rolling){
            xOff = 5+(getTick()%4);
        }
        else if (damageTime > 0){
            xOff = 4;
            yOff = 4;
        }
        else if (stopTime > 0) xOff = (stopTime/4)%3;
        else if (attackTime > 0){
            if (attackTime == maxAttackTime/2) xOff = 4;
            else xOff = 3;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
    }

    protected float determineSpeed(){
        if (rolling) return 8;
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
            if (rolling);
            else if (damageTime > 0){
                damageTime--;
            }
            else if (attackTime > 0){
                attackTime--;

                setHeading(headingTowardPlayer());

                if (attackTime == maxAttackTime/2){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                    if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                    if (distance < 36)
                        getScene().player.damage(new DamageAttributes(1, Arrays.asList(Attribute.Physical, Attribute.Earth, Attribute.Knockback)));

                }

                if (attackTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    stopTime = maxStopTime;
                }
            }
            else if (spinTime > 0){
                spinTime--;

                if (spinTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    stopTime = maxStopTime;
                }
            }
            else if (stopTime > 0){
                stopTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (!angered && distance < 36d && attackTime == 0 && spinTime == 0){
                    attackTime = maxAttackTime;
                    stopTime = 0;
                }
                if (angered && distance < 36d && attackTime == 0 && spinTime == 0){
                    spinTime = maxSpinTime;
                    stopTime = 1;
                }
                else if (angered && distance > 128d && !isMiniboss()){
                    angered = false;
                    stopTime = 1;
                }
                else if (stopTime == 0){
                    if (Math.random() < 0.3f)
                        stopTime = maxStopTime;
                    else{
                        if (angered){
                            setHeading(headingTowardPlayer());
                            rolling = true;
                        }
                        else{
                            setHeading(Math.random()*(Math.PI*2));
                            rolling = true;
                        }
                    }
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
            spinTime = 0;
            rolling = false;
        }

        if (rolling){
            if (fiery) setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Fire, Attribute.Knockback)));
            else setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Earth, Attribute.Knockback)));

            if (getTick()%8==0) getScene().getSound().play(Art.getSample("rolling.wav"), this, 1, 1, 1);
        }
        else if (spinTime > 0){
            setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Poison, Attribute.Knockback)));
        }
        else setImpactDamageAttributes(null);

        setMoving(rolling);

        calcPic();

        if (rolling && fiery && getTick()%2==0){
            FireField ff = new FireField("ff", getScene(), 100, this, false, -1);
            ff.setX(getX());
            ff.setY(getY());
            getScene().addSprite(ff);
        }

        super.move();
    }

    public boolean move(float xa, float ya){
        boolean moved = !super.move(xa, ya);
        if (!moved || (xa == 0 && ya == 0)){
            if (rolling) getScene().getSound().play(Art.getSample("collide.wav"), this, 1, 1, 1);
            rolling = false;
            if (stopTime == 0) stopTime = maxStopTime;
        }
        return !moved;
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes in){
        in = super.modifyDamageAttributes(in);
        if (fiery && in.getAttributes().contains(Attribute.Fire)) in.setDamage(0);
        else if (!fiery && in.getAttributes().contains(Attribute.Earth)) in.setDamage(0);
        return in;
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

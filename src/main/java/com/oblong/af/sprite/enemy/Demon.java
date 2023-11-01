package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Demons walk around, stopping random amounts of time between moving.  If activated, they chase the player and
 *   attempt to attack him to death.
 */

public class Demon extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int attackTime = 0, maxAttackTime = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;

    public Demon(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.demon);
        setWPic(64);
        setHPic(64);
        setWidth(24);
        setHeight(32);
        setXPicO(0);
        setYPicO(-8);
        setSpeed(2);
        moveTime = maxMoveTime;
        setCanBeKnockedback(true);
        setHeading(Math.random()*2*Math.PI);
        setFlying(false);
        setShadowVisible(false);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AbilityGem);
        double randmin = Math.random();
        if (randmin < 0.02){ setMinibossAbilityDrop(Ability.EarthRend); }
        else if (randmin < 0.04){ setMinibossAbilityDrop(Ability.FreezingBolt); }
        else if (randmin < 0.06){ setMinibossAbilityDrop(Ability.Sparkler); }
        else if (randmin < 0.08){ setMinibossAbilityDrop(Ability.Stonefist); }
        else{ setMinibossAbilityDrop(Ability.HomingBolt); }

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.HpMaxUpPotion);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AffinityGem);
        }
        else {
            setPowerupDrop(Powerups.OneUp);
        }
    }

    public void calcPic(){
        int xOff, yOff;

        switch (getFacing()) {
            default: case DOWN: yOff = 0; break;
            case UP: yOff = 1; break;
            case RIGHT: yOff = 2; break;
            case LEFT: yOff = 3; break;
        }

        if (isDead()){
            xOff = 10;
            yOff = 1;
        }
        else if (damageTime > 0){
            xOff = 10;
            yOff = 0;
        }
        else if (attackTime > 0){
            if (attackTime > maxAttackTime/2)
                xOff = 6+((maxAttackTime-attackTime)*2)/maxAttackTime;
            else xOff = 8+(getTick()/6)%2;
        }
        else{
            int mod = angered ? 3 : 6;
            xOff = (getTick()/mod)%4;
            if (xOff == 3) xOff = 1;
            if (angered) xOff += 3;
        }

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

                if (getTick()%12 == 0)
                    getScene().getSound().play(Art.getSample("thunkMetal.wav"), this, 1, 1, 1);

                if (!angered && distance < 36d){
                    angered = true;
                }

                if (angered && distance < 36d){
                    attackTime = maxAttackTime;
                    moveTime = 0;
                }
                else if (angered && distance > 128d){
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
                        getScene().player.damage(new DamageAttributes(16, Arrays.asList(Attribute.Physical, Attribute.Spirit)));
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

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        modified.getAttributes().remove(Attribute.Freeze);
        modified.getAttributes().remove(Attribute.Drown);
        modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        //modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }

}

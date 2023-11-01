package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Rabides hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to tongue him to death.
 */

public class Zombie extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int stopTime = 0, maxStopTime = 10;
    private int attackTime = 0, maxAttackTime = 8;
    private int damageTime = 0, maxDamageTime = 8;
    private int colorYOffset = 0;
    private boolean purple;
    private boolean angered = false;

    public Zombie(String id, AreaScene scene, int oxPic, int oyPic, boolean purple) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.zombie);
        setWPic(64);
        setHPic(64);
        setXPicO(0);
        setYPicO(-16);
        if (!purple) maxAttackTime = 16;
        stopTime = maxStopTime;
        this.purple = purple;
        colorYOffset = purple ? 4 : 0;
        setShadowVisible(false);
        setCanBeKnockedback(true);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.HpMaxUpPotion);

        double randpow = Math.random();
        if (purple){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Shout);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Regenerate);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.PoisonPotion);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Wail);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Spiritproof);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.PoisonPotion);
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
            xOff = 9;
            yOff = purple ? 3-colorYOffset : 1;
        }
        else if (damageTime > 0){
            xOff = 9;
            yOff = purple ? 2-colorYOffset : 0;
        }
        else if (stopTime > 0) xOff = (getTick()/16)%2;
        else if (moveTime > 0){
            if (angered) xOff = 2+(getTick()/4)%4;
            else xOff = 2+(getTick()/8)%4;
        }
        else if (attackTime > 0){
            if (purple) xOff = (int)(6+4f*(float)(maxAttackTime-attackTime)/(float)maxAttackTime);
            else xOff = (int)(6+3f*(float)(attackTime)/(float)maxAttackTime);
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
    }

    protected float determineSpeed(){
        if (angered) return 3f;
        else return 0.5f;
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
            if (!isDead()){
                //regenerate over time
                if (getHp() < getMaxHp() && getTick()%16 == 0){
                    damage(new DamageAttributes(1, Arrays.asList(Attribute.Heal)));
                }

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;

                if (!angered && distance < 36){
                    if (!angered) setSurprisedTime(16);
                    angered = true;
                }

                if (damageTime > 0){
                    damageTime--;
                }
                else if (moveTime > 0){
                    moveTime--;

                    //if we get within attacking range of player while moving and are angry, cancel this and do our attack
                    if (angered && distance < 36d){
                        attackTime = maxAttackTime;
                        moveTime = 0;
                    }

                    if (moveTime == 0){
                        if (angered) stopTime = 2;
                        else stopTime = maxStopTime+(int)(Math.random()*10);
                    }
                }
                else if (stopTime > 0){
                    stopTime--;

                    if (angered && distance < 36d){
                        attackTime = maxAttackTime;
                        stopTime = 0;
                    }
                    else if (angered && distance > 128d){
                        angered = false;
                        stopTime = 1;
                    }
                    else if (stopTime == 0){
                        moveTime = maxMoveTime;
                        if (angered) setHeading(headingTowardPlayer());
                        else setHeading(Math.random()*(Math.PI*2));
                    }
                }
                else if (attackTime > 0){
                    attackTime--;

                    if (attackTime == maxAttackTime-1){
                        if (purple) getScene().getSound().play(Art.getSample("zombieAttack.wav"), this, 1, 1, 1);
                        else getScene().getSound().play(Art.getSample("whiff.wav"), this, 1, 1, 1);
                    }

                    setHeading(headingTowardPlayer());

                    if (attackTime == maxAttackTime/2)
                        if (distance < 36){
                            java.util.List<Attribute> damageAttributes = new ArrayList<Attribute>();
                            damageAttributes.add(Attribute.Physical);
                            if (purple) damageAttributes.add(Attribute.Poison);
                            getScene().player.damage(new DamageAttributes(1, damageAttributes));
                        }

                    if (attackTime == 0){
                        if (distance < 36) attackTime = maxAttackTime;
                        else  moveTime = maxMoveTime;
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
            moveTime = 0;
            attackTime = 0;
        }

        setMoving(moveTime > 0 && damageTime == 0 && !isDead());

        super.move();

        if (isMoving() && getTick()%8 == 0)
            getScene().getSound().play(Art.getSample("heavyFootstep.wav"), this, 1, 1, 1);

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

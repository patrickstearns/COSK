package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.MimeBlast;
import com.oblong.af.util.Art;

import java.awt.*;

public class MimeWizard extends Prop {

    private int fadeInTime = 0, maxFadeInTime = 16;
    private int fadeOutTime = 0, maxFadeOutTime = 16;
    private int moveTime = 0, maxMoveTime = 32;
    private int attackTime = 0, maxAttackTime = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;
    private boolean wizard;

    public MimeWizard(String id, AreaScene scene, int oxPic, int oyPic, boolean wizard) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.mimewizard);
        setWPic(48);
        setHPic(48);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        moveTime = maxMoveTime;
        this.wizard = wizard;

        setHeading(Math.random()*2*Math.PI);
        setCanBeKnockedback(true);
        setShadowVisible(false); //have a custom one

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.FullHpPotion);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Blink);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Vanish);
        }
        else if (randpow < 0.2){
            setPowerupDrop(Powerups.HpPotion);
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;

        if (getXa() < 0){
            if (getYa() < 0) yOff = 3;
            else yOff = 0;
        }
        else{
            if (getYa() < 0) yOff = 2;
            else yOff = 1;
        }

        //if we're attacking, usually
        if (getXa() == 0 && getYa() == 0){
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT: yOff = 0; break;
                case RIGHT: yOff = 2; break;
                case UP: yOff = 3; break;
                case DOWN: yOff = 1; break;
            }
        }

        if (isDead()){
            xOff = 5;
            yOff = 1;
        }
        else if (damageTime > 0){
            xOff = 5;
            yOff = 0;
        }
        else if (attackTime > maxAttackTime/2 && angered) xOff = 4;
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

    public float determineSpeed(){ return 8; }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (damageTime > 0){
                damageTime--;
            }
            else if (fadeInTime > 0){
                fadeInTime--;

                if (fadeInTime == maxFadeInTime-1)
                    getScene().getSound().play(Art.getSample("badStatusEffect.wav"), this, 1, 1, 1);

                float fadeRatio = (float)(maxFadeInTime-fadeInTime)/(float)maxFadeInTime;
                if (!wizard) fadeRatio = 1f-((1f-fadeRatio)/1.5f);
                setFadeRatio(fadeRatio);

                if (fadeInTime == 0){
                    attackTime = maxAttackTime;
                }
            }
            else if (attackTime > 0){
                attackTime--;
                setFadeRatio(1);

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (distance < 128){
                    setHeading(headingTowardPlayer());
                    if (!angered){
                        setSurprisedTime(16);
                        angered = true;
                    }
                }

                if (attackTime == maxAttackTime/2 && angered){
                    getScene().addSprite(new MimeBlast(getScene(), this, getHeading()));
                }
                else if (attackTime == 0){
                    fadeOutTime = maxFadeOutTime;
                }
            }
            else if (fadeOutTime > 0){
                fadeOutTime--;

                if (fadeOutTime == maxFadeOutTime-1)
                    getScene().getSound().play(Art.getSample("goodStatusEffect.wav"), this, 1, 1, 1);

                float fadeRatio = (float)fadeOutTime/(float)maxFadeOutTime;
                if (!wizard) fadeRatio = 1f-((1f-fadeRatio)/1.5f);
                setFadeRatio(fadeRatio);
                if (fadeOutTime == 0){
                    moveTime = (int)(maxMoveTime*Math.random());
                    setHeading(Math.random()*(Math.PI*2));
                    angered = false;
                }
            }
            else if (moveTime > 0){
                moveTime--;
                setFadeRatio(wizard ? 0f : 0.5f);

                if (moveTime == 0) fadeInTime = maxFadeInTime;
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            fadeInTime = 0;
            fadeOutTime = 0;
            attackTime = 0;
            setFadeRatio(1f);
        }

        setMoving(moveTime > 0 && damageTime == 0);

        //setCollidable(attackTime > 0 || !canAct);

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

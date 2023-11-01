package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Ability;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Powerups;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.TailedBolt;
import com.oblong.af.util.Art;

import java.awt.*;

/**
 * Rabides hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to tongue him to death.
 */

public class Petrowizard extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int stopTime = 0, maxStopTime = 32;
    private int attackTime = 0, maxAttackTime = 8;
    private int attackDelay = 0, maxAttackDelay = 24;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;

    public Petrowizard(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.stonewizard);
        setWPic(48);
        setHPic(64);
        setXPicO(0);
        setYPicO(-10);
        //stopTime = maxStopTime;

        setCanBeKnockedback(true);
        setShadowVisible(false);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AffinityGem);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Petrify);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Osmose);
        }
        else if (randpow < 0.2){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Aegis);
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
            xOff = 7;
            yOff = 2;
        }
        else if (damageTime > 0){
            xOff = 7;
            yOff = 3;
        }
        else if (stopTime > 0) xOff = 0;
        else if (moveTime > 0){
            int r = (int)((getRunTime()/6)%4);
            if (r%2 == 0) xOff = 0;
            else if (r == 1) xOff = 1;
            else if (r == 3) xOff = 2;
        }
        else if (attackTime > 0){
            xOff = (int)(3+3f*(float)(attackTime)/(float)maxAttackTime);
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

    protected float determineSpeed(){
        if (angered) return 2f;
        else return 1f;
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
            if (!isDead()){
                if (attackDelay > 0) attackDelay--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                boolean playerPetrified = getScene().player.hasStatusEffect(StatusEffect.Petrified);

                if (damageTime > 0){
                    damageTime--;
                }
                else if (attackTime > 0){
                    attackTime--;

                    //fire squirt
                    if (attackTime == maxAttackTime/2){
                        getScene().addSprite(TailedBolt.createPetrifyingBolt(getScene(), this, getHeading()));
                    }

                    if (attackTime == 0 || playerPetrified){
                        moveTime = maxMoveTime;
                        attackDelay = maxAttackDelay;
                    }
                }
                else if (stopTime > 0){
                    stopTime--;
                    if (stopTime == 0){
                        moveTime = maxMoveTime;
                    }
                }
                else if (moveTime > 0){
                    moveTime--;

                    int sightRadius = 90;

                    //if close get angry
                    if (distance < sightRadius && !angered && !playerPetrified){
                        angered = true;
                        setSurprisedTime(16);
                    }
                    //if far off, stop being angry
                    else if (distance > sightRadius*2 && angered || playerPetrified){
                        angered = false;
                    }

                    //if player is too close, move away
                    if (distance < sightRadius){
                        double h = headingTowardPlayer()+Math.PI;
                        if (h > 2*Math.PI) h -= 2*Math.PI;
                        setHeading(h);
                        setMoving(true);
                        setSpeed(3f);
                        moveTime = maxMoveTime;
                    }
                    //if angered and player is about right, shoot
                    else if (angered && distance < sightRadius*2f && attackDelay == 0){
                        attackTime = maxAttackTime;
                        moveTime = 0;
                        setHeading(headingTowardPlayer());
                        setMoving(false);
                    }
                    //if not angered and player is too far away, wander around
                    else {
                        setHeading(getHeading()-0.2f+0.4f*Math.random());
                        setSpeed(1.5f);
                        setMoving(true);
                        moveTime = maxMoveTime;
                    }

                    if (moveTime == 0){
                        stopTime = (int)(Math.random()*maxStopTime);
                        moveTime = 0;
                    }
                }
                else moveTime = 1; //so we don't get stuck
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = 0;
            stopTime = maxStopTime;
            attackTime = 0;
            attackDelay = 0;
        }

        setMoving(moveTime > 0 && damageTime == 0 && !isDead() && stopTime == 0); //shouldn't have to have stoptime in there but have to for some reason

        super.move();

        calcPic();
    }

    public void damage(DamageAttributes attributes){
        if (stopTime > 0) return; //invulnerable when stopped

        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp){
            damageTime = maxDamageTime;
            if (!angered) setSurprisedTime(16);
            angered = true;
        }
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);
        if (stopTime > 0){
            Image shield = Art.effects32x48[0][1];
            og.drawImage(shield, (int)(getX()-shield.getWidth(null)/2), (int)(getY()-shield.getHeight(null))+4, null);
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

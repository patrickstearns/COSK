package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.FeatherEffect;
import com.oblong.af.sprite.projectile.Gust;
import com.oblong.af.util.Art;

import java.awt.*;

/**
 * Mantises buzz around, stopping and blinking for random amounts of time between moving.  If activated, they chase the player and
 *   attempt to attack him to death.
 */

public class Miniroc extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int blowTime = 0, maxBlowTime = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;
    private boolean purple;

    public Miniroc(String id, AreaScene scene, int oxPic, int oyPic, boolean purple) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.gullibird);
        setWPic(48);
        setHPic(48);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        moveTime = maxMoveTime;
        this.purple = purple;

        setHeading(Math.random()*2*Math.PI);
        setShadowVisible(false);
        setCanBeKnockedback(true);
        setFlying(true);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.FullHpPotion);

        double randpow = Math.random();
        if (purple){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.CureWounds);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Gust);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.WindKick);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Windproof);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
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
                case RIGHT: yOff = 1; break;
                case UP: yOff = 3; break;
                case DOWN: yOff = 0; break;
            }
        }

        if (isDead()){
            xOff = 4;
            yOff = 2;
        }
//        else if (attackTime > maxAttackTime/2) xOff = 3;
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
                if (damageTime%3 == 0)
                    getScene().addSprite(new FeatherEffect(getScene(), (int)(getX()-getWidth()+Math.random()*getWidth()*2), (int)getY(), !purple));
            }
            else if (moveTime > 0){
                moveTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (!angered && distance <= 96d){
                    angered = true;
                    setSurprisedTime(16);
                    blowTime = maxBlowTime;
                    moveTime = 0;
                }
                else if (angered && distance <= 128d){
                    blowTime = maxBlowTime;
                    moveTime = 0;
                }
                else if (angered && distance > 196d){
                    angered = false;
                    moveTime = maxMoveTime;
                    setHeading(Math.random()*(Math.PI*2));
                }
                else{
                    if (angered) setHeading(headingTowardPlayer());
                }
            }
            else if (blowTime > 0){
                blowTime--;

                if (blowTime == maxBlowTime-1)
                    getScene().getSound().play(Art.getSample("miniroc.wav"), this, 1, 1, 1);

                setHeading(headingTowardPlayer());

                //blow wind bursts
                if (getXPic() == 2){
                    for (int i = 0; i < 6; i++){
                        double h = getHeading() - 0.1d + 0.2d * Math.random();
                        Gust g = new Gust(getScene(), this, h);
                        g.setY(getY() - 8); //to make it look like coming from wings
                        getScene().addSprite(g);
                    }
                }

                if (blowTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    moveTime = maxMoveTime;
                }
            }
            else{
                moveTime = maxMoveTime;
                setHeading(Math.random()*(Math.PI*2));
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            blowTime = 0;
        }

        setMoving(moveTime > 0 && damageTime == 0);

        super.move();

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("flap.wav"), this, 1, 1, 1);

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

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
        for (int i = 0; i < 6; i++)
            getScene().addSprite(new FeatherEffect(getScene(),
                    (int)(getX()-getWidth()+Math.random()*getWidth()*2),
                    (int)(getY()-getHeight()+Math.random()*getHeight()*2),
                    !purple));
    }

    public void setMiniboss(boolean miniboss){
        super.setMiniboss(miniboss);
        if (miniboss){
            angered = true;
            setSurprisedTime(8);
        }
    }

}

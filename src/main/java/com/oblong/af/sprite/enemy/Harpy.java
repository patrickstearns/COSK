package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.FeatherEffect;
import com.oblong.af.sprite.projectile.TornadoGust;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Mantises buzz around, stopping and blinking for random amounts of time between moving.  If activated, they chase the player and
 *   attempt to attack him to death.
 */

public class Harpy extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int attackTime = 0, maxAttackTime = 24;
    private int blowTime = 0, maxBlowTime = 32;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;
    private boolean purple;

    public Harpy(String id, AreaScene scene, int oxPic, int oyPic, boolean purple) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.harpy);
        setWPic(64);
        setHPic(64);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        moveTime = maxMoveTime;
        this.purple = purple;

        setHeading(Math.random()*2*Math.PI);

        setCanBeKnockedback(true);
        setFlying(true);
        setShadowVisible(false); //we do our own shadow

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.HpMaxUpPotion);

        double randpow = Math.random();
        if (purple){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Tornado);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.BigHpPotion);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Tornado);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.BigHpPotion);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
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
                case RIGHT: yOff = 1; break;
                case UP: yOff = 2; break;
                case DOWN: yOff = 0; break;
            }
        }

        if (isDead()){
            xOff = 4;
            yOff = 0;
        }
        else if (attackTime > 0 && attackTime < maxAttackTime/4) xOff = 3;
        else xOff = (getTick()/2)%3;

        if (attackTime >= maxAttackTime/4) setYPicO(maxAttackTime-attackTime);
        else setYPicO(attackTime*3);

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

    public float determineSpeed(){
        if (attackTime > 0 && attackTime < maxAttackTime/4) return 8;
        return angered ? 3 : 1;
    }

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
                if (distance <= 32d){
                    if (!angered){
                        angered = true;
                        setSurprisedTime(16);
                    }
                }

                if (angered && distance <= 64d){
                    if (Math.random() < 0.5)
                        blowTime = maxBlowTime;
                    else
                        attackTime = maxAttackTime;
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
            else if (attackTime > 0){
                attackTime--;

                if (attackTime == maxAttackTime/2)
                    getScene().getSound().play(Art.getSample("harpy.wav"), this, 1, 1, 1);

                setHeading(headingTowardPlayer());

                if (attackTime == 0){
                    moveTime = maxMoveTime;
                }
            }
            else if (blowTime > 0){
                blowTime--;

                setHeading(headingTowardPlayer());

                if (blowTime > maxBlowTime-16){
                    getScene().addSprite(new TornadoGust(getScene(), this));
                }

                if (blowTime%4 == 0){
                    FeatherEffect f = new FeatherEffect(getScene(), (int)getX(), (int)getY()-32, !purple);
                    f.setXa((int)(Math.random()*10-5));
                    f.setYa((int)(Math.random()*10-5));
                    getScene().addSprite(f);
                }

                if (blowTime == 0){
                    moveTime = maxMoveTime;
                }
            }
            else{
                moveTime = maxMoveTime;
                setHeading(Math.random()*(Math.PI*2));
            }

            setMoving((moveTime > 0 && damageTime == 0) || attackTime < maxAttackTime/4);
            if (blowTime > 0) setMoving(false);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            attackTime = 0;
            blowTime = 0;
        }

        if (attackTime > 0 && attackTime < maxAttackTime/4)
            setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Physical)));
        else setImpactDamageAttributes(null);

        super.move();

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("flap.wav"), this, 0.5f, 1, 0.5f);

        calcPic();
    }

    public void setMiniboss(boolean miniboss){
        super.setMiniboss(miniboss);
        if (miniboss){
            angered = true;
            setSurprisedTime(8);
        }
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
        for (int i = 0; i < 6; i++)
            getScene().addSprite(new FeatherEffect(getScene(),
                    (int)(getX()-getWidth()+Math.random()*getWidth()*2),
                    (int)(getY()-getHeight()+Math.random()*getHeight()*2)-32,
                    !purple));
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }

    public void render(Graphics2D og, float alpha){
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha);
        og.drawImage(getSheet()[4][1], xPixel-32, yPixel-64, null);

        super.render(og, alpha);
    }

}

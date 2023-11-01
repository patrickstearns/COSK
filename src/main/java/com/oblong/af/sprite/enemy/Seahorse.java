package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.Squirt;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Seahorses wander about aimlessly until angered; then attempt to squirt player from a distance.  Gray ones can also
 *   electric-aura themselves, shocking anything in the water.
 */

public class Seahorse extends Prop {

    private int moveTime = 0, maxMoveTime = 100;
    private int attackTime = 0, maxAttackTime = 8;
    private int shockTime = 0, maxShockTime = 20;
    private int damageTime = 0, maxDamageTime = 16;
    private int attackDelay = 0, maxAttackDelay = 12;
    private int colorYOffset = 0;
    private int lastHp;
    private boolean angered = false;
    private boolean shocker;

    public Seahorse(String id, AreaScene scene, int oxPic, int oyPic, boolean shocker) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.seahorses);
        setWPic(32);
        setHPic(48);
        setXPicO(0);
        setYPicO(-6);
        setWidth(16);
        setHeight(40);
        moveTime = maxMoveTime;

        this.shocker = shocker;
        colorYOffset = shocker ? 4 : 0;

        setCanBeKnockedback(true);
        setFlying(true);
        lastHp = getHp();

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.BigHpPotion);

        double randpow = Math.random();
        if (shocker){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.ShockFingers);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.ShockingAura);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Drown);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Squirt);
            }
        }
    }

    public void calcPic(){
        int xOff = 0, yOff;
        switch(getFacing()){
            case LEFT: yOff = 2; break;
            case RIGHT: yOff = 3; break;
            case UP: yOff = 1; break;
            case DOWN: default: yOff = 0; break;
        }

        if (isDead()){
            xOff = 4;
            yOff = 1;
        }
        else if (damageTime > 0){
            xOff = 4;
            yOff = 0;
        }
        else if (attackTime > 0){
            if (attackTime > maxAttackTime*3f/4f) xOff = 1;
            else if (attackTime > maxAttackTime/2f+2) xOff = 2;
            else if (attackTime > maxAttackTime/4f+2) xOff = 3;
            else if (attackTime > maxAttackTime/4f) xOff = 2;
            else xOff = 1;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + colorYOffset + yOff);
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
            if (getHp() < lastHp){
                damageTime = maxDamageTime;
                if (!angered) setSurprisedTime(16);
                angered = true;
            }

            if (attackDelay > 0) attackDelay--;

            if (damageTime > 0){
                damageTime--;
            }
            else if (attackTime > 0){
                attackTime--;

                //fire squirt
                if (attackTime >= maxAttackTime/2-2 && attackTime <= maxAttackTime/2+2){
                    getScene().addSprite(
                            new Squirt((int) getX(), (int) getY() - 1 + ((int) (Math.random() * 2))-12, headingTowardPlayer() - 0.02 + 0.04f * Math.random(), this));
                }

                if (attackTime == 0){
                    moveTime = (int)(Math.random()*maxMoveTime);
                    attackDelay = maxAttackDelay;
                }
            }
            else if (shockTime > 0){
                shockTime--;
                if (shockTime == maxShockTime-1)
                    getScene().getSound().play(Art.getSample("electricHum.wav"), this, 1, 1, 1);
                if (shockTime == 0) moveTime = (int)(Math.random()*maxMoveTime);
            }
            else if (moveTime > 0){
                moveTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                int sightRadius = 90;

                //if really close or a miniboss get angry
                if ((distance < sightRadius/2 || isMiniboss()) && !angered){
                    angered = true;
                    setSurprisedTime(16);
                }

                //if far off and not a miniboss, stop being angry
                if (distance > sightRadius*2 && angered && !isMiniboss()){
                    angered = false;
                }

                //if player is too close, shock if we can or move away
                if (distance < sightRadius/2){
                    if (shocker && Math.random() < 0.05f){
                        shockTime = maxShockTime;
                        moveTime = 0;
                        setMoving(false);
                    }
                    else{
                        double h = headingTowardPlayer()+Math.PI;
                        if (h > 2*Math.PI) h -= 2*Math.PI;
                        setHeading(h);
                        setMoving(true);
                        setSpeed(3f);
                    }
                }
                else if (angered){
                    //if angered and player is about right, shoot
                    if (distance < 56 && attackDelay == 0){
                        attackTime = maxAttackTime;
                        moveTime = 0;
                        setHeading(headingTowardPlayer());
                        setMoving(false);
                    }
                    //if angered and player is too far away, move in
                    else {
                        setHeading(headingTowardPlayer());
                        setSpeed(2f);
                        setMoving(true);
                    }
                }
                //if not angered and player is too far away, wander around
                else {
                    setHeading(getHeading()-0.2f+0.4f*Math.random());
                    setSpeed(1.5f);
                    setMoving(true);
                }
            }
            else moveTime = 1; //so we don't get stuck
        }
        else {
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            attackTime = 0;
            shockTime = 0;
            attackDelay = 0;
        }

        if (shockTime > 0 && canAct){
            setImpactDamageAttributes(new DamageAttributes(4, Arrays.asList(Attribute.Electric, Attribute.Knockback)));
        }
        else{
            setImpactDamageAttributes(null);
        }

        calcPic();

        super.move();

        lastHp = getHp();
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes in){
        in = super.modifyDamageAttributes(in);
        if (in.getAttributes().contains(Attribute.Water)) in.setDamage(0);
        else if (shocker && in.getAttributes().contains(Attribute.Electric)) in.setDamage(0);
        return in;
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        //draw electric effect over seahorse
        if (shockTime > 0){
            Image shockImage = Art.effects32x48[shockTime%4][0];
            og.drawImage(shockImage, (int)(getX()-shockImage.getWidth(null)/2), (int)(getY()-getHeight()+2), null);
        }
    }

    public void setMiniboss(boolean miniboss){
        super.setMiniboss(miniboss);
        if (miniboss){
            angered = true;
            setSurprisedTime(8);
        }
    }

}

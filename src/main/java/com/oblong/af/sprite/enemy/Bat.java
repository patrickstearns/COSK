package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * Mantises buzz around, stopping and blinking for random amounts of time between moving.  If activated, they chase the player and
 *   attempt to attack him to death.
 */

public class Bat extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int attackTime = 0, maxAttackTime = 2;
    private int attackDelay = 0, maxAttackDelay = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean angered = false;
    private boolean fiery; //if not fiery is icy

    public Bat(String id, AreaScene scene, int oxPic, int oyPic, boolean fiery) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.bat);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);

        moveTime = maxMoveTime;
        this.fiery = fiery;

        setShadowYOffset(0);
        setShadowVisible(true);
        setCanBeKnockedback(true);
        setFlying(true);

        setHeading(Math.random()*2*Math.PI);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            if (fiery) setAbilityDrop(Ability.FlamingAura);
            else setAbilityDrop(Ability.FreezingAura);
        }
    }

    public void calcPic(){
        int xOff, yOff;

        if (getXa() < 0){
            if (getYa() < 0) yOff = 3;
            else yOff = 1;
        }
        else{
            if (getYa() < 0) yOff = 2;
            else yOff = 0;
        }

        //if we're attacking, usually
        if (getXa() == 0 && getYa() == 0){
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT: yOff = 3; break;
                case RIGHT: yOff = 0; break;
                case UP: yOff = 2; break;
                case DOWN: yOff = 1; break;
            }
        }

        if (isDead() || damageTime > 0){
            xOff = 4;
            if (yOff == 1 || yOff == 3) yOff = 1;
            else yOff = 0;
        }
        else if (attackTime > 0) xOff = 3;
        else xOff = (getTick()/2)%3;

//        int yOffset = fiery ? 4 : 0;

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

    public float determineSpeed(){ return angered ? 3 : 1; }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (attackDelay > 0) attackDelay--;

            if (damageTime > 0){
                damageTime--;
            }
            else if (moveTime > 0){
                moveTime--;

                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;
                if (distance < 16d){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    moveTime = maxMoveTime;
                }
                else if (angered && distance < 48d && attackDelay == 0){
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

                if (attackTime == maxAttackTime/2){
                    double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                    if (distance < 48)
                        getScene().player.damage(new DamageAttributes(1, Arrays.asList(Attribute.Physical, Attribute.Water)));

                }

                if (attackTime == 0){
                    double heading = getHeading()+Math.PI;
                    if (heading > Math.PI*2) heading -= Math.PI*2;
                    setHeading(heading);
                    attackDelay = maxAttackDelay;
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
            attackTime = 0;
            attackDelay = 0;
        }

        setMoving(moveTime > 0 && damageTime == 0);

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("flap.wav"), this, 0.1f, 1, 0.2f);
        if (Math.random() < 0.01)
            getScene().getSound().play(Art.getSample("rat.wav"), this, 1, 1, 1);

        super.move();

        if (angered){
            if (fiery) setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Fire)));
            else setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Freeze)));
        }
        else setImpactDamageAttributes(null);

        calcPic();
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();

        if (angered && fiery && attributes.getAttributes().contains(Attribute.Fire)) attributes.setDamage(-attributes.getDamage());
        else if (angered && attributes.getAttributes().contains(Attribute.Water)) attributes.setDamage(-attributes.getDamage());

        super.damage(attributes);
        if (getHp() < hp){
            damageTime = maxDamageTime;
            if (!angered) setSurprisedTime(16);
            angered = true;
        }
    }

    public void render(Graphics2D og, float alpha){
        if (angered && fiery) setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.RED, Color.RED.darker().darker()}, 10));
        else if (angered) setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.CYAN, Color.BLUE.darker().darker()}, 10));
        else setOutlineColor(null);

        super.render(og, alpha);
    }

}

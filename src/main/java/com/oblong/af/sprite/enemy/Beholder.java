package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.HomingBolt;
import com.oblong.af.sprite.projectile.Laser;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

/**
 * Beholders move around slowly, occasionally stopping and opening their eye.  If they see the player, they shoot,
 *   otherwise they close their eye again and wander about.
 */

public class Beholder extends Prop {

    private int moveTime = 0, maxMoveTime = 32;
    private int stopTime = 0, maxStopTime = 16;
    private int attackTime = 0, maxAttackTime = 12;
    private int attackDelay = 0, maxAttackDelay = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean green;
    private Polygon viewCone = null;

    public Beholder(String id, AreaScene scene, int oxPic, int oyPic, boolean green) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.beholder);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(4);
        setSpeed(2);
        setWidth(24);
        setHeight(24);
        moveTime = maxMoveTime;
        this.green = green;
        setCanBeKnockedback(true);

        if (Math.random() < 0.1) setPowerupDrop(Powerups.HpPotion);
        setHeading(Math.random() * 2 * Math.PI);

        setMinibossifiable(true);
        setMinibossPowerupDrop(Powerups.AffinityGem);

        double randpow = Math.random();
        if (green){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.OneUp);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Laser);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
        else{
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.HomingBolt);
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
        int xOff, yOff;

        double deg = Math.toDegrees(getHeading());
        if (isDead()){
            xOff = 5;
            if (deg < 180) yOff = 0;
            else yOff = 1;
        }
        else if (deg < 90){ //up-right
            xOff = 0;
            yOff = 0;
        }
        else if (deg < 180){ //up-left
            xOff = 0;
            yOff = 1;
        }
        else if (deg < 270){ //down-left
            if (stopTime > 0) xOff = 2;
            else if (attackTime > 0) xOff = 3;
            else xOff = 1;
            yOff = 1;
        }
        else { //down-right
            if (stopTime > 0) xOff = 2;
            else if (attackTime > 0) xOff = 3;
            else xOff = 1;
            yOff = 0;
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

    public float determineSpeed(){ return 1; }

    private Polygon calculateViewCone(){
        double lowerAngle = getHeading()-Math.PI/4d;
        double higherAngle = getHeading()+Math.PI/4d;
        double radius = 128;
        int[] xs = new int[]{ (int)(getX()+Math.cos(lowerAngle)*radius), (int)(getX()+Math.cos(higherAngle)*radius), (int)getX() };
        int[] ys = new int[]{ (int)(getY()-Math.sin(lowerAngle)*radius)-16, (int)(getY()-Math.sin(higherAngle)*radius)-16, (int)getY()-16 };
        return new Polygon(xs, ys, 3);
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (attackDelay > 0) attackDelay--;

            if (damageTime > 0) damageTime--;
            else if (stopTime > 0){
                stopTime--;

                setHeading(getHeading()+0.025d);
                viewCone = calculateViewCone();
                if (viewCone != null && viewCone.intersects(getScene().player.getFootprint()) &&
                        !getScene().player.isDead() && !getScene().player.hasStatusEffect(StatusEffect.Invisible)){
                    attackTime = maxAttackTime;
                    if (green) attackTime = maxAttackTime*2;
                    setHeading(headingTowardPlayer());
                    stopTime = 0;
                    moveTime = 0;
                    setSurprisedTime(16);
                }
                else if (stopTime == 0){
                    moveTime = maxMoveTime;
                    viewCone = null;
                    setHeading(Math.random()*2*Math.PI);
                }
            }
            else if (attackTime > 0){
                attackTime--;
                if (attackTime == 5){
                    if (green) getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()));
                    else getScene().addSprite(new Laser((int)getX(), (int)getY()-1+((int)(Math.random()*2)), headingTowardPlayer()-0.02+0.04f*Math.random(), this));
                }
                if (attackTime == 0) stopTime = 1;
            }
            else if (moveTime > 0){
                moveTime--;
                if (moveTime == 0) stopTime = maxStopTime;
            }

            setMoving(moveTime > 0 && damageTime == 0);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            moveTime = maxMoveTime;
            stopTime = 0;
            attackTime = 0;
            attackDelay = 0;
        }
        super.move();

        calcPic();
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public void render(Graphics2D og, float alpha){
        //white "charging" outline
        if (attackTime > 5)
            setTintColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.ORANGE, Color.WHITE}, 3));

        //view cone
        if (viewCone != null){
            Color color = new Color(0.5f, 0f, 0f, 0.5f);
            GradientPaint gradient = new GradientPaint(viewCone.xpoints[2], viewCone.ypoints[2], color,
                    (viewCone.xpoints[0]+viewCone.xpoints[1])/2, (viewCone.ypoints[0]+viewCone.ypoints[1])/2, ImageUtils.TRANSPARENT);
            og.setPaint(gradient);
            og.fillPolygon(viewCone);
        }

        super.render(og, alpha);
    }

    public void setMiniboss(boolean miniboss){
        super.setMiniboss(miniboss);
        if (miniboss){
            setSurprisedTime(8);
        }
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }
}

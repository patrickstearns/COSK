package com.oblong.af.sprite.enemy;

import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.HomingBolt;
import com.oblong.af.sprite.projectile.OrbeholderLaser;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Orbeholders spin around their parent shooting when possible.
 */

public class Orbeholder extends Prop {

    private int attackTime = 0, maxAttackTime = 2;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean green;
    private int index;
    private BigBeholder parent;

    public Orbeholder(BigBeholder parent, int index, boolean green) {
        super("orbeholder_" + index, parent.getScene(), 0, 0);
        setSheet(Art.beholder);
        setWPic(16);
        setHPic(16);
        setXPicO(0);
        setYPicO(4);
        setSpeed(2);
        setWidth(24);
        setHeight(24);

        setMaxHp(8);
        setHp(8);

        setSuppressHpMeter(true);
        setCanBeKnockedback(false);
        setImpactDamageAttributes(new DamageAttributes(1, new ArrayList<Attribute>(Arrays.asList(Attribute.Knockback))));
        setShadowYOffset(4);

        this.green = green;
        this.index = index;
        this.parent = parent;

        setX(parent.getX());
        setY(parent.getY());

        setTick(parent.getTick());
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;

        if (getYa() > 0) xOff = 1;
        if (getXa() < 0) yOff = 1;
        if (getXa() == 0 && getYa() == 0){
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT:
                    xOff = 0;
                    yOff = 1;
                    break;
                case DOWN:
                    xOff = 2;
                    yOff = 1;
                    break;
                case RIGHT:
                    xOff = 2;
                    yOff = 0;
                    break;
                case UP:
                    xOff = 0;
                    yOff = 0;
                    break;
            }
        }

        if (isDead() || damageTime > 0) xOff = 4;
        else if (attackTime > 0 && xOff == 2) xOff = 3;

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff + (green ? 2 : 0));
    }

    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop instanceof Orbeholder) return false;
        if (prop == getScene().player && getScene().player.getKnockbackCounter() > 0) return false;
        return super.isCollidableWith(prop);
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

    public float determineSpeed(){ return 2; }

    private boolean hasLOSToPlayer(){
        Point loc = new Point((int)getX(), (int)getY());
        Point pLoc = new Point((int)getScene().player.getX(), (int)getScene().player.getY());
        //y = mx+b
        float m = (float)(loc.x-pLoc.x)/(float)(loc.y-pLoc.y);
        float b = loc.y - m * loc.x;
        for (int i = Math.min(loc.x, pLoc.x); i < Math.max(loc.x, pLoc.x); i+=4){
            int tx = i;
            int ty = (int)(m * i + b);
            for (Prop prop: getScene().propsBlockingMovement(new Rectangle(tx-1, ty-1, tx+1, ty+1))){
                if (prop != getScene().player && prop != this){
                    return false;
                }
            }
        }
        return true;
    }

    private Point2D.Double getIdealLocation(int radius){
        int period = 128;
        int maxOrbiters = parent.getMaxNumOrbiters();
        double orbitalIncrement = (2*Math.PI)/(double)maxOrbiters;
        double tickIncrement = (2*Math.PI)/(double)period;
        double tickMod = getTick()%period;
        double angle = index*orbitalIncrement+tickIncrement*tickMod;
        int xo = (int)(Math.cos(angle)*radius);
        int yo = (int)(Math.sin(angle)*radius);
        return new Point2D.Double(parent.getX()+xo, parent.getY()+yo);
    }

    private void moveTowardLocation(Point2D.Double location, double speed){
        double dx = location.x-getX(), dy = location.y-getY();
        double distance = Point2D.distance(location.x, location.y, getX(), getY());
        if (Math.abs(distance) > speed){
            double r = speed/distance;
            dx *= r;
            dy *= r;
        }
        setX((int)(getX()+dx));
        setY((int)(getY()+dy));
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            setHeading(headingTowardPlayer());

            if (damageTime > 0) damageTime--;

            if (attackTime > 0){
                attackTime--;
                //show ! icon if sees player
                if (attackTime == 1){
                    if (green) getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()));
                    else getScene().addSprite(new OrbeholderLaser((int)getX(), (int)getY()-1+((int)(Math.random()*2)), headingTowardPlayer()-0.02+0.04f*Math.random(), this, false));
                }
            }

            int radius = 64;
            if (parent.getHp() < parent.getMaxHp()/2) radius += Math.abs(((parent.getTick()*4)%128)-64);
            if (parent.getDamageTime() > 0) radius -= 32*Math.random();
            Point2D.Double idealLocation = getIdealLocation(radius);

            moveTowardLocation(idealLocation, 20);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            attackTime = 0;
        }

        super.move();

        calcPic();
    }

    public void shoot(){
        attackTime = maxAttackTime;
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
        parent.orbiterDestroyed(this);
        setDeadCounter(11);
    }

    public void render(Graphics2D og, float alpha){
        //white "charging" outline
        if (attackTime > 0)
            setTintColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.ORANGE, Color.WHITE}, 3));

        super.render(og, alpha);
    }

}

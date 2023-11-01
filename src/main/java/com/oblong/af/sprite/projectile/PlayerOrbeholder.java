package com.oblong.af.sprite.projectile;

import com.oblong.af.models.*;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.enemy.BigBeholder;
import com.oblong.af.sprite.enemy.Orbeholder;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Orbeholders spin around their parent shooting when possible.
 */

public class PlayerOrbeholder extends Prop {

    private int attackTime = 0, maxAttackTime = 2;
    private int damageTime = 0, maxDamageTime = 8;
    private int playerDamageTime = 0, maxPlayerDamageTime = 8;
    private boolean green;
    private int index;
    private Player parent;
    private int lastPlayerHp;
    private Prop target = null;
    private int attackDelay = 0, maxAttackDelay = 32;

    public PlayerOrbeholder(Player parent, int index, boolean green) {
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
        setCollidable(false);

        this.green = green;
        this.index = index;
        this.parent = parent;

        setX(parent.getX());
        setY(parent.getY());

        setTick(parent.getTick());
        lastPlayerHp = parent.getHp();
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
        if (prop instanceof PlayerOrbeholder) return false;
        return super.isCollidableWith(prop);
    }

    private void findTarget(){
        double dist = Double.MAX_VALUE;
        Prop newTarget = null;
        for (Prop prop: getScene().getDamageablePropsWithinRange((int)getX(), (int)getY(), 96)){
            if (prop == parent) continue;
            if (prop.isProjectile()) continue;
            if (prop.hasStatusEffect(StatusEffect.Invisible)) continue;

            if (prop.getSpriteTemplate() != null && prop.getSpriteTemplate().getSpriteDef() != null)
                if (SpriteDefinitions.isEnemy(prop.getId())){
                    Double pdist = Point2D.distance(prop.getX(), prop.getY(), getX(), getY());
                    if (pdist < dist){
                        dist = pdist;
                        newTarget = prop;
                    }
                }
        }

        target = newTarget;
    }

    private double headingTowardTarget(){

        if (target == null){
            if (getScene().player == null) return 0;
            double xDiff = getScene().player.getX()-getX();
            double yDiff = getScene().player.getY()-getY();
            double atan = Math.atan2(xDiff, yDiff);
            if(atan < 0) atan += Math.PI*2;
            atan -= Math.PI/2f;
            atan -= Math.PI; //turn it 180 degrees from the player
            return atan;
        }
        else{
            double xDiff = target.getX()-getX();
            double yDiff = target.getY()-getY();
            double atan = Math.atan2(xDiff, yDiff);
            if(atan < 0) atan += Math.PI*2;
            atan -= Math.PI/2f;
            return atan;
        }
    }

    public float determineSpeed(){ return 2; }

    private Point2D.Double getIdealLocation(int radius){
        int period = 64;
        int maxOrbiters = 2;
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
        setX((int) (getX() + dx));
        setY((int) (getY() + dy));
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (attackDelay > 0) attackDelay--;

            findTarget();
            setHeading(headingTowardTarget());
            if (target != null && attackDelay == 0){
                attackTime = maxAttackTime;
            }

            if (parent.getHp() < lastPlayerHp) playerDamageTime = maxPlayerDamageTime;
            lastPlayerHp = parent.getHp();

            if (damageTime > 0) damageTime--;
            if (playerDamageTime > 0) playerDamageTime--;

            if (attackTime > 0){
                attackTime--;
                if (attackTime == 1){
                    if (green) getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()));
                    else getScene().addSprite(new OrbeholderLaser((int)getX(), (int)getY()-1+((int)(Math.random()*2)), getHeading()-0.02+0.04f*Math.random(), this, false){
                        public boolean isCollidableWith(Prop prop){
                            if (prop.isProjectile()) return false;
                            if (prop == getScene().player) return false;
                            return super.isCollidableWith(prop);
                        }
                    });
                    attackDelay = maxAttackDelay;
                }
            }

            int radius = 24;
            if (playerDamageTime > 0) radius -= 16*Math.random();
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

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public void die(){
        super.die();
        parent.orbiterDestroyed(this); //tell ability that it needs to recharge
        setDeadCounter(11);
    }

    public void render(Graphics2D og, float alpha){
        //white "charging" outline
        if (attackTime > 0)
            setTintColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.ORANGE, Color.WHITE}, 3));

        super.render(og, alpha);
    }

}

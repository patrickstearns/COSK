package com.oblong.af.sprite.enemy;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.Gust;
import com.oblong.af.sprite.projectile.LightningStrike;
import com.oblong.af.sprite.projectile.TornadoGust;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * Wyvern flies down to start with and hovers just above ground.  From there, loops behaviors between hovering and causing
 *   a windstorm, flying high up offscreen and dropping boulders, and swooping in for a claw attack the same way harpies
 *   do.  After has lost half hp, will loose a lightning storm (random bolts) and again occasionally from there on.
 */

public class Wyvern extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "Wyvern";

    private int descendingTime = 0, maxDescendingTime = 32;
    private int ascendingTime = 0, maxAscendingTime = 32;
    private int hoveringTime = 0, minHoveringTime = 32, maxHoveringTime = 96;
    private int swoopTime = 0, maxSwoopTime = 32;
    private int stormTime = 0, maxStormTime = 32;
    private int altitude = 100, maxAltitude = 100;

    public Wyvern(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.bossWyvern112x80);
        setWPic(112);
        setHPic(80);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        setWidth(48);
        setHeight(80);
        descendingTime = maxDescendingTime;

        setCanBeKnockedback(false);
        setShadowVisible(false);

        setCollidable(false);

        setPowerupDrop(Powerups.AbilityGem);
        setAbilityDrop(Ability.StormsEye);
    }

    public void calcPic(){
        int xp, yp;
        if (swoopTime == 0 || swoopTime > maxSwoopTime/4){
            xp = (getTick()/2)%3;
            yp = 0;

            if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen) ||
                    hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
                xp = 1;
        }
        else{
            if (swoopTime > maxSwoopTime*0.2) xp = 0;
            else if (swoopTime > maxSwoopTime*0.1) xp = 1;
            else xp = 2;
            yp = 1;
        }

        if (getScene().player.getX() < getX()) yp += 2;

        setXPic(xp);
        setYPic(yp);
    }

    public void die(){
        super.die();
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4, 1, 1);
    }

    private double headingTowardPlayer(){
        if (getScene() == null || getScene().player == null) return 0;

        if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) return Math.random()*2*Math.PI;

        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        atan -= Math.PI/2f;
        if(atan < 0) atan += Math.PI*2;
        return atan;
    }

    public float determineSpeed(){ return getSpeed(); }

    private double distanceToPlayer(){
        Point loc = new Point((int)getX(), (int)getY());
        Point pLoc = new Point((int)getScene().player.getX(), (int)getScene().player.getY());
        return Point2D.distance(loc.x, loc.y, pLoc.x, pLoc.y);
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        setImpactDamageAttributes(null);

        if (canAct && !isDead()){
            if (descendingTime > 0){
                descendingTime--;

                altitude = maxAltitude*descendingTime/maxDescendingTime;
                setMoving(false);

                if (descendingTime == 0){
                    if (distanceToPlayer() < 64) swoopTime = maxSwoopTime;
                    else if (getHp() < getMaxHp()/2) stormTime = maxStormTime;
                    else hoveringTime = (int)(minHoveringTime+(maxHoveringTime-minHoveringTime)*Math.random());
                }
            }
            else if (ascendingTime > 0){
                ascendingTime--;

                altitude = maxAltitude*(maxAscendingTime-ascendingTime)/maxAscendingTime;
                setMoving(false);

                if (ascendingTime == 0){
                    stormTime = maxStormTime;
                }
            }
            else if (hoveringTime > 0){
                hoveringTime--;

                setHeading(headingTowardPlayer());
                setSpeed(1);
                setMoving(true);
                getScene().addSprite(new TornadoGust(getScene(), this));

                if (distanceToPlayer() < 64){
                    swoopTime = maxSwoopTime;
                    hoveringTime = 0;
                }
                else if (hoveringTime == 0){
                    ascendingTime = maxAscendingTime;
                }
            }
            else if (swoopTime > 0){
                swoopTime--;

                if (swoopTime >= maxSwoopTime/4){
                    altitude += 2;
                    setMoving(false);
                }
                else{
                    altitude -= 6;
                    setHeading(headingTowardPlayer());
                    setSpeed((float)Math.min(8, distanceToPlayer()));
                    setMoving(true);

                    setImpactDamageAttributes(new DamageAttributes(8, Arrays.asList(Attribute.Knockback, Attribute.Physical)));
                }

                if (swoopTime == maxSwoopTime/4)
                    getScene().getSound().play(Art.getSample("harpy.wav"), this, 1, 1, 1);

                if (swoopTime == 0){
                    if (distanceToPlayer() < 64) swoopTime = maxSwoopTime;
                    else ascendingTime = maxAscendingTime;
                }
            }
            else if (stormTime > 0){
                stormTime--;

                int sx = (int)(getX()-150+300*Math.random());
                int sy = (int)(getY()-150+300*Math.random());
                getScene().addSprite(new LightningStrike(getScene(), this, sx, sy));

                if (stormTime == 0){
                    double afp = Math.random()*2*Math.PI;
                    int nx = (int)(getScene().player.getX()+Math.cos(afp)*64);
                    int ny = (int)(getScene().player.getY()+Math.sin(afp)*64);
                    setX(nx);
                    setY(ny);
                    setXOld(nx);
                    setYOld(ny);
                    descendingTime = maxDescendingTime;
                }
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }

        //things related to altitude
        setYPicO(altitude);
        setCollidable((altitude < 8));
        if (altitude > maxAltitude-32) setFadeRatio((maxAltitude-altitude)/32f);
        if (canAct && altitude < 32 && (getTick()/2)%3 == 2){ //create circular gusts outward
            for (double h = 0; h < Math.PI*2; h += (Math.PI*2)/20)
                getScene().addSprite(new Gust(getScene(), this, h+Math.random()*0.1));
        }

        if (altitude < 32) setLayer(Area.Layer.Main);
        else setLayer(Area.Layer.Upper);

        super.move();

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("flap.wav"), this, 0.5f, 1, 0.2f);

        calcPic();
    }

    public void render(Graphics2D og, float alpha){
        //draw shadow
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
        Image shadow = Art.bossBeholder64x64[0][2];
        Image fadedShadow = ImageUtils.fadeImage(shadow, null, (maxAltitude-altitude)/(float)maxAltitude);
        og.drawImage(fadedShadow, xPixel - 32, yPixel + 16 - 64 + getYPicO(), null);

        super.render(og, alpha);
    }

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        modified.getAttributes().remove(Attribute.Freeze);
        modified.getAttributes().remove(Attribute.Drown);
        modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        //modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }
}

package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.projectile.OrbeholderLaser;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BigBeholder extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "BigBeholder";

    private int enterTime = 0, maxEnterTime = 64;
    private int regenerateTime = 0, maxRegenerateTime = 16;
    private int shootTime = 0, maxShootTime = 32;
    private int damageTime = 0, maxDamageTime = 16;
    private int numOrbiters = 0, maxNumOrbiters = 8;
    private int blinkTime = 0, maxBlinkTime = 3;
    private int lastOrbiterDeathTick = 0, maxLastOrbiterDeathTick = 128;
    private Orbeholder[] orbiters;

    public BigBeholder(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.bossBeholder64x64);
        setWPic(64);
        setHPic(64);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        setWidth(56);
        setHeight(56);
        enterTime = maxEnterTime;

        setHeading(headingTowardPlayer());
        setCanBeKnockedback(false);
        setShadowVisible(false);

        orbiters = new Orbeholder[maxNumOrbiters];

        setCollidable(false);

        setPowerupDrop(Powerups.AbilityGem);
        setAbilityDrop(Ability.Orbeholder);
    }

    public void calcPic(){
        int xOff = 0, yOff;
        double pi8 = Math.PI/4d;
        if (getHeading() < 2*pi8){
            xOff = 0;
            yOff = 0;
        }
        else if (getHeading() < 4*pi8){
            xOff = 0;
            yOff = 1;
        }
        else if (getHeading() <= 5*pi8){
            xOff = 1;
            yOff = 1;
        }
        else if (getHeading() <= 7*pi8){
            xOff = 1;
            yOff = 2;
        }
        else {
            xOff = 1;
            yOff = 0;
        }

        if (xOff == 1){
            if (damageTime > 0) xOff = 4;
            else if (blinkTime > 0) xOff = 1;
            else if (shootTime > 0) xOff = 3;
            else xOff = 2;
        }

        setXPic(xOff);
        setYPic(yOff);
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

    public float determineSpeed(){ return 1; }

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

        if (canAct && !isDead()){
            if (blinkTime > 0) blinkTime--;

            if (enterTime > 0){
                enterTime--;

                setFadeRatio((float)((double)(maxEnterTime-enterTime)/(double)maxEnterTime));

                double rand = Math.random();
                Color puffColor = Color.MAGENTA;
                if (rand < 0.25) puffColor = new Color(0.7f, 0f, 0.7f);
                else if (rand < 0.5) puffColor = new Color(0.5f, 0f, 0.7f);
                else if (rand < 0.75) puffColor = new Color(0.5f, 0f, 0.3f);
                puff(5, puffColor);

                if (enterTime == 0){
                    setCollidable(true);
                    regenerateTime = maxRegenerateTime;
                }
            }
            else if (damageTime > 0){
                damageTime--;
            }
            else if (regenerateTime > 0){
                regenerateTime--;

                setHeading(headingTowardPlayer());

                if (regenerateTime == 0){
                    getScene().getSound().play(Art.getSample("snotPlop.wav"), this, 1f, 1, 1f);
                    while(numOrbiters < maxNumOrbiters)
                        spawnOrbiter(false);
                }
            }
            else if (shootTime > 0){
                shootTime--;

                if (shootTime > 10)
                    setHeading(headingTowardPlayer());

                if (shootTime == 5){
                    getScene().addSprite(new OrbeholderLaser((int)getX(), (int)getY()-1+((int)(Math.random()*2)), headingTowardPlayer()-0.02+0.04f*Math.random(), this, true));
                    getScene().getSound().play(Art.getSample("bigLaser.wav"), this, 1f, 1, 1f);
                }

                if (shootTime == 0) regenerateTime = maxRegenerateTime;
            }
            else{
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)){
                    //stop moving and freeze heading
                    setXa(0);
                    setYa(0);
                    setMoving(false);
                    blinkTime = maxBlinkTime;
                }
                else{
                    setHeading(headingTowardPlayer());

                    if (blinkTime == 0 && Math.random() < 0.02)
                        blinkTime = maxBlinkTime;

                    if (distanceToPlayer() < 64) setHeading(headingTowardPlayer()+Math.PI/2d);
                    setSpeed(1);
                    setMoving(true);

                    if (getTick()%32 == 0 && getHp() < getMaxHp()/2){
                        fireOrbiters();
                    }

                    if (lastOrbiterDeathTick > 0){
                        lastOrbiterDeathTick--;
                        if (lastOrbiterDeathTick == 0 && numOrbiters < maxNumOrbiters){
                            spawnOrbiter(true);
                            lastOrbiterDeathTick = maxLastOrbiterDeathTick;
                        }
                    }
                }
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }
        super.move();

        calcPic();
    }

    private void spawnOrbiter(boolean green){
        //find earliest unoccupied index
        int index = 0;
        for (int i = orbiters.length-1; i >= 0; i--)
            if (orbiters[i] == null)
                index = i;

        //create spawn and record in array, add to scene
        Orbeholder spawn = new Orbeholder(this, index, green);
        orbiters[index] = spawn;
        getScene().addSprite(spawn);

        numOrbiters++;
    }

    private void fireOrbiters(){
        Map<Orbeholder, Double> distances = new HashMap<Orbeholder, Double>();
        for (Orbeholder orbiter : orbiters)
            if (orbiter != null)
                distances.put(orbiter, Point2D.distance(orbiter.getX(), orbiter.getY(), getScene().player.getX(), getScene().player.getY()));

        java.util.List<Orbeholder> toFire = new ArrayList<Orbeholder>();
        for (int i = 0; i < 4; i++){
            double min = Double.MAX_VALUE;
            Orbeholder closest = null;
            for (Orbeholder orbiter: distances.keySet()){
                if (distances.get(orbiter) < min){
                    min = distances.get(orbiter);
                    closest = orbiter;
                }
            }

            if (closest != null){
                toFire.add(closest);
                distances.remove(closest);
            }
        }

        for (Orbeholder orbiter: toFire)
            if (orbiter != null)
                orbiter.shoot();
    }


    public void orbiterDestroyed(Prop orbiter){
        int index = 0;
        for (int i = 0; i < orbiters.length; i++)
            if (orbiters[i] == orbiter)
                index = i;

        orbiters[index] = null;

        numOrbiters--;
        lastOrbiterDeathTick = maxLastOrbiterDeathTick;

        if (numOrbiters == 0) shootTime = maxShootTime;
    }

    public int getDamageTime(){ return damageTime; }
    public int getMaxNumOrbiters(){ return maxNumOrbiters; }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        modified.getAttributes().remove(Attribute.Freeze);
        //modified.getAttributes().remove(Attribute.Drown);
        modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }

    public void die(){
        super.die();
        for (int i = 0; i < maxNumOrbiters; i++)
            if (orbiters[i] != null)
                orbiters[i].die();
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4f, 1, 1f);
    }

    public void render(Graphics2D og, float alpha){
        //white "charging" outline
        if (shootTime > 5)
            setTintColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.ORANGE, Color.WHITE}, 3));
        if (regenerateTime > 0)
            setTintColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.GREEN, ImageUtils.TRANSPARENT}, 8));

        //draw shadow
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
        Image shadow = Art.bossBeholder64x64[0][2];
        og.drawImage(shadow, xPixel-32, yPixel+16-64, null);

        super.render(og, alpha);
    }

}

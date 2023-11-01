package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.projectile.Boulder;
import com.oblong.af.sprite.projectile.TornEarth;
import com.oblong.af.sprite.thing.FlameBasin;
import com.oblong.af.sprite.thing.RockColumn;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Vampire chick starts out with 'dead' graphic; when player gets close, she gets up and does lots of Earth Rends.  From
 *   there on, she glides around, clawing the player if she gets close enough.  If not, she sometimes releases ice bats
 *   or Earth Rends or waves of Earth Fingers.  At half HP she starts doing Quakes, with boulders dropping from ceiling,
 *   earth rends and waves of fingers all at once.
 * Ice bats released are drawn to any lit sunstone.  Vampire is only damageable when near a sunstone.
 */

public class Vampire extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "Vampire";

    private boolean fakingDead = true, invulnerable = true;
    private int standingUpTime = 0, maxStandingUpTime = 8;
    private int clawTime = 0, maxClawTime = 16;
    private int teleportTime = 0, maxTeleportTime = 4;
    private int quakeTime = 0, maxQuakeTime = 32;
    private int summonBatTime = 0, maxSummonBatTime = 16;
    private int earthRendTime = 0, maxEarthRendTime = 32;
    private int earthFingersTime = 0, maxEarthFingersTime = 16;
    private int damageTime = 0, maxDamageTime = 8;
    private int tx, ty;

    public Vampire(AreaScene scene) {
        super("Vampire", scene, 0, 0);
        setSheet(Art.bossVampire48x48);
        setWPic(48);
        setHPic(48);
        setXPicO(0);
        setYPicO(0);

        setShadowVisible(false);
        setCanBeKnockedback(true);

        setPowerupDrop(Powerups.AbilityGem);
        setAbilityDrop(Ability.Quake);
    }

    public void calcPic(){
        int xOff = 0, yOff;
        switch(getFacing()){
            case LEFT: yOff = 3; break;
            case RIGHT: yOff = 2; break;
            case UP: yOff = 1; break;
            case DOWN: default: yOff = 0; break;
        }

        if (isDead() || fakingDead){
            xOff = 6;
            yOff = 4;
        }
        else if (damageTime > 0){
            xOff = 5;
            yOff = 4;
        }
        if (standingUpTime > 0){
            if (standingUpTime > maxStandingUpTime/2) xOff = 4;
            else xOff = 3;
            yOff = 4;
        }
        else if (clawTime > 0){
            if (clawTime > maxClawTime-2) xOff = 2;
            else xOff = 3+(getTick()/2)%4;
        }
        else if (quakeTime > 0){
            xOff = 1;
            yOff = 4;
        }
        else if (summonBatTime > 0){
            xOff = 2;
            yOff = 4;
        }
        else if (earthRendTime > 0){
            xOff = 0;
            yOff = 4;
        }
        else if (earthFingersTime > 0){
            xOff = 0;
            yOff = 4;
        }

        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (!canAct){
            xOff = 5;
            yOff = 4;
        }

        setXPic(xOff);
        setYPic(yOff);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4, 1, 1);
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
    }

    protected float determineSpeed(){
        if (clawTime > 0) return 6;
        return 2;
    }

    private double headingTowardPlayer(){
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

        //figure list of lit torches
        java.util.List<FlameBasin> litTorches = new ArrayList<FlameBasin>();
        for (Sprite sprite: getScene().getSprites())
            if (sprite.isActive() && sprite instanceof FlameBasin)
                litTorches.add((FlameBasin)sprite);

        //invulnerable unless close to a lit torch
        boolean tooClose = false;
        for (Prop torch: litTorches){
            double tdist = Point.distance(getX(), getY(), torch.getX(), torch.getY());
            if (tdist < 64) tooClose = true;
        }
        invulnerable = !tooClose;

        if (invulnerable && !fakingDead)
            setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{ Color.RED, Color.RED.darker()}, 10));
        else setOutlineColor(null);

        if (canAct){
            if (!isDead()){
                //regenerate over time
                if (getHp() < getMaxHp() && getTick()%16 == 0){
                    damage(new DamageAttributes(1, Arrays.asList(Attribute.Heal)));
                }

                //distance to player
                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;

                if (fakingDead){
                    setMoving(false);
                    if (distance < 64){
                        fakingDead = false;
                        standingUpTime = maxStandingUpTime;
                    }
                }
                else if (damageTime > 0){
                    damageTime--;
                    setMoving(false);
                }
                else if (standingUpTime > 0){
                    standingUpTime--;
                    setMoving(false);
                    if (standingUpTime == 0)
                        earthFingersTime = maxEarthFingersTime;
                }
                else if (clawTime > 0){
                    clawTime--;

                    if (clawTime == maxClawTime-1)
                        getScene().getSound().play(Art.getSample("whiff.wav"), this, 1, 1, 1);

                    setHeading(headingTowardPlayer());
                    setMoving(true);
                    setImpactDamageAttributes(new DamageAttributes(4, Arrays.asList(Attribute.Physical)));
                    if (clawTime == 0){
                        setImpactDamageAttributes(null);
                        teleportTime = maxTeleportTime;
                    }
                }
                else if (teleportTime > 0){
                    teleportTime--;
                    setMoving(false);

                    setTintColor(Color.WHITE);

                    if (teleportTime == maxTeleportTime/2){
                        setX(tx);
                        setY(ty);
                        setXOld(tx);
                        setYOld(ty);

                        getScene().getSound().play(Art.getSample("toss.wav"), this, 1, 1, 1);
                    }

                    if (teleportTime == 0){
                        setTintColor(null);
                    }
                }
                else if (quakeTime > 0){
                    quakeTime--;
                    setMoving(false);

                    if (quakeTime%4 == 0)
                        getScene().addSprite(new Boulder(getScene(), (int)getScene().player.getX(), (int)getScene().player.getY()));
                    if (quakeTime%2 == 0)
                        getScene().addSprite(TornEarth.createEarthRend(getScene(), this, Math.random()*2*Math.PI));
                    if (quakeTime%4 == 0){
                        int radius = 32+(maxQuakeTime-quakeTime)*16;
                        for (int i = 0; i < 5; i++){
                            double h = Math.toRadians(i*72);
                            double x = getX()+(float)(Math.cos(h)*radius);
                            double y = getY()-(float)(Math.sin(h)*radius);
                            getScene().addSprite(new RockColumn(getScene(), (int)x, (int)y));
                        }
                    }
                }
                else if (summonBatTime > 0){
                    summonBatTime--;
                    setMoving(false);

                    if (summonBatTime == maxSummonBatTime/4){
                        VampireBat bat1 = new VampireBat("VampireBat", getScene(), 0, 0);
                        bat1.setX(getX()-32);
                        bat1.setY(getY());
                        VampireBat bat2 = new VampireBat("VampireBat", getScene(), 0, 0);
                        bat2.setX(getX()+32);
                        bat2.setY(getY());
                        getScene().addSprite(bat1);
                        getScene().addSprite(bat2);

                        getScene().getSound().play(Art.getSample("rat.wav"), this, 1, 1, 1);
                    }
                }
                else if (earthRendTime > 0){
                    earthRendTime--;
                    setMoving(false);

                    if (earthRendTime%2 == 0)
                        getScene().addSprite(TornEarth.createEarthRend(getScene(), this, Math.random()*2*Math.PI));
                }
                else if (earthFingersTime > 0){
                    earthFingersTime--;
                    setMoving(false);

                    if (earthFingersTime%4 == 0){
                        int radius = (maxEarthFingersTime-earthFingersTime)*4;
                        double circ = Math.PI*2*radius;
                        int numFingers = (int)(circ/32d);
                        double angleInc = Math.PI*2d/(double)numFingers;
                        for (int i = 0; i < numFingers; i++){
                            double h = i*angleInc;
                            double x = getX()+(float)(Math.cos(h)*radius);
                            double y = getY()-(float)(Math.sin(h)*radius);
                            getScene().addSprite(new RockColumn(getScene(), (int)x, (int)y));
                        }
                    }
                }
                else{
                    //if close do claws
                    //if there are lit torches, release bats
                    //otherwise randomly select rend, fingers, and if less than hp maybe quake
                    if (distance < 64){
                        clawTime = maxClawTime;
                        tx = (int)getX();
                        ty = (int)getY();
                    }
                    else if (Math.random() < 0.02){
                        if (litTorches.size() > 0) summonBatTime = maxSummonBatTime;
                        else{
                            double r = Math.random();
                            if (r < 0.1) earthFingersTime = maxEarthFingersTime;
                            else if (r < 0.2) earthRendTime = maxEarthRendTime;
                            else if (r < 0.3 && getHp() < getMaxHp()/2) quakeTime = maxQuakeTime;
                        }
                    }
                    else{
                        if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible))
                            setHeading(getHeading()-0.01+Math.random()*0.02);
                        else setHeading(headingTowardPlayer());
                        setMoving(true);
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

    public void damage(DamageAttributes attributes){
        if (invulnerable) return;

        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        //modified.getAttributes().remove(Attribute.Freeze);
        modified.getAttributes().remove(Attribute.Drown);
        modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }
}

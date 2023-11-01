package com.oblong.af.sprite.enemy;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.FadingImageEffect;
import com.oblong.af.sprite.projectile.HomingBolt;
import com.oblong.af.sprite.projectile.TornEarth;
import com.oblong.af.sprite.projectile.TornadoFlame;
import com.oblong.af.sprite.projectile.TornadoGust;
import com.oblong.af.sprite.thing.PetrifyCloud;
import com.oblong.af.sprite.thing.RockColumn;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Nemesis is darkened version of player; uses homing bolts, quake, firestorm, freeze, tornado, and petrify breath;
 *   occasionally teleports around like vampire.  When player is petrified, drains their HP.
 */

public class Nemesis extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "Nemesis";

    private int teleportTime = 0, maxTeleportTime = 4;
    private int quakeTime = 0, maxQuakeTime = 32;
    private int firestormTime = 0, maxFirestormTime = 32;
    private int freezeTime = 0, maxFreezeTime = 32;
    private int tornadoTime = 0, maxTornadoTime = 32;
    private int homingBoltTime = 0, maxHomingBoltTime = 32;
    private int petrifyBreathTime = 0, maxPetrifyBreathTime = 32;
    private int damageTime = 0, maxDamageTime = 8;
    private int tx, ty;
    private ArrayList<Point2D.Double> teleportDestinations;

    public Nemesis(AreaScene scene) {
        super("Nemesis", scene, 0, 0);
        setSheet(Art.characters);
        setWPic(16);
        setHPic(32);
        setXPicO(0);
        setYPicO(0);
        setWidth(16);
        setHeight(32);

        setShadowVisible(false);
        setCanBeKnockedback(true);
        setImmuneToStatusEffects(true);

        if (getScene() != null && getScene().player != null){
            setOxPic(getScene().player.getOxPic());
            setOyPic(getScene().player.getOyPic());
        }

        teleportDestinations = new ArrayList<Point2D.Double>();
    }

    public void die(){
        super.die();
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4, 1, 1);
    }

    protected void calcPic(){
        int xOff, yOff = 0;

        switch(getFacing()){
            case LEFT: xOff = 2; break;
            case RIGHT: xOff = 3; break;
            case UP: xOff = 1; break;
            case DOWN: xOff = 0; break;
            default: xOff = 0; break;
        }

        int runFrame = ((int) (getRunTime()/4)) % 4;
        if (runFrame == 0 || runFrame == 2) yOff = 0;
        else if (runFrame == 1) yOff = 1;
        else if (runFrame == 3) yOff = 2;

        if (!isMoving()) yOff = 0;

        if (isDead()){
            xOff = 5;
            yOff = 1;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
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
        setTintColor(Color.BLACK);

        if (!isDead()){
            //distance to player
            double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
            if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;

            //if player is petrified, regain health
            if (getScene().player.hasStatusEffect(StatusEffect.Petrified) && getTick()%16 == 0){
                damage(new DamageAttributes(1, Arrays.asList(Attribute.Heal)));
            }

            if (damageTime > 0){
                damageTime--;
                setMoving(false);
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
                }

                if (teleportTime == 0){
                    setTintColor(null);
                }
            }
            else if (quakeTime > 0){
                quakeTime--;
                setMoving(false);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.GREEN, new Color(0f, 0.3f, 0f)}, 32));

                if (quakeTime%4 == 0)
                    getScene().addSprite(TornEarth.createEarthRend(getScene(), this, Math.random()*2*Math.PI));
                if (quakeTime%4 == 0){
                    int radius = 32+(maxQuakeTime-quakeTime)*16;
                    for (int i = 0; i < 3; i++){
                        double h = Math.toRadians(i*72);
                        double x = getX()+(float)(Math.cos(h)*radius);
                        double y = getY()-(float)(Math.sin(h)*radius);
                        getScene().addSprite(new RockColumn(getScene(), (int)x, (int)y));
                    }
                }
            }
            else if (firestormTime > 0){
                firestormTime--;
                setMoving(false);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.RED, new Color(0.3f, 0f, 0f)}, 32));
                if (firestormTime == 0){
                    for (int i = 0; i < 20; i++)
                        getScene().addSprite(new TornadoFlame(getScene(), this));
                }
            }
            else if (freezeTime > 0){
                freezeTime--;
                setMoving(false);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.CYAN, new Color(0.2f, 0.2f, 0.5f)}, 32));
                if (freezeTime == 0){
                    //freeze any props nearby
                    for (Prop prop: getScene().getDamageablePropsWithinRange((int) getX(), (int) getY(), 64)){
                        if (prop == this) continue;
                        prop.damage(new DamageAttributes(5, new ArrayList<Attribute>(Arrays.asList(Attribute.Freeze))));
                    }

                    //freeze any water nearby
                    int px = (int)(getX()/16), py = (int)(getY()/16);
                    for (int i = px-5; i <= px+5; i++)
                        for (int j = py-5; j <= py+5; j++)
                            if (Point2D.distance(px, py, i, j) < 4){
                                Block b = getScene().areaGroup.getCurrentArea().getBlock(i, j, Area.Layer.Main);
                                if (getScene().areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Water)){
                                    b.dFreeze = 1;
                                    b.electrifiedCounter = 0;
                                }
                            }

                    //add a bunch of ice effects
                    for (int i = 0; i < 16; i++){
                        double r = Math.random()*64;
                        double h = Math.random()*Math.PI*2;
                        int x = (int)(getX()+(float)(Math.cos(h)*r));
                        int y = (int)(getY()-(float)(Math.sin(h)*r));
                        getScene().addSprite(new FadingImageEffect(getScene(), x, y, false, 3, 0));
                    }
                }
            }
            else if (tornadoTime > 0){
                tornadoTime--;
                setMoving(false);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.YELLOW, new Color(0.3f, 0.3f, 0f)}, 32));
                getScene().addSprite(new TornadoGust(getScene(), this));
            }
            else if (homingBoltTime > 0){
                homingBoltTime--;
                setMoving(false);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.WHITE, new Color(0.3f, 0.3f, 0.3f)}, 32));

                if (homingBoltTime == 0){
                    getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()-1f));
                    getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()));
                    getScene().addSprite(new HomingBolt(getScene(), this, getScene().player, getHeading()+1f));
                }
            }
            //if real close, teleport away
            else if (distance < 32 && teleportDestinations.size() > 0){
                Point2D.Double td = teleportDestinations.get((int) (Math.random() * teleportDestinations.size()));
                tx = (int)td.x;
                ty = (int)td.y;
                teleportTime = maxTeleportTime;
            }
            else if (petrifyBreathTime > 0){
                petrifyBreathTime--;
                setMoving(false);
                setHeading(headingTowardPlayer());

                if (petrifyBreathTime %2 == 0){
                    PetrifyCloud spurt = new PetrifyCloud(getScene(), 48, this, true);
                    spurt.setHeading(getHeading()-Math.PI/8f+Math.random()*Math.PI/4f);
                    spurt.setSpeed(7f);
                    spurt.setMoving(true);
                    spurt.setX(getX()+(float)(Math.cos(spurt.getHeading())*16));
                    spurt.setY(getY()-(float)(Math.sin(spurt.getHeading())*16));
                    getScene().addSprite(spurt);
                }
            }
            //if only kind of close, do petrify breath
            else if (distance < 64){
                petrifyBreathTime = maxPetrifyBreathTime;
            }
            //else randomly select between other abilities
            else if (Math.random() < 0.02){
                double r = Math.random();
                if (r < 0.2) firestormTime = maxFirestormTime;
                else if (r < 0.4) freezeTime = maxFreezeTime;
                else if (r < 0.6) quakeTime = maxQuakeTime;
                else if (r < 0.8) tornadoTime = maxTornadoTime;
                else homingBoltTime = maxHomingBoltTime;
            }
            //otherwise move around
            else{
                setMoving(true);
                setSpeed(4);
                setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.MAGENTA, new Color(0.3f, 0f, 0.3f)}, 32));

                if (getTick() % 64 == 0) teleportDestinations.add(new Point2D.Double(getX(), getY()));
                if (teleportDestinations.size() > 8) teleportDestinations.remove((int)(Math.random()*teleportDestinations.size()));

                setHeading(headingTowardPlayer()+1.5*Math.random());
            }
        }

        super.move();

        calcPic();
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

}

package com.oblong.af.sprite.projectile;

import com.mojang.sonar.sample.SonarSample;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

public class TailedBolt extends Prop {

    public static TailedBolt createSpikehogSpine(AreaScene scene, Prop parent, double heading){
        return new TailedBolt("SpikehogSpine", scene, parent, heading, 0, 16, new DamageAttributes(1, Arrays.asList(Attribute.Physical)), 128, Art.getSample("whiff.wav")){
            protected void calcPic(){} //noop'd
            protected void twinkle(int numSparkles){}
        };
    }

    public static TailedBolt createPetrifyingBolt(final AreaScene scene, final Prop parent, double heading){
        return new TailedBolt("petrifying bolt", scene, parent, heading, 0, 6, new DamageAttributes(3, Arrays.asList(Attribute.Petrify)), 128, Art.getSample("drone.wav")){
            protected void twinkle(int numSparkles){
                for (int i = 0; i < numSparkles; i++){
                    int degree = (int)(Math.random()*360);
                    int distance = (int)(Math.random()*16);
                    int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
                    int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
                    scene.addSprite(new Puff(scene,
                            (int) (getX() + getWidth() / 2 + xOffset), (int) (getY() - getHeight() / 2 + yOffset),
                            Color.GRAY));
                }
            }
        };
    }

    public static TailedBolt createFreezingBolt(AreaScene scene, Prop parent, double heading){
        return new TailedBolt("freezing bolt", scene, parent, heading, 0, 7, new DamageAttributes(1, Arrays.asList(Attribute.Freeze)), 128, Art.getSample("freeze.wav"));
    }

    public static TailedBolt createDeathBolt(final AreaScene scene, Prop parent, double heading){
        TailedBolt bolt = new TailedBolt("death bolt", scene, parent, heading, 0, 8, new DamageAttributes(1, Arrays.asList(Attribute.Death)), 128, Art.getSample("deathbolt.wav")){
            protected void twinkle(int numSparkles){
                for (int i = 0; i < numSparkles; i++){
                    int degree = (int)(Math.random()*360);
                    int distance = (int)(Math.random()*16);
                    int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
                    int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
                    scene.addSprite(new Puff(scene,
                            (int) (getX() + getWidth() / 2 + xOffset), (int) (getY() - getHeight() / 2 + yOffset),
                            Color.MAGENTA));
                }
            }
        };
        if (parent instanceof Player){
            int affinityCount = scene.getGameState().getSpiritAffinity()+scene.getGameState().getFireAffinity()+
                    scene.getGameState().getWaterAffinity()+scene.getGameState().getAirAffinity()+scene.getGameState().getEarthAffinity();
            bolt.getImpactDamageAttributes().setDamage(affinityCount);
        }
        return bolt;
    }

    private Prop parent;
    private int timeToLive;
    private SonarSample fireSound;

	public TailedBolt(String id, AreaScene scene, Prop parent, double heading, int xPic, int yPic,
                      DamageAttributes impactDamageAttributes, int timeToLive, SonarSample fireSound){
		super(id, scene, xPic, yPic);
        setSheet(Art.effects32x32);
        setXPic(xPic);
        setYPic(yPic);
        setXPicO(0);
        setYPicO(0);
        setWPic(32);
        setHPic(32);
        setSpeed(8);
        setTimeToLive(timeToLive);
		setWidth(8);
		setHeight(8);
        setHeading(heading);
        setFacing(Facing.nearestFacing(heading));
        setParent(parent);
		setLayer(parent.getLayer());
		setMoving(true);
        setRenderingOrder(9);
        setBlockableByScreenEdge(false);
        setFlying(true);
        setBlockable(true);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setCanBeKnockedback(false);
        setDiesOnCollide(true);
        setImpactDamageAttributes(impactDamageAttributes);

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));

        this.fireSound = fireSound;
	}

    protected void calcPic(){
    	int xPic = (getTick()/2)%4;
        setXPic(xPic);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) getScene().removeSprite(this);
        if (Math.random() < 0.5f) twinkle(1);

        if (getTick() == 1 && fireSound != null)
            getScene().getSound().play(fireSound, this, 1, 1, 1);

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);

        twinkle(20);
    }

    protected void twinkle(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            int xa = xOffset/6;
            int ya = -(int)(Math.random()*3);
            getScene().addSprite(new Sparkle(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset),
                    Color.WHITE, xa, ya));
        }
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Rectangle imageBounds = getImageFootprint(alpha);
        Image image = getSheet()[getXPic()][getYPic()];
        image = ImageUtils.rotateImage(image, null, -getHeading()+Math.PI/2d); //the entire point; rotate the image
        og.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);

        if (AreaGroupRenderer.renderBehaviors){
            Rectangle fp = getFootprint();
            if (fp != null){
                og.setColor(new Color(1f, 0f, 0f, 0.3f));
                if (!isBlocksFlying()) og.setColor(new Color(0f, 1f, 0.5f, 0.3f));
                og.fill(fp);
                og.setColor(Color.RED);
                if (!isBlocksFlying()) og.setColor(Color.CYAN);
                og.draw(fp);

                //render heading
                if (getXa() != 0 || getYa() != 0){
                    double f = 5;
                    og.drawLine((int)fp.getCenterX(), (int)fp.getCenterY(), (int)(fp.getCenterX()+getXa()*f), (int)(fp.getCenterY()+getYa()*f));
                }
            }
        }
    }

    public int getTimeToLive(){ return timeToLive; }
	public void setTimeToLive(int timeToLive){ this.timeToLive = timeToLive; }

	public Prop getParent(){ return parent; }
	public void setParent(Prop parent){ this.parent = parent; }

}

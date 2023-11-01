package com.oblong.af.sprite.projectile;

import com.mojang.sonar.sample.SonarSample;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

public class ThrownWeapon extends Prop {

    public static ThrownWeapon createThrowingKnife(AreaScene scene, Prop parent, double heading){
        return new ThrownWeapon("thowing knife", scene, parent, heading, 0, 0, new DamageAttributes(1, Arrays.asList(Attribute.Physical)), 128, Art.getSample("whiff.wav"));
    }

    public static ThrownWeapon createBomb(AreaScene scene, Prop parent, double heading){
        ThrownWeapon bomb = new ThrownWeapon("bomb", scene, parent, heading, 0, 1, new DamageAttributes(10, Arrays.asList(Attribute.Physical, Attribute.Fire)), 16, Art.getSample("toss.wav"));
        bomb.explodes = true;
        return bomb;
    }

    public static ThrownWeapon createThrowingAxe(AreaScene scene, Prop parent, double heading){
        return new ThrownWeapon("throwing axe", scene, parent, heading, 0, 2, new DamageAttributes(2, Arrays.asList(Attribute.Physical)), 128, Art.getSample("whiff.wav"));
    }

    private Prop parent;
    private int timeToLive;
    private boolean explodes = false;
    private SonarSample fireSound;

	public ThrownWeapon(String id, AreaScene scene, Prop parent, double heading, int xPic, int yPic,
                        DamageAttributes impactDamageAttributes, int timeToLive, SonarSample fireSound){
		super(id, scene, xPic, yPic);
        setSheet(Art.weapons16x16);
        setXPic(xPic);
        setYPic(yPic);
        setXPicO(0);
        setYPicO(0);
        setWPic(16);
        setHPic(16);
        setSpeed(8);
        setTimeToLive(timeToLive);
		setWidth(16);
		setHeight(16);
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

        if (!explodes) setYPicO(8);
	}

    protected void calcPic(){
    	int xPic = getTick()%4;
        if (getHeading() > Math.PI) xPic = 3-xPic;
        setXPic(xPic);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) die();
        if (explodes){
            float h = timeToLive;
            float x = (getTick()-timeToLive/2f)/2f;
            float y = (float)(h-Math.pow(x, 2));
            setYPicO((int)y);
        }

        if (getTick() == 1)
            getScene().getSound().play(fireSound, this, 1, 1, 1);

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);

        if (explodes){
            getScene().addSprite(new ExplodeEffect(getScene(), (int)(getX()), (int)getY()));
            for (Prop target: getScene().getDamageablePropsWithinRange((int) getX(), (int) getY(), 32))
                target.damage(getImpactDamageAttributes());
        }
    }

    public void render(Graphics2D og, float alpha){
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha);
        og.drawImage(getSheet()[3][3], xPixel-8, yPixel-16, null);

        super.render(og, alpha);
    }

    public int getTimeToLive(){ return timeToLive; }
	public void setTimeToLive(int timeToLive){ this.timeToLive = timeToLive; }

	public Prop getParent(){ return parent; }
	public void setParent(Prop parent){ this.parent = parent; }

}

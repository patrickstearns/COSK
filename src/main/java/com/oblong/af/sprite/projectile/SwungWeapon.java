package com.oblong.af.sprite.projectile;

import com.mojang.sonar.sample.SonarSample;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

public class SwungWeapon extends Prop {

    public static SwungWeapon createStaff(AreaScene scene, Prop parent, double heading){
        return new SwungWeapon("staff", scene, parent, heading, 0, 0, new DamageAttributes(1, Arrays.asList(Attribute.Physical)), 4, Art.getSample("weaponWhiff.wav"));
    }

    public static SwungWeapon createSword(AreaScene scene, Prop parent, double heading){
        return new SwungWeapon("sword", scene, parent, heading, 0, 1, new DamageAttributes(2, Arrays.asList(Attribute.Physical)), 4, Art.getSample("weaponWhiff.wav"));
    }

    public static SwungWeapon createMace(AreaScene scene, Prop parent, double heading){
        return new SwungWeapon("mace", scene, parent, heading, 0, 2, new DamageAttributes(2, Arrays.asList(Attribute.Physical, Attribute.Knockback)), 6, Art.getSample("weaponWhiff.wav"));
    }

    public static SwungWeapon createAxe(AreaScene scene, Prop parent, double heading){
        return new SwungWeapon("axe", scene, parent, heading, 0, 3, new DamageAttributes(3, Arrays.asList(Attribute.Physical, Attribute.Knockback)), 8, Art.getSample("weaponWhiff.wav"));
    }

    private Prop parent;
    private int timeToLive;
    private SonarSample fireSound;

	public SwungWeapon(String id, AreaScene scene, Prop parent, double heading, int xPic, int yPic,
                       DamageAttributes impactDamageAttributes, int timeToLive, SonarSample fireSound){
		super(id, scene, xPic, yPic);
        setSheet(Art.weapons32x32);
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
        setDiesOnCollide(false);
        setImpactDamageAttributes(impactDamageAttributes);

        this.fireSound = fireSound;

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    protected void calcPic(){
        setXPic((int)(4*((float)getTick()/(float)timeToLive)));
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) die();

        setHeading(parent.getHeading());
        setX(getParent().getX()+(float)(Math.cos(getHeading())*16));
        setY(getParent().getY()-(float)(Math.sin(getHeading())*16));

        if (getTick() == 1 && fireSound != null)
            getScene().getSound().play(fireSound, this, 1, 1, 1);

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
    }

    private void poof(int numSparkles){
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

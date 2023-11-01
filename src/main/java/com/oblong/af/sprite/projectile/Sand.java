package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class Sand extends Prop {

    private Prop parent;
    private int timeToLive, tickOffset;
    private float lastX, lastY;

	public Sand(AreaScene scene, Prop parent, double heading){
		super("sand", scene, 0, 1);

        setSheet(Art.projectiles);
        setWidth(4);
        setHeight(4);
        setOxPic(0);
        setOyPic(1);
        setXPic(0);
        setYPic(1);
        setXPicO(0);
        setYPicO(0);
        setWPic(8);
        setHPic(8);
        setRenderingOrder(10);
        setSpeed(8);
        setTimeToLive(8);
        setHeading(heading);
        setFacing(Facing.nearestFacing(heading));
        setParent(parent);
		setLayer(parent.getLayer());
		setMoving(true);
        setBlockableByScreenEdge(false);
        setFlying(true);
        setBlockable(true);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setCanBeKnockedback(false);
        setDiesOnCollide(true);
        setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Earth, Attribute.Wind, Attribute.Knockback)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));

        tickOffset = (int)(Math.random()*21);
        lastX = getX();
        lastY = getY();
	}

    protected void calcPic(){
    	int xPic = ((getTick()+tickOffset)/2)%4;
        setXPic(xPic);
    }

    public void move(){
        super.move();
        if (timeToLive-- < 0) getScene().removeSprite(this);
        lastX = getX();
        lastY = getY();

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("whiff.wav"), this, 1, 1, 1);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
        poof(4);
    }

    private void poof(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            int xa = xOffset/6;
            int ya = -(int)(Math.random()*3);
            Sparkle sparkle = new Sparkle(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset),
                    Color.WHITE, xa, ya);
            sparkle.setYPic(1);
            getScene().addSprite(sparkle);
        }
    }

    public void render(Graphics2D g, float alpha){
        //for each drop, draw a line from drops[i] to lastDrops[i]
        int yOffset = -12; //so it looks like it's coming from mouth
        Point2D.Float a = new Point2D.Float(getX(), getY());
        Point2D.Float b = new Point2D.Float(lastX, lastY);

        double r = Math.random();
        Color color;
        if (r < 0.25) color = new Color(124, 68, 22);
        else if (r < 0.5) color = new Color(233, 195, 44);
        else if (r < 0.75) color = new Color(229, 157, 103);
        else color = new Color(225, 227, 176);

        g.setPaint(new GradientPaint(a.x, a.y+yOffset, color, b.x, b.y+yOffset, ImageUtils.TRANSPARENT));
        g.drawLine((int)a.x, (int)(a.y+yOffset), (int)b.x, (int)(b.y+yOffset));
    }

    public int getTimeToLive(){ return timeToLive; }
	public void setTimeToLive(int timeToLive){ this.timeToLive = timeToLive; }

	public Prop getParent(){ return parent; }
	public void setParent(Prop parent){ this.parent = parent; }

}

package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class Sparkler extends Prop {

    private Prop parent, target;
    private int timeToLive;

	public Sparkler(AreaScene scene, Prop parent, Prop target, double heading){
		super("sparkler", scene, 0, 11);
        setSheet(Art.effects16x16);
        setXPicO(0);
        setYPicO(0);
        setWPic(16);
        setHPic(16);
        setSpeed(8);
        setTimeToLive(2);
		setWidth(8);
		setHeight(8);
        setHeading(heading);
        setFacing(Facing.nearestFacing(heading));
        setParent(parent);
        setTarget(target);
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
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Fire)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    protected void calcPic(){
    	int xPic = (getTick()/2)%4, yPic = getOyPic();
        setXPic(xPic);
        setYPic(yPic);
    }

    private double headingTowardTarget(){
        double xDiff = target.getX()-getX();
        double yDiff = target.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (timeToLive-- < 0) getScene().removeSprite(this);

        //set target to be the closest damageable prop within 32 px who isn't our parent
        if (target == null){
            Prop targ = null;
            Double targDistance = null;
            for (Prop t: getScene().getDamageablePropsWithinRange((int)getX(), (int)getY(), 32)){
                if (t != parent){
                    double distance = Point2D.distance(getX(), getY(), t.getX(), t.getY());
                    if (targ == null || targDistance < distance){
                        targ = t;
                        targDistance = distance;
                    }
                }
            }
            target = targ;
        }

        if (target != null){
            double toTurn = 0d;
            double p = headingTowardTarget();
            double h = getHeading();
            if (p > h){
                if (p-h > h+Math.PI*2-p) toTurn = -1d;
                else toTurn = 1d;
            }
            else if (h > p){
                if (h-p > p+Math.PI*2-h) toTurn = 1d;
                else toTurn = -1d;
            }

            h += toTurn*0.2f;
            if (h > Math.PI*2) h -= Math.PI*2;
            if (h < 0) h += Math.PI*2;
            setHeading(h);
        }

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("lowfireburst.wav"), this, 1, 1, 1);

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);

        poof(3);
    }

    private void poof(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            getScene().addSprite(new Puff(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset),
                    Color.WHITE));
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

    public Prop getTarget(){ return target; }
    public void setTarget(Prop target){ this.target = target; }

}

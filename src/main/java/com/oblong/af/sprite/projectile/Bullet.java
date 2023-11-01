package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class Bullet extends Prop {

    private Prop parent;
    private Color color;
    private int timeToLive;

	public Bullet(AreaScene scene, Prop parent, double heading, Color color,
                  DamageAttributes impactDamageAttributes, int timeToLive){
		super("bullet", scene, 0, 0);
        setSpeed(24);
        setTimeToLive(timeToLive);
		setWidth(2);
		setHeight(2);
        setWPic(3);
        setHPic(3);
        setYPicO(8);
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

        this.color = color;
		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) getScene().removeSprite(this);
        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
        poof(5);
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

        int x1 = imageBounds.x+imageBounds.width/2, y1 = imageBounds.y-imageBounds.height/2;
        int x2 = (int)(x1-Math.cos(getHeading())*(getTick()*2+8)), y2 = (int)(y1+Math.sin(getHeading())*(getTick()*2+8));
        y1++; y1++; y2++; y2++;
        og.setPaint(new GradientPaint(x1, y1, color, x2, y2, ImageUtils.TRANSPARENT));
        og.drawLine(x1, y1, x2, y2);

        og.setColor(color);
        og.fillOval(imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height);

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

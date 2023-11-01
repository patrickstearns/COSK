package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

public class TornadoGust extends Prop {

    private Prop parent;
    private double initialAngle = Math.random()*Math.PI*2;
    private int timeToLive = 64;

	public TornadoGust(AreaScene scene, Prop parent){
		super("tornado gust", scene, 0, 4);
        setSheet(Art.effects32x32);
        setYPic(4);
        setXPicO(0);
        setYPicO(0);
        setWPic(32);
        setHPic(32);
        setSpeed(8);
		setWidth(8);
		setHeight(8);
        setParent(parent);
		setLayer(parent.getLayer());
		setMoving(true);
        setRenderingOrder(9);
        setBlockableByScreenEdge(false);
        setFlying(true);
        setBlockable(false);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setCanBeKnockedback(false);
        setDiesOnCollide(true);
        setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Wind, Attribute.Knockback)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    protected void calcPic(){
    	int xPic = (getTick()/2)%4, yPic = 4;
        setXPic(xPic);
        setYPic(yPic);
    }

    public void move(){
        timeToLive--;
        if (timeToLive == 0) die();

        //radius of turn / distance from parent = tick/2
        float radius = 16+getTick()/2f;

        //angle in circle around player is tick*something % 2*pi
        float angle = (float)(initialAngle+getTick()*0.5f);
        while (angle > 2*Math.PI) angle -= 2*Math.PI;

        //figure what new velocity and heading should be
        float xPos = (float)(parent.getX()+Math.cos(angle)*radius);
        float yPos = (float)(parent.getY()+Math.sin(angle)*radius);
        double heading = angle+Math.PI/2; //angle from center + 90 degrees

        //set velocity as difference between where we should be and where we are
        setX(xPos);
        setY(yPos);
        setHeading(heading);

        if (Math.random() < 0.1f) poof(1);

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("windLong.wav"), this, 1, 1, 1);
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

        poof(10);
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
        image = ImageUtils.rotateImage(image, null, getHeading()+Math.PI/2d); //the entire point; rotate the image
        image = ImageUtils.fadeImage(image, null, 0.5d);
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

	public Prop getParent(){ return parent; }
	public void setParent(Prop parent){ this.parent = parent; }

}

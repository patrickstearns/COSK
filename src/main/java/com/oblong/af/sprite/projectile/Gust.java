package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

public class Gust extends Prop {

    private Prop parent;
    private int timeToLive, tickOffset;

	public Gust(AreaScene scene, Prop parent, double heading){
		super("gust", scene, 0, 1);

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
        setImpactDamageAttributes(new DamageAttributes(0, Arrays.asList(Attribute.Wind, Attribute.Knockback)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*8));
		setY(parent.getY()-(float)(Math.sin(getHeading())*8));

        tickOffset = (int)(Math.random()*21);
	}

    protected void calcPic(){
    	int xPic = ((getTick()+tickOffset)/2)%4;
        setXPic(xPic);
    }

    public void move(){
        super.move();
        if (getTick() == 1) getScene().getSound().play(Art.getSample("wind.wav"), this, 1, 1, 1);
        if (timeToLive-- < 0) getScene().removeSprite(this);
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

//    public void render(Graphics2D og, float alpha){
//        if (!isVisible()) return;
//
//        Rectangle imageBounds = getImageFootprint(alpha);
//        Image image = getSheet()[getXPic()][getYPic()];
//        image = ImageUtils.rotateImage(image, null, -getHeading()+Math.PI/2d); //the entire point; rotate the image
//        og.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
//
//        if (AreaGroupRenderer.renderBehaviors){
//            Rectangle fp = getFootprint();
//            if (fp != null){
//                og.setColor(new Color(1f, 0f, 0f, 0.3f));
//                if (!isBlocksFlying()) og.setColor(new Color(0f, 1f, 0.5f, 0.3f));
//                og.fill(fp);
//                og.setColor(Color.RED);
//                if (!isBlocksFlying()) og.setColor(Color.CYAN);
//                og.draw(fp);
//
//                //render heading
//                if (getXa() != 0 || getYa() != 0){
//                    double f = 5;
//                    og.drawLine((int)fp.getCenterX(), (int)fp.getCenterY(), (int)(fp.getCenterX()+getXa()*f), (int)(fp.getCenterY()+getYa()*f));
//                }
//            }
//        }
//    }

    public int getTimeToLive(){ return timeToLive; }
	public void setTimeToLive(int timeToLive){ this.timeToLive = timeToLive; }

	public Prop getParent(){ return parent; }
	public void setParent(Prop parent){ this.parent = parent; }

}

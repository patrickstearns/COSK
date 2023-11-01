package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

public class StoneFist extends Prop {

    private Prop parent;
    private int timeToLive;

	public StoneFist(AreaScene scene, Prop parent, double heading){
		super("stonefist", scene, 1, 8);
        setSheet(Art.effects16x16);
        setXPic(1);
        setYPic(8);
        setXPicO(8);
        setYPicO(8);
        setWPic(16);
        setHPic(16);
        setSpeed(8);
        setTimeToLive(8);
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
        setImpactDamageAttributes(new DamageAttributes(4, Arrays.asList(Attribute.Earth, Attribute.Knockback)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*16)-8);
		setY(parent.getY()-(float)(Math.sin(getHeading())*16)-8);
	}

    protected void calcPic(){}

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) getScene().removeSprite(this);

        setXOld(getX());
        setYOld(getY());
        setX(parent.getX()+(float)(Math.cos(getHeading())*(16)));
        setY(parent.getY()-(float)(Math.sin(getHeading())*(16)));

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("weaponWhiff.wav"), this, 1, 1, 1);

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Rectangle imageBounds = getImageFootprint(alpha);
        Image image = getSheet()[getXPic()][getYPic()];
        image = ImageUtils.rotateImage(image, null, -getHeading()+Math.PI/2d); //the entire point; rotate the image
        og.drawImage(image, imageBounds.x+8, imageBounds.y+8, imageBounds.width, imageBounds.height, null);

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

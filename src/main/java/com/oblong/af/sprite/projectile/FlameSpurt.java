package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
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
import java.util.Arrays;

public class FlameSpurt extends Prop {

    private Prop parent;
    private int timeToLive;

	public FlameSpurt(AreaScene scene, Prop parent, double heading, int timeToLive ){
		super("flamespurt", scene, 0, 0);
        setSheet(Art.flame);
        setXPicO(0);
        setYPicO(0);
        setWPic(32);
        setHPic(32);
        setYPic(Math.random() > 0.5f ? 1 : 0);
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
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Fire)));

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    protected void calcPic(){
        setXPic((int) (((float) getTick() / (float) timeToLive)) * 6);
        if (getXPic() > 5)
            setXPic(5);
        if (getXPic() < 0)
            setXPic(0);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (getTick() >= timeToLive) getScene().removeSprite(this);
        if (Math.random() < 0.5f) poof(1);
        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("fireburst.wav"), this, 1, 1, 1);
        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
        poof(40);
    }

    private void poof(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            getScene().addSprite(new Puff(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset)));
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

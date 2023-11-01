package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.sprite.thing.FireField;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class ShieldFlame extends Prop {

    private Prop parent;
    private int index;
    private int timeToLive = 256;

	public ShieldFlame(AreaScene scene, Prop parent, int index){
		super("shield flame", scene, 0, 4);
        setSheet(Art.flame);
        setYPic(4);
        setXPicO(0);
        setYPicO(0);
        setWPic(48);
        setHPic(48);
        setSpeed(8);
		setWidth(48);
		setHeight(48);
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
        setDiesOnCollide(false);
        setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Fire)));
        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));

        setYPic(Math.random() < 0.5f ? 0 : 1);

        this.index = index;
	}

    protected void calcPic(){
    	int xPic = (getTick())%6;
        setXPic(xPic);
    }

    private Point2D.Double getIdealLocation(int radius){
        int period = 32;
        int maxOrbiters = 2;
        double orbitalIncrement = (2*Math.PI)/(double)maxOrbiters;
        double tickIncrement = (2*Math.PI)/(double)period;
        double tickMod = getTick()%period;
        double angle = index*orbitalIncrement+tickIncrement*tickMod;
        int xo = (int)(Math.cos(angle)*radius);
        int yo = (int)(Math.sin(angle)*radius);
        return new Point2D.Double(parent.getX()+xo, parent.getY()+16+yo);
    }

    private void moveTowardLocation(Point2D.Double location, double speed){
        double dx = location.x-getX(), dy = location.y-getY();
        double distance = Point2D.distance(location.x, location.y, getX(), getY());
        if (Math.abs(distance) > speed){
            double r = speed/distance;
            dx *= r;
            dy *= r;
        }
        setX((int) (getX() + dx));
        setY((int) (getY() + dy));
    }

    public void move(){
        timeToLive--;
        if (timeToLive == 0) die();

        int radius = 48;
        Point2D.Double idealLocation = getIdealLocation(radius);
        moveTowardLocation(idealLocation, 20);
        setHeading(headingTowardPlayer()); //so image rotates

        calcPic();

        poof((int) (Math.random() * 4));

        if (Math.random() < 0.5f) poof(1);
        if (Math.random() < 0.1f){
            FireField ff = new FireField("firefield", getScene(), 32, getParent(), true, 0);
            ff.setX(getX());
            ff.setY(getY());
            getScene().addSprite(ff);
        }

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("fireburst.wav"), this, 1, 1, 1);
        if (getTick()%90 == 0)
            getScene().getSound().play(Art.getSample("sizzle.wav"), this, 1, 1, 1);
    }

//    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
//        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void die(){
        poof(50);
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
            Puff puff = new Puff(getScene(), (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset));
            puff.setXa(getXa()/4f);
            puff.setYa(getYa()/4f);
            getScene().addSprite(puff);
        }
    }

    private double headingTowardPlayer(){
        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Rectangle imageBounds = getImageFootprint(alpha);
        Image image = getSheet()[getXPic()][getYPic()];
        image = ImageUtils.rotateImage(image, null, getHeading()+Math.PI/2d); //the entire point; rotate the image
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

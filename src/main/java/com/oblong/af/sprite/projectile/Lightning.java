package com.oblong.af.sprite.projectile;

import com.oblong.af.level.Area;
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

public class Lightning extends Prop {

    public static Lightning createLightning(AreaScene scene, Prop parent, double heading){
        float x = parent.getX()+(float)(Math.cos(heading)*16);
        float y = parent.getY()-(float)(Math.sin(heading)*16);
        return new Lightning(scene, parent, heading, x, y, 4, 0);
    }

    private Prop parent;
    private int timeToLive;
    private int index;
    private int maxIndex = 4;
    private int dead = -1;

    public Lightning(AreaScene scene, Prop parent, double heading, float x, float y, int timeToLive, int index){
        super("lightning", scene, 0, 12);
        setSheet(Art.effects16x16);
        setX(x);
        setY(y);
        setXPic(0);
        setYPic(9);
        setXPicO(0);
        setYPicO(0);
        setWPic(16);
        setHPic(16);
        setSpeed(0);
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
        setDiesOnCollide(false);
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Electric)));
        this.index = index;
        setLayer(Area.Layer.Main);
    }

    protected void calcPic(){
        int xPic, yPic = 12;
        if (isDead()){
            xPic = 3-dead;
            yPic = 13;
            if (xPic < 0) xPic = 0;
        }
        else if (getTick() < 2){
            xPic = getTick();
            if (index == maxIndex && xPic > 2) xPic = 2;
        }
        else if (index == maxIndex) xPic = 2;
        else xPic = 3;
        setXPic(xPic);
        setYPic(yPic);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void die(){
        dead = 3;
        setDead(true);
    }

    public void move(){
        if (isDead()){
            dead--;
            if (dead < 0) getScene().removeSprite(this);
        }
        if (getTick() >= timeToLive && !isDead()) die();

        if (index < maxIndex && getTick() == 1){
            double h = getHeading();
            h = h-0.3d+0.6d*Math.random();
            float nx = getX()+(float)(Math.cos(h)*12);
            float ny = getY()-(float)(Math.sin(h)*12);
            getScene().addSprite(new Lightning(getScene(), parent, h, nx, ny, timeToLive-1, index+1));
        }

        super.move();
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Rectangle imageBounds = getImageFootprint(alpha);
        Image image = getSheet()[getXPic()][getYPic()];
        image = ImageUtils.rotateImage(image, null, -getHeading() + Math.PI / 2d); //the entire point; rotate the image
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

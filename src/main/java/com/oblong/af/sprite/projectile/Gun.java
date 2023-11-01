package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class Gun extends Prop {

    public static Gun createGun(AreaScene scene, Prop parent, double heading){
        return new Gun("gun", scene, parent, heading, 0, 3, 4);
    }

    private Prop parent;
    private int timeToLive;

	public Gun(String id, AreaScene scene, Prop parent, double heading, int xPic, int yPic, int timeToLive){
		super(id, scene, xPic, yPic);
        setSheet(Art.weapons16x16);
        setXPic(xPic);
        setYPic(yPic);
        setXPicO(0);
        setYPicO(0);
        setWPic(16);
        setHPic(16);
        setSpeed(8);
        setTimeToLive(timeToLive);
		setWidth(8);
		setHeight(8);
        setHeading(heading);
        setFacing(Facing.nearestFacing(heading));
        setParent(parent);
		setLayer(parent.getLayer());
        setRenderingOrder(9);
        setBlockableByScreenEdge(false);
        setCollidable(false);

		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16));
	}

    protected void calcPic(){
        if (getHeading() > Math.PI/2 && getHeading() <= Math.PI+Math.PI/2) setXPic(0);
        else setXPic(1);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        return false;
    }

    public void move(){
        if (getTick() >= timeToLive) die();

        setHeading(parent.getHeading());
        setX(getParent().getX()+(float)(Math.cos(getHeading())*16));
        setY(getParent().getY()-(float)(Math.sin(getHeading())*16));

        calcPic();

        super.move();
    }

    public void die(){
        super.die();
        setDeadCounter(0);
        getScene().removeSprite(this);
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

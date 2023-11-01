package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

public class SonicBlast extends Prop {

    private Prop parent;
    private int timeToLive;
    private boolean strong;

	public SonicBlast(AreaScene scene, Prop parent, double heading, boolean strong){
		super("sonic blast", scene, 0, 5);
//        setSheet(Art.effects32x32);
//        setXPic(0);
//        setYPic(5);
//        setXPicO(0);
//        setYPicO(0);
//        setWPic(32);
//        setHPic(32);
        setSpeed(0);
        setTimeToLive(4);
		setWidth(16);
		setHeight(16);
//        setHeading(heading);
//        setFacing(Facing.nearestFacing(heading));
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

        this.strong = strong;
        if (strong) setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Knockback)));
        else setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Knockback, Attribute.Stun)));

		setX(parent.getX());
		setY(parent.getY()-8);
	}

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    protected void calcPic(){
        int xPic = (getTick()/2)%4;
        setXPic(xPic);
    }

    public void move(){
        if (timeToLive-- < 0) getScene().removeSprite(this);

        setFadeRatio(timeToLive/4f);

        setSpeed(0);
        setMoving(false);

        int inc = 16;
        setWidth(getWidth()+inc);
        setHeight(getHeight()+inc);
        setWPic(getWPic()+inc);
        setHPic(getHPic()+inc);

        setY(getY()+inc/2);

        super.move();

        if (getTick() == 1){
            if (strong) getScene().getSound().play(Art.getSample("scream.wav"), this, 1, 1, 1);
            else getScene().getSound().play(Art.getSample("shout.wav"), this, 1, 1, 1);
        }
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Color color;
        if (strong) color = Color.BLUE;
        else color = Color.CYAN;
        og.setColor(color);
        Rectangle bounds = new Rectangle((int)(getX()-getWidth()/2), (int)(getY()-getHeight()), getWidth(), getHeight());
        float tinc = 255f/5f;
        for (int i = 0; i < 5; i++){
            og.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((5-i)*tinc)));
            og.drawOval(bounds.x+i, bounds.y+i, bounds.width-2*i, bounds.height-2*i);
        }

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

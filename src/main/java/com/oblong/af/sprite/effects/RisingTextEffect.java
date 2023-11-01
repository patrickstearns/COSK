package com.oblong.af.sprite.effects;

import com.oblong.af.GameComponent;
import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.console.Console;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.Footprint;
import com.oblong.af.util.ImageUtils;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RisingTextEffect extends Sprite {

	private static final int MAX_TICK = 15;
	
	private Image image;
	private int tick;
	
	public RisingTextEffect(AreaScene scene, int x, int y, String text, int color){
		super("rising text effect: "+text, scene);
		image = TextImageCreator.getOutlinedTextImage(text, color, Color.BLACK);
		
		setWPic(image.getWidth(null));
		setHPic(image.getHeight(null));
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
        setRenderingOrder(10);
        setX(x);
        setY(y);
		setXa(0);
		setYa(-1);

        setLayer(Area.Layer.Upper);
	}
	
    public int getRenderingOrder(){ return 11; }
    
    public Rectangle getFootprint(){ return new Rectangle(0, 0, 0, 0); }
    public Rectangle2D.Float getFootprint2D(){ return new Rectangle2D.Float(0, 0, 0, 0); }

    public void render(Graphics2D og, float alpha){ //this alpha is scaling, not transparency
        if (alpha == 0f) return;

        //base image location
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha);
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha);

        //if half-sized, shrink proportions
        int dwPic = getWPic(), dhPic = getHPic();
        if (isHalfSize()){
        	dwPic *= 0.75;
        	dhPic *= 0.75;
        }

        //line center of image up with x, yPixel w/bottom of image
        xPixel -= dwPic/2;
        yPixel -= dhPic;

        if (getFadeRatio() != 1f) image = ImageUtils.fadeImage(image, null, getFadeRatio());
        og.drawImage(image, xPixel, yPixel, dwPic, dhPic, null);
    }
    
	protected Point tileBlocking(Footprint footprint){ return null; }
	protected boolean move(float xa, float ya){ return true; }
    public void move(){
    	tick++;
    	setYa(-3*(MAX_TICK-tick)/MAX_TICK);
    	if (tick > MAX_TICK) setFadeRatio((2*MAX_TICK-tick)/(2*MAX_TICK));
    	if (tick > 2*MAX_TICK) getScene().removeSprite(this);
    	
    	setX(getX()+getXa());
    	setY(getY()+getYa());
    }
}
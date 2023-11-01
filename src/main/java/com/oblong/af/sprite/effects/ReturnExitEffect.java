package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

import java.awt.*;

public class ReturnExitEffect extends Effect {

	public ReturnExitEffect(AreaScene scene, int x, int y){
    	super(scene, x, y, Area.Layer.Main, 0, 0, 0, 1, 48, 48, Integer.MAX_VALUE, false);
        setWidth(48);
        setHeight(48);
        setSheet(Art.effects48x48);
    }

    public void move(){
        int xpic = (getTick()/4)%4;
        if (xpic == 3) xpic = 1;
        setXPic(xpic);

        sparkle();
    }

    private void sparkle(){
        int sx = (int)(getX()-getWidth()/2+getWidth()*Math.random());
        int sy = (int)(getY()-getHeight()+Math.random()*(getHeight()-16));
        getScene().addSprite(new Sparkle(getScene(), sx, sy, Color.WHITE, 0, 1));
    }

    public void render(Graphics2D og, float alpha){
        og.setColor(new Color(1f, 1f, 1f, 0.5f));
        og.fillOval((int)(getX()-16), (int)getY(), 32, 16);
        super.render(og, alpha);
    }

}
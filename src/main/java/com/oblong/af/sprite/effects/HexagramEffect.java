package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

import java.awt.*;

public class HexagramEffect extends Effect {

	public HexagramEffect(AreaScene scene, int x, int y){
    	super(scene, x, y, Area.Layer.Lower, 0, 0, 4, 3, 32, 32, Integer.MAX_VALUE, false);
        setWidth(32);
        setHeight(32);
        setSheet(Art.objects32x32x2);
        setYPicO(-16);
    }

    public void move(){
        super.move();
        setFadeRatio(Math.abs((getTick()%64)-32)/32f);
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);
    }

}
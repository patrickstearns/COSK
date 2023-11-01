package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FeatherEffect extends Sprite {

    private static final int MAX_TICK = 18;

    private int tick;

    public FeatherEffect(AreaScene scene, int x, int y, boolean white){
        super("feather-"+x+"-"+y, scene);
        setX(x);
        setY(y);
        setSheet(Art.feather);
        setWPic(16);
        setHPic(16);
        setWidth(16);
        setHeight(16);
        setXa(0);
        setYa(1);
        setLayer(Area.Layer.Upper);
        if (white) setYPic(1);

        if (Math.random() > 0.5f) tick = 8;
    }

    public int getRenderingOrder(){ return 11; }

    public Rectangle getFootprint(){ return new Rectangle(0, 0, 0, 0); }
    public Rectangle2D.Float getFootprint2D(){ return new Rectangle2D.Float(0, 0, 0, 0); }

    protected boolean move(float xa, float ya){ return true; }
    public void move(){
        tick++;

        int xp = tick%16;
        if (xp >= 8) xp = 15-tick;
        setXPic(xp);

        if (tick > MAX_TICK-10){
            setFadeRatio(((float)MAX_TICK-tick)/10f);
        }

        if (tick >= MAX_TICK) getScene().removeSprite(this);

        setX(getX()+getXa());
        setY(getY()+getYa());
    }
}
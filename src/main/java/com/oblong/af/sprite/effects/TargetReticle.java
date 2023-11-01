package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

import java.awt.*;

public class TargetReticle extends Effect {

    public TargetReticle(AreaScene scene, int x, int y, int timeToLive){
        super(scene, x, y, Area.Layer.Upper, 0, 0, 0, 14, 8, 8, timeToLive, true);
    }

    public void move(){
        if (getTick() > maxTick){
            getScene().removeSprite(this);
            return;
        }

        super.move();
    }

    public void render(Graphics2D g, float alpha){
        //base image location
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();

        g.drawImage(Art.effects16x16[(getTick()/2)%4][14], xPixel-8, yPixel-16, null);
    }
}
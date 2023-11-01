package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

import java.awt.*;

public class DigDirt extends Effect {

    public DigDirt(AreaScene scene, int x, int y, int timeToLive){
        super(scene, x, y, Area.Layer.Main, 0, 0, 0, 14, 32, 32, timeToLive, true);
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

        g.drawImage(Art.effects32x32[(getTick()/2)%4][12], xPixel-16, yPixel-30, null);
    }
}
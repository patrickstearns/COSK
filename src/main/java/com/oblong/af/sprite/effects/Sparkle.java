package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;

import java.awt.*;

public class Sparkle extends Effect {

    private Color color;
    private float radius;

    public Sparkle(AreaScene scene, int x, int y, Color c, int xa, int ya){
        super(scene, x, y, Area.Layer.Upper, 0, 0, 0, 1, 8, 8, (int)(Math.random()*8+8), false);
        setXa(xa);
        setYa(ya);
        radius = 0.5f+(float)(Math.random()*1);
        this.color = c;
    }

    public void move(){
        if (getTick() > maxTick){
            getScene().removeSprite(this);
            return;
        }

        if (getTick()/2%2 == 0) radius--;
        else radius++;

        super.move();
    }

    public void render(Graphics2D g, float alpha){
        //base image location
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
        g.setColor(color);

        g.drawLine((int)(xPixel-radius), yPixel, (int)(xPixel+radius)+1, yPixel);
        g.drawLine(xPixel, (int)(yPixel-radius), xPixel, (int)(yPixel+radius)+1);
    }
}
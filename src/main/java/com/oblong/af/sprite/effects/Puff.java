package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;

import java.awt.*;

public class Puff extends Effect {

    private Color color, fadedColor;
    private float radius;

    public Puff(AreaScene scene, int x, int y, Color c){
        super(scene, x, y, Area.Layer.Upper, 0, 0, 0, 1, 8, 8, (int)(Math.random()*8+8), false);
        setYa((float) (-2 - 2 * Math.random()));
        radius = (float)(Math.random()*4);
        this.color = c;
    }

    public Puff(AreaScene scene, int x, int y){
        this(scene, x, y, Color.WHITE);

        double c = Math.random();
        if (c < 0.25d) color = Color.WHITE;
        else if (c < 0.50d) color = Color.LIGHT_GRAY;
        else if (c < 0.75d) color = Color.GRAY;
        else color = Color.DARK_GRAY;

        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(Math.random()*256));

        if (Math.random() < 0.1f){
            radius = 1;
            color = Color.ORANGE;
        }

        fadedColor = color;
    }

    public void move(){
        if (getTick() > maxTick){
            getScene().removeSprite(this);
            return;
        }

        double m = Math.random();
        setXa(getXa()/1.1f);
        setYa(getYa()/1.1f);
        if (getYa() > -1) setYa(getYa()-0.2f);
        if (m < 0.05d) setXa(getXa()-1);
        else if (m < 0.10d) setXa(getXa()+1);

        int na = (int)(color.getAlpha()*((maxTick-getTick())/(float)maxTick));
        fadedColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), na);

        super.move();
    }

    public void render(Graphics2D g, float alpha){
        //base image location
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
        g.setColor(fadedColor);
        g.fillOval((int)(xPixel-radius), (int)(yPixel-radius), (int)(radius*2), (int)(radius*2));
    }
}
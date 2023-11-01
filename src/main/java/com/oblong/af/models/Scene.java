package com.oblong.af.models;

import com.mojang.sonar.SonarSoundEngine;
import com.mojang.sonar.SoundListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


public abstract class Scene implements SoundListener {

	protected SonarSoundEngine sound;

    public final void setSound(SonarSoundEngine sound){
        sound.setListener(this);
        this.sound = sound;
    }

    public abstract void init();
    public abstract void tick();
    public abstract void render(Graphics2D og, float alpha);
    protected abstract boolean isMouseOverHotspot(Point mousePosition);

    public SonarSoundEngine getSound(){ return sound; }

    public void toggleSubscreen(){} //noop for most scenes

    protected Point convertMouseLocation(Point position){
        int f = 2;
        int mx = (position.x/f);
        int my = (position.y/f);
        return new Point(mx, my);
    }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (mousePosition == null) return;

        //arrow angle based on angle from player
        int xDiff = mousePosition.x-screenCenter.x, yDiff = mousePosition.y-screenCenter.y;
        double angleRads = Math.atan2(screenCenter.x - mousePosition.x, screenCenter.y - mousePosition.y);
        angleRads += Math.PI/2; //b/c 0 is up, but arrow points given make it point left

        //arrow length based on distance from player, width constant
        int length;
        double hyp = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
        if (hyp < 10) length = 2;
        else if (hyp > 200) length = 20;
        else length = (int)(hyp/10);
        int width = 8;

        //offsets so it points at the right spot
        int arrowXLength = (int)(Math.cos(angleRads)*(length+width));
        int arrowYLength = (int)(Math.sin(angleRads)*(length+width));

        //figure out based on whether arrow is over clickable area
        Color arrowColor = new Color(0.5f, 0.5f, 0.5f);
        if (isMouseOverHotspot(convertMouseLocation(mousePosition))) arrowColor = new Color(0f, 1f, 0f);

        //constants
        Color arrowOutlineColor = Color.BLACK;
        Point2D[] ps = new Point2D[]{
                new Point2D.Double(0,               width/2),
                new Point2D.Double(length,          width/2),
                new Point2D.Double(length,          width),
                new Point2D.Double(length+width,    0),
                new Point2D.Double(length,          -width),
                new Point2D.Double(length,          -width/2),
                new Point2D.Double(0,               -width/2),
        };
        Point2D[] ds = new Point2D[ps.length];

        //rotate and translate it
        AffineTransform at = AffineTransform.getRotateInstance(-angleRads);
        at.transform(ps, 0, ds, 0, 7);

        //turn into integer-precision polygon
        int[] xsi = new int[7], ysi = new int[7];
        for (int i = 0; i < ds.length; i++){
            xsi[i] = (int)ds[i].getX();
            ysi[i] = (int)ds[i].getY();
            xsi[i] += mousePosition.x/2;
            ysi[i] += mousePosition.y/2;
            xsi[i] -= arrowXLength;
            ysi[i] += arrowYLength;
        }
        Polygon polygon = new Polygon(xsi, ysi, 7);

        //draw it
        g.setColor(arrowColor);
        g.fillPolygon(polygon);
        g.setColor(arrowOutlineColor);
        g.drawPolygon(polygon);
    }
}
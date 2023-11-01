package com.oblong.af.sprite.projectile;

import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class Squirt extends Prop {
    private int tick = 0, maxLength = 96, maxTicks = 5;
    private double heading;
    private Point a, b;
    private Prop parent;

    public Squirt(int x, int y, double heading, Prop parent){
        super("squirt", parent.getScene(), 0, 0);
        this.heading = heading;
        a = new Point(x, y);
        b = new Point(x, y);
        this.parent = parent;
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        tick++;

        if (tick > maxTicks){
            getScene().removeSprite(this);
            return;
        }

        //move a along at regular speed, b at half that, unless a hits something
        float speed = (float)maxLength/(float)maxTicks; //max speed of leading droplet
        for (Prop prop: getScene().propsBlockingMovement(new Rectangle2D.Float(a.x-2, a.y-2, 4, 4))){
            if (prop == parent) continue;
            speed = 0;
            prop.damage(new DamageAttributes(1, Arrays.asList(Attribute.Water)));
        }

        float xd = (float)Math.cos(heading)*speed;
        float yd = -(float)Math.sin(heading)*speed;
        a.x += xd;
        a.y += yd;
        b.x += xd/2;
        b.y += yd/2;

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("squirt.wav"), this, 1, 1, 1);
    }

    public void render(Graphics2D g, float alpha){
        //for each drop, draw a line from drops[i] to lastDrops[i]
        g.setPaint(new GradientPaint(a.x, a.y, Color.BLUE, b.x, b.y, Color.WHITE));
        g.drawLine(a.x, a.y, b.x, b.y);
    }
}

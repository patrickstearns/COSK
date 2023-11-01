package com.oblong.af.sprite.projectile;

import com.oblong.af.level.Area;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.thing.IceShield;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class Silk extends Prop {
    private int tick = 0, maxLength = 200, maxTicks = 5;
    private double heading;
    private Point a, b;
    private Prop parent;

    public Silk(int x, int y, double heading, Prop parent){
        super("Silk", parent.getScene(), 0, 0);
        this.heading = heading;
        a = new Point(x, y);
        b = new Point(x, y);
        this.parent = parent;
        setLayer(Area.Layer.Upper);
        setBlockable(false);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        if (prop instanceof IceShield) return false;
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
            if (prop instanceof IceShield) continue;
            speed = 0;
            prop.damage(new DamageAttributes(0, Arrays.asList(Attribute.Slow)));
        }

        float xd = (float)Math.cos(heading)*speed;
        float yd = -(float)Math.sin(heading)*speed;
        a.x += xd;
        a.y += yd;
        b.x += xd/2;
        b.y += yd/2;

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("whiff.wav"), this, 1, 1, 1);
    }

    public void render(Graphics2D g, float alpha){
        //for each drop, draw a line from drops[i] to lastDrops[i]
        g.setPaint(new GradientPaint(a.x, a.y, Color.WHITE, b.x, b.y, Color.GRAY));
        g.drawLine(a.x, a.y, b.x, b.y);
    }
}

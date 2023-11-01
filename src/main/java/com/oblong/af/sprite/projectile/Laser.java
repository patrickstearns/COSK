package com.oblong.af.sprite.projectile;

import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class Laser extends Prop {

    protected int tick = 0, maxLength = 96, maxTicks = 100;
    protected double heading;
    protected Point a, b;
    protected Prop parent;
    protected boolean dead = false;

    public Laser(int x, int y, double heading, Prop parent){
        super("laser", parent.getScene(), 0, 0);
        this.heading = heading;
        a = new Point(x, y);
        b = new Point(x, y);
        this.parent = parent;
        setDiesOnCollide(true);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        tick++;

        if (tick == 1)
            getScene().getSound().play(Art.getSample("laser.wav"), this, 1, 1, 1);

        if (dead){
            getScene().removeSprite(this);
            return;
        }
        if (tick > maxTicks){
            getScene().removeSprite(this);
            return;
        }

        //move a along at regular speed, b at half that, unless a hits something
        float baseSpeed = (float)maxLength/(float)4; //max speed of leading droplet
        float speed = baseSpeed;
        for (Prop prop: getScene().propsBlockingMovement(new Rectangle2D.Float(a.x-2, a.y-2, 4, 4))){
            if (!isCollidableWith(prop)) continue;
            speed = 0;
            prop.damage(new DamageAttributes(1, Arrays.asList(Attribute.Spirit)));
            dead = true;
        }

        float xd = (float)Math.cos(heading)*speed;
        float yd = -(float)Math.sin(heading)*speed;
        a.x += xd;
        a.y += yd;
        if (tick > 2){
            b.x += xd;
            b.y += yd;
        }
    }

    public void render(Graphics2D g, float alpha){
        if (dead) return;

        //for each drop, draw a line from drops[i] to lastDrops[i]
        int yOffset = -12; //so it looks like it's coming from mouth
        g.setPaint(new GradientPaint(a.x, a.y+yOffset, Color.WHITE, b.x, b.y+yOffset, Color.ORANGE));
        g.drawLine(a.x, a.y+yOffset, b.x, b.y+yOffset);
    }
}

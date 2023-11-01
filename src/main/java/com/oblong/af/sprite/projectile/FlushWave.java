package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class FlushWave extends Prop {

    private class Breaker {
        double x, y, h;
        public Breaker(double x, double y, double h){
            this.x = x;
            this.y = y;
            this.h = h;
        }
    }

    private int tick = 0, maxExpandTicks = 16, maxFadeTicks = 24; //fade includes expand
    private Prop parent;
    private int radius;
    private java.util.List<Breaker> breakers;

    public FlushWave(int x, int y, Prop parent){
        super("flush wave", parent.getScene(), 0, 0);
        setX(x);
        setY(y);
        this.parent = parent;
        breakers = new ArrayList<Breaker>();
        for (int i = 0; i < 90; i++){
            double h = Math.random()*(Math.PI*2);
            int bx = (int)(x+(float)(Math.cos(h)*4));
            int by = (int)(y-(float)(Math.sin(h)*4));
            breakers.add(new Breaker(bx, by, h));
        }

        setImpactDamageAttributes(new DamageAttributes(5, Arrays.asList(Attribute.Water, Attribute.Knockback)));
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        if (prop instanceof PlayerOrbeholder) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        tick++;

        if (tick == 1)
            getScene().getSound().play(Art.getSample("geyser.wav"), this, 1, 1, 1);

        if (tick > maxFadeTicks){
            getScene().removeSprite(this);
            return;
        }

        if (tick < maxExpandTicks){
            radius += (64-radius)/2;
            if (tick < maxExpandTicks/2){
                for (int i = 0; i < 10; i++){
                    double h = Math.random()*(Math.PI*2);
                    int bx = (int)(getX()+(float)(Math.cos(h)*4));
                    int by = (int)(getY()-(float)(Math.sin(h)*4));
                    breakers.add(new Breaker(bx, by, h));
                }
            }
        }

        for (Breaker breaker: breakers){
            double dx = (Math.cos(breaker.h)*((60-radius)*Math.random()+4));
            double dy = -(Math.sin(breaker.h)*((60-radius)*Math.random()+4));

            if (Point2D.distance(breaker.x+dx, breaker.y+dy, getX(), getY()) > radius+2){
                dx = 0;
                dy = 0;
            }

            breaker.x += (int)dx;
            breaker.y += (int)dy;
        }

        //damage any collideables
        for (Prop prop: getScene().getDamageablePropsWithinRange((int)getX(), (int)getY(), radius)){
            if (prop != parent && isCollidableWith(prop))
                prop.damage(getImpactDamageAttributes());
        }
    }

    public void render(Graphics2D g, float alpha){
        float fade = 1f;
        if (getTick() >= maxExpandTicks){
            fade = 1-((float)(getTick()-maxExpandTicks)/(float)(maxFadeTicks-maxExpandTicks));
        }

        g.setColor(new Color(0f, 0.5f, 1f, fade*0.5f));
        g.fillOval((int)(getX()-radius), (int)(getY()-radius), radius*2, radius*2);
        g.setColor(Color.WHITE);
        for (Breaker breaker: breakers)
            g.fillOval((int)(breaker.x-2), (int)(breaker.y-2), 3, 3);
    }
}

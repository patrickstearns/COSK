package com.oblong.af.sprite.projectile;

import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.enemy.BigBeholder;
import com.oblong.af.sprite.enemy.Orbeholder;

import java.awt.*;

public class OrbeholderLaser extends Laser {

    private boolean thick;

    public OrbeholderLaser(int x, int y, double heading, Prop parent, boolean thick){
        super(x, y, heading, parent);
        this.thick = thick;
    }

    public boolean isCollidableWith(Prop prop){
        if (prop.isProjectile()) return false;
        if (prop instanceof BigBeholder) return false;
        if (prop instanceof Orbeholder) return false;
        return super.isCollidableWith(prop);
    }

    public void render(Graphics2D g, float alpha){
        if (dead) return;

        int yOffset = -12; //so it looks like it's coming from mouth
        g.setPaint(new GradientPaint(a.x, a.y+yOffset, Color.WHITE, b.x, b.y+yOffset, Color.ORANGE));

        if (thick){
            Stroke s = g.getStroke();
            g.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(a.x, a.y+yOffset, b.x, b.y+yOffset);
            g.setStroke(s);
        }
        else g.drawLine(a.x, a.y+yOffset, b.x, b.y+yOffset);
    }
}

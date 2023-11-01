package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class Spark extends Prop {

    public Spark(AreaScene scene, int x, int y) {
        super("spark", scene, 0, 3);
        setSheet(Art.projectiles);
        setX(x);
        setY(y);
        setXPic(0);
        setYPic(3);
        setOxPic(0);
        setOyPic(3);
        setWPic(8);
        setHPic(8);
        setWidth(8);
        setHeight(8);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setBlockableByScreenEdge(false);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(false);

        setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Electric)));
    }

    public void move(){
        super.move();

        int xOff = getTick()%4;

        setXPic(getOxPic()+xOff);

        if (getTick() > 1) die();
    }

    public void die(){
        super.die();
        setDeadCounter(1);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

}

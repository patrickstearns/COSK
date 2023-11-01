package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.thing.Thing;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class LightningStrike extends Thing {

    private Prop parent;

    public LightningStrike(AreaScene scene, Prop parent, int x, int y){
        super("lightningstrike" + Math.random(), scene, x, y, Art.bossWyvern32x32, 0, 0, 32, 32, 32, 32, false, false);
        this.parent = parent;
        setShadowVisible(false);

        setCollidable(true);
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Electric)));

        setWidth(16);
        setHeight(16);
        setWPic(16);
        setHPic(96);
    }

    public boolean isProjectile(){ return true; }
    public boolean isCollidableWith(Prop prop){
        if (prop == parent) return false;
        if (prop.isProjectile()) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        setXPic(getTick()/3);
        setYPic(Math.random() < 0.5 ? 0 : 1);

        if (getTick() == 1)
            getScene().getSound().play(Art.getSample("lightningStrike.wav"), this, 1, 1, 1);

        if (getXPic() > 4) getScene().removeSprite(this);
    }
}

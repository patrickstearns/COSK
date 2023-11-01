package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class RockColumn extends Prop {

    private int timeToLive = 32;

    public RockColumn(AreaScene scene, int x, int y) {
        super("rock column", scene, 0, 2);
        setSheet(Art.effects32x48);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlockableByScreenEdge(false);
        setBlocksFlying(true);
        setBlocksMovement(true);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollideWithPlayer(false);
        setCanBeKnockedback(false);
        setWidth(24);
        setHeight(24);
        setWPic(32);
        setHPic(48);
        setX(x);
        setY(y);
        setYPic(2);
        setYPicO(-8);
        setShadowVisible(false);

        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Earth)));
    }

    public void calcPic(){
        int xPic = getTick();
        if (xPic > 3) xPic = 3;
        if (timeToLive-getTick() < 3) xPic = timeToLive-getTick();
        setXPic(xPic);
    }

    public void move(){
        if (getTick() == 4) setImpactDamageAttributes(null);
        if (getTick() > timeToLive) die();
        super.move();
        calcPic();
        if (getTick()==1)
            getScene().getSound().play(Art.getSample("rip.wav"), this, 1, 1, 1);
    }

    public void die(){
        super.die();
        getScene().removeSprite(this);
    }
}

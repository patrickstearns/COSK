package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class GrowingPlant extends Prop {

    private int brokenTime = 0, maxBrokenTime = 0;

    public GrowingPlant(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.objects16x32);

        setXPic(oxPic+3);

        setImmuneToDamage(false);
        setImmuneToStatusEffects(true);
        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlockableByScreenEdge(false);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollide(true);
        setDiesOnCollideWithPlayer(false);
        setShadowVisible(false);

        maxBrokenTime = 50+(int)(Math.random()*8);

        updateState();
    }

    private void updateState(){
        if (getXPic()-getOxPic() != 3){
            setBlocksMovement(false);
            setBlocksFlying(false);
            setCollidable(false);
            setBlockable(false);
            setDiesOnCollide(false);
        }
        else{
            setBlocksMovement(false);
            setBlocksFlying(true);
            setCollidable(true);
            setBlockable(true);
            setDiesOnCollide(true);
        }
    }

    public void move(){
        super.move();

        if (brokenTime > 0)
            brokenTime--;

        setLayer(brokenTime > 2 ? Area.Layer.Lower : Area.Layer.Main);

        for (Prop prop: getScene().propsColliding(this, getFootprint()))
            if (prop.isBlocksMovement())
                die();

        updateState();

        int xPic = getOxPic();
        if (brokenTime == 3) xPic += 0;
        else if (brokenTime == 2) xPic += 1;
        else if (brokenTime == 1) xPic += 2;
        else if (brokenTime == 0) xPic += 3;
        else if (brokenTime == maxBrokenTime) xPic += 4;
        else if (brokenTime == maxBrokenTime-1) xPic += 5;
        else if (brokenTime == maxBrokenTime-2) xPic += 6;
        else if (brokenTime == maxBrokenTime-3) xPic += 7;
        setXPic(xPic);
    }

    public void damage(DamageAttributes attributes){
        if (isDead()) return;
        DamageAttributes modified = modifyDamageAttributes(attributes);
        if (modified.getAttributes().contains(Attribute.Wind) || modified.getAttributes().contains(Attribute.Physical))
            die();
    }

    public void die(){
        brokenTime = maxBrokenTime;
    }

}

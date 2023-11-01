package com.oblong.af.sprite.enemy;


import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

public class SpiderGenerator extends Prop {

    private int waitTime = 0, baseWaitTime = 50;

    public SpiderGenerator(AreaScene scene){
        super("SpiderGenerator", scene, 0, 0);
        setSheet(Art.spiderGenerator32x16);
        setWPic(32);
        setHPic(16);
        setYPicO(0);
        setWidth(16);
        setHeight(12);
        setMaxHp(4);
        setHp(4);
        waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
        setShadowVisible(false);
        setCanBeKnockedback(false);
        setLayer(Area.Layer.Lower);
        setCollidable(false);
        setBlocksMovement(false);
        setBlocksFlying(false);
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (waitTime > 0) waitTime--;

            if (waitTime == 0){
                IceSpider spider = new IceSpider(getScene(), 0, 0, true);
                spider.setX(getX());
                spider.setY(getY());
                spider.setHeading(Math.random() * Math.PI * 2);
                getScene().addSprite(spider);
                waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
                getScene().getSound().play(Art.getSample("splat.wav"), this, 1, 1, 1);
            }
        }
        else{
            waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
        }

        //freeze if anything freezing comes near
        for (Prop prop: getScene().propsColliding(this, getFootprint()))
            if (prop.getImpactDamageAttributes() != null && prop.getImpactDamageAttributes().getAttributes().contains(Attribute.Freeze))
                damage(new DamageAttributes(0, Arrays.asList(Attribute.Freeze)));

        super.move();
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;

        if (isDead()){
            xOff = (int)(4*((20f-(float)getDeadCounter())/20f));
            yOff = 2;
        }
        else if (waitTime < 16){
            int index = (16-waitTime)/2;
            if (index < 4) xOff = index;
            else{
                xOff = index-4;
                yOff = 1;
            }
        }

        setXPic(getOxPic()+xOff);
        setYPic(getOyPic()+yOff);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }
}

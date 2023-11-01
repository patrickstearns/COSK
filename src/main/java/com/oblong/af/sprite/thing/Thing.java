package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

import java.awt.*;

public class Thing extends Prop {

    public Thing(String id, AreaScene scene, int x, int y, Image[][] sheet, int xPic, int yPic, int wPic, int hPic,
                 int xPicO, int yPicO, int width, int height, boolean tangible, boolean knockbackable){
        this(id, scene, x, y, sheet, xPic, yPic, wPic, hPic, width, height, tangible, knockbackable);
        setXPicO(xPicO);
        setYPicO(yPicO);
    }

    public Thing(String id, AreaScene scene, int x, int y, Image[][] sheet, int xPic, int yPic, int wPic, int hPic,
                 int width, int height, boolean tangible, boolean knockbackable){
        super(id, scene, xPic, yPic);
        setX(x);
        setY(y);
        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setBlockable(tangible);
        setBlockableByScreenEdge(tangible);
        setBlocksFlying(tangible);
        setBlocksMovement(tangible);
        setCanBeKnockedback(knockbackable);
        setCollidable(tangible);
        setSheet(sheet);
        setXPic(xPic);
        setYPic(yPic);
        setOxPic(xPic);
        setOyPic(yPic);
        setWPic(wPic);
        setHPic(hPic);
        setWidth(width);
        setHeight(height);
        setMaxHp(1);
        setHp(1);
        setShadowVisible(false);
    }

}

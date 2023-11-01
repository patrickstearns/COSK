package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.Art;

public class Effect extends Sprite {

    protected int maxTick;
    private boolean animated;

    private static Effect createDamageEffect(AreaScene scene, Sprite smacked, int xPic, int yPic){
        int x = (int)(smacked.getX()-smacked.getWPic()/2+Math.random()*smacked.getWPic());
        int y = (int)(smacked.getY()-smacked.getHPic()/2+Math.random()*smacked.getHPic());
        return new Effect(scene, x, y, smacked.getLayer(), 0, 0, xPic, yPic, 16, 16, 4, true);
    }

    public static Effect createPhysicalDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 0); }
    public static Effect createFireDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 1); }
    public static Effect createIceDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 2); }
    public static Effect createWindDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 3); }
    public static Effect createEarthDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 4); }
    public static Effect createLightDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 5); }
    public static Effect createPoisonDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 6); }
    public static Effect createElectricDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 16); }
    public static Effect createHealDamageEffect(AreaScene scene, Sprite smacked){ return createDamageEffect(scene, smacked, 0, 7); }

    protected Effect(AreaScene scene, int x, int y, Area.Layer layer, float xa, float ya, int xPic, int yPic, int wPic, int hPic, int maxTick, boolean animated){
    	super("effect", scene);
    	setSheet(Art.projectiles);
        setX(x);
        setY(y);
        setWidth(4);
        setHeight(4);
        setXa(xa);
        setYa(ya);
        setOxPic(xPic);
        setOyPic(yPic);
        setXPic(xPic);
        setYPic(yPic);
        setXPicO(0);
        setYPicO(0);
        setWPic(wPic);
        setHPic(hPic);
        setLayer(layer);
        setRenderingOrder(10);

        this.maxTick = maxTick;
        this.animated = animated;

        if (animated) setSheet(Art.effects16x16);
    }

    public void move(){
        if (animated){
            setXPic(getXPic()+1);
            if (getXPic() >= getOxPic()+4) setXPic(getOxPic());
        }

        setX(getX()+getXa());
        setY(getY()+getYa());
        if (getTick() >= maxTick) getScene().removeSprite(this);
    }

}
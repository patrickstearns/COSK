package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.Art;

import java.awt.geom.Point2D;

public class SlimeTrail extends Prop {

    private int ignitedTime = 0, maxIgnitedTime = 4;

    public SlimeTrail(AreaScene scene) {
        super("slimetrail", scene, 0, 0);
        setSheet(Art.slimeTrail);

        setWPic(16);
        setHPic(16);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setCollidable(false);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setBlockableByScreenEdge(false);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(false);
        setXPic((int)(Math.random()*3000)%4);
        setYPic(0);
        setLayer(Area.Layer.Lower);

        setShadowVisible(false);
    }

    public void die(){
        getScene().removeSprite(this);
        FireField ff = new FireField("firefield", getScene(), 48, null, true, -1);
        ff.setX(getX());
        ff.setY(getY());
        getScene().addSprite(ff);
    }

    public void move(){
        if (ignitedTime > 0){
            ignitedTime--;
            if (ignitedTime == 0){
                die();
                return;
            }
        }
        else{
            for (Sprite sprite: getScene().getSprites()){
                double distance = Point2D.distance(sprite.getX(), sprite.getY(), getX(), getY());
                if (distance < 16){
                    if (sprite instanceof Prop){
                        Prop prop = (Prop)sprite;
                        if (prop.getImpactDamageAttributes() != null && prop.getImpactDamageAttributes().getAttributes().contains(Attribute.Fire))
                            ignitedTime = maxIgnitedTime;
                    }
                }
            }
        }
    }

}

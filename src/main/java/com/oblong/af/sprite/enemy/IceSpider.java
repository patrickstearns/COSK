package com.oblong.af.sprite.enemy;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.thing.IceShield;
import com.oblong.af.util.Art;

import java.util.Arrays;

/**
 * Identical to beetles except for the spritesheet they use and that they can't explode
 */

public class IceSpider extends Prop {

    private int rollTime, maxRollTime = 4;

    public IceSpider(AreaScene scene, int oxPic, int oyPic, boolean rolling){
        super("IceSpider", scene, oxPic, oyPic);
        setSheet(Art.spider16x16);
        if (rolling) rollTime = maxRollTime;
        setWPic(16);
        setHPic(16);
        setWidth(16);
        setHeight(12);

        setCollidable(false);
        setBlockable(false);
        setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Freeze)));
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(true);
        setImpactDamagesPlayerOnly(true);
        setLayer(Area.Layer.Upper);
        setCanBeKnockedback(true);
        setShadowVisible(false);
    }

    public boolean isCollidableWith(Prop prop){
        if (prop instanceof IceSpider || prop instanceof IceShield) return false;
        return super.isCollidableWith(prop);
    }

    public void move(){
        if (rollTime > 0){
            rollTime--;

            setMoving(true);
            setSpeed(10);

            if (rollTime == 0){
                setBlockable(true);
                setCollidable(true);
                setLayer(Area.Layer.Main);
                setMoving(true);
                setHeading(0);
            }
        }
        else{
            boolean canAct = true;
            if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen) ||
                    hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
                canAct = false;

            if (canAct){
                if (Math.random() < 0.2){
                    setHeading(getHeading()+(0.6-(Math.random()*0.12)));
                }
                setSpeed(4);
            }
            else{
                setMovementLockedCounter(0);
                setXa(0);
                setYa(0);
                setMovementLockedCounter(2);
            }
        }

        super.move();
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;

        if (isDead()){
            xOff = 4;
            yOff = 1;
        }
        else if (rollTime > 0){
            xOff = 4;
            yOff = 0;
        }
        else {
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT:
                    xOff = 0;
                    yOff = 0;
                    break;
                case RIGHT:
                    xOff = 0;
                    yOff = 1;
                    break;
                case UP:
                    xOff = 2;
                    yOff = 0;
                    break;
                case DOWN:
                    xOff = 2;
                    yOff = 1;
                    break;
            }

            xOff += (getTick()/4)%2;
        }

        setXPic(getOxPic()+xOff);
        setYPic(getOyPic()+yOff);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }
}

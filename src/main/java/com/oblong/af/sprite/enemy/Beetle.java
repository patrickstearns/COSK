package com.oblong.af.sprite.enemy;


import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class Beetle extends Prop {

    private int rollTime, maxRollTime = 4;
    private boolean explosive = false;

    public Beetle(String id, AreaScene scene, int oxPic, int oyPic, boolean rolling, boolean explosive){
        super(id, scene, oxPic, oyPic);
        setSheet(Art.beetle);
        this.explosive = explosive;
        if (rolling) rollTime = maxRollTime;
        setWPic(16);
        setHPic(16);
        setWidth(16);
        setHeight(12);

        setBlockable(false);
        setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Physical)));
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(true);
        setImpactDamagesPlayerOnly(true);
        setLayer(Area.Layer.Upper);
        setCanBeKnockedback(true);
        setShadowVisible(false);

        if (explosive){
            double randpow = Math.random();
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Explode);
            }
            else if (randpow < 0.08)
                setPowerupDrop(Powerups.HpPotion);
        }
    }

    public void die(){
        super.die();
        if (explosive){
            getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));
            DamageAttributes attrs = new DamageAttributes(3, Arrays.asList(Attribute.Fire));
            for (Prop target: getScene().getDamageablePropsWithinRange((int) getX(), (int) getY(), 32))
                target.damage(attrs);
        }
        else getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }

    public void move(){
        if (rollTime > 0){
            rollTime--;

            int rollingTime = maxRollTime-rollTime;
            setY(getY()+rollingTime*1.5f);

            if (rollTime == 0){
                setBlockable(true);
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

        if (explosive) yOff += 2;

        setXPic(getOxPic()+xOff);
        setYPic(getOyPic()+yOff);
    }

}

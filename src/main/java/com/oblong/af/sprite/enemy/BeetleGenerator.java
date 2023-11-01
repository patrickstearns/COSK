package com.oblong.af.sprite.enemy;


import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Ability;
import com.oblong.af.models.Powerups;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class BeetleGenerator extends Prop {

    private int waitTime = 0, baseWaitTime = 50;
    private boolean makingExplosive = false;

    public BeetleGenerator(String id, AreaScene scene, int oxPic, int oyPic){
        super(id, scene, oxPic, oyPic);
        setSheet(Art.beetleGenerator);
        setWPic(32);
        setHPic(32);
        setYPicO(-2);
        setWidth(24);
        setHeight(16);
        waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
        setShadowVisible(false);
        setCanBeKnockedback(false);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.OneUp);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Earthproof);
        }
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (waitTime > 0) waitTime--;

            if (waitTime == 0){
                Beetle beetle = new Beetle("Beetle", getScene(), 0, 0, true, makingExplosive);
                beetle.setX(getX());
                beetle.setY(getY());
                getScene().addSprite(beetle);
                beetle.resetToLastUnblockedPosition();
                waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
                makingExplosive = Math.random() < 0.2;

                getScene().getSound().play(Art.getSample("splat.wav"), this, 1, 1, 1);
            }
        }
        else{
            waitTime = (int)(baseWaitTime+Math.random()*baseWaitTime);
        }
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

        if (makingExplosive) xOff += 4;

        setXPic(getOxPic()+xOff);
        setYPic(getOyPic()+yOff);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash2.wav"), this, 1, 1, 1);
    }
}

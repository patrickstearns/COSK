package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.thing.FireField;
import com.oblong.af.sprite.thing.SlimeTrail;
import com.oblong.af.util.Art;

/**
 * Rabides hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to tongue him to death.
 */

public class Fireslime extends Prop {

    private int hopTime = 0, maxHopTime = 48;
    private int stopTime = 0, maxStopTime = 4;
    private int mitosisTime = 0, maxMitosisTime = 32;
    private int damageTime = 0, maxDamageTime = 8;
    private int trailDelay = 0, maxTrailDelay = 16;

    public Fireslime(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.fireslime);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(-8);
        setWidth(8);
        setHeight(8);
        stopTime = maxStopTime;

        setShadowVisible(false);
        setCanBeKnockedback(true);

        double randpow = Math.random();
        if (randpow < 0.02){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Sparkler);
        }
        else if (randpow < 0.08){
            setPowerupDrop(Powerups.AbilityGem);
            setAbilityDrop(Ability.Fireproof);
        }

    }

    public void calcPic(){
        int xOff = 0, yOff = 0;
        switch(getFacing()){
            case LEFT: yOff = 2; break;
            case RIGHT: yOff = 3; break;
            case UP: yOff = 1; break;
            case DOWN: default: yOff = 0; break;
        }

        if (isDead()){
            xOff = 1;
            yOff = 5;
        }
        else if (damageTime > 0){
            xOff = 0;
            yOff = 5;
        }
        else if (stopTime > 0) xOff = 0;
        else if (hopTime > 0) xOff = 1+(hopTime/8)%4;
        else if (mitosisTime > 0){
            if (mitosisTime > 8) xOff = (mitosisTime/2)%2;
            else if (mitosisTime > 6) xOff = 2;
            else if (mitosisTime > 3) xOff = 3;
            else if (mitosisTime > 0) xOff = 4;
            yOff = 4;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

    protected float determineSpeed(){
        if (hopTime > 0) return 0.5f;
        else return 0;
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){

            if (damageTime > 0){
                damageTime--;
            }
            else if (hopTime > 0){
                hopTime--;

                if (trailDelay > 0) trailDelay--;
                if (trailDelay == 0){
                    boolean clear = true;
                    for (Sprite sprite: getScene().getSprites())
                        if (sprite instanceof SlimeTrail && Math.abs(sprite.getX()-getX()) < 4 && Math.abs(sprite.getY()-getY()) < 4)
                            clear = false;
                    if (clear){
                        SlimeTrail trail = new SlimeTrail(getScene());
                        trail.setX(getX());
                        trail.setY(getY());
                        getScene().addSprite(trail);
                        trailDelay = maxTrailDelay;
                    }
                }

                if (hopTime == 0){
                    stopTime = maxStopTime+(int)(Math.random()*10);
                }
            }
            else if (stopTime > 0){
                stopTime--;

                if (stopTime == 0){
                    if (Math.random() < 0.025f){
                        mitosisTime = maxMitosisTime;
                    }
                    else{
                        hopTime = (int)(Math.random()*maxHopTime/2f+maxHopTime/2f);
                        setHeading(Math.random()*(Math.PI*2));
                    }
                }
            }
            else if (mitosisTime > 0){
                mitosisTime--;

                if (mitosisTime == 0){
                    getScene().removeSprite(this);

                    Fireslime child1 = new Fireslime("fireslime", getScene(), 0, 0);
                    child1.setX(getX()-8);
                    child1.setY(getY());
                    if (getScene().propsBlockingMovement(child1.getFootprint()).size() == 0)
                        getScene().addSprite(child1);

                    Fireslime child2 = new Fireslime("fireslime", getScene(), 0, 0);
                    child2.setX(getX()+9);
                    child2.setY(getY());
                    if (getScene().propsBlockingMovement(child2.getFootprint()).size() == 0)
                        getScene().addSprite(child2);

                    getScene().getSound().play(Art.getSample("bloop.wav"), this, 1, 1, 1);
                }
            }

            setMoving(hopTime > 0 && damageTime == 0);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
            stopTime = maxStopTime;
            hopTime = 0;
            mitosisTime = 0;
            trailDelay = 0;
        }

        super.move();

        calcPic();
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
        FireField ff = new FireField("firefield", getScene(), 48, this, true, -1);
        ff.setX(getX());
        ff.setY(getY());
        getScene().addSprite(ff);
    }

    public void damage(DamageAttributes attributes){
        if (attributes.getAttributes().contains(Attribute.Fire)) return;

        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp){
            damageTime = maxDamageTime;
        }
    }

}

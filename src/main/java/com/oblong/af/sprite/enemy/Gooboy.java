package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;

/**
 * Rabides hop around, stopping and blinking for random amounts of time between hops.  If activated, they chase the player and
 *   attempt to tongue him to death.
 */

public class Gooboy extends Prop {

    private int spawnTime = 0, maxSpawnTime = 8;
    private int moveTime = 0, maxMoveTime = 32;
    private int stopTime = 0, maxStopTime = 10;
    private int attackTime = 0, maxAttackTime = 8;
    private int damageTime = 0, maxDamageTime = 8;
    private int bubbleTime = 0, maxBubbleTime = 64;
    private boolean black;
    private boolean angered = false;

    public Gooboy(String id, AreaScene scene, int oxPic, int oyPic, boolean black) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.gooboy);
        setWPic(16);
        setHPic(32);
        setXPicO(0);
        setYPicO(2);
        spawnTime = maxSpawnTime;
        this.black = black;
        setDiesOnCollideWithPlayer(true);
        setCanBeKnockedback(true);
        setCollidable(false);

        double randpow = Math.random();
        if (black){
            if (randpow < 0.02){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Lifespark);
            }
            else if (randpow < 0.08){
                setPowerupDrop(Powerups.BigHpPotion);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.PoisonPotion);
            }
        }
        else{
            if (randpow < 0.08){
                setPowerupDrop(Powerups.AbilityGem);
                setAbilityDrop(Ability.Waterproof);
            }
            else if (randpow < 0.2){
                setPowerupDrop(Powerups.HpPotion);
            }
        }
    }

    public void calcPic(){
        int xOff = 0, yOff = 0;
        switch(getFacing()){
            case LEFT: yOff = 1; break;
            case RIGHT: yOff = 0; break;
            case UP: yOff = 3; break;
            case DOWN: default: yOff = 2; break;
        }

        if (isDead()){
            if (bubbleTime > 0){
                xOff = 6-getDeadCounter();
                yOff = 5;
            }
            else{
                xOff = 4-getDeadCounter();
                yOff = 4;
            }
        }
        else if (bubbleTime > 0){
            xOff = (getTick()/4)%3;
            yOff = 5;
        }
        else if (damageTime > 0){
            xOff = 6;
        }
        else if (stopTime > 0) xOff = 3+(getTick()/16)%2;
        else if (moveTime > 0){
            xOff = (getTick()/4)%3;
        }
        else if (attackTime > 0 || spawnTime > 0){
            xOff = 5;
            if (getXa() < 0) yOff = 1;
            else yOff = 0;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

    protected float determineSpeed(){
        if (angered || spawnTime > 0) return 3f;
        else return 0.5f;
    }

    private double headingTowardPlayer(){
        if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) return Math.random()*2*Math.PI;

        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (!isDead()){
                double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
                if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;

                if (!angered && distance < 128){
                    if (!angered) setSurprisedTime(16);
                    angered = true;
                }

                if (bubbleTime > 0){
                    bubbleTime--;

                    if (bubbleTime == 0){
                        getScene().getSound().play(Art.getSample("splat.wav"), this, 1, 1, 1);
                        for (int i = 0; i < 5; i++){
                            boolean black = Math.random() < 0.5f;
                            Gooboy boy = new Gooboy("gooboy", getScene(), 0, black ? 5 : 0, black);
                            SpriteDefinitions def = black ? SpriteDefinitions.BlackGooboy : SpriteDefinitions.BlueGooboy;
                            boy.setMaxHp(def.getMaxHp());
                            boy.setHp(def.getMaxHp());
                            boy.setHeading(Math.random()*Math.PI*2);
                            boy.setX(getX());
                            boy.setY(getY());
                            getScene().addSprite(boy);
                        }
                        die();
                    }
                }
                else if (damageTime > 0){
                    damageTime--;
                }
                else if (moveTime > 0){
                    moveTime--;

                    setHeading(headingTowardPlayer());

                    //if we get within attacking range of player while moving and are angry, cancel this and do our attack
                    if (angered && distance < 36d){
                        attackTime = maxAttackTime;
                        moveTime = 0;
                    }
                    else if (moveTime == 0){
                        if (angered) moveTime = maxMoveTime;
                        else stopTime = maxStopTime+(int)(Math.random()*10);
                    }
                }
                else if (stopTime > 0){
                    stopTime--;

                    if (angered){
                        stopTime = 0;
                        moveTime = maxMoveTime;
                    }
                    else if (stopTime == 0){
                        moveTime = maxMoveTime;
                        setHeading(Math.random()*(Math.PI*2));
                    }
                }
                else if (spawnTime > 0){
                    spawnTime--;

                    //set yoffset as a parabola function of attacktime as it goes from maxAttackTime-2 to 0
                    //y = -x^2+h, and x = (maxAttackTime-2)/2-attackTime
                    float h = 24f;
                    float x = maxSpawnTime/2f-spawnTime;
                    float y = (float)(h-Math.pow(x, 2));
                    setYPicO((int)y);

                    if (spawnTime == 0){
                        if (distance < 36) attackTime = maxAttackTime;
                        else moveTime = maxMoveTime;
                    }
                }
                else if (attackTime > 0){
                    attackTime--;

                    if (attackTime == maxAttackTime-1)
                        getScene().getSound().play(Art.getSample("laugh.wav"), this, 1, 1, 1);

                    setHeading(headingTowardPlayer());

                    if (attackTime > maxAttackTime-2) ; //just pause
                    else{
                        //set yoffset as a parabola function of attacktime as it goes from maxAttackTime-2 to 0
                        //y = -x^2+h, and x = (maxAttackTime-2)/2-attackTime
                        float h = 24f;
                        float x = (maxAttackTime-2)/2f-attackTime;
                        float y = (float)(h-Math.pow(x, 2));
                        setYPicO((int)y);
                    }

                    if (attackTime == 0){
                        if (distance < 36) attackTime = maxAttackTime;
                        else moveTime = maxMoveTime;
                    }
                }
            }

            if (spawnTime == 0 && (attackTime == 0 || attackTime >= maxAttackTime-2)) setYPicO(2);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }

        setMoving((moveTime > 0 || attackTime > 0 || spawnTime > 0) && damageTime == 0 && !isDead());

        setCollidable(spawnTime == 0);

        if (attackTime > 0 || spawnTime > 0)
            setImpactDamageAttributes(new DamageAttributes(1, Arrays.asList(Attribute.Knockback)));
        else setImpactDamageAttributes(new DamageAttributes(0, Arrays.asList(Attribute.Knockback)));

        super.move();

        calcPic();
    }

    public void die(){
        super.die();
        setDeadCounter(4);
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp && !isDead()){
            damageTime = maxDamageTime;
            if (!angered) setSurprisedTime(16);
            angered = true;

            if (black && bubbleTime == 0){
                bubbleTime = maxBubbleTime;
                setMaxHp(10);
                setHp(getMaxHp());
            }
        }
    }

    public void render(Graphics2D og, float alpha){
        //do custom shadow
        if (!isDead()){
            int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
            int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha);
            og.drawImage(getSheet()[5][2], xPixel-8, yPixel-32, null);
        }

        super.render(og, alpha);
    }

}

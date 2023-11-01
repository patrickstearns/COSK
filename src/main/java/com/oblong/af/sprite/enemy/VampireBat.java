package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Facing;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.thing.FlameBasin;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

/**
 * The bats emitted by Vampires.
 */

public class VampireBat extends Prop {

    private int moveTime = 0, maxMoveTime = 8;

    public VampireBat(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.bat);
        setWPic(32);
        setHPic(32);
        setXPicO(0);
        setYPicO(0);
        setSpeed(2);
        setMaxHp(3);
        setHp(3);

        setShadowYOffset(0);
        setShadowVisible(true);
        setCanBeKnockedback(true);
        setSuppressHpMeter(true);

        setHeading(Math.random() * 2 * Math.PI);
        setMoving(true);
        setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Freeze)));

        moveTime = maxMoveTime;
    }

    public void calcPic(){
        int xOff, yOff;

        if (getXa() < 0){
            if (getYa() < 0) yOff = 3;
            else yOff = 1;
        }
        else{
            if (getYa() < 0) yOff = 2;
            else yOff = 0;
        }

        //if we're attacking, usually
        if (getXa() == 0 && getYa() == 0){
            switch (Facing.nearestFacing(getHeading())) {
                case LEFT: yOff = 3; break;
                case RIGHT: yOff = 0; break;
                case UP: yOff = 2; break;
                case DOWN: yOff = 1; break;
            }
        }

        if (isDead()){
            xOff = 4;
            if (yOff == 1 || yOff == 3) yOff = 1;
            else yOff = 0;
        }
        else xOff = (getTick()/2)%3;

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

    private double headingTowardLantern(){
        //figure list of lit torches
        FlameBasin closestLitTorch = null;
        double sdist = Double.MAX_VALUE;
        for (Sprite sprite: getScene().getSprites()){
            if (sprite.isActive() && sprite instanceof FlameBasin){
                FlameBasin torch = (FlameBasin)sprite;
                double tdist = Point.distance(getX(), getY(), torch.getX(), torch.getY());
                if (tdist < sdist){
                    sdist = tdist;
                    closestLitTorch = torch;
                }
            }
        }

        //if there is no lit torch, go for the player
        Prop target;
        if (closestLitTorch == null) target = getScene().player;
        else target = closestLitTorch;

        //angle to target
        double xDiff = target.getX()-getX();
        double yDiff = target.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public float determineSpeed(){ return 2; }

    public void move(){
        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (moveTime > 0){
                moveTime--;
                if (moveTime == 0){
                    setHeading(headingTowardLantern());
                    moveTime = maxMoveTime;
                }
            }

            setMoving(true);
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }

        super.move();

        calcPic();
        setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.CYAN, Color.BLUE.darker().darker()}, 10));

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("flap.wav"), this, 0.1f, 1, 0.2f);
        if (Math.random() < 0.01)
            getScene().getSound().play(Art.getSample("rat.wav"), this, 1, 1, 1);
    }

    public void die(){
        super.die();
        getScene().getSound().play(Art.getSample("squash1.wav"), this, 1, 1, 1);
    }
}

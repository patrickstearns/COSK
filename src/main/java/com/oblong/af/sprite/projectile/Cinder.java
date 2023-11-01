package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.sprite.thing.Thing;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.util.Arrays;

public class Cinder extends Thing {

    private int altitude = 120;

    public Cinder(AreaScene scene, int x, int y){
        super("cinder" + Math.random(), scene, x, y, Art.effects16x16, 0, 17, 16, 16, 16, 16, false, false);
        setYPicO(altitude);
        setCollidable(false);
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Physical, Attribute.Fire)));
        setShadowVisible(true);
    }

    public void move(){
        if (altitude == 0){
            die();
            return;
        }
        else{
            altitude-=4;
            setYPicO(altitude);
            setShadowYOffset(altitude);

            if (altitude <= 16){
                setBlockable(true);
                setBlocksFlying(true);
                setBlocksMovement(true);
                setDiesOnCollide(true);
                setCollidable(true);
            }
        }

        getScene().addSprite(new Puff(getScene(), (int)getX(), (int)(getY()-altitude)));

        setXPic((getTick()/2)%4);

        setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.RED, ImageUtils.TRANSPARENT}, 10));

        if (getTick() < 16) setFadeRatio((float)getTick()/16f);
        else setFadeRatio(1f);
    }

    public void die(){
        super.die();
        getScene().addSprite(new ExplodeEffect(getScene(), (int) getX(), (int) getY()));
        getScene().removeSprite(this);
        getScene().getSound().play(Art.getSample("cinder.wav"), new FixedSoundSource(getX(), getY()), 1, 1, 1);
    }
}

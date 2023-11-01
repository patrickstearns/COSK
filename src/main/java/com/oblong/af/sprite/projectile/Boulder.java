package com.oblong.af.sprite.projectile;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.sprite.thing.Thing;
import com.oblong.af.util.Art;

import java.util.Arrays;

public class Boulder extends Thing {

    private int altitude = 120;

    public Boulder(AreaScene scene, int x, int y){
        super("boulder" + Math.random(), scene, x, y, Art.effects32x32, 0, 13, 32, 32, 32, 32, false, false);
        setYPicO(altitude);
        setCollidable(false);
        setImpactDamageAttributes(new DamageAttributes(3, Arrays.asList(Attribute.Physical, Attribute.Earth)));
        setShadowVisible(true);
    }

    public void move(){
        if (altitude == 0){
            die();
            return;
        }
        else{
            altitude-=8;
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

        setXPic((getTick()/2)%4);

        if (getTick() < 16) setFadeRatio((float)getTick()/16f);
        else setFadeRatio(1f);
    }

    public void die(){
        super.die();
        getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));
        getScene().removeSprite(this);
    }
}

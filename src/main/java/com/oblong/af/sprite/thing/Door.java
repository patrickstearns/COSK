package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

public class Door extends Barricade {
    public Door(String id, AreaScene scene, int oxPic, int oyPic){
        super(id, scene, oxPic, oyPic);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setShadowVisible(false);
    }

    public void move(){
        super.move();
        setBlocksMovement(!isActive() || frameX != animLength-1);
        setCollidable(isActive() && frameX != 0);

        if (isActive() && frameX < animLength-1)
            getScene().getSound().play(Art.getSample("creak.wav"), this, 0.4f, 1, 1);
        else if (!isActive() && frameX > 0)
            getScene().getSound().play(Art.getSample("creak.wav"), this, 0.4f, 1, 1);
    }
}

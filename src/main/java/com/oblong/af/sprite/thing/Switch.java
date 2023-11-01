package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

/**
 * Pressure plates, buttons, switches, and other two-state actors with only two sprite.
 */

public class Switch extends Prop {

    protected int frameX, frameY, animLength;

    public Switch(String id, AreaScene scene, int oxPic, int oyPic, int animLength) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.objects16x32);
        frameX = 0;
        frameY = 0;
        setWidth(15);
        setHeight(16);
        this.animLength = animLength;

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);

        setShadowVisible(false);
    }

    public void move(){
        super.move();

        //if we just changed state
        if (isActive() && frameX < animLength-1){
            frameX++;
        }
        else if (!isActive() && frameX > 0){
            frameX--;
        }
        frameY = 0;
    }

    public void setActive(boolean active){
        if (!isActive() && active) getScene().getSound().play(Art.getSample("click.wav"), this, 1, 1, 1);
        else if (isActive() && !active) getScene().getSound().play(Art.getSample("click.wav"), this, 1, 1, 1);
        super.setActive(active);
    }

    protected void calcPic(){
        setXPic(getOxPic()+frameX);
        setYPic(getOyPic()+frameY);
    }

}

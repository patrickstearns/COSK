package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class Candle extends Prop {

    protected int frameX, frameY, animLength;

    public Candle(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setSheet(Art.objects16x32);
        setOxPic(oxPic);
        setOyPic(oyPic);
        frameX = 0;
        frameY = 0;
        animLength = 6;
        setWidth(15);
        setHeight(16);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setShadowVisible(false);
    }

    boolean lit = false;
    public void move(){
        super.move();

        if (!isActive()) lit = false;
        if (lit){
            frameX = (getTick())%animLength;
            frameY = 1;
        }
        else frameY = 0;

        //if we just changed state
        if (isActive() && frameX < animLength-1){

            frameX++;
            if (frameX == animLength-1){
                lit = true;
            }
        }
        else if (!isActive() && frameX > 0)
            frameX--;

//        if (isActive()){
//            frameX = (getTick()/2)%animLength;
//            frameY = 1;
//        }
//        else frameY = 0;

        calcPic();
    }

    public void setActive(boolean active){
        if (!isActive() && active && getScene().getSound() != null) getScene().getSound().play(Art.getSample("fireburst.wav"), this, 1, 1, 1);
        super.setActive(active);
    }

    protected void calcPic(){
        setXPic(getOxPic()+frameX);
        setYPic(getOyPic()+frameY);
    }

}

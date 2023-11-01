package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class EnergyBarricade extends Prop {

    protected int frameX, frameY, animLength;

    public EnergyBarricade(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        setActive(true);
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

        setLayer(Area.Layer.Lower);
    }

    public void move(){
        super.move();

        //if we just changed state
        if (isActive() && frameX < animLength-1)
            frameX++;
        else if (!isActive() && frameX > 0)
            frameX--;
        frameY = 0;

        if (isActive() && getTick()%12 == 0)
            getScene().getSound().play(Art.getSample("electricHum.wav"), this, 0.5f, 1, 1);

//        if (isActive()){
//            frameX = (getTick()/2)%animLength;
//            frameY = 1;
//        }
//        else frameY = 0;

        setBlocksMovement(isActive() && frameX != 0);
        setCollidable(isActive() && frameX != 0);
        setLayer(isActive() ? Area.Layer.Main : Area.Layer.Lower);

        calcPic();
    }

    protected void calcPic(){
        setXPic(getOxPic()+frameX);
        setYPic(getOyPic()+frameY);
    }

}

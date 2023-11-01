package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.Art;

public class Barricade extends Prop {

    protected int frameX, frameY, animLength;

    public Barricade(String id, AreaScene scene, int oxPic, int oyPic) {
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

        setLayer(Area.Layer.Lower); //barricades are always on the lower level

        if (isActive()) frameX = animLength-1;
    }

    public void receiveMessage(Sprite source, String message){
        if (!isActive() && getMessages().getActivatingMessages().contains(message)) setActive(true);
        else if (isActive() && getMessages().getDeactivatingMessages().contains(message)) setActive(false);
    }

    public void move(){
        super.move();

        //if we just changed state
        if (isActive() && frameX < animLength-1)
            frameX++;
        else if (!isActive() && frameX > 0)
            frameX--;
        frameY = 0;

        if (isActive() && frameX < animLength-1)
            getScene().getSound().play(Art.getSample("schink.wav"), this, 0.4f, 1, 1);
        else if (!isActive() && frameX > 0)
            getScene().getSound().play(Art.getSample("schink.wav"), this, 0.4f, 1, 1);

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

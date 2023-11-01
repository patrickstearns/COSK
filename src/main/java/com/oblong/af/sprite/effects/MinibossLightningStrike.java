package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class MinibossLightningStrike extends Effect {

    private Prop parent;

    public MinibossLightningStrike(AreaScene scene, Prop parent){
        super(scene, (int)parent.getX(), (int)parent.getY(), Area.Layer.Main, 0, 0, 0, 0, 16, 96, 4, true);
        setSheet(Art.purpleLightning32x32);
        this.parent = parent;
    }

    public void move(){
        setX(parent.getX());
        setY(parent.getY());

        setXPic(getTick()/3);
        setYPic(Math.random() < 0.5 ? 0 : 1);
        if (getXPic() > maxTick) getScene().removeSprite(this);
    }
}

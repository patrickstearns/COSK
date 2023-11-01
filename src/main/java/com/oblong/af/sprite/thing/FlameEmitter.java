package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.projectile.FlameSpurt;
import com.oblong.af.util.Art;

import java.awt.*;

public class FlameEmitter extends Thing {

    private double heading;

    public FlameEmitter(AreaScene scene, int x, int y, int yPic, double heading){
        super("flamer"+Math.random(), scene, x, y, Art.editorIcons, 12, yPic, 16, 16, 16, 16, false, false);
        setOutlineColor(Color.WHITE);
        this.heading = heading;
        setShadowVisible(false);
    }

    public void move(){
        if (isActive()) flame(getScene());
    }

    private void flame(AreaScene scene){
        int num = (int)(Math.random()*3);
        for (int i = 0; i < num; i++){
            getScene().addSprite(new FlameSpurt(scene, this, heading-Math.PI/8f+Math.random()*Math.PI/4f, 6));
        }
    }

    public void render(Graphics2D og, float alpha){
        if (AreaGroupRenderer.renderBehaviors) super.render(og, alpha);
    }
}

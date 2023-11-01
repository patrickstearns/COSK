package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.projectile.Gust;
import com.oblong.af.util.Art;

import java.awt.*;

public class GustEmitter extends Thing {

    private double heading;

    public GustEmitter(AreaScene scene, int x, int y, int yPic, double heading){
        super("guster"+Math.random(), scene, x, y, Art.editorIcons, 11, yPic, 16, 16, 16, 16, false, false);
        setOutlineColor(Color.WHITE);
        this.heading = heading;
    }

    public void move(){
        if (isActive()) gust(getScene());
    }

    private void gust(AreaScene scene){
        int num = (int)(Math.random()*3);
        for (int i = 0; i < num; i++)
            scene.addSprite(new Gust(scene, this, heading-0.1f+0.2f*Math.random()));
    }

    public void render(Graphics2D og, float alpha){
        if (AreaGroupRenderer.renderBehaviors) super.render(og, alpha);
    }
}

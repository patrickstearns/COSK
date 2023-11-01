package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.util.Art;

import java.awt.*;

public class SmokeEmitter extends Thing {

    public SmokeEmitter(AreaScene scene, int x, int y){
        super("smokey"+Math.random(), scene, x, y, Art.editorIcons, 11, 0, 16, 16, 16, 16, false, false);
        setOutlineColor(Color.WHITE);
        getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
    }

    public void move(){
        if (isActive()) puff(getScene());
    }

    private void puff(AreaScene scene){
        int num = (int)(Math.random()*3);
        for (int i = 0; i < num; i++)
            scene.addSprite(new Puff(scene, (int)((getX()-getWidth()/2)+getWidth()*Math.random()), (int)(getY()-Math.random()*getHeight())));
    }

    public void render(Graphics2D og, float alpha){
        if (AreaGroupRenderer.renderBehaviors) super.render(og, alpha);
    }
}

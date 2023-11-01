package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class GlowShroom extends Thing {

    private int glowCounter = 0;

    public GlowShroom(AreaScene scene, int x, int y, int yPic){
        super("glowshroom"+Math.random(), scene, x, y, Art.objects32x32x2, 6, yPic, 32, 32, 32, 16, true, false);
        setImmuneToDamage(false);
        setMaxHp(24);
        setHp(24);
        setSuppressHpMeter(true);
    }

    public void move(){
        if (glowCounter > 0){
            glowCounter--;
            setActive(false);
        }
    }

    public void damage(DamageAttributes damageAttributes){
        super.damage(damageAttributes);
        if (damageAttributes.getAttributes().contains(Attribute.Spirit)){
            glowCounter = 64;
            setActive(true);

            if (getYPic() == 0) getScene().getSound().play(Art.getSample("shroom1.wav"), this, 1, 1, 1);
            else if (getYPic() == 1) getScene().getSound().play(Art.getSample("shroom2.wav"), this, 1, 1, 1);
            else if (getYPic() == 2) getScene().getSound().play(Art.getSample("shroom3.wav"), this, 1, 1, 1);
        }
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        if (glowCounter > 0){
            Rectangle imageBounds = getImageFootprint(alpha);
            Image image = getSheet()[getXPic()+1][getYPic()];
            image = ImageUtils.outlineImage(image, Color.CYAN);
            if (glowCounter < 16) image = ImageUtils.fadeImage(image, null, (float)glowCounter/16f);
            og.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
        }
    }
}

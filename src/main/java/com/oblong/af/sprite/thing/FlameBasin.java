package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.util.Art;

import java.awt.*;

public class FlameBasin extends Thing {

    private int glowCounter = 0;

    public FlameBasin(AreaScene scene, int x, int y){
        super("flameBasin" + Math.random(), scene, x, y, Art.objects16x32, 8, 6, 16, 32, 16, 16, true, false);
        setImmuneToDamage(false);
        setMaxHp(Integer.MAX_VALUE);
        setHp(Integer.MAX_VALUE);
        setSuppressHpMeter(true);
        setShadowVisible(false);
    }

    public void move(){
        if (glowCounter > 0)
            glowCounter--;
        setActive(glowCounter > 0);

        if (glowCounter > 0 && glowCounter%6 == 0)
            getScene().getSound().play(Art.getSample("sizzle.wav"), this, 1, 1, 1);
    }

    public void damage(DamageAttributes damageAttributes){
        super.damage(damageAttributes);
        if (damageAttributes.getAttributes().contains(Attribute.Fire)){
            glowCounter = 128;
        }
        else if (damageAttributes.getAttributes().contains(Attribute.Water) || damageAttributes.getAttributes().contains(Attribute.Freeze)){
            glowCounter = Math.min(glowCounter, 3);
        }
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        if (glowCounter > 0){
            int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
            int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();

            //glow
            og.setColor(new Color(1f, 1f, 0.5f, 0.3f));
            int xOff = (int)(Math.random()*6)-3;
            int yOff = (int)(Math.random()*6)-3;
            int r = 64;
            if (glowCounter < 6) r = 10*glowCounter;
            og.fillOval(xPixel-r+xOff, yPixel-r+yOff-6, r*2, r*2);

            //flame image
            Image flameImage = Art.objects16x32[6+glowCounter%6][4];
            if (glowCounter < 6) flameImage = Art.objects16x32[12-glowCounter%6][5];
            og.drawImage(flameImage, xPixel-8, yPixel-38, null);
        }
    }
}

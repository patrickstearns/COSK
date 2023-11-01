package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.ArrayList;

public class IceShield extends Thing {

    private int brokenTime = 0, maxBrokenTime = 128;

    private static int calcXOff(int index){
        if (index == 0) return -52;
        else if (index == 1) return -40;
        else if (index == 2) return -24;
        else if (index == 3) return -8;
        else if (index == 4) return -0;
        else if (index == 5) return 8;
        else if (index == 6) return 24;
        else if (index == 7) return 40;
        else if (index == 8) return 52;
        else return 0;
    }

    private static int calcYOff(int index){
        if (index == 0 || index == 8) return -47;
        else if (index == 1 || index == 7) return -18;
        else if (index == 2 || index == 6) return -4;
        else if (index == 3 || index == 5) return 0;
        else return 1;
    }

    public IceShield(AreaScene scene, Prop parent, int index){
        super("IceShield", scene, (int)(parent.getX()+calcXOff(index)), (int)(parent.getY()+8),
                Art.bossSpider32x48, index, 0, 32, 48, 16, 24, true, false);
        setMaxHp(16);
        setHp(16);
        setYPicO(-calcYOff(index));
        setBlocksMovement(true);
        setBlocksFlying(true);
        setSuppressHpMeter(false);
        setImmuneToDamage(false);
        setImmuneToStatusEffects(true);
        setFadeRatio(0.9f);
        setShadowVisible(false);
    }

    //noop'd
    protected void calcPic(){
        setYPic(brokenTime > 0 ? 1 : 0);
    }

    public void move(){
        super.move();
        getDecorators().clear(); //clear any transparency due to invulnerability

        if (brokenTime > 0){
            brokenTime--;
            if (brokenTime == 0){
                setHp(getMaxHp());
                twinkle(20);
            }
        }

        if (Math.random() < 0.1 && brokenTime == 0) twinkle(1);

        setFadeRatio(0.5f+0.4f*(getHp()/(float)getMaxHp()));
        if (brokenTime > 0){
            if (brokenTime > 4) setFadeRatio(0.5f);
            else setFadeRatio(0.5f+0.1f*(4-brokenTime));
        }
    }

    public void die(){
        brokenTime = maxBrokenTime;
        twinkle(20);
        if (getYPic() == 0) getScene().getSound().play(Art.getSample("iceCrash.wav"), this, 1, 1, 1);
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() != hp){
            twinkle(4);
        }
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes.getDamage(), new ArrayList<Attribute>(attributes.getAttributes()));
        if (attributes.getAttributes().contains(Attribute.Fire)) modified.setDamage(modified.getDamage()*2);
        return modified;
    }

    private void twinkle(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int x = (int)(getX()-getWidth()/2+Math.random()*getWPic());
            int y = (int)(getY()-Math.random()*getHPic()-getYPicO());
            getScene().addSprite(new Sparkle(getScene(), x, y, Color.WHITE, 0, 0));
        }
    }
}

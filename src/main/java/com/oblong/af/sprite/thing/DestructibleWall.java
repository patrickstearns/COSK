package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.ArrayList;

public class DestructibleWall extends Thing {

    private int deadTick = 0;
    private boolean icewall = false;

    public DestructibleWall(AreaScene scene, int x, int y, boolean icewall){
        super("wall" + Math.random(), scene, x, y, Art.objects16x48, 0, icewall ? 0 : 1, 16, 48, 16, 16, true, false);

        setHp(16);
        setMaxHp(16);
        setImmuneToDamage(false);
        setSuppressHpMeter(true);
        this.icewall = icewall;
        setShadowVisible(false);
    }

    public void move(){
        super.move();
        getDecorators().clear(); //clear any transparency due to invulnerability

        if (icewall && Math.random() < 0.1 && deadTick >= 0) twinkle(1);

        if (deadTick > 0){
            setXPic((getTick()-deadTick)/2);
            if (getTick()-deadTick > 10){
                deadTick = -1;
                setCollidable(false);
                setXPic(5);
                puff(20, null);
                setLayer(Area.Layer.Lower);
            }
        }
    }

    public void die(){
        deadTick = getTick();
        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);

        if (icewall) getScene().getSound().play(Art.getSample("iceCrash.wav"), this, 1, 1, 1);
        else getScene().getSound().play(Art.getSample("explosion.wav"), this, 1, 1, 1);
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() != hp){
            puff(4, null);
        }
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes.getDamage(), new ArrayList<Attribute>(attributes.getAttributes()));
        if (icewall && !attributes.getAttributes().contains(Attribute.Fire)) modified.setDamage(0);
        if (!icewall && !attributes.getAttributes().contains(Attribute.Physical)) modified.setDamage(0);
        return modified;
    }

    private void twinkle(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int x = (int)(getX()-getWidth()/2+Math.random()*getWPic());
            int y = (int)(getY()-Math.random()*getHPic());
            getScene().addSprite(new Sparkle(getScene(), x, y, Color.WHITE, 0, 0));
        }
    }
}

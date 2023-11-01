package com.oblong.af.sprite.thing;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Glasscane extends Prop {

    private class Spike implements Comparable<Spike>{
        int x, y, length, brokenTime, variant, maxBrokenTime;

        public Spike(){
            x = (int)(Math.random()*16);
            y = (int)(Math.random()*16);
            length = 3;
            brokenTime = 0;
            variant = (int)(Math.random()*3);
            if (variant == 3) variant = 2;
            maxBrokenTime = 50+(int)(Math.random()*8);
        }

        public int compareTo(Spike other){
            return y-other.y;
        }
    }

    private List<Spike> spikes;

    public Glasscane(String id, AreaScene scene, int oxPic, int oyPic) {
        super(id, scene, oxPic, oyPic);
        spikes = new ArrayList<Spike>();
        setSheet(Art.objects16x32);

        for (int i = 0; i < 5; i++)
            spikes.add(new Spike());
        Collections.sort(spikes);

        setImmuneToDamage(false);
        setImmuneToStatusEffects(true);
        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(false);
        setBlockableByScreenEdge(false);
        setImpactDamagesPlayerOnly(false);
        setDiesOnCollideWithPlayer(false);
        setShadowVisible(false);

        updateState();
    }

    private void updateState(){
        int maxLength = 0, moreThanOneCount = 0;
        for (Spike spike: spikes){
            if (spike.length > maxLength)
                maxLength = spike.length;
            if (spike.length > 1) moreThanOneCount++;
        }

        if (maxLength == 0 || maxLength == 1){
            setBlocksMovement(false);
            setBlocksFlying(false);
            setCollidable(false);
            setBlockable(false);
            setDiesOnCollide(false);
            setLayer(Area.Layer.Lower);
        }
        else if (maxLength == 2){
            setBlocksMovement(true);
            setBlocksFlying(false);
            setCollidable(true);
            setBlockable(true);
            setDiesOnCollide(true);
            setLayer(Area.Layer.Main);
        }
        else{
            setBlocksMovement(true);
            setBlocksFlying(true);
            setCollidable(true);
            setBlockable(true);
            setDiesOnCollide(false);
            setLayer(Area.Layer.Main);
        }

        if (moreThanOneCount > 0 && maxLength < 3)
            setImpactDamageAttributes(new DamageAttributes(moreThanOneCount, Arrays.asList(Attribute.Physical)));
        else setImpactDamageAttributes(null);
    }

    public void move(){
        super.move();

        for (Spike spike: spikes){
            if (spike.brokenTime > 0){
                spike.brokenTime--;
                if (spike.brokenTime == 0)
                    getScene().getSound().play(Art.getSample("whiff.wav"), this, 0.1f, 1, 1);
            }
            else if (spike.length < 3) spike.length++;
        }

        for (Prop prop: getScene().propsColliding(this, getFootprint())){
            if (prop.isBlocksMovement()){
                if (getImpactDamageAttributes() != null){
                    prop.damage(getImpactDamageAttributes());
                    breakSpikes();
                }
            }
        }

        updateState();
    }

    public void damage(DamageAttributes attributes){
        if (isDead()) return;
        breakSpikes();
    }

    public void die(){
        breakSpikes();
    }

    private void breakSpikes(){
        boolean brokeOne = false;
        for (Spike spike: spikes){
            if (spike.length > 0) brokeOne = true;
            spike.brokenTime = spike.maxBrokenTime;
            spike.length = 0;
        }
        if (brokeOne) getScene().getSound().play(Art.getSample("iceCrash.wav"), this, 1, 1, 1);
    }

    public void render(Graphics2D og, float alpha){
        for (Spike spike: spikes){
            int xPic = spike.length;
            int yPic = 11;
            if (spike.length > 0) xPic += spike.variant*3;

            if (spike.brokenTime > 0){
                int bt = spike.maxBrokenTime-spike.brokenTime;
                if (bt < 6) xPic = 10+bt;
            }

            int xPixel = (int)(getX()+spike.x-getWPic()/2-getWidth()/2);
            int yPixel = (int)(getY()+spike.y-getHPic()-getHeight());
            og.drawImage(getSheet()[xPic][yPic], xPixel, yPixel, null);
        }
    }

}

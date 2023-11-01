package com.oblong.af.sprite;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Ability;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.Powerups;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;

import java.awt.*;

public class Powerup extends Prop {

	private Powerups powerupDefinition;
    private Ability learnedAbility;

	public Powerup(AreaScene scene, Powerups powerupDefinition, Ability learnedAbility, int x, int y){
		super(powerupDefinition.name(), scene, powerupDefinition.getXPic(), powerupDefinition.getYPic());
		setPowerupDefinition(powerupDefinition);
        setLearnedAbility(learnedAbility);
        setSheet(Art.powerups);
        setXPic(powerupDefinition.getXPic());
        setYPic(powerupDefinition.getYPic());
        setWPic(16);
        setHPic(16);
		setX(x);
		setY(y);
		setWidth(16);
		setHeight(16);
		setXa(0);
		setYa(0);
        setCollidable(true);
        setCollidesWithPlayerOnly(true);
        setDiesOnCollideWithPlayer(true);
        setImpactDamagesPlayerOnly(true);
        setBlocksMovement(false);
        setBlocksFlying(false);
        setBlockableByScreenEdge(false);
        setBlockable(false);
        setCanBeKnockedback(false);
        setRenderingOrder(1);
        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);

        if (learnedAbility != null) setImpactDamageAttributes(new DamageAttributes(learnedAbility));
        else setImpactDamageAttributes(new DamageAttributes(powerupDefinition.getHpGain(), powerupDefinition.getAttributes()));

        if (powerupDefinition == Powerups.HpPotion || powerupDefinition == Powerups.BigHpPotion)
            setShadowVisible(false);
    }

    public Powerups getPowerupDefinition(){ return powerupDefinition; }
	public void setPowerupDefinition(Powerups powerupDefinition){ this.powerupDefinition = powerupDefinition; }

    public Ability getLearnedAbility(){ return learnedAbility; }
    public void setLearnedAbility(Ability learnedAbility){ this.learnedAbility = learnedAbility; }

    protected void calcPic(){
    	int xOff = (getTick()/5)%4;
        if (getPowerupDefinition() == Powerups.AffinityGem) xOff = getTick()%4;
    	int xPic = powerupDefinition.getXPic()+xOff, yPic = powerupDefinition.getYPic();
        setXPic(xPic);
        setYPic(yPic);
    }

    public void move(){
        calcPic();

        if (learnedAbility != null){
            setOutlineColor(learnedAbility.getPrimaryAffinity().getColor());
            setTintColor(learnedAbility.getPrimaryAffinity().getColor());
        }

        move(getXa(), 0);
        move(0, getYa());
    }

    public void die(){
//        super.die();

        //remove this sprite immediately, just leave sparkles behind
        getScene().removeSprite(this);
        setDead(true);

        //create sparkles
        for (int xx = 0; xx < 1; xx++)
            for (int yy = 0; yy < 1; yy++)
                getScene().addSprite(new Sparkle(getScene(), (int)getX() + xx * 8 + (int) (Math.random() * 8), (int)getY() + yy * 8 + (int) (Math.random() * 8),
                        Color.WHITE, 0, 0));
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;
        if (getFadeRatio() == 0f) return;

        super.render(og, alpha);

        if (getLearnedAbility() != null){
            //base image location
            int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
            int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
            int dwPic = 8;
            Image icon = getLearnedAbility().getBigIcon();

            //above here is just super version
            og.drawImage(icon, xPixel-dwPic/2, yPixel-12, xPixel-dwPic/2+8, yPixel-12+dwPic, 0, 0, icon.getWidth(null), icon.getHeight(null), null);
        }
    }

}

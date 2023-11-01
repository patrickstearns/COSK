package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.projectile.FallingIceSpike;
import com.oblong.af.sprite.projectile.PoisonSpit;
import com.oblong.af.sprite.projectile.Silk;
import com.oblong.af.sprite.thing.IceShield;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * A giant spider partially embedded in ice.  Normally just sits there with little legs wiggling, occasionally shooting
 *   out loads of tiny freezing bullets.  Ice is destructible but regrows.  Rarely, spider convulses and breaks all the
 *   ice herself, then a bunch of spiders pop out; is more vulnerable to damage when birthing.
 * There are also little holes around the room that work like beetle generators, but for spiders, and release them less
 *   often.  Earth damage can collapse them permanently.
 */

public class SpiderQueen extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "SpiderQueen";

    private int waitTime = 0, minWaitTime = 16, maxWaitTime = 64;
    private int silkTime = 0, maxSilkTime = 16;
    private int spitTime = 0, maxSpitTime = 32;
    private int shakeTime = 0, maxShakeTime = 32;
    private int birthTime = 0, maxBirthTime = 32;
    private int damageTime = 0, maxDamageTime = 8;
    private boolean hasCreatedIceShields = false;
    private java.util.List<IceShield> iceShields;

    public SpiderQueen(AreaScene scene){
        super("SpiderQueen", scene, 0, 0);
        setSheet(Art.bossSpider112x112);
        setWPic(112);
        setHPic(112);
        setYPicO(0);
        setWidth(96);
        setHeight(96);
        setRenderingOrder(-16);
        setMaxHp(256);
        setHp(256);

        waitTime = (int)(minWaitTime+Math.random()*maxWaitTime);
        iceShields = new ArrayList<IceShield>();

        setShadowVisible(false);
        setCanBeKnockedback(false);
        setBlocksFlying(true);
        setSuppressHpMeter(false);

        setPowerupDrop(Powerups.AbilityGem);
        setAbilityDrop(Ability.Freeze);
    }

    private double headingTowardPlayer(int fromX, int fromY){
        double xDiff = getScene().player.getX()-fromX;
        double yDiff = getScene().player.getY()-fromY;
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void move(){
        if (!hasCreatedIceShields){
            createIceShields();
            hasCreatedIceShields = true;
        }

        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (canAct){
            if (damageTime > 0){
                damageTime--;
                setMoving(false);
            }
            else if (waitTime > 0){
                waitTime--;
                if (waitTime == 0) nextAction();
            }
            else if (silkTime > 0){
                silkTime--;

                //fire off a pair of silk things from legs
                if (silkTime % 2 == 0){
                    Rectangle imageBounds = getImageFootprint();
                    int pair = (int)(Math.random()*3);
                    int ly = (getTick()/2+pair)%7;
                    if (ly > 3) ly = 6-ly;
                    int sx1 = imageBounds.x+1;
                    int sy1 = imageBounds.y+8+pair*4+ly*2;
                    int sx2 = imageBounds.x+103;
                    int sy2 = imageBounds.y+8+pair*4+ly*2;
                    double sh1 = headingTowardPlayer(sx1, sy1)+Math.random()*0.1-0.05;
                    double sh2 = headingTowardPlayer(sx2, sy2)+Math.random()*0.1-0.05;
                    getScene().addSprite(new Silk(sx1, sy1, sh1, this));
                    getScene().addSprite(new Silk(sx2, sy2, sh2, this));
                }

                if (silkTime == 0) nextAction();
            }
            else if (spitTime > 0){
                spitTime--;

                //spit!
                if (spitTime % 4 == 0){
                    double sh = (Math.random()*Math.PI/2)-3*Math.PI/4;
                    getScene().addSprite(new PoisonSpit(getScene(), this, sh));
                }

                if (maxSpitTime == 0) nextAction();
            }
            else if (shakeTime > 0){
                shakeTime--;

                //break a shield if there is one to break
                IceShield toBreak = iceShields.get((int)(Math.random()*iceShields.size()));
                if (!toBreak.isDead()) toBreak.die();

                //drop ice spikes
                if (shakeTime % 3 == 0){
                    int sx = (int)(getX()-150+Math.random()*300);
                    int sy = (int)(getY()+32+Math.random()*200);
                    getScene().addSprite(new FallingIceSpike(getScene(), sx, sy));
                }

                if (shakeTime == 0){
                    for (IceShield iceShield: iceShields)
                        if (!iceShield.isDead())
                            iceShield.die();
                    birthTime = maxBirthTime;
                }
            }
            else if (birthTime > 0){
                birthTime--;

                //spray spiders out her hole
                if (birthTime % 4  == 0){
                    IceSpider spider = new IceSpider(getScene(), 0, 0, true);
                    spider.setX((int)(getX()-8+Math.random()*16));
                    spider.setY(getY());
                    spider.setHeading((Math.random() * Math.PI / 2) - 3 * Math.PI / 4);
                    getScene().addSprite(spider);

                    getScene().getSound().play(Art.getSample("snotPlop.wav"), this, 1f, 1, 1f);
                }

                if (birthTime == 0) nextAction();
            }
            else nextAction(); //shouldn't happen but just in case
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }
        super.move();
    }

    private void createIceShields(){
        for (int i = 0; i < 9; i++){
            IceShield iceShield = new IceShield(getScene(), this, i);
            iceShields.add(iceShield);
            getScene().addSprite(iceShield);
        }
    }

    public void die(){
        super.die();
        for (IceShield iceShield: iceShields)
            getScene().removeSprite(iceShield);
        for (Sprite sprite: getScene().getSprites())
            if (sprite instanceof IceSpider || sprite instanceof SpiderGenerator){
                ((Prop)sprite).die();
            }
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4f, 1, 1f);
    }

    private void nextAction(){
        double r = Math.random();
        if (getHp() < getMaxHp()/2){
            if (r < 0.25d) waitTime = minWaitTime+(int)(Math.random()*maxWaitTime);
            else if (r < 0.5d) spitTime = maxSpitTime;
            else if (r < 0.75d) silkTime = maxSilkTime;
            else shakeTime = maxShakeTime;
        }
        else{
            if (r < 0.2d) waitTime = minWaitTime+(int)(Math.random()*maxWaitTime);
            else if (r < 0.4d) spitTime = maxSpitTime;
            else if (r < 0.6d) silkTime = maxSilkTime;
            else shakeTime = maxShakeTime;
        }
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        if (birthTime > 0) modified.setDamage(attributes.getDamage()*2);
        modified.getAttributes().remove(Attribute.Freeze);
        modified.getAttributes().remove(Attribute.Drown);
        //modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (hp < getHp()) damageTime = maxDamageTime;
    }

    private void renderPiece(Graphics2D og, Image image, int x, int y){
        Color oldTint = getTintColor();
        if (hasStatusEffect(StatusEffect.Frozen))
            setTintColor(Color.CYAN);
        else if (hasStatusEffect(StatusEffect.Electrocuted))
            setTintColor(Color.YELLOW);

        if (isDesaturated()) image = ImageUtils.grayscaleImage(image, null);
        if (getFadeRatio() != 1f) image = ImageUtils.fadeImage(image, null, getFadeRatio());
        if (getTintColor() != null) image = ImageUtils.tintImage(image, getTintColor(), null);
        if (getOutlineColor() != null) image = ImageUtils.outlineImage(image, getOutlineColor());
        og.drawImage(image, x, y, null);

        setTintColor(oldTint);
    }

    public void render(Graphics2D og, float alpha){
        Rectangle imageBounds = getImageFootprint(alpha); //this should be for the main body piece

        //lower body piece
        renderPiece(og, Art.bossSpider112x112[0][0], imageBounds.x, imageBounds.y);

        //draw legs
        int midShakeXOff = (shakeTime > 0) ? (int)(Math.random()*5-2) : 0;
        int midShakeYOff = (shakeTime > 0) ? (int)(Math.random()*3-1) : 0;
        int rate = (silkTime > 0) ? 2 : 4;
        for (int pair = 3; pair >= 0; pair--){
            int ly = (getTick()/rate+pair)%7;
            if (ly > 3) ly = 6-ly;
            renderPiece(og, Art.bossSpider32x16[0][ly], imageBounds.x-7+midShakeXOff, imageBounds.y+8+pair*4+ly*2+midShakeYOff);
            renderPiece(og, Art.bossSpider32x16[1][ly], imageBounds.x+87+midShakeXOff, imageBounds.y+8+pair*4+ly*2+midShakeYOff);
        }

        //top body pieces
        renderPiece(og, Art.bossSpider112x112[1][0], imageBounds.x+midShakeXOff, imageBounds.y-32+midShakeYOff);

        int topShakeXOff = midShakeXOff + ((shakeTime > 0) ? (int)(Math.random()*3-1) : 0);
        int topShakeYOff = midShakeYOff + ((shakeTime > 0) ? (int)(Math.random()*2-1) : 0);
        renderPiece(og, Art.bossSpider112x112[2][0], imageBounds.x+topShakeXOff, imageBounds.y-40+topShakeYOff);

        //pincers
        int spitRate = (spitTime > 0) ? 1 : 4;
        int xpOff = (getTick()/spitRate)%7;
        if (xpOff > 3) xpOff = 6-xpOff;
        renderPiece(og, Art.bossSpider32x16[3][0], imageBounds.x+24-xpOff+topShakeXOff, imageBounds.y+24+topShakeYOff);
        renderPiece(og, Art.bossSpider32x16[4][0], imageBounds.x+56+xpOff+topShakeXOff, imageBounds.y+24+topShakeYOff);

        //head
        renderPiece(og, Art.bossSpider112x112[3][0], imageBounds.x+topShakeXOff, imageBounds.y-52+topShakeYOff);

        //draw mouth
        int my = (getTick()/spitRate)%4;
        if (my == 3) my = 1;
        renderPiece(og, Art.bossSpider32x16[3][1+my], imageBounds.x+40+topShakeXOff, imageBounds.y+28+topShakeYOff);

        //birthin' hole
        int birthRate = (birthTime > 0) ? 2 : 4;
        int hy = (getTick()/birthRate)%7;
        if (hy > 3) hy = 6-hy;
        renderPiece(og, Art.bossSpider32x16[2][hy], imageBounds.x+40, imageBounds.y+86);

        //show over head
        if (hasStatusEffect(StatusEffect.Drowning)){
            //colors
            Color darkblue = new Color(0, 0.5f, 1f, 0.75f);
            Color solidblue = new Color(0, 0.5f, 1f, 1f);

            //figure out bounding box for drown ball
            int drownTicks = Integer.MAX_VALUE-getStatusEffects().get(StatusEffect.Drowning);
            int fillTicks = 16;
            double fillRatio = drownTicks/(double)fillTicks;
            if (fillRatio > 1) fillRatio = 1;

            double maxRadius = 80;
            double radius = maxRadius*fillRatio;

            Rectangle2D.Double bounds = new Rectangle2D.Double();
            bounds.x = imageBounds.x+imageBounds.width/2-radius;
            bounds.y = imageBounds.y-48;
            bounds.width = radius*2;
            bounds.height = radius*2;

            og.setColor(darkblue);
            og.fillOval((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);
            og.setColor(new Color(1f, 1f, 1f, 0.2f));
            og.fillOval((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height/2);
            og.setColor(solidblue);
            og.drawOval((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
        }

        //draw above birthin' hole
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();
        if (getHpMeterCounter() > 0) renderHpBar(og, xPixel, yPixel);

        //footprint
        if (AreaGroupRenderer.renderBehaviors){
            Rectangle fp = getFootprint();
            if (fp != null){
                if (isCollidable()){
                    og.setColor(new Color(1f, 0f, 0f, 0.3f));
                    if (!isBlocksFlying()) og.setColor(new Color(0f, 1f, 0.5f, 0.3f));
                    og.fill(fp);
                    og.setColor(Color.RED);
                    if (!isBlocksFlying()) og.setColor(Color.CYAN);
                    og.draw(fp);
                }

                //render heading
                if (getXa() != 0 || getYa() != 0){
                    double f = 5;
                    og.drawLine((int)fp.getCenterX(), (int)fp.getCenterY(), (int)(fp.getCenterX()+getXa()*f), (int)(fp.getCenterY()+getYa()*f));
                }
            }
        }
    }
}

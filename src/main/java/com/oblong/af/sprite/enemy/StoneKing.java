package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.StatusEffect;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.sprite.projectile.TailedBolt;
import com.oblong.af.sprite.thing.PetrifyCloud;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Stone King is a giant machine thing; has two base layers that spin around and a top that spins independently the
 *   other direction, spewing out petrifying gas and some other kind of projectile
 */

public class StoneKing extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "StoneKing";

    private int petrifyBreathTime = 0, maxPetrifyBreathTime = 32;
    private int shootTime = 0, maxShootTime = 32;
    private int damageTime = 0, maxDamageTime = 8;
    private int moveTime = 0, maxMoveTime = 16;
    private double base1Angle = 0, base2Angle = 0, gearsAngle = 0, capAngle = 0;

    public StoneKing(AreaScene scene) {
        super("StoneKing", scene, 0, 0);
        setSheet(Art.bossStoneKing144x144);
        setWPic(72);
        setHPic(72);
        setXPicO(0);
        setYPicO(0);
        setWidth(64);
        setHeight(64);

        setShadowVisible(false);
        setCanBeKnockedback(false);
        setImmuneToStatusEffects(true);
        setBlocksMovement(true);
        setBlockable(true);

        setImpactDamageAttributes(new DamageAttributes(4, Arrays.asList(Attribute.Physical, Attribute.Knockback)));
    }

    private double headingTowardPlayer(){
        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void move(){
        if (!isDead()){
            //distance to player
            double distance = Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
            if (getScene().player.isDead() || getScene().player.hasStatusEffect(StatusEffect.Invisible)) distance = Double.MAX_VALUE;

            if (damageTime > 0){
                damageTime--;
                setMoving(false);
            }
            else if (shootTime > 0){
                shootTime--;
                setMoving(false);
                getScene().addSprite(TailedBolt.createPetrifyingBolt(getScene(), this, 3 * (shootTime / (double) maxShootTime) * (Math.PI * 2)));
                if (shootTime == 0) moveTime = maxMoveTime;
            }
            else if (petrifyBreathTime > 0){
                petrifyBreathTime--;
                setMoving(true);
                setHeading(headingTowardPlayer());

                if (petrifyBreathTime %2 == 0){
                    PetrifyCloud spurt = new PetrifyCloud(getScene(), 48, this, true);
                    spurt.setHeading(getHeading()-Math.PI/8f+Math.random()*Math.PI/4f);
                    spurt.setSpeed(15f);
                    spurt.setMoving(true);
                    spurt.setX(getX()+(float)(Math.cos(spurt.getHeading())*16));
                    spurt.setY(getY()-(float)(Math.sin(spurt.getHeading())*16));
                    getScene().addSprite(spurt);
                }
                if (petrifyBreathTime == 0) moveTime = maxMoveTime;
            }
            else if (moveTime > 0){
                moveTime--;
            }
            //otherwise move around
            else{
                double r = Math.random();
                if (r < 0.01) petrifyBreathTime = maxPetrifyBreathTime;
                else if (r < 0.02) shootTime = maxShootTime;
                else{
                    setMoving(true);
                    setSpeed(6);
                    double heading = getHeading();
                    if (distance < 128) setHeading(Math.random()*Math.PI*2);
                    else setHeading(headingTowardPlayer());
                    moveTime = maxMoveTime;

                    if (heading != getHeading())
                        getScene().getSound().play(Art.getSample("clang.wav"), this, 1, 1, 1);
                }
            }
        }

        super.move();

        if (isMoving()){
            double turnRate = 32;
            double turnDir = 1;//getHeading() < Math.PI ? -1d : 1d;
            base1Angle += turnDir*Math.PI/turnRate;
            base2Angle += -turnDir*Math.PI/(turnRate+3);
            gearsAngle += (-turnDir*Math.PI/turnRate)*3d;
            capAngle += (turnDir*Math.PI/turnRate)/2d;
        }

        if (isMoving() && getTick()%4 == 0)
            getScene().getSound().play(Art.getSample("swish.wav"), this, 0.1f, 1, 0.2f);
//        if (Math.random() < 0.05)
//            getScene().getSound().play(Art.getSample("clang.wav"), this, 1, 1, 1);

    }

    public void die(){
        super.die();
        getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));
        getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");
        getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4, 1, 1);
    }

    public void damage(DamageAttributes attributes){
        int hp = getHp();
        super.damage(attributes);
        if (getHp() < hp) damageTime = maxDamageTime;
    }

    public Rectangle getFootprint(){ return new Rectangle((int)getX()-getWidth()/2, (int)getY()-getHeight()/2, getWidth(), getHeight()); }
    public Rectangle2D.Float getFootprint2D(){ return new Rectangle2D.Float(getX()-getWidth()/2f, getY()-getHeight()/2, getWidth(), getHeight()); }

    private void renderInternal(Graphics2D og, Image image, double angle, double xPixel, double yPixel){
        if (getFadeRatio() != 1f)
            try{ image = ImageUtils.fadeImage(image, null, getFadeRatio()); }
            catch(Exception e){}
        if (getTintColor() != null) image = ImageUtils.tintImage(image, getTintColor(), null);
        if (getOutlineColor() != null) image = ImageUtils.outlineImage(image, getOutlineColor());

        image = ImageUtils.scaleImage(image, null, 0.5d, 0.5d);
        if (angle != 0) image = ImageUtils.rotateImage(image, null, angle);
        og.drawImage(image, (int)(xPixel-image.getWidth(null)/2), (int)(yPixel-image.getHeight(null)/2), null);
    }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();

        Image baseImage = getSheet()[0][0];
        Image gearsImage = getSheet()[2][0];
        Image capImage = getSheet()[1][0];

        renderInternal(og, baseImage, base1Angle, xPixel, yPixel);
        renderInternal(og, baseImage, base2Angle, xPixel, yPixel);
        og.setColor(Color.BLACK);
        og.fillOval(xPixel-16, yPixel-16, 32, 32);
        renderInternal(og, gearsImage, gearsAngle, xPixel, yPixel);
        renderInternal(og, capImage, capAngle, xPixel, yPixel);

        if (hasStatusEffect(StatusEffect.Drowning)){
            //colors
            Color darkblue = new Color(0, 0.5f, 1f, 0.75f);
            Color solidblue = new Color(0, 0.5f, 1f, 1f);
            Color lightblue = new Color(0, 0.8f, 0.1f);

            //figure out bounding box for drown ball
            int drownTicks = Integer.MAX_VALUE-getStatusEffects().get(StatusEffect.Drowning);
            int fillTicks = 16;
            double fillRatio = drownTicks/(double)fillTicks;
            if (fillRatio > 1) fillRatio = 1;

            double maxRadius = Math.max(getWidth(), getHeight())*3d/2d;
            double radius = maxRadius*fillRatio;

            Rectangle2D.Double bounds = new Rectangle2D.Double();
            bounds.x = getX()-radius;
            bounds.y = getY()-getHPic()/3d-radius;
            bounds.width = radius*2;
            bounds.height = radius*2;

            og.setColor(darkblue);
            og.fillOval((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);
            og.setColor(new Color(1f, 1f, 1f, 0.2f));
            og.fillOval((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height/2);
            og.setColor(solidblue);
            og.drawOval((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
        }

        if (getHpMeterCounter() > 0) renderHpBar(og, xPixel, yPixel+32);

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

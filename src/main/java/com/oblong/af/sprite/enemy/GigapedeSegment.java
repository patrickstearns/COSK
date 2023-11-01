package com.oblong.af.sprite.enemy;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Powerup;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.sprite.projectile.PedeFlameSpurt;
import com.oblong.af.sprite.projectile.TornadoFlame;
import com.oblong.af.sprite.thing.PoisonCloud;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A segment can represent either a head or body segment.  The tail is generated behind the last segment in the chain.
 *   We start off with a head with five segments trailing; snaking around until it goes underground (either randomly
 *   or if its movement is tile-blocked).  If a segment is destroyed, a new head is pops out in its place with the
 *   trailing segments of the original chain attached behind it, making for multiple 'pedes, and poison gas clouds are
 *   released.  Heads breathe fire or poison.  Tails are indestructible.  If head is destroyed, all segments trailing
 *   it are too, but heads are a lot tougher than segments.
 */

public class GigapedeSegment extends Prop {

    public static final String DEFEATED_STATE_VARIABLE = "Gigapede";

    public static GigapedeSegment createBossGigapede(AreaScene scene, int numTrailingSegments, float x, float y){
        GigapedeSegment head = new GigapedeSegment(scene, 0, true);
        head.setX(x);
        head.setY(y);

        GigapedeSegment ahead = head;
        for (int i = 0; i < numTrailingSegments; i++){
            GigapedeSegment segment = new GigapedeSegment(scene, i+1, true);
            segment.setX(x);
            segment.setY(y);
            ahead.segmentBehind = segment;
            segment.segmentAhead = ahead;
            ahead = segment;
        }

        return head;
    }

    protected class SegmentPos {
        public float x, y;
        public double h;
        public SegmentPos(){
            x = getX();
            y = getY();
            h = getHeading();
        }
    }

    private GigapedeSegment segmentAhead, segmentBehind;
    private java.util.List<SegmentPos> trail;
    private int index;
    private int oxPic;
    private int explodeCounter = 0;
    private int fireBreathTime = 0, maxFireBreathTime = 32;

    private GigapedeSegment(AreaScene scene, int index, boolean boss){
        super("GigapedeSegment", scene, 0, 0);
        setSheet(Art.bossGigapede48x48);
        setWPic(32);
        setHPic(32);
        setWidth(28);
        setHeight(28);

        setMaxHp(64);
        setHp(64);

        setBlockable(true);
        setImpactDamageAttributes(new DamageAttributes(2, Arrays.asList(Attribute.Physical, Attribute.Knockback)));
        setCanBeKnockedback(false);
        setShadowVisible(false);

        oxPic = index%4;
        trail = new ArrayList<SegmentPos>();

        this.index = index;
        setBoss(boss);
    }

    private boolean isHead(){ return segmentAhead == null; }
    private boolean isMiddle(){ return !isHead() && !isTail(); }
    private boolean isTail(){ return segmentBehind == null; }

    private boolean inited = false;

    public boolean isCollidableWith(Prop prop){
        return !(prop instanceof GigapedeSegment) && super.isCollidableWith(prop);
    }

    public Rectangle getFootprint(){ return new Rectangle((int)getX()-getWidth()/2, (int)getY()-getHeight()/2, getWidth(), getHeight()); }
    public Rectangle2D.Float getFootprint2D(){ return new Rectangle2D.Float(getX()-getWidth()/2f, getY()-getHeight()/2, getWidth(), getHeight()); }

    public void move(){
        if (!inited){
            if (isHead()){
                GigapedeSegment segment = this;
                for (int i = 0; i < 8; i++) trail.add(new SegmentPos());
                while(segment.segmentBehind != null){
                    segment = segment.segmentBehind;
                    for (int i = 0; i < 8; i++) segment.trail.add(new SegmentPos());
                    getScene().addSprite(segment);
                    segment.setMessages(getMessages());
                }
            }

            setBlockable(isHead());
            if (!isHead()) setImmuneToStatusEffects(true);

            inited = true;
        }

        trail.add(new SegmentPos());
        trail.remove(0);

        boolean canAct = true;
        if (hasStatusEffect(StatusEffect.Petrified) || hasStatusEffect(StatusEffect.Frozen)  || hasStatusEffect(StatusEffect.Drowning) ||
                hasStatusEffect(StatusEffect.Electrocuted) || hasStatusEffect(StatusEffect.Stunned))
            canAct = false;

        if (explodeCounter > 0){
            explodeCounter--;
            if (explodeCounter <= 0){
                setDead(true);
                getScene().removeSprite(this);
                getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));

                int nonDeadSegments = 0;
                for (Sprite sprite: getScene().getSprites())
                    if (sprite != this && sprite instanceof GigapedeSegment && !((GigapedeSegment) sprite).isDead())
                        nonDeadSegments++;

                if (nonDeadSegments == 0){
                    getScene().addSprite(new Powerup(getScene(), Powerups.AbilityGem, Ability.Firestorm, (int) getX(), (int) getY()));
                    getScene().getGameState().setVariable(DEFEATED_STATE_VARIABLE, "true");

                    if (getMessages() != null)
                        for (String message: getMessages().getOnDieMessages())
                            getScene().message(this, message);

                    getScene().getSound().play(Art.getSample("bossDeadFanfare.wav"), this, 4, 1, 1);
                }
            }
        }

        if (canAct && explodeCounter == 0){
            if (isHead()){
                if (Math.random() < 0.2) setHeading(getHeading()+(0.3-(Math.random()*0.6)));
                if (blocked())
                    setHeading(getHeading()+Math.PI);
                setMoving(true);
                setSpeed(3);
                if (segmentBehind == null) setSpeed(8);

                GigapedeSegment current = this;
                while (current.segmentBehind != null){
                    current = current.segmentBehind;
                    SegmentPos pos = current.segmentAhead.trail.get(0);
                    current.setX(pos.x);
                    current.setY(pos.y);
                    current.setHeading(pos.h);
                }

                //flame spurt
                if (fireBreathTime > 0){
                    fireBreathTime--;
                    getScene().addSprite(new PedeFlameSpurt(getScene(), this, getHeading()-Math.PI/8f+Math.random()*Math.PI/4f, 12));
//                    if (segmentBehind == null && fireBreathTime%4 == 0){
//                        getScene().addSprite(new TornadoFlame(getScene(), this));
//                    }
                }
                else if (Math.random() < 0.01)
                    fireBreathTime = maxFireBreathTime;

                //if just a head leave a poison gas trail
                if (segmentBehind == null && getTick()%3 == 0){
                    PoisonCloud cloud = new PoisonCloud(getScene(), 48, this, true);
                    cloud.setX((int)(getX()-getWidth()/2+Math.random()*getWidth()));
                    cloud.setY((int) (getY() - getHeight() / 2 + Math.random() * getHeight()));
                    getScene().addSprite(cloud);
                }

                getScene().getSound().play(Art.getSample("scratch.wav"), this, 0.1f, 1, 1);
            }
        }
        else{
            setMovementLockedCounter(0);
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }

        super.move();
    }

    protected void checkForScreenEdge(){
        if (getX()-getWidth()/2 < 0){
            setX(getWidth()/2);
            setXa(0);
            setChargingAhead(false);
            if (isHead()) setHeading(getHeading()+Math.PI);
        }
        if (getX()+getWidth()/2 > getScene().areaGroup.getWidth() * 16){
            setX(getScene().areaGroup.getWidth() * 16 - getWidth()/2);
            setXa(0);
            setChargingAhead(false);
            if (isHead()) setHeading(getHeading()+Math.PI);
        }
        if (getY()-getHeight() < 0){
            setY(getHeight());
            setYa(0);
            setChargingAhead(false);
            if (isHead()) setHeading(getHeading()+Math.PI);
        }
        if (getY() > getScene().areaGroup.getHeight() * 16){
            setY(getScene().areaGroup.getHeight() * 16);
            setYa(0);
            setChargingAhead(false);
            if (isHead()) setHeading(getHeading()+Math.PI);
        }
    }

    private double headingTowardProp(Prop target){
        double xDiff = target.getX()-getX();
        double yDiff = target.getY()-getY();
        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        return atan;
    }

    public void calcPic(){
        if (isHead()){
            setXPic(1);
            setYPic(1);
        }
        else if (isTail()){
            int tx = (getTick()/2)%7;
            if (tx > 3) tx = 6-tx;
            setXPic(tx);
            setYPic(2);
        }
        else{
            setXPic(oxPic);
            setYPic(0);
        }
    }

    protected void puff(int numSparkles, Color color){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*Math.max(getWPic()/2, getHPic()/2));
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance)+24;
            if (color == null)
                getScene().addSprite(new Puff(getScene(),
                        (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset-8)));
            else
                getScene().addSprite(new Puff(getScene(),
                        (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset-8), color));
        }
    }

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes);
        modified.getAttributes().remove(Attribute.Freeze);
        modified.getAttributes().remove(Attribute.Drown);
        modified.getAttributes().remove(Attribute.Electric);
        modified.getAttributes().remove(Attribute.Petrify);
        //modified.getAttributes().remove(Attribute.Stun);
        modified.getAttributes().remove(Attribute.Death);
        modified.getAttributes().remove(Attribute.Poison);
        return modified;
    }

    public void die(){
        //if we're the head, go through and destroy all the trailing segments
        if (isHead()){
            GigapedeSegment current = this;
            explode(1);
            for (int delay = 1; current.segmentBehind != null; delay += 8){
                current = current.segmentBehind;
                current.explode(delay);
            }

            //green poofs
            puff(50, new Color(0.7f, 0f, 0.7f));
        }
        //if we're a middle segment...
        else if (isMiddle()){
            //add a new tail to the segment ahead
            GigapedeSegment newTail = new GigapedeSegment(getScene(), index*10, isBoss());
            newTail.setMessages(getMessages());
            newTail.setX(getX());
            newTail.setY(getY());
            newTail.setXOld(getX());
            newTail.setYOld(getY());
            newTail.segmentAhead = segmentAhead;
            segmentAhead.segmentBehind = newTail;
            getScene().addSprite(newTail);

            //add a new head to the segment behind
            GigapedeSegment newHead = new GigapedeSegment(getScene(), index*10+1, isBoss());
            newHead.setMessages(getMessages());
            newHead.setX(getX());
            newHead.setY(getY());
            newHead.setXOld(getX());
            newHead.setYOld(getY());
            newHead.segmentBehind = segmentBehind;
            segmentBehind.segmentAhead = newHead;
            getScene().addSprite(newHead);

            //create a bunch of poison clouds
            for (double h = 0; h < Math.PI*2; h += Math.PI/8){
                PoisonCloud cloud = new PoisonCloud(getScene(), 48, this, true);
                cloud.setX((int)(getX()-getWidth()/2+Math.random()*getWidth()));
                cloud.setY((int) (getY() - getHeight() / 2 + Math.random() * getHeight()));
                cloud.setMoving(true);
                cloud.setHeading(h);
                cloud.setSpeed((float)(Math.random()*8f));
                getScene().addSprite(cloud);
            }

            //green poofs
            puff(50, new Color(0.7f, 0f, 0.7f));

            explode(1);

            segmentAhead = null;
            segmentBehind = null;
        }
        //if we're the tail just die
        else if (isTail()){
            segmentAhead.segmentBehind = null;
            explode(1);

            //green poofs
            puff(50, new Color(0.7f, 0f, 0.7f));
        }
    }

    private void explode(int delay){ explodeCounter = delay; }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        Color oldTint = getTintColor();
        if (hasStatusEffect(StatusEffect.Frozen))
            setTintColor(Color.CYAN);
        else if (hasStatusEffect(StatusEffect.Electrocuted))
            setTintColor(Color.YELLOW);

        //do it like our super
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO()-getWidth()/2;
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO()-getHeight()/2;
        Image image = getSheet()[getXPic()][getYPic()];
        image = ImageUtils.scaleImage(image, null, 0.66, 0.66);
        image = ImageUtils.rotateImage(image, null, -getHeading() + Math.PI / 2d); //the entire point; rotate the image
        if (isDesaturated()) image = ImageUtils.grayscaleImage(image, null);
        if (getFadeRatio() != 1f) image = ImageUtils.fadeImage(image, null, getFadeRatio());
        if (getTintColor() != null) image = ImageUtils.tintImage(image, getTintColor(), null);
        if (getOutlineColor() != null) image = ImageUtils.outlineImage(image, getOutlineColor());

        og.drawImage(image, xPixel, yPixel, null);

        setTintColor(oldTint);

        if (getStatusEffects().containsKey(StatusEffect.Drowning)){
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

        if (getHpMeterCounter() > 0) renderHpBar(og, xPixel+getWidth()/2, yPixel+getHeight());

        if (AreaGroupRenderer.renderBehaviors){
            Rectangle fp = getFootprint();
            if (fp != null){
                og.setColor(new Color(1f, 0f, 0f, 0.3f));
                if (!isBlocksFlying()) og.setColor(new Color(0f, 1f, 0.5f, 0.3f));
                og.fill(fp);
                og.setColor(Color.RED);
                if (!isBlocksFlying()) og.setColor(Color.CYAN);
                og.draw(fp);

                //render heading
                if (getXa() != 0 || getYa() != 0){
                    double f = 5;
                    og.drawLine((int)fp.getCenterX(), (int)fp.getCenterY(), (int)(fp.getCenterX()+getXa()*f), (int)(fp.getCenterY()+getYa()*f));
                }
            }
        }

    }

}

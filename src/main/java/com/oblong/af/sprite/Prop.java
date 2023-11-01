package com.oblong.af.sprite;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.level.decorators.FlashingDecorator;
import com.oblong.af.level.decorators.LayerDecorator;
import com.oblong.af.models.*;
import com.oblong.af.sprite.decorator.AbstractSpriteDecorator;
import com.oblong.af.sprite.decorator.FadeDecorator;
import com.oblong.af.sprite.decorator.OutlineDecorator;
import com.oblong.af.sprite.decorator.TintDecorator;
import com.oblong.af.sprite.effects.*;
import com.oblong.af.sprite.projectile.SonicBlast;
import com.oblong.af.util.Art;
import com.oblong.af.util.Footprint;
import com.oblong.af.util.ImageUtils;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Props are Sprites that can move and collide with things (and/or be intangible), be activated, damaged or destroyed.
 */
public class Prop extends Sprite {

    private static final int MAX_DEAD_COUNTER = 20;

    public static final Color COLOR_TINT_DAMAGE = new Color(1f, 0f, 0f, 0.7f);
    public static final Color COLOR_TINT_HEALED = new Color(0f, 1f, 0f, 0.7f);

    private class TMV { //"timed movement vector"
        public double x, y;
        public boolean knockback;
        public int ticks;
        public TMV(double x, double y, int ticks, boolean knockback){
            this.x = x;
            this.y = y;
            this.ticks = ticks;
            this.knockback = knockback;
        }
    }

    //moving stuff
    private boolean moving;
    private float runTime, speed, slipSpeed;
    private List<TMV> movementVectors;
    private int spinCounter = 0, flameSpinCounter = 0;
    private int headingLockedCounter = 0, movementLockedCounter = 0;
    private int wailCounter = 0, shoutCounter = 0;
    private boolean chargingAhead = false;
    private int tunnelDownTime = 0, tunnelUpTime = 0, maxTunnelTime = 12;
    private int tunnelDelayCounter = 0, maxTunnelDelayCounter = 16;
    private Point tunnelLoc = null, tunnelFromLoc = null;
    private Point2D lastSlipperyMovementVector = new Point2D.Float(0, 0);

    //blocking stuff
    private boolean
            collidable,             //are we notified of collisions with other things?
            collidesWithPlayerOnly, //can we only collide with the player and nothing else?
            flying,                 //can we fly over pits?
            blocksMovement,         //do we stop other things from moving?
            blocksFlying,           //do we stop flying things from moving?
            blockable,              //can our movement be blocked?
            blockableByScreenEdge,  //are we stopped by the edge of the screen, or destroyed when moving off of it?
            impactDamagesPlayerOnly,//do we only do our impact damage attributes to the player?
            diesOnCollide,          //do we die when colliding with something?
            diesOnCollideWithPlayer;//do we die when colliding with the player only?
    private Point2D.Float lastUnblockedPosition = new Point2D.Float(0, 0);
    private DamageAttributes impactDamageAttributes;

    //damage and knockback stuff
    private boolean dead, canBeKnockedback;
    private int hp, maxHp, knockbackCounter, invulnerableCounter;
    private double knockbackHeading;
    private Ability abilityDrop, minibossAbilityDrop;
    private Powerups powerupDrop, minibossPowerupDrop;

    //state stuff
    private boolean suppressHpMeter;
    private int hpMeterCounter, deadCounter;
    private int enteringTime = 0, exitingTime = 0;
    private int teleportInTime = 0, teleportOutTime = 0;
    private String teleportToLevel;
    private Point2D.Float teleportOutFromPoint;
    private Map<StatusEffect, Integer> statusEffects;
    private int surprisedTime = 0;
    private boolean miniboss = false, minibossifiable = false;

    //other
    private boolean immuneToDamage = false, immuneToStatusEffects = false;
    private Skills[] skills;
    private boolean shadowVisible = true;
    private int shadowYOffset = 2;
    private boolean boss = false;

    protected Prop(String id, AreaScene scene, int oxPic, int oyPic){
		super(id, scene);
        setOxPic(oxPic);
        setOyPic(oyPic);
        setLayer(Area.Layer.Main);
        statusEffects = new HashMap<StatusEffect, Integer>();

        setCollidable(true);
        setCollidesWithPlayerOnly(false);
        setFlying(false);
        setBlockable(true);
        setBlocksMovement(true);
        setBlocksFlying(true);
        setBlockableByScreenEdge(true);
        setDiesOnCollide(false);
        setDiesOnCollideWithPlayer(false);

        movementVectors = new ArrayList<TMV>();

        if (isProjectile()) setShadowVisible(false);
    }

    public int getRenderingOrder(){ return 8; }

    public void render(Graphics2D og, float alpha){
        if (!isVisible()) return;

        if (tunnelDownTime > 0 && tunnelDownTime < 8) setYPicO(getYPicO() - 1);
        else if (tunnelUpTime > 0 && tunnelUpTime > maxTunnelTime-8) setYPicO(getYPicO()+1);

        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-getXPicO();
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-getYPicO();

        if (shadowVisible && tunnelLoc == null){
            Image shadowImage = Art.effects16x16[2][8];
            if (getFadeRatio() != 1f) shadowImage = ImageUtils.fadeImage(shadowImage, null, getFadeRatio());
            og.drawImage(shadowImage, xPixel-shadowImage.getWidth(null)/2, yPixel-shadowImage.getHeight(null)+shadowYOffset, null);
        }

        if (tunnelDownTime > 0 || tunnelUpTime > 0)
            og.setClip(new Rectangle(xPixel-getWPic(), yPixel-getHPic()+getYPicO(), getWPic()*3, getHPic()));

        if (spinCounter > 0){
            Image spin = Art.effects32x32[2+(getTick()/2)%2][10];
            og.drawImage(spin, xPixel-spin.getWidth(null)/2, yPixel-spin.getHeight(null)+4, null);
        }
        if (flameSpinCounter > 0){
            Image spin = Art.effects32x32[2+(getTick()/2)%2][11];
            og.drawImage(spin, xPixel-spin.getWidth(null)/2, yPixel-spin.getHeight(null)+4, null);
        }

        Color oldTint = getTintColor();
        if (hasStatusEffect(StatusEffect.Frozen))
            setTintColor(Color.CYAN);
        else if (hasStatusEffect(StatusEffect.Electrocuted))
            setTintColor(Color.YELLOW);

        //do it like our super
        super.render(og, alpha);

        setTintColor(oldTint);

        if (spinCounter > 0){
            Image spin = Art.effects32x32[(getTick()/2)%2][10];
            og.drawImage(spin, xPixel-spin.getWidth(null)/2, yPixel-spin.getHeight(null)+4, null);
        }
        if (flameSpinCounter > 0){
            Image spin = Art.effects32x32[(getTick()/2)%2][11];
            og.drawImage(spin, xPixel-spin.getWidth(null)/2, yPixel-spin.getHeight(null)+4, null);
        }

        if (hasStatusEffect(StatusEffect.Shield)){
            Image shield = Art.effects32x48[0][1];
            og.drawImage(shield, (int)(getX()-shield.getWidth(null)/2), (int)(getY()-shield.getHeight(null))+4, null);
        }

        if (tunnelDownTime > 0){
            Image spinImage = Art.spikehog[(getTick()/2)%2][4];
            if (tunnelDownTime >= maxTunnelTime-4)
                spinImage = ImageUtils.fadeImage(spinImage, null, (maxTunnelTime - tunnelDownTime)/4f);
            else if (tunnelDownTime == maxTunnelTime-5) setFadeRatio(0f);
            og.drawImage(spinImage, xPixel-spinImage.getWidth(null)/2, yPixel-spinImage.getHeight(null), null);
        }
        else if (tunnelUpTime > 0){
            Image spinImage = Art.spikehog[(getTick()/2)%2][4];
            if (tunnelUpTime == 5) setFadeRatio(1f);
            else if (tunnelUpTime < 4)
                spinImage = ImageUtils.fadeImage(spinImage, null, tunnelUpTime/4f);
            og.drawImage(spinImage, xPixel-spinImage.getWidth(null)/2, yPixel-spinImage.getHeight(null), null);
        }

        if (tunnelDownTime > 0 || tunnelUpTime > 0)
            og.setClip(null);

        if (statusEffects.containsKey(StatusEffect.Drowning)){
            //colors
            Color darkblue = new Color(0, 0.5f, 1f, 0.75f);
            Color solidblue = new Color(0, 0.5f, 1f, 1f);
            Color lightblue = new Color(0, 0.8f, 0.1f);

            //figure out bounding box for drown ball
            int drownTicks = Integer.MAX_VALUE-statusEffects.get(StatusEffect.Drowning);
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

        if (hpMeterCounter > 0) renderHpBar(og, xPixel, yPixel);

        if (surprisedTime > 0){
            Image image = Art.effects16x16[3][8];
            og.drawImage(image, xPixel-image.getWidth(null)/2, yPixel-image.getHeight(null)-getHPic()+16, null);
        }

        if (AreaGroupRenderer.renderBehaviors){
            Rectangle fp = getFootprint();
            if (fp != null){
                if (collidable){
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

    protected void renderHpBar(Graphics g, int px, int py){
    	float fade = 1f;
    	if (hpMeterCounter < 10) fade = hpMeterCounter/10f;

    	Rectangle bounds = new Rectangle(px-getWPic()/2, py-getHPic()-2, getWPic(), 4);
    	g.setColor(new Color(0f, 0f, 0f, fade));
    	g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 4, 4);

    	int barWidth = getWPic()*getHp()/getMaxHp()-1;
    	g.setColor(new Color(1f, 0f, 0f, fade));
    	g.fillRect(bounds.x+1, bounds.y+1, barWidth, 2);
    }

    public void die(){
        setDead(true);
        deadCounter = MAX_DEAD_COUNTER;
        statusEffects.clear();
        getDecorators().clear();

        //fade out over deadCounter ticks
        addDecorator(new FadeDecorator(1f, -1, deadCounter));

        //send our on-die messages
        if (getMessages() != null)
            for (String message: getMessages().getOnDieMessages())
                getScene().message(this, message);

        if (isBoss()){
            //longer death period than normal
            setDeadCounter(40);

            //make screen flash
            FlashingDecorator fd = new FlashingDecorator();
            fd.init(320, 240);
            getScene().areaGroup.addDecorator(fd);

            //initial explosion
            getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));

            //stop current music from playing
            Art.stopMusic();
        }
    }

    protected void dead(){
        if (isBoss()){
            bossDead();
            return;
        }

		deadCounter--;

		if (deadCounter == MAX_DEAD_COUNTER/2){
			//create sparkles
	        for (int xx = 0; xx < 4; xx++)
	            for (int yy = 0; yy < 4; yy++){
                    int x = (int)(getX()-getWPic()/2+Math.random()*getWPic());
                    int y = (int)(getY()-getHPic()/2+Math.random()*getHPic())-getYPicO();
                    getScene().addSprite(new Sparkle(getScene(), x, y, Color.WHITE, 0, 0));
                }
		}

		if (deadCounter == 0){
			getScene().removeSprite(this);
	        Powerups pDef = getPowerupDrop();
            Ability ability = null;
            if (pDef == Powerups.AbilityGem) ability = getAbilityDrop();
	        if (pDef != null) getScene().addSprite(new Powerup(getScene(), pDef, ability, (int) getX(), (int) getY()));
		}
    }

    //is the same as the regular version, except adds explosions instead of sparkles
    protected void bossDead(){
        setDeadCounter(getDeadCounter()-1);
        int deadCounter = getDeadCounter();
        setFadeRatio((float) deadCounter / 40f);

        if (Math.random() < 0.1)
            getScene().addSprite(new ExplodeEffect(getScene(), (int)(getX()-24+Math.random()*48), (int)(getY()+Math.random()*48)));

        if (deadCounter == 35){
            for (LayerDecorator ld: new ArrayList<LayerDecorator>(getScene().areaGroup.getDecorators()))
                if (ld instanceof FlashingDecorator)
                    getScene().areaGroup.getDecorators().remove(ld);
        }

        if (deadCounter%5 == 0){
            //create sparkles
            for (int xx = 0; xx < 4; xx++)
                for (int yy = 0; yy < 4; yy++)
                    getScene().addSprite(new Sparkle(getScene(),
                            (int)(getX()-24+Math.random()*48), (int)(getY()+8-48+Math.random()*48),
                            Color.WHITE, 0, 0));
        }

        if (deadCounter == 0){
            getScene().removeSprite(this);
            Powerups pDef = getPowerupDrop();
            Ability ability = null;
            if (pDef == Powerups.AbilityGem) ability = getAbilityDrop();
            if (pDef != null) getScene().addSprite(new Powerup(getScene(), pDef, ability, (int) getX(), (int) getY()));
        }
    }

    protected float determineSpeed(){
        float currentSpeed = getSpeed();
        if (isDead()) return 0;
        if (!flying && getTileBehaviorsUnderSprite().contains(Block.Trait.Slows)) currentSpeed *= 0.3f;
        if (statusEffects.containsKey(StatusEffect.Speed)) currentSpeed *= 1.5;
        if (statusEffects.containsKey(StatusEffect.Slowed)) currentSpeed *= 0.3f;
        if (hasSkill(Skills.Sprinter)) currentSpeed *= 1.5;

        //if we're stoneskinned go slow
        if (hasStatusEffect(StatusEffect.Stoneskin)){
            if (currentSpeed >= 0) currentSpeed = Math.min(currentSpeed, 1);
            else currentSpeed = Math.max(currentSpeed, -1);
        }

        if (chargingAhead) currentSpeed *= 2;

        return currentSpeed;
    }

    protected Facing determineFacing(double heading){ return Facing.nearestFacing(heading); }

    protected Point2D determineMovementVector(float speed, double heading, boolean slippery){
        float xa = 0, ya = 0;
        if (slippery){
            float s = speed;
            xa = (float)(lastSlipperyMovementVector.getX()*0.95f+(Math.cos(heading)*s/10f));
            ya = (float)(lastSlipperyMovementVector.getY()*0.95f-(Math.sin(heading)*s/10f));

            //limit this to speed*1.5
            double sv = Point2D.distance(0, 0, xa, ya);
            double mv = 15*1.5d; //means 15 is max speed on ice
            if (sv > mv){
                float ratio = (float)(mv/sv);
                xa = xa * ratio;
                ya = ya * ratio;
            }
        }
        else if (speed != 0){
            xa = (float)(Math.cos(heading)*speed);
            ya = -(float)(Math.sin(heading)*speed);
        }
        return new Point2D.Float(xa, ya);
    }

    protected Point2D determineKnockbackVector(){
        if (canBeKnockedback && knockbackCounter > 0)
            return new Point2D.Float((float)(Math.cos(knockbackHeading)*8), -(float)(Math.sin(knockbackHeading)*8));
        else return new Point2D.Float(0, 0);
    }

    protected Point2D sumVectors(List<Point2D> vectors){
        float x = 0, y = 0;
        for (Point2D v: vectors){
            x += v.getX();
            y += v.getY();
        }
        return new Point2D.Float(x, y);
    }

    protected void checkForScreenEdge(){
        if (isBlockableByScreenEdge()){
            if (getX()-getWidth()/2 < 0){
                setX(getWidth()/2);
                setXa(0);
                chargingAhead = false;
            }
            if (getX()+getWidth()/2 > getScene().areaGroup.getWidth() * 16){
                setX(getScene().areaGroup.getWidth() * 16 - getWidth()/2);
                setXa(0);
                chargingAhead = false;
            }
            if (getY()-getHeight() < 0){
                setY(getHeight());
                setYa(0);
                chargingAhead = false;
            }
            if (getY() > getScene().areaGroup.getHeight() * 16){
                setY(getScene().areaGroup.getHeight() * 16);
                setYa(0);
                chargingAhead = false;
            }
        }
        else{
            if ((getX()-getWidth()/2 < 0) ||
                    (getX()+getWidth()/2 > getScene().areaGroup.getWidth() * 16) ||
                    (getY()-getHeight() < 0) ||
                    (getY() > getScene().areaGroup.getHeight() * 16)){
                getScene().removeSprite(this);
            }
        }
    }

    protected void updateStatusEffects(){
        //tick all effects, mark any we need to remove
        List<StatusEffect> toRemove = new ArrayList<StatusEffect>();
        for (StatusEffect effect: statusEffects.keySet()){
            int value = statusEffects.get(effect)-1;
            statusEffects.put(effect, value);
            if (value <= 0) toRemove.add(effect);
        }

        if (statusEffects.containsKey(StatusEffect.Shield)){
            setXa(0);
            setYa(0);
            setMovementLockedCounter(2);
        }

        if (statusEffects.containsKey(StatusEffect.Drowning) || statusEffects.containsKey(StatusEffect.Petrified) ||
                statusEffects.containsKey(StatusEffect.Frozen) || statusEffects.containsKey(StatusEffect.Stunned) ||
                statusEffects.containsKey(StatusEffect.Electrocuted)){
            setMovementLockedCounter(0); //just in case
            setXa(0);
            setYa(0);
            setMoving(false);
            setMovementLockedCounter(2);
            setHeadingLockedCounter(2);
        }

        if (toRemove.size() > 0){
            //remove any effects past their expiration
            for (StatusEffect effect: toRemove){
                if (effect == StatusEffect.Invisible) setFadeRatio(1f);

                //make invulnerable if coming out of a stunning effect
                else if (effect == StatusEffect.Drowning || effect == StatusEffect.Petrified || effect == StatusEffect.Frozen ||
                        effect == StatusEffect.Stunned || effect == StatusEffect.Electrocuted)
                    setInvulnerableCounter(32);

                statusEffects.remove(effect);
            }

            if (isDesaturated() && !hasStatusEffect(StatusEffect.Stoneskin) && !hasStatusEffect(StatusEffect.Petrified))
                setDesaturated(false);
        }

        //update our outline
        if (statusEffects.size() > 0){
            StatusEffect e = null;
            int max = Integer.MIN_VALUE;
            for (StatusEffect effect: statusEffects.keySet()){
                if (statusEffects.get(effect) > max && effect.getColor() != null){
                    max = statusEffects.get(effect);
                    e = effect;
                }
            }
            if (e != null)
                addDecorator(new OutlineDecorator(e.getColor(), 0, 2));
        }
    }

    private boolean wasBlocked = false;
    protected boolean blocked(){ return wasBlocked; }

    public void move(){
        //if we're dead, deal with that
    	if (deadCounter > 0) dead();

        //decrement invulnerability (from coming back from death)
    	if (invulnerableCounter > 0) invulnerableCounter--;
        if (knockbackCounter > 0) knockbackCounter--;

        if (surprisedTime > 0) surprisedTime--;

        //update our status effects
        updateStatusEffects();

        //update hp meter counter if it's visible
    	if (hpMeterCounter > 0) hpMeterCounter -= 1;

        //determine working speed and movement
        float currentSpeed = determineSpeed();
        if (!moving) currentSpeed = 0;
        runTime += currentSpeed;

        if (headingLockedCounter > 0) headingLockedCounter--;
        if (movementLockedCounter > 0) movementLockedCounter--;
        if (wailCounter > 0){
            if (wailCounter%3 == 0) getScene().addSprite(new SonicBlast(getScene(), this, getHeading(), true));
            wailCounter--;
        }
        if (shoutCounter > 0){
            if (shoutCounter%3 == 0) getScene().addSprite(new SonicBlast(getScene(), this, getHeading(), false));
            shoutCounter--;
        }

        //deal with tunnelling
        if (tunnelDownTime > 0){
            tunnelDownTime--;
            setFacing(Facing.nextFacing(getFacing()));
            if (tunnelDownTime == maxTunnelTime-5){
                getScene().addSprite(new DigDirt(getScene(), (int)getX(), (int)getY(), 11));
                getScene().getSound().play(Art.getSample("rip.wav"), this, 1, 1, 1);
            }
            if (tunnelDownTime == 0){
                tunnelDelayCounter = maxTunnelDelayCounter;
                setVisible(false);
            }
        }
        else if (tunnelDelayCounter > 0){
            setVisible(false);
            setCollidable(false);

            tunnelDelayCounter--;

            //move along path between start and destination so camera follows smoothly
            if (tunnelDelayCounter > 0){
                double ratio = (maxTunnelDelayCounter-tunnelDelayCounter)/(double)maxTunnelDelayCounter;
                int xDiff = tunnelLoc.x-tunnelFromLoc.x, yDiff = tunnelLoc.y-tunnelFromLoc.y;
                int nx = tunnelFromLoc.x+(int)(ratio*xDiff), ny = tunnelFromLoc.y+(int)(ratio*yDiff);
                setX(nx);
                setY(ny);
            }
            else{
                //teleport to just in front of player
                setX(tunnelLoc.x);
                setY(tunnelLoc.y);
                tunnelUpTime = maxTunnelTime;
                setCollidable(true);
            }
        }
        else if (tunnelUpTime > 0){
            setVisible(true);
            if (tunnelUpTime == maxTunnelTime){
                getScene().addSprite(new DigDirt(getScene(), (int)getX(), (int)getY(), 11));
                getScene().getSound().play(Art.getSample("rip.wav"), this, 1, 1, 1);
            }
            tunnelUpTime--;
            setFacing(Facing.nextFacing(getFacing()));
            if (tunnelUpTime == 0){
                tunnelLoc = null;
                tunnelFromLoc = null;
            }
        }

        //deal with spinning and/or facing
        Block b = getScene().areaGroup.getBlock((int)(getX()/16), (int)(getY()/16), Area.Layer.Main);
        boolean slippery = b.frozenCounter > 0 && !isFlying() && getTileBehaviorsUnderSprite().contains(Block.Trait.Water);

        if (headingLockedCounter == 0 && !chargingAhead && tunnelLoc == null){
            if (spinCounter > 0){
                spinCounter--;
                setFacing(Facing.nextFacing(getFacing()));
                if (spinCounter%5==0)
                    getScene().getSound().play(Art.getSample("wind.wav"), this, 1, 1, 1);
            }
            else if (flameSpinCounter > 0){
                flameSpinCounter--;
                setFacing(Facing.nextFacing(getFacing()));
                puff(3, null);
                if (spinCounter%5==0)
                    getScene().getSound().play(Art.getSample("fireSwoosh.wav"), this, 1, 1, 1);
            }
            else{
                //determine facing from heading
                setFacing(determineFacing(getHeading()));
            }
        }

        if (movementLockedCounter == 0){
            //determine movement vector as sum of all forces acting on us
            List<Point2D> mvs = new ArrayList<Point2D>();
            if (chargingAhead){
                movementVectors.clear();
                mvs.add(determineMovementVector(12f, getHeading(), slippery));
                if (Math.random() < 0.05) getScene().addSprite(new Puff(getScene(), (int)getX(), (int)getY(), Color.WHITE));
            }
            else{
                Point2D kv = determineKnockbackVector();
                if (kv.getX() == 0 && kv.getY() == 0)
                    mvs.add(determineMovementVector(currentSpeed, getHeading(), slippery)); //set slippery to false as are dealing with it later
                mvs.add(kv);
                for (TMV tmv: new ArrayList<TMV>(movementVectors)){
                    tmv.ticks--;
                    if (tmv.ticks < 0) movementVectors.remove(tmv);
                    else mvs.add(new Point2D.Double(tmv.x, tmv.y));
                }
            }
            Point2D sum = sumVectors(mvs);

            if (!isDead()){
                setXa((float)sum.getX());
                setYa((float)sum.getY());
            }
            else{
                setXa(0);
                setYa(0);
            }
        }

        //stop at edges
        checkForScreenEdge();

        //record current vector if we're on slippery ground
        lastSlipperyMovementVector = new Point2D.Float(getXa(), getYa());

        //do our actual moving, with collisions
        boolean blocked = false;
        blocked |= move(getXa(), 0);
        blocked |= move(0, getYa());
        wasBlocked = blocked;

        if (!wasBlocked && !getTileBehaviorsUnderSprite().contains(Block.Trait.Water))
            lastUnblockedPosition = new Point2D.Float(getX(), getY());

        //override fade ratio if invisible or invulnerable
        if (getInvulnerableCounter() > 0 || hasStatusEffect(StatusEffect.Invisible))
            addDecorator(new FadeDecorator(0.3f, 2));

        //deal with any status effects
        if (statusEffects.containsKey(StatusEffect.Poisoned) && getTick()%20 == 0)
            damage(new DamageAttributes(1, Arrays.asList(Attribute.PoisonDamage)));

        if (statusEffects.containsKey(StatusEffect.Electrocuted) && getTick()%32 == 0)
            damage(new DamageAttributes(1, Arrays.asList(Attribute.ElectrocuteDamage)));

        if (statusEffects.containsKey(StatusEffect.Drowning) && getTick()%10 == 0)
            damage(new DamageAttributes(1, Arrays.asList(Attribute.Water)));

        minibossMove();

        //determine image
        calcPic();

        //deal with a few soundfx
        if (isMoving()){
            if (getTick()%10 == 0 && hasStatusEffect(StatusEffect.Stoneskin))
                getScene().getSound().play(Art.getSample("thunkMetal.wav"), this, 1, 1, 1);
            else if (getTick()%3 == 0 && chargingAhead)
                getScene().getSound().play(Art.getSample("stomp.wav"), this, 1, 1, 1);
        }
        if (hasStatusEffect(StatusEffect.Drowning) && getTick()%8 == 1)
            getScene().getSound().play(Art.getSample("drown.wav"), this, 1, 1, 1);
    }

    public void tunnelTo(Point loc){
        tunnelLoc = loc;
        tunnelFromLoc = new Point((int)getX(), (int)getY());
        tunnelDownTime = maxTunnelTime;
    }

    public void resetToLastUnblockedPosition(){
        setX((float)lastUnblockedPosition.getX());
        setY((float)lastUnblockedPosition.getY()+4);
        setXa(0);
        setYa(0);
        movementVectors.clear();
    }

    private Point findNearestUnblockedTile(Point start, Point current, Rectangle footprint, Point bestSoFar, Set<Integer> marks){
        Integer key = current.x*getScene().areaGroup.getCurrentArea().getWidth()+current.y;

        //if we're marked, return
        if (marks.contains(key)) return bestSoFar;

        //mark ourselves
        marks.add(key);

        //if we're equal to or further away than the best so far, return early
        if (bestSoFar != null){
            double distance = Point2D.distance(start.x, start.y, current.x, current.y);
            double distanceFromBest = Point2D.distance(start.x, start.y, bestSoFar.x, bestSoFar.y);
            if (distance >= distanceFromBest)
                return bestSoFar;
        }

        //see if we can fit where we are; if so we become the best so far
        Rectangle translatedFootprintRect = new Rectangle(current.x*16+8-footprint.width/2, current.y*16+15-footprint.height,
            footprint.width, footprint.height);
        Footprint translatedFootprint = new Footprint(new Point2D.Double[]{
                new Point2D.Double(current.x*16+8+footprint.width/2d, current.y*16+15),
                new Point2D.Double(current.x*16+8-footprint.width/2d, current.y*16+15),
                new Point2D.Double(current.x*16+8-footprint.width/2d, current.y*16+15-footprint.height),
                new Point2D.Double(current.x*16+8+footprint.width/2d, current.y*16+15-footprint.height)
            });
        if (getScene().propsBlockingMovement(translatedFootprintRect).size() == 0 &&
                getScene().tileBlocking(translatedFootprint, isFlying()) == null)
            bestSoFar = current;

        //check each of our sides if they're not marked; if any return something return it
        if (current.x > 0) bestSoFar = findNearestUnblockedTile(start, new Point(current.x-1, current.y), footprint, bestSoFar, marks);
        if (current.x < getScene().areaGroup.getCurrentArea().getWidth())
            bestSoFar = findNearestUnblockedTile(start, new Point(current.x+1, current.y), footprint, bestSoFar, marks);
        if (current.y > 0) bestSoFar = findNearestUnblockedTile(start, new Point(current.x, current.y-1), footprint, bestSoFar, marks);
        if (current.y < getScene().areaGroup.getCurrentArea().getHeight())
            bestSoFar = findNearestUnblockedTile(start, new Point(current.x, current.y+1), footprint, bestSoFar, marks);

        return bestSoFar;
    }

    protected void puff(int numSparkles, Color color){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*Math.max(getWPic()/2, getHPic()/2));
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            if (color == null)
                getScene().addSprite(new Puff(getScene(),
                    (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset-8)));
            else
                getScene().addSprite(new Puff(getScene(),
                    (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset-8), color));
        }
    }

    protected void calcPic(){}

    public boolean isProjectile(){ return false; }

    //by default we're "collideable" with anything (use setCollideable(false) to make not collide); just override
    //  this to return false if for some reason we shouldn't collide with the other prop
    public boolean isCollidableWith(Prop prop){ return true; }

    //returns true if we're blocked
    protected boolean move(float xa, float ya){
    	//recursively try to move
        float step = 4f;
    	while (xa > step){
            if (move(step, 0)) return true;
            xa -= step;
        }
        while (xa < -step){
            if (move(-step, 0)) return true;
            xa += step;
        }
        while (ya > step){
            if (move(0, step)) return true;
            ya -= step;
        }
        while (ya < -step){
            if (move(0, -step)) return true;
            ya += step;
        }

        float x = getX(), y = getY(), width = getWidth(), height = getHeight();

        //see if any tiles or other sprites are blocking us
        Point blockingTile = null;
        List<Prop> blockingProps = null, collidingProps = null;

        if (!isDead() && isCollidable()){
            Footprint footprint = new Footprint(new Point2D.Double[]{new Point2D.Double(x-width/2d, y), new Point2D.Double(x-width/2d, y-height), new Point2D.Double(x+width/2d, y-height), new Point2D.Double(x+width/2d, y)});
            Rectangle2D.Float footRect = new Rectangle2D.Float(x-width/2f, y-height, width, height);
//            blockingTile = getScene().tileBlocking(footprint, flying);
//            collidingProps = getScene().propsColliding(this, footRect);
//            blockingProps = getScene().propsBlocking(this, footRect);
//
//            //if not, see if they will once we move
//            if (blockingTile == null && collidingProps.size() == 0 && blockingProps.size() == 0){
                footprint.translate(xa, ya);
                footRect.x += xa;
                footRect.y += ya;
                blockingTile = getScene().tileBlocking(footprint, flying);
                collidingProps = getScene().propsColliding(this, footRect);
                blockingProps = getScene().propsBlocking(this, footRect);
//            }
        }

        //notify of any collisions
        if (collidingProps != null)
            for (Prop collide: collidingProps){
                if (collide.isDead()) continue; //happens when props die during this

                //do mutual impact damage
                if ((impactDamagesPlayerOnly && collide == getScene().player) || !impactDamagesPlayerOnly)
                    if (getImpactDamageAttributes() != null)
                        collide.damage(getImpactDamageAttributes());

                if ((collide.isImpactDamagesPlayerOnly() && this == getScene().player) || !collide.isImpactDamagesPlayerOnly())
                    if (collide.getImpactDamageAttributes() != null)
                        damage(collide.getImpactDamageAttributes());

                //if we ran into something that knocks us back and we can be knocked back (or vice versa), do that
                if (canBeKnockedback && collide.getImpactDamageAttributes() != null && collide.getImpactDamageAttributes().getAttributes().contains(Attribute.Knockback)){
                    double xDiff = getX()-collide.getX();
                    double yDiff = getY()-collide.getY();
                    double atan = Math.atan2(xDiff, yDiff);
                    if(atan < 0) atan += Math.PI*2;
                    atan -= Math.PI/2f;
                    double knockbackHeading = atan;

                    setKnockbackCounter(4);
                    setKnockbackHeading(knockbackHeading);
                }

                if (collide.isCanBeKnockedback() && getImpactDamageAttributes() != null && getImpactDamageAttributes().getAttributes().contains(Attribute.Knockback)){
                    double xDiff = collide.getX()-getX();
                    double yDiff = collide.getY()-getY();
                    double atan = Math.atan2(xDiff, yDiff);
                    if(atan < 0) atan += Math.PI*2;
                    atan -= Math.PI/2f;
                    double knockbackHeading = atan;

                    collide.setKnockbackCounter(4);
                    collide.setKnockbackHeading(knockbackHeading);
                }

                //if either of us die when we collide with something, die
                if ((diesOnCollideWithPlayer && collide == getScene().player) || (diesOnCollide && !collide.getId().equals(getId())))
                    die();
                if ((collide.isDiesOnCollideWithPlayer() && this == getScene().player) || (collide.isDiesOnCollide() && !collide.getId().equals(getId())))
                    collide.die();

                collided(collide);
                collide.collided(this);

                if (isDead()) break;
            }

        //notify of blocking tile
        if (blockingTile != null){
            if (isDiesOnCollide())
                die();
        }

        boolean blocked = blockingTile != null || (blockingProps != null && blockingProps.size() > 0);
        if (blocked){
            //if was charging, make a thunk
            if (chargingAhead) getScene().getSound().play(Art.getSample("collide.wav"), this, 1, 1, 1);
            chargingAhead = false;

            if (xa < 0) setXa(0);
            if (xa > 0) setXa(0);
            if (ya < 0) setYa(0);
            if (ya > 0) setYa(0);
        }
        else{
            setX(x+xa);
            setY(y+ya);
        }

        return blocked;
    }

    protected void collided(Prop with){}

    public boolean isCollidable(){ return collidable; }
    public void setCollidable(boolean collidable){ this.collidable = collidable; }

    public boolean isFlying(){ return flying; }
    public void setFlying(boolean flying){ this.flying = flying; }

    public boolean isCollidesWithPlayerOnly(){ return collidesWithPlayerOnly; }
    public void setCollidesWithPlayerOnly(boolean collidesWithPlayerOnly){ this.collidesWithPlayerOnly = collidesWithPlayerOnly; }

    public boolean isBlocksMovement(){ return blocksMovement; }
    public void setBlocksMovement(boolean blocksMovement){ this.blocksMovement = blocksMovement; }

    public boolean isBlocksFlying(){ return blocksFlying; }
    public void setBlocksFlying(boolean blocksFlying){ this.blocksFlying = blocksFlying; }

    public boolean isBlockable(){ return blockable; }
    public void setBlockable(boolean blockable){ this.blockable = blockable; }

    public boolean isBlockableByScreenEdge(){ return blockableByScreenEdge; }
    public void setBlockableByScreenEdge(boolean blockableByScreenEdge){ this.blockableByScreenEdge = blockableByScreenEdge; }

    public boolean isImpactDamagesPlayerOnly(){ return impactDamagesPlayerOnly; }
    public void setImpactDamagesPlayerOnly(boolean impactDamagesPlayerOnly){ this.impactDamagesPlayerOnly = impactDamagesPlayerOnly; }

    public boolean isDiesOnCollide(){ return diesOnCollide; }
    public void setDiesOnCollide(boolean diesOnCollide){ this.diesOnCollide = diesOnCollide; }

    public boolean isDiesOnCollideWithPlayer(){ return diesOnCollideWithPlayer; }
    public void setDiesOnCollideWithPlayer(boolean diesOnCollideWithPlayer){ this.diesOnCollideWithPlayer = diesOnCollideWithPlayer; }

    public boolean isDead(){ return dead; }
    public void setDead(boolean dead){ this.dead = dead; }

    public boolean isCanBeKnockedback(){ return canBeKnockedback; }
    public void setCanBeKnockedback(boolean canBeKnockedback){ this.canBeKnockedback = canBeKnockedback; }

    public boolean isMoving(){ return moving; }
	public void setMoving(boolean moving){ this.moving = moving; }

    public boolean isChargingAhead(){ return chargingAhead; }
    public void setChargingAhead(boolean chargingAhead){ this.chargingAhead = chargingAhead; }

    public float getRunTime(){ return runTime; }
	public void setRunTime(float runTime){ this.runTime = runTime; }

	public int getHp(){ return hp; }
	public void setHp(int hp){ this.hp = hp; }

	public int getMaxHp(){ return maxHp; }
	public void setMaxHp(int maxHp){ this.maxHp = maxHp; }

    public float getSpeed(){ return speed; }
	public void setSpeed(float speed){ this.speed = speed; }

	public int getKnockbackCounter(){ return knockbackCounter; }
	public void setKnockbackCounter(int knockbackCounter){ this.knockbackCounter = knockbackCounter; }

	public double getKnockbackHeading(){ return knockbackHeading; }
	public void setKnockbackHeading(double knockbackHeading){ this.knockbackHeading = knockbackHeading; }

    public int getEnteringTime(){ return enteringTime; }
    public void setEnteringTime(int enteringTime){ this.enteringTime = enteringTime; }

    public int getExitingTime(){ return exitingTime; }
    public void setExitingTime(int exitingTime){ this.exitingTime = exitingTime; }

    public int getTeleportInTime(){ return teleportInTime; }
    public void setTeleportInTime(int teleportInTime){ this.teleportInTime = teleportInTime; }

    public int getTeleportOutTime(){ return teleportOutTime; }
    public void setTeleportOutTime(int teleportOutTime){ this.teleportOutTime = teleportOutTime; }

    public String getTeleportToLevel(){ return teleportToLevel; }
    public void setTeleportToLevel(String teleportToLevel){ this.teleportToLevel = teleportToLevel; }

    public Point2D.Float getTeleportOutFromPoint(){ return teleportOutFromPoint; }
    public void setTeleportOutFromPoint(Point2D.Float teleportOutFromPoint){ this.teleportOutFromPoint = teleportOutFromPoint; }

    public int getDeadCounter(){ return deadCounter; }
    public void setDeadCounter(int deadCounter){ this.deadCounter = deadCounter; }

    public int getInvulnerableCounter(){ return invulnerableCounter; }
    public void setInvulnerableCounter(int invulnerableCounter){ this.invulnerableCounter = invulnerableCounter; }

    public int getSpinCounter(){ return spinCounter; }
    public void setSpinCounter(int spinCounter){ this.spinCounter = spinCounter; }

    public int getFlameSpinCounter(){ return flameSpinCounter; }
    public void setFlameSpinCounter(int flameSpinCounter){ this.flameSpinCounter = flameSpinCounter; }

    public int getHeadingLockedCounter(){ return headingLockedCounter; }
    public void setHeadingLockedCounter(int headingLockedCounter){ this.headingLockedCounter = headingLockedCounter; }

    public int getMovementLockedCounter(){ return movementLockedCounter; }
    public void setMovementLockedCounter(int movementLockedCounter){ this.movementLockedCounter = movementLockedCounter; }

    public int getWailCounter(){ return wailCounter; }
    public void setWailCounter(int wailCounter){ this.wailCounter = wailCounter; }

    public int getShoutCounter(){ return shoutCounter; }
    public void setShoutCounter(int shoutCounter){ this.shoutCounter = shoutCounter; }

    public Ability getAbilityDrop(){ return abilityDrop; }
    public void setAbilityDrop(Ability abilityDrop){ this.abilityDrop = abilityDrop; }

    public DamageAttributes getImpactDamageAttributes(){ return impactDamageAttributes; }
    public void setImpactDamageAttributes(DamageAttributes impactDamageAttributes){ this.impactDamageAttributes = impactDamageAttributes; }

    public Powerups getPowerupDrop(){ return powerupDrop; }
    public void setPowerupDrop(Powerups powerupDrop){ this.powerupDrop = powerupDrop; }

    public boolean isSuppressHpMeter(){ return suppressHpMeter; }
    public void setSuppressHpMeter(boolean suppressHpMeter){ this.suppressHpMeter = suppressHpMeter; }

    public boolean isBoss(){ return boss; }
    public void setBoss(boolean boss){ this.boss = boss; }

    public void addMovementVector(Point2D v, int time){ movementVectors.add(new TMV(v.getX(), v.getY(), time, false)); }

    public boolean isImmuneToDamage(){ return immuneToDamage; }
    public void setImmuneToDamage(boolean immuneToDamage){ this.immuneToDamage = immuneToDamage; }

    public boolean isImmuneToStatusEffects(){ return immuneToStatusEffects; }
    public void setImmuneToStatusEffects(boolean immuneToStatusEffects){ this.immuneToStatusEffects = immuneToStatusEffects; }

    public Skills[] getSkills(){ return skills; }
    public void setSkills(Skills[] skills){ this.skills = skills; }
    public boolean hasSkill(Skills skill){ return skills != null && (skills[0] == skill || (skills.length > 1 && skills[1] == skill)); }

    public boolean isShadowVisible(){ return shadowVisible; }
    public void setShadowVisible(boolean shadowVisible){ this.shadowVisible = shadowVisible; }

    public int getShadowYOffset(){ return shadowYOffset; }
    public void setShadowYOffset(int shadowYOffset){ this.shadowYOffset = shadowYOffset; }

    public int getSurprisedTime(){ return surprisedTime; }
    public void setSurprisedTime(int surprisedTime){ this.surprisedTime = surprisedTime; }

    public int getHpMeterCounter(){ return hpMeterCounter; }

    public Map<StatusEffect, Integer> getStatusEffects(){ return statusEffects; }
    public boolean hasStatusEffect(StatusEffect status){ return statusEffects.keySet().contains(status); }
    public void removeStatusEffect(StatusEffect status){
        statusEffects.remove(status);

        //remove our outline forcibly
        for (AbstractSpriteDecorator decorator: new ArrayList<AbstractSpriteDecorator>(getDecorators())){
            if (decorator instanceof OutlineDecorator){
                Color[] dcolors = ((OutlineDecorator)decorator).getColors();
                if ((dcolors.length == 1 && dcolors[0] == status.getColor()))
                    getDecorators().remove(decorator);
            }
        }
    }

    public Point getTunnelLoc(){ return tunnelLoc; }

    public Ability getMinibossAbilityDrop(){ return minibossAbilityDrop; }
    public void setMinibossAbilityDrop(Ability minibossAbilityDrop){ this.minibossAbilityDrop = minibossAbilityDrop; }

    public Powerups getMinibossPowerupDrop(){ return minibossPowerupDrop; }
    public void setMinibossPowerupDrop(Powerups minibossPowerupDrop){ this.minibossPowerupDrop = minibossPowerupDrop; }

    public boolean isMiniboss(){ return miniboss; }
    public void setMiniboss(boolean miniboss){ this.miniboss = miniboss; }

    public boolean isMinibossifiable(){ return minibossifiable; }
    public void setMinibossifiable(boolean minibossifiable){ this.minibossifiable = minibossifiable; }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = new DamageAttributes(attributes.getDamage(), new ArrayList<Attribute>(attributes.getAttributes()));

        //if we're petrified or invulnerable, we take no damage; if already stunned can't be stunned that same way again
        int damage = attributes.getDamage();
        if (statusEffects.containsKey(StatusEffect.Drowning)) modified.getAttributes().remove(Attribute.Drown);
        else if (statusEffects.containsKey(StatusEffect.Petrified)){
            damage = 0;
            modified.getAttributes().remove(Attribute.Petrify);
        }
        else if (statusEffects.containsKey(StatusEffect.Frozen)) modified.getAttributes().remove(Attribute.Freeze);
        else if (statusEffects.containsKey(StatusEffect.Stunned)) modified.getAttributes().remove(Attribute.Stun);
        else if (statusEffects.containsKey(StatusEffect.Electrocuted)) modified.getAttributes().remove(Attribute.Electric);

        //if invulnerable, can't be stunned or damaged
        if (invulnerableCounter > 0){
            damage = 0;
            modified.getAttributes().remove(Attribute.Drown);
            modified.getAttributes().remove(Attribute.Petrify);
            modified.getAttributes().remove(Attribute.Freeze);
            modified.getAttributes().remove(Attribute.Stun);
            modified.getAttributes().remove(Attribute.Electric);
        }

        if (statusEffects.containsKey(StatusEffect.Osmose)){
            int hpGain = 0;
            if (attributes.getAttributes().contains(Attribute.Fire) && getScene().getGameState().getFireAffinity() > 0)
                hpGain = Math.max(damage, getScene().getGameState().getFireAffinity());
            else if (attributes.getAttributes().contains(Attribute.Water) && getScene().getGameState().getWaterAffinity() > 0)
                hpGain = Math.max(damage, getScene().getGameState().getWaterAffinity());
            else if (attributes.getAttributes().contains(Attribute.Wind) && getScene().getGameState().getAirAffinity() > 0)
                hpGain = Math.max(damage, getScene().getGameState().getAirAffinity());
            else if (attributes.getAttributes().contains(Attribute.Earth) && getScene().getGameState().getEarthAffinity() > 0)
                hpGain = Math.max(damage, getScene().getGameState().getEarthAffinity());
            else if (attributes.getAttributes().contains(Attribute.Spirit) && getScene().getGameState().getSpiritAffinity() > 0)
                hpGain = Math.max(damage, getScene().getGameState().getSpiritAffinity());

            if (hpGain > 0){
                attributes.setDamage(hpGain);
                attributes.setAttributes(Arrays.asList(Attribute.Heal));
            }
        }

        if (attributes.getAttributes().contains(Attribute.Heal) ||
                (attributes.getAttributes().contains(Attribute.PoisonDamage) && hasSkill(Skills.ToxicBlood))){
            damage = -damage;

            //apply speedy healing damage
            if (hasSkill(Skills.SpeedyHealing)) damage *= 1.5;
        }

        modified.setDamage(damage);
        return modified;
    }

    public void damage(DamageAttributes attributes){
    	//short-circuits for certain easy conditions
    	if (isDead()) return;

        //Modify damage according to any resistances or abilities we might have
        DamageAttributes modified = modifyDamageAttributes(attributes);

        if (!immuneToStatusEffects){
            //Status Effects
            for (Attribute attribute: modified.getAttributes()){
                StatusEffect statusEffect = StatusEffect.forAttribute(attribute);
                if (statusEffect != null){
                    if (!hasStatusEffect(statusEffect) && !statusEffect.isSuppressEffect()){
                        addTextEffect(statusEffect.name()+"!", statusEffect.isHarmful() ? TextImageCreator.COLOR_RED : TextImageCreator.COLOR_CYAN);
                    }

                    if (!hasStatusEffect(statusEffect) && statusEffect != StatusEffect.Shield && statusEffect != StatusEffect.Osmose){
                        if (statusEffect.isHarmful()) getScene().getSound().play(Art.getSample("badStatusEffect.wav"), this, 1, 1, 1);
                        else getScene().getSound().play(Art.getSample("goodStatusEffect.wav"), this, 1, 1, 1);
                    }

                    statusEffects.put(statusEffect, statusEffect.getDuration());

                    if (statusEffect == StatusEffect.Frozen)
                        getScene().addSprite(FadingImageEffect.createIceEffect(this));

                    if (hasStatusEffect(StatusEffect.Stoneskin) || hasStatusEffect(StatusEffect.Petrified))
                        setDesaturated(true);
                }
            }

            //Instant Effects
            //  Cleanse
            if (modified.getAttributes().contains(Attribute.Cleanse)){
                //remove all harmful status effects
                for (StatusEffect effect: statusEffects.keySet())
                    if (effect.isHarmful()) statusEffects.remove(effect);
                addTextEffect("Cleansed!", TextImageCreator.COLOR_WHITE);
                getScene().getSound().play(Art.getSample("cleanse.wav"), this, 1, 1, 1);
            }

            //  Death
            if (modified.getAttributes().contains(Attribute.Death)){ //20% chance of death
                if (Math.random()*100 < 20){
                    setHp(0);
                    addTextEffect("DEAD", TextImageCreator.COLOR_MAGENTA);
                }
            }
        }

        if (!immuneToDamage){
            //apply damage and constrain to maxHp 0r 0
            setHp(getHp()-modified.getDamage());
            if (getHp() <= 0){
                die();
                if (getHp() < 0) setHp(0);
            }
            else if (getHp() > getMaxHp()) setHp(getMaxHp());

            //show strike effects
            if (modified.getDamage() != 0) addDamageEffects(attributes);

            //tint sprite green or red, possibly show hp meter
            if (!suppressHpMeter){
                if (modified.getDamage() > 0) addDecorator(new TintDecorator(COLOR_TINT_DAMAGE, 10));
                if (modified.getDamage() < 0) addDecorator(new TintDecorator(COLOR_TINT_HEALED, 10));
                if (modified.getDamage() != 0 && !isDead()) hpMeterCounter = 30;
            }

            if (modified.getDamage() > 0){
                if (!(modified.getAttributes().contains(Attribute.PoisonDamage) && modified.getAttributes().size() == 1)){
                    //use original damage amount, not any reduced amount
                    if (attributes.getDamage() > 4) setInvulnerableCounter(8);
                    else if (attributes.getDamage() > 1 ) setInvulnerableCounter(4);
                    else if (attributes.getDamage() > 0) setInvulnerableCounter(2);
                }

                getScene().getSound().play(Art.getSample("hit.wav"), this, 1, 1, 1);
            }
            else if (modified.getDamage() < 0)
                getScene().getSound().play(Art.getSample("heal.wav"), this, 1, 1, 1);

            //message critical damage at 20%
            if (hp*5 < maxHp)
                for (String message: getMessages().getOnCriticalMessages())
                    getScene().message(this, message);
        }
    }

    protected void addTextEffect(String text, int color){
        getScene().addSprite(new RisingTextEffect(getScene(), (int)getX(), (int)(getY()-getHeight()-2), text, color));
    }

    protected void addDamageEffects(DamageAttributes damageAttributes){
        int numEffectsToAdd = 1;
        if (Math.abs(damageAttributes.getDamage()) > 3) numEffectsToAdd++;
        if (Math.abs(damageAttributes.getDamage()) > 6) numEffectsToAdd++;
        if (Math.abs(damageAttributes.getDamage()) > 10) numEffectsToAdd++;
        if (Math.abs(damageAttributes.getDamage()) > 15) numEffectsToAdd++;

        if (damageAttributes.getAttributes().contains(Attribute.Physical))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createPhysicalDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Fire))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createFireDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Water))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createIceDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Wind))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createWindDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Earth))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createEarthDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Spirit))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createLightDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Poison) || damageAttributes.getAttributes().contains(Attribute.PoisonDamage))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createPoisonDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Electric) || damageAttributes.getAttributes().contains(Attribute.ElectrocuteDamage))
            for (int i = 0; i < numEffectsToAdd; i++)
                getScene().addSprite(Effect.createElectricDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Heal))
            for (int i = 0; i < 10; i++)
                getScene().addSprite(Effect.createHealDamageEffect(getScene(), this));
        if (damageAttributes.getAttributes().contains(Attribute.Cleanse))
            for (int i = 0; i < 5; i++)
                getScene().addSprite(Effect.createHealDamageEffect(getScene(), this));
    }

    public void minibossify(){
        if (!isMinibossifiable()) return;
        setMiniboss(true);
        setPowerupDrop(getMinibossPowerupDrop());
        setAbilityDrop(getMinibossAbilityDrop());
        setMaxHp(getMaxHp()*2);
        setHp(getMaxHp());
        puff(15, new Color(0.5f, 0f, 0.5f));
        getScene().addSprite(new MinibossLightningStrike(getScene(), this));
    }

    protected void minibossMove(){
        if (!isMiniboss()) return;
        setTintColor(Color.BLACK);
        setOutlineColor(new Color(0.5f, 0f, 0.5f));
    }
}

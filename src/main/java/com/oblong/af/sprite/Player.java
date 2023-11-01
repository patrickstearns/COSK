package com.oblong.af.sprite;

import com.oblong.af.GameComponent;
import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Marker;
import com.oblong.af.level.decorators.FlashingDecorator;
import com.oblong.af.models.*;
import com.oblong.af.sprite.effects.RisingTextEffect;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.sprite.projectile.PlayerOrbeholder;
import com.oblong.af.util.Art;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Player extends Actor {

    public static Player INSTANCE;
	
	private boolean justEntered = true, mouseWasDown = false;
    private Marker enteringMarker = null;
    private int maxExitTime = 16, maxTeleportTime = 16; //these need to be the same as maxFadeCounter in areaScene
    private boolean[] lastKeys = new boolean[]{false, false, false, false};

    public Player(String id, AreaScene scene, int oxPic, int oyPic){
    	super(id, scene, oxPic, oyPic);
        setPowerupDrop(Powerups.OneUp);
        setEnteringTime(0);
        setExitingTime(0);
        setRenderingOrder(6);
        setCanBeKnockedback(true);
        INSTANCE = this;
    }

    public void tickNoMove(){ tick(); }

    public void setHeading(double heading){
        if (getHeadingLockedCounter() == 0) super.setHeading(heading);
    }

    public void move(){
        if (getEnteringTime() != 0){
        	if (getEnteringTime() == 1) getScene().actionPaused = true;

            setEnteringTime(getEnteringTime()+1);
        	if (getEnteringTime() > maxExitTime){
        		AreaGroup ag = World.getInstance().findAreaGroupForPortal(enteringMarker, false);

        		if (!(enteringMarker.getLevelId().equals(ag.getId())))
        			getScene().changeAreaGroup(ag.getId());

        		Area newArea = getScene().areaGroup.findAreaForPortal(enteringMarker, false); //find entrance
        		if (getScene().areaGroup.getCurrentArea() != newArea && newArea != null){ //change area if needed
	        		getScene().areaGroup.setCurrentArea(newArea);
			        getScene().areaChanged();
        		}

                Marker exitingMarker = getScene().areaGroup.findEntranceForExit(enteringMarker);
        		
        		justEntered = true;
                setExitingTime(1);
		        setX(exitingMarker.getX()*16+8);
        		setY(exitingMarker.getY()*16+12);

                setEnteringTime(0);
        		enteringMarker = null;
        	}

        	setXa(0);
        	setYa(0);
            calcPic();
        	return;
        }

        if (getExitingTime() != 0){
        	setExitingTime(getExitingTime()+1);
            if (getExitingTime() > maxExitTime){
        		getScene().actionPaused = false;
                refreshOrbiters();
                setExitingTime(0);
            }

        	setXa(0);
        	setYa(0);
            calcPic();
        	return;
        }

        if (getTeleportInTime() != 0){
            if (getTeleportInTime() == 1) getScene().actionPaused = true;

            setTeleportInTime(getTeleportInTime() + 1);

            if (getTeleportInTime() == maxTeleportTime/2)
                getScene().getSound().play(Art.getSample("teleport.wav"), this, 1, 1, 1);

            if (getTeleportInTime() > maxTeleportTime){
                justEntered = true;
                setTeleportInTime(0);
                enteringMarker = null;
                GameComponent.INSTANCE.teleportToAreaScene(getTeleportToLevel());

                return;
            }

            setXa(0);
            setYa(0);
            calcPic();

            poof(5);

            return;
        }

        if (getTeleportOutTime() != 0){
            setTeleportOutTime(getTeleportOutTime() + 1);
            if (getTeleportOutTime() == maxTeleportTime/2)
                getScene().getSound().play(Art.getSample("teleport.wav"), this, 1, 1, 1);
            if (getTeleportOutTime() > maxTeleportTime){
                getScene().actionPaused = false;
                refreshOrbiters();
                setTeleportOutTime(0);
            }

            setXa(0);
            setYa(0);
            calcPic();

            poof(5);

            return;
        }

        if (getScene().actionPaused){
            setMoving(false);
        }

        //moving
        if (!isDead()){
            if (!getScene().isSubscreenOpen()){
                if (GameComponent.keys[GameComponent.KEY_MENU]){
                    getScene().openSubscreen();
                    return;
                }
                else{
                    if (!getScene().actionPaused){
                        boolean a1 = GameComponent.keys[GameComponent.KEY_ABILITY_1];
                        boolean a2 = GameComponent.keys[GameComponent.KEY_ABILITY_2];
                        boolean a3 = GameComponent.keys[GameComponent.KEY_ABILITY_3];
                        boolean m1 = GameComponent.mouse[1];
                        boolean la1 = lastKeys[GameComponent.KEY_ABILITY_1];
                        boolean la2 = lastKeys[GameComponent.KEY_ABILITY_2];
                        boolean la3 = lastKeys[GameComponent.KEY_ABILITY_3];
                        boolean lm1 = mouseWasDown;
                        AbilitySlot s1 = getAbilitySlots()[0];
                        AbilitySlot s2 = getAbilitySlots()[1];
                        AbilitySlot s3 = getAbilitySlots()[2];
                        Ability.TriggerType t1 = s1.ability.getTriggerType();
                        Ability.TriggerType t2 = s2.ability.getTriggerType();
                        Ability.TriggerType t3 = s3.ability.getTriggerType();
                        int c1 = (int)s1.cooldownTimer, c2 = (int)s2.cooldownTimer, c3 = (int)s3.cooldownTimer;

                        boolean cancelMove = false;
                        if (a1 && abilityEnabled(s1.ability) && abilitySlotActive(s1) && t1 == Ability.TriggerType.Charge) cancelMove = true;
                        if (a2 && abilityEnabled(s2.ability) && abilitySlotActive(s2) && t2 == Ability.TriggerType.Charge) cancelMove = true;
                        if (a3 && abilityEnabled(s3.ability) && abilitySlotActive(s3) && t3 == Ability.TriggerType.Charge) cancelMove = true;
                        setMoving(!cancelMove && GameComponent.mouse[3]);
                        if (isChargingAhead()) setMoving(true);

                        //tippity tappity
                        if (!la1 && a1 && t1 == Ability.TriggerType.Tap && c1 == 0) tap(s1);
                        if (!la2 && a2 && t2 == Ability.TriggerType.Tap && c2 == 0) tap(s2);
                        if (!la3 && a3 && t3 == Ability.TriggerType.Tap && c3 == 0) tap(s3);

                        //holding
                        if (la1 && a1 && t1 == Ability.TriggerType.Hold) hold(s1);
                        if (la2 && a2 && t2 == Ability.TriggerType.Hold) hold(s2);
                        if (la3 && a3 && t3 == Ability.TriggerType.Hold) hold(s3);

                        //release holding
                        if ((la1 && !a1) && t1 == Ability.TriggerType.Hold) release(s1);
                        if ((la2 && !a2) && t2 == Ability.TriggerType.Hold) release(s2);
                        if ((la3 && !a3) && t3 == Ability.TriggerType.Hold) release(s3);

                        //charge
                        setCharging(false);
                        if (la1 && a1 && t1 == Ability.TriggerType.Charge) charge(s1);
                        if (la2 && a2 && t2 == Ability.TriggerType.Charge) charge(s2);
                        if (la3 && a3 && t3 == Ability.TriggerType.Charge) charge(s3);

                        //clicked
                        if (!lm1 && m1) getScene().playerClicked();

                        mouseWasDown = m1;

                        //update lastKeys
                        System.arraycopy(GameComponent.keys, 0, lastKeys, 0, lastKeys.length);
                    }
                }
	        }
        }
        else setMoving(false);

        handleOrbiters();

        super.move();
    }

    protected double getProjectileHeading(){ return getScene().getPlayerMouseAngle(); }

    protected boolean move(float xa, float ya){
    	boolean blocked = super.move(xa, ya);

		//check for exit or trigger markers
        float x = getX();
        float y = getY();
		int ix = (int)(x/16);
		int iy = (int)(y/16);
		ArrayList<Marker> markers = getScene().areaGroup.getMarkers(ix, iy);
		for (Marker m: markers){
            if (!m.isActive()){
                if (m.getType() == Marker.Type.TriggerGate){
                    m.setActive(true);
                    for (String message: m.getMessages().getOnActivationMessages())
                        getScene().message(this, message);
                }
                else if (m.getType() == Marker.Type.Minibossify){
                    m.setActive(true);
                    getScene().minibossifyEnemies();
                    for (String message: m.getMessages().getOnActivationMessages())
                        getScene().message(this, message);
                }
                else continue;
            }

			if (!justEntered && (m.getType() == Marker.Type.Exit || m.getType() == Marker.Type.TwoWay)){
				setEnteringTime(1);
        		enteringMarker = m;
        		calcPic();
        	}
            else if (m.getType() == Marker.Type.ReturnExit || m.getType() == Marker.Type.LairExit){
                GameState state = getScene().getGameState();
                state.setMaxHp(getMaxHp());
                state.setEqAbId1(getAbilitySlots()[0].ability.name());
                state.setEqAbId2(getAbilitySlots()[1].ability.name());
                state.setEqAbId3(getAbilitySlots()[2].ability.name());
                state.setEqAbId4(getAbilitySlots()[3].ability.name());
                state.setSpAbId1(getSpareAbilitySlots()[0].ability.name());
                state.setSpAbId2(getSpareAbilitySlots()[1].ability.name());
                state.setSpAbId3(getSpareAbilitySlots()[2].ability.name());
                state.setSpAbId4(getSpareAbilitySlots()[3].ability.name());


                if (state.isAllBossesDead()){
                    if (state.isNemesisDead()) GameComponent.INSTANCE.toScene(GameComponent.Scenes.Ending);
                    else if (state.isStoneKingDead()){
                        if (state.isAllTownspeopleRescued()) GameComponent.INSTANCE.toScene(GameComponent.Scenes.OpenRift);
                        else GameComponent.INSTANCE.toScene(GameComponent.Scenes.Ending);
                    }
                    else GameComponent.INSTANCE.toScene(GameComponent.Scenes.OpenLair);
                }
                else{
                    setTeleportToLevel(World.HUB_AREA_GROUP_ID);
                    setTeleportInTime(1);
                }
            }
        }
		if (markers.size() == 0) justEntered = false;
		
		return blocked;
    }

    private Point randomPointWithin(Point origin, int min, int max){
        double heading = Math.random()*Math.PI*2;
        double range = min+Math.random()*(max-min);
        return new Point((int)(origin.x+Math.cos(heading)*range), (int)(origin.y+Math.sin(heading)*range));
    }

    public DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes attrs = super.modifyDamageAttributes(attributes);

        //if have an aura and the damage matches it, halve the damage
        if (
                (hasStatusEffect(StatusEffect.FlameAura) && attrs.getAttributes().contains(Attribute.Fire)) ||
                (hasStatusEffect(StatusEffect.FreezeAura) && attrs.getAttributes().contains(Attribute.Freeze)) ||
                (hasStatusEffect(StatusEffect.ShockAura) && attrs.getAttributes().contains(Attribute.Electric))){
            attrs.setDamage(attrs.getDamage()/2);
        }
        //if we're stoneskinned, damage of 1 or 2 is reduced to zero, higher than that to 1
        else if (hasStatusEffect(StatusEffect.Stoneskin)){
            if (attrs.getDamage() < 3) attrs.setDamage(0);
            else attrs.setDamage(1);
        }

        if (hasStatusEffect(StatusEffect.Shield)){
            attrs.setDamage(0);
            boolean hadShieldInIt = attrs.getAttributes().contains(Attribute.Shield);
            attrs.getAttributes().clear();
            if (hadShieldInIt) attrs.getAttributes().add(Attribute.Shield); //keeps it from flickering
        }

        return attrs;
    }

    public void damage(DamageAttributes damageAttributes){
        if (isEquipped(Ability.Blink) && damageAttributes.getAttributes().contains(Attribute.Physical)){
            Point random = randomPointWithin(new Point((int)getX(), (int)getY()), 16, 32);
            int counter = 0;
            while (getScene().getDamageablePropsWithinRange(random.x, random.y, 4).size() > 0 && counter < 10){
                random = randomPointWithin(new Point((int)getX(), (int)getY()), 16, 32);
                counter++;
            }

            if (getScene().getDamageablePropsWithinRange(random.x, random.y, 4).size() == 0){
                FlashingDecorator fd = new FlashingDecorator(2);
                fd.init(320, 240);
                getScene().areaGroup.addDecorator(fd);

                setX(random.x);
                setY(random.y);
                setXOld(random.x);
                setYOld(random.y);

                getScene().getSound().play(Art.getSample("toss.wav"), this, 1, 1, 1);
            }

            return;
        }

        super.damage(damageAttributes);

        GameState state = getScene().getGameState();

        //handle player-only powerup stuff here; hp gain is handled elsewhere.
        for (Attribute attribute: damageAttributes.getAttributes()){
            switch (attribute) {
                case MaxHpUp:
                    int gain = 5;
                    if (hasSkill(Skills.HardDrinker)) gain = 7;
                    setMaxHp(state.getMaxHp() + gain);
                    setHp(getMaxHp());
                    getScene().getSound().play(Art.getSample("oneUp.wav"), this, 1, 1, 1);
                    break;
                case OneUp:
                    state.setLives(state.getLives() + 1);
                    getScene().addSprite(new RisingTextEffect(getScene(), (int)getX(), (int)getY()-32, "1UP!", TextImageCreator.COLOR_GREEN));
                    getScene().getSound().play(Art.getSample("oneUp.wav"), this, 1, 1, 1);
                    break;
                case LearnAbility:
                    getScene().getSound().play(Art.getSample("pickupAbility.wav"), this, 1, 1, 1);
                    AbilitySlot slot = null;
                    List<AbilitySlot> allSlots = new ArrayList<AbilitySlot>();
                    if (damageAttributes.getToLearn().getTriggerType() == Ability.TriggerType.Auto){
                        allSlots.add(getAbilitySlots()[3]);
                    }
                    else{
                        allSlots.add(getAbilitySlots()[0]);
                        allSlots.add(getAbilitySlots()[1]);
                        allSlots.add(getAbilitySlots()[2]);
                    }
                    allSlots.addAll(Arrays.asList(getSpareAbilitySlots()));
                    allSlots.add(getTrashAbilitySlot());
                    for (AbilitySlot aslot: allSlots){
                        if (aslot.ability == Ability.None){
                            slot = aslot;
                            break;
                        }
                    }

                    if (slot == null) slot = getTrashAbilitySlot(); //should never happen, but eh
                    slot.ability = damageAttributes.getToLearn();

                    //flag us as having found this ability
                    damageAttributes.getToLearn().setFound(getScene().getGameState());

                    if (slot == getTrashAbilitySlot()) getScene().openSubscreen();
                    break;
                case GainAffinityPoint:
                    getScene().getSound().play(Art.getSample("oneUp.wav"), this, 1, 1, 1);
                    state.setSpareAffinity(state.getSpareAffinity() + 1);
                    if (hasSkill(Skills.QuickStudy)) state.setSpareAffinity(state.getSpareAffinity() + 1);
                    break;
            }
        }
    }

    public void die(){
        if (isEquipped(Ability.Lifespark) && abilityEnabled(Ability.Lifespark) && getAbilitySlots()[3].cooldownTimer == 0){
            setHp(getMaxHp()/2);
            getAbilitySlots()[3].cooldownTimer = Ability.Lifespark.getMaxCooldown();
            poof(100);
            addTextEffect("Revived!", TextImageCreator.COLOR_WHITE);
            getScene().getSound().play(Art.getSample("hallelujah.wav"), this, 1, 1, 1);
        }
    	else{
            super.die();
            setDeadCounter(80);
            getDecorators().clear(); //remove the fade decorator Prop adds and any others
            getScene().getSound().play(Art.getSample("playerDies.wav"), this, 1, 1, 1);
        }
    }

    private void poof(int numSparkles){
        for (int i = 0; i < numSparkles; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            int xa = xOffset/6;
            int ya = -(int)(Math.random()*3);
            Sparkle sparkle = new Sparkle(getScene(),
                    (int)(getX()+xOffset), (int)(getY()-getHeight()/2+yOffset),
                    Color.WHITE, xa, ya);
            sparkle.setYPic(1);
            getScene().addSprite(sparkle);
        }
    }

    protected void dead(){
        setDeadCounter(getDeadCounter() - 1);

		if (getDeadCounter() == 0){
			if (getScene().getGameState().getLives() > 0){
                getScene().getGameState().setLives(getScene().getGameState().getLives()-1);
				setHp(getMaxHp());
				setDead(false);
				setInvulnerableCounter(100);
                refreshOrbiters();
			}
			else{
                getScene().getGameState().setLives(2);
                GameComponent.INSTANCE.toScene(GameComponent.Scenes.Hub);
			}
		}
    }

    public boolean abilityEnabled(Ability ability){ return super.abilityEnabled(ability) && hasAffinityReguirements(ability); }

    public boolean hasAffinityReguirements(Ability ability){
        Map<Affinity, Integer> counts = new HashMap<Affinity, Integer>();
        counts.put(Affinity.Fire, getScene().getGameState().getFireAffinity());
        counts.put(Affinity.Water, getScene().getGameState().getWaterAffinity());
        counts.put(Affinity.Air, getScene().getGameState().getAirAffinity());
        counts.put(Affinity.Earth, getScene().getGameState().getEarthAffinity());
        counts.put(Affinity.Spirit, getScene().getGameState().getSpiritAffinity());
        for (Affinity aff: ability.getAffinityRequirements()){
            int val = counts.get(aff)-1;
            if (val < 0) return false;
            counts.put(aff, val);
        }
        return true;
    }

    public DamageAttributes getImpactDamageAttributes(){
        DamageAttributes s = super.getImpactDamageAttributes();
        if (s == null) s = new DamageAttributes(0, new ArrayList<Attribute>());
        if (hasStatusEffect(StatusEffect.FlameAura)){
            s.getAttributes().add(Attribute.Fire);
            s.setDamage(getScene().getGameState().getFireAffinity());
        }
        else if (hasStatusEffect(StatusEffect.FreezeAura)){
            s.getAttributes().add(Attribute.Freeze);
            s.setDamage(Math.min(getScene().getGameState().getFireAffinity(), getScene().getGameState().getWaterAffinity()));
        }
        else if (hasStatusEffect(StatusEffect.ShockAura)){
            s.getAttributes().add(Attribute.Electric);
            s.setDamage(Math.min(getScene().getGameState().getAirAffinity(), getScene().getGameState().getEarthAffinity()));
        }

        if (getSpinCounter() > 0){
            s.getAttributes().add(Attribute.Wind);
            s.setDamage(2);
        }
        if (getFlameSpinCounter() > 0){
            s.getAttributes().add(Attribute.Fire);
            s.getAttributes().add(Attribute.Wind);
            s.setDamage(4);
        }

        if (isChargingAhead()){
            s.setDamage(3);
            s.getAttributes().add(Attribute.Knockback);
            s.getAttributes().add(Attribute.Earth);
        }

        if (getTunnelLoc() != null){
            s.setDamage(5);
            s.getAttributes().add(Attribute.Knockback);
            s.getAttributes().add(Attribute.Earth);
        }

        if (s.getAttributes().size() == 0 && s.getDamage() == 0) s = null;

        return s;
    }

    private int numOrbiters = 0, maxNumOrbiters = 2;
    private int lastOrbiterDeathTick = 0, maxLastOrbiterDeathTick = Ability.Orbeholder.getMaxCooldown();
    private PlayerOrbeholder[] orbiters = new PlayerOrbeholder[maxNumOrbiters];

    //called when init'ing an area scene after having moved player to start point, or below
    public void initOrbiters(){
        if (!isEquipped(Ability.Orbeholder)) return;

        while(numOrbiters < maxNumOrbiters)
            spawnOrbiter(false);
    }

    //called when teleported or exited into a new area
    public void refreshOrbiters(){
        if (!isEquipped(Ability.Orbeholder)) return;

        for (int i = 0; i < orbiters.length; i++){
            if (orbiters[i] != null){
                getScene().removeSprite(orbiters[i]);
                orbiters[i] = null;
            }
        }
        numOrbiters = 0;
        initOrbiters();
    }

    //called at end of move method
    private boolean orbeholderWasEquipped = false;
    private void handleOrbiters(){
        if (!isDead() && isEquipped(Ability.Orbeholder)){
            if (lastOrbiterDeathTick == 0 && !orbeholderWasEquipped && !getScene().actionPaused)
                initOrbiters();

            if (lastOrbiterDeathTick > 0){
                lastOrbiterDeathTick--;
                if (lastOrbiterDeathTick == 0 && numOrbiters < maxNumOrbiters){
                    spawnOrbiter(false);
                    lastOrbiterDeathTick = maxLastOrbiterDeathTick;
                    getAbilitySlots()[3].cooldownTimer = maxLastOrbiterDeathTick;
                }
            }
        }
        else{
            for (int i = 0; i < orbiters.length; i++){
                if (orbiters[i] != null){
                    getScene().removeSprite(orbiters[i]);
                    orbiters[i] = null;
                }
            }
            numOrbiters = 0;
        }

        if (!getScene().actionPaused) orbeholderWasEquipped = isEquipped(Ability.Orbeholder);
    }

    private void spawnOrbiter(boolean green){
        //find earliest unoccupied index
        int index = 0;
        for (int i = orbiters.length-1; i >= 0; i--)
            if (orbiters[i] == null)
                index = i;

        //create spawn and record in array, add to scene
        PlayerOrbeholder spawn = new PlayerOrbeholder(this, index, green);
        orbiters[index] = spawn;
        getScene().addSprite(spawn);

        numOrbiters++;
    }

    public void orbiterDestroyed(Prop orbiter){
        int index = 0;
        for (int i = 0; i < orbiters.length; i++)
            if (orbiters[i] == orbiter)
                index = i;

        orbiters[index] = null;

        numOrbiters--;
        lastOrbiterDeathTick = maxLastOrbiterDeathTick;
        getAbilitySlots()[3].cooldownTimer = maxLastOrbiterDeathTick;
    }

}
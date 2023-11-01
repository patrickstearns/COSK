package com.oblong.af.sprite;

import com.oblong.af.GameComponent;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.behavior.AbstractActorBehavior;
import com.oblong.af.sprite.effects.ExplodeEffect;
import com.oblong.af.util.Art;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Actor extends Prop {

    public static class AbilitySlot {

        public enum SlotType {Equipped, Auto, Initial, Spare, Trash }

        public Ability ability;
        public int index;
        public float cooldownTimer, holdTimer, chargeTimer;
        public SlotType type;

        public AbilitySlot(int index, SlotType type, Ability ability){
            this.index = index;
            this.type = type;
            this.ability = ability;
            cooldownTimer = 0;
            holdTimer = 0;
            chargeTimer = 0;
        }
    }

    private boolean running, charging;
    private int portraitXPic, portraitYPic;
    private AbilitySlot trashSlot;
    private AbilitySlot[] abilitySlots, spareAbilitySlots, initialAbilitySlots;
    private AbstractActorBehavior behavior;

    public Actor(String id, AreaScene scene, int oxPic, int oyPic){
		super(id, scene, oxPic, oyPic);
		setFacing(Facing.DOWN);
        setSheet(Art.characters);
        setRenderingOrder(8);
        setRunning(false);
        setCharging(false);
        setBlockable(true);
        setBlocksMovement(true);
        setBlocksFlying(true);
        setFlying(false);
        setBlockableByScreenEdge(true);

        abilitySlots = new AbilitySlot[]{
            new AbilitySlot(0, AbilitySlot.SlotType.Equipped, Ability.None),
            new AbilitySlot(1, AbilitySlot.SlotType.Equipped, Ability.None),
            new AbilitySlot(2, AbilitySlot.SlotType.Equipped, Ability.None),
            new AbilitySlot(3, AbilitySlot.SlotType.Auto, Ability.None),
        };
        initialAbilitySlots = new AbilitySlot[]{
            new AbilitySlot(0, AbilitySlot.SlotType.Initial, Ability.None),
            new AbilitySlot(1, AbilitySlot.SlotType.Initial, Ability.None),
            new AbilitySlot(2, AbilitySlot.SlotType.Initial, Ability.None),
            new AbilitySlot(3, AbilitySlot.SlotType.Initial, Ability.None),
        };
        spareAbilitySlots = new AbilitySlot[]{
            new AbilitySlot(0, AbilitySlot.SlotType.Spare, Ability.None),
            new AbilitySlot(1, AbilitySlot.SlotType.Spare, Ability.None),
            new AbilitySlot(2, AbilitySlot.SlotType.Spare, Ability.None),
            new AbilitySlot(3, AbilitySlot.SlotType.Spare, Ability.None),
        };
        trashSlot = new AbilitySlot(0, AbilitySlot.SlotType.Trash, Ability.None);
    }

    protected void calcPic(){
    	int xOff, yOff = 0;

        switch(getFacing()){
            case LEFT: xOff = 2; break;
            case RIGHT: xOff = 3; break;
            case UP: xOff = 1; break;
            case DOWN: xOff = 0; break;
            default: xOff = 0; break;
        }

        float slowRate = 0.8f;
        if (charging) slowRate = 1f;
        int runFrame = ((int) (getTick()*slowRate)) % 4;
        if (runFrame == 0 || runFrame == 2) yOff = 0;
        else if (runFrame == 1) yOff = 1;
        else if (runFrame == 3) yOff = 2;

        if (!isMoving() && !charging) yOff = 0;
        if (charging) xOff = 4;

        if (isDead()){
    		xOff = 5;
    		yOff = 1;
    	}
    	
        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

    public void move(){
        float abilityCounterDecrement = 1;
        if (hasStatusEffect(StatusEffect.Haste)) abilityCounterDecrement = 3;

        for (AbilitySlot slot: abilitySlots){
            float dec = abilityCounterDecrement;

            //mad skillz
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.None && hasSkill(Skills.FastHands)) dec *= 2;
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.Fire && hasSkill(Skills.Blazin)) dec *= 2;
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.Water && hasSkill(Skills.Streamin)) dec *= 2;
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.Air && hasSkill(Skills.Breezin)) dec *= 2;
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.Earth && hasSkill(Skills.Rooted)) dec *= 2;
            if (slot.ability != null && slot.ability.getPrimaryAffinity() == Affinity.Spirit && hasSkill(Skills.Astral)) dec *= 2;

            //cooldown - if it's there, cool it off
            if (slot.cooldownTimer > 0) slot.cooldownTimer -= dec;
    		if (slot.cooldownTimer < 0) slot.cooldownTimer = 0;

            //charge - if it's not being held, cool it off.
            boolean buttonDown = false;
            if (slot.index == 0 && GameComponent.keys[GameComponent.KEY_ABILITY_1]) buttonDown = true;
            else if (slot.index == 1 && GameComponent.keys[GameComponent.KEY_ABILITY_2]) buttonDown = true;
            else if (slot.index == 2 && GameComponent.keys[GameComponent.KEY_ABILITY_3]) buttonDown = true;
            if (slot.chargeTimer > 0 && !buttonDown) slot.chargeTimer -= dec;
        }

        if (behavior != null) behavior.move(this);

        //apply some auto abilities
        if (getAbility(3) == Ability.Regenerate && getHp() < getMaxHp() && getTick()%64 == 0){
            damage(new DamageAttributes(1, Arrays.asList(Attribute.Heal)));
        }

        super.move();
    }

    protected DamageAttributes modifyDamageAttributes(DamageAttributes attributes){
        DamageAttributes modified = super.modifyDamageAttributes(attributes);

        //reduce damage from immunity abilities
        if (isEquipped(Ability.Fireproof) && modified.getAttributes().contains(Attribute.Fire)){
            modified.setDamage(modified.getDamage()-(int)(modified.getDamage()*getScene().getGameState().getFireAffinity()*0.2));
        }
        else if (isEquipped(Ability.Windproof) && modified.getAttributes().contains(Attribute.Wind)){
            modified.setDamage(modified.getDamage()-(int)(modified.getDamage()*getScene().getGameState().getAirAffinity()*0.2));
        }
        else if (isEquipped(Ability.Earthproof) && modified.getAttributes().contains(Attribute.Earth)){
            modified.setDamage(modified.getDamage()-(int)(modified.getDamage()*getScene().getGameState().getEarthAffinity()*0.2));
        }
        else if (isEquipped(Ability.Waterproof) && modified.getAttributes().contains(Attribute.Water)){
            modified.setDamage(modified.getDamage() - (int) (modified.getDamage() * getScene().getGameState().getWaterAffinity() * 0.2));
        }
        else if (isEquipped(Ability.Spiritproof) && modified.getAttributes().contains(Attribute.Spirit)){
            modified.setDamage(modified.getDamage()-(int)(modified.getDamage()*getScene().getGameState().getSpiritAffinity()*0.2));
        }

        return modified;
    }

    public float getSpeed(){ return running ? super.getSpeed()*2 : super.getSpeed(); }

    public boolean isRunning(){ return running; }
    public void setRunning(boolean running){ this.running = running; }

    public boolean isCharging(){ return charging; }
    public void setCharging(boolean charging){ this.charging = charging; }

    public AbilitySlot[] getAbilitySlots(){ return abilitySlots; }
    public AbilitySlot[] getInitialAbilitySlots(){ return initialAbilitySlots; }
    public AbilitySlot[] getSpareAbilitySlots(){ return spareAbilitySlots; }
    public AbilitySlot getTrashAbilitySlot(){ return trashSlot; }

    public Ability getAbility(int index){ return abilitySlots[index].ability; }
    public void setAbility(int index, Ability ability){ abilitySlots[index].ability = ability; }

    public int getPortraitXPic(){ return portraitXPic; }
    public void setPortraitXPic(int portraitXPic){ this.portraitXPic = portraitXPic; }

    public int getPortraitYPic(){ return portraitYPic; }
    public void setPortraitYPic(int portraitYPic){ this.portraitYPic = portraitYPic; }

    public AbstractActorBehavior getBehavior(){ return behavior; }
    public void setBehavior(AbstractActorBehavior behavior){ this.behavior = behavior; }

    public void die(){
        super.die();

        if (isEquipped(Ability.Explode)){
            getScene().addSprite(new ExplodeEffect(getScene(), (int)getX(), (int)getY()));
        }
    }

    public boolean abilityEnabled(Ability ability){
        boolean ret = true;
        if (ability == null) return false;
        if (ability == Ability.None) ret = false;
        if (ability.getTriggerType() != Ability.TriggerType.Auto){
            if (hasStatusEffect(StatusEffect.Drowning)) ret = false;
            if (hasStatusEffect(StatusEffect.Petrified)) ret = false;
            if (hasStatusEffect(StatusEffect.Frozen)) ret = false;
            if (hasStatusEffect(StatusEffect.Electrocuted)) ret = false;
            if (hasStatusEffect(StatusEffect.Stunned)) ret = false;
            if (isChargingAhead()) ret = false;
        }
        if (isDead()) ret = false;
        return ret;
	}

    public boolean abilitySlotActive(AbilitySlot slot){
        boolean ret = true;
        if (slot.ability == null) return false;
        if (slot.ability == null) ret = false;
        if (getHp() <= slot.ability.getHpCost()) ret = false;
        if (slot.cooldownTimer > 0) ret = false;

        return ret;
    }

    public boolean isEquipped(Ability ability){
        for (AbilitySlot slot: abilitySlots)
            if (ability == slot.ability)
                return true;
        return false;
    }

    //fire off a tap ability; public for behaviors
    public void tap(AbilitySlot slot){
    	if (!(abilityEnabled(slot.ability) && abilitySlotActive(slot))) return;

        //set our cooldown and pay our HP cost, if there is one
        slot.cooldownTimer = slot.ability.getMaxCooldown();
        setHp(getHp()-slot.ability.getHpCost());

        //use the ability and add any new sprite it creates
        List<Sprite> sprites = slot.ability.takeEffect(getScene(), this, getProjectileHeading());
        for (Sprite sprite: sprites) getScene().addSprite(sprite);
    }

    protected double getProjectileHeading(){ return getScene().getPlayerMouseAngle(); }
    protected void hold(AbilitySlot slot){
        if (!(abilityEnabled(slot.ability) && abilitySlotActive(slot))){
            release(slot);
            return;
        }
        slot.chargeTimer += slot.ability.getChargeSpeed();
        if (slot.chargeTimer >= slot.ability.getMaxCharge()){
            //reset charge timer, set our cooldown and pay our HP cost, if there is one
            slot.chargeTimer = 0;
            slot.cooldownTimer = slot.ability.getMaxCooldown();
            release(slot);
            return;
        }

        setHp(getHp()-slot.ability.getHpCost());

        //use the ability and add any new sprite it creates
        List<Sprite> sprites = slot.ability.takeEffect(getScene(), this, getScene().getPlayerMouseAngle());
        for (Sprite sprite: sprites) getScene().addSprite(sprite);
    }

    protected void release(AbilitySlot slot){
        //release the ability and add any new sprite that creates
        List<Sprite> sprites = slot.ability.released(getScene(), this, getScene().getPlayerMouseAngle());
        for (Sprite sprite: sprites) getScene().addSprite(sprite);
    }

    protected void charge(AbilitySlot slot){
        if (!(abilityEnabled(slot.ability) && abilitySlotActive(slot))) return;
        slot.chargeTimer += slot.ability.getChargeSpeed();
        charging = true;
        if (slot.chargeTimer >= slot.ability.getMaxCharge()){
            //reset charge timer, set our cooldown and pay our HP cost, if there is one
            slot.chargeTimer = 0;
            slot.cooldownTimer = slot.ability.getMaxCooldown();
            setHp(getHp()-slot.ability.getHpCost());

            //use the ability and add any new sprite it creates
            List<Sprite> sprites = slot.ability.takeEffect(getScene(), this, getScene().getPlayerMouseAngle());
            if (sprites != null) for (Sprite sprite: sprites) getScene().addSprite(sprite);
        }
    }
}

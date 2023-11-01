package com.oblong.af.models;

import com.oblong.af.util.Art;

import java.awt.*;

public enum Attribute {
    //Elements
    Physical { public Image getEffectIcon(){ return Art.consoleIcons8x8[3][0]; } },
    Fire { public Image getEffectIcon(){ return Art.consoleIcons8x8[1][0]; } },
    Water { public Image getEffectIcon(){ return Art.consoleIcons8x8[1][1]; } },
    Earth { public Image getEffectIcon(){ return Art.consoleIcons8x8[1][2]; } },
    Wind { public Image getEffectIcon(){ return Art.consoleIcons8x8[1][3]; } },
    Spirit { public Image getEffectIcon(){ return Art.consoleIcons8x8[1][7]; } },
    Heal,
    Electric,

    //Status Effects
    Freeze,
    Stun,
    Poison,
    Slow,
    PoisonDamage, //the actual damage taken from poison
    ElectrocuteDamage, //the actual damage taken from electrocution
    Petrify,
    Drown,
    Haste,
    Speed,
    Osmose,
    Invisible,
    Invincible,
    FlamingAura,
    FreezingAura,
    ShockingAura,
    Stoneskin,
    Shield,

    //Instant Effects
    Knockback,
    Cleanse,
    Death,

    //Powerup Effects (also has Cleanse)
    MaxHpUp,
    OneUp,
    LearnAbility,
    GainAffinityPoint,
    TempInvisible,
    TempInvincible,
    ;
    public Image getEffectIcon(){ return null; }
}
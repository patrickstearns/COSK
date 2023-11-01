package com.oblong.af.models;

import java.awt.*;

public enum StatusEffect {

    Frozen(2, 1, 64, Color.CYAN, true, Attribute.Freeze, false),
    Electrocuted(2, 1, 16, Color.YELLOW, true, Attribute.Electric, false),
    Stunned(2, 1, 64, Color.ORANGE, true, Attribute.Stun, false),
    Petrified(2, 1, 32, null, true, Attribute.Petrify, false),
    Drowning(0, 0, Integer.MAX_VALUE, null, true, Attribute.Drown, true),
    Poisoned(2, 1, 128, Color.GREEN, true, Attribute.Poison, false),
    Slowed(0, 0, 64, Color.WHITE, true, Attribute.Slow, false),

    Invisible(0, 2, Integer.MAX_VALUE, null, false, Attribute.Invisible, false),
    Haste(0, 2, Integer.MAX_VALUE, Color.RED, false, Attribute.Haste, false),
    Speed(0, 2, Integer.MAX_VALUE, Color.ORANGE, false, Attribute.Speed, false),
    Osmose(0, 2, Integer.MAX_VALUE, Color.MAGENTA, false, Attribute.Osmose, false),
    Invincible(0, 0, 50, Color.YELLOW, false, Attribute.Invincible, true),
    FlameAura(0, 0, 2, Color.RED, false, Attribute.FlamingAura, true),
    FreezeAura(0, 0, 2, Color.CYAN, false, Attribute.FreezingAura, true),
    ShockAura(0, 0, 2, Color.YELLOW, false, Attribute.ShockingAura, true),
    Stoneskin(0, 0, 2, Color.GRAY, false, Attribute.Stoneskin, true),
    Shield(0, 0, 2, null, false, Attribute.Shield, true),
    ;

    public static StatusEffect forAttribute(Attribute attribute){
        for (StatusEffect effect: values())
            if (effect.getAttribute() == attribute)
                return effect;
        return null;
    }

    private boolean harmful;
    private int xPic, yPic, duration;
    private Attribute attribute;
    private Color color;
    private boolean suppressEffect;

    private StatusEffect(int xPic, int yPic, int duration, Color color, boolean harmful, Attribute attribute, boolean suppressEffect){
        this.xPic = xPic;
        this.yPic = yPic;
        this.duration = duration;
        this.color = color;
        this.harmful = harmful;
        this.attribute = attribute;
        this.suppressEffect = suppressEffect;
    }

    public boolean isHarmful(){ return harmful; }
    public int getXPic(){ return xPic; }
    public int getYPic(){ return yPic; }
    public int getDuration(){ return duration; }
    public Color getColor(){ return color; }
    public Attribute getAttribute(){ return attribute; }
    public boolean isSuppressEffect(){ return suppressEffect; }

}


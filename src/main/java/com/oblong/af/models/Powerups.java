package com.oblong.af.models;

import java.util.Arrays;
import java.util.List;

public enum Powerups {

	HpPotion(5, Arrays.asList(Attribute.Heal)),
    BigHpPotion(20, Arrays.asList(Attribute.Heal)),
    FullHpPotion(50, Arrays.asList(Attribute.Heal)),
    HpMaxUpPotion(100, Arrays.asList(Attribute.Heal, Attribute.MaxHpUp)),
    PoisonPotion(0, Arrays.asList(Attribute.Poison)),
    OneUp(0, Arrays.asList(Attribute.OneUp)),
    AbilityGem(0, Arrays.asList(Attribute.LearnAbility)),
    AffinityGem(0, Arrays.asList(Attribute.GainAffinityPoint));
    ;
    private int hpGain;
    private List<Attribute> attributes;

    private Powerups(int hpGain, List<Attribute> attributes){
        this.hpGain = hpGain;
        this.attributes = attributes;
    }

	public int getXPic(){ return 0; }
	public int getYPic(){ return ordinal(); }
    public int getHpGain(){ return hpGain; }
	public List<Attribute> getAttributes(){ return attributes; }

}

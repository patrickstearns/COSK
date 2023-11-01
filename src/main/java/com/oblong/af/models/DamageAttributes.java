package com.oblong.af.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DamageAttributes {

    private int damage;
    private List<Attribute> attributes;
    private Ability toLearn;

    public DamageAttributes(DamageAttributes copy){
        setDamage(copy.getDamage());
        setAttributes(new ArrayList<Attribute>(copy.getAttributes()));
        setToLearn(copy.toLearn);
    }

    public DamageAttributes(Ability toLearn){
        setDamage(0);
        setAttributes(Arrays.asList(Attribute.LearnAbility));
        setToLearn(toLearn);
    }

    public DamageAttributes(int damage, List<Attribute> atttributes){
        setDamage(damage);
        setAttributes(new ArrayList<Attribute>(atttributes));
        setToLearn(toLearn);
    }

    public int getDamage(){ return damage; }
    public void setDamage(int damage){ this.damage = damage; }

    public List<Attribute> getAttributes(){ return attributes; }
    public void setAttributes(List<Attribute> attributes){ this.attributes = new ArrayList<Attribute>(attributes); }

    public Ability getToLearn(){ return toLearn; }
    public void setToLearn(Ability toLearn){ this.toLearn = toLearn; }

}

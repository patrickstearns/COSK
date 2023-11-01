package com.oblong.af.models;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.decorator.OutlineDecorator;
import com.oblong.af.sprite.effects.FadingImageEffect;
import com.oblong.af.sprite.effects.Puff;
import com.oblong.af.sprite.effects.RisingTextEffect;
import com.oblong.af.sprite.effects.TargetReticle;
import com.oblong.af.sprite.enemy.Rat;
import com.oblong.af.sprite.projectile.*;
import com.oblong.af.sprite.thing.FireField;
import com.oblong.af.sprite.thing.PoisonCloud;
import com.oblong.af.sprite.thing.RockColumn;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {

	//Non-colored abilities
    None("None", "Nothing.", null, TriggerType.None, Affinity.None, 0, 0, 0, 0) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    ThrowingKnives("Throwing Knives", "Throw a sharp blade with pinpoint accuracy.",
            Art.abilities[0][5], TriggerType.Tap, Affinity.None, 0, 10, 10, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{ThrownWeapon.createThrowingKnife(scene, parent, heading)}));
        }
    },
    ThrowingAxes("Throwing Axes", "Throw an axe.",
            Art.abilities[0][4], TriggerType.Tap, Affinity.None, 0, 20, 20, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{ThrownWeapon.createThrowingAxe(scene, parent, heading)}));
        }
    },
    Bombs("Bombs", "Throw an explosive bomb that damages anything near where it hits.",
            Art.abilities[1][7], TriggerType.Tap, Affinity.None, 0, 50, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{ThrownWeapon.createBomb(scene, parent, heading)}));
        }
    },
    Staff("Staff", "Rapidly swing a staff in an arc.",
            Art.abilities[1][5], TriggerType.Tap, Affinity.None, 0, 20, 20, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{SwungWeapon.createStaff(scene, parent, heading)}));
        }
    },
    Sword("Sword", "Rapidly swing a sword in an arc.",
            Art.abilities[1][4], TriggerType.Tap, Affinity.None, 0, 15, 15, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{SwungWeapon.createSword(scene, parent, heading)}));
        }
    },
    Mace("Mace", "Swing a mace in an arc.",
            Art.abilities[0][6], TriggerType.Tap, Affinity.None, 0, 25, 25, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{SwungWeapon.createMace(scene, parent, heading)}));
        }
    },
    Axe("Axe", "Heft an axe in an arc.",
            Art.abilities[0][4], TriggerType.Tap, Affinity.None, 0, 30, 30, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{SwungWeapon.createAxe(scene, parent, heading)}));
        }
    },
    Pistol("Pistol", "Fire a bullet.",
            Art.abilities[0][7], TriggerType.Tap, Affinity.None, 0, 30, 30, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            scene.getSound().play(Art.getSample("bullet.wav"), parent, 1, 1, 1);
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{
                    Gun.createGun(scene, parent, heading),
                    new Bullet(scene, parent, heading, Color.RED, new DamageAttributes(2, Arrays.asList(Attribute.Physical)), 32)
            }));
        }
    },
    Osmose("Osmose", "Absorb incoming elemental damage.  The higher your elemental affinity, the more HP you can recover.",
            Art.abilities[3][3],
            TriggerType.Hold, Affinity.None, 0, 50, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            if (parent.getTick()%20 == 0) scene.getSound().play(Art.getSample("drone.wav"), parent, 1, 1, 1);
            parent.damage(new DamageAttributes(0, Arrays.asList(Attribute.Osmose)));
            return new ArrayList<Sprite>();
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            parent.removeStatusEffect(StatusEffect.Osmose);
            return new ArrayList<Sprite>();
        }
    },
    Shout("Shout", "Knock enemies back with a loud shout.",
            Art.abilities[4][6], TriggerType.Tap, Affinity.None, 0, 20, 10, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.setShoutCounter(9);
            parent.setXa(0);
            parent.setYa(0);
            parent.setMovementLockedCounter(9);
            return new ArrayList<Sprite>();
        }
    },
    Wail("Wail", "Knock enemies back and stun them.",
            Art.abilities[4][7], TriggerType.Charge, Affinity.None, 0, 20, 10, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.setWailCounter(9);
            parent.setXa(0);
            parent.setYa(0);
            parent.setMovementLockedCounter(9);
            return new ArrayList<Sprite>();
        }
    },

    //Fire abilities
    Flamethrower("Flamethrower", "Breathe fire.",
            Art.abilities[0][0], TriggerType.Hold, Affinity.Fire, 0, 200, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            FlameSpurt spurt = new FlameSpurt(parent.getScene(), parent, heading-Math.PI/8f+Math.random()*Math.PI/4f, 6);
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{spurt}));
        }
    },
    Firefoot("Firefoot", "Leave a trail of flame behind you as you move, but be careful you don't get burned.",
            Art.abilities[3][1], TriggerType.Hold, Affinity.Fire, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Fire, Affinity.Fire); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            if (parent.getTick()%2==0){
                scene.getSound().play(Art.getSample("lightwhiff.wav"), parent, 1, 1, 1);

                FireField ff = new FireField("firefield", scene, 64, parent, false, 8);
                ff.setX(parent.getX());
                ff.setY(parent.getY());
                return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{ff}));
            }
            else return new ArrayList<Sprite>();
        }
    },
    Firestorm("Firestorm", "Flame all enemies in range.  Watch them burn, and cackle with glee as they lament ever having opposed you!",
            Art.abilities[3][4],
            TriggerType.Charge, Affinity.Fire, 0, 300, 100, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Fire, Affinity.Fire, Affinity.Fire, Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            scene.getSound().play(Art.getSample("windLong.wav"), parent, 1, 1, 1);

            List<Sprite> flames = new ArrayList<Sprite>();
            for (int i = 0; i < 20; i++)
                flames.add(new TornadoFlame(scene, parent));
            return flames;
        }
    },
    RingOfFire("Ring of Fire", "I fell in to a burning ring of fire.  I went down, down, down, and the flames went higher, and it burns, burns, burns, " +
            "the ring of fire - the ring of fire.",
            Art.abilities[3][5],
            TriggerType.Charge, Affinity.Fire, 0, 100, 100, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> flames = new ArrayList<Sprite>();
            for (int d = 0; d < 360; d += 10){
                double i = Math.toRadians(d);
                FireField ff = new FireField("fire field", scene, 20, parent, false, 0);
                ff.setX(parent.getX());
                ff.setY(parent.getY());
                ff.setHeading(i);
                ff.setMoving(true);
                ff.setSpeed(6f);
                flames.add(ff);
            }

            scene.getSound().play(Art.getSample("fireburst.wav"), parent, 1, 1, 1);

            return flames;
        }
    },
    FlameShield("Flame Shield", "Flaming wisps surround you.",
            Art.abilities[4][2],
            TriggerType.Hold, Affinity.Fire, 0, 100, 400, 3) {
        private boolean held = false;
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Fire, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> sprites = new ArrayList<Sprite>();
            if (!held){
                sprites.add(new ShieldFlame(scene, parent, 0));
                sprites.add(new ShieldFlame(scene, parent, 1));
            }
            held = true;
            return sprites;
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            for (Sprite sprite: scene.getSprites())
                if (sprite instanceof ShieldFlame)
                    ((ShieldFlame) sprite).die();
            held = false;
            return new ArrayList<Sprite>();
        }
    },
    Fireproof("Fireproof", "Fire-based damage is reduced by 20% for every Fire affinity point the player has.",
            Art.abilities[5][0],
            TriggerType.Auto, Affinity.Fire, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Sparkler("Sparkler", "Release a shower of burning sparks that seek nearby targets.",
            Art.abilities[4][5], TriggerType.Hold, Affinity.Fire, 0, 200, 100, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Fire, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return Arrays.asList(new Sprite[]{
                    new Sparkler(scene, parent, null, Math.random()*Math.PI*2),
                    new Sparkler(scene, parent, null, Math.random()*Math.PI*2)
            });
        }
    },
    Explode("Explode", "Explode upon death, doing a LOT of damage.",
            Art.abilities[5][5],
            TriggerType.Auto, Affinity.Fire, 0, 0, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Fire, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    FlamingAura("Flaming Aura", "Burn anything that comes into contact with you.",
            Art.abilities[6][2], TriggerType.Hold, Affinity.Fire, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Fire, Affinity.Fire); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.FlamingAura))));
            return new ArrayList<Sprite>();
        }
    },
    FierySpin("Fiery Spin", "Spin forward in a whirl of flame.",
            Art.abilities[7][2],
            TriggerType.Tap, Affinity.Fire, 0, 30, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            int ticks = 6;
            parent.addDecorator(new OutlineDecorator(Color.RED, ticks));
            double x = Math.cos(heading)*4;
            double y = -Math.sin(heading)*4;
            parent.addMovementVector(new Point2D.Double(x, y), ticks);
            parent.setFlameSpinCounter(ticks);
            return new ArrayList<Sprite>();
        }
    },

    //Air abilities
    Tornado("Tornado", "Create a powerful cyclone of air around yourself.",
            Art.abilities[2][3], TriggerType.Hold, Affinity.Air, 0, 100, 100, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new TornadoGust(scene, parent)));
        }
    },
    Gust("Gust", "Blow.  Like, hard.",
            Art.abilities[2][4], TriggerType.Hold, Affinity.Air, 0, 200, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> gusts = new ArrayList<Sprite>();
            for (int i = 0; i < 3; i++){
                double rheading = heading-0.5f+Math.random()*1f;
                gusts.add(new Gust(scene, parent, rheading));
            }

            //move player slightly backward
            double h = parent.getHeading();
            float px = -(float)(Math.cos(h)*1f);
            float py = (float)(Math.sin(h)*1f);
            Point2D.Float v = new Point2D.Float(px, py);
            parent.addMovementVector(v, 1);

            return gusts;
        }
    },
    Sand("Sand", "Blow sand in a forward cone.",
            Art.abilities[4][4], TriggerType.Hold, Affinity.Air, 0, 200, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> gusts = new ArrayList<Sprite>();
            for (int i = 0; i < 3; i++){
                double rheading = heading-0.5f+Math.random()*1f;
                gusts.add(new Sand(scene, parent, rheading));
            }

            //move player slightly backward
            double h = parent.getHeading();
            float px = -(float)(Math.cos(h)*1f);
            float py = (float)(Math.sin(h)*1f);
            Point2D.Float v = new Point2D.Float(px, py);
            parent.addMovementVector(v, 1);

            return gusts;
        }
    },
    Speed("Speed", "Move faster.",
            Art.abilities[3][2],
            TriggerType.Hold, Affinity.Air, 0, 200, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, Arrays.asList(Attribute.Speed)));
            return new ArrayList<Sprite>();
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            scene.getSound().play(Art.getSample("badStatusEffect.wav"), parent, 1, 1, 1);
            parent.removeStatusEffect(StatusEffect.Speed);
            return new ArrayList<Sprite>();
        }
    },
    Haste("Haste", "Shorten ability recharge time.",
            Art.abilities[1][2],
            TriggerType.Hold, Affinity.Air, 0, 200, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, Arrays.asList(Attribute.Haste)));
            return new ArrayList<Sprite>();
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            scene.getSound().play(Art.getSample("badStatusEffect.wav"), parent, 1, 1, 1);
            parent.removeStatusEffect(StatusEffect.Haste);
            return new ArrayList<Sprite>();
        }
    },
    Windproof("Windproof", "Wind-based damage is reduced by 20% for every Wind affinity point the player has.",
            Art.abilities[5][1],
            TriggerType.Auto, Affinity.Air, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Blink("Blink", "When struck by purely physical damage, you'll be teleported away instead of being hurt...probably.",
            Art.abilities[5][6],
            TriggerType.Auto, Affinity.Air, 0, 0, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Air, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    StormsEye("Storm's Eye", "Wrap yourself in a thunderstorm of whirling wind and lightning.",
            Art.abilities[6][0], TriggerType.Hold, Affinity.Air, 0, 300, 100, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Air, Affinity.Air, Affinity.Air, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> ret = new ArrayList<Sprite>();
            ret.add(new TornadoGust(scene, parent));

            int sx = (int)(parent.getX()-150+300*Math.random());
            int sy = (int)(parent.getY()-150+300*Math.random());
            ret.add(new LightningStrike(scene, parent, sx, sy));

            return ret;
        }
    },
    ShockingAura("Shocking Aura", "Shock anything that comes into contact with you.",
            Art.abilities[6][4], TriggerType.Hold, Affinity.Air, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Air, Affinity.Air, Affinity.Earth); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            if (parent.getTick()%26 == 0) scene.getSound().play(Art.getSample("electricHum.wav"), parent, 1, 1, 1);
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.ShockingAura))));
            return new ArrayList<Sprite>();
        }
    },
    Twirl("Twirl", "Spin forward, smacking anyone out of your way.",
            Art.abilities[6][7],
            TriggerType.Tap, Affinity.Air, 0, 20, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            int ticks = 6;
            parent.addDecorator(new OutlineDecorator(Color.YELLOW, ticks));
            double x = Math.cos(heading)*4;
            double y = -Math.sin(heading)*4;
            parent.addMovementVector(new Point2D.Double(x, y), ticks);
            parent.setSpinCounter(ticks);
            return new ArrayList<Sprite>();
        }
    },
    WindKick("Wind Kick", "Kick out and knock yourself backward.",
            Art.abilities[7][3],
            TriggerType.Tap, Affinity.Air, 0, 20, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Air, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            for (int i = 0; i < 5; i++){
                double x = Math.cos(heading)*4;
                double y = -Math.sin(heading)*4;
                parent.addMovementVector(new Point2D.Double(-x, -y), i);
            }

            List<Sprite> ret = new ArrayList<Sprite>();
            for (int i = 0; i < 20; i++){
                double rheading = heading-0.5f+Math.random()*1f;
                Gust g = new Gust(scene, parent, rheading);
                g.setTimeToLive(4);
                ret.add(g);
            }
            return ret;
        }
    },
    Vanish("Vanish", "Disappear from enemy view, but can't use any other abilities.",
            Art.abilities[7][6], TriggerType.Hold, Affinity.Air, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Air, Affinity.Air); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.Invisible))));
            return new ArrayList<Sprite>();
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            parent.removeStatusEffect(StatusEffect.Invisible);
            scene.getSound().play(Art.getSample("badStatusEffect.wav"), parent, 1, 1, 1);
            return new ArrayList<Sprite>();
        }
    },

    //Water abilities
    FreezingBolt("Freezing Bolt", "Fire a freezing bolt of ice.",
            Art.abilities[0][1], TriggerType.Tap, Affinity.Water, 0, 50, 0, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire, Affinity.Water)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{TailedBolt.createFreezingBolt(scene, parent, heading)}));
        }
    },
    PoisonSting("Poison Sting", "Sting an enemy with deadly poison.",
            Art.abilities[2][1], TriggerType.Tap, Affinity.Water, 0, 20, 0, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Water)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{new PoisonSting(scene, parent, heading)}));
        }
    },
    PoisonBreath("Poison Breath", "Breathe poisonous gas.",
            Art.abilities[2][5], TriggerType.Hold, Affinity.Water, 0, 200, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Water, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            PoisonCloud spurt = new PoisonCloud(parent.getScene(), 48, parent, true);
            spurt.setHeading(heading-Math.PI/8f+Math.random()*Math.PI/4f);
            spurt.setSpeed(5f);
            spurt.setMoving(true);
            spurt.setX(parent.getX()+(float)(Math.cos(spurt.getHeading())*16));
            spurt.setY(parent.getY()-(float)(Math.sin(spurt.getHeading())*16));
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{spurt}));
        }
    },
    Squirt("Squirt", "Fire a powerful jet of water in a straight line.",
            Art.abilities[3][0], TriggerType.Tap, Affinity.Water, 0, 30, 0, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Water); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{
                    new Squirt((int)parent.getX(), (int)parent.getY()-1+((int)(Math.random()*2))-12, heading, parent)}));
        }
    },
    Flush("Flush", "Blast enemies away with a gushing wall of water.",
            Art.abilities[3][7],
            TriggerType.Charge, Affinity.Water, 0, 300, 100, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Water, Affinity.Water, Affinity.Water, Affinity.Water)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new FlushWave((int)parent.getX(), (int)parent.getY(), parent)));
        }
    },
    Waterproof("Waterproof", "Water-based damage is reduced by 20% for every Water affinity point the player has.",
            Art.abilities[5][3],
            TriggerType.Auto, Affinity.Water, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    FreezingAura("Freezing Aura", "Freeze anything that comes into contact with you.",
            Art.abilities[6][3], TriggerType.Hold, Affinity.Water, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Fire, Affinity.Water); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.FreezingAura))));
            return new ArrayList<Sprite>();
        }
    },
    Freeze("Freeze", "Freeze everything around you.",
            Art.abilities[6][5], TriggerType.Charge, Affinity.Water, 0, 300, 100, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            //freeze any props nearby
            for (Prop prop: scene.getDamageablePropsWithinRange((int)parent.getX(), (int)parent.getY(), 64)){
                if (prop == parent) continue;
                prop.damage(new DamageAttributes(5, new ArrayList<Attribute>(Arrays.asList(Attribute.Freeze))));
            }

            //freeze any water nearby
            int px = (int)(parent.getX()/16), py = (int)(parent.getY()/16);
            for (int i = px-5; i <= px+5; i++)
                for (int j = py-5; j <= py+5; j++)
                    if (Point2D.distance(px, py, i, j) < 4)
                        freezeBlock(scene, scene.areaGroup.getCurrentArea().getBlock(i, j, Area.Layer.Main));

            //add a bunch of ice effects
            List<Sprite> ret = new ArrayList<Sprite>();
            for (int i = 0; i < 16; i++){
                double r = Math.random()*64;
                double h = Math.random()*Math.PI*2;
                int x = (int)(parent.getX()+(float)(Math.cos(h)*r));
                int y = (int)(parent.getY()-(float)(Math.sin(h)*r));
                ret.add(new FadingImageEffect(scene, x, y, false, 3, 0));
            }

            scene.getSound().play(Art.getSample("freeze.wav"), parent, 1, 1, 1);

            return ret;
        }

        private void freezeBlock(AreaScene scene, Block b){
            if (scene.areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Water)){
                b.dFreeze = 1;
                b.electrifiedCounter = 0;
            }
        }
    },
    Drown("Drown", "Envelop nearby enemies in a crushing ball of water.",
            Art.abilities[7][7], TriggerType.Hold, Affinity.Water, 0, 200, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Water, Affinity.Water, Affinity.Water); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> ret = new ArrayList<Sprite>();
            List<Prop> inrange = scene.getDamageablePropsWithinRange((int)parent.getX(), (int) parent.getY(), 64);
            for (Prop prop: inrange){
                if (prop == parent) continue;
                if (prop.isImmuneToStatusEffects()) continue;

                prop.setMovementLockedCounter(2);
                prop.setHeadingLockedCounter(2);
                prop.setXa(0);
                prop.setYa(0);

                if (!prop.hasStatusEffect(StatusEffect.Drowning))
                    prop.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.Drown, Attribute.Water))));
            }

            parent.setXa(0);
            parent.setYa(0);
            parent.setMovementLockedCounter(2);
            parent.setHeadingLockedCounter(2);

            return ret;
        }
        public List<Sprite> released(AreaScene scene, Prop parent, double heading){
            List<Prop> inrange = scene.getDamageablePropsWithinRange((int)parent.getX(), (int) parent.getY(), 64);
            for (Prop prop: inrange)
                if (prop.hasStatusEffect(StatusEffect.Drowning))
                    prop.removeStatusEffect(StatusEffect.Drowning);
            return new ArrayList<Sprite>();
        }
    },

    //Earth abilities
    Petrify("Petrify", "Fire a petrifying bolt, turning any living thing it hits to stone.",
            Art.abilities[2][0], TriggerType.Tap, Affinity.Earth, 0, 100, 0, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Earth, Affinity.Earth, Affinity.Spirit); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{TailedBolt.createPetrifyingBolt(scene, parent, heading)}));
        }
    },
    SummonRat("Summon Rat", "I came as gold, I came as crap\n" +
            "I came clean and I came as a Rat\n" +
            "It takes a long time, but God dies too\n" +
            "But not before he'll stick it to you.",
            Art.abilities[3][6],
            TriggerType.Charge, Affinity.Earth, 0, 100, 50, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            Rat rat = new Rat("rat", scene, 0, 0);
            rat.setX(parent.getX() + (float) (Math.cos(heading) * 16));
            rat.setY(parent.getY() - (float) (Math.sin(heading) * 16));
            rat.setHeading(heading);
            scene.getSound().play(Art.getSample("toss.wav"), rat, 1, 1, 1);
            return new ArrayList<Sprite>(Arrays.asList(rat));
        }
    },
    EarthRend("Earth Rend", "Tear the earth in a line, damaging any enemies struck by the rupture.",
            Art.abilities[4][0],
            TriggerType.Tap, Affinity.Earth, 0, 10, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(TornEarth.createEarthRend(scene, parent, heading)));
        }
    },
    FingersOfTheEarth("Fingers of the Earth", "Rock columns erupt from the ground around you to shield you from harm.",
            Art.abilities[4][1],
            TriggerType.Tap, Affinity.Earth, 0, 50, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Earth, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> sprites = new ArrayList<Sprite>();
            for (int i = 0; i < 5; i++){
                double h = Math.toRadians(i*72);
                double x = parent.getX()+(float)(Math.cos(h)*32);
                double y = parent.getY()-(float)(Math.sin(h)*32);
                sprites.add(new RockColumn(scene, (int)x, (int)y));
            }
            return sprites;
        }
    },
    Quake("Quake", "The fury of the earth erupts around you.",
            Art.abilities[10][0],
            TriggerType.Hold, Affinity.Earth, 0, 100, 100, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Earth, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            List<Sprite> sprites = new ArrayList<Sprite>();

            if (parent.getTick()%4 == 0){
                int rx = (int)(Math.random()*128)-64;
                int ry = (int)(Math.random()*128)-64;
                rx += Math.signum(rx)*32;
                ry += Math.signum(ry)*32;
                sprites.add(new Boulder(scene, (int)(parent.getX()+rx), (int)(parent.getY()+ry)));
            }
            if (parent.getTick()%2 == 0)
                sprites.add(TornEarth.createEarthRend(scene, parent, Math.random()*2*Math.PI));
            if (parent.getTick()%4 == 0){
                int radius = 32+(int)(Math.random()*8*16);
                for (int i = 0; i < 5; i++){
                    double h = Math.toRadians(i*72);
                    double x = parent.getX()+(float)(Math.cos(h)*radius);
                    double y = parent.getY()-(float)(Math.sin(h)*radius);
                    sprites.add(new RockColumn(scene, (int)x, (int)y));
                }
            }

            return sprites;
        }
    },
    Earthproof("Earthproof", "Earth-based damage is reduced by 20% for every Earth affinity point the player has.",
            Art.abilities[5][2],
            TriggerType.Auto, Affinity.Earth, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    ShockFingers("Shocking Fingers", "Lightning dances from your fingertips.",
            Art.abilities[6][1],
            TriggerType.Tap, Affinity.Earth, 0, 100, 100, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Air)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            scene.getSound().play(Art.getSample("electricShock.wav"), parent, 1, 1, 1);
            return new ArrayList<Sprite>(Arrays.asList(Lightning.createLightning(scene, parent, heading)));
        }
    },
    Tunnel("Tunnel", "Tunnel from one patch of dirt or grass to another.",
            Art.abilities[6][6], TriggerType.Charge, Affinity.Earth, 0, 100, 30, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            Point dest = scene.tileUnderPointOnScreen(scene.getMousePosition());
            if (scene.isTunneable(dest, parent) && scene.isTunneable(new Point((int)(parent.getX()/16f), (int)(parent.getY()/16f)), parent)){
                parent.tunnelTo(new Point(dest.x*16+8, dest.y*16+15));
                return new ArrayList<Sprite>(Arrays.asList(new TargetReticle(scene, dest.x*16+8, dest.y*16+16, 32)));
            }
            else return new ArrayList<Sprite>();
        }
    },
    Stonefist("Stonefist", "Smack down your enemies with heavy fists of stone.",
            Art.abilities[7][1],
            TriggerType.Tap, Affinity.Earth, 0, 30, 0, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            double x = Math.cos(heading)*12;
            double y = -Math.sin(heading)*12;
            parent.addMovementVector(new Point2D.Double(x, y), 6);
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{new StoneFist(scene, parent, heading)}));
        }
    },
    Stoneskin("Stoneskin", "Greatly reduces damage taken as well as movement speed.",
            Art.abilities[6][4], TriggerType.Hold, Affinity.Earth, 0, 100, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Air, Affinity.Earth, Affinity.Earth); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.Stoneskin))));
            return new ArrayList<Sprite>();
        }
    },
    LightningCoil("Lightning Coil", "Shock anything that comes near.",
            Art.abilities[8][0], TriggerType.Hold, Affinity.Earth, 0, 100, 200, 1) {
        private int chargeCounter = 3;
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Earth, Affinity.Earth, Affinity.Air, Affinity.Air); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            chargeCounter++;

            List<Sprite> ret = new ArrayList<Sprite>();
            if (chargeCounter == 4){
                for (Prop prop: scene.getDamageablePropsWithinRange((int)parent.getX(), (int) parent.getY(), 64)){
                    if (prop == parent) continue;

                    if (Math.random() < 0.125){
                        double xDiff = prop.getX()-parent.getX();
                        double yDiff = prop.getY()-parent.getY();
                        double atan = Math.atan2(xDiff, yDiff);
                        if(atan < 0) atan += Math.PI*2;
                        atan -= Math.PI/2f;
                        ret.add(Lightning.createLightning(scene, parent, atan));
                        chargeCounter = 0;
                        scene.getSound().play(Art.getSample("electricShock.wav"), parent, 1, 1, 1);
                    }
                }
            }

            if (chargeCounter == 4) chargeCounter = 0;

            parent.setXa(0);
            parent.setYa(0);
            parent.setMovementLockedCounter(2);
            parent.setHeadingLockedCounter(2);

            return ret;
        }
    },
    Charge("Charge", "Charge ahead, doing earth damage to anything in your path.",
            Art.abilities[8][1], TriggerType.Charge, Affinity.Earth, 0, 10, 20, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Earth, Affinity.Air); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.setChargingAhead(true);
            parent.setHeading(heading);
            parent.setMoving(true);

            List<Sprite> ret = new ArrayList<Sprite>();
            for (int i = 0; i < 20; i++){
                int degree = (int)(Math.random()*360);
                int distance = (int)(Math.random()*16);
                int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
                int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
                ret.add(new Puff(scene, (int)(parent.getX()+parent.getWidth()/2+xOffset), (int)(parent.getY()-parent.getHeight()/2+yOffset-8)));
            }

            return ret;
        }
    },

    //Spirit abilities
    DeathBolt("Death Bolt", "Fire a killing bolt of death energy.",
            Art.abilities[1][0], TriggerType.Tap, Affinity.Spirit, 0, 75, 75, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Fire, Affinity.Water, Affinity.Air, Affinity.Earth)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{TailedBolt.createDeathBolt(scene, parent, heading)}));
        }
    },
    HomingBolt("Homing Bolt", "Fire a bolt that homes in on nearby enemies.",
            Art.abilities[2][6], TriggerType.Tap, Affinity.Spirit, 0, 50, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            Prop target = null;
            HomingBolt bolt = new HomingBolt(scene, parent, target, heading);

            for (Sprite sprite: scene.getSprites())
                if (sprite instanceof Prop && sprite != parent){
                    Prop prop = (Prop)sprite;
                    if (prop.isCollidableWith(bolt) && SpriteDefinitions.isEnemy(prop.getId())){
                        if (target == null) target = prop;
                        else {
                            double d = Point2D.distance(parent.getX(), parent.getY(), prop.getX(), prop.getY());
                            double t = Point2D.distance(parent.getX(), parent.getY(), target.getX(), target.getY());
                            if (d < t) target = prop;
                        }
                    }
                }

            bolt.setTarget(target);

            return new ArrayList<Sprite>(Arrays.asList(new Sprite[]{bolt}));
        }
    },
    Regenerate("Regenerate", "Slowly regain health over time.",
            Art.abilities[2][7],
            TriggerType.Auto, Affinity.Spirit, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Cleanse("Cleanse", "Remove all negative status effects.",
            Art.abilities[0][3],
            TriggerType.Charge, Affinity.Spirit, 0, 100, 50, 3) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            parent.damage(new DamageAttributes(0, Arrays.asList(Attribute.Cleanse)));
            return null;
        }
    },
    CureWounds("Cure", "Regain HP.  The higher your Spirit affinity, the more you can heal.",
            Art.abilities[4][3],
            TriggerType.Charge, Affinity.Spirit, 0, 500, 50, 1) {
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
			ArrayList<Sprite> ret = new ArrayList<Sprite>();
            int hpGained = (int)(scene.getGameState().getSpiritAffinity()*2+scene.getGameState().getSpiritAffinity()*Math.random());
            parent.damage(new DamageAttributes(hpGained, Arrays.asList(Attribute.Heal)));
            ret.add(new RisingTextEffect(scene, (int)parent.getX(), (int)parent.getY()-32, "+"+hpGained+" HP", TextImageCreator.COLOR_GREEN));
			return ret;
		}
    },
    Spiritproof("Spiritproof", "Spirit-based damage is reduced by 20% for every Spirit affinity point the player has.",
            Art.abilities[5][4],
            TriggerType.Auto, Affinity.Spirit, 0, 1, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Fire)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Lifespark("Lifespark", "Come back from death once.",
            Art.abilities[5][7],
            TriggerType.Auto, Affinity.Spirit, 0, 500, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Orbeholder("Orbeholder", "Summon a pair of orbiting mini-Beholders to fire lasers at your enemies.",
            Art.abilities[9][7],
            TriggerType.Auto, Affinity.Spirit, 0, 200, 1, 0){
        public List<Affinity> getAffinityRequirements(){ return new ArrayList<Affinity>(Arrays.asList(Affinity.Spirit, Affinity.Spirit, Affinity.Spirit)); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    },
    Aegis("Aegis", "You are totally protected from damage, but can't do anything else.",
            Art.abilities[7][5], TriggerType.Hold, Affinity.Spirit, 0, 200, 200, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Spirit, Affinity.Spirit, Affinity.Earth); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            if (parent.getTick()%26 == 0) scene.getSound().play(Art.getSample("waawaa.wav"), parent, 1, 1, 1);
            parent.damage(new DamageAttributes(0, new ArrayList<Attribute>(Arrays.asList(Attribute.Shield))));
            return new ArrayList<Sprite>();
        }
    },
    Laser("Laser", "Fire a beam of pure spirit energy.",
            Art.abilities[8][2], TriggerType.Charge, Affinity.Spirit, 0, 50, 10, 1) {
        public List<Affinity> getAffinityRequirements(){ return Arrays.asList(Affinity.Spirit, Affinity.Spirit); }
        public List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading){
            return new ArrayList<Sprite>(Arrays.asList(new Laser((int)parent.getX(), (int)parent.getY()-1+((int)(Math.random()*2)), heading-0.02+0.04f*Math.random(), parent)));
        }
    },
    ;
    public static enum TriggerType {
        None(Art.consoleIcons32x32[5][0]),
        Tap(Art.consoleIcons32x32[0][0]),
        Hold(Art.consoleIcons32x32[2][0]),
        Charge(Art.consoleIcons32x32[1][0]),
        Auto(Art.consoleIcons32x32[3][0]),
        ;
        private Image socketImage;

        private TriggerType(Image socketImage){
            this.socketImage = socketImage;
        }

        public Image getSocketImage(){ return socketImage; }
    }

    private String name, description;
    private Image bigIcon, cursorIcon;
    private TriggerType triggerType;
    private Affinity primaryAffinity;
    private int hpCost, maxCooldown, maxCharge, chargeSpeed;

    private Ability(String name, String description, Image bigIcon, TriggerType triggerType,
                    Affinity primaryAffinity, int hpCost, int maxCooldown, int maxCharge, int chargeSpeed){
        this.name = name;
        this.description = description;
        this.bigIcon = bigIcon;
        this.triggerType = triggerType;
        this.primaryAffinity = primaryAffinity;
        this.hpCost = hpCost;
        this.maxCooldown = maxCooldown;
        this.maxCharge = maxCharge;
        this.chargeSpeed = chargeSpeed;
    }

    public abstract List<Sprite> takeEffect(AreaScene scene, Prop parent, double heading);
    public List<Sprite> released(AreaScene scene, Prop parent, double heading){ return new ArrayList<Sprite>(); }
    public abstract List<Affinity> getAffinityRequirements();

    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public Image getBigIcon(){ return bigIcon; }
    public TriggerType getTriggerType(){ return triggerType; }
    public Affinity getPrimaryAffinity(){ return primaryAffinity; }
    public int getHpCost(){ return hpCost; }
    public int getMaxCooldown(){ return maxCooldown; }
    public int getMaxCharge(){ return maxCharge; }
    public int getChargeSpeed(){ return chargeSpeed; }

    public Image getCursorIcon(int tickCounter){
        if (bigIcon != null){
            cursorIcon = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = cursorIcon.getGraphics();
            Image si = getTriggerType().getSocketImage(), bi = getBigIcon();
            si = ImageUtils.tintImage(si, primaryAffinity.getColor(), null);
            g.drawImage(Art.consoleIcons16x16[2][1], 0, 0, null);
            g.drawImage(si, 0, 0, 32, 32, 0, 0, si.getWidth(null), si.getHeight(null), null);
            g.drawImage(bi, 8, 8, 24, 24, 0, 0, bi.getWidth(null), bi.getHeight(null), null);
            cursorIcon = ImageUtils.outlineImage(cursorIcon, ImageUtils.calculateCycleColor(tickCounter, new Color[]{Color.ORANGE, ImageUtils.HALF_ORANGE }, 20));
            cursorIcon = ImageUtils.outlineImage(cursorIcon, ImageUtils.calculateCycleColor(tickCounter, new Color[]{Color.YELLOW, ImageUtils.HALF_YELLOW }, 20));
        }
        return cursorIcon;
    }

    public boolean isFound(GameState gameState){ return "true".equals(gameState.getVariable(name()+"_found")); }
    public void setFound(GameState gameState){ gameState.setVariable(name()+"_found", "true");}
}

package com.oblong.af.models;

import com.oblong.af.editor.Toolbox;
import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.SpriteTemplate;
import com.oblong.af.sprite.*;
import com.oblong.af.sprite.enemy.GigapedeSegment;
import com.oblong.af.sprite.npcs.RescuableNPC;
import com.oblong.af.sprite.thing.*;
import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public enum SpriteDefinitions {

	//Player Characters
	Brent(Art.characters, 0, 27, null) {
        public int getPortraitXPic(){ return 2; }
        public int getPortraitYPic(){ return 0; }
        public int getSpeed(){ return 8; }
        public int getMaxHp(){ return 30; }
        public Ability getInitialAbility1(){ return Ability.Axe; }
        public Ability getInitialAbility2(){ return Ability.None; }
        public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 2; }
        public int getInitialWaterAffinity(){ return 1; }
        public int getInitialAirAffinity(){ return 0; }
        public int getInitialFireAffinity(){ return 0; }
        public int getInitialSpiritAffinity(){ return 0; }
        public Skills[] getSkills(){ return new Skills[]{Skills.HardDrinker, Skills.Rooted}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
	},
	Sean(Art.characters, 12, 27, null) {
		public int getPortraitXPic(){ return 1; }
		public int getPortraitYPic(){ return 0; }
		public int getSpeed(){ return 8; }
		public int getMaxHp(){ return 20; }
        public Ability getInitialAbility1(){ return Ability.ThrowingKnives; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 0; }
        public int getInitialWaterAffinity(){ return 2; }
        public int getInitialAirAffinity(){ return 0; }
        public int getInitialFireAffinity(){ return 0; }
        public int getInitialSpiritAffinity(){ return 1; }
        public Skills[] getSkills(){ return new Skills[]{Skills.Sprinter}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Allen(Art.characters, 18, 27, null) {
		public int getPortraitXPic(){ return 0; }
		public int getPortraitYPic(){ return 1; }
		public int getSpeed(){ return 8; }
		public int getMaxHp(){ return 25; }
        public Ability getInitialAbility1(){ return Ability.Bombs; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 1; }
        public int getInitialWaterAffinity(){ return 0; }
        public int getInitialAirAffinity(){ return 0; }
        public int getInitialFireAffinity(){ return 2; }
        public int getInitialSpiritAffinity(){ return 0; }
        public int getInitialSpareAffinity(){ return 2; }
        public Skills[] getSkills(){ return new Skills[]{Skills.FastHands, Skills.Potential}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Dragon(Art.characters, 0, 30, null) {
		public int getPortraitXPic(){ return 1; }
		public int getPortraitYPic(){ return 1; }
		public int getSpeed(){ return 6; }
		public int getMaxHp(){ return 20; }
        public Ability getInitialAbility1(){ return Ability.Flamethrower; }
		public Ability getInitialAbility2(){ return Ability.FreezingBolt; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 1; }
        public int getInitialWaterAffinity(){ return 1; }
        public int getInitialAirAffinity(){ return 0; }
        public int getInitialFireAffinity(){ return 1; }
        public int getInitialSpiritAffinity(){ return 0; }
        public Skills[] getSkills(){ return new Skills[]{Skills.Blazin, Skills.Streamin}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Matt(Art.characters, 6, 27, null) {
		public int getPortraitXPic(){ return 2; }
		public int getPortraitYPic(){ return 1; }
		public int getSpeed(){ return 6; }
		public int getMaxHp(){ return 30; }
        public Ability getInitialAbility1(){ return Ability.Staff; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 0; }
        public int getInitialWaterAffinity(){ return 0; }
        public int getInitialAirAffinity(){ return 0; }
        public int getInitialFireAffinity(){ return 0; }
        public int getInitialSpiritAffinity(){ return 3; }
        public int getInitialSpareAffinity(){ return 2; }
        public Skills[] getSkills(){ return new Skills[]{Skills.Astral, Skills.Potential}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Marshall(Art.characters, 12, 30, null) {
		public int getPortraitXPic(){ return 3; }
		public int getPortraitYPic(){ return 0; }
		public int getSpeed(){ return 8; }
		public int getMaxHp(){ return 20; }
        public Ability getInitialAbility1(){ return Ability.Pistol; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 0; }
        public int getInitialWaterAffinity(){ return 0; }
        public int getInitialAirAffinity(){ return 1; }
        public int getInitialFireAffinity(){ return 1; }
        public int getInitialSpiritAffinity(){ return 1; }
        public Skills[] getSkills(){ return new Skills[]{Skills.SpeedyHealing, Skills.Breezin}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Adam(Art.characters, 6, 30, null) {
		public int getPortraitXPic(){ return 3; }
		public int getPortraitYPic(){ return 1; }
		public int getSpeed(){ return 8; }
		public int getMaxHp(){ return 25; }
        public Ability getInitialAbility1(){ return Ability.ThrowingAxes; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 0; }
        public int getInitialWaterAffinity(){ return 1; }
        public int getInitialAirAffinity(){ return 1; }
        public int getInitialFireAffinity(){ return 1; }
        public int getInitialSpiritAffinity(){ return 0; }
        public int getInitialSpareAffinity(){ return 2; }
        public Skills[] getSkills(){ return new Skills[]{Skills.SpeedyHealing, Skills.Potential}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },
	Patrick(Art.characters, 18, 30, null) {
		public int getPortraitXPic(){ return 0; }
		public int getPortraitYPic(){ return 0; }
		public int getSpeed(){ return 8; }
		public int getMaxHp(){ return 30; }
        public Ability getInitialAbility1(){ return Ability.Sword; }
		public Ability getInitialAbility2(){ return Ability.None; }
		public Ability getInitialAbility3(){ return Ability.None; }
        public Ability getInitialAutoAbility(){ return Ability.None; }
        public int getInitialEarthAffinity(){ return 0; }
        public int getInitialWaterAffinity(){ return 0; }
        public int getInitialAirAffinity(){ return 1; }
        public int getInitialFireAffinity(){ return 0; }
        public int getInitialSpiritAffinity(){ return 2; }
        public Skills[] getSkills(){ return new Skills[]{Skills.QuickStudy}; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Player player = new Player(name(), scene, getXPic(), getYPic());
            config(player, template, x, y, facing);
            return player;
        }
    },

    //Actor Characters
    PurpleBeholder(Art.beholder, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getSpeed(){ return 3; }
        public int getMaxHp(){ return 20; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Beholder enemy = new com.oblong.af.sprite.enemy.Beholder(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    GreenBeholder(Art.beholder, 0, 2, Toolbox.ToolGroup.Enemies){
        public int getSpeed(){ return 3; }
        public int getMaxHp(){ return 30; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Beholder enemy = new com.oblong.af.sprite.enemy.Beholder(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Icebat(Art.bat, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getSpeed(){ return 3; }
        public int getMaxHp(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Bat enemy = new com.oblong.af.sprite.enemy.Bat(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Firebat(Art.bat, 0, 4, Toolbox.ToolGroup.Enemies){
        public int getSpeed(){ return 3; }
        public int getMaxHp(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Bat enemy = new com.oblong.af.sprite.enemy.Bat(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Beetle(Art.beetle, 0, 0, Toolbox.ToolGroup.Enemies){
		public int getSpeed(){ return 3; }
		public int getMaxHp(){ return 1; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Beetle enemy = new com.oblong.af.sprite.enemy.Beetle(name(), scene, getXPic(), getYPic(), false, false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
	BeetleGenerator(Art.beetleGenerator, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 50; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.BeetleGenerator enemy = new com.oblong.af.sprite.enemy.BeetleGenerator(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    SpiderGenerator(Art.spiderGenerator32x16, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 50; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.SpiderGenerator enemy = new com.oblong.af.sprite.enemy.SpiderGenerator(scene);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Fireslime(Art.fireslime, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Fireslime enemy = new com.oblong.af.sprite.enemy.Fireslime(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    BlueGooboy(Art.gooboy, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 2; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Gooboy enemy = new com.oblong.af.sprite.enemy.Gooboy(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    BlackGooboy(Art.gooboy, 0, 5, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 4; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Gooboy enemy = new com.oblong.af.sprite.enemy.Gooboy(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Harpy(Art.harpy, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 15; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Harpy enemy = new com.oblong.af.sprite.enemy.Harpy(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    PurpleHarpy(Art.harpy, 0, 4, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 25; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Harpy enemy = new com.oblong.af.sprite.enemy.Harpy(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Mantis(Art.mantis, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 10; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Mantis enemy = new com.oblong.af.sprite.enemy.Mantis(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    MimeMage(Art.mimewizard, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 5; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.MimeWizard enemy = new com.oblong.af.sprite.enemy.MimeWizard(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    MimeWizard(Art.mimewizard, 0, 4, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 10; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.MimeWizard enemy = new com.oblong.af.sprite.enemy.MimeWizard(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    PinkMiniroc(Art.gullibird, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 10; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Miniroc enemy = new com.oblong.af.sprite.enemy.Miniroc(name(), scene, getXPic(), getYPic(), false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    WhiteMiniroc(Art.gullibird, 0, 4, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 15; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Miniroc enemy = new com.oblong.af.sprite.enemy.Miniroc(name(), scene, getXPic(), getYPic(), true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Petrowizard(Art.stonewizard, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 20; }
        public int getSpeed(){ return 3; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Petrowizard enemy = new com.oblong.af.sprite.enemy.Petrowizard(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Rabide(Art.rabides, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 5; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Rabide enemy = new com.oblong.af.sprite.enemy.Rabide(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Rat(Art.rat, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 2; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Rat enemy = new com.oblong.af.sprite.enemy.Rat(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    GreenRollerpede(Art.rollerpede, 2, 1, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 10; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Rollerpede enemy = new com.oblong.af.sprite.enemy.Rollerpede(name(), scene, 0, 0, false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    RedRollerpede(Art.rollerpede, 2, 6, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 15; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Rollerpede enemy = new com.oblong.af.sprite.enemy.Rollerpede(name(), scene, 0, 5, true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    BlueSeahorse(Art.seahorses, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 8; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Seahorse enemy = new com.oblong.af.sprite.enemy.Seahorse(name(), scene, 0, 0, false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    GraySeahorse(Art.seahorses, 0, 4, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 10; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Seahorse enemy = new com.oblong.af.sprite.enemy.Seahorse(name(), scene, 0, 0, true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Spikehog(Art.spikehog, 0, 3, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 15; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Spikehog enemy = new com.oblong.af.sprite.enemy.Spikehog(name(), scene, getXPic(), 0);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Spinnyblob(Art.spinnyblob, 0, 2, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 5; }
        //public Ability getInitialAbility1(){ return Ability.GENERATE_BEETLE; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Spinnyblob enemy = new com.oblong.af.sprite.enemy.Spinnyblob(name(), scene, getXPic(), 0);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Zombie(Art.zombie, 0, 1, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 8; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Zombie enemy = new com.oblong.af.sprite.enemy.Zombie(name(), scene, getXPic(), 0, false);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    SuperZombie(Art.zombie, 0, 5, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 16; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Zombie enemy = new com.oblong.af.sprite.enemy.Zombie(name(), scene, getXPic(), 0, true);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Demon(Art.demon, 0, 0, Toolbox.ToolGroup.Enemies){
        public int getMaxHp(){ return 32; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Demon enemy = new com.oblong.af.sprite.enemy.Demon(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },

    //Bosses
    BigBeholder(Art.bossBeholder64x64, 2, 2, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 100; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.BigBeholder enemy = new com.oblong.af.sprite.enemy.BigBeholder(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    StormWyvern(Art.bossWyvern112x80, 0, 0, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 100; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Wyvern enemy = new com.oblong.af.sprite.enemy.Wyvern(name(), scene, getXPic(), getYPic());
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Vampire(Art.bossVampire48x48, 0, 0, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 100; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Vampire enemy = new com.oblong.af.sprite.enemy.Vampire(scene);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    SpiderQueen(Art.bossSpider112x112, 0, 0, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 100; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.SpiderQueen enemy = new com.oblong.af.sprite.enemy.SpiderQueen(scene);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Gigapede(Art.bossGigapede48x48, 1, 1, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 64; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            GigapedeSegment enemy = GigapedeSegment.createBossGigapede(scene, 5, x, y);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    StoneKing(Art.bossStoneKing144x144, 0, 0, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 256; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.StoneKing enemy = new com.oblong.af.sprite.enemy.StoneKing(scene);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },
    Nemesis(Art.characters, 1, 1, Toolbox.ToolGroup.Bosses){
        public int getMaxHp(){ return 64; }
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            com.oblong.af.sprite.enemy.Nemesis enemy = new com.oblong.af.sprite.enemy.Nemesis(scene);
            config(enemy, template, x, y, facing);
            return enemy;
        }
    },

    //Objects
    VerticalButton(Art.objects16x32, 14, 0, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    VerticalSwitch(Art.objects16x32, 14, 1, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    LeftHorizontalButton(Art.objects16x32, 14, 4, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    RightHorizontalButton(Art.objects16x32, 14, 2, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    LeftHorizontalSwitch(Art.objects16x32, 14, 5, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    RightHorizontalSwitch(Art.objects16x32, 14, 3, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 2);
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    Lever(Art.objects16x32, 0, 4, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Switch actor = new Switch(name(), scene, getXPic(), getYPic(), 6);
            config(actor, template, x, y, facing);
            return actor;
        }
    },

    HorizontalSpikeBarricade(Art.objects16x32, 0, 0, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Barricade actor = new Barricade(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    VerticalSpikeBarricade(Art.objects16x32, 0, 1, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Barricade actor = new Barricade(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    HorizontalEnergyBarricade(Art.objects16x32, 0, 2, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            EnergyBarricade actor = new EnergyBarricade(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    VerticalEnergyBarricade(Art.objects16x32, 0, 3, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            EnergyBarricade actor = new EnergyBarricade(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    LeftVerticalDoor(Art.objects16x32, 0, 5, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    RightVerticalDoor(Art.objects16x32, 0, 6, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    TopLeftHorizontalDoor(Art.objects16x32, 0, 7, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    BottomLeftHorizontalDoor(Art.objects16x32, 0, 8, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    TopRightHorizontalDoor(Art.objects16x32, 0, 9, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    BottomRightHorizontalDoor(Art.objects16x32, 0, 10, Toolbox.ToolGroup.Barricade){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Door actor = new Door(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },

    RedCandle(Art.objects16x32, 6, 0, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Candle actor = new Candle(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    BlueCandle(Art.objects16x32, 6, 2, Toolbox.ToolGroup.Trigger){
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Candle actor = new Candle(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    DarkShroom1(Art.objects32x32x2, 6, 0, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GlowShroom(scene, x, y, getYPic());
        }
    },
    DarkShrooms2(Art.objects32x32x2, 6, 1, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GlowShroom(scene, x, y, getYPic());
        }
    },
    DarkShroom3(Art.objects32x32x2, 6, 2, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GlowShroom(scene, x, y, getYPic());
        }
    },

    MagicTargetFire(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Fire));
        }
    },
    MagicTargetWater(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Water));
        }
    },
    MagicTargetWind(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Wind));
        }
    },
    MagicTargetEarth(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Earth));
        }
    },
    MagicTargetSpirit(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Spirit));
        }
    },
    MagicTargetFreeze(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Freeze));
        }
    },
    MagicTargetElectric(Art.objects32x32, 0, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, Arrays.asList(Attribute.Electric));
        }
    },

    GigapedeDeadIndicator(Art.objects32x32, 2, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, com.oblong.af.sprite.enemy.GigapedeSegment.DEFEATED_STATE_VARIABLE);
        }
    },

    SpiderQueenDeadIndicator(Art.objects32x32, 2, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, com.oblong.af.sprite.enemy.SpiderQueen.DEFEATED_STATE_VARIABLE);
        }
    },

    VampireDeadIndicator(Art.objects32x32, 2, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, com.oblong.af.sprite.enemy.Vampire.DEFEATED_STATE_VARIABLE);
        }
    },

    StormWyvernDeadIndicator(Art.objects32x32, 2, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, com.oblong.af.sprite.enemy.Wyvern.DEFEATED_STATE_VARIABLE);
        }
    },

    BigBeholderDeadIndicator(Art.objects32x32, 2, 5, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new MagicTarget(scene, x, y, com.oblong.af.sprite.enemy.BigBeholder.DEFEATED_STATE_VARIABLE);
        }
    },

    GoddessStatue(Art.objects32x64, 5, 1, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GoddessStatue(scene, x, y);
        }
    },

    //random stuff by size: 16x32
    Mailbox(Art.objects16x32, 6, 6, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects16x32, getXPic(), getYPic(), 16, 32, 16, 16, true, false);
        }
    },
    Bush1(Art.objects16x32, 7, 6, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects16x32, getXPic(), getYPic(), 16, 32, 16, 16, true, false);
        }
    },
    Bush2(Art.objects16x32, 7, 7, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects16x32, getXPic(), getYPic(), 16, 32, 16, 16, true, false);
        }
    },
    Rock1(Art.objects16x32, 6, 7, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects16x32, getXPic(), getYPic(), 16, 32, 16, 16, true, false);
        }
    },
    FlameBasin(Art.objects16x32, 8, 6, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new FlameBasin(scene, x, y);
        }
    },

    //random stuff by size: 32x32
    PurpleFlower(Art.objects32x32x2, 0, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    PurpleFlowerWatery(Art.objects32x32x2, 1, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, true, false);
        }
    },
    LilypadFlowered(Art.objects32x32x2, 2, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    Lilypad1(Art.objects32x32x2, 3, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    LogMushroomed(Art.objects32x32x2, 0, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, true, false);
        }
    },
    LogVined(Art.objects32x32x2, 1, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, true, false);
        }
    },
    MushroomsSmall(Art.objects32x32x2, 2, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    Lilypad2(Art.objects32x32x2, 3, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    FlowerNoBloom(Art.objects32x32x2, 0, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 8, 16, false, false);
        }
    },
    FlowerRed(Art.objects32x32x2, 1, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 8, 16, false, false);
        }
    },
    FlowerPurple(Art.objects32x32x2, 2, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 8, 16, false, false);
        }
    },
    Skeleton(Art.objects32x32x2, 3, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Prop prop = new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
            prop.setLayer(Area.Layer.Lower);
            return prop;
        }
    },
    FlowerNoBloomSmall(Art.objects32x32x2, 0, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 8, 16, false, false);
        }
    },
    FlowerYellow(Art.objects32x32x2, 1, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 8, 16, false, false);
        }
    },
    GiantFlower(Art.objects32x32x2, 2, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, false, false);
        }
    },
    Stalactite(Art.objects32x32x2, 3, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 16, true, false);
        }
    },
    Crystal1(Art.objects32x32x2, 4, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },
    BrownRocks1(Art.objects32x32x2, 5, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 32, false, false);
        }
    },
    Crystal2(Art.objects32x32x2, 4, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },
    BrownRocks2(Art.objects32x32x2, 5, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 24, true, false);
        }
    },
    Tablet(Art.objects32x32x2, 4, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 24, true, false);
        }
    },
    PillarBroken(Art.objects32x32x2, 5, 2, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 24, true, false);
        }
    },
    SmallHexagram(Art.objects32x32x2, 4, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 32, false, false);
        }
    },
    PillarBrokenIvy(Art.objects32x32x2, 5, 3, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 32, 24, true, false);
        }
    },

    StoneDragonDown(Art.objects32x32x2, 0, 4, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },
    StoneDragonRight(Art.objects32x32x2, 1, 4, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },
    StoneDragonLeft(Art.objects32x32x2, 2, 4, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },
    StoneDragonUp(Art.objects32x32x2, 3, 4, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x32x2, getXPic(), getYPic(), 32, 32, 16, 16, true, false);
        }
    },

    //random objects by size: 32x64
    RedTree(Art.objects32x64, 0, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 8, 8, true, false);
        }
    },
    BlueTree(Art.objects32x64, 1, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 8, 8, true, false);
        }
    },
    PineTree(Art.objects32x64, 2, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 8, 8, true, false);
        }
    },
    PineTreeSnowy(Art.objects32x64, 4, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 8, 8, true, false);
        }
    },
    DemonStatueRight(Art.objects32x64, 3, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    DemonStatueLeft(Art.objects32x64, 4, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    DemonStatueWeird(Art.objects32x64, 5, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    SaintStatue(Art.objects32x64, 6, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    Obelisk(Art.objects32x64, 7, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    StalactiteBrown(Art.objects32x64, 0, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    StalactiteGray(Art.objects32x64, 1, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    CrystalBig(Art.objects32x64, 2, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },
    Pillar(Art.objects32x64, 3, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects32x64, getXPic(), getYPic(), 32, 64, 32, 32, true, false);
        }
    },

    //random objects by size: 64x64
    JungleTree1(Art.objects64x64, 0, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
        }
    },
    JungleTree2(Art.objects64x64, 1, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
        }
    },
    JungleTree3(Art.objects64x64, 2, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
        }
    },
    PalmTree(Art.objects64x64, 0, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
        }
    },
    TundraBush(Art.objects64x64, 3, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Thing bush = new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
            bush.setYPicO(-16);
            return bush;
        }
    },
    TundraBushSnowy(Art.objects64x64, 1, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Thing bush = new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
            bush.setYPicO(-16);
            return bush;
        }
    },
    JungleBush(Art.objects64x64, 4, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, true, false);
        }
    },
    JungleVine(Art.objects64x64, 5, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 16, 16, false, false);
        }
    },
    DragonStatue(Art.objects64x64, 7, 0, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects64x64, getXPic(), getYPic(), 64, 64, 64, 32, true, false);
        }
    },
    LargeHexagram(Art.objects112x128, 0, 3, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, Art.objects112x128, getXPic(), getYPic(), 64, 64, 64, 64, false, false);
        }
    },

    //various plants
    Glasscane(Art.objects16x32, 3, 11, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            Glasscane actor = new Glasscane(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    LeafyPlant(Art.objects16x32, 0, 12, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            GrowingPlant actor = new GrowingPlant(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    FruitingPlant(Art.objects16x32, 0, 13, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            GrowingPlant actor = new GrowingPlant(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    FloweringPlant(Art.objects16x32, 8, 12, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            GrowingPlant actor = new GrowingPlant(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },
    ThreeFloweredPlant(Art.objects16x32, 8, 13, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            GrowingPlant actor = new GrowingPlant(name(), scene, getXPic(), getYPic());
            config(actor, template, x, y, facing);
            return actor;
        }
    },

    OakTree(Art.objects112x128, 0, 2, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 4, -14, 16, 16, true, false);
        }
    },
    ElmTree(Art.objects112x128, 1, 2, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -14, 16, 16, true, false);
        }
    },
    RoundTree(Art.objects112x128, 2, 2, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -14, 16, 16, true, false);
        }
    },

    HorizontalCoffin(Art.objects64x64, 2, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 64, 64, 48, 24, true, false);
        }
    },
    VerticalCoffin(Art.objects64x64, 3, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 64, 64, 24, 40, true, false);
        }
    },
    HorizontalHedge(Art.objects64x64, 4, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 64, 64, 32, 16, true, false);
        }
    },
    VerticalHedge(Art.objects64x64, 5, 1, Toolbox.ToolGroup.Object) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 64, 64, 16, 32, true, false);
        }
    },

    //houses
    SinglePointedHouse(Art.objects112x128, 0, 0, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 64, 48, true, false);
        }
    },
    DoublePointedHouse(Art.objects112x128, 1, 0, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 96, 48, true, false);
        }
    },
    WideHouse(Art.objects112x128, 2, 0, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 96, 48, true, false);
        }
    },
    SiloHouse(Art.objects112x128, 3, 0, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 96, 48, true, false);
        }
    },
    FarmHouse(Art.objects112x128, 0, 1, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 96, 48, true, false);
        }
    },
    DoubleRoundedHouse(Art.objects112x128, 1, 1, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 96, 48, true, false);
        }
    },
    SingleBlueRoundedHouse(Art.objects112x128, 2, 1, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 64, 48, true, false);
        }
    },
    WideBlueRoundedHouse(Art.objects112x128, 3, 1, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 0, -16, 80, 48, true, false);
        }
    },
    Silo(Art.objects112x128, 3, 2, Toolbox.ToolGroup.LargeObject) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new Thing(name(), scene, x, y, getSheet(), getXPic(), getYPic(), 112, 128, 48, 48, true, false);
        }
    },

    //emitters
    SmokeEmitter(Art.editorIcons, 11, 0, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new SmokeEmitter(scene, x, y);
        }
    },
    GustEmitterUp(Art.editorIcons, 11, 1, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GustEmitter(scene, x, y, getYPic(), Math.PI/2d);
        }
    },
    GustEmitterRight(Art.editorIcons, 11, 2, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GustEmitter(scene, x, y, getYPic(), 0d);
        }
    },
    GustEmitterDown(Art.editorIcons, 11, 3, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GustEmitter(scene, x, y, getYPic(), 3*Math.PI/2d);
        }
    },
    GustEmitterLeft(Art.editorIcons, 11, 4, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new GustEmitter(scene, x, y, getYPic(), Math.PI);
        }
    },
    FlameEmitterUp(Art.editorIcons, 12, 0, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new FlameEmitter(scene, x, y, getYPic(), Math.PI/2d);
        }
    },
    FlameEmitterRight(Art.editorIcons, 12, 1, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new FlameEmitter(scene, x, y, getYPic(), 0d);
        }
    },
    FlameEmitterDown(Art.editorIcons, 12, 2, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new FlameEmitter(scene, x, y, getYPic(), 3*Math.PI/2d);
        }
    },
    FlameEmitterLeft(Art.editorIcons, 12, 3, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new FlameEmitter(scene, x, y, getYPic(), Math.PI);
        }
    },
    DestructibleIceWall(Art.objects16x48, 0, 0, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new DestructibleWall(scene, x, y, true);
        }
    },
    DestructibleRockWall(Art.objects16x48, 0, 1, Toolbox.ToolGroup.Trigger) {
        public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
            return new DestructibleWall(scene, x, y, false);
        }
    },

    //rescuables
    RescuableAdventurer(NPCs.Adventurer, true),
    RescuableKnight(NPCs.Knight, true),
    RescuableTrainConductor(NPCs.TrainConductor, true),
    RescuableRaincoat(NPCs.Raincoat, true),
    RescuableRobes(NPCs.Robes, true),
    RescuableBusinessman(NPCs.Businessman, true),
    RescuableImpresario(NPCs.Impresario, true),
    RescuableShopkeeper(NPCs.Shopkeeper, true),
    RescuableScholar(NPCs.Scholar, true),
    RescuableGovernor(NPCs.Governor, true),
    RescuableBlueOldMan(NPCs.BlueOldMan, true),
    RescuableBlueMan(NPCs.BlueMan, true),
    RescuablePilot(NPCs.Pilot, true),
    RescuableBlueScholar(NPCs.BlueScholar, true),
    RescuablePinkKid(NPCs.PinkKid, true),
    RescuableGreenOldMan(NPCs.GreenOldMan, true),
    RescuableEngineer(NPCs.Engineer, true),
    RescuableBeggar(NPCs.Beggar, true),
    RescuableBlacksmith(NPCs.Blacksmith, true),
    RescuableBlueKid(NPCs.BlueKid, true),
    RescuableWaterCarrier(NPCs.WaterCarrier, true),
    RescuableFlowerGirl(NPCs.FlowerGirl, true),
    RescuableMechanic(NPCs.Mechanic, true),
    RescuableOldFarmLady(NPCs.OldFarmLady, true),
    RescuableBlueOldLady(NPCs.BlueOldLady, true),
    RescuablePolicewoman(NPCs.Policewoman, true),
    RescuableBlueChick(NPCs.BlueChick, true),
    RescuablePinkChick(NPCs.PinkChick, true),
    RescuableHealerChick(NPCs.HealerChick, true),
    RescuablePinkBowGirl(NPCs.PinkBowGirl, true),
    RescuableBrent(NPCs.Brent, true),
    RescuableSean(NPCs.Sean, true),
    RescuableDragon(NPCs.Dragon, true),
    RescuableAllen(NPCs.Allen, true),
    RescuableMatt(NPCs.Matt, true),
    RescuableMarshall(NPCs.Marshall, true),
    RescuableAdam(NPCs.Adam, true),
    RescuablePatrick(NPCs.Patrick, true),

    //npcs
    TalkableAdventurer(NPCs.Adventurer, false),
    TalkableKnight(NPCs.Knight, false),
    TalkableTrainConductor(NPCs.TrainConductor, false),
    TalkableRaincoat(NPCs.Raincoat, false),
    TalkableRobes(NPCs.Robes, false),
    TalkableBusinessman(NPCs.Businessman, false),
    TalkableImpresario(NPCs.Impresario, false),
    TalkableShopkeeper(NPCs.Shopkeeper, false),
    TalkableScholar(NPCs.Scholar, false),
    TalkableGovernor(NPCs.Governor, false),
    TalkableBlueOldMan(NPCs.BlueOldMan, false),
    TalkableBlueMan(NPCs.BlueMan, false),
    TalkablePilot(NPCs.Pilot, false),
    TalkableBlueScholar(NPCs.BlueScholar, false),
    TalkablePinkKid(NPCs.PinkKid, false),
    TalkableGreenOldMan(NPCs.GreenOldMan, false),
    TalkableEngineer(NPCs.Engineer, false),
    TalkableBeggar(NPCs.Beggar, false),
    TalkableBlacksmith(NPCs.Blacksmith, false),
    TalkableBlueKid(NPCs.BlueKid, false),
    TalkableWaterCarrier(NPCs.WaterCarrier, false),
    TalkableFlowerGirl(NPCs.FlowerGirl, false),
    TalkableMechanic(NPCs.Mechanic, false),
    TalkableOldFarmLady(NPCs.OldFarmLady, false),
    TalkableBlueOldLady(NPCs.BlueOldLady, false),
    TalkablePolicewoman(NPCs.Policewoman, false),
    TalkableBlueChick(NPCs.BlueChick, false),
    TalkablePinkChick(NPCs.PinkChick, false),
    TalkableHealerChick(NPCs.HealerChick, false),
    TalkablePinkBowGirl(NPCs.PinkBowGirl, false),
    TalkableBrent(NPCs.Brent, false),
    TalkableSean(NPCs.Sean, false),
    TalkableDragon(NPCs.Dragon, false),
    TalkableAllen(NPCs.Allen, false),
    TalkableMatt(NPCs.Matt, false),
    TalkableMarshall(NPCs.Marshall, false),
    TalkableAdam(NPCs.Adam, false),
    TalkablePatrick(NPCs.Patrick, false),

    Dog(NPCs.Dog, false),

    //Powerups
    HpPotion(Powerups.HpPotion),
    BigHpPotion(Powerups.BigHpPotion),
    FullHpPotion(Powerups.FullHpPotion),
    HpMaxUpPotion(Powerups.HpMaxUpPotion),
    PoisonPotion(Powerups.PoisonPotion),
    OneUp(Powerups.OneUp),
    AffinityGem(Powerups.AffinityGem),

    AegisAbility(Ability.Aegis),
    AxeAbility(Ability.Axe),
    BlinkAbility(Ability.Blink),
    BombsAbility(Ability.Bombs),
    ChargeAbility(Ability.Charge),
    CleanseAbility(Ability.Cleanse),
    CureWoundsAbility(Ability.CureWounds),
    DeathBoltAbility(Ability.DeathBolt),
    DrownAbility(Ability.Drown),
    EarthAffinityAbility(Ability.Earthproof),
    EarthRendAbility(Ability.EarthRend),
    ExplodeAbility(Ability.Explode),
    FierySpinAbility(Ability.FierySpin),
    FingersOfTheEarthAbility(Ability.FingersOfTheEarth),
    FireAffinityAbility(Ability.Fireproof),
    FirefootAbility(Ability.Firefoot),
    FirestormAbility(Ability.Firestorm),
    FlameShieldAbility(Ability.FlameShield),
    FlamethrowerAbility(Ability.Flamethrower),
    FlamingAuraAbility(Ability.FlamingAura),
    FlushAbility(Ability.Flush),
    FreezeAbility(Ability.Freeze),
    FreezingAuraAbility(Ability.FreezingAura),
    FreezingBoltAbility(Ability.FreezingBolt),
    GustAbility(Ability.Gust),
    HasteAbility(Ability.Haste),
    HomingBoltAbility(Ability.HomingBolt),
    LaserAbility(Ability.Laser),
    LifesparkAbility(Ability.Lifespark),
    LightningCoilAbility(Ability.LightningCoil),
    MaceAbility(Ability.Mace),
    OsmoseAbility(Ability.Osmose),
    PetrifyAbility(Ability.Petrify),
    PistolAbility(Ability.Pistol),
    PoisonBreathAbility(Ability.PoisonBreath),
    PoisonStingAbility(Ability.PoisonSting),
    QuakeAbility(Ability.Quake),
    RegenerateAbility(Ability.Regenerate),
    RingOfFireAbility(Ability.RingOfFire),
    SandAbility(Ability.Sand),
    ShockFingersAbility(Ability.ShockFingers),
    ShockingAuraAbility(Ability.ShockingAura),
    ShoutAbility(Ability.Shout),
    SparklerAbility(Ability.Sparkler),
    SpeedAbility(Ability.Speed),
    SpiritAffinityAbility(Ability.Spiritproof),
    SquirtAbility(Ability.Squirt),
    StaffAbility(Ability.Staff),
    StonefistAbility(Ability.Stonefist),
    StoneskinAbility(Ability.Stoneskin),
    StormsEyeAbility(Ability.StormsEye),
    SummonRatAbility(Ability.SummonRat),
    SwordAbility(Ability.Sword),
    ThrowingAxesAbility(Ability.ThrowingAxes),
    ThrowingKnivesAbility(Ability.ThrowingKnives),
    TornadoAbility(Ability.Tornado),
    TunnelAbility(Ability.Tunnel),
    TwirlAbility(Ability.Twirl),
    VanishAbility(Ability.Vanish),
    WailAbility(Ability.Wail),
    WaterAffinityAbility(Ability.Waterproof),
    WindAffinityAbility(Ability.Windproof),
    WindKickAbility(Ability.WindKick),
    ;
    private int xPic, yPic;
    private Image[][] sheet;
    private int portraitXPic, portraitYPic;
    private int maxHp, speed;
    private Ability initialAbility1, initialAbility2, initialAbility3, initialAbilityAuto;
    private int affEarth, affWater, affAir, affFire, affSpirit, affSpare;
    private Skills[] skills;
    private Toolbox.ToolGroup toolGroup;

    public Ability ability;
    public NPCs npc;
    public boolean rescuableNPC;
    private Powerups powerupDef;

    private SpriteDefinitions(Image[][] sheet, int xPic, int yPic, Toolbox.ToolGroup toolGroup){
        this.sheet = sheet;
        this.xPic = xPic;
        this.yPic = yPic;
        speed = 0;
        this.toolGroup = toolGroup;
    }
    
    private SpriteDefinitions(Ability ability){
        this.ability = ability;

        sheet = Art.powerups;
        xPic = Powerups.AbilityGem.getXPic();
        yPic = Powerups.AbilityGem.getYPic();
        speed = 0;
        toolGroup = Toolbox.ToolGroup.Ability;
    }

    private SpriteDefinitions(NPCs npc, boolean rescuableNPC){
        this.npc = npc;
        this.rescuableNPC = rescuableNPC;

        sheet = Art.characters;
        xPic = npc.getXPic();
        yPic = npc.getYPic();
        speed = npc.getSpeed();
        if(rescuableNPC) toolGroup = Toolbox.ToolGroup.Rescuables;
        else toolGroup = Toolbox.ToolGroup.NPCs;
    }

    private SpriteDefinitions(Powerups powerupDef){
        this.powerupDef = powerupDef;

        sheet = Art.powerups;
        xPic = powerupDef.getXPic();
        yPic = powerupDef.getYPic();
        speed = 0;
        toolGroup = Toolbox.ToolGroup.Powerup;
    }

    public int getPortraitXPic(){ return portraitXPic; }
    public int getPortraitYPic(){ return portraitYPic; }
    public Image[][] getSheet(){ return sheet; }
    public int getXPic(){ return xPic; }
    public int getYPic(){ return yPic; }
    public int getMaxHp(){ return maxHp; }
    public int getSpeed(){ return speed; }
    public Ability getInitialAbility1(){ return initialAbility1; }
    public Ability getInitialAbility2(){ return initialAbility2; }
    public Ability getInitialAbility3(){ return initialAbility3; }
    public Ability getInitialAutoAbility(){ return initialAbilityAuto; }
    public int getInitialEarthAffinity(){ return affEarth; }
    public int getInitialWaterAffinity(){ return affWater; }
    public int getInitialAirAffinity(){ return affAir; }
    public int getInitialFireAffinity(){ return affFire; }
    public int getInitialSpiritAffinity(){ return affSpirit; }
    public int getInitialSpareAffinity(){ return affSpare; }
    public Skills[] getSkills(){ return skills; }
    public Toolbox.ToolGroup getToolGroup(){ return toolGroup; }

    public Sprite create(AreaScene scene, SpriteTemplate template, int x, int y, Facing facing){
        if (ability != null){
            Powerup actor = new Powerup(scene, Powerups.AbilityGem, ability, x, y);
            config(actor, template, x, y, facing);
            return actor;
        }
        else if (powerupDef != null){
            Powerup actor = new Powerup(scene, powerupDef, null, x, y);
            config(actor, template, x, y, facing);
            return actor;
        }
        else if (npc != null){
            if (npc == NPCs.Dog){
                Prop npc = NPCs.Dog.createTalkableNPC(scene);
                config(npc, template, x, y, facing);
                return npc;
            }
            else if (rescuableNPC){
                RescuableNPC rnpc = npc.createRescuableNPC(scene);
                if (rnpc == null) return null;
                config(rnpc, template, x, y, facing);
                return rnpc;
            }
            else{
                Prop pnpc = npc.createTalkableNPC(scene);
                if (pnpc == null) return null;
                config(pnpc, template, x, y, facing);
                return pnpc;
            }
        }
        return null;
    }

    protected void config(Prop prop, SpriteTemplate template, int x, int y, Facing facing){
        prop.setSpriteTemplate(template);
        prop.setX(x);
        prop.setY(y);
        prop.setFacing(facing);
        prop.setXPic(getXPic());
        prop.setYPic(getYPic());
        prop.setSpeed(getSpeed());
        prop.setMaxHp(getMaxHp());
        prop.setHp(getMaxHp());
    }
    
    protected void config(Actor actor, SpriteTemplate template, int x, int y, Facing facing){
        config((Prop)actor, template, x, y, facing);
        actor.setPortraitXPic(getPortraitXPic());
        actor.setPortraitYPic(getPortraitYPic());
        actor.getInitialAbilitySlots()[0].ability = getInitialAbility1();
        actor.getInitialAbilitySlots()[1].ability = getInitialAbility2();
        actor.getInitialAbilitySlots()[2].ability = getInitialAbility3();
        actor.getInitialAbilitySlots()[3].ability = getInitialAutoAbility();
        actor.getAbilitySlots()[0].ability = getInitialAbility1();
        actor.getAbilitySlots()[1].ability = getInitialAbility2();
        actor.getAbilitySlots()[2].ability = getInitialAbility3();
        actor.getAbilitySlots()[3].ability = getInitialAutoAbility();
        actor.setSkills(getSkills());
    }

    public static java.util.List<SpriteDefinitions> getPlayerCharacterDefs(){ return Arrays.asList(Adam, Allen, Brent, Dragon, Marshall, Matt, Patrick, Sean); }
    public static boolean isEnemy(String id){ return contains(id, Arrays.asList(Beetle, BeetleGenerator, BlackGooboy,
            BlueGooboy, Demon, SuperZombie, Zombie, BlueSeahorse, Firebat, Icebat, Fireslime, GraySeahorse, GreenRollerpede, GreenBeholder, Harpy,
            Mantis, MimeMage, MimeWizard, Petrowizard, PinkMiniroc, PurpleBeholder, PurpleHarpy, Rabide, Rat, RedRollerpede, Spikehog, Spinnyblob,
            WhiteMiniroc, BigBeholder, StormWyvern, Vampire, SpiderQueen, Gigapede, StoneKing, Nemesis)); }

    private static boolean contains(String id, List<SpriteDefinitions> actors){
        for (SpriteDefinitions a: actors)
            if (a.name().equals(id))
                return true;
        return false;
    }

}

package com.oblong.af.models;

import com.oblong.af.util.Art;

import java.awt.*;

public enum Skills {

    SpeedyHealing("Speedy Healing", "Gain 1.5x HP when healing.", 8, 3),
    FastHands("Fast Hands", "Double cooldown speed for physical abilities.", 9, 0),
    Blazin("Blazin'", "Double cooldown speed for Fire abilities.", 9, 1),
    Streamin("Streamin'", "Double cooldown speed for Water abilities.", 9, 2),
    Breezin("Breezin'", "Double cooldown speed for Air abilities.", 9, 3),
    Rooted("Rooted", "Double cooldown speed for Earth abilities.", 9, 4),
    Astral("Astral", "Double cooldown speed for Spirit abilities.", 9, 5),
    Sprinter("Sprinter", "Speed increased by 1.5x.", 8, 4),
    QuickStudy("Quick Study", "Gain double affinity points from powerups.", 8, 5),
    HardDrinker("Hard Drinker", "Gain more Max HP from Max HP Up potions.", 8, 6),
    ToxicBlood("Toxic Blood", "Gain health from poison damage.", 8, 7),
    Potential("Potential", "Start the game with two bonus affinity points to spend.", 9, 6)
    ;
    private String name, description;
    private int xPic, yPic;
    private Skills(String name, String description, int xPic, int yPic){
        this.name = name;
        this.description = description;
        this.xPic = xPic;
        this.yPic = yPic;
    }

    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public Image getIcon(){ return Art.abilities[xPic][yPic]; }

}

package com.oblong.af.models;

import com.oblong.af.util.Art;

import java.awt.*;

public enum Affinity {

    None ("None", "None", new Color(0.5f, 0.5f, 0.5f, 0f), new Color(0.5f, 0.5f, 0.5f, 0f), new Color(0.5f, 0.5f, 0.5f, 0f), null),
    Fire ("Fire", "Fire magic controls energy and heat - both creating and removing it.",
            new Color(0.8f, 0f, 0f),
            new Color(1f, 0.2f, 0.2f),
            new Color(0.5f, 0f, 0f),
            Art.consoleIcons16x16[0][0]),
    Water ("Water", "Water magic controls liquid matter, and deals with healing, knowledge, and the arcane.",
            new Color(0f, 0.5f, 0.8f),
            new Color(0.5f, 0.7f, 1f),
            new Color(0f, 0.3f, 0.5f),
            Art.consoleIcons16x16[2][0]),
    Air ("Air", "Air magic controls gaseous matter, and deals with speed and movement.",
            new Color(0.8f, 0.8f, 0f),
            new Color(1f, 1f, 0.2f),
            new Color(0.5f, 0.5f, 0.0f),
            Art.consoleIcons16x16[1][0]),
    Earth ("Earth", "Earth magic controls solid matter, and deals with defense, steadfastness, and weightiness or something.",
            new Color(0f, 0.8f, 0f),
            new Color(0.2f, 1f, 0.2f),
            new Color(0f, 0.3f, 0f),
            Art.consoleIcons16x16[3][0]),
    Spirit ("Spirit", "Spirit magic is the control of that which is beyond, the spark of life or the kiss of death.",
            new Color(0.8f, 0.8f, 0.8f),
            new Color(1f, 1f, 1f),
            new Color(0.5f, 0.5f, 0.5f),
            Art.consoleIcons16x16[1][1]),
    ;

    public static final int MAX_VALUE = 5;

    private String name, description;
    private Color color, highlightColor, darkColor;
    private Image icon;

    private Affinity(String name, String description, Color color, Color highlightColor, Color darkColor, Image icon){
        this.name = name;
        this.description = description;
        this.color = color;
        this.highlightColor = highlightColor;
        this.darkColor = darkColor;
        this.icon = icon;
    }

    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public Color getColor(){ return color; }
    public Color getHighlightColor(){ return highlightColor; }
    public Color getDarkColor(){ return darkColor; }
    public Image getIcon(){ return icon; }

}

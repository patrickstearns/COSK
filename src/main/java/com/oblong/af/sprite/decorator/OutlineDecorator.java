package com.oblong.af.sprite.decorator;

import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class OutlineDecorator extends AbstractSpriteDecorator {

    private Color[] colors;
    private int cycleRate;

    /**
     * Create a constant outline of a certain color.
     * @param color color to outline
     * @param timeToLive how long for it to last, or AbstractSpriteDecrator.INFINITE_TTL to last until removed
     */
    public OutlineDecorator(Color color, int timeToLive){ this(new Color[]{color}, 0, timeToLive); }

    /**
     * Create a pulsing outline.
     * @param color color to pulse
     * @param rate pulse rate (e.g. time to get back to start)
     * @param timeToLive how long for it to last, or AbstractSpriteDecrator.INFINITE_TTL to last until removed
     */
    public OutlineDecorator(Color color, int rate, int timeToLive){ this(new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 128)},
            rate, timeToLive); }

    /**
     * Create a multicolored cycling outline.
     * @param colors colors to cycle through
     * @param cycleRate cycle rate (e.g. time to get back to start)
     * @param timeToLive how long for it to last, or AbstractSpriteDecrator.INFINITE_TTL to last until removed
     */
    public OutlineDecorator(Color[] colors, int cycleRate, int timeToLive){
        super(timeToLive);
        this.colors = colors;
        this.cycleRate = cycleRate;
    }

    public void decorate(Sprite sprite, int tick){
        sprite.setOutlineColor(ImageUtils.calculateCycleColor(tick, colors, cycleRate));
    }

    public Color[] getColors(){ return colors; }

}

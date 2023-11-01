package com.oblong.af.sprite.decorator;

import com.oblong.af.sprite.Sprite;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class TintDecorator extends AbstractSpriteDecorator  {

    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);

    private boolean desaturating;
    private Color[] colors;
    private int cycleRate;

    /**
     * Create a desaturating "tint" for a specified number of ticks (or forever).
     * @param timeToLive time to stay tinted in ticks, or SprintTint.INFINITE_TTL to stay until removed.
     */
    public TintDecorator(int timeToLive){ this(null, timeToLive); }

    /**
     * Create a constant tint for a specified number of ticks (or forever).
     * @param timeToLive time to stay tinted in ticks, or SprintTint.INFINITE_TTL to stay until removed.
     */
    public TintDecorator(Color color, int timeToLive){ this(new Color[]{ color }, 0, timeToLive); }

    /**
     * Create a pulsating tint cycling between transparent and the specified color, at the specified rate for however
     *   long, or SprintTint.INFINITE_TTL to stay until removed.
     * @param color Color to pulse
     * @param rate how fast to pulse (in ticks); e.g. to go from clear to color to clear again
     * @param timeToLive how long this tint should last for
     */
    public TintDecorator(Color color, int rate, int timeToLive){ this(new Color[]{color, TRANSPARENT}, rate, timeToLive); }

    /**
     * Create a pulsating tint cycling between the specified list of colors, at the specified rate for however
     *   long, or SprintTint.INFINITE_TTL to stay until removed.
     * @param colors Colors to cycle through
     * @param cycleRate how fast to cycle through the list (in ticks); e.g. to get back to first color again
     * @param timeToLive how long this tint should last for
     */
    public TintDecorator(Color[] colors, int cycleRate, int timeToLive){
        super(timeToLive);
        desaturating = colors[0] == null;
        this.colors = colors;
        this.cycleRate = cycleRate;
    }

    public void decorate(Sprite sprite, int tick){
        if (desaturating)sprite.setDesaturated(true);
        else sprite.setTintColor(ImageUtils.calculateCycleColor(tick, colors, cycleRate));
    }

}

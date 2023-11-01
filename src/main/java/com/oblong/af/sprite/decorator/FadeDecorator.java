package com.oblong.af.sprite.decorator;

import com.oblong.af.sprite.Sprite;

public class FadeDecorator extends AbstractSpriteDecorator {

    private float visibleRatio;
    private int fadeDirection; //-1 means fade out, 1 means fade in
    private int ottl;

    public FadeDecorator(float visibleRatio, int timeToLive){ this(visibleRatio, 0, timeToLive); }

    public FadeDecorator(float visibleRatio, int fadeDirection, int timeToLive){
        super(timeToLive);
        this.fadeDirection = fadeDirection;
        this.visibleRatio = visibleRatio;
        ottl = timeToLive;
    }

    public void decorate(Sprite sprite, int tick){
        if (fadeDirection != 0){
            float ratio = visibleRatio;
            if (fadeDirection < 0) ratio = (float)timeToLive/(float)ottl;
            else if (fadeDirection > 0) ratio = 1f - (float)timeToLive/(float)ottl;;
            sprite.setFadeRatio(ratio);
        }
        else sprite.setFadeRatio(visibleRatio);
    }

}

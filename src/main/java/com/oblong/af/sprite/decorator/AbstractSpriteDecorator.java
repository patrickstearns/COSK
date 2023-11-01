package com.oblong.af.sprite.decorator;

import com.oblong.af.sprite.Sprite;

public abstract class AbstractSpriteDecorator {

    public static final int INFINITE_TTL = -1;

    protected int timeToLive;

    protected AbstractSpriteDecorator(int timeToLive){
        this.timeToLive = timeToLive;
    }

    /**
     * Update this decorator by one tick
     * @param sprite the sprite this decorator is applied to
     */
    public void tick(Sprite sprite){
        if (timeToLive > 0 && timeToLive != INFINITE_TTL){
            timeToLive--;
            if (timeToLive == 0) sprite.removeDecorator(this);
            else decorate(sprite, sprite.getTick());
        }
    }

    /**
     * Decorate an image
     * @param sprite sprite to decorate
     * @param tick current tick counter
     * @return decorated image
     */
    protected abstract void decorate(Sprite sprite, int tick);

}

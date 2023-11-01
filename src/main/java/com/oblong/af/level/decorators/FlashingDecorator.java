package com.oblong.af.level.decorators;

import com.oblong.af.level.AreaScene;

import java.awt.*;

public class FlashingDecorator extends LayerDecorator {

    private int maxTicks = Integer.MAX_VALUE;

	public FlashingDecorator(){}

    public FlashingDecorator(int maxTicks){
        this.maxTicks = maxTicks;
    }

	public void tick(AreaScene scene){
		reinit(); //clears image
        if (tickCounter%2==0){
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 320, 240);
        }
        tickCounter++;
    }

    public boolean isComplete(){ return tickCounter >= maxTicks; }

}

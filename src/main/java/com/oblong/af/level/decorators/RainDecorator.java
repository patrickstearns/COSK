package com.oblong.af.level.decorators;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

import java.awt.*;

public class RainDecorator extends LayerDecorator {

	private static Color[] COLORS = new Color[]{new Color(0.9f, 0.9f, 1f, 0.9f), new Color(0.5f, 0.5f, 1f, 0.9f), new Color(0.5f, 0.5f, 0.6f, 0.9f)};
	
	public RainDecorator(){}

	public void tick(AreaScene scene){
		reinit(); //clears image

        if (tickCounter%36 == 0){
            scene.getSound().play(Art.getSample("rain.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
        }

        tickCounter++;
		for (int i = 0; i < 100; i++){
			int colorIndex = ((int)(Math.random()*1000))%3;
			int x1 = ((int)(Math.random()*1000))%320;
			int y1 = ((int)(Math.random()*1000))%240;
			graphics.setColor(COLORS[colorIndex]);
			graphics.drawLine(x1, y1, x1+5, y1-10);
		}
    }
}

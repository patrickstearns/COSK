package com.oblong.af.level.decorators;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.level.AreaScene;
import com.oblong.af.util.Art;

public class RandomQuakeDecorator extends LayerDecorator {

	public RandomQuakeDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
	}
	
	public void tick(AreaScene scene){
        if (Math.random() < 0.05){
            scene.shake(16);
            scene.getSound().play(Art.getSample("quake.wav"), new FixedSoundSource(160, 120), 5, 1, 1);
        }
    }
}

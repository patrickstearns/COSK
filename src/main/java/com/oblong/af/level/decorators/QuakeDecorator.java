package com.oblong.af.level.decorators;

import com.oblong.af.level.AreaScene;

/**
 * Does nothing on its own; is just a placeholder
 */
public class QuakeDecorator extends LayerDecorator {

	public QuakeDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
	}
	
	public void tick(AreaScene scene){
        scene.shake(2);
    }
}

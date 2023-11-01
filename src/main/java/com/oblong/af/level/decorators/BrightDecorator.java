package com.oblong.af.level.decorators;

import java.awt.*;

public class BrightDecorator extends LayerDecorator {

	private static Color OVERLAY_COLOR = new Color(1f, 1f, 0.9f, 0.2f);

	public BrightDecorator(){}
	
	public void init(int width, int height){
		super.init(width, height);
		graphics.setColor(OVERLAY_COLOR);
		graphics.fillRect(0, 0, width, height);
	}
}

package com.oblong.af.level.decorators;

import java.awt.*;

public class NightDecorator extends LayerDecorator {

	private static Color OVERLAY_COLOR = new Color(0.0f, 0.0f, 0.5f, 0.4f);

	public NightDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
		graphics.setColor(OVERLAY_COLOR);
		graphics.fillRect(0, 0, width, height);
	}
}

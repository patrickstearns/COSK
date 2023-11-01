package com.oblong.af.level.decorators;

import java.awt.*;

public class OvercastDecorator extends LayerDecorator {

	private static Color OVERLAY_COLOR = new Color(0f, 0f, 0f, 0.1f);

	public OvercastDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
		graphics.setColor(OVERLAY_COLOR);
		graphics.fillRect(0, 0, width, height);
	}
}

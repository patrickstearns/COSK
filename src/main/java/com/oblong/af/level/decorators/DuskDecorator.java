package com.oblong.af.level.decorators;

import java.awt.*;

public class DuskDecorator extends LayerDecorator {

	private static Color OVERLAY_COLOR = new Color(0.8f, 0.4f, 0f, 0.3f);

	public DuskDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
		graphics.setColor(OVERLAY_COLOR);
		graphics.fillRect(0, 0, width, height);
	}
}

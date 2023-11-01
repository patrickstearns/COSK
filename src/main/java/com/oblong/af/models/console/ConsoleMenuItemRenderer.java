package com.oblong.af.models.console;

import java.awt.*;

public abstract class ConsoleMenuItemRenderer<T> {

	private Rectangle size;
	
	protected ConsoleMenuItemRenderer(Rectangle size){
		this.size = size;
	}
	
	public Rectangle getSize(){ return size; }
    public void setSize(Rectangle size){ this.size = size; }

	public abstract void renderItem(Graphics2D g, T item, ConsoleMenu menu, int index, Point location, boolean enabled, boolean focused);
}

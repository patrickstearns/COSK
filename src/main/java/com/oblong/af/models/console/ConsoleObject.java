package com.oblong.af.models.console;

import java.awt.*;
import java.io.Serializable;
import java.util.Comparator;

/**
 * ConsoleObject is the model for a given "thing" on the console - some menu or display element.
 */

public abstract class ConsoleObject implements Serializable{
	
	//objects with low Z-order are "deeper" into the monitor, and "greater" according to this comparator.
	public static final Comparator<ConsoleObject> Z_ORDER_COMPARATOR = new Comparator<ConsoleObject>() {
		public int compare(ConsoleObject co1, ConsoleObject co2){
			if (co1.getZOrder() == co2.getZOrder()) {
				if (co1.getName() != null) return co1.getName().compareTo(co2.getName());
				else return 0;
			}
			else if (co1.getZOrder() < co2.getZOrder()) return 1;
			else return -1;
		}
	};
	
	private Color background;
	private Font font;
	private boolean focusable, focused;
	private int zOrder;
	private Console console;
	protected int tickCounter = 0;
	private String name;
	private boolean scrollable;
	private Rectangle bounds;

	protected ConsoleObject(Console console, String name, Rectangle bounds, int zOrder, boolean focusable){
		this.console = console;
		setName(name);
        setBounds(bounds);
		setScrollable(scrollable);
		setBackground(getConsole().getBackground());
		setFont(Console.STANDARD_FONT);
		setFocusable(focusable);
		setFocused(false);
		setZOrder(zOrder);
	}
	
	protected Console getConsole(){ return console; }
	
	public Color getBackground() { return background; }
	public void setBackground(Color background){ this.background = background; }

	public Font getFont() { return font; }
	public void setFont(Font font){ this.font = font; }

	public boolean isFocused() { return focused; }
	public void setFocused(boolean focused){ this.focused = focused; }

	public boolean isFocusable(){ return focusable; }
	public void setFocusable(boolean focusable){ this.focusable = focusable; }

	public int getZOrder(){ return zOrder; }
	public void setZOrder(int zOrder){ this.zOrder = zOrder; }

	public abstract void paintContents(Graphics g);
	protected void paintBackground(Graphics g, Rectangle bounds){
		g.setColor(getBackground());
		g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 5, 5);
	}
	
	protected void paintBorder(Graphics g, Rectangle bounds){
		if (!isFocused() || !isFocusable()) g.setColor(Color.DARK_GRAY);
		else g.setColor(Color.WHITE);
		g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 5, 5);
	}
	
	public void tick(){
		tickCounter++;
	}
	
	public void paint(Graphics g){
		paintBackground(g, getBounds());
		paintBorder(g, getBounds());
		paintContents(g);
	}
	
	public void moveUp(){}
	public void moveDown(){}
	public void moveLeft(){}
	public void moveRight(){}
	public void select(){}
	public void cancel(){}

	public String getName() { return name; }
	public void setName(String name){ this.name = name; }

	public Rectangle getBounds() { return bounds; }
	public void setBounds(Rectangle bounds){ this.bounds = bounds; }

	public boolean isScrollable() { return scrollable; }
	public void setScrollable(boolean scrollable){ this.scrollable = scrollable; }

	public void scroll(int dx, int dy){if (!isScrollable()) throw new IllegalStateException("This object is not scrollable."); }

}

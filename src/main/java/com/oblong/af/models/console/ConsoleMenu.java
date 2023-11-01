package com.oblong.af.models.console;

import com.oblong.af.util.Art;

import java.awt.*;
import java.util.Vector;

public abstract class ConsoleMenu<T> extends ConsoleObject {

	protected java.util.List<T> items;
	protected T focusedItem, selectedItem;
	protected ConsoleMenu parentMenu;
	protected boolean mouseIsOver = false;
	private Point location;
	private ConsoleMenuItemRenderer<T> renderer;
	
	public ConsoleMenu(Console console, ConsoleMenu parentMenu, String name, Point location, ConsoleMenuItemRenderer<T> renderer, java.util.List<T> items){
		super(console, name,
                new Rectangle(location.x, location.y, (int)renderer.getSize().getWidth(), (int)renderer.getSize().getHeight()*items.size()),
                parentMenu == null ? 0 : parentMenu.getZOrder()-1, true);
		setLocation(new Point(location));
		setRenderer(renderer);
        setParentMenu(parentMenu);
        this.items = items;
        setBackground(new Color(0f, 0.3f, 1f, 0.5f));
    }

    public Image getCursor(){ return Art.consoleIcons16x16[2][1]; }

    public Point getLocation(){ return location; }
	public void setLocation(Point location){ this.location = location; }
	
	public ConsoleMenuItemRenderer<T> getRenderer(){ return renderer; }
	public void setRenderer(ConsoleMenuItemRenderer<T> renderer){ this.renderer = renderer; }

    public int indexAt(Point p){
        for (int i = 0; i < items.size(); i++){
            if (getItemBounds(i).contains(p))
                return i;
        }
        return -1;
    }

    public Rectangle getItemBounds(int index){
		return new Rectangle(
			getItemLocation(items.get(index)),
			new Dimension(renderer.getSize().width, renderer.getSize().height)
		);
	}
	
	public void setFocused(boolean focused){
		super.setFocused(focused);
		if(focused) selectedItem = null;
	}
	
	public ConsoleMenu getParentMenu(){ return parentMenu; }
	public void setParentMenu(ConsoleMenu parentMenu){ this.parentMenu = parentMenu; }
	
	public java.util.List<T> getItems(){ return items; }
	public void setItems(java.util.List<T> items){
		this.items = new Vector<T>(items);
		if (this.items.size() > 0) setFocusedItem(this.items.get(0));
	}

	public T getFocusedItem(){ return focusedItem; }
	public void setFocusedItem(T focusedItem){ this.focusedItem = focusedItem; }

	public void moveLeft(){ moveUp(); }
	public void moveRight(){ moveDown(); }
	
	private long lastMoveTime = System.currentTimeMillis();
	public void moveUp(){
		long moveTime = System.currentTimeMillis();
		if (moveTime-lastMoveTime < 150) return;
		lastMoveTime = moveTime;
		
		T item;
		int ind = items.indexOf(getFocusedItem())-1;
		if (ind < 0) ind = items.size()-1;
		item = items.get(ind);
		setFocusedItem(item);
	}
	
	public void moveDown(){
		long moveTime = System.currentTimeMillis();
		if (moveTime-lastMoveTime < 150) return;
		lastMoveTime = moveTime;
		
		T item;
		int ind = items.indexOf(getFocusedItem())+1;
		if (ind >= items.size()) ind = 0;
		item = items.get(ind);
		setFocusedItem(item);
	}
	
    public void mouseMoved(Point p){
        int index = indexAt(p);
        if (index == -1) return;
        focusedItem = items.get(index);
    }

    public void mouseClicked(Point p){
        int index = indexAt(p);
        if (index == -1) return;
        focusedItem = items.get(index);
        select();
    }

	public Point getItemLocation(T item){
		return new Point(getBounds().x, getBounds().y+items.indexOf(item)*renderer.getSize().height);
	}

    public void select(){
        selectedItem = focusedItem;
        getConsole().remove(this);
    }

    public void cancel(){
		selectedItem = null; 
		getConsole().remove(this);
	}

	protected void paintBackground(Graphics g, Rectangle bounds){
		if (isFocused() && mouseIsOver){
			Color color = new Color(getBackground().getRed()/2+128, getBackground().getGreen()/2+128, 
					getBackground().getBlue()/2+128, getBackground().getAlpha());
			g.setColor(color);
		}
		else if (mouseIsOver){
			Color color = new Color(getBackground().getRed()/2+64, getBackground().getGreen()/2+64, 
					getBackground().getBlue()/2+64, getBackground().getAlpha());
			g.setColor(color);
		}
		else g.setColor(getBackground());
		g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15);
	}

	protected void paintItems(Graphics2D g, Point upperLeftCorner){
        int x = upperLeftCorner.x, y = upperLeftCorner.y;

		int xStep = 0, yStep = renderer.getSize().height;
		x += 4;
		y += 4;
		for (int i = 0; i < items.size(); i++){
            T item = items.get(i);
			renderer.renderItem(g, item, this, i, new Point(x, y), true, item == getFocusedItem());
			x += xStep;
			y += yStep;
		}
	}
	
	public T getSelectedItem(){ return selectedItem; }
	
	public String toString(){ return getName(); }
}

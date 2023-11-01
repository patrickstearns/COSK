package com.oblong.af.models.console;


import java.awt.*;


public class ConsoleObjectScroller extends ConsoleObject {

	private static final long serialVersionUID = 9034588514816205132L;

	private static final int height = 20;
	
	private int tickCounter = 0;
	private ConsoleObject parentObject;
	private int numTicks, selectedTick;
	private boolean visible;
	
	public ConsoleObjectScroller(Console console, ConsoleObject parentObject, int numTicks, int selectedTick){
		super(console, "Scroller", null, parentObject.getZOrder(), false);
		setParentObject(parentObject);
		setNumTicks(numTicks);
		setSelectedTick(selectedTick);
	}

	public int getNumTicks(){ return numTicks; }
	public void setNumTicks(int numTicks){this.numTicks = numTicks; }

	public int getSelectedTick(){ return selectedTick; }
	public void setSelectedTick(int selectedTick){ this.selectedTick = selectedTick; }

	public ConsoleObject getParentObject(){ return parentObject; }
	public void setParentObject(ConsoleObject parentObject){ this.parentObject = parentObject; }

	private void recalculateBounds(){
		Dimension size = new Dimension(20*getNumTicks(), height);
		Rectangle newBounds = new Rectangle(new Point(parentObject.getBounds().x+parentObject.getBounds().width-size.width, parentObject.getBounds().y-height+4), size);
		setBounds(newBounds);
	}
	
	public void paint(Graphics g){ 
		if (visible) paintContents(g); 
	}
	
	public void paintContents(Graphics g){ 
		recalculateBounds();

		tickCounter++;
		if (tickCounter >= 20) tickCounter = 0;
		
		if (getNumTicks() < 1) {
			visible = false;
			return;
		}
		
		g.setColor(getParentObject().getBackground());
		g.fillRoundRect(getBounds().x+4, getBounds().y+4, getBounds().width-10, getBounds().height-9, height-10, height-10);
		g.setColor(Color.black);
		g.drawRoundRect(getBounds().x+4, getBounds().y+4, getBounds().width-10, getBounds().height-9, height-10, height-10);
		
		int radius = 3;
		int yOffset = height/2;
		for (int i = 0; i < getNumTicks(); i++){
			int xOffset = 10+20*i;
			if (i == getSelectedTick()){
				Color color = null;
				if (getParentObject().isFocused()){
					float ratio = ((float)tickCounter)/10f;
					if (tickCounter >= 10) ratio = (20f-((float)tickCounter))/10f;
					ratio/=2;
					color = new Color(1f, ratio, ratio);
				}
				else color = Color.GRAY;
				
				g.setColor(color);
				int x = (int)(getBounds().x+xOffset-radius*1.5);
				int y = (int)(getBounds().y+yOffset-radius*1.5);
				g.fillOval(x, y, radius*3, radius*3);

				g.setColor(Color.BLACK);
				g.drawOval(x, y, radius*3, radius*3);
			}
			else{
				g.setColor(Color.WHITE);
				int x = getBounds().x+xOffset-radius;
				int y = getBounds().y+yOffset-radius;
				g.fillOval(x, y, radius*2, radius*2);

				g.setColor(Color.BLACK);
				g.drawOval(x, y, radius*2, radius*2);
			}
		}
	}
}

package com.oblong.af.level.decorators;

import com.oblong.af.GameComponent;
import com.oblong.af.level.AreaScene;

import java.awt.*;

public abstract class LayerDecorator {

	protected Image image;
	protected Graphics2D graphics;
	protected int tickCounter = 0, width, height, xCam, yCam;

    protected LayerDecorator(){}

	public void paint(Graphics g){ g.drawImage(image, 0, 0, null); }
	public void init(int width, int height){
		this.width = width;
		this.height = height;
		xCam = yCam = 0;
		reinit();
	}

	public void setCam(int xCam, int yCam){
		this.xCam = xCam;
		this.yCam = yCam;
	}
	
	protected void reinit(){
        if (GameComponent.INSTANCE != null && GameComponent.INSTANCE.getGraphicsConfiguration() != null){
            image = GameComponent.INSTANCE.getGraphicsConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            graphics = (Graphics2D)image.getGraphics();
        }
	}
	
	public void tick(AreaScene scene){ tickCounter++; }

    public boolean isComplete(){ return false; }
}

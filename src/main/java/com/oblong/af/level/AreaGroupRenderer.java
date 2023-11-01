package com.oblong.af.level;

import com.oblong.af.level.decorators.LayerDecorator;
import com.oblong.af.util.Art;
import com.oblong.af.util.Footprint;

import java.awt.*;

public class AreaGroupRenderer{

    public static boolean renderBehaviors = false;

	private int xCam, yCam, width, height, realWidth, realHeight;
    private Image image[];
    private Graphics2D g[];
    private static final Color transparent = new Color(0, 0, 0, 0);
    private AreaGroup areaGroup;
    private GraphicsConfiguration graphicsConfiguration;
    
    public AreaGroupRenderer(AreaGroup areaGroup, GraphicsConfiguration graphicsConfiguration, int realWidth, int realHeight){
    	setGraphicsConfiguration(graphicsConfiguration);
    	this.realWidth = realWidth;
    	this.realHeight = realHeight;
    	setLevel(areaGroup, realWidth, realHeight);
    }

    public void setCam(int xCam, int yCam){
    	int xCamD = this.xCam - xCam;
        int yCamD = this.yCam - yCam;
        this.xCam = xCam;
        this.yCam = yCam;

        if (xCamD < 0)
            if (xCamD < -width) xCamD = -width;
        else if (xCamD > 0)
            if (xCamD > width) xCamD = width;
        if (yCamD < 0)
        	if (yCamD < -width) yCamD = -width;
        else if (yCamD > 0) 
        	if (yCamD > width) yCamD = width;

    	for (Area.Layer layer: Area.Layer.values()){
	        g[layer.ordinal()].setComposite(AlphaComposite.Src);
	        g[layer.ordinal()].copyArea(0, 0, width, height, xCamD, yCamD);
	
	        if (xCamD < 0) updateArea(width + xCamD, 0, -xCamD, height, layer);
	        else if (xCamD > 0) updateArea(0, 0, xCamD, height, layer);
	
	        if (yCamD < 0) updateArea(0, height + yCamD, width, -yCamD, layer);
	        else if (yCamD > 0) updateArea(0, 0, width, yCamD, layer);
    	}
    	
    	for (LayerDecorator d: areaGroup.getDecorators()) d.setCam(xCam, yCam);
    }

    private void updateArea(int x0, int y0, int w, int h, Area.Layer layer){
    	g[layer.ordinal()].setBackground(transparent);
        g[layer.ordinal()].clearRect(x0, y0, w, h);

		int startX = (x0+xCam)/16, startY = (y0+yCam)/16, endX = (x0+xCam+w)/16, endY = (y0+yCam+h)/16;
		if (startX < 0){
			endX -= startX;
			startX = 0;
		}
		if (startY < 0){
			endY -= startY;
			startY = 0;
		}
		if (endX >= areaGroup.getWidth()) endX = areaGroup.getWidth()-1;
		if (endY >= areaGroup.getHeight()) endY = areaGroup.getHeight()-1;

		Tileset tileset = areaGroup.getCurrentArea().getTileset();
		if (tileset != null && tileset.getArt() != null)
	        for (int x = startX; x <= endX; x++){
	            for (int y = startY; y <= endY; y++){
	                Block b = areaGroup.getBlock(x, y, layer);
	                if (b != null && b.blockId > AreaGroup.NULL_BLOCK){
	                	try{ //when changing tilesets, if old one is bigger than new some tile ID's will be out of range
	                		g[layer.ordinal()].drawImage(tileset.getArt()[b.blockId % 16][b.blockId / 16], x*16 - xCam, y*16 - yCam, null);
	                	}catch(ArrayIndexOutOfBoundsException e){}
	                }
	            }
	        }
    }

    public void render(Graphics g, int tick, float alpha, Area.Layer layer){
    	g.drawImage(image[layer.ordinal()], 0, 0, null);

		int startX = xCam/16, startY = yCam/16, endX = (xCam+width)/16, endY = (yCam+height)/16;
		if (startX < 0){
			endX -= startX;
			startX = 0;
		}
		if (startY < 0){
			endY -= startY;
			startY = 0;
		}
		if (endX >= areaGroup.getWidth()) endX = areaGroup.getWidth()-1;
		if (endY >= areaGroup.getHeight()) endY = areaGroup.getHeight()-1;

		for (int x = startX; x <= endX; x++)
            for (int y = startY; y <= endY; y++){
                Block b = areaGroup.getBlock(x, y, layer);

                if (b.blockId > AreaGroup.NULL_BLOCK){
                	Tileset tileset = areaGroup.getCurrentArea().getTileset();
                	if (tileset != null){
                        if (tileset.getBehavior(b.blockId).contains(Block.Trait.Animated)){
                            int animTime = (tick / 3) % 4;
                            if (animTime == 3) animTime = 1;

                            Image image = tileset.getArt()[b.blockId%16+animTime][b.blockId/16];
                            if (b.frozenCounter > 0){
                                image = tileset.getArt()[b.blockId%16][b.blockId/16]; //turn off animation
                            }

                            g.drawImage(image, (x*16) - xCam, (y*16) - yCam, null);

                            if (b.frozenCounter > 0){
                                Image iceImage = Art.effects16x16[b.frozenCounter-1][15];
                                if (areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Lava))
                                    iceImage = Art.effects16x16[b.frozenCounter-1][19]; //if lava is actually rock
                                g.drawImage(iceImage, (x*16) - xCam, (y*16) - yCam, null);
                            }
                        }
                        else if (tileset.getBehavior(b.blockId).contains(Block.Trait.Water)){
                            if (b.frozenCounter > 0){
                                Image iceImage = Art.effects16x16[b.frozenCounter-1][15];
                                if (areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Lava))
                                    iceImage = Art.effects16x16[b.frozenCounter-1][19]; //if lava is actually rock
                                g.drawImage(iceImage, (x*16) - xCam, (y*16) - yCam, null);
                            }
                        }

                        if (renderBehaviors){
	                        Footprint fp = tileset.getBlockFootprint(b.blockId).getFootprint();
	                        Footprint pfp = tileset.getProjectileBlockFootprint(b.blockId).getFootprint();
	                        if (fp != null && fp.getLength() > 0){
	                        	fp.translate(x*16-xCam, y*16-yCam);
	                        	g.setColor(new Color(1f, 0f, 0f, 0.3f));
	                        	Polygon p = fp.asPolygon();
	                        	g.fillPolygon(p);
	                        	g.setColor(Color.RED);
	                        	g.drawPolygon(p);
	                        }
	                        if (pfp != null && pfp.getLength() > 0){
	                        	pfp.translate(x*16-xCam, y*16-yCam);
	                        	g.setColor(new Color(0f, 0f, 1f, 0.3f));
	                        	Polygon p = pfp.asPolygon();
	                        	g.fillPolygon(p);
	                        	g.setColor(Color.CYAN);
	                        	g.drawPolygon(p);
	                        }
		                }
                	}
                }
            }
    }

    public void repaint(int x, int y, int w, int h, Area.Layer layer){
    	updateArea(x * 16 - xCam, y * 16 - yCam, w * 16, h * 16, layer);
    }

    public void setLevel(AreaGroup areaGroup, int maxWidth, int maxHeight){
    	//if (maxWidth == 0 || maxHeight == 0) throw new IllegalArgumentException("width and height must be > 0; maxWidth: "+maxWidth+", maxHeight: "+maxHeight);
    	
    	this.areaGroup = areaGroup;
        this.width = Math.max(Math.min(maxWidth, areaGroup.getWidth()*16), 320);
        this.height = Math.max(Math.min(maxHeight, areaGroup.getHeight()*16), 240);
        
        image = new Image[Area.Layer.values().length];
        g = new Graphics2D[Area.Layer.values().length];
        for (int i = 0; i < Area.Layer.values().length; i++){
        	image[i] = graphicsConfiguration.createCompatibleImage(Math.max(realWidth, 1024), realHeight, Transparency.TRANSLUCENT);
	        g[i] = (Graphics2D) image[i].getGraphics();
	        g[i].setComposite(AlphaComposite.Src);
        }
        for (Area.Layer layer: Area.Layer.values())
        	updateArea(0, 0, width, height, layer);
    }

    public GraphicsConfiguration getGraphicsConfiguration(){ return graphicsConfiguration; }
    public void setGraphicsConfiguration(GraphicsConfiguration gc){ this.graphicsConfiguration = gc; }
    
	public int getWidth(){ return width; }
	public void setWidth(int width){ this.width = width; }

	public int getHeight(){ return height; }
	public void setHeight(int height){ this.height = height; }
}
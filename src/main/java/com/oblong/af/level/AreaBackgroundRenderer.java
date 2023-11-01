package com.oblong.af.level;

import com.oblong.af.util.Art;

import java.awt.*;

public class AreaBackgroundRenderer{

	private static final Color transparent = new Color(0, 0, 0, 0);

	private int xCam, yCam, width, height, distance;
    private Image image;
    private Graphics2D g;
    private AreaGroup areaGroup;

    public boolean renderBehaviors = false;

    public AreaBackgroundRenderer(AreaGroup areaGroup, GraphicsConfiguration graphicsConfiguration, int width, int height, int distance){
        this.distance = distance;
        this.width = width;
        this.height = height;

        this.areaGroup = areaGroup;
        image = graphicsConfiguration.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);

        updateArea(0, 0, width, height);
    }

    public void setCam(int xCam, int yCam){
        xCam /= distance;
        yCam /= distance;
        int xCamD = this.xCam - xCam;
        int yCamD = this.yCam - yCam;
        this.xCam = xCam;
        this.yCam = yCam;

        g.setComposite(AlphaComposite.Src);
        g.copyArea(0, 0, width, height, xCamD, yCamD);

        if (xCamD < 0){
            if (xCamD < -width) xCamD = -width;
            updateArea(width + xCamD, 0, -xCamD, height);
        }
        else if (xCamD > 0){
            if (xCamD > width) xCamD = width;
            updateArea(0, 0, xCamD, height);
        }

        if (yCamD < 0){
            if (yCamD < -width) yCamD = -width;
            updateArea(0, height + yCamD, width, -yCamD);
        }
        else if (yCamD > 0){
            if (yCamD > width) yCamD = width;
            updateArea(0, 0, width, yCamD);
        }
    }

    private void updateArea(int x0, int y0, int w, int h){
        g.setBackground(transparent);
        g.clearRect(x0, y0, w, h);
        int xTileStart = (x0 + xCam) / 32;
        int yTileStart = (y0 + yCam) / 32;
        int xTileEnd = (x0 + xCam + w) / 32;
        int yTileEnd = (y0 + yCam + h) / 32;
        for (int x = xTileStart; x <= xTileEnd; x++){
            for (int y = yTileStart; y <= yTileEnd; y++){
	            for (Area.Layer z: Area.Layer.values()){
	            	if (areaGroup.getBlock(x, y, z).blockId > -1){
		                int b = areaGroup.getBlock(x, y, z).blockId & 0xff;
		                g.drawImage(Art.bg[b % 8][b / 8], (x << 5) - xCam, (y << 5) - yCam-16, null);
	            	}
	            }
            }
        }
    }

    public void render(Graphics g, int tick, float alpha){
        g.drawImage(image, 0, 0, null);
    }

    public void setLevel(AreaGroup areaGroup){
        this.areaGroup = areaGroup;
        updateArea(0, 0, width, height);
    }
}
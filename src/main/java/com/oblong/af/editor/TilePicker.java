package com.oblong.af.editor;

import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.Block;
import com.oblong.af.level.Tileset;
import com.oblong.af.util.Art;
import com.oblong.af.util.Footprint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

@SuppressWarnings("serial")
public class TilePicker extends JComponent implements MouseListener, MouseMotionListener {
	
    private int xTile, yTile;
    private LevelEditor levelEditor;
    private Rectangle selection;
    private int anchorX, anchorY;
    
    public TilePicker(LevelEditor levelEditor){
    	this.levelEditor = levelEditor;
    	
    	Dimension size = new Dimension(256, 1400);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        addMouseListener(this);
        addMouseMotionListener(this);
        
        xTile = -10;
        yTile = -10;
    }

    public void addNotify(){
        super.addNotify();
        Art.init(getGraphicsConfiguration(), null);
    }

    public void paintComponent(Graphics g1){
    	Graphics2D g = (Graphics2D)g1;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        Tileset tileset = levelEditor.getAreaEditPanel().getLevel().getCurrentArea().getTileset();
        if (tileset == null) return;
        Image[][] art = tileset.getArt();
        if (art == null) return;

        g.setColor(new Color(0x8090ff));
        g.fillRect(0, 0, art.length*16, art[0].length*16);
        
        for (int x = 0; x < art.length; x++)
            for (int y = 0; y < art[x].length; y++){
                g.drawImage(art[x][y], x*16, y*16, null);
                
                if (AreaGroupRenderer.renderBehaviors){
                    Footprint fp = tileset.getBlockFootprint(y*16+x).getFootprint();
                    Footprint pfp = tileset.getProjectileBlockFootprint(y*16+x).getFootprint();
                    if (fp != null && fp.getPoints().length > 0){
                    	fp.translate(x*16, y*16);
                    	g.setColor(new Color(1f, 0f, 0f, 0.3f));
                    	Polygon p = fp.asPolygon();
                    	g.fillPolygon(p);
                    	g.setColor(Color.RED);
                    	g.drawPolygon(p);
                    }
                    if (pfp != null && pfp.getPoints().length > 0){
                    	pfp.translate(x*16, y*16);
                    	g.setColor(new Color(0f, 0f, 1f, 0.3f));
                    	Polygon p = pfp.asPolygon();
                        g.fillPolygon(p);
                        g.setColor(Color.CYAN);
                    	g.drawPolygon(p);
                    }
                }
            }
        //highlight selection by darkening everything else
        if (selection != null){
        	g.setColor(new Color(0f, 0f, 0f, 0.3f));

        	g.fillRect(0, selection.y*16, selection.x*16, selection.height*16); //left side
        	g.fillRect(selection.x*16+selection.width*16, selection.y*16, getWidth()-(selection.x*16+selection.width*16), selection.height*16); //right side
        	g.fillRect(0, 0, getWidth(), selection.y*16); //top
        	g.fillRect(0, selection.y*16+selection.height*16, getWidth(), getHeight()-(selection.y*16+selection.height*16)); //bottom

        	g.setColor(Color.WHITE);
        	g.drawRect(selection.x*16-1, selection.y*16-1, selection.width*16+1, selection.height*16+1);
        }
        
        //draw cursor
        g.setColor(Color.BLACK);
        g.drawRect(xTile*16-1, yTile*16-1, 17, 17);
        g.setColor(Color.CYAN);
        g.drawRect(xTile*16, yTile*16, 15, 15);
    }

    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){
    	anchorX = -1;
    	anchorY = -1;
    	xTile = -10;
    	yTile = -10;
    	repaint();
    }

    public void mousePressed(MouseEvent e){
        xTile = e.getX() / 16;
        yTile = e.getY() / 16;

    	anchorX = xTile;
    	anchorY = yTile;
    	selection = new Rectangle(xTile, yTile, 1, 1);
        repaint();
    }

    private int blockAt(int x, int y){ return x+y*16; }
    private Block[][] selectionToBlocks(){
    	if (selection == null) return null;
    	Block[][] ret = new Block[selection.width][selection.height];
    	for (int x = 0; x < ret.length; x++)
    		for (int y = 0; y < ret[x].length; y++)
    			ret[x][y] = new Block(blockAt(selection.x+x, selection.y+y));
    	return ret;
    }
    
    public void mouseReleased(MouseEvent e){
    	levelEditor.setClipboard(selectionToBlocks());
    	anchorX = -1;
    	anchorY = -1;
    	
    	Toolbox.setSelectedTool(Toolbox.paste);
    	levelEditor.getAreaEditPanel().setDrawWidth(levelEditor.getClipboard().length);
    	levelEditor.getAreaEditPanel().setDrawHeight(levelEditor.getClipboard()[0].length);
    }
    
    public void mouseDragged(MouseEvent e){
        xTile = e.getX() / 16;
        yTile = e.getY() / 16;

    	int dxTile = e.getX() / 16, dyTile = e.getY() / 16;
    	
    	if (selection == null) selection = new Rectangle(xTile, yTile, 1, 1);
    	int nsx = selection.x, nsy = selection.y, nsw = selection.width, nsh = selection.height;
    	if (dxTile > anchorX){
    		nsw = dxTile-anchorX+1;
    		nsx = anchorX;
    	}
    	else if (dxTile < anchorX){
    		nsw = anchorX-dxTile+1;
    		nsx = dxTile;
    	}
    	else nsw = 1;
    	if (dyTile > anchorY){
    		nsh = dyTile-anchorY+1;
    		nsy = anchorY;
    	}
    	else if (dyTile < anchorY){
    		nsh = anchorY-dyTile+1;
    		nsy = dyTile;
    	}
    	else nsh = 1;

    	selection = new Rectangle(nsx, nsy, nsw, nsh);
    	
        repaint();
    }

    public void mouseMoved(MouseEvent e){
        xTile = e.getX() / 16;
        yTile = e.getY() / 16;
        repaint();
    }

	public Rectangle getSelection(){ return selection; }
	public void setSelection(Rectangle selection){ 
		this.selection = selection; 
		repaint();
	}
	public void clearSelection(){ 
		selection = null; 
		repaint();
	}

}
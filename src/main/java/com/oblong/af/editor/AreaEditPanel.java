package com.oblong.af.editor;

import com.oblong.af.editor.painter.PainterFactory;
import com.oblong.af.level.*;
import com.oblong.af.models.ActorMessages;
import com.oblong.af.models.Facing;
import com.oblong.af.models.Messages;
import com.oblong.af.models.SpriteDefinitions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;

public class AreaEditPanel extends JComponent implements MouseListener, MouseMotionListener, ComponentListener, ChangeListener {
    private static final long serialVersionUID = -7696446733303717142L;

    private AreaGroupRenderer areaGroupRenderer;
    private AreaGroup areaGroup;
    private LevelEditor levelEditor;
    private int xTile = -1, yTile = -1;
    private Area.Layer zTile;
    private int drawWidth = 1, drawHeight = 1;
    private int anchorX, anchorY;
    private boolean viewAllLayers;
    private Rectangle selection;
    private GraphicsConfiguration gc;
    
    public AreaEditPanel(LevelEditor levelEditor, GraphicsConfiguration gc){
        this.levelEditor = levelEditor;
        this.gc = gc;

        areaGroup = new AreaGroup("Default", (short)32, (short)32);
        try{ areaGroup.getCurrentArea().setTileset(Tileset.getTileset(getGraphicsConfiguration())); }
        catch(IOException e){
        	System.err.println("Couldn't load default tileset.");
        	e.printStackTrace();
        }
        Dimension size = new Dimension(areaGroup.getWidth() * 16, areaGroup.getHeight() * 16);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
        Toolbox.addChangeListener(this);
        
        areaGroupRenderer = new AreaGroupRenderer(areaGroup, gc, 
        		(int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
        AreaGroupRenderer.renderBehaviors = true;
    }
    
    public int getDrawWidth(){ return drawWidth; }
	public void setDrawWidth(int drawWidth){ this.drawWidth = drawWidth; }

    public int getDrawHeight(){ return drawHeight; }
	public void setDrawHeight(int drawHeight){ this.drawHeight = drawHeight; }

	public Area.Layer getDrawLevel(){ return zTile; }
	public void setDrawLevel(Area.Layer zTile){ this.zTile = zTile; }
	
	public boolean isViewAllLayers(){ return viewAllLayers; }
	public void setViewAllLayers(boolean viewAllLayers){ 
		this.viewAllLayers = viewAllLayers; 
		repaint();
	}

	public void setLevel(AreaGroup areaGroup){
        this.areaGroup = areaGroup;
        Dimension size = new Dimension(areaGroup.getWidth() * 16, areaGroup.getHeight() * 16);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        repaint();
        areaGroupRenderer.setLevel(areaGroup, getWidth(), getHeight());
        componentResized(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

    }
    
    public AreaGroup getLevel(){ return areaGroup; }

    public void paintComponent(Graphics g1){
    	Graphics2D g = (Graphics2D)g1;
    	
    	//background
    	g.setColor(Color.BLACK);
        g.fillRect(0, 0, areaGroup.getWidth() * 16, areaGroup.getHeight() * 16);

        //draw by layer
   		for (Area.Layer layer: Area.Layer.values()){
   			if (isViewAllLayers() || zTile == layer){
   				if (areaGroup.getCurrentArea().getTileset() != null) areaGroupRenderer.render(g, 0, 0, layer);

   				for (int i = 0; i < areaGroup.getWidth(); i++){
		        	for (int j = 0; j < areaGroup.getHeight(); j++){
	        			if (isViewAllLayers() || layer == zTile){
	        				//sprite
			        		if (areaGroup.getSpriteTemplate(i, j, layer) != null){
				        		if (areaGroup.getSpriteTemplate(i, j, layer).getSprite() == null)
				        			areaGroup.getSpriteTemplate(i, j, layer).spawn(null, i, j, Facing.DOWN, true);
				        		areaGroup.getSpriteTemplate(i, j, layer).getSprite().render(g, 1f);
			        		}

			        		//markers
			        		if (AreaGroupRenderer.renderBehaviors){
			        			for (Marker m: areaGroup.getMarkers(i, j, layer)){
                                    m.render(g, 1f);
			        			}
			        		}
	        			}
	        		}
	        	}
   			}
   		}
   		
        //selected tile
        int dw = getDrawWidth(), dh = getDrawHeight();
        if (Toolbox.getSelectedTool() == Toolbox.fill){
        	dw = 1;
        	dh = 1;
        }
        else if (Toolbox.getSelectedTool() == Toolbox.paste){
        	dw = levelEditor.getClipboard().length;
        	dh = levelEditor.getClipboard()[0].length;
        }
        g.setColor(new Color(1f, 1f, 1f, 0.2f));
        g.fillRect((xTile-dw/2)*16, (yTile-dh/2)*16, 16*dw-1, 16*dh-1);
        g.setColor(Color.CYAN);
        g.drawRect((xTile-dw/2)*16, (yTile-dh/2)*16, 16*dw-1, 16*dh-1);
        g.setColor(Color.BLACK);
        g.drawRect((xTile-dw/2)*16-1, (yTile-dh/2)*16-1, 16*dw+1, 16*dh+1);

        //highlight selection
        if (selection != null){
        	g.setColor(Color.WHITE);
        	g.drawRect(selection.x*16, selection.y*16, selection.width*16-1, selection.height*16-1);
        }
    }

    public void stateChanged(ChangeEvent e){
    	Toolbox.Tool tool = (Toolbox.Tool)e.getSource();
        if (tool == Toolbox.draw3px){
            drawWidth = 3;
            drawHeight = 3;
        }
        else if (tool == Toolbox.paste){
            if (levelEditor.getClipboard() == null) return;
            drawWidth = levelEditor.getClipboard().length;
            drawHeight = levelEditor.getClipboard()[0].length;
        }
        else{
            try{ levelEditor.getTilePicker().clearSelection(); }
            catch(Exception ignored){} //happens during startup
            drawWidth = 1;
            drawHeight = 1;
        }
    }
    
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){
        xTile = -10;
        yTile = -10;
    	anchorX = -1;
    	anchorY = -1;
        repaint();
    }

    private void fill(int x, int y, Area.Layer z, int idToFill, int idToFillWith){
    	if (idToFill == idToFillWith) return;
    	if (x < 0 || y < 0 || x >= areaGroup.getWidth() || y >= areaGroup.getHeight()) return;
    	if (areaGroup.getBlock(x, y, z).blockId != idToFill) return;
    	
    	areaGroup.setBlock(x, y, z, idToFillWith);
		fill(x-1, y, z, idToFill, idToFillWith);
		fill(x+1, y, z, idToFill, idToFillWith);
		fill(x, y-1, z, idToFill, idToFillWith);
		fill(x, y+1, z, idToFill, idToFillWith);
    }
    
    public void mousePressed(MouseEvent e){
        xTile = e.getX() / 16;
        yTile = e.getY() / 16;

   		int dw = drawWidth, dh = drawHeight;
   		Toolbox.Tool selectedTool = Toolbox.getSelectedTool();
   		
        if (e.getButton() == 3){
        	Block block = areaGroup.getBlock(xTile, yTile, zTile);
            levelEditor.setClipboard(new Block[][]{{ block }} );
            if (block.blockId == AreaGroup.NULL_BLOCK){
            	Toolbox.setSelectedTool(Toolbox.erase);
            	levelEditor.getTilePicker().setSelection(null);
            }
            else{
            	levelEditor.getTilePicker().setSelection(new Rectangle(block.blockId%16, block.blockId/16, 1, 1));
            	Toolbox.setSelectedTool(Toolbox.draw1px);
            }
            levelEditor.getTilePicker().repaint();
        }
        else if (selectedTool != null){
            if (selectedTool.getSpriteDefinition() != null){
                placeActor(selectedTool.getSpriteDefinition(), xTile, yTile);
            }
            else if (selectedTool == Toolbox.fill){
                if (levelEditor.getClipboard() == null) return;
                fill(xTile, yTile, zTile, areaGroup.getBlock(xTile,yTile, zTile).blockId, levelEditor.getClipboard()[0][0].blockId);
                areaGroupRenderer.repaint(0, 0, areaGroup.getWidth(), areaGroup.getHeight(), zTile);
            }
            else if (selectedTool == Toolbox.draw1px || selectedTool == Toolbox.draw3px){
                if (levelEditor.getClipboard() == null) return;
                for (int x = xTile-dw/2; x < xTile-dw/2+dw; x++)
                    for (int y = yTile-dh/2; y < yTile-dh/2+dh; y++)
                        areaGroup.setBlock(x, y, zTile, levelEditor.getClipboard()[0][0].blockId);
                areaGroupRenderer.repaint(xTile-dw/2 - 1, yTile-dh/2 - 1, dw+1, dh+1, zTile);
            }
            else if (selectedTool == Toolbox.erase){
                if (levelEditor.getClipboard() == null) return;
                for (int x = xTile-dw/2; x < xTile-dw/2+dw; x++)
                    for (int y = yTile-dh/2; y < yTile-dh/2+dh; y++)
                        areaGroup.setBlock(x, y, zTile, AreaGroup.NULL_BLOCK);
                areaGroupRenderer.repaint(xTile-dw/2 - 1, yTile-dh/2 - 1, dw+1, dh+1, zTile);
            }
            else if (selectedTool == Toolbox.select){
                anchorX = xTile;
                anchorY = yTile;
                selection = new Rectangle(xTile, yTile, 1, 1);
            }
            else if (selectedTool == Toolbox.paste){
                dw = drawWidth = levelEditor.getClipboard().length;
                dh = drawHeight = levelEditor.getClipboard()[0].length;
                for (int x = 0; x < dw; x++)
                    for (int y = 0; y < dh; y++)
                        areaGroup.setBlock(xTile-dw/2+x, yTile-dh/2+y, zTile, levelEditor.getClipboard()[x][y].blockId);
                areaGroupRenderer.repaint(xTile-dw/2 - 1, yTile-dh/2 - 1, dw+2, dh+2, zTile);
            }
            else if (selectedTool == Toolbox.editMessage){
                editMessage(xTile, yTile);
            }
            else if (selectedTool == Toolbox.editMarkerMessage){
                editMarkerMessage(xTile, yTile, zTile);
            }
            else if (selectedTool == Toolbox.eraseActor){
                areaGroup.setSpriteTemplate(xTile, yTile, zTile, null);
                repaint();
            }
            else if (selectedTool == Toolbox.eraseMarker){
                for (Marker m: new Vector<Marker>(areaGroup.getMarkers(xTile, yTile, zTile)))
                    areaGroup.getCurrentArea().removeMarker(m);
            }
            else if (selectedTool.getMarkerType() == Marker.Type.StartPosition){
                areaGroup.setStartPos(xTile, yTile, zTile);
            }
            else if (selectedTool.getMarkerType() != null){
                addMarker(selectedTool.getMarkerType(), xTile, yTile, zTile);
            }
            else if (selectedTool == Toolbox.paintGrass) drawPainter(areaGroup, xTile, yTile, PainterFactory.getGrassPainter());
            else if (selectedTool == Toolbox.fillGrass) fillPainter(areaGroup, xTile, yTile, PainterFactory.getGrassPainter());
            else if (selectedTool == Toolbox.paintSand) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandPainter());
            else if (selectedTool == Toolbox.fillSand) fillPainter(areaGroup, xTile, yTile, PainterFactory.getSandPainter());
            else if (selectedTool == Toolbox.paintCave) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCavePainter());
            else if (selectedTool == Toolbox.fillCave) fillPainter(areaGroup, xTile, yTile, PainterFactory.getCavePainter());
            else if (selectedTool == Toolbox.paintCheckerboard) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCheckerboardPainter());
            else if (selectedTool == Toolbox.fillCheckerboard) fillPainter(areaGroup, xTile, yTile, PainterFactory.getCheckerboardPainter());
            else if (selectedTool == Toolbox.paintRift) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftPainter());
            else if (selectedTool == Toolbox.fillRift) fillPainter(areaGroup, xTile, yTile, PainterFactory.getRiftPainter());
            else if (selectedTool == Toolbox.paintIceCave) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCavePainter());
            else if (selectedTool == Toolbox.fillIceCave) fillPainter(areaGroup, xTile, yTile, PainterFactory.getIceCavePainter());
            else if (selectedTool == Toolbox.paintDirtOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDirtOverlayPainter());
            else if (selectedTool == Toolbox.paintSandOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandOverlayPainter());
            else if (selectedTool == Toolbox.paintCobblestoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCobblestoneOverlayPainter());
            else if (selectedTool == Toolbox.paintSnowOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSnowOverlayPainter());
            else if (selectedTool == Toolbox.paintDarkStoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDarkStoneOverlayPainter());
            else if (selectedTool == Toolbox.paintLightStoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLightStoneOverlayPainter());
            else if (selectedTool == Toolbox.paintWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getWaterOverlayPainter());
            else if (selectedTool == Toolbox.paintLavaOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLavaOverlayPainter());
            else if (selectedTool == Toolbox.paintCaveWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveWaterOverlayPainter());
            else if (selectedTool == Toolbox.paintCaveHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveHoleOverlayPainter());
            else if (selectedTool == Toolbox.paintRiftHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftHoleOverlayPainter());
            else if (selectedTool == Toolbox.paintIceCaveWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveWaterOverlayPainter());
            else if (selectedTool == Toolbox.paintIceCaveHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveHoleOverlayPainter());
            else if (selectedTool == Toolbox.paintDarkStoneEdged) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDarkStoneEdgedPainter());
            else if (selectedTool == Toolbox.paintLightStoneEdged) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLightStoneEdgedPainter());
            else if (selectedTool == Toolbox.paintWoodenFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getWoodenFencePainter());
            else if (selectedTool == Toolbox.paintCircuitFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCircuitFencePainter());
            else if (selectedTool == Toolbox.paintStoneFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getStoneFencePainter());
            else if (selectedTool == Toolbox.paintGrassyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getGrassyRockWallPainter());
            else if (selectedTool == Toolbox.paintDirtyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDirtyRockWallPainter());
            else if (selectedTool == Toolbox.paintSandyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandyRockWallPainter());
            else if (selectedTool == Toolbox.paintSnowyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSnowyRockWallPainter());
            else if (selectedTool == Toolbox.paintCaveWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveWallPainter());
            else if (selectedTool == Toolbox.paintCryptWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCryptWallPainter());
            else if (selectedTool == Toolbox.paintCircuitWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCircuitWallPainter());
            else if (selectedTool == Toolbox.paintRiftWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftWallPainter());
            else if (selectedTool == Toolbox.paintIceCaveWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveWallPainter());
        }
        repaint();
    }

    private void drawPainter(AreaGroup areaGroup, int xTile, int yTile, com.oblong.af.editor.painter.Painter p){
        p.paint(areaGroup, xTile,  yTile);
        for (Area.Layer layer: Area.Layer.values())
            areaGroupRenderer.repaint(0, 0, areaGroup.getWidth(), areaGroup.getHeight(), layer);
    }

    private void fillPainter(AreaGroup areaGroup, int xTile, int yTile, com.oblong.af.editor.painter.Painter p){
        p.fill(areaGroup, xTile,  yTile);
        for (Area.Layer layer: Area.Layer.values())
            areaGroupRenderer.repaint(0, 0, areaGroup.getWidth(), areaGroup.getHeight(), layer);
    }

    private void editMessage(int xTile, int yTile){
        SpriteTemplate st = levelEditor.getAreaGroup().getCurrentArea().getSpriteTemplate(xTile, yTile, Area.Layer.Main);
        if (st != null){
            EditMessagesDialog emd = new EditMessagesDialog(null, st);
            emd.setVisible(true);
        }
    }

    private void addMarker(Marker.Type type, int xTile, int yTile, Area.Layer zTile){
        Marker marker = new Marker("", areaGroup.getId(), xTile, yTile, zTile, 16, 16, type, new Messages(), 0);
        areaGroup.addMarker(marker);
        editMarkerMessage(xTile, yTile, zTile);
    }

    private void editMarkerMessage(int xTile, int yTile, Area.Layer zTile){
        for (Marker m: levelEditor.getAreaGroup().getCurrentArea().getMarkers(xTile, yTile, zTile)){
            MarkerDialog md = new MarkerDialog(null, m);
            md.setVisible(true);
        }
    }

    private void placeActor(SpriteDefinitions actorDef, int xTile, int yTile){
        SpriteTemplate spriteTemplate = new SpriteTemplate(actorDef, Area.Layer.Main, false, false, new Messages(), new ActorMessages());
        spriteTemplate.spawn(null, xTile, yTile, Facing.DOWN, true);
        levelEditor.getAreaGroup().getCurrentArea().setSpriteTemplate(xTile, yTile, Area.Layer.Main, spriteTemplate);
    }

    private int blockAt(int x, int y){ return areaGroup.getBlock(x, y, zTile).blockId; }
    private Block[][] selectionToBlocks(){
    	if (selection == null) return null;
    	Block[][] ret = new Block[selection.width][selection.height];
    	for (int x = 0; x < ret.length; x++)
    		for (int y = 0; y < ret[x].length; y++)
    			ret[x][y] = new Block(blockAt(selection.x+x, selection.y+y));
    	return ret;
    }

    public void mouseReleased(MouseEvent e){
    	if (Toolbox.getSelectedTool() == Toolbox.select){
    		levelEditor.setClipboard(selectionToBlocks());
    		selection = null;
	    	anchorX = -1;
	    	anchorY = -1;
	    	Toolbox.setSelectedTool(Toolbox.paste);
    	}
    }

    public void mouseDragged(MouseEvent e){
    	if (Toolbox.getSelectedTool() == Toolbox.fill)
    		return;
    	
    	xTile = e.getX() / 16;
        yTile = e.getY() / 16;

        if (Toolbox.getSelectedTool() == Toolbox.draw1px ||
        		Toolbox.getSelectedTool() == Toolbox.draw3px){
			int dw = drawWidth;
			for (int x = xTile-dw/2; x < xTile-dw/2+dw; x++)
	        	for (int y = yTile-dw/2; y < yTile-dw/2+dw; y++)
	        		areaGroup.setBlock(x, y, zTile, levelEditor.getClipboard()[0][0].blockId);
	   		areaGroupRenderer.repaint(xTile-dw/2 - 1, yTile-dw/2 - 1, dw+2, dw+2, zTile);
        }
        else if (Toolbox.getSelectedTool() == Toolbox.paintGrass) drawPainter(areaGroup, xTile, yTile, PainterFactory.getGrassPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintSand) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCave) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCavePainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCheckerboard) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCheckerboardPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintRift) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintIceCave) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCavePainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintDirtOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDirtOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintSandOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCobblestoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCobblestoneOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintSnowOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSnowOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintDarkStoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDarkStoneOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintLightStoneOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLightStoneOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getWaterOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintLavaOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLavaOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCaveWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveWaterOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCaveHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveHoleOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintRiftHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftHoleOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintIceCaveWaterOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveWaterOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintIceCaveHoleOverlay) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveHoleOverlayPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintDarkStoneEdged) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDarkStoneEdgedPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintLightStoneEdged) drawPainter(areaGroup, xTile, yTile, PainterFactory.getLightStoneEdgedPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintWoodenFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getWoodenFencePainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCircuitFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCircuitFencePainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintStoneFence) drawPainter(areaGroup, xTile, yTile, PainterFactory.getStoneFencePainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintGrassyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getGrassyRockWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintDirtyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getDirtyRockWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintSandyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSandyRockWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintSnowyRockWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getSnowyRockWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCaveWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCaveWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCryptWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCryptWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintCircuitWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getCircuitWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintRiftWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getRiftWallPainter());
        else if (Toolbox.getSelectedTool() == Toolbox.paintIceCaveWall) drawPainter(areaGroup, xTile, yTile, PainterFactory.getIceCaveWallPainter());

        else if (Toolbox.getSelectedTool() == Toolbox.erase){
            int dw = drawWidth, dh = drawHeight;
            if (levelEditor.getClipboard() == null) return;
            for (int x = xTile-dw/2; x < xTile-dw/2+dw; x++)
                for (int y = yTile-dh/2; y < yTile-dh/2+dh; y++)
                    areaGroup.setBlock(x, y, zTile, AreaGroup.NULL_BLOCK);
            areaGroupRenderer.repaint(xTile-dw/2 - 1, yTile-dh/2 - 1, dw+1, dh+1, zTile);
        }
        else if (Toolbox.getSelectedTool() == Toolbox.eraseMarker){
        	for (Marker m: new Vector<Marker>(areaGroup.getMarkers(xTile, yTile, zTile)))
        		areaGroup.getCurrentArea().removeMarker(m); 
        }
        else if (Toolbox.getSelectedTool() == Toolbox.paste){
        	int dw = drawWidth = levelEditor.getClipboard().length;
        	int dh = drawHeight = levelEditor.getClipboard()[0].length;
			for (int x = 0; x < dw; x++)
            	for (int y = 0; y < dh; y++)
            		areaGroup.setBlock(xTile-dw/2+x, yTile-dh/2+y, zTile, levelEditor.getClipboard()[x][y].blockId);
       		areaGroupRenderer.repaint(xTile - dw / 2 - 1, yTile - dh / 2 - 1, dw + 2, dh + 2, zTile);
        }
        else if (Toolbox.getSelectedTool() == Toolbox.select){
	        xTile = e.getX() / 16;
	        yTile = e.getY() / 16;

	        if (selection == null) selection = new Rectangle(xTile, yTile, 1, 1);
	    	int nsx = selection.x, nsy = selection.y, nsw, nsh;
	    	if (xTile > anchorX){
	    		nsw = xTile-anchorX+1;
	    		nsx = anchorX;
	    	}
	    	else if (xTile < anchorX){
	    		nsw = anchorX-xTile+1;
	    		nsx = xTile;
	    	}
	    	else nsw = 1;
	    	if (yTile > anchorY){
	    		nsh = yTile-anchorY+1;
	    		nsy = anchorY;
	    	}
	    	else if (yTile < anchorY){
	    		nsh = anchorY-yTile+1;
	    		nsy = yTile;
	    	}
	    	else nsh = 1;
	
	    	selection = new Rectangle(nsx, nsy, nsw, nsh);
	    	
	        repaint();
        }
        
        repaint();
    }

    public void mouseMoved(MouseEvent e){
        xTile = e.getX() / 16;
        yTile = e.getY() / 16;
        repaint();
    }
    
    public void componentMoved(ComponentEvent e){}
    public void componentHidden(ComponentEvent e){}
    public void componentShown(ComponentEvent e){}
    public void componentResized(ComponentEvent e){
        areaGroupRenderer = new AreaGroupRenderer(areaGroup, gc, 
        		(int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
    }
}

package com.oblong.af.editor;

import com.oblong.af.level.Marker;
import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.util.Art;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Toolbox {
	
    public static enum ToolGroup {
        Draw(null),
        Paint(null),
        Exits(null),
        LogicGates(null),
        Enemies(null),
        Rescuables(null),
        NPCs(null),
        Bosses(null),
        Trigger(null),
        Barricade(null),
        Object(null),
        LargeObject(null),
        Powerup(null),
        Ability(null),
        ;

        private List<Tool> tools;

        private ToolGroup(List<Tool> tools){ this.tools = tools; }

        public List<Tool> getTools(){
            if (tools == null){
                tools = new ArrayList<Tool>();
                for (SpriteDefinitions def: SpriteDefinitions.values())
                    if (def.getToolGroup() == this)
                        tools.add(new Tool(def, this));
            }
            return tools;
        }
    }
    
    public static class Tool {
        private String toolName;
        private int imageX, imageY;
        private ToolGroup group;
        private Image[][] sheet;
        private SpriteDefinitions spriteDefinition;
        private Marker.Type markerType;

        public Tool(String toolName, int imageX, int imageY, ToolGroup group){
            this.toolName = toolName;
            this.imageX = imageX;
            this.imageY = imageY;
            this.group = group;
            this.sheet = Art.editorIcons;
        }

        public Tool(Marker.Type markerType, int xPic, int yPic, ToolGroup group){
            this.toolName = markerType.name();
            this.markerType = markerType;
            this.imageX = xPic;
            this.imageY = yPic;
            this.group = group;
            this.sheet = Art.editorIcons;
        }

        public Tool(SpriteDefinitions actorDef, ToolGroup group){
            this.toolName = actorDef.name();
            this.spriteDefinition = actorDef;
            this.imageX = actorDef.getXPic();
            this.imageY = actorDef.getYPic();
            this.group = group;
            this.sheet = actorDef.getSheet();
        }

        public String getName(){ return toolName; }
        public int getImageX(){ return imageX; }
    	public int getImageY(){ return imageY; }
        public ToolGroup getGroup(){ return group; }
        public Image[][] getSheet(){ return sheet; }
        public SpriteDefinitions getSpriteDefinition(){ return spriteDefinition; }
        public Marker.Type getMarkerType(){ return markerType; }
    }

    //Tile-based Tools
    public static final Tool draw1px = new Tool("Draw1px", 1, 0, ToolGroup.Draw);
    public static final Tool draw3px = new Tool("Draw3px", 1, 1, ToolGroup.Draw);
    public static final Tool erase = new Tool("Erase", 1, 2, null);
    public static final Tool fill = new Tool("Fill", 1, 3, ToolGroup.Draw);
    public static final Tool select = new Tool("Select", 1, 5, null);
    public static final Tool paste = new Tool("Paste", 1, 6, null);

    //painters
    public static final Tool paintGrass = new Tool("Grass Painter", 13, 0, ToolGroup.Paint);
    public static final Tool fillGrass = new Tool("Grass Filler", 14, 0, ToolGroup.Paint);
    public static final Tool paintSand = new Tool("Sand Painter", 13, 1, ToolGroup.Paint);
    public static final Tool fillSand = new Tool("Sand Filler", 14, 1, ToolGroup.Paint);
    public static final Tool paintCave = new Tool("Cave Painter", 15, 0, ToolGroup.Paint);
    public static final Tool fillCave = new Tool("Cave Filler", 16, 0, ToolGroup.Paint);
    public static final Tool paintCheckerboard = new Tool("Checkerboard Painter", 13, 2, ToolGroup.Paint);
    public static final Tool fillCheckerboard = new Tool("Checkerboard Filler", 14, 2, ToolGroup.Paint);
    public static final Tool paintRift = new Tool("Rift Painter", 12, 6, ToolGroup.Paint);
    public static final Tool fillRift = new Tool("Rift Filler", 12, 7, ToolGroup.Paint);
    public static final Tool paintIceCave = new Tool("Ice Cave Painter", 12, 4, ToolGroup.Paint);
    public static final Tool fillIceCave = new Tool("Ice Cave Filler", 12, 5, ToolGroup.Paint);
    public static final Tool paintDirtOverlay = new Tool("Dirt Painter", 13, 3, ToolGroup.Paint);
    public static final Tool paintSandOverlay = new Tool("Sand Painter", 13, 4, ToolGroup.Paint);
    public static final Tool paintCobblestoneOverlay = new Tool("Cobblestone Painter", 13, 5, ToolGroup.Paint);
    public static final Tool paintSnowOverlay = new Tool("Snow Painter", 13, 6, ToolGroup.Paint);
    public static final Tool paintDarkStoneOverlay = new Tool("Dark Stone Painter", 13, 7, ToolGroup.Paint);
    public static final Tool paintLightStoneOverlay = new Tool("Light Stone Painter", 14, 3, ToolGroup.Paint);
    public static final Tool paintWaterOverlay = new Tool("Water Painter", 14, 4, ToolGroup.Paint);
    public static final Tool paintLavaOverlay = new Tool("Lava Painter", 14, 5, ToolGroup.Paint);
    public static final Tool paintCaveWaterOverlay = new Tool("Cave Water Painter", 15, 1, ToolGroup.Paint);
    public static final Tool paintCaveHoleOverlay = new Tool("Cave Hole Painter", 15, 2, ToolGroup.Paint);
    public static final Tool paintRiftHoleOverlay = new Tool("Rift Hole Painter", 15, 6, ToolGroup.Paint);
    public static final Tool paintIceCaveWaterOverlay = new Tool("Ice Cave Water Painter", 11, 5, ToolGroup.Paint);
    public static final Tool paintIceCaveHoleOverlay = new Tool("Ice Cave Hole Painter", 9, 7, ToolGroup.Paint);
    public static final Tool paintDarkStoneEdged = new Tool("Edged Dark Stone Painter", 14, 6, ToolGroup.Paint);
    public static final Tool paintLightStoneEdged = new Tool("Edged Light Stone Painter", 14, 7, ToolGroup.Paint);
    public static final Tool paintWoodenFence = new Tool("Wooden Fence Painter", 15, 3, ToolGroup.Paint);
    public static final Tool paintCircuitFence = new Tool("Circuit Fence Painter", 15, 5, ToolGroup.Paint);
    public static final Tool paintStoneFence = new Tool("Stone Fence Painter", 15, 4, ToolGroup.Paint);
    public static final Tool paintGrassyRockWall = new Tool("Grassy Rock Wall Painter", 16, 1, ToolGroup.Paint);
    public static final Tool paintDirtyRockWall = new Tool("Dirty Rock Wall Painter", 16, 2, ToolGroup.Paint);
    public static final Tool paintSandyRockWall = new Tool("Sandy Rock Wall Painter", 16, 3, ToolGroup.Paint);
    public static final Tool paintSnowyRockWall = new Tool("Snowy Rock Wall Painter", 16, 4, ToolGroup.Paint);
    public static final Tool paintCaveWall = new Tool("Cave Wall Painter", 16, 5, ToolGroup.Paint);
    public static final Tool paintCryptWall = new Tool("Crypt Wall Painter", 16, 6, ToolGroup.Paint);
    public static final Tool paintCircuitWall = new Tool("Circuit Wall Painter", 16, 7, ToolGroup.Paint);
    public static final Tool paintRiftWall = new Tool("Rift Wall Painter", 15, 7, ToolGroup.Paint);
    public static final Tool paintIceCaveWall = new Tool("Ice Cave Wall Painter", 11, 6, ToolGroup.Paint);

    //Actor Tools
    public static final Tool editMessage = new Tool("EditMessage", 2, 6, null);
    public static final Tool eraseActor = new Tool("EraseActor", 2, 7, null);

    //Marker-based Tools
    public static final Tool editMarkerMessage = new Tool("EditMarkerMessage", 0, 5, null);
    public static final Tool eraseMarker = new Tool("EraseMarker", 0, 4, null);

    static{
        ToolGroup.Draw.tools = new ArrayList<Tool>(Arrays.asList(new Tool[]{ draw1px, draw3px, fill }));

        ToolGroup.Paint.tools = new ArrayList<Tool>(Arrays.asList(new Tool[]{
                paintGrass, fillGrass, paintGrassyRockWall,
                paintSand, fillSand, paintSandOverlay, paintSandyRockWall,
                paintCave, fillCave, paintCaveWaterOverlay, paintCaveHoleOverlay, paintCaveWall,
                paintIceCave, fillIceCave, paintIceCaveWaterOverlay, paintIceCaveHoleOverlay, paintIceCaveWall,
                paintRift, fillRift, paintRiftHoleOverlay, paintRiftWall,
                paintDirtOverlay, paintDirtyRockWall,
                paintSnowOverlay, paintSnowyRockWall,
                paintCheckerboard, fillCheckerboard, paintCryptWall,
                paintCobblestoneOverlay,
                paintDarkStoneEdged, paintDarkStoneOverlay,
                paintLightStoneEdged, paintLightStoneOverlay,
                paintWaterOverlay,  paintLavaOverlay,
                paintWoodenFence, paintStoneFence,
                paintCircuitFence, paintCircuitWall
        }));

        ToolGroup.Exits.tools = new ArrayList<Tool>(Arrays.asList(new Tool[]{
                new Tool(Marker.Type.StartPosition, 0, 0, ToolGroup.Exits),
                new Tool(Marker.Type.Entrance, 0, 1, ToolGroup.Exits),
                new Tool(Marker.Type.Exit, 0, 2, ToolGroup.Exits),
                new Tool(Marker.Type.TwoWay, 0, 3, ToolGroup.Exits),
                new Tool(Marker.Type.ReturnExit, 0, 6, ToolGroup.Exits),
                new Tool(Marker.Type.LairExit, 0, 7, ToolGroup.Exits),
        }));

        ToolGroup.LogicGates.tools = new ArrayList<Tool>(Arrays.asList(new Tool[]{
                new Tool(Marker.Type.AndGate, 10, 0, ToolGroup.LogicGates),
                new Tool(Marker.Type.OrGate, 10, 1, ToolGroup.LogicGates),
                new Tool(Marker.Type.XorGate, 10, 2, ToolGroup.LogicGates),
                new Tool(Marker.Type.NotGate, 10, 3, ToolGroup.LogicGates),
                new Tool(Marker.Type.DelayGate, 10, 4, ToolGroup.LogicGates),
                new Tool(Marker.Type.TriggerGate, 10, 5, ToolGroup.LogicGates),
                new Tool(Marker.Type.Minibossify, 2, 5, ToolGroup.LogicGates),
                new Tool(Marker.Type.WeatherTrigger, 10, 6, ToolGroup.LogicGates),
        }));
    }

    private static Tool selectedTool;
    private static ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    public static void removeChangeListener(ChangeListener listener){ listeners.remove(listener); }
    public static void addChangeListener(ChangeListener listener){ 
    	removeChangeListener(listener);
    	listeners.add(listener); 
    }

    private static void fireChangeEvent(ChangeEvent e){
    	for (ChangeListener listener: listeners) listener.stateChanged(e);
    }

	public static Tool getSelectedTool(){ return selectedTool; }
	public static void setSelectedTool(Tool selectedTool){ 
		Toolbox.selectedTool = selectedTool;
		fireChangeEvent(new ChangeEvent(selectedTool));
	}

    public static List<JComponent> createToolButtons(ArrayList<Tool> tools){
        List<JComponent> toolButtons = new ArrayList<JComponent>();
        for (Toolbox.Tool tool: tools){
            Image iconImage = tool.getSheet()[tool.getImageX()][tool.getImageY()];
            JToggleButton button = new JToggleButton(new ImageIcon(iconImage));
            button.setActionCommand(tool.getName());
            final Toolbox.Tool ftool = tool;
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Toolbox.setSelectedTool(ftool);
                }
            });
            final JToggleButton fbutton = button;
            addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e){
                    Toolbox.Tool selectedTool = (Toolbox.Tool)e.getSource();
                    fbutton.setSelected(fbutton.getActionCommand().equals(selectedTool.getName()));
                }
            });
            button.setFocusable(false);
            button.setPreferredSize(new Dimension(32, 32));
            button.setToolTipText(tool.getName());
            toolButtons.add(button);
        }
		
        return toolButtons;
	}
}

package com.oblong.af.editor;

import com.oblong.af.level.Block;
import com.oblong.af.level.Tileset;
import com.oblong.af.util.Art;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class TilesetEditor extends JPanel implements ItemListener, ChangeListener {

    private JCheckBox[] bitmapCheckboxes = new JCheckBox[Block.Trait.values().length];
    private JToggleButton[] brButtons = new JToggleButton[Block.BlockFootprint.values().length];
    private JToggleButton[] pbrButtons = new JToggleButton[Block.BlockFootprint.values().length];
    private TilePicker tilePicker;
    private LevelEditor levelEditor;

    public TilesetEditor(final LevelEditor levelEditor, GraphicsConfiguration gc){
    	super(new BorderLayout());
    	this.levelEditor = levelEditor;

    	//tile picker
    	tilePicker = new TilePicker(levelEditor);
        JScrollPane tilePickerJsp = new JScrollPane(tilePicker);
        tilePickerJsp.getViewport().setPreferredSize(new Dimension(256, 300));

        //buttons - traits, then block rects
        Box buttonBox = Box.createVerticalBox();
        int i = 0;
        for (i = 0; i < Block.Trait.values().length; i++){
        	Block.Trait trait = Block.Trait.values()[i];
        	bitmapCheckboxes[i] = new JCheckBox(trait.name());
            buttonBox.add(bitmapCheckboxes[i]);
            
            bitmapCheckboxes[i].addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                	Block[][] selection = levelEditor.getClipboard();
                	Tileset tileset = levelEditor.getAreaEditPanel().getLevel().getCurrentArea().getTileset();
                	for (int x = 0; x < selection.length; x++)
                		for (int y = 0; y < selection[x].length; y++){
	                	if (((JCheckBox)e.getSource()).isSelected())
	                		tileset.getBehavior(selection[x][y].blockId).add(Block.Trait.valueOf(e.getActionCommand()));
	                	else tileset.getBehavior(selection[x][y].blockId).remove(Block.Trait.valueOf(e.getActionCommand()));
                	}

                	try { Tileset.saveTileset(); }
			        catch(IOException exc){ exc.printStackTrace(); }
                }
            });
        }
        buttonBox.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel brBox = new JPanel(new GridLayout(5, 4, 2, 2));
        ButtonGroup buttonGroup = new ButtonGroup();
        for (i = 0; i < Block.BlockFootprint.values().length; i++){
        	Block.BlockFootprint br = Block.BlockFootprint.values()[i];
        	JToggleButton button = new JToggleButton(new ImageIcon(Art.editorIcons[br.getIconX()][br.getIconY()]));
        	button.setActionCommand(br.name());
        	button.setPreferredSize(new Dimension(32, 32));
        	brBox.add(button);
			if (i == 2) brBox.add(new JLabel()); //spacer
			buttonGroup.add(button);
        	
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                	Block[][] selection = levelEditor.getClipboard();
                	if (selection == null) return;
                	
                	Tileset tileset = levelEditor.getAreaEditPanel().getLevel().getCurrentArea().getTileset();
                	for (int x = 0; x < selection.length; x++)
                		for (int y = 0; y < selection[x].length; y++)
	                		tileset.setBlockFootprint(selection[x][y].blockId, Block.BlockFootprint.valueOf(e.getActionCommand()));

                	try { Tileset.saveTileset(); }
			        catch(IOException exc){ exc.printStackTrace(); }
			        
			        levelEditor.getAreaEditPanel().repaint();
			        tilePicker.repaint();
                }
            });
            brButtons[i] = button;
        }
        brBox.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel pbrBox = new JPanel(new GridLayout(5, 4, 2, 2));
        ButtonGroup pbuttonGroup = new ButtonGroup();
        for (i = 0; i < Block.BlockFootprint.values().length; i++){
        	Block.BlockFootprint pbr = Block.BlockFootprint.values()[i];
        	JToggleButton button = new JToggleButton(new ImageIcon(Art.editorIcons[pbr.getIconX()+3][pbr.getIconY()]));
        	button.setActionCommand(pbr.name());
        	button.setPreferredSize(new Dimension(32, 32));
        	pbrBox.add(button);
			if (i == 2) pbrBox.add(new JLabel()); //spacer
			pbuttonGroup.add(button);
        	
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                	Block[][] selection = levelEditor.getClipboard();
                	if (selection == null) return;
                	
                	Tileset tileset = levelEditor.getAreaEditPanel().getLevel().getCurrentArea().getTileset();
                	for (int x = 0; x < selection.length; x++)
                		for (int y = 0; y < selection[x].length; y++)
	                		tileset.setProjectileBlockFootprint(selection[x][y].blockId, Block.BlockFootprint.valueOf(e.getActionCommand()));

                	try { Tileset.saveTileset(); }
			        catch(IOException exc){ exc.printStackTrace(); }
			        
			        levelEditor.getAreaEditPanel().repaint();
			        tilePicker.repaint();
                }
            });
            pbrButtons[i] = button;
        }
        pbrBox.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel sPanel = new JPanel();
        sPanel.add(buttonBox);
        sPanel.add(brBox);
        sPanel.add(pbrBox);
        
        buttonBox.setPreferredSize(new Dimension(160, 152));
        brBox.setPreferredSize(new Dimension(160, 160));
        pbrBox.setPreferredSize(new Dimension(160, 160));
        sPanel.setPreferredSize(new Dimension(160, 1000));
        
        add(tilePickerJsp, BorderLayout.WEST);
        add(sPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Tiles"));
        
        levelEditor.addChangeListener(this);
        Toolbox.setSelectedTool(Toolbox.draw1px);
    }

    public TilePicker getTilePicker(){ return tilePicker; }

    public void itemStateChanged(ItemEvent e){
    	if (e.getStateChange() == ItemEvent.DESELECTED) return;
    	String tilesetId = e.getItem().toString();
    	try{
	    	Tileset tileset = Tileset.getTileset(getGraphicsConfiguration());
	    	levelEditor.getAreaEditPanel().getLevel().getCurrentArea().setTileset(tileset);
	    	levelEditor.getAreaEditPanel().setLevel(levelEditor.getAreaEditPanel().getLevel());
	    	tilePicker.repaint();
    	}
    	catch(IOException exc){
    		System.err.println("Error loading tileset "+tilesetId);
    		exc.printStackTrace();
    	}
    }
    
    public void stateChanged(ChangeEvent e){
    	Block[][] selection = levelEditor.getClipboard();
    	if (selection == null){
	    	for (JCheckBox box: bitmapCheckboxes){
	    		box.setEnabled(false);
	    		box.setSelected(false);
	    	}
	    	for (JToggleButton button: brButtons){
	    		button.setEnabled(false);
	    		button.setSelected(false);
	    	}
    	}
    	else{
          	Tileset tileset = levelEditor.getAreaEditPanel().getLevel().getCurrentArea().getTileset();
          	if (tileset != null){
		    	if (selection.length == 1 && selection[0].length == 1){
		    		ArrayList<Block.Trait> traits = tileset.getBehavior(selection[0][0].blockId);
		    		Block.BlockFootprint br = tileset.getBlockFootprint(selection[0][0].blockId);
		    		Block.BlockFootprint pbr = tileset.getProjectileBlockFootprint(selection[0][0].blockId);
			    	for (JCheckBox box: bitmapCheckboxes)
			    		box.setSelected(traits.contains(Block.Trait.valueOf(box.getActionCommand())));
			    	for (JToggleButton button: brButtons)
		    			button.setSelected(br.name().equals(button.getActionCommand()));
			    	for (JToggleButton button: pbrButtons)
		    			button.setSelected(pbr.name().equals(button.getActionCommand()));
		    	}
		    	else{
			    	for (JCheckBox box: bitmapCheckboxes){
			    		boolean select = tileset.getBehavior(selection[0][0].blockId).contains(Block.Trait.valueOf(box.getActionCommand()));
			    		for (int x = 0; x < selection.length; x++)
			    			for (int y = 0; y < selection[x].length; y++)
			    				select &= tileset.getBehavior(selection[x][y].blockId).contains(Block.Trait.valueOf(box.getActionCommand()));
			    		box.setSelected(select);
			    	}
	
		    		boolean allSame = true;
		    		Block.BlockFootprint testBr = tileset.getBlockFootprint(selection[0][0].blockId);
		    		for (int x = 0; x < selection.length; x++)
		    			for (int y = 0; y < selection[x].length; y++)
		    				allSame &= testBr == tileset.getBlockFootprint(selection[x][y].blockId);
		    		if (allSame) 
		    			for (JToggleButton button: brButtons) 
		    				if (button.getActionCommand().equals(testBr.name())) 
		    					button.setSelected(true);

		    		allSame = true;
		    		Block.BlockFootprint testPbr = tileset.getProjectileBlockFootprint(selection[0][0].blockId);
		    		for (int x = 0; x < selection.length; x++)
		    			for (int y = 0; y < selection[x].length; y++)
		    				allSame &= testPbr == tileset.getProjectileBlockFootprint(selection[x][y].blockId);
		    		if (allSame) 
		    			for (JToggleButton button: pbrButtons) 
		    				if (button.getActionCommand().equals(testPbr.name())) 
		    					button.setSelected(true);
		    	}
          	}
    	}
    }
}

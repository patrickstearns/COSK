package com.oblong.af.editor;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.util.Art;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AreaPicker extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = -8268555862580930231L;
	private JButton addArea, removeArea, editArea, weather, saveButton;
    private PlayButtonPicker playButton;
    private JComboBox<Area> areaCombo;
    private JComboBox<Area.Layer> layerCombo;
    private JCheckBox viewAllLayersCheckbox, viewBehaviorsCheckbox;
    private LevelEditor levelEditor;
    private NewAreaDialog newAreaDialog;
    private WeatherDialog weatherDialog;

    @SuppressWarnings("serial")
	public AreaPicker(final LevelEditor levelEditor){
    	this.levelEditor = levelEditor;
    	newAreaDialog = new NewAreaDialog(levelEditor.getParentFrame());
    	weatherDialog = new WeatherDialog(levelEditor.getParentFrame());

        playButton = new PlayButtonPicker(levelEditor, SpriteDefinitions.getPlayerCharacterDefs());

        saveButton = new JButton(new ImageIcon(Art.editorIcons[9][2]));
        saveButton.setPreferredSize(new Dimension(32, 32));
        saveButton.addActionListener(this);

        areaCombo = new JComboBox<Area>();
        areaCombo.setEditable(false);
        areaCombo.addItemListener(this);
        areaCombo.setPreferredSize(new Dimension(100, areaCombo.getPreferredSize().height));
        areaCombo.setRenderer(new DefaultListCellRenderer(){
        	public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focused){
        		JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, selected, focused);
        		if (value != null) ret.setText(" "+((Area)value).getId());
        		else ret.setText(" ---");
        		if (selected){
	        		if (levelEditor.getAreaEditPanel().getLevel().getStartAreaIndex() == index) ret.setForeground(Color.CYAN);
	        		else ret.setForeground(Color.WHITE);
        		}
        		else{
	        		if (levelEditor.getAreaEditPanel().getLevel().getStartAreaIndex() == index) ret.setForeground(Color.BLUE.darker());
	        		else ret.setForeground(Color.BLACK);
        		}
        		return ret;
        	}
        });

        List<Area.Layer> layers = Arrays.asList(Area.Layer.values());
        Collections.reverse(layers);
		layerCombo = new JComboBox<Area.Layer>(new DefaultComboBoxModel<Area.Layer>(layers.toArray(new Area.Layer[4])));
        layerCombo.setSelectedIndex(0);
        layerCombo.setEditable(false);
        layerCombo.addItemListener(this);
        layerCombo.setRenderer(new DefaultListCellRenderer(){
        	public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focused){
        		JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, selected, focused);
        		if (value != null) ret.setText(((Area.Layer)value).name());
        		return ret;
        	}
        });
        
        viewAllLayersCheckbox = new JCheckBox("View All", false);
        viewAllLayersCheckbox.addActionListener(this);

        viewBehaviorsCheckbox = new JCheckBox("View Behaviors", AreaGroupRenderer.renderBehaviors);
        viewBehaviorsCheckbox.addActionListener(this);

        addArea = new JButton(new ImageIcon(Art.editorIcons[2][0]));
        addArea.setPreferredSize(new Dimension(32, 32));
        addArea.addActionListener(this);

        removeArea = new JButton(new ImageIcon(Art.editorIcons[2][1]));
        removeArea.setPreferredSize(new Dimension(32, 32));
        removeArea.addActionListener(this);

        editArea = new JButton(new ImageIcon(Art.editorIcons[2][2]));
        editArea.setPreferredSize(new Dimension(32, 32));
        editArea.addActionListener(this);

        weather = new JButton(new ImageIcon(Art.editorIcons[2][3]));
        weather.setPreferredSize(new Dimension(32, 32));
        weather.addActionListener(this);

        JPanel areaControlsBox = new JPanel(new FlowLayout(FlowLayout.LEADING));
        areaControlsBox.add(saveButton);
        areaControlsBox.add(playButton);
        areaControlsBox.add(weather);
        areaControlsBox.add(addArea);
        areaControlsBox.add(removeArea);
        areaControlsBox.add(editArea);
        areaControlsBox.add(new JLabel(" Area: "));
        areaControlsBox.add(areaCombo);
        areaControlsBox.add(new JLabel(" Layer: "));
        areaControlsBox.add(layerCombo);
        areaControlsBox.add(viewAllLayersCheckbox);
        areaControlsBox.add(viewBehaviorsCheckbox);

        JPanel tileButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JComponent button: Toolbox.createToolButtons(new ArrayList<Toolbox.Tool>(Arrays.asList(new Toolbox.Tool[]{
                Toolbox.erase, Toolbox.select, Toolbox.paste,
        }))))
            tileButtonsPanel.add(button);
        tileButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Paint.getTools()));
        tileButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Draw.getTools()));

        JPanel markerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JComponent button: Toolbox.createToolButtons(new ArrayList<Toolbox.Tool>(Arrays.asList(new Toolbox.Tool[]{
                Toolbox.editMarkerMessage, Toolbox.eraseMarker,
        }))))
            markerButtonsPanel.add(button);
        markerButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Exits.getTools()));
        markerButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.LogicGates.getTools()));

        JPanel propButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        propButtonsPanel.add(tileButtonsPanel);
        propButtonsPanel.add(markerButtonsPanel);
        for (JComponent button: Toolbox.createToolButtons(new ArrayList<Toolbox.Tool>(Arrays.asList(new Toolbox.Tool[]{
                Toolbox.editMessage,Toolbox.eraseActor,
        }))))
            propButtonsPanel.add(button);
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Enemies.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Bosses.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Rescuables.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.NPCs.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Trigger.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Object.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.LargeObject.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Barricade.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Powerup.getTools()));
        propButtonsPanel.add(new SpritePicker(Toolbox.ToolGroup.Ability.getTools()));

        setLayout(new BorderLayout());
        add(areaControlsBox, BorderLayout.NORTH);
        add(propButtonsPanel, BorderLayout.CENTER);

        newAreaDialog = new NewAreaDialog(levelEditor.getParentFrame());
        
        setPreferredSize(new Dimension(1000, 128));

        layerCombo.setSelectedItem(Area.Layer.Main);
    }

    public void itemStateChanged(ItemEvent e){
    	if (e.getStateChange() == ItemEvent.DESELECTED) return;
    	
    	if (e.getSource() == areaCombo){
	    	if (areaCombo.getSelectedIndex() < 0) return;
	    	levelEditor.getAreaEditPanel().getLevel().setCurrentArea((Area)areaCombo.getSelectedItem());
	    	levelEditor.getAreaEditPanel().setLevel(levelEditor.getAreaEditPanel().getLevel());
    	}
    	else if (e.getSource() == layerCombo){
    		Area.Layer layer = (Area.Layer)e.getItem();
    		levelEditor.getAreaEditPanel().setDrawLevel(layer);
    		levelEditor.getAreaEditPanel().repaint();
    	}
    }

    public void actionPerformed(ActionEvent e){
        try{
            if (e.getSource() == saveButton) save();
            else if (e.getSource() == addArea) addArea();
	        else if (e.getSource() == removeArea) removeArea();
	        else if (e.getSource() == editArea) editArea();
	        else if (e.getSource() == weather) weather();
	        else if (e.getSource() == viewAllLayersCheckbox) levelEditor.getAreaEditPanel().setViewAllLayers(viewAllLayersCheckbox.isSelected());
	        else if (e.getSource() == viewBehaviorsCheckbox){
	        	AreaGroupRenderer.renderBehaviors = viewBehaviorsCheckbox.isSelected();
	        	levelEditor.getAreaEditPanel().repaint();
	        	levelEditor.getTilePicker().repaint();
	        }
        }
        catch (Exception ex){
        	ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.toString(), "Operation failed.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save(){
        levelEditor.saveLevel(false);
    }

    private void addArea(){
    	Area area = showNewAreaDialog(getTopLevelAncestor(), false);
    	if (area == null) return;
    	levelEditor.getAreaEditPanel().getLevel().addArea(area);
    	refreshAreaList();
    }
    
    private void removeArea(){
    	if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(this, "You sure you want to delete this area?")) return;
    	levelEditor.getAreaEditPanel().getLevel().removeArea(levelEditor.getAreaEditPanel().getLevel().getCurrentArea());
    	refreshAreaList();
    }
    
    private void editArea(){
    	showNewAreaDialog(getTopLevelAncestor(), true);
    	refreshAreaList();
    }
    
    private void weather(){
    	weatherDialog.setLevelArea(levelEditor.getAreaEditPanel().getLevel().getCurrentArea());
    	weatherDialog.setVisible(true);
    	if (!weatherDialog.isCancelled()) weatherDialog.updateLevelArea(levelEditor.getAreaEditPanel().getLevel().getCurrentArea());
    }
    
    public void refreshAreaList(){
    	AreaGroup areaGroup = levelEditor.getAreaEditPanel().getLevel();
    	
    	DefaultComboBoxModel<Area> model = new DefaultComboBoxModel<Area>();
    	for (Area area: areaGroup.getAreas()) model.addElement(area);
    	areaCombo.setModel(model);
    	areaCombo.setSelectedItem(areaGroup.getCurrentArea());
    }
    
    private Area showNewAreaDialog(Component c, boolean forEditing){
    	newAreaDialog.setTitle("New Area");
    	if (forEditing){
    		newAreaDialog.setTitle("Edit Area");
    		newAreaDialog.setLevelArea(levelEditor.getAreaEditPanel().getLevel().getCurrentArea());
    	}
    	else newAreaDialog.clear();
    	newAreaDialog.setVisible(true);
    	if (newAreaDialog.isCancelled()) return null;
    	else if (forEditing){
    		newAreaDialog.updateLevelArea(levelEditor.getAreaEditPanel().getLevel().getCurrentArea());
    		return null;
    	}
    	else return newAreaDialog.createLevelArea();
    }
}

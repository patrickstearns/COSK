package com.oblong.af.editor;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;
import com.oblong.af.level.Block;
import com.oblong.af.level.Marker;
import com.oblong.af.models.Facing;
import com.oblong.af.util.Art;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class LevelEditor extends JInternalFrame implements ActionListener {
    private static final long serialVersionUID = 7461321112832160393L;

    public static enum FirstTimeOpenBehavior { NEW, LOAD, PRELOAD };
    
    private JFrame frame;
    private AreaEditPanel levelEditView;
    private TilesetEditor tilesetEditor;
    private AreaPicker areaPicker;
    private Block[][] clipboard;
    private AreaGroup areaGroup;
    private EventListenerList changeListeners;
	private JMenuItem saveLevel, saveLevelAs;
    private JFileChooser fileChooser;
    private NewAreaDialog newAreaDialog;
	private JMenuBar menuBar;
	private boolean firstTimeOpen = true;
    private FirstTimeOpenBehavior ftoBehavior;
    private String preloadLevelFilename;
	
    @SuppressWarnings("serial")
	public LevelEditor(GraphicsConfiguration gc){
        super("Map Edit", true, true, false, true);

        changeListeners = new EventListenerList();
        
        setSize(1200, 800);
        setLocation(20, 20);
        setResizable(true);

        //components
        levelEditView = new AreaEditPanel(this, gc);
        tilesetEditor = new TilesetEditor(this, gc);
        areaPicker = new AreaPicker(this);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tilesetEditor, BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(areaPicker, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(levelEditView), BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Area"));
        
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(leftPanel, BorderLayout.WEST);
        borderPanel.add(rightPanel, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(borderPanel);
        
		fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./src/main/resources"));

        menuBar = createMenuBar();
		setJMenuBar(menuBar);
    }
    
    public void setVisible(boolean vis){
    	super.setVisible(vis);
    	if (vis && firstTimeOpen){
    		firstTimeOpen = false;
    		if (ftoBehavior == FirstTimeOpenBehavior.NEW) newLevel();
    		else if (ftoBehavior == FirstTimeOpenBehavior.LOAD) loadLevel();
            else if (ftoBehavior == FirstTimeOpenBehavior.PRELOAD){
                loadLevelFilename(preloadLevelFilename);
                fileChooser.setSelectedFile(new File(preloadLevelFilename));
            }
        }
    }
    
    public final JFrame getParentFrame(){ return frame; }
    public void setFirstTimeOpenBehavior(FirstTimeOpenBehavior ftoBehavior){ this.ftoBehavior = ftoBehavior; }
    
    public AreaEditPanel getAreaEditPanel(){ return levelEditView; }
    public TilePicker getTilePicker(){ return tilesetEditor.getTilePicker(); }
    public TilesetEditor getTilesetEditor(){ return tilesetEditor; }
    
	public Block[][] getClipboard(){ return clipboard; }
	public void setClipboard(Block[][] clipboard){ 
		if (this.clipboard == clipboard) return;
		this.clipboard = clipboard; 
		fireChangeEvent();
	}
    
	public void removeChangeListener(ChangeListener listener){ changeListeners.remove(ChangeListener.class, listener); }
	public void addChangeListener(ChangeListener listener){ 
		removeChangeListener(listener);
		changeListeners.add(ChangeListener.class, listener);
	}
	private void fireChangeEvent(){
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener: changeListeners.getListeners(ChangeListener.class)) listener.stateChanged(event);
	}

	public AreaGroup getAreaGroup(){ return areaGroup; }
    public void setLevel(AreaGroup areaGroup){
    	this.areaGroup = areaGroup;
    	levelEditView.setLevel(areaGroup);
        areaPicker.refreshAreaList();

        for (int i = 0; i < areaGroup.getWidth(); i++)
        	for (int j = 0; j < areaGroup.getHeight(); j++)
	        	for (Area.Layer layer: Area.Layer.values())
	        		if (areaGroup.getSpriteTemplate(i, j, layer) != null)
	        			areaGroup.getSpriteTemplate(i, j, layer).spawn(null, i, j, Facing.DOWN, true);
        levelEditView.repaint();
        tilesetEditor.getTilePicker().repaint();
    }
	
    public ArrayList<String> validateLevel(){
    	ArrayList<String> errors = new ArrayList<String>();
    	
    	//there must be a start marker and every transport marker has exactly one exit
        for (Area area: areaGroup.getAreas())
            for (int i = 0; i < area.getWidth(); i++)
                for (int j = 0; j < area.getHeight(); j++)
                    for (Marker m: area.getMarkers(i, j)){
                        if (m.getType() == Marker.Type.Entrance || m.getType() == Marker.Type.Exit || m.getType() == Marker.Type.TwoWay)
                            if (areaGroup.findEntranceForExit(m) == null)
                                errors.add("Marker "+m.getId()+" in area "+area.getId()+" at "+i+","+j+" has no match.");
                    }
    	
    	if (areaGroup.findStartPos() == null) errors.add("No start position set - one must be.");

        for (Area area: areaGroup.getAreas())
            if (area.getMusicKey() == null)
                errors.add("Area "+area.getId()+" doesn't have a music key; one must be set.");
    	return errors;
    }
    
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		saveLevel = new JMenuItem("Save Area Group");
		saveLevelAs = new JMenuItem("Save Area Group As...");
		saveLevel.addActionListener(this);
		saveLevelAs.addActionListener(this);

		saveLevel.setIcon(new ImageIcon(Art.editorIcons[9][2]));
		saveLevelAs.setIcon(new ImageIcon(Art.editorIcons[9][3]));
		
		fileMenu.addSeparator();
		fileMenu.add(saveLevel);
		fileMenu.add(saveLevelAs);
		menuBar.add(fileMenu);
		
		return menuBar;
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == saveLevel) saveLevel(false);
		else if (e.getSource() == saveLevelAs) saveLevel(true);
	}

	private void newLevel(){
    	AreaGroup areaGroup = new AreaGroup();
    	Area defaultArea = showNewLevelDialog(this);
    	if (defaultArea == null){
            setVisible(false);
            return;
        }
    	
    	areaGroup.addArea(defaultArea);
    	areaGroup.setCurrentArea(defaultArea);

    	setLevel(areaGroup);
	}
	
	private void loadLevel(){
    	if (JFileChooser.CANCEL_OPTION == fileChooser.showOpenDialog(this)){
            setVisible(false);
            return;
        }
    	try{
    		File file = fileChooser.getSelectedFile();
	    	AreaGroup level = AreaGroup.load(getGraphicsConfiguration(),
	        		new DataInputStream(new FileInputStream(file)), true, file);
	    	setLevel(level);
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(this, "Exception loading level.", "Error", JOptionPane.ERROR_MESSAGE);
            setVisible(false);
        }
	}

    private void loadLevelFilename(String filename){
        try{
            File file = new File(filename);
            AreaGroup level = AreaGroup.load(getGraphicsConfiguration(),
                    new DataInputStream(new FileInputStream(file)), true, file);
            setLevel(level);
        }
        catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Exception loading level.", "Error", JOptionPane.ERROR_MESSAGE);
            setVisible(false);
        }
    }

	public void saveLevel(boolean saveAs){
		ArrayList<String> errors = validateLevel();
    	if (errors.size() == 0){
		    if (fileChooser.getSelectedFile() == null || saveAs){
	    		if (JFileChooser.CANCEL_OPTION == fileChooser.showSaveDialog(this)) return;
	    	}
		    try{
		    	File file = fileChooser.getSelectedFile();
		    	getAreaGroup().save(new DataOutputStream(new FileOutputStream(file)), file);
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    	JOptionPane.showMessageDialog(this, "Exception saving level.", "Error", JOptionPane.ERROR_MESSAGE);
		    }
    	}
    	else {
    		String errorList = "Errors were found and must be corrected:\n";
    		for (String s: errors) errorList += s+"\n";
    		JTextArea messageArea = new JTextArea(errorList);
    		JScrollPane ms = new JScrollPane(messageArea);
    		messageArea.setEditable(false);
    		JOptionPane.showMessageDialog(this, ms, "Validation Failed", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
	}

    private Area showNewLevelDialog(Component c){
		newAreaDialog = new NewAreaDialog(frame);
    	newAreaDialog.clear();
    	newAreaDialog.setTitle("New Level");
    	newAreaDialog.setVisible(true);
    	if (newAreaDialog.isCancelled()) return null;
    	else return newAreaDialog.createLevelArea();
    }

    public String getPreloadLevelFilename(){ return preloadLevelFilename; }
    public void setPreloadLevelFilename(String preloadLevelFilename){ this.preloadLevelFilename = preloadLevelFilename; }

}
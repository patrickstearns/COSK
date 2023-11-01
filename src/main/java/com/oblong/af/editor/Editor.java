package com.oblong.af.editor;

import com.oblong.af.models.World;
import com.oblong.af.util.Art;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class Editor extends JDesktopPane {

	public static void main(String[] args){ new Editor().setVisible(true); }
	
	public Editor(){
		JFrame frame = new JFrame("Editor");

        Art.init(frame.getGraphicsConfiguration(), null);
        World.init();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.setJMenuBar(createMenubar());
        frame.setSize(1250, 900);
        frame.setLocation(50, 50);
        frame.setVisible(true);
	}

	private JMenuBar createMenubar(){
		JMenuBar menubar = new JMenuBar();

		JMenuItem newAreaGroup = new JMenuItem("New Area Group");
		newAreaGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ newAreaGroup(); }
        });
        newAreaGroup.setIcon(new ImageIcon(Art.editorIcons[9][0]));

        JMenuItem loadAreaGroup = new JMenuItem("Load Area Group");
        loadAreaGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ loadAreaGroup(); }
        });
		loadAreaGroup.setIcon(new ImageIcon(Art.editorIcons[9][1]));

		JMenu windowMenu = new JMenu("Window");
		windowMenu.add(newAreaGroup);
		windowMenu.add(loadAreaGroup);
        windowMenu.addSeparator();
        windowMenu.add(createLoadMenuItem("Village", World.EDITOR_RESOURCES+World.LEVELS+World.HUB_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Volcanic Jungle", World.EDITOR_RESOURCES+World.LEVELS+World.JUNGLE_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Frozen Glacier", World.EDITOR_RESOURCES+World.LEVELS+World.GLACIER_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Windscoured Desert", World.EDITOR_RESOURCES+World.LEVELS+World.DESERT_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Quaking Caverns", World.EDITOR_RESOURCES+World.LEVELS+World.CAVERNS_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Spirit Crypts", World.EDITOR_RESOURCES+World.LEVELS+World.CRYPTS_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Stone King's Lair", World.EDITOR_RESOURCES+World.LEVELS+World.LAIR_AREA_GROUP_ID));
        windowMenu.add(createLoadMenuItem("Dimensional Rift", World.EDITOR_RESOURCES+World.LEVELS+World.RIFT_AREA_GROUP_ID));

        menubar.add(windowMenu);
		
		return menubar;
	}

    private JMenuItem createLoadMenuItem(String name, final String filename){
        JMenuItem loadAreaGroup = new JMenuItem(name);
        loadAreaGroup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ loadArea(filename); }
        });
        return loadAreaGroup;
    }

	private void newAreaGroup(){
		LevelEditor editor = new LevelEditor(getGraphicsConfiguration());
		add(editor);
		editor.setFirstTimeOpenBehavior(LevelEditor.FirstTimeOpenBehavior.NEW);
		editor.setVisible(true);
	}

	private void loadAreaGroup(){
		LevelEditor editor = new LevelEditor(getGraphicsConfiguration());
		add(editor);
		editor.setFirstTimeOpenBehavior(LevelEditor.FirstTimeOpenBehavior.LOAD);
		editor.setVisible(true);
	}

    private void loadArea(String filename){
        LevelEditor editor = new LevelEditor(getGraphicsConfiguration());
        add(editor);
        editor.setPreloadLevelFilename(filename);
        editor.setFirstTimeOpenBehavior(LevelEditor.FirstTimeOpenBehavior.PRELOAD);
        editor.setVisible(true);
    }
}

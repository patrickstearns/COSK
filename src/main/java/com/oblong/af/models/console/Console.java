package com.oblong.af.models.console;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.World;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.sprite.Prop;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

public class Console {

	public static final Font STANDARD_FONT = new Font("Lucida", Font.PLAIN, 10);
	public static final Color BACKGROUND = new Color(0f, 0f, 0f, 0.8f);
    public static final String SAVE_GAMES_DIR = "/cosk/saves";

	protected TreeSet<ConsoleObject> consoleObjects;
	protected ConsoleMenu focusedMenu;
	protected Color background;
	protected AreaScene scene;
	
	public Console(AreaScene scene) {
		this.scene = scene;
		consoleObjects = new TreeSet<ConsoleObject>(ConsoleObject.Z_ORDER_COMPARATOR);
		setFocusedMenu(null);
        setBackground(BACKGROUND);
	}

	public void add(ConsoleObject object) { 
		if (object == null) throw new NullPointerException("no adding null objects!");	
		if (consoleObjects.contains(object)) consoleObjects.remove(object);
		consoleObjects.add(object);
	}

	public void remove(ConsoleObject object) { 
		consoleObjects.remove(object); 
		if (object == focusedMenu){
            ConsoleMenu parentMenu = focusedMenu.getParentMenu();
            setFocusedMenu(parentMenu);
            if (parentMenu == null) getScene().actionPaused = false;
        }
	}

	public void openMenu(ConsoleMenu menu){
		add(menu);
		setFocusedMenu(menu);
        getScene().actionPaused = true;
	}

    public void showTalkMenu(Prop speaking, ConversationNode startingNode){
        if (startingNode.getText() != null)
            openMenu(TalkMenu.createTalkMenu(this, speaking, startingNode));
        else startingNode.takeEffect(scene, speaking, null);
    }

    public void showLoadFileMenu(Prop speaking){
        openMenu(FileMenu.createLoadMenu(this, speaking, new File(SAVE_GAMES_DIR)));
    }

    public void showSaveFileMenu(Prop speaking){
        openMenu(FileMenu.createSaveMenu(this, speaking, new File(SAVE_GAMES_DIR)));
    }

    public final AreaScene getScene(){ return scene; }

	public Color getBackground(){ return background; }
    public void setBackground(Color background){ this.background = background; }
	
	public final TreeSet<ConsoleObject> getConsoleElements() { return consoleObjects; }

	public final ConsoleMenu getFocusedMenu() { return focusedMenu; }
	public void setFocusedMenu(ConsoleMenu focusedMenu) {
		if (this.focusedMenu == focusedMenu) return;
		ConsoleMenu old = this.focusedMenu;
		this.focusedMenu = focusedMenu;
		if (old != null) old.setFocused(false);
		if (focusedMenu != null) focusedMenu.setFocused(true);
	}

	public void paint(Graphics2D g) {
		if (g == null) return;
		g.setFont(Console.STANDARD_FONT);
        for (ConsoleObject celement: new ArrayList<ConsoleObject>(consoleObjects)) celement.paint(g);
	}
	
	public void tick(){ for (ConsoleObject celement: new ArrayList<ConsoleObject>(consoleObjects)) celement.tick(); }
    public void mouseMoved(Point p){ if (focusedMenu != null) focusedMenu.mouseMoved(p); }
    public void mouseClicked(Point p){ if (focusedMenu != null) focusedMenu.mouseClicked(p); }

	public void select(){ if (focusedMenu != null) focusedMenu.select(); }
	public void cancel(){ 
		if (focusedMenu != null) focusedMenu.cancel(); 
		else scene.closeSubscreen();
	}

}
package com.oblong.af.models.console;

import com.oblong.af.LoadGameScene;
import com.oblong.af.sprite.Prop;

import java.io.File;

public class LoadGameConsole extends Console{

    protected LoadGameScene scene;

    public LoadGameConsole(LoadGameScene scene) {
        super(null);
        this.scene = scene;
    }

    public void add(ConsoleObject object) {
        if (object == null) throw new NullPointerException("no adding null objects!");
        if (consoleObjects.contains(object)) consoleObjects.remove(object);
        consoleObjects.add(object);
    }

    public void remove(ConsoleObject object) {
        if (object instanceof FileMenu){
            if (((FileMenu)object).isError()) loadError();
            else if (((FileMenu)object).getSelectedItem() == null) loadCancelled();
            else loaded();
        }
        else if (object instanceof TalkMenu){
            showLoadFileMenu(null);
        }
        consoleObjects.remove(object);
        if (object == focusedMenu){
            ConsoleMenu parentMenu = focusedMenu.getParentMenu();
            setFocusedMenu(parentMenu);
        }
    }

    public void openMenu(ConsoleMenu menu){
        add(menu);
        setFocusedMenu(menu);
    }

    public void showLoadFileMenu(Prop speaking){
        openMenu(FileMenu.createLoadMenu(this, speaking, new File(SAVE_GAMES_DIR)));
    }

    public void showSaveFileMenu(Prop speaking){
        openMenu(FileMenu.createSaveMenu(this, speaking, new File(SAVE_GAMES_DIR)));
    }

    public void loaded(){
        scene.ok();
    }

    public void loadCancelled(){
        scene.cancel();
    }

    public void loadError(){
        scene.error();
    }

    public void cancel(){
        if (focusedMenu != null){
            focusedMenu.cancel();
            remove(focusedMenu);
        }
        else scene.cancel();
    }

}
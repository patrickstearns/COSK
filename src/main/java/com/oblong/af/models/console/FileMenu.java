package com.oblong.af.models.console;

import com.oblong.af.GameComponent;
import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.models.conversation.TalkNode;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileMenu extends ConsoleMenu<FileMenu.FileMenuItem> {

    private static final Rectangle FILE_ITEM_SIZE = new Rectangle(300, 18);
    private static final Point LOCATION = new Point(10, 10);
    private static final Point MENU_ITEMS_LOCATION = new Point(14, 34);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mma dd MMM yyyy");

    public static class FileMenuItem {
        public int index;
        public File file;
        public FileMenuItem(int index, File file){
            this.index = index;
            this.file = file;
        }
    }

    private static class FileConsoleMenuItemRenderer extends ConsoleMenuItemRenderer<FileMenuItem> {
        protected FileConsoleMenuItemRenderer(){ super(FILE_ITEM_SIZE); }

        public void renderItem(Graphics2D g, FileMenuItem item, ConsoleMenu menu, int index, Point location, boolean enabled, boolean focused) {
            //should be exactly 38 chars long
            String label = "";

            if (item.index == 0){
                label += "  Cancel";
            }
            else{
                if (index < 10) label += " ";
                label += index+" ";
                if (item.file.exists()){
                    label += "   ";
                    label += DATE_FORMAT.format(new Date(item.file.lastModified()));

                    SpriteDefinitions playerDef = GameComponent.INSTANCE.playerForGame(item.file);
                    if (playerDef != null){
                        Image portraitImage = Art.portraits[playerDef.getPortraitXPic()][playerDef.getPortraitYPic()];
                        g.drawImage(portraitImage, location.x+34, location.y-8, 16, 16, null);
                    }
                }
                else { label += "No File"; }
            }

            int color = 7;
            if (!enabled) color = 3;
            else if (focused) color = 4;
            GameComponent.drawString(g, label, location.x + 10, location.y, 0);
            GameComponent.drawString(g, label, location.x+10, location.y-1, color);
        }
    }

    private static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            if (!name.endsWith(".sav")) return false;
            try{ Integer.parseInt(name.substring(0, name.indexOf(".")-1)); }
            catch(NumberFormatException e){ return false; }
            return true;
        }
    };

    private static java.util.List<FileMenuItem> listFiles(File saveGamesDir){
        File[] rawFiles = saveGamesDir.listFiles(FILENAME_FILTER);
        java.util.List<FileMenuItem> files = new ArrayList<FileMenuItem>();
        files.add(new FileMenuItem(0, null));
        for (int i = 0; i < 10; i++){
            File file;
            if (rawFiles != null && i < rawFiles.length) file = rawFiles[i];
            else file = new File(saveGamesDir+""+(i+1)+".sav");
            files.add(new FileMenuItem(i+1, file));
        }
        return files;
    }

    private static enum Mode { Load, Save }

    public static FileMenu createLoadMenu(Console console, Prop speaking, File saveGamesDir){
        return new FileMenu(console, Mode.Load, speaking, listFiles(saveGamesDir));
    }

    public static FileMenu createSaveMenu(Console console, Prop speaking, File saveGamesDir){
        return new FileMenu(console, Mode.Save, speaking, listFiles(saveGamesDir));
    }

    private Mode mode;
    private Prop speaking;
    private boolean error = false;

    private FileMenu(Console console, Mode mode, Prop speaking, java.util.List<FileMenuItem> items) {
        super(console, null, mode.name(), LOCATION, new FileConsoleMenuItemRenderer(), items);
        this.mode = mode;
        this.speaking = speaking;
        setBounds(new Rectangle(LOCATION.x, LOCATION.y, 300, 220));
    }

    public Point getItemLocation(FileMenuItem item){
        return new Point(getBounds().x, MENU_ITEMS_LOCATION.y+items.indexOf(item)*getRenderer().getSize().height);
    }

    public void paintContents(Graphics g) {
        //menu name
        GameComponent.drawString(g, getName(), LOCATION.x+4, LOCATION.y+4, 0);
        GameComponent.drawString(g, getName(), LOCATION.x+4, LOCATION.y+4-1, 7);

        //items
        paintItems((Graphics2D)g, MENU_ITEMS_LOCATION);
    }

    public void select(){
        //they hit 'cancel'
        if (focusedItem.index == 0){
            cancel();
            return;
        }

        if (!focusedItem.file.exists() && mode == Mode.Load) return;

        selectedItem = focusedItem;

        try{
            if (mode == Mode.Load) error = !GameComponent.INSTANCE.loadGame(selectedItem.file);
            else if (mode == Mode.Save) error = !GameComponent.INSTANCE.saveGame(selectedItem.file);
        }
        catch(Exception e){
            e.printStackTrace();
            error = true;
        }

        if (error) getConsole().showTalkMenu(speaking, new TalkNode("Error loading file."));
        else getConsole().remove(this);
    }

    public boolean isError(){ return error; }

    public void cancel(){
        selectedItem = null;
        getConsole().remove(this);
    }

    //so we can say if an item is enabled, otherwise the same
    protected void paintItems(Graphics2D g, Point upperLeftCorner){
        int x = upperLeftCorner.x, y = upperLeftCorner.y;

        int xStep = 0, yStep = getRenderer().getSize().height;
        x += 4;
        y += 4;
        for (int i = 0; i < items.size(); i++){
            FileMenuItem item = items.get(i);
            getRenderer().renderItem(g, item, this, i, new Point(x, y),
                    mode == Mode.Save || item.index == 0 || (item.file != null && item.file.exists()),
                    item == getFocusedItem());
            x += xStep;
            y += yStep;
        }
    }

}

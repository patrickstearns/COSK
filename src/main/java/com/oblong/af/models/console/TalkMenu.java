package com.oblong.af.models.console;

import com.oblong.af.GameComponent;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;
import com.oblong.af.util.GameConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class TalkMenu extends ConsoleMenu<TalkMenu.TalkMenuItem> {

	private static final long serialVersionUID = 2191370390705139048L;
	private static final Rectangle DIALOGUE_BOUNDS = new Rectangle(10, 10, GameConstants.PLAYFIELD_WIDTH-20, 44);
    private static final Point MENU_LOCATION = new Point(140, 60);

	private Image portraitImage;
    private Prop speaking;
    private ConversationNode node;

    public static class TalkMenuItem {
        public String key;
        public ConversationNode node;

        public TalkMenuItem(String key, ConversationNode node){
            this.key = key;
            this.node = node;
        }
    }

    private static class TalkMenuItemRenderer extends ConsoleMenuItemRenderer<TalkMenuItem> {
        public TalkMenuItemRenderer(){ super(new Rectangle(170, 12)); }
        public void renderItem(Graphics2D g, TalkMenuItem item, ConsoleMenu menu, int index, Point location, boolean enabled, boolean focused){
            int color = 7;
            if (!enabled) color = 3;
            else if (focused) color = 4;
            GameComponent.drawString(g, item.key, location.x+10, location.y, 0);
            GameComponent.drawString(g, item.key, location.x+10, location.y-1, color);
        }
    }

    public static TalkMenu createTalkMenu(Console console, Prop speaking, ConversationNode node){
        List<TalkMenuItem> items = new ArrayList<TalkMenuItem>();
        for (String key: node.getOptions().keySet())
            items.add(new TalkMenuItem(key, node.getOptions().get(key)));

        if (items.size() == 0)
            items.add(new TalkMenuItem("Close", null));

        return new TalkMenu(console, speaking, node, items);
    }

    private TalkMenu(Console console, Prop speaking, ConversationNode node, List<TalkMenuItem> items){
		super(console, null, "Talk", MENU_LOCATION, new TalkMenuItemRenderer(), items);
        this.speaking = speaking;
        if (speaking instanceof Actor)
		    this.portraitImage = Art.portraits[((Actor)speaking).getPortraitXPic()][((Actor)speaking).getPortraitYPic()];
        this.node = node;
	}
	
	public void select(){
        super.select();

        node.takeEffect(getConsole().getScene(), speaking, selectedItem.key);
        if (selectedItem.node != null) getConsole().showTalkMenu(speaking, selectedItem.node);
    }

	public void cancel(){} //noop'd; means nothing here

    public void paint(Graphics g){
        paintBackground(g, getBounds());
        paintBackground(g, DIALOGUE_BOUNDS);
        paintBorder(g, getBounds());
        paintBorder(g, DIALOGUE_BOUNDS);
        paintContents(g);
    }

    public void paintContents(Graphics g){
        int inset = 2;
        Rectangle textBounds = new Rectangle(DIALOGUE_BOUNDS.x+inset, DIALOGUE_BOUNDS.y+inset+2,
                (int)(DIALOGUE_BOUNDS.getWidth()-inset), (int)(DIALOGUE_BOUNDS.getHeight()+inset+inset));
        g.setColor(Color.WHITE);
        g.setFont(Console.STANDARD_FONT);

        //draw text
		String[] texts = cutString(node.getText(), textBounds.width);
		for (int i = 0; i < texts.length; i++){
            GameComponent.drawString(g, texts[i], textBounds.x, textBounds.y+i*12+2+1, 0);
            GameComponent.drawString(g, texts[i], textBounds.x, textBounds.y+i*12+2, 7);
        }

        paintItems((Graphics2D)g, MENU_LOCATION);
    }

    private String[] cutString(String text, int width){
        Vector<String> ret = new Vector<String>();

        String currentString = "";
        StringTokenizer st = new StringTokenizer(text, " ");
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            int tokenWidth = 8*token.length();
            int stringWidth = 8*currentString.length();
            if (tokenWidth+stringWidth > width) {
                ret.add(currentString);
                currentString = token;
            }
            else currentString += " "+token;
        }
        if (!"".equals(currentString)) ret.addElement(currentString);

        return ret.toArray(new String[ret.size()]);
    }

}

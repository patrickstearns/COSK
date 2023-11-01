package com.oblong.af.sprite.npcs;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.decorator.OutlineDecorator;
import com.oblong.af.util.Art;

import java.awt.*;

public class PetrifiedNPC extends Prop {

    private ConversationNode conversationNode;

    public PetrifiedNPC(String id, AreaScene scene, int oxPic, int oyPic, ConversationNode conversationNode) {
        super(id, scene, oxPic, oyPic);

        setXPic(oxPic);
        setYPic(oyPic);
        setWidth(15);
        setHeight(16);

        setConversationNode(conversationNode);
        setSheet(Art.characters);

        setBlocksMovement(true);
        setBlocksFlying(true);
        setFlying(false);
        setBlockableByScreenEdge(true);

        setCanBeKnockedback(false);
        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);

        if (conversationNode != null){
            addDecorator(new OutlineDecorator(Color.BLUE, 10, -1));
            getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
        }
        else getMessages().getActivatingMessages().remove(AreaScene.PLAYER_CLICK_MESSAGE);
    }

    public ConversationNode getConversationNode(){ return conversationNode; }
    public void setConversationNode(ConversationNode conversationNode){ this.conversationNode = conversationNode; }

    public void setActive(boolean active){
        if (getConversationNode() != null)
            getScene().getConsole().showTalkMenu(this, getConversationNode());
    }

    public void move(){
        setDesaturated(true);
        super.move(0, 0);
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        //if loadlevel isn't null, overlay astral cord atop image
        int x1 = (int)getX(), y1 = (int)(getY()-22);
        int x2 = x1, y2 = y1-32;
        int yinc = (y2-y1)/10;
        int pulse = (getTick()/2)%10;

        for (int i = 0; i < 10; i++){
            float a = 1-((float)i/10f);
            og.setPaint(new GradientPaint(x1, y1-pulse, new Color(1f, 1f, 1f, a), x2, y1-pulse+5, new Color(0.25f, 0.5f, 1f, a), true));
            og.drawLine(x1, y1+i*yinc, x2, y1+i*yinc+yinc);
            og.drawLine(x1-1, y1+i*yinc, x2-1, y1+i*yinc+yinc);
        }
    }
}

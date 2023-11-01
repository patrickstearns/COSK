package com.oblong.af.sprite.npcs;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.NPCs;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class RescuableNPC extends Prop {

    private static final int MAX_FREED_COUNTER = 30;
    private static final int MAX_FADE_COUNTER = 30;

    protected int frameX, frameY, animLength;
    private int freedCounter = 0, fadeCounter = 0;
    private ConversationNode conversationNode;

    public RescuableNPC(String id, AreaScene scene, int oxPic, int oyPic, ConversationNode conversationNode) {
        super(id, scene, oxPic, oyPic);

        frameX = 0;
        frameY = 0;
        animLength = 6;
        setWidth(15);
        setHeight(16);

        setConversationNode(conversationNode);
        setSheet(Art.characters);

        setBlocksMovement(true);
        setBlocksFlying(true);
        setFlying(false);
        setBlockableByScreenEdge(true);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);

        setShadowVisible(false);

        getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
    }

    public ConversationNode getConversationNode(){ return conversationNode; }
    public void setConversationNode(ConversationNode conversationNode){ this.conversationNode = conversationNode; }

    public void tickNoMove(){
        super.tickNoMove();
        if (freedCounter > 0 || fadeCounter > 0) move();
    }

    public void move(){
        if (freedCounter > 0){
            freedCounter--;
            if (freedCounter == 0){
                //open up modal talk window with their talk message
                getScene().getConsole().showTalkMenu(this, getConversationNode());

                //set them to intangible, start things up again, poof and fade out
                setBlocksMovement(false);
                setBlockable(false);
                fadeCounter = MAX_FADE_COUNTER;
                NPCs.setRescued(getId(), getScene().getGameState()); //immediately mark as rescued in case we leave area before they fade away
            }
        }

        if (getScene().actionPaused && fadeCounter > 0) return;

        if (fadeCounter > 0){
            setYPicO(getYPicO()+1);
            if (fadeCounter == MAX_FADE_COUNTER){
                poof();
                getScene().getSound().play(Art.getSample("rescued.wav"), this, 1, 1, 1);
            }
            fadeCounter--;
            if (fadeCounter == 0){
                getScene().removeSprite(this);
                die();
            }
        }

        if (!isActive() || freedCounter > 0){
            if (getTick()%5 == 0)
                getScene().addSprite(new Sparkle(getScene(),
                        (int)(getX()+getWidth()/2-24+Math.random()*48), (int)(getY()+8-48+Math.random()*48),
                        Color.WHITE, 0, 0));
        }

        calcPic();
        move(getXa(), 0);
        move(0, getYa());
    }

    private void poof(){
        for (int i = 0; i < 100; i++){
            int degree = (int)(Math.random()*360);
            int distance = (int)(Math.random()*16);
            int xOffset = (int)(Math.cos(Math.toRadians(degree))*distance);
            int yOffset = (int)(Math.sin(Math.toRadians(degree))*distance);
            int xa = xOffset/6;
            int ya = -(int)(Math.random()*3);
            getScene().addSprite(new Sparkle(getScene(),
                    (int)(getX()+getWidth()/2+xOffset), (int)(getY()-getHeight()/2+yOffset),
                    Color.WHITE, xa, ya));
        }
    }

    public void setActive(boolean active){
        if (!this.isActive() && active){
            freedCounter = MAX_FREED_COUNTER;
            getScene().actionPaused = true;
        }
        super.setActive(active);
    }

    public void calcPic(){
        int xPic = getOxPic(), yPic = getOyPic();
        if (fadeCounter > 0) xPic++;
        setXPic(xPic);
        setYPic(yPic);
    }

    public void render(Graphics2D og, float alpha){
        if (fadeCounter > 0){
            float spriteAlpha = (float)fadeCounter/(float)MAX_FADE_COUNTER;
            setFadeRatio(spriteAlpha);
        }

        super.render(og, alpha);

        //overlay crystal image atop regular one
        if (!isActive() || freedCounter > 0){
            float crystalAlpha = 1f;
            if (freedCounter > 0) crystalAlpha = (float)freedCounter/(float)MAX_FREED_COUNTER;
            crystalAlpha /= 2f;

            Image crystalImage = Art.effects48x48[0][0];
            crystalImage = ImageUtils.fadeImage(crystalImage, null, crystalAlpha);
            int crystalX = (int)(getX()-crystalImage.getWidth(null)/2);
            int crystalY = (int)(getY()-crystalImage.getHeight(null));
            if (getOutlineColor() != null){
                crystalImage = ImageUtils.outlineImage(crystalImage, getOutlineColor());
                crystalX-=1;
                crystalY-=1;
            }
            og.drawImage(crystalImage, crystalX, crystalY, null);
        }

        //if loadlevel isn't null, overlay astral cord atop image
        if (fadeCounter == 0){
            int x1 = (int)getX(), y1 = (int)(getY()-22);
            int x2 = x1, y2 = y1-32;
            int yinc = (y2-y1)/10;
            int pulse = 10-(getTick()/2)%10;
            for (int i = 0; i < 10; i++){
                float a = 1-((float)i/10f);
                og.setPaint(new GradientPaint(x1, y1-pulse, new Color(1f, 1f, 1f, a), x2, y1-pulse+5, new Color(0.25f, 0.5f, 1f, a), true));
                og.drawLine(x1, y1+i*yinc, x2, y1+i*yinc+yinc);
                og.drawLine(x1-1, y1+i*yinc, x2-1, y1+i*yinc+yinc);
            }
        }
    }
}

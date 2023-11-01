package com.oblong.af.sprite.npcs;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Facing;
import com.oblong.af.models.NPCs;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.models.conversation.TalkNode;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;

public class Dog extends Prop {

    private ConversationNode startingNode;

    public Dog(AreaScene scene, ConversationNode startingNode) {
        super("Dog", scene, 24, 21);
        setStartingNode(startingNode);
        setSheet(Art.characters);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setBlockableByScreenEdge(true);
    }

    public ConversationNode getStartingNode(){ return startingNode; }
    public void setStartingNode(ConversationNode startingNode){ this.startingNode = startingNode; }

    protected double headingTowardPlayer(){
        double xDiff = getScene().player.getX()-getX();
        double yDiff = getScene().player.getY()-getY();

        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        atan += Math.random()*0.4-0.2;
        return atan;
    }

    public void move(){
        super.move();
        double distance = Point2D.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY());
        setHeading(headingTowardPlayer());
        setMoving(distance > 32);
        setSpeed((distance > 96) ? 5 : 2);

        setOutlineColor(ImageUtils.calculateCycleColor(getTick(), new Color[]{Color.WHITE, new Color(1f, 1f, 1f, 0.5f)}, 30));
    }

    public void setActive(boolean active){
        getScene().getConsole().showTalkMenu(this, getStartingNode());
    }

    protected void calcPic(){
        int xOff, yOff = 0;

        switch(getFacing()){
            case LEFT: xOff = 2; break;
            case RIGHT: xOff = 3; break;
            case UP: xOff = 1; break;
            case DOWN: xOff = 0; break;
            default: xOff = 0; break;
        }

        int runFrame = ((int) (getRunTime()/4)) % 4;
        if (runFrame == 0 || runFrame == 2) yOff = 0;
        else if (runFrame == 1) yOff = 1;
        else if (runFrame == 3) yOff = 2;

        if (!isMoving()){
            xOff = (getFacing() == Facing.LEFT || getFacing() == Facing.UP) ? 4 : 5;
            yOff = 1;
        }

        if (isDead()){
            xOff = 5;
            yOff = 1;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

}
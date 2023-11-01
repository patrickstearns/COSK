package com.oblong.af.sprite.npcs;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.NPCs;
import com.oblong.af.models.conversation.ConversationNode;
import com.oblong.af.models.conversation.TalkNode;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

import java.awt.*;
import java.awt.geom.Point2D;

public class TalkableNPC extends Prop {

    private ConversationNode startingNode;

    public TalkableNPC(String id, AreaScene scene, int oxPic, int oyPic, ConversationNode startingNode) {
        super(id, scene, oxPic, oyPic);
        setStartingNode(startingNode);
        setSheet(Art.characters);

        setImmuneToDamage(true);
        setImmuneToStatusEffects(true);
        setBlockableByScreenEdge(true);
    }

    public ConversationNode getStartingNode(){ return startingNode; }
    public void setStartingNode(ConversationNode startingNode){ this.startingNode = startingNode; }

    public void move(){
        super.move();

        Point2D.Float actorP = new Point2D.Float(getX(), getY());
        Point2D.Float origP = new Point2D.Float(getSpriteTemplate().getOriginalX(), getSpriteTemplate().getOriginalY());
        int distToOrigin = (int)actorP.distance(origP);

        if (getTick() % 10 == 0){
            //if we're outside of our wander radius, move toward our center point
            if (distToOrigin > 64) moveToPoint(this, origP);
                //otherwise possibly stop moving for a moment
            else if (Math.random() < 0.2f) setMoving(false);
                //elsewise pick a direction to go in
            else{
                setHeading(Math.toRadians((Math.random()*10000)%360));
                setMoving(true);
            }
        }

        if (Point.distance(getX(), getY(), getScene().player.getX(), getScene().player.getY()) < 32){
            setMoving(false);
            setHeading(getProjectileHeading(this, getScene().player));
        }
    }

    public void setActive(boolean active){
        getScene().getConsole().showTalkMenu(this, getStartingNode());

        //refresh conversation
        if (NPCs.isRescued(getId(), getScene().getGameState())){
            if (NPCs.isGaveFirst(getId(), getScene().getGameState())){
                setStartingNode(new TalkNode(NPCs.valueOf(getId()).getNothingToGiveMessage()));
            }
            else{
                NPCs.setGaveFirst(getId(), getScene().getGameState());
                setStartingNode(NPCs.valueOf(getId()).createConversationNode(getScene(), false));
            }
        }
    }

    protected void moveToPoint(Prop actor, Point2D.Float target){
        actor.setHeading(Math.atan2(target.getX()-actor.getX(), target.getY()-actor.getY()));
        actor.setMoving(true);
    }

    protected double getProjectileHeading(Prop actor, Player player){
        double xDiff = actor.getScene().player.getX()-actor.getX();
        double yDiff = actor.getScene().player.getY()-actor.getY();

        double atan = Math.atan2(xDiff, yDiff);
        if(atan < 0) atan += Math.PI*2;
        atan -= Math.PI/2f;
        atan += Math.random()*0.4-0.2;
        return atan;
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

        if (!isMoving()) yOff = 0;

        if (isDead()){
            xOff = 5;
            yOff = 1;
        }

        setXPic(getOxPic() + xOff);
        setYPic(getOyPic() + yOff);
    }

}
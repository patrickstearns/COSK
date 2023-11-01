package com.oblong.af.models.conversation;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

import java.awt.geom.Point2D;

public class GoToLevelNode extends TalkNode {

    private String level;

    public GoToLevelNode(String level){
        super(null);
        this.level = level;
    }

    public void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey){
        scene.player.setTeleportToLevel(level);
        scene.player.setTeleportInTime(1);
        scene.player.setTeleportOutFromPoint(new Point2D.Float(scene.player.getX(), scene.player.getY()));
    }

}

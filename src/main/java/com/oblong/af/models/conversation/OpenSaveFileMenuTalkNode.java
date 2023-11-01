package com.oblong.af.models.conversation;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

public class OpenSaveFileMenuTalkNode extends ConversationNode {

    public OpenSaveFileMenuTalkNode(){ super(null); }

    public void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey){
        scene.getConsole().showSaveFileMenu(speaking);
    }

}

package com.oblong.af.models.conversation;

import com.oblong.af.GameComponent;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

public class QuitGameTalkNode extends ConversationNode {

    public QuitGameTalkNode(){ super(null); }

    public void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey){
        GameComponent.INSTANCE.toScene(GameComponent.Scenes.Title);
    }

}

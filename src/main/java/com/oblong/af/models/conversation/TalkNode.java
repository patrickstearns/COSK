package com.oblong.af.models.conversation;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

public class TalkNode extends ConversationNode {
    public TalkNode(String text){ super(text); }
    public void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey){}
}

package com.oblong.af.models.conversation;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;

import java.util.HashMap;
import java.util.Map;

public abstract class ConversationNode {

    private String text;
    private Map<String, ConversationNode> options;

    protected ConversationNode(String text){
        this.text = text;
        options = new HashMap<String, ConversationNode>();
    }

    public abstract void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey);

    public String getText(){ return text; }
    public void setText(String text){ this.text = text; }

    public Map<String, ConversationNode> getOptions(){ return options; }
    public void setOptions(Map<String, ConversationNode> options){ this.options = options; }

    public boolean isTerminal(){ return getOptions() == null || getOptions().size() == 0; }

}

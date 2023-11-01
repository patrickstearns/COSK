package com.oblong.af.models.conversation;

public class DogNode extends TalkNode {

    public DogNode(){
        super("I can save your game, ruff ruff!");
        getOptions().put("Quit", new QuitGameTalkNode());
        getOptions().put("Save Game", new OpenSaveFileMenuTalkNode());
        getOptions().put("Cancel", null);
    }

}

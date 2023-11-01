package com.oblong.af.models.conversation;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.Ability;
import com.oblong.af.models.Powerups;
import com.oblong.af.sprite.Powerup;
import com.oblong.af.sprite.Prop;

public class PowerupTalkNode extends ConversationNode {

    private Powerups powerup;
    private Ability abilityDrop;

    public PowerupTalkNode(String text, Powerups powerup, Ability abilityDrop){
        super(text);
        this.powerup = powerup;
        this.abilityDrop = abilityDrop;
    }

    public void takeEffect(AreaScene scene, Prop speaking, String selectedItemKey){
        if (powerup != null)
            scene.addSprite(new Powerup(scene, powerup, abilityDrop, (int) speaking.getX(), (int) speaking.getY()+24));
    }

}

package com.oblong.af.level;

import com.oblong.af.models.ActorMessages;
import com.oblong.af.models.Facing;
import com.oblong.af.models.Messages;
import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;

public class SpriteTemplate {

	private SpriteDefinitions spriteDef;
    private Sprite sprite;
	private int lastVisibleTick = -1;
    private Area.Layer layer;
    private boolean active, enabled, boss, activeByDefault;
	private float originalX, originalY;
    private Messages templateMessages;
    private ActorMessages actorMessages;

    public SpriteTemplate(SpriteDefinitions spriteDef, Area.Layer layer, boolean boss, boolean activeByDefault, Messages templateMessages, ActorMessages actorMessages){
    	setSpriteDef(spriteDef);
    	this.layer = layer;
        this.templateMessages = templateMessages;
        this.actorMessages = actorMessages;

        active = true;
        enabled = templateMessages.getEnablingMessages().size() == 0;
        this.boss = boss;
        this.activeByDefault = activeByDefault;
    }
    
	public Sprite spawn(AreaScene scene, int x, int y, Facing facing, boolean evenIfDisabled){
        if (!active) return null;
        if (!enabled && !evenIfDisabled) return null;
        sprite = spriteDef.create(scene, this, x * 16 + 8, y * 16 + 15, facing);
        if (sprite == null) return null;
        originalX = sprite.getX();
        originalY = sprite.getY();

        sprite.setMessages(actorMessages);
        if (activeByDefault) sprite.setActive(true);
        if (sprite instanceof Prop) ((Prop)sprite).setBoss(boss);

        if (scene != null){
        	if (sprite instanceof Prop && scene.propsBlocking((Prop)sprite, sprite.getFootprint2D()).size() > 0) sprite = null;
        	else scene.addSprite(sprite); //scene is null if this is the editor; not if it's the game
        }

        return sprite;
    }

    public void actorDied(){
        setEnabled(false);
    }

    public void receiveMessage(Sprite source, String message){
        if (!enabled && getTemplateMessages().getEnablingMessages().contains(message)) setEnabled(true);
        if (enabled && getTemplateMessages().getDisablingMessages().contains(message)) setEnabled(false);
        if (!active && getTemplateMessages().getActivatingMessages().contains(message)) setActive(true);
        if (active && getTemplateMessages().getDeactivatingMessages().contains(message)) setActive(false);
    }

    public Area.Layer getLayer(){ return layer; }

	public Sprite getSprite(){ return sprite; }

    public boolean isEnabled(){ return enabled; }
    public void setEnabled(boolean enabled){ this.enabled = enabled; }

    public boolean isActive(){ return active; }
    public void setActive(boolean active){ this.active = active; }

    public boolean isBoss(){ return boss; }
    public void setBoss(boolean boss){ this.boss = boss; }

    public SpriteDefinitions getSpriteDef(){ return spriteDef; }
    public void setSpriteDef(SpriteDefinitions spriteDef){ this.spriteDef = spriteDef; }

    public int getLastVisibleTick(){ return lastVisibleTick; }
    public void setLastVisibleTick(int lastVisibleTick){ this.lastVisibleTick = lastVisibleTick; }

    public float getOriginalX(){ return originalX; }
	public float getOriginalY(){ return originalY; }

    public Messages getTemplateMessages(){ return templateMessages; }
    public void setTemplateMessages(Messages templateMessages){ this.templateMessages = templateMessages; }

    public ActorMessages getActorMessages(){ return actorMessages; }
    public void setActorMessages(ActorMessages actorMessages){ this.actorMessages = actorMessages; }

    public boolean isActiveByDefault(){ return activeByDefault; }
    public void setActiveByDefault(boolean activeByDefault){ this.activeByDefault = activeByDefault; }

}
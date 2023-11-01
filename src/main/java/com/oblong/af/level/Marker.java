package com.oblong.af.level;

import com.oblong.af.models.Messages;
import com.oblong.af.sprite.effects.HexagramEffect;
import com.oblong.af.sprite.effects.ReturnExitEffect;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Marker {

    public static enum Type {
        StartPosition(Art.editorIcons[0][0]),
        Entrance(Art.editorIcons[0][1]),
        Exit(Art.editorIcons[0][2]),
        TwoWay(Art.editorIcons[0][3]),
        ReturnExit(Art.editorIcons[0][6]),
        LairExit(Art.editorIcons[0][7]),
        Minibossify(Art.editorIcons[2][5]),
        AndGate(Art.editorIcons[10][0]),
        OrGate(Art.editorIcons[10][1]),
        XorGate(Art.editorIcons[10][2]),
        NotGate(Art.editorIcons[10][3]),
        DelayGate(Art.editorIcons[10][4]),
        TriggerGate(Art.editorIcons[10][5]),
        WeatherTrigger(Art.editorIcons[10][6]),
        ;
        private ImageIcon editorIcon;
        private Type(Image image){ editorIcon = new ImageIcon(image); }
        public ImageIcon getEditorIcon(){ return editorIcon; }
    }

	private String id, levelId;
    private int x, y, width, height, delayTicks;
    private Area.Layer layer;
    private Type type;
    private Messages messages;

    private boolean active;
    private int delayTick;
    private java.util.List<String> activeActivatingMessages;

    public Marker(String id, String levelId, int x, int y, Area.Layer layer, int width, int height, Type type, Messages messages, int delayTicks){
		this.id = id;
		this.levelId = levelId;
		this.x = x;
		this.y = y;
        this.layer = layer;
		this.width = width;
		this.height = height;
		this.type = type;
        this.messages = messages;

        this.active = (type == Type.StartPosition || type == Type.Entrance | type == Type.Exit || type == Type.TwoWay);
        this.delayTick = 0;
        this.delayTicks = delayTicks;
        activeActivatingMessages = new ArrayList<String>();
    }

    public void render(Graphics2D og, float alpha){
        if (!AreaGroupRenderer.renderBehaviors) return;

        Color outlineColor = Color.RED;
        if (isActive()) outlineColor = Color.CYAN;

        Rectangle imageBounds = new Rectangle(getX()*16-1, getY()*16-1, 18, 18);
        Image image = getType().getEditorIcon().getImage();
        image = ImageUtils.outlineImage(image, outlineColor);
        og.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
    }

    public void tick(AreaScene scene){
        if (delayTick > 0){
            delayTick--;
            if (delayTick == 0){
                setActive(!isActive());
                if (isActive())
                    for (String m: messages.getOnActivationMessages()){
                        scene.message(null, m);
                    }
                else
                    for (String m: messages.getOnDeactivationMessages()){
                        scene.message(null, m);
                    }
            }
        }
    }

    public void receiveMessage(AreaScene scene, String message){
        boolean oldActive = isActive();
        switch (getType()) {
            case StartPosition: case Entrance: case Exit: case TwoWay: case TriggerGate: case ReturnExit: case LairExit:
            case Minibossify: case WeatherTrigger:
                receiveMessageExit(scene, message);
                break;
            case DelayGate: case AndGate: case OrGate: case XorGate: case NotGate:
                receiveMessageGate(scene, message);
                break;
        }
        if (isActive() != oldActive){
            if (isActive())
                for (String m: messages.getOnActivationMessages()){
                    scene.message(null, m);
                }
            else
                for (String m: messages.getOnDeactivationMessages()){
                    scene.message(null, m);
                }
        }
    }

    private void receiveMessageExit(AreaScene scene, String message){
        boolean oldActive = isActive();

        if (!active && getMessages().getActivatingMessages().contains(message)){
            setActive(true);
            if (getType() == Type.ReturnExit) scene.addSprite(new ReturnExitEffect(scene, x*16+8, y*16));
            else if (getType() == Type.LairExit) scene.addSprite(new HexagramEffect(scene, x*16+8, y*16));
            else if (getType() == Type.Minibossify) scene.minibossifyEnemies();
            else if (getType() == Type.WeatherTrigger && getMessages().getOnActivationMessages().size() > 0){
                int split = getMessages().getOnActivationMessages().get(0).indexOf("-");
                AreaGroup.Weather weather = AreaGroup.Weather.valueOf(getMessages().getOnActivationMessages().get(0).substring(0, split));
                AreaGroup.LightLevel lightLevel = AreaGroup.LightLevel.valueOf(getMessages().getOnActivationMessages().get(0).substring(split+1));
                scene.areaGroup.getCurrentArea().setWeather(weather);
                scene.areaGroup.getCurrentArea().setLightLevel(lightLevel);
            }
        }
        if (active && getMessages().getDeactivatingMessages().contains(message)){
            setActive(false);
            if (getType() == Type.WeatherTrigger && getMessages().getOnDeactivationMessages().size() > 0){
                int split = getMessages().getOnDeactivationMessages().get(0).indexOf("-");
                AreaGroup.Weather weather = AreaGroup.Weather.valueOf(getMessages().getOnDeactivationMessages().get(0).substring(0, split));
                AreaGroup.LightLevel lightLevel = AreaGroup.LightLevel.valueOf(getMessages().getOnDeactivationMessages().get(0).substring(split+1));
                scene.areaGroup.getCurrentArea().setWeather(weather);
                scene.areaGroup.getCurrentArea().setLightLevel(lightLevel);
            }
        }

        if (isActive() != oldActive){
            if (isActive())
                for (String m: messages.getOnActivationMessages()){
                    scene.message(null, m);
                }
            else
                for (String m: messages.getOnDeactivationMessages()){
                    scene.message(null, m);
                }
        }
    }

    private void receiveMessageGate(AreaScene scene, String message){
        if (getMessages().getActivatingMessages().contains(message) && !activeActivatingMessages.contains(message))
            activeActivatingMessages.add(message);
        else if (getMessages().getDeactivatingMessages().contains(message))
            activeActivatingMessages.remove(message);
        if (getType() == Type.DelayGate && getMessages().getActivatingMessages().contains(message)){
            delayTick = delayTicks;
        }
        else{
            boolean should = shouldBeActive();
            if (should != isActive()) setActive(should);
        }
    }

    private boolean shouldBeActive(){
        switch (getType()) {
            default:
                return (activeActivatingMessages.size() > 0);
            case AndGate:
                return (activeActivatingMessages.size() == getMessages().getActivatingMessages().size());
            case OrGate:
                return (activeActivatingMessages.size() > 0);
            case XorGate:
                return (activeActivatingMessages.size() > 0 && activeActivatingMessages.size() != getMessages().getActivatingMessages().size());
            case NotGate:
                return (activeActivatingMessages.size() == 0);
            case DelayGate:
                return false;
        }
    }

    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    public String getLevelId(){ return levelId; }
    public void setLevelId(String levelId){ this.levelId = levelId; }

    public int getX(){ return x; }
    public void setX(int x){ this.x = x; }

    public int getY(){ return y; }
    public void setY(int y){ this.y = y; }

    public Area.Layer getLayer(){ return layer; }
    public void setLayer(Area.Layer layer){ this.layer = layer; }

    public int getWidth(){ return width; }
    public void setWidth(int width){ this.width = width; }

    public int getHeight(){ return height; }
    public void setHeight(int height){ this.height = height; }

    public Type getType(){ return type; }
    public void setType(Type type){ this.type = type; }

    public Messages getMessages(){ return messages; }
    public void setMessages(Messages messages){ this.messages = messages; }

    public boolean isActive(){ return active; }
    public void setActive(boolean active){ this.active = active; }

    public int getDelayTicks(){ return delayTicks; }
    public void setDelayTicks(int delayTicks){ this.delayTicks = delayTicks; }

}

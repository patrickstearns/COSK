package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.ActorMessages;
import com.oblong.af.models.Attribute;
import com.oblong.af.models.DamageAttributes;
import com.oblong.af.models.World;
import com.oblong.af.sprite.enemy.*;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;

public class MagicTarget extends Thing {

    private int glowCounter = 0;
    private Color glowColor;
    private java.util.List<Attribute> activatingAttributes;
    private String activatingFlag;

    public MagicTarget(AreaScene scene, int x, int y, java.util.List<Attribute> activatingAttributes){
        this(scene, x, y, 0, activatingAttributes, null);
        setShadowVisible(false);
    }

    public MagicTarget(AreaScene scene, int x, int y, String activatingFlag){
        this(scene, x, y, 2, null, activatingFlag);
        getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
    }

    private MagicTarget(AreaScene scene, int x, int y, int xPic, java.util.List<Attribute> activatingAttributes, String activatingFlag){
        super("MagicTarget", scene, x, y, Art.objects32x32, xPic, 5, 32, 32, 16, 16, true, false);
        setImmuneToDamage(false);
        setMaxHp(Integer.MAX_VALUE);
        setHp(Integer.MAX_VALUE);
        setSuppressHpMeter(true);
        this.activatingAttributes = activatingAttributes;
        this.activatingFlag = activatingFlag;
    }

    public void move(){
        if (activatingAttributes != null && glowCounter > 0){
            glowCounter--;
            setActive(false);
        }
        if (activatingFlag != null){
            if (GigapedeSegment.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) glowColor = Color.RED;
            else if (SpiderQueen.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) glowColor = Color.CYAN;
            else if (Vampire.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) glowColor = Color.GREEN;
            else if (Wyvern.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) glowColor = Color.YELLOW;
            else if (BigBeholder.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) glowColor = Color.WHITE;
            else glowColor = Color.MAGENTA;

            if ("true".equals(getScene().getGameState().getVariable(activatingFlag))){
                glowCounter = 10;
                getMessages().getActivatingMessages().remove(AreaScene.PLAYER_CLICK_MESSAGE);
            }
            else{
                glowCounter = 0;
                if (!getMessages().getActivatingMessages().contains(AreaScene.PLAYER_CLICK_MESSAGE))
                    getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
            }
        }
    }

    public void setActive(boolean active){
        boolean wasActive = isActive();
        super.setActive(active);

        if (activatingFlag != null && active && !wasActive){
            String level = null;
            if (GigapedeSegment.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) level = World.JUNGLE_AREA_GROUP_ID;
            else if (SpiderQueen.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) level = World.GLACIER_AREA_GROUP_ID;
            else if (Vampire.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) level = World.CAVERNS_AREA_GROUP_ID;
            else if (Wyvern.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) level = World.DESERT_AREA_GROUP_ID;
            else if (BigBeholder.DEFEATED_STATE_VARIABLE.equals(activatingFlag)) level = World.CRYPTS_AREA_GROUP_ID;

            if (level == null) throw new NullPointerException("Messed something up with level teleporters; level is null.");

            getScene().player.setTeleportToLevel(level);
            getScene().player.setTeleportInTime(1);
            getScene().player.setTeleportOutFromPoint(new Point2D.Float(getScene().player.getX(), getScene().player.getY()));
        }
    }


    public void damage(DamageAttributes damageAttributes){
        if (activatingFlag != null) return;

        super.damage(damageAttributes);
        for (Attribute attribute: activatingAttributes)
            if (damageAttributes.getAttributes().contains(attribute)){
                glowCounter = 128;

                if (attribute == Attribute.Fire) glowColor = Color.RED;
                else if (attribute == Attribute.Water) glowColor = Color.BLUE;
                else if (attribute == Attribute.Earth) glowColor = Color.GREEN;
                else if (attribute == Attribute.Wind) glowColor = Color.YELLOW;
                else if (attribute == Attribute.Spirit) glowColor = Color.WHITE;
                else if (attribute == Attribute.Freeze) glowColor = Color.CYAN;
                else if (attribute == Attribute.Electric) glowColor = new Color(1f, 1f, 0.5f);
                else glowColor = Color.MAGENTA;

                setActive(true);
            }
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        Rectangle imageBounds = getImageFootprint(alpha);

        Image ball = getSheet()[1][getYPic()];
        if (glowCounter > 0){
            float fade;
            if (glowCounter < 16) fade = (float)glowCounter/16f;
            else {
                int f = (glowCounter+16)%16-8;
                if (f < 0) f *= -1;
                float fr = (float)f/8f;
                fr /= 2;
                fr += 0.5f;
                fade = fr;
            }
            og.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), (int)(fade*255)));
            int radius = 5;//(int)(5*(1.5f-fade));
            og.fillOval((int)(imageBounds.x+imageBounds.width/2f-radius), (int)(imageBounds.y+imageBounds.height/2f-radius*2)+2,
                    radius*2, radius*2);

            ball = ImageUtils.outlineImage(ball, glowColor);
        }

        og.drawImage(ball, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
    }
}

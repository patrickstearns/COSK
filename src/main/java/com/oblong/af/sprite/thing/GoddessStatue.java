package com.oblong.af.sprite.thing;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.World;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Point2D;

public class GoddessStatue extends Thing {

    private int tick = 0;
    private boolean broken;

    public GoddessStatue(AreaScene scene, int x, int y){
        super("MagicTarget", scene, x, y, Art.objects32x64, 5, 1, 32, 64, 32, 32, true, false);
        setImmuneToDamage(true);
        setMaxHp(Integer.MAX_VALUE);
        setHp(Integer.MAX_VALUE);
        setSuppressHpMeter(true);

        broken = scene != null && scene.getGameState() != null && scene.getGameState().isAllBossesDead();
        if (broken){
            setXPic(getXPic()+1);
        }
    }

    public void move(){
        if (tick == 0 && broken){
            getMessages().getActivatingMessages().add(AreaScene.PLAYER_CLICK_MESSAGE);
        }
        tick++;
    }

    public void setActive(boolean active){
        boolean wasActive = isActive();
        super.setActive(active);
        if (active && !wasActive){
            getScene().player.setTeleportToLevel(World.LAIR_AREA_GROUP_ID);
            getScene().player.setTeleportInTime(1);
            getScene().player.setTeleportOutFromPoint(new Point2D.Float(getScene().player.getX(), getScene().player.getY()));
        }
    }

    public void render(Graphics2D og, float alpha){
        super.render(og, alpha);

        if (broken){
            Rectangle imageBounds = getImageFootprint(alpha);
            imageBounds.y += 32;
            imageBounds.height = 32;

            int f = (tick+16)%16-8;
            if (f < 0) f *= -1;
            float fr = (float)f/8f;
            fr /= 2;
            fr += 0.5f;
            og.setColor(new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), (int)(fr*255)));
            int radius = 5;//(int)(5*(1.5f-fade));
            og.fillOval((int)(imageBounds.x+imageBounds.width/2f-radius), (int)(imageBounds.y+imageBounds.height/2f-radius*2)+2,
                    radius*2, radius*2);

            og.drawImage(ImageUtils.outlineImage(Art.objects32x32[1][5], Color.MAGENTA),
                    imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
        }
    }
}

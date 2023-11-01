package com.oblong.af;

import com.oblong.af.models.*;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class PlayerSelectScene extends Scene {

    private enum PickablePlayer{ //from 30 to 190 in x direction, 40 to 190 in y
        Brent(SpriteDefinitions.Brent, 30, 60, Facing.DOWN),
        Sean(SpriteDefinitions.Sean, 70, 110, Facing.DOWN),
        Matt(SpriteDefinitions.Matt, 90, 40, Facing.LEFT),
        Allen(SpriteDefinitions.Allen, 40, 135, Facing.RIGHT),
        Dragon(SpriteDefinitions.Dragon, 80, 170, Facing.LEFT),
        Marshall(SpriteDefinitions.Marshall, 120, 150, Facing.RIGHT),
        Adam(SpriteDefinitions.Adam, 150, 80, Facing.DOWN),
        Patrick(SpriteDefinitions.Patrick, 170, 150, Facing.DOWN),
        ;
        private SpriteDefinitions playerDef;
        private int x, y;
        private Facing facing;
        PickablePlayer(SpriteDefinitions playerDef, int x, int y, Facing facing){
            this.playerDef = playerDef;
            this.x = x;
            this.y = y;
            this.facing = facing;
        }

        public SpriteDefinitions getPlayerDef(){ return playerDef; }
        public Rectangle getBounds(){ return new Rectangle(x-8, y-32, 16, 32); }
        public Rectangle getPortraitBounds(){ return new Rectangle((160-((values().length*32)/2))+ordinal()*34, 208, 32, 32); }

        public void render(Graphics2D g, boolean focused){
            int xOff = 0, yOff = 0;
            switch(facing){
                case LEFT: xOff = 2; break;
                case RIGHT: xOff = 3; break;
                case UP: xOff = 1; break;
                case DOWN: xOff = 0; break;
            }
            Image image = Art.characters[getPlayerDef().getXPic()+xOff][getPlayerDef().getYPic()+yOff];
            Rectangle bounds = getBounds();
            if (focused){
                image = ImageUtils.outlineImage(image, Color.CYAN);
                bounds.x -= 1;
                bounds.y -= 1;
            }
            else image = ImageUtils.grayscaleImage(image, null);

            g.drawImage(image, bounds.x, bounds.y, null);
        }
    }

    private class Cloud {
        Rectangle2D.Double bounds;
        double dir;
        public Cloud(Rectangle2D.Double bounds, float dir){
            this.bounds = bounds;
            this.dir = dir;
        }
    }

	private GameComponent component;
    private int fadeOutTime = 0, maxFadeOutTime = 8;
    private int fadeInTime = 0, maxFadeInTime = 8;
    private PickablePlayer focusedPickable = null, selectedPickable = null;
    private java.util.List<Cloud> bgClouds, fgClouds;

    public PlayerSelectScene(GameComponent component, GraphicsConfiguration gc){
        this.component = component;
        fadeInTime = maxFadeInTime;
        fgClouds = new ArrayList<Cloud>();
        bgClouds = new ArrayList<Cloud>();
    }

    public void init(){
//        Art.startMusic(4);

        for (int i = 0; i < 160; i++){
            int x = (int)(Math.random()*360-20);
            int y = (int)(Math.random()*280-20);
            int w = (int)(Math.random()*60+40);
            float d = (float)(Math.random()-0.5f);
            d += Math.abs(d)*0.5f;
            bgClouds.add(new Cloud(new Rectangle2D.Double(x, y, w, 16), d));
        }

        for (int i = 0; i < 160; i++){
            int x = (int)(Math.random()*360-20);
            int y = (int)(Math.random()*280-20);
            int w = (int)(Math.random()*60+40);
            float d = (float)((Math.random()*2)-1);
            d += Math.abs(d)*0.5f;
            fgClouds.add(new Cloud(new Rectangle2D.Double(x, y, w, 16), d));
        }
    }

    public void render(Graphics2D g, float alpha){
        //clear bg
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, 320, 240);

        Color cloudColor = new Color(0.8f, 0.8f, 0.8f, 0.2f);

        //draw background clouds
        g.setColor(cloudColor);
        for (Cloud cloud: bgClouds) g.fillRoundRect((int)cloud.bounds.x, (int)cloud.bounds.y, (int)cloud.bounds.width, (int)cloud.bounds.height, 16, 16);

        //draw pickables
        for (PickablePlayer pickable: PickablePlayer.values()) pickable.render(g, focusedPickable == pickable);

        //draw foreground clouds
        g.setColor(cloudColor);
        for (Cloud cloud: fgClouds) g.fillRoundRect((int)cloud.bounds.x, (int)cloud.bounds.y, (int)cloud.bounds.width, (int)cloud.bounds.height, 16, 16);

        //draw portraits background
        Rectangle portraitsBounds = new Rectangle(30-3, 208-3, 272+6, 60);
        g.setColor(new Color(0.0f, 0f, 0f, 0.6f));
        g.fillRoundRect(portraitsBounds.x, portraitsBounds.y, portraitsBounds.width, portraitsBounds.height, 8, 8);
        g.setColor(Color.WHITE);
        g.drawRoundRect(portraitsBounds.x, portraitsBounds.y, portraitsBounds.width, portraitsBounds.height, 8, 8);

        //draw player portraits
        for (PickablePlayer pickablePlayer: PickablePlayer.values()){
            SpriteDefinitions player = pickablePlayer.getPlayerDef();
            Rectangle bounds = pickablePlayer.getPortraitBounds();
            Image portraitImage = Art.portraits[player.getPortraitXPic()][player.getPortraitYPic()];
            if (focusedPickable != pickablePlayer) portraitImage = ImageUtils.tintImage(portraitImage, Color.BLACK, null);
            g.drawImage(portraitImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
        }

        //draw player info
        if (focusedPickable != null){
            SpriteDefinitions player = focusedPickable.getPlayerDef();
            int xo = 200, yo = 16;

            //draw backgrounds
            Rectangle attrsBounds = new Rectangle(xo-3, yo-3, 116, 60);
            g.setColor(new Color(0.5f, 0f, 0f, 0.4f));
            g.fillRoundRect(attrsBounds.x, attrsBounds.y, attrsBounds.width, attrsBounds.height, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(attrsBounds.x, attrsBounds.y, attrsBounds.width, attrsBounds.height, 8, 8);

            Rectangle abilityBounds = new Rectangle(xo-3, yo+18*3+8-3, 116, 78);
            g.setColor(new Color(0f, 0f, 0.5f, 0.4f));
            g.fillRoundRect(abilityBounds.x, abilityBounds.y, abilityBounds.width, abilityBounds.height, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(abilityBounds.x, abilityBounds.y, abilityBounds.width, abilityBounds.height, 8, 8);

            Rectangle skillsBounds = new Rectangle(xo-3, yo+18*8-5, 116, 40);
            g.setColor(new Color(0f, 0.5f, 0f, 0.4f));
            g.fillRoundRect(skillsBounds.x, skillsBounds.y, skillsBounds.width, skillsBounds.height, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(skillsBounds.x, skillsBounds.y, skillsBounds.width, skillsBounds.height, 8, 8);

            //name
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g.setColor(Color.BLACK);
            g.drawString(player.name(), xo+16+37-g.getFontMetrics().stringWidth(player.name())/2+1, yo+16+1);
            g.setColor(Color.WHITE);
            g.drawString(player.name(), xo+16+37-g.getFontMetrics().stringWidth(player.name())/2, yo+16);

            //text setup
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.setColor(Color.WHITE);
            int to = 12;

            //hp and speed
            g.drawString("HP", xo-6+32, yo+18+to);
            g.drawString(""+player.getMaxHp(), xo+18+32, yo+18+to);
            g.drawString("Spd", xo-10+32, yo+18*2+to);
            g.drawString(""+player.getSpeed(), xo+18+32, yo+18*2+to);

            //abilities
            Ability a1 = player.getInitialAbility1(), a2 = player.getInitialAbility2(), a3 = player.getInitialAbility3(), aa = player.getInitialAutoAbility();
            g.drawImage(a1.getBigIcon(), xo, yo+18*3+9, 16, 16, null);
            g.drawString(a1.getName(), xo+18, yo+18*3+to+9);
            g.drawImage(a2.getBigIcon(), xo, yo+18*4+9, 16, 16, null);
            g.drawString(a2.getName(), xo+18, yo+18*4+to+9);
            g.drawImage(a3.getBigIcon(), xo, yo+18*5+9, 16, 16, null);
            g.drawString(a3.getName(), xo+18, yo+18*5+to+9);
            g.drawImage(aa.getBigIcon(), xo, yo+18*6+9, 16, 16, null);
            g.drawString(aa.getName(), xo+18, yo+18*6+to+9);

            //skills
            int syo = 0;
            for (Skills skill: player.getSkills()){
                g.drawImage(skill.getIcon(), xo, yo+18*8+syo, 16, 16, null);
                g.drawString(skill.getName(), xo+18, yo+18*8+syo+to);
                syo += 16;
            }

        }

        //fade in/out
        if (fadeInTime > 0){
            float fade = fadeInTime/(float)maxFadeInTime;
            g.setColor(new Color(0f, 0f, 0f, fade));
            g.fillRect(0, 0, 320, 240);
        }
        if (fadeOutTime > 0){
            float fade = (maxFadeOutTime-fadeOutTime)/(float)maxFadeOutTime;
            g.setColor(new Color(0f, 0f, 0f, fade));
            g.fillRect(0, 0, 320, 240);
        }
    }

    private boolean wasDown = true;
    public void tick(){
        //check if we're mouse-hovering over anything
        Point mp = component.getMousePosition();
        PickablePlayer hoveredPickable = null;
        if (mp != null && fadeInTime == 0 && fadeOutTime == 0){
            mp.x /= 2;
            mp.y /= 2;
            for (PickablePlayer pickablePlayer: PickablePlayer.values())
                if (pickablePlayer.getBounds().contains(mp))
                    hoveredPickable = pickablePlayer;
        }
        focusedPickable = hoveredPickable;

        //tick clouds
        for (Cloud cloud: new ArrayList<Cloud>(bgClouds)){
            cloud.bounds.x += cloud.dir;
            if (cloud.dir > 0 && cloud.bounds.x > 320){
                bgClouds.remove(cloud);

                int y = (int)(Math.random()*280-20);
                int w = (int)(Math.random()*60+40);
                float d = (float)((Math.random()*2)-1);
                d += Math.abs(d)*0.5f;
                int x = (d > 0) ? -w : 320;
                bgClouds.add(new Cloud(new Rectangle2D.Double(x, y, w, 16), d));
            }
        }

        for (Cloud cloud: new ArrayList<Cloud>(fgClouds)){
            cloud.bounds.x += cloud.dir;
            if (cloud.dir > 0 && cloud.bounds.x > 320){
                fgClouds.remove(cloud);

                int y = (int)(Math.random()*280-20);
                int w = (int)(Math.random()*60+40);
                float d = (float)((Math.random()*2)-1);
                d += Math.abs(d)*0.5f;
                int x = (d > 0) ? -w : 320;
                fgClouds.add(new Cloud(new Rectangle2D.Double(x, y, w, 16), d));
            }
        }

        //if we clicked on something, fade out and lock selected option
        if (!wasDown && GameComponent.mouse[1] && focusedPickable != null){
            selectedPickable = focusedPickable;
            fadeOutTime = maxFadeOutTime;
        }

        //clear this out
        wasDown = GameComponent.mouse[1];

        //fading in
        if (fadeInTime > 0){
            fadeInTime--;
        }

        //fading out
        if (fadeOutTime > 0){
            fadeOutTime--;
            if (fadeOutTime == 0){
                component.playerSelected(selectedPickable.getPlayerDef());
                component.toScene(GameComponent.Scenes.Intro2);

                //apply potential skill
                for (Skills skill: selectedPickable.getPlayerDef().getSkills())
                    if (skill == Skills.Potential)
                        component.getGameState().setSpareAffinity(selectedPickable.getPlayerDef().getInitialSpareAffinity());
            }
        }
    }

    public float getX(float alpha){ return 0; }
    public float getY(float alpha){ return 0; }

    protected boolean isMouseOverHotspot(Point mouseLocation){
        for (PickablePlayer pickablePlayer: PickablePlayer.values())
            if (pickablePlayer.getBounds().contains(mouseLocation))
                return true;
        return false;
    }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (mousePosition == null) return;
        if (fadeOutTime > 0) return;

        Image cursor = Art.consoleIcons16x16[3][8];
        if (focusedPickable == null) cursor = ImageUtils.grayscaleImage(cursor, null);
        g.drawImage(cursor, -16+mousePosition.x/2, mousePosition.y/2, null);
    }
}

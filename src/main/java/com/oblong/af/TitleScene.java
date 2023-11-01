package com.oblong.af;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.models.Scene;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class TitleScene extends Scene {

    private static final int bgFadeTick = 4, logoFadeTick = 12, optionsFadeTick = 20;

    private enum Option {
        LoadGame, NewGame, Credits, Help, QuitGame;
        public Rectangle getBounds(){ return new Rectangle(320-116, 240-100+16*ordinal(), 96, 16); }
    }

	private GameComponent component;
    private int tick;
    private int fadeOutTime = 0, maxFadeOutTime = 8;
    private Option selectedOption = null;
    
    public TitleScene(GameComponent component, GraphicsConfiguration gc){
        this.component = component;
    }

    public void init(){
        Art.startMusic("Mallorca-Albeniz.mid");
    }

    public void render(Graphics2D g, float alpha){
        //clear bg
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 320, 240);

        //background
        if (tick >= bgFadeTick){
            Image bgImage = Art.titleBackground320x240[0][0];
            double fade = ((tick-bgFadeTick)/10d);
            if (fade > 1d) fade = 1d;
            if (fade != 1d) bgImage = ImageUtils.fadeImage(bgImage, null, fade);
            g.drawImage(bgImage, 0, 0, null);
        }

        //logo
        if (tick >= logoFadeTick){
            Image logoImage = Art.titleLogo161x126[0][0];
            double fade = ((tick-logoFadeTick)/20d);
            if (fade > 1d) fade = 1d;
            if (fade != 1d) logoImage = ImageUtils.fadeImage(logoImage, null, fade);
            g.drawImage(logoImage, 4, 240 - 132 - 4, null);
        }

        //options
        if (tick >= optionsFadeTick){
            for (Option option: Option.values()){
                int xPic = (selectedOption != null && selectedOption.ordinal() == option.ordinal()) ? 1 : 0;
                int yPic = option.ordinal();
                Image optionImage = Art.titleOptions96x16[xPic][yPic];
                double fade = ((tick-optionsFadeTick)/20d);
                if (fade > 1d) fade = 1d;
                if (fade != 1d) optionImage = ImageUtils.fadeImage(optionImage, null, fade);
                g.drawImage(optionImage, option.getBounds().x, option.getBounds().y, null);
            }
        }

        //fade out
        if (fadeOutTime > 0){
            float fade = (maxFadeOutTime-fadeOutTime)/(float)maxFadeOutTime;
            g.setColor(new Color(0f, 0f, 0f, fade));
            g.fillRect(0, 0, 320, 240);
        }
    }

    private boolean wasDown = true;
    public void tick(){
    	tick++;

        //check if we're mouse-hovering over anything
        Option hoveredOption = null;
        Point mp = component.getMousePosition();
        if (mp != null && tick > optionsFadeTick && fadeOutTime == 0){
            mp.x /= 2;
            mp.y /= 2;
            for (Option option: Option.values())
                if (option.getBounds().contains(mp))
                    hoveredOption = option;
        }
        if (fadeOutTime == 0 && selectedOption != hoveredOption){
            selectedOption = hoveredOption;
            sound.play(Art.getSample("tick.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
        }

        //if we clicked on something, fade out and lock selected option
        if (!wasDown && GameComponent.mouse[1] && selectedOption != null){
            fadeOutTime = maxFadeOutTime;
        }

        //clear this out
        if (GameComponent.mouse[1]) wasDown = false;

        //fading out
        if (fadeOutTime > 0){
            fadeOutTime--;
            if (fadeOutTime == 0){
                switch (selectedOption) {
                    case LoadGame: loadGame(); break;
                    case NewGame: newGame(); break;
                    case Credits: credits(); break;
                    case Help: help(); break;
                    case QuitGame: quitGame(); break;
                }
            }
        }
    }

    public float getX(float alpha){ return 0; }
    public float getY(float alpha){ return 0; }

    protected boolean isMouseOverHotspot(Point mouseLocation){
        for (Option option: Option.values())
            if (option.getBounds().contains(mouseLocation))
                return true;
        return false;
    }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (mousePosition == null) return;
        if (fadeOutTime > 0) return;

        Image cursor = Art.consoleIcons16x16[3][8];
        if (selectedOption == null) cursor = ImageUtils.grayscaleImage(cursor, null);
        g.drawImage(cursor, mousePosition.x/2, mousePosition.y/2, null);
    }

    private void loadGame(){ component.toScene(GameComponent.Scenes.LoadGame); }
    private void credits(){ component.toScene(GameComponent.Scenes.Credits); }
    private void newGame(){ component.toScene(GameComponent.Scenes.Intro1); }
    private void help(){ component.toScene(GameComponent.Scenes.HowToPlay); }
    private void quitGame(){ System.exit(0); }
}

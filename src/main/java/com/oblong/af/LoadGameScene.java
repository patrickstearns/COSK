package com.oblong.af;

import com.oblong.af.models.Scene;
import com.oblong.af.models.console.LoadGameConsole;
import com.oblong.af.util.Art;

import java.awt.*;

public class LoadGameScene extends Scene {

    private static final int bgFadeTick = 4;

	private GameComponent component;
    private int tick = 0, fadeOutTime = 0, maxFadeOutTime = 8, fadeInTime = 8, maxFadeInTime = 8;
    private LoadGameConsole console;
    private boolean cancelled = false;

    public LoadGameScene(GameComponent component, GraphicsConfiguration gc){
        this.component = component;
        console = new LoadGameConsole(this);
    }

    public void init(){
//        Art.startMusic(4);
    }

    public void render(Graphics2D g, float alpha){
        //clear bg
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 320, 240);

        //background
        if (tick >= bgFadeTick){
            Image bgImage = Art.loadGameBackground;
            g.drawImage(bgImage, 0, 0, null);
        }

        //paint console stuff
        if (console != null) console.paint(g);

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
    	tick++;

        if (console != null){
            console.tick();
        }

        //clear this out
        if (!GameComponent.mouse[1]) wasDown = false;

        if (fadeInTime > 0){
            fadeInTime--;
            if (fadeInTime == 0 && console != null){
                console.showLoadFileMenu(null);
            }
        }

        //fading out
        if (fadeOutTime > 0){
            fadeOutTime--;
            if (fadeOutTime == 0){
                if (cancelled) component.toScene(GameComponent.Scenes.Title);
                else component.toScene(GameComponent.Scenes.Hub);
            }
        }
    }

    public float getX(float alpha){ return 0; }
    public float getY(float alpha){ return 0; }

    public boolean isMouseOverHotspot(Point mouseLocation){ return false; }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (mousePosition == null) return;
        if (fadeOutTime > 0) return;

        if (console != null && console.getFocusedMenu() != null){
            if (GameComponent.mouse[1] && !wasDown){
                console.mouseClicked(new Point(mousePosition.x / 2, mousePosition.y / 2));
                wasDown = true;
            }
            console.mouseMoved(new Point(mousePosition.x/2, mousePosition.y/2));
        }

        Image cursor = Art.consoleIcons16x16[3][8];
        g.drawImage(cursor, mousePosition.x/2, mousePosition.y/2, null);
    }

    public void ok(){
        fadeOutTime = maxFadeOutTime;
        cancelled = false;
    }

    //called by subscreen
    public void cancel(){
        fadeOutTime = maxFadeOutTime;
        cancelled = true;
    }

    public void error(){

    }
}

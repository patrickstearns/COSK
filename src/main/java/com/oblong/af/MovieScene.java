package com.oblong.af;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.models.Scene;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

public class MovieScene extends Scene {

    public static enum NextScene { PlayerSelect, StartGame, LairOpened, RiftOpened, Title }

	private GameComponent component;
    private int fadeOutTime = 0, maxFadeOutTime = 8;
    private int fadeInTime = 0, maxFadeInTime = 8;
    private Integer focusedImageIndex = null, selectedImageIndex = 0;
    private Image backgroundImage;
    private Image[] images;
    private GameComponent.Scenes nextScene;
    private boolean backable, skippable;
    private String musicKey;

    public MovieScene(GameComponent component, GraphicsConfiguration gc, Image[] images, Image backgroundImage,
                      GameComponent.Scenes nextScene, boolean backable, boolean skippable, String musicKey){
        this.component = component;
        fadeInTime = maxFadeInTime;
        this.images = images;
        this.backgroundImage = backgroundImage;
        this.nextScene = nextScene;
        this.backable = backable;
        this.skippable = skippable;
        this.musicKey = musicKey;
    }

    public void init(){
        if (musicKey != null)
            Art.startMusic(musicKey);
    }

    private Rectangle getBounds(int index){ return new Rectangle((int)(160+16*(index-images.length/2f)), 220, 16, 16); }
    private Rectangle getPrevBounds(){ 
        Rectangle r = getBounds(0);
        r.translate(-16, 0);
        return r;
    }
    private Rectangle getNextBounds(){
        Rectangle r = getBounds(images.length-1);
        r.translate(16, 0);
        return r;
    }
    private Rectangle getSkipBounds(){ return new Rectangle(294, 220, 16, 16); }
    private Rectangle getBackBounds(){ return new Rectangle(10, 220, 16, 16); }

    public void render(Graphics2D g, float alpha){
        //clear bg
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 320, 240);

        //background
        g.drawImage(backgroundImage, 0, 0, null);

        //foreground
        g.drawImage(images[selectedImageIndex], 0, 0, null);

        //options
        Image prevImage = Art.consoleIcons16x16[0][9];
        if (selectedImageIndex == -1) prevImage = Art.consoleIcons16x16[0][11];
        else if (focusedImageIndex != null && focusedImageIndex == -1) prevImage = Art.consoleIcons16x16[1][9];
        Rectangle prevBounds = getPrevBounds();
        g.drawImage(prevImage, prevBounds.x, prevBounds.y, null);

        Image nextImage = Art.consoleIcons16x16[2][9];
        if (selectedImageIndex == -2) nextImage = Art.consoleIcons16x16[1][11];
        else if (focusedImageIndex != null && focusedImageIndex == -2) nextImage = Art.consoleIcons16x16[3][9];
        Rectangle nextBounds = getNextBounds();
        g.drawImage(nextImage, nextBounds.x, nextBounds.y, null);

        if (backable && selectedImageIndex != images.length-1){
            Image backImage = Art.consoleIcons16x16[2][11];
            if (focusedImageIndex != null && focusedImageIndex == -3) backImage = Art.consoleIcons16x16[3][11];
            Rectangle backBounds = getBackBounds();
            g.drawImage(backImage, backBounds.x, backBounds.y, null);
        }

        if (skippable && selectedImageIndex != images.length-1){
            Image skipImage = Art.consoleIcons16x16[2][12];
            if (focusedImageIndex != null && focusedImageIndex == -4) skipImage = Art.consoleIcons16x16[3][12];
            Rectangle skipBounds = getSkipBounds();
            g.drawImage(skipImage, skipBounds.x, skipBounds.y, null);
        }

        if (selectedImageIndex == images.length-1){
            Image contImage = Art.consoleIcons16x16[0][12];
            if (focusedImageIndex != null && focusedImageIndex == -4) contImage = Art.consoleIcons16x16[1][12];
            Rectangle contBounds = getSkipBounds();
            g.drawImage(contImage, contBounds.x, contBounds.y, null);
        }

        for (int i = 0; i < images.length; i++){
            Image optionImage;
            if (i == selectedImageIndex){
                if (focusedImageIndex != null && i == focusedImageIndex) optionImage = Art.consoleIcons16x16[3][10];
                else optionImage = Art.consoleIcons16x16[2][10];
            }
            else{
                if (focusedImageIndex != null && i == focusedImageIndex) optionImage = Art.consoleIcons16x16[1][10];
                else optionImage = Art.consoleIcons16x16[0][10];
            }

            Rectangle bounds = getBounds(i);
            g.drawImage(optionImage, bounds.x, bounds.y, null);
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
        Integer hoveredOption = null;
        Point mp = component.getMousePosition();
        if (mp != null && fadeInTime == 0 && fadeOutTime == 0){
            mp.x /= 2;
            mp.y /= 2;
            for (int i = 0; i < images.length; i++)
                if (getBounds(i).contains(mp))
                    hoveredOption = i;
            if (getPrevBounds().contains(mp)) hoveredOption = -1;
            if (getNextBounds().contains(mp)) hoveredOption = -2;
            if (getBackBounds().contains(mp) && backable && selectedImageIndex != images.length-1) hoveredOption = -3;
            if (getSkipBounds().contains(mp) && skippable && selectedImageIndex != images.length-1) hoveredOption = -4;
            if (getSkipBounds().contains(mp) && selectedImageIndex == images.length-1) hoveredOption = -4;
        }
        if (fadeInTime == 0 && fadeOutTime == 0) focusedImageIndex = hoveredOption;

        //if we clicked on something, fade out and lock selected option
        if (!wasDown && GameComponent.mouse[1] && fadeOutTime == 0){
            if (focusedImageIndex != null){
                if (focusedImageIndex == -1 && selectedImageIndex > 0)
                    selectedImageIndex = selectedImageIndex-1;
                else if (focusedImageIndex == -2 && selectedImageIndex != images.length-1)
                    selectedImageIndex = selectedImageIndex+1;
                else if (focusedImageIndex == -3) fadeOutTime = maxFadeOutTime;
                else if (focusedImageIndex == -4) fadeOutTime = maxFadeOutTime;
                else if (focusedImageIndex >= 0) selectedImageIndex = focusedImageIndex;
            }
            else {
                if (selectedImageIndex != images.length-1) selectedImageIndex = selectedImageIndex+1;
                else fadeOutTime = maxFadeOutTime;
            }

            sound.play(Art.getSample("tick.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
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
            if (fadeOutTime == 0)
                component.toScene(nextScene);
        }
    }

    public float getX(float alpha){ return 0; }
    public float getY(float alpha){ return 0; }

    protected boolean isMouseOverHotspot(Point mouseLocation){
        for (int i = 0; i < images.length; i++)
            if (getBounds(i).contains(mouseLocation))
                return true;
        if (getPrevBounds().contains(mouseLocation)) return true;
        if (getNextBounds().contains(mouseLocation)) return true;
        if (getBackBounds().contains(mouseLocation)) return true;
        if (getSkipBounds().contains(mouseLocation)) return true;
        return false;
    }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (mousePosition == null) return;
        if (fadeOutTime > 0) return;

        Image cursor = Art.consoleIcons16x16[3][8];
        if (selectedImageIndex == null) cursor = ImageUtils.grayscaleImage(cursor, null);
        g.drawImage(cursor, -16+mousePosition.x/2, mousePosition.y/2, null);
    }
}

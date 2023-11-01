package com.oblong.af.sprite;

import com.mojang.sonar.SoundSource;
import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.level.Block;
import com.oblong.af.level.SpriteTemplate;
import com.oblong.af.models.ActorMessages;
import com.oblong.af.models.Facing;
import com.oblong.af.sprite.decorator.AbstractSpriteDecorator;
import com.oblong.af.util.ImageUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A Sprite is "something" that's drawn on the screen (usually an image), has a location, can move, make sounds and
 *   do messaging.
 * Standard rendering supports half-sized rendering, desaturation, tinting, outlining.
 */

public abstract class Sprite implements SoundSource {

    private static final Color FOCUSED_COLOR = new Color(0f, 0.4f, 1f, 1f);

	public static Comparator<Sprite> spriteRenderingComparator = new Comparator<Sprite>(){
		public int compare(Sprite s1, Sprite s2){
            if (s1.getRenderingOrder() != s2.getRenderingOrder()) return s1.getRenderingOrder()-s2.getRenderingOrder();
            if (s1.getLayer() != s2.getLayer()) return s1.getLayer().ordinal()-s2.getLayer().ordinal();
			if (s1.getY()-s1.getHeight()/2 != s2.getY()-s2.getHeight()/2) return (int)(s1.getY()-s1.getHeight()/2-(s2.getY()-s2.getHeight()/2));
			else return s1.hashCode()-s2.hashCode();
		}
	};

    private String id;
    private boolean active, visible, halfSize, desaturated;
    private int tick, width, height, oxPic, oyPic, xPic, yPic, wPic, hPic, xPicO, yPicO, renderingOrder;
    private Area.Layer layer;
    private float xOld, yOld, x, y, xa, ya, fadeRatio;
    private double heading; //which way we're moving
    private Color tintColor, outlineColor;
    private Image[][] sheet;
    private SpriteTemplate spriteTemplate;
    private AreaScene scene;
    private Facing facing; //which way sprite is "facing"
    private ActorMessages messages;
    private List<AbstractSpriteDecorator> decorators;

    protected Sprite(String id, AreaScene scene){
        setId(id);
    	setScene(scene);
    	setWidth(15);
    	setHeight(12);
    	setWPic(16);
    	setHPic(32);
    	setVisible(true);
    	setHalfSize(false);
        setRenderingOrder(0);
        setMessages(new ActorMessages());
        setFadeRatio(1f);
        decorators = new ArrayList<AbstractSpriteDecorator>();
        setLayer(Area.Layer.Main);
    }

    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    public AreaScene getScene(){ return scene; }
	public void setScene(AreaScene scene){ this.scene = scene; }

	public float getXOld(){ return xOld; }
	public void setXOld(float old){ xOld = old; }

	public float getYOld(){ return yOld; }
	public void setYOld(float old){ yOld = old; }

    public int getOxPic(){ return oxPic; }
    public void setOxPic(int oxPic){ this.oxPic = oxPic; }

    public int getOyPic(){ return oyPic; }
    public void setOyPic(int oyPic){ this.oyPic = oyPic; }

    public float getX(float alpha){ return (getXOld()+(getX()-getXOld())*alpha)-xPicO; }
	public float getX(){ return x; }
	public void setX(float x){ this.x = x; }

    public float getY(float alpha){ return (getYOld()+(getY()-getYOld())*alpha)-yPicO; }
	public float getY(){ return y; }
	public void setY(float y){ this.y = y; }

	public float getXa(){ return xa; }
	public void setXa(float xa){ this.xa = xa; }

	public float getYa(){ return ya; }
	public void setYa(float ya){ this.ya = ya; }

    public int getWidth(){ return width; }
	public void setWidth(int width){ this.width = width; }

	public int getHeight(){ return height; }
	public void setHeight(int height){ this.height = height; }

	public int getXPic(){ return xPic; }
	public void setXPic(int pic){ xPic = pic; }

	public int getYPic(){ return yPic; }
	public void setYPic(int pic){ yPic = pic; }

	public int getWPic(){ return wPic; }
	public void setWPic(int pic){ wPic = pic; }

	public int getHPic(){ return hPic; }
	public void setHPic(int pic){ hPic = pic; }

	public int getXPicO(){ return xPicO; }
	public void setXPicO(int picO){ xPicO = picO; }

	public int getYPicO(){ return yPicO; }
	public void setYPicO(int picO){ yPicO = picO; }

	public Area.Layer getLayer(){ return layer; }
	public void setLayer(Area.Layer layer){ this.layer = layer; }

    public int getRenderingOrder(){ return renderingOrder; }
    public void setRenderingOrder(int renderingOrder){ this.renderingOrder = renderingOrder; }

    public boolean isVisible(){ return visible; }
	public void setVisible(boolean visible){ this.visible = visible; }

	public boolean isHalfSize(){ return halfSize; }
	public void setHalfSize(boolean halfSize){ this.halfSize = halfSize; }

    public ActorMessages getMessages(){ return messages; }
    public void setMessages(ActorMessages messages){ this.messages = messages; }

    public Image[][] getSheet(){ return sheet; }
	public void setSheet(Image[][] sheet){ this.sheet = sheet; }

	public SpriteTemplate getSpriteTemplate(){ return spriteTemplate; }
	public void setSpriteTemplate(SpriteTemplate spriteTemplate){ this.spriteTemplate = spriteTemplate; }

	public Facing getFacing(){ return facing; }
	public void setFacing(Facing facing){ this.facing = facing; }

    public double getHeading(){ return heading; }
    public void setHeading(double heading){ this.heading = heading; }

    public List<AbstractSpriteDecorator> getDecorators(){ return decorators; }
    public void addDecorator(AbstractSpriteDecorator decorator){ decorators.add(decorator); }
    public void removeDecorator(AbstractSpriteDecorator decorator){ decorators.remove(decorator); }

    public float getFadeRatio(){ return fadeRatio; }
    public void setFadeRatio(float fadeRatio){ this.fadeRatio = fadeRatio; }

    public Color getTintColor(){ return tintColor; }
    public void setTintColor(Color tintColor){ this.tintColor = tintColor; }

    public Color getOutlineColor(){ return outlineColor; }
    public void setOutlineColor(Color outlineColor){ this.outlineColor = outlineColor; }

    public boolean isDesaturated(){ return desaturated; }
    public void setDesaturated(boolean desaturated){ this.desaturated = desaturated; }

    public int getTick(){ return tick; }
    protected void setTick(int tick){ this.tick = tick; }

    public final void tick(){
        setXOld(getX());
        setYOld(getY());

        tick++;
        setTintColor(null); //clear these out
        setOutlineColor(null);
        setFadeRatio(1f);
        for (AbstractSpriteDecorator decorator: new ArrayList<AbstractSpriteDecorator>(decorators)) decorator.tick(this);
        if (isClickable() && getImageFootprint().contains(getScene().getApparentMousePosition())) setOutlineColor(FOCUSED_COLOR);
        move();
    }

    public void tickNoMove(){
    	setXOld(getX());
    	setYOld(getY());
    }

    public void move(){
        setX(getX()+getXa());
        setY(getY()+getYa());
    }

    protected boolean move(float xa, float ya){ return true; }

    public Rectangle getFootprint(){ return new Rectangle((int)getX()-getWidth()/2, (int)getY()-getHeight(), getWidth(), getHeight()); }
    public Rectangle2D.Float getFootprint2D(){ return new Rectangle2D.Float(getX()-getWidth()/2f, getY()-getHeight(), getWidth(), getHeight()); }
    public Rectangle getImageFootprint(){ return getImageFootprint(1f); }
    public Rectangle getImageFootprint(float alpha){
        //base image location
        int xPixel = (int)(getXOld()+(getX()-getXOld())*alpha)-xPicO;
        int yPixel = (int)(getYOld()+(getY()-getYOld())*alpha)-yPicO;

        //if half-sized, shrink proportions
        int dwPic = getWPic(), dhPic = getHPic();
        if (isHalfSize()){
            dwPic *= 0.75;
            dhPic *= 0.75;
        }

        //line center of image up with x, yPixel w/bottom of image
        xPixel -= dwPic/2;
        yPixel -= dhPic;

        if (outlineColor != null){
            xPixel-=1;
            yPixel-=1;
            dwPic += 2;
            dhPic += 2;
        }

        return new Rectangle(xPixel, yPixel, dwPic, dhPic);
    }

    public void render(Graphics2D og, float alpha){
        if (!visible) return;

        Rectangle imageBounds = getImageFootprint(alpha);
        Image image = getSheet()[getXPic()][getYPic()];
        if (desaturated) image = ImageUtils.grayscaleImage(image, null);
        if (fadeRatio != 1f)
            try{ image = ImageUtils.fadeImage(image, null, fadeRatio); }
            catch(Exception e){}
        if (tintColor != null)
            try{ image = ImageUtils.tintImage(image, tintColor, null); }
            catch(Exception e){}
        if (outlineColor != null) image = ImageUtils.outlineImage(image, outlineColor);

        og.drawImage(image, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
    }

    protected List<Block.Trait> getTileBehaviorsUnderSprite(){
        if (getScene() == null) return new ArrayList<Block.Trait>();
        return getScene().areaGroup.getCurrentArea().getBehavior(getScene().areaGroup.getBlock(
                (int)(getX()/16d),
                (int)((getY()-1)/16d),
                getLayer())
                .blockId);
    }

    public void spawned(){
        for (String message: getMessages().getOnSpawnMessages())
            getScene().message(this, message);
    }

    public boolean isActive(){ return active; }
    public void setActive(boolean active){
        boolean oldActive = this.active;
        this.active = active;
        if (active != oldActive){
            if (active){
                for (String message: getMessages().getOnActivationMessages())
                    getScene().message(this, message);
            }
            else{
                for (String message: getMessages().getOnDeactivationMessages())
                    getScene().message(this, message);
            }
        }
    }

    public boolean isClickable(){
        return (!active && getMessages().getActivatingMessages().contains(AreaScene.PLAYER_CLICK_MESSAGE)) ||
                (active && getMessages().getDeactivatingMessages().contains(AreaScene.PLAYER_CLICK_MESSAGE));
    }

    public void receiveMessage(Sprite source, String message){
        if (!active && getMessages().getActivatingMessages().contains(message)) setActive(true);
        else if (active && getMessages().getDeactivatingMessages().contains(message)) setActive(false);
    }

}

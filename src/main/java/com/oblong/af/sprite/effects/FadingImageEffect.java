package com.oblong.af.sprite.effects;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;

public class FadingImageEffect extends Effect {

	private boolean animated;
	
	public static FadingImageEffect createProtectEffect(Prop target){ return new FadingImageEffect(target, false, 0, 0); }
	public static FadingImageEffect createShellEffect(Prop target){ return new FadingImageEffect(target, false, 1, 0); }
	public static FadingImageEffect createSlowEffect(Prop target){ return new FadingImageEffect(target, false, 0, 1); }
	public static FadingImageEffect createHasteEffect(Prop target){ return new FadingImageEffect(target, false, 1, 1); }
	public static FadingImageEffect createBoostEffect(Prop target){ return new FadingImageEffect(target, false, 2, 0); }
	public static FadingImageEffect createGrenadeEffect(Prop target){ return new FadingImageEffect(target, false, 0, 2); }
	public static FadingImageEffect createIceEffect(Prop target){ return new FadingImageEffect(target, false, 3, 0); }
	public static FadingImageEffect createBlackMagicEffect(Prop target){ return new FadingImageEffect(target, false, 2, 1); }
	public static FadingImageEffect createWhiteMagicEffect(Prop target){ return new FadingImageEffect(target, false, 3, 1); }

	public FadingImageEffect(Prop target, boolean animated, int xPic, int yPic){
    	super(target.getScene(), 0, 0, target.getLayer(), 0, 0, xPic, yPic, 32, 32, 20, false);
        setSheet(Art.effects32x32);
        setX(target.getX()+target.getWidth()/2-2); //dunno why I need the 2 but whatever
        setY(target.getY()+target.getHeight()/2);
        this.animated = animated;
        setLayer(Area.Layer.Upper);
    }

    public FadingImageEffect(AreaScene scene, int x, int y, boolean animated, int xPic, int yPic){
        super(scene, 0, 0, Area.Layer.Main, 0, 0, xPic, yPic, 32, 32, 20, false);
        setSheet(Art.effects32x32);
        setX(x-2); //dunno why I need the 2 but whatever
        setY(y);
        this.animated = animated;
        setLayer(Area.Layer.Upper);
    }

    public void move(){
        setFadeRatio(((float)(maxTick-getTick()))/(float)maxTick);
       if (animated && ((maxTick-getTick())+1)%5 == 0) setXPic(getXPic()+1);
        if (getXPic() > 3) setXPic(3);
    }

}
package com.oblong.af.util;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ImageObserver;
import java.util.Hashtable;

public class ImageUtils {

    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
    public static final Color HALF_ORANGE = new Color(1f, 0.7f, 0f, 0.5f);
    public static final Color HALF_YELLOW = new Color(1f, 1f, 0f, 0.5f);
    public static final Color HALF_WHITE = new Color(1f, 1f, 1f, 0.5f);

    public static Color calculateCycleColor(int tick, Color[] colors, int cycleRate){
        if (colors.length == 1 || cycleRate == 0) return colors[0];

        float cyclePos = (float)colors.length*((float)(tick%cycleRate)/(float)cycleRate);
        int ind1 = (int)Math.floor(cyclePos);
        int ind2 = (int)Math.ceil(cyclePos);
        Color col1 = colors[ind1];
        Color col2 = (ind2 >= colors.length) ? colors[0] : colors[ind2];

        float mixRatio = cyclePos-ind1;
        int r = (int)(mixRatio*col2.getRed()+(1f-mixRatio)*col1.getRed());
        int g = (int)(mixRatio*col2.getGreen()+(1f-mixRatio)*col1.getGreen());
        int b = (int)(mixRatio*col2.getBlue()+(1f-mixRatio)*col1.getBlue());
        int a = (int)(mixRatio*col2.getAlpha()+(1f-mixRatio)*col1.getAlpha());

        return new Color(r, g, b, a);
    }

    public static BufferedImage rotateImage(Image image, ImageObserver io, double radians){
		//now rotate it
		AffineTransformOp rotateOp = new AffineTransformOp(AffineTransform.getRotateInstance(radians, image.getWidth(io)/2, image.getHeight(io)/2), null);
		BufferedImage rotated = rotateOp.filter(ensureBufferedImage(image, io), null);
		return rotated;
	}
	
	private static Hashtable<Image, BufferedImage> grayscaleCache = new Hashtable<Image, BufferedImage>();
	public static BufferedImage grayscaleImage(Image image, ImageObserver io){
		BufferedImage ret = grayscaleCache.get(image);
		if (ret == null){
			ColorConvertOp grayOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			image = ensureBufferedImage(image, io);
			ret = grayOp.filter((BufferedImage)image, null);
			grayscaleCache.put(image, ret);
		}
		return ret;
	}

	public static BufferedImage fadeImage(Image image, ImageObserver io, double fade){
		FadeImageOp fadeOp = new FadeImageOp();
		image = ensureBufferedImage(image, io);
		return fadeOp.filter((BufferedImage)image, null, fade);			
	}
	
	public static BufferedImage flipImage(Image image, ImageObserver io, boolean yAxis, boolean xAxis){

		BufferedImage im = ensureBufferedImage(image, io);
		BufferedImage ret = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = ret.getGraphics();
		
		if (xAxis && yAxis){
			g.drawImage (im,
				0, im.getHeight(), im.getWidth(), 0,
				0, 0, im.getWidth(), im.getHeight(),
				io);
		}
		else if (yAxis){
			g.drawImage (im,
				im.getWidth(), 0, 0, im.getHeight(),
				0, 0, im.getWidth(), im.getHeight(),
				io);
		}
		else if (xAxis){
			g.drawImage (im,
				0, im.getHeight(), im.getWidth(), 0,
				0, 0, im.getWidth(), im.getHeight(),
				io);
		}

		return ret;
	}
	
	public static BufferedImage scaleImage(Image image, ImageObserver io, double xFactor, double yFactor){
		BufferedImage im = ensureBufferedImage(image, io);
		BufferedImage ret = new BufferedImage((int)(im.getWidth()*xFactor), (int)(im.getHeight()*yFactor), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = ret.getGraphics();
		g.drawImage (im, 0, 0, (int)(im.getWidth()*xFactor), (int)(im.getHeight()*yFactor), io);
		return ret;
	}

	public static BufferedImage tintImage(Image image, Color color, ImageObserver io){
		ColorFilterOp op = new ColorFilterOp(color);
		image = ensureBufferedImage(image, io);
		return op.filter((BufferedImage)image, null);			
	}
	
	public static BufferedImage damageImage(Image image, ImageObserver io){ return tintImage(image, Color.RED, io); }
	public static BufferedImage deathImage(Image image, ImageObserver io){ return tintImage(image, Color.WHITE, io); }
	public static BufferedImage healImage(Image image, ImageObserver io){ return tintImage(image, Color.GREEN, io); }

    private static float OUTLINE_PERIOD = 30;
    public static BufferedImage outlineImage(Image image, Color outline){
        BufferedImage src = ensureBufferedImage(image, null);
        BufferedImage src2 = new BufferedImage(image.getWidth(null)+2, image.getHeight(null)+2, BufferedImage.TYPE_4BYTE_ABGR);
        src2.getGraphics().drawImage(src, 1, 1, null);
        OutlineSpriteImageFilter op1 = new OutlineSpriteImageFilter(outline);
        return op1.filter(src2, null);
    }

    public static BufferedImage ensureBufferedImage(Image image, ImageObserver io){
		if (!(image instanceof BufferedImage)){
			BufferedImage buf = new BufferedImage(image.getWidth(io), image.getHeight(io), BufferedImage.TYPE_4BYTE_ABGR);
			buf.getGraphics().drawImage(image, 0, 0, io);
			image = buf;
		}
		return (BufferedImage)image;
	}
}
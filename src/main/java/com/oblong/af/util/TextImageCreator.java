package com.oblong.af.util;

import com.oblong.af.GameComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextImageCreator {

    public final static int COLOR_BLACK = 0;
    public final static int COLOR_RED = 1;
    public final static int COLOR_GREEN = 2;
    public final static int COLOR_BLUE = 3;
    public final static int COLOR_YELLOW = 4;
    public final static int COLOR_MAGENTA = 5;
    public final static int COLOR_CYAN = 6;
    public final static int COLOR_WHITE = 7;
    public final static int COLOR_GRAY = 8;

    private static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();

    private TextImageCreator(){}

    private static String getHashkey(String text, int textColor){ return text+textColor; }
    private static String getHashkey(String text, int textColor, Color outlineColor){ return text+textColor+outlineColor.toString(); }

    public static BufferedImage getTextImage(String text, int textColor){
		String hashkey = getHashkey(text, textColor);
		BufferedImage ret = images.get(hashkey);
		if (ret == null){
			ret = generateTextImage(text, textColor);
			images.put(hashkey, ret);
		}
		return ret;
	}

    public static BufferedImage getOutlinedTextImage(String text, int textColor, Color outlineColor){
        String hashkey = getHashkey(text, textColor, outlineColor);
        BufferedImage ret = images.get(hashkey);
        if (ret == null){
            ret = generateOutlinedTextImage(text, textColor, outlineColor);
            images.put(hashkey, ret);
        }
        return ret;
    }

    private static BufferedImage generateTextImage(String text, int textColor){
        BufferedImage ret = new BufferedImage(text.length()*8, 8, BufferedImage.TYPE_4BYTE_ABGR);
        GameComponent.drawString(ret.getGraphics(), text, 0, 0, textColor);
        return ret;
    }

    private static BufferedImage generateOutlinedTextImage(String text, int textColor, Color outlineColor){
        BufferedImage ret = new BufferedImage(text.length()*8+2, 8+2, BufferedImage.TYPE_4BYTE_ABGR);
        GameComponent.drawString(ret.getGraphics(), text, 1, 1, textColor);
        return ImageUtils.outlineImage(ret, outlineColor);
    }

}

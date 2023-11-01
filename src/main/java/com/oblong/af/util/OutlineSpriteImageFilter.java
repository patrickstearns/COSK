package com.oblong.af.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

public class OutlineSpriteImageFilter implements BufferedImageOp {

    private Color color;
    public OutlineSpriteImageFilter(Color color){
        this.color = color;
    }

    public final BufferedImage filter(BufferedImage source_img, BufferedImage dest_img){
        // If no destination image provided, make one of same form as source
        if (dest_img == null) dest_img = createCompatibleDestImage (source_img, null);

        int width = source_img.getWidth();
        int height= source_img.getHeight();
        int crgb = color.getRGB();

        for (int y=0; y < height; y++) {
            for (int x=0; x < width; x++) {
                int pixel = source_img.getRGB(x, y);
                if (isTransparentOrColor(source_img, x, y, color)){
                    boolean isAdjacent = false;
                    if (x < width-1 && !isTransparentOrColor(source_img, x+1, y, color)) isAdjacent = true;
                    if (x > 0 && !isTransparentOrColor(source_img, x-1, y, color)) isAdjacent = true;
                    if (y < height-1 && !isTransparentOrColor(source_img, x, y+1, color)) isAdjacent = true;
                    if (y > 0 && !isTransparentOrColor(source_img, x, y-1, color)) isAdjacent = true;
                    if (isAdjacent) pixel = crgb;
                }
                dest_img.setRGB(x, y, pixel);
            }
        }
        return dest_img;
    }

    private boolean isTransparentOrColor(BufferedImage source_img, int x, int y, Color c){
        int pixel = source_img.getRGB(x,y);

        // Get the component colors
        int alpha = (pixel >> 24) & 0xff;
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >> 8)  & 0xff;
        int blue  =  pixel        & 0xff;

        return (alpha == 0) || (alpha == c.getAlpha() && red == c.getRed() && green == c.getGreen() && blue == c.getBlue());
    }

    /**
     *  Create a destination image if needed. Must be same width as source
     *  and will by default use the same color model. Otherwise, it will use
     *  the one passed to it.
     **/
    public BufferedImage createCompatibleDestImage (BufferedImage source_img,  ColorModel dest_color_model) {
        // If no color model passed, use the same as in source
        if (dest_color_model == null)
            dest_color_model = source_img.getColorModel ();

        int width = source_img.getWidth ();
        int height= source_img.getHeight ();

        // Create a new image with this color model & raster. Check if the
        // color components already multiplied by the alpha factor.
        return new BufferedImage (
                dest_color_model,
                dest_color_model.createCompatibleWritableRaster (width,height),
                dest_color_model.isAlphaPremultiplied(),
                null);
    }

    public final Rectangle2D getBounds2D (BufferedImage source_img) {
        return source_img.getRaster().getBounds();
    }

    public final Point2D getPoint2D (Point2D source_point, Point2D dest_point) {
        if (dest_point == null) dest_point = new Point2D.Float ();
        dest_point.setLocation (source_point.getX(), source_point.getY());
        return dest_point;
    }

    public final RenderingHints getRenderingHints(){ return null; }
}
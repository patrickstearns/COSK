package com.oblong.af.editor.painter;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;

/**
 * OverlayPainter paints on the main layer, and adjusts it and the tiles around it based on whether and how it borders
 *   tiles that haven't been painted with this painter.
 */

public class WoodenFencePainter implements Painter {

    private int ob;

    public WoodenFencePainter(int ob){
        this.ob = ob;
    }

    public void paint(AreaGroup areaGroup, int x, int y) {
        if (!isPaintedTile(areaGroup.getBlock(x, y, Area.Layer.Main).blockId)){
            paintTile(areaGroup, x, y, newTileId(areaGroup, x, y));
            if (isPaintedTile(areaGroup.getBlock(x-1, y, Area.Layer.Main).blockId))
                paintTile(areaGroup, x-1, y, newTileId(areaGroup, x-1, y));
            if (isPaintedTile(areaGroup.getBlock(x+1, y, Area.Layer.Main).blockId))
                paintTile(areaGroup, x+1, y, newTileId(areaGroup, x+1, y));
            if (isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Main).blockId))
                paintTile(areaGroup, x, y-1, newTileId(areaGroup, x, y-1));
            if (isPaintedTile(areaGroup.getBlock(x, y+1, Area.Layer.Main).blockId))
                paintTile(areaGroup, x, y+1, newTileId(areaGroup, x, y+1));
        }
    }

    private void paintTile(AreaGroup areaGroup, int x, int y, int b){
        areaGroup.setBlock(x, y, Area.Layer.Main, b);
    }

    //noop'd
    public void fill(AreaGroup areaGroup, int x, int y){}

    public int getBaseBlockId(){ return ob; }

    public boolean isPaintedTile(int b){
        return ((b >= ob && b < ob+5) ||
                (b >= ob+16 && b < ob+16+5) ||
                (b >= ob+16*2 && b < ob+16*2+5) ||
                (b >= ob+16*3 && b < ob+16*3+5));
    }

    private int newTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;
        boolean upLeftPainted = false, upRightPainted = false, downLeftPainted = false, downRightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Main).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isPaintedTile(areaGroup.getBlock(x, y+1, Area.Layer.Main).blockId);
        if (x > 0) leftPainted = isPaintedTile(areaGroup.getBlock(x-1, y, Area.Layer.Main).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isPaintedTile(areaGroup.getBlock(x+1, y, Area.Layer.Main).blockId);
        if (x > 0 && y > 0) upLeftPainted = isPaintedTile(areaGroup.getBlock(x-1, y-1, Area.Layer.Main).blockId);
        if (x > 0 && y < areaGroup.getHeight()-1) downLeftPainted = isPaintedTile(areaGroup.getBlock(x-1, y+1, Area.Layer.Main).blockId);
        if (x < areaGroup.getWidth()-1 && y > 0) upRightPainted = isPaintedTile(areaGroup.getBlock(x+1, y-1, Area.Layer.Main).blockId);
        if (x < areaGroup.getWidth()-1 && y < areaGroup.getHeight()-1) downRightPainted = isPaintedTile(areaGroup.getBlock(x+1, y+1, Area.Layer.Main).blockId);

        //then check again on lower layer
        if (y > 0) upPainted |= isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Lower).blockId);
        if (y < areaGroup.getHeight()-1) downPainted |= isPaintedTile(areaGroup.getBlock(x, y+1, Area.Layer.Lower).blockId);
        if (x > 0) leftPainted |= isPaintedTile(areaGroup.getBlock(x-1, y, Area.Layer.Lower).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted |= isPaintedTile(areaGroup.getBlock(x+1, y, Area.Layer.Lower).blockId);
        if (x > 0 && y > 0) upLeftPainted |= isPaintedTile(areaGroup.getBlock(x-1, y-1, Area.Layer.Lower).blockId);
        if (x > 0 && y < areaGroup.getHeight()-1) downLeftPainted |= isPaintedTile(areaGroup.getBlock(x-1, y+1, Area.Layer.Lower).blockId);
        if (x < areaGroup.getWidth()-1 && y > 0) upRightPainted |= isPaintedTile(areaGroup.getBlock(x+1, y-1, Area.Layer.Lower).blockId);
        if (x < areaGroup.getWidth()-1 && y < areaGroup.getHeight()-1) downRightPainted |= isPaintedTile(areaGroup.getBlock(x+1, y+1, Area.Layer.Lower).blockId);

        if (x == 0){
            leftPainted = true;
            upLeftPainted = true;
            downLeftPainted = true;
        }
        else if (x == areaGroup.getWidth()-1){
            rightPainted = true;
            upRightPainted = true;
            downRightPainted = true;
        }
        if (y == 0){
            upPainted = true;
            upLeftPainted = true;
            upRightPainted = true;
        }
        else if (y == areaGroup.getHeight()-1){
            downPainted = true;
            downLeftPainted = true;
            downRightPainted = true;
        }

        int xo, yo;
        //zero connections
        if (!upPainted && !downPainted && !leftPainted && !rightPainted){ //lone spot
            xo = 0;
            yo = 0;
        }
        //one connection
        else if (!upPainted && downPainted && !leftPainted && !rightPainted){ //upward peninsula
            xo = 3;
            yo = 0;
        }
        else if (upPainted && !downPainted && !leftPainted && !rightPainted){ //downward peninsula
            xo = 3;
            yo = 1;
        }
        else if (!upPainted && !downPainted && leftPainted && !rightPainted){ //rightward peninsula
            xo = 3;
            yo = 2;
        }
        else if (!upPainted && !downPainted && !leftPainted && rightPainted){ //leftward peninsula
            xo = 3;
            yo = 3;
        }
        //two connections
        else if (upPainted && downPainted && !leftPainted && !rightPainted){ //vertical strip
            xo = 0;
            yo = 1;
        }
        else if (!upPainted && !downPainted && leftPainted && rightPainted){ //horizontal strip
            xo = 0;
            yo = 2;
        }
        else if (!upPainted && downPainted && !leftPainted && rightPainted){ //UL corner
            xo = 2;
            yo = 0;
        }
        else if (upPainted && !downPainted && !leftPainted && rightPainted){ //DL corner
            xo = 2;
            yo = 1;
        }
        else if (!upPainted && downPainted && leftPainted && !rightPainted){ //UR corner
            xo = 2;
            yo = 2;
        }
        else if (upPainted && !downPainted && leftPainted && !rightPainted){ //DR corner
            xo = 2;
            yo = 3;
        }
        //three connections
        else if (upPainted && downPainted && !leftPainted && rightPainted){ //left edge
            xo = 1;
            yo = 0;
        }
        else if (upPainted && downPainted && leftPainted && !rightPainted){ //right edge
            xo = 1;
            yo = 1;
        }
        else if (!upPainted && downPainted && leftPainted && rightPainted){ //top edge
            xo = 1;
            yo = 2;
        }
        else if (upPainted && !downPainted && leftPainted && rightPainted){ //bottom edge
            xo = 1;
            yo = 3;
        }
        //four connections
        else {
            xo = 0;
            yo = 3;
        }

        int b = ob+xo+yo*16;
        return b;
    }

}

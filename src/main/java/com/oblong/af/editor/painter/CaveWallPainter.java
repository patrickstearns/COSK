package com.oblong.af.editor.painter;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;

/**
 * OverlayPainter paints on the main layer, and adjusts it and the tiles around it based on whether and how it borders
 *   tiles that haven't been painted with this painter.
 */

public class CaveWallPainter implements Painter {

    private int ob;
    private int upperLeftLowerTile, upperRightLowerTile;

    public CaveWallPainter(int ob, int upperLeftLowerTile, int upperRightLowerTile){
        this.ob = ob;
        this.upperLeftLowerTile = upperLeftLowerTile;
        this.upperRightLowerTile = upperRightLowerTile;
    }

    public void paint(AreaGroup areaGroup, int x, int y) {
        if (!isTopBlockId(areaGroup.getBlock(x, y-2, Area.Layer.Upper).blockId)){
            if (!isMiddleBlockId(areaGroup.getBlock(x, y, Area.Layer.Main).blockId))
                areaGroup.setBlock(x, y, Area.Layer.Main, newBottomTileId(areaGroup, x, y));
            areaGroup.setBlock(x, y-1, Area.Layer.Main, newMiddleTileId(areaGroup, x, y));
            areaGroup.setBlock(x, y-2, Area.Layer.Upper, newTopTileId(areaGroup, x, y));

            if (isTopBlockId(areaGroup.getBlock(x, y-3, Area.Layer.Upper).blockId)){
                if (!isMiddleBlockId(areaGroup.getBlock(x, y-1, Area.Layer.Main).blockId))
                    areaGroup.setBlock(x, y-1, Area.Layer.Main, newBottomTileId(areaGroup, x, y-1));
                if (!isTopBlockId(areaGroup.getBlock(x, y-2, Area.Layer.Upper).blockId))
                    areaGroup.setBlock(x, y-2, Area.Layer.Main, newMiddleTileId(areaGroup, x, y-1));
                areaGroup.setBlock(x, y-3, Area.Layer.Upper, newTopTileId(areaGroup, x, y-1));
            }

            if (isTopBlockId(areaGroup.getBlock(x-1, y-2, Area.Layer.Upper).blockId)){
                if (!isMiddleBlockId(areaGroup.getBlock(x-1, y, Area.Layer.Main).blockId))
                    areaGroup.setBlock(x-1, y, Area.Layer.Main, newBottomTileId(areaGroup, x-1, y));
                if (!isTopBlockId(areaGroup.getBlock(x-1, y-1, Area.Layer.Upper).blockId))
                    areaGroup.setBlock(x-1, y-1, Area.Layer.Main, newMiddleTileId(areaGroup, x-1, y));
                areaGroup.setBlock(x-1, y-2, Area.Layer.Upper, newTopTileId(areaGroup, x-1, y));
            }

            if (isTopBlockId(areaGroup.getBlock(x+1, y-2, Area.Layer.Upper).blockId)){
                if (!isMiddleBlockId(areaGroup.getBlock(x+1, y, Area.Layer.Main).blockId))
                    areaGroup.setBlock(x+1, y, Area.Layer.Main, newBottomTileId(areaGroup, x+1, y));
                if (!isTopBlockId(areaGroup.getBlock(x+1, y-1, Area.Layer.Upper).blockId))
                    areaGroup.setBlock(x+1, y-1, Area.Layer.Main, newMiddleTileId(areaGroup, x+1, y));
                areaGroup.setBlock(x+1, y-2, Area.Layer.Upper, newTopTileId(areaGroup, x+1, y));
            }

            if (isTopBlockId(areaGroup.getBlock(x, y-1, Area.Layer.Upper).blockId)){
                if (!isMiddleBlockId(areaGroup.getBlock(x, y+1, Area.Layer.Main).blockId))
                    areaGroup.setBlock(x, y+1, Area.Layer.Main, newBottomTileId(areaGroup, x, y+1));
                if (!isTopBlockId(areaGroup.getBlock(x, y-2, Area.Layer.Upper).blockId))
                    areaGroup.setBlock(x, y-2, Area.Layer.Main, newMiddleTileId(areaGroup, x, y+1));
                areaGroup.setBlock(x, y-1, Area.Layer.Upper, newTopTileId(areaGroup, x, y+1));
            }
        }

    }

    private boolean isTopBlockId(int b){
        int rowOffset = (b-ob)/16;
        return rowOffset >= 0 && rowOffset < 3;
    }

    private boolean isMiddleBlockId(int b){
        int rowOffset = (b-ob)/16;
        return rowOffset == 3;
    }

    //noop'd
    public void fill(AreaGroup areaGroup, int x, int y){}

    public int getBaseBlockId(){ return ob; }

    public boolean isPaintedTile(int b){
        return (b == upperLeftLowerTile) ||
                (b == upperRightLowerTile) ||
                (b >= ob && b < ob+4) ||
                (b >= ob+16 && b < ob+16+4) ||
                (b >= ob+16*2 && b < ob+16*2+4) ||
                (b >= ob+16*2 && b < ob+16*3+4) ||
                (b >= ob+16*2 && b < ob+16*4+4) ||
                (b >= ob+16*3 && b < ob+16*5+4);
    }

    private int newBottomTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isPaintedTile(areaGroup.getBlock(x, y-3, Area.Layer.Upper).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Upper).blockId);
        if (x > 0) leftPainted = isPaintedTile(areaGroup.getBlock(x-1, y-2, Area.Layer.Upper).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isPaintedTile(areaGroup.getBlock(x+1, y-2, Area.Layer.Upper).blockId);

        if (x == 0) leftPainted = true;
        else if (x == areaGroup.getWidth()-1) rightPainted = true;
        if (y == 0) upPainted = true;
        else if (y == areaGroup.getHeight()-1) downPainted = true;

        int xo, yo;
        //zero connections
        if (!upPainted && !downPainted && !leftPainted && !rightPainted){ //DL corner
            xo = 1+x%2;
            yo = 4;
        }
        //one connection
        else if (upPainted && !downPainted && !leftPainted && !rightPainted){
            xo = 1+x%2;
            yo = 4;
        }
        else if (!upPainted && downPainted && !leftPainted && !rightPainted){
            xo = 1+x%2;
            yo = 4;
        }
        else if (!upPainted && !downPainted && leftPainted && !rightPainted){
            xo = 3;
            yo = 4;
        }
        else if (!upPainted && !downPainted && !leftPainted && rightPainted){
            xo = 0;
            yo = 4;
        }
        //two connections
        else if (upPainted && !downPainted && !leftPainted && rightPainted){ //DL corner
            xo = 0;
            yo = 4;
        }
        else if (upPainted && !downPainted && leftPainted && !rightPainted){ //DR corner
            xo = 3;
            yo = 4;
        }
        else if (!upPainted && downPainted && !leftPainted && rightPainted){ //UL corner
            xo = upperLeftLowerTile%16;
            yo = upperLeftLowerTile/16;
            xo -= ob; //to make offsets come out right
        }
        else if (!upPainted && downPainted && leftPainted && !rightPainted){ //UR corner
            xo = upperRightLowerTile%16;
            yo = upperRightLowerTile/16;
            xo -= ob; //to make offsets come out right
        }
        else if (!upPainted && !downPainted && leftPainted && rightPainted){
            xo = 1+x%2;
            yo = 4;
        }
        else if (upPainted && downPainted && !leftPainted && !rightPainted){
            xo = 1+x%2;
            yo = 4;
        }
        //anything else - middle segment
        else {
            xo = 1+x%2;
            yo = 4;
        }
        int b = ob+xo+yo*16;
        return b;
    }

    private int newMiddleTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isPaintedTile(areaGroup.getBlock(x, y-3, Area.Layer.Upper).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Upper).blockId);
        if (x > 0) leftPainted = isPaintedTile(areaGroup.getBlock(x-1, y-2, Area.Layer.Upper).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isPaintedTile(areaGroup.getBlock(x+1, y-2, Area.Layer.Upper).blockId);

        if (x == 0) leftPainted = true;
        else if (x == areaGroup.getWidth()-1) rightPainted = true;
        if (y == 0) upPainted = true;
        else if (y == areaGroup.getHeight()-1) downPainted = true;

        int xo, yo;
        //two connections
        if (!downPainted && !leftPainted && rightPainted){ //DL corner
            xo = 0;
            yo = 3;
        }
        else if (!downPainted && leftPainted && !rightPainted){ //DR corner
            xo = 3;
            yo = 3;
        }
        //anything else - middle segment
        else {
            xo = 1+x%2;
            yo = 3;
        }
        int b = ob+xo+yo*16;
        return b;
    }

    private int newTopTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isPaintedTile(areaGroup.getBlock(x, y-3, Area.Layer.Upper).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isPaintedTile(areaGroup.getBlock(x, y-1, Area.Layer.Upper).blockId);
        if (x > 0) leftPainted = isPaintedTile(areaGroup.getBlock(x-1, y-2, Area.Layer.Upper).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isPaintedTile(areaGroup.getBlock(x+1, y-2, Area.Layer.Upper).blockId);

        if (x == 0) leftPainted = true;
        else if (x == areaGroup.getWidth()-1) rightPainted = true;
        if (y == 0) upPainted = true;
        else if (y == areaGroup.getHeight()-1) downPainted = true;

        int xo, yo;
        //bottom row of top pieces
        if (!downPainted){
            if (!leftPainted && rightPainted) xo = 0;
            else if (leftPainted && !rightPainted) xo = 3;
            else xo = 1+x%2;
            yo = 2;
        }
        //top row of top pieces
        else if (!upPainted){
            if (!leftPainted && rightPainted) xo = 0;
            else if (leftPainted && !rightPainted) xo = 3;
            else xo = 1+x%2;
            yo = 0;
        }
        //middle row of top pieces
        else{
            if (!leftPainted && rightPainted) xo = 0;
            else if (leftPainted&& !rightPainted) xo = 3;
            else xo = 1+x%2;
            yo = 1;
        }

        int b = ob+xo+yo*16;
        return b;
    }

}

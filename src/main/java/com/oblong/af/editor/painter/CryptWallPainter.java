package com.oblong.af.editor.painter;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;

/**
 * OverlayPainter paints on the main layer, and adjusts it and the tiles around it based on whether and how it borders
 *   tiles that haven't been painted with this painter.
 */

public class CryptWallPainter implements Painter {

    private int ob;

    public CryptWallPainter(int ob){
        this.ob = ob;
    }

    public void paint(AreaGroup areaGroup, int x, int y) {
        areaGroup.setBlock(x, y-3, Area.Layer.Upper, newCapTileId(areaGroup, x, y));

        for (int i = 0; i < areaGroup.getWidth(); i++){
            for (int j = 0; j < areaGroup.getHeight(); j++){
                if (isTopBlockId(areaGroup.getBlock(i, j-3, Area.Layer.Upper).blockId)){
                    int newCT = newCapTileId(areaGroup, i, j);
                    areaGroup.setBlock(i, j-3, Area.Layer.Upper, newCT);

                    int newBT = newBottomTileId(areaGroup, i, j);
                    areaGroup.setBlock(i, j, Area.Layer.Main, newBT);
                    areaGroup.setBlock(i, j-1, Area.Layer.Main, newBT-16);
                    if (!isTopBlockId(areaGroup.getBlock(i, j-2, Area.Layer.Upper).blockId))
                        areaGroup.setBlock(i, j-2, Area.Layer.Upper, newBT-32);
//System.out.println(i+","+j+": "+newBT+" "+newCT);
                }
            }
        }
    }

    private boolean isTopBlockId(int b){ return (b-ob)/16 == 0 || b == 15+20*16; }
    private boolean isUnderTopBlockId(int b){ return (b-ob)/16 == 1; }
    private boolean isSecondBlockId(int b){ return (b-ob)/16 == 2; }

    //noop'd
    public void fill(AreaGroup areaGroup, int x, int y){}

    public int getBaseBlockId(){ return ob; }

    public boolean isPaintedTile(int b){
        return (b >= ob && b < ob+5) ||
                (b >= ob+16 && b < ob+16+5) ||
                (b >= ob+16*2 && b < ob+16*2+5) ||
                (b >= ob+16*2 && b < ob+16*3+5);
    }

    private int newBottomTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isTopBlockId(areaGroup.getBlock(x, y-4, Area.Layer.Upper).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isTopBlockId(areaGroup.getBlock(x, y-2, Area.Layer.Upper).blockId);
        if (x > 0) leftPainted = isTopBlockId(areaGroup.getBlock(x-1, y-3, Area.Layer.Upper).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isTopBlockId(areaGroup.getBlock(x+1, y-3, Area.Layer.Upper).blockId);

        if (x == 0) leftPainted = true;
        else if (x == areaGroup.getWidth()-1) rightPainted = true;
        if (y == 0) upPainted = true;
        else if (y == areaGroup.getHeight()-1) downPainted = true;

        int xo, yo = 3;

        if (!downPainted){
            if (!leftPainted || !rightPainted) xo = 0;
            else {
                //count the number of contiguous tiles in this horizontal row on either side of target tile minus column ends
                int toLeft = 0;
                for (int i = x-1; i >= 0; i--) //going left
                    if (isTopBlockId(areaGroup.getBlock(i, y-3, Area.Layer.Upper).blockId) &&
                            !isPaintedTile(areaGroup.getBlock(i, y+1, Area.Layer.Main).blockId))
                        toLeft++;
                    else if (isTopBlockId(areaGroup.getBlock(i, y-3, Area.Layer.Upper).blockId) &&
                            isTopBlockId(areaGroup.getBlock(i, y-2, Area.Layer.Upper).blockId)){
                        toLeft++;
                        i = 0;
                    }
                    else i = 0;

                int toRight = 0;
                for (int i = x+1; i < areaGroup.getWidth(); i++) //going right
                    if (isTopBlockId(areaGroup.getBlock(i, y-3, Area.Layer.Upper).blockId) &&
                            !isPaintedTile(areaGroup.getBlock(i, y+1, Area.Layer.Main).blockId))
                        toRight++;
                    else if (isTopBlockId(areaGroup.getBlock(i, y-3, Area.Layer.Upper).blockId) &&
                            isTopBlockId(areaGroup.getBlock(i, y-2, Area.Layer.Upper).blockId)){
                        toRight++;
                        i = areaGroup.getWidth();
                    }
                    else i = areaGroup.getWidth();

                int tilesInSegment = toLeft + 1 + toRight - 2;

                //if 1 or 3, use columns
                if (tilesInSegment == 1 || tilesInSegment == 3) xo = 0;
                //else if an even number in segment, use closed wall
                else if (tilesInSegment%2 == 0){
                    if (toLeft%2 == 0) xo = 4;
                    else xo = 3;
                }
                else if ((tilesInSegment+2-9)%4 == 0 && Math.abs(toLeft-toRight) == 2)
                    xo = 0;
                //otherwise put a column in the middle and closed walls to sides
                else{
                    if (toLeft == toRight) xo = 0;
                    else if (toLeft > toRight){ //on right side
                        if (toRight%2 == 1) xo = 4;
                        else xo = 3;
                    }
                    else{
                        if (toLeft%2 == 1) xo = 3;
                        else xo = 4;
                    }
                }
            }
        }
        else xo = 0;

        return ob+xo+yo*16;
    }


    private int newCapTileId(AreaGroup areaGroup, int x, int y){
        boolean upPainted = false, downPainted = false, leftPainted = false, rightPainted = false;

        //first check on main layer
        if (y > 0) upPainted = isTopBlockId(areaGroup.getBlock(x, y-4, Area.Layer.Upper).blockId);
        if (y < areaGroup.getHeight()-1) downPainted = isTopBlockId(areaGroup.getBlock(x, y-2, Area.Layer.Upper).blockId);
        if (x > 0) leftPainted = isTopBlockId(areaGroup.getBlock(x-1, y-3, Area.Layer.Upper).blockId);
        if (x < areaGroup.getWidth()-1) rightPainted = isTopBlockId(areaGroup.getBlock(x+1, y-3, Area.Layer.Upper).blockId);

        if (x == 0) leftPainted = true;
        else if (x == areaGroup.getWidth()-1) rightPainted = true;
        if (y == 0) upPainted = true;
        else if (y == areaGroup.getHeight()-1) downPainted = true;

        int xo, yo = 0;
        if (!upPainted && !downPainted && !leftPainted && !rightPainted) xo = 0;        //zero connections
        else if (upPainted && !downPainted && !leftPainted && !rightPainted) xo = 0;    //one connections
        else if (!upPainted && downPainted && !leftPainted && !rightPainted) xo = 0;
        else if (!upPainted && !downPainted && leftPainted && !rightPainted) xo = 0;
        else if (!upPainted && !downPainted && !leftPainted && rightPainted) xo = 0;
        else if (upPainted && !downPainted && leftPainted && !rightPainted) xo = 0;     //two connections at a bend
        else if (upPainted && !downPainted && !leftPainted && rightPainted) xo = 0;
        else if (!upPainted && downPainted && leftPainted && !rightPainted) xo = 0;
        else if (!upPainted && downPainted && !leftPainted && rightPainted) xo = 0;
        else if (upPainted && downPainted && !leftPainted && !rightPainted) xo = 0;     //two connections straight across
        else if (!upPainted && !downPainted && leftPainted && rightPainted) xo = 0;
        else if (upPainted && downPainted && leftPainted && !rightPainted) xo = 4;      //three connections
        else if (upPainted && downPainted && !leftPainted && rightPainted) xo = 1;
        else if (upPainted && !downPainted && leftPainted && rightPainted) xo = 2;
        else if (!upPainted && downPainted && leftPainted && rightPainted) xo = 3;
        else{                                                                           //four connections
            return 15+20*16; //return early
        }

        return ob+xo+yo*16;
    }

}

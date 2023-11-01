package com.oblong.af.editor.painter;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;

import java.util.List;

/**
 * RandomPainter is easy: there are 'common' tiles and 'uncommon' ones; we pick which based on rareProbability, then
 *   randomly choose one of the two lists.  We won't paint over our own tiles, and always go on the Lower layer.
 */

public class RandomPainter implements Painter {

    private List<Integer> commonTiles, rareTiles;
    private float rareProbability;

    public RandomPainter(List<Integer> commonTiles, List<Integer> rareTiles, float rareProbability){
        this.commonTiles = commonTiles;
        this.rareTiles = rareTiles;
        this.rareProbability = rareProbability;
    }

    public void paint(AreaGroup areaGroup, int x, int y) {
        if (!isPaintedTile(areaGroup.getBlock(x, y, Area.Layer.Lower).blockId))
           areaGroup.setBlock(x, y, Area.Layer.Lower, randomTileId());
    }

    public void fill(AreaGroup areaGroup, int x, int y) {
        int b = areaGroup.getBlock(x, y, Area.Layer.Lower).blockId;
        if (!isPaintedTile(b)) fill(areaGroup, x, y, Area.Layer.Lower, b);
    }

    private void fill(AreaGroup areaGroup, int x, int y, Area.Layer z, int idToFill){
        if (x < 0 || y < 0 || x >= areaGroup.getWidth() || y >= areaGroup.getHeight()) return;
        if (areaGroup.getBlock(x, y, z).blockId != idToFill) return;

        areaGroup.setBlock(x, y, z, randomTileId());
        fill(areaGroup, x-1, y, z, idToFill);
        fill(areaGroup, x+1, y, z, idToFill);
        fill(areaGroup, x, y-1, z, idToFill);
        fill(areaGroup, x, y+1, z, idToFill);
    }

    public int getBaseBlockId(){ return commonTiles.get(0); }

    public boolean isPaintedTile(int b){ return commonTiles.contains(b) || rareTiles.contains(b); }

    private int randomTileId(){
        if (rareTiles.size() > 0 && Math.random() < rareProbability)
            return rareTiles.get((int)((Math.random()*1000)%rareTiles.size()));
        else return commonTiles.get((int)((Math.random()*1000)%commonTiles.size()));
    }

}

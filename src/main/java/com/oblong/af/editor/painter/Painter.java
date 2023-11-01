package com.oblong.af.editor.painter;

import com.oblong.af.level.AreaGroup;

public interface Painter {

    public void paint(AreaGroup areaGroup, int x, int y);

    public void fill(AreaGroup areaGroup, int x, int y);

    public int getBaseBlockId();

    public boolean isPaintedTile(int blockId);

}

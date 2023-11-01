package com.oblong.af.editor.painter;

import java.util.Arrays;

public class PainterFactory {

    private PainterFactory(){}

    //fence and wall painters are intentionally omitted, since they stick up instead of down
    public static Painter findPainterForBlock(int blockId){
        for (Painter painter: new Painter[]{ grassPainter, sandPainter, checkerboardPainter, dirtOverlayPainter,
        sandOverlayPainter, cobblestoneOverlayPainter, snowOverlayPainter, darkStoneOverlayPainter, lightStoneOverlayPainter,
        waterOverlayPainter, lavaOverlayPainter, darkStoneEdgedPainter, lightStoneEdgedPainter})
            if (painter.isPaintedTile(blockId))
                return painter;
        return null;
    }

    private static Painter grassPainter = new RandomPainter(
            Arrays.asList(16*9-1, 16*10-1, 16*11-1), Arrays.asList(16*12-1, 16*13-1, 16*14-1, 16*15-1), 0.05f);
    public static Painter getGrassPainter(){ return grassPainter; }

    private static Painter sandPainter = new RandomPainter(
            Arrays.asList(32), Arrays.asList(11+16*12, 12+16*12, 13+16*12, 14+16*12), 0.075f);
    public static Painter getSandPainter(){ return sandPainter; }

    private static Painter checkerboardPainter = new CheckerboardTilePainter(
            Arrays.asList(0+16*32, 0+16*33, 0+16*34, 0+16*35, 2+16*32, 2+16*33, 2+16*34, 2+16*35),
            Arrays.asList(1+16*32, 1+16*33, 1+16*34, 1+16*35, 3+16*32, 3+16*33, 3+16*34, 3+16*35));
    public static Painter getCheckerboardPainter(){ return checkerboardPainter; }

    private static Painter cavePainter = new RandomPainter(
            Arrays.asList(17*16+3, 17*16+4, 17*16+5, 17*16+6),
            Arrays.asList(17*16+7, 17*16+8, 17*16+9, 17*16+10, 17*16+11, 17*16+12, 17*16+13, 17*16+14), 0.5f);
    public static Painter getCavePainter(){ return cavePainter; }

    private static Painter iceCavePainter = new RandomPainter(
            Arrays.asList(48*16+4, 48*16+5, 48*16+6, 48*16+7),
            Arrays.asList(48*16+8, 48*16+9, 48*16+10, 48*16+11, 48*16+12, 48*16+13, 48*16+14, 48*16+15), 0.1f);
    public static Painter getIceCavePainter(){ return iceCavePainter; }

    private static Painter dirtOverlayPainter = new OverlayPainter(16, 1, false, false);
    public static Painter getDirtOverlayPainter(){ return dirtOverlayPainter; }

    private static Painter sandOverlayPainter = new OverlayPainter(32, 6, false, false);
    public static Painter getSandOverlayPainter(){ return sandOverlayPainter; }

    private static Painter cobblestoneOverlayPainter = new OverlayPainter(48, 11, false, false);
    public static Painter getCobblestoneOverlayPainter(){ return cobblestoneOverlayPainter; }

    private static Painter snowOverlayPainter = new OverlayPainter(64, 65, false, false);
    public static Painter getSnowOverlayPainter(){ return snowOverlayPainter; }

    private static Painter darkStoneOverlayPainter = new OverlayPainter(80, 70, false, false);
    public static Painter getDarkStoneOverlayPainter(){ return darkStoneOverlayPainter; }

    private static Painter lightStoneOverlayPainter = new OverlayPainter(96, 75, false, false);
    public static Painter getLightStoneOverlayPainter(){ return lightStoneOverlayPainter; }

    private static Painter waterOverlayPainter = new OverlayPainter(12*16, 8*16, true, true);
    public static Painter getWaterOverlayPainter(){ return waterOverlayPainter; }

    private static Painter lavaOverlayPainter = new OverlayPainter(17*16, 13*16, true, true);
    public static Painter getLavaOverlayPainter(){ return lavaOverlayPainter; }

    private static Painter caveWaterOverlayPainter = new OverlayPainter(18*16+15, 18*16+10, false, true);
    public static Painter getCaveWaterOverlayPainter(){ return caveWaterOverlayPainter; }

    private static Painter caveHoleOverlayPainter = new OverlayPainter(19*16+15, 22*16, false, false);
    public static Painter getCaveHoleOverlayPainter(){ return caveHoleOverlayPainter; }

    private static Painter iceCaveWaterOverlayPainter = new OverlayPainter(43*16+1, 44*16+4, false, true);
    public static Painter getIceCaveWaterOverlayPainter(){ return iceCaveWaterOverlayPainter; }

    private static Painter iceCaveHoleOverlayPainter = new OverlayPainter(19*16+15, 49*16, false, false);
    public static Painter getIceCaveHoleOverlayPainter(){ return iceCaveHoleOverlayPainter; }

    private static Painter darkStoneEdgedPainter = new EdgedPainter(80, 18*16);
    public static Painter getDarkStoneEdgedPainter(){ return darkStoneEdgedPainter; }

    private static Painter lightStoneEdgedPainter = new EdgedPainter(96, 18*16+5);
    public static Painter getLightStoneEdgedPainter(){ return lightStoneEdgedPainter; }

    private static Painter woodenFencePainter = new WoodenFencePainter(22*16+5);
    public static Painter getWoodenFencePainter(){ return woodenFencePainter; }

    private static Painter stoneFencePainter = new StoneFencePainter(22*16+10);
    public static Painter getStoneFencePainter(){ return stoneFencePainter; }

    private static Painter grassyRockWallPainter = new RockWallPainter(26*16, 31*16+2, 31*16+5, grassPainter);
    public static Painter getGrassyRockWallPainter(){ return grassyRockWallPainter; }

    private static Painter dirtyRockWallPainter = new RockWallPainter(26*16+3, 31*16+2, 31*16+5, dirtOverlayPainter);
    public static Painter getDirtyRockWallPainter(){ return dirtyRockWallPainter; }

    private static Painter sandyRockWallPainter = new RockWallPainter(26*16+6, 31*16+2, 31*16+5, sandPainter);
    public static Painter getSandyRockWallPainter(){ return sandyRockWallPainter; }

    private static Painter snowyRockWallPainter = new RockWallPainter(26*16+9, 31*16+2, 31*16+5, snowOverlayPainter);
    public static Painter getSnowyRockWallPainter(){ return snowyRockWallPainter; }

    private static Painter caveWallPainter = new CaveWallPainter(26*16+12, 31*16+2, 31*16+5);
    public static Painter getCaveWallPainter(){ return caveWallPainter; }

    private static Painter cryptWallPainter = new CryptWallPainter(32*16+4);
    public static Painter getCryptWallPainter(){ return cryptWallPainter; }

    private static Painter iceCaveWallPainter = new CaveWallPainter(44*16, 31*16+2, 31*16+5);
    public static Painter getIceCaveWallPainter(){ return iceCaveWallPainter; }

    private static Painter circuitFencePainter = new WoodenFencePainter(39*16);
    public static Painter getCircuitFencePainter(){ return circuitFencePainter; }

    private static Painter circuitPainter = new RandomPainter(Arrays.asList(43*16), Arrays.asList(43*16), 0f);
    private static Painter circuitWallPainter = new CircuitWallPainter(39*16+4, 31*16+2, 31*16+5, circuitPainter);
    public static Painter getCircuitWallPainter(){ return circuitWallPainter; }

    private static Painter riftPainter = new RandomPainter(
            Arrays.asList(43*16+11, 43*16+12, 43*16+13, 43*16+14), Arrays.asList(43*16+11, 43*16+12, 43*16+13, 43*16+14), 0f);
    public static Painter getRiftPainter(){ return riftPainter; }

    private static Painter riftHoleOverlayPainter = new OverlayPainter(40*16+9, 39*16+11, false, false);
    public static Painter getRiftHoleOverlayPainter(){ return riftHoleOverlayPainter; }

    private static Painter riftWallPainter = new CaveWallPainter(39*16+7, 31*16+2, 31*16+5);
    public static Painter getRiftWallPainter(){ return riftWallPainter; }
}

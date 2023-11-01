package com.oblong.af.level;

import com.oblong.af.util.Footprint;

import java.awt.geom.Point2D;

public class Block {

    public static enum Trait {
        Animated,
        Hurts,
        Slows,
        Water,
        Lava,
        Conductive,
        Tunnelable,
    	;
    }

    private static Footprint NO_FP = new Footprint(new Point2D.Double[]{});
    private static Footprint ALL_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(0, 16), new Point2D.Double(16, 16), new Point2D.Double(16, 0)});
    private static Footprint MID_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(4, 4), new Point2D.Double(4, 12), new Point2D.Double(12, 12), new Point2D.Double(12, 4)});
    private static Footprint TOP_HALF_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(0, 8), new Point2D.Double(16, 8), new Point2D.Double(16, 0)});
    private static Footprint BOTTOM_HALF_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 8), new Point2D.Double(0, 16), new Point2D.Double(16, 16), new Point2D.Double(16, 8)});
    private static Footprint LEFT_HALF_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(0, 16), new Point2D.Double(8, 16), new Point2D.Double(8, 0)});
    private static Footprint RIGHT_HALF_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 0), new Point2D.Double(8, 16), new Point2D.Double(16, 16), new Point2D.Double(16, 0)});
    private static Footprint NW_THICK_CORNER_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 0), new Point2D.Double(16, 0), new Point2D.Double(16, 16), new Point2D.Double(0, 16), new Point2D.Double(0, 8)});
    private static Footprint NE_THICK_CORNER_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 0), new Point2D.Double(16, 8), new Point2D.Double(16, 16), new Point2D.Double(0, 16), new Point2D.Double(0, 0)});
    private static Footprint SW_THICK_CORNER_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(16, 0), new Point2D.Double(16, 16), new Point2D.Double(8, 16), new Point2D.Double(0, 8)});
    private static Footprint SE_THICK_CORNER_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(16, 0), new Point2D.Double(16, 8), new Point2D.Double(8, 16), new Point2D.Double(0, 16)});
    private static Footprint NW_SMALL_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 0), new Point2D.Double(0, 8), new Point2D.Double(0, 0)});
    private static Footprint NE_SMALL_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 0), new Point2D.Double(16, 0), new Point2D.Double(16, 8)});
    private static Footprint SW_SMALL_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 16), new Point2D.Double(0, 16), new Point2D.Double(0, 8)});
    private static Footprint SE_SMALL_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(8, 16), new Point2D.Double(16, 16), new Point2D.Double(16, 8)});
    private static Footprint NW_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(0, 16), new Point2D.Double(16, 0)});
    private static Footprint NE_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(16, 16), new Point2D.Double(16, 0)});
    private static Footprint SW_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(0, 0), new Point2D.Double(0, 16), new Point2D.Double(16, 16)});
    private static Footprint SE_TRIANGLE_FP = new Footprint(new Point2D.Double[]{new Point2D.Double(16, 0), new Point2D.Double(0, 16), new Point2D.Double(16, 16)});

    public static enum BlockFootprint {
        None{ 
        	public Footprint getFootprint(){ return new Footprint(NO_FP); } 
	        public int getIconX(){ return 3; }
	        public int getIconY(){ return 0; }
        },
        Mid{ 
        	public Footprint getFootprint(){ return new Footprint(MID_FP); } 
	        public int getIconX(){ return 3; }
	        public int getIconY(){ return 2; }
        },
        All{ 
        	public Footprint getFootprint(){ return new Footprint(ALL_FP); } 
	        public int getIconX(){ return 3; }
	        public int getIconY(){ return 1; }
        },
        SETriangle{ 
        	public Footprint getFootprint(){ return new Footprint(SE_TRIANGLE_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 7; }
        },
        SWTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(SW_TRIANGLE_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 4; }
        },
        TopHalf{ 
        	public Footprint getFootprint(){ return new Footprint(TOP_HALF_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 0; }
        },
        BottomHalf{ 
        	public Footprint getFootprint(){ return new Footprint(BOTTOM_HALF_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 1; }
        },
        NETriangle{ 
        	public Footprint getFootprint(){ return new Footprint(NE_TRIANGLE_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 6; }
        },
        NWTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(NW_TRIANGLE_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 5; }
        },
        LeftHalf{ 
        	public Footprint getFootprint(){ return new Footprint(LEFT_HALF_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 2; }
        },
        RightHalf{ 
        	public Footprint getFootprint(){ return new Footprint(RIGHT_HALF_FP); } 
	        public int getIconX(){ return 4; }
	        public int getIconY(){ return 3; }
        },
        SEThickCorner{ 
        	public Footprint getFootprint(){ return new Footprint(SE_THICK_CORNER_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 1; }
        },
        SWThickCorner{ 
        	public Footprint getFootprint(){ return new Footprint(SW_THICK_CORNER_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 2; }
        },
        SESmallTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(SE_SMALL_TRIANGLE_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 7; }
        },
        SWSmallTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(SW_SMALL_TRIANGLE_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 4; }
        },
        NEThickCorner{ 
        	public Footprint getFootprint(){ return new Footprint(NE_THICK_CORNER_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 0; }
        },
        NWThickCorner{ 
        	public Footprint getFootprint(){ return new Footprint(NW_THICK_CORNER_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 3; }
        },
        NESmallTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(NE_SMALL_TRIANGLE_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 6; }
        },
        NWSmallTriangle{ 
        	public Footprint getFootprint(){ return new Footprint(NW_SMALL_TRIANGLE_FP); } 
	        public int getIconX(){ return 5; }
	        public int getIconY(){ return 5; }
        },
        ;
        public abstract Footprint getFootprint();
        public abstract int getIconX();
        public abstract int getIconY();
    }
    
    public int blockId;
    public int frozenCounter = 0, electrifiedCounter = 0, meltCounter = 0;
    public int dFreeze = 0;
    
    public Block(int blockId){
    	this.blockId = blockId;
    }

    public void tick(){
        frozenCounter += dFreeze;
        if (frozenCounter > 4) frozenCounter = 4;
        if (frozenCounter < 0) frozenCounter = 0;

        if (electrifiedCounter > 0) electrifiedCounter--;
    }
}

package com.oblong.af.level;

import com.oblong.af.models.World;
import com.oblong.af.util.Art;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Tileset {

    private static final long FILE_HEADER = 0x327e1237;
	public static final String TILE_BEHAVIOR_FILENAME = World.RESOURCES+"behavior.dat";
	public static final String TILE_ART_FILENAME = World.RESOURCES+"tiles.png";

    private static Tileset instance = null;

	public static Tileset getTileset(GraphicsConfiguration gc) throws IOException {
		if (instance == null) instance = loadTileset(gc, new DataInputStream(World.class.getResourceAsStream(TILE_BEHAVIOR_FILENAME)));
		return instance;
	}
	
    private static Tileset loadTileset(GraphicsConfiguration gc, DataInputStream dis) throws IOException {
        long header = dis.readLong(); //header
        if (header != FILE_HEADER) throw new IOException("Bad level header");

        HashMap<Integer, ArrayList<Block.Trait>> behaviors = new HashMap<Integer, ArrayList<Block.Trait>>();
    	HashMap<Integer, Block.BlockFootprint> footprintMap = new HashMap<Integer, Block.BlockFootprint>();
    	HashMap<Integer, Block.BlockFootprint> projectileFootprintMap = new HashMap<Integer, Block.BlockFootprint>();
    	footprintMap.put(AreaGroup.NULL_BLOCK, Block.BlockFootprint.None);
    	String id = null;
try{
    	id = dis.readUTF();
    	int mapSize = dis.readInt();
    	for (int i = 0; i < mapSize; i++){ //size of map
        	int blockId = dis.readInt(); //block Id
        	ArrayList<Block.Trait> traits = new ArrayList<Block.Trait>();
        	Block.BlockFootprint blockRect = Block.BlockFootprint.None, projectileBlockRect = Block.BlockFootprint.None;
try { 
 			blockRect = Block.BlockFootprint.valueOf(dis.readUTF());
}catch(Exception e){
blockRect = Block.BlockFootprint.None;
}
try { 
 			projectileBlockRect = Block.BlockFootprint.valueOf(dis.readUTF());
}catch(Exception e){
blockRect = Block.BlockFootprint.None;
}
try{
        	int traitsSize = dis.readInt(); //size of traits list
        	for (int j = 0; j < traitsSize; j++){
       			String traitname = dis.readUTF();
        		try{ traits.add(Block.Trait.valueOf(traitname)); } //trait name
        		catch(IllegalArgumentException e){ System.err.println("Warning: unrecognized tile trait name: "+traitname); }
        	}
        	footprintMap.put(blockId, blockRect);
        	projectileFootprintMap.put(blockId, projectileBlockRect);
        	behaviors.put(blockId, traits);
}
catch(Exception e){}
}
        }
catch(Throwable e2){}

        Image[][] art = null;
        String filename = TILE_ART_FILENAME;
        try{ art = Art.cutImage(gc, filename, 16, 16); }
        catch (Exception e){
            System.err.println("Error loading image "+filename);
            e.printStackTrace();
        }

        return new Tileset(id, behaviors, footprintMap, projectileFootprintMap, art);
    }
    
    public static void saveTileset() throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(TILE_BEHAVIOR_FILENAME)));

        dos.writeLong(FILE_HEADER); //header
    	dos.writeUTF(instance.id);
    	dos.writeInt(instance.behaviors.size()); //size of behaviors map
        for (Integer b: instance.behaviors.keySet()){
        	ArrayList<Block.Trait> traits = instance.behaviors.get(b);
        	dos.writeInt(b); //block Id
if (instance.footprintMap.get(b) == null) instance.footprintMap.put(b, Block.BlockFootprint.None);
if (instance.projectileFootprintMap.get(b) == null) instance.projectileFootprintMap.put(b, Block.BlockFootprint.None);
        	dos.writeUTF(instance.footprintMap.get(b).name());
        	dos.writeUTF(instance.projectileFootprintMap.get(b).name());
        	dos.writeInt(traits.size()); //size of traits list
        	for (int i = 0; i < traits.size(); i++){
        		dos.writeUTF(traits.get(i).name()); //trait name
        	}
        }
    }

    private String id;
    private HashMap<Integer, ArrayList<Block.Trait>> behaviors;
    private HashMap<Integer, Block.BlockFootprint> footprintMap, projectileFootprintMap;
    private Image[][] art;

    public Tileset(String id, HashMap<Integer, ArrayList<Block.Trait>> behaviors, 
    		HashMap<Integer, Block.BlockFootprint> footprintMap, HashMap<Integer, Block.BlockFootprint> projectileFootprintMap,
            Image[][] art){
    	this.id = id;
    	this.behaviors = behaviors;
    	this.footprintMap = footprintMap;
    	this.projectileFootprintMap = projectileFootprintMap;
        this.art = art;
    }
    
    public void setId(String id){ this.id = id; }
    public String getId(){ return id; }
    
    public void setBehavior(int blockId, ArrayList<Block.Trait> traits){ behaviors.put(blockId, traits); }
    public ArrayList<Block.Trait> getBehavior(int blockId){ 
    	ArrayList<Block.Trait> traits = behaviors.get(blockId);
    	if (traits == null){
    		traits = new ArrayList<Block.Trait>();
    		behaviors.put(blockId, traits);
    	}
    	return traits; 
    }

    public void setBlockFootprint(int blockId, Block.BlockFootprint blockRect){ footprintMap.put(blockId, blockRect); }
    public Block.BlockFootprint getBlockFootprint(int blockId){ 
    	Block.BlockFootprint br = footprintMap.get(blockId);
    	if (br == null) br = Block.BlockFootprint.None;
    	return br;
    }

    public void setProjectileBlockFootprint(int blockId, Block.BlockFootprint blockRect){ projectileFootprintMap.put(blockId, blockRect); }
    public Block.BlockFootprint getProjectileBlockFootprint(int blockId){ 
    	Block.BlockFootprint br = projectileFootprintMap.get(blockId);
    	if (br == null) br = Block.BlockFootprint.None;
    	return br;
    }

    public void setArt(Image[][] art){ this.art = art; }
    public Image[][] getArt(){ return art; }
}

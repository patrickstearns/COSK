package com.oblong.af.models;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;
import com.oblong.af.level.Marker;

import java.io.*;
import java.util.TreeSet;

/**
 * World holds stuff like universe of inventory items, monsters, characters, levels, party, party's inventory, etc.  
 * There is only ever one instance of it at a time.
 */

public class World {

    public static final String EDITOR_RESOURCES = "src/main/resources/";
    public static final String RESOURCES = "/";
    public static final String LEVELS = "levels/";
    public static final String WORLD_FILENAME = "world.dat";
	public static final String HUB_AREA_GROUP_ID = "village.lvl";
    public static final String CRYPTS_AREA_GROUP_ID = "crypts.lvl";
    public static final String CAVERNS_AREA_GROUP_ID = "plains.lvl";
    public static final String DESERT_AREA_GROUP_ID = "desert.lvl";
    public static final String JUNGLE_AREA_GROUP_ID = "jungle.lvl";
    public static final String GLACIER_AREA_GROUP_ID = "caverns.lvl";
    public static final String LAIR_AREA_GROUP_ID = "lair.lvl";
    public static final String RIFT_AREA_GROUP_ID = "rift.lvl";

    public static final String[] AREA_GROUP_IDS = new String[]{
        HUB_AREA_GROUP_ID, CRYPTS_AREA_GROUP_ID, CAVERNS_AREA_GROUP_ID, DESERT_AREA_GROUP_ID,
            JUNGLE_AREA_GROUP_ID, GLACIER_AREA_GROUP_ID, LAIR_AREA_GROUP_ID, RIFT_AREA_GROUP_ID
    };

    private static World instance;
	public static World getInstance(){ return instance; }
	static{
		instance = new World();
		init();
	}

	public static void init(){
		try{ load(World.class.getResourceAsStream(RESOURCES+WORLD_FILENAME)); }
		catch(IOException e){
			System.err.println("Unable to load world; starting with empty default.");
			e.printStackTrace();
			instance.startingAreaId = HUB_AREA_GROUP_ID;
			instance.areaGroupIds = new TreeSet<String>();
            try{ save(instance, new DataOutputStream(new FileOutputStream(RESOURCES+WORLD_FILENAME))); }
			catch(IOException e2){ e2.printStackTrace(); }
		}
	}
	
	public static void load(InputStream stream) throws IOException {
		DataInputStream dis = new DataInputStream(stream);
		instance.startingAreaId = dis.readUTF();

		int numAreaGroups = dis.readInt();
		TreeSet<String> areaGroupIds = new TreeSet<String>();
		for (int i = 0; i < numAreaGroups; i++) areaGroupIds.add(dis.readUTF());
		instance.areaGroupIds = areaGroupIds;
	}
	
	public static void save(World world, DataOutputStream dos) throws IOException {
		dos.writeUTF(world.startingAreaId);
		dos.writeInt(instance.areaGroupIds.size());
		for (String agId: world.areaGroupIds) dos.writeUTF(agId);
	}

	private String startingAreaId;
	private TreeSet<String> areaGroupIds;

	private World(){}

	public String getStartingAreaId(){ return startingAreaId; }
	public void setStartingAreaId(String startingAreaId){ this.startingAreaId = startingAreaId; }

	public final TreeSet<String> getAreaGroupIds(){ return areaGroupIds; }
	
	public AreaGroup findAreaGroupForPortal(Marker marker, boolean findExits) {
		try{
			for (String id: AREA_GROUP_IDS){
				InputStream stream = World.class.getResourceAsStream(World.RESOURCES+LEVELS+id);
				AreaGroup areaGroup = AreaGroup.load(null, new DataInputStream(stream), false, null);
				Area area = areaGroup.findAreaForPortal(marker, findExits);
				if (area != null) return areaGroup;
			}
		}
		catch(IOException e){
            System.err.println("Error searching levels for portals: "+e.getMessage());
            e.printStackTrace();
        }
		return null;
	}
}

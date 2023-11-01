package com.oblong.af.level;

import com.oblong.af.level.decorators.*;
import com.oblong.af.models.Messages;
import com.oblong.af.models.World;
import com.oblong.af.util.Footprint;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AreaGroup{

	public static enum Weather {
		Clear{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{})); }
		},
		Rainy{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new RainDecorator()})); }
		},
        Stormy{
            public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new OvercastDecorator(), new StormDecorator()})); }
        },
        Snowing{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new SnowDecorator()})); }
		},
		Foggy{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new FogDecorator()})); }
		},
        VolcanicEruption{
            public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new VolcanicEruptionDecorator() })); }
        },
        Quaking{
            public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new RandomQuakeDecorator()})); }
        },
        ;
		public abstract ArrayList<LayerDecorator> getDecorators();
	}

	public static enum LightLevel {
		Normal{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{})); }
		},
		Bright{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new BrightDecorator()})); }
		},
		Overcast{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new OvercastDecorator()})); }
		},
		Dusk{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new DuskDecorator()})); }
		},
		Night{
			public ArrayList<LayerDecorator> getDecorators(){ return new ArrayList<LayerDecorator>(Arrays.asList(new LayerDecorator[]{new NightDecorator()})); }
		},
		;
		public abstract ArrayList<LayerDecorator> getDecorators();
	}
	
	public static final String TILE_BEHAVIOR_FILENAME = "tiles.dat";
    private static final int FILE_HEADER = 0x271c4178;
    
    //special tile constants
    public static final int NULL_BLOCK = -1;
    public static final int DEFAULT_BLOCK = 0;

    private String id;
    private ArrayList<Area> areas;
    private Area currentArea;
    private int startAreaIndex;
    private Marker startPos;
    private File file;
    
    public AreaGroup(){
        areas = new ArrayList<Area>();
        startAreaIndex = 0;
    }
    
    public AreaGroup(String id, short width, short height){
    	this();
    	setId(id);
    	addArea(new Area("Default", width, height));
    }

    public void addArea(Area area){
    	areas.add(area);
    	if (areas.size() == 1) currentArea = area;
    }
    
    public void removeArea(Area area){
    	areas.remove(area);
    }
    
    public static AreaGroup load(GraphicsConfiguration gc, DataInputStream dis, boolean loadTileset, File file) throws IOException{
        long header = dis.readLong(); //header
        if (header != AreaGroup.FILE_HEADER) throw new IOException("Bad level header");

        AreaGroup areaGroup = new AreaGroup();
        String groupId = dis.readUTF();
        areaGroup.setId(groupId);
        areaGroup.setStartAreaIndex(dis.readInt());
        int numAreas = dis.readInt();
        for (int i = 0; i < numAreas; i++) areaGroup.addArea(Area.load(gc, dis, groupId, loadTileset));
        areaGroup.setCurrentArea(areaGroup.getAreas().get(areaGroup.getStartAreaIndex()));

        areaGroup.setFile(file);
        
        return areaGroup;
    }

    public void save(DataOutputStream dos, File file) throws IOException{
    	setId(file.getName());
    	
    	dos.writeLong(AreaGroup.FILE_HEADER); //header
        dos.writeUTF(id);
        dos.writeInt(startAreaIndex);
        dos.writeInt(areas.size());
        for (Area area: areas) area.save(dos);
        setFile(file);
    }

    public void tick(){ currentArea.tick(); }

    public String getId(){ return id; }
	public void setId(String id){ this.id = id; }

	public Block getBlock(int x, int y, Area.Layer layer){ return currentArea.getBlock(x, y, layer); }
    public void setBlock(int x, int y, Area.Layer layer, int b){ currentArea.setBlock(x, y, layer, b); }
    public Point tileBlocking(Footprint footprint, Area.Layer layer, boolean forProjectile){ return currentArea.tileBlocking(footprint, layer, forProjectile); }

    public SpriteTemplate getSpriteTemplate(int x, int y, Area.Layer layer){ return currentArea.getSpriteTemplate(x, y, layer); }
    public void setSpriteTemplate(int x, int y, Area.Layer layer, SpriteTemplate spriteTemplate){ currentArea.setSpriteTemplate(x, y, layer, spriteTemplate); }

	public ArrayList<Area> getAreas(){ return areas; }
	public void setAreas(ArrayList<Area> areas){ this.areas = areas; }

	public Area getCurrentArea(){ return currentArea; }
	public void setCurrentArea(Area currentArea){ this.currentArea = currentArea; }

	public int getStartAreaIndex(){ return startAreaIndex; }
	public void setStartAreaIndex(int startAreaIndex){ this.startAreaIndex = startAreaIndex; }

	public short getWidth(){ return currentArea.getWidth(); }
	public void setWidth(short width){ currentArea.setWidth(width); }

	public short getHeight(){ return currentArea.getHeight(); }
	public void setHeight(short height){ currentArea.setHeight(height); }

	public List<LayerDecorator> getDecorators(){ return currentArea.getDecorators(); }
	public void addDecorator(LayerDecorator decorator){ currentArea.addDecorator(decorator); }
	public void clearDecorators(){ currentArea.clearDecorators(); }

	public Weather getWeather(){ return currentArea.getWeather(); }
	public void setWeather(Weather weather){ currentArea.setWeather(weather); }

	public LightLevel getLightLevel(){ return currentArea.getLightLevel(); }
	public void setLightLevel(LightLevel lightLevel){ currentArea.setLightLevel(lightLevel); }
	
	public File getFile(){ return file; }
	public void setFile(File file){ this.file = file; }

	public ArrayList<Marker> getMarkers(int x, int y){ return currentArea.getMarkers(x, y); }
	public ArrayList<Marker> getMarkers(int x, int y, Area.Layer z){ return currentArea.getMarkers(x, y, z); }
	public void addMarker(Marker marker){ currentArea.addMarker(marker); }

	public Marker findStartPos(){
		for (Area area: areas)
			for (int i = 0; i < area.getWidth(); i++)
				for (int j = 0; j < area.getHeight(); j++)
					for (Area.Layer k: Area.Layer.values())
						for (Marker m: area.getMarkers(i, j, k))
								if (m.getType() == Marker.Type.StartPosition)
									return m;
		return null;
	}
	
	private Marker clearStartPos(){
		for (Area area: areas)
			for (int i = 0; i < area.getWidth(); i++)
				for (int j = 0; j < area.getHeight(); j++)
					for (Area.Layer k: Area.Layer.values())
						for (Marker m: area.getMarkers(i, j, k))
								if (m.getType() == Marker.Type.StartPosition)
									area.removeMarker(m);
		return null;
	}
	
	public void setStartPos(int x, int y, Area.Layer z){
		clearStartPos();
		startPos = new Marker("Start Position", id, x, y, z, 16, 16, Marker.Type.StartPosition, new Messages(), 0);
		addMarker(startPos);
		setStartAreaIndex(areas.indexOf(currentArea));
	}
	
	public Area findAreaForPortal(Marker marker, boolean findExits){
		for (Area area: areas)
			for (int i = 0; i < area.getWidth(); i++)
				for (int j = 0; j < area.getHeight(); j++)
					for (Area.Layer k: Area.Layer.values())
						for (Marker m: area.getMarkers(i, j, k)){
							if (m.getId().equals(marker.getId())){
								if (findExits && m.getType() == Marker.Type.Exit) return area;
								if (!findExits && m.getType() == Marker.Type.Entrance) return area;
								if (marker.getType() == Marker.Type.TwoWay && m != marker) return area;
							}
						}
		return null;
	}
	
	public Marker findEntranceForExit(Marker exit){
		for (Area area: areas)
			for (int i = 0; i < area.getWidth(); i++)
				for (int j = 0; j < area.getHeight(); j++)
					for (Area.Layer k: Area.Layer.values())
						for (Marker m: area.getMarkers(i, j, k))
							if (m.getId().equals(exit.getId())){
								if (m.getType() == Marker.Type.Entrance) return m;
								else if (m.getType() == Marker.Type.TwoWay && m != exit) return m;
							}
		return null;
	}
}
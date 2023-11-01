package com.oblong.af.level;

import com.oblong.af.GameComponent;
import com.oblong.af.level.decorators.LayerDecorator;
import com.oblong.af.models.ActorMessages;
import com.oblong.af.models.Messages;
import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.util.Footprint;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.oblong.af.level.AreaGroup.LightLevel;
import static com.oblong.af.level.AreaGroup.Weather;

@SuppressWarnings("unused")
public class Area{

    public static enum Layer { Lower, Main, Upper }

	private short width, height;
    private Block[][][] map; //depth, width, height
    private SpriteTemplate[][][] spriteTemplates;
    private String id;
    private List<Marker> markers;
    private List<LayerDecorator> decorators;
    private Weather weather;
    private LightLevel lightLevel;
    private Tileset tileset;
    private String musicKey;

    public Area(String id, short width, short height){
    	this.id = id;
    	this.width = width;
        this.height = height;

        decorators = new ArrayList<LayerDecorator>();
        setWeather(AreaGroup.Weather.Clear);
        setLightLevel(AreaGroup.LightLevel.Normal);

        map = new Block[Layer.values().length][width][height];
        markers = new ArrayList<Marker>();
        for (int k = 0; k < map.length; k++)
	        for (int i = 0; i < map[k].length; i++)
	        	for (int j = 0; j < map[k][i].length; j++)
	        		map[k][i][j] = new Block(AreaGroup.NULL_BLOCK);
    
        spriteTemplates = new SpriteTemplate[Layer.values().length][width][height];
    }

    private boolean inBounds(Block[][][] arr, int k, int i, int j){ return (arr.length < k && arr[k].length < i && arr[k][i].length < j); }
    public void resize(){
    	Block[][][] oldMap = map;
    	SpriteTemplate[][][] oldSpriteTemplates = spriteTemplates;
    	
        map = new Block[Layer.values().length][width][height];
        spriteTemplates = new SpriteTemplate[Layer.values().length][width][height];
        for (int k = 0; k < map.length; k++)
	        for (int i = 0; i < map[k].length; i++)
	        	for (int j = 0; j < map[k][i].length; j++){
	        		if (inBounds(oldMap, k, i, j)){
	        			map[k][i][j] = oldMap[k][i][j];
		        		spriteTemplates[k][i][j] = oldSpriteTemplates[k][i][j];
	        		}
	        		else{
	        			map[k][i][j] = new Block(AreaGroup.NULL_BLOCK);
	        		}
	        	}
    }

    @SuppressWarnings("unused")
	private void outputTiles(){
    	System.out.println("Area "+id+" "+width+"x"+height);
        for (Block[][] aMap : map) {
            for (Block[] anAMap : aMap) {
                for (Block anAnAMap : anAMap) System.out.print(anAnAMap.blockId + ",");
                System.out.println();
            }
        }
    }
    
    public static Area load(GraphicsConfiguration gc, DataInputStream dis, String areaGroupId, boolean loadTileset) throws IOException{
    	String areaId = dis.readUTF();
    	short width = dis.readShort(), height = dis.readShort();
    	String weatherStr = dis.readUTF();
    	String lightLevelStr = dis.readUTF();
        Area area = new Area(areaId, width, height);
    	
        area.setWeather(AreaGroup.Weather.valueOf(weatherStr));
        area.setLightLevel(AreaGroup.LightLevel.valueOf(lightLevelStr));

        String tilesetId = dis.readUTF(); //yes, this is ignored - deprecated
        if (loadTileset) area.setTileset(Tileset.getTileset(gc));

        String musicKey = dis.readUTF();
        area.setMusicKey(musicKey);

        for (short k = 0; k < Layer.values().length; k++)
	        for (short i = 0; i < width; i++)
	            for (short j = 0; j < height; j++)
	            	area.setBlock(i, j, Layer.values()[k], dis.readInt());

        int numST = dis.readInt();
        for (int i = 0; i < numST; i++){
        	try{
	        	int x = dis.readInt(), y = dis.readInt();
                Layer layer = Layer.valueOf(dis.readUTF());
                SpriteDefinitions actorDef = null;
                try{ actorDef = SpriteDefinitions.valueOf(dis.readUTF()); }
                catch(IllegalArgumentException exc){}
                boolean boss = dis.readBoolean();
                boolean activeByDefault = dis.readBoolean();

                Messages templateMessages = Messages.load(dis);
                ActorMessages actorMessages = ActorMessages.load(dis);

                if (actorDef != null)
                    area.setSpriteTemplate(x, y, layer, new SpriteTemplate(actorDef, layer, boss, activeByDefault, templateMessages, actorMessages));
        	}
        	catch(IllegalArgumentException e){
        		e.printStackTrace();
        	}
        }

        int numMarkers = dis.readInt();
        for (int i = 0; i < numMarkers; i++){
        	String mid = dis.readUTF();
        	int mx = dis.readInt(), my = dis.readInt();
            Area.Layer layer = Area.Layer.valueOf(dis.readUTF());
            int mwidth = dis.readInt();
            int mheight = dis.readInt();
            int delayTicks = dis.readInt();
        	Marker.Type type = Marker.Type.valueOf(dis.readUTF());
            Messages messages = Messages.load(dis);
        	area.addMarker(new Marker(mid, areaGroupId, mx, my, layer, mwidth, mheight, type, messages, delayTicks));
        }

        return area;
    }

    public void save(DataOutputStream dos) throws IOException{
    	dos.writeUTF(id);
        dos.writeShort(width); //area width
        dos.writeShort(height); //area height
        dos.writeUTF(weather.name());
        dos.writeUTF(lightLevel.name());
        dos.writeUTF(tileset.getId());
        dos.writeUTF(musicKey);

        int stCount = 0;
        for (int k = 0; k < Layer.values().length; k++){
	        for (int i = 0; i < width; i++){
	            for (int j = 0; j < height; j++){
	            	dos.writeInt(map[k][i][j].blockId); //block id
	            	if (spriteTemplates[k][i][j] != null) stCount++;
	            }
	        }
        }

        dos.writeInt(stCount); //number of sprite templates
        for (Layer layer: Layer.values()){
	        for (int i = 0; i < width; i++){
	            for (int j = 0; j < height; j++){
	            	SpriteTemplate st = spriteTemplates[layer.ordinal()][i][j];
	            	if (st != null){
	            		dos.writeInt(i); //sprite template x
	            		dos.writeInt(j); //sprite template y
	            		dos.writeUTF(st.getLayer().name());
	            		dos.writeUTF(st.getSpriteDef().name());
                        dos.writeBoolean(st.isBoss());
                        dos.writeBoolean(st.isActiveByDefault());

                        Messages.save(st.getTemplateMessages(), dos);
                        ActorMessages.save(st.getActorMessages(), dos);
	            	}
	            }
	        }
        }

        dos.writeInt(markers.size()); //number of markers
        for (Marker m: markers){
    		dos.writeUTF(m.getId());
    		dos.writeInt(m.getX());
    		dos.writeInt(m.getY());
    		dos.writeUTF(m.getLayer().name());
    		dos.writeInt(m.getWidth());
    		dos.writeInt(m.getHeight());
            dos.writeInt(m.getDelayTicks());
            dos.writeUTF(m.getType().name());
            Messages.save(m.getMessages(), dos);
        }
    }

    public void tick(){}

    public Block getBlock(int x, int y, Layer layer){
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[layer.ordinal()][x][y];
    }

    public void setBlock(int x, int y, Layer layer, int b){
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        map[layer.ordinal()][x][y].blockId = b;

        if (tileset != null && tileset.getBehavior(b) == null) tileset.setBehavior(b, new ArrayList<Block.Trait>());
    }

    public Point tileBlocking(Footprint footprint, Layer layer, boolean forProjectile){
    	Rectangle2D.Double r = footprint.getBounds();
    	int xTile = (int)(r.x/16f);
    	int yTile = (int)(r.y/16f);
    	int wTile = (int)Math.ceil(((r.x+r.width)/16f));
    	int hTile = (int)Math.ceil(((r.y+r.height)/16f));
    	
    	for (int i = 0; i < wTile; i++){
    		for (int j = 0; j < hTile; j++){
    			Block block = getBlock(xTile+i, yTile+j, layer);
    			Block.BlockFootprint blockFp = tileset.getBlockFootprint(block.blockId);
                if (blockFp != Block.BlockFootprint.None){
                    Footprint tileFp = blockFp.getFootprint();
    				tileFp.translate((xTile+i)*16, (yTile+j)*16);
	    			if (footprint.intersects(tileFp)){
	    				return new Point(xTile+i, yTile+j);
	    			}
    			}

                Block.BlockFootprint pitFP = tileset.getProjectileBlockFootprint(block.blockId);
                if (pitFP != Block.BlockFootprint.None && !forProjectile && block.frozenCounter == 0){
                    Footprint tileFp = pitFP.getFootprint();
                    tileFp.translate((xTile+i)*16, (yTile+j)*16);
                    if (footprint.intersects(tileFp)){
                        return new Point(xTile+i, yTile+j);
                    }
                }
            }
    	}
    	
    	return null;
    }
    
    public SpriteTemplate getSpriteTemplate(int x, int y, Area.Layer layer){
    	SpriteTemplate ret;
        if (x < 0) ret = null;
        else if (y < 0) ret = null;
        else if (x >= width) ret = null;
        else if (y >= height) ret = null;
        else ret = spriteTemplates[layer.ordinal()][x][y];
        return ret;
    }

    public void setSpriteTemplate(int x, int y, Layer layer, SpriteTemplate spriteTemplate){
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        spriteTemplates[layer.ordinal()][x][y] = spriteTemplate;
    }
    
    public Marker findMarker(String id, Marker.Type type){
    	for (Marker m: markers)
			if (m.getType() == type && m.getId().equals(id))
				return m;
    	return null;
    }
    
    public Marker findMarker(Marker.Type type){
    	for (Marker m: markers)
			if (m.getType() == type)
				return m;
    	return null;
    }

    public ArrayList<Marker> getMarkers(Layer layer){
        Set<Marker> markers = new HashSet<Marker>();
        for (int x = 0; x < getWidth(); x++)
            for (int y = 0; y < getWidth(); y++)
                markers.addAll(getMarkers(x, y, layer));
        return new ArrayList<Marker>(markers);
    }

    public ArrayList<Marker> getMarkers(int x, int y){
    	ArrayList<Marker> ret = new ArrayList<Marker>();
    	for (Marker m: markers)
    		if (m.getX() == x && m.getY() == y)
    			ret.add(m);
    	return ret;
    }

    public ArrayList<Marker> getMarkers(int x, int y, Layer layer){
    	ArrayList<Marker> ret = new ArrayList<Marker>();
    	for (Marker m: markers)
    		if (m.getX() == x && m.getY() == y && m.getLayer() == layer){
    			ret.add(m);
    		}
    	return ret;
    }

    public void addMarker(Marker marker){ markers.add(marker); }
    public void removeMarker(Marker marker){ markers.remove(marker); }

    public ArrayList<Block.Trait> getBehavior(int blockId){
    	ArrayList<Block.Trait> traits = tileset.getBehavior(blockId);
    	if (traits == null){
    		traits = new ArrayList<Block.Trait>();
    		tileset.setBehavior(blockId, traits);
    	}
    	return traits; 
    }
    public void setBehavior(int blockId, ArrayList<Block.Trait> traits){ tileset.setBehavior(blockId, traits); }

	public String getId(){ return id; }
	public void setId(String id){ this.id = id; }

	public short getWidth(){ return width; }
	public void setWidth(short width){ this.width = width; }

	public short getHeight(){ return height; }
	public void setHeight(short height){ this.height = height; }

   public Tileset getTileset(){ return tileset; }
    public void setTileset(Tileset tileset){ this.tileset = tileset; }

    public String getMusicKey(){ return musicKey; }
    public void setMusicKey(String musicKey){ this.musicKey = musicKey; }

    public List<LayerDecorator> getDecorators(){ return decorators; }
	public void addDecorator(LayerDecorator decorator){ decorators.add(decorator); }
	public void clearDecorators(){ decorators.clear(); }
	public void resetDecorators(){
		clearDecorators();
		if (lightLevel != null) for (LayerDecorator d: lightLevel.getDecorators()) addDecorator(d);
		if (weather != null) for (LayerDecorator d: weather.getDecorators()) addDecorator(d);
        if (GameComponent.INSTANCE != null && GameComponent.INSTANCE.getGraphicsConfiguration() != null)
            for (LayerDecorator decorator: decorators)
                decorator.init(320, 240);
	}
	
	public Weather getWeather(){ return weather; }
	public void setWeather(Weather weather){
		this.weather = weather; 
		resetDecorators();
	}

	public LightLevel getLightLevel(){ return lightLevel; }
	public void setLightLevel(LightLevel lightLevel){
		this.lightLevel = lightLevel;
		resetDecorators();
	}
}
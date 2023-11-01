package com.oblong.af.level;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.GameComponent;
import com.oblong.af.level.decorators.*;
import com.oblong.af.models.*;
import com.oblong.af.models.console.BattleDisplay;
import com.oblong.af.models.console.Console;
import com.oblong.af.models.console.Subscreen;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.Sprite;
import com.oblong.af.sprite.effects.Sparkle;
import com.oblong.af.sprite.thing.Spark;
import com.oblong.af.util.Art;
import com.oblong.af.util.Footprint;
import com.oblong.af.util.ImageUtils;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class AreaScene extends Scene {

    public static final String PLAYER_CLICK_MESSAGE = "playerClicked";

    private static Map<String, List<Object[]>> PERSISTED_MESSAGES = new HashMap<String, List<Object[]>>();

    public AreaGroup areaGroup;
    public Player player;
    public float xCam, yCam, xCamO, yCamO;
    public boolean paused = false, actionPaused = true;
    public int startTime = 0;

	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
    private ArrayList<Sprite> spritesToAdd = new ArrayList<Sprite>();
    private ArrayList<Sprite> spritesToRemove = new ArrayList<Sprite>();
    private int tick;
    private AreaGroupRenderer renderer;
    private GraphicsConfiguration graphicsConfiguration;
    private String loadedFilename;
    private Console console;
    private Subscreen subscreen;
    private SpriteDefinitions playerDefinition;
    private boolean was1 = false, was2 = false, was3 = false, wasMenu = false, wasMouseLeft = false;
    private GameState gameState;
    private int shakeCounter = 0;

    public AreaScene(GraphicsConfiguration graphicsConfiguration, GameState gameState){
        this.graphicsConfiguration = graphicsConfiguration;
        console = new Console(this);
        this.gameState = gameState;
        subscreen = new Subscreen(console);
    }

    public AreaScene(GraphicsConfiguration graphicsConfiguration, GameState gameState, String loadedFilename){
    	this(graphicsConfiguration, gameState);
        this.loadedFilename = loadedFilename;
        this.playerDefinition = gameState.getPlayerDefinition();
    }

    public void init(){
		try{ 
			InputStream stream = World.class.getResourceAsStream(loadedFilename);
			areaGroup = AreaGroup.load(graphicsConfiguration, new DataInputStream(stream), true, null);
		}
		catch(Exception e){ e.printStackTrace(); System.exit(1); }

        Point2D.Float teleportInPoint = null;
        if (Player.INSTANCE != null) teleportInPoint = Player.INSTANCE.getTeleportOutFromPoint();

        areaGroup.setCurrentArea(areaGroup.getAreas().get(areaGroup.getStartAreaIndex()));

        player = (Player)playerDefinition.create(this, null, 0, 0, Facing.DOWN);
        player.setFacing(Facing.DOWN);
        areaChanged();
        paused = false;
        actionPaused = false;
        startTime = 1;
        tick = 0;

        player.setX(areaGroup.getCurrentArea().findMarker(Marker.Type.StartPosition).getX()*16+8);
        player.setY(areaGroup.getCurrentArea().findMarker(Marker.Type.StartPosition).getY()*16+8);
        player.initOrbiters(); //have to do after moving player

        if (loadedFilename.endsWith(World.HUB_AREA_GROUP_ID) && teleportInPoint != null){
            player.setX((float)teleportInPoint.getX());
            player.setY((float)teleportInPoint.getY());
            player.setTeleportOutFromPoint(null);
        }
        else player.setTeleportOutFromPoint(teleportInPoint);

        player.setLayer(Area.Layer.Main);

        player.setMaxHp(getGameState().getMaxHp());
        player.setHp(player.getMaxHp());
        player.getAbilitySlots()[0].ability = Ability.valueOf(getGameState().getEqAbId1());
        player.getAbilitySlots()[1].ability = Ability.valueOf(getGameState().getEqAbId2());
        player.getAbilitySlots()[2].ability = Ability.valueOf(getGameState().getEqAbId3());
        player.getAbilitySlots()[3].ability = Ability.valueOf(getGameState().getEqAbId4());
        player.getSpareAbilitySlots()[0].ability = Ability.valueOf(getGameState().getSpAbId1());
        player.getSpareAbilitySlots()[1].ability = Ability.valueOf(getGameState().getSpAbId2());
        player.getSpareAbilitySlots()[2].ability = Ability.valueOf(getGameState().getSpAbId3());
        player.getSpareAbilitySlots()[3].ability = Ability.valueOf(getGameState().getSpAbId4());

        console.add(new BattleDisplay(console));
    }

    public Subscreen getSubscreen(){ return subscreen; }

    public void changeAreaGroup(String newAreaId){
		try{ 
			InputStream stream = World.class.getResourceAsStream(World.RESOURCES+World.LEVELS+newAreaId+".lvl");
			areaGroup = AreaGroup.load(graphicsConfiguration, new DataInputStream(stream), true, null);
		}
		catch(Exception e){ e.printStackTrace(); System.exit(1); }
    }
    
    public void areaChanged(){
        areaGroup.getCurrentArea().resetDecorators();
    	for (LayerDecorator d: areaGroup.getDecorators()) d.init(320, 240);
        sprites.clear();
        renderer = new AreaGroupRenderer(areaGroup, graphicsConfiguration, 320, 240);
        sprites.add(player);

        //send any messages we persisted last time we were here
        List<Object[]> persistedMessages = PERSISTED_MESSAGES.get(areaGroup.getCurrentArea().getId());

        //spawn sprites and markers, make sure their messages are up to date
        for (int x = 0; x <= areaGroup.getWidth(); x++)
            for (int y = 0; y <= areaGroup.getHeight(); y++){
                for (Area.Layer layer: Area.Layer.values()){
                    SpriteTemplate st = areaGroup.getSpriteTemplate(x, y, layer);
                    if (st != null){
//                        if (st.getLastVisibleTick() != tick - 1)
                        if (st.getSprite() == null || !sprites.contains(st.getSprite())){
                            Sprite spawned = st.spawn(this, x, y, Facing.DOWN, false);
                            if (spawned != null && persistedMessages != null){
                                for (Object[] tuple: new ArrayList<Object[]>(persistedMessages))
                                    spawned.receiveMessage((Sprite) tuple[0], (String) tuple[1]);
                            }
                        }
                        st.setLastVisibleTick(tick);
                    }

                    for (Marker marker: areaGroup.getCurrentArea().getMarkers(layer)){
                        if (persistedMessages != null){
                            for (Object[] tuple: new ArrayList<Object[]>(persistedMessages))
                                marker.receiveMessage(this, (String)tuple[1]);
                        }
//trigger any initial weather markers that should be (NOT SURE WHAT THIS MEANT OR WAS FOR)
//                        for (String message: marker.getMessages().getActivatingMessages())
//                            if ("true".equals(gameState.getVariable(message)))
//                                marker.receiveMessage(this, message);
                    }
                }
            }

        tick = 0;

        //start music
        String musicKey = areaGroup.getCurrentArea().getMusicKey();
        if (musicKey != null && !"".equals(musicKey))
            Art.startMusic(areaGroup.getCurrentArea().getMusicKey());
        else Art.stopMusic();
    }

    private boolean bossWasPresent = false;
    public void tick(){
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0) startTime++;

        float targetXCam = player.getX() - 160;
        float targetYCam = player.getY()-120;
        xCam = targetXCam;
        yCam = targetYCam;
        if (xCam < 0) xCam = 0;
        if (xCam > areaGroup.getWidth() * 16 - 320) xCam = areaGroup.getWidth() * 16 - 320;
		if (yCam < 0) yCam = 0;
		if (yCam > areaGroup.getHeight() * 16 - 240) yCam = areaGroup.getHeight() * 16 - 240;

        boolean pressed1 = false, pressed2 = false, pressed3 = false, pressedMenu = false, pressedMouseLeft = false;
        if (GameComponent.keys[GameComponent.KEY_MENU] && !wasMenu) pressedMenu = true;
        if (GameComponent.keys[GameComponent.KEY_ABILITY_1] && !was1) pressed1 = true;
        if (GameComponent.keys[GameComponent.KEY_ABILITY_2] && !was2) pressed2 = true;
        if (GameComponent.keys[GameComponent.KEY_ABILITY_3] && !was3) pressed3 = true;
        if (GameComponent.mouse[1] && !wasMouseLeft) pressedMouseLeft = true;

        if (console != null){
            console.tick();
            if (console.getFocusedMenu() != null){
                if (pressedMenu) closeSubscreen();
                else if (pressed1) console.select();
                else if (pressed2) console.cancel();
                else if (pressedMouseLeft) console.mouseClicked(new Point(lastMousePosition.x/2, lastMousePosition.y/2));
                console.mouseMoved(new Point(lastMousePosition.x/2, lastMousePosition.y/2));
            }
        }

        if (paused || actionPaused)
            for (Sprite sprite : new ArrayList<Sprite>(sprites))
                sprite.tickNoMove();
        else {
            for (LayerDecorator d: new ArrayList<LayerDecorator>(areaGroup.getDecorators())){
                d.tick(this);
                if (d.isComplete()) areaGroup.getDecorators().remove(d);
            }

            tick++;
            areaGroup.tick();

            if (shakeCounter > 0) shakeCounter--;

			int sx = (int) xCam / 16 - 1, ex = (int) (xCam + renderer.getWidth()) / 16 + 1;
			int sy = (int) yCam / 16 - 1, ey = (int) (yCam + renderer.getHeight()) / 16 + 1;
            for (int x = sx; x <= ex; x++)
                for (int y = sy; y <= ey; y++){
	                for (Area.Layer layer: Area.Layer.values()){
                        //tick templates
	                    SpriteTemplate st = areaGroup.getSpriteTemplate(x, y, layer);
	                    if (st != null){
	                        if (st.getLastVisibleTick() != tick - 1)
	                            if (st.getSprite() == null || !sprites.contains(st.getSprite()))
	                                st.spawn(this, x, y, Facing.DOWN, false);

                            if (st.getSprite() != null)
	                            st.setLastVisibleTick(tick);
	                    }

                        //tick markers
                        for (Marker marker: areaGroup.getMarkers(x, y))
                            marker.tick(this);

                        //add twinkles for ice
                        Block b = areaGroup.getBlock(x, y, layer);
                        if (layer == Area.Layer.Main && !areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Lava)){
                            if (b.frozenCounter == 4 && Math.random() < 0.01f){
                                addSprite(new Sparkle(this, (int)(x*16+Math.random()*16), (int)(y*16+Math.random()*16), Color.WHITE, 0, 0));
                            }
                        }
	                }
                }

            //new lists are to avoid comod issues
            boolean bossPresent = false;
            for (Sprite sprite : new ArrayList<Sprite>(sprites)){
                sprite.tick();
                if (sprite instanceof Prop && ((Prop)sprite).isBoss()) bossPresent = true;
            }

            if (!bossWasPresent && bossPresent) Art.startMusic("TocattaOpera11-Profokiev.mid");
            bossWasPresent = bossPresent;

            //check for water stuff to see if it should melt, freeze, or electrify
            for (Sprite sprite: sprites){
                boolean fire = false, ice = false, elec = false;
                if (sprite instanceof Prop){
                    Prop p = (Prop)sprite;

                    if (p.getImpactDamageAttributes() == null) continue;
                    if (p instanceof Spark) continue;

                    if (p.getImpactDamageAttributes().getAttributes().contains(Attribute.Fire)) fire = true;
                    if (p.getImpactDamageAttributes().getAttributes().contains(Attribute.Freeze))
                        ice = true;
                    if (p.getImpactDamageAttributes().getAttributes().contains(Attribute.Electric)) elec = true;

                    if (!fire && !ice && !elec) continue;

                    int tx = (int)(p.getX()/16d), ty = (int)(p.getY()/16d);
                    checkBlock(tx, ty, fire, ice, elec);
                    checkBlock(tx-1, ty, fire, ice, elec);
                    checkBlock(tx+1, ty, fire, ice, elec);
                    checkBlock(tx, ty-1, fire, ice, elec);
                    checkBlock(tx, ty+1, fire, ice, elec);
                }
            }

            //tick all the block counters
            for (int i = 0; i < areaGroup.getCurrentArea().getWidth(); i++)
                for (int j = 0; j < areaGroup.getCurrentArea().getHeight(); j++)
                    areaGroup.getCurrentArea().getBlock(i, j, Area.Layer.Main).tick();

            for (int i = 0; i < areaGroup.getCurrentArea().getWidth(); i++)
                for (int j = 0; j < areaGroup.getCurrentArea().getHeight(); j++)
                    if (areaGroup.getCurrentArea().getBlock(i, j, Area.Layer.Main).electrifiedCounter > 0){
                        int x = i*16+(int)(Math.random()*16)-4;
                        int y = j*16+(int)(Math.random()*16)+4;
                        addSprite(new Spark(this, x, y));
                    }
        }

        was1 = GameComponent.keys[GameComponent.KEY_ABILITY_1];
        was2 = GameComponent.keys[GameComponent.KEY_ABILITY_2];
        was3 = GameComponent.keys[GameComponent.KEY_ABILITY_3];
        wasMenu = GameComponent.keys[GameComponent.KEY_MENU];
        wasMouseLeft = GameComponent.mouse[1];

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
        Collections.sort(sprites, spriteComparator);
    }

    private void checkBlock(int tileX, int tileY, boolean fire, boolean ice, boolean elec){
        checkBlock(tileX, tileY, Area.Layer.Main, fire, ice, elec);
        checkBlock(tileX, tileY, Area.Layer.Lower, fire, ice, elec);
    }

    private void checkBlock(int tileX, int tileY, Area.Layer layer, boolean fire, boolean ice, boolean elec){
        Block b = areaGroup.getCurrentArea().getBlock(tileX, tileY, layer);
        if (areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Water)
                || areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Lava)){
            if (fire && b.frozenCounter > 0){
                b.dFreeze = -1;
                for (Prop prop: propsBlockingMovement(new Rectangle2D.Double(tileX*16, tileY*16, 16, 16))){
                    prop.resetToLastUnblockedPosition();
                }
            }
            else if (ice){
                b.dFreeze = 1;
                b.electrifiedCounter = 0;
            }
        }

        if (areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Water) ||
                areaGroup.getCurrentArea().getBehavior(b.blockId).contains(Block.Trait.Conductive)){
            if (elec && b.electrifiedCounter == 0 && b.frozenCounter == 0){
                b.electrifiedCounter = 2;
            }
        }
    }

    private Comparator<Sprite> spriteComparator = new Comparator<Sprite>(){
    	public int compare(Sprite s1, Sprite s2){
    		return (int)Math.signum(s1.getX()-s2.getX());
    	}
    };

    public boolean isTunneable(Point tile, Prop tunneller){
        boolean ret = true;

        //if any prop is blocking the tile, ret = false
        List<Prop> blockers = propsBlockingMovement(new Rectangle(tile.x, tile.y, 15, 15));
        blockers.remove(tunneller);
        if (blockers.size() > 0) ret = false;

        //lower tile must be tunnelable
        if (!areaGroup.getCurrentArea().getBehavior(areaGroup.getCurrentArea().getBlock(tile.x, tile.y, Area.Layer.Lower).blockId).contains(Block.Trait.Tunnelable))
            ret = false;

        //main tile must have open footprint
        Block.BlockFootprint blockFp = areaGroup.getCurrentArea().getTileset().getBlockFootprint(
                areaGroup.getCurrentArea().getBlock(tile.x, tile.y, Area.Layer.Main).blockId);
        if (blockFp != Block.BlockFootprint.None) ret = false;

        Block.BlockFootprint pitFp = areaGroup.getCurrentArea().getTileset().getProjectileBlockFootprint(
                areaGroup.getCurrentArea().getBlock(tile.x, tile.y, Area.Layer.Main).blockId);
        if (pitFp != Block.BlockFootprint.None) ret = false;

        return ret;
    }

    public Point tileBlocking(Footprint footprint, boolean flying){
        return areaGroup.tileBlocking(footprint, Area.Layer.Main, flying);
    }

    //src footprint rect is passed in separately to account for if it's translated when doing collision logic
    public List<Prop> propsColliding(Prop src, Rectangle2D srcFoot){
        List<Prop> ret = new ArrayList<Prop>();
        for (Sprite sprite: sprites){
            if (!(sprite instanceof Prop)) continue;
            if (sprite == src) continue;

            Prop prop = (Prop)sprite;
            if (!prop.isCollidable()) continue;
            if (!prop.isCollidableWith(src) || !src.isCollidableWith(prop)) continue;
            if ((prop.isCollidesWithPlayerOnly() && src != player) ||
                    src.isCollidesWithPlayerOnly() && prop != player) continue;
            if (!prop.getFootprint2D().intersects(srcFoot)) continue;

            ret.add(prop);
        }
        return ret;
    }

    public List<Prop> propsBlockingMovement(Rectangle2D srcFoot){
        List<Prop> ret = new ArrayList<Prop>();
        for (Sprite sprite: sprites){
            if (!(sprite instanceof Prop)) continue;

            Prop prop = (Prop)sprite;
            if (!prop.isCollidable()) continue;
            if (!prop.isBlocksMovement()) continue;
            if (!prop.getFootprint2D().intersects(srcFoot)) continue;

            ret.add(prop);
        }
        return ret;
    }

    //src footprint rect is passed in separately to account for if it's translated when doing collision logic
    public List<Prop> propsBlocking(Prop src, Rectangle2D srcFoot){
        List<Prop> blocking = new ArrayList<Prop>();
        for (Prop colliding: propsColliding(src, srcFoot))
            if ((src.isBlockable() && colliding.isBlocksMovement()))
                if (!(src.isFlying() && !colliding.isBlocksFlying()) && !(colliding.isFlying() && !src.isBlocksFlying()))
                    blocking.add(colliding);
    	return blocking;
    }

    public ArrayList<Prop> getDamageablePropsWithinRange(int x, int y, int range){
    	ArrayList<Prop> ret = new ArrayList<Prop>();
    	for (Sprite sprite: sprites){
    		if (sprite instanceof Prop){
                Prop prop = ((Prop)sprite);
	    		if (Point.distance(prop.getFootprint().getCenterX(), prop.getFootprint().getCenterY(), x, y) < range)
                    if (!prop.isImmuneToDamage())
	    			    ret.add(prop);
            }
    	}
    	return ret;
    }

    public void shake(int shakeCounter){
        this.shakeCounter = shakeCounter;
    }

    public void render(Graphics2D g, float alpha){
    	//figure camera position
    	int xCam = (int) (player.getXOld() + (player.getX() - player.getXOld()) * alpha) - 160;
        int yCam = (int) (player.getYOld() + (player.getY() - player.getYOld()) * alpha) - 120;
        if (xCam < 0) xCam = 0;
        if (yCam < 0) yCam = 0;
        if (xCam > areaGroup.getWidth() * 16 - 320) xCam = areaGroup.getWidth() * 16 - 320;
        if (yCam > areaGroup.getHeight() * 16 - 240) yCam = areaGroup.getHeight() * 16 - 240;

        if (areaGroup.getWidth()*16 < 320) xCam /= 2;
        if (areaGroup.getHeight()*16 < 240) yCam /= 2;

        if (shakeCounter > 0 && !paused && !actionPaused){
            xCam += Math.random()*6-3;
            yCam += Math.random()*6-3;
        }

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 320, 240);
        
        //sort sprite into rendering order
        try{ Collections.sort(sprites, Sprite.spriteRenderingComparator); }
        catch(IllegalArgumentException e){}
        
        //paint rest of layers
        for (Area.Layer layer: Area.Layer.values()){
	        renderer.setCam(xCam, yCam);
	        renderer.render(g, tick, (paused||actionPaused)?0:alpha, layer);
	
	        g.translate(-xCam, -yCam);
	        for (Sprite sprite : sprites)
	            if (sprite.getLayer() == layer)
	            	sprite.render(g, alpha);

            for (Marker marker: areaGroup.getCurrentArea().getMarkers(layer))
                marker.render(g, alpha);

	        g.translate(xCam, yCam);
        }
        
        g.setColor(Color.BLACK);

        //paint layer decorator
    	for (LayerDecorator d: areaGroup.getDecorators()) d.paint(g);

        //paint console stuff
    	if (console != null) console.paint(g);

        //fadein/out
        float maxFadeCounter = 16;
        if (player.getTeleportOutTime() > 0){
            renderWhiteout(g, (maxFadeCounter - player.getTeleportOutTime()) / maxFadeCounter);
        }
        else if (player.getTeleportInTime() > 0){
            renderWhiteout(g, (float)player.getTeleportInTime()/maxFadeCounter);
        }
        else if (startTime < maxFadeCounter){
            renderFade(g, (maxFadeCounter-(float)startTime)/maxFadeCounter);
        }
        else if (player.getExitingTime() > 0){
        	renderFade(g, (maxFadeCounter-player.getExitingTime())/maxFadeCounter);
        }
        else if (player.getEnteringTime() > 0){
        	renderFade(g, (float)player.getEnteringTime()/maxFadeCounter);
        }

        //level name
        if (tick < 32){
            Image levelIdImage = TextImageCreator.getOutlinedTextImage(
                    areaGroup.getCurrentArea().getId(), TextImageCreator.COLOR_WHITE, Color.BLACK);
            if (tick > 22) levelIdImage = ImageUtils.fadeImage(levelIdImage, null, (32f-(float)tick)/10f);
            g.drawImage(levelIdImage, 160-levelIdImage.getWidth(null)/2, 120-levelIdImage.getHeight(null)/2, null);
        }

    }
    
    private void renderFade(Graphics g, float percent){
    	g.setColor(new Color(0, 0, 0, (int)(255*percent)));
    	g.fillRect(0, 0, 320, 240);
    }

    private void renderWhiteout(Graphics g, float percent){
        g.setColor(new Color(255, 255, 255, (int)(255*percent)));
        g.fillRect(0, 0, 320, 240);
    }

    public void addSprite(Sprite sprite){
        spritesToAdd.add(sprite);
        sprite.spawned();
        sprite.tick();

        if (sprite instanceof Prop && ((Prop)sprite).isBoss()){
            Art.startMusic("TocattaOpera11-Profokiev.mid");
        }
    }

    public void removeSprite(Sprite sprite){
    	spritesToRemove.add(sprite);
    	if (sprite.getSpriteTemplate() != null)
            sprite.getSpriteTemplate().actorDied();
    }

    public List<Sprite> getSprites(){ return sprites; }

    public List<Prop> getBosses(){
        List<Prop> bosses = new ArrayList<Prop>();
        for (Sprite sprite: sprites)
            if (sprite instanceof Prop)
                if (((Prop)sprite).isBoss())
                    bosses.add((Prop)sprite);
        return bosses;
    }

    public void playerClicked(){
        Point amp = getApparentMousePosition();
        amp.x += xCam;
        amp.y += yCam;
        for (Sprite sprite: getSprites()){
            if (sprite.isClickable() && sprite.getImageFootprint().contains(amp))
                if (sprite != player && Point2D.distance(player.getX(), player.getY(), sprite.getX(), sprite.getY()) <= 32)
                    sprite.receiveMessage(player, PLAYER_CLICK_MESSAGE);
        }
    }

    public void message(Sprite source, String message){
        //persist messages for later replaying if we leave and reenter this area
        List<Object[]> areaMessages = PERSISTED_MESSAGES.get(areaGroup.getCurrentArea().getId());
        if (areaMessages == null){
            areaMessages = new ArrayList<Object[]>();
            PERSISTED_MESSAGES.put(areaGroup.getCurrentArea().getId(), areaMessages);
        }
        areaMessages.add(new Object[]{source, message});

        applyMessage(source, message);
    }

    private void applyMessage(Sprite source, String message){
        //message all the markers
        for (int x = 0; x < areaGroup.getCurrentArea().getWidth(); x++)
            for (int y = 0; y < areaGroup.getCurrentArea().getHeight(); y++)
                for (Area.Layer layer: Area.Layer.values())
                    for (Marker marker: areaGroup.getCurrentArea().getMarkers(x, y, layer))
                        marker.receiveMessage(this, message);

        //message all the actor templates
        for (int x = 0; x < areaGroup.getCurrentArea().getWidth(); x++)
            for (int y = 0; y < areaGroup.getCurrentArea().getHeight(); y++)
                for (Area.Layer layer: Area.Layer.values()){
                    SpriteTemplate at = areaGroup.getCurrentArea().getSpriteTemplate(x, y, layer);
                    if (at != null) at.receiveMessage(source, message);
                }

        //message all the actors
        for (Sprite sprite: new ArrayList<Sprite>(sprites))
                sprite.receiveMessage(source, message);
    }

    public float getX(float alpha){
        int xCam = (int) (player.getXOld() + (player.getX() - player.getXOld()) * alpha) - 160;
        if (xCam < 0) xCam = 0;
        return xCam + 160;
    }
    public float getY(float alpha){ return 0; }

	public Console getConsole(){ return console; }
	public void setConsole(Console console){ this.console = console; }

    public GameState getGameState(){ return gameState; }
    public void setGameState(GameState gameState){ this.gameState = gameState; }

    public void toggleSubscreen(){
		if (getConsole().getConsoleElements().contains(subscreen)) closeSubscreen();
		else openSubscreen();
	}
	
	public boolean isSubscreenOpen(){ return getConsole().getConsoleElements().contains(subscreen); }
	
	public void openSubscreen(){
        getSound().play(Art.getSample("openSubscreen.wav"), this, 1, 1, 1);
        getConsole().openMenu(subscreen);
		subscreen.opened();
	}

    public void closeSubscreen(){
        getSound().play(Art.getSample("closeSubscreen.wav"), this, 1, 1, 1);
        subscreen.closed();
	}

    public Point tileUnderPointOnScreen(Point position){
        int f = 2;
        int mx = (int)((xCam+position.x/f)/16);
        int my = (int)((yCam+position.y/f)/16);
        return new Point(mx, my);
    }

    //noop'd due to reimplementation of updateMouseCursor
    protected boolean isMouseOverHotspot(Point mouseLocation){ return false; }

    private Point lastMousePosition = new Point(0, 0);
    public Point getMousePosition(){ return lastMousePosition; }
    public Point getApparentMousePosition(){ return new Point(lastMousePosition.x/2, lastMousePosition.y/2-12); }

    public double getPlayerMouseAngle(){
        Point playerPosition = new Point(2*(int)(player.getX()-xCam), 2*(int)(player.getY()-yCam));
        double angleRads = Math.atan2(playerPosition.x - lastMousePosition.x, playerPosition.y - lastMousePosition.y);
        angleRads += Math.PI/2; //b/c 0 is up, but arrow points given make it point left
        return angleRads;
    }

    public int getMouseArrowLength(){
        int length;
        Point playerPosition = new Point(2*(int)(player.getX()-xCam), 2*(int)(player.getY()-yCam));
        int xDiff = lastMousePosition.x-playerPosition.x, yDiff = lastMousePosition.y-playerPosition.y;
        double hyp = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
        if (hyp < 10) length = 2;
        else if (hyp > 200) length = 20;
        else length = (int)(hyp/10);
        return length;
    }

    public void updateMouseCursor(Graphics g, Point mousePosition, Point screenCenter){
        if (paused) return;
        if (mousePosition == null) return;
        lastMousePosition = mousePosition;

        if (getConsole().getFocusedMenu() != null){
            Image cursor = getConsole().getFocusedMenu().getCursor();
            g.drawImage(cursor, mousePosition.x/2, mousePosition.y/2, null);
        }
        else{
            //arrow angle based on angle from player
            double angleRads = getPlayerMouseAngle();

            //help player out by setting their heading
            if (!actionPaused || paused)
                if (player.getHeadingLockedCounter() == 0 && !player.isChargingAhead())
                    player.setHeading(angleRads);

            //arrow length based on distance from player, width constant
            int length = getMouseArrowLength();
            int width = 8;

            //offsets so it points at the right spot
            int arrowXLength = (int)(Math.cos(angleRads)*(length+width));
            int arrowYLength = (int)(Math.sin(angleRads)*(length+width));

            //figure out based on whether arrow is over walkable area or clickable actor
            Color arrowColor;
            Point mousePoint = tileUnderPointOnScreen(mousePosition);
            Sprite clickable = null;

            Point amp = getApparentMousePosition();
            amp.x += xCam;
            amp.y += yCam;
            for (Sprite sprite: getSprites())
                if (sprite.isClickable() && sprite.getImageFootprint().contains(amp))
                    clickable = sprite;

            Footprint footprint = new Footprint(
                    new Point2D.Double[]{new Point2D.Double(mousePoint.x*16, mousePoint.y*16), new Point2D.Double(mousePoint.x*16+1, mousePoint.y*16+1)});
            Point blockingTile = areaGroup.tileBlocking(footprint, player.getLayer(), false);

            if (clickable != null){
                if (Point2D.distance(player.getX(), player.getY(), clickable.getX(), clickable.getY()) > 32)
                    arrowColor = new Color(0.5f, 0.5f, 0.5f, 1f);
                else arrowColor = new Color(0f, 0.4f, 1f, 1f);
            }
            else if (blockingTile != null) arrowColor = new Color(1f, 0f, 0f, 1f);
            else arrowColor = new Color(0f, 1f, 0f, 1f);

            //constants
            Color arrowOutlineColor = Color.BLACK;
            Point2D[] ps = new Point2D[]{
                    new Point2D.Double(0,               width/2),
                    new Point2D.Double(length,          width/2),
                    new Point2D.Double(length,          width),
                    new Point2D.Double(length+width,    0),
                    new Point2D.Double(length,          -width),
                    new Point2D.Double(length,          -width/2),
                    new Point2D.Double(0,               -width/2),
            };
            Point2D[] ds = new Point2D[ps.length];

            //rotate and translate it
            AffineTransform at = AffineTransform.getRotateInstance(-angleRads);
            at.transform(ps, 0, ds, 0, 7);

            //turn into integer-precision polygon
            int[] xsi = new int[7], ysi = new int[7];
            for (int i = 0; i < ds.length; i++){
                xsi[i] = (int)ds[i].getX();
                ysi[i] = (int)ds[i].getY();
                xsi[i] += mousePosition.x/2;
                ysi[i] += mousePosition.y/2;
                xsi[i] -= arrowXLength;
                ysi[i] += arrowYLength;
            }
            Polygon polygon = new Polygon(xsi, ysi, 7);

            polygon.translate(0, -12); //lines it up with projectile targeting

            //draw it
            g.setColor(arrowColor);
            g.fillPolygon(polygon);
            g.setColor(arrowOutlineColor);
            g.drawPolygon(polygon);
        }
    }

    public void minibossifyEnemies(){
        for (Sprite sprite: getSprites()){
            if (sprite instanceof Prop)
                ((Prop)sprite).minibossify();
        }
        getSound().play(Art.getSample("lightningStrike.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
    }
}

package com.oblong.af;

import com.mojang.sonar.FakeSoundEngine;
import com.mojang.sonar.SonarSoundEngine;
import com.oblong.af.level.AreaGroupRenderer;
import com.oblong.af.level.AreaScene;
import com.oblong.af.models.*;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.enemy.*;
import com.oblong.af.util.Art;
import com.oblong.af.util.Scale2x;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameComponent extends Canvas implements Runnable {

	private static final long serialVersionUID = 739318775993206607L;
	public static GameComponent INSTANCE;
	
	public static final int TICKS_PER_SECOND = 16;
    
    public static final int KEY_ABILITY_1 = 0;
    public static final int KEY_ABILITY_2 = 1;
    public static final int KEY_ABILITY_3 = 2;
    public static final int KEY_MENU = 3;

    public enum Scenes {
        CharacterSelect{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new PlayerSelectScene(comp, gc);
            }
        },
        Credits{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.credits, Art.creditsBg, Scenes.Title, true, false, "Mallorca-Albeniz.mid");
            }
        },
        Ending{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.ending, Art.howToPlayBg, Scenes.Credits, false, true, "Arabesque-Debussy.mid");
            }
        },
        HowToPlay{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.howToPlay, Art.howToPlayBg, Scenes.Title, true, false, "Mallorca-Albeniz.mid");
            }
        },
        Intro1{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.intro1, Art.howToPlayBg, Scenes.CharacterSelect, false, true, "Cataluna-Albeniz.mid");
            }
        },
        Intro2{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.intro2, Art.howToPlayBg, Scenes.Hub, false, true, "Cataluna-Albeniz.mid");
            }
        },
        Lair{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new AreaScene(gc, comp.getGameState(), World.RESOURCES+World.LEVELS+World.LAIR_AREA_GROUP_ID);
            }
        },
        LoadGame{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new LoadGameScene(comp, gc);
            }
        },
        OpenLair{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.openLair, Art.howToPlayBg, Scenes.Hub, false, true, "Mars-Holst.mid");
            }
        },
        OpenRift{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new MovieScene(comp, gc, Art.openRift, Art.howToPlayBg, Scenes.Rift, false, true, "Mars-Holst.mid");
            }
        },
        Rift{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new AreaScene(gc, comp.getGameState(), World.RESOURCES+World.LEVELS+World.RIFT_AREA_GROUP_ID);
            }
        },
        Hub{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new AreaScene(gc, comp.getGameState(), World.RESOURCES+World.LEVELS+World.HUB_AREA_GROUP_ID);
            }
        },
        Title{
            public Scene createScene(GameComponent comp, GraphicsConfiguration gc){
                return new TitleScene(comp, gc);
            }
        },
        ;
        public abstract Scene createScene(GameComponent comp, GraphicsConfiguration gc);
    }

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private BufferStrategy bufferStrategy;
    private Scene scene;
    private SonarSoundEngine sound;
    private boolean useScale2x = false;
    private Scale2x scale2x = new Scale2x(320, 240);
    private Point lastMousePos;
    private GameState gameState;

    private int framesLastSecond = 0, framesThisSecond = 0;
    private boolean renderFPS = false;

    public static boolean[] keys = new boolean[4];
    public static boolean[] lastKeys = new boolean[4];
    public static boolean[] mouse = new boolean[4];

    private String quickLaunchLevelName, quickLaunchPlayerDefinitionName;

    public GameComponent(int width, int height, String quickLaunchLevelName, String quickLaunchPlayerDefinitionName){
        this(width, height);
        this.quickLaunchLevelName = quickLaunchLevelName;
        this.quickLaunchPlayerDefinitionName = quickLaunchPlayerDefinitionName;
    }

    public GameComponent(int width, int height){
        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        try{ sound = new SonarSoundEngine(64); }
        catch (LineUnavailableException e){
            e.printStackTrace();
            sound = new FakeSoundEngine();
        }

        setFocusable(true);
        INSTANCE = this;

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent arg0){ toggleKey(arg0.getKeyCode(), true); }
            public void keyReleased(KeyEvent arg0){
                toggleKey(arg0.getKeyCode(), false);
                if (arg0.getKeyCode() == KeyEvent.VK_SPACE) scene.toggleSubscreen();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e){ lastMousePos = new Point(e.getX(), e.getY()); }
            public void mouseDragged(MouseEvent e){ mouseMoved(e); }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){ toggleMouse(e.getButton(), true); }
            public void mouseReleased(MouseEvent e){ toggleMouse(e.getButton(), false); }
        });
    }

    public boolean isUseScale2x(){ return useScale2x; }

    public SonarSoundEngine getSound(){ return sound; }

    private void toggle(int key, boolean isPressed){
        keys[key] = isPressed;
    }

    private void toggleKey(int keyCode, boolean isPressed){
        if (keyCode == KeyEvent.VK_Z) toggle(KEY_ABILITY_1, isPressed);
        if (keyCode == KeyEvent.VK_X) toggle(KEY_ABILITY_2, isPressed);
        if (keyCode == KeyEvent.VK_C) toggle(KEY_ABILITY_3, isPressed);
        if (isPressed && keyCode == KeyEvent.VK_F1) renderFPS = !renderFPS;
        if (isPressed && keyCode == KeyEvent.VK_F2) AreaGroupRenderer.renderBehaviors = !AreaGroupRenderer.renderBehaviors;
        if (isPressed && keyCode == KeyEvent.VK_F12) System.exit(0); //TODO: eventually replace this with are you sure etc.
        if (isPressed && keyCode == KeyEvent.VK_Q){
            if (Player.INSTANCE != null) Player.INSTANCE.die();
        }
    }

    private void toggleMouse(int button, boolean isPressed){
        if (button >= mouse.length) System.err.println("Got mouseclick for button "+button+" but only have "+mouse.length+" slots!");
        else mouse[button] = isPressed;
    }

    public void paint(Graphics g){}
    public void update(Graphics g){}

    public void start(){
        if (!running){
            running = true;

            new Thread(new Runnable() {
                public void run() {
                    while(running){
                        framesLastSecond = framesThisSecond;
                        framesThisSecond = 0;
                        try{ Thread.sleep(1000); }
                        catch(InterruptedException ignored){}
                    }
                }
            }).start();

            new Thread(this, "Game Thread").start();
        }
    }

    public void stop(){
        Art.stopMusic();
        Art.closeSequencer();
        running = false;
    }

    public void run(){
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        graphicsConfiguration = getGraphicsConfiguration();

//        scene = new AreaScene(graphicsConfiguration, new GameState(SpriteDefinitions.Dragon), World.HUB_AREA_GROUP_ID);
//        scene.setSound(sound);

        Art.init(graphicsConfiguration, sound);
        World.init();

        VolatileImage image = createVolatileImage(320, 240);
        Graphics g = getGraphics();
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D og = (Graphics2D)image.getGraphics();
        og.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long lastTick = -1;
        double time = System.nanoTime() / 1000000000.0;
        double now = time;
        double averagePassedTime = 0;
        boolean naiveTiming = true;

        if (quickLaunchLevelName != null){
            SpriteDefinitions quickLaunchPlayerDefinition = SpriteDefinitions.valueOf(quickLaunchPlayerDefinitionName);
            gameState = new GameState(quickLaunchPlayerDefinition);
            //apply potential skill
            for (Skills skill: quickLaunchPlayerDefinition.getSkills())
                if (skill == Skills.Potential)
                    gameState.setSpareAffinity(quickLaunchPlayerDefinition.getInitialSpareAffinity());
            teleportToAreaScene(quickLaunchLevelName);
        }
        else toScene(Scenes.Title);

        while (running){
            double lastTime = time;
            time = System.nanoTime() / 1000000000.0;
            double passedTime = time - lastTime;

            if (passedTime < 0) naiveTiming = false; // Stop relying on nanotime if it starts skipping around in time (ie running backwards at least once). This sometimes happens on dual core amds.
            averagePassedTime = averagePassedTime * 0.9 + passedTime * 0.1;

            if (naiveTiming) now = time;
            else now += averagePassedTime;

            g = bufferStrategy.getDrawGraphics();

            long tick = (long) (now * TICKS_PER_SECOND);
            if (lastTick == -1) lastTick = tick;
            while (lastTick < tick){
                scene.tick();
                lastTick++;
            }

            float alpha = (float) (now * TICKS_PER_SECOND - tick);

            //keep playing music
            sound.clientTick(alpha);

            //clear background
            og.setColor(Color.BLACK);
            og.fillRect(0, 0, 320, 240);

            //draw our scene
            scene.render(og, alpha);

            //do mouse stuff
            scene.updateMouseCursor(og, lastMousePos, new Point(320, 240));

            //pause if we don't have focus
            //if (!hasFocus() && scene instanceof AreaScene && !((AreaScene)scene).isSubscreenOpen()) ((AreaScene)scene).openSubscreen();

            //draw the image onto our main canvas, possibly scaled
            if (width != 320 || height != 240){
                if (useScale2x) g.drawImage(scale2x.scale(image), 0, 0, null);
                else g.drawImage(image, 0, 0, 640, 480, null);
            }
            else g.drawImage(image, 0, 0, null);

            //possible draw FPS
            if (renderFPS){
                drawString(g, "FPS: "+framesLastSecond, 5, 5, 0);
                drawString(g, "FPS: "+framesLastSecond, 4, 4, 7);
            }

            //flip the buffer
            g.dispose();
            bufferStrategy.show();

            framesThisSecond++;

            try{ Thread.sleep(5); }
            catch (InterruptedException ignored){}
        }

        Art.stopMusic();
    }

    public static void drawString(Graphics g, String text, int x, int y, int c){
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++)
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
    }

    public void toScene(Scenes sceneDef){
        scene = sceneDef.createScene(this, graphicsConfiguration);
        scene.setSound(sound);
        scene.init();
    }

    public void teleportToAreaScene(String levelFilename){
        scene = new AreaScene(graphicsConfiguration, gameState, World.RESOURCES+World.LEVELS+levelFilename);
        scene.setSound(sound);
        scene.init();
        ((AreaScene)scene).player.setTeleportOutTime(1);
    }

    public void playerSelected(SpriteDefinitions playerDefinition){
        gameState = new GameState(playerDefinition);
    }

    public boolean loadGame(File file) {
        DataInputStream dis = null;
        try{
            dis = new DataInputStream(new FileInputStream(file));
            gameState = GameState.load(dis);
        }
        catch(IOException e){
            System.err.println("Error loading game from "+file.getAbsolutePath());
            e.printStackTrace(System.err);
            return false;
        }
        finally{
            if (dis != null){
                try{ dis.close(); }
                catch(IOException e){
                    System.err.println("Error closing input stream from "+file.getAbsolutePath());
                    e.printStackTrace(System.err);
                }
            }
        }
        return true;
    }

    private Map<File, SpriteDefinitions> playerGameCache = new HashMap<File, SpriteDefinitions>();
    public SpriteDefinitions playerForGame(File file) {
        if (playerGameCache.containsKey(file)) return playerGameCache.get(file);

        DataInputStream dis = null;
        try{
            dis = new DataInputStream(new FileInputStream(file));
            GameState state = GameState.load(dis);
            playerGameCache.put(file, state.getPlayerDefinition());
            return state.getPlayerDefinition();
        }
        catch(IOException e){
            System.err.println("Error loading game from "+file.getAbsolutePath());
            e.printStackTrace(System.err);
            playerGameCache.put(file, null);
            return null;
        }
        finally{
            if (dis != null){
                try{ dis.close(); }
                catch(IOException e){
                    System.err.println("Error closing input stream from "+file.getAbsolutePath());
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public boolean saveGame(File file){
        DataOutputStream dos = null;
        try{
            if (!file.getParentFile().exists()) file.getParentFile().createNewFile();
            if (!file.exists()) file.createNewFile();
            dos = new DataOutputStream(new FileOutputStream(file));
            GameState.save(gameState, dos);
        }
        catch(IOException e){
            System.err.println("Error saving game to "+file.getAbsolutePath());
            e.printStackTrace(System.err);
            return false;
        }
        finally{
            if (dos != null){
                try{ dos.close(); }
                catch(IOException e){
                    System.err.println("Error closing output stream to "+file.getAbsolutePath());
                    e.printStackTrace(System.err);
                }
            }
        }
        return true;
    }

    public GameState getGameState(){ return gameState; }
    public void setGameState(GameState gameState){ this.gameState = gameState; }

}
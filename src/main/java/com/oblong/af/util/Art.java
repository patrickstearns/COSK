package com.oblong.af.util;

import com.mojang.sonar.FixedSoundSource;
import com.mojang.sonar.SonarSoundEngine;
import com.mojang.sonar.sample.SonarSample;
import com.oblong.af.models.World;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Art {
    public static Image[][] objects16x32;
    public static Image[][] objects16x48;
    public static Image[][] objects32x32;
    public static Image[][] objects32x32x2;
    public static Image[][] objects32x64;
    public static Image[][] objects64x64;
    public static Image[][] objects112x128;
    public static Image[][] abilities;
    public static Image[][] characters;
    public static Image[][] portraits;
    public static Image[][] font;
    public static Image[][] bg;
    public static Image[][] editorIcons;
    public static Image[][] consoleIcons8x8;
    public static Image[][] consoleIcons16x16;
    public static Image[][] consoleIcons32x32;
    public static Image[][] projectiles;
    public static Image[][] meleeWeapons;
    public static Image[][] statusEffects;
    public static Image[][] powerups;
    public static Image[][] effects16x16;
    public static Image[][] effects32x32;
    public static Image[][] effects32x48;
    public static Image[][] effects48x48;
    public static Image[][] weapons16x16;
    public static Image[][] weapons32x32;
    public static Image[][] slimeTrail;
    public static Image[][] flame;
    public static Image[][] bosses;
    public static Image[][] explosion;
    public static Image[][] purpleLightning32x32;

    public static Image[][] bat;
    public static Image[][] beetle;
    public static Image[][] beetleGenerator;
    public static Image[][] beholder;
    public static Image[][] demon;
    public static Image[][] feather;
    public static Image[][] fireslime;
    public static Image[][] gooboy;
    public static Image[][] gullibird;
    public static Image[][] harpy;
    public static Image[][] mantis;
    public static Image[][] mimewizard;
    public static Image[][] rabides;
    public static Image[][] rat;
    public static Image[][] rollerpede;
    public static Image[][] seahorses;
    public static Image[][] spikehog;
    public static Image[][] spinnyblob;
    public static Image[][] stonewizard;
    public static Image[][] zombie;
    public static Image[][] spider16x16;
    public static Image[][] spiderGenerator32x16;

    public static Image[][] bossBeholder64x64;
    public static Image[][] bossGigapede32x32;
    public static Image[][] bossGigapede48x48;
    public static Image[][] bossSpider32x16;
    public static Image[][] bossSpider32x48;
    public static Image[][] bossSpider112x112;
    public static Image[][] bossVampire48x48;
    public static Image[][] bossWyvern32x32;
    public static Image[][] bossWyvern112x80;
    public static Image[][] bossStoneKing144x144;

    public static Image[][] titleBackground320x240;
    public static Image[][] titleLogo161x126;
    public static Image[][] titleOptions96x16;

    public static Image characterSelectLogo, continueLogo;
    public static Image howToPlayBg;
    public static Image loadGameBackground, creditsBg;

    public static Image[] howToPlay, intro1, intro2, openLair, openRift, ending, credits;

    private static HashMap<String, SonarSample> samples = new HashMap<String, SonarSample>();
    private static HashMap<String, Sequence> songs = new HashMap<String, Sequence>();
    private static Sequencer sequencer;

    private static GraphicsConfiguration savedGC = null;

    private static final String[] wavs = new String[]{
        "badStatusEffect.wav", "bigLaser.wav", "bite.wav", "bloop.wav", "boing.wav", "bossDeadFanfare.wav", "bullet.wav",
        "cinder.wav", "clang.wav", "cleanse.wav", "click.wav", "closeSubscreen.wav", "collide.wav", "creak.wav",
        "crunch.wav", "deathbolt.wav", "drone.wav", "drown.wav", "electricHum.wav", "electricShock.wav", "error.wav",
        "explosion.wav", "fallingRock.wav", "fireburst.wav", "fireSwoosh.wav", "flap.wav", "freeze.wav", "geyser.wav",
        "goodStatusEffect.wav", "hallelujah.wav", "harpy.wav", "heal.wav", "heavyFootstep.wav", "hit.wav", "iceCrash.wav",
        "laser.wav", "laugh.wav", "lightningStrike.wav", "lightwhiff.wav", "lowfireburst.wav", "mantis.wav", "miniroc.wav",
        "oneUp.wav", "openSubscreen.wav", "physicalStrike.wav", "pickupAbility.wav", "playerDies.wav", "quake.wav",
        "rain.wav", "rat.wav", "rattle.wav", "rescued.wav", "rip.wav", "rolling.wav", "schink.wav", "scratch.wav",
        "scream.wav", "select.wav", "shout.wav", "shroom1.wav", "shroom2.wav", "shroom3.wav", "sizzle.wav", "skshoot.wav",
        "snotPlop.wav", "sonar.wav", "sparks.wav", "splash.wav", "splat.wav", "squash1.wav", "squash2.wav", "squirt.wav",
        "sting.wav", "stomp.wav", "swish.wav", "teleport.wav", "thunkMetal.wav", "tick.wav", "toss.wav", "trash.wav",
        "waawaa.wav", "warble.wav", "weaponWhiff.wav", "whiff.wav", "wind.wav", "windLong.wav", "zombieAttack.wav"
    };

    private static final String[] midis = new String[]{
        "AdagioGMinor-Albinoni.mid", "Andanta-Opera2-Aguado.mid", "Arabesque-Debussy.mid", "CaprichoArabe-Tarrega.mid",
        "CaprichoCatalan-Albeniz.mid", "Cataluna-Albeniz.mid", "HallOfTheMountainKing-Grieg.mid", "HutOfBabaYaga-Mussorgsky.mid",
        "Interlude-Carmen-Bizet.mid", "MakropolousCase-Leos.mid", "Mallorca-Albeniz.mid", "Mars-Holst.mid",
        "Pavana-Faure.mid", "PoissonsDOr-Debussy.mid", "TocattaOpera11-Profokiev.mid",
    };

    public static void init(GraphicsConfiguration gc, SonarSoundEngine sound){
        try{
            abilities = cutImage(gc, World.RESOURCES+"graphics/abilityIconSheet.png", 16, 16);
            objects16x32 = cutImage(gc, World.RESOURCES+"graphics/objects16x32.png", 16, 32);
            objects16x48 = cutImage(gc, World.RESOURCES+"graphics/objects16x48.png", 16, 48);
            objects32x32 = cutImage(gc, World.RESOURCES+"graphics/objects32x32.png", 32, 32);
            objects32x32x2 = cutImage(gc, World.RESOURCES+"graphics/objects32x32.2.png", 32, 32);
            objects32x64 = cutImage(gc, World.RESOURCES+"graphics/objects32x64.png", 32, 64);
            objects64x64 = cutImage(gc, World.RESOURCES+"graphics/objects64x64.png", 64, 64);
            objects112x128 = cutImage(gc, World.RESOURCES+"graphics/objects112x128.png", 112, 128);
            characters = cutImage(gc, World.RESOURCES+"graphics/characters.png", 16, 32);
            portraits = cutImage(gc, World.RESOURCES+"graphics/portraits.png", 32, 32);
            projectiles = cutImage(gc, World.RESOURCES + "graphics/particlesheet.png", 8, 8);
            characterSelectLogo = getImage(gc, World.RESOURCES + "graphics/characterSelect.png");
            continueLogo = getImage(gc, World.RESOURCES + "graphics/continue.png");
            font = cutImage(gc, World.RESOURCES + "graphics/font.gif", 8, 8);
            editorIcons = cutImage(gc, World.RESOURCES+"graphics/editorIconSheet.png", 16, 16);
            meleeWeapons = cutImage(gc, World.RESOURCES+"graphics/meleeWeaponsSheet.png", 16, 16);
            statusEffects = cutImage(gc, World.RESOURCES+"graphics/statusEffectsSheet.png", 16, 16);
            powerups = cutImage(gc, World.RESOURCES+"graphics/powerupsSheet.png", 16, 16);
            consoleIcons8x8 = cutImage(gc, World.RESOURCES+"graphics/console8x8.png", 8, 8);
            consoleIcons16x16 = cutImage(gc, World.RESOURCES+"graphics/console16x16.png", 16, 16);
            consoleIcons32x32 = cutImage(gc, World.RESOURCES+"graphics/console32x32.png", 32, 32);
            effects16x16 = cutImage(gc, World.RESOURCES+"graphics/effects16x16.png", 16, 16);
            effects32x32 = cutImage(gc, World.RESOURCES+"graphics/effects32x32.png", 32, 32);
            effects32x48 = cutImage(gc, World.RESOURCES+"graphics/effects32x48.png", 32, 48);
            effects48x48 = cutImage(gc, World.RESOURCES+"graphics/effects48x48.png", 48, 48);
            weapons16x16 = cutImage(gc, World.RESOURCES+"graphics/weapons16x16.png", 16, 16);
            weapons32x32 = cutImage(gc, World.RESOURCES+"graphics/weapons32x32.png", 32, 32);
            slimeTrail = cutImage(gc, World.RESOURCES+"graphics/slimeTrail16x16.png", 16, 16);
            flame = cutImage(gc, World.RESOURCES+"graphics/flame48x48.png", 48, 48);
            bosses = cutImage(gc, World.RESOURCES+"graphics/bosses.png", 16, 32);
            explosion = cutImage(gc, World.RESOURCES+"graphics/explosion.png", 48, 48);
            purpleLightning32x32 = cutImage(gc, World.RESOURCES+"graphics/purple_lightning32x32.png", 32, 32);
            feather = cutImage(gc, World.RESOURCES+"graphics/feather16x16.png", 16, 16);

            bat = cutImage(gc, World.RESOURCES+"graphics/bat32x32.png", 32, 32);
            beetle = cutImage(gc, World.RESOURCES+"graphics/beetle16x16.png", 16, 16);
            beetleGenerator = cutImage(gc, World.RESOURCES+"graphics/beetleGenerator32x32.png", 32, 32);
            beholder = cutImage(gc, World.RESOURCES+"graphics/beholder32x32.png", 32, 32);
            demon = cutImage(gc, World.RESOURCES+"graphics/demon64x64.png", 64, 64);
            fireslime = cutImage(gc, World.RESOURCES+"graphics/fireslime32x32.png", 32, 32);
            gooboy = cutImage(gc, World.RESOURCES+"graphics/gooboy16x32.png", 16, 32);
            gullibird = cutImage(gc, World.RESOURCES+"graphics/gullibird48x48.png", 48, 48);
            harpy = cutImage(gc, World.RESOURCES+"graphics/harpy64x64.png", 64, 64);
            mantis = cutImage(gc, World.RESOURCES+"graphics/mantis32x32.png", 32, 32);
            mimewizard = cutImage(gc, World.RESOURCES+"graphics/mimewizard48x48.png", 48, 48);
            rabides = cutImage(gc, World.RESOURCES+"graphics/rabide32x48.png", 32, 48);
            rat = cutImage(gc, World.RESOURCES+"graphics/rat32x32.png", 32, 32);
            rollerpede = cutImage(gc, World.RESOURCES+"graphics/rollerpede48x48.png", 48, 48);
            seahorses = cutImage(gc, World.RESOURCES+"graphics/seahorse32x48.png", 32, 48);
            spikehog = cutImage(gc, World.RESOURCES+"graphics/spikehog32x32.png", 32, 32);
            spinnyblob = cutImage(gc, World.RESOURCES+"graphics/spinnyblob32x32.png", 32, 32);
            stonewizard = cutImage(gc, World.RESOURCES+"graphics/stoneWizard48x64.png", 48, 64);
            zombie = cutImage(gc, World.RESOURCES+"graphics/zombie64x48.png", 64, 64);
            spider16x16 = cutImage(gc, World.RESOURCES+"graphics/spider16x16.png", 16, 16);
            spiderGenerator32x16 = cutImage(gc, World.RESOURCES+"graphics/spiderGenerator32x16.png", 32, 16);
            bossBeholder64x64 = cutImage(gc, World.RESOURCES+"graphics/boss_beholder64x64.png", 64, 64);
            bossGigapede32x32 = cutImage(gc, World.RESOURCES+"graphics/boss_gigapede32x32.png", 32, 32);
            bossGigapede48x48 = cutImage(gc, World.RESOURCES+"graphics/boss_gigapede48x48.png", 48, 48);
            bossSpider32x16 = cutImage(gc, World.RESOURCES+"graphics/boss_spider32x16.png", 32, 16);
            bossSpider32x48 = cutImage(gc, World.RESOURCES+"graphics/boss_spider32x48.png", 32, 48);
            bossSpider112x112 = cutImage(gc, World.RESOURCES+"graphics/boss_spider112x112.png", 112, 112);
            bossVampire48x48 = cutImage(gc, World.RESOURCES+"graphics/boss_vampire48x48.png", 48, 48);
            bossWyvern32x32 = cutImage(gc, World.RESOURCES+"graphics/boss_wyvern32x32.png", 32, 32);
            bossWyvern112x80 = cutImage(gc, World.RESOURCES+"graphics/boss_wyvern112x80.png", 112, 80);
            bossStoneKing144x144 = cutImage(gc, World.RESOURCES+"graphics/boss_stoneking144x144.png", 144, 144);

            titleBackground320x240 = cutImage(gc, World.RESOURCES+"graphics/title_background.png", 320, 240);
            titleLogo161x126 = cutImage(gc, World.RESOURCES+"graphics/title_logo.png", 161, 126);
            titleOptions96x16 = cutImage(gc, World.RESOURCES+"graphics/title_options.png", 96, 16);

            howToPlayBg = getImage(gc, World.RESOURCES + "graphics/htp_bg.png");
            loadGameBackground = getImage(gc, World.RESOURCES + "graphics/load_game_bg.png");
            creditsBg = getImage(gc, World.RESOURCES + "graphics/credits_bg.png");
            howToPlay = loadImageSeries(gc, World.RESOURCES + "graphics/htp", ".png");
            intro1 = loadImageSeries(gc, World.RESOURCES + "graphics/intro1", ".png");
            intro2 = loadImageSeries(gc, World.RESOURCES + "graphics/intro2", ".png");
            openLair = loadImageSeries(gc, World.RESOURCES + "graphics/openLair", ".png");
            openRift = loadImageSeries(gc, World.RESOURCES + "graphics/openRift", ".png");
            ending = loadImageSeries(gc, World.RESOURCES + "graphics/ending", ".png");
            credits = loadImageSeries(gc, World.RESOURCES + "graphics/credits", ".png");

            savedGC = gc; //after above so can make sure it's good
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (sound != null){
            try{
                for (String entry: wavs){
                    SonarSample sample = sound.loadSampleFromStream(
                            Art.class.getResourceAsStream(World.RESOURCES+"snd/"+entry));
                    samples.put(entry, sample);
                }
            }
            catch(Exception e){
                System.err.println("Error loading samples: "+e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            for (String entry: midis){
                try{
                    Sequence sequence = MidiSystem.getSequence(Art.class.getResourceAsStream(World.RESOURCES+"mus/"+entry));
                    songs.put(entry, sequence);
                }
                catch(InvalidMidiDataException exc){
                    System.err.println("Unable to load midi date from entry "+entry+":");
                    exc.printStackTrace(System.err);
                }
            }
        }
        catch (Exception e){
            sequencer = null;
            e.printStackTrace();
        }
    }

    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    private static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
            * In case of a jar file, we can't actually find a directory.
            * Have to assume the same jar as clazz.
            */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }

    public static void testSamples(final SonarSoundEngine sound){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (String key: samples.keySet()){
                    System.out.println("Playing sample "+key);
                    sound.play(samples.get(key), new FixedSoundSource(160, 120), 1, 1, 1);
                    try{ Thread.sleep(1000); }
                    catch(Exception ignored){}
                }
            }
        });
        thread.start();
    }

    public static void closeSequencer(){ sequencer.close(); }

    public static Image[] loadImageSeries(GraphicsConfiguration gc, String imagePrefix, String imageSuffix){
        List<Image> loadedImages = new ArrayList<Image>();
        boolean error = false;
        for (int i = 1; !error; i++){
            String filename = imagePrefix+i+imageSuffix;
            try{
                Image image = getImage(gc, filename);
                loadedImages.add(image);
            }
            catch(IOException e){ error = true; }
            catch(IllegalArgumentException e){ error = true; } //happens when image series reaches its end and IOUtils throws error
        }
        return loadedImages.toArray(new Image[loadedImages.size()]);
    }

    public static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException {
		InputStream is = Art.class.getResourceAsStream(imageName);
        BufferedImage source = ImageIO.read(is);
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    public static SonarSample getSample(String key){ return samples.get(key); }

    public static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException {
    	if (gc == null) gc = savedGC;
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++){
            for (int y = 0; y < source.getHeight(null) / ySize; y++){
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.TRANSLUCENT);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }
        return images;
    }

    public static void startMusic(String key){
        if (sequencer != null){
            try{
                Sequence newSequence = songs.get(key);
                if (sequencer.getSequence() == newSequence) return;
                if (newSequence == null) throw new IllegalArgumentException("Unknown sequence: "+key);
                stopMusic();
                sequencer.open();
                sequencer.setSequence((Sequence)null);
                sequencer.setSequence(newSequence);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
            }
            catch (Exception e){
                if (e instanceof IllegalArgumentException)
                    e.printStackTrace();
            }
        }
    }

    public static void stopMusic(){
        if (sequencer != null){
            try{
                sequencer.stop();
                sequencer.close();
            }
            catch (Exception ignored){}
        }
    }
}
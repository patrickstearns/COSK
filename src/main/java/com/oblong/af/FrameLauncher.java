package com.oblong.af;

import com.oblong.af.level.Area;
import com.oblong.af.util.Art;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class FrameLauncher{

    private static final int WIDTH = 640, HEIGHT = 480;

    public static void main(String[] args){
        GameComponent game;
        boolean fullscreen = false;

        if (args.length > 0) game = new GameComponent(WIDTH, HEIGHT, args[1], args[0]);
        else{
            String option = (String)JOptionPane.showInputDialog(null,
                    "Do you want to run the game in fullscreen or windowed mode?  Some systems have issues with fullscreen.",
                    "Curse of the Stone King", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Fullscreen", "Windowed"},
                    "Fullscreen");
            if ("Fullscreen".equals(option)) fullscreen = true;
            game = new GameComponent(WIDTH, HEIGHT);
        }

        JFrame frame = createFrame(args.length > 0, game, fullscreen);

        if (!fullscreen){
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        }
        else{
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (!device.isFullScreenSupported()){
                JOptionPane.showMessageDialog(null, "This graphics device does not support fullscreen mode.",
                        "Fullscreen Mode Not Supported", JOptionPane.ERROR_MESSAGE);
                game = null;
                frame = null;
                System.exit(1);
            }
            else{
                DisplayMode mode = getBestDisplayMode(device);
                if (mode == null){
                    JOptionPane.showMessageDialog(null, "No appropriate screen resolution is supported by this graphics device.",
                            "No Supported Screen Resolution", JOptionPane.ERROR_MESSAGE);
                    game = null;
                    frame = null;
                    System.exit(1);
                }

                System.out.println("Attempting to use display mode: " + mode.getWidth() + "x" + mode.getHeight() + ", " + mode.getBitDepth() + " bit depth, " + mode.getRefreshRate());

                device.setFullScreenWindow(frame);
                device.setDisplayMode(mode);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        }

        frame.getContentPane().setCursor(
                Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR),
                        new Point(1, 1), "BLANK"));

        frame.validate();
        frame.setVisible(true);
        game.start();
    }

    public static void main(Area levelDef){
        final GameComponent game = new GameComponent(WIDTH, HEIGHT);
        JFrame frame = createFrame(true, game, false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.validate();
        frame.setVisible(true);
        game.start();
    }

    private static JFrame createFrame(boolean testMode, final GameComponent game, boolean fullscreen){
        JFrame frame = new JFrame("Curse of the Stone King");
        frame.setResizable(false);
        if (fullscreen){
            frame.setUndecorated(true);
            frame.setLayout(new GridLayout(1, 1));
            frame.add(game);
        }
        else{
            JPanel contentPane = new JPanel(new BorderLayout());
            contentPane.add(game, BorderLayout.CENTER);
            frame.setContentPane(contentPane);

            if (!testMode){
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        game.stop();
                    }
                });
            }
            else{
                frame.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        Art.stopMusic();
                    }
                });
            }


        }
        frame.pack();

        return frame;
    }

    private static DisplayMode getBestDisplayMode(GraphicsDevice device){
        DisplayMode[] modes = device.getDisplayModes();

        Comparator<DisplayMode> comparator = new Comparator<DisplayMode>(){
            public int compare(DisplayMode mode1, DisplayMode mode2){
                if (mode1.getBitDepth() != mode2.getBitDepth()) return mode2.getBitDepth()-mode1.getBitDepth();
                else if (mode1.getWidth() != mode2.getWidth()) return mode1.getWidth() - mode2.getWidth();
                else return mode1.getHeight() - mode2.getHeight();
            }
        };

        Arrays.sort(modes, comparator);

        for (DisplayMode dm: modes){
            if (dm.getWidth() < 320 || dm.getHeight() < 240) continue;
//			if (dm.getWidth()/dm.getHeight() != (8/5)) continue;
//			if (dm.getBitDepth() == 8) continue;
            return dm;
        }

        return null;
    }

}
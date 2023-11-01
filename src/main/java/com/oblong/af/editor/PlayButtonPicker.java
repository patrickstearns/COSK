package com.oblong.af.editor;

import com.oblong.af.models.SpriteDefinitions;
import com.oblong.af.util.Art;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class PlayButtonPicker extends JComponent implements ChangeListener {

    private static Image createPlayButtonIcon(Image baseImage){
        BufferedImage image = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
        image.getGraphics().drawImage(baseImage, 0, 0, null);
        image.getGraphics().drawImage(Art.editorIcons[9][4], image.getWidth()-16, image.getHeight()-18, null);
        return image;
    }

    private List<SpriteDefinitions> spriteDefs;
    private SpriteDefinitions selectedDef; //among our defs
    private JPopupMenu popup;
    private boolean selected;
    private JLabel imageLabel;

    public PlayButtonPicker(final LevelEditor levelEditor, List<SpriteDefinitions> spriteDefs){
        this.spriteDefs = spriteDefs;

        Dimension prefSize = new Dimension(60, 30);
        setMinimumSize(prefSize);
        setPreferredSize(prefSize);
        setMaximumSize(prefSize);

        setBorder(BorderFactory.createLineBorder(new Color(0.55f, 0.6f, 0.7f), 1));

        JButton popupButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        imageLabel = new JLabel();
        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(popupButton, BorderLayout.EAST);

        popupButton.setSelected(true);
        popupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPopup();
            }
        });

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if (selectedDef == null) return;
                setSelected(true);
                popup.setVisible(false);
                com.oblong.af.FrameLauncher.main(new String[]{getSelectedDefinition().name(), levelEditor.getAreaGroup().getFile().getName()});
            }
        });

        popup = buildPopup(spriteDefs);

        setSelectedDefinition(spriteDefs.get(0));
        Toolbox.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e){
        setSelected(false);
    }

    public boolean isSelected(){ return selected; }
    public void setSelected(boolean selected){
        this.selected = selected;
        imageLabel.setBackground(selected ? UIManager.getColor("Menu.selectionBackground") : UIManager.getColor("Menu.background"));
    }

    public SpriteDefinitions getSelectedDefinition(){ return selectedDef; }
    public void setSelectedDefinition(SpriteDefinitions selectedDef){
        this.selectedDef = selectedDef;
        imageLabel.setIcon(new ImageIcon(createPlayButtonIcon(selectedDef.getSheet()[selectedDef.getXPic()][selectedDef.getYPic()])));
    }

    private class ToolToggleButton extends JToggleButton {
        public ToolToggleButton(final SpriteDefinitions def){
            super(new ImageIcon(createPlayButtonIcon(def.getSheet()[def.getXPic()][def.getYPic()])));

            addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    setSelectedDefinition(def);
                    setSelected(true);
                    popup.setVisible(false);
                }
            });
        }
    }

    private JPopupMenu buildPopup(List<SpriteDefinitions> defs){
        JPopupMenu popup = new JPopupMenu();

        //figure max size of all sprites in list
        int maxWidth = 32, maxHeight = 32;
        for (SpriteDefinitions def: defs){
            int spriteWidth = def.getSheet()[0][0].getWidth(null);
            int spriteHeight = def.getSheet()[0][0].getHeight(null);
            if (spriteWidth > maxWidth) maxWidth = spriteWidth;
            if (spriteHeight > maxHeight) maxHeight = spriteHeight;
        }

        //figure out sizes and def counts
        int popupWidth = 300;
        if (maxWidth == 32 && maxHeight == 32) popupWidth = 160;

        int tw = popupWidth/maxWidth;
        popupWidth = tw*maxWidth;
        int th = defs.size()/tw;
        if (defs.size()%tw > 0) th++;
        int popupHeight = th*maxHeight;

        if (popupHeight > 600){
            popupWidth = 600;
            tw = popupWidth/maxWidth;
            popupWidth = tw*maxWidth;
            th = defs.size()/tw;
            if (defs.size()%tw > 0) th++;
            popupHeight = th*maxHeight;
        }

        popup.setPopupSize(popupWidth, popupHeight);
        popup.setLayout(new GridLayout(th, tw));

        //create actual items
        ButtonGroup buttonGroup = new ButtonGroup();
        for (SpriteDefinitions def: defs){
            ToolToggleButton toggleButton = new ToolToggleButton(def);
            toggleButton.setToolTipText(def.name());
            buttonGroup.add(toggleButton);
            popup.add(toggleButton);
        }

        return popup;
    }

    private void openPopup(){
        popup.setLocation(getX(), getY() + getHeight());
        popup.show(this, 0, getHeight());
    }

    public String getToolTipText(){ return "Test Level with "+selectedDef.name(); }

}

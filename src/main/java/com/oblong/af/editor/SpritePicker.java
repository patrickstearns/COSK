package com.oblong.af.editor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SpritePicker extends JComponent implements ChangeListener {

    private List<Toolbox.Tool> tools;
    private Toolbox.Tool selectedTool; //among our tools
    private JPopupMenu popup;
    private boolean selected;
    private JButton popupButton;
    private JLabel imageLabel;

    public SpritePicker(List<Toolbox.Tool> tools){
        this.tools = tools;

        Dimension prefSize = new Dimension(60, 30);
        setMinimumSize(prefSize);
        setPreferredSize(prefSize);
        setMaximumSize(prefSize);

        setBorder(BorderFactory.createLineBorder(new Color(0.55f, 0.6f, 0.7f), 1));

        popupButton = new BasicArrowButton(BasicArrowButton.SOUTH);
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
                if (selectedTool == null) return;
                Toolbox.setSelectedTool(selectedTool);
                setSelected(true);
                popup.setVisible(false);
            }
        });

        popup = buildPopup(tools);

        setSelectedTool(tools.get(0));
        Toolbox.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e){
        Toolbox.Tool selectedTool = (Toolbox.Tool)e.getSource();
        boolean select = false;
        for (Toolbox.Tool tool: tools)
            if (tool == selectedTool)
                select = true;
        setSelected(select);
    }

    public boolean isSelected(){ return selected; }
    public void setSelected(boolean selected){
        this.selected = selected;
        imageLabel.setBackground(selected ? UIManager.getColor("Menu.selectionBackground") : UIManager.getColor("Menu.background"));
    }

    public Toolbox.Tool getSelectedTool(){ return selectedTool; }
    public void setSelectedTool(Toolbox.Tool selectedTool){
        this.selectedTool = selectedTool;
        imageLabel.setIcon(new ImageIcon(selectedTool.getSheet()[selectedTool.getImageX()][selectedTool.getImageY()]));
        if (selectedTool.getSpriteDefinition() != null && selectedTool.getSpriteDefinition().ability != null)
            imageLabel.setIcon(new ImageIcon(selectedTool.getSpriteDefinition().ability.getBigIcon()));
    }

    private class ToolToggleButton extends JToggleButton {
        public ToolToggleButton(final Toolbox.Tool tool){
            super(new ImageIcon(tool.getSheet()[tool.getImageX()][tool.getImageY()]));

            //this is actually a powerup, so use ability icon instead
            if (tool.getSpriteDefinition() != null && tool.getSpriteDefinition().ability != null){
                setIcon(new ImageIcon(tool.getSpriteDefinition().ability.getBigIcon()));
            }

            addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    Toolbox.setSelectedTool(tool);
                    setSelectedTool(tool);
                    setSelected(true);
                    popup.setVisible(false);
                }
            });
        }
    }

    private JPopupMenu buildPopup(List<Toolbox.Tool> tools){
        JPopupMenu popup = new JPopupMenu();

        //figure max size of all sprites in list
        int maxWidth = 32, maxHeight = 32;
        for (Toolbox.Tool tool: tools){
            int spriteWidth = tool.getSheet()[0][0].getWidth(null);
            int spriteHeight = tool.getSheet()[0][0].getHeight(null);
            if (spriteWidth > maxWidth) maxWidth = spriteWidth;
            if (spriteHeight > maxHeight) maxHeight = spriteHeight;
        }

        //figure out sizes and tool counts
        int popupWidth = 300;
        if (maxWidth == 32 && maxHeight == 32) popupWidth = 160;

        int tw = popupWidth/maxWidth;
        popupWidth = tw*maxWidth;
        int th = tools.size()/tw;
        if (tools.size()%tw > 0) th++;
        int popupHeight = th*maxHeight;

        if (popupHeight > 600){
            popupWidth = 600;
            tw = popupWidth/maxWidth;
            popupWidth = tw*maxWidth;
            th = tools.size()/tw;
            if (tools.size()%tw > 0) th++;
            popupHeight = th*maxHeight;
        }

        popup.setPopupSize(popupWidth, popupHeight);
        popup.setLayout(new GridLayout(th, tw));

        //create actual items
        ButtonGroup buttonGroup = new ButtonGroup();
        for (Toolbox.Tool tool: tools){
            ToolToggleButton toggleButton = new ToolToggleButton(tool);
            toggleButton.setToolTipText(tool.getName());
            buttonGroup.add(toggleButton);
            popup.add(toggleButton);
        }

        return popup;
    }

    private void openPopup(){
        popup.setLocation(getX(), getY() + getHeight());
        popup.show(this, 0, getHeight());
    }

    public String getToolTipText(){ return selectedTool.getName(); }

}

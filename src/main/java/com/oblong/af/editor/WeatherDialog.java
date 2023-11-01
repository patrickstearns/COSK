package com.oblong.af.editor;

import com.oblong.af.level.Area;
import com.oblong.af.level.AreaGroup;
import com.oblong.af.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class WeatherDialog extends JDialog implements ActionListener {

	private JComboBox weatherCombo, lightLevelCombo;
    private JTextField musicKey;
	private JButton ok, cancel;
	private boolean cancelled;
	
	public WeatherDialog(JFrame parent){
		super(parent, "Weather", true);

		weatherCombo = new JComboBox<AreaGroup.Weather>(new DefaultComboBoxModel<AreaGroup.Weather>(AreaGroup.Weather.values()));
        weatherCombo.setSelectedIndex(0);
        weatherCombo.setEditable(false);
        weatherCombo.setRenderer(new DefaultListCellRenderer(){
        	public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focused){
        		JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, selected, focused);
        		if (value != null) ret.setText(((AreaGroup.Weather)value).name());
        		return ret;
        	}
        });
        
		lightLevelCombo = new JComboBox(new DefaultComboBoxModel(AreaGroup.LightLevel.values()));
        lightLevelCombo.setSelectedIndex(0);
        lightLevelCombo.setEditable(false);
        lightLevelCombo.setRenderer(new DefaultListCellRenderer(){
        	public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focused){
        		JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, selected, focused);
        		if (value != null) ret.setText(((AreaGroup.LightLevel)value).name());
        		return ret;
        	}
        });


        musicKey = new JTextField();

        ok = new JButton("OK");
        cancel = new JButton("Cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);

        //Create and populate the main panel
        String[] labels = {"Weather: ", "Light: ", "Music Key: "};
        JComponent[] components = { weatherCombo, lightLevelCombo, musicKey };
        int numPairs = labels.length;
        JPanel mainPanel = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);
            mainPanel.add(l);
            l.setLabelFor(components[i]);
            mainPanel.add(components[i]);
        }

        //Lay out the panel
        SpringUtilities.makeCompactGrid(mainPanel,
                numPairs, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(ok);
        buttonBox.add(cancel);
        buttonBox.add(Box.createHorizontalGlue());

        JPanel content = new JPanel(new BorderLayout());
        content.add(mainPanel, BorderLayout.CENTER);
        content.add(buttonBox, BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setContentPane(content);

        setSize(new Dimension(260, 190));
		setLocation(400, 400);
	}

	private void clear(){
		weatherCombo.setSelectedItem(null);
		lightLevelCombo.setSelectedItem(null);
		musicKey.setText("");
		cancelled = true;
	}

	public boolean isCancelled(){ return cancelled; }
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == ok) cancelled = false;
		setVisible(false);
	}
	
	public void setLevelArea(Area area){
		clear();
		weatherCombo.setSelectedItem(area.getWeather());
		lightLevelCombo.setSelectedItem(area.getLightLevel());
		musicKey.setText(area.getMusicKey());
	}
	
	public void updateLevelArea(Area area){
		area.setWeather((AreaGroup.Weather)weatherCombo.getSelectedItem());
		area.setLightLevel((AreaGroup.LightLevel)lightLevelCombo.getSelectedItem());
        area.setMusicKey(musicKey.getText());
    }
}

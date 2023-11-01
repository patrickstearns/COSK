package com.oblong.af.editor;

import com.oblong.af.level.Area;
import com.oblong.af.level.Tileset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@SuppressWarnings("serial")
public class NewAreaDialog extends JDialog implements ActionListener {

	private JTextField width, height, id;
	private JButton ok, cancel;
	private boolean cancelled = true;
	
	public NewAreaDialog(JFrame parent){
		super(parent, "New Area", true);

		width = new JTextField();
		height = new JTextField();
		id = new JTextField();

		ok = new JButton("OK");
		ok.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		JPanel mainPanel = new JPanel(new GridLayout(2, 1));
		Box nameBox = Box.createHorizontalBox();
		nameBox.add(new JLabel("Name:"));
		nameBox.add(id);
		mainPanel.add(nameBox);
		
		Box dimBox = Box.createHorizontalBox();
		dimBox.add(new JLabel("W x H:"));
		dimBox.add(width);
		dimBox.add(new JLabel("x"));
		dimBox.add(height);
		dimBox.add(Box.createHorizontalGlue());
		mainPanel.add(dimBox);

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
		
		setSize(new Dimension(260, 110));
		setResizable(false);
		setLocation(400, 400);
	}

	public void clear(){
		id.setText("");
		height.setText("");
		width.setText("");
		cancelled = true;
	}

	public boolean isCancelled(){ return cancelled; }
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == ok) cancelled = false;
		setVisible(false);
	}
	
	public Area createLevelArea(){
		String idStr = id.getText();
		short widthShort = Short.parseShort(width.getText());
		short heightShort = Short.parseShort(height.getText());
		Area ret = new Area(idStr, widthShort, heightShort);
		try{ ret.setTileset(Tileset.getTileset(getGraphicsConfiguration())); }
		catch(IOException e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public void setLevelArea(Area area){
		clear();
		id.setText(area.getId());
		width.setText(""+area.getWidth());
		height.setText(""+area.getHeight());
	}
	
	public void updateLevelArea(Area area){
		String idStr = id.getText();
		short widthShort = Short.parseShort(width.getText());
		short heightShort = Short.parseShort(height.getText());
		area.setId(idStr);
		area.setWidth(widthShort);
		area.setHeight(heightShort);
		area.resize();
	}
}

package com.oblong.af.editor;

import com.oblong.af.level.Marker;
import com.oblong.af.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkerDialog extends JDialog {

    private Marker marker;
    private JTextField markerIdTF, delayTicksTF;
    private JTextField activatingMessagesTF, deactivatingMessagesTF, enablingMessagesTF, disablingMessagesTF,
            onActivationMessagesTF, onDeactivationMessagesTF;

    public MarkerDialog(JFrame parent, Marker marker){
        super(parent, "Edit Messages", true);
        this.marker = marker;

        markerIdTF = new JTextField(marker.getId());
        delayTicksTF = new JTextField(""+marker.getDelayTicks());
        activatingMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getActivatingMessages(), ","));
        deactivatingMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getDeactivatingMessages(), ","));
        enablingMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getEnablingMessages(), ","));
        disablingMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getDisablingMessages(), ","));
        onActivationMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getOnActivationMessages(), ","));
        onDeactivationMessagesTF = new JTextField(StringUtils.join(marker.getMessages().getOnDeactivationMessages(), ","));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
                setVisible(false);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel mainPanel = new JPanel(null);

        int row = 0;
        int inset = 2;
        int col1Width = 150, col2Width = 400, cbWidth = 100;
        int rowHeight = 20;

        JLabel idLabel = new JLabel("Marker ID:");
        idLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(idLabel);
        markerIdTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(markerIdTF);
        row++;

        JLabel dtLabel = new JLabel("Delay Ticks:");
        dtLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(dtLabel);
        delayTicksTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(delayTicksTF);
        row++;

        JLabel aLabel = new JLabel("Activating:");
        aLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(aLabel);
        activatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(activatingMessagesTF);
        row++;

        JLabel dLabel = new JLabel("Deactivating:");
        dLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(dLabel);
        deactivatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(deactivatingMessagesTF);
        row++;

        JLabel eLabel = new JLabel("Enabling:");
        eLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(eLabel);
        enablingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(enablingMessagesTF);
        row++;

        JLabel diLabel = new JLabel("Disabling:");
        diLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(diLabel);
        disablingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(disablingMessagesTF);
        row++;

        JLabel oaLabel = new JLabel("On Activation:");
        oaLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(oaLabel);
        onActivationMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActivationMessagesTF);
        row++;

        JLabel odLabel = new JLabel("On Deactivation:");
        odLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(odLabel);
        onDeactivationMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onDeactivationMessagesTF);
        row++;

        Box buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(Box.createHorizontalGlue());
        buttonsBox.add(cancelButton);
        buttonsBox.add(Box.createHorizontalStrut(4));
        buttonsBox.add(okButton);
        buttonsBox.add(Box.createHorizontalGlue());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsBox, BorderLayout.SOUTH);

        setSize(574, 296);
        setLocation(300, 200);
    }

    private void ok(){
        int dt = 0;
        try {
            dt = Integer.parseInt(delayTicksTF.getText());
            marker.setDelayTicks(dt);
        }
        catch(Exception ignored){
            JOptionPane.showMessageDialog(this, "Delay Ticks field must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        marker.setId(markerIdTF.getText());

        String am = "".equals(activatingMessagesTF.getText()) ? null : activatingMessagesTF.getText();
        List<String> ams = new ArrayList<String>();
        if (am != null) ams = StringUtils.split(am, ",");
        marker.getMessages().setActivatingMessages(ams);

        String dm = "".equals(deactivatingMessagesTF.getText()) ? null : deactivatingMessagesTF.getText();
        List<String> dms = new ArrayList<String>();
        if (dm != null) dms = StringUtils.split(dm, ",");
        marker.getMessages().setDeactivatingMessages(dms);

        String em = "".equals(enablingMessagesTF.getText()) ? null : enablingMessagesTF.getText();
        List<String> ems = new ArrayList<String>();
        if (em != null) ems = StringUtils.split(em, ",");
        marker.getMessages().setEnablingMessages(ems);

        String im = "".equals(disablingMessagesTF.getText()) ? null : disablingMessagesTF.getText();
        List<String> ims = new ArrayList<String>();
        if (im != null) ims = StringUtils.split(im, ",");
        marker.getMessages().setDisablingMessages(ims);

        String oam = "".equals(onActivationMessagesTF.getText()) ? null : onActivationMessagesTF.getText();
        List<String> oams = new ArrayList<String>();
        if (oam != null) oams = StringUtils.split(oam, ",");
        marker.getMessages().setOnActivationMessages(oams);

        String odm = "".equals(onDeactivationMessagesTF.getText()) ? null : onDeactivationMessagesTF.getText();
        List<String> odms = new ArrayList<String>();
        if (odm != null) odms = StringUtils.split(odm, ",");
        marker.getMessages().setOnDeactivationMessages(odms);
    }
}

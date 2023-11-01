package com.oblong.af.editor;

import com.oblong.af.level.AreaScene;
import com.oblong.af.level.SpriteTemplate;
import com.oblong.af.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMessagesDialog extends JDialog {

    private SpriteTemplate template;
    private JTextField templateActivatingMessagesTF, templateDeactivatingMessagesTF, templateEnablingMessagesTF, templateDisablingMessagesTF;
    private JTextField actorActivatingMessagesTF, actorDeactivatingMessagesTF;
    private JTextField onActorActivationMessagesTF, onActorDeactivationMessagesTF, onActorSpawnMessagesTF, onActorDieMessagesTF, onActorCriticalMessagesTF;
    private JCheckBox actorActivatingCB, actorDeactivatingCB, bossCB, defaultActiveCB;

    public EditMessagesDialog(JFrame parent, SpriteTemplate template){
        super(parent, "Edit Messages", true);
        this.template = template;

        templateActivatingMessagesTF = new JTextField(StringUtils.join(template.getTemplateMessages().getActivatingMessages(), ","));
        templateDeactivatingMessagesTF = new JTextField(StringUtils.join(template.getTemplateMessages().getDeactivatingMessages(), ","));
        templateEnablingMessagesTF = new JTextField(StringUtils.join(template.getTemplateMessages().getEnablingMessages(), ","));
        templateDisablingMessagesTF = new JTextField(StringUtils.join(template.getTemplateMessages().getDisablingMessages(), ","));

        boolean isClickActivateable = template.getActorMessages().getActivatingMessages().contains(AreaScene.PLAYER_CLICK_MESSAGE);
        List<String> ams = new ArrayList<String>(template.getActorMessages().getActivatingMessages());
        if (isClickActivateable) ams.remove(AreaScene.PLAYER_CLICK_MESSAGE);
        String activatingMessagesString = StringUtils.join(ams, ",");
        actorActivatingMessagesTF = new JTextField(activatingMessagesString);

        boolean isClickDeactivateable = template.getActorMessages().getDeactivatingMessages().contains(AreaScene.PLAYER_CLICK_MESSAGE);
        List<String> dms = new ArrayList<String>(template.getActorMessages().getDeactivatingMessages());
        if (isClickDeactivateable) dms.remove(AreaScene.PLAYER_CLICK_MESSAGE);
        String deactivatingMessagesString = StringUtils.join(dms, ",");
        actorDeactivatingMessagesTF = new JTextField(deactivatingMessagesString);

        onActorActivationMessagesTF = new JTextField(StringUtils.join(template.getActorMessages().getOnActivationMessages(), ","));
        onActorDeactivationMessagesTF = new JTextField(StringUtils.join(template.getActorMessages().getOnDeactivationMessages(), ","));
        onActorSpawnMessagesTF = new JTextField(StringUtils.join(template.getActorMessages().getOnSpawnMessages(), ","));
        onActorDieMessagesTF = new JTextField(StringUtils.join(template.getActorMessages().getOnDieMessages(), ","));
        onActorCriticalMessagesTF = new JTextField(StringUtils.join(template.getActorMessages().getOnCriticalMessages(), ","));

        actorActivatingCB = new JCheckBox("Clickable", isClickActivateable);
        actorDeactivatingCB = new JCheckBox("Clickable", isClickDeactivateable);
        bossCB = new JCheckBox("Boss", template.isBoss());
        defaultActiveCB = new JCheckBox("Active by Default", template.isActiveByDefault());

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

        JLabel aLabel = new JLabel("Template-Activating:");
        aLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(aLabel);
        templateActivatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(templateActivatingMessagesTF);
        row++;

        JLabel dLabel = new JLabel("Template-Deactivating:");
        dLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(dLabel);
        templateDeactivatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(templateDeactivatingMessagesTF);
        row++;

        JLabel eLabel = new JLabel("Template-Enabling:");
        eLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(eLabel);
        templateEnablingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(templateEnablingMessagesTF);
        row++;

        JLabel diLabel = new JLabel("Template-Disabling:");
        diLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(diLabel);
        templateDisablingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(templateDisablingMessagesTF);
        row++;


        JLabel aaLabel = new JLabel("Actor-Activating:");
        aaLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(aaLabel);
        actorActivatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width-cbWidth, rowHeight);
        mainPanel.add(actorActivatingMessagesTF);

        actorActivatingCB.setBounds(inset + col1Width + inset + col2Width - cbWidth, inset + row * rowHeight, cbWidth, rowHeight);
        mainPanel.add(actorActivatingCB);
        row++;

        JLabel adLabel = new JLabel("Actor-Deactivating:");
        adLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(adLabel);
        actorDeactivatingMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width-cbWidth, rowHeight);
        mainPanel.add(actorDeactivatingMessagesTF);

        actorDeactivatingCB.setBounds(inset + col1Width + inset + col2Width - cbWidth, inset + row * rowHeight, cbWidth, rowHeight);
        mainPanel.add(actorDeactivatingCB);
        row++;

        JLabel oaLabel = new JLabel("On Activation:");
        oaLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(oaLabel);
        onActorActivationMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActorActivationMessagesTF);
        row++;

        JLabel odLabel = new JLabel("On Deactivation:");
        odLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(odLabel);
        onActorDeactivationMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActorDeactivationMessagesTF);
        row++;

        JLabel osLabel = new JLabel("On Spawn:");
        osLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(osLabel);
        onActorSpawnMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActorSpawnMessagesTF);
        row++;

        JLabel ocLabel = new JLabel("On Critical HP:");
        ocLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(ocLabel);
        onActorCriticalMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActorCriticalMessagesTF);
        row++;

        JLabel oiLabel = new JLabel("On Death:");
        oiLabel.setBounds(inset, inset + row * rowHeight, col1Width, rowHeight);
        mainPanel.add(oiLabel);
        onActorDieMessagesTF.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(onActorDieMessagesTF);
        row++;

        bossCB.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(bossCB);
        row++;

        defaultActiveCB.setBounds(inset + col1Width + inset, inset + row * rowHeight, col2Width, rowHeight);
        mainPanel.add(defaultActiveCB);
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

        setSize(574, 336);
        setLocation(300, 200);
    }

    private void ok(){
        String am = "".equals(templateActivatingMessagesTF.getText()) ? null : templateActivatingMessagesTF.getText();
        List<String> ams = new ArrayList<String>();
        if (am != null) ams = StringUtils.split(am, ",");
        template.getTemplateMessages().setActivatingMessages(ams);

        String dm = "".equals(templateDeactivatingMessagesTF.getText()) ? null : templateDeactivatingMessagesTF.getText();
        List<String> dms = new ArrayList<String>();
        if (dm != null) dms = StringUtils.split(dm, ",");
        template.getTemplateMessages().setDeactivatingMessages(dms);

        String em = "".equals(templateEnablingMessagesTF.getText()) ? null : templateEnablingMessagesTF.getText();
        List<String> ems = new ArrayList<String>();
        if (em != null) ems = StringUtils.split(em, ",");
        template.getTemplateMessages().setEnablingMessages(ems);

        String im = "".equals(templateDisablingMessagesTF.getText()) ? null : templateDisablingMessagesTF.getText();
        List<String> ims = new ArrayList<String>();
        if (im != null) ims = StringUtils.split(im, ",");
        template.getTemplateMessages().setDisablingMessages(ims);

        String aam = "".equals(actorActivatingMessagesTF.getText()) ? null : actorActivatingMessagesTF.getText();
        List<String> aams = new ArrayList<String>();
        if (aam != null) aams = StringUtils.split(aam, ",");
        if (actorActivatingCB.isSelected()) aams.add(AreaScene.PLAYER_CLICK_MESSAGE);
        else if (actorActivatingCB.isSelected()) aams.remove(AreaScene.PLAYER_CLICK_MESSAGE);
        template.getActorMessages().setActivatingMessages(aams);

        String adm = "".equals(actorDeactivatingMessagesTF.getText()) ? null : actorDeactivatingMessagesTF.getText();
        List<String> adms = new ArrayList<String>();
        if (adm != null) adms = StringUtils.split(adm, ",");
        if (actorDeactivatingCB.isSelected()) adms.add(AreaScene.PLAYER_CLICK_MESSAGE);
        else if (actorDeactivatingCB.isSelected()) adms.remove(AreaScene.PLAYER_CLICK_MESSAGE);
        template.getActorMessages().setDeactivatingMessages(adms);

        String oam = "".equals(onActorActivationMessagesTF.getText()) ? null : onActorActivationMessagesTF.getText();
        List<String> oams = new ArrayList<String>();
        if (oam != null) oams = StringUtils.split(oam, ",");
        template.getActorMessages().setOnActivationMessages(oams);

        String odm = "".equals(onActorDeactivationMessagesTF.getText()) ? null : onActorDeactivationMessagesTF.getText();
        List<String> odms = new ArrayList<String>();
        if (odm != null) odms = StringUtils.split(odm, ",");
        template.getActorMessages().setOnDeactivationMessages(odms);

        String osm = "".equals(onActorSpawnMessagesTF.getText()) ? null : onActorSpawnMessagesTF.getText();
        List<String> osms = new ArrayList<String>();
        if (osm != null) osms = StringUtils.split(osm, ",");
        template.getActorMessages().setOnSpawnMessages(osms);

        String ocm = "".equals(onActorCriticalMessagesTF.getText()) ? null : onActorCriticalMessagesTF.getText();
        List<String> ocms = new ArrayList<String>();
        if (ocm != null) ocms = StringUtils.split(ocm, ",");
        template.getActorMessages().setOnCriticalMessages(ocms);

        String oim = "".equals(onActorDieMessagesTF.getText()) ? null : onActorDieMessagesTF.getText();
        List<String> oims = new ArrayList<String>();
        if (oim != null) oims = StringUtils.split(oim, ",");
        template.getActorMessages().setOnDieMessages(oims);

        template.setBoss(bossCB.isSelected());

        template.setActiveByDefault(defaultActiveCB.isSelected());
    }
}

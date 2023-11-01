package com.oblong.af.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static Messages load(DataInputStream dis) throws IOException {
        List<String> activatingMessages = readStringList(dis);
        List<String> deactivatingMessages = readStringList(dis);
        List<String> enablingMessages = readStringList(dis);
        List<String> disablingMessages = readStringList(dis);
        List<String> activatedMessages = readStringList(dis);
        List<String> deactivatedMessages = readStringList(dis);
        return new Messages(activatingMessages, deactivatingMessages, enablingMessages, disablingMessages,
                activatedMessages, deactivatedMessages);
    }

    private static List<String> readStringList(DataInputStream dis) throws IOException {
        int numStrings = dis.readInt();
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < numStrings; i++) strings.add(dis.readUTF());
        return strings;
    }

    public static void save(Messages messages, DataOutputStream dos) throws IOException {
        writeStringList(messages.activatingMessages, dos);
        writeStringList(messages.deactivatingMessages, dos);
        writeStringList(messages.enablingMessages, dos);
        writeStringList(messages.disablingMessages, dos);
        writeStringList(messages.onActivationMessages, dos);
        writeStringList(messages.onDeactivationMessages, dos);
    }

    private static void writeStringList(List<String> strings, DataOutputStream dos) throws IOException {
        dos.writeInt(strings.size());
        for (String s: strings) dos.writeUTF(s);
    }

    private List<String> onActivationMessages, onDeactivationMessages, activatingMessages, deactivatingMessages, enablingMessages, disablingMessages;

    public Messages(){
        activatingMessages = new ArrayList<String>();
        deactivatingMessages = new ArrayList<String>();
        enablingMessages = new ArrayList<String>();
        disablingMessages = new ArrayList<String>();
        onActivationMessages = new ArrayList<String>();
        onDeactivationMessages = new ArrayList<String>();
    }

    public Messages(List<String> activatingMessages, List<String> deactivatingMessages,
                    List<String> enablingMessages, List<String> disablingMessages,
                    List<String> onActivationMessages, List<String> onDeactivationMessages){
        this.activatingMessages = activatingMessages;
        this.deactivatingMessages = deactivatingMessages;
        this.enablingMessages = enablingMessages;
        this.disablingMessages = disablingMessages;
        this.onActivationMessages = onActivationMessages;
        this.onDeactivationMessages = onDeactivationMessages;
    }

    public List<String> getActivatingMessages(){ return activatingMessages; }
    public void setActivatingMessages(List<String> activatingMessages){ this.activatingMessages = activatingMessages; }

    public List<String> getDeactivatingMessages(){ return deactivatingMessages; }
    public void setDeactivatingMessages(List<String> deactivatingMessages){ this.deactivatingMessages = deactivatingMessages; }

    public List<String> getEnablingMessages(){ return enablingMessages; }
    public void setEnablingMessages(List<String> enablingMessages){ this.enablingMessages = enablingMessages; }

    public List<String> getDisablingMessages(){ return disablingMessages; }
    public void setDisablingMessages(List<String> disablingMessages){ this.disablingMessages = disablingMessages; }

    public List<String> getOnActivationMessages(){ return onActivationMessages; }
    public void setOnActivationMessages(List<String> onActivationMessages){ this.onActivationMessages = onActivationMessages; }

    public List<String> getOnDeactivationMessages(){ return onDeactivationMessages; }
    public void setOnDeactivationMessages(List<String> onDeactivationMessages){ this.onDeactivationMessages = onDeactivationMessages; }

}

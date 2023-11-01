package com.oblong.af.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActorMessages {

    public static ActorMessages load(DataInputStream dis) throws IOException {
        List<String> activatingMessages = readStringList(dis);
        List<String> deactivatingMessages = readStringList(dis);
        List<String> activatedMessages = readStringList(dis);
        List<String> deactivatedMessages = readStringList(dis);
        List<String> spawnMessages = readStringList(dis);
        List<String> dieMessages = readStringList(dis);
        List<String> criticalMessages = readStringList(dis);
        return new ActorMessages(activatingMessages, deactivatingMessages, activatedMessages, deactivatedMessages,
                spawnMessages, criticalMessages, dieMessages);
    }

    private static List<String> readStringList(DataInputStream dis) throws IOException {
        int numStrings = dis.readInt();
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < numStrings; i++) strings.add(dis.readUTF());
        return strings;
    }

    public static void save(ActorMessages messages, DataOutputStream dos) throws IOException {
        writeStringList(messages.activatingMessages, dos);
        writeStringList(messages.deactivatingMessages, dos);
        writeStringList(messages.onActivationMessages, dos);
        writeStringList(messages.onDeactivationMessages, dos);
        writeStringList(messages.onSpawnMessages, dos);
        writeStringList(messages.onDieMessages, dos);
        writeStringList(messages.onCriticalMessages, dos);
    }

    private static void writeStringList(List<String> strings, DataOutputStream dos) throws IOException {
        dos.writeInt(strings.size());
        for (String s: strings) dos.writeUTF(s);
    }

    private List<String> activatingMessages, deactivatingMessages;
    private List<String> onActivationMessages, onDeactivationMessages, onSpawnMessages, onDieMessages, onCriticalMessages;

    public ActorMessages(){
        activatingMessages = new ArrayList<String>();
        deactivatingMessages = new ArrayList<String>();
        onActivationMessages = new ArrayList<String>();
        onDeactivationMessages = new ArrayList<String>();
        onSpawnMessages = new ArrayList<String>();
        onCriticalMessages = new ArrayList<String>();
        onDieMessages = new ArrayList<String>();
    }

    public ActorMessages(List<String> activatingMessages, List<String> deactivatingMessages,
                         List<String> onActivatedMessages, List<String> onDeactivatedMessages,
                         List<String> onSpawnMessages, List<String> onCriticalMessages, List<String> onDieMessages){
        this.activatingMessages = activatingMessages;
        this.deactivatingMessages = deactivatingMessages;
        this.onActivationMessages = onActivatedMessages;
        this.onDeactivationMessages = onDeactivatedMessages;
        this.onSpawnMessages = onSpawnMessages;
        this.onCriticalMessages = onCriticalMessages;
        this.onDieMessages = onDieMessages;
    }

    public List<String> getActivatingMessages(){ return activatingMessages; }
    public void setActivatingMessages(List<String> activatingMessages){ this.activatingMessages = activatingMessages; }

    public List<String> getDeactivatingMessages(){ return deactivatingMessages; }
    public void setDeactivatingMessages(List<String> deactivatingMessages){ this.deactivatingMessages = deactivatingMessages; }

    public List<String> getOnActivationMessages(){ return onActivationMessages; }
    public void setOnActivationMessages(List<String> onActivationMessages){ this.onActivationMessages = onActivationMessages; }

    public List<String> getOnDeactivationMessages(){ return onDeactivationMessages; }
    public void setOnDeactivationMessages(List<String> onDeactivationMessages){ this.onDeactivationMessages = onDeactivationMessages; }

    public List<String> getOnSpawnMessages(){ return onSpawnMessages; }
    public void setOnSpawnMessages(List<String> onSpawnMessages){ this.onSpawnMessages = onSpawnMessages; }

    public List<String> getOnDieMessages(){ return onDieMessages; }
    public void setOnDieMessages(List<String> onDieMessages){ this.onDieMessages = onDieMessages; }

    public List<String> getOnCriticalMessages(){ return onCriticalMessages; }
    public void setOnCriticalMessages(List<String> onCriticalMessages){ this.onCriticalMessages = onCriticalMessages; }

}

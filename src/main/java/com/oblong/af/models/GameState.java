package com.oblong.af.models;

import com.oblong.af.sprite.enemy.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class GameState {

    public static final String SELECTED_PLAYER_KEY = "selectedPlayer";

	public static GameState load(DataInputStream dis) throws IOException {

        //player id, lives, hp, maxHp, speed, affinity points, abilities
        GameState gameState = new GameState(SpriteDefinitions.valueOf(dis.readUTF())); //player id
        gameState.setLives(dis.readInt());
        gameState.setHp(dis.readInt());
        gameState.setMaxHp(dis.readInt());
        gameState.setSpeed(dis.readInt());
        gameState.setSpareAffinity(dis.readInt());
        gameState.setEarthAffinity(dis.readInt());
        gameState.setWaterAffinity(dis.readInt());
        gameState.setAirAffinity(dis.readInt());
        gameState.setFireAffinity(dis.readInt());
        gameState.setSpiritAffinity(dis.readInt());
        gameState.setEqAbId1(dis.readUTF());
        gameState.setEqAbId2(dis.readUTF());
        gameState.setEqAbId3(dis.readUTF());
        gameState.setEqAbId4(dis.readUTF());
        gameState.setSpAbId1(dis.readUTF());
        gameState.setSpAbId2(dis.readUTF());
        gameState.setSpAbId3(dis.readUTF());
        gameState.setSpAbId4(dis.readUTF());

        //current area group
        gameState.setCurrentAreaGroupId(dis.readUTF());

        //state variables
        int numVars = dis.readInt();
        for (int i = 0; i < numVars; i++) gameState.setVariable(dis.readUTF(), dis.readUTF());

        return gameState;
	}

	public static void save(GameState gameState, DataOutputStream dos) throws IOException {
        //player id, lives, hp, maxHp, speed, affinity points, abilities
        dos.writeUTF(gameState.getPlayerDefinition().name());
        dos.writeInt(gameState.getLives());
        dos.writeInt(gameState.getHp());
        dos.writeInt(gameState.getMaxHp());
        dos.writeInt(gameState.getSpeed());
        dos.writeInt(gameState.getSpareAffinity());
        dos.writeInt(gameState.getEarthAffinity());
        dos.writeInt(gameState.getWaterAffinity());
        dos.writeInt(gameState.getAirAffinity());
        dos.writeInt(gameState.getFireAffinity());
        dos.writeInt(gameState.getSpiritAffinity());
        dos.writeUTF(gameState.getEqAbId1());
        dos.writeUTF(gameState.getEqAbId2());
        dos.writeUTF(gameState.getEqAbId3());
        dos.writeUTF(gameState.getEqAbId4());
        dos.writeUTF(gameState.getSpAbId1());
        dos.writeUTF(gameState.getSpAbId2());
        dos.writeUTF(gameState.getSpAbId3());
        dos.writeUTF(gameState.getSpAbId4());

        //current area group
        dos.writeUTF(gameState.getCurrentAreaGroupId());

        //state variables
        dos.writeInt(gameState.scriptingVariables.size());
        for (String key: gameState.scriptingVariables.keySet()){
            String value = gameState.getVariable(key);
            dos.writeUTF(key);
            dos.writeUTF(value);
        }
    }

    private String currentAreaGroupId;
    private Hashtable<String, String> scriptingVariables;
    private SpriteDefinitions playerDefinition;
    private int lives, hp, maxHp, speed, spareAffinity, earthAffinity, waterAffinity, airAffinity, fireAffinity, spiritAffinity;
    private String eqAbId1, eqAbId2, eqAbId3, eqAbId4, spAbId1, spAbId2, spAbId3, spAbId4;

    public GameState(SpriteDefinitions playerDefinition){
        setPlayerDefinition(playerDefinition);
		currentAreaGroupId = "";
		scriptingVariables = new Hashtable<String, String>();
        lives = 2;
        hp = playerDefinition.getMaxHp();
        maxHp = playerDefinition.getMaxHp();
        speed = playerDefinition.getSpeed();
        spareAffinity = 0;
        earthAffinity = playerDefinition.getInitialEarthAffinity();
        waterAffinity = playerDefinition.getInitialWaterAffinity();
        airAffinity = playerDefinition.getInitialAirAffinity();
        fireAffinity = playerDefinition.getInitialFireAffinity();
        spiritAffinity = playerDefinition.getInitialSpiritAffinity();
        eqAbId1 = playerDefinition.getInitialAbility1().name();
        eqAbId2 = playerDefinition.getInitialAbility2().name();
        eqAbId3 = playerDefinition.getInitialAbility3().name();
        eqAbId4 = playerDefinition.getInitialAutoAbility().name();
        spAbId1 = Ability.None.name();
        spAbId2 = Ability.None.name();
        spAbId3 = Ability.None.name();
        spAbId4 = Ability.None.name();

        setVariable(SELECTED_PLAYER_KEY, playerDefinition.name());
        playerDefinition.getInitialAbility1().setFound(this);
        playerDefinition.getInitialAbility2().setFound(this);
        playerDefinition.getInitialAbility3().setFound(this);
        playerDefinition.getInitialAutoAbility().setFound(this);

//setVariable(GigapedeSegment.DEFEATED_STATE_VARIABLE, "true");
//setVariable(BigBeholder.DEFEATED_STATE_VARIABLE, "true");
//setVariable(Wyvern.DEFEATED_STATE_VARIABLE, "true");
//setVariable(Vampire.DEFEATED_STATE_VARIABLE, "true");
//setVariable(SpiderQueen.DEFEATED_STATE_VARIABLE, "true");
//setVariable(StoneKing.DEFEATED_STATE_VARIABLE, "true");
    }

    public boolean isAllBossesDead(){
        boolean bigBeholderDead = ("true".equals(getVariable(BigBeholder.DEFEATED_STATE_VARIABLE)));
        boolean stormWyvernDead = ("true".equals(getVariable(Wyvern.DEFEATED_STATE_VARIABLE)));
        boolean vampireDead = ("true".equals(getVariable(Vampire.DEFEATED_STATE_VARIABLE)));
        boolean gigapedeDead = ("true".equals(getVariable(GigapedeSegment.DEFEATED_STATE_VARIABLE)));
        boolean spiderQueenDead = ("true".equals(getVariable(SpiderQueen.DEFEATED_STATE_VARIABLE)));
        return bigBeholderDead && stormWyvernDead && vampireDead && gigapedeDead && spiderQueenDead;
    }

    public boolean isStoneKingDead(){ return "true".equals(getVariable(StoneKing.DEFEATED_STATE_VARIABLE)); }

    public boolean isNemesisDead(){ return "true".equals(getVariable(Nemesis.DEFEATED_STATE_VARIABLE)); }

    public boolean isAllTownspeopleRescued(){
        boolean allTownspeopleRescued = true;
        for (NPCs npc: NPCs.values())
            allTownspeopleRescued &= (NPCs.isRescued(npc.name(), this) || NPCs.isPlayer(npc.name(), this)) || npc == NPCs.Dog;
        return allTownspeopleRescued;
    }

	public String getCurrentAreaGroupId(){ return currentAreaGroupId; }
	public void setCurrentAreaGroupId(String currentLevelId){ this.currentAreaGroupId = currentLevelId; }

	public String getVariable(String name){ return scriptingVariables.get(name); }
	public void setVariable(String name, String value){ scriptingVariables.put(name, value); }

    public SpriteDefinitions getPlayerDefinition(){ return playerDefinition; }
    public void setPlayerDefinition(SpriteDefinitions playerDefinition){ this.playerDefinition = playerDefinition; }

    public int getLives(){ return lives; }
    public void setLives(int lives){ this.lives = lives; }

    public int getHp(){ return hp; }
    public void setHp(int hp){ this.hp = hp; }

    public int getMaxHp(){ return maxHp; }
    public void setMaxHp(int maxHp){ this.maxHp = maxHp; }

    public int getSpeed(){ return speed; }
    public void setSpeed(int speed){ this.speed = speed; }

    public int getSpareAffinity(){ return spareAffinity; }
    public void setSpareAffinity(int spareAffinity){ this.spareAffinity = spareAffinity; }

    public int getEarthAffinity(){ return earthAffinity; }
    public void setEarthAffinity(int earthAffinity){ this.earthAffinity = earthAffinity; }

    public int getWaterAffinity(){ return waterAffinity; }
    public void setWaterAffinity(int waterAffinity){ this.waterAffinity = waterAffinity; }

    public int getAirAffinity(){ return airAffinity; }
    public void setAirAffinity(int airAffinity){ this.airAffinity = airAffinity; }

    public int getFireAffinity(){ return fireAffinity; }
    public void setFireAffinity(int fireAffinity){ this.fireAffinity = fireAffinity; }

    public int getSpiritAffinity(){ return spiritAffinity; }
    public void setSpiritAffinity(int spiritAffinity){ this.spiritAffinity = spiritAffinity; }

    public String getEqAbId1(){ return eqAbId1; }
    public void setEqAbId1(String eqAbId1){ this.eqAbId1 = eqAbId1; }

    public String getEqAbId2(){ return eqAbId2; }
    public void setEqAbId2(String eqAbId2){ this.eqAbId2 = eqAbId2; }

    public String getEqAbId3(){ return eqAbId3; }
    public void setEqAbId3(String eqAbId3){ this.eqAbId3 = eqAbId3; }

    public String getEqAbId4(){ return eqAbId4; }
    public void setEqAbId4(String eqAbId4){ this.eqAbId4 = eqAbId4; }

    public String getSpAbId1(){ return spAbId1; }
    public void setSpAbId1(String spAbId1){ this.spAbId1 = spAbId1; }

    public String getSpAbId2(){ return spAbId2; }
    public void setSpAbId2(String spAbId2){ this.spAbId2 = spAbId2; }

    public String getSpAbId3(){ return spAbId3; }
    public void setSpAbId3(String spAbId3){ this.spAbId3 = spAbId3; }

    public String getSpAbId4(){ return spAbId4; }
    public void setSpAbId4(String spAbId4){ this.spAbId4 = spAbId4; }

}

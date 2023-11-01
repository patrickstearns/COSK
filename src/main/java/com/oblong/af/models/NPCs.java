package com.oblong.af.models;

import com.oblong.af.level.AreaScene;
import com.oblong.af.models.conversation.*;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.npcs.PetrifiedNPC;
import com.oblong.af.sprite.npcs.RescuableNPC;
import com.oblong.af.sprite.npcs.TalkableNPC;

public enum NPCs {

    Adventurer(ConvoType.GiveThenGive, 2, 0, 0, "What's happening?  What's going on?",
            "Man, that was crazy.  Thanks for getting me out. Oh, hey, I found this in the desert.",
            "Hey, I kept looking around and found one of these.",
            Powerups.AbilityGem, Ability.Sand, Powerups.AbilityGem, Ability.Gust,
            "That's all I've got for ya."),
    Knight(ConvoType.GiveThenGive, 2, 1, 0, "Ho, I've been rescued!  Thank thee!",
            "My thanks for the timely rescue. Accept this with my gratitude.",
            "I can always lend you a sturdy sword.",
            Powerups.AbilityGem, Ability.Stoneskin, Powerups.AbilityGem, Ability.Sword,
            "I've no more to give you."),
    TrainConductor(ConvoType.GiveThenTalk, 2, 2, 0, "Uh-oh, now where am I going?",
            "Thanks for getting me back on track.  Hey, we use these on the rails - maybe it can help you.",
            "Thanks again.",
            Powerups.AbilityGem, Ability.Windproof, null, null, null),
    Raincoat(ConvoType.GiveThenTalk, 2, 3, 0, "Free at last!",
            "Thanks for the help!  I managed to snag this for ya.",
            "Sorry, but that was all I could snag.",
            Powerups.AffinityGem, null, null, null, null),
    Robes(ConvoType.GiveThenGive, 2, 4, 0, "Oh, bless you!",
            "Praises for saving me!  Please, accept this with my thanks!",
            "Let me help cleanse you of impurity.",
            Powerups.AbilityGem, Ability.Lifespark, Powerups.AbilityGem, Ability.Cleanse,
            "Go now in peace."),
    Businessman(ConvoType.GiveThenTalk, 2, 0, 1, "Great, I can finally get back to work!",
            "Hey, I repay my debts.  Take this from me.",
            "So if I move those assets to the other accounts the tax will be two percent lower...",
            Powerups.HpMaxUpPotion, null, null, null, null),
    Impresario(ConvoType.Talk, 2, 1, 1, "And my coda becomes another verse!",
            "Vampires can only be damaged in the light.",
            null, null, null, null, null, null),
    Shopkeeper(ConvoType.GiveThenTalk, 2, 2, 1, "My word!  Where am I going now?",
            "Heavens but that was terrifying!  Thank you so much for the rescue.  This is the best I can do as thanks.",
            "Thanks again.",
            Powerups.AffinityGem, null, null, null, null),
    Scholar(ConvoType.GiveThenTalk, 2, 3, 1, "Ah, and now the cord is pulling me back to my body...quite disconcerting...",
            "You've given me so much to document!  Oh, but I should do something for you...here, please, take this.",
            "Hmm, \"Astral Sundering\" or \"Soul Separation...\"  Which to call it?",
            Powerups.HpMaxUpPotion, null, null, null, null),
    Governor(ConvoType.GiveThenTalk, 2, 4, 1, "Release me!  I'm the Governor!  You can't do this to me!",
            "Ah, you, good.  Please, accept this on behalf of the town.",
            "Good day.",
            Powerups.AbilityGem, Ability.Stonefist, null, null, null),
    BlueOldMan(ConvoType.GiveThenTalk, 2, 0, 2, "I'm a-comin' for ya, Glenda!",
            "Oh, I thought I was dead - twice! Still kickin', though. Maybe this could help ya...I use 'em on my underpants...",
            "Don't tell nobody what I use them for, ya hear?",
            Powerups.AbilityGem, Ability.Waterproof, null, null, null),
    BlueMan(ConvoType.GiveThenTalk, 2, 1, 2, "What's the meaning of this?  Release me immediately!",
            "Ah, it's you who I've to thank for my release.  Here, with my thanks.",
            "Are you begging for more?  I'm afraid that was all I have.",
            Powerups.HpMaxUpPotion, null, null, null, null),
    Pilot(ConvoType.GiveThenTalk, 2, 2, 2, "Oh, yeah, back to the skies!",
            "Whoah, talk about a trip, bro.  Thanks for gettin' me out, and, hey, I snagged this for ya.",
            "I'll let ya know if I find anything else for ya, bro.",
            Powerups.AffinityGem, null, null, null, null),
    BlueScholar(ConvoType.Talk, 2, 3, 2, "Ah, some good karma at last!",
            "And here I thought that mirror I'd broken had screwed me forever!",
            null, null, null, null, null, null),
    PinkKid(ConvoType.Talk, 2, 4, 2, "Sock them monsters good for me!",
            "Git them monsters, mister!  Show 'em what for!", null, null, null, null, null, null),
    GreenOldMan(ConvoType.GiveThenTalk, 2, 0, 3, "Whoah, here I go!",
            "Oh, sonny, thanks for getting me out of that trap.  It was like back in the mines...we used to use these.  Have one.",
            "Hoo, flashbacks...eek.",
            Powerups.AbilityGem, Ability.Earthproof, null, null, null),
    Engineer(ConvoType.GiveThenTalk, 2, 1, 3, "Oh, yeah, free at last!",
            "I thought I was hosed there.  Thanks for the rescue!  I managed to snag this for you.",
            "Thanks again.",
            Powerups.AffinityGem, null, null, null, null),
    Beggar(ConvoType.GiveThenTalk, 2, 2, 3, "There goes my free house...",
            "Ack!  Thppt...hmm...here.",
            "Pizza rolls!  Pizza rolls!  Hollyhock and pizza rolls!",
            Powerups.HpMaxUpPotion, null, null, null, null),
    Blacksmith(ConvoType.GiveThenGive, 2, 3, 3, "Fuck yeah, bro!  Kick some ass for me!",
            "That was some crazy shit, man, thanks for pulling my ass out of the flames.  Got ya this t'say thanks.",
            "Hey, bro, you can have one of these anytime.",
            Powerups.AbilityGem, Ability.Flamethrower, Powerups.AbilityGem, Ability.RingOfFire,
            "Ah, I'm tapped out, bud."),
    BlueKid(ConvoType.Talk, 2, 4, 3, "Man, being trapped like that sure sucked.",
            "Thanks for the save, mister.", null, null, null, null, null, null),
    WaterCarrier(ConvoType.GiveThenGive, 2, 0, 4, "I had the chills and now the vapors...",
            "Thank thee for the rescue, good sir.  I found this by the well.  Perhaps it can help you.",
            "Ah, good sir!  I've found plenty of these.  Please, have one.",
            Powerups.AbilityGem, Ability.FreezingBolt, Powerups.AbilityGem, Ability.Flush,
            "I'm afraid that's all I can give you."),
    FlowerGirl(ConvoType.GiveThenTalk, 2, 1, 4, "Off like a dandelion seed...",
            "Oh!  I was plucked but am reattached.  Please, good gentleman, may this help you.",
            "I've give you all I can, for now.",
            Powerups.AbilityGem, Ability.Spiritproof, null, null, null),
    Mechanic(ConvoType.GiveThenTalk, 2, 2, 4, "Pull the cogs out of their infernal machine!",
            "Man, my gears were gritty back there...thanks for the grease!",
            "Thanks again.",
            Powerups.AffinityGem, null, null, null, null),
    OldFarmLady(ConvoType.GiveThenGive, 2, 3, 4, "Oh, good, I've still got rabides to milk...",
            "Sure appreciate the rescue, sonny.  Let me share something with you I've had for awhile...",
            "Anytime you need to put a spark into something, hon.",
            Powerups.AbilityGem, Ability.ShockFingers, Powerups.AbilityGem, Ability.LightningCoil,
            "Oh, how nice you came by, sonny!"),
    BlueOldLady(ConvoType.GiveThenTalk, 2, 4, 4, "This is NOT how a lady of my standing should be treated!",
            "Hmph.  I'm still peeved about how those hooligans treated me, but I suppose you've earned yourself a token.",
            "Did you need something, my dear?",
            Powerups.HpMaxUpPotion, null, null, null, null),
    Policewoman(ConvoType.GiveThenTalk, 2, 0, 5, "Oooo, this is, like, SOOOO many violations!",
            "I saw what you did back there, but...I guess I can let it slide.  And here, take this in case you need to keep from getting burned.",
            "I've got my eyes on you.",
            Powerups.AbilityGem, Ability.Fireproof, null, null, null),
    BlueChick(ConvoType.Talk, 2, 1, 5, "Oh, what a relief!",
            "Thanks, big boy!  Here's a hint: some walls are breakable, but only with physical damage.",
            null, null, null, null, null, null),
    PinkChick(ConvoType.GiveThenTalk, 2, 2, 5, "Oooh, those dirty monsters!  I'll kick their bu...",
            "Sorry I couldn't help you back there, but I did manage to find this for you.",
            "Gonna kick those monsters' collective butts if I see 'em again...",
            Powerups.AbilityGem, Ability.FlameShield, null, null, null),
    HealerChick(ConvoType.GiveThenTalk, 2, 3, 5, "I hope no one's been hurt...",
            "Here, this will come in handy if you get hurt.",
            "I wish I could do more.",
            Powerups.AbilityGem, Ability.CureWounds, null, null, null),
    PinkBowGirl(ConvoType.GiveThenTalk, 2, 4, 5, "Wheee!  This is fun!",
            "Wow, that was super fun, mister!  And here, look at this shiny thing I found.  Here, you keep it!",
            "Tra-la-la...",
            Powerups.AffinityGem, null, null, null, null),
    Dog(ConvoType.Dog, 2, 4, 7, null, null, null, null, null, null, null, null),
    Brent(ConvoType.GiveThenTalk, 2, 0, 9, "Woo-hoo!",
            "Sweet!  And here, take this in case you need to axe anybody anything.",
            "Hey, and be sure you've got a fire spell before taking on any vampires!", Powerups.AbilityGem, Ability.Axe,
            null, null, null),
    Sean(ConvoType.GiveThenTalk, 2, 2, 9, "Finally!",
            "Thanks!  If you want them, you can have these.",
            "Hey, you know those spider spitters on the glacier?  You can freeze them shut!",
            Powerups.AbilityGem, Ability.ThrowingKnives, null, null, null),
    Dragon(ConvoType.GiveThenTalk, 2, 0, 10, "Nice!",
            "Thanks, man!  Here, in case you need to heat things up.",
            "Hey, I saw the shopkeeper in the village's Eastern Fields, but you'll need an ice spell to rescue him.",
            Powerups.AbilityGem, Ability.Flamethrower, null, null, null),
    Allen(ConvoType.GiveThenTalk, 2, 3, 9, "Commencing extraction!",
            "Thanks for the evac.  Here - blow some shit up.",
            "Make sure you look everywhere for all the townspeople - never leave one behind!",
            Powerups.AbilityGem, Ability.Bombs, null, null, null),
    Matt(ConvoType.GiveThenTalk, 2, 1, 9, "Whew!",
            "Thanks man!  Here, bonk 'em good!",
            "And watch out for those electrified seahorses when they get near water...zap!",
            Powerups.AbilityGem, Ability.Staff, null, null, null),
    Marshall(ConvoType.GiveThenTalk, 2, 2, 10, "Oh, boy...",
            "Thanks man!  Take this and nail 'em!",
            "Be careful killing a beholder's orbiters; they come back even nastier!",
            Powerups.AbilityGem, Ability.Pistol, null, null, null),
    Adam(ConvoType.GiveThenTalk, 2, 1, 10, "Whew!",
            "Thanks for the rescue.  You can have these if you want.",
            "Didja know some ice blocks can be melted with fire spells?",
            Powerups.AbilityGem, Ability.ThrowingAxes, null, null, null),
    Patrick(ConvoType.GiveThenTalk, 2, 3, 10, "Woo-hoo!",
            "Uh, right, I'm supposed to drop an item...uh...okay, here.",
            "Yeah, I know there's still some bugs in it...I swear, I'm working on them...mumble mumble...",
            Powerups.AbilityGem, Ability.Sword, null, null, null),
    ;

    public static final String GAVE_FIRST_ITEM_KEY = "_gave_first_item";
    public static final String GAVE_FIRST_ITEM_VALUE = "given";
    public static final String RESCUED_KEY = "_rescued";
    public static final String RESCUED_VALUE = "rescued";

    public enum ConvoType {
        GiveThenGive,
        GiveThenTalk,
        Talk,
        Load,
        Save,
        Dog,
    }

    private ConvoType convoType;
    private int speed, xPic, yPic;
    private String rescuedMessage, firstAfterRescuedMessage, laterAfterRescuedMessage;
    private Powerups firstAfterRescuedPowerup, laterAfterRescuedPowerup;
    private Ability firstAfterRescuedAbility, laterAfterRescuedAbility;
    private String nothingToGiveMessage;

    private NPCs(ConvoType convoType, int speed, int xPic, int yPic, String rescuedMessage,
                 String firstAfterRescuedMessage, String laterAfterRescuedMessage,
                 Powerups firstAfterRescuedPowerup, Ability firstAfterRescuedAbility,
                 Powerups laterAfterRescuedPowerup, Ability laterAfterRescuedAbility,
                 String nothingToGiveMessage){
        this.convoType = convoType;
        this.speed = speed;
        this.xPic = xPic;
        this.yPic = yPic;
        this.rescuedMessage = rescuedMessage;
        this.firstAfterRescuedMessage = firstAfterRescuedMessage;
        this.laterAfterRescuedMessage = laterAfterRescuedMessage;
        this.firstAfterRescuedPowerup = firstAfterRescuedPowerup;
        this.laterAfterRescuedPowerup = laterAfterRescuedPowerup;
        this.firstAfterRescuedAbility = firstAfterRescuedAbility;
        this.laterAfterRescuedAbility = laterAfterRescuedAbility;
        this.nothingToGiveMessage = nothingToGiveMessage;
    }

    public ConvoType getConvoType(){ return convoType; }
    public int getSpeed(){ return speed; }
    public int getXPic(){ return 6*xPic; }
    public int getYPic(){ return 3*yPic; }
    public String getRescuedMessage(){ return rescuedMessage; }
    public String getFirstAfterRescuedMessage(){ return firstAfterRescuedMessage; }
    public String getLaterAfterRescuedMessage(){ return laterAfterRescuedMessage; }
    public Powerups getFirstAfterRescuedPowerup(){ return firstAfterRescuedPowerup; }
    public Powerups getLaterAfterRescuedPowerup(){ return laterAfterRescuedPowerup; }
    public Ability getFirstAfterRescuedAbility(){ return firstAfterRescuedAbility; }
    public Ability getLaterAfterRescuedAbility(){ return laterAfterRescuedAbility; }
    public String getNothingToGiveMessage(){ return nothingToGiveMessage; }

    public static boolean isPlayer(String name, GameState gameState){ return name.equals(gameState.getVariable(GameState.SELECTED_PLAYER_KEY)); }
    public static boolean isRescued(String name, GameState gameState){ return name.equals(Dog.name()) || RESCUED_VALUE.equals(gameState.getVariable(name + RESCUED_KEY)); }
    public static void setRescued(String name, GameState gameState){ gameState.setVariable(name+RESCUED_KEY, RESCUED_VALUE); }
    public static boolean isGaveFirst(String name, GameState gameState){ return GAVE_FIRST_ITEM_VALUE.equals(gameState.getVariable(name+GAVE_FIRST_ITEM_KEY)); }
    public static void setGaveFirst(String name, GameState gameState){ gameState.setVariable(name+GAVE_FIRST_ITEM_KEY, GAVE_FIRST_ITEM_VALUE); }

    public RescuableNPC createRescuableNPC(AreaScene scene){
        if (scene != null && isPlayer(name(), scene.getGameState())) return null; //is the player so return null
        if (scene != null && isRescued(name(), scene.getGameState())) return null; //already rescued so return null
        return new RescuableNPC(name(), scene, getXPic(), getYPic(), createConversationNode(scene, true));
    }

    public Prop createTalkableNPC(AreaScene scene){
        if (scene != null && isPlayer(name(), scene.getGameState())) return null; //is the player so return null
        if (this == Dog) return new com.oblong.af.sprite.npcs.Dog(scene, createConversationNode(scene, false));
        if (scene != null && isRescued(name(), scene.getGameState())) return new TalkableNPC(name(), scene, getXPic(), getYPic(), createConversationNode(scene, false));
        else return new PetrifiedNPC(name(), scene, getXPic(), getYPic(), createConversationNode(scene, false));
    }

    public ConversationNode createConversationNode(AreaScene scene, boolean rescuable){
        if (scene == null) return null;
        if (rescuable){
            return new TalkNode(getRescuedMessage());
        }
        else if (getConvoType() == ConvoType.Dog){
            return new DogNode();
        }
        else if (isRescued(name(), scene.getGameState())){
            switch (getConvoType()) {
                case GiveThenGive:
                    if (isGaveFirst(name(), scene.getGameState()))
                        return new PowerupTalkNode(getLaterAfterRescuedMessage(), getLaterAfterRescuedPowerup(), getLaterAfterRescuedAbility());
                    else return new PowerupTalkNode(getFirstAfterRescuedMessage(), getFirstAfterRescuedPowerup(), getFirstAfterRescuedAbility());
                case GiveThenTalk:
                    if (isGaveFirst(name(), scene.getGameState()))
                        return new TalkNode(getLaterAfterRescuedMessage());
                    else return new PowerupTalkNode(getFirstAfterRescuedMessage(), getFirstAfterRescuedPowerup(), getFirstAfterRescuedAbility());
                default:
                case Talk:
                    return new TalkNode(getFirstAfterRescuedMessage());
                case Load:
                    return new OpenLoadFileMenuTalkNode();
                case Save:
                    return new OpenSaveFileMenuTalkNode();
            }
        }
        else return null;
    }
}

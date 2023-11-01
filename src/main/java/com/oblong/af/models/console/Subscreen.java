package com.oblong.af.models.console;

import com.mojang.sonar.FixedSoundSource;
import com.oblong.af.GameComponent;
import com.oblong.af.models.Ability;
import com.oblong.af.models.Affinity;
import com.oblong.af.models.NPCs;
import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.enemy.*;
import com.oblong.af.util.Art;
import com.oblong.af.util.GameConstants;
import com.oblong.af.util.ImageUtils;
import com.oblong.af.util.TextImageCreator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class Subscreen extends ConsoleMenu<Subscreen.SubscreenItem> {

    private static final Color transparent = new Color(0f, 0f, 0f, 0f);
    public static final int MAX_OPENING_COUNTER = 10;
    private static final int ABILITY_DIM = 24, GAP = 2;
    private static final int AFFINITY_WIDTH = 16, AFFINITY_HEIGHT = ABILITY_DIM*2+GAP;

    public class SubscreenItem {
        public Rectangle bounds;
        public Actor.AbilitySlot abilitySlot;
        public Affinity affinity;
        public boolean spareAbilitySlot;

        public SubscreenItem(Rectangle bounds, Actor.AbilitySlot abilitySlot, Affinity affinity, boolean spareAbilitySlot){
            this.bounds = bounds;
            this.abilitySlot = abilitySlot;
            this.affinity = affinity;
            this.spareAbilitySlot = spareAbilitySlot;
        }

        public void render(Graphics2D g, boolean enabled, boolean focused){
            if (affinity != null) drawAffinitySlot(g, affinity, bounds, enabled, focused);
            else drawAbilitySlot(g, abilitySlot, bounds, enabled, focused);
        }

        private void drawAffinitySlot(Graphics2D g, Affinity affinity, Rectangle bounds, boolean enabled, boolean focused){
            //draw meter
            int affinityPoints = 0;
            switch (affinity) {
                case Fire: affinityPoints = getConsole().getScene().getGameState().getFireAffinity(); break;
                case Water: affinityPoints = getConsole().getScene().getGameState().getWaterAffinity(); break;
                case Air: affinityPoints = getConsole().getScene().getGameState().getAirAffinity(); break;
                case Earth: affinityPoints = getConsole().getScene().getGameState().getEarthAffinity(); break;
                case Spirit: affinityPoints = getConsole().getScene().getGameState().getSpiritAffinity(); break;
            }

            int meterHeight = bounds.height-16;
            int filledMeterHeight = (int)(affinityPoints*(float)meterHeight/(float)Affinity.MAX_VALUE);
            Point blc = new Point(bounds.x, bounds.y+bounds.height-16);
            drawBigMeter(g, blc, 14, affinity.getColor(), affinity.getHighlightColor(), filledMeterHeight, meterHeight, affinity.getIcon(),
                    getConsole().getScene().getGameState().getSpareAffinity() > 0 && focused);
        }

        private void drawBigMeter(Graphics2D g, Point bottomLeftCorner, int width, Color c, Color h, int value, int maxValue, Image icon, boolean focused){
            int maxHeight = 180;
            if (maxValue > maxHeight){
                value = (int)(value*((float)maxHeight/(float)maxValue));
                maxValue = (int)(maxValue*((float)maxHeight/(float)maxValue));
            }

            double barLength = maxValue;
            double filledLength = barLength * ((double)value/(double)maxValue);
            Rectangle barBounds = new Rectangle(bottomLeftCorner.x, (int)(bottomLeftCorner.y-barLength), width, (int)barLength);
            Rectangle filledBounds = new Rectangle(bottomLeftCorner.x, (int)(bottomLeftCorner.y-filledLength), width, (int)filledLength);

            //bar section
            //bar background
            g.setColor(Color.BLACK);
            g.fillRoundRect(barBounds.x, barBounds.y, barBounds.width, barBounds.height, 4, 4);
            //bar filled part
            g.setColor(c);
            if (focused) g.setColor(h);
            g.fillRoundRect(filledBounds.x + 1, filledBounds.y + 1, filledBounds.width - 2, filledBounds.height - 2, 4, 4);
            //bar highlight
            if (value != maxValue){
                g.setColor(h);
                g.fillOval(filledBounds.x+1, filledBounds.y+1, filledBounds.width-3, 3);
            }
            //bar lowlight
            g.setColor(c.darker().darker());
            g.drawLine(filledBounds.x+filledBounds.width-2, filledBounds.y+2, filledBounds.x+filledBounds.width-2, filledBounds.y+filledBounds.height-3);
            g.drawLine(filledBounds.x+2, filledBounds.y+filledBounds.height-2, filledBounds.x+filledBounds.width-3, filledBounds.y+filledBounds.height-2);
            //glass glint
            g.setPaint(new GradientPaint(barBounds.x + 2, barBounds.y, new Color(0f, 0f, 0f, 0f), barBounds.x + 2, barBounds.y + 5, Color.WHITE));
            g.drawLine(barBounds.x+2, barBounds.y+2, barBounds.x+2, barBounds.y+barBounds.height/3-3);
            g.setPaint(new GradientPaint(barBounds.x+2, barBounds.y+5, Color.WHITE, barBounds.x+2, barBounds.y+barBounds.height-8, new Color(0f, 0f, 0f, 0f)));
            g.drawLine(barBounds.x+2, barBounds.y+5, barBounds.x+2, barBounds.y+barBounds.height-8);
            //glass ticks
            float inc = (float)maxHeight/20f;
            g.setColor(new Color(0f, 0f, 0f, 0.25f));
            for (int y = barBounds.y+barBounds.height; y >= barBounds.y; y -= inc)
                g.drawLine(barBounds.x+barBounds.width/2-1, y, barBounds.x+barBounds.width/2, y);
            inc = (float)maxHeight/5f;
            g.setColor(new Color(0f, 0f, 0f, 0.5f));
            for (int y = barBounds.y+barBounds.height; y >= barBounds.y; y -= inc)
                g.drawLine(barBounds.x+barBounds.width/2-1, y, barBounds.x+barBounds.width/2, y);
            //icon section
            Rectangle iconBounds = new Rectangle(barBounds.x+barBounds.width/2-10, barBounds.y+barBounds.height-1, 19, 19);
            //paint background
            g.setPaint(new GradientPaint(iconBounds.x, iconBounds.y, c, iconBounds.x, iconBounds.y+iconBounds.height, transparent));
            g.fillRoundRect(iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 15, 15);
            //paint image
            g.drawImage(icon, iconBounds.x+iconBounds.width/2-icon.getWidth(null)/2+1, iconBounds.y+iconBounds.height/2-icon.getHeight(null)/2+1, null);
            //paint border
            g.setColor(Color.BLACK);
            g.drawRoundRect(iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 10, 10);
            g.setPaint(new GradientPaint(iconBounds.x+1, iconBounds.y+1, Color.WHITE, iconBounds.x+1+iconBounds.width-2, iconBounds.y+1+iconBounds.height-2, Color.BLACK));
            if (focused)
                g.setPaint(new GradientPaint(iconBounds.x + 1, iconBounds.y + 1, Color.YELLOW, iconBounds.x + 1 + iconBounds.width - 2, iconBounds.y + 1 + iconBounds.height - 2, Color.ORANGE));
            g.drawRoundRect(iconBounds.x+1, iconBounds.y+1, iconBounds.width-2, iconBounds.height-2, 9, 9);
        }

        private void drawAbilitySlot(Graphics2D g, Actor.AbilitySlot slot, Rectangle bounds, boolean enabled, boolean focused){
            //figure out border colors
            Color outer, inner;
            int outlineTick = tickCounter;
            if (focused){
                if (slot.type == Actor.AbilitySlot.SlotType.Trash){
                    outer = new Color(1f, 0.1f, 0f);
                    inner = Color.RED;
                }
                else{
                    outer = Color.CYAN;
                    inner = Color.WHITE;
                }
            }
            else if (enabled){
                outer = Color.GRAY;
                inner = Color.LIGHT_GRAY;
                outlineTick = 0;
            }
            else{
                outer = Color.BLACK;
                inner = Color.DARK_GRAY;
                outlineTick = 0;
            }

            inner = ImageUtils.calculateCycleColor(outlineTick, new Color[]{inner, new Color(inner.getRed(), inner.getGreen(), inner.getBlue(), 128) }, 20);
            outer = ImageUtils.calculateCycleColor(outlineTick, new Color[]{outer, new Color(outer.getRed(), outer.getGreen(), outer.getBlue(), 128) }, 20);

            //figure out ability icon image and background image
            Image icon = null, bg = Ability.TriggerType.None.getSocketImage();
            if (slot == eqAbA.abilitySlot) bg = Art.consoleIcons32x32[6][0];
            if (slot != null && (slot.ability != Ability.None || (heldAbility != Ability.None && focused))){
                icon = slot.ability.getBigIcon();
                if (slot.ability.getPrimaryAffinity().getColor() != null)
                    bg = ImageUtils.tintImage(slot.ability.getTriggerType().getSocketImage(),
                            slot.ability.getPrimaryAffinity().getColor(), null);

                bg = ImageUtils.outlineImage(bg, inner);
                bg = ImageUtils.outlineImage(bg, outer);
            }

            //draw bg image
            g.drawImage(bg, bounds.x+bounds.width/2-bg.getWidth(null)/2, bounds.y+bounds.height/2-bg.getHeight(null)/2, null);

            //draw icon
            if (icon != null){
                int iconDim = icon.getWidth(null);
                Rectangle iconBounds = new Rectangle(bounds.x+bounds.width/2-iconDim/2, bounds.y+bounds.height/2-iconDim/2, iconDim, iconDim);
                if (enabled){
                    g.drawImage(icon, iconBounds.x, iconBounds.y, iconBounds.x + iconBounds.width, iconBounds.y + iconBounds.height,
                            0, 0, icon.getWidth(null), icon.getHeight(null), null);
                }
                else{
                    Image grayIcon = ImageUtils.grayscaleImage(icon, null);
                    g.drawImage(grayIcon, iconBounds.x, iconBounds.y, iconBounds.x+iconBounds.width, iconBounds.y+iconBounds.height,
                            0, 0, icon.getWidth(null), icon.getHeight(null), null);
                }
            }

            //if can't meet affinity requirements, show red X
            if (!Player.INSTANCE.hasAffinityReguirements(abilitySlot.ability) && abilitySlot.ability != Ability.None && abilitySlot.type == Actor.AbilitySlot.SlotType.Equipped){
                g.drawImage(Art.consoleIcons16x16[2][8], bounds.x, bounds.y+bounds.height-16, null);
            }
        }
    }

    private static final ConsoleMenuItemRenderer<SubscreenItem> fakeRenderer = new ConsoleMenuItemRenderer<SubscreenItem>(new Rectangle(0, 0, 0, 0)) {
        public void renderItem(Graphics2D g, SubscreenItem item, ConsoleMenu menu, int index, Point location, boolean enabled, boolean focused){
            item.render(g, enabled, focused);
        }
    };

    private boolean open, trashButtonFocused = false, showNewLabel = false;
    private int openingCounter, yTrans = 120, focusedIndex = -1, errorTickCounter = 0;
    private int numTownspersonsToRescue = 0;
    private SubscreenItem eqAb1, eqAb2, eqAb3, eqAbA, spAb1, spAb2, spAb3, spAb4, nAb, aff1, aff2, aff3, aff4, aff5;
    private Rectangle descriptionBounds, equippedAreaBounds, spareAreaBounds, trashAreaBounds, trashButtonBounds, livesAreaBounds;
    private Ability heldAbility = Ability.None;
    private Actor.AbilitySlot heldAbilityOrigin;
    private List<SubscreenPoof> poofs;

	public Subscreen(Console console){
		super(console, null, "Subscreen", new Point(0, 0), fakeRenderer, new ArrayList<SubscreenItem>());
		setBounds(new Rectangle(0, 0, GameConstants.PLAYFIELD_WIDTH, GameConstants.PLAYFIELD_HEIGHT));
		setBackground(new Color(0f, 0f, 0f, 0f)); //just the initial value to avoid flicker; is recalculated below
        descriptionBounds = new Rectangle(16, 16, 320-32, 240-120);
        equippedAreaBounds = new Rectangle(12, 240-76, 108, 32);
        spareAreaBounds = new Rectangle(12, 240-42, 108, 32);
        trashAreaBounds = new Rectangle(130, 240-76, 36, 66);
        trashButtonBounds = new Rectangle(trashAreaBounds.x, trashAreaBounds.y+trashAreaBounds.height-24, trashAreaBounds.width, 12);
        livesAreaBounds = new Rectangle(169, 240-76, 36, 66);
        poofs = new ArrayList<SubscreenPoof>();
    }

    private void rebuildItems(Player player){
        int baseY = 240-64;
        eqAb1 = new SubscreenItem(new Rectangle(equippedAreaBounds.x+16, equippedAreaBounds.y+4, 24, 24), player.getAbilitySlots()[0], null, false);
        eqAb2 = new SubscreenItem(new Rectangle(equippedAreaBounds.x+40, equippedAreaBounds.y+4, 24, 24), player.getAbilitySlots()[1], null, false);
        eqAb3 = new SubscreenItem(new Rectangle(equippedAreaBounds.x+64, equippedAreaBounds.y+4, 24, 24), player.getAbilitySlots()[2], null, false);
        eqAbA = new SubscreenItem(new Rectangle(equippedAreaBounds.x+88, equippedAreaBounds.y+4, 24, 24), player.getAbilitySlots()[3], null, false);
        spAb1 = new SubscreenItem(new Rectangle(spareAreaBounds.x+16, spareAreaBounds.y+4, 24, 24), player.getSpareAbilitySlots()[0], null, true);
        spAb2 = new SubscreenItem(new Rectangle(spareAreaBounds.x+40, spareAreaBounds.y+4, 24, 24), player.getSpareAbilitySlots()[1], null, true);
        spAb3 = new SubscreenItem(new Rectangle(spareAreaBounds.x+64, spareAreaBounds.y+4, 24, 24), player.getSpareAbilitySlots()[2], null, true);
        spAb4 = new SubscreenItem(new Rectangle(spareAreaBounds.x+88, spareAreaBounds.y+4, 24, 24), player.getSpareAbilitySlots()[3], null, true);
        nAb = new SubscreenItem(new Rectangle(trashAreaBounds.x+2, (int)(trashAreaBounds.y+trashAreaBounds.getHeight()/2-12), 24, 24), player.getTrashAbilitySlot(), null, false);
        aff1 = new SubscreenItem(new Rectangle(32+5*(32+2)+10, baseY, AFFINITY_WIDTH, AFFINITY_HEIGHT), null, Affinity.Earth, false);
        aff2 = new SubscreenItem(new Rectangle(32+5*(32+2)+(AFFINITY_WIDTH+2)+10, baseY, AFFINITY_WIDTH, AFFINITY_HEIGHT), null, Affinity.Water, false);
        aff3 = new SubscreenItem(new Rectangle(32+5*(32+2)+2*(AFFINITY_WIDTH+2)+10, baseY, AFFINITY_WIDTH, AFFINITY_HEIGHT), null, Affinity.Air, false);
        aff4 = new SubscreenItem(new Rectangle(32+5*(32+2)+3*(AFFINITY_WIDTH+2)+10, baseY, AFFINITY_WIDTH, AFFINITY_HEIGHT), null, Affinity.Fire, false);
        aff5 = new SubscreenItem(new Rectangle(32+5*(32+2)+4*(AFFINITY_WIDTH+2)+10, baseY, AFFINITY_WIDTH, AFFINITY_HEIGHT), null, Affinity.Spirit, false);
        items = new ArrayList<SubscreenItem>(Arrays.asList(new SubscreenItem[]{ eqAb1, eqAb2, eqAb3, eqAbA, spAb1, spAb2, spAb3, spAb4, nAb, aff1, aff2, aff3, aff4, aff5}));

        int toRescue = 0;
        for (NPCs npc: NPCs.values())
            if (!NPCs.isRescued(npc.name(), getConsole().getScene().getGameState()) && !NPCs.isPlayer(npc.name(), getConsole().getScene().getGameState()))
                toRescue++;
        numTownspersonsToRescue = toRescue;
    }
    
    public void tick(){
        super.tick();
        if (errorTickCounter > 0) errorTickCounter--;

        if (open && openingCounter < MAX_OPENING_COUNTER) openingCounter++;
        if (!open && openingCounter > 0) openingCounter--;

        float ratio = (float)openingCounter/(float)MAX_OPENING_COUNTER;
        setBackground(new Color(0f, 0f, 0f, ratio * 0.8f));

        yTrans = 120-(int)(ratio*120);

        for (SubscreenPoof poof: new ArrayList<SubscreenPoof>(poofs)){
            poof.tick();
            if (poof.timeToLive <= 0) poofs.remove(poof);
        }

        if (!open && openingCounter == 0) getConsole().remove(this);
    }

    public Rectangle getItemBounds(int index){ return items.get(index).bounds; }
    public int indexAt(Point p){
        for (int i = 0; i < items.size(); i++){
            if (getItemBounds(i).contains(p))
                return i;
        }
        return -1;
    }

    public void mouseMoved(Point p){
        focusedIndex = indexAt(p);
        if (focusedIndex == -1) focusedItem = null;
        else focusedItem = items.get(focusedIndex);

        trashButtonFocused = trashButtonBounds.contains(p);
    }

    private boolean slotCanAccept(Actor.AbilitySlot slot, Ability ability){
        boolean isAutoSlot = focusedItem.abilitySlot == Player.INSTANCE.getAbilitySlots()[3];
        boolean holdingAutoAbility = heldAbility.getTriggerType() == Ability.TriggerType.Auto;
        boolean isSpareSlot = focusedItem.spareAbilitySlot;
        boolean isTrashSlot = focusedItem.abilitySlot.type == Actor.AbilitySlot.SlotType.Trash;
        if ((!isAutoSlot && !isSpareSlot && !isTrashSlot) && (holdingAutoAbility)) return false;
        if ((isAutoSlot) && (!holdingAutoAbility) && !isTrashSlot) return false;
        return true;
    }

    public void mouseClicked(Point p){
        int index = indexAt(p);

        //if clicked on trash button, trash button is enabled, and not holding anything, trash the item in nAB
        if (trashButtonFocused && nAb.abilitySlot.ability != Ability.None && heldAbility == Ability.None){
            nAb.abilitySlot.ability = Ability.None;
            showNewLabel = false;
            poof(nAb.bounds.x+nAb.bounds.width/2, nAb.bounds.y+nAb.bounds.height/2, 12);
            getConsole().getScene().getSound().play(Art.getSample("trash.wav"),
                    new FixedSoundSource(nAb.bounds.x+nAb.bounds.width/2, nAb.bounds.y+nAb.bounds.height/2), 1, 1, 1);
        }
        else if (index == -1 || focusedItem == null){
            if (heldAbility != Ability.None){
                //return dragged item to its original position
                heldAbilityOrigin.ability = heldAbility;
                heldAbility = Ability.None;
                updateGameState(heldAbilityOrigin);

                getConsole().getScene().getSound().play(Art.getSample("rattle.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
            }
        }

        //spend an affinity point if clicked there
        else if (focusedItem.affinity != null && getConsole().getScene().getGameState().getSpareAffinity() > 0 && heldAbility == Ability.None){
            int currentAffinity = 0;
            switch (focusedItem.affinity){
                case Fire: currentAffinity = getConsole().getScene().getGameState().getFireAffinity(); break;
                case Water: currentAffinity = getConsole().getScene().getGameState().getWaterAffinity(); break;
                case Air: currentAffinity = getConsole().getScene().getGameState().getAirAffinity(); break;
                case Earth: currentAffinity = getConsole().getScene().getGameState().getEarthAffinity(); break;
                case Spirit: currentAffinity = getConsole().getScene().getGameState().getSpiritAffinity(); break;
            }
            if (currentAffinity < Affinity.MAX_VALUE) currentAffinity += 1;
            switch (focusedItem.affinity){
                case Fire: getConsole().getScene().getGameState().setFireAffinity(currentAffinity); break;
                case Water: getConsole().getScene().getGameState().setWaterAffinity(currentAffinity); break;
                case Air: getConsole().getScene().getGameState().setAirAffinity(currentAffinity); break;
                case Earth: getConsole().getScene().getGameState().setEarthAffinity(currentAffinity); break;
                case Spirit: getConsole().getScene().getGameState().setSpiritAffinity(currentAffinity); break;
            }
            getConsole().getScene().getGameState().setSpareAffinity(getConsole().getScene().getGameState().getSpareAffinity()-1);
            getConsole().getScene().getSound().play(Art.getSample("bloop.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
        }
        //if clicked on ability slot...
        else if (focusedItem.abilitySlot != null){
            //if not holding an ability...
            if (heldAbility == Ability.None){
                //if there's nothing there, do nothing
                if (focusedItem.abilitySlot.ability == Ability.None){}
                //if there is, pick it up
                else{
                    heldAbility = focusedItem.abilitySlot.ability;
                    heldAbilityOrigin = focusedItem.abilitySlot;
                    focusedItem.abilitySlot.ability = Ability.None;
                    showNewLabel = false;
                    updateGameState(focusedItem.abilitySlot);
                    getConsole().getScene().getSound().play(Art.getSample("bloop.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
                }
            }
            else{
                //if the spot can accept the ability you're holding...
                if (slotCanAccept(focusedItem.abilitySlot, heldAbility)){
                    //if holding and clicked on a spot with an ability in it, swap them
                    if (focusedItem.abilitySlot.ability != Ability.None){
                        Ability clickedOn = focusedItem.abilitySlot.ability;
                        focusedItem.abilitySlot.ability = heldAbility;
                        updateGameState(focusedItem.abilitySlot);
                        heldAbility = clickedOn;
                        //heldAbilityOrigin = focusedItem.abilitySlot; do NOT do this; if try to put back you lose the ability
                    }
                    //if holding and clicked on an empty spot, drop ability into it
                    else{
                        focusedItem.abilitySlot.ability = heldAbility;
                        updateGameState(focusedItem.abilitySlot);
                        heldAbility = Ability.None;
                        heldAbilityOrigin = null;
                    }
                    getConsole().getScene().getSound().play(Art.getSample("bloop.wav"), new FixedSoundSource(160, 120), 1, 1, 1);
                }
            }
        }
    }

    private void updateGameState(Actor.AbilitySlot slot){
        if (slot.type == Actor.AbilitySlot.SlotType.Equipped){
            if (slot.index == 0) getConsole().getScene().getGameState().setEqAbId1(slot.ability.name());
            if (slot.index == 1) getConsole().getScene().getGameState().setEqAbId2(slot.ability.name());
            if (slot.index == 2) getConsole().getScene().getGameState().setEqAbId3(slot.ability.name());
        }
        if (slot.type == Actor.AbilitySlot.SlotType.Auto){
            if (slot.index == 3) getConsole().getScene().getGameState().setEqAbId4(slot.ability.name());
        }
        if (slot.type == Actor.AbilitySlot.SlotType.Spare){
            if (slot.index == 0) getConsole().getScene().getGameState().setSpAbId1(slot.ability.name());
            if (slot.index == 1) getConsole().getScene().getGameState().setSpAbId2(slot.ability.name());
            if (slot.index == 2) getConsole().getScene().getGameState().setSpAbId3(slot.ability.name());
            if (slot.index == 3) getConsole().getScene().getGameState().setSpAbId4(slot.ability.name());
        }
    }

    public Image getCursor(){
        if (errorTickCounter > 0) return Art.consoleIcons16x16[2][8];
        else if (focusedItem != null && focusedItem.affinity != null && getConsole().getScene().getGameState().getSpareAffinity() > 0) return Art.consoleIcons16x16[2][5]; //point finger
        else if (trashButtonFocused && nAb.abilitySlot.ability != Ability.None) return Art.consoleIcons16x16[2][5];//point finger
        else if (focusedItem != null && focusedItem.abilitySlot != null &&
                focusedItem.abilitySlot.ability != Ability.None && heldAbility == Ability.None) return Art.consoleIcons16x16[1][5];
        else if (heldAbility == Ability.None) return Art.consoleIcons16x16[2][1];
        else return heldAbility.getCursorIcon(tickCounter); //ability token plus pointer
    }

    //noop'd
    public void select(){}
    public void cancel(){}
    public void moveUp(){}
    public void moveDown(){}

    public void paint(Graphics g){
        paintBackground(g, getBounds());
        g.translate(0, yTrans);
        paintBorder(g, getBounds());
        paintContents(g);
        g.translate(0, -yTrans);
    }

    protected void paintBorder(Graphics g, Rectangle bounds){}

	protected void paintBackground(Graphics g, Rectangle bounds){
		g.setColor(getBackground());
		g.fillRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height); 
	}

    public void paintContents(Graphics g){
        if (!open && openingCounter == 0) return;

        //paint area bgs
        g.setColor(new Color(0f, 0f, 0f, 0.5f));
        g.fillRoundRect(8 + equippedAreaBounds.x, equippedAreaBounds.y, equippedAreaBounds.width, equippedAreaBounds.height, 5, 5);
        g.fillRoundRect(8+spareAreaBounds.x, spareAreaBounds.y, spareAreaBounds.width, spareAreaBounds.height, 5, 5);
        g.fillRoundRect(aff1.bounds.x-4, aff1.bounds.y-12, aff1.bounds.width*5+14, aff1.bounds.height+16, 5, 5);
        g.fillRoundRect(livesAreaBounds.x, livesAreaBounds.y, livesAreaBounds.width, livesAreaBounds.height, 5, 5);

        g.setColor(new Color(0.5f, 0f, 0f, 0.5f));
        g.fillRoundRect(trashAreaBounds.x, trashAreaBounds.y, trashAreaBounds.width, trashAreaBounds.height, 5, 5);

        //paint boss progress
        Rectangle bossProgressBounds = new Rectangle(livesAreaBounds.x+2, livesAreaBounds.y+2, livesAreaBounds.width-4, livesAreaBounds.width-4);
        paintBossProgress((Graphics2D)g, bossProgressBounds);

        //paint lives
        Image livesIcon = Art.powerups[0][5];
        g.drawImage(livesIcon, livesAreaBounds.x+2, livesAreaBounds.y+livesAreaBounds.height-livesIcon.getHeight(null)-4, null);
        GameComponent.drawString(g, " x"+getConsole().getScene().getGameState().getLives(),
                livesAreaBounds.x+7, livesAreaBounds.y+livesAreaBounds.height-livesIcon.getHeight(null)+4+1, 0);
        GameComponent.drawString(g, " x"+getConsole().getScene().getGameState().getLives(),
                livesAreaBounds.x+6, livesAreaBounds.y+livesAreaBounds.height-livesIcon.getHeight(null)+4, 7);

        //paint items
        for (int i = 0; i < items.size(); i++){
            SubscreenItem item = items.get(i);
            boolean canAccept = true;
            if (focusedItem != null && heldAbility != Ability.None && !slotCanAccept(focusedItem.abilitySlot, heldAbility)) canAccept = false;
            getRenderer().renderItem((Graphics2D) g, item, this, i, new Point(0, 0), true, i == focusedIndex && canAccept);
        }

        //draw equipped and spare labels
        g.drawImage(Art.consoleIcons16x16[0][3], equippedAreaBounds.x, equippedAreaBounds.y+16, null);
        g.drawImage(Art.consoleIcons16x16[0][2], equippedAreaBounds.x, equippedAreaBounds.y, null);
        g.drawImage(Art.consoleIcons16x16[1][3], spareAreaBounds.x, spareAreaBounds.y+16, null);
        g.drawImage(Art.consoleIcons16x16[1][2], spareAreaBounds.x, spareAreaBounds.y, null);

        //new label
        if (showNewLabel){
            g.drawImage(Art.consoleIcons16x16[2][3], nAb.bounds.x, nAb.bounds.y-12, null);
            g.drawImage(Art.consoleIcons16x16[3][3], nAb.bounds.x+16, nAb.bounds.y-12, null);
        }

        //trash label
        if (nAb.abilitySlot.ability != Ability.None){
            Image image1 = Art.consoleIcons16x16[3][5], image2 = Art.consoleIcons16x16[3][6];
            int offset = 0;
            if (trashButtonFocused){
                Color tbColor = ImageUtils.calculateCycleColor(tickCounter, new Color[]{Color.YELLOW, ImageUtils.HALF_YELLOW }, 5);
                image1 = ImageUtils.outlineImage(image1, tbColor);
                image2 = ImageUtils.outlineImage(image2, tbColor);
                offset = 1;
            }
            g.drawImage(image1, trashButtonBounds.x+2-offset, trashButtonBounds.y+trashButtonBounds.height-12-offset, null);
            g.drawImage(image2, trashButtonBounds.x+2+16-offset, trashButtonBounds.y+trashButtonBounds.height-12-offset, null);
        }
        else{
            g.drawImage(Art.consoleIcons16x16[3][1], trashButtonBounds.x+2, trashButtonBounds.y+trashButtonBounds.height-12, null);
            g.drawImage(Art.consoleIcons16x16[3][2], trashButtonBounds.x+2+16, trashButtonBounds.y+trashButtonBounds.height-12, null);
        }

        //points-to-spend label
        if (getConsole().getScene().getGameState().getSpareAffinity() > 0){
            //PTS label
            g.drawImage(Art.consoleIcons16x16[0][4], aff1.bounds.x+12, aff1.bounds.y-16-2, null);
            g.drawImage(Art.consoleIcons16x16[1][4], aff1.bounds.x+28, aff1.bounds.y-16-2, null);
            g.drawImage(Art.consoleIcons16x16[2][4], aff1.bounds.x+44, aff1.bounds.y-16-2, null);
            g.drawImage(Art.consoleIcons16x16[3][4], aff1.bounds.x+60, aff1.bounds.y-16-2, null);

            //little glowing arrow
            Color ptsColor = ImageUtils.calculateCycleColor(tickCounter, new Color[]{Color.WHITE, ImageUtils.HALF_WHITE }, 20);
            g.drawImage(ImageUtils.outlineImage(Art.consoleIcons16x16[0][5], ptsColor),
                    aff1.bounds.x+60, aff1.bounds.y-15, null);

            //the actual number
            g.drawImage(ImageUtils.outlineImage(Art.font[16 + getConsole().getScene().getGameState().getSpareAffinity()][4], ptsColor),
                    aff1.bounds.x + 5, aff1.bounds.y - 11, null);

        }

        //draw description of focused item
        if (open && openingCounter == MAX_OPENING_COUNTER){
            paintDescription((Graphics2D)g);

            //if focused item is unusable, say so
            if (focusedItem != null && focusedItem.abilitySlot != null && !Player.INSTANCE.hasAffinityReguirements(focusedItem.abilitySlot.ability) &&
                    focusedItem.abilitySlot.ability != Ability.None && focusedItem.abilitySlot.type == Actor.AbilitySlot.SlotType.Equipped){
                g.setColor(Color.RED);
                g.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
                g.drawString("You don't meet the affinity requirements to use this.", descriptionBounds.x+8, descriptionBounds.y+descriptionBounds.height+24);
            }
        }

        //draw poofs
        for (SubscreenPoof poof: poofs) poof.render(g);
    }

    private void paintBossProgress(Graphics2D g, Rectangle bounds){
        int cx = (int)bounds.getCenterX();
        int cy = (int)bounds.getCenterY();
        int r = (bounds.width/2)-4;
        for (int i = 0; i < 5; i++){
            double angle = i*Math.PI*2d/5d;
            angle -= Math.PI/2;
            int px = cx+(int)(Math.cos(angle)*r);
            int py = cy+(int)(Math.sin(angle)*r);

            Color c1 = new Color(0.5f, 0f, 0.2f),
                c2 = new Color(0.3f, 0f, 0f);
            if (i == 0 && "true".equals(getConsole().getScene().getGameState().getVariable(GigapedeSegment.DEFEATED_STATE_VARIABLE))){
                c1 = Color.RED;
                c2 = Color.RED.darker();
            }
            else if (i == 1 && "true".equals(getConsole().getScene().getGameState().getVariable(SpiderQueen.DEFEATED_STATE_VARIABLE))){
                c1 = Color.CYAN;
                c2 = Color.CYAN.darker();
            }
            else if (i == 2 && "true".equals(getConsole().getScene().getGameState().getVariable(Wyvern.DEFEATED_STATE_VARIABLE))){
                c1 = Color.YELLOW;
                c2 = Color.YELLOW.darker();
            }
            else if (i == 3 && "true".equals(getConsole().getScene().getGameState().getVariable(Vampire.DEFEATED_STATE_VARIABLE))){
                c1 = Color.GREEN;
                c2 = Color.GREEN.darker();
            }
            else if (i == 4 && "true".equals(getConsole().getScene().getGameState().getVariable(BigBeholder.DEFEATED_STATE_VARIABLE))){
                c1 = Color.WHITE;
                c2 = Color.LIGHT_GRAY;
            }

            g.setColor(c1);
            g.fillOval(px-3, py-3, 6, 6);
            g.setColor(c2);
            g.drawOval(px-3, py-3, 6, 6);
        }

        if ("true".equals(getConsole().getScene().getGameState().getVariable(StoneKing.DEFEATED_STATE_VARIABLE))){
            Color c1 = Color.GRAY, c2 = Color.DARK_GRAY;
            g.setColor(c1);
            g.fillOval(cx-3, cy-3, 6, 6);
            g.setColor(c2);
            g.drawOval(cx-3, cy-3, 6, 6);
        }
    }

    public int getOpeningCounter(){ return openingCounter; }
    public boolean isOpen(){ return open; }

	public void opened(){
        rebuildItems(Player.INSTANCE);
        if (nAb.abilitySlot.ability != Ability.None) showNewLabel = true;
        open = true;
    }

    public void closed(){
        if (heldAbility != Ability.None || nAb.abilitySlot.ability != Ability.None){
            errorTickCounter = 10;
        }
        else open = false;
    }

    private void paintDescription(Graphics2D g){
        Rectangle bounds = descriptionBounds;

        //paint select focused ability or affinity, if there is one
        if (focusedItem != null){
            if (focusedItem.abilitySlot != null && focusedItem.abilitySlot.ability != Ability.None){
                paintAbilityDescription(g, focusedItem.abilitySlot.ability);
            }
            else if (focusedItem.abilitySlot != null){
                paintAbilitySlotDescription(g, focusedItem.abilitySlot);
            }
            else if (focusedItem.affinity != null){
                paintAffinityDescription(g, focusedItem.affinity);
            }
        }
        //else paint our game progress
        else paintGameProgress(g);

        //border
        g.setColor(Color.WHITE);
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);

        //paint "help" label
        g.drawImage(Art.consoleIcons16x16[2][2], bounds.x+4, bounds.y-12, null);
    }

    private void paintAbilityDescription(Graphics2D g, Ability ability){
        //icon in upper left corner
        Image icon = ability.getBigIcon();
        g.drawImage(icon, descriptionBounds.x+8, descriptionBounds.y+8, descriptionBounds.x+40, descriptionBounds.y+40,
                0, 0, icon.getWidth(null), icon.getHeight(null), null);

        //then name
        Image nameImage = TextImageCreator.getOutlinedTextImage(ability.getName(), TextImageCreator.COLOR_WHITE,
                ability.getPrimaryAffinity().getColor());
        g.drawImage(nameImage, descriptionBounds.x+8+32+4, descriptionBounds.y+8, null);

        //next to that is hp cost, if there is one
        if (ability.getHpCost() > 0){
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.setColor(Color.RED);
            g.drawString("Costs "+ability.getHpCost()+" HP to use.", descriptionBounds.x+8+32+4+nameImage.getWidth(null)+4, descriptionBounds.y+20);
        }

        //next to that casting cost with "requires" label; if no requirements don't show label
        int affX = descriptionBounds.x+descriptionBounds.width-ability.getAffinityRequirements().size()*10-8;
        int affY = descriptionBounds.y+12;
        for (Affinity affinity: ability.getAffinityRequirements()){
            g.drawImage(affinity.getIcon(), affX, affY, affX+8, affY+8, 0, 0, affinity.getIcon().getWidth(null), affinity.getIcon().getHeight(null), null);
            affX += 10;
        }

        //below icon is activation type icon
        switch (ability.getTriggerType()) {
            case Tap:
                g.drawImage(Art.consoleIcons16x16[2][6], descriptionBounds.x+8+8, descriptionBounds.y+8+32, null);
                break;
            case Hold:
                g.drawImage(Art.consoleIcons16x16[0][7], descriptionBounds.x+8, descriptionBounds.y+8+32, null);
                g.drawImage(Art.consoleIcons16x16[1][7], descriptionBounds.x+8+16, descriptionBounds.y+8+32, null);
                break;
            case Charge:
                g.drawImage(Art.consoleIcons16x16[2][7], descriptionBounds.x+8, descriptionBounds.y+8+32, null);
                g.drawImage(Art.consoleIcons16x16[3][7], descriptionBounds.x+8+16, descriptionBounds.y+8+32, null);
                break;
            case Auto:
                g.drawImage(Art.consoleIcons16x16[0][8], descriptionBounds.x+8, descriptionBounds.y+8+32, null);
                g.drawImage(Art.consoleIcons16x16[1][8], descriptionBounds.x+8+16, descriptionBounds.y+8+32, null);
                break;
        }

        //then description in main area
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        int textY = descriptionBounds.y+4+32+4;
        for (String line: breakString(g, ability.getDescription(), descriptionBounds.width-(descriptionBounds.x+4+32+4)-20)){
            g.drawString(line, descriptionBounds.x+4+32+4, textY);
            textY += 16;
        }
    }

    private java.util.List<String> breakString(Graphics g, String string, int maxWidth){
        java.util.List<String> lines = new ArrayList<String>();
        String[] words = string.split("\\s");
        String currentLine = "";
        for (String word: words){
            if (g.getFontMetrics().stringWidth(currentLine) >= maxWidth){
                lines.add(currentLine);
                currentLine = "";
            }
            currentLine += " "+word;
        }
        lines.add(currentLine);

        return lines;
    }

    private void paintAffinityDescription(Graphics2D g, Affinity affinity){
        //icon in upper left corner
        Image icon = affinity.getIcon();
        g.drawImage(icon, descriptionBounds.x+8, descriptionBounds.y+8, descriptionBounds.x+40, descriptionBounds.y+40,
                0, 0, icon.getWidth(null), icon.getHeight(null), null);

        //then name
        Image nameImage = TextImageCreator.getOutlinedTextImage(affinity.getName(), TextImageCreator.COLOR_WHITE, affinity.getColor());
        g.drawImage(nameImage, descriptionBounds.x+8+32+4, descriptionBounds.y+8, null);

        //then description
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        int textY = descriptionBounds.y+4+32+4;
        for (String line: breakString(g, affinity.getDescription(), descriptionBounds.width-(descriptionBounds.x+4+32+4)-20)){
            g.drawString(line, descriptionBounds.x+4+32+4, textY);
            textY += 16;
        }
    }

    private void paintAbilitySlotDescription(Graphics2D g, Actor.AbilitySlot abilitySlot){
        //icon in upper left corner
        Image icon = Art.consoleIcons32x32[5][0];
        if (abilitySlot.type == Actor.AbilitySlot.SlotType.Auto) icon = Art.consoleIcons32x32[6][0];
        g.drawImage(icon, descriptionBounds.x+8, descriptionBounds.y+8, descriptionBounds.x+40, descriptionBounds.y+40,
                0, 0, icon.getWidth(null), icon.getHeight(null), null);

        //then socket type name
        Image nameImage = TextImageCreator.getOutlinedTextImage(abilitySlot.type.name(), TextImageCreator.COLOR_WHITE, Color.BLACK);
        g.drawImage(nameImage, descriptionBounds.x+8+32+4, descriptionBounds.y+8, null);

        //then description
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        int textY = descriptionBounds.y+4+32+4;
        String description = "";
        switch (abilitySlot.type) {
            case Equipped:
                description = "Ability gems placed in these sockets determine your equipped abilities.  Only \"Auto\" abilities may not be placed here.";
                break;
            case Auto:
                description = "This socket determines your auto ability.  Only \"Auto\" abilities may be placed here.";
                break;
            case Spare:
                description = "This row of sockets are for spare gems you want to keep but aren't using.";
                break;
            case Trash:
                description = "Drop ability gems you don't want here, then click \"trash\" to get rid of them.  You may not close the subscreen if there is a gem in this socket.";
                break;
            case Initial: break; //won't happen
        }

        for (String line: breakString(g, description, descriptionBounds.width-(descriptionBounds.x+4+32+4)-20)){
            g.drawString(line, descriptionBounds.x+4+32+4, textY);
            textY += 16;
        }
    }

    private void paintGameProgress(Graphics2D g){
        //paint little matrix of all rescuable people, in color if rescued; hover to show name
        int i = 0;
        int x = descriptionBounds.x+8, y = descriptionBounds.y-4;
        for (NPCs npc: NPCs.values()){
            //skip the dog
            if (npc == NPCs.Dog) continue;

            //38 remaining townspersons - 5,6,5,6,5,6,5
            int row = (i/11)*2;
            int column = (i%11);
            if (i%11 > 4){
                row++;
                column -= 5;
            }

            int nx = x+column*16;
            if (row%2 == 0) nx += 8;
            int ny = y+row*12;

            Image npcImage = Art.characters[npc.getXPic()][npc.getYPic()];
            if (!NPCs.isRescued(npc.name(), getConsole().getScene().getGameState()) &&
                    !NPCs.isPlayer(npc.name(), getConsole().getScene().getGameState()))
                npcImage = ImageUtils.grayscaleImage(npcImage, null);

            g.drawImage(npcImage, nx, ny, null);

            i++;
        }

        //beneath that a little mini crystal icon x number remaining to be rescued
        int cx = x+10, cy = y+16*6+8;
        Image crystalImage = Art.effects48x48[0][0];
        int to = numTownspersonsToRescue < 10 ? 8 : 0;
        GameComponent.drawString(g, ""+numTownspersonsToRescue, cx+1+to, cy+16+1-8, 0);
        GameComponent.drawString(g, "" + numTownspersonsToRescue, cx + to, cy + 16 - 8, 7);
        g.drawImage(crystalImage, cx + 16, cy, 16, 16, null);
        GameComponent.drawString(g, "Remain", cx + 34 + 1, cy + 16 + 1 - 8, 0);
        GameComponent.drawString(g, "Remain", cx+34, cy+16-8, 7);

        //matrix of squares, each representing an ability, with ?'s in the ones you haven't found or icon in ones you have; hover to show name
        //currently 67 abilities available but will certainly be more
        int ax = x+104, ay = y+8, aw = (descriptionBounds.width-ax+16)/10;
        int ai = 0;
        for (Ability ability: Ability.values()){
            if (ability == Ability.None) continue;

            drawAbility(g, ability, new Rectangle(ax, ay, aw, 16), ability.isFound(getConsole().getScene().getGameState()));

            ax += aw;
            ai++;
            if (ai%10 == 0 && ai != 0){
                ax = x+104;
                ay += 16;
            }
        }
    }

    private void drawAbility(Graphics2D g, Ability ability, Rectangle bounds, boolean found){
        Color reg = ability.getPrimaryAffinity().getColor();
        Color high = ability.getPrimaryAffinity().getHighlightColor();
        if (reg == null) reg = Color.GRAY;
        if (high == null) high = Color.LIGHT_GRAY;
        if (!found){
            reg = Color.BLACK;
            high = Color.DARK_GRAY;
        }
        Color c2 = new Color(reg.getRed(), reg.getGreen(), reg.getBlue(), 128),
                c3 = new Color(high.getRed(), high.getGreen(), high.getBlue(), 128);

        g.setColor(c3);
        g.fillRect(bounds.x, bounds.y+4, bounds.width-1, bounds.height-1-8);
        g.setColor(c2);
        g.drawRect(bounds.x, bounds.y+4, bounds.width - 1, bounds.height-1-8);

        if (found) g.drawImage(ability.getBigIcon(), bounds.x + 1, bounds.y, 15, 15, null);
        else{
            GameComponent.drawString(g, "?", bounds.x+6, bounds.y+5, 0);
            GameComponent.drawString(g, "?", bounds.x+5, bounds.y+4, 8);
        }
    }

    private class SubscreenPoof {
        private float x, y, xa, ya;
        private int timeToLive;
        private int xPic, yPic;

        public SubscreenPoof(float x, float y){
            this.x = x;
            this.y = y;
            this.xa = (int)(10-(Math.random()*20));
            this.ya = -2-(int)(Math.random()*2);
            timeToLive = 5+(int)(Math.random()*5);
            yPic = (int)(1+Math.random());
        }

        public void tick(){
            xa *= 0.5f;
            x += xa;
            y += ya;
            timeToLive--;
            if (timeToLive <= 2) xPic = 3-timeToLive;
        }

        public void render(Graphics g){
            if (timeToLive <= 0) return;
            g.drawImage(Art.projectiles[xPic][yPic], (int)(x-4), (int)(y-4), null);
        }
    }

    private void poof(int x, int y, int squareRadius){
        for (int i = 0; i < 50; i++){
            int poofX = x+squareRadius/2-(int)(Math.random()*squareRadius);
            int poofY = y+squareRadius/2-(int)(Math.random()*squareRadius);
            poofs.add(new SubscreenPoof(poofX, poofY));
        }
    }
}

package com.oblong.af.models.console;

import com.oblong.af.GameComponent;
import com.oblong.af.models.Ability;
import com.oblong.af.sprite.Actor;
import com.oblong.af.sprite.Player;
import com.oblong.af.sprite.Prop;
import com.oblong.af.util.Art;
import com.oblong.af.util.ImageUtils;

import java.awt.*;

@SuppressWarnings("serial")
public class BattleDisplay extends ConsoleObject {

    private static final Color transparent = new Color(0f, 0f, 0f, 0f);

    private int cutoff = 0;
    private float trans = 0;

    public BattleDisplay(Console console){
		super(console, "BattleDisplay", new Rectangle(0, 0, 320, 240), 0, false);
	}

    public void tick(){
        super.tick();

        float ratio = (float)getConsole().getScene().getSubscreen().getOpeningCounter()/(float)Subscreen.MAX_OPENING_COUNTER;
        trans = -ratio*200;
    }

	protected void paintBackground(Graphics g, Rectangle bounds){}
	protected void paintBorder(Graphics g, Rectangle bounds){}
	public void paintContents(Graphics g1){
        //general painting stuff
        Graphics2D g = (Graphics2D)g1;
		Player player = getConsole().getScene().player;

        g.translate(trans, 0);

        //figure portrait stuff
        Color pbgColor = new Color(0f, 0.1f, 1f, 1f);
        Image portrait = Art.portraits[player.getPortraitXPic()][player.getPortraitYPic()];
        Rectangle portraitBounds = new Rectangle(2, 204, 32, 32);
        if (player.getTintColor() == Prop.COLOR_TINT_DAMAGE) portraitBounds.translate((int)(Math.random()*4-2), 0);

        if (player.getFadeRatio() != 1f) portrait = ImageUtils.fadeImage(portrait, null, player.getFadeRatio());
        if (player.isDesaturated()){
            portrait = ImageUtils.grayscaleImage(portrait, null);
            pbgColor = Color.GRAY;
        }
        else if (player.getTintColor() != null){
            try{ portrait = ImageUtils.tintImage(portrait, player.getTintColor(), null); }
            catch(Exception e){ System.err.println(); }
            pbgColor = player.getTintColor();
        }
        //paint background
        g.setPaint(new GradientPaint(portraitBounds.x, portraitBounds.y, pbgColor, portraitBounds.x, portraitBounds.y + portraitBounds.height, transparent));
        g.fillRoundRect(portraitBounds.x, portraitBounds.y, portraitBounds.width, portraitBounds.height, 10, 10);
        //paint image
        g.drawImage(portrait, portraitBounds.x, portraitBounds.y, null);
        //paint border
        g.setColor(Color.BLACK);
        g.drawRoundRect(portraitBounds.x, portraitBounds.y, portraitBounds.width, portraitBounds.height, 10, 10);
        g.setPaint(new GradientPaint(portraitBounds.x + 1, portraitBounds.y + 1, Color.WHITE, portraitBounds.x + 1 + portraitBounds.width - 2, portraitBounds.y + 1 + portraitBounds.height - 2, Color.BLACK));
        g.drawRoundRect(portraitBounds.x + 1, portraitBounds.y + 1, portraitBounds.width - 2, portraitBounds.height - 2, 10, 10);

		//paint hp bar
		drawBigMeter(g, new Point(portraitBounds.x + portraitBounds.width / 2 - 11, portraitBounds.y - 13), 8, Color.RED, Color.PINK, player.getHp(), player.getMaxHp(), Art.consoleIcons8x8[7][0]);

        //paint ability timers
        int atDim = 23;
		Rectangle abilityBounds = new Rectangle(portraitBounds.x+portraitBounds.width+2, portraitBounds.y+portraitBounds.height-atDim, atDim, atDim);
        drawAbilitySlot(g, player.getAbilitySlots()[0], abilityBounds);
        abilityBounds.translate((int)Math.ceil(atDim+2), 0);
		drawAbilitySlot(g, player.getAbilitySlots()[1], abilityBounds);
		abilityBounds.translate((int)Math.ceil(atDim+2), 0);
		drawAbilitySlot(g, player.getAbilitySlots()[2], abilityBounds);
        abilityBounds.translate((int) Math.ceil(atDim + 2), 0);
        drawAbilitySlot(g, player.getAbilitySlots()[3], abilityBounds);

        g.translate(-trans, 0);

        //paint boss hp bar, if there is one
        java.util.List<Prop> bosses = getConsole().getScene().getBosses();
        if (bosses.size() > 0 || cutoff > 0){
            int hp = 0;
            int maxHp;
            boolean anyNotDead = false;
            boolean tinted = false;
            if (bosses.size() > 0){
                hp = 0;
                maxHp = 0;
                for (Prop boss: bosses){
                    hp += boss.getHp();
                    maxHp += boss.getMaxHp();
                    if (!boss.isDead()) anyNotDead = true;
                    if (boss.getTintColor() == Actor.COLOR_TINT_DAMAGE) tinted = true;
                }

                if (!anyNotDead){ //that is, all are dead
                    cutoff--;
                    maxHp = Math.min(maxHp, cutoff);
                }
                else if (anyNotDead && cutoff < maxHp){
                    cutoff++;
                    hp = Math.min(cutoff, hp);
                    maxHp = Math.min(cutoff, maxHp);
                }
                else if (hp <= 0 && cutoff > 0){
                    cutoff--;
                    maxHp = cutoff;
                }
            }
            else{
                cutoff--;
                maxHp = cutoff;
            }

            if (cutoff < 0) cutoff = 0;
            if (maxHp < 0) maxHp = 0;

            int shakeX = 0;
            if (tinted) shakeX = (int)(Math.random()*4-2);
            drawBigMeter(g, new Point(304+shakeX, 191), 8, Color.BLUE, Color.CYAN, hp, maxHp, Art.consoleIcons8x8[7][1]);
        }

    }

    private void drawBigMeter(Graphics2D g, Point bottomLeftCorner, int width, Color c, Color h, int value, int maxValue, Image icon){
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
        Rectangle iconBounds = new Rectangle(barBounds.x+barBounds.width/2-7, barBounds.y+barBounds.height-1, 13, 13);
        //paint background
        g.setPaint(new GradientPaint(iconBounds.x, iconBounds.y, c, iconBounds.x, iconBounds.y+iconBounds.height, transparent));
        g.fillRoundRect(iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 10, 10);
        //paint image
        g.drawImage(icon, iconBounds.x+iconBounds.width/2-icon.getWidth(null)/2+1, iconBounds.y+iconBounds.height/2-icon.getHeight(null)/2+1, null);
        //paint border
        g.setColor(Color.BLACK);
        g.drawRoundRect(iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 10, 10);
        g.setPaint(new GradientPaint(iconBounds.x+1, iconBounds.y+1, Color.WHITE, iconBounds.x+1+iconBounds.width-2, iconBounds.y+1+iconBounds.height-2, Color.BLACK));
        g.drawRoundRect(iconBounds.x+1, iconBounds.y+1, iconBounds.width-2, iconBounds.height-2, 10, 10);
    }

    private void drawAbilitySlot(Graphics2D g, Actor.AbilitySlot slot, Rectangle bounds){
        //if our button is pressed, we're buttonDown
        boolean buttonDown = false;
        if (slot.index == 0 && GameComponent.keys[GameComponent.KEY_ABILITY_1]) buttonDown = true;
        else if (slot.index == 1 && GameComponent.keys[GameComponent.KEY_ABILITY_2]) buttonDown = true;
        else if (slot.index == 2 && GameComponent.keys[GameComponent.KEY_ABILITY_3]) buttonDown = true;

        //if can't meet affinity requirements or currently can't use this ability, we're not enabled
        boolean enabled = !(!Player.INSTANCE.hasAffinityReguirements(slot.ability) && slot.ability != Ability.None &&
                slot.type == Actor.AbilitySlot.SlotType.Equipped);
        if (!Player.INSTANCE.abilitySlotActive(slot)) enabled = false;

        //figure out border colors
        Color outer, inner;
        int outlineTick = tickCounter;
        if (buttonDown){
            outer = Color.CYAN;
            inner = Color.WHITE;
        }
        else if (enabled){
            outer = slot.ability.getPrimaryAffinity().getHighlightColor();
            inner = slot.ability.getPrimaryAffinity().getColor();
        }
        else{
            outer = Color.BLACK;
            inner = Color.DARK_GRAY;
            outlineTick = 0;
        }

        inner = ImageUtils.calculateCycleColor(outlineTick, new Color[]{inner, new Color(inner.getRed(), inner.getGreen(), inner.getBlue(), 128) }, 20);
        outer = ImageUtils.calculateCycleColor(outlineTick, new Color[]{outer, new Color(outer.getRed(), outer.getGreen(), outer.getBlue(), 128) }, 20);

        //figure out ability icon image and background image
        Image icon = null;
        if (slot.ability != Ability.None){
            Ability ability = slot.ability;
            icon = ability.getBigIcon();

            float filledHeight;
            boolean cooldown;
            int value = (int)slot.cooldownTimer;
            int maxValue = slot.ability.getMaxCooldown();
            cooldown = value > 0;

            if (slot.chargeTimer > 0){
                cooldown = false;
                value = (int)slot.chargeTimer;
                maxValue = slot.ability.getMaxCharge();
            }

            if (maxValue == 0) maxValue = 1;

            filledHeight = bounds.height*(maxValue-value)/maxValue;

            Image bg = ability.getTriggerType().getSocketImage();
            if (slot.index == 3) bg = Art.consoleIcons32x32[6][0]; //if we're the auto-only slot
            bg = ImageUtils.outlineImage(bg, inner);
            bg = ImageUtils.outlineImage(bg, outer);

            Color bgColor = ability.getPrimaryAffinity().getColor() == null ? Color.GRAY : ability.getPrimaryAffinity().getColor();
            if (cooldown || !enabled) bgColor = Color.BLACK;
            Image tbg = ImageUtils.tintImage(slot.ability.getTriggerType().getSocketImage(), bgColor, null);

            //draw bg image
            g.drawImage(bg, bounds.x+bounds.width/2-bg.getWidth(null)/2, bounds.y+bounds.height/2-bg.getHeight(null)/2, null);

            //draw secondary bg image
            Shape oldClip = g.getClip();
            g.setClip(new Rectangle(bounds.x+bounds.width/2-bg.getWidth(null)/2, (int)(bounds.y+bounds.height-filledHeight), bg.getWidth(null), (int)filledHeight));
            g.drawImage(tbg, bounds.x+bounds.width/2-tbg.getWidth(null)/2, bounds.y+bounds.height/2-tbg.getHeight(null)/2, null);
            g.setClip(oldClip);
        }

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
    }
}

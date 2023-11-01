package com.oblong.af.level.decorators;

import com.oblong.af.level.AreaScene;
import java.awt.*;
import java.util.ArrayList;

public class SnowDecorator extends LayerDecorator {

	private class Snowflake {
		public int x, y, age, origX;
		public Snowflake(int x, int y){
			this.x = x;
			origX = x;
			this.y = y;
			age = 0;
		}
		public void tick(){ 
			age++;
			x = origX+(int)(Math.sin(age)*2);
			//if (age%2 == 0)
                y++;
		}
	}
	
	private ArrayList<Snowflake> flakes = new ArrayList<Snowflake>();
	
	public SnowDecorator(){}

	public void tick(AreaScene scene){
		reinit(); //clears image
        tickCounter++;
		for (int i = 0; i < 10; i++){
			int x = ((int)(Math.random()*1000))%320;
			int y = ((int)(Math.random()*1000))%240;
			flakes.add(new Snowflake(x, y));
		}
		for (Snowflake flake: new ArrayList<Snowflake>(flakes)){
			flake.tick();
            graphics.setColor(Color.WHITE);
            graphics.drawLine(flake.x, flake.y, flake.x, flake.y);
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawLine(flake.x, flake.y-1, flake.x, flake.y-1);
            if (flake.age > 20) flakes.remove(flake);
		}
    }
}

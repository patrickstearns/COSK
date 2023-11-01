package com.oblong.af.level.decorators;

import com.oblong.af.level.AreaScene;

import java.awt.*;
import java.util.ArrayList;

public class FogDecorator extends LayerDecorator {

	private static final int BLOB_LIFESPAN = 300;
	private class FogBlob {
		public int x, y, age, origX;
		public FogBlob(int x, int y, int age){
			this.x = x;
			origX = x;
			this.y = y;
			this.age = age;
		}
		public void tick(){ 
			age++;
			if (age%4 == 0) x++;
		}
		public Color getColor(){
			float t = 0.2f;
			if (age < 50) t *= ((float)age/50f);
			else if (age > BLOB_LIFESPAN-50) t *= -((float)(age-BLOB_LIFESPAN))/50f;
			return new Color(1f, 1f, 1f, t);
		}
	}
	
	private ArrayList<FogBlob> blobs = new ArrayList<FogBlob>();
	private int tickCounter = 0;
	
	public FogDecorator(){}

	public void init(int width, int height){
		super.init(width, height);
		for (int i = 0; i < 20; i++){
			int x = ((int)(Math.random()*1000))%640-320;
			int y = ((int)(Math.random()*1000))%240;
			int a = ((int)(Math.random()*1000))%BLOB_LIFESPAN;
			blobs.add(new FogBlob(x, y, a));
		}
	}
	
	public void tick(AreaScene scene){
		reinit(); //clears image
		graphics.setColor(Color.WHITE);
		tickCounter++;
		for (FogBlob blob: new ArrayList<FogBlob>(blobs)){
			blob.tick();
			if (blob.age > BLOB_LIFESPAN){
				blobs.remove(blob);
				int x = ((int)(Math.random()*1000))%640-320;
				int y = ((int)(Math.random()*1000))%240;
				blobs.add(new FogBlob(x, y, 0));
			}
			else{
				graphics.setColor(blob.getColor());
				graphics.fillRoundRect(blob.x, blob.y, 200, 30, 30, 30);
			}
		}
	}
}

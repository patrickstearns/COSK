package com.oblong.af.util;

import java.awt.*;

public class GameConstants {

	//directions
	public static enum Direction { NONE, UP, DOWN, LEFT, RIGHT };
	
	//size of play area
	public static final int PLAYFIELD_HEIGHT = 240;
	public static final int PLAYFIELD_WIDTH = 320;

	public static int dice(int numTimes, int numSides){
		int total = 0;
		for (int i = 0; i < numTimes; i++) total += (((Math.random()*10000)%numSides)+1);
		return total;
	}
	
	public static Direction getOppositeDirection(Direction direction){
		if (direction == Direction.UP) return Direction.DOWN;
		else if (direction == Direction.DOWN) return Direction.UP;
		else if (direction == Direction.LEFT) return Direction.RIGHT;
		else if (direction == Direction.RIGHT) return Direction.LEFT;
		return Direction.NONE;
	}
	
	public static int getDistanceBetween(Point p1, Point p2){
		int raw = Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
		return raw;
	}
	
}
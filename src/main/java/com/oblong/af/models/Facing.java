package com.oblong.af.models;

public enum Facing {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    ;

    public static Facing nextFacing(Facing f){
        switch (f) {
            case LEFT: return UP;
            case RIGHT: return DOWN;
            case UP: return RIGHT;
            case DOWN: return LEFT;
        }
        return UP;
    }

    /**
     * Return the facing closest to the given heading
     * @param heading current heading in radians
     * @return best Facing approximation of heading
     */
    public static Facing nearestFacing(double heading){
        double h = Math.toDegrees(heading);
        while (h < 0) h += 360;
        if (h <= 45) return Facing.RIGHT;
        else if (h <= 135) return Facing.UP;
        else if (h <= 225) return Facing.LEFT;
        else if (h <= 315) return Facing.DOWN;
        else return Facing.RIGHT;
    }
}

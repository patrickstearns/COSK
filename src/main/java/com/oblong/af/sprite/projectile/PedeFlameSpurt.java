package com.oblong.af.sprite.projectile;

import com.oblong.af.level.AreaScene;
import com.oblong.af.sprite.Prop;
import com.oblong.af.sprite.enemy.GigapedeSegment;

public class PedeFlameSpurt extends FlameSpurt {

	public PedeFlameSpurt(AreaScene scene, Prop parent, double heading, int timeToLive){
		super(scene, parent, heading, timeToLive);
		setX(parent.getX()+(float)(Math.cos(getHeading())*16));
		setY(parent.getY()-(float)(Math.sin(getHeading())*16-16));
        setSpeed(parent.getSpeed()*3);
	}

    protected float determineSpeed(){
        return getSpeed();
    }

    public boolean isCollidableWith(Prop prop){
        return !(prop instanceof GigapedeSegment) && super.isCollidableWith(prop);
    }
}

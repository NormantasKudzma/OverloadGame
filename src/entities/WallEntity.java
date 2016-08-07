package entities;

import org.jbox2d.dynamics.BodyType;

import com.ovl.physics.PhysicsBody;
import com.ovl.engine.GameObject;

public class WallEntity extends GameObject {
	
	@Override
	public void initEntity(PhysicsBody.EBodyType type) {
		super.initEntity(type);
		if (type == PhysicsBody.EBodyType.INTERACTIVE)
		{
			body.getBody().setType(BodyType.STATIC);
		}
	}
}

package client;

import java.util.ArrayList;

public class Vessel extends BasicObject {
	
	float xSpeed, ySpeed, speed;
	float throttle = 1;
	float maxSpeed = 1;
	float vertSpeed = 100;
	float weaponRange = 25;
	float weaponDamage = 20;
	float accuracy = 90;
	int weaponSpeed = 2;
	int fireTimer = weaponSpeed;
	float shieldStrength = 0.5f;
	float xDest = x;
	float yDest = y;
	float altDest = alt;
	float headingRad = 0;
	float heading = 0;
	float destDist;
	boolean fireOnTarget = false;
	boolean commandGiven = false;
	boolean targetDestroyed = false;
	boolean atDest = true;
	int ETA = 0;
	Vessel target;
	String view;
	
	public Vessel(float x, float y, float alt, String name){
		super(x, y, alt, name);
		type = "vessel";
		health = 100;
	}
	
	public void update(){
		ETA = calcETA(x, y, xDest, yDest, maxSpeed, throttle);
		move();
		fire();
		if(throttle > 1){throttle = 1;}
		if(throttle < 0){throttle = 0;}
		if(target != null) { if(target.toRemove = true){target = null;}}
		while(headingRad >= 360) headingRad -= 360;
		while(headingRad < 0) headingRad += 360;
		heading = (float)Math.toDegrees(headingRad);
		if(health <= 0){toRemove = true;}
		isAtDest();
		if(target == null){targetDestroyed = false;}
	}
	
	public void move(){
		float dist = calcDist(x, y, xDest, yDest);
		float xDist = xDest - x;
		float altDist = altDest - alt;
	
		if(dist != 0){
			headingRad = (float)calcAngleRad(xDist, dist);
			heading = (float)Math.toDegrees(headingRad);
			while(heading >= 360) heading -= 360;
			while(heading < 0) heading += 360;
			
			xSpeed = calcXIncrement();
			ySpeed = calcYIncrement();
			
			
			if(dist >= maxSpeed){
				x += xSpeed/throttle;
				y += ySpeed/throttle;
			}
			else if(dist <= calcDist(x, y, x + xSpeed/throttle, y + ySpeed/throttle)){
				x = xDest;
				y = yDest;
			}
			speed = (float) Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));
		}
		if(altDist != 0){
			if(altDist <= vertSpeed){
				alt = altDest;
			}
			else if(altDest < alt){
				alt -= 100;
			}
			else if(altDest > alt){
				alt += 100;
			}
		}
	}
	
	public void fire(){
		if(target != null){
			int oldHealth = target.health;
			if(calcDistToObject(this, target) <= weaponRange && fireTimer <= 0){
				if((rand.nextInt(100)+1) < accuracy){
					target.health -= weaponDamage;
				}
			fireTimer = weaponSpeed;
			}
			else if(fireTimer > 0){
				fireTimer --;
			}
			if(target.health <= 0){
				show(name + ": Target vessel \"" + target.name +"\" destroyed.");
				target = null;
				targetDestroyed = true;
			}else if(target.health != oldHealth){
				show(name + ": Target vessel \"" + target.name +"\" hit.");
				targetDestroyed = false;
			}
		}
	}

	public int calcETA(float x, float y, float xDest, float yDest, float maxSpeed, float throttle){
		return (int)Math.ceil(calcDist(x, y, xDest, yDest)/maxSpeed*throttle);
	}
	public void isAtDest(){
		if(x == xDest && y == yDest){
			atDest = true;
		}else{
			atDest = false;
		}
	}
	
	public ArrayList<BasicObject> scanObjects(ArrayList<BasicObject> queryList){
		ArrayList<BasicObject> result = new ArrayList<BasicObject>();
		for(BasicObject i : queryList){
			if(i != this){
				result.add(i);
			}
		}
		return result;
	}
	public ArrayList<Vessel> scanVessels(ArrayList<Vessel> queryList){
		ArrayList<Vessel> result = new ArrayList<Vessel>();
		for(Vessel i : queryList){
			if(i != this){
				result.add(i);
			}
		}
		return result;
	}
	public ArrayList<BasicObject> directionalScanObjects(ArrayList<BasicObject> queryList){
		ArrayList<BasicObject> result = new ArrayList<BasicObject>();
		for(BasicObject i : queryList){
			if(i != this){
				i.scanAngle = (float) Math.toDegrees(objectAngleFromHeading(this, i));
				result.add(i);
			}
		}
		return result;
	}
	
	public float calcXIncrement(){
		return (float)(maxSpeed*Math.cos(headingRad));
	}
	public float calcYIncrement(){
		return (float)(maxSpeed*Math.sin(headingRad));
	}
	public float calcDist(float x0, float y0, float x1, float y1){
		float xDist = x1 - x0;
		float yDist = y1 - y0;
		return (float) Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
	}
	public float calcAngleRad(float xDist, float dist){
		return (float) Math.acos(xDist/dist);
	}
	public float angleFromHeading(float heading, float dist, float xDist){
		if(dist == 0 || xDist == 0){
			return 0;
		}else{
			return calcAngleRad(xDist, dist) + heading;
		}
	}
	public float calcDistToObject(BasicObject i1, BasicObject i2){
		return calcDist(i1.x, i1.y, i2.x, i2.y);
	}
	public float objectAngleFromHeading(BasicObject i1, BasicObject i2){
		float i = angleFromHeading(headingRad, calcDistToObject(i1, i2), xDistToObject(i1, i2));
		return i;
	}
	public float xDistToObject(BasicObject i1, BasicObject i2){
		return i2.x - i1.x;
	}
}
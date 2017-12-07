package client;

public class BasicObject extends Main {
	int health;
	float armor = 1;
	float x;
	float y;
	float alt;
	boolean toRemove = false;
	float scanAngle = 0;
	String name;
	String type = "BasicObject";
	String info;
	
	public BasicObject(float x, float y, float alt, String name){
		this.x=x;
		this.y=y;
		this.name=name;
		All.add(this);
	}
	
	public void update(){
	}
}

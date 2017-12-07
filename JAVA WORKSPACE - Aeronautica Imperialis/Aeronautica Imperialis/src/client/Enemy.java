package client;

import java.util.ArrayList;

public class Enemy{

	public ArrayList<Vessel> aiVessels = new ArrayList<Vessel>();
	public ArrayList<Vessel> aiVesselsRemove = new ArrayList<Vessel>();
	Vessel select;
	
	public Enemy() {
		initVessels();
	}
	
	public void initVessels(){
		Vessel aiVessel1 = new Vessel(19, 20, 1000, "ai vessel 1");
		Vessel aiVessel2 = new Vessel(20, 19, 1000, "ai vessel 2");
		aiVessels.add(aiVessel1);
		aiVessels.add(aiVessel2);
		select = aiVessel1;
	}
	
	public void enemyUpdate(){
		for(Vessel v : aiVessels){
			if(v.toRemove == true){
				aiVesselsRemove.add(v);
			}
		}
		for(Vessel v : aiVesselsRemove){
			aiVessels.remove(v);
		}
		aiVesselsRemove.clear();
	}
}

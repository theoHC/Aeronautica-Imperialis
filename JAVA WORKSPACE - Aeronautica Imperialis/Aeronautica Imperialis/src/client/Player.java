package client;

import java.io.IOException;
import java.util.ArrayList;

public class Player{
	
	String goCommand = "go";
	String selectCommand = "select";
	String scanCommand = "scan";
	String scanDirectional = "directional";
	String scanCoordinate = "coordinate";
	String readoutCommand = "readout";
	String setDestCommand = "set dest";
	String setAltCommand = "set altit";
	String setThrotCommand = "set throt";
	String setHeadingCommand = "set heading";
	String defaultGoCommand = "set default go";
	String abortCommand = "abort";
	String infoCommand = "info";
	String commandSplit = " ";
	String coordSplit = ", ";
	
	ArrayList<Vessel> playerVessels = new ArrayList<Vessel>();
	ArrayList<Vessel> playerVesselsRemove = new ArrayList<Vessel>();
	Vessel select;

	int commenceTime = 10;
	String command;
	
	public Player() throws IOException {
		initVessels();
	}
	
	public void initVessels() throws IOException{
		ImperialShip1 playerVessel1 = new ImperialShip1(0, 0, 1000, "Photon");
		ImperialShip1 playerVessel2 = new ImperialShip1(0, 0, 1000, "Nayhall");
		playerVessels.add(playerVessel1);
		playerVessels.add(playerVessel2);
		select = playerVessel1;
	}
	
	public void playerUdate(){
		for(Vessel v : playerVessels){
			if(v.toRemove == true){
				playerVesselsRemove.add(v);
			}
		}
		for(Vessel v : playerVesselsRemove){
			playerVessels.remove(v);
		}
		playerVesselsRemove.clear();
	}
	
	public void vesselUpdate(){
		select.fireOnTarget = false;
		command = Main.query(basicStats() + "\nstanding by for orders (go, select, scan, set dest, set alt, set target, set throt, readout, set default go, abort).");
		command = command.toLowerCase();
		String[] commandArray = command.split(", ");
		if(commandArray[0].toLowerCase().equals("go")){
			boolean commence = true;
			for(Vessel v : playerVessels){
				if(!v.commandGiven && commence == true){
					String in = Main.query(v.name + ": No command has been given, please confirm order commence.").toLowerCase();
					if(in.equals("yes") || in.equals("go")){
						commence = true;
					}else{
						commence = false;
					}
			}
		}
			if(commence){
				Main.soundHandler.soundQueue.add(Main.scanOrder);
				int timeIncrement;
				Main.show("roger, commencing orders.");
				try {
					timeIncrement = Integer.parseInt(commandArray[1]);
				}catch(java.lang.ArrayIndexOutOfBoundsException e){
					timeIncrement = commenceTime;
				}
				while(timeIncrement > 0){
					Main.mainUpdate();
					timeIncrement--;
					for(Vessel v : playerVessels){
						if((v.atDest == true || v.targetDestroyed == true) && select.commandGiven == true){
							String in = Main.query(v.name + ": We have completed given commands, do you want to provide new orders.").toLowerCase();
							if(in.equals("yes")){
								timeIncrement = 0;
							}
						}
					}
				}
				basicStats();
			}
		}
		else if(command.toLowerCase().equals("select")){
				select = getVesselFromName(Main.query("roger, select vessel, " + getVesselNames()), playerVessels, select);
		}
		else if(command.toLowerCase().equals("fire")){
			if(select.target != null){
				select.commandGiven = true;
				Main.show(select.name + ": roger, opening fire on target");
				select.fireOnTarget = true;
			}
			else{
				Main.show(select.name + ": no valid target");
			}
		}
		if(commandArray[0].toLowerCase().equals("scan")){
			try {
				if(commandArray[1].equals("directional") || commandArray[1].equals("d")){
					Main.show(compileDirectionalScan());
				}else if(commandArray[1].equals("coordinate") || commandArray[1].equals("c")){
					Main.show(compileScan());
				}
			}catch(java.lang.ArrayIndexOutOfBoundsException e){
				Main.show(compileScan());
			}
		}
		else if(command.toLowerCase().equals("set dest")){
			setDest();
			select.commandGiven = true;
		}
		else if(command.toLowerCase().equals("set target")){
			select.target = getVesselFromName(Main.query(compileVesselScan() + "Please select target"), Main.enemy.aiVessels, select.target);
		}
		else if(command.toLowerCase().equals("set throt")){
			setThrottle();
		}
		else if(command.toLowerCase().equals("readout")){
			Main.show(select.name + ": roger, displaying readout \n" + readout());
		}
		else if(command.toLowerCase().equals("info")){
			if(select.info != null){
				Main.show(select.info);
			}
		}
		else if(command.toLowerCase().equals("set default go")){
			commenceTime = Integer.parseInt(Main.query("Please set default time to commence orders"));
		}
		else if (command.toLowerCase().equals("settings")){
			settings();
		}
		else if(command.toLowerCase().equals("abort")){
			Main.show("roger, abort. Disengaging.");
			Main.playing = false;
			return;
		}
		else if(command.equals("")){
			Main.playing = false;
			return;
		}
		else{
			Main.show(select.name + ": received");
		}
	}
	
	public Vessel getVesselFromName(String in, ArrayList<Vessel> queryList, Vessel original){
		Vessel result = original;
		for(Vessel i : queryList){
			if(in.toLowerCase().equals(i.name.toLowerCase())){
				result = i;
			}
		}
		if(result == original){
			Main.show("Invalid input");
		}
		return result;
	}
	
	public ArrayList<String> getVesselNames(){
		ArrayList<String> names = new ArrayList<String>();
		for(Vessel i : playerVessels){
			names.add(i.name);
		}
		return names;
	}
	
	public void setDest(){
		String Dest = Main.query(select.name + ": roger, standing by for destination coordinates (x, y).");
		String driveMode = "plasma";
		String[] destCoords = Dest.split(", ");
		try {
			select.xDest = Float.parseFloat(destCoords[0]);
			select.yDest = Float.parseFloat(destCoords[1]);
		} catch (NumberFormatException e) {
			Main.show("invalid coordinates");
			setDest();
		} catch (java.lang.ArrayIndexOutOfBoundsException d){
			Main.show("invalid coordinates");
			setDest();
		}
		select.destDist = select.calcDist(select.x, select.y, select.xDest, select.yDest);
		select.ETA = select.calcETA(select.x, select.y, select.xDest, select.yDest, select.maxSpeed, select.throttle);
		Main.show(select.name + ": coordinates received, setting destination to " 
		+ select.xDest + ", " + select.yDest + " with " + driveMode + " drive engaged. ETA: "
		+ select.ETA + " seconds");
		select.isAtDest();
	}
	
	public void setThrottle(){
		String throttleIn = Main.query(select.name + ": roger, standing by for throttle settings (number between 0 and 1))");
		try {
			select.throttle = Float.parseFloat(throttleIn);
		} catch (NumberFormatException e) {
			Main.show("invalid throttle settings");
			setThrottle();
		}
		if(select.throttle > 1){
			select.throttle = 1;
		}
		if(select.throttle < 0){
			select.throttle = 1;
		}
		Main.show(select.name + ": throttle settings received, setting throttle to " + select.throttle*100 + "%");
	}
	public void setHeading(){

		String headingIn = Main.query(select.name + ": roger, standing by for heading (number between 0 and 360))");
		try {
			select.headingRad = Float.parseFloat(headingIn);
		} catch (NumberFormatException e) {
			Main.show("invalid heading");
			setHeading();
		}
		if(select.headingRad >= 360){
			select.headingRad = 0;
		}
		if(select.headingRad < 0){
			select.headingRad = 360;
		}
		Main.show(select.name + ": heading received, setting heading to " + select.headingRad + " degrees");
	}
	
	public void stats(){
		Main.show("Vessel: " + select.name);
		Main.show("MET: " + Main.elapsedTime + " seconds");
		Main.show("Health: " + select.health + "%");
		Main.show("Position: (" + select.x + ", " + select.y + ")");
		Main.show("Destination: (" + select.xDest + ", " + select.yDest + ")");
	}
	
	public void settings(){
		String settingIn = Main.query("EDIT SETTINGS \n Sound: " + Main.sound + "\n");
		if(settingIn.toLowerCase().equals("sound")){
			String soundIn = Main.query("SET SOUND PLAYING: TRUE/FALSE");
			if(soundIn.toLowerCase().equals("true")){
				Main.sound = true;
			}else if(soundIn.toLowerCase().equals("false")){
				Main.sound = false;
				Main.show("Sound will end after current loop.");
			}
		}
	}
	
	public String compileScan(){
		String result = select.name + ": " + "Roger, displaying scan results \n";
		for(BasicObject i : select.scanObjects(Main.All)){
			result += ("'" + i.type + "' class \"" + i.name + "\" at " + i.x + ", " + i.y + " (" + select.calcDistToObject(select, select.target) + 
			 " kilometers away) with an estimated "
			+ i.health + " structural integrety\n");
		}
		return result;
	}
	public String compileDirectionalScan(){
		String result = select.name + ": " + "Roger, displaying scan results \n";
		for(BasicObject i : select.directionalScanObjects(Main.All)){
			result += ("'" + i.type + "' class \"" + i.name + "\" at " + i.scanAngle + " degrees from heading\n");
	}
		return result;
	}
	public String compileVesselScan(){
		String result = select.name + ": " + "Roger, displaying scan results \n";
		for(BasicObject i : select.scanVessels(Main.enemy.aiVessels)){
			result += ("'" + i.type + "' class \"" + i.name + "\" at (" + i.x + ", " + i.y + ")" + "\n");
		}
		return result;
	}
	
	public String basicStats(){
		String result = "MET: " + Main.elapsedTime + " minutes \n"
		+ "Vessel: \"" + select.name + "\"\n"
		+ "Airframe Integrity: " + select.health + "\n"
		+ "Position: (" + select.x + ", " + select.y + ")\n"
		+ "Altitude: " + select.alt + " metres\n"
		+ "Destination: " + select.xDest + ", " + select.yDest + ", " +  select.altDest + "\n"
		+ "throttle: " + select.throttle*100 + "%\n"
		+ "Heading: " + select.heading + " degrees north\n"
		+ "Speed: " + select.speed + " km/s\n";
		if(select.target != null){
			result += "Target: '" + select.target.type + "' class \"" + select.target.name +
			" \" at (" + select.target.x + ", " + select.target.y + ")\n";
		}else{
			result += "Target: none \n";
		}
		result += "Available vessels: ";
		for(Vessel v : playerVessels){
			if(v == this.select){
				result += ("'" + v.type + "' class \"" + v.name + "\" (current), ");
			}else{
				result += ("'" + v.type + "' class \"" + v.name + "\", ");
			}
		}
		result = result.substring(0, result.length() - 2);;
		return result;
	}
	
	public String readout(){
		String result = "MET: " + Main.elapsedTime + " seconds \n"
		+ "Vessel: \"" + select.name + "\"\n"
		+ "Airframe Integrity: " + select.health + "\n"
		+ "Position: (" + select.x + ", " + select.y + ")\n"
		+ "Altitude: " + select.alt + " metres\n"
		+ "Destination: " + select.xDest + ", " + select.yDest + ", " +  select.altDest + "\n"
		+ "Distance to destination: " + select.destDist + " kilometers \n"
		+ "Estimated time to destination: " + select.ETA + " seconds \n"
		+ "throttle: " + select.throttle*100 + "%\n"
		+ "Heading: " + select.heading + " degrees\n"
		+ "Speed: " + select.speed + " km/s\n"
		+ "Weapon Accuracy: " + select.accuracy + "% \n"
		+ "Weapon Range: " + select.weaponRange + " kilometers \n"
		+ "Estimated damage of succsesful hit: " + select.weaponDamage + " \n"
		+ "Estimated time untill next volley: " + select.fireTimer + " seconds \n"
		+ "Valid command has been given: " + select.commandGiven + "\n";
		if(select.target != null){
			result += "Target: '" + select.target.type + "' class \"" + select.target.name 
			+ " \" at (" + select.target.x + ", " + select.target.y + ")\n"
			+ "Open fire order given: " + select.fireOnTarget;
		}else{
			result += "Target: none \n";
		}
		return result;
			
	}
}
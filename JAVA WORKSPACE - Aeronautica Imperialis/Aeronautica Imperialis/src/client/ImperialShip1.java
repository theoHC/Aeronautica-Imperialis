package client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImperialShip1 extends Vessel {
	
	public ImperialShip1(float x, float y, float alt, String name) throws IOException {
		super(x, y, alt, name);
		maxSpeed = 1;
		weaponRange = 50;
		weaponDamage = 20;
		accuracy = 90;
		weaponSpeed = 5;
		health = 100;
		type = "Tempest";
	}
}

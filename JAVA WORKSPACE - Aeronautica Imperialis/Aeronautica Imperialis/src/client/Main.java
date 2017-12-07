package client;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Main {

	static int elapsedTime = 0;
	static boolean playing = true;
	static String gameName = "Empire Aerial";
	static boolean sound = false;
	
	static ArrayList<BasicObject> All = new ArrayList<BasicObject>();
	static ArrayList<BasicObject> AllRemove = new ArrayList<BasicObject>();
	static Scanner scan;
	static Random rand;
	static Enemy enemy;
	static Player player;
	static ShipView shipView;
	static SoundHandler soundHandler;
	static Font monospace;
	static File background = new File("assets/SCBackground.wav");
	static File intro = new File("assets/SCOpening.wav");
	static File scanOrder = new File("assets/SCScanOrder.wav");
	static String StarAllianceBattleship;
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException{
		//StarAllianceBattleship = new String(Files.readAllBytes(Paths.get("assets/StarAllianceBattleship.txt")));
		
		scan = new Scanner(System.in);
		rand = new Random();
		monospace = new Font(Font.MONOSPACED, Font.PLAIN, 10);
		
		player = new Player();
		enemy = new Enemy();
		soundHandler = new SoundHandler();
		//shipView = new ShipView();
		//shipView.start();
		
		while(playing){
			player.vesselUpdate();
		}
		
		scan.close();
	}
	
	public static void mainUpdate(){
		player.playerUdate();
		enemy.enemyUpdate();
		for(BasicObject i : All){
			((BasicObject) i).update();
			if(i.toRemove == true){
				AllRemove.add(i);
			}
		}
		for(BasicObject i : AllRemove){
			All.remove(i);
		}
		AllRemove.clear();
		
		incrementTime();
	}
	
	public static void show(Object x){
		JOptionPane.showMessageDialog(null, x, gameName, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showFormatedText(String s, Font f, JTextArea t, String header){
		t = new JTextArea(s);
		t.setFont(f);
		t.setOpaque(false);
		JOptionPane.showMessageDialog(null, t, header, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void print(Object x){
		System.out.println(x);
	}
	
	public static String query(Object x){
		return JOptionPane.showInputDialog(null, x, gameName, JOptionPane.INFORMATION_MESSAGE);
	}
	public static String consoleIn(){
		String in = scan.nextLine();
		return in;
	}
	
	public static void incrementTime(){
		elapsedTime++;
	}
	
	public static float sign(float x){
		if(x == 0){
			return 0;
		}
		else{
			return Math.abs(x)/x;
		}
	}
	public static void test(){
		print("test");
	}
}

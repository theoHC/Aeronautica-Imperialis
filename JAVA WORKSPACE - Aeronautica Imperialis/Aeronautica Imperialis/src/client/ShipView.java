package client;

import javax.swing.JTextArea;

public class ShipView extends Thread {
	
	static JTextArea shipViewArea;
	
	public ShipView(){}

	public void run() {
		if(Main.player.select.view != null){
			Main.showFormatedText(Main.player.select.view, Main.monospace, shipViewArea, Main.gameName + ": F.S.S." + Main.player.select.name);
		}
	}
}
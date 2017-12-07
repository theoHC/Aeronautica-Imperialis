package client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundHandler extends Main
{	
	ArrayList<File> soundQueue = new ArrayList<File>();
	String threadName = "soundHandler";
	Thread t;
    Clip clip;
    LineListener listener;
    static boolean soundPlaying;
	
	public SoundHandler() throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException {
		soundQueue.add(intro);
		updateSounds();
	}
	
	public void updateSounds() throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException{
		if(sound){
			if(!soundPlaying && !soundQueue.isEmpty()){
				playSound(soundQueue.get(0));
				soundQueue.remove(0);
			}else if(!soundPlaying && soundQueue.isEmpty()){
				playSound(background);
				if(rand.nextInt(10) > 2){
					soundQueue.add(Main.scanOrder);
				}
			}
		}
	}

	public void playSound(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException{
	    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
	    AudioFormat format = audioStream.getFormat();
	    DataLine.Info info = new DataLine.Info(Clip.class, format);
	    clip = (Clip) AudioSystem.getLine(info);
	    clip.open(audioStream);
	    listener = new LineListener() {
	        public void update(LineEvent event) {
	        	if (event.getType().equals(LineEvent.Type.STOP)) {
	        		soundPlaying = false;
	        		try {
	    				updateSounds();
	    			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException | InterruptedException e) {
	    				e.printStackTrace();
	    			}
                }
	        }
	    };
	    clip.addLineListener(listener);
	    clip.start();
	    soundPlaying = true;
	}
}

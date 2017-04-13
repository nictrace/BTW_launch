package net.launcher;
import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.player.Player;
public class MusPlay {
	private Player player;
  
	public MusPlay(String filename)	{
		play(filename);
	}

	public void close(){ if (player != null) player.close(); }
  
	public void play(String filename)  {
		try {
			InputStream is = getClass().getResourceAsStream(filename);
			@SuppressWarnings("unused")
			BufferedInputStream bis = new BufferedInputStream(is);
			player = new Player(is);
		}
		catch (Exception e) {
			System.out.println("Problem playing file " + filename);
			System.out.println(e);
		}

		new Thread() {
			@Override
			public void run() {
				try { player.play(); }
				catch (Exception e) { System.out.println(e); }
			}
		}.start();
	}
}

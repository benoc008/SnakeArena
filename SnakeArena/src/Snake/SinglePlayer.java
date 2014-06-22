package Snake;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/**
 * Az osztály az egyjátékos mód kezeléséért felelős. Konstruktorban betölti a beállításokat és létrehoz egy Core objektumot, amihez hozzá is ad egy kígyót, majd létrehozza a játék f
 * @author Demkó Bence LWMEHK
 *
 */
public class SinglePlayer extends Thread {

	private Decider play;
	private Core s1;
	private JFrame f;
	private Thread t;
	private PlayerSetup player;
	private GameSetup game;

	private SnakeArea s;

	public SinglePlayer() {
		super();
		this.player = new PlayerSetup();
		this.game = new GameSetup();

		try {
			File pl = new File("PlayerSetup.snk");
			if (pl.exists())
				player.load();
			File gl = new File("GameSetup.snk");
			if (gl.exists())
				game.load();
		} catch (Exception e) {
			System.out.println(e);
		}

		play = new Decider();
		play.setValue(true);
		s1 = new Core(game.getWidth(), game.getHeight(), game.getFieldSize(),
				game.getMapPath(), game.getSpeed(), play, game.isPalinka(),
				game.getDeathMode());
		Thread gameThread = new Thread(s1);
		gameThread.start();

		s1.addSnake(player.getName(), player.getColor(), 0);
		s = new SnakeArea(game.getWidth(), game.getHeight(),
				game.getFieldSize(), game.getSpeed(), new ArrayList<Snake>(),
				new ArrayList<Apple>(), new ArrayList<Point>(), 0, play,
				game.isPalinka());

		f = new JFrame();
		f.setContentPane(s);
		f.pack();
		f.setTitle("Snake");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
		t = new Thread(s);
		t.start();

	}
	
	/**
	 * Szinkorinizáció miatt klónozza a Core-tól kapott értékeket, majd úgy adja meg a kirajzoló szálnak. 
	 */
	public void run() {
		s.setBricks(s1.getBricks());
		List<Snake> cloneSnake = null;
		List<Apple> cloneApple = null;
		while (play.getValue()) {

			synchronized (SinglePlayer.class) {
				s1.getSnakes().get(0).setDir(s.getDir());
				cloneSnake = new ArrayList<Snake>(s1.getSnakes().size());
				for (Snake item : s1.getSnakes())
					cloneSnake.add(item.clone());
				cloneApple = new ArrayList<Apple>(s1.getApples().size());
				for (Apple item : s1.getApples())
					cloneApple.add(item.clone());

				s.setSnakes(cloneSnake);
				s.setApples(cloneApple);
			}

			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		f.dispose();
	}

}

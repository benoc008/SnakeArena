package Snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter; 
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A pálya kirajzolásáért felelős osztály.
 * 
 * @author Demkó Bence LWMEHK
 *  */
public class SnakeArea extends JPanel implements KeyListener, Runnable {

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private int fieldSize;
	private List<Snake> snakes;
	private List<Apple> apples;
	private List<Point> bricks;
	private Direction direction;
	private int id;
	private Decider play;
	private boolean scoreVisible;
	private boolean gridVisible;
	private boolean palinka;
	private String message;
	private int messageTimer;

	private boolean rajzol;

	public SnakeArea(int width, int height, int fieldSize, int speed,
			List<Snake> snakes, List<Apple> apples, List<Point> bricks, int id,
			Decider play, boolean palinka) {

		this.width = width;
		this.height = height;
		this.fieldSize = fieldSize;
		this.snakes = snakes;
		this.apples = apples;
		this.bricks = bricks;
		this.id = id;
		this.play = play;
		this.direction = Direction.NORTH;
		this.scoreVisible = true;
		this.gridVisible = true;
		this.palinka = palinka;
		this.message = null;
		this.messageTimer = 0;

		rajzol = false;

		this.addKeyListener(this);
		this.setFocusable(true);

		setBackground(new Color(55, 200, 100));
		setPreferredSize(new Dimension(width, height));
	}

	/**
	 * A függvény szinkronizálva meghívja a kirajzoló függvényeket. Erre azért
	 * van szükség, mert közbe más szálak módosíthatnák a listák és elemek
	 * tartalmát.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		synchronized (this) {
			try {
				while (rajzol)
					wait();
				rajzol = true;
				drawGrid(g);
				drawApple(g);
				drawSnake(g);
				drawBricks(g);
				drawScore(g);
				drawMessage(g);
				rajzol = false;
				notify();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}

	/**
	 * A függvény rácsot rajzol a képernyőre. A G gomb megnyomásával
	 * eltűntethető.
	 * 
	 * @param g
	 */
	public void drawGrid(Graphics g) {
		if (gridVisible) {
			g.setColor(Color.BLACK);
			for (int i = 0; i <= width; i += fieldSize)
				g.drawLine(i, 0, i, height);

			for (int i = 0; i <= height; i += fieldSize)
				g.drawLine(0, i, width, i);
		}
	}

	/**
	 * A függvény az összes kígyót felrajzolja a képernyőre.
	 * 
	 * @param g
	 */
	public void drawSnake(Graphics g) {
		for (Snake s : snakes) {
			g.setColor(s.getColor());
			for (Point i : s) {
				g.fillRect(i.x * fieldSize, i.y * fieldSize, fieldSize,
						fieldSize);
			}
		}
	}

	/**
	 * A függvény felrajzolja az almákat a képernyőre. Ha valamelyik már
	 * romlott, akkor sárgára színezi.
	 * 
	 * @param g
	 */
	public void drawApple(Graphics g) {
		for (Apple a : apples) {
			if ((a.getAge() < 20) || !palinka)
				g.setColor(Color.RED);
			else
				g.setColor(Color.YELLOW);
			g.fillOval(a.getPoint().x * fieldSize, a.getPoint().y * fieldSize,
					fieldSize, fieldSize);
		}
	}

	/**
	 * A függvény a téglákat rajzolja a képernyőre.
	 * 
	 * @param g
	 */
	public void drawBricks(Graphics g) {
		g.setColor(new Color(200, 200, 0));
		for (Point p : bricks) {
			g.fillRect(p.x * fieldSize, p.y * fieldSize, fieldSize, fieldSize);
		}
	}

	/**
	 * A függvény a játékosok pontjait rajzolja a bal felső sarokba. A D gombbal
	 * eltűntethető.
	 * 
	 * @param g
	 */
	public void drawScore(Graphics g) {
		if (scoreVisible) {
			Font font = new Font("Serif", Font.PLAIN, 26);
			g.setFont(font);
			int x = 10;
			int y = 30;
			for (Snake s : snakes) {
				g.setColor(s.getColor());
				g.drawString(s.getName() + ": " + s.getPoint(), x, y);
				y += 20;
			}
		}
	}

	/**
	 * A függvény a szervertől érkező üzenetet rajzolja a képernyő közepére.
	 * Csak multiplayer módban van jelentősége.
	 * 
	 * @param g
	 */
	public void drawMessage(Graphics g) {
		if (messageTimer > 0) {
			messageTimer -= 50;
			Font font = new Font("Serif", Font.PLAIN, 26);
			g.setFont(font);
			g.setColor(Color.BLACK);
			int stringLen = (int) g.getFontMetrics()
					.getStringBounds(message, g).getWidth();
			int start = width / 2 - stringLen / 2;
			g.drawString(message, start, height / 2 - 13);

		}
	}

	/**
	 * Kiválasztja a kígyók közül a játékos saját kígyójának referenciáját, az
	 * ID alapján.
	 * 
	 * @return
	 */
	public Snake getMine() {
		Snake mine = null;
		for (Snake s : snakes) {
			if (s.getID() == id) {
				mine = s;
				break;
			}
		}
		return mine;
	}

	/**
	 * A játék végén, vagy megszakításakor hívódik meg, elmenti az eredményt a
	 * megfelelő fileba, és kiad egy üzenetet, ami erről értesít.
	 */
	public void saveScore() {
		File file = new File("scores.snk");
		Snake mine = getMine();
		if (!file.exists()) {

		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file, true)));
			out.println(mine.getName() + " " + mine.getPoint());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(this,
				"Game over. You can check your position at the scores menu.");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Snake mine = getMine();
		if (mine == null)
			return;
		Direction dir = mine.getLastDir();
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (!(dir == Direction.NORTH || dir == Direction.SOUTH)) {
				direction = Direction.NORTH;
			}
			break;
		case KeyEvent.VK_DOWN:
			if (!(dir == Direction.NORTH || dir == Direction.SOUTH)) {
				direction = Direction.SOUTH;
			}
			break;
		case KeyEvent.VK_LEFT:
			if (!(dir == Direction.EAST || dir == Direction.WEST)) {
				direction = Direction.WEST;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (!(dir == Direction.EAST || dir == Direction.WEST)) {
				direction = Direction.EAST;
			}
			break;
		case KeyEvent.VK_ESCAPE:
			play.setValue(false);
			break;
		case KeyEvent.VK_D:
			scoreVisible = !scoreVisible;
			break;
		case KeyEvent.VK_G:
			gridVisible = !gridVisible;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	public void run() {
		while (play.getValue()) {
			Snake mine = getMine();
			if (mine != null && (mine.isDead() || mine.isDrunk())) {
				direction = mine.getLastDir();
				System.out.println(direction);
			} else if (mine != null && mine.isVeryDead()) {
				play.setValue(false);
			}
			repaint();
			try {
				Thread.currentThread();
				Thread.sleep(50); // konstans 20 fps.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		saveScore();
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}

	public synchronized void setSnakes(List<Snake> snakes) {
		try {
			while (rajzol)
				wait();
			rajzol = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.snakes = snakes;
		rajzol = false;
		notify();
	}

	public synchronized void setApples(List<Apple> apples) {
		try {
			while (rajzol)
				wait();
			rajzol = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.apples = apples;
		rajzol = false;
		notify();
	}

	public synchronized void setBricks(List<Point> bricks) {
		try {
			while (rajzol)
				wait();
			rajzol = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.bricks = bricks;
		rajzol = false;
		notify();
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPlay(Decider play) {
		this.play = play;
	}

	public Decider getPlay() {
		return this.play;
	}

	public Direction getDir() {
		return direction;
	}

	public void setDir(Direction dir) {
		this.direction = dir;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessageTimer(int messageTimer) {
		this.messageTimer = messageTimer;
	}

	public void setPalinka(boolean palinka) {
		this.palinka = palinka;
	}
}
package Snake;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A játék fő funkcióit megvalósító osztály. Ez ütemezi a kígyók mozgását,
 * helyzetét, almák állapotát és egyéb beállításokat mind egyjátékos mind
 * multiplayer módban.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */

public class Core implements Serializable, Cloneable, Runnable {

	private static final long serialVersionUID = -1359450405191700690L;
	private int width;
	private int height;
	private int fieldSize;
	private List<Snake> snakes;
	private List<Apple> apples;
	private List<Point> bricks;
	private Decider play;
	private String mapPath;
	private int speed;
	private boolean palinka;
	private int cnt; // <-- alma ido leptetes
	private boolean sync;
	private DeathMode deathMode;
	private String message;
	private int messageTimer;

	public Core(int width, int height, int fieldSize, String mapPath,
			int speed, Decider play, boolean palinka, DeathMode dm) {
		this.width = width;
		this.height = height;
		this.fieldSize = fieldSize;
		this.mapPath = mapPath;
		snakes = new ArrayList<Snake>();
		apples = new ArrayList<Apple>();
		bricks = new ArrayList<Point>();
		this.play = play;
		this.speed = speed;
		this.palinka = palinka;
		this.cnt = 0;
		sync = false;
		this.deathMode = dm;
		this.message = null;
		this.messageTimer = 0;

		loadBricks();
	}

	/**
	 * Létrehoz egy új kígyót, és hozzáadja a játék kígyókat tartalmazó
	 * listájához.
	 * 
	 * @param name
	 *            Játékos neve
	 * @param color
	 *            Játékos által választott szín
	 * @param id
	 *            Játékost azonosító szám
	 * @return Visszatér a hozzáadott kígyóval
	 */
	public Snake addSnake(String name, Color color, int id) {
		Snake s = new Snake(name, color, id);
		snakes.add(s);
		return s;
	}

	/**
	 * A függvény ellenörzi, hogy az adott ponton van-e valami. Először megnézi,
	 * hogy tégla, majd, hogy másik kígyó áll-e ott.
	 * 
	 * @param field
	 *            Ellenőrizni kívánt mező
	 * @return Igaz, ha szabad, hamis, ha foglalt a mező.
	 */
	public boolean isFree(Point field) {
		boolean enable = true;

		for (Point p : bricks) { // tÃ©glÃ¡k ellenÃ¶rzÃ©se
			if (p.equals(field)) {
				enable = false;
				break;
			}
		}
		for (Snake ss : snakes) {
			for (Point p : ss) { // tÃ¶bbi kÃ­gyÃ³ ellenÃ¶rzÃ©se (Ã¶nmaga is)
				if (p.equals(field)) {
					enable = false;
					break;
				}
			}
		}
		return enable;
	}

	/**
	 * A játékban lévő kígyók mozgatásáért felelős osztály. Sorban megnézi az
	 * összes kígyó aktuális pozícióját, valamint, hogy merre szeretne menni.
	 * Megállapítja, hogy szabad-e az út, és ha bármi áll előtte, a megfelelő
	 * lépéseket hozza. Ha alma, akkor megeszi, és növeli a kígyót, ha fal,
	 * akkor meghívja a kígyó halál függvényét, különben csak lép.
	 */
	public void move() {
		for (Snake s : snakes) {
			s.setLastDir(s.getDir());
			Point field = s.nextField();
			Apple alma = null;
			for (Apple a : apples) {
				if (a.getPoint().equals(field)) {
					alma = a;
					apples.remove(a);
					break;
				}
			}
			if (field.x < 0 || field.x > width / fieldSize - 1 || field.y < 0
					|| field.y > height / fieldSize - 1) {
				s.die(deathMode);
			} else if (alma != null) {
				s.grow();
				s.addPoint(10); // Everything counts 10 points
				if (palinka) {
					if (alma.getAge() > 20) // 15 Apple rotting time
						s.drunk();
				}
			} else if (isFree(field)) {
				s.move();
			} else {
				s.die(deathMode);
			}
		}
	}

	/**
	 * Az almák korosodását szabályozza, és ha eljött az idő, új almákat helyez
	 * el a pályán.
	 */
	public void appleSpawner() {
		for (Apple a : apples)
			// Apple aging
			a.time();
		if (palinka) {
			if (cnt == 15) { // apple spawn time
				cnt = 0;
				spawnApples();
			} else {
				cnt++;
			}
		} else if (apples.size() == 0) {
			spawnApples();
		}
	}

	/**
	 * Elindítás után a run függvény lépteti a kígyókat, és kezeli az almákat,
	 * természetesen szinkronizálva, mivel például a kígyók iránya lépés közben
	 * megváltozhat. A játék sebességét is itt szabályozza.
	 */
	public void run() {
		while (play.getValue()) {
			try {
				synchronized (this) {
					while (sync)
						wait();
					sync = true;
					move(); // move the snakes
					appleSpawner();
					sync = false;
					notify();
				}
				Thread.currentThread();
				Thread.sleep((speed > 950) ? 50 : 1000 - speed);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Clone függvény, szinkronizáltan. Azért kell, mert az adatok küldése
	 * közben a sok adat valamelyike könnyen változhat.
	 */
	@Override
	public Core clone() {
		synchronized (this) {
			while (sync) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sync = true;

			List<Snake> cloneSnake = null;
			List<Apple> cloneApple = null;
			List<Point> cloneBricks = null;

			cloneSnake = new ArrayList<Snake>(snakes.size());
			for (Snake item : snakes)
				cloneSnake.add(item.clone());
			cloneApple = new ArrayList<Apple>(apples.size());
			for (Apple item : apples)
				cloneApple.add(item.clone());
			cloneBricks = new ArrayList<Point>(bricks.size());
			for (Point i : bricks)
				cloneBricks.add((Point) (i.clone()));

			Core copy = new Core(width, height, fieldSize, mapPath, speed,
					play, palinka, deathMode);
			copy.setApples(cloneApple);
			copy.setSnakes(cloneSnake);
			copy.setBricks(cloneBricks);

			copy.setMessage(message);
			copy.setMessageTimer(messageTimer);

			sync = false;
			notify();

			return copy;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getSpeed() {
		return speed;
	}

	public int getHeight() {
		return height;
	}

	public int getFieldSize() {
		return fieldSize;
	}

	public List<Snake> getSnakes() {
		return snakes;
	}

	public List<Apple> getApples() {
		return apples;
	}

	public List<Point> getBricks() {
		return bricks;
	}

	public boolean getPlay() {
		return play.getValue();
	}

	public boolean isPalinka() {
		return palinka;
	}

	public void setSnakes(List<Snake> snakes) {
		this.snakes = snakes;
	}

	public void setApples(List<Apple> apples) {
		this.apples = apples;
	}

	public void setBricks(List<Point> bricks) {
		this.bricks = bricks;
	}

	/**
	 * Almák véletlenszerű elhelyezése a pályán.
	 */
	public void spawnApples() {
		Point p = new Point();
		while (true) {
			p.x = (int) (Math.random() * (width / fieldSize));
			p.y = (int) (Math.random() * (height / fieldSize));
			if (isFree(p))
				break;
		}
		apples.add(new Apple(p));
	}

	/**
	 * A téglák koordinátáit beolvassa a megfelelő fileból, és hozzáadja a
	 * listához.
	 */
	public void loadBricks() {
		if (mapPath != null)
			try {
				String line;
				String[] token;
				BufferedReader br = new BufferedReader(new FileReader(mapPath));
				line = br.readLine();
				while (true) {
					line = br.readLine();
					if (line == null)
						break;
					token = line.split(" ");
					bricks.add(new Point(Integer.parseInt(token[0]), Integer
							.parseInt(token[1])));
				}
				br.close();
			} catch (Exception e) {
				System.out.println(e);
			}
	}

	public String getMapPath() {
		return mapPath;
	}

	public DeathMode getDeathMode() {
		return deathMode;
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

	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setPalinka(boolean palinka) {
		this.palinka = palinka;
	}

	public void setDeathMode(DeathMode deathMode) {
		this.deathMode = deathMode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessageTimer(int messageTimer) {
		this.messageTimer = messageTimer;
	}

	public String getMessage() {
		return message;
	}

	public int getMessageTimer() {
		return messageTimer;
	}
}
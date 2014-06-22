package Snake;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A kígyókat reprezentáló osztály. A kígyók önállóan döntésképtelenek, a Core
 * osztály irányítja őket, viszont a megvalósítás az ő függvényeiben van. A két
 * irányért felelős változó azért kell, mert így ellenörzi, hogy két lépés
 * között történt-e két irányváltás, ezzel illegális lépést eredményezve.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class Snake implements Iterable<Point>, Serializable, Cloneable {

	private static final long serialVersionUID = 4847085408666436934L;
	private List<Point> snake;
	private String name;
	private Color color;
	private Direction dir;
	private Direction lastDir;
	private int id;
	private int point;
	private boolean dead;
	private boolean veryDead;
	private boolean drunk;

	public Snake(String name, Color color, int id) {
		snake = new LinkedList<Point>();
		dir = Direction.NORTH;
		lastDir = Direction.NORTH;

		this.name = name;
		this.color = color;
		this.id = id;
		this.point = 0;
		this.dead = false;
		this.drunk = false;
		this.veryDead = false;

		spawnSnake();
	}

	/**
	 * Mozgatja a kígyót, a megfelelő irányba.
	 */
	public void move() {
		if (drunk)
			drunk = false;
		else if (dead)
			dead = false;

		snake.remove(0);
		snake.add(nextField());
		// System.out.println("move " + snake.get(snake.size() - 1).x + " "
		// + snake.get(snake.size() - 1).y + " " + lastDir);
	}

	/**
	 * Növeli a kígyót 1 egységgel.
	 */
	public void grow() {
		snake.add(nextField());
	}

	/**
	 * Az ittas kígyó irányát és a pontjait tartalmazó listát fordítja meg.
	 */
	public void drunk() {
		if (snake.get(0).x > snake.get(1).x) {
			dir = Direction.EAST;
		} else if (snake.get(0).x < snake.get(1).x) {
			dir = Direction.WEST;
		} else if (snake.get(0).y > snake.get(1).y) {
			dir = Direction.SOUTH;
		} else if (snake.get(0).y < snake.get(1).y) {
			dir = Direction.NORTH;
		}
		Collections.reverse(snake);

		lastDir = dir;
		drunk = true;
		System.out.println("drunk");
	}

	/**
	 * Visszatér a kígyó feje és iránya függvényében a következő mezőmezővel.
	 * 
	 * @return következő mező
	 */
	public Point nextField() {
		int x = snake.get(snake.size() - 1).x;
		int y = snake.get(snake.size() - 1).y;
		if (lastDir == Direction.NORTH)
			return new Point(x, y - 1);
		else if (lastDir == Direction.SOUTH)
			return new Point(x, y + 1);
		else if (lastDir == Direction.EAST)
			return new Point(x + 1, y);
		else
			return new Point(x - 1, y);
	}

	/**
	 * Kígyó klónozó
	 */
	public Snake clone() {
		Snake copy = new Snake(this.name, this.color, this.id);
		List<Point> copyPoints = new LinkedList<Point>();
		for (Point i : snake)
			copyPoints.add((Point) (i.clone()));
		copy.setSnake(copyPoints);
		copy.setDir(this.dir);
		copy.setLastDir(this.lastDir);
		copy.setPoint(this.point);
		copy.setDead(this.dead);
		copy.setVeryDead(this.veryDead);
		copy.setDrunk(this.drunk);
		return copy;
	}

	public Color getColor() {
		return color;
	}

	/**
	 * A kezdőhelyre tesz egy kígyót.
	 */
	public void spawnSnake() {
		dir = Direction.NORTH;
		lastDir = Direction.NORTH;
		snake.add(new Point(1, 12));
		snake.add(new Point(1, 11));
		snake.add(new Point(1, 10));
	}

	/**
	 * A beállított halálmód függvényében állít a kígyón, esetleg reseteli azt,
	 * vagy véget vet a játéknak.
	 * 
	 * @param dm
	 *            a halálmód
	 */
	public void die(DeathMode dm) {
		switch (dm) {
		case Respawn:
			dead = true;
			snake.removeAll(snake);
			point = 0;
			spawnSnake();
			break;
		case LosePoints:
			if (point > -10) {
				snake.remove(snake.size() - 1);
				point -= 10;
			}
			break;
		case EndGame:
			veryDead = true;
			break;
		case Immortal:
			break;
		}
	}

	public Iterator<Point> iterator() {
		return snake.iterator();
	}

	public int getID() {
		return id;
	}

	public List<Point> getSnake() {
		return snake;
	}

	public String getName() {
		return name;
	}

	public Direction getDir() {
		return dir;
	}

	public Direction getLastDir() {
		return lastDir;
	}

	public void setLastDir(Direction d) {
		lastDir = d;
	}

	public void setDir(Direction d) {
		dir = d;
	}

	public void addPoint(int p) {
		point += p;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int p) {
		this.point = p;
	}

	public void setSnake(List<Point> p) {
		this.snake = p;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean isVeryDead() {
		return veryDead;
	}

	public void setVeryDead(boolean dead) {
		this.veryDead = dead;
	}

	public boolean isDrunk() {
		return this.drunk;
	}

	public void setDrunk(boolean drunk) {
		this.drunk = drunk;
	}
}

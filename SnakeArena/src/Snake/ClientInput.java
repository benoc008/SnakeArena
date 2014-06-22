package Snake;

import java.awt.Color;
import java.io.Serializable;

/**
 * A kliens által küldött adatokat tartalmazó osztály.
 * @author Demkó Bence LWMEHK
 *
 */
public class ClientInput implements Serializable {

	private static final long serialVersionUID = -6536012969280165417L;
	private String name;
	private int id;
	private Direction dir;
	private boolean isIn;
	private Color color;

	public ClientInput(String name, int id, Direction dir, boolean isIn,
			Color color) {
		this.name = name;
		this.id = id;
		this.dir = dir;
		this.isIn = isIn;
		this.color = color;
	}

	public boolean getIsIn() {
		return isIn;
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Direction getDir() {
		return dir;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public void setIsIn(boolean v) {
		this.isIn = v;
	}
}

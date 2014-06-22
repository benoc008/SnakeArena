package Snake;

import java.awt.Point;
import java.io.Serializable;

/**
 * 
 * Az osztály egy almát valósít meg. Az almának vannak koordinátái és kora. A
 * korra a romlás miatt van szükség. Az osztály klónozható is.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */

public class Apple implements Serializable, Cloneable {

	private static final long serialVersionUID = -6683059023188370279L;
	private Point point;
	private int age;

	public Apple(Point point) {
		super();
		this.point = point;
		this.age = 0;
	}

	public Point getPoint() {
		return point;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void time() {
		age++;
	}

	public Apple clone() {
		Apple clone = new Apple(point);
		clone.setAge(age);
		return clone;
	}
}

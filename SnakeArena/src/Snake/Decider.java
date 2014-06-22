package Snake;

import java.io.Serializable;

/**
 * 
 * @author Demkó Bence LWMEHK
 * 
 */

public class Decider implements Serializable {

	private static final long serialVersionUID = -2240445127405489499L;
	private boolean value;

	public Decider() {

	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean a) {
		value = a;
	}
}

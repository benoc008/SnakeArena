package Snake;

/**
 * A játékban előforduló halál lehetőségeket tartalmazó enum
 * @author Demkó Bence LWMEHK
 *
 */

public enum DeathMode {
	Respawn(0), LosePoints(1), EndGame(2), Immortal(3);

	private int n;

	private DeathMode(int n) {
		this.n = n;
	}

	public int getValue() {
		return n;
	}
}

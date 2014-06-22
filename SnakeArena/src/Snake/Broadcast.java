package Snake;

import java.util.List;

/**
 * Az osztály a szerver oldali információszórásért felelős. Külön szálon
 * futtatható, és másodpercenként 20-szor elküldi a játék adatait a
 * felhasználóknak.
 * 
 * Privát változóként tárolja a játékot, a csatlakozott kliensek kezelésért
 * felelős objektumok listáját és megszakításért felelős változókat
 * 
 * @author Demkó Bence LWMEHK
 * 
 */

public class Broadcast extends Thread {

	private Core game;
	private Decider play;
	private List<ClientThread> clients;
	private Decider modify;

	public Broadcast(Core game, Decider play, List<ClientThread> clients,
			Decider modify) {
		this.game = game;
		this.play = play;
		this.clients = clients;
		this.modify = modify;
	}

	/**
	 * A függvény szinkronizált, ha valaki módosítja a játék, vagy a kliensek
	 * adatait, akkor vár, majd a klienseknek elküldi a játék adatait. Továbbá,
	 * ha üzenetet küld a szerver a klienseinek, az első elküldés után
	 * leállítja, hogy ne kapják meg többször.
	 */
	private synchronized void broadcast() {
		try {
			while (modify.getValue())
				wait();
			modify.setValue(true);
			ClientThread temp = null;

			for (ClientThread ct : clients) {
				if (ct.isRunner())
					ct.sendData();
				else
					temp = ct;
			}
			if (game.getMessage() != null) {
				game.setMessage(null);
			}
			clients.remove(temp);
			modify.setValue(false);
			notify();
		} catch (InterruptedException e) {
			System.out.println("exception at broadcast: " + e);
		}
	}

	public void run() {
		while (play.getValue()) {
			broadcast();
			try {
				Thread.currentThread();
				// Thread.sleep((game.getSpeed()>950)?50:(1000-game.getSpeed()-50));
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

package Snake;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A szerver példányosítja, ez az osztály felelős az az adatok fogadására a kliens felől.
 * @author Demkó Bence LWMEHK
 *
 */
public class ClientThread extends Thread {

	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private ClientInput ci;
	private String username;

	private boolean runner;
	private Core game;
	private int id;
	private MultiHost mh;

	ClientThread(Socket socket, int id, Core game, MultiHost mh) {
		this.id = id;
		this.game = game;
		this.runner = true;
		this.socket = socket;
		this.mh = mh;

		try {
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput = new ObjectInputStream(socket.getInputStream());

			ci = (ClientInput) (sInput.readObject());
			username = ci.getName();

			game.addSnake(username, ci.getColor(), id);
			mh.display(username + " just connected as #" + id);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Fogadja az adatokat, és továbbítja a szervernek.
	 * Ha lecsatlakozott, törli a kígyót a pályáról.
	 */
	public void run() {
		while (runner) {
			try {
				ci = (ClientInput) (sInput.readObject());
				if (ci.getIsIn()) {
					for (Snake s : game.getSnakes()) {
						if (s.getID() == ci.getId())
							s.setDir(ci.getDir());
					}
				} else {
					runner = false;
				}

			} catch (Exception e) {
				System.out.println(e);
			}
		}

		// snake torlese a palyarol
		Snake temp = null;
		for (Snake s : game.getSnakes())
			if (s.getID() == id)
				temp = s;
		game.getSnakes().remove(temp);
		mh.display(temp.getName() + " disconnected");
		close();
	}
	
	/**
	 * Lezárja a streameket
	 */
	public void close() {
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		}
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		}
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Elküldi a kliensnek a játékadatokat tartalmazó objektum klónját.
	 * @return
	 */
	public boolean sendData() {
		try {
			Core copy = game.clone();
			sOutput.writeObject(copy);
			sOutput.reset();
			return true;
		} catch (Exception e) {
			mh.display("Error sending message to " + username);
			mh.display("Now will disconnect!");
			runner = false;
			close();
			System.out.println(e);
		}
		return true;
	}

	public boolean isRunner() {
		return runner;
	}

	public void setRunner(boolean runner) {
		this.runner = runner;
	}

	public int getID() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectInputStream getsInput() {
		return sInput;
	}

	public ObjectOutputStream getsOutput() {
		return sOutput;
	}

}

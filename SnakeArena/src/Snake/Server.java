package Snake;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Szerver osztály. Létrehozza és elindítja a játék magját, várja a klienseket a
 * megfelelő porton, ha csatlakoztak, létrehoz nekik ClientThread-et, és tárolja
 * azt egy listában.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class Server extends Thread {

	private Core game;
	private Decider play;
	private int port;
	private List<ClientThread> clients;
	private int uniqueId;
	private Broadcast brc;
	private MultiHost mh;
	private Decider modify;

	public Server(int port, MultiHost mh) {

		this.uniqueId = 0;
		this.port = port;
		this.clients = new ArrayList<ClientThread>();
		this.mh = mh;
		this.modify = new Decider();
		this.modify.setValue(false);

		GameSetup gm = new GameSetup();
		try {
			gm.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		play = new Decider();
		play.setValue(true);

		game = new Core(gm.getWidth(), gm.getHeight(), gm.getFieldSize(),
				gm.getMapPath(), gm.getSpeed(), play, gm.isPalinka(),
				gm.getDeathMode());
		Thread gameThread = new Thread(game);
		gameThread.start();
		mh.display("Server has started");

		brc = new Broadcast(game, play, clients, modify);
		brc.start();

	}

	public synchronized void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			while (play.getValue()) {
				mh.display("Waiting for clients at port: " + port);
				Socket socket = serverSocket.accept();
				if (!play.getValue()) {
					break;
				}
				ClientThread t = new ClientThread(socket, ++uniqueId, game, mh);
				while (modify.getValue())
					wait();
				modify.setValue(true);
				clients.add(t);
				t.start();
				modify.setValue(false);
				notify();
			}

			try {
				serverSocket.close();
				for (int i = 0; i < clients.size(); i++) {
					ClientThread tc = clients.get(i);
					try {
						tc.getsInput().close();
						tc.getsOutput().close();
						tc.getSocket().close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Törli a játékost az id-je alapján
	 * 
	 * @param id
	 *            a törlendő játékos azonosítója
	 */
	synchronized void remove(int id) {
		for (Snake s : game.getSnakes()) {
			System.out.println(id + " " + s.getID());
			if (s.getID() == id) {
				mh.display(s.getName() + "disconnected");
				game.getSnakes().remove(s);
				break;
			}
		}
		for (ClientThread ct : clients) {
			if (ct.getID() == id) {
				ct.close();
				clients.remove(ct);
				return;
			}
		}
	}

	public Core getGame() {
		return game;
	}

	public Decider getPlay() {
		return play;
	}

	public List<ClientThread> getClients() {
		return clients;
	}
}

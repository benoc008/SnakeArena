package Snake;

import java.awt.Dimension;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;

/**
 * Az osztály a kliens oldal megvalósításának legfontosabb osztálya
 * 
 * @author Demkó Bence LWMEHK
 * 
 */

public class Client {

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;

	private int id;
	private Decider play;

	private String server;
	private ClientInput ci;
	private int port;

	private PlayerSetup player;

	private SnakeArea s;
	private JFrame f;
	private Thread t;
	private Core data;

	Client(String server, int port) {
		this.server = server;
		this.port = port;
		this.id = 0;

		play = new Decider();
		play.setValue(true);

		player = new PlayerSetup();
	}

	/**
	 * Felcsatlakozik a szerverre, beolvassa a játékos adatait, létrehozza a
	 * ClientInput objektumot, amit majd a szervernek fog küldözgetni, és
	 * elindítja az inner classt, ami fogadja az adatokat a szervertől
	 * 
	 * @return
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

		/* Creating both Data Stream */
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

		File pl = new File("PlayerSetup.snk");
		if (pl.exists())
			try {
				player.load();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		ci = new ClientInput(player.getName(), id, Direction.NORTH, true,
				player.getColor());

		new ListenFromServer().start();

		try {
			sOutput.writeObject(ci);
			sOutput.reset();
		} catch (Exception e) {
			System.out.println("Exception doing login : " + e);
			disconnect();
			return false;
		}

		return true;
	}

	/**
	 * Az output streamre írja az adatokat (tehát a szervernek), majd reseteli a
	 * streamet, hogy ne mindig ugyan azt küldje.
	 * 
	 * @param stuff
	 */
	void sendMessage(ClientInput stuff) {
		try {
			sOutput.writeObject(stuff);
			sOutput.reset();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Lezárja a streameket és a socketet.
	 */
	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		}
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		}
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Inner class, ami a szervertől, az input streamről várja a játék adatait.
	 * Miután az első adatokkal létrehozta a játékfelületet, várakozás nélkül
	 * tölti le a soron következőket, és frissít. Tovább itt hívja meg a
	 * sendMessage függvényt is. A játék végénél kilép a ciklusból és elküldi a
	 * servernek az utolsó üzenetet, ami tartalmazza, hogy kilépett.
	 */
	class ListenFromServer extends Thread {

		public void run() {
			try {
				data = (Core) (sInput.readObject());

				id = data.getSnakes().get(data.getSnakes().size() - 1).getID();
				ci.setId(id);

				s = new SnakeArea(data.getWidth(), data.getHeight(),
						data.getFieldSize(), data.getSpeed(), data.getSnakes(),
						data.getApples(), data.getBricks(), Client.this.id,
						play, data.isPalinka());

				f = new JFrame();
				f.setContentPane(s);
				f.pack();
				f.setTitle("Snake - Multiplayer");
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setVisible(true);

				t = new Thread(s);
				t.start();

				play = s.getPlay();
			} catch (Exception e) {
				System.out.println("00" + e);
			}
			while (play.getValue()) {
				sendMessage(ci);
				try {
					data = (Core) sInput.readObject();
					if (!data.getPlay())
						break;
					play = s.getPlay(); // for client side interrupt
					// Szerver utasitashoz
					if ((s.getWidth() != data.getWidth())
							|| (s.getHeight() != data.getHeight())) {

						s.setWidth(data.getWidth());
						s.setHeight(data.getHeight());

						s.setPreferredSize(new Dimension(data.getWidth(), data
								.getHeight()));

						f.getContentPane().setSize(s.getWidth(), s.getHeight());
						f.pack();
					}

					s.setFieldSize(data.getFieldSize());
					s.setSnakes(data.getSnakes());
					s.setApples(data.getApples());
					s.setBricks(data.getBricks());
					s.setPalinka(data.isPalinka());
					if (data.getMessage() != null) {
						s.setMessage(data.getMessage());
						s.setMessageTimer(data.getMessageTimer());
					}
					ci.setDir(s.getDir());
				} catch (Exception e) {
					System.out.println("Server has close the connection: " + e);
					break;
				}
			}
			ci.setIsIn(false);
			sendMessage(ci);
			f.dispose();
		}
	}
}

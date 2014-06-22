package Snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 * A szerver kezeléséért felelős osztály, egy ablakból áll, aminek az alján
 * található egy beviteli mező, a felső részén pedig a szerver kommunikál a
 * felhasználóval.
 * 
 * A beviteli mezőn keresztül irányíthatjuk a szerverünket.
 * 
 * @author Demkó Bence
 * 
 */
public class MultiHost extends JFrame implements Runnable, KeyListener {

	private static JTextArea input;
	private static JTextArea area;
	private Server srv;
	private static JScrollPane scroll;

	private static final long serialVersionUID = 5905463403837393603L;

	public MultiHost(int port) {
		area = new JTextArea();
		area.setEditable(false);
		area.setText("");

		input = new JTextArea();

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		input.setBorder(BorderFactory.createCompoundBorder(border,
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		scroll = new JScrollPane(area);

		input.addKeyListener(this);
		input.setFocusable(true);

		srv = new Server(port, this);
		srv.start();
	}

	/**
	 * Parancsfeldolgozó függvény. A megfelelő paracsszóra a megfelelő függvényt
	 * hívja meg.
	 * 
	 * @param cmd
	 *            a parancsot tartalmazó String.
	 */
	public void commander(String cmd) {
		String[] split = cmd.split(" ");
		if (split[0].equals("exit")) {
			exit();
		} else if (split[0].equals("help")) {
			help();
		} else if (split[0].equals("info")) {
			info();
		} else if (split[0].equals("list")) {
			list();
		} else if (split[0].equals("kick")) {
			if (split.length > 1)
				kick(split[1]);
			else {
				display("Need more arguments!");
			}
		} else if (split[0].equals("set")) {
			setter(split);
		} else if (split[0].equals("message")) {
			message(split);
		} else if (split[0].equals("restart")) {
			restart(split);
		}

	}

	/**
	 * Továbbítja az üzenetet az ablaknak, hogy a felhasználó láthassa.
	 * 
	 * @param s
	 *            az üzenet
	 */
	public void display(String s) {
		area.append(s + " \n");
	}

	/**
	 * Az exit parancs kiadásával bezárjuk a szervert, de előtte készít egy
	 * naplófilet.
	 */
	public void exit() {
		display("Server is shutting down.");
		srv.getPlay().setValue(false);
		PrintWriter pw;
		try {
			long date = new Date().getTime();
			pw = new PrintWriter("log" + date + ".txt");
			pw.write(area.getText());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.dispose();
	}

	/**
	 * Az info parancs kiadásával információkat kaphatunk a szerver aktuális
	 * beállításairól.
	 */
	public void info() {
		display("Width: " + srv.getGame().getWidth());
		display("Height: " + srv.getGame().getHeight());
		display("FieldSize: " + srv.getGame().getFieldSize());
		display("Speed: " + srv.getGame().getSpeed());
		display("Palinka mode: " + (srv.getGame().isPalinka() ? "on" : "off"));
		display("Number of players: " + srv.getGame().getSnakes().size());
	}

	/**
	 * A list függvény kilistázza szerverre csatlakozott játékosokat.
	 */
	public void list() {
		display("List of online players:");
		for (Snake s : srv.getGame().getSnakes()) {
			display(s.getName());
		}
	}

	/**
	 * A help parancs kiadására a függvény egy rövid leírást ad az érvényes
	 * parancsokról és azok működéséről.
	 */
	public void help() {
		display("List of commands:");
		display("\thelp\tshows this list");
		display("\tlist\tlists online players");
		display("\tinfo\tshows informations of the current game");
		display("\tkick NAME\tkicks NAME from the server");
		display("\texit\tstops the server");
		display("\tset PROPERTY VALUE\tsets the property to value. \n\t\t "
				+ "Properties: size, speed, width, height, mode. \n\t\t"
				+ "modes: immortal, endgame, losepoints, respawn \n\t\t"
				+ "Example: set width 40");
		display("\tmessage TIME MESSAGE\tshows the MESSAGE to the clients for TIME milisecs");
	}

	/**
	 * A kick paranccsal a paraméterként megadott felhasználót kirúghatjuk a
	 * szerverről.
	 * 
	 * @param str
	 *            a kiválaszott felhasználó
	 */
	public void kick(String str) {
		for (Snake s : srv.getGame().getSnakes()) {
			if (s.getName().equals(str)) {
				for (ClientThread ct : srv.getClients()) {
					if (s.getID() == ct.getID())
						ct.setRunner(false);
				}
			}
		}
		display(str + " kicked");
	}

	public void run() {
		setSize(480, 640);
		setTitle("Snake - Server");
		// pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		this.add(scroll, BorderLayout.CENTER);
		this.add(input, BorderLayout.PAGE_END);
	}

	/**
	 * A set parancs kiadásával a set függvény hívódik meg. Ezzel lehet a
	 * különféle beállításokat módosítani.
	 * 
	 * @param cmd
	 *            String a paranccsal és argumentumaival
	 */
	public void setter(String[] cmd) {
		if (cmd.length < 3) {
			display("Too few arguments!");
		} else {
			if (cmd[1].equals("palinka")) {
				if (cmd[2].equals("true"))
					srv.getGame().setPalinka(true);
				else if (cmd[2].equals("false"))
					srv.getGame().setPalinka(false);
				else {
					display("wrong argument: " + cmd[2]);
					return;
				}
				display("palinka set to " + cmd[2]);
			} else if (cmd[1].equals("speed")) {
				srv.getGame().setSpeed(Integer.parseInt(cmd[2]));
				display("speed set to " + cmd[2]);
			} else if (cmd[1].equals("height")) {
				srv.getGame()
						.setHeight(
								Integer.parseInt(cmd[2])
										* srv.getGame().getFieldSize());
				display("height set to " + cmd[2]);
			} else if (cmd[1].equals("width")) {
				srv.getGame()
						.setWidth(
								Integer.parseInt(cmd[2])
										* srv.getGame().getFieldSize());
				display("width set to " + cmd[2]);
			} else if (cmd[1].equals("size")) {
				srv.getGame().setWidth(
						Integer.parseInt(cmd[2])
								* (srv.getGame().getWidth() / srv.getGame()
										.getFieldSize()));
				srv.getGame().setHeight(
						Integer.parseInt(cmd[2])
								* (srv.getGame().getHeight() / srv.getGame()
										.getFieldSize()));
				srv.getGame().setFieldSize(Integer.parseInt(cmd[2]));
				display("size set to " + cmd[2]);
			} else if (cmd[1].equals("mode")) {
				switch (cmd[2]) {
				case "immortal":
					srv.getGame().setDeathMode(DeathMode.Immortal);
					display("game mode set to immortal.");
					break;
				case "endgame":
					srv.getGame().setDeathMode(DeathMode.EndGame);
					display("game mode set to endgame.");
					break;
				case "respawn":
					srv.getGame().setDeathMode(DeathMode.Respawn);
					display("game mode set to respawn.");
					break;
				case "losepoints":
					srv.getGame().setDeathMode(DeathMode.LosePoints);
					display("game mode set to losepoints.");
					break;
				}
			}
		}
	}

	/**
	 * A restart parancs kiadásására hívdik meg, és újraindítja a játékot,
	 * esetleg új pályát tölt be.
	 * 
	 * @param map
	 *            a betöltendő pálya elérése
	 */
	public void newGame(String map) {
		// Remove apples
		srv.getGame().getApples().removeAll(srv.getGame().getApples());
		if (map != null) { // if new map is set
			// set the width and height from the file's first row
			srv.getGame().setMapPath(map);
			try (BufferedReader br = new BufferedReader(new FileReader(map))) {
				String line = br.readLine();
				String[] token = line.split(" ");
				srv.getGame().setWidth(
						Integer.parseInt(token[0])
								* srv.getGame().getFieldSize());
				srv.getGame().setHeight(
						Integer.parseInt(token[1])
								* srv.getGame().getFieldSize());
			} catch (Exception e) {
				System.out.println(e);
			}
			// delete old bricks and add the new
			srv.getGame().getBricks().removeAll(srv.getGame().getBricks());
			srv.getGame().loadBricks();
		}
		for (Snake s : srv.getGame().getSnakes()) {
			s.setPoint(0);
			s.getSnake().removeAll(s.getSnake());

			int x, y;
			while (true) {
				x = (int) (Math.random() * (srv.getGame().getWidth() / srv
						.getGame().getFieldSize()));
				y = (int) (Math.random() * (srv.getGame().getHeight() / srv
						.getGame().getFieldSize()));
				if (srv.getGame().isFree(new Point(x, y))
						&& srv.getGame().isFree(new Point(x, y + 1))
						&& srv.getGame().isFree(new Point(x, y + 2)))
					break;
			}
			s.getSnake().add(new Point(x, y + 2));
			s.getSnake().add(new Point(x, y + 1));
			s.getSnake().add(new Point(x, y));
			s.setDir(Direction.NORTH);
			s.setLastDir(Direction.NORTH);
		}
	}

	/**
	 * Az újraindítást, vagy újraindítás pályamódosítássalt kezeli.
	 * 
	 * @param s
	 *            az új pálya elérési útja.
	 */
	public void restart(String[] s) {
		if (s.length == 1) {
			newGame(null);
		} else {
			newGame(s[1]);
		}
	}

	/**
	 * A függvény segítségével üzenetet küldhet a szerver a klienseknek olyan
	 * módon, hogy az az ő játékfelületük közepén fog megjelenni, annyi
	 * másodpercre, amennyit a szerver kíván.
	 * 
	 * @param s
	 *            a kívánt időt és az üzenetet tartalmazza
	 */
	public void message(String[] s) {
		if (s.length < 3)
			display("Too few arguments.");
		else {
			StringBuilder builder = new StringBuilder();
			for (int i = 2; i < s.length; i++) {
				builder.append(s[i] + " ");
			}
			String message = builder.toString();
			srv.getGame().setMessageTimer(Integer.parseInt(s[1]));
			srv.getGame().setMessage(message);
			display("Server: " + message);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			commander(input.getText());
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			input.setText(null);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
}

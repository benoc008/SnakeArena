package Snake;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A játékos beállításait tartalmazó szerializálható osztály.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class PlayerSetup implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Color color;

	public PlayerSetup() {
		name = "Player";
		color = new Color(100, 100, 100);
	}

	public PlayerSetup(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	/**
	 * Az objektum elmentése a fileba.
	 */
	public void save() {
		try {
			FileOutputStream f = new FileOutputStream("PlayerSetup.snk");
			ObjectOutputStream out = new ObjectOutputStream(f);
			out.writeObject(this);
			out.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	/**
	 * Az objektum betöltése a fileból.
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception {
		File file = new File("PlayerSetup.snk");
		if (!file.exists())
			return;
		FileInputStream f = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(f);
		PlayerSetup p = (PlayerSetup) in.readObject();
		in.close();
		this.name = p.name;
		this.color = p.color;
	}

	public void alterColor(Color color) throws Exception {
		load();
		this.color = color;
		save();
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}
}

package Snake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * A játék beállításait tartalmazó serializálható file.
 * @author Demkó Bence LWMEHK
 *
 */
public class GameSetup implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	private int fieldSize;
	private String mapPath;
	private int speed;
	private boolean palinka;
	private DeathMode deathMode;

	public GameSetup(){
		this.width = 640;
		this.height = 480;
		this.fieldSize = 10;
		this.mapPath = null;
		this.speed = 250;
		this.palinka = true;
		this.deathMode = DeathMode.Respawn;
	}

	public GameSetup(int width, int height, int fieldSize, String mapPath,
			int speed, boolean palinka, DeathMode dm) {
		super();
		this.width = width*fieldSize;
		this.height = height*fieldSize;
		this.fieldSize = fieldSize;
		this.mapPath = mapPath;
		this.speed = speed;
		this.palinka = palinka;
		this.deathMode = dm;
	}
	
	/**
	 * Az objektum kiírása fileba.
	 */
	public void save(){
		try {
			FileOutputStream f = new FileOutputStream("GameSetup.snk");
			ObjectOutputStream out = new ObjectOutputStream(f);
			out.writeObject(this);
			out.close();
		} catch(IOException ex) { System.out.println(ex); }
	}
	
	/**
	 * Az objektum betöltése fileból.
	 */
	public void load() throws Exception{
		File file = new File("GameSetup.snk");
		if(!file.exists())
			return;
		FileInputStream f = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(f);
		GameSetup p = (GameSetup)in.readObject();
		in.close();
		
		this.width = p.width;
		this.height = p.height;
		this.fieldSize = p.fieldSize;
		this.mapPath = p.mapPath;
		this.speed = p.speed;
		this.palinka = p.palinka;
		this.deathMode = p.deathMode;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFieldSize() {
		return fieldSize;
	}

	public String getMapPath() {
		return mapPath;
	}

	public int getSpeed() {
		return speed;
	}
	
	public DeathMode getDeathMode() {
		return deathMode;
	}

	public boolean isPalinka() {
		return palinka;
	}

}

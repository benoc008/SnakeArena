package Snake;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A játékos beállításaiért felelős menü. Konstruktorban létrehozza a
 * menüpontoknak megfelelő elemeket, feltölti a korábbi beállítások értékeivel.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class Settings extends JFrame implements Runnable, ChangeListener,
		ActionListener {

	private static final long serialVersionUID = 5905463403837393603L;

	private JColorChooser jcc;
	private Color myColor;
	private File chosenFile;

	private JButton save;
	private JButton exit;

	private JLabel nameLabel;
	private JTextField Name;

	private JLabel widthLabel;
	private JTextField width;
	private JLabel heightLabel;
	private JTextField height;

	private JLabel fieldSizeLabel;
	private JTextField fieldSize;

	private JLabel fileChooserLabel;
	private JButton fileChooserButton;

	private JFileChooser fileChooser;

	private JLabel speedLabel;
	private JTextField speed;

	private JLabel checkboxLabel;
	private JCheckBox checkbox;

	private JLabel comboLabel;
	private JComboBox<?> combo;

	public Settings() {

		// set layout for the frame
		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		PlayerSetup pl = new PlayerSetup();
		GameSetup gm = new GameSetup();
		try {
			pl.load();
			gm.load();
		} catch (Exception e) {
			System.out.println(e);
		}

		save = new JButton("Save");
		save.addActionListener(this);
		exit = new JButton("Exit");
		exit.addActionListener(this);

		nameLabel = new JLabel("Player Name");
		Name = new JTextField(10);
		Name.setText(pl.getName());

		// Color picker
		myColor = pl.getColor();
		jcc = new JColorChooser();
		jcc.getSelectionModel().addChangeListener(this);

		widthLabel = new JLabel("Map Width");
		width = new JTextField(4);
		width.setText("" + gm.getWidth() / gm.getFieldSize()); // valami
																// toString
																// szebb volna

		heightLabel = new JLabel("Map Height");
		height = new JTextField(4);
		height.setText("" + gm.getHeight() / gm.getFieldSize());

		fieldSizeLabel = new JLabel("Size");
		fieldSize = new JTextField(2);
		fieldSize.setText("" + gm.getFieldSize());

		// file chooser
		fileChooserLabel = new JLabel("Map Path");
		fileChooserButton = new JButton("Load Map");
		fileChooserButton.addActionListener(this);

		if (gm.getMapPath() != null) {
			chosenFile = new File(gm.getMapPath());
			fileChooserButton.setText(chosenFile.getName()
					+ " - Click to change");
		}
		fileChooser = new JFileChooser(chosenFile);

		speedLabel = new JLabel("Speed");
		speed = new JTextField(4);
		speed.setText("" + gm.getSpeed());

		// checkbox
		checkboxLabel = new JLabel("Palinka mode");
		checkbox = new JCheckBox();
		checkbox.setSelected(gm.isPalinka());

		comboLabel = new JLabel("Death mode");
		String[] options = { "Respawn upon death", "Lose points upon death",
				"End game upon death", "Immortal" };
		combo = new JComboBox<String>(options);
		combo.setSelectedIndex(gm.getDeathMode().getValue());

		// add buttons to frame
		c.gridx = 0;
		c.gridy = 0;
		add(nameLabel, c);
		c.gridx = 1;
		c.gridy = 0;
		add(Name, c);
		c.gridx = 0;
		c.gridy = 1;
		add(widthLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		add(width, c);
		c.gridx = 0;
		c.gridy = 2;
		add(heightLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		add(height, c);
		c.gridx = 0;
		c.gridy = 3;
		add(fieldSizeLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		add(fieldSize, c);
		c.gridx = 0;
		c.gridy = 4;
		add(speedLabel, c);
		c.gridx = 1;
		c.gridy = 4;
		add(speed, c);
		c.gridx = 0;
		c.gridy = 5;
		add(checkboxLabel, c);
		c.gridx = 1;
		c.gridy = 5;
		add(checkbox, c);
		c.gridx = 0;
		c.gridy = 6;
		add(comboLabel, c);
		c.gridx = 1;
		c.gridy = 6;
		add(combo, c);
		c.gridx = 0;
		c.gridy = 7;
		add(fileChooserLabel, c);
		c.gridx = 1;
		c.gridy = 7;
		add(fileChooserButton, c);
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 2;
		add(jcc, c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 10;
		add(save, c);
		c.gridx = 1;
		c.gridy = 10;
		add(exit, c);

	}
	
	/**
	 * Gombok kezelése
	 * save: elmenti a beállításokat a megfelelő GameSetup és PlayerSetup osztályok segítségével.
	 * exit: bezárja az ablakot.
	 * fileChooserButton: megnyitja a file kiválasztó ablakot a pálya kiválasztásához.
	 */
	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == save) {
			try {
				PlayerSetup pl = new PlayerSetup(Name.getText(), myColor);
				pl.save();
				GameSetup gm = new GameSetup(Integer.parseInt(width.getText()),
						Integer.parseInt(height.getText()),
						Integer.parseInt(fieldSize.getText()),
						(chosenFile == null) ? null : chosenFile
								.getCanonicalPath(), Integer.parseInt(speed
								.getText()), checkbox.isSelected(),
						DeathMode.values()[combo.getSelectedIndex()]);

				gm.save();
			} catch (IOException ex) {
				System.out.println(ex);
			}
		} else if (e.getSource() == exit) {
			Settings.this.dispose();
		} else if (e.getSource() == fileChooserButton) {
			int returnVal = fileChooser.showOpenDialog(Settings.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				chosenFile = fileChooser.getSelectedFile();

				try (BufferedReader br = new BufferedReader(new FileReader(
						chosenFile))) {
					String line = br.readLine();
					if (line == null)
						throw new FileNotFoundException();
					String[] token = line.split(" ");
					width.setText(token[0]);
					height.setText(token[1]);
				} catch (Exception ex) {
					System.out.println(ex);
				}

				fileChooserButton.setText(chosenFile.getName()
						+ " - Click to change");
			}
		}
	}

	private static void createMenu() {
		// Create and set up the window.
		JFrame frame = new Settings();
		frame.setSize(640, 480);
		frame.setTitle("Snake - Settings");
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void run() {
		createMenu();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		myColor = jcc.getColor();
		this.repaint();
	}

}

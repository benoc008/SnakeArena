package Snake;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * A multiplayer játékmdhoz kapcsolódó menü ablaka, és tartalma. Innen lehet
 * játékhoz csatlakozni, vagy szervert létrehozni. Konstruktorban hozza létre az
 * elemeket, és tölti fel adattal a korábbi beállításoknak megfelelően.
 * GridBagLayoutot használ.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class MultiMenu extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 7723265607171630845L;

	private File chosenFile;

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
	private JComboBox<String> combo;

	private JLabel portLabelh;
	private JTextField porth;

	private JLabel ipLabel;
	private JTextField ipf;

	private JLabel portLabel;
	private JTextField portf;

	private JButton create;
	private JButton join;

	public MultiMenu() {

		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		GameSetup gm = new GameSetup();
		try {
			gm.load();
		} catch (Exception e) {
			System.out.println(e);
		}

		create = new JButton("Create");
		create.addActionListener(this);
		join = new JButton("Join");
		join.addActionListener(this);

		widthLabel = new JLabel("Map Width");
		width = new JTextField(4);
		width.setText("" + gm.getWidth() / gm.getFieldSize()); // valami
																// toString
																// szebb volna

		heightLabel = new JLabel("Map Height");
		height = new JTextField(4);
		height.setText("" + gm.getHeight() / gm.getFieldSize());

		fieldSizeLabel = new JLabel("size");
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

		// port host
		portLabelh = new JLabel("Port");
		porth = new JTextField(5);
		porth.setText("5000");

		// checkbox
		checkboxLabel = new JLabel("Palinka mode");
		checkbox = new JCheckBox();
		checkbox.setSelected(gm.isPalinka());

		comboLabel = new JLabel("Death mode");
		String[] options = { "Respawn upon death", "Lose points upon death",
				"End game upon death", "Immortal" };
		combo = new JComboBox<String>(options);
		combo.setSelectedIndex(gm.getDeathMode().getValue());

		// ip
		ipLabel = new JLabel("IP");
		ipf = new JTextField(16);
		ipf.setText("localhost");

		// port
		portLabel = new JLabel("Port");
		portf = new JTextField(5);
		portf.setText("5000");

		// add buttons to frame

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
		add(portLabelh, c);
		c.gridx = 1;
		c.gridy = 5;
		add(porth, c);
		c.gridx = 0;
		c.gridy = 6;
		add(checkboxLabel, c);
		c.gridx = 1;
		c.gridy = 6;
		add(checkbox, c);
		c.gridx = 0;
		c.gridy = 7;
		add(comboLabel, c);
		c.gridx = 1;
		c.gridy = 7;
		add(combo, c);
		c.gridx = 0;
		c.gridy = 8;
		add(fileChooserLabel, c);
		c.gridx = 1;
		c.gridy = 8;
		add(fileChooserButton, c);
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 2;
		add(create, c);
		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		add(ipLabel, c);
		c.gridx = 1;
		c.gridy = 10;
		add(ipf, c);
		c.gridx = 0;
		c.gridy = 11;
		add(portLabel, c);
		c.gridx = 1;
		c.gridy = 11;
		add(portf, c);
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 2;
		add(join, c);

	}

	/**
	 * Gombnyomást kezelő függvény.
	 * 
	 * Create esetén elmenti a beállításokat, majd letrehoz egy új MultiHost-t,
	 * és bezárja magaután az ablakot.
	 * 
	 * Join esetén csatlakozik a megfelelő IP-hez és porthoz. Bezárja maga után
	 * az ablakot.
	 * 
	 * A fileChooserButton egy file kiválasztó menüt nyit meg, ahol
	 * kitallózhatjuk a használni kívánt térképet.
	 */
	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == create) {
			try {
				GameSetup gm = new GameSetup(Integer.parseInt(width.getText()),
						Integer.parseInt(height.getText()),
						Integer.parseInt(fieldSize.getText()),
						(chosenFile == null) ? null : chosenFile
								.getCanonicalPath(), Integer.parseInt(speed
								.getText()), checkbox.isSelected(),
						DeathMode.values()[combo.getSelectedIndex()]);
				gm.save();

				MultiHost mh = new MultiHost(Integer.parseInt(porth.getText()));
				Thread t = new Thread(mh);
				t.start();
				MultiMenu.this.dispose();
			} catch (Exception ex) {
				System.out.println(ex);
			}
		} else if (e.getSource() == join) {
			Client sp = new Client(ipf.getText(), Integer.parseInt(portf
					.getText()));
			sp.start();
			MultiMenu.this.dispose();
		} else if (e.getSource() == fileChooserButton) {
			int returnVal = fileChooser.showOpenDialog(MultiMenu.this);

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
		JFrame frame = new MultiMenu();
		frame.setSize(640, 480);
		frame.setTitle("Snake - Multiplayer");
		// Display the window.
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void run() {
		createMenu();
	}
}

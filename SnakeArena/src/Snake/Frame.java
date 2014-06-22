package Snake;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * A főmenüt létrehozó osztály.
 * @author Demkó Bence LWMEHK
 *
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = -28516077806102065L;

	public Frame() {
		setLayout(new FlowLayout());
		setContentPane(new JLabel(new ImageIcon("img/background.png")));
		setLayout(new BorderLayout());

		JButton button1 = new JButton("Single Player");
		JButton button2 = new JButton("Multi Player");
		JButton scores = new JButton("Scores");
		JButton button3 = new JButton("Settings");
		JButton button4 = new JButton("Quit");

		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SinglePlayer sp = new SinglePlayer();
				sp.start();
			}
		});

		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MultiMenu mm = new MultiMenu();
				Thread tmm = new Thread(mm);
				tmm.start();
			}
		});

		button3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Settings set = new Settings();
				Thread t = new Thread(set);
				t.start();
			}
		});

		button4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		scores.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Scores();
			}
		});

		// add buttons to frame
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(button1);
		panel.add(button2);
		panel.add(scores);
		panel.add(button3);
		panel.add(button4);
		add(panel, BorderLayout.SOUTH);

		setSize(480, 480);
		setTitle("Snake");
		// Display the window.
		// pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

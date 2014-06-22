package Snake;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Az eredménylista táblázatát megvalósító osztály. Konstruktorban beolvassa a
 * az eddigi eredményeket, és egy 2D tömbben tárolja.
 * 
 * @author Demkó Bence LWMEHK
 * 
 */
public class Scores extends JFrame {

	private static final long serialVersionUID = -4849515361273150947L;

	private String[][] data;

	public Scores() {
		try {
			File file = new File("scores.snk");
			if (!file.exists()) {

			}
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);

			int numLines = lnr.getLineNumber();
			lnr.close();

			BufferedReader br = new BufferedReader(new FileReader(file));
			int i = 0;
			data = new String[numLines][2];
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				String[] splits = line.split(" ");
				data[i][0] = splits[0];
				data[i][1] = splits[1];
				i++;
			}
			br.close();
		} catch (Exception e) {
			System.out.println("scores: " + e);
		}

		setSize(480, 480);
		setTitle("Snake - Toplist");
		// Display the window.
		// pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initComponents();
	}

	/**
	 * Létrehozza a táblázatot, scrollozhatóvá teszi, beállítja a sortert, és
	 * használja is.
	 */
	private void initComponents() {
		this.setLayout(new BorderLayout());
		JTable table = new JTable(new MyTableModel());
		JScrollPane scroll = new JScrollPane(table);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				table.getModel());
		table.setRowSorter(sorter);

		table.getRowSorter().toggleSortOrder(1);
		table.getRowSorter().toggleSortOrder(1);

		add(scroll);

	}

	/**
	 * Inner class a TableModel megvalósításához.
	 * @author Demkó Bence
	 *
	 */
	class MyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -6809896913672718495L;

		String[] columnNames = { "Name", "Score" };

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Class<?> getColumnClass(int c) {
			if (c == 0)
				return String.class;
			else
				return Integer.class;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			if (arg1 == 0)
				return data[arg0][arg1];
			else
				return Integer.parseInt(data[arg0][arg1]);
		}
	}
}

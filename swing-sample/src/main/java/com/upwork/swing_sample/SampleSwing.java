package com.upwork.swing_sample;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class SampleSwing extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6536616336446213988L;

	private JTextArea area;
	private JButton button;
	private JTable table;
	private DefaultTableModel tableModel = new DefaultTableModel();

	Connection c = null;
	Statement stmt = null;

	public SampleSwing() {
		initUI();
	}

	private void initUI() {
		try {
			// connect to sleekbill.db
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:sleekbill.db");
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
		// create menu
		createMenuBar();
		// create main panel
		createPanel();

		setTitle("SampleSwing");
		setSize(700, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createMenuBar() {
		// add menu to frame
		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("File");
		// shortcut to menu "File" alt+f
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem eMenuItem = new JMenuItem("Exit");
		// shortcut to menu "Exit" e
		eMenuItem.setMnemonic(KeyEvent.VK_E);
		eMenuItem.setToolTipText("Exit application");
		eMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		file.add(eMenuItem);
		menubar.add(file);

		setJMenuBar(menubar);
	}

	private void createPanel() {
		// main panel
		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
		add(basic);

		basic.add(Box.createVerticalGlue());

		// create panel for textarea and button
		JPanel queryPanel = new JPanel();
		queryPanel.setAlignmentX(1f);
		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.LINE_AXIS));

		area = new JTextArea();
		area.setPreferredSize(new Dimension(100, 100));
		queryPanel.add(area);

		// adding shortcut to button ctrl+f9 
		KeyStroke keySave = KeyStroke.getKeyStroke(KeyEvent.VK_F9, Event.CTRL_MASK); 
		Action execute = new AbstractAction("Execute") {  
			/**
			 * 
			 */
			private static final long serialVersionUID = -3784831494117127020L;

			public void actionPerformed(ActionEvent e) {     
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						executeQuery();
						return null;
					}
				}.execute();
			}
		}; 
		button = new JButton(execute); 
		button.getActionMap().put("execute", execute);
		button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keySave, "execute"); 

		queryPanel.add(button);
		queryPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		basic.add(queryPanel);

		// results panel for table to represent query result
		JPanel resultPanel = new JPanel();
		resultPanel.setAlignmentX(1f);
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.LINE_AXIS));

		// create table
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		resultPanel.add(scrollPane, BorderLayout.CENTER);
		basic.add(resultPanel);

		basic.add(Box.createRigidArea(new Dimension(0, 15)));
	}

	private void executeQuery() {
		System.out.println("START executeQuery method: \n query: " + area.getText());

		button.setEnabled(false);

		try {
			ResultSet rs = stmt.executeQuery(area.getText());
			ResultSetMetaData metaData = rs.getMetaData();

			// Names of columns
			Vector<String> columnNames = new Vector<String>();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnNames.add(metaData.getColumnName(i));
			}

			// Data of the table
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			while (rs.next()) {
				Vector<Object> vector = new Vector<Object>();
				for (int i = 1; i <= columnCount; i++) {
					vector.add(rs.getObject(i));
				}
				data.add(vector);
			}
			rs.close();

			// update table model
			tableModel.setDataVector(data, columnNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
		button.setEnabled(true);

		System.out.println("END executeQuery method");
	}


	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SampleSwing ex = new SampleSwing();
				ex.setVisible(true);
			}
		});
	}
}


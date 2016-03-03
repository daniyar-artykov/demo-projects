package com.upwork.ftp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.upwork.ftp.FileServer;

public class ServerForm extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1029472673102757266L;
	private JPanel panel;
	private JTextArea port;
	private JButton startServer;
	private JButton stopServer;
	private JLabel serverStatusText;
	private JLabel status;
	private BufferedImage disconnected = null;
	private BufferedImage connected = null;
	private FileServer fileServer = null;
	//	private JTextArea textArea;

	public ServerForm() {

		panel = new JPanel();
		port = new JTextArea();
		startServer = new JButton("Start");
		stopServer = new JButton("Stop");
		try {
			disconnected = ImageIO.read(ServerForm.class.getClassLoader().getResourceAsStream("disconnected.png"));
			connected = ImageIO.read(ServerForm.class.getClassLoader().getResourceAsStream("connected.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		status = new JLabel(new ImageIcon(disconnected));
		status.setBounds(111, 5, 30, 30);
		panel.add(status);

		serverStatusText = new JLabel();
		serverStatusText.setText("Stopped");
		serverStatusText.setBounds(5, 80, 150, 30);
		panel.add(serverStatusText);

		this.setSize(280, 150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel.setLayout(null);
		this.add(panel);

		port.setBounds(7, 5, 90, 30);

		panel.add(port);
		port.setText("23111");

		startServer.setBounds(5, 40, 95, 30);
		stopServer.setBounds(105, 40, 95, 30);

		panel.add(stopServer);
		panel.add(startServer);

		this.setTitle("Server");

		startServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if(fileServer == null) {
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							fileServer = new FileServer(Integer.parseInt(port.getText()));
							fileServer.accept();
							return null;
						}
					}.execute();

					status.setIcon(new ImageIcon(connected));
					serverStatusText.setText("Started");
				}
			} 
		});

		stopServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				fileServer = null;
				status.setIcon(new ImageIcon(disconnected));
				serverStatusText.setText("Stopped");
			} 
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ServerForm sf = new ServerForm();
				sf.setVisible(true);
			}
		});
	}
}

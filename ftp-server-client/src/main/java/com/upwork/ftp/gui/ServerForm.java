package com.upwork.ftp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.upwork.ftp.FileServer;

public class ServerForm extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1029472673102757266L;
	private JPanel panel;
	private JTextField port;
	private JButton startServer;
	private JButton stopServer;
	private JLabel serverStatusText;
	private JLabel status;
	private BufferedImage disconnected = null;
	private BufferedImage connected = null;
	private FileServer fileServer = null;

	public ServerForm() {

		panel = new JPanel();
		port = new JTextField();
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
		serverStatusText.setBounds(5, 80, 95, 30);
		panel.add(serverStatusText);

		this.setSize(280, 150);
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel.setLayout(null);
		this.add(panel);

		port.setBounds(7, 5, 90, 30);

		panel.add(port);
		startServer.setBounds(5, 40, 95, 30);
		stopServer.setBounds(105, 40, 95, 30);

		panel.add(stopServer);
		panel.add(startServer);

		this.setTitle("Server");


		startServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				try {
					if(fileServer != null) {
						fileServer = new FileServer(Integer.parseInt(port.getText()));
						fileServer.accept();

						status = new JLabel(new ImageIcon(connected));
						serverStatusText.setText("The File Server is running at port " + port.getText());
					}
				} catch (IOException ex) {
					System.out.println("Error: could not start the server at port " +  port);
				} 
			} 
		});

		stopServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				fileServer = null;
				status = new JLabel(new ImageIcon(disconnected));
				serverStatusText.setText("Stopped");
			} 
		});
	}

	public static void main(String[] args) {
		new ServerForm();
	}
}

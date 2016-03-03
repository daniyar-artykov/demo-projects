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
	private BufferedImage stopped = null;
	private BufferedImage started = null;
	private FileServer fileServer = null;
	private SwingWorker<Void, Void> currentWorker = null;

	public ServerForm() {
		// set title for server form
		this.setTitle("The File Server");
		// set default size of the server form
		this.setSize(300, 150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// add main panel to file server form
		panel = new JPanel();
		panel.setLayout(null);
		this.add(panel);

		// input for port, "23111" is default port
		port = new JTextArea("23111");
		// set bounds to port input
		port.setBounds(7, 5, 90, 30);
		// add port input to panel
		panel.add(port);


		// create buffered image instance for server status: started (color is green) and stopped (color is red)
		try {
			stopped = ImageIO.read(ServerForm.class.getClassLoader().getResourceAsStream("stopped.png"));
			started = ImageIO.read(ServerForm.class.getClassLoader().getResourceAsStream("started.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}

		// label to show server status
		status = new JLabel(new ImageIcon(stopped));
		status.setBounds(111, 5, 30, 30);
		panel.add(status);

		// start server button
		startServer = new JButton("Start");
		startServer.setBounds(5, 40, 95, 30);
		panel.add(startServer);

		// stop server button
		stopServer = new JButton("Stop");
		stopServer.setBounds(105, 40, 95, 30);
		panel.add(stopServer);

		// label to show messages and exceptions
		serverStatusText = new JLabel("Stopped");
		serverStatusText.setBounds(5, 80, 250, 30);
		panel.add(serverStatusText);		

		// action listener of the button Start
		startServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				try {
					if(port.getText() != null 
							&& Integer.parseInt(port.getText()) >= 10000 
							&& Integer.parseInt(port.getText()) <= 30000) {

						// stop current worker before start another file server
						if(currentWorker != null) {
							currentWorker.cancel(true);
							if(fileServer != null) {
								fileServer.stop();
								fileServer = null;
							}
							currentWorker = null;
						}

						currentWorker = new SwingWorker<Void, Void>() {
							@Override
							protected Void doInBackground() {
								fileServer = new FileServer(Integer.parseInt(port.getText()));
								if(fileServer.isServerStarted()) {
									fileServer.accept();
								} else {
									fileServer = null;
									serverStatusText.setText("Server doesn't started, use another port!");
								}
								return null;
							}
						};
						// execute in background thread for file server
						currentWorker.execute();
						// change server status icon to started (green)
						status.setIcon(new ImageIcon(started));
						// change info message 
						serverStatusText.setText("Started");
					} else {
						serverStatusText.setText("Invalid port! ");
					}
				} catch(Exception ex) {
					System.err.println(ex.getMessage());
					ex.printStackTrace();
					serverStatusText.setText("Invalid port! ");
				}
			}
		});

		// action listener of the button Stop
		stopServer.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				// if current worker is not null cancel it
				// and stop file server too
				if(currentWorker != null) {
					currentWorker.cancel(true);
					if(fileServer != null) {
						fileServer.stop();
						fileServer = null;
					}
					currentWorker = null;
				}
				// change server status to stopped (red)
				status.setIcon(new ImageIcon(stopped));
				// change info message
				serverStatusText.setText("Stopped");
			}
		});
	}

	public static void main(String[] args) {

		// start gui
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ServerForm sf = new ServerForm();
				sf.setVisible(true);
			}
		});
	}
}

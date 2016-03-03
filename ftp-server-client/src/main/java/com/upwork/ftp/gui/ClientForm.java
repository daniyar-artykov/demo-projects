package com.upwork.ftp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.upwork.ftp.FileClient;

public class ClientForm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2442287481161446544L;
	private JPanel panel;
	private JLabel serverAddressLabel;
	private JTextArea serverAddress;
	private JButton upload;
	private JButton download;
	private JLabel msg;
	private JLabel sourceLabel;
	private JTextField source;
	private JLabel destinationLabel;
	private JTextField destination;


	public ClientForm() {
		// set default size of the client form
		this.setSize(350, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// set title for client form
		this.setTitle("Client");

		// add main panel to client form
		panel = new JPanel();
		panel.setLayout(null);

		this.add(panel);

		serverAddressLabel = new JLabel("Server");
		serverAddressLabel.setBounds(7, 5, 90, 30);
		panel.add(serverAddressLabel);

		// server address input, default is "127.0.0.1:23111"
		serverAddress = new JTextArea("127.0.0.1:23111");
		serverAddress.setBounds(105, 5, 150, 30);
		panel.add(serverAddress);

		sourceLabel = new JLabel("Source");
		sourceLabel.setBounds(5, 40, 80, 30);
		panel.add(sourceLabel);

		source = new JTextField("server.txt");
		source.setBounds(105, 40, 150, 30);
		panel.add(source);

		destinationLabel = new JLabel("Destination");
		destinationLabel.setBounds(5, 80, 80, 30);
		panel.add(destinationLabel);

		destination = new JTextField("from-server.txt");
		destination.setBounds(105, 80, 150, 30);
		panel.add(destination);

		// upload button
		upload = new JButton("Upload");
		upload.setBounds(5, 120, 95, 30);
		panel.add(upload);

		// download button
		download = new JButton("Download");
		download.setBounds(105, 120, 95, 30);
		panel.add(download);

		msg = new JLabel();
		msg.setBounds(5, 160, 280, 30);
		panel.add(msg);

		upload.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if(validateFields()) {
					String[] address = serverAddress.getText().split(":");
					String rsp = FileClient.upload(address[0], Integer.parseInt(address[1]), source.getText(), destination.getText());
					msg.setText(rsp);
				}
			}
		});

		download.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				if(validateFields()) {
					String[] address = serverAddress.getText().split(":");
					String rsp = FileClient.download(address[0], Integer.parseInt(address[1]), source.getText(), destination.getText());
					msg.setText(rsp);
				}
			}
		});
	}

	public boolean validateFields() {
		if(serverAddress.getText() == null 
				|| "".equals(serverAddress.getText())) {
			msg.setText("Error: The File address is wrong! Format ip:port!");
			return false;
		} else {
			String[] address = serverAddress.getText().split(":");
			if(address == null || address.length != 2) {
				msg.setText("Error: The File address is wrong! Format ip:port!");
				return false;
			}
		}
		
		if(source.getText() == null 
				|| "".equals(source.getText())) {
			msg.setText("Error: Source file isn't set!");
			return false;
		}
		
		if(destination.getText() == null 
				|| "".equals(destination.getText())) {
			msg.setText("Error: Destination file isn't set!");
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ClientForm sf = new ClientForm();
				sf.setVisible(true);
			}
		});
	}
}

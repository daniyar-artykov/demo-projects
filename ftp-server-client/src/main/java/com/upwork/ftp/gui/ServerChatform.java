package com.upwork.ftp.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerChatform extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2146652485612759413L;
	JPanel panel;
	JTextField NewMsg;
	JTextArea ChatHistory;
	JButton Send;
	
	public ServerChatform() throws UnknownHostException, IOException {

		panel = new JPanel();
		NewMsg = new JTextField();
		ChatHistory = new JTextArea();
		Send = new JButton("Send");
		this.setSize(500, 500);
		this.setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel.setLayout(null);
		this.add(panel);
		ChatHistory.setBounds(20, 20, 450, 360);
		panel.add(ChatHistory);
		NewMsg.setBounds(20, 400, 340, 30);
		panel.add(NewMsg);
		Send.setBounds(375, 400, 95, 30);
		panel.add(Send);
		this.setTitle("Server");
		ChatHistory.setText("Waiting for Client");
		ChatHistory.setText(ChatHistory.getText() + 'n' + "Client Found");
	}

	public static void main(String[] args) throws UnknownHostException,
	IOException {
		new ServerChatform();
	}
}
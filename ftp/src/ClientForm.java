import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientForm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2442287481161446544L;
	private JPanel topPanel;
	private JLabel serverAddressLabel;
	private JTextArea serverAddress;

	private JTabbedPane tabbedPane;
	private JPanel uploadPanel;
	private JPanel downloadPanel;

	private JButton upload;
	private JButton download;

	private JLabel msg;

	private JLabel uploadSourceLabel;
	private JTextField uploadSource;
	private JButton uploadSourceOpen;
	private JLabel uploadDestinationLabel;
	private JTextField uploadDestination;

	private JLabel downloadSourceLabel;
	private JTextField downloadSource;
	private JLabel downloadDestinationLabel;
	private JTextField downloadDestination;

	public ClientForm() {
		// set default size of the client form
		this.setSize(457, 320);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// set title for client form
		this.setTitle("Client");

		// add main panel to client form
		topPanel = new JPanel();
		topPanel.setLayout(null);

		this.add(topPanel);

		serverAddressLabel = new JLabel("Server");
		serverAddressLabel.setBounds(12, 5, 90, 30);
		topPanel.add(serverAddressLabel);

		// server address input, default is "127.0.0.1:23111"
		serverAddress = new JTextArea("127.0.0.1:23111");
		serverAddress.setBounds(105, 5, 150, 30);
		topPanel.add(serverAddress);

		// Create a tabbed pane
		tabbedPane = new JTabbedPane();

		uploadPanel = new JPanel();
		uploadPanel.setLayout(null);

		downloadPanel = new JPanel();
		downloadPanel.setLayout(null);

		tabbedPane.addTab("Upload", uploadPanel);
		tabbedPane.addTab("Download", downloadPanel);

		tabbedPane.setBounds(5, 40, 450, 160);

		topPanel.add(tabbedPane);

		uploadSourceLabel = new JLabel("Source");
		uploadSourceLabel.setBounds(5, 5, 80, 30);
		uploadPanel.add(uploadSourceLabel);

		uploadSource = new JTextField();
		uploadSource.setEnabled(false);
		uploadSource.setBounds(105, 5, 200, 30);
		uploadPanel.add(uploadSource);

		uploadSourceOpen = new JButton("Browse...");
		uploadSourceOpen.setBounds(310, 5, 100, 30);
		uploadPanel.add(uploadSourceOpen);

		uploadDestinationLabel = new JLabel("Destination");
		uploadDestinationLabel.setBounds(5, 40, 80, 30);
		uploadPanel.add(uploadDestinationLabel);

		uploadDestination = new JTextField("from-client.txt");
		uploadDestination.setBounds(105, 40, 150, 30);
		uploadPanel.add(uploadDestination);

		// upload button
		upload = new JButton("Upload");
		upload.setBounds(5, 80, 120, 30);
		uploadPanel.add(upload);

		downloadSourceLabel = new JLabel("Source");
		downloadSourceLabel.setBounds(5, 5, 80, 30);
		downloadPanel.add(downloadSourceLabel);

		downloadSource = new JTextField("server.txt");
		downloadSource.setBounds(105, 5, 150, 30);
		downloadPanel.add(downloadSource);

		downloadDestinationLabel = new JLabel("Destination");
		downloadDestinationLabel.setBounds(5, 40, 80, 30);
		downloadPanel.add(downloadDestinationLabel);

		downloadDestination = new JTextField("from-server.txt");
		downloadDestination.setBounds(105, 40, 150, 30);
		downloadPanel.add(downloadDestination);

		// download button
		download = new JButton("Download");
		download.setBounds(5, 80, 120, 30);
		downloadPanel.add(download);

		msg = new JLabel();
		msg.setBounds(5, 200, 280, 30);
		topPanel.add(msg);

		uploadSourceOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				int rVal = c.showOpenDialog(ClientForm.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					uploadSource.setText(c.getCurrentDirectory().toString() + "/" + c.getSelectedFile().getName());
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					uploadSource.setText("");
				}
			}
		});

		upload.addActionListener(new ActionListener() { 

			@Override
			public void actionPerformed(ActionEvent e) {
				if(validateUploadFields()) {
					String[] address = serverAddress.getText().split(":");
					String rsp = FileClient.upload(address[0], Integer.parseInt(address[1]), uploadSource.getText(), uploadDestination.getText());
					msg.setText(rsp);
				}
			}
		});

		download.addActionListener(new ActionListener() { 

			@Override
			public void actionPerformed(ActionEvent e) { 
				if(validateDownloadFields()) {
					String[] address = serverAddress.getText().split(":");
					String rsp = FileClient.download(address[0], Integer.parseInt(address[1]), downloadSource.getText(), downloadDestination.getText());
					msg.setText(rsp);
				}
			}
		});
	}

	public boolean validateUploadFields() {
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

		if(uploadSource.getText() == null 
				|| "".equals(uploadSource.getText())) {
			msg.setText("Error: Source file isn't set!");
			return false;
		}

		if(uploadDestination.getText() == null 
				|| "".equals(uploadDestination.getText())) {
			msg.setText("Error: Destination file isn't set!");
			return false;
		}

		return true;
	}

	public boolean validateDownloadFields() {
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

		if(downloadSource.getText() == null 
				|| "".equals(downloadSource.getText())) {
			msg.setText("Error: Source file isn't set!");
			return false;
		}

		if(downloadDestination.getText() == null 
				|| "".equals(downloadDestination.getText())) {
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

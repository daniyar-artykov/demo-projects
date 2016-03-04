import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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
	private  JProgressBar uploadProgressBar;
	private UploadTask uploadTask;

	private JLabel downloadSourceLabel;
	private JTextField downloadSource;
	private JLabel downloadDestFoldeLabel;
	private JTextField downloadDestFolder;
	private JButton downloadDestFolderOpen;
	private JLabel downloadDestinationLabel;
	private JTextField downloadDestination;
	private  JProgressBar downloadProgressBar;
	private DownloadTask downloadTask;

	public ClientForm() {
		// set default size of the client form
		this.setSize(487, 350);
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

		tabbedPane.setBounds(5, 40, 452, 200);

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
		upload.setBounds(5, 120, 120, 30);
		uploadPanel.add(upload);

		uploadProgressBar = new JProgressBar();
		uploadProgressBar.setMinimum(0);
		uploadProgressBar.setMaximum(100);
		uploadProgressBar.setBounds(130, 120, 120, 30);
		uploadProgressBar.setStringPainted(true);
		uploadPanel.add(uploadProgressBar);

		downloadSourceLabel = new JLabel("Source");
		downloadSourceLabel.setBounds(5, 5, 120, 30);
		downloadPanel.add(downloadSourceLabel);

		downloadSource = new JTextField("server.txt");
		downloadSource.setBounds(125, 5, 150, 30);
		downloadPanel.add(downloadSource);

		downloadDestFoldeLabel = new JLabel("Destination Folder");
		downloadDestFoldeLabel.setBounds(5, 40, 120, 30);
		downloadPanel.add(downloadDestFoldeLabel);

		downloadDestFolder = new JTextField();
		downloadDestFolder.setBounds(125, 40, 200, 30);
		downloadDestFolder.setEnabled(false);
		downloadPanel.add(downloadDestFolder);

		downloadDestFolderOpen = new JButton("Browse...");
		downloadDestFolderOpen.setBounds(335, 40, 100, 30);
		downloadPanel.add(downloadDestFolderOpen);

		downloadDestinationLabel = new JLabel("Destination File");
		downloadDestinationLabel.setBounds(5, 80, 120, 30);
		downloadPanel.add(downloadDestinationLabel);

		downloadDestination = new JTextField("from-server.txt");
		downloadDestination.setBounds(125, 80, 150, 30);
		downloadPanel.add(downloadDestination);

		// download button
		download = new JButton("Download");
		download.setBounds(5, 120, 120, 30);
		downloadPanel.add(download);

		downloadProgressBar = new JProgressBar();
		downloadProgressBar.setMinimum(0);
		downloadProgressBar.setMaximum(100);
		downloadProgressBar.setBounds(130, 120, 120, 30);
		downloadProgressBar.setStringPainted(true);
		downloadPanel.add(downloadProgressBar);

		msg = new JLabel();
		msg.setBounds(5, 250, 280, 30);
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

		downloadDestFolderOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				c.setCurrentDirectory(new java.io.File("."));
				c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				c.setAcceptAllFileFilterUsed(false);

				int rVal = c.showOpenDialog(ClientForm.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					downloadDestFolder.setText(c.getCurrentDirectory().toString() + "/" + c.getSelectedFile().getName());
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					downloadDestFolder.setText("");
				}
			}
		});

		upload.addActionListener(new ActionListener() { 

			@Override
			public void actionPerformed(ActionEvent e) {
				upload.setEnabled(false);
				if(validateUploadFields()) {
					uploadTask = new UploadTask();
					uploadTask.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress" == evt.getPropertyName()) {
								int progress = (Integer) evt.getNewValue();
								uploadProgressBar.setValue(progress);
							}
						}
					});
					uploadTask.execute();
				} else {
					upload.setEnabled(true);
				}
			}
		});

		download.addActionListener(new ActionListener() { 

			@Override
			public void actionPerformed(ActionEvent e) { 
				download.setEnabled(false);
				if(validateDownloadFields()) {
					downloadTask = new DownloadTask();
					downloadTask.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress" == evt.getPropertyName()) {
								int progress = (Integer) evt.getNewValue();
								downloadProgressBar.setValue(progress);
							}
						}
					});
					downloadTask.execute();
				} else {
					download.setEnabled(true);
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

	class UploadTask extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(10);
			sleep(500);
			setProgress(20);
			String[] address = serverAddress.getText().split(":");
			setProgress(30);
			sleep(500);
			setProgress(40);
			Integer port = Integer.parseInt(address[1]);
			setProgress(50);
			sleep(500);
			setProgress(60);
			sleep(500);
			setProgress(70);
			String rsp = FileClient.upload(address[0], port, uploadSource.getText(), 
					uploadDestination.getText());
			setProgress(80);
			sleep(500);
			setProgress(90);
			sleep(500);
			setProgress(100);
			msg.setText(rsp);

			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			upload.setEnabled(true);
		}

		public void sleep(long mls) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class DownloadTask extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			setProgress(10);
			sleep(500);
			setProgress(20);
			sleep(500);
			setProgress(30);
			sleep(500);
			setProgress(40);
			String[] address = serverAddress.getText().split(":");
			setProgress(50);
			String folder = downloadDestFolder.getText();
			String file = downloadDestination.getText();
			
			if(folder != null && folder.length() > 0) {
				file = folder + "/" + file;
			}
			setProgress(60);
			sleep(500);
			setProgress(70);
			String rsp = FileClient.download(address[0], Integer.parseInt(address[1]), 
					downloadSource.getText(), file);
			setProgress(80);
			sleep(500);
			setProgress(90);
			sleep(500);
			setProgress(100);
			msg.setText(rsp);

			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			download.setEnabled(true);
		}

		public void sleep(long mls) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

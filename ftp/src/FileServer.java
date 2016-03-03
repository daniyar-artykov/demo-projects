import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The file server program.
 *
 */
public class FileServer extends NetCommon {

	// the port and socket of the server
	private ServerSocket server;
	private List<ClientWorker> clients = new ArrayList<ClientWorker>();
	private static boolean serverStarted = false;

	/**
	 * Create the server socket to accept clients at the given port.
	 * @param port
	 */
	public FileServer(int port) {
		try {
			server = new ServerSocket(port);
			server.setReuseAddress(true);
			System.out.println("The File Server is running at port " + port);
			//		System.out.println("Press Ctrl-D to terminate.\n");
			serverStarted = true;
		} catch(Exception e) {
			System.err.println("Error: could not start the server at port " +  port + "\n" + e.getMessage());
		}
	}


	/**
	 * Listen on the specified port and accept clients.
	 * For each client, a new thread is created to handle it. 
	 */
	public void accept() {
		while (!server.isClosed()) {
			try {
				Socket client = server.accept();
				InetAddress addr = client.getInetAddress();
				System.out.println("Info: new client from " + addr.getHostName());

				// create a new thread to handle this client
				ClientWorker cw = new ClientWorker(client);

				clients.add(cw);

			} catch (IOException e) {
				System.out.println("Warning: " + e.getMessage());
			}
		}
	}

	/**
	 * Stop server and all waiting connect threads
	 */
	public void stop() {
		try {
			if(!serverStarted) {
				return;
			}
			// stop server
			server.close();
			serverStarted = false;
			System.out.println("The File Server stopped");
			// stop all waiting connect threads
			for(ClientWorker client : clients) {
				client.shutdown();
			}
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}

	// the internal thread class to handle one client.
	private class ClientWorker implements Runnable {
		final Socket client;
		final Thread thread; // the thread instance
		final String name;   // the name of the client.
		private boolean shutdown;

		public ClientWorker(Socket client) {
			this.client = client;
			this.name = client.getInetAddress().getHostName();
			this.thread = new Thread(this);
			this.thread.start();
		}

		@Override
		public void run() {
			if(!shutdown) {
				try {
					// get the input and output stream for the client
					InputStream input = client.getInputStream();
					OutputStream output = client.getOutputStream();

					// read the file request from the client
					String request = readString(input);

					System.out.println("Info: client " + name + " request: " + request);

					// check which type of request it is
					String[] fields = request.split("\\s+");
					if (fields.length != 2
							|| !Arrays.asList("upload", "download").contains(fields[0])) {
						System.out.println("Warning: could not understand request: " + request);
					} else {
						String fileName = fields[1];

						if ("upload".equals(fields[0])) {
							// try to upload the file, get the file size.
							upload(input, output, fileName);
						} else {
							// try to download the file
							download(input, output, fileName);
						}
					}
					// shutdown the client
					client.close();
					System.out.println("Info: client " + name + " closed.");
				} catch (IOException e) {
					System.out.println(
							"Warning: client " + name + " shutdown unexpectely");
				}
			}
		}

		public void shutdown() {
			shutdown = true;
		}
	}

	// the client upload the file to the server
	private void upload(InputStream input, OutputStream output, String fileName) 
			throws IOException
	{
		// make sure the server can save the file
		try {
			FileOutputStream fout = new FileOutputStream(new File(fileName));
			// send an OK message to the client
			// and read the following bytes from the client to the file
			sendString(output, "OK");
			redirect0(input, fout);
			fout.close();
		} catch (IOException e) {
			System.out.println("Warning: could not save into " + fileName);
			// send a FAIL message to the client
			sendString(output, "FAIL");
		}
	}

	// the client upload the file to the server
	private void download(InputStream input, OutputStream output, String fileName) 
			throws IOException
	{
		// make sure the server can read the file
		try {
			FileInputStream fin = new FileInputStream(new File(fileName));
			// send an OK message to the client
			// and read the following bytes from the client to the file
			sendString(output, "OK");
			redirect1(fin, output);
			fin.close();
		} catch (IOException e) {
			System.out.println("Warning: could not read " + fileName);
			// send a FAIL message to the client
			sendString(output, "FAIL");
		}		
	}

	public boolean isServerStarted() {
		return serverStarted;
	}
}

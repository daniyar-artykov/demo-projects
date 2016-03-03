import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileClient extends NetCommon {

	/**
	 * update the local file (source file) to the given server
	 * and save as (destin file) on the server side.
	 * @param ip
	 * @param port
	 * @param sourceFile
	 * @param destinFile
	 */
	public static String upload(String ip, int port, String sourceFile, String destinFile) {
		String returnString = null;
		try {
			// make sure the local file exists
			FileInputStream fin = new FileInputStream(new File(sourceFile));
			// connect with the server
			Socket client = new Socket(ip, port);

			// get the input and output stream for the client
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();

			// send the request to the server
			sendString(output, "upload " + destinFile);

			// read the response from the server
			String response = readString(input);
			if ("OK".equals(response)) {
				// server confirms it's okay to send the file
				// send the file to the server
				redirect1(fin, output);
			} else {
				// could not save the file due to permission issue
				System.out.println("Error: permission denied.");
				returnString = "Error: permission denied.";
			}
			client.close();
			fin.close();
			if(returnString == null) {
				System.out.println("Done! Saved as " + destinFile);
				returnString = "Done! Saved as " + destinFile;
			}
		} catch (IOException e) {
			System.out.println("Error: could not upload file " + sourceFile);
			returnString = "Error: could not upload file " + sourceFile;
		}

		return returnString;
	}


	/**
	 * download the remote file (source file) from the given server
	 * and save as (destin file) on the local side.
	 * @param ip
	 * @param port
	 * @param sourceFile
	 * @param destinFile
	 */
	public static String download(String ip, int port, String sourceFile, String destinFile) {
		String returnString = null;
		try {
			// make sure the client can save this file
			FileOutputStream fout = new FileOutputStream(new File(destinFile));
			// connect with the server
			Socket client = new Socket(ip, port);

			// get the input and output stream for the client
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();

			// send the request to the server
			sendString(output, "download " + sourceFile);

			// read the response from the server
			String response = readString(input);
			if ("OK".equals(response)) {
				// server confirms it's okay to download the file
				// download the file from the server
				redirect0(input, fout);
			} else {
				// could not download the file due to permission issue
				System.out.println("Error: no such file or permission denied.");
				returnString = "Error: no such file or permission denied.";
			}
			client.close();
			fout.close();
			if(returnString == null) {
				System.out.println("Done! Saved as " + destinFile);
				returnString = "Done! Saved as " + destinFile;
			}
		} catch (IOException e) {
			System.out.println("Error: could not download file " + sourceFile);
			returnString = "Error: could not download file " + sourceFile;
		}

		return returnString;
	}
}

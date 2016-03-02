package com.upwork.ftp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class NetCommon {

	/**
	 * Read the byte stream from the input and write to the output.
	 * The input stream could contain multiple sections each starts
	 * with the length of the stream in 4 bytes then the data itself.
	 * A section with length 0 is the end of the input stream.
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void redirect0(InputStream input, OutputStream output) 
			throws IOException
	{
		byte[] data = new byte[1024];
		
		while (true) {
			// read length of next section
			// if length = 0, it is the end of the stream.
			int length = readLength(input);
			if (length == 0) break;
			
			// read the data section followed
			if (length > data.length) {
				data = new byte[length];
			}
			if (readBytes(input, data, length) != length) {
				throw new IOException("the input stream is corrupted.");
			}
			// save the data into the output stream
			output.write(data, 0, length);
		}
	}
	
	/**
	 * Read the byte stream from the input and write to the output.
	 * Read until EOF of input stream.
	 * The output stream could contain multiple sections each starts
	 * with the length of the stream in 4 bytes then the data itself.
	 * A section with length 0 is to notify the end of the data.
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void redirect1(InputStream input, OutputStream output) 
			throws IOException
	{
		byte[] data = new byte[1024];
		
		while (true) {
			int bytes = input.read(data);
			if (bytes < 0) {
				// the end of the input stream
				break;
			}
			// send the data to the output stream 
			sendBytes(output, data, bytes);
		}
		// send data of length 0 to indicate
		// the end of the data stream. 
		sendBytes(output, data, 0);
	}
	
	/**
	 * Read a string from the input stream.
	 * The input stream should have the length of the string in 4 bytes
	 * followed by the UTF-8 bytes of the string of the length.
	 * 
	 * @param input
	 * @return the string
	 * @throws IOException
	 */
	public static String readString(InputStream input) throws IOException {
		// read the length of the string
		int length = readLength(input);
		byte[] data = new byte[length];
		if (readBytes(input, data, length) != length) {
			throw new IOException("the input stream is corrupted.");
		}
		// convert the bytes back to string
		return new String(data, "UTF-8");
	}
	
	/**
	 * Send the string to the output stream.
	 * It should send the length of the UTF-8 bytes of the string in
	 * 4 bytes followed by the UTF-8 bytes of the string.
	 * @param output
	 * @throws IOException
	 */
	public static void sendString(OutputStream output, String value) 
		throws IOException
	{
		//using UTF-8 to convert any unicode characters
		byte[] data = value.getBytes("UTF-8");
		// send the data with length
		sendBytes(output, data, data.length);
	}
	
	/**
	 * Send the byte array to the output stream.
	 * It should send the length of the array in 4 bytes followed
	 * by the array itself.
	 * @param output
	 * @param value
	 * @param length
	 * @throws IOException
	 */
    public static void sendBytes(OutputStream output, byte[] value, int length)
    	throws IOException
    {
    	// convert the length into byte array to send
    	byte[] data = new byte[4];
    	data[0] = (byte)((length >> 24) & 0xff);
    	data[1] = (byte)((length >> 16) & 0xff);
    	data[2] = (byte)((length >> 8) & 0xff);
    	data[3] = (byte)((length >> 0) & 0xff);
        output.write(data);
        // send the data
        output.write(value, 0, length);
    }
    
    // a helper function to read 4 bytes from the input stream and
    // convert it to a non-negative integer.
    private static int readLength(InputStream input) throws IOException {
    	byte[] data = new byte[4];
		if (readBytes(input, data, data.length) != data.length) {
			throw new IOException("the input stream is corrupted.");
		}
				
		// now convert the byte into length of the array.
		int length = (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | data[3];
		if (length < 0) {
			throw new IOException("the input stream is corrupted.");
		}
		return length;
    }
	
	/**
	 * Read bytes up to capacity from the input stream into the given array.
	 * @param input
	 * @param data
	 * @param capacity
	 * @return the number of bytes read
	 * @throws IOException
	 */
	private static int readBytes(InputStream input, byte[] data, int capacity) 
		throws IOException
	{
		int bytes = 0;
		while (bytes < capacity) {
			// try to read (capacity - bytes) length of data
			int size = input.read(data, bytes, capacity - bytes);
			if (size < 0) {
				// end of file has met before the data is done.
				break;
			}
			bytes = bytes + size;
		}
		return bytes;
	}
}

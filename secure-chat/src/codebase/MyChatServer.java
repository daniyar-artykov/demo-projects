package codebase;

import infrastructure.ChatServer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.codec.binary.Base64;

import codebase.util.CertificateManager;

/**
 * ChatServer implements the fundamental communication capabilities for your
 * server, but it does not take care of the semantics of the payload it carries.
 * 
 * Here MyChatServer (of your choice) extends it and implements the actual
 * server-side protocol. It must be replaced with/adapted for your designed
 * protocol.
 *
 */
class MyChatServer extends ChatServer {

	/** A Json array loaded from disk file storing plaintext uids and pwds. */
	JsonArray database;

	/**
	 * Client login status; "" indicates not logged in or otherwise is set to
	 * uid.
	 **/
	String statA = "";
	String statB = "";
	private PublicKey publicKeyA = null;
	private PublicKey publicKeyB = null;

	private RSAPrivateKey privateKey = null;

	// In Constructor, the user database is loaded.
	MyChatServer() {
		try {
			InputStream in = new FileInputStream("database.json");
			JsonReader jsonReader = Json.createReader(in);
			database = jsonReader.readArray();
			privateKey = CertificateManager.getPrivateKey(new File("resources/keys/server_prv.pk8"));
		} catch (FileNotFoundException e) {
			System.err.println("Database file not found!");
			System.exit(-1);
		}
	}

	/**
	 * Methods invoked by the network stack
	 */

	/**
	 * Overrides the function in ChatServer Whenever a packet is received this
	 * method is called and IsA indicates whether it is from A (or B) with the
	 * byte array of the raw packet
	 */
	public void PacketReceived(boolean IsA, byte[] buf) {
		ByteArrayInputStream is = new ByteArrayInputStream(buf);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(is);
			Object o = in.readObject();
			ChatPacket p = (ChatPacket) o;

			if (p.request == ChatRequest.LOGIN_STEP_1) { //if client request encrypted salt

				//				System.out.println("SERVER LOGIN_STEP_1");
				// We want to go through all records
				for (int i = 0; i < database.size(); i++) {

					JsonObject l = database.getJsonObject(i);
					// When uid match
					if (l.getString("uid").equals(p.uid)) {
						// We do not allow one user to be logged in on multiple
						// clients
						if (p.uid.equals(IsA ? statB : statA))
							continue;

						//						System.out.println("salt: " + l.getString("salt"));

						// Inform the client that salt found and send it
						RespondtoClient(IsA, p.uid, "LOGIN_STEP_1", l.getString("salt"), ChatRequest.RESPONSE);

						break;
					}

				}

			} else if (p.request == ChatRequest.LOGIN) {
				//				System.out.println("SERVER LOGIN");
				// We want to go through all records
				for (int i = 0; i < database.size(); i++) {

					JsonObject l = database.getJsonObject(i);	
					// When both uid and pwd match
					if (l.getString("uid").equals(p.uid)
							&& l.getString("password").equals(p.password)) {
						// We do not allow one user to be logged in on multiple
						// clients
						if (p.uid.equals(IsA ? statB : statA))
							continue;

						// Update the corresponding login status
						if (IsA) {
							statA = l.getString("uid");
						} else {
							statB = l.getString("uid");
						}

						// Update the UI to indicate this
						UpdateLogin(IsA, l.getString("uid"));

						// Inform the client that it was successful
						RespondtoClient(IsA, "LOGIN");

						break;
					}
				}

				if ((IsA ? statA : statB).equals("")) {
					// Oops, this means a failure, we tell the client so
					RespondtoClient(IsA, "");
				}
			} else if (p.request == ChatRequest.LOGOUT) {
				if (IsA) {
					statA = "";
				} else {
					statB = "";
				}
				UpdateLogin(IsA, "");
				RespondtoClient(IsA, "LOGOUT");

			} else if (p.request == ChatRequest.CHAT) {
				// This is a chat message
				// Whoever is sending it must be already logged in
				if (((IsA && statA != "") || (!IsA && statB != ""))) {
					byte []decrypted = CertificateManager.decrypt(privateKey, p.data);
					System.out.println("decrypted msg: " + new String(decrypted).trim());
					String decryptedStr = new String(decrypted).trim();
					
					PublicKey currentPk = !IsA ? publicKeyA : publicKeyB;
					if(currentPk != null) { // check that receiver also have selected private key and certificate
						byte []encrypted = CertificateManager.encrypt(currentPk, decryptedStr.getBytes("UTF-8"));
						p.data = encrypted;
						// Forward the encrypted packet to the recipient
						SerializeNSend(!IsA, p);

						p.request = ChatRequest.CHAT_ACK;
						p.uid = (IsA ? statB : statA);

						// encrypt decrypted msg using sender public key, to send back to sender
						encrypted = CertificateManager.encrypt(IsA ? publicKeyA : publicKeyB, decrypted);
						p.data = encrypted;
						// Flip the uid and send it back to the sender for updating
						// chat history
						SerializeNSend(IsA, p);
					} else { // notify
						System.err.println("receiver also need to select private key and certificate to receive messages!");
					}
				}
			} else if (p.request == ChatRequest.REQUEST_PUBLIC_KEY) { // client requested server public key
				try {
					// Update the corresponding login status
					if (IsA) {
						statA = p.uid;
					} else {
						statB = p.uid;
					}

					// get client certificate
					X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(p.success));
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					if(IsA) {
						publicKeyA = keyFactory.generatePublic(spec);
					} else {
						publicKeyB = keyFactory.generatePublic(spec);
					}
					String pk = CertificateManager.getPublicKeyString("resources/keys/server_pub.pem");
					// Update the UI to indicate this
					UpdateLogin(IsA, p.uid);
					// sending public key to client
					RespondtoClient(IsA, p.uid, pk, null, ChatRequest.RESPONSE_PUBLIC_KEY);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methods for updating UI
	 */

	// You can use this.UpdateServerLog("anything") to update the TextField on
	// the server portion of the UI
	// when needed

	/**
	 * Methods invoked locally
	 */

	/**
	 * This method serializes (into byte[] representation) a Java object
	 * (ChatPacket) and sends it to the corresponding recipient (A or B)
	 */
	private void SerializeNSend(boolean IsA, ChatPacket p) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(os);
			out.writeObject(p);
			byte[] packet = os.toByteArray();
			SendtoClient(IsA, packet);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * This method composes the packet needed to respond to a client (indicated
	 * by IsA) regarding whether the login/logout request was successful
	 * p.success would be "" if failed or "LOGIN"/"LOGOUT" respectively if
	 * successful
	 */
	void RespondtoClient(boolean IsA, String Success) {
		ChatPacket p = new ChatPacket();
		p.request = ChatRequest.RESPONSE;
		p.uid = IsA ? statA : statB;
		p.success = Success;

		SerializeNSend(IsA, p);
	}

	void RespondtoClient(boolean IsA, String uid, String Success, String salt, ChatRequest chatRequest) {
		ChatPacket p = new ChatPacket();
		p.request = chatRequest;
		p.uid = uid;
		p.success = Success;
		p.salt = salt;

		SerializeNSend(IsA, p);
	}
}

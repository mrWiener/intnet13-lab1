package core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket socket;
	
	Client(String host, int port) throws UnknownHostException, IOException {
		System.out.println("Connecting to " + host + " on port " + port + "...");
		
		socket = new Socket(host, port);
		
		System.out.println("Connected!");
		
		Reader reader = new Reader();
		reader.run(socket);
		
		Writer writer = new Writer();
		writer.run(socket);
		
		while(socket.isConnected()) {
			//do nothing
		}
		
		System.out.println("Closing...");
		socket.close();
	}
	
	public static void main(String [] args) {		
		try {
			new Client(args[0], Integer.parseInt(args[1]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Writer extends Thread {
		public void run(Socket socket) throws IOException {
			DataOutputStream writeStream = new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			while(true) {
				writeStream.writeUTF(in.readLine());
				System.out.println();
			}
		}
	}
	
	private class Reader extends Thread {
		public void run(Socket socket) throws IOException {
			DataInputStream readStream = new DataInputStream(socket.getInputStream());
			
			while(true) {
				System.out.println(readStream.readUTF());
			}
		}
	}
}
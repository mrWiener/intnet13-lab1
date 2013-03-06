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
	
	private static int PORT = 8888;
	private static String HOST = "localhost";
	
	Client(String host, int port) throws UnknownHostException, IOException {
		System.out.println("Connecting to " + host + " on port " + port + "...");
		
		socket = new Socket(host, port);
		
		System.out.println("Connected!");
		
		Thread reader = new Thread(new Reader(socket), "reader");
		reader.start();
		
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream writeStream = new DataOutputStream(socket.getOutputStream());
		
		String input = null;
		while(socket.isConnected()) {
	        input = consoleInput.readLine();

			writeStream.writeUTF(input);
		}
		
		System.out.println("Closing...");
		socket.close();
	}
	
	public static void main(String [] args) {
		try {
			if(args.length != 2){
				new Client(HOST, PORT);
			} else{
				new Client(args[0], Integer.parseInt(args[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class Reader extends Thread {
		private Socket socket;
		
		Reader(Socket socket) {
			this.socket = socket;
		}
		
		Reader(String threadName) {
			super(threadName); // Initialize thread.
			System.out.println(this);
			start();
		}
		
		public void run() {
			
			DataInputStream readStream;
			try {
				readStream = new DataInputStream(socket.getInputStream());
				
				while(true) {
					System.out.println(readStream.readUTF());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
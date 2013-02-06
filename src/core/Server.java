package core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ServerSocket serverSocket;
	private ArrayList<Socket> clients;
	
	
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clients = new ArrayList<Socket>();	
	}
	
	public void run() throws IOException, InterruptedException {
		System.out.println("Listening on port 8888...");
		
		clients.add(serverSocket.accept());
		System.out.println("Client connetected with IP: " + clients.get(0).getRemoteSocketAddress());
		
		DataOutputStream writeStream = new DataOutputStream(clients.get(0).getOutputStream());
		DataInputStream readStream = new DataInputStream(clients.get(0).getInputStream());
		
		Reader reader = new Reader();
		reader.run(clients.get(0), clients);
		
		while(true) {
			Thread.sleep(1000);
			writeStream.writeUTF("Hejsan din fula klient");
		}
		
		//System.out.println(readStream.readUTF());
		/*
		System.out.println("Closing...");
		
		clients.get(0).close();
		serverSocket.close();*/
	}
	
	public static void main(String [] args) {
		Server server;
		
		try {
			server = new Server(8888);
			server.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Reader extends Thread {
		public void run(Socket socket, ArrayList<Socket> clients) throws IOException {
			DataInputStream readStream = new DataInputStream(socket.getInputStream());
			DataOutputStream writeStream;
			String message;
			
			while(socket.isConnected()) {	
				
				try{
					message = readStream.readUTF();

					System.out.println("Received: " + message);
					
					//send incoming messages to other clients.
					for(Socket client : clients) {
						if(client.isClosed()) {
							//disconnect
							continue;
						}
						
						if(!client.equals(socket) || true) {
							writeStream = new DataOutputStream(client.getOutputStream());
							writeStream.writeUTF(message);
						}
					}
				} catch(Exception e) {
					//client probably disconnected
				}	
			}
			
			//client disconnected.
		}
	}
}
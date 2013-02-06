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
		
		while(true) {
			//Accept incoming clients
			clients.add(serverSocket.accept());
			System.out.println("Client connetected with IP: " + clients.get(clients.size()-1).getRemoteSocketAddress());
			System.out.println("Clients: " + clients.size());
			
			//Spawn a new reader thread for each connected client
			Thread reader = new Thread(new Reader(clients.get(clients.size()-1), clients), "reader-client-0");
			reader.start();
		}
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
		private Socket socket;
		private ArrayList<Socket> clients;
		
		Reader(Socket socket, ArrayList<Socket> clients) {
			this.socket = socket;
			this.clients = clients;
		}
		
		Reader(String threadName) {
			super(threadName); // Initialize thread.
			System.out.println(this);
			start();
		}
		
		public void run() {
			try {
				DataInputStream readStream = new DataInputStream(socket.getInputStream());
			
				DataOutputStream writeStream;
				String message;
				
				while(socket.isConnected()) {	
					message = readStream.readUTF();

					System.out.println("Received: " + message);
					
					//send incoming messages to other clients.
					for(Socket client : clients) {
						if(client.isClosed()) {
							//disconnect
							clients.remove(client);
							System.out.println("Client disconnected");
							System.out.println("Clients: " + clients.size());
							continue;
						}
						
						if(!client.equals(socket)) {
							writeStream = new DataOutputStream(client.getOutputStream());
							writeStream.writeUTF(message);
						}
					}
				}
			} catch (Exception e) {
				//something wrong.
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				clients.remove(socket);
				System.out.println("Client hard disconnected");
				System.out.println("Clients: " + clients.size());
			}
		}
	}
}
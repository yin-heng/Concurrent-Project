package project.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.entities.Player;
import project.main.Game_Server;

public class Dispatcher implements Runnable {
	
	private Socket dataSocket;
	private Socket echoSocket;
	
	private Scanner dataIn;
	private PrintWriter dataOut;
	
	private Scanner echoIn;
	private PrintWriter echoOut;
	
	
	private Player player;
	
	private Client client;

	@Override
	public void run() {

		dispatch();
		
	}

	private void dispatch() {
		while(true) {
			if(listen()) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						Handler handle = new Handler(client);
						if(handle.register()) {
							handle.handle();
						} else {
							handle.disconnect();
							System.out.println("connection disconnect");
						}
					}
				});
				t.start();
			}
		}
	}

	private boolean listen() {
		try {
			
			dataSocket = Game_Server.dataServerSocket.accept();
			echoSocket = Game_Server.echoServerSocket.accept();
			
			dataOut = new PrintWriter(dataSocket.getOutputStream());
			dataIn = new Scanner(dataSocket.getInputStream());
			
			echoOut = new PrintWriter(echoSocket.getOutputStream());
			echoIn = new Scanner(echoSocket.getInputStream());
			
			client = new Client(dataSocket, echoSocket, dataOut, dataIn, echoOut, echoIn, player);
			
			return true;
		} catch (IOException e) {
			System.out.println("Client not accepted.");
			return false;
		}
	}

}

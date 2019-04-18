package project.network;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.entities.Player;

public class Client {
	
	public Player player;
	
	public Socket dataSocket;
	
	public Socket echoSocket;
	
	public Scanner dataIn;
	public PrintWriter dataOut;
	
	public Scanner echoIn;
	public PrintWriter echoOut;
	
	
	
	public Client(Socket dataSocket,  Socket echoSocket, PrintWriter dataOut,
			Scanner dataIn, PrintWriter echoOut,Scanner echoIn, Player player) {
		
		this.player = player;
		this.dataSocket = dataSocket;
		this.echoSocket = echoSocket;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.echoIn = echoIn;
		this.echoOut = echoOut;
		
		
	}
	
	

}

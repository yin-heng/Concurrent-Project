package project.network;

import java.awt.Color;
import java.awt.Point;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.entities.Player;
import project.entities.Projectile;
import project.main.Game_Main;
import project.util.Constant;

public class Connection {

	public boolean isConnected;
	
	public Client client;

	public void connect() {
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					
					Socket dataSocket = new Socket(Constant.SERVER_IP,Constant.DATA_PORT);
					Socket echoSocket = new Socket(Constant.SERVER_IP, Constant.ECHO_PORT);
					
					PrintWriter dataOut = new PrintWriter(dataSocket.getOutputStream());
					Scanner dataIn = new Scanner(dataSocket.getInputStream());
					
					
					
					PrintWriter echoOut = new PrintWriter(echoSocket.getOutputStream());
					Scanner echoIn = new Scanner(echoSocket.getInputStream());
					
					client = new Client(dataSocket, echoSocket, dataOut, dataIn, echoOut, echoIn, Game_Main.player);
					
					isConnected = true;
					goOnline();
				}catch(Exception e){
					Game_Main.window.connectionTextArea.append("Failed to connect to server!\n");
					isConnected = false;
				}	
			}
		});
		t.start();
	}
	
	public void disconnect() {
		
		try {
			client.dataOut.println(Constant.CMD_DEREGISTERPLAYER);
			client.dataOut.flush();
			client.dataIn.close();
			client.dataOut.close();
			client.echoIn.close();
			client.echoOut.close();
			client.echoSocket.close();
			client.dataSocket.close();
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			
		}
		
	}

	public void goOnline() {
		if(isConnected){
			register();
			echoPosition();
			echoListen();

			Game_Main.window.connectionTextArea.append("Connected to server!\n");
		}
	}
	
	

	private void register() {
		client.dataOut.println(client.player.username);
		client.dataOut.println(client.player.color);
		client.dataOut.flush();
	}

	

	public void echoPosition() {
		if(isConnected) {
			client.dataOut.println(Constant.CMD_UPDATEPOSITION);
			client.dataOut.print(client.player.cPos.x + " ");
			client.dataOut.print(client.player.cPos.y + " \n");
			client.dataOut.flush();
		}
	}
	
	public void echoProjectile(Projectile proj){
		if(isConnected){
			client.dataOut.println(Constant.CMD_UPDATEPROJ);
			client.dataOut.println(proj.getClass().getName().substring(proj.getClass().getName().lastIndexOf(".")+1));
			client.dataOut.print(proj.origin.x + " ");
			client.dataOut.print(proj.origin.y + " ");
			client.dataOut.print(proj.destination.x + " ");
			client.dataOut.print(proj.destination.y + " \n");
			client.dataOut.flush();
		}
	}
	
	private void echoListen() {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				
				try{
					while(true) {
						
						String cmd = client.echoIn.nextLine();
						
						if (cmd.equals(Constant.CMD_DEREGISTERPLAYER)) {
							
							String username = client.echoIn.nextLine();
							
							for(int i = 0; i < Game_Main.players.size(); i++) {
								if(Game_Main.players.get(i).username.equals(username)) {
									Game_Main.players.remove(i);
									break;
								}
							}
							
						} else if (cmd.equals(Constant.CMD_REGISTERPLAYER)) {
							
							String username = client.echoIn.nextLine();
							String color = client.echoIn.nextLine();
							
							Game_Main.players.add(new Player(username, Color.getColor(color, Color.RED), null));
							
						} else if (cmd.equals(Constant.CMD_UPDATEPOSITION)) {
							
							String username = client.echoIn.nextLine();
							int x = client.echoIn.nextInt();
							int y = client.echoIn.nextInt();
							
							for(Player player : Game_Main.players) {
								if(player.username.equals(username)) {
									player.cPos.setLocation(x, y);
									break;
								}
							}
							
							
						} else if (cmd.equals(Constant.CMD_UPDATEPROJ)) {
							
							String username = client.echoIn.nextLine();
							String className = client.echoIn.nextLine();
							int originX = client.echoIn.nextInt();
							int originY = client.echoIn.nextInt();
							int destX = client.echoIn.nextInt();
							int destY = client.echoIn.nextInt();
							
							for(Player player : Game_Main.players) {
								if(player.username.equals(username)) {
									switch(className) {
										case "Projectile":
											player.liveAmmo.add(new Projectile(player, new Point(originX, originY), new Point(destX, destY)));
											break;
										default:
											break;
									}
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
				
			}
			
		});
		t.start();
	}

}

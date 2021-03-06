import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.lang.Integer;


public class Server2 {
	
	public static int portNum = 4445;
	public static int numGames = 3;
	
	// Lobby that adds and moves players around
	public static Lobby lobby = null;
	// opQueue is a queue of strings formatted: "username:operation"
	public static Queue<String> opQueue = null;
	// comms maintains output streams for each logged in player
	public static Hashtable<String, PrintWriter> comms = null;
	// the list of players logged in
	public static Hashtable<String, Player> players = null;
	
	public static void main(String args[]){
		opQueue = new LinkedList<String>();
		comms = new Hashtable<String,PrintWriter>(128);
		players = new Hashtable<String,Player>(128);
		lobby = new Lobby(3, players);
		
		Thread queueProc = new Thread(new Worker(opQueue, comms, lobby));
		Socket playerSocket = null;
		ServerSocket ss2=null;
		queueProc.start();
		try{
			ss2 = new ServerSocket(portNum);
		}
		catch(IOException e){
			e.printStackTrace();
			System.err.println("Server had error opening a ServerSocket.");
		}
		
		System.out.println("Server started.");
		while(true){
			//System.out.println("Inside accepting connections phase...");
			try{
				playerSocket = ss2.accept();
				System.out.println("Connection with a new player established.");
				
				try{
					BufferedReader is = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
					PrintWriter os = new PrintWriter(playerSocket.getOutputStream());
					
					// Create new player thread that can handle its own input and output
					// player thread cannot handle major requests, instead them into the opQueue
					Thread playerT = new Thread(new PlayerThread(is, os, lobby, opQueue, comms));
					playerT.start();
		    	}
		    	catch(IOException e){
		    		System.err.println("IO error with trying to create new output/input stream for new player.");
		    	}
				
			}
			catch(Exception e){
				try {
					// e.printStackTrace();
					System.err.println("Socket exception thrown... Closing player socket...");
					playerSocket.close();
				} catch (IOException e1) {
					// //e1.printStackTrace();
					System.err.println("Error closing the player socket.");
				}
				System.err.println("Connection Error (with accepting players).");
			}
		}

	}

}

class PlayerThread extends Thread {
	public Lobby lobby = null;
	public Player p = null;
	public String line = "";
	public BufferedReader inputStream = null;
	public PrintWriter outputStream = null;
	public Queue<String> opQueue = null;
	public String delims = ":";
	public int check = 0;
	public Hashtable<String,PrintWriter> commsLink = null;
	public volatile boolean running;
	public String gameRoom;
    
    public PlayerThread(BufferedReader is, PrintWriter os, Lobby mainlobby, Queue<String> theQueue, Hashtable<String,PrintWriter> outstreams){
        inputStream = is;
        outputStream = os;
        opQueue = theQueue;
        lobby = mainlobby;
        commsLink = outstreams;
        running = true;
        gameRoom = "0";
        System.out.println("Game room = " + gameRoom);
    }

	public void run() {
    	/* This block provides the signIn and signUP functionality */
		
		while (running) {
			try {
				if (inputStream != null && outputStream != null) {
					line = inputStream.readLine();
					String parts[] = line.split(delims);
					
					System.out.println("PlayerThread: Client said: " + parts[0] + "," + parts[1]);
					
					if ("X".equals(parts[0])) {
						System.out.println("Client attempting to exit");
						if (p != null) {
							lobby.movePlayer(p.name, -1);
							commsLink.remove(p.name, outputStream);
							
							String newPlayerList = lobby.removePlayer(p.name);
							String newOp = "BROADCAST:" + newPlayerList;
							opQueue.add(newOp);
							
							String lobbyInfo = lobby.showLobbyInfo();
							newOp = "BROADCAST:" + lobbyInfo;
							opQueue.add(newOp);
						}
						
						running = false;
					}
					
					//If L:user:pass, player is requesting to login
					else if("LOGIN".equals(parts[0])) {
						check = DBUtils.signIn(parts[1], parts[2]);
						//let client know how SignIn went
						if (check == 0) {
							if (lobby.players.containsKey(parts[1]))
								check = -1;
						}
						
						System.out.println("PlayerThread: Login attempt for: " + parts[1] + " resulted in: " + check);
						outputStream.println("LOGIN:" + check);
						outputStream.flush();
						
						if (check == 0) {
							p = new Player(parts[1]);
							System.out.println("PlayerThread: Adding player '" + parts[1] + "' to Lobby.");
							String playerList = lobby.addPlayer(p);
							commsLink.put(parts[1], outputStream);
							String newOp = "BROADCAST:" + playerList;
							opQueue.add(newOp);
							
							String lobbyInfo = lobby.showLobbyInfo();
							outputStream.println(lobbyInfo);
							outputStream.flush();
						}
					}
					
					//If R:user:pass:email, used for registration
					else if("REGISTER".equals(parts[0])) {
						check = DBUtils.signUp(parts[1], parts[2], parts[3]);
						System.out.println("PlayerThread: Registration attempt for: " + parts[1] + " resulted in: " + check);
						outputStream.println("REGISTER:" + check);
						outputStream.flush();
					}
					
					// IF GAMECHOICE:[location#]
					else if("GAMECHOICE".equals(parts[0])) {
						// Player is choosing a room to enter
						System.out.println("Room chosen = " + parts[1]);
						int gameRoom = Integer.parseInt(parts[1]);
						String newPlayerList = "";
						newPlayerList = lobby.movePlayer(p.name, gameRoom);
						String newOp = "BROADCAST:" + newPlayerList;
						opQueue.add(newOp);
						
						String welcomeMsg = "GAMENOTIFICATION:" + gameRoom + ":Welcome to Game Room " + gameRoom;
						outputStream.println(welcomeMsg);
						outputStream.flush();
						
						String lobbyInfo = lobby.showLobbyInfo();
						newOp = "BROADCAST:" + lobbyInfo;
						opQueue.add(newOp);
						
						String scoresList = lobby.games[gameRoom-1].showScores();
						System.err.println("Printing out scores = " + scoresList);
						scoresList = "BROADCAST:" + scoresList;
						opQueue.add(scoresList);
						
						// Also have to send that player the cards in play in that game
						String cardsOnBoard = lobby.games[gameRoom-1].board.displayBoard();
						cardsOnBoard = "UPDATECARDS:" + Integer.toString(gameRoom) + ":" + cardsOnBoard;
						outputStream.println(cardsOnBoard);
						System.out.println(cardsOnBoard);
						outputStream.flush();
					}
					
					else if("CHAT".equals(parts[0])) {
						System.out.println("Received chat from user '" + p.name + "': " + line);
						String newOp = "BROADCAST:" + line;
						opQueue.add(newOp);
					}
					
					else if("CLAIMSET".equals(parts[0])) {
						System.out.println("Received a set triplet request from user '" + p.name + "': " + line);
						int gameNum = Integer.parseInt(parts[1]);
						int card1 = Integer.parseInt(parts[2]);
						int card2 = Integer.parseInt(parts[3]);
						int card3 = Integer.parseInt(parts[4]);
						String result = lobby.check_set(p.name,gameNum,card1,card2,card3);
						
						if (result != null) {
							if (result.substring(0, 8)!="GAMEOVER"){
								String newOp = "BROADCAST:UPDATECARDS:" + gameNum + ":" + result;
								opQueue.add(newOp);
								
								String gamenotif = "BROADCAST:GAMENOTIFICATION:" + gameNum + ":" + p.name + " found a set! 1 Point for Gryffindor!";
								opQueue.add(gamenotif);
								
								String releaseStr = "BROADCAST:RELEASELOCK" + gameNum;
								opQueue.add(releaseStr);
							}
							else {
								// Game is over, display player with highest score
								String newOp = "BROADCAST:GAMEOVER:" + gameNum + ":" + result.substring(9, result.length());
								opQueue.add(newOp);
								
								String scoresList = lobby.games[gameNum-1].showScores();
								scoresList = "BROADCAST:" + scoresList;
								opQueue.add(scoresList);
								
								String gamenotif = "BROADCAST:GAMENOTIFICATION:" + gameNum + ":" + "Game over! Hope you had fun!";
								opQueue.add(gamenotif);
							}
						}
						
						else {
							// Else player's set claim was not true
							String gamenotif = "BROADCAST:GAMENOTIFICATION:" + gameNum + ":" + p.name + " submitted a false set!";
							opQueue.add(gamenotif);
							
							String releaseStr = "BROADCAST:RELEASELOCK" + gameNum;
							opQueue.add(releaseStr);
							
							String scoresList = lobby.games[gameNum-1].showScores();
							scoresList = "BROADCAST:" + scoresList;
							opQueue.add(scoresList);
						}
					}
					
					else if ("LEAVEROOM".equals(parts[0])) {
						int oldGameNum = p.location;
						
						String newPlayerList = lobby.movePlayer(p.name, 0);
						String newOp = "BROADCAST:" + newPlayerList;
						opQueue.add(newOp);
						
						String lobbyInfo = lobby.showLobbyInfo();
						newOp = "BROADCAST:" + lobbyInfo;
						opQueue.add(newOp);
						
						String scoresList = lobby.games[oldGameNum-1].showScores();
						System.err.println("Printing out scores = " + scoresList);
						scoresList = "BROADCAST:" + scoresList;
						opQueue.add(scoresList);
					}
					
					else if ("SETLOCKREQ".equals(parts[0])) {
						int gameNum = Integer.parseInt(parts[1]);
						String userRequesting = parts[2];
						String newOp = "SETLOCKREQ:" + gameNum + ":" + userRequesting;
						opQueue.add(newOp);
					}
					
					else if ("RELEASELOCK".equals(parts[0])) {
						int gameNum = Integer.parseInt(parts[1]);
						String newOp = "BROADCAST:RELEASELOCK" + gameNum;
						opQueue.add(newOp);
					}
					
					else if ("CARDCLICK".equals(parts[0])) {
						System.out.println("Server saw card clicked = " + line);
						opQueue.add("BROADCAST:" + line);
					}
					
					
					else {
						System.out.println("PlayerThread for '" + p.name + "' moving operation '" + line + "' to queue.");
						String newOp = p.name + ":" + line;
						opQueue.add(newOp);		
					}
					
					System.out.println("Exiting the player thread.");
				}
				
				// If outputStream is null
				else {
					System.err.println("PlayerThread: outputStream for player '" + p.name + "' is null. ");
					line = "X:Exiting";
				}
				
			}
			catch (Exception e1) {
				if (outputStream != null) {
					//System.err.println("Error thrown in player thread.");
					//e1.printStackTrace();
				}
				else {
					line = "X:Exiting";
					String newOp = "REMOVEPLAYER:" + p.name;
					opQueue.add(newOp);
					inputStream = null;
					
					String lobbyInfo = lobby.showLobbyInfo();
					newOp = "BROADCAST:" + lobbyInfo;
					opQueue.add(newOp);
					
					System.out.println("Client attempting to exit");
					System.out.println("About to call movePlayer");
					lobby.movePlayer(p.name, -1);
					System.out.println("After movePlayer");
					commsLink.remove(p.name, outputStream);
					//lobby.removePlayer(p);
					String newPlayerList = lobby.removePlayer(p.name);
					newOp = "BROADCAST:" + newPlayerList;
					opQueue.add(newOp);
				}
			}
		}
   	}
 
	
	public void remove() {
		
	}
	
}


class Worker extends Thread {
	private static Queue<String> opQueue = null;
	private static Hashtable<String, PrintWriter> playerOutputStreams = null;
    private static Lobby lobby = null;
    
	private static String delims = ":";
    private static int check = 0;
    private static String line = "";
    private static PrintWriter os = null;
    
	public Worker(Queue<String> theQueue, Hashtable<String,PrintWriter> outstreams, Lobby mainlobby) {
		opQueue = theQueue;
		playerOutputStreams = outstreams;
		lobby = mainlobby;
	}
	
	public void run() {
		while(true) {
			try {
				String op = opQueue.remove();
				System.out.println("QueueProcThread: Queue Processor handling new operation '" + op + "'.");
				parseString(op);
			}
			catch(NoSuchElementException e) {
				// No new operation waiting.
			}
		}
		
	}
	
	private static void parseString(String clientLine) {
		String parts[] = clientLine.split(delims);
		
		String uname = parts[0];
		if ("BROADCAST".equals(uname)) {
			String broadcastMsg = "";
			broadcastMsg = parts[1] + ":";
			
			int i;
			// Need to append back the parts with the ":"
			for (i = 2; i < Array.getLength(parts)-1; i++) {
				broadcastMsg = broadcastMsg + parts[i] + ":";
			}
			for (i = Array.getLength(parts)-1; i < Array.getLength(parts); i++) {
				broadcastMsg = broadcastMsg + parts[i];
			}
			
			for (PrintWriter oneOutput : playerOutputStreams.values()) {
			    oneOutput.println(broadcastMsg);
			    oneOutput.flush();
			}
		}
		
		else if ("SETLOCKREQ".equals(uname)) {
			int gameNum = Integer.parseInt(parts[1]);
			String userRequesting = parts[2];
			System.out.println("Queue proc setting set lock request from player: " + userRequesting);
			
			if ((lobby.games[gameNum-1]).getSetLock(userRequesting) == true) {
				String newOp = "BROADCAST:SETLOCKED:" + gameNum + ":" + userRequesting;
				opQueue.add(newOp);
			}
		}
		
		else if ("REMOVEPLAYER".equals(uname)) {
			String removePlayerName = parts[1];
			lobby.movePlayer(removePlayerName, -1);
			System.out.println("After movePlayer");
			playerOutputStreams.remove(removePlayerName);
			
			String newPlayerList = lobby.removePlayer(uname);
			String newOp = "BROADCAST:" + newPlayerList;
			opQueue.add(newOp);
		}
		
		else {
			// Get output stream to the specificied player from the username(uname)
			os = playerOutputStreams.get(uname);			
			
			// If player is choosing a game room:
			if("GAMECHOICE".equals(parts[1])) {
				int gameRoom = Integer.parseInt(parts[2]);
				System.out.println("Moving player '" + uname + "' to room " + gameRoom);
				String newPlayerList = lobby.movePlayer(uname, gameRoom);
				String newOp = "BROADCAST:" + newPlayerList;
				opQueue.add(newOp);
				
				String scoresList = lobby.games[gameRoom-1].showScores();
				System.err.println("Printing out scores = " + scoresList);
				scoresList = "BROADCAST:" + scoresList;
				opQueue.add(scoresList);
				
				// Also have to send that player the cards in play in that game
				String cardsOnBoard = lobby.games[gameRoom-1].board.displayBoard();
				os.println(cardsOnBoard);
				os.flush();
				System.out.println(cardsOnBoard);
				
				String welcomeMsg = "GAMENOTIFICATION:Welcome to Game Room " + gameRoom;
				os.println(welcomeMsg);
				os.flush();
			}
			
			// If player is exiting the entire game
			if("X".equals(parts[1])) {
				String newPlayerList = lobby.removePlayer(uname);
				playerOutputStreams.remove(uname);
				os = null;
				String newOp = "BROADCAST:" + newPlayerList;
				opQueue.add(newOp);
				
				// Show all other players new list of players
				// [allOS].println(lobby.showPlayers());
				System.out.println("QueueProcThread: Closing connection to thread for player: '" + uname + "'.");
			}
			
		}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server2 {
	
	public static int portNum = 4445;
	public static int numGames = 3;
	
	// Lobby that adds and moves players around
	public static Lobby lobby = null;
	// opQueue is a queue of strings formatted: "username:operation"
	public static Queue<String> opQueue = null;
	// comms maintains output streams for each logged in player
	public static HashMap<String,PrintWriter> comms = null;
	// the list of players logged in
	public static HashMap<String,Player> players = null;
	
	public static void main(String args[]){
		opQueue = new LinkedList<String>();
		comms = new HashMap<String,PrintWriter>();
		players = new HashMap<String,Player>();
		lobby = new Lobby(3, players);
		
		Worker queueProc = new Worker(opQueue, comms, lobby);
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

		while(true){
			System.out.println("Inside accepting connections phase...");
			try{
				playerSocket = ss2.accept();
				System.out.println("Connection with a new player established.");
				
				try{
					BufferedReader is = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
					PrintWriter os = new PrintWriter(playerSocket.getOutputStream());
					
					// Create new player thread that can handle its own input and output
					// player thread cannot handle major requests, instead them into the opQueue
					PlayerThread playerT = new PlayerThread(is, os, lobby, opQueue, comms);
					playerT.start();
		    	}
		    	catch(IOException e){
		    		System.err.println("IO error with trying to create new output/input stream for new player.");
		    	}
				
			} catch(Exception e){
				e.printStackTrace();
				System.err.println("Connection Error (with accepting players).");
			}
		}

	}

}

class PlayerThread extends Thread {
	private static Lobby lobby = null;
	private static Player p = null;
    private static String line = "";
    private static BufferedReader inputStream = null;
    private static PrintWriter outputStream = null;
    private static Queue<String>opQueue = null;
    private static String delims = ":";
    private static int check = 0;
    private static HashMap<String,PrintWriter> commsLink = null;
    
    public PlayerThread(BufferedReader is, PrintWriter os, Lobby mainlobby, Queue<String> theQueue, HashMap<String,PrintWriter> outstreams){
        inputStream = is;
        outputStream = os;
        opQueue = theQueue;
        lobby = mainlobby;
        commsLink = outstreams;
    }

	public void run() {
    	/* This block provides the signIn and signUP functionality */
		
		while (!line.equals("X:bye")) {
			try {
				if (inputStream != null) {
					line = inputStream.readLine();
					String parts[] = line.split(delims);
					
					System.out.println("Client said: " + line);
					
					//If L:user:pass, player is requesting to login
					if("L".equals(parts[0])) {
						check = DBUtils.signIn(parts[1], parts[2]);
						//let client know how SignIn went
						System.out.println("Login attempt for: " + parts[1] + " resulted in: " + check);
						outputStream.println("L:" + check);
						outputStream.flush();	
						p = new Player(parts[1]);
						
						if ("0".equals(check)) {
							System.out.println("Adding player '" + parts[1] + "' to Lobby.");
							lobby.addPlayer(p);
							commsLink.put(p.name, outputStream);
							outputStream.println(lobby.showPlayers());
							outputStream.flush();
						}
					}
					
					//If R:user:pass:email, used for registration
					if("R".equals(parts[0])) {
						check = DBUtils.signUp(parts[1], parts[2], parts[3]);
						System.out.println("Registration attempt for: " + parts[1] + " resulted in: " + check);
						outputStream.println("R:" + check);
						outputStream.flush();
					}
					
					else {
						String newOp = p.name + ":" + line;
						opQueue.add(newOp);		
					}
				}
				
				// If outputStream is null
				else {
					System.err.println("outputStream for player '" + p.name + "' is null. ");
					line = "X:bye";
				}
				
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
   	}
    
}


class Worker extends Thread {
	private Queue<String>opQueue = null;
	private static HashMap<String, PrintWriter> playerOutputStreams = null;
    private static Lobby lobby = null;
    
	private static String delims = ":";
    private static int check = 0;
    private static String line = "";
    private static PrintWriter os = null;
    
	public Worker(Queue<String> theQueue, HashMap<String,PrintWriter> outstreams, Lobby mainlobby) {
		opQueue = theQueue;
		playerOutputStreams = outstreams;
		lobby = mainlobby;
	}
	
	public void run() {
		while(true) {
			try {
				String op = opQueue.remove();
				System.out.println("Queue Processor handling new operation '" + op + "'.");
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
		os = playerOutputStreams.get(uname);
		// Get output stream to the specificied player from the username(uname)
		
		
		// If player is choosing a game room:
		if("GC".equals(parts[1])) {
			lobby.games[Integer.parseInt(parts[2])].addPlayer(uname);
		}
		
		// If player is exiting the entire game
		if("X".equals(parts[1])) {
			try {
				lobby.removePlayer(uname);
				playerOutputStreams.remove(uname);
				os = null;
				
				// Show all other players new list of players
				// [allOS].println(lobby.showPlayers());
				System.out.println("Closing connect to thread for player: '" + uname + "'.");
			}
			
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
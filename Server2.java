// echo server
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {

	public static Lobby lobby = new Lobby(3);
	
	public static void main(String args[]){
		
		Socket s=null;
		ServerSocket ss2=null;
		try{
			ss2 = new ServerSocket(4445); // can also use static final PORT_NUM , when defined
		} catch(IOException e){
			e.printStackTrace();
			System.out.println("Server error");
		}

		while(true){
			System.out.println("While(true)");
			try{
				s= ss2.accept();
				System.out.println("connection Established");
				ServerThread st=new ServerThread(s,lobby);
				st.start();
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("Connection Error");
			}
		}

	}

}

class ServerThread extends Thread{  
	private static Lobby lobby = null;
	private static Player p = null;
    private static String line = "";
    private static BufferedReader  is = null;
    private static PrintWriter os = null;
    private static Socket s = null;
    private static String delims = ":";
    private static int check = 0;
    public ServerThread(Socket s,Lobby mainlobby){
        this.s=s;
        lobby = mainlobby;
    }

    public void run() {
    	try{
    		is= new BufferedReader(new InputStreamReader(s.getInputStream()));
    		os=new PrintWriter(s.getOutputStream());

    	}catch(IOException e){
    		System.out.println("IO error in server thread");
    	}
    	
    	/* This block provides the signIn and signUP functionality */
    	
    	while (!line.equals("X:bye")) {
			try {
				if (is != null)
					line = is.readLine();
				else
					line = "X:bye";
				System.out.println("Client said: " + line);
				parseString(line);					
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    	
    	/*
   		try {
       		line=is.readLine(); //read the string from client
       		String parts[] = line.split(delims);
       		//If L:user:pass , its for login
       		if("L".equals(parts[0])) {
       			check = DBUtils.signIn(parts[1], parts[2]);
       			//let client know how SignIn went
       			System.out.println(check);
       			os.println("L:"+check);
       			os.flush();	
       			p = new Player(parts[1]);
       			lobby.addPlayer(p);
       		}
       		//If R:user:pass:email, used for registration
       		if("R".equals(parts[0])) {
       			check = DBUtils.signUp(parts[1], parts[2], parts[3]);
       			System.out.println(check);
       			os.println("R:"+check);
       			os.flush();
       		}
   		}catch (IOException e) {
   			line=this.getName(); //reused String line for getting thread name
   			System.out.println("IO Error/ Client "+line+" terminated abruptly");
   		} catch(NullPointerException e){
   			line=this.getName(); //reused String line for getting thread name
   			System.out.println("Client "+line+" Closed");
   		}
   		*/
    	
    	
   		/*
   		finally {    
   			try{
   				System.out.println("Connection Closing..");
   				if (is!=null){
   					is.close(); 
   					System.out.println(" Socket Input Stream Closed");
   				}
   				if(os!=null){
   					os.close();
   					System.out.println("Socket Out Closed");
   				}
   				if (s!=null){
   					s.close();
   					System.out.println("Socket Closed");
   				}
   			} catch(IOException ie){
   				System.out.println("Socket Close Error");
   			}
   		}//end finally
   		*/
   	}
    
    private static void parseString(String clientLine) {
    	String parts[] = clientLine.split(delims);
   		//If L:user:pass , its for login
   		if("L".equals(parts[0])) {
   			check = DBUtils.signIn(parts[1], parts[2]);
   			//let client know how SignIn went
   			System.out.println(check);
   			os.println("L:"+check);
   			os.flush();	
   			p = new Player(parts[1]);
   			lobby.addPlayer(p);
   		}
   		//If R:user:pass:email, used for registration
   		if("R".equals(parts[0])) {
   			check = DBUtils.signUp(parts[1], parts[2], parts[3]);
   			System.out.println(check);
   			os.println("R:"+check);
   			os.flush();
   		}
   		if("X".equals(parts[0])) {
   			try {
				s.close();
				is = null;
				os = null;
				lobby.removePlayer(p);
				System.out.println("Closing connect to thread for player: " + p.name + "\n");
			}
   			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}
    }
}
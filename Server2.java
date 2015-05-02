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
	Lobby lobby = null;
	Player p = null;
    String line = null;
    BufferedReader  is = null;
    PrintWriter os = null;
    Socket s = null;
    String delims = ":";
    int check = 0;
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
}


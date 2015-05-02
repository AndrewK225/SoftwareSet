// echo server
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server2 {
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
			try{
				s= ss2.accept();
				System.out.println("connection Established");
				ServerThread st=new ServerThread(s);
				st.start();
			} catch(Exception e){
				e.printStackTrace();
				System.out.println("Connection Error");
			}
		}

	}

}

class ServerThread extends Thread{  

    String line = null;
    BufferedReader  is = null;
    PrintWriter os = null;
    Socket s = null;
    String delims = ":";
    public ServerThread(Socket s){
        this.s=s;
    }

    public void run() {
    	try{
    		is= new BufferedReader(new InputStreamReader(s.getInputStream()));
    		os=new PrintWriter(s.getOutputStream());

    	}catch(IOException e){
    		System.out.println("IO error in server thread");
    	}
    	
    	/* This try block provides most of the functionality of the server-side */
    	try {
        	line=is.readLine(); //read the string from client
        	System.out.println(line);
        	String parts[] = line.split(delims);
        	if(parts[0] == "L") { //Rest of the info is for login
        		System.out.println("Username: "+parts[1]);
        		System.out.println("Username: " + parts[2]);
        		int check = DBUtils.signIn(parts[1], parts[2]);
        			//let client know SignIn was successful
        			System.out.println(check);
        			os.println(check);
        	}
        
        	 
    	} catch (IOException e) {

    		line=this.getName(); //reused String line for getting thread name
    		System.out.println("IO Error/ Client "+line+" terminated abruptly");
    	} catch(NullPointerException e){
    		line=this.getName(); //reused String line for getting thread name
    		System.out.println("Client "+line+" Closed");
    	}

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
    }
}

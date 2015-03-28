import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public final class DBUtils {

    /* Will add a new user account into the database.*/
    public static void signUp(String user, String clear_pass, String email) {
        try{
        	//wins/losses default to 0. Hash the password
            String hashed_pass = hash(clear_pass);
            int wins = 0;
            int losses = 0;
            
            //Database login stuff CHANGE IF TESTING LOCALLY OR MAKE A USER WITH THESE CREDENTIALS
            Properties p = new Properties();
            p.put("user", "andrew");
            p.put("password", "password");
            
            //make connection
            String url = "jdbc:mysql://127.0.0.1/set_game";
            Connection conn = DriverManager.getConnection(url,p);
            
            //do the sql stuffs
            
            String insert_statement = "INSERT INTO Accounts (Username, Password, Email, Wins, Losses) VALUE(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(insert_statement);
            ps.setString(1,user);
            ps.setString(2, hashed_pass);
            ps.setString(3, email);
            ps.setInt(4, wins);
            ps.setInt(5, losses);
            
            ps.executeUpdate();
            conn.close();
            
        } catch(Exception e) {
        	System.err.println(e);
        }
    }

    /*Query database for user account.
     * returns 0 for success
     * return 1 for username error
     * return 2 for password error
     * return 3 for exception
     * */
    public static byte signIn(String user, String clear_pass) {
    	try {
    		String stored_pass = null; 
    		String entered_pass = null; //hashed version of the user cleartxt password 
    		
    		// access the database
    		Properties p = new Properties();
    		p.put("user", "andrew");
    		p.put("password", "password");
    		String url= "jdbc:mysql://127.0.0.1/set_game";
    		Connection conn = DriverManager.getConnection(url,p);
    		
    		//query the database
    		String query_statement = "SELECT Password FROM Accounts WHERE BINARY Username = ?";
    		PreparedStatement ps = conn.prepareStatement(query_statement);
    		ps.setString(1, user);
    		ResultSet rs = ps.executeQuery();
    		
    		
    		//get stored password
    		if(rs.next()) {
    			stored_pass = rs.getString("Password");
    			conn.close();
    		} else {  //username not found
    			conn.close();
    			return 1;
    		}

    		
    		//hash the entered password and see if it matches value in DB
    		entered_pass = hash(clear_pass);
    		if(entered_pass.equals(stored_pass)) {
    			return 0;
    		}
    		else { //password doesn't match
    			return 2;
    		}
    		
    	} catch(Exception e) {
    		System.err.println(e);
    		return 3;
    	}
    	
    	
    	
    }
    
    
    
    //use an MD5 hash 
    private static String hash(String cleartxt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(cleartxt.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
    
    
    
    
    
    
    
}

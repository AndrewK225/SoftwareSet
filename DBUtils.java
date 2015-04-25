/* This class holds all of the account utilities (create, sign in, sign out, update win/loss).
 * 
 * usage information
 * ---------to create an account----------------
 * DBUtils.signUp(String user, String clear_pass, String email)
 * 
 * ---------to sign in--------------------------
 * DBUtils.signIn(String user, String clear_pass)
 * 
 * ---------to sign out-------------------------
 * DBUtils.signOut(String user)
 * */


import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;


public final class DBUtils {
    
    /* Will add a new user account into the database.*/
    public static void signUp(String user, String clear_pass, String email) {
        try{
        	//wins/losses default to 0. Hash the password
	    Class.forName("com.mysql.jdbc.Driver");
	    String hashed_pass = hash(clear_pass);
            int wins = 0;
            int losses = 0;
            int logged_in = 0; //default, not loggedIn, change to 1 once logged in
           
            
	    String url = "jdbc:mysql://199.98.20.120:3306/set_game";
    
	    Properties p = new Properties();
	    p.put("user", "andrew");
	    p.put("password", "password");
           
            
	    
            //make connection
            Connection conn = DriverManager.getConnection(url,p);
            
            //check if username already exists
            String check_username = "Select * FROM Accounts WHERE Username =?";
            PreparedStatement ps = conn.prepareStatement(check_username);
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                //Insert username into database
            	String insert_statement = "INSERT INTO Accounts (Username, Password, Email, Wins, Losses,LoggedIn) VALUE(?,?,?,?,?,?)";
            	PreparedStatement ps2 = conn.prepareStatement(insert_statement);
            	ps2.setString(1,user);
            	ps2.setString(2, hashed_pass);
            	ps2.setString(3, email);
            	ps2.setInt(4, wins);
            	ps2.setInt(5, losses);
            	ps2.setInt(6, logged_in);
            	ps2.executeUpdate();
            	conn.close();
            } else {
            	//WE SHOULD PROBABLY REPROMPT FOR ANOTHER USERNAME
            	System.out.println("Username already exists");
            	conn.close();
            }
            
        } catch(Exception e) {
        	System.err.println(e);
        }
    }

    /* Query database for user account.
     * returns 0 for success
     * return 1 for username error
     * return 2 for password error
     * return 3 for exception
     * 
     * If all is good, change LoggedIn to 1.
     * */
    public static byte signIn(String user, String clear_pass) {
    	try {
	    Class.forName("com.mysql.jdbc.Driver");
	    String stored_pass = null; 
	    String entered_pass = null; //hashed version of the user cleartxt password 
    		
    		// access the database
    	    
	    String url = "jdbc:mysql://199.98.20.120:3306/set_game";
    
	    Properties p = new Properties();
	    p.put("user", "andrew");
	    p.put("password", "password");
	    Connection conn = DriverManager.getConnection(url,p);
    		
    		//query the database
	    String query_statement = "SELECT Password FROM Accounts WHERE BINARY Username = ?";
	    PreparedStatement ps = conn.prepareStatement(query_statement);
	    ps.setString(1, user);
	    ResultSet rs = ps.executeQuery();
    				
    		//get stored password
	    if(rs.next()) {
		stored_pass = rs.getString("Password");
		
	    } else {  //username not found
		conn.close();
		return 1;
	    }
    		
	    //hash the entered password and see if it matches value in DB
	    entered_pass = hash(clear_pass);
	    if(entered_pass.equals(stored_pass)) {
		//If all is good, change the LoggedIn field to 1
		String make_logged_in = "UPDATE Accounts SET LoggedIn = 1 WHERE Username = ?";
		PreparedStatement ps2 = conn.prepareStatement(make_logged_in);
		ps2.setString(1, user);
		ps2.executeUpdate();
		conn.close();
		return 0;
	    }
	    else { //password doesn't match
		conn.close();
		return 2;
	    }
	    
    	} catch(Exception e) {
    		System.err.println(e);
    		return 3;
    	}
    	
    	
    }
    
    /* Change the LoggedIn field to 0
     * returns 1 on success.
     * */
    public static int signOut(String user) {
    	try {
	    Class.forName("com.mysql.jdbc.Driver");
    	     
	    String url = "jdbc:mysql://199.98.20.120:3306/set_game";
    
	    Properties p = new Properties();
	    p.put("user", "andrew");
	    p.put("password", "password");
	    Connection conn = DriverManager.getConnection(url,p);
	    
	    String logout = "UPDATE Accounts SET LoggedIn = 0 WHERE Username = ?";
	    PreparedStatement ps = conn.prepareStatement(logout);
	    ps.setString(1, user);
	    ps.executeUpdate();
	    conn.close();
	    return 1; //when user signs out, return 1
    	}catch(Exception e) {
    		System.err.println(e);
    		return 2;
    	}
    	
    }
    
    /* Update win record 
     * I leave it to game logic to just send me the name of the winner
     * */
    public static void updateRecord(String p1) {
    	try {
	    Class.forName("com.mysql.jdbc.Driver");
    		Properties p = new Properties();
    		p.put("user","andrew");
    		p.put("password", "password");
    		String url = "jdbc:mysql://199.98.20.120:3306/set_game";
    		Connection conn = DriverManager.getConnection(url,p);
    		 
    		String updateWin = "UPDATE Accounts SET Wins = Wins + 1 WHERE Username = ?";
    		PreparedStatement ps = conn.prepareStatement(updateWin);
    		ps.setString(1, p1);
    		ps.executeUpdate();
    		conn.close();
    	}catch (Exception e) {
    		System.err.println(e);
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

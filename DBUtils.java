import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public final class DBUtils {

    /* Will add a new user account into the database.*/
    public static void signup(String user, String clear_pass, String email) {
        try{
        	//wins/losses default to 0. Hash the password
            String hashed_pass = hash(clear_pass);
            int wins = 0;
            int losses = 0;
            
            //login stuff
            Properties p = new Properties();
            p.put("user", "andrew");
            p.put("password", "password");
            
            //make connection
            String url = "jdbc:mysql://127.0.0.1/set_game";
            Connection conn = DriverManager.getConnection(url,p);
            
            //do the sql stuffs
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO Accounts (Username, Password, Email, Wins, Losses) VALUE ('"+user+"','"+hashed_pass+"','"+email+"','"+wins+"','"+losses+"')");
            conn.close();
            
        } catch(Exception e) {
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
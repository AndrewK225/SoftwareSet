
public class Utils_test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBUtils.signUp("bob", "123", "t@gmail.com");
		byte a = DBUtils.signIn("Andrew", "123");
		System.out.println(a);
	}

}

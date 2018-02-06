package bengine.networking;

import java.security.SecureRandom;

public class Util {
	private static SecureRandom r = new SecureRandom();
	
	public static long generateId() {
		return r.nextLong();
	}
	
}

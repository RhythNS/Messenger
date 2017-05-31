package secruity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
	
	public static String getMD5(String input) throws NoSuchAlgorithmException{
		MessageDigest md=MessageDigest.getInstance("MD5");
		byte[]messageDigest=md.digest(input.getBytes());
		BigInteger bigInt= new BigInteger(1,messageDigest);
		String hash= bigInt.toString(16);	
		while(hash.length()<32){
			hash="0"+hash;
		}
		return hash;
	}
	
	
}

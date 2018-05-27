package tests;

import java.security.MessageDigest;
import java.util.Base64;

import enums.HashFunction;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			String toHash = "Pödödel";
			
			MessageDigest hash = MessageDigest.getInstance(HashFunction.SHA1.toString(), "BC");
			hash.update(toHash.getBytes());
			System.out.println(Base64.getEncoder().encodeToString(hash.digest()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

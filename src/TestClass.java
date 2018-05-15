
public class TestClass {

	public static void main(String[] args) {
		
		CryptoManager cmana = new CryptoManager();
		
		try {
			
			byte[] getOutpu = cmana.encryptString("Peterfjopaj", EncryptionType.DES, EncryptionMode.ECB, PaddingType.PKCS7Padding);
			System.out.println(new String(getOutpu, "UTF-8"));
			System.out.println(cmana.decryptString(getOutpu, EncryptionType.DES, EncryptionMode.ECB, PaddingType.PKCS7Padding));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

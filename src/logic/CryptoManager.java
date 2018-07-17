package logic;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import enums.EncryptionType;
import enums.KeyLength;
import persistence.FileManager;
import persistence.MetaData;


/**
 * Used to de and encrypt data
 * @author Joel
 */
public class CryptoManager {
	
	
	private static final int iterationCount = 1000;
    private static final int saltLength = 8;
	
	/**
	 * Creates a cipher object for symmetric en & decryption
	 * @param mode cipher mode
	 * @param fileData metadata of the file
	 * @param key key to be used
	 * @param ivSpec iv to be used
	 * @return generated cipher
	 * @throws Exception
	 */
    private static Cipher generateCipher(int mode, MetaData fileData, Key key, IvParameterSpec ivSpec) throws Exception
    {
    	Cipher cipher = null;
    	
    	//if the enc type is ARC4
    	if(fileData.getEncryptionType() == EncryptionType.ARC4)
    	{
    		//The Cipher.getInstance only takes the 'ARC4' argument, no padding or mode
    		cipher = Cipher.getInstance(EncryptionType.ARC4.toString(), "BC");
    	}
    	else
    	{
    		//create the cipher object with the concatenated string 'encType/encMode/padding'
    		cipher = Cipher.getInstance(fileData.getEncryptionType().toString() + "/" + fileData.getEncryptionMode().toString() + "/" + fileData.getPaddingType().toString(), "BC");
    	}
    	
    	//If there is an iv present
		if(ivSpec != null)
		{
			//init the cipher with an iv
			cipher.init(mode, key, ivSpec);
		}
		//if there is none
		else
		{
			//init the cipher without an iv
			cipher.init(mode, key);
		}

		
		//Return the initialized cipher object
		return cipher;
    }
    
    /**
     * Creates a cipher for an asymmetric en & decryption
     * @param mode cipher mode
     * @param fileData metadata of the file
     * @param key key to be used
     * @param random SecureRandom object to be used
     * @return generated cipher
     * @throws Exception
     */
    private static Cipher generateCipher(int mode, MetaData fileData, Key key, SecureRandom random) throws Exception
    {
    	//RSA ciphers are created with "RSA/None/NoPadding"
    	Cipher cipher = Cipher.getInstance(fileData.getEncryptionType().toString() + "/None/NoPadding", "BC");
    	
    	//TODO how to 
    	cipher.init(mode, key, random);
    	return cipher;
    }
    
    /**
     * Creates a cipher for a pbe en & decryption
     * @param mode cipher mode
     * @param fileData metadata of the file
     * @param key key to be used
     * @param spec Pbe param specs to be used in the cipher
     * @return generated cipher
     * @throws Exception
     */
    private static Cipher generateCipher(int mode, MetaData fileData,SecretKey key, PBEParameterSpec spec) throws Exception
    {
	    Cipher cipher = Cipher.getInstance(fileData.getEncryptionType().toString());
	    cipher.init(mode, key, spec);
	    return cipher;
    }
    

    
	/**
	 * Encrypts a given string with the desired encryption, encryption mode and padding type 
	 * @param input String to be encrypted
	 * @param fileData file metadata
	 * @return encrypted string in a byte array
	 * @throws Exception
	 * 
	 */
	public static byte[] encryptString(String input, MetaData fileData) throws Exception
	{ 
		byte[] output;
		byte[] keyBytes = null;
		IvParameterSpec iv;
		Cipher cipher = null;
		Key keyObject = null;
		
		fileData.setHashValue(generateHash(fileData, input.trim().getBytes()));
		
		input = input + (char)3 + fileData.getHashValue();
		
		if(fileData.getEncryptionType() != EncryptionType.none && !input.isEmpty())
		{
			//TODO dont pull the keys from their respective key objects
			switch(fileData.getEncryptionType().getOperationMode())
			{
			case Asymmetric:
				SecureRandom random = new SecureRandom();
				KeyPair keyPair = generateAsymmetricKeys(random, fileData.getKeyLength());
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyPair.getPublic(), random);
				keyBytes =  new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded();
				break;
			case Passwordbased:
				byte[] salt = generateByteArray(saltLength);
				fileData.setSalt(salt);
				SecretKeyFactory factory = SecretKeyFactory.getInstance(fileData.getEncryptionType().toString(), "BC");
			    SecretKey key = factory.generateSecret(new PBEKeySpec(fileData.getPassword().toCharArray()));
			    
			    cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, key, new PBEParameterSpec(salt, iterationCount));				
				break;
			case Symmetric:
				keyObject = generateSymmetricKey(fileData.getEncryptionType(), fileData.getKeyLength());
				
				iv = getIvIfNeeded(fileData);
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyObject, iv);
				keyBytes = keyObject.getEncoded();
				break;
			}
			
			output = applyCipher(cipher, input.getBytes());
		}
		else
		{
			output = input.getBytes();
		}
		
		
		
		if(keyBytes != null)
			FileManager.saveKey(keyBytes, fileData.getHashValue(), fileData.getUsbData().getDriveLetter());
		
		return output;
	}
	

	
	/**
	 * Generates a hash of the desired hashfunction
	 * @param hashFunction Hashfunction to be applied
	 * @param input input to be hashed
	 * @return hash
	 * @throws Exception
	 */
	public static String generateHash(MetaData metadata, byte[] input) throws Exception
	{
		MessageDigest hash = MessageDigest.getInstance(metadata.getHashFunction().toString(), "BC");
		hash.update(input);
		//TODO
		//hash.update(metadata.getEncryptionType().toString().getBytes());
		//hash.update(metadata.getUsbData().toString().getBytes());
		return Base64.getEncoder().encodeToString(hash.digest());
	}
	
	/**
	 * Applys the given cipher to a given input and returns it
	 * @param cipher Cipher to apply
	 * @param input input to cipher
	 * @return ciphered output
	 * @throws Exception
	 */
	private static byte[] applyCipher(Cipher cipher, byte[] input) throws Exception
	{
		byte [] output = new byte[cipher.getOutputSize(input.length)];
		
		int ctLength = cipher.update(input, 0, input.length, output, 0);
		
		ctLength += cipher.doFinal(output, ctLength);
		
		return output;
	}
	
	/**
	 * Compares the stored hash and one generated by the input. If they dont match, an error is displayed 
	 * @param fileData metadata of the read file
	 * @param input input array to be hashed
	 * @param readHash read hash
	 * @throws Exception
	 */
	public static void validateHash(MetaData fileData, byte[] input, String readHash) throws Exception
	{
		//Compare the two hashes using a message digest helper function
		
		if(!MessageDigest.isEqual(Base64.getDecoder().decode(readHash) , Base64.getDecoder().decode(generateHash(fileData, input))))
		{
			throw new Exception("File has been altered!");
		}
	}
	
	/**
	 * Decrypts a given byte array using the given parameters
	 * @param input byte array to be decrypted
	 * @param encryptionType encryption type used to decrypt
	 * @param encryptionMode encryption mode used to decrypt
	 * @param paddingType padding type used to decrypt
	 * @return Decrypted String
	 * @throws Exception
	 * 
	 * 		Cipher-Based I/O ?
	 */
	public static String decryptString(byte[] input, MetaData fileData) throws Exception
	{
		byte[] plainText;
		
		
		
		if(fileData.getEncryptionType() != EncryptionType.none)
		{
			
			IvParameterSpec ivSpec = null; 
			
			if(fileData.getiV() != null)
			{
				if(fileData.getEncryptionMode().usesIV() && !fileData.getiV().equals("null"))
				{
					ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
				}
				else if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
				{
					throw new Exception("IV WAS NOT SET");
				}
			}
			
			
			Cipher cipher = null;
			
			switch(fileData.getEncryptionType().getOperationMode())
			{
			case Asymmetric:
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(FileManager.getKeyFromFile(fileData.getHashValue(), fileData.getUsbData().getDriveLetter()));
				PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
				
				//Instantiate the cipher with a private key
				cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				break;
			case Passwordbased:
			    SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance(fileData.getEncryptionType().toString());
			    SecretKey skey = keyFactory2.generateSecret(new PBEKeySpec(fileData.getPassword().toCharArray()));
			   
			    cipher = generateCipher(Cipher.DECRYPT_MODE, fileData, skey, new PBEParameterSpec(fileData.getSalt(), iterationCount));
				break;
			case Symmetric:
				Key key = new SecretKeySpec(FileManager.getKeyFromFile(fileData.getHashValue(), fileData.getUsbData().getDriveLetter()), fileData.getEncryptionType().toString());
				cipher = generateCipher(Cipher.DECRYPT_MODE, fileData, key, ivSpec);
				break;
			default:
				break;
			
			}

			plainText = applyCipher(cipher, input);
		}
		else
		{
			plainText = input;
		}
		
		String outputString = new String(plainText, "UTF-8").trim();
		String hash = "";
		
		for(int count = outputString.length() - 1; count > 0; count --)
		{
			//Char 3 is a char used to symbolize the end of a transmission (ASCII)
			if(outputString.charAt(count) == (char)3)
			{
				hash = outputString.substring(count +1, outputString.length());
				outputString = outputString.substring(0,count);
			}
		}
		
		validateHash(fileData, outputString.trim().getBytes(), hash);
		
		return outputString;
	}
	
	
	
	
	
	/**
	 * Generates a key for a symmetric encryption
	 * @param encryptionType encryption type that the key will be used with
	 * @param keyLength desired key length
	 * @return Key
	 * @throws Exception
	 */
	private static Key generateSymmetricKey(EncryptionType encryptionType, KeyLength keyLength) throws Exception
	{
		KeyGenerator generator = KeyGenerator.getInstance(encryptionType.toString(), "BC");
		generator.init(keyLength.returnAsInt());
		Key encryptionKey = generator.generateKey();
		return encryptionKey;
	}
	
	/**
	 * Generates a KeyPair for an asymmetric encryption
	 * @param random SecureRandom object to generate the key
	 * @param keyLength desired key length
	 * @return KeyPair
	 * @throws Exception
	 */
	private static KeyPair generateAsymmetricKeys(SecureRandom random, KeyLength keyLength) throws Exception
	{
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		generator.initialize(keyLength.returnAsInt(), random);
		KeyPair pair = generator.generateKeyPair();
		return pair;
	}
	
	
	/**
	 * Gets an iv matching the fitting length depending on the encryption type
	 * @param encryption encryption to be used
	 * @return iv 
	 */
	private static byte[] generateMatchingIV(EncryptionType encryption)
	{
		switch(encryption)
		{
		case DES:
			return generateByteArray(8);
		case AES:
			return generateByteArray(16);
		case none:
		default:
			return null;
		}
	}
	
	/**
	 * Randomly generates a random byte array with the given length
	 * @param length length of the array
	 * @return array
	 */
	private static byte[] generateByteArray(int length)
	{
		if(length > 0)
		{
			SecureRandom random = new SecureRandom();
			byte[] randomBytes = new byte[length];
			random.nextBytes(randomBytes);
			return randomBytes;
		}
		return null;
	}
	
	/**
	 * Returns an iv one is needed. Returns a newly generated one if one is needed but not present, returns the present one if there is one. 
	 * @param fileData 
	 * @return
	 */
	private static IvParameterSpec getIvIfNeeded(MetaData fileData)
	{
		IvParameterSpec ivSpec = null;		
		if(fileData.getEncryptionMode().usesIV() && fileData.getEncryptionType() != EncryptionType.ARC4)
		{
			byte[] ivArray = generateMatchingIV(fileData.getEncryptionType());
			fileData.setiV(Base64.getEncoder().encodeToString(ivArray));
			ivSpec = new IvParameterSpec(ivArray);
			
		}
		return ivSpec;
	}
	
	
}

package logic;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import persistence.FileManager;
import persistence.MetaData;

/**
 * Used to de and encrypt data
 * @author Joel
 */
public class CryptoManager {
    
    /**
     * Generates a cipherobject
     * @param mode ENCRYPT/DECRYPT
     * @param fileData metadata that contains needed information
     * @param key key to be used
     * @param ivSpec iv to be used
     * @return generated cipher
     * @throws Exception
     */
    private static Cipher generateCipher(int mode, MetaData fileData, Key key, IvParameterSpec ivSpec, SecureRandom random) throws Exception
    {
    	//TODO if operationmode == symmetrical
    	
		Cipher cipher = Cipher.getInstance(fileData.getEncryptionType().toString() + "/" + fileData.getEncryptionMode().toString() + "/" + fileData.getPaddingType().toString(), "BC");
		
		if(ivSpec != null)
		{
			cipher.init(mode, key, ivSpec);
		}
		else if(random != null && fileData.getOperationMode() == OperationMode.Asymmetric)
		{
			cipher.init(mode, key, random);
		}
		else
		{
			cipher.init(mode, key);
		}

		
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
		byte[] key = null;
		Cipher cipher = null;
		if(fileData.getEncryptionType() != EncryptionType.none)
		{
			//TODO dont pull the keys from their respective key objects
			switch(fileData.getOperationMode())
			{
			case Asymmetric:
				SecureRandom random = new SecureRandom();
				KeyPair keyPair = generateAsymmetricKeys(random, fileData.getKeyLength());
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyPair.getPublic(), null, random);
				key =  new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded();
				break;
			case Passwordbased:
				break;
			case Symmetric:
				Key keyObject = generateSymmetricKey(fileData.getEncryptionType(), fileData.getKeyLength());
				IvParameterSpec iv = getIvIfNeeded(fileData);
				System.out.println("iv" + iv.getIV());
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyObject, iv, null);
				key = keyObject.getEncoded();
				break;
			}
			
			output = applyCipher(cipher, input.getBytes());
		}
		else
		{
			output = input.getBytes();
		}
		
		fileData.setHashValue(generateHash(fileData.getHashFunction(), output));
		
		if(key != null)
			FileManager.saveKey(key, fileData.getHashValue(), fileData.getUsbData().getDriveLetter());
		
		return output;
	}

	
	/**
	 * Generates a hash of the desired hashfunction
	 * @param hashFunction Hashfunction to be applied
	 * @param input input to be hashed
	 * @return hash
	 * @throws Exception
	 */
	private static String generateHash(HashFunction hashFunction, byte[] input) throws Exception
	{
		MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
		hash.update(input);
	
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
	
	
	private static void validateHash(HashFunction hashFunction, byte[] input, String readHash) throws Exception
	{
		MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
		hash.update(input);
		
		//Compare the two hashes using a message digest helper function
		if(!MessageDigest.isEqual(Base64.getDecoder().decode(readHash) , hash.digest()))
		{
			throw new Exception("File has been altered REEEEEEEEEEEEEEEEEEE");
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
		
		validateHash(fileData.getHashFunction(), input, fileData.getHashValue());
		
		if(fileData.getEncryptionType() != EncryptionType.none)
		{
			
			IvParameterSpec ivSpec = null;
			
			if(fileData.getEncryptionMode().usesIV() && !fileData.getiV().equals("null"))
			{
				ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
			}
			else if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
			{
				throw new Exception("IV WAS NOT SET");
			}
			
			
			Cipher cipher = null;
			
			switch(fileData.getOperationMode())
			{
			case Asymmetric:
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(FileManager.getKeyFromFile(fileData.getHashValue(), fileData.getUsbData().getDriveLetter()));
				PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
				cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				break;
			case Passwordbased:
				break;
			case Symmetric:
				Key key = new SecretKeySpec(FileManager.getKeyFromFile(fileData.getHashValue(), fileData.getUsbData().getDriveLetter()), fileData.getEncryptionType().toString());
				cipher = generateCipher(Cipher.DECRYPT_MODE, fileData, key, ivSpec, null);
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
		
		return new String(plainText, "UTF-8");
	}
	
	private static Key generateSymmetricKey(EncryptionType encryptionType, KeyLength keyLength) throws Exception
	{
		KeyGenerator generator = KeyGenerator.getInstance(encryptionType.toString(), "BC");
		generator.init(keyLength.returnAsInt());
		Key encryptionKey = generator.generateKey();
		return encryptionKey;
	}
	
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
			return generateIV(8);
		case AES:
			return generateIV(16);
		case none:
		default:
			return null;
		}
	}
	
	/**
	 * Randomly generates an IV with the given length
	 * @param length length of the IV
	 * @return iv
	 */
	private static byte[] generateIV(int length)
	{
		if(length > 0)
		{
			SecureRandom random = new SecureRandom();
			byte[] ivBytes = new byte[length];
			random.nextBytes(ivBytes);
			return ivBytes;
		}
		return null;
	}
	
	private static IvParameterSpec getIvIfNeeded(MetaData fileData)
	{
		IvParameterSpec ivSpec = null;
		if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
		{
			byte[] ivArray = generateMatchingIV(fileData.getEncryptionType());
			
			fileData.setiV(Base64.getEncoder().encodeToString(ivArray));
			
			ivSpec = new IvParameterSpec(ivArray);
		}
		else if(fileData.getEncryptionMode().usesIV())
		{
			ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
		}
		return ivSpec;
	}
	
	
}

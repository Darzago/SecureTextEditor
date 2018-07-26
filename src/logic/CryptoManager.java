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
		//Prepare variables
		byte[] output;
		byte[] keyBytes = null;
				
		//appends the hash to the plaintext, using char '3' (ETX/End of Text in ASCI) as a seperation character
		input = input + (char)3 + generateHash(fileData, input.trim().getBytes());
		
		//if the encryption type is not 'None' 
		if(fileData.getEncryptionType() != EncryptionType.none)
		{
			//Prepare a cipher reference
			Cipher cipher = null;

			//Create the correct type of cipher based on the operation mode
			switch(fileData.getEncryptionType().getOperationMode())
			{
			case Asymmetric:
				//Create a secure random (always should be properly seeded, do not use sth like the date/current time)
				SecureRandom random = new SecureRandom();
				
				//Generate a keypair
				KeyPair keyPair = generateAsymmetricKeys(random, fileData.getKeyLength());
				
				//Generate a cipher for the encryption using the generated public key
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyPair.getPublic(), random);
				
				//encode the Private key to make it possible to save it in a file, since asymmetric keys consist of 2 parts, the modulo and a large int
				keyBytes =  new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded()).getEncoded();
				break;
			case Passwordbased:
				
				//generate a random salt and save it in the filedata to save it in the metadata
				byte[] salt = generateRandomByteArray(fileData.getEncryptionType().getPBESaltLength());
				fileData.setSalt(salt);
				
				//Generate a key from the selected password
				SecretKeyFactory factory = SecretKeyFactory.getInstance(fileData.getEncryptionType().toString(), "BC");
			    SecretKey key = factory.generateSecret(new PBEKeySpec(fileData.getPassword()));
			    
			    //Generate a cipher from the salt, iterationcount and the generated key
			    cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, key, new PBEParameterSpec(salt, iterationCount));				
				break;
			case Symmetric:
				
				//Generate a random symmetric key of the desired key length
				Key keyObject = generateSymmetricKey(fileData.getEncryptionType(), fileData.getKeyLength());
				
				//Create an iv if the currently selected mode needs one
				IvParameterSpec iv = getIvIfNeeded(fileData);
				
				//generate a cipher from the key and, if needed, the iv
				cipher = generateCipher(Cipher.ENCRYPT_MODE, fileData, keyObject, iv);
				
				//Get the encoded key to save it in a file
				keyBytes = keyObject.getEncoded();
				break;
			}
			
			//Apply the generated cipher to the input bytes
			output = applyCipher(cipher, input.getBytes());
		}
		//If the encryption type is 'none' or null
		else
		{
			//forward the content without encrypting it
			output = input.getBytes();
		}
		
		//If the keybyte array is not empty
		if(keyBytes != null)
		{
			//Save the key in a file with the filename being the hash of the encrypted file content
			FileManager.saveKey(keyBytes, generateHash(fileData, output), fileData.getUsbData().getDriveLetter());
		}
		
		//Reset the keybyte array to 'remove' it from the random access memory
		keyBytes = null;
		
		return output;
	}


	/**
	 * Generates a hash of the desired hashfunction
	 * @param metadata Metadata containing the hashfunction
	 * @param input data to be hashed
	 * @return Hash as a string
	 * @throws Exception 
	 */
	public static String generateHash(MetaData metadata, byte[] input) throws Exception
	{
		//create a message digest of the selected hashfunction of the input metadata
		MessageDigest hash = MessageDigest.getInstance(metadata.getHashFunction().toString(), "BC");
		
		//Apply the hash to the input
		hash.update(input);

		//finalize the hash generation by invoking the digest method and encoding it in base64
		String generatedHash= Base64.getEncoder().encodeToString(hash.digest());

		return generatedHash;
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
		//create an output array appropriate size, dependinng on the size of the plaintext and the encryption used
		byte [] output = new byte[cipher.getOutputSize(input.length)];
		
		//Ctlength stores the length of the data that has already been processed by the cipher
		//Arguments: input array,input offset, input length, output array, output offset
		int ctLength = cipher.update(input, 0, input.length, output, 0);
		
		//Finalize the encryption
		ctLength += cipher.doFinal(output, ctLength);
		
		//return the en/de - crypted output
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
		//Compare the two hashes using a message digest helper function.
		//If the hashed were not equal
		if(!MessageDigest.isEqual(Base64.getDecoder().decode(readHash) , Base64.getDecoder().decode(generateHash(fileData, input))))
		{
			//Throw a custom exception
			throw new Exception("File has been altered!");
		}
	}
	
	/**
	 * Decrypts a given byte array using the given metadata
	 * @param input Data to be encrypted
	 * @param fileData metadata containing the encryption that will be applied
	 * @return Encrypted data
	 * @throws Exception
	 */
	public static String decryptString(byte[] input, MetaData fileData) throws Exception
	{
		//Array to hold the plaintext
		byte[] plainText;
		
		//if the encryption type is something other than 'none'
		if(fileData.getEncryptionType() != EncryptionType.none)
		{
			//Prepare a cipher reference
			Cipher cipher = null;
			
			//Create the correct type of cipher based on the mode of operation
			switch(fileData.getEncryptionType().getOperationMode())
			{
			case Asymmetric:
				//Create a keyfactory to reproduce the private key from the keyfile
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				
				//get the private key from the file, as an PKCS8EncodedKeySpec
				PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(FileManager.getKeyFromFile(generateHash(fileData, input), fileData.getUsbData().getDriveLetter()));
				
				//generate the private key from the PKCS8EncodedKeySpec
				PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
				
				//Instantiate the cipher with a private key
				cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				
				break;
			case Passwordbased:
				//Create a keyfactory to generate a SecretKey from the input password
			    SecretKeyFactory keyFactoryPBE = SecretKeyFactory.getInstance(fileData.getEncryptionType().toString());
			    SecretKey skey = keyFactoryPBE.generateSecret(new PBEKeySpec(fileData.getPassword()));
			   
			    //Generate a cipher with the key, the salt from the metadata of the file and the iteration count.
			    cipher = generateCipher(Cipher.DECRYPT_MODE, fileData, skey, new PBEParameterSpec(fileData.getSalt(), iterationCount));
				break;
			case Symmetric:
				
				IvParameterSpec ivSpec = null; 
				
				//If the loaded filedata has 'content'
				if(fileData.getiV() != null)
				{
					//If the block mode uses an iv 
					if(fileData.getEncryptionMode().usesIV() && !fileData.getiV().equals("null"))
					{
						//Create an iv object from the iv that was loaded from the filedata
						ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
					}
					//If the filedata has content, but is not a proper iv
					else if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
					{
						//Throw a custom exception
						throw new Exception("IV WAS NOT SET");
					}
				}
				
				//Get the corresponding key from the keyfile directory
				Key key = new SecretKeySpec(FileManager.getKeyFromFile(generateHash(fileData, input), fileData.getUsbData().getDriveLetter()), fileData.getEncryptionType().toString());
				
				//Generate a cipher with an iv and the loaded key
				cipher = generateCipher(Cipher.DECRYPT_MODE, fileData, key, ivSpec);
				break;
			default:
				break;
			
			}
			
			//Apply the generated cipher and store the result in the plaintext array
			plainText = applyCipher(cipher, input);
		}
		//If the encryption type is 'none' or null
		else
		{
			//load the input as plaintext without decrypting anything
			plainText = input;
		}
		
		String outputString = new String(plainText, "UTF-8").trim();
		
		//Read the hash that was added to the end of the plaintext 
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
		
		//Validate the read hash
		validateHash(fileData, outputString.trim().getBytes(), hash);
		
		//Return the decrypted content
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
		//Init the keygen with an encryptiontype and keylength
		KeyGenerator generator = KeyGenerator.getInstance(encryptionType.toString(), "BC");
		generator.init(keyLength.returnAsInt());
		
		//Generate the key and return it
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
		//Init the KeyGen with a keylength and a securerandom object
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		generator.initialize(keyLength.returnAsInt(), random);
		
		//Generate the keypair and return it
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
			return generateRandomByteArray(8);
		case AES:
			return generateRandomByteArray(16);
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
	private static byte[] generateRandomByteArray(int length)
	{
		//If the desired length is greater than 0
		if(length > 0)
		{
			//Create a array and fill it with random bytes
			SecureRandom random = new SecureRandom();
			byte[] randomBytes = new byte[length];
			random.nextBytes(randomBytes);
			return randomBytes;
		}
		
		//If the desired length was not greater than 0, return an empty array
		return new byte[0];
	}
	
	/**
	 * Returns an iv one is needed. Returns a newly generated one if one is needed but not present, returns the present one if there is one. 
	 * @param fileData 
	 * @return
	 */
	private static IvParameterSpec getIvIfNeeded(MetaData fileData)
	{
		IvParameterSpec ivSpec = null;
		
		//if the mode uses an iv and the selected type is not ARC4
		if(fileData.getEncryptionMode().usesIV() && fileData.getEncryptionType() != EncryptionType.ARC4)
		{
			//Generate a random byte array with the matching length
			byte[] ivArray = generateMatchingIV(fileData.getEncryptionType());
			
			//save the iv in the passed filedata
			fileData.setiV(Base64.getEncoder().encodeToString(ivArray));
			
			//Create an IvParameterSpec to return
			ivSpec = new IvParameterSpec(ivArray);
			
		}
		
		//Return the generated object
		return ivSpec;
	}
	
	
}

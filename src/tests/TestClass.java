import java.security.Security;
import java.util.Set;
import java.util.TreeSet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.registry.AlgorithmRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClass {
	
    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void test() {
        Set<String> supported = new TreeSet<>();
        Set<String> unsupported = new TreeSet<>();
        for (Object oAlgorithm : AlgorithmRegistry.getAllPBEAlgorithms()) {
            String algorithm = (String) oAlgorithm;
            try {
                SimpleStringPBEConfig pbeConfig = new SimpleStringPBEConfig();
                pbeConfig.setAlgorithm(algorithm);
                pbeConfig.setPassword("changeme");
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setConfig(pbeConfig);

                String encrypted = encryptor.encrypt("foo");
                String decrypted = encryptor.decrypt(encrypted);
                Assert.assertEquals("foo", decrypted);
                supported.add(algorithm);
            } catch (EncryptionOperationNotPossibleException e) {
                unsupported.add(algorithm);
            }
        }
        System.out.println("Supported");
        supported.forEach((String alg) -> System.out.println("   " + alg)); 
        System.out.println("Unsupported");
        unsupported.forEach((String alg) -> System.out.println("   " + alg)); 
    }
}
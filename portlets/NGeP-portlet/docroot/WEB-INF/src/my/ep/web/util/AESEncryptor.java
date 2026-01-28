package my.ep.web.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
// For Java 7 Base64

public class AESEncryptor {

    // Encryption method (same as before)
    public static String encrypt(String payload, String secretKey) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        byte[] iv = new byte[16];
        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(
            secretKey.toCharArray(), 
            salt, 
            65536, 
            256
        );
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey aesKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[salt.length + iv.length + encryptedBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, salt.length + iv.length, encryptedBytes.length);

        // Java 7-compatible Base64
        return DatatypeConverter.printBase64Binary(combined);
    }

    // New decryption method
    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        // Base64 decode (Java 7 compatible)
        byte[] combined = DatatypeConverter.parseBase64Binary(encryptedData);
        
        // Extract components
        byte[] salt = new byte[8];
        byte[] iv = new byte[16];
        byte[] encryptedBytes = new byte[combined.length - salt.length - iv.length];
        
        System.arraycopy(combined, 0, salt, 0, salt.length);
        System.arraycopy(combined, salt.length, iv, 0, iv.length);
        System.arraycopy(combined, salt.length + iv.length, encryptedBytes, 0, encryptedBytes.length);

        // Regenerate AES key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(
            secretKey.toCharArray(),
            salt,
            65536,
            256
        );
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey aesKey = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Decrypt payload
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
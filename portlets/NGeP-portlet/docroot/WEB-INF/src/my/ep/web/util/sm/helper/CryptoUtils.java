package my.ep.web.util.sm.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CryptoUtils {

	private static final int SALT_LENGTH = 16;
	private static final int IV_LENGTH = 16;
	private static final int AES_KEY_LENGTH_BYTES = 16; // 128-bit AES, safe for
														// Java 7
	private static final int HMAC_KEY_LENGTH_BYTES = 32; // 256-bit HMAC key
	private static final int HMAC_LENGTH_BYTES = 32;
	private static final int ITERATIONS = 65536;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String HMAC_SHA_256 = "HmacSHA256";

	public static String encryptAES(String plainText, String secretKey) throws Exception {
		byte[] salt = new byte[SALT_LENGTH];
		byte[] iv = new byte[IV_LENGTH];

		SECURE_RANDOM.nextBytes(salt);
		SECURE_RANDOM.nextBytes(iv);

		DerivedKeys keys = deriveKeys(secretKey, salt);

		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, keys.aesKey, new IvParameterSpec(iv));

		byte[] plainBytes = plainText == null ? new byte[0] : plainText.getBytes(StandardCharsets.UTF_8);

		byte[] encryptedBytes = cipher.doFinal(plainBytes);

		byte[] dataToAuthenticate = concat(salt, iv, encryptedBytes);

		Mac mac = Mac.getInstance(HMAC_SHA_256);
		mac.init(keys.hmacKey);
		byte[] hmac = mac.doFinal(dataToAuthenticate);

		byte[] finalPayload = concat(dataToAuthenticate, hmac);

		return DatatypeConverter.printBase64Binary(finalPayload);
	}

	public static String decryptAES(String encryptedData, String secretKey) throws Exception {
		byte[] combined = DatatypeConverter.parseBase64Binary(encryptedData);

		int minimumLength = SALT_LENGTH + IV_LENGTH + HMAC_LENGTH_BYTES;

		if (combined.length < minimumLength) {
			throw new IllegalArgumentException("Invalid encrypted data");
		}

		byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH);

		byte[] iv = Arrays.copyOfRange(combined, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);

		byte[] encryptedBytes = Arrays.copyOfRange(combined, SALT_LENGTH + IV_LENGTH, combined.length - HMAC_LENGTH_BYTES);

		byte[] receivedHmac = Arrays.copyOfRange(combined, combined.length - HMAC_LENGTH_BYTES, combined.length);

		byte[] dataToAuthenticate = Arrays.copyOfRange(combined, 0, combined.length - HMAC_LENGTH_BYTES);

		DerivedKeys keys = deriveKeys(secretKey, salt);

		Mac mac = Mac.getInstance(HMAC_SHA_256);
		mac.init(keys.hmacKey);
		byte[] calculatedHmac = mac.doFinal(dataToAuthenticate);

		if (!MessageDigest.isEqual(receivedHmac, calculatedHmac)) {
			throw new SecurityException("Invalid authentication tag");
		}

		Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, keys.aesKey, new IvParameterSpec(iv));

		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	private static DerivedKeys deriveKeys(String secretKey, byte[] salt) throws Exception {
		int totalKeyLengthBits = (AES_KEY_LENGTH_BYTES + HMAC_KEY_LENGTH_BYTES) * 8;

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

		KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt, ITERATIONS, totalKeyLengthBits);

		SecretKey tmp = factory.generateSecret(spec);
		byte[] keyBytes = tmp.getEncoded();

		byte[] aesKeyBytes = Arrays.copyOfRange(keyBytes, 0, AES_KEY_LENGTH_BYTES);

		byte[] hmacKeyBytes = Arrays.copyOfRange(keyBytes, AES_KEY_LENGTH_BYTES, AES_KEY_LENGTH_BYTES + HMAC_KEY_LENGTH_BYTES);

		SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
		SecretKey hmacKey = new SecretKeySpec(hmacKeyBytes, HMAC_SHA_256);

		return new DerivedKeys(aesKey, hmacKey);
	}

	private static byte[] concat(byte[]... arrays) {
		int totalLength = 0;

		for (byte[] array : arrays) {
			totalLength += array.length;
		}

		byte[] result = new byte[totalLength];
		int offset = 0;

		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}

	private static class DerivedKeys {
		private final SecretKey aesKey;
		private final SecretKey hmacKey;

		private DerivedKeys(SecretKey aesKey, SecretKey hmacKey) {
			this.aesKey = aesKey;
			this.hmacKey = hmacKey;
		}
	}
}

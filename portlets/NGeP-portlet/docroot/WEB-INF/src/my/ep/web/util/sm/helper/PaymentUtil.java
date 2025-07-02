package my.ep.web.util.sm.helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.AESEncryptor;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class PaymentUtil {
	public static Logger log = Logger.getLogger(PaymentUtil.class);
	private static PrivateKey privKey;

	private static final String PRIVATE_PKCS_1_PEM_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
	private static final String PRIVATE_PKCS_1_PEM_FOOTER = "-----END RSA PRIVATE KEY-----";
	private static final String PRIVATE_PKCS_8_PEM_HEADER = "-----BEGIN PRIVATE KEY-----";
	private static final String PRIVATE_PKCS_8_PEM_FOOTER = "-----END PRIVATE KEY-----";

	private static PrivateKey getPrivKey() {
		if (privKey == null) {
			log.info("********private key is null");
			try {
				privKey = loadPrivateKey();
			} catch (IOException | GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return privKey;

	}

	// Encrypt text using AES key
	public static String encryptTextUsingAES(String plainText,
			String aesKeyString) throws Exception {
		byte[] decodedKey = Base64.decodeBase64(aesKeyString);
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0,
				decodedKey.length, "AES");

		// AES defaults to AES/ECB/PKCS5Padding in Java 7
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, originalKey);
		byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
		return Base64.encodeBase64String(byteCipherText);
	}

	// Encrypt AES Key using RSA private key
	public static String encryptAESKey(String plainAESKey) {
		log.info("plain key:" + plainAESKey);
		PrivateKey privateKey = getPrivKey();
		Cipher cipher;
		String encryptedAESKey = "";
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			encryptedAESKey = Base64.encodeBase64String(cipher
					.doFinal(plainAESKey.getBytes()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedAESKey;
	}

	private static PrivateKey loadPrivateKey() throws GeneralSecurityException,
			IOException {
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		String keyPath = prop.getProperty("payment.key.path");
		log.info("payment pub key path:" + keyPath);
		String keyString = getKey(keyPath);

		byte[] keyDataBytes = keyString.getBytes(StandardCharsets.UTF_8);
		String keyDataString = new String(keyDataBytes, StandardCharsets.UTF_8);

		if (keyDataString.contains(PRIVATE_PKCS_1_PEM_HEADER)) {
			// OpenSSL / PKCS#1 Base64 PEM encoded file
			keyDataString = keyDataString
					.replace(PRIVATE_PKCS_1_PEM_HEADER, "");
			keyDataString = keyDataString
					.replace(PRIVATE_PKCS_1_PEM_FOOTER, "");
			return readPkcs8PrivateKey(constructPkcs8(Base64
					.decodeBase64(keyDataString)));
		}

		if (keyDataString.contains(PRIVATE_PKCS_8_PEM_HEADER)) {
			// PKCS#8 Base64 PEM encoded file
			keyDataString = keyDataString
					.replace(PRIVATE_PKCS_8_PEM_HEADER, "");
			keyDataString = keyDataString
					.replace(PRIVATE_PKCS_8_PEM_FOOTER, "");
			return readPkcs8PrivateKey(Base64.decodeBase64(keyDataString));
		}

		// We assume it's a PKCS#8 DER encoded binary file
		return readPkcs8PrivateKey(keyDataBytes);
	}

	private static PrivateKey readPkcs8PrivateKey(byte[] pkcs8Bytes)
			throws GeneralSecurityException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8Bytes);
		try {
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new IllegalArgumentException("Unexpected key format!", e);
		}
	}

	private static byte[] constructPkcs8(byte[] pkcs1Bytes)
			throws GeneralSecurityException {
		// We can't use Java internal APIs to parse ASN.1 structures, so we
		// build a
		// PKCS#8 key Java can understand
		int pkcs1Length = pkcs1Bytes.length;
		int totalLength = pkcs1Length + 22;
		byte[] pkcs8Header = new byte[] {
				0x30,
				(byte) 0x82,
				(byte) ((totalLength >> 8) & 0xff),
				(byte) (totalLength & 0xff), // Sequence + total length
				0x2,
				0x1,
				0x0, // Integer (0)
				0x30, 0xD, 0x6, 0x9, 0x2A, (byte) 0x86, 0x48, (byte) 0x86,
				(byte) 0xF7, 0xD, 0x1, 0x1,
				0x1,
				0x5,
				0x0, // Sequence:
						// 1.2.840.113549.1.1.1,
						// NULL
				0x4, (byte) 0x82, (byte) ((pkcs1Length >> 8) & 0xff),
				(byte) (pkcs1Length & 0xff) // Octet string +
											// length
		};
		byte[] pkcs8bytes = join(pkcs8Header, pkcs1Bytes);
		return pkcs8bytes;
	}

	private static byte[] join(byte[] byteArray1, byte[] byteArray2) {
		byte[] bytes = new byte[byteArray1.length + byteArray2.length];
		System.arraycopy(byteArray1, 0, bytes, 0, byteArray1.length);
		System.arraycopy(byteArray2, 0, bytes, byteArray1.length,
				byteArray2.length);
		return bytes;
	}

	private static String getKey(String filename) throws IOException {
		// Read key from file
		String strKeyPEM = "";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			strKeyPEM += line + "\n";
		}
		br.close();
		return strKeyPEM;
	}

	public static String constructPayload(String merchantCode,
			String clientKey, Long orderId, BigDecimal amount, String billName,
			String email, String mobile, String billDesc, String country,
			String cancelUrl) {
		String payload = "";
		StringBuilder sb = new StringBuilder();
		sb.append("merchantCode#").append(merchantCode).append(";");
//		sb.append("clientSecret#").append(clientKey).append(";");
		sb.append("orderId#").append(orderId).append(";");
		sb.append("amount#").append(amount.setScale(2, RoundingMode.HALF_UP))
				.append(";");
		sb.append("billName#").append(billName).append(";");
		sb.append("email#").append(email).append(";");
		sb.append("mobile#").append(mobile).append(";");
		sb.append("billDesc#").append(billDesc).append(";");
		sb.append("country#").append(country).append(";");
		sb.append("cancelUrl#").append(cancelUrl);
		try {
			payload = AESEncryptor.encrypt(sb.toString(), clientKey);
//			payload = encryptWithAES(sb.toString(), clientKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return payload;
	}
}

package my.ep.web.util.sm.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.id.Hex;
import org.apache.log4j.Logger;

public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);
	
	public static String hashSupplierId(String supplierId) {
		
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
//            return;
        }
        md.update(supplierId.getBytes());
        byte[] shaDig = md.digest();
        return new String(Hex.encodeHex(shaDig));
	}
}

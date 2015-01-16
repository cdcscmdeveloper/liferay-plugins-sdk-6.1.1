package my.ep.web.integration.common;

public class IntegrationConstants {

	// for PHIS
	public static final String PHIS_BUSINESS_AREA = "2014";
	public static final String GMAS_BUSINESS_AREA = "1000";
	public static final String AGENSI_ID_ePerolehan = "400001";
	public static final String AGENSI_ID_PHIS = "400002";
	// for PHIS
	public static final String FILE_EXTENSION_PHIS = ".EPN";
	public static final String FILE_EXTENSION_GFMAS = ".GPG";
	public static final String SEQUENCE_NO = "001";

	public static final String FILE_PATH = "C:/Integration/Upload/";

	public static final String GMAS_UPLOAD = "batch/1GFMAS/OUT/";
	public static final String GMAS_DOWNLOAD = "/batch/1GFMAS/IN/";

	public static final String PHIS_UPLOAD_DIR = "/batch/PHIS/OUT/";
	
	public static final String EPERUNDING_UPLOAD_DIR = "/batch/ePerunding/IN/";
	
	public static final String ENCRYPTED_FILE_PATH="C:/Integration/Encryption/encrypt/";

	public static final String SFTP_HOST = "192.168.62.15";
	public static final int SFTP_PORT = 22;// Integer.parseInt("22");
	public static final String SFTP_USER = "oracle";
	public static final String SFTP_PASS = "welcome1";
	
	public static final String GMAS_PUBLIC_KEY_PATH = "./NGeP-portlet/docroot/encryptionKeys/1gfmasdev_pub.id";
	public static final String GMAS_PRIVATE_KEY_PATH = "./NGeP-portlet/docroot/encryptionKeys/privatekey";

	public static final String GMAS_PASS_PHASE = "eperolehanpsstg";
	
	public static final String PHIS_PUBLIC_KEY_PATH = "./NGeP-portlet/docroot/encryptionKeys/PLSB publickey.asc";
	
	public static final String CRE="CRE";
	public static final String ADJ="ADJ";
	public static final String DEL="DEL";
	
	public static final String OSB_HOST="192.168.62.17";
	
	public static final String EMPTY_TRING="";
	
	public static final String CONSUMER_ID = "EPP-001";
	

}

package my.ep.web.integration.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class IntegrationConstants {

	public static final String PHIS_BUSINESS_AREA = "2014";
	public static final String GMAS_BUSINESS_AREA = "1000";
	public static final String AGENSI_ID_ePerolehan = "400001";
	public static final String AGENSI_ID_PHIS = "400002";
	public static final String FILE_EXTENSION_PHIS = ".EPN";
	public static final String FILE_EXTENSION_GFMAS = ".GPG";
	public static final String SEQUENCE_NO = "001";

//	public static final String FILE_PATH = "/batch/Upload/";
	public static final String FILE_PATH = "C:/Integration/Upload/";

	public static final String GMAS_UPLOAD = "/batch/1GFMAS/OUT/";
	public static final String GMAS_DOWNLOAD = "/batch/1GFMAS/IN/";
	public static final String GMAS_ARCHIVE = "/batch/1GFMAS/ARCHIVE/";

	public static final String PHIS_UPLOAD_DIR = "/batch/PHIS/OUT/";
	public static final String PHIS_ARCHIVE_DIR = "/batch/PHIS/ARCHIVE/";

	public static final String EPERUNDING_UPLOAD_DIR = "/batch/ePerunding/OUT/";
	public static final String EPERUNDING_DOWNLOAD_DIR = "/batch/ePerunding/IN/";
	public static final String EPERUNDING_ARCHIVE_DIR = "/batch/ePerunding/ARCHIVE/";

//	public static final String ENCRYPTED_FILE_PATH="/batch/Encryption/encrypt/";
	public static final String ENCRYPTED_FILE_PATH = "C:/Integration/Encryption/encrypt/";

//	 public static final String DECRYPTED_FILE_PATH="/batch/Encryption/decrypt/";
	public static final String DECRYPTED_FILE_PATH = "C:/Integration/Encryption/decrypt/";

	public static final String SFTP_HOST = "192.168.62.15";    //Stg
//	public static final String SFTP_HOST = "192.168.120.14";   //Dev
	public static final int SFTP_PORT = 22;// Integer.parseInt("22");
	public static final String SFTP_USER = "oracle";
	public static final String SFTP_PASS = "welcome1";

	public static final String GMAS_PUBLIC_KEY_PATH = "NGeP-portlet.war/encryptionKeys/1gfmasdev_pub.id";
	public static final String GMAS_PRIVATE_KEY_PATH = "NGeP-portlet.war/encryptionKeys/privatekey";

	public static final String GMAS_PASS_PHASE = "eperolehanpsstg";

	public static final String PHIS_PUBLIC_KEY_PATH = "NGeP-portlet.war/encryptionKeys/PLSB publickey.asc";

	public static final String CRE = "CRE";
	public static final String ADJ = "ADJ";
	public static final String DEL = "DEL";

	public static final String OSB_HOST = "192.168.62.17";

	public static final String SERVICE_HOST = "192.168.120.14";

	public static final String EMPTY_TRING = "";

	public static final String CONSUMER_ID = "EPP-001";

	public static final int DEFAULT_INT = 0;

	public static final Date DEFAULT_DATE = new Date();

	public static final String VAVLUE_YES = "Y";

	public static final String VAVLUE_NO = "N";

	public static final String VAVLUE_NA = "N/A";

	public static final String VALUE_ONE = "1";

	public static final String VALUE_ZERO = "0";

	public final static Charset ENCODING = StandardCharsets.UTF_8;

	public static final Long CHANGED_BY = 12345L;

	public static final String SERVICE_SUCCESS_CODE = "00";
	
	public static final String CONSULTANT_PROFILE_CODE = "J";
	
	public static final String GL_RECON_NO = "L0190000";
	
	public static final String CURRENCY_CODE_MY = "RM";
	
	public static final String COUNTRY_CODE_MY = "MY";
	
	public static final int RECORD_STATUS_SUSPENDED = 0;
	public static final int RECORD_STATUS_ACTIVE = 1;
	public static final int RECORD_STATUS_CANCELLED = 2;
	public static final int RECORD_STATUS_EXPIRED = 3;
	public static final int RECORD_STATUS_DELETED = 9;
	
	public static final String WORK_PROGRESS_CMPLTD = "S";
	public static final String WORK_PROGRESS_INPGRS = "P";
	public static final String WORK_PROGRESS_FAILURE = "F";
	

}

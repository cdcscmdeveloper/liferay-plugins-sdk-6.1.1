package my.ep.web.integration.common;

/*
 *@author P Ganga prasad
 */
public interface ErrorMessages {

	public static final String DUPLICATE_RECORD_1 = "Pejabat Perakaunan Already Created";
	public static final String DUPLICATE_RECORD_2 = "Pegawai Pengawal Already Created";
	public static final String DUPLICATE_RECORD_3 = "Vot Fund Already Created";
	public static final String DUPLICATE_RECORD_4 = "Trust and General Fund (Dana) Already Created";
	public static final String DUPLICATE_RECORD_5 = "program / aktiviti Already Created";
	public static final String DUPLICATE_RECORD_6 = "Project Already Created";
	public static final String DUPLICATE_RECORD_7 = "Kod Akaun Already Created";
	public static final String DUPLICATE_RECORD_8 = "Kumpulan PTJ Already Created";
	public static final String DUPLICATE_RECORD_9 = "Pusat Tanggungjawab Already Created";
	public static final String DUPLICATE_RECORD_10 = "Pusat Tanggungjawab Already Created";
	public static final String DUPLICATE_RECORD_11 = "Kod Bank (Swift Code) Already Created";
	public static final String DATE_CONVERSION_ERROR = "X is invalid format";
	public static final String LENGTH_ERROR = "Record length is mis matched check the record with IDD";
	public static final String DEPENDENCY_ERROR_1 = "There is no Kumpulan PTJ master data for code X";
	public static final String DEPENDENCY_ERROR_2 = "There is no Vot Fund master data for code X";
	public static final String DEPENDENCY_ERROR_3 = "There is no Pejabat Perakaunan master data for code X";
	public static final String DEPENDENCY_ERROR_4 = "There is no Pegawai Pengawal master data for code X";
	public static final String FILE_CORRUPT_ERROR = "Failed to open file";
	public static final String ENCRYPTION_ERROR = "Failed to decrypt file";
	public static final String MANDATORY_COLUMN_NULL = "X null is invalid";

}

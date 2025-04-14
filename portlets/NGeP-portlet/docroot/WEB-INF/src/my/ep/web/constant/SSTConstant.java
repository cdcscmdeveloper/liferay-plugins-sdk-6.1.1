package my.ep.web.constant;

/**
 * Contain Surat Setuju Terima Constant variable
 * 
 * **/
public class SSTConstant {
	

	//Sonarqube #G29 - start
	public static final String SuratSetujuTerima = "SuratSetujuTerima(TermasukLampiranA";
	public static final String ScLoa = "scLoa";
	public static final String fileTypeGz = ".gz";
	public static final String fileTypeMergedPdfGz = "_merged.pdf.gz";
	public static final String fileTypePdfGz = ".pdf.gz";
	public static final String fileTypePdf = ".pdf";
	public static final String fileTypeTempPdf = "_temp.pdf";
	public static final String fileTypeMergedPdf = "_merged.pdf";
	public static final String masters = "masters";
	public static final String perkhidmatan = "Perkhidmatan";
	public static final String service = "Service";
	public static final String produk = "Produk";
	public static final String product = "Product";
	public static final String specCombineProduct = "specCombineProduct";
	public static final String specCombineService = "specCombineService";
	public static final String daripada = "daripada";
	public static final String pageNo = "pageNo";
	public static final String serviceItemPageNo = "serviceItemPageNo";
	public static final String TB = "T/B";
	public static final String NA = "N/A";
	public static final String whiteSpace = "&nbsp;";
	public static final String nbsp = "&nbsp";
	public static final String clItem = "clItem";
	public static final String uomName = "uomName";
	public static final String spesifikasi = "spesifikasi";
	public static final String clPriceTypeCode = "clPriceTypeCode";
	public static final String clUomFrequency = "clUomFrequency";
	public static final String clUomPerDay = "clUomPerDay";
	public static final String clUomPerMonth = "clUomPerMonth";
	public static final String quantity = "qty";
	public static final String tierQuantity = "tierQty";
	public static final String item = "I";
	public static final String specification = "S";
	public static final String specValue ="specVal";
	public static final String specType = "specType";
	public static final String itemType = "itemType";
	public static final String rowSpan = "rowSpan";
	public static final String questionType = "questionType";
	public static final String financial = "F";
	public static final String technical = "T";
	public static final String zoneName = "zoneName";
	public static final String answerNumeric = "answerNumeric";
	public static final String Ya = "Ya";
	public static final String Tidak = "Tidak";
	public static final String Feedback = "feedback";
	public static final String Remark = "remark";
	public static final String qtyBerperingkat = "qtyBerperingkat";
	public static final String answerRemark = "answerRemark";
	public static final String isZonal = "isZonal";
	public static final String serviceScope = "serviceScope";
	public static final String bean = "bean";
	//26226 - start
	public static final String SUPPLIER_NAME = "supplierName";
	public static final String ZONE_ID = "zoneId";
	public static final String LANGUAGE_CODE_EN = "en";
	//26226 - end
	//Sonarqube #G29 - end
	//25025 - start
	public static final String HEADER_PEMBELIAN_TERUS = "Pembelian Terus";
	public static final String EMPTY_STRING = "";
	public static final String HEADER_TITLE = "headerTitle";
	public static final String SUBJECT_TITLE = "subTitle";
	public static final String SIGN_PERCENTAGE = "%";
	public static final String SIGN_RINGGIT = "RM";
	public static final String UNDERLINE = "_____________________________";
	public static final String BOND_PERCENTAGE_TEXT = "bondPercentageText";
	public static final String BOND_PERCENTAGE = "bondPercentage";
	public static final String BOND_AMOUNT = "bondAmt";
	public static final String BOND_AMOUNT_TEXT = "bondAmtText";
	public static final String CONTRACT_EFFECTIVE_DATE = "contractEffDate";
	public static final String CONTRACT_EXPIRED_DATE = "contractExpDate";
	public static final String OVERALL_CONTRACT_AMOUNT = "overallCtAmt";
	public static final String OVERALL_CONTRACT_AMOUNT_WORDING = "overallCtAmtWording";
	public static final String SST_AMOUNT = "sstAmt";
	public static final String OFFER_PRICE = "offerPrice";
	public static final String OFFER_PRICE_WORDING = "offerPriceWording";
	public static final String GST_REG_NO = "gstRegNo";
	public static final String SSM_EXPIRED = "ssmExpired";
	public static final String REGISTRATION_NO = "regNo";
	public static final String SEPARATOR = ",";
	public static final String LINE_BREAK = "\n";
	public static final String BUMIPUTERA = "Bumiputera";
	public static final String BUKAN_BUMIPUTERA = "Bukan Bumiputera";
	public static final String UNDERLINE_BOND_AMT = "__________________________";
	public static final String PROTEGE_UPPERCASE_E = "&#201;";
	public static final String PROTEGE_LOWERCASE_E = "&#233;";
	public static final String LESS_THAN_SYMBOL = "&#60;";
	public static final String GREATER_THAN_SYMBOL = "&#62;";
	public static final String AND_SYMBOL = "&#38;";
	public static final String BREAKLINE ="<br />";
	
	//25025 - end
	//29006 start
	
	/**
	 * Represent fulfilment type code for Lampiran A3 
	 */
	public static final String CL_FULFILMENT_TYPE_CODE = "clFulfilmentTypeCode";
	/**
	 * Represent value for month duration in Lampiran A3
	 */
	public static final String CL_DURATION_MONTH = "clDurationMonth";
	/**
	 * Represent value for supplier offer in Lampiran A3
	 */
	public static final String SUPPLIER_OFFER = "supplierOffer";
	/**
	 * Contain total zone in Lampiran A3.
	 * Total zone is required to merge the table data for zonal
	 */
	public static final String ZONE_SIZE = "ZoneSize";
	/**
	 * Contain total specification available for the item.
	 *
	 */
	public static final String SPEC_SIZE = "sizeSpec";
	/**
	 * A flag to validate if the item is WIP.
	 */
	public static final String IS_WIP = "isWip";
	/**
	 * A flag to validate if the item is the last item in the list.
	 * This flag to merge the last column for supplier offer to prevent
	 * the table border missing at the bottom. 
	 */
	public static final String LAST_INDEX_WIP = "lastIndexWip";
	
	/**
	 * Represent HTML Entity line break.
	 */
	public static final String HTML_ENTITY_LINE_SEPARATOR = "<br/>";

	/**
	 * Constant representing the prefix for a new supplier offer.
	 * This is used as a standard identifier or key prefix in various parts of the application.
	 */
	public static final String NEW_SUPPLIER_OFFER_ = "NSO_";

	/**
	 * Constant representing the prefix for a new supplier offer justification.
	 * This prefix is typically used to identify or reference justifications related to new supplier offers.
	 */
	public static final String NEW_SUPPLIER_OFFER_JUSTIFICATON_ = "NSOJ_";
	//29006 end
	//35295 @zaim - start
	/**
	 * Represent rowspan needed for item and spec in SST
	 */
	 public static final String ROWSPAN_ITEM_NAME = "rowSpanItem";
	/**
	 * Represent rowspan needed for item for zonal in SST
	 */
	public static final String ROWSPAN_ZONE_QUANTITY = "rowSpanZone";
	/**
	 * Represent number of row for non zonal
	 */
	public static final Integer NON_ZONAL_ROW = 1;
	/**
	 * Represent list of purchase request that contain data to generate Surat Setuju Terima
	 */
	public static final String LIST_PURCHASE_REQUEST_SST = "PRSSTList";	
	//35295 @zaim - end
	//36122 - start
	/**
	 * Add whitespace to the text
	 */
	public static final String ADD_WHITESPACE = " ";
	//36122 - end
	//35295 - start
	/**
	 * Represent temporary string before replace with <br>
	 */
	public static final String WORD_BREAK_TO_REMOVE = "/AddBreakLineHere/";
	/**
	 * Represent PDF file format
	 */
	public static final String FILE_TYPE_PDF = ".pdf";
	/**
	 * Represent Microsoft Word Document file format
	 */
	public static final String FILE_TYPE_DOC = ".doc";
	
	//35295 - end
}

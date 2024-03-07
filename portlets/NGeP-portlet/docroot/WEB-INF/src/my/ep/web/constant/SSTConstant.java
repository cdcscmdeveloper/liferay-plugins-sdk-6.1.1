package my.ep.web.constant;

public class SSTConstant {

	// 26226 - start
	public static String SUPPLIER_NAME = "supplierName";
	public static String ZONE_ID = "zoneId";
	public static String LANGUAGE_CODE_EN = "en";
	public static String TB = "T/B";
	public static String NA = "N/A";
	public static String specification = "S";
	public static String isZonal = "isZonal";
	public static String serviceScope = "serviceScope";
	public static String bean = "bean";
	public static String masters = "masters";
	public static String itemType = "itemType";
	public static String EMPTY_STRING = "";
	public static String questionType = "questionType";
	public static String clItem = "clItem";
	public static String uomName = "uomName";
	public static String whiteSpace = "&nbsp;";
	public static String clUomFrequency = "clUomFrequency";
	public static String clUomPerDay = "clUomPerDay";
	public static String clUomPerMonth = "clUomPerMonth";
	public static String quantity = "qty";
	public static String zoneName = "zoneName";
	public static final String LESS_THAN_SYMBOL = "&#60;";
	public static final String GREATER_THAN_SYMBOL = "&#62;";
	public static String answerRemark = "answerRemark";
	
	
	
	// 26226 - end
	
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

	//29006 end
}


/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author Kenny
 * @since 10.04.2014
 *
 */
public class CommonUtils {
	private static Map<String, Integer> uomMap = new HashMap<String, Integer>();
	
	/**
	 * Get the display date time format pattern by date format type.
	 * 
	 * @param dateFormatType
	 * 		date format type
	 * @return
	 */
	public String getDisplayDateTimeFormat(int dateFormatType) {
		String dateFormat = null;
		
		switch (dateFormatType) {
			case CommonConstants.DATE_FORMAT_TYPE_DATE : 
				dateFormat = CommonConstants.DEFAULT_DATE_FORMAT;
				break;
			case CommonConstants.DATE_FORMAT_TYPE_TIME : 
				dateFormat = CommonConstants.DEFAULT_12HR_TIME_FORMAT;
				break;
			case CommonConstants.DATE_FORMAT_TYPE_DATETIME : 
				dateFormat = CommonConstants.DEFAULT_DATETIME_FORMAT;
				break;
			case CommonConstants.DATE_FORMAT_TYPE_DATETIME_WITH_SECONDS : 
				dateFormat = CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT;
				break;
			default : 
				throw new IllegalArgumentException("Invalid dateFormatType.");
		}
		
		return dateFormat;
	}
	
	/**
	 * Get the display number format pattern by number format type.
	 * 
	 * @param numberFormatType
	 * 		number format type
	 * @return
	 */
	public String getDisplayNumberFormat(int numberFormatType) {
		int decimalPrecision = 0;
		
		switch (numberFormatType) {
			case CommonConstants.NUMBER_FORMAT_TYPE_INTEGER :
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT : decimalPrecision = 2;
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE : decimalPrecision = 2;
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE_EQUITY : decimalPrecision = 3;
				break;
			default : 
				throw new IllegalArgumentException("Invalid numberFormatType.");
		}
		
		return genNumberFormatDecimalPrecision(decimalPrecision);
	}
	
	/**
	 * Get the display quantity number format pattern by uom code.
	 * 
	 * @param numberFormatType
	 * 		number format type
	 * @param uomCode
	 * 		uom code
	 * @return
	 */
	public String getDisplayNumberFormat(int numberFormatType, String uomCode) {
		int decimalPrecision = 0;
		
		switch (numberFormatType) {
			case CommonConstants.NUMBER_FORMAT_TYPE_QTY : 				
				if (StringUtils.isNotBlank(uomCode)) {
					Integer uomDecimalPrecision = uomMap.get(uomCode);
					if (uomDecimalPrecision == null) {
						IUomSO uomSO;
						try {
							uomSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
							
							UomReqDTO reqDTO = new UomReqDTO();
							reqDTO.setUomCode(uomCode);
							
							// Get PmUom from db.
							UomResDTO resDTO = uomSO.retrieveUomByCode(reqDTO);
							PmUom pmUom = resDTO.getPmUom();
							if (pmUom != null) {
								// Put UOM into map.
								decimalPrecision = pmUom.getDecimalScale();
								uomMap.put(uomCode, decimalPrecision);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							throw new EpException(e.getMessage());
						} finally {
							uomSO = null;
						}	
					} else {
						decimalPrecision = uomDecimalPrecision;
					}
				}
				
				break;
			default :
				throw new IllegalArgumentException("Invalid numberFormatType.");
		}
		
		return genNumberFormatDecimalPrecision(decimalPrecision);
	}
	
	private String genNumberFormatDecimalPrecision(int decimalPrecision) {
		StringBuilder numberFormat = new StringBuilder(CommonConstants.DEFAULT_NUMBER_FORMAT);
		
		if (decimalPrecision > 0) {
			numberFormat.append(".");
			
			for (int i = 0; i < decimalPrecision; i++) {
				numberFormat.append("0");
			}
		}
		
		return numberFormat.toString();
	}
	
	public int convertStringToInteger(String word){
		return Integer.parseInt(word);
	}
	
	/**
	 * Get the formated date based on specific format.
	 * 
	 * @param actualDate
	 * @param format
	 * @return formattedDate
	 */
	public String getFormattedDate(Date actualDate, String format) {
		
		String formattedDate = null;
		if (actualDate != null) {
			SimpleDateFormat dateSDF = new SimpleDateFormat(format);
			formattedDate = dateSDF.format(actualDate);
		}
		return formattedDate;
	}
	
	//#25000 - Start
	/**
	 * Processes the given string by replacing newline characters with HTML line break tags
	 * and encoding the < and > characters to their HTML entity equivalents. If the string
	 * is null or empty, it returns null.
	 *
	 * @param value the string to be processed; may be null
	 * @return the processed string value with newline characters replaced by &lt;br/&gt;
	 *         and <, > characters encoded to &lt; and &gt; respectively; {@code null} if the input is null or empty
	 */
	public String encodeHtmlStringOrNull(String value) {
	    if (value == null || value.isEmpty()) {
	        return null;
	    }
	    // Replace < and > with their HTML entity equivalents
	    String formatted = value.replace("<", "&lt;").replace(">", "&gt;");
	    // Replace newline characters with <br/>
	    return formatted.replace("\n", "<br/>");
	}
	//#25000 - End
	
	
	// #31344 start
	/**
	 * Filter item name to remove "N/A" or “NA” or “N A”.
	 * 
	 * @param itemName
	 * @return itemName
	 */
	public String filterItemNameNA(String itemName) {
		if(itemName != null && !itemName.isEmpty()){
			return itemName.replaceAll("\\s(NA|N/A|N A)\\b", "");
		}
		return itemName;
	}
	// #31344 end
	
	/**
	 * #31503 Remove "N/A" or “NA” or “N A”.
	 * @param itemName
	 * @return
	 */
	public String removeItemNameNA(String itemName) {
		if (itemName != null && !itemName.isEmpty()) {
			return itemName.replaceAll("\\bN\\s*A\\b|\\bN/A\\b|\\bNA\\b", "");
		}
		return itemName;
	}
	
}

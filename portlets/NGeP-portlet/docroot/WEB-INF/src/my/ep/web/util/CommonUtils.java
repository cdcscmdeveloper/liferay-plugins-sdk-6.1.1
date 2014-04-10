/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import my.ep.constants.CommonConstants;

/**
 * @author Kenny
 * @since 10.04.2014
 *
 */
public class CommonUtils {
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
				dateFormat = CommonConstants.DEFAULT_TIME_FORMAT;
				
				break;
			case CommonConstants.DATE_FORMAT_TYPE_DATETIME : 
				dateFormat = CommonConstants.DEFAULT_DATETIME_FORMAT;
				
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
			default : 
				throw new IllegalArgumentException("Invalid numberFormatType.");
		}
		
		return generateDecimalPrecision(decimalPrecision);
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
			case CommonConstants.NUMBER_FORMAT_TYPE_QTY : decimalPrecision = 3;
				break;
			default :
				throw new IllegalArgumentException("Invalid numberFormatType.");
		}
		
		return generateDecimalPrecision(decimalPrecision);
	}
	
	private String generateDecimalPrecision(int decimalPrecision) {
		StringBuilder numberFormat = new StringBuilder(CommonConstants.DEFAULT_NUMBER_FORMAT);
		
		if (decimalPrecision > 0) {
			numberFormat.append(".");
			
			for (int i = 0; i < decimalPrecision; i++) {
				numberFormat.append("0");
			}
		}
		
		return numberFormat.toString();
	}
}

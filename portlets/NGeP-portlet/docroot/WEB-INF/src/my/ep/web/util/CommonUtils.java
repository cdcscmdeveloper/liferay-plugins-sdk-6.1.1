package my.ep.web.util;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;


public class CommonUtils {
	public String getDisplayDateFormat() {
		return CommonConstants.DEFAULT_DATE_FORMAT;
	}
	
	public String getDisplayTimeFormat() {
		return CommonConstants.DEFAULT_TIME_FORMAT;
	}
	
	public String getDisplayDateTimeFormat() {
		return CommonConstants.DEFAULT_DATETIME_FORMAT;
	}
	
	public String getDisplayNumberFormat(int numberFormatType) {
		String numberFormat = CommonConstants.DEFAULT_NUMBER_FORMAT;
		String decimalPrecision = null;
		
		switch (numberFormatType) {
			case CommonConstants.NUMBER_FORMAT_TYPE_INTEGER :
			case CommonConstants.NUMBER_FORMAT_TYPE_WEIGHT :
			case CommonConstants.NUMBER_FORMAT_TYPE_LENGTH :
				decimalPrecision = "";
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_QTY : decimalPrecision = "000";
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_PRICE : decimalPrecision = "00";
				break;
			case CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE : decimalPrecision = "00";
				break;
		}
		
		if (StringUtils.isNotBlank(decimalPrecision)) {
			numberFormat += "." + decimalPrecision;
		}
		
		return numberFormat;
	}
}

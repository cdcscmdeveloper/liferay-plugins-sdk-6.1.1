/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import java.util.HashMap;
import java.util.Map;

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
	private static Map<String, PmUom> uomMap = new HashMap<String, PmUom>();
	
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
				PmUom pmUom = uomMap.get(uomCode);
				
				if (pmUom == null && uomCode!=null && uomCode.length()>0) {
					IUomSO uomSO;
					
					try {
						uomSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
						
						UomReqDTO reqDTO = new UomReqDTO();
						reqDTO.setUomCode(uomCode);
						
						// Get PmUom from db.
						UomResDTO resDTO = uomSO.retrieveUomByCode(reqDTO);
						
						pmUom = resDTO.getPmUom();
					    
					    // Put UOM into map.
						uomMap.put(uomCode, pmUom);
					} catch (Exception e) {
						e.printStackTrace();
						throw new EpException(e.getMessage());
					} finally {
						uomSO = null;
					}	
				}
				
				if (pmUom != null) {
					decimalPrecision = pmUom.getDecimalScale();
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
}

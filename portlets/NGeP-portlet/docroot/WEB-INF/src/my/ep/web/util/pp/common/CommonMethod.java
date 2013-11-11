/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.util.pp.common;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.apache.log4j.Logger;

public class CommonMethod {
	private static Logger log = Logger.getLogger(CommonMethod.class);
	
	public static PmStatus currentStatus(Long statusId, String docType){
		//call api to get pm status
		return new PmStatus();
	}
	
	//temp
	public static final Map<Long, String> approverMap;
    static
    {
    	approverMap = new HashMap<Long, String>();
    	approverMap.put(1L, "lailah");
    	approverMap.put(2L, "khai");
    }

	public static BigDecimal addBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.add(amtTwo);
	}

	public static BigDecimal substractBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.subtract(amtTwo);
	}
	

	public static String generateHeader(String viewMode, String progType){
		log.info("generateHeader - Start");
		String pageTitle = "";
		String titleLabel = "";
		if(ProcurementPlanConst.MODE_NEW.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.new";
		}else if(ProcurementPlanConst.MODE_LIST.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.list";
		}else if(ProcurementPlanConst.MODE_APPROVAL.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.approval";
		}else if(ProcurementPlanConst.MODE_REVISION.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.revision";
		}else if(ProcurementPlanConst.MODE_VERIFICATION.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.verification";
		}else if(ProcurementPlanConst.MODE_MAINTAIN.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.maintain";
		}else if(ProcurementPlanConst.MODE_NEW_MTO.equals(viewMode)){
			pageTitle = "lbl.pp." + progType + ".title.mto.plan";
		}

		titleLabel = getMessageCommon(pageTitle);
		
		log.info("generateHeader - End");
		return titleLabel;
	}

	public static String getMessageCommon(String name){
		log.info("getMessageCommon");
		return ResourceBundleUtils.getMessage(name, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}

	
    
    public static int getYear(int index){
		log.info("getYear - Start");
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    int year = cal.get(Calendar.YEAR)+index;
	    log.info("getYear - End");
	    return year;
	}
	
    public static boolean isSelected(String value){
    	boolean result = false;
    	if(value!=null && value.trim().length()>0 && !"0".equals(value)){
    		result = true;
    	}
    	return result;
    }
    
    public static boolean isSelected(Long value){
    	boolean result = false;
    	if(value!=null && value>0L){
    		result = true;
    	}
    	return result;
    }
    
    public static Calendar convertDateToCalender(Date date){
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
    }
    
    /**
	 * retrieve financial year options for current year & current year +1
	 * @return financial year options
	 */
	public static List<SelectItem> financialYearOptions() {
		log.info("financialYearOptions - Start");
		int currentYear = CommonMethod.getYear(0);
		int nextYear = CommonMethod.getYear(1);
		
		List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
		financialYearOpt.add(new SelectItem(currentYear, Integer.toString(currentYear)));
		financialYearOpt.add(new SelectItem(nextYear, Integer.toString(nextYear)));
		log.info("financialYearOptions - End");
		return financialYearOpt;
	} 
	/**
	 * retrieve list of short month e.g Jul
	 * @return
	 */
	public static List<SelectItem> calendarMonths(){
		List<SelectItem> months = new ArrayList<SelectItem>();
		String[] shortMonths = new DateFormatSymbols().getShortMonths();
		Integer i = 0;
		for(String month:shortMonths){
			months.add(new SelectItem(i, month));
			i++;
		}
		return months;
	}
	
//	public static List<String> selectedCheckBoxValue(ValueChangeEvent event){
//		List<String> itemList = new ArrayList<String>();
//		if(event!=null){
//			Object[] objArr = (Object[])event.getNewValue();
//		 for (int x = 0; x < objArr.length; x++) { 
//			 Object obj = objArr[x]; 
//		 }
//		}
//        return itemList;
//	}
}

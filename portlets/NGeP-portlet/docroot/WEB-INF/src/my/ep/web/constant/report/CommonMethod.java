/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.constant.report;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

public class CommonMethod {
	private static Logger log = Logger.getLogger(CommonMethod.class);

	
	/**
	 * retrieve org profile by orgProfileId
	 * @param orgProfileId
	 * @returnPmOrgProfile
	 */
	public static PmOrgProfile retrieveOrgProfile(Long orgProfileId){
		log.info("retrieveOrgProfile - Start");
		PmOrgProfile profile =  new PmOrgProfile();
		try{
			if(orgProfileId!=null){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileId(orgProfileId);
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
//				if(resDTO.isSuccessful()){
					profile = resDTO.getPmOrgProfile();
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("retrieveOrgProfile - End");
		return profile;
	}
	
	/**
	 * retrieve org profile by login user and type
	 * @param userId
	 * @param type
	 * @return org profile
	 */
	public static List <PmOrgProfile> getOrgProfiles(Long userId, String orgType){
		log.info("getOrgProfiles - Start");
		log.info("userId " + userId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(userId)){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				if(CommonMethod.isSelected(orgType)){
					reqDTO.setOrgType(orgType);
				}
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getOrgProfileList();
					System.out.println("size.. " + profileList.size());
		
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("getOrgProfiles - End");
		return profileList;
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
		String[] shortMonths = Arrays.copyOf(DateFormatSymbols.getInstance().getShortMonths(), 12);
		Calendar cal = Calendar.getInstance();
		Integer i = 0;
		for(String month:shortMonths){
			months.add(new SelectItem(i, month));
			i++;
		}
		return months;
	}
	
	

	public Integer getMonthInt(Date date) {
		if(date!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.MONTH);
		}else{
			return null;
		}
	}
	
	public String getMonthString(Date date) {
		if(date!=null){
			SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy"); 
			return formatter.format(date);
		}else{
			return null;
		}
	}
	


	public static Date convertIntMonthToDate(Integer planStartMonth) {
		if(planStartMonth!=null && planStartMonth<12){
			Calendar cal = Calendar.getInstance();
			cal.set(cal.get(Calendar.YEAR), (planStartMonth), cal.get(Calendar.DATE));
			return cal.getTime();
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * generate plan number 
	 * @param planType
	 * @param issueOrgProf
	 * @return String
	 */
//	public static String generatePlanNumber(boolean isPlanned, String planType, PmOrgProfile issueOrgProf){
//		StringBuilder sb = new StringBuilder();
//		if(planType!=null && issueOrgProf!=null){
//		
//			//plan prefix
//			if(isPlanned){
//				if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
//					sb.append("pe");
//				}else{
//					sb.append("pp");
//				}
//			}else{
//				if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
//					sb.append("ue");
//				}else{
//					sb.append("up");
//				}
//			}
//			
//			sb.append(issueOrgProf.getParentOrgProfile().getOrgCode()); //4  last digits
//			sb.append(issueOrgProf.getOrgCode()); // 6 digits
//			//2 last digits year
//			Calendar cal = Calendar.getInstance();
//			String.valueOf(cal.get(Calendar.YEAR)).substring(2);
//			
//		}
//
//		
//		System.out.println("plan number: " + sb.toString());
//		return sb.toString();
//	}
//	
	

	/**
	 * auto generate doc number 
	 * @param isPlan
	 * @param isPlanned
	 * @param planType
	 * @param issueOrgProf
	 * @return String
	 */
	public static String generateDocNumber(Boolean isPlan, Boolean isPlanned, String planType, PmOrgProfile issueOrgProf){
		StringBuilder sb = new StringBuilder();
		if(isPlan){
			//plan prefix
			if(isPlanned){
				if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
					sb.append("pe");
				}else{
					sb.append("pp");
				}
			}else{
				if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
					sb.append("ue");
				}else{
					sb.append("up");
				}
			}
		}else{
			sb.append("ba");
		}
		
		Calendar cal = Calendar.getInstance();
		sb.append(cal.get(Calendar.YEAR));
		sb.append(cal.get(Calendar.MONTH));
		sb.append(cal.get(Calendar.DATE));
		sb.append(cal.get(Calendar.HOUR));
		sb.append(cal.get(Calendar.MINUTE));
		sb.append(cal.get(Calendar.SECOND));
		sb.append(cal.get(Calendar.MILLISECOND));
		System.out.println("doc number: " + sb.toString());
		return sb.toString();
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

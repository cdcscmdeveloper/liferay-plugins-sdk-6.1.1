/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.util.pp.common;

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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.procurementcattype.IProcurementCatTypeSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pp.ProcurementPlanConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.procurementcattype.ProcurementCatTypeReqDTO;
import my.ep.dto.pm.common.procurementcattype.ProcurementCatTypeResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
/**
 * Common Methods for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 27/10/2013
 *
 */
public class PpCommonUtil {
	private static Logger log = Logger.getLogger(PpCommonUtil.class);
	
	/**
	 * Retrieve plan by plan id
	 * @param planId
	 * @return PpPlan
	 */
	public static PpPlan retrievePlanById(Long planId){
		log.info("retrievePlanById - Start");
		PpPlan ppPlan = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setPlanId(planId);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrievePlanDetById(reqDTO);
			if (resDTO.isSuccessful()) {
				ppPlan = resDTO.getPlan();
			}
		}catch(Exception e){
			throw new EpException(e.toString()); //TODO
		}finally{
			reqSO = null;
		}
		log.info("retrievePlanById - End");
		return ppPlan;
	}

	/**
	 * retriever user org by user id
	 * @param userId
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrieveUserOrgListByUserId(Long userId, boolean isDetails){
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		IUserSO userSO  = null;
		try{
			if(userId!=null){
				UserReqDTO reqDTO = new UserReqDTO();
				UserResDTO resDTO = new UserResDTO();
				reqDTO.setUserId(userId);
				
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
				
				if(resDTO.isSuccessful() && resDTO.getUserOrgList()!=null){
					pmOrgProfile = resDTO.getUserOrgList().get(0).getPmOrgProfile();
					if(isDetails){
						pmOrgProfile = retrievePmOrgProfileDtl(pmOrgProfile.getOrgProfileId()); // retrieve the full details
					}
					
				}
			}

		}finally{
			userSO = null;
		}
		return pmOrgProfile;
	}
	
	
	/**
	 * retrieve user by role and org id
	 * @param orgProfileId
	 * @param roleCode
	 * @return @return List<PmUser>
	 */
	public static List<PmUser> retrieveUserListByRole(Long orgProfileId, String roleCode, List<PmUser> excludedUsers){
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		List<Long> userIds = null;
		IUserSO userSO  = null;
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			reqDTO.setOrgProfileId(orgProfileId);
			reqDTO.setRoleCode(roleCode);
			
			if(excludedUsers!=null && !excludedUsers.isEmpty()){
				userIds = new ArrayList<Long>();
				for(PmUser pmUser:excludedUsers){
					userIds.add(pmUser.getUserId());
				}
				reqDTO.setUserIds(userIds);
			}
			
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.retrieveUserListByRole(reqDTO);
			
			if(resDTO.isSuccessful() && resDTO.getUserList()!=null){
				pmUsers = resDTO.getUserList();
			}
			

		}finally{
			userSO = null;
		}
		return pmUsers;
	}
	
	

	/**
	 * retrieve org profile dtl
	 * @param id
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrievePmOrgProfileDtl(Long id){
		
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		IOrgProfileSO orgProfileSO   = null;
		try{
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = null;
			reqDTO.setOrgProfileId(id);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			resDTO = orgProfileSO.retrieveOrgProfileDtlByProfileId(reqDTO);
			if(resDTO.isSuccessful()){
				pmOrgProfile = resDTO.getPmOrgProfile();
			}
		}finally{
			orgProfileSO = null;
		}	
			
		return pmOrgProfile;
	}
	
	
	/**
	 * retrieve plan year options for current year & current year +1
	 * @return plan year options
	 */
	public static SelectItem[] getPlanYearOptions() {
		log.info("getPlanYearOptions - Start");
		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Integer nextYear = currentYear + 1;
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(currentYear.longValue(), currentYear.toString());
		items[2] = new SelectItem(nextYear.longValue(), nextYear.toString());
		log.info("getPlanYearOptions - End");
		return items;
	} 
		
	

	public static List<PmProcurementCatType> retrieveProcurementCatTypeList(String langugageCode){
		log.info("retrieveProcurementCatTypeList");
		String locale = "";
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		if(langugageCode==null){
			locale =  new FacesScopes().getUserLocale().getLanguage();
		}else{
			locale = langugageCode;
		}
		
		IProcurementCatTypeSO reqSO  = null;
		try{
			ProcurementCatTypeReqDTO reqDTO = new ProcurementCatTypeReqDTO();
			ProcurementCatTypeResDTO resDTO = new ProcurementCatTypeResDTO();
			reqDTO.setLanguageCode(locale);
			
			reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProcurementCatTypeSO.class);
			resDTO = reqSO.retrieveDistinctProcurementCatTypeList(reqDTO);
			
			if(resDTO.isSuccessful() && resDTO.getProcurementCatTypeList()!=null){
				procurementCatTypeList = resDTO.getProcurementCatTypeList();
			}
		}finally{
			reqSO = null;
		}
		return procurementCatTypeList;
	}
	
	public static List<PmProcurementCatType> retrieveProcurementTypeListByCatId(String langugageCode, Long procurementCatId){
		log.info("retrieveProcurementTypeListByCatId");
		String locale = "";
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		if(procurementCatId==null){
			throw new EpException("procurementCatId is required."); //TODO
		}
		if(langugageCode==null){
			locale =  new FacesScopes().getUserLocale().getLanguage();
		}else{
			locale = langugageCode;
		}
		
		IProcurementCatTypeSO reqSO  = null;
		try{
			ProcurementCatTypeReqDTO reqDTO = new ProcurementCatTypeReqDTO();
			ProcurementCatTypeResDTO resDTO = new ProcurementCatTypeResDTO();
			reqDTO.setLanguageCode(locale);
			reqDTO.setProcurementCatId(procurementCatId);
			reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProcurementCatTypeSO.class);
			resDTO = reqSO.retrieveDistinctProcurementTypeListByProcurementCat(reqDTO);
			
			if(resDTO.isSuccessful() && resDTO.getProcurementCatTypeList()!=null){
				procurementCatTypeList = resDTO.getProcurementCatTypeList();
			}
		}finally{
			reqSO = null;
		}
		return procurementCatTypeList;
	}
	
	
	
	
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
	

	
    
    public static int getYear(int index){
		log.info("getYear - Start");
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    int year = cal.get(Calendar.YEAR)+index;
	    log.info("getYear - End");
	    return year;
	}
    
    public static Calendar convertDateToCalender(Date date){
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
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
	
	/**
	 * return default workflow status
	 * @param docType
	 * @return PmStatus
	 */
	public static PmStatus getDefaultStatus(String docType){
		log.info("getDefaultStatus");
		PmStatus pmStatus = new PmStatus();
		if(ParameterConst.DOCTYPE_PLAN_PROCUREMENT.equals(docType)){
			pmStatus.setStatusId(ProcurementPlanConst.PPP_PENDING_SUBMISSION);
		}else if(ParameterConst.DOCTYPE_PLAN_EXPENDITURE.equals(docType)){
			pmStatus.setStatusId(ProcurementPlanConst.PPE_PENDING_SUBMISSION);
		}
		return pmStatus;
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
	
	public static String getMessageCommon(String name){
		log.info("getMessageCommon");
		return ResourceBundleUtils.getMessage(name, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}

	public static String getDocTitle(PpPlan plan){
		log.info("getDocTitle");
		StringBuilder sb = new StringBuilder();
		sb.append(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode()) 
				? getMessageCommon("lbl.pp.procurement.plan")
				: getMessageCommon("lbl.pp.expenditure.plan"));
		sb.append(" ");
		sb.append(getYear(0));

		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode())){
			sb.append(" ");
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(plan.getPlanType().getParameterCode())){
			sb.append(" ");
			sb.append(ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.VOT_TYPE, plan.getVotFundType().getParameterCode()));
		}
	    return sb.toString();
	}
    
	
	
	
}

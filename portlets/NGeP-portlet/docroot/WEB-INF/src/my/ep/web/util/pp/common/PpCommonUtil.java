/*
 * Copyright 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.procurementcattype.IProcurementCatTypeSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
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
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpWorkflowStatus;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
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
	 * get record status options 
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getStatusOptionsDefault() {
		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE, ResourceBundleUtils.getMessage(
				"lbl.pm.active", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_DELETED, ResourceBundleUtils.getMessage(
				"lbl.pm.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
	    return items;
	}
	
	
	/**
	 * Retrieve ppPlan by searchbean
	 * @param searchBean
	 * @return List<PpPlan> 
	 */
	public static List<PpPlan> retrievePlanBySearchBean(ISearchBean<PpPlan> searchBean){
		log.info("retrievePlanBySearchBean - Start");
		List<PpPlan> ppPlans = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setSearchBean(searchBean);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrievePlanBySearchBean(reqDTO);
			if (resDTO.isSuccessful()) {
				ppPlans = resDTO.getPlanList();
			}

		}finally{
			reqSO = null;
		}
		log.info("retrievePlanBySearchBean - End");
		return ppPlans;
	}

	
	
	/**
	 * Retrieve remark list by docId and docType
	 * @param docId
	 * @param docType
	 * @return List<PpRemark>
	 */
	public static List<PpRemark> retrieveRemark(Long docId, String docType){
		log.info("retrieveRemark - Start");
		List<PpRemark> remarks = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setDocId(docId);
			reqDTO.setDocType(docType);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrieveRemark(reqDTO);
			if (resDTO.isSuccessful()) {
				remarks = resDTO.getRemarkList();
			}
		}finally{
			reqSO = null;
		}
		log.info("retrieveRemark - End");
		return remarks;
	}
	/**
	 * Retrieve justification list by docId and docType
	 * @param docId
	 * @param docType
	 * @return List<PpRemark>
	 */
	public static List<PpRemark> retrieveJustification(Long docId, String docType){
		log.info("retrieveJustification - Start");
		List<PpRemark> remarks = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setDocId(docId);
			reqDTO.setDocType(docType);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrieveJustification(reqDTO);
			if (resDTO.isSuccessful()) {
				remarks = resDTO.getRemarkList();
			}
		}finally{
			reqSO = null;
		}
		log.info("retrieveJustification - End");
		return remarks;
	}
	/**
	 * Retrieve reject reason list by docId and docType
	 * @param docId
	 * @param docType
	 * @return  List<PpRejectReason> 
	 */
	public static List<PpRejectReason> retrieveRejectReason(Long docId, String docType){
		log.info("retrieveRejectReason - Start");
		List<PpRejectReason> rejectReasons = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setDocId(docId);
			reqDTO.setDocType(docType);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrieveRejectReason(reqDTO);
			if (resDTO.isSuccessful()) {
				rejectReasons = resDTO.getRejectReasonList();
			}
		}finally{
			reqSO = null;
		}
		log.info("retrieveRejectReason - End");
		return rejectReasons;
	}

	
	/**
	 * Retrieve ppWorkflowStatus list by docId, docType and statusId
	 * @param docId
	 * @param docType
	 * @param statusId
	 * @return List<PpWorkflowStatus>
	 */
	public static List<PpWorkflowStatus> retrievePpApprovedStatus(Long docId, String docType, Long statusId){
		log.info("retrievePpApprovedStatus - Start");
		List<PpWorkflowStatus> ppWorkflowStatus = null;
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		
		try{
			reqDTO = new ProcurementPlanReqDTO();
			resDTO = new ProcurementPlanResDTO();
			reqDTO.setDocId(docId);
			reqDTO.setDocType(docType);
			reqDTO.setStatusId(statusId);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
			resDTO = reqSO.retrieveStatusList(reqDTO);
			if (resDTO.isSuccessful()) {
				ppWorkflowStatus = resDTO.getPpWorkflowStatus();
			}
		}finally{
			reqSO = null;
		}
		log.info("retrievePpApprovedStatus - End");
		return ppWorkflowStatus;
	}
	
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
		}finally{
			reqSO = null;
		}
		log.info("retrievePlanById - End");
		return ppPlan;
	}
	/**
	 * retriever user by loginId or userId
	 * @param loginId
	 * @param userId
	 * @return pmUser
	 */
	public static PmUser retrieveUserByLoginIdOrUserId(String loginId, Long userId){
		log.info("retrieveUserByLoginIdOrUserId - Start");
		PmUser pmUser = new PmUser();
		IUserSO userSO  = null;
		try{
			if(loginId!=null || userId!=null){
				UserReqDTO reqDTO = new UserReqDTO();
				UserResDTO resDTO = new UserResDTO();
				
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				if(loginId!=null){
					reqDTO.setLoginId(loginId);
					resDTO = userSO.retrieveUserByLoginId(reqDTO);
				}else{
					reqDTO.setUserId(userId);
					resDTO = userSO.retrieveUserDtlById(reqDTO);
				}
				
				
				
				if(resDTO.isSuccessful()){
					pmUser = resDTO.getPmUser();
				}
			}
	
		}finally{
			userSO = null;
		}
		log.info("retrieveUserByLoginIdOrUserId - End");
		return pmUser;
	}
	
	/**
	 * retriever user org by user id
	 * @param userId
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrieveUserOrgListByUserId(Long userId, boolean isDetails){
		log.info("retrieveUserOrgListByUserId - Start");
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
		log.info("retrieveUserOrgListByUserId - End");
		return pmOrgProfile;
	}
	
	
	/**
	 * retrieve user by role and org id
	 * @param orgProfileId
	 * @param roleCode
	 * @return @return List<PmUser>
	 */
	public static List<PmUser> retrieveUserListByRole(Long ptjOrgProfileId, String roleCode, List<PmUser> excludedUsers, boolean isWithGroupUser){
		log.info("retrieveUserListByRole - Start");
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		List<Long> userIds = null;
		IUserSO userSO  = null;
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			reqDTO.setOrgProfileId(ptjOrgProfileId);
			reqDTO.setRoleCode(roleCode);
//			reqDTO.setWithProfileGroup(isWithGroupUser);
			
			if(excludedUsers!=null && !excludedUsers.isEmpty()){
				userIds = new ArrayList<Long>();
				for(PmUser pmUser:excludedUsers){
					userIds.add(pmUser.getUserId());
				}
				reqDTO.setUserIds(userIds);
			}
			
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.retrieveUserListFromGroupOrgType(reqDTO);
			
			if(resDTO.isSuccessful() && resDTO.getUserList()!=null){
				pmUsers = resDTO.getUserList();
			}
			

		}finally{
			userSO = null;
		}
		log.info("retrieveUserListByRole - End");
		return pmUsers;
	}
	
	

	/**
	 * retrieve org profile dtl
	 * @param id
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrievePmOrgProfileDtl(Long id){
		log.info("retrievePmOrgProfileDtl - Start");
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
		log.info("retrievePmOrgProfileDtl - End");
		return pmOrgProfile;
	}
	
	public static List<Long> getExistingPlanYear(Long userId, Long orgProfileId, Long planTypeId) {
		log.info("getExistingPlanYear");
		
		ProcurementPlanReqDTO reqDTO = null;
		IProcurementPlanSO reqSO = null;
		ProcurementPlanResDTO resDTO = null;
		List <Long> planYears = null;
		try{
			//get from DB
			if(userId!=null){
				reqDTO = new ProcurementPlanReqDTO();
				resDTO = new ProcurementPlanResDTO();
				reqDTO.setOrgProfileId(orgProfileId);
				reqDTO.setPlanTypeId(planTypeId);
				reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
				resDTO = reqSO.retrieveExistingPlanYears(reqDTO);
				if (resDTO.isSuccessful() && resDTO.getPlanYears()!=null) {
					planYears = resDTO.getPlanYears();
					
				}
			}
		}finally{
			reqSO = null;
		}
		return planYears;
	}
	
//	/**
//	 * retrieve existing planned plan years
//	 * @param userId
//	 * @param orgProfileId
//	 * @return List<Long> 
//	 */
//	private static List<Long> getEpExistingPlanYear(Long userId, Long orgProfileId) {
//		log.info("getEpExistingPlanYear");
//		
//		ProcurementPlanReqDTO reqDTO = null;
//		IProcurementPlanSO reqSO = null;
//		ProcurementPlanResDTO resDTO = null;
//		List <Long> planYears = null;
//		try{
//			//get from DB
//			if(userId!=null){
//				reqDTO = new ProcurementPlanReqDTO();
//				resDTO = new ProcurementPlanResDTO();
//				reqDTO.setEP(true);
//				reqDTO.setOrgProfileId(orgProfileId);
//				reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
//				resDTO = reqSO.retrieveExistingPlanYears(reqDTO);
//				if (resDTO.isSuccessful() && resDTO.getPlanYears()!=null) {
//					planYears = resDTO.getPlanYears();					
//				}
//			}
//		}finally{
//			reqSO = null;
//		}
//		return planYears;
//	}
	

//	/**
//	 * retrieve existing planned vot type ids
//	 * @param orgId
//	 * @param planYear
//	 * @return List<Long> 
//	 */
//	public static List<Long> existingPlannedVotTypes(Long orgId, Long planYear){
//		log.info("existingPlannedVotTypes");
//		ProcurementPlanReqDTO reqDTO = null;
//		IProcurementPlanSO reqSO = null;
//		ProcurementPlanResDTO resDTO = null;
//		List <Long> existingVotTypeIds = null;
//		
//		try{
//			reqDTO = new ProcurementPlanReqDTO();
//			resDTO = new ProcurementPlanResDTO();
//			reqDTO.setPlanYear(planYear);
//			reqDTO.setOrgProfileId(orgId);
//			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);	
//			resDTO = reqSO.retrieveExistingVotTypesIds(reqDTO);
//			if (resDTO.isSuccessful()) {
//				existingVotTypeIds = resDTO.getVotTypeIds();
//			}
//
//		}finally{
//			reqSO = null;
//		}
//
//		
//		return existingVotTypeIds;
//	}
//	
	/**
	 * retrieve plan year options for current year & current year +1
	 * @return plan year options
	 */
	public static SelectItem[] getPlanYearOptions(Long userId, Long orgProfileId, Long planTypeId) {
		log.info("getPlanYearOptions - Start");
		SelectItem[] items = new SelectItem[1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		
		
		List<Long> planYears = getExistingPlanYear(userId, orgProfileId, planTypeId);
		List<Long> years = new ArrayList<Long>();
		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Integer nextYear = currentYear + 1;
		years.add(currentYear.longValue());
		years.add(nextYear.longValue());
		if(planYears!=null && !planYears.isEmpty()){
			if(planYears.contains(currentYear.longValue())){
				years.remove(currentYear.longValue());
			}
			if(planYears.contains(nextYear.longValue())){
				years.remove(nextYear.longValue());
				
			}
		}
		
		if(years!=null && !years.isEmpty()){
			items = new SelectItem[years.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			int i = 1;
			for (Long year : years) {
				items[i++] = new SelectItem(year, year.toString());
			}
		}
		log.info("getPlanYearOptions - End");
		return items;
	} 
		
//	
//	/**
//	 * retrieve plan year options for current year & current year +1
//	 * @return plan year options
//	 */
//	public static SelectItem[] getEpPlanYearOptions(Long userId, Long orgProfileId) {
//		log.info("getEpPlanYearOptions - Start");
//		SelectItem[] items = new SelectItem[1];
//		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
//		
//		
//		List<Long> planYears = getEpExistingPlanYear(userId, orgProfileId);
//		List<Long> years = new ArrayList<Long>();
//		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
//		Integer nextYear = currentYear + 1;
//		years.add(currentYear.longValue());
//		years.add(nextYear.longValue());
//		if(planYears!=null && !planYears.isEmpty()){
//			System.out.println(planYears.size());
//			if(planYears.contains(currentYear.longValue())){
//				years.remove(currentYear.longValue());
//			}
//			if(planYears.contains(nextYear.longValue())){
//				years.remove(nextYear.longValue());
//				
//			}
//		}
//		
//		if(years!=null && !years.isEmpty()){
//			items = new SelectItem[years.size()+1];
//			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
//			int i = 1;
//			for (Long year : years) {
//				items[i++] = new SelectItem(year, year.toString());
//			}
//		}
//		
//	
//		
//		
//		
//		log.info("getEpPlanYearOptions - End");
//		return items;
//	} 
//	

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
			throw new EpException(MessageConst.ERROR_730026);
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
//		sb.append(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode()) 
//				? getMessageCommon("lbl.pp.procurement.plan")
//				: getMessageCommon("lbl.pp.expenditure.plan"));
//		sb.append(" ");
		sb.append(plan.getOrgProfile().getPmOrgValidity().getOrgCode());
		sb.append(" ");
		sb.append(plan.getOrgProfile().getPmOrgValidity().getOrgName());
		sb.append(" ");
		sb.append(plan.getPlanYear());
		/*
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equalsIgnoreCase(plan.getPlanType().getParameterCode())){
			sb.append(" ");
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(plan.getPlanType().getParameterCode())){
			sb.append(" ");
			sb.append(ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.VOT_TYPE, plan.getVotFundType().getParameterCode()));
		}
		*/
		System.out.println(sb.toString());
	    return sb.toString();
	}
	
	public static List<PmOrgProfile> retrieveOrgProfileListFromGroupOrgType(Long orgProfileId,String parentOrgType, String groupOrgType){
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setParentOrgType(parentOrgType);
		reqDTO.setGroupOrgType(groupOrgType);
		OrgProfileResDTO resDTO = profileSO.retrieveOrgProfileListFromGroupOrgType(reqDTO);
		return resDTO.getOrgProfileList();
	}
	
}

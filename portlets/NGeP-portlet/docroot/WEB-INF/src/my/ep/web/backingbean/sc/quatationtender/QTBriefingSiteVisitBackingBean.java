/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.sc.quatationtender;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;//#28188
import java.util.LinkedList;

import javax.faces.application.FacesMessage;//#28188
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;//#28188
import javax.faces.context.FacesContext;//#28188
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;//#28188

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;//#28188
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTScAddressSO;//#28188
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;//#28188
import my.ep.dto.pm.common.state.StateResDTO;//#28188
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;//#28188
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;//#28188
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;//#28188
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * The Class QTBriefingSiteVisitBackingBean.
 *
 * @author Viswanath
 */
@ManagedBean(name = "qTBriefingSiteVisitBackingBean")
@RequestScoped
public class QTBriefingSiteVisitBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant NAV_TO_BSV_ATTENDANCE_PAGE. */
	private static final String NAV_TO_BSV_ATTENDANCE_PAGE = "/xhtml/sc/quatationtender/briefingsitevisit/briefingAttendance.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTBriefingSiteVisitBackingBean.class);

	/** The index. */
	private int index;

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt != null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		/*else{
		log.info(message);
		}*/
	}

	/**
	 * Instantiates a new QT briefing site visit backing bean.
	 */
	public QTBriefingSiteVisitBackingBean() {

	}

	/**
	 * Retrieve state list by country id
	 * 
	 */
	public void retrievePmStateList() {
		debugLogger("retrievePmStateList");
		ICountrySO countrySo = null;
		try{
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		if (null == this.getManagedBean().getStates()) {
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);

			List<PmState> stateList = PmCommonUtil
					.retrievePmStateList(countryResDTO.getPmCountry()
							.getCountryId());
			this.getManagedBean().setStates(stateList);
		}
		retrievePmDivisions();
		}finally{
			countrySo = null;	
		}
	}

	/**
	 * Retrieve district list by state id.
	 */
	public void retrievePmDistrictList() {
		debugLogger("retrievePmDistrictList");
		IDistrictSO reqSO = null;
		try{
		if (null != this.getManagedBean().getQtBriefing()) {
			this.getManagedBean().setDistricts(null);

			Long divisionId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDivision().getDivisionId();

			Long stateId = this.getManagedBean().getQtBriefing().getAddress()
					.getPmState().getStateId();
			// PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			if (!this.getManagedBean().isState() && divisionId == null) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setStateId(stateId);
				 reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByStateID(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());

			} else if (divisionId != null) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setDivisionId(divisionId);
				 reqSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = reqSO
						.retriveDistrictListByDivision(reqDTO);
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}

		}
		retrievePmCityList();
		}finally{
			reqSO = null;	
		}
	}

	/**
	 * Retrieve city list by district id.
	 */
	public void retrievePmCityList() {
		this.getManagedBean().setCitys(null);
		ICitySO reqSO =null;
		try{
		if (null != this.getManagedBean().getQtBriefing()) {
			Long districtId = this.getManagedBean().getQtBriefing()
					.getAddress().getPmDistrict().getDistrictId();

			if (null != districtId) {
				CityReqDTO reqDTO = new CityReqDTO();
				reqDTO.setDistrictId(districtId);
				 reqSO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
				this.getManagedBean().setCitys(resDTO.getCityList());
			}
		}
		}finally{
			reqSO =null;	
		}
	}

	/**
	 * Retrieve division list by state id (only applicable for sarawak).
	 */
	public void retrievePmDivisions() {
		debugLogger("retrievePmDistrictList");
		this.getManagedBean().setState(false);
		this.getManagedBean().setDivisionList(null);
		if (null != this.getManagedBean().getQtBriefing()) {
			/*
			 * this.getManagedBean().getQtBriefing().getAddress()
			 * .setPostcode(null);
			 */
			Long stateId = this.getManagedBean().getQtBriefing().getAddress()
					.getPmState().getStateId();
			this.getManagedBean().setDivisionList(null);
			this.getManagedBean().setDistrictList(null);
			this.getManagedBean().setCitys(null);
			if (stateId != null) {
				PmState pmState = PmCommonUtil
						.retrievePmStateByStateId(stateId);
				if (ParameterConst.SARAK_SK_CODE.equalsIgnoreCase(pmState
						.getStateCode())) {
					this.getManagedBean().setState(true);
					this.getManagedBean().setDivisionList(
							PmCommonUtil.retrievePmDivisionList(stateId));
				} else {
					PmDivision division = this.getManagedBean().getQtBriefing()
							.getAddress().getPmDivision();
					if (null != division) {
						this.getManagedBean().getQtBriefing().getAddress()
								.getPmDivision().setDivisionId(null);
					} else {
						this.getManagedBean().getQtBriefing().getAddress()
								.setPmDivision(new PmDivision());
					}

				}
				Long dbStateId = this.getManagedBean().getBsvDBStateId();
				if(dbStateId != null && !stateId.equals(dbStateId)){
					ScAddress address = this.getManagedBean().getQtBriefing().getAddress();
					if(address.getPmDistrict()!= null){
						address.setPmDistrict(new PmDistrict());
					}
					if(address.getPmCity()!= null){
						address.setPmCity(new PmCity());
					}
				}
			}
			

		}

		retrievePmDistrictList();
	}

	/**
	 * Process checked.
	 *
	 * @param e the e
	 */
	public void processChecked(AjaxBehaviorEvent e) {
		Boolean selectedValue = (Boolean) e.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setAllSiteVisitReq(true);
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	/**
	 * Add new bsv details to list.
	 *
	 * @param bsvType the bsv type
	 */
	public void addBsvDetails(String bsvType) {
		debugLogger("addBriefingDetails");
		this.getManagedBean().setRequired(Boolean.TRUE);
		this.getManagedBean().setBsvType(bsvType);
		ScQtBsvDtl bsvDtl = new ScQtBsvDtl();
		bsvDtl.setBsvType(bsvType);
		ScAddress address = new ScAddress();
		PmCity pmCity = new PmCity();
		address.setPmCity(pmCity);
		PmState pmState = new PmState();
		address.setPmState(pmState);
		address.setPmDivision(new PmDivision());
		address.setPmDistrict(new PmDistrict());
		bsvDtl.setAddress(address);
		bsvDtl.setBsvStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
		this.getManagedBean().setQtBriefing(bsvDtl);
		retrievePmStateList();
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Add details to briefing list.
	 */
	public void addToBriefingList(Long editQtBsvDtlId) {//#28188
		debugLogger("addToBriefingList");
		this.getManagedBean().setRequired(false);
		String bsvType = this.getManagedBean().getBsvType();
		BriefingZoneBean briefingBean = this.getManagedBean().getBsvDtlBean();
		ScQtBsvDtl bsvDetail = this.getManagedBean().getQtBriefing();
		boolean cloneFlag = false;//#28188
		
		//#28188 - Start
	    if (bsvDetail != null && bsvDetail.getAddress() != null) {
	        populateAddress(bsvDetail);
	    }
	    //#28188 - End
	    
		// Checking Max attendance value is greater than zero.
		if (bsvDetail.getMaxNoOfAttendance() < 1) {
			throw new EpException(MessageConst.ERROR_760401);
		}
		validatePostcode();
		
		// Checking for duplication.. if it is add flow..
		if (!bsvDetail.isSelected()) {
			if (null != briefingBean.getBriefingList()
					&& briefingBean.getBriefingList().contains(bsvDetail)) {
				throw new EpException(MessageConst.ERROR_760099);
			} else if (null != briefingBean.getSitevisitList()
					&& briefingBean.getSitevisitList().contains(bsvDetail)) {
				throw new EpException(MessageConst.ERROR_760099);
			}
			bsvDetail.setScQtBsv(briefingBean.getScQtBsv());
		}
		/*
		 * if (null != bsvDetail) { Date bsvDate = bsvDetail.getBsvDate(); if
		 * (null != bsvDate) { Date publishDate =
		 * this.getManagedBean().getQt().getPublishDate(); if (null !=
		 * publishDate) { if (bsvDate.compareTo(publishDate) < 0 ) { throw new
		 * EpException("Publish Date is more Than Briefing/Site Visit date."); }
		 * } } }
		 */
		if(CollectionUtils.isEmpty(briefingBean.getScQtBsv().getScQtBsvDtls())){//#28188
			if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
				List<ScQtBsvDtl> briefingDetails = briefingBean.getBriefingList();
				if (null == briefingDetails) {
					briefingDetails = new ArrayList<ScQtBsvDtl>();
				}
				if (!briefingDetails.contains(bsvDetail)) {
					briefingDetails.add(bsvDetail);
				}
				briefingBean.setBriefingList(briefingDetails);
			} else if (SourcingConstants.BSV_TYPE_SITEVISIT
					.equalsIgnoreCase(bsvType)) {
				List<ScQtBsvDtl> siteVisitDetails = briefingBean.getSitevisitList();
				if (null == siteVisitDetails) {
					siteVisitDetails = new ArrayList<ScQtBsvDtl>();
				}
				if (!siteVisitDetails.contains(bsvDetail)) {
					siteVisitDetails.add(bsvDetail);
				}
				if (siteVisitDetails.size() == 1) {
					briefingBean.getScQtBsv().setAllSiteVisitReq(true);
				}
				briefingBean.setSitevisitList(siteVisitDetails);
			}
		}else{
			//Start - #28188 Enhancement code
			if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {	
				List<ScQtBsvDtl> briefingDetails = briefingBean.getBriefingList();
				if (null == briefingDetails) {
					briefingDetails = new ArrayList<ScQtBsvDtl>();
				}
				if (!briefingDetails.contains(bsvDetail)) {
					briefingDetails.add(bsvDetail);
				}
				//Start - #28188
			    if (CollectionUtils.isEmpty(briefingBean.getBriefingList())) {
			        // First-time assignment
			        briefingBean.setBriefingList(briefingDetails);
			    } else {
			        // Always have a usable list
			        List<ScQtBsvDtl> currentList = briefingBean.getBriefingList();
			        if (currentList == null) currentList = new ArrayList<ScQtBsvDtl>();
			
			        
			        // Result list to replace the old one (prevents duplicates)
			        List<ScQtBsvDtl> resultList = new ArrayList<ScQtBsvDtl>();
			        
			        // All list to replace the old one (prevents duplicates)
			        List<ScQtBsvDtl> allBriefingList = new ArrayList<ScQtBsvDtl>();
			        
			    	
			    	boolean existFlag = checkBSVDetailExistAndPostpone(editQtBsvDtlId, currentList);
					
			        
			    	if(existFlag){
			        	for (ScQtBsvDtl currentBsvDtl : currentList) {
			    	        PmStatus status = currentBsvDtl.getBsvStatus();
			    	        Long statusId = (status != null ? status.getStatusId() : null);
			    	        
			    	        ScQtBsvDtl editBsvDtl = retrieveBSVDetail(editQtBsvDtlId);
							if (editBsvDtl != null
									&& editBsvDtl.getQtBsvDtlId().equals(
											currentBsvDtl.getQtBsvDtlId())
									&& bsvDetail.getBsvStatus().getStatusId()
											.equals(QTStatusConst.BSV_POSTPONE)) {
			    	        	ScQtBsvDtl clone = makeCloneWithStatus(currentBsvDtl, statusId);
		    	                currentBsvDtl.setRecordStatus(DbValueConst.FLAG_NO);
		    	                currentBsvDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		    	                resultList.add(clone);
		    	                resultList.add(currentBsvDtl);
		    	                cloneFlag = true;
					        }else{
					        	resultList.add(currentBsvDtl);
					        }
		    	        	
			    	    }
			        }
			        
			        if (CollectionUtils.isNotEmpty(resultList)) {
			            for (ScQtBsvDtl dtl : resultList) {
			                if (dtl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
			                	if(dtl.getQtBsvDtlId() != null && dtl.getQtBsvDtlId().equals(editQtBsvDtlId)){
			                        ScQtBsvDtl temp = getSafeTempDtl(dtl);
			                        populateAddress(temp);
			                        temp.getBsvStatus().setStatusId(QTStatusConst.BSV_POSTPONE);
			                        allBriefingList.add(temp);
			                	}else if(dtl.getBsvDate() == null){
			                		ScQtBsvDtl temp = getSafeTempDtl(dtl);
			                		
			                		//28188 -start-
			                		if(null==dtl.getParentPostponeId() && temp != null){
			                		dtl.setParentQtBsvDtlId(temp.getQtBsvDtlId());//Add for tagging parent_qt_bsv_dtl_id(Notification purpose)
			                		dtl.setParentPostponeId(temp.getQtBsvDtlId());//Add for tagging parentBsvId(QT Maintenance purpose)
			                		}
			                		//28188 -end
			                		
			                		dtl.getBsvStatus().setStatusId(QTStatusConst.BSV_PENDING);
			                        populateAddress(dtl);
			                        allBriefingList.add(dtl);
			                	}else{
			                		populateAddress(dtl);
			                		allBriefingList.add(dtl);
			                	}
			                } else {
			                	if(!dtl.getQtBsvDtlId().equals(editQtBsvDtlId)){
			                		ScAddress address = dtl.getAddress();
									if (address != null) {
										address.getPmCountry();
										address.getPmCity();
										address.getPmDistrict();
										address.getPmDivision();
										address.getPmState();
									}
			                		populateAddressNonEdit(address);
			                		allBriefingList.add(dtl);
			                	}else{
			                		populateAddress(dtl);
			                		allBriefingList.add(dtl);
			                	}
			                }
			            }
			        } else if (CollectionUtils.isNotEmpty(currentList)) {
		                for (ScQtBsvDtl dtl : currentList) {
		                    populateAddress(dtl);
		                    allBriefingList.add(dtl);
		                }
			        	
			        }
			        
					// 28188 -start-
					briefingBean.setBriefingList(groupAndSortBriefingSiteVisitDtls(allBriefingList));
					// 28188 -end-
			        
					/*
					 *28188 removed this code if site visit details changed and
					 * Briefing details: Try to change same time reset all
                     * Site visit details.
					 */
			    }
			} else if (SourcingConstants.BSV_TYPE_SITEVISIT.equalsIgnoreCase(bsvType)) {
				List<ScQtBsvDtl> siteVisitDetails = briefingBean.getSitevisitList();
				if (null == siteVisitDetails) {
					siteVisitDetails = new ArrayList<ScQtBsvDtl>();
				}
				if (!siteVisitDetails.contains(bsvDetail)) {
					siteVisitDetails.add(bsvDetail);
				}
				if (siteVisitDetails.size() == 1) {
					briefingBean.getScQtBsv().setAllSiteVisitReq(true);
				}
				
				//Start - #28188
			    if (CollectionUtils.isEmpty(briefingBean.getSitevisitList())) {
			        // First-time assignment
			        briefingBean.setSitevisitList(siteVisitDetails);
//			    	List<ScQtBsvDtl> finalList = groupAndSortSiteVisitList(siteVisitDetails);
//			    	briefingBean.setSitevisitList(finalList);
			    	
			    } else {
			        // Always have a usable list
			        List<ScQtBsvDtl> currentList = briefingBean.getSitevisitList();
			        if (currentList == null) currentList = new ArrayList<ScQtBsvDtl>();
			        
			        // Result list to replace the old one (prevents duplicates)
			        List<ScQtBsvDtl> resultList = new ArrayList<ScQtBsvDtl>();
			        
			        // All list to replace the old one (prevents duplicates)
			        List<ScQtBsvDtl> allSVList = new ArrayList<ScQtBsvDtl>();
			        
			
			    	boolean existFlag = checkBSVDetailExistAndPostpone(editQtBsvDtlId, currentList);
			
			        if(existFlag){
			        	for (ScQtBsvDtl currentBsvDtl : currentList) {
			    	        PmStatus status = currentBsvDtl.getBsvStatus();
			    	        Long statusId = (status != null ? status.getStatusId() : null);
			    	        
			    	        ScQtBsvDtl editBsvDtl = retrieveBSVDetail(editQtBsvDtlId);
							if (editBsvDtl != null
									&& editBsvDtl.getQtBsvDtlId().equals(
											currentBsvDtl.getQtBsvDtlId())
									&& bsvDetail.getBsvStatus().getStatusId()
											.equals(QTStatusConst.BSV_POSTPONE)) {
			    	        	ScQtBsvDtl clone = makeCloneWithStatus(currentBsvDtl, statusId);
		    	                currentBsvDtl.setRecordStatus(DbValueConst.FLAG_NO);
		    	                currentBsvDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		    	                resultList.add(clone);
		    	                resultList.add(currentBsvDtl);
		    	                cloneFlag = true;
					        }else{
					        	resultList.add(currentBsvDtl);
					        }
		    	        	
			    	    }
			        }
			        
			        if (CollectionUtils.isNotEmpty(resultList)) {
			            for (ScQtBsvDtl dtl : resultList) {
			                if (dtl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
			                	if(dtl.getQtBsvDtlId() != null && dtl.getQtBsvDtlId().equals(editQtBsvDtlId)){
			                        ScQtBsvDtl temp = getSafeTempDtl(dtl);
			                        populateAddress(temp);
			                        temp.getBsvStatus().setStatusId(QTStatusConst.BSV_POSTPONE);
			                        allSVList.add(temp);
			                	}else if(dtl.getBsvDate() == null){
			                		ScQtBsvDtl temp = getSafeTempDtl(dtl);
			                		
			                		//28188 -start-
			                		if(null==dtl.getParentPostponeId()&& temp != null){
			                		dtl.setParentQtBsvDtlId(temp.getQtBsvDtlId());//Add for tagging parent_qt_bsv_dtl_id(Notification purpose) 
			                	    dtl.setParentPostponeId(temp.getQtBsvDtlId());//Add for tagging parentBsvId(QT Maintenance purpose) 
			                		}
			                		//28188 -end-
			                		
			                		dtl.getBsvStatus().setStatusId(QTStatusConst.BSV_PENDING);
			                        populateAddress(dtl);
			                        allSVList.add(dtl);
			                	}else{
			                		populateAddress(dtl);
			                		allSVList.add(dtl);
			                	}
			                } else {
			                	if(!dtl.getQtBsvDtlId().equals(editQtBsvDtlId)){
			                		ScAddress address = dtl.getAddress();
									if (address != null) {
										address.getPmCountry();
										address.getPmCity();
										address.getPmDistrict();
										address.getPmDivision();
										address.getPmState();
									}
			                		populateAddressNonEdit(address);
				                    allSVList.add(dtl);
			                	}else{
			                		populateAddress(dtl);
				                    allSVList.add(dtl);
			                	}
			                }
			            }
			        } else if (CollectionUtils.isNotEmpty(currentList)) {
		                for (ScQtBsvDtl dtl : currentList) {
		                    populateAddress(dtl);
		                    allSVList.add(dtl);
		                }
			        	
			        }
			        
					// 28188 -start-
					briefingBean.setSitevisitList(groupAndSortBriefingSiteVisitDtls(allSVList));

					// 28188 -end-
			        
			        /*
					 *28188 removed this code if Briefing details  changed and
					 * site visit details: Try to change same time reset all
                     * Briefing details .
					 */
			    }
			}
		}	
		//End - #28188 Enhancement code
		
		if (this.getManagedBean().getQt().isZonal()) {
			List<BriefingZoneBean> zoneBeans = this.getManagedBean()
					.getBriefingZoneBean();
			for (int i = 0; i < zoneBeans.size(); i++) {
				if (zoneBeans
						.get(i)
						.getTplSpecZoneDtl()
						.getZoneName()
						.equalsIgnoreCase(
								briefingBean.getTplSpecZoneDtl().getZoneName())) {
					zoneBeans.remove(i);
					zoneBeans.add(i, briefingBean);
					break;
				}
			}

		}
		this.getManagedBean().setBsvDtlBean(briefingBean);
		this.getManagedBean().setQtBriefing(null);
		// facesmgs error hide..
		
		this.getManagedBean().setRequired(Boolean.FALSE);
		
		ScQtBsvDtl tempScQtBsvDtl = this.getManagedBean().getTempScQtBsvDtl();
		if (null != tempScQtBsvDtl) {
			updateQtAmmendMentDtls(bsvType, tempScQtBsvDtl,bsvDetail);
		} else {
			updateLatestBsvDate();
			if(this.getManagedBean().getAmendMentBean() == null){
				this.getManagedBean().setAmendMentBean(new QtAmendMentBean());
			}
			
		}
		
		RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
	}

	/**
	 * Delete bsv details from list.
	 *
	 * @param bsvType the bsv type
	 */
	public void deleteBsvDetails(String bsvType) {
		debugLogger("deleteBriefingDetails");
		BriefingZoneBean briefingDtlBean = this.getManagedBean()
				.getBsvDtlBean();
		if (SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> briefingDetails = briefingDtlBean
					.getBriefingList();
			List<ScQtBsvDtl> selectedBsvDetails = briefingDtlBean
					.getSelectedBriefingDtls();
			if (null == selectedBsvDetails || selectedBsvDetails.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760141);
			}
			briefingDetails.removeAll(selectedBsvDetails);
			briefingDtlBean.setSelectedBriefingDtls(null);
		} else if (SourcingConstants.BSV_TYPE_SITEVISIT
				.equalsIgnoreCase(bsvType)) {
			List<ScQtBsvDtl> siteVisitDetails = briefingDtlBean
					.getSitevisitList();
			List<ScQtBsvDtl> selectedBsvDetails = briefingDtlBean
					.getSelectedSiteVisitDtls();
			if (null == selectedBsvDetails || selectedBsvDetails.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760141);
			}
			siteVisitDetails.removeAll(selectedBsvDetails);
			briefingDtlBean.setSelectedSiteVisitDtls(null);
		}
		this.getManagedBean().setBsvDtlBean(briefingDtlBean);
		
		// Set QtAmendMentBean to invoke BPM from Maintenance.
		if(this.getManagedBean().getAmendMentBean() == null){
			this.getManagedBean().setAmendMentBean(new QtAmendMentBean());
		}
		updateLatestBsvDate();
	}

	/**
	 * Goto attandance dtl.
	 *
	 * @param scQtBriefing the sc qt briefing
	 * @return the string
	 */
	public String gotoAttandanceDtl(ScQtBsvDtl scQtBriefing) {
		
		BSVAttendanceBackingBean attendanceBackingBean= new BSVAttendanceBackingBean();
		attendanceBackingBean.getregistrationDetails(scQtBriefing, true);
		attendanceBackingBean.getManagedBean().setScQt(this.getManagedBean().getQt());
		attendanceBackingBean.getManagedBean().setToBeDeletedRegistartions(null);//#28188
		new QTDetailBackingBean().retrieveApprovalTabDtl();
		List<TabBean> tabList = this.getManagedBean().getTabList();
		for (TabBean tabBean : tabList) {
			if (ParameterConst.APPROVAL_CATEGORY_PTJ.equals(tabBean.getTabType())) {
				ScQt scQt = this.getManagedBean().getQt();
				ScQtApproval qtApproval = tabBean.getScQtApproval();
				List<ScQtApprovalRemark> scQtApprovalRemarks = initializeRemarks(
						qtApproval, scQt.getCurrentWorkFlowStatus(),
						null,
						ScQtApprovalRemark.class);
				attendanceBackingBean.getManagedBean().setScQtApproval(qtApproval);
				attendanceBackingBean.getManagedBean().setScQtApprovalRemarks(scQtApprovalRemarks);
				attendanceBackingBean.getManagedBean().setScQtApprovalAttachments(new ArrayList<ScQtApprovalAttachment>());
				
				// 28188 -start-
				List<ScQtBsvRegistration> bsvRegistartion = attendanceBackingBean
						.getManagedBean().getBriefingRegistrationList();
				Long qtId = scQt.getQtId();
				Long qtBsvdtlId = scQtBriefing.getQtBsvDtlId();
				checkBsvRegistartionInprogress(bsvRegistartion, qtId,
						qtBsvdtlId);
				// 28188 -end-
				
				break;
			}
		}
		
		attendanceBackingBean.getManagedBean().setMaintanceFlow(true);
		return NAV_TO_BSV_ATTENDANCE_PAGE + "?faces-redirect=true";
	}

	/**
	 * On row select.
	 *
	 * @param event the event
	 */
	public void onRowSelect(SelectEvent event) {
		debugLogger("onRowSelect");
		this.getManagedBean().setBriefingAttendanceList(null);
		ScQtBsvRegistration briefingRegistration = (ScQtBsvRegistration) event
				.getObject();

		getAttandanceDtlsBySelectedRegistartion(briefingRegistration);
	}
	
	/**
	 * Gets the attandance dtls by selected registartion.
	 *
	 * @param briefingRegistration the briefing registration
	 * @return the attandance dtls by selected registartion
	 */
	private void getAttandanceDtlsBySelectedRegistartion(
			ScQtBsvRegistration briefingRegistration) {
		debugLogger("getAttandanceDtlsBySelectedRegistartion");
		this.getManagedBean()
				.setSelectedbriefRegistration(briefingRegistration);

		List<ScQtBsvAttendance> attanedList = briefingRegistration
				.getBsvAttendance();
		IQTBriefingSO briefingSO =null;
		try{
		if (null == attanedList && null != briefingRegistration
				&& null != briefingRegistration.getBsvRegistrationId()) {
			 briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
			briefingReqDTO.setQtBriefingId(briefingRegistration
					.getBsvRegistrationId());
			briefingReqDTO.setSupplierId(briefingRegistration.getSupplier()
					.getSupplierId());
			BriefingResDTO resDto = briefingSO
					.retrieveBriefingAttendanceList(briefingReqDTO);
			attanedList = resDto.getBriefingAttResultBean().getResultList();
		}
		
		if (null == attanedList || attanedList.isEmpty()) {
			attanedList = new ArrayList<ScQtBsvAttendance>();
			briefingSO  = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			SmSupplier supplier = briefingRegistration.getSupplier();
			if (supplier != null)
				reqDTO.setSupplierId(supplier.getSupplierId());//Sonarqube #83
			BriefingResDTO resDTO = briefingSO
					.retrieveAuthriozesPersonalBySupplierId(reqDTO);
			List<SmPersonnel> personals = resDTO.getPersonals();
			log.debug("Personal list size**************" + personals.size());

			/**
			 * Below code to add the smpersonal to attandance list if alredy
			 * attanded persons mark it.
			 */

			for (SmPersonnel personnel : personals) {
				ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
				briefingAttendance.setName(personnel.getName());
				briefingAttendance.setIcPassPort(personnel
						.getIdentificationNo());
				briefingAttendance.setContactNo(personnel.getMobileNo());
				briefingAttendance.setEmail(personnel.getEmail());
				briefingAttendance.setBsvRegistration(briefingRegistration);
				briefingAttendance.setAuthorized(true);
				attanedList.add(briefingAttendance);
			}
		}
		
		this.getManagedBean().getSelectedbriefRegistration()
			.setBsvAttendance(attanedList);
		}finally{
			briefingSO=null;	
		}
	}

	/**
	 * To add the attandance user click add button.
	 */
	public void addAttendanceForBSV() {
		debugLogger("addAttendanceForBSV");
		if (null != this.getManagedBean().getSelectedbriefRegistration()) {
			List<ScQtBsvAttendance> attandances = this.getManagedBean()
					.getSelectedbriefRegistration().getBsvAttendance();
			ScQtBsvAttendance briefingAttendance = new ScQtBsvAttendance();
			briefingAttendance.setBsvRegistration(this.getManagedBean()
					.getSelectedbriefRegistration());
			briefingAttendance.setAttended(true);
			briefingAttendance.setBrandNew(false);
			if (null == attandances || attandances.isEmpty()) {
				attandances = new ArrayList<ScQtBsvAttendance>();
				attandances.add(briefingAttendance);
			} else {
				attandances.add(briefingAttendance);
			}
			// this.getManagedBean().setBriefingAttendanceList(attandances);
			this.getManagedBean().getSelectedbriefRegistration()
					.setBsvAttendance(attandances);
		} else {
			throw new EpException(MessageConst.ERROR_760185);
		}
	}

	/**
	 * Bsv registation listner.
	 *
	 * @param mofAccounts the mof accounts
	 */
	public void bsvRegistationListner(List<SmMofAccount> mofAccounts) {
		debugLogger("bsvRegistationListner");
		if (getManagedBean().getBriefingRegistrationList() == null
				|| getManagedBean().getBriefingRegistrationList().isEmpty()) {
			getManagedBean().setBriefingRegistrationList(
					new ArrayList<ScQtBsvRegistration>());
		}
		
		if (mofAccounts != null) {
			for (int i = 0; i < mofAccounts.size(); i++) {
				SmMofAccount mofAccount = mofAccounts.get(i);
				ScQtBsvRegistration bsvRegistration = new ScQtBsvRegistration();
				bsvRegistration.setSupplier(mofAccount.getSmSupplier());
				bsvRegistration.getSupplier().setSmMofAccounts(mofAccounts);
				bsvRegistration.setBsvDtl(this.getManagedBean().getQtBriefing());
				if (getManagedBean().getBriefingRegistrationList().contains(
						bsvRegistration)) {
					throw new EpException(MessageConst.ERROR_760186);
				}
				getManagedBean().getBriefingRegistrationList().add(
						bsvRegistration);
				if (i == mofAccounts.size() - 1) {
					this.getManagedBean().setSelectedbriefRegistration(
							bsvRegistration);
					getAttandanceDtlsBySelectedRegistartion(bsvRegistration);
				}
			}
		}
	}

	/**
	 * Method to delete the registrations.
	 */
	public void deleteBsvAttandances() {
		debugLogger("deleteBsvAttandances");
		List<ScQtBsvAttendance> attandances = null;
		if (null != this.getManagedBean().getSelectedbriefRegistration()) {
			attandances = this.getManagedBean().getSelectedbriefRegistration()
					.getBsvAttendance();
		}
		ScQtBsvAttendance selectedBsvAttandance = this.getManagedBean()
				.getScQtBriefingAttendance();
		if (null == selectedBsvAttandance) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (null != attandances && !selectedBsvAttandance.isAuthorized()) {
			for (int i = 0; i < attandances.size(); i++) {

				if (attandances
						.get(i)
						.getIcPassPort()
						.equalsIgnoreCase(selectedBsvAttandance.getIcPassPort())) {
					attandances.remove(i);
					break;
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760126);
		}
	}

	/**
	 * Adds the supplier details.
	 */
	public void addSupplierDetails() {
		debugLogger("addSupplierDetails");
		try {
			ScQtBsvDtl scQtBsvDtl = getManagedBean().getQtBriefing();
			// ScQtBriefing scQtBriefing = getManagedBean().getQtBriefing();
			ScQtBsvRegistration briefReg = new ScQtBsvRegistration();
			// ScQtBriefingRegistration briefReg = new
			// ScQtBriefingRegistration();
			SmSupplier smSupplier = new SmSupplier();
			SmMofAccount account = new SmMofAccount();
			List<SmMofAccount> accounts = new ArrayList<SmMofAccount>();
			accounts.add(account);
			smSupplier.setSmMofAccounts(accounts);
			briefReg.setSupplier(smSupplier);
			briefReg.setBsvDtl(scQtBsvDtl);

			List<ScQtBsvRegistration> attendaceforComp = getManagedBean()
					.getBriefingRegistrationList();

			if (null == attendaceforComp) {
				attendaceforComp = new ArrayList<ScQtBsvRegistration>();
			}

			attendaceforComp.add(briefReg);

			this.getManagedBean().setBriefingRegistrationList(attendaceforComp);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Method to delete the registrations.
	 */
	public void deleteSupplierDetails() {
		debugLogger("deleteSupplierDetails");
		List<ScQtBsvRegistration> registrations = this.getManagedBean()
				.getBriefingRegistrationList();
		if (null == registrations || registrations.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		ScQtBsvRegistration selectedReqistration = this.getManagedBean()
				.getSelectedbriefRegistration();
		if (null == selectedReqistration) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		for (int i = 0; i < registrations.size(); i++) {
			if (registrations
					.get(i)
					.getSupplier()
					.getCompanyName()
					.equalsIgnoreCase(
							this.getManagedBean()
									.getSelectedbriefRegistration()
									.getSupplier().getCompanyName())) {

				registrations.remove(i);
				break;
			}
		}
		
		this.getManagedBean().setBriefingRegistrationList(registrations);
		this.getManagedBean().setBriefingAttendanceList(null);
	}

	/**
	 * Briefing back action.
	 *
	 * @return the string
	 */
	public String briefingBackAction() {
		debugLogger("briefingBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Method to add briefing details to site visit details.
	 */
	public void addToSiteVisitDtl() {
		debugLogger("addToSiteVisitDtl");
		BriefingZoneBean briefingZoneBean = this.getManagedBean()
				.getBsvDtlBean();
		briefingZoneBean.getScQtBsv().setAllSiteVisitReq(true);
		if (briefingZoneBean.getBriefingList() != null
				&& !briefingZoneBean.getBriefingList().isEmpty()) {
			List<ScQtBsvDtl> selectedBsvDtls = briefingZoneBean
					.getSelectedBriefingDtls();
			List<ScQtBsvDtl> siteVisitDetails = briefingZoneBean
					.getSitevisitList();
			if (null == siteVisitDetails) {
				siteVisitDetails = new ArrayList<ScQtBsvDtl>();
			}
			if (selectedBsvDtls != null && !selectedBsvDtls.isEmpty()) {
				/*
				 * for (ScQtBsvDtl bsvDtls : selectedBsvDtls) { if
				 * (siteVisitDetails.contains(bsvDtls)) { //
				 * briefingZoneBean.setSitevisitList(null); throw new
				 * EpException(MessageConst.ERROR_760099); } }
				 */

				for (ScQtBsvDtl bsvDtl : selectedBsvDtls) {
					ScQtBsvDtl siteVisitDtl = null;
					try {

						siteVisitDtl = (ScQtBsvDtl) BeanUtils.cloneBean(bsvDtl);
						siteVisitDtl.setChangedDate(null);
						siteVisitDtl.setQtBsvDtlId(null);
						siteVisitDtl
								.setBsvType(SourcingConstants.BSV_TYPE_SITEVISIT);
						siteVisitDtl.setQtBsvRegistrations(null);

					} catch (Exception e) {
					}
					if (siteVisitDetails.contains(siteVisitDtl)) {
						// briefingZoneBean.setSitevisitList(null);
						throw new EpException(MessageConst.ERROR_760099);
					} else {
						siteVisitDetails.add(siteVisitDtl);
					}
				}
				briefingZoneBean.setSitevisitList(siteVisitDetails);
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}
			briefingZoneBean.setSelectedBriefingDtls(null);
			briefingZoneBean.setSelectedSiteVisitDtls(null);
			this.getManagedBean().setBsvDtlBean(briefingZoneBean);
		} else {
			throw new EpException(MessageConst.ERROR_760199);
		}
		
		updateLatestBsvDate();
	}

	/**
	 * Cancel briefing list.
	 */
	public void cancelBriefingList() {
	    debugLogger("cancelBriefingList");
	
	    //#28188 - Start
	    /*
	     * IMPORTANT:
	     * qtBriefing is pointing to the same object inside
	     * briefingList/sitevisitList.
	     *
	     * Therefore restore the original values BEFORE setting
	     * qtBriefing to null.
	     */
	    restoreBsvDetailAfterCancel();
	    this.getManagedBean().setQtBriefing(null);
	    this.getManagedBean().setDistricts(null);
	    this.getManagedBean().setStates(null);
	    this.getManagedBean().setCitys(null);
	    this.getManagedBean().setDivisionList(null);
	    this.getManagedBean().setDistrictList(null);
	    this.getManagedBean().setTempScQtBsvDtl(null);
	    this.getManagedBean().setBsvDBStateId(null);
	    //#28188 - End

	    getManagedBean().setRequired(Boolean.FALSE);
	    RequestContext.getCurrentInstance().execute("addbsvdtls.hide();");
	}

	/**
	 * Briefing click event.
	 *
	 * @param event the event
	 */
	public void briefingClickEvent(AjaxBehaviorEvent event) {
		debugLogger("briefingClickEvent");
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			
		} else {
			zoneBean.getScQtBsv().setMinBriefingAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}

	/**
	 * Site visit click event.
	 *
	 * @param event the event
	 *//*
	public void siteVisitClickEvent(AjaxBehaviorEvent event) {
		debugLogger("siteVisitClickEvent");
		Boolean selectedValue = (Boolean) event.getComponent().getAttributes()
				.get("value");
		BriefingZoneBean zoneBean = this.getManagedBean().getBsvDtlBean();
		if (selectedValue) {
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		} else {
			zoneBean.getScQtBsv().setMinSiteVisitAttend(0);
		}
		this.getManagedBean().setBsvDtlBean(zoneBean);

	}*/

	/**
	 * Edits the action.
	 *
	 * @param bsvDtl the bsv dtl
	 * @param index the index
	 * @param bsvType the bsv type
	 */
	public void editAction(ScQtBsvDtl bsvDtl, int index, String bsvType) {
		debugLogger("editAction");
		bsvDtl.setSelected(true);
		this.getManagedBean().setRequired(true);
		this.getManagedBean().setBsvIndex(index);
		this.getManagedBean().setQtBriefing(bsvDtl);
		this.getManagedBean().setBsvType(bsvType);
		this.getManagedBean().setBsvDBStateId(null);
		RequestContext.getCurrentInstance().execute("addbsvdtls.show();");
		retrievePmStateList();
		ScAddress currentAddress = bsvDtl.getAddress();
		this.getManagedBean().setBsvDBStateId(currentAddress.getPmState().getStateId());
		if (super.isQTStatusInBetween(getManagedBean().getCurrentStatusId(), 
				QTStatusConst.QT_PENDING_PUBLICATION, 
				QTStatusConst.QT_BRIEFING_SITE_VISIT)){
			
			ScQtBsvDtl tempScQtBsvDtl = new ScQtBsvDtl();
			
			ScAddress tempPmAddress = new ScAddress();
			PmStatus currentBsvStatus = bsvDtl.getBsvStatus();
			try {				
				ConvertUtilsBean convertUtilsBean = BeanUtilsBean
						.getInstance().getConvertUtils();
				convertUtilsBean.register(false, true, -1);
				
				// tempScQtBsvDtl
				BeanUtils.copyProperties(tempScQtBsvDtl, bsvDtl);
				
				// PmAddress
				tempScQtBsvDtl.setAddress(tempPmAddress);
				BeanUtils.copyProperties(tempPmAddress, currentAddress);

				// PmState
				tempPmAddress.setPmState(new PmState());
				tempPmAddress.getPmState().setStateId(currentAddress.getPmState().getStateId());
                 
				// PmDivision
				tempPmAddress.setPmDivision(new PmDivision());
				tempPmAddress.getPmDivision().setDivisionId(currentAddress.getPmDivision().getDivisionId());

				// PmDistrict
				tempPmAddress.setPmDistrict(new PmDistrict());
				tempPmAddress.getPmDistrict().setDistrictId(currentAddress.getPmDistrict().getDistrictId());

				// PmCity
				tempPmAddress.setPmCity(new PmCity());
				tempPmAddress.getPmCity().setCityId(currentAddress.getPmCity().getCityId());
				
				if (currentBsvStatus != null) {
					// update transient for bsv status id
					bsvDtl.setBsvStatusId(currentBsvStatus.getStatusId());
					
					tempScQtBsvDtl.setBsvStatus(new PmStatus());
					tempScQtBsvDtl.getBsvStatus().setStatusId(currentBsvStatus.getStatusId());	
				}
				
				//#28188 - Start
				Date restrictionDate = null;
				/*
				 * CASE 1:
				 * Existing pending child from previous POSTPONE.
				 *
				 * IMPORTANT:
				 * Separate:
				 *
				 * 1. tempScQtBsvDtl.bsvDate
				 *    = current working value, used for Cancel restore.
				 *
				 * 2. briefingSiteVisitPostponeDate
				 *    = ORIGINAL/PARENT restriction date.
				 *
				 * Do NOT use the newly selected child date as restriction.
				 */
				if (bsvDtl.getParentPostponeId() != null) {

				    /*
				     * tempScQtBsvDtl was already copied from bsvDtl using
				     * BeanUtils.copyProperties().
				     *
				     * Therefore DO NOT modify tempScQtBsvDtl.bsvDate here.
				     *
				     * If child date is NULL -> temp stays NULL.
				     * If child date was selected after OK -> temp keeps that value.
				     */

				    ScQtBsvDtl parentDtl = retrieveBSVDetail(bsvDtl.getParentPostponeId());

				    if (parentDtl != null
				            && parentDtl.getBsvDate() != null) {

				        restrictionDate = parentDtl.getBsvDate();

				        /*
				         * This is the REAL minimum/reference date.
				         */
				        this.getManagedBean()
				                .setBriefingSiteVisitPostponeDate(
				                        restrictionDate);
				    }
				}
				
				/*
				 * CASE 2:
				 * Normal existing BSV.
				 *
				 * KEEP your existing behaviour.
				 */
				else if (bsvDtl.getQtBsvDtlId() != null) {
				    ScQtBsvDtl dbDtl = retrieveBSVDetail(bsvDtl.getQtBsvDtlId());

				    Date originalBsvDate = null;
				    if (dbDtl != null && dbDtl.getBsvDate() != null) {
				        originalBsvDate = dbDtl.getBsvDate();
				    } else {
				        originalBsvDate = bsvDtl.getBsvDate();
				    }

				    if (originalBsvDate != null) {
				        this.getManagedBean().setBriefingSiteVisitPostponeDate(originalBsvDate);
				        tempScQtBsvDtl.setBsvDate(originalBsvDate);
				    }
				}
				/*
				 * CASE 3:
				 * Brand-new / unsaved record.
				 *
				 * KEEP existing behaviour.
				 */
				else {
				    tempScQtBsvDtl.setBsvDate(bsvDtl.getBsvDate());
				    if (bsvDtl.getParentQtBsvDtlId() != null) {
				        ScQtBsvDtl parentDtl =
				                retrieveBSVDetail(bsvDtl.getParentQtBsvDtlId());

						if (parentDtl != null && parentDtl.getBsvDate() != null) {
							this.getManagedBean().setBriefingSiteVisitPostponeDate(
											parentDtl.getBsvDate());
						}
				    }
				}
				//#28188 - End
			} catch (IllegalAccessException e) {
				log.error("Exception occured while copying properties"
						+ e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("Exception occured while copying properties"
						+ e.getMessage());
			}
			this.getManagedBean().setTempScQtBsvDtl(tempScQtBsvDtl);
		}
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Will be called on change of accordian panel..
	 *
	 * @param event the event
	 */
	public void onZoneTabChange(TabChangeEvent event) {
		debugLogger("onZoneTabChange");
		BriefingZoneBean zoneBean = (BriefingZoneBean) event.getData();

		// this.getManagedBean().setBsvZoneindx(1);
		this.getManagedBean().setBsvDtlBean(zoneBean);
	}
	
	/**
	 * Validate postcode.
	 */
	public void validatePostcode() {
		debugLogger("validatePostcode");
		String postcode = getManagedBean().getQtBriefing().getAddress().getPostcode();
		Pattern pattern = Pattern.compile("\\d{5}");
		Matcher matcher = pattern.matcher(postcode);
		if (!matcher.matches()) {
			throw new EpException("Postcode is not valid, Postcode Should be limited to 5 digits.");
		}

	}
	
	/** 
	 * Update time stamp value into SCQTAMEND table if any changes occurs 
	 * in Briefing/Visit.
	 * 
	 * @param bsvType
	 * @param bsvDetail
	 * @param tempScQtBsvDtl
	 */
	private void updateQtAmmendMentDtls(String bsvType,
			ScQtBsvDtl oldBsvDtl, ScQtBsvDtl newBsvDtl) {

		String fieldPrefix = oldBsvDtl.getQtBsvDtlId() + QTFieldConst.FIELD_QT_BSV_SEPARATOR;
		QtAmendMentBean amendMentBean = this.getManagedBean().getAmendMentBean();
		
		if (null == amendMentBean) {
			amendMentBean = new QtAmendMentBean();
		}
		
		// bsvDesc
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_DESC, 
					oldBsvDtl.getBsvDesc(), newBsvDtl.getBsvDesc(),
					true);
		}
		//#28188 - End
		
		// bsvDate
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_DATE, 
					oldBsvDtl.getBsvDate(), newBsvDtl.getBsvDate(),
					true);
		}
		//#28188 - End
		
		// Proposal Start Date
		ScQt scQt = getManagedBean().getQt();
		Date oldProposalStartDate = scQt.getProposalStartDate();
		updateLatestBsvDate();
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				QTFieldConst.FIELD_QT_PROPOSAL_START_DATE, 
				oldProposalStartDate, scQt.getProposalStartDate(),
				true);
		
		// location
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
			putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_LOCATION, 
				oldBsvDtl.getLocation(), newBsvDtl.getLocation(),
				true);
		}
		//#28188 - End

		// address
		ScAddress oldAddress = oldBsvDtl.getAddress();
		ScAddress newAddress = newBsvDtl.getAddress();
		if (newAddress.getAddress2().equals("")) {
			newAddress.setAddress2(null);
		}
		if (newAddress.getAddress3().equals("")) {
			newAddress.setAddress3(null);
		}
		/*if (newAddress.getAddress4().equals("")) {
			newAddress.setAddress4(null);
		}*/
		
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
			// address1
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_1, 
					oldAddress.getAddress1(), newAddress.getAddress1(),
					true);
			// address2
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_2, 
					oldAddress.getAddress2(), newAddress.getAddress2(),
					true);
			// address3
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_3, 
					oldAddress.getAddress3(), newAddress.getAddress3(),
					true);
			// address4
			/*putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_4, 
					oldAddress.getAddress4(), newAddress.getAddress4());*/
			// postcode
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_POSTCODE, 
					oldAddress.getPostcode(), newAddress.getPostcode(),
					true);
			
			// cityId
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_ADDRESS_CITY, 
					oldAddress.getPmCity().getCityId(), newAddress.getPmCity().getCityId(),
					true);
		}
		//#28188 - End

		// coordinatingOfficer
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_COORDINATING_OFFICER, 
				oldBsvDtl.getCoordinatingOfficer(), newBsvDtl.getCoordinatingOfficer(),
				true);
		}
		//#28188 - End

		// maxNoOfAttendance
		//#28188 - Start
		if(!newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_MAX_NO_OF_ATTENDANCE, 
				oldBsvDtl.getMaxNoOfAttendance(), newBsvDtl.getMaxNoOfAttendance(),
				true);
		}
		//#28188 - End

		// bsvStatus
		Long oldBsvStatusId = oldBsvDtl.getBsvStatus() != null ? oldBsvDtl.getBsvStatus().getStatusId() : null;
		Long newBsvStatusId = newBsvDtl.getBsvStatus() != null ? newBsvDtl.getBsvStatus().getStatusId() : null;
		//#28188 - Start
		if(newBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){	
			oldBsvStatusId = retrieveOldBsvStatus(bsvType, newBsvDtl);

			// bsvRecordStatus
			putIntoAmendMentBeanIfChanged(amendMentBean, 
					fieldPrefix + QTFieldConst.FIELD_QT_BSV_RECORD_STATUS, 
					//DbValueConst.RECORD_STATUS_IN_PROGRESS, DbValueConst.RECORD_STATUS_ACTIVE,
					//DbValueConst.RECORD_STATUS_ACTIVE, DbValueConst.RECORD_STATUS_ACTIVE, //Change to RE_APPROVAL status for postponed 
					DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL, DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL,
					true);
			
		}
		//#28188 - End
		putIntoAmendMentBeanIfChanged(amendMentBean, 
				fieldPrefix + QTFieldConst.FIELD_QT_BSV_STATUS, 
				oldBsvStatusId, newBsvStatusId,
				true);
		
		this.getManagedBean().setAmendMentBean(amendMentBean);
		this.getManagedBean().setTempScQtBsvDtl(null);

	}
	
	/**
	 * Put into AmendmentBean if any changes.
	 * 
	 * @param amendMentBean
	 * 		the QtAmendMentBean instance
	 * @param key
	 * 		the field key
	 * @param oldValue
	 * 		the old value
	 * @param newValue
	 * 		the new value
	 * @return	the indicator field is changed.
	 */
	public boolean putIntoAmendMentBeanIfChanged(QtAmendMentBean amendMentBean, String key, Object oldValue, Object newValue, boolean ptjApprovalReqFlg) {
		boolean isChanged = false;
		QtAmendMentValueBean oldValueBean = amendMentBean.getValueMap().get(key);
		
		// Always get the original old value. 
		if (oldValueBean != null) {
			oldValue = oldValueBean.getOldValue();
		}
		
		if (oldValue instanceof String || newValue instanceof String) {
			if (!StringUtils.equals((String)oldValue, (String)newValue)) {
				isChanged = true;
			}
		} else {
			if (oldValue != null && newValue != null &&
					!oldValue.equals(newValue)) {
				isChanged = true;
			}	
		}
		
		if (isChanged) {
			QtAmendMentValueBean valueBean = new QtAmendMentValueBean();
			valueBean.setOldValue(oldValue);
			valueBean.setNewValue(newValue);
			amendMentBean.getValueMap().put(key, valueBean);
			
			if (ptjApprovalReqFlg) {
				amendMentBean.setPTJApprovalReq(true);	
			} else {
				valueBean.setApprovalDate(new Date());
				valueBean.setIsApproved(true);
			}
		} else {
			// remove if had been revert to old value.
			if (oldValueBean != null) {
				amendMentBean.getValueMap().remove(key);
			}
		}
		
		return isChanged;
	}

	private void updateLatestBsvDate() {
		ScQt scQt = getManagedBean().getQt();
		Date latestBsvDate = null;
		
		scQt.setBsvRequired(false);
		
		if (scQt.isZonal()) {
			List<BriefingZoneBean> briefingBeans = getManagedBean()
					.getBriefingZoneBean();
			if (null != briefingBeans && !briefingBeans.isEmpty()) {
				for (BriefingZoneBean bsvDtlBean : briefingBeans) {
					latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getBriefingList());
					latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getSitevisitList());
				}
			} 
		} else {
			BriefingZoneBean bsvDtlBean = getManagedBean().getBsvDtlBean();
			if (bsvDtlBean != null) {
				latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getBriefingList());
				latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getSitevisitList());
			} 
		}
		
		// if no briefing / site visit.
		if (!scQt.isBsvRequired()) {
			latestBsvDate = scQt.getPublishDate();
		} 
		
		scQt.setProposalStartDate(latestBsvDate);
		updatePublishClosingDate(scQt);
	}
	
	private Date updateProposalStartDate(Date latestBsvDate, List<ScQtBsvDtl> scQtBsvDtls) {
		ScQt scQt = getManagedBean().getQt();
		
		if (CollectionUtils.isNotEmpty(scQtBsvDtls)) {
			for (ScQtBsvDtl scQtBsvDtl : scQtBsvDtls) {
				Date bsvDate = scQtBsvDtl.getBsvDate();
				
				if (bsvDate != null &&
						(scQtBsvDtl.getBsvStatus() == null || !QTStatusConst.BSV_CANCELLED.equals(scQtBsvDtl.getBsvStatus().getStatusId())) && 
						(latestBsvDate == null || (latestBsvDate != null && bsvDate.after(latestBsvDate)))) {
					latestBsvDate = bsvDate;
					//20190718@asyrafhaziq - Bug #26137 - Start
					scQt.setBsvRequired(true);
					//20190718@asyrafhaziq - Bug #26137 - End
				}
			}
		}
		
		return latestBsvDate;
	}
	
	/**
	 * Get the BsvMinDate.
	 * @return minDate
	 */
	public Date handleBSVMinDate() {
		Date minDate = DateUtils.addDays(handlePublishDate(),1);
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		if (isQTStatusPublished(statusId)) {
			minDate = DateUtils.addDays(scQt.getPublishDate(),1);
		}
		//#28188 - Start
		Date currentDate = new Date();
		Date postponeDate = this.getManagedBean().getBriefingSiteVisitPostponeDate();

		if (postponeDate != null) {
			if (postponeDate.before(currentDate)) {
				minDate = currentDate;
			} else {
				minDate = postponeDate;
			}
		}
		//#28188 - End
		return minDate;
	}
	
	//#28188 - Start
	/**
	 * Get the landing method for retrieve BSV Min Date.
	 * @return handleBSVMinDate
	 */
	public Date handleOriginalBSVMinDate() {
	
	    Date normalMinDate = handleBSVMinDate();
	    ScQtBsvDtl tempDtl = this.getManagedBean().getTempScQtBsvDtl();

	    if (tempDtl != null && tempDtl.getBsvDate() != null) {
	        Date originalDate = tempDtl.getBsvDate();
	        /*
	         * If original old BSV date is backdated,
	         * use current/normal min date instead.
	         */
	        if (originalDate.before(normalMinDate)) {
	            return normalMinDate;
	        }
	        return originalDate;
	    }

	    return normalMinDate;
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Get the BSV Calendar Min Date.
	 *
	 * For postponed child:
	 * Date is still selectable.
	 * Time restriction will be handled separately.
	 *
	 * @return CalendarMinDate
	 */
	public Date handleBSVCalendarMinDate() {
	    Date currentDate = clearTime(new Date());
	    ScQtBsvDtl currentDtl = this.getManagedBean().getQtBriefing();
	    ScQtBsvDtl tempDtl = this.getManagedBean().getTempScQtBsvDtl();

	    /*
	     * #28188
	     * Postpone child.
	     *
	     * IMPORTANT:
	     * Do NOT use tempDtl.bsvDate as restriction.
	     *
	     * tempDtl.bsvDate may contain the new working date
	     * selected before pressing final Submit.
	     *
	     * Always use ORIGINAL/PARENT date.
	     */
	    if (currentDtl != null
	            && currentDtl.getParentPostponeId() != null) {

	        Date restrictionDate = this.getManagedBean()
	                        .getBriefingSiteVisitPostponeDate();

	        if (restrictionDate != null) {
	            Date restrictionDateOnly = clearTime(restrictionDate);

	            /*
	             * FUTURE original date.
	             *
	             * Example:
	             * original = 03/09/2026
	             * today    = 01/09/2026
	             *
	             * Minimum remains 03/09/2026.
	             */
	            if (restrictionDateOnly.after(currentDate)) {
	                return restrictionDateOnly;
	            }

	            /*
	             * PAST / CURRENT original date.
	             *
	             * Minimum becomes current date.
	             */
	            return currentDate;
	        }
	    }

	    /*
	     * ORIGINAL LOGIC.
	     * Keep unchanged for normal BSV.
	     */
	    if (tempDtl != null
	            && tempDtl.getBsvDate() != null) {

	        Date oldDateOnly = clearTime(tempDtl.getBsvDate());

	        if (oldDateOnly.after(currentDate)) {
	            return oldDateOnly;
	        }
	    }

	    return currentDate;
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Get the BSV Restriction Date Time.
	 *
	 * For postpone child:
	 * restriction is based on OLD/PARENT BSV date/time.
	 * 
	 * DateTimeBackingBean will apply modVal:
	 * @return DateTime
	 */
	public Date handleBSVRestrictionDateTime() {
	    Date now = new Date();
	    ScQtBsvDtl currentDtl = this.getManagedBean().getQtBriefing();
	    ScQtBsvDtl tempDtl = this.getManagedBean().getTempScQtBsvDtl();

	    /*
	     * CASE 1:
	     * Pending child generated from POSTPONE.
	     *
	     * Always use ORIGINAL/PARENT BSV as restriction.
	     *
	     * Do NOT use tempDtl.bsvDate because that may already
	     * contain an unsaved date selected after pressing OK.
	     */
	    if (currentDtl != null
	            && currentDtl.getParentPostponeId() != null) {

	        Date parentDate = this.getManagedBean()
	        		.getBriefingSiteVisitPostponeDate();
	        if (parentDate != null) {
	            /*
	             * PAST / CURRENT:
	             *
	             * If now is later than original BSV,
	             * use current time.
	             */
	            if (now.after(parentDate)) {
	                return now;
	            }

	            /*
	             * FUTURE:
	             *
	             * Keep original future BSV restriction.
	             */
	            return parentDate;
	        }
	    }

	    /*
	     * ORIGINAL EXISTING LOGIC.
	     * DO NOT CHANGE.
	     */
	    Date currentDateOnly = clearTime(now);

	    if (tempDtl != null
	            && tempDtl.getBsvDate() != null) {

	        Date oldDateOnly = clearTime(tempDtl.getBsvDate());

	        if (!oldDateOnly.before(currentDateOnly)) {
	            return tempDtl.getBsvDate();
	        }
	    }

	    // important: do not return null
	    return now;
	}
	//#28188 - End
	
	/**
	 * Get BSV Status Dropdown
	 * 
	 * @return	the array of SelectItem instance
	 */
	public SelectItem[] getBsvStatusDropdownValues() {
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		// Filter By - statusId
		List<Long> statusIds = new ArrayList<Long>();
		//#28188 - Start
		if(this.getManagedBean().getQtBriefing().isBrandNew()  || this.getManagedBean().getQtBriefing().getQtBsvDtlId()== null){
			statusIds.add(QTStatusConst.BSV_POSTPONE);//#28188
			statusIds.add(QTStatusConst.BSV_COMPLETED);
			statusIds.add(QTStatusConst.BSV_REQUEST_REJECT);//#28188
			statusIds.add(QTStatusConst.BSV_CANCELLED);//#28188
		}else{
			statusIds.add(QTStatusConst.BSV_COMPLETED);
			statusIds.add(QTStatusConst.BSV_REQUEST_REJECT);//#28188
			statusIds.add(QTStatusConst.BSV_CANCELLED);//#28188
		}
		//#28188 - End
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.statusId",
				SearchOperator.OPERATOR_NOT_IN.value(),
				statusIds));

		return super.getStatusDropdownValuesBySearchBean(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				ParameterConst.DOCTYPE_BSV_REGISTRATION, 
				searchBean);
	}
	
	/**
	 * BSV Status dropdown change action
	 */
	public void bsvStatusChanged() {
		IStatusSO statusSO = null;
		try {
			ScQtBsvDtl scQtBsvDtl = getManagedBean().getQtBriefing();
			Long statusId = scQtBsvDtl.getBsvStatusId();

			if (statusId != null) {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
				scQtBsvDtl.setBsvStatus(resDTO.getPmStatus());	
			}
		} finally {
			statusSO = null;
		}
	}
	
	//#34217 - Start
	public void closeBriefingList() {
		//#28188 - Start
	    restoreBsvDetailAfterCancel();
	    this.getManagedBean().setQtBriefing(null);
	    this.getManagedBean().setDistricts(null);
	    this.getManagedBean().setStates(null);
	    this.getManagedBean().setCitys(null);
	    this.getManagedBean().setDivisionList(null);
	    this.getManagedBean().setDistrictList(null);
	    this.getManagedBean().setTempScQtBsvDtl(null);
	    this.getManagedBean().setBsvDBStateId(null);
	    //#28188 - End
		getManagedBean().setRequired(Boolean.FALSE);
	}
	//#34217 - End
	
	//#28188 - Start
	/**
	 * Retrieve pm city by id.
	 * 
	 * @param cityId
	 *            - Long the city id
	 * @return PmCity object
	 */
	public static PmCity retrievePmCityById(Long cityId) {
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setCityId(cityId);
		CityResDTO resDTO = reqSO.retriveCityById(reqDTO);

		return resDTO.getPmCity();
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Retrieve pm dist by id.
	 * 
	 * @param distId
	 *            - Long the dist id
	 * @return PmDistrict object
	 */
	public static PmDistrict retrievePmDistById(Long distId) {
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(distId);
		DistrictResDTO resDTO = reqSO.retriveDistrictByID(reqDTO);

		return resDTO.getPmDistrict();
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Retrieve pm dist by id.
	 * 
	 * @param distId
	 *            - Long the dist id
	 * @return PmDistrict object
	 */
	private static PmState retrievePmStateById(Long stateId) {
		IStateSO iStateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);
		StateResDTO resDTO = iStateSO.retriveStateByID(reqDTO);
		
		return resDTO.getPmState();
	}
	//#28188 - End
	
	//#28188 - Start
	private List<ScQtBsv> retrieveCurrentBsv(Long qtId) {
	 	IQTBriefingSO briefingSO = null;
	 	//BriefingResDTO resDto = new BriefingResDTO();
	 	List<ScQtBsv> scQtBsvList = new ArrayList<ScQtBsv>();
		try{
			 briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);

			 scQtBsvList = briefingSO.retrieveCurrentBSV(qtId);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	    return scQtBsvList;
	}
	//#28188 - End
	
	//#28188 - Start
	private List<ScQtBsvDtl> retrieveCurrentBSVDetailByBsvType(Long qtBsvId, String bsvType) {
	 	IQTBriefingSO briefingSO = null;
	 	//BriefingResDTO resDto = new BriefingResDTO();
	 	List<ScQtBsvDtl> scQtBsvDtlList = new ArrayList<ScQtBsvDtl>();
		try{
			 briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);

			 scQtBsvDtlList = briefingSO.retrieveCurrentBSVDetailByBsvType(qtBsvId, bsvType);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	    return scQtBsvDtlList;
	}
	//#28188 - End
	
	//#28188 - Start
	/** Null-safe Long equality. */
	private boolean equalsLong(Long a, Long b) {
	    return (a == b) || (a != null && a.equals(b));
	}
	//#28188 - End

	//#28188 - Start
	/** Clone helper that ensures new ID, clears bsvDate, and sets a safe PmStatus with the same statusId. */
	private ScQtBsvDtl makeCloneWithStatus(ScQtBsvDtl src, Long statusId) {
	    ScQtBsvDtl clone = cloneForList(src);    // your existing method
	    // Ensure new identity and date reset per your requirement
	    clone.setQtBsvDtlId(null); //-- disabled temp
	    clone.setBsvDate(null);
	    clone.setRecordStatus(DbValueConst.FLAG_NO);
	    clone.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
	    //src = revertCurrentFromOriginal(src, qtBsvDtlId);

	    // Ensure the clone has its *own* PmStatus instance carrying the same ID
	    PmStatus cloneStatus = new PmStatus();
	    cloneStatus.setStatusId(statusId);
	    clone.setBsvStatus(cloneStatus);
	    
	    //28188 -start-
	    clone.setParentQtBsvDtlId(null);
	    clone.setParentPostponeId(null);
	    //28188 -end-
	    return clone;
	}
	//#28188 - End
	
	//#28188 - Start
	private boolean checkBSVDetailExistAndPostpone(Long editQtBsvDtlId, List<ScQtBsvDtl> currentList) {
		boolean checkFlag = false;
		ScQtBsvDtl scQtBsvDtl = retrieveBSVDetail(editQtBsvDtlId);
		boolean checkPostpone = checkPostponeStatus(currentList);
		if (scQtBsvDtl != null && checkPostpone) {
			checkFlag = true;
		}
		return checkFlag;
	}
	//#28188 - End
	
	//#28188 - Start
	private boolean checkPostponeStatus(List<ScQtBsvDtl> currentList) {
		boolean checkFlag = false;
		for (ScQtBsvDtl currentBsvDtl : currentList) {
			if(currentBsvDtl.getBsvStatus().getStatusId().equals(QTStatusConst.BSV_POSTPONE)){
				checkFlag = true;
				break;
			}
		}
	    return checkFlag;
	}
	//#28188 - End
	
	//#28188 - Start
	/** Clone and optionally force a status on the clone. */
	private ScQtBsvDtl cloneForList(ScQtBsvDtl src) {
		ScQtBsvDtl dst = new ScQtBsvDtl();

		try {
			BeanUtils.copyProperties(dst, src);
			// Important: create new address object
			if (src.getAddress() != null) {
				ScAddress newAddress = new ScAddress();
				BeanUtils.copyProperties(newAddress, src.getAddress());

				if (src.getAddress().getPmState() != null) {
					PmState state = new PmState();
					state.setStateId(src.getAddress().getPmState().getStateId());
					newAddress.setPmState(state);
				}
				if (src.getAddress().getPmDistrict() != null) {
					PmDistrict district = new PmDistrict();
					district.setDistrictId(src.getAddress().getPmDistrict()
							.getDistrictId());
					newAddress.setPmDistrict(district);
				}
				if (src.getAddress().getPmCity() != null) {
					PmCity city = new PmCity();
					city.setCityId(src.getAddress().getPmCity().getCityId());
					newAddress.setPmCity(city);
				}
				dst.setAddress(newAddress);
			}
			return dst;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//#28188 - End
	
	//#28188 - Start
	private ScQtBsvDtl retrieveBSVAddressDetail(Long qtBsvDtlId, String bsvType) {
	 	IQTBriefingSO briefingSO = null;
	 	ScQtBsvDtl scQtBsvDtl = null;
		try{
			 briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			
			 scQtBsvDtl = briefingSO.retrieveBSVAddressDetail(qtBsvDtlId, bsvType);
		}finally{
			briefingSO=null;	
		}

	    return scQtBsvDtl;
	}
	//#28188 - End
	
	//#28188 - Start
	private Long retrieveOldBsvStatus(String bsvType, ScQtBsvDtl newBsvDtl) {
		Long oldBsvStatusId =  null;
		
		if((SourcingConstants.BSV_TYPE_BRIEFING.equalsIgnoreCase(bsvType))){
			List<ScQtBsv> scQtBsvList = retrieveCurrentBsv(newBsvDtl.getScQtBsv().getScQt().getQtId()); 
	       	if (CollectionUtils.isNotEmpty(scQtBsvList)){
				for (ScQtBsv currentBsv : scQtBsvList) {
					List<ScQtBsvDtl> originalBsvDtlList = retrieveCurrentBSVDetailByBsvType(currentBsv.getQtBsvId(), SourcingConstants.BSV_TYPE_BRIEFING);
					if (CollectionUtils.isNotEmpty(originalBsvDtlList)) {
						for (ScQtBsvDtl originalBsvDtl : originalBsvDtlList) {
							oldBsvStatusId = originalBsvDtl.getBsvStatus().getStatusId();
						}
					}	
				}
	    	}
		}else{
			List<ScQtBsv> scQtBsvList = retrieveCurrentBsv(newBsvDtl.getScQtBsv().getScQt().getQtId()); 
	       	if (CollectionUtils.isNotEmpty(scQtBsvList)){
				for (ScQtBsv currentBsv : scQtBsvList) {
					List<ScQtBsvDtl> originalBsvDtlList = retrieveCurrentBSVDetailByBsvType(currentBsv.getQtBsvId(), SourcingConstants.BSV_TYPE_SITEVISIT);
					if (CollectionUtils.isNotEmpty(originalBsvDtlList)) {
						for (ScQtBsvDtl originalBsvDtl : originalBsvDtlList) {
							oldBsvStatusId = originalBsvDtl.getBsvStatus().getStatusId();
						}
					}	
				}
	    	}
		}
		return oldBsvStatusId;
	}
	//#28188 - End
	
	//#28188 - Start
	public void validateBsvDate(FacesContext context, UIComponent component, Object value) {
	    Date selectedDate = (Date) value;
	    Date minDate = handleOriginalBSVMinDate();

	    if (selectedDate != null && minDate != null && selectedDate.before(minDate)) {
	        throw new ValidatorException(
	            new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                "Date/Time cannot be before " + minDate,
	                null)
	        );
	    }
	}
	//#28188 - End
	
	//#28188 - Start
	private ScQtBsvDtl retrieveBSVDetail(Long qtBsvDtlId) {
	 	IQTBriefingSO briefingSO = null;
	 	ScQtBsvDtl scQtBsvDtl = null;
		try{
			 briefingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			
			 scQtBsvDtl = briefingSO.retrieveBSVDetail(qtBsvDtlId);
		}finally{
			briefingSO=null;	
		}

	    return scQtBsvDtl;
	}
	//#28188 - End
	
	//#28188 - Start
	private ScQtBsvDtl getSafeTempDtl(ScQtBsvDtl fallback) {
	    ScQtBsvDtl temp = this.getManagedBean().getTempScQtBsvDtl();
	    return temp != null ? temp : fallback;
	}
	//#28188 - End
	
	//#28188 - Start
	private void populateAddress(ScQtBsvDtl dtl) {
	    if (dtl != null && dtl.getAddress() != null) {
	        if (dtl.getAddress().getPmCity() != null) {
	            dtl.getAddress().setPmCity(
	                retrievePmCityById(dtl.getAddress().getPmCity().getCityId()));
	        }
	        if (dtl.getAddress().getPmDistrict() != null) {
	            dtl.getAddress().setPmDistrict(
	                retrievePmDistById(dtl.getAddress().getPmDistrict().getDistrictId()));
	        }
	        if (dtl.getAddress().getPmState() != null) {
	            dtl.getAddress().setPmState(
	                retrievePmStateById(dtl.getAddress().getPmState().getStateId()));
	        }
	    }
	}
	//#28188 - End
	

	
	//#28188 - Start
	private Date clearTime(Date date) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
	//#28188 - End 
	
	//28188 -start-
	/**
	 * check any supplier data inprogess during bsvdetails edit.
	 * @param bsvRegisterList
	 * @param qtId
	 * @param qtBsvDtlId
	 */
	private void checkBsvRegistartionInprogress(
			List<ScQtBsvRegistration> bsvRegisterList, Long qtId,
			Long qtBsvDtlId) {
		if (CollectionUtils.isNotEmpty(bsvRegisterList)) {
			for (ScQtBsvRegistration bsvRegistration : bsvRegisterList) {
				ScQtApprovalRequest approvalRequest= bsvRegistration.getScQtApprovalRequest();
				if (bsvRegistration.getRecordStatusFlg().equals(
						DbValueConst.RECORD_STATUS_IN_PROGRESS) && null == approvalRequest) {
					createRegistration(qtId, qtBsvDtlId);
					break;
				}
			}
		}
	}

	// #28188 - end-
	
	//28188 -start-
	/**
	 * create bsvRegistration Details.
	 * @param qtId
	 * @param qtBsvDtlId
	 */
	private void createRegistration(Long qtId, Long qtBsvDtlId) {
		this.getManagedBean().setBsvRegisterDetails(
				new HashMap<Long, List<Long>>());
		Map<Long, List<Long>> bsvRegisterDetails = this.getManagedBean()
				.getBsvRegisterDetails();
		List<Long> bsvDetails = bsvRegisterDetails.get(qtId);
		if (null == bsvDetails) {
			bsvDetails = new ArrayList<>();
			bsvDetails.add(qtBsvDtlId);
			bsvRegisterDetails.put(qtId, bsvDetails);
		}
		if (CollectionUtils.isNotEmpty(bsvDetails)
				&& !bsvDetails.contains(qtBsvDtlId)) {
			bsvDetails.add(qtBsvDtlId);
			bsvRegisterDetails.put(qtId, bsvDetails);
		}
		this.getManagedBean().setBsvRegisterDetails(bsvRegisterDetails);

	}
	
	// #28188 - end-
	
	// #28188 - Start
	/**
	 * check published qt for added time.
	 * 
	 * @return Date.
	 */
	public Date checkBSVMaintenanceNormal() {
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		if (!isQTStatusPublished(statusId)) {
			return null;
		} else {
			ScQtBsvDtl tempDtl = this.getManagedBean().getTempScQtBsvDtl();
			if (tempDtl != null && tempDtl.getBsvDate() != null
					&& null != tempDtl.getQtBsvDtlId()) {
				return tempDtl.getBsvDate();
			} else if (tempDtl != null && null == tempDtl.getQtBsvDtlId()) {
				return null;
			}
		}
		return null;
	}
	// #28188 - End
	
	//#28188 - Start
	/**
	 * To Restore BSV detail incase of Cancel the pop-up detail after change 
	 * Address information
	 * 
	 */
	private void restoreBsvDetailAfterCancel() {
	    ScQtBsvDtl currentDtl = this.getManagedBean().getQtBriefing();
	    ScQtBsvDtl tempDtl = this.getManagedBean().getTempScQtBsvDtl();

	    if (currentDtl == null || tempDtl == null) {
	        return;
	    }

	    try {
	        ConvertUtilsBean convertUtilsBean = BeanUtilsBean
	                .getInstance().getConvertUtils();
	        convertUtilsBean.register(false, true, -1);

	        /*
	         * Restore ScQtBsvDtl fields.
	         */
	        BeanUtils.copyProperties(currentDtl, tempDtl);

	        /*
	         * Restore Address using a NEW instance.
	         * Do not share the temporary address reference.
	         */
	        if (tempDtl.getAddress() != null) {

	            ScAddress restoredAddress = new ScAddress();

	            BeanUtils.copyProperties(
	                    restoredAddress,
	                    tempDtl.getAddress());

	            // State
	            restoredAddress.setPmState(new PmState());
	            if (tempDtl.getAddress().getPmState() != null) {
	                restoredAddress.getPmState().setStateId(
	                        tempDtl.getAddress().getPmState().getStateId());
	            }

	            // Division
	            restoredAddress.setPmDivision(new PmDivision());
	            if (tempDtl.getAddress().getPmDivision() != null) {
	                restoredAddress.getPmDivision().setDivisionId(
	                        tempDtl.getAddress().getPmDivision().getDivisionId());
	            }

	            // District
	            restoredAddress.setPmDistrict(new PmDistrict());
	            if (tempDtl.getAddress().getPmDistrict() != null) {
	                restoredAddress.getPmDistrict().setDistrictId(
	                        tempDtl.getAddress().getPmDistrict().getDistrictId());
	            }

	            // City
	            restoredAddress.setPmCity(new PmCity());
	            if (tempDtl.getAddress().getPmCity() != null) {
	                restoredAddress.getPmCity().setCityId(
	                        tempDtl.getAddress().getPmCity().getCityId());
	            }

	            currentDtl.setAddress(restoredAddress);
	        }

	        /*
	         * Restore status using separate object.
	         */
	        if (tempDtl.getBsvStatus() != null) {
	            PmStatus restoredStatus = new PmStatus();
	            restoredStatus.setStatusId(
	                    tempDtl.getBsvStatus().getStatusId());

	            currentDtl.setBsvStatus(restoredStatus);
	        }

	    } catch (IllegalAccessException e) {
	        log.error("Error restoring BSV detail after cancel", e);
	    } catch (InvocationTargetException e) {
	        log.error("Error restoring BSV detail after cancel", e);
	    }
	}
	//#28188 - End
	
	//#28188 - Start
	private void populateAddressNonEdit(ScAddress scAddress) {
	
	    if (scAddress!= null) {
	    	ScAddress scAddressDB = retrieveScAddressyById(scAddress.getAddressId());
	    	
	    	if (scAddressDB.getPmCity() != null) {
	    		scAddress.setPmCity(
	                retrievePmCityById(scAddressDB.getPmCity().getCityId()));
	        }
	        if (scAddressDB.getPmDistrict() != null) {
	        	scAddress.setPmDistrict(
	                retrievePmDistById(scAddressDB.getPmDistrict().getDistrictId()));
	        }
	        if (scAddressDB.getPmState() != null) {
	        	scAddress.setPmState(
	                retrievePmStateById(scAddressDB.getPmState().getStateId()));
	        }
	    }
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Retrieve sc Address by address id.
	 * 
	 * @param addressId
	 *            - Long the address id
	 * @return PmCity object
	 */
	public static ScAddress retrieveScAddressyById(Long addressId) {
		IQTScAddressSO addressSO = (IQTScAddressSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTScAddressSO.class);
		
		ScAddress scAddress = addressSO.retrieveScAddressById(addressId);

		return scAddress;
	}
	//#28188 - End
	
}

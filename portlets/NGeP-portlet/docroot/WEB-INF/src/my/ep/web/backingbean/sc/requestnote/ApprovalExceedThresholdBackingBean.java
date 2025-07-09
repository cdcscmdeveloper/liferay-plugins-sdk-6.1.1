package my.ep.web.backingbean.sc.requestnote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.trackingdiary.TrackingDiarySearchBackingBean;
import my.ep.web.managedbean.sc.requestnote.ApprovalExceedThresholdManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.lang.StringUtils;


/**
 * The Class ApprovalExceedThresholdBackingBean.
 * 
 * @author Zairul
 * @since 28.04.2025
 */
@ManagedBean(name = "approvalExceedThresholdBackingBean")
@RequestScoped
public class ApprovalExceedThresholdBackingBean extends SourcingBaseBackingBean<ApprovalExceedThresholdManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String approvalExceedThresholdPage = "/xhtml/sc/directpurchase/exceedThresholdApproval/approvalExceedThreshold.xhtml";
	
	/**
	 * List of allowed role code
	 */
	private static final List<String> ALLOWED_ROLES = Arrays.asList(RoleCode.BPK_APPROVER.value(),
			RoleCode.BPK_APPROVER_SA.value(), RoleCode.BPK_APPROVER_SK.value(), RoleCode.BPK_CC_APPROVER.value(),
			RoleCode.BPK_CQ_APPROVER.value(), RoleCode.BPK_PQ_APPROVER.value());

	
	/**
	 * Renders the view, resets search info, and navigates to the approval page.
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		FacesContext fc = FacesContext.getCurrentInstance();
		// reset search info
		reset();
		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null, approvalExceedThresholdPage);
	}
	
	/**
	 * Checks if is BPK Approver.
	 *
	 * @return true, if is BPK Approver
	 */
	public boolean isBPKApprover() {
		for (String role : ALLOWED_ROLES) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc() throws NamingException {
		if(isSupplier() && getManagedBean().getSearchDocumentType() != null){
			if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
				FacesScopes face = new FacesScopes();
				List<PmStatusDesc> statusDescsTemp = new ArrayList<>();
				
				List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
						.getStatusDescList(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
								getManagedBean().getSearchDocumentType(), null);

				for (PmStatusDesc statusDesc : statusDescs) {
					if(statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_SUPPLIER_RESPONSE)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_SUBMISSION)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_COMPLETED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_CANCELLED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_CANCELLED_DUE_TO_PID_VALIDITY_EXPIRED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_RESPONSE_SUBMITTED)
							|| statusDesc.getPmStatus().getStatusId().equals(DPStatusConstants.SQ_PENDING_ITEM_CODE_ASSIGNMENT)){
						
						statusDescsTemp.add(statusDesc);
					}
				}
				
				int i = 1;
				SelectItem[] items = new SelectItem[statusDescsTemp.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				
				for (PmStatusDesc statusDescr : statusDescsTemp) {
					items[i++] = new SelectItem(statusDescr.getPmStatus()
							.getStatusId(), statusDescr.getStatusName());
				}
				return items;
			}else{
				return super.getStatusDropdownValues(
						ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
						getManagedBean().getSearchDocumentType());
			}
		}else{
			if(StringUtils.isNotBlank(getManagedBean().getSearchDocumentType())){
				return super.getStatusDropdownValues(
						ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
						getManagedBean().getSearchDocumentType());
			}
				return null;
		}
	}

	/**
	 * Performs a search operation based on the provided request note number or title.
	 * Throws an EpException if the search-related input fields are not valid (e.g., less than 3 characters).
	 * Sets the document type and title for the search, then reloads the document data model.
	 */
	public void search() {
		if ((StringUtils.isNotBlank(getManagedBean().getRequestNoteNo()) && getManagedBean().getRequestNoteNo().trim()
				.length() < 3)
				|| (StringUtils.isNotBlank(getManagedBean().getRequestNoteTitle()) && getManagedBean()
						.getRequestNoteTitle().trim().length() < 3)) {
			throw new EpException(MessageConst.ERROR_716019);
		}

		getManagedBean().setSearchDocumentType(ParameterConst.DOCTYPE_REQUEST_NOTE);
		getManagedBean().setDocumentTypeDesc(ParameterConst.RN_TITLE);
		getManagedBean().getDpDocumentDataModel().reloadData();
	}

	/**
	 * Displays document details, populating tracking diary and viewing the request note.
	 *
	 * @param dpObj Array containing document ID, number, type, and status.
	 */
	public void viewDoc(Object[] dpObj){
		debugLogger("viewDoc");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];

		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);

		RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
		rnBackingBean.viewBpkApproverRequestNote(docId);
		rnBackingBean.getManagedBean().setDpObj(dpObj);
	}
	
	/**
	 * @param dpObj
	 */
	public void performViewDoc(Object[] dpObj) {
		this.viewDoc(dpObj);
	}

	/**
	 * Resets all search fields to default, configuring PTJ/Ministry based on user role.
	 */
	public void reset(){
		this.getManagedBean().setSearchFunctionType(null);
		this.getManagedBean().setRequestNoteNo(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setRequestNoteTitle(null);
		this.getManagedBean().resetDocumentDataModel();
		this.getManagedBean().setDisablePtjGroupLov(true);
		this.getManagedBean().setDisablePtjLov(true);
		this.getManagedBean().setCounter(0);	
		
		if(isBPKApprover()){
			getManagedBean().setGovernment(Boolean.TRUE);

			// show default Ministry, Kumpulan PTJ and PTJ
			getDefaultPtjAndMinistryValues();

		} else if(isSupplier()){
			getManagedBean().setGovernment(Boolean.FALSE);
		}
	}

	/**
	 * Gets the user name.
	 *
	 * @param userId the user id
	 * @return the user name
	 */
	public String getUserName(long userId){
		String userName = "";
		UserReqDTO userReqDTO  = new UserReqDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setUserId(userId);
		PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
		if(pmUser != null){
			userName = pmUser.getUserName();
		}
		return userName;
	}

	/**
	 * Creates and returns a new instance of {@link FacesScopes}.
	 *
	 * @return A new {@code FacesScopes} object.
	 */
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the org name.
	 *
	 * @param orgProfileId the org profile id
	 * @return the org name
	 */
	public String getOrgName(Long orgProfileId) {
		String orgName="";

		if (orgProfileId != null) {
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByID(orgProfileReqDTO);
			if(orgProfileResDTO.getPmOrgProfile() != null){
				orgName = orgProfileResDTO.getPmOrgProfile().getOrgName();
			}
			return orgName;
		}
		return null;
	}
	
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuilder condition = new StringBuilder("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				for (int i = 0; i < orgLevel; i++) {
					condition.append("parentOrgProfile.");
				}
				condition.append("orgProfileId");
				condition.append(SourcingConstants.LOV_CONDITION_SEPARATOR);
				condition.append(searchMinistryProfile.getOrgProfileId());

			}

		} 
		return condition.toString();
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPtjCondition() {
		debugLogger("getPTJCondition");
		StringBuilder condition = new StringBuilder("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		PmOrgProfile searchPtjGroupProfile = this.getManagedBean().getSearchPtjGroupProfile();
		if (null != searchPtjGroupProfile && searchPtjGroupProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchPtjGroupProfile.getOrgProfileId());
		} else if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchMinistryProfile.getOrgProfileId());
		} 
		return condition.toString();
	}
	
	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void postUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("postUpdateOrgProfileListener");

		getManagedBean().setCounter(0);

		PmOrgProfile currentProcessingProfile = null;

		if (orgProfile instanceof PmOrgProfile) {
			currentProcessingProfile = (PmOrgProfile) orgProfile;
		} else {
			return;
		}

		while (currentProcessingProfile != null) {
			String orgType = currentProcessingProfile.getOrgTypeCode().getParameterCode();
			PmOrgProfile nextProfileToProcess = null;

			debugLogger("Processing orgType: " + orgType + ", Counter: " + getManagedBean().getCounter());

			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);

				getManagedBean().setSearchPtjGroupProfile(ministryOrgProfile);

				getManagedBean().setCounter(1);

				nextProfileToProcess = ministryOrgProfile;

			} else {
				if (getManagedBean().getSearchMinistryProfile() == null
						|| getManagedBean().getSearchMinistryProfile().getOrgProfileId() == null) {
					getManagedBean().setSearchMinistryProfile(
							retrieveMinistryOrgProfile(currentProcessingProfile.getOrgProfileId()));
					getManagedBean().setDisablePtjGroupLov(false);
					getManagedBean().setDisablePtjLov(true);
				}

				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					getManagedBean().setSearchPtjProfile(new PmOrgProfile());
					getManagedBean().setDisablePtjGroupLov(false);
					getManagedBean().setDisablePtjLov(false);

					if (getManagedBean().getCounter() == 1) {
						getManagedBean().setDisablePtjLov(true);
					}
					getManagedBean().setCounter(0);

				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					getManagedBean().setDisablePtjGroupLov(false);
					getManagedBean().setDisablePtjLov(false);
					if (getManagedBean().getSearchPtjGroupProfile() == null
							|| getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == null) {
						getManagedBean().setSearchPtjGroupProfile(
								retrieveJabatanOrgProfile(currentProcessingProfile.getOrgProfileId()));
					}
					getManagedBean().setCounter(0);
				}
			}

			currentProcessingProfile = nextProfileToProcess;
		}

		debugLogger("postUpdateOrgProfileListener end");
	}
    
    /**
   	 * The method used to disable the ministry lov
   	 * if login user is or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is ministry contract administrator/internal auditor
   	 *         false, if login user is not ministry contract administrator/internal auditor
	 */
    public boolean disableMinistryLov() {
    	return (isReqOrApprover() || isContractAdminOrInternalAudit());
    }
     
  
    /**
     * Checks if is requisitioner or approver.
     *
     * @return true, if is requisitioner or approver
     */
    public boolean isReqOrApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value()));
	}
   
   
    /**
     * Checks if is contract admin or internal audit.
     *
     * @return true, if is contract admin or internal audit
     */
    public boolean isContractAdminOrInternalAudit() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()));
	}
    
    /**
     * Checks if is search all.
     *
     * @return true, if is search all
     */
    public boolean isSearchAll() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()));
	}
    
    /**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		
		PmOrgProfile orgProfile = getOrgProfileByLoginUser();
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		
		getManagedBean().setSearchMinistryProfile(ministryProfile);
		getManagedBean().setSearchPtjGroupProfile(ptjGroupProfile);
		getManagedBean().setSearchPtjProfile(ptjProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& !resDTO.getUserOrgList().isEmpty()) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}
	
	/**
	 * 
	 * @param dpObj
	 * @return boolean
	 */
	public boolean checkStatusSqExpired(Object[] dpObj){
		
		Long docId = null;
		String docNo = null;
		Long docStatusId = null;
		String docType = null;
		
		if(dpObj[0] != null){
			docId = (Long) dpObj[0];
		}
		if(dpObj[1] != null){
			docNo = (String) dpObj[1];
		}
		if(dpObj[4] != null){
			docStatusId = (Long) dpObj[4];
		}
		if(dpObj[3] != null){
			docType = (String) dpObj[3];
		}
			
 	    QuoteResDTO quoteResDTO = null;
	    Long currentStatusId = docStatusId;
	    Boolean sqExpired = false;
	    
	    if(docType.equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.SQ_PENDING_RN_CREATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
			}								   
	    }else if(docType.equals(ParameterConst.DOCTYPE_REQUEST_NOTE)){
	    	if(currentStatusId.equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_APPROVAL)
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION) 
	    	   || currentStatusId.equals(DPStatusConstants.RN_PENDING_REVISION_APPROVAL)
	    	   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}  	
	    }else if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
	    	if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
	 		   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
			   || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}
	    }else if(docType.equals(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
	    	if(currentStatusId.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
			   || currentStatusId.equals(QTStatusConst.LOA_PENDING_APPROVAL)
		       || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION)
		       || currentStatusId.equals(QTStatusConst.LOA_PENDING_REVISION_APPROVAL)){
	    		IQuoteSO quoteSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	    		quoteResDTO = quoteSO.getQuoteInfoFromOtherDocIdDocNo(docId, docNo, docStatusId, docType, false);	
	    		if(quoteResDTO.getEndDate() != null && quoteResDTO.getPid() == null){
	    			if(!isSqStillValid(quoteResDTO.getEndDate())){
	    				sqExpired = true;
				    }
	    		}
	    	}
	    }
	    
		return sqExpired;
	}
	
	/**
	 * Checks if the SQ (Service Quote) is still valid based on its end date and a configured validity period.
	 *
	 * @param sqEndDate The end date of the Service Quote.
	 * @return {@code true} if the SQ is still valid, {@code false} otherwise.
	 */
	public boolean isSqStillValid(Date sqEndDate){		
		
		Boolean validDate = true;
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();
		
		if(sqEndDate != null){
				Date currentDate = new Date();
				Date closingDate = sqEndDate;
					
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
					
				if(currentDate.compareTo(cal.getTime()) >= 0){
						validDate = false;
				}							
			}						
			return validDate;	
	}
	
	/**
	 * Retrieves an array of SelectItem objects representing the available function types.
	 * These types are based on predefined organization types (PTJ and G2G_ROLECODE).
	 *
	 * @return An array of {@link SelectItem} containing the function types.
	 */
	public SelectItem[] getFunctionType() {

		List<String> typeList = new ArrayList<>();
		typeList.add(ParameterConst.ORG_TYPE_PTJ);
		typeList.add(ParameterConst.ORG_TYPE_G2G_ROLECODE);

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), typeList));

		return super.getDropdownValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);

	}

	/**
	 * Compare with selected request note details and add into the request note details selected list
	 * 
	 * @param tempRnDtls
	 *			  Temporary store all the selected request note details for matching.
	 * @param purchaseRequestId
	 *            The ID of the purchase request to match with id in request note details.
	 * @return list of selected requestNoteDtls           
	 */
	public List<ScRequestNoteDtl> addSelectedRequestNoteDtls(List<ScRequestNoteDtl> tempRnDtls, Long purchaseRequestId){
		List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<>();
		for(ScRequestNoteDtl rnDtl : tempRnDtls){
			if(purchaseRequestId.compareTo(rnDtl.getPurchaseRequest().getPurchaseRequestId()) == 0){
				requestNoteDtls.add(rnDtl);
			}
		}
		return requestNoteDtls;
	}
}
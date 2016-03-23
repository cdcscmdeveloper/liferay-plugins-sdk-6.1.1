/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.accessappl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.ProfileManagementStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.pm.profiledata.PMProfileDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.persistence.entity.pm.common.PmAccessApplDoc;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.pm.accessappl.AccessApplManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;


/**
 * The backing bean for Access Appl
 * 
 * @author Ken
 * @since 21.12.2015
 */
@ManagedBean(name = "accessApplBackingBean")
@ViewScoped
public class AccessApplBackingBean extends
	BaseBackingBean<AccessApplManagedBean>{

	/** The log. */
	private static Logger log = Logger
			.getLogger(AccessApplBackingBean.class);
	
	private static final String ACCESS_APPL_MAIN_PAGE ="/xhtml/pm/accessappl/accessApplicationMain.xhtml";
	
	/**
	 * Pre Loading
	 * 	 
	 * */
	public void preLoading() {
		log.info("preLoading");
		renewSesionObject();
		initialAccessApplication();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ACCESS_APPL_MAIN_PAGE);
	}
	
	/**
	 * Pre render View
	 * 	 
	 * */
	public void preRenderView() {
		log.info("preRenderView");
		renewSesionObject();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if(taskId == null){
			return;
		}
		Object obj = super.getBpmTaskDetails(Object.class, null);
		this.getManagedBean().setTaskId(taskId);
		// Procurement Plan
		PMProfileDataType pmObj = (PMProfileDataType) obj;
		
		IAccessApplSO accessApplSO = null;
		try{
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			reqDTO.setAccessApplId(Long.valueOf(pmObj.getProfileId()));
			AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplById(reqDTO);
			if(resDTO.isSuccessful()){
				PmAccessAppl accessAppl = resDTO.getAccessAppl();
				accessAppl.setCurrentStatus(getManagedBean().getCurrentStatusId().intValue());
				getManagedBean().setAccessAppl(accessAppl);
				if(accessAppl.getAccessApplDocs() != null){
					getManagedBean().setAccessApplDocList(accessAppl.getAccessApplDocs());
				}
				if(accessAppl.getOrgProfile() !=null){
					getManagedBean().setSelectedOrgProfile(accessAppl.getOrgProfile());
					getManagedBean().setSelectedOrgType(accessAppl.getOrgProfile().getOrgTypeCode().getParameterId());
				}			
				
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally{
			accessApplSO = null;
		}
		this.getManagedBean().setShowTrackingDairy(false);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ACCESS_APPL_MAIN_PAGE);
	}
	
	public void redirectToAccessApplView(Long accessApplId, Integer statusId) {
		log.info("redirectToAccessApplView");
		renewSesionObject();
		IAccessApplSO accessApplSO = null;
		try{
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			reqDTO.setAccessApplId(accessApplId);
			AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplById(reqDTO);
			if(resDTO.isSuccessful()){
				PmAccessAppl accessAppl = resDTO.getAccessAppl();
				accessAppl.setCurrentStatus(statusId != null ? statusId : 0);
				if(accessAppl.isMarketResearch()){
					accessAppl.setDocType("MR");
				}else if(accessAppl.isCompanyProfile()){
					accessAppl.setDocType("CP");
				}
				getManagedBean().setAccessAppl(accessAppl);
				if(accessAppl.getAccessApplDocs() != null){
					getManagedBean().setAccessApplDocList(accessAppl.getAccessApplDocs());
				}
				if(accessAppl.getOrgProfile() !=null){
					getManagedBean().setSelectedOrgProfile(accessAppl.getOrgProfile());
					getManagedBean().setSelectedOrgType(accessAppl.getOrgProfile().getOrgTypeCode().getParameterId());
				}			
				
			}
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}finally{
			accessApplSO = null;
		}
		this.getManagedBean().setShowTrackingDairy(true);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ACCESS_APPL_MAIN_PAGE);
	}
	
	private void initialAccessApplication(){
		PmAccessAppl accessAppl = new PmAccessAppl();
		PmSalutation salutation = new PmSalutation();
		accessAppl.setSalutationCode(salutation);
		accessAppl.setPmCountry(PmCommonUtil.retrieveCountryByCountryCode(
				CommonConstants.DEFAULT_COUNTRY_CODE));
		PmState state = new PmState();
		accessAppl.setPmState(state);
		accessAppl.setPreferedLanguage(getFacescopes().getUserLocale().getLanguage());
		getManagedBean().setAccessAppl(accessAppl);
		this.getManagedBean().setShowTrackingDairy(false);
		if(accessAppl.isBrandNew()){
			retrieveSuppDocList();
		}
	}
	
	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		SelectItem[] items = null;
		if(getManagedBean().getOrgTypeMap().get("somAccessApplOrgType") == null){
			ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
					PmParameterDesc.class, "somAccessApplOrgType");
			
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
			recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES_STATE.value());
			recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());
	
	
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), recommendList));
			if(searchBean != null){
				searchBean.getSortColumns();
				items = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			}			
			getManagedBean().getOrgTypeMap().put("somAccessApplOrgType", items);
		}else{
			items = getManagedBean().getOrgTypeMap().get("somAccessApplOrgType");
		}
		
		return items;
	}
	
	
	
	/**
	 * retrieve salutation list
	 * 
	 * @return List<PmSalutation>
	 */
	public List<PmSalutation> salutationOptions() {
		log.info("salutationOptions");
		return PmCommonUtil.retrievePmSalutationList();
	}
	
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getSelectedOrgType());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	public void changeOrgUnit(BaseEntity entity) {
		log.info("changeOrgUnit");
	}
	
	public void changeOrgType() {
		log.info("changeResOrgType");

		if (this.getManagedBean().getSelectedOrgProfile() != null) {
			getManagedBean().setSelectedOrgProfile(null);
		}

	}
	
	public List<PmState> getStateList() {
		log.info("getStateList");
		Long countryId = getManagedBean().getAccessAppl().getPmCountry().getCountryId();
		List<PmState> resultList = PmCommonUtil.retrievePmStateList(countryId);
		return resultList;
	}
	
	public void retrievePmState(PmState pmState) {
		log.info("retrievePmState");
		if(pmState != null ){
			if (pmState.getStateId() != null) {
				log.info("pmState.getStateId() " + pmState.getStateId());
				pmState = PmCommonUtil.retrievePmStateByStateId(pmState
						.getStateId());				
				getManagedBean().getAccessAppl().setPmState(pmState);
				getManagedBean().getAccessAppl().setPmDistrict(new PmDistrict());
				getManagedBean().getAccessAppl().setPmCity(new PmCity());
				
			} else {
				pmState.setStateCode(null);
			}
		}
	}
	
	/**
	 * get district list by state id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		log.info("getDistrictList");
		return PmCommonUtil.retrievePmDistrictList(stateId);
	}

	/**
	 * get city list by district id
	 * 
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		log.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}
	
	
	public void retrievePmDistrict(PmDistrict pmDistrict) {	
		if(pmDistrict != null && pmDistrict.getDistrictId() != null){
			PmDistrict tempDistrict = PmCommonUtil.retrievePmDistById(pmDistrict.getDistrictId());				
			getManagedBean().getAccessAppl().setPmDistrict(tempDistrict);			
		}
		getManagedBean().getAccessAppl().setPmCity(new PmCity());		
	}
	
	public void retrievePmCity(PmCity pmCity) {
		if(pmCity != null && pmCity.getCityId() !=null){
			PmCity tempCity = PmCommonUtil.retrievePmCityById(pmCity.getCityId());			
			getManagedBean().getAccessAppl().setPmCity(tempCity);
		}
	}
	
	public String saveAccessApplication(String outcome) throws Exception {
		log.info("saveAccessApplication");
		IAccessApplSO accessApplSO = null;		
		try{
			PmAccessAppl accessAppl = getManagedBean().getAccessAppl();
			Integer currentStatus = accessAppl.getCurrentStatus();
			accessAppl.setOrgProfile(getManagedBean().getSelectedOrgProfile());			
			validateAccessAppl(outcome);
			accessAppl.setOrgTypeCode(getManagedBean().getSelectedOrgProfile().getOrgTypeCode());
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			reqDTO.setAccessAppl(accessAppl);
			reqDTO.setAccessApplDocList(getManagedBean().getAccessApplDocList());
			PmUser user =  PmCommonUtil.retrieveUserByIdNo(accessAppl.getIdentificationNo());
			if(user == null){
				reqDTO.setPortalBaseUrl(PmCommonUtil.getBasePortalUrl());
			}
			
			if(!accessAppl.isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setToken(getFacescopes().getBPMAuthenticationCtxToken());
				if(ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.longValue()
						== accessAppl.getCurrentStatus().longValue()){
					accessAppl.setMrRemark(getManagedBean().getRemarks());
				}else if(ProfileManagementStatusConst.PENDING_COMPANY_ACCESS_APPROVAL.longValue()
						== accessAppl.getCurrentStatus().longValue()){
					accessAppl.setCpRemark(getManagedBean().getRemarks());
				}
				reqDTO.setActionedBy(getFacescopes().getUserSessionBean());
				reqDTO.setRoleCode(getUserRoleValue());
			}
			
			reqDTO.setOutcome(outcome);			
			AccessApplResDTO resDTO = accessApplSO.saveAccessAppl(reqDTO);
			if(resDTO.isSuccessful()){
				if(accessAppl.isBrandNew()){
					uploadAttachments();
				}
				
				String messageId = null;
				PmAccessAppl savedAccessAppl = resDTO.getAccessAppl();
				if(BpmOutcomeConst.BPM_SAVE.equals(outcome)){
					if(savedAccessAppl.isCompanyProfile() && savedAccessAppl.isMarketResearch()){
						messageId = MessageConst.INFO_730033;
					}else if(savedAccessAppl.isCompanyProfile() ){
						messageId = MessageConst.INFO_730031;
					}else if(savedAccessAppl.isMarketResearch()){
						messageId = MessageConst.INFO_730032;
					}
				}else if(BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
					if(ProfileManagementStatusConst.PENDING_COMPANY_ACCESS_APPROVAL.equals(currentStatus.longValue())){
						messageId = MessageConst.INFO_730035;
					}else if(ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.equals(currentStatus.longValue())){
						messageId = MessageConst.INFO_730037;
					}
					
				}else if(BpmOutcomeConst.BPM_REJECT.equals(outcome)){
					if(ProfileManagementStatusConst.PENDING_COMPANY_ACCESS_APPROVAL.equals(currentStatus.longValue())){
						messageId = MessageConst.INFO_730036;
					}else if(ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.equals(currentStatus.longValue())){
						messageId = MessageConst.INFO_730038;
					}
				}
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{savedAccessAppl.getApplNo() }, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			accessApplSO = null;
		}
		
		return null;
	}
	
	private void retrieveSuppDocList(){
		IDocumentSO docSO = null;
		try{
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			reqDTO.setDocumentSearchBean(new SearchBean<PmDocument>());
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_ACCESS_APPLICATION_DOC);			
			docSO = (IDocumentSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDocumentSO.class);
			DocumentResDTO resDTO = docSO.retrieveDocumentList(reqDTO);
			if(resDTO.isSuccessful() && resDTO.getDocumentList() !=null){
				List<PmAccessApplDoc> accessApplDocList = new ArrayList<PmAccessApplDoc>();
				PmAccessApplDoc applDoc = null;
				for(PmDocument doc : resDTO.getDocumentList()){
					applDoc = new PmAccessApplDoc();
					applDoc.setDocument(doc);
					accessApplDocList.add(applDoc);
				}
				getManagedBean().setAccessApplDocList(accessApplDocList);
			}
		}finally{
			 docSO = null;
		}
	}
	
	public void assignSelectedApplDoc(PmAccessApplDoc doc) {
		getManagedBean().setSelectedPmAccessApplDoc(doc);
	}
	
//	public void handleAccessApplDocFileAttachment(FileUploadEvent event) {
//		if (event.getFile() != null) {
//			if (getManagedBean().getSelectedPmAccessApplDoc() != null) {
//
//				// construct file path
//				String filePath = PmCommonUtil
//						.getUploadFilePath(ProfileManagementConst.ACCESS_APPL_FILE_PATH)
//						+ getManagedBean().getAccessAppl().getIdentificationNo()
//						+ "/"
//						+ System.currentTimeMillis() + "/";
//				
//				for(PmAccessApplDoc applDoc : getManagedBean().getAccessApplDocList()){
//					if(applDoc.getDocument().getDocumentId().equals(getManagedBean().getSelectedPmAccessApplDoc().getDocument().getDocumentId())){
//						applDoc.setFileName(StringUtils.replace(event.getFile()
//								.getFileName(), " ", "-"));
//						applDoc.setFilePath(filePath);
//					}
//				}
//
//				// upload file
//				commonUploadFile(event, filePath);
//			}
//		}
//
//	}
	
	public void deleteAttachmentAction(){
		if (getManagedBean().getSelectedPmAccessApplDoc() != null) {
			for(PmAccessApplDoc applDoc : getManagedBean().getAccessApplDocList()){
				if(applDoc.getDocument().getDocumentId().equals(getManagedBean().getSelectedPmAccessApplDoc().getDocument().getDocumentId())){
					applDoc.setFileName(null);
					applDoc.setFilePath(null);
					break;
				}
			}
		}
	}
	
	private void validateAccessAppl(String outcome){
		PmAccessAppl accessAppl = getManagedBean().getAccessAppl();
		
		if(accessAppl.getOrgProfile() == null ){
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.pm.access.app.org",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())});
		}
		
		for(PmAccessApplDoc accessApplDoc : getManagedBean().getAccessApplDocList()){
			if(StringUtils.isBlank(accessApplDoc.getFileName()) || StringUtils.isBlank(accessApplDoc.getFilePath())){
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { accessApplDoc.getDocument().getDocName()});
			}
		}
		
		if(accessAppl.isBrandNew() && !accessAppl.getEmail().equals(accessAppl.getConfirmEmail())){
			throw new EpException(MessageConst.ERROR_721020);
		}
		
		if(!accessAppl.isMarketResearch() 
				&& !accessAppl.isCompanyProfile()){
			throw new EpException(MessageConst.ERROR_100050,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.pm.access.app.request.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}
		
		if(accessAppl.isBrandNew()){
			IAccessApplSO accessApplSO = null;
			try{
				accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAccessApplSO.class);
				
				AccessApplReqDTO reqDTO = new AccessApplReqDTO();
				reqDTO.setIdentificationNo(accessAppl.getIdentificationNo());
				AccessApplResDTO resDTO = accessApplSO.isAccessApplProccessingByIdNo(reqDTO);
				if(resDTO.isSuccessful() && !resDTO.isValid()){
					throw new EpException(MessageConst.ERROR_730034);
				}
			}catch(Exception e){
				throw new EpException(e.getMessage());
			}finally{
				accessApplSO = null;
			}
		}
		
		PmUser user = PmCommonUtil.retrieveUserByIdNo(accessAppl.getIdentificationNo());
		if(user != null){
			PmUserOrg currentUserOrg = null;
			for(PmUserOrg userOrg : user.getPmUserOrgs()){
				if(DbValueConst.RECORD_STATUS_ACTIVE == userOrg.getRecordStatusFlg().intValue()){
					currentUserOrg = userOrg;
					break;
				}
			}
			
			if(currentUserOrg != null){
				if(!currentUserOrg.getPmOrgProfile().getOrgProfileId()
						.equals(accessAppl.getOrgProfile().getOrgProfileId())){
					throw new EpException(MessageConst.ERROR_770217,
							new String[] {currentUserOrg.getPmOrgProfile().getOrgName()});
				}else{
					boolean hasMrRole = false;
					boolean hasCpRole = false;
					for(PmUserRole userRole : currentUserOrg.getPmUserRoles()){
						if(DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg().intValue()){
							if(RoleCode.MARKET_RESEARCH.value().equals(userRole.getPmRole().getRoleCode())){
								hasMrRole = true;
							}else if(RoleCode.COMPANY_PROFILE.value().equals(userRole.getPmRole().getRoleCode())){
								hasCpRole = true;
							}
						}
					}
					
					if(accessAppl.isCompanyProfile() && hasCpRole && accessAppl.isMarketResearch() && hasMrRole){						
						throw new EpException(MessageConst.ERROR_770216,
								new String[] {ResourceBundleUtils
								.getMessage("lbl.pm.access.app.cp.and.mr",
										new String[] {},
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()),
										PmCommonUtil.getBasePortalUrl()});
					}else if(accessAppl.isCompanyProfile() && hasCpRole){
						throw new EpException(MessageConst.ERROR_770216,
								new String[] {ResourceBundleUtils
								.getMessage("lbl.pm.access.app.company.profile",
										new String[] {},
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()),
										PmCommonUtil.getBasePortalUrl()});
					}else if(accessAppl.isMarketResearch() && hasMrRole){
						throw new EpException(MessageConst.ERROR_770216,
								new String[] {ResourceBundleUtils
								.getMessage("lbl.pm.access.app.market.research",
										new String[] {},
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()),
										PmCommonUtil.getBasePortalUrl()});
					}
				}
			}
		}
		
		if(BpmOutcomeConst.BPM_REJECT.equals(outcome) && !accessAppl.isBrandNew() && StringUtils.isBlank(getManagedBean().getRemarks())){
			throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils
					.getMessage("lbl.pm.access.app.remarks",
							new String[] {},
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())});
		}
	}
	
	private FacesScopes getFacescopes(){
		return new FacesScopes();
	}
	
	/**
	 * Download attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void downloadAttachment(PmAccessApplDoc attachment)
			throws FileNotFoundException, IOException {
		log.info("downloadAttachment - START");
		try {
			if (attachment != null) {
				downloadFile(attachment.getFilePath(), attachment.getFileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770055,
					new String[] { e.getMessage() });

		}
		log.info("downloadAttachment - END");
	}

	/**
	 * Download attachment from server
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	private void downloadFile(String filePath, String fileName)
			throws IOException {
		log.info("Inside downloadFile start");
		log.info(" file path : "+filePath);
		log.info(" file name : "+fileName);
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (filePath != null) {
			log.info(">>>>filePath != null");
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				log.info(e.getMessage());
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
			input.close();
		}
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
		log.info("Inside downloadFile end");
	}
	
	/**
	 * Upload files
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void uploadAttachments() throws FileNotFoundException, IOException {
		log.info("uploadAttachments - START");
		try {
			if (this.getManagedBean().getFileEventMapper() != null
					&& !this.getManagedBean().getFileEventMapper().isEmpty()) {
				PmAccessApplDoc tempPmUserAttachment = null;
				FileUploadEvent tempFileUploadEvent = null;
				for (Map.Entry<PmAccessApplDoc, FileUploadEvent> entry : this
						.getManagedBean().getFileEventMapper().entrySet()) {
					tempPmUserAttachment = entry.getKey(); // PmUserAttachment
					tempFileUploadEvent = entry.getValue(); // FileUploadEvent
					PmCommonUtil.commonUploadFile(tempFileUploadEvent,
							PmCommonUtil.getAttachmentPath(tempPmUserAttachment
									.getFilePath()));
				}
				this.getManagedBean().getFileEventMapper().clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060,
					new String[] { e.getMessage() });

		}
		log.info("uploadAttachments - END");
	}
	
	/**
	 * Add attachment to list
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.info("handleFileUpload - START");
		try {
			if (event != null) {
				if (this.getManagedBean().getFileEventMapper() == null) {
					this.getManagedBean().setFileEventMapper(
							new HashMap<PmAccessApplDoc, FileUploadEvent>());
				}
				String loginId = getManagedBean().getAccessAppl().getIdentificationNo() != null 
						? getManagedBean().getAccessAppl().getIdentificationNo() 
								: "newApplication";
						
				// construct file path
						
				String filePath = 
						PmCommonUtil.getUploadFilePath(ProfileManagementConst.ACCESS_APPL_FILE_PATH,loginId,event.getFile().getFileName());
						
				
				PmAccessApplDoc accessApplDoc = null;
				for(PmAccessApplDoc applDoc : getManagedBean().getAccessApplDocList()){
					if(applDoc.getDocument().getDocumentId().equals(getManagedBean().getSelectedPmAccessApplDoc().getDocument().getDocumentId())){
						applDoc.setFileName(StringUtils.replace(event.getFile()
								.getFileName(), " ", "-"));
						applDoc.setFilePath(filePath);
						accessApplDoc = applDoc;
						break;
					}
				}				
				if(accessApplDoc != null){
					this.getManagedBean().getFileEventMapper()
						.put(accessApplDoc, event);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060,
					new String[] { e.getMessage() });

		}
		log.info("handleFileUpload - END");
	}
	
	/**
	 * Delete attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachment()
			throws FileNotFoundException, IOException {
		log.info("deleteAttachment - START");
		try {
			if (getManagedBean().getSelectedPmAccessApplDoc() != null) {
				for(PmAccessApplDoc applDoc : getManagedBean().getAccessApplDocList()){
					if(applDoc.getDocument().getDocumentId().equals(getManagedBean().getSelectedPmAccessApplDoc().getDocument().getDocumentId())){
						applDoc.setFileName(null);
						applDoc.setFilePath(null);
						break;
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054,
					new String[] { e.getMessage() });

		}
		log.info("deleteAttachment - END");
	}
	
	private static String getUserRoleValue() {
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			return RoleCode.UPS_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.EP_ADMIN_2.value())) {
			return RoleCode.EP_ADMIN_2.value();
		} 
		return null;
	}
}

/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.loiloa;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmTerm;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.loaloi.ScLoiWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.type.sc.TermType;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.loiloa.LOIDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends SourcingBaseBackingBean<LOIDetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOIDetailBackingBean.class);
	
	
	public static final String LOI_DETAILS_MAIN_MENU = "/xhtml/sc/loi/loiDetailsMain.xhtml.xhtml";
	public static final String LOI_DETAILS_MENU = "/xhtml/sc/loi/loiDetailsMenu.xhtml";
	public static final String LOI_AWARD_DETAIL_MENU = "/xhtml/sc/loi/loiAwardDetailsMenu.xhtml";
	public static final String LOI_NEGO_DETAIL_MENU = "/xhtml/sc/loi/loiNegotiationDetailsMenu.xhtml";
	public static final String LOI_APPROVAL_MENU = "/xhtml/sc/loi/loiApprovalMenu.xhtml";
	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");
		populateUserMinistryList();
		initializeRemarks(new ScLoi());
		getApproverNames();
	}
	
	public void preRenderLoiListView() {
		debugLogger("preRenderLoiListView");
		
		try {
			/*SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			
			getManagedBean().setSourceId(sourceId);*/
			
			Long sourceId = 1450L;
			
			retrieveLOIList(sourceId);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);	
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loiId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);

			// Long loiId = 299L;

			ScLoi scLoi = new ScLoi();
			scLoi.setLoiId(loiId);
			//editLOIAction(scLoi);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");

		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loiId = Long.valueOf(dpDataType.getAwardId());
			
			getManagedBean().setSourceId(sourceId);

			//editSupplierLOIAction(loiId);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.SUPPLIER_LOI_PAGE);

		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public void retrieveLOIList(Long qtId) {
		debugLogger("retrieveLOIList");
		IQTAwardSO qtAwardSO = null;
		try {

			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(qtId);
			reqDTO.setOrgProfileId(32950L);	// TODO - change in SO to remove this.

			AwardSupplierResDTO resDTO = qtAwardSO.retrievePendingLoiList(reqDTO);

			// loi loa list
			if(resDTO.getLoiLoas() != null &&  resDTO.getLoiLoas().size() >0){
				//getManagedBean().setLoiLoaBeans(prepareLoiLoaList(resDTO.getLoiLoas()));
				reqDTO.setScLoi(resDTO.getLoiLoas().get(0).getScLois().get(0));
			}

			getManagedBean().setLoiLoas(resDTO.getLoiLoas());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());

			// rec supplier in loi list
			getManagedBean().setRecSupplierList(resDTO.getPendingLoiLoas());

			// status
			getCurrentStatus(DPStatusConstants.LOI_PENDING_SUBMISSION);

			populateUserMinistryList();
			initializeRemarks(new ScLoi());
			getApproverNames();

		} catch (Exception ex) {																		
			handleException(ex);
		}
	}

	private List<LoiLoaBean> prepareLoiLoaList(List<ScLoiLoa>  loiloas){
		List<LoiLoaBean>  loiloaBeans  = new ArrayList<LoiLoaBean>();
		if(loiloas != null && loiloas.size()>0){
			for (ScLoiLoa scLoiLoa : loiloas) {

				if(scLoiLoa.getScloas() != null && scLoiLoa.getScloas().size()>0){
					for (ScLoa scLoa : scLoiLoa.getScloas()) {
						LoiLoaBean loiLoaBean  = new LoiLoaBean();
						loiLoaBean.setScLoa(scLoa);
						loiLoaBean.setLoiLoaType("LOA");
						loiLoaBean.setLoiLoaStatus(getLoiLoaStatus(null ,scLoa.getScLoaWorkFlowStatuses()));
						loiloaBeans.add(loiLoaBean);
					}
				}

				if(scLoiLoa.getScLois() != null && scLoiLoa.getScLois().size()>0){
					for (ScLoi scLoi : scLoiLoa.getScLois()) {
						LoiLoaBean loiLoaBean  = new LoiLoaBean();
						loiLoaBean.setScLoi(scLoi);
						loiLoaBean.setLoiLoaType("LOI");
						loiLoaBean.setLoiLoaStatus(getLoiLoaStatus(scLoi.getScLoiWorkFlowStatuses() ,null));
						loiloaBeans.add(loiLoaBean);
					}
				}	
			}
		}
		return loiloaBeans;
	}

	public void onRowRecSupplierAction(SelectEvent event) {
		IQTAwardSO qtAwardSO = null;
		ScLoiLoa scLoiLoa = (ScLoiLoa) event.getObject();
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setSupplierId(scLoiLoa.getSmSupplier().getSupplierId());
			reqDTO.setDocId(scLoiLoa.getDocId());
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);
			
			if(resDTO.getLoiLoas() != null && resDTO.getLoiLoas().size()>0){
				getManagedBean().setLoiLoaBeans(prepareLoiLoaList(resDTO.getLoiLoas()));
			}
		} catch (Exception ex) {																		
			handleException(ex);
		}
	}

	/**
	 * to get ptj drop down Listing
	 */
	private void populateUserMinistryList() {
		  // to get ptj
		  PmUser user = new FacesScopes().getUserSessionBean();
		  if (user != null) {
			  IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			  UserReqDTO reqDTO = new UserReqDTO();
			  reqDTO.setUserId(user.getUserId());
			  UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			  if(resDTO.getUserOrgList() != null && resDTO.getUserOrgList().size()>0){
				  getManagedBean().setPmOrgProfile(resDTO.getUserOrgList() .get(0).getPmOrgProfile());
			  }
		  }
	}
	
	public void tabMenuListener(Integer selectedTabIndex) {
		debugLogger("tabMenuListener");
		if(selectedTabIndex.equals(0)){
			getManagedBean().setSelectedTabIndex(0);

		} else if(selectedTabIndex.equals(1)){
			getManagedBean().setSelectedTabIndex(1);
		} else if(selectedTabIndex.equals(2)){
			getManagedBean().setSelectedTabIndex(2);
		} else if(selectedTabIndex.equals(3)){
			getManagedBean().setSelectedTabIndex(3);

		}
	}

   
	/**
	 * Initializing remarks in loi approval
	 */
	public List<ScLoiRemark> initializeRemarks(ScLoi scLoi) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		FacesScopes facesScopes = new FacesScopes();
		List<ScLoiRemark> results =   scLoi.getScLoiRemarks();
		if(results == null){
			results = new ArrayList<ScLoiRemark>();
		} 
		if (results.size() == 0
				|| !compareUserName(results.get(0).getCreatedUser().getLoginId())) {
			ScLoiRemark remark = new ScLoiRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			remark.setRemark("");
			PmStatus status = new PmStatus();
			status.setStatusId(DPStatusConstants.PRI_PENDING_SUBMISSION);
		    StatusReqDTO statusReqDTO = new StatusReqDTO();
		    if (status != null) {
		     statusReqDTO.setStatusId(status.getStatusId());
		     StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
		     remark.setPmStatus(statusResDTO.getPmStatus());
		    }
		    remark.setPmStatus(status);
		   
		    results.add(0, remark);
		}
		//getManagedBean().setRemarks(results);
		return results;
	}
	
	/**
	 * viewItemDetails method to view item details  in loi listing
	 */
	public void viewItemDetails(LoiLoaBean bean) {

		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		reqDTO = new AwardSupplierReqDTO();
		
		if(bean.getScLoi() != null){
			reqDTO.setScLoi(bean.getScLoi());
		}
		if(bean.getScLoa() != null){
			reqDTO.setScLoa(bean.getScLoa());
		}
		
		AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);

		if(resDTO.getScLoi() != null){
			getManagedBean().setScLoi(resDTO.getScLoi());
		}
	}

	/**
	 * getting Approver drop down listing
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();

		// to get Approver
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(user.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

			IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			//userReqDto.setOrgProfileId(resDTO.getUserOrgList() .get(0).getPmOrgProfile().getOrgProfileId());

			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			getManagedBean().setApproverNames(userResDTO.getUserList());

		}
		return approverUsers;
	}

	/**
	 * To get status for loi
	 */
	public void  getCurrentStatus(Long statusId) {
		if (statusId != null) {
			getManagedBean().setStatus(getStatusDescById(statusId));
		}
	} 



	private String  getLoiLoaStatus(List<ScLoiWorkFlowStatus> loiStatus , List<ScLoaWorkFlowStatus> loaStatus) {
		String loiLoaStatus = "";
		if (loiStatus != null && loiStatus.size()>0) {
			loiLoaStatus = getStatusDescById( 
					loiStatus.get(0).getPmStatus().getStatusId());
		}
		if (loaStatus != null && loaStatus.size()>0) {
			loiLoaStatus = getStatusDescById(loaStatus.get(0).getPmStatus().getStatusId());
		}
		return loiLoaStatus;
	} 


	/**
	 * Page navigation to loi loa list
	 */
	public void backToPervious() {
		log.info("backToPervious");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);  
	}


	/*
	 * public List<SelectItem> getApproverNames() {
	 * debugLogger("getApproverNames"); List<SelectItem> selectItems = new
	 * ArrayList<SelectItem>();
	 * 
	 * try { IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class); List<PmUser> comitteeUsersUsers = new
	 * ArrayList<PmUser>(); if (getManagedBean().getComitteeUsers() == null) {
	 * UserReqDTO reqDTO = new UserReqDTO();
	 * reqDTO.setRoleCode(RoleCode.APPROVER.value()); UserResDTO resDTO =
	 * userSO.retrieveUserListByRole(reqDTO); comitteeUsersUsers =
	 * resDTO.getUserList(); } selectItems.add(new SelectItem(null,
	 * "--Select One--")); for (int i = 0; i < comitteeUsersUsers.size(); i++) {
	 * PmUser user = (PmUser) comitteeUsersUsers.get(i); selectItems.add(new
	 * SelectItem(user.getUserId(), user .getUserName())); }
	 * this.getManagedBean().setComitteeUsers(selectItems); } catch (Exception
	 * e) { e.printStackTrace(); } return getManagedBean().getComitteeUsers(); }
	 */

	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScLoiNegoLocation negoLocation = new ScLoiNegoLocation();

		List<ScLoiNegoLocation> negoLocationList = getManagedBean().getNegoLocationList();
		if (null == negoLocationList) {
			negoLocationList = new ArrayList<ScLoiNegoLocation>();
		}
		negoLocation.setScLoi(getManagedBean().getScLoi());
		negoLocationList.add(negoLocation);
		getManagedBean().getScLoi().setScNegoLocations(negoLocationList);
		this.getManagedBean().setNegoLocationList(negoLocationList);
	}

	public void addNegotiationTerm() {
		debugLogger("addNegotiationTerm");
		ScLoiTerm negoTerm = new ScLoiTerm();
		List<ScLoiTerm> scNegoTerms = getManagedBean().getScLoi().getScNegoTerms();
		if(null == scNegoTerms){
			scNegoTerms =   new ArrayList<ScLoiTerm>();
		}
		negoTerm.setPmTerm(new PmTerm());
		negoTerm.setSelected(Boolean.TRUE);
		negoTerm.setScLoi(getManagedBean().getScLoi());
		scNegoTerms.add(negoTerm);

		getManagedBean().getScLoi().setScNegoTerms(scNegoTerms);
		//this.getManagedBean().setNegoTermsList(scNegoTerms);
	}

	public void populatePreDefinedDetails() {
		debugLogger("populatePreDefinedDetails");
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String languageCode = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setTermType(TermType.TERM_LOI.value());
			reqDTO.setLanguageCode(languageCode);

			/*
			 * AwardSupplierResDTO resDTO = qtAwardSO
			 * .retrieveTermDescListByTermType(reqDTO);
			 * 
			 * List<ScTermDesc> list = resDTO.getTermDescResultBean()
			 * .getResultList();
			 * 
			 * getManagedBean().setScTermDescList(list);
			 */

		} finally {
			qtAwardSO = null;
		}
	}

	public void addPredefinedTerms() {
		debugLogger("addPredefinedTerms");

		ScLoiTerm scNegoTerm = new ScLoiTerm();
		//ScTerm scTerm = new ScTerm();
		List<ScLoiTerm> list = getManagedBean().getNegoTermsList();

		if (list == null) {
			list = new ArrayList<ScLoiTerm>();
		}
		/*
		 * List<ScTermDesc> scTermDescList = getManagedBean()
		 * .getSelectedCheckDetails(); scTerm.setScTermDescs(scTermDescList);
		 * //scNegoTerm.setScTerm(scTerm); System.out.println("Description : " +
		 * scNegoTerm.getScTerm().getScTermDescs().get(0).getTermDesc());
		 */
		scNegoTerm.setScLoi(getManagedBean().getScLoi());
		list.add(scNegoTerm);
		getManagedBean().setNegoTermsList(list);

	}

	public void deleteNegoTerm() { 
		List<ScLoiTerm> selectedNegoTermsList  = getManagedBean().getSelectedNegoTermsList();
		if(selectedNegoTermsList != null){

			for (ScLoiTerm scLoiTerm : selectedNegoTermsList) {
				if(scLoiTerm.isBrandNew()){
					getManagedBean().getScLoi().getScNegoTerms().remove(scLoiTerm);
				} else{
					//throw new EpException("can't delete "+ scLoiTerm.getPmTerm().getTermDesc());
				}
			}
		} else{
			throw new EpException("Please select At least one Item");
		}
	}

	public void deleteNegoLocations() { 
		List<ScLoiNegoLocation> selectedNegoLocs  = getManagedBean().getSelectedNegoLocationList();
		if(selectedNegoLocs != null){

			for (ScLoiNegoLocation scLoiNegoLocation : selectedNegoLocs) {
				getManagedBean().getScLoi().getScNegoLocations().remove(scLoiNegoLocation);
			}
		} else{
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of department.
	 * */
	public void handleMinistryProfileChanged() {
		debugLogger("handleMinistryProfileChanged");

		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getMinistryOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();

			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = BusinessDelegate
						.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				this.getManagedBean().setUserDepartmentList(
						resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setUserDepartmentList(
					new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getQt().getDepartmentOrgProfile()
		.setOrgProfileId(null);

		handleDepartmentProfileChanged();
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of division.
	 */
	public void handleDepartmentProfileChanged() {
		debugLogger("handleDepartmentProfileChanged");

		Long parentOrgProfileId = this.getManagedBean().getQt()
				.getDepartmentOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();

			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = BusinessDelegate
						.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				/*this.getManagedBean().setUserDivisionList(
						resDTO.getOrgProfileList());*/
			}
		} else {
			/*this.getManagedBean().setUserDivisionList(
					new ArrayList<PmOrgProfile>());*/
		}

		this.getManagedBean().getQt().getPmOrgProfile().setOrgProfileId(null);
	}

	public void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile parentOrgProfile = scQt.getPmOrgProfile()
				.getParentOrgProfile();

		while (parentOrgProfile != null) {
			// Ministry
			if (OrganizationType.MINISTRY.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getOrgTypeCode().getParameterCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}

	public String createLOIAction(ScQtEvaluationResult scQtEvaluationResult,
			String docType) {
		debugLogger("createLOIAction");
		IQTAwardSO iqtAwardSO = null;
		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
		try {

			long id = scQtEvaluationResult.getEvaluationResultId();

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(id);

			String language = new FacesScopes().getUserLocale().getLanguage();
			reqDTO.setLanguageCode(language);

			docType = reqDTO.getDocType();

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOIDtl(reqDTO);

			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|"
						+ scQt.getQtNo());
			}

			ScLoi scLoi = resDTO.getScLoi();

			if (scLoi != null) {
				System.out.println("Loi Id : " + scLoi.getLoiId());
				scLoi.setLoiNo("12345");
				getManagedBean().setScLoi(scLoi);

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");



				getManagedBean().setQt(scQt);
				getManagedBean().getScLoi().setBrandNew(false);
				populateParentOrgProfile(getManagedBean().getQt());


				/*List<ScQtZoneCriteria> zonelist = scQt.getScQtCriterias()
						.getScQtZoneCriterias();


				List<ScRequestItem> itemList = resDTO.getItemResultBean()
						.getResultList();
				System.out.println("itemlist: " + itemList.size());

				getManagedBean().setScQtItemList(itemList);

				//getManagedBean().setZonelist(zonelist);

			  List<ScLoiNegoLocation> negoLocationList = resDTO
						.getNegoLocResultBean().getResultList();
				System.out.println("list: " + negoLocationList.size());

				List<ScLoiTerm> negoTermList = resDTO.getNegoTermResultBean()
						.getResultList();
				System.out.println("negoTermlist: " + negoTermList.size());

				getManagedBean().setNegoLocationList(negoLocationList);
				getManagedBean().setNegoTermsList(negoTermList);*/

				getManagedBean().setApproverNames(getApproverNames());

			} else {

				Calendar c = Calendar.getInstance();
				System.out.println("Current date : "
						+ (c.get(Calendar.MONTH) + 1) + "-"
						+ c.get(Calendar.DATE) + "-" + c.get(Calendar.YEAR));
				c.add(Calendar.DATE, 14);
				System.out.println("Date (after): "
						+ (c.get(Calendar.MONTH) + 1) + "-"
						+ c.get(Calendar.DATE) + "-" + c.get(Calendar.YEAR));

				Date date = new Date();
				System.out.println("Date : " + date);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DATE, 14);

				date = cal.getTime();
				System.out.println("Date : " + cal.getTime());

				getManagedBean().setQt(scQt);
				/* getManagedBean().setQt(new ScQt()); */

				populateParentOrgProfile(getManagedBean().getQt());

				getManagedBean().setScLoi(new ScLoi());

				getManagedBean().setScQtEvaluationResult(scQtEvaluationResult);

				/*getManagedBean().getScLoi().setSmSupplier(
						getManagedBean().getScQtEvaluationResult()
								.getScQtProposal()
								.getScQtSupplier().getSmSupplier());*/

				/*getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposalEvaluate().getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierName());*/

				/*getManagedBean().getScLoi().setDocType(
						LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
				getManagedBean().getScLoi().setDocId(id);
				getManagedBean().getScLoi().setLoiDuration(14);*/
				getManagedBean().getScLoi().setLoiNo("12345");

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				showOfficerDetails();

				PmUser pmUser = new PmUser();
				//getManagedBean().getScLoi().setApproverUser(pmUser);
				getManagedBean().setApproverNames(getApproverNames());

				/*
				 * getManagedBean().setScQtItemList(null);
				 * getManagedBean().setZonelist(null);
				 */

				getManagedBean().setNegoLocationList(null);
				getManagedBean().setNegoTermsList(null);

				SmSupplierBranch branch = new SmSupplierBranch();
				SmSupplierBranch branch1 = new SmSupplierBranch();
				SmSupplierBranch branch2 = new SmSupplierBranch();
				branch.setBranchName("HQ");
				branch1.setBranchName("Putrajaya");
				branch2.setBranchName("Ampang");
				bList.add(branch);
				bList.add(branch1);
				bList.add(branch2);
				getManagedBean().setBranchsList(bList);

				branch.setSmAppl(getManagedBean().getScQtProposalRanking()
						.getScQtProposalEvaluate().getScQtProposal()
						.getScQtSupplier().getSmSupplier().getSmAppl());

			}

		} finally {
			iqtAwardSO = null;
		}
		return QTConstants.LOI_PAGE;
	}

	public void createLOI(ScLoiLoa scLoiLoa){
		IQTAwardSO qtAwardSO = null;

		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			ScLoi loi = new ScLoi();
			loi.setScLoiLoa(scLoiLoa);
			reqDTO.setScLoi(loi);
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			
			populateLOIDetails(resDTO);			
			
		} catch (Exception ex) {																		
			handleException(ex);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "loiDetailsMain");
	}
	
	
	private void populateLOIDetails(AwardSupplierResDTO resDTO) {
		ScLoi scLoi = resDTO.getScLoi();
		// loi details
		getManagedBean().setScLoi(scLoi);

		// branch list 
		if(resDTO.getSmSupplierBranchs() != null && resDTO.getSmSupplierBranchs().size() >0){
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
		}
		
		// Supplier Branch
		getManagedBean().setBranchName(
				scLoi.getSupplierBranch());
		getManagedBean()
				.setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
		
		if (scLoi.isBrandNew()) {
			handleBranchDetailsChanged();
		}
		
		// Issue & Acknowledge Date
		scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), 14));
		
		// approvers
		if(resDTO.getApproverList() != null && resDTO.getApproverList().size() >0){
			getManagedBean().setApproverNames(resDTO.getApproverList());
		}

		getManagedBean().setScLoiLoaItems(resDTO.getScLoi().getScLoiLoaItems());
		getManagedBean().setScLoi(resDTO.getScLoi());

		// TODO APPPROVER HISTORY
		getManagedBean().getLois().add(resDTO.getScLoi());

		// remarks
		getManagedBean().getScLoi().setScLoiRemarks(initializeRemarks(resDTO.getScLoi()));

		// status
		getCurrentStatus(DPStatusConstants.LOI_PENDING_SUBMISSION);
		
		populateUserMinistryList();
		initializeRemarks(new ScLoi());
		//getApproverNames();
		
		// Contract Signer
		PmUser contractSignerUser = scLoi.getContractSigner();
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}
		getManagedBean().setContractSignerUser(contractSignerUser);
		getManagedBean().setContractSigners(resDTO.getContractSignerList());
		
	}

	/**
	 * 
	 */
	private void showOfficerDetails() {
		IAddressSO addressSO;
		try {
			PmUser user = new FacesScopes().getUserSessionBean();
			getManagedBean().setPmUser(user);
			//getManagedBean().getScLoi().setOfficierName(user.getUserName());
			/*getManagedBean().getScLoi().setOfficerDesignation(
					user.getDesignation());*/

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setUserId(user.getUserId());
			addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);

			List<PmAddressType> addList = addResDTO.getAddressTypeList();

			/*	getManagedBean().getScLoi().setOfficerAddress(
					addList.get(0).getPmAddress().getAddress1());*/
			/*getManagedBean().getScLoi().setOfficerFaxNo(
					addList.get(0).getPmAddress().getFaxNo());
			getManagedBean().getScLoi().setOfficerPhoneNo(
					addList.get(0).getPmAddress().getPhoneNo());*/
			/*	getManagedBean().getScLoi().setOfficerEmail(
					addList.get(0).getPmAddress().getEmail());*/

			// getManagedBean().getScLoi().setApproverUser(user);

		} finally {
			addressSO = null;
		}

	}

	public void handleBranchDetailsChanged() {
		log.info("handleBranchDetailsChanged");
		debugLogger("handleBranchDetailsChanged");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		String selectedSupplierBranchName = this.getManagedBean()
				.getBranchName();

		if (selectedSupplierBranchName == null
				|| "HQ".equals(selectedSupplierBranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl()
					.getSmCompanyBasic();

			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");

					scLoi.setSupplierBranch("HQ");
					scLoi.setSupplierAddress(smCompanyAddress.getSmAddress());
					scLoi.setSupplierContact(supplierHQPersonnel.getName());
					scLoi.setSupplierPhoneNo(smCompanyBasic.getPhoneNo());
					scLoi.setSupplierFaxNo(smCompanyBasic.getFaxNo());
					scLoi.setSupplierMobNo(supplierHQPersonnel.getMobileNo());
					scLoi.setSupplierEmail(supplierHQPersonnel.getEmail());

					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(
						selectedSupplierBranchName)) {
					log.info("found branch address");

					scLoi.setSupplierBranch(supplierBranch.getBranchName());
					scLoi.setSupplierAddress(supplierBranch.getSmAddress());
					scLoi.setSupplierContact(supplierBranch.getSmPersonnel()
							.getName());
					scLoi.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoi.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoi.setSupplierMobNo(supplierBranch.getSmPersonnel()
							.getMobileNo());
					scLoi.setSupplierEmail(supplierBranch.getSmPersonnel()
							.getEmail());

					break;
				}
			}
		}

	}

	/**
	 * Method to save the LOA details
	 */
	public void saveAction() {
		debugLogger("saveAction");

		IStatusSO statusSO = null;
		IUserSO userSO = null;
		IQTAwardSO iqtAwardSO = null;

		try {
			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
			.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoi scLoi = this.getManagedBean().getScLoi();
			scLoi.setIssueDate(new Date());

			// Date date = new Date();
			System.out.println("Date : " + scLoi.getIssueDate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoi.getIssueDate());
			cal.add(Calendar.DATE, 14);
			System.out.println("Date : " + cal.getTime());
			scLoi.setAckDueDate(cal.getTime());
			System.out.println("LOI Response Due Date : "
					+ scLoi.getAckDueDate());


			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			/*userReqDTO.setUserId(getManagedBean().getScLoi().getApproverUser()
					.getUserId());
			 */
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			//scLoi.setApproverUser(user);

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(60000l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();


			List<ScLoiNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScLoiTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
			for (ScLoiTerm scNegoTerm : negoTerms) {
				//scNegoTerm.getScTerm().setTermType("I");
				//scNegoTerm.getScTerm().getScTermDescs().get(0).setScTerm(scNegoTerm.getScTerm());
			}
			scLoi.setScNegoTerms(negoTerms);

			awardSupplierReqDTO.setScLoi(scLoi);
			awardSupplierReqDTO.setOutcome(outcome);
			awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOIDtl(awardSupplierReqDTO);

			if (awardSupplierResDTO.isSuccessful()) {
				this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"SuccessFully Saved", "LOI"));
			}

		} finally {
			statusSO = null;
			userSO = null;
			iqtAwardSO = null;
		}
	}

	public String submitAction() {
		debugLogger("submitAction");

		IStatusSO statusSO = null;
		IUserSO userSO = null;
		IQTAwardSO iqtAwardSO = null;

		try {
			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
			.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoi scLoi = this.getManagedBean().getScLoi();
			scLoi.setIssueDate(new Date());

			// Date date = new Date();
			System.out.println("Date : " + scLoi.getIssueDate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoi.getIssueDate());
			cal.add(Calendar.DATE, 14);
			System.out.println("Date : " + cal.getTime());
			scLoi.setAckDueDate(cal.getTime());
			System.out.println("LOI Response Due Date : "
					+ scLoi.getAckDueDate());


			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			/*userReqDTO.setUserId(getManagedBean().getScLoi().getApproverUser()
					.getUserId());
			 */
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			//scLoi.setApproverUser(user);

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(60000l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();

			List<ScLoiNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScLoiTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
			for (ScLoiTerm scNegoTerm : negoTerms) {
				//scNegoTerm.getScTerm().setTermType("I");
				//scNegoTerm.getScTerm().getScTermDescs().get(0).setScTerm(scNegoTerm.getScTerm());
			}
			scLoi.setScNegoTerms(negoTerms);

			awardSupplierReqDTO.setScLoi(scLoi);
			awardSupplierReqDTO.setOutcome(outcome);
			awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOIDtl(awardSupplierReqDTO);

			if (awardSupplierResDTO.isSuccessful()) {
				scLoi = awardSupplierResDTO.getScLoi();

				renewSesionObject();

				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
								.getMessage(
										MessageConst.INFO_700026,
										new String[] {
												getParameterDesc(
														ParameterTypeConst.DOCUMENT_TYPES,
														ParameterConst.DOCTYPE_LETTER_OF_INTENT),
														scLoi.getLoiNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
										.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			statusSO = null;
			userSO = null;
			iqtAwardSO = null;
		}

		return null;
	}

	/**
	 * Method to save the LOI details
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		IStatusSO statusSO = null;
		IUserSO userSO;
		IQTAwardSO iqtAwardSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
			.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			ScLoi scLoi = this.getManagedBean().getScLoi();
			//Long statusId = scLoi.getPmStatus().getStatusId().longValue();
			Long statusId =null ;

			scLoi.setIssueDate(new Date());

			// Date date = new Date();
			System.out.println("Date : " + scLoi.getIssueDate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoi.getIssueDate());
			cal.add(Calendar.DATE, 14);
			System.out.println("Date : " + cal.getTime());
			scLoi.setAckDueDate(cal.getTime());
			System.out.println("LOI Response Due Date : "
					+ scLoi.getAckDueDate());


			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			/*userReqDTO.setUserId(getManagedBean().getScLoi().getApproverUser()
					.getUserId());
			 */
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			//scLoi.setApproverUser(user);

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(60000l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();


			List<ScLoiNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScLoiTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
			for (ScLoiTerm scNegoTerm : negoTerms) {
				//scNegoTerm.getScTerm().setTermType("I");
				//scNegoTerm.getScTerm().getScTermDescs().get(0).setScTerm(scNegoTerm.getScTerm());
			}
			scLoi.setScNegoTerms(negoTerms);

			awardSupplierReqDTO.setScLoi(scLoi);
			awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());
			awardSupplierReqDTO.setOutcome(outcome);
			awardSupplierReqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			awardSupplierReqDTO.setTaskId(this.getManagedBean().getTaskId());

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOIDtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

			if (awardSupplierResDTO.isSuccessful()) {
				scLoi = awardSupplierResDTO.getScLoi();

				renewSesionObject();

				String messageCode = null;

				if (QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700013;
				} else if (QTStatusConst.LOI_PENDING_NEGOTIATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}

				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT,
						scLoi.getLoiNo());
			}
		} finally {
			statusSO = null;
			userSO = null;
			iqtAwardSO = null;
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	public StreamedContent getLoiLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
		generateLoiLetterFromVelocityToPdf((ScAttachment) pbAttachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;

	}

	private void generateLoiLetterFromVelocityToPdf(ScAttachment attachment)
			throws Exception {
		attachment.setFileName("Loi Letter.pdf");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOI_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}
}

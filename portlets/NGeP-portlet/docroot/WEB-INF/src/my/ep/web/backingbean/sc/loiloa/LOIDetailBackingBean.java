/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.loiloa;
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
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.DPStatusConstants;
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
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.loaloi.ScTerm;
import my.ep.persistence.entity.sc.loaloi.ScTermDesc;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.type.sc.TermType;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loiloa.LOIDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends SourcingBaseBackingBean<LOIDetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOIDetailBackingBean.class);

	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");
		loiDetailsAction();
		populateUserMinistryList();
		initializeRemarks(new ScLoi());
		getApproverNames();
	}
	
	public void loiDetailsAction() {
		debugLogger("loiDetailsAction");
		IQTAwardSO qtAwardSO = null;
		try {
			
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(1450L);
			reqDTO.setOrgProfileId(32950L);
			
			AwardSupplierResDTO resDTO = qtAwardSO.retrievePendingLoiList(reqDTO);
			getManagedBean().setLoiLoas(resDTO.getLoiLoas());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());

			// rec supplier in loi list
			getManagedBean().setRecSupplierList(resDTO.getPendingLoiLoas());
			

			//getManagedBean().setRecommend(resDTO.getScQtRecommends().get(0));
			//getManagedBean().setScQtRecommendDtls(resDTO.getScQtRecommendDtls());
			
			reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(resDTO.getLoiLoas().get(0).getScLois().get(0));
			resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			
			
			// loi details
			getManagedBean().setScLoi(resDTO.getScLoi());
			
			// branch list \
			if(resDTO.getSmSupplierBranchs() != null && resDTO.getSmSupplierBranchs().size() >0){
				getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			}
			
			
			// call approvers and contract signers
			
			if(resDTO.getApproverList() != null && resDTO.getApproverList().size() >0){
				getManagedBean().setApproverNames(resDTO.getApproverList());
			}
			
			if(resDTO.getContractSignerList() != null && resDTO.getContractSignerList().size() >0){
				getManagedBean().setContractSigners(resDTO.getContractSignerList());
			}
			
			
			
			// remarks
			getManagedBean().getScLoi().setScLoiRemarks(initializeRemarks(resDTO.getScLoi()));
			
			// status
			getCurrentStatus(DPStatusConstants.LOI_PENDING_SUBMISSION);
			
		} catch (Exception ex) {																		
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
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
		System.out.println("SelectedTabIndex :"+ getManagedBean().getSelectedTabIndex());
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
	public void viewItemDetails(ScLoi loi) {

		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoi(loi);
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
		try {
			List<ScLoiNegoLocation> negoLocationList = getManagedBean()
					.getNegoLocationList();
			if (null == negoLocationList) {
				negoLocationList = new ArrayList<ScLoiNegoLocation>();
			}
			negoLocation.setScLoi(getManagedBean().getScLoi());
			negoLocationList.add(negoLocation);
			this.getManagedBean().setNegoLocationList(negoLocationList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addNegotiationTerm() {
		debugLogger("addNegotiationTerm");
		ScLoiTerm negoTerm = new ScLoiTerm();
		ScTerm scTerm = new ScTerm();
		try {
			List<ScLoiTerm> negoTermsList = getManagedBean()
					.getNegoTermsList();
			if (null == negoTermsList) {
				negoTermsList = new ArrayList<ScLoiTerm>();
			}
			List<ScTermDesc> scTermDescList = getManagedBean()
					.getSelectedCheckDetails();
			if (null == scTermDescList){
				scTermDescList = new ArrayList<ScTermDesc>();
			}
			scTermDescList.add(new ScTermDesc());
			scTerm.setScTermDescs(scTermDescList);
			negoTerm.setScTerm(scTerm);
			negoTerm.setScLoi(getManagedBean().getScLoi());
			negoTermsList.add(negoTerm);
			this.getManagedBean().setNegoTermsList(negoTermsList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void populatePreDefinedDetails() {
		debugLogger("populatePreDefinedDetails");
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setTermType(TermType.TERM_LOI.value());
			reqDTO.setLanguageCode("en");

			AwardSupplierResDTO resDTO = qtAwardSO
					.retrieveTermDescListByTermType(reqDTO);

			List<ScTermDesc> list = resDTO.getTermDescResultBean()
					.getResultList();

			getManagedBean().setScTermDescList(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPredefinedTerms() {
		debugLogger("addPredefinedTerms");

		ScLoiTerm scNegoTerm = new ScLoiTerm();
		ScTerm scTerm = new ScTerm();
		List<ScLoiTerm> list = getManagedBean().getNegoTermsList();

		if (list == null) {
			list = new ArrayList<ScLoiTerm>();
		}
		List<ScTermDesc> scTermDescList = getManagedBean()
				.getSelectedCheckDetails();
		scTerm.setScTermDescs(scTermDescList);
		scNegoTerm.setScTerm(scTerm);
		System.out.println("Description : "
				+ scNegoTerm.getScTerm().getScTermDescs().get(0).getTermDesc());
		scNegoTerm.setScLoi(getManagedBean().getScLoi());
		list.add(scNegoTerm);
		getManagedBean().setNegoTermsList(list);

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
						.getScQtZoneCriterias();*/
				

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
				getManagedBean().setNegoTermsList(negoTermList);

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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return QTConstants.LOI_PAGE;
	}
	
	public String createLOI(){
		return "loiDetailsMain";
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void handleBranchDetailsChanged() {
		log.info("handleBranchDetailsChanged");
		String name = this.getManagedBean().getBranchName();
		/*if (name.equalsIgnoreCase("HQ")) {
			getManagedBean().getScLoi().setSupplierAddress("abcd");
			getManagedBean().getScLoi().setSupplierPhoneNo(7324797l);
			getManagedBean().getScLoi().setSupplierFaxNo(1234L);
			getManagedBean().getScLoi().setSupplierContact("KBB SDN BHD");
			getManagedBean().getScLoi().setSupplierContactMobil(7324810l);
			getManagedBean().getScLoi().setSupplierContactEmail("hq@gmail.com");
		} else if (name.equalsIgnoreCase("Putrajaya")) {
			getManagedBean().getScLoi().setSupplierAddress("xyz");
			getManagedBean().getScLoi().setSupplierPhoneNo(6789L);
			getManagedBean().getScLoi().setSupplierFaxNo(4567L);
			getManagedBean().getScLoi().setSupplierContact("KBB SDN BHD");
			getManagedBean().getScLoi().setSupplierContactMobil(7324811l);
			getManagedBean().getScLoi().setSupplierContactEmail(
					"putra@gmail.com");
		} else {
			getManagedBean().getScLoi().setSupplierAddress("aaaaa");
			getManagedBean().getScLoi().setSupplierPhoneNo(23489L);
			getManagedBean().getScLoi().setSupplierFaxNo(65432L);
			getManagedBean().getScLoi().setSupplierContact("XYZ SDN BHD");
			getManagedBean().getScLoi().setSupplierContactMobil(732111l);
			getManagedBean().getScLoi().setSupplierContactEmail(
					"sara@gmail.com");
		}*/

	}

	/**
	 * Method to save the LOI details
	 */
	public void saveLOIDetailsAction() {
		debugLogger("saveLOIDetailsAction");
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
			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);

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
				scNegoTerm.getScTerm().setTermType("I");
				//scNegoTerm.getScTerm().getScTermDescs().get(0).setScTerm(scNegoTerm.getScTerm());
			}
			scLoi.setScNegoTerms(negoTerms);

			awardSupplierReqDTO.setScLoi(scLoi);

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOIDtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SuccessFully Saved", "LOI"));
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	
}

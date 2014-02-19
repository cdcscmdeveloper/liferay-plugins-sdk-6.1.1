package my.ep.web.backingbean.sc.loiloa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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
import my.ep.persistence.entity.sc.quationtender.ScLoi;
import my.ep.persistence.entity.sc.quationtender.ScNegoLocation;
import my.ep.persistence.entity.sc.quationtender.ScNegoTerm;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtItem;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScTerm;
import my.ep.persistence.entity.sc.quationtender.ScTermDesc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.type.sc.TermType;
import my.ep.web.backingbean.BaseBackingBean;
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
public class LOIDetailBackingBean extends BaseBackingBean<LOIDetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOILOADetailsBackingBean.class);

	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");

		getManagedBean().setScQtSpec(new ScQtSpec());
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	public void init() {
	}

	/**
	 * getting Approver drop down listing
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();

		if (approverUsers == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				approverUsers = resDTO.getUserList();
				System.out.println("Approver List: " + approverUsers.size());
				this.getManagedBean().setApproverNames(approverUsers);

			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
								.toString(), ex.getMessage()));
			} finally {
				userSO = null;
			}
		}
		return approverUsers;
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
		ScNegoLocation negoLocation = new ScNegoLocation();
		try {
			List<ScNegoLocation> negoLocationList = getManagedBean()
					.getNegoLocationList();
			if (null == negoLocationList) {
				negoLocationList = new ArrayList<ScNegoLocation>();
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
		ScNegoTerm negoTerm = new ScNegoTerm();
		ScTerm scTerm = new ScTerm();
		try {
			List<ScNegoTerm> negoTermsList = getManagedBean()
					.getNegoTermsList();
			if (null == negoTermsList) {
				negoTermsList = new ArrayList<ScNegoTerm>();
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

		ScNegoTerm scNegoTerm = new ScNegoTerm();
		ScTerm scTerm = new ScTerm();
		List<ScNegoTerm> list = getManagedBean().getNegoTermsList();

		if (list == null) {
			list = new ArrayList<ScNegoTerm>();
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

				this.getManagedBean().setUserDivisionList(
						resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setUserDivisionList(
					new ArrayList<PmOrgProfile>());
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

				System.out.println("LOI Status : "
						+ getManagedBean().getScLoi().getPmStatus()
								.getStatusId());

				getManagedBean().setQt(scQt);
				getManagedBean().getScLoi().setBrandNew(false);
				populateParentOrgProfile(getManagedBean().getQt());

				System.out.println(" Qtn/Tender No : "
						+ getManagedBean().getQt().getQtNo());
				System.out.println("Status : "
						+ getManagedBean().getQt().getPmStatus().getStatusId());
				System.out.println("Status : "
						+ getManagedBean().getScLoi().getPmStatus()
								.getPmStatusDescs().get(0).getStatusName());

				List<ScQtZoneCriteria> zonelist = scQt.getScQtCriterias()
						.getScQtZoneCriterias();
				System.out.println("zonelist: " + zonelist.size());

				List<ScQtItem> itemList = resDTO.getItemResultBean()
						.getResultList();
				System.out.println("itemlist: " + itemList.size());

				getManagedBean().setScQtItemList(itemList);

				getManagedBean().setZonelist(zonelist);

				List<ScNegoLocation> negoLocationList = resDTO
						.getNegoLocResultBean().getResultList();
				System.out.println("list: " + negoLocationList.size());

				List<ScNegoTerm> negoTermList = resDTO.getNegoTermResultBean()
						.getResultList();
				System.out.println("negoTermlist: " + negoTermList.size());

				getManagedBean().setNegoLocationList(negoLocationList);
				getManagedBean().setNegoTermsList(negoTermList);

				getManagedBean().setApproverNames(getApproverNames());
				System.out.println("Approver User Id : "
						+ getManagedBean().getScLoi().getApproverUser()
								.getUserId());

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

				getManagedBean().getScLoi().setSmSupplier(
						getManagedBean().getScQtEvaluationResult()
								.getScQtProposal()
								.getScQtSupplier().getSmSupplier());

				/*getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposalEvaluate().getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierName());*/

				getManagedBean().getScLoi().setDocType(
						LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
				getManagedBean().getScLoi().setDocId(id);
				getManagedBean().getScLoi().setLoiDuration(14);
				getManagedBean().getScLoi().setLoiNo("12345");

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				showOfficerDetails();

				PmUser pmUser = new PmUser();
				getManagedBean().getScLoi().setApproverUser(pmUser);
				getManagedBean().setApproverNames(getApproverNames());
				System.out.println("Approver User Id : "
						+ getManagedBean().getScLoi().getApproverUser()
								.getUserId());

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

	/**
	 * 
	 */
	private void showOfficerDetails() {
		IAddressSO addressSO;
		try {
			PmUser user = new FacesScopes().getUserSessionBean();
			getManagedBean().setPmUser(user);
			getManagedBean().getScLoi().setOfficierName(user.getUserName());
			getManagedBean().getScLoi().setOfficerDesignation(
					user.getDesignation());

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setUserId(user.getUserId());
			addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);

			List<PmAddressType> addList = addResDTO.getAddressTypeList();

			getManagedBean().getScLoi().setOfficerAddress(
					addList.get(0).getPmAddress().getAddress1());
			getManagedBean().getScLoi().setOfficerFaxNo(
					addList.get(0).getPmAddress().getFaxNo());
			getManagedBean().getScLoi().setOfficerPhoneNo(
					addList.get(0).getPmAddress().getPhoneNo());
			getManagedBean().getScLoi().setOfficerEmail(
					addList.get(0).getPmAddress().getEmail());

			// getManagedBean().getScLoi().setApproverUser(user);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void handleBranchDetailsChanged() {
		log.info("handleBranchDetailsChanged");
		String name = this.getManagedBean().getBranchName();
		if (name.equalsIgnoreCase("HQ")) {
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
		}

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

			System.out
					.println("Approver User Id : "
							+ getManagedBean().getScLoi().getApproverUser()
									.getUserId());
			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getManagedBean().getScLoi().getApproverUser()
					.getUserId());

			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			scLoi.setApproverUser(user);

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(60000l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scLoi.setPmStatus(status);

			List<ScNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScNegoTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
			for (ScNegoTerm scNegoTerm : negoTerms) {
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
}

package my.ep.web.backingbean.ss.loiloa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.ss.quationtender.ScLoi;
import my.ep.persistence.entity.ss.quationtender.ScNegoLocation;
import my.ep.persistence.entity.ss.quationtender.ScNegoTerm;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.ss.quationtender.ScTerm;
import my.ep.persistence.entity.ss.quationtender.ScTermDesc;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.type.ss.TermType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOIDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
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

	private static final String MODULE_NAME = "ss";

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

	public List<SelectItem> getApproverNames() {
		debugLogger("getApproverNames");
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScNegoLocation negoLocation = new ScNegoLocation();
		try {
			List<ScNegoLocation> negoLocationList = getManagedBean()
					.getNegoLocationList();
			if (null == negoLocationList) {
				negoLocationList = new ArrayList<ScNegoLocation>();
			}
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
		try {
			List<ScNegoTerm> negoTermsList = getManagedBean()
					.getNegoTermsList();
			if (null == negoTermsList) {
				negoTermsList = new ArrayList<ScNegoTerm>();
			}
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
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setTermType(TermType.TERM_LOI.value());
			reqDTO.setLanguageCode("en");

			AwardSupplierResDTO resDTO = qtAwardSO
					.retrieveTermDescListByTermType(reqDTO);

			List<ScTermDesc> list = resDTO.getTermDescResultBean()
					.getResultList();
			System.out.println("LOI list: " + list.size());

			getManagedBean().setScTermDescList(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPredefinedTerms() {
		debugLogger("addPredefinedTerms");

		ScNegoTerm negoTerms = new ScNegoTerm();

		List<ScNegoTerm> list = getManagedBean().getNegoTermsList();

		List<ScTermDesc> scTermList1 = getManagedBean()
				.getSelectedCheckDetails();

		ScTerm term = new ScTerm();

		negoTerms.setScTerm(term);

		negoTerms.getScTerm().setScTermDescs(scTermList1);

		list.add(negoTerms);

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

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject("pm",
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

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject("pm",
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
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}
	
	public String createLOIAction(ScQtProposalRanking scQtProposalRanking,
			String docType) {
		debugLogger("createLOIAction");
		IQTAwardSO iqtAwardSO = null;
		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
		try {

			long id = scQtProposalRanking.getQtProposalRankingId();

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			reqDTO.setDocId(id);

			String language = new FacesScopes().getUserLocale().getLanguage();
			reqDTO.setLanguageCode(language);

			docType = reqDTO.getDocType();

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOIDtl(reqDTO);

			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|" + scQt.getQtNo());
			}

			ScLoi scLoi = resDTO.getScLoi();
			
			if (scLoi != null) {
				System.out.println("Loi Id : " + scLoi.getLoiId());
				scLoi.setLoiNo("12345");
				getManagedBean().setScLoi(scLoi);

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");
				
				System.out.println("LOI Status : " + getManagedBean().getScLoi().getPmStatus().getStatusId());
				
				getManagedBean().setQt(scQt);
				getManagedBean().getScLoi().setBrandNew(false);
				populateParentOrgProfile(getManagedBean().getQt());
				
				System.out.println(" Qtn/Tender No : " + getManagedBean().getQt().getQtNo());
				System.out.println("Status : " + getManagedBean().getQt().getPmStatus().getStatusId());
				System.out.println("Status : " + getManagedBean().getScLoi().getPmStatus().getPmStatusDescs().get(0).getStatusName());
				
				List<ScQtZoneCriteria> zonelist = scQt.getScQtCriterias().getScQtZoneCriterias();
				System.out.println("zonelist: " + zonelist.size());
				
				List<ScQtItem> itemList = resDTO.getItemResultBean().getResultList();
				System.out.println("itemlist: " + itemList.size());
				
				getManagedBean().setScQtItemList(itemList);
				
				getManagedBean().setZonelist(zonelist);
				
				List<ScNegoLocation> negoLocationList = resDTO.getNegoLocResultBean().getResultList();
				System.out.println("list: " + negoLocationList.size());
				
				List<ScNegoTerm> negoTermList = resDTO.getNegoTermResultBean().getResultList();
				System.out.println("negoTermlist: " + negoTermList.size());
				
				getManagedBean().setNegoLocationList(negoLocationList);
				getManagedBean().setNegoTermsList(negoTermList);

			} else {
				
				getManagedBean().setQt(scQt);
				/*getManagedBean().setQt(new ScQt());*/
				
				populateParentOrgProfile(getManagedBean().getQt());
				
				getManagedBean().setScLoi(new ScLoi());
				
				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				getManagedBean().getScLoi().setSmSupplier(getManagedBean().getScQtProposalRanking().getScQtProposalEvaluate()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier());
				
				getManagedBean().getScLoi().setSupplierName(
						getManagedBean().getScQtProposalRanking().getScQtProposalEvaluate()
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier().getSupplierName());
				
				getManagedBean().getScLoi().setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
				getManagedBean().getScLoi().setDocId(id);
				
				getManagedBean().getScLoi().setLoiDuration(14);
				getManagedBean().getScLoi().setLoiNo("12345");
				
				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");
				
				showOfficerDetails();
				
				getManagedBean().setNegoLocationList(null);
				getManagedBean().setNegoTermsList(null);
				
				SmSupplierBranch branch = new SmSupplierBranch();
				SmSupplierBranch branch1 = new SmSupplierBranch();
				branch.setBranchName("HQ");
				branch1.setBranchName("SET");
				bList.add(branch);
				bList.add(branch1);
				getManagedBean().setBranchsList(bList);

				branch.setSmAppl(getManagedBean().getScQtProposalRanking().getScQtProposalEvaluate()
						.getScQtProposal().getScQtSupplier().getSmSupplier()
						.getSmAppl());
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
					"pm", IAddressSO.class);
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
			
			//getManagedBean().getScLoi().setApproverUser(user);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		

		
	}

	public void handleBranchDetailsChanged() {
		log.info("handleBranchDetailsChanged");
		String name = this.getManagedBean().getBranchName();
		if (name.equalsIgnoreCase("HQ")){
			getManagedBean().getScLoi().setSupplierAddress("abcd");
			getManagedBean().getScLoi().setSupplierPhoneNo(1234567L);
			getManagedBean().getScLoi().setSupplierFaxNo(1234L);
		} else {
			getManagedBean().getScLoi().setSupplierAddress("xyz");
			getManagedBean().getScLoi().setSupplierPhoneNo(6789L);
			getManagedBean().getScLoi().setSupplierFaxNo(4567L);
		}
		
	}
	
	/**
	 * Method to save the LOI details
	 */
	public void saveLOIDetailsAction() {
		debugLogger("saveLOIDetailsAction");
		IStatusSO statusSO = null;
		IQTAwardSO iqtAwardSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);

			ScLoi scLoi = this.getManagedBean().getScLoi();
			scLoi.setIssueDate(new Date());

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scLoi.setPmStatus(status);

			List<ScNegoLocation> negoLocations = this.getManagedBean()
					.getNegoLocationList();
			scLoi.setScNegoLocations(negoLocations);

			List<ScNegoTerm> negoTerms = this.getManagedBean()
					.getNegoTermsList();
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

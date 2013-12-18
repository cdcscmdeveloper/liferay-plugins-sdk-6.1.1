package my.ep.web.backingbean.ss.loiloa;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import my.ep.persistence.entity.ss.quationtender.ScBondDtl;
import my.ep.persistence.entity.ss.quationtender.ScLoa;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.ss.LoiLoaDocType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOADetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends BaseBackingBean<LOADetailManagedBean> {
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);

	private static final String MODULE_NAME = "ss";

	public LOADetailBackingBean() {
		log.info("inside LOADetailBackingBean constructor");
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

	public void addPerformanceBond() {
		debugLogger("addPerformanceBond");
		ScBondDtl scBondDtl = new ScBondDtl();
		try {

			List<ScBondDtl> bondDtlList = getManagedBean().getScBondDtlList();
			if (null == bondDtlList) {
				bondDtlList = new ArrayList<ScBondDtl>();
			}
			bondDtlList.add(scBondDtl);
			this.getManagedBean().setScBondDtlList(bondDtlList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
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
	
	
	public void handleContractPTJ() {
		debugLogger("handleContractPTJ");
		
		IOrgProfileSO orgProfileSO;
		try {
			orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(1L);
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			
			List<PmOrgProfile> list = resDTO.getOrgProfileList();
			System.out.println("Ministry List: " + list.size());
			
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			list = resDTO.getOrgProfileList();
			System.out.println("Division List: " + list.size());
			for (PmOrgProfile orgProfile : list) {
				System.out.println(orgProfile.getOrgProfileId() + "|"
						+ orgProfile.getOrgCode() + "|" + orgProfile.getOrgName());
			}
			getManagedBean().setUserDivisionList(list);
		} catch (Exception e) {
			
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

	public String createLOAAction(ScQtProposalRanking scQtProposalRanking,
			String docType) {
		debugLogger("createLOAAction");
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

			AwardSupplierResDTO resDTO = iqtAwardSO.retrieveLOADtl(reqDTO);

			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|"
						+ scQt.getQtNo());
			}

			ScLoa scLoa = resDTO.getScLoa();

			if (scLoa != null) {
				System.out.println("Loi Id : " + scLoa.getLoaId());
				scLoa.setLoaNo("12345");
				getManagedBean().setScLoa(scLoa);

				getManagedBean().setScQtProposalRanking(scQtProposalRanking);
				/*
				 * getManagedBean().getScLoa().setSupplierName(
				 * getManagedBean().getScQtProposalRanking()
				 * .getScQtProposal().getScQtSupplier()
				 * .getSmSupplier().getSupplierName());
				 */

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				getManagedBean().setQt(scQt);

				getManagedBean().getScLoa().setBrandNew(false);

				populateParentOrgProfile(getManagedBean().getQt());

				// System.out.println("Status : " +
				// getManagedBean().getScLoa().getPmStatus().getPmStatusDescs().get(0).getStatusName());
				// System.out.println("Award Type : " +
				// getManagedBean().getQt().getAwardType());
				// System.out.println("Fulfilment Type : " +
				// getManagedBean().getQt().getScQtSpec().getFulfilmentType());
				// System.out.println("Contract Duration : " +
				// getManagedBean().getQt().getScQtSpec().getContractDuration());
				// System.out.println("Multiple Locality : " +
				// getManagedBean().getQt().getScQtCriterias().isMultiLocality());
				// System.out.println("Currency Name : " +
				// getManagedBean().getQt().getScQtSpec().getPmCurrency().getCurrencyName());

				PmUser user = new FacesScopes().getUserSessionBean();
				getManagedBean().getScLoa().setCreatedBy(user.getUserId());
				getManagedBean().getScLoa().setChangedBy(user.getUserId());
				getManagedBean().getScLoa().setCreatedDate(
						new Timestamp(System.currentTimeMillis()));

				List<ScQtZoneCriteria> zoneList = scQt.getScQtCriterias()
						.getScQtZoneCriterias();
				System.out.println("zonelist: " + zoneList.size());

				List<ScQtItem> itemList = resDTO.getItemResultBean()
						.getResultList();
				System.out.println("itemlist: " + itemList.size());

				getManagedBean().setScQtItemList(itemList);

				getManagedBean().setZonelist(zoneList);

				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				System.out.println("DeductionList : " + deductionList.size());
				getManagedBean().setScQtDeductionList(deductionList);
				
				handleContractPTJ();

			} else {

				getManagedBean().setQt(scQt);

				populateParentOrgProfile(getManagedBean().getQt());

				getManagedBean().setScLoa(new ScLoa());

				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				getManagedBean().getScLoa().setSmSupplier(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposalEvaluate().getScQtProposal()
								.getScQtSupplier().getSmSupplier());

				getManagedBean().getScLoa().setSupplierName(
						getManagedBean().getScQtProposalRanking()
								.getScQtProposalEvaluate().getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierName());

				getManagedBean().getScLoa().setDocType(
						LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
				getManagedBean().getScLoa().setDocId(id);

				getManagedBean().getScLoa().setLoaType("Standard");
				getManagedBean().getScLoa().setLoaDuration(14);
				getManagedBean().getScLoa().setBondDuration(30);
				getManagedBean().getScLoa().setLoaNo("12345");
				getManagedBean().getScLoa().setLoaAmount(new BigDecimal(10000));

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				// System.out.println("Fulfilment Type : " +
				// getManagedBean().getQt().getScQtSpec().getFulfilmentType());
				// System.out.println("Contract Duration : " +
				// getManagedBean().getQt().getScQtSpec().getContractDuration());
				// System.out.println("Multiple Locality : " +
				// getManagedBean().getQt().getScQtCriterias().isMultiLocality());

				showOfficerDetails();

				getManagedBean().setScQtDeductionList(null);

				SmSupplierBranch branch = new SmSupplierBranch();
				SmSupplierBranch branch1 = new SmSupplierBranch();
				branch.setBranchName("HQ");
				branch1.setBranchName("SET");
				bList.add(branch);
				bList.add(branch1);
				getManagedBean().setBranchsList(bList);

				branch.setSmAppl(getManagedBean().getScQtProposalRanking()
						.getScQtProposalEvaluate().getScQtProposal()
						.getScQtSupplier().getSmSupplier().getSmAppl());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return QTConstants.LOA_PAGE;
	}

	/**
	 * 
	 */
	private void showOfficerDetails() {
		IAddressSO addressSO;
		try {
			PmUser user = new FacesScopes().getUserSessionBean();
			getManagedBean().setPmUser(user);
			getManagedBean().getScLoa().setOfficierName(user.getUserName());
			getManagedBean().getScLoa().setOfficerDesignation(
					user.getDesignation());

			addressSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IAddressSO.class);
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setUserId(user.getUserId());
			addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);

			List<PmAddressType> addList = addResDTO.getAddressTypeList();

			getManagedBean().getScLoa().setOfficerAddress(
					addList.get(0).getPmAddress().getAddress1());
			getManagedBean().getScLoa().setOfficerFaxNo(
					addList.get(0).getPmAddress().getFaxNo());
			getManagedBean().getScLoa().setOfficerPhoneNo(
					addList.get(0).getPmAddress().getPhoneNo());
			getManagedBean().getScLoa().setOfficerEmail(
					addList.get(0).getPmAddress().getEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");
		String name = this.getManagedBean().getBranchName();
		if (name.equalsIgnoreCase("HQ")) {
			getManagedBean().getScLoa().setSupplierAddress("abcd");
			getManagedBean().getScLoa().setSupplierPhoneNo(7324797l);
			getManagedBean().getScLoa().setSupplierFaxNo(1234L);
			getManagedBean().getScLoa().setSupplierContact("KBB SDN BHD");
			getManagedBean().getScLoa().setSupplierContactMobil(7324810l);
			getManagedBean().getScLoa().setSupplierContactEmail("hq@gmail.com");
		} else {
			getManagedBean().getScLoa().setSupplierAddress("xyz");
			getManagedBean().getScLoa().setSupplierPhoneNo(6789L);
			getManagedBean().getScLoa().setSupplierFaxNo(4567L);
			getManagedBean().getScLoa().setSupplierContact("KBB SDN BHD");
			getManagedBean().getScLoa().setSupplierContactMobil(7324811l);
			getManagedBean().getScLoa().setSupplierContactEmail("hq@gmail.com");
		}

	}

	/**
	 * Method to save the LOA details
	 */
	public void saveLOADetailsAction() {
		debugLogger("saveLOADetailsAction");
		IQTAwardSO iqtAwardSO = null;
		IStatusSO statusSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);

			ScLoa scLoa = this.getManagedBean().getScLoa();
			scLoa.setIssueDate(new Date());

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scLoa.setPmStatus(status);

			awardSupplierReqDTO.setScLoa(scLoa);

			AwardSupplierResDTO awardSupplierResDTO = iqtAwardSO
					.saveLOADtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoa(awardSupplierResDTO.getScLoa());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SuccessFully Saved", "LOA"));

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}
}

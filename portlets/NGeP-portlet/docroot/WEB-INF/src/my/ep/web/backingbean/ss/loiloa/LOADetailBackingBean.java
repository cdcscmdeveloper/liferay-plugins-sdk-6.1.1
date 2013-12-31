package my.ep.web.backingbean.ss.loiloa;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
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

	/**
	 * getting Approver drop down listing
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUserNames = this.getManagedBean().getApproverName();

		if (approverUserNames == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						QTConstants.PM_MODULE, IUserSO.class);

				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.APPROVER.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				approverUserNames = resDTO.getUserList();
				System.out.println("Approver List: " + approverUserNames.size());
				this.getManagedBean().setApproverName(approverUserNames);

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
		return approverUserNames;
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
						.getInstance().getServiceObject(QTConstants.PM_MODULE,
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
						.getInstance().getServiceObject(QTConstants.PM_MODULE,
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

	public String createLOAAction(ScQtProposalRanking scQtProposalRanking,
			String docType) {
		debugLogger("createLOAAction");
		IQTAwardSO iqtAwardSO = null;
		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
		try {

			long id = scQtProposalRanking.getQtProposalRankingId();

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTAwardSO.class);
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
				System.out.println("LOA Id : " + scLoa.getLoaId());
				scLoa.setLoaNo("12345");
				getManagedBean().setScLoa(scLoa);

				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				getManagedBean().setQt(scQt);

				getManagedBean().getScLoa().setBrandNew(false);

				populateParentOrgProfile(getManagedBean().getQt());

				System.out.println("Fulfilment Type : "
						+ getManagedBean().getQt().getScQtSpec()
								.getFulfilmentType());
				System.out.println("Status : " +
						getManagedBean().getScLoa().getPmStatus().getPmStatusDescs().get(0).getStatusName());
				
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

				getManagedBean().setApproverName(getApproverNames());
				System.out.println("Approver User Id : "
						+ getManagedBean().getScLoa().getApproverUser()
								.getUserId());
				getManagedBean().setContractSignerList(getContractSigners());

				System.out.println("LOA Offered Price : "
						+ getManagedBean().getScLoa().getLoaAmount());
				if (getManagedBean().getScLoa().getLoaAmount().intValue() > 500000) {
					getManagedBean().setBondPercentage("5%");
					getManagedBean().setPercentageBond(0.05);
					System.out.println("Bond Percentage : "
							+ getManagedBean().getPercentageBond());
				} else if (getManagedBean().getScLoa().getLoaAmount()
						.intValue() > 200000
						&& getManagedBean().getScLoa().getLoaAmount()
								.intValue() < 500000) {
					getManagedBean().setBondPercentage("2.5%");
					getManagedBean().setPercentageBond(0.025);
					System.out.println("Bond Percentage : "
							+ getManagedBean().getPercentageBond());
				}

				System.out.println("Contract Duration : "
						+ getManagedBean().getQt().getScQtSpec()
								.getContractDuration());

				int amt = (int) (((getManagedBean().getScLoa().getLoaAmount()
						.intValue() / getManagedBean().getQt().getScQtSpec()
						.getContractDuration().intValue()) * 12) * getManagedBean()
						.getPercentageBond());

				System.out.println("D :" + amt);
				getManagedBean().setBondAmount(amt);
				System.out.println("bond amount : "
						+ getManagedBean().getBondAmount());

				retrieveBondDtls(getManagedBean().getScLoa());
			} else {
				getManagedBean().setQt(scQt);

				populateParentOrgProfile(getManagedBean().getQt());

				getManagedBean().setScLoa(new ScLoa());

				getManagedBean().setScQtProposalRanking(scQtProposalRanking);

				PmOrgProfile orgProfile = new PmOrgProfile();
				getManagedBean().getScLoa().setContractPTJProfile(orgProfile);

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
				getManagedBean().getScLoa().setLoaAmount(
						new BigDecimal(1000000));
				getManagedBean().getScLoa().setMtoDuration(14);

				getManagedBean().setSmMofAccount(new SmMofAccount());
				getManagedBean().getSmMofAccount().setMofNo("MOF000001");

				showOfficerDetails();

				PmUser pmUser = new PmUser();
				getManagedBean().getScLoa().setApproverUser(pmUser);
				getManagedBean().setApproverName(getApproverNames());
				System.out.println("Approver User Id : "
						+ getManagedBean().getScLoa().getApproverUser()
								.getUserId());

				getManagedBean().getScLoa().setContractSignerUser(pmUser);
				getManagedBean().setContractSignerList(getContractSigners());

				getManagedBean().setScQtDeductionList(null);

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

				System.out.println("LOA Offered Price : "
						+ getManagedBean().getScLoa().getLoaAmount());
				if (getManagedBean().getScLoa().getLoaAmount().intValue() > 500000) {
					getManagedBean().setBondPercentage("5%");
					getManagedBean().setPercentageBond(0.05);
					System.out.println("Bond Percentage : "
							+ getManagedBean().getPercentageBond());
				} else if (getManagedBean().getScLoa().getLoaAmount()
						.intValue() > 200000
						&& getManagedBean().getScLoa().getLoaAmount()
								.intValue() < 500000) {
					getManagedBean().setBondPercentage("2.5%");
					getManagedBean().setPercentageBond(0.025);
					System.out.println("Bond Percentage : "
							+ getManagedBean().getPercentageBond());
				}

				System.out.println("Contract Duration : "
						+ getManagedBean().getQt().getScQtSpec()
								.getContractDuration());

				int amt = (int) (((getManagedBean().getScLoa().getLoaAmount()
						.intValue() / getManagedBean().getQt().getScQtSpec()
						.getContractDuration().intValue()) * 12) * getManagedBean()
						.getPercentageBond());

				getManagedBean().setBondAmount(amt);
				System.out.println("bond amount : "
						+ getManagedBean().getBondAmount());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return QTConstants.LOA_PAGE;
	}

	/**
	 * @param scLoa
	 */
	private void retrieveBondDtls(ScLoa scLoa) {
		debugLogger("retrieveBondDtls");
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			ScLoa loa = new ScLoa();
			loa.setLoaId(scLoa.getLoaId());
			reqDTO.setScLoa(loa);
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveBondDtls(reqDTO);

			List<ScBondDtl> bondDtl = resDTO.getBondDtls();
			System.out.println("Bond Details : " + bondDtl.size());

			for (ScBondDtl dtl : bondDtl) {
				System.out.println("Ref No : " + dtl.getBondRefNo());
				System.out.println("Institution Name : "
						+ dtl.getInstitutionName());
			}

			getManagedBean().setScBondDtlList(bondDtl);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IAddressSO.class);
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
		} else if (name.equalsIgnoreCase("Putrajaya")) {
			getManagedBean().getScLoa().setSupplierAddress("xyz");
			getManagedBean().getScLoa().setSupplierPhoneNo(6789L);
			getManagedBean().getScLoa().setSupplierFaxNo(4567L);
			getManagedBean().getScLoa().setSupplierContact("KBB SDN BHD");
			getManagedBean().getScLoa().setSupplierContactMobil(7324811l);
			getManagedBean().getScLoa().setSupplierContactEmail(
					"putra@gmail.com");
		} else {
			getManagedBean().getScLoa().setSupplierAddress("aaaaa");
			getManagedBean().getScLoa().setSupplierPhoneNo(23489L);
			getManagedBean().getScLoa().setSupplierFaxNo(65432L);
			getManagedBean().getScLoa().setSupplierContact("XYZ SDN BHD");
			getManagedBean().getScLoa().setSupplierContactMobil(732111l);
			getManagedBean().getScLoa().setSupplierContactEmail(
					"sara@gmail.com");
		}

	}

	/**
	 * Method to save the LOA details
	 */
	public void saveLOADetailsAction() {
		debugLogger("saveLOADetailsAction");
		IQTAwardSO iqtAwardSO = null;
		IUserSO userSO;
		IStatusSO statusSO = null;
		try {

			iqtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IStatusSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IUserSO.class);

			ScLoa scLoa = this.getManagedBean().getScLoa();
			scLoa.setIssueDate(new Date());

			// Date date = new Date();
			System.out.println("Date : " + scLoa.getIssueDate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoa.getIssueDate());
			cal.add(Calendar.DATE, 14);
			System.out.println("Date : " + cal.getTime());
			scLoa.setAckDueDate(cal.getTime());
			System.out.println("LOA Response Due Date : "
					+ scLoa.getAckDueDate());

			System.out
					.println("Approver User Id : "
							+ getManagedBean().getScLoa().getApproverUser()
									.getUserId());
			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getManagedBean().getScLoa().getApproverUser()
					.getUserId());

			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			scLoa.setApproverUser(user);
			scLoa.setContractSignerUser(user);

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

	public void retrieveContractType() {
		debugLogger("retrieveContractType");
		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setOrgProfileId(1l);
			reqDTO.setOrgType(getManagedBean().getScLoa().getContractType());
			UserResDTO dto = userSO
					.retrieveUserListByOrgProfileAndContractType(reqDTO);
			List<PmUser> userList = dto.getUserList();
			System.out.println("PmUser List : " + userList.size());
			// System.out.println(dto.getUserList().size());
			getManagedBean().setPmUserList(userList);

			System.out.println("User Id : "
					+ getManagedBean().getPmUserList().get(0).getUserId());
			// handleContractPTJ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleContractPTJ() {
		debugLogger("handleContractPTJ");

		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			System.out.println("User Id : "
					+ getManagedBean().getPmUserList().get(0).getUserId());
			reqDTO.setUserId(getManagedBean().getPmUserList().get(0)
					.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			List<PmUserOrg> list = resDTO.getUserOrgList();
			System.out.println("List: " + list.size());
			for (PmUserOrg userOrg : list) {
				System.out.println(userOrg.getUserOrgId() + "|"
						+ userOrg.getPmUser().getUserId() + "|"
						+ userOrg.getPmUser().getUserName() + "|"
						+ userOrg.getPmOrgProfile().getOrgProfileId() + "|"
						+ userOrg.getPmOrgProfile().getOrgCode() + "|"
						+ userOrg.getPmOrgProfile().getOrgName());
			}
			getManagedBean().setPmUserOrgList(list);

			// handleContractPTJ();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<PmUser> getContractSigners() {
		List<PmUser> contractUsers = this.getManagedBean()
				.getContractSignerList();

		if (contractUsers == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						QTConstants.PM_MODULE, IUserSO.class);

				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				contractUsers = resDTO.getUserList();
				System.out.println("Contract Signers List: "
						+ contractUsers.size());
				this.getManagedBean().setContractSignerList(contractUsers);

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
		return contractUsers;
	}

}

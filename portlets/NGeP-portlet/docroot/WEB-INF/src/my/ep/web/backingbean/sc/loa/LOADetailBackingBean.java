package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends SourcingBaseBackingBean<LOADetailManagedBean> {
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);

	public LOADetailBackingBean() {
		log.info("inside LOADetailBackingBean constructor");
		//getManagedBean().setScQtSpec(new ScQtSpec());
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
		ScPerformanceBond scBondDtl = new ScPerformanceBond();
		try {

			List<ScPerformanceBond> bondDtlList = getManagedBean().getScBondDtlList();
			if (null == bondDtlList) {
				bondDtlList = new ArrayList<ScPerformanceBond>();
			}
			scBondDtl.setScLoa(getManagedBean().getScLoa());
			bondDtlList.add(scBondDtl);
			this.getManagedBean().setScBondDtlList(bondDtlList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public String createLOAAction(ScLoiLoa scLoiLoa) {
		debugLogger("createLOAAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			String language = new FacesScopes().getUserLocale().getLanguage();
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoiLoa(scLoiLoa);
			reqDTO.setLanguageCode(language);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOADtlTab(reqDTO);
			
			ScLoa scLoa = new ScLoa();
			
			scLoa.setScLoiLoa(scLoiLoa);
			scLoa.setContractType(new PmParameter());
			scLoa.setContractAgency(new PmOrgProfile());
			scLoa.setContractAdmin(new PmUser());
			scLoa.setContractSigner(new PmUser());
			
			getManagedBean().setScLoa(scLoa);
			
			getManagedBean().setScRequestNote(resDTO.getScRequestNote());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());
			
			getManagedBean().setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			handleBranchDetailsChanged();
			
			getManagedBean().setLoaAgencyWitness(new ScLoaWitness());

			//getManagedBean().getScLoa().setContractPTJProfile(orgProfile);

			/*getManagedBean().getScLoa().setSmSupplier(
					getManagedBean().getScQtEvaluationResult()
							.getScQtProposal()
							.getScQtSupplier().getSmSupplier());*/

			/*getManagedBean().getScLoa().setSupplierName(
					getManagedBean().getScQtProposalRanking()
							.getScQtProposalEvaluate().getScQtProposal()
							.getScQtSupplier().getSmSupplier()
							.getSupplierName());*/

		/*	getManagedBean().getScLoa().setDocType(
					LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			getManagedBean().getScLoa().setDocId(id);*/

			/*getManagedBean().getScLoa().setLoaType("Standard");
			getManagedBean().getScLoa().setLoaDuration(14);
			getManagedBean().getScLoa().setBondDuration(30);
			getManagedBean().getScLoa().setLoaNo("12345");
			getManagedBean().getScLoa().setLoaAmount(
					new BigDecimal(1000000));*/
			//getManagedBean().getScLoa().setMtoDuration(14);

			getManagedBean().setScQtRecommendDtls(resDTO.getScQtRecommendDtls());
			
			if (resDTO.getDeductionResultBean() != null) {
				getManagedBean().setScQtDeductionList(resDTO.getDeductionResultBean().getResultList());	
			}
			
			getManagedBean().setContractSignerList(resDTO.getContractSignerList());

			/*branch.setSmAppl(getManagedBean().getScQtProposalRanking()
					.getScQtProposalEvaluate().getScQtProposal()
					.getScQtSupplier().getSmSupplier().getSmAppl());*/

			//TODO
			/*if (getManagedBean().getScLoa().getLoaAmt().intValue() > 500000) {
				getManagedBean().setBondPercentage("5%");
				getManagedBean().setPercentageBond(0.05);
				System.out.println("Bond Percentage : "
						+ getManagedBean().getPercentageBond());
			} else if (getManagedBean().getScLoa().getLoaAmt()
					.intValue() > 200000
					&& getManagedBean().getScLoa().getLoaAmt()
							.intValue() < 500000) {
				getManagedBean().setBondPercentage("2.5%");
				getManagedBean().setPercentageBond(0.025);
				System.out.println("Bond Percentage : "
						+ getManagedBean().getPercentageBond());
			}*/

			
/*
			int amt = (int) (((getManagedBean().getScLoa().getLoaAmount()
					.intValue() / getManagedBean().getQt().getScQtSpec()
					.getContractDuration().intValue()) * 12) * getManagedBean()
					.getPercentageBond());*/

		//	getManagedBean().setBondAmount(amt);
		} finally {
			qtAwardSO = null;
		}
		
		return QTConstants.LOA_PAGE;
	}
	
	public List<TplSpecZoneDtl> getZoneList() {
		ISpecZoneLocSO specZoneLocSO = null;
		try {
			specZoneLocSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ISpecZoneLocSO.class);

			SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
			reqDTO.setDocType("QTZ");
			reqDTO.setDocId(146L);

			SpecZoneLocResDTO resDTO = specZoneLocSO.retrieveZoneDtlsByDocIdNType(reqDTO);
			
			return resDTO.getZonesList();

		} finally {
			specZoneLocSO = null;
		}
	}
	
	public String editLOAAction(ScLoiLoa scLoiLoa) {
		debugLogger("editLOAAction");
		
		IQTAwardSO qtAwardSO = null;
		List<SmSupplierBranch> bList = new ArrayList<SmSupplierBranch>();
		try {
			String language = new FacesScopes().getUserLocale().getLanguage();

			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoiLoa(scLoiLoa);
			reqDTO.setLanguageCode(language);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOADtlTab(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			
			getManagedBean().setScLoa(scLoa);
			
			getManagedBean().setScQtRecommendDtls(resDTO.getScQtRecommendDtls());
			
			if (resDTO.getDeductionResultBean() != null) {
				getManagedBean().setScQtDeductionList(resDTO.getDeductionResultBean().getResultList());	
			}
			
			getManagedBean().setContractSignerList(resDTO.getContractSignerList());

			
			/*if (getManagedBean().getScLoa().getLoaAmount().intValue() > 500000) {
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
*/
			
		/*	int amt = (int) (((getManagedBean().getScLoa().getLoaAmount()
					.intValue() / getManagedBean().getQt().getScQtSpec()
					.getContractDuration().intValue()) * 12) * getManagedBean()
					.getPercentageBond());*/

			
			//getManagedBean().setBondAmount(amt);
			
			//TODO
			retrieveBondDtls(getManagedBean().getScLoa());
			
		} finally {
			qtAwardSO = null;
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
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			ScLoa loa = new ScLoa();
			loa.setLoaId(scLoa.getLoaId());
			reqDTO.setScLoa(loa);
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveBondDtls(reqDTO);

			List<ScPerformanceBond> bondDtl = resDTO.getBondDtls();
			System.out.println("Bond Details : " + bondDtl.size());

			for (ScPerformanceBond dtl : bondDtl) {
				
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
			
		//	getManagedBean().getScLoa().setOfficierName(user.getUserName());
		//	getManagedBean().getScLoa().setOfficerDesignation(
				//	user.getDesignation());

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setUserId(user.getUserId());
			addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);

			List<PmAddressType> addList = addResDTO.getAddressTypeList();

			/*getManagedBean().getScLoa().setOfficerAddress(
					addList.get(0).getPmAddress().getAddress1());*/
			/*getManagedBean().getScLoa().setOfficerFaxNo(
					addList.get(0).getPmAddress().getFaxNo());
			getManagedBean().getScLoa().setOfficerPhoneNo(
					addList.get(0).getPmAddress().getPhoneNo());*/
			/*getManagedBean().getScLoa().setOfficerEmail(
					addList.get(0).getPmAddress().getEmail());*/

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");
		
		ScLoiLoa scLoiLoa = this.getManagedBean().getScLoa().getScLoiLoa();
		String selectedSupplierBranchName = this.getManagedBean().getSelectedSupplierBranchName();
		
		if (selectedSupplierBranchName == null) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean().getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl().getSmCompanyBasic();
			
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
					log.info("found hq address");
					
					scLoiLoa.setSupplierBranch("HQ");
					scLoiLoa.setSupplierAddress(smCompanyAddress.getSmAddress());
					scLoiLoa.setSupplierContact(supplierHQPersonnel.getName());
					scLoiLoa.setSupplierPhoneNo(smCompanyBasic.getPhoneNo());
					scLoiLoa.setSupplierFaxNo(smCompanyBasic.getFaxNo());
					scLoiLoa.setSupplierMobNo(supplierHQPersonnel.getMobileNo());
					scLoiLoa.setSupplierEmail(supplierHQPersonnel.getEmail());
					
					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean().getBranchsList();
			
			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(selectedSupplierBranchName)) {
					log.info("found branch address");
					
					scLoiLoa.setSupplierBranch(supplierBranch.getBranchName());
					scLoiLoa.setSupplierAddress(supplierBranch.getSmAddress());
					scLoiLoa.setSupplierContact(supplierBranch.getSmPersonnel().getName());
					scLoiLoa.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoiLoa.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoiLoa.setSupplierMobNo(supplierBranch.getSmPersonnel().getMobileNo());
					scLoiLoa.setSupplierEmail(supplierBranch.getSmPersonnel().getEmail());
					
					break;
				}
			}	
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
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

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

		
			// Setting the pm user id for LOI details
			UserReqDTO userReqDTO = new UserReqDTO();
			/*userReqDTO.setUserId(getManagedBean().getScLoa().getApproverUser()
					.getUserId());*/

			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);

			PmUser user = userResDTO.getPmUser();
			System.out.println("User id and Name" + user.getUserId() + "|"
					+ user.getUserName());
			//scLoa.setApproverUser(user);
			//scLoa.setContractSignerUser(user);

			// Setting the pm status for LOI details
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			//scLoa.setPmStatus(status);

			List<ScPerformanceBond> bondDtls = this.getManagedBean().getScBondDtlList();
			//scLoa.setScBondDtls(bondDtls);
			
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
	
	public void contractTypeChanged() {
		debugLogger("contractTypeChanged");
		
		ScLoa scLoa = getManagedBean().getScLoa();
		
		IParameterSO parameterSO = null;
		try {			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(scLoa.getContractType().getParameterId());
			
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setContractType(resDTO.getPmParameter());
			
		} finally {
			parameterSO = null;
		}
		
		String contractType = scLoa.getContractType().getParameterCode();
		
		if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
			IOrgProfileSO orgProfileSO;
			try {
				orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.MINISTRY.value());
				
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
				
				getManagedBean().setAgencyProfileList(resDTO.getOrgProfileList());

			} finally {
				orgProfileSO = null;
			}	
		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			List<String> orgTypes = new ArrayList<String>();
			
			orgTypes.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
			orgTypes.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
			orgTypes.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
			
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmParameter.parameterCode", 
					SearchOperator.OPERATOR_IN.value(), 
					orgTypes));
			
			List<PmParameterDesc> agencyList = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			
			getManagedBean().setAgencyList(agencyList);
		}
		
	}

	public void contractAdminChanged() {
		debugLogger("contractAdminChanged");
		
		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			ScLoa scLoa = getManagedBean().getScLoa();
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setOrgType(scLoa.getContractType().getParameterCode());
			reqDTO.setOrgProfileId(scLoa.getContractAgency().getOrgProfileId());
			
			UserResDTO resDTO = userSO.retrieveUserListByOrgProfileAndContractType(reqDTO);
			
			getManagedBean().setContractAdminList(resDTO.getUserList());

		} finally {
			userSO = null;
		}
	}

	public void handleContractPTJ() {
		debugLogger("handleContractPTJ");
		//TODO
		/*IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

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
		}*/

	}

	public void handleFileAttachment(FileUploadEvent event) {
		debugLogger("handleFileAttachment");
		ScPerformanceBond bondDtl = getManagedBean().getScBondDtl();
	//	ScBondDtlAttachment attachment = bondDtl.getScBondDtlAttachment();
		/*if (null == attachment) {
			attachment = new ScBondDtlAttachment();
			attachment.setScBondDtl(bondDtl);
		}*/

		//uploadAttachment(event.getFile(), attachment);

		//bondDtl.setScBondDtlAttachment(attachment);

	}
}

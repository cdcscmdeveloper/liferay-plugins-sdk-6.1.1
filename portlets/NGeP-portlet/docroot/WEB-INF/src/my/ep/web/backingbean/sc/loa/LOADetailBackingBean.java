package my.ep.web.backingbean.sc.loa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.constant.MessageConst;
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
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
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
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
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

	public String editLOAAction(ScLoa scLoa) {
		debugLogger("editLOAAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			String language = new FacesScopes().getUserLocale().getLanguage();
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setLanguageCode(language);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			scLoa = resDTO.getScLoa();
			
			getManagedBean().setScLoa(scLoa);
			
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());
			
			getManagedBean().setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());
			
			handleBranchDetailsChanged();
			
			if (scLoa.getContractType().getParameterId() != null) {
				contractTypeChanged();	
			}
			
			ScLoaWitness loaAgencyWitness = null;
			for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
				if (scLoaWitness.getPartyType() == 'O') {
					loaAgencyWitness = scLoaWitness;
				}
			}
			getManagedBean().setLoaAgencyWitness(loaAgencyWitness);

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
	
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");
		
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scQtDeduction.setDeductionType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");
		
		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();
		
		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();
			
			scLoa.setScQtDeductions(scQtDeductions);
		}
		
		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setDeductionType(new PmParameter());
		
		scQtDeductions.add(scQtDeduction);
	}
	
	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");
		
		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();
		
		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();
			
			selectedScQtDeduction.setScQtDeductionClauses(scQtDeductionClauses);
		}
		
		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(getManagedBean().getSelectedScQtDeduction());
		scQtDeductionClauses.add(scQtDeductionClause);
	}
	
	public void addLoaContentAction() {
		debugLogger("addLoaContentAction");
		
		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		
		if (scLoaReports == null) {
			scLoaReports = new ArrayList<ScLoaReport>();
			
			scLoa.setScLoaReports(scLoaReports);
		}
		
		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(scLoa);
		scLoaReport.setScLoaReportAttachment(new ScLoaReportAttachment());
		
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scLoaReport.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	
		scLoaReports.add(scLoaReport);
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		if (event.getFile() != null) {
			FacesScopes facesScopes = new FacesScopes();
			ScLoaReport selectedScLoaReport = this.getManagedBean().getSelectedScLoaReport();
			ScLoaReportAttachment attachment = selectedScLoaReport.getScLoaReportAttachment();
			if (null == attachment) {
				attachment = new ScLoaReportAttachment();
				attachment.setScLoaReport(selectedScLoaReport);
			}
			attachment.setUser(facesScopes.getUserSessionBean());
			
			uploadAttachment(event.getFile(), attachment);
			
			selectedScLoaReport.setScLoaReportAttachment(attachment);
		}
	}
	
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");
		
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport.getScLoaReportAttachment();
		
		String definitionType = scLoaReport.getDefinationType().getParameterCode();
		
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			// TODO - generate documents
		}
		
		downloadFile(scLoaReportAttachment);	
	}
	
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa().getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();
		
		for (ScLoaReport scLoaReport : scLoaReports) {
			scAttachments.add(scLoaReport.getScLoaReportAttachment());
		}
		
		downloadAllFiles("NGep-LOA-Report", scAttachments);
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
	
	public String editLOAAction1(ScLoiLoa scLoiLoa) {
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

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOADtl(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			
			getManagedBean().setScLoa(scLoa);
			
			getManagedBean().setScQtRecommendDtls(resDTO.getScQtRecommendDtls());
			
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
		
		ScLoa scLoa = this.getManagedBean().getScLoa();
		String selectedSupplierBranchName = this.getManagedBean().getSelectedSupplierBranchName();
		
		if (selectedSupplierBranchName == null) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean().getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl().getSmCompanyBasic();
			
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
					log.info("found hq address");
					
					scLoa.setSupplierBranch("HQ");
					scLoa.setSupplierAddress(smCompanyAddress.getSmAddress());
					scLoa.setSupplierContact(supplierHQPersonnel.getName());
					scLoa.setSupplierPhoneNo(smCompanyBasic.getPhoneNo());
					scLoa.setSupplierFaxNo(smCompanyBasic.getFaxNo());
					scLoa.setSupplierMobNo(supplierHQPersonnel.getMobileNo());
					scLoa.setSupplierEmail(supplierHQPersonnel.getEmail());
					
					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean().getBranchsList();
			
			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(selectedSupplierBranchName)) {
					log.info("found branch address");
					
					scLoa.setSupplierBranch(supplierBranch.getBranchName());
					scLoa.setSupplierAddress(supplierBranch.getSmAddress());
					scLoa.setSupplierContact(supplierBranch.getSmPersonnel().getName());
					scLoa.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoa.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoa.setSupplierMobNo(supplierBranch.getSmPersonnel().getMobileNo());
					scLoa.setSupplierEmail(supplierBranch.getSmPersonnel().getEmail());
					
					break;
				}
			}	
		}
	}
	
	public void handleBankDetailsChanged() {
		debugLogger("handleBankDetailsChanged");
		
		Long selectedBankId = this.getManagedBean().getSelectedBankId();
		ScLoa scLoa = this.getManagedBean().getScLoa();
		
		if (selectedBankId != null) {
			List<SmSupplierBank> bankList = this.getManagedBean().getBankList();
			
			for (SmSupplierBank supplierBank : bankList) {
				if (supplierBank.getSupplierBankId().equals(selectedBankId)) {
					log.info("found supplier bank");
					
					scLoa.setBankAccNo(supplierBank.getAccountNo());
					scLoa.setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setBankAddress(supplierBank.getSmAddress());
					
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
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			
			ScLoa scLoa = this.getManagedBean().getScLoa();
			scLoa.setIssueDate(new Date());

			// Date date = new Date();
			log.info("Issue Date : " + scLoa.getIssueDate());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));
			log.info("Response Due Date : " + scLoa.getAckDueDate());
			
			if (scLoa.isBrandNew()) {
				List<ScLoaWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoaWorkFlowStatus>();
				ScLoaWorkFlowStatus workFlowStatus = new ScLoaWorkFlowStatus();
				workFlowStatus.setScLoa(scLoa);
				workFlowStatus.setPmStatus(scLoa.getPmStatus());
				workFlowStatus.setCurrent(true);
				workFlowStatuses.add(workFlowStatus);
				scLoa.setScLoaWorkFlowStatuses(workFlowStatuses);	
			}

			reqDTO.setScLoa(scLoa);

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);
			
			scLoa = resDTO.getScLoa();

			this.getManagedBean().setScLoa(scLoa);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_INFO,
							null,
							MessageUtils.getMessage(
									MessageConst.INFO_700004,
									new String[] {
											getParameterDesc(
													ParameterTypeConst.DOCUMENT_TYPES,
													ParameterConst.DOCTYPE_LETTER_OF_AWARD),
											scLoa.getLoaNo() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		} finally {
			qtAwardSO = null;
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
			String contractType = scLoa.getContractType().getParameterCode();
			List<PmUser> userList = null;
			
			if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
				reqDTO.setOrgProfileId(scLoa.getContractAgency().getOrgProfileId());
				
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);	
				
				userList = resDTO.getUserList();
				
			} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
				// TODO  - filter user based on central contract.
			}
			
			getManagedBean().setContractAdminList(userList);

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

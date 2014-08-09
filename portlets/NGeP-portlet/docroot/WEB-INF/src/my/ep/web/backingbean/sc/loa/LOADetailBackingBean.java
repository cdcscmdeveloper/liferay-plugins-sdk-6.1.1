package my.ep.web.backingbean.sc.loa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
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
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

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
			
			populateLOADetails(resDTO);
			
		} finally {
			qtAwardSO = null;
		}
				
		return QTConstants.LOA_PAGE;
	}
	
	public void editSupplierLOAAction(Long loaId) {
		debugLogger("editSupplierLOAAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveSupplierLOADtl(reqDTO);
			
			ScLoa scLoa = resDTO.getScLoa();
			getManagedBean().setScLoa(scLoa);
			
			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());
			if (!scLoa.isBrandNew()) {
				getManagedBean().setSelectedBankId(getSelectedBankIdByScLoa(scLoa));	
			}
			
			// Supplier Witness
			ScLoaWitness loaSupplierWitness = null;
			List<ScLoaWitness> scLoaWitnesses = scLoa.getScLoaWitnesses();
			for (ScLoaWitness scLoaWitness : scLoaWitnesses) {
				String partyType = scLoaWitness.getPartyType();
				
				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
					loaSupplierWitness = scLoaWitness;
				}
			}
			if (loaSupplierWitness == null) {
				loaSupplierWitness = new ScLoaWitness();
				loaSupplierWitness.setPartyType(ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER);
				loaSupplierWitness.setScLoa(scLoa);
			
				scLoaWitnesses.add(loaSupplierWitness);
			}
			getManagedBean().setLoaSupplierWitness(loaSupplierWitness);
			
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			
			generateDeclarationFromVelocity(scLoa);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.SUPPLIER_LOA_PAGE);
			
		} catch (Exception e) {
			handleException(e);
		} finally {
			qtAwardSO = null;
		}
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");
		
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			
			Long loaId = Long.valueOf(dpDataType.getAwardId());
			ScLoa scLoa = new ScLoa();
			scLoa.setLoaId(loaId);
			editLOAAction(scLoa);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOA_PAGE);	
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");
		
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			
			Long loaId = Long.valueOf(dpDataType.getAwardId());
			
			editSupplierLOAAction(loaId);
			
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	private void populateLOADetails(AwardSupplierResDTO resDTO) {
		ScLoa scLoa = resDTO.getScLoa();
		
		getManagedBean().setScLoa(scLoa);

		// Supplier Branch
		getManagedBean().setSelectedSupplierBranchName(scLoa.getSupplierBranch());
		getManagedBean().setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
		getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
		if (scLoa.isBrandNew()) {
			handleBranchDetailsChanged();	
		}
		
		if (scLoa.getContractType().getParameterId() != null) {
			contractTypeChanged();	
		}
		
		// Agency & Supplier Witness
		ScLoaWitness loaAgencyWitness = null;
		ScLoaWitness loaSupplierWitness = null;
		for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
			String partyType = scLoaWitness.getPartyType();
			
			if (ParameterConst.WITNESS_PARTY_TYPE_AGENCY.equals(partyType)) {
				loaAgencyWitness = scLoaWitness;
			} else if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
				loaSupplierWitness = scLoaWitness;
			}
		}
		getManagedBean().setLoaAgencyWitness(loaAgencyWitness);
		getManagedBean().setLoaSupplierWitness(loaSupplierWitness);
		
		// Issue & Acknowledge Date  
		scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));
		
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
		
		// Contract Signer
		PmUser contractSignerUser = scLoa.getContractSigner();
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}
		getManagedBean().setContractSignerUser(contractSignerUser);
		getManagedBean().setContractSignerList(resDTO.getContractSignerList());
		
		// Approver
		ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
		PmUser approverUser = null;
		if (scLoaApproval != null) {
			approverUser = scLoaApproval.getApprover();
			
			if (!QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa.getPmStatus().getStatusId().longValue())) {
				getManagedBean().setScLoaApproval(scLoa.getScLoaApproval());	
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		getManagedBean().setApproverList(resDTO.getApproverList());
		
		// Remarks
		scLoa.setScLoaRemarks(initializeRemarks(
				scLoa, scLoa.getScLoaRemarks(), ScLoaRemark.class));

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
			scQtDeduction.setScQtDeductionClauses(null);
			
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
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
		scQtDeduction.setScLoa(scLoa);
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
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
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
	
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			String attachmentDocType = getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;
			
			if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(attachmentDocType)) {
				ScLoa scLoa = this.getManagedBean().getScLoa();
				ScLoaAttachment scLoaAttachment = scLoa.getScLoaAttachment();
				if (null == scLoaAttachment) {
					scLoaAttachment = new ScLoaAttachment();
					scLoaAttachment.setScLoa(scLoa);
					
					scLoa.setScLoaAttachment(scLoaAttachment);
				}
				
				scLoaAttachment.setDocNo(scLoa.getLoaNo());
				scAttachment = scLoaAttachment;
				
			} else {
				ScLoaReport selectedScLoaReport = this.getManagedBean().getSelectedScLoaReport();
				ScLoaReportAttachment scLoaReportAttachment = selectedScLoaReport.getScLoaReportAttachment();
				if (null == scLoaReportAttachment) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(selectedScLoaReport);
					
					selectedScLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
				}
				
				scLoaReportAttachment.setDocNo(selectedScLoaReport.getScLoa().getLoaNo());
				scAttachment = scLoaReportAttachment; 
			}
			
			uploadAttachment(event.getFile(), scAttachment);
		}
	}
	
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");
		
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport.getScLoaReportAttachment();
		
		if (scLoaReportAttachment == null) {
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
		}
		
		downloadFile(scLoaReportAttachment);	
	}
	
	private ScLoaReportAttachment generateDocumentAttachment(ScLoaReport scLoaReport) {
		String definitionType = scLoaReport.getDefinationType().getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = null;
		
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoaReport.getPmDocument().getDocDefinitionKey();
				
				scLoaReportAttachment = new ScLoaReportAttachment();
				scLoaReportAttachment.setScLoaReport(scLoaReport);
				scLoaReportAttachment.setDocNo(scLoaReport.getScLoa().getLoaNo());
				
				if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY.equals(docDefinitionKey)) {
					generateContractSummaryPdfFromVelocity((ScAttachment)scLoaReportAttachment, 
							getManagedBean().getScLoa());	
				} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equals(docDefinitionKey)) {
					generateAgreementLetterPdfFromVelocity((ScAttachment)scLoaReportAttachment, 
							getManagedBean().getScLoa());
				} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT.equals(docDefinitionKey)) {
					generateDeclarationPdfFromVelocity((ScAttachment)scLoaReportAttachment, 
							getManagedBean().getScLoa());
				}
				// TODO - generate documents
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
		}
		
		return scLoaReportAttachment;
	}
	
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa().getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();
		
		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport.getScLoaReportAttachment();
			
			if (scLoaReportAttachment == null) {
				scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			}
			
			scAttachments.add(scLoaReportAttachment);
		}
		
		downloadAllFiles("NGeP-LOA-Report", scAttachments);
	}
	
	public void generateContractSummaryPdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception {
		
		attachment.setFileName("contract_summary.pdf");
		populateAttachmentDetails(attachment);
		
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
    	VelocityContext context = new VelocityContext();
    	
    	context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
    	context.put("docNo", "");
    	context.put("title", scLoiLoa.getTitle());
    	context.put("supplierName", scLoiLoa.getSupplierName());
    	context.put("loaAmt", amountDF.format(scLoa.getLoaAmt()));
    	context.put("fulfilmentType", getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));
    	
    	StringBuilder deductionTypes = new StringBuilder();
    	List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();
    	if (scQtDeductions != null) {
    		for (ScQtDeduction scQtDeduction : scQtDeductions) {
        		if (deductionTypes.length() > 0) {
        			deductionTypes.append(",");
        		}
        		deductionTypes.append(getParameterDesc(scQtDeduction.getDeductionType()));
        	}	
    	}
    	context.put("deductionTypes", deductionTypes.toString());
    	
    	context.put("contractDuration", scLoa.getContractDuration());
    	context.put("contractEffDate", dateSDF.format(scLoa.getContractEffDate()));
    	context.put("contractExpDate", dateSDF.format(scLoa.getContractExpDate()));
    	
    	String bondAmtStr = "";
    	if (scLoa.getBondAmt() != null) {
    		bondAmtStr = amountDF.format(scLoa.getBondAmt());	
    	}
    	context.put("bondAmt", bondAmtStr);
    	
    	DocumentGenerator.getInstance().generatePdfFromVelocity(
    			"/sc/loa-contract-summary.vm", 
    			context, 
    			new FileOutputStream(attachment.getFilePath()));
	}
	
	public void generateAgreementLetterPdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception {
		
		attachment.setFileName("agreement_letter.pdf");
		populateAttachmentDetails(attachment);
		
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
    	VelocityContext context = new VelocityContext();
    	
    	IAddressSO addressSO = null;
    	PmAddressType pmAddressType = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			
			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);	
			}
		} finally {
			addressSO = null;
		}
    	context.put("agency", pmOrgProfile.getOrgName());
    	context.put("ministry", pmOrgProfile.getParentOrgProfile().getOrgName());
    	
    	String agencyAddress = "";
    	String agencyPhoneNo = "";
    	String agencyFaxNo = "";
    	if (pmAddressType != null) {
    		PmAddress pmAddress = pmAddressType.getPmAddress();
    		
    		agencyAddress = getFullAddress(pmAddress);
    		agencyAddress = agencyAddress.replaceAll("\r\n", "<br />");
    		agencyPhoneNo = pmAddress.getPhoneNo();
    		agencyFaxNo = pmAddress.getFaxNo();
    	}
    	context.put("agencyAddress", agencyAddress);
    	context.put("agencyPhoneNo", agencyPhoneNo);
    	context.put("agencyFaxNo", agencyFaxNo);
    	context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
    	context.put("loaNo", scLoa.getLoaNo());
    	
    	String issueDate = "";
    	if (scLoa.getIssueDate() != null) {
    		issueDate = dateSDF.format(scLoa.getIssueDate());	
    	}
    	context.put("issueDate", issueDate);
    	
    	context.put("supplierName", scLoiLoa.getSupplierName());
    	String supplierAddress = "";
    	if (scLoa.getSupplierAddress() != null) {
    		supplierAddress = getFullAddress(scLoa.getSupplierAddress());	
    		supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
    	}
    	context.put("supplierAddress", supplierAddress);
    	context.put("procurementMode", "Direct Purchase");	// TODO
    	context.put("title", scLoiLoa.getTitle());
    	context.put("hasLoi", false);	//TODO
    	context.put("bondReq", scLoa.isBondReq());
    	
    	String bondAmt = "";
    	if (scLoa.getBondAmt() != null) {
    		bondAmt = amountDF.format(scLoa.getBondAmt());	
    	}
    	context.put("bondAmt", bondAmt);
    	context.put("bondAmtText", bondAmt);	// TODO
    	
    	String bondPercentage = "";
    	if (scLoa.getBondPercentage() != null) {
    		bondPercentage = percentageDF.format(scLoa.getBondPercentage());	
    	}
    	context.put("bondPercentage", bondPercentage);
    	context.put("bondExpDate", "");	// TODO
    	
    	String bondResDueDays = "";
    	if (scLoa.getBondResDueDate() != null && scLoa.getAckDate() != null) {
    		bondResDueDays = DateUtils.getDaysDifference(scLoa.getBondResDueDate(), scLoa.getAckDate()) + "";	
    	}
    	context.put("bondResDueDays", bondResDueDays);
    	
    	String ackDate = "";
    	if (scLoa.getAckDate() != null) {
    		ackDate = dateSDF.format(scLoa.getAckDate());	
    	}
    	context.put("ackDate", ackDate);
    	
    	context.put("insWarrantyReq", scLoa.isInsWarrantyReq());
    	
    	String contractEffDate = "";
    	if (scLoa.getContractEffDate() != null) {
    		contractEffDate = dateSDF.format(scLoa.getContractEffDate());	
    	}
    	context.put("contractEffDate", contractEffDate);
    	context.put("contractSigner", scLoa.getContractSigner().getUserName());
    	context.put("contractSignerDesignation", scLoa.getContractSigner().getDesignation());
    	
    	DocumentGenerator.getInstance().generatePdfFromVelocity(
    			"/sc/loa-agreement-letter.vm", 
    			context, 
    			new FileOutputStream(attachment.getFilePath()));
	}
	
	public void generateDeclarationPdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception {	    	
    	String termsConditions = generateDeclarationFromVelocity(scLoa);
    	
    	attachment.setFileName("integrity_pact.pdf");
    	populateAttachmentDetails(attachment);
    	
    	DocumentGenerator.getInstance().generatePdf(
    			new FileOutputStream(attachment.getFilePath()), 
    			termsConditions);
	}
	
	private String generateDeclarationFromVelocity(ScLoa scLoa) throws Exception {	
		PmUser pmUser = scLoa.getAckBy();
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
    	VelocityContext context = new VelocityContext();
    	
    	context.put("title", scLoiLoa.getTitle());
    	
    	String userName = "&#60;supplier name&#62;";
    	String icNo = "&#60;ic no&#62;";
    	if (pmUser != null) {
    		userName = pmUser.getUserName();
    		icNo = pmUser.getIdentificationNo();
    	}
    	context.put("userName", userName);
    	context.put("icNo", icNo);
    	
    	context.put("supplierName", scLoiLoa.getSupplierName());
    	context.put("mofNo", "");	// TODO
    	context.put("agency", pmOrgProfile.getOrgName());
    	
    	String termsConditions = DocumentGenerator.getInstance().getHtmlFromVelocity(
    			"/sc/loa-declaration.vm", 
    			context);
    	
    	getManagedBean().setTermsConditions(termsConditions);
    	
    	return termsConditions;
	}
	
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = getManagedBean().getLoaStreamContent();
		
		if (loaStreamContent == null) {
			loaStreamContent = getAttachmentStreamContent(
					getManagedBean().getScLoa().getScLoaAttachment(), CommonConstants.FILE_CONTENT_TYPE_PDF);
			
			getManagedBean().setLoaStreamContent(loaStreamContent);
		} else {
			loaStreamContent.getStream().reset();
		}
		
		return loaStreamContent;
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
		
		if (selectedSupplierBranchName == null || "HQ".equals(selectedSupplierBranchName)) {
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
	
	private Long getSelectedBankIdByScLoa(ScLoa scLoa) {
		Long selectedBankId = null;
		SupplierBankFK loaSupplierBankFK = new SupplierBankFK(scLoa);
		List<SmSupplierBank> bankList = this.getManagedBean().getBankList();
		
		for (SmSupplierBank supplierBank : bankList) {
			if (new SupplierBankFK(supplierBank).equals(loaSupplierBankFK)) {
				log.info("found supplier bank");
				
				selectedBankId = supplierBank.getSupplierBankId();
				
				break;
			}
		}
		
		return selectedBankId;
	}
	
	private class SupplierBankFK {
		private String accountNo;
		private String bankName;
		private String bankBranch;
		
		public SupplierBankFK(SmSupplierBank smSupplierBank) {
			this.accountNo = smSupplierBank.getAccountNo();
			this.bankName = smSupplierBank.getPmFinancialOrg().getFinOrgName();
			this.bankBranch = smSupplierBank.getBankBranch();
		}
		
		public SupplierBankFK(ScLoa scLoa) {
			this.accountNo = scLoa.getBankAccNo();
			this.bankName = scLoa.getBankName();
			this.bankBranch = scLoa.getBankBranch();
		}
		
		public boolean equals(SupplierBankFK other) {
			return getPK(this).equals(getPK(other));
		}

		public int hashCode() {
			int result = 3;
			
			if (this.getAccountNo() != null) {
				result = 31 * result + this.getAccountNo().hashCode();	
			}
			
			if (this.getBankName() != null) {
				result = 31 * result + this.getBankName().hashCode();
			}
			
			if (this.getBankBranch() != null) {
				result = 31 * result + this.getBankBranch().hashCode();
			}
			
			return result;
		}

		/**
		 * @return the accountNo
		 */
		public String getAccountNo() {
			return accountNo;
		}

		/**
		 * @param accountNo the accountNo to set
		 */
		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}

		/**
		 * @return the bankName
		 */
		public String getBankName() {
			return bankName;
		}

		/**
		 * @param bankName the bankName to set
		 */
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		/**
		 * @return the bankBranch
		 */
		public String getBankBranch() {
			return bankBranch;
		}

		/**
		 * @param bankBranch the bankBranch to set
		 */
		public void setBankBranch(String bankBranch) {
			this.bankBranch = bankBranch;
		}
		
		private String getPK(SupplierBankFK fk) {
			return new StringBuilder(fk.getAccountNo() != null ? fk.getAccountNo() : "")
				.append("_").append(fk.getBankName() != null ? fk.getBankName() : "")
				.append("_").append(fk.getBankBranch() != null ? fk.getBankBranch() : "").toString();
		}
		
	}

	/**
	 * Method to save the LOA details
	 */
	public void saveAction() {
		debugLogger("saveAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		
			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();
			
			parseLOADetails(scLoa);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				
				//renewSesionObject();
				
				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
					editSupplierLOAAction(scLoa.getLoaId());
				} else {
					editLOAAction(scLoa);
				}
				
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
			}

		} finally {
			qtAwardSO = null;
		}
	}
	
	/**
	 * Method to save the LOA details
	 */
	public String submitAction() {
		debugLogger("submitAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			ScLoa scLoa = this.getManagedBean().getScLoa();
			
			validateLOADetails(scLoa);
			
			parseLOADetails(scLoa);
			
			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setSubmit(true);
			reqDTO.setSourceId(getManagedBean().getSourceId());

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);
			
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				
				renewSesionObject();
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils.getMessage(
										MessageConst.INFO_700026,
										new String[] {
												getParameterDesc(
														ParameterTypeConst.DOCUMENT_TYPES,
														ParameterConst.DOCTYPE_LETTER_OF_AWARD),
												scLoa.getLoaNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			qtAwardSO = null;
		}
		
		return null;
	}
	
	public String updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		
		IQTAwardSO qtAwardSO = null;
		
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			
			ScLoa scLoa = this.getManagedBean().getScLoa();
			
			validateLOADetails(scLoa);
			
			parseLOADetails(scLoa);
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);
			
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				
				renewSesionObject();
				
				Long statusId = scLoa.getPmStatus().getStatusId().longValue();
				String messageCode = null;
				
				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;	
					} else if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_CONTRACT_SIGNING.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
					messageCode = MessageConst.INFO_700013;
				} else if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}
				
				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD, 
						scLoa.getLoaNo());
			}

		} finally {
			qtAwardSO = null;
		}
		
		return null;	
	}
	
	private void validateLOADetails(ScLoa scLoa) {
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();
		
		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}	
		}
	}
	
	private void parseLOADetails(ScLoa scLoa) {
		if (scLoa.isBrandNew()) {
			scLoa.setIssueDate(new Date());
			
			List<ScLoaWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoaWorkFlowStatus>();
			ScLoaWorkFlowStatus workFlowStatus = new ScLoaWorkFlowStatus();
			workFlowStatus.setScLoa(scLoa);
			workFlowStatus.setPmStatus(scLoa.getPmStatus());
			workFlowStatus.setCurrent(true);
			workFlowStatuses.add(workFlowStatus);
			scLoa.setScLoaWorkFlowStatuses(workFlowStatuses);	
		}
		
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();
		
		if (scLoa.isApprovalReq() && QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)) {
			PmUser approverUser = getManagedBean().getApproverUser();
			
			if (approverUser.getUserId() != null) {
				ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
				
				if (scLoaApproval == null) {
					scLoaApproval = new ScLoaApproval();
					scLoaApproval.setScLoa(scLoa);
					scLoaApproval.setApprover(approverUser);
					
					scLoa.setScLoaApproval(scLoaApproval);						
				} else {
					scLoaApproval.setApprover(approverUser);
				}
			}
		}
		
		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
		}
		
		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null && contractSignerUser.getUserId() != null) {
			scLoa.setContractSigner(contractSignerUser);	
		}
		
		scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
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
		
		contractAgencyChanged();
	}

	public void contractAgencyChanged() {
		debugLogger("contractAgencyChanged");
		
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

	public void contractAdminChanged() {
		debugLogger("contractAdminChanged");
		
		IUserSO userSO;
		ScLoa scLoa = getManagedBean().getScLoa();
		PmUser pmUser = scLoa.getContractAdmin();
		
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(pmUser.getUserId());
			
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			scLoa.setContractAdmin(resDTO.getPmUser());

		} finally {
			userSO = null;
		}

	}
	
	public void contractEffDateChanged() {
		debugLogger("contractEffDateChanged");
		
		ScLoa scLoa = getManagedBean().getScLoa();
		Date contractEffDate = scLoa.getContractEffDate();
		BigDecimal contractDuration = scLoa.getContractDuration();
		
		Calendar contractExpDate = Calendar.getInstance();
		contractExpDate.setTime(contractEffDate);
		contractExpDate.add(Calendar.MONTH, contractDuration.intValue());
		contractExpDate.add(Calendar.DATE, -1);

		scLoa.setContractExpDate(contractExpDate.getTime());
	}
	
	public void approverChanged() {
		debugLogger("approverChanged");
		
		PmUser approverUser = getManagedBean().getApproverUser();
		
		IUserSO userSO = null;
		try {			
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(approverUser.getUserId());
			
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			getManagedBean().setApproverUser(resDTO.getPmUser());
			
		} finally {
			userSO = null;
		}
		
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

package my.ep.web.backingbean.sc.loa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.clause.IClauseSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmClause;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.DirectLOAManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class directLOABackingBean.
 * 
 * @author Sri
 * 
 * @since 15/06/2015
 */
@ManagedBean(name = "directLOABackingBean")
@RequestScoped
public class DirectLOABackingBean extends
SourcingBaseBackingBean<DirectLOAManagedBean> {

	/** The log. */
	public static Logger log = EpLogger.getLogger(DirectLOABackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/**
	 * Instantiates a new LOA detail backing bean.
	 */
	public DirectLOABackingBean() {
		log.info("inside DirectLOABackingBean constructor");
	}

	/**
	 * save the LOA details.
	 * save - save data into database and initiate task for LOA details.
	 * page navigate to loa details page.
	 */
	public void saveAction() {
		debugLogger("saveAction");

		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			if(scLoa.getScQtSchedules() != null && scLoa.getScQtSchedules().size()>0){
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}

			parseLOADetails(scLoa, "SAVE");

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				// renewSesionObject();

				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					editSupplierLOAAction(scLoa.getLoaId());
				} else {
					//editLOAAction(scLoa);
				}
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, scLoa.getLoaNo(), Boolean.FALSE);
			}

		} finally {
			//qtAwardSO = null;
		}
	}

	/**
	 * submit LOA details.
	 * submit - save data into database and BPM process will route to the next respective user.
	 * @param outcome the outcome
	 * @return the string
	 * page navigate to acknowledge page.
	 */
	public String submitAction() {
		debugLogger("submitAction");


		try {
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			validateLOADetails(scLoa, outcome);

			parseLOADetails(scLoa, outcome);
			scLoa.setScLoaWorkFlowStatuses(null);
			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));

			if(scLoa.getScQtSchedules() != null && scLoa.getScQtSchedules().size()>0){
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();
				renewSesionObject();
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_AWARD, scLoa.getLoaNo(), Boolean.TRUE);
			}

		} finally {
			//qtAwardSO = null;
		}
		return null;
	}


	/**
	 * Gets the agreement letter.
	 *
	 * @return the loa stream content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = getManagedBean()
				.getLoaStreamContent();

		/*	if (loaStreamContent == null) {
			ScLoaReport agreementLetterReport = null;
			for (ScLoaReport scLoaReport : getManagedBean().getScLoa()
					.getScLoaReports()) {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();

				if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
						.equals(docDefinitionKey)) {
					agreementLetterReport = scLoaReport;

					break;
				}
			}

			if (agreementLetterReport != null) {
				ScLoaReportAttachment agreementLetterAttachment = generateDocumentAttachment(agreementLetterReport);
				agreementLetterReport
						.setScLoaReportAttachment(agreementLetterAttachment);

				loaStreamContent = getAttachmentStreamContent(
						agreementLetterAttachment,
						CommonConstants.FILE_CONTENT_TYPE_PDF);

				getManagedBean().setLoaStreamContent(loaStreamContent);
			}
		} else {
			loaStreamContent.getStream().reset();
		}*/

		return loaStreamContent;
	}

	/**
	 * Adds the performance bond.
	 */
	public void addPerformanceBond() {

		debugLogger("addPerformanceBond");
		ScPerformanceBond scBondDtl = new ScPerformanceBond();
		try {

			List<ScPerformanceBond> bondDtlList = getManagedBean()
					.getScBondDtlList();
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

	/**
	 * Calculate bond amount.
	 *
	 * @param scLoa the sc loa
	 * @return the big decimal
	 */
	private ScLoa  calcBondAmount(ScLoa scLoa){
		BigDecimal loaAmt  = BigDecimal.ZERO;
		BigDecimal bondAmt  = BigDecimal.ZERO; 
		double totalDuration = 0;
		double loaAmtForBondPercentage = 0;
		double bondPercentage =0;
		if(scLoa.getLoaAmt() != null){
			BigDecimal  lamt  = scLoa.getLoaAmt();
			lamt =  lamt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
			double loaAmount =  lamt.doubleValue();

			if(scLoa.getContractDuration() != null){
				BigDecimal  duration  = scLoa.getContractDuration();
				duration = duration.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				double cduration =  duration.doubleValue();
				if(cduration >0){
					totalDuration = cduration/12;
				}

				if(totalDuration >0){
					loaAmtForBondPercentage = loaAmount/totalDuration;
				}

				if(loaAmtForBondPercentage >0){
					loaAmt  = new BigDecimal(String.valueOf(loaAmtForBondPercentage));
					loaAmt =  loaAmt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				}
			}

			// Get performance bond rule percentage
			IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.BOND_PERCENTAGE);
			ruleReqDTO.setCompareValue(loaAmt.toString());
			RuleResDTO roleResDTO = ruleSO.getRuleValuesByTypeAndCurrentDateRange(ruleReqDTO);

			if (roleResDTO.isSuccessful() && roleResDTO.getRuleValueStrList() != null 
					&& roleResDTO.getRuleValueStrList().size() > 0) {
				String percentage = roleResDTO.getRuleValueStrList().get(0);
				double res = Double.valueOf(percentage);
				bondPercentage = res/100;
				scLoa.setBondPercentage(new BigDecimal(percentage));
			}
			if(bondPercentage >0 && loaAmtForBondPercentage >0){
				double total  = loaAmtForBondPercentage * bondPercentage;
				bondAmt  = new BigDecimal(String.valueOf(total));
				bondAmt  = bondAmt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				scLoa.setBondAmt(bondAmt);
			}
		}
		return scLoa;
	}

	/**
	 * Edits the supplier loa action.
	 * page navigate to loa supplier page.
	 * @param loaId the loa id
	 */
	public void editSupplierLOAAction(Long loaId) {
		debugLogger("editSupplierLOAAction");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveSupplierLOADtl(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			getManagedBean().setScLoa(scLoa);

			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());

			// Supplier Witness
			ScLoaWitness loaSupplierWitness = null;
			List<ScLoaWitness> scLoaWitnesses = scLoa.getScLoaWitnesses();
			for (ScLoaWitness scLoaWitness : scLoaWitnesses) {
				String partyType = scLoaWitness.getPartyType();

				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER
						.equals(partyType)) {
					loaSupplierWitness = scLoaWitness;
				}
			}
			if (loaSupplierWitness == null) {
				loaSupplierWitness = new ScLoaWitness();
				loaSupplierWitness
				.setPartyType(ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER);
				loaSupplierWitness.setScLoa(scLoa);

				scLoaWitnesses.add(loaSupplierWitness);
			}
			getManagedBean().setLoaSupplierWitness(loaSupplierWitness);
			ScQt scQt = resDTO.getScQt();
			if(scQt != null){
				getManagedBean().setScQt(resDTO.getScQt());
			}
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());

			scLoa = calcBondAmount(scLoa);

			if (scLoa.getBondAmt() != null) {
				getManagedBean().setBondAmountWord(
						NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			}

			// loa signer
			if(scLoa.getContractSigner() != null){
				getManagedBean().setContractSignerUser(scLoa.getContractSigner());
			}

			//generateDeclarationFromVelocity(scLoa,scQt);

			getManagedBean().setSelectedSupplierBranchName(
					scLoa.getSupplierBranch());


			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_SUPPLIER_MAIN_PAGE);

		} catch (Exception e) {
			handleException(e);
		} finally {
			//qtAwardSO = null;
		}
	}

	/**
	 * Pre render page navigation to loa supplier page based on task id
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");

		try {
			/*SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loaId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(dpDataType);
			 */
			/*
			 * String[] outComes = new String[]{BpmOutcomeConst.BPM_ACCEPT,
			 * BpmOutcomeConst.BPM_REJECT};
			 * getManagedBean().setOutComes(outComes);
			 * getManagedBean().setTaskId("123");
			 * 
			 * long loaId = 299L;
			 */

			// supplier witness

			if(null == getManagedBean().getLoaSupplierWitness()){
				getManagedBean().setLoaSupplierWitness(new ScLoaWitness());
			}

			editSupplierLOAAction(1561826L);

		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * Freq schedule validation.
	 *
	 * @param scQtSchedule the sc qt schedule
	 */
	private void freqScheduleValidation(ScQtSchedule scQtSchedule) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;

		if(scQtSchedule.getStartYear() == null || scQtSchedule.getStartYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ""});
		}
		if(scQtSchedule.getStartMonth() == null || scQtSchedule.getStartMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.start.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ""});
		}
		if(scQtSchedule.getEndYear() == null || scQtSchedule.getEndYear().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.year",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ""});
		}
		if(scQtSchedule.getEndMonth() == null || scQtSchedule.getEndMonth().isEmpty()){
			throw new EpException(MessageConst.ERROR_721110, new String[] {ResourceBundleUtils.getMessage("lbl.sc.end.month",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), ""});
		}

		ScLoa loa = 	getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();

		Calendar effDate =Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();  

		if(scQtSchedule.getStartYear() != null && scQtSchedule.getStartMonth() != null){
			startDate = Calendar.getInstance();
			startDate.clear();
			startDate.set(Integer.valueOf(scQtSchedule.getStartYear()),Integer.valueOf(scQtSchedule.getStartMonth()), 1, 0,0,0);
		}

		if(scQtSchedule.getEndYear() != null && scQtSchedule.getEndMonth() != null){
			endDate = Calendar.getInstance();
			endDate.clear();
			endDate.set(Integer.valueOf(scQtSchedule.getEndYear()),Integer.valueOf(scQtSchedule.getEndMonth()), Integer.valueOf(getDays(contractExpDate)),0,0,0);
		}

		if(startDate != null && endDate != null){
			if (startDate.after(endDate)) {
				throw new EpException("Start date must be before end date");
			}

			if (endDate.before(startDate)) {
				throw new EpException("End date must be after start date");
			}

			if (!(startDate.getTime().after(contractEffDate)
					&& (startDate.getTime().before(contractExpDate)) || contractEffDate.equals(startDate.getTime()))) {
				throw new EpException(
						"Start date should be in contract validity period.");
			}

			if (!(endDate.getTime().after(contractEffDate)
					&& (endDate.getTime().before(contractExpDate)) || contractExpDate.equals(endDate.getTime()))) {
				throw new EpException(
						"End date should be in contract validity period.");
			}
		}

	}
	/**
	 * @ param Date date
	 * @return days of month
	 */
	private	static	String	getDays(Date date){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		String	days	=	String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		return days;
	}

	/**
	 * Deduction type change.
	 * Deduction Clause will show when deduction type changes
	 * @param scQtDeduction the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");

		IParameterSO parameterSO = null;
		IClauseSO clauseSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			clauseSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IClauseSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scQtDeduction.setDeductionType(resDTO.getPmParameter());
			getClausesTitle(scQtDeduction);
			ScQtDeductionClause deductionClause = null;
			List<ScQtDeductionClause> deductionClauseList = new ArrayList<ScQtDeductionClause>();
			if (!ParameterConst.DEDUCTION_TYPE_OTHERS
					.equalsIgnoreCase(scQtDeduction.getDeductionType()
							.getParameterCode())) {
				ClauseReqDTO clauseReqDTO = new ClauseReqDTO();
				clauseReqDTO.setDeductionType(scQtDeduction.getDeductionType()
						.getParameterCode());
				ClauseResDTO clauseResDTO = clauseSO
						.retrieveClausesByDeductionType(clauseReqDTO);
				if (clauseResDTO.getResultBean() != null) {
					List<PmClause> pmClauseList = clauseResDTO.getResultBean()
							.getResultList();
					for (PmClause pmClause : pmClauseList) {
						deductionClause = new ScQtDeductionClause();
						deductionClause.setPmClause(pmClause);
						deductionClause.setScQtDeduction(scQtDeduction);
						deductionClause.setClauseDesc(pmClause.getClauseDesc());
						deductionClauseList.add(deductionClause);
					}
					scQtDeduction.setScQtDeductionClauses(deductionClauseList);
				} else {
					scQtDeduction.setScQtDeductionClauses(null);
				}
			} else {
				scQtDeduction.setScQtDeductionClauses(null);
			}

			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Gets the clauses title.
	 *
	 * @param deduction the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction){
		StringBuilder title  = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if(deduction != null && deduction.getDeductionType() != null){
			title.append(getParameterDesc(deduction.getDeductionType()));
			title.append(">"); 
			title.append(" ");
		}

		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.deduction.clauses",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setClausesTitle(title.toString());
	}

	/**
	 * Adds the deduction payment in deduction payment.
	 */
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

	/**
	 * Adds the deduction clause in deduction payment.
	 */
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

	/**
	 * Delete deduction payment deduction payment.
	 */
	public void deleteDeductionPaymentAction() {
		debugLogger("deleteDeductionPaymentAction");
		if (getManagedBean().getScLoa().getScQtDeductions() != null
				&& getManagedBean().getSelectedScQtDeduction() != null) {
			getManagedBean().getScLoa().getScQtDeductions()
			.remove(getManagedBean().getSelectedScQtDeduction());
		}
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		ScQtDeduction scQtDeduction = getManagedBean().getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean().getSelectedDeductionClauses();
		if(clauses != null && clauses.size()>0){
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Handle file upload.
	 *
	 * @param event the event
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			String attachmentDocType = getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;

			if (ParameterConst.DOCTYPE_LETTER_OF_AWARD
					.equals(attachmentDocType)) {
				ScLoa scLoa = this.getManagedBean().getScLoa();
				ScLoaAttachment scLoaAttachment = scLoa.getScLoaAttachment();
				if (null == scLoaAttachment) {
					scLoaAttachment = new ScLoaAttachment();
					scLoaAttachment.setScLoa(scLoa);

					scLoa.setScLoaAttachment(scLoaAttachment);
				}

				scLoaAttachment.setDocNo(scLoa.getLoaNo());
				scLoaAttachment.setScLoa(scLoa);
				scAttachment = scLoaAttachment;

			} else {
				ScLoaReport selectedScLoaReport = this.getManagedBean()
						.getSelectedScLoaReport();
				ScLoaReportAttachment scLoaReportAttachment = selectedScLoaReport
						.getScLoaReportAttachment();
				if (null == scLoaReportAttachment) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(selectedScLoaReport);

					selectedScLoaReport
					.setScLoaReportAttachment(scLoaReportAttachment);
				}

				scLoaReportAttachment.setDocNo(selectedScLoaReport.getScLoa()
						.getLoaNo());
				scLoaReportAttachment.setScLoaReport(selectedScLoaReport);
				scAttachment = scLoaReportAttachment;
			}

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Handle branch details changed.
	 *  shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		String selectedSupplierBranchName = this.getManagedBean()
				.getSelectedSupplierBranchName();

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
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(
						selectedSupplierBranchName)) {
					log.info("found branch address");

					scLoa.setSupplierBranch(supplierBranch.getBranchName());
					scLoa.setSupplierAddress(supplierBranch.getSmAddress());
					scLoa.setSupplierContact(supplierBranch.getSmPersonnel()
							.getName());
					scLoa.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoa.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoa.setSupplierMobNo(supplierBranch.getSmPersonnel()
							.getMobileNo());
					scLoa.setSupplierEmail(supplierBranch.getSmPersonnel()
							.getEmail());

					break;
				}
			}
		}
	}

	/**
	 * Handle bank details changed.
	 * shows the supplier address details for selected branch and bank.
	 */
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
					scLoa.setBankName(supplierBank.getPmFinancialOrg()
							.getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setBankAddress(supplierBank.getSmAddress());

					break;
				}
			}
		}
	}


	/**
	 * Validate loa details.
	 *
	 * @param scLoa the sc loa
	 * @param outcome the outcome
	 */
	private void validateLOADetails(ScLoa scLoa, String outcome) {
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_ACCEPT.equals(outcome)) {

			if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}
		}
	}

	/**
	 * Parses the loa details.
	 *
	 * @param scLoa the sc loa
	 * @param outcome the outcome
	 */
	private void parseLOADetails(ScLoa scLoa, String outcome) {
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

		if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)) {
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
		} else if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			} else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REVISED.value();
			}

			scLoaApproval.setApproverAction(action);
			scLoaApproval.setApproverActionDate(new Date());

			scLoa.setScLoaApproval(scLoaApproval);
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			scLoa.setBondResDueDate(new Date());
		} else {
			//saveScheduleList();
			saveProgressivePaymntList();

			scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoa.setContractSigner(contractSignerUser);
		}

		// supplier witness details
		/*if(scLoa.getScLoaWitnesses() != null && scLoa.getScLoaWitnesses().size()>0){
			List<ScLoaWitness> witnesses = scLoa.getScLoaWitnesses();
			ScLoaWitness  loaWitness = getManagedBean().getLoaSupplierWitness();
			if(loaWitness != null){
				int index  = witnesses.indexOf(loaWitness);
				scLoa.getScLoaWitnesses().set(index, loaWitness);	
			}
		}*/
	}

	/**
	 * Contract type changed.
	 * shows Agency based on contract type changes.
	 */
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
			try {

				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				reqDTO.setScLoa(scLoa);

				AwardSupplierResDTO resDTO = getQtAwardSO()
						.retrieveMinistryAgencyList(reqDTO);

				getManagedBean().setAgencyProfileDDList(
						resDTO.getAgencyProfileDDList());

				if(scLoa.getScLoiLoa().getPmOrgProfile() != null){
					boolean found = false;
					for(Object[] object : resDTO.getAgencyProfileDDList()){
						if(object[0].toString().equals(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId().toString())){
							found = true;
							break;
						}
					}
					if(found){
						scLoa.setContractAgency(scLoa.getScLoiLoa().getPmOrgProfile());
					}
				}


			} finally {
				//qtAwardSO = null;
			}
		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			List<String> orgTypes = new ArrayList<String>();

			orgTypes.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
			orgTypes.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
			orgTypes.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), orgTypes));

			List<PmParameterDesc> agencyList = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(
							ParameterTypeConst.ORGANISATION_TYPES, searchBean);

			getManagedBean().setAgencyList(agencyList);
		}

		contractAgencyChanged();
		//checkAgreementMandatory();
	}

	/**
	 * Contract agency changed.
	 * shows Contract Administrator based on agency changes.
	 */
	public void contractAgencyChanged() {
		debugLogger("contractAgencyChanged");

		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			ScLoa scLoa = getManagedBean().getScLoa();
			String contractType = scLoa.getContractType().getParameterCode();
			List<PmUser> userList = null;

			if (scLoa.getContractAgency() != null || getManagedBean().getSelectedContractAgency() !=null) {
				if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
					reqDTO.setOrgProfileId(scLoa.getContractAgency()
							.getOrgProfileId());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();

				} else if (ParameterConst.CONTRACT_TYPE_CENTRAL
						.equals(contractType)) {
					// TODO - filter user based on central contract.
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.BPK_CCA.value());
					reqDTO.setOrgType(getManagedBean().getSelectedContractAgency());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();
				}
			}

			getManagedBean().setContractAdminList(userList);

		} finally {
			userSO = null;
		}
	}

	/**
	 * Contract admin changed.
	 */
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

	/**
	 * Contract eff date changed.
	 *  calculate Completion Expiry Date based on Completion Period.
	 */
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

	/**
	 * Approver changed.
	 */
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

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		PmUser contractSigner = getManagedBean().getContractSignerUser();
		if(contractSigner != null){
			if(getManagedBean().getContractSignerList() != null && getManagedBean().getContractSignerList().size()>0 ){
				List<PmUser> contractSignerList  = getManagedBean().getContractSignerList();
				for (PmUser user : contractSignerList) {

					if(contractSigner.getUserId().equals(user.getUserId())){
						getManagedBean().getScLoa().setLoaSignerDesig(user.getDesignation());
					}
				}
			}
		}
	}


	/**
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {

		try {

			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
					.getScQtSchedules();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtSchedule>();
			}

			if (freqSchedule.size() > this.getManagedBean().getTotalFrequency()) {

				List<ScQtSchedule> localSchedule = new ArrayList<ScQtSchedule>();
				for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {
					localSchedule.add(freqSchedule.get(j));

				}
				this.getManagedBean().getScLoa()
				.setScQtSchedules(localSchedule);
			} else {
				for (int i = freqSchedule.size(); i < this.getManagedBean()
						.getTotalFrequency(); i++) {

					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setStartYear(String.valueOf(year));
					schedule.setEndYear(String.valueOf(year));
					schedule.setScheduleNo(BigDecimal.valueOf(i + 1));
					PmParameter parameter = this.getParameterByTypeAndCode(ParameterTypeConst.SCHEDULE_TYPE, ParameterConst.SCHEDULE_TYPE_PERIODIC);
					schedule.setScheduleType(parameter);					
					setUserDetails(schedule);
					freqSchedule.add(schedule);
				}
				this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);
			}

		} finally {

		}
	}

	/**
	 * Gets the year details.
	 * Shows start month and end month drop downs based on Total Frequency.
	 * @return the year details
	 */
	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		FacesScopes face = new FacesScopes();
		try {
			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));
			selectItems.add(new SelectItem(1, getMonth(1)));
			selectItems.add(new SelectItem(2, getMonth(2)));
			selectItems.add(new SelectItem(3, getMonth(3)));
			selectItems.add(new SelectItem(4, getMonth(4)));
			selectItems.add(new SelectItem(5, getMonth(5)));
			selectItems.add(new SelectItem(6, getMonth(6)));
			selectItems.add(new SelectItem(7, getMonth(7)));
			selectItems.add(new SelectItem(8, getMonth(8)));
			selectItems.add(new SelectItem(9, getMonth(9)));
			selectItems.add(new SelectItem(10,getMonth(10)));
			selectItems.add(new SelectItem(11,getMonth(11)));
			selectItems.add(new SelectItem(12,getMonth(12)));

			this.getManagedBean().setYearList(selectItems);
		} finally {

		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the month.
	 * 
	 * @param monthId the month id
	 * @return the month
	 */
	private String getMonth(int monthId){
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM"); 
		String month = formatter.format(fullMonth.getTime());

		return month;
	}

	/**
	 * Calculate start month.
	 *
	 * @param event the event
	 */
	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	/**
	 * Gets the years display dtls.
	 * Shows start year and end year drop downs based on Total Frequency.
	 * @return the years display dtls
	 */
	public List<SelectItem> getYearsDisplayDtls() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ScLoa loa = 	getManagedBean().getScLoa();

		try {
			//int year = Calendar.getInstance().get(Calendar.YEAR);
			Calendar calendar = Calendar.getInstance(); 
			int effYear = 0;
			int expYear = 0;
			if(loa != null){

				if(loa.getContractEffDate() != null){
					calendar.setTime(loa.getContractEffDate());  
					effYear = calendar.get(Calendar.YEAR);
				}

				if(loa.getContractExpDate() != null){
					calendar.setTime(loa.getContractExpDate());  
					expYear = calendar.get(Calendar.YEAR);
				}

				int noOfYears = expYear - effYear;
				selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));

				for (int i = 0; i <= noOfYears +1; i++) {

					if(i == 0){
						selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
					}

					if(noOfYears == 0){
						break;
					}
					effYear = effYear+1;

					if(effYear == expYear){
						selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
						break;
					}

					selectItems.add(new SelectItem(effYear, String.valueOf(effYear)));
				}
			}

			/*
			 * selectItems.add(new SelectItem(year, String.valueOf(year))); for
			 * (int i = 0; i < 20; i++) { year = year + 1; selectItems.add(new
			 * SelectItem(year, String.valueOf(year))); }
			 */

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Calculate schedule months.
	 * Calculating start month , start year , end month and end year to display total months.
	 * @param schedule the schedule
	 * @param startMonthAction the start month action
	 */
	public void calculateScheduleMonths(ScQtSchedule schedule, Boolean startMonthAction) {

		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, diffYear = 0, count = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
				endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
						.getEndMonth()) : null;
				startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
						.getStartYear()) : null;
				endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
						.getEndYear()) : null;

				if (startMonthAction !=null && startMonthAction && startMonth != null) {
					endMonth = startMonth;
					endYear = startYear;
					schedule.setEndMonth(endMonth.toString());
					if(endYear != null){
						schedule.setEndYear(endYear.toString());
					}

				}

				if (startMonth != null && endMonth != null && startYear != null
						&& endYear != null && endYear >= startYear) {
					if (!endYear.equals(startYear)) {
						diffYear = endYear - startYear;
					}
					for (int i = startMonth + 1; i <= endMonth + 1 + (diffYear * 12); i++) {
						count++;
					}
					schedule.setTotalMonths(new BigDecimal(count));
				} else {
					schedule.setTotalMonths(BigDecimal.ZERO);
				}

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);
				this.getManagedBean().setScheduleList(freqSchedule);
	}

	/**
	 * Gets the deduction item type dropdown list.
	 *
	 * @return the deduction item type dropdown list
	 */
	public SelectItem[] getDeductionItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.DEDUCTION_TYPE, searchBean);
	}

	/**
	 * Adds the progressive details.
	 */
	public void addProgressiveDetails() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScQtProgressivePayment> progressive = this.getManagedBean()
				.getProgressivePaymentList();

		if (null == progressive) {
			progressive = new ArrayList<ScQtProgressivePayment>();

		}

		if (progressive.size() > this.getManagedBean()
				.getTotalProgressivePayment()) {

			List<ScQtProgressivePayment> progressiveList = new ArrayList<ScQtProgressivePayment>();
			for (int j = 0; j < this.getManagedBean()
					.getTotalProgressivePayment(); j++) {
				progressiveList.add(progressive.get(j));

			}
			this.getManagedBean().setProgressivePaymentList(progressiveList);
		} else {
			for (int i = progressive.size(); i < this.getManagedBean()
					.getTotalProgressivePayment(); i++) {

				ScQtProgressivePayment payment = new ScQtProgressivePayment();
				payment.setStartYear(String.valueOf(year));
				payment.setEndYear(String.valueOf(year));
				payment.setPhaseNo(BigDecimal.valueOf(i + 1));
				payment.setScLoa(scLoa);
				// payment.setScheduleType(new BigDecimal(1));

				setUserDetails(payment);
				progressive.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressive);
		}
	}

	/**
	 * Save progressive paymnt list.
	 */
	private void saveProgressivePaymntList() {
		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getProgressivePaymentList();
		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
			String startMonth = "";
			String endmonth = "";
			for (ScQtProgressivePayment payment : progressivePayemnt) {
				if (payment.getStartMonth().equalsIgnoreCase("0")) {
					startMonth = "01";
				} else if (payment.getStartMonth().equalsIgnoreCase("1")) {
					startMonth = "02";
				} else if (payment.getStartMonth().equalsIgnoreCase("2")) {
					startMonth = "03";
				} else if (payment.getStartMonth().equalsIgnoreCase("3")) {
					startMonth = "04";
				} else if (payment.getStartMonth().equalsIgnoreCase("4")) {
					startMonth = "05";
				} else if (payment.getStartMonth().equalsIgnoreCase("5")) {
					startMonth = "06";
				} else if (payment.getStartMonth().equalsIgnoreCase("6")) {
					startMonth = "07";
				} else if (payment.getStartMonth().equalsIgnoreCase("7")) {
					startMonth = "08";
				} else if (payment.getStartMonth().equalsIgnoreCase("8")) {
					startMonth = "09";
				} else if (payment.getStartMonth().equalsIgnoreCase("9")) {
					startMonth = "10";
				} else if (payment.getStartMonth().equalsIgnoreCase("10")) {
					startMonth = "11";
				} else if (payment.getStartMonth().equalsIgnoreCase("11")) {
					startMonth = "12";
				}
				if (payment.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				} else if (payment.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				} else if (payment.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}
				else if (payment.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				} else if (payment.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				} else if (payment.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}
				else if (payment.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				} else if (payment.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				} else if (payment.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				} else if (payment.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				} else if (payment.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				} else if (payment.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}
				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/" + startMonth + "/"
						+ payment.getStartYear();
				String toDate = date + "/" + endmonth + "/"
						+ payment.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						payment.setFromDate(df.parse(fromdate));
						payment.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getManagedBean().getScLoa().getScQtProgPayments();

				paymentList.add(payment);
			}
			getManagedBean().getScLoa().setScQtProgPayments(paymentList);

		} else {
			getManagedBean().getScLoa().setScQtProgPayments(null);
		}
	}

	/**
	 * Pre render page navigation to direct nego or loa
	 */
	public void preRenderDirectNegoView() {
		debugLogger("preRenderDirectNegoView");
		renewSesionObject();
		IStatusSO statusSO = null;
		ScLoa scLoa = new ScLoa();
		PmParameter pmParameter = new PmParameter();
		scLoa.setContractType(pmParameter);

		ScLoiLoa  scLoiLoa = new ScLoiLoa();
		scLoiLoa.setSmSupplier(new SmSupplier());
		scLoa.setScLoiLoa(scLoiLoa);
		scLoa.setContractAdmin(new PmUser());
		scLoa.setPmOrgProfile(new PmOrgProfile());

		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(QTStatusConst.LOA_PENDING_SUBMISSION);
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			scLoa.setPmStatus(statusResDTO.getPmStatus());
		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			statusSO = null;
		}


		//generate loa number
		scLoa.setLoaNo(getQtAwardSO().generateDirectLoaNo());

		getManagedBean().setScLoa(scLoa);

		// witness
		ScLoaWitness scLoaWitness = new ScLoaWitness();
		getManagedBean().setLoaAgencyWitness(scLoaWitness);

		// award details
		getManagedBean().getTplSpecMaster().setItemOriginCode(new PmParameter());


		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
	}

	/**
	 * Mof no listener.
	 * Gets supplier, branch and adress details based on mof nor.
	 * @param mofAccount the mof account
	 */
	public void mofNoListener(SmMofAccount mofAccount) {
		debugLogger("mofNoListener");

		getManagedBean().getScLoa().getScLoiLoa().setSmSupplier(mofAccount.getSmSupplier());
		getManagedBean().getScLoa().getScLoiLoa().setMofNo(mofAccount.getMofNo());
		getManagedBean().getScLoa().getScLoiLoa().setSupplierName(mofAccount.getSmSupplier().getCompanyName());

		AwardSupplierReqDTO reqDTO  = new AwardSupplierReqDTO();
		reqDTO.setSupplierId(mofAccount.getSmSupplier().getSupplierId());

		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveSmPersonnelBySupplierId(reqDTO);

		// smPersonnel
		if(resDTO.getSupplierHQPersonnel() != null){
			getManagedBean().setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());	
			getManagedBean().setSelectedSupplierBranchName("HQ");
			handleBranchDetailsChanged();
		}
	}

	/**
	 * Supplier listener.
	 * Gets mof no, branch and adress details based on mof nor.
	 * @param suSmSupplier the su sm supplier
	 */
	public void supplierListener(SmSupplier suSmSupplier) {
		debugLogger("supplierListener");

		getManagedBean().getScLoa().getScLoiLoa().setSmSupplier(suSmSupplier);
		getManagedBean().getScLoa().getScLoiLoa().setSupplierName(suSmSupplier.getCompanyName());

		AwardSupplierReqDTO reqDTO  = new AwardSupplierReqDTO();
		reqDTO.setSupplierId(suSmSupplier.getSupplierId());
		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveSmPersonnelBySupplierId(reqDTO);

		// mof nor
		getManagedBean().getScLoa().getScLoiLoa().setMofNo(resDTO.getSmSupplier().getSmMofAccounts().get(0).getMofNo());

		// smPersonnel
		if(resDTO.getSupplierHQPersonnel() != null){
			getManagedBean().setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());	
			getManagedBean().setSelectedSupplierBranchName("HQ");
			handleBranchDetailsChanged();
		}
	}

	/**
	 * Ptj dropdown list.
	 *
	 * @return the list
	 */
	public List<PmOrgProfile> ptjDropdownList() {
		debugLogger("ptjDropdownList");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null && user.getUserId() != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			// reqDTO.setParentOrgProfileId(parentOrgProfileId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
			this.getManagedBean().setPtjDropdownList(pmOrgProfiles);
			return pmOrgProfiles;
		}
		return null;
	}

	/**
	 * Ptj change listener.
	 *
	 * @param orgProfile the org profile
	 */
	public void ptjChangeListener(PmOrgProfile orgProfile) {
		List<PmOrgProfile> orgProfileList = getManagedBean()
				.getPtjDropdownList();
		if (null != orgProfileList) {
			for (PmOrgProfile pmOrgProfile : orgProfileList) {
				if (pmOrgProfile.getOrgProfileId().equals(orgProfile.getOrgProfileId())) {
					getManagedBean().getScLoa().getScLoiLoa().setPmOrgProfile(pmOrgProfile);
				}
			}
		} else {
			debugLogger("org profile list should not be empty");
		}
	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Gets the local goods radio values.display Local goods (Local/Import)
	 * radio values in template spec.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());

					}
				}
			}
		} finally {
		}
		return items;
	}

	/**
	 * Get item type values from pmParameter
	 * @return list of dropdown item values.
	 */
	public SelectItem[] getItemTypeValues(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String recommend = ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE;

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), recommend));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}
	
	
	/**
	 * Success message.
	 *
	 * @param messageCode the message code
	 * @param docType the doc type
	 * @param docTypeCode the doc type code
	 * @param docNo the doc no
	 * @param isAcknowledgeReq the is acknowledge req
	 */
	private void successMessage(String messageCode, String docType,
			String docTypeCode, String docNo, boolean isAcknowledgeRequired) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(
								messageCode,
								new String[] {
										getParameterDesc(docType, docTypeCode),
										docNo },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
		
		if(isAcknowledgeRequired){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}

	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}



	/**
	 * Gets the pm digi sign.
	 *
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 *
	 * @param pmDigiSign the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign.
	 *
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 *
	 * @param textBeforeSign the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the qt award so.
	 *
	 * @return the qt award so
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}
}

package my.ep.web.backingbean.sc.loa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import java.util.Map;
import java.util.Set;

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
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDirectAwardSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.DirectAwardReqDTO;
import my.ep.dto.sc.quationtender.DirectAwardResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmClause;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.DirectLOAManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

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
	 * save the LOA details. save - save data into database and initiate task
	 * for LOA details. page navigate to loa details page.
	 */
	public String saveAction() {
		debugLogger("saveAction");

		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();

			parseLOADetails(scLoa, "SAVE");

			DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setToBeDeleteLocations(this.getManagedBean()
					.getLocationList());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.saveDirectAwardDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();

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
																		ParameterConst.DOCTYPE_LETTER_OF_AWARD),
																scLoa.getLoaNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			// qtAwardSO = null;
		}
		return null;
	}

	/**
	 * submit LOA details. submit - save data into database and BPM process will
	 * route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitAction(String outCome) {
		debugLogger("submitAction");

		try {
			// String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			// validateLOADetails(scLoa, outCome);

			parseLOADetails(scLoa, outCome);
			scLoa.setScLoaWorkFlowStatuses(null);
			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));

			if (scLoa.getScQtSchedules() != null
					&& scLoa.getScQtSchedules().size() > 0) {
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}

			DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.submitDirectAwardDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();

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
																		ParameterConst.DOCTYPE_LETTER_OF_AWARD),
																scLoa.getLoaNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			// qtAwardSO = null;
		}
		return null;
	}

	/**
	 * Gets the agreement letter.
	 * 
	 * @return the loa stream content
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = getManagedBean()
				.getLoaStreamContent();

		/*
		 * if (loaStreamContent == null) { ScLoaReport agreementLetterReport =
		 * null; for (ScLoaReport scLoaReport : getManagedBean().getScLoa()
		 * .getScLoaReports()) { String docDefinitionKey =
		 * scLoaReport.getPmDocument() .getDocDefinitionKey();
		 * 
		 * if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
		 * .equals(docDefinitionKey)) { agreementLetterReport = scLoaReport;
		 * 
		 * break; } }
		 * 
		 * if (agreementLetterReport != null) { ScLoaReportAttachment
		 * agreementLetterAttachment =
		 * generateDocumentAttachment(agreementLetterReport);
		 * agreementLetterReport
		 * .setScLoaReportAttachment(agreementLetterAttachment);
		 * 
		 * loaStreamContent = getAttachmentStreamContent(
		 * agreementLetterAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
		 * 
		 * getManagedBean().setLoaStreamContent(loaStreamContent); } } else {
		 * loaStreamContent.getStream().reset(); }
		 */

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
	 * @param scLoa
	 *            the sc loa
	 * @return the big decimal
	 */
	private ScLoa calcBondAmount(ScLoa scLoa) {
		BigDecimal loaAmt = BigDecimal.ZERO;
		BigDecimal bondAmt = BigDecimal.ZERO;
		double totalDuration = 0;
		double loaAmtForBondPercentage = 0;
		double bondPercentage = 0;
		if (scLoa.getLoaAmt() != null) {
			BigDecimal lamt = scLoa.getLoaAmt();
			lamt = lamt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
			double loaAmount = lamt.doubleValue();

			if (scLoa.getContractDuration() != null) {
				BigDecimal duration = scLoa.getContractDuration();
				duration = duration.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				double cduration = duration.doubleValue();
				if (cduration > 0) {
					totalDuration = cduration / 12;
				}

				if (totalDuration > 0) {
					loaAmtForBondPercentage = loaAmount / totalDuration;
				}

				if (loaAmtForBondPercentage > 0) {
					loaAmt = new BigDecimal(
							String.valueOf(loaAmtForBondPercentage));
					loaAmt = loaAmt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				}
			}

			// Get performance bond rule percentage
			IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.BOND_PERCENTAGE);
			ruleReqDTO.setCompareValue(loaAmt.toString());
			RuleResDTO roleResDTO = ruleSO
					.getRuleValuesByTypeAndCurrentDateRange(ruleReqDTO);

			if (roleResDTO.isSuccessful()
					&& roleResDTO.getRuleValueStrList() != null
					&& roleResDTO.getRuleValueStrList().size() > 0) {
				String percentage = roleResDTO.getRuleValueStrList().get(0);
				double res = Double.valueOf(percentage);
				bondPercentage = res / 100;
				scLoa.setBondPercentage(new BigDecimal(percentage));
			}
			if (bondPercentage > 0 && loaAmtForBondPercentage > 0) {
				double total = loaAmtForBondPercentage * bondPercentage;
				bondAmt = new BigDecimal(String.valueOf(total));
				bondAmt = bondAmt.setScale(4, BigDecimal.ROUND_HALF_EVEN);
				scLoa.setBondAmt(bondAmt);
			}
		}
		return scLoa;
	}

	/**
	 * Edits the supplier loa action. page navigate to loa supplier page.
	 * 
	 * @param loaId
	 *            the loa id
	 */
	public void getSupplierDeatilsByLoaId(Long loaId) {
		debugLogger("editSupplierLOAAction");
		try {
			DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
			reqDTO.setLoaId(loaId);

			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.retriveSupplierDetails(reqDTO);

			ScLoa scLoa = resDTO.getLoa();
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

			getManagedBean().setScQt(scLoa.getScQt());

			scLoa.setAckBy(new FacesScopes().getUserSessionBean());

			scLoa = calcBondAmount(scLoa);

			if (scLoa.getBondAmt() != null) {
				getManagedBean().setBondAmountWord(
						NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			}

			// loa signer
			if (scLoa.getContractSigner() != null) {
				getManagedBean().setContractSignerUser(
						scLoa.getContractSigner());
			}

			// generateDeclarationFromVelocity(scLoa,scQt);

			getManagedBean().setSelectedSupplierBranchName(
					scLoa.getSupplierBranch());
			if (scLoa.getScQt().isZonal()) {
				List<TplSpecZoneDtl> zoneDetails = resDTO.getSpecZoneDetails();
				constructTreeNodeForzones(zoneDetails);
			} /*
			 * else { List<TplSpecLoc> specLocatations =
			 * resDTO.getSpecLocations();
			 * constructLocationTreeNode(specLocatations, this
			 * .getManagedBean().getScQt()); }
			 */
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.SC_DIRECT_LOA_SUPPLIER_MAIN_PAGE);

		} catch (Exception e) {
			handleException(e);
		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Pre render page navigation to loa supplier page based on task id
	 * 
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");

		try {

			SCAwardSupplierDataType awardDateType = getBpmTaskDetails(SCAwardSupplierDataType.class);
			Long sourceId = Long.valueOf(awardDateType.getSourceId());
			Long loaId = Long.valueOf(awardDateType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(awardDateType);

			getSupplierDeatilsByLoaId(loaId);

		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * Freq schedule validation.
	 * 
	 * @param scQtSchedule
	 *            the sc qt schedule
	 */
	private void freqScheduleValidation(ScQtSchedule scQtSchedule) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;

		if (scQtSchedule.getStartYear() == null
				|| scQtSchedule.getStartYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getStartMonth() == null
				|| scQtSchedule.getStartMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndYear() == null
				|| scQtSchedule.getEndYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndMonth() == null
				|| scQtSchedule.getEndMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}

		ScLoa loa = getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();

		Calendar effDate = Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();

		if (scQtSchedule.getStartYear() != null
				&& scQtSchedule.getStartMonth() != null) {
			startDate = Calendar.getInstance();
			startDate.clear();
			startDate.set(Integer.valueOf(scQtSchedule.getStartYear()),
					Integer.valueOf(scQtSchedule.getStartMonth()), 1, 0, 0, 0);
		}

		if (scQtSchedule.getEndYear() != null
				&& scQtSchedule.getEndMonth() != null) {
			endDate = Calendar.getInstance();
			endDate.clear();
			endDate.set(Integer.valueOf(scQtSchedule.getEndYear()),
					Integer.valueOf(scQtSchedule.getEndMonth()),
					Integer.valueOf(getDays(contractExpDate)), 0, 0, 0);
		}

		if (startDate != null && endDate != null) {
			if (startDate.after(endDate)) {
				throw new EpException("Start date must be before end date");
			}

			if (endDate.before(startDate)) {
				throw new EpException("End date must be after start date");
			}

			if (!(startDate.getTime().after(contractEffDate)
					&& (startDate.getTime().before(contractExpDate)) || contractEffDate
						.equals(startDate.getTime()))) {
				throw new EpException(
						"Start date should be in contract validity period.");
			}

			if (!(endDate.getTime().after(contractEffDate)
					&& (endDate.getTime().before(contractExpDate)) || contractExpDate
						.equals(endDate.getTime()))) {
				throw new EpException(
						"End date should be in contract validity period.");
			}
		}

	}

	/**
	 * @ param Date date
	 * 
	 * @return days of month
	 */
	private static String getDays(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		String days = String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		return days;
	}

	/**
	 * Deduction type change. Deduction Clause will show when deduction type
	 * changes
	 * 
	 * @param scQtDeduction
	 *            the sc qt deduction
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
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if (deduction != null && deduction.getDeductionType() != null) {
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
		ScQtDeduction scQtDeduction = getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean()
				.getSelectedDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/**
	 * Handle file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
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
	 * Handle branch details changed. shows the supplier address details.
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
	 * Handle bank details changed. shows the supplier address details for
	 * selected branch and bank.
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
	 * @param scLoa
	 *            the sc loa
	 * @param outcome
	 *            the outcome
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
	 * @param scLoa
	 *            the sc loa
	 * @param outcome
	 *            the outcome
	 */
	private void parseLOADetails(ScLoa scLoa, String outcome) {
		if (scLoa.isBrandNew()) {
			scLoa.setIssueDate(new Date());
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

			saveProgressivePaymntList();
			constructScheduleDetails();
			if (CollectionUtils.isNotEmpty(scLoa.getScQtSchedules())) {
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}
		}

		scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));

		ScQt scQt = this.getManagedBean().getScQt();
		if (scQt.isZonal()) {
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getScQt().getScQtSupplierCriteria();
			List<TplSpecZoneDtl> zoneDetails = convertTreeNodesToZoneCriteria(
					this.getManagedBean().getZoneTrees(), null);

			if (CollectionUtils.isNotEmpty(zoneDetails)) {

				for (TplSpecZoneDtl tplSpecZoneDtl : zoneDetails) {

					tplSpecZoneDtl.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());

				}
			}
			supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel());

			supplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
					zoneDetails);
		} else {/*
				 * ScQtSupplierCriteria supplierCriteria = scQt
				 * .getScQtSupplierCriteria(); List<ScQtLocCriteria>
				 * locCriterias = convertTreeNodesToLocationCriteria(
				 * this.getManagedBean().getLocationTreeNodes(),
				 * supplierCriteria);
				 * 
				 * supplierCriteria.setScQtLocCriteria(locCriterias);
				 * 
				 * supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel())
				 * ; // scQt.setZoneLocationLevel(null);
				 */
		}

	}

	/**
	 * Contract type changed. shows Agency based on contract type changes.
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

				if (scLoa.getScLoiLoa().getPmOrgProfile() != null) {
					boolean found = false;
					for (Object[] object : resDTO.getAgencyProfileDDList()) {
						if (object[0].toString().equals(
								scLoa.getScLoiLoa().getPmOrgProfile()
										.getOrgProfileId().toString())) {
							found = true;
							break;
						}
					}
					if (found) {
						scLoa.setContractAgency(scLoa.getScLoiLoa()
								.getPmOrgProfile());
					}
				}

			} finally {
				// qtAwardSO = null;
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
		// checkAgreementMandatory();
	}

	/**
	 * Contract agency changed. shows Contract Administrator based on agency
	 * changes.
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

			if (scLoa.getContractAgency() != null) {
				if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
					reqDTO.setOrgProfileId(scLoa.getScQt().getPmOrgProfile()
							.getOrgProfileId());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();

				} else if (ParameterConst.CONTRACT_TYPE_CENTRAL
						.equals(contractType)) {
					// TODO - filter user based on central contract.
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.BPK_CCA.value());

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
	 * Contract eff date changed. calculate Completion Expiry Date based on
	 * Completion Period.
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
		if (contractSigner != null) {
			if (getManagedBean().getContractSignerList() != null
					&& getManagedBean().getContractSignerList().size() > 0) {
				List<PmUser> contractSignerList = getManagedBean()
						.getContractSignerList();
				for (PmUser user : contractSignerList) {

					if (contractSigner.getUserId().equals(user.getUserId())) {
						getManagedBean().getScLoa().setLoaSignerDesig(
								user.getDesignation());
						break;
					}
				}
			}
		}
	}

	/**
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {

		IParameterSO parameterSo = null;
		try {

			removefullfillmentDetails(SourcingConstants.FREQUENCY_SCHEDULE);

			List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
					.getScQtSchedules();

			parameterSo = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			// int year = Calendar.getInstance().get(Calendar.YEAR);
			ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			docDefTypeReqDTO.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
			docDefTypeReqDTO
					.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
			ParameterResDTO docDefTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
			for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {

				ScQtSchedule schedule = new ScQtSchedule();
				schedule.setScQt(this.getManagedBean().getScQt());
				schedule.setScLoa(this.getManagedBean().getScLoa());

				schedule.setScheduleType(docDefTypeResDTO.getPmParameter());

				scheduleList.add(schedule);
			}

			this.getManagedBean().getScLoa().setScQtSchedules(scheduleList);
		}

		finally {
			parameterSo = null;
		}
	}

	/**
	 * Gets the year details. Shows start month and end month drop downs based
	 * on Total Frequency.
	 * 
	 * @return the year details
	 */
	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		FacesScopes face = new FacesScopes();
		try {
			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							face.getUserLocale())));
			selectItems.add(new SelectItem(1, getMonth(1)));
			selectItems.add(new SelectItem(2, getMonth(2)));
			selectItems.add(new SelectItem(3, getMonth(3)));
			selectItems.add(new SelectItem(4, getMonth(4)));
			selectItems.add(new SelectItem(5, getMonth(5)));
			selectItems.add(new SelectItem(6, getMonth(6)));
			selectItems.add(new SelectItem(7, getMonth(7)));
			selectItems.add(new SelectItem(8, getMonth(8)));
			selectItems.add(new SelectItem(9, getMonth(9)));
			selectItems.add(new SelectItem(10, getMonth(10)));
			selectItems.add(new SelectItem(11, getMonth(11)));
			selectItems.add(new SelectItem(12, getMonth(12)));

			this.getManagedBean().setYearList(selectItems);
		} finally {

		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the month.
	 * 
	 * @param monthId
	 *            the month id
	 * @return the month
	 */
	private String getMonth(int monthId) {
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM");
		String month = formatter.format(fullMonth.getTime());

		return month;
	}

	/**
	 * Gets the years display dtls. Shows start year and end year drop downs
	 * based on Total Frequency.
	 * 
	 * @return the years display dtls
	 */
	public List<SelectItem> getYearsDisplayDtls() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ScLoa loa = getManagedBean().getScLoa();

		try {
			// int year = Calendar.getInstance().get(Calendar.YEAR);
			Calendar calendar = Calendar.getInstance();
			int effYear = 0;
			int expYear = 0;
			if (loa != null) {

				if (loa.getContractEffDate() != null) {
					calendar.setTime(loa.getContractEffDate());
					effYear = calendar.get(Calendar.YEAR);
				}

				if (loa.getContractExpDate() != null) {
					calendar.setTime(loa.getContractExpDate());
					expYear = calendar.get(Calendar.YEAR);
				}

				int noOfYears = expYear - effYear;
				selectItems.add(new SelectItem(null, ResourceBundleUtils
						.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));

				for (int i = 0; i <= noOfYears + 1; i++) {

					if (i == 0) {
						selectItems.add(new SelectItem(effYear, String
								.valueOf(effYear)));
					}

					if (noOfYears == 0) {
						break;
					}
					effYear = effYear + 1;

					if (effYear == expYear) {
						selectItems.add(new SelectItem(effYear, String
								.valueOf(effYear)));
						break;
					}

					selectItems.add(new SelectItem(effYear, String
							.valueOf(effYear)));
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
	 * Calculate schedule months. Calculating start month , start year , end
	 * month and end year to display total months.
	 * 
	 * @param schedule
	 *            the schedule
	 * @param startMonthAction
	 *            the start month action
	 */
	public void calculateScheduleMonths(ScQtSchedule schedule,
			Boolean startMonthAction) {

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

		if (startMonthAction != null && startMonthAction && startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if (endYear != null) {
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
		this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);
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

		removefullfillmentDetails(SourcingConstants.PROGRESSIVE_PAYMENT);
		List<ScQtProgressivePayment> payments = this.getManagedBean()
				.getScLoa().getScQtProgPayments();

		for (int j = 0; j < this.getManagedBean().getTotalProgressivePayment(); j++) {

			ScQtProgressivePayment payment = new ScQtProgressivePayment();

			setUserDetails(payment);

			payment.setScQt(this.getManagedBean().getScQt());
			payment.setScLoa(this.getManagedBean().getScLoa());
			payments.add(payment);

		}
		this.getManagedBean().getScQt()
				.setScheduleType(SourcingConstants.PROGRESSIVE_PAYMENT);
		this.getManagedBean().getScLoa().setScQtProgPayments(payments);

	}

	/**
	 * Save progressive paymnt list.
	 */
	private void saveProgressivePaymntList() {
		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getScLoa().getScQtProgPayments();

		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (CollectionUtils.isNotEmpty(progressivePayemnt)) {

			for (ScQtProgressivePayment payment : progressivePayemnt) {

				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/"
						+ getMonth(payment.getStartMonth()) + "/"
						+ payment.getStartYear();
				String toDate = date + "/" + getMonth(payment.getEndMonth())
						+ "/" + payment.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						payment.setFromDate(df.parse(fromdate));
						payment.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					debugLogger("Parce Exception");
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
	 * Save progressive paymnt list.
	 */
	private void constructScheduleDetails() {
		List<ScQtSchedule> schdules = this.getManagedBean().getScLoa()
				.getScQtSchedules();

		List<ScQtSchedule> schedules = new ArrayList<ScQtSchedule>();
		if (CollectionUtils.isNotEmpty(schdules)) {

			for (ScQtSchedule schedule : schdules) {

				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/"
						+ getMonth(schedule.getStartMonth()) + "/"
						+ schedule.getStartYear();
				String toDate = date + "/" + getMonth(schedule.getEndMonth())
						+ "/" + schedule.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					debugLogger("Parce Exception");
				}

				schedules.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(schedules);

		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
		}
	}

	/**
	 * Pre render page navigation to direct nego or loa
	 */
	public void preRenderDirectNegoView() {
		debugLogger("preRenderDirectNegoView");
		renewSesionObject();

		retriveLoaDetails(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
	}

	/**
	 * Mof no listener. Gets supplier, branch and adress details based on mof
	 * nor.
	 * 
	 * @param mofAccount
	 *            the mof account
	 */
	public void mofNoListener(SmMofAccount mofAccount) {
		debugLogger("mofNoListener");

		getManagedBean().getScLoa().getScLoiLoa()
				.setSmSupplier(mofAccount.getSmSupplier());
		getManagedBean().getScLoa().getScLoiLoa()
				.setMofNo(mofAccount.getMofNo());
		getManagedBean().getScLoa().getScLoiLoa()
				.setSupplierName(mofAccount.getSmSupplier().getCompanyName());

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setSupplierId(mofAccount.getSmSupplier().getSupplierId());

		AwardSupplierResDTO resDTO = getQtAwardSO()
				.retrieveSmPersonnelBySupplierId(reqDTO);

		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
			getManagedBean().setSelectedSupplierBranchName("HQ");
			handleBranchDetailsChanged();
		}
	}

	/**
	 * Supplier listener. Gets mof no, branch and adress details based on mof
	 * nor.
	 * 
	 * @param suSmSupplier
	 *            the su sm supplier
	 */
	public void supplierListener(SmSupplier suSmSupplier) {
		debugLogger("supplierListener");

		getManagedBean().getScLoa().getScLoiLoa().setSmSupplier(suSmSupplier);
		getManagedBean().getScLoa().getScLoiLoa()
				.setSupplierName(suSmSupplier.getCompanyName());

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setSupplierId(suSmSupplier.getSupplierId());
		AwardSupplierResDTO resDTO = getQtAwardSO()
				.retrieveSmPersonnelBySupplierId(reqDTO);

		// mof nor
		getManagedBean()
				.getScLoa()
				.getScLoiLoa()
				.setMofNo(
						resDTO.getSmSupplier().getSmMofAccounts().get(0)
								.getMofNo());

		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
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
	 * @param orgProfile
	 *            the org profile
	 */
	public void ptjChangeListener() {
		List<PmOrgProfile> orgProfileList = getManagedBean()
				.getPtjDropdownList();
		if (null != orgProfileList) {
			for (PmOrgProfile pmOrgProfile : orgProfileList) {
				if (pmOrgProfile.getOrgProfileId().equals(
						this.getManagedBean().getScQt().getPmOrgProfile()
								.getOrgProfileId())) {
					getManagedBean().getScLoa().getScQt()
							.setPmOrgProfile(pmOrgProfile);
					getManagedBean().getScLoa().getScLoiLoa()
							.setPmOrgProfile(pmOrgProfile);
					getApprovalUsers();
					break;
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
	 * 
	 * @return list of dropdown item values.
	 */
	public SelectItem[] getItemTypeValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String recommend = ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE;

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), recommend));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}

	/**
	 * Success message.
	 * 
	 * @param messageCode
	 *            the message code
	 * @param docType
	 *            the doc type
	 * @param docTypeCode
	 *            the doc type code
	 * @param docNo
	 *            the doc no
	 * @param isAcknowledgeReq
	 *            the is acknowledge req
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

		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}

	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
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
	 * @param pmDigiSign
	 *            the new pm digi sign
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
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTDirectAwardSO getQtDirectAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDirectAwardSO.class);
	}

	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
		this.getManagedBean().setZoneLocation(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Select zone tree node.Event handle check non panel Treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode)
	 */
	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select zone tree node.Event handle uncheck Nonpanel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Edits the zones.click pencil icon in general information menu navigate to
	 * ZoneLocation Popup.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index).
	 */
	public void editZones(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();

					break;
				}
			}

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, TplSpecZoneLoc.class));
			int i = index;
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setIndex(i);
			getManagedBean().setZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
			getManagedBean().setTempZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
		} finally {

		}

	}

	/**
	 * Gets the zone locations. convert Treenode to TplSpecZoneLoc objects in
	 * general information.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<TplSpecZoneLoc>
	 */
	private List<TplSpecZoneLoc> getZoneLocations(TreeNode zoneLocation) {

		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getZoneLocations(childrendLocationNode));
			}
		}

		return results;
	}

	/**
	 * Select location tree node.Event handle check treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select location tree node.Event handle Uncheck treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Edits the supplier locations.click pencil icon in supplier qualification
	 * criteria navigate to Locations Popup.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index)
	 */
	public void editSupplierLocations(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;

		try {

			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
			List<TreeNode> locationNodes = getManagedBean()
					.getLocationTreeNodes();
			if (CollectionUtils.isNotEmpty(locationNodes)) {
				for (TreeNode treeNode : locationNodes) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}

			// List<ScQtLocCriteria> locationDTOs =
			// getLocations(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);

					break;
				}
			}

			// ScQuoteZone zone = getZone(rootZoneLocation);
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootLocationTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, ScQtLocCriteria.class));

			// this.getManagedBean().setIndex(i);
		} finally {
			parameterSo = null;
		}

		// getManagedBean().setZoneName(zone.getZoneName());

	}

	/**
	 * Gets the locations.convert Treenode to Location Criteria objects in
	 * supplier qualification criteria.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<ScQtLocCriteria>
	 */
	private List<ScQtLocCriteria> getLocations(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (zoneLocation.isLeaf()) {
			ScQtLocCriteria locationDTO = (ScQtLocCriteria) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	/**
	 * Show supplier location popup.Event handle navigate to the Supplier
	 * Locations Popup.
	 */
	public void showSupplierLocationPopup() {

		ICountrySO countrySo = null;
		try {

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();

			if (locationLevel == null) {

				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setLocalLevelCode(
							paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);

					List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
					if (getManagedBean().getLocationTreeNodes() != null) {
						for (TreeNode treeNode : getManagedBean()
								.getLocationTreeNodes()) {
							List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
							locationDTOs.addAll(locationDTO);
						}
					}

					List<PmState> states = SourcingLocationHelper
							.getStateFromLocationDTO(locationDTOs);
					List<PmDivision> divisions = null;
					List<PmDistrict> districts = null;
					if (ParameterConst.LOCATION_LEVEL_DIVISION
							.equalsIgnoreCase(this.getManagedBean()
									.getLocalLevelCode())) {
						divisions = SourcingLocationHelper
								.getDivisionFromLocationDTO(locationDTOs);

					} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
							.equalsIgnoreCase(this.getManagedBean()
									.getLocalLevelCode())) {
						divisions = SourcingLocationHelper
								.getDivisionFromLocationDTO(locationDTOs);
						districts = SourcingLocationHelper
								.getDistrictFromLocationDTO(locationDTOs);
					}

					getManagedBean().setRootLocationTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											states,
											districts,
											divisions,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											ScQtLocCriteria.class));
					getManagedBean().setIndex(-1);
				}

			}
			RequestContext.getCurrentInstance().execute("ZoneLocation.show()");
		} finally {

		}
	}

	/*
	 * Delete supplier qualification tree node.Event handle delete Locations
	 * from supplier qualification criteria.
	 * 
	 * @throws NoSuchMethodException
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @throws InstantiationException
	 * 
	 * @throws IllegalAccessException
	 */
	public void deleteSupplierQualificationTreeNode() {
		try {
			List<TreeNode> rootTrees = getManagedBean().getLocationTreeNodes();
			deleteTreeNodes(rootTrees);

			List<ScQtLocCriteria> newLocCriterias = new ArrayList<ScQtLocCriteria>();
			if (!rootTrees.isEmpty()) {

				List<TplSpecLoc> localLoc = new ArrayList<TplSpecLoc>();
				List<ScQtLocCriteria> Criteria = new ArrayList<ScQtLocCriteria>();

				Criteria = convertTreeNodesToLocationCriteria(rootTrees, this
						.getManagedBean().getScQt().getScQtSupplierCriteria());

				for (ScQtLocCriteria locCriteria : Criteria) {
					// Criteria.add(locCriteria);
					ScQtLocCriteria criteriaNew = (ScQtLocCriteria) BeanUtils
							.cloneBean(locCriteria);
					newLocCriterias.add(criteriaNew);
					TplSpecLoc specLoc = (TplSpecLoc) locCriteria;

					localLoc.add(specLoc);

				}

				this.getManagedBean().setTotalSuppliersSpecLoc(localLoc);
			}

			checkExistingLocation(newLocCriterias, this.getManagedBean()
					.getExistingLocationList());

		} catch (IllegalAccessException e) {
			debugLogger(e.getMessage());
		} catch (InstantiationException e) {
			debugLogger(e.getMessage());
		} catch (InvocationTargetException e) {
			debugLogger(e.getMessage());
		} catch (NoSuchMethodException e) {
			debugLogger(e.getMessage());
		}
	}

	/**
	 * Delete tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 */
	private void deleteTreeNodes(List<TreeNode> rootTrees) {
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {

						rootTrees.remove(row--);
						// deleteSupplierLocacriteria(loccriteria);
					} else {
						deleteChildTreeNodes(zoneTree.getChildren());

						if (zoneTree.getChildren().size() == 0) {
							// rootTrees.remove(row--);
						}
					}
				}
			}
		}
	}

	/**
	 * Delete child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 */
	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {

					nodesToRemoved.add(treeNode);

					// deleteSupplierLocacriteria(loccriteria);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {

						deleteChildTreeNodes(treeNode.getChildren());
						// deleteSupplierLocacriteria(loccriteria);
						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
	}

	private void checkExistingLocation(
			List<ScQtLocCriteria> currentLocationList,
			List<ScQtLocCriteria> existingLocations) {
		List<ScQtLocCriteria> localDeleteList = this.getManagedBean()
				.getLocationList();
		if (null == localDeleteList) {
			localDeleteList = new ArrayList<ScQtLocCriteria>();
		}
		if (null != existingLocations && !existingLocations.isEmpty()
				&& null != currentLocationList
				&& !currentLocationList.isEmpty()) {
			for (ScQtLocCriteria locDeleteCriteria : existingLocations) {
				boolean delectLoc = false;
				for (ScQtLocCriteria locCurrentList : currentLocationList) {
					if (null != locCurrentList.getSpecLocId()) {
						if (locCurrentList.getSpecLocId().equals(
								locDeleteCriteria.getSpecLocId())) {
							delectLoc = true;
							break;
						}
					} else {
						delectLoc = true;
					}
				}
				if (!delectLoc) {
					if (!localDeleteList.contains(locDeleteCriteria)) {
						localDeleteList.add(locDeleteCriteria);
					}
				}
			}

			this.getManagedBean().setLocationList(localDeleteList);

		}
		if (null == currentLocationList || currentLocationList.isEmpty()) {
			if (null != existingLocations && !existingLocations.isEmpty()) {
				this.getManagedBean().setLocationList(existingLocations);
			}
		}
	}

	/**
	 * Update supplier locations.Event handle add Locations to Supplier
	 * qualification criteria menu.
	 */
	public void updateSupplierLocations() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		this.getManagedBean().setLocationList(
				getManagedBean().getExistingLocationList());
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<TplSpecLoc> specLoc = new ArrayList<TplSpecLoc>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				} else {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		retrievLocationDtls(locationDTOs);
		log.info("updateZoneLocation - END");
	}

	/**
	 * Retriev location dtls.
	 * 
	 * @param scQtLocCriteriaList
	 *            the sc qt loc criteria list
	 */
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.info("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setLocationTreeNodes(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getLocationTreeNodes().add(
							LocationSingleton.getInstance().buildLocationTree(
									null, localStateList, districts, divisions,
									cities, ScQtLocCriteria.class));
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != districts && !districts.isEmpty()) {
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(
								districts, null);
				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
						.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts,
								null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
						.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null
						&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null
								&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
										.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
										.keySet();
								if (districtsCitiesKeySet != null
										&& !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							getManagedBean().getLocationTreeNodes().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
				if (statesDistrictsCitiesKeySet != null
						&& !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
								.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);
							}
						}
						getManagedBean().getLocationTreeNodes().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, null,
												districts, divisions, cities,
												ScQtLocCriteria.class));
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton
						.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision
								.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);
							}
							getManagedBean().getLocationTreeNodes().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
			}
		}
		log.info("retrievLocationDtls - END");
	}

	/**
	 * Show general zones popup.Event handle navigate to the ZoneLocations
	 * Popup.
	 */
	public void showGeneralZonesPopup() {

		ICountrySO countrySo = null;

		try {
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			if (locationLevel == null) {

				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setZoneLocation(true);

					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootZoanlTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											null,
											null,
											null,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											TplSpecZoneLoc.class));
					this.getManagedBean().setIndex(-1);
					this.getManagedBean().setZoneName("");
				}

			}
			this.getManagedBean().setAutoUpdate(false);
			RequestContext.getCurrentInstance().execute("ZoneGeneral.show()");
		} finally {

		}

	}

	/**
	 * Delete tree node.Event handle delete NonPanel Zones from General
	 * information.
	 */
	public void deleteTreeNode() {

		List<TreeNode> rootTrees = this.getManagedBean().getZoneTrees();

		// LocationSingleton.getInstance().deleteTreeNodes(rootTrees);
		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (!rootTrees.isEmpty()) {
			getManagedBean()
					.getScQt()
					.getScQtSupplierCriteria()
					.getQtZoneCriterias()
					.setTplSpecZoneDtls(
							convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
		}
		this.getManagedBean().setIndex(-1);

	}

	/**
	 * Delete zones tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 * @return the list
	 */
	private List<TreeNode> deleteZonesTreeNodes(List<TreeNode> rootTrees) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> removedList = new ArrayList<TreeNode>();
		if (rootTrees != null) {
			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						tplSpecZoneDtl = (TplSpecZoneDtl) zoneTree.getData();
						if (tplSpecZoneDtl.getPanelZone() != null) {
							// deletePanelItemSuppliers(tplSpecZoneDtl);

						} else {
							deleteStateOrDivOrDist(tplSpecZoneDtl, null);
						}

						rootTrees.remove(row--);
					} else {

						removedList = deleteZoneChildTreeNodes(zoneTree
								.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getZoneLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl,
										locationDTOs);
							}
						}

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}

				}
			}
		}
		return removedList;
	}

	/**
	 * Delete zone child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the list
	 */
	private List<TreeNode> deleteZoneChildTreeNodes(List<TreeNode> treeNodes) {
		List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
		if (treeNodes != null) {

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
		return nodesToRemoved;
	}

	/**
	 * Delete state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 */
	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);

			divIds = getPmDivisionIds(tplSpecZoneLocs);

			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> results = new ArrayList<ZonesBean>();
		List<ZonesBean> list = getManagedBean().getZoneBeans();
		if (list != null && list.size() > 0) {
			for (ZonesBean zonesBean : list) {
				ZonesBean bean = new ZonesBean();
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (tplSpecZoneDtl != null) {
						if (!zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								}
							}
							bean.setTplSpecZoneLocs(specZoneLocs);
						}
						results.add(bean);
					}
				}
			}
			getManagedBean().setZoneBeans(results);
		}
	}

	/**
	 * Gets the pm state ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmStateIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmState() != null) {
					results.add(TplSpecZoneLoc.getPmState().getStateId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm division ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDivisionIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDivision() != null) {
					results.add(TplSpecZoneLoc.getPmDivision().getDivisionId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm districts ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDistrictsIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDistrict() != null) {
					results.add(TplSpecZoneLoc.getPmDistrict().getDistrictId());
				}
			}
		}
		return results;
	}

	/**
	 * Update zone general location.Event handle add zones to general
	 * information menu.
	 */
	public void updateZoneGeneralLocation() {
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<ZonesBean> beans = getManagedBean().getZoneBeans();
		if (null == beans) {
			beans = new ArrayList<ZonesBean>();
		}
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getLocalLevelCode();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equalsIgnoreCase(locationLevel)) {
					if (treeNode.getType().equals("state")) {
						if (treeNode.getChildCount() == 0
								&& treeNode.getChildren().isEmpty()) {
							List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
							allNodes.addAll(locationDTO);

						}
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				} else {

					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				}
			}
		}

		if (null == getManagedBean().getZoneTrees()) {
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}
		if (null != allNodes && !allNodes.isEmpty()) {

			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			TplSpecZoneDtl zone = new TplSpecZoneDtl();

			if (null == getManagedBean().getZoneName()
					|| StringUtils.isBlank(getManagedBean().getZoneName())) {
				throw new EpException("Zone Name is Mandatory.");
			}

			zone.setZoneName(getManagedBean().getZoneName());

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(allNodes);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(allNodes);

			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(allNodes);

			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(allNodes);
			String editZoneName = getManagedBean().getTempZoneName();
			if (getManagedBean().getIndex() >= 0) {
				updateZoneBean(editZoneName);
			}
			zone.setTplSpecZoneLocs(allNodes);
			zonesBean.setTplSpecZoneDtl(zone);
			// zonesBean.setTplSpecZoneDtl(zone);
			// validate zone name
			checkZoneExistsOrNot(zone);

			if (ParameterConst.LOCATION_LEVEL_STATE
					.equalsIgnoreCase(locationLevel)) {
				// check whether state exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (ParameterConst.LOCATION_LEVEL_DIVISION
					.equalsIgnoreCase(locationLevel)) {
				// check whether division exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (ParameterConst.LOCATION_LEVEL_DISTRICT
					.equalsIgnoreCase(locationLevel)) {
				// check whether district exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			beans.add(zonesBean);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));

			}

			else if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
				getManagedBean().getZoneTrees().add(
						getManagedBean().getIndex(),
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));
			}
			getManagedBean().getZoneBeans().add(zonesBean);
		} else {
			throw new EpException("Please select At least one Zone Location");
		}
		this.getManagedBean().setZoneLocation(false);
		getManagedBean().setZoneName(null);
		getManagedBean().setTempZoneName(null);
		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");

	}

	// check zone name existing in the list or not.
	private void updateZoneBean(String zoneName) {
		List<ZonesBean> beans = new ArrayList<ZonesBean>();
		List<ZonesBean> zoneBeans = getManagedBean().getZoneBeans();

		if (zoneBeans != null && zoneBeans.size() > 0) {
			for (ZonesBean zonesBean : zoneBeans) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();
				if (null != prevsZone) {
					if (!prevsZone.getZoneName().equalsIgnoreCase(zoneName)) {
						beans.add(zonesBean);
					}
				}
			}
		}
		getManagedBean().setZoneBeans(beans);

	}

	/**
	 * Check zone exists or not.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * 
	 * @return List<Long>
	 */
	private List<Long> checkZoneExistsOrNot(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> results = new ArrayList<Long>();
		// if (index < 0) {
		List<ZonesBean> list = getManagedBean().getZoneBeans();
		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (zone.getZoneName().equalsIgnoreCase(
							tplSpecZoneDtl.getZoneName())) {
						throw new EpException(tplSpecZoneDtl.getZoneName()
								+ " zone already exists.");
					}
				}
			}
		}
		// }
		return results;
	}

	/**
	 * Check duplicate state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 */
	private void checkDuplicateStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (null != tplSpecZoneDtl.getTplSpecZoneLocs()
				&& tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
			List<TplSpecZoneLoc> currentTplSpecLocs = tplSpecZoneDtl
					.getTplSpecZoneLocs();
			stateIds = getPmStateIds(currentTplSpecLocs);
			divIds = getPmDivisionIds(currentTplSpecLocs);
			distIds = getPmDistrictsIds(currentTplSpecLocs);
		}

		List<ZonesBean> list = getManagedBean().getZoneBeans();

		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();

				if (prevsZone != null) {

					if (prevsZone.getTplSpecZoneLocs() != null
							&& prevsZone.getTplSpecZoneLocs().size() > 0) {
						List<TplSpecZoneLoc> previousTplSpecLocs = prevsZone
								.getTplSpecZoneLocs();
						for (TplSpecZoneLoc TplSpecZoneLoc : previousTplSpecLocs) {

							if (TplSpecZoneLoc.getPmState() != null) {

								if (stateIds != null && stateIds.size() > 0) {

									if (stateIds.contains(TplSpecZoneLoc
											.getPmState().getStateId())) {
										throw new EpException(
												MessageConst.ERROR_760168,
												new String[] {
														TplSpecZoneLoc
																.getPmState()
																.getStateName(),
														prevsZone.getZoneName() });
									}
								}

							}

							if (TplSpecZoneLoc.getPmDivision() != null) {
								if (divIds != null && divIds.size() > 0) {
									if (divIds.contains(TplSpecZoneLoc
											.getPmDivision().getDivisionId())) {
										throw new EpException(
												MessageConst.ERROR_760167,
												new String[] {
														TplSpecZoneLoc
																.getPmDivision()
																.getDivisionName(),
														prevsZone.getZoneName() });
									}
								}
							}

							if (TplSpecZoneLoc.getPmDistrict() != null) {
								if (distIds != null && distIds.size() > 0) {
									if (distIds.contains(TplSpecZoneLoc
											.getPmDistrict().getDistrictId())) {
										throw new EpException(
												MessageConst.ERROR_760166,
												new String[] {
														TplSpecZoneLoc
																.getPmDistrict()
																.getDistrictName(),
														prevsZone.getZoneName() });
									}
								}
							}
						}
					}
					if (prevsZone.getZoneName().equals(
							tplSpecZoneDtl.getZoneName())) {
						throw new EpException(
								MessageConst.ERROR_700027,
								new String[] { ResourceBundleUtils.getMessage(
										tplSpecZoneDtl.getZoneName(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}
			}
		}
	}

	/**
	 * Fulfilment change.changed Fulfilemnt Dropdown in specification committee.
	 * 
	 * @param event
	 *            (Long fulfilmentId)
	 */
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		Long type = (Long) event.getComponent().getAttributes().get("value");
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
		for (PmParameterDesc paramDesc : localParamDesc) {
			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(type))) {

				this.getManagedBean().getScQt()
						.setFulfilmentType(paramDesc.getPmParameter());
				this.getManagedBean().getScLoa().getScLoiLoa()
						.setFulfilmentTypeCode(paramDesc.getPmParameter());
				break;
			}
		}

		if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
				.getManagedBean().getScQt().getFulfilmentType()
				.getParameterCode())) {
			this.getManagedBean().getScQt()
					.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
			this.getManagedBean().setTotalFrequency(0);
			this.getManagedBean().setTotalProgressivePayment(0);

		}

		this.getManagedBean().getScQt().setScQtSchedules(null);
		this.getManagedBean().getScQt().setScQtProgressivePayments(null);
		removefullfillmentDetails(SourcingConstants.PROGRESSIVE_PAYMENT);
		removefullfillmentDetails(SourcingConstants.FREQUENCY_SCHEDULE);
		getLoaReports();

	}

	/**
	 * Gets the dropdown location values.display the Location level dropdown
	 * values.
	 * 
	 * @param locationLevel
	 *            the location level
	 * @return the dropdown location values
	 */
	public SelectItem[] getDropdownLocationValues(String locationLevel) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (PTJLocalityCode.STATE_SARAWAK == this.getManagedBean()
				.getAllStateCode()) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	/**
	 * Select zone location level.change Location Level Dropdown in General
	 * information menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectZoneLocationLevel(AjaxBehaviorEvent event) {

		if (this.getManagedBean().getScQt().isZonal()) {
			if (CollectionUtils
					.isNotEmpty(this.getManagedBean().getZoneTrees())) {
				this.getManagedBean().setZoneTrees(null);
				this.getManagedBean().setZoneBeans(new ArrayList<ZonesBean>());
			}

		}

	}

	/**
	 * Zone radio change.change zone location level radio in business owner.
	 * 
	 * @param event
	 *            (boolean zoneLocationlevel).
	 */
	public void zoneRadioChange(AjaxBehaviorEvent event) {
		Boolean isZonal = (Boolean) event.getComponent().getAttributes()
				.get("value");
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
		scQt.setZoneLocationLevel(new PmParameter());
		if (null == supplierCriteria) {
			supplierCriteria = new ScQtSupplierCriteria();
		}
		if (isZonal) {
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();
			if (null == zoneCriteria) {
				zoneCriteria = new ScQtZoneCriteria();
				zoneCriteria.setScQtSupplierCriteria(supplierCriteria);
				supplierCriteria.setQtZoneCriterias(zoneCriteria);
			}

			supplierCriteria.setScQtLocCriteria(null);

		} else {

			this.getManagedBean().setZoneTrees(null);
			this.getManagedBean().setZoneBeans(new ArrayList<ZonesBean>());

		}
		scQt.setScQtSupplierCriteria(supplierCriteria);
		this.getManagedBean().setScQt(scQt);

	}

	/*
	 * public void getLoaReports() { ScLoa scLoa =
	 * this.getManagedBean().getScLoa(); if
	 * (CollectionUtils.isEmpty(scLoa.getScLoaReports())) { IQTAwardSO awardSO =
	 * BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	 * 
	 * AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
	 * AwardSupplierResDTO awardSupplierResDTO = awardSO
	 * .getAwardReports(awardSupplierReqDTO); this.getManagedBean().getScLoa()
	 * .setScLoaReports(awardSupplierResDTO.getLoaReports()); } }
	 */

	public void getApprovalUsers() {
		if (CollectionUtils.isEmpty(this.getManagedBean().getApproverList())) {
			IUserSO userSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			userReqDTO.setOrgProfileId(this.getManagedBean().getScQt()
					.getPmOrgProfile().getOrgProfileId());
			userReqDTO.setWithProfileGroup(true);
			UserResDTO userResDTO = userSo
					.retrieveUserListWithProfileGroupUserByRole(userReqDTO);
			// TODO Need to remove this..
			this.getManagedBean().setContractSignerList(
					userResDTO.getUserList());
			this.getManagedBean().setApproverList(userResDTO.getUserList());
		}
	}

	public void getContractSigners() {
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
		if (this.getManagedBean().getScLoa().getLoaAmt() != null) {
			userReqDTO.setApprovalLimit(this.getManagedBean().getScLoa()
					.getLoaAmt());
		}
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IUserSO.class);
		userReqDTO.setOrgProfileId(this.getManagedBean().getScQt()
				.getPmOrgProfile().getOrgProfileId());
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		this.getManagedBean().setContractSignerList(userResDTO.getUserList());
	}

	/**
	 * Schedule type change.Event handle change Schedule
	 * type(Schedule/Progressive payment)in Specification committee.
	 * 
	 * @param event
	 *            (String ScheduleType).
	 */
	public void scheduleTypeChange(AjaxBehaviorEvent event) {

		String scheduleType = (String) event.getComponent().getAttributes()
				.get("value");
		String toBeRemoveType = null;
		if (SourcingConstants.FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			toBeRemoveType = SourcingConstants.PROGRESSIVE_PAYMENT;
			this.getManagedBean().setTotalProgressivePayment(0);
		} else if (SourcingConstants.PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			toBeRemoveType = SourcingConstants.FREQUENCY_SCHEDULE;
			this.getManagedBean().setTotalFrequency(0);
		}
		this.getManagedBean().getScQt().setScheduleType(scheduleType);
		removefullfillmentDetails(toBeRemoveType);

	}

	private void removefullfillmentDetails(String scheduleType) {

		if (SourcingConstants.FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
					.getScQtSchedules();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();

				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().setToBeRemoveSchedules(toBeRemoveList);
				this.getManagedBean().getScQt().setScQtSchedules(null);
			}
			this.getManagedBean().getScLoa()
					.setScQtSchedules(new ArrayList<ScQtSchedule>());
		} else if (SourcingConstants.PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getScLoa().getScQtProgPayments();

			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();

				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().setToBeRemoveProgPayments(toBeRemoveList);
				this.getManagedBean().getScQt()
						.setScQtProgressivePayments(null);
			}
			this.getManagedBean()
					.getScLoa()
					.setScQtProgPayments(
							new ArrayList<ScQtProgressivePayment>());
		}

	}

	/**
	 * Calculate start month.change start month dropdown in frequency schedule
	 * menu.
	 * 
	 * @param schedule
	 *            (ScQtSchedule schedule).
	 */
	public void calculateStartMonth(ScQtSchedule schedule) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		if (null != schedule.getStartMonth() && null != schedule.getEndMonth()) {
			int start = Integer.parseInt(schedule.getStartMonth());
			String fromMonth = getMonth(start);
			schedule.setEndMonth(schedule.getStartMonth());
			int end = Integer.parseInt(schedule.getEndMonth());
			String toMonth = getMonth(end);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String fromdate = null;
			String toDate = null;
			if (null != schedule.getStartYear()
					&& null != schedule.getEndYear()) {
				fromdate = 01 + "/" + fromMonth + "/" + schedule.getStartYear();
				toDate = 31 + "/" + toMonth + "/" + schedule.getEndYear();
			}

			// schedule.setScheduleNo(schedule.getScheduleNo());

			try {

				Date date1;
				Date date2;
				try {
					if (null != fromdate && null != toDate) {
						date1 = format.parse(fromdate);
						date2 = format.parse(toDate);
						long days = DateUtils.getDaysDifference(date1, date2);
						int day = (int) days;
						int months = day / 30;
						schedule.setTotalMonths(new BigDecimal(months));
					}
				} catch (ParseException e) {
					log.debug(e.getMessage());
				}

				// schedule.setFromDate(df.parse(fromdate));
				// schedule.setToDate(df.parse(toDate));

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);

				this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);

			} finally {

			}
		} else {
			int index = freqSchedule.indexOf(schedule);
			if (null != schedule.getStartMonth()) {
				schedule.setEndMonth(schedule.getStartMonth());
			}
			freqSchedule.set(index, schedule);
		}

	}

	/**
	 * Calculate progressive start month.change start month in progessive
	 * payment.
	 * 
	 * @param progressivepayment
	 *            (ScQtProgressivePayment progressivepayment).
	 */
	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean().getScLoa()
				.getScQtProgPayments();

		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			if (null == progressivepayment.getEndMonth()) {
				progressivepayment.setEndMonth(progressivepayment
						.getStartMonth());
			}
			payment.set(index, progressivepayment);

			this.getManagedBean().getScLoa().setScQtProgPayments(payment);
		}

	}

	/**
	 * Calculate progressivepayment.change end month dropdown in progressive
	 * payment.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateProgressivepayment(
			ScQtProgressivePayment progressivepayment) {

		List<ScQtProgressivePayment> payment = this.getManagedBean().getScLoa()
				.getScQtProgPayments();

		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment) {
			int index = payment.indexOf(progressivepayment);

			payment.set(index, progressivepayment);

			this.getManagedBean().getScLoa().setScQtProgPayments(payment);
		}

	}

	/**
	 * Calculate schedule percentage.calculate months difference in Frequency
	 * schedule.
	 * 
	 * @param schedule
	 *            (ScQtSchedule schedule).
	 */
	public void calculateSchedulePercentage(ScQtSchedule schedule) {
		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();

		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		if (null != schedule.getStartMonth() && null != schedule.getEndMonth()) {
			int start = Integer.parseInt(schedule.getStartMonth());
			String fromMonth = getMonth(start);
			int end = Integer.parseInt(schedule.getEndMonth());
			String toMonth = getMonth(end);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String fromdate = null;
			String toDate = null;
			if (null != schedule.getStartYear()
					&& null != schedule.getEndYear()) {
				fromdate = 01 + "/" + fromMonth + "/" + schedule.getStartYear();
				toDate = 31 + "/" + toMonth + "/" + schedule.getEndYear();
			}

			// schedule.setScheduleNo(schedule.getScheduleNo());

			try {

				Date date1;
				Date date2;
				try {
					if (null != fromdate && null != toDate) {
						date1 = format.parse(fromdate);
						date2 = format.parse(toDate);
						long days = DateUtils.getDaysDifference(date1, date2);
						int day = (int) days;
						int months = day / 30;
						schedule.setTotalMonths(new BigDecimal(months));
					}
				} catch (ParseException e) {
					log.error(e.getMessage());
				}

				// schedule.setFromDate(df.parse(fromdate));
				// schedule.setToDate(df.parse(toDate));

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);

				this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);

			} finally {

			}
		}

	}

	private void getLoaReports() {
		if (CollectionUtils.isEmpty(this.getManagedBean().getScLoa()
				.getScLoaReports())) {
			IQTAwardSO awardSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setScLoa(this.getManagedBean().getScLoa());
			this.getManagedBean()
					.getScLoa()
					.setScLoaReports(
							awardSO.getAwardReports(awardSupplierReqDTO)
									.getLoaReports());
		}
	}

	/**
	 * Adds the loa content action.
	 */
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
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoaReport.setDefinationType(resDTO.getPmParameter());
			scLoaReport
					.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); // AQ
		} finally {
			parameterSO = null;
		}

		scLoaReports.add(scLoaReport);
	}

	/**
	 * Delete loa content action.
	 */
	public void deleteLoaContentAction() {
		debugLogger("deleteLoaContentAction");
		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScLoaReport> selectedScLoaReports = getManagedBean()
				.getSelectedScLoaReports();

		if (selectedScLoaReports != null && selectedScLoaReports.size() > 0) {
			deleteRowsAction(scLoa.getScLoaReports(), selectedScLoaReports);
		} else {
			throw new EpException("Please select At least one Item");
		}
	}

	/*
	 * * Download all reports as pdf or xlsx files in Letter of Acceptance.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa()
				.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			scAttachments.add(scLoaReportAttachment);
		}

		downloadAllFiles("NGeP-LOA-Report", scAttachments);
	}

	/**
	 * Generate document attachment.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @return the sc loa report attachment
	 */
	private ScLoaReportAttachment generateDocumentAttachment(
			ScLoaReport scLoaReport) {
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();

		/*
		 * if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
		 * .equals(definitionType)) { try { String docDefinitionKey =
		 * scLoaReport.getPmDocument() .getDocDefinitionKey(); ScLoa scLoa =
		 * getManagedBean().getScLoa(); Long statusId =
		 * scLoa.getPmStatus().getStatusId().longValue(); StringBuilder filename
		 * = new StringBuilder(); //replace white space due to firefox
		 * filename.append(scLoaReport.getTitle().replaceAll("\\s","")); if
		 * (scLoaReportAttachment == null) { scLoaReportAttachment = new
		 * ScLoaReportAttachment();
		 * scLoaReportAttachment.setScLoaReport(scLoaReport);
		 * scLoaReportAttachment.setDocNo(scLoaReport.getScLoa() .getLoaNo());
		 * 
		 * if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateContractSummaryPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa); } else if
		 * (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateAgreementLetterPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa, getManagedBean().getScQt()); } else if
		 * (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateDeclarationPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa); } else if
		 * (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * saveScheduleList(); generateFrequencySchedulePdfFromVelocity(
		 * (ScAttachment) scLoaReportAttachment, scLoa); } else if
		 * (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
		 * .equals(docDefinitionKey)) { filename.append(".xlsx");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * formJadualHarga((ScAttachment) scLoaReportAttachment, scLoa);
		 * 
		 * }else if (SourcingConstants.DOC_DEF_KEY_SPECIFICATION
		 * .equals(docDefinitionKey)) { filename.append(".xlsx");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * formSpecification((ScAttachment) scLoaReportAttachment, scLoa);
		 * 
		 * }else if (SourcingConstants.DOC_DEF_KEY_FORMAT_BOND_EXECUTION
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateBondLetterFromVelocityToPdf((ScAttachment)
		 * scLoaReportAttachment,scLoa); } else if
		 * (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment); }
		 * 
		 * }else if(scLoaReportAttachment != null &&
		 * QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
		 * || QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)){ // if
		 * pending supplier acknowledgement , always regenerate
		 * 
		 * if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateContractSummaryPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa); } else if
		 * (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateAgreementLetterPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa, getManagedBean().getScQt()); } else if
		 * (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateDeclarationPdfFromVelocity( (ScAttachment)
		 * scLoaReportAttachment, scLoa);
		 * 
		 * } else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
		 * .equals(docDefinitionKey)) { filename.append(".xlsx");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * formJadualHarga((ScAttachment) scLoaReportAttachment, scLoa);
		 * 
		 * } else if (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment); }
		 * }else if(scLoaReportAttachment != null &&
		 * QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)){ // if pending
		 * submission , always regenerate if
		 * (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
		 * .equals(docDefinitionKey)) { filename.append(".pdf");
		 * scLoaReportAttachment.setFileName(filename.toString());
		 * saveScheduleList(); generateFrequencySchedulePdfFromVelocity(
		 * (ScAttachment) scLoaReportAttachment, scLoa); } } } catch (Exception
		 * e) { //e.printStackTrace(); throw new EpException(e.getMessage()); }
		 * 
		 * // TODOs if (scLoaReportAttachment.getFileName() != null &&
		 * scLoaReportAttachment.getFilePath() != null) {
		 * scLoaReport.setScLoaReportAttachment(scLoaReportAttachment); } }
		 */

		return scLoaReportAttachment;
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

	private void constructLocationTreeNode(
			List<TplSpecLoc> supplierLocationList, ScQt scQt) {
		if (null != supplierLocationList) {

			if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
				this.getManagedBean().setTotalSuppliersSpecLoc(
						new ArrayList<TplSpecLoc>());
			}
			this.getManagedBean().getTotalSuppliersSpecLoc()
					.addAll(supplierLocationList);
			List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
			for (TplSpecLoc localLoc : supplierLocationList) {
				if (null != scQt.getScQtSupplierCriteria()
						&& null != scQt.getScQtSupplierCriteria()
								.getLocationLevel()) {
					this.getManagedBean().setLocalLevelCode(
							scQt.getScQtSupplierCriteria().getLocationLevel()
									.getParameterCode());
				}
				getManagedBean()
						.setLocationTreeNodes(new ArrayList<TreeNode>());
				ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
				locCriteria.add(criteria);
			}
			if (null != getManagedBean().getLocationTreeNodes()
					&& getManagedBean().getLocationTreeNodes().size() == 0) {

				this.getManagedBean().setExistingLocationList(locCriteria);
				getManagedBean().getLocationTreeNodes().addAll(
						LocationSingleton.getInstance().constuctTreeNode(
								locCriteria));
			}
		}

	}

	/**
	 * Retrive nonpanel zone dtls.retrive all the zones informations and
	 * display.
	 * 
	 * @param zonedtls
	 *            the zonedtls
	 */
	private void constructTreeNodeForzones(List<TplSpecZoneDtl> zonedtls) {

		List<ZonesBean> beans = getManagedBean().getZoneBeans();

		if (isNotNullOREmpty(zonedtls)) {

			for (TplSpecZoneDtl zone : zonedtls) {

				if (null != this.getManagedBean().getScQt()
						&& !this.getManagedBean().getScQt().isPanel()) {
					ZonesBean zonesBean = new ZonesBean();
					zonesBean.setTplSpecZoneDtl(zone);
					zonesBean.setTplSpecZoneLocs(zone.getTplSpecZoneLocs());
					beans.add(zonesBean);
				}

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().constuctTreeNode(zone,
								zone.getTplSpecZoneLocs()));

			}
			getManagedBean().setZoneBeans(beans);
		}

	}

	public void preRenderDirectLoa() {

		SCAwardSupplierDataType awardSupplierDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);
		retriveLoaDetails(awardSupplierDataType.getAwardId());

		contractTypeChanged();
		constructStartNEndMonth();
		this.getManagedBean().setPayLoad(awardSupplierDataType);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);

	}

	private void retriveLoaDetails(Long loaId) {
		IQTDirectAwardSO directAwardSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDirectAwardSO.class);

		DirectAwardReqDTO awardReqDTO = new DirectAwardReqDTO();
		if (null == loaId) {
			awardReqDTO.setScLoa(new ScLoa());
		} else {
			awardReqDTO.setLoaId(loaId);
		}
		DirectAwardResDTO awardResDTO = directAwardSO
				.retriveDirectAwardDetails(awardReqDTO);
		ScLoa scLoa = awardResDTO.getLoa();

		ScQt scQt = scLoa.getScQt();
		this.getManagedBean().setScQt(scLoa.getScQt());
		this.getManagedBean().setApproverUser(new PmUser());
		getManagedBean().setLoaAgencyWitness(scLoa.getScLoaWitnesses().get(0));

		if (scQt.isZonal()) {
			List<TplSpecZoneDtl> zoneDetails = awardResDTO.getSpecZoneDetails();
			constructTreeNodeForzones(zoneDetails);
		}
		this.getManagedBean().setContractSignerList(
				awardResDTO.getContractSignerList());
		this.getManagedBean().setApproverList(awardResDTO.getApproverList());
		// award details
		getManagedBean().getTplSpecMaster()
				.setItemOriginCode(new PmParameter());
		if (null != scLoa.getScLoaApproval()) {
			this.getManagedBean().setApproverUser(
					scLoa.getScLoaApproval().getApprover());
		}
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, null,
				scLoa.getScLoaRemarks(), ScLoaRemark.class));
		this.getManagedBean().setScLoa(scLoa);
		this.getManagedBean().setContractSignerList(
				awardResDTO.getApproverList());

	}

	private String getMonth(String month) {

		if (StringUtils.isEmpty(month)) {
			throw new EpException("");
		}
		int mon = Integer.parseInt(month);
		String finalMonth = null;
		if (mon <= 9) {
			finalMonth = "0" + mon;
		} else {
			return month;
		}
		return finalMonth;

	}

	private void constructStartNEndMonth() {

		List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (CollectionUtils.isNotEmpty(scheduleList)) {

			this.getManagedBean().setTotalFrequency(scheduleList.size());

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			for (ScQtSchedule scQtSchedule : scheduleList) {

				String fromdate = dateFormat.format(scQtSchedule.getFromDate());
				String todate = dateFormat.format(scQtSchedule.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());

				int start = Integer.parseInt(startdate);
				int end = Integer.parseInt(enddate);

				int sYear = Integer.parseInt(startYear);
				int eYear = Integer.parseInt(endYear);
				int diffYear = 0;
				int count = 0;

				if (eYear >= sYear) {
					if (!endYear.equals(startYear)) {
						diffYear = eYear - sYear;
					}
					for (int i = start + 1; i <= end + 1; i++) {
						count++;
					}
					count = count + (diffYear * 12);
					scQtSchedule.setTotalMonths(new BigDecimal(count));
				} else {
					scQtSchedule.setTotalMonths(BigDecimal.ZERO);
				}
				// scQtSchedule.setTotalMonths(new BigDecimal(months));

				scQtSchedule.setStartMonth(Integer.valueOf(start).toString());
				scQtSchedule.setEndMonth(Integer.valueOf(end).toString());
				scQtSchedule.setStartYear(startYear);
				scQtSchedule.setEndYear(endYear);

			}

		}

		// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());

		List<ScQtProgressivePayment> payments = this.getManagedBean()
				.getScLoa().getScQtProgPayments();
		if (CollectionUtils.isNotEmpty(payments)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (ScQtProgressivePayment payment : payments) {

				String fromdate = dateFormat.format(payment.getFromDate());
				String todate = dateFormat.format(payment.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());

				payment.setStartMonth(startdate);
				payment.setEndMonth(enddate);
				payment.setStartYear(startYear);
				payment.setEndYear(endYear);

			}

		}
	}

	/**
	 * submit LOA details. submit - save data into database and BPM process will
	 * route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitSupplierAction(String outCome) {
		debugLogger("submitAction");

		try {
			// String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			validateLOADetails(scLoa, outCome);

			parseLOADetails(scLoa, outCome);
			scLoa.setScLoaWorkFlowStatuses(null);
			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));

			DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.submitDirectAwardDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();

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
																		ParameterConst.DOCTYPE_LETTER_OF_AWARD),
																scLoa.getLoaNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			// qtAwardSO = null;
		}
		return null;
	}
	
	/**
	 * Gets the bond letter stream content.
	 *
	 * @return the bond letter stream content
	 * @throws Exception the exception
	 */
	public StreamedContent getBondLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		generateBondLetterFromVelocityToPdf((ScAttachment) pbAttachment,null);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;

	}
	
	/**
	 * Show verify button.
	 */
	public void showVeriryButton() {
	       
		if(getManagedBean().isVerifyBtnFlag()){
			getManagedBean().setVerifyBtnFlag(false);
		}
	}

}

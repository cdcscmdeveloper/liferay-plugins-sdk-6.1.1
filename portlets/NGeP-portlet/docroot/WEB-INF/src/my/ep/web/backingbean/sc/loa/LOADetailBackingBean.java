package my.ep.web.backingbean.sc.loa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.clause.IClauseSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
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
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmClause;
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
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
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
import my.ep.web.backingbean.ct.amendment.JadualHargaBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.loa.LOADetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailBackingBean")
@RequestScoped
public class LOADetailBackingBean extends
		SourcingBaseBackingBean<LOADetailManagedBean> {
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

			AwardSupplierResDTO resDTO = qtAwardSO
					.retrieveSupplierLOADtl(reqDTO);

			ScLoa scLoa = resDTO.getScLoa();
			getManagedBean().setScLoa(scLoa);

			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());
			if (!scLoa.isBrandNew()) {
				getManagedBean().setSelectedBankId(
						getSelectedBankIdByScLoa(scLoa));
			}

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
		IQTAwardSO qtAwardSO = null;
		try {
			SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class);

			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loaId = Long.valueOf(dpDataType.getAwardId());

			getManagedBean().setSourceId(sourceId);

			// Long loaId = 299L;

			ScLoa scLoa = new ScLoa();
			// scLoa.setLoaId(loaId);

			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			AwardSupplierResDTO resDTO = qtAwardSO
					.retrieveLOADtlByLoaId(reqDTO);
			if (resDTO.getScLoa() != null) {
				scLoa = resDTO.getScLoa();
			}
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
			Long sourceId = Long.valueOf(dpDataType.getSourceId());
			Long loaId = Long.valueOf(dpDataType.getAwardId());
			getManagedBean().setSourceId(sourceId);

			/*
			 * String[] outComes = new String[]{BpmOutcomeConst.BPM_ACCEPT,
			 * BpmOutcomeConst.BPM_REJECT};
			 * getManagedBean().setOutComes(outComes);
			 * getManagedBean().setTaskId("123");
			 * 
			 * long loaId = 299L;
			 */

			editSupplierLOAAction(loaId);

		} catch (Exception e) {
			handleException(e);
		}
	}

	private void populateLOADetails(AwardSupplierResDTO resDTO) {
		ScLoa scLoa = resDTO.getScLoa();
		System.out.println("===== ScLoaLoiItem : " + scLoa.getScLoiLoaItems().size());
		getManagedBean().setScLoa(scLoa);
		getManagedBean().setScQt(resDTO.getScQt());

		// Supplier Branch
		getManagedBean().setSelectedSupplierBranchName(
				scLoa.getSupplierBranch());
		getManagedBean()
				.setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
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
			// scLoaWitness.setScLoa(scLoa);
			if (ParameterConst.WITNESS_PARTY_TYPE_AGENCY.equals(partyType)) {
				loaAgencyWitness = scLoaWitness;
			} else if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER
					.equals(partyType)) {
				loaSupplierWitness = scLoaWitness;
			}
		}
		getManagedBean().setLoaAgencyWitness(loaAgencyWitness);
		getManagedBean().setLoaSupplierWitness(loaSupplierWitness);

		// Issue & Acknowledge Date
		scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));

		// Fulfilment Schedule

		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();

		if (null != scheduleList && !scheduleList.isEmpty()) {

			this.getManagedBean().setTotalFrequency(scheduleList.size());
			String startMonth = "";
			String endMonth = "";
			for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				String fromdate = dateFormat.format(scQtSchedule.getFromDate());
				String todate = dateFormat.format(scQtSchedule.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());

				if (startdate.equalsIgnoreCase("01")) {
					startMonth = "0";
				}
				if (startdate.equalsIgnoreCase("02")) {
					startMonth = "1";
				}
				if (startdate.equalsIgnoreCase("03")) {
					startMonth = "2";
				}
				if (startdate.equalsIgnoreCase("04")) {
					startMonth = "3";
				}
				if (startdate.equalsIgnoreCase("05")) {
					startMonth = "4";
				}
				if (startdate.equalsIgnoreCase("06")) {
					startMonth = "5";
				}

				if (startdate.equalsIgnoreCase("07")) {
					startMonth = "6";
				}
				if (startdate.equalsIgnoreCase("08")) {
					startMonth = "7";
				}
				if (startdate.equalsIgnoreCase("09")) {
					startMonth = "8";
				}
				if (startdate.equalsIgnoreCase("10")) {
					startMonth = "9";
				}
				if (startdate.equalsIgnoreCase("11")) {
					startMonth = "10";
				}
				if (startdate.equalsIgnoreCase("12")) {
					startMonth = "11";
				}

				if (enddate.equalsIgnoreCase("01")) {
					endMonth = "0";
				}
				if (enddate.equalsIgnoreCase("02")) {
					endMonth = "1";
				}
				if (enddate.equalsIgnoreCase("03")) {
					endMonth = "2";
				}
				if (enddate.equalsIgnoreCase("04")) {
					endMonth = "3";
				}
				if (enddate.equalsIgnoreCase("05")) {
					endMonth = "4";
				}
				if (enddate.equalsIgnoreCase("06")) {
					endMonth = "5";
				}

				if (enddate.equalsIgnoreCase("07")) {
					endMonth = "6";
				}
				if (enddate.equalsIgnoreCase("08")) {
					endMonth = "7";
				}
				if (enddate.equalsIgnoreCase("09")) {
					endMonth = "8";
				}
				if (enddate.equalsIgnoreCase("11")) {
					endMonth = "10";
				}
				if (enddate.equalsIgnoreCase("12")) {
					endMonth = "11";
				}
				int start = Integer.parseInt(startMonth);
				int end = Integer.parseInt(endMonth);
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
				scQtSchedule.setStartMonth(startMonth);
				scQtSchedule.setEndMonth(endMonth);
				scQtSchedule.setStartYear(startYear);
				scQtSchedule.setEndYear(endYear);
			}
			this.sortFrequentScheduleList(scLoa.getScQtSchedules());

			// this.getManagedBean().setScheduleList(scheduleList);
		}

		if (getManagedBean().getScQt() != null) {
			List<ScQtProgressivePayment> progressivePayment = this
					.getManagedBean().getProgressivePaymentList();
			if (progressivePayment == null) {
				progressivePayment = new ArrayList<ScQtProgressivePayment>();

				if (null != scLoa.getScQtProgPayments()
						&& !scLoa.getScQtProgPayments().isEmpty()) {
					this.getManagedBean().setTotalProgressivePayment(
							scLoa.getScQtProgPayments().size());
					String startMonth = "";
					String endMonth = "";
					for (ScQtProgressivePayment scQtProgressivePayment : scLoa
							.getScQtProgPayments()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String fromdate = dateFormat
								.format(scQtProgressivePayment.getFromDate());
						String todate = dateFormat
								.format(scQtProgressivePayment.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
						String enddate = todate.substring(3, 5);
						String endYear = todate.substring(6, todate.length());

						if (startdate.equalsIgnoreCase("01")) {
							startMonth = "0";
						}
						if (startdate.equalsIgnoreCase("02")) {
							startMonth = "1";
						}
						if (startdate.equalsIgnoreCase("03")) {
							startMonth = "2";
						}
						if (startdate.equalsIgnoreCase("04")) {
							startMonth = "3";
						}
						if (startdate.equalsIgnoreCase("05")) {
							startMonth = "4";
						}
						if (startdate.equalsIgnoreCase("06")) {
							startMonth = "5";
						}

						if (startdate.equalsIgnoreCase("07")) {
							startMonth = "6";
						}
						if (startdate.equalsIgnoreCase("08")) {
							startMonth = "7";
						}
						if (startdate.equalsIgnoreCase("09")) {
							startMonth = "8";
						}
						if (startdate.equalsIgnoreCase("10")) {
							startMonth = "9";
						}
						if (startdate.equalsIgnoreCase("11")) {
							startMonth = "10";
						}
						if (startdate.equalsIgnoreCase("12")) {
							startMonth = "11";
						}

						if (enddate.equalsIgnoreCase("01")) {
							endMonth = "0";
						}
						if (enddate.equalsIgnoreCase("02")) {
							endMonth = "1";
						}
						if (enddate.equalsIgnoreCase("03")) {
							endMonth = "2";
						}
						if (enddate.equalsIgnoreCase("04")) {
							endMonth = "3";
						}
						if (enddate.equalsIgnoreCase("05")) {
							endMonth = "4";
						}
						if (enddate.equalsIgnoreCase("06")) {
							endMonth = "5";
						}

						if (enddate.equalsIgnoreCase("07")) {
							endMonth = "6";
						}
						if (enddate.equalsIgnoreCase("08")) {
							endMonth = "7";
						}
						if (enddate.equalsIgnoreCase("09")) {
							endMonth = "8";
						}
						if (enddate.equalsIgnoreCase("11")) {
							endMonth = "10";
						}
						if (enddate.equalsIgnoreCase("12")) {
							endMonth = "11";
						}
						scQtProgressivePayment.setStartMonth(startMonth);
						scQtProgressivePayment.setEndMonth(endMonth);
						scQtProgressivePayment.setStartYear(startYear);
						scQtProgressivePayment.setEndYear(endYear);
						progressivePayment.add(scQtProgressivePayment);

					}
					this.sortProgressivePaymentList(progressivePayment);
					this.getManagedBean().setProgressivePaymentList(
							progressivePayment);
				}
			}
		}

		// getManagedBean().getScLoa().setContractPTJProfile(orgProfile);

		/*
		 * getManagedBean().getScLoa().setSmSupplier(
		 * getManagedBean().getScQtEvaluationResult() .getScQtProposal()
		 * .getScQtSupplier().getSmSupplier());
		 */

		/*
		 * getManagedBean().getScLoa().setSupplierName(
		 * getManagedBean().getScQtProposalRanking()
		 * .getScQtProposalEvaluate().getScQtProposal()
		 * .getScQtSupplier().getSmSupplier() .getSupplierName());
		 */

		/*
		 * getManagedBean().getScLoa().setDocType(
		 * LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
		 * getManagedBean().getScLoa().setDocId(id);
		 */

		/*
		 * getManagedBean().getScLoa().setLoaType("Standard");
		 * getManagedBean().getScLoa().setLoaDuration(14);
		 * getManagedBean().getScLoa().setBondDuration(30);
		 * getManagedBean().getScLoa().setLoaNo("12345");
		 * getManagedBean().getScLoa().setLoaAmount( new BigDecimal(1000000));
		 */
		// getManagedBean().getScLoa().setMtoDuration(14);

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

			if (!QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa
					.getPmStatus().getStatusId().longValue())) {
				getManagedBean().setScLoaApproval(scLoa.getScLoaApproval());
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		getManagedBean().setApproverList(resDTO.getApproverList());

		// Remarks
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, null, scLoa.getScLoaRemarks(),
				ScLoaRemark.class));

		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}
		/*
		 * branch.setSmAppl(getManagedBean().getScQtProposalRanking()
		 * .getScQtProposalEvaluate().getScQtProposal()
		 * .getScQtSupplier().getSmSupplier().getSmAppl());
		 */

		// TODO
		/*
		 * if (getManagedBean().getScLoa().getLoaAmt().intValue() > 500000) {
		 * getManagedBean().setBondPercentage("5%");
		 * getManagedBean().setPercentageBond(0.05);
		 * System.out.println("Bond Percentage : " +
		 * getManagedBean().getPercentageBond()); } else if
		 * (getManagedBean().getScLoa().getLoaAmt() .intValue() > 200000 &&
		 * getManagedBean().getScLoa().getLoaAmt() .intValue() < 500000) {
		 * getManagedBean().setBondPercentage("2.5%");
		 * getManagedBean().setPercentageBond(0.025);
		 * System.out.println("Bond Percentage : " +
		 * getManagedBean().getPercentageBond()); }
		 */

		/*
		 * int amt = (int) (((getManagedBean().getScLoa().getLoaAmount()
		 * .intValue() / getManagedBean().getQt().getScQtSpec()
		 * .getContractDuration().intValue()) * 12) * getManagedBean()
		 * .getPercentageBond());
		 */

		// getManagedBean().setBondAmount(amt);
	}

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
						deductionClause.setClauseDesc(pmClause.getClauseDesc());
						deductionClause.setScQtDeduction(scQtDeduction);
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
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoaReport.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

		scLoaReports.add(scLoaReport);
	}

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
				scAttachment = scLoaReportAttachment;
			}

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");

		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();

		if (scLoaReportAttachment == null) {
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
		}

		downloadFile(scLoaReportAttachment);
	}

	private ScLoaReportAttachment generateDocumentAttachment(
			ScLoaReport scLoaReport) {
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = null;

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();

				scLoaReportAttachment = new ScLoaReportAttachment();
				scLoaReportAttachment.setScLoaReport(scLoaReport);
				scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
						.getLoaNo());

				if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
						.equals(docDefinitionKey)) {
					generateContractSummaryPdfFromVelocity(
							(ScAttachment) scLoaReportAttachment,
							getManagedBean().getScLoa());
				} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
						.equals(docDefinitionKey)) {
					generateAgreementLetterPdfFromVelocity(
							(ScAttachment) scLoaReportAttachment,
							getManagedBean().getScLoa());
				} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
						.equals(docDefinitionKey)) {
					generateDeclarationPdfFromVelocity(
							(ScAttachment) scLoaReportAttachment,
							getManagedBean().getScLoa());
				} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
						.equals(docDefinitionKey)) {
					saveScheduleList();
					generateFrequencySchedulePdfFromVelocity(
							(ScAttachment) scLoaReportAttachment,
							getManagedBean().getScLoa());
				} else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
						.equals(docDefinitionKey)) {
					formJadualHarga((ScAttachment) scLoaReportAttachment,
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

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa()
				.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();

			if (scLoaReportAttachment == null) {
				scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			}

			scAttachments.add(scLoaReportAttachment);
		}

		downloadAllFiles("NGeP-LOA-Report", scAttachments);
	}

	public void generateContractSummaryPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {

		attachment.setFileName("contract_summary.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
		context.put("docNo", "");
		context.put("title", scLoiLoa.getTitle());
		context.put("supplierName", scLoiLoa.getSupplierName());
		context.put("loaAmt", amountDF.format(scLoa.getLoaAmt()));
		context.put("fulfilmentType",
				getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));

		StringBuilder deductionTypes = new StringBuilder();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();
		if (scQtDeductions != null) {
			for (ScQtDeduction scQtDeduction : scQtDeductions) {
				if (deductionTypes.length() > 0) {
					deductionTypes.append(",");
				}
				deductionTypes.append(getParameterDesc(scQtDeduction
						.getDeductionType()));
			}
		}
		context.put("deductionTypes", deductionTypes.toString());

		context.put("contractDuration", scLoa.getContractDuration());
		context.put("contractEffDate",
				dateSDF.format(scLoa.getContractEffDate()));
		context.put("contractExpDate",
				dateSDF.format(scLoa.getContractExpDate()));

		String bondAmtStr = "";
		if (scLoa.getBondAmt() != null) {
			bondAmtStr = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmtStr);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_CONTRACT_SUMMARY, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	public void generateAgreementLetterPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {

		attachment.setFileName("agreement_letter.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		PmUser ackByUser = scLoa.getAckBy();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		VelocityContext context = new VelocityContext();

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		IOrgProfileSO orgProfileSO = null;
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

			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(orgProfileReqDTO);

			if (orgProfileResDTO.getPmOrgProfile() != null) {
				context.put("agency", orgProfileResDTO.getPmOrgProfile()
						.getOrgName());
				context.put("ministry", orgProfileResDTO.getPmOrgProfile()
						.getParentOrgProfile().getOrgName());
			}

		} finally {
			addressSO = null;
			orgProfileSO = null;
		}

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
		String supplierPhoneNo = scLoa.getSupplierPhoneNo();
		if (supplierPhoneNo == null) {
			supplierPhoneNo = "";
		}
		context.put("supplierPhoneNo", supplierPhoneNo);
		String supplierFaxNo = scLoa.getSupplierFaxNo();
		if (supplierFaxNo == null) {
			supplierFaxNo = "";
		}
		context.put("supplierFaxNo", supplierFaxNo);
		String supplierMobNo = scLoa.getSupplierMobNo();
		if (supplierMobNo == null) {
			supplierMobNo = "";
		}
		context.put("supplierMobNo", supplierMobNo);
		String supplierEmail = scLoa.getSupplierEmail();
		if (supplierEmail == null) {
			supplierEmail = "";
		}
		context.put("supplierEmail", supplierEmail);

		context.put("procurementMode", "Direct Purchase"); // TODO
		context.put("title", scLoiLoa.getTitle());
		context.put("hasLoi", false); // TODO
		context.put("bondReq", scLoa.isBondReq());

		String bondAmt = "";
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmt);
		context.put("bondAmtText", bondAmt); // TODO

		String bondPercentage = "";
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage());
		}
		context.put("bondPercentage", bondPercentage);
		context.put("bondExpDate", ""); // TODO

		String bondResDueDays = "";
		if (scLoa.getBondResDueDate() != null && scLoa.getAckDate() != null) {
			bondResDueDays = DateUtils.getDaysDifference(
					scLoa.getBondResDueDate(), scLoa.getAckDate())
					+ "";
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
		context.put("contractSignerDesignation", scLoa.getContractSigner()
				.getDesignation());

		// Supplier user details
		String supplierUser = "";
		String supplierIcNo = "";
		String supplierDesignation = "";
		if (ackByUser != null) {
			supplierUser = ackByUser.getUserName();
			supplierIcNo = ackByUser.getIdentificationNo();
			supplierDesignation = ackByUser.getDesignation();
		}
		context.put("supplierUser", supplierUser);
		context.put("supplierIcNo", supplierIcNo);
		context.put("supplierDesignation", supplierDesignation);

		// Supplier Witness
		String supplierWitnessUser = "";
		String supplierWitnessIcNo = "";
		String supplierWitnessDesignation = "";
		String supplierWitnessAddress = "";
		String supplierWitnessPhoneNo = "";
		String supplierWitnessFaxNo = "";
		String supplierWitnessMobNo = "";
		String supplierWitnessEmail = "";
		ScLoaWitness supplierWitness = null;
		for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
			String partyType = scLoaWitness.getPartyType();

			if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
				supplierWitness = scLoaWitness;

				break;
			}
		}
		if (supplierWitness != null) {
			supplierWitnessUser = supplierWitness.getWitnessName();
			supplierWitnessIcNo = supplierWitness.getIdentificationNo();
			supplierWitnessDesignation = supplierWitness.getDesignation();
			supplierWitnessAddress = supplierWitness.getAddress();
			supplierWitnessPhoneNo = supplierWitness.getPhoneNo();
			supplierWitnessFaxNo = supplierWitness.getFaxNo();
			supplierWitnessMobNo = supplierWitness.getMobileNo();
			supplierWitnessEmail = supplierWitness.getEmail();
		}
		context.put("supplierWitnessUser", supplierWitnessUser);
		context.put("supplierWitnessIcNo", supplierWitnessIcNo);
		context.put("supplierWitnessDesignation", supplierWitnessDesignation);
		context.put("supplierWitnessAddress", supplierWitnessAddress);
		context.put("supplierWitnessPhoneNo", supplierWitnessPhoneNo);
		context.put("supplierWitnessFaxNo", supplierWitnessFaxNo);
		context.put("supplierWitnessMobNo", supplierWitnessMobNo);
		context.put("supplierWitnessEmail", supplierWitnessEmail);

		// Bank account details.
		String bankAccNo = scLoa.getBankAccNo();
		if (bankAccNo == null) {
			bankAccNo = "";
		}
		context.put("bankAccNo", bankAccNo);
		String bankName = scLoa.getBankName();
		if (bankName == null) {
			bankName = "";
		}
		context.put("bankName", bankName);
		String bankBranch = scLoa.getBankBranch();
		if (bankBranch == null) {
			bankBranch = "";
		}
		context.put("bankBranch", bankBranch);
		String bankAddress = "";
		if (scLoa.getBankAddress() != null) {
			bankAddress = getFullAddress(scLoa.getBankAddress());
			bankAddress = bankAddress.replaceAll("\r\n", "<br />");
		}
		context.put("bankAddress", bankAddress);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_AGREEMENT_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	public void generateDeclarationPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {
		String termsConditions = generateDeclarationFromVelocity(scLoa);

		attachment.setFileName("integrity_pact.pdf");
		populateAttachmentDetails(attachment);

		DocumentGenerator.getInstance().generatePdf(
				new FileOutputStream(getActualFilePath(attachment)),
				termsConditions);
	}

	private String generateDeclarationFromVelocity(ScLoa scLoa)
			throws Exception {
		PmUser pmUser = scLoa.getAckBy();
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();

		context.put("title", scLoiLoa.getTitle());

		String userName = "&#60;supplier user name&#62;";
		String icNo = "&#60;ic no&#62;";
		if (pmUser != null) {
			userName = pmUser.getUserName();
			icNo = pmUser.getIdentificationNo();
		}
		context.put("userName", userName);
		context.put("icNo", icNo);

		context.put("supplierName", scLoiLoa.getSupplierName());
		context.put("mofNo", ""); // TODO
		context.put("agency", pmOrgProfile.getOrgName());

		String termsConditions = DocumentGenerator
				.getInstance()
				.getHtmlFromVelocity(SourcingConstants.LOA_DECLARATION, context);

		getManagedBean().setTermsConditions(termsConditions);

		return termsConditions;
	}

	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = getManagedBean()
				.getLoaStreamContent();

		if (loaStreamContent == null) {
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

			// getManagedBean().getScLoa().setOfficierName(user.getUserName());
			// getManagedBean().getScLoa().setOfficerDesignation(
			// user.getDesignation());

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setUserId(user.getUserId());
			addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);

			List<PmAddressType> addList = addResDTO.getAddressTypeList();

			/*
			 * getManagedBean().getScLoa().setOfficerAddress(
			 * addList.get(0).getPmAddress().getAddress1());
			 */
			/*
			 * getManagedBean().getScLoa().setOfficerFaxNo(
			 * addList.get(0).getPmAddress().getFaxNo());
			 * getManagedBean().getScLoa().setOfficerPhoneNo(
			 * addList.get(0).getPmAddress().getPhoneNo());
			 */
			/*
			 * getManagedBean().getScLoa().setOfficerEmail(
			 * addList.get(0).getPmAddress().getEmail());
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

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

	private Long getSelectedBankIdByScLoa(ScLoa scLoa) {
		Long selectedBankId = null;
		SupplierBankFK loaSupplierBankFK = new SupplierBankFK(scLoa);
		List<SmSupplierBank> bankList = this.getManagedBean().getBankList();
		if (bankList != null) {
			for (SmSupplierBank supplierBank : bankList) {
				if (new SupplierBankFK(supplierBank).equals(loaSupplierBankFK)) {
					log.info("found supplier bank");

					selectedBankId = supplierBank.getSupplierBankId();

					break;
				}
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
		 * @param accountNo
		 *            the accountNo to set
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
		 * @param bankName
		 *            the bankName to set
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
		 * @param bankBranch
		 *            the bankBranch to set
		 */
		public void setBankBranch(String bankBranch) {
			this.bankBranch = bankBranch;
		}

		private String getPK(SupplierBankFK fk) {
			return new StringBuilder(
					fk.getAccountNo() != null ? fk.getAccountNo() : "")
					.append("_")
					.append(fk.getBankName() != null ? fk.getBankName() : "")
					.append("_")
					.append(fk.getBankBranch() != null ? fk.getBankBranch()
							: "").toString();
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

				// renewSesionObject();

				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					editSupplierLOAAction(scLoa.getLoaId());
				} else {
					editLOAAction(scLoa);
				}

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700004,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_LETTER_OF_AWARD),
																scLoa.getLoaNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
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

			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			validateLOADetails(scLoa, outcome);

			parseLOADetails(scLoa);

			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			scLoa.setAckDueDate(DateUtils.addDays(scLoa.getIssueDate(), 14));

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();

				renewSesionObject();

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
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			validateLOADetails(scLoa, outcome);

			parseLOADetails(scLoa);

			// if reject, remove the new witness
			if(BpmOutcomeConst.BPM_REJECT.equals(outcome)){
				ScLoaWitness scLoaWitness = null;
				Iterator<ScLoaWitness> it = scLoa.getScLoaWitnesses().iterator();
	            while (it.hasNext()) {
	            	scLoaWitness = it.next();
	                if (scLoaWitness.isBrandNew() && null == scLoaWitness.getWitnessName()) {
	                    it.remove();
	                }
	            }
			}
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoa(scLoa);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());

			AwardSupplierResDTO resDTO = qtAwardSO.saveLOADtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getScLoa();

				renewSesionObject();

				String messageCode = null;

				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_CONTRACT_SIGNING
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700013;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}					
				} else if (QTStatusConst.LOA_PENDING_VERIFICATION
						.equals(statusId)) {
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

	private void validateLOADetails(ScLoa scLoa, String outcome) {
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_ACCEPT.equals(outcome)) {

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
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			scLoa.setBondResDueDate(new Date());
		} else {
			saveScheduleList();
			saveProgressivePaymntList();
		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
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
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.MINISTRY.value());

				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				getManagedBean().setAgencyProfileList(
						resDTO.getOrgProfileList());

			} finally {
				orgProfileSO = null;
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

			if (scLoa.getContractAgency().getOrgProfileId() != null) {
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
				}
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
		// ScBondDtlAttachment attachment = bondDtl.getScBondDtlAttachment();
		/*
		 * if (null == attachment) { attachment = new ScBondDtlAttachment();
		 * attachment.setScBondDtl(bondDtl); }
		 */

		// uploadAttachment(event.getFile(), attachment);

		// bondDtl.setScBondDtlAttachment(attachment);

	}

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
					PmParameter parameter = new PmParameter();
					schedule.setScheduleType(parameter);
					setUserDetails(schedule);
					freqSchedule.add(schedule);
				}
				this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);
			}

		} finally {

		}
	}

	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, "--Select One--"));
			selectItems.add(new SelectItem(0, "January"));
			selectItems.add(new SelectItem(1, "February"));
			selectItems.add(new SelectItem(2, "March"));
			selectItems.add(new SelectItem(3, "April"));
			selectItems.add(new SelectItem(4, "May"));
			selectItems.add(new SelectItem(5, "June"));
			selectItems.add(new SelectItem(6, "July"));
			selectItems.add(new SelectItem(7, "August"));
			selectItems.add(new SelectItem(8, "September"));
			selectItems.add(new SelectItem(9, "October"));
			selectItems.add(new SelectItem(10, "November"));
			selectItems.add(new SelectItem(11, "December"));

			this.getManagedBean().setYearList(selectItems);
		} finally {

		}
		return getManagedBean().getYearList();
	}

	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	public List<SelectItem> getYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			selectItems.add(new SelectItem(null, "--Select One--"));
			selectItems.add(new SelectItem(year, String.valueOf(year)));
			for (int i = 0; i < 20; i++) {
				year = year + 1;
				selectItems.add(new SelectItem(year, String.valueOf(year)));
			}

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	public void calculateScheduleMonths(ScQtSchedule schedule) {

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

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null && endYear >= startYear) {
			if (endYear != startYear) {
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

	private void saveScheduleList() {
		List<ScQtSchedule> freqschedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
		if (freqschedule != null && !freqschedule.isEmpty()) {
			String startMonth = "";
			String endmonth = "";
			for (ScQtSchedule schedule : freqschedule) {
				if (schedule.getStartMonth().equalsIgnoreCase("0")) {
					startMonth = "01";
				} else if (schedule.getStartMonth().equalsIgnoreCase("1")) {
					startMonth = "02";
				} else if (schedule.getStartMonth().equalsIgnoreCase("2")) {
					startMonth = "03";
				} else if (schedule.getStartMonth().equalsIgnoreCase("3")) {
					startMonth = "04";
				} else if (schedule.getStartMonth().equalsIgnoreCase("4")) {
					startMonth = "05";
				} else if (schedule.getStartMonth().equalsIgnoreCase("5")) {
					startMonth = "06";
				}

				else if (schedule.getStartMonth().equalsIgnoreCase("6")) {
					startMonth = "07";
				} else if (schedule.getStartMonth().equalsIgnoreCase("7")) {
					startMonth = "08";
				} else if (schedule.getStartMonth().equalsIgnoreCase("8")) {
					startMonth = "09";
				} else if (schedule.getStartMonth().equalsIgnoreCase("9")) {
					startMonth = "10";
				} else if (schedule.getStartMonth().equalsIgnoreCase("10")) {
					startMonth = "11";
				} else if (schedule.getStartMonth().equalsIgnoreCase("11")) {
					startMonth = "12";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				} else if (schedule.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				} else if (schedule.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}

				else if (schedule.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				} else if (schedule.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				} else if (schedule.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}

				else if (schedule.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				} else if (schedule.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				} else if (schedule.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				} else if (schedule.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				} else if (schedule.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				} else if (schedule.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}
				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/" + startMonth + "/"
						+ schedule.getStartYear();
				String toDate = date + "/" + endmonth + "/"
						+ schedule.getEndYear();

				schedule.setScheduleNo(schedule.getScheduleNo());

				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					throw new EpException(e.toString());
				}
				if (schedule.getScLoa() == null) {
					schedule.setScLoa(this.getManagedBean().getScLoa());
				}
				localScheduleList.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(localScheduleList);
		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
		}
	}

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

	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScLoa scLoa) throws Exception {
		attachment.setFileName("Frequency_Schedule.pdf");
		populateAttachmentDetails(attachment);
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		IParameterSO parameterSO = null;
		VelocityContext context = new VelocityContext();
		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();
		List<ScQtSchedule> scheduleReportList = new ArrayList<ScQtSchedule>();
		ScQtSchedule reportSchedule = null;
		int totalFrequency = 0;
		if (scheduleList != null && scheduleList.size() > 0) {
			totalFrequency = scheduleList.size();
			for (ScQtSchedule schedule : scheduleList) {
				try {
					reportSchedule = new ScQtSchedule();
					parameterSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(schedule.getScheduleType()
							.getParameterId());
					ParameterResDTO resDTO = parameterSO
							.retrieveParameterById(reqDTO);
					reportSchedule.setScheduleType(resDTO.getPmParameter());
					reportSchedule.setStartMonth(monthFormat.format(schedule
							.getFromDate()));
					reportSchedule.setStartYear(yearFormat.format(schedule
							.getFromDate()));
					reportSchedule.setEndMonth(monthFormat.format(schedule
							.getToDate()));
					reportSchedule.setEndYear(yearFormat.format(schedule
							.getToDate()));
					reportSchedule.setScheduleNo(schedule.getScheduleNo());
					reportSchedule.setTotalMonths(schedule.getTotalMonths());
					scheduleReportList.add(reportSchedule);

				} finally {
					parameterSO = null;
				}
			}
		}
		context.put("totalFrequency", totalFrequency);
		context.put("scheduleList", scheduleReportList);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_FREQUENCY_SCHEDULE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	public void formJadualHarga(ScAttachment attachment, ScLoa scLoa)
			throws Exception {
		attachment.setFileName("JadualHarga.xlsx");
		populateAttachmentDetails(attachment);
		List<JadualHargaBean> productJadualHargaList = new ArrayList<>();
		List<JadualHargaBean> serviceJadualHargaList = new ArrayList<>();
		JadualHargaBean jadualHarga = null;

		for (ScLoiLoaItem item : scLoa.getScLoiLoaItems()) {
			jadualHarga = mappingJadualHarga(item);
			if (getParameterDesc(item.getScRequestItem().getItemType())
					.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT_STRING)) {
				productJadualHargaList.add(jadualHarga);
			} else if (getParameterDesc(item.getScRequestItem().getItemType())
					.equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE_STRING)) {
				serviceJadualHargaList.add(jadualHarga);
			}

		}

		generateJadualHarga(attachment, productJadualHargaList,
				serviceJadualHargaList);

	}

	private JadualHargaBean mappingJadualHarga(ScLoiLoaItem item) {
		JadualHargaBean jadualHarga = new JadualHargaBean();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat nf = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		jadualHarga.setZone(item.getTplSpecZoneDtl() != null ? item
				.getTplSpecZoneDtl().getZoneName() : "N/A");
		jadualHarga.setItem(item.getScRequestItem().getItemName());
		jadualHarga.setUnitPrice(nf.format(item.getUnitPrice().longValue()));
		jadualHarga.setUom(item.getScRequestItem().getTplSpecQuestion()
				.getClUom().getUomName());
		jadualHarga.setFreqUom(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomFrequency() ? "N/A" : item
				.getScRequestItem().getTplSpecQuestion().getClUomFrequency()
				.toString());
		jadualHarga.setUomPerDay(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomPerDay() ? "N/A" : item
				.getScRequestItem().getTplSpecQuestion().getClUomPerDay()
				.toString());
		jadualHarga.setUomPerMonth(null == item.getScRequestItem()
				.getTplSpecQuestion().getClUomPerMonth() ? "N/A" : item
				.getScRequestItem().getTplSpecQuestion().getClUomPerMonth()
				.toString());
		jadualHarga.setUomPerYear(null == item.getScRequestItem()
				.getTplSpecQuestion().getClDurationMonth() ? "N/A" : item
				.getScRequestItem().getTplSpecQuestion().getClDurationMonth()
				.toString());
		jadualHarga.setQuantity(null == item.getScRequestItem()
				.getTplSpecQuestion().getClQuantity() ? "N/A" : item
				.getScRequestItem().getTplSpecQuestion().getClQuantity()
				.toString());
		jadualHarga.setTotalAmount(item
				.getUnitPrice()
				.multiply(
						item.getScRequestItem().getTplSpecQuestion()
								.getClQuantity()).toString());

		return jadualHarga;

	}

	private void generateJadualHarga(ScAttachment attachment,
			List<JadualHargaBean> productList, List<JadualHargaBean> serviceList)
			throws Exception {
		if (serviceList.size() > 0 || productList.size() > 0) {
			try {
				FileInputStream inputStream = new FileInputStream(new File(
						FacesContext.getCurrentInstance().getExternalContext()
								.getRealPath("/WEB-INF/templates")
								+ "/sc/loa/jadual-harga.xlsx"));
				XSSFWorkbook wb = new XSSFWorkbook(inputStream); // or new
																	// HSSFWorkbook();
				boolean emptyProductList = false;
				Sheet productSheet = null;
				Sheet serviceSheet = null;
				Row row = null;
				int rowIndex = 0;
				int cellIndex = 0;
				if (productList.size() > 0) {
					rowIndex = 0;
					productSheet = wb.getSheetAt(0);
					wb.setSheetName(0, "ProductSheet");
					for (JadualHargaBean product : productList) {
						rowIndex++;
						cellIndex = 0;
						row = productSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(
								product.getZone());
						row.createCell(cellIndex++).setCellValue(
								product.getItem());
						row.createCell(cellIndex++).setCellValue(
								product.getUom());
						row.createCell(cellIndex++).setCellValue(
								product.getQuantity());
						row.createCell(cellIndex++).setCellValue(
								product.getSubSequence());
						row.createCell(cellIndex++).setCellValue(
								product.getUnitPrice());
						row.createCell(cellIndex++).setCellValue(
								product.getTotalAmount());
					}
					// Auto size all the columns
					for (int x = 0; x < productSheet.getRow(0)
							.getPhysicalNumberOfCells(); x++) {
						productSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(0);
					emptyProductList = true;
				}

				if (serviceList.size() > 0) {
					rowIndex = 0;
					if (emptyProductList) {
						serviceSheet = wb.getSheetAt(0);
						wb.setSheetName(0, "ServiceSheet");
					} else {
						serviceSheet = wb.getSheetAt(1);
						wb.setSheetName(1, "ServiceSheet");
					}

					for (JadualHargaBean service : serviceList) {
						rowIndex++;
						cellIndex = 0;
						row = serviceSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(
								service.getZone());
						row.createCell(cellIndex++).setCellValue(
								service.getItem());
						row.createCell(cellIndex++).setCellValue(
								service.getUom());
						row.createCell(cellIndex++).setCellValue(
								service.getFreqUom());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerDay());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerMonth());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerYear());
						row.createCell(cellIndex++).setCellValue(
								service.getQuantity());
						row.createCell(cellIndex++).setCellValue(
								service.getUnitPrice());
						row.createCell(cellIndex++).setCellValue(
								service.getTotalAmount());

					}

					for (int x = 0; x < serviceSheet.getRow(0)
							.getPhysicalNumberOfCells(); x++) {
						serviceSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(1);
				}

				wb.setActiveSheet(0);
				FileOutputStream os = new FileOutputStream(
						getActualFilePath(attachment));
				wb.write(os);
				os.flush();
				os.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void generateSuppLoiLoaList() {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			SuppLoiLoaBean suppLoaLoi = null;
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			List<SuppLoiLoaBean> suppLoiLoaList = new ArrayList<>();
			if (getManagedBean().getSelectedLoiLoa() != null) {
				for (ScLoi scLoi : getManagedBean().getSelectedLoiLoa()
						.getScLois()) {
					suppLoaLoi = new SuppLoiLoaBean();
					suppLoaLoi.setLoiLoaNo(scLoi.getLoiNo());
					suppLoaLoi.setType(SourcingConstants.SUPP_LOI_TYPE);
					// suppLoaLoi.setStatus(getStatusDescById(scLoi.get()
					// .getStatusId()));
					suppLoaLoi.setScLoi(scLoi);
					suppLoiLoaList.add(suppLoaLoi);
				}

				for (ScLoa scLoa : getManagedBean().getSelectedLoiLoa()
						.getScloas()) {
					suppLoaLoi = new SuppLoiLoaBean();
					suppLoaLoi.setLoiLoaNo(scLoa.getLoaNo());
					suppLoaLoi.setType(SourcingConstants.SUPP_LOA_TYPE);
					// suppLoaLoi.setStatus(getStatusDescById(scLoa.getPmStatus()
					// .getStatusId()));
					reqDTO.setLoaId(scLoa.getLoaId());
					resDTO = qtAwardSO.retrieveLOADtlByLoaId(reqDTO);
					suppLoaLoi.setScLoa(resDTO.getScLoa());
					suppLoiLoaList.add(suppLoaLoi);

				}

				getManagedBean().setSuppLoiLoaList(suppLoiLoaList);
			}
		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}

	public void showItemDetailsPopUps(SuppLoiLoaBean suppLoiLoaBean) {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (suppLoiLoaBean.getScLoa() != null) {
				reqDTO.setScLoa(suppLoiLoaBean.getScLoa());
				resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			} else if (suppLoiLoaBean.getScLoi() != null) {
				reqDTO.setScLoi(suppLoiLoaBean.getScLoi());
				resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					suppLoiLoaBean.setScLoa(resDTO.getScLoa());
					suppLoiLoaBean.setScLoiLoaItems(resDTO.getScLoa()
							.getScLoiLoaItems());
				} else if (resDTO.getScLoi() != null) {
					suppLoiLoaBean.setScLoi(resDTO.getScLoi());
					suppLoiLoaBean.setScLoiLoaItems(resDTO.getScLoi()
							.getScLoiLoaItems());
				}
			}

			getManagedBean().setSelectedSuppLoiLoaBean(suppLoiLoaBean);

		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}

	public String createQtLOAAction(ScLoiLoa scLoiLoa) {
		IStatusSO statusSO = null;
		ScLoa scLoa = new ScLoa();
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			
			scLoa.setScLoiLoa(scLoiLoa);
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

		return editLOAAction(scLoa);

	}

	public String goQtEditLoaAction(SuppLoiLoaBean suppLoiLoaBean) {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (suppLoiLoaBean.getScLoa() != null) {
				reqDTO.setScLoa(suppLoiLoaBean.getScLoa());
				resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			} else if (suppLoiLoaBean.getScLoi() != null) {
				reqDTO.setScLoi(suppLoiLoaBean.getScLoi());
				resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					return editLOAAction(resDTO.getScLoa());
				} else if (resDTO.getScLoi() != null) {
					return null;
				}
			}

		} catch (Exception e) {
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
		return "";
	}

	public void addProgressiveDetails() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
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
				// payment.setScheduleType(new BigDecimal(1));

				setUserDetails(payment);
				progressive.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressive);
		}
	}

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
				payment.setScLoa(getManagedBean().getScLoa());
				paymentList.add(payment);
			}
			getManagedBean().getScLoa().setScQtProgPayments(paymentList);

		} else {
			getManagedBean().getScLoa().setScQtProgPayments(null);
		}
	}

	public StreamedContent getBondLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		generateBondLetterFromVelocityToPdf((ScAttachment) pbAttachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;

	}

	private void generateBondLetterFromVelocityToPdf(ScAttachment attachment)
			throws Exception {
		attachment.setFileName("Bond Letter.pdf");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_BOND_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private void sortProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		Comparator<ScQtProgressivePayment> comparator = new Comparator<ScQtProgressivePayment>() {
			@Override
			public int compare(ScQtProgressivePayment o1,
					ScQtProgressivePayment o2) {
				return new CompareToBuilder().append(o1.getPhaseNo(),
						o2.getPhaseNo()).toComparison();
			}
		};
		Collections.sort(progressivePaymentList, comparator);
	}

	private void sortFrequentScheduleList(List<ScQtSchedule> scQtScheduleList) {
		Comparator<ScQtSchedule> comparator = new Comparator<ScQtSchedule>() {
			@Override
			public int compare(ScQtSchedule o1, ScQtSchedule o2) {
				return new CompareToBuilder().append(o1.getScheduleNo(),
						o2.getScheduleNo()).toComparison();
			}
		};
		Collections.sort(scQtScheduleList, comparator);
	}
}

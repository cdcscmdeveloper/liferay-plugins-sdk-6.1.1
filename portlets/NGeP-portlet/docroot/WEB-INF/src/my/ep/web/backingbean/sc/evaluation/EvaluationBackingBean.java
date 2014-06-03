package my.ep.web.backingbean.sc.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.evaluation.EvaluationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "evaluationBackingBean")
@RequestScoped
public class EvaluationBackingBean extends
		SourcingBaseBackingBean<EvaluationManagedBean> {
	private boolean skip;
	private int currentTab;
	private int outputTab;

	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	public static final String NAV_TO_STAGE2 = "/xhtml/ss/evaluation/peringkat2.xhtml";

	public EvaluationBackingBean() throws ParseException {

	}

	@PostConstruct
	public void init() throws IOException {
		try {
			if (this.getManagedBean().getScQtMeetings() == null) {
				retriveDetails();
			}

		} finally {

		}
	}

	public void summaryChange(AjaxBehaviorEvent event) {
		String view = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setSummaryViewBy(view);
	}

	public String retriveDetails() {
		IQTMeetingSO meetingSO;

		try {
			this.getManagedBean().setPrepareActiveIndex(0);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommitteType("SC");
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getMeetingDtls() != null
						&& !resDTO.getMeetingDtls().isEmpty()) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}

				ScQtCommittee committee = resDTO.getQtCommittee();
				if (null == committee) {
					committee = new ScQtCommittee();
				}

				this.getManagedBean().setScQtCommittee(committee);

			}

		} finally {
			meetingSO = null;
		}
		return "evaluationstage1view.xhtml";
	}

	public String retriveOpeningCommitteeDtls() {
		IQTMeetingSO meetingSO = null;

		this.getManagedBean().setPrepareActiveIndex(1);
		try {

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getCommitteeMemberList() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommitteType("SC");
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getCommitteMembers() != null
						&& !resDTO.getCommitteMembers().isEmpty()) {
					this.getManagedBean().setCommitteeMemberList(
							resDTO.getCommitteMembers());

				}
			}
			ScQtCommittee committee = resDTO.getQtCommittee();
			if (null == committee) {
				committee = new ScQtCommittee();
			}

			List<ScQtCommitteRemarks> qtRemarkList = committee
					.getQtCommitteRemarks();
			if (qtRemarkList == null) {
				qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
			}

			System.out.println("========qtRemarks size===="
					+ qtRemarkList.size());

			/*
			 * if ( null!=qtRemarkList && !qtRemarkList.isEmpty()) { for
			 * (ScQtCommitteRemarks remark : qtRemarkList) { ScQtCommitteRemarks
			 * qtRemark = (ScQtCommitteRemarks) remark;
			 * //qtRemarkList.add(qtRemark); }
			 * this.getManagedBean().setQtCommitteeRemarks(qtRemarkList); }
			 */
			if (this.getManagedBean().getQtCommitteeRemarks() == null) {

				List<ScQtCommitteRemarks> remarkList = new ArrayList<ScQtCommitteRemarks>();
				for (ScRemark remark : qtRemarkList) {
					ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
					remarkList.add(qtRemark);

				}
				this.getManagedBean().setQtCommitteeRemarks(remarkList);

				// initializeRemarks();
			}

		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	public void initializeRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScQtCommitteRemarks remark = null;
		if (this.getManagedBean().getQtCommitteeRemarks() == null) {
			this.getManagedBean().setQtCommitteeRemarks(
					new ArrayList<ScQtCommitteRemarks>());
		}

		remark = new ScQtCommitteRemarks();
		List<ScQtRemark> qtRemarkList = new ArrayList<ScQtRemark>();
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQtCommittee(this.getManagedBean().getScQtCommittee());
		PmStatus status = new PmStatus();
		status.setStatusId(1L);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());

		}
		// qtRemarkList.add(remark);
		this.getManagedBean().getQtCommitteeRemarks().add(remark);
	}

	public String retriveTurutHadirDtls() {
		this.getManagedBean().setPrepareActiveIndex(2);
		try {

			if (this.getManagedBean().getScQtCommittee()
					.getMeetingAttendances() != null
					&& !this.getManagedBean().getScQtCommittee()
							.getMeetingAttendances().isEmpty()) {
				this.getManagedBean().setMeetingAttendanceList(
						this.getManagedBean().getScQtCommittee()
								.getMeetingAttendances());
			}
		} finally {

		}
		return "evaluationstage1view.xhtml";
	}

	public String technicalComplianceAction(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(checklist.getQtChecklistId());
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
		} finally {

		}
		return "technicalComplianceStage.xhtml";
	}

	public String financialComplianceAction(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			reqDTO.setCheckListId(checklist.getQtChecklistId());
			EvaluationResDTO resDTO = evaluationSo
					.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList().isEmpty()
					&& resDTO.getComplianceResultBean().getResultList() != null) {

				this.getManagedBean().setComplianceFinancialProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceFinancialProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}

		} finally {

		}
		return "financialComplianceStage.xhtml";
	}

	public String sampleCompliance(ScQtSampleLable label) {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setScQtSampleLbl(label);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			reqDTO.setLableId(label.getSampleLableId());
			EvaluationResDTO resDTO = evaluationSo
					.retrieveSampleItemDtlsByLable(reqDTO);
			if (resDTO.getItemResultBean().getResultList().isEmpty()
					&& resDTO.getItemResultBean().getResultList() != null) {
				this.getManagedBean().setComplianceSampleItemList(
						new ArrayList<ScQtSampleItem>());

			} else {

				this.getManagedBean().setComplianceSampleItemList(
						resDTO.getItemResultBean().getResultList());
			}

		} finally {

		}
		return "sampleComplianceStage.xhtml";
	}

	public void saveFinacialCompliance() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> finacialCompliance = this
					.getManagedBean().getComplianceFinancialProposalCheckList();
			if (finacialCompliance != null && !finacialCompliance.isEmpty()) {
				reqDTO.setComplianceList(finacialCompliance);
			}

			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.", ""));

		} finally {

		}
	}

	public void saveComplianceSample() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtSampleItem> sampleCompliance = this.getManagedBean()
					.getComplianceSampleItemList();
			if (sampleCompliance != null && !sampleCompliance.isEmpty()) {
				reqDTO.setSampleComplianceList(sampleCompliance);
			}

			evaluationSo.saveOrUpdateSampleComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.", ""));

		} finally {
			evaluationSo = null;
		}
	}

	public void saveTechnicalCompliance() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> technicalCompliance = this
					.getManagedBean().getComplianceTechnicalProposalCheckList();
			if (technicalCompliance != null && !technicalCompliance.isEmpty()) {
				reqDTO.setComplianceList(technicalCompliance);
			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.", ""));

		} finally {

		}
	}

	public String evaluationBackaction() {

		return "evaluationstage1view.xhtml";

	}

	public String retriveComplianceTechnical() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setComplianceActiveIndex(0);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			this.getManagedBean().setComplianceTechnicalCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {

		}
		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceSample() {
		IQTEvaluationSO evaluationSo = null;

		this.getManagedBean().setComplianceActiveIndex(1);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(146L);
			EvaluationResDTO resDTO = evaluationSo
					.retrieveSampleLableDtls(reqDTO);
			if (resDTO.getLableResultBean().getResultList() != null
					&& resDTO.getLableResultBean().getResultList().isEmpty()) {
				if (this.getManagedBean().getScqt() == null) {
					getManagedBean().setScQt(new ScQt());
				}
				this.getManagedBean().setComplianceSampleLblsupplier(
						new ArrayList<ScQtSampleLable>());
			}

			else {

				Date d1 = new Date();
				DateUtils dateUtil = new DateUtils();
				for (ScQtSampleLable label : resDTO.getLableResultBean()
						.getResultList()) {
					long days = dateUtil.getDaysDifference(d1, label
							.getScQtProposal().getScQtSupplier().getScQt()
							.getSampleClosingDate());
					getManagedBean()
							.setScQt(
									label.getScQtProposal().getScQtSupplier()
											.getScQt());
					int day = (int) days;

					this.getManagedBean().setCountDays(day);
				}

			}
			this.getManagedBean().setComplianceSampleLblsupplier(
					resDTO.getLableResultBean().getResultList());
		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceFinancial() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;

		this.getManagedBean().setComplianceActiveIndex(2);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			this.getManagedBean().setCompliancefinancialCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {
			evaluationSO = null;
		}

		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceSummary() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setComplianceActiveIndex(3);
		try {
			System.out.println("======enter compliance summary method===");
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// reqDTO.setQtId(791L);
			// evaluationSO.prepareComplianceSummaryResults(reqDTO);
			EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
			summaryReqDTO.setQtId(791L);
			summaryReqDTO.setEvalStage(1);
			resDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
			if (null != resDTO.getQulifiedSuppliers()
					&& resDTO.getQulifiedSuppliers().getResultList().isEmpty()) {
				this.getManagedBean().setComplianceSummaryQualSuppliers(
						new ArrayList<ScQtEvaluationResult>());
			} else {

				this.getManagedBean().setComplianceSummaryQualSuppliers(
						resDTO.getQulifiedSuppliers().getResultList());
				this.getManagedBean().setComplianceQualifiedSuppliers(
						resDTO.getQulifiedSuppliers().getTotalRecord());
			}
			if (null != resDTO.getDisQulifiedSuppliers()
					&& resDTO.getDisQulifiedSuppliers().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceSummaryDisQualSuppliers(
						new ArrayList<ScQtEvaluationResult>());
			} else {

				this.getManagedBean().setComplianceSummaryDisQualSuppliers(
						resDTO.getDisQulifiedSuppliers().getResultList());
				this.getManagedBean().setComplianceDisQualifiedSuppliers(
						resDTO.getDisQulifiedSuppliers().getTotalRecord());
			}

		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	public void retriveTechnicalEvaluationStage() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setEvaluatioStageActiveIndex(0);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			this.getManagedBean().setEvaluationStgTechnicalCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {
			evaluationSO = null;

		}
		// return "evaluationstage1view.xhtml";

	}

	public void retriveFinancialEvaluationStage() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setEvaluatioStageActiveIndex(1);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			this.getManagedBean().setEvaluationStgfinancialCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {
			evaluationSO = null;

		}
	}

	public void retriveSummaryEvaluationStage() {
		try {
			this.getManagedBean().setEvaluatioStageActiveIndex(2);

		} finally {

		}
	}

	public String viewTechnicalEvaluation(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			System.out.println("====technical view checklistId===="
					+ this.getManagedBean().getScQtChecklist()
							.getQtChecklistId());
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			if ("UP".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {

				return "technicalEvaluationStgView.xhtml";
			}
			if ("TS".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "technicalSpecEvaluation.xhtml";
				// return "productServiceTechScoreSpec.xhtml";
			} else {
				return "standardEvaluationTechList.xhtml";
				// return "techGovCompanyExperience.xhtml";
				// return "techPrivateCompanyExperience.xhtml";
				// return "senariTechnical.xhtml";
				// return "bilanganPerjaka.xhtml";
			}
		} finally {

		}

	}

	public String evaluateTechnicalEvaluation(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			System.out.println("====technical evaluation checklistId===="
					+ this.getManagedBean().getScQtChecklist()
							.getQtChecklistId());
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			if ("UP".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "technicalEvaluationEvaluateDocument.xhtml";
			}
			if ("TS".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "technicalSpecEvaluation.xhtml";
				// return "productServiceTechScoreSpec.xhtml";

			} else {
				return "standardEvaluationTechList.xhtml";
				// return "techGovCompanyExperience.xhtml";
				// return "techPrivateCompanyExperience.xhtml";
				// return "senariTechnical.xhtml";
				// return "bilanganPerjaka.xhtml";
			}
		} finally {

		}

	}

	public String viewfinacialEvaluation(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			if ("UP".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "technicalEvaluationStgView.xhtml";
			}
			if ("TS".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "evaluationFinancialSpec.xhtml";
			} else {
				return "standardEvaluationFinList.xhtml";
				// return "standardEvaluationFinList.xhtml";
				// return "finGovCompanyExperience.xhtml";
				// return "finPrivateCompanyExperience.xhtml";
				// return "bankStatement.xhtml";
				// return "cashOnHand.xhtml";
				// return "creditFacility.xhtml";
				// return "assetsLiabilities.xhtml";
				// return "paidUpCapital.xhtml";
				// return "supplierInfo.xhtml";
			}
		} finally {

		}
	}

	public String evaluatefinancialEvaluation(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {

				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			// this.getManagedBean().setQtProposalChecklist(checklist);
			if ("UP".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "financialEvaluationEvaluationDocument.xhtml";
			}
			if ("TS".equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
					.getDefinationType().getParameterCode())) {
				return "evaluationFinancialSpec.xhtml";
			} else {
				return "standardEvaluationFinList.xhtml";
				// return "finGovCompanyExperience.xhtml";
				// return "finPrivateCompanyExperience.xhtml";
				// return "bankStatement.xhtml";
				// return "cashOnHand.xhtml";
				// return "creditFacility.xhtml";
				// return "assetsLiabilities.xhtml";
				// return "paidUpCapital.xhtml";
				// return "supplierInfo.xhtml";
			}
		} finally {

		}

	}

	public String othersTechnicalEvaluate(
			ScQtProposalChecklist proposalCheckList) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if ("TS".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);
				if (resDTO != null) {
					if (null == this.getManagedBean()
							.getEvaluationComplianceList()) {
						this.getManagedBean().setEvaluationComplianceList(
								new ArrayList<ScQtCheckList>());
					}
					if (null == this.getManagedBean().getSpecItemList()) {
						this.getManagedBean().setSpecItemList(
								new ArrayList<TplSpecQuestion>());
					}

					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
					this.getManagedBean()
							.setSpecItemList(resDTO.getSpecItems());
				}
				
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSupplier(proposalCheckList.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
				templateSpecBean.setSpecMaster(proposalCheckList.getScQtCheckList().getScQtDoc().getTplSpecMaster());
				specBB.retrieveTemplateSpecMaster(templateSpecBean);

				return "productServiceTechScoreSpec.xhtml";
			}

			if ("SP".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveSampleDetails(reqDTO);
				if (null != resDTO.getItemResultBean().getResultList()
						&& !resDTO.getItemResultBean().getResultList()
								.isEmpty()) {
					this.getManagedBean().setSampleItemList(
							resDTO.getItemResultBean().getResultList());
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				return "techSampleEvaluation.xhtml";
			} else {
				if ("TG".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey())) {
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO.retrieveCompanyExpDetails(reqDTO);
					List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
					if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 10) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfContracts.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												noOfContracts);

							} else if (checklistScore.getCriteriaType() == 11) {

								this.getManagedBean().setCompanyScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfYears.add(checklistScore);
								this.getManagedBean().setTechnicalGovNoOfYears(
										noOfYears);

							}

						}
					}
					if (!resDTO.getComplianceChecklistResultBean()
							.getResultList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplianceChecklistResultBean()
										.getResultList());
					}

					if (null != resDTO.getLoaResultBean().getResultList()
							&& !resDTO.getLoaResultBean().getResultList()
									.isEmpty()) {
						this.getManagedBean().setLoaGovCompanyExpList(
								resDTO.getLoaResultBean().getResultList());
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechGovProposalScore(
								resDTO.getProposalScores());
					}

					/*
					 * this.getManagedBean().getScQtChecklist().
					 * setScQtCheckListValue(resDTO .getScQtCheckListValue());
					 */
					return "techGovCompanyExperience.xhtml";
				} else if ("TN".equalsIgnoreCase(proposalCheckList
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);
					reqDTO.setDocDefinationKey(proposalCheckList
							.getScQtCheckList().getPmDocument()
							.getDocDefinitionKey());
					resDTO = evaluationSO.retrieveNonCompanyExpDetails(reqDTO);
					List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
					if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 15) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfContracts.add(checklistScore);
								this.getManagedBean()
										.setTechnicalPrivateNoOfContracts(
												noOfContracts);

							} else if (checklistScore.getCriteriaType() == 16) {

								this.getManagedBean().setCompanyScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfYears.add(checklistScore);
								this.getManagedBean()
										.setTechnicalPrivateNoOfYears(noOfYears);

							}

						}
					}

					if (!resDTO.getComplianceChecklistResultBean()
							.getResultList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplianceChecklistResultBean()
										.getResultList());
					}

					if (!resDTO.getPrpExperience().isEmpty()) {

						this.getManagedBean().setPrpCompanyExperienceList(
								resDTO.getPrpExperience());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechGovProposalScore(
								resDTO.getProposalScores());
					}
					/*
					 * this.getManagedBean().getScQtChecklist().
					 * setScQtCheckListValue(resDTO .getScQtCheckListValue());
					 */
					return "techPrivateCompanyExperience.xhtml";
				}

				else if ("TL".equalsIgnoreCase(proposalCheckList
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveTechinicalStaffDtls(reqDTO);

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 20) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								technicalStaff.add(checklistScore);
								this.getManagedBean().setTechnicalStaffScore(
										technicalStaff);

							}

						}
					}
					if (!resDTO.getComplianceChecklistResultBean()
							.getResultList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplianceChecklistResultBean()
										.getResultList());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechGovProposalScore(
								resDTO.getProposalScores());
					}

					return "senariTechnical.xhtml";
				}

				else if ("NE".equalsIgnoreCase(proposalCheckList
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveEmployeeDtls(reqDTO);

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 21) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								technicalStaff.add(checklistScore);
								this.getManagedBean().setTotalNumEmployees(
										technicalStaff);

							}

						}
					}
					if (!resDTO.getComplianceChecklistResultBean()
							.getResultList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplianceChecklistResultBean()
										.getResultList());
					}
					if (null != resDTO.getScQtPRPEmployee()) {
						this.getManagedBean().setNoOfEmployeeList(
								resDTO.getScQtPRPEmployee());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechGovProposalScore(
								resDTO.getProposalScores());
					}
					return "bilanganPerjaka.xhtml";
				}

			}

		} finally {

		}

		return null;
	}

	public String othersFinancialEvaluate(
			ScQtProposalChecklist proposalCheckList) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			
			
			if ("TS".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);
				if (resDTO != null) {
					if (null == this.getManagedBean()
							.getEvaluationComplianceList()) {
						this.getManagedBean().setEvaluationComplianceList(
								new ArrayList<ScQtCheckList>());
					}
					if (null == this.getManagedBean().getSpecItemList()) {
						this.getManagedBean().setSpecItemList(
								new ArrayList<TplSpecQuestion>());
					}

					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
					this.getManagedBean()
							.setSpecItemList(resDTO.getSpecItems());
				}
				
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSupplier(proposalCheckList.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
				templateSpecBean.setSpecMaster(proposalCheckList.getScQtCheckList().getScQtDoc().getTplSpecMaster());
				specBB.retrieveTemplateSpecMaster(templateSpecBean);

				return "productStandardTarifTierFinSpec.xhtml";
			}
			
			else{

			if ("FG".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveCompanyExpDetails(reqDTO);
				List<ScQtCheckListScore> finGovContract = new ArrayList<ScQtCheckListScore>();

				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 12) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							finGovContract.add(checklistScore);
							this.getManagedBean().setFinGovContract(
									finGovContract);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (null != resDTO.getLoaResultBean().getResultList()
						&& !resDTO.getLoaResultBean().getResultList().isEmpty()) {
					this.getManagedBean().setLoaGovCompanyExpList(
							resDTO.getLoaResultBean().getResultList());
				}
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				return "finGovCompanyExperience.xhtml";
			} else if ("FN".equalsIgnoreCase(proposalCheckList
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
						.getQtChecklistId());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				reqDTO.setDocDefinationKey(proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey());
				resDTO = evaluationSO.retrieveNonCompanyExpDetails(reqDTO);
				List<ScQtCheckListScore> finGovContract = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 17) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							finGovContract.add(checklistScore);
							this.getManagedBean().setFinPrivateContract(
									finGovContract);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (!resDTO.getPrpExperience().isEmpty()) {

					this.getManagedBean().setPrpCompanyExperienceList(
							resDTO.getPrpExperience());
				}
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}
				return "finPrivateCompanyExperience.xhtml";
			}

			else if ("BS".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveBankStmtDtls(reqDTO);
				List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 50) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							bankStatement.add(checklistScore);
							this.getManagedBean().setBankStatementScore(
									bankStatement);
						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				if (!resDTO.getBankStmtResultBean().isEmpty()) {
					if (this.getManagedBean().getBankStmtList() == null) {
						for (ScQtPRPBankStmt bankStmt : resDTO
								.getBankStmtResultBean()) {
							String monthvalue = "";
							String amount = "";
							Date date = bankStmt.getStmtDate();
							SimpleDateFormat dfFormat = new SimpleDateFormat(
									"dd/MM/yyyy");

							String convert = dfFormat.format(date);

							String month = convert.substring(3, 5);

							if ("01".equalsIgnoreCase(month)) {
								monthvalue = "January".concat(convert
										.substring(6, convert.length()));
							}

							if ("02".equalsIgnoreCase(month)) {
								monthvalue = "February".concat(convert
										.substring(6, convert.length()));
							}
							if ("03".equalsIgnoreCase(month)) {
								monthvalue = "March".concat(convert.substring(
										6, convert.length()));
							}

							if ("04".equalsIgnoreCase(month)) {
								monthvalue = "April".concat(convert.substring(
										6, convert.length()));
							}

							if ("05".equalsIgnoreCase(month)) {
								monthvalue = "May".concat(convert.substring(6,
										convert.length()));
							}
							if ("06".equalsIgnoreCase(month)) {
								monthvalue = "June".concat(convert.substring(6,
										convert.length()));
							}

							if ("07".equalsIgnoreCase(month)) {
								monthvalue = "July".concat(convert.substring(6,
										convert.length()));
							}

							if ("08".equalsIgnoreCase(month)) {
								monthvalue = "August".concat(convert.substring(
										6, convert.length()));
							}
							if ("09".equalsIgnoreCase(month)) {
								monthvalue = "September".concat(convert
										.substring(6, convert.length()));
							}

							if ("10".equalsIgnoreCase(month)) {
								monthvalue = "October".concat(convert
										.substring(6, convert.length()));
							}

							if ("11".equalsIgnoreCase(month)) {
								monthvalue = "November".concat(convert
										.substring(6, convert.length()));
							}
							if ("12".equalsIgnoreCase(month)) {
								monthvalue = "December".concat(convert
										.substring(6, convert.length()));
							}
							List<ScQtPRPBankStmt> bankList = this
									.getManagedBean().getBankStmtList();
							if (bankList == null) {
								bankList = new ArrayList<ScQtPRPBankStmt>();
							}
							bankStmt.setMonth(monthvalue);
							bankList.add(bankStmt);
							this.getManagedBean().setBankStmtList(bankList);

						}
					}
				}

				return "bankStatement.xhtml";
			} else if ("CF".equalsIgnoreCase(proposalCheckList
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveCreditFacilitiesDtls(reqDTO);
				List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 51) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							creditFacility.add(checklistScore);
							this.getManagedBean().setCreditFacilityScore(
									creditFacility);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (!resDTO.getCreditFacilityResultBean().isEmpty()) {
					this.getManagedBean().setCreditFacilityList(
							resDTO.getCreditFacilityResultBean());
				}
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				return "creditFacility.xhtml";
			} else if ("CH".equalsIgnoreCase(proposalCheckList
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveCashOnHandDtls(reqDTO);
				List<ScQtCheckListScore> cashOhHand = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 52) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							cashOhHand.add(checklistScore);
							this.getManagedBean()
									.setCashOnHandScore(cashOhHand);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (!resDTO.getCashOnHandResultBean().isEmpty()) {
					this.getManagedBean().setCashOnHandList(
							resDTO.getCashOnHandResultBean());
				}
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				return "cashOnHand.xhtml";
			}

			else if ("AL".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveAssetLiabilityDtls(reqDTO);
				List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 60) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							assetLiability.add(checklistScore);
							this.getManagedBean().setAssetLiabilityScore(
									assetLiability);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				return "assetsLiabilities.xhtml";
			} else if ("PC".equalsIgnoreCase(proposalCheckList
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				// resDTO = evaluationSO.ret
				List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();
				if (!resDTO.getScoreResultBean().getResultList().isEmpty()) {
					for (ScQtCheckListScore checklistScore : resDTO
							.getScoreResultBean().getResultList()) {
						if (checklistScore.getCriteriaType() == 70) {

							this.getManagedBean().setChecklistScoreType(
									String.valueOf(checklistScore
											.getScoreType().intValue()));
							paidUpCapital.add(checklistScore);
							this.getManagedBean().setPaidUpCapitalScore(
									paidUpCapital);

						}

					}
				}

				if (!resDTO.getComplianceChecklistResultBean().getResultList()
						.isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplianceChecklistResultBean()
									.getResultList());
				}

				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechGovProposalScore(
							resDTO.getProposalScores());
				}

				return "paidUpCapital.xhtml";
			}

			else if ("SD".equalsIgnoreCase(proposalCheckList.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {
				return "supplierInfo.xhtml";
			}
			}
		} finally {

		}

		return null;
	}

	public void reportDownLoadZipFile() throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(new File("\\upload\\clone.jpg"));
		files.add(new File("\\upload\\scan.docx"));
		files.add(new File("\\upload\\console.txt"));

		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=Ngep.zip");

		// ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));

		for (File file : files) {

			System.out.println("Adding file " + file.getName());
			zos.putNextEntry(new ZipEntry(file.getName()));

			// Get the file
			FileInputStream fis = null;
			try {

				fis = new FileInputStream(file);

			} catch (FileNotFoundException fnfe) {
				// If the file does not exists, write an error entry instead of
				// file
				// contents
				zos.write(("ERROR: Could not find file " + file.getName())
						.getBytes());
				zos.closeEntry();
				System.out.println("Could not find file "
						+ file.getAbsolutePath());
				continue;
			}

			BufferedInputStream fif = new BufferedInputStream(fis);

			// Write the contents of the file
			int data = 0;
			while ((data = fif.read()) != -1) {

				zos.write(data);
			}
			fif.close();
			zos.closeEntry();
			System.out.println("Finished adding file " + file.getName());
		}
		zos.close();
		response.flushBuffer();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void reportDownLoad() throws IOException {

		File file = new File("\\upload\\console.txt");

		InputStream input = new FileInputStream(file);
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition",
				"attachment; filename=\"" + file.getName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();

		try {
			URLConnection uc = new URL("\\upload\\console.docx")
					.openConnection();

			input = new BufferedInputStream(uc.getInputStream());
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			e.printStackTrace();
			input = new FileInputStream(file.getPath());
		}
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}

		input.close();
		out.flush();
		out.close();

		FacesContext.getCurrentInstance().responseComplete();
	}

	public void setTabValue(int currentTab, int outputTab) {
		try {

			this.currentTab = currentTab;
			this.outputTab = outputTab;
			if (outputTab == 1) {
				retriveComplianceTechnical();
				// retrives1Details();
			}
			if (outputTab == 2) {
				retriveTechnicalEvaluationStage();
				// getManagedBean().getTemplateSpecBean().setSelectedSpecQuestion(new
				// TplSpecQuestion());
				// retrives2Details();
			}
			if (outputTab == 3) {
				// this.getManagedBean().setScqt(new ScQt());
				// retriveStageOneSummary();

			}
			if (outputTab == 4) {
				// this.getManagedBean().setScqt(new ScQt());
				// retrivesummary();

			}
			if (outputTab == 5) {

			}
			if (outputTab == 6) {

			}
			if (outputTab == 0) {
				retriveDetails();
			}

			if (getManagedBean().getTempTab() < outputTab) {
				getManagedBean().setTempTab(outputTab);
			}
			getManagedBean().setTabActiveIndex(outputTab);
		} finally {

		}
	}

	public String navigateAction(String tabIndex) {

		if (!StringUtils.isEmpty(tabIndex)) {
			getManagedBean().setTabActiveIndex(new Integer(tabIndex));
		}
		return null;
	}

	public void updateSmAppl() throws Exception {
		try {
			// retrives2Details();
			getManagedBean().setTabActiveIndex(outputTab);

		} finally {

		}

	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public void addMeetingAction() {

		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();

			if (null == meetingList) {
				meetingList = new ArrayList<ScQtMeeting>();
			}

			ScQtMeeting meeting = new ScQtMeeting();
			// meeting.setPmStatus(new PmStatus());
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			meeting.setMeetingStatus(statusres.getPmStatus());
			meetingList.add(meeting);

			this.getManagedBean().setScQtMeetings(meetingList);

		} finally {

		}
	}

	public void addTurutDetails() {

		try {
			List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean()
					.getMeetingAttendanceList();

			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			}

			ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);
			meetingAttendanceList.add(meeting);

			this.getManagedBean().setMeetingAttendanceList(
					meetingAttendanceList);

		} finally {

		}
	}

	public void saveMeetingDtls() {
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			if (this.getManagedBean().getScQtMeetings() != null
					&& !this.getManagedBean().getScQtMeetings().isEmpty()) {
				qtCommittee.setScQtMeetings(this.getManagedBean()
						.getScQtMeetings());
			}

			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveOpenCommitteeDtls() {
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();

			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			qtCommittee.setScQtMeetings(null);
			if (this.getManagedBean().getCommitteeMemberList() != null
					&& !this.getManagedBean().getCommitteeMemberList()
							.isEmpty()) {
				qtCommittee.setCommitteeActivities(this.getManagedBean()
						.getCommitteeMemberList());
			}

			List<ScQtCommitteRemarks> remarksList = this.getManagedBean()
					.getQtCommitteeRemarks();
			List<ScQtCommitteRemarks> committeeRemarks = null;
			if (isNotNullOREmpty(remarksList)) {
				committeeRemarks = new ArrayList<ScQtCommitteRemarks>();
				for (ScQtCommitteRemarks remarks : remarksList) {
					if (remarks.getRemark() != null
							&& "" != remarks.getRemark()) {
						committeeRemarks.add(remarks);
					}

				}

			}
			qtCommittee.setQtCommitteRemarks(committeeRemarks);
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveMeetingAttendenceDtls() {
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			qtCommittee.setScQtMeetings(null);
			qtCommittee.setCommitteeActivities(null);
			qtCommittee.setQtCommitteRemarks(null);
			if (getManagedBean().getMeetingAttendanceList() != null
					&& !getManagedBean().getMeetingAttendanceList().isEmpty()) {
				qtCommittee.setMeetingAttendances(getManagedBean()
						.getMeetingAttendanceList());
			}

			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void deleteMeetingDetails() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			List<ScQtMeeting> selectMeeting = getManagedBean()
					.getMeetingMultiSelect();
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeeting meeting : selectMeeting)
					meetingList.remove(meeting);
			}
		} finally {

		}
	}

	public void deleteTurutHadirDetails() {
		try {
			List<ScQtMeetingAttendance> meetingList = getManagedBean()
					.getMeetingAttendanceList();
			List<ScQtMeetingAttendance> selectMeeting = getManagedBean()
					.getMeetingAttendanceSelectList();
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeetingAttendance meeting : selectMeeting)
					meetingList.remove(meeting);
			}
		} finally {

		}
	}

	public void handleFileUpload(FileUploadEvent event) {

		this.getManagedBean().setFileName(event.getFile().getFileName());

	}

	public void removeFile() {
		this.getManagedBean().setFileName(new String());
	}

	public void reportUpload() {
		System.out.println("yeye7348445459");

	}

	/**
	 * @return the skip
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * @param skip
	 *            the skip to set
	 */
	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * @return the outputTab
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * @param outputTab
	 *            the outputTab to set
	 */
	public void setOutputTab(int outputTab) {
		this.outputTab = outputTab;
	}

}

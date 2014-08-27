package my.ep.web.backingbean.sc.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.evaluation.EvaluationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

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

		} finally {

		}
	}

	public void summaryChange(AjaxBehaviorEvent event) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			String view = (String) event.getComponent().getAttributes()
					.get("value");
			this.getManagedBean().setSummaryViewBy(view);
			String award = null;
			EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
			List<String> resultStg = new ArrayList<String>();
			if (QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())
					&& !ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				award = QTConstants.AWARD_TYPE_PACKAGE;
				resultStg.add(this.getManagedBean().getSummaryViewBy());
				summaryReqDTO.setResultStages(resultStg);
				this.getManagedBean().setSummaryZoneBean(null);
			} else if (QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())
					&& ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				summaryReqDTO.setResultStages(resultStg);
				this.getManagedBean().setSummaryZoneBean(null);

			} else if (QTConstants.FINANCIAL_EVALUATION_BY_ITEM
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())
					&& !ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {

				resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg);
				award = QTConstants.AWARD_TYPE_ITEM;
				this.getManagedBean().setSummaryZoneBean(null);
			} else if (QTConstants.FINANCIAL_EVALUATION_BY_ITEM
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())
					&& ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg);
				award = QTConstants.AWARD_TYPE_ITEM;
				this.getManagedBean().setSummaryZoneBean(null);
			} else if (QTConstants.PREFERENCE_MARGIN.equalsIgnoreCase(this
					.getManagedBean().getSummaryViewBy())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				summaryReqDTO.setResultStages(resultStg);
				this.getManagedBean().setSummaryZoneBean(null);
			}

			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			summaryReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

			summaryReqDTO.setResultStages(resultStg);
			summaryReqDTO.setAwardBy(award);

			evalResDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
			if (null != evalResDTO.getZones()
					&& !evalResDTO.getZones().isEmpty()) {
				if (this.getManagedBean().getSummaryZoneBean() == null) {
					this.getManagedBean().setSummaryZoneBean(
							new ArrayList<SummaryZoneBaen>());
					for (TplSpecZoneDtl tplSpecZoneDtl : evalResDTO.getZones()) {

						summaryReqDTO.setSpeZoneId(tplSpecZoneDtl
								.getSpecZoneId());
						EvaluationResDTO evalResDTO1 = evaluationSO
								.retrieveSummaryDtlsByZoneNAwardNItem(summaryReqDTO);
						SummaryZoneBaen bean = null;
						if (QTConstants.FINANCIAL_EVALUATION_BY_ITEM
								.equalsIgnoreCase(this.getManagedBean()
										.getSummaryViewBy())) {
							evalResDTO = evaluationSO
									.retrieveSummaryDetails(summaryReqDTO);

							if (null != evalResDTO.getRequestItemResults()
									&& null != evalResDTO
											.getRequestItemResults()
											.getResultList()
									&& !evalResDTO.getRequestItemResults()
											.getResultList().isEmpty()) {
								this.getManagedBean().setSummaryItemList(
										evalResDTO.getRequestItemResults()
												.getResultList());
								bean = new SummaryZoneBaen(tplSpecZoneDtl,
										evalResDTO.getRequestItemResults()
												.getResultList());

							}
						} else {
							bean = new SummaryZoneBaen(tplSpecZoneDtl, null,
									null);
							if (null != evalResDTO1.getQulifiedSuppliers()
									&& null != evalResDTO1
											.getQulifiedSuppliers()
											.getResultList()
									&& !evalResDTO1.getQulifiedSuppliers()
											.getResultList().isEmpty()) {
								this.getManagedBean()
										.setEvaTotQualifiedSuppliers(
												evalResDTO1
														.getQulifiedSuppliers()
														.getTotalRecord());

								bean = new SummaryZoneBaen(tplSpecZoneDtl,
										evalResDTO1.getQulifiedSuppliers()
												.getResultList(), null);

							}
							if (null != evalResDTO1.getDisQulifiedSuppliers()
									&& null != evalResDTO1
											.getDisQulifiedSuppliers()
											.getResultList()
									&& !evalResDTO1.getDisQulifiedSuppliers()
											.getResultList().isEmpty()) {

								bean = new SummaryZoneBaen(tplSpecZoneDtl,
										evalResDTO1.getQulifiedSuppliers()
												.getResultList(), evalResDTO1
												.getDisQulifiedSuppliers()
												.getResultList());
								this.getManagedBean()
										.setEvaTotDisQualifiedSuppliers(
												evalResDTO1
														.getDisQulifiedSuppliers()
														.getTotalRecord());

							}
						}

						this.getManagedBean().getSummaryZoneBean().add(bean);
					}
				}

			}

			else {

				if (null != evalResDTO.getRequestItemResults()
						&& null != evalResDTO.getRequestItemResults()
								.getResultList()
						&& !evalResDTO.getRequestItemResults().getResultList()
								.isEmpty()) {
					this.getManagedBean().setSummaryItemList(
							evalResDTO.getRequestItemResults().getResultList());
				}

				if (null != evalResDTO.getQulifiedSuppliers()
						&& null != evalResDTO.getQulifiedSuppliers()
								.getResultList()
						&& !evalResDTO.getQulifiedSuppliers().getResultList()
								.isEmpty()) {

					this.getManagedBean().setEvaluationSummaryQualSuppliers(
							evalResDTO.getQulifiedSuppliers().getResultList());
					this.getManagedBean().setEvaTotQualifiedSuppliers(
							evalResDTO.getQulifiedSuppliers().getTotalRecord());
				}

				if (null != evalResDTO.getDisQulifiedSuppliers()
						&& null != evalResDTO.getDisQulifiedSuppliers()
								.getResultList()
						&& !evalResDTO.getDisQulifiedSuppliers()
								.getResultList().isEmpty()) {
					this.getManagedBean().setEvaluationSummaryDisQualSuppliers(
							evalResDTO.getDisQulifiedSuppliers()
									.getResultList());
					this.getManagedBean().setEvaTotDisQualifiedSuppliers(
							evalResDTO.getDisQulifiedSuppliers()
									.getTotalRecord());
				}
			}

		} finally {

		}
	}

	public void summaryItemSelect(SelectEvent event) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			ScRequestItem requestItem = (ScRequestItem) event.getObject();

			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(791L);
			List<String> resultStg = new ArrayList<String>();
			resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
			reqDTO.setResultStages(resultStg);
			reqDTO.setAwardBy(QTConstants.AWARD_TYPE_ITEM);
			reqDTO.setRequestItemId(requestItem.getRequestNoteItemId());
			evalResDTO = evaluationSO
					.retrieveSummaryDtlsByZoneNAwardNItem(reqDTO);
			if (null != evalResDTO.getQulifiedSuppliers()
					&& null != evalResDTO.getQulifiedSuppliers()
							.getResultList()
					&& !evalResDTO.getQulifiedSuppliers().getResultList()
							.isEmpty()) {

				this.getManagedBean().setItemSummaryQualSuppliers(
						evalResDTO.getQulifiedSuppliers().getResultList());
				this.getManagedBean().setItemTotQualSuppliers(
						evalResDTO.getQulifiedSuppliers().getTotalRecord());
			}
			if (null != evalResDTO.getDisQulifiedSuppliers()
					&& null != evalResDTO.getDisQulifiedSuppliers()
							.getResultList()
					&& !evalResDTO.getDisQulifiedSuppliers().getResultList()
							.isEmpty()) {

				this.getManagedBean().setItemSummaryDisQualSuppliers(
						evalResDTO.getDisQulifiedSuppliers().getResultList());
				this.getManagedBean().setItemTotDisQualSuppliers(
						evalResDTO.getDisQulifiedSuppliers().getTotalRecord());
			}

		} finally {

		}
	}

	public String retriveMeetingDtls(Long qtId, String commType) {
		IQTMeetingSO meetingSO;
		IQTDetailSO qtDetailSo = null;

		try {
			this.getManagedBean().setCommitteeType(commType);
			this.getManagedBean().setPrepareActiveIndex(0);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			qtDetailSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			if (null == this.getManagedBean().getScQt()) {
				qtReqDTO.setQtId(qtId);
				qtResDTO = qtDetailSo.retriveQtById(qtReqDTO);
				this.getManagedBean().setScQt(qtResDTO.getQtDetail());
			}

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

				reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
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
			qtDetailSo = null;
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
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

				reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				ScQtCommittee committee = resDTO.getQtCommittee();
				if (null == committee) {
					committee = new ScQtCommittee();
				}

				if (null != resDTO.getQtCommittee()) {

					if (null != resDTO.getQtCommittee()
							.getCommitteeActivities()

							&& !resDTO.getQtCommittee()
									.getCommitteeActivities().isEmpty()) {

						this.getManagedBean().setCommitteeMemberList(
								resDTO.getQtCommittee()
										.getCommitteeActivities());

					}

				}

				List<ScQtCommitteRemarks> qtRemarkList = committee
						.getQtCommitteRemarks();
				if (qtRemarkList == null) {
					qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
				}

				if (this.getManagedBean().getQtCommitteeRemarks() == null) {

					List<ScQtCommitteRemarks> remarkList = new ArrayList<ScQtCommitteRemarks>();
					for (ScRemark remark : qtRemarkList) {
						ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
						remarkList.add(qtRemark);

					}
					this.getManagedBean().setQtCommitteeRemarks(remarkList);

					// initializeRemarks();
				}
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

			if (null != this.getManagedBean().getScQtCommittee()
					.getMeetingAttendances()
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
			reqDTO.setCompliance(null);
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
			reqDTO.setCompliance(null);
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
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									"Evaluation Compliance saved successfully.",
									"Evaluation Financial Compliance saved successfully."));

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
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									"Evaluation Compliance saved successfully.",
									"Evaluation Sample Compliance saved successfully."));

		} finally {
			evaluationSo = null;
		}
	}

	public void complianceStatus(int index,
			List<ScQtProposalChecklist> proposalCheckList) {
		ScQtProposalChecklist scqtProposalCheckList = proposalCheckList
				.get(index);
		this.getManagedBean().getComplianceTechnicalProposalCheckList()
				.set(index, scqtProposalCheckList);
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
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									"Evaluation Compliance saved successfully.",
									"Evaluation Technical Compliance saved successfully."));

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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			EvaluationResDTO resDTO = evaluationSo
					.retrieveSampleLableDtls(reqDTO);
			if (resDTO.getLableResultBean().getResultList() != null
					&& resDTO.getLableResultBean().getResultList().isEmpty()) {

				this.getManagedBean().setComplianceSampleLblsupplier(
						new ArrayList<ScQtSampleLable>());
			}

			else {

				Date d1 = new Date();
				DateUtils dateUtil = new DateUtils();

				for (ScQtSampleLable label : resDTO.getLableResultBean()
						.getResultList()) {
					long days = dateUtil.getDaysDifference(d1, this
							.getManagedBean().getScQt().getSampleClosingDate());

					/*
					 * this.getManagedBean() .setScQt(
					 * label.getScQtProposal().getScQtSupplier() .getScQt());
					 */
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

		try {
			if (this.getManagedBean().getScQt().isSampleReq()) {
				this.getManagedBean().setComplianceActiveIndex(2);
			} else {
				this.getManagedBean().setComplianceActiveIndex(1);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
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

		try {
			if (this.getManagedBean().getScQt().isSampleReq()) {
				this.getManagedBean().setComplianceActiveIndex(3);
			} else {
				this.getManagedBean().setComplianceActiveIndex(2);
			}

			int count = this.getManagedBean().getComplianceSummaryCount();
			count++;
			int count2 = count;
			this.getManagedBean().setComplianceSummaryCount(count2);
			if (this.getManagedBean().getComplianceSummaryCount() == 1) {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				List<String> evalstage = new ArrayList<String>();
				evalstage.add(QTConstants.COMPLIANCE_CHECK);
				reqDTO.setResultStages(evalstage);
				evaluationSO.prepareComplianceSummaryResults(reqDTO);
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());
				List<String> qualifiedstage = new ArrayList<String>();
				qualifiedstage.add(QTConstants.COMPLIANCE_CHECK);
				summaryReqDTO.setResultStages(evalstage);
				resDTO = evaluationSO
						.retrieveSummaryDtlsByZoneNAwardNItem(summaryReqDTO);
				if (null != resDTO.getQulifiedSuppliers()
						&& null != resDTO.getQulifiedSuppliers()
								.getResultList()
						&& !resDTO.getQulifiedSuppliers().getResultList()
								.isEmpty()) {

					this.getManagedBean().setComplianceSummaryQualSuppliers(
							resDTO.getQulifiedSuppliers().getResultList());
					this.getManagedBean().setComplianceQualifiedSuppliers(
							resDTO.getQulifiedSuppliers().getTotalRecord());
				}
				if (null != resDTO.getDisQulifiedSuppliers()
						&& null != resDTO.getDisQulifiedSuppliers()
								.getResultList()
						&& !resDTO.getDisQulifiedSuppliers().getResultList()
								.isEmpty()) {
					this.getManagedBean().setComplianceSummaryDisQualSuppliers(
							resDTO.getDisQulifiedSuppliers().getResultList());
					this.getManagedBean().setComplianceDisQualifiedSuppliers(
							resDTO.getDisQulifiedSuppliers().getTotalRecord());
				}
			}

		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	public void generateEvaluationStage() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;

		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
				reqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				evaluationSO.generateEvaluationSheet(reqDTO);
			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
				reqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				evaluationSO.generateEvaluationSheet(reqDTO);
			}

			else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

				reqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				resDTO = evaluationSO.generateEvaluationSheet(reqDTO);
				reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
				resDTO = evaluationSO.generateEvaluationSheet(reqDTO);

			}

			this.getManagedBean().setGenerateEvaluationButton(true);

		} finally {
			evaluationSO = null;

		}
	}

	public void retriveTechnicalEvaluationStage() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;

		try {

			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(0);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
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
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(1);
			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(0);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
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
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = null;
		try {
			int count = this.getManagedBean().getComplianceSummaryCount();
			count++;
			int count2 = count;
			this.getManagedBean().setComplianceSummaryCount(count2);
			if (this.getManagedBean().getComplianceSummaryCount() == 1) {

				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					this.getManagedBean().setEvaluatioStageActiveIndex(2);
				}

				else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					this.getManagedBean().setEvaluatioStageActiveIndex(1);
				}
				IQTDetailSO detailsSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				QuationTenderResDTO resDTO = detailsSo.retriveQTDetails(reqDTO);
				this.getManagedBean().setScQt(resDTO.getQtDetail());
				EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					evalReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					evalReqDTO
							.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
					evalReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					evalReqDTO.setRecomandationType(1);
					List<String> resultStg = new ArrayList<String>();
					resultStg.add(QTConstants.TECHNICAL_EVALUATION);
					evalReqDTO.setResultStages(resultStg);
					evaluationSo.prepareEvaluationResultsByStage(evalReqDTO);
					EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
					summaryReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					List<String> resultStg2 = new ArrayList<String>();
					resultStg2.add(QTConstants.TECHNICAL_EVALUATION);
					summaryReqDTO.setResultStages(resultStg2);
					evalResDTO = evaluationSo
							.retrieveSummaryDetails(summaryReqDTO);
					if (null != evalResDTO.getQulifiedSuppliers()
							&& null != evalResDTO.getQulifiedSuppliers()
									.getResultList()

							&& !evalResDTO.getQulifiedSuppliers()
									.getResultList().isEmpty()) {

						this.getManagedBean()
								.setEvaluationSummaryQualSuppliers(
										evalResDTO.getQulifiedSuppliers()
												.getResultList());
						this.getManagedBean().setEvaTotQualifiedSuppliers(
								evalResDTO.getQulifiedSuppliers()
										.getTotalRecord());
					}
					if (null != evalResDTO.getDisQulifiedSuppliers()
							&& null != evalResDTO.getDisQulifiedSuppliers()
									.getResultList()
							&& evalResDTO.getDisQulifiedSuppliers()
									.getResultList().isEmpty()) {
						this.getManagedBean()
								.setEvaluationSummaryDisQualSuppliers(
										evalResDTO.getDisQulifiedSuppliers()
												.getResultList());
						this.getManagedBean().setEvaTotDisQualifiedSuppliers(
								evalResDTO.getDisQulifiedSuppliers()
										.getTotalRecord());
					}
				}

				else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {

					evalReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					evalReqDTO
							.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
					evalReqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					evalReqDTO.setRecomandationType(2);
					List<String> resultStg = new ArrayList<String>();
					resultStg.add(QTConstants.FINANCIAL_EVALUATION);
					evalReqDTO.setResultStages(resultStg);
					evaluationSo.prepareEvaluationResultsByStage(evalReqDTO);
					EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
					summaryReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					summaryReqDTO
							.setEvalStage(QTConstants.FINANCIAL_EVALUATION);
					List<String> resultStg2 = new ArrayList<String>();
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
					summaryReqDTO.setResultStages(resultStg2);
					evaluationSo
							.prepareEvaluationResultsSummaryForTwoTier(summaryReqDTO);

				}

				else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
					summaryReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					summaryReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					List<String> resultStg2 = new ArrayList<String>();
					resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
					resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
					summaryReqDTO.setResultStages(resultStg2);

					evaluationSo
							.prepareEvaluationResultsSummaryForOneTier(summaryReqDTO);

				}
			}

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

			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {

				return "technicalEvaluationStgView.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
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
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setCompliance(true);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "technicalEvaluationEvaluateDocument.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "technicalSpecEvaluation.xhtml";
				// return "productServiceTechScoreSpec.xhtml";

			} else {
				return "standardEvaluationTechList.xhtml";

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
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "technicalEvaluationStgView.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {

				EvaluationReqDTO specDtls = new EvaluationReqDTO();
				specDtls.setQtId(this.getManagedBean().getScQt().getQtId());
				specDtls.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());
				EvaluationResDTO speczone = evaluationSo
						.retrieveFinSpecDetailsByCheckListId(specDtls);

				if (null != speczone.getZones()
						&& !speczone.getZones().isEmpty()) {
					if (null == this.getManagedBean().getFinSpecZoneBean()) {
						for (TplSpecZoneDtl tplSpecZoneDtl : speczone
								.getZones()) {

							EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
							this.getManagedBean().setSelectedZone(
									speczone.getZones().get(0));
							zoneReqDto.setSpeZoneId(tplSpecZoneDtl
									.getSpecZoneId());
							zoneReqDto.setCheckListId(this.getManagedBean()
									.getScQtChecklist().getQtChecklistId());
							EvaluationResDTO zoneResDTO = evaluationSo
									.retrievProposalCheckListByCheckListNZone(zoneReqDto);
							this.getManagedBean().setFinSpecZoneBean(
									new ArrayList<SummaryZoneBaen>());
							SummaryZoneBaen finSpecZone = new SummaryZoneBaen(
									tplSpecZoneDtl, null, null, 0);
							if (null != zoneResDTO.getComplianceResultBean()
									&& null != zoneResDTO
											.getComplianceResultBean()
											.getResultList()
									&& !zoneResDTO.getComplianceResultBean()
											.getResultList().isEmpty()) {

								finSpecZone = new SummaryZoneBaen(
										tplSpecZoneDtl, zoneResDTO
												.getComplianceResultBean()
												.getResultList(), null, 0);
							}

							if (null != speczone.getScoreOfferPrices()) {
								finSpecZone = new SummaryZoneBaen(
										tplSpecZoneDtl, zoneResDTO
												.getComplianceResultBean()
												.getResultList(),
										speczone.getScoreOfferPrices(), 0);
							}

							this.getManagedBean().getFinSpecZoneBean()
									.add(finSpecZone);

						}
					}
					return "evaluationFinancialSpec.xhtml";
				} else {
					// rertriveFinSpecDtlsByChecklistId() withoutzone
					// retriveProposalChecklistByzone() withzonespec
					if (null != speczone.getComplianceResultBean()
							&& null != speczone.getComplianceResultBean()
									.getResultList()
							&& !speczone.getComplianceResultBean()
									.getResultList().isEmpty()) {
						this.getManagedBean().setFinSpecSupplierList(
								speczone.getComplianceResultBean()
										.getResultList());
					}
					if (null != speczone.getScoreOfferPrices()) {
						this.getManagedBean().setOfferPricePackage(
								speczone.getScoreOfferPrices());
					}
					return "evaluationFinSpecWithOutzone.xhtml";
				}

			} else {
				return "standardEvaluationFinList.xhtml";

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
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}
			// this.getManagedBean().setQtProposalChecklist(checklist);
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "financialEvaluationEvaluationDocument.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				EvaluationReqDTO specDtls = new EvaluationReqDTO();
				specDtls.setQtId(this.getManagedBean().getScQt().getQtId());
				specDtls.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());
				EvaluationResDTO speczone = evaluationSo
						.retrieveFinSpecDetailsByCheckListId(specDtls);

				if (null != speczone.getZones()
						&& !speczone.getZones().isEmpty()) {
					if (null == this.getManagedBean().getFinSpecZoneBean()) {
						for (TplSpecZoneDtl tplSpecZoneDtl : speczone
								.getZones()) {
							EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
							this.getManagedBean().setSelectedZone(
									speczone.getZones().get(0));
							zoneReqDto.setSpeZoneId(tplSpecZoneDtl
									.getSpecZoneId());
							zoneReqDto.setCheckListId(this.getManagedBean()
									.getScQtChecklist().getQtChecklistId());
							EvaluationResDTO zoneResDTO = evaluationSo
									.retrievProposalCheckListByCheckListNZone(zoneReqDto);
							this.getManagedBean().setFinSpecZoneBean(
									new ArrayList<SummaryZoneBaen>());
							SummaryZoneBaen finSpecZone = new SummaryZoneBaen(
									tplSpecZoneDtl, null, null, 0);
							if (null != zoneResDTO.getComplianceResultBean()
									&& null != zoneResDTO
											.getComplianceResultBean()
											.getResultList()
									&& !zoneResDTO.getComplianceResultBean()
											.getResultList().isEmpty()) {

								finSpecZone = new SummaryZoneBaen(
										tplSpecZoneDtl, zoneResDTO
												.getComplianceResultBean()
												.getResultList(), null, 0);
							}

							if (null != speczone.getScoreOfferPrices()) {
								finSpecZone = new SummaryZoneBaen(
										tplSpecZoneDtl, zoneResDTO
												.getComplianceResultBean()
												.getResultList(),
										speczone.getScoreOfferPrices(), 0);
							}

							this.getManagedBean().getFinSpecZoneBean()
									.add(finSpecZone);

						}
					}
					return "evaluationFinancialSpec.xhtml";
				} else {
					// rertriveFinSpecDtlsByChecklistId() withoutzone
					// retriveProposalChecklistByzone() withzonespec
					if (null != speczone.getComplianceResultBean()
							&& null != speczone.getComplianceResultBean()
									.getResultList()
							&& !speczone.getComplianceResultBean()
									.getResultList().isEmpty()) {
						this.getManagedBean().setFinSpecSupplierList(
								speczone.getComplianceResultBean()
										.getResultList());
					}
					if (null != speczone.getScoreOfferPrices()) {
						this.getManagedBean().setOfferPricePackage(
								speczone.getScoreOfferPrices());
					}
					return "evaluationFinSpecWithOutzone.xhtml";
				}

			} else {
				return "standardEvaluationFinList.xhtml";

			}
		} finally {

		}

	}

	public void specTabChange(TabChangeEvent event) {
		TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) event.getData();
		getManagedBean().setSelectedZone(specZoneDtl);

	}

	public String othersTechnicalEvaluate(
			ScQtProposalChecklist proposalCheckList) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);

				if (null != resDTO.getComplainceList()

				&& !resDTO.getComplainceList().isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplainceList());
				}
				if (null != resDTO.getSpecItems()
						&& !resDTO.getSpecItems().isEmpty()) {
					this.getManagedBean()
							.setSpecItemList(resDTO.getSpecItems());
				}

				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSupplier(proposalCheckList
						.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_SCORE);
				templateSpecBean.setSpecMaster(proposalCheckList
						.getScQtCheckList().getScQtDoc().getTplSpecMaster());
				specBB.retrieveTemplateSpecMaster(templateSpecBean);

				return "productServiceTechScoreSpec.xhtml";
			}

			else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO.retrieveSampleDetails(reqDTO);
				if (null != resDTO.getItemResultBean()
						&& null != resDTO.getItemResultBean().getResultList()
						&& !resDTO.getItemResultBean().getResultList()
								.isEmpty()) {
					this.getManagedBean().setSampleItemList(
							resDTO.getItemResultBean().getResultList());
				}

				if (null != resDTO.getComplainceList()

				&& !resDTO.getComplainceList().isEmpty()) {
					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplainceList());
				}

				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					this.getManagedBean().setTechSampleProposalScore(
							resDTO.getProposalScores());
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechSampleProposalScore());
				}

				return "techSampleEvaluation.xhtml";
			} else {
				if (QTConstants.TECHNICAL_GOV_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO.retrieveCompanyExpDetails(reqDTO);
					List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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
					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getLoaResultBean()
							&& null != resDTO.getLoaResultBean()
									.getResultList()
							&& !resDTO.getLoaResultBean().getResultList()
									.isEmpty()) {
						this.getManagedBean().setLoaGovCompanyExpList(
								resDTO.getLoaResultBean().getResultList());
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechGovProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getTechGovProposalScore());
					}

					/*
					 * this.getManagedBean().getScQtChecklist().
					 * setScQtCheckListValue(resDTO .getScQtCheckListValue());
					 */
					return "techGovCompanyExperience.xhtml";
				} else if (QTConstants.TECHNICAL_PRIVATE_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {
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
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getPrpExperience()
							&& !resDTO.getPrpExperience().isEmpty()) {

						this.getManagedBean().setPrpCompanyExperienceList(
								resDTO.getPrpExperience());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechPrivateProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getTechPrivateProposalScore());
					}
					/*
					 * this.getManagedBean().getScQtChecklist().
					 * setScQtCheckListValue(resDTO .getScQtCheckListValue());
					 */
					return "techPrivateCompanyExperience.xhtml";
				}

				else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveTechinicalStaffDtls(reqDTO);

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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
					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechStaffProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getTechStaffProposalScore());
					}

					return "senariTechnical.xhtml";
				}

				else if (QTConstants.TECHNICAL_NUMBER_EMPLOYEES
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveEmployeeDtls(reqDTO);

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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
					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}
					if (null != resDTO.getScQtPRPEmployee()) {
						this.getManagedBean().setNoOfEmployeeList(
								resDTO.getScQtPRPEmployee());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setTechNoOfEmployeeProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getTechNoOfEmployeeProposalScore());
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
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);
				if (null != resDTO.getComplainceList()

				&& !resDTO.getComplainceList().isEmpty()) {

					this.getManagedBean().setEvaluationComplianceList(
							resDTO.getComplainceList());

				}
				if (null != resDTO.getSpecItems()
						&& !resDTO.getSpecItems().isEmpty()) {
					this.getManagedBean()
							.setSpecItemList(resDTO.getSpecItems());
				}
				if (null != resDTO.getScoreOfferPrices()) {
					this.getManagedBean().setOfferPriceItem(
							resDTO.getScoreOfferPrices());
				}

				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSupplier(proposalCheckList
						.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_SCORE);
				templateSpecBean.setSpecMaster(proposalCheckList
						.getScQtCheckList().getScQtDoc().getTplSpecMaster());
				specBB.retrieveTemplateSpecMaster(templateSpecBean);
				return "productStandardTarifTierFinSpec.xhtml";
			}

			else {

				if (QTConstants.FINANCIAL_GOV_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO.retrieveCompanyExpDetails(reqDTO);
					List<ScQtCheckListScore> finGovContract = new ArrayList<ScQtCheckListScore>();

					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getLoaResultBean()
							&& null != resDTO.getLoaResultBean()
									.getResultList()
							&& !resDTO.getLoaResultBean().getResultList()
									.isEmpty()) {
						this.getManagedBean().setLoaGovCompanyExpList(
								resDTO.getLoaResultBean().getResultList());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setFinGovProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinGovProposalScore());
					}

					return "finGovCompanyExperience.xhtml";
				} else if (QTConstants.FINANCIAL_PRIVATE_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

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
					List<ScQtCheckListScore> finGovContract = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getPrpExperience()
							&& !resDTO.getPrpExperience().isEmpty()) {

						this.getManagedBean().setPrpCompanyExperienceList(
								resDTO.getPrpExperience());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setFinPrivateProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinPrivateProposalScore());
					}
					return "finPrivateCompanyExperience.xhtml";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveBankStmtDtls(reqDTO);
					List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setFinbankStmtProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinbankStmtProposalScore());
					}

					if (null != resDTO.getBankStmtResultBean()
							&& !resDTO.getBankStmtResultBean().isEmpty()) {
						if (this.getManagedBean().getBankStmtList() == null) {
							int total = 0;
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
									monthvalue = "March".concat(convert
											.substring(6, convert.length()));
								}

								if ("04".equalsIgnoreCase(month)) {
									monthvalue = "April".concat(convert
											.substring(6, convert.length()));
								}

								if ("05".equalsIgnoreCase(month)) {
									monthvalue = "May".concat(convert
											.substring(6, convert.length()));
								}
								if ("06".equalsIgnoreCase(month)) {
									monthvalue = "June".concat(convert
											.substring(6, convert.length()));
								}

								if ("07".equalsIgnoreCase(month)) {
									monthvalue = "July".concat(convert
											.substring(6, convert.length()));
								}

								if ("08".equalsIgnoreCase(month)) {
									monthvalue = "August".concat(convert
											.substring(6, convert.length()));
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
								total = total
										+ bankStmt.getBalanceAmt().intValue();
								this.getManagedBean().setBankTotalAmt(
										new BigDecimal(total));
								bankStmt.setMonth(monthvalue);
								bankList.add(bankStmt);
								this.getManagedBean().setBankStmtList(bankList);

							}
						}
					}

					return "bankStatement.xhtml";
				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveCreditFacilitiesDtls(reqDTO);
					List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getCreditFacilityResultBean()
							&& !resDTO.getCreditFacilityResultBean().isEmpty()) {
						this.getManagedBean().setCreditFacilityList(
								resDTO.getCreditFacilityResultBean());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean()
								.setFinCreditFacilityProposalScore(
										resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinCreditFacilityProposalScore());
					}

					return "creditFacility.xhtml";
				} else if (QTConstants.FINANCIAL_CASH_ON_HAND_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO.retrieveCashOnHandDtls(reqDTO);
					List<ScQtCheckListScore> cashOhHand = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 52) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								cashOhHand.add(checklistScore);
								this.getManagedBean().setCashOnHandScore(
										cashOhHand);

							}

						}
					}

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getCashOnHandResultBean()
							&& !resDTO.getCashOnHandResultBean().isEmpty()) {
						this.getManagedBean().setCashOnHandList(
								resDTO.getCashOnHandResultBean());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setFinCashOnHandProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinCashOnHandProposalScore());
					}

					return "cashOnHand.xhtml";
				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO.retrieveAssetLiabilityDtls(reqDTO);
					List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean()
								.setFinAssetLiabilityProposalScore(
										resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinAssetLiabilityProposalScore());
					}

					return "assetsLiabilities.xhtml";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);

					reqDTO.setProposalChecklist(proposalCheckList);
					// resDTO = evaluationSO.ret
					List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();
					if (null != resDTO.getScoreResultBean()
							&& null != resDTO.getScoreResultBean()
									.getResultList()
							&& !resDTO.getScoreResultBean().getResultList()
									.isEmpty()) {
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

					if (null != resDTO.getComplainceList()

					&& !resDTO.getComplainceList().isEmpty()) {
						this.getManagedBean().setEvaluationComplianceList(
								resDTO.getComplainceList());
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {

						this.getManagedBean().setFinPaidUpCapitalProposalScore(
								resDTO.getProposalScores());
						this.getManagedBean()
								.getQtProposalChecklist()
								.setProposalScore(
										this.getManagedBean()
												.getFinPaidUpCapitalProposalScore());
					}

					return "paidUpCapital.xhtml";
				}

				else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
								.getPmDocument().getDocDefinitionKey())) {
					return "supplierInfo.xhtml";
				}
			}
		} finally {

		}

		return null;
	}

	public void retrivePresentationDemo() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setPresentationDemoActiveIndex(0);

			/*
			 * EvaluationReqDTO reqDTO = new EvaluationReqDTO(); evaluationSo =
			 * BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			 * reqDTO.setCompliance(true); resDTO =
			 * evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 */
			/*
			 * if (null!=resDTO.getComplianceResultBean() &&
			 * null!=resDTO.getComplianceResultBean().getResultList() &&
			 * !resDTO.getComplianceResultBean().getResultList() .isEmpty()) {
			 * this.getManagedBean().setPresentationDemoSupplierList(resDTO.
			 * getComplianceResultBean().getResultList());
			 * 
			 * }
			 */

		} finally {

		}
	}

	public void retrivePresentationDemoSummary() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = null;
		try {
			this.getManagedBean().setPresentationDemoActiveIndex(1);
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
			evalReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			evalReqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
			evalReqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
			evalReqDTO.setRecomandationType(1);
			List<String> resultStg = new ArrayList<String>();
			resultStg.add(QTConstants.AWARD_TYPE_PACKAGE);
			evalReqDTO.setResultStages(resultStg);
			evaluationSo.prepareEvaluationResultsByStage(evalReqDTO);
			EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
			summaryReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			List<String> resultStg2 = new ArrayList<String>();
			resultStg2.add(QTConstants.AWARD_TYPE_PACKAGE);
			summaryReqDTO.setResultStages(resultStg2);
			evalResDTO = evaluationSo.retrieveSummaryDetails(summaryReqDTO);
			if (null != evalResDTO.getQulifiedSuppliers()
					&& null != evalResDTO.getQulifiedSuppliers()
							.getResultList()

					&& !evalResDTO.getQulifiedSuppliers().getResultList()
							.isEmpty()) {

				this.getManagedBean().setEvaluationSummaryQualSuppliers(
						evalResDTO.getQulifiedSuppliers().getResultList());
				this.getManagedBean().setEvaTotQualifiedSuppliers(
						evalResDTO.getQulifiedSuppliers().getTotalRecord());
			}
			if (null != evalResDTO.getDisQulifiedSuppliers()
					&& null != evalResDTO.getDisQulifiedSuppliers()
							.getResultList()
					&& evalResDTO.getDisQulifiedSuppliers().getResultList()
							.isEmpty()) {
				this.getManagedBean().setEvaluationSummaryDisQualSuppliers(
						evalResDTO.getDisQulifiedSuppliers().getResultList());
				this.getManagedBean().setEvaTotDisQualifiedSuppliers(
						evalResDTO.getDisQulifiedSuppliers().getTotalRecord());
			}
		} finally {

		}
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
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					retriveTechnicalEvaluationStage();
				}

				if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					retriveFinancialEvaluationStage();
				}
				// getManagedBean().getTemplateSpecBean().setSelectedSpecQuestion(new
				// TplSpecQuestion());
				// retrives2Details();
			}
			if (outputTab == 3) {
				// this.getManagedBean().setScqt(new ScQt());
				// retriveStageOneSummary();
				retrivePresentationDemo();

			}
			if (outputTab == 4) {
				// this.getManagedBean().setScqt(new ScQt());
				// retrivesummary();

			}
			if (outputTab == 5) {
				retriveCommitteeEndorsment();
			}
			if (outputTab == 6) {

			}
			if (outputTab == 0) {
				retriveMeetingDtls(this.getManagedBean().getScQt().getQtId(),
						this.getManagedBean().getCommitteeType());
			}

			if (getManagedBean().getTempTab() < outputTab) {
				getManagedBean().setTempTab(outputTab);
			}
			getManagedBean().setTabActiveIndex(outputTab);
		} finally {

		}
	}

	public void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			PmParameter committeeType = this.getManagedBean().getScQt()
					.getScQtCommittees().get(0).getScCommittee()
					.getCommitteeType();
			reqDTO.setCommitteeType(committeeType.getParameterCode());
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);

			this.getManagedBean().setEndorscommitteeMemberList(
					resDTO.getCommMemberResultBean().getResultList());

		} finally {
			committeSO = null;
			resDTO = null;
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
			PmStatus status = new PmStatus();
			meeting.setMeetingStatus(status);
			setUserDetails(meeting);
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
		IQTMeetingSO meetingso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTMeetingSO.class);
		try {
			MeetingReqDTO metReqDtO = evaluationPreparation();

			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public MeetingReqDTO evaluationPreparation() {
		MeetingReqDTO metReqDtO = new MeetingReqDTO();
		try {

			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			if (null != this.getManagedBean().getScQtMeetings()
					&& !this.getManagedBean().getScQtMeetings().isEmpty()) {
				qtCommittee.setScQtMeetings(this.getManagedBean()
						.getScQtMeetings());
			}
			if (null != this.getManagedBean().getCommitteeMemberList()
					&& !this.getManagedBean().getCommitteeMemberList()
							.isEmpty()) {
				qtCommittee.setCommitteeActivities(this.getManagedBean()
						.getCommitteeMemberList());
			}
			if (null != getManagedBean().getMeetingAttendanceList()
					&& !getManagedBean().getMeetingAttendanceList().isEmpty()) {
				qtCommittee.setMeetingAttendances(getManagedBean()
						.getMeetingAttendanceList());
			}

			metReqDtO.setScQtCommitte(qtCommittee);

		} finally {

		}
		return metReqDtO;

	}

	public void saveTechCompantDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveTechNonCompantDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveTechSampleDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveTechStaffDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveTechNoOfEmployeeDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinCompantDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinNonCompantDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinBankStmtDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinCreditFacilityDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinCashOnHandDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinAssetLiabilityDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinPaidUpCaptlDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {

		}

	}

	public void saveFinSupplierInfoDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDtO = new EvaluationReqDTO();

			reqDtO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evaluationSO.saveProposalDetails(reqDtO);

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

	public void downloadDocuments(ScQtProposalChecklist proposalCheckList) {
		try {

			downloadFile(proposalCheckList.getScQtProposalChecklistAttachment());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void secretarySubmitTask() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType(QTConstants.DOCTYPE_SPEC_COMMITTE);
			reqDTO.setSubject(this.getManagedBean().getSubject());
			reqDTO.setBodyText(this.getManagedBean().getMessage());
			committeSO.sendCommitteEndorseNotification(reqDTO);

		} finally {

		}
	}

	public EvaluationResDTO saveTempleateSpecDtls() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			List<TplSpecAnswer> answers = getManagedBean()
					.getTemplateSpecBean().getSpecAnswerList();
			for (TplSpecAnswer tplSpecAnswer : answers) {
				tplSpecAnswer.setTplSpecSuppAnswer(tplSpecAnswer
						.getTplSpecSuppAnswer());

				tplSpecAnswer.getTplSpecSuppAnswer().setTplSpecZoneDtl(
						getManagedBean().getSelectedZone());

			}

			evalReqDTO.setAnswersList(answers);
			evalResDTO = evaluationSO.saveSpecDetails(evalReqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} finally {
			evaluationSO = null;
		}
		return evalResDTO;
	}

	public String submitPrepareEvaluationDetails(String outCome) {
		try {
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();

			IQTEvaluationSO evaluationso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(ctxToken);
			reqDTO.setPayLoad(this.getManagedBean().getEvaluationPayLoad());
			reqDTO.setOutcome(outCome);
			EvaluationResDTO resDTO = evaluationso.updateBPMAction(reqDTO);

			// Code to renew the session Object..
			renewSesionObject();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Prepare Evaluation Submitted Successfully",
							"Prepare Evaluation Submitted Successfully"));
		} finally {

		}

		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	public void preRenderView() {
		renewSesionObject();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		log.info("taskId : " + taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		String taskName = res.getTaskName();
		this.getManagedBean().setOutComes(res.getAction());
		SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) obj;
		this.getManagedBean().setEvaluationPayLoad(evaluationDataType);

		FacesContext fc = FacesContext.getCurrentInstance();

		retriveMeetingDtls(evaluationDataType.getQtId().longValue(),
				evaluationDataType.getCommitteeType());

		if (SCProcessNameConst.FINALISE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}else if (SCProcessNameConst.ENDORSE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}
	}

}

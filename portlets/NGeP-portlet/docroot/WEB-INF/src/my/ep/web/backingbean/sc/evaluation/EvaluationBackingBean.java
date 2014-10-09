package my.ep.web.backingbean.sc.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
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

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationReportSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.EvaluationReportReqDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.evaluation.EvaluationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
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

	private static final String DOCUMENT_PAGE = "/xhtml/sc/evaluation/evaluationstage1view.xhtml?faces-redirect=true";

	public EvaluationBackingBean() throws ParseException {

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
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
				if (!this.getManagedBean().getScQt().isZonal()) {
					award = SourcingConstants.AWARD_BY_ITEM;
				}
				this.getManagedBean().setSummaryZoneBean(null);
			} else if (QTConstants.FINANCIAL_EVALUATION_BY_ITEM
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())
					&& ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg);
				if (!this.getManagedBean().getScQt().isZonal()) {
					award = SourcingConstants.AWARD_BY_ITEM;
				}
				this.getManagedBean().setSummaryZoneBean(null);
			} else if (QTConstants.PREFERENCE_MARGIN.equalsIgnoreCase(this
					.getManagedBean().getSummaryViewBy())) {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					resultStg.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				} else {
					resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
				}
				summaryReqDTO.setResultStages(resultStg);
				this.getManagedBean().setSummaryZoneBean(null);
			}

			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			summaryReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			summaryReqDTO.setZonal(this.getManagedBean().getScQt().isZonal());
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
							summaryReqDTO.setZonal(false);
							award = SourcingConstants.AWARD_BY_ITEM;
							summaryReqDTO.setAwardBy(award);
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

							} else {
								bean = new SummaryZoneBaen(tplSpecZoneDtl, null);
							}
							/*
							 * this.getManagedBean().getSummaryZoneBean()
							 * .add(bean);
							 */
						} else {
							bean = new SummaryZoneBaen(tplSpecZoneDtl, null,
									null);
							this.getManagedBean()
									.setEvaTotQualifiedSuppliers(0);
							if (null != evalResDTO1.getQulifiedSuppliers()

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
							this.getManagedBean()
									.setEvaTotDisQualifiedSuppliers(0);
							if (null != evalResDTO1.getDisQulifiedSuppliers()

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
				this.getManagedBean().setEvaTotQualifiedSuppliers(0);
				this.getManagedBean().setEvaluationSummaryQualSuppliers(null);
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
				this.getManagedBean()
						.setEvaluationSummaryDisQualSuppliers(null);
				this.getManagedBean().setEvaTotDisQualifiedSuppliers(0);
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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			List<String> resultStg = new ArrayList<String>();
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_ITEM);
			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
			}
			reqDTO.setResultStages(resultStg);
			// reqDTO.setAwardBy(QTConstants.AWARD_TYPE_ITEM);
			reqDTO.setRequestItemId(requestItem.getRequestNoteItemId());
			evalResDTO = evaluationSO
					.retrieveSummaryDtlsByZoneNAwardNItem(reqDTO);
			this.getManagedBean().setItemSummaryDisQualSuppliers(null);
			this.getManagedBean().setItemTotDisQualSuppliers(0);
			this.getManagedBean().setItemSummaryQualSuppliers(null);
			this.getManagedBean().setItemTotQualSuppliers(0);
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

	public void retriveMeetingDtls(Long qtId, String commType, int index) {
		IQTMeetingSO meetingSO;
		IQTDetailSO qtDetailSo = null;

		try {
			this.getManagedBean().setCommitteeType(commType);
			this.getManagedBean().setPrepareActiveIndex(index);
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
			if (null == this.getManagedBean().getScQtMeetings()) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

				reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getMeetingDtls() != null
						&& !resDTO.getMeetingDtls().isEmpty()) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}

				this.getManagedBean().setScQtCommittee(resDTO.getQtCommittee());
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
				List<ScQtCommitteRemarks> qtRemarkList = this.getManagedBean()
						.getScQtCommittee().getQtCommitteRemarks();

				if (null == this.getManagedBean().getQtCommitteeRemarks()) {

					List<ScQtCommitteRemarks> remarkList = new ArrayList<ScQtCommitteRemarks>();
					for (ScRemark remark : qtRemarkList) {
						ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
						remarkList.add(qtRemark);

					}
					this.getManagedBean().setQtCommitteeRemarks(remarkList);

					// initializeRemarks();
				}

				if (null == this.getManagedBean().getMeetingAttendanceList()) {
					this.getManagedBean().setMeetingAttendanceList(
							this.getManagedBean().getScQtCommittee()
									.getMeetingAttendances());
				}

			}

		} finally {
			meetingSO = null;
			qtDetailSo = null;
		}

	}

	public void retriveOpeningCommitteeDtls(int index) {

		try {
			this.getManagedBean().setPrepareActiveIndex(index);

		} finally {

		}

	}

	public String retriveTurutHadirDtls(int index) {

		try {
			this.getManagedBean().setPrepareActiveIndex(index);

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
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}
		} finally {

		}
		return "technicalComplianceStage.xhtml";
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
			if (resDTO.getItemResultBean().getTotalRecord() > 0) {
				this.getManagedBean().setComplianceSampleItemList(
						resDTO.getItemResultBean().getResultList());

			}

		} finally {

		}
		return "sampleComplianceStage.xhtml";
	}

	public void saveComplianceSample() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtSampleItem> sampleCompliance = this.getManagedBean()
					.getComplianceSampleItemList();
			if (null != sampleCompliance && !sampleCompliance.isEmpty()) {
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
			List<ScQtProposalChecklist> presentatiodemo = this.getManagedBean()
					.getPresentationDemoSupplierList();
			List<ScQtProposalChecklist> technicalCompliance = this
					.getManagedBean().getComplianceTechnicalProposalCheckList();
			if (null != technicalCompliance && !technicalCompliance.isEmpty()) {
				reqDTO.setComplianceList(technicalCompliance);
			}
			if (null != presentatiodemo && !presentatiodemo.isEmpty()) {
				reqDTO.setComplianceList(presentatiodemo);
			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.",
							"Evaluation Compliance saved successfully."));

		} finally {

		}
	}

	public String evaluationBackaction() {

		return "evaluationstage1view.xhtml";

	}

	public String technicalBackaction() {

		return "standardEvaluationTechList.xhtml";

	}

	public String financialBackaction() {

		return "standardEvaluationFinList.xhtml";

	}

	public String technicalSpecBackaction() {

		return "technicalSpecEvaluation.xhtml";

	}

	public String financialSpecBackaction() {
		if (this.getManagedBean().getScQt().isZonal()) {
			return "evaluationFinancialSpec.xhtml";
		} else {
			return "evaluationFinSpecWithOutzone.xhtml";
		}

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
			if (null != resDTO.getLableResultBean().getResultList()
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
			// if (this.getManagedBean().getComplianceSummaryCount() == 1) {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			List<String> evalstage = new ArrayList<String>();
			evalstage.add(QTConstants.COMPLIANCE_CHECK);
			reqDTO.setResultStages(evalstage);
			evaluationSO.prepareComplianceSummaryResults(reqDTO);
			EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
			summaryReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			List<String> qualifiedstage = new ArrayList<String>();
			qualifiedstage.add(QTConstants.COMPLIANCE_CHECK);
			summaryReqDTO.setResultStages(evalstage);
			resDTO = evaluationSO
					.retrieveSummaryDtlsByZoneNAwardNItem(summaryReqDTO);
			if (resDTO.getQulifiedSuppliers().getTotalRecord() > 0) {

				this.getManagedBean().setComplianceSummaryQualSuppliers(
						resDTO.getQulifiedSuppliers().getResultList());
				this.getManagedBean().setComplianceQualifiedSuppliers(
						resDTO.getQulifiedSuppliers().getTotalRecord());
			}
			if (resDTO.getDisQulifiedSuppliers().getTotalRecord() > 0) {
				this.getManagedBean().setComplianceSummaryDisQualSuppliers(
						resDTO.getDisQulifiedSuppliers().getResultList());
				this.getManagedBean().setComplianceDisQualifiedSuppliers(
						resDTO.getDisQulifiedSuppliers().getTotalRecord());
			}
			// }

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
				if (this.getManagedBean().getScQt().isDemoRequired()) {
					reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
				} else {
					reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
				}

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

			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(2);
			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(1);
			}
			int count = this.getManagedBean().getComplianceSummaryCount();
			count++;
			int count2 = count;
			this.getManagedBean().setComplianceSummaryCount(count2);
			// if (this.getManagedBean().getComplianceSummaryCount() == 1) {

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				evalReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				evalReqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
				evalReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				evalReqDTO.setRecomandationType(1);
				List<String> resultStg = new ArrayList<String>();
				resultStg.add(QTConstants.TECHNICAL_EVALUATION);
				evalReqDTO.setResultStages(resultStg);
				evaluationSo.prepareEvaluationResultsByStage(evalReqDTO);
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());
				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.TECHNICAL_EVALUATION);
				summaryReqDTO.setResultStages(resultStg2);
				summaryReqDTO.setZonal(false);
				evalResDTO = evaluationSo.retrieveSummaryDetails(summaryReqDTO);

				if (evalResDTO.getQulifiedSuppliers().getTotalRecord() > 0) {

					this.getManagedBean().setEvaluationSummaryQualSuppliers(
							evalResDTO.getQulifiedSuppliers().getResultList());
					this.getManagedBean().setEvaTotQualifiedSuppliers(
							evalResDTO.getQulifiedSuppliers().getTotalRecord());
				}
				if (evalResDTO.getDisQulifiedSuppliers().getTotalRecord() > 0) {
					this.getManagedBean().setEvaluationSummaryDisQualSuppliers(
							evalResDTO.getDisQulifiedSuppliers()
									.getResultList());
					this.getManagedBean().setEvaTotDisQualifiedSuppliers(
							evalResDTO.getDisQulifiedSuppliers()
									.getTotalRecord());
				}
			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {

				/*
				 * evalReqDTO.setQtId(this.getManagedBean().getScQt()
				 * .getQtId()); evalReqDTO
				 * .setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
				 * evalReqDTO .setEvalStage(QTConstants.TECHNICAL_EVALUATION);
				 * evalReqDTO.setRecomandationType(2); List<String> resultStg =
				 * new ArrayList<String>();
				 * resultStg.add(QTConstants.FINANCIAL_EVALUATION);
				 * evalReqDTO.setResultStages(resultStg);
				 * //evaluationSo.prepareEvaluationResultsByStage (evalReqDTO);
				 */
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());
				if (this.getManagedBean().getScQt().isDemoRequired()) {
					summaryReqDTO
							.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);

				} else {
					summaryReqDTO
							.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
				}
				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg2);
				evaluationSo
						.prepareEvaluationResultsSummaryForTwoTier(summaryReqDTO);

			}

			else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());
				summaryReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg2);

				evaluationSo
						.prepareEvaluationResultsSummaryForOneTier(summaryReqDTO);

			}
			// }

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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				reqDTO.setCompliance(true);
			} else {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

			}
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				reqDTO.setCompliance(true);
			} else {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

			}
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "technicalEvaluationEvaluateDocument.xhtml";
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
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
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				reqDTO.setCompliance(true);
			} else {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

			}
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				return "technicalEvaluationStgView.xhtml";
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {

				EvaluationReqDTO specDtls = new EvaluationReqDTO();
				specDtls.setQtId(this.getManagedBean().getScQt().getQtId());
				specDtls.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());
				if (!this.getManagedBean().getScQt().isZonal()) {
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else {
						if (this.getManagedBean().getScQt().isDemoRequired()) {
							reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
						} else {
							reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						}
					}
				}
				EvaluationResDTO speczone = evaluationSo
						.retrieveFinSpecDetailsByCheckListId(specDtls);

				if (null != speczone.getZones()
						&& !speczone.getZones().isEmpty()) {
					if (null == this.getManagedBean().getFinSpecZoneBean()) {
						this.getManagedBean().setSpecZoneDtls(
								speczone.getZones());
						for (TplSpecZoneDtl tplSpecZoneDtl : speczone
								.getZones()) {

							EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
							if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
									.equalsIgnoreCase(this.getManagedBean()
											.getCommitteeType())) {
								zoneReqDto
										.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							} else {
								if (this.getManagedBean().getScQt()
										.isDemoRequired()) {
									zoneReqDto
											.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
								} else {
									zoneReqDto
											.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
								}
							}

							this.getManagedBean().setSelectedZone(
									speczone.getZones().get(0));

							zoneReqDto.setQtId(this.getManagedBean().getScQt()
									.getQtId());
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
							if (zoneResDTO.getComplianceResultBean()
									.getTotalRecord() > 0) {

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
					if (speczone.getComplianceResultBean().getTotalRecord() > 0) {
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
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				reqDTO.setCompliance(true);
			} else {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

			}
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			this.getManagedBean().setComplianceTechnicalProposalCheckList(
					new ArrayList<ScQtProposalChecklist>());
			if (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
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
				if (!this.getManagedBean().getScQt().isZonal()) {
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else {
						if (this.getManagedBean().getScQt().isDemoRequired()) {
							specDtls.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
						} else {
							specDtls.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						}
					}
				}
				EvaluationResDTO speczone = evaluationSo
						.retrieveFinSpecDetailsByCheckListId(specDtls);

				if (null != speczone.getZones()
						&& !speczone.getZones().isEmpty()) {
					if (null == this.getManagedBean().getFinSpecZoneBean()) {
						this.getManagedBean().setFinSpecZoneBean(
								new ArrayList<SummaryZoneBaen>());
						this.getManagedBean().setSpecZoneDtls(
								speczone.getZones());
						for (TplSpecZoneDtl tplSpecZoneDtl : speczone
								.getZones()) {
							EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
							if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
									.equalsIgnoreCase(this.getManagedBean()
											.getCommitteeType())) {
								zoneReqDto
										.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							} else {
								if (this.getManagedBean().getScQt()
										.isDemoRequired()) {
									zoneReqDto
											.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
								} else {
									zoneReqDto
											.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
								}
							}

							this.getManagedBean().setSelectedZone(
									speczone.getZones().get(0));

							zoneReqDto.setQtId(this.getManagedBean().getScQt()
									.getQtId());
							zoneReqDto.setSpeZoneId(tplSpecZoneDtl
									.getSpecZoneId());
							zoneReqDto.setCheckListId(this.getManagedBean()
									.getScQtChecklist().getQtChecklistId());
							EvaluationResDTO zoneResDTO = evaluationSo
									.retrievProposalCheckListByCheckListNZone(zoneReqDto);

							SummaryZoneBaen finSpecZone = new SummaryZoneBaen(
									tplSpecZoneDtl, null, null, 0);
							if (zoneResDTO.getComplianceResultBean()
									.getTotalRecord() > 0) {

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
					if (speczone.getComplianceResultBean().getTotalRecord() > 0) {
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
		SummaryZoneBaen specZoneDtl = (SummaryZoneBaen) event.getData();
		getManagedBean().setSelectedZone(specZoneDtl.getTplSpecZoneDtl());

		for (int i = 0; i < this.getManagedBean().getSpecZoneDtls().size(); i++) {
			TplSpecZoneDtl zoneDtls = this.getManagedBean().getSpecZoneDtls()
					.get(i);
			if (zoneDtls.getZoneName().equalsIgnoreCase(
					specZoneDtl.getTplSpecZoneDtl().getZoneName())) {
				this.getManagedBean().setSpecZoneTabIndex(i);
			}
		}

	}

	public String othersTechnicalEvaluate(
			ScQtProposalChecklist proposalCheckList) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		String proposalPage = null;
		String evaluationPage = null;
		SPDetailBackingBean detailBackingBean = new SPDetailBackingBean();
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			reqDTO.setProposalChecklist(proposalCheckList);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
			reqDTO.setCompliance(true);
			EvaluationResDTO evalResDTO = evaluationSO
					.retrieveComplainceCheckListsForSupplier(reqDTO);
			this.getManagedBean().setTechnicalComplinaceList(
					evalResDTO.getComplainceList());

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);
				this.getManagedBean().setEvaluationComplianceList(
						new ArrayList<ScQtProposalChecklist>());
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
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());

				if (resDTO.getItemResultBean().getTotalRecord() > 0) {
					this.getManagedBean().setSampleItemList(
							resDTO.getItemResultBean().getResultList());
				}

				this.getManagedBean().setNoOfContractsProposalScore(null);
				if (null != resDTO.getProposalScores()
						&& !resDTO.getProposalScores().isEmpty()) {

					for (ScQtProposalScore proposalScore : resDTO
							.getProposalScores()) {

						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}

				} else {
					ScQtProposalScore proposalScore = new ScQtProposalScore();
					proposalScore.setProposalChecklist(this.getManagedBean()
							.getQtProposalChecklist());
					setUserDetails(proposalScore);
					this.getManagedBean().setNoOfContractsProposalScore(
							proposalScore);
					this.getManagedBean()
							.getTechGovProposalScore()
							.add(this.getManagedBean()
									.getNoOfContractsProposalScore());

				}
				this.getManagedBean()
						.getQtProposalChecklist()
						.setProposalScore(
								this.getManagedBean().getTechGovProposalScore());
				return "techSampleEvaluation.xhtml";
			} else {
				String docDefinationKey = proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey();
				this.getManagedBean().setDocDefinitionKey(docDefinationKey);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());
				// this.getManagedBean().setNoOfContractsProposalScore(null);
				// this.getManagedBean().setCompanyScoreType(null);

				if (QTConstants.TECHNICAL_GOV_CODE
						.equalsIgnoreCase(docDefinationKey)) {
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();

					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
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

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 10) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}
							if (proposalScore.getCriteriaType() == 11) {
								this.getManagedBean()
										.setNoOfyearsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this.getManagedBean()
												.getNoOfyearsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(10);
						setUserDetails(proposalScore);
						ScQtProposalScore proposalScoreyears = new ScQtProposalScore();
						proposalScoreyears.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScoreyears.setCriteriaType(11);
						setUserDetails(proposalScoreyears);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean().setNoOfyearsProposalScore(
								proposalScoreyears);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfyearsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					detailBackingBean.getManagedBean().setQt(
							this.getManagedBean().getScQt());
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());
					proposalPage = detailBackingBean
							.retrieveTecCompanyExpDtls(proposalCheckList);
					evaluationPage = "techGovCompanyExperience.xhtml";
					// return "techGovCompanyExperience.xhtml";
				} else if (QTConstants.TECHNICAL_PRIVATE_CODE
						.equalsIgnoreCase(docDefinationKey)) {
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
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();

					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 15) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfContracts.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												noOfContracts);

							} else if (checklistScore.getCriteriaType() == 16) {

								this.getManagedBean().setCompanyScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								noOfYears.add(checklistScore);
								this.getManagedBean().setTechnicalGovNoOfYears(
										noOfYears);

							}

						}
					}

					/*
					 * this.getManagedBean().getScQtChecklist().
					 * setScQtCheckListValue(resDTO .getScQtCheckListValue());
					 */
					// return "techPrivateCompanyExperience.xhtml";
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 15) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}
							if (proposalScore.getCriteriaType() == 16) {
								this.getManagedBean()
										.setNoOfyearsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this.getManagedBean()
												.getNoOfyearsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(15);
						setUserDetails(proposalScore);
						ScQtProposalScore proposalScoreyears = new ScQtProposalScore();
						proposalScoreyears.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScoreyears.setCriteriaType(16);
						setUserDetails(proposalScoreyears);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean().setNoOfyearsProposalScore(
								proposalScoreyears);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfyearsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveTecNonGovCompanyExpDtls(proposalCheckList);
					evaluationPage = "techPrivateCompanyExperience.xhtml";
				}

				else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(docDefinationKey)) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					reqDTO.setCheckListId(proposalCheckList.getScQtCheckList()
							.getQtChecklistId());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());
					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 20) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								technicalStaff.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												technicalStaff);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 20) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					}

					else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(20);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());
					detailBackingBean.getManagedBean().setSmSupplier(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier());
					proposalPage = detailBackingBean
							.retriveTechnicalStaffDtls(proposalCheckList);
					evaluationPage = "senariTechnical.xhtml";

					// return "senariTechnical.xhtml";
				}

				else if (QTConstants.TECHNICAL_NUMBER_EMPLOYEES
						.equalsIgnoreCase(docDefinationKey)) {

					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());

					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 21) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								technicalStaff.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												technicalStaff);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 21) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(21);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveEmployeeDtls(proposalCheckList);
					evaluationPage = "bilanganPerjaka.xhtml";
					// return "bilanganPerjaka.xhtml";
				}

			}

		} finally {

		}

		this.getManagedBean().setProposalPage(proposalPage);
		this.getManagedBean().setEvaluationPage(evaluationPage);

		return "technicalotherchecklists.xhtml";
	}

	public String othersFinancialEvaluate(
			ScQtProposalChecklist proposalCheckList) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		String proposalPage = null;
		String evaluationPage = null;
		SPDetailBackingBean detailBackingBean = new SPDetailBackingBean();

		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// Code to get complaince checkList..

			reqDTO.setProposalChecklist(proposalCheckList);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(true);
			EvaluationResDTO evalResDTO = evaluationSO
					.retrieveComplainceCheckListsForSupplier(reqDTO);
			this.getManagedBean().setEvaluationComplianceList(
					evalResDTO.getComplainceList());

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setScQtChecklist(
						proposalCheckList.getScQtCheckList());
				this.getManagedBean().setQtProposalChecklist(proposalCheckList);
				reqDTO.setProposalChecklist(proposalCheckList);
				resDTO = evaluationSO
						.retrieveSpecificationDetailsByProposal(reqDTO);

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
				String docDefinationKey = proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey();
				this.getManagedBean().setDocDefinitionKey(docDefinationKey);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());
				// this.getManagedBean().setNoOfContractsProposalScore(null);

				if (QTConstants.FINANCIAL_GOV_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
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
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												finGovContract);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 12) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(12);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					detailBackingBean.getManagedBean().setQt(
							this.getManagedBean().getScQt());
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());
					proposalPage = detailBackingBean
							.retrieveFinCompanyExpDtls(proposalCheckList);
					evaluationPage = "finGovCompanyExperience.xhtml";

				} else if (QTConstants.FINANCIAL_PRIVATE_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);

					List<ScQtCheckListScore> finGovContract = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 17) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								finGovContract.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												finGovContract);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 17) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(17);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveFinNonGovCompanyExpDtls(proposalCheckList);
					evaluationPage = "finPrivateCompanyExperience.xhtml";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(docDefinationKey)) {
					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);

					List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 50) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								bankStatement.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												bankStatement);
							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 50) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(50);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveBankStmtDtls(proposalCheckList);
					evaluationPage = "bankStatement.xhtml";
				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(docDefinationKey)) {
					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);

					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());
					List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 51) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								creditFacility.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												creditFacility);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 51) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(51);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveCreditFacilitiesDtls(proposalCheckList);
					evaluationPage = "creditFacility.xhtml";
				} else if (QTConstants.FINANCIAL_CASH_ON_HAND_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);

					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());

					List<ScQtCheckListScore> cashOhHand = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {

							if (checklistScore.getCriteriaType() == 52) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								cashOhHand.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												cashOhHand);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 52) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(52);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveCashOnHandDtls(proposalCheckList);

					evaluationPage = "cashOnHand.xhtml";

				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());
					List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 60) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								assetLiability.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												assetLiability);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 60) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(60);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					proposalPage = detailBackingBean
							.retrieveAssetLiabilityDtls(proposalCheckList);
					evaluationPage = "assetsLiabilities.xhtml";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);

					List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 70) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								paidUpCapital.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												paidUpCapital);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 70) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					} else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(70);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());
					detailBackingBean.getManagedBean().setSmSupplier(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier());
					proposalPage = detailBackingBean
							.retrievePaidUpCapitalDtls(proposalCheckList);

					evaluationPage = "paidUpCapital.xhtml";
				}

				else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(docDefinationKey)) {
					this.getManagedBean().setScQtChecklist(
							proposalCheckList.getScQtCheckList());
					this.getManagedBean().setQtProposalChecklist(
							proposalCheckList);

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 30) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								paidUpCapital.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												paidUpCapital);

							}

						}
					}
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == 30) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}

						}

					}

					else {
						ScQtProposalScore proposalScore = new ScQtProposalScore();
						proposalScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalScore.setCriteriaType(30);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());

					}
					this.getManagedBean()
							.getQtProposalChecklist()
							.setProposalScore(
									this.getManagedBean()
											.getTechGovProposalScore());
					this.getManagedBean().setStatusHide(true);
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());

					detailBackingBean.getManagedBean().setSmSupplier(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier());
					proposalPage = detailBackingBean
							.retrieveSupplierDtls(proposalCheckList);

					evaluationPage = "/xhtml/sc/evaluation/supplierInfo.xhtml";
					// return "supplierInfo.xhtml";
				}
			}
		} finally {

		}
		this.getManagedBean().setProposalPage(proposalPage);
		this.getManagedBean().setEvaluationPage(evaluationPage);

		return "financialotherchecklists.xhtml";
	}

	public void retrivePresentationDemo() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setPresentationDemoActiveIndex(0);
			this.getManagedBean().setDemo(
					this.getManagedBean().getScQt().isDemoRequired());
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
			resDTO = evaluationSo.preparePresentationDemoDtls(reqDTO);

			if (null != resDTO.getDemoCheckLists()
					&& !resDTO.getDemoCheckLists().isEmpty()) {
				this.getManagedBean().setPresentationDemoSupplierList(
						resDTO.getDemoCheckLists());

			}

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
			evalReqDTO.setRecomandationType(3);
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

			&& !evalResDTO.getQulifiedSuppliers().getResultList().isEmpty()) {

				this.getManagedBean().setEvaluationSummaryQualSuppliers(
						evalResDTO.getQulifiedSuppliers().getResultList());
				this.getManagedBean().setEvaTotQualifiedSuppliers(
						evalResDTO.getQulifiedSuppliers().getTotalRecord());
			}
			if (null != evalResDTO.getDisQulifiedSuppliers()

			&& !evalResDTO.getDisQulifiedSuppliers().getResultList().isEmpty()) {
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

			}
			if (outputTab == 3) {

				retrivePresentationDemo();

			}
			if (outputTab == 4) {

				retrieveReportTabDtl();
			}
			if (outputTab == 5) {
				retriveCommitteeEndorsment();
			}
			if (outputTab == 6) {

			}
			if (outputTab == 0) {
				retriveMeetingDtls(this.getManagedBean().getScQt().getQtId(),
						this.getManagedBean().getCommitteeType(), 0);
			}

			if (getManagedBean().getTempTab() < outputTab) {
				getManagedBean().setTempTab(outputTab);
			}
			getManagedBean().setTabActiveIndex(outputTab);
		} finally {

		}
	}

	/*
	 * private void retrieveReportTabDtl() {
	 * debugLogger("retrieveReportTabDtl"); retrieveReportHeader();
	 * IQTEvaluationSO evaluationSO; EvaluationResDTO resDTO = null; try {
	 * evaluationSO = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
	 * EvaluationReqDTO reqDTO = new EvaluationReqDTO();
	 * reqDTO.setQtId(getManagedBean().getScQt().getQtId());
	 * reqDTO.setCommType(getManagedBean().getCommitteeType());
	 * List<ScQtEvaluationReport> scQtEvaluationReports = new
	 * ArrayList<ScQtEvaluationReport>(); if (null ==
	 * this.getManagedBean().getScQtEvaluationReportList()) { resDTO =
	 * evaluationSO.retriveReportDetails(reqDTO); if (resDTO.getReports() !=
	 * null && !resDTO.getReports().isEmpty()) {
	 * this.getManagedBean().setScQtEvaluationReportList( resDTO.getReports());
	 * } else if (resDTO.getPmDocuments() != null &&
	 * resDTO.getPmDocuments().size() > 0) { for (PmDocument pmDoc :
	 * resDTO.getPmDocuments()) { ScQtEvaluationReport evaluationReport = new
	 * ScQtEvaluationReport(); evaluationReport.setScQt(this.getManagedBean()
	 * .getScQt()); evaluationReport.setDocCategory(pmDoc
	 * .getDocCategoryCode().getParameterCode());
	 * evaluationReport.setPmDocument(pmDoc);
	 * evaluationReport.setTitle(pmDoc.getDocName());
	 * evaluationReport.setDefinationType(pmDoc .getDocDefinitionTypeCode());
	 * scQtEvaluationReports.add(evaluationReport); }
	 * this.getManagedBean().setScQtEvaluationReportList(scQtEvaluationReports);
	 * saveQtReportDtls(); } } } finally { evaluationSO = null; } }
	 */

	private void retrieveReportTabDtl() {
		debugLogger("retrieveReportTabDtl");
		retrieveReportHeader();
		IQTEvaluationSO evaluationSO;
		EvaluationResDTO resDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setCommType(getManagedBean().getCommitteeType());

			if (null == this.getManagedBean().getScQtEvaluationReportList()) {
				resDTO = evaluationSO.retriveReportDetails(reqDTO);
				if (null != resDTO.getReports()
						&& !resDTO.getReports().isEmpty()) {
					this.getManagedBean().setScQtEvaluationReportList(
							resDTO.getReports());
				}

			}
		} finally {
			evaluationSO = null;
		}
	}

	private void retrieveReportHeader() {
		debugLogger("retrieveReportHeader");
		String reportHeader = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			reportHeader = "Evaluation Report";
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			reportHeader = "Opening Report";
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			reportHeader = "Technical Evaluation Report";
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			reportHeader = "Financial Evaluation Report";
		}
		getManagedBean().setReportHeader(reportHeader);
	}

	public void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

			reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);

			this.getManagedBean().setEndorscommitteeMemberList(
					resDTO.getCommitteActivities());

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
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
		try {
			EvaluationReqDTO reqDtO = evaluationPreparation();

			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long qtId = this.getManagedBean().getScQt().getQtId();
			int index = this.getManagedBean().getPrepareActiveIndex();
			int tabindex = this.getManagedBean().getTabActiveIndex();
			String committeeType = this.getManagedBean().getCommitteeType();
			List<ScQtProposalChecklist> demoList = this.getManagedBean()
					.getPresentationDemoSupplierList();
			if (!this.getManagedBean().isDemo()) {
				evaluationSO.saveQTDtl(reqDtO);
				renewSesionObject();
			} else {
				saveTechnicalCompliance();
				renewSesionObject();
				retriveMeetingDtls(qtId, committeeType, index);
				retrivePresentationDemo();
			}
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setTabActiveIndex(tabindex);
			retriveMeetingDtls(qtId, committeeType, index);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully", ""));
		} finally {

		}

	}

	public EvaluationReqDTO evaluationPreparation() {
		EvaluationReqDTO reqDtO = new EvaluationReqDTO();
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

			List<ScQtMeeting> meetingRemoveList = this.getManagedBean()
					.getMeetingRemoveList();
			if (null != meetingRemoveList && !meetingRemoveList.isEmpty()) {
				for (int i = 0; i < meetingRemoveList.size(); i++) {
					if (null == meetingRemoveList.get(i).getQtMeetingId()) {
						meetingRemoveList.remove(i);
					}
				}

			}
			reqDtO.setRemovedmeetingList(meetingRemoveList);
			List<ScQtMeetingAttendance> meetingAttendanceRemoveList = this
					.getManagedBean().getMeetingAttendanceRemoveList();
			if (null != meetingAttendanceRemoveList
					&& !meetingAttendanceRemoveList.isEmpty()) {
				for (int i = 0; i < meetingAttendanceRemoveList.size(); i++) {
					if (null == meetingAttendanceRemoveList.get(i)
							.getQtMeetingAttendanceId()) {
						meetingAttendanceRemoveList.remove(i);
					}
				}

			}
			reqDtO.setRemovedmeetingAttendanceList(meetingAttendanceRemoveList);
			List<ScQtEvaluationReport> evaluationReport = this.getManagedBean()
					.getScQtEvaluationReportList();
			if (null != evaluationReport && !evaluationReport.isEmpty()) {
				reqDtO.setEvaluationReportList(evaluationReport);
			}

			List<ScQtProposalChecklist> presentationDemoList = this
					.getManagedBean().getPresentationDemoSupplierList();
			if (null != presentationDemoList && !presentationDemoList.isEmpty()) {
				reqDtO.setDemoList(presentationDemoList);
			}

			List<ScQtCommitteeActivity> committeActivities = this
					.getManagedBean().getEndorscommitteeMemberList();
			if (null != committeActivities) {
				PmUser pmUser = new FacesScopes().getUserSessionBean();
				for (int i = 0; i < committeActivities.size(); i++) {
					if (pmUser.getIdentificationNo().equals(
							committeActivities.get(i).getScCommitteeMember()
									.getPmUser().getIdentificationNo())) {
						committeActivities.get(i).setSignedOff(true);
						break;
					}

				}
				qtCommittee.setCommitteeActivities(committeActivities);
			}
			reqDtO.setCommitteeMemberEndorsmentList(committeActivities);
			reqDtO.setScQtCommitte(qtCommittee);

		} finally {

		}
		return reqDtO;

	}

	public void saveProposalScoreDtls() {
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
							"Evaluation Preperation Scores Saved Successfully",
							"Evaluation Proposal Scores Saved Successfully"));
		} finally {

		}

	}

	public void deleteMeetingDetails() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			List<ScQtMeeting> selectMeeting = getManagedBean()
					.getMeetingMultiSelect();
			List<ScQtMeeting> removeMeetingList = this.getManagedBean()
					.getMeetingRemoveList();
			if (null == removeMeetingList) {
				removeMeetingList = new ArrayList<ScQtMeeting>();
			}
			removeMeetingList.addAll(selectMeeting);
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeeting meeting : selectMeeting) {
					meeting.setDeleted(true);

					meetingList.remove(meeting);
				}

			}
			this.getManagedBean().setMeetingRemoveList(removeMeetingList);

		} finally {

		}
	}

	public void deleteTurutHadirDetails() {
		try {
			List<ScQtMeetingAttendance> meetingList = getManagedBean()
					.getMeetingAttendanceList();
			List<ScQtMeetingAttendance> selectMeeting = getManagedBean()
					.getMeetingAttendanceSelectList();
			List<ScQtMeetingAttendance> removeMeetingAttendanceList = this
					.getManagedBean().getMeetingAttendanceRemoveList();
			if (null == removeMeetingAttendanceList) {
				removeMeetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			}
			removeMeetingAttendanceList.addAll(selectMeeting);
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeetingAttendance meeting : selectMeeting) {
					meeting.setDeleted(true);
					meetingList.remove(meeting);
				}
			}
			this.getManagedBean().setMeetingAttendanceRemoveList(
					removeMeetingAttendanceList);

		} finally {

		}
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtEvaluationReport scQtEvaluationReport = this.getManagedBean()
					.getScQtEvaluationReport();
			scQtEvaluationReport.setScQt(this.getManagedBean().getScQt());
			ScQtEvaluationReportAttachment attachment = scQtEvaluationReport
					.getScQtEvaluationReportAttachment();
			if (null == attachment) {
				attachment = new ScQtEvaluationReportAttachment();
				attachment.setScQtEvaluationReport(scQtEvaluationReport);
				scQtEvaluationReport
						.setScQtEvaluationReportAttachment(attachment);
			} else {
				attachment.setScQtEvaluationReport(scQtEvaluationReport);
				scQtEvaluationReport
						.setScQtEvaluationReportAttachment(attachment);
			}
			attachment.setDocNo(scQtEvaluationReport.getScQt().getQtNo());
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	public void preSentationDemoFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {

			try {

				ScQtProposalChecklist scQtProposalChecklist = this
						.getManagedBean().getQtProposalChecklist();
				ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
						.getScQtProposalChecklistAttachment();
				if (null == attachment) {
					attachment = new ScQtProposalChecklistAttachment();
					attachment.setScQtProposalChecklist(scQtProposalChecklist);
				}
				attachment.setDocNo(getManagedBean().getScQt().getQtNo());
				attachment.setPrefixFileName(scQtProposalChecklist
						.getScQtProposal().getProposalNo());

				uploadAttachment(event.getFile(), attachment);

				scQtProposalChecklist
						.setScQtProposalChecklistAttachment(attachment);

			} finally {

			}
		}

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
							"Evaluation TemplateSpec Saved Successfully."));
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
			EvaluationReqDTO reqDTO = evaluationPreparation();

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
				evaluationDataType.getCommitteeType(), 0);

		if (SCProcessNameConst.FINALISE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		} else if (SCProcessNameConst.FINALISE_OC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_OC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		} else if (SCProcessNameConst.FINALISE_TEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}

		else if (SCProcessNameConst.FINALISE_FEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_FEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		} else if (SCProcessNameConst.ENDORSE_OC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_OC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}

		else if (SCProcessNameConst.ENDORSE_TEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		} else if (SCProcessNameConst.ENDORSE_FEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_FEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}

		else if (SCProcessNameConst.ENDORSE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationstage1view.xhtml");
		}
	}

	public void downloadReport(ScQtEvaluationReport scQtEvaluationReport)
			throws Exception {
		debugLogger("downloadReport");
		ScQtEvaluationReportAttachment scQtEvaluationReportAttachment = scQtEvaluationReport
				.getScQtEvaluationReportAttachment();
		if (scQtEvaluationReportAttachment == null) {
			scQtEvaluationReportAttachment = generateDocumentAttachment(scQtEvaluationReport);
		}
		downloadFile(scQtEvaluationReportAttachment);
	}

	/**
	 * @param scQtEvaluationReport
	 * @return
	 */
	private ScQtEvaluationReportAttachment generateDocumentAttachment(ScQtEvaluationReport scQtEvaluationReport) throws Exception {
		debugLogger("generateDocumentAttachment");
		String definitionType = scQtEvaluationReport.getDefinationType().getParameterCode();
		ScQtEvaluationReportAttachment scQtEvaluationReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			String docDefinitionKey = scQtEvaluationReport.getPmDocument().getDocDefinitionKey();
			scQtEvaluationReportAttachment = new ScQtEvaluationReportAttachment();
			scQtEvaluationReportAttachment.setScQtEvaluationReport(scQtEvaluationReport);
			scQtEvaluationReportAttachment.setDocNo(scQtEvaluationReport.getScQt().getQtNo());
			
			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
				generateCoverPdfFromVelocity((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_MEETING_DTLS.equals(docDefinitionKey)) {
				generateMeetingDetailsPdfFromVelocity((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_COMMITTEE_DTLS.equals(docDefinitionKey)) {
				generateCommitteeDetailsPdfFromVelocity((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_SHORTLISTED.equals(docDefinitionKey)) {
				generatePematuhan1Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_ASSESSMENT_METHOD.equals(docDefinitionKey)) {
				generateKaedah1Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_SHORTLISTED.equals(docDefinitionKey)) {
				generatePematuhan2Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_ASSESSMENT_METHOD.equals(docDefinitionKey)) {
				generateKaedah2Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_TECH_FIN_EVALUATION.equals(docDefinitionKey)) {
				generateAnalisisKeputusanPenilaianTeknikalPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			}
			scQtEvaluationReport.setScQtEvaluationReportAttachment(scQtEvaluationReportAttachment);
		}
		return scQtEvaluationReportAttachment;
	}

	private void generateAnalisisKeputusanPenilaianTeknikalPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateAnalisisKeputusanPenilaianTeknikalPdf");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_ANALYSIS_TECH_FIN_EVALUATION);
		populateAttachmentDetails(attachment);

		long qtId = qt.getQtId();
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPTPH_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_FEC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_TEC_RPT_JSP;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.AMP_QUOTE_ID_EQ, qtId + "");

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	private void generateKaedah2Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateKaedah2Pdf");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_EVALUATION_ASSESSMENT_METHOD);
		populateAttachmentDetails(attachment);

		long qtId = qt.getQtId();
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_TEC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_FEC_RPT_JSP;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.AMP_QUOTE_ID_EQ, qtId + "");

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	private void generatePematuhan2Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generatePematuhan2Pdf");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_EVALUATION_SHORTLISTED);
		populateAttachmentDetails(attachment);

		long qtId = qt.getQtId();
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_TEC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_FEC_RPT_JSP;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.AMP_QUOTE_ID_EQ, qtId + "");

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	private void generateKaedah1Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateKaedah1Pdf");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_COMPLIANCE_ASSESSMENT_METHOD);
		populateAttachmentDetails(attachment);

		long qtId = qt.getQtId();
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPP1_PEMATUHAN_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPPP1_PEMATUHAN_OC_RPT_JSP;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.AMP_QUOTE_ID_EQ, qtId + "");

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	private void generatePematuhan1Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generatePematuhan1Pdf");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_COMPLIANCE_SHORTLISTED);
		populateAttachmentDetails(attachment);

		long qtId = qt.getQtId();
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPP1_PEMATUHAN_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPPP_PEMATUHAN_OC_RPT_JSP;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.AMP_QUOTE_ID_EQ, qtId + "");

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	private void generateCommitteeDetailsPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName(SourcingConstants.DOCNAME_EV_COMMITTEE_DTLS);
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		String committeType = getManagedBean().getCommitteeType();
		List<ScQtCommitteeActivity> committeeActivities = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> committeeActivitiesList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtMeetingAttendance> meetingAttendances = new ArrayList<ScQtMeetingAttendance>();
		List<ScQtMeetingAttendance> meetingAttendancesList = new ArrayList<ScQtMeetingAttendance>();
		ScQtCommittee scQtCommittee = new ScQtCommittee();
		IQTMeetingSO meetingSO = null;
		MeetingResDTO resDTO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setCommitteType(committeType);
			resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			scQtCommittee = resDTO.getQtCommittee();
		} finally {
			meetingSO = null;
		}
		if (null == scQtCommittee) {
			scQtCommittee = new ScQtCommittee();
		}
		if (null != scQtCommittee) {
			if (null != scQtCommittee.getCommitteeActivities()
					&& !scQtCommittee.getCommitteeActivities().isEmpty()) {
				committeeActivitiesList = scQtCommittee
						.getCommitteeActivities();
				for (ScQtCommitteeActivity committeeActivity : committeeActivitiesList) {
					if (committeeActivity.getScCommitteeMember() != null) {
						if (committeeActivity.getScCommitteeMember()
								.getMemberName() == null) {
							committeeActivity.getScCommitteeMember()
									.setMemberName("");
						}
					} else {
						committeeActivity
								.setScCommitteeMember(new ScCommitteeMember());
						committeeActivity.getScCommitteeMember().setMemberName(
								"");
					}
					if (committeeActivity.getScCommitteeMember() != null) {
						if (committeeActivity.getScCommitteeMember()
								.getDesignation() == null) {
							committeeActivity.getScCommitteeMember()
									.setDesignation("");
						}
					} else {
						committeeActivity
								.setScCommitteeMember(new ScCommitteeMember());
						committeeActivity.getScCommitteeMember()
								.setDesignation("");
					}
					if (committeeActivity.getScCommitteeMember() != null) {
						if (committeeActivity.getScCommitteeMember().getGrade() == null) {
							committeeActivity.getScCommitteeMember().setGrade(
									"");
						}
					} else {
						committeeActivity
								.setScCommitteeMember(new ScCommitteeMember());
						committeeActivity.getScCommitteeMember().setGrade("");
					}
					if (committeeActivity.getScCommitteeMember() != null) {
						if (committeeActivity.getScCommitteeMember()
								.getIcPassport() == null) {
							committeeActivity.getScCommitteeMember()
									.setIcPassport("");
						}
					} else {
						committeeActivity
								.setScCommitteeMember(new ScCommitteeMember());
						committeeActivity.getScCommitteeMember().setIcPassport(
								"");
					}
					committeeActivities.add(committeeActivity);
				}
			}
			if (null != scQtCommittee.getMeetingAttendances()
					&& !scQtCommittee.getMeetingAttendances().isEmpty()) {
				meetingAttendances = scQtCommittee.getMeetingAttendances();
				for (ScQtMeetingAttendance attendance : meetingAttendances) {
					if (attendance.getName() == null) {
						attendance.setName("");
					}
					if (attendance.getIcPassport() == null) {
						attendance.setIcPassport("");
					}
					meetingAttendancesList.add(attendance);
				}
			}
		}

		boolean commType = false;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(committeType)
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equals(committeType)
				|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equals(committeType)) {
			commType = true;
			context.put("ec", commType);
			context.put("committeeActivities", committeeActivities);
			context.put("meetingAttendances", meetingAttendancesList);
			context.put("bean", this);
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equals(committeType)) {
			commType = true;
			context.put("oc", commType);
			context.put("committeeActivities", committeeActivities);
			context.put("bean", this);
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_COMMITTEE_DTLS, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private void generateMeetingDetailsPdfFromVelocity(ScAttachment attachment,
			ScQt qt) throws Exception {
		attachment.setFileName(SourcingConstants.DOCNAME_EV_MEETING_DTLS);
		populateAttachmentDetails(attachment);

		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		VelocityContext context = new VelocityContext();

		List<ScQtMeeting> scQtMeetings = new ArrayList<ScQtMeeting>();
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			scQtMeetings = resDTO.getMeetingDtls();
		} finally {
			meetingSO = null;
		}
		for (ScQtMeeting meeting : scQtMeetings) {
			meeting.setMeetDateStr(dateSDF.format(meeting.getMeetingDate()));
			DateFormat formatter = DateFormat.getInstance();
			formatter = DateFormat.getTimeInstance(); // time only
			meeting.setMeetTimeStr(formatter.format(meeting.getMeetingDate()));
		}
		context.put("scQtMeetings", scQtMeetings);

		boolean commType = false;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("ec", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("oc", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("tec", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("fec", commType);
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_MEETING_DTLS, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private void generateCoverPdfFromVelocity(ScAttachment attachment, ScQt qt)
			throws Exception {
		attachment.setFileName(SourcingConstants.DOCNAME_EV_COVER);
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		boolean commType = false;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("ec", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("oc", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("tec", commType);
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("fec", commType);
		}
		context.put("title", qt.getQtTitle());
		context.put("fileNo", qt.getQtNo());
		context.put("ptjName", qt.getPmOrgProfile().getParentOrgProfile()
				.getOrgName());

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_COVER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	public void downloadAllReports() throws Exception {
		debugLogger("downloadAllReports");
		List<ScQtEvaluationReport> evaluationReports = getManagedBean()
				.getScQtEvaluationReportList();
		List<ScQtEvaluationReportAttachment> attachments = new ArrayList<ScQtEvaluationReportAttachment>();
		for (ScQtEvaluationReport scQtEvaluationReport : evaluationReports) {
			ScQtEvaluationReportAttachment reportAttachment = scQtEvaluationReport
					.getScQtEvaluationReportAttachment();
			if (reportAttachment == null) {
				reportAttachment = generateDocumentAttachment(scQtEvaluationReport);
			}
			attachments.add(reportAttachment);
		}
		downloadAllFiles("NGeP-QT-Evaluation-Report", attachments);
	}

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtEvaluationReportAttachment attachment) {
		debugLogger("determineIcon");
		String imageFileName = "icon_file_default.png";
		String ext = "";
		if (attachment != null) {
			String fileName = attachment.getFileName();
			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}
			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getScQtEvaluationReport()
				.setScQtEvaluationReportAttachment(null);
	}

	public String showTemplateAction(ScQtEvaluationReport scQtEvaluationReport)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		debugLogger("showTemplateAction");
		String templateDocUrl = null;
		try {
			TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
			tplDocBB.getManagedBean().setExternalPageUrl(DOCUMENT_PAGE);

			this.getManagedBean().setScQtEvaluationReport(scQtEvaluationReport);

			ScQtEvaluationReport qtEvaluationReport = retriveRulesAndRegulationReport(scQtEvaluationReport);
			if (qtEvaluationReport != null) {
				scQtEvaluationReport = qtEvaluationReport;
			} else {
				scQtEvaluationReport = saveScQtReportList(scQtEvaluationReport);
			}

			TplDocMaster docMaster = scQtEvaluationReport.getTplDocMaster();
			if (docMaster == null) {
				// clone standard tplDocMaster
				TplDocMaster tplDocMaster = cloneTplDocMasterDtls(scQtEvaluationReport);
				scQtEvaluationReport.setTplDocMaster(tplDocMaster);

				// save clone to db
				docMaster = saveClonedTplDocMaster(scQtEvaluationReport);
			}
			templateDocUrl = tplDocBB.goToMaint(docMaster);
		} finally {
		}
		return templateDocUrl;
	}

	private TplDocMaster saveClonedTplDocMaster(
			ScQtEvaluationReport scQtEvaluationReport) {
		IQTTemplateSO qtTemplateSO = null;
		try {
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
			reqDTO.setScQtEvaluationReport(scQtEvaluationReport);

			QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtEvalReports(reqDTO);
			getManagedBean().setScQtEvaluationReport(
					resDTO.getScQtEvaluationReport());

			TplDocMaster tplDocMaster = getManagedBean()
					.getScQtEvaluationReport().getTplDocMaster();
			return tplDocMaster;
		} finally {
			qtTemplateSO = null;
		}
	}

	private TplDocMaster cloneTplDocMasterDtls(
			ScQtEvaluationReport scQtEvaluationReport)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		ScQt scQt = scQtEvaluationReport.getScQt();
		Long tplMasterId = retrieveStandardTplDocMaster(scQt).getDocMasterId();
		TplDocMaster templateDocMaster = retrieveTplDocMasterById(tplMasterId);

		// clone TplDocMaster Details
		TplDocMaster tplDocMaster = (TplDocMaster) templateDocMaster
				.cloneEntity();
		tplDocMaster.setDocMasterId(null);
		tplDocMaster.setOrgProfile(null);
		tplDocMaster.setTplDocSectionCollection(null);
		tplDocMaster.setDocTitle(scQtEvaluationReport.getTitle());
		tplDocMaster.setDocName(scQtEvaluationReport.getTitle());

		setUserDetails(tplDocMaster);

		// clone tplDocSection Details
		tplDocMaster.setTplDocSections(new ArrayList<TplDocSection>());
		List<TplDocSection> docSections = tplDocMaster.getTplDocSections();
		for (TplDocSection tplDocSection : docSections) {
			TplDocSection section = (TplDocSection) tplDocSection.cloneEntity();
			section.setDocSectionId(null);
			section.setTplDocMaster(tplDocMaster);

			setUserDetails(section);
			tplDocMaster.getTplDocSections().add(section);
		}

		// Procurement Category
		PmParameter procurementCategory = scQtEvaluationReport.getScQt()
				.getPmProcurementCategory();
		tplDocMaster.setProcurementCategory(procurementCategory);

		// Process Type
		PmParameter processType = getPmParameter(
				ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
				ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
		tplDocMaster.setProcessType(processType);

		// Template Type
		PmParameter templateType = getPmParameter(
				ParameterTypeConst.TEMPLATE_TYPE,
				ParameterConst.TEMPLATE_TYPE_MINISTRY);
		tplDocMaster.setTemplateType(templateType);

		// Status
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.DOC_TEMPLATE_DEFAULT);
		tplDocMaster.setPmStatus(status);

		tplDocMaster.setScReport(scQtEvaluationReport);

		return tplDocMaster;
	}

	private TplDocMaster retrieveTplDocMasterById(Long masterId) {
		IQTTemplateSO qtTemplateSO = null;
		try {
			qtTemplateSO = (IQTTemplateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTTemplateSO.class);

			QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
			reqDTO.setMasterId(masterId);

			QTTemplateDocResDTO resDTO = qtTemplateSO.getTemplateMaster(reqDTO);
			TplDocMaster docMaster = resDTO.getTplDocMaster();

			return docMaster;
		} finally {
			qtTemplateSO = null;
		}
	}

	private TplDocMaster retrieveStandardTplDocMaster(ScQt scQt) {
		ITemplateDocSO templateDocSO = null;
		try {
			templateDocSO = (ITemplateDocSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();

			reqDTO.setCategoryId(scQt.getPmProcurementCategory()
					.getParameterId());
			PmParameter processType = getPmParameter(
					ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
					ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
			reqDTO.setProcessTypeId(processType.getParameterId());
			reqDTO.setTitle(SourcingConstants.DOCTITLE_QT);

			TemplateDocResDTO resDTO = templateDocSO
					.getTemplateMasterList(reqDTO);
			List<TplDocMaster> docMasterList = resDTO.getDocMasterList();

			TplDocMaster docMaster = null;
			if (docMasterList != null && docMasterList.size() > 0) {
				docMaster = docMasterList.get(0);
				return docMaster;
			} else {
				throw new EpException(
						"Please create Standard Document Template");
			}
		} finally {
			templateDocSO = null;
		}
	}

	// save to scQtEvaluationReport
	private ScQtEvaluationReport saveScQtReportList(
			ScQtEvaluationReport scQtEvaluationReport) {
		debugLogger("ScQtEvaluationReport");

		if (scQtEvaluationReport.getQtReportId() == null) {
			saveQtReportDtls();
			ScQtEvaluationReport qtEvaluationReport = retriveRulesAndRegulationReport(scQtEvaluationReport);
			return qtEvaluationReport;
		} else {
			return null;
		}
	}

	public void saveQtReportDtls() {
		IQTEvaluationReportSO evaluationReportSO = null;
		try {
			evaluationReportSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationReportSO.class);

			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();

			List<ScQtEvaluationReport> evaluationReportList = null;
			List<ScQtEvaluationReport> docReportsList = getManagedBean()
					.getScQtEvaluationReportList();
			if (docReportsList != null && !docReportsList.isEmpty()) {
				evaluationReportList = new ArrayList<ScQtEvaluationReport>();
				evaluationReportList.addAll(docReportsList);
			}

			EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
			// reqDTO.setScQtEvaluationReportList(evaluationReportList);
			evaluationReportSO.saveQTDtl(reqDTO);

			renewSesionObject();
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(outComes);
			retrieveReportTabDtl();

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation is successfully saved", ""));
		} finally {
			evaluationReportSO = null;
		}
	}

	// retrieve the scQtEvaluationReport for Maklumat Tender/Sebut Harga/
	// Asas-Asas Penilaian / Perakuan
	private ScQtEvaluationReport retriveRulesAndRegulationReport(
			ScQtEvaluationReport scQtEvaluationReport) {
		debugLogger("retriveRulesAndRegulationReport");
		IQTDetailSO qTDetailSO = null;
		try {
			qTDetailSO = (IQTDetailSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setScQtEvaluationReport(scQtEvaluationReport);
			reqDTO.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_EVALUATION_REPORT);
			reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_QT_INFO);
			reqDTO.setScQtEvaluationReport(scQtEvaluationReport);

			QuationTenderResDTO resDTO = qTDetailSO
					.retriveScQtReportByDefKey(reqDTO);
			ScQtEvaluationReport qtEvaluationReport = resDTO
					.getScQtEvaluationReport();
			return qtEvaluationReport;
		} finally {
			qTDetailSO = null;
		}
	}

	private PmParameter getPmParameter(String parameterType,
			String parameterCode) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(parameterType);
		reqDTO.setParameterCode(parameterCode);

		ParameterResDTO resDTO = getParameterSO()
				.retrieveParameterByTypeAndCode(reqDTO);
		return resDTO.getPmParameter();
	}

	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
}

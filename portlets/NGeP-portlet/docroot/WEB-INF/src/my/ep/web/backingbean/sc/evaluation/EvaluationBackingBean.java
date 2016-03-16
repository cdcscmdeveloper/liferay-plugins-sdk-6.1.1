/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
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
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationReportSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
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
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScEvaluationCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScFinancialEvaluationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScOpenCommitteeReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinancialCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtOpeningCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtTechnicalCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScTechnicalEvaluationReportAttachment;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecEstimation;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.sc.EvaluationReport;
import my.ep.util.MessageUtils;
import my.ep.util.encrypt.BouncyCastleProvider_AES_CBC;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.evaluation.EvaluationManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import com.liferay.portal.util.PortalUtil;

/**
 * The Class EvaluationBackingBean.
 * 
 * @author Garikapati Mangaiah.
 */
@ManagedBean(name = "evaluationBackingBean")
@RequestScoped
public class EvaluationBackingBean extends
		SourcingBaseBackingBean<EvaluationManagedBean> {

	/** The skip. */
	private boolean skip;

	/** The current tab. */
	private int currentTab;

	/** The output tab. */
	private int outputTab;

	/** The text before sign. */
	private String textBeforeSign;

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/**
	 * Instantiates a new evaluation backing bean.
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	public EvaluationBackingBean() throws ParseException {

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
	 * Inits the.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@PostConstruct
	public void init() throws IOException {
		try {

		} finally {

		}
	}

	/**
	 * Summary change. when you change summary in evaluation stage it will
	 * display all the qualified and disqualified suppliers for package and
	 * preference margin
	 * 
	 * @param event
	 *            (is Mandatory)
	 * 
	 */
	public void recommendationsummaryChange(AjaxBehaviorEvent event) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			String view = (String) event.getComponent().getAttributes()
					.get("value");
			if (null != view && !"".equals(view)) {
				ScQt scQt = getManagedBean().getScQt();

				getManagedBean().setSelectedZone(null);
				getManagedBean().setSinglesummaryItemList(null);
				this.getManagedBean().setFinancialSummaryView("");
				this.getManagedBean().setSummaryViewBy(view);
				EvaluationReqDTO summaryReqDTO = setSummaryPrepareData(view);
				evalResDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
				this.getManagedBean().setEvaluationStageZonesSummary(
						new ArrayList<TplSpecZoneDtl>());
				if (null != evalResDTO.getZones()
						&& !evalResDTO.getZones().isEmpty()) {
					this.getManagedBean().setEvaluationStageZonesSummary(
							evalResDTO.getZones());
					summaryReqDTO.setSpeZoneId(this.getManagedBean()
							.getEvaluationStageZonesSummary().get(0)
							.getSpecZoneId());
					getManagedBean().setSelectedZone(
							this.getManagedBean()
									.getEvaluationStageZonesSummary().get(0));
					EvaluationStageZoneSummary(summaryReqDTO);
				}

				else {
					this.getManagedBean().setSummaryItemList(
							new ArrayList<ScRequestItem>());
					if (null != evalResDTO.getRequestItemResults()
							&& null != evalResDTO.getRequestItemResults()
									.getResultList()
							&& !evalResDTO.getRequestItemResults()
									.getResultList().isEmpty()) {
						this.getManagedBean().setSummaryItemList(
								evalResDTO.getRequestItemResults()
										.getResultList());
					}
				}

				if (SourcingConstants.AWARD_BY_ITEM.equals(summaryReqDTO
						.getAwardBy())) {
					this.getManagedBean().setQualifiedSuppliersDM(
							new SummarySupplierPagingDataModel(
									"evaluationSummaryItemQualifiedSuppliers",
									true, scQt.isZonal(), summaryReqDTO
											.getAwardBy(), summaryReqDTO
											.getResultStages()));
					this.getManagedBean()
							.setDisqualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryItemDisQualifiedSuppliers",
											false, scQt.isZonal(),
											summaryReqDTO.getAwardBy(),
											summaryReqDTO.getResultStages()));

				} else {
					this.getManagedBean()
							.setQualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryPreferencemarginQualifiedSuppliers",
											true, scQt.isZonal(), null,
											summaryReqDTO.getResultStages()));
					this.getManagedBean()
							.setDisqualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryPreferencemarginDisQualifiedSuppliers",
											false, scQt.isZonal(), null,
											summaryReqDTO.getResultStages()));
				}
			}

		} finally {

		}
	}

	public void financialsummaryChange(AjaxBehaviorEvent event) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			String view = (String) event.getComponent().getAttributes()
					.get("value");
			if (null != view && !"".equals(view)) {
				ScQt scQt = getManagedBean().getScQt();

				getManagedBean().setSelectedZone(null);
				getManagedBean().setSinglesummaryItemList(null);
				this.getManagedBean().setFinancialSummaryView(view);
				this.getManagedBean().setSummaryViewBy("");
				EvaluationReqDTO summaryReqDTO = setSummaryPrepareData(view);

				evalResDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
				this.getManagedBean().setEvaluationStageZonesSummary(
						new ArrayList<TplSpecZoneDtl>());
				if (null != evalResDTO.getZones()
						&& !evalResDTO.getZones().isEmpty()) {
					this.getManagedBean().setEvaluationStageZonesSummary(
							evalResDTO.getZones());
					summaryReqDTO.setSpeZoneId(this.getManagedBean()
							.getEvaluationStageZonesSummary().get(0)
							.getSpecZoneId());
					getManagedBean().setSelectedZone(
							this.getManagedBean()
									.getEvaluationStageZonesSummary().get(0));
					EvaluationStageZoneSummary(summaryReqDTO);
				}

				else {
					this.getManagedBean().setSummaryItemList(
							new ArrayList<ScRequestItem>());
					if (null != evalResDTO.getRequestItemResults()
							&& null != evalResDTO.getRequestItemResults()
									.getResultList()
							&& !evalResDTO.getRequestItemResults()
									.getResultList().isEmpty()) {
						this.getManagedBean().setSummaryItemList(
								evalResDTO.getRequestItemResults()
										.getResultList());
					}
				}

				if (SourcingConstants.AWARD_BY_ITEM.equals(summaryReqDTO
						.getAwardBy())) {
					this.getManagedBean().setQualifiedSuppliersDM(
							new SummarySupplierPagingDataModel(
									"evaluationSummaryItemQualifiedSuppliers",
									true, scQt.isZonal(), summaryReqDTO
											.getAwardBy(), summaryReqDTO
											.getResultStages()));
					this.getManagedBean()
							.setDisqualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryItemDisQualifiedSuppliers",
											false, scQt.isZonal(),
											summaryReqDTO.getAwardBy(),
											summaryReqDTO.getResultStages()));

				} else {
					this.getManagedBean()
							.setQualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryPreferencemarginQualifiedSuppliers",
											true, scQt.isZonal(), null,
											summaryReqDTO.getResultStages()));
					this.getManagedBean()
							.setDisqualifiedSuppliersDM(
									new SummarySupplierPagingDataModel(
											"evaluationSummaryPreferencemarginDisQualifiedSuppliers",
											false, scQt.isZonal(), null,
											summaryReqDTO.getResultStages()));
				}

			}

		} finally {

		}

	}

	/**
	 * Sets the prepare data.to get qualified and disqualified suppliers for
	 * package and preference margin.
	 * 
	 * @return the EvaluationReqDTO (Long qtId(Mandatory),boolean zonal(
	 *         Optional),String resultStage(Mandatory),String award(optional))
	 */
	private EvaluationReqDTO setSummaryPrepareData(String view) {
		this.getManagedBean().setPassingScore(0);
		EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
		List<String> resultStg = new ArrayList<String>();
		String award = null;
		EvaluationResDTO resDTO = null;

		if (null != view && !"".equals(view)) {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			if (QTConstants.FINANCIAL_EVALUATION_BY_ITEM.equalsIgnoreCase(view)) {

				resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg);
				if (!this.getManagedBean().getScQt().isZonal()) {
					award = SourcingConstants.AWARD_BY_ITEM;
				}
				// this.getManagedBean().setSummaryZoneBean(null);

				reqDTO.setSettingType(getSettingType(QTConstants.FINANCIAL_EVALUATION_BY_ITEM));
				resDTO = evaluationSO.retrievePassingScoreByQT(reqDTO);
			} else if (QTConstants.FINANCIAL_PREFERENCE_MARGIN
					.equalsIgnoreCase(view)) {

				resultStg.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);

				summaryReqDTO.setResultStages(resultStg);
				// this.getManagedBean().setSummaryZoneBean(null);

				reqDTO.setSettingType(getSettingType(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE));
				resDTO = evaluationSO.retrievePassingScoreByQT(reqDTO);
			}

			else if (QTConstants.RECOMMENDATION_BY_ITEM.equalsIgnoreCase(this
					.getManagedBean().getSummaryViewBy())) {
				resultStg.add(QTConstants.RECOMMENDATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg);
				if (!this.getManagedBean().getScQt().isZonal()) {
					award = SourcingConstants.AWARD_BY_ITEM;
				}
				// this.getManagedBean().setSummaryZoneBean(null);

				reqDTO.setSettingType(getSettingType(QTConstants.RECOMMENDATION_BY_ITEM));
				resDTO = evaluationSO.retrievePassingScoreByQT(reqDTO);
			} else if (QTConstants.RECOMMENDATION_PREFERENCE_MARGIN
					.equalsIgnoreCase(this.getManagedBean().getSummaryViewBy())) {

				resultStg.add(QTConstants.RECOMMENDATION_BY_PACKAGE);

				summaryReqDTO.setResultStages(resultStg);
				// this.getManagedBean().setSummaryZoneBean(null);

				reqDTO.setSettingType(getSettingType(QTConstants.RECOMMENDATION_BY_PACKAGE));
				resDTO = evaluationSO.retrievePassingScoreByQT(reqDTO);
			}
		}

		this.getManagedBean().setPassingScore(resDTO.getPassingScore());
		summaryReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		summaryReqDTO.setZonal(this.getManagedBean().getScQt().isZonal());
		summaryReqDTO.setResultStages(resultStg);
		summaryReqDTO.setAwardBy(award);

		return summaryReqDTO;
	}

	private int getSettingType(String recommendBy) {
		// ScQt scQt = this.getManagedBean().getScQt();

		// scQt.isTechEvaluationReq()

		// if (scQt.getEvaluationType().equals
		// (QTConstants.EVALUATION_ONE_TIER)) {
		//
		// }

		if (StringUtils.equals(recommendBy, QTConstants.TECHNICAL_EVALUATION)) {
			return QTConstants.SETTING_TYPE_TECHNICAL;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE)) {
			return QTConstants.SETTING_TYPE_FINANCIAL_BY_PACKAGE;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.PRESENTATION_DEMO_EVALUATION)) {
			return QTConstants.SETTING_TYPE_DEMO;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.RECOMMENDATION_BY_PACKAGE)) {
			return QTConstants.SETTING_TYPE_RECOMMENDATION_BY_PACKAGE;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.FINANCIAL_EVALUATION_BY_ITEM)) {
			return QTConstants.SETTING_TYPE_FINANCIAL_BY_ITEM;
		} else {
			return QTConstants.SETTING_TYPE_RECOMMENDATION_BY_ITEM;
		}
	}

	/**
	 * Evaluation stage zone summary.to get all the qualified and disqualified
	 * suppliers based on zonal
	 * 
	 * @param EvaluationReqDTO
	 *            (Long qtId (Mandatory),boolean zonal( Mandatory),String
	 *            resultStage( Mandatory),String award(optional))
	 */
	private void EvaluationStageZoneSummary(EvaluationReqDTO summaryReqDTO) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (QTConstants.RECOMMENDATION_BY_ITEM.equalsIgnoreCase(this
					.getManagedBean().getSummaryViewBy())
					|| QTConstants.FINANCIAL_EVALUATION_BY_ITEM
							.equalsIgnoreCase(this.getManagedBean()
									.getFinancialSummaryView())) {
				summaryReqDTO.setZonal(false);
				String award = SourcingConstants.AWARD_BY_ITEM;
				summaryReqDTO.setAwardBy(award);
				evalResDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
				this.getManagedBean().setSummaryItemList(null);
				if (null != evalResDTO.getRequestItemResults()
						&& null != evalResDTO.getRequestItemResults()
								.getResultList()
						&& !evalResDTO.getRequestItemResults().getResultList()
								.isEmpty()) {
					this.getManagedBean().setSummaryItemList(
							evalResDTO.getRequestItemResults().getResultList());

				}
			}
		} finally {

		}
	}

	/**
	 * Evaluation Recommendation zonessummary tab change.when you change zones
	 * in evaluation stage summary based on zonal to get all qualified and
	 * disqualified suppliers for package and preference margin
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void evaluationRecommendationZonessummaryTabChange(
			TabChangeEvent event) {

		try {
			this.getManagedBean().setFinancialSummaryView("");

			TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) event.getData();
			getManagedBean().setSelectedZone(specZoneDtl);
			getManagedBean().setSinglesummaryItemList(null);

			for (int i = 0; i < this.getManagedBean()
					.getEvaluationStageZonesSummary().size(); i++) {
				TplSpecZoneDtl zoneDtls = this.getManagedBean()
						.getEvaluationStageZonesSummary().get(i);
				if (zoneDtls.getSpecZoneId() == specZoneDtl.getSpecZoneId()) {
					EvaluationReqDTO summaryReqDTO = setSummaryPrepareData(this
							.getManagedBean().getSummaryViewBy());
					summaryReqDTO.setSpeZoneId(specZoneDtl.getSpecZoneId());
					EvaluationStageZoneSummary(summaryReqDTO);

				}
			}

			this.getManagedBean().getQualifiedSuppliersDM().reloadData();
			this.getManagedBean().getDisqualifiedSuppliersDM().reloadData();
		} finally {

		}
	}

	/**
	 * Evaluation zonessummary tab change.when you change zones in evaluation
	 * stage Financial summary based on zonal to get all qualified and
	 * disqualified suppliers for package and preference margin
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void evaluationZonesFinancialsummaryTabChange(TabChangeEvent event) {

		try {
			this.getManagedBean().setSummaryViewBy("");
			TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) event.getData();
			getManagedBean().setSelectedZone(specZoneDtl);
			getManagedBean().setSinglesummaryItemList(null);

			for (int i = 0; i < this.getManagedBean()
					.getEvaluationStageZonesSummary().size(); i++) {
				TplSpecZoneDtl zoneDtls = this.getManagedBean()
						.getEvaluationStageZonesSummary().get(i);
				if (zoneDtls.getSpecZoneId() == specZoneDtl.getSpecZoneId()) {
					EvaluationReqDTO summaryReqDTO = setSummaryPrepareData(this
							.getManagedBean().getFinancialSummaryView());
					summaryReqDTO.setSpeZoneId(specZoneDtl.getSpecZoneId());
					EvaluationStageZoneSummary(summaryReqDTO);

				}
			}

			this.getManagedBean().getQualifiedSuppliersDM().reloadData();
			this.getManagedBean().getDisqualifiedSuppliersDM().reloadData();
		} finally {

		}
	}

	/**
	 * Summary item select.when you select item in evaluation stage summary
	 * based on item you will get all qualified and disqualified suppliers for
	 * that item.
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void recommendationSummaryItemSelect(SelectEvent event) {
		this.getManagedBean().getQualifiedSuppliersDM().reloadData();
		this.getManagedBean().getDisqualifiedSuppliersDM().reloadData();
	}

	public void retrieveGeneralInfo() {
		this.getManagedBean().setPrepareActiveIndex(0);
	}

	/**
	 * Retrieve meeting dtls.to retrieve all the meeting details information for
	 * preparation tab
	 * 
	 * @param qtId
	 *            (Mandatory)
	 * @param commType
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 */
	public void retriveMeetingDtls(Long qtId, String commType, int index) {
		IQTMeetingSO meetingSO;
		IQTEvaluationSO evaluationSo = null;

		try {
			List<ScQtMeeting> meetingRemoveList = this.getManagedBean()
					.getMeetingRemoveList();
			if (null == meetingRemoveList) {
				meetingRemoveList = new ArrayList<ScQtMeeting>();
			}

			this.getManagedBean().setCommitteeType(commType);
			this.getManagedBean().setPrepareActiveIndex(index);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			EvaluationReqDTO qtReqDTO = new EvaluationReqDTO();
			EvaluationResDTO qtResDTO = new EvaluationResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			if (null == this.getManagedBean().getScQt()) {
				qtReqDTO.setQtId(qtId);
				qtResDTO = evaluationSo.retriveQtDtlsById(qtReqDTO);
				this.getManagedBean().setScQt(qtResDTO.getScQt());
			}

			if (null == this.getManagedBean().getScQtMeetings()) {
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

				reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getMeetingDtls() != null
						&& !resDTO.getMeetingDtls().isEmpty()) {

					for (ScQtMeeting scQtMeeting : resDTO.getMeetingDtls()) {
						Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting
								.getMeetingStatus().getStatusId() : null;
						scQtMeeting.setMeetingStatusStr(meetingStatusId);
					}
					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}

				this.getManagedBean().setScQtCommittee(resDTO.getQtCommittee());
				if (null != resDTO.getQtCommittee()) {

					if (null != resDTO.getQtCommittee()
							.getCommitteeActivities()

							&& !resDTO.getQtCommittee()
									.getCommitteeActivities().isEmpty()) {

						for (ScQtCommitteeActivity committeeActivity : resDTO
								.getQtCommittee().getCommitteeActivities()) {
							if (committeeActivity
									.getScCommitteeMember()
									.getMemberRole()
									.getParameterCode()
									.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
								if (!committeeActivity.isAttended()) {
									committeeActivity.setAttended(true);
								}

							}
						}
						Collections.sort(resDTO.getQtCommittee()
								.getCommitteeActivities());
						this.getManagedBean().setCommitteeMemberList(
								resDTO.getQtCommittee()
										.getCommitteeActivities());

					}

					else {

						ScCommittee sccommittee = resDTO.getQtCommittee()
								.getScCommittee();
						List<ScQtCommitteeActivity> localList = new ArrayList<ScQtCommitteeActivity>();
						if (null != sccommittee.getScCommitteeMembers()
								&& !sccommittee.getScCommitteeMembers()
										.isEmpty()) {

							for (ScCommitteeMember scCommitteeMember : sccommittee
									.getScCommitteeMembers()) { //
								scCommitteeMember.setScCommittee(sccommittee); //
								setUserDetails(scCommitteeMember);

								ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
								if (scCommitteeMember
										.getMemberRole()
										.getParameterCode()
										.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
									activity.setAttended(true);

								}
								activity.setScQtCommittee(this.getManagedBean()
										.getScQtCommittee());
								activity.setScCommitteeMember(scCommitteeMember);
								setUserDetails(activity);
								localList.add(activity);
							}

						}

						Collections.sort(localList);
						this.getManagedBean().setCommitteeMemberList(localList);

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
			evaluationSo = null;
		}

	}

	/**
	 * Retrieve opening committee dtls for preparation tab
	 * 
	 * @param index
	 *            (Mandatory)
	 */
	public void retriveOpeningCommitteeDtls(int index) {

		try {
			this.getManagedBean().setPrepareActiveIndex(index);

		} finally {

		}

	}

	/**
	 * Retrieve turut hadir dtls for preparation tab
	 * 
	 * @param index
	 *            (Mandatory)
	 * 
	 */
	public void retriveTurutHadirDtls(int index) {

		try {
			this.getManagedBean().setPrepareActiveIndex(index);
			List<ScQtMeetingAttendance> meetingAttendanceRemoveList = this
					.getManagedBean().getMeetingAttendanceRemoveList();
			if (null == meetingAttendanceRemoveList) {
				meetingAttendanceRemoveList = new ArrayList<ScQtMeetingAttendance>();
			}

		} finally {

		}

	}

	/**
	 * Technical compliance action.when you click compliance button in
	 * compliance stage to get all the proposal submitted suppliers.
	 * 
	 * @param checklist
	 *            (optional)
	 * @param index
	 *            (Mandatory)
	 * @param checkListType
	 *            (Mandatory)
	 * @return navigate to proposal submitted supplier page.
	 */
	public String complianceUploadCheckList(ScQtCheckList checklist, int index,
			String checkListType) {
		ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
		if (null != finalization) {

			if (finalization.isReEvaluateCompReq()) {

				this.getManagedBean().setComplianceReEvaluateRequired(false);
			} else {

				this.getManagedBean().setComplianceReEvaluateRequired(true);
			}
		} else {

			this.getManagedBean().setComplianceReEvaluateRequired(false);
		}
		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setComplianceCheckListIndex(index);
			this.getManagedBean().setCheckListType(checkListType);
			reqDTO.setCheckListId(checklist.getQtChecklistId());
			reqDTO.setCompliance(null);
			/*
			 * resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * new ArrayList<ScQtProposalChecklist>()); if
			 * (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getComplianceResultBean().getResultList());
			 * 
			 * }
			 */
			this.getManagedBean().setComplianceTechnicalProposalCheckListDM(
					new ComplianceSupplierPagingDataModel(
							"complianceTechnicalSupplier", reqDTO
									.getCheckListId(), reqDTO.getCompliance(),
							null, null));
		} finally {

		}

		if (SourcingConstants.TECHINICAL_STAGE.equals(checkListType)) {

			return "/xhtml/sc/evaluation/compliance/technicalComplianceEditSuppliers.xhtml";
		} else {

			return "/xhtml/sc/evaluation/compliance/financialComplianceEditSuppliers.xhtml";
		}

	}

	/**
	 * Sample compliance.when you click compliance button in sample tab to get
	 * all the sample items
	 * 
	 * @param label
	 *            (Mandatory)
	 * @return navigate to sampleItem page.
	 */
	public String complianceSampleItems(ScQtSampleLable label) {

		try {
			ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
			if (null != finalization) {

				if (finalization.isReEvaluateCompReq()) {

					this.getManagedBean()
							.setComplianceReEvaluateRequired(false);
				} else {

					this.getManagedBean().setComplianceReEvaluateRequired(true);
				}
			} else {

				this.getManagedBean().setComplianceReEvaluateRequired(false);
			}
			this.getManagedBean().setItemName(null);
			this.getManagedBean().setSampleItemSpecifications(null);
			this.getManagedBean().setScQtSampleLbl(label);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setLableId(label.getSampleLableId());
			this.getManagedBean().setComplianceSampleItemListDM(
					new ComplianceSampleItemsPagingDataModel("sampleItemId",
							reqDTO.getLableId()));

		} finally {

		}
		return "/xhtml/sc/evaluation/compliance/sampleComplianceEditItems.xhtml";
	}

	/**
	 * Save compliance sample.
	 */
	public void saveComplianceSample() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtSampleItem> sampleCompliance = this.getManagedBean()
					.getComplianceSampleItemListDM().getList();
			ScQtSampleLable sampleLabel = null;
			if (null != sampleCompliance && !sampleCompliance.isEmpty()) {
				for (ScQtSampleItem sampleItem : sampleCompliance) {
					if (null == sampleItem.getSubmissionStatus()
							|| null == sampleItem.getSubmissionStatus()
									.getParameterId()) {
						throw new EpException(MessageConst.ERROR_760363);

					}
				}
				sampleLabel = sampleCompliance.get(0).getScQtSampleLable();
				reqDTO.setSampleComplianceList(sampleCompliance);
			}

			evaluationSo.saveOrUpdateSampleComplianceDtls(reqDTO);
			if (null != sampleLabel) {
				this.getManagedBean()
						.setComplianceSampleItemListDM(
								new ComplianceSampleItemsPagingDataModel(
										"sampleItemId", sampleLabel
												.getSampleLableId()));
			}

			getManagedBean().setComplianceSummaryGenerated(false);
			updateSummaryGeneratedStatus();
			if ((QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType()))
					|| (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType()))) {
				complianceChangeScQtStatusUpdate();
			}

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

	private void updateSummaryGeneratedStatus() {
		boolean technicalSummaryGenerated = getManagedBean()
				.isTechnicalSummaryGenerated();
		boolean presentationDemoSummaryGenerated = getManagedBean()
				.isPresentationDemoSummaryGenerated();
		boolean financialSummaryGenerated = getManagedBean()
				.isFinancialSummaryGenerated();

		if (!getManagedBean().isComplianceSummaryGenerated()) {
			technicalSummaryGenerated = false;
			presentationDemoSummaryGenerated = false;
			financialSummaryGenerated = false;
		} else if (!technicalSummaryGenerated) {
			presentationDemoSummaryGenerated = false;
			financialSummaryGenerated = false;
		} else if (!presentationDemoSummaryGenerated) {
			financialSummaryGenerated = false;
		}

		getManagedBean()
				.setTechnicalSummaryGenerated(technicalSummaryGenerated);
		getManagedBean().setPresentationDemoSummaryGenerated(
				presentationDemoSummaryGenerated);
		getManagedBean()
				.setFinancialSummaryGenerated(financialSummaryGenerated);
		this.getManagedBean().setScQtEvaluationReportList(null);
	}

	/**
	 * Compliance status.
	 * 
	 * @param index
	 *            the index
	 * @param proposalCheckList
	 *            the proposal check list
	 */
	public void complianceStatus(int index,
			List<ScQtProposalChecklist> proposalCheckList) {
		ScQtProposalChecklist scqtProposalCheckList = proposalCheckList
				.get(index);
		this.getManagedBean().getComplianceTechnicalProposalCheckList()
				.set(index, scqtProposalCheckList);
	}

	/**
	 * Save compliance upload checkList.
	 */
	public void saveComplianceUploadCheckList() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationResDTO resDTO = new EvaluationResDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			List<ScQtProposalChecklist> technicalCompliance = (List<ScQtProposalChecklist>) this
					.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();
			reqDTO.setCheckList(this.getManagedBean().getScQtChecklist());

			if (null != technicalCompliance && !technicalCompliance.isEmpty()) {
				for (ScQtProposalChecklist proposalCheckList : technicalCompliance) {
					if (proposalCheckList.getScQtProposal().isSubmitted()) {
						/*
						 * if (null == proposalCheckList.getCompliance()) { //
						 * throw new EpException(MessageConst.ERROR_760204); }
						 */
						if (null != proposalCheckList.getCompliance()
								&& !proposalCheckList.getCompliance()) {
							if (null == proposalCheckList.getRemark()
									|| "".equalsIgnoreCase(proposalCheckList
											.getRemark())) {
								throw new EpException(MessageConst.ERROR_760198);
							}
						}
					}

				}

				reqDTO.setComplianceList(technicalCompliance);
			}

			resDTO = evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			List<ScQtCheckList> qtCheckLists = getManagedBean()
					.getComplianceTechnicalCheckList();
			if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType()
							.equalsIgnoreCase("T")) {

				qtCheckLists = getManagedBean()
						.getComplianceTechnicalCheckList();
			} else if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType()
							.equalsIgnoreCase("F")) {
				qtCheckLists = getManagedBean()
						.getCompliancefinancialCheckList();
			}

			if (qtCheckLists != null) {
				updateCheckListIntoQtCheckLists(qtCheckLists,
						qtCheckLists.indexOf(getManagedBean()
								.getScQtChecklist()), resDTO.getCheckList());
			}

			/*
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getSaveComplianceList());
			 */
			this.getManagedBean().setComplianceTechnicalProposalCheckListDM(
					new ComplianceSupplierPagingDataModel(
							"complianceTechnicalSupplier", resDTO
									.getCheckList().getQtChecklistId(), null,
							null, null));

			getManagedBean().setComplianceSummaryGenerated(false);
			updateSummaryGeneratedStatus();
			if ((QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType()))
					|| (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType()))) {
				complianceChangeScQtStatusUpdate();
			}

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.",
							"Evaluation Compliance saved successfully."));

		} finally {

		}
	}

	private void updateCheckListIntoQtCheckLists(
			List<ScQtCheckList> qtCheckLists, int toBeUpdateChecklistIndex,
			ScQtCheckList toBeUpdateCheckList) {
		this.getManagedBean().setScQtChecklist(toBeUpdateCheckList);

		if (toBeUpdateChecklistIndex >= 0
				&& qtCheckLists.size() > toBeUpdateChecklistIndex) {
			if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType()
							.equalsIgnoreCase("T")) {

				qtCheckLists.remove(toBeUpdateChecklistIndex);
				qtCheckLists.add(toBeUpdateChecklistIndex, toBeUpdateCheckList);
			} else if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType()
							.equalsIgnoreCase("F")) {
				qtCheckLists.remove(toBeUpdateChecklistIndex);
				qtCheckLists.add(toBeUpdateChecklistIndex, toBeUpdateCheckList);
			}
		}
	}

	/**
	 * Save presentation demo dtls.when you click save in presentation demo tab.
	 */
	public void savePresentationDemoDtls() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> presentatiodemo = this.getManagedBean()
					.getPresentationDemoSupplierListDM().getList();

			if (null != presentatiodemo && !presentatiodemo.isEmpty()) {
				PmStatus Status = getPmStatus();
				for (ScQtProposalChecklist demoChecklist : presentatiodemo) {
					demoChecklist.setEvaluationStatus(Status);
				}
				reqDTO.setComplianceList(presentatiodemo);
				evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			}

			this.getManagedBean().setPresentationDemoSupplierListDM(
					new PresentationDemoSuppliersPagingDataModel(
							"demoSupplierId", this.getManagedBean().getScQt()
									.getQtId(),
							QTConstants.TECHNICAL_EVALUATION));

			getManagedBean().setPresentationDemoSummaryGenerated(false);
			updateSummaryGeneratedStatus();

		} finally {

		}

	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param String
	 *            type (Mandatory)
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Evaluation backaction.when you click back button in compliance stage and
	 * evaluation stage.
	 * 
	 * @return navigate to evaluation main page.
	 */
	public String evaluationBackaction() {
		this.getManagedBean().setEvaluationTechnicalFinancialType("");
		this.getManagedBean().setEvaluationCheckListType("");
		this.getManagedBean().setEvaluationCheckListIndex(-1);
		// this.getManagedBean().setCheckListType(checklistType);
		this.getManagedBean().setComplianceCheckListIndex(-1);
		this.getManagedBean().setCheckListType("");

		return "/xhtml/sc/evaluation/evaluationMain.xhtml";

	}

	/**
	 * Technical backaction.
	 * 
	 * @return navigate to compliance qualified supplier technical checklist
	 *         page.
	 */
	public String technicalBackaction() {
		this.getManagedBean().setTechindex(-1);
		return "/xhtml/sc/evaluation/evaluationStage/technical/technicalOtherCheckListSuppliesMain.xhtml";

	}

	/**
	 * Financial backaction.
	 * 
	 * @return navigate to compliance or technical or financial qualified
	 *         supplier financial checklist page.
	 */
	public String financialBackaction() {
		this.getManagedBean().setTechindex(-1);
		return "/xhtml/sc/evaluation/evaluationStage/financial/financialOtherListSuppliersMain.xhtml";

	}

	/**
	 * Technical spec backaction.
	 * 
	 * @return navigate to compliance qualified supplier technical checklist
	 *         page.
	 */
	public String technicalSpecBackaction() {
		this.getManagedBean().setTechindex(-1);
		return "/xhtml/sc/evaluation/evaluationStage/technical/technicalSpecEvaluation.xhtml";

	}

	/**
	 * Financial spec backaction.
	 * 
	 * @return navigate to compliance or technical or financial qualified
	 *         supplier financial Spec page.
	 */
	public String financialSpecBackaction() {
		this.getManagedBean().setTechindex(-1);
		if (this.getManagedBean().getScQt().isZonal()) {
			return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithZone.xhtml";
		} else {
			return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithOutzone.xhtml";
		}

	}

	/**
	 * Retrieve compliance technical.display all the technical checklist in
	 * compliance stage technical tab.
	 * 
	 * 
	 */
	public void retriveComplianceTechnical() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		this.getManagedBean().setComplianceActiveIndex(0);
		IStatusSO statussO = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

			reqDTO.setCompliance(null);

			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
			if (null != resDTO.getCheckListResultBean().getResultList()
					&& !resDTO.getCheckListResultBean().getResultList()
							.isEmpty()) {

				for (ScQtCheckList checkList : resDTO.getCheckListResultBean()
						.getResultList()) {
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(checkList.getDefinationType()
									.getParameterCode())
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(checkList
											.getDefinationType()
											.getParameterCode())
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
									.equalsIgnoreCase(checkList
											.getDefinationType()
											.getParameterCode())) {

						if (null != checkList.getPmEvalStatus()
								&& QTStatusConst.TS_PENDING_SUBMISSION
										.equals(checkList.getPmEvalStatus()
												.getStatusId())) {
							StatusReqDTO statusreq = new StatusReqDTO();
							statusreq.setStatusId(QTStatusConst.TS_COMPLETED);
							StatusResDTO statusres = statussO
									.retrieveStatusByStatusId(statusreq);
							checkList.setPmEvalStatus(statusres.getPmStatus());

						}

					} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
							.equalsIgnoreCase(checkList.getDefinationType()
									.getParameterCode())) {
						if (QTConstants.EVALUATION_TWO_TIER
								.equalsIgnoreCase(this.getManagedBean()
										.getScQt().getEvaluationType())
								&& !this.getManagedBean()
										.getCurrentStatusId()
										.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)) {
							if (null != checkList.getPmEvalStatus()
									&& QTStatusConst.TS_PENDING_SUBMISSION
											.equals(checkList.getPmEvalStatus()
													.getStatusId())) {
								StatusReqDTO statusreq = new StatusReqDTO();
								statusreq
										.setStatusId(QTStatusConst.TS_COMPLETED);
								StatusResDTO statusres = statussO
										.retrieveStatusByStatusId(statusreq);
								checkList.setPmEvalStatus(statusres
										.getPmStatus());

							}

						}

					}

				}

			}

			this.getManagedBean().setComplianceTechnicalCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {
			statussO = null;
			evaluationSO = null;
		}

	}

	/**
	 * Retrieve compliance sample.display all the samples in compliance stage
	 * sample tab.
	 * 
	 * 
	 */
	public void retriveComplianceSample() {

		this.getManagedBean().setComplianceActiveIndex(1);
		try {

			int day = sampleClosingDateCountDays();
			if (day <= 0) {

				this.getManagedBean().setComplianceSampleLblsupplierDM(
						new ComplianceSampleLabelSuppliersPagingDataModel(
								"complianceSample", this.getManagedBean()
										.getScQt().getQtId()));
			}

		} finally {

		}

	}

	/**
	 * Retrieve compliance financial.display all the financial checklist in
	 * compliance stage financial checklist tab
	 * 
	 * 
	 */
	public void retriveComplianceFinancial() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;
		IStatusSO statussO = null;

		try {
			if (this.getManagedBean().getScQt().isSampleReq()) {
				this.getManagedBean().setComplianceActiveIndex(2);
			} else {
				this.getManagedBean().setComplianceActiveIndex(1);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			if (null != resDTO.getCheckListResultBean().getResultList()
					&& !resDTO.getCheckListResultBean().getResultList()
							.isEmpty()) {
				for (ScQtCheckList checkList : resDTO.getCheckListResultBean()
						.getResultList()) {
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(checkList.getDefinationType()
									.getParameterCode())
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(checkList
											.getDefinationType()
											.getParameterCode())) {

						if (null != checkList.getPmEvalStatus()
								&& QTStatusConst.TS_PENDING_SUBMISSION
										.equals(checkList.getPmEvalStatus()
												.getStatusId())) {
							StatusReqDTO statusreq = new StatusReqDTO();
							statusreq.setStatusId(QTStatusConst.TS_COMPLETED);
							StatusResDTO statusres = statussO
									.retrieveStatusByStatusId(statusreq);
							checkList.setPmEvalStatus(statusres.getPmStatus());

						}

					}

				}

			}

			this.getManagedBean().setCompliancefinancialCheckList(
					resDTO.getCheckListResultBean().getResultList());

		} finally {
			evaluationSO = null;
			statussO = null;
		}

	}

	/**
	 * Retrieve compliance summary.display all the qualified and disqualified
	 * suppliers in compliance stage summary tab.
	 * 
	 * 
	 */
	public void retriveComplianceSummary() {
		IQTEvaluationSO evaluationSO = null;

		try {
			if (this.getManagedBean().getScQt().isSampleReq()) {
				this.getManagedBean().setComplianceActiveIndex(3);
				this.getManagedBean().setComplianceOnly(true);
			} else {
				this.getManagedBean().setComplianceActiveIndex(2);
				this.getManagedBean().setComplianceOnly(true);
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
			if (this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)
					|| this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)) {
				ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
				if (null != finalization) {
					if (finalization.isReEvaluateCompReq()) {
						if (!getManagedBean().isComplianceSummaryGenerated()) {
							getManagedBean().setComplianceSummaryGenerated(
									false);
						}
					} else {
						getManagedBean().setComplianceSummaryGenerated(true);
					}
				}
				if (!getManagedBean().isComplianceSummaryGenerated()) {
					evaluationSO.prepareComplianceSummaryResults(reqDTO);

					getManagedBean().setComplianceSummaryGenerated(true);
				}
			}

			List<String> evalstage = new ArrayList<String>();
			evalstage.add(QTConstants.COMPLIANCE_CHECK);

			this.getManagedBean().setQualifiedSuppliersDM(
					new SummarySupplierPagingDataModel(
							"complianceQualifiedSupplier", true, false, null,
							evalstage));
			this.getManagedBean().setDisqualifiedSuppliersDM(
					new SummarySupplierPagingDataModel(
							"complianceDisQualifiedSupplier", false, false,
							null, evalstage));

		} finally {

		}

	}

	/**
	 * Generate evaluation stage.display all the scores for supplies in
	 * evaluation stage
	 */
	private void generateEvaluationStage() {

		IQTEvaluationSO evaluationSO = null;

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
				if (!this.getManagedBean().getScQt().isTechEvaluationReq()) {
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

					reqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					evaluationSO.generateEvaluationSheet(reqDTO);
					reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
					evaluationSO.generateEvaluationSheet(reqDTO);
				} else if (this.getManagedBean().getScQt()
						.isTechEvaluationReq()) {
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

					reqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					evaluationSO.generateEvaluationSheet(reqDTO);

				}

			}
			/*
			 * this.getManagedBean().setEvaluationStageStatusHide(
			 * resDTO.isGenerateSuppliers());
			 */
			this.getManagedBean().setGenerateEvaluationButton(true);

		} finally {
			evaluationSO = null;

		}
	}

	private void generateFinancialEvaluationresults() {
		IQTEvaluationSO evaluationSO = null;

		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
			evaluationSO.generateEvaluationSheet(reqDTO);
		} finally {
			evaluationSO = null;
		}

	}

	/**
	 * Retrieve technical evaluation stage.display all the technical checklist
	 * in evaluation stage technical tab.
	 */
	public void retriveTechnicalEvaluationStage() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();

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

			List<String> evalstage = new ArrayList<String>();
			evalstage.add(QTConstants.COMPLIANCE_CHECK);
			reqDTO.setResultStages(evalstage);

			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
			List<ScQtCheckList> localCheckList = new ArrayList<ScQtCheckList>();
			this.getManagedBean().setCheckQualifiedSuppliers(
					resDTO.isCheckQualifiedSuppliers());
			if (null != resDTO
					&& null != resDTO.getCheckListResultBean().getResultList()
					&& !resDTO.getCheckListResultBean().getResultList()
							.isEmpty()) {

				List<ScQtCheckList> proposalChecklistList = resDTO
						.getCheckListResultBean().getResultList();
				for (ScQtCheckList proposalCheckList : proposalChecklistList) {
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(proposalCheckList
									.getDefinationType().getParameterCode())
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(proposalCheckList
											.getDefinationType()
											.getParameterCode())) {
						localCheckList.add(proposalCheckList);
					} else {
						if (proposalCheckList.isScoringReq()) {
							// localCheckList.add(proposalCheckList);
							localCheckList
									.add(checkStausForUploadNoActionCheckLists(
											proposalCheckList,
											QTConstants.COMPLIANCE_CHECK));
						}
					}

				}

				/*
				 * this.getManagedBean().setEvaluationStgTechnicalCheckList(
				 * resDTO.getCheckListResultBean().getResultList());
				 */
			}

			this.getManagedBean().setEvaluationStgTechnicalCheckList(
					localCheckList);
			checkTechnicalSuppliersQualified();

		} finally {
			evaluationSO = null;
			resDTO = null;

		}
		// return "/xhtml/sc/evaluation/evaluationMain.xhtml";

	}

	private ScQtCheckList checkStausForUploadNoActionCheckLists(
			ScQtCheckList checkList, String evalStage) {
		IQTEvaluationSO evaluationSO = null;
		IStatusSO statussO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(checkList.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(evalStage);
			resDTO = evaluationSO.checkDemoAndUploadChecklistStatus(reqDTO);
			if (resDTO.getPendingCount() > 0) {

				statussO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);
				checkList.setPmEvalStatus(statusres.getPmStatus());

			} else {
				if (null != checkList.getPmEvalStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList
								.getPmEvalStatus().getStatusId())) {
					statussO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStatusSO.class);
					StatusReqDTO statusreq = new StatusReqDTO();
					statusreq.setStatusId(QTStatusConst.TS_COMPLETED);
					StatusResDTO statusres = statussO
							.retrieveStatusByStatusId(statusreq);
					checkList.setPmEvalStatus(statusres.getPmStatus());
				}

			}

		} finally {
			evaluationSO = null;
			statussO = null;
		}
		return checkList;
	}

	/**
	 * Retrieve financial evaluation stage.display all the financial checklist
	 * in evaluation stage financialchecklist tab.
	 */
	public void retriveFinancialEvaluationStage() {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setEvaluatioStageActiveIndex(1);
		try {
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (!this.getManagedBean().getScQt().isTechEvaluationReq()) {
					this.getManagedBean().setEvaluatioStageActiveIndex(1);
				} else if (this.getManagedBean().getScQt()
						.isTechEvaluationReq()) {
					this.getManagedBean().setOneTierFinancialActiveIndex(0);

				}
			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(0);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			List<String> evalstage = new ArrayList<String>();
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (!this.getManagedBean().getScQt().isTechEvaluationReq()) {
					evalstage.add(QTConstants.COMPLIANCE_CHECK);
				} else if (this.getManagedBean().getScQt()
						.isTechEvaluationReq()) {
					evalstage.add(QTConstants.TECHNICAL_EVALUATION);
				}
			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (this.getManagedBean().getScQt().isDemoRequired()) {
					evalstage.add(QTConstants.PRESENTATION_DEMO_EVALUATION);
				} else {
					evalstage.add(QTConstants.TECHNICAL_EVALUATION);
				}

			}
			reqDTO.setResultStages(evalstage);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
			List<ScQtCheckList> localCheckList = new ArrayList<ScQtCheckList>();
			this.getManagedBean().setCheckQualifiedSuppliers(
					resDTO.isCheckQualifiedSuppliers());
			if (null != resDTO
					&& null != resDTO.getCheckListResultBean().getResultList()
					&& !resDTO.getCheckListResultBean().getResultList()
							.isEmpty()) {

				List<ScQtCheckList> proposalChecklistList = resDTO
						.getCheckListResultBean().getResultList();
				for (ScQtCheckList proposalCheckList : proposalChecklistList) {

					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(proposalCheckList
									.getDefinationType().getParameterCode())
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(proposalCheckList
											.getDefinationType()
											.getParameterCode())) {
						localCheckList.add(proposalCheckList);
					} else {
						if (proposalCheckList.isScoringReq()) {
							// localCheckList.add(proposalCheckList);
							localCheckList
									.add(checkStausForUploadNoActionCheckLists(
											proposalCheckList, evalstage.get(0)));
						}
					}

				}

				/*
				 * this.getManagedBean().setEvaluationStgfinancialCheckList(
				 * resDTO.getCheckListResultBean().getResultList());
				 */

			}

			this.getManagedBean().setEvaluationStgfinancialCheckList(
					localCheckList);
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				checkFinancialSuppliersQualified();

			}

		} finally {
			evaluationSO = null;

		}
	}

	/**
	 * Retrieve summary evaluation stage.generate summary for evaluation stage
	 * summary to get all the qualified and disqualified suppliers for package
	 * and preference margin and item.
	 */
	public void retriveSummaryEvaluationStage() {
		this.getManagedBean().setPassingScore(0);
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {

			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (!this.getManagedBean().getScQt().isTechEvaluationReq()) {
					this.getManagedBean().setEvaluatioStageActiveIndex(3);
				} else if (this.getManagedBean().getScQt()
						.isTechEvaluationReq()) {
					this.getManagedBean().setEvaluatioStageActiveIndex(1);
				}

			}

			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				this.getManagedBean().setEvaluatioStageActiveIndex(1);
			}
			this.getManagedBean().setSummaryViewBy("");
			this.getManagedBean().setFinancialSummaryView("");
			int count = this.getManagedBean().getComplianceSummaryCount();
			count++;
			int count2 = count;
			this.getManagedBean().setComplianceSummaryCount(count2);
			// if (this.getManagedBean().getComplianceSummaryCount() == 1) {

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
				evalReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				evalReqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
				evalReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
				// evalReqDTO.setRecomandationType(1);
				if (this.getManagedBean().getScQt().isDemoRequired()) {

					evalReqDTO
							.setRecomandationType(QTConstants.SETTING_TYPE_DEMO);
				} else {

					evalReqDTO
							.setRecomandationType(QTConstants.SETTING_TYPE_TECHNICAL);
				}
				List<String> resultStg = new ArrayList<String>();
				resultStg.add(QTConstants.TECHNICAL_EVALUATION);
				evalReqDTO.setResultStages(resultStg);
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()) {
					ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
					if (null != finalization) {
						if (finalization.isReEvaluateTechReq()) {
							if (!getManagedBean().isTechnicalSummaryGenerated()) {
								getManagedBean().setTechnicalSummaryGenerated(
										false);
							}
						} else {
							this.getManagedBean().setTechnicalSummaryGenerated(
									true);
						}
					}
				}
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isTechnicalSummaryGenerated()) {
					evaluationStageStatusCheck();
					evaluationSo.prepareTechnicalEvaluationSummary(evalReqDTO);
					getManagedBean().setTechnicalSummaryGenerated(true);
				}

				this.getManagedBean().setQualifiedSuppliersDM(
						new SummarySupplierPagingDataModel(
								"oneTierQualifiedSupplier", true, false, null,
								evalReqDTO.getResultStages()));
				this.getManagedBean().setDisqualifiedSuppliersDM(
						new SummarySupplierPagingDataModel(
								"oneTierDisQualifiedSupplier", false, false,
								null, evalReqDTO.getResultStages()));

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				if (this.getManagedBean().getScQt().isDemoRequired()) {
					reqDTO.setSettingType(getSettingType(QTConstants.PRESENTATION_DEMO_EVALUATION));
				} else {
					reqDTO.setSettingType(getSettingType(QTConstants.TECHNICAL_EVALUATION));
				}
				resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
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

					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setSettingType(getSettingType(QTConstants.PRESENTATION_DEMO_EVALUATION));
					resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
				} else {
					summaryReqDTO
							.setEvalStage(QTConstants.TECHNICAL_EVALUATION);

					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setSettingType(getSettingType(QTConstants.TECHNICAL_EVALUATION));
					resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
				}
				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg2);
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isFinancialSummaryGenerated()) {
					/*
					 * evaluationSo
					 * .prepareEvaluationResultsSummaryForTwoTier(summaryReqDTO
					 * );
					 */
					evaluationStageStatusCheck();
					evaluationSo
							.prepareFinancialEvaluationSummary(summaryReqDTO);
					getManagedBean().setFinancialSummaryGenerated(true);
				}

			}

			else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {

				if (this.getManagedBean().getScQt().getEvaluationType()
						.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
					summaryReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					summaryReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					List<String> resultStg2 = new ArrayList<String>();
					resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
					resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
					summaryReqDTO.setResultStages(resultStg2);
					if (this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()
							&& !getManagedBean().isFinancialSummaryGenerated()) {
						evaluationStageStatusCheck();
						evaluationSo
								.prepareFinancialEvaluationSummary(summaryReqDTO);
						getManagedBean().setFinancialSummaryGenerated(true);
					}

					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setSettingType(getSettingType(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE));
					resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
				} else if (this.getManagedBean().getScQt().getEvaluationType()
						.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
						&& this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					// evaluationStageStatusCheck();
					/*
					 * evaluationSo
					 * .prepareRecommendationEvaluationSummary(summaryReqDTO);
					 */

					EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
					evalReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					evalReqDTO
							.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
					evalReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					evalReqDTO
							.setRecomandationType(QTConstants.SETTING_TYPE_TECHNICAL);
					List<String> resultStg = new ArrayList<String>();
					resultStg.add(QTConstants.TECHNICAL_EVALUATION);
					evalReqDTO.setResultStages(resultStg);
					if (this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()) {
						ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
						if (null != finalization) {
							if (finalization.isReEvaluateTechReq()) {
								if (!getManagedBean()
										.isTechnicalSummaryGenerated()) {
									getManagedBean()
											.setTechnicalSummaryGenerated(false);
								}
							} else {
								getManagedBean().setTechnicalSummaryGenerated(
										true);
							}
						}
					}
					if (this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()
							&& !getManagedBean().isTechnicalSummaryGenerated()) {
						evaluationStageStatusCheck();
						evaluationSo
								.prepareTechnicalEvaluationSummary(evalReqDTO);
						getManagedBean().setTechnicalSummaryGenerated(true);
					}

					this.getManagedBean().setQualifiedSuppliersDM(
							new SummarySupplierPagingDataModel(
									"oneTierQualifiedSupplier", true, false,
									null, evalReqDTO.getResultStages()));
					this.getManagedBean().setDisqualifiedSuppliersDM(
							new SummarySupplierPagingDataModel(
									"oneTierDisQualifiedSupplier", false,
									false, null, evalReqDTO.getResultStages()));

					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setSettingType(getSettingType(QTConstants.TECHNICAL_EVALUATION));
					resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
				}

			}
			// }

			this.getManagedBean().setPassingScore(resDTO.getPassingScore());

		} finally {

		}

	}

	/**
	 * Retrieve One Tier With Technical Financial Summary.generate summary for
	 * evaluation stage summary to get all the qualified and disqualified
	 * suppliers for package and preference margin and item.
	 */
	public void retriveOneTierFinancialSupplierSummary() {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setOneTierFinancialActiveIndex(1);
			this.getManagedBean().setSummaryViewBy("");
			this.getManagedBean().setFinancialSummaryView("");
			if (this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
					&& !this.getManagedBean().isReadOnly()
					&& this.getManagedBean().getScQt().getEvaluationType()
							.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
					&& this.getManagedBean().getScQt().isTechEvaluationReq()) {
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());

				summaryReqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);

				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg2);
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isFinancialSummaryGenerated()) {
					/*
					 * evaluationSo
					 * .prepareEvaluationResultsSummaryForTwoTier(summaryReqDTO
					 * );
					 */
					financialCheckListStatusCheck();
					evaluationSo
							.prepareFinancialEvaluationSummary(summaryReqDTO);
					getManagedBean().setFinancialSummaryGenerated(true);
				}
			}
		} finally {

		}

	}

	/**
	 * Retrieve One Tier With Technical Recommendation Summary.generate summary
	 * for evaluation stage summary to get all the qualified and disqualified
	 * suppliers for package and preference margin and item.
	 */
	public void retriveOneTierRecommendationSupplierSummary() {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setOneTierFinancialActiveIndex(2);
			this.getManagedBean().setSummaryViewBy("");
			this.getManagedBean().setFinancialSummaryView("");
			if (this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
					&& !this.getManagedBean().isReadOnly()
					&& this.getManagedBean().getScQt().getEvaluationType()
							.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
					&& this.getManagedBean().getScQt().isTechEvaluationReq()) {
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
				summaryReqDTO
						.setQtId(this.getManagedBean().getScQt().getQtId());

				summaryReqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);

				List<String> resultStg2 = new ArrayList<String>();
				resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
				resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
				resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
				summaryReqDTO.setResultStages(resultStg2);
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isFinancialSummaryGenerated()) {
					/*
					 * evaluationSo
					 * .prepareEvaluationResultsSummaryForTwoTier(summaryReqDTO
					 * );
					 */
					financialCheckListStatusCheck();
					evaluationSo
							.prepareFinancialEvaluationSummary(summaryReqDTO);
					getManagedBean().setFinancialSummaryGenerated(true);
				}
			}
		} finally {

		}

	}

	/**
	 * Retrieve One Tier Without Technical Financial supplier Summary.generate
	 * summary for evaluation stage summary to get all the qualified and
	 * disqualified suppliers for package and preference margin and item.
	 */
	public void retriveOneTierWithoutTechnicalFinancialSupplierSummary() {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setEvaluatioStageActiveIndex(2);
			this.getManagedBean().setSummaryViewBy("");
			this.getManagedBean().setFinancialSummaryView("");
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				if (this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()
						&& this.getManagedBean()
								.getScQt()
								.getEvaluationType()
								.equalsIgnoreCase(
										QTConstants.EVALUATION_ONE_TIER)
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()
						&& !getManagedBean().isFinancialSummaryGenerated()) {
					evaluationStageStatusCheck();
					EvaluationReqDTO summaryReqDTO = new EvaluationReqDTO();
					summaryReqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					summaryReqDTO.setEvalStage(QTConstants.COMPLIANCE_CHECK);
					List<String> resultStg2 = new ArrayList<String>();
					resultStg2.add(QTConstants.RECOMMENDATION_BY_PACKAGE);
					resultStg2.add(QTConstants.RECOMMENDATION_BY_ITEM);
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE);
					resultStg2.add(QTConstants.FINANCIAL_EVALUATION_BY_ITEM);
					summaryReqDTO.setResultStages(resultStg2);
					evaluationSo
							.prepareFinancialEvaluationSummary(summaryReqDTO);
					getManagedBean().setFinancialSummaryGenerated(true);
				}
			}
		} finally {

		}

	}

	// check one Tier technical Financial checklist status is completed or not.
	private void financialCheckListStatusCheck() {
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			List<ScQtCheckList> financialcheckList = this.getManagedBean()
					.getEvaluationStgfinancialCheckList();
			if (null == financialcheckList || financialcheckList.isEmpty()) {
				retriveFinancialEvaluationStage();
				if (!this.getManagedBean().isCheckQualifiedSuppliers()) {
					if (null != this.getManagedBean()
							.getEvaluationStgfinancialCheckList()
							&& !this.getManagedBean()
									.getEvaluationStgfinancialCheckList()
									.isEmpty()) {

						for (ScQtCheckList techCheckList : this
								.getManagedBean()
								.getEvaluationStgfinancialCheckList()) {
							if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(techCheckList
											.getDefinationType()
											.getParameterCode())
									|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
											.equalsIgnoreCase(techCheckList
													.getDefinationType()
													.getParameterCode())) {
								if (techCheckList.isScoringReq()) {
									retriveUploadCheckListFinancialSuppliers(techCheckList);
								}

							} else {
								if (techCheckList.isScoringReq()
										&& null != techCheckList
												.getPmEvalStatus()

										&& techCheckList
												.getPmEvalStatus()
												.getStatusId()
												.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
									if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
											.equalsIgnoreCase(this
													.getManagedBean()
													.getCommitteeType())) {
										this.getManagedBean()
												.setOneTierFinancialActiveIndex(
														0);
									}
									throw new EpException(
											MessageConst.ERROR_760241);
								}
							}
						}
					}
				} else {

					/*
					 * FacesContext .getCurrentInstance() .addMessage( null, new
					 * FacesMessage( FacesMessage.SEVERITY_WARN, null,
					 * MessageUtils .getMessage( MessageConst.WARN_760248,
					 * MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
					 * .getUserLocale())));
					 */

				}
			} else {
				if (!this.getManagedBean().isCheckQualifiedSuppliers()) {
					for (ScQtCheckList techCheckList : this.getManagedBean()
							.getEvaluationStgfinancialCheckList()) {
						if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
								.equalsIgnoreCase(techCheckList
										.getDefinationType().getParameterCode())
								|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
										.equalsIgnoreCase(techCheckList
												.getDefinationType()
												.getParameterCode())) {
							if (techCheckList.isScoringReq()) {
								retriveUploadCheckListFinancialSuppliers(techCheckList);
							}

						} else {
							if (techCheckList.isScoringReq()
									&& null != techCheckList.getPmEvalStatus()

									&& techCheckList
											.getPmEvalStatus()
											.getStatusId()
											.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
								if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
										.equalsIgnoreCase(this.getManagedBean()
												.getCommitteeType())) {
									this.getManagedBean()
											.setOneTierFinancialActiveIndex(0);
								}
								throw new EpException(MessageConst.ERROR_760241);
							}
						}
					}
				} else {

					/*
					 * FacesContext .getCurrentInstance() .addMessage( null, new
					 * FacesMessage( FacesMessage.SEVERITY_WARN, null,
					 * MessageUtils .getMessage( MessageConst.WARN_760248,
					 * MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
					 * .getUserLocale())));
					 */

				}
			}
		}

	}

	/**
	 * View technical evaluation.Event handle to display all the compliance
	 * qualified suppliers in evaluation stage technical view button.
	 * 
	 * @param checklist
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checklistType
	 *            (Mandatory)
	 * @return navigate to Upload checklist or Template spec or Other checklist
	 *         pages.
	 */
	public String viewTechnicalEvaluation(ScQtCheckList checklist, int index,
			String checklistType) {
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setEvaluationCheckListType("");
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setEvaluationCheckListIndex(index);
			this.getManagedBean().setEvaluationTechnicalFinancialType(
					checklistType);
			this.getManagedBean().setUserScoreSchemaType("");
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				// reqDTO.setCompliance(true);
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

			else {
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
			/*
			 * resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * new ArrayList<ScQtProposalChecklist>()); if
			 * (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getComplianceResultBean().getResultList());
			 * 
			 * }
			 */
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"complianceTechnicalSupplier",
										checklist.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technicalFinancialUploadNoActionNoScoringSuppliersMain.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"templateSpecId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalSpecEvaluation.xhtml";
				// return "productServiceTechScoreSpec.xhtml";
			} else {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"othersTechListId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalOtherCheckListSuppliesMain.xhtml";

			}
		} finally {

		}

	}

	/**
	 * Evaluate technical evaluation.Event handle to display all the compliance
	 * qualified suppliers in evaluation stage Technical evaluate button.
	 * 
	 * @param checklist
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checklistType
	 *            (Mandatory)
	 * @return navigate to Upload checklist or Template spec or Other Checklist
	 *         pages.
	 */
	public String evaluateTechnicalEvaluation(ScQtCheckList checklist,
			int index, String checklistType) {
		try {
			this.getManagedBean().setEvaluationCheckListType("");
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setEvaluationCheckListIndex(index);
			this.getManagedBean().setEvaluationTechnicalFinancialType(
					checklistType);
			this.getManagedBean().setUserScores(null);
			this.getManagedBean().setUserScoreSchemaType("");
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(getEvaluationStageForTechnicalChecklist());
			/*
			 * resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * new ArrayList<ScQtProposalChecklist>()); if
			 * (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getComplianceResultBean().getResultList());
			 * 
			 * }
			 */
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
				if (null != finalization) {
					if (finalization.isReEvaluateTechReq()) {
						this.getManagedBean()
								.setTechnicalOrFinancialReEvaluateRequired(
										false);
					} else {
						this.getManagedBean()
								.setTechnicalOrFinancialReEvaluateRequired(true);
					}
				} else {
					this.getManagedBean()
							.setTechnicalOrFinancialReEvaluateRequired(false);
				}
				this.getManagedBean().setUserScoreSchemaType("2");
				this.getManagedBean().setUserScores(
						checklist.getQtScoreWeightage().getScore().toString());
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"complianceTechnicalSupplier",
										checklist.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalUploadNoActionSuppliersMain.xhtml";
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean()
						.setEvaluationCheckListType(
								ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
				// retriveTemplateSpecScoring(checklist);
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"templateSpecId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalSpecEvaluation.xhtml";
				// return "productServiceTechScoreSpec.xhtml";

			} else {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"othersTechListId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));

				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalOtherCheckListSuppliesMain.xhtml";

			}
		} finally {

		}

	}

	private String getEvaluationStageForTechnicalChecklist() {
		String committeeType = this.getManagedBean().getCommitteeType();

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(committeeType)
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(committeeType)) {
			return SourcingConstants.COMPLAINCE_STAGE;
		} else {
			if (this.getManagedBean().getScQt().isDemoRequired()) {
				return QTConstants.PRESENTATION_DEMO_EVALUATION;
			} else {
				return QTConstants.TECHNICAL_EVALUATION;
			}
		}
	}

	/**
	 * View financial evaluation.Event handle to display all the compliance or
	 * technical or presentation demo qualified suppliers in evaluation stage
	 * financial view button.
	 * 
	 * @param checklist
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checklistType
	 *            (Mandatory)
	 * @return navigate to Upload checklist or Template Spec or Other ChecList
	 *         pages.
	 */
	public String viewfinacialEvaluation(ScQtCheckList checklist, int index,
			String checklistType) {
		IQTEvaluationSO evaluationSo = null;
		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setEvaluationCheckListType("");
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setEvaluationCheckListIndex(index);
			this.getManagedBean().setEvaluationTechnicalFinancialType(
					checklistType);
			this.getManagedBean().setUserScoreSchemaType("");
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				// reqDTO.setCompliance(true);
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					// reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						} else {
							reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						}

					}
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}
			}

			else {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					// reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						} else {
							reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						}

					}
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

			}
			/*
			 * resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * new ArrayList<ScQtProposalChecklist>()); if
			 * (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getComplianceResultBean().getResultList()); }
			 */
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"complianceTechnicalSupplier",
										checklist.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/technicalFinancialUploadNoActionNoScoringSuppliersMain.xhtml";
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {

				this.getManagedBean().setEvaluationCheckListType("FS");
				EvaluationReqDTO specDtls = new EvaluationReqDTO();
				specDtls.setQtId(this.getManagedBean().getScQt().getQtId());
				specDtls.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());
				if (!this.getManagedBean().getScQt().isZonal()) {
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						// specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							if (this.getManagedBean().getScQt()
									.isTechEvaluationReq()) {
								specDtls.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							} else {
								specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							}

						}
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
					retriveFinancialSpecZones(speczone);
					return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithZone.xhtml";
				} else {
					// rertriveFinSpecDtlsByChecklistId() withoutzone
					// retriveProposalChecklistByzone() withzonespec
					calculateFinSpecPackageVarieance(this.getManagedBean()
							.getScQt(), speczone.getScoreOfferPrices(), this
							.getManagedBean().getSelectedZone());
					if (null != speczone.getScoreOfferPrices()) {
						this.getManagedBean().setOfferPricePackage(
								speczone.getScoreOfferPrices());
						this.getManagedBean().setWithInVarieance(
								speczone.getWithinVarieancePackage());

					}
					this.getManagedBean()
							.setFinSpecSupplierListDM(
									new FinancialSpecSupplierWithOutZonePagingDataModel(
											"financialSpecEvaluationStage",
											specDtls.getCheckListId(), specDtls
													.getQtId(), specDtls
													.getEvalStage()));

					return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithOutzone.xhtml";
				}

			} else {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"othersFinListId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/financial/financialOtherListSuppliersMain.xhtml";

			}
		} finally {

		}
	}

	/**
	 * Retrive financial spec zones.
	 * 
	 * @param speczone
	 *            the speczone
	 */
	private void retriveFinancialSpecZones(EvaluationResDTO speczone) {
		try {
			// if (null == this.getManagedBean().getFinSpecZoneBean()) {
			this.getManagedBean().setSpecZoneDtls(speczone.getZones());

			populateFinancialSpecDetailsByZone();
			for (int i = 0; i < speczone.getZones().size(); i++) {

				EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						zoneReqDto
								.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							zoneReqDto
									.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						} else {
							zoneReqDto
									.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						}

					}
					// zoneReqDto.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						zoneReqDto
								.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						zoneReqDto
								.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}

				this.getManagedBean().setSelectedZone(
						speczone.getZones().get(0));

				zoneReqDto.setQtId(this.getManagedBean().getScQt().getQtId());
				zoneReqDto.setSpeZoneId(speczone.getZones().get(0)
						.getSpecZoneId());
				zoneReqDto.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());

				if (null != speczone.getScoreOfferPrices()
						&& this.getManagedBean()
								.getSelectedZone()
								.getZoneName()
								.equalsIgnoreCase(
										speczone.getZones().get(i)
												.getZoneName())) {
					this.getManagedBean().setOfferPricePackage(
							speczone.getScoreOfferPrices());
					this.getManagedBean().setWithInVarieance(
							speczone.getWithinVarieancePackage());
					calculateFinSpecPackageVarieance(this.getManagedBean()
							.getScQt(), this.getManagedBean()
							.getOfferPricePackage(), this.getManagedBean()
							.getSelectedZone());
					// break;

				}
				if (this.getManagedBean()
						.getSelectedZone()
						.getZoneName()
						.equalsIgnoreCase(
								speczone.getZones().get(i).getZoneName())) {

					this.getManagedBean().setFinSpecSupplierListDM(
							new FinancialSpecSupplierWithZonePagingDataModel(
									"financialSpecEvaluationStage", zoneReqDto
											.getCheckListId(), zoneReqDto
											.getQtId(), zoneReqDto
											.getEvalStage(), zoneReqDto
											.getSpeZoneId()));
					break;
				}

			}
			// }
		} finally {
		}
	}

	private void populateFinancialSpecDetailsByZone() {

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getSpecZoneDtls())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean()
					.getSpecZoneDtls();
			this.getManagedBean().setSpecZoneTabIndex(0);
			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					zoneItem.setAjax(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOncomplete("specTabChange([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean()
					.setEvaluationSpecZonalMenuModel(zoneMenuModel);
		}
	}

	/**
	 * Calculate fin spec package varieance.calculate from and to varieance for
	 * package based on estimated price.
	 * 
	 * @param scQt
	 *            (Mandatory)
	 * @param packageOfferPrice
	 *            (Mandatory)
	 */
	private void calculateFinSpecPackageVarieance(ScQt scQt,
			ScQtScoreOfferPrice packageOfferPrice, TplSpecZoneDtl specZoneDtl) {
		ITemplateSpecSO templateSpecSo;
		TemplateSpecResDTO zoneSpecresDTO;
		try {
			if (null != packageOfferPrice) {
				if (!scQt.isZonal()) {
					BigDecimal estimatedPrice = scQt.getEstimatedAmount();
					this.getManagedBean().setFinSpecZoneEstimatedPrice(
							estimatedPrice);
					if (null != packageOfferPrice.getFromVariance()) {
						int fromvarieance = estimatedPrice.intValue()
								* packageOfferPrice.getFromVariance()
										.intValue() / 100;
						int packageamount = estimatedPrice.intValue()
								- fromvarieance;
						this.getManagedBean().setPackageFromVarieance(
								new BigDecimal(packageamount));

					}
					if (null != packageOfferPrice.getToVariance()) {
						int tovarieance = estimatedPrice.intValue()
								* packageOfferPrice.getToVariance().intValue()
								/ 100;
						int toamount = estimatedPrice.intValue() + tovarieance;
						this.getManagedBean().setPackageToVarieance(
								new BigDecimal(toamount));

					}
				} else {
					if (null != specZoneDtl) {
						templateSpecSo = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.COMMON_COMPONENT,
										ITemplateSpecSO.class);
						TemplateSpecReqDTO zonePriceReqDto = new TemplateSpecReqDTO();
						zonePriceReqDto.setQtId(scQt.getQtId());
						zonePriceReqDto.setSpecZoneId(specZoneDtl
								.getSpecZoneId());
						zoneSpecresDTO = templateSpecSo
								.retrieveTotalSpecEstimationsByQtIdAndZone(zonePriceReqDto);
						this.getManagedBean()
								.setFinSpecZoneEstimatedPrice(null);
						this.getManagedBean().setPackageFromVarieance(null);
						this.getManagedBean().setPackageToVarieance(null);
						if (null != zoneSpecresDTO
								&& null != zoneSpecresDTO.getEstimatedPrices()) {

							if (null != packageOfferPrice.getFromVariance()) {
								int fromvarieance = zoneSpecresDTO
										.getEstimatedPrices().intValue()
										* packageOfferPrice.getFromVariance()
												.intValue() / 100;
								int packageamount = zoneSpecresDTO
										.getEstimatedPrices().intValue()
										- fromvarieance;
								this.getManagedBean().setPackageFromVarieance(
										new BigDecimal(packageamount));

							}
							if (null != packageOfferPrice.getToVariance()) {
								int tovarieance = zoneSpecresDTO
										.getEstimatedPrices().intValue()
										* packageOfferPrice.getToVariance()
												.intValue() / 100;
								int toamount = zoneSpecresDTO
										.getEstimatedPrices().intValue()
										+ tovarieance;
								this.getManagedBean().setPackageToVarieance(
										new BigDecimal(toamount));

							}

							this.getManagedBean().setFinSpecZoneEstimatedPrice(
									zoneSpecresDTO.getEstimatedPrices());

						}

					}

				}

			}
		} finally {

		}

	}

	/**
	 * Evaluate financial evaluation.Event handle to display all the compliance
	 * or technical or presentation demo qualified suppliers in evaluation stage
	 * Financial evaluate button.
	 * 
	 * @param checklist
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checklistType
	 *            (Mandatory)
	 * @return navigate to upload checklist or Template Spec or Other checkList
	 *         pages.
	 */
	public String evaluatefinancialEvaluation(ScQtCheckList checklist,
			int index, String checklistType) {
		IQTEvaluationSO evaluationSo = null;
		try {
			this.getManagedBean().setEvaluationCheckListType("");
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setEvaluationCheckListIndex(index);
			this.getManagedBean().setEvaluationTechnicalFinancialType(
					checklistType);
			this.getManagedBean().setUserScoreSchemaType("");
			this.getManagedBean().setUserScores(null);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(this.getManagedBean().getScQtChecklist()
					.getQtChecklistId());
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(getEvaluationStageForFinancialChecklist());
			/*
			 * resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * new ArrayList<ScQtProposalChecklist>()); if
			 * (resDTO.getComplianceResultBean().getTotalRecord() > 0) {
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getComplianceResultBean().getResultList());
			 * 
			 * }
			 */
			// this.getManagedBean().setQtProposalChecklist(checklist);
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())
					|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())) {
				ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
				if (null != finalization) {
					if (finalization.isReEvaluateFinReq()) {
						this.getManagedBean()
								.setTechnicalOrFinancialReEvaluateRequired(
										false);
					} else {
						this.getManagedBean()
								.setTechnicalOrFinancialReEvaluateRequired(true);
					}
				} else {
					this.getManagedBean()
							.setTechnicalOrFinancialReEvaluateRequired(false);
				}
				this.getManagedBean().setUserScoreSchemaType("2");
				this.getManagedBean().setUserScores(
						this.getManagedBean().getScQtChecklist()
								.getQtScoreWeightage().getScore().toString());
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"evaluateFinSupplier", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/financial/financialUploadNoActionSupplierMain.xhtml";
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(this.getManagedBean().getScQtChecklist()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setEvaluationCheckListType("FS");
				EvaluationReqDTO specDtls = new EvaluationReqDTO();
				specDtls.setQtId(this.getManagedBean().getScQt().getQtId());
				specDtls.setCheckListId(this.getManagedBean()
						.getScQtChecklist().getQtChecklistId());
				if (!this.getManagedBean().getScQt().isZonal()) {
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							if (this.getManagedBean().getScQt()
									.isTechEvaluationReq()) {
								specDtls.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							} else {
								specDtls.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							}

						}
					} else {
						if (this.getManagedBean().getScQt().isDemoRequired()) {
							specDtls.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
						} else {
							specDtls.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						}
					}
				}
				// retriveTemplateSpecScoring(checklist);
				EvaluationResDTO speczone = evaluationSo
						.retrieveFinSpecDetailsByCheckListId(specDtls);

				if (null != speczone.getZones()
						&& !speczone.getZones().isEmpty()) {

					retriveFinancialSpecZones(speczone);
					return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithZone.xhtml";
				} else {
					// rertriveFinSpecDtlsByChecklistId() withoutzone
					// retriveProposalChecklistByzone() withzonespec
					calculateFinSpecPackageVarieance(this.getManagedBean()
							.getScQt(), speczone.getScoreOfferPrices(), this
							.getManagedBean().getSelectedZone());
					if (null != speczone.getScoreOfferPrices()) {
						this.getManagedBean().setOfferPricePackage(
								speczone.getScoreOfferPrices());
						this.getManagedBean().setWithInVarieance(
								speczone.getWithinVarieancePackage());
					}

					/*
					 * calculateFinacialSpecSupplierPackageScore(speczone
					 * .getComplianceResultBean().getResultList(),
					 * speczone.getScoreOfferPrices());
					 */
					this.getManagedBean()
							.setFinSpecSupplierListDM(
									new FinancialSpecSupplierWithOutZonePagingDataModel(
											"financialSpecEvaluationStage",
											specDtls.getCheckListId(), specDtls
													.getQtId(), specDtls
													.getEvalStage()));

					return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecWithOutzone.xhtml";
				}

			} else {
				this.getManagedBean()
						.setComplianceTechnicalProposalCheckListDM(
								new ComplianceSupplierPagingDataModel(
										"othersFinListId", checklist
												.getQtChecklistId(), reqDTO
												.getCompliance(), this
												.getManagedBean().getScQt()
												.getQtId(), reqDTO
												.getEvalStage()));
				return "/xhtml/sc/evaluation/evaluationStage/financial/financialOtherListSuppliersMain.xhtml";

			}
		} finally {

		}

	}

	private String getEvaluationStageForFinancialChecklist() {
		String committeeType = this.getManagedBean().getCommitteeType();

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(committeeType)
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(committeeType)) {
			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				return SourcingConstants.COMPLAINCE_STAGE;
			} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				if (this.getManagedBean().getScQt().isTechEvaluationReq()) {
					return QTConstants.TECHNICAL_EVALUATION;
				} else {
					return SourcingConstants.COMPLAINCE_STAGE;
				}

			}
		} else {
			if (this.getManagedBean().getScQt().isDemoRequired()) {
				return QTConstants.PRESENTATION_DEMO_EVALUATION;
			} else {
				return QTConstants.TECHNICAL_EVALUATION;
			}
		}

		return null;
	}

	/**
	 * Spec tab change.Event handle changed zones in financial spec to display
	 * all the compliance or technical or presentation demo qualified suppliers
	 * based on zonal.
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void specTabChange() {
		try {
			this.getManagedBean().getFinSpecSupplierListDM().reloadData();
			Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
					.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("selectedZoneTabIndex"));

			this.getManagedBean().setSpecZoneTabIndex(selectedZoneTabIndex);
			debugLogger("zoneTabChange");
			TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) getManagedBean()
					.getSpecZoneDtls().get(selectedZoneTabIndex);
			log.info("Selected Zone : " + specZoneDtl.getZoneName());
			getManagedBean().setSelectedZone(specZoneDtl);

			for (int i = 0; i < this.getManagedBean().getSpecZoneDtls().size(); i++) {
				TplSpecZoneDtl zoneDtls = this.getManagedBean()
						.getSpecZoneDtls().get(i);
				if (zoneDtls.getZoneName().equalsIgnoreCase(
						specZoneDtl.getZoneName())) {

					EvaluationReqDTO zoneReqDto = new EvaluationReqDTO();
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						// zoneReqDto
						// .setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							zoneReqDto
									.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
							if (this.getManagedBean().getScQt()
									.isTechEvaluationReq()) {
								zoneReqDto
										.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							} else {
								zoneReqDto
										.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							}

						}
					} else {
						if (this.getManagedBean().getScQt().isDemoRequired()) {
							zoneReqDto
									.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
						} else {
							zoneReqDto
									.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						}
					}

					zoneReqDto.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					zoneReqDto.setSpeZoneId(specZoneDtl.getSpecZoneId());
					zoneReqDto.setCheckListId(this.getManagedBean()
							.getScQtChecklist().getQtChecklistId());
					calculateFinSpecPackageVarieance(this.getManagedBean()
							.getScQt(), this.getManagedBean()
							.getOfferPricePackage(), this.getManagedBean()
							.getSelectedZone());
					this.getManagedBean().setFinSpecSupplierListDM(
							new FinancialSpecSupplierWithZonePagingDataModel(
									"financialSpecEvaluationStage", zoneReqDto
											.getCheckListId(), zoneReqDto
											.getQtId(), zoneReqDto
											.getEvalStage(), zoneReqDto
											.getSpeZoneId()));

					break;
				}
			}
		} finally {
		}
	}

	/**
	 * Return check list scores.
	 * 
	 * @param checklistScores
	 *            the checklist scores
	 */
	private void returnCheckListScores(List<ScQtCheckListScore> checklistScores) {

		Collections.sort(checklistScores);

	}

	/**
	 * Others technical evaluate.Event handle to display the supplier
	 * information based on the compliance qualified supplier in evaluation
	 * stage technical Tab.
	 * 
	 * @param proposalCheckList
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checkListType
	 *            (Mandatory)
	 * @return navigate to supplier information page.
	 */
	public String technicalSupplierEvaluate(
			ScQtProposalChecklist proposalCheckList, String checkListType) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		this.getManagedBean().setCheckListType(checkListType);
		String proposalPage = null;
		String evaluationPage = null;
		SPDetailBackingBean detailBackingBean = new SPDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(
				this.getManagedBean().getScQt());
		try {
			// this.getManagedBean().setTechindex(index);
			this.getManagedBean().setUserScores(null);
			this.getManagedBean().setContractScores(null);
			this.getManagedBean().setNoOfContractsProposalScore(null);
			this.getManagedBean().setNoOfyearsProposalScore(null);
			this.getManagedBean().setUserScoreSchemaType("");
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
			ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
			if (null != finalization) {
				if (finalization.isReEvaluateTechReq()) {
					this.getManagedBean()
							.setTechnicalOrFinancialReEvaluateRequired(false);
				} else {
					this.getManagedBean()
							.setTechnicalOrFinancialReEvaluateRequired(true);
				}
			} else {
				this.getManagedBean()
						.setTechnicalOrFinancialReEvaluateRequired(false);
			}
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
				retriveTemplateSpecScoring(proposalCheckList.getScQtCheckList());
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

				return "/xhtml/sc/evaluation/evaluationStage/technical/technicalTemplateSpecScoring.xhtml";
			}

			else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(proposalCheckList.getScQtCheckList()
							.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSampleItemSpecifications(null);
				if (proposalCheckList.getScQtCheckList().isScoringReq()) {
					this.getManagedBean().setUserScoreSchemaType("2");
				}
				this.getManagedBean().setUserScores(
						proposalCheckList.getScQtCheckList()
								.getQtScoreWeightage().getScore().toString());
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
				return "/xhtml/sc/evaluation/evaluationStage/technical/techSampleEvaluation.xhtml";
			} else {
				String docDefinationKey = proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey();
				this.getManagedBean().setDocDefinitionKey(docDefinationKey);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());
				this.getManagedBean().setTechnicalGovNoOfContracts(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setTechnicalGovNoOfYears(
						new ArrayList<ScQtCheckListScore>());
				// this.getManagedBean().setNoOfContractsProposalScore(null);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setChecklistScoreType(null);
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								noOfContracts.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												noOfContracts);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

							} else if (checklistScore.getCriteriaType() == 11) {

								this.getManagedBean().setCompanyScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean()
												.setContractScores(
														checklistScore
																.getScore()
																.toString());
									}
								}
								noOfYears.add(checklistScore);
								this.getManagedBean().setTechnicalGovNoOfYears(
										noOfYears);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfYears()
										&& this.getManagedBean()
												.getTechnicalGovNoOfYears()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfYears());

								}

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
							if (proposalScore.getCriteriaType() == 11
									|| proposalScore.getCriteriaType() == 0) {
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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/techGovCompanyExperience.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								noOfContracts.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												noOfContracts);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

							} else if (checklistScore.getCriteriaType() == 16) {

								this.getManagedBean().setCompanyScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean()
												.setContractScores(
														checklistScore
																.getScore()
																.toString());
									}
								}
								noOfYears.add(checklistScore);
								this.getManagedBean().setTechnicalGovNoOfYears(
										noOfYears);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfYears()
										&& this.getManagedBean()
												.getTechnicalGovNoOfYears()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfYears());

								}

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
							if (proposalScore.getCriteriaType() == 16
									|| proposalScore.getCriteriaType() == 0) {
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
					detailBackingBean.getManagedBean().setQt(
							this.getManagedBean().getScQt());
					proposalPage = detailBackingBean
							.retrieveTecNonGovCompanyExpDtls(proposalCheckList);

					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/techPrivateCompanyExperience.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								technicalStaff.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												technicalStaff);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
					/*
					 * detailBackingBean.getManagedBean().setSmSupplier(
					 * proposalCheckList.getScQtProposal()
					 * .getScQtSupplier().getSmSupplier());
					 */
					proposalPage = detailBackingBean
							.retriveTechnicalStaffDtls(proposalCheckList);
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/technicalStaffList.xhtml";

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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								technicalStaff.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												technicalStaff);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/technicalNoOfEmployees.xhtml";

				}

			}

		} finally {

		}

		this.getManagedBean().setProposalPage(proposalPage);
		this.getManagedBean().setEvaluationPage(evaluationPage);

		return "technicalotherchecklists.xhtml";
	}

	/**
	 * Others financial evaluate..Event handle to display the supplier
	 * information based on the compliance or technical or presentation demo
	 * qualified supplier in evaluation stage financial Tab.
	 * 
	 * @param proposalCheckList
	 *            (Mandatory)
	 * @param index
	 *            (Mandatory)
	 * @param checkListType
	 *            (Mandatory)
	 * @return navigate to supplier information page.
	 */
	public String financialSupplierEvaluate(
			ScQtProposalChecklist proposalCheckList, String checkListType) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		this.getManagedBean().setCheckListType(checkListType);

		String proposalPage = null;
		String evaluationPage = null;
		SPDetailBackingBean detailBackingBean = new SPDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(
				this.getManagedBean().getScQt());
		ScQtFinalization finalization = retriveScQtFinalizationForReEvaluate();
		if (null != finalization) {
			if (finalization.isReEvaluateFinReq()) {
				this.getManagedBean()
						.setTechnicalOrFinancialReEvaluateRequired(false);
			} else {
				this.getManagedBean()
						.setTechnicalOrFinancialReEvaluateRequired(true);
			}
		} else {
			this.getManagedBean().setTechnicalOrFinancialReEvaluateRequired(
					false);
		}
		ITemplateSpecSO templateSpecSO = null;
		try {
			// this.getManagedBean().setTechindex(index);
			this.getManagedBean().setUserScores(null);
			this.getManagedBean().setContractScores(null);
			this.getManagedBean().setNoOfContractsProposalScore(null);
			this.getManagedBean().setNoOfyearsProposalScore(null);
			this.getManagedBean().setCompanyScoreType(null);
			this.getManagedBean().setChecklistScoreType(null);
			this.getManagedBean().setUserScoreSchemaType("");
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
				if (null != resDTO.getScoreOfferPricesItem()) {
					this.getManagedBean().setOfferPriceItem(
							resDTO.getScoreOfferPricesItem());
					this.getManagedBean().setWithInVarieance(
							resDTO.getWithinVarieanceItem());
				}

				/*
				 * TemplateSpecBackingBean specBB = new
				 * TemplateSpecBackingBean(); TemplateSpecBean templateSpecBean
				 * = getManagedBean() .getTemplateSpecBean();
				 * templateSpecBean.setSupplier(proposalCheckList
				 * .getScQtProposal().getScQtSupplier().getSmSupplier());
				 * templateSpecBean
				 * .setContentType(TemplateSpecConst.CONTENT_TYPE_SCORE);
				 * templateSpecBean.setSpecMaster(proposalCheckList
				 * .getScQtCheckList().getScQtDoc().getTplSpecMaster());
				 * specBB.retrieveTemplateSpecMaster(templateSpecBean);
				 * System.out.println("====list size===" + templateSpecBean
				 * .getSpecAnswerList().get(0)
				 * .getTplSpecQuestion().getTplSpecScorings());
				 */
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				templateSpecSO = (ITemplateSpecSO) BusinessDelegate
						.getInstance().getServiceObject("cc",
								ITemplateSpecSO.class);

				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();

				templateSpecBean.setSupplier(proposalCheckList
						.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_SCORE);
				templateSpecBean.setSpecMaster(proposalCheckList
						.getScQtCheckList().getScQtDoc().getTplSpecMaster());

				if (null != this.getManagedBean().getScQt()
						&& this.getManagedBean().getScQt().isZonal()) {
					if (null != this.getManagedBean().getSelectedZone()) {
						templateSpecBean.setTplSpecZoneDtl(this
								.getManagedBean().getSelectedZone());
					}
				}

				specBB.retrieveTemplateSpecMaster(templateSpecBean);
				specReqDTO.setSpecMaster(proposalCheckList.getScQtCheckList()
						.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_SCORE);
				specReqDTO.setSupplier(proposalCheckList.getScQtProposal()
						.getScQtSupplier().getSmSupplier());
				TemplateSpecResDTO specresDTO1 = templateSpecSO
						.getTemplateMaster(specReqDTO);
				/*
				 * TemplateSpecBean templateSpecBean = getManagedBean()
				 * .getTemplateSpecBean();
				 * templateSpecBean.setSpecMaster(specresDTO1.getSpecMaster());
				 * templateSpecBean.setSpecAnswerList(specresDTO1
				 * .getSpecAnswerList());
				 */

				this.getManagedBean().setSpecItemScoringList(
						new ArrayList<TplSpecScoring>());
				Map<Long, TplSpecScoring> distinctScoring = new HashMap<Long, TplSpecScoring>();
				if (null != specresDTO1.getSpecAnswerList()
						&& !specresDTO1.getSpecAnswerList().isEmpty()) {
					for (TplSpecAnswer specAnswers : specresDTO1
							.getSpecAnswerList()) {
						if (null != specAnswers.getTplSpecQuestion()
								&& null != specAnswers.getTplSpecQuestion()
										.getScoreTypeCode()
								&& specAnswers
										.getTplSpecQuestion()
										.getScoreTypeCode()
										.getParameterCode()
										.equalsIgnoreCase(
												ParameterConst.SCORE_TYPE_AUTO)) {
							List<TplSpecScoring> specScoring = calclateSpecItemVariance(
									specAnswers.getTplSpecQuestion()
											.getTplSpecEstimations(),
									specAnswers.getTplSpecQuestion()
											.getTplSpecScorings(), this
											.getManagedBean().getScQt(), this
											.getManagedBean().getSelectedZone());
							distinctScoring.put(specScoring.get(0)
									.getSpecScoringId(), specScoring.get(0));

						}
						List<TplSpecScoring> scoringList = new ArrayList<TplSpecScoring>(
								distinctScoring.values());
						this.getManagedBean().setSpecItemScoringList(
								scoringList);
					}
				}
				retriveTemplateSpecScoring(proposalCheckList.getScQtCheckList());
				return "/xhtml/sc/evaluation/evaluationStage/financial/financialSpecTemplateSpecScoring.xhtml";
			}

			else {
				String docDefinationKey = proposalCheckList.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey();
				this.getManagedBean().setDocDefinitionKey(docDefinationKey);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());
				this.getManagedBean().setTechnicalGovNoOfContracts(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setChecklistScoreType(null);
				this.getManagedBean().setTechnicalGovNoOfYears(
						new ArrayList<ScQtCheckListScore>());
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								finGovContract.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												finGovContract);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/finGovCompanyExperience.xhtml";

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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								finGovContract.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												finGovContract);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/finPrivateCompanyExperience.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								bankStatement.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												bankStatement);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}
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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/bankStatement.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								creditFacility.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												creditFacility);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/creditFacility.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								cashOhHand.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												cashOhHand);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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

					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/cashOnHand.xhtml";

				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(docDefinationKey)) {

					reqDTO.setProposalChecklist(proposalCheckList);
					resDTO = evaluationSO
							.retrieveCheckListScoreNProposalScores(reqDTO);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());
					List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 60) {

								this.getManagedBean().setChecklistScoreType(
										String.valueOf(checklistScore
												.getScoreType().intValue()));
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								assetLiability.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												assetLiability);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

							}
							
							 else if (checklistScore.getCriteriaType() == 61) {

									this.getManagedBean().setCompanyScoreType(
											String.valueOf(checklistScore
													.getScoreType().intValue()));
									if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
										if (null != checklistScore.getScore()) {
											this.getManagedBean()
													.setContractScores(
															checklistScore
																	.getScore()
																	.toString());
										}
									}
									noOfYears.add(checklistScore);
									this.getManagedBean().setTechnicalGovNoOfYears(
											noOfYears);
									if (null != this.getManagedBean()
											.getTechnicalGovNoOfYears()
											&& this.getManagedBean()
													.getTechnicalGovNoOfYears()
													.size() > 1) {
										returnCheckListScores(this.getManagedBean()
												.getTechnicalGovNoOfYears());

									}

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
							if (proposalScore.getCriteriaType() == 61
									) {
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
						proposalScore.setCriteriaType(60);
						setUserDetails(proposalScore);
						this.getManagedBean().setNoOfContractsProposalScore(
								proposalScore);
						this.getManagedBean()
								.getTechGovProposalScore()
								.add(this.getManagedBean()
										.getNoOfContractsProposalScore());
						
						ScQtProposalScore proposalcurrentRatioScore = new ScQtProposalScore();
						proposalcurrentRatioScore.setProposalChecklist(this
								.getManagedBean().getQtProposalChecklist());
						proposalcurrentRatioScore.setCriteriaType(61);
						setUserDetails(proposalcurrentRatioScore);
						this.getManagedBean().setNoOfyearsProposalScore(
								proposalcurrentRatioScore);
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
							.retrieveAssetLiabilityDtls(proposalCheckList);

					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/assetsLiabilities.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
								paidUpCapital.add(checklistScore);
								this.getManagedBean()
										.setTechnicalGovNoOfContracts(
												paidUpCapital);
								if (null != this.getManagedBean()
										.getTechnicalGovNoOfContracts()
										&& this.getManagedBean()
												.getTechnicalGovNoOfContracts()
												.size() > 1) {
									returnCheckListScores(this.getManagedBean()
											.getTechnicalGovNoOfContracts());

								}

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
							.retrievePaidUpCapitalNdDeclarationDtls();

					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/paidUpCapital.xhtml";
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
								if (checklistScore.getScoreType().intValue() == QTConstants.CHECKLIST_USER_SCORE) {
									if (null != checklistScore.getScore()) {
										this.getManagedBean().setUserScores(
												checklistScore.getScore()
														.toString());
									}
								}
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
					this.getManagedBean()
							.setStatusHide(
									proposalCheckList.getScQtCheckList()
											.isScoringReq());
					this.getManagedBean().setProfileHide(true);
					detailBackingBean.getManagedBean().setSupplierId(
							proposalCheckList.getScQtProposal()
									.getScQtSupplier().getSmSupplier()
									.getSupplierId());

					/*
					 * detailBackingBean.getManagedBean().setSmSupplier(
					 * proposalCheckList.getScQtProposal()
					 * .getScQtSupplier().getSmSupplier());
					 */
					proposalPage = detailBackingBean.retrieveSupplierDtls();

					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/financial/supplierInfo.xhtml";
					// return "supplierInfo.xhtml";
				}
			}
		} finally {

		}
		this.getManagedBean().setProposalPage(proposalPage);
		this.getManagedBean().setEvaluationPage(evaluationPage);

		return "/xhtml/sc/evaluation/evaluationStage/financial/financialotherchecklists.xhtml";
	}

	/**
	 * Calclate spec item variance.calulate from and to variance for item based
	 * on tplspecEstimatedPrice
	 * 
	 * @param specEstimations
	 *            (Mandatory)
	 * @param specScorig
	 *            (Mandatory)
	 * @param scQt
	 *            (Mandatory)
	 * @param zoneDtl
	 *            (Mandatory)
	 * @return List<TplSpecScoring>
	 */
	private List<TplSpecScoring> calclateSpecItemVariance(
			List<TplSpecEstimation> specEstimations,
			List<TplSpecScoring> specScorig, ScQt scQt, TplSpecZoneDtl zoneDtl) {
		if (scQt.isZonal()) {
			if (null != zoneDtl) {
				if (null != specEstimations && !specEstimations.isEmpty()) {
					List<TplSpecEstimation> estimation = specEstimations;
					for (TplSpecEstimation specEstimation : estimation) {
						if (specEstimation.getTplSpecZoneDtl().getSpecZoneId()
								.equals(zoneDtl.getSpecZoneId())) {
							if (null != specEstimation.getEstimatedCost()
									&& null != specScorig.get(0).getRangeFrom()) {
								int fromvariance = specEstimation
										.getEstimatedCost().intValue()
										* specScorig.get(0).getRangeFrom()
												.intValue() / 100;
								int fromvarianceAmount = specEstimation
										.getEstimatedCost().intValue()
										- fromvariance;
								specScorig.get(0).setFromVariance(
										new BigDecimal(fromvarianceAmount));
							}
							if (null != specEstimation.getEstimatedCost()
									&& null != specScorig.get(0).getRangeTo()) {
								int tovariance = specEstimation
										.getEstimatedCost().intValue()
										* specScorig.get(0).getRangeTo()
												.intValue() / 100;
								int tovarianceAmount = specEstimation
										.getEstimatedCost().intValue()
										+ tovariance;
								specScorig.get(0).setToVariance(
										new BigDecimal(tovarianceAmount));
							}
							break;
						}

					}

				}

			}

		} else {
			if (null != specScorig && !specScorig.isEmpty()) {
				if (null != specEstimations && !specEstimations.isEmpty()) {
					TplSpecEstimation estimation = specEstimations.get(0);
					if (null != estimation.getEstimatedCost()
							&& null != specScorig.get(0).getRangeFrom()) {
						int fromvariance = estimation.getEstimatedCost()
								.intValue()
								* specScorig.get(0).getRangeFrom().intValue()
								/ 100;
						int fromvarianceAmount = estimation.getEstimatedCost()
								.intValue() - fromvariance;
						specScorig.get(0).setFromVariance(
								new BigDecimal(fromvarianceAmount));
					}
					if (null != estimation.getEstimatedCost()
							&& null != specScorig.get(0).getRangeTo()) {
						int tovariance = estimation.getEstimatedCost()
								.intValue()
								* specScorig.get(0).getRangeTo().intValue()
								/ 100;
						int tovarianceAmount = estimation.getEstimatedCost()
								.intValue() + tovariance;
						specScorig.get(0).setToVariance(
								new BigDecimal(tovarianceAmount));
					}

				}
			}

		}

		return specScorig;
	}

	/**
	 * Retrive presetation demo check list.Event handle presentation demo
	 * checklist for presentationdemo stage.
	 */
	private void retrivePresetationDemoCheckList() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		// this.getManagedBean().setQtProposalChecklist(checklist);
		this.getManagedBean().setUserScores(null);
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			resDTO = evaluationSo.retrievePresentationDemoCheckList(reqDTO);

			if (null != resDTO
					&& null != resDTO.getCheckListResultBean().getResultList()
					&& !resDTO.getCheckListResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setScQtChecklist(
						resDTO.getCheckListResultBean().getResultList().get(0));
				this.getManagedBean().setUserScores(
						this.getManagedBean().getScQtChecklist()
								.getQtScoreWeightage().getScore().toString());

			}

		} finally {
			evaluationSo = null;
		}
	}

	/**
	 * Retrieve presentation demo.display all the technical qualified suppliers
	 * in presentation demo stage
	 */
	public void retrivePresentationDemo() {

		// this.getManagedBean().setQtProposalChecklist(checklist);
		try {
			this.getManagedBean().setPresentationDemoActiveIndex(0);
			this.getManagedBean().setDemo(
					this.getManagedBean().getScQt().isDemoRequired());
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
			retrivePresetationDemoCheckList();
			if (this.getManagedBean().getScQt().isDemoRequired()) {
				this.getManagedBean().setPresentationDemoSupplierListDM(
						new PresentationDemoSuppliersPagingDataModel(
								"demoSupplierId", reqDTO.getQtId(), reqDTO
										.getEvalStage()));
			}

		} finally {

		}
	}

	/**
	 * Retrieve presentation demo summary.display all qualified and disqualified
	 * suppliers in presentation demo summary tab.
	 */
	public void retrivePresentationDemoSummary() {
		IQTEvaluationSO evaluationSo = null;
		try {

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					&& this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
					&& !this.getManagedBean().isReadOnly()) {
				if (this.getManagedBean().getScQt().isDemoRequired()) {
					saveMeetingDtls(false);
					if (null == this.getManagedBean().getScQtChecklist()) {
						retrivePresetationDemoCheckList();
					}
					retrivePresentationDemoChecklistSuppliersStatus(this
							.getManagedBean().getScQtChecklist());
					// retrivePresentationDemoSummary();
				}

			}
			this.getManagedBean().setPresentationDemoActiveIndex(1);
			EvaluationReqDTO evalReqDTO = new EvaluationReqDTO();
			evalReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			evalReqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
			evalReqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
			// evalReqDTO.setRecomandationType(3);
			evalReqDTO.setRecomandationType(QTConstants.SETTING_TYPE_TECHNICAL);
			List<String> resultStg = new ArrayList<String>();
			resultStg.add(SourcingConstants.EVAL_STAGE_PRESENTATION_DEMO);
			evalReqDTO.setResultStages(resultStg);
			if (this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
					&& !this.getManagedBean().isReadOnly()
					&& !getManagedBean().isPresentationDemoSummaryGenerated()) {
				evaluationSo.prepareTechnicalEvaluationSummary(evalReqDTO);
				getManagedBean().setPresentationDemoSummaryGenerated(true);
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationResDTO resDTO = new EvaluationResDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSettingType(getSettingType(QTConstants.TECHNICAL_EVALUATION));
			resDTO = evaluationSo.retrievePassingScoreByQT(reqDTO);
			this.getManagedBean().setDemoPassingScore(resDTO.getPassingScore());
			this.getManagedBean().setQualifiedSuppliersDM(
					new SummarySupplierPagingDataModel("qualifiedListId", true,
							false, null, evalReqDTO.getResultStages()));
			this.getManagedBean().setDisqualifiedSuppliersDM(
					new SummarySupplierPagingDataModel("disQualifiedListId",
							false, false, null, evalReqDTO.getResultStages()));
		} finally {

		}
	}

	/**
	 * Report down load zip file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Report down load.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Compliance stage status check.check compliance stage checklist status
	 * pending or complete
	 */
	private void complianceStageStatusCheck() {
		List<ScQtCheckList> technicalcheckList = this.getManagedBean()
				.getComplianceTechnicalCheckList();
		if (null != technicalcheckList && !technicalcheckList.isEmpty()) {
			for (ScQtCheckList techCheckList : technicalcheckList) {
				if (((null != techCheckList.getPmEvalStatus())
						&& (techCheckList
								.getDefinationType()
								.getParameterCode()
								.equalsIgnoreCase(
										ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD) && techCheckList
								.getPmEvalStatus().getStatusId()
								.equals(QTStatusConst.TS_PENDING_SUBMISSION)) || (techCheckList
						.getDefinationType()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION) && techCheckList
						.getPmEvalStatus().getStatusId()
						.equals(QTStatusConst.TS_PENDING_SUBMISSION)))) {
					int count = checkProposalSubmittedForCompliance(techCheckList);
					if (count > 0) {
						this.getManagedBean().setComplianceActiveIndex(0);
						throw new EpException(MessageConst.ERROR_760238);
					}
				}
			}
		}
		List<ScQtCheckList> financialcheckList = this.getManagedBean()
				.getCompliancefinancialCheckList();
		if (null == financialcheckList || financialcheckList.isEmpty()) {
			retriveComplianceFinancial();
			if (null != this.getManagedBean().getCompliancefinancialCheckList()
					&& !this.getManagedBean().getCompliancefinancialCheckList()
							.isEmpty()) {
				for (ScQtCheckList techCheckList : this.getManagedBean()
						.getCompliancefinancialCheckList()) {
					if (((null != techCheckList.getPmEvalStatus())
							&& (techCheckList
									.getDefinationType()
									.getParameterCode()
									.equalsIgnoreCase(
											ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD) && techCheckList
									.getPmEvalStatus()
									.getStatusId()
									.equals(QTStatusConst.TS_PENDING_SUBMISSION)) || (techCheckList
							.getDefinationType()
							.getParameterCode()
							.equalsIgnoreCase(
									ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION) && techCheckList
							.getPmEvalStatus().getStatusId()
							.equals(QTStatusConst.TS_PENDING_SUBMISSION)))) {
						int count = checkProposalSubmittedForCompliance(techCheckList);
						if (count > 0) {
							if (!this.getManagedBean().getScQt().isSampleReq()) {
								this.getManagedBean().setComplianceActiveIndex(
										1);
							} else {
								this.getManagedBean().setComplianceActiveIndex(
										2);
							}
							throw new EpException(MessageConst.ERROR_760239);
						}
					}
				}
			}
		} else {
			for (ScQtCheckList techCheckList : this.getManagedBean()
					.getCompliancefinancialCheckList()) {
				if (((null != techCheckList.getPmEvalStatus())
						&& (techCheckList
								.getDefinationType()
								.getParameterCode()
								.equalsIgnoreCase(
										ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD) && techCheckList
								.getPmEvalStatus().getStatusId()
								.equals(QTStatusConst.TS_PENDING_SUBMISSION)) || (techCheckList
						.getDefinationType()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION) && techCheckList
						.getPmEvalStatus().getStatusId()
						.equals(QTStatusConst.TS_PENDING_SUBMISSION)))) {
					int count = checkProposalSubmittedForCompliance(techCheckList);
					if (count > 0) {
						if (!this.getManagedBean().getScQt().isSampleReq()) {
							this.getManagedBean().setComplianceActiveIndex(1);
						} else {
							this.getManagedBean().setComplianceActiveIndex(2);
						}
						throw new EpException(MessageConst.ERROR_760239);
					}
				}
			}
		}
	}

	private int checkProposalSubmittedForCompliance(ScQtCheckList checkList) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		int count = 0;

		try {
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setCheckListId(checkList.getQtChecklistId());
			reqDTO.setCompliance(null);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(null);
			reqDTO.setProposalChecklistSearchBean(null);
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);

			if (null != resDTO
					&& null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				count = resDTO.getComplianceResultBean().getTotalRecord();
			}
		} finally {
			evaluationSo = null;
			resDTO = null;
		}
		return count;
	}

	/**
	 * Evaluation stage status check.check evaluation stage checklist status
	 * pending or complete.
	 */
	private void evaluationStageStatusCheck() {
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
			List<ScQtCheckList> technicalcheckList = this.getManagedBean()
					.getEvaluationStgTechnicalCheckList();
			if (!this.getManagedBean().isCheckQualifiedSuppliers()) {
				if (null != technicalcheckList && !technicalcheckList.isEmpty()) {
					for (ScQtCheckList techCheckList : technicalcheckList) {
						if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
								.equalsIgnoreCase(techCheckList
										.getDefinationType().getParameterCode())
								|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
										.equalsIgnoreCase(techCheckList
												.getDefinationType()
												.getParameterCode())) {
							if (techCheckList.isScoringReq()) {
								retriveUploadCheckListTechnicalSuppliers(techCheckList);
							}

						} else {
							if (techCheckList.isScoringReq()
									&& null != techCheckList.getPmEvalStatus()
									&& techCheckList
											.getPmEvalStatus()
											.getStatusId()
											.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
								this.getManagedBean()
										.setEvaluatioStageActiveIndex(0);
								throw new EpException(MessageConst.ERROR_760240);

							}
						}
					}
				}
			} else {
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					if (((QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType())) || (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(this.getManagedBean().getScQt()
									.getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq()))) {
						/*
						 * FacesContext .getCurrentInstance() .addMessage( null,
						 * new FacesMessage( FacesMessage.SEVERITY_WARN, null,
						 * MessageUtils .getMessage( MessageConst.WARN_760248,
						 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
						 * FacesScopes() .getUserLocale())));
						 */
					}
				}

			}
		}
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
			if (((QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) || (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(this.getManagedBean().getScQt()
							.getEvaluationType()) && !this.getManagedBean()
					.getScQt().isTechEvaluationReq()))) {
				List<ScQtCheckList> financialcheckList = this.getManagedBean()
						.getEvaluationStgfinancialCheckList();
				if (null == financialcheckList || financialcheckList.isEmpty()) {
					retriveFinancialEvaluationStage();
					if (!this.getManagedBean().isCheckQualifiedSuppliers()) {
						if (null != this.getManagedBean()
								.getEvaluationStgfinancialCheckList()
								&& !this.getManagedBean()
										.getEvaluationStgfinancialCheckList()
										.isEmpty()) {

							for (ScQtCheckList techCheckList : this
									.getManagedBean()
									.getEvaluationStgfinancialCheckList()) {
								if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
										.equalsIgnoreCase(techCheckList
												.getDefinationType()
												.getParameterCode())
										|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
												.equalsIgnoreCase(techCheckList
														.getDefinationType()
														.getParameterCode())) {
									if (techCheckList.isScoringReq()) {
										retriveUploadCheckListFinancialSuppliers(techCheckList);
									}

								} else {

									if (techCheckList.isScoringReq()
											&& null != techCheckList
													.getPmEvalStatus()

											&& techCheckList
													.getPmEvalStatus()
													.getStatusId()
													.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
										if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
												.equalsIgnoreCase(this
														.getManagedBean()
														.getCommitteeType())) {
											this.getManagedBean()
													.setEvaluatioStageActiveIndex(
															1);
										} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
												.equalsIgnoreCase(this
														.getManagedBean()
														.getCommitteeType())) {
											this.getManagedBean()
													.setEvaluatioStageActiveIndex(
															0);
										}
										throw new EpException(
												MessageConst.ERROR_760241);
									}
								}
							}
						}
					} else {

						/*
						 * FacesContext .getCurrentInstance() .addMessage( null,
						 * new FacesMessage( FacesMessage.SEVERITY_WARN, null,
						 * MessageUtils .getMessage( MessageConst.WARN_760248,
						 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
						 * FacesScopes() .getUserLocale())));
						 */

					}
				} else {
					if (!this.getManagedBean().isCheckQualifiedSuppliers()) {
						for (ScQtCheckList techCheckList : this
								.getManagedBean()
								.getEvaluationStgfinancialCheckList()) {
							if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(techCheckList
											.getDefinationType()
											.getParameterCode())
									|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
											.equalsIgnoreCase(techCheckList
													.getDefinationType()
													.getParameterCode())) {
								if (techCheckList.isScoringReq()) {
									retriveUploadCheckListFinancialSuppliers(techCheckList);
								}

							} else {
								if (techCheckList.isScoringReq()
										&& null != techCheckList
												.getPmEvalStatus()

										&& techCheckList
												.getPmEvalStatus()
												.getStatusId()
												.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
									if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
											.equalsIgnoreCase(this
													.getManagedBean()
													.getCommitteeType())) {
										this.getManagedBean()
												.setEvaluatioStageActiveIndex(1);
									} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
											.equalsIgnoreCase(this
													.getManagedBean()
													.getCommitteeType())) {
										this.getManagedBean()
												.setEvaluatioStageActiveIndex(0);
									}
									throw new EpException(
											MessageConst.ERROR_760241);
								}
							}
						}
					} else {

						/*
						 * FacesContext .getCurrentInstance() .addMessage( null,
						 * new FacesMessage( FacesMessage.SEVERITY_WARN, null,
						 * MessageUtils .getMessage( MessageConst.WARN_760248,
						 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
						 * FacesScopes() .getUserLocale())));
						 */

					}
				}
			}
		}
	}

	// retrieve upload qualified suppliers based on compliance stage.
	private void retriveUploadCheckListTechnicalSuppliers(
			ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				reqDTO.setCheckListId(checklist.getQtChecklistId());
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				/*
				 * resDTO =
				 * evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO); if
				 * (null != resDTO && null != resDTO.getComplianceResultBean()
				 * && resDTO.getComplianceResultBean().getTotalRecord() > 0) {
				 * List<ScQtProposalChecklist> proposalCheckList = resDTO
				 * .getComplianceResultBean().getResultList(); String
				 * checklistScoring = ":Scoring is not completed"; for
				 * (ScQtProposalChecklist qtUploadProposalCheckList :
				 * proposalCheckList) { if (null == qtUploadProposalCheckList
				 * .getTotalUserScore() || qtUploadProposalCheckList
				 * .getTotalUserScore().intValue() <= 0) {
				 * this.getManagedBean().setEvaluatioStageActiveIndex( 0); throw
				 * new EpException(MessageConst.ERROR_760266, new String[] {
				 * checklist.getTitle(), checklistScoring }); }
				 * 
				 * }
				 * 
				 * }
				 */
				resDTO = evaluationSo.checkDemoAndUploadChecklistStatus(reqDTO);
				if (resDTO.getPendingCount() > 0) {
					String checklistScoring = ":Scoring is not completed";
					this.getManagedBean().setEvaluatioStageActiveIndex(0);
					throw new EpException(MessageConst.ERROR_760266,
							new String[] { checklist.getTitle(),
									checklistScoring });
				}
			}
		} finally {

		}

	}

	// retrieve upload qualified suppliers based on compliance stage or
	// technical stage or presentationdemo stage.
	private void retriveUploadCheckListFinancialSuppliers(
			ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();
		try {

			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				reqDTO.setCheckListId(checklist.getQtChecklistId());
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				// reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
					} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
						} else {
							reqDTO.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						}

					}
				} else {
					if (this.getManagedBean().getScQt().isDemoRequired()) {
						reqDTO.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
					} else {
						reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
					}
				}
				/*
				 * resDTO =
				 * evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO); if
				 * (null != resDTO && null != resDTO.getComplianceResultBean()
				 * && resDTO.getComplianceResultBean().getTotalRecord() > 0) {
				 * List<ScQtProposalChecklist> proposalCheckList = resDTO
				 * .getComplianceResultBean().getResultList(); String
				 * checklistScoring = ":Scoring is not completed"; for
				 * (ScQtProposalChecklist qtUploadProposalCheckList :
				 * proposalCheckList) { if (null == qtUploadProposalCheckList
				 * .getTotalUserScore() || qtUploadProposalCheckList
				 * .getTotalUserScore().intValue() <= 0) { if
				 * (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				 * .equalsIgnoreCase(this.getManagedBean() .getCommitteeType()))
				 * { if (!this.getManagedBean().getScQt()
				 * .isTechEvaluationReq()) { this.getManagedBean()
				 * .setEvaluatioStageActiveIndex(1); } else if
				 * (this.getManagedBean().getScQt() .isTechEvaluationReq()) {
				 * this.getManagedBean() .setOneTierFinancialActiveIndex(0);
				 * 
				 * } throw new EpException( MessageConst.ERROR_760267, new
				 * String[] { checklist.getTitle(), checklistScoring }); } else
				 * if
				 * (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				 * .equalsIgnoreCase(this.getManagedBean() .getCommitteeType()))
				 * { this.getManagedBean() .setEvaluatioStageActiveIndex(0); }
				 * throw new EpException(MessageConst.ERROR_760267, new String[]
				 * { checklist.getTitle(), checklistScoring }); }
				 * 
				 * }
				 * 
				 * }
				 */

				resDTO = evaluationSo.checkDemoAndUploadChecklistStatus(reqDTO);
				if (resDTO.getPendingCount() > 0) {
					String checklistScoring = ":Scoring is not completed";
					/*
					 * this.getManagedBean().setEvaluatioStageActiveIndex(0);
					 * throw new EpException(MessageConst.ERROR_760266, new
					 * String[] { checklist.getTitle(), checklistScoring });
					 */
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						if (!this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							this.getManagedBean().setEvaluatioStageActiveIndex(
									1);
						} else if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							this.getManagedBean()
									.setOneTierFinancialActiveIndex(0);

						}
						throw new EpException(MessageConst.ERROR_760267,
								new String[] { checklist.getTitle(),
										checklistScoring });
					} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
						this.getManagedBean().setEvaluatioStageActiveIndex(0);
						throw new EpException(MessageConst.ERROR_760267,
								new String[] { checklist.getTitle(),
										checklistScoring });
					}

				}
			}
		} finally {

		}

	}

	/**
	 * Sets the tab value.check current tab and next tab for chain.
	 * 
	 * @param currentTab
	 *            (Mandatory)
	 * @param outputTab
	 *            (Mandatory)
	 * @throws Exception
	 */
	public void evaluationMaintainPreviousNextTabIndex(int currentTab,
			int outputTab) throws Exception {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {

			// this.currentTab = currentTab;
			// this.outputTab = outputTab;
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			if (outputTab == 1) {
				this.getManagedBean().setCheckReportDownload(false);
				if (((ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType()) && !this.getManagedBean()
						.isReadOnly()) || (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType()) && !this.getManagedBean()
						.isReadOnly()))) {
					// checkMeetingDetailsAndStatus(this.getManagedBean()
					// .getScQtMeetings());
					checkCommitteeMembersAttendance(this.getManagedBean()
							.getCommitteeMemberList());
					if (currentTab == 0) {
						saveMeetingDtls(false);
					}
				}
				retriveComplianceTechnical();
				// retrives1Details();
			} else if (outputTab == 2) {
				this.getManagedBean().setDemo(false);
				this.getManagedBean().setCheckReportDownload(false);
				// this.getManagedBean().setCheckListType("");
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)) {
						complianceStageStatusCheck();
					}
					if (currentTab == 1) {
						if (!this.getManagedBean().getScQt().isSampleReq()) {
							if (this.getManagedBean()
									.getComplianceActiveIndex() < 2) {
								retriveComplianceSummary();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 1;
							}

						} else {
							sampleClosingDateCountDays();
							if (this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)) {

								if (this.getManagedBean().getCountDays() > 0
										|| (this.getManagedBean()
												.getCountDays() == 0 && this
												.getManagedBean()
												.getCurrentTime() < 12)) {
									throw new EpException(
											MessageConst.ERROR_760261);
								}
							}
							if (this.getManagedBean().getCountDays() < 0
									|| (this.getManagedBean().getCountDays() == 0 && this
											.getManagedBean().getCurrentTime() >= 12)) {
								if (this.getManagedBean()
										.getCurrentStatusId()
										.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)) {
									checkSampleComplianceCompleted();
								}
							}

							if (this.getManagedBean()
									.getComplianceActiveIndex() < 3) {
								// this.getManagedBean().setComplianceActiveIndex(3);
								retriveComplianceSummary();
								outputTab = 1;
							}
						}

					}

					if (!this.getManagedBean().isReadOnly()) {
						if (!this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
							if (null == this.getManagedBean().getScQt()
									.getFecEvaluationCheck()) {

								generateEvaluationStage();
								this.getManagedBean().getScQt()
										.setFecEvaluationCheck(2);

								EvaluationReqDTO reqDTO = new EvaluationReqDTO();
								reqDTO.setScQt(this.getManagedBean().getScQt());
								evalResDTO = evaluationSo
										.saveReportDtls(reqDTO);
								this.getManagedBean().setScQt(
										evalResDTO.getScQt());
								/*
								 * if (null != evalResDTO.getWorkFlowStatus()) {
								 * this.getManagedBean().setCurrentStatusId(
								 * evalResDTO.getWorkFlowStatus() .getPmStatus()
								 * .getStatusId()); }
								 */
							}
						} else if (this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {

							if (null == this.getManagedBean().getScQt()
									.getTecEvaluationCheck()) {

								generateEvaluationStage();
								EvaluationReqDTO reqDTO = new EvaluationReqDTO();
								this.getManagedBean().getScQt()
										.setTecEvaluationCheck(1);
								reqDTO.setScQt(this.getManagedBean().getScQt());
								evalResDTO = evaluationSo
										.saveReportDtls(reqDTO);
								this.getManagedBean().setScQt(
										evalResDTO.getScQt());
								/*
								 * if (null != evalResDTO.getWorkFlowStatus()) {
								 * this.getManagedBean().setCurrentStatusId(
								 * evalResDTO.getWorkFlowStatus() .getPmStatus()
								 * .getStatusId()); }
								 */
							}
						}
					}

				}

				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& !this.getManagedBean().isReadOnly()) {
						/*
						 * checkMeetingDetailsAndStatus(this.getManagedBean()
						 * .getScQtMeetings());
						 */
						checkCommitteeMembersAttendance(this.getManagedBean()
								.getCommitteeMemberList());
						if (currentTab == 0) {
							saveMeetingDtls(false);
						}
						// retriveTechnicalEvaluationStage();
					}
					/*
					 * if(!this.getManagedBean().isReadOnly()){
					 * generateEvaluationStage(); }
					 */
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& !this.getManagedBean().isReadOnly()) {
						if (null == this.getManagedBean().getScQt()
								.getTecEvaluationCheck()) {

							generateEvaluationStage();
							EvaluationReqDTO reqDTO = new EvaluationReqDTO();
							this.getManagedBean().getScQt()
									.setTecEvaluationCheck(1);
							reqDTO.setScQt(this.getManagedBean().getScQt());
							evalResDTO = evaluationSo.saveReportDtls(reqDTO);
							this.getManagedBean().setScQt(evalResDTO.getScQt());
						}
					}
					retriveTechnicalEvaluationStage();
				}

				if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (!this.getManagedBean().isReadOnly()) {
						/*
						 * checkMeetingDetailsAndStatus(this.getManagedBean()
						 * .getScQtMeetings());
						 */
						checkCommitteeMembersAttendance(this.getManagedBean()
								.getCommitteeMemberList());
						if (currentTab == 0) {
							saveMeetingDtls(false);
						}
					}
					/*
					 * if(!this.getManagedBean().isReadOnly()){
					 * generateEvaluationStage(); }
					 */
					if (!this.getManagedBean().isReadOnly()) {
						if (null == this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {

							generateEvaluationStage();
							this.getManagedBean().getScQt()
									.setFecEvaluationCheck(2);

							EvaluationReqDTO reqDTO = new EvaluationReqDTO();
							reqDTO.setScQt(this.getManagedBean().getScQt());
							evalResDTO = evaluationSo.saveReportDtls(reqDTO);
							this.getManagedBean().setScQt(evalResDTO.getScQt());
						}
					}
					retriveFinancialEvaluationStage();
				}

			} else if (outputTab == 3) {
				this.getManagedBean().setCheckReportDownload(false);
				// retrivePresetationDemoCheckList();
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()) {
						if (currentTab == 2) {

							if (this.getManagedBean()
									.getEvaluatioStageActiveIndex() < 1) {
								retriveSummaryEvaluationStage();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 2;

							}

						}
					}
					retrivePresentationDemo();
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()
							&& this.getManagedBean().getScQt().isDemoRequired()) {
						if (null == this.getManagedBean().getScQt()
								.getDemoNotificationCheck()) {
							EvaluationReqDTO demoReqDTO = new EvaluationReqDTO();
							demoReqDTO.setScQt(this.getManagedBean().getScQt());
							demoReqDTO
									.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							FacesScopes facesScopes = new FacesScopes();
							PmUser curLogedInUser = facesScopes
									.getUserSessionBean();
							demoReqDTO.setSenderId(curLogedInUser.getUserId());
							EvaluationResDTO demoResDTO = evaluationSo
									.sendDemoNotificationsForSuppliers(demoReqDTO);
							if (demoResDTO.isSuccessful()) {
								this.getManagedBean().getScQt()
										.setDemoNotificationCheck(1);
								EvaluationReqDTO reqDTO = new EvaluationReqDTO();
								reqDTO.setScQt(this.getManagedBean().getScQt());
								evalResDTO = evaluationSo
										.saveReportDtls(reqDTO);
								this.getManagedBean().setScQt(
										evalResDTO.getScQt());
							}
						}
					}
				} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()) {
						if (currentTab == 2) {

							if (this.getManagedBean()
									.getEvaluatioStageActiveIndex() < 1) {
								retriveSummaryEvaluationStage();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 2;

							}

						}
						if (null == this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {

							generateFinancialEvaluationresults();
							this.getManagedBean().getScQt()
									.setFecEvaluationCheck(2);
							EvaluationReqDTO reqDTO = new EvaluationReqDTO();
							reqDTO.setScQt(this.getManagedBean().getScQt());
							evalResDTO = evaluationSo.saveReportDtls(reqDTO);
							this.getManagedBean().setScQt(evalResDTO.getScQt());
						}
					}

					retriveFinancialEvaluationStage();

				}

			} else if (outputTab == 4) {
				this.getManagedBean().setCheckReportDownload(false);
				if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)) {
						complianceStageStatusCheck();
					}
					if (currentTab == 1) {
						if (!this.getManagedBean().getScQt().isSampleReq()) {
							if (this.getManagedBean()
									.getComplianceActiveIndex() < 2) {
								retriveComplianceSummary();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 1;
							}

						} else {
							sampleClosingDateCountDays();
							if (this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)) {
								if (this.getManagedBean().getCountDays() > 0
										|| (this.getManagedBean()
												.getCountDays() == 0 && this
												.getManagedBean()
												.getCurrentTime() < 12)) {
									throw new EpException(
											MessageConst.ERROR_760261);
								}
							}
							if (this.getManagedBean().getCountDays() < 0
									|| (this.getManagedBean().getCountDays() == 0 && this
											.getManagedBean().getCurrentTime() >= 12)) {
								if (this.getManagedBean()
										.getCurrentStatusId()
										.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)) {
									checkSampleComplianceCompleted();
								}
							}
							if (this.getManagedBean()
									.getComplianceActiveIndex() < 3) {
								// this.getManagedBean().setComplianceActiveIndex(3);
								retriveComplianceSummary();
								outputTab = 1;
							}
						}

					}
					if (!this.getManagedBean().isReadOnly()) {
						checkMeetingDetailsAndStatus(this.getManagedBean()
								.getScQtMeetings());
					}
				}

				else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						&& this.getManagedBean()
								.getCurrentStatusId()
								.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()) {
					// evaluationStageCheck();
					if (currentTab == 2) {

						if (this.getManagedBean()
								.getEvaluatioStageActiveIndex() < 2) {
							retriveOneTierWithoutTechnicalFinancialSupplierSummary();
							// this.getManagedBean().setComplianceActiveIndex(2);
							outputTab = 2;

						}

					}
					if (currentTab == 3) {

						if (this.getManagedBean()
								.getOneTierFinancialActiveIndex() < 1) {
							retriveOneTierFinancialSupplierSummary();
							// this.getManagedBean().setComplianceActiveIndex(2);
							outputTab = 3;

						}

					}
					checkMeetingDetailsAndStatus(this.getManagedBean()
							.getScQtMeetings());
				}

				else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						&& this.getManagedBean()
								.getCurrentStatusId()
								.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()) {

					if (!this.getManagedBean().getScQt().isDemoRequired()) {
						if (currentTab == 2) {

							if (this.getManagedBean()
									.getEvaluatioStageActiveIndex() < 1) {
								retriveSummaryEvaluationStage();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 2;

							}

						}
					} else {
						if (currentTab == 3) {
							if (this.getManagedBean()
									.getPresentationDemoActiveIndex() < 1) {
								retrivePresentationDemoSummary();
								outputTab = 3;
							}
						}
					}
					checkMeetingDetailsAndStatus(this.getManagedBean()
							.getScQtMeetings());
				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						&& this.getManagedBean()
								.getCurrentStatusId()
								.equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL)
						&& !this.getManagedBean().isReadOnly()) {
					if (currentTab == 2) {

						if (this.getManagedBean()
								.getEvaluatioStageActiveIndex() < 1) {
							retriveSummaryEvaluationStage();
							// this.getManagedBean().setComplianceActiveIndex(2);
							outputTab = 2;

						}

					}
					checkMeetingDetailsAndStatus(this.getManagedBean()
							.getScQtMeetings());
				}
				/*
				 * if
				 * (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				 * .equalsIgnoreCase(this.getManagedBean() .getCommitteeType())
				 * && this.getManagedBean() .getCurrentStatusId()
				 * .equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL) &&
				 * !this.getManagedBean().isReadOnly()) { if
				 * (this.getManagedBean().getScQt().isDemoRequired()) {
				 * saveMeetingDtls(false); retrivePresentationDemoSummary(); }
				 * 
				 * }
				 */

				retrieveReportTabDtl();
			} else if (outputTab == 5) {
				saveQtReportDtls();
				
				retriveCommitteeEndorsment();
				// RequestContext.getCurrentInstance().update("@form");
			} else if (outputTab == 6) {

			} else if (outputTab == 0) {
				this.getManagedBean().setCheckReportDownload(false);
				this.getManagedBean().setDemo(false);
				retriveMeetingDtls(this.getManagedBean().getScQt().getQtId(),
						this.getManagedBean().getCommitteeType(), 1);
			}

			if (getManagedBean().getTempTab() < outputTab) {
				getManagedBean().setTempTab(outputTab);
			}
			getManagedBean().setTabActiveIndex(outputTab);
		} finally {

		}
	}

	// check sample compliance completed or not
	private void checkSampleComplianceCompleted() {
		IQTEvaluationSO evaluationSO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO sampleReqDto = new EvaluationReqDTO();
			sampleReqDto.setQtId(this.getManagedBean().getScQt().getQtId());
			EvaluationResDTO sampleResDto = new EvaluationResDTO();
			sampleResDto = evaluationSO
					.checkSampleSubmissionStatus(sampleReqDto);
			if (sampleResDto.getSampleSubmissionPendingCount() > 0) {
				this.getManagedBean().setComplianceActiveIndex(1);
				throw new EpException(MessageConst.ERROR_760262);
			}

		} finally {

		}

	}

	// check meeting details status Done or not.

	private void checkMeetingDetailsAndStatus(
			List<ScQtMeeting> meetingDetailsList) {
		if (null == meetingDetailsList || meetingDetailsList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760253);
		} else {
			int i = 0;
			for (ScQtMeeting meeting : meetingDetailsList) {
				if (null != meeting.getMeetingStatus()
						&& null != meeting.getMeetingStatus().getStatusId()
						&& QTStatusConst.MS_DONE.equals(meeting
								.getMeetingStatus().getStatusId())) {
					i++;

				}
			}
			if (i == 0) {
				throw new EpException(MessageConst.ERROR_760255);
			}
		}

	}

	// check attendance for opening committee.
	private void checkCommitteeMembersAttendance(
			List<ScQtCommitteeActivity> committeeActivityList) {
		if (null != committeeActivityList && !committeeActivityList.isEmpty()) {
			List<ScQtCommitteeActivity> chairPersonList = new ArrayList<ScQtCommitteeActivity>();
			List<ScQtCommitteeActivity> secretaryList = new ArrayList<ScQtCommitteeActivity>();
			List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();
			for (ScQtCommitteeActivity committeeActivity : committeeActivityList) {
				if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON
						.equalsIgnoreCase(committeeActivity
								.getScCommitteeMember().getMemberRole()
								.getParameterCode())) {
					chairPersonList.add(committeeActivity);

				} else if (ParameterConst.COMMITTEE_ROLE_SECRETARY
						.equalsIgnoreCase(committeeActivity
								.getScCommitteeMember().getMemberRole()
								.getParameterCode())) {
					secretaryList.add(committeeActivity);

				} else if (ParameterConst.COMMITTEE_ROLE_MEMBER
						.equalsIgnoreCase(committeeActivity
								.getScCommitteeMember().getMemberRole()
								.getParameterCode())) {
					memberList.add(committeeActivity);

				}

			}
			if (!isAtleastOneChairPersonAttendance(chairPersonList)) {
				throw new EpException(MessageConst.ERROR_760252, new String[] {
						"ChairPerson",
						getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
								this.getManagedBean().getCommitteeType()) });
			}
			if (!isAtleastOneSecretaryAttendance(secretaryList)) {
				throw new EpException(MessageConst.ERROR_760252, new String[] {
						"Secretary",
						getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
								this.getManagedBean().getCommitteeType()) });
			}

			if (!isAtleastOneMemberAttendance(memberList)) {
				throw new EpException(MessageConst.ERROR_760252, new String[] {
						"Member",
						getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
								this.getManagedBean().getCommitteeType()) });
			}

		}

	}

	private boolean isAtleastOneChairPersonAttendance(
			List<ScQtCommitteeActivity> chairPersonList) {
		int i = 0;
		if (null != chairPersonList && !chairPersonList.isEmpty()) {
			for (ScQtCommitteeActivity committeeActivity : chairPersonList) {
				if (committeeActivity.isAttended()) {
					i++;
				}

			}
			if (i >= 1) {
				return true;
			}
		}

		return false;
	}

	private boolean isAtleastOneSecretaryAttendance(
			List<ScQtCommitteeActivity> secretaryList) {
		int i = 0;
		if (null != secretaryList && !secretaryList.isEmpty()) {
			for (ScQtCommitteeActivity committeeActivity : secretaryList) {
				if (committeeActivity.isAttended()) {
					i++;
				}

			}
			if (i >= 1) {
				return true;
			}
		}

		return false;
	}

	private boolean isAtleastOneMemberAttendance(
			List<ScQtCommitteeActivity> memberList) {
		int i = 0;
		if (null != memberList && !memberList.isEmpty()) {
			for (ScQtCommitteeActivity committeeActivity : memberList) {
				if (committeeActivity.isAttended()) {
					i++;
				}

			}
			if (i >= 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieve report tab dtl.Event handle set attachment for Reports tab.
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
			reqDTO.setCommType(EvaluationReport.getName(getManagedBean()
					.getCommitteeType()));
			// qtReqDTO.setQtId(qtId);
			resDTO = evaluationSO.retriveQtDtlsByRemarksAttachments(reqDTO);
			this.getManagedBean().setScQt(resDTO.getScQt());
			if (null == this.getManagedBean().getScQtEvaluationReportList()) {
				resDTO = evaluationSO.retriveReportDetails(reqDTO);
				if (null != resDTO.getReports()
						&& !resDTO.getReports().isEmpty()) {
					this.getManagedBean().setScQtEvaluationReportList(
							resDTO.getReports());
				}

			}
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScAttachment attachment = this.getManagedBean().getScQt()
						.getScEvaluationCommitteeReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-Evaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);
			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScAttachment attachment = this.getManagedBean().getScQt()
						.getScOpenCommitteeReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-Opening-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScAttachment attachment = this.getManagedBean().getScQt()
						.getScTechnicalEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-TechnicalEvaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScAttachment attachment = this.getManagedBean().getScQt()
						.getScFinancialEvaluationReportAttachment();
				this.getManagedBean().setReportDownloadFileName(
						"NGeP-QT-FinacialEvaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			}
		} finally {
			evaluationSO = null;
		}

	}

	/**
	 * Retrieve report header.set header for reports.
	 */
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

	/**
	 * Retrieve committee endorsment.Event handle display all the committee
	 * members list in committee endorsment tab.
	 * 
	 * @throws Exception
	 */
	public void retriveCommitteeEndorsment() throws Exception {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;
		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

			reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);

			this.getManagedBean().setEndorscommitteeMemberList(
					resDTO.getCommitteActivities());
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				qtReqDTO.setDocType("OER");

			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				qtReqDTO.setDocType("TOR");

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				qtReqDTO.setDocType("TTR");

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				qtReqDTO.setDocType("TFR");

			}

			// CR#7782 Commented as report attachment is not required to be generated
//			if (!this.getManagedBean().isReadOnly()) {
//				setReportAttachemnt();
//			}

			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (resDto.getRemarks() == null) {

					if (null == this.getManagedBean()
							.getEvaluationCommitteeRemark()) {
						this.getManagedBean().setEvaluationCommitteeRemark(
								new ArrayList<ScQtEvaluationCommitteeRemark>());
						initializeRemarks(this.getManagedBean().getScQt(),
								null, this.getManagedBean()
										.getEvaluationCommitteeRemark(),
								ScQtEvaluationCommitteeRemark.class);

					}
					this.getManagedBean()
							.getScQt()
							.setScQtEvaluationCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationCommitteeRemark());
				}

				else {

					List<ScQtEvaluationCommitteeRemark> remarkList = new ArrayList<ScQtEvaluationCommitteeRemark>();
					for (ScRemark remark : resDto.getRemarks()) {
						ScQtEvaluationCommitteeRemark qtRemark = (ScQtEvaluationCommitteeRemark) remark;
						remarkList.add(qtRemark);

					}
					if (null == this.getManagedBean()
							.getEvaluationCommitteeRemark()
							|| this.getManagedBean()
									.getEvaluationCommitteeRemark().isEmpty()) {
						this.getManagedBean().setEvaluationCommitteeRemark(
								remarkList);
						initializeRemarks(this.getManagedBean().getScQt(),
								null, this.getManagedBean()
										.getEvaluationCommitteeRemark(),
								ScQtEvaluationCommitteeRemark.class);
					}
					this.getManagedBean()
							.getScQt()
							.setScQtEvaluationCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationCommitteeRemark());
				}
			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (resDto.getRemarks() == null) {

					if (null == this.getManagedBean()
							.getEvaluationOpeningCommitteeRemark()) {
						this.getManagedBean()
								.setEvaluationOpeningCommitteeRemark(
										new ArrayList<ScQtOpeningCommitteeRemark>());
						initializeRemarks(this.getManagedBean().getScQt(),
								null, this.getManagedBean()
										.getEvaluationOpeningCommitteeRemark(),
								ScQtOpeningCommitteeRemark.class);

					}
					this.getManagedBean()
							.getScQt()
							.setScQtOpeningCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationOpeningCommitteeRemark());
				}

				else {

					List<ScQtOpeningCommitteeRemark> remarkList = new ArrayList<ScQtOpeningCommitteeRemark>();
					for (ScRemark remark : resDto.getRemarks()) {
						ScQtOpeningCommitteeRemark qtRemark = (ScQtOpeningCommitteeRemark) remark;
						remarkList.add(qtRemark);

					}
					if (null == this.getManagedBean()
							.getEvaluationOpeningCommitteeRemark()
							|| this.getManagedBean()
									.getEvaluationOpeningCommitteeRemark()
									.isEmpty()) {
						this.getManagedBean()
								.setEvaluationOpeningCommitteeRemark(remarkList);
						initializeRemarks(this.getManagedBean().getScQt(),
								null, this.getManagedBean()
										.getEvaluationOpeningCommitteeRemark(),
								ScQtOpeningCommitteeRemark.class);
					}
					this.getManagedBean()
							.getScQt()
							.setScQtOpeningCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationOpeningCommitteeRemark());
				}
			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (resDto.getRemarks() == null) {

					if (null == this.getManagedBean()
							.getEvaluationTechnicalCommitteeRemark()) {
						this.getManagedBean()
								.setEvaluationTechnicalCommitteeRemark(
										new ArrayList<ScQtTechnicalCommitteeRemark>());
						initializeRemarks(
								this.getManagedBean().getScQt(),
								null,
								this.getManagedBean()
										.getEvaluationTechnicalCommitteeRemark(),
								ScQtTechnicalCommitteeRemark.class);

					}
					this.getManagedBean()
							.getScQt()
							.setScQtTechnicalCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationTechnicalCommitteeRemark());

				}

				else {

					List<ScQtTechnicalCommitteeRemark> remarkList = new ArrayList<ScQtTechnicalCommitteeRemark>();
					for (ScRemark remark : resDto.getRemarks()) {
						ScQtTechnicalCommitteeRemark qtRemark = (ScQtTechnicalCommitteeRemark) remark;
						remarkList.add(qtRemark);

					}
					if (null == this.getManagedBean()
							.getEvaluationTechnicalCommitteeRemark()
							|| this.getManagedBean()
									.getEvaluationTechnicalCommitteeRemark()
									.isEmpty()) {
						this.getManagedBean()
								.setEvaluationTechnicalCommitteeRemark(
										remarkList);
						initializeRemarks(
								this.getManagedBean().getScQt(),
								null,
								this.getManagedBean()
										.getEvaluationTechnicalCommitteeRemark(),
								ScQtTechnicalCommitteeRemark.class);
					}
					this.getManagedBean()
							.getScQt()
							.setScQtTechnicalCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationTechnicalCommitteeRemark());
				}
			}
			if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				if (resDto.getRemarks() == null) {

					if (null == this.getManagedBean()
							.getEvaluationFinancialCommitteeRemark()) {
						this.getManagedBean()
								.setEvaluationFinancialCommitteeRemark(
										new ArrayList<ScQtFinancialCommitteeRemark>());
						initializeRemarks(
								this.getManagedBean().getScQt(),
								null,
								this.getManagedBean()
										.getEvaluationFinancialCommitteeRemark(),
								ScQtFinancialCommitteeRemark.class);

					}
					this.getManagedBean()
							.getScQt()
							.setScQtFinancialCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationFinancialCommitteeRemark());
				}

				else {

					List<ScQtFinancialCommitteeRemark> remarkList = new ArrayList<ScQtFinancialCommitteeRemark>();
					for (ScRemark remark : resDto.getRemarks()) {
						ScQtFinancialCommitteeRemark qtRemark = (ScQtFinancialCommitteeRemark) remark;
						remarkList.add(qtRemark);

					}
					if (null == this.getManagedBean()
							.getEvaluationFinancialCommitteeRemark()
							|| this.getManagedBean()
									.getEvaluationFinancialCommitteeRemark()
									.isEmpty()) {
						this.getManagedBean()
								.setEvaluationFinancialCommitteeRemark(
										remarkList);
						initializeRemarks(
								this.getManagedBean().getScQt(),
								null,
								this.getManagedBean()
										.getEvaluationFinancialCommitteeRemark(),
								ScQtFinancialCommitteeRemark.class);
					}
					this.getManagedBean()
							.getScQt()
							.setScQtFinancialCommitteeRemarks(
									this.getManagedBean()
											.getEvaluationFinancialCommitteeRemark());
				}
			}

		} finally {
			committeSO = null;

		}
	}

	/**
	 * Committee remarks save.to save the remarks in committee endorsment tab.
	 */
	public void committeeRemarksSave() {
		IQTEvaluationSO evaluationSo = null;
		try {

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				List<ScQtEvaluationCommitteeRemark> remarks = this
						.getManagedBean().getScQt()
						.getScQtEvaluationCommitteeRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							remarks.remove(i);
						}
					}
				}
				if (null != remarks && !remarks.isEmpty()) {
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evaluationSo.saveReportDtls(reqDTO);
				}
			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				List<ScQtOpeningCommitteeRemark> remarks = this
						.getManagedBean().getScQt()
						.getScQtOpeningCommitteeRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							remarks.remove(i);
						}
					}
				}
				if (null != remarks && !remarks.isEmpty()) {
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evaluationSo.saveReportDtls(reqDTO);
				}

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				List<ScQtTechnicalCommitteeRemark> remarks = this
						.getManagedBean().getScQt()
						.getScQtTechnicalCommitteeRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							remarks.remove(i);
						}
					}
				}
				if (null != remarks && !remarks.isEmpty()) {
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evaluationSo.saveReportDtls(reqDTO);
				}

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				List<ScQtFinancialCommitteeRemark> remarks = this
						.getManagedBean().getScQt()
						.getScQtFinancialCommitteeRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							remarks.remove(i);
						}
					}
				}
				if (null != remarks && !remarks.isEmpty()) {
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evaluationSo.saveReportDtls(reqDTO);
				}

			}

		} finally {
			evaluationSo = null;
		}
	}

	/**
	 * Update sm appl.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void updateSmAppl() throws Exception {
		try {
			// retrives2Details();
			// getManagedBean().setTabActiveIndex(outputTab);
			// RequestContext.getCurrentInstance().update("@form");

		} finally {

		}

	}

	/**
	 * Adds the meeting action.Event handle add empty row for data table in
	 * meeting details tab.
	 */
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

	/**
	 * Adds the turut details.add empty row for data table in turut hadir tab.
	 */
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

	/**
	 * Save meeting dtls.Event handle click save button in evaluation screen
	 * 
	 * @param save
	 *            (Mandatory)
	 */
	public void saveMeetingDtls(boolean save) {
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
		try {
			EvaluationReqDTO reqDtO = evaluationPreparation();
			boolean evaluationStatus = this.getManagedBean().isStatusOnly();
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long qtId = this.getManagedBean().getScQt().getQtId();
			int index = this.getManagedBean().getPrepareActiveIndex();
			int tabindex = this.getManagedBean().getTabActiveIndex();
			String committeeType = this.getManagedBean().getCommitteeType();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			String taskName = this.getManagedBean().getTaskName();
			boolean readoonly = this.getManagedBean().isReadOnly();
			int tempIndx = getManagedBean().getTempTab();
			boolean checkQualifiedSuppliers = this.getManagedBean()
					.isCheckQualifiedSuppliers();
			ScAttachment attachemnt = this.getManagedBean().getScAttachment();
			String reportFileName = this.getManagedBean()
					.getReportDownloadFileName();
			int countDays = this.getManagedBean().getCountDays();
			int currentTime = this.getManagedBean().getCurrentTime();
			String QtNum = this.getManagedBean().getScQt().getQtNo();
			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) this
					.getManagedBean().getPayLoad();

			if (!this.getManagedBean().isDemo()) {
				if (tabindex == 0) {
					evaluationSO.saveQTDtl(reqDtO);
					renewSesionObject();
				}
			} else {
				if (tabindex == 3
						&& this.getManagedBean().getScQt().isDemoRequired()
						&& this.getManagedBean()
								.getPresentationDemoActiveIndex() == 0) {
					savePresentationDemoDtls();
					renewSesionObject();
					retriveMeetingDtls(qtId, committeeType, index);
					retrivePresentationDemo();
				}
			}
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setTabActiveIndex(tabindex);
			this.getManagedBean().setCurrentStatusId(currentStatusId);
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setReadOnly(readoonly);
			this.getManagedBean().setTempTab(tempIndx);
			this.getManagedBean().setCheckQualifiedSuppliers(
					checkQualifiedSuppliers);
			this.getManagedBean().setScAttachment(attachemnt);
			this.getManagedBean().setReportDownloadFileName(reportFileName);
			this.getManagedBean().setCountDays(countDays);
			this.getManagedBean().setPayLoad(evaluationDataType);
			this.getManagedBean().setCurrentTime(currentTime);
			this.getManagedBean().setStatusOnly(evaluationStatus);
			retriveMeetingDtls(qtId, committeeType, index);
			// getStatusDropdownValues("SC", "MS");
			if (save == true) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004, new String[] {
												"Prepare Evaluation", QtNum },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {

		}

	}

	/**
	 * Gets the meeting to be remove list.
	 * 
	 * @return List<ScQtMeeting>
	 */
	private List<ScQtMeeting> getMeetingToBeRemoveList() {
		List<ScQtMeeting> toBeRemoveList = new ArrayList<ScQtMeeting>();

		List<ScQtMeeting> meetingRemoveList = this.getManagedBean()
				.getMeetingRemoveList();
		if (null != meetingRemoveList && meetingRemoveList.size() > 0) {
			toBeRemoveList.addAll(meetingRemoveList);
		}

		return toBeRemoveList;
	}

	/**
	 * Gets the attendance to be remove list.
	 * 
	 * @return the attendance to be remove list
	 */
	private List<ScQtMeetingAttendance> getAttendanceToBeRemoveList() {
		List<ScQtMeetingAttendance> toBeRemoveList = new ArrayList<ScQtMeetingAttendance>();

		List<ScQtMeetingAttendance> meetingAttendanceRemoveList = this
				.getManagedBean().getMeetingAttendanceRemoveList();
		if (null != meetingAttendanceRemoveList
				&& meetingAttendanceRemoveList.size() > 0) {
			toBeRemoveList.addAll(meetingAttendanceRemoveList);
		}

		return toBeRemoveList;
	}

	/**
	 * Evaluation preparation.to prepare all the evaluation tabs data for save
	 * or submit or accept buttons .
	 * 
	 * @return the evaluation req dto
	 */
	private EvaluationReqDTO evaluationPreparation() {
		EvaluationReqDTO reqDtO = new EvaluationReqDTO();
		try {

			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
			if (null != this.getManagedBean().getScQtMeetings()
					&& !this.getManagedBean().getScQtMeetings().isEmpty()) {
				for (ScQtMeeting meeting : this.getManagedBean()
						.getScQtMeetings()) {
					compareMeetingDates(meeting, localMeetingList);
				}
				qtCommittee.setScQtMeetings(this.getManagedBean()
						.getScQtMeetings());
			} else {
				qtCommittee.setScQtMeetings(null);
			}
			if (null != this.getManagedBean().getCommitteeMemberList()
					&& !this.getManagedBean().getCommitteeMemberList()
							.isEmpty()) {
				qtCommittee.setCommitteeActivities(this.getManagedBean()
						.getCommitteeMemberList());
			}
			if (null != getManagedBean().getMeetingAttendanceList()
					&& !getManagedBean().getMeetingAttendanceList().isEmpty()) {

				List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean()
						.getMeetingAttendanceList();
				if (CollectionUtils.isNotEmpty(meetingAttendanceList)) {
					if (meetingAttendanceList.size() > 1) {
						List<String> userIdentificationNos = new ArrayList<String>();
						for (ScQtMeetingAttendance meetingAttendance : meetingAttendanceList) {
							if (userIdentificationNos
									.contains(meetingAttendance.getIcPassport())) {
								// this.getManagedBean().setActiveIndex(o);
								// this.getManagedBean().setPrepareActiveIndex(2);
								throw new EpException(MessageConst.ERROR_760320);
							}
							userIdentificationNos.add(meetingAttendance
									.getIcPassport());
						}
					}
					qtCommittee.setMeetingAttendances(getManagedBean()
							.getMeetingAttendanceList());
				}

			} else {
				qtCommittee.setMeetingAttendances(null);
			}

			reqDtO.setRemovedmeetingList(getMeetingToBeRemoveList());

			reqDtO.setRemovedmeetingAttendanceList(getAttendanceToBeRemoveList());
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

	/**
	 * Save proposal score dtls.Event handle save all the suppliers scores in
	 * evaluation stage.
	 */
	public void saveProposalScoreDtls() {
		try {

			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);
			// int index = this.getManagedBean().getTechindex();
			if (null != this.getManagedBean().getNoOfContractsProposalScore()) {
				ScQtProposalScore contract = this.getManagedBean()
						.getNoOfContractsProposalScore();
				if (null != contract.getUserScore()) {
					if (null != this.getManagedBean().getUserScores()) {
						if (contract.getUserScore().intValue() > new Integer(
								getManagedBean().getUserScores())) {
							throw new EpException(MessageConst.ERROR_760201);
						}
					}
				}

			}
			if (null != this.getManagedBean().getNoOfyearsProposalScore()) {
				ScQtProposalScore contract = this.getManagedBean()
						.getNoOfyearsProposalScore();
				if (null != contract.getUserScore()) {
					if (null != this.getManagedBean().getContractScores()) {
						if (contract.getUserScore().intValue() > new Integer(
								getManagedBean().getContractScores())) {
							throw new EpException(MessageConst.ERROR_760201);
						}
					}
				}

			}
			ScQtProposalChecklist qtProposalChecklist = getManagedBean()
					.getQtProposalChecklist();
			EvaluationReqDTO reqDtO = new EvaluationReqDTO();
			reqDtO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDtO.setProposalChecklist(qtProposalChecklist);
			reqDtO.setPrpExperienceList(this.getManagedBean()
					.getPrpCompanyExperienceList());
			List<ScQtCheckList> qtCheckLists = null;
			if ("T".equalsIgnoreCase(getManagedBean().getCheckListType())) {
				reqDtO.setEvalStage(getEvaluationStageForTechnicalChecklist());

				qtCheckLists = getManagedBean()
						.getEvaluationStgTechnicalCheckList();
			} else {
				reqDtO.setEvalStage(getEvaluationStageForFinancialChecklist());

				qtCheckLists = getManagedBean()
						.getEvaluationStgfinancialCheckList();
			}
			EvaluationResDTO resDto = evaluationSO.saveProposalDetails(reqDtO);

			List<ScQtProposalChecklist> proposalCheckList = this
					.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();
			int index = proposalCheckList.indexOf(qtProposalChecklist);
			// proposalCheckList.remove(index);
			qtProposalChecklist = resDto.getProposalChecklist();
			getManagedBean().setQtProposalChecklist(qtProposalChecklist);
			proposalCheckList.set(index, this.getManagedBean()
					.getQtProposalChecklist());

			if (qtCheckLists != null) {
				updateCheckListIntoQtCheckLists(qtCheckLists,
						qtCheckLists.indexOf(getManagedBean()
								.getScQtChecklist()), resDto.getCheckList());
			}
			if (SourcingConstants.TECHNICAL_DOCTYPE.equals(qtProposalChecklist
					.getScQtCheckList().getDocCategory())) {
				getManagedBean().setTechnicalSummaryGenerated(false);
				getManagedBean().setPresentationDemoSummaryGenerated(false);
				technicalSupplierEvaluate(getManagedBean()
						.getQtProposalChecklist(), getManagedBean()
						.getCheckListType());
				checkDemoStatusForQtCheckList();
				evaluationTechnicalChangeScQtStatusUpdate();
			} else {
				getManagedBean().setFinancialSummaryGenerated(false);
				financialSupplierEvaluate(getManagedBean()
						.getQtProposalChecklist(), getManagedBean()
						.getCheckListType());
			}
			updateSummaryGeneratedStatus();

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Scores Saved Successfully",
							"Evaluation Proposal Scores Saved Successfully"));
		} finally {

		}

	}

	// this method to set downaloadall reports file name and save file name in
	// db;
	private void setReportAttachemnt() throws Exception {
		IQTEvaluationSO evaluationSO = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			this.getManagedBean().setCheckReportDownload(true);
			ScAttachment scAttachment = this.getManagedBean().getScAttachment();
			ScQt scQt = this.getManagedBean().getScQt();

			// scQtEvaluationReport.setScQt(this.getManagedBean().getScQt());
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScEvaluationCommitteeReportAttachment attachment = scQt
						.getScEvaluationCommitteeReportAttachment();
				attachment = new ScEvaluationCommitteeReportAttachment();
				attachment.setScQt(scQt);
				scQt.setScEvaluationCommitteeReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScQtEvaluationReportAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScEvaluationCommitteeReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				EvaluationResDTO evalResDTO = evaluationSO
						.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScEvaluationCommitteeReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
				// RequestContext.getCurrentInstance().update("@form");
			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScOpenCommitteeReportAttachment attachment = scQt
						.getScOpenCommitteeReportAttachment();
				attachment = new ScOpenCommitteeReportAttachment();
				attachment.setScQt(scQt);
				scQt.setScOpenCommitteeReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScQtEvaluationReportAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScOpenCommitteeReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				EvaluationResDTO evalResDTO = evaluationSO
						.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScOpenCommitteeReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScTechnicalEvaluationReportAttachment attachment = scQt
						.getScTechnicalEvaluationReportAttachment();
				attachment = new ScTechnicalEvaluationReportAttachment();
				attachment.setScQt(scQt);
				scQt.setScTechnicalEvaluationReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScQtEvaluationReportAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScTechnicalEvaluationReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				EvaluationResDTO evalResDTO = evaluationSO
						.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScTechnicalEvaluationReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScFinancialEvaluationReportAttachment attachment = scQt
						.getScFinancialEvaluationReportAttachment();
				attachment = new ScFinancialEvaluationReportAttachment();
				attachment.setScQt(scQt);
				scQt.setScFinancialEvaluationReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScQtEvaluationReportAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScFinancialEvaluationReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				EvaluationResDTO evalResDTO = evaluationSO
						.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScFinancialEvaluationReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			}

		} finally {

		}

	}

	private List<ScQtEvaluationReportAttachment> convertReportAsReportAttachment(
			List<ScQtEvaluationReport> evaluationReports) throws Exception {
		List<ScQtEvaluationReportAttachment> attachments = new ArrayList<ScQtEvaluationReportAttachment>();
		try {
			if (null != evaluationReports) {
				for (ScQtEvaluationReport scQtEvaluationReport : evaluationReports) {
					ScQtEvaluationReportAttachment reportAttachment = scQtEvaluationReport
							.getScQtEvaluationReportAttachment();
					if (reportAttachment == null) {
						reportAttachment = generateDocumentAttachment(scQtEvaluationReport);
						scQtEvaluationReport
								.setScQtEvaluationReportAttachment(reportAttachment);
					}
					attachments.add(reportAttachment);
				}
			}

		} finally {

		}
		return attachments;
	}

	private void convertmultipleFilesAsaZipFile(
			List<ScQtEvaluationReportAttachment> scAttachments,
			ScAttachment attachemnt) throws IOException {
		List<File> files = new ArrayList<File>();
		try {
			for (ScAttachment scAttachment : scAttachments) {
				if (null != scAttachment
						&& StringUtils.isNotBlank(scAttachment.getFilePath())) {
					files.add(new File(getActualFilePath(scAttachment)));
				}
			}

			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
					getActualFilePath(attachemnt)));
			try {
				for (File file : files) {

					zos.putNextEntry(new ZipEntry(file.getName()));

					// Get the file
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException fnfe) {
						zos.write(("ERROR: Could not find file " + file
								.getName()).getBytes());
						zos.closeEntry();
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
				}
			} catch (Exception ex) {

			} finally {

				zos.close();

				// response.flushBuffer();

				// FacesContext.getCurrentInstance().responseComplete();
			}
		} finally {

		}
	}

	/**
	 * Pre sentation demo file upload.to upload files for all the technical
	 * qualified suppliers in presentation demo tab
	 * 
	 * @param event
	 *            (Mandatory)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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

	/**
	 * demo checklist upload.upload file for presentation demo checklist in
	 * presntation demo tab.
	 * 
	 * @param event
	 *            (Mandatory)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void demoChecklistUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		IQTEvaluationSO evaluationSO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			ScQtCheckList scQtChecklist = this.getManagedBean()
					.getScQtChecklist();
			/*
			 * if (null == scQtChecklist) { scQtChecklist = new ScQtCheckList();
			 * scQtChecklist.setScQt(this.getManagedBean().getScQt());
			 * setUserDetails(scQtChecklist); }
			 */

			ScQtCheckListAttachment attachment = scQtChecklist
					.getScQtCheckListAttachment();
			if (null == attachment) {
				attachment = new ScQtCheckListAttachment();
				attachment.setScQtCheckList(scQtChecklist);
			}
			attachment.setDocNo(scQtChecklist.getScQt().getQtNo());

			uploadAttachment(event.getFile(), attachment);

			scQtChecklist.setScQtCheckListAttachment(attachment);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setCheckList(scQtChecklist);

			EvaluationResDTO resDTO = evaluationSO
					.saveOrUpdatePresentationDemoCheckList(reqDTO);

			this.getManagedBean().setScQtChecklist(resDTO.getCheckList());
		} finally {

		}

	}

	/**
	 * Download documents.Event handle click download on uploaded checklist.
	 * 
	 * @param proposalCheckList
	 *            (Mandatory)
	 */
	public void downloadDocuments(ScQtProposalChecklist proposalCheckList) {
		try {

			downloadFile(proposalCheckList.getScQtProposalChecklistAttachment());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Secretary submit task.
	 */
	public void secretarySubmitTask() {
		ICommitteSO committeSO = null;

		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
			reqDTO.setSubject(this.getManagedBean().getSubject());
			reqDTO.setBodyText(this.getManagedBean().getMessage());
			committeSO.sendCommitteEndorseNotification(reqDTO);

		} finally {

		}
	}

	/**
	 * Save templeate spec dtls.Event handle technical and financial specs
	 * suppliers save in evaluation stage.
	 * 
	 * @return List<TplSpecAnswer>
	 */
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
			evalReqDTO.setProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			evalReqDTO.setAnswersList(answers);
			evalReqDTO.setChecklistType(getManagedBean().getCheckListType());
			List<ScQtCheckList> qtCheckLists = null;
			if ("T".equalsIgnoreCase(getManagedBean().getCheckListType())) {
				evalReqDTO
						.setEvalStage(getEvaluationStageForTechnicalChecklist());

				qtCheckLists = getManagedBean()
						.getEvaluationStgTechnicalCheckList();
			} else {
				evalReqDTO
						.setEvalStage(getEvaluationStageForFinancialChecklist());

				qtCheckLists = getManagedBean()
						.getEvaluationStgfinancialCheckList();
			}
			evalResDTO = evaluationSO.saveSpecDetails(evalReqDTO);

			if (QTConstants.TECHNICAL_SPEC_TYPE
					.equalsIgnoreCase(getManagedBean().getCheckListType())) {
				List<ScQtProposalChecklist> technicalSpes = this
						.getManagedBean()
						.getComplianceTechnicalProposalCheckListDM().getList();
				int tecindex = technicalSpes.indexOf(this.getManagedBean()
						.getQtProposalChecklist());
				technicalSupplierEvaluate(evalResDTO.getProposalChecklist(),
						getManagedBean().getCheckListType());

				// technicalSpes.remove(index);
				this.getManagedBean().setQtProposalChecklist(
						evalResDTO.getProposalChecklist());
				technicalSpes.set(tecindex, this.getManagedBean()
						.getQtProposalChecklist());
				// evaluateTechnicalEvaluation(checklist)

				getManagedBean().setTechnicalSummaryGenerated(false);
				getManagedBean().setPresentationDemoSummaryGenerated(false);
				updateSummaryGeneratedStatus();
				checkDemoStatusForQtCheckList();
				evaluationTechnicalChangeScQtStatusUpdate();
			}
			if (QTConstants.FINANCIAL_SPEC_TYPE
					.equalsIgnoreCase(getManagedBean().getCheckListType())) {
				List<ScQtProposalChecklist> financialSpes = this
						.getManagedBean().getFinSpecSupplierListDM().getList();
				int finindex = financialSpes.indexOf(this.getManagedBean()
						.getQtProposalChecklist());
				financialSupplierEvaluate(evalResDTO.getProposalChecklist(),
						getManagedBean().getCheckListType());
				this.getManagedBean().setQtProposalChecklist(
						evalResDTO.getProposalChecklist());
				// this.getManagedBean().getFinSpecSupplierList().remove(index);

				financialSpes.set(finindex, this.getManagedBean()
						.getQtProposalChecklist());

				getManagedBean().setFinancialSummaryGenerated(false);
				updateSummaryGeneratedStatus();
			}

			if (qtCheckLists != null) {
				updateCheckListIntoQtCheckLists(qtCheckLists,
						qtCheckLists.indexOf(getManagedBean()
								.getScQtChecklist()), evalResDTO.getCheckList());
			}

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

	/**
	 * Revise proposal evaluation.Event handle when member revise in evaluation.
	 * 
	 * @return the string
	 */
	public String reviseProposalEvaluation() {
		EvaluationResDTO resDTO = updateEvaluationCommitteDtls(this
				.getManagedBean().getLocalOutCome(), null);
		if (resDTO.isSuccessful()) {
			ScQtCommittee qtCommittee = resDTO.getQtCommittee();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															"Prepare Evaluation Submitted Successfully",
															qtCommittee
																	.getScQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	/**
	 * Submit prepare evaluation details.Event handle to submit data for bpm.
	 * 
	 * @param outCome
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public String submitPrepareEvaluationDetails(String outCome) {

		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO.setCommitteeType(this.getManagedBean()
				.getCommitteeType());
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		String roleCode = member.getMemberRole().getParameterCode();
		this.getManagedBean().setRoleCode(roleCode);
		if (roleCode
				.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			this.getManagedBean().setLocalOutCome(outCome);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		} else {
			this.getManagedBean().setLocalOutCome(outCome);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outCome)) {
				/*
				 * retriveCommitteeEndorsment(); List<ScQtSpecificationRemark>
				 * remarks = this.getManagedBean() .getSpecificationRemarks();
				 * if (null != remarks) { for (int i = 0; i < remarks.size();
				 * i++) { String userRemark = remarks.get(i).getRemark(); if
				 * (null == userRemark || "".equals(userRemark)) { throw new
				 * EpException(MessageConst.ERROR_760154); } } }
				 */

				RequestContext.getCurrentInstance().execute(
						"reviceApprovalDlgs.show()");
			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outCome)
					|| BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outCome)) {

				EvaluationResDTO resDTO = updateEvaluationCommitteDtls(outCome,
						null);
				if (resDTO.isSuccessful()) {
					ScQtCommittee qtCommittee = resDTO.getQtCommittee();
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															getMessgeConstant(outCome),
															new String[] {
																	"Prepare Evaluation",
																	qtCommittee
																			.getScQt()
																			.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			}
		}

		return null;

	}

	/**
	 * Update evaluation committe dtls.
	 * 
	 * @param action
	 *            (Mandatory)
	 * @param digiSign
	 *            (optional)
	 * @return EvaluationResDTO (boolean successful,ScQtCommittee committee)
	 */
	private EvaluationResDTO updateEvaluationCommitteDtls(String action,
			PmDigiSign digiSign) {
		IQTEvaluationSO evaluationso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
		committeeRemarksSave();
		EvaluationReqDTO reqDtO = evaluationPreparation();
		ScQt scQt = this.getManagedBean().getScQt();
		reqDtO.setOutcome(action);
		if (null != digiSign) {
			updatePmDigiSignData(digiSign, this.getManagedBean()
					.getCommitteeType(), scQt.getQtId(), scQt.getQtNo());
		}
		reqDtO.setCommType(this.getManagedBean().getCommitteeType());
		reqDtO.setDigiSignData(digiSign);
		reqDtO.setTaskId(this.getManagedBean().getTaskId());
		reqDtO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		reqDtO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDtO.setPmUser(facesScopes.getUserSessionBean());
		reqDtO.setRoleCode(this.getManagedBean().getRoleCode());
		EvaluationResDTO resDTO = null;
		try {
			resDTO = evaluationso.updateBPMAction(reqDtO);

		} finally {
			evaluationso = null;
		}
		return resDTO;
	}

	/**
	 * Gpki sign action.Event handle when chairperson submit.
	 * 
	 * @param digiSign
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public String gpkiSignAction(PmDigiSign digiSign) {
		EvaluationResDTO resDTO = updateEvaluationCommitteDtls(
				BpmOutcomeConst.BPM_SUBMIT, digiSign);
		if (resDTO.isSuccessful()) {
			ScQtCommittee qt = resDTO.getQtCommittee();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(BpmOutcomeConst.BPM_SUBMIT),
													new String[] {
															"Prepare Evaluation",
															qt.getScQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	/*
	 * 
	 * preRenderView(). user click on task list navigate to evaluation screens
	 * for(EC/OC/TEC/FEC).
	 */
	public void preRenderView() {

		SCEvaluationDataType evaluationDataType = getBpmTaskDetails(
				SCEvaluationDataType.class, null);
		FacesScopes facesScopes = new FacesScopes();

		// FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();

		retriveMeetingDtls(evaluationDataType.getQtId().longValue(),
				evaluationDataType.getCommitteeType(), 1);
		String taskName = this.getManagedBean().getTaskName();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO.setCommitteeType(this.getManagedBean()
				.getCommitteeType());
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		if (null != member) {
			if (member.getMemberRole().getParameterCode()
					.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {

				this.getManagedBean().setReadOnly(false);
				this.getManagedBean().setMemberOnly(false);

			} else if (member.getMemberRole().getParameterCode()
					.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
				this.getManagedBean().setReadOnly(true);
				this.getManagedBean().setMemberOnly(false);

			} else if (member
					.getMemberRole()
					.getParameterCode()
					.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				this.getManagedBean().setReadOnly(true);
				this.getManagedBean().setMemberOnly(true);
			}
		}
		if (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL.equals(this
				.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())) {
			this.getManagedBean().setStatusOnly(false);

		} else {
			this.getManagedBean().setStatusOnly(true);
		}

		this.getManagedBean().setPrepareActiveIndex(0);
		if (SCProcessNameConst.FINALISE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		} else if (SCProcessNameConst.FINALISE_OC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_OC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		} else if (SCProcessNameConst.FINALISE_TEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}

		else if (SCProcessNameConst.FINALISE_FEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_FEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		} else if (SCProcessNameConst.ENDORSE_OC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_OC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}

		else if (SCProcessNameConst.ENDORSE_TEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		} else if (SCProcessNameConst.ENDORSE_FEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_FEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}

		else if (SCProcessNameConst.ENDORSE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_EC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}
	}

	/**
	 * Download report.
	 * 
	 * @param scQtEvaluationReport
	 *            (Mandatory)
	 * @throws Exception
	 *             the exception
	 */
	public void downloadReport() throws Exception {
		debugLogger("downloadReport");
		ScQtEvaluationReportAttachment evalReportAttachment = 
											generateDocumentAttachment();
		saveQtReportDtls();
		downloadFile(evalReportAttachment);
	}

	/**
	 * Generate document attachment.generate all evaluation reports.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the sc qt evaluation report attachment
	 * @throws Exception
	 *             the exception
	 */
	private ScQtEvaluationReportAttachment generateDocumentAttachment(
			ScQtEvaluationReport scQtEvaluationReport) throws Exception {
		debugLogger("generateDocumentAttachment");
		String definitionType = scQtEvaluationReport.getDefinationType()
				.getParameterCode();
		ScQtEvaluationReportAttachment scQtEvaluationReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			String docDefinitionKey = scQtEvaluationReport.getPmDocument()
					.getDocDefinitionKey();
			scQtEvaluationReportAttachment = new ScQtEvaluationReportAttachment();
			scQtEvaluationReportAttachment
					.setScQtEvaluationReport(scQtEvaluationReport);
			scQtEvaluationReportAttachment.setDocNo(scQtEvaluationReport
					.getScQt().getQtNo());

			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtEvaluationReport.getTitle().replaceAll("\\s",
					"_"));

			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateCoverPdfFromVelocity(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_MAKLUMAT_TENDER_HARGA
					.equals(docDefinitionKey)) {
				// filename.append(".pdf");
				// scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateMaklumatTenderHargaPdfFromVelocity(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_MEETING_DTLS
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateMeetingDetailsPdfFromVelocity(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_COMMITTEE_DTLS
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateCommitteeDetailsPdfFromVelocity(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_SHORTLISTED
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generatePematuhan1Pdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_ASSESSMENT_METHOD
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateKaedah1Pdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_SHORTLISTED
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generatePematuhan2Pdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_ASSESSMENT_METHOD
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateKaedah2Pdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_ASSESSMENT_METHOD_FIN
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateFinancialKaedah2Pdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_TECH_FIN_EVALUATION
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateAnalisisKeputusanPenilaianTeknikalPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_TECH_SPECIFICATION
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateTeknikalSpecificationPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_EVAL_REPORT
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateEvaluationPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_HARGA_TAWARAN
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateHargaTawaranPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_OVERALL_FIN_RESULT
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateOverallFinancialResultPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_OVERALL_TECH_RESULT
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateOverallTechnicalResultPdf(
						(ScAttachment) scQtEvaluationReportAttachment,
						getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_USER_SCORE_SCHEMA
					.equals(docDefinitionKey)) {
				if (null != getManagedBean().getScQt()
						.getTechnicalUserScoringSchemaAttachment()) {
					filename = new StringBuilder();
					filename.append(getManagedBean().getScQt()
							.getTechnicalUserScoringSchemaAttachment()
							.getFileName().replaceAll("\\s", "_"));
					// filename.append(".pdf");
					scQtEvaluationReportAttachment.setFileName(filename
							.toString());
					scQtEvaluationReportAttachment.setFilePath(getManagedBean()
							.getScQt()
							.getTechnicalUserScoringSchemaAttachment()
							.getFilePath());
				}
				// downloadFile(scQtEvaluationReportAttachment);
				// downloadLocalFile(scQtEvaluationReportAttachment);
			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_USER_SCORE_SCHEMA
					.equals(docDefinitionKey)) {
				if (null != getManagedBean().getScQt()
						.getFinancialUserScoringSchemaAttachment()) {
					filename = new StringBuilder();
					filename.append(getManagedBean().getScQt()
							.getFinancialUserScoringSchemaAttachment()
							.getFileName().replaceAll("\\s", "_"));
					// filename.append(".pdf");
					scQtEvaluationReportAttachment.setFileName(filename
							.toString());
					scQtEvaluationReportAttachment.setFilePath(getManagedBean()
							.getScQt()
							.getFinancialUserScoringSchemaAttachment()
							.getFilePath());
				}
				// downloadFile(scQtEvaluationReportAttachment);
				// downloadLocalFile(scQtEvaluationReportAttachment);
			}
			scQtEvaluationReport
					.setScQtEvaluationReportAttachment(scQtEvaluationReportAttachment);
		} else {
			String docDefinitionKey = scQtEvaluationReport.getPmDocument()
					.getDocDefinitionKey();
			scQtEvaluationReportAttachment = new ScQtEvaluationReportAttachment();
			scQtEvaluationReportAttachment
					.setScQtEvaluationReport(scQtEvaluationReport);
			scQtEvaluationReportAttachment.setDocNo(scQtEvaluationReport
					.getScQt().getQtNo());
			if (SourcingConstants.DOC_DEF_KEY_QT_INFO.equals(docDefinitionKey)) {
				generateSebutHargaPdfFromVelocity(
						(ScAttachment) scQtEvaluationReportAttachment,
						scQtEvaluationReport);
			}
			scQtEvaluationReport
					.setScQtEvaluationReportAttachment(scQtEvaluationReportAttachment);
		}
		return scQtEvaluationReportAttachment;
	}
	
	private ScQtEvaluationReportAttachment generateDocumentAttachment() throws Exception {
		debugLogger("generateDocumentAttachment");
		
		ScQtEvaluationReportAttachment reportAttachment = 
							new ScQtEvaluationReportAttachment();
		StringBuilder fileName = new StringBuilder(
				getManagedBean().getCommitteeType() + "_Report");
		fileName.append(".pdf");
		reportAttachment.setFileName(fileName.toString());
		generateEvaluationPdf(
				(ScAttachment) reportAttachment,
				getManagedBean().getScQt());
		
		return reportAttachment;
	}
	
	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateFinancialKaedah2Pdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generateFinancialKaedah2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_GEN_SPEC_FIN_4D;
			reportId = ReportConst.TECSPEC_EC_RPT_VAL; // SC_027
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate maklumat tender pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @throws Exception
	 *             the exception
	 */
	private void generateSebutHargaPdfFromVelocity(ScAttachment attachment,
			ScQtEvaluationReport scQtEvaluationReport) throws Exception {
		debugLogger("generateSebutHargaPdfFromVelocity");
		attachment.setFileName(SourcingConstants.DOCNAME_EV_QT_INFO);

		TemplateDocBackingBean backingBean = new TemplateDocBackingBean();

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

		backingBean.goToMaint(docMaster);
		backingBean.previewAction();
		String html = backingBean.getManagedBean().getPreviewValue();

		/*
		 * String html = generateSebutHargaFromVelocity(scQtEvaluationReport,
		 * getManagedBean().getScQt());
		 */
		populateAttachmentDetails(attachment);

		DocumentGenerator.getInstance().generatePdf(
				new FileOutputStream(getActualFilePath(attachment)), html);
	}

	/**
	 * Generate maklumat tender from velocity.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @param scQt
	 *            the sc qt
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	/*
	 * private String generateSebutHargaFromVelocity( ScQtEvaluationReport
	 * scQtEvaluationReport, ScQt scQt) throws Exception {
	 * debugLogger("generateSebutHargaFromVelocity");
	 * 
	 * TemplateDocBackingBean backingBean = new TemplateDocBackingBean();
	 * 
	 * ScQtEvaluationReport qtEvaluationReport =
	 * retriveRulesAndRegulationReport(scQtEvaluationReport); if
	 * (qtEvaluationReport != null) { scQtEvaluationReport = qtEvaluationReport;
	 * } else { scQtEvaluationReport = saveScQtReportList(scQtEvaluationReport);
	 * }
	 * 
	 * TplDocMaster docMaster = scQtEvaluationReport.getTplDocMaster(); if
	 * (docMaster == null) { // clone standard tplDocMaster TplDocMaster
	 * tplDocMaster = cloneTplDocMasterDtls(scQtEvaluationReport);
	 * scQtEvaluationReport.setTplDocMaster(tplDocMaster);
	 * 
	 * // save clone to db docMaster =
	 * saveClonedTplDocMaster(scQtEvaluationReport); }
	 * 
	 * backingBean.goToMaint(docMaster); backingBean.previewAction(); String
	 * html = backingBean.getManagedBean().getPreviewValue();
	 * 
	 * CommonUtils commonUtils = new CommonUtils(); SimpleDateFormat dateSDF =
	 * new SimpleDateFormat( commonUtils
	 * .getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
	 * DecimalFormat amountDF = new DecimalFormat( commonUtils
	 * .getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
	 * 
	 * StringBuilder openType = new StringBuilder(); if (openType != null) {
	 * openType.append(getParameterDesc(
	 * ParameterTypeConst.QUOTATION_TENDER_TYPE, scQt.getQtType())); }
	 * 
	 * String estimatedPrice = ""; if (scQt.getEstimatedAmount() != null) {
	 * estimatedPrice = amountDF.format(scQt.getEstimatedAmount()); }
	 * 
	 * String publishDate = ""; if (scQt.getPublishDate() != null) { publishDate
	 * = dateSDF.format(scQt.getPublishDate()); }
	 * 
	 * String closingDate = ""; if (scQt.getClosingDate() != null) { closingDate
	 * = dateSDF.format(scQt.getClosingDate()); } String totalSuppliers = ""; if
	 * (scQt.getScQtSuppliers() != null) { totalSuppliers =
	 * String.valueOf(scQt.getScQtSuppliers().size()); }
	 * 
	 * html = html.replaceAll("&lt;&lt;QT_TYPE&gt;&gt;", openType.toString());
	 * html = html.replaceAll("&lt;&lt;ESTIMATED_PRICE&gt;&gt;",
	 * estimatedPrice); html = html.replaceAll("&lt;&lt;PUBLISH_DATE&gt;&gt;",
	 * publishDate); html = html.replaceAll("&lt;&lt;CLOSING_DATE&gt;&gt;",
	 * closingDate); html = html .replaceAll("&lt;&lt;NO_OF_SUPPLIER&gt;&gt;",
	 * totalSuppliers); return html; }
	 */

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateOverallTechnicalResultPdf(ScAttachment attachment,
			ScQt qt) throws Exception {
		debugLogger("generateFinancialEvaluationReportPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {

			reportName = ReportConst.SC_EC_OVERALLTECH_WITH_WEI_4H;
			reportId = ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL; // SC_035

		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_TEC_OVERALL_WITH_WEI_2F;
			reportId = ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL; // SC_012
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/*
	 * private void generateFinancialEvaluationReportPdf(ScAttachment
	 * attachment, ScQt qt) throws Exception {
	 * debugLogger("generateFinancialEvaluationReportPdf");
	 * populateAttachmentDetails(attachment);
	 * 
	 * FacesScopes facesScopes = new FacesScopes(); long qtId = qt.getQtId();
	 * String reportId = ""; String reportName = "";
	 * 
	 * if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
	 * .equals(this.getManagedBean().getCommitteeType())) { reportName =
	 * ReportConst.SC_FEC_OTHER_CRITERIA; reportId =
	 * ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL; // SC_018 }
	 * 
	 * Map<String, String> paramMap = new HashMap<String, String>();
	 * paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
	 * paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
	 * .getUserSessionBean().getUserId() + "");
	 * paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");
	 * 
	 * generatePdfFromOracle(reportName, paramMap, attachment, this
	 * .getManagedBean().isCheckReportDownload()); }
	 */

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateOverallFinancialResultPdf(ScAttachment attachment,
			ScQt qt) throws Exception {
		debugLogger("generateOverallFinancialResultPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_OVERALL_FIN_4I;
			reportId = ReportConst.OVRL_FINRS_EC_RPT_VAL; // SC_036

		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_OVERALL_FIN_3E;
			reportId = ReportConst.OVRL_FINRS_FEC_RPT_VAL; // SC_020
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateHargaTawaranPdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generateHargaTawaranPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_PRICEOFFER_4M;
			reportId = ReportConst.HARGA_TAWARAN_EC_RPT_VAL; // SC_030
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_PRICEOFFER_3D;
			reportId = ReportConst.HARGA_TAWARAN_FEC_RPT_VAL; // SC_016
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateEvaluationPdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generateEvaluationPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
//		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_RPT_JSP;
//			reportId = ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL; // SC_032
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_OC_RPT_JSP;
//			reportId = ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL; // SC_018
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_TEC_RPT_JSP;
//			reportId = ReportConst.PSDABK_TEC_RPT_VAL; // SC_010
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_RPT_JSP;
//			reportId = ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL; // SC_018
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
//		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/*
	 * private void generateOverallSummaryPdf(ScAttachment attachment, ScQt qt)
	 * throws Exception { debugLogger("generateOverallSummaryPdf");
	 * populateAttachmentDetails(attachment);
	 * 
	 * FacesScopes facesScopes = new FacesScopes(); long qtId = qt.getQtId();
	 * String reportId = ""; String reportName = ""; if
	 * (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
	 * .getManagedBean().getCommitteeType())) {
	 * 
	 * } else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
	 * .equals(this.getManagedBean().getCommitteeType())) {
	 * 
	 * reportName = ReportConst.SC_TEC_OVERALL_WITH_WEI_2F; reportId =
	 * ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL; // SC_012
	 * 
	 * } else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
	 * .equals(this.getManagedBean().getCommitteeType())) {
	 * 
	 * }
	 * 
	 * Map<String, String> paramMap = new HashMap<String, String>();
	 * paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
	 * paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
	 * .getUserSessionBean().getUserId() + "");
	 * paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");
	 * 
	 * generatePdfFromOracle(reportName, paramMap, attachment, this
	 * .getManagedBean().isCheckReportDownload()); }
	 */

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateTeknikalSpecificationPdf(ScAttachment attachment,
			ScQt qt) throws Exception {
		debugLogger("generateTeknikalSpecificationPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getScQt());
		SpecificationResDTO resDTO = null;
		List<Integer> settinglist = new ArrayList<Integer>();
		settinglist.add(1);
		reqDTO.setSettingTypes(settinglist);

		log.info("before retrieveScoreSettingDtls");

		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

		List<ScQtScoreSetting> list = resDTO.getScoreSettingResultBean()
				.getResultList();

		log.info("after retrieveScoreSettingDtls");

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			/* for (ScQtScoreSetting qtScoreSetting : list) { */
			/* if (qtScoreSetting.isWeightageReq()) { */
			// with weightage
			reportName = ReportConst.SC_EC_TECH_SPEC_WEI;
			reportId = ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL; // SC_029
			/*
			 * } else { // without weightage reportName =
			 * ReportConst.SC_EC_TECH_SPEC; reportId =
			 * ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL; // SC_028 }
			 */
			/* } */
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			for (ScQtScoreSetting qtScoreSetting : list) {
				if (qtScoreSetting.getSettingType() == 1) {
					/* if (qtScoreSetting.isWeightageReq()) { */
					// with weightage
					reportName = ReportConst.SC_TEC_TECH_SPEC_WEI;
					reportId = ReportConst.PSDAK_TEC_RPT_VAL; // SC_009
					/*
					 * } else { // without weightage reportName =
					 * ReportConst.SC_TEC_TECH_SPEC; reportId =
					 * ReportConst.TECSPEC_TEC_RPT_VAL; // SC_008 }
					 */
				}
			}
		}

		log.info("before paramMap");

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		log.info("after paramMap");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());

		log.info("end generateTeknikalSpecificationPdf");
	}

	/**
	 * Generate analisis keputusan penilaian teknikal pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateAnalisisKeputusanPenilaianTeknikalPdf(
			ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateAnalisisKeputusanPenilaianTeknikalPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPTPH_EC_RPT_JSP;
			reportId = ReportConst.AKPTPH_EC_RPT_VAL; // SC_026
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_TEC_RPT_JSP;
			reportId = ReportConst.AKPT_TEC_RPT_VAL; // SC_007
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_FEC_RPT_JSP;
			reportId = ReportConst.AKPT_FEC_RPT_VAL; // SC_015
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate kaedah2 pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateKaedah2Pdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generateKaedah2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_EC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL; // SC_025
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_TEC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL; // SC_006
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_FEC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL; // SC_014
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate pematuhan2 pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generatePematuhan2Pdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generatePematuhan2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_EC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_EC_RPT_VAL; // SC_024
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_TEC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_TEC_RPT_VAL; // SC_005
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_FEC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_FEC_RPT_VAL; // SC_013
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate kaedah1 pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateKaedah1Pdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generateKaedah1Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPP1_PEMATUHAN_EC_RPT_JSP;
			reportId = ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL; // SC_022
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPPP1_PEMATUHAN_OC_RPT_JSP;
			reportId = ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL; // SC_002
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate pematuhan1 pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generatePematuhan1Pdf(ScAttachment attachment, ScQt qt)
			throws Exception {
		debugLogger("generatePematuhan1Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPP1_PEMATUHAN_EC_RPT_JSP;
			reportId = ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL; // SC_021
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this
				.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPPP_PEMATUHAN_OC_RPT_JSP;
			reportId = ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL; // SC_001
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this
				.getManagedBean().isCheckReportDownload());
	}

	/**
	 * Generate committee details pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateCommitteeDetailsPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
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
					if (committeeActivity.isAttended()) {
						committeeActivities.add(committeeActivity);
					}
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

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(committeType)
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equals(committeType)
				|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equals(committeType)
				|| ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equals(committeType)) {
			context.put("committeeActivities", committeeActivities);
			context.put("meetingAttendances", meetingAttendancesList);
			context.put("bean", this);
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_COMMITTEE_DTLS, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate meeting details pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateMeetingDetailsPdfFromVelocity(ScAttachment attachment,
			ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		String userName = fs.getUserSessionBean().getUserName();
		context.put("hostUrl", hostUrl);
		context.put("userName", userName);

		Date newDate = new Date();
		String date = "";
		if (newDate != null) {
			date = dateSDF.format(newDate);
		}
		context.put("date", date);

		List<ScQtMeeting> scQtMeetings = new ArrayList<ScQtMeeting>();
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			// scQtMeetings = resDTO.getMeetingDtls();
			for (ScQtMeeting meeting : resDTO.getMeetingDtls()) {
				if (QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus()
						.getStatusId())) {
					meeting.setMeetDateStr(dateSDF.format(meeting
							.getMeetingDate()));
					DateFormat formatter = DateFormat.getInstance();
					formatter = DateFormat.getTimeInstance(); // time only
					meeting.setMeetTimeStr(formatter.format(meeting
							.getMeetingDate()));
					scQtMeetings.add(meeting);
				}
			}
			context.put("scQtMeetings", scQtMeetings);
		} finally {
			meetingSO = null;
		}

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

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateMaklumatTenderHargaPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateMaklumatTenderHargaPdfFromVelocity");
		attachment
				.setFileName(SourcingConstants.DOCNAME_EV_MAKLUMAT_TENDER_HARGA);
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		String userName = fs.getUserSessionBean().getUserName();
		context.put("hostUrl", hostUrl);
		context.put("userName", userName);

		Date newDate = new Date();
		String date = "";
		if (newDate != null) {
			date = dateSDF.format(newDate);
		}
		context.put("date", date);

		StringBuilder openType = new StringBuilder();
		if (openType != null) {
			openType.append(getParameterDesc(
					ParameterTypeConst.QUOTATION_TENDER_TYPE, qt.getQtType()));
		}

		String estimatedPrice = "";
		if (qt.getEstimatedAmount() != null) {
			estimatedPrice = amountDF.format(qt.getEstimatedAmount());
		}

		String publishDate = "";
		if (qt.getPublishDate() != null) {
			publishDate = dateSDF.format(qt.getPublishDate());
		}

		String closingDate = "";
		if (qt.getClosingDate() != null) {
			closingDate = dateSDF.format(qt.getClosingDate());
		}
		String totalSuppliers = "0";
		if (qt.getScQtSuppliers() != null) {

			IQTDetailSO detailsSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderReqDTO.setQtId(qt.getQtId());
			tenderResDTO = detailsSO
					.retriveQualifiedRestrictedSuppliers(tenderReqDTO);
			if (CollectionUtils.isNotEmpty(tenderResDTO.getRestrictedSuppliers())
					
					) {
								
				totalSuppliers = String.valueOf(tenderResDTO.
						getRestrictedSuppliers().size());

			}
		}

		boolean commType = false;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())
				|| ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equals(getManagedBean().getCommitteeType())
				|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("ec", commType);
			context.put("qtType", openType);
			context.put("estimatedPrice", estimatedPrice);
			context.put("publishDate", publishDate);
			context.put("closingDate", closingDate);
			context.put("totalSuppliers", totalSuppliers);

		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equals(getManagedBean().getCommitteeType())) {
			commType = true;
			context.put("tec", commType);
			context.put("qtType", openType);
			context.put("publishDate", publishDate);
			context.put("closingDate", closingDate);
			context.put("totalSuppliers", totalSuppliers);
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_MAKLUMAT_TENDER_HARGA, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate cover pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateCoverPdfFromVelocity(ScAttachment attachment, ScQt qt)
			throws Exception {
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
		context.put("procurementMode", qt.getProcurementMode()
				.getParameterCode());
		context.put("qtNo", qt.getQtNo());
		context.put("fileNo", qt.getFileNo());
		context.put("ptjName", qt.getPmOrgProfile().getOrgName());

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_EV_COVER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Download all reports.download all evaluation reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
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
				scQtEvaluationReport
						.setScQtEvaluationReportAttachment(reportAttachment);
			}
			attachments.add(reportAttachment);
		}
		downloadAllFiles(this.getManagedBean().getReportDownloadFileName(),
				attachments);
	}

	/**
	 * Determine icon.set icon for all uploaded files in evaluation.
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the string
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

	/**
	 * Delete upload file.Event handle to delete upload files in reports tab.
	 */
	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		this.getManagedBean().setScAttachment(null);
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			this.getManagedBean().getScQt()
					.setScEvaluationCommitteeReportAttachment(null);
			ScAttachment attachment = this.getManagedBean().getScQt()
					.getScEvaluationCommitteeReportAttachment();
			this.getManagedBean().setScAttachment(attachment);
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			this.getManagedBean().getScQt()
					.setScOpenCommitteeReportAttachment(null);
			ScAttachment attachment = this.getManagedBean().getScQt()
					.getScOpenCommitteeReportAttachment();
			this.getManagedBean().setScAttachment(attachment);

		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			this.getManagedBean().getScQt()
					.setScTechnicalEvaluationReportAttachment(null);
			ScAttachment attachment = this.getManagedBean().getScQt()
					.getScTechnicalEvaluationReportAttachment();
			this.getManagedBean().setScAttachment(attachment);

		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			this.getManagedBean().getScQt()
					.setScFinancialEvaluationReportAttachment(null);
			ScAttachment attachment = this.getManagedBean().getScQt()
					.getScFinancialEvaluationReportAttachment();
			this.getManagedBean().setScAttachment(attachment);

		}

	}

	/**
	 * Delete demo upload file.Event handle to delete upload file in
	 * presentation demo tab.
	 */
	public void deleteDemoUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getScQtChecklist().setScQtCheckListAttachment(null);

	}

	/**
	 * To set the report to be edited
	 * 
	 * @param evaluationReport
	 */
	public void setSelectedReport(ScQtEvaluationReport evaluationReport) {
		this.getManagedBean().setSelectedReport(evaluationReport);
	}
	
	/**
	 * Show template action.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the string
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	public String showTemplateAction(ScQtEvaluationReport scQtEvaluationReport)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		debugLogger("showTemplateAction");
		String templateDocUrl = null;
		try {
			TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
			tplDocBB.getManagedBean().setExternalPageUrl(
					"/xhtml/sc/evaluation/evaluationMain.xhtml");

			this.getManagedBean().setScQtEvaluationReport(scQtEvaluationReport);

//			ScQtEvaluationReport qtEvaluationReport = retriveRulesAndRegulationReport(scQtEvaluationReport);
//			if (qtEvaluationReport != null) {
//				scQtEvaluationReport = qtEvaluationReport;
//			} else {
//				scQtEvaluationReport = saveScQtReportList(scQtEvaluationReport);
//			}

			TplDocMaster docMaster = scQtEvaluationReport.getTplDocMaster();
			if (docMaster == null) {
				// clone standard tplDocMaster
				TplDocMaster tplDocMaster = cloneTplDocMasterDtls(scQtEvaluationReport);
				scQtEvaluationReport.setTplDocMaster(tplDocMaster);

				// save clone to db
				docMaster = saveClonedTplDocMaster(scQtEvaluationReport);
			}
			templateDocUrl = tplDocBB.goToMaint(docMaster, this
					.getManagedBean().isReadOnly());

		} finally {
		}
		return templateDocUrl;
	}

	/**
	 * Save cloned tpl doc master.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the tpl doc master
	 */
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

	/**
	 * Clone tpl doc master dtls.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the tpl doc master
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
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
		tplDocMaster.setTplDocSections(null);
		// tplDocMaster.setTplDocSectionCollection(null);
		tplDocMaster.setDocTitle(scQtEvaluationReport.getTitle());
		tplDocMaster.setDocName(scQtEvaluationReport.getTitle());

		setUserDetails(tplDocMaster);

		// clone tplDocSection Details
		/*
		 * tplDocMaster.setTplDocSections(new ArrayList<TplDocSection>());
		 * List<TplDocSection> docSections = tplDocMaster.getTplDocSections();
		 * for (TplDocSection tplDocSection : docSections) { TplDocSection
		 * section = (TplDocSection) tplDocSection.cloneEntity();
		 * section.setDocSectionId(null); section.setTplDocMaster(tplDocMaster);
		 * 
		 * setUserDetails(section);
		 * tplDocMaster.getTplDocSections().add(section); }
		 */

		tplDocMaster.setTplDocSectionCollection(new ArrayList<TplDocSection>());
		List<TplDocSection> docSectionCOllection = templateDocMaster
				.getTplDocSectionCollection();
		for (TplDocSection tplDocSection : docSectionCOllection) {
			TplDocSection section = (TplDocSection) tplDocSection.cloneEntity();
			section.setDocSectionId(null);
			section.setParent(null); // Temporarily am setting null, need to
										// confirm once
			section.setTplDocMaster(tplDocMaster);

			setUserDetails(section);
			tplDocMaster.getTplDocSectionCollection().add(section);
		}

		// Procurement Category
		PmParameter procurementCategory = scQt.getPmProcurementCategory();
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

		// Item Type - Service & Product
		PmParameter itemType = null;
		if (procurementCategory.getParameterCode().equals(
				ParameterConst.PROCUREMENT_CATEGORY_BAHAN_MENTAH)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_KAWALAN_KESELAMATAN)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_PERKHIDMATAN)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_MTO)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_PERKHIDMATAN_PEMINDAHAN_BARANG)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_HIRE_PURCHASE)) {
			itemType = getPmParameter(ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_SERVICE);
		} else if (procurementCategory.getParameterCode().equals(
				ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_MAKMAL)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_ICT)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_OTHERS)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_PEJABAT)) {
			itemType = getPmParameter(ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_PRODUCT);
		}
		tplDocMaster.setItemTypeId(itemType);

		// Status
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.DOC_TEMPLATE_DEFAULT);
		tplDocMaster.setPmStatus(status);

		tplDocMaster.setScReport(scQtEvaluationReport);

		return tplDocMaster;
	}

	/**
	 * Retrieve tpl doc master by id.
	 * 
	 * @param masterId
	 *            the master id
	 * @return the tpl doc master
	 */
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

	/**
	 * Retrieve standard tpl doc master.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @return the tpl doc master
	 */
	private TplDocMaster retrieveStandardTplDocMaster(ScQt scQt) {
		ITemplateDocSO templateDocSO = null;
		try {
			templateDocSO = (ITemplateDocSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();

			PmParameter categoryType = getPmParameter(
					ParameterTypeConst.PROCUREMENT_CATEGORY,
					ParameterConst.PROCUREMENT_CATEGORY_OTHERS);
			reqDTO.setCategoryId(categoryType.getParameterId());

			PmParameter processType = getPmParameter(
					ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
					ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
			reqDTO.setProcessTypeId(processType.getParameterId());

			if (this.getManagedBean().getCommitteeType()
					.equals(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE)) {
				reqDTO.setTitle(SourcingConstants.DOCTITLE_EVALUATION);
			} else if (this.getManagedBean().getCommitteeType()
					.equals(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {
				reqDTO.setTitle(SourcingConstants.DOCTITLE_OC_EVALUATION);
			} else if (this
					.getManagedBean()
					.getCommitteeType()
					.equals(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
				reqDTO.setTitle(SourcingConstants.DOCTITLE_TEC_EVALUATION);
			} else if (this
					.getManagedBean()
					.getCommitteeType()
					.equals(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
				reqDTO.setTitle(SourcingConstants.DOCTITLE_FEC_EVALUATION);
			}

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
	/**
	 * Save sc qt report list.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the sc qt evaluation report
	 */
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

	/**
	 * Save qt report dtls.
	 */
	public void saveQtReportDtls() {
		IQTEvaluationReportSO evaluationReportSO = null;
		try {
			evaluationReportSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationReportSO.class);

//			ScQt scQt = getManagedBean().getScQt();
//			String taskId = this.getManagedBean().getTaskId();
//			String[] outComes = this.getManagedBean().getOutComes();

			List<ScQtEvaluationReport> evaluationReportList = null;
			List<ScQtEvaluationReport> docReportsList = getManagedBean()
					.getScQtEvaluationReportList();
			if (docReportsList != null && !docReportsList.isEmpty()) {
				evaluationReportList = new ArrayList<ScQtEvaluationReport>();
				evaluationReportList.addAll(docReportsList);
			}

			EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
			reqDTO.setScQtEvaluationReportList(evaluationReportList);
			evaluationReportSO.saveQTDtl(reqDTO);

//			renewSesionObject();
			
//			this.getManagedBean().setScQt(scQt);
//			this.getManagedBean().setTaskId(taskId);
//			this.getManagedBean().setOutComes(outComes);
			retrieveReportTabDtl();

		} finally {
			evaluationReportSO = null;
		}
	}

	// retrieve the scQtEvaluationReport for Maklumat Tender/Sebut Harga/
	// Asas-Asas Penilaian / Perakuan
	/**
	 * Retrive rules and regulation report.
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the sc qt evaluation report
	 */
	private ScQtEvaluationReport retriveRulesAndRegulationReport(
			ScQtEvaluationReport scQtEvaluationReport) {
		debugLogger("retriveRulesAndRegulationReport");
		IQTDetailSO qTDetailSO = null;
		try {
			qTDetailSO = (IQTDetailSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setScReport(scQtEvaluationReport);
			reqDTO.setDocCategory(scQtEvaluationReport.getDocCategory());
			reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_QT_INFO);

			QuationTenderResDTO resDTO = qTDetailSO
					.retriveScQtReportByDefKey(reqDTO);
			ScQtEvaluationReport qtEvaluationReport = (ScQtEvaluationReport) resDTO
					.getScReport();
			return qtEvaluationReport;
		} finally {
			qTDetailSO = null;
		}
	}

	/**
	 * Gets the pm parameter.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param parameterCode
	 *            the parameter code
	 * @return the pm parameter
	 */
	private PmParameter getPmParameter(String parameterType,
			String parameterCode) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(parameterType);
		reqDTO.setParameterCode(parameterCode);

		ParameterResDTO resDTO = getParameterSO()
				.retrieveParameterByTypeAndCode(reqDTO);
		return resDTO.getPmParameter();
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Download encrypted file.
	 * 
	 * @param <ATTACHMENT>
	 *            the
	 * @param scAttachment
	 *            the sc attachment
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public <ATTACHMENT extends ScAttachment> File downloadEncryptedFile(
			ATTACHMENT scAttachment) throws IOException {
		if (scAttachment != null
				&& StringUtils.isNotBlank(scAttachment.getFileName())) {

			return downloadEncryptedFile(getActualFilePath(scAttachment),
					scAttachment.getFileName());
		} else {
			throw new EpException("Attachment not found.");
		}
	}

	/**
	 * Download encrypted file.
	 * 
	 * @param filePath
	 *            the file path
	 * @param fileName
	 *            the file name
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File downloadEncryptedFile(String filePath, String fileName)
			throws IOException {

		log.info("Inside downloadFile(...)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		// fire fox issue regarding space
		String finalFileName = fileName.replaceAll("\\s+",
				QTConstants.REPLACE_CHAR);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ finalFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();

		OutputStream out = response.getOutputStream();

		if (filePath != null) {
			InputStream input = null;
			try {
				log.info("Inside filePath(...)");
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				log.info("Inside filePaths(...)");
				input = new FileInputStream(filePath);
			}
			// byte[] buffer = new byte[4096];
			// int bytesRead;
			// while ((bytesRead = input.read(buffer)) != -1) {
			// out.write(buffer, 0, bytesRead);
			// buffer = new byte[4096];

			BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC();
			bc.InitDecryptCipher();
			bc.CBCDecryptFile(input, out);
			input.close();
		}

		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();

		// use to upload back to database
		File file = new File(filePath);
		return file;
	}

	/**
	 * Event to update the status of the selected meeting
	 * 
	 * @param scQtMeeting
	 *            the selected ScQtMeeting entity
	 */
	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(scQtMeeting.getMeetingStatusStr());

		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		scQtMeeting.setMeetingStatus(statusResDTO.getPmStatus());
	}

	private PmStatus getPmStatus() {
		IStatusSO statussO = null;
		PmStatus status = null;
		try {
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(QTStatusConst.PR_COMPLETED);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);
			status = statusres.getPmStatus();
		} finally {

		}
		return status;

	}

	/**
	 * Save evalution stage upload dtls.Event handle to save the evaluation
	 * stage technical and financial uploaded checklists.
	 */
	public void saveEvalutionStageUploadDtls() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			ScQtProposalChecklist qtProposalChecklist = null;
			List<ScQtProposalChecklist> uploadedList = this.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();

			if (null != uploadedList && !uploadedList.isEmpty()) {
				PmStatus Status = getPmStatus();
				for (ScQtProposalChecklist uploadedChecklist : uploadedList) {
					uploadedChecklist.setEvaluationStatus(Status);
				}

				reqDTO.setComplianceList(uploadedList);

				qtProposalChecklist = uploadedList.get(0);
			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);

			/*
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getSaveComplianceList());
			 */

			if (qtProposalChecklist != null) {
				if (SourcingConstants.TECHNICAL_DOCTYPE
						.equals(qtProposalChecklist.getScQtCheckList()
								.getDocCategory())) {
					EvaluationReqDTO savereqDTO = new EvaluationReqDTO();
					evaluationSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTEvaluationSO.class);
					savereqDTO.setCheckListId(qtProposalChecklist
							.getScQtCheckList().getQtChecklistId());
					savereqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(qtProposalChecklist
									.getScQtCheckList().getDefinationType()
									.getParameterCode())
							|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(qtProposalChecklist
											.getScQtCheckList()
											.getDefinationType()
											.getParameterCode())) {
						// reqDTO.setCompliance(true);
						if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())
								|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
										.equalsIgnoreCase(this.getManagedBean()
												.getCommitteeType())) {
							savereqDTO
									.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
						} else {
							if (this.getManagedBean().getScQt()
									.isDemoRequired()) {
								savereqDTO
										.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
							} else {
								savereqDTO
										.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							}
						}
					}
					ScQtCheckList technicalUploadNoActionCheckList = checkStausForUploadNoActionCheckLists(
							qtProposalChecklist.getScQtCheckList(),
							savereqDTO.getEvalStage());
					List<ScQtCheckList> technicalChecklsit = this
							.getManagedBean()
							.getEvaluationStgTechnicalCheckList();
					if (null != technicalChecklsit
							&& !technicalChecklsit.isEmpty()) {
						for (int i = 0; i < technicalChecklsit.size(); i++) {
							ScQtCheckList checkList = technicalChecklsit.get(i);
							if (checkList.getTitle()
									.equalsIgnoreCase(
											technicalUploadNoActionCheckList
													.getTitle())) {
								int index = technicalChecklsit.indexOf(this
										.getManagedBean().getScQtChecklist());
								// technicalChecklsit.remove(checkList);
								technicalChecklsit.set(index,
										technicalUploadNoActionCheckList);
							}

						}
					}
					this.getManagedBean()
							.setComplianceTechnicalProposalCheckListDM(
									new ComplianceSupplierPagingDataModel(
											"complianceTechnicalSupplier",
											qtProposalChecklist
													.getScQtCheckList()
													.getQtChecklistId(),
											savereqDTO.getCompliance(), this
													.getManagedBean().getScQt()
													.getQtId(), savereqDTO
													.getEvalStage()));
					getManagedBean().setTechnicalSummaryGenerated(false);
					getManagedBean().setPresentationDemoSummaryGenerated(false);
					checkDemoStatusForQtCheckList();
					evaluationTechnicalChangeScQtStatusUpdate();
				} else {
					EvaluationReqDTO finreqDTO = new EvaluationReqDTO();

					finreqDTO.setCheckListId(this.getManagedBean()
							.getScQtChecklist().getQtChecklistId());
					finreqDTO
							.setQtId(this.getManagedBean().getScQt().getQtId());
					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equalsIgnoreCase(this.getManagedBean()
									.getScQtChecklist().getDefinationType()
									.getParameterCode())
							|| ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
									.equalsIgnoreCase(this.getManagedBean()
											.getScQtChecklist()
											.getDefinationType()
											.getParameterCode())) {
						// reqDTO.setCompliance(true);
						if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())
								|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
										.equalsIgnoreCase(this.getManagedBean()
												.getCommitteeType())) {
							if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
									.equalsIgnoreCase(this.getManagedBean()
											.getCommitteeType())) {
								finreqDTO
										.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
							} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
									.equalsIgnoreCase(this.getManagedBean()
											.getCommitteeType())) {
								if (this.getManagedBean().getScQt()
										.isTechEvaluationReq()) {
									finreqDTO
											.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
								} else {
									finreqDTO
											.setEvalStage(SourcingConstants.COMPLAINCE_STAGE);
								}

							}
						} else {
							if (this.getManagedBean().getScQt()
									.isDemoRequired()) {
								finreqDTO
										.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
							} else {
								finreqDTO
										.setEvalStage(QTConstants.TECHNICAL_EVALUATION);
							}
						}
					}
					ScQtCheckList financialUploadNoActionCheckList = checkStausForUploadNoActionCheckLists(
							qtProposalChecklist.getScQtCheckList(),
							finreqDTO.getEvalStage());
					List<ScQtCheckList> technicalChecklsit = this
							.getManagedBean()
							.getEvaluationStgfinancialCheckList();
					if (null != technicalChecklsit
							&& !technicalChecklsit.isEmpty()) {
						for (int i = 0; i < technicalChecklsit.size(); i++) {
							ScQtCheckList checkList = technicalChecklsit.get(i);
							if (checkList.getTitle()
									.equalsIgnoreCase(
											financialUploadNoActionCheckList
													.getTitle())) {
								// technicalChecklsit.remove(checkList);
								int index = technicalChecklsit.indexOf(this
										.getManagedBean().getScQtChecklist());
								technicalChecklsit.set(index,
										financialUploadNoActionCheckList);
							}

						}
					}
					this.getManagedBean()
							.setComplianceTechnicalProposalCheckListDM(
									new ComplianceSupplierPagingDataModel(
											"evaluateFinSupplier",
											qtProposalChecklist
													.getScQtCheckList()
													.getQtChecklistId(),
											finreqDTO.getCompliance(), this
													.getManagedBean().getScQt()
													.getQtId(), finreqDTO
													.getEvalStage()));
					getManagedBean().setFinancialSummaryGenerated(false);
				}
				updateSummaryGeneratedStatus();
			}

			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									"Evaluation Uploaded Checklist saved successfully.",
									"Evaluation Uploaded Checklist saved successfully."));

		} finally {

		}

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
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getScQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
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
	 * To select checkbox check/Uncheck in meeting details.
	 * 
	 * @param selected
	 *            (boolean)
	 * @param list
	 */
	public <ENTITY_CLASS extends BaseEntity> void selectEvaluationMeetingAll(
			boolean selected, List<ENTITY_CLASS> list) {
		if (list != null) {
			for (ENTITY_CLASS entity : list) {
				entity.setSelected(selected);
			}
		}
	}

	/**
	 * To remove records for existing .
	 * 
	 * @param list
	 * @param toBeRemovedList
	 */
	public <ENTITY_CLASS extends BaseEntity> void markDeleteSelectedEvaluationRowsAction(
			List<ScQtMeeting> list, List<ScQtMeeting> markDeletedList) {
		if (log.isDebugEnabled()) {
			log.debug("markDeleteSelectedRowsAction");
		}
		try {
			if (list != null) {
				if (markDeletedList == null) {
					markDeletedList = new ArrayList<ScQtMeeting>();
				}

				for (ScQtMeeting entity : list) {
					if (entity.isSelected()) {
						entity.setDeleted(true);
						markDeletedList.add(entity);
					}
				}
				if (markDeletedList.size() == 0) {
					throw new EpException(MessageConst.ERROR_760146);
				}

				deleteMeetingRowsEvaluationAction(list, markDeletedList);
			} else {
				throw new EpException(MessageConst.ERROR_760146);
			}
		} finally {

		}
	}

	private <ENTITY_CLASS> void deleteMeetingRowsEvaluationAction(
			List<ScQtMeeting> list, List<ScQtMeeting> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}
		if (list != null && toBeRemovedList != null) {
			this.getManagedBean().setMeetingRemoveList(toBeRemovedList);
			list.removeAll(toBeRemovedList);
		}
	}

	// this method checking two meeting dates or equal or not.
	private void compareMeetingDates(ScQtMeeting scqtMeeting,
			List<ScQtMeeting> meetingList) {
		if (null != meetingList && meetingList.size() >= 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			boolean duplicateDate = false;
			for (ScQtMeeting meeting : meetingList) {

				String objectDate = sdf.format(scqtMeeting.getMeetingDate());
				String listDate = sdf.format(meeting.getMeetingDate());

				if (objectDate.equals(listDate)) {
					duplicateDate = true;
					break;

				} else {
					duplicateDate = false;
				}

			}
			if (duplicateDate == true) {
				throw new EpException(MessageConst.ERROR_760271);
			} else {
				meetingList.add(scqtMeeting);
			}
		} else {
			meetingList.add(scqtMeeting);
		}

	}

	// delete existing meeting details records from database.
	public void deleteMeetingDtls() {
		markDeleteSelectedEvaluationRowsAction(this.getManagedBean()
				.getScQtMeetings(), this.getManagedBean()
				.getMeetingRemoveList());

	}

	// delete existing meeting attendance details records from database.
	public void deleteMeetingAttendanceDtls() {
		markDeleteSelectedRowsAction(getManagedBean()
				.getMeetingAttendanceList(), getManagedBean()
				.getMeetingAttendanceRemoveList());

	}

	// retrieve specification based on sample item.
	public void retriveSpecificationsByItem(SelectEvent event) {
		this.getManagedBean().setSampleItemSpecifications(
				new ArrayList<TplSpecQuestion>());
		this.getManagedBean().setItemName(null);
		ScQtSampleItem sampleItem = (ScQtSampleItem) event.getObject();

		// TplSpecQuestion specQuestions = requestItem.getTplSpecQuestion();
		this.getManagedBean().setItemName(
				sampleItem.getScRequestItem().getItemName());
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		specReqDTO.setItemName(sampleItem.getScRequestItem().getItemName());

		TemplateSpecResDTO resDTO = templateSpecSO
				.retrieveTechSpecByItemNames(specReqDTO);

		if (null != resDTO && null != resDTO.getSpecQuestionList()) {
			this.getManagedBean().getSampleItemSpecifications()
					.addAll(resDTO.getSpecQuestionList());

		}

	}

	// change sample submission status in sample tab.
	public void changeSampleStatus(ScQtSampleItem sampleItem,
			IPagingDataModel<ScQtSampleItem> sampleItemList) {
		List<ScQtSampleItem> dataModelItemList = sampleItemList.getList();
		int index = dataModelItemList.indexOf(sampleItem);
		Long submissionStatusId = null;
		if (null != sampleItem && null != sampleItem.getSubmissionStatus()) {
			submissionStatusId = sampleItem.getSubmissionStatus()
					.getParameterId();
		}

		if (null != submissionStatusId) {
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SAMPLE_SUBMISSION_STATUS);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(submissionStatusId)) {
					sampleItem.setSubmissionStatus(paramDesc.getPmParameter());
					break;

				}

			}
			dataModelItemList.set(index, sampleItem);
		}
	}

	private void checkTechnicalSuppliersQualified() {
		if (!this.getManagedBean().isCheckQualifiedSuppliers()) {

		} else {
			if (!this.getManagedBean().isReadOnly()) {
				this.getManagedBean().setCheckTechnicalQualifiedOpenPopup(true);
				/*
				 * RequestContext.getCurrentInstance().execute(
				 * "technicalEvaluationStandardChecklistDlgs.show()");
				 */
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null,
								MessageUtils.getMessage(
										MessageConst.WARN_760290,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		}

	}

	private void checkFinancialSuppliersQualified() {
		if (!this.getManagedBean().isCheckQualifiedSuppliers()) {

		} else {
			if (!this.getManagedBean().isReadOnly()) {
				this.getManagedBean().setCheckFinancialQualifiedOpenPopup(true);
				/*
				 * RequestContext.getCurrentInstance().execute(
				 * "financialEvaluationStandardChecklistDlgs.show()");
				 */
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null,
								MessageUtils.getMessage(
										MessageConst.WARN_760248,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		}

	}

	/**
	 * Summary Supplier Paging Data Model
	 * 
	 * @author kenny
	 * @since 14.06.2015
	 */
	private class SummarySupplierPagingDataModel extends
			PagingDataModel<ScQtEvaluationResultDtl> {

		private static final long serialVersionUID = 1L;

		private boolean isQualified;
		private boolean isZonal;
		private String awardBy;
		private List<String> resultStages;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public SummarySupplierPagingDataModel(String componentId,
				boolean isQualified, boolean isZonal, String awardBy,
				List<String> resultStages) {

			super(componentId);
			this.isQualified = isQualified;
			this.isZonal = isZonal;
			this.awardBy = awardBy;
			this.resultStages = resultStages;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtEvaluationResultDtl> pageChangeListener(
				ISearchBean<ScQtEvaluationResultDtl> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				ScQt scQt = getManagedBean().getScQt();

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setZonal(isZonal);
				reqDTO.setQualified(isQualified);
				reqDTO.setResultStages(resultStages);
				reqDTO.setEvalResultSearchBean(searchBean);

				if (getManagedBean().getSelectedZone() != null && isZonal) {
					reqDTO.setSpeZoneId(getManagedBean().getSelectedZone()
							.getSpecZoneId());
				}

				if (SourcingConstants.AWARD_BY_ITEM.equals(awardBy)) {
					if (getManagedBean().getSinglesummaryItemList() != null) {
						reqDTO.setRequestItemId(getManagedBean()
								.getSinglesummaryItemList()
								.getRequestNoteItemId());
					} else {
						return new SearchResultBean<ScQtEvaluationResultDtl>();
					}
				}

				return evaluationSO.retrieveSummarySuppliersByStage(reqDTO)
						.getSummarySupplierResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * Compliance Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class ComplianceSupplierPagingDataModel extends
			PagingDataModel<ScQtProposalChecklist> {

		private static final long serialVersionUID = 1L;
		private Long qtId;
		private Long checkListId;
		private Boolean compliance;
		private String evalStage;

		/**
		 * Constructor for Compliance Supplier Paging Data Model
		 */
		public ComplianceSupplierPagingDataModel(String componentId,
				Long checkListId, Boolean compliance, Long qtId,
				String evalStage) {

			super(componentId);
			this.checkListId = checkListId;
			this.compliance = compliance;
			this.qtId = qtId;
			this.evalStage = evalStage;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtProposalChecklist> pageChangeListener(
				ISearchBean<ScQtProposalChecklist> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setCheckListId(checkListId);
				reqDTO.setCompliance(compliance);
				reqDTO.setQtId(qtId);
				reqDTO.setEvalStage(evalStage);
				reqDTO.setProposalChecklistSearchBean(searchBean);
				return evaluationSO.retrieveComplianceDtlsByCheckList(reqDTO)
						.getComplianceResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * FinancialSpec WithOutzone Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class FinancialSpecSupplierWithOutZonePagingDataModel extends
			PagingDataModel<ScQtProposalChecklist> {

		private static final long serialVersionUID = 1L;
		private Long qtId;
		private Long checkListId;
		private String evalStage;

		/**
		 * FinancialSpec WithOutzone Supplier Paging Data Model
		 */
		public FinancialSpecSupplierWithOutZonePagingDataModel(
				String componentId, Long checkListId, Long qtId,
				String evalStage) {

			super(componentId);
			this.checkListId = checkListId;
			this.qtId = qtId;
			this.evalStage = evalStage;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtProposalChecklist> pageChangeListener(
				ISearchBean<ScQtProposalChecklist> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setCheckListId(checkListId);
				reqDTO.setQtId(qtId);
				reqDTO.setEvalStage(evalStage);
				reqDTO.setProposalChecklistSearchBean(searchBean);
				return evaluationSO.retrievProposalCheckListByCheckListNZone(
						reqDTO).getComplianceResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * FinancialSpec Withzone Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class FinancialSpecSupplierWithZonePagingDataModel extends
			PagingDataModel<ScQtProposalChecklist> {

		private static final long serialVersionUID = 1L;
		private Long qtId;
		private Long checkListId;
		private String evalStage;
		private Long speZoneId;

		/**
		 * FinancialSpec Withzone Supplier Paging Data Model
		 */
		public FinancialSpecSupplierWithZonePagingDataModel(String componentId,
				Long checkListId, Long qtId, String evalStage, Long speZoneId) {

			super(componentId);
			this.checkListId = checkListId;
			this.qtId = qtId;
			this.evalStage = evalStage;
			this.speZoneId = speZoneId;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtProposalChecklist> pageChangeListener(
				ISearchBean<ScQtProposalChecklist> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setCheckListId(checkListId);
				reqDTO.setQtId(qtId);
				reqDTO.setEvalStage(evalStage);
				reqDTO.setSpeZoneId(speZoneId);
				reqDTO.setProposalChecklistSearchBean(searchBean);
				return evaluationSO.retrievProposalCheckListByCheckListNZone(
						reqDTO).getComplianceResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * Compliance Sample Items Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class ComplianceSampleItemsPagingDataModel extends
			PagingDataModel<ScQtSampleItem> {

		private static final long serialVersionUID = 1L;
		private Long lableId;

		/**
		 * Compliance Sample Items Supplier Paging Data Model
		 */
		public ComplianceSampleItemsPagingDataModel(String componentId,
				Long lableId) {

			super(componentId);
			this.lableId = lableId;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtSampleItem> pageChangeListener(
				ISearchBean<ScQtSampleItem> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setLableId(lableId);
				reqDTO.setSampleItemSearchBean(searchBean);
				return evaluationSO.retrieveSampleItemDtlsByLable(reqDTO)
						.getItemResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * Compliance Sample Label Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class ComplianceSampleLabelSuppliersPagingDataModel extends
			PagingDataModel<ScQtSampleLable> {

		private static final long serialVersionUID = 1L;
		private Long qtId;

		/**
		 * Compliance Sample Label Supplier Paging Data Model
		 */
		public ComplianceSampleLabelSuppliersPagingDataModel(
				String componentId, Long qtId) {

			super(componentId);
			this.qtId = qtId;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtSampleLable> pageChangeListener(
				ISearchBean<ScQtSampleLable> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setQtId(qtId);
				reqDTO.setSampleLableSearchBean(searchBean);
				return evaluationSO.retrieveSampleLableDtls(reqDTO)
						.getLableResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/**
	 * Presentation Demo Supplier Paging Data Model
	 * 
	 * @author Manga
	 * @since 16.06.2015
	 */
	private class PresentationDemoSuppliersPagingDataModel extends
			PagingDataModel<ScQtProposalChecklist> {

		private static final long serialVersionUID = 1L;
		private Long qtId;
		private String evalStage;

		/**
		 * Compliance Sample Label Supplier Paging Data Model
		 */
		public PresentationDemoSuppliersPagingDataModel(String componentId,
				Long qtId, String evalStage) {

			super(componentId);
			this.qtId = qtId;
			this.evalStage = evalStage;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtProposalChecklist> pageChangeListener(
				ISearchBean<ScQtProposalChecklist> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTEvaluationSO evaluationSO = null;
			try {
				evaluationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setQtId(qtId);
				reqDTO.setEvalStage(evalStage);
				reqDTO.setProposalChecklistSearchBean(searchBean);
				return evaluationSO.preparePresentationDemoDtls(reqDTO)
						.getComplianceResultBean();

			} finally {
				evaluationSO = null;
			}
		}
	}

	/*
	 * this method to save compliance Proposal suppliers to navigate to
	 * anotherpage using paging
	 */
	public void complianceProposalSubmitSuppliers() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> dataProposalCheckList = (List<ScQtProposalChecklist>) this
					.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();
			if (null != dataProposalCheckList
					&& !dataProposalCheckList.isEmpty()) {
				for (ScQtProposalChecklist proposalCheckList : dataProposalCheckList) {
					if (proposalCheckList.getScQtProposal().isSubmitted()) {
						/*
						 * if (null == proposalCheckList.getCompliance()) { //
						 * throw new EpException(MessageConst.ERROR_760204); }
						 */
						if (null != proposalCheckList.getCompliance()
								&& !proposalCheckList.getCompliance()) {
							if (null == proposalCheckList.getRemark()
									|| "".equalsIgnoreCase(proposalCheckList
											.getRemark())) {
								throw new EpException(MessageConst.ERROR_760198);
							}
						}
					}
					reqDTO.setComplianceList(dataProposalCheckList);
				}

			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
		} finally {
			evaluationSo = null;
		}

	}

	/* this method to save Upload checklist suppliers using paging data model. */
	public void SaveUploadCheckListSuppliersDM() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			List<ScQtProposalChecklist> uploadedList = this.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();

			if (null != uploadedList && !uploadedList.isEmpty()) {
				PmStatus Status = getPmStatus();
				for (ScQtProposalChecklist uploadedChecklist : uploadedList) {
					uploadedChecklist.setEvaluationStatus(Status);
				}

				reqDTO.setComplianceList(uploadedList);

			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
		} finally {
			evaluationSo = null;
		}

	}

	/* save Compliance Paging data model */
	public void saveComplianceSampleItemsDM() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtSampleItem> sampleCompliance = this.getManagedBean()
					.getComplianceSampleItemListDM().getList();

			if (null != sampleCompliance && !sampleCompliance.isEmpty()) {
				for (ScQtSampleItem sampleItem : sampleCompliance) {
					if (null == sampleItem.getSubmissionStatus()
							|| null == sampleItem.getSubmissionStatus()
									.getParameterId()) {
						throw new EpException(MessageConst.ERROR_760363);

					}
				}

				reqDTO.setSampleComplianceList(sampleCompliance);
			}

			evaluationSo.saveOrUpdateSampleComplianceDtls(reqDTO);

			getManagedBean().setComplianceSummaryGenerated(false);
			updateSummaryGeneratedStatus();

		} finally {
			evaluationSo = null;
		}
	}

	// retrieve presentationdemo qualified suppliers based on technical stage.
	private void retrivePresentationDemoChecklistSuppliersStatus(
			ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
				reqDTO.setCheckListId(checklist.getQtChecklistId());
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setEvalStage(SourcingConstants.TECHINICAL_STAGE);

				resDTO = evaluationSo.checkDemoAndUploadChecklistStatus(reqDTO);
				if (resDTO.getPendingCount() > 0) {
					this.getManagedBean().setPresentationDemoActiveIndex(0);
					throw new EpException(MessageConst.ERROR_760316);
				}
			}
		} finally {

		}

	}

	private void presentationDemoScQtStatusUpdate() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			this.getManagedBean().getScQt().setDemoNotificationCheck(null);
			reqDTO.setScQt(this.getManagedBean().getScQt());
			evalResDTO = evaluationSo.saveReportDtls(reqDTO);
			this.getManagedBean().setScQt(evalResDTO.getScQt());
		} finally {
			evaluationSo = null;
		}

	}

	private void checkDemoStatusForQtCheckList() {
		if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
				.getManagedBean().getScQt().getEvaluationType())
				&& ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
				&& this.getManagedBean().getScQt().isDemoRequired()
				&& null != getManagedBean().getScQt()
						.getDemoNotificationCheck()
				&& !this.getManagedBean().isReadOnly()) {
			if (null != getManagedBean().getScQt().getDemoNotificationCheck()
					&& 1 == getManagedBean().getScQt()
							.getDemoNotificationCheck()) {
				presentationDemoScQtStatusUpdate();
			}

		}
	}

	private void complianceChangeScQtStatusUpdate() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				if (null != this.getManagedBean().getScQt()
						.getTecEvaluationCheck()
						|| null != this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {
					evaluationSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTEvaluationSO.class);
					EvaluationReqDTO checkListreqDTO = new EvaluationReqDTO();
					checkListreqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					checkListreqDTO.setStatusUpdateChecklistType(null);
					evaluationSo.retriveAllChecklist(checkListreqDTO);
					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					this.getManagedBean().getScQt().setTecEvaluationCheck(null);
					this.getManagedBean().getScQt().setFecEvaluationCheck(null);
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evalResDTO = evaluationSo.saveReportDtls(reqDTO);
					this.getManagedBean().setScQt(evalResDTO.getScQt());

				}

			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())) {
				if (null != this.getManagedBean().getScQt()
						.getTecEvaluationCheck()
						|| null != this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {
					evaluationSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTEvaluationSO.class);
					EvaluationReqDTO checkListreqDTO = new EvaluationReqDTO();
					checkListreqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					checkListreqDTO.setStatusUpdateChecklistType(null);
					evaluationSo.retriveAllChecklist(checkListreqDTO);
					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					this.getManagedBean().getScQt().setTecEvaluationCheck(null);
					this.getManagedBean().getScQt().setFecEvaluationCheck(null);
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evalResDTO = evaluationSo.saveReportDtls(reqDTO);
					this.getManagedBean().setScQt(evalResDTO.getScQt());

				}

			}
		} finally {

		}

	}

	private void evaluationTechnicalChangeScQtStatusUpdate() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
					&& this.getManagedBean().getScQt().isTechEvaluationReq()
					&& null != this.getManagedBean().getScQt()
							.getFecEvaluationCheck()
					&& !this.getManagedBean().isReadOnly()) {
				if (null != this.getManagedBean().getScQt()
						.getFecEvaluationCheck()) {
					evaluationSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTEvaluationSO.class);
					EvaluationReqDTO checkListreqDTO = new EvaluationReqDTO();
					checkListreqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					checkListreqDTO
							.setStatusUpdateChecklistType(SourcingConstants.FINANCIAL_DOCTYPE);
					evaluationSo.retriveAllChecklist(checkListreqDTO);
					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					this.getManagedBean().getScQt().setFecEvaluationCheck(null);
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evalResDTO = evaluationSo.saveReportDtls(reqDTO);
					this.getManagedBean().setScQt(evalResDTO.getScQt());

				}

			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
					&& null != this.getManagedBean().getScQt()
							.getFecEvaluationCheck()
					&& !this.getManagedBean().isReadOnly()) {
				if (null != this.getManagedBean().getScQt()
						.getFecEvaluationCheck()) {
					evaluationSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTEvaluationSO.class);
					EvaluationReqDTO checkListreqDTO = new EvaluationReqDTO();
					checkListreqDTO.setQtId(this.getManagedBean().getScQt()
							.getQtId());
					checkListreqDTO
							.setStatusUpdateChecklistType(SourcingConstants.FINANCIAL_DOCTYPE);
					evaluationSo.retriveAllChecklist(checkListreqDTO);
					EvaluationReqDTO reqDTO = new EvaluationReqDTO();
					this.getManagedBean().getScQt().setFecEvaluationCheck(null);
					reqDTO.setScQt(this.getManagedBean().getScQt());
					evalResDTO = evaluationSo.saveReportDtls(reqDTO);
					this.getManagedBean().setScQt(evalResDTO.getScQt());

				}

			}
		} finally {

		}

	}

	private int sampleClosingDateCountDays() {
		try {

			Date d1 = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH");
			this.getManagedBean().setCurrentTime(
					Integer.valueOf(sdf.format(d1)));
			long days = DateUtils.getDaysDifference(d1, this.getManagedBean()
					.getScQt().getSampleClosingDate());
			SimpleDateFormat currendate = new SimpleDateFormat("dd");
			String currentDate = currendate.format(d1);
			int day = (int) days;
			if (day > 0 || day < 0) {
				this.getManagedBean().setCountDays(day);
			}
			if (day == 0
					&& !currentDate
							.equals(currendate.format(this.getManagedBean()
									.getScQt().getSampleClosingDate()))) {
				this.getManagedBean().setCountDays(1);
			}
			if (day == 0
					&& currentDate
							.equals(currendate.format(this.getManagedBean()
									.getScQt().getSampleClosingDate()))) {
				this.getManagedBean().setCountDays(0);
			}
		} finally {

		}
		return this.getManagedBean().getCountDays();
	}

	private ScQtFinalization retriveScQtFinalizationForReEvaluate() {
		IQTEvaluationSO evaluationSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
		EvaluationReqDTO supplierFinalizationReqDTO = new EvaluationReqDTO();
		EvaluationResDTO supplierFinalizationResDTO = new EvaluationResDTO();
		supplierFinalizationReqDTO.setQtId(this.getManagedBean().getScQt()
				.getQtId());
		supplierFinalizationResDTO = evaluationSo
				.retrieveSupplierFinalizationForReEvaluation(supplierFinalizationReqDTO);
		return supplierFinalizationResDTO.getScQtFinalization();
	}

	private void retriveTemplateSpecScoring(ScQtCheckList checklist) {
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO resDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		List<TplSpecQuestion> questions = resDTO.getSpecMaster()
				.getTplSpecQuestions();
		for (TplSpecQuestion specQuestion : questions) {
			log.info("===specQuestion name====" + specQuestion.getClItem());
			if (null != specQuestion.getScoreTypeCode()
					&& specQuestion.getScoreTypeCode().getParameterCode()
							.equalsIgnoreCase("U")) {
				this.getManagedBean().setUserScoreSchemaType("2");

				break;
			} else {
				if (null != specQuestion.getScoreTypeCode()
						&& specQuestion.getScoreTypeCode().getParameterCode()
								.equalsIgnoreCase("A")) {

				}
			}
		}

	}


}

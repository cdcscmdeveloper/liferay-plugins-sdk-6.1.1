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
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDemoSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationReportSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
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
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.DemoReqDTO;
import my.ep.dto.sc.quationtender.DemoResDTO;
import my.ep.dto.sc.quationtender.EvaluationReportReqDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
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
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
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
import my.ep.persistence.entity.sc.quationtender.ScQtDemoByItemScore;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoSupplierAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoTechnicalCommitteeRemark;
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
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.EvaluationReport;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.common.ScRemarkAndAttachmentBackingBean;
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
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.primefaces.component.datatable.DataTable;
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
public class EvaluationBackingBean extends SourcingBaseBackingBean<EvaluationManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	
   private static final String  UNDER_SCORE = "_";

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
		//log.info(message);
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
			evaluationSO=null;
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
			evaluationSO=null;
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

	/*
	 * user get setting type as technical,financial,package,item
	 * @param recommendBy
	 *                (String recommendBy)
	 *  
	 *  return int.              
	 */
	private int getSettingType(String recommendBy) {
		// ScQt scQt = this.getManagedBean().getScQt();

		// scQt.isTechEvaluationReq()

		// if (scQt.getEvaluationType().equals
		// (ParameterConst.EVALUATION_TYPES_ONE_TIER)) {
		//
		// }

		if (StringUtils.equals(recommendBy, QTConstants.TECHNICAL_EVALUATION)) {
			return SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.FINANCIAL_EVALUATION_BY_PACKAGE)) {
			return SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.PRESENTATION_DEMO_EVALUATION)) {
			return SourcingConstants.SCORE_SETTING_TYPE_DEMO;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.RECOMMENDATION_BY_PACKAGE)) {
			return SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE;
		} else if (StringUtils.equals(recommendBy,
				QTConstants.FINANCIAL_EVALUATION_BY_ITEM)) {
			return SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM;
		} else {
			return SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM;
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
			evaluationSO = null;
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

	
	/**
	 *to retrive all the general information menu details.
	 * 
	 *
	 */
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

			// 25037 - start - note 568
			if (!this.getManagedBean().isCommitteModified()) {
			// 25037 - end - note 568
				if (null == this.getManagedBean().getScQtMeetings()) {
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

					reqDTO.setCommitteType(this.getManagedBean().getCommitteeType());
					resDTO = meetingSO.retrieveMeetingDls(reqDTO);

					if (resDTO.getMeetingDtls() != null && !resDTO.getMeetingDtls().isEmpty()) {

						for (ScQtMeeting scQtMeeting : resDTO.getMeetingDtls()) {
							Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting
									.getMeetingStatus().getStatusId() : null;
							scQtMeeting.setMeetingStatusStr(meetingStatusId);

							// 29473, 31675 -start
							if (scQtMeeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_SUSPENDED)) {
								this.getManagedBean().setHasPreviousMeeting(true);
							}
							// 29473, 31675 -End
						}

						this.getManagedBean().setScQtMeetings(resDTO.getMeetingDtls());

					}

					this.getManagedBean().setScQtCommittee(resDTO.getQtCommittee());
					if (null != resDTO.getQtCommittee()) {
						// 25037 - start
						ScCommittee sccommittee = resDTO.getQtCommittee().getScCommittee();
						if (CollectionUtils.isNotEmpty(resDTO.getQtCommittee().getCommitteeActivities())) {

							for (ScQtCommitteeActivity committeeActivity : resDTO.getQtCommittee()
									.getCommitteeActivities()) {
								ScCommitteeMember member = committeeActivity.getScCommitteeMember();
								if (member.getMemberRole().getParameterCode()
										.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
									committeeActivity.setAttended(true);
								}
							}
							Map<Object, Object> map = CustomUtils.listToMap(resDTO.getQtCommittee()
									.getCommitteeActivities(), "getScCommitteeMember.getCommitteeMemberId");
							List<ScQtCommitteeActivity> addedActivities = new ArrayList<>();
							for (ScCommitteeMember member : sccommittee.getScCommitteeMembers()) {
								if (map.get(member.getCommitteeMemberId()) == null) {
									member.setScCommittee(sccommittee);
									setUserDetails(member);
									ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
									if (member.getMemberRole().getParameterCode()
											.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
										activity.setAttended(true);
									}
									activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
									activity.setScCommitteeMember(member);
									setUserDetails(activity);
									addedActivities.add(activity);
								}
							}
							resDTO.getQtCommittee().getCommitteeActivities().addAll(addedActivities);
							Collections.sort(resDTO.getQtCommittee().getCommitteeActivities());
							this.getManagedBean().setCommitteeMemberList(
									resDTO.getQtCommittee().getCommitteeActivities());
							this.getManagedBean().setCommitteeMemberActivity(sccommittee.getScCommitteeMembers());// 25037 - note 568
						} else {
							List<ScQtCommitteeActivity> localList = new ArrayList<>();
							if (CollectionUtils.isNotEmpty(sccommittee.getScCommitteeMembers())) {
								for (ScCommitteeMember scCommitteeMember : sccommittee.getScCommitteeMembers()) {
									scCommitteeMember.setScCommittee(sccommittee);
						// 25037 - end
									setUserDetails(scCommitteeMember);

									ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
									if (scCommitteeMember.getMemberRole().getParameterCode()
											.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
										activity.setAttended(true);

									}
									activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
									activity.setScCommitteeMember(scCommitteeMember);
									setUserDetails(activity);
									localList.add(activity);
								}

							}

							Collections.sort(localList);
							this.getManagedBean().setCommitteeMemberList(localList);
							this.getManagedBean().setCommitteeMemberActivity(sccommittee.getScCommitteeMembers());// 25037 - note 568
						}

					}

					List<ScQtCommitteRemarks> qtRemarkList = this.getManagedBean().getScQtCommittee()
							.getQtCommitteRemarks();

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
								this.getManagedBean().getScQtCommittee().getMeetingAttendances());
					}

				}
			// 25037 - start - note 568
				prepareUpdateCommitteeMember();
			}
			// 25037 - end - note 568
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
			
			// 26339  -venkat start
			String title =this.getManagedBean().getScQtChecklist().getTitle();
			
			if(title.equals(ParameterConst.CREDIT_FACILITIES_TITLE) ||
					title.equals(ParameterConst.ASSET_FACILITIES_TITLE)) {
				this.getManagedBean().setFP_Compliance(true);
			} else {
				this.getManagedBean().setFP_Compliance(false);
			}
			// 26339  -venkat End	
			
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
	
	// #28786 - start
	public String complianceUploadBasedOnSupp(ScQtProposalChecklist proposalCheckList, String checkListType) {
		this.getManagedBean().setQtProposalChecklist(proposalCheckList);
		this.getManagedBean().setCheckListType(checkListType);

		String proposalPage = null;
		
		SPDetailBackingBean detailBackingBean = new SPDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(this.getManagedBean().getScQt());
		detailBackingBean.getManagedBean().setReadOnly(true);
		//#28786 fix under 28784 change hardcoded to constant - start
		//Bug #34745 start
		if (proposalCheckList.getScQtCheckList().getTitle().equals(SourcingConstants.PENGESAHAN_TIGA_BULAN)
					&& proposalCheckList.getScQtCheckList().getPmDocument() != null) {
			retrieveAllFinancialChecklist(SourcingConstants.PENYATA_TIGA_BULAN);
			retrieveAllFinancialChecklist("Penyata bank untuk tiga bulan");
			retrieveComplianceUploadedProposalChecklist(proposalCheckList, true);
			
			proposalPage = detailBackingBean.retrieveBankStmtDtls(this
					.getManagedBean().getComplianceQtProposalChecklist());
		}
		
		if (proposalCheckList.getScQtCheckList().getTitle().equals(SourcingConstants.KUNCI_KIRA_KIRA_TAHUNAN)
					&& proposalCheckList.getScQtCheckList().getPmDocument() != null) {
			retrieveAllFinancialChecklist(SourcingConstants.ASSET_DAN_LIABILITI);
			retrieveComplianceUploadedProposalChecklist(proposalCheckList, true);
			
			proposalPage = detailBackingBean.retrieveAssetLiabilityDtls(this.getManagedBean().getComplianceQtProposalChecklist());
		}
		
		if (proposalCheckList.getScQtCheckList().getTitle().equals(SourcingConstants.PENGESAHAN_DARI_INSITUSI)
					&& proposalCheckList.getScQtCheckList().getPmDocument() != null) {
			retrieveAllFinancialChecklist(SourcingConstants.KEMUDAHAN_KREDIT);
			retrieveComplianceUploadedProposalChecklist(proposalCheckList, true);
			
			proposalPage = detailBackingBean.retrieveCreditFacilitiesDtls(this.getManagedBean().getComplianceQtProposalChecklist());
		}
		//Bug #34745 end
		//#28786 fix under 28784 change hardcoded to constant - end
		this.getManagedBean().setProposalPage(proposalPage);
		
		return "/xhtml/sc/evaluation/compliance/financialComplianceSupplierList.xhtml";
	}
	// #28786 - end

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
				// #11104-start
				for (ScQtSampleItem sampleItem : sampleCompliance) {
					PmParameter sampleSubStatus = sampleItem.getSubmissionStatus();

					if (null != sampleSubStatus.getParameterId()
							&& ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_COMPLIANCE.equals(sampleSubStatus
									.getParameterCode())) {
						String sampleItemRemark = sampleItem.getRemark();

						if (StringUtils.isBlank(sampleItemRemark) || StringUtils.isEmpty(sampleItemRemark)) {
							throw new EpException(MessageConst.ERROR_760545);
						}
					}
				}
				// #11104-End

				sampleLabel = sampleCompliance.get(0).getScQtSampleLable();
				reqDTO.setSampleComplianceList(sampleCompliance);
				reqDTO.setSampleLabel(this.getManagedBean().getScQtSampleLbl()); // 11104
			}
			
			// #26339 - Start
			EvaluationResDTO evaluationResDTO = evaluationSo.saveOrUpdateSampleComplianceDtls(reqDTO);
			
			if (null != evaluationResDTO && CollectionUtils.isNotEmpty(evaluationResDTO.getSampleItemList())) {
				if (null != getManagedBean().getComplianceSampleLblsupplierDM() && CollectionUtils.isNotEmpty(getManagedBean().getComplianceSampleLblsupplierDM().getList())) {
					
					for (ScQtSampleLable qtSampleLable : getManagedBean().getComplianceSampleLblsupplierDM().getList()) {
						if (sampleLabel.getSampleLableId().longValue() == qtSampleLable.getSampleLableId().longValue()) {
							
							for (ScQtSampleItem sampleItem : this.getManagedBean()
									.getComplianceSampleItemListDM().getList()) {
								
								for (ScQtSampleItem lblSampleItem : qtSampleLable.getSampleItems()) {
									if (lblSampleItem.getSampleItemId().longValue() == sampleItem.getSampleItemId().longValue()) {
										lblSampleItem.setSubmissionStatus(sampleItem.getSubmissionStatus());
										
										break;
									}
								}
							}
							
							break;
						}
					}
				}
			}
			// #26339 - End

			// 11104-start
			if (null != evaluationResDTO && null != evaluationResDTO.getSampleLabel()) {
				List<ScQtSampleLable> samplelableList = getManagedBean().getComplianceSampleLblsupplierDM().getList();
				int index = samplelableList.indexOf(this.getManagedBean().getScQtSampleLbl());

				samplelableList.set(index, evaluationResDTO.getSampleLabel());

				this.getManagedBean().setScQtSampleLbl(evaluationResDTO.getSampleLabel());
			}
			// 11104-end

			if (null != sampleLabel) {
				this.getManagedBean()
						.setComplianceSampleItemListDM(
								new ComplianceSampleItemsPagingDataModel(
										"sampleItemId", sampleLabel
												.getSampleLableId()));
			}

			getManagedBean().setComplianceSummaryGenerated(false);
			updateSummaryGeneratedStatus();
			if ((ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType()))
					|| (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType()))) {
				complianceChangeScQtStatusUpdate();
			}
			
			// #26544, #25638 -start-
			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.prepare.evaluation.compliance"), " ",
					Boolean.FALSE);
			// #26544, #25638 -end-
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

			//#28786 - start
			List<ScQtProposalChecklist> technicalCompliance = new ArrayList<ScQtProposalChecklist>();

			if(null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType().equalsIgnoreCase("T")) {
				technicalCompliance = (List<ScQtProposalChecklist>) this
						.getManagedBean()
						.getComplianceTechnicalProposalCheckListDM().getList();
				reqDTO.setCheckList(this.getManagedBean().getScQtChecklist());
			} else if(null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType().equalsIgnoreCase("F")) {
				if (null != this.getManagedBean()
						.getComplianceTechnicalProposalCheckListDM().getList()) {
					for(ScQtProposalChecklist proposalCheckList : this.getManagedBean()
							.getComplianceTechnicalProposalCheckListDM().getList()) {
						if(proposalCheckList.getScQtProposal().getSupplierNo() == this.getManagedBean().getQtProposalChecklist().getScQtProposal().getSupplierNo()) {
							technicalCompliance.add(proposalCheckList);
						}
					}
					
					reqDTO.setCheckList(this.getManagedBean().getQtProposalChecklist().getScQtCheckList());
				} else {
					for(ScQtProposalChecklist proposalCheckList2 : this.getManagedBean()
							.getComplianceFinancialProposalCheckList()) {
						if(proposalCheckList2.getScQtProposal().getSupplierNo() == this.getManagedBean().getQtProposalChecklist().getScQtProposal().getSupplierNo()) {
							proposalCheckList2.setCompliance(this.getManagedBean().getQtProposalChecklist().getCompliance());
							
							if (null != this.getManagedBean().getQtProposalChecklist().getRemark()) {
								proposalCheckList2.setRemark(this.getManagedBean().getQtProposalChecklist().getRemark());
							}
							
							technicalCompliance.add(proposalCheckList2);
							
							reqDTO.setCheckList(proposalCheckList2.getScQtCheckList());
						}
					}
				}
			}
			//#28786 - end

			if (null != technicalCompliance && !technicalCompliance.isEmpty()) {
				for (ScQtProposalChecklist proposalCheckList : technicalCompliance) {
					if (proposalCheckList.getScQtProposal().isSubmitted()) {
						/*
						 * if (null == proposalCheckList.getCompliance()) { //
						 * throw new EpException(MessageConst.ERROR_760204); }
						 */
						
						// #26339 - Start
						if (proposalCheckList.getCompliance() == null && proposalCheckList.getScQtProposalChecklistAttachment() == null) {
							proposalCheckList.setCompliance(new Integer(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE));
						}
						// #26339 - End
						
						if (null != proposalCheckList.getCompliance()
								&&ParameterConst.COMPLIANCE_STATUS_NONCOMPLIANCE==proposalCheckList.getCompliance()) {
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
			
			//#34493, #34427 - Start
			//#28786 - start
			/*
			if (null != this.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList()) {
				for (ScQtProposalChecklist propCheck : this.getManagedBean()
						.getComplianceTechnicalProposalCheckListDM().getList()) {
					if (propCheck.getScQtProposalChecklistAttachment() != null
							&& (propCheck.getCompliance() == null || propCheck
									.getCompliance() == 2)) {
						reqDTO.setEvalStgCompliance(false);
						break;
					} else if (propCheck.getScQtProposalChecklistAttachment() != null
							&& propCheck.getCompliance() != null
							&& propCheck.getCompliance() != 2) {
						reqDTO.setEvalStgCompliance(true);
					} else if (propCheck.getScQtProposalChecklistAttachment() == null) {
						reqDTO.setEvalStgCompliance(true);
					}
				}
			} else {
				for (ScQtProposalChecklist propCheck : this.getManagedBean()
						.getComplianceFinancialProposalCheckList()) {
					if (propCheck.getScQtProposalChecklistAttachment() != null
							&& (propCheck.getCompliance() == null || propCheck
									.getCompliance() == 2)) {
						reqDTO.setEvalStgCompliance(false);
						break;
					} else if (propCheck.getScQtProposalChecklistAttachment() != null
							&& propCheck.getCompliance() != null
							&& propCheck.getCompliance() != 2) {
						reqDTO.setEvalStgCompliance(true);
					} else if (propCheck.getScQtProposalChecklistAttachment() == null) {
						reqDTO.setEvalStgCompliance(true);
					}
				}
			}
			//#28786 - end
			*/
			//#34493, #34427 - END
			
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
				// Bug #33249@Syazani 03/08/2022 -- Start
//				updateCheckListIntoQtCheckLists(qtCheckLists,
//						qtCheckLists.indexOf(getManagedBean()
//								.getScQtChecklist()), resDTO.getCheckList());
				updateCheckListIntoQtCheckLists(qtCheckLists, getManagedBean().getScQtChecklist(), resDTO.getCheckList());
				// Bug #33249@Syazani 03/08/2022 -- End
			}

			/*
			 * this.getManagedBean().setComplianceTechnicalProposalCheckList(
			 * resDTO.getSaveComplianceList());
			 */
			// Bug #33541@Syazani -- Start 07/07/2022
			// Purpose: only renew constructor if coming from Technical and not
			// applicable for Financial due to pagination issue. Financial will only reload data
			if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType().equalsIgnoreCase("T")) {
				this.getManagedBean().setComplianceTechnicalProposalCheckListDM(
						new ComplianceSupplierPagingDataModel(
								"complianceTechnicalSupplier", resDTO
										.getCheckList().getQtChecklistId(), null,
								null, null));
			}
			// Bug #33541@Syazani -- End 07/07/2022
			
			//#28786 - start
			if (null != this.getManagedBean().getCheckListType()
					&& this.getManagedBean().getCheckListType().equalsIgnoreCase("F")) {
				retrieveComplianceUploadedProposalChecklist(this.getManagedBean().getQtProposalChecklist(), false);
			}
			//#28786 - end

			getManagedBean().setComplianceSummaryGenerated(false);
			updateSummaryGeneratedStatus();
			if ((ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType()))
					|| (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType()))) {
				complianceChangeScQtStatusUpdate();
			}

			// #26544, #25638 -start-
			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.prepare.evaluation.compliance"), " ",
					Boolean.FALSE);
			// #26544, #25638 -end-
		} finally {
			evaluationSo = null;
		}
	}
	// Bug #33249@Syazani 03/08/2022 -- Start
	/*private void updateCheckListIntoQtCheckLists(
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
	}*/
	
	/**
	 * Replaced current method above due to replace object from list is not
	 * effective if got more than two(2) list that contains same title with
	 * different mechanism
	 *
	 */
	private void updateCheckListIntoQtCheckLists(List<ScQtCheckList> qtCheckLists, ScQtCheckList oldCurrChecklist, ScQtCheckList toBeUpdateCheckList) {
		if (oldCurrChecklist != null && qtCheckLists != null && qtCheckLists.size() > 0) {
			for (int i = 0; i < qtCheckLists.size(); i++) {
				ScQtCheckList tempChecklist = qtCheckLists.get(i);
				Long tempChckLstId = tempChecklist.getQtChecklistId();
				Long oldChckLstId = oldCurrChecklist.getQtChecklistId();
				if (tempChckLstId.equals(oldChckLstId)) {
					qtCheckLists.set(i, toBeUpdateCheckList);
					break;
				}
			}
		}
		this.getManagedBean().setScQtChecklist(toBeUpdateCheckList);
	}
	// Bug #33249@Syazani 03/08/2022 -- End

	/**
	 * Save presentation demo dtls.user click on save button in presentation demo tab.
	 */
	public void savePresentationDemoDtls() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> presentatiodemo = this.getManagedBean()
					.getPresentationDemoSupplierListDM().getList();
			List<ScQtProposalChecklistAttachment> toBeRemoveChecklist=this.getManagedBean().getToBeRemovedProposalAttachmentsChecklists();
			if(CollectionUtils.isNotEmpty(toBeRemoveChecklist)){
				reqDTO.setToBeRemovedProposalAttachmentsChecklists(toBeRemoveChecklist);
			}
			if (null != presentatiodemo && !presentatiodemo.isEmpty()) {
				PmStatus Status = getPmStatusByStatusId(QTStatusConst.PR_COMPLETED);
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
			evaluationSo = null;
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

	// #28786 - start
	public void retrieveAllFinancialChecklist(String title) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;

		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);

			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			for (ScQtCheckList checklistOfTitle : resDTO.getCheckListResultBean().getResultList()) {
				if (checklistOfTitle.getTitle().equals(title) && checklistOfTitle.getPmDocument() != null) {//Bug #34745
					this.getManagedBean().setComplianceQtChecklist(checklistOfTitle);
				}
			}

		} finally {
			evaluationSO = null;
		}
	}
	// #28786 - end

	// #28786 - start
	public void retrieveComplianceUploadedProposalChecklist(
			ScQtProposalChecklist proposalCheckList, boolean isComplianceTrue) {

		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();

		try {
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			if (isComplianceTrue) {
				reqDTO.setCheckListId(this.getManagedBean().getComplianceQtChecklist().getQtChecklistId());
			} else {
				reqDTO.setCheckListId(proposalCheckList.getScQtCheckList().getQtChecklistId());
			}

			reqDTO.setCompliance(null);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(null);
			reqDTO.setProposalChecklistSearchBean(null);

			reqDTO.setProposalChecklist(proposalCheckList); //Bug #38123 CRM#5224901: MOE issue 16/4/2025
			resDTO = evaluationSo.retrieveComplianceDtlsByCheckList(reqDTO);

			if (null != resDTO
					&& null != resDTO.getComplianceResultBean()
					&& null != resDTO.getComplianceResultBean().getResultList()
					&& !resDTO.getComplianceResultBean().getResultList().isEmpty() && isComplianceTrue) {
				for (ScQtProposalChecklist propCheck : resDTO
						.getComplianceResultBean().getResultList()) {
					if (propCheck.getScQtProposal().getQtProposalId() == proposalCheckList
							.getScQtProposal().getQtProposalId()
							|| propCheck.getScQtProposal().getSupplierName()
									.equalsIgnoreCase(
											proposalCheckList.getScQtProposal().getSupplierName())) {
						this.getManagedBean().setComplianceQtProposalChecklist(propCheck);
					}
				}
			} else if (!isComplianceTrue) {
				this.getManagedBean().setComplianceFinancialProposalCheckList(
						resDTO.getComplianceResultBean().getResultList());
			}
		} finally {
			evaluationSo = null;
			resDTO = null;
		}
	}
	// #28786 - end

	/**
	 * Evaluation backaction.user clicks back button in compliance stage and
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

	//#28786 - start
	public String complianceFinancialBackaction() {
		this.getManagedBean().setTechindex(-1);
		this.getManagedBean().getComplianceTechnicalProposalCheckListDM().reloadDataWithoutReset(); // Bug #33541@Syazani 07/07/2022
		return "/xhtml/sc/evaluation/compliance/financialComplianceEditSuppliers.xhtml";
	}
	//#28786 - end

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
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);

			reqDTO.setCompliance(null);
			reqDTO.setComplaianceProposalChecklist(SourcingConstants.COMPLIANCE_PROPOSAL_CHECKLIST);

			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
			/*if (null != resDTO.getCheckListResultBean().getResultList()
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
							checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

						}

					} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
							.equalsIgnoreCase(checkList.getDefinationType()
									.getParameterCode())) {
						if (ParameterConst.EVALUATION_TYPES_TWO_TIER
								.equalsIgnoreCase(this.getManagedBean()
										.getScQt().getEvaluationType())
								&& !this.getManagedBean()
										.getCurrentStatusId()
										.equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)) {
							if (null != checkList.getPmEvalStatus()
									&& QTStatusConst.TS_PENDING_SUBMISSION
											.equals(checkList.getPmEvalStatus()
													.getStatusId())) {
								checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

							}

						}

					}

				}

			}*/

			this.getManagedBean().setComplianceTechnicalCheckList(
					resDTO.getCheckListResultBean().getResultList());
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getComplianceTechnicalCheckList())){
			checklistSorting(this.getManagedBean().getComplianceTechnicalCheckList());
			}
		} finally {
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
	public void retriveComplianceFinancial(boolean value) {
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = null;

		try {
			if (this.getManagedBean().getScQt().isSampleReq() && value) {
				this.getManagedBean().setComplianceActiveIndex(2);
			} else {
				if(value){
				this.getManagedBean().setComplianceActiveIndex(1);
				}
			}
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			reqDTO.setComplaianceProposalChecklist(SourcingConstants.COMPLIANCE_PROPOSAL_CHECKLIST);
			resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);

			/*if (null != resDTO.getCheckListResultBean().getResultList()
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
							checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

						}

					}

				}

			}*/

			this.getManagedBean().setCompliancefinancialCheckList(
					resDTO.getCheckListResultBean().getResultList());
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getCompliancefinancialCheckList())){
				checklistSorting(this.getManagedBean().getCompliancefinancialCheckList());
				}

		} finally {
			evaluationSO = null;
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
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
					// #25638 - Start
					|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION)
					// #25638 - End
					// #25638 - #31493 - Start.
					|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION)
			// #25638 - #31493 - End.
			) {
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
			evaluationSO = null;
		}

	}

	/**
	 * Generate evaluation stage.display all the scores for supplies in
	 * evaluation stage
	 */
	private boolean generateEvaluationStage() { //Bug #31751

		IQTEvaluationSO evaluationSO = null;
		boolean isSuccess = false; //Bug #31751
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
			//Bug #31751 - start
			isSuccess = true; 
		}catch(Exception e){
			log.info("Error while decrypt data redmine:31751::qtId:"+this.getManagedBean().getScQt().getQtId() +"::committeeType:"+this.getManagedBean().getCommitteeType());
			//Bug #31751 - end
		} finally {
			evaluationSO = null;

		}
		return isSuccess; //Bug #31751
	}

	private boolean generateFinancialEvaluationresults() { //Bug #31751
		IQTEvaluationSO evaluationSO = null;
		boolean isSuccess = false; //Bug #31751
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			//#28784 add demo condition - Start
			String evalStageDemoOrTechnical = demoOrTechnicalEvaluation();
			reqDTO.setEvalStage(evalStageDemoOrTechnical);
			//#28784 add demo condition - End
			evaluationSO.generateEvaluationSheet(reqDTO);
			//Bug #31751 - start
			isSuccess = true; 
		}catch(Exception e){
				log.info("Error while decrypt data redmine:31751::qtId:"+this.getManagedBean().getScQt().getQtId() +"::committeeType:"+this.getManagedBean().getCommitteeType());
			//Bug #31751 - end
		} finally {
			evaluationSO = null;
		}
		return  isSuccess; //Bug #31751
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

			// #28784 WIP @idham - Start
			if (this.getManagedBean().getScQt().getEvaluationType().equals(ParameterConst.EVALUATION_TYPES_TWO_TIER)
					&& this.getManagedBean().getScQt().isDemoRequired()) {
				boolean demoWip = evaluationSO.checkDemoWip(reqDTO);
				this.getManagedBean().setDemoWip(demoWip);
			} else {
				this.getManagedBean().setDemoWip(false);
			}
			// #28784 WIP @idham - End
			
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
			this.getManagedBean().setEvalStatusComp(true);//#28784

			this.getManagedBean().setEvaluationStgTechnicalCheckList(
					localCheckList);
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getEvaluationStgTechnicalCheckList())){
				checklistSorting(this.getManagedBean().getEvaluationStgTechnicalCheckList());
				}
			checkTechnicalSuppliersQualified();
			localCheckList=null;
			evalstage=null;
		} finally {
			evaluationSO = null;
			resDTO = null;

		}
		// return "/xhtml/sc/evaluation/evaluationMain.xhtml";

	}

	/*
	 * check the status for ptj upload and supplier upload checklists.
	 * @param checkList
	 *               (Mandatory)
	 *  @param evalStage
	 *              (Mandatory)  
	 *    
	 *  return   ScQtCheckList.                      
	 */
	private ScQtCheckList checkStausForUploadNoActionCheckLists(
			ScQtCheckList checkList, String evalStage) {
		IQTEvaluationSO evaluationSO = null;
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
				checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

			} else {
				if (null != checkList.getPmEvalStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList
								.getPmEvalStatus().getStatusId())) {
					checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				}

			}

		} finally {
			evaluationSO = null;
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
			if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				checkFinancialSuppliersQualified();

			}
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getEvaluationStgfinancialCheckList())){
				checklistSorting(this.getManagedBean().getEvaluationStgfinancialCheckList());
				}
			localCheckList=null;
			evalstage=null;
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
		afterDemoInv(false);//#28784
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
							.setRecomandationType(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
				} else {

					evalReqDTO
							.setRecomandationType(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
				}
				List<String> resultStg = new ArrayList<String>();
				resultStg.add(QTConstants.TECHNICAL_EVALUATION);
				evalReqDTO.setResultStages(resultStg);

				// #25638 -start-
				Long[] technicalEvaluationStatus = new Long[] { QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL,
						QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION };
				
				if (Arrays.asList(technicalEvaluationStatus).contains(this.getManagedBean().getCurrentStatusId())
						&& !this.getManagedBean().isReadOnly()) {
					// #25638 -end-
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

				// #25638 -start-
				if (Arrays.asList(technicalEvaluationStatus).contains(this.getManagedBean().getCurrentStatusId())
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isTechnicalSummaryGenerated()) {
					// #25638 -end-
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

				// #25638 -start-
				Long[] financialEvaluationStatus = new Long[] { QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL,
						QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL_REVISION };
				
				if (Arrays.asList(financialEvaluationStatus).contains(this.getManagedBean().getCurrentStatusId())
						&& !this.getManagedBean().isReadOnly() && !getManagedBean().isFinancialSummaryGenerated()
						// #35384 recalculate when at endorse or sign off -start-
						// #38074 remove status revision
						|| (null == this.getManagedBean().getScQt().getFecEvaluationCheck()
						&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_ENDORSEMENT) 
						|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_SIGN_OFF)))
						// #35384 recalculate when at endorse or sign off -end-
				) {
					// #25638 -end-
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

				// #25638 -start-
				Long[] evaluationStatus = new Long[] { QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL,
						QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION };
				// #25638 -end-
				
				if (this.getManagedBean().getScQt().getEvaluationType()
						.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER)
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

					// #25638 -start-
					if (Arrays.asList(evaluationStatus).contains(this.getManagedBean().getCurrentStatusId())
							&& !this.getManagedBean().isReadOnly()
							&& !getManagedBean().isFinancialSummaryGenerated()) {
						// #25638 -end-
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
						.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER)
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
							.setRecomandationType(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
					List<String> resultStg = new ArrayList<String>();
					resultStg.add(QTConstants.TECHNICAL_EVALUATION);
					evalReqDTO.setResultStages(resultStg);

					// #25638 -start-
					if (Arrays.asList(evaluationStatus).contains(this.getManagedBean().getCurrentStatusId())
							&& !this.getManagedBean().isReadOnly()) {
						// #25638 -end-
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

					if (// #25638 -Start.
					(this.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL) || this
							.getManagedBean()
							.getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
							// #25638 - End.
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

			this.getManagedBean().setPassingScore(resDTO != null ? resDTO.getPassingScore() : BigDecimal.ZERO.intValue());
			
			//28784 - Start
            if (this.getManagedBean().getScQt().isDemoRequired() && !this.getManagedBean().isDemoWip()) {
            	/* use in 4ser
            	retriveSummaryEvalPresentationDemo();*/
            	this.getManagedBean().setEvalStatusComp(false);
            }
            //28784 - End

		} finally {
			evaluationSo = null;
		}
		
		// #28784 add remarks - Start
		if (checkDemoStatuses()) {
			retrieveDemoRemarks();
		}
		// #28784 add remarks - End

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
					// #25638 -start-
					|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION)
					// #25638 -end-
					&& !this.getManagedBean().isReadOnly()
					&& this.getManagedBean().getScQt().getEvaluationType()
							.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER)
					&& this.getManagedBean().getScQt().isTechEvaluationReq()
					// #35384 recalculate at endorse or sign off -start-
					// #38074 remove status revision
					|| (null == this.getManagedBean().getScQt().getFecEvaluationCheck()
					&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_ENDORSEMENT) 
					|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_SIGN_OFF)))
					// #35384 recalculate at endorse or sign off -end-
			) { 
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

				if (// #25638 -Start.
				(this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL) || this
						.getManagedBean()
						.getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
						// #25638 - End.
						&& !this.getManagedBean().isReadOnly()
						&& !getManagedBean().isFinancialSummaryGenerated()
						// #35384 recalculate at endorse or sign off -start-
						// #38074 remove status revision
						|| (null == this.getManagedBean().getScQt().getFecEvaluationCheck()
						&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_ENDORSEMENT) 
						|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_SIGN_OFF)))
				        // #35384 recalculate at endorse or sign off -end-
				) {
					financialCheckListStatusCheck();
					evaluationSo
							.prepareFinancialEvaluationSummary(summaryReqDTO);
					getManagedBean().setFinancialSummaryGenerated(true);
				}
			}
		} finally {
			evaluationSo = null;
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

			if (// #25638 -Start.
					(this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
					|| this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
				// #25638 - End.
					&& !this.getManagedBean().isReadOnly()
					&& this.getManagedBean().getScQt().getEvaluationType()
							.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER)
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

				if (// #25638 -Start.
						(this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						|| this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
					// #25638 - End.
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
			evaluationSo = null;
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

				if (// #25638 -Start.
						(this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						|| this.getManagedBean().getCurrentStatusId()
						.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
					// #25638 - End.
						&& !this.getManagedBean().isReadOnly()
						&& this.getManagedBean()
								.getScQt()
								.getEvaluationType()
								.equalsIgnoreCase(
										ParameterConst.EVALUATION_TYPES_ONE_TIER)
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
			evaluationSo = null;
		}

	}

	
	/**
	 * check weather the checklist status is pending or completed.
	 * 
	 */
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
			this.getManagedBean().setTemplateSpecBean(new TemplateSpecBean());
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
			this.getManagedBean().setTemplateSpecBean(new TemplateSpecBean());
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
				this.getManagedBean().setUserScoreSchemaType(QTConstants.USER_SCORE);
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
			this.getManagedBean().setTemplateSpecBean(new TemplateSpecBean());
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
			 evaluationSo = null;
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

	
	/**
	 * create menu model for financial spec zones .
	 * 
	 */
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
					//zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("specTabChange([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]); blockWidget.hide();");
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
	 *  @param TplSpecZoneDtl
	 *                     (optional)          
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
						BigDecimal fromvarieance = estimatedPrice.multiply(packageOfferPrice.getFromVariance()).divide(
								new BigDecimal(100), 2,
								BigDecimal.ROUND_HALF_UP);
						this.getManagedBean().setPackageFromVarieance(
								estimatedPrice.subtract(fromvarieance));

					}
					if (null != packageOfferPrice.getToVariance()) {
						BigDecimal tovarieance=	 estimatedPrice.multiply(packageOfferPrice.getToVariance()).divide(
										new BigDecimal(100), 2,
										BigDecimal.ROUND_HALF_UP);
							this.getManagedBean().setPackageToVarieance(
									estimatedPrice.add(tovarieance));

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
								BigDecimal fromvarieance = zoneSpecresDTO
										.getEstimatedPrices().multiply(packageOfferPrice.getFromVariance()).divide(
										new BigDecimal(100), 2,
										BigDecimal.ROUND_HALF_UP);
								this.getManagedBean().setPackageFromVarieance(
										zoneSpecresDTO
										.getEstimatedPrices().subtract(fromvarieance));

							}
							if (null != packageOfferPrice.getToVariance()) {
								BigDecimal tovarieance=	 zoneSpecresDTO
										.getEstimatedPrices().multiply(packageOfferPrice.getToVariance()).divide(
										new BigDecimal(100), 2,
										BigDecimal.ROUND_HALF_UP);
								this.getManagedBean().setPackageToVarieance(
										zoneSpecresDTO
										.getEstimatedPrices().add(tovarieance));

							}

							this.getManagedBean().setFinSpecZoneEstimatedPrice(
									zoneSpecresDTO.getEstimatedPrices());

						}

					}

				}

			}
		} finally {
			 templateSpecSo=null;;
			 zoneSpecresDTO=null;
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
			this.getManagedBean().setTemplateSpecBean(new TemplateSpecBean());
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
				this.getManagedBean().setUserScoreSchemaType(QTConstants.USER_SCORE);
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
							// #28784 - FEC set eval type for package and Item
							//Used by 4serangkai
							/**if(this.getManagedBean().getTabItemPackageIndex() == 1){
								specDtls.setEvalStage(QTConstants.TECHNICAL_EVALUATION_BY_ITEM);
							} else {
							specDtls.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);
							}*/
							specDtls.setEvalStage(QTConstants.PRESENTATION_DEMO_EVALUATION);//delete this code after uncomment above
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
			evaluationSo = null;
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
	 * 
	 *            
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
			log.debug("Selected Zone : " + specZoneDtl.getZoneName());
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
	 * technical supplier evaluate.Event handle to display the supplier
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
						null);
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
					this.getManagedBean().setUserScoreSchemaType(QTConstants.USER_SCORE);
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
						null);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());

				if (resDTO.getItemResultBean().getTotalRecord() > 0) {
					this.getManagedBean().setSampleItemList(
							resDTO.getItemResultBean().getResultList());

					// #11104-start
					if (null != resDTO.getItemResultBean().getResultList().get(0).getScRequestItem()
							.getTplSpecQuestion().getParentSpecQuestion()) {
						getManagedBean().setItemSampleReq(
								resDTO.getItemResultBean().getResultList().get(0).getScRequestItem()
										.getTplSpecQuestion().getParentSpecQuestion().isSampleReq());
					}
					// #11104_End
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
						null);
				this.getManagedBean().setTechnicalGovNoOfContracts(
						null);
				this.getManagedBean().setTechnicalGovNoOfYears(
						null);
				this.getManagedBean().setTechGovProposalScore(
						new ArrayList<ScQtProposalScore>());
				this.getManagedBean().setTechnicalGovNoOfContracts(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setTechnicalGovNoOfYears(
						new ArrayList<ScQtCheckListScore>());
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
					//List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();

					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV) {

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
							} 
						}
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV);
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
					
					//Enhancement #18723 To Generate Company Experience from SM Module
					proposalPage = detailBackingBean.getTecCompanyExpDtls(proposalCheckList);
//					proposalPage = detailBackingBean.retrieveTecCompanyExpDtls(proposalCheckList);
					//end of Enhancement #18723
					
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/techGovCompanyExperience.xhtml";
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
					//List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();

					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV) {

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

							}
						}
					}

					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						for (ScQtProposalScore proposalScore : resDTO
								.getProposalScores()) {
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV);
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

					List<ScQtCheckListScore> technicalStaff = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE);
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
					detailBackingBean.getManagedBean().setQtProposal(proposalCheckList.getScQtProposal());

					proposalPage = detailBackingBean
							.retriveTechnicalStaffDtls(proposalCheckList);
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/technicalStaffList.xhtml";

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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE);
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
					detailBackingBean.getManagedBean().setQtProposal(proposalCheckList.getScQtProposal());
					proposalPage = detailBackingBean
							.retrieveEmployeeDtls(proposalCheckList);
					evaluationPage = "/xhtml/sc/evaluation/evaluationStage/technical/technicalNoOfEmployees.xhtml";

				}

			}

		} finally {
			evaluationSO = null;
		}

		this.getManagedBean().setProposalPage(proposalPage);
		this.getManagedBean().setEvaluationPage(evaluationPage);

		return "technicalotherchecklists.xhtml";
	}

	/**
	 *  financial supplier evaluate.Event handle to display the supplier
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
		//ITemplateSpecSO templateSpecSO = null;
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
				
				//TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				//templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance().getServiceObject("cc", ITemplateSpecSO.class);

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

				this.getManagedBean().setSpecItemScoringList(
						null);
				this.getManagedBean().setSpecItemScoringList(
						new ArrayList<TplSpecScoring>());
				Map<Long, TplSpecScoring> distinctScoring = new HashMap<Long, TplSpecScoring>();
				if (null != templateSpecBean.getSpecAnswerList()
						&& !templateSpecBean.getSpecAnswerList().isEmpty()) {
					for (TplSpecAnswer specAnswers : templateSpecBean
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

						// #31857 -start-
						final List<TplSpecScoring> scoringList = new ArrayList<TplSpecScoring>(distinctScoring.values());
						// #31857 -end-

						this.getManagedBean().setSpecItemScoringList(scoringList);
						
						// #31857 - start
						Collections.sort(scoringList, new Comparator<TplSpecScoring>() {
							public int compare(TplSpecScoring o1, TplSpecScoring o2) {

								return o1.getTplSpecQuestion().getSpecQuestionId()
										.compareTo(o2.getTplSpecQuestion().getSpecQuestionId());
							}
						});

						this.getManagedBean().setSpecItemScoringList(scoringList);
						// #31857 - end
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
						null);
				this.getManagedBean().setTechnicalGovNoOfContracts(
						null);
				this.getManagedBean().setTechnicalGovNoOfYears(
						null);
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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV);
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
					proposalPage = detailBackingBean.getFinCompanyExpDtls(proposalCheckList);
					//proposalPage = detailBackingBean.retrieveFinCompanyExpDtls(proposalCheckList);
					
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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV);
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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT);
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
					/*this.getManagedBean().setTechGovProposalScore(
							null);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());*/
					List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES);
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
				/*	this.getManagedBean().setTechGovProposalScore(
							null);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());*/

					List<ScQtCheckListScore> cashOhHand = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {

							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND);
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
				/*	this.getManagedBean().setTechGovProposalScore(
							null);
					this.getManagedBean().setTechGovProposalScore(
							new ArrayList<ScQtProposalScore>());*/
					List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
					List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
					if (resDTO.getScoreResultBean().getTotalRecord() > 0) {
						for (ScQtCheckListScore checklistScore : resDTO
								.getScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO) {

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
							
							 else if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO) {
								this.getManagedBean()
										.setNoOfContractsProposalScore(
												proposalScore);
								this.getManagedBean()
										.getTechGovProposalScore()
										.add(this
												.getManagedBean()
												.getNoOfContractsProposalScore());
							}
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);
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
						proposalcurrentRatioScore.setCriteriaType(QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO);
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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT);
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
					detailBackingBean.getManagedBean().setQtProposal(proposalCheckList.getScQtProposal());
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
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS) {

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
							if (proposalScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS) {
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
						proposalScore.setCriteriaType(QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS);
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
					detailBackingBean.getManagedBean().setQtProposal(proposalCheckList.getScQtProposal());

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
          //templateSpecSO=null;
          evaluationSO=null;
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
				this.getManagedBean().setDemoTotalScore(this.getManagedBean().getScQtChecklist()
						.getQtScoreWeightage().getScore());//28784
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
			this.getManagedBean().setEvaluatioStageActiveIndex(3);//#28784
			this.getManagedBean().setDemo(
					this.getManagedBean().getScQt().isDemoRequired());
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setEvalStage(QTConstants.TECHNICAL_EVALUATION);

			// #28784 WIP @idham - Start
			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (this.getManagedBean().getScQt().getEvaluationType().equals(ParameterConst.EVALUATION_TYPES_TWO_TIER)
					&& this.getManagedBean().getScQt().isDemoRequired()) {
				boolean demoWip = evaluationSO.checkDemoWip(reqDTO);
				this.getManagedBean().setDemoWip(demoWip);
			} else {
				this.getManagedBean().setDemoWip(false);
			}

			if (!this.getManagedBean().isDemoWip()) {
				reqDTO.setCompliance(null);
			}
			// #28784 WIP @idham - End
			
			retrivePresetationDemoCheckList();
			if (this.getManagedBean().getScQt().isDemoRequired()) {
				this.getManagedBean().setPresentationDemoSupplierListDM(
						new PresentationDemoSuppliersPagingDataModel(
								"demoSupplierId", reqDTO.getQtId(), reqDTO
										.getEvalStage()));
			}

		} finally {

		}

		// #28784 WIP @idham - Start
		if (!this.getManagedBean().isDemoWip()) {
			retrieveReportTabDtl();
		}
		// #28784 WIP @idham - End
	}

	/**
	 * Retrieve presentation demo summary.display all qualified and disqualified
	 * suppliers in presentation demo summary tab.
	 * @throws Exception 
	 */
	public void retrivePresentationDemoSummary() throws Exception {
		demoAttachmentChecking(); // #28784
		IQTEvaluationSO evaluationSo = null;
		try {

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// #28784 - Start
			boolean demoEvaluation = this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO)
					|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
			boolean nonDemoEvaluation = this.getManagedBean().getCurrentStatusId()
					.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
					|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION);
			// #28784 - End
			
			// #28784 - Start
			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
					&& (nonDemoEvaluation || demoEvaluation)
					&& !this.getManagedBean().isReadOnly()) 
			// #28784 - End
			{
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
			evalReqDTO.setRecomandationType(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
			List<String> resultStg = new ArrayList<String>();
			resultStg.add(SourcingConstants.EVAL_STAGE_PRESENTATION_DEMO);
			evalReqDTO.setResultStages(resultStg);
			evalReqDTO.setDemo(true); // #28784 used in 4serangkai for Itemize
			if ((nonDemoEvaluation || demoEvaluation)
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
			//#28784 - Start
			retrieveAllTechnicalChecklist();
			resDTO = evaluationSo.retrieveAllTechnicalChecklist(reqDTO);
			if (CollectionUtils.isNotEmpty(resDTO.getQtChecklists())) {
				retrieveMaxTechnicalScoreAndOtherElementsScore(resDTO.getQtChecklists());
				/** Use in 4ser
				retrieveMaxTechnicalScoreAndOtherElementsPercentage(this.getManagedBean().getScQt().getQtId());
				*/
			}
			//#28784 - End
		} finally {
			evaluationSo = null;
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
			retriveComplianceFinancial(false);
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
							// #28784 - Start
							if (techCheckList.isScoringReq()
									&& null != techCheckList.getPmEvalStatus()
									&& techCheckList.getPmEvalStatus().getStatusId()
											.equals(QTStatusConst.TS_PENDING_SUBMISSION)
									&& !techCheckList.getDefinationType().getParameterCode()
											.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO))
							// #28784 - End
							{
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
					if (((ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType())) || (ParameterConst.EVALUATION_TYPES_ONE_TIER
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
			if (((ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) || (ParameterConst.EVALUATION_TYPES_ONE_TIER
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

	
	/**
	 * check the technical upload checklist scores in comleted or not.
	 * 
	 */
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
					String checklistScoring = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.scoring.completed", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
					this.getManagedBean().setEvaluatioStageActiveIndex(0);
					throw new EpException(MessageConst.ERROR_760266,
							new String[] { checklist.getTitle(),
									checklistScoring });
				}
			}
		} finally {
			evaluationSo = null;
		}

	}

	
	/**
	 * retrieve upload qualified suppliers based on compliance stage or
	  *  technical stage or presentationdemo stage.
	  *      
	 * 
	 */
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
					String checklistScoring =  ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.scoring.completed", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
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
			evaluationSo = null;
		}

	}

	/**
	 * evaluation maintain previous nextTabIndex.check current tab and next tab for chain.
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
		boolean noProposalSubmitted = this.getManagedBean().isNoProposalSubmitted();// 6906
		try {

			// this.currentTab = currentTab;
			// this.outputTab = outputTab;
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			if (currentTab == 1) {
				if (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)
						|| this.getManagedBean().getCurrentStatusId()
								.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
						// #25638 - Start.
						|| this.getManagedBean().getCurrentStatusId()
								.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION)
						// #25638 - End.
				) {
					// 35148&11104-start
					int complianceIndex = this.getManagedBean().getComplianceActiveIndex();
					int lcaloutputTab = Integer.MIN_VALUE;

					// 35148&11104-33
					if (this.getManagedBean().getScQt().isSampleReq()
							&& QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX > complianceIndex
							&& outputTab >= QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX) {
						retriveComplianceSample();

						outputTab = QTConstants.EVALUATION_MAIN_ACTIVE_INDEX;
					} else {
						lcaloutputTab = retriveFinancialChecklistStatusCheck();
					}
					// 35148&11104-End

					if (lcaloutputTab > 0) {
						outputTab = lcaloutputTab;
						if (outputTab == 7) {
							if (!this.getManagedBean().getScQt().isSampleReq()) {
								// 35148&11104-start
								this.getManagedBean().setComplianceActiveIndex(
										QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX);

								if (QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX == complianceIndex) {
									throw new EpException(MessageConst.ERROR_760239);
								}
								// 35148&11104-End
							} else {
								// 35148&11104-start
								if (this.getManagedBean().getScQt().isSampleReq()
										&& QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX > complianceIndex) {
									this.getManagedBean().setComplianceActiveIndex(
											QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX);
								} else {
									this.getManagedBean().setComplianceActiveIndex(
											QTConstants.COMPLIANCE_FINANCIAL_ACTIVE_INDEX);

									if (QTConstants.COMPLIANCE_FINANCIAL_ACTIVE_INDEX == complianceIndex) {
										throw new EpException(MessageConst.ERROR_760239);
									}
								}
								// 35148&11104-End
							}
						}
					}
				}
			}

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

				// #28784 - Start
				afterDemoInv(false);

				if (this.getManagedBean().getScQt().isDemoRequired()
						&& (SCProcessNameConst.ENDORSE_TEC_EVALUATION.equalsIgnoreCase(this.getManagedBean()
								.getTaskName()) || SCProcessNameConst.SIGNOFF_TEC_EVALUATION.equalsIgnoreCase(this
								.getManagedBean().getTaskName()))) {
					retriveCommitteeEndorsment();
				}
				// #28784 - End
				
				this.getManagedBean().setCheckReportDownload(false);
				// this.getManagedBean().setCheckListType("");
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					if (// #25638 -Start.
							(this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
							|| this.getManagedBean().getCurrentStatusId()
							.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
						// #25638 - End.
							) {
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

							if (// #25638 -Start.
									(this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
									|| this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
								// #25638 - End.
									) {

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
								if (// #25638 -Start.
										(this.getManagedBean().getCurrentStatusId()
										.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
										|| this.getManagedBean().getCurrentStatusId()
										.equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION))
									// #25638 - End.
										) {
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

								//Bug #31751 - start1	
								boolean isSuccess = generateEvaluationStage();
								if(!isSuccess){
									throw new EpException(MessageConst.ERROR_760530);
								}
								//Bug #31751 - end1
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

								//Bug #31751 - start2
								boolean isSuccess = generateEvaluationStage();
								if(!isSuccess){
									throw new EpException(MessageConst.ERROR_760530);
								}
								//Bug #31751 - end2
								EvaluationReqDTO reqDTO = new EvaluationReqDTO();
								this.getManagedBean().getScQt()
										.setTecEvaluationCheck(1);
								this.getManagedBean().getScQt().setDemoNotificationCheck(null); // 11104

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

							//Bug #31751 - start3
							boolean isSuccess = generateEvaluationStage();
							if(!isSuccess){
								throw new EpException(MessageConst.ERROR_760530);
							}
							//Bug #31751 - end3
							EvaluationReqDTO reqDTO = new EvaluationReqDTO();
							this.getManagedBean().getScQt()
									.setTecEvaluationCheck(1);
							this.getManagedBean().getScQt().setDemoNotificationCheck(null); // 11104

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

					if (!this.getManagedBean().isReadOnly()
							// #35384 recalculate at endorse or sign off -start-
							
							// #38074 remove status revision
							|| this.getManagedBean().isReadOnly()
							&& null == this.getManagedBean().getScQt().getFecEvaluationCheck()
							&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_ENDORSEMENT)
							|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_SIGN_OFF))
							// #35384 recalculate at endorse or sign off -end-
					) {
						if (null == this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {

							//Bug #31751 - start4
							boolean isSuccess = generateEvaluationStage();
							if(!isSuccess){
								throw new EpException(MessageConst.ERROR_760530);
							}
							//Bug #31751 - end4

							// #35384 recalculate at endorse or sign off -start-
							
							// #38074 remove status revision
							if (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_ENDORSEMENT)
									|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_SIGN_OFF)) {
								retriveSummaryEvaluationStage();
							}
							// #35384 recalculate at endorse or sign off -end-

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
					//#28784, #34748 - Start
					if (currentTab == 2) {

						if (this.getManagedBean()
								.getEvaluatioStageActiveIndex() < 1) {
							retriveSummaryEvaluationStage();
							// this.getManagedBean().setComplianceActiveIndex(2);
							outputTab = 2;

						}
						else if(/**|| this.getManagedBean().getTabTechnicalIndex() == 3*/
								this.getManagedBean().getEvaluatioStageActiveIndex() == 1
								&& this.getManagedBean().getScQt().isDemoRequired()){
							if (this.getManagedBean().isDemoWip()) {
								retrieveDemoDetails();									
								outputTab = 3;
							} else {
								retrivePresentationOrDemoEvaluationStage();
								outputTab = 2;
							}

						} else if (this.getManagedBean().getScQt().isDemoRequired()) {
							retrieveDemoDetails();
							outputTab = 3;
						}
					}
					else if (this.getManagedBean().getScQt().isDemoRequired()) {
						retrieveDemoDetails();
					}
					//#28784, #34748 - End
					/**retrivePresentationDemo();*/ //#28784 delete
					if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getCommitteeType())
							&& this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL)
							&& !this.getManagedBean().isReadOnly()
							&& this.getManagedBean().getScQt().isDemoRequired()) {
						if (null == this.getManagedBean().getScQt()
								.getDemoNotificationCheck() && checkDemoWIP()) {//#28784 enable notification for WIP
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
							// #25638 -start-
							&& (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL
									.equals(this.getManagedBean()
											.getCurrentStatusId()) || QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION
									.equals(this.getManagedBean()
											.getCurrentStatusId()))
							// #25638 -end-
							&& !this.getManagedBean().isReadOnly()
							// #35384 avoid run this code below when endorse or sign off -start-
							
							// #38074 remove status revision
							|| this.getManagedBean().isReadOnly()
							&& this.getManagedBean().getScQt().isTechEvaluationReq()
							&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_ENDORSEMENT) 
							|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_SIGN_OFF))
							// #35384 avoid run this code below when endorse or sign off -end-
					) {

						if (currentTab == 2
						// #35384 avoid run this code below when endorse or sign off -start-
						// #38074 remove status revision
								&& (!this.getManagedBean().getCurrentStatusId()
										.equals(QTStatusConst.EC_PENDING_ENDORSEMENT) || !this.getManagedBean()
										.getCurrentStatusId().equals(QTStatusConst.EC_PENDING_SIGN_OFF))
						// #35384 avoid run this code below when endorse or sign off -end-
						) {

							if (this.getManagedBean()
									.getEvaluatioStageActiveIndex() < 1) {
								retriveSummaryEvaluationStage();
								// this.getManagedBean().setComplianceActiveIndex(2);
								outputTab = 2;

							}

						}
						if (null == this.getManagedBean().getScQt()
								.getFecEvaluationCheck()) {

							//Bug #31751 - start5
							boolean isSuccess = generateFinancialEvaluationresults();						
							if(!isSuccess){
								throw new EpException(MessageConst.ERROR_760530);
							}
							//Bug #31751 - end5

							// #35384 add this condition to recalculate new
							// formula when qt is at status endorse/sign off
							// evaluation once deploy into environment
							
							// #38074 remove status revision
							if (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_ENDORSEMENT)
								|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_SIGN_OFF)) {
								retriveOneTierFinancialSupplierSummary();
							}
							// #35384 end

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
					// Bug #36747@Syazani 20/01/2025 -- Start
					if (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL)
							|| this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION)) {
						// Bug #36747@Syazani 20/01/2025 -- End
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
						// #25638 -start-
						&& (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL
								.equals(this.getManagedBean()
										.getCurrentStatusId()) || QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION
								.equals(this.getManagedBean()
										.getCurrentStatusId()))
						// #25638 -end-
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

				// Bug #36747@Syazani 20/01/2025 -- Start
				else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equalsIgnoreCase(this
						.getManagedBean().getCommitteeType())
						&& (this.getManagedBean().getCurrentStatusId()
								.equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL) || this.getManagedBean()
								.getCurrentStatusId().equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION))
						&& !this.getManagedBean().isReadOnly()) {
					// Bug #36747@Syazani 20/01/2025 -- End

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
								demoAttachmentChecking(); // #28784
								retrivePresentationDemoSummary();
								outputTab = 3;
							}
						}
					}
					checkMeetingDetailsAndStatus(this.getManagedBean()
							.getScQtMeetings());
					// Bug #36747@Syazani 20/01/2025 -- Start
				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equalsIgnoreCase(this
						.getManagedBean().getCommitteeType())
						&& (this.getManagedBean().getCurrentStatusId()
								.equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL) || this.getManagedBean()
								.getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL_REVISION))
						&& !this.getManagedBean().isReadOnly()) {
					// Bug #36747@Syazani 20/01/2025 -- End

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
				// Bug #36747@Syazani 20/01/2025 -- Start
				outputTab = evaluationStageSummaryForceViewCheck(currentTab, outputTab);
				
				if (currentTab != 4) {
					retrieveReportTabDtl();
				}
				
				if (outputTab != 2) {
					// Bug #36747@Syazani 20/01/2025 -- End
					retriveCommitteeEndorsment();
				}	// Bug #36747@Syazani 20/01/2025
				
				// RequestContext.getCurrentInstance().update("@form");
			} else if (outputTab == 6) {

			} else if (outputTab == 0) {
				afterDemoInv(false); // #28784
				this.getManagedBean().setCheckReportDownload(false);
				this.getManagedBean().setDemo(false);
				retriveMeetingDtls(this.getManagedBean().getScQt().getQtId(),
						this.getManagedBean().getCommitteeType(), 1);
				
				outputTab = evaluationStageSummaryForceViewCheck(currentTab, outputTab);	// Bug #36747@Syazani 20/01/2025
			}

			if (getManagedBean().getTempTab() < outputTab) {
				// 35148&11104-33 -start-
				if (outputTab == QTConstants.EVALUATION_MAIN_ACTIVE_INDEX) {
					outputTab = QTConstants.COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX;
				} else {
					getManagedBean().setTempTab(outputTab);
				}
				// 35148&11104-33 -end-
			}
			getManagedBean().setTabActiveIndex(outputTab);
			// 6906 - start
			this.getManagedBean().setNoProposalSubmitted(noProposalSubmitted);
			// 6906 - end
		}catch(Exception e){
			log.info("ExceptionBlock"+currentTab);
			log.info("ExceptionBlockCommitteeType"+this.getManagedBean()
					.getCommitteeType());
			
			 if(currentTab==1){
				int complianceIndex = this.getManagedBean().getComplianceActiveIndex(); // 35148&11104
				retriveComplianceTechnical();
				this.getManagedBean().setComplianceActiveIndex(complianceIndex); //35148&11104
			}
			else if(currentTab==2){
				log.debug("TwoExcuted"+currentTab);
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())
						|| ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					log.debug("ifTwoExcutedCommitteeType"+this.getManagedBean()
							.getCommitteeType());
				retriveTechnicalEvaluationStage();
				}
				else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					log.debug("elseTwoExcutedCommitteeType"+this.getManagedBean()
							.getCommitteeType());
					retriveFinancialEvaluationStage();
				}
			}
			else if(currentTab==3){
				log.debug("ThreeExcuted"+currentTab);
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
								.equalsIgnoreCase(this.getManagedBean()
										.getCommitteeType())) {
					log.debug("ifThreeExcutedCommitteeType"+this.getManagedBean()
							.getCommitteeType());
				retriveFinancialEvaluationStage();
				}
				else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					log.debug("elseThreeExcutedCommitteeType"+this.getManagedBean()
							.getCommitteeType());
					retrivePresentationDemo();
					
				}
			}
			
			
			outputTab=currentTab;
			getManagedBean().setTabActiveIndex(outputTab);
			
			handleException(e);
		}
		finally {
			evaluationSo = null;
		}
	}

	
	/**
	 * check the sample compliance submission is completed or not.
	 * 
	 */
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
			evaluationSO = null;
		}

	}

	
	/**
	 * check the meating details status is pending or done.
	 * 
	 */
	private void checkMeetingDetailsAndStatus(
			List<ScQtMeeting> meetingDetailsList) {
		if (null == meetingDetailsList || meetingDetailsList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760253);
		} else {
			int i = 0;
			for (ScQtMeeting meeting : meetingDetailsList) {

				// #29473, #31675_start
				if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
						&& QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
						&& meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				// #29473, #31675 -end-
					i++;

				}
			}
			if (i == 0 && !this.getManagedBean().isAfterDemoInv()) { //#28784
				throw new EpException(MessageConst.ERROR_760255);
			}
		}

	}

	
	/**
	 * this method is used to check the committee members attendance is completed or pending.
	 * 
	 */
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
						ResourceBundleUtils
						.getMessage("lbl.sc.chairperson",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()),
						getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
								this.getManagedBean().getCommitteeType()) });
			}
			if (!isAtleastOneSecretaryAttendance(secretaryList)) {
				throw new EpException(MessageConst.ERROR_760252, new String[] {
						ResourceBundleUtils
						.getMessage("lbl.sc.secretary",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()),
						getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
								this.getManagedBean().getCommitteeType()) });
			}

			if (!isAtleastOneMemberAttendance(memberList)) {
				throw new EpException(MessageConst.ERROR_760423, new String[] {
						ResourceBundleUtils
						.getMessage("lbl.sc.member",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()),
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
			if (i >= 2) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieve report tab dtl.Event handle set attachment for Reports tab.
	 */
	private void retrieveReportTabDtl() {
		log.debug("start retrieveReportTabDtl");
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

			// #28784 WIP @idham - Start
			if (this.getManagedBean().getScQt().getEvaluationType().equals(ParameterConst.EVALUATION_TYPES_TWO_TIER)
					&& this.getManagedBean().getScQt().isDemoRequired()) {
				boolean demoWip = evaluationSO.checkDemoWip(reqDTO);
				
				this.getManagedBean().setDemoWip(demoWip);
			} else {
				this.getManagedBean().setDemoWip(false);
			}
			// #28784 WIP @idham - End
			
			resDTO = evaluationSO.retriveReportDetails(reqDTO);
			if (null != resDTO.getReports() && !resDTO.getReports().isEmpty()) {
				// #28784 - Start
				if (getManagedBean().getScQt().isDemoRequired()) {
					//Add all existing report
					changePerakuanToBottom(resDTO.getReports());
				} else {
					//Remove Ulasan pembentangan demonstrasi from report list
					removeUlasanDemo(resDTO.getReports());
				}
				// #28784 - End
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
						"NGeP-QT-FinancialEvaluation-Committee-Report");

				this.getManagedBean().setScAttachment(attachment);

			}
			log.debug("end retrieveReportTabDtl");
		} finally {
			evaluationSO = null;
		}
		
		// #28784 add remarks - Start
		// use taskname instead of status id
		String taskName = this.getManagedBean().getTaskName();
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF_DEMO);

		if (this.getManagedBean().getScQt().isDemoRequired()
				&& demoStatuses.contains(this.getManagedBean().getCurrentStatusId())) {
			retrieveDemoRemarks();

			if ((SCProcessNameConst.FINALISE_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName))
					&& (this.getManagedBean().getTabActiveIndex() == 5
								|| this.getManagedBean().getTabActiveIndex() == 0
								|| this.getManagedBean().getTabActiveIndex() == 2)) {// use taskname instead of status id
				this.getManagedBean().setReadOnly(false);
			}
		}
		// #28784 add remarks - End
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
      log.debug("start retriveCommitteeEndorsment");
		
		try {
			
			retriveEndorsmentList();

			if (!this.getManagedBean().isReadOnly()) {
				//saveQtReportDtls();
				saveEvaluationReportsDtls();
				setReportAttachment();
			}

			retriveCommitteeRemarksList();

			
			
			
			log.debug("end retriveCommitteeEndorsment");
		} finally {
			

		}
	}
private void retriveEndorsmentList(){
	ICommitteSO committeSO = null;
	CommitteResDTO resDTO = null;
	try{
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		committeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
		resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
		if(null!=resDTO && CollectionUtils.isNotEmpty(resDTO.getCommitteActivities())){
			this.getManagedBean().setEndorscommitteeMemberList(
					new ArrayList<ScQtCommitteeActivity>());
			for(ScQtCommitteeActivity committeeActivity:resDTO.getCommitteActivities()){
				if(committeeActivity.isAttended()){
						// #29668, #26474-start
						if ((SCProcessNameConst.ENDORSE_EC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.ENDORSE_FEC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.ENDORSE_TEC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.ENDORSE_OC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName())) && ParameterConst.COMMITTEE_ROLE_SECRETARY 
							.equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode()) ){ 

							committeeActivity.setSignedOff(Boolean.TRUE);
						} else if ((SCProcessNameConst.SIGNOFF_EC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_FEC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_TEC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_OC_EVALUATION
							.equalsIgnoreCase(this.getManagedBean().getTaskName())) && !ParameterConst.COMMITTEE_ROLE_CHAIRPERSON 
							.equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode()) ){

							committeeActivity.setSignedOff(Boolean.TRUE);
						}
						// #29668, #26474-End

					this.getManagedBean().getEndorscommitteeMemberList().add(committeeActivity);	
				}
			}
			
		}
	}finally{
		 committeSO = null;
		 resDTO = null;
	}
}

private void retriveCommitteeRemarksList(){
	IQTDetailSO qtDetailSO;
	QuationTenderResDTO resDto;
	try{
		qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
		resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
		ScWorkFlowStatus currentWorkFlowStatus = qtCommittee.getCurrentStatus();
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			if (resDto.getRemarks() == null) {

				if (null == this.getManagedBean()
						.getEvaluationCommitteeRemark()) {
					this.getManagedBean().setEvaluationCommitteeRemark(
							new ArrayList<ScQtEvaluationCommitteeRemark>());
					initializeRemarks(scQt,
							currentWorkFlowStatus, 
							this.getManagedBean().getEvaluationCommitteeRemark(),
							ScQtEvaluationCommitteeRemark.class);

				}
				scQt.setScQtEvaluationCommitteeRemarks(
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
					initializeRemarks(scQt,
							currentWorkFlowStatus, 
							this.getManagedBean().getEvaluationCommitteeRemark(),
							ScQtEvaluationCommitteeRemark.class);
				}
				scQt.setScQtEvaluationCommitteeRemarks(
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
					initializeRemarks(scQt,
							currentWorkFlowStatus, 
							this.getManagedBean().getEvaluationOpeningCommitteeRemark(),
							ScQtOpeningCommitteeRemark.class);

				}
				scQt.setScQtOpeningCommitteeRemarks(
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
					initializeRemarks(scQt,
							currentWorkFlowStatus, 
							this.getManagedBean().getEvaluationOpeningCommitteeRemark(),
							ScQtOpeningCommitteeRemark.class);
				}
				scQt.setScQtOpeningCommitteeRemarks(
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
							scQt,
							currentWorkFlowStatus,
							this.getManagedBean().getEvaluationTechnicalCommitteeRemark(),
							ScQtTechnicalCommitteeRemark.class);

				}
				scQt.setScQtTechnicalCommitteeRemarks(
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
							scQt,
							currentWorkFlowStatus,
							this.getManagedBean().getEvaluationTechnicalCommitteeRemark(),
							ScQtTechnicalCommitteeRemark.class);
				}
				scQt.setScQtTechnicalCommitteeRemarks(
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
							scQt,
							currentWorkFlowStatus,
							this.getManagedBean().getEvaluationFinancialCommitteeRemark(),
							ScQtFinancialCommitteeRemark.class);

				}
				scQt.setScQtFinancialCommitteeRemarks(
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
							scQt,
							currentWorkFlowStatus,
							this.getManagedBean().getEvaluationFinancialCommitteeRemark(),
							ScQtFinancialCommitteeRemark.class);
				}
				scQt.setScQtFinancialCommitteeRemarks(
								this.getManagedBean()
										.getEvaluationFinancialCommitteeRemark());
			}
		}
	}finally{
		qtDetailSO=null;
		resDto=null;
	}
}
	
	/**
	 * Committee remarks save.to save the remarks in committee endorsment tab.
	 */
	public EvaluationReqDTO committeeRemarksSave() {
		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		
		try {

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
					//evaluationSo.saveReportDtls(reqDTO);
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
					//evaluationSo.saveReportDtls(reqDTO);
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
					//evaluationSo.saveReportDtls(reqDTO);
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
					//evaluationSo.saveReportDtls(reqDTO);
				}

			}

		} finally {
			
		}
		return reqDTO;
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
	 * @throws Exception 
	 */
	public void saveMeetingDtls(boolean save) throws Exception {
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
			boolean noProposalSubmitted = this.getManagedBean().isNoProposalSubmitted();// 6906 - note 93
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
			if(tabindex == 4){
				saveEvaluationReportsDtls();
				renewSesionObject();
				retriveMeetingDtls(qtId, committeeType, index);
				retrieveReportTabDtl();
			}
			else if(tabindex == 5){
				EvaluationReqDTO endorsmentReqDTO=committeeRemarksSave();
				if(null!=endorsmentReqDTO.getScQt()){
					reqDtO.setScQt(endorsmentReqDTO.getScQt());
				}
				evaluationSO.saveQTDtl(reqDtO);
				renewSesionObject();
				retriveMeetingDtls(qtId, committeeType, index);
				retriveEndorsmentList();
				retriveCommitteeRemarksList();
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
			this.getManagedBean().setNoProposalSubmitted(noProposalSubmitted);// 6906 - note 93

			/* ***#25638#99 - start */
			if(userHasRole(new RoleCode[]{RoleCode.SC_SECRETARY, RoleCode.EC_SECRETARY, RoleCode.TEC_SECRETARY, RoleCode.OC_SECRETARY})
					&& (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL.equals(this
							.getManagedBean().getCurrentStatusId())
						||QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION.equals(this
								.getManagedBean().getCurrentStatusId()	))){
				this.getManagedBean().setStatusOnly(false);
			}
			/* ***#25638#99 - end */
			
			retriveMeetingDtls(qtId, committeeType, index);

			if (save == true) {
				// #26544, #25638 -start-
				successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.prepare.evaluation"), QtNum,
						Boolean.FALSE);
				// #26544, #25638 -end-
			}
		} finally {
			evaluationSO=null;
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
					if(null!=meeting.getMeetingDate()){
						compareMeetingDates(meeting, localMeetingList);
						}else{
							throw new EpException(MessageConst.ERROR_760418);
						}
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
			List<ScQtMeeting> meetingRemoveList = getMeetingToBeRemoveList();
			if (null != meetingRemoveList && !meetingRemoveList.isEmpty()) {
				for (int i = 0; i < meetingRemoveList.size(); i++) {
					if (null == meetingRemoveList.get(i).getQtMeetingId()) {
						meetingRemoveList.remove(i);
					}
				}

			}
			reqDtO.setRemovedmeetingList(meetingRemoveList);

			List<ScQtMeetingAttendance> meetingAttendanceRemoveList = getAttendanceToBeRemoveList();
			if (null != meetingAttendanceRemoveList && !meetingAttendanceRemoveList.isEmpty()) {
				log.debug("before EvaluationBackingBean MeetingAttendance List Size"+meetingAttendanceRemoveList.size());
				for (int i = 0; i < meetingAttendanceRemoveList.size(); i++) {
					if (null == meetingAttendanceRemoveList.get(i).getQtMeetingAttendanceId()) {
						meetingAttendanceRemoveList.remove(i);
					}
				}
				log.debug("after EvaluationBackingBean MeetingAttendance List Size"+meetingAttendanceRemoveList.size());
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

			reqDtO.setScQtCommitte(qtCommittee);
			reqDtO.setPmUser(new FacesScopes().getUserSessionBean());

		} finally {

		}
		return reqDtO;

	}

	/**
	 * Save proposal score dtls.Event handle save all the suppliers scores in
	 * evaluation stage.
	 */
	public void saveProposalScoreDtls() {
		IQTEvaluationSO evaluationSO;
		try {

		 evaluationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationSO.class);

			if (null != this.getManagedBean().getNoOfContractsProposalScore()) {
				ScQtProposalScore contract = this.getManagedBean()
						.getNoOfContractsProposalScore();
				if (null != contract.getUserScore() && null != this.getManagedBean().getUserScores()) {
						if (contract.getUserScore().intValue() > new Integer(
								getManagedBean().getUserScores())) {
							throw new EpException(MessageConst.ERROR_760201);
						}
					
				}

			}
			if (null != this.getManagedBean().getNoOfyearsProposalScore()) {
				ScQtProposalScore contract = this.getManagedBean()
						.getNoOfyearsProposalScore();
				if (null != contract.getUserScore() && null != this.getManagedBean().getContractScores()) {
						if (contract.getUserScore().intValue() > new Integer(
								getManagedBean().getContractScores())) {
							throw new EpException(MessageConst.ERROR_760201);
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

			// #11104-start
			if (this.getManagedBean().isItemSampleReq()) {
				List<ScQtSampleItem> sampleItemlist = this.getManagedBean().getSampleItemList();

				reqDtO.setSampleComplianceList(sampleItemlist);
			}
			// #11104-End

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

			qtProposalChecklist = resDto.getProposalChecklist();
			getManagedBean().setQtProposalChecklist(qtProposalChecklist);
			proposalCheckList.set(index, this.getManagedBean()
					.getQtProposalChecklist());

			if (qtCheckLists != null) {
				// Bug #33249@Syazani 03/08/2022 -- Start
//				updateCheckListIntoQtCheckLists(qtCheckLists,
//						qtCheckLists.indexOf(getManagedBean()
//								.getScQtChecklist()), resDto.getCheckList());
				updateCheckListIntoQtCheckLists(qtCheckLists, getManagedBean().getScQtChecklist(), resDto.getCheckList());
				// Bug #33249@Syazani 03/08/2022 -- End
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

			// #26544, #25638 -start-
			successMessage(MessageConst.INFO_700004,
					getMessageCommon("lbl.sc.prepare.evaluation.evaluationStage.scores"), " ", Boolean.FALSE);
			// #26544, #25638 -end-
		} finally {
			evaluationSO=null;
		}

	}

	
	/**
	 * this method to set downaloadall reports file name and save file name in
	 *  db.
	 * 
	 */
	private void setReportAttachment() throws Exception {
		IQTEvaluationSO evaluationSO = null;
		try {
			EvaluationResDTO evalResDTO = null;
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			this.getManagedBean().setCheckReportDownload(true);
			ScAttachment scAttachment = this.getManagedBean().getScAttachment();
			ScQt scQt = this.getManagedBean().getScQt();
            log.info("Start EvaluationReportAttachment downaload"+scQt.getQtId()+"Time"+DateUtils.getCurrentDate());
			// scQtEvaluationReport.setScQt(this.getManagedBean().getScQt());
			if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				//ScEvaluationCommitteeReportAttachment attachment = scQt
						//.getScEvaluationCommitteeReportAttachment();
				//attachment = new ScEvaluationCommitteeReportAttachment();
				
				// CR#7782
//				List<ScQtEvaluationReportAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
//						.getScQtEvaluationReportList());
//				SaveEvaluationAttachment(this.getManagedBean()
//						.getReportDownloadFileName() + ".pdf", scAttachment);
//				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				//attachment = (ScEvaluationCommitteeReportAttachment) generateDocumentAttachment();
				//attachment.setScQt(scQt);
				//scAttachment = attachment;				
				//scQt.setScEvaluationCommitteeReportAttachment(attachment);
				//reqDTO.setScQt(scQt);
				//evalResDTO = evaluationSO.saveReportDtls(reqDTO);
				//ScAttachment localAttachment = evalResDTO.getScQt()
					//	.getScEvaluationCommitteeReportAttachment();
				//this.getManagedBean().setScQt(evalResDTO.getScQt());
				//this.getManagedBean().setScAttachment(localAttachment);
				// RequestContext.getCurrentInstance().update("@form");
				
				ScEvaluationCommitteeReportAttachment attachment = scQt
						.getScEvaluationCommitteeReportAttachment();
				attachment = new ScEvaluationCommitteeReportAttachment();
				attachment.setScQt(scQt);
				scQt.setScEvaluationCommitteeReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScEvaluationCommitteeReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				 evalResDTO = evaluationSO
						.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScEvaluationCommitteeReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScOpenCommitteeReportAttachment attachment = 
							scQt.getScOpenCommitteeReportAttachment();
				attachment = new ScOpenCommitteeReportAttachment();
				
				//attachment = (ScOpenCommitteeReportAttachment) generateDocumentAttachment();
				attachment.setScQt(scQt);
				scQt.setScOpenCommitteeReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;	
				List<ScAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScOpenCommitteeReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				evalResDTO = evaluationSO.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScOpenCommitteeReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScTechnicalEvaluationReportAttachment attachment = 
						scQt.getScTechnicalEvaluationReportAttachment();
				attachment = new ScTechnicalEvaluationReportAttachment();
				
				//attachment = (ScTechnicalEvaluationReportAttachment) generateDocumentAttachment();
				attachment.setScQt(scQt);
				scQt.setScTechnicalEvaluationReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;	
				List<ScAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScTechnicalEvaluationReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				evalResDTO = evaluationSO.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScTechnicalEvaluationReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
				ScFinancialEvaluationReportAttachment attachment = 
						scQt.getScFinancialEvaluationReportAttachment();
				attachment = new ScFinancialEvaluationReportAttachment();
				
				//attachment = (ScFinancialEvaluationReportAttachment) generateDocumentAttachment();
				attachment.setScQt(scQt);
				scQt.setScFinancialEvaluationReportAttachment(attachment);
				attachment.setDocNo(scQt.getQtNo());
				scAttachment = attachment;
				List<ScAttachment> attachments = convertReportAsReportAttachment(getManagedBean()
						.getScQtEvaluationReportList());
				SaveEvaluationAttachment(this.getManagedBean()
						.getReportDownloadFileName() + ".zip", scAttachment);
				convertmultipleFilesAsaZipFile(attachments, scAttachment);
				scQt.setScFinancialEvaluationReportAttachment(attachment);
				reqDTO.setScQt(scQt);
				evalResDTO = evaluationSO.saveReportDtls(reqDTO);
				ScAttachment localAttachment = evalResDTO.getScQt()
						.getScFinancialEvaluationReportAttachment();
				this.getManagedBean().setScQt(evalResDTO.getScQt());
				this.getManagedBean().setScAttachment(localAttachment);
			}
			  log.info("end EvaluationReportAttachment downaload"+scQt.getQtId()+"Time"+DateUtils.getCurrentDate());
		} finally {
			evaluationSO = null;
		}

	}

	/**
	 * To save Evaluation Attachment
	 * 
	 * @param fileName
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void SaveEvaluationAttachment(String fileName,
			ScAttachment attachment) throws FileNotFoundException, IOException {

		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded");

		populateAttachmentDetails(attachment);

		// FileOutputStream fos;
		// fos = new FileOutputStream(getActualFilePath(attachment));
		// fos.write(fileBytes);
		// fos.close();

		/*
		 * FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
		 * "The file was uploaded successfully.", null);
		 * 
		 * FacesContext.getCurrentInstance().addMessage(null, message);
		 */
	}
	private List<ScAttachment> convertReportAsReportAttachment(
			List<ScQtEvaluationReport> evaluationReports) throws Exception {
		List<ScAttachment> attachments = new ArrayList<ScAttachment>();
		try {
			if (null != evaluationReports) {
				for(ScQtEvaluationReport evaluationreport:evaluationReports){
					if(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(evaluationreport.getDefinationType().getParameterCode())){
						if(null!=evaluationreport.getScQtEvaluationReportAttachment()){
							attachments.add(evaluationreport.getScQtEvaluationReportAttachment());	
						}
					}
					
				}
				attachments.add(generateDocumentAttachment());
			}
			

		} finally {

		}
		return attachments;
	}

	private void convertmultipleFilesAsaZipFile(
			List<ScAttachment> scAttachments,
			ScAttachment attachemnt) throws IOException {
		List<File> files = new ArrayList<File>();
		ZipOutputStream zos = null;
		try {
			for (ScAttachment scAttachment : scAttachments) {
				if (null != scAttachment
						&& StringUtils.isNotBlank(scAttachment.getFilePath())) {
					files.add(new File(getActualFilePath(scAttachment)));
				}
			}
			 zos = new ZipOutputStream(new FileOutputStream(
					getActualFilePath(attachemnt)));
			downloadAllFilesAsZipFile(zos,scAttachments);
		}
		 finally {
			 zos.close();
			 files=null;
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
				if(null!=scQtProposalChecklist){
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
				}

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
			evaluationSO = null;
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
			log.error(e.getMessage(), e);
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
			committeSO = null;
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
			evalReqDTO.setScQt(this.getManagedBean().getScQt()); // #28784
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

			if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL
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
			else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL
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
				// Bug #33249@Syazani 03/08/2022 -- Start
//				updateCheckListIntoQtCheckLists(qtCheckLists,
//						qtCheckLists.indexOf(getManagedBean()
//								.getScQtChecklist()), evalResDTO.getCheckList());
				updateCheckListIntoQtCheckLists(qtCheckLists, getManagedBean().getScQtChecklist(), evalResDTO.getCheckList());
				// Bug #33249@Syazani 03/08/2022 -- End
			}

			// #26544, #25638 -start-
			successMessage(MessageConst.INFO_700004,
					getMessageCommon("lbl.sc.prepare.evaluation.evaluationStage.scores"), " ", Boolean.FALSE);
			// #26544, #25638 -end-
		} finally {
			evaluationSO = null;
		}
		return evalResDTO;
	}

	/**
	 * Revise proposal evaluation.Event handle when member revise in evaluation.
	 * 
	 * @return navigate to acknowledge page.
	 */
	// #25638 - Start.
	public void reviseProposalEvaluation() {
					
		EvaluationResDTO resDTO = updateEvaluationCommitteDtls(this.getManagedBean().getLocalOutCome(), null, true);
		
		if (resDTO.isSuccessful()) {
			ScQtCommittee qtCommittee = resDTO.getQtCommittee();
			
			successMessage(MessageConst.INFO_776043,
					getMessageCommon("lbl.sc.quotation.tender"), qtCommittee.getScQt().getQtNo(), Boolean.TRUE);
		}
		// #25638 - End.
	}

	/**
	 * Submit prepare evaluation details.Event handle to submit data for bpm.
	 * 
	 * @param outCome
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	// #26544, #25638 -start-
	public void submitPrepareEvaluationDetails(String outCome) {
		// #26544, #25638 -end-

		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
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

			//25638 Revise at SC,EC,OC,TEC,FEC,FC - Start
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outCome)) {
				// #28784 - Start
				if (this.getManagedBean().getScQt().isDemoRequired()
						&& (SCProcessNameConst.SIGNOFF_TEC_EVALUATION.equalsIgnoreCase(this.getManagedBean()
								.getTaskName()))) {
					validateDemoRemarks();// add remarks
				} else {
					validateRemarks();
				}
				// #28784 - End
				RequestContext.getCurrentInstance().execute("reviseApprovalDlgs.show()");
			} else {
				getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
			//25638 - End
		} else {
			this.getManagedBean().setLocalOutCome(outCome);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outCome)) {
				// #28784 - Start
				if (this.getManagedBean().getScQt().isDemoRequired()
						&& (SCProcessNameConst.ENDORSE_TEC_EVALUATION.equalsIgnoreCase(this.getManagedBean()
								.getTaskName()))) {
					validateDemoRemarks();// add remarks
				} else {
					// 25638 - Start
					validateRemarks();
				}
				// #28784 - End
				
				RequestContext.getCurrentInstance().execute("reviseApprovalDlgs.show()");
				//25638 - End
			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outCome)
					|| BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outCome) || BpmOutcomeConst.BPM_ENDORSE.equalsIgnoreCase(outCome)) {

				EvaluationResDTO resDTO = updateEvaluationCommitteDtls(outCome,
						null);
				if (resDTO.isSuccessful()) {
					ScQtCommittee qtCommittee = resDTO.getQtCommittee();

					// #26544, #25638 -start-
					String messageCode = "";
					
					if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outCome)) {
						messageCode = MessageConst.INFO_700033;

					} else {
						messageCode = getMessgeConstant(outCome);
					}
					
					successMessage(messageCode, getMessageCommon("lbl.sc.prepare.secretary.evaluation"), qtCommittee
							.getScQt().getQtNo(), Boolean.TRUE);
					// #26544, #25638 -end-
				}
			}
		}
		
		committeSO=null;

	}
	
	//25638 Revise at SC,EC,OC,TEC,FEC,FC - Start
	private void validateRemarks() {
		String committeeType = (this.getManagedBean().getCommitteeType() == null) ? "" : this.getManagedBean()
				.getCommitteeType().toUpperCase();
		
		switch (committeeType) {
			case ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE:
				List<ScQtOpeningCommitteeRemark> OCRemarks = this.getManagedBean().getEvaluationOpeningCommitteeRemark();
				
				if (null != OCRemarks) {
					for (int i = 0; i < OCRemarks.size(); i++) {
						String userRemark = OCRemarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				
				break;
				
			case ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE:
				List<ScQtEvaluationCommitteeRemark> ECRemarks = this.getManagedBean().getEvaluationCommitteeRemark();
				
				if (null != ECRemarks) {
					for (int i = 0; i < ECRemarks.size(); i++) {
						String userRemark = ECRemarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				
				break;
				
			case ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE:
				List<ScQtTechnicalCommitteeRemark> TECRemarks = this.getManagedBean().getEvaluationTechnicalCommitteeRemark();
				
				if (null != TECRemarks) {
					for (int i = 0; i < TECRemarks.size(); i++) {
						String userRemark = TECRemarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				
				break;
				
			case ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE:
				List<ScQtFinancialCommitteeRemark> FECRemarks = this.getManagedBean().getEvaluationFinancialCommitteeRemark();
				
				if (null != FECRemarks) {
					for (int i = 0; i < FECRemarks.size(); i++) {						
						String userRemark = FECRemarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				
				break;
				
			default:
				break;
		}
	}
	//25638 - End
	
	/**
	 * Update evaluation committe dtls.
	 * 
	 * @param action
	 *            (Mandatory)
	 * @param digiSign
	 *            (optional)
	 * @return EvaluationResDTO (boolean successful,ScQtCommittee committee)
	 */
	private EvaluationResDTO updateEvaluationCommitteDtls(String action, PmDigiSign digiSign){
						
		return updateEvaluationCommitteDtls(action, digiSign, false);
	}

	/**
	 * Update evaluation committe dtls.
	 * 
	 * @param action
	 *            (Mandatory)
	 * @param digiSign
	 *            (optional)
	 * @param setSignedOffAllMembers
	 *           
	 * @return EvaluationResDTO (boolean successful,ScQtCommittee committee)
	 */
	// #25638 -start-
	private EvaluationResDTO updateEvaluationCommitteDtls(String action,
			PmDigiSign digiSign, boolean setSignedOffAllMembers) {
		// #25638 -end-
		IQTEvaluationSO evaluationso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTEvaluationSO.class);
		EvaluationReqDTO reqDtO = evaluationPreparation();
		List<ScQtCommitteeActivity> committeActivities = this
				.getManagedBean().getEndorscommitteeMemberList();

		// #28784 WIP @idham - Start
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

		if (this.getManagedBean().getScQt().getEvaluationType().equals(ParameterConst.EVALUATION_TYPES_TWO_TIER)
				&& this.getManagedBean().getScQt().isDemoRequired()) {
			if (reqDtO.getQtId() == null) {
				reqDtO.setQtId(this.getManagedBean().getScQt().getQtId());
			}

			boolean demoWip = evaluationSO.checkDemoWip(reqDtO);

			this.getManagedBean().setDemoWip(demoWip);
		} else {
			this.getManagedBean().setDemoWip(false);
		}
		// #28784 WIP @idham - End
		
		if (null != committeActivities) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();

			// #25638 -start-
			boolean isRevise = BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action);

			if (setSignedOffAllMembers && isRevise) {
				for (int i = 0; i < committeActivities.size(); i++) {
					committeActivities.get(i).setSignedOff(!isRevise);
				}
			} else {
				for (int i = 0; i < committeActivities.size(); i++) {
					if (pmUser.getIdentificationNo().equals(
							committeActivities.get(i).getScCommitteeMember()
									.getPmUser().getIdentificationNo())) {
						committeActivities.get(i).setSignedOff(true);
					}
					// #25638 -end-
				}
			}
			
			// #28784 - Start
			if (this.getManagedBean().getScQt().isDemoRequired()
					&& SCProcessNameConst.SIGNOFF_TEC_EVALUATION.equalsIgnoreCase(this.getManagedBean().getTaskName())
					&& !this.getManagedBean().isDemoWip()) {
				for (int i = 0; i < committeActivities.size(); i++) {
					committeActivities.get(i).setSignedOff(false);
				}
			}
			// #28784 - End
			
			reqDtO.getScQtCommitte().setCommitteeActivities(committeActivities);
		}
		
		// #28784 - Start
		SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) this.getManagedBean().getPayLoad();
		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action) && this.getManagedBean().getScQt().isDemoRequired()
				&& !this.getManagedBean().isDemoWip()) {
			evaluationDataType.setIsPresentationDemo(true);
		}
		// #28784 - End
		
		reqDtO.setCommitteeMemberEndorsmentList(committeActivities);
		ScQt scQt = this.getManagedBean().getScQt();
		
		// #28784 add demo remarks - Start
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF);
		if (this.getManagedBean().getScQt().isDemoRequired()
				&& demoStatuses.contains(this.getManagedBean().getCurrentStatusId()) && !this.getManagedBean().isDemoWip()) {
			EvaluationReqDTO demoReqDTO = saveDemoTECCommitteeRemarks();
			if (null != demoReqDTO) {
				reqDtO.setScQt(demoReqDTO.getScQt());
			}
		}
		// #28784 add demo remarks - End
		
		EvaluationReqDTO endorsmentRemarks=committeeRemarksSave();
		if(null!=endorsmentRemarks.getScQt()){
			if(BpmOutcomeConst.BPM_SIGNOFF.equalsIgnoreCase(action)){
																	  
				endorsmentRemarks.getScQt().setFecEvaluationCheck(null);
			}
			reqDtO.setScQt(endorsmentRemarks.getScQt());
		}else{
			if(BpmOutcomeConst.BPM_SIGNOFF.equalsIgnoreCase(action)){
																	  
				scQt.setFecEvaluationCheck(null);
				reqDtO.setScQt(scQt);
			}
		}
		reqDtO.setOutcome(action);
		if (null != digiSign) {
			updatePmDigiSignData(digiSign, this.getManagedBean()
					.getCommitteeType(), scQt.getQtId(), scQt.getQtNo());
		}
		reqDtO.setCommType(this.getManagedBean().getCommitteeType());
		reqDtO.setDigiSignData(digiSign);
		reqDtO.setTaskId(this.getManagedBean().getTaskId());
		reqDtO.setPayLoad(evaluationDataType); // #28784
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
	// #26544, #25638 -start-
	public void gpkiSignAction(PmDigiSign digiSign) {
		// #26544, #25638 -end-
		EvaluationResDTO resDTO = updateEvaluationCommitteDtls(
				this.getManagedBean().getLocalOutCome(), digiSign);
		if (resDTO.isSuccessful()) {
			ScQtCommittee qt = resDTO.getQtCommittee();
			
			// #28784 - Start
			if (qt.getScQt().isDemoRequired()
					&& this.getManagedBean().getTaskName().equals(SCProcessNameConst.SIGNOFF_TEC_EVALUATION)
					&& !this.getManagedBean().isDemoWip()) {//#28784 sign off WIP
				successMessage(MessageConst.INFO_700101,
						getMessageCommon("lbl.sc.eva.technical.presentation.demo.signoff"), qt.getScQt().getQtNo(),
						Boolean.TRUE);
			} else {
				// #26544, #25638 -start-
				successMessage(MessageConst.INFO_700033, getMessageCommon("lbl.sc.prepare.secretary.evaluation"), qt
						.getScQt().getQtNo(), Boolean.TRUE);
				// #26544, #25638 -end-
			}
			// #28784 - End
		}
	}

	
	/**
	 * preRenderView(). user click on task list navigate to evaluation screens
	 * for(EC/OC/TEC/FEC).
	 * 
	 * 
	 * 
	 */
	public void preRenderView() {

		SCEvaluationDataType evaluationDataType = getBpmTaskDetails(
				SCEvaluationDataType.class, null);
		// 25037 - start
		String[] outcomes = this.getManagedBean().getOutComes();
		this.getManagedBean().setOutComes(this.sortQtOutcomes(outcomes));
		// 25037 - end
		FacesScopes facesScopes = new FacesScopes();

		// FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		// 6906 - start
		IQTProposalSO proposalSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTProposalSO.class);
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtId(evaluationDataType.getQtId().longValue());
		Long proposalSubmitted = proposalSO.getTotalSubmittedProposal(reqDTO).getTotalSubmitted();
		this.getManagedBean().setNoProposalSubmitted(proposalSubmitted.compareTo(new Long(0)) == 0);
		// 6906 - end
		
		retriveMeetingDtls(evaluationDataType.getQtId().longValue(),
				evaluationDataType.getCommitteeType(), 1);
		String taskName = this.getManagedBean().getTaskName();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO.setCommitteeType(this.getManagedBean()
				.getCommitteeType());
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		//#28784 - Start
		List<String> demoProceses = new ArrayList<>();
		demoProceses.add(SCProcessNameConst.FINALISE_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.UPDATE_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.VIEW_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.UPDATE_DEMO_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.VIEW_DEMO_TEC_EVALUATION);
		//#28784 - End
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
		committeSO=null;
		
		if (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL.equals(this
				.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL.equals(this
						.getManagedBean().getCurrentStatusId())
				// #25638 - Start.
				|| isStatusLessThanOrEquals(
						this.getManagedBean().getCurrentStatusId(),
						QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION,
						QTStatusConst.OC_ORDERED_STATUSES)
				|| QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION
						.equals(this.getManagedBean().getCurrentStatusId())
		// #25638 - End.
				) {
			this.getManagedBean().setStatusOnly(false);

		} else {
			this.getManagedBean().setStatusOnly(true);
		}
		
		// 28834 starts
		if (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL.equals(this.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL.equals(this.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL.equals(this.getManagedBean().getCurrentStatusId())
				|| QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL.equals(this.getManagedBean().getCurrentStatusId())
				|| isStatusLessThanOrEquals(this.getManagedBean().getCurrentStatusId(),
						QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION, QTStatusConst.OC_ORDERED_STATUSES)
				&& ArrayUtils.isNotEmpty(this.getManagedBean().getOutComes())
				&& this.getManagedBean().getOutComes().length > 0) {
			boolean reviseExists = false;
			
			for (String outcome : this.getManagedBean().getOutComes()) {
				if (outcome.equals(BpmOutcomeConst.BPM_REVISEBP)) {
					reviseExists = true;
				}
			}

			if (reviseExists) {
				String tempOutComes[] = new String[this.getManagedBean().getOutComes().length - 1];
				
				for (int i = 0, k = 0; i < this.getManagedBean().getOutComes().length; i++) {					
					if (this.getManagedBean().getOutComes()[i].equals(BpmOutcomeConst.BPM_REVISEBP)) {
						continue;
					}
					
					tempOutComes[k++] = this.getManagedBean().getOutComes()[i];
				}
				
				this.getManagedBean().setOutComes(tempOutComes);
			}
		}
		// 28834 ends

		this.getManagedBean().setPrepareActiveIndex(0);
		if (SCProcessNameConst.FINALISE_EC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_EC_EVALUATION
						.equalsIgnoreCase(taskName) || SCProcessNameConst.VIEW_EC_EVALUATION
						.equalsIgnoreCase(taskName) ) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		} else if (SCProcessNameConst.FINALISE_OC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_OC_EVALUATION
						.equalsIgnoreCase(taskName) || SCProcessNameConst.VIEW_OC_EVALUATION
						.equalsIgnoreCase(taskName)) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}
		// #28784 - Start
		else if (demoProceses.contains(taskName))
		// #28784 - End
		{
			afterDemoInv(true); // #28784
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}

		else if (SCProcessNameConst.FINALISE_FEC_EVALUATION
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_FEC_EVALUATION
						.equalsIgnoreCase(taskName) || SCProcessNameConst.VIEW_FEC_EVALUATION
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
		// #28784 - Start
		else if (SCProcessNameConst.ENDORSE_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ENDORSE_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)) {
			afterDemoInv(true);
			// #28784 - End
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
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void downloadReport() throws Exception {
		debugLogger("downloadReport");
		  log.info("Start EvaluationdownloadReport"+DateUtils.getCurrentDate());
		ScAttachment evalReportAttachment = generateDocumentAttachment();
		downloadFile(evalReportAttachment);
		log.info("end EvaluationdownloadReport"+DateUtils.getCurrentDate());
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
	/*
	private ScQtEvaluationReportAttachment generateDocumentAttachment(ScQtEvaluationReport scQtEvaluationReport) throws Exception {
		debugLogger("generateDocumentAttachment");
		String definitionType = scQtEvaluationReport.getDefinationType().getParameterCode();
		ScQtEvaluationReportAttachment scQtEvaluationReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			String docDefinitionKey = scQtEvaluationReport.getPmDocument().getDocDefinitionKey();
			scQtEvaluationReportAttachment = new ScQtEvaluationReportAttachment();
			scQtEvaluationReportAttachment.setScQtEvaluationReport(scQtEvaluationReport);
			scQtEvaluationReportAttachment.setDocNo(scQtEvaluationReport.getScQt().getQtNo());

			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtEvaluationReport.getTitle().replaceAll("\\s", "_"));

			if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_SHORTLISTED.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generatePematuhan1Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_COMPLIANCE_ASSESSMENT_METHOD.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateKaedah1Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_SHORTLISTED.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generatePematuhan2Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_ASSESSMENT_METHOD.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateKaedah2Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_ASSESSMENT_METHOD_FIN.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateFinancialKaedah2Pdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_TECH_FIN_EVALUATION.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateAnalisisKeputusanPenilaianTeknikalPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_TECH_SPECIFICATION.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateTeknikalSpecificationPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_EVAL_REPORT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateEvaluationPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_HARGA_TAWARAN.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateHargaTawaranPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_OVERALL_FIN_RESULT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateOverallFinancialResultPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_OVERALL_TECH_RESULT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtEvaluationReportAttachment.setFileName(filename.toString());
				generateOverallTechnicalResultPdf((ScAttachment) scQtEvaluationReportAttachment, getManagedBean().getScQt());
			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_USER_SCORE_SCHEMA.equals(docDefinitionKey)) {
				if (null != getManagedBean().getScQt().getTechnicalUserScoringSchemaAttachment()) {
					filename = new StringBuilder();
					filename.append(getManagedBean().getScQt().getTechnicalUserScoringSchemaAttachment().getFileName().replaceAll("\\s", "_"));
					// filename.append(".pdf");
					scQtEvaluationReportAttachment.setFileName(filename.toString());
					scQtEvaluationReportAttachment.setFilePath(getManagedBean().getScQt().getTechnicalUserScoringSchemaAttachment().getFilePath());
				}
				// downloadFile(scQtEvaluationReportAttachment);
				// downloadLocalFile(scQtEvaluationReportAttachment);
			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_USER_SCORE_SCHEMA.equals(docDefinitionKey)) {
				if (null != getManagedBean().getScQt().getFinancialUserScoringSchemaAttachment()) {
					filename = new StringBuilder();
					filename.append(getManagedBean().getScQt().getFinancialUserScoringSchemaAttachment().getFileName().replaceAll("\\s", "_"));
					// filename.append(".pdf");
					scQtEvaluationReportAttachment.setFileName(filename.toString());
					scQtEvaluationReportAttachment.setFilePath(getManagedBean().getScQt().getFinancialUserScoringSchemaAttachment().getFilePath());
				}
				// downloadFile(scQtEvaluationReportAttachment);
				// downloadLocalFile(scQtEvaluationReportAttachment);
			}
			scQtEvaluationReport.setScQtEvaluationReportAttachment(scQtEvaluationReportAttachment);
		} else {
			String docDefinitionKey = scQtEvaluationReport.getPmDocument().getDocDefinitionKey();
			scQtEvaluationReportAttachment = new ScQtEvaluationReportAttachment();
			scQtEvaluationReportAttachment.setScQtEvaluationReport(scQtEvaluationReport);
			scQtEvaluationReportAttachment.setDocNo(scQtEvaluationReport.getScQt().getQtNo());
			if (SourcingConstants.DOC_DEF_KEY_QT_INFO.equals(docDefinitionKey)) {
				// generateSebutHargaPdfFromVelocity( (ScAttachment) scQtEvaluationReportAttachment, scQtEvaluationReport);
			}
			// scQtEvaluationReport .setScQtEvaluationReportAttachment(scQtEvaluationReportAttachment);
		}
		return scQtEvaluationReportAttachment;
	}
	*/
	
	private ScAttachment generateDocumentAttachment() throws Exception {
		debugLogger("generateDocumentAttachment");
		
		ScAttachment reportAttachment = null;
		ScAttachment commReportAttachment = null; // 37617
		ScQt scQt = getManagedBean().getScQt();
		
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			commReportAttachment = scQt.getScEvaluationCommitteeReportAttachment(); // 37617
			reportAttachment = new ScEvaluationCommitteeReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			commReportAttachment = scQt.getScOpenCommitteeReportAttachment(); // 37617
			reportAttachment = new ScOpenCommitteeReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			commReportAttachment = scQt.getScTechnicalEvaluationReportAttachment(); // 37617
			reportAttachment = new ScTechnicalEvaluationReportAttachment();
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
				.equalsIgnoreCase(this.getManagedBean().getCommitteeType())) {
			commReportAttachment = scQt.getScFinancialEvaluationReportAttachment(); // 37617
			reportAttachment = new ScFinancialEvaluationReportAttachment();
		}	

		// 37617 - end
		String taskName = this.getManagedBean().getTaskName();
		taskName = null != taskName ? taskName : "";
		boolean isFetchReportOnly = Arrays.asList(SCProcessNameConst.EVAL_COMM_ENDORSE_GROUP).contains(taskName)
				|| Arrays.asList(SCProcessNameConst.EVAL_COMM_SIGNOFF_GROUP).contains(taskName);
		if (null != commReportAttachment && isFetchReportOnly) {
			return commReportAttachment;
		}
		// 37617 - end

		StringBuilder fileName = new StringBuilder(
				getManagedBean().getReportDownloadFileName());
		fileName.append(".pdf");
		reportAttachment.setFileName(fileName.toString());
		reportAttachment.setDocNo(scQt.getQtNo());
		generateEvaluationPdf(
				(ScAttachment) reportAttachment,
				getManagedBean().getScQt());
		
		return reportAttachment;
	}
	
	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	/*
	private void generateFinancialKaedah2Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateFinancialKaedah2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_GEN_SPEC_FIN_4D;
			reportId = ReportConst.TECSPEC_EC_RPT_VAL; // SC_027
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

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
	/*
	private void generateSebutHargaPdfFromVelocity(ScAttachment attachment, ScQtEvaluationReport scQtEvaluationReport) throws Exception {
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

		populateAttachmentDetails(attachment);

		DocumentGenerator.getInstance().generatePdf(new FileOutputStream(getActualFilePath(attachment)), html);
	}
	*/

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	/*
	private void generateOverallTechnicalResultPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateFinancialEvaluationReportPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {

			reportName = ReportConst.SC_EC_OVERALLTECH_WITH_WEI_4H;
			reportId = ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL; // SC_035

		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_TEC_OVERALL_WITH_WEI_2F;
			reportId = ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL; // SC_012
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	/*
	private void generateOverallFinancialResultPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateOverallFinancialResultPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_OVERALL_FIN_4I;
			reportId = ReportConst.OVRL_FINRS_EC_RPT_VAL; // SC_036

		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_OVERALL_FIN_3E;
			reportId = ReportConst.OVRL_FINRS_FEC_RPT_VAL; // SC_020
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	/*
	private void generateHargaTawaranPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateHargaTawaranPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_PRICEOFFER_4M;
			reportId = ReportConst.HARGA_TAWARAN_EC_RPT_VAL; // SC_030
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_PRICEOFFER_3D;
			reportId = ReportConst.HARGA_TAWARAN_FEC_RPT_VAL; // SC_016
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	private void generateEvaluationPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateEvaluationPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportName = StringUtils.EMPTY;
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_OC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_TEC_RPT_JSP;
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_FEC_RPT_JSP;
		}

		String estimatedAmountStr = null;
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			BigDecimal estimatedAmount = qtDetailSO.getdecryptedEstAmount(qtId);
			estimatedAmountStr = estimatedAmount.toPlainString();
		} finally {
			qtDetailSO = null;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_ESTIMATED_AMOUNT, estimatedAmountStr);

		if (StringUtils.isNotBlank(reportName)) {
			generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
		} else {
			log.info("ReportName is empty");
		}
	}

	/**
	 * @param scQtEvaluationReportAttachment
	 * @param scQt
	 */
	/*
	private void generateTeknikalSpecificationPdf(ScAttachment attachment, ScQt qt) throws Exception {
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

		log.debug("before retrieveScoreSettingDtls");

		specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

		List<ScQtScoreSetting> list = resDTO.getScoreSettingResultBean().getResultList();

		log.debug("after retrieveScoreSettingDtls");

		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_EC_TECH_SPEC_WEI;
			reportId = ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL; // SC_029
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			for (ScQtScoreSetting qtScoreSetting : list) {
				if (qtScoreSetting.getSettingType() == 1) {
					reportName = ReportConst.SC_TEC_TECH_SPEC_WEI;
					reportId = ReportConst.PSDAK_TEC_RPT_VAL; // SC_009
				}
			}
		}

		log.debug("before paramMap");

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		log.debug("after paramMap");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());

		log.debug("end generateTeknikalSpecificationPdf");
	}
	*/

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
	/*
	private void generateAnalisisKeputusanPenilaianTeknikalPdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateAnalisisKeputusanPenilaianTeknikalPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPTPH_EC_RPT_JSP;
			reportId = ReportConst.AKPTPH_EC_RPT_VAL; // SC_026
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_TEC_RPT_JSP;
			reportId = ReportConst.AKPT_TEC_RPT_VAL; // SC_007
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_AKPT_FEC_RPT_JSP;
			reportId = ReportConst.AKPT_FEC_RPT_VAL; // SC_015
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

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
	/*
	private void generateKaedah2Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateKaedah2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_EC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL; // SC_025
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_TEC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL; // SC_006
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPSAP2_PEMARKAHAN_FEC_RPT_JSP;
			reportId = ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL; // SC_014
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

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
	/*
	private void generatePematuhan2Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generatePematuhan2Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_EC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_EC_RPT_VAL; // SC_024
		} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_TEC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_TEC_RPT_VAL; // SC_005
		} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPSAP2_FEC_RPT_JSP;
			reportId = ReportConst.SPPSAP2_FEC_RPT_VAL; // SC_013
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

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
	/*
	private void generateKaedah1Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateKaedah1Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPP1_PEMATUHAN_EC_RPT_JSP;
			reportId = ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL; // SC_022
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_KPPP1_PEMATUHAN_OC_RPT_JSP;
			reportId = ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL; // SC_002
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/

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
	/*
	private void generatePematuhan1Pdf(ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generatePematuhan1Pdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportId = "";
		String reportName = "";
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPP1_PEMATUHAN_EC_RPT_JSP;
			reportId = ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL; // SC_021
		} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(this.getManagedBean().getCommitteeType())) {
			reportName = ReportConst.SC_SPPPP_PEMATUHAN_OC_RPT_JSP;
			reportId = ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL; // SC_001
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");

		generatePdfFromOracle(reportName, paramMap, attachment, this.getManagedBean().isCheckReportDownload());
	}
	*/
	
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
		List<ScAttachment> attachments = new ArrayList<ScAttachment>();
		/*for (ScQtEvaluationReport scQtEvaluationReport : evaluationReports) {
			ScQtEvaluationReportAttachment reportAttachment = scQtEvaluationReport
					.getScQtEvaluationReportAttachment();
			if (reportAttachment == null) {
				//reportAttachment = generateDocumentAttachment(scQtEvaluationReport);
				reportAttachment =(ScQtEvaluationReportAttachment)generateDocumentAttachment();
				scQtEvaluationReport
						.setScQtEvaluationReportAttachment(reportAttachment);
			}
			attachments.add(reportAttachment);
		}*/
		for(ScQtEvaluationReport evaluationreport:evaluationReports){
			if(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(evaluationreport.getDefinationType().getParameterCode())){
				if(null!=evaluationreport.getScQtEvaluationReportAttachment()){
					attachments.add(evaluationreport.getScQtEvaluationReportAttachment());	
				}
			}
			
		}
		attachments.add(generateDocumentAttachment());
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
	 * @param delEntity
	 *               (BaseEntity delEntity).
	 */
	public void deleteDemoUploadFile(BaseEntity delEntity) {
		debugLogger("deleteUploadFile");
		IQTEvaluationSO evaluationSO=null;
		try{
		evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		getManagedBean().getScQtChecklist().setScQtCheckListAttachment(null);
		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		reqDTO.setCheckList(getManagedBean().getScQtChecklist());

		EvaluationResDTO resDTO = evaluationSO
				.saveOrUpdatePresentationDemoCheckList(reqDTO);

		this.getManagedBean().setScQtChecklist(resDTO.getCheckList());
		}finally{
			evaluationSO=null;
		}

	}

	/**
	 * To set the report to be edited
	 * 
	 * @param evaluationReport
	 *//*
	public void setSelectedReport(ScQtEvaluationReport evaluationReport) {
		this.getManagedBean().setSelectedReport(evaluationReport);
	}*/
	
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
		tplDocMaster.setPmStatus(getPmStatusByStatusId(QTStatusConst.DOC_TEMPLATE_DEFAULT));

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
			}
			if(null==docMaster){
				docMaster=new TplDocMaster();
			}
			return docMaster;
			/* else {
				//throw new EpException(
						//MessageConst.ERROR_760417);
			}*/
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
	/*
	private ScQtEvaluationReport saveScQtReportList(ScQtEvaluationReport scQtEvaluationReport) {
		debugLogger("ScQtEvaluationReport");

		if (scQtEvaluationReport.getQtReportId() == null) {
			saveQtReportDtls();
			ScQtEvaluationReport qtEvaluationReport = retriveRulesAndRegulationReport(scQtEvaluationReport);
			return qtEvaluationReport;
		} else {
			return null;
		}
	}
	*/

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


			//retrieveReportTabDtl();

		} finally {
			evaluationReportSO = null;
		}
	}

	
	/**
	 * Retrive rules and regulation report.retrieve the scQtEvaluationReport for Maklumat Tender/Sebut Harga/
	Asas-Asas Penilaian / Perakuan
	 * 
	 * @param scQtEvaluationReport
	 *            the sc qt evaluation report
	 * @return the sc qt evaluation report
	 */
	/*
	private ScQtEvaluationReport retriveRulesAndRegulationReport(ScQtEvaluationReport scQtEvaluationReport) {
		debugLogger("retriveRulesAndRegulationReport");
		IQTDocumentSO qtDocumentSO = null;
		try {
			qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);

			QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
			reqDTO.setScReport(scQtEvaluationReport);
			reqDTO.setDocCategory(scQtEvaluationReport.getDocCategory());
			reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_QT_INFO);

			QTDocumentResDTO resDTO = qtDocumentSO.retriveScQtReportByDefKey(reqDTO);
			ScQtEvaluationReport qtEvaluationReport = (ScQtEvaluationReport) resDTO.getScReport();
			return qtEvaluationReport;
		} finally {
			qtDocumentSO = null;
		}
	}
	*/

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
		return new ScRemarkAndAttachmentBackingBean().downloadEncryptedFile(
				scAttachment, getEncryptionKey());
	}
	
	private String getEncryptionKey() {
		String encryptionKey = getManagedBean().getEncryptionKey();
		
		if (StringUtils.isBlank(encryptionKey)) {
			IQTProposalSO proposalSO = null;
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				reqDTO.setScQt(this.getManagedBean().getScQt());

				encryptionKey = proposalSO.getGeneratedEncrpyptedKey(reqDTO);
				getManagedBean().setEncryptionKey(encryptionKey);

			} finally {
				proposalSO = null;
			}
		}
		
		return encryptionKey;
	}

	/**
	 * Event to update the status of the selected meeting
	 * 
	 * @param scQtMeeting
	 *            the selected ScQtMeeting entity
	 */
	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {
		scQtMeeting.setMeetingStatus(getPmStatusByStatusId(scQtMeeting.getMeetingStatusStr()));
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
				//Bug #21523 Marks display 0 in technical evaluation
				log.info(":::saveEvalutionStageUploadDtls() qt id: "+ uploadedList.get(0).getScQtCheckList().getScQt().getQtId() +". Supp list size: "+ uploadedList.size());
				//end Bug #21523
				
				PmStatus Status = getPmStatusByStatusId(QTStatusConst.PR_COMPLETED);
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
							// Bug #33249@Syazani 03/08/2022 -- Start
							if (checkList.getQtChecklistId().equals(technicalUploadNoActionCheckList.getQtChecklistId())) {
								technicalChecklsit.set(i, technicalUploadNoActionCheckList);
							}
//							if (checkList.getTitle()
//									.equalsIgnoreCase(
//											technicalUploadNoActionCheckList
//													.getTitle())) {
//								int index = technicalChecklsit.indexOf(this
//										.getManagedBean().getScQtChecklist());
//								// technicalChecklsit.remove(checkList);
//								technicalChecklsit.set(index,
//										technicalUploadNoActionCheckList);
//							}
							// Bug #33249@Syazani 03/08/2022 -- End

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
							// Bug #33249@Syazani 03/08/2022 -- Start
							if (checkList.getQtChecklistId().equals(financialUploadNoActionCheckList.getQtChecklistId())) {
								technicalChecklsit.set(i, financialUploadNoActionCheckList);
							}
//							if (checkList.getTitle()
//									.equalsIgnoreCase(
//											financialUploadNoActionCheckList
//													.getTitle())) {
//								// technicalChecklsit.remove(checkList);
//								int index = technicalChecklsit.indexOf(this
//										.getManagedBean().getScQtChecklist());
//								technicalChecklsit.set(index,
//										financialUploadNoActionCheckList);
//							}
							// Bug #33249@Syazani 03/08/2022 -- End

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
			
			// #26544, #25638 -start-
			successMessage(MessageConst.INFO_700004,
					getMessageCommon("lbl.sc.prepare.evaluation.evaluationStage.scores"), " ", Boolean.FALSE);
			// #26544, #25638 -end-
		} finally {
			evaluationSo=null;
		}

	}

	/**
	 * return skip value
	 * @return boolean
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * set spki value
	 * @param skip
	 *            (boolean skip).
	 *            
	 */
	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	/**
	 * return current tab value.
	 * @return int
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * set  current tab Value.
	 * @param currentTab
	 *            (int currentTab)
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * return output tab Value.
	 * @return int
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * set  output tab value.
	 * @param outputTab
	 *           (int outputTab)
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

	
	
	/**
	 * To remove records for existing .
	 * 
	 * @param list
	 * @param toBeRemovedList
	 */
	public <ENTITY_CLASS extends BaseEntity> void markDeleteSelectedMeetingAttendanceRowsAction(
			List<ScQtMeetingAttendance> list, List<ScQtMeetingAttendance> markDeletedList) {
		if (log.isDebugEnabled()) {
			log.debug("markDeleteSelectedRowsAction");
		}
		try {
			if (list != null) {
				if (markDeletedList == null) {
					markDeletedList = new ArrayList<ScQtMeetingAttendance>();
				}

				for (ScQtMeetingAttendance entity : list) {
					if (entity.isSelected()) {
						entity.setDeleted(true);
						markDeletedList.add(entity);
					}
				}
				if (markDeletedList.size() == 0) {
					throw new EpException(MessageConst.ERROR_760146);
				}

				deleteMeetingAttendanceRowsAction(list, markDeletedList);
			} else {
				throw new EpException(MessageConst.ERROR_760146);
			}
		} finally {

		}
	}

	private <ENTITY_CLASS> void deleteMeetingAttendanceRowsAction(
			List<ScQtMeetingAttendance> list, List<ScQtMeetingAttendance> toBeRemovedList) {
		if (log.isDebugEnabled()) {
			log.debug("deleteRowAction");
		}
		if (list != null && toBeRemovedList != null) {
			this.getManagedBean().setMeetingAttendanceRemoveList(toBeRemovedList);
			list.removeAll(toBeRemovedList);
		}
	}
	

	/**
	 * check two meeting dates equal or not.
	 * @param scqtMeeting
	 *                (ScQtMeeting scqtMeeting)
	 *  @param meetingList
	 *                 (List<ScQtMeeting> meetingList).
	 * 
	 */
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

	
	/**
	 * delete existing meeting details records from database. 
	 * 
	 */
	public void deleteMeetingDtls() {
		markDeleteSelectedEvaluationRowsAction(this.getManagedBean()
				.getScQtMeetings(), this.getManagedBean()
				.getMeetingRemoveList());

	}

	
	/**
	 * delete existing meeting attendance details records from database.
	 *  
	 */
	public void deleteMeetingAttendanceDtls() {
		markDeleteSelectedMeetingAttendanceRowsAction(getManagedBean()
				.getMeetingAttendanceList(), getManagedBean()
				.getMeetingAttendanceRemoveList());

	}

	
	/**
	 * retrieve specification based on sample item.
	 * @param event
	 *           (ScQtSampleItem event).
	 *  
	 */
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

	
	/**
	 * change sample submission status in sample tab.
	 * @param ScQtSampleItem
	 *                (ScQtSampleItem sampleItem,IPagingDataModel<ScQtSampleItem> sampleItemList)
	 *  
	 * 
	 */
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
			if (!this.getManagedBean().isReadOnly() && !this.getManagedBean().isNoProposalSubmitted()) {// 6906
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
			if (!this.getManagedBean().isReadOnly() && !this.getManagedBean().isNoProposalSubmitted()) {// 6906
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

	
	/**
	 * this method is used to save compliance Proposal suppliers when user navigate to
	 * anotherpage using paging data model.
	 * 
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
								&&ParameterConst.COMPLIANCE_STATUS_NONCOMPLIANCE==proposalCheckList.getCompliance()) {
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

	
	/**
	 * this method is used to save the Upload checklist suppliers when user navigate to another page using paging data model.
	 * 
	 */
	public void SaveUploadCheckListSuppliersDM() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			List<ScQtProposalChecklist> uploadedList = this.getManagedBean()
					.getComplianceTechnicalProposalCheckListDM().getList();

			if (null != uploadedList && !uploadedList.isEmpty()) {
				//Bug #34815 start
				if (!this.getManagedBean().isReadOnly()){
					PmStatus Status = getPmStatusByStatusId(QTStatusConst.PR_COMPLETED);
					for (ScQtProposalChecklist uploadedChecklist : uploadedList) {
						uploadedChecklist.setEvaluationStatus(Status);
					}
				}
				//Bug #34815 end
				reqDTO.setComplianceList(uploadedList);

			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
		} finally {
			evaluationSo = null;
		}

	}

	
	/**
	 *save Compliance Sample Paging data model.
	 * 
	 */
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

	
	/**
	 * retrieve presentationdemo qualified suppliers based on technical stage. 
	 * 
	 */
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
			evaluationSo = null;
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
		if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
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
			if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
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
			if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
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
			evaluationSo = null;
		}
	}

	private void evaluationTechnicalChangeScQtStatusUpdate() {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO evalResDTO = new EvaluationResDTO();
		try {
			if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
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
			if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
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
			evaluationSo = null;
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
				this.getManagedBean().setCountDays(0);
				if(this.getManagedBean().getCurrentTime()<12){
					this.getManagedBean().setCurrentTime(12);
				}
			}
			if (day == 0
					&& currentDate
							.equals(currendate.format(this.getManagedBean()
									.getScQt().getSampleClosingDate()))) {
				if(this.getManagedBean().getCurrentTime()<12){
					this.getManagedBean().setCurrentTime(12);
				}
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
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(specReqDTO);
		List<TplSpecQuestion> questions = resDTO.getSpecMaster().getTplSpecQuestions();
		for (TplSpecQuestion specQuestion : questions) {
			log.debug("===specQuestion name====" + specQuestion.getClItem());
			if (null != specQuestion.getScoreTypeCode() && specQuestion.getScoreTypeCode().getParameterCode().equalsIgnoreCase(ParameterConst.SCORE_TYPE_USER)) {
				this.getManagedBean().setUserScoreSchemaType(QTConstants.USER_SCORE);

				break;
			} else {
				if (null != specQuestion.getScoreTypeCode() && specQuestion.getScoreTypeCode().getParameterCode().equalsIgnoreCase(ParameterConst.SCORE_TYPE_AUTO)) {

				}
			}
		}

	}
	/**
	 * Delete ProposalChecklistAttachments file.Event handle to delete upload file in
	 * presentation demo tab.
	 */
	public void deleteProposalChecklistAttachments(BaseEntity delEntity) {
		debugLogger("deleteUploadFile");
		List<ScQtProposalChecklistAttachment> proposalChecklist = this.getManagedBean().getToBeRemovedProposalAttachmentsChecklists();
		if (null == proposalChecklist) {
			proposalChecklist = new ArrayList<ScQtProposalChecklistAttachment>();
		}
		ScQtProposalChecklist proposalCheckList = this.getManagedBean().getQtProposalChecklist();
		if (null != proposalCheckList) {
			if (!proposalCheckList.isBrandNew()) {
				proposalChecklist.add(proposalCheckList.getScQtProposalChecklistAttachment());
				proposalCheckList.setScQtProposalChecklistAttachment(null);
			} else {
				proposalCheckList.setScQtProposalChecklistAttachment(null);
			}
		}
		this.getManagedBean().setToBeRemovedProposalAttachmentsChecklists(proposalChecklist);
	}	
	
	/**
	 * this method is used to save the userInput type popup window content in reports tab.
	 * @param evaluationReport
	 *                 (ScQtEvaluationReport evaluationReport).
	 *                 
	 */
	public void saveEditTemplateDocContent(ScQtEvaluationReport evaluationReport){
		IQTEvaluationSO evaluationSO = null;
		try{
	List<ScQtEvaluationReport> evaluationReportList=this.getManagedBean().getScQtEvaluationReportList();
		if(null!=evaluationReport && !this.getManagedBean().isReadOnly()){
			int report=	evaluationReportList.indexOf(evaluationReport);
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDto=new EvaluationReqDTO();
			reqDto.setScQtEvaluationReport(evaluationReport);
			reqDto.setReportType(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_DOCUMENT);
			EvaluationResDTO resDto=evaluationSO.saveReportsDtls(reqDto);
			if(null!=resDto.getScQtEvaluationReport()){
				this.getManagedBean().getScQtEvaluationReportList().set(report, resDto.getScQtEvaluationReport());
				this.getManagedBean().setScQtEvaluationReport(resDto.getScQtEvaluationReport());
			}
		}
		}finally{
			evaluationSO = null;
		}
		
	}	
	
	private int  retriveFinancialChecklistStatusCheck(){
		int outputTab=0;
		IQTEvaluationSO evaluationSO = null;
		try{
		List<ScQtCheckList> financialcheckList = this.getManagedBean()
				.getCompliancefinancialCheckList();
		if(null==financialcheckList || financialcheckList.isEmpty()){
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocCategory(SourcingConstants.FINANCIAL_DOCTYPE);
			reqDTO.setCompliance(null);
			reqDTO.setComplaianceProposalChecklist(SourcingConstants.COMPLIANCE_PROPOSAL_CHECKLIST);
			EvaluationResDTO resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
			this.getManagedBean().setCompliancefinancialCheckList(resDTO.getCheckListResultBean().getResultList());
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
								return outputTab=7;
							} else {
								this.getManagedBean().setComplianceActiveIndex(
										2);
								return outputTab=7;
							}
							//throw new EpException(MessageConst.ERROR_760239);
						}
					}
				}
			}
		}
			// #11104,35148-start
			else {
				for (ScQtCheckList techCheckList : this.getManagedBean().getCompliancefinancialCheckList()) {
					if (null != techCheckList.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION
									.equals(techCheckList.getPmEvalStatus().getStatusId())) {
						int count = checkProposalSubmittedForCompliance(techCheckList);

						if (count > 0) {
							return outputTab = QTConstants.EVALUATION_MAIN_ACTIVE_INDEX;
						}
					}
				}
			}
			// #11104,35148-End
		}finally{
			evaluationSO=null;
		}
		return outputTab;
	}
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
	
	/**
	 * this method is used to get the financial summary dropdown values.
	 * @return List<SelectItem>
	 */
	public List<SelectItem> finanialSummaryDropDownValues(){
		log.debug("finanialSummaryDropDownValues");
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		selectItems.add(new SelectItem(null, ResourceBundleUtils
				.getMessage("lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
	//boolean medicineCategoryCode=isMultipleProposalCategory(this.getManagedBean().getScQt(), null);
	//medicineCategoryCode=false;
	//log.info("finanialSummaryDropDownValues medicineCategoryCode"+medicineCategoryCode);
	if(SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM.equalsIgnoreCase(this.getManagedBean().getScQt().getEvaluateBy())){
		selectItems.add(new SelectItem(QTConstants.FINANCIAL_SUMMARY_PACKAGE_MARGIN, ResourceBundleUtils
				.getMessage("lbl.sc.evaluation.view.by.package.preference.margin",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
	}
		selectItems.add(new SelectItem(QTConstants.FINANCIAL_SUMMARY_ITEM, ResourceBundleUtils
				.getMessage("lbl.sc.evaluation.view.by.item.preference.margin",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
		return selectItems;
	}
	
	/**
	 * this method is used to get the recommendation summary dropdown values.
	 * @return List<SelectItem>
	 */
	public List<SelectItem> recommendationSummaryDropDownValues(){
		log.debug("recommendationSummaryDropDownValues");
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		selectItems.add(new SelectItem(null, ResourceBundleUtils
				.getMessage("lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
		//boolean medicineCategoryCode=isMultipleProposalCategory(this.getManagedBean().getScQt(), null);
		//log.info("recommendationSummaryDropDownValues medicineCategoryCode"+medicineCategoryCode);
		//medicineCategoryCode=false;
		if(SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM.equalsIgnoreCase(this.getManagedBean().getScQt().getEvaluateBy())){
		selectItems.add(new SelectItem(QTConstants.RECOMMENDATION_SUMMARY_PACKAGE_MARGIN, ResourceBundleUtils
				.getMessage("lbl.sc.evaluation.view.by.package.preference.margin",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
		}
		selectItems.add(new SelectItem(QTConstants.RECOMMENDATION_SUMMARY_ITEM, ResourceBundleUtils
				.getMessage("lbl.sc.evaluation.view.by.item.preference.margin",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())));
		return selectItems;
	}
	
	private void checklistSorting(List<ScQtCheckList> checkList) {
		Collections.sort(checkList, new Comparator<ScQtCheckList>() {
			public int compare(ScQtCheckList checklist1, ScQtCheckList checklist2) {

				// return checklist1.getChecklistSeq()-checklist2.getChecklistSeq();
				return new CompareToBuilder().append(checklist1.getChecklistSeq(), checklist2.getChecklistSeq()).append(checklist1.getCreatedDate(), 
						checklist2.getCreatedDate()).toComparison();
			}
		});
	}	
	
	/**
	 * To add empty row to document data table.
	 */
	public void addDocumentList() {
		debugLogger("addDocumentList");
		ScQt scQt = this.getManagedBean().getScQt();
		List<ScQtEvaluationReport> scQtDocReports = this.getManagedBean()
				.getScQtEvaluationReportList();
		if (scQtDocReports == null) {
			scQtDocReports = new ArrayList<ScQtEvaluationReport>();
			//scQt.setScQtE(scQtDocReports);
		}
		ScQtEvaluationReport scQtDocReport = new ScQtEvaluationReport();
		scQtDocReport
				.setDocCategory(EvaluationReport.getName(getManagedBean()
						.getCommitteeType()));
		scQtDocReport.setScQt(scQt);
		// scQtDocReport.setScQtDocReportAttachment(new
		// ScQtDocReportAttachment());
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scQtDocReport.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		scQtDocReports.add(scQtDocReport);
	}	
	
	/**
	 *reports file upload.to upload files for manual added reports.
	 * 
	 * @param event
	 *            (Mandatory)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void reportsUploadAttachment(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtEvaluationReport selectedEvaluationReport = getManagedBean()
					.getSelectedReport();
			ScQtEvaluationReportAttachment reportAttachment = selectedEvaluationReport
					.getScQtEvaluationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtEvaluationReportAttachment();
				reportAttachment.setScQtEvaluationReport(selectedEvaluationReport);
				selectedEvaluationReport
						.setScQtEvaluationReportAttachment(reportAttachment);
			} else {
				reportAttachment.setScQtEvaluationReport(selectedEvaluationReport);
				selectedEvaluationReport
						.setScQtEvaluationReportAttachment(reportAttachment);
			}
			List<ScQtEvaluationReport> evaluationReports = getManagedBean()
					.getScQtEvaluationReportList();
			int indexOfReport = evaluationReports.indexOf(selectedEvaluationReport);
			reportAttachment.setDocNo(selectedEvaluationReport.getScQt()
					.getQtNo());
			scAttachment = reportAttachment;
			uploadAttachment(event.getFile(), scAttachment);
			String fileName = scAttachment.getFileName();
			if (indexOfReport >= 0 && fileName != null) {
				reportAttachment.setFileName(indexOfReport + UNDER_SCORE
						+ fileName);
			}
			selectedEvaluationReport
					.setScQtEvaluationReportAttachment(reportAttachment);
			
		}
	}	
	
	/**
	 * Delete reports upload file.Event handle to delete upload file in
	 * reports tab.
	 * @param delEntity
	 *               (BaseEntity delEntity).
	 */
	public void deleteReportsUploadFile(BaseEntity delEntity) {
		debugLogger("deleteUploadFile");
		
		try{
		log.debug("deleteReportsUploadFile");
		ScQtEvaluationReport selectedEvaluationReport = getManagedBean()
				.getSelectedReport();
		selectedEvaluationReport.setScQtEvaluationReportAttachment(null);
		 getManagedBean()
			.setSelectedReport(selectedEvaluationReport);
		}finally{
		
		}

	}
	
	
	/**
	 * delete existing report details records from database.
	 *  
	 */
	public void deleteReportsTabDtls() {
		List<ScQtEvaluationReport> evaluationReportRemoveList = getManagedBean()
				.getReportsToBeRemovedList();
		if (null == evaluationReportRemoveList) {
			evaluationReportRemoveList = new ArrayList<ScQtEvaluationReport>();
			getManagedBean()
			.setReportsToBeRemovedList(evaluationReportRemoveList);
		}
		markDeleteSelectedRowsAction(getManagedBean()
				.getScQtEvaluationReportList(), evaluationReportRemoveList);
		if(CollectionUtils.isNotEmpty(evaluationReportRemoveList)){
			getManagedBean()
			.setReportsToBeRemovedList(evaluationReportRemoveList);
		}

	}	
	
	private void saveEvaluationReportsDtls() {
		IQTEvaluationSO evaluationSO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtEvaluationReport> evaluationReportList = this.getManagedBean().getScQtEvaluationReportList();
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setEvaluationReportList(evaluationReportList);
			reqDTO.setReportType(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			if (CollectionUtils.isNotEmpty(getManagedBean().getReportsToBeRemovedList())) {
				List<ScQtEvaluationReport> localReports = new ArrayList<ScQtEvaluationReport>();
				for (ScQtEvaluationReport evaluationReport : getManagedBean().getReportsToBeRemovedList()) {
					if (null != evaluationReport.getQtReportId() && !evaluationReport.isBrandNew()) {
						evaluationReport.setDeleted(true);
						localReports.add(evaluationReport);
					}

				}
				reqDTO.setToBeRemovedReportList(localReports);
			}
			//EvaluationResDTO resDto = evaluationSO.saveReportsDtls(reqDTO);
			evaluationSO.saveReportsDtls(reqDTO);
			getManagedBean().setReportsToBeRemovedList(null);
		} finally {
			evaluationSO = null;
		}

	}
	
	//Enhancement #26339 -start-
	/**
	* Gets the local goods radio values.
	* 
	* @param itemType
	* the item type
	* @return the Status values
	*/
	@SuppressWarnings("unused")
	public SelectItem[] statusDropdownCodeValues(String itemType, ScQtProposalChecklistAttachment scQtProposalChecklistAttachment) {
		List<Integer> types = new ArrayList<Integer>();

		PmDocument pmdoc = this.getManagedBean().getScQtChecklist().getPmDocument();

		if (pmdoc != null && pmdoc.getDocGroupId() != null) {
			if (pmdoc.getDocGroupId().toString().equals(ParameterConst.ASSET_LIABILITIES_GROUP_ID)) {
				this.getManagedBean().setFP_Compliance(true);
			} else if (pmdoc.getDocGroupId().toString().equals(ParameterConst.CREDIT_FACILITIES_GROUP_ID)) {
				this.getManagedBean().setFP_Compliance(true);
			} else if (pmdoc.getDocGroupId().toString().equals(ParameterConst.BANK_STATEMENT_GROUP_ID)) {
				this.getManagedBean().setFP_Compliance(true);
			} else {
				this.getManagedBean().setFP_Compliance(false);
			}
		}

		if (null != scQtProposalChecklistAttachment && scQtProposalChecklistAttachment.getAttachmentId() != null) {
			types.add(ParameterConst.COMPLIANCE_STATUS_NONCOMPLIANCE);
			types.add(ParameterConst.COMPLIANCE_STATUS__COMPLIANCE);
		} else if (null == scQtProposalChecklistAttachment && this.getManagedBean().isFP_Compliance()) {
			// #28308 Fix - Start (#26339)
			// #28703 (#26339) - Start.
			for (ScQtProposalChecklist proposalChecklist : getManagedBean().getComplianceTechnicalProposalCheckListDM().getList()) {
				if (proposalChecklist.getScQtProposalChecklistAttachment() == null) {
					proposalChecklist.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
				}
			}
			// #28703 (#26339) - End.

			types.add(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
			// #28308 Fix - End (#26339)
		} else {
			types.add(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
			types.add(ParameterConst.COMPLIANCE_STATUS_NONCOMPLIANCE);
			types.add(ParameterConst.COMPLIANCE_STATUS__COMPLIANCE);
		}

		SelectItem[] items = null;

		try {
			items = new SelectItem[types.size() + 1];

			if (null == scQtProposalChecklistAttachment && !this.getManagedBean().isFP_Compliance()) { // lbl.ct.not.applicable
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.ct.not.applicable", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			} else {
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			}

			int i = 1;

			for (Integer location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					log.info("ParamerterDesc Code " + paramDesc.getPmParameter().getParameterCode());

					if (paramDesc.getPmParameter().getParameterCode().equals(String.valueOf(location.intValue()))) {

						items[i++] = new SelectItem(paramDesc.getPmParameter().getParameterCode(), paramDesc.getCodeName());
					}
				}
			}
		} finally {
		}

		return items;
	}
	
	/**
	 * @param scQtProposalChecklist
	 * @return
	 */
	public boolean isComplianceStatusDisabled(ScQtProposalChecklist scQtProposalChecklist) {
		
		if(!this.getManagedBean().isFP_Compliance()) {
			String title =this.getManagedBean().getScQtChecklist().getTitle();
			
			if(title.equals(ParameterConst.CREDIT_FACILITIES_TITLE) ||
					title.equals(ParameterConst.ASSET_FACILITIES_TITLE) || title.equals(ParameterConst.BANK_STATEMENT_GROUP_ID))
			{	
				if (null != scQtProposalChecklist.getScQtProposalChecklistAttachment()) {
					this.getManagedBean().setFP_Compliance(true);
				}
			} else {
				this.getManagedBean().setFP_Compliance(false);
			}
		// #28308 Fix - Start (#26339)
		} else {
			if (null == scQtProposalChecklist.getScQtProposalChecklistAttachment()) {
				return true;
			}
		// #28308 Fix - End (#26339)
		}
		
		if (!scQtProposalChecklist.getScQtProposal().isSubmitted() || this.getManagedBean().isStatusOnly() 
				|| this.getManagedBean().isComplianceReEvaluateRequired()) {
			return true;
		}
		
		return false;
	}

	/**
	 * @param qtSampleLable
	 * @return
	 */
	public boolean sampleChecklistCompleted(ScQtSampleLable qtSampleLable) {
		try {
			if (null != qtSampleLable) {
				boolean statusCompleted = true;

				// #11104-start
				if (null == qtSampleLable.getSampleLableStatus()
						|| QTStatusConst.TS_PENDING_SUBMISSION.equals(qtSampleLable.getSampleLableStatus()
								.getStatusId())) {
					statusCompleted = false;
				}
				// #11104-End

				return statusCompleted;
			}
		} catch (Exception e) {
			log.error(this.getClass().getName() + ".sampleChecklistCompleted(): Error while checking status for label ID: " + qtSampleLable.getSampleLableId());
			log.error(this.getClass().getName() + ".sampleChecklistCompleted(): Error message is: " + e.getMessage());
			
			return false;
		}
		
		return false;
	}
	//Enhancement #26339 -end-
	 
	// #25638 - Start
	public boolean displayReviseInstrn() {
		if (	// #25638 - #31322 - Start.
				null != this.getManagedBean().getOutComes()
				&& this.getManagedBean().getOutComes().length > 0
				&& ArrayUtils.contains(this.getManagedBean().getOutComes(), BpmOutcomeConst.BPM_REVISE)
				&&
				// #25638 - #31322 - End.
				null != getManagedBean().getCurrentStatusId()) {
			long currentStatus = getManagedBean().getCurrentStatusId().longValue();

			if ((currentStatus == QTStatusConst.EC_PENDING_ENDORSEMENT.longValue())
					|| (currentStatus == QTStatusConst.TEC_PENDING_ENDORSEMENT.longValue())
					|| (currentStatus == QTStatusConst.FEC_PENDING_ENDORSEMENT.longValue())
					|| (currentStatus == QTStatusConst.SE_PENDING_ENDORSMENT.longValue())
					|| (currentStatus == QTStatusConst.OC_PENDING_ENDORSEMENT.longValue())
					|| (currentStatus == QTStatusConst.EC_PENDING_SIGN_OFF.longValue())
					|| (currentStatus == QTStatusConst.TEC_PENDING_SIGN_OFF.longValue())
					|| (currentStatus == QTStatusConst.FEC_PENDING_SIGN_OFF.longValue())
					|| (currentStatus == QTStatusConst.SE_PENDING_SIGNOFF.longValue())
					|| (currentStatus == QTStatusConst.OC_PENDING_SIGN_OFF.longValue())) {
				return true;
			}
		}
		
		return false;
	}
	// #25638 - End
	
	// 11104-start
	/**
	 * Gets the sample submission dropdown values.display sample submission drop
	 * in compliance Type Dropdown values.
	 * 
	 * @param type
	 *            (String sampleType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getSampleSubmissionDropDownValues(String sampleType, ScQtSampleItem sampleItem) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<>();
		List<String> sampleList = new ArrayList<String>();
		sampleList.add(ParameterConst.SAMPLE_SUBMISSION_STATUS_COMPLIANCE);
		sampleList.add(ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_COMPLIANCE);
		sampleList.add(ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_SUBMIT);

		if (null != sampleItem && null != sampleItem.getScRequestItem()
				&& null != sampleItem.getScRequestItem().getTplSpecQuestion()
				&& null != sampleItem.getScRequestItem().getTplSpecQuestion().getParentSpecQuestion()
				&& !sampleItem.getScRequestItem().getTplSpecQuestion().getParentSpecQuestion().isSampleReq()) {
			sampleList.add(ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_APPLICABLE);
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), sampleList));

		SelectItem[] sampleData = getDropdownValuesBySearchBean(ParameterTypeConst.SAMPLE_SUBMISSION_STATUS, searchBean);
		searchBean = null;
		sampleList = null;

		return sampleData;
	}
	// #11104_End

	// 11104-start
	/**
	 * calculate sample item score in technical Item tab for supplier.
	 * 
	 */
	public void sampleScoreCalculate() {
		BigDecimal sampleScore = BigDecimal.valueOf(0);
		List<ScQtSampleItem> itemList = this.getManagedBean().getSampleItemList();

		for (ScQtSampleItem item : itemList) {
			if (null != item.getUserScore()) {
				sampleScore = sampleScore.add(item.getUserScore());
			}
		}

		this.getManagedBean().getNoOfContractsProposalScore().setUserScore(sampleScore);
	}
	// #11104_End

	// 11104 -start-
	/**
	 * check checklist type is Sample or others.
	 * 
	 * @param checkList
	 * @return boolean
	 */
	public boolean checklistSample(ScQtCheckList checkList) {
		String paramCode = checkList.getDefinationType().getParameterCode();
		
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
				.equalsIgnoreCase(paramCode)) {
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	// 11104 -end-

	// 11104 -start-
	/**
	 * fetch sample sample status description based on statusId.
	 * 
	 * @param proposalCheckList
	 * @return String
	 */
	public String sampleStatusDesc(ScQtProposalChecklist proposalCheckList) {
		Long statusId = null != proposalCheckList.getEvaluationStatus() ? proposalCheckList.getEvaluationStatus()
				.getStatusId() : QTStatusConst.PR_PENDING;

		if (QTStatusConst.PR_PENDING.equals(statusId)) {
			return ResourceBundleUtils.getMessage(QTConstants.SAMPLE_PENDING_SUBMISSION,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}

		return getStatusDescById(statusId);
	}
	// 11104 -end-
	
	// 25037 - start
	/**
	 * Populate the data for the screen update committee member.
	 */
	public void prepareUpdateCommitteeMember() {
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
		// 25037 - start - note 568
		EvaluationMeetingBackingBean meetingBB = this.getManagedBean().getMeetingBB();
		if (meetingBB == null) {
			meetingBB = new EvaluationMeetingBackingBean();
			meetingBB.renewSesionObject();
		}
		this.getManagedBean().setMeetingBB(meetingBB);
		// 25037 - end - note 568
		meetingBB.retriveMeetingDetails(scQt.getQtId(), qtCommittee.getScCommittee().getCommitteeType().getParameterCode());
		meetingBB.getManagedBean().setEvalBB(this);
		meetingBB.getManagedBean().setTaskName(this.getManagedBean().getTaskName());
		meetingBB.getManagedBean().setToken(this.getManagedBean().getToken());
		meetingBB.getManagedBean().setPayLoad(this.getManagedBean().getPayLoad());
		meetingBB.getManagedBean().setTaskId(this.getManagedBean().getTaskId());
	}
	// 25037 - end

	// 25037 - start
	/**
	 * Submit committee member changes and send for approval.
	 */
	public void submitCommitteeUpdate() {
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		FacesScopes facesScopes = new FacesScopes();
		EvaluationReqDTO reqDTO = evaluationPreparation();
		reqDTO.setCommType(this.getManagedBean().getCommitteeType());
		reqDTO.setCommitteeModified(true);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setTaskName(this.getManagedBean().getTaskName());
		reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		reqDTO.setPmUser(facesScopes.getUserSessionBean());
		reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
		reqDTO.setDeletedCommitteeMembers(this.getManagedBean().getRemoveCommitteeList());
		reqDTO.setScQtApprovalRemark(this.getManagedBean().getScQtApprovalRemark());
		evaluationSO.updateCommitteeBpmAction(reqDTO);

		ScQt qt = getManagedBean().getScQt();
		String committeeType = this.getManagedBean().getCommitteeType();
		committeeType = StringUtils.isEmpty(committeeType) ? "" : this.getParameterDesc(ParameterConst.COMMITTE_TYPE,
				committeeType);
		String procumentMode = this.getParameterDesc(qt.getProcurementMode());
		String message = MessageUtils.getMessage(MessageConst.INFO_760504, new String[] { committeeType, procumentMode,
				qt.getQtNo() }, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	// 25037 - end
	
	// 6906 - start
	/**
	 * Get warning message when no supplier participate on evaluation
	 * 
	 * @param
	 * @return String
	 */
	public String getNoSupplierWarningMessage() {
		return MessageUtils.getMessage(MessageConst.WARN_760530, MessageUtils.BUNDLE_COMMON_MESSAGES,
				new FacesScopes().getUserLocale());
	}
	// 6906 - end
	
	// Bug #36747@Syazani 20/01/2025 -- Start
	private int evaluationStageSummaryForceViewCheck(int currentTab, int outputTab) throws Exception {
		int outputTabEval = outputTab;
		boolean isEvaluationCommittee = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				&& (QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL.equals(this.getManagedBean().getCurrentStatusId()) 
						|| QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION.equals(this.getManagedBean().getCurrentStatusId()));
		
		boolean isTecCommittee = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL) 
						|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION));

		boolean isFecCommittee = ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL) 
						|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL_REVISION));
		
		boolean isOcCommittee = ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getCommitteeType())
				&& (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL) 
						|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION));

		boolean isCommittee2TierPass = isTecCommittee || isFecCommittee || isOcCommittee;

		boolean isDemoRequired = isTecCommittee && this.getManagedBean().getScQt().isDemoRequired();

		if (!isEvaluationCommittee) {
			outputTabEval = tier2EvaluationSummaryCheck(isCommittee2TierPass, isDemoRequired, currentTab, outputTabEval, isOcCommittee);
		} else {
			outputTabEval = tier1EvaluationSummaryCheck(currentTab, outputTabEval);
		}
		return outputTabEval;
	}

	private int tier2EvaluationSummaryCheck(boolean isCommittee2TierPass, boolean isDemoRequired, int currentTab,
			int outputTabEval, boolean isOcCommittee) throws Exception {
		int outputTabEvalTier2 = outputTabEval;
		boolean isWithoutDemo = isWithoutDemoTier2(isCommittee2TierPass, isDemoRequired, currentTab);
		boolean isDemo = isDemoTier2(isCommittee2TierPass, isDemoRequired, currentTab);
		// Tier 2
		// Opening, Financial & Technical Evaluation
		if (isOcTier2(isOcCommittee, currentTab)) {
			if (!this.getManagedBean().getScQt().isSampleReq()) {
				if (this.getManagedBean().getComplianceActiveIndex() < 2) {
					retriveComplianceSummary();
					outputTabEvalTier2 = 1;
				}
			} else {
				outputTabEvalTier2 = sampleReqComplianceStageCheck(outputTabEvalTier2);
			}
		} else if (isWithoutDemo) {
			if (this.getManagedBean().getEvaluatioStageActiveIndex() < 1) {
				retriveSummaryEvaluationStage();
				outputTabEvalTier2 = 2;
			}

		} else if (isDemo && this.getManagedBean().getPresentationDemoActiveIndex() < 1) {
			retrivePresentationDemoSummary();
			outputTabEvalTier2 = 3;
		}
		return outputTabEvalTier2;
	}
	
	private boolean isOcTier2(boolean isOcCommittee, int currentTab) {
		return isOcCommittee && currentTab == 1;
	}

	private boolean isWithoutDemoTier2(boolean isCommittee2TierPass, boolean isDemoRequired, int currentTab) {
		return isCommittee2TierPass && currentTab == 2 && !this.getManagedBean().isReadOnly() && !isDemoRequired;
	}

	private boolean isDemoTier2(boolean isCommittee2TierPass, boolean isDemoRequired, int currentTab) {
		return isCommittee2TierPass && currentTab == 3 && !this.getManagedBean().isReadOnly() && isDemoRequired;
	}

	private int tier1EvaluationSummaryCheck(int currentTab, int outputTabEval) {
		int outputTabEvalTier1 = outputTabEval;
		// Tier 1
		// Compliance Stage
		if (currentTab == 1) {
			if (!this.getManagedBean().getScQt().isSampleReq()) {
				if (this.getManagedBean().getComplianceActiveIndex() < 2) {
					retriveComplianceSummary();
					outputTabEvalTier1 = 1;
				}
			} else {
				outputTabEvalTier1 = sampleReqComplianceStageCheck(outputTabEvalTier1);
			}
			// Technical Evaluation
		} else if (currentTab == 2 && this.getManagedBean().getEvaluatioStageActiveIndex() < 1) {
			retriveSummaryEvaluationStage();
			outputTabEvalTier1 = 2;
			// Financial Evaluation
		} else if (currentTab == 3 && this.getManagedBean().getOneTierFinancialActiveIndex() < 1) {
			retriveOneTierFinancialSupplierSummary();
			outputTabEvalTier1 = 3;
		}
		return outputTabEvalTier1;
	}

	private int sampleReqComplianceStageCheck(int outputTabEvalTier1) {
		int outputTabEvalTier1SampleReq = outputTabEvalTier1;
		sampleClosingDateCountDays();

		if (ecPendingComplianceFailedCheck()) {
			throw new EpException(MessageConst.ERROR_760261);
		}
		if (ecPendingComplianceCheck()) {
			checkSampleComplianceCompleted();
		}

		if (this.getManagedBean().getComplianceActiveIndex() < 3) {
			retriveComplianceSummary();
			outputTabEvalTier1SampleReq = 1;
		}
		return outputTabEvalTier1SampleReq;
	}

	private boolean ecPendingComplianceFailedCheck() {
		boolean isEcPendingComplianceCheck = this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
				|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION);

		boolean isDaysCheck = this.getManagedBean().getCountDays() > 0
				|| (this.getManagedBean().getCountDays() == 0 && this.getManagedBean().getCurrentTime() < 12);

		return isEcPendingComplianceCheck && isDaysCheck;
	}

	private boolean ecPendingComplianceCheck() {
		boolean isDaysCheck = this.getManagedBean().getCountDays() < 0
				|| (this.getManagedBean().getCountDays() == 0 && this.getManagedBean().getCurrentTime() >= 12);

		boolean isEcPendingComplianceCheck = this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL)
				|| this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL_REVISION);

		return isDaysCheck && isEcPendingComplianceCheck;
	}
	// Bug #36747@Syazani 20/01/2025 -- End
	
	// #28784 - Start
	/**
	 * Back action for 'Back' button
	 * 
	 * @return
	 */
	public String presentationDemoBackaction() {
		this.getManagedBean().setTechindex(-1);
		return "/xhtml/sc/evaluation/evaluationMain.xhtml";
	}
	// #28784 - End
	
	// #28784 - Start
	/* Use in 4ser
	public void retriveSummaryEvalPresentationDemo() {

		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO evalResDTO = null;
		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTEvaluationSO.class);
			String view = QTConstants.TECHNICAL_SUMMARY_PACKAGE_MARGIN;
			if (null != view && !"".equals(view)) {
				ScQt scQt = getManagedBean().getScQt();

				getManagedBean().setSelectedZone(null);
				getManagedBean().setSinglesummaryItemList(null);
				this.getManagedBean().setFinancialSummaryView("");
				this.getManagedBean().setTechnicalSummaryView(view);
				this.getManagedBean().setSummaryViewBy("");
				this.getManagedBean().setSummaryItemList(null);

				EvaluationReqDTO summaryReqDTO = setSummaryPrepareData(view);
				evalResDTO = evaluationSO.retrieveSummaryDetails(summaryReqDTO);
				this.getManagedBean().setEvaluationStageZonesSummary(new ArrayList<TplSpecZoneDtl>());
				if (null != evalResDTO.getZones() && !evalResDTO.getZones().isEmpty()) {
					this.getManagedBean().setEvaluationStageZonesSummary(evalResDTO.getZones());
					summaryReqDTO.setSpeZoneId(this.getManagedBean().getEvaluationStageZonesSummary().get(0)
							.getSpecZoneId());
					getManagedBean().setSelectedZone(this.getManagedBean().getEvaluationStageZonesSummary().get(0));
				}
				this.getManagedBean().setQualifiedSuppliersDM(
						new SummarySupplierPagingDataModel("oneTierQualifiedSupplier", true, false, null, summaryReqDTO
								.getResultStages()));
				this.getManagedBean().setDisqualifiedSuppliersDM(
						new SummarySupplierPagingDataModel("oneTierDisQualifiedSupplier", false, false, null,
								summaryReqDTO.getResultStages()));
				//BUG #32453 & 32297 related to #21695 Start
				this.getManagedBean()
						.setNotOfferSupplierDM(
								new SummaryNotOfferSupplierPagingDataModel(
										"evaluationCommitteeSummaryNotOfferSupplierTechPackage",
										scQt.isZonal(), null,summaryReqDTO.getResultStages().toString()));
				//BUG #32453 & 32297 related to #21695 End
			}

		} finally {
			evaluationSO = null;
		}

	}*/
	// #28784 - End

	// #28784 - Start
	/**
	 * Retrieve all specification that has demo = yes
	 */
	public void retrivePresentationOrDemoEvaluationStage() {
		/** this.getManagedBean().setTabTechnicalIndex(4); */	// #34748
		evaluationStageStatusCheck();
		if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equalsIgnoreCase(this.getManagedBean()
				.getCommitteeType())
				|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE.equalsIgnoreCase(this.getManagedBean()
						.getCommitteeType())) {
			this.getManagedBean().setEvaluatioStageActiveIndex(2);//4ser, change to 3 after 4ser
			/** this.getManagedBean().setTabTechnicalIndex(4); */	// #34748
		}
		List<ScQtCheckList> localcheckList = new ArrayList<>();
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQt(this.getManagedBean().getScQt());
		reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
		SpecificationResDTO resDTO = specSo.retrieveAllDemoCheckLists(reqDTO);

		if (null != resDTO.getTechnicalCheckList()) {
			localcheckList = populateChecklistContainingDemo(localcheckList, resDTO);
		}
		this.getManagedBean().setEvaluationStgTechnicalCheckList(localcheckList);
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getEvaluationStgTechnicalCheckList())) {
			checklistSorting(this.getManagedBean().getEvaluationStgTechnicalCheckList());
		}
	}
	// #28784 - End
	
	//#28784 - Start
	private List<ScQtCheckList> populateChecklistContainingDemo(List<ScQtCheckList> localcheckList, SpecificationResDTO resDTO) {
		for (ScQtCheckList checklist : resDTO.getTechnicalCheckList()) {
			for (TplSpecQuestion q : checklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
				if (q.isItemReqDemo()) {
					localcheckList.add(checklist);
					break;
				}
			}

		}
		return localcheckList;
	}
	//#28784 - End
	
	//#28784 - Start
	private boolean checkDemoWIP() {
		try {
			IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			return evaluationSO.checkDemoWip(reqDTO);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SourcingConstants.SUPPLIER_SELECTION_DOC_TYPE, "checkDemoWIP",
					getManagedBean().getScQt().getQtNo(), "checkDemoWIP", getUserIdSessionFromLoginId(), e);
		}
		return true;
	}
	//#28784 - End
	
	//#28784 - Start
	private void changePerakuanToBottom(List<ScQtEvaluationReport> reportList) {
		List<ScQtEvaluationReport> newReportList = new ArrayList<>();
		ScQtEvaluationReport reportPerakuan = null;
		for (ScQtEvaluationReport report : reportList) {
			if (report.getTitle().equalsIgnoreCase(SourcingConstants.PERAKUAN)) {
				reportPerakuan = report;
			} else {
				newReportList.add(report);
			}
		}
		if (reportPerakuan != null) {
			newReportList.add(reportPerakuan);
		}
		this.getManagedBean().setScQtEvaluationReportList(newReportList);
	}
	//#28784 - End

	// #28784 - start
	/**
	 * demo file upload (checklist) validation
	 */
	public void demoAttachmentChecking() {
		if (!this.getManagedBean().isDemoWip()) {
			ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();
			if (null != this.getManagedBean().getScQtChecklist() && null == scQtChecklist.getScQtCheckListAttachment()) {
				throw new EpException(MessageConst.ERROR_760329);
			}
		}
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieve total technical score for package during Presentation/Demonstration Stage Summary
	 * 
	 * @param propId
	 * @return
	 */
	public String retriveTotalTechnicalScore(ScQtEvaluationResultDtl propId) {
		BigDecimal divNum = new BigDecimal(0);

		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		
		reqDTO.setQtproposalId(propId.getQtEvaluationResult().getScQtProposal().getQtProposalId());
		reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
		reqDTO.setDemo(false);//differentiate between item and package prop checklist
		//calculate total tech score
		EvaluationResDTO resDTO = evaluationSO.technicalChecklistList(reqDTO);
		for(ScQtProposalChecklist specscore : resDTO.getDemoCheckLists()){
			BigDecimal totalScore = specscore.getTotalScore();
			if (null == totalScore) {
				totalScore = BigDecimal.valueOf(0);
			}
			// add weightage to calculation - start
			if (null != specscore.getScQtCheckList().getQtScoreWeightage() &&
					(null != specscore.getScQtCheckList().getQtScoreWeightage().getWeightage() &&
						!specscore.getScQtCheckList().getQtScoreWeightage().getWeightage().equals(BigDecimal.valueOf(0)))) {
				BigDecimal maxScore = specscore.getScQtCheckList().getQtScoreWeightage().getScore();
				BigDecimal weightage = specscore.getScQtCheckList().getQtScoreWeightage().getWeightage();
				
				totalScore = weightage.multiply(totalScore).divide(maxScore, 2, RoundingMode.HALF_UP).stripTrailingZeros();//#28784 no package bug//#28784 sonarqube
			}
			// add weightage to calculation - end
			divNum = divNum.add(totalScore);
		}		
		return  String.valueOf(divNum.toPlainString());
	}
	// #28784 -end-
	
	// #28784 -start-	
	/**
	 * Retrieve Total Demo score for Package during Presentation/Demonstration Stage Summary
	 * 
	 * @param propId
	 * @return
	 */
	public String retriveTotalDemolScore(ScQtEvaluationResultDtl propId) {
		BigDecimal divNum = new BigDecimal(0);

		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		
		reqDTO.setQtproposalId(propId.getQtEvaluationResult().getScQtProposal().getQtProposalId());
		reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
		reqDTO.setDemo(true);
		EvaluationResDTO resDTO = evaluationSO.technicalChecklistList(reqDTO);
		//calculate demo total score
		for(ScQtProposalChecklist specscore : resDTO.getDemoCheckLists()){
			BigDecimal totalScore = specscore.getTotalScore();
			if (null == totalScore) {
				totalScore = BigDecimal.valueOf(0);
			}
			// add weightage to calculation - start
			if (null != specscore.getScQtCheckList().getQtScoreWeightage() &&
					(null != specscore.getScQtCheckList().getQtScoreWeightage().getWeightage() &&
						!specscore.getScQtCheckList().getQtScoreWeightage().getWeightage().equals(BigDecimal.valueOf(0)))) {
				BigDecimal maxScore = specscore.getScQtCheckList().getQtScoreWeightage().getScore();
				BigDecimal weightage = specscore.getScQtCheckList().getQtScoreWeightage().getWeightage();
				
				totalScore = weightage.multiply(totalScore).divide(maxScore, 2, RoundingMode.HALF_UP).stripTrailingZeros();//#28784 no package bug//#28784 sonarqube
			}
			divNum = divNum.add(totalScore);
			// add weightage to calculation - end
		}
		return String.valueOf(divNum.toPlainString());
	}
	// #28784 -end-
	
	// #28784 -start-	
	/** Use in 4ser
	public String retrieveTotalTechnicalItemScore(ScQtEvaluationResultDtl propId) {
		String totalTechScore = null;
		BigDecimal totalOtherchecklistScore = new BigDecimal(0);
		BigDecimal totalNum = new BigDecimal(0);
		IQTEvaluationSO evaluationSO = null;
		EvaluationResDTO resDTO = new EvaluationResDTO();

		try {
			
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationReqDTO reqDTO1 = new EvaluationReqDTO();
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			
			
			retrieveDemoSupplierNo(String.valueOf(propId.getQtEvaluationResult().getScQtProposal().getSupplierNo()));
			
			reqDTO1.setQtproposalId(propId.getQtEvaluationResult().getScQtProposal().getQtProposalId());
			reqDTO1.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO1.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);			
			reqDTO1.setDocDefinationKey(null);
			resDTO = evaluationSO.retrieveSpecScore(reqDTO1);
			for(ScQtProposalChecklist specscore : resDTO.getSpecScoreBySupplier()){
				if (specscore.getScQtCheckList().getDefinationType().getParameterCode().equals(SourcingConstants.DOC_DEF_KEY_PRESENTATION_DEMO)
						|| specscore.getScQtCheckList().getDefinationType().getParameterCode().equals(SourcingConstants.DOC_DEF_KEY_TEMPLATE_SPECIFICATION)) {
					//skip demo and technical spec
				} else {
					//#28784 ignore non scoring data - start
					if (specscore.getTotalScore() != null) {
						totalNum = totalNum.add(specscore.getTotalScore());
					}
					totalOtherchecklistScore = specscore.getScQtCheckList().getQtScoreWeightage() != null ?
							totalOtherchecklistScore.add(specscore.getScQtCheckList().getQtScoreWeightage().getScore()) : totalOtherchecklistScore;
					//#28784 ignore non scoring data - end
				}
			}
			
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setRequestItemId(this.getManagedBean().getSinglesummaryItemList().getRequestNoteItemId());
			reqDTO.setQtproposalId(propId.getQtEvaluationResult().getScQtProposal().getQtProposalId());
			resDTO = evaluationSO.excludePresentationDemo(reqDTO);
			this.getManagedBean().setOtherchecklistScore(String.valueOf(totalNum.toPlainString()));
			totalTechScore = String.valueOf(resDTO.getTechScore());
			this.getManagedBean().setItemTsPercentage(resDTO.getTechScore().multiply(this.getManagedBean().getMaxItemTsPercentage())
					.divide(this.getManagedBean().getMaxItemTsScore(), 2, RoundingMode.HALF_UP).stripTrailingZeros().add(BigDecimal.ZERO));//#28784 sonarqube
			if (resDTO.isGenerateSuppliers()) {
				presentationDemoItemScore(String.valueOf(propId.getQtEvaluationResult().getScQtProposal().getSupplierNo()),
						this.getManagedBean().getSinglesummaryItemList().getRequestNoteItemId());
			} else {
				this.getManagedBean().setDemoScore(ResourceBundleUtils.getMessage(
						"lbl.sc.qt.item.spec.null",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes()
						.getUserLocale()));
				this.getManagedBean().setItemAllOtherElementsPercentage(totalNum.multiply(this.getManagedBean().getMaxItemAllOtherElementsPercentage())
						.divide(this.getManagedBean().getMaxItemAllOtherElementsScore(), 2, RoundingMode.HALF_UP).stripTrailingZeros().add(BigDecimal.ZERO));//#28784 sonarqube
			}
		} finally {
			evaluationSO = null;
		}
		
		return totalTechScore;
	}
	 */
	// #28784 -end-
	
	// #28784 -start-
	/* Use in 4ser
	private void presentationDemoItemScore(String supplierNo, Long requestItem) {
		DemoResDTO resDto = new DemoResDTO();
		
		try {
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			DemoReqDTO demoReqDTO = new DemoReqDTO();
			demoReqDTO.setDemoSupplierNo(this.getManagedBean().getDemoSupplierNo());
			demoReqDTO.setSpecQuestionId(requestItem);
			
			resDto = qtDemoSO.retrieveByItemScoreBySuppppierIdAndSpecQuestionId(demoReqDTO);
			
			for (ScQtDemoByItemScore score : resDto.getByItemList()) {
				this.getManagedBean().setDemoScore(score.getDemoEvalScore());
				}
			this.getManagedBean().setItemAllOtherElementsPercentage((new BigDecimal(this.getManagedBean().getOtherchecklistScore())
			.add(new BigDecimal(this.getManagedBean().getDemoScore())))
			.multiply(this.getManagedBean().getMaxItemAllOtherElementsPercentage())
			.divide(this.getManagedBean().getMaxItemAllOtherElementsScore(), 2, RoundingMode.HALF_UP).stripTrailingZeros().add(BigDecimal.ZERO));//#28784 sonarqube
			}
		finally {

		}
	}*/
	//#28784 summary bug - end
	
	// #28784 -start-
	/**
	 * Suppliers file upload by TEC Secretary during Presentation/Demonstration Stage
	 * 
	 * @param event File Upload
	 * @throws IOException
	 */
	public void demoSupplierListFileUpload(FileUploadEvent event) throws IOException {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		ScQtDemoSupplier scQtDemoSupplier = this.getManagedBean().getScQtDemoSupplier();
		if (null != scQtDemoSupplier) {
			ScQtDemoSupplierAttachment attachment = scQtDemoSupplier.getScQtDemoSupplierAttachment();
			if (null == attachment) {
				attachment = new ScQtDemoSupplierAttachment();
				attachment.setScQtDemoSchedule(scQtDemoSupplier);
			}
			attachment.setDocNo(getManagedBean().getScQt().getQtNo());

			uploadAttachment(event.getFile(), attachment);

			scQtDemoSupplier.setScQtDemoSupplierAttachment(attachment);
		}
		DemoReqDTO demoReqDTO = new DemoReqDTO();
		demoReqDTO.setScQtDemoSupplier(scQtDemoSupplier);
		DemoResDTO demoResDTO = qtDemoSO.saveOrUpdateDemoSupplier(demoReqDTO);

		this.getManagedBean().setScQtDemoSupplier(demoResDTO.getScQtDemoSupplier());
	}
	// #28784 -end-
	
	// #28784 -start-	
	/**
	 * Deleting supplier file during Presentation/Demonstration Stage
	 * 
	 * @param delEntity
	 */
	public void deleteProposalDemoUploadFile(BaseEntity delEntity) {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDemoSO.class);
		getManagedBean().getScQtDemoSupplier().setScQtDemoSupplierAttachment(null);
		DemoReqDTO demoReqDTO = new DemoReqDTO();
		demoReqDTO.setScQtDemoSupplier(getManagedBean().getScQtDemoSupplier());

		DemoResDTO demoResDTO = qtDemoSO.saveOrUpdateDemoSupplier(demoReqDTO);

		this.getManagedBean().setScQtDemoSupplier(demoResDTO.getScQtDemoSupplier());

	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieve all technical checklist to have total max score for other checklists
	 */
	public void retrieveAllTechnicalChecklist() {
		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		reqDTO.setDocCategory(SourcingConstants.TECHNICAL_DOCTYPE);
		reqDTO.setCompliance(null);
		EvaluationResDTO resDTO = evaluationSO.retrieveCheckListsByCategory(reqDTO);
		
		this.getManagedBean().setOtherScore(resDTO.getOtherScore());
		this.getManagedBean().setEvaluationStgTechnicalCheckList(resDTO.getCheckListResultBean().getResultList());
	}
	// #28784 -end-
	
	// #28784 -start-
	private void afterDemoInv(boolean demoStage) {
		String taskName = this.getManagedBean().getTaskName();
		List<String> demoProceses = new ArrayList<>();
		demoProceses.add(SCProcessNameConst.FINALISE_DEMO_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.UPDATE_DEMO_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.VIEW_DEMO_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.ENDORSE_DEMO_TEC_EVALUATION);
		demoProceses.add(SCProcessNameConst.SIGNOFF_DEMO_TEC_EVALUATION);
		
		if (demoProceses.contains(taskName)) {
			if (!demoStage) {
				this.getManagedBean().setAfterDemoInv(true);
				this.getManagedBean().setReadOnly(true);
			} else {
				this.getManagedBean().setTabActiveIndex(3);
				getManagedBean().setTempTab(3);
				retrivePresentationDemo();
				this.getManagedBean().setPresentationDemoActiveIndex(0);
				retrieveReportTabDtl();
				this.getManagedBean().setAfterDemoInv(true);
				if (SCProcessNameConst.VIEW_DEMO_TEC_EVALUATION
						.equalsIgnoreCase(taskName) || SCProcessNameConst.ENDORSE_DEMO_TEC_EVALUATION
						.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.SIGNOFF_DEMO_TEC_EVALUATION
						.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.UPDATE_DEMO_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
					this.getManagedBean().setReadOnly(true);
				} else if (SCProcessNameConst.FINALISE_DEMO_TEC_EVALUATION
						.equalsIgnoreCase(taskName)) {
					this.getManagedBean().setReadOnly(false);
				}
			}
		} else {
			this.getManagedBean().setAfterDemoInv(false);
		}
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieve all technical checklist that has Presentation/Demonstration item in it
	 * 
	 * @param index
	 * @param checkList
	 * @param specType
	 * @return
	 */
	public String demoSpectechnicalChecklist(int index, ScQtCheckList checkList, String specType) {

		try {

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				specReqDTO.setDemoSpecView(true);
				TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
				TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
				if (null != templateresDTO) {
					checkList.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
				}

				checkList.getScQtDoc().getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
				templateSpecBean.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				this.getManagedBean().setScQtChecklist(checkList);

			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "demoSpectechnicalChecklist", checkList.getScQt().getQtNo(),
					"demoSpectechnicalChecklist", getUserIdSessionFromLoginId(), e);
		}
		
		return "/xhtml/sc/evaluation/evaluationStage/demoInvitationItemList.xhtml";

	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieving Demo Supplier Number for each supplier
	 * 
	 * @param supplierNo
	 * @return
	 */
	public String retrieveDemoSupplierNo(String supplierNo) {
		String demoSupplierId = null;
		
		DemoResDTO resDto = new DemoResDTO();
		try {
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			DemoReqDTO demoReqDTO = new DemoReqDTO();
			demoReqDTO.setScQt(this.getManagedBean().getScQt());
			demoReqDTO.setSupplierNo(supplierNo);
			resDto = qtDemoSO.retriveDemoSupplierByDemoIdAndSupplierNo(demoReqDTO);
			qtDemoSO.retrieveReEvalByItemListBySupplierId(demoReqDTO);//#28784 reeval bug - final
			demoSupplierId = resDto.getDemoNo();
			this.getManagedBean().setDemoSupplierNo(resDto.getDemoSupplierNo());
			this.getManagedBean().setScQtDemo(resDto.getScQtDemo());
			this.getManagedBean().setScQtDemoSupplier(resDto.getScQtDemoSupplier());
			}
		finally {

		}
		
		return demoSupplierId;
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieve each of demo supplier during Presentation/Demonstration Stage
	 * 
	 * @param supplierNo
	 * @return
	 */
	public ScQtDemoSupplier retrieveDemoSupplier(String supplierNo) {
		ScQtDemoSupplier demoSupplier = new ScQtDemoSupplier();
		
		DemoResDTO resDto = new DemoResDTO();
		try {
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			DemoReqDTO demoReqDTO = new DemoReqDTO();
			demoReqDTO.setScQt(this.getManagedBean().getScQt());
			demoReqDTO.setSupplierNo(supplierNo);
			resDto = qtDemoSO.retriveDemoSupplierByDemoIdAndSupplierNo(demoReqDTO);
			demoSupplier = resDto.getScQtDemoSupplier();
			}
		finally {

		}
		
		return demoSupplier;
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Retrieving attachment for each supplier in Presentation/Demonstration Stage
	 * 
	 * @param supplierNo
	 * @return
	 */
	public ScQtDemoSupplierAttachment retrieveDemoSupplierAttachment(String supplierNo) {
		ScQtDemoSupplier demoSupplier = new ScQtDemoSupplier();
		if (StringUtils.isEmpty(supplierNo)) {
			return null;
		}
		DemoResDTO resDto = new DemoResDTO();
		try {
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			DemoReqDTO demoReqDTO = new DemoReqDTO();
			demoReqDTO.setScQt(this.getManagedBean().getScQt());
			demoReqDTO.setSupplierNo(supplierNo);
			resDto = qtDemoSO.retriveDemoSupplierByDemoIdAndSupplierNo(demoReqDTO);
			demoSupplier = resDto.getScQtDemoSupplier();
			}
		finally {

		}
		
		return demoSupplier.getScQtDemoSupplierAttachment();
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Evaluation for each Demo Supplier
	 * 
	 * @param supplierNo
	 * @param demoPropChecklist
	 * @return
	 */
	public String presentationDemoScoring(String supplierNo, ScQtProposalChecklist demoPropChecklist) {

		retrieveDemoSupplierNo(supplierNo);

		DemoResDTO resDto = new DemoResDTO();
		try {
			IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
			DemoReqDTO demoReqDTO = new DemoReqDTO();
			demoReqDTO.setDemoSupplierNo(this.getManagedBean().getDemoSupplierNo());
			resDto = qtDemoSO.retrieveByItemListBySupplierId(demoReqDTO);
			this.getManagedBean().setByItemDemoList(resDto.getByItemList());
			this.getManagedBean().setQtProposalChecklist(demoPropChecklist);
		} finally {

		}

		return "/xhtml/sc/evaluation/presentationDemo/presentationDemoSupplierItemList.xhtml";
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * use in xhtml, save 2 items, ScQtDemoByItemScore and ScQtProposalChecklist
	 * 
	 */
	public void saveDemoScoring() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDemoSO.class);
		DemoReqDTO demoReqDTO = new DemoReqDTO();
		demoReqDTO.setDemoQtProposalChecklist(this.getManagedBean().getQtProposalChecklist());
		demoReqDTO.setScQtDemoByItemScoreList(this.getManagedBean().getByItemDemoList());
		demoReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		DemoResDTO resDto = qtDemoSO.saveDemoByItemScore(demoReqDTO);
		this.getManagedBean().setByItemDemoList(resDto.getByItemList());
		this.getManagedBean().setQtProposalChecklist(resDto.getDemoQtProposalChecklist());
		this.getManagedBean().getPresentationDemoSupplierListDM().reloadDataWithoutReset();//#28784 use this instead of retrieve all data
		successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.eva.technical.presentation.demo.item.list"), " ",
				Boolean.FALSE);
	}
	// #28784 -end-
	
	// #28784 -start-
	/**
	 * Save demo scoring during Presentation/Demonstration Stage evaluation
	 * 
	 */
	public void saveDemoAllItemScoring() {
		IQTDemoSO qtDemoSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDemoSO.class);
		DemoReqDTO demoReqDTO = new DemoReqDTO();
		demoReqDTO.setDemoQtProposalChecklist(this.getManagedBean().getQtProposalChecklist());
		demoReqDTO.setDemoChecklist(this.getManagedBean().getScQtChecklist());
		demoReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		DemoResDTO resDto = qtDemoSO.saveDemoAllItemScore(demoReqDTO);
		this.getManagedBean().setQtProposalChecklist(resDto.getDemoQtProposalChecklist());

		retrivePresentationDemo();
		successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.eva.technical.presentation.demo.item.list"),
				" ", Boolean.FALSE);
	}
	// #28784 - end
	
	// #28784 add remarks - start
	private void retrieveDemoRemarks() {
		if (!this.getManagedBean().isDemoWip()) {
			IQTDetailSO qtDetailSO;
			QuationTenderResDTO resDto;
			try {
				qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				qtReqDTO.setDocType(QTConstants.TEC_PRESENTATION_DEMO_REMARK);
				
				resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);
				ScQt scQt = this.getManagedBean().getScQt();
				ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
				ScWorkFlowStatus currentWorkFlowStatus = qtCommittee.getCurrentStatus();
				
				if (resDto.getRemarks() == null) {

					checkDemoRemark(scQt, currentWorkFlowStatus);
					scQt.setScQtDemoTechnicalCommitteeRemarks(
								this.getManagedBean()
										.getDemoTechnicalCommitteeRemark());
					setDemoRemarkNull(scQt);
				} else {
					initializeDemoRemarkAndChecking(resDto, scQt, currentWorkFlowStatus);
				}
			}
			catch (Exception e) {
				SLAUtils.getInstance().logSLAError("QT", "RetrieveListOfPresentationDemoRemarks",
						this.getManagedBean().getScQt().getQtNo(), "retrieveDemoRemarks", getUserIdSessionFromLoginId(), e);
			}
		}
	}
	// #28784 -end-
	
	// #28784 -start-
	private EvaluationReqDTO saveDemoTECCommitteeRemarks() {
		EvaluationReqDTO reqDTO = new EvaluationReqDTO();
		List<ScQtDemoTechnicalCommitteeRemark> remarks = this.getManagedBean().getScQt()
				.getScQtDemoTechnicalCommitteeRemarks();
		if (null != remarks) {
			remarks = removeNullRemark(remarks);
		}
		if (null != remarks && !remarks.isEmpty()) {
			reqDTO.setScQt(this.getManagedBean().getScQt());
		}
		return reqDTO;
	}
	// #28784 -end-
	
	// #28784 -start-
	public boolean isBeforeDemo() {
		boolean beforeDemo = false;
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF_DEMO);
		
		if (this.getManagedBean().getScQt().isDemoRequired()
				&& demoStatuses.contains(this.getManagedBean().getCurrentStatusId())) {
			beforeDemo = true;
		}
		return beforeDemo;
	}
	// #28784 -end-
	
	// #28784 -start-
	private void validateDemoRemarks() {
		if (this.getManagedBean().getScQt().isDemoRequired()
				&& (QTStatusConst.TEC_PENDING_ENDORSEMENT.equals(this.getManagedBean().getCurrentStatusId()) || QTStatusConst.TEC_PENDING_SIGN_OFF
						.equals(this.getManagedBean().getCurrentStatusId()))) {
			List<ScQtDemoTechnicalCommitteeRemark> demoTecRemarks = this.getManagedBean()
					.getDemoTechnicalCommitteeRemark();

			if (null != demoTecRemarks) {
				for (int i = 0; i < demoTecRemarks.size(); i++) {
					String userRemark = demoTecRemarks.get(i).getRemark();
					demoUserRemarkValidation(userRemark);
				}
			}
		}
	}
	// #28784 - end
	
	//#28784 add demo condition - Start
	/**
	 * If demo required return 'P' else return 'T'
	 * 
	 * @return
	 */
	private String demoOrTechnicalEvaluation() {
		if (this.getManagedBean().getScQt().isDemoRequired()) {
			return QTConstants.PRESENTATION_DEMO_EVALUATION;
		} else {
			return QTConstants.TECHNICAL_EVALUATION;
		}
	}
	//#28784 add demo condition - End
	
	//#28784 - Start
	/**
	 * used in xhtml, if wip save demo
	 */
	public void validateBeforeSave() {
		if (this.getManagedBean().isDemoWip()) {
			savePresentationDemoDtls();
		}
	}
	//#28784 - End
	//#28784 - Start
	/**
	 * used in xhtml, save page record
	 */
	public void retainDemoPage() {
		DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("form:demoSupplierId");
		if (null != dataTable) {
			this.getManagedBean().setRetainPageNumber(dataTable.getFirst());
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void retrieveMaxTechnicalScoreAndOtherElementsScore(List<ScQtCheckList> checklists) {
		BigDecimal maxTsScore = BigDecimal.ZERO;
		BigDecimal maxOtherElementsScore = BigDecimal.ZERO;
		BigDecimal maxItemOtherElementsScore = BigDecimal.ZERO;
		for (ScQtCheckList cl : checklists) {
			if (cl.getQtScoreWeightage() != null) {
				if (SourcingConstants.DOC_DEF_KEY_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(cl
								.getDefinationType()
								.getParameterCode())) {
					maxTsScore = maxTsScore.add(cl.getQtScoreWeightage().getScore());
				} else if (SourcingConstants.DOC_DEF_KEY_PRESENTATION_DEMO
						.equalsIgnoreCase(cl
								.getDefinationType()
								.getParameterCode())) {
					maxOtherElementsScore = maxOtherElementsScore.add(cl.getQtScoreWeightage().getScore());
					this.getManagedBean().setDemoWeightage(cl.getQtScoreWeightage().getWeightage());
				} else {
					maxOtherElementsScore = maxOtherElementsScore.add(cl.getQtScoreWeightage().getScore());
					maxItemOtherElementsScore = maxItemOtherElementsScore.add(cl.getQtScoreWeightage().getScore());
				}
			}
		}
		this.getManagedBean().setMaxPackageTsScore(maxTsScore);
		this.getManagedBean().setMaxPackageOtherElementsScore(maxOtherElementsScore);
		this.getManagedBean().setMaxItemOtherElementsScore(maxItemOtherElementsScore);
		this.getManagedBean().setMaxDemoScore(SourcingConstants.NO_TS_DEMO_DATA_EXIST);
	}
	//#28784 - End

	//#28784 - Start
	/**
	 * Get Total Max score of Other checklists including demo
	 * 
	 * @return
	 */
	/* use in 4ser
	public BigDecimal otherChecklistOverallScore() {
		String score = SourcingConstants.NO_TS_DEMO_DATA_EXIST;
		if (this.getManagedBean().getSinglesummaryItemList() != null) {
			ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SERVICE_OBJECT_CC, ITemplateSpecSO.class);
			score = templateSpecSO.getMaxDemoScore(this.getManagedBean().getSinglesummaryItemList().getTplSpecQuestion().getSpecQuestionId());
			if (score == null) {
				score = SourcingConstants.NO_TS_DEMO_DATA_EXIST;
			}
		}
		this.getManagedBean().setMaxDemoScore(score);
		BigDecimal allOtherChecklistScore = SourcingConstants.NO_TS_DEMO_DATA_EXIST.equals(this.getManagedBean().getMaxDemoScore()) ? 
				this.getManagedBean().getMaxItemOtherElementsScore() :
			this.getManagedBean().getMaxItemOtherElementsScore().add(new BigDecimal(this.getManagedBean().getMaxDemoScore()));
		this.getManagedBean().setMaxItemAllOtherElementsScore(allOtherChecklistScore);
		return allOtherChecklistScore;
	}*/
	//#28784 - End

	//#28784 - Start
	/**
	 * Get Total Max score of Template Specifications 
	 * 
	 * @return
	 */
	/* use in 4ser
	public String maxTsItemScore() {
		String score = SourcingConstants.NO_TS_DEMO_DATA_EXIST;
		if (this.getManagedBean().getSinglesummaryItemList() != null) {
			ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SERVICE_OBJECT_CC, ITemplateSpecSO.class);
			score = templateSpecSO.getSumOfTsSpecMaxScore(this.getManagedBean().getSinglesummaryItemList().getTplSpecQuestion().getSpecQuestionId());
			this.getManagedBean().setMaxItemTsScore(new BigDecimal(score));
		}
		return score;
	}*/
	//#28784 - End

	//#28784 - Start
	/* use in 4ser
	private void retrieveMaxTechnicalScoreAndOtherElementsPercentage(Long qtId) {
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
		ScQtScoreSetting scoreSetting = evaluationSO.getTechnicalItemScoreSetting(qtId);
		this.getManagedBean().setMaxItemTsPercentage(scoreSetting.getOverallTechnical());
		this.getManagedBean().setMaxItemAllOtherElementsPercentage(scoreSetting.getOverallOtherTechnical());
	}*/
	//#28784 - End
	
	//#28784 - Start
	public void checkingForBeforeDemoMeetingDetails() {
		if (!this.getManagedBean().isReadOnly()) {
			checkMeetingDetailsAndStatus(this.getManagedBean()
					.getScQtMeetings());
		}
		RequestContext.getCurrentInstance().execute("confirmUpdatePresentationDemoPopup.show()");
	}
	//#28784 - End
	
	//#28784 - Start
	private boolean checkDemoStatuses() {
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF_DEMO);
		
		return this.getManagedBean().getScQt().isDemoRequired()
				&& (demoStatuses.contains(this.getManagedBean().getCurrentStatusId())) && !this.getManagedBean().isDemoWip();
	}
	//#28784 - End
	
	//#28784 - Start
	private void demoUserRemarkValidation(String userRemark) {
		if (null == userRemark || "".equals(userRemark)) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.remarks", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void checkDemoRemark(ScQt scQt, ScWorkFlowStatus currentWorkFlowStatus) {
		if (null == this.getManagedBean().getDemoTechnicalCommitteeRemark()) {
			this.getManagedBean().setDemoTechnicalCommitteeRemark(new ArrayList<ScQtDemoTechnicalCommitteeRemark>());
			initializeRemarks(scQt, currentWorkFlowStatus, this.getManagedBean().getDemoTechnicalCommitteeRemark(),
					ScQtDemoTechnicalCommitteeRemark.class);

		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void setDemoRemarkNull(ScQt scQt) {
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF_DEMO);
		if (this.getManagedBean().getScQt().isDemoRequired()
				&& demoStatuses.contains(this.getManagedBean().getCurrentStatusId())) {
			scQt.setScQtDemoTechnicalCommitteeRemarks(null);
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void initializeDemoRemarkAndChecking(QuationTenderResDTO resDto, ScQt scQt, ScWorkFlowStatus currentWorkFlowStatus) {
		List<ScQtDemoTechnicalCommitteeRemark> remarkList = new ArrayList<>();

		for (ScRemark remark : resDto.getRemarks()) {
			ScQtDemoTechnicalCommitteeRemark qtRemark = (ScQtDemoTechnicalCommitteeRemark) remark;
			remarkList.add(qtRemark);
		}
		setRemarkAndInitializeRemark(remarkList, scQt, currentWorkFlowStatus);
		scQt.setScQtDemoTechnicalCommitteeRemarks(this.getManagedBean().getDemoTechnicalCommitteeRemark());
		List<Long> demoStatuses = new ArrayList<>();
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO_REVISION);
		demoStatuses.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_ENDORSEMENT_DEMO);
		demoStatuses.add(QTStatusConst.TEC_PENDING_SIGN_OFF_DEMO);
		if (this.getManagedBean().getScQt().isDemoRequired()
				&& demoStatuses.contains(this.getManagedBean().getCurrentStatusId())) {
			ScQtDemoTechnicalCommitteeRemark demoRemark = null;
			for (ScQtDemoTechnicalCommitteeRemark scRemark : scQt.getScQtDemoTechnicalCommitteeRemarks()) {
				scRemark.setEditable(false);
				scRemark.setBrandNew(false);
				if (scRemark.getRemark() != null && "".equalsIgnoreCase(scRemark.getRemark())
						&& scQt.getScQtDemoTechnicalCommitteeRemarks().size() == 1) {
					scQt.setScQtDemoTechnicalCommitteeRemarks(null);
				}
				if (scRemark.getRemark() == null) {
					demoRemark = scRemark;
				}
			}
			removeDemoRemark(scQt, demoRemark);
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void removeDemoRemark(ScQt scQt, ScQtDemoTechnicalCommitteeRemark demoRemark) {
		if (null != demoRemark) {
			scQt.getScQtDemoTechnicalCommitteeRemarks().remove(demoRemark);
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void setRemarkAndInitializeRemark(List<ScQtDemoTechnicalCommitteeRemark> remarkList, ScQt scQt,
			ScWorkFlowStatus currentWorkFlowStatus) {
		if (null == this.getManagedBean().getDemoTechnicalCommitteeRemark()
				|| this.getManagedBean().getDemoTechnicalCommitteeRemark().isEmpty()) {
			this.getManagedBean().setDemoTechnicalCommitteeRemark(remarkList);
			initializeRemarks(scQt, currentWorkFlowStatus, this.getManagedBean().getDemoTechnicalCommitteeRemark(),
					ScQtDemoTechnicalCommitteeRemark.class);
		}
	}
	//#28784 - End
	
	//#28784 - Start
	private void removeUlasanDemo(List<ScQtEvaluationReport> reportList) {
		List<ScQtEvaluationReport> evaluationReports = new ArrayList<>();
		for (ScQtEvaluationReport evalReport : reportList) {
			if (!evalReport.getTitle().equalsIgnoreCase(SourcingConstants.DEMO_REPORT)
					&& !evalReport.getTitle().equalsIgnoreCase(SourcingConstants.DEMO_REPORT_WITH_SLASH)) {
				evaluationReports.add(evalReport);
			}
		}
		this.getManagedBean().setScQtEvaluationReportList(evaluationReports);
	}
	//#28784 - End
	
	//#28784 - Start
	private List<ScQtDemoTechnicalCommitteeRemark> removeNullRemark(List<ScQtDemoTechnicalCommitteeRemark> remarks) {
		for (int i = 0; i < remarks.size(); i++) {
			String userRemark = remarks.get(i).getRemark();

			if (null == userRemark || "".equals(userRemark)) {
				remarks.remove(i);
			}
		}
		return remarks;
	}
	//#28784 - End
	
	//#28784 - Start
	private void retrieveDemoDetails() {
		String taskName = this.getManagedBean().getTaskName();
		if (SCProcessNameConst.VIEW_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ENDORSE_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SIGNOFF_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.UPDATE_DEMO_TEC_EVALUATION
				.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setReadOnly(true);
		} else if (SCProcessNameConst.FINALISE_DEMO_TEC_EVALUATION.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setReadOnly(false);
		}
		retrivePresentationDemo();
	}
	//#28784 - End
}

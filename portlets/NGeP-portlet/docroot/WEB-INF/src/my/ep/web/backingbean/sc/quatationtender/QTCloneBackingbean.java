package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateDocConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtAppRequestRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalReqAttachement;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtRequestContent;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierZone;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.ApproverSettings;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.QTFieldMapper;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.persistence.indirection.IndirectList;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "qTCloneBackingbean")
@RequestScoped
public class QTCloneBackingbean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/**
	 * test only
	 */
	
	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(QTCloneBackingbean.class);

	private TreeNode root;
	private TreeNode[] selectedNodes;
	private boolean showTree = false;
	private static final String ALL = "lbl.txt.all";
	private static final String QT_DETAILS = "lbl.sc.qt.qtn.details";
	private static final String SUPPLIER_QUALIFICATION_CRITERIA = "lbl.sc.supplier.qulification.criteria";
	private static final String COMMITTEE_APPOINTMENT = "lbl.sc.qt.committee.appointment";
	private static final String APPROVAL = "lbl.sc.approval";
	
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
			  SourcingConstants.MALAYSIA_COUNTRY_CODE);
	
	/**
	 * lbl.sc.qt.briefing.sv
	 * lbl.sc.qt.schedule.contact
	 * lbl.sc.deduction.payment
	 * lbl.sc.qt.documents
	 * lbl.sc.qt.public.approval
	 */
	private static final String BRIEFING = "lbl.sc.qt.briefing.sv";
	private static final String PUBLISHING = "lbl.sc.qt.schedule.contact";
	private static final String PAYMENT = "lbl.sc.deduction.payment";
	private static final String DOCUMENT = "lbl.sc.qt.documents";
	private static final String PUBLICATION_APPROVAL = "lbl.sc.qt.public.approval";
	
	

	/**
	 * Instantiates a new QT committe backing bean.
	 */
	public QTCloneBackingbean() {
		Long statusId=this.getManagedBean().getQt().getQtStatus().getStatusId();
		this.root = new DefaultTreeNode("Root Node", null);

		TreeNode child = new DefaultTreeNode(new QTSelectionNode(getLabel(ALL),0L), this.root);
		child.setParent(this.root);
		child.setExpanded(true);
		
		if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statusId)){
			TreeNode descendent1 = new DefaultTreeNode(new QTSelectionNode(getLabel(QT_DETAILS),1L), child);
			descendent1.setParent(child);
			//25635-start
			descendent1.setSelected(Boolean.TRUE);
			descendent1.setSelectable(Boolean.TRUE);
			//25635-end
			TreeNode descendent2 = new DefaultTreeNode(new QTSelectionNode(getLabel(SUPPLIER_QUALIFICATION_CRITERIA),2L), child);
			descendent2.setParent(child);
			TreeNode descendent3 = new DefaultTreeNode(new QTSelectionNode(getLabel(COMMITTEE_APPOINTMENT),3L), child);
			descendent3.setParent(child);
			TreeNode descendent4 = new DefaultTreeNode(new QTSelectionNode(getLabel(APPROVAL),4L), child);
			descendent4.setParent(child);
		}
		if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId)){
			TreeNode descendent5 = new DefaultTreeNode(new QTSelectionNode(getLabel(BRIEFING),5L), child);
			descendent5.setParent(child);
			descendent5.setSelected(true);
			TreeNode descendent6 = new DefaultTreeNode(new QTSelectionNode(getLabel(PUBLISHING),6L), child);
			descendent6.setParent(child);
			descendent6.setSelected(true);
			TreeNode descendent7 = new DefaultTreeNode(new QTSelectionNode(getLabel(PAYMENT),7L), child);
			descendent7.setParent(child);
			descendent7.setSelected(true);
			TreeNode descendent8 = new DefaultTreeNode(new QTSelectionNode(getLabel(DOCUMENT),8L), child);
			descendent8.setParent(child);
			descendent8.setSelected(true);
			TreeNode descendent9 = new DefaultTreeNode(new QTSelectionNode(getLabel(PUBLICATION_APPROVAL),9L), child);
			descendent9.setParent(child);
			descendent9.setSelected(true);
		}
		
		if (null == this.getManagedBean().getCommitteMember()) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

	}
	
	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone qtn tender search bean. */
		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param cloneQtnTenderSearchBean
		 *            the clone qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean cloneQtnTenderSearchBean) {
			super(componentId, false);
			this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				populateCloneQTSearchCriteria(cloneQtnTenderSearchBean,
						searchBean);
				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}
	
	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneQtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = cloneQtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			String qtNumber = cloneQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			
			
			if (null != this.getManagedBean().getQt()
					&& null != this.getManagedBean().getQt().getQtNo()) {
				List<String> qtNo = new ArrayList<String>();
				qtNo.add(this.getManagedBean().getQt().getQtNo());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchValue(qtNo);
				searchFilterBean.add(bean);
			}
			
			Long statId=this.getManagedBean().getQt().getQtStatus().getStatusId();
			if (null != this.getManagedBean().getQt() 
					&& null != this.getManagedBean().getPanel() 
					//&& QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statId)
					){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isPanel");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getPanel());
				searchFilterBean.add(bean);
			}
			
			if (null != this.getManagedBean().getQt()){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isZonal");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isZonal());
				searchFilterBean.add(bean);
				
			}
			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getQt().getContractType()&& null != this.getManagedBean().getQt().getContractType().getParameterId()){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("contractType.parameterId");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().getContractType().getParameterId());
				searchFilterBean.add(bean);
				
			}
			
			if (null != this.getManagedBean().getQt() && null!=this.getManagedBean().getQt().getFulfilmentType()&& null!=this.getManagedBean().getQt().getFulfilmentType().getParameterId()){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("fulfilmentType.parameterId");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().getFulfilmentType().getParameterId());
				searchFilterBean.add(bean);
				
			}
				
			String procurementMode = cloneQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			}
			/*
			 * Date fromCreatedDate = cloneQtnTenderSearchBean
			 * .getFromCreatedDate(); Date toCreatedDate =
			 * cloneQtnTenderSearchBean.getToCreatedDate(); if (null !=
			 * fromCreatedDate || toCreatedDate != null) { SearchFilterBean bean
			 * = new SearchFilterBean();
			 * bean.setSearchField(QTConstants.CREATED_DATE);
			 * bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			 * SearchBetweenValue<Date> betweenVal = new
			 * SearchBetweenValue<Date>( fromCreatedDate, toCreatedDate);
			 * bean.setSearchValue(betweenVal); searchFilterBean.add(bean); }
			 */
			Date fromCreatedDate = cloneQtnTenderSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				// Setting current date as To-date to perform search if user selected only From Date
				if(toCreatedDate == null){
					toCreatedDate = DateUtils.getCurrentDateWithoutTime();
				}
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromPublishDate = cloneQtnTenderSearchBean
					.getFromPublishDate();
			Date toPublishDate = cloneQtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				// Setting current date as To-date to perform search if user selected only From Date
				if(toPublishDate == null){
					toPublishDate = DateUtils.getCurrentDateWithoutTime();
				}
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromClosingDate = cloneQtnTenderSearchBean
					.getFromClosingDate();
			Date toClosingDate = cloneQtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				// Setting current date as To-date to perform search if user selected only From Date
				if(toClosingDate == null){
					toClosingDate = DateUtils.getCurrentDateWithoutTime();
				}
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = cloneQtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}else{
				Long[] stats= null;
				Long statusId=this.getManagedBean().getQt().getQtStatus().getStatusId();
				ArrayList<Long> s=null;
				if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statusId)){
					stats=getStatuses(QTStatusConst.QT_PENDING_QT_SUBMISSION,QTStatusConst.QT_PENDING_QT_SUBMISSION);
					s=new ArrayList<Long>(Arrays.asList(stats));
				}
				if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId)){//25635
					stats=getStatuses(QTStatusConst.QT_PENDING_QT_SUBMISSION,QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
					s=new ArrayList<Long>(Arrays.asList(stats));
					s.add(QTStatusConst.QT_PENDING_BPK_APPROVAL);
					s.add(QTStatusConst.QT_REVISION_PUBLICATION);//25635
				}
				
				
				qtSearchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean(
						"scQtStatus.pmStatus.statusId",
						SearchOperator.OPERATOR_NOT_IN.value(), s));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Filter by - ptj org profileId
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							getPTJOrgProfileByLoginUser().getOrgProfileId()));
			
			// Filter by - evaluationType
			String commType = getManagedBean().getCommType();
			String evaluationType = getManagedBean().getQt().getEvaluationType();
			if (StringUtils.isNotBlank(evaluationType) &&
					(commType == null || !ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.equals(commType))) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("evaluationType",
								SearchOperator.OPERATOR_EQUALS.value(),
								evaluationType));	
			}
			// Filter by - PartialOffer		
	if (null != this.getManagedBean().getQt()){//21695&25635-start
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isPartial");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isPartial());
				searchFilterBean.add(bean);
				
			}//21695&25635-End
	Long statusId=this.getManagedBean().getQt().getQtStatus().getStatusId();//33904
	if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId)){//33904
		SearchFilterBean samplebean = new SearchFilterBean();
		samplebean.setSearchField("sampleReq");
		samplebean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		samplebean.setSearchValue(this.getManagedBean().getQt().isSampleReq());
		searchFilterBean.add(samplebean);
		
		SearchFilterBean demobean = new SearchFilterBean();//33905
		demobean.setSearchField("demoRequired");
		demobean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		demobean.setSearchValue(this.getManagedBean().getQt().isDemoRequired());
		searchFilterBean.add(demobean);//33905
		
	}//33904
						
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}
	
	public Long[] getStatuses(Long fromStatusId,Long toStatusId){
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		orderedStatuses = orderedStatuses.subList(
				orderedStatuses.indexOf(fromStatusId), 
				orderedStatuses.indexOf(toStatusId)+1);
		Long[] ret = new Long[orderedStatuses.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = orderedStatuses.get(i).longValue();
	    }
	    return ret;
	}

	public void cloneCommitteePopupAction() {
//		debugLogger("cloneCommitteePopupAction");
		// logic to check whether committe will allow for clone or not..
		// checkIsSpecCommitteFormOrNot();
		getManagedBean().setCloneCommitteeSelectQt(null);
		this.getManagedBean().setSelectedCloneTender(null);//25635

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getMainCloneSearchQTDataModel();//34053
		

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setMainCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid2",
							getManagedBean().getCloneQtnTenderSearchBean()));//34053
		}
		getManagedBean().getMainCloneSearchQTDataModel().resetData();//34053

		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		//getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());//34053
		RequestContext.getCurrentInstance().execute("screen3.show()");
		getManagedBean().setAutoUpdate(false);
		 getManagedBean().setSelectSearchcommitteeNode(new TreeNode[0]);
		 getManagedBean().setCommType(null);
		
		//9019 - Start (Harun) initializing remark 
	    //       This code for initialize back after Clone button is trigger while remarks is 
		//       able to entered when available.Reopen remarks disabled #note-253
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			
			ScQt scQt = this.getManagedBean().getQt();
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else{
			ScQt scQt = this.getManagedBean().getQt();
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}
		//9019 - End (Harun)
		 
        //9019 added for disable mandatory checking while clone
         ScQt scQt = getManagedBean().getQt();
		 scQt.setCloned(true);
		 getManagedBean().setQt(scQt);
	}
	
	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(
				context, "lbl");
		String message = bundle.getString(label);
		return message;
	}

//	/**
//	 * Debug logger.
//	 * 
//	 * @param message
//	 *            the message
//	 */
//	private void debugLogger(Object message) {
//
//		ScQt scQt = this.getManagedBean().getQt();
//		if (scQt != null) {
//			message += " @qtNo = " + scQt.getQtNo();
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug(message);
//		}
//		/*
//		 * else{ logger.info(message); }
//		 */
//
//	}

	/**
	 * On row clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
//		debugLogger("onRowCloneAction");
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		ScQt scQt = (ScQt) event.getObject();
		Long qtId = scQt.getQtId();
		String evaluationType = scQt.getEvaluationType();
		if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(this.getManagedBean().getQt().getQtStatus().getStatusId())){//25635-start
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);
		}//25635-End
		
		Long statusId=this.getManagedBean().getQt().getQtStatus().getStatusId();
		if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId) ){//25635
			this.showTree = false;
		}else{
			this.showTree = true;
		}
	}
	
	
	public void refreshCreation(){
		FacesContext fc = FacesContext.getCurrentInstance();
		super.renewSesionObject();
		this.getManagedBean().setReadOnly(false);
		this.getManagedBean().setCreateQt(true);
		this.getManagedBean().setOutComes(
				new String[] { ResourceBundleUtils.getMessage("lbl.btn.submit",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()) });
		
		ScQt scQt = new ScQt();
		scQt.setCircularVer(new CtCircularVer());
		scQt.setPanel(this.getManagedBean().getPanel());
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		if (pmOrgProfile != null) {
			scQt.setPmOrgProfile(pmOrgProfile);
			scQt.setOwnerOrgProfile(pmOrgProfile);
			scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			scQt.setOwnerOrgProfileId(pmOrgProfile.getOrgProfileId());
			// check whether ptj have the special approval or not for login user
			PmPtjSpecialApproval ptjSpecialApproval = getPtjSpecialApprovalByOrgProfileId(pmOrgProfile
					.getOrgProfileId());
			if (ptjSpecialApproval != null) {
				scQt.setCircularVer(null);
				this.getManagedBean().setPanel(null);
				scQt.setPanel(false);
				this.getManagedBean().setPtjSpecialApproval(ptjSpecialApproval);
				scQt.setObtainSpecialApproval(false);
			}else{
				scQt.setItemGroup(new CtPanelItemGroup());
			}
		}else{
			addFacesMessage(fc, MessageConst.ERROR_760447, null);
			this.getManagedBean().setPtjPresent(false);
		}
		
		scQt.setProcurementMode(new PmParameter());
		scQt.setProcurementTypeCatId(new PmParameter());
		scQt.setFulfilmentType(new PmParameter());
		scQt.setPmProcurementCategory(new PmParameter());
		scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
		
		PmStatus status = getPmStatusByStatusId(QTStatusConst.QT_PENDING_QT_SUBMISSION);
		scQt.setQtStatus(status);            
		/* scQt.setQtNo(dto.getQtNo()); */
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(scQt);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(Boolean.TRUE);
		qtStatus.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		scQt.setCurrentWorkFlowStatus(qtStatus);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		scQt.setScQtStatus(qtStatuses);
		ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
		supplierCriteria.setScQt(scQt);
		scQt.setScQtSupplierCriteria(supplierCriteria);
						
		
		this.getManagedBean().setActiveIndex(0);
		scQt.setContractType(new PmParameter());
		
		setUserDetails(scQt);
		this.getManagedBean().setQt(scQt);
		Long currentStatus = this.getManagedBean().getQt().getQtStatus()
				.getStatusId();
		this.getManagedBean().setCurrentStatusId(currentStatus);
		
		
		if (pmOrgProfile != null && !isPtjAddressTypePresent(pmOrgProfile.getOrgProfileId())) {
			addFacesMessage(fc, MessageConst.ERROR_760450, null);
		}
		
	}
	
	
	public void refreshQT(){
		ScQt scQt = new ScQt();
		scQt.setCircularVer(new CtCircularVer());
		scQt.setPanel(this.getManagedBean().getPanel());
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		if (pmOrgProfile != null) {
			scQt.setPmOrgProfile(pmOrgProfile);
			scQt.setOwnerOrgProfile(pmOrgProfile);
			scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			scQt.setOwnerOrgProfileId(pmOrgProfile.getOrgProfileId());
			// check whether ptj have the special approval or not for login user
			PmPtjSpecialApproval ptjSpecialApproval = getPtjSpecialApprovalByOrgProfileId(pmOrgProfile
					.getOrgProfileId());
			if (ptjSpecialApproval != null) {
				scQt.setCircularVer(null);
				scQt.setPanel(false);
				scQt.setObtainSpecialApproval(false);
			}else{
				scQt.setItemGroup(new CtPanelItemGroup());
			}
		}
		scQt.setProcurementMode(new PmParameter());
		scQt.setProcurementTypeCatId(new PmParameter());
		scQt.setFulfilmentType(new PmParameter());
		scQt.setPmProcurementCategory(new PmParameter());
		scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
		
		PmStatus status = getPmStatusByStatusId(QTStatusConst.QT_PENDING_QT_SUBMISSION);
		scQt.setQtStatus(status);            
		/* scQt.setQtNo(dto.getQtNo()); */
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(scQt);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(Boolean.TRUE);
		qtStatus.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		scQt.setCurrentWorkFlowStatus(qtStatus);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		scQt.setScQtStatus(qtStatuses);
		ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
		supplierCriteria.setScQt(scQt);
		scQt.setScQtSupplierCriteria(supplierCriteria);
		scQt.setContractType(new PmParameter());
		
		setUserDetails(scQt);
		this.getManagedBean().setQt(scQt);
	}
	
	private boolean isCreation(TreeNode[] nodes){
		List<Long> list=new ArrayList<Long>();
		for(TreeNode node:nodes){
			QTSelectionNode selection=(QTSelectionNode) node.getData();
			list.add(selection.getValue());
		}
		if(list.contains(1L)||list.contains(2L)||list.contains(3L)||list.contains(4L)){
            return true;
        }
		return false;
	}
	
	// public void addToClone(Long prevQtId,TreeNode[] selectedNodes){
	public void addToClone() {
		IQTDetailSO QtDetailsSO;
		Long statusId=this.getManagedBean().getQt().getQtStatus().getStatusId();
		
		ScQt selected=this.getManagedBean().getSelectedCloneTender();
		TreeNode[] nodes=this.selectedNodes;
		if(!QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) && !QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId)){//25635
			if(nodes.length==0){
				throw new EpException(MessageConst.ERROR_760495);
			}
		}
		try {
			if(isCreation(nodes)){
			//	refreshQT();
			}
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setMaintanceFlow(this.getManagedBean()
					.isMaintanceFlow());
			tenderReqDTO.setQtId(selected.getQtId());
			tenderReqDTO.setBdgDtlsReq(true);
			
			QuationTenderResDTO tenderResDTO = null;
			ScQt scQt = this.getManagedBean().getQt();
			/*
			 * TRACE HI-EXEC TIME
			 * Development #29385
			 */
			long startTime = System.currentTimeMillis();
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			SLAUtils.getInstance().logHiExecInfo("QT","QTCloneBackingbean.addToClone", startTime);
			/*
			 * END
			 */
			ScQt copyScqt = tenderResDTO.getQtDetail();
			
			FacesContext context = FacesContext.getCurrentInstance();
			ELContext elContext = context.getELContext();
			QTDetailBackingBean qTDetailBB = (QTDetailBackingBean) elContext.getELResolver().getValue(elContext, null, "qTDetailBB");
			if (tenderResDTO.getQtDetail() != null) {
				for(TreeNode node:nodes){
					QTSelectionNode selection=(QTSelectionNode) node.getData();
					if(selection.getValue().equals(1L)){
						retrieveQtnDetails(scQt,copyScqt,QtDetailsSO);
						qTDetailBB.retrieveQuationTenderDetails(scQt.getQtId());
					}
					if(selection.getValue().equals(2L)){
						retrieveSupplierCriteria(scQt,copyScqt,QtDetailsSO);
						qTDetailBB.retrieveSuppQualificationTabDtl();
					}
					if(selection.getValue().equals(3L)){
						retrieveCommitteeMembers(scQt,copyScqt);
						updateCommittees();
						qTDetailBB.retrieveCommitteeAppointmentTabDtl(0, 0);
					}
					
					if(selection.getValue().equals(4L)){
						copyScqt=retrieveScqtCommittee(copyScqt);
						retrieveApproval(scQt,copyScqt,QtDetailsSO);
					}
					
				}
				if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId)){//25635
					if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId)){//31350-start
//						log.info("==========stratCommitteeCloneDuringPendingDocument========="+scQt.getQtNo());//31350
						retrieveComittee1And2Tier(scQt,copyScqt);
						qTDetailBB.retrieveApprovalTabDtl();//34332&25635
//						log.info("==========EndCommitteeCloneDuringPendingDocument========="+scQt.getQtNo());//31350
					}//31350-End
					else if(QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId)){//31350-start
//						log.info("==========stratCommitteeCloneDuringPendingrevision========="+scQt.getQtNo());//31350
						retrieveComittee1And2TierRevision(scQt,copyScqt);
						qTDetailBB.retrieveApprovalTabDtl();//34332&25635
//						log.info("==========EndCommitteeCloneDuringPendingrevision========="+scQt.getQtNo());//31350
					}//31350-End
					retrieveBrief(copyScqt);
					retrievePublishing(copyScqt);
					retrieveDeductionPaymentTabDtl(scQt,copyScqt,QtDetailsSO);
					retrieveDocs(copyScqt);
					retrievePublicationApprovalTabDtl(copyScqt);
					retriveFulfilmentDtls(copyScqt);//33916
				}
				
			}

		} finally {

			QtDetailsSO = null;
		}
		RequestContext.getCurrentInstance().execute("screen3.hide()");
	}
	
	public List<ScQtDocReport> retrieveAllDocumentsByQt(ScQt scQt) {
		List<ScQtDocReport> list=null;
		IQTDocumentSO qtDocumentSO;
		
			QTDocumentResDTO resDTO = new QTDocumentResDTO();
			try {
				qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
				QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
				reqDTO.setScQt(scQt);
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				resDTO = qtDocumentSO.retriveQTDocDetails(reqDTO);
				list= resDTO.getQtDocReports();
				
			} finally {
				qtDocumentSO = null;
				resDTO=null;
			}
		
		return list;
	}
	
	public List<ScQtDocReport> retrieveDocumentByQt(ScQt scQt) {
		List<ScQtDocReport> list=null;
		List<ScQtDocReport> listRet=new ArrayList<ScQtDocReport>();
		IQTDocumentSO qtDocumentSO;
		
			QTDocumentResDTO resDTO = new QTDocumentResDTO();
			try {
				qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
				QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				resDTO = qtDocumentSO.retriveCloneQTDocDetails(reqDTO);
				
				
				list= resDTO.getQtDocReports();
				if(null!=list && CollectionUtils.isNotEmpty(list)){//25635-start
				for(ScQtDocReport toRet:list){
					if(null==toRet.getPmDocument()){
						listRet.add(toRet);
					}else{
					   String docDefinitionKey=toRet.getPmDocument().getDocDefinitionKey();
						if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
								.equals(docDefinitionKey)) {
							listRet.add(toRet);
						}
					}
					
					
				}
			}//25635-End
				
				
			} finally {
				qtDocumentSO = null;
				resDTO=null;
			}
		
		return listRet;
	}
	
	private void retrieveDocs(ScQt copyQt){
//		debugLogger("addDocumentCloneAction");
		ScQt scQt = this.getManagedBean().getQt();
		
		
		
		List<ScQtDocReport> reportList = getManagedBean()
				.getScQtDocReportList();
		if (null == reportList) {
			reportList = retrieveAllDocumentsByQt(scQt);
		}
		
		//List<ScQtDocReport> docInDBList = retrieveDocumentByQt(scQt);
		List<ScQtDocReport> listDel=new ArrayList<ScQtDocReport>();
		if(null!=reportList && !reportList.isEmpty()){
			for(ScQtDocReport toDel:reportList){
				if(null==toDel.getPmDocument()){
					toDel.setDeleted(true);
					listDel.add(toDel);
				}
			}
			this.getManagedBean().setRemoveDocReports(listDel);
		}
		reportList.removeAll(listDel);
		
		List<String> existedDocumets=new ArrayList<String>(); //additional docs;
		ScQtDocReport templetDoc=null;
		if(CollectionUtils.isNotEmpty(reportList)){
			for(ScQtDocReport docReport:reportList){
				if(null==docReport.getPmDocument()){
				existedDocumets.add(docReport.getTitle());
				}
				else{
				String docDefinitionKey=	docReport.getPmDocument().getDocDefinitionKey();
					if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
							.equals(docDefinitionKey)) {
					templetDoc=docReport;
					}
				}
			}
			
		}
		List<ScQtDocReport> scQtDocReports = retrieveDocumentByQt(copyQt);
//				this.getManagedBean()
//				.getSelectedCloneScQtDocReports();
		if (null!=scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport docReport : scQtDocReports) {
				ScQtDocReport qtDocReport = null;
				ScQtDocReportAttachment qtDocReportAttachment=null;
				try {
					if (!existedDocumets
							.contains(
									 docReport.getTitle())) {
						qtDocReport = (ScQtDocReport) BeanUtils
								.cloneBean(docReport);
					}
				} catch (IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException e) {
					log.warn(e.getMessage(), e);
				}
				if (null != qtDocReport) {
					qtDocReport.setQtReportId(null);
					qtDocReport.setScQt(scQt);
					qtDocReport.setDocId(String.valueOf(qtDocReport.getScQt().getQtId()));
					setUserDetails(qtDocReport);
					qtDocReport.setChangedDate(null);
					
					if (docReport.getScQtDocReportAttachment() != null) {
						try {
							 qtDocReportAttachment = (ScQtDocReportAttachment)BeanUtils.cloneBean(docReport.getScQtDocReportAttachment());
							if (qtDocReportAttachment != null) {
								qtDocReportAttachment.setAttachmentId(null);
								qtDocReportAttachment.setScQtDocReport(qtDocReport);
								setUserDetails(qtDocReportAttachment);
								qtDocReportAttachment.setChangedDate(null);
								qtDocReportAttachment.setDocNo(qtDocReport.getScQt().getQtNo());//33954
								try {
									uploadAttachment(qtDocReportAttachment,docReport.getScQtDocReportAttachment());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}//33954
								qtDocReport.setScQtDocReportAttachment(qtDocReportAttachment);
							}
						} catch (IllegalAccessException | InstantiationException
								| InvocationTargetException | NoSuchMethodException e) {
							log.warn(e.getMessage(), e);
						}	
					}
					boolean toClone=true;
					//if SS - syarat-syarat am
					String docDefinitionKey=null;
					if(null!=docReport.getPmDocument()){
						docDefinitionKey=docReport.getPmDocument().getDocDefinitionKey();
					}		
					boolean templateDoc=Boolean.FALSE;//33954
					if(null!=docDefinitionKey && SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
							.equals(docDefinitionKey)){
						IQTTemplateSO qtTemplateSO = null;
						try{
							qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
							QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
							if (docReport.getTplDocMaster() != null) {
								templateDoc=Boolean.TRUE;//33954
								if(null!=templetDoc){
//								log.info("=======removeDocumentCodesexecuted======");//33954
								int index=reportList.indexOf(templetDoc);
								reportList.remove(templetDoc);
								templetDoc=retriveRulesAndRegulationReport(templetDoc);
								TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
								master=cloneDocMasterDocSectionDetails(master, templetDoc); //replace tplDocmaster
								templetDoc.setTplDocMaster(master);
									reqDTO.setScQtDocReport(templetDoc);
									templetDoc.setCloned(Boolean.TRUE);
									reportList.add(index,templetDoc);//33954
								}else{
//									log.info("=======UnremoveDocumentCodesexecuted======");//33954
									TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
									master=cloneDocMasterDocSectionDetails(master, qtDocReport);
									qtDocReport.setTplDocMaster(master);
										reqDTO.setScQtDocReport(qtDocReport);
									qtDocReport.setCloned(Boolean.TRUE);
									reportList.add(qtDocReport);//33954
									
								}
								//QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);//33954
								//qtDocReport=resDTO.getScQtDocReport();//33954	
							}else{
								//if(null!=templetDoc){
								//	reportList.remove(templetDoc);
								//}
								//reqDTO.setScQtDocReport(qtDocReport);	
								toClone=false;
							}
							
							
						}finally{
							qtTemplateSO = null;
						}
					}
					
					if(toClone && !templateDoc){//33954
						reportList.add(qtDocReport);
					}
					
					
				}
			}
			 getManagedBean()
				.setScQtDocReportList(reportList);
		} else {
			//throw new EpException(MessageConst.ERROR_760141);//25635
		}
		existedDocumets=null;
	}
	
	private void retrieveDocs2(ScQt copyQt){
//		debugLogger("addDocumentCloneAction");
		ScQt scQt = this.getManagedBean().getQt();
		
//		List<ScQtDocReport> docInDBList = retrieveDocumentByQt(scQt);
//		if(null!=docInDBList && !docInDBList.isEmpty()){
//			List<ScQtDocReport> listDel=new ArrayList<ScQtDocReport>();
//			for(ScQtDocReport toDel:docInDBList){
//				if(null==toDel.getPmDocument()){
//					toDel.setDeleted(true);
//					listDel.add(toDel);
//				}
//			}
//			this.getManagedBean().setRemoveDocReports(listDel);
//		}
		
		List<ScQtDocReport> reportList = getManagedBean()
				.getScQtDocReportList();
		if (null == reportList) {
			reportList = retrieveAllDocumentsByQt(scQt);
		}
		List<String> existedDocumets=new ArrayList<String>();
		ScQtDocReport templetDoc=null;
		if(CollectionUtils.isNotEmpty(reportList)){
			for(ScQtDocReport docReport:reportList){
				if(null==docReport.getPmDocument()){
				existedDocumets.add(docReport.getTitle());
				}
				else{
				String docDefinitionKey=	docReport.getPmDocument().getDocDefinitionKey();
					if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
							.equals(docDefinitionKey)) {
					templetDoc=docReport;
					}
				}
			}
			
		}
		List<ScQtDocReport> scQtDocReports = retrieveDocumentByQt(copyQt);
//				this.getManagedBean()
//				.getSelectedCloneScQtDocReports();
		if (null!=scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport docReport : scQtDocReports) {
				ScQtDocReport qtDocReport = null;
				ScQtDocReportAttachment qtDocReportAttachment=null;
				try {
					if (!existedDocumets
							.contains(
									 docReport.getTitle())) {
						qtDocReport = (ScQtDocReport) BeanUtils
								.cloneBean(docReport);
					}
				} catch (IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException e) {
					log.warn(e.getMessage(), e);
				}
				if (null != qtDocReport) {
					qtDocReport.setQtReportId(null);
					qtDocReport.setScQt(scQt);
					qtDocReport.setDocId(String.valueOf(qtDocReport.getScQt().getQtId()));
					setUserDetails(qtDocReport);
					qtDocReport.setChangedDate(null);
					
					if (docReport.getScQtDocReportAttachment() != null) {
						try {
							 qtDocReportAttachment = (ScQtDocReportAttachment)BeanUtils.cloneBean(docReport.getScQtDocReportAttachment());
							if (qtDocReportAttachment != null) {
								qtDocReportAttachment.setAttachmentId(null);
								qtDocReportAttachment.setScQtDocReport(qtDocReport);
								setUserDetails(qtDocReportAttachment);
								qtDocReportAttachment.setChangedDate(null);
								
								qtDocReport.setScQtDocReportAttachment(qtDocReportAttachment);
							}
						} catch (IllegalAccessException | InstantiationException
								| InvocationTargetException | NoSuchMethodException e) {
							log.warn(e.getMessage(), e);
						}	
					}
					if (docReport.getTplDocMaster() != null) {
						IQTTemplateSO qtTemplateSO = null;
						try {
							qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
					if(null!=templetDoc){
					reportList.remove(templetDoc);
					templetDoc=retriveRulesAndRegulationReport(templetDoc);
					TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
					master=cloneDocMasterDocSectionDetails(master, templetDoc);
					templetDoc.setTplDocMaster(master);
						QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
						reqDTO.setScQtDocReport(templetDoc);
						QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);
						qtDocReport=resDTO.getScQtDocReport();
					}else{
						TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
						master=cloneDocMasterDocSectionDetails(master, qtDocReport);
						qtDocReport.setTplDocMaster(master);
							QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
							reqDTO.setScQtDocReport(qtDocReport);
							QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);
							qtDocReport=resDTO.getScQtDocReport();
						
					}
						
					}finally{
						qtTemplateSO = null;
					}
						
					}
					reportList.add(qtDocReport);
					
				}
			}
			 getManagedBean()
				.setScQtDocReportList(reportList);
		} else {
			throw new EpException(MessageConst.ERROR_760141);
		}
		existedDocumets=null;
	}
	
	
	private TplDocMaster cloneDocMasterDocSectionDetails(TplDocMaster templateDocMaster,ScQtDocReport scQtDocReport){
		// clone TplDocMaster Details
				TplDocMaster tplDocMaster = (TplDocMaster) templateDocMaster
						.cloneEntity();
				tplDocMaster.setDocMasterId(null);
				tplDocMaster.setOrgProfile(null);
				tplDocMaster.setTplDocSections(null);
				// tplDocMaster.setTplDocSectionCollection(null);
				tplDocMaster.setDocTitle(scQtDocReport.getTitle());
				tplDocMaster.setDocName(scQtDocReport.getTitle());

				setUserDetails(tplDocMaster);

				// clone tplDocSection Details
				/*
				 * tplDocMaster.setTplDocSections(new ArrayList<TplDocSection>());
				 * List<TplDocSection> docSections =
				 * templateDocMaster.getTplDocSections(); for (TplDocSection
				 * tplDocSection : docSections) { TplDocSection section =
				 * (TplDocSection) tplDocSection.cloneEntity();
				 * section.setDocSectionId(null); section.setParent(null); //
				 * Temporarily am setting null, need to confirm once
				 * section.setTplDocMaster(tplDocMaster);
				 * 
				 * setUserDetails(section);
				 * tplDocMaster.getTplDocSections().add(section); }
				 */

				tplDocMaster.setTplDocSectionCollection(new ArrayList<TplDocSection>());
				List<TplDocSection> docSectionCOllection = templateDocMaster
						.getTplDocSectionCollection();
				for (TplDocSection tplDocSection : docSectionCOllection) {
					TplDocSection section = (TplDocSection) tplDocSection.cloneEntity();
					//log.info("===doc section name"+section.getSectionTitle()+"section content"+section.getSectionContent());
					if(TemplateDocConst.PARAM_DOC_SECTION_HEADER_TITLE.equalsIgnoreCase(section.getSectionTitle())){
						String title=TemplateDocConst.PARAM_DOC_SECTION_HEADER_TITLE+TemplateDocConst.PARAM_SPACE+getParameterDesc(this.getManagedBean().getQt().getProcurementMode(), myLocale).toUpperCase();
						section.setSectionName(title);
						section.setSectionTitle(title);
					}
					section.setDocSectionId(null);
					section.setParent(null); // Temporarily am setting null, need to
												// confirm once
					section.setTplDocMaster(tplDocMaster);

					setUserDetails(section);
					tplDocMaster.getTplDocSectionCollection().add(section);
				}

				// Procurement Category
				PmParameter procurementCategory = scQtDocReport.getScQt()
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

				tplDocMaster.setScReport(scQtDocReport);
				
				return tplDocMaster;
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
	 * Retrieve tpl doc master by master id.
	 * 
	 * @param masterId
	 *            the master id
	 * @return TplDocMaster
	 */
	private TplDocMaster retrieveTplDocMasterById(Long masterId) {
//		debugLogger("retrieveTplDocMasterById");
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
	
	// retrieve the scQtDocReport for Syarat-syarat Am/ Arahan/Syarat-syarat
		// Khas
		/**
		 * Retrive rules and regulation report.
		 * 
		 * @param scQtDocReport
		 *            the sc qt doc report
		 * @return ScQtDocReport
		 */
		private ScQtDocReport retriveRulesAndRegulationReport(
				ScQtDocReport scQtDocReport) {
//			debugLogger("retriveRulesAndRegulationReport");
			IQTDocumentSO qtDocumentSO = null;
			try {
				qtDocumentSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDocumentSO.class);

				QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
				reqDTO.setScReport(scQtDocReport);
				reqDTO.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
				reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_RULES_REGULATION);

				QTDocumentResDTO resDTO = qtDocumentSO
						.retriveScQtReportByDefKey(reqDTO);
				ScQtDocReport qtDocReport = (ScQtDocReport) resDTO.getScReport();
				return qtDocReport;
			} finally {
				qtDocumentSO = null;
			}
		}
	
		
	private ScQtContactPerson copyEmailPhoneAndFax(ScQtContactPerson origin,ScQtContactPerson copy){
		PmUser user = new FacesScopes().getUserSessionBean();
		
		if(origin.getPhoneNo()!=null){
			copy.setPhoneNo(origin.getPhoneNo());
		}else{
			if(user.getEmail().equals(origin.getEmail()) && user.getUserName().equals(origin.getName())){
			copy.setPhoneNo(user.getPhoneNo());
			}
		}
		if(origin.getEmail()!=null){
			copy.setEmail(origin.getEmail());
		}else{
			if(user.getEmail().equals(origin.getEmail()) && user.getUserName().equals(origin.getName())){
			copy.setEmail(user.getEmail());
			}
		}
		if(origin.getPhoneNo()!=null){
			copy.setFaxNo(origin.getFaxNo());
		}else{
			if(user.getEmail().equals(origin.getEmail()) && user.getUserName().equals(origin.getName())){
			copy.setFaxNo(user.getFaxNo());
			}
		}
		
		return copy;
	}
		
	private void retrievePublishing(ScQt copyScqt){
		ScQt qt = this.getManagedBean().getQt();
		IQTDetailSO detailSO=null;
		try{
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			// reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setQtdetail(copyScqt);
			QuationTenderResDTO resDTO = detailSO
					.retriveQTScheduleDetails(reqDTO);
			
			ScQt copyQt=reqDTO.getQtdetail();
			if(null==qt.getNewPublishDate()){//25635-Start
			qt.setNewPublishDate(copyQt.getPublishDate());
			}//25635-End
			qt.setNewClosingDate(copyQt.getNewClosingDate());
			
			ISearchResultBean<ScQtContactPerson> contactPersons = resDTO
					.getContactPersons();
			List<ScQtContactPerson> qtContactPersons = contactPersons
					.getResultList();
			
			//this.getManagedBean().setToBeRemovalContactPersonList(contactPersonList);
			QuationTenderReqDTO reqDTOS = new QuationTenderReqDTO();//33921
			// reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTOS.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO resDTO1 = detailSO
					.retriveQTScheduleDetails(reqDTOS);
			List<ScQtContactPerson> delcontactpersonList=new ArrayList<ScQtContactPerson>();
			if(CollectionUtils.isNotEmpty(resDTO1.getContactPersons().getResultList())){
				delcontactpersonList.addAll(resDTO1.getContactPersons().getResultList());
			}
			if(CollectionUtils.isNotEmpty(resDTO1.getDeskOfficers().getResultList())){
				delcontactpersonList.addAll(resDTO1.getDeskOfficers().getResultList());
			}//33921
			
			if (delcontactpersonList != null || !delcontactpersonList.isEmpty()) {//33921
				List<ScQtContactPerson> listDel=new ArrayList<ScQtContactPerson>();
				for(ScQtContactPerson p:delcontactpersonList){//33921
					p.setDeleted(true);
					listDel.add(p);
				}
				this.getManagedBean().setToBeRemovalContactPersonList(listDel);
			}
			if (qtContactPersons != null || !qtContactPersons.isEmpty()) {
				List<ScQtContactPerson> persons=new ArrayList<ScQtContactPerson>();
				PmUser user = new FacesScopes().getUserSessionBean();
//				ScQtContactPerson contactPerson = new ScQtContactPerson();
//				contactPerson.setPhoneNo(user.getPhoneNo());
//				contactPerson.setUser(user);
//				contactPerson.setEmail(user.getEmail());
//				contactPerson.setFaxNo(user.getFaxNo());
//				contactPerson
//						.setOfficerType(SourcingConstants.OFFICER_TYPE_QT);
//				contactPerson.setName(user.getUserName());
//				contactPerson.setScQt(this.getManagedBean().getQt());
//				persons.add(contactPerson);
				for(ScQtContactPerson origin:qtContactPersons){
//					boolean different=true;
//					if(user.getEmail().equals(origin.getEmail()) && user.getUserName().equals(origin.getName())){
//						different=false;
//					}
					//if(different){
						ScQtContactPerson copy=populateContact(origin,SourcingConstants.OFFICER_TYPE_QT);
						persons.add(copy);
					//}
				}
			   this.getManagedBean().setScQtContactPersons(persons);
			}
			List<ScQtContactPerson> desks=resDTO.getDeskOfficers().getResultList();
			this.getManagedBean().setPmuserContactpersonList(new ArrayList<ScQtContactPerson>());//25635
			if(!CollectionUtils.isEmpty(desks)){
				List<ScQtContactPerson> persons=new ArrayList<ScQtContactPerson>();
				for(ScQtContactPerson origin:desks){
					ScQtContactPerson copy=populateContact(origin,SourcingConstants.OFFICER_TYPE_DOAA);
					persons.add(copy);
				}
				this.getManagedBean().setPmuserContactpersonList(persons);
			}
			
	// according to Salleh, no need to generate DOA from request content.		
//			else{
//				ScQtRequestContent requestContent=null;
//				if(!CollectionUtils.isEmpty(copyQt.getRequestContents())){
//					for(ScQtRequestContent r: copyQt.getRequestContents()){
//						if(r.getRequestType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_DOA_ASSIGNMENT)){
//							requestContent = r;
//							break;
//						}
//					}
//				}
//				if(null!=requestContent){
//					this.getManagedBean().setPmuserContactpersonList(this.generateDOAFromRequestContent(requestContent, qt));
//				}
//			}
			
			
			
			
			QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
//			if (this.getManagedBean().getQt().isSampleReq()
//					&& (null == this.getManagedBean()
//							.getScQtSampleSubmission() && isEmptyOrNull(this
//							.getManagedBean().getScQtSampleSubmissionList()))) {
//				
//			}
			
			if(qt.isSampleReq()){
				if (!qt.isZonal()){
					ScQtSampleSubmission srcSub=resDTO.getSampleSubmissionResultBean().get(0);
					ScQtSampleSubmission existing=resDTO1.getSampleSubmissionResultBean().get(0);//33946
					if(null!=existing){
						setUserDetails(existing);
						existing.setScAddress(populateScAddress(srcSub.getScAddress()));
						this.getManagedBean().setScQtSampleSubmission(existing);
					}
					else{//33946
					if(null!=srcSub){
						ScQtSampleSubmission sbs=new ScQtSampleSubmission();
						sbs.setBrandNew(true);
						setUserDetails(sbs);
						sbs.setScQt(qt);
						sbs.setScAddress(populateScAddress(srcSub.getScAddress()));//33946
						this.getManagedBean().setScQtSampleSubmission(sbs);
					}
					}//33946
					
					if (null != this.getManagedBean()
							.getScQtSampleSubmission()) {//25635-start
						this.getManagedBean()
									.setScQtSampleSubmissionList(
											new ArrayList<ScQtSampleSubmission>());
						
						this.getManagedBean()
								.getScQtSampleSubmissionList()
								.add(this.getManagedBean()
										.getScQtSampleSubmission());
					}//25635-End
					
					
					if (!getManagedBean().isSubmit()) {
						backingBean.retrievePmStateList();
					}
				}else{//25635_start
					QuationTenderReqDTO reqDTO1 = new QuationTenderReqDTO();
					// reqDTO.setQtId(getManagedBean().getQt().getQtId());
					reqDTO1.setQtdetail(getManagedBean().getQt());
					QuationTenderResDTO res1DTO = detailSO
							.retriveQTScheduleDetails(reqDTO1);
					if(CollectionUtils.isEmpty(this
								.getManagedBean().getScQtSampleSubmissionList())){
					this.getManagedBean().setScQtSampleSubmissionList(
							res1DTO.getSampleSubmissionResultBean());
				}
				}//25635_End
			}
			
			
           this.getManagedBean().setDoaRequestEditable(Boolean.TRUE);//25635
			backingBean.getDeskOfficerAdminAssistant();	
			//
		}finally{
			detailSO=null;
		}
	}
	
	/*	
	private List<ScQtContactPerson> generateDOAFromRequestContent(ScQtRequestContent requestContent, ScQt qt) throws IOException{
		List<ScQtContactPerson> doaList = new ArrayList<ScQtContactPerson>();
		Object content = requestContent.getRequestContent() == null ? new HashMap<Long, String>(): requestContent.getRequestContent();
		@SuppressWarnings("unchecked")
		Map<Long, String> doaMap = (Map<Long, String>) content;
		for (Map.Entry<Long, String> map : doaMap.entrySet()) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(map.getKey());
			UserResDTO userResDTO = this.getUserSOGlobal()
					.retrieveUserByUserId(userReqDTO);
			PmUser user = userResDTO.getPmUser();
			if (user != null
					&& user.getLoginId().equalsIgnoreCase(
							map.getValue())) {
				ScQtContactPerson doa = new ScQtContactPerson();
				doa.setUserId(user.getUserId());
				doa.setBrandNew(false);
				doa.setScQt(qt);
				doa.setOfficerType(SourcingConstants.OFFICER_TYPE_DOAA);
				doa.setName(user.getUserName());
				doa.setUser(user);
				doa.setPhoneNo(user.getPhoneNo());
				doa.setFaxNo(user.getFaxNo());
				doa.setEmail(user.getEmail());
				doaList.add(doa);
			}
		}
		return doaList;
	}
	*/
	private ScQtContactPerson populateContact(ScQtContactPerson origin,String type){
		ScQtContactPerson contactPerson = new ScQtContactPerson();
		//PmUser user = new FacesScopes().getUserSessionBean();
		contactPerson=copyEmailPhoneAndFax(origin,contactPerson);
		contactPerson
				.setOfficerType(type);
		contactPerson.setUser(origin.getUser());
		contactPerson.setUserId(origin.getUserId());
//		if(SourcingConstants.OFFICER_TYPE_DOAA.equals(type)){
//			contactPerson.setUser(origin.getUser());
//		}else{
//			contactPerson.setUser(new PmUser());
//		}
		
		contactPerson.setName(origin.getName());
		contactPerson.setScQt(this.getManagedBean().getQt());
		return contactPerson;
	}
	
	private BriefingZoneBean updateBrief(ScQtBsv toUpdate,ScQtBsv qtBsv){
		toUpdate.setAllSiteVisitReq(qtBsv.isAllSiteVisitReq());
		toUpdate.setAllBriefingReq(qtBsv.isAllBriefingReq());
		toUpdate.setSameBsv(qtBsv.isSameBsv());
		toUpdate.setMinBriefingAttend(qtBsv.getMinBriefingAttend());
		toUpdate.setMinSiteVisitAttend(qtBsv.getMinSiteVisitAttend());
		List<ScQtBsvDtl> listBsv=new ArrayList<ScQtBsvDtl>();
		List<ScQtBsvDtl> listBrief=new ArrayList<ScQtBsvDtl>();
		List<ScQtBsvDtl> listSiteVisit=new ArrayList<ScQtBsvDtl>();
		if (null != qtBsv.getScQtBsvDtls()) {
			for (ScQtBsvDtl bsvDtl : qtBsv.getScQtBsvDtls()) {
				ScQtBsvDtl copydtl=new ScQtBsvDtl();
				copydtl.setScQtBsv(toUpdate);
				copydtl.setBsvStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));//34347&25635-Start
				copydtl.setBsvStatusId(QTStatusConst.BSV_PENDING);//34347&25635-End
				copydtl.setLocation(bsvDtl.getLocation());
				copydtl.setBsvType(bsvDtl.getBsvType());
				copydtl.setBsvDesc(bsvDtl.getBsvDesc());
				copydtl.setBsvDate(bsvDtl.getBsvDate());
				copydtl.setAddress(populateScAddress(bsvDtl.getAddress()));//31653&25635-start
				copydtl.setCoordinatingOfficer(bsvDtl.getCoordinatingOfficer());
				copydtl.setMaxNoOfAttendance(bsvDtl.getMaxNoOfAttendance());
				if(SourcingConstants.BSV_TYPE_SITEVISIT.equals(bsvDtl.getBsvType())){
					listSiteVisit.add(copydtl);
				}else{
					listBrief.add(copydtl);
				}
				
				
			}
			listBsv.addAll(listSiteVisit);
			listBsv.addAll(listBrief);
			if(null!=toUpdate.getScQtBsvDtls()){//25635-start
			toUpdate.getScQtBsvDtls().clear();
			}//25635-End
			toUpdate.setScQtBsvDtls(listBsv);
		}
			
//		toUpdate.setBsvRemarks(initializeRemarks(toUpdate, this
//				.getManagedBean().getQt()
//				.getCurrentWorkFlowStatus(), toUpdate.getBsvRemarks(),
//				ScQtBSVRemark.class));
		
		BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
				null, listBrief,
				listSiteVisit, toUpdate);
		
		
		return briefingZoneBean;
		
		
		
	}
	
	private BriefingZoneBean mapBrief(ScQt qt,ScQtBsv qtBsv){
		ScQtBsv copy=new ScQtBsv();
		copy.setScQt(qt);
		copy.setBrandNew(true);
		copy.setAllSiteVisitReq(qtBsv.isAllSiteVisitReq());
		copy.setAllBriefingReq(qtBsv.isAllBriefingReq());
		copy.setSameBsv(qtBsv.isSameBsv());
		copy.setMinBriefingAttend(qtBsv.getMinBriefingAttend());
		copy.setMinSiteVisitAttend(qtBsv.getMinSiteVisitAttend());
		List<ScQtBsvDtl> listBsv=new ArrayList<ScQtBsvDtl>();
		List<ScQtBsvDtl> listBrief=new ArrayList<ScQtBsvDtl>();
		List<ScQtBsvDtl> listSiteVisit=new ArrayList<ScQtBsvDtl>();
		if (null != qtBsv.getScQtBsvDtls()) {
			for (ScQtBsvDtl bsvDtl : qtBsv.getScQtBsvDtls()) {
				ScQtBsvDtl copydtl=new ScQtBsvDtl();
				copydtl.setScQtBsv(copy);
				copydtl.setBsvStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));//34347&25635-Start
				copydtl.setBsvStatusId(QTStatusConst.BSV_PENDING);//34347&25635-End
				copydtl.setLocation(bsvDtl.getLocation());
				copydtl.setBsvType(bsvDtl.getBsvType());
				copydtl.setBsvType(bsvDtl.getBsvType());
				copydtl.setBsvDesc(bsvDtl.getBsvDesc());
				copydtl.setBsvDate(bsvDtl.getBsvDate());
				copydtl.setAddress(populateScAddress(bsvDtl.getAddress()));//31653&25635-start
				copydtl.setCoordinatingOfficer(bsvDtl.getCoordinatingOfficer());
				copydtl.setMaxNoOfAttendance(bsvDtl.getMaxNoOfAttendance());
				if(SourcingConstants.BSV_TYPE_SITEVISIT.equals(bsvDtl.getBsvType())){
					listSiteVisit.add(copydtl);
				}else{
					listBrief.add(copydtl);
				}
				
				
			}
			listBsv.addAll(listSiteVisit);
			listBsv.addAll(listBrief);
			
			copy.setScQtBsvDtls(listBsv);
			
		}
		
//		copy.setBsvRemarks(initializeRemarks(copy, this
//				.getManagedBean().getQt()
//				.getCurrentWorkFlowStatus(), copy.getBsvRemarks(),
//				ScQtBSVRemark.class));
		
		BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
				null, listBrief,
				listSiteVisit, copy);
		
		
		return briefingZoneBean;
	}
	
	private void retrieveBrief(ScQt copyScqt){
		ScQt qt = this.getManagedBean().getQt();
		IQTBriefingSO qTBriefingSO = null;
		try {
			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setScQt(copyScqt);
			reqDTO.setLanguageCode(languageCode);
			reqDTO.setMaintanceFlow(true);
			BriefingResDTO resDTO = qTBriefingSO
					.retrieveBriefingSiteVisitDtl(reqDTO);
			
			Long currentStatus = this.getManagedBean().getQt()
					.getQtStatus().getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);
			
			if(!qt.isZonal()){
				if (!copyScqt.isZonal()) {
					qt.setBsvRequired(copyScqt.isBsvRequired());//25635-start
					//qt.setPublishDate(null);//25635-End
					ScQtBsv qtBsv = resDTO.getScQtBsv();
					BriefingZoneBean briefingZoneBean=null;
					if(qt.isBsvRequired()){
						BriefingReqDTO currreqDTO = new BriefingReqDTO();//33954&25635-start
						currreqDTO.setScQt(qt);
						currreqDTO.setLanguageCode(languageCode);
						currreqDTO.setMaintanceFlow(true);
						BriefingResDTO CurrresDTO = qTBriefingSO
								.retrieveBriefingSiteVisitDtl(currreqDTO);	
						 ScQtBsv CurrqtBsv = CurrresDTO.getScQtBsv();	
						if(!CurrqtBsv.isBrandNew()&& null!=CurrqtBsv.getQtBsvId()){						
							ScQtBsv ori=CurrqtBsv;//33954&25635-end
							briefingZoneBean=updateBrief(ori,qtBsv);
						}else{//25635-start
							
							ScQtBsv ori=null;
							if(null==this.getManagedBean().getBsvDtlBean()){
							
							 ori=new ScQtBsv();
							ori.setScQt(qt);
							ori.setTplSpecZoneDtl(null);
							}else{
								
								ori=this.getManagedBean().getBsvDtlBean().getScQtBsv();	
							}
							briefingZoneBean=updateBrief(ori,qtBsv);	
						}//25635-End
					}else{
						qt.setBsvRequired(copyScqt.isBsvRequired());
						briefingZoneBean=mapBrief(qt,qtBsv);
					}
					this.getManagedBean().setBsvDtlBean(briefingZoneBean);
					BriefingZoneBean bsvDtlBean = getManagedBean().getBsvDtlBean();//25635-start
					Date latestBsvDate = null;
					if (bsvDtlBean != null) {
						latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getBriefingList());
						latestBsvDate = updateProposalStartDate(latestBsvDate, bsvDtlBean.getSitevisitList());
					} 
					if(null!=copyScqt.getPublishDate()){//25635-Start
						qt.setNewPublishDate(copyScqt.getPublishDate());
						}
					Date newPublishDate = qt.getNewPublishDate();
					
					if (newPublishDate != null) {	
						qt.setPublishDate(DateUtils.setDefaultDateTime(newPublishDate));
						
					}//25635-End
					
					// if no briefing / site visit.
					if (!qt.isBsvRequired()) {
						latestBsvDate = qt.getPublishDate();
					} 
					
					qt.setProposalStartDate(latestBsvDate);//25635-End
					updatePublishClosingDate(qt);//25635
				}else{
					
//					List<TplSpecZoneDtl> zones = resDTO.getZonesList();
//					List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
//					if(null!=zones){
//						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
//						briefingReqDTO.setQtId(copyScqt
//								.getQtId());
//						briefingReqDTO.setScQt(getManagedBean().getQt());
//						briefingReqDTO.setMaintanceFlow(true);
//						//briefingReqDTO.setAmendMentBean(amendMentBean);
//						for (TplSpecZoneDtl specZoneDtl : zones) {
//							briefingReqDTO.setSpecZoneId(specZoneDtl
//									.getSpecZoneId());
//							BriefingResDTO briefingResDTO = qTBriefingSO
//									.retrieveBriefingSiteVisitDtls(briefingReqDTO);
//							ScQtBsv qtBsv = briefingResDTO.getScQtBsv();
//							
//							BriefingZoneBean briefingZoneBean=mapBrief(qt,qtBsv);
//							zoneBeans.add(briefingZoneBean);
//						}
//						
//					}
//					this.getManagedBean().setBriefingZoneBean(zoneBeans);
//					this.getManagedBean().setBsvDtlBean(zoneBeans.get(0));
					updatePublishingdates(qt,copyScqt);//25635
				}			
			}
			else{//25635-start			
				updatePublishingdates(qt,copyScqt);
			}//25635-End

			
		}finally{
			qTBriefingSO = null;
		}
	}
	
	
	private ScQtApproval retrievePublicApprovalByQt(ScQt copyQt){
		IQTDetailSO detailSO;
		ScQtApproval app=null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(copyQt.getQtId());
			reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_PTJ);
			QuationTenderResDTO resDTO = detailSO
					.retrieveApprovalDtlsByQtIdNType(reqDTO);
			app = resDTO.getQtApproval();
		}finally{
			detailSO=null;
		}
		
		return app;
	}
	public void retrievePublicationApprovalTabDtl(ScQt copyQt) {
		ScQt scQt = this.getManagedBean().getQt();
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(copyQt.getQtId());
			reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_PTJ);
			QuationTenderResDTO resDTO = detailSO
					.retrieveApprovalDtlsByQtIdNType(reqDTO);
			ScQtApproval copyApproval = resDTO.getQtApproval();
			
			ScQtApproval prevApp =retrievePublicApprovalByQt(scQt);
			List<ScQtApproval> toBeApprovalRemoveList = new ArrayList<ScQtApproval>();
			/*25635-start if(prevApp!=null){
				prevApp.setDeleted(true);
				toBeApprovalRemoveList.add(prevApp);
			}25635-end */
			this.getManagedBean().setToBeApprovalRemoveList(toBeApprovalRemoveList);
			
			if(copyApproval!=null){
				ScQtApproval publicationApproval =null;//25635-start
				if(null!=prevApp){
				publicationApproval=prevApp;	
				}else{
				 publicationApproval = new ScQtApproval();
				 publicationApproval.setScQt(scQt);
					publicationApproval.setApprovalCategory(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_CATEGORY, 
							ParameterConst.APPROVAL_CATEGORY_PTJ));
					publicationApproval.setBrandNew(true);
					publicationApproval.setRecordStatusFlg(copyApproval.getRecordStatusFlg());
					publicationApproval.setRecordStatus(copyApproval.isRecordStatus());
					setUserDetails(publicationApproval);
				}//25635-End
				publicationApproval
						.setScQtApprovalRemarks(initializeRemarks(
								publicationApproval, scQt
										.getCurrentWorkFlowStatus(),
								publicationApproval
										.getScQtApprovalRemarks(),
								ScQtApprovalRemark.class));
				if(copyApproval.getApproverSettings()!=null && !copyApproval.getApproverSettings().isEmpty()){
					List<ScQtApproverSettings> approverSettings=new ArrayList<ScQtApproverSettings>();
					for(ScQtApproverSettings setting:copyApproval.getApproverSettings()){
						ScQtApproverSettings aps=new ScQtApproverSettings();
						aps.setBrandNew(true);
						setUserDetails(aps);
						aps.setCommitteeType(setting.getCommitteeType());
						aps.setDocType(setting.getDocType());
						aps.setPmUser(setting.getPmUser());
						aps.setRecordStatus(setting.isRecordStatus());
						aps.setRecordStatusFlg(setting.getRecordStatusFlg());
						aps.setScQtApproval(publicationApproval);
						aps.setSelectable(setting.isSelectable());
						aps.setSelected(setting.isSelected());
						aps.setUserSequence(setting.getUserSequence());
						approverSettings.add(aps);
					}
					publicationApproval.setApproverSettings(approverSettings);
				}
				
				if(copyApproval.getScQtApprovalAttachments()!=null && !copyApproval.getScQtApprovalAttachments().isEmpty()){
					List<ScQtApprovalAttachment> approverAttachments=new ArrayList<ScQtApprovalAttachment>();
					for(ScQtApprovalAttachment attachment:copyApproval.getScQtApprovalAttachments()){
						ScQtApprovalAttachment att=new ScQtApprovalAttachment();
						setUserDetails(att);
						att.setBrandNew(true);
						att.setDocNo(scQt.getQtNo());
						att.setDocType(attachment.getDocType());
						att.setScQtApproval(publicationApproval);
						try {
							uploadAttachment(att,attachment);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						approverAttachments.add(att);
					}
					publicationApproval.setScQtApprovalAttachments(approverAttachments);
				}
				if(CollectionUtils.isEmpty(publicationApproval.getScQtApprovalDtls())){//25635-start
				if(copyApproval.getScQtApprovalDtls()!=null && !copyApproval.getScQtApprovalDtls().isEmpty()){
					List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
					for(ScQtApprovalRequest request:copyApproval.getScQtApprovalDtls()){
						ScQtApprovalRequest req=new ScQtApprovalRequest();
						req.setBrandNew(true);
						setUserDetails(req);
						req.setRecordStatus(request.isRecordStatus());
						req.setRecordStatusFlg(request.getRecordStatusFlg());
						req.setApprovalType(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, 
								ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL));
						req.setScQtApproval(publicationApproval);
						req.setSelectable(request.isSelectable());
						req.setSelected(request.isSelected());
						approverRequest.add(req);
					}
					publicationApproval.setScQtApprovalDtls(approverRequest);
				}
				}//25635-End
				
				this.getManagedBean().setPublicationApproval(
						publicationApproval);
				
				Long[] orgIds={scQt.getOwnerOrgProfile().getOrgProfileId(),scQt.getPmOrgProfile().getOrgProfileId()};
				dualListModel = ApproverSettings.getDetails(
						RoleCode.PUBLICATION_APPROVER,orgIds, publicationApproval);
				this.getManagedBean().setPublicationApprovals(dualListModel);
				
			}
		}finally{
			detailSO=null;
		}
	}
	
	private ScQt retrieveApproval(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		copyScqt=retrieveScqtCommittee(copyScqt);
		tenderReqDTO.setQtdetail(copyScqt);
		tenderReqDTO.setAvaliableApprovals(copyScqt.getScQtApprovals());
		tenderReqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
		tenderReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
	    //QuationTenderResDTO resDTO = QtDetailsSO.retriveQTApprovalDetails(tenderReqDTO);//34332&25635_start
		List<ScQtApproval> requiredApproval = copyScqt.getScQtApprovals();//34332&25635_end
		List<ScQtApproval> approvals=new ArrayList<ScQtApproval>();
		List<ScQtApproval> newApprovals=new ArrayList<ScQtApproval>();
		//25635 & 34502 - Start
		List<TabBean> tabBeans = new ArrayList<TabBean>();
		List<ScQtApproval> otherapprovals=new ArrayList<>();//25635 & 34502 
		this.getManagedBean().setScQtApprovalReqAttachementICT(null);//34502&25635-start
		this.getManagedBean().setScQtAppRequestRemarkICT(StringUtils.EMPTY); // #34694,#25077
		this. getManagedBean().setPmParam(null);
		scQt.setCloneEnabled(Boolean.FALSE);//34502&25635-End
		//25635 & 34502 - End
		if(null!=requiredApproval && CollectionUtils.isNotEmpty(requiredApproval)){//25635-start
		for(ScQtApproval approval:requiredApproval){
			if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
					.equalsIgnoreCase(approval.getApprovalCategory()
							.getParameterCode())
				) {//25635 & 34502 
				
				approvals.add(approval);
				
				} 
				// 25635 & 34502&35282 -start-
				else if (ParameterConst.APPROVAL_CATEGORY_OTHERS.equalsIgnoreCase(approval.getApprovalCategory()
						.getParameterCode())
						&& ParameterConst.PROCUREMENT_CATEGORY_ICT.equals(scQt.getPmProcurementCategory()
								.getParameterCode()))
				// 25635 & 34502&35282 -end-
				{
					// 25635 & 34502 -start-
					if (CollectionUtils.isEmpty(otherapprovals)) {
						otherapprovals.add(approval);
					}
					// 25635 & 34502 -end-
				} //25635 & 34502 
		}
		}//25635-End
		
		for(ScQtApproval copyApproval:approvals){
			ScQtApproval newApproval= null;//25635&31484-start
			 if(!scQt.isBrandNew()){//25635&31484-start
				if(null!=scQt.getScQtApprovals() && CollectionUtils.isNotEmpty(scQt.getScQtApprovals())){
					for(ScQtApproval approval:scQt.getScQtApprovals()){
						if (!approval.isBrandNew() && ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
								.equalsIgnoreCase(approval.getApprovalCategory()
										.getParameterCode())) {
							newApproval=approval;
						}
					}
				}
			 }
			 if(null==newApproval){
			newApproval= new ScQtApproval();
			newApproval.setBrandNew(true);
			newApproval.setApprovalCategory(copyApproval.getApprovalCategory());
			newApproval.setRecordStatusFlg(copyApproval.getRecordStatusFlg());
			newApproval.setRecordStatus(copyApproval.isRecordStatus());
			newApproval.setScQt(scQt);
			setUserDetails(newApproval);
			 }//25635&31484-End
			if(copyApproval.getApproverSettings()!=null && !copyApproval.getApproverSettings().isEmpty()){
				List<ScQtApproverSettings> approverSettings=new ArrayList<ScQtApproverSettings>();
				for(ScQtApproverSettings setting:copyApproval.getApproverSettings()){
					ScQtApproverSettings aps=new ScQtApproverSettings();
					aps.setBrandNew(true);
					setUserDetails(aps);
					aps.setCommitteeType(setting.getCommitteeType());
					aps.setDocType(setting.getDocType());
					aps.setPmUser(setting.getPmUser());
					aps.setRecordStatus(setting.isRecordStatus());
					aps.setRecordStatusFlg(setting.getRecordStatusFlg());
					aps.setScQtApproval(newApproval);
					aps.setSelectable(setting.isSelectable());
					aps.setSelected(setting.isSelected());
					aps.setUserSequence(setting.getUserSequence());
					approverSettings.add(aps);
				}
				newApproval.setApproverSettings(approverSettings);
			}
			
			if(copyApproval.getScQtApprovalAttachments()!=null && !copyApproval.getScQtApprovalAttachments().isEmpty()){
				List<ScQtApprovalAttachment> approverAttachments=new ArrayList<ScQtApprovalAttachment>();
				for(ScQtApprovalAttachment attachment:copyApproval.getScQtApprovalAttachments()){
					ScQtApprovalAttachment att=new ScQtApprovalAttachment();
					setUserDetails(att);
					att.setBrandNew(true);
					att.setDocNo(scQt.getQtNo());
					att.setDocType(attachment.getDocType());
					att.setScQtApproval(newApproval);
					try {
						uploadAttachment(att,attachment);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					approverAttachments.add(att);
				}
				newApproval.setScQtApprovalAttachments(approverAttachments);
			}
			
			if(scQt.isBrandNew() & copyApproval.getScQtApprovalDtls()!=null && !copyApproval.getScQtApprovalDtls().isEmpty()){//25635&31484-start
				List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
				for(ScQtApprovalRequest request:copyApproval.getScQtApprovalDtls()){
					if(null!=request.getApprovalType() && null!=request.getApprovalType().getParameterId() && ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.equalsIgnoreCase(request.getApprovalType().getParameterCode())){//25635&31484-start
					ScQtApprovalRequest req=new ScQtApprovalRequest();
					req.setBrandNew(true);
					setUserDetails(req);
					req.setRecordStatus(request.isRecordStatus());
					req.setRecordStatusFlg(request.getRecordStatusFlg());
					req.setApprovalType(request.getApprovalType());
					req.setScQtApproval(newApproval);
					req.setSelectable(request.isSelectable());
					req.setSelected(request.isSelected());
					approverRequest.add(req);
					}//25635&31484-End
				}
				newApproval.setScQtApprovalDtls(approverRequest);
			}else{//25635&31484-start	
//				log.info("=======elsenewCloneCodesexecuted=========");
				if(null==newApproval.getScQtApprovalDtls() || CollectionUtils.isEmpty(newApproval.getScQtApprovalDtls())){
			
				List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
				ScQtApprovalRequest req=new ScQtApprovalRequest();
				req.setBrandNew(true);
				setUserDetails(req);
				ParameterHelper parameterHelper = ParameterHelper.getInstance();
			PmParameter parameter=	parameterHelper.getParameterByTypeAndCode(
					ParameterConst.COMMITTE_TYPE,
						ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
				req.setApprovalType(parameter);
				req.setScQtApproval(newApproval);
				approverRequest.add(req);
				newApproval.setScQtApprovalDtls(approverRequest);
				}
			}//25635&31484-start
			List<ScQtApprovalRemark> existedRemark=new ArrayList<>();//34466&25635-start
			if(!newApproval.isBrandNew() && CollectionUtils.isNotEmpty(newApproval
							.getScQtApprovalRemarks())){
				for(ScQtApprovalRemark committeeApproval:newApproval
							.getScQtApprovalRemarks()){
					if(!committeeApproval.isBrandNew()){		
						existedRemark.add(committeeApproval);
					}
				}
				
			}//34466&25635-end
			newApproval
			.setScQtApprovalRemarks(initializeRemarks(
					newApproval, scQt
							.getCurrentWorkFlowStatus(),
							existedRemark,
					ScQtApprovalRemark.class));//34342&25635&34466
			
			//25635 & 34502 - Start
			if(newApproval.getApprovalCategory().getParameterCode().equals(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT)){
			//25635 & 34502 - End
			
				ParameterHelper parameterHelper = ParameterHelper.getInstance();
				String approvalCategoryCode = newApproval
						.getApprovalCategory().getParameterCode();
				String titleName = parameterHelper.getParameterDesc(
						ParameterTypeConst.APPROVAL_CATEGORY,
						approvalCategoryCode);
				Long[] orgIds={scQt.getOwnerOrgProfile().getOrgProfileId(),scQt.getPmOrgProfile().getOrgProfileId()};
				DualListModel<ScQtApproverSettings> budgetApprovalUsers=ApproverSettings.getDetails(
						RoleCode.COMMITTEE_APPROVER,orgIds,
						newApproval);
				tabBeans.add(new TabBean(titleName,
						approvalCategoryCode, newApproval,budgetApprovalUsers));
						
				newApprovals.add(newApproval);
				
			//25635 & 34502 - Start
			}
			//25635 & 34502 - End
		}
		
		//25635 & 34502 - Start
		for(ScQtApproval copyOthersApproval:otherapprovals){
			ScQtApproval newOthersApproval= null;
			scQt.setCloneEnabled(Boolean.TRUE);//34502&25635-end
			this.getManagedBean().setApproverTabLoaded(Boolean.TRUE);//34502&25635-end
			 if(!scQt.isBrandNew()){
				if(null!=scQt.getScQtApprovals() && CollectionUtils.isNotEmpty(scQt.getScQtApprovals())){
					for(ScQtApproval approval:scQt.getScQtApprovals()){
						if (!approval.isBrandNew() && ParameterConst.APPROVAL_CATEGORY_OTHERS
								.equalsIgnoreCase(approval.getApprovalCategory()
										.getParameterCode())) {
							newOthersApproval=approval;
						}
					}
				}
			 }
			 if(null==newOthersApproval){
				newOthersApproval= new ScQtApproval();
				newOthersApproval.setBrandNew(true);
				newOthersApproval.setApprovalCategory(copyOthersApproval.getApprovalCategory());
				newOthersApproval.setRecordStatusFlg(copyOthersApproval.getRecordStatusFlg());
				newOthersApproval.setRecordStatus(copyOthersApproval.isRecordStatus());
				newOthersApproval.setScQt(scQt);
			 }
			
			 if(copyOthersApproval.getScQtApprovalDtls()!=null && !copyOthersApproval.getScQtApprovalDtls().isEmpty()){//25635 & 34502
				List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
				for(ScQtApprovalRequest approvalRequest:copyOthersApproval.getScQtApprovalDtls()){
						
					if(null!=approvalRequest.getApprovalType() && null!=approvalRequest.getApprovalType().getParameterId() &&
							approvalRequest.getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)){
						
						    ScQtApprovalRequest req=new ScQtApprovalRequest();
							req.setBrandNew(true);
							req.setApprovalType(approvalRequest.getApprovalType());
							req.setApproverAction(new PmParameter());
							if(null!=approvalRequest.getApproverAction()){//34502&25635&43-start
							req.setApproverAction(approvalRequest.getApproverAction());//25635 & 34502
							this. getManagedBean().setPmParam(req.getApproverAction());//34502&25635
							}//34502&25635&43-End
							req.setApprovalRequestAttachement(null);//25635 & 34502
							ScQtApprovalReqAttachement approvalReqAttachement = 	req.getApprovalRequestAttachement();//25635 & 34502
							if (null == approvalReqAttachement) {
								approvalReqAttachement = new ScQtApprovalReqAttachement();
								approvalReqAttachement.setApprovalRequest(req);
							}
							approvalReqAttachement.setDocNo(scQt.getQtNo());
							try {
								if(null!=approvalRequest.getApprovalRequestAttachement()){//34502&25635&43-start
								uploadAttachment(approvalReqAttachement,approvalRequest.getApprovalRequestAttachement());
								req.setApprovalRequestAttachement(approvalReqAttachement);//25635 & 34502
								this.getManagedBean().setScQtApprovalReqAttachementICT(req.getApprovalRequestAttachement());//34502&25635
								}//34502&25635&43-End
							} catch (IOException e) {
								SLAUtils.getInstance().logSLAError("QT", "ICTAPProvalATTAchemnt", "docNo", "ICTAPProvalATTAchemntClone", new FacesScopes().getUserSessionBean().getUserId(), e);
								
							}
							ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
							approvalRemark.setApprovalRequest(req);//25635 & 34502
							req.setAppRequestRemark(approvalRemark);

						// 34502&25635&43-start
						if (null != approvalRequest.getAppRequestRemark()) {
							req.getAppRequestRemark().setRemark(approvalRequest.getAppRequestRemark().getRemark());

							// #34694,#25077 -start-
							this.getManagedBean().setScQtAppRequestRemarkICT(req.getAppRequestRemark().getRemark());
							// #34694,#25077 -end-
						}
						// 34502&25635&43-End

							req.setScQtApproval(newOthersApproval);
							approverRequest.add(req);
									
					}
					if(!newOthersApproval.isBrandNew()){//25635 & 34502
						List<ScQtApprovalRequest> delRequest=new ArrayList<>();
						if(CollectionUtils.isNotEmpty(newOthersApproval.getScQtApprovalDtls())){
							for(ScQtApprovalRequest  delApprequest:newOthersApproval.getScQtApprovalDtls()){
								if(!delApprequest.isBrandNew()){
									delApprequest.setDeleted(Boolean.TRUE);
									delRequest.add(delApprequest);	
								}
							}
							newOthersApproval.setToBeDeltedApprovRequests(delRequest);
							
						}
						
					}//25635 & 34502
					newOthersApproval.setScQtApprovalDtls(approverRequest);
				}
			 }
			
		    if(newOthersApproval.getApprovalCategory().getParameterCode().equals(ParameterConst.APPROVAL_CATEGORY_OTHERS)){
		    	List<PmParameter> settings = null;
				ParameterHelper parameterHelper = ParameterHelper.getInstance();
				
				// 35282-start
				if (CollectionUtils.isEmpty(newOthersApproval.getScQtApprovalDtls())) {
					List<ScQtApprovalRequest> approverRequest = new ArrayList<>();

					ScQtApprovalRequest req = new ScQtApprovalRequest();
					req.setBrandNew(true);
					req.setApprovalType(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES,
							ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS));
					req.setApproverAction(new PmParameter());
					req.setApprovalRequestAttachement(null);

					ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
					approvalRemark.setApprovalRequest(req);

					req.setAppRequestRemark(approvalRemark);
					req.setScQtApproval(newOthersApproval);

					approverRequest.add(req);

					newOthersApproval.setScQtApprovalDtls(approverRequest);
				}
				// 35282-End
				
				String approvalCategoryCode = newOthersApproval
						.getApprovalCategory().getParameterCode();
				String titleName = parameterHelper.getParameterDesc(
						ParameterTypeConst.APPROVAL_CATEGORY,
						approvalCategoryCode);
			
				tabBeans.add(new TabBean(titleName,
						approvalCategoryCode, newOthersApproval,settings));
				
				newApprovals.add(newOthersApproval);
		    }
		}
		Long status = copyScqt.getQtStatus().getStatusId();//34502&25635-start
		if (QTStatusConst.QT_CANCELLED.equals(status) && ParameterConst.PROCUREMENT_CATEGORY_ICT.equals(scQt
				.getPmProcurementCategory().getParameterCode())
				&& CollectionUtils.isEmpty(otherapprovals)) {
			ScQtApproval newOthersApproval = null;
			if(CollectionUtils.isNotEmpty(scQt.getScQtApprovals())){//34502&25635&43-start
					for (ScQtApproval approval : scQt.getScQtApprovals()) {
						if (ParameterConst.APPROVAL_CATEGORY_OTHERS
								.equalsIgnoreCase(approval
										.getApprovalCategory()
										.getParameterCode())) {
							newOthersApproval = approval;
							break;
						}
					}
			}//34502&25635&43-End
					if (null != newOthersApproval) {
						List<PmParameter> settings = null;
						ParameterHelper parameterHelper = ParameterHelper
								.getInstance();

						String approvalCategoryCode = newOthersApproval
								.getApprovalCategory().getParameterCode();
						String titleName = parameterHelper.getParameterDesc(
								ParameterTypeConst.APPROVAL_CATEGORY,
								approvalCategoryCode);

						tabBeans.add(new TabBean(titleName,
								approvalCategoryCode, newOthersApproval,
								settings));

						newApprovals.add(newOthersApproval);
					} else {

						List<ScQtApprovalRequest> approverRequest = new ArrayList<>();
						newOthersApproval = new ScQtApproval();
						newOthersApproval.setBrandNew(true);
						newOthersApproval
								.setApprovalCategory(getParameterByTypeAndCode(
										ParameterTypeConst.APPROVAL_CATEGORY,
										ParameterConst.APPROVAL_CATEGORY_OTHERS));
						newOthersApproval.setScQt(scQt);
						setUserDetails(newOthersApproval);
						ScQtApprovalRequest req = new ScQtApprovalRequest();
						req.setBrandNew(true);
						req.setApprovalType(getParameterByTypeAndCode(
								ParameterTypeConst.APPROVAL_REQUEST_TYPES,
								ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS));
						req.setApproverAction(new PmParameter());
						req.setApprovalRequestAttachement(null);
						ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
						approvalRemark.setApprovalRequest(req);
						req.setAppRequestRemark(approvalRemark);
						req.setScQtApproval(newOthersApproval);
						approverRequest.add(req);
						newOthersApproval.setScQtApprovalDtls(approverRequest);
						List<PmParameter> settings = null;
						ParameterHelper parameterHelper = ParameterHelper
								.getInstance();

						String approvalCategoryCode = newOthersApproval
								.getApprovalCategory().getParameterCode();
						String titleName = parameterHelper.getParameterDesc(
								ParameterTypeConst.APPROVAL_CATEGORY,
								approvalCategoryCode);
						tabBeans.add(new TabBean(titleName,
								approvalCategoryCode, newOthersApproval,
								settings));

						newApprovals.add(newOthersApproval);
					}
				}//34502&25635-End
		
		if (CollectionUtils.isNotEmpty(tabBeans)) {
			int selectedTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
			if (selectedTabIndex > tabBeans.size()-1) {
				selectedTabIndex = 0;
			}
			this.getManagedBean().setSelectedApprovalTabBean(
					tabBeans.get(selectedTabIndex));
			this.getManagedBean().setScQtApproval(
					this.getManagedBean().getSelectedApprovalTabBean()
							.getScQtApproval());
		}
		this.getManagedBean().setTabList(tabBeans);
		//25635 & 34502 - End
		
		scQt.setScQtApprovals(newApprovals);
		
		
			
		return scQt;
	}
	
	public void retrieveDeductionPaymentTabDtl(ScQt scQt,ScQt copyScqt,IQTDetailSO detailSO) {
		
		
//		debugLogger("retrieveDeductionPaymentTabDtl");
	//	if (this.getManagedBean().getScQtDeductionList() == null || this.getManagedBean().getScQtDeductionList().isEmpty()) {
			
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(copyScqt.getQtId());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> copyList = resDTO
						.getDeductionResultBean().getResultList();
				
				List<ScQtDeduction> deductionList = new ArrayList<ScQtDeduction>();
				
				for(ScQtDeduction ded:copyList){
					ScQtDeduction obj=new ScQtDeduction();
					obj.setBrandNew(true);
					obj.setDeductionAmount(ded.getDeductionAmount());
					obj.setDeductionDesc(ded.getDeductionDesc());
					obj.setDeductionType(ded.getDeductionType());
					//obj.setScLoa(ded.getScLoa());
					//obj.setScLoi(ded.getScLoi());
					obj.setScQt(scQt);
					List<ScQtDeductionClause> clauses=new ArrayList<ScQtDeductionClause>();
					for(ScQtDeductionClause cloneClause:ded.getScQtDeductionClauses()){
						ScQtDeductionClause newClause= new ScQtDeductionClause();
						newClause.setBrandNew(true);
						setUserDetails(newClause);
						newClause.setScQtDeduction(obj);
						newClause.setClauseDesc(cloneClause.getClauseDesc());
						newClause.setClauseDescRendered(newClause.getClauseDescRendered());
						clauses.add(newClause);
					}
					obj.setScQtDeductionClauses(clauses);
					
					setUserDetails(obj);
					deductionList.add(obj);
				}
				
				
				reqDTO.setQtId(scQt.getQtId());
				resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> toDelete =resDTO
				.getDeductionResultBean().getResultList();
				if(toDelete!=null && !toDelete.isEmpty()){
					for(ScQtDeduction del:toDelete){
						del.setDeleted(true);
					}
					this.getManagedBean().setRemoveDeductions(toDelete);
				}
				
				this.getManagedBean().setScQtDeductionList(deductionList);
				this.getManagedBean().getQt().setScQtDeductions(deductionList);
				getManagedBean().setSelectedScQtDeduction(null);
				/*
				 * if (null != deductionList && !deductionList.isEmpty()) {
				 * this.
				 * getManagedBean().setSelectedScQtDeduction(deductionList.get
				 * (0)); this.getManagedBean().getSelectedScQtDeduction().
				 * setScQtDeductionClauses
				 * (deductionList.get(0).getScQtDeductionClauses()); }
				 */
			} finally {
				detailSO = null;
			}
		//}
	}
	
	private void uploadAttachment(ScAttachment copy, ScAttachment origin)
			throws FileNotFoundException, IOException {
		String fileName = origin.getFileName();
		File file = new File(getActualFilePath(origin));
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		// byte[] fileBytes = null;//file.getContents();
		FileOutputStream fos = null;
		try {
			if (fileBytes != null) {
				copy.setFileName(fileName);
				copy.setFileDesc("File uploded");
				populateAttachmentDetails(copy);
				fos = new FileOutputStream(getActualFilePath(copy));
				fos.write(fileBytes);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	private void retrieveApproval(List<ScQtApproval> requiredApproval,ScQt copyScqt){
		if (requiredApproval != null) {
			List<TabBean> tabBeans = new ArrayList<TabBean>();
			ParameterHelper parameterHelper = ParameterHelper.getInstance();
			for (ScQtApproval qtApproval : requiredApproval) {
				String approvalCategoryCode = qtApproval
						.getApprovalCategory().getParameterCode();
				String titleName = parameterHelper.getParameterDesc(
						ParameterTypeConst.APPROVAL_CATEGORY,
						approvalCategoryCode);
				
				ScQt qt=qtApproval.getScQt();
				 qtApproval.setScQtApprovalRemarks(initializeRemarks(
							qtApproval, qt.getCurrentWorkFlowStatus(),
							qtApproval.getScQtApprovalRemarks(),
							ScQtApprovalRemark.class));
				 
				 
				 tabBeans.add(new TabBean(titleName,
							approvalCategoryCode, qtApproval,
							approverSettingDtls(RoleCode.COMMITTEE_APPROVER, copyScqt
											.getPmOrgProfile()
											.getOrgProfileId(),
									qtApproval)));
				 
				 if (CollectionUtils.isNotEmpty(tabBeans)) {
						int selectedTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
						if (selectedTabIndex > tabBeans.size()-1) {
							selectedTabIndex = 0;
						}
						this.getManagedBean().setSelectedApprovalTabBean(
								tabBeans.get(selectedTabIndex));
						this.getManagedBean().setScQtApproval(
								this.getManagedBean().getSelectedApprovalTabBean()
										.getScQtApproval());
					}
					this.getManagedBean().setTabList(tabBeans);
				
				}
			}
	}
	
	/**
	 * Approver setting dtls.
	 * 
	 * @param code
	 *            the code
	 * @param approval
	 *            the approval
	 * @return the dual list model
	 */
	private DualListModel<ScQtApproverSettings> approverSettingDtls(
			RoleCode code, Long orgProfileId, ScQtApproval approval) {
		List<ScQtApproverSettings> availableApprovers = new ArrayList<ScQtApproverSettings>();
		List<ScQtApproverSettings> selectedApprovers = new ArrayList<ScQtApproverSettings>();
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(code.value());
		userReqDto.setOrgProfileId(orgProfileId);
		userReqDto.setWithProfileGroup(true);
		UserResDTO userResDTO = iUserSO
				.retrieveUserListWithProfileGroupUserByRole(userReqDto);
		List<PmUser> approvers = userResDTO.getUserList();
		// Remove Current DO from approver list
		removeDeskOfficer(approvers);
		if (null != approval) {
			if (null != approval.getApproverSettings()
					&& !approval.getApproverSettings().isEmpty()) {
				boolean isApproverExists;
				for (PmUser pmUser : approvers) {
					isApproverExists = false;
					for (ScQtApproverSettings scQtApproverSettings : approval
							.getApproverSettings()) {
						if (scQtApproverSettings.getPmUser().getUserId()
								.equals(pmUser.getUserId())) {
							scQtApproverSettings.setScQtApproval(approval);
							isApproverExists = true;
							break;
						}
					}
					if (!isApproverExists) {
						ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
						qtApproverSettings.setPmUser(pmUser);
						qtApproverSettings.setScQtApproval(approval);
						availableApprovers.add(qtApproverSettings);
					}
				}
				selectedApprovers = approval.getApproverSettings();
			} else {
				for (PmUser pmUser : approvers) {
					ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
					qtApproverSettings.setPmUser(pmUser);
					qtApproverSettings.setScQtApproval(approval);
					availableApprovers.add(qtApproverSettings);
				}
				selectedApprovers = new ArrayList<ScQtApproverSettings>();
			}
		}
		return new DualListModel<ScQtApproverSettings>(availableApprovers,
				selectedApprovers);
	}
	
	/**
	 * Remove pmUser if contains loggined in Desk Officer
	 * 
	 * @param approvers
	 */
	private void removeDeskOfficer(List<PmUser> approvers) {
//      log.debug("removeDeskOfficer");
		if (CollectionUtils.isNotEmpty(approvers)) {
			PmUser currentDO = new FacesScopes().getUserSessionBean();
			//boolean isDO = false;
			//int index = 0;
			for (PmUser pmUser : approvers) {
				if (pmUser.getUserId().equals(currentDO.getUserId())) {
//					log.info("loopremoveDeskOfficer");
					//isDO = true;
					//index++;
					approvers.remove(pmUser);
					break;
				}
			}
			/*if (isDO) {
				approvers.remove(index);
			}*/
		}
//		log.debug("endremoveDeskOfficer");
	}
	
	/**
	 * Update qt dtls.
	 */
	private void updateCommittees() {
		List<TabBean> commiteTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommittes = null;
		if (null != commiteTabs) {
			qtCommittes = new ArrayList<ScQtCommittee>();
			for (TabBean tabBean : commiteTabs) {
				if (!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType())) {
					qtCommittes.add(tabBean.getScQtCommittee());
				}

			}
		}
		/*
		 * List<ScQtCommittee> qtCommittees = new ArrayList<ScQtCommittee>();
		 * qtCommittees.add(this.getManagedBean().getScQtCommittee());
		 */
		this.getManagedBean().getQt().setScQtCommittees(qtCommittes);
	}
	
	private ScQt retrieveScqtCommittee(ScQt copyScqt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(copyScqt.getQtId());
		CommitteResDTO resDTO = null;
		committeSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
		ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
		
		List<ScQtCommittee> list=new ArrayList<ScQtCommittee>();
		list.add(qtCommitte);
		copyScqt.setScQtCommittees(list);
		return copyScqt;
	}
	
	private ScQtCommittee getScQtCommitteeByQt(ScQt scQt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte =null;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
		}finally{
			committeSO =null;
		}
		return qtCommitte;
	}
	private ScQt retrieveCommitteeMembers(ScQt scQt,ScQt copyScqt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(copyScqt.getQtId());
		CommitteResDTO resDTO = null;
		
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
			if(null!=qtCommitte){
				IParameterSO parameterSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				ParameterResDTO parameterResDTO = null;
				
				parameterReqDTO
				.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
		        parameterReqDTO
				.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
				
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
				List<ScQtCommittee> qtCommitteList = new ArrayList<ScQtCommittee>();
				ScQtCommittee clonedQtCom = getScQtCommitteeByQt(scQt);
				if(null==clonedQtCom){
					clonedQtCom = new ScQtCommittee();
					clonedQtCom.setScQt(scQt);
				}
				setUserDetails(clonedQtCom);
				clonedQtCom.setCommitteeActivities(null);
				clonedQtCom.setQtCommitteRemarks(null);
				clonedQtCom.setScQtMeetings(null);
				
				ScCommittee committee = new ScCommittee();
				if(null!=clonedQtCom.getScCommittee()){
					committee = clonedQtCom.getScCommittee();
				}
				
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);
				
				List<ScCommitteeMember> toDeleteMember=new ArrayList<ScCommitteeMember>();
				if(null!=committee.getScCommitteeMembers() && !committee.getScCommitteeMembers().isEmpty()){
					for(ScCommitteeMember toDel:committee.getScCommitteeMembers()){
						toDel.setDeleted(true);
						toDeleteMember.add(toDel);
					}
					this.getManagedBean().setToBeDelCommitteMemebers(toDeleteMember);
				}
				
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				
				if(null!=qtCommitte.getScCommittee()){
					List<ScCommitteeMember> toCloneMemberList = new ArrayList<ScCommitteeMember>();
					if(CollectionUtils.isNotEmpty(qtCommitte.getScCommittee().getScCommitteeMembers())){
						toCloneMemberList=qtCommitte.getScCommittee().getScCommitteeMembers();
						for(ScCommitteeMember source:toCloneMemberList){
							ScCommitteeMember cloned = null;
							try {
								cloned = (ScCommitteeMember) BeanUtils
										.cloneBean(source);
							} catch (IllegalAccessException | InstantiationException
									| InvocationTargetException | NoSuchMethodException e) {
								e.printStackTrace();
							}
							if(null!=cloned){
								cloned.setBrandNew(true);
								cloned.setCommitteeMemberId(null);
								setUserDetails(cloned);
								setCommitteeMemberOrgProfile(cloned);
								if(null==cloned.getPmOrgProfile()){//31709-start
									cloned.setPmOrgProfile(scQt.getPmOrgProfile());	
								}//31709-End
								cloned.setChangedDate(null);
								if (cloned.isConsolidate()) {
									this.getManagedBean().setSelectScCommitteMember(
											cloned);
								}
								cloned.setScCommittee(committee);
								committee.getScCommitteeMembers().add(cloned);
							}
						}
						
					}
				}
				clonedQtCom.setScCommittee(committee);
				qtCommitteList.add(clonedQtCom);
				scQt.setScQtCommittees(qtCommitteList);
				
				List<TabBean> committeTabs = new ArrayList<TabBean>();
				ParameterHelper parameterHelper = ParameterHelper.getInstance();
				String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
				committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, commType), commType, clonedQtCom));
				this.getManagedBean()
				.setScCommitteeMemberTabList(committeTabs);
			}
			
			
		}finally{
			committeSO=null;
		}
		
		
		return scQt;
	}
	
	
	private void retrieveComittee1And2Tier(ScQt scQt,ScQt copyScqt){
		String evaluationType = copyScqt.getEvaluationType();
		List<ScQtCommittee> qtCommitteList = new ArrayList<ScQtCommittee>();
		this.getManagedBean().setToBeDelCommitteMemebers(new ArrayList<ScCommitteeMember>());
		 List<TabBean> committeTabs = new ArrayList<TabBean>();
		 ParameterHelper parameterHelper = ParameterHelper.getInstance();
		 String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
		 String tabType=null;
		 ScQtCommittee clonedScQtCommittee =null;

			tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));
			boolean fsApplicable=false;
			SelectItem[] fcTypes=this.retrieveFinalizationCommitteeType();
			if(null!=fcTypes){
				if(fcTypes.length>0){
					for(SelectItem i:fcTypes){
						if(copyScqt.getFinalizationCommitte()!=null){
							if(i.getValue().equals(copyScqt.getFinalizationCommitte().getParameterId())){
								fsApplicable=true;
							}
						}
					}
				}
			}	
			if(fsApplicable){
				scQt.setFinalizationCommitte(copyScqt.getFinalizationCommitte());
				scQt.setFinalizationSecratery(copyScqt.getFinalizationSecratery());
			}
			
			if (null == this.getManagedBean().getQt().getFinalizationCommitte()) {
				scQt.setFinalizationCommitte(new PmParameter());
			}
			if (null == this.getManagedBean().getQt().getFinalizationSecratery()) {
				scQt.setFinalizationSecratery(new PmUser());
			}
			
	
	    if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType)) {
	    	tabType=ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
		}else{
//			log.info("==========2TierCommitteeCloneDuringPendingDocument========="+scQt.getQtNo());//31350
			tabType=ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE;
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
			
			
			tabType=ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
			
			
			tabType=ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
		}
	    
	    
	    
	    if (scQt.getPmOrgProfile().getBpk() &&						
				scQt.getContractType().getParameterId().equals(SourcingConstants.CENTRAL_CONTRACT)) {
			StringBuilder fcTitleLabel = new StringBuilder("lbl.ministry.finance");
			String fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString(),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			tabType = SourcingConstants.COMMITTE_TYPE_MINISTRY_OF_FINANCE;
			ScQtCommittee mofCommitte = cloneComitteee(scQt,copyScqt,tabType);
			if(null!=mofCommitte && null!=mofCommitte.getScCommittee() && null!=mofCommitte.getScCommittee().getCommitteeType()){//35041&25635-start
			committeTabs.add(new TabBean(fcTitleName, tabType, mofCommitte));
	        }else{//35041&25635-start
	        	//FC Tabs
				PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
				tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
				StringBuilder fcTitleLabel1 = new StringBuilder(
						"lbl.sc.finalization.committee.secretary.");
				String fcTitleName1 = ResourceBundleUtils
						.getMessage(fcTitleLabel1.toString()
									+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				if (fcSecretaryCode == null
						|| fcSecretaryCode.getParameterId() == null) {
					SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
					if (fcSecretaries != null && fcSecretaries.length > 0) {
						fcSecretaryCode = retrieveParameterById(Long
								.valueOf(fcSecretaries[0].getValue().toString()));
					} else {
						fcSecretaryCode = null;
					}
				}
				
				// Displaying the Secretary Details
				if (fcSecretaryCode != null) {
					
					String fcSecretary = fcSecretaryCode.getParameterCode();

					if (fcSecretary != null) {
						if (fcSecretary
								.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
							fcTitleLabel1
									.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
						} else {
							fcTitleLabel1
									.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
						}
						fcTitleName1 = ResourceBundleUtils.getMessage(
								fcTitleLabel1.toString().toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
					}
				}
				committeTabs.add(new TabBean(fcTitleName1, tabType, null));
	        }//35041&25635-End

		}else{
			//FC Tabs
			PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
			tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
			StringBuilder fcTitleLabel = new StringBuilder(
					"lbl.sc.finalization.committee.secretary.");
			String fcTitleName = ResourceBundleUtils
					.getMessage(fcTitleLabel.toString()
								+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		    
			if (fcSecretaryCode == null
					|| fcSecretaryCode.getParameterId() == null) {
				SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
				if (fcSecretaries != null && fcSecretaries.length > 0) {
					fcSecretaryCode = retrieveParameterById(Long
							.valueOf(fcSecretaries[0].getValue().toString()));
					;
				} else {
					fcSecretaryCode = null;
				}
			}
			
			// Displaying the Secretary Details
			if (fcSecretaryCode != null) {
				
				String fcSecretary = fcSecretaryCode.getParameterCode();

				if (fcSecretary != null) {
					if (fcSecretary
							.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
						fcTitleLabel
								.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
					} else {
						fcTitleLabel
								.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
					}
					fcTitleName = ResourceBundleUtils.getMessage(
							fcTitleLabel.toString().toLowerCase(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				}
			}
			committeTabs.add(new TabBean(fcTitleName, tabType, null));
		}
		
		//pass
		this.getManagedBean()
		.setScCommitteeMemberTabList(committeTabs);
	    
	    
		scQt.setScQtCommittees(qtCommitteList);
		
	
		for (TabBean tabBean : committeTabs) {
			if ((!SourcingConstants.COMMITTE_TYPE_FINALIZATION.equals(tabBean
					.getTabType()) && tabBean.getScQtCommittee()
					.getScCommittee().getCommitteStatus().getStatusId()
					.equals(QTStatusConst.CMT_COMPLETED))
					|| (SourcingConstants.COMMITTE_TYPE_FINALIZATION
							.equals(tabBean.getTabType())
							&& scQt.getFinalizationCommitte() != null
							&& scQt.getFinalizationCommitte().getParameterId() != null
							&& scQt.getFinalizationSecratery() != null && scQt
							.getFinalizationSecratery().getUserId() != null)) {
				tabBean.setIcon("ui-icon-endorse");
			}
		}
		if (committeTabs.size() > 0) {
			// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
			this.getManagedBean().setSelectedTabBean(
					committeTabs.get(this.getManagedBean()
							.getSelectedCommitteeTabIndex()));
		}
		
		this.getManagedBean().setQt(scQt);
	    
	}
	
	private ScQtCommittee cloneComitteee(ScQt scQt,ScQt copyScqt,String tabType){
		
		ScQtCommittee copyScQtCommittee=getQtCommiteDtlsByQtIdNType(tabType,copyScqt.getQtId());
		
		ScQtCommittee clonedScQtCommittee = getQtCommiteDtlsByQtIdNType(tabType,scQt.getQtId());
		if(clonedScQtCommittee==null){
			clonedScQtCommittee=getNewScQtComittee(scQt,tabType);
		}
		ScCommittee committee = clonedScQtCommittee.getScCommittee();
		if(null!=committee){
			List<ScCommitteeMember> toDeleteMember=new ArrayList<ScCommitteeMember>();
			if(null!=committee.getScCommitteeMembers() && !committee.getScCommitteeMembers().isEmpty()){
				for(ScCommitteeMember toDel:committee.getScCommitteeMembers()){
					toDel.setDeleted(true);
					toDeleteMember.add(toDel);
				}
				//this.getManagedBean().setToBeDelCommitteMemebers(toDeleteMember);
				this.getManagedBean().getToBeDelCommitteMemebers().addAll(toDeleteMember);
			}
		}
		
		committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
		committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
		//committee.setApprovalReq(true);//31943
		committee.setModified(true);
		
		if(null!=copyScQtCommittee && null!=copyScQtCommittee.getScCommittee()){//25635
			committee.setApprovalReq(copyScQtCommittee.getScCommittee().isApprovalReq());//31943
			List<ScCommitteeMember> toCloneMemberList = new ArrayList<ScCommitteeMember>();
			if(CollectionUtils.isNotEmpty(copyScQtCommittee.getScCommittee().getScCommitteeMembers())){
				toCloneMemberList=copyScQtCommittee.getScCommittee().getScCommitteeMembers();
				for(ScCommitteeMember source:toCloneMemberList){
					ScCommitteeMember cloned = null;
					try {
						cloned = (ScCommitteeMember) BeanUtils
								.cloneBean(source);
					} catch (IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					if(null!=cloned){
						cloned.setBrandNew(true);
						cloned.setCommitteeMemberId(null);
						setUserDetails(cloned);
						setCommitteeMemberOrgProfile(cloned);
						if(null==cloned.getPmOrgProfile()){//31943-start
							cloned.setPmOrgProfile(scQt.getPmOrgProfile());	
						}//31943-End
						cloned.setChangedDate(null);
						if (cloned.isConsolidate()) {
							this.getManagedBean().setSelectScCommitteMember(
									cloned);
						}
						cloned.setScCommittee(committee);
						committee.getScCommitteeMembers().add(cloned);
					}
				}
				
			}
		}
		
		clonedScQtCommittee.setScCommittee(committee);
		
		
		return clonedScQtCommittee;
	}
	
	private ScQtCommittee getNewScQtComittee(ScQt scQt,String commType){
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		ParameterResDTO parameterResDTO = null;
		ScQtCommittee scQtCommittee = new ScQtCommittee();
		try{
			
			if (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);

			} else if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);

			} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);

			} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);

			} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);

			} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);

			} else if (ParameterConst.COMMITTEE_TYPE_MINISTRY_OF_FINANCE.equalsIgnoreCase(commType)) {
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
				parameterReqDTO
						.setParameterCode(ParameterConst.COMMITTEE_TYPE_MINISTRY_OF_FINANCE);
			}

			parameterResDTO = parameterSO
					.retrieveParameterByTypeAndCode(parameterReqDTO);
			
			scQtCommittee.setScQt(scQt);
			setUserDetails(scQtCommittee);
			scQtCommittee.setCommitteeActivities(null);
			scQtCommittee.setQtCommitteRemarks(null);
			scQtCommittee.setScQtMeetings(null);
			ScCommittee committee = new ScCommittee();
			committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
			committee.setCommitteeType(parameterResDTO.getPmParameter());
			if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
					.equalsIgnoreCase(commType)) {
				committee.setApprovalReq(true);
			}
			setUserDetails(committee);

			committee
					.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
			scQtCommittee.setScCommittee(committee);
			
		} finally {
			parameterSO=null;
		}
		
			
		
		return scQtCommittee;
	}
	
	/**
	 * Retrieve finalization committee types.
	 * 
	 * @return the list
	 */
	public SelectItem[] retrieveFinalizationCommitteeType() {
		
		ScQt scQt = getManagedBean().getQt();
		SelectItem[] finalizationSecratary = getManagedBean().getFinalizationSecratary();
		Long currentStatusId = scQt.getQtStatus().getStatusId();
		
		if (finalizationSecratary == null
				&& isQTStatusLessThanOrEquals(currentStatusId, QTStatusConst.QT_PENDING_LOA)) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

				CommitteReqDTO reqDTO = new CommitteReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				CommitteResDTO resDTO = committeSO
						.retrieveFinalizationCommitteeType(reqDTO);

				List<PmParameter> finalizationCommTypes = resDTO.getFinalizationSecratry();
				if(CollectionUtils.isNotEmpty(finalizationCommTypes)){
					finalizationSecratary = new SelectItem[finalizationCommTypes.size()];
					for(int i=0;i<finalizationCommTypes.size();i++){
						String commTypeDesc = getParameterDesc(
								finalizationCommTypes.get(i),new FacesScopes().getUserLocale());
						finalizationSecratary[i] = new SelectItem(
								finalizationCommTypes.get(i).getParameterId(),commTypeDesc);
					}
					getManagedBean().setFinalizationSecratary(finalizationSecratary);
				}

				
			} finally {
				committeSO = null;
			}
		}

		return finalizationSecratary;
	}
	
	private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long qtId) {
		
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
			
		} finally {
			committeSO = null;
			//parameterSO=null;
		}
		return qtCommitte;
	}
	
	
	
	private boolean isIndirectWithInstantiated(Object o){
		
		if(o instanceof IndirectList) {
		    boolean inst = ((IndirectList)o).isInstantiated();
		    return inst;
		}
		
		return true;
	}
	
	private void retrieveCommitteeMembers2(ScQt copyScqt){
		String commType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
		ICommitteSO committeSO;
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(copyScqt.getQtId());
		CommitteResDTO resDTO = null;
		this.getManagedBean().setScCommitteeMemberTabList(null);
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
			
			if(null!=qtCommitte){
				IParameterSO parameterSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				ParameterResDTO parameterResDTO = null;
				
				parameterReqDTO
				.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
		        parameterReqDTO
				.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
				
				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
				
				ScQtCommittee clonedQtCom = new ScQtCommittee();
				clonedQtCom.setScQt(this.getManagedBean().getQt());
				setUserDetails(clonedQtCom);
				clonedQtCom.setCommitteeActivities(null);
				clonedQtCom.setQtCommitteRemarks(null);
				clonedQtCom.setScQtMeetings(null);
				
				ScCommittee committee = new ScCommittee();
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);
				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				
				if(null!=qtCommitte.getScCommittee()){
					List<ScCommitteeMember> toCloneMemberList = new ArrayList<ScCommitteeMember>();
					if(CollectionUtils.isNotEmpty(qtCommitte.getScCommittee().getScCommitteeMembers())){
						toCloneMemberList=qtCommitte.getScCommittee().getScCommitteeMembers();
						for(ScCommitteeMember source:toCloneMemberList){
							ScCommitteeMember cloned = null;
							try {
								cloned = (ScCommitteeMember) BeanUtils
										.cloneBean(source);
							} catch (IllegalAccessException | InstantiationException
									| InvocationTargetException | NoSuchMethodException e) {
								e.printStackTrace();
							}
							if(null!=cloned){
								cloned.setBrandNew(true);
								cloned.setCommitteeMemberId(null);
								setUserDetails(cloned);
								setCommitteeMemberOrgProfile(cloned);
								cloned.setChangedDate(null);
								if (cloned.isConsolidate()) {
									this.getManagedBean().setSelectScCommitteMember(
											cloned);
								}
								cloned.setScCommittee(committee);
								committee.getScCommitteeMembers().add(cloned);
							}
						}
						
					}
				}
				clonedQtCom.setScCommittee(committee);
				
				List<TabBean> committeTabs = new ArrayList<TabBean>();
				ParameterHelper parameterHelper = ParameterHelper.getInstance();
				String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
				committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, commType), commType, clonedQtCom));
				this.getManagedBean()
				.setScCommitteeMemberTabList(committeTabs);
			}
			
		}finally{
			committeSO=null;
		}
	}
	
	
	private ScQt retrieveSupplierCriteria(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtdetail(copyScqt);
		QuationTenderResDTO resDTO =QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
		
		
		this.getManagedBean().setAllStateCode(resDTO.getStateCode());
		
		
		
		if(copyScqt.isPanel()){
			this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
			if(CollectionUtils.isNotEmpty(resDTO.getPanelItems())){
				this.getManagedBean().setPanelItemList(
						resDTO.getPanelItems());
			}
			
			if (null != this.getManagedBean().getQt().getCircularVer()) {
				this.getManagedBean().setCircularExpDate(
						scQt.getCircularVer()
								.getEndDate());
				this.getManagedBean().setCircularNo(
						scQt.getCircularVer()
								.getCircularNo());
				this.getManagedBean().setCircularName(
						scQt.getCircularVer()
								.getName());
				this.getManagedBean().setCircularType(
						scQt.getCircularVer()
								.getDocType());
			}
			if (this.getManagedBean().getZoneGeneralTrees()== null ||
					this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
				retriveZonesByCircularNo();
			}
		}else{
			//quationTenderReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());
			//quationTenderReqDTO
					//.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
			//this.getManagedBean().getTobeRemoveCategoryCodes();
			
			ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
			
			ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
			if(null!=scQt.getScQtSupplierCriteria()){
				supplierCriteria =scQt.getScQtSupplierCriteria();
			}else{
				supplierCriteria.setScQt(scQt);
				supplierCriteria.setBrandNew(true);
			}
			

			if(cps!=null){
				if(cps.getLocationLevel()!=null){
					supplierCriteria.setLocationLevel(cps.getLocationLevel());
				}
				
				//to Delete
//				List<ScQtLocCriteria> tobeRemoveLocCriteriaList=new ArrayList<ScQtLocCriteria>();
//				if(null!=supplierCriteria.getScQtLocCriteria() && !supplierCriteria.getScQtLocCriteria().isEmpty()){
//					for(ScQtLocCriteria cloc:cps.getScQtLocCriteria()){
//						cloc.setDeleted(true);
//						tobeRemoveLocCriteriaList.add(cloc);
//					}
//					this.getManagedBean().setSupplierLocationList(tobeRemoveLocCriteriaList);
//				}
				
//				if(cps.getScQtLocCriteria()!=null){
//					List<ScQtLocCriteria> toAdd=new ArrayList<ScQtLocCriteria>();
//					for(ScQtLocCriteria cloc:cps.getScQtLocCriteria()){
//						ScQtLocCriteria loc=new ScQtLocCriteria();
//						loc.setScQtSupplierCriteria(supplierCriteria);
//						loc.setBrandNew(true);
//						loc.setDocId(cloc.getDocId());
//						loc.setDocType(cloc.getDocType());
//						loc.setPmDistrict(cloc.getPmDistrict());
//						loc.setPmDivision(cloc.getPmDivision());
//						loc.setPmState(cloc.getPmState());
//						loc.setReasonForUpdateEnabled(cloc.isReasonForUpdateEnabled());
//						loc.setRecordStatus(cloc.isRecordStatus());
//						loc.setRecordStatusFlg(cloc.getRecordStatusFlg());
//						loc.setSpecLocId(cloc.getSpecLocId());
//						loc.setZonalLocation(cloc.isZonalLocation());
//						toAdd.add(loc);
//					}
//					supplierCriteria.setScQtLocCriteria(toAdd);
//				}
				supplierCriteria.setAllState(cps.isAllState());
				
				
			}
			

//			if(copyScqt.isZonal()){
//				ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
//				qtZoneCriterias.setBrandNew(true);
//				qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
//				if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
//					qtZoneCriterias.setTplSpecZoneDtls(cps.getQtZoneCriterias().getTplSpecZoneDtls());
//				}
//				supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
//			}
			
			scQt.setScQtSupplierCriteria(supplierCriteria);
			this.getManagedBean().setSupplierCriteria(scQt.getScQtSupplierCriteria());
			List<ScQtCategory> categoryResults = resDTO
					.getCategories();
			List<ScQtSupplier> restrictResults=resDTO.getRestrictedSuppliers();
			
			List<ScQtCategory> currentExistingCategory=null;//34466&25635-start
			if(!this.getManagedBean().getQt().isBrandNew()){
			QuationTenderReqDTO currentReqDTO = new QuationTenderReqDTO();
			currentReqDTO.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO currentresDTO =QtDetailsSO.retriveQTSuppQulCriDetails(currentReqDTO);
			currentExistingCategory = currentresDTO
					.getCategories();
			}//34466&25635-end
			//to Delete
			List<ScQtCategory> tobeRemoveCategoryCodes=new ArrayList<ScQtCategory>();
			if(CollectionUtils.isNotEmpty(currentExistingCategory)){//34466&25635-start
				for(ScQtCategory toDel:currentExistingCategory){//34466&25635-end
					toDel.setDeleted(true);
					tobeRemoveCategoryCodes.add(toDel);
				}
				this.getManagedBean().setTobeRemoveCategoryCodes(tobeRemoveCategoryCodes);
			}
			
			if(categoryResults!=null){
				List<ScQtCategory> qtCategoryList = new ArrayList<ScQtCategory>();
				for(ScQtCategory cat:categoryResults){
					ScQtCategory category = new ScQtCategory();
					category.setScQt(scQt);
					category.setPmCategoryL1(cat.getPmCategoryL1());
					category.setPmCategoryL2(cat.getPmCategoryL2());
					category.setPmCategoryL3(cat.getPmCategoryL3());
					category.setPmCategoryL1Name(cat.getPmCategoryL1Name());
					category.setPmCategoryL2Name(cat.getPmCategoryL2Name());
					category.setPmCategoryL3Name(cat.getPmCategoryL3Name());
					qtCategoryList.add(category);
				}
				scQt.setScQtCategorys(qtCategoryList);
				this.getManagedBean().setQtCategoryList(qtCategoryList);
			}
			
			
			//to Delete
			List<ScQtSupplier> removeSupplierList = new ArrayList<ScQtSupplier>();
			if(scQt.getScQtSuppliers()!=null && !scQt.getScQtSuppliers().isEmpty()){
				for(ScQtSupplier toDel:scQt.getScQtSuppliers()){
					toDel.setDeleted(true);
					removeSupplierList.add(toDel);
				}
				this.getManagedBean().setToBeRemoveRestrictedSupplierList(removeSupplierList);
			}
						
			if(restrictResults!=null){
				List<ScQtSupplier> listSupplier=new ArrayList<ScQtSupplier>();
				for(ScQtSupplier supp:restrictResults){
					ScQtSupplier supplier=new ScQtSupplier();
					supplier.setBrandNew(true);
					supplier.setBumi(supp.isBumi());
					supplier.setDisableEditAccount(supp.isDisableEditAccount());
					supplier.setDisableForPindaan(supp.isDisableForPindaan());
					supplier.setDisqualifiedStage(supp.getDisqualifiedStage());
					supplier.setInvitationType(supp.getInvitationType());
					supplier.setJustificationEnabled(supp.isJustificationEnabled());
					supplier.setMofAccountExpDate(supp.getMofAccountExpDate());
					supplier.setReasonForUpdateEnabled(supp.isReasonForUpdateEnabled());
					supplier.setScQt(scQt);
					
//					if(supp.getScQtProposals()!=null && !supp.getScQtProposals().isEmpty()){
//						for(ScQtProposal proposal:supp.getScQtProposals()){
//							
//						}
//					}
					//supplier.setScQtProposals(scQtProposals);
					if(supp.getScQtSupplierZones()!=null && !supp.getScQtSupplierZones().isEmpty()){
						List<ScQtSupplierZone> scQtSupplierZones=new ArrayList<ScQtSupplierZone>();
						for(ScQtSupplierZone zone:supp.getScQtSupplierZones()){
							ScQtSupplierZone z=new ScQtSupplierZone();
							z.setBrandNew(true);
							setUserDetails(z);
							z.setDisableEditAccount(zone.isDisableEditAccount());
							z.setDisableForPindaan(zone.isDisableForPindaan());
							z.setDisqualifiedStage(zone.getDisqualifiedStage());
							z.setJustificationEnabled(zone.isJustificationEnabled());
							z.setReasonForUpdateEnabled(zone.isReasonForUpdateEnabled());
							z.setScQtSupplier(supplier);
							if(scQt.getScQtSupplierCriteria()!=null){
								if(scQt.getScQtSupplierCriteria().getQtZoneCriterias()!=null){
									for(TplSpecZoneDtl tpl:scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls()){
										if(tpl.getZoneName().equals(zone.getTplSpecZoneDtl().getZoneName())){
											z.setTplSpecZoneDtl(tpl);
										}
									}
								}
							}
							scQtSupplierZones.add(z);
						}
						supplier.setScQtSupplierZones(scQtSupplierZones);
					}
					
					
					
					supplier.setSmMofAccount(supp.getSmMofAccount());
					supplier.setSmSupplier(supp.getSmSupplier());
					supplier.setStatusEnabled(supp.isStatusEnabled());
					supplier.setSupplierAddress(supp.getSupplierAddress());
					supplier.setSupplierMofNo(supp.getSupplierMofNo());
					supplier.setSupplierName(supp.getSupplierName());
					setUserDetails(supplier);
					
					
					
					supp.setScQt(scQt);
					listSupplier.add(supp);
				}
				scQt.setScQtSuppliers(listSupplier);
				this.getManagedBean().setQtSupplierList(listSupplier);
			}
			
			this.getManagedBean()
			.setTotalSuppliers(
					resDTO.getRestrictedSuppliers() != null ? resDTO
							.getRestrictedSuppliers()
							.size() : 0);
			
			if (ParameterConst.OPEN_FOR_BUMI.equals(scQt.getOpenFor())) {
				CreateQtBackingBean backingBean = new CreateQtBackingBean();
				backingBean.clearNonBumiSuppliers(scQt);
			}
		}
		
		
		
		
		return scQt;
	}
	
	private void retrieveSupplierCriteria2(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtdetail(copyScqt);
		QuationTenderResDTO tenderResDTO=null;
		tenderResDTO =QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
		if(tenderResDTO.getQtDetail().getScQtSupplierCriteria()!=null)
			getManagedBean().setSupplierCriteria(tenderResDTO.getQtDetail().getScQtSupplierCriteria());
		this.getManagedBean().setAllStateCode(tenderResDTO.getStateCode());
		List<ScQtCategory> qtCategoryList = new ArrayList<ScQtCategory>();
		if(tenderResDTO.getQtDetail().getScQtSupplierCriteria()!=null)
			this.getManagedBean().getQt().setScQtSupplierCriteria(tenderResDTO.getQtDetail().getScQtSupplierCriteria());
		this.getManagedBean().setQtCategoryList(null);
		this.getManagedBean().setQtSupplierList(null);
		this.getManagedBean().setTotalSuppliers(null);
		if (!copyScqt.isPanel()) {
			List<ScQtCategory> categoryResults = tenderResDTO
					.getCategories();
			for(ScQtCategory cat:categoryResults){
				ScQtCategory category = new ScQtCategory();
				category.setScQt(scQt);
				category.setPmCategoryL1(cat.getPmCategoryL1());
				category.setPmCategoryL2(cat.getPmCategoryL2());
				category.setPmCategoryL3(cat.getPmCategoryL3());
				category.setPmCategoryL1Name(cat.getPmCategoryL1Name());
				category.setPmCategoryL2Name(cat.getPmCategoryL2Name());
				category.setPmCategoryL3Name(cat.getPmCategoryL3Name());
				qtCategoryList.add(category);
			}
			this.getManagedBean().setQtCategoryList(qtCategoryList);
			
			if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
					.equalsIgnoreCase(copyScqt.getQtType())) {
				List<ScQtSupplier> listSupplier=new ArrayList<ScQtSupplier>();
				for(ScQtSupplier supp:tenderResDTO.getRestrictedSuppliers()){
					supp.setScQt(scQt);
					listSupplier.add(supp);
				}
				
				this.getManagedBean().setQtSupplierList(listSupplier);
				
				this.getManagedBean()
						.setTotalSuppliers(
								tenderResDTO.getRestrictedSuppliers() != null ? tenderResDTO
										.getRestrictedSuppliers()
										.size() : 0);
				if (ParameterConst.OPEN_FOR_BUMI.equals(scQt.getOpenFor())) {
					CreateQtBackingBean backingBean = new CreateQtBackingBean();
					backingBean.clearNonBumiSuppliers(scQt);
				}
				
			}
		}
	}
	
	private void retriveZonesByCircularNo() {
//		debugLogger("retriveZonesByCircularNo");
		
		
		//PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {
			//PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			//panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					//.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							//IPanelSupplierSO.class);

			List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();
			
			/*if (CollectionUtils.isNotEmpty(panelItemList)){

				reqDTO.setPanelItem(panelItemList.get(0));
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
			}

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {
				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {
					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getZoneGeneralTrees()
							&& getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper
									.getStateFromCtPanelZone(panelZones
											.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper
									.getDistrictFromCtPanelZone(panelZones
											.getPanelZoneDtls());

							getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													true, TplSpecZoneLoc.class));
						}
					}
				}
			}*/ //else {
				
				this.getManagedBean().setTotalSuppliers(0);
				
				if (CollectionUtils.isNotEmpty(panelItemList)) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(panelItemList.get(0).getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != supplierResDTO.getCtPanelSuppliers()) {
						this.getManagedBean().setTotalSuppliers(
								supplierResDTO.getCtPanelSuppliers().size());
						for(CtPanelSupplier paelSupplier:supplierResDTO.getCtPanelSuppliers()){
							paelSupplier.setSmMofAccount(retriveMofAccountBySupplier(paelSupplier.getSupplier()));
						}
						this.getManagedBean().setCtZoneSupplierList(
								supplierResDTO.getCtPanelSuppliers());
					}
				}
			//}
		} finally {
			//panelSupplierSO=null;
		}
	}
	
	private void retrieveNonZonalDtls() {
		log.debug("retrieveNonZonalDtls - START");
		ScQt scQt = this.getManagedBean().getQt();
		if (null != scQt.getZoneLocationLevel()
				&& null != scQt.getZoneLocationLevel()
						.getParameterId()) {
			Long locationLevel = scQt
					.getZoneLocationLevel().getParameterId();
			scQt.getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(locationLevel);
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					scQt.getScQtSupplierCriteria().setLocationLevel( paramDesc.getPmParameter());
					
					break;
				}
			}
			
			if (!ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt.getProcurementMode().getParameterCode())) {
				List<ScQtLocCriteria> scQtLocCriteriaList = new ArrayList<ScQtLocCriteria>();
				if (null != getManagedBean().getZoneGeneralTrees()
						&& !getManagedBean().getZoneGeneralTrees().isEmpty()) {
					for (TreeNode treeNode : getManagedBean().getZoneGeneralTrees()) {
						List<ScQtLocCriteria> qtLocCriteriaList = getLocationsList(treeNode);
						scQtLocCriteriaList.addAll(qtLocCriteriaList);
					}
				}
				retrievLocationDtls(scQtLocCriteriaList);	
			}
		}
		log.debug("retrieveNonZonalDtls - END");
	}
 
 /**
	 * Retriev location dtls.
	 * 
	 * @param scQtLocCriteriaList
	 *            the sc qt loc criteria list
	 */
 
 
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.debug("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getZoneTrees().add(
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
							getManagedBean().getZoneTrees().add(
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
						getManagedBean().getZoneTrees().add(
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
							getManagedBean().getZoneTrees().add(
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
		log.debug("retrievLocationDtls - END");
	}
	
	private List<ScQtLocCriteria> getLocationsList(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		for (TreeNode zoneNode : zoneLocation.getChildren()) {
			for (TreeNode stateNode : zoneNode.getChildren()) {
				if (stateNode.getType().equals("state")) {

					TplSpecLoc stateLoc = (TplSpecLoc) stateNode.getData();
					ScQtLocCriteria stateLocCriteria = new ScQtLocCriteria();
					stateLocCriteria.setPmState(stateLoc.getPmState());
					// stateLocCriteria.setPmDistrict(stateLoc.getPmDistrict());
					// stateLocCriteria.setPmDivision(stateLoc.getPmDivision());
					// ScQtLocCriteria locationDTO = (ScQtLocCriteria)
					// zoneLocation.getData();
					results.add(stateLocCriteria);

					for (TreeNode districtNode : stateNode.getChildren()) {
						TplSpecLoc districtLoc = (TplSpecLoc) districtNode
								.getData();
						ScQtLocCriteria districtLocCriteria = new ScQtLocCriteria();
						// districtLocCriteria.setPmState(districtLoc.getPmState());
						districtLocCriteria.setPmDistrict(districtLoc
								.getPmDistrict());
						districtLocCriteria.setPmDivision(districtLoc
								.getPmDivision());
						results.add(districtLocCriteria);
						for (TreeNode divisionCode : districtNode.getChildren()) {
							TplSpecLoc divisionLoc = (TplSpecLoc) divisionCode
									.getData();
							ScQtLocCriteria divisionLocCriteria = new ScQtLocCriteria();
							divisionLocCriteria.setPmDistrict(divisionLoc
									.getPmDistrict());
							results.add(divisionLocCriteria);
						}
					}
				}
			}
		}
		/*
		 * if (zoneLocation.isLeaf()) {
		 * 
		 * } else { for (TreeNode childrendLocationNode :
		 * zoneLocation.getChildren()) {
		 * results.addAll(getLocationsList(childrendLocationNode)); } }
		 */
		return results;
	}
	
	private void retrieveQtnDetails(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		//ScQt scQt=new ScQt();
		//if(null==scQt.getFileNo()){25635&31548-start
			
			//scQt.setBrandNew(true);
		//}25635&31548-End
		scQt.setFileNo(null);
		scQt.setObtainSpecialApproval(copyScqt
				.getObtainSpecialApproval());
		scQt.setPanel(copyScqt.isPanel());
		scQt.setCircularVer(copyScqt.getCircularVer());
		scQt.setItemGroup(copyScqt.getItemGroup());
		scQt.setQtTitle(copyScqt.getQtTitle());
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		scQt.setPartial(copyScqt.isPartial());
		BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
				.getDecryptedExpectedIntendedPrice(copyScqt.getQtId());
		if(null==tmpExpectedIntendedPrice){//31709-start
			tmpExpectedIntendedPrice=copyScqt.getExpectedIntendedPrice();	
		}//31709-End
		scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
	//	scQt.setEstimatedAmount(copyScqt.getEstimatedAmount());
	//	scQt.setDecryptedEstAmt(copyScqt.getDecryptedEstAmt());
	//	scQt.setDecExpectedIntendedPrice(copyScqt.getDecExpectedIntendedPrice());
		
		//9019 added for disable mandatory checking while clone
		scQt.setCloned(true);
		scQt.setZonal(copyScqt.isZonal());
		
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		scQt.setQtType(copyScqt.getQtType());
		scQt.setOpenFor(copyScqt.getOpenFor());
		scQt.setEvaluationType(copyScqt.getEvaluationType());
		scQt.setScheduleType(copyScqt.getScheduleType());
		scQt.setMaxProposal(copyScqt.getMaxProposal());
		scQt.setEvaluateBy(copyScqt.getEvaluateBy());
		
		scQt.setQtType(copyScqt.getQtType());
		scQt.setZoneLocationLevel(copyScqt.getZoneLocationLevel());
		scQt.setProcurementTypeCatId(copyScqt.getProcurementTypeCatId());
		scQt.setPmProcurementCategory(copyScqt.getPmProcurementCategory());
		scQt.setContractType(copyScqt.getContractType());
		scQt.setExistingContractNo(copyScqt.getExistingContractNo());
		scQt.setFulfilmentType(copyScqt.getFulfilmentType());
		scQt.setContractDuration(copyScqt.getContractDuration());
		//scQt.setPublishPeriod(copyScqt.getPublishPeriod());//31799
		//scQt.setQtValidityPeriod(copyScqt.getQtValidityPeriod());//31799
		
		scQt.setSpecApprovalReq(copyScqt.getSpecApprovalReq());
		scQt.setMinistrySpecSecrataryId(copyScqt.getMinistrySpecSecrataryId());
		
		scQt.setJustificationProcValue(copyScqt.getJustificationProcValue());
		
		PmStatus status = getPmStatusByStatusId(QTStatusConst.QT_PENDING_QT_SUBMISSION);
		scQt.setQtStatus(status);
		
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(scQt);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(Boolean.TRUE);
		qtStatus.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		scQt.setCurrentWorkFlowStatus(qtStatus);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		scQt.setScQtStatus(qtStatuses);
		
		scQt.setContractType(copyScqt.getContractType());
		setUserDetails(scQt);
		
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		if (pmOrgProfile != null) {
			scQt.setPmOrgProfile(pmOrgProfile);
			scQt.setOwnerOrgProfile(pmOrgProfile);
			scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			scQt.setOwnerOrgProfileId(pmOrgProfile.getOrgProfileId());
		}
		
		this.getManagedBean().setPanel(scQt.isPanel());
		Long currentStatus = this.getManagedBean().getQt()
				.getQtStatus().getStatusId();
		this.getManagedBean().setCurrentStatusId(currentStatus);
		this.getManagedBean().getCtContract().setContractNo(this.getManagedBean().getQt().getExistingContractNo());
		
		if (scQt.isZonal()) {
			
			if (null != this.getManagedBean().getQt()
					.getZoneLocationLevel()) {
				this.getManagedBean().setStateCodes(
						this.getManagedBean().getQt()
								.getZoneLocationLevel()
								.getParameterCode());
			}
			
				ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
				
//				ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
//				supplierCriteria.setScQt(scQt);
//				supplierCriteria.setBrandNew(true);
//				supplierCriteria.setLocationLevel(new PmParameter());
//				supplierCriteria.setScQtLocCriteria(null);
//				//supplierCriteria.setAllState(cps.isAllState());
//				if (supplierCriteria.isAllState() == true) {
//					this.getManagedBean().setZoneTrees(null);
//				}
//				
//				ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
//				qtZoneCriterias.setBrandNew(true);
//				qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
				ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
				if(null!=scQt.getScQtSupplierCriteria()){
					supplierCriteria= scQt.getScQtSupplierCriteria();
				}
				
				supplierCriteria.setScQt(scQt);
				supplierCriteria.setBrandNew(true);
				supplierCriteria.setLocationLevel(new PmParameter());
				supplierCriteria.setScQtLocCriteria(null);
				setUserDetails(supplierCriteria);
				if (supplierCriteria.isAllState() == true) {
				this.getManagedBean().setZoneTrees(null);
				}
				
				ScQtZoneCriteria qtZoneCriterias =supplierCriteria
						.getQtZoneCriterias();
				
				if (null == qtZoneCriterias) {
					qtZoneCriterias = new ScQtZoneCriteria();
					qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
					supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
				}
				//qtZoneCriterias.setBrandNew(true);
				qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
				setUserDetails(qtZoneCriterias);
				if(!copyScqt.isPanel()){
					if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
						//qtZoneCriterias.setTplSpecZoneDtls(cps.getQtZoneCriterias().getTplSpecZoneDtls());
						List<TplSpecZoneDtl> listTpl=new ArrayList<TplSpecZoneDtl>();
						for(TplSpecZoneDtl tpl:cps.getQtZoneCriterias().getTplSpecZoneDtls()){
							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(tpl.getZoneName());
							tplSpecZoneDtl.setPanelZone(tpl.getPanelZone());
							tplSpecZoneDtl.setTplSpecZone(qtZoneCriterias);
							tplSpecZoneDtl.setBrandNew(true);
							setUserDetails(tplSpecZoneDtl);
							if(tpl.getTplSpecZoneLocs()!=null){
								List<TplSpecZoneLoc> listLoc=new ArrayList<TplSpecZoneLoc>();
								for(TplSpecZoneLoc loc:tpl.getTplSpecZoneLocs()){
									TplSpecZoneLoc l=new TplSpecZoneLoc();
									l.setBrandNew(true);
									l.setTplSpecZoneDtl(tplSpecZoneDtl);
									l.setPmDistrict(loc.getPmDistrict());
									l.setPmState(loc.getPmState());
									l.setPmDivision(loc.getPmDivision());
									l.setZonalLocation(loc.isZonalLocation());
									setUserDetails(l);
									listLoc.add(l);
								}
								tplSpecZoneDtl.setTplSpecZoneLocs(listLoc);
							}
							
							
							listTpl.add(tplSpecZoneDtl);
						}
						qtZoneCriterias.setTplSpecZoneDtls(listTpl);
					}
					this.getManagedBean().setZoneGeneralTrees(null);
				}
				
				supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
				scQt.setScQtSupplierCriteria(supplierCriteria);
				List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
				
				populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
			
		} else {
//			ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
//			
//			ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
//			if(null!=scQt.getScQtSupplierCriteria()){
//				supplierCriteria= scQt.getScQtSupplierCriteria();
//			}
//			supplierCriteria.setScQt(scQt);
//			supplierCriteria.setBrandNew(true);
//			supplierCriteria.setLocationLevel(cps.getLocationLevel());
//			
//			if(cps.getScQtLocCriteria()!=null){
//				List<ScQtLocCriteria> listLoc=new ArrayList<ScQtLocCriteria>();
//				for(ScQtLocCriteria loc:cps.getScQtLocCriteria()){
//					ScQtLocCriteria toloc=new ScQtLocCriteria();
//					toloc.setBrandNew(true);
//					toloc.setScQtSupplierCriteria(supplierCriteria);
//					toloc.setDocId(loc.getDocId());
//					toloc.setDocType(loc.getDocType());
//					toloc.setPmDistrict(loc.getPmDistrict());
//					toloc.setPmDivision(loc.getPmDivision());
//					toloc.setPmState(loc.getPmState());
//					toloc.setSpecLocId(loc.getSpecLocId());
//					toloc.setZonalLocation(loc.isZonalLocation());
//				}
//				supplierCriteria.setScQtLocCriteria(listLoc);
//			}
//			
//			
//			supplierCriteria.setAllState(cps.isAllState());
//			supplierCriteria.setLocationLevel(cps.getLocationLevel());
//			setUserDetails(supplierCriteria);
//			if (supplierCriteria.isAllState() == true) {
//				this.getManagedBean().setZoneTrees(null);
//			}
//			scQt.setScQtSupplierCriteria(supplierCriteria);
		}
		
		if (scQt.getObtainSpecialApproval() != null && !scQt.getObtainSpecialApproval()) {
			this.getManagedBean().setPtjSpecialApproval(
					getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));
							
		} else {
			this.getManagedBean().setPtjSpecialApproval(scQt.getPtjSpecialApproval());
		}
		
		if (null != getManagedBean().getQt() && null == getManagedBean().getQt().getProcurementTypeCatId()) {
			getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
		}
		
		if (copyScqt.isPanel()) {
			FacesContext context = FacesContext.getCurrentInstance();
			ELContext elContext = context.getELContext();
			CreateQtBackingBean createQtBB = (CreateQtBackingBean) elContext.getELResolver().getValue(elContext, null,
					"createQtBB");
			if (null != scQt.getCircularVer()) {// 25635-start
				createQtBB.changePanelGroupItem();
				this.getManagedBean().setPanelItemList(null);// 34330&25635
				this.getManagedBean().setPanelGroupItems(null);// 34330&25635
			}// 25635-End
		}	
	}
	
	private void retrieveQtnDetails2(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		scQt.setFileNo(null);
		
		this.getManagedBean().setStateCodes(null);
		this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
		this.getManagedBean().setZoneSuppQualTrees(null);
		this.getManagedBean().setCtzones(Boolean.FALSE);
		this.getManagedBean().setTemplateSpecZoneDtls(null);
		this.getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		this.getManagedBean().setZoneSuppQualTrees(null);
		
		scQt.setObtainSpecialApproval(copyScqt
				.getObtainSpecialApproval());
		scQt.setPanel(copyScqt.isPanel());
		this.getManagedBean().setPanel(copyScqt.isPanel());
		if (copyScqt.isPanel()) {
			scQt.setCircularVer(copyScqt.getCircularVer());
			scQt.setItemGroup(copyScqt.getItemGroup());
		}
		scQt.setQtTitle(copyScqt.getQtTitle());
        //9019 added for disable mandatory checking while clone
		scQt.setCloned(true);
		
		scQt.setProcurementMode(copyScqt.getProcurementMode());
			BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
					.getDecryptedExpectedIntendedPrice(copyScqt.getQtId());
			scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
			scQt.setEstimatedAmount(copyScqt.getEstimatedAmount());
			scQt.setZonal(copyScqt.isZonal());
			setProcurementMode(scQt, tmpExpectedIntendedPrice);
			scQt.setQtType(copyScqt.getQtType());
		scQt.setZoneLocationLevel(copyScqt.getZoneLocationLevel());
		
		if(copyScqt.getScQtSupplierCriteria()!=null){
			scQt.setScQtSupplierCriteria(copyScqt.getScQtSupplierCriteria());
		}
		
		
		
		if (copyScqt.isZonal()
				&& null != this.getManagedBean().getZoneGeneralTrees()
				&& this.getManagedBean().getZoneGeneralTrees()
						.isEmpty()) {
			if (null != copyScqt.getZoneLocationLevel()) {
				this.getManagedBean().setStateCodes(
						copyScqt.getZoneLocationLevel()
								.getParameterCode());
			}
			
			if (copyScqt.getScQtSupplierCriteria() != null &&
					copyScqt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
				List<TplSpecZoneDtl> specZoneDtls = copyScqt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
				populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
				// setting into zones bean to validate zones while adding 
				if (CollectionUtils.isNotEmpty(specZoneDtls)) {
					List<ZonesBean> zoneBeanLst = new ArrayList<ZonesBean>();
					for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
						ZonesBean bean = new ZonesBean();
						bean.setTplSpecZoneDtl(specZoneDtl);
						zoneBeanLst.add(bean);
					}
					this.getManagedBean().setBeans(zoneBeanLst);
				}
			}

		}
		scQt.setProcurementTypeCatId(copyScqt.getProcurementTypeCatId());
		scQt.setPmProcurementCategory(copyScqt.getPmProcurementCategory());
		scQt.setEvaluationType(copyScqt.getEvaluationType());
		scQt.setContractType(copyScqt.getContractType());
		scQt.setExistingContractNo(copyScqt.getExistingContractNo());
		scQt.setFulfilmentType(copyScqt.getFulfilmentType());
		scQt.setContractDuration(copyScqt.getContractDuration());
		scQt.setPublishPeriod(copyScqt.getPublishPeriod());
		scQt.setQtValidityPeriod(copyScqt.getQtValidityPeriod());
		scQt.setSpecApprovalReq(copyScqt.getSpecApprovalReq());
		scQt.setMinistrySpecSecrataryId(copyScqt.getMinistrySpecSecrataryId());

		this.getManagedBean().setQt(scQt);
		
	}
	
	private void populateZoneDetailIntoTreeNode(ScQt scQt, List<TplSpecZoneDtl> specZoneDtls) {
		
		List<TreeNode> zoneGeneralTrees = getManagedBean().getZoneGeneralTrees();
		
		this.getManagedBean().setCtzones(Boolean.TRUE);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		if (zoneGeneralTrees == null) {
			zoneGeneralTrees = new ArrayList<TreeNode>();
			getManagedBean().setZoneGeneralTrees(zoneGeneralTrees);
		} else {
			zoneGeneralTrees.clear();	
		}
		
		boolean zoneCoverageReadonly = enableFieldByStatusNFiledName(QTFieldConst.FIELD_QT_ZONE_COVERAGE);
		List<TreeNode> zoneSuppQualTrees = null;
		if (zoneCoverageReadonly) {
			zoneSuppQualTrees = getManagedBean().getZoneSuppQualTrees();
			if (zoneSuppQualTrees == null) {
				zoneSuppQualTrees = new ArrayList<TreeNode>();
				getManagedBean().setZoneSuppQualTrees(zoneSuppQualTrees);
			} else {
				zoneSuppQualTrees.clear();	
			}	
		} else {
			getManagedBean().setZoneSuppQualTrees(null);
		}
		
		if (CollectionUtils.isNotEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl zone : specZoneDtls) {
				if (CollectionUtils.isNotEmpty(zone.getTplSpecZoneLocs())) {
					List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
					
					for (TplSpecZoneLoc specLoc : zone.getTplSpecZoneLocs()) {
						if (specLoc.isZonalLocation()) {
							specZoneLocs.add(specLoc);
						}
					}
					zoneGeneralTrees.add(
							LocationSingleton.getInstance().constuctTreeNode(
									zone, specZoneLocs));
					
					if (zoneSuppQualTrees != null) {
						zoneSuppQualTrees.add(
								LocationSingleton.getInstance().constuctTreeNode(
										zone, zone.getTplSpecZoneLocs()));	
					}
				}
			}
		}
	}
	
	public boolean enableFieldByStatusNFiledName(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		return enableFieldByStatusNFiledName(fieldName, statusId, 
				getManagedBean().isMaintanceFlow(), 
				!getManagedBean().isReadOnly());

	}
	
	private boolean enableFieldByStatusNFiledName(String fieldName,
			Long statusId, boolean isMaintance, boolean isEditable) {

		List<String> values = QTFieldMapper.getEnableFieldsByStatus(statusId,
				isMaintance, isEditable);
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.contains(fieldName)) {
				return false;
			}

		}
		return true;

	}
	
	public void setProcurementMode(ScQt scQt, BigDecimal amount) {

//		debugLogger("setProcurementMode,@qtNo = " + scQt.getQtNo());
		IParameterSO parameterSo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			BigDecimal estimatedAmount = scQt.getEstimatedAmount();
			boolean isQuotationThresHold = isQuotationThresHold(amount);
			boolean isPanel = scQt.isPanel();
			ScQtSupplierCriteria scQtSupplierCriteria = scQt
					.getScQtSupplierCriteria();
			String qtType = scQt.getQtType();

			// Intended Price
			if (isPanel) {
				BigDecimal panelThreshold = getPanelThresholdAmount();

				if (null == amount || (amount.compareTo(panelThreshold) <= 0)) {
					throw new EpException(MessageConst.ERROR_760380,
							new String[] { SourcingConstants.DF_AMOUNT
									.format(panelThreshold) });
				}
			} else {
				BigDecimal prThreshold = getPRThresholdAmount();

				if (null == amount || (amount.compareTo(prThreshold) <= 0)) {
					throw new EpException(MessageConst.ERROR_760024,
							new String[] { SourcingConstants.DF_AMOUNT
									.format(prThreshold) });
				}
			}
			
				// Procurement Mode
				PmPtjSpecialApproval ptjSpecialApproval = getManagedBean().getPtjSpecialApproval();
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO
						.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				this.getManagedBean().setProcMethodDropdownPresent(false);
				if (isQuotationThresHold || isPanel) {
					parameterReqDTO
							.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				} else if (BooleanUtils.isTrue(scQt.getObtainSpecialApproval()) && 
						ptjSpecialApproval != null && !isQuotationThresHold &&
						ptjSpecialApproval.getProcureAmt() != null &&
						amount != null &&
						amount.compareTo(ptjSpecialApproval.getProcureAmt()) <= 0){
					this.getManagedBean().setProcMethodDropdownPresent(true);
					//scQt.setProcurementMode(new PmParameter());
				}else {
					parameterReqDTO
							.setParameterCode(ParameterConst.PROCUREMENT_MODE_TENDER);
				}
				if(!this.getManagedBean().isProcMethodDropdownPresent()){
					ParameterResDTO parameterResDTO = parameterSo
							.retrieveParameterByTypeAndCode(parameterReqDTO);
					scQt.setProcurementMode(parameterResDTO.getPmParameter());
				}
				
//			debugLogger("setProcurementMode, @procurementMode = "
//					+ scQt.getProcurementMode());
			if (estimatedAmount == null) {
				// QT Type
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

				// Open For
				if (isPanel) {
					scQt.setOpenFor(null);
				} else {
					if (isQuotationThresHold && isOpenForBumiputraThresHold(amount)) {
						scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
					} else {
						scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
					}	
				}

				// Evaluation Type
				if (isQuotationThresHold && isOneTierThresHold(amount)) {
					scQt.setEvaluationType(ParameterConst.EVALUATION_TYPES_ONE_TIER);

					populateEvaluationTypes(scQt, amount);
				}
			} else {
				if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
						.equalsIgnoreCase(qtType)) {
					// Open For - Restricted Tender
					updateRestrictedTenderOpenFor(scQt);
				}
			}

			// All State
			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN
					.equalsIgnoreCase(qtType)) {
				updateRestrictedTenderOpenFor(scQt);
				if (null != scQtSupplierCriteria) {
					if (!isQuotationThresHold
							&& getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR) {

						scQtSupplierCriteria.setAllState(true);

					} else {
						if (null == scQtSupplierCriteria.getLocationLevel()) {
							scQtSupplierCriteria
									.setLocationLevel(new PmParameter());
						}

						scQtSupplierCriteria.setAllState(false);
					}
				}
			}

			// Reset zones details
			if (null != scQtSupplierCriteria
					&& ((scQtSupplierCriteria.isAllState() && ParameterConst.QUOTATION_TENDER_TYPE_OPEN
							.equalsIgnoreCase(qtType)) || ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
							.equalsIgnoreCase(qtType))) {
				if (null != getManagedBean().getExistingSupplierLocationList()) {
					this.getManagedBean().setSupplierLocationList(
							getManagedBean().getExistingSupplierLocationList());
					getManagedBean().setExistingSupplierLocationList(null);
					getManagedBean().setZoneTrees(null);
				}
			}
		} finally {
			parameterSo = null;
		}
	}
	
	public void populateEvaluationTypes(ScQt scQt, BigDecimal estimatedAmount) {
		
//		debugLogger("populateEvaluationTypes,@qtNo = "+scQt.getQtNo());
		if (isOneTierThresHold(estimatedAmount)) {

			List<SelectItem> itemList = new ArrayList<SelectItem>();
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				String evaluationTypeCode = paramDesc.getPmParameter()
						.getParameterCode();
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER
						.equals(evaluationTypeCode)) {

					itemList.add(new SelectItem(evaluationTypeCode, paramDesc
							.getCodeName()));
				}
			}
			this.getManagedBean().setEvaluationTypeItem(itemList);
		}
	}
	
	private void updateRestrictedTenderOpenFor(ScQt scQt) {
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(getManagedBean().getQt());
			
			if (qtDetailSO.isRestrictedTenderOpenForBumi(reqDTO)) {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
				clearNonBumiSuppliers(scQt);
			} else {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
				List<ScQtSupplier> supplierList = this.getManagedBean()
						.getQtSupplierList();
				scQt.setScQtSuppliers(supplierList);
			}

		} finally {
			qtDetailSO = null;
		}
	}
	
	public void clearNonBumiSuppliers(ScQt scQt){
		
		List<ScQtSupplier> supplierList = this.getManagedBean()
				.getQtSupplierList();
		if (CollectionUtils.isNotEmpty(supplierList)) {
			int size = supplierList.size();
			List<ScQtSupplier> removeSupplierList = new ArrayList<ScQtSupplier>(size);
			for (ScQtSupplier supplier : supplierList) {
				if (!supplier.isBumi()) {
					removeSupplierList.add(supplier);
				}
			}
			if (CollectionUtils.isNotEmpty(removeSupplierList)) {
				this.getManagedBean().setToBeRemoveRestrictedSupplierList(
						removeSupplierList);
				if (size == removeSupplierList.size()) {
					supplierList.clear();
				} else {
					for (ScQtSupplier supplier : removeSupplierList) {
						supplierList.remove(supplier);
					}
				}
				this.getManagedBean().setQtSupplierList(supplierList);
				getManagedBean().setTotalSuppliers(supplierList.size());

			}
		}
		scQt.setScQtSuppliers(supplierList);
	}

	
	
	
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}
	
	

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public boolean isShowTree() {
		return showTree;
	}

	public void setShowTree(boolean showTree) {
		this.showTree = showTree;
	}
	
	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}
	
	/**
	 * Clone search qt action.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().setCloneCommitteeSelectQt(null);
		this.getManagedBean().setSelectedCloneTender(null);//25635

		getManagedBean().getMainCloneSearchQTDataModel().reloadData();//34053
	}
	
	/**
	 * Reset clone.
	 */
	public void resetClone() {
//		debugLogger("resetClone");
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		//this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());//34053
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromClosingDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
		.setToClosingDate(null);
		getManagedBean().setCloneCommitteeSelectQt(null);
	}
	
	/*25635-start
	 * datatable page loading
	 * 
	 */
	public void searchActionPageChange(){
		this.getManagedBean().setSelectedCloneTender(null);
	}
	//25635-end
	

	/*25635-start
	 * Briefingsitevisitdataset
	 * 
	 */
	private Date updateProposalStartDate(Date latestBsvDate, List<ScQtBsvDtl> scQtBsvDtls) {
		ScQt scQt = getManagedBean().getQt();
		
		if (CollectionUtils.isNotEmpty(scQtBsvDtls)) {
			for (ScQtBsvDtl scQtBsvDtl : scQtBsvDtls) {
				Date bsvDate = scQtBsvDtl.getBsvDate();
				
				if (bsvDate != null &&
						(scQtBsvDtl.getBsvStatus() == null || !QTStatusConst.BSV_CANCELLED.equals(scQtBsvDtl.getBsvStatus().getStatusId())) && 
						(latestBsvDate == null || (latestBsvDate != null && bsvDate.after(latestBsvDate)))) {
					latestBsvDate = bsvDate;
					//20190718@asyrafhaziq - Bug #26137 - Start
					scQt.setBsvRequired(true);
					//20190718@asyrafhaziq - Bug #26137 - End
				}
			}
		}
		
		return latestBsvDate;
	}
	
	
	//25635-end
	/*25635-start
	 * updatePublishingdates
	 * 
	 */
	private void updatePublishingdates(ScQt qt,ScQt copyScqt){
		if(null!=copyScqt.getPublishDate()){
			qt.setNewPublishDate(copyScqt.getPublishDate());
		}
		Date newPublishDate = qt.getNewPublishDate();
		if (newPublishDate != null) {
			qt.setPublishDate(DateUtils.setDefaultDateTime(newPublishDate));
			
			if (!qt.isBsvRequired()) {
				qt.setProposalStartDate(qt.getPublishDate());
			}
		
		}
		if(null==qt.getProposalStartDate()){
			qt.setProposalStartDate(qt.getPublishDate());	
		}
		updatePublishClosingDate(qt);	
	}
	//25635-end
	
	private ScAddress populateScAddress(ScAddress address) {//31653&25635-start
		//PmAddress pmAddress = addressType.getPmAddress();
		ScAddress scAddress = new ScAddress();
		scAddress.setAddressName(address.getAddressName());
		scAddress.setAddress1(address.getAddress1());
		scAddress.setAddress2(address.getAddress2());
		scAddress.setAddress3(address.getAddress3());
		// scAddress.setAddress4(pmAddress.getAddress4());
		scAddress.setPostcode(address.getPostcode());
		scAddress.setPmCountry(address.getPmCountry());
		scAddress.setPmState(address.getPmState());
		scAddress.setPmDivision(address.getPmDivision());
		scAddress.setPmDistrict(address.getPmDistrict());
		scAddress.setPmCity(address.getPmCity());
		scAddress.setMapLatitude(address.getMapLatitude());
		scAddress.setMapLongitude(address.getMapLongitude());

		scAddress.setContactFaxArea(address.getFaxArea());
		scAddress.setContactFaxCountry(address.getFaxCountry());
		scAddress.setContactPhoneArea(address.getPhoneArea());
		scAddress.setContactPhoneCountry(address.getPhoneCountry());
		scAddress.setContactFaxNo(address.getFaxNo());
		scAddress.setContactPhoneNo(address.getPhoneNo());
		scAddress.setContactMobileNo(address.getContactMobileNo());//33946
		scAddress.setContactPerson(address.getContactPerson());//33946
        setUserDetails(scAddress);
		return scAddress;
	}
	//31653&25635-End
	
	private void retrieveComittee1And2TierRevision(ScQt scQt,ScQt copyScqt){//31350-start
		String evaluationType = copyScqt.getEvaluationType();
		List<ScQtCommittee> qtCommitteList = new ArrayList<ScQtCommittee>();
		this.getManagedBean().setToBeDelCommitteMemebers(new ArrayList<ScCommitteeMember>());
		 List<TabBean> committeTabs = new ArrayList<TabBean>();
		 ParameterHelper parameterHelper = ParameterHelper.getInstance();
		 String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
		 String tabType=null;
		 ScQtCommittee clonedScQtCommittee =null;

			tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));
		boolean fsApplicable=false;
			SelectItem[] fcTypes=this.retrieveFinalizationCommitteeType();
			if(null!=fcTypes){
				if(fcTypes.length>0){
					for(SelectItem i:fcTypes){
						if(copyScqt.getFinalizationCommitte()!=null){
							if(i.getValue().equals(copyScqt.getFinalizationCommitte().getParameterId())){
								fsApplicable=true;
							}
						}
					}
				}
			}	
			if(fsApplicable){
				scQt.setFinalizationCommitte(copyScqt.getFinalizationCommitte());
				scQt.setFinalizationSecratery(copyScqt.getFinalizationSecratery());
			}
			
			if (null == this.getManagedBean().getQt().getFinalizationCommitte()) {
				scQt.setFinalizationCommitte(new PmParameter());
			}
			if (null == this.getManagedBean().getQt().getFinalizationSecratery()) {
				scQt.setFinalizationSecratery(new PmUser());
			}
			
	
	    if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType)) {
	    	tabType=ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
	    	ScQtCommittee ecCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
	    	if(null!=ecCommitte && null!=ecCommitte.getScCommittee() && (QTStatusConst.CMT_PENDING_SUBMISSION.equals(ecCommitte.getScCommittee().getCommitteStatus().getStatusId()) || QTStatusConst.CMT_COMPLETED.equals(ecCommitte.getScCommittee().getCommitteStatus().getStatusId()))){
	    		clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
		    	qtCommitteList.add(clonedScQtCommittee);
		    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, tabType), tabType, clonedScQtCommittee));	
	    	}else{
	    		
	    		qtCommitteList.add(ecCommitte);
		    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, tabType), tabType, ecCommitte));
	    	}
	    
		}
	    else{
			
			tabType=ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE;
			ScQtCommittee ocCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
			if(null!=ocCommitte && null!=ocCommitte.getScCommittee() && (QTStatusConst.CMT_PENDING_SUBMISSION.equals(ocCommitte.getScCommittee().getCommitteStatus().getStatusId()) || QTStatusConst.CMT_COMPLETED.equals(ocCommitte.getScCommittee().getCommitteStatus().getStatusId()))){
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
			}else{
	    		qtCommitteList.add(ocCommitte);
		    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, tabType), tabType, ocCommitte));
	    	}
			
			
			tabType=ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
			ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
			if(null!=tecCommitte && null!=tecCommitte.getScCommittee() && (QTStatusConst.CMT_PENDING_SUBMISSION.equals(tecCommitte.getScCommittee().getCommitteStatus().getStatusId()) || QTStatusConst.CMT_COMPLETED.equals(tecCommitte.getScCommittee().getCommitteStatus().getStatusId()))){
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
			}else{
	    		qtCommitteList.add(tecCommitte);
		    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, tabType), tabType, tecCommitte));
	    	}
			
			
			tabType=ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
			ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					scQt.getQtId());
			if(null!=fecCommitte && null!=fecCommitte.getScCommittee() && (QTStatusConst.CMT_PENDING_SUBMISSION.equals(fecCommitte.getScCommittee().getCommitteStatus().getStatusId()) || QTStatusConst.CMT_COMPLETED.equals(fecCommitte.getScCommittee().getCommitteStatus().getStatusId()))){
	    	clonedScQtCommittee =cloneComitteee(scQt,copyScqt,tabType);
	    	qtCommitteList.add(clonedScQtCommittee);
	    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, clonedScQtCommittee));
			}
			else{
	    		qtCommitteList.add(fecCommitte);
		    	committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
						parameterType, tabType), tabType, fecCommitte));
	    	}
		}
	    
	    
	    
	    if (scQt.getPmOrgProfile().getBpk() &&						
				scQt.getContractType().getParameterId().equals(SourcingConstants.CENTRAL_CONTRACT)) {
			StringBuilder fcTitleLabel = new StringBuilder("lbl.ministry.finance");
			String fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString(),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			tabType = SourcingConstants.COMMITTE_TYPE_MINISTRY_OF_FINANCE;
			ScQtCommittee mofCommitte = cloneComitteee(scQt,copyScqt,tabType);			
			committeTabs.add(new TabBean(fcTitleName, tabType, mofCommitte));

		}else{
			//FC Tabs
			PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
			tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
			StringBuilder fcTitleLabel = new StringBuilder(
					"lbl.sc.finalization.committee.secretary.");
			String fcTitleName = ResourceBundleUtils
					.getMessage(fcTitleLabel.toString()
								+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		    
			if (fcSecretaryCode == null
					|| fcSecretaryCode.getParameterId() == null) {
				SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
				if (fcSecretaries != null && fcSecretaries.length > 0) {
					fcSecretaryCode = retrieveParameterById(Long
							.valueOf(fcSecretaries[0].getValue().toString()));
					;
				} else {
					fcSecretaryCode = null;
				}
			}
			
			// Displaying the Secretary Details
			if (fcSecretaryCode != null) {
				
				String fcSecretary = fcSecretaryCode.getParameterCode();

				if (fcSecretary != null) {
					if (fcSecretary
							.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
						fcTitleLabel
								.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
					} else {
						fcTitleLabel
								.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
					}
					fcTitleName = ResourceBundleUtils.getMessage(
							fcTitleLabel.toString().toLowerCase(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				}
			}
			committeTabs.add(new TabBean(fcTitleName, tabType, null));
		}
	    
	  //pass
	  		this.getManagedBean()
	  		.setScCommitteeMemberTabList(committeTabs);
	  	    
	  	    
	  		scQt.setScQtCommittees(qtCommitteList);
	  		
	  	
	  		for (TabBean tabBean : committeTabs) {
	  			if ((!SourcingConstants.COMMITTE_TYPE_FINALIZATION.equals(tabBean
	  					.getTabType()) && tabBean.getScQtCommittee()
	  					.getScCommittee().getCommitteStatus().getStatusId()
	  					.equals(QTStatusConst.CMT_COMPLETED))
	  					|| (SourcingConstants.COMMITTE_TYPE_FINALIZATION
	  							.equals(tabBean.getTabType())
	  							&& scQt.getFinalizationCommitte() != null
	  							&& scQt.getFinalizationCommitte().getParameterId() != null
	  							&& scQt.getFinalizationSecratery() != null && scQt
	  							.getFinalizationSecratery().getUserId() != null)) {
	  				tabBean.setIcon("ui-icon-endorse");
	  			}
	  		}
	  		if (committeTabs.size() > 0) {
	  			// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
	  			this.getManagedBean().setSelectedTabBean(
	  					committeTabs.get(this.getManagedBean()
	  							.getSelectedCommitteeTabIndex()));
	  		}
	  		
	  		this.getManagedBean().setQt(scQt);
		
	}//31350-End
	
	private void retriveFulfilmentDtls(ScQt copy){//33916
		IQTSpecSO specSo = null;
		ScQt cloned=this.getManagedBean().getQt();
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(copy.getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);
			
			List<ScQtSchedule> scheduleList = new ArrayList<ScQtSchedule>();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
			
			if (null != resDTO.getScheduleResults()
					&& null != resDTO.getScheduleResults().getResultList()
					&& !resDTO.getScheduleResults().getResultList()
							.isEmpty()) {
				
				this.getManagedBean().setTotalFrequency(resDTO.getScheduleResults().getResultList().size());
				try{
					
					for (ScQtSchedule scQtSchedule : resDTO
							.getScheduleResults().getResultList()) {
						String startMonth = "";
						String endMonth = "";
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								CommonConstants.DEFAULT_DATE_FORMAT);
						int months;
						Date startDate = null;
						Date endDate = null;
						String fromdate = dateFormat.format(scQtSchedule
								.getFromDate());
						String todate = dateFormat.format(scQtSchedule
								.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
						String fromDay = fromdate.substring(0, 2);
	
						String enddate = todate.substring(3, 5);
						String endYear = todate.substring(6,
								todate.length());
						String toDay = todate.substring(0, 2);
	
						startMonth = fulfilmentMonthlyDtls(startdate);
						endMonth = fulfilmentMonthlyDtls(enddate);
	
						int sYear = Integer.parseInt(startYear);
						int eYear = Integer.parseInt(endYear);
	
						String fromDate = fromDay + "/" + startdate + "/"
								+ sYear;
						String toDate = toDay + "/" + enddate + "/" + eYear;
	
						startDate = dateFormat.parse(fromDate);
						endDate = dateFormat.parse(toDate);
	
						months = DateUtils.getMonths(startDate, endDate);
					
						ScQtSchedule cls=new ScQtSchedule();
						cls.setBrandNew(true);
						setUserDetails(cls);
						cls.setFromDate(scQtSchedule.getFromDate());
						cls.setToDate(scQtSchedule.getToDate());
						cls.setEndMonth(endMonth);
						cls.setEndYear(endYear);
						cls.setScheduleNo(scQtSchedule.getScheduleNo());
						cls.setScheduleType(scQtSchedule.getScheduleType());
						cls.setScQt(cloned);
						cls.setStartMonth(startMonth);
						cls.setStartYear(startYear);
						cls.setTotalMonths(new BigDecimal(months));
						scheduleList.add(cls);
				
					}
					
				} catch (ParseException e) {

				}
			}
			this.getManagedBean().setScheduleList(scheduleList);
			
			
			if (null != resDTO.getProgressivePaymentResults()
					&& null != resDTO.getProgressivePaymentResults()
							.getResultList()
					&& !resDTO.getProgressivePaymentResults()
							.getResultList().isEmpty()) {
				
				this.getManagedBean().setTotalProgressivePayment(
						resDTO.getProgressivePaymentResults()
								.getResultList().size());
				
				String startMonth = "";
				String endMonth = "";
				for (ScQtProgressivePayment payment : resDTO
						.getProgressivePaymentResults().getResultList()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");

					String fromdate = dateFormat.format(payment
							.getFromDate());
					String todate = dateFormat.format(payment.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6,
							fromdate.length());
					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());
					startMonth = fulfilmentMonthlyDtls(startdate);
					endMonth = fulfilmentMonthlyDtls(enddate);
					
					ScQtProgressivePayment clp =new ScQtProgressivePayment();
					
					clp.setBrandNew(true);
					clp.setScQt(cloned);
					setUserDetails(clp);
					clp.setFromDate(payment.getFromDate());
					clp.setToDate(payment.getToDate());
					clp.setPercentage(payment.getPercentage());
					clp.setPhaseDesc(payment.getPhaseDesc());
					clp.setPhaseNo(payment.getPhaseNo());
					clp.setTotalMonths(payment.getTotalMonths());
					
					clp.setStartMonth(startMonth);
					clp.setEndMonth(endMonth);
					clp.setStartYear(startYear);
					clp.setEndYear(endYear);
					
					paymentList.add(clp);

				}
				
				
			}
			this.getManagedBean().setProgressivePaymentList(paymentList);
			
			reqDTO.setQtId(cloned.getQtId());
			resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);
			
			if (null != resDTO.getScheduleResults()
					&& null != resDTO.getScheduleResults().getResultList()
					&& !resDTO.getScheduleResults().getResultList()
							.isEmpty()) {
				List<ScQtSchedule> deletes=new ArrayList<ScQtSchedule>();
				for(ScQtSchedule toDel:resDTO.getScheduleResults().getResultList()){
					toDel.setDeleted(true);
					deletes.add(toDel);
				}
				this.getManagedBean().setToBeRemoveSchedules(deletes);
			}
			
			if (null != resDTO.getProgressivePaymentResults()
					&& null != resDTO.getProgressivePaymentResults()
							.getResultList()
					&& !resDTO.getProgressivePaymentResults()
							.getResultList().isEmpty()) {
				List<ScQtProgressivePayment> deletes=new ArrayList<ScQtProgressivePayment>();
				for(ScQtProgressivePayment toDel:resDTO.getProgressivePaymentResults().getResultList()){
					toDel.setDeleted(true);
					deletes.add(toDel);
				}
				this.getManagedBean().setToBeRemoveProgPayments(deletes);
			}
			
		}finally{
			specSo = null;
		}
	}
	
	private String fulfilmentMonthlyDtls(String startdate) {
		String startMonth = "";
		try {
			if (startdate.equalsIgnoreCase("01")) {
				startMonth = "0";
			} else if (startdate.equalsIgnoreCase("02")) {
				startMonth = "1";
			} else if (startdate.equalsIgnoreCase("03")) {
				startMonth = "2";
			} else if (startdate.equalsIgnoreCase("04")) {
				startMonth = "3";
			} else if (startdate.equalsIgnoreCase("05")) {
				startMonth = "4";
			} else if (startdate.equalsIgnoreCase("06")) {
				startMonth = "5";
			}

			else if (startdate.equalsIgnoreCase("07")) {
				startMonth = "6";
			} else if (startdate.equalsIgnoreCase("08")) {
				startMonth = "7";
			} else if (startdate.equalsIgnoreCase("09")) {
				startMonth = "8";
			} else if (startdate.equalsIgnoreCase("10")) {
				startMonth = "9";
			} else if (startdate.equalsIgnoreCase("11")) {
				startMonth = "10";
			} else if (startdate.equalsIgnoreCase("12")) {
				startMonth = "11";
			}

		} finally {

		}
		return startMonth;
	}//33916
}

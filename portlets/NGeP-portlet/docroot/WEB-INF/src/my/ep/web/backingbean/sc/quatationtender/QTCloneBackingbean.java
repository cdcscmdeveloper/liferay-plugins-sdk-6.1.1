package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
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
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
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
			descendent1.setSelected(true);
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
			TreeNode descendent7 = new DefaultTreeNode(new QTSelectionNode(getLabel(PAYMENT),7L), child);
			descendent7.setParent(child);
			TreeNode descendent8 = new DefaultTreeNode(new QTSelectionNode(getLabel(DOCUMENT),8L), child);
			descendent8.setParent(child);
			TreeNode descendent9 = new DefaultTreeNode(new QTSelectionNode(getLabel(PUBLICATION_APPROVAL),9L), child);
			descendent9.setParent(child);
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
				if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statusId)){
					stats=getStatuses(QTStatusConst.QT_PENDING_QT_SUBMISSION,QTStatusConst.QT_PENDING_QT_SUBMISSION);
				}
				if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId)){
					stats=getStatuses(QTStatusConst.QT_PENDING_QT_SUBMISSION,QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
				}
				
				
				
				ArrayList<Long> s=new ArrayList<Long>(Arrays.asList(stats));
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
		debugLogger("cloneCommitteePopupAction");
		// logic to check whether committe will allow for clone or not..
		// checkIsSpecCommitteFormOrNot();
		getManagedBean().setCloneCommitteeSelectQt(null);

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid2",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}
		getManagedBean().getCloneSearchQTDataModel().resetData();

		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		RequestContext.getCurrentInstance().execute("screen3.show()");
		getManagedBean().setAutoUpdate(false);
		 getManagedBean().setSelectSearchcommitteeNode(new TreeNode[0]);
		 getManagedBean().setCommType(null);
	}
	
	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(
				context, "lbl");
		String message = bundle.getString(label);
		return message;
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {

		ScQt scQt = this.getManagedBean().getQt();
		if (scQt != null) {
			message += " @qtNo = " + scQt.getQtNo();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		/*
		 * else{ logger.info(message); }
		 */

	}

	/**
	 * On row clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		ScQt scQt = (ScQt) event.getObject();
		Long qtId = scQt.getQtId();
		String evaluationType = scQt.getEvaluationType();
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);
		this.showTree = true;
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
		ScQt selected=this.getManagedBean().getSelectedCloneTender();
		TreeNode[] nodes=this.selectedNodes;
		if(nodes.length==0){
			throw new EpException(MessageConst.ERROR_760495);
		}
		try {
			if(isCreation(nodes)){
				//refreshQT();
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
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
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
					
					if(selection.getValue().equals(5L)){
						retrieveBrief(copyScqt);
					}
					if(selection.getValue().equals(6L)){
						retrievePublishing(copyScqt);
					}
					if(selection.getValue().equals(7L)){
						retrieveDeductionPaymentTabDtl(scQt,copyScqt,QtDetailsSO);
					}
				}
				
			}

		} finally {

			QtDetailsSO = null;
		}
		RequestContext.getCurrentInstance().execute("screen3.hide()");
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
			
			ISearchResultBean<ScQtContactPerson> contactPersons = resDTO
					.getContactPersons();
			List<ScQtContactPerson> qtContactPersons = contactPersons
					.getResultList();
			if (qtContactPersons != null || !qtContactPersons.isEmpty()) {
				List<ScQtContactPerson> persons=new ArrayList<ScQtContactPerson>();
				for(ScQtContactPerson origin:qtContactPersons){
					ScQtContactPerson copy=populateContact(origin,SourcingConstants.OFFICER_TYPE_QT);
					persons.add(copy);
				}
			   this.getManagedBean().setScQtContactPersons(persons);
				
			}
			List<ScQtContactPerson> desks=resDTO.getDeskOfficers().getResultList();
			if (desks != null || !desks.isEmpty()) {
				List<ScQtContactPerson> persons=new ArrayList<ScQtContactPerson>();
				for(ScQtContactPerson origin:desks){
					ScQtContactPerson copy=populateContact(origin,SourcingConstants.OFFICER_TYPE_DOAA);
					persons.add(copy);
				}
				this.getManagedBean().setPmuserContactpersonList(persons);
			}
			//
		}finally{
			detailSO=null;
		}
	}
	
	private ScQtContactPerson populateContact(ScQtContactPerson origin,String type){
		ScQtContactPerson contactPerson = new ScQtContactPerson();
		//PmUser user = new FacesScopes().getUserSessionBean();
		contactPerson.setPhoneNo(origin.getPhoneNo());
		//contactPerson.setUser(user);
		contactPerson.setEmail(origin.getEmail());
		contactPerson.setFaxNo(origin.getFaxNo());
		contactPerson
				.setOfficerType(type);
		if(SourcingConstants.OFFICER_TYPE_DOAA.equals(type)){
			contactPerson.setUser(origin.getUser());
		}else{
			contactPerson.setUser(new PmUser());
		}
		
		contactPerson.setName(origin.getName());
		contactPerson.setScQt(this.getManagedBean().getQt());
		return contactPerson;
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
				copydtl.setBsvType(bsvDtl.getBsvType());
				copydtl.setBsvDesc(bsvDtl.getBsvDesc());
				copydtl.setBsvDate(bsvDtl.getBsvDate());
				copydtl.setAddress(bsvDtl.getAddress());
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
		
		copy.setBsvRemarks(initializeRemarks(copy, this
				.getManagedBean().getQt()
				.getCurrentWorkFlowStatus(), copy.getBsvRemarks(),
				ScQtBSVRemark.class));
		
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
			
			if (!copyScqt.isZonal()) {
				ScQtBsv qtBsv = resDTO.getScQtBsv();
				BriefingZoneBean briefingZoneBean=mapBrief(qt,qtBsv);
				this.getManagedBean().setBsvDtlBean(briefingZoneBean);
			}else{
				
//				List<TplSpecZoneDtl> zones = resDTO.getZonesList();
//				List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
//				if(null!=zones){
//					BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
//					briefingReqDTO.setQtId(copyScqt
//							.getQtId());
//					briefingReqDTO.setScQt(getManagedBean().getQt());
//					briefingReqDTO.setMaintanceFlow(true);
//					//briefingReqDTO.setAmendMentBean(amendMentBean);
//					for (TplSpecZoneDtl specZoneDtl : zones) {
//						briefingReqDTO.setSpecZoneId(specZoneDtl
//								.getSpecZoneId());
//						BriefingResDTO briefingResDTO = qTBriefingSO
//								.retrieveBriefingSiteVisitDtls(briefingReqDTO);
//						ScQtBsv qtBsv = briefingResDTO.getScQtBsv();
//						
//						BriefingZoneBean briefingZoneBean=mapBrief(qt,qtBsv);
//						zoneBeans.add(briefingZoneBean);
//					}
//					
//				}
//				this.getManagedBean().setBriefingZoneBean(zoneBeans);
//				this.getManagedBean().setBsvDtlBean(zoneBeans.get(0));
			}
			
		}finally{
			qTBriefingSO = null;
		}
	}
	
	
	private ScQt retrieveApproval(ScQt scQt,ScQt copyScqt,IQTDetailSO QtDetailsSO){
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		copyScqt=retrieveScqtCommittee(copyScqt);
		tenderReqDTO.setQtdetail(copyScqt);
		tenderReqDTO.setAvaliableApprovals(copyScqt.getScQtApprovals());
		tenderReqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
		tenderReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
		QuationTenderResDTO resDTO = QtDetailsSO.retriveQTApprovalDetails(tenderReqDTO);
		List<ScQtApproval> requiredApproval = resDTO.getApproval();
		List<ScQtApproval> approvals=new ArrayList<ScQtApproval>();
		List<ScQtApproval> newApprovals=new ArrayList<ScQtApproval>();
		for(ScQtApproval approval:requiredApproval){
			if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
					.equalsIgnoreCase(approval.getApprovalCategory()
							.getParameterCode())) {
				
				approvals.add(approval);
				
			}
		}
		
		for(ScQtApproval copyApproval:approvals){
			ScQtApproval newApproval= new ScQtApproval();
			newApproval.setBrandNew(true);
			newApproval.setApprovalCategory(copyApproval.getApprovalCategory());
			newApproval.setRecordStatusFlg(copyApproval.getRecordStatusFlg());
			newApproval.setRecordStatus(copyApproval.isRecordStatus());
			newApproval.setScQt(scQt);
			setUserDetails(newApproval);
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
			
			if(copyApproval.getScQtApprovalDtls()!=null && !copyApproval.getScQtApprovalDtls().isEmpty()){
				List<ScQtApprovalRequest> approverRequest=new ArrayList<ScQtApprovalRequest>();
				for(ScQtApprovalRequest request:copyApproval.getScQtApprovalDtls()){
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
				}
				newApproval.setScQtApprovalDtls(approverRequest);
			}
			
			
			List<TabBean> tabBeans = new ArrayList<TabBean>();
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
			
			
			newApprovals.add(newApproval);
		}
		scQt.setScQtApprovals(newApprovals);
		
		
		
		return scQt;
	}
	
	public void retrieveDeductionPaymentTabDtl(ScQt scQt,ScQt copyScqt,IQTDetailSO detailSO) {
		
		
		debugLogger("retrieveDeductionPaymentTabDtl");
		if (this.getManagedBean().getScQtDeductionList() == null || this.getManagedBean().getScQtDeductionList().isEmpty()) {
			
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
					setUserDetails(obj);
					deductionList.add(obj);
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
		}
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
      log.debug("removeDeskOfficer");
		if (CollectionUtils.isNotEmpty(approvers)) {
			PmUser currentDO = new FacesScopes().getUserSessionBean();
			//boolean isDO = false;
			//int index = 0;
			for (PmUser pmUser : approvers) {
				if (pmUser.getUserId().equals(currentDO.getUserId())) {
					log.info("loopremoveDeskOfficer");
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
		log.debug("endremoveDeskOfficer");
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
			
			ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
			if(null!=scQt.getScQtSupplierCriteria()){
				supplierCriteria =scQt.getScQtSupplierCriteria();
			}
			
			supplierCriteria.setScQt(scQt);
			supplierCriteria.setBrandNew(true);
			if(cps!=null){
				if(cps.getLocationLevel()!=null){
					supplierCriteria.setLocationLevel(cps.getLocationLevel());
				}
				
				//to Delete
				List<ScQtLocCriteria> tobeRemoveLocCriteriaList=new ArrayList<ScQtLocCriteria>();
				if(null!=supplierCriteria.getScQtLocCriteria() && !supplierCriteria.getScQtLocCriteria().isEmpty()){
					for(ScQtLocCriteria cloc:cps.getScQtLocCriteria()){
						cloc.setDeleted(true);
						tobeRemoveLocCriteriaList.add(cloc);
					}
					this.getManagedBean().setSupplierLocationList(tobeRemoveLocCriteriaList);
				}
				
				if(cps.getScQtLocCriteria()!=null){
					List<ScQtLocCriteria> toAdd=new ArrayList<ScQtLocCriteria>();
					for(ScQtLocCriteria cloc:cps.getScQtLocCriteria()){
						ScQtLocCriteria loc=new ScQtLocCriteria();
						loc.setScQtSupplierCriteria(supplierCriteria);
						loc.setBrandNew(true);
						loc.setDocId(cloc.getDocId());
						loc.setDocType(cloc.getDocType());
						loc.setPmDistrict(cloc.getPmDistrict());
						loc.setPmDivision(cloc.getPmDivision());
						loc.setPmState(cloc.getPmState());
						loc.setReasonForUpdateEnabled(cloc.isReasonForUpdateEnabled());
						loc.setRecordStatus(cloc.isRecordStatus());
						loc.setRecordStatusFlg(cloc.getRecordStatusFlg());
						loc.setSpecLocId(cloc.getSpecLocId());
						loc.setZonalLocation(cloc.isZonalLocation());
						toAdd.add(loc);
					}
					supplierCriteria.setScQtLocCriteria(toAdd);
				}
				supplierCriteria.setAllState(cps.isAllState());
				
				
			}
			

			if(copyScqt.isZonal()){
				ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
				qtZoneCriterias.setBrandNew(true);
				qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
				if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
					qtZoneCriterias.setTplSpecZoneDtls(cps.getQtZoneCriterias().getTplSpecZoneDtls());
				}
				supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
			}
			
			scQt.setScQtSupplierCriteria(supplierCriteria);
			this.getManagedBean().setSupplierCriteria(scQt.getScQtSupplierCriteria());
			List<ScQtCategory> categoryResults = resDTO
					.getCategories();
			List<ScQtSupplier> restrictResults=resDTO.getRestrictedSuppliers();
			
			
			//to Delete
			List<ScQtCategory> tobeRemoveCategoryCodes=new ArrayList<ScQtCategory>();
			if(scQt.getScQtCategorys()!=null && !scQt.getScQtCategorys().isEmpty()){
				for(ScQtCategory toDel:scQt.getScQtCategorys()){
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
		debugLogger("retriveZonesByCircularNo");
		
		
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
		if(null==scQt.getFileNo()){
			scQt.setFileNo(null);
			scQt.setBrandNew(true);
		}
		
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
		
		scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
	//	scQt.setEstimatedAmount(copyScqt.getEstimatedAmount());
	//	scQt.setDecryptedEstAmt(copyScqt.getDecryptedEstAmt());
	//	scQt.setDecExpectedIntendedPrice(copyScqt.getDecExpectedIntendedPrice());
		
		
		
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
		scQt.setPublishPeriod(copyScqt.getPublishPeriod());
		scQt.setQtValidityPeriod(copyScqt.getQtValidityPeriod());
		
		scQt.setSpecApprovalReq(copyScqt.getSpecApprovalReq());
		scQt.setMinistrySpecSecrataryId(copyScqt.getMinistrySpecSecrataryId());
		
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
		
		if (this.getManagedBean().getQt().isZonal()) {
			
			if (null != this.getManagedBean().getQt()
					.getZoneLocationLevel()) {
				this.getManagedBean().setStateCodes(
						this.getManagedBean().getQt()
								.getZoneLocationLevel()
								.getParameterCode());
			}
			
				ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
				
				ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
				supplierCriteria.setScQt(scQt);
				supplierCriteria.setBrandNew(true);
				supplierCriteria.setLocationLevel(new PmParameter());
				supplierCriteria.setScQtLocCriteria(null);
				//supplierCriteria.setAllState(cps.isAllState());
				if (supplierCriteria.isAllState() == true) {
					this.getManagedBean().setZoneTrees(null);
				}
				
				ScQtZoneCriteria qtZoneCriterias =new ScQtZoneCriteria();
				qtZoneCriterias.setBrandNew(true);
				qtZoneCriterias.setScQtSupplierCriteria(supplierCriteria);
				if(cps!=null && cps.getQtZoneCriterias()!=null && cps.getQtZoneCriterias().getTplSpecZoneDtls()!=null){
					qtZoneCriterias.setTplSpecZoneDtls(cps.getQtZoneCriterias().getTplSpecZoneDtls());
				}
				supplierCriteria.setQtZoneCriterias(qtZoneCriterias);
				scQt.setScQtSupplierCriteria(supplierCriteria);
				List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
				
				populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
				
			
			
		}else{
			ScQtSupplierCriteria cps =copyScqt.getScQtSupplierCriteria();
			
			ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
			supplierCriteria.setScQt(scQt);
			supplierCriteria.setBrandNew(true);
			supplierCriteria.setLocationLevel(cps.getLocationLevel());
			supplierCriteria.setScQtLocCriteria(cps.getScQtLocCriteria());
			supplierCriteria.setAllState(cps.isAllState());
			supplierCriteria.setLocationLevel(cps.getLocationLevel());
			if (supplierCriteria.isAllState() == true) {
				this.getManagedBean().setZoneTrees(null);
			}
		}
		
		if (scQt.getObtainSpecialApproval() != null && !scQt.getObtainSpecialApproval()) {
			this.getManagedBean().setPtjSpecialApproval(
					getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));
							
		} else {
			this.getManagedBean().setPtjSpecialApproval(scQt.getPtjSpecialApproval());
		}
		
		if (null != getManagedBean().getQt()
				&& null == getManagedBean().getQt()
						.getProcurementTypeCatId()) {
			getManagedBean().getQt().setProcurementTypeCatId(
					new PmParameter());
		}
		
		if(copyScqt.isPanel()){
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		CreateQtBackingBean createQtBB = (CreateQtBackingBean) elContext.getELResolver().getValue(elContext, null, "createQtBB");
		createQtBB.changePanelGroupItem();
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

		debugLogger("setProcurementMode,@qtNo = " + scQt.getQtNo());
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
				
			debugLogger("setProcurementMode, @procurementMode = "
					+ scQt.getProcurementMode());
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
		
		debugLogger("populateEvaluationTypes,@qtNo = "+scQt.getQtNo());
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

		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}
	
	/**
	 * Reset clone.
	 */
	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
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

}

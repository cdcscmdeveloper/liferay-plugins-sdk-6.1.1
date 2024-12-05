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
package my.ep.web.backingbean.sc.specification;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.CreateQtBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class SpecificationBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "specMaintenanceBB")
@ViewScoped
public class SpecificationMaintenanceBackingBean extends
SourcingBaseBackingBean<QTDetailManagedBean> implements Serializable{

private static final long serialVersionUID = 1L;

private static Logger log = Logger
.getLogger(SpecificationMaintenanceBackingBean.class);

//private IPagingDataModel<ScQt> specMaintenanceDataModel = new SpecMaintenanceSearchQtDataModel();


public SpecificationMaintenanceBackingBean(){
	
}

@PostConstruct
public void  init(){
	
}

/**
 * The Class SpecMaintenancePagingDataModel.spec maintenance paging data
 * model.
 */
private class SpecMaintenanceSearchQtDataModel extends
		PagingDataModel<ScQt> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new SpecMaintenance paging data model.
	 */
	public SpecMaintenanceSearchQtDataModel() {

		super("specMaintenancesearchQTs",false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
	 * .biz.bean.search.ISearchBean)
	 */
	public ISearchResultBean<ScQt> pageChangeListener(
			ISearchBean<ScQt> searchBean) {
		
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			populateQTSearchCriteria(reqDTO, getManagedBean().getQtnTenderSearchBean(), searchBean);
			QuationTenderResDTO resDTO = detailSO
					.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
			
			
			// set search result doc type for navigate to diff page based on diff doc type selected.
			getManagedBean().getQtnTenderSearchBean().setResultDocType(getManagedBean().getQtnTenderSearchBean().getDocType());
			if(resDTO.isSuccessful()){
			return resDTO.getQtResultBean();
			}
		} finally {
			detailSO = null;
		}
		return null;

	}
}

/**
 * Method to prepare qt search criteria.
 * 
 * @param qtnTenderSearchBean
 *            the qt search criteria bean
 * @param qtSearchBean
 *            the qt search filter bean
 */
private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO,
		QtnTenderSearchBean qtnTenderSearchBean,
		ISearchBean<ScQt> qtSearchBean) {
	if (qtnTenderSearchBean != null) {
		if (CollectionUtils.isNotEmpty(qtSearchBean
				.getDataFilterBeans())) {
			qtSearchBean.getDataFilterBeans().clear();
		}
		List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
		String qtTitle = qtnTenderSearchBean.getQtTitle();
		if (StringUtils.isNotEmpty(qtTitle) && StringUtils.isNotEmpty(qtTitle)) {
			searchFilterBean.add(
					new SearchFilterBean(QTConstants.QT_TITILE,
							SearchOperator.OPERATOR_LIKE.value(), qtTitle,
							true));
		}
		
		// docType & docNo
		String docType = qtnTenderSearchBean.getDocType();
		String docNo = qtnTenderSearchBean.getDocNo();
		if (StringUtils.isNotEmpty(docType)) {
			if (ParameterConst.DOCTYPE_QUOTATION.equals(docType)) {
				if (!StringUtils.isBlank(docNo)) {
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField(QTConstants.QT_NUMBER);
					bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
					bean.setSearchValue(docNo);
					searchFilterBean.add(bean);	
				}
			} else {
				reqDTO.setDocType(docType);
				reqDTO.setDocNo(docNo);
			}
		}
		String procurementMode = qtnTenderSearchBean.getProcurementMode();
		if (StringUtils.isNotBlank(procurementMode) && StringUtils.isNotEmpty(procurementMode)) {
			searchFilterBean.add(
					new SearchFilterBean("procurementMode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							procurementMode));
		} else {
			// Filter By - procurementMode
			List<String> procurementModes = new ArrayList<String>();
			procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
			procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
			searchFilterBean.add(
					new SearchFilterBean(
							"procurementMode.parameterCode",
							SearchOperator.OPERATOR_IN.value(), 
							procurementModes));
		}
		
		Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
		Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
		if (null != fromCreatedDate || toCreatedDate != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(fromCreatedDate);
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
					c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			fromDate.set(Calendar.MILLISECOND, 0);
			fromCreatedDate = fromDate.getTime();
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
		Date fromPublishDate = qtnTenderSearchBean.getFromPublishDate();
		Date toPublishDate = qtnTenderSearchBean.getToPublishDate();
		if (null != fromPublishDate || null != toPublishDate) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(fromPublishDate);
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
					c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			fromDate.set(Calendar.MILLISECOND, 0);
			fromPublishDate = fromDate.getTime();
			if (toPublishDate != null) {
				Calendar c2 = Calendar.getInstance();
				c2.setTime(toPublishDate);
				Calendar toDate = Calendar.getInstance();
				toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				toDate.set(Calendar.MILLISECOND, 0);
				toPublishDate = toDate.getTime();
			}
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField(QTConstants.PUBLISH_DATE);
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					fromPublishDate, toPublishDate);
			bean.setSearchValue(betweenVal);
			searchFilterBean.add(bean);
		}
		Date fromClosingDate = qtnTenderSearchBean.getFromClosingDate();
		Date toClosingDate = qtnTenderSearchBean.getToClosingDate();
		if (null != fromClosingDate || null != toClosingDate) {
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField(QTConstants.CLOSED_DATE);
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					fromClosingDate, toClosingDate);
			bean.setSearchValue(betweenVal);
			searchFilterBean.add(bean);
		}
		
		String status = qtnTenderSearchBean.getQtStatus();
		if (!StringUtils.isBlank(status)) {
			searchFilterBean.add(new SearchFilterBean(
							"scQtStatus.pmStatus.statusId",
							SearchOperator.OPERATOR_EQUALS.value(), status));
			searchFilterBean.add(
					new SearchFilterBean("scQtStatus.current",
							SearchOperator.OPERATOR_EQUALS.value(), true));
		} else {
			searchFilterBean.add(new SearchFilterBean(
					"scQtStatus.pmStatus.statusId",
					SearchOperator.OPERATOR_NOT_IN.value(),nonRecommendedStatusesForSpecMaint()));
		}
			

                 searchFilterBean.add(
		new SearchFilterBean("scQtStatus.current",
				SearchOperator.OPERATOR_EQUALS.value(), true));
            reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			 if(isSpecCommitteeSecretary()){
				reqDTO.setRoleCode(RoleCode.SC_SECRETARY.value());
			}
			PmOrgProfile ptjProfiles = qtnTenderSearchBean.getPtjProfile();
			if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
				// Filter By - PTJ
				searchFilterBean.add(new SearchFilterBean(
						"pmOrgProfile." + OrgProfileUtil
							.getSearchBeanParentString(OrganizationType.DIVISION.value()),
						SearchOperator.OPERATOR_EQUALS.value(),
						ptjProfiles.getOrgProfileId()));
			} else {
				// Filter By - PTJ Group
				PmOrgProfile ptjGroupProfile = qtnTenderSearchBean.getPtjGroupProfile();
				if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
					searchFilterBean.add(new SearchFilterBean(
							"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjGroupProfile.getOrgProfileId()));
				} else {
					// Filter By - Ministry
					PmOrgProfile ministryProfile = qtnTenderSearchBean.getMinistryProfile();
					if (ministryProfile != null
							&& ministryProfile.getOrgProfileId() != null) {	
						searchFilterBean.add(new SearchFilterBean(
								"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryProfile.getOrgProfileId()));
					}
				}
				
			}
		

		// Adding search filterbeans to searchbean.
		qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		
		reqDTO.setQtSearchBean(qtSearchBean);
	}
}

/**
 * Method to search the quationtenders based on serach date.
 */
public void searchSpeMaintenanceQTAction() {
	this.getManagedBean().getSearchQTDataModel().reloadData();
}

/**
 * specCommittee maintenance data rest.
 */
public void specCommitteeMaintenancereset() {
	QTDetailBackingBean detailBackingBean=new QTDetailBackingBean();
	detailBackingBean.reset();
	this.getManagedBean().getSearchQTDataModel().resetData();
}

	/* ***25692#95 - start */
	public void nullifyRelatedBackingBean() {
		nullifyManagedBean();
		
		FacesScopes fs = new FacesScopes();
		
		if (fs.getRequestScope("createQtBB") != null) {
			((CreateQtBackingBean) fs.getRequestScope("createQtBB")).nullifyManagedBean();
		}
		
		if (fs.getRequestScope("qTDetailBB") != null) {
			((QTDetailBackingBean) fs.getRequestScope("qTDetailBB")).nullifyManagedBean();
		}
	}
	/* ***25692#95 - end */
	
	/* ***25692#95 - start */
	public void switchToTempManagedBean() {
		FacesScopes fs = new FacesScopes();
		fs.setSessionScope(getTempManagedBeanName(), (QTDetailManagedBean) fs.getSessionScope(getManagedBeanName()));
		
		QTDetailManagedBean mb = new QTDetailManagedBean();
		
		try {
			mb = (QTDetailManagedBean) BeanUtils.cloneBean(this.getManagedBean());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		fs.setSessionScope(getManagedBeanName(), mb);
		
		nullifyRelatedBackingBean();
	}
	/* ***25692#95 - end */
	
	/* ***25692#95 - start */
	public void switchToOrigManagedBean() {
		// boolean setOtherActiveIndex = false;
		FacesScopes fs = new FacesScopes();
		//QTDetailManagedBean mb1 = (QTDetailManagedBean) fs.getSessionScope(getManagedBeanName());
		//QTDetailManagedBean mb2 = (QTDetailManagedBean) fs.getSessionScope(getTempManagedBeanName());

		fs.setSessionScope(getManagedBeanName(), (QTDetailManagedBean) fs.getSessionScope(getTempManagedBeanName()));
		fs.removeFromSession(getTempManagedBeanName());

		nullifyRelatedBackingBean();
	}
	/* ***25692#95 - end */
	
	/* ***25692#95 - start */
	public String backToQtScreen() {
		// 29464 -start-
		String destPage = this.getManagedBean().getPreviousPage() == null ? QTConstants.MAINTANANCE_PAGE_FULL : this
				.getManagedBean().getPreviousPage();
		// 29464 -end-

		switchToOrigManagedBean();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, destPage); // 29464
		
		return "";
	}
	/* ***25692#95 - end */
	
	/* ***25692#95 - start */
	public void editSpecificationMaintenanceNewManagedBean(ScQt scQt, boolean isEditable, String previouPageId) {
		this.switchToTempManagedBean();
		
		editSpecificationMaintenance(this.getManagedBean().getQt(), isEditable, true, true, previouPageId);
	}
	/* ***25692#95 - end */

	/**
	 * The method  used to get the specificationMaintenance edit details.
	 * @param ScQt scQt.
	 * @param Boolean true/false.
	 */
	public void editSpecificationMaintenance(ScQt scQt, boolean isEditable) {
		/*** 25692#95 ** -start- */
		editSpecificationMaintenance(scQt, isEditable, true, false, null);
		/*** 25692#95 ** -end- */
	}
	
	/**
	 * The method used to get the specificationMaintenance edit details.
	 * 
	 * @param scQt
	 * @param isEditable
	 * @param popup
	 */
	public void editSpecificationMaintenance(ScQt scQt, boolean isEditable, boolean popup) {
		editSpecificationMaintenance(scQt, isEditable, true, false, null);
		if (popup) {
			this.assertSpecCommitteeMaintenanceInProgress(scQt, false);
			if (this.getManagedBean().getInactiveSCMemberStr().length() > 0) {
				SpecificationBackingBean specBB = new SpecificationBackingBean();
				// temporary while awaiting #29474 close in SIT -start-
				// remove when #29474 ready for UAT
				specBB.qtChecklistTabChange(9);
				// temporary while awaiting #29474 close in SIT -end-

				// use this when #29474 ready for UAT -start-
				// specBB.qtChecklistTabChange(10);
				// use this when #29474 ready for UAT -end-
				// RequestContext.getCurrentInstance().execute("inactiveSCMembersPopup.show()");
			}
		}
		// 25037 - end - note 510
	}

	/**
	 * The method  used to get the specificationMaintenance edit details.
	 * @param ScQt scQt.
	 * @param Boolean true/false.
	 * @param renewSessionObject
	 * @param Boolean useTempManagedBean 
	 * @param previousPageId - nullable
	 */
	// 29464-start
	public void editSpecificationMaintenance(ScQt scQt, boolean isEditable,
			boolean renewSessionObject, boolean useTempManagedBean,
			String previousPageId) {
		editSpecificationMaintenance(scQt, isEditable, renewSessionObject,
				useTempManagedBean, previousPageId, false);
	}
	// 29464-end

	/*** 25692#95 ** -start- */
	public void editSpecificationMaintenance(ScQt scQt, boolean isEditable,
			boolean renewSessionObject, boolean useTempManagedBean,
			String previousPageId, boolean redirect/* 29464-overload arg */) {
	/*** 25692#95 ** -end- */
	
		QtnTenderSearchBean qtTnderBean = this.getManagedBean().getQtnTenderSearchBean();
		IPagingDataModel<ScQt> qtdataModel=this.getManagedBean().getSearchQTDataModel();
	
		/* ***25692#95 - start */
		if (renewSessionObject) {
			renewSesionObject();
		}
		/* ***25692#95 - end */
	
		this.getManagedBean().setQtnTenderSearchBean(qtTnderBean);
		this.getManagedBean().setSearchQTDataModel(qtdataModel);
		this.getManagedBean().setMaintanceFlow(true);

		FacesContext fc = FacesContext.getCurrentInstance();
		boolean specCommitteeSecreatry = isSpecCommitteeSecretary();

		this.getManagedBean().setAssignedState(Boolean.FALSE);
		/* ***25692#95 - start */
		this.getManagedBean().setPreviousPage(StringUtils.isEmpty(previousPageId) ? null : previousPageId);
		/* ***25692#95 - end */
	    //#15391 -start-
	    this.getManagedBean().setGenInfo(false);
	    this.getManagedBean().setChecklist(false);
	    //#15391 -end-

		// 1873 note 125 start
		ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT,
				scQt.getQtId());
		this.getManagedBean().setSpecMaintenanceWorkFlowStatus(workFlowStatus);
		// 1873 note 125 end

		if (specCommitteeSecreatry) {
			if (isEditable) {
				if (null == workFlowStatus
						|| QTStatusConst.SE_PENDING_SUBMISSION
								.equals(workFlowStatus.getPmStatus().getStatusId())
						|| QTStatusConst.SE_COMPLETED.equals(workFlowStatus.getPmStatus().getStatusId())) {
					this.getManagedBean().setReadOnly(Boolean.FALSE);
					this.getManagedBean().setSpecMaintenance(Boolean.TRUE);
				} else {
					this.getManagedBean().setReadOnly(Boolean.TRUE);
					this.getManagedBean().setSpecMaintenance(Boolean.FALSE);
				}
			} else {
				this.getManagedBean().setReadOnly(Boolean.TRUE);
				this.getManagedBean().setSpecMaintenance(Boolean.FALSE);
			}
		} else {
			this.getManagedBean().setReadOnly(Boolean.TRUE);
		}
	
		SpecificationBackingBean specBB = new SpecificationBackingBean();
		specBB.retriveSpecdetails(scQt.getQtId().longValue());
		
		ScQt scQt_ = this.getManagedBean().getQt();
		
		if (null == scQt_.getContractType()) {
			scQt_.setContractType(new PmParameter());
		}

		if (null != scQt_.getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(scQt_.getFulfilmentType()
							.getParameterCode());
		}
		specBB.retriveTechnicalCheckList();//#11104-start
		List<ScQtCheckList> technicalChekclist= this.getManagedBean().getScQtCheckSt2techProposal();
		boolean isSampleWip=specBB.checkSampleRadioShowHideInSpecPrepareAndMaintenance();//11104Wip
		if(null!=technicalChekclist && CollectionUtils.isNotEmpty(technicalChekclist)){
		for(ScQtCheckList checkList: technicalChekclist){
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
				.equalsIgnoreCase(checkList.getDefinationType()
						.getParameterCode())) {
			scQt_.setSampleReq(Boolean.TRUE);//#11104
			checkList.setSampleWip(isSampleWip);//11104Wip
			break;

		}
		}
		}//#11104-End

		this.getManagedBean().setCurrentStatusId(scQt.getQtStatus().getStatusId());
		this.getManagedBean().setSpecMaintenanceScreen(QTConstants.SPEC_MAINTENANCE);
		this.getManagedBean().setLoginRoleType("SC");
		
		ScWorkFlowStatus specMainworkflow = this.getManagedBean().getSpecMaintenanceWorkFlowStatus();
		
		if (null != specMainworkflow) {
			Long currentSpecStatusId = specMainworkflow.getPmStatus().getStatusId();

			if(QTStatusConst.SE_COMPLETED.equals(currentSpecStatusId)){
				currentSpecStatusId = scQt_.getQtStatus().getStatusId();
			}

			this.getManagedBean().setCurrentStatusId(currentSpecStatusId);
		}		
		
		/*** 25692#95 - start ***/
		if (useTempManagedBean) {
			this.getManagedBean().setReadOnly(!isEditable);

			nullifyRelatedBackingBean();
		}
		/*** 25692#95 - end ***/
		
		// #15391 - start
		if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId())
				|| QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId())) {
			this.getManagedBean().setGenInfo(true);

			if (QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_REVISION.equals(this.getManagedBean().getCurrentStatusId())) {
				this.getManagedBean().setGenInfo(false);
			}
		}

		if ((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_PUBLICATION_REVISION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				// #33837 - add bpk and reschedule -start-
				|| (QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(scQt.getQtStatus().getStatusId()))
				// #33837 - add bpk and reschedule -end-
		) {
			this.getManagedBean().setChecklist(true);

			if (QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_REVISION.equals(this.getManagedBean().getCurrentStatusId())) {
				this.getManagedBean().setChecklist(false);
			}
		}
		// #15391 - end

		// 29464-start
		String renderPage = QTConstants.SPECIFICICATON_COMM_PREPARATION;

		if (redirect) {
			renderPage = renderPage.concat("?faces-redirect=true");
		}

	this.getManagedBean().setSampleShowHide(isSampleWip);//11104Wip
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, renderPage);
		// 29464-end
	}

/**
 * Get status select items by module and doc type for dropdown.
 * 
 *@param parameterType
 *            the parameter type
 * @param searchBean
 *            the search bean instance
 * @return List<PmParameterDesc>
 * 
 */
/*public SelectItem[] getStatusDropdownValuesSpecCommittee(String moduleCode,
		String docType) {
	SelectItem[] items = null;
	FacesScopes face = new FacesScopes();
	List<PmStatusDesc> localstatusDescs=new ArrayList<PmStatusDesc>();
	try {
		List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
				.getStatusDescList(moduleCode, docType);
		for (PmStatusDesc statusDesc : statusDescs) {
			if(statusDesc.getPmStatus().getStatusId()>=QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT){
				localstatusDescs.add(statusDesc);
			}
		}
		items = new SelectItem[localstatusDescs.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				face.getUserLocale()));
		int i = 1;
		for (PmStatusDesc statusDesc : localstatusDescs) {
			items[i++] = new SelectItem(statusDesc.getPmStatus()
					.getStatusId(), statusDesc.getStatusName());
			}
		
	} catch (Exception e) {
		e.printStackTrace();
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
						e.getMessage()));
	}

	return items;
}*/

/**
 * Goto specMaintenance search page.
 * 
 * @return the string
 */
public String gotoSpecMaintenanceSearchPage(){
//renewSesionObject();
return QTConstants.SPEC_MAINTENANCE_SEARCH_PAGE;	
}

private boolean isSpecCommitteeSecretary() {
	FacesScopes facesScopes = new FacesScopes();
	return facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value());
}

/**
 * @return the specMaintenanceDataModel
 */
public IPagingDataModel<ScQt> getSpecMaintenanceDataModel() {
	IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
			.getSearchQTDataModel();
	if (searchQTDataModel == null) {
		searchQTDataModel = new SpecMaintenanceSearchQtDataModel();
		getManagedBean().setSearchQTDataModel(searchQTDataModel);
	}
	return searchQTDataModel;
}

private ScWorkFlowStatus retrieveStatusByTypeAndId(String docType, Long docId) {
	log.debug("Start retrieveStatusByTypeAndId");
	IScWorkFlowStatusSO workFlowStatusSO;
	try {
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(docType);
		reqDTO.setDocId(docId);
		
		return workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
	} finally {
		workFlowStatusSO = null;
	}
}

	// 1873 note 125 start
	public boolean renderIcon(ScQt qt) {
		Long qtStatusId = (qt != null && qt.getQtStatus() != null) ? qt.getQtStatus().getStatusId() : 0L;
		Long specMaintenanceStatusId = (qt != null && qt.getSpecMaintenanceStatus() != null) ? qt
				.getSpecMaintenanceStatus().getStatusId() : 0L;
		boolean isRenderIcon = false;

		if (qtStatusId != null) {
			isRenderIcon = checkStatus(qtStatusId);
		}

		return isRenderIcon
				&& !Arrays.asList(
						new Long[] { QTStatusConst.SE_PENDING_ENDORSMENT, QTStatusConst.SE_PENDING_SIGNOFF,
								QTStatusConst.SE_PENDING_REVISION }).contains(specMaintenanceStatusId);
		// 1873 note 125 end
	}

	private boolean checkStatus(Long statusId) {
		// 25037 - start
		if (QTStatusConst.QT_PENDING_PUBLICATION.equals(statusId)) {
			return false;
		}
		// 25037 - end
		return isQTStatusInBetween(statusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT,
				QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);

	}

	// #15391 - start
	public boolean isSpecCommittee() {
		return QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #15391 - end

	// 33837 - start
	public boolean isSpecCommitteeSecretaryAndSpecCommMaintPortlet() {
		boolean isSpec = false;
		FacesScopes facesScopes = new FacesScopes();

		if (facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value())
				&& QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())) {
			isSpec = true;
		}
		
		return isSpec;
	}
	// 33837 - end

	// 29464-start
	public void redirectToSpecificationMaintenance(ScQt scQt, boolean isEditable, String previouPageId, boolean redirect) {
		this.switchToTempManagedBean();
		this.getManagedBean().setQt(scQt);
		
		editSpecificationMaintenance(scQt, isEditable, true, true, previouPageId, redirect);
	}
	// 29464-end

}

/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 * @author User
 *
 */
@ManagedBean(name = "createQtBB")
@RequestScoped
public class CreateQtBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(CreateQtBackingBean.class);

	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public CreateQtBackingBean() {
		log.info("inside constructor");
		
		log.info("@@@@@@@@@"+this.getManagedBean());

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

	
	public String constrcutFieldsForPanel() {

		StringBuffer conditionString = new StringBuffer();
		conditionString.append("ctPanelItems.qt^1^");
		conditionString.append(SearchOperator.OPERATOR_LIKE.value());
		conditionString.append("|");
		conditionString.append("docType^");
		conditionString.append(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		// conditionString.append("|");

		Date todayDate = new Date();
		Calendar c1 = Calendar.getInstance();
		c1.setTime(todayDate);
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
				c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		fromDate.set(Calendar.MILLISECOND, 0);
		/*
		 * conditionString.append("endDate^");
		 * conditionString.append("28-JAN-2016");
		 */

		return conditionString.toString();
	}
	
	public void circularListSelect(CtCircularVer circular) {
			
		if(null != circular){
			this.getManagedBean().getQt().setCircularVer(circular);
			
				this.getManagedBean().setPanelGroupItems(getPanelGroupItemsByCirculerVer(circular));
				this.getManagedBean().setCircularType(circular.getDocType());
						
		}

	}
	
	/**
	 * Panel conformation.change panel(Yes/No) radion button in business owner.
	 * 
	 * @param event
	 *            (boolean panelType).
	 */
	public void panelConformation(ScQt scQt) {
		
		Boolean isPanel = this.getManagedBean().getQt().isPanel();
		/*Boolean isPanel = (Boolean) event.getComponent().getAttributes()
				.get("value");*/
		scQt.setPanel(isPanel);
		
		if (!isPanel) {
			
			ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtSupplierCriteriaId()
					&& (isPanel != this.getManagedBean().isPanelChange())) {
				RequestContext.getCurrentInstance().execute("panelApprovalDlgs.show()");

			} else {
				scQt.setCircularVer(null);
				scQt.setQtType(null);
				scQt.setZonal(false);
				scQt.setPanelItemId(null);
				scQt.setItemGroup(null);
				//this.getManagedBean().setCtPanelItem(null);
				this.getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
				
			}
		}

	}

	/**
	 * Panel no conformation.change panel yes to No or No to yes Confirmation .
	 */
	public void panelNoConformation() {
		Boolean panel = this.getManagedBean().getQt().isPanel();
		if (panel) {
			this.getManagedBean().getQt().setPanel(false);
		} else {
			this.getManagedBean().getQt().setPanel(true);
			circularListSelect(this.getManagedBean().getQt().getCircularVer());
		}

	}
	
	public void preCreateQuotationTenderRenderView() {
		
		debugLogger("preCreateQuotationTenderRenderView");

		super.renewSesionObject();
		this.getManagedBean().setEditable(false);
		this.getManagedBean().setCreateQt(true);
		this.getManagedBean().setOutComes(
				new String[] { ResourceBundleUtils.getMessage("lbl.btn.submit",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()) });
		try {

			ScQt scQt = new ScQt();
			PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
			if (pmOrgProfile != null) {
				scQt.setPmOrgProfile(pmOrgProfile);
				scQt.setOwnerOrgProfile(pmOrgProfile);
			}
			scQt.setProcurementMode(new PmParameter());
			scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			scQt.setMinistrySpecSecrataryId(new PmUser());
			scQt.setProcurementTypeCatId(new PmParameter());
			scQt.setFulfilmentType(new PmParameter());
			scQt.setPmProcurementCategory(new PmParameter());
			scQt.setPanel(true);
			
			/*ScQtCommittee qtCommitte = new ScQtCommittee();
			qtCommitte.setScQt(scQt);
			setUserDetails(qtCommitte);
			qtCommitte.setCommitteeActivities(null);
			qtCommitte.setQtCommitteRemarks(null);
			qtCommitte.setScQtMeetings(null);
			ScCommittee committee = new ScCommittee();
			PmStatus committeStatus = new PmStatus();
			committeStatus.setStatusId(QTStatusConst.CMT_PENDING_SUBMISSION);
			committee.setCommitteStatus(committeStatus);
			committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
			parameterReqDTO
					.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterByTypeAndCode(parameterReqDTO);
			committee.setCommitteeType(parameterResDTO.getPmParameter());
			qtCommitte.setScCommittee(committee);
			this.getManagedBean().setScQtCommittee(qtCommitte);
			this.getManagedBean().setScCommitteeMemberList(
					qtCommitte.getScCommittee().getScCommitteeMembers());
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());*/
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.QT_PENDING_QT_SUBMISSION);
			scQt.setQtStatus(status);
            ScWorkFlowStatus workFlowStatus = new ScWorkFlowStatus();
            workFlowStatus.setPmStatus(scQt.getQtStatus());
            scQt.setCurrentWorkFlowStatus(workFlowStatus);
			/* scQt.setQtNo(dto.getQtNo()); */
			ScQtStatus qtStatus = new ScQtStatus();
			qtStatus.setScQt(scQt);
			qtStatus.setPmStatus(status);
			qtStatus.setCurrent(Boolean.TRUE);
			qtStatus.setDocType(QTConstants.DOCTYPE_QT);
			List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
			qtStatuses.add(qtStatus);
			scQt.setScQtStatus(qtStatuses);
			scQt.setItemGroup(new CtPanelItemGroup());
			ScQtSupplierCriteria supplierCriteria = new ScQtSupplierCriteria();
			supplierCriteria.setScQt(scQt);
			scQt.setScQtSupplierCriteria(supplierCriteria);
							
			
			this.getManagedBean().setActiveIndex(0);
			scQt.setContractType(new PmParameter());
			scQt.setCircularVer(new CtCircularVer());
			
			this.getManagedBean().setMinistrySpecApprovers(getUsersByRoleCode(RoleCode.MSC_SECRETARY.value()));
			
			this.getManagedBean().setQt(scQt);
			Long currentStatus = this.getManagedBean().getQt().getQtStatus()
					.getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, "/xhtml/sc/quatationtender/quatationtendercreation.xhtml");

		} finally {
		}
	}
	
	/**
	 * Zone radio change.change zone location level radio in business owner.
	 * 
	 * @param event
	 *            (boolean zoneLocationlevel).
	 */
	public void zoneRadioChange(ScQt scQt) {
		
		/*Boolean isZonal = (Boolean) event.getComponent().getAttributes()
				.get("value");*/
		Boolean isZonal = scQt.isZonal();
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
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
			// supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel());
			supplierCriteria.setScQtLocCriteria(null);
			scQt.setZoneLocationLevel(new PmParameter());
			if (supplierCriteria.isAllState() == true) {
				this.getManagedBean().setZoneTrees(null);
			}
			/*if (null != supplierCriteria
					&& supplierCriteria.isBrandNew() == false
					&& scQt.isPanel() == false) {
				updateTechnicalAndFinacialSpecStatusZoneLocationChange(zoneCriteria);
			}*/
		} else {
			this.getManagedBean().setZoneGeneralTrees(null);
			supplierCriteria.setQtZoneCriterias(null);
			if (null == supplierCriteria.getLocationLevel()) {
				supplierCriteria.setLocationLevel(new PmParameter());
			}

			this.getManagedBean().setZoneGeneralTrees(null);
			this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
			/*if (null != supplierCriteria
					&& supplierCriteria.isBrandNew() == false
					&& scQt.isPanel() == false) {
				updateTechnicalAndFinacialSpecStatusZoneLocationChange(null);
			}*/
		}

		scQt.setScQtSupplierCriteria(supplierCriteria);
		//this.getManagedBean().setQt(scQt);

	}
	
	/**
	 * Gets the quotation tendervalues.display quotation/Tender
	 * Type(open/Restricted) radio values in business owner.
	 * 
	 * @param quotationTenderType
	 *            ((String quotationTenderType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getQuotationTendervalues(String quotationTenderType, ScQt scQt) {
		
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> qtTypes = new ArrayList<String>();
		qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_INTERNATIONAL);
		if (null != scQt &&null != scQt.getProcurementMode()
				&& ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
		}
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), qtTypes));

		return super.getRadioButtonCodeValuesBySearchBean(quotationTenderType,
				searchBean);
	}


/**
 * Sets the procurement mode.based on Estimated price set Procuremnt
 * Mode(Quotation/Tender).
 * 
 * @param scQt
 *            (ScQt scQt)
 */
public void setProcurementMode(ScQt scQt) {
	IParameterSO parameterSo = null;
	try {
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

		boolean isQuotationThresHold = isQuotationThresHold(scQt
				.getEstimatedAmount());
		ScQtSupplierCriteria scQtSupplierCriteria = scQt
				.getScQtSupplierCriteria();

		if (null == scQt.getEstimatedAmount()
				|| scQt.getEstimatedAmount().intValue() <= 20000) {
			throw new EpException(MessageConst.ERROR_760024,
					new String[] { "20000" });
		}

		// Procurement Mode
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO
				.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
		if (isQuotationThresHold) {
			parameterReqDTO
					.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
		} else {
			parameterReqDTO
					.setParameterCode(ParameterConst.PROCUREMENT_MODE_TENDER);
		}
		ParameterResDTO parameterResDTO = parameterSo
				.retrieveParameterByTypeAndCode(parameterReqDTO);
		scQt.setProcurementMode(parameterResDTO.getPmParameter());

		// QT Type
		scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

		// Open For
		if (isQuotationThresHold
				&& isOpenForBumiputraThresHold(scQt.getEstimatedAmount())) {
			scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
		} else {
			scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
		}

		// Evaluation Type
		if (isQuotationThresHold
				&& isOneTierThresHold(scQt.getEstimatedAmount())) {
			scQt.setEvaluationType(null);

			populateEvaluationTypes(scQt);
		}

		// All State
		if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(scQt
				.getQtType())) {
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
						.equalsIgnoreCase(scQt.getQtType())) || ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
						.equalsIgnoreCase(scQt.getQtType()))) {
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

	/**
	 * Sets the evaluation type.based on Estimated Price set Evaluation Type.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void populateEvaluationTypes(ScQt scQt) {
		
		if (isOneTierThresHold(scQt.getEstimatedAmount())) {

			List<SelectItem> itemList = new ArrayList<SelectItem>();
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				String evaluationTypeCode = paramDesc.getPmParameter()
						.getParameterCode();
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER
						.equals(evaluationTypeCode)) {

					itemList.add(new SelectItem(evaluationTypeCode, paramDesc
							.getCodeName()));

					if (scQt.getEvaluationType() == null) {
						scQt.setEvaluationType(evaluationTypeCode);
					}
				}
			}
			this.getManagedBean().setEvaluationTypeItem(itemList);
		}
	}
	
	/**
	 * On change qt type.change qt Type(open/restricted) radion button in
	 * business owner.
	 * 
	 * @param event
	 *            ( String qttype)
	 */
	public void onChangeQtType(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setSuppCriteriaopenList(
				new ArrayList<ScQtCategory>());
		if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(type)) {

			if (null != this.getManagedBean().getQt()
					.getScQtSupplierCriteria()
					&& null == this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getLocationLevel()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		} else if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
				.equalsIgnoreCase(type)) {
			this.getManagedBean().setSupplierLocationList(
					getManagedBean().getExistingSupplierLocationList());
		}

		this.getManagedBean().getQt().setQtType(type);
	}

	/**
	 * Open for change.change open for(Bumiputra/All) radion button in business
	 * owner.
	 * 
	 * @param event
	 *            (String Openfor)
	 */
	public void openForChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getQt().setOpenFor(type);
	}
	
	/**
	 * Procurement change.change Procurement Category Dropdown in Business
	 * owner.
	 * 
	 * @param event
	 *            (Long procuremntCategoryId).
	 */
	public void procurementChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setProcuremntCategory(
							paramDesc.getPmParameter().getParameterCode());
				}

			}
		} finally {

		}
	}
	/**
	 * Fulfilment change.changed Fulfilemnt Dropdown in specification committee.
	 * 
	 * @param event
	 *            (Long fulfilmentId)
	 */
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		/*
		 * IQTSpecSO specso = getSpecSO(); SpecificationReqDTO specReqDTO = new
		 * SpecificationReqDTO();
		 */try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setFulfilmentType(
							paramDesc.getPmParameter().getParameterCode());
					this.getManagedBean().getQt()
							.setFulfilmentType(paramDesc.getPmParameter());
					break;
				}
			}

			if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			} else if (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
				this.getManagedBean().setTotalProgressivePayment(0);

			} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
				this.getManagedBean().setTotalFrequency(0);
				this.getManagedBean().setTotalProgressivePayment(0);

			}
			this.getManagedBean().getQt().setScQtSchedules(null);
			this.getManagedBean().getQt().setScQtProgressivePayments(null);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

		} finally {

		}

	}
	private void removefullfillmentDetails(String scheduleType) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();

				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().setToBeRemoveSchedules(toBeRemoveList);
				this.getManagedBean().getQt().setScQtSchedules(null);
			}
			this.getManagedBean()
					.setScheduleList(new ArrayList<ScQtSchedule>());
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();
			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();

				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().setToBeRemoveProgPayments(toBeRemoveList);
				this.getManagedBean().getQt()
						.setScQtProgressivePayments(null);
			}
			this.getManagedBean().setProgressivePaymentList(
					new ArrayList<ScQtProgressivePayment>());
		}

	}
	
	
	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            the type
	 * @return the list
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}
	
	
	/**
	 * Business owner next action.click next button in businessowner panel
	 * selection.navigate to business owner tabs.
	 * 
	 * @return the string
	 */
	public void panelItemSelection() {
	
			if (null == this.getManagedBean().getQt().getCircularVer()) {
				throw new EpException(MessageConst.ERROR_760157);
			}
			ScQt scQt = this.getManagedBean().getQt();
			boolean isPanel = scQt.isPanel();
			List<CtPanelItem> panelItemList = null;
			//this.getManagedBean().getPanelGroupItems();
			if (CollectionUtils.isNotEmpty(panelItemList)) {
				Long panelItemId = scQt.getPanelItemId();
				for (CtPanelItem panelItem : panelItemList) {
					if (panelItem.getPanelItemId().equals(panelItemId)) {
						this.getManagedBean().setCtPanelItem(panelItem);
						this.getManagedBean().setPanelItemCategory(
								panelItem.getItemCategoryId().getParameterCode());
					}
				}
			}
			if (QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
					.getManagedBean().getCircularType())) {
				if (null == this.getManagedBean().getCtPanelItem()) {
					throw new EpException(MessageConst.ERROR_760157);
				}
			}
			if (isPanel
					&& QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
							.getManagedBean().getCircularType())
					&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
							.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())
					|| ParameterConst.ITEM_CATEGORY_TYPE_GENERAL
							.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())) {
				scQt.setZonal(false);
				scQt.setQtType(QTConstants.OPEN_TYPE);
				this.getManagedBean().setCtZoneSuppliers(new ArrayList<TreeNode>());
				this.getManagedBean().setTotalSuppliers(0);
				this.getManagedBean().setCtZoneSupplierList(new ArrayList<CtPanelSupplier>());
				ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();

				if ((null == supplierCriteria)
						|| (null != supplierCriteria && null == supplierCriteria
								.getQtSupplierCriteriaId())) {
					this.getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
					this.getManagedBean().setSuppCriteriaopenList(new ArrayList<ScQtCategory>());
					supplierCriteria = new ScQtSupplierCriteria();
					supplierCriteria.setScQt(scQt);
					
					//scQt.setCircularVer(this.getManagedBean().getCircularVer());
					if (null == scQt.getZoneLocationLevel()) {
						scQt.setZoneLocationLevel(new PmParameter());
					}
					scQt.setZoneLocationLevel(this.getManagedBean()
							.getCtPanelItem().getLocationLevelId());
					setUserDetails(supplierCriteria);
					this.getManagedBean().getQt()
							.setScQtSupplierCriteria(supplierCriteria);
					if (null != this.getManagedBean().getCtPanelItem()
							&& null != this.getManagedBean().getCtPanelItem()
									.getPanelItemId()) {
						scQt.setZonal(false);
						retriveZonesOrSuppliersByItem();
						if (null != this.getManagedBean().getZoneGeneralTrees()
								&& !this.getManagedBean().getZoneGeneralTrees()
										.isEmpty()) {
							scQt.setZonal(true);
						}
					}
				} else {

					if (null != supplierCriteria
							&& null != supplierCriteria.getQtSupplierCriteriaId()
							&& (isPanel != this.getManagedBean().isPanelChange())) {
						RequestContext.getCurrentInstance().execute("panelApprovalDlgs.show()");
					} else {
						
						CtPanelItem ctPanelItem = this.getManagedBean().getCtPanelItem();
						if (null != ctPanelItem) {}
					}
				}
			}

			else if (isPanel
					&& QTConstants.CIRCULAR_CENTRAL_CONTRACT_TYPE
							.equalsIgnoreCase(this.getManagedBean().getCircularType())) {

				FacesScopes facesScopes = new FacesScopes();
				ScQtRemark remark = new ScQtRemark();
				// ScWorkFlowStatus status = new ScWorkFlowStatus();
				// PmStatus status=new PmStatus();
				setUserDetails(remark);
				remark.setCreatedBy(facesScopes.getUserSessionBean()
						.getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setScQt(this.getManagedBean().getQt());
				// status.setPmStatus(this.getManagedBean().getScQt().getQtStatus());
				// status.setStatusId(this.getManagedBean().getScQt().getQtStatus().getStatusId());
				remark.setScWorkFlowStatus(null);
				this.getManagedBean().setContractremark(remark);
				this.getManagedBean().setScQtRemarksList(
						new ArrayList<ScQtRemark>());
				this.getManagedBean().getScQtRemarksList()
						.add(this.getManagedBean().getContractremark());

			}
			this.getManagedBean().setPanelChange(isPanel);
		
	}

	/**
	 * Retrive zones or suppliers by item.retrive zones or suppliers based on
	 * item in supplier qualification criteria.
	 */
	public void retriveZonesOrSuppliersByItem() {
		
		IPanelSupplierSO panelSupplierSO;
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {
			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);

			if (null != this.getManagedBean().getCtPanelItem()
					&& null != this.getManagedBean().getCtPanelItem()
							.getPanelItemId()) {

				reqDTO.setPanelItem(this.getManagedBean().getCtPanelItem());
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
			} else {
				if (null != this.getManagedBean().getCtPanelItem()
						&& null != this.getManagedBean().getCtPanelItem()
								.getPanelItemId()) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					/*
					 * PanelSupplierReqDTO supplierreqDTO = new
					 * PanelSupplierReqDTO();
					 * supplierreqDTO.setPanelItemId(this.getManagedBean()
					 * .getCtPanelItem().getPanelItemId());
					 * supplierreqDTO.setFetchSubEntity(true);
					 * PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
					 * .retrievePanelItem(supplierreqDTO);
					 */
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(this.getManagedBean()
							.getCtPanelItem().getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					this.getManagedBean().setTotalSuppliers(0);
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != supplierResDTO.getCtPanelSuppliers()) {
						// CtPanelItem panelItem =
						// supplierResDTO.getCtPanelSuppliers();
						this.getManagedBean().setTotalSuppliers(
								supplierResDTO.getCtPanelSuppliers().size());

						this.getManagedBean().setCtZoneSupplierList(
								supplierResDTO.getCtPanelSuppliers());

					}
				}

			}
			/*
			 * List<CtPanelItem> itemList = this.getManagedBean()
			 * .getSelectpanelItem(); itemList = new ArrayList<CtPanelItem>();
			 * itemList.add(this.getManagedBean().getCtPanelItem());
			 * 
			 * this.getManagedBean().setSelectpanelItem(itemList);
			 */

		} finally {

		}

	}
	/**
	 * Select zone location level.change Location Level Dropdown in General
	 * information menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectZoneLocationLevel(AjaxBehaviorEvent event) {

		if (this.getManagedBean().getQt().isZonal()) {
			if (null != this.getManagedBean().getZoneGeneralTrees()
					&& !this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
				this.getManagedBean().setZoneGeneralTrees(null);
				this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
			}

		}

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
	 * Un select zone tree node.Event handle un check Non panel treeNodes.
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
	public void editZones(TreeNode rootZoneLocation, int index, ScQt scQt) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = scQt
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
	 * Method to check all the state/districts in data table.
	 * 
	 * @param event
	 *            the event
	 */
	public void checkAllZoneGeneral(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<TreeNode> list = getManagedBean().getZoneGeneralTrees();
		if (null != list) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						for (TreeNode node2 : childNode.getChildren()) {
							node2.setSelected(flag);
						}
					}
					childNode.setSelected(flag);
				}
			}
			getManagedBean().setZoneGeneralTrees(list);
		} else {

		}
	}
	
	/**
	 * Show general zones popup.Event handle navigate to the ZoneLocations
	 * Popup.
	 */
	public void showGeneralZonesPopup() {

		ICountrySO countrySo = null;

		try {
			Long locationLevel = this.getManagedBean().getQt()
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
					this.getManagedBean().setStatecode(
							paramDesc.getPmParameter().getParameterCode());
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
		
		getManagedBean().setSelectedFlag(true);
		List<TreeNode> rootTrees = this.getManagedBean().getZoneGeneralTrees();

		// LocationSingleton.getInstance().deleteTreeNodes(rootTrees);
		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (getManagedBean().isSelectedFlag()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (!rootTrees.isEmpty()) {
			getManagedBean()
					.getQt()
					.getScQtSupplierCriteria()
					.getQtZoneCriterias()
					.setTplSpecZoneDtls(
							convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
		}
		/*ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getScQt()
				.getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria
				&& zoneSupplierCriteria.isBrandNew() == false) {
			updateFinacialSpecStatus();
		}*/
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
			if (null != this.getManagedBean().getQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						getManagedBean().setSelectedFlag(false);
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
		List<ZonesBean> list = getManagedBean().getBeans();
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
			getManagedBean().setBeans(results);
		}
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
					getManagedBean().setSelectedFlag(false);
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
	 * Method to check all the state/districts in data table in dialog.
	 * 
	 * @param isCheck
	 */
	public void checkAllZoneGeneralPopUp(boolean ischeck) {
		// boolean flag = ((SelectBooleanCheckbox)
		// event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootZoanlTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(ischeck);
					}
				}
				stateNode.setSelected(ischeck);
			}
			getManagedBean().setRootZoanlTree(treeNode);
		} else {

		}
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
		List<ZonesBean> beans = getManagedBean().getBeans();
		if (null == beans) {
			beans = new ArrayList<ZonesBean>();
		}
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getStatecode();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (locationLevel
						.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
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

		if (null == getManagedBean().getZoneGeneralTrees()) {
			getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		}
		if (null != allNodes && !allNodes.isEmpty()) {

			if (null != this.getManagedBean().getQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
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

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
				// check whether state exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
				// check whether division exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				// check whether district exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			beans.add(zonesBean);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneGeneralTrees().add(
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));

			}

			else if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneGeneralTrees().remove(
						getManagedBean().getIndex());
				getManagedBean().getZoneGeneralTrees().add(
						getManagedBean().getIndex(),
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));
			}
			getManagedBean().getBeans().add(zonesBean);
		} else {
			throw new EpException("Please select At least one Zone Location");
		}
		this.getManagedBean().setZoneLocation(false);
		getManagedBean().setZoneName(null);
		getManagedBean().setTempZoneName(null);
		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setAutoUpdate(true);
		/*ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getScQt()
				.getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria
				&& zoneSupplierCriteria.isBrandNew() == false) {
			updateFinacialSpecStatus();
		}*/
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");

	}
	// check zone name existing in the list or not.
		private void updateZoneBean(String zoneName) {
			List<ZonesBean> beans = new ArrayList<ZonesBean>();
			List<ZonesBean> zoneBeans = getManagedBean().getBeans();

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
			getManagedBean().setBeans(beans);

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
			List<ZonesBean> list = getManagedBean().getBeans();
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

			List<ZonesBean> list = getManagedBean().getBeans();

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
		 * Constuct condition fields.
		 * 
		 * @return the string
		 */
		public String constuctConditionFields() {
			if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
				Long orgProfileId = this.getManagedBean().getQt().getPmOrgProfile()
						.getOrgProfileId();
				if (null != orgProfileId) {
					return "quotationTender.pmOrgProfile.orgProfileId^"
							+ orgProfileId;
				}
			}
			return "";
		}
		
		/**
		 * Gets the dropdown location values.display the Location level dropdown
		 * values.
		 * 
		 * @param locationLevel
		 *            the location level
		 * @return the dropdown location values
		 */
		public SelectItem[] getDropdownLocationValues(String locationLevel, ScQt scQt) {
			
			IOrgProfileSO orgProfile = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO orgResDto = orgProfile
					.retrieveOrgStateCodeByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
			this.getManagedBean().setAllStateCode(orgResDto.getStateStatusId());
			List<String> types = new ArrayList<String>();
			types.add(ParameterConst.LOCATION_LEVEL_STATE);
			if (this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_SARAWAK) {
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
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
								e.getMessage()));
			}
			return items;
			// return getDropdownCodeValues(committeeRole);
		}
		
		/**
		 * Contract type drop down values.
		 * @return the list
		 */
		public List<PmParameterDesc> getContractTypes() {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			// check logged in user org type
			boolean  isMinistry =  retrievePmUserOrgTypeByUserId();

			if(isMinistry){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.CONTRACT_TYPE_MINISTRY));
			} else{
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.CONTRACT_TYPE_CENTRAL));
			}

			List<PmParameterDesc> contractTypes = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(
							ParameterTypeConst.CONTRACT_TYPE, searchBean);

			return contractTypes;
		}
		
		/**
		 * Retrieve pm user org type by user id.
		 * to check user org type 
		 * @return true, if successful
		 */
		private boolean retrievePmUserOrgTypeByUserId(){
			boolean isMinistry  = Boolean.TRUE;
			PmParameter  orgTypeCode = null;
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if(resDTO.getUserOrgList() != null && resDTO.getUserOrgList().size()>0){
					for (PmUserOrg pmUserOrg :  resDTO.getUserOrgList()) {
						if( pmUserOrg.getPmOrgProfile().getOrgTypeCode() != null){
							orgTypeCode   = pmUserOrg.getPmOrgProfile().getOrgTypeCode();
							if(ParameterConst.ORG_TYPE_BPK_HQ.equalsIgnoreCase(orgTypeCode.getParameterCode()) || 
									ParameterConst.ORG_TYPE_PMS_SABAH.equalsIgnoreCase(orgTypeCode.getParameterCode()) ||
									ParameterConst.ORG_TYPE_PMS_SARAWAK.equalsIgnoreCase(orgTypeCode.getParameterCode())){
								isMinistry  = Boolean.FALSE;
							}
						}
					}
				}
			}finally{
				userSO = null;
			}
			return isMinistry;		
		}
		
		/**
		 * contractTypeSelection by User from drop down& Set into Sc.
		 * 
		 * @param event
		 *            (boolean panelType).
		 */
	public void contractTypeSelection(AjaxBehaviorEvent event) {
		
		Long parameterId = (Long) event.getComponent().getAttributes().get("value");
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			this.getManagedBean().getQt()
					.setContractType(resDTO.getPmParameter());

		} finally {
			parameterSO = null;
		}


		}

		
	public void setFileNo(ScQt scQt) {
		scQt.getFileNo();
		
	}
	
	
}

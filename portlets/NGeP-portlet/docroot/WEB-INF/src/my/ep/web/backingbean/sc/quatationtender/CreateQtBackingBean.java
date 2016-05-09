/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
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
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.specification.SpecificationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.CommonUtils;
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
		conditionString.append("qt^true");
		conditionString.append("|");
		conditionString.append("docType^");
		conditionString.append(ParameterConst.DOCTYPE_PANEL_CIRCULAR);

		return conditionString.toString();
	}
	
	/**
	 * Circular changed event.
	 * 
	 * @param circular
	 * 		the CtCircularVer instance
	 */
	public void circularListSelect(CtCircularVer circular) {
		if (null != circular) {
			ScQt scQt = getManagedBean().getQt();
			scQt.setCircularVer(circular);
			scQt.setItemGroup(new CtPanelItemGroup());
			
			List<CtPanelItemGroup> panelGroupItems = null;
			if (circular.getCircularVerId() != null) {
				panelGroupItems = getPanelGroupItemsByCirculerVer(circular);
			}
			getManagedBean().setPanelGroupItems(panelGroupItems);
			getManagedBean().setCircularType(circular.getDocType());
			this.getManagedBean().setCircularExpDate(circular.getEndDate());
			this.getManagedBean().setCircularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			getManagedBean().setQt(scQt);
			if(null!=scQt.getItemGroup() && null!=scQt.getItemGroup().getPanelItemGroupId()){
			deleteTemplateSpecs(getManagedBean().getQt());
			}
			
		}

	}
	private void deleteTemplateSpecs(ScQt scQt) {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scQt);
		IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		SpecificationResDTO resDTO= specSO.deleteTemplateSpecs(reqDTO);
		this.getManagedBean().setQt(resDTO.getScQt());
		this.getManagedBean().setScQtCheckSt2techProposal(null);
		this.getManagedBean().setScQtCheckSt2financeProposalList(null);
	}
	/**
	 * Panel conformation.change panel(Yes/No) radion button in business owner.
	 * 
	 * @param event
	 *            (boolean panelType).
	 */
	public void panelConformation(ScQt scQt) {

		Boolean isPanel = scQt.isPanel();
		/*
		 * Boolean isPanel = (Boolean) event.getComponent().getAttributes()
		 * .get("value");
		 */
		scQt.setPanel(isPanel);

		if (!isPanel) {
			if (scQt.isBrandNew() == false) {
				this.getManagedBean().setQt(scQt);
				RequestContext.getCurrentInstance().execute(
						"panelApprovalDlgs.show()");

			} else {
				scQt.setCircularVer(null);
				scQt.setPanelItemId(null);
				scQt.setItemGroup(null);
				this.getManagedBean().setQt(scQt);
				setDataNull();
			}
		} else {
			if (scQt.isBrandNew() == false) {
				this.getManagedBean().setQt(scQt);
				circularListSelect(new CtCircularVer());
				RequestContext.getCurrentInstance().execute(
						"panelApprovalDlgs.show()");
			} else {
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
				scQt.setOpenFor(null);
				this.getManagedBean().setQt(scQt);
				circularListSelect(new CtCircularVer());
				setDataNull();
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
			this.getManagedBean().getQt().setCircularVer(null);
			this.getManagedBean().getQt().setItemGroup(null);
		} else {
			this.getManagedBean().getQt().setPanel(true);
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
			scQt.setProcurementTypeCatId(new PmParameter());
			scQt.setFulfilmentType(new PmParameter());
			scQt.setPmProcurementCategory(new PmParameter());
			scQt.setPanel(true);
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
			
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
			scQt.setCircularVer(new CtCircularVer());
			setUserDetails(scQt);
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
	 * Get list of Panel Item Group.
	 * 
	 * @return	the list of CtPanelItemGroup instance
	 */
	public List<CtPanelItemGroup> getPanelGroupItems() {
		List<CtPanelItemGroup> panelGroupItems = getManagedBean().getPanelGroupItems();
		
		if (panelGroupItems == null || panelGroupItems.size() == 0) {
			CtCircularVer circularVer = getManagedBean().getQt().getCircularVer();
			
			if (circularVer != null) {
				panelGroupItems = getPanelGroupItemsByCirculerVer(circularVer);
				
				getManagedBean().setPanelGroupItems(panelGroupItems);	
			}
		}
		return panelGroupItems;
	}
	
	/**
	 * Get list of Ministry Spec Approver.
	 * 
	 * @return	the list of PmUser instance
	 */
	public List<PmUser> getMinistrySpecApprovers() {
		List<PmUser> ministrySpecApprovers = getManagedBean().getMinistrySpecApprovers();
		
		if (ministrySpecApprovers == null || ministrySpecApprovers.size() == 0) {
			ministrySpecApprovers = getUsersByRoleCode(RoleCode.MSC_SECRETARY.value());
			
			getManagedBean().setMinistrySpecApprovers(ministrySpecApprovers);
		}
		return ministrySpecApprovers;
	}
	
	/**
	 * Zone radio change.change zone location level radio in business owner.
	 * 
	 * @param event
	 *            (boolean zoneLocationlevel).
	 */
	public void zoneRadioChange(ScQt scQt) {
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
			if (null != supplierCriteria
					&& supplierCriteria.isBrandNew() == false
					&& scQt.isPanel() == false) {
				if (null != this.getManagedBean().getLoginRoleType()
						&& !"".equals(this.getManagedBean().getLoginRoleType())
						&& "SC".equalsIgnoreCase(this.getManagedBean()
								.getLoginRoleType())) {
					updateTechnicalAndFinacialSpecStatusZoneLocationChange(zoneCriteria);
				}
			}
		} else {
			this.getManagedBean().setZoneGeneralTrees(null);
			supplierCriteria.setQtZoneCriterias(null);
			if (null == supplierCriteria.getLocationLevel()) {
				supplierCriteria.setLocationLevel(new PmParameter());
			}

			this.getManagedBean().setZoneGeneralTrees(null);
			this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
			if (null != supplierCriteria
					&& supplierCriteria.isBrandNew() == false
					&& scQt.isPanel() == false) {
				if (null != this.getManagedBean().getLoginRoleType()
						&& !"".equals(this.getManagedBean().getLoginRoleType())
						&& "SC".equalsIgnoreCase(this.getManagedBean()
								.getLoginRoleType())) {
					updateTechnicalAndFinacialSpecStatusZoneLocationChange(null);
				}
			}
		}

		scQt.setScQtSupplierCriteria(supplierCriteria);
		this.getManagedBean().setQt(scQt);
	}
	
	private void updateTechnicalAndFinacialSpecStatusZoneLocationChange(
			ScQtZoneCriteria zoneCriteria) {
		try {
			SpecificationBackingBean specificationBackingBean = new SpecificationBackingBean();
			List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null == technicalSpecCheckList
					|| technicalSpecCheckList.isEmpty()) {
				specificationBackingBean.retriveTechnicalCheckList();
				technicalSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
			}

			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList
					|| financialSpecCheckList.isEmpty()) {
				specificationBackingBean.retriveFinancialCheckList();
				financialSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
			}
			if (null != financialSpecCheckList
					&& !financialSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatus(QTStatusConst.TS_PENDING_SUBMISSION);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE
							.equalsIgnoreCase(financialchecklist.getDefinationType()
									.getParameterCode())) {
					financialchecklist.setPmStatus(status);
					
				}
			}
			}
			if (null != technicalSpecCheckList
					&& !technicalSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatus(QTStatusConst.TS_PENDING_SUBMISSION);
				boolean zonelevelChange = false;
				for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
					zonelevelChange = true;
					//technicalchecklist.setPmStatus(status);
					if (QTConstants.TEMPLETE_SPEC_CODE
							.equalsIgnoreCase(technicalchecklist.getDefinationType()
									.getParameterCode())) {
						technicalchecklist.setPmStatus(status);
					
				}
				}
				if (zonelevelChange) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_WARN,
											null,
											MessageUtils
													.getMessage(
															MessageConst.WARN_760357,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}

			}

		} finally {

		}

	}

	private PmStatus getPmStatus(Long statusId) {
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(statusId);
		return iStatusSO.retrieveStatusByStatusId(reqDTO).getPmStatus();
	}
	
	/**
	 * Ministry Specification Secretary Required radio change.
	 * 
	 * @param scQt
	 * 		the scQt instance
	 */
	public void ministrySpecSecretaryRequiredChanged(ScQt scQt) {
		if (scQt.getSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(new PmUser());
		}
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
		
		if (null != scQt &&null != scQt.getProcurementMode()
				&& ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), qtTypes));
		}

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
public void setProcurementMode(ScQt scQt, BigDecimal amount) {
	IParameterSO parameterSo = null;
	try {
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

		BigDecimal estimatedAmount = scQt.getEstimatedAmount();
		boolean isQuotationThresHold = isQuotationThresHold(amount);
		ScQtSupplierCriteria scQtSupplierCriteria = scQt
				.getScQtSupplierCriteria();
		String qtType = scQt.getQtType();
		
		// Intended Price
		if (scQt.isPanel()) {
			BigDecimal panelThreshold = getPanelThresholdAmount();
			
			if (null == amount || (amount.compareTo(panelThreshold) <= 0)) {
				throw new EpException(MessageConst.ERROR_760380,
						new String[] { SourcingConstants.DF_AMOUNT.format(panelThreshold) });
			}
		} else {
			BigDecimal prThreshold = getPRThresholdAmount();
			
			if (null == amount || (amount.compareTo(prThreshold) <= 0)) {
				throw new EpException(MessageConst.ERROR_760024,
						new String[] { SourcingConstants.DF_AMOUNT.format(prThreshold) });
			}
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

		if (estimatedAmount == null) {
			// QT Type
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);	
			
			// Open For
			if (isQuotationThresHold
					&& isOpenForBumiputraThresHold(amount)) {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
			} else {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
			}

			// Evaluation Type
			if (isQuotationThresHold
					&& isOneTierThresHold(amount)) {
				scQt.setEvaluationType(null);

				populateEvaluationTypes(scQt, amount);
			}			
		} else {
			// Open For
			updateRestrictedTenderOpenFor(scQt);
		}

		// All State
		if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(qtType)) {
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

	/**
	 * Sets the evaluation type.based on Estimated Price set Evaluation Type.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void populateEvaluationTypes(ScQt scQt, BigDecimal estimatedAmount) {
		
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
		ScQt scQt = this.getManagedBean().getQt();

		this.getManagedBean().setQtCategoryList(
				new ArrayList<ScQtCategory>());
		if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(type)) {

			if (null != scQt.getScQtSupplierCriteria()
					&& null == scQt.getScQtSupplierCriteria().getLocationLevel()) {
				scQt.getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getQtSupplierList())) {
				this.getManagedBean().setToBeRemoveRestrictedSupplierList(this.getManagedBean().getQtSupplierList());
				this.getManagedBean().setQtSupplierList(null);
				scQt.setScQtSuppliers(null);
			}

		} else {
			if(scQt.getTmpExpectedIntendedPrice()== null){
				
				String labelKey =  ResourceBundleUtils.getMessage(
						"lbl.sc.spec.expected.intended.price", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
				throw new EpException(labelKey+" is mandatory.");
			}
			if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
					.equalsIgnoreCase(type)) {
				this.getManagedBean().setSupplierLocationList(
						getManagedBean().getExistingSupplierLocationList());
				
				updateRestrictedTenderOpenFor(scQt);
				
			}
			scQt.setZonal(false);
		}

		scQt.setQtType(type);
	}
	
	/**
	 * update Open For value for QT Type is Restricted Tender.
	 * 
	 * @return
	 */
	private void updateRestrictedTenderOpenFor(ScQt scQt) {
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(getManagedBean().getQt());
			
			if (qtDetailSO.isRestrictedTenderOpenForBumi(reqDTO)) {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
			} else {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
			}

		} finally {
			qtDetailSO = null;
		}
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
			
			List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();

			if (CollectionUtils.isNotEmpty(panelItemList)) {

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
			} else {
				this.getManagedBean().setTotalSuppliers(0);
				
				if (CollectionUtils.isNotEmpty(panelItemList)) {
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
					supplierReqDTO.setPanelItemId(panelItemList.get(0).getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
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
			this.getManagedBean().setStatecode(locationLevelCode);
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
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt()
				.getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria
				&& zoneSupplierCriteria.isBrandNew() == false&& null!=zoneSupplierCriteria.getQtZoneCriterias() && zoneSupplierCriteria.getQtZoneCriterias().isBrandNew()==false) {
			if (null != this.getManagedBean().getLoginRoleType()
					&& !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean()
							.getLoginRoleType())) {
			updateFinacialSpecStatus();
			}
		
		}
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
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt()
				.getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria
				&& zoneSupplierCriteria.isBrandNew() == false && null!=zoneSupplierCriteria.getQtZoneCriterias() && zoneSupplierCriteria.getQtZoneCriterias().isBrandNew()==false) {
			if (null != this.getManagedBean().getLoginRoleType()
					&& !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean()
							.getLoginRoleType())) {
			updateFinacialSpecStatus();
			}
		}
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
			if(null!=this.getManagedBean().getQt()
					.getContractType() &&ParameterConst.CONTRACT_TYPE_CENTRAL.equalsIgnoreCase(this.getManagedBean().getQt()
					.getContractType().getParameterCode())){
				ParameterReqDTO fulfilMentAsandWhen = new ParameterReqDTO();
				fulfilMentAsandWhen
						.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
				fulfilMentAsandWhen.setParameterCode(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
				ParameterResDTO fulfilMentAsandWhenresDTO = parameterSO
						.retrieveParameterByTypeAndCode(fulfilMentAsandWhen);
				this.getManagedBean().getQt().setFulfilmentType(fulfilMentAsandWhenresDTO.getPmParameter());
			}

		} finally {
			parameterSO = null;
		}


		}
	/**
	 * Panel Group Item by User from drop down& Set into Sc.
	 * 
	 * 
	 */
	public void changePanelGroupItem() {
		ScQt scQt = getManagedBean().getQt();
		if(null!=scQt.getItemGroup() && null!=scQt.getItemGroup().getPanelItemGroupId()){
			if (null != this.getManagedBean().getLoginRoleType()
					&& !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean()
							.getLoginRoleType())){
				deleteTemplateSpecs(scQt);
				}
		}
	}
	
	/**
	 * Panel conformation delete.change panel yes to No or No to yes
	 * Confirmation to delete Existing data.
	 * 
	 * @return navigate to busineess owner menu pages.
	 */
	public void panelConformationDelete() {
		this.getManagedBean().getQt().setZonal(false);
		this.getManagedBean().getQt().setZoneLocationLevel(null);
		setDataNull();
		removespecDtls();
		if (null == this.getManagedBean().getQt().getPmProcurementCategory()) {
			this.getManagedBean().getQt()
					.setPmProcurementCategory(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getZoneLocationLevel()) {
			this.getManagedBean().getQt()
					.setZoneLocationLevel(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getProcurementTypeCatId()) {
			this.getManagedBean().getQt()
					.setProcurementTypeCatId(new PmParameter());
		}
		if (this.getManagedBean().getQt().isPanel()) {
			this.getManagedBean().getQt()
					.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
			this.getManagedBean().getQt().setOpenFor(ParameterConst.OPEN_FOR_ALL);
			circularListSelect(new CtCircularVer());
			if (null == this.getManagedBean().getQt().getItemGroup()) {
				this.getManagedBean().getQt()
						.setItemGroup(new CtPanelItemGroup());
			}
		}
		this.getManagedBean().getQt().setZonal(false);
		RequestContext.getCurrentInstance().execute("panelApprovalDlgs.hide()");
	}
	
	private void setDataNull() {
		getManagedBean().setZoneGeneralTrees(null);
		getManagedBean().setZoneTrees(null);
		this.getManagedBean().setScQtCheckSt2techProposal(null);
		this.getManagedBean().setScQtCheckSt2financeProposalList(null);
		this.getManagedBean().setQtCategoryList(null);
	}

	/**
	 * Removespec dtls.change Panle yes to no or no to yes delete all the
	 * existing information.
	 */
	private void removespecDtls() {
		IQTSpecSO specSo = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			if (null != this.getManagedBean().getExistingSupplierLocationList()
					&& !this.getManagedBean().getExistingSupplierLocationList()
							.isEmpty()) {
				this.getManagedBean()
						.setSupplierLocationList(
								this.getManagedBean()
										.getExistingSupplierLocationList());
				List<ScQtLocCriteria> qtLocCriteriaRemoveList = this
						.getManagedBean().getSupplierLocationList();
				if (null != qtLocCriteriaRemoveList
						&& !qtLocCriteriaRemoveList.isEmpty()) {
					for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
						criteria.setDeleted(true);
					}
				}
				reqDTO.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
			}

			reqDTO.setScQt(this.getManagedBean().getQt());
			SpecificationResDTO resDTO = specSo.removeBOSpecDetails(reqDTO);
			this.getManagedBean().setQt(resDTO.getScQt());
			this.getManagedBean().setExistingSupplierLocationList(
					new ArrayList<ScQtLocCriteria>());
			this.getManagedBean().setSupplierLocationList(
					new ArrayList<ScQtLocCriteria>());
			this.getManagedBean().setQtCategoryList(
					new ArrayList<ScQtCategory>());
		} finally {
			specSo = null;
		}
	}
		
	public void setFileNo(ScQt scQt) {
		scQt.getFileNo();
		
	}
	
	/**
	 * QT Title value change listener
	 * 
	 * @param event
	 * 		the value change event
	 */
	public void qtTitleValueChanged(ValueChangeEvent event) {

		updateQtAmmendMentDtls(event.getComponent().getId(),
				event.getOldValue(), event.getNewValue());

	}
	
	/**
	 * Publish Period Days value change listener
	 * 
	 * @param changeEvent
	 * 		the value change event
	 */
	public void publishPeriodDaysChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(),
				changeEvent.getOldValue(), changeEvent.getNewValue());

	}

	/**
	 * Proposal Validity value change listener
	 * 
	 * @param changeEvent
	 * 		the value change event
	 */
	public void proposalValidityChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(),
				changeEvent.getOldValue(), changeEvent.getNewValue());

	}
	
	/**
	 * Update qt ammend ment dtls.
	 * 
	 * @param componentId
	 *            the component id
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	private void updateQtAmmendMentDtls(String componentId, Object oldValue,
			Object newValue) {
		QtAmendMentBean amendMentBean = null;
		
		if ("title1".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FILED_QT_TITLE,
					oldValue, newValue, false);

		} else if ("proposalvalidity".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FILED_QT_VALIDITY_PERIOD,
					oldValue, newValue, false);

		} else if ("publishperiod".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FILED_QT_PUBLISH_PERIOD,
					oldValue, newValue, true);
		}

		this.getManagedBean().setAmendMentBean(amendMentBean);
	}
	
	private QtAmendMentBean putIntoAmendmentValueMap(String field, Object oldValue, Object newValue, boolean ptjApprovalReqFlg) {
		QtAmendMentBean amendMentBean = this.getManagedBean()
				.getAmendMentBean();
		
		if (null == amendMentBean) {
			amendMentBean = new QtAmendMentBean();
		}
		
		QtAmendMentValueBean valueBean = amendMentBean.getValueMap().get(field);
		if (null == valueBean) {
			valueBean = new QtAmendMentValueBean();
			valueBean.setOldValue(oldValue);
			
			amendMentBean.getValueMap().put(field, valueBean);
		}
		valueBean.setNewValue(newValue);
		
		if (valueBean.getOldValue() != null && newValue != null) {
			if (!valueBean.getOldValue().equals(newValue)) {
				if (ptjApprovalReqFlg) {
					amendMentBean.setPTJApprovalReq(true);	
				}
			} else {
				amendMentBean.getValueMap().remove(field);
			}
		}
		
		// remove amendment bean if no changes available.
		if (amendMentBean.getValueMap() != null && amendMentBean.getValueMap().size() > 0) {
			return amendMentBean;
		} else {
			return null;
		}
	}
	
	private void ZonesUpdateWarningMessage(boolean warningMessage) {
		if (warningMessage == true) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, null,
							MessageUtils.getMessage(MessageConst.WARN_760328,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}

	}




	private void updateFinacialSpecStatus() {
		try {
			
			List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null == technicalSpecCheckList
					|| technicalSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean=new SpecificationBackingBean();
				specBackingBean.retriveTechnicalCheckList();
				technicalSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
			}
			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList
					|| financialSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean=new SpecificationBackingBean();
				specBackingBean.retriveFinancialCheckList();
				financialSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
			}
			boolean financialSpecZonesEstimatedpriceUpdate = Boolean.FALSE;
			if (null != financialSpecCheckList
					&& !financialSpecCheckList.isEmpty()) {
				
				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				PmStatus status = getPmStatus(QTStatusConst.TS_PENDING_SUBMISSION);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE
							.equalsIgnoreCase(financialchecklist.getDefinationType()
									.getParameterCode())) {
					if (null != financialchecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(financialchecklist.getPmStatus()
											.getStatusId())) {
						financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
						this.getManagedBean().setZonesWarnMessageUpdate(
								Boolean.TRUE);
						this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
						financialchecklist.setPmStatus(status);

					}
					}

				}
			}
			if (null != technicalSpecCheckList
					&& !technicalSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatus(QTStatusConst.TS_PENDING_SUBMISSION);
				boolean zonelevelChange = false;
				for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
					zonelevelChange = true;
					//technicalchecklist.setPmStatus(status);
					if (QTConstants.TEMPLETE_SPEC_CODE
							.equalsIgnoreCase(technicalchecklist.getDefinationType()
									.getParameterCode())) {
						//technicalchecklist.setPmStatus(status);
						if (null != technicalchecklist.getPmStatus()
								&& QTStatusConst.TS_COMPLETED
										.equals(technicalchecklist.getPmStatus()
												.getStatusId())) {
							financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
							this.getManagedBean().setZonesWarnMessageUpdate(
									Boolean.TRUE);
							this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
							technicalchecklist.setPmStatus(status);

						}
					
				}
				}
			}
				if (financialSpecZonesEstimatedpriceUpdate == true) {
					ZonesUpdateWarningMessage(this.getManagedBean()
							.isZonesWarnMessageUpdate());

				}
			
		} finally {

		}

	}
	public void searchAllPanelItems(){

		IPagingDataModel<CtPanelItem> panelItemDataModel = getManagedBean()
				.getSearchDataModelPanelItem();
		if (null == panelItemDataModel) {
			getManagedBean().setSearchDataModelPanelItem(
					new panelItemPagingDataModel(
							"viewCircularItemsGroupIds"));
		}
		//getManagedBean().getSearchDataModelPanelItem().resetData();
	}

	/**
	 * The Class panelItemPagingDataModel.
	 */
	private class panelItemPagingDataModel extends PagingDataModel<CtPanelItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

	

		/**
		 * Instantiates a new  panelItem paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * 
		 */
		public panelItemPagingDataModel(String componentId) {
			super(componentId);
			
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<CtPanelItem> pageChangeListener(
				ISearchBean<CtPanelItem> searchBean) {
		
			ICircularDetailSO circularDetailsSo=null;
			try {
				 circularDetailsSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);
				CircularDetailReqDTO circularReqDTO=new CircularDetailReqDTO();
				populateItemSearchCriteria(searchBean);
				circularReqDTO.setPanelItemSearchBean(searchBean);
				CircularDetailResDTO resDto = circularDetailsSo.searchListOfPanelItem(circularReqDTO);

				
				return resDto.getPanelItemVerResultBean();
			} finally {
				circularDetailsSo = null;
			}
		}
	}

	/**
	 * Method to prepare user search criteria.
	 * 
	 * @param userCommitteeSearchBean
	 *            the user committee search bean
	 * @param userSearchBean
	 *            the user search bean
	 */
	private void populateItemSearchCriteria(
			
			ISearchBean<CtPanelItem> itemSearchBean) {
		

			
		
		itemSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("ctCircularVer.qt",
								SearchOperator.OPERATOR_EQUALS.value(), true
								));
			
			
	itemSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("ctCircularVer.docType",
								SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_PANEL_CIRCULAR
								));
			
		
	itemSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"ctCircularVer.ctCircular.recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(), 
								true));
	
	
	Date todayDate = new Date();
	Calendar c1 = Calendar.getInstance();
	c1.setTime(todayDate);
	Calendar fromDate = Calendar.getInstance();
	fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
			c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	fromDate.set(Calendar.MILLISECOND, 0);
	itemSearchBean.getDataFilterBeans().add(
			new SearchFilterBean("ctCircularVer.endDate",
					SearchOperator.OPERATOR_GREATER_EQUAL.value(),
					fromDate));
	

			
		}
	
	public void setProcurmentCategory(AjaxBehaviorEvent event){
		
		Long id = (Long) event.getComponent().getAttributes().get("value");
		if (id != null) {
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(id);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterById(parameterReqDTO);
			this.getManagedBean().getQt().setPmProcurementCategory(
					parameterResDTO.getPmParameter());
		}
	}
	
	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	
	public String getPublishingPeriodDaysToolTip() {
		StringBuilder tooltip = new StringBuilder();
		ScQt scQt = getManagedBean().getQt();
		
		if (scQt != null) {
			List<PmConfigRuleValue> ruleValueList = getPublishingPeriodDaysRuleValues(scQt.getQtType());
			Locale locale = new FacesScopes().getUserLocale();
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat dformat = new DecimalFormat(commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
			
			for (PmConfigRuleValue configRuleValue : ruleValueList) {
				if (configRuleValue.getFromValue() != null) {
					tooltip.append(dformat.format(configRuleValue.getFromValue()));	
				}
				
				if (configRuleValue.getToValue() != null) {
					tooltip.append(" - ");
					tooltip.append(dformat.format(configRuleValue.getToValue()));	
				} else {
					tooltip.append(" ");
					tooltip.append(ResourceBundleUtils.getMessage(
							"lbl.sc.qty.and.above", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							locale));
				}
				
				tooltip.append(" : ");
				tooltip.append(configRuleValue.getRuleValue());
				tooltip.append(" ");
				tooltip.append(ResourceBundleUtils.getMessage(
						"lbl.sc.days", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						locale));
				tooltip.append("<br/>");
			}
		}
		
		return tooltip.toString();
	}

	private List<PmConfigRuleValue> getPublishingPeriodDaysRuleValues(String qtType) {
		IRuleSO ruleSO = null;

		try {
			ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.QT_PUBLISHING_PERIOD_DAYS);
			List<PmConfigRuleValue> ruleValueList = ruleSO
					.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();
			if (ruleValueList != null && ruleValueList.size() > 0) {
				return ruleValueList;
			} else {
				throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(reqDTO.getRuleType())});
			}

		} finally {
			ruleSO = null;
		}
	}
	
	/**
	 * To update closing date and proposal validity days..
	 */
	public void handlePublishClosingDate() {
		debugLogger("handlePublishClosingDate");
		ScQt scQt = this.getManagedBean().getQt();

		updatePublishClosingDate(scQt);
	}
	
	/**
	 * To update proposal validity days..
	 */
	public void handleProposalValidityDate() {
		debugLogger("handleProposalValidityDate");
		ScQt scQt = this.getManagedBean().getQt();

		updateProposalValidityDate(scQt);
	}
}

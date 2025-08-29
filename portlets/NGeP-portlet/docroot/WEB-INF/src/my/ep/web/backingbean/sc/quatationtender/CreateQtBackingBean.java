/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.constant.BpmOutcomeConst;
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
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
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
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.specification.SpecificationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 *  Create Quotation / Tender  Backing Bean.
 * @author User
 *
 */
@ManagedBean(name = "createQtBB")
@RequestScoped
public class CreateQtBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The Constant log. */
	private static final Logger log = EpLogger.getLogger(CreateQtBackingBean.class);

	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public CreateQtBackingBean() {
		log.debug("inside constructor");
		log.debug("@@@@@@@@@ Managed Bean @@@@@@@@@ = " + this.getManagedBean());

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
		// log.info(message);
	}

	/**
	 * create LOV ConstructionFields For Panel.
	 * 
	 * @return the panelCondtion.
	 */
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
	 * @param circular the CtCircularVer instance
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

			if (null != scQt.getItemGroup() && null != scQt.getItemGroup().getPanelItemGroupId()) {
				deleteTemplateSpecs(getManagedBean().getQt());
			}

		}

	}

	private void deleteTemplateSpecs(ScQt scQt) {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scQt);
		IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		SpecificationResDTO resDTO = specSO.deleteTemplateSpecs(reqDTO);
		this.getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		this.getManagedBean().setQt(resDTO.getScQt());
		this.getManagedBean().setScQtCheckSt2techProposal(null);
		this.getManagedBean().setScQtCheckSt2financeProposalList(null);
		specSO = null;
	}

	/**
	 * Panel conformation.change panel(Yes/No) radion button in business owner.
	 * 
	 * @param event
	 *            (boolean panelType).
	 */
	public void panelConformation(ScQt scQt) {
		this.getManagedBean().setPanelZone(false);
		scQt.setZonal(false);
		Boolean isPanel = this.getManagedBean().getPanel();
		scQt.setPanel(isPanel);
		if (!isPanel) {
			if (scQt.isBrandNew() == false) {
				RequestContext.getCurrentInstance().execute("panelApprovalDlgs.show()");

			} else {
				scQt.setCircularVer(null);
				scQt.setPanelItemId(null);
				scQt.setItemGroup(null);
				setDataNull();
			}
		} else {
			if (scQt.isBrandNew() == false) {
				circularListSelect(new CtCircularVer());
				RequestContext.getCurrentInstance().execute("panelApprovalDlgs.show()");
			} else {
				circularListSelect(new CtCircularVer());
				setDataNull();
			}
		}

	}

	/**
	 * Panel no conformation.change panel yes to No or No to yes Confirmation .
	 */
	public void panelNoConformation() {

		Boolean panel = this.getManagedBean().getPanel();
		ScQt scQt = this.getManagedBean().getQt();
		if (panel) {
			scQt.setPanel(false);
			scQt.setCircularVer(null);
			scQt.setItemGroup(null);
			this.getManagedBean().setPanel(false);
		} else {
			scQt.setPanel(true);
			this.getManagedBean().setPanel(true);
		}

		// 9019 Start - (Harun) #Note-501
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else {
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}

		if (null == this.getManagedBean().getQt().getProcurementTypeCatId()) {
			this.getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
			this.getManagedBean().getQt().getProcurementTypeCatId().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {
			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());
			this.getManagedBean().getQt().getFulfilmentType().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getPmProcurementCategory()) {
			this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());
			this.getManagedBean().getQt().getPmProcurementCategory().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
			this.getManagedBean().getQt().getContractType().setParameterId((long) 0);
		}
		// 9019 End

	}

	/**
	 * Navigate to quotationTender creation page.
	 */
	public void preCreateQuotationTenderRenderView() {

		super.renewSesionObject();
		debugLogger("preCreateQuotationTenderRenderView,@Managed Bean is " + this.getManagedBean());

		this.getManagedBean().setReadOnly(false);
		this.getManagedBean().setCreateQt(true);
		this.getManagedBean().setOutComes(
				new String[] { ResourceBundleUtils.getMessage("lbl.btn.submit", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
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
				PmPtjSpecialApproval ptjSpecialApproval = getPtjSpecialApprovalByOrgProfileId(pmOrgProfile.getOrgProfileId());
				if (ptjSpecialApproval != null) {
					scQt.setCircularVer(null);
					this.getManagedBean().setPanel(false);//35507 defaulted to false
					scQt.setPanel(false);
					this.getManagedBean().setPtjSpecialApproval(ptjSpecialApproval);
					scQt.setObtainSpecialApproval(false);
					// #25635 -start-
				} else {
					scQt.setItemGroup(new CtPanelItemGroup());
					// #25635 -end-
				}
			} else {
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
            
			// 9019 initializing QT remark for Quotation/Tender creation -start-
			scQt.setScQtRemarks(initializeRemarks(scQt, qtStatus, scQt.getScQtRemarks(), ScQtRemark.class));
			// 9019 end
			
			this.getManagedBean().setQt(scQt);
			Long currentStatus = this.getManagedBean().getQt().getQtStatus().getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);

			if (pmOrgProfile != null && !isPtjAddressTypePresent(pmOrgProfile.getOrgProfileId())) {
				addFacesMessage(fc, MessageConst.ERROR_760450, null);
			}
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sc/quatationtender/quatationtendercreation.xhtml");

		} finally {
		}
	}

	/**
	 * Get list of Panel Item Group.
	 * 
	 * @return the list of CtPanelItemGroup instance
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
	 * @return the list of PmUser instance
	 */
	public List<PmUser> getMinistrySpecApprovers() {
		List<PmUser> ministrySpecApprovers = getManagedBean().getMinistrySpecApprovers();

		if (ministrySpecApprovers == null || ministrySpecApprovers.size() == 0) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_SECRETARY.value());

				// Filter by - QT ministry
				PmOrgProfile pmOrgProfile = getManagedBean().getQt().getPmOrgProfile();
				if (pmOrgProfile != null) {
					SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile." + OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(), retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()).getOrgProfileId()));
					reqDTO.setUserSearchBean(searchBean);
				}

				ministrySpecApprovers = userSO.retrieveUserListByRole(reqDTO).getUserList();
			} finally {
				userSO = null;
			}

			getManagedBean().setMinistrySpecApprovers(ministrySpecApprovers);
		}
		return ministrySpecApprovers;
	}

	// 20210615@asyrafahaziq - Bug #30891 - Start
	public void ContractPeriodChange() {
		try {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {

					Long currentStatus = this.getManagedBean().getQt().getQtStatus().getStatusId();

					if (currentStatus.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING)
							|| currentStatus.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
							|| isQTStatusGreaterThan(currentStatus, QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING)) {

						SpecificationBackingBean specificationBackingBean = new SpecificationBackingBean();
						List<ScQtCheckList> financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
						if (null == financialSpecCheckList || financialSpecCheckList.isEmpty()) {
							specificationBackingBean.retriveFinancialCheckList();
							financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
						}
						if (null != financialSpecCheckList && !financialSpecCheckList.isEmpty()) {
							PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
							for (ScQtCheckList financialchecklist : financialSpecCheckList) {
								if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financialchecklist.getDefinationType().getParameterCode())) {
									financialchecklist.setPmStatus(status);
								}
							}
						}
					}
				}
			}
		} finally {

		}

	}

	// 20210615@asyrafahaziq - Bug #30891 - End

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
			ScQtZoneCriteria zoneCriteria = supplierCriteria.getQtZoneCriterias();
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
			if (null != supplierCriteria && supplierCriteria.isBrandNew() == false && scQt.isPanel() == false) {
				if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
						&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
					updateTechnicalAndFinacialSpecStatusZoneLocationChange(zoneCriteria);
				}
			}
			// #32929 - start
			this.getManagedBean().setZoneRdbButtonChecked(true);
		} else {
			if (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(scQt.getQtStatus().getStatusId()) && !this.getManagedBean().isZoneRdbButtonChecked()
					&& !this.getManagedBean().getPanel()) {
				RequestContext.getCurrentInstance().execute("scQtZoneChangeDlgs.show()");
			} else {
				this.getManagedBean().setZoneGeneralTrees(null);
				// supplierCriteria.setQtZoneCriterias(null);
				if (null == supplierCriteria.getLocationLevel()) {
					supplierCriteria.setLocationLevel(new PmParameter());
				}

				this.getManagedBean().setZoneGeneralTrees(null);
				this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
				if (null != supplierCriteria && supplierCriteria.isBrandNew() == false && scQt.isPanel() == false) {
					if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
							&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
						updateTechnicalAndFinacialSpecStatusZoneLocationChange(null);
						this.getManagedBean().setDeliveryLocRemoveList(null);
						this.getManagedBean().setDeliveryLocBean(null);
						deleteDeliveryAdreesZoneDetails(scQt);
					}
				}
			}
		}

		scQt.setScQtSupplierCriteria(supplierCriteria);
	}

	// #32929 - end

	// #32929 - start
	/**
	 * Popup zonal No confirmation.
	 */
	public void zoneNoConfirmation() {

		Boolean panel = this.getManagedBean().getPanel();
		ScQt scQt = this.getManagedBean().getQt();
		if (!panel) {
			scQt.setZonal(true);
		}

	}

	/**
	 * Popup zonal Yes confirmation.
	 */
	public void zoneYesConfirmation() {

		boolean flag = true;
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
		}

		deleteZoneTreeNode();

		ScQt scQt = this.getManagedBean().getQt();
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();

		this.getManagedBean().setZoneGeneralTrees(null);
		if (null == supplierCriteria.getLocationLevel()) {
			supplierCriteria.setLocationLevel(new PmParameter());
		}

		this.getManagedBean().setZoneGeneralTrees(null);
		this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
		scQt.setScQtSupplierCriteria(supplierCriteria);

	}

	/**
	 * Delete tree node.Event handle delete NonPanel Zones from General
	 * information.
	 */
	public void deleteZoneTreeNode() {

		getManagedBean().setSelectedFlag(true);
		List<TreeNode> rootTrees = this.getManagedBean().getZoneGeneralTrees();

		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (!rootTrees.isEmpty()) {

			ScQtZoneCriteria zonCriteria = getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias();
			zonCriteria.setTplSpecZoneDtls(convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
			// setting into zones bean to validate zones while adding
			if (CollectionUtils.isNotEmpty(zonCriteria.getTplSpecZoneDtls())) {
				List<ZonesBean> zonBeansList = new ArrayList<ZonesBean>();
				for (TplSpecZoneDtl tplSpecZoneDtl : zonCriteria.getTplSpecZoneDtls()) {
					ZonesBean bean = new ZonesBean();
					bean.setTplSpecZoneDtl(tplSpecZoneDtl);
					zonBeansList.add(bean);
				}
				this.getManagedBean().setBeans(zonBeansList);
			}
		}

		ScQt scQt = getManagedBean().getQt();
		if (scQt.isZonal() && scQt.isPanel()) {
			List<TplSpecZoneDtl> specZoneDtls = null;

			if (!rootTrees.isEmpty()) {
				specZoneDtls = convertTreeNodesToZoneCriteria(rootTrees, zoneDtl);
			} else {
				specZoneDtls = new ArrayList<TplSpecZoneDtl>();
			}
			retrieveZoneGeneralTreesPanel(scQt, specZoneDtls);
		}

		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria && zoneSupplierCriteria.isBrandNew() == false && null != zoneSupplierCriteria.getQtZoneCriterias()
				&& zoneSupplierCriteria.getQtZoneCriterias().isBrandNew() == false) {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				updateZoneChangeSpecStatus();
				this.getManagedBean().setDeliveryLocRemoveList(null);
				this.getManagedBean().setDeliveryLocBean(null);
				deleteDeliveryAdreesZoneDetails(this.getManagedBean().getQt());
			}

		}
		this.getManagedBean().setIndex(-1);

	}

	/**
	 * Update spec checklist when QT Zonal change to Non Zonal
	 */
	private void updateZoneChangeSpecStatus() {
		try {

			List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			if (null == technicalSpecCheckList || technicalSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean = new SpecificationBackingBean();
				specBackingBean.retriveTechnicalCheckList();
				technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			}
			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList || financialSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean = new SpecificationBackingBean();
				specBackingBean.retriveFinancialCheckList();
				financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			}
			boolean financialSpecZonesEstimatedpriceUpdate = Boolean.FALSE;
			if (null != financialSpecCheckList && !financialSpecCheckList.isEmpty()) {

				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financialchecklist.getDefinationType().getParameterCode())) {
						if (null != financialchecklist.getPmStatus()
								&& (QTStatusConst.TS_PENDING_SUBMISSION.equals(financialchecklist.getPmStatus().getStatusId()) || QTStatusConst.TS_COMPLETED
										.equals(financialchecklist.getPmStatus().getStatusId()))) {
							financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
							this.getManagedBean().setZonesWarnMessageUpdate(Boolean.TRUE);
							this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
							// set financial checklist status to 9
							financialchecklist.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);

						}
					}

				}
			}
			if (null != technicalSpecCheckList && !technicalSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(technicalchecklist.getDefinationType().getParameterCode())) {
						if (null != technicalchecklist.getPmStatus() && QTStatusConst.TS_COMPLETED.equals(technicalchecklist.getPmStatus().getStatusId())) {
							financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
							this.getManagedBean().setZonesWarnMessageUpdate(Boolean.TRUE);
							this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
							technicalchecklist.setPmStatus(status);

						}
					}

					// #28784 - Start
					if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(technicalchecklist.getDefinationType()
							.getParameterCode())) {
						if (null != technicalchecklist.getPmStatus()
								&& QTStatusConst.TS_COMPLETED.equals(technicalchecklist.getPmStatus().getStatusId())) {
							technicalchecklist.setPmStatus(status);
						}
					}
					// #28784 - End
				}
			}
			if (financialSpecZonesEstimatedpriceUpdate == true) {
				ZonesUpdateWarningMessage(this.getManagedBean().isZonesWarnMessageUpdate());

			}

		} finally {

		}

	}

	// #32929 - end

	private void updateTechnicalAndFinacialSpecStatusZoneLocationChange(ScQtZoneCriteria zoneCriteria) {
		try {
			SpecificationBackingBean specificationBackingBean = new SpecificationBackingBean();
			List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			if (null == technicalSpecCheckList || technicalSpecCheckList.isEmpty()) {
				specificationBackingBean.retriveTechnicalCheckList();
				technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			}

			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList || financialSpecCheckList.isEmpty()) {
				specificationBackingBean.retriveFinancialCheckList();
				financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			}
			if (null != financialSpecCheckList && !financialSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financialchecklist.getDefinationType().getParameterCode())) {
						this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
						financialchecklist.setPmStatus(status);

					}
				}
			}
			if (null != technicalSpecCheckList && !technicalSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				boolean zonelevelChange = false;
				for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
					zonelevelChange = true;
					// technicalchecklist.setPmStatus(status);
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(technicalchecklist.getDefinationType().getParameterCode())) {
						this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
						technicalchecklist.setPmStatus(status);

					}
				}
				if (zonelevelChange) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_760357, MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
				}

			}

		} finally {

		}

	}

	/**
	 * Ministry Specification Secretary Required radio change.
	 * 
	 * @param scQt
	 *            the scQt instance
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

		qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
		if (null != scQt && null != scQt.getProcurementMode() && ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt.getProcurementMode().getParameterCode())) {
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
		}

		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), qtTypes));

		return super.getRadioButtonCodeValuesBySearchBean(quotationTenderType, searchBean);
	}

	// 20190910@asyrafhaziq - 26404 -Start
	/**
	 * Set only the procurement mode.based on Estimated price set Procurement
	 * Mode(Quotation/Tender).
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void setProcurementModeOnly(ScQt scQt, BigDecimal amount) {

		log.info("setProcurementModeOnly,@qtNo = " + scQt.getQtNo());
		IParameterSO parameterSo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			boolean isQuotationThresHold = isQuotationThresHold(amount);
			boolean isPanel = scQt.isPanel();
			PmPtjSpecialApproval ptjSpecialApproval = getManagedBean().getPtjSpecialApproval();
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
			this.getManagedBean().setProcMethodDropdownPresent(false);
			if (isQuotationThresHold || isPanel) {
				parameterReqDTO.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
			} else if (BooleanUtils.isTrue(scQt.getObtainSpecialApproval()) && ptjSpecialApproval != null && !isQuotationThresHold
					&& ptjSpecialApproval.getProcureAmt() != null && amount != null && amount.compareTo(ptjSpecialApproval.getProcureAmt()) <= 0) {
				this.getManagedBean().setProcMethodDropdownPresent(true);
			} else {
				parameterReqDTO.setParameterCode(ParameterConst.PROCUREMENT_MODE_TENDER);
			}
			if (!this.getManagedBean().isProcMethodDropdownPresent()) {
				ParameterResDTO parameterResDTO = parameterSo.retrieveParameterByTypeAndCode(parameterReqDTO);
				scQt.setProcurementMode(parameterResDTO.getPmParameter());
			}
			log.info("setProcurementMode, @procurementMode = " + scQt.getProcurementMode());

		} finally {
			parameterSo = null;
		}
	}

	// 20190910@asyrafhaziq - 26404 -End

	// 20190415@asyrafhaziq - 15979 -Start
	/**
	 * Sets the Open for .based on Estimated price Mode(Quotation/Tender).
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void setOpenFor(ScQt scQt, BigDecimal amount) {
		debugLogger("setOpenFor,@qtNo = " + scQt.getQtNo());

		boolean isQuotationThresHold = isQuotationThresHold(amount);
		boolean isPanel = scQt.isPanel();

		if (amount != null) {
			// Bug #31605 -start-
			if (scQt.getQtType() == null || (amount.intValue() <= 500000 && !isPanel) || isPanel) {
				// Bug #31605 -end-
				// QT Type
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
				// Bug #31605 -start-
			}

			if (scQt.getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				// Bug #31605 -end-
				// Open For
				if (isPanel) {
					scQt.setOpenFor(null);
				} else {
					if (isQuotationThresHold && isOpenForBumiputraThresHold(amount)) {
						scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
					} else {
						boolean printed = false;
						printed = isPrintedForBumiputraOnly(this.getManagedBean().getQtCategoryList(), printed);

						if (printed || scQt.getOpenFor().equals(ParameterConst.OPEN_FOR_BUMI)) {
							scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
						} else {
							scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
						}
					}
				}
				// Bug #31605 -start-
			}
			// Bug #31605 -end-
		}
	}

	// 20190415@asyrafhaziq - 15979 -End

	/**
	 * Sets the procurement mode.based on Estimated price set Procuremnt
	 * Mode(Quotation/Tender).
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	// Enhancement #25729 -start-
	public void setProcurementMode(ScQt scQt, BigDecimal amount, String... enable) {
		// Enhancement #25729 -end-

		debugLogger("setProcurementMode,@qtNo = " + scQt.getQtNo());
		IParameterSO parameterSo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			BigDecimal estimatedAmount = scQt.getEstimatedAmount();
			boolean isQuotationThresHold = isQuotationThresHold(amount);
			boolean isPanel = scQt.isPanel();
			ScQtSupplierCriteria scQtSupplierCriteria = scQt.getScQtSupplierCriteria();
			String qtType = scQt.getQtType();

			// Intended Price
			if (isPanel) {
				BigDecimal panelThreshold = getPanelThresholdAmount();

				// Start 9019 #note-342
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(this.getManagedBean().getCurrentStatusId())) {
					if (null == amount || (amount.compareTo(panelThreshold) <= 0)) {
						throw new EpException(MessageConst.ERROR_760380,
								new String[] { SourcingConstants.DF_AMOUNT.format(panelThreshold) });
					}
				}
				// End 9019 
			} else {
				// Enhancement #25729 -start-
				// Start Reopen 9019 #note-289
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(this.getManagedBean().getCurrentStatusId())) {
					// #9019 note-289 -end-
					if (null == amount || (amount.compareTo(BigDecimal.ZERO) <= 0)) {
						throw new EpException(MessageConst.ERROR_760024,
								new String[] { SourcingConstants.DF_AMOUNT.format(BigDecimal.ZERO) });
					}
				} // 9019 #note-289

				if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(this.getManagedBean().getCurrentStatusId()) || this.getManagedBean().isCreateQt()) {
					scQt.setJustificationProcValue(null);
				}
				// Enhancement #25729 -end-
			}

			// Procurement Mode
			PmPtjSpecialApproval ptjSpecialApproval = getManagedBean().getPtjSpecialApproval();
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
			this.getManagedBean().setProcMethodDropdownPresent(false);
			if (isQuotationThresHold || isPanel) {
				parameterReqDTO.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
			} else if (BooleanUtils.isTrue(scQt.getObtainSpecialApproval()) && ptjSpecialApproval != null && !isQuotationThresHold
					&& ptjSpecialApproval.getProcureAmt() != null && amount != null && amount.compareTo(ptjSpecialApproval.getProcureAmt()) <= 0) {
				this.getManagedBean().setProcMethodDropdownPresent(true);
				// scQt.setProcurementMode(new PmParameter());
			} else {
				parameterReqDTO.setParameterCode(ParameterConst.PROCUREMENT_MODE_TENDER);
			}
			if (!this.getManagedBean().isProcMethodDropdownPresent()) {
				ParameterResDTO parameterResDTO = parameterSo.retrieveParameterByTypeAndCode(parameterReqDTO);
				scQt.setProcurementMode(parameterResDTO.getPmParameter());
			}

			debugLogger("setProcurementMode, @procurementMode = " + scQt.getProcurementMode());
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
				if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER.equalsIgnoreCase(qtType)) {
					// Open For - Restricted Tender
					updateRestrictedTenderOpenFor(scQt);
				}
			}

			// All State
			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(qtType)) {
				if (null != scQtSupplierCriteria) {
					if (!isQuotationThresHold && getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR) {

						scQtSupplierCriteria.setAllState(true);

					} else {
						if (null == scQtSupplierCriteria.getLocationLevel()) {
							scQtSupplierCriteria.setLocationLevel(new PmParameter());
						}

						scQtSupplierCriteria.setAllState(false);
					}
				}
			}

			// Reset zones details
			if (null != scQtSupplierCriteria
					&& ((scQtSupplierCriteria.isAllState() && ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(qtType)) || ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
							.equalsIgnoreCase(qtType))) {
				if (null != getManagedBean().getExistingSupplierLocationList()) {
					this.getManagedBean().setSupplierLocationList(getManagedBean().getExistingSupplierLocationList());
					getManagedBean().setExistingSupplierLocationList(null);
					getManagedBean().setZoneTrees(null);
				}
			}

			// Start Reopen 9019 #note-289
			//#35061 - remove to allow show the the panel 
			// #9019 note-289 -end-
			// 25729#174 start
			// Bug #35061 - asyrafhaziq -start-
			// get total indicative price threshold validation value
			BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();

			if ((!isPanel && enable.length > 0) && (amount.compareTo(indPriceThreshold) <= 0)) {
				// set message
				String msg = getMessage(SourcingConstants.ALERT_MESSAGE_BELOW_QUATATION_VALUE,
						new String[] { String.valueOf(indPriceThreshold).trim() });
				// SONARQUBE QT 2023 - haziq
				this.getManagedBean().setMsgConfirmBelowIndi(msg.trim());
				// Bug #35061 -end-
				RequestContext.getCurrentInstance().execute("proceedBelowQtValueDlgs.show()");
			}
			// 25729#174 end
		 // 9019 #note-289

		} finally {
			parameterSo = null;
		}
	}

	// Enhancement #25729 - start -
	public boolean enableJustificationProValue(boolean enable) {
		if (!enable) {
			this.getManagedBean().getQt().setTmpExpectedIntendedPrice(null);
		}

		return enable;
	}

	// Enhancement #25729 - end -

	/**
	 * Sets the evaluation type.based on Estimated Price set Evaluation Type.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void populateEvaluationTypes(ScQt scQt, BigDecimal estimatedAmount) {

		debugLogger("populateEvaluationTypes,@qtNo = " + scQt.getQtNo());
		if (isOneTierThresHold(estimatedAmount)) {

			List<SelectItem> itemList = new ArrayList<SelectItem>();
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				String evaluationTypeCode = paramDesc.getPmParameter().getParameterCode();
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationTypeCode)) {

					itemList.add(new SelectItem(evaluationTypeCode, paramDesc.getCodeName()));
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
		String type = (String) event.getComponent().getAttributes().get("value");
		ScQt scQt = this.getManagedBean().getQt();

		this.getManagedBean().setQtCategoryList(new ArrayList<ScQtCategory>());
		if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(type)) {

			if (null != scQt.getScQtSupplierCriteria() && null == scQt.getScQtSupplierCriteria().getLocationLevel()) {
				scQt.getScQtSupplierCriteria().setLocationLevel(new PmParameter());
			}

			this.getManagedBean().setToBeRemoveRestrictedSupplierList(this.getManagedBean().getQtSupplierList());
			this.getManagedBean().setQtSupplierList(null);
			scQt.setScQtSuppliers(null);

		} else {
			if (scQt.getTmpExpectedIntendedPrice() == null) {

				String labelKey = ResourceBundleUtils.getMessage("lbl.sc.spec.expected.intended.price", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
				throw new EpException(labelKey + " is mandatory.");
			}
			if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER.equalsIgnoreCase(type)) {
				this.getManagedBean().setSupplierLocationList(getManagedBean().getExistingSupplierLocationList());

				updateRestrictedTenderOpenFor(scQt);
				if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
						&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType()) && scQt.isZonal()) {
					updateTechnicalAndFinacialSpecStatusZoneLocationChange(null);
					this.getManagedBean().setDeliveryLocRemoveList(null);
					this.getManagedBean().setDeliveryLocBean(null);
					deleteDeliveryAdreesZoneDetails(this.getManagedBean().getQt());
				}

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
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(getManagedBean().getQt());

			if (qtDetailSO.isRestrictedTenderOpenForBumi(reqDTO)) {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
				clearNonBumiSuppliers(scQt);
			} else {
				scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
				List<ScQtSupplier> supplierList = this.getManagedBean().getQtSupplierList();
				scQt.setScQtSuppliers(supplierList);
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
		String type = (String) event.getComponent().getAttributes().get("value");
		this.getManagedBean().getQt().setOpenFor(type);
	}
	
	/** #21695 FARID START */
	/**
	 * Partial offer change.change partialy offer(offer/not offer) radion button
	 * in owner.
	 * 
	 * @param event
	 *            (String partialChange)
	 */
	public void partialChange(AjaxBehaviorEvent event) {
		Boolean type = (Boolean) event.getComponent().getAttributes().get("value");
	
		this.getManagedBean().getQt().setPartial(type);
		
		if (type == false) {
			RequestContext.getCurrentInstance().execute("confirmDialogWidgetVar.hide()");
		} else {
			RequestContext.getCurrentInstance().execute("confirmDialogWidgetVar.show()");
		}
	}
	/** #21695 FARID END */
	
	/** #21695 FARID START */
	public void partialChangePopUpNo() {
		if (this.getManagedBean().getQt().isPartial() == false) {
			this.getManagedBean().getQt().setPartial(true);
		} else if (this.getManagedBean().getQt().isPartial() != false) {
			this.getManagedBean().getQt().setPartial(false);
		}
	}
	/** #21695 FARID END */
	
	/** #21695 FARID START */
	public void partialChangePopUpYes() {
		if (this.getManagedBean().getQt().isPartial() != true) {
			this.getManagedBean().getQt().setPartial(false);
		} else if (this.getManagedBean().getQt().isPartial() == true) {
			this.getManagedBean().getQt().setPartial(true);
		}
		
		this.getManagedBean().getQt().setSampleReq(Boolean.FALSE); // #11104
	}
	/** #21695 FARID END */	

	/**
	 * Procurement change.change Procurement Category Dropdown in Business
	 * owner.
	 * 
	 * @param event
	 *            (Long procuremntCategoryId).
	 */
	public void procurementChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes().get("value");

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId().equals(Long.valueOf(type))) {

					this.getManagedBean().setProcuremntCategory(paramDesc.getPmParameter().getParameterCode());
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
			Long type = (Long) event.getComponent().getAttributes().get("value");
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				// Start Reopen 9019 #note-289
				if (null != type) {
					// #9019 note-289 -end-
					if (paramDesc.getPmParameter().getParameterId().equals(Long.valueOf(type))) {

						this.getManagedBean().setFulfilmentType(paramDesc.getPmParameter().getParameterCode());
						this.getManagedBean().getQt().setFulfilmentType(paramDesc.getPmParameter());
						break;
					}
				} // 9019 #note-289
			}

			if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getQt().setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			} else if (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getQt().setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
				this.getManagedBean().setTotalProgressivePayment(0);

			} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getQt().setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
				this.getManagedBean().setTotalFrequency(0);
				this.getManagedBean().setTotalProgressivePayment(0);

			}
			this.getManagedBean().getQt().setScQtSchedules(null);
			this.getManagedBean().getQt().setScQtProgressivePayments(null);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			// 20210615@asyrafahaziq - Bug #30891 - Start
			// 9019 Start - Harun change comparing null done by Haziq
			if (null != this.getManagedBean().getQt().getContractDuration()) {
				ContractPeriodChange();
			}
			// 9019 End
			// 20210615@asyrafahaziq - Bug #30891 - End
		} finally {

		}

	}

	private void removefullfillmentDetails(String scheduleType) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean().getScheduleList();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();
				if (null == this.getManagedBean().getToBeRemoveSchedules() || this.getManagedBean().getToBeRemoveSchedules().isEmpty()) {
					this.getManagedBean().setToBeRemoveSchedules(new ArrayList<ScQtSchedule>());

				}
				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().getToBeRemoveSchedules().addAll(toBeRemoveList);
				this.getManagedBean().getQt().setScQtSchedules(null);
			}
			this.getManagedBean().setScheduleList(new ArrayList<ScQtSchedule>());
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean().getProgressivePaymentList();
			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();
				if (null == this.getManagedBean().getToBeRemoveProgPayments() || this.getManagedBean().getToBeRemoveProgPayments().isEmpty()) {
					this.getManagedBean().setToBeRemoveProgPayments(new ArrayList<ScQtProgressivePayment>());

				}

				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().getToBeRemoveProgPayments().addAll(toBeRemoveList);
				this.getManagedBean().getQt().setScQtProgressivePayments(null);
			}
			this.getManagedBean().setProgressivePaymentList(new ArrayList<ScQtProgressivePayment>());
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
	 * Select zone location level.change Location Level Dropdown in General
	 * information menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectZoneLocationLevel(AjaxBehaviorEvent event) {
		// Bug #36730@Syazani 07/01/2025 -- Start
		if (this.getManagedBean().getQt().isZonal() && null != this.getManagedBean().getZoneGeneralTrees()
				&& !this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
			// Bug #36730@Syazani 07/01/2025 -- End
			this.getManagedBean().setZoneGeneralTrees(null);
			this.getManagedBean().setBeans(new ArrayList<ZonesBean>());

			// Bug #36730@Syazani 07/01/2025 -- Start
			Long eventLocationLevelId = (Long) event.getComponent().getAttributes().get("value");
			if (null != eventLocationLevelId) {
				reinstateZoneLocationLevel(eventLocationLevelId);
			}
			// Bug #36730@Syazani 07/01/2025 -- End
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
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if (null != childNodes && childNodes.size() > 0) {

			for (int i = 0; i < childNodes.size(); i++) {
				childNodes.get(i).setSelected(true);

			}
		} else {

			selectedTreeNode.setSelected(true);

		}
	}

	/**
	 * Un select zone tree node.Event handle un check Non panel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if (null != childNodes && childNodes.size() > 0) {

			for (int i = 0; i < childNodes.size(); i++) {
				childNodes.get(i).setSelected(false);

			}
		} else {

			selectedTreeNode.setSelected(false);
		}
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
	public void editZones(TreeNode rootZoneLocation, int index, boolean disabledPopupSelectable) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = getManagedBean().getQt().getZoneLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter().getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId().equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter().getParameterCode();

					break;
				}
			}

			List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(countryResDTO.getPmCountry().getCountryId(), states, districts, divisions, cities,
							locationLevelCode, TplSpecZoneLoc.class, disabledPopupSelectable));
			int i = index;
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setIndex(i);
			getManagedBean().setZoneName(locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
			getManagedBean().setTempZoneName(locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
			this.getManagedBean().setStatecode(locationLevelCode);
		} finally {
			parameterSo = null;
			countrySo = null;
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
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation.getData();
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
			// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
			if (!this.getManagedBean().getQt().isPanel()) {
				// 20200210@asyrafhaziq_Bug #27381&#26694 - End
				Long locationLevel = this.getManagedBean().getQt().getZoneLocationLevel().getParameterId();

				countrySo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
				if (locationLevel == null) {

					throw new EpException(MessageConst.ERROR_760063);
				}

				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

				for (PmParameterDesc paramDesc : localParamDesc) {

					if (paramDesc.getPmParameter().getParameterId().equals(locationLevel)) {
						this.getManagedBean().setZoneLocation(true);
						this.getManagedBean().setStatecode(paramDesc.getPmParameter().getParameterCode());
						CountryReqDTO countryReqDTo = new CountryReqDTO();
						countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
						CountryResDTO countryResDTO = countrySo.retrieveCountryByCode(countryReqDTo);
						getManagedBean().setRootZoanlTree(
								LocationSingleton.getInstance().buildLocationPopupTree(countryResDTO.getPmCountry().getCountryId(), null, null, null, null,
										paramDesc.getPmParameter().getParameterCode(), TplSpecZoneLoc.class, false));
						this.getManagedBean().setIndex(-1);
						this.getManagedBean().setZoneName("");
					}

				}
				// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
			} else {
				this.getManagedBean().setIndex(-1);
			}
			// 20200210@asyrafhaziq_Bug #27381&#26694 - End
			this.getManagedBean().setAutoUpdate(false);
			RequestContext.getCurrentInstance().execute("ZoneGeneral.show()");
		} finally {
			countrySo = null;
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
			// throw new EpException(MessageConst.ERROR_760146);
		}
		if (!rootTrees.isEmpty()) {

			ScQtZoneCriteria zonCriteria = getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias();
			zonCriteria.setTplSpecZoneDtls(convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
			// setting into zones bean to validate zones while adding
			if (CollectionUtils.isNotEmpty(zonCriteria.getTplSpecZoneDtls())) {
				List<ZonesBean> zonBeansList = new ArrayList<ZonesBean>();
				for (TplSpecZoneDtl tplSpecZoneDtl : zonCriteria.getTplSpecZoneDtls()) {
					ZonesBean bean = new ZonesBean();
					bean.setTplSpecZoneDtl(tplSpecZoneDtl);
					zonBeansList.add(bean);
				}
				this.getManagedBean().setBeans(zonBeansList);
			}
		}
		// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
		// set ZoneGeneral Trees Panel
		ScQt scQt = getManagedBean().getQt();
		if (scQt.isZonal() && scQt.isPanel()) {
			List<TplSpecZoneDtl> specZoneDtls = null;

			if (!rootTrees.isEmpty()) {
				specZoneDtls = convertTreeNodesToZoneCriteria(rootTrees, zoneDtl);
			} else {
				specZoneDtls = new ArrayList<TplSpecZoneDtl>();
			}
			retrieveZoneGeneralTreesPanel(scQt, specZoneDtls);
		}
		// 20200210@asyrafhaziq_Bug #27381&#26694 - End
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria && zoneSupplierCriteria.isBrandNew() == false && null != zoneSupplierCriteria.getQtZoneCriterias()
				&& zoneSupplierCriteria.getQtZoneCriterias().isBrandNew() == false) {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				updateFinacialSpecStatus();
				this.getManagedBean().setDeliveryLocRemoveList(null);
				this.getManagedBean().setDeliveryLocBean(null);
				deleteDeliveryAdreesZoneDetails(this.getManagedBean().getQt());
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
			if (null != this.getManagedBean().getQt().getScQtSupplierCriteria() && null != this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias().setModified(true);
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

						removedList = deleteZoneChildTreeNodes(zoneTree.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getZoneLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl, locationDTOs);
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
	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl, List<TplSpecZoneLoc> tplSpecZoneLocs) {

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
						if (!zone.getZoneName().equalsIgnoreCase(tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null && zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc.getPmDivision().getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc.getPmDistrict().getDistrictId())) {
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
					if (treeNode.getChildren() != null && treeNode.getChildren().size() > 0) {
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
					if (treeNode.getChildren() != null && treeNode.getChildren().size() > 0) {

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
			this.getManagedBean().setTotalSuppliersSpecLoc(new ArrayList<TplSpecLoc>());
		}
		List<ZonesBean> beans = getManagedBean().getBeans();
		if (null == beans) {
			beans = new ArrayList<ZonesBean>();
		}
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getStatecode();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedGeneralTreeNodes()) {

				if (locationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
					if (treeNode.getType().equals("state")) {
						if (treeNode.getChildCount() == 0 && treeNode.getChildren().isEmpty()) {
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

			if (null != this.getManagedBean().getQt().getScQtSupplierCriteria() && null != this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias().setModified(true);
			}
			TplSpecZoneDtl zone = new TplSpecZoneDtl();

			if (null == getManagedBean().getZoneName() || StringUtils.isBlank(getManagedBean().getZoneName())) {
				throw new EpException("Zone Name is Mandatory.");
			}

			zone.setZoneName(getManagedBean().getZoneName());

			List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(allNodes);

			List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(allNodes);

			List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(allNodes);

			List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(allNodes);
			String editZoneName = getManagedBean().getTempZoneName();
			if (getManagedBean().getIndex() >= 0) {
				updateZoneBean(editZoneName);
			}
			zone.setTplSpecZoneLocs(allNodes);
			zonesBean.setTplSpecZoneDtl(zone);
			// zonesBean.setTplSpecZoneDtl(zone);
			// validate zone name
			checkZoneExistsOrNot(zone);

			if (locationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
				// check whether state exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, -1);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
				// check whether division exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, -1);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				// check whether district exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, -1);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			beans.add(zonesBean);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneGeneralTrees().add(
						LocationSingleton.getInstance().buildLocationTree(zone, states, districts, divisions, cities, TplSpecZoneLoc.class));

			}

			else if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneGeneralTrees().remove(getManagedBean().getIndex());
				getManagedBean().getZoneGeneralTrees().add(getManagedBean().getIndex(),
						LocationSingleton.getInstance().buildLocationTree(zone, states, districts, divisions, cities, TplSpecZoneLoc.class));
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
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria && zoneSupplierCriteria.isBrandNew() == false && null != zoneSupplierCriteria.getQtZoneCriterias()
				&& zoneSupplierCriteria.getQtZoneCriterias().isBrandNew() == false) {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				updateFinacialSpecStatus();
				this.getManagedBean().setDeliveryLocRemoveList(null);
				this.getManagedBean().setDeliveryLocBean(null);
				deleteDeliveryAdreesZoneDetails(this.getManagedBean().getQt());
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

					if (zone.getZoneName().equalsIgnoreCase(tplSpecZoneDtl.getZoneName())) {
						throw new EpException(tplSpecZoneDtl.getZoneName() + " zone already exists.");
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
	public void checkDuplicateStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl, int index) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (null != tplSpecZoneDtl.getTplSpecZoneLocs() && tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
			List<TplSpecZoneLoc> currentTplSpecLocs = tplSpecZoneDtl.getTplSpecZoneLocs();
			stateIds = getPmStateIds(currentTplSpecLocs);
			divIds = getPmDivisionIds(currentTplSpecLocs);
			distIds = getPmDistrictsIds(currentTplSpecLocs);
		}

		List<ZonesBean> list = getManagedBean().getBeans();
		ZonesBean bean = null;
		// skip the validation for current edited zone under the supplier Qualification tab 
		if (index >= 0) {
			bean = getManagedBean().getBeans().get(index);
			getManagedBean().getBeans().remove(index);
		}

		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();

				if (prevsZone != null) {

					if (prevsZone.getTplSpecZoneLocs() != null && prevsZone.getTplSpecZoneLocs().size() > 0) {
						List<TplSpecZoneLoc> previousTplSpecLocs = prevsZone.getTplSpecZoneLocs();
						for (TplSpecZoneLoc TplSpecZoneLoc : previousTplSpecLocs) {

							if (TplSpecZoneLoc.getPmState() != null) {

								if (stateIds != null && stateIds.size() > 0) {

									if (stateIds.contains(TplSpecZoneLoc.getPmState().getStateId())) {
										if (bean != null) {
											getManagedBean().getBeans().add(index, bean);
										}
										throw new EpException(MessageConst.ERROR_760168, new String[] { TplSpecZoneLoc.getPmState().getStateName(),
												prevsZone.getZoneName() });
									}
								}

							}

							if (TplSpecZoneLoc.getPmDivision() != null) {
								if (divIds != null && divIds.size() > 0) {
									if (divIds.contains(TplSpecZoneLoc.getPmDivision().getDivisionId())) {
										if (bean != null) {
											getManagedBean().getBeans().add(index, bean);
										}
										throw new EpException(MessageConst.ERROR_760167, new String[] { TplSpecZoneLoc.getPmDivision().getDivisionName(),
												prevsZone.getZoneName() });
									}
								}
							}

							if (TplSpecZoneLoc.getPmDistrict() != null) {
								if (distIds != null && distIds.size() > 0) {
									if (distIds.contains(TplSpecZoneLoc.getPmDistrict().getDistrictId())) {
										if (bean != null) {
											getManagedBean().getBeans().add(index, bean);
										}
										throw new EpException(MessageConst.ERROR_760166, new String[] { TplSpecZoneLoc.getPmDistrict().getDistrictName(),
												prevsZone.getZoneName() });
									}
								}
							}
						}
					}
					if (prevsZone.getZoneName().equals(tplSpecZoneDtl.getZoneName())) {
						if (bean != null) {
							getManagedBean().getBeans().add(index, bean);
						}
						throw new EpException(MessageConst.ERROR_700027, new String[] { ResourceBundleUtils.getMessage(tplSpecZoneDtl.getZoneName(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
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
			Long orgProfileId = this.getManagedBean().getQt().getPmOrgProfile().getOrgProfileId();
			if (null != orgProfileId) {
				return "quotationTender.pmOrgProfile.orgProfileId^" + orgProfileId;
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

		IOrgProfileSO orgProfile = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileResDTO orgResDto = orgProfile.retrieveOrgStateCodeByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
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
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode().equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter().getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		orgProfile = null;
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
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			// Start Reopen 9019 #note-289
			if (null != parameterId) {
				// #9019 note-289 -end-
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterId(parameterId);
				
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				
				this.getManagedBean().getQt().setContractType(resDTO.getPmParameter());
			
				if (null != this.getManagedBean().getQt().getContractType()
						&& ParameterConst.CONTRACT_TYPE_CENTRAL.equalsIgnoreCase(this.getManagedBean().getQt()
								.getContractType().getParameterCode())) {
					ParameterReqDTO fulfilMentAsandWhen = new ParameterReqDTO();
					fulfilMentAsandWhen.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
					fulfilMentAsandWhen.setParameterCode(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
					
					ParameterResDTO fulfilMentAsandWhenresDTO = parameterSO
							.retrieveParameterByTypeAndCode(fulfilMentAsandWhen);
					
					this.getManagedBean().getQt().setFulfilmentType(fulfilMentAsandWhenresDTO.getPmParameter());
				}
			} // 9019 #note-289

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

		IPanelSupplierSO panelSupplierSO;
		ICircularDetailSO circularDetailSO;

		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);

			reqDTO.setCircularVerId(scQt.getCircularVer().getCircularVerId());
			reqDTO.setPanelItemTypeGroup(true);
			if (null != this.getManagedBean().getQt().getItemGroup()) {
				reqDTO.setGroupItemId(scQt.getItemGroup().getPanelItemGroupId());
			}

			CircularDetailResDTO resDTO = circularDetailSO.getPanelItemsByCircularVerId(reqDTO);
			this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());

			PanelSupplierReqDTO reqPanelDTO = new PanelSupplierReqDTO();
			PanelSupplierResDTO resPanelDTO = new PanelSupplierResDTO();
			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
			for (CtPanelItem panelItem : this.getManagedBean().getPanelItemList()) {

				reqPanelDTO.setPanelItem(panelItem);
				resPanelDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqPanelDTO);
				if (panelItem.getLocationLevelId() != null && panelItem.getLocationLevelId().getParameterId() != null)
					scQt.setZoneLocationLevel(getPmParameter(panelItem.getLocationLevelId().getParameterId()));
				break;
			}

			ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
			if (null != resDTO && null != resPanelDTO.getCtPanelZones() && !resPanelDTO.getCtPanelZones().isEmpty()) {
				scQt.setZonal(true);
				this.getManagedBean().setPanelZone(true);

				/* to show zone information panel */
				if (null == supplierCriteria) {
					supplierCriteria = new ScQtSupplierCriteria();
				}

				ScQtZoneCriteria zoneCriteria = supplierCriteria.getQtZoneCriterias();
				if (null == zoneCriteria) {
					zoneCriteria = new ScQtZoneCriteria();
					zoneCriteria.setScQtSupplierCriteria(supplierCriteria);
					supplierCriteria.setQtZoneCriterias(zoneCriteria);
				}
				supplierCriteria.setScQtLocCriteria(null);

				if (supplierCriteria.isAllState() == true) {
					this.getManagedBean().setZoneTrees(null);
				}

				if (null != supplierCriteria && supplierCriteria.isBrandNew() == false) {
					if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
							&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
						updateTechnicalAndFinacialSpecStatusZoneLocationChange(zoneCriteria);
					}
				}
				scQt.setScQtSupplierCriteria(supplierCriteria);

				/* set tree zonal information */
				if (isNotNullOREmpty(resPanelDTO.getCtPanelZones())) {
					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
					// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
					getManagedBean().setCtPanelZones(resPanelDTO.getCtPanelZones());
					// 20200210@asyrafhaziq_Bug #27381&#26694 - End
					if (null != getManagedBean().getZoneGeneralTrees() && getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resPanelDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper.getStateFromCtPanelZone(panelZones.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromCtPanelZone(panelZones.getPanelZoneDtls());
							// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
							getManagedBean().getZoneGeneralTrees().add(
									LocationSingleton.getInstance().buildTreeWithDisabledChilds(tplSpecZoneDtl, states, districts, null, null, false,
											TplSpecZoneLoc.class));

							// 20200210@asyrafhaziq_Bug #27381&#26694 - End
						}
					}
				}

			} else {

				scQt.setZonal(false);

				this.getManagedBean().setZoneGeneralTrees(null);
				// supplierCriteria.setQtZoneCriterias(null);
				if (null == supplierCriteria.getLocationLevel()) {
					supplierCriteria.setLocationLevel(new PmParameter());
				}

				this.getManagedBean().setZoneGeneralTrees(null);
				this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
				if (null != supplierCriteria && supplierCriteria.isBrandNew() == false) {
					if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
							&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
						updateTechnicalAndFinacialSpecStatusZoneLocationChange(null);
						this.getManagedBean().setDeliveryLocRemoveList(null);
						this.getManagedBean().setDeliveryLocBean(null);
						deleteDeliveryAdreesZoneDetails(scQt);
					}
				}

				if (null != scQt.getItemGroup() && null != scQt.getItemGroup().getPanelItemGroupId()) {
					if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
							&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
						deleteTemplateSpecs(scQt);
					}
				}
			}
			// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
			// reset zoneGeneralTreesPanel
			if (isNotNullOREmpty(getManagedBean().getZoneGeneralTreesPanel())) {
				getManagedBean().setZoneGeneralTreesPanel(null);
			}
			// 20200210@asyrafhaziq_Bug #27381&#26694 - End
		} finally {
			panelSupplierSO = null;
			circularDetailSO = null;
		}

	}

	private PmParameter getPmParameter(Long parameterId) {
		IParameterSO parameterSO;

		try {
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(parameterId);
			return parameterSO.retrieveParameterById(parameterReqDTO).getPmParameter();
		} finally {
			parameterSO = null;
		}
	}
	
	/**
	 * Panel conformation delete.change panel yes to No or No to yes
	 * Confirmation to delete Existing data.
	 * 
	 * @return navigate to busineess owner menu pages.
	 */
	public void panelConformationDelete() {
		Locale locale = new FacesScopes().getUserLocale(); // 9019 #Note-501

		setDataNull();
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria && zoneSupplierCriteria.isBrandNew() == false && null != zoneSupplierCriteria.getQtZoneCriterias()
				&& zoneSupplierCriteria.getQtZoneCriterias().isBrandNew() == false) {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				this.getManagedBean().setDeliveryLocRemoveList(null);
				deleteDeliveryAdreesZoneDetails(getManagedBean().getQt());
			}
		}

		// 9019 Start - (Harun) #Note-501
		if (StringUtils.isBlank(getManagedBean().getQt().getQtTitle())) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.title", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}

		boolean emptyRemarksExist = false;
		
		if (null != this.getManagedBean().getQt().getScQtRemarks()
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			for (int i = 0; i < this.getManagedBean().getQt().getScQtRemarks().size(); i++) {
				String userRemark = this.getManagedBean().getQt().getScQtRemarks().get(i).getRemark();
			
				if (null == userRemark || "".equals(userRemark)) {
					emptyRemarksExist = true;
				} else {
					emptyRemarksExist = false;
				}
			}

			if (emptyRemarksExist) {
				this.getManagedBean().getQt().setScQtRemarks(null);
			}
		}
		//9019 End 
		
		removespecDtls();

		ScQt scQt = this.getManagedBean().getQt();
		scQt.setZonal(false);
		scQt.setZoneLocationLevel(null);
		scQt.setPanel(this.getManagedBean().getPanel());
		if (null == scQt.getPmProcurementCategory()) {
			scQt.setPmProcurementCategory(new PmParameter());
		}
		if (null == scQt.getZoneLocationLevel()) {
			scQt.setZoneLocationLevel(new PmParameter());
		}
		if (null == scQt.getProcurementTypeCatId()) {
			scQt.setProcurementTypeCatId(new PmParameter());
		}
		if (scQt.isPanel()) {
			circularListSelect(new CtCircularVer());
			if (null == scQt.getItemGroup()) {
				scQt.setItemGroup(new CtPanelItemGroup());
			}
		}
		
		// 9019 Start - (Harun) #Note-501
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else {
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}

		if (null == this.getManagedBean().getQt().getProcurementTypeCatId()) {
			this.getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
			this.getManagedBean().getQt().getProcurementTypeCatId().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {
			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());
			this.getManagedBean().getQt().getFulfilmentType().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getPmProcurementCategory()) {
			this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());
			this.getManagedBean().getQt().getPmProcurementCategory().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
			this.getManagedBean().getQt().getContractType().setParameterId((long) 0);
		}
		//9019 - End (Harun)
		
		RequestContext.getCurrentInstance().execute("panelApprovalDlgs.hide()");
	}

	private void setDataNull() {
		getManagedBean().setZoneGeneralTrees(null);
		getManagedBean().setZoneTrees(null);
		// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
		getManagedBean().setZoneGeneralTreesPanel(null);
		getManagedBean().setBeans(new ArrayList<ZonesBean>());
		// 20200210@asyrafhaziq_Bug #27381&#26694 - End
		this.getManagedBean().setScQtCheckSt2techProposal(null);
		this.getManagedBean().setScQtCheckSt2financeProposalList(null);
		this.getManagedBean().setQtCategoryList(null);

		ScQt scQt = this.getManagedBean().getQt();
		// populate procurment mode values if changes as Non panel
		if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(scQt.getQtStatus().getStatusId())) {
			BigDecimal amount = scQt.getTmpExpectedIntendedPrice();

			// Start - 25729
			if (null != amount) {
				// 25729#174 Change 1 line code
				// Bug #35061 - asyrafhaziq -start-
				// get total indicative price threshold validation value
				BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();

				if (amount.compareTo(indPriceThreshold) <= 0) {
					// Bug #35061 -end-
					setProcurementMode(scQt, amount, "Y");
				} else {
					setProcurementMode(scQt, amount);
				}
			}
			// End - 25729
		}
	}

	/**
	 * Removespec dtls.change Panle yes to no or no to yes delete all the
	 * existing information.
	 */
	private void removespecDtls() {
		IQTSpecSO specSo = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			if (null != this.getManagedBean().getExistingSupplierLocationList() && !this.getManagedBean().getExistingSupplierLocationList().isEmpty()) {
				this.getManagedBean().setSupplierLocationList(this.getManagedBean().getExistingSupplierLocationList());
				List<ScQtLocCriteria> qtLocCriteriaRemoveList = this.getManagedBean().getSupplierLocationList();
				if (null != qtLocCriteriaRemoveList && !qtLocCriteriaRemoveList.isEmpty()) {
					for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
						criteria.setDeleted(true);
					}
				}
				reqDTO.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
			}

			reqDTO.setScQt(this.getManagedBean().getQt());
			SpecificationResDTO resDTO = specSo.removeBOSpecDetails(reqDTO);
			this.getManagedBean().setQt(resDTO.getScQt());
			this.getManagedBean().setExistingSupplierLocationList(new ArrayList<ScQtLocCriteria>());
			this.getManagedBean().setSupplierLocationList(new ArrayList<ScQtLocCriteria>());
			this.getManagedBean().setQtCategoryList(new ArrayList<ScQtCategory>());
		} finally {
			specSo = null;
		}
	}

	/**
	 * Set the FileNo.
	 * 
	 * @param ScQt
	 * 		the scQt
	 */
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

		updateQtAmmendMentDtls(event.getComponent().getId(), event.getOldValue(), event.getNewValue());

	}

	/**
	 * Publish Period Days value change listener
	 * 
	 * @param changeEvent
	 * 		the value change event
	 */
	public void publishPeriodDaysChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(), changeEvent.getOldValue(), changeEvent.getNewValue());

	}

	/**
	 * Proposal Validity value change listener
	 * 
	 * @param changeEvent
	 * 		the value change event
	 */
	public void proposalValidityChanged(ValueChangeEvent changeEvent) {

		updateQtAmmendMentDtls(changeEvent.getComponent().getId(), changeEvent.getOldValue(), changeEvent.getNewValue());

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
	private void updateQtAmmendMentDtls(String componentId, Object oldValue, Object newValue) {
		QtAmendMentBean amendMentBean = null;

		if ("title1".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FIELD_QT_TITLE, oldValue, newValue, false);

		} else if ("proposalvalidity".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FIELD_QT_VALIDITY_PERIOD, oldValue, newValue, false);

		} else if ("publishperiod".equalsIgnoreCase(componentId)) {
			amendMentBean = putIntoAmendmentValueMap(QTFieldConst.FIELD_QT_PUBLISH_PERIOD, oldValue, newValue, true);
		}

		this.getManagedBean().setAmendMentBean(amendMentBean);
	}

	private QtAmendMentBean putIntoAmendmentValueMap(String field, Object oldValue, Object newValue, boolean ptjApprovalReqFlg) {
		QtAmendMentBean amendMentBean = this.getManagedBean().getAmendMentBean();

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

		// #29425 - start
		QTDetailBackingBean qtDtl = new QTDetailBackingBean();

		if (field.equalsIgnoreCase(SourcingConstants.QT_VALIDITY_PERIOD)) {
			if (null != newValue) {
				qtDtl.validateNewValueForValidityDays((int) newValue);
			}

			//#21692 - Start
			if (null != newValue && null != valueBean.getOldValue()
					&& (int) newValue > (int) valueBean.getOldValue()) {
				this.getManagedBean().setProposalValidityChangeValue(true);
			} else {
				this.getManagedBean().setProposalValidityChangeValue(false);
			}
			//#21692 - End
		}
		// #29425 - end

		if (valueBean.getOldValue() != null && newValue != null) {
			if (!valueBean.getOldValue().equals(newValue)) {
				if (ptjApprovalReqFlg) {
					amendMentBean.setPTJApprovalReq(true);
				} else {
					valueBean.setApprovalDate(new Date());
					valueBean.setIsApproved(true);
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
					new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_760328, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		}

	}

	private void updateFinacialSpecStatus() {
		try {

			List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			if (null == technicalSpecCheckList || technicalSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean = new SpecificationBackingBean();
				specBackingBean.retriveTechnicalCheckList();
				technicalSpecCheckList = this.getManagedBean().getScQtCheckSt2techProposal();
			}
			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList || financialSpecCheckList.isEmpty()) {
				SpecificationBackingBean specBackingBean = new SpecificationBackingBean();
				specBackingBean.retriveFinancialCheckList();
				financialSpecCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			}
			boolean financialSpecZonesEstimatedpriceUpdate = Boolean.FALSE;
			if (null != financialSpecCheckList && !financialSpecCheckList.isEmpty()) {

				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financialchecklist.getDefinationType().getParameterCode())) {
						if (null != financialchecklist.getPmStatus() && QTStatusConst.TS_COMPLETED.equals(financialchecklist.getPmStatus().getStatusId())) {
							financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
							this.getManagedBean().setZonesWarnMessageUpdate(Boolean.TRUE);
							this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
							financialchecklist.setPmStatus(status);

						}
					}

				}
			}
			if (null != technicalSpecCheckList && !technicalSpecCheckList.isEmpty()) {
				PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				// boolean zonelevelChange = false;
				for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
					// zonelevelChange = true;
					// technicalchecklist.setPmStatus(status);
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(technicalchecklist.getDefinationType().getParameterCode())) {
						// technicalchecklist.setPmStatus(status);
						if (null != technicalchecklist.getPmStatus() && QTStatusConst.TS_COMPLETED.equals(technicalchecklist.getPmStatus().getStatusId())) {
							financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
							this.getManagedBean().setZonesWarnMessageUpdate(Boolean.TRUE);
							this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
							technicalchecklist.setPmStatus(status);

						}
					}

					// #28784 - Start
					if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(technicalchecklist.getDefinationType()
							.getParameterCode())) {
						if (null != technicalchecklist.getPmStatus()
								&& QTStatusConst.TS_COMPLETED.equals(technicalchecklist.getPmStatus().getStatusId())) {
							technicalchecklist.setPmStatus(status);
						}
					}
					// #28784 - End
				}
			}
			if (financialSpecZonesEstimatedpriceUpdate == true) {
				ZonesUpdateWarningMessage(this.getManagedBean().isZonesWarnMessageUpdate());

			}

		} finally {

		}

	}

	/**
	 * method to search PanelItem PagingDataModel.
	 */
	public void searchAllPanelItems() {

		IPagingDataModel<CtPanelItem> panelItemDataModel = getManagedBean().getSearchDataModelPanelItem();
		if (null == panelItemDataModel) {
			getManagedBean().setSearchDataModelPanelItem(new panelItemPagingDataModel("viewCircularItemsGroupIds"));
		}
		// getManagedBean().getSearchDataModelPanelItem().resetData();
	}

	/**
	 * The Class panelItemPagingDataModel.
	 */
	private class panelItemPagingDataModel extends PagingDataModel<CtPanelItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new panelItem paging data model.
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
		protected ISearchResultBean<CtPanelItem> pageChangeListener(ISearchBean<CtPanelItem> searchBean) {

			ICircularDetailSO circularDetailsSo = null;
			try {
				circularDetailsSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);
				CircularDetailReqDTO circularReqDTO = new CircularDetailReqDTO();
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
	private void populateItemSearchCriteria(ISearchBean<CtPanelItem> itemSearchBean) {

		itemSearchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircularVer.qt", SearchOperator.OPERATOR_EQUALS.value(), true));

		itemSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("ctCircularVer.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_PANEL_CIRCULAR));

		itemSearchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircularVer.ctCircular.recordStatus", SearchOperator.OPERATOR_EQUALS.value(), true));

		Date todayDate = new Date();
		Calendar c1 = Calendar.getInstance();
		c1.setTime(todayDate);
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		fromDate.set(Calendar.MILLISECOND, 0);
		itemSearchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircularVer.endDate", SearchOperator.OPERATOR_GREATER_EQUAL.value(), fromDate));

	}

	/**
	 * User change the procurement category DropDown  based on  get the Procurement category object and set.
	 * 
	 * @param event
	 *
	 */
	public void setProcurmentCategory(AjaxBehaviorEvent event) {

		Long id = (Long) event.getComponent().getAttributes().get("value");
		if (id != null) {
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(id);
			ParameterResDTO parameterResDTO = getParameterSO().retrieveParameterById(parameterReqDTO);
			this.getManagedBean().getQt().setPmProcurementCategory(parameterResDTO.getPmParameter());
		}
		// #35285 & #25635 - Start
		else {
			this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());
		}

		if (null != this.getManagedBean().getQt().getPmProcurementCategory()) {
			if (!ParameterConst.PROCUREMENT_CATEGORY_ICT.equalsIgnoreCase(this.getManagedBean().getQt()
					.getPmProcurementCategory().getParameterCode())) {
				this.getManagedBean().getQt().setCloneEnabled(Boolean.FALSE);
			}
		} else {
			this.getManagedBean().getQt().setCloneEnabled(Boolean.FALSE);
		}
		// #35285 & #25635 - End
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Gets the PublishingPeriod tooltip.
	 * 
	 * @return the PublishingPeriod tooltip.
	 */
	public String getPublishingPeriodDaysToolTip() {
		StringBuilder tooltip = new StringBuilder();
		ScQt scQt = getManagedBean().getQt();

		if (scQt != null) {
			Date publishDate = null;
			if (scQt.getCurrentWorkFlowStatus() != null && super.isQTStatusPublished(scQt.getCurrentWorkFlowStatus().getPmStatus().getStatusId())) {
				publishDate = scQt.getPublishDate();
			}
			List<PmConfigRuleValue> ruleValueList = getPublishingPeriodDaysRuleValues(scQt.getQtType(), scQt.isPanel(), publishDate);
			Locale locale = new FacesScopes().getUserLocale();
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat dformat = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

			for (PmConfigRuleValue configRuleValue : ruleValueList) {
				if (configRuleValue.getFromValue() != null) {
					tooltip.append(dformat.format(configRuleValue.getFromValue()));
				}

				if (configRuleValue.getToValue() != null) {
					tooltip.append(" - ");
					tooltip.append(dformat.format(configRuleValue.getToValue()));
				} else {
					tooltip.append(" ");
					tooltip.append(ResourceBundleUtils.getMessage("lbl.sc.qty.and.above", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
				}

				tooltip.append(" : ");
				tooltip.append(configRuleValue.getRuleValue());
				tooltip.append(" ");
				tooltip.append(ResourceBundleUtils.getMessage("lbl.sc.days", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
				tooltip.append("<br/>");
			}
		}

		return tooltip.toString();
	}

	private List<PmConfigRuleValue> getPublishingPeriodDaysRuleValues(String qtType, boolean isPanel, Date publishDate) {
		IRuleSO ruleSO = null;

		try {
			ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(isPanel ? RuleTypeConst.PANEL_PUBLISHING_PERIOD_DAYS : RuleTypeConst.QT_PUBLISHING_PERIOD_DAYS);
			reqDTO.setValidityDate(publishDate);

			List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(reqDTO).getRuleValueList();
			if (ruleValueList != null && ruleValueList.size() > 0) {
				return ruleValueList;
			} else if (publishDate != null) {
				// if not found, always get the latest rule
				log.info(getManagedBean().getQt().getQtNo() + " " + reqDTO.getRuleType() + " rule not found for " + publishDate + ".");
				return getPublishingPeriodDaysRuleValues(qtType, isPanel, null);
			} else {
				throw new EpException(MessageConst.ERROR_770222, new String[] { getRuleTypeDesc(reqDTO.getRuleType()) });
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

	/**
	 * based on treenode selection button enable/disable.
	 * 
	 * @param event
	 *            the event
	 */
	public boolean zoneLocationDeleteBtnDisable() {
		List<TreeNode> list = getManagedBean().getZoneGeneralTrees();

		if (CollectionUtils.isNotEmpty(list)) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {

						for (TreeNode state : childNode.getChildren()) {

							if (state.isSelected()) {
								return false;
							}
							for (TreeNode district : state.getChildren()) {

								if (district.getChildCount() > 0) {
									for (TreeNode division : district.getChildren()) {
										if (division.isSelected()) {
											return false;
										}
										if (division.getChildCount() > 0) {
											for (TreeNode anotherDistrict : district.getChildren()) {
												if (anotherDistrict.isSelected()) {
													return false;
												}
											}
										}
									}
								} else {
									if (district.isSelected()) {
										return false;
									}
								}
							}
						}
					} else {
						if (childNode.isSelected()) {
							return false;
						}
					}
				}
			}
		} else {
			return true;
		}
		return true;
	}

	/**
	 * On change of drop down
	 * 
	 * @param approvalDetails
	 */
	public void otherApprovalStatus(ScQtApprovalRequest approvalDetails) {

		Long parameterId = (approvalDetails != null && approvalDetails.getApproverAction() != null) ? approvalDetails.getApproverAction().getParameterId() : null;
		if (parameterId != null) {
			approvalDetails.setApproverAction(retrieveParameterById(parameterId));

			// bug #30435, Enhancement #25077 - start
			getManagedBean().setPmParam(retrieveParameterById(parameterId));
			// #30435, #25077 -end-
		} else {
			approvalDetails.setApproverAction(new PmParameter());
		}

	}

	// #30435,#34694,#25077 -start-
	public void otherAppValueChangedICT(ValueChangeEvent event) {

		if (null != event.getNewValue()) {
			getManagedBean().setScQtAppRequestRemarkICT(event.getNewValue().toString());
		}
	}
	// bug #30435,#34694,Enhancement #25077 - end

	// #34694, #25077 -start-
	public void otherAppValueChangedMITI(ValueChangeEvent event) {

		if (null != event.getNewValue()) {
			getManagedBean().setScQtAppRequestRemarkMITI(event.getNewValue().toString());
		}
	}
	// #34694, #25077 -end-

	/**
	 * To remove the non bumi suppliers as open for Bumi for Restricted tender
	 * 
	 * @param scQt
	 */
	public void clearNonBumiSuppliers(ScQt scQt) {

		List<ScQtSupplier> supplierList = this.getManagedBean().getQtSupplierList();
		if (CollectionUtils.isNotEmpty(supplierList)) {
			int size = supplierList.size();
			List<ScQtSupplier> removeSupplierList = new ArrayList<ScQtSupplier>(size);
			for (ScQtSupplier supplier : supplierList) {
				if (!supplier.isBumi()) {
					removeSupplierList.add(supplier);
				}
			}
			if (CollectionUtils.isNotEmpty(removeSupplierList)) {
				this.getManagedBean().setToBeRemoveRestrictedSupplierList(removeSupplierList);
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

	/**
	 * Set PmPtjSpecialApproval based on selection
	 * 
	 * @param scQt
	 */
	public void ptjSpecialApprovalConformation(ScQt scQt) {

		Boolean isSpecialApproval = scQt.getObtainSpecialApproval();
		if (isSpecialApproval) {
			PmPtjSpecialApproval ptjSpecialApproval = this.getManagedBean().getPtjSpecialApproval();

			if (ptjSpecialApproval == null) {
				ptjSpecialApproval = getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
				this.getManagedBean().setPtjSpecialApproval(ptjSpecialApproval);
			}
			scQt.setPtjSpecialApproval(ptjSpecialApproval);
		} else {
			scQt.setPtjSpecialApproval(null);

		}
		this.getManagedBean().setProcMethodDropdownPresent(false);

		BigDecimal amount = scQt.getEstimatedAmount() != null ? scQt.getEstimatedAmount() : scQt.getTmpExpectedIntendedPrice();
		if (amount != null) {
			setProcurementMode(scQt, amount);
		}
	}

	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> excludedList = new ArrayList<String>();
		excludedList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		excludedList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(), excludedList));
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);

		return items;
	}

	/**
	 * Procurement Mode dropdown change action
	 */
	public void procurementModeChange(AjaxBehaviorEvent event) {

		IParameterSO parameterSO = null;
		Long parameterId = (Long) event.getComponent().getAttributes().get("value");

		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			this.getManagedBean().getQt().setProcurementMode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Reset the PmPtjSpecialApproval based on org profile id.
	 * 
	 */
	public void resetPTJSpecApproval() {

		ScQt scQt = this.getManagedBean().getQt();
		if (scQt.getPmOrgProfile() != null) {

			Long orgProfileId = scQt.getPmOrgProfile().getOrgProfileId();

			// Enhancement #21877
			scQt.setOrgProfileId(orgProfileId);

			PmPtjSpecialApproval ptjSpecialApproval = getPtjSpecialApprovalByOrgProfileId(orgProfileId);
			this.getManagedBean().setPtjSpecialApproval(ptjSpecialApproval);
			scQt.setPtjSpecialApproval(null);
			if (ptjSpecialApproval != null) {
				scQt.setObtainSpecialApproval(false);
			} else {
				scQt.setObtainSpecialApproval(null);
				if (this.getManagedBean().getPanel() == null) {
					this.getManagedBean().setPanel(true);
					scQt.setPanel(true);
				}
			}
		}

	}

	public boolean isPanelZone() {
		return this.getManagedBean().isPanelZone();
	}

	// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
	public boolean zoneLocationOkBtnDisable() {
		List<TreeNode> list = getManagedBean().getZoneGeneralTreesPanel();

		if (CollectionUtils.isNotEmpty(list)) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {

						for (TreeNode state : childNode.getChildren()) {

							if (state.isSelected()) {
								return false;
							}
							for (TreeNode district : state.getChildren()) {

								if (district.getChildCount() > 0) {
									for (TreeNode division : district.getChildren()) {
										if (division.isSelected()) {
											return false;
										}
										if (division.getChildCount() > 0) {
											for (TreeNode anotherDistrict : district.getChildren()) {
												if (anotherDistrict.isSelected()) {
													return false;
												}
											}
										}
									}
								} else {
									if (district.isSelected()) {
										return false;
									}
								}
							}
						}
					} else {
						if (childNode.isSelected()) {
							return false;
						}
					}
				}
			}
		} else {
			return true;
		}
		return true;
	}

	// 20200210@asyrafhaziq_Bug #27381&#26694 - End

	// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
	public boolean zoneLocationAddBtnDisable() {
		List<TreeNode> list = getManagedBean().getZoneGeneralTreesPanel();

		if (CollectionUtils.isEmpty(list) || list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	// 20200210@asyrafhaziq_Bug #27381&#26694 - End
	
	// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
	public void checkAllZoneGeneralPanel(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<TreeNode> list = getManagedBean().getZoneGeneralTreesPanel();

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

	// 20200210@asyrafhaziq_Bug #27381&#26694 - End

	// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
	public void addTreeNode() {
		getManagedBean().setSelectedFlag(true);
		List<TreeNode> rootTreesPanel = getManagedBean().getZoneGeneralTreesPanel();
		List<TreeNode> rootTrees = getManagedBean().getZoneGeneralTrees();

		if (null == rootTrees) {
			getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		}
		if (rootTreesPanel != null) {
			if (null != this.getManagedBean().getQt().getScQtSupplierCriteria() && null != this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias().setModified(true);
			}
			if (null != rootTreesPanel) {
				for (TreeNode node : rootTreesPanel) {
					boolean isInsert = false;
					for (TreeNode childNode : node.getChildren()) {
						if (childNode.getChildCount() > 0) {
							for (TreeNode node2 : childNode.getChildren()) {
								if (node2.isSelected()) {
									isInsert = true;
									break;
								}
							}
							if (isInsert == true) {
								break;
							}
						}
						if (childNode.isSelected()) {
							isInsert = true;
							break;
						}
					}
					if (isInsert == true) {
						if (getManagedBean().getIndex() == -1) {
							rootTrees.add(node);
						} else if (getManagedBean().getIndex() >= 0) {
							rootTrees.remove(getManagedBean().getIndex());
							rootTrees.add(getManagedBean().getIndex(), node);
						}
					}
				}
			}
			// set back tree node selected to false
			if (null != rootTrees) {
				for (TreeNode node : rootTrees) {
					for (TreeNode childNode : node.getChildren()) {
						if (childNode.getChildCount() > 0) {
							for (TreeNode node2 : childNode.getChildren()) {
								node2.setSelected(false);
							}
						}
						childNode.setSelected(false);
					}
				}
			}
		}

		TplSpecZoneDtl zoneDtl = null;

		if (!rootTrees.isEmpty()) {
			ScQtZoneCriteria zonCriteria = getManagedBean().getQt().getScQtSupplierCriteria().getQtZoneCriterias();
			zonCriteria.setTplSpecZoneDtls(convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
			// setting into zones bean to validate zones while adding
			if (CollectionUtils.isNotEmpty(zonCriteria.getTplSpecZoneDtls())) {
				List<ZonesBean> zonBeansList = new ArrayList<ZonesBean>();
				for (TplSpecZoneDtl tplSpecZoneDtl : zonCriteria.getTplSpecZoneDtls()) {
					ZonesBean bean = new ZonesBean();
					bean.setTplSpecZoneDtl(tplSpecZoneDtl);
					zonBeansList.add(bean);
				}
				this.getManagedBean().setBeans(zonBeansList);
			}
		}

		// set ZoneGeneral Trees Panel
		ScQt scQt = getManagedBean().getQt();
		if (scQt.isZonal() && scQt.isPanel()) {
			List<TplSpecZoneDtl> specZoneDtls = null;
			if (!rootTrees.isEmpty()) {
				specZoneDtls = convertTreeNodesToZoneCriteria(rootTrees, zoneDtl);
			} else {
				specZoneDtls = new ArrayList<TplSpecZoneDtl>();
			}
			retrieveZoneGeneralTreesPanel(scQt, specZoneDtls);
		}
		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setAutoUpdate(true);
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria && zoneSupplierCriteria.isBrandNew() == false && null != zoneSupplierCriteria.getQtZoneCriterias()
				&& zoneSupplierCriteria.getQtZoneCriterias().isBrandNew() == false) {
			if (null != this.getManagedBean().getLoginRoleType() && !"".equals(this.getManagedBean().getLoginRoleType())
					&& "SC".equalsIgnoreCase(this.getManagedBean().getLoginRoleType())) {
				updateFinacialSpecStatus();
				this.getManagedBean().setDeliveryLocRemoveList(null);
				this.getManagedBean().setDeliveryLocBean(null);
				deleteDeliveryAdreesZoneDetails(this.getManagedBean().getQt());
			}

		}
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");
	}

	// 20200210@asyrafhaziq_Bug #27381&#26694 - End

	// 20200210@asyrafhaziq_Bug #27381&#26694 - Start
	public void retrieveZoneGeneralTreesPanel(ScQt scQt, List<TplSpecZoneDtl> specZoneDtls) {
		log.info("retrieveZoneGeneralTreesPanel");

		IPanelSupplierSO panelSupplierSO;
		ICircularDetailSO circularDetailSO;

		List<CtPanelZone> ctPanelZones = new ArrayList<CtPanelZone>();
		List<TreeNode> zoneGeneralTreesPanel = getManagedBean().getZoneGeneralTreesPanel();

		if (null != getManagedBean().getCtPanelZones() && !getManagedBean().getCtPanelZones().isEmpty()) {
			ctPanelZones = getManagedBean().getCtPanelZones();
		} else {
			try {
				CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
				circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);

				reqDTO.setCircularVerId(scQt.getCircularVer().getCircularVerId());
				reqDTO.setPanelItemTypeGroup(true);
				if (null != this.getManagedBean().getQt().getItemGroup()) {
					reqDTO.setGroupItemId(scQt.getItemGroup().getPanelItemGroupId());
				}

				CircularDetailResDTO resDTO = circularDetailSO.getPanelItemsByCircularVerId(reqDTO);
				this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());

				PanelSupplierReqDTO reqPanelDTO = new PanelSupplierReqDTO();
				PanelSupplierResDTO resPanelDTO = new PanelSupplierResDTO();
				panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
				for (CtPanelItem panelItem : this.getManagedBean().getPanelItemList()) {

					reqPanelDTO.setPanelItem(panelItem);
					resPanelDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqPanelDTO);
					if (panelItem.getLocationLevelId() != null && panelItem.getLocationLevelId().getParameterId() != null)
						scQt.setZoneLocationLevel(getPmParameter(panelItem.getLocationLevelId().getParameterId()));
					break;
				}

				if (null != resDTO && null != resPanelDTO.getCtPanelZones() && !resPanelDTO.getCtPanelZones().isEmpty()) {
					ctPanelZones = resPanelDTO.getCtPanelZones();
					getManagedBean().setCtPanelZones(ctPanelZones);
				}
			} finally {
				panelSupplierSO = null;
				circularDetailSO = null;
			}
		}
		/* set tree zonal information for panel */
		if (isNotNullOREmpty(ctPanelZones)) {
			if (zoneGeneralTreesPanel == null) {
				zoneGeneralTreesPanel = new ArrayList<TreeNode>();
				getManagedBean().setZoneGeneralTreesPanel(zoneGeneralTreesPanel);
			} else {
				zoneGeneralTreesPanel.clear();
			}
			for (CtPanelZone panelZones : ctPanelZones) {
				boolean setData = true;
				if (isNotNullOREmpty(specZoneDtls)) {
					for (TplSpecZoneDtl zoneDtl : specZoneDtls) {
						if (zoneDtl.getZoneName().equalsIgnoreCase(panelZones.getName())) {
							setData = false;
							break;
						}
					}
				}
				if (setData == true) {
					TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
					tplSpecZoneDtl.setZoneName(panelZones.getName());
					tplSpecZoneDtl.setPanelZone(panelZones);

					List<PmState> states = SourcingLocationHelper.getStateFromCtPanelZone(panelZones.getPanelZoneDtls());
					List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromCtPanelZone(panelZones.getPanelZoneDtls());

					zoneGeneralTreesPanel.add(LocationSingleton.getInstance().buildTreeWithDisabledChilds(tplSpecZoneDtl, states, districts, null, null, false,
							TplSpecZoneLoc.class));
				}
			}
		}

	}
	//20200210@asyrafhaziq_Bug #27381&#26694  - End

	//#21692 - Start
	/**
	 * Check if current QT Status is in between QT Publication and QT Closed/Awarded
	 * 
	 * @param currentStatusId
	 * @return
	 */
	public boolean isQTStatusInProposalValidPeriodExtApproval(Long currentStatusId) {
		List<Long> validPeriodExts = Arrays.asList(QTStatusConst.QT_PENDING_PUBLICATION, QTStatusConst.QT_BRIEFING_SITE_VISIT, QTStatusConst.QT_PROPOSAL_PREPARATION,
				QTStatusConst.QT_EVALUATION_PROPOSAL, QTStatusConst.QT_SUPPLIER_FINALIZATION, QTStatusConst.QT_PENDING_LOI, QTStatusConst.QT_PENDING_LOA,
				QTStatusConst.QT_AWARDED, QTStatusConst.QT_CLOSED, QTStatusConst.QT_CONSOLIDATION_REPORT);

		for (int i = 0; i < validPeriodExts.size(); i++) {
			if (validPeriodExts.get(i).longValue() == currentStatusId)
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	//#21692 - End

	/* :::Enhancement #25638 #29 - start::: */
	public boolean isStatusInQTReviseGroupStatus(Long currentStatusId) {
		List<Long> allowReviseStatus = Arrays.asList(QTStatusConst.QT_REVISE_GROUP_STATUS);

		if ( // #25638 - #31322 - Start.
		null != this.getManagedBean().getOutComes() && this.getManagedBean().getOutComes().length > 0
				&& ArrayUtils.contains(this.getManagedBean().getOutComes(), BpmOutcomeConst.BPM_REVISE) &&
				// #25638 - #31322 - End.
				allowReviseStatus.contains(currentStatusId)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	/* :::Enhancement #25638 #29 - end::: */

	// 9019 Start - (Harun) #Note-501
	public void updateRemarks(ScQt scQt) {
		this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());

	}
	// 9019 End
	
	// Bug #36730@Syazani 07/01/2025 -- Start
	private void reinstateZoneLocationLevel(Long eventLocationLevelId) {
		PmParameter zoneLocationLevel = new PmParameter();
		ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
		Long sourceLocationLevelId = null != supplierCriteria && null != supplierCriteria.getLocationLevel()
				&& null != supplierCriteria.getLocationLevel().getParameterId() ? supplierCriteria.getLocationLevel()
				.getParameterId() : 0L;
		zoneLocationLevel.setParameterId(eventLocationLevelId);
		this.getManagedBean().getQt().setZoneLocationLevel(zoneLocationLevel);
		if (!sourceLocationLevelId.equals(eventLocationLevelId) && 0l != sourceLocationLevelId
				&& null != supplierCriteria) {
			supplierCriteria.setLocationLevel(zoneLocationLevel);
		}
	}
	// Bug #36730@Syazani 07/01/2025 -- End
	
	/** #11104  START */	
	/**
	 * sample change.change sample(yes/no) radion button in 
	 * owner.
	 * 
	 * @param event
	 *            (String Samplechange)
	 */
	public void sampleChange(AjaxBehaviorEvent event) {
		Boolean type = (Boolean) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getQt().setSampleReq(type);
		List<ScQtCheckList> st2technicalproposalList = getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null == st2technicalproposalList) {
			st2technicalproposalList = new ArrayList<>();

		}
		if (type) {
			this.getManagedBean().getQt().setPartial(Boolean.FALSE);
		} else {
			this.getManagedBean().getQt().setDemoNotificationCheck(null);
			List<ScQtCheckList> deltechnicalproposalList = getManagedBean()
					.getMarkDeletedTechChecklist();
			if (deltechnicalproposalList == null) {
				deltechnicalproposalList = new ArrayList<>();
			}
			if (null == st2technicalproposalList
					|| CollectionUtils.isEmpty(st2technicalproposalList)) {
				SpecificationBackingBean spBB = new SpecificationBackingBean();
				spBB.retriveTechnicalCheckList();
				st2technicalproposalList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
			}
			if (CollectionUtils.isNotEmpty(st2technicalproposalList)) {
				for (ScQtCheckList checklist : st2technicalproposalList) {
					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
							.equalsIgnoreCase(checklist.getDefinationType()
									.getParameterCode())) {
						deltechnicalproposalList.add(checklist);
						getManagedBean().setMarkDeletedTechChecklist(
								deltechnicalproposalList);
						st2technicalproposalList.remove(checklist);
						break;
					}
				}
			}
		}
	}
	// #11104 -end-

	/***11104 -start-
	 * check portlet is specpreparation or not
	 */
	public boolean isSpecScreen() {
		return QTConstants.PORTLET_NAME_QT_SPEC_COMMITTEE_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #11104 -end-

	/***
	 * 11104 -start-
	 * check portlet is specmaintennace or not
	 */
	public boolean isSpecEndorsmentScreen() {
		return QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_TASKLIT
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #11104-end

	// #21695 Farid START 3/7/2021
	public boolean hasroleSCSecretary() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value());
	}
	// #21695 Farid END 3/7/2021

	// #28784 -start-
	/**
	 * Render Demonstration Radio button if Evaluation Method 2 Tier Selected
	 * #28784 - Include Demo WIP
	 * @param event
	 *
	 */
	public void renderDemoRadioButton(AjaxBehaviorEvent event) {
		Long status = this.getManagedBean().getQt().getQtStatus().getStatusId();
		RequestContext.getCurrentInstance().execute("blockWidget.show()");

		if (!status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION) && this.getManagedBean().isNewVersion()){
			String type = (String) event.getComponent().getAttributes().get("value");
			boolean demoRequired = this.getManagedBean().getQt().isDemoRequired();

			// 38426 - start
			if (this.isStatusLessThanOrEquals(status,
					QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH,
					QTStatusConst.QT_ORDERED_STATUSES)) {
				// note937 Issue 2 - Start
				boolean oneTier = type.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER) && demoRequired;

				if (oneTier) {
					RequestContext.getCurrentInstance().execute("demoEvalDialogConfirm.show()");
				}
				// note937 Issue 2 - End
			}
			// 38426 - end

			if (type.equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)
					&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
				this.getManagedBean().setDemoType(true);
			} else {
				this.getManagedBean().setDemoType(false);
				this.getManagedBean().getQt().setDemoRequired(false);
			}

			if (null != status
					&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) && !this.getManagedBean().isQtSpecMaintanance() && !this
							.getManagedBean().isMaintanceFlow()) && !demoRequired) {
				this.getManagedBean().setDemoSpecialCase(true);
				this.getManagedBean().setDemoEvalDisabled(true);
			}

		} else if (!this.getManagedBean().isNewVersion()) {
			this.getManagedBean().setDemoType(false);
		}

		RequestContext.getCurrentInstance().execute("blockWidget.hide()");
	}
	// #28784 -end-

	// #28784 -start-
	/**
	 * #28784 - check demo radio button and display popup to delete
	 * @param event
	 */
	public void validateDemoTechList(AjaxBehaviorEvent event) {
		Boolean type = (Boolean) event.getComponent().getAttributes().get("value");
		this.getManagedBean().getQt().setDemoRequired(type);

		if (type == false) {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			List<ScQtCheckList> scQtTechChecklist = this.getManagedBean().getScQtCheckSt2techProposal();

			if (null == scQtTechChecklist) {
				IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				scQtTechChecklist = specSo.retrieveCheckLists(reqDTO).getTechnicalCheckList();
			}

			RequestContext.getCurrentInstance().execute("demoDialogConfirm.show()");//note937 Issue 1
		} else {
			RequestContext.getCurrentInstance().execute("demoDialogConfirm.hide()");
		}

		QTDetailBackingBean detailBB = new QTDetailBackingBean();
		detailBB.checkDemoPresentationByStatus();
	}
	// #28784 -end-

	// #28784 -start-
	/*
	 * #28784 - Delete Demo tech checklist if user change yes to no
	 */
	public void yesDeleteExistingDemoTechChecklist() {
		SpecificationResDTO resDTO = null;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		SpecificationBackingBean specificationBackingBean = new SpecificationBackingBean();// #28784
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);

		try {
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

			scQt.setScQtStatus(null);
			scQt.setScQtBriefings(null);
			scQt.setScQtCommittees(null);
			scQt.setScQtChecklists(null);

			specReqDTO.setScQt(scQt);

			resDTO = specSO.saveQTDtl(specReqDTO);

			this.getManagedBean().setQt(resDTO.getScQt());

			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);

			specificationBackingBean.getDemoScoreSetting(reqDTO);

			resDTO = specSo.deleteDemoSpecChecklist(reqDTO);

			if (resDTO.isSuccessful()) {
				specificationBackingBean.resetDemoTechSpecListAfterDelete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// #28784 -end-

	// #28784 -start-
	public void noDeleteExistingDemoTechChecklist() {
		this.getManagedBean().getQt().setEvaluationType(ParameterConst.EVALUATION_TYPES_TWO_TIER);
		this.getManagedBean().setDemoType(true);
		this.getManagedBean().getQt().setDemoRequired(true);
		this.getManagedBean().setDemoEvalDisabled(false); // disable evaluation method during sc maintenance
	}
	// #28784 -end-
}

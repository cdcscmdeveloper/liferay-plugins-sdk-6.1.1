package my.ep.web.backingbean.report.ct;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.report.ct.cc.ContractItemMonitoringCCRptBackingBean;
import my.ep.web.backingbean.report.ct.cc.ContractManagementReportBackingBean;
import my.ep.web.backingbean.report.ct.cc.ListOfCircularRptBackingBean;
import my.ep.web.backingbean.report.ct.cc.StatementOfOrderRptBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class CtReportBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "ctReportBB")
@ViewScoped
public class CtReportBackingBean extends CtReportBaseBackingBean<CtReportManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/reports/ct/ctReportMain.xhtml";
	
	public CtReportBackingBean() {
		// set default date value
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
	}
	
	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		log.debug("preRenderView");
		this.renewSesionObject();
		
		retrieveReportCategoryListByModuleCode(
				ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_CT_REPORT_MAIN);
	}
	
	/**
	 * The method used to retrieve the report list base on report category.
	 * 
	 * @param reportCategory  the selected reportCategory
	 */
 	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase(),
				reportCategory);
		
		getManagedBean().setReportCode(null);
		clearAllSearchCriteria();
	}
	
	/**
	 * The method used to retrieve the report value from database.
	 */
	public void onChangeReportName() {
		clearAllSearchCriteria();
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		if (!getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_LIST_OF_CIRCULAR_PQ_RPT_VAL)
				&& !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_STATEMENT_OF_ORDER_PQ_RPT_VAL)) {
			setCurrentActiveOrg();
		}
	}
	
	private void clearAllSearchCriteria() {
		StatementOfOrderRptBackingBean ccStatementOfOrderRptBB = new StatementOfOrderRptBackingBean();
		ListOfCircularRptBackingBean ccListOfCircularRptBB = new ListOfCircularRptBackingBean();		
		my.ep.web.backingbean.report.ct.panel.StatementOfOrderRptBackingBean panelStatementOfOrderRptBB = 
				new my.ep.web.backingbean.report.ct.panel.StatementOfOrderRptBackingBean();
		my.ep.web.backingbean.report.ct.panel.ListOfCircularRptBackingBean panelListOfCircularRptBB = 
				new my.ep.web.backingbean.report.ct.panel.ListOfCircularRptBackingBean();
		//#29416 - baking bean for contract item monitoring report
		ContractItemMonitoringCCRptBackingBean ccContractItemMonitoringRptBB = new ContractItemMonitoringCCRptBackingBean();
		//#30918 - start
		ContractManagementReportBackingBean ctManagementReportBB = new ContractManagementReportBackingBean();
		//#30918 - end
		
		// reset common search criteria
		resetSearch();
		// reset ccStatementOfOrderRptBB search criteria
		ccStatementOfOrderRptBB.resetSearch();
		// reset ccListOfCircularRptBB search criteria
		ccListOfCircularRptBB.resetSearch();
		// reset panelStatementOfOrderRptBB search criteria
		panelStatementOfOrderRptBB.resetSearch();
		// reset panelListOfCircularRptBB search criteria
		panelListOfCircularRptBB.resetSearch();
		//#29416 start
		ccContractItemMonitoringRptBB.resetSearch();
		//29416 end
		//#30918 -start
		ctManagementReportBB.resetSearch();
		//#30918 - end
	}
	
	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.debug("setCurrentActiveOrg");
		
		
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		if (!getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			List<PmUserOrg> userOrgList = PmCommonUtil
					.retrieveActiveOrgWithoutRole(getFacesScopes()
							.getUserSessionBean().getUserId(), false);
	
			if (userOrgList == null || userOrgList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_770105);
			}
	
			PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
	
			if (!getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL) 
					|| !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL)){
				
				if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
						|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
					getManagedBean().setDisableOrgField(true);
					getManagedBean().setDisableMinistryField(true);
					getManagedBean().setSelectedMinistry(
							retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedKumpulanPTJ(
							retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedPTJ(pmOrgProfile);
					
					//Bug #29974 | Notes 39 - start
					//CT021
					if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_STATEMENT_OF_ORDER_CC_RPT_VAL)){
						StatementOfOrderRptBackingBean ccStatementOfOrderRptBB = new StatementOfOrderRptBackingBean();
						ccStatementOfOrderRptBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
						ccStatementOfOrderRptBB.getManagedBean().setSelectedKumpulanPTJ(getManagedBean().getSelectedKumpulanPTJ());
						ccStatementOfOrderRptBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
						ccStatementOfOrderRptBB.getManagedBean().setDisableMinistryField(true);
						ccStatementOfOrderRptBB.getManagedBean().setDisableOrgField(true);}
					//CT016
					if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_LIST_OF_CIRCULAR_CQ_RPT_VAL)){
						ListOfCircularRptBackingBean ccListOfCircularRptBB = new ListOfCircularRptBackingBean();
						ccListOfCircularRptBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
						ccListOfCircularRptBB.getManagedBean().setSelectedKumpulanPTJ(getManagedBean().getSelectedKumpulanPTJ());
						ccListOfCircularRptBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
						ccListOfCircularRptBB.getManagedBean().setDisableMinistryField(true);
						ccListOfCircularRptBB.getManagedBean().setDisableOrgField(true);}
					//Bug #29974 | Notes 39 - end
		
				} else if(getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
					getManagedBean().setSelectedMinistry(
							pmOrgProfile);
					getManagedBean().setDisableMinistryField(true);
				}
			}
			
			//29416 start
			if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL) 
					|| getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL)){
				
				ContractItemMonitoringCCRptBackingBean ccContractItemMonitoringRptBB = new ContractItemMonitoringCCRptBackingBean();
				
				if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
					getManagedBean().setSelectedMinistry(
							retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedKumpulanPTJ(
							retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedPTJ(pmOrgProfile);
					
				
					//#Assign val to ContractItemMonitoringCCRptBakingBean
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedKumpulanPTJ(getManagedBean().getSelectedKumpulanPTJ());
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
					//cc
					ccContractItemMonitoringRptBB.getManagedBean().setDisableMinistryField(true);
					ccContractItemMonitoringRptBB.getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())? true:false);
					//mc
					getManagedBean().setDisableMinistryField(true);
					getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())? true:false);
				}
				
				else if (getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())){
					//cc
					ccContractItemMonitoringRptBB.getManagedBean().setDisableMinistryField(false);
					//mc
					getManagedBean().setDisableMinistryField(false);
				}
			}//29416 - end 
		}
		
		//#30918 - start
	
		if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_MANAGEMENT_RPT_MC_VAL)) {

			ContractManagementReportBackingBean ctManagementReportBB = new ContractManagementReportBackingBean();

			if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				// disable supplier textfield for role supplier admin and supplier user.
				ctManagementReportBB.getManagedBean().setSupplierVisible(false);

				SmSupplier currSupp = null;
				currSupp = SmApplicationUtil.getCurrentSupplier();
				ctManagementReportBB.getManagedBean().setSupplierID(currSupp.getSupplierId());

				ctManagementReportBB.getManagedBean().setDisableMinistryField(false);

			} else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
				

					ctManagementReportBB.getManagedBean().setSupplierID(null);
					ctManagementReportBB.getManagedBean().setSelectedSupplier(null);

					List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrgWithoutRole(getFacesScopes()
							.getUserSessionBean().getUserId(), false);

					if (userOrgList == null || userOrgList.isEmpty()) {
						throw new EpException(MessageConst.ERROR_770105);
					}

					PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();

					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
					getManagedBean().setSelectedPTJ(pmOrgProfile);

					// #Assign val to ContractItemMonitoringCCRptBakingBean
					ctManagementReportBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
					ctManagementReportBB.getManagedBean().setSelectedKumpulanPTJ(
							getManagedBean().getSelectedKumpulanPTJ());
					ctManagementReportBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
					// cc
					ctManagementReportBB.getManagedBean().setDisableMinistryField(true);
					ctManagementReportBB.getManagedBean().setDisableOrgField(
							getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()) ? true : false);

				}
			}
			else if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_MANAGEMENT_RPT_CC_VAL)) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
					|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {

				ContractManagementReportBackingBean ctManagementReportBB = new ContractManagementReportBackingBean();

				if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
						|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
					// disable supplier textfield for role supplier admin and
					// supplier user.
					ctManagementReportBB.getManagedBean().setSupplierVisible(false);
					// getManagedBean().setSelectedSupplier()

					SmSupplier currSupp = null;
					currSupp = SmApplicationUtil.getCurrentSupplier();
					ctManagementReportBB.getManagedBean().setSupplierID(currSupp.getSupplierId());
				} else {
					ctManagementReportBB.getManagedBean().setSupplierVisible(true);
				}
				// cc
				ctManagementReportBB.getManagedBean().setDisableMinistryField(false);

			}
		}
		// #30918 - end
	}
	
	/**
	 * The method used to reset the search criteria for report.
	 */
	@Override
	public void resetSearch() {
		log.debug("resetSearch: ctReportBB");		
		getManagedBean().setPopUpMsg(null);
		getManagedBean().setReportUrl(null);
		getManagedBean().setDisableMinistryField(false);
		getManagedBean().setDisableOrgField(false);
		getManagedBean().setSelectedMinistry(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
		getManagedBean().setContractNo(null);
		getManagedBean().setContractPhysicalNo(null);
		getManagedBean().setLoaNo(null);
		getManagedBean().setLoaPhysicalNo(null);
		getManagedBean().setAgreement(null);
		getManagedBean().setProcurementMode(null);
		getManagedBean().setStatus(null);
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());	
		//#30418 - start
		//#30918 - end
	}

	/**
	 * The method used to construct the parameter.
	 */
	@Override
	public LinkedHashMap<String, String> getparams() {
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		if (getManagedBean().getSelectedMinistry() != null) {
			map.put(CtReportConst.PARAM_KEY_MINISTRY, getManagedBean()
					.getSelectedMinistry().getOrgProfileId().toString());
		}
		if (getManagedBean().getSelectedKumpulanPTJ() != null) {
			map.put(CtReportConst.PARAM_KEY_KUMPULAN_PTJ, getManagedBean()
					.getSelectedKumpulanPTJ().getOrgProfileId().toString());
		}		
		if (getManagedBean().getSelectedPTJ() != null) {
			map.put(CtReportConst.PARAM_KEY_PTJ, getManagedBean()
					.getSelectedPTJ().getOrgProfileId().toString());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getContractNo())) {
			map.put(CtReportConst.PARAM_KEY_CONTRACT_NO, getManagedBean()
					.getContractNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getContractPhysicalNo())) {
			map.put(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO, getManagedBean()
					.getContractPhysicalNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getLoaNo())) {
			map.put(CtReportConst.PARAM_KEY_LOA_NO, getManagedBean()
					.getLoaNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getLoaPhysicalNo())) {
			map.put(CtReportConst.PARAM_KEY_LOA_PHYSICAL_NO, getManagedBean()
					.getLoaPhysicalNo());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getAgreement())) {
			map.put(CtReportConst.PARAM_KEY_AGREEMENT, getManagedBean()
					.getAgreement());
		}
		if (getManagedBean().getProcurementMode() != null) {
			map.put(CtReportConst.PARAM_KEY_PROCUREMENT_MODE, getManagedBean()
					.getProcurementMode().toString());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getStatus())) {
			map.put(CtReportConst.PARAM_KEY_STATUS, getManagedBean()
					.getStatus());
		}
		if (getManagedBean().getFromDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getFromDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_FROM_DATE, df.format(
					getManagedBean().getFromDate()));
		}
		if (getManagedBean().getToDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getToDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_TO_DATE, df.format(
					getManagedBean().getToDate()));
		}
		
		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(CtReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			map.put(CtReportConst.PARAM_KEY_REPORT_ID, getManagedBean()
					.getReportCode());
			map.put(CtReportConst.PARAM_KEY_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
		return map;
	}
}

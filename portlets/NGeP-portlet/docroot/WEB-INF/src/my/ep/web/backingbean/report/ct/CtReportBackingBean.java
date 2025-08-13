package my.ep.web.backingbean.report.ct;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.rpt.IRPTRequestSO; 	//#32675 - shaqimi
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;//#32675 - shaqimi
import my.ep.dto.pm.common.report.RPTRequestReqDTO;//#32675 - shaqimi
import my.ep.dto.pm.common.report.RPTRequestResDTO;//#32675 - shaqimi
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
//import my.ep.persistence.entity.sm.SmPersonnel; //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;//#32675 - shaqimi
import my.ep.web.backingbean.report.ct.cc.ContractItemMonitoringCCRptBackingBean;
import my.ep.web.backingbean.report.ct.cc.ContractItemUsageMonitoringDtlsBackingBean; //#34889
import my.ep.web.backingbean.report.ct.cc.ContractManagementReportBackingBean;
import my.ep.web.backingbean.report.ct.cc.ListOfCircularRptBackingBean;
import my.ep.web.backingbean.report.ct.cc.StatementOfOrderRptBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.constant.report.ReportConst;//#32675 - shaqimi
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;//#32675 - shaqimi

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
		
		//Enhancement 32675 - shaqimi start
		if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
			this.getManagedBean().setFactoringUser(true);
		}
		//Enhancement 32675 - shaqimi start
		
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
	//Enhancement 32675 - shaqimi start
	public void onChangeFromToDate() {
		if(this.getManagedBean().getFromDate().after(this.getManagedBean().getToDate())){
			throw new EpException(MessageConst.ERROR_775186);
		}
	}
	//Enhancement 32675 - shaqimi end
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
		//Enhancement 32675 - shaqimi start
		if (!getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())
				&& !getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
		//Enhancement 32675 - shaqimi start
			List<PmUserOrg> userOrgList = PmCommonUtil
					.retrieveActiveOrgWithoutRole(getFacesScopes()
							.getUserSessionBean().getUserId(), false);
	
			if (userOrgList == null || userOrgList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_770105);
			}
	
			PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
	
			if (!getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL) 
					|| !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL)
						|| !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_DTLS_RPT_MC_VAL) //Fix - #34889
							|| !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_DTLS_RPT_CC_VAL)){ //Fix - #34889
				
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
			
			//29416 start changes by Nordin 26/6/2023
			if (getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL) 
					|| getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL)){
				
				ContractItemMonitoringCCRptBackingBean ccContractItemMonitoringRptBB = new ContractItemMonitoringCCRptBackingBean();
				
				if ((getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) && !getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
						|| getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
					
					if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
						getManagedBean().setSelectedMinistry(pmOrgProfile);
					} else {
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
					}
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
					if (!getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
						getManagedBean().setSelectedPTJ(pmOrgProfile);
					}
					
				
					//#Assign val to ContractItemMonitoringCCRptBakingBean
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedKumpulanPTJ(getManagedBean().getSelectedKumpulanPTJ());
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
					//cc
					ccContractItemMonitoringRptBB.getManagedBean().setDisableMinistryField(true);
					ccContractItemMonitoringRptBB.getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())); //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
					//mc
					getManagedBean().setDisableMinistryField(true);
					getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()));//Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
				}
				
				else if (isItemUsageReportRoles()){ //re-endorsed #29416
					
					log.info("called here");
					
					
					//cc
					ccContractItemMonitoringRptBB.getManagedBean().setDisableMinistryField(false);
					//mc
					getManagedBean().setDisableMinistryField(false);
					
					
					ccContractItemMonitoringRptBB.getManagedBean().setDisableOrgField(false);
					getManagedBean().setDisableOrgField(false);
					
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedMinistry(null);
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedKumpulanPTJ(null);
					ccContractItemMonitoringRptBB.getManagedBean().setSelectedPTJ(null);
					
					getManagedBean().setSelectedMinistry(null);
					getManagedBean().setSelectedKumpulanPTJ(null);
					getManagedBean().setSelectedPTJ(null);
					
				}
			}//29416 - end 
			
			//Start Fix -  #34889
			if(getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_DTLS_RPT_MC_VAL)
					|| getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_DTLS_RPT_CC_VAL)){
				
				ContractItemUsageMonitoringDtlsBackingBean ccContractItemUsageMonitoringDtlsReportBB = new ContractItemUsageMonitoringDtlsBackingBean();

				if ((getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) && !getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
						|| getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
					
					defaultValueRoles(pmOrgProfile);
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
					auditInternalRolesCheck(pmOrgProfile);
			
					//#Assign val to ContractItemMonitoringCentralReportDtlsBackingBean
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedMinistry(getManagedBean().getSelectedMinistry());
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedKumpulanPTJ(getManagedBean().getSelectedKumpulanPTJ());			
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedPTJ(getManagedBean().getSelectedPTJ());
					//cc
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setDisableMinistryField(true);
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())); //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
					//mc
					getManagedBean().setDisableMinistryField(true);
					getManagedBean().setDisableOrgField(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()));//Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
				} else if (isNoDefaultValues()) {
					// cc
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setDisableMinistryField(false);
					// mc
					getManagedBean().setDisableMinistryField(false);

					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setDisableOrgField(false);
					getManagedBean().setDisableOrgField(false);

					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedMinistry(null);
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedKumpulanPTJ(null);				
					ccContractItemUsageMonitoringDtlsReportBB.getManagedBean().setSelectedPTJ(null);

					getManagedBean().setSelectedMinistry(null);
					getManagedBean().setSelectedKumpulanPTJ(null);
					getManagedBean().setSelectedPTJ(null);
				}
			}
			//End Fix - #34889
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
							getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())); //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
					ctManagementReportBB.getManagedBean().setSupplierVisible(true);
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
	//Start - #34889
	/**
	 * @param pmOrgProfile
	 */
	private void auditInternalRolesCheck(PmOrgProfile pmOrgProfile) {
		if (!getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			getManagedBean().setSelectedPTJ(pmOrgProfile);
		}
	}
	
	/**
	 * @param pmOrgProfile
	 */
	private void defaultValueRoles(PmOrgProfile pmOrgProfile) {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			getManagedBean().setSelectedMinistry(pmOrgProfile);
		} else {
			getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
		}
	}
	//End - #34889
	/**
	 * The method used to reset the search criteria for report.
	 */
	@Override
	public void resetSearch() {
		log.info("resetSearch: ctReportBB :::::::::::::::::::::::::::::::::::::::::: ");
		//#29416 Nordin start
		if (!getManagedBean().isDisableMinistryField()) 
		{
			getManagedBean().setSelectedMinistry(null);
			getManagedBean().setDisableMinistryField(false);
			getManagedBean().setDisableOrgField(false);
			getManagedBean().setSelectedKumpulanPTJ(null);
			getManagedBean().setSelectedPTJ(null);
		}
		
		//#29416 Nordin end
		
		getManagedBean().setPopUpMsg(null);
		getManagedBean().setReportUrl(null);
		
		//#29416 Nordin start
		//getManagedBean().setDisableMinistryField(false);
		//getManagedBean().setDisableOrgField(false);
		//getManagedBean().setSelectedMinistry(null);
		//getManagedBean().setSelectedKumpulanPTJ(null);
		//getManagedBean().setSelectedPTJ(null);
		//#29416 Nordin end
		
		getManagedBean().setContractNo(null);
		getManagedBean().setContractPhysicalNo(null);
		getManagedBean().setLoaNo(null);
		getManagedBean().setLoaPhysicalNo(null);
		getManagedBean().setAgreement(null);
		getManagedBean().setProcurementMode(null);
		getManagedBean().setStatus(null);
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
		// Start fix- #29416
		if (getManagedBean().isLovItemReportNameTriggered()) {
			getManagedBean().setLovItemReportNameTriggered(false);
			RequestContext.getCurrentInstance().execute("resetAllData();");
		}		
		getManagedBean().setFormattedSelectedItems(null);
		getManagedBean().setLovSelectedItemList(null);
		getManagedBean().setSelectedSupplier(null);
		
		// End fix- #29416
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
		//Start - #29416
		if (getManagedBean().getSelectedSupplier() != null) {
			map.put(CtReportConst.PARAM_KEY_SUPPLIER_NAME, getManagedBean()
					.getSelectedSupplier().getOrgProfileId().toString());
		}
		if (getManagedBean().getContractItemId() != null) {
			map.put(CtReportConst.PARAM_KEY_ITEM_NAME, getManagedBean().getContractItemId());
		}
		//End - #29416
		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(CtReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			map.put(CtReportConst.PARAM_KEY_REPORT_ID, getManagedBean()
					.getReportCode());
			map.put(CtReportConst.PARAM_KEY_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());
			//Enhancement 32675 - shaqimi start
			if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
				map.put(CtReportConst.PARAM_KEY_USER_ROLE, RoleCode.FACTORING_USER.value());
			}
			//Enhancement 32675 - shaqimi end
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
		return map;
	}
	
	//#32675 - shaqimi start
	/**
	 * The method used to generate the Contract Report
	 * 
	 * @param params  the map of param key and value
	 */
	
	public void generateReportFactoring(LinkedHashMap<String, String> params) {
		log.debug("generateReport");
		
		if(this.getManagedBean().getFromDate().before(this.getManagedBean().getToDate())){
			IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IRPTRequestSO.class);
			params.put(ReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			params.put(ReportConst.PARAM_REPORT_ID, getManagedBean()
					.getReportCode());
			params.put(ReportConst.PARAM_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());

			RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
			reqDTO.setDefaultOutputType(this.getManagedBean()
					.getDefaultOutputType());
			reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
			reqDTO.setParametersMap(params);
			
			try {
				RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
				
				//R5 #14076 start
				RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
				restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
				
				if(params.get(CtReportConst.PARAM_KEY_CONTRACT_NO) != null){
					restDtlDTO.getRptRequestDtl().setDocumentNo01(params.get(CtReportConst.PARAM_KEY_CONTRACT_NO));
				}
				if(params.get(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO) != null){
					restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO));
				}
				if(params.get(CtReportConst.PARAM_KEY_CIRCULAR_NO) != null){
					restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(CtReportConst.PARAM_KEY_CIRCULAR_NO));
				}
					
				if(restDtlDTO.getRptRequestDtl().getDocumentNo01() != null || restDtlDTO.getRptRequestDtl().getDocumentNo02() != null || restDtlDTO.getRptRequestDtl().getDocumentNo03() != null ){
					addRptRequestDetail(restDtlDTO);	
				}
				//R5 #14076 end
				
				getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
				RequestContext.getCurrentInstance().execute(
						"rptConfirmationDlg.show()");
				
				log.debug("Report URL: "+resDTO.getReportUrl());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			FacesContext.getCurrentInstance().validationFailed();
			throw new EpException(MessageConst.ERROR_775186);
		}
	}
	//#32675 - shaqimi end
	
	//start re-endorsed - #29416
	private boolean isItemUsageReportRoles() {
	    String[] roles = {
	        RoleCode.BPK_AUDITOR.value(),
	        RoleCode.BPK_CC_APPROVER.value(),
	        RoleCode.BASIC_SUPPLIER_ADMIN.value(),
	        RoleCode.BASIC_SUPPLIER_USER.value(),
	        RoleCode.EP_ADMIN.value(),
	        RoleCode.AUDITOR_GENERAL.value(),
	        RoleCode.BPK_CCA.value(),
	        RoleCode.AUDITOR_INTERNAL.value(),
	        RoleCode.MIN_CONTRACT_ADMIN.value(),
	        RoleCode.CT_APPROVER.value(),
	        RoleCode.REQUISITIONER.value(),
	        RoleCode.BPK_CQ_APPROVER.value()
	    };

	    for (String role : roles) {
	        if (getFacesScopes().hasRoleUser(role)) {
	            return true;
	        }
	    }
	    return false;
	}
	//End re-endorsed - #29416
	
	//Start Fix - #34889
	private boolean isNoDefaultValues() {
	    String[] roles = {
	        RoleCode.BPK_AUDITOR.value(),
	        RoleCode.MIN_CONTRACT_ADMIN.value(),
	        RoleCode.CT_APPROVER.value(),
	        RoleCode.SYS_ADMIN.value(),
	        RoleCode.BPK_CCA.value(),
	        RoleCode.BPK_CC_APPROVER.value(),
	        RoleCode.BPK_CQ_APPROVER.value()
	    };

	    for (String role : roles) {
	        if (getFacesScopes().hasRoleUser(role)) {
	            return true;
	        }
	    }
	    return false;
	}
	//End Fix - #34889
}

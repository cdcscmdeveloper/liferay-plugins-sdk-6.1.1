/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular;

import java.util.ArrayList;
import java.util.Collections; //#30014
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException; //30866

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils; //30866
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.managedbean.ct.circular.CircularManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
// Start - #30866
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.pm.common.PmParameter;
// End - #30866
/**
 * Backing Bean for Circular
 * 
 * @author kieyun
 * @since 23/12/2014
 */
@ManagedBean
@ViewScoped
public class CircularBackingBean extends BaseBackingBean<CircularManagedBean>{
	
	private static final long serialVersionUID = 1L;
	// Start - #30866
	private static final String CENTRAL_CONTRACT_VIEW_AUDIT = "/xhtml/ct/viewcontract/resultViewCircularContract.xhtml?faces-redirect=true";
	private static final String PANEL_CIRCULAR_DP_VIEW_AUDIT = "/xhtml/ct/viewcontract/resultViewCircularContractDp.xhtml?faces-redirect=true";
	private static final String PANEL_CIRCULAR_CC_QT_VIEW_AUDIT = "/xhtml/ct/viewcontract/resultViewCircularContractQt.xhtml?faces-redirect=true";
	private static final String SEARCH_CIRCULAR_MAINTENANCE = "/xhtml/ct/circular/maintenance/search.xhtml?faces-redirect=true";
	// End - #30866
	/**
	 * The method used for searching the circular list.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCircularListing() throws Exception {
		if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) &&
				(getManagedBean().getSearchPhysicalCircularNo() == null || getManagedBean().getSearchPhysicalCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularDocType() == null) && (getManagedBean().getSearchStatus() == null)) {
			throw new EpException(MessageConst.ERROR_100060);
		}
		else {
			getManagedBean().getCircularListingPagingDataModel().reloadData();
		}		
	}

	/**
	 * The method used for generating a list of available circular document
	 * type.
	 * 
	 * @return the selection list of circular document type
	 */
	public SelectItem[] getCircularDocTypeValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> circularDocTypes = new ArrayList<String>();
		if(isAuditor() || isCentralContractAdmin()) {
			circularDocTypes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
		}
		if(isAuditor() || isPanelAdmin()) {
			circularDocTypes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		}
		if(!isAuditor() && !isPanelAdmin() && !isCentralContractAdmin()) { // public view
			circularDocTypes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
			circularDocTypes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		}
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterType", SearchOperator.OPERATOR_EQUALS.value(), ParameterTypeConst.DOCUMENT_TYPES));
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), circularDocTypes));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.DOCUMENT_TYPES, searchBean);
	}

	/**
	 * The method used for generating a list of available circular status.
	 * 
	 * @return the selection list of circular status
	 */
	public SelectItem[] getCircularStatusValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> statusList = new ArrayList<String>();
		statusList.add(ParameterConst.RECORD_STATUS_ACTIVE);
		statusList.add(ParameterConst.RECORD_STATUS_CANCLED);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), statusList));
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.RECORD_STATUS, searchBean);
	}

	/**
	 * The method used for generating a list of contract status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of contract status
	 */
	public SelectItem[] getSearchCircularStatusDropdown() {
		SelectItem[] items = null;

		try {
			items = new SelectItem[5];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.bpm.all", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_EFFECTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_INACTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_EXPIRED,  
					ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CIRCULAR_STATUS_CANCELLED,  
					ResourceBundleUtils.getMessage("lbl.ct.cancelled", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * The method used to reset the search criteria for circular.
	 */
	public void searchReset(){
		getManagedBean().setSearchCircularDocType(null);
		getManagedBean().setSearchPhysicalCircularNo(null);
		getManagedBean().setSearchCircularNo(null);
		getManagedBean().setSearchCircularName(null);
		getManagedBean().setSearchSupplierName(null);
		getManagedBean().setSearchItemName(null);
		getManagedBean().setSearchStartDate(null);
		getManagedBean().setSearchEndDate(null);
		getManagedBean().setSearchStatusId(null);
		getManagedBean().setSearchStatus(null);
		getManagedBean().setSearchCircularStatus(0);
		// Start - #30866
		getManagedBean().setPqCircularType(false);
		getManagedBean().setProcurementMode(null);
		getManagedBean().setProcurementModeCode(null);
		// End - #30866
		if(getManagedBean().isReadOnly()) {
			getManagedBean().getCircularListingPagingDataModel().reloadData();
		} else {
			getManagedBean().resetCircularListingPagingDataModel();
		}
	}

	/**
	 * The method used to navigate to circular details screen.
	 * 
	 * @param circularVerId  the circular version id
	 * @param docType  the document type
	 * @return the navigate URL 
	 */
	public String gotoPage(Long circularVerId, String docType) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean ppBB = new PanelBackingBean();
			ppBB.getManagedBean().setSelectionOfZone(false);
			ppBB.getManagedBean().setSelectedPanelItems(null);
			ppBB.getManagedBean().setSelectedPanelZone(null);
			ppBB.getManagedBean().setSelectedTabIndex(0);
			ppBB.getManagedBean().setAction("view");
			ppBB.getManagedBean().setUpdateAction(false);
			ppBB.getManagedBean().setViewContract(true);
			ppBB.getManagedBean().setOutComes(new String[]{});
			ppBB.setPanelCircularVar(reqDTO);
			// Start -#30866
			if (getManagedBean().isContinuityCircularAuditSearch()) {
				ppBB.getManagedBean().setFromCircularNotice(false);
				ppBB.getManagedBean().setDifferentModule(false);
				return "/xhtml/ct/circular/panel/circularBase.xhtml?faces-redirect=true";
			} else {
				ppBB.getManagedBean().setFromCircularNotice(true);
			}
			return "../panel/circularBase.xhtml?faces-redirect=true";
			// End -#30866
		} else {
			CentralContractBackingBean ccBB = new CentralContractBackingBean();
			ccBB.getManagedBean().setSelectedCircularContract(null);
			ccBB.getManagedBean().setSelectedCircularItem(null);
			ccBB.getManagedBean().setSelectedZoneItems(null);
			ccBB.getManagedBean().setSelectedTabIndex(0);
			ccBB.getManagedBean().setAction("view");
			ccBB.getManagedBean().setViewContract(true);
			ccBB.getManagedBean().setUpdateAction(false);
			ccBB.getManagedBean().setOutComes(new String[]{});
			ccBB.setContractCircularVar(reqDTO);	
			// Start -#30866
			if (getManagedBean().isContinuityCircularAuditSearch()) {
				ccBB.getManagedBean().setFromCircularNotice(false);
				ccBB.getManagedBean().setChkCentralCircular(false);
				return "/xhtml/ct/circular/centralcontract/circularBase.xhtml?faces-redirect=true";
			} else {
				ccBB.getManagedBean().setFromCircularNotice(true);
			}
			return "../centralcontract/circularBase.xhtml?faces-redirect=true";
			// End -#30866
		}
	}

	/**
	 * The method used to navigate to circular details screen
	 * for view purpose.
	 * 
	 * @param circularVerId  the circular version id
	 * @param docType  the document type
	 * @return the navigate URL
	 */
	public String gotoViewPage(Long circularVerId, String docType) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean ppBB = new PanelBackingBean();
			ppBB.getManagedBean().setSelectionOfZone(false);
			ppBB.getManagedBean().setSelectedPanelItems(null);
			ppBB.getManagedBean().setSelectedPanelZone(null);
			ppBB.getManagedBean().setSelectedTabIndex(0);
			ppBB.getManagedBean().setAction("view");
			ppBB.getManagedBean().setUpdateAction(false);
			ppBB.getManagedBean().setViewContract(true);
			ppBB.getManagedBean().setOutComes(new String[]{});
			ppBB.setPanelCircularVar(reqDTO);
			ppBB.getManagedBean().setFromCircularNotice(true);
			return "panelCircularTab.xhtml?faces-redirect=true";
		} else {
			CentralContractBackingBean ccBB = new CentralContractBackingBean();
			ccBB.getManagedBean().setSelectedCircularContract(null);
			ccBB.getManagedBean().setSelectedCircularItem(null);
			ccBB.getManagedBean().setSelectedZoneItems(null);
			ccBB.getManagedBean().setSelectedTabIndex(0);
			ccBB.getManagedBean().setAction("view");
			ccBB.getManagedBean().setViewContract(true);
			ccBB.getManagedBean().setUpdateAction(false);
			ccBB.getManagedBean().setOutComes(new String[]{});
			ccBB.setContractCircularVar(reqDTO);	
			ccBB.getManagedBean().setFromCircularNotice(true);
			return "centralCircularTab.xhtml?faces-redirect=true";
		}
	}

	/**
	 * This  method used to fetch status of circular
	 * 
	 * @return the selection list of circular status
	 */
	public SelectItem[] getStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			
			int i = 1;
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode(ModuleCodeConst.CONTRACT_MANAGEMENT);
			reqDTO.setDocType(getManagedBean().getSearchCircularDocType());
			reqDTO.setLanguageCode(languageCode);
			//#30014 - Start
			StatusResDTO resDTO = (reqDTO.getDocType() != null)
			        ? statusSO.retrieveStatusDescList(reqDTO)
			        : new StatusResDTO();			

			List<PmStatusDesc> list =  resDTO.getStatusDescList();
			if (list == null) {
	            list = Collections.emptyList();
	        }
			items = new SelectItem[list.size() + 1];
	        items[0] = new SelectItem(
	                null,
	                ResourceBundleUtils.getMessage(
	                        "lbl.txt.select",
	                        ResourceBundleUtils.BUNDLE_COMMON_LABELS,
	                        new FacesScopes().getUserLocale()
	                )
	        );
	        //#340014 - End
			for(PmStatusDesc statusDesc : list) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			statusSO = null;
		}
		return items;
	}

	/**
	 * Method used to retrieve the information and render it on screen.
	 */
	public void preRenderSearch(boolean trackingDiarySearch) {
		searchReset();
		getManagedBean().setReadOnly(false);
		getManagedBean().resetCircularListingPagingDataModel();
		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
		FacesContext fc = FacesContext.getCurrentInstance();
		// Start - #30866
		String fullViewRootPath = null;
		fullViewRootPath = fc.getViewRoot().getViewId();
		if (fullViewRootPath.contains("trackingDiarySearch.xhtml")) {
			getManagedBean().setContinuityCircularAuditSearch(true);
		}
		// End - #30866
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "search.xhtml");
	}
	//Start - #30866
	/**
	 * The method used for searching the circular list.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCircular() throws Exception {
		try {
			if (getManagedBean().getSearchCircularDocType() == null) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
										"lbl.ct.circular.type", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
			} else if (getManagedBean().getSearchCircularDocType().equals(ParameterConst.DOCTYPE_PANEL_CIRCULAR)
					&& getManagedBean().getProcurementMode() == null && getManagedBean().isContinuityCircularAuditSearch()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_750081, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
			} else {
				getManagedBean().getCircularListingPagingDataModel().reloadData();
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "CircularBackingBean", null,
					"CircularBackingBean.searchCircular", null, e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "circular search failed."));
		}
	}
	//End - #30866

	/**
	 * User need to make decision on circular maintenance page
	 * Navigate to circular page as per user selection for update or view
	 * 
	 * @param action			the user action
	 * @param circularVerId		the circular version id
	 * @return the navigate URL 
	 * */
	public String gotoPage(String action, Long circularVerId) {

		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setCircularVerId(circularVerId);

		if(getManagedBean().getSearchCircularDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			PanelBackingBean panelBackingBean = new PanelBackingBean();
			panelBackingBean.getManagedBean().setSelectionOfZone(false);
			panelBackingBean.getManagedBean().setSelectedPanelItems(null);
			panelBackingBean.getManagedBean().setSelectedPanelZone(null);
			panelBackingBean.getManagedBean().setSelectedTabIndex(0);
			panelBackingBean.getManagedBean().setAction(action);
			panelBackingBean.getManagedBean().setGotoPreviewPage("/xhtml/ct/circular/maintenance/search.xhtml?faces-redirect=true");

			if(action.equalsIgnoreCase("view")) {
				panelBackingBean.getManagedBean().setUpdateAction(false);
				panelBackingBean.getManagedBean().setViewContract(true);
				panelBackingBean.getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				panelBackingBean.getManagedBean().setUpdateAction(true);
				panelBackingBean.getManagedBean().setViewContract(false);
				panelBackingBean.getManagedBean().setCircularExtension(false);
				panelBackingBean.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			}
			panelBackingBean.setPanelCircularVar(reqDTO);
			return "/xhtml/ct/circular/panel/circularBase.xhtml?faces-redirect=true";
		} else {
			CentralContractBackingBean centralContractBackingBean = new CentralContractBackingBean();
			centralContractBackingBean.getManagedBean().setSelectedCircularContract(null);
			centralContractBackingBean.getManagedBean().setSelectedCircularItem(null);
			centralContractBackingBean.getManagedBean().setSelectedZoneItems(null);
			centralContractBackingBean.getManagedBean().setSelectedTabIndex(0);
			centralContractBackingBean.getManagedBean().setAction(action);
			centralContractBackingBean.getManagedBean().setGotoPreviewPage("/xhtml/ct/circular/maintenance/search.xhtml?faces-redirect=true");

			if(action.equalsIgnoreCase("view")) {
				centralContractBackingBean.getManagedBean().setViewContract(true);
				centralContractBackingBean.getManagedBean().setUpdateAction(false);
				centralContractBackingBean.getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				centralContractBackingBean.getManagedBean().setUpdateAction(true);
				centralContractBackingBean.getManagedBean().setViewContract(false);
				centralContractBackingBean.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			}
			centralContractBackingBean.setContractCircularVar(reqDTO);
			return "/xhtml/ct/circular/centralcontract/circularBase.xhtml?faces-redirect=true";
		}
	}

	/**
	 * Method used to check is it the login user have the Panel Circular Admin Role
	 * 
	 * @return true, if login user is panel administrator
	 *         false, if login user is not panel administrator
	 */
	public boolean isPanelAdmin() {
		return new FacesScopes().hasRoleUser(RoleCode.BPK_PCA.value());
	}

	/**
	 * Method used to check is it the login user have the Cental Contract Admin Role
	 * 
	 * @return true, if login user is central contract administrator
	 *         false, if login user is not central contract administrator
	 */
	public boolean isCentralContractAdmin() {
		return new FacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}

	/**
	 * Method used to check is it the login user have the Auditor Role
	 * 
	 * @return true, if login user is auditor
	 *         false, if login user is not auditor
	 */
	public boolean isAuditor() {
		return new FacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) ||  
				new FacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}
	
	// Start - #30866
	/**
	 * This method used to retrieve login user information.
	 */
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier false, if login user is not
	 *         supplier
	 */
	public boolean isSupplierRole() {
		return getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}

	/**
	 * method to display audit instruction
	 * 
	 * @return true if user is from audit menu (directory)
	 */
	public boolean isAuditCheck() {
		boolean auditCheck = false;
		if (getManagedBean().isContinuityCircularAuditSearch()) {
			// Only allow user with specific designated role and the request is
			// only from audit menu
			auditCheck = true;
		}
		return auditCheck;
	}

	/**
	 * method to determine specific user roles for audit(supplier)
	 * 
	 * Only allow user with supplier_role and the request is only from the audit
	 * menu
	 * 
	 * @return true if user has either one of the describe roles
	 */
	public boolean isAuditCheckForSupplier() {
		boolean auditSupplierCheck = false;
		if (isSupplierRole()) {
			// Only allow user with supplier_role and the request is only from
			// audit menu
			auditSupplierCheck = getManagedBean().isContinuityCircularAuditSearch();
		}
		return auditSupplierCheck;
	}

	/**
	 * Gets the procurement mode drop-down values. Display procurement mode
	 * values(DirectPurchase/Quotation/Tender) in maintenance audit screen for
	 * circular Panel(PQ).
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<>();
		List<String> procList = new ArrayList<>();

		procList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		procList.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
		procList.add(ParameterConst.PROCUREMENT_MODE_TENDER);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), procList));

		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
	}

	/**
	 * Procurement Mode drop-down changed action(listener) from UI
	 */
	public void procurementModeChanged() {
		IParameterSO parameterSO = null;
		PmParameter pmParam = new PmParameter();
		try {
			String parameterCode = getManagedBean().getProcurementMode();
			String paramenterType = ParameterTypeConst.SC_PROCUREMENT_MODE;
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(paramenterType);
			if (parameterCode != null) {
				ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				if (resDTO.isSuccessful()) {
					pmParam = resDTO.getPmParameter();
					getManagedBean().setPmParameterId(pmParam.getParameterId());
					getManagedBean().setProcurementModeCode(pmParam.getParameterCode());
				}
			}
		} catch (NoResultException e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "CircularBackingBean", null,
					"CircularBackingBean.procurementModeChanged", null, e);
		} finally {
			parameterSO = null;
		}

	}

	/**
	 * The method used to detect all on-change activity related to panel
	 * circular type for search. This method used to determine circular type
	 * selected by the user
	 */
	public void getCircularTypeOnChange() {
		if (getManagedBean().getSearchCircularDocType() != null) {
			if (getManagedBean().getSearchCircularDocType().equals(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
				getManagedBean().setPqCircularType(true);
			} else {
				getManagedBean().setPqCircularType(false);
				getManagedBean().setProcurementMode(null);
				getManagedBean().setProcurementModeCode(null);
			}
		} else {
			// case when user choose (select one) in drop-down menu
			getManagedBean().setPqCircularType(false);
		}
	}

	/**
	 * The method used to determine the rendering object to display a specific
	 * field
	 */
	public boolean isPqCircularCheck() {
		return  getManagedBean().isPqCircularType() ? isAuditCheck() : false;
	}

	/**
	 * The method used to retrieve the status completion of the contract.
	 * 
	 * @return complete or incomplete
	 */
	public String chkContractStatusType(int status) {
		FacesScopes fc = new FacesScopes();
		String contractStatusCheck = "";
		if (status == ContractConst.CONTRACT_STATUS_EFFECTIVE) {
			contractStatusCheck = ResourceBundleUtils.getMessage("lbl.ct.effective",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());
		} else if (status == ContractConst.CONTRACT_STATUS_INACTIVE) {
			contractStatusCheck = ResourceBundleUtils.getMessage("lbl.ct.inactive",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());
		} else if (status == ContractConst.CONTRACT_STATUS_TERMINATED) {
			contractStatusCheck = ResourceBundleUtils.getMessage("lbl.ct.terminated",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());

		} else if (status == ContractConst.CONTRACT_STATUS_EXPIRED) {
			contractStatusCheck = ResourceBundleUtils.getMessage("lbl.ct.expired",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());

		} else {
			contractStatusCheck = ResourceBundleUtils.getMessage("lbl.ct.not.applicable",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());
		}
		return contractStatusCheck;
	}

	/**
	 * The method used to check the the docType for language used by the user
	 * 
	 * @param code
	 *            (Q/T)
	 * @return appropriate language code
	 */
	public String checkProcModeValue(String code) {
		FacesScopes fc = new FacesScopes();
		String procModeValue = "";
		if (code.equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			procModeValue = ResourceBundleUtils.getMessage("lbl.qt.maintenance.quotation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());
		} else if (code.equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
			procModeValue = ResourceBundleUtils.getMessage("lbl.qt.maintenance.tender",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fc.getUserLocale());
		}
		return procModeValue;
	}

	/**
	 * The method used to check the the docType for language used by the user
	 * 
	 * @param code
	 *            (RN)
	 * @return appropriate language code
	 */
	public String checkDpCode(String code) {
		String dpCode = "";
		if (code.equals(ParameterConst.REQUEST_NOTE_RN)) {
			dpCode = ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
		return dpCode;
	}

	/**
	 * The method used to navigate to previous screen. This method will navigate
	 * to circular maintenance screen when the previous screen is not set.
	 * 
	 * @return the navigate URL
	 */
	public String backAction() {
		if (getManagedBean().getSearchCircularNo() != null) {
			getManagedBean().setSearchCircularNo(null);
		}
		return SEARCH_CIRCULAR_MAINTENANCE;
	}

	/**
	 * contract search from audit maintenance screen.
	 * 
	 * @param circularNo
	 * @param circularData
	 * @return navigate to specific audit view page(URL)
	 * @throws Exception
	 */
	public String searchCircularContractAudit(String circularNo, CtCircularVer circularData) {
		String searchDefault = "";
		getManagedBean().setSearchCircularNo(circularNo);
		if (getManagedBean().getSearchCircularDocType().equals(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR)) {
			getManagedBean().setCircularCqCentralContractList(circularData);
			getManagedBean().getCircularCtListingDataModel().reloadData();
			getManagedBean().setCircularDocType(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR); // use for back button
			getManagedBean().setExternalPageUrlForAudit(CENTRAL_CONTRACT_VIEW_AUDIT); // use for back button
			searchDefault =  CENTRAL_CONTRACT_VIEW_AUDIT;
		} else if (getManagedBean().getSearchCircularDocType().equals(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
			getManagedBean().setCircularDocType(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
			if (getManagedBean().getProcurementModeCode().equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)
					|| getManagedBean().getProcurementModeCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
				getManagedBean().setCircularPqQtList(circularData);
				// getManagedBean().getCircularQuotationListingDataModel().reloadData();
				getManagedBean().getCircularQtListingDataModel().reloadData();
				getManagedBean().setExternalPageUrlForAudit(PANEL_CIRCULAR_CC_QT_VIEW_AUDIT);
				searchDefault = PANEL_CIRCULAR_CC_QT_VIEW_AUDIT;
			} else if (getManagedBean().getProcurementModeCode()
					.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				getManagedBean().setCircularPqDirectPurchaseList(circularData);
				getManagedBean().getCircularDpListingDataModel().reloadData();
				getManagedBean().setExternalPageUrlForAudit(PANEL_CIRCULAR_DP_VIEW_AUDIT);
				searchDefault = PANEL_CIRCULAR_DP_VIEW_AUDIT;
			}
		}

		return searchDefault;
	}
	// End - #30866
	
	//#35506 - Start
	/**
	 * method to determine specific user roles for audit CT Trail
	 * 
	 * Only allow user with specific designated role and the request is only
	 * 
	 * @return true if user has either one of the describe roles
	 */
	public boolean isAuditRoleCheck() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.SYS_ADMIN.value());
	}
	//#35506 - End
}

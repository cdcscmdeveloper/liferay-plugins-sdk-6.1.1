package my.ep.web.backingbean.report.dp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.constant.sc.DPConstants;
import my.ep.web.managedbean.report.dp.ItemExceedThresholdRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zaim
 * 
 */
@SuppressWarnings("serial")
@ManagedBean(name = "itemExceedThresholdRptBb")
@ViewScoped
public class ItemExceedThresholdRptBackingBean extends DpReportBackingBean<ItemExceedThresholdRptManagedBean> {

	/**
	 * @param args
	 */

	private static final String ORG_TYPE_CODE_PARAM = "orgTypeCode.parameterCode^";
	private static final String RECORD_STATUS_PARAM = "|recordStatus^1";
	private boolean isValidationError = false;

	// Use Set for faster lookup
	private static final Set<RoleCode> ENABLE_FIELD_ROLE = EnumSet.of(RoleCode.EP_ADMIN , RoleCode.AUDITOR_INTERNAL,
			RoleCode.AUDITOR_GENERAL, RoleCode.BPK_AUDITOR, RoleCode.BPK_APPROVER);

	private static final Set<RoleCode> DISABLE_FIELD_ROLE = EnumSet.of(RoleCode.REQUISITIONER, RoleCode.RN_APPROVER);

	/**
	 * Constructor for class ItemExceedThresholdRptBackingBean.
	 */
	public ItemExceedThresholdRptBackingBean() {
		getManagedBean().setFinancialYear(null);
		getManagedBean().setCentralPanelContractSelected(false);
		getManagedBean().setDisableReason(false);
		initializeOrgFieldValue();
		resetSearch();
	}

	/**
	 * Generates report if all required fields are valid.
	 * 
	 * Calls {@link #reportValidationFields()} first. Only proceeds to
	 * {@link #generateDpReport(LinkedHashMap)} if validation passes.
	 * 
	 * @param params report generation parameters
	 * @see #reportValidationFields()
	 * @see #generateDpReport(LinkedHashMap)
	 * 
	 * @param params
	 */
	public void generateReport(LinkedHashMap<String, String> params) { // NOSONAR - follow exact same return type the parent declares
		if (!isValidationError){
			generateDpReport(params);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.report.dp.DpReportBackingBean#resetSearch() To
	 * reset value to default value
	 */
	@Override
	public void resetSearch() {

		if (getManagedBean().isDisableRole()) {
			getManagedBean().setDisableMinistryField(false);
			getManagedBean().setDisableOrgField(true);
			getManagedBean().setDisablePTJ(true);
			getManagedBean().setSelectedMinistry(null);
			getManagedBean().setSelectedKumpulanPTJ(null);
			getManagedBean().setSelectedPTJ(null);
		} else {
			getManagedBean().setDisableMinistryField(true);
			getManagedBean().setDisableOrgField(true);
		}
		getManagedBean().setReportUrl(null);
		getManagedBean().setCentralPanelContract(null);
		getManagedBean().setCircularNo(null);
		getManagedBean().setStatus(null);
		getManagedBean().setItemCode(null);
		getManagedBean().setProcurementType(null);
		getManagedBean().setItemName(null);
		getManagedBean().setGroupBy(null);
		getManagedBean().setRnNo(null);
		getManagedBean().setFinancialYear(null);
		getManagedBean().setDisableReason(false);
		getManagedBean().setCentralPanelContractSelected(false);

	}

	@Override
	public LinkedHashMap<String, String> getparams() { // NOSONAR - follow exact same return type the parent declares
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		validateCriteria();

		if (!isValidationError) {
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_MINISTRY, getManagedBean().getSelectedMinistry()
					.getOrgProfileId().toString());
			if (getManagedBean().getSelectedKumpulanPTJ() != null) {
				addIfNotEmpty(map, DpReportConst.PARAM_KEY_KUMPULAN_PTJ, getManagedBean().getSelectedKumpulanPTJ()
						.getOrgProfileId().toString());
			}

			if (getManagedBean().getSelectedPTJ() != null) {
				addIfNotEmpty(map, DpReportConst.PARAM_KEY_PTJ, getManagedBean().getSelectedPTJ().getOrgProfileId()
						.toString());
			}
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_CENTRAL_PANEL_CONTRACT, getManagedBean()
					.getCentralPanelContract() ? DpReportConst.YES_FLAG : DpReportConst.NO_FLAG);
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_CIRCULAR_NO, getManagedBean().getCircularNo());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_STATUS, getManagedBean().getDpStatus());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_ITEM_CODE, getManagedBean().getItemCode());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_PROCUREMENT_TYPE, getManagedBean().getProcurementType());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_ITEM_NAME, getManagedBean().getItemName());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_REASON, getManagedBean().getReason());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_RN_NO, getManagedBean().getRnNo());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_GROUP, getManagedBean().getGroupBy());
			addIfNotEmpty(map, DpReportConst.PARAM_KEY_FINANCIAL_YEAR, getManagedBean().getFinancialYear());

			if (MapUtils.isNotEmpty(map)) {
				constructReportCodeAndName(map);
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780003);
			}
		}
		return map;
	}

	/**
	 * @param map
	 */
	private void constructReportCodeAndName(LinkedHashMap<String, String> map) {
		// mandatory map values
		if (getManagedBean().getCentralPanelContract() != null) {
			if (getManagedBean().getCentralPanelContract()) {
				// panel
				this.getManagedBean().setReportName(DPConstants.SC_ITEM_EXCEEDED_THRESHOLD_PANEL);
				this.getManagedBean().setReportCode(DpReportConst.DP_ITEM_EXCEED_THRESHOLD_PANEL);
			} else {
				// non panel
				this.getManagedBean().setReportName(DPConstants.SC_ITEM_EXCEEDED_THRESHOLD_NON_PANEL);
				this.getManagedBean().setReportCode(DpReportConst.DP_ITEM_EXCEED_THRESHOLD_NON_PANEL);
			}
		}

		map.put(DpReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		map.put(DpReportConst.PARAM_KEY_REPORT_ID, getManagedBean().getReportCode());
		map.put(DpReportConst.PARAM_KEY_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
	}	
	/**
	 * Adds non-empty string to map.
	 * 
	 * Skips null, empty, or blank strings (StringUtils.isNotEmpty).
	 * 
	 * @param map target map
	 * @param key map key  
	 * @param value string value to add (if non-empty)
	 */
	private void addIfNotEmpty(Map<String, String> map, String key, String value) {
		if (StringUtils.isNotEmpty(value)) {
			map.put(key, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.backingbean.report.dp.DpReportBackingBean#validateCriteria()
	 */
	@Override
	public void validateCriteria() {
		isValidationError = reportValidationFields();
	}

	/**
	 * Generates a list of selectable years for use in dropdown menus or UI
	 * components. The list starts from the year 2018 and continues up to the
	 * current system year. Each entry is represented as a {@link SelectItem}
	 * with the year as both the value and label. Additionally, if the managed
	 * bean's financial year is not yet set, this method initializes it to the
	 * current year by default to ensure a valid selection is always available.
	 * 
	 * @return a list of {@link SelectItem} objects representing the available
	 *         year options, including all years from 2018 through the current
	 *         year.
	 */
	public List<SelectItem> generateYearList() {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		List<SelectItem> yearList = new ArrayList<>();

		for (int i = 2018; i <= currentYear; i++) {
			yearList.add(new SelectItem(i, String.valueOf(i)));
		}

		// set default value to current year if not selected yet
		// reset value
		if (getManagedBean().getFinancialYear() == null) {
			getManagedBean().setFinancialYear(String.valueOf(currentYear));
		}

		return yearList;
	}

	/**
	 * Listener method triggered when the Ministry dropdown value changes in the
	 * JSF view.
	 * 
	 * @param entity
	 */
	@SuppressWarnings("squid:S1172") //Suppressed: parameter required by JSF binding
	public void onChangeMinistry(BaseEntity entity) {
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);

		if (getManagedBean().isDisableRole()) {
			getManagedBean().setDisableOrgField(false);
			getManagedBean().setDisablePTJ(true);
		}
	}

	/**
	 * Construct ministry condition fields.
	 * 
	 * @return the string
	 */
	public String constructMinistryConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append(ORG_TYPE_CODE_PARAM);
		sb.append(OrganizationType.MINISTRY.value());
		sb.append(RECORD_STATUS_PARAM);
		return sb.toString();
	}

	/**
	 * Construct kumpulan ptj condition fields.
	 * 
	 * @return the string
	 */
	public String constructKumpulanPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append(ORG_TYPE_CODE_PARAM);
		sb.append(OrganizationType.DEPARTMENT.value());
		sb.append(RECORD_STATUS_PARAM);
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),
				OrganizationType.MINISTRY.value()));
		sb.append("^");
		if (this.getManagedBean().getSelectedMinistry() != null) {
			sb.append(this.getManagedBean().getSelectedMinistry().getOrgProfileId());
		}
		return sb.toString();
	}

	/**
	 * Reset the selected value when change the Kumpulan PTJ
	 * 
	 * @param entity
	 */
	@SuppressWarnings("squid:S1172")
	// Suppressed: parameter required by JSF binding
	public void onChangeKumpulanPTJ(BaseEntity entity) {
		getManagedBean().setSelectedPTJ(null);

		if (getManagedBean().isDisableRole()) {
			getManagedBean().setDisablePTJ(false);
		}
	}

	/**
	 * Construct ptj condition fields.
	 * 
	 * @return the string
	 */
	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append(ORG_TYPE_CODE_PARAM);
		sb.append(OrganizationType.DIVISION.value());
		sb.append(RECORD_STATUS_PARAM);
		sb.append("|");
		sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		if (this.getManagedBean().getSelectedKumpulanPTJ() != null) {
			sb.append(this.getManagedBean().getSelectedKumpulanPTJ().getOrgProfileId());
		}
		return sb.toString();
	}

	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.PROCUREMENT_TYPE_RPT, null);
		SelectItem[] filtered;
		
		if (getManagedBean().getCentralPanelContract() == null || !getManagedBean().getCentralPanelContract()){
			filtered = constructNonPanelProcurementDropdown(items);
		}else {
			filtered = constructPanelProcurementDropdown(items);
		}
		return filtered;
	}

	/**
	 * @param items
	 * @return
	 */
	private SelectItem[] constructNonPanelProcurementDropdown(SelectItem[] items) {
		List<SelectItem> list = new ArrayList<>();
		for (SelectItem item : items) {
			if (item.getValue() == null || !DPConstants.FULFILMENT_TYPE_G2G.equals(item.getLabel())) {
	            list.add(item);
	        }
		}

		SelectItem[] filtered = list.toArray(new SelectItem[list.size()]);

		if (filtered != null && items.length > 0) {
			filtered[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.all",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		}
		return filtered;
	}
	
	/**
	 * @param items
	 * @return
	 */
	private SelectItem[] constructPanelProcurementDropdown(SelectItem[] items) {
		List<SelectItem> list = new ArrayList<>();
		for (SelectItem item : items) {
			if (DPConstants.FULFILMENT_TYPE_NORMAL.equals(item.getLabel())) {
	            list.add(item);
	        }
		}
		return list.toArray(new SelectItem[list.size()]);
	}

	/**
	 * Listener method triggered when the Financial Year dropdown value changes
	 * in the JSF view.
	 */
	public void onChangeValues() {
		getManagedBean().setFinancialYear(getManagedBean().getFinancialYear());
	}

	/**
	 * @param roles
	 * @return
	 */
	private boolean hasAnyRole(Set<RoleCode> roles) {
		for (RoleCode role : roles) {
			if (getFacesScopes().hasRoleUser(role.value())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Identify user organization role
	 */
	public void initializeOrgFieldValue() {
		List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrgWithoutRole(getFacesScopes().getUserSessionBean()
				.getUserId(), false);

		if (userOrgList.isEmpty()) {
			return; // safety check
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();

		if (hasAnyRole(ENABLE_FIELD_ROLE)) {
			applyEnableFieldRoleFlags();
		} else if (hasAnyRole(DISABLE_FIELD_ROLE)) {
			applyDisableFieldRoleFlags(pmOrgProfile);
		}
	}

	/**
	 * Applies the UI state flags for users who belong to any role in
	 * {@code ENABLE_FIELD_ROLE}.
	 * <p>
	 * This configuration enables the Ministry field while disabling the
	 * Organization, PTJ, and Role fields. It is used to control which form
	 * fields are accessible based on the user's role.
	 */
	private void applyEnableFieldRoleFlags() {
		getManagedBean().setDisableMinistryField(false);
		getManagedBean().setDisableOrgField(true);
		getManagedBean().setDisablePTJ(true);
		getManagedBean().setDisableRole(true);
	}

	/**
	 * Applies the UI state flags for users who belong to any role in
	 * {@code DISABLE_FIELD_ROLE}.
	 * <p>
	 * This configuration disables the Ministry, Organization, and PTJ fields,
	 * while automatically setting the user's Ministry, Kumpulan PTJ, and PTJ
	 * values based on their organization profile. It ensures that form field
	 * accessibility is restricted according to the user's role.
	 */
	private void applyDisableFieldRoleFlags(PmOrgProfile pmOrgProfile) {
		getManagedBean().setDisableMinistryField(true);
		getManagedBean().setDisableOrgField(true);
		getManagedBean().setDisablePTJ(true);

		getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
		getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
		getManagedBean().setSelectedPTJ(pmOrgProfile);
	}

	/**
	 * Listener method triggered when the Central Panel Contract dropdown value
	 * changes in the JSF view.
	 */
	public void onCentralPanelChange() {
		if (getManagedBean().getCentralPanelContract() == null || !getManagedBean().getCentralPanelContract()){
			getManagedBean().setCentralPanelContractSelected(false);
		}else if (getManagedBean().getCentralPanelContract()){
			getManagedBean().setCentralPanelContractSelected(true);
		}
		getProcurementModeDropdownValues();
	}

	/**
	 * Listener method triggered when the procurement type dropdown value
	 * changes in the JSF view.
	 */
	public void onChangeProcurementType() {
		Object selected = getManagedBean().getProcurementType();

		if (selected == null) {
			getManagedBean().setDisableReason(false);
			return;
		}

		// convert ke string
		String procType = selected.toString();

		// check sama ada dia PK7 atau SPECIAL_ITEM
		boolean isReasonRequired = ParameterConst.PARAM_ID_PK7.equalsIgnoreCase(procType)
				|| ParameterConst.PARAM_ID_SPECIAL_ITEM.equalsIgnoreCase(procType);

		getManagedBean().setDisableReason(isReasonRequired);

		try {
			getManagedBean().setProcurementType(procType);
		} catch (NumberFormatException e) {
			getManagedBean().setProcurementType(null);
		}
	}

	/**
	 * Listener method triggered when the Reason dropdown value changes in the
	 * JSF view.
	 * 
	 * @return
	 */
	public SelectItem[] getReasonDropDown() {
		String param = StringUtils.EMPTY;

		if (getManagedBean().getProcurementType() == null) {
			return new SelectItem[] { new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.all",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())) };
		}

		if (getManagedBean().getProcurementType().equalsIgnoreCase(DpReportConst.PARAM_ID_SPECIAL_ITEM)) {
			param = DpReportConst.REPORT_CODE_RPT_SPECIAL_ITEM;
		} else if (getManagedBean().getProcurementType().equalsIgnoreCase(DpReportConst.PARAM_ID_PK7)) {
			param = DpReportConst.REPORT_CODE_RPT_PK7;
		}
		SelectItem[] items = getReasonCodeDropdownValues(param);

		if (items != null && items.length > 0) {
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.rpt.pleaseSelect",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		}
		return items;
	}

	/**
	 * Validates mandatory fields required for report generation.
	 * 
	 * Checks the following required fields:
	 * Ministry selection (orgName and orgCode must not be empty)</li>
	 * Central Panel Contract selection</li>
	 * Financial Year selection</li>
	 * 
	 * Each validation failure adds an appropriate error message via {@link #addErrorMessage(String)}.
	 * 
	 * @return {@code true} if any validation error is found (report generation should be prevented),
	 *         {@code false} if all fields are valid and report can be generated
	 */
	public boolean reportValidationFields() {
	    ItemExceedThresholdRptManagedBean bean = getManagedBean();
	    boolean hasError = false;
	    
	    if (isMinistryInvalid(bean)) {
	        hasError = true;
	        addErrorMessage("lbl.rpt.ministry");
	    }
	    
	    if (bean.getCentralPanelContract() == null) {
	        hasError = true;
	        addErrorMessage("lbl.rpt.drpdown.panel");
	    }
	    
	    if (bean.getFinancialYear() == null) {
	        hasError = true;
	        addErrorMessage("lbl.sc.rpt.financial.year");
	    }
	    
	    return hasError;
	}

	/**
	 * Validates ministry selection for report generation.
	 * 
	 *A ministry is considered invalid if:
	 *
	 *{@code bean.getSelectedMinistry()} is {@code null}</li>
	 *{@code ministry.getPmOrgValidity()} is {@code null}</li>
	 *
	 * 
	 * @param bean the managed bean containing ministry selection
	 * @return {@code true} if ministry selection is invalid and requires user correction,
	 *         {@code false} if ministry is properly selected with valid orgName and orgCode
	 */
	private boolean isMinistryInvalid(ItemExceedThresholdRptManagedBean bean) {
	    PmOrgProfile ministry = bean.getSelectedMinistry();
	    if (ministry == null || ministry.getPmOrgValidity() == null) {
	        return true;
	    }
	    
	    String orgName = ministry.getPmOrgValidity().getOrgName();
	    String orgCode = ministry.getPmOrgValidity().getOrgCode();
	    
	    return StringUtils.isEmpty(orgName) || StringUtils.isEmpty(orgCode);
	}


	/**
	 * Adds a global error message for a required field validation failure.
	 * 
	 * The error message is constructed using:
	 * 
	 *   Error key: {@link MessageConst#ERROR_100022} (formatted with field label)</li>
	 *   Field label: Resolved from {@link ResourceBundleUtils#BUNDLE_COMMON_LABELS} using {@code labelKey}</li>
	 *   Locale: Current user's locale from {@link FacesScopes#getUserLocale()}</li>
	 * 
	 * Marks the current FacesContext as validation failed, preventing form submission.
	 * 
	 * @param labelKey the message bundle key for the field label (e.g., "lbl.rpt.ministry")
	 */
	private void addErrorMessage(String labelKey) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(labelKey,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) },
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		FacesContext.getCurrentInstance().validationFailed();
	}
	
	/**
	 * Listener method that populate status for dropdown in JSF.
	 */
	public SelectItem[] getStatusDropdown() {

	    SelectItem[] items = getStatusValuesBySearchBean(
	            ModuleCodeConst.SUPPLIER_SELECTION,
	            ParameterConst.REQUEST_NOTE_RN,
	            null
	    );

	    // Always treat null as empty
	    if (items == null || items.length == 0) {
	        return new SelectItem[0];
	    }

	    // Replace first entry’s label with "All" for this specific dropdown use-case
	    items[0] = new SelectItem(
	            null,
	            ResourceBundleUtils.getMessage(
	                    "lbl.sc.rpt.status.all",
	                    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
	                    getFacesScopes().getUserLocale()
	            )
	    );

	    return items;
	}
	
	/**
	 * Returns filtered status options for report dropdown.
	 * 
	 * Adds "All" option first, then valid statuses matching
	 * DPConstants.STATUS_ITEM_EXCEEDED_THRESHOLD_REPORT. Returns empty array if
	 * any error occurs (logged via SLAUtils).
	 * 
	 * 
	 * @param moduleCode
	 * @param docType
	 * @param searchBean
	 * @return
	 */
	public SelectItem[] getStatusValuesBySearchBean(String moduleCode, String docType,
			SearchBean<PmStatusDesc> searchBean) {

		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance().getStatusDescList(moduleCode, docType,
					searchBean);

			List<SelectItem> items = new ArrayList<>();

			// First item is a generic "all" label – data-level default
			items.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.sc.rpt.status.all",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())));

			for (PmStatusDesc desc : statusDescs) {
				if (desc.getPmStatus() != null
						&& DPConstants.STATUS_ITEM_EXCEED_THRESHOLD_REPORT.contains(desc.getPmStatus()
								.getStatusId())) {

					items.add(new SelectItem(desc.getPmStatus().getStatusId(), desc.getStatusName()));
				}
			}

			return items.toArray(new SelectItem[0]);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.DIRECT_PURCHASE, "populate SelectItem[]", null, "getStatusValuesBySearchBean", 0L, e);
			return new SelectItem[0];
		}
	}

	
	/**
	 * Listener method triggered when the Status dropdown value changes in the
	 * JSF view.
	 */
	public void onChangeStatus(){
		if (getManagedBean().getStatus() == null){
			getManagedBean().setDpStatus(null);
		}else {
			getManagedBean().setDpStatus(String.valueOf(getManagedBean().getStatus()));
		}
	}
}

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

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.managedbean.report.dp.ItemExceedThresholdRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

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

	// Use Set for faster lookup
	private static final Set<RoleCode> ENABLE_FIELD_ROLE = EnumSet.of(RoleCode.SYS_ADMIN, RoleCode.AUDITOR_INTERNAL,
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
	}

	/**
	 * @param params
	 */
	public void generateReport(LinkedHashMap<String, String> params) { // NOSONAR - follow exact same return type the parent declares
		if (!reportValidationFields()) {
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

		addIfNotNull(map, DpReportConst.PARAM_KEY_MINISTRY, getManagedBean().getSelectedMinistry());
		addIfNotNull(map, DpReportConst.PARAM_KEY_KUMPULAN_PTJ, getManagedBean().getSelectedKumpulanPTJ());
		addIfNotNull(map, DpReportConst.PARAM_KEY_PTJ, getManagedBean().getSelectedPTJ());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_CENTRAL_PANEL_CONTRACT, getManagedBean().getCentralPanelContract());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_CIRCULAR_NO, getManagedBean().getCircularNo());
		addIfNotNull(map, DpReportConst.PARAM_KEY_STATUS, getManagedBean().getStatus());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_ITEM_CODE, getManagedBean().getItemCode());
		addIfNotNull(map, DpReportConst.PARAM_KEY_PROCUREMENT_TYPE, getManagedBean().getProcurementType());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_ITEM_NAME, getManagedBean().getItemName());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_REASON, getManagedBean().getReason());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_RN_NO, getManagedBean().getRnNo());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_GROUP, getManagedBean().getGroupBy());
		addIfNotEmpty(map, DpReportConst.PARAM_KEY_FINANCIAL_YEAR, getManagedBean().getFinancialYear());

		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(DpReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
			map.put(DpReportConst.PARAM_KEY_REPORT_ID, getManagedBean().getReportCode());
			map.put(DpReportConst.PARAM_KEY_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}

		return map;
	}

	private void addIfNotEmpty(Map<String, String> map, String key, String value) {
		if (StringUtils.isNotEmpty(value)) {
			map.put(key, value);
		}
	}

	private void addIfNotNull(Map<String, String> map, String key, Object obj) {
		if (obj != null) {
			map.put(key, obj.toString());
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
		// Intentionally leave blank
	}

	/**
	 * Generate year list.
	 * 
	 * @return the list
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
		Long paramCodeG2g = 1013L;
		SelectItem[] items = getDropdownValuesBySearchBean("RPT", null);

		List<SelectItem> list = new ArrayList<>();
		for (SelectItem item : items) {

			if (item.getValue() == null || !item.getValue().equals(paramCodeG2g)) {
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
		String centralPanelNo = "0";
		String centralPanelYes = "1";

		if (getManagedBean().getCentralPanelContract() == null
				|| getManagedBean().getCentralPanelContract().equalsIgnoreCase(centralPanelNo)) {
			getManagedBean().setCentralPanelContractSelected(false);
		} else if (getManagedBean().getCentralPanelContract().equalsIgnoreCase(centralPanelYes)) {
			getManagedBean().setCentralPanelContractSelected(true);
		}
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
			getManagedBean().setProcurementType(Long.valueOf(procType));
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

		if (getManagedBean().getProcurementType().compareTo(DpReportConst.PARAM_ID_SPECIAL_ITEM) == 0) {
			param = DpReportConst.REPORT_CODE_RPT_SPECIAL_ITEM;
		} else if (getManagedBean().getProcurementType().compareTo(DpReportConst.PARAM_ID_PK7) == 0) {
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
	 * @param params
	 * @param isReportError
	 * @return
	 */
	public boolean reportValidationFields() {
		boolean isHasError = false;

		boolean ministryInvalid = getManagedBean().getSelectedMinistry() == null
				|| getManagedBean().getSelectedMinistry().getMinstryId() == null;
		if (getManagedBean().isDisableRole() && ministryInvalid) {
			isHasError = true;
			addErrorMessage("lbl.rpt.ministry");
		}
		if (getManagedBean().getCentralPanelContract() == null) {
			isHasError = true;
			addErrorMessage("lbl.rpt.drpdown.panel");
		}
		if (getManagedBean().getFinancialYear() == null) {
			isHasError = true;
			addErrorMessage("lbl.sc.rpt.financial.year");
		}

		return isHasError;
	}

	/**
	 * @param labelKey
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

}

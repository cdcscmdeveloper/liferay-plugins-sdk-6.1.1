package my.ep.web.backingbean.report.fl.pa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.pm.ParameterConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.pa.ItemPanelThresholdRptManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;

/**
 * Backing Bean for Item Panel Threshold - 31344
 * 
 * @author luqmanhakim
 * @since 13/10/2025
 * 
 */
@ManagedBean(name = "itemPanelThrRptBB")
@SessionScoped
public class ItemPanelThresholdRptBackingBean extends FlReportBackingBean<ItemPanelThresholdRptManagedBean> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ItemPanelThresholdRptBackingBean.class);

	/**
	 * Initialize backing bean
	 */
	public ItemPanelThresholdRptBackingBean() {
		if (getManagedBean().getMinistry() == null) {
			defaultPtjAndMinistryValues();
		}
	}

	/**
	 * Get select item for procurement type
	 * 
	 * @return
	 */
	public SelectItem[] getDropdownValuesProcurementType() {
		SelectItem[] items = null;
		String parameterType = FlReportConst.FL_PARAMETER_TYPE_RPT;
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					parameterType, null);

			List<SelectItem> itemList = new ArrayList<>();
			
			for (PmParameterDesc parameterDesc : parameterDescs) {
				if (ParameterConst.PROC_TYPE_NORMAL.equalsIgnoreCase(parameterDesc.getPmParameter().getParameterCode())) {
					itemList.add(new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc
							.getCodeName()));
				}
			}
			items = itemList.toArray(new SelectItem[itemList.size()]);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("FL", "procurementTypeDropdown", "procurementTypeDropdown", null,
					getUserIdSessionFromLoginId(), e);
		}

		return items;
	}

	/**
	 * Reset the search form value
	 */
	public void resetAction() {
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) {
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		} else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) {
			defaultPtjAndMinistryValues();
		} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
		getManagedBean().setFinancialYear(null);
		getManagedBean().setProcurementTypeId(null);
		resetSearchCriteriaValues();
	}

	/**
	 * Generate report
	 */
	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		retrieveReportValueFromDb();
		
		if (getManagedBean().getMinistry() != null) {
			params.put(FlReportConst.FL_RPT_MINISTRY, getManagedBean().getMinistry().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtjGroup() != null) {
			params.put(FlReportConst.FL_RPT_KUMPULAN, getManagedBean().getPtjGroup().getOrgProfileId().toString());
		}
		if (getManagedBean().getPtj() != null) {
			params.put(FlReportConst.FL_RPT_PTJ_NAME, getManagedBean().getPtj().getOrgProfileId().toString());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPanelName())) {
			params.put(FlReportConst.FL_RPT_CC_PANEL_NAME, getManagedBean().getPanelName());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getPkpNo())) {
			params.put(FlReportConst.FL_RPT_TREASURY_CIRCULAR, getManagedBean().getPkpNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getFinancialYear())) {
			params.put(FlReportConst.FL_RPT_FN_YEAR, getManagedBean().getFinancialYear());
		}
		if (getManagedBean().getStatusDescId() != null) {
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if (getManagedBean().getProcurementTypeId() != null) {
			params.put(FlReportConst.FL_RPT_PROCUREMENT_TYPE, getManagedBean().getProcurementTypeId().toString());
		}
		if (StringUtils.isNotBlank(getManagedBean().getGroup())) {
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}
		
		generateFlReport(params);

	}
}

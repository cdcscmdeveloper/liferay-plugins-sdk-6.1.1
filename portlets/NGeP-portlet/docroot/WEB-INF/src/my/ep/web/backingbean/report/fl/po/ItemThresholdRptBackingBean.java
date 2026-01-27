package my.ep.web.backingbean.report.fl.po;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.po.ItemThresholdRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.ReasonHelper;
import my.ep.web.util.pm.helper.StatusHelper;

/**
 * Backing Bean for Item Threshold Enhancement - 31344
 * 
 * @author luqmanhakim
 * @since 18/9/2025
 */
@ManagedBean(name = "itemThRptBB")
@SessionScoped
public class ItemThresholdRptBackingBean extends FlReportBackingBean<ItemThresholdRptManagedBean> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ItemThresholdRptBackingBean.class);

	/**
	 * Initialize backing bean
	 */
	public ItemThresholdRptBackingBean() {
		if (getManagedBean().getMinistry() == null) {
			defaultPtjAndMinistryValues();
		}

		getManagedBean().setReasonFlag(Boolean.FALSE);
	}

	/**
	 * Generate report
	 */
	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
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
		if (StringUtils.isNotBlank(getManagedBean().getPurchaseOrderNo())) {
			params.put(FlReportConst.FL_RPT_PURCHASE_ORDER, getManagedBean().getPurchaseOrderNo());
		}
		if (StringUtils.isNotBlank(getManagedBean().getFinancialYear())) {
			params.put(FlReportConst.FL_RPT_FN_YEAR, getManagedBean().getFinancialYear());
		}
		if (getManagedBean().getStatusDescId() != null) {
			params.put(FlReportConst.FL_RPT_STATUS, getManagedBean().getStatusDescId().toString());
		}
		if (getManagedBean().getProcurementTypeCode() != null) {
			params.put(FlReportConst.FL_RPT_PROCUREMENT_TYPE, getManagedBean().getProcurementTypeCode().toString());
		}
		if (getManagedBean().getReasonCode() != null) {
			params.put(FlReportConst.FL_RPT_REASON, getManagedBean().getReasonCode().toString());
		}
		if (StringUtils.isNotBlank(getManagedBean().getGroup())) {
			params.put(FlReportConst.FL_RPT_GROUP_BY, getManagedBean().getGroup());
		}

		generateFlReport(params);

	}

	/**
	 * Handle onChange for selected procurement type
	 */
	public void onProcurementTypeChange() {
		String procType = getManagedBean().getProcurementTypeCode();
		if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			getManagedBean().setReasonFlag(Boolean.TRUE);
			getManagedBean().setReasonDropdown(getReasonDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
		} else if (procType.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
			getManagedBean().setReasonFlag(Boolean.TRUE);
			getManagedBean().setReasonDropdown(getReasonDropdownValues(ReasonCategory.JUSTIFICATION_SIT.value()));
		} else {
			getManagedBean().setReasonFlag(Boolean.FALSE);
		}
	}

	/**
	 * Get select item for reason
	 * 
	 * @param categoryCode
	 * @return
	 */
	public SelectItem[] getReasonDropdownValues(String categoryCode) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<PmReasonDesc> reasonDescs = ReasonHelper.getInstance().getReasonDescList(categoryCode);

			items = new SelectItem[reasonDescs.size() + 1];
			items[0] = new SelectItem(0L, ResourceBundleUtils.getMessage("lbl.txt.all",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmReasonDesc reasonDesc : reasonDescs) {
				items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonCode(), reasonDesc.getReasonDesc());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * Get select item for procurement type
	 * 
	 * @return
	 */
	public SelectItem[] getDropdownValuesProcurementType() {
		SelectItem[] items = null;
		String parameterType = FlReportConst.FL_PARAMETER_TYPE_RPT;
		FacesScopes face = new FacesScopes();
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					parameterType, null);

			items = new SelectItem[parameterDescs.size()];
			items[0] = new SelectItem(0L, ResourceBundleUtils.getMessage("lbl.txt.all",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				if (!parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(),
							parameterDesc.getCodeName());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
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
		resetSearchCriteriaValues();
		getManagedBean().setReasonFlag(Boolean.FALSE);
		getManagedBean().setFinancialYear(null);
		getManagedBean().setProcurementTypeCode(null);
		getManagedBean().setReasonCode(null);

	}
}

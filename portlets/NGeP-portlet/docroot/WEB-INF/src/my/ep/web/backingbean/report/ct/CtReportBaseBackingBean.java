/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct;

import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ct.CtReportBaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class CtReportBaseBackingBean.
 * 
 * @author Nancy Ang
 */
public abstract class CtReportBaseBackingBean<T extends CtReportBaseManagedBean> extends ReportBaseBackingBean<T> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportBaseBackingBean.class);
	
	
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * The method used to retrieve the report value from database.
	 */
	public void onChangeReportName() {
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		if (!getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_LIST_OF_CIRCULAR_PQ_RPT_VAL)
				&& !getManagedBean().getReportCode().equalsIgnoreCase(CtReportConst.CT_STATEMENT_OF_ORDER_PQ_RPT_VAL))
		setCurrentActiveOrg();
	}
	
	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.info("setCurrentActiveOrg");
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);

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
		} else if(getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
			getManagedBean().setSelectedMinistry(
					pmOrgProfile);
			getManagedBean().setDisableMinistryField(true);
		}
	}
	
	/**
	 * The method used to generate the Contract Report
	 * 
	 * @param params  the map of param key and value
	 */
	
	public void generateCtReport(LinkedHashMap<String, String> params) {
		log.info("generateCtReport");
		
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
			getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
			RequestContext.getCurrentInstance().execute(
					"confirmationId.show()");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Construct ministry condition fields.
	 * 
	 * @return the string
	 */
	public String constructMinistryConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	/**
	 * Construct kumpulan ptj condition fields.
	 * 
	 * @return the string
	 */
	public String constructKumpulanPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.DEPARTMENT.value(),
				OrganizationType.MINISTRY.value()));
		sb.append("^");
		if (this.getManagedBean().getSelectedMinistry() != null) {
			sb.append(this.getManagedBean().getSelectedMinistry()
					.getOrgProfileId());
		}
		return sb.toString();
	}

	/**
	 * Construct ptj condition fields.
	 * 
	 * @return the string
	 */
	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil
				.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		if (this.getManagedBean().getSelectedKumpulanPTJ() != null) {
			sb.append(this.getManagedBean().getSelectedKumpulanPTJ()
					.getOrgProfileId());
		}
		return sb.toString();
	}
	
	/**
	 * Reset the selected value when change the Ministry
	 */
	public void onChangeMinistry(BaseEntity entity) {
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
	}
	
	/**
	 * Reset the selected value when change the Kumpulan PTJ
	 */
	public void onChangeKumpulanPTJ(BaseEntity entity) {
		getManagedBean().setSelectedPTJ(null);
	}
	
	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, null);
		
		if (items != null && items.length > 0) {
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
		}	
		return items;
	}
}

/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct;

import java.util.LinkedHashMap;

import javax.faces.application.FacesMessage; //#29416
import javax.faces.context.FacesContext; //#29416
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean; //#29416
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst; //#29416
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;//#29416
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;//#29416
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.backingbean.report.ct.cc.ContractItemMonitoringCCRptBackingBean; //#29416
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ct.CtReportBaseManagedBean;
import my.ep.web.util.FacesScopes;

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
	
	public abstract void resetSearch();

	public abstract LinkedHashMap<String, String> getparams();
	
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * The method used to generate the Contract Report
	 * 
	 * @param params  the map of param key and value
	 */
	
	public void generateReport(LinkedHashMap<String, String> params) {
		log.debug("generateReport");
		
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
	}
	
	//R5 #14076 start
	public void addRptRequestDetail(RPTRequestDtlReqDTO restDtlDTO){
				
		IRPTRequestDtlSO rptRequestDtlSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestDtlSO.class);	
		if(restDtlDTO.getRptRequestDtl().getCreatedBy() == null){
			restDtlDTO.getRptRequestDtl().setCreatedBy(getFacesScopes()
				.getUserSessionBean().getUserId());
		}
		rptRequestDtlSO.saveRPTRequestDtlDTO(restDtlDTO);
	}
	//end

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
	
	// Start - #29416
	public void checkValue() {
		// Get the bean instance
		if (getManagedBean().getLovSelectedItemList() != null) {
			StringBuilder sb = new StringBuilder();
			for (CtContractItem item : getManagedBean().getLovSelectedItemList()) {
				if (sb.length() > 0) {
					sb.append(", "); // Add comma before appending next item
				}
				sb.append(item.getContractItemId());
			}
			getManagedBean().setContractItemId(sb.toString());
		}
	}

	/**
	 * To set contractNo in searchbean
	 */
	public void onChangeContractNo() {
		String contractNo = getManagedBean().getContractNo(); // Store value in
																// a variable
		if (contractNo != null) {
			SearchBean searchbeanz = new SearchBean();
			searchbeanz.setDocNo(contractNo);
		}
	}
	
	//Start - #34889
		public boolean isCentralItemUsageMonitoringDtls(){
			boolean isQualifiedReport = false;
			if(getManagedBean().getReportCode().equals(CtReportConst.CT_CONTRACT_ITEM_USAGE_MONITORING_DTLS_RPT_CC_VAL)){
				isQualifiedReport = true;
			}
			return isQualifiedReport;
		}

	// Start - #29416 || #34889
	/**
	 * Validates the contract number before executing the LOV process.
	 * <p>
	 * This method checks if the contract number in the managed bean is:
	 * <ul>
	 * <li>Null or empty: Logs a message and adds an error message to the JSF
	 * context.</li>
	 * <li>Not exactly 17 characters long: Logs a message and adds an error
	 * message to the JSF context.</li>
	 * </ul>
	 * If any validation fails, an error message is displayed in the UI using
	 * {@link FacesContext}.
	 * </p>
	 * 
	 * @param contractNumber
	 *            is passed from different beans
	 * @return {@code true} if validation fails (error detected), otherwise
	 *         {@code false}.
	 */
	public boolean validateBeforeExecuteLov(String contractNumber) {
		boolean isError = false;
		if (contractNumber == null || contractNumber.isEmpty()) {
			isError = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(
									MessageConst.ERROR_750083,
									new String[] {
											ResourceBundleUtils.getMessage("lbl.rpt.itmName",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()),
											ResourceBundleUtils.getMessage("lbl.ct.contract.no",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()) },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		} else if (contractNumber.length() != 17) {
			isError = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_750084, new String[] { ResourceBundleUtils.getMessage(
									"lbl.ct.contract.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) }, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		}
		return isError;
	}
	// End - #29416 || #34889
}

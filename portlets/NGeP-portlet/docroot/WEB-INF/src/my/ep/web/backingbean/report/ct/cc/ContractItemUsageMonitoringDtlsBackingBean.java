package my.ep.web.backingbean.report.ct.cc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ct.cc.ContractItemUsageMonitoringDtlsCCReportManageBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext; //#29416

@ManagedBean(name = "ccContractItemUsageMonitoringDtlsReportBB")
@ViewScoped

public class ContractItemUsageMonitoringDtlsBackingBean extends CtReportBaseBackingBean<ContractItemUsageMonitoringDtlsCCReportManageBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ContractItemMonitoringCCRptBackingBean.class);
	
	public ContractItemUsageMonitoringDtlsBackingBean(){
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
	}
	
	
	@Override
	public void resetSearch() {		
		log.debug("resetSearch: ccContractItemMonitoringRptBB");
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
		getManagedBean().setCircularNo(null);
		getManagedBean().setCircularName(null);
		getManagedBean().setCircularEffFromDate(null);
		getManagedBean().setCircularEffToDate(null);
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
	
	public void selectAllMinistry() {
		getManagedBean().setMinistryFlag(CtReportConst.enableAllMinistrySelection);
		getManagedBean().setDisableOrgField(true);

	}
	
	@Override	
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
	    SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);

	    // Add string-based params
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_CONTRACT_NO, getManagedBean().getContractNo());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO, getManagedBean().getContractPhysicalNo());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_LOA_NO, getManagedBean().getLoaNo());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_LOA_PHYSICAL_NO, getManagedBean().getLoaPhysicalNo());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_AGREEMENT, getManagedBean().getAgreement());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_STATUS, getManagedBean().getStatus());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_CIRCULAR_NO, getManagedBean().getCircularNo());
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_CIRCULAR_NAME, getManagedBean().getCircularName());

	    // Add object-based (ID) params
	    mapIdIfNotNull(map, CtReportConst.PARAM_KEY_MINISTRY, getManagedBean().getSelectedMinistry());
	    mapIdIfNotNull(map, CtReportConst.PARAM_KEY_KUMPULAN_PTJ, getManagedBean().getSelectedKumpulanPTJ());
	    mapIdIfNotNull(map, CtReportConst.PARAM_KEY_PTJ, getManagedBean().getSelectedPTJ());
	    mapIdIfNotNull(map, CtReportConst.PARAM_KEY_SUPPLIER_NAME, getManagedBean().getSelectedSupplier());

	    // Add enums or other object params
	    mapIfNotNull(map, CtReportConst.PARAM_KEY_PROCUREMENT_MODE, getManagedBean().getProcurementMode());

	    // Add date-based params
	    putFormattedDateIfNotNull(map, CtReportConst.PARAM_KEY_FROM_DATE, getManagedBean().getFromDate(), df);
	    putFormattedDateIfNotNull(map, CtReportConst.PARAM_KEY_TO_DATE, getManagedBean().getToDate(), df);
	    putFormattedDateIfNotNull(map, CtReportConst.PARAM_KEY_CIRCULAR_FROM_DATE, getManagedBean().getCircularEffFromDate(), df);
	    putFormattedDateIfNotNull(map, CtReportConst.PARAM_KEY_CIRCULAR_TO_DATE, getManagedBean().getCircularEffToDate(), df);

	    // Additional parameter
	    mapIfNotEmpty(map, CtReportConst.PARAM_KEY_ITEM_NAME, getManagedBean().getContractItemId());

	    // Special case: central ministry check
	    if (getManagedBean().getSelectedMinistry() != null && isCentralItemUsageMonitoringDtls()) {
	        getManagedBean().setMinistryFlag(null);
	    }

	    log.info(" check for all ministry ::::::::::::::::::: " + getManagedBean().getMinistryFlag());
	    map.put(CtReportConst.PARAM_KEY_ALLMINISTRY_FLAG, getManagedBean().getMinistryFlag());

	    // Mandatory parameters
	    if (MapUtils.isNotEmpty(map)) {
	        map.put(CtReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
	        map.put(CtReportConst.PARAM_KEY_REPORT_ID, getManagedBean().getReportCode());
	        map.put(CtReportConst.PARAM_KEY_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
	    } else {
	        throw new EpException(MessageConst.ERROR_REPORT_780003);
	    }

	    return map;
	}
	
	/**
	 * The method used to generate the Contract Report
	 * 
	 * @param params  the map of param key and value
	 */
	
	public void generateReportCT038(LinkedHashMap<String, String> params) {
		log.debug("generateReport");
		boolean isReportError = false;
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
			log.info(" getManagedBean().getCircularNo() :::::::::::::::::::::::::::::::::: "+getManagedBean().getCircularNo());
			try {
				RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
				isReportError = reportValidationFields(params, isReportError);
				if(!isReportError){
					
					RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
					
					//R5 #14076 start
					
					restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
					
					getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
					RequestContext.getCurrentInstance().execute(
							"rptConfirmationDlg.show()");
					log.debug("Report URL: "+resDTO.getReportUrl());
					getManagedBean().setMinistryFlag(null);
				}			
				commonReportValidation(params, restDtlDTO);
				//R5 #14076 end
	
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "ccContractItemUsageMonitoringDtlsReportBB", null,
						"ccContractItemUsageMonitoringDtlsReportBB.generateReportCT038", null, e);
			}
		} else {
			throw new EpException(MessageConst.ERROR_775186);
		}
	}


	/**
	 * @param params
	 * @param restDtlDTO
	 */
	private void commonReportValidation(Map<String, String> params, RPTRequestDtlReqDTO restDtlDTO) {
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
	}


	/**
	 * @param params
	 * @param isReportError
	 * @return
	 */
	private boolean reportValidationFields(Map<String, String> params, boolean isReportError) {
		boolean hasError = isReportError;
		if ((getManagedBean().getSelectedMinistry() == null && params.get(CtReportConst.PARAM_KEY_ALLMINISTRY_FLAG) == null) && getManagedBean().getMinistryFlag() == null){
			hasError = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_750091, new String[] { ResourceBundleUtils.getMessage(
									"lbl.ct.ministry", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) }, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			FacesContext.getCurrentInstance().validationFailed();
		}
		if (getManagedBean().getCircularNo().isEmpty()){
			hasError = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_750091, new String[] { ResourceBundleUtils.getMessage(
									"lbl.rpt.crclrnolabel", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) }, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			FacesContext.getCurrentInstance().validationFailed();
		}
		return hasError;
	}
	
	
	private void mapIfNotEmpty(Map<String, String> map, String key, String value) {
	    if (StringUtils.isNotEmpty(value)) {
	        map.put(key, value);
	    }
	}

	private void mapIfNotNull(Map<String, String> map, String key, Object value) {
	    if (value != null) {
	        map.put(key, value.toString());
	    }
	}

	private void mapIdIfNotNull(Map<String, String> map, String key, PmOrgProfile bean) {
	    if (bean != null && bean.getOrgProfileId() != null) {
	        map.put(key, bean.getOrgProfileId().toString());
	    }
	}

	private void putFormattedDateIfNotNull(Map<String, String> map, String key, Date date, SimpleDateFormat df) {
	    if (date != null && StringUtils.isNotEmpty(date.toString())) {
	        map.put(key, df.format(date));
	    }
	}
}

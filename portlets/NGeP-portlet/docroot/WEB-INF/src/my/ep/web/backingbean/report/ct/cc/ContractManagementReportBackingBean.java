package my.ep.web.backingbean.report.ct.cc;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.cc.ContractManagementReportManagedBean;
import my.ep.web.util.DateUtils;

@ManagedBean(name = "ctManagementReportBB")
@ViewScoped
public class ContractManagementReportBackingBean extends CtReportBaseBackingBean <ContractManagementReportManagedBean>{

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ContractManagementReportBackingBean.class);
	
	public ContractManagementReportBackingBean(){
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
	}
	@Override
	public void resetSearch() {
		// TODO Auto-generated method stub
		
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
		getManagedBean().setFulfilmentType(null);
		getManagedBean().setSelectedSupplier(null);
		
	}
	@Override
	public LinkedHashMap<String, String> getparams() {
		// TODO Auto-generated method stub

		
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
		if (StringUtils.isNotEmpty(getManagedBean().getCircularNo())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_NO, getManagedBean()
					.getCircularNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getCircularName())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_NAME, getManagedBean()
					.getCircularName());
		}
		if (getManagedBean().getCircularEffFromDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getCircularEffFromDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_FROM_DATE, df.format(
					getManagedBean().getCircularEffFromDate()));
		}
		if (getManagedBean().getCircularEffToDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getCircularEffToDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_TO_DATE, df.format(
					getManagedBean().getCircularEffToDate()));
		}
		
		if (getManagedBean().getFulfilmentType()!= null){
			map.put(CtReportConst.PARAM_KEY_FULFILMENT_TYPE, getManagedBean()
					.getFulfilmentType().toString());
		}
		
		if (getManagedBean().getSelectedSupplier() != null) {
			map.put(CtReportConst.PARAM_KEY_SUPPLIER_NAME, getManagedBean()
					.getSelectedSupplier().getOrgProfileId().toString());
		}
		// supplier id value based on supplier login
		else if (getManagedBean().getSupplierID() != null){
			map.put(CtReportConst.PARAM_KEY_SUPPLIER_NAME, getManagedBean()
					.getSupplierID().toString());
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

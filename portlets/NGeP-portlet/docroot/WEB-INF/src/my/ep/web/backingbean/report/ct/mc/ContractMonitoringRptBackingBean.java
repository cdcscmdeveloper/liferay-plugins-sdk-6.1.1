/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.mc;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.mc.ContractMonitoringRptManagedBean;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

/**
 * The Class ContractMonitoringRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "contractMonitoringRptBB")
@ViewScoped
public class ContractMonitoringRptBackingBean extends CtReportBaseBackingBean<ContractMonitoringRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ContractMonitoringRptBackingBean.class);
	
	/**
	 * generateCtReport - Generate CONTRACT MANAGEMENT module reports.
	 */
	public void generateCtReport() {
		generateCtReport(constructParamMap());
	}
	
	private LinkedHashMap<String, String> constructParamMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
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
		
		if (getManagedBean().getContractNo() != null) {
			map.put(CtReportConst.PARAM_KEY_CONTRACT_NO, getManagedBean()
					.getContractNo());
		}
		
		if (getManagedBean().getContractPhysicalNo() != null) {
			map.put(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO, getManagedBean()
					.getContractPhysicalNo());
		}
		
		if (getManagedBean().getLoaNo() != null) {
			map.put(CtReportConst.PARAM_KEY_LOA_NO, getManagedBean()
					.getLoaNo());
		}
		
		if (getManagedBean().getLoaPhysicalNo() != null) {
			map.put(CtReportConst.PARAM_KEY_LOA_PHYSICAL_NO, getManagedBean()
					.getLoaPhysicalNo());
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

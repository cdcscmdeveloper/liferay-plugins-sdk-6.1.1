/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct;

import java.util.LinkedHashMap;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class CtReportBackingBean.
 * 
 * @author Nancy Ang
 */
public abstract class CtReportBackingBean<T extends CtReportManagedBean> extends ReportBaseBackingBean<T> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportBackingBean.class);
	
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
}

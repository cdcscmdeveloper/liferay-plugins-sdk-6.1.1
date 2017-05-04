/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report.dp;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.DpReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.dp.DpReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class DpReportBackingBean.
 *
 * @author AnJie
 * @since 25.04.2017
 */
public abstract class DpReportBackingBean<T extends DpReportManagedBean>
		extends ReportBaseBackingBean<T> {
	
	private static final long serialVersionUID = 1L;
	
	/** The log. */
	private static final Logger log = EpLogger
			.getLogger(DpReportBackingBean.class);

	/**
	 * To generate report according to params that passed in
	 * @param params
	 */
	public void generateDpReport(LinkedHashMap<String, String> params) {
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		params.put(DpReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(DpReportConst.PARAM_REPORT_ID, getManagedBean()
				.getReportCode());
		params.put(DpReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean()
				.getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
			RequestContext.getCurrentInstance().execute(
					"rptConfirmationDlg.show()");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TODO to have in base class
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	//TODO to have in base class
	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateFrom(fromDate.getTime());
			getManagedBean().setDateTo(new Date());
		}
	}
	
	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc(String docType) throws NamingException {
		return super.getStatusDropdownValues(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				docType);
	}

	public void onChangeReportName() {
		log.info("onChangeReportName");
		resetSearch();
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}
	
	public abstract void resetSearch();

	public abstract LinkedHashMap<String,String> getparams();
}

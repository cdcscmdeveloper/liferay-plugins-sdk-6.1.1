/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class CtReportBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "ctReportBackingBean")
@SessionScoped
public class CtReportBackingBean extends ReportBaseBackingBean<CtReportManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/reports/ct/ctReportMain.xhtml";
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		
		retrieveReportCategoryListByModuleCode(
				ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_CT_REPORT_MAIN);
	}
	
	public void onChangeReportName() {
		log.info("onChangeReportName");	
		
		resetAction();		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}
	
	public void generateCtReport() {
		log.info("generateCtReport");
		
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);		
		
		RPTRequestResDTO resDTO = rptRequestSO.generateReport(
				constructCtParamStrings(), 
				getManagedBean().getReportCode(), 
				null, 
				getFacesScopes().getUserSessionBean().getUserId(), 
				false, 
				getManagedBean().isTempAccess());
		
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");	
	}
	
	public void resetAction() {
		log.info("resetAction");
	}
	
	private String constructCtParamStrings() {
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();	
		
//		map.put(FlReportsConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
//		map.put(FlReportsConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
//		
		
		return constructParameterString(map);
	}

}

/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.LinkedHashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.FlReportsConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.FlReportsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "flReportsBackingBean")
@ViewScoped
public class FlReportsBackingBean extends ReportBaseBackingBean<FlReportsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_FLREPORT = "/xhtml/reports/new/fl/flreportsmain.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(FlReportsBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportListByAuthorization(ModuleCodeConst.ORDER_FULFILLMENT
				.toUpperCase());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_FLREPORT);
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void onChangeReportName(){
		log.info("onChangeReportName");	
		resetAction();
		if(StringUtils.isNotBlank(getManagedBean().getReportCode())){			
			retrieveReportValueFromDb();
		}
	}
	
	public void generateFlReport(){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);		
		
		RPTRequestResDTO resDTO = rptRequestSO.generateReport(
				constructFlParamStrings(), 
				getManagedBean().getReportCode(), 
				null, 
				getFacesScopes().getUserSessionBean().getUserId(), 
				false, 
				getManagedBean().isTempAccess());	
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
		
	
	}
	
	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}
	
	public SelectItem[] getPmZoneList() {
		return PmCommonUtil.getPmZoneListOptions();
	}
	
	private String constructFlParamStrings(){
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();	
		
		map.put(FlReportsConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		map.put(FlReportsConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
		
		if(StringUtils.isNotBlank(getManagedBean().getZoneName())){
			map.put(FlReportsConst.PARAM_FL_SUPP_ZONE_EQ, getManagedBean().getZoneName());
		}
		
		if(getManagedBean().getStatus() != null){
			map.put(FlReportsConst.PARAM_FL_SUPP_PROFILE_STATUS_EQ, getManagedBean().getStatus() == 1 ? "Active":"Inactive");
		}
		
		return constructParameterString(map);
	}
	
	public void resetAction(){
		getManagedBean().setZoneName(null);
		getManagedBean().setStatus(null);
	}
}
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
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.PmReportsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "pmReportsBackingBean")
@ViewScoped
public class PmReportsBackingBean extends ReportBaseBackingBean<PmReportsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_PMREPORT = "/xhtml/reports/new/pm/pmreportsmain.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PmReportsBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportListByAuthorization(ModuleCodeConst.PROFILE_MANAGEMENT
				.toUpperCase());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_PMREPORT);
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

//	public List<SelectItem> retrieveReportListByAuthorization() {
//		log.info("ReportBackingBean-retrieveReportListByAuthorization");
//		return this.retrieveReportListByAuthorization(ModuleCodeConst.PROFILE_MANAGEMENT
//				.toUpperCase());
//	}
	
	public void onChangeReportName(){
		log.info("onChangeReportName");	
		resetAction();
		if(StringUtils.isNotBlank(getManagedBean().getReportCode())){			
			retrieveReportValueFromDb();
		}
	}
	
	public void generatePmReport(){
		IRPTRequestSO rptRequestSO = null;
		try {
			rptRequestSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IRPTRequestSO.class);		
			RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
			reqDTO.setParametersMap(constructParamMap());
			reqDTO.setDefaultOutputType(getManagedBean().getDefaultOutputType());
			reqDTO.setTempAccess(getManagedBean().isTempAccess());
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);	
			getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
			RequestContext.getCurrentInstance().execute("confirmationId.show()");
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}finally{
			rptRequestSO = null;
		}	
		
	
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
	
	
	private LinkedHashMap<String, String> constructParamMap(){
		LinkedHashMap<String, String> map = new LinkedHashMap<>();	
		map.put(PmReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		map.put(PmReportConst.PARAM_REPORT_ID, getManagedBean().getReportCode());
		map.put(PmReportConst.PARAM_USER_ID,getFacesScopes().getUserSessionBean().getUserId().toString());
		if(StringUtils.isNotBlank(getManagedBean().getZoneName())){
			map.put(PmReportConst.PARAM_PM_SUPP_ZONE, getManagedBean().getZoneName());
		}
		
		if(getManagedBean().getStatus() != null){
			map.put(PmReportConst.PARAM_PM_SUPP_PROFILE_STATUS, getManagedBean().getStatus() == 1 ? "Active":"Inactive");
		}
		
		return map;
	}
	
	public void resetAction(){
		getManagedBean().setZoneName(null);
		getManagedBean().setStatus(null);
		
	}
	

}

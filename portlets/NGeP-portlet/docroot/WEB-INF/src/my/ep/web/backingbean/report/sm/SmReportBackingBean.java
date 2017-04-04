package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SmReportManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "smReportBB")
@ViewScoped
public class SmReportBackingBean<T extends SmReportManagedBean> extends ReportBaseBackingBean<SmReportManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(SmReportBackingBean.class);
	private static final String REPORT_MAIN = "/xhtml/reports/new/sm/smReportMain.xhtml";

	public void preRender() {
		log.info(".................SmReport Pre-Render................");
		retrieveReportListByAuthorization(StringUtils.upperCase(ModuleCodeConst.SUPPLIER_MANAGEMENT));

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, REPORT_MAIN);
	}
	
	public void generateSmReport(LinkedHashMap<String,String> params){
		String paramStr = constructParameterString(params);
		log.info("paramStr>>>>>>>>" + paramStr);
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);	
		
		/*log.info("TempAccess"+this.getManagedBean().isTempAccess());
		log.info("this.getManagedBean().getReportName():"+this.getManagedBean().getReportName());
		log.info("this.getManagedBean().getReportCode():"+this.getManagedBean().getReportCode());*/
		params.put(SmReportConst.PARAM_REPORT_CODE, getManagedBean().getReportCode());
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			log.info("resDTO.getRequestId():"+resDTO.getRequestId());
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
}

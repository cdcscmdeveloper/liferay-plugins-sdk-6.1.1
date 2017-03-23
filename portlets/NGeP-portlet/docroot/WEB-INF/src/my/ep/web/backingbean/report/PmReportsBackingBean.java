/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.PmReportsManagedBean;
import my.ep.web.util.FacesScopes;

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
		if(StringUtils.isNotBlank(getManagedBean().getReportCode())){
			for(SelectItem item : getManagedBean().getReportList()){
				if(item.getValue().equals(getManagedBean().getReportCode())){
					getManagedBean().setReportName(item.getLabel());
					break;
				}
			}
		}
	}

}

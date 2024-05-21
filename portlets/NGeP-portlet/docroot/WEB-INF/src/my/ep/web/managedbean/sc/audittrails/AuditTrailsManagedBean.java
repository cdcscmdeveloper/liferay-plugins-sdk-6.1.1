package my.ep.web.managedbean.sc.audittrails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

import my.ep.web.managedbean.BaseManagedBean;
//import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean.SearchTrackingDiaryPagingDataModel;
@ManagedBean(name = "auditTrailsManagedBean")
@SessionScoped
public class AuditTrailsManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String moduleSelected="";
	private String previousSelectedModule="";
	private String backUrl="";

	public String getModuleSelected() {
		return moduleSelected;
	}
	public void setModuleSelected(String moduleSelected) {
		this.moduleSelected = moduleSelected;
	}
	public String getPreviousSelectedModule() {
		return previousSelectedModule;
	}
	public void setPreviousSelectedModule(String previousSelectedModule) {
		this.previousSelectedModule = previousSelectedModule;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	
}

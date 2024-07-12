package my.ep.web.managedbean.sc.audittrails;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.web.bean.au.AuditTrailsSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
//import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean.SearchTrackingDiaryPagingDataModel;
@ManagedBean(name = "auditTrailsManagedBean")
@SessionScoped
public class AuditTrailsManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String moduleSelected="";
	private String previousSelectedModule="";
	private String backUrl="";
	
	//#35500
	private Long qtId;
	private String previousPageUrl;
	private int activeIndex;
	private List searchGeneralInfo;
	private AuditTrailsSearchBean auditTrailsSearchBean;
	private boolean isLazy = false;

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
	public String getPreviousPageUrl() {
		return previousPageUrl;
	}
	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}
	public int getActiveIndex() {
		return activeIndex;
	}
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}
	
	public Long getQtId() {
		return qtId;
	}
	public void setQtId(Long qtId) {
		this.qtId = qtId;
	}
	public AuditTrailsSearchBean getAuditTrailsSearchBean() {
		if (auditTrailsSearchBean == null)
			auditTrailsSearchBean = new AuditTrailsSearchBean();
		return auditTrailsSearchBean;
	}
	public void setAuditQTSearchBean(AuditTrailsSearchBean auditTrailsSearchBean) {
		this.auditTrailsSearchBean = auditTrailsSearchBean;
	}
	public boolean isLazy() {
		return false;
	}
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	public List getSearchGeneralInfo() {
		return searchGeneralInfo;
	}
	public void setSearchGeneralInfo(List searchGeneralInfo) {
		this.searchGeneralInfo = searchGeneralInfo;
	}
	
	
}

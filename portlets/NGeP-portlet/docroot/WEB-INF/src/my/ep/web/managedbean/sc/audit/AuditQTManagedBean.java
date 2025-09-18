package my.ep.web.managedbean.sc.audit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.web.bean.au.AuditQTSearchBean;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "auditQTManagedBean")
@SessionScoped
public class AuditQTManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String moduleSelected="";
	private String previousSelectedModule="";
	private String backURL="";
	
	//#35500
	private Long qtId;
	private String previousPageUrl;
	private int activeIndex;
	private List<AuditQTSearchBean> searchGeneralInfo;
	private AuditQTSearchBean auditQTSearchBean;
	private Long groupSectionId;
	
	

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
	public String getBackURL() {
		return backURL;
	}
	public void setBackURL(String backURL) {
		this.backURL = backURL;
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
	public AuditQTSearchBean getAuditQTSearchBean() {
		if (auditQTSearchBean == null)
			auditQTSearchBean = new AuditQTSearchBean();
		return auditQTSearchBean;
	}
	public void setAuditQTSearchBean(AuditQTSearchBean auditQTSearchBean) {
		this.auditQTSearchBean = auditQTSearchBean;
	}
	public List getSearchGeneralInfo() {
		return searchGeneralInfo;
	}
	public void setSearchGeneralInfo(List searchGeneralInfo) {
		this.searchGeneralInfo = searchGeneralInfo;
	}
	public Long getGroupSectionId() {
		return groupSectionId;
	}
	public void setGroupSectionId(Long groupSectionId) {
		this.groupSectionId = groupSectionId;
	}
	
}

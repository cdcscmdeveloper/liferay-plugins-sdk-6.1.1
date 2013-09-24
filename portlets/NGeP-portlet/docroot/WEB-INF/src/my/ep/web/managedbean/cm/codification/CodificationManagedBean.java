package my.ep.web.managedbean.cm.codification;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "unspscCodeMB")
@SessionScoped
public class CodificationManagedBean extends BaseManagedBean {


	private String cmNavigationURL;

	private List<CmUnspsc> unspscList;
	private CmUnspsc selectedUNSPSCObj;
	
	private String unspscCode;
	private String unspscTitle;



	





	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getUnspscTitle() {
		return unspscTitle;
	}

	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}

	public String getCmNavigationURL() {
		return cmNavigationURL;
	}

	public void setCmNavigationURL(String cmNavigationURL) {
		this.cmNavigationURL = cmNavigationURL;
	}

	public List<CmUnspsc> getUnspscList() {
		return unspscList;
	}

	public void setUnspscList(List<CmUnspsc> unspscList) {
		this.unspscList = unspscList;
	}

	public CmUnspsc getSelectedUNSPSCObj() {
		if(selectedUNSPSCObj==null)
			selectedUNSPSCObj = new CmUnspsc();
		return selectedUNSPSCObj;
	}

	public void setSelectedUNSPSCObj(CmUnspsc selectedUNSPSCObj) {
		this.selectedUNSPSCObj = selectedUNSPSCObj;
	}

	





}

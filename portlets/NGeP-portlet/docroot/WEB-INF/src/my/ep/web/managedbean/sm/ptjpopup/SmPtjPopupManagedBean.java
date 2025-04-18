package my.ep.web.managedbean.sm.ptjpopup;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * ManagedBean for Sm PTJ Popup
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smPtjPopupMB")
@SessionScoped
public class SmPtjPopupManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeNameKey;
	private String ptjCodeNameValue;

	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	public String getPtjCodeNameKey() {
		return ptjCodeNameKey;
	}

	public void setPtjCodeNameKey(String ptjCodeNameKey) {
		this.ptjCodeNameKey = ptjCodeNameKey;
	}

	public String getPtjCodeNameValue() {
		return ptjCodeNameValue;
	}

	public void setPtjCodeNameValue(String ptjCodeNameValue) {
		this.ptjCodeNameValue = ptjCodeNameValue;
	}

}
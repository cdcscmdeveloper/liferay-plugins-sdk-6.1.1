package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean(name = "cdTrackingDiaryManagedBean")
@SessionScoped
public class CdTrackingDiaryManagedBean<PmTrackingDiary> extends BaseManagedBean { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CmUnspsc autoCompleteUnspscCode;

	public CmUnspsc getAutoCompleteUnspscCode() {
		if (autoCompleteUnspscCode == null)
			autoCompleteUnspscCode = new CmUnspsc();
		return autoCompleteUnspscCode;
	}

	public void setAutoCompleteUnspscCode(CmUnspsc autoCompleteUnspscCode) {
		this.autoCompleteUnspscCode = autoCompleteUnspscCode;
	}

}

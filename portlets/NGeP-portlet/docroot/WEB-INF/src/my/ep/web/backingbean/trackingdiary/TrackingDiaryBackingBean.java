package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;

@ManagedBean(name = "trackingDiaryBackingBean")
@SessionScoped
public class TrackingDiaryBackingBean extends
		BaseBackingBean<CdTrackingDiaryManagedBean> {

	private String codeType;

	private int previousScreen = 0;//0=catSearch;1=catMaintenance;
	
	

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
}


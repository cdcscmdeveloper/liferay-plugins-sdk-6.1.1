package my.ep.web.managedbean.sm.postsupplierregistration;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean (name = "postSupplierRegMB")
@SessionScoped
public class PostSupplierRegistrationManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SmSupplier smSupplier;
	private String statusDesc;
	
	private boolean hyperlink;
	private Map<String,String> userActionMap;
	private boolean bannerImage;
	private int flowStatus;
	private SmAppl smAppl;
	private String mofExpDateMsg;
	
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public boolean isHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(boolean hyperlink) {
		this.hyperlink = hyperlink;
	}

	public Map<String, String> getUserActionMap() {
		return userActionMap;
	}

	public void setUserActionMap(Map<String, String> userActionMap) {
		this.userActionMap = userActionMap;
	}

	public boolean isBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(boolean bannerImage) {
		this.bannerImage = bannerImage;
	}

	public int getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(int flowStatus) {
		this.flowStatus = flowStatus;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public String getMofExpDateMsg() {
		return mofExpDateMsg;
	}

	public void setMofExpDateMsg(String mofExpDateMsg) {
		this.mofExpDateMsg = mofExpDateMsg;
	}

}

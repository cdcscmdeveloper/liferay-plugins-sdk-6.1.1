package my.ep.web.managedbean.sm.postsupplierregistration;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	private Map<String,String> mofUserMap;
	
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

	public Map<String, String> getMofUserMap() {
		return mofUserMap;
	}

	public void setMofUserMap(Map<String, String> mofUserMap) {
		this.mofUserMap = mofUserMap;
	}

}

package my.ep.web.managedbean.sm.updategstinfo;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.SmGstBranchVO;

@ManagedBean(name = "gstInfoMB")
@SessionScoped
public class SmUpdateGstInfoManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2838889053648703839L;
	private SmSupplier smSupplier;
	private SmCompanyBasic companyBasic;
	private SmGstSupplier hqGstSupplier;
	private SmMofAccount mofAccount;
	private List<SmGstBranchVO> branches;
	private boolean gstEmpty;

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmGstSupplier getHqGstSupplier() {
		return hqGstSupplier;
	}

	public void setHqGstSupplier(SmGstSupplier hqGstSupplier) {
		this.hqGstSupplier = hqGstSupplier;
	}

	public boolean isGstEmpty() {
		return gstEmpty;
	}

	public void setGstEmpty(boolean gstEmpty) {
		this.gstEmpty = gstEmpty;
	}

	public SmMofAccount getMofAccount() {
		return mofAccount;
	}

	public void setMofAccount(SmMofAccount mofAccount) {
		this.mofAccount = mofAccount;
	}

	public SmCompanyBasic getCompanyBasic() {
		return companyBasic;
	}

	public void setCompanyBasic(SmCompanyBasic companyBasic) {
		this.companyBasic = companyBasic;
	}

	public List<SmGstBranchVO> getBranches() {
		return branches;
	}

	public void setBranches(List<SmGstBranchVO> branches) {
		this.branches = branches;
	}

}

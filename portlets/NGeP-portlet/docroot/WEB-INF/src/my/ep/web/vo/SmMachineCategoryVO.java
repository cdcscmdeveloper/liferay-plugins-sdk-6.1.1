package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmMachineDtl;

public class SmMachineCategoryVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9150227604058321990L;
	private PmParameter categoryTypeCode;
	private List<SmMachineDtl> smMachineDtls;

	public PmParameter getCategoryTypeCode() {
		return categoryTypeCode;
	}

	public void setCategoryTypeCode(PmParameter categoryTypeCode) {
		this.categoryTypeCode = categoryTypeCode;
	}

	public List<SmMachineDtl> getSmMachineDtls() {
		return smMachineDtls;
	}

	public void setSmMachineDtls(List<SmMachineDtl> smMachineDtls) {
		this.smMachineDtls = smMachineDtls;
	}
}

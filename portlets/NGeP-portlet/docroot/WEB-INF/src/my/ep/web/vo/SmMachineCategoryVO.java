package my.ep.web.vo;

import java.util.List;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmMachineDtl;

public class SmMachineCategoryVO {
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

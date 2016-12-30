package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmApplSupportDoc;

public class SmLicenseInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8538916961941006931L;
	private List<SmApplSupportDoc> licenseSuppDocs;
	private PmCategoryL3 l3;
	private String categoryDesc;
	private int count;

	public List<SmApplSupportDoc> getLicenseSuppDocs() {
		return licenseSuppDocs;
	}

	public void setLicenseSuppDocs(List<SmApplSupportDoc> licenseSuppDocs) {
		this.licenseSuppDocs = licenseSuppDocs;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public PmCategoryL3 getL3() {
		return l3;
	}

	public void setL3(PmCategoryL3 l3) {
		this.l3 = l3;
	}

	public String getCategoryDesc() {
		return categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

}

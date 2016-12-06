package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.sm.SmApplSupportDoc;

public class SmLicenseInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8538916961941006931L;
	private List<SmApplSupportDoc> licenseSuppDocs;

	public List<SmApplSupportDoc> getLicenseSuppDocs() {
		return licenseSuppDocs;
	}

	public void setLicenseSuppDocs(List<SmApplSupportDoc> licenseSuppDocs) {
		this.licenseSuppDocs = licenseSuppDocs;
	}

}

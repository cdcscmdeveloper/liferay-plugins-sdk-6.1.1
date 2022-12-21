package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmSupplierBranch;

public class SmGstBranchVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5397341475903208711L;
	private SmSupplierBranch branch;
	private SmGstSupplier gst;
	private SmGstSupplierDoc doc;
	private boolean sameWithHq;

	public SmSupplierBranch getBranch() {
		return branch;
	}

	public void setBranch(SmSupplierBranch branch) {
		this.branch = branch;
	}

	public SmGstSupplier getGst() {
		return gst;
	}

	public void setGst(SmGstSupplier gst) {
		this.gst = gst;
	}

	public SmGstSupplierDoc getDoc() {
		return doc;
	}

	public void setDoc(SmGstSupplierDoc doc) {
		this.doc = doc;
	}

	public boolean isSameWithHq() {
		return sameWithHq;
	}

	public void setSameWithHq(boolean sameWithHq) {
		this.sameWithHq = sameWithHq;
	}
}

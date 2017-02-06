package my.ep.web.vo;

public class SmAuthorizedPersonnelCountVO {
	private boolean justificationRequired;
	private boolean reachAuthorizedPersonnelLimit;
	private boolean reachSupplierUserAuthorizedLimit;

	public boolean isJustificationRequired() {
		return justificationRequired;
	}

	public void setJustificationRequired(boolean justificationRequired) {
		this.justificationRequired = justificationRequired;
	}

	public boolean isReachAuthorizedPersonnelLimit() {
		return reachAuthorizedPersonnelLimit;
	}

	public void setReachAuthorizedPersonnelLimit(
			boolean reachAuthorizedPersonnelLimit) {
		this.reachAuthorizedPersonnelLimit = reachAuthorizedPersonnelLimit;
	}

	public boolean isReachSupplierUserAuthorizedLimit() {
		return reachSupplierUserAuthorizedLimit;
	}

	public void setReachSupplierUserAuthorizedLimit(
			boolean reachSupplierUserAuthorizedLimit) {
		this.reachSupplierUserAuthorizedLimit = reachSupplierUserAuthorizedLimit;
	}

}

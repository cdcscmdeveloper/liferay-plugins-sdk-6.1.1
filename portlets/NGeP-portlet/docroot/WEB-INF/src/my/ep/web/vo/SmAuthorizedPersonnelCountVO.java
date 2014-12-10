package my.ep.web.vo;

public class SmAuthorizedPersonnelCountVO {
	private boolean justificationRequired;
	private boolean reachAuthorizedPersonnelLimit;

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

}

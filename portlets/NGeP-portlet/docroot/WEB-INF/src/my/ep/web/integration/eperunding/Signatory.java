package my.ep.web.integration.eperunding;

public class Signatory {

	private String MOFNo;

	private AuthorizedSignatory authorizedSignatory;

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public AuthorizedSignatory getAuthorizedSignatory() {
		return authorizedSignatory;
	}

	public void setAuthorizedSignatory(AuthorizedSignatory authorizedSignatory) {
		this.authorizedSignatory = authorizedSignatory;
	}

}

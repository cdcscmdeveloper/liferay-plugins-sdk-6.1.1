package my.ep.web.integration.eperunding;


/*
 *@author P Ganga prasad
 */
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Signatory) {
			Signatory temp = (Signatory) obj;
			if (this.MOFNo.equalsIgnoreCase(temp.MOFNo) && this.authorizedSignatory.getName().equalsIgnoreCase(temp.authorizedSignatory.getName()))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.MOFNo.hashCode()+this.authorizedSignatory.getName().hashCode());
	}

}

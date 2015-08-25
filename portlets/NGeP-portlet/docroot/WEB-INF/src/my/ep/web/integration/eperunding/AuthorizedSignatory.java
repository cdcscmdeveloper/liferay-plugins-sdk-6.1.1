package my.ep.web.integration.eperunding;

/*
 * @author P Ganga prasad
 */

public class AuthorizedSignatory {

	private String name;
	private String newICNo;
	private String passport;
	private String isBumi;
	private String designation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNewICNo() {
		return newICNo;
	}

	public void setNewICNo(String newICNo) {
		this.newICNo = newICNo;
	}

	public String getIsBumi() {
		return isBumi;
	}

	public void setIsBumi(String isBumi) {
		this.isBumi = isBumi;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof AuthorizedSignatory)
	    {
	    	AuthorizedSignatory temp = (AuthorizedSignatory) obj;
	        if(this.name.equalsIgnoreCase(temp.name)&& this.newICNo.equalsIgnoreCase(temp.newICNo)&& this.passport.equalsIgnoreCase(temp.passport))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.name.hashCode()+this.newICNo.hashCode()+this.passport.hashCode());        
	}

}

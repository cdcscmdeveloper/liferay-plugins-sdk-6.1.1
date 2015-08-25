package my.ep.web.integration.eperunding;

import java.util.List;

public class Owner {

	private String isEquityOwner;
	private String name;
	private String newICNo;
	private String passport;
	private String isBumi;
	private int citizenOf;
	private String equityValue;
	private String equityPercentage;
	private String Race;
	private String designation;

	private List<Qualification> qualificationList;

	public String getIsEquityOwner() {
		return isEquityOwner;
	}

	public void setIsEquityOwner(String isEquityOwner) {
		this.isEquityOwner = isEquityOwner;
	}

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

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getIsBumi() {
		return isBumi;
	}

	public void setIsBumi(String isBumi) {
		this.isBumi = isBumi;
	}

	public int getCitizenOf() {
		return citizenOf;
	}

	public void setCitizenOf(int citizenOf) {
		this.citizenOf = citizenOf;
	}

	public String getEquityValue() {
		return equityValue;
	}

	public void setEquityValue(String equityValue) {
		this.equityValue = equityValue;
	}

	public String getEquityPercentage() {
		return equityPercentage;
	}

	public void setEquityPercentage(String equityPercentage) {
		this.equityPercentage = equityPercentage;
	}

	public String getRace() {
		return Race;
	}

	public void setRace(String race) {
		Race = race;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public List<Qualification> getQualificationList() {
		return qualificationList;
	}

	public void setQualificationList(List<Qualification> qualificationList) {
		this.qualificationList = qualificationList;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof Owner)
	    {
	    	Owner temp = (Owner) obj;
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

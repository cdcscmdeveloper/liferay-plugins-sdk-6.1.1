package my.ep.web.integration.eperunding;

import java.math.BigDecimal;
import java.util.List;

public class Director {

	private String isBOD;
	private String isCEO;
	private String name;
	private String newICNo;
	private String passport;
	private String isBumi;
	private int citizenOf;
	private String race;
	private String designation;
	private BigDecimal workExperience;

	private List<Qualification> qualificationList;

	public String getIsBOD() {
		return isBOD;
	}

	public void setIsBOD(String isBOD) {
		this.isBOD = isBOD;
	}

	public String getIsCEO() {
		return isCEO;
	}

	public void setIsCEO(String isCEO) {
		this.isCEO = isCEO;
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

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public BigDecimal getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(BigDecimal workExperience) {
		this.workExperience = workExperience;
	}

	public List<Qualification> getQualificationList() {
		return qualificationList;
	}

	public void setQualificationList(List<Qualification> qualificationList) {
		this.qualificationList = qualificationList;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof Director)
	    {
	    	Director temp = (Director) obj;
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

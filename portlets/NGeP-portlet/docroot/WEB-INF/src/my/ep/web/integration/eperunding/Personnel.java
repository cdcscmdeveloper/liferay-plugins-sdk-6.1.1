package my.ep.web.integration.eperunding;

import java.math.BigDecimal;
import java.util.List;

public class Personnel {

	private String isManagement;
	private String isProfessional;
	private String isSemiProf;
	private String isClerical;
	private String name;
	private String newICNo;
	private String passport;
	private String isBumi;
	private int citizenOf;
	private String race;
	private String designation;
	private BigDecimal workExperience;

	private List<Qualification> qualificationList;

	public String getIsManagement() {
		return isManagement;
	}

	public void setIsManagement(String isManagement) {
		this.isManagement = isManagement;
	}

	public String getIsProfessional() {
		return isProfessional;
	}

	public void setIsProfessional(String isProfessional) {
		this.isProfessional = isProfessional;
	}

	public String getIsSemiProf() {
		return isSemiProf;
	}

	public void setIsSemiProf(String isSemiProf) {
		this.isSemiProf = isSemiProf;
	}

	public String getIsClerical() {
		return isClerical;
	}

	public void setIsClerical(String isClerical) {
		this.isClerical = isClerical;
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
	    if(obj instanceof Personnel)
	    {
	    	Personnel temp = (Personnel) obj;
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

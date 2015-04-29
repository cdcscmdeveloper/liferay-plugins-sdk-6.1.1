package my.ep.web.integration.eperunding;

import java.math.BigDecimal;

public class Director {

	private String isBOD;
	private String isCEO;
	private String Name;
	private String newICNo;
	private String passport;
	private String isBumi;
	private int citizenOf;
	private String race;
	private String designation;
	private BigDecimal workExperience;

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
		return Name;
	}

	public void setName(String name) {
		Name = name;
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

}

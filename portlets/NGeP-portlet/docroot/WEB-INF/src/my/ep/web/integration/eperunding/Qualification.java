package my.ep.web.integration.eperunding;

/*
 * @author P Ganga Prasad
 */

public class Qualification {

	private String majorIn;
	private String qualification;
	private int qualificationLevel;
	private String institute;
	private String year;
	private Long educationField;

	public String getMajorIn() {
		return majorIn;
	}

	public void setMajorIn(String majorIn) {
		this.majorIn = majorIn;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public int getQualificationLevel() {
		return qualificationLevel;
	}

	public void setQualificationLevel(int qualificationLevel) {
		this.qualificationLevel = qualificationLevel;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Long getEducationField() {
		return educationField;
	}

	public void setEducationField(Long educationField) {
		this.educationField = educationField;
	}

}

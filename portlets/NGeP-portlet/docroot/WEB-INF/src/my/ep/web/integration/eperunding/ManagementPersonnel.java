package my.ep.web.integration.eperunding;

public class ManagementPersonnel {

	private String MOFNo;

	private Personnel personnel;
	private Expertise expertise;
	private Qualification qualification;
	private ProfBodyDetail profBodyDetail;

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public Personnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	public Expertise getExpertise() {
		return expertise;
	}

	public void setExpertise(Expertise expertise) {
		this.expertise = expertise;
	}

	public Qualification getQualification() {
		return qualification;
	}

	public void setQualification(Qualification qualification) {
		this.qualification = qualification;
	}

	public ProfBodyDetail getProfBodyDetail() {
		return profBodyDetail;
	}

	public void setProfBodyDetail(ProfBodyDetail profBodyDetail) {
		this.profBodyDetail = profBodyDetail;
	}

}

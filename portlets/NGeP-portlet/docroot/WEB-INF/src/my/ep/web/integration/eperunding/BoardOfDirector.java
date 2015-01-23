package my.ep.web.integration.eperunding;


public class BoardOfDirector {

	private String MOFNo;

	private Director director;
	private Expertise expertise;
	private Qualification qualification;
	private ProfBodyDetail profBodyDetail;

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public Director getDirector() {
		return director;
	}

	public void setDirector(Director director) {
		this.director = director;
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
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof BoardOfDirector)
	    {
	    	BoardOfDirector temp = (BoardOfDirector) obj;
	        if(this.MOFNo.equalsIgnoreCase(temp.MOFNo)&& this.director.getIsBumi().equalsIgnoreCase(temp.director.getIsBumi())&& this.director.getName().equalsIgnoreCase(temp.director.getName()))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.MOFNo.hashCode()+this.director.getIsBumi().hashCode()+this.director.getName().hashCode());        
	}

}

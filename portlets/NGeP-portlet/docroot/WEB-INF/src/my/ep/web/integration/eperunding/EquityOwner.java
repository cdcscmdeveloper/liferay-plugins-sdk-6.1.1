package my.ep.web.integration.eperunding;

public class EquityOwner {

	private String MOFNo;

	private Owner owner;
	private Qualification qualification;
	private ProfBodyDetail profBodyDetail;
	private Expertise expertise;

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
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
	    if(obj instanceof EquityOwner)
	    {
	    	EquityOwner temp = (EquityOwner) obj;
	        if(this.MOFNo.equalsIgnoreCase(temp.MOFNo)&& this.owner.getIsBumi().equalsIgnoreCase(temp.owner.getIsBumi())&& this.owner.getName().equalsIgnoreCase(temp.owner.getName()))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.MOFNo.hashCode()+this.owner.getIsBumi().hashCode()+this.owner.getName().hashCode());        
	}

}

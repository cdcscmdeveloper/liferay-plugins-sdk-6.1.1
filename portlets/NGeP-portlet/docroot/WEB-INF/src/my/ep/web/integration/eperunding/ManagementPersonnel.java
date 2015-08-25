package my.ep.web.integration.eperunding;

import java.util.List;

/*
 * @author P Ganga prasad
 */
public class ManagementPersonnel {

	private String MOFNo;

	private Personnel personnel;

	private List<ProfBodyDetail> profBodyDetailList;
	private Expertise expertise;

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public Expertise getExpertise() {
		return expertise;
	}

	public void setExpertise(Expertise expertise) {
		this.expertise = expertise;
	}
	
	public Personnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(Personnel personnel) {
		this.personnel = personnel;
	}

	public List<ProfBodyDetail> getProfBodyDetailList() {
		return profBodyDetailList;
	}

	public void setProfBodyDetailList(List<ProfBodyDetail> profBodyDetailList) {
		this.profBodyDetailList = profBodyDetailList;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof ManagementPersonnel)
	    {
	    	ManagementPersonnel temp = (ManagementPersonnel) obj;
	        if(this.MOFNo.equalsIgnoreCase(temp.MOFNo) && this.personnel.getName().equalsIgnoreCase(temp.personnel.getName()))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.MOFNo.hashCode() + this.personnel.getName().hashCode());        
	}

}

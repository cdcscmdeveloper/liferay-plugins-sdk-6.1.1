package my.ep.web.integration.eperunding;

import java.util.List;

/*
 * @ P Ganga prasad
 */

public class BoardOfDirector {

	private String MOFNo;

	private Director director;
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

	public Director getDirector() {
		return director;
	}

	public void setDirector(Director director) {
		this.director = director;
	}

	public List<ProfBodyDetail> getProfBodyDetailList() {
		return profBodyDetailList;
	}

	public void setProfBodyDetailList(List<ProfBodyDetail> profBodyDetailList) {
		this.profBodyDetailList = profBodyDetailList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoardOfDirector) {
			BoardOfDirector temp = (BoardOfDirector) obj;
			if (this.MOFNo.equalsIgnoreCase(temp.MOFNo) && this.director.getName().equalsIgnoreCase(temp.director.getName()) )
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.MOFNo.hashCode()+director.getName().hashCode());
	}

}

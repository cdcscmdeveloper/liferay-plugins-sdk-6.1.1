package my.ep.web.integration.eperunding;

import java.util.List;

/*
 *@P Ganga Prasad
 */
public class EquityOwner {

	private String MOFNo;

	private Owner owner;

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

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public List<ProfBodyDetail> getProfBodyDetailList() {
		return profBodyDetailList;
	}

	public void setProfBodyDetailList(List<ProfBodyDetail> profBodyDetailList) {
		this.profBodyDetailList = profBodyDetailList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EquityOwner) {
			EquityOwner temp = (EquityOwner) obj;
			if (this.MOFNo.equalsIgnoreCase(temp.MOFNo)
					&& this.owner.getName().equalsIgnoreCase(temp.owner.getName()))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.MOFNo.hashCode() + this.owner.getName().hashCode());
	}

}

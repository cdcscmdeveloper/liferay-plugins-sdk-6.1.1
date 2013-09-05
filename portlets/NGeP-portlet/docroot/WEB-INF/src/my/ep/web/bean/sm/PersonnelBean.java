package my.ep.web.bean.sm;

public class PersonnelBean {

	private int personelSeq;
	private String name;
	private boolean auth;
	private boolean softCertIssued;

	public PersonnelBean() {
	}

	public PersonnelBean(int personelSeq, String name, boolean auth,
			boolean softCertIssued) {
		this.personelSeq = personelSeq;
		this.name = name;
		this.auth = auth;
		this.softCertIssued = softCertIssued;
	}

	public int getPersonelSeq() {
		return personelSeq;
	}

	public void setPersonelSeq(int personelSeq) {
		this.personelSeq = personelSeq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isSoftCertIssued() {
		return softCertIssued;
	}

	public void setSoftCertIssued(boolean softCertIssued) {
		this.softCertIssued = softCertIssued;
	}

}

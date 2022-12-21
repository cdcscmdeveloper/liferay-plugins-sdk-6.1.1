package my.ep.web.managedbean.sc.maintainence;

public class MaintainList {

	boolean panelIsVisible;
	private int mainno;
	private String maintitle;
	
	private int supno;
	private String suptitle;
	private String suptype;
	
	
	public int getMainno() {
		return mainno;
	}
	public void setMainno(int mainno) {
		this.mainno = mainno;
	}
	public String getMaintitle() {
		return maintitle;
	}
	public void setMaintitle(String maintitle) {
		this.maintitle = maintitle;
	}
	
	
	public int getSupno() {
		return supno;
	}
	public void setSupno(int supno) {
		this.supno = supno;
	}
	public String getSuptitle() {
		return suptitle;
	}
	public void setSuptitle(String suptitle) {
		this.suptitle = suptitle;
	}
	public String getSuptype() {
		return suptype;
	}
	public void setSuptype(String suptype) {
		this.suptype = suptype;
	}
	


}

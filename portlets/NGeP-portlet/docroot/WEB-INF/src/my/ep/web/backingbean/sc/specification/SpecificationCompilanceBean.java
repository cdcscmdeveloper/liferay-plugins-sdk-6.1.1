package my.ep.web.backingbean.sc.specification;

public class SpecificationCompilanceBean {
	
	private int compilanceId;
	private String compilanceTitle;
	
	public SpecificationCompilanceBean(){
		
	}
	
	public SpecificationCompilanceBean(int i, String title) {
		// TODO Auto-generated constructor stub
		this.compilanceId=i;
		this.compilanceTitle=title;
	}
	/**
	 * @return the compilanceId
	 */
	public int getCompilanceId() {
		return compilanceId;
	}
	/**
	 * @param compilanceId the compilanceId to set
	 */
	public void setCompilanceId(int compilanceId) {
		this.compilanceId = compilanceId;
	}
	/**
	 * @return the compilanceTitle
	 */
	public String getCompilanceTitle() {
		return compilanceTitle;
	}
	/**
	 * @param compilanceTitle the compilanceTitle to set
	 */
	public void setCompilanceTitle(String compilanceTitle) {
		this.compilanceTitle = compilanceTitle;
	}
	

}

/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum NoticeBoardSection implements IEnumType{

	PUBLISHDETAILS("PD"),
	BRIEFINGDETAILS("BD"),
	SITEVISITDETAILS("SVD");
	
	private String sectionName;
	
	
	private NoticeBoardSection(String section){
		this.sectionName=section;
	}



	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return sectionName;
	}
	
	public static void main(String arg[]){
		System.out.println(NoticeBoardSection.PUBLISHDETAILS.name());
	}
	

}

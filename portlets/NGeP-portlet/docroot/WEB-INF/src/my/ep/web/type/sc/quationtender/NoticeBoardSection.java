/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

// TODO: Auto-generated Javadoc
/**
 * NoticeBoardSection Enum class for Notice board screen.
 *
 * @author VidyasagarReddy
 * @since 22.11.2013
 */
public enum NoticeBoardSection implements IEnumType{

	/** The publishdetails. */
	PUBLISHDETAILS("PD"),
	
	/** The briefingdetails. */
	BRIEFINGDETAILS("BD"),
	
	/** The sitevisitdetails. */
	SITEVISITDETAILS("SVD");
	
	/** The section name. */
	private String sectionName;
	
	/**
	 * Parameterized constructor.
	 *
	 * @param section 			the String variable
	 */
	private NoticeBoardSection(String section){
		this.sectionName=section;
	}
	
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		return sectionName;
	}
	
	

}

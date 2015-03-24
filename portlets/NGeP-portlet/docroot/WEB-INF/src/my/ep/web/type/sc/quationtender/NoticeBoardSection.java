/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * NoticeBoardSection Enum class for Notice board screen
 * 
 * @author VidyasagarReddy
 *
 */
public enum NoticeBoardSection implements IEnumType{

	PUBLISHDETAILS("PD"),
	BRIEFINGDETAILS("BD"),
	SITEVISITDETAILS("SVD");
	
	private String sectionName;
	
	/**
	 * Parameterized constructor
	 * 
	 * @param section
	 * 			the String variable	
	 */
	private NoticeBoardSection(String section){
		this.sectionName=section;
	}
	
	@Override
	public Object value() {
		return sectionName;
	}
	
	

}

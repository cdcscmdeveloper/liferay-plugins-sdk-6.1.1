/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Preference List
 * 
 * @author RAIN POH
 * @since 6/8/2014
 *
 */
public class PreferenceList implements Serializable {

	private static final long serialVersionUID = 1L;
	// user preferences datatables
	private String noticeMode;	
	private String prefered;	
		
	public PreferenceList(){}

	/**
	 * @return the noticeMode
	 */
	public String getNoticeMode() {
		return noticeMode;
	}

	/**
	 * @param noticeMode the noticeMode to set
	 */
	public void setNoticeMode(String noticeMode) {
		this.noticeMode = noticeMode;
	}

	/**
	 * @return the prefered
	 */
	public String getPrefered() {
		return prefered;
	}

	/**
	 * @param prefered the prefered to set
	 */
	public void setPrefered(String prefered) {
		this.prefered = prefered;
	}
}

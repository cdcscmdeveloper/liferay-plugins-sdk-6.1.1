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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// user preferences datatables
	private String noticeMode;
	
	private String prefered;	
		
	
	public String getNoticeMode() {
		return noticeMode;
	}

	public void setNoticeMode(String noticeMode) {
		this.noticeMode = noticeMode;
	}

	
	public String getPrefered() {
		return prefered;
	}

	public void setPrefered(String prefered) {
		this.prefered = prefered;
	}	
	
}

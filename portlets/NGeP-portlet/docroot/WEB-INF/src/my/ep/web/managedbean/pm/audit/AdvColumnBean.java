/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.audit;

import java.io.Serializable;

/**
 * AdvColumnBean
 * 
 * @author venugopal
 * @since 15/08/2014
 * 
 */
public class AdvColumnBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String columnTitle;
	private String columnName;

	public AdvColumnBean() {

	}

	/**
	 * @return the columnTitle
	 */
	public String getColumnTitle() {
		return columnTitle;
	}

	/**
	 * @param columnTitle the columnTitle to set
	 */
	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}

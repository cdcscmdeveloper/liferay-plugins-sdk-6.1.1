/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.loa;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;

public class SuppLoiLoaBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String loiLoaNo;
	private String type;
	private String status;
	private ScLoa scLoa;
	private ScLoi scLoi;
	private List<ScLoiLoaItem> scLoiLoaItems;
	
	public String getLoiLoaNo() {
		return loiLoaNo;
	}
	public void setLoiLoaNo(String loiLoaNo) {
		this.loiLoaNo = loiLoaNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ScLoa getScLoa() {
		return scLoa;
	}
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	public ScLoi getScLoi() {
		return scLoi;
	}
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}
	public List<ScLoiLoaItem> getScLoiLoaItems() {
		return scLoiLoaItems;
	}
	public void setScLoiLoaItems(List<ScLoiLoaItem> scLoiLoaItems) {
		this.scLoiLoaItems = scLoiLoaItems;
	}
	
	

}

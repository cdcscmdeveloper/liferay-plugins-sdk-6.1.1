package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmAppl;

public class BlacklistOwnerDirectorVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7811736505109743175L;
	
	private String icNo; 

	public String getIcNo() {
		return icNo;
	}

	public void setIcNo(String icNo) {
		this.icNo = icNo;
	}
	
	
	
}

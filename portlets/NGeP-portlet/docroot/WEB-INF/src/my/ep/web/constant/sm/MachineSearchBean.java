package my.ep.web.constant.sm;

import java.io.Serializable;

public class MachineSearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ttStatus;
	private String code;
	private String name;
	private Long categoryId;
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getTtStatus() {
		return ttStatus;
	}
	public void setTtStatus(String ttStatus) {
		this.ttStatus = ttStatus;
	}

}

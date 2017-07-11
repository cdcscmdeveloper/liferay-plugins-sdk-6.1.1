package my.ep.web.bean.sm;

import java.io.Serializable;

public class ComplaintSearchBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String searchBy;
	private Long status;
	
	private String keyword;
	
	public String getSearchBy() {
		return searchBy;
	}
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}
	
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	
}

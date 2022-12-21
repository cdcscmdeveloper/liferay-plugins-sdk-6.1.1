/**
 * 
 */
package my.ep.web.vo;

import java.sql.Timestamp;

/**
 * @author siew mei
 *
 */
public class RemarksVO {
	
	private String createdUserId;
	private Timestamp createdDate;
	private String remark;
	
	private Long unspscID;
	private String unspscCode;
	private String unspscTitle;
	
	
	
	/**
	 * @return the createdUserId
	 */
	public String getCreatedUserId() {
		return createdUserId;
	}
	/**
	 * @param createdUserId the createdUserId to set
	 */
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}
	/**
	 * @return the createdDate
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUnspscCode() {
		return unspscCode;
	}
	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}
	public String getUnspscTitle() {
		return unspscTitle;
	}
	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}
	public Long getUnspscID() {
		return unspscID;
	}
	public void setUnspscID(Long unspscID) {
		this.unspscID = unspscID;
	}
	
	
	
	
	
}

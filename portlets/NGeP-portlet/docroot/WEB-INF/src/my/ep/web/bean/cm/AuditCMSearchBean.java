package my.ep.web.bean.cm;

import java.io.Serializable;

/**
 * New file introduced for Audit Trail Codification Management (#28895 - #35503)
 * Search Bean for CM audit report/view
 * 
 * @author irfan
 * @since 01/11/2024
 */
public class AuditCMSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String language;
	private String segmentCode;
	private String familyCode;
	private String classCode;
	private String commodityCode;
	private int groupSection;
	private boolean isSearching = true;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public int getGroupSection() {
		return groupSection;
	}

	public void setGroupSection(int groupSection) {
		this.groupSection = groupSection;
	}

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}

}

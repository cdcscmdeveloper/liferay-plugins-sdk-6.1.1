/**
 * 
 */
package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;

/**
 * @author se7en
 * 
 */
public class BriefingZoneBean {

	private String zoneName;
	private List<ScQtBriefing> briefingList;

	
	/**
	 * @param zoneName
	 * @param briefingList
	 */
	public BriefingZoneBean(String zoneName, List<ScQtBriefing> briefingList) {
		this.zoneName = zoneName;
		this.briefingList = briefingList;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName
	 *            the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the briefingList
	 */
	public List<ScQtBriefing> getBriefingList() {
		return briefingList;
	}

	/**
	 * @param briefingList
	 *            the briefingList to set
	 */
	public void setBriefingList(List<ScQtBriefing> briefingList) {
		this.briefingList = briefingList;
	}

}

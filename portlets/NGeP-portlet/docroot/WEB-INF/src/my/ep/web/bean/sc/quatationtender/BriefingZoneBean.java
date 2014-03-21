/**
 * 
 */
package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;

/**
 * @author se7en
 * 
 */
public class BriefingZoneBean {

	private String zoneName;
	private List<ScQtBriefing> briefingList;
	private List<ScQtDeliveryLoc> deliveryLocList;
	private List<ScQtDeliveryLoc> selectDeliveryList;
	public int id;

	
	/**
	 * @param zoneName
	 * @param briefingList
	 */
	public BriefingZoneBean(String zoneName, List<ScQtBriefing> briefingList) {
		this.zoneName = zoneName;
		this.briefingList = briefingList;
	}

	public BriefingZoneBean(int id,String zoneName, List<ScQtDeliveryLoc> deliveryLocList, List<ScQtDeliveryLoc> selectDeliveryList) {
		this.id=id;
		this.zoneName = zoneName;
		this.deliveryLocList = deliveryLocList;
		this.selectDeliveryList=selectDeliveryList;
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

	/**
	 * @return the deliveryLocList
	 */
	public List<ScQtDeliveryLoc> getDeliveryLocList() {
		return deliveryLocList;
	}

	/**
	 * @param deliveryLocList the deliveryLocList to set
	 */
	public void setDeliveryLocList(List<ScQtDeliveryLoc> deliveryLocList) {
		this.deliveryLocList = deliveryLocList;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the selectDeliveryList
	 */
	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	/**
	 * @param selectDeliveryList the selectDeliveryList to set
	 */
	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	

}

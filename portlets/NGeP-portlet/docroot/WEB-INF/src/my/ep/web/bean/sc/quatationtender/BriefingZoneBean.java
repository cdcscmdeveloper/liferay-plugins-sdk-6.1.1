package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

/**
 * @author Viswanath
 * 
 */
public class BriefingZoneBean {

	private String zoneName;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private List<ScQtBriefing> briefingList;
	private List<ScQtBriefing> sitevisitList;
	private List<ScQtDeliveryLoc> deliveryLocList;
	private List<ScQtDeliveryLoc> selectDeliveryList;
	public int id;

	/**
	 * @param zoneName
	 * @param briefingList
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtBriefing> briefingList, List<ScQtBriefing> sitevisitList) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.briefingList = briefingList;
		this.sitevisitList = sitevisitList;
	}

	public BriefingZoneBean(int id, String zoneName,
			List<ScQtDeliveryLoc> deliveryLocList,
			List<ScQtDeliveryLoc> selectDeliveryList) {
		this.id = id;
		this.zoneName = zoneName;
		this.deliveryLocList = deliveryLocList;
		this.selectDeliveryList = selectDeliveryList;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public List<ScQtBriefing> getBriefingList() {
		return briefingList;
	}

	public void setBriefingList(List<ScQtBriefing> briefingList) {
		this.briefingList = briefingList;
	}

	public List<ScQtDeliveryLoc> getDeliveryLocList() {
		return deliveryLocList;
	}

	public void setDeliveryLocList(List<ScQtDeliveryLoc> deliveryLocList) {
		this.deliveryLocList = deliveryLocList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	public List<ScQtBriefing> getSitevisitList() {
		return sitevisitList;
	}

	public void setSitevisitList(List<ScQtBriefing> sitevisitList) {
		this.sitevisitList = sitevisitList;
	}

	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

}

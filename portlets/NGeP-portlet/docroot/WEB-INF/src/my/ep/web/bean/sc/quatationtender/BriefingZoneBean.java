package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

/**
 * @author Viswanath
 * 
 */
public class BriefingZoneBean {

	private String zoneName;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private List<ScQtBsvDtl> briefingList;
	private List<ScQtBsvDtl> sitevisitList;
	private List<ScQtDeliveryLoc> deliveryLocList;
	private List<ScQtDeliveryLoc> selectDeliveryList;
	public int id;

	/**
	 * @param zoneName
	 * @param briefingList
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtBsvDtl> briefingList, List<ScQtBsvDtl> sitevisitList) {
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

	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	public List<ScQtBsvDtl> getBriefingList() {
		return briefingList;
	}

	public void setBriefingList(List<ScQtBsvDtl> briefingList) {
		this.briefingList = briefingList;
	}

	public List<ScQtBsvDtl> getSitevisitList() {
		return sitevisitList;
	}

	public void setSitevisitList(List<ScQtBsvDtl> sitevisitList) {
		this.sitevisitList = sitevisitList;
	}

}

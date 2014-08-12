package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

/**
 * @author Viswanath
 * 
 */
public class BriefingZoneBean implements Serializable {

	private String zoneName;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private List<ScQtBsvDtl> briefingList;
	private List<ScQtBsvDtl> sitevisitList;
	private List<ScQtDeliveryLoc> deliveryLocList;
	private List<ScQtDeliveryLoc> selectDeliveryList;
	public int id;
	private ScQtBsv scQtBsv;
	private List<ScQtBsvDtl> selectedBriefingDtls;
	private List<ScQtBsvDtl> selectedSiteVisitDtls;

	/**
	 * @param zoneName
	 * @param briefingList
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtBsvDtl> briefingList, List<ScQtBsvDtl> sitevisitList,
			ScQtBsv scQtBsv) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.briefingList = briefingList;
		this.sitevisitList = sitevisitList;
		this.scQtBsv = scQtBsv;
		this.selectedBriefingDtls=new ArrayList<>();
		this.selectedSiteVisitDtls=new ArrayList<>();
	}

	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtDeliveryLoc> deliveryLocList) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.deliveryLocList = deliveryLocList;

	}

	public BriefingZoneBean(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
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

	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}

	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	public List<ScQtBsvDtl> getSelectedBriefingDtls() {
		return selectedBriefingDtls;
	}

	public void setSelectedBriefingDtls(List<ScQtBsvDtl> selectedBriefingDtls) {
		this.selectedBriefingDtls = selectedBriefingDtls;
	}

	public List<ScQtBsvDtl> getSelectedSiteVisitDtls() {
		return selectedSiteVisitDtls;
	}

	public void setSelectedSiteVisitDtls(List<ScQtBsvDtl> selectedSiteVisitDtls) {
		this.selectedSiteVisitDtls = selectedSiteVisitDtls;
	}

}

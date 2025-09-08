/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.CustomUtils;	// Bug #36702@Syazani 28/11/2024

/**
 * The Class BriefingZoneBean.
 *
 * @author Viswanath
 */
public class BriefingZoneBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5155820654362481606L;
	
	/** The zone name. */
	private String zoneName;
	
	/** The tpl spec zone dtl. */
	private TplSpecZoneDtl tplSpecZoneDtl;
	
	/** The briefing list. */
	private List<ScQtBsvDtl> briefingList;
	
	/** The sitevisit list. */
	private List<ScQtBsvDtl> sitevisitList;
	
	/** The delivery loc list. */
	private List<ScQtDeliveryLoc> deliveryLocList;
	
	/** The select delivery list. */
	private List<ScQtDeliveryLoc> selectDeliveryList;
	
	/** The id. */
	public int id;
	
	/** The sc qt bsv. */
	private ScQtBsv scQtBsv;
	
	/** The selected briefing dtls. */
	private List<ScQtBsvDtl> selectedBriefingDtls;
	
	/** The selected site visit dtls. */
	private List<ScQtBsvDtl> selectedSiteVisitDtls;
	
	/** The total suppliers attended in current zone **/
	private int totalSuppliersAttended;	

	private ScQtSampleSubmission sampleAddress;
	
	
	//11104-start
	private Long stateId;
	private Long districtid;
	private Long divisionId;
	private Long cityId;//11104-End
	
	// #28777 & #29729 - Start
	@Transient
	private boolean branchDeactivated;

	// #28777 & #29729 - End

	/**
	 * Instantiates a new briefing zone bean.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @param briefingList the briefing list
	 * @param sitevisitList the sitevisit list
	 * @param scQtBsv the sc qt bsv
	 * @param ScQtSampleSubmission the sampleAddress
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtBsvDtl> briefingList, List<ScQtBsvDtl> sitevisitList,
			ScQtBsv scQtBsv,ScQtSampleSubmission sampleAddress) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.briefingList = briefingList;
		this.sitevisitList = sitevisitList;
		this.scQtBsv = scQtBsv;
		this.selectedBriefingDtls=new ArrayList<>();
		this.selectedSiteVisitDtls=new ArrayList<>();
		this.sampleAddress = sampleAddress;
	}
	
	/**
	 * Instantiates a new briefing zone bean.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @param briefingList the briefing list
	 * @param sitevisitList the sitevisit list
	 * @param scQtBsv the sc qt bsv
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtBsvDtl> briefingList, List<ScQtBsvDtl> sitevisitList,
			ScQtBsv scQtBsv ) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.briefingList = briefingList;
		this.sitevisitList = sitevisitList;
		this.scQtBsv = scQtBsv;
		this.selectedBriefingDtls=new ArrayList<>();
		this.selectedSiteVisitDtls=new ArrayList<>();
	}

	/**
	 * Instantiates a new briefing zone bean.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @param deliveryLocList the delivery loc list
	 */
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtDeliveryLoc> deliveryLocList) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.deliveryLocList = deliveryLocList;

	}
	
	// Bug #36702@Syazani 28/11/2024 -- Start
	/**
	 * Instantiates a new Copy of briefing zone bean. 
	 *
	 * @param source the original BriefingZoneBean object
	 */
	public BriefingZoneBean(BriefingZoneBean source) {
		if (source == null) {
			throw new IllegalArgumentException("Source object cannot be null");
		}
		this.briefingList = CustomUtils.getInstance().deepCopyArrayList(source.briefingList);
		this.sitevisitList = CustomUtils.getInstance().deepCopyArrayList(source.sitevisitList);
		this.deliveryLocList = CustomUtils.getInstance().deepCopyArrayList(source.deliveryLocList);
		this.selectDeliveryList = CustomUtils.getInstance().deepCopyArrayList(source.selectDeliveryList);
		this.selectedBriefingDtls = CustomUtils.getInstance().deepCopyArrayList(source.selectedBriefingDtls);
		this.selectedSiteVisitDtls = CustomUtils.getInstance().deepCopyArrayList(source.selectedSiteVisitDtls);

		this.tplSpecZoneDtl = (TplSpecZoneDtl) (source.tplSpecZoneDtl != null ? source.tplSpecZoneDtl.cloneEntity()
				: null);
		this.scQtBsv = (ScQtBsv) (source.scQtBsv != null ? source.scQtBsv.cloneEntity() : null);
		this.sampleAddress = (ScQtSampleSubmission) (source.sampleAddress != null ? source.sampleAddress.cloneEntity()
				: null);
	}
	// Bug #36702@Syazani 28/11/2024 -- End

	/**
	 * Instantiates a new briefing zone bean.
	 *
	 * @param scQtBsv the sc qt bsv
	 */
	public BriefingZoneBean(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	/**
	 * Gets the zone name.
	 *
	 * @return the zone name
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * Sets the zone name.
	 *
	 * @param zoneName the new zone name
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Gets the delivery loc list.
	 *
	 * @return the delivery loc list
	 */
	public List<ScQtDeliveryLoc> getDeliveryLocList() {
		return deliveryLocList;
	}

	/**
	 * Sets the delivery loc list.
	 *
	 * @param deliveryLocList the new delivery loc list
	 */
	public void setDeliveryLocList(List<ScQtDeliveryLoc> deliveryLocList) {
		this.deliveryLocList = deliveryLocList;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the select delivery list.
	 *
	 * @return the select delivery list
	 */
	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	/**
	 * Sets the select delivery list.
	 *
	 * @param selectDeliveryList the new select delivery list
	 */
	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	/**
	 * Gets the tpl spec zone dtl.
	 *
	 * @return the tpl spec zone dtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * Sets the tpl spec zone dtl.
	 *
	 * @param tplSpecZoneDtl the new tpl spec zone dtl
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * Gets the briefing list.
	 *
	 * @return the briefing list
	 */
	public List<ScQtBsvDtl> getBriefingList() {
		return briefingList;
	}

	/**
	 * Sets the briefing list.
	 *
	 * @param briefingList the new briefing list
	 */
	public void setBriefingList(List<ScQtBsvDtl> briefingList) {
		this.briefingList = briefingList;
	}

	/**
	 * Gets the sitevisit list.
	 *
	 * @return the sitevisit list
	 */
	public List<ScQtBsvDtl> getSitevisitList() {
		return sitevisitList;
	}

	/**
	 * Sets the sitevisit list.
	 *
	 * @param sitevisitList the new sitevisit list
	 */
	public void setSitevisitList(List<ScQtBsvDtl> sitevisitList) {
		this.sitevisitList = sitevisitList;
	}

	/**
	 * Gets the sc qt bsv.
	 *
	 * @return the sc qt bsv
	 */
	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}

	/**
	 * Sets the sc qt bsv.
	 *
	 * @param scQtBsv the new sc qt bsv
	 */
	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	/**
	 * Gets the selected briefing dtls.
	 *
	 * @return the selected briefing dtls
	 */
	public List<ScQtBsvDtl> getSelectedBriefingDtls() {
		return selectedBriefingDtls;
	}

	/**
	 * Sets the selected briefing dtls.
	 *
	 * @param selectedBriefingDtls the new selected briefing dtls
	 */
	public void setSelectedBriefingDtls(List<ScQtBsvDtl> selectedBriefingDtls) {
		this.selectedBriefingDtls = selectedBriefingDtls;
	}

	/**
	 * Gets the selected site visit dtls.
	 *
	 * @return the selected site visit dtls
	 */
	public List<ScQtBsvDtl> getSelectedSiteVisitDtls() {
		return selectedSiteVisitDtls;
	}

	/**
	 * Sets the selected site visit dtls.
	 *
	 * @param selectedSiteVisitDtls the new selected site visit dtls
	 */
	public void setSelectedSiteVisitDtls(List<ScQtBsvDtl> selectedSiteVisitDtls) {
		this.selectedSiteVisitDtls = selectedSiteVisitDtls;
	}

	/**
	 * @return the totalSuppliersAttended
	 */
	public int getTotalSuppliersAttended() {
		return totalSuppliersAttended;
	}

	/**
	 * @param totalSuppliersAttended 
	 * 				the totalSuppliersAttended to set
	 */
	public void setTotalSuppliersAttended(int totalSuppliersAttended) {
		this.totalSuppliersAttended = totalSuppliersAttended;
	}

	/**
	 * @return the sampleAddress
	 */
	public ScQtSampleSubmission getSampleAddress() {
		return sampleAddress;
	}

	/**
	 * @param sampleAddress the sampleAddress to set
	 */
	public void setSampleAddress(ScQtSampleSubmission sampleAddress) {
		this.sampleAddress = sampleAddress;
	}

	
//11104-start
	public BriefingZoneBean(TplSpecZoneDtl tplSpecZoneDtl,Long stateId,Long divisionId,Long districtId,Long cityId){
		this.tplSpecZoneDtl=tplSpecZoneDtl;
		this.stateId=stateId;
		this.divisionId=divisionId;
		this.districtid=districtId;
		this.cityId=cityId;
	}

	/**
	 * @return the stateId
	 */
	public Long getStateId() {
		return stateId;
	}

	/**
	 * @param stateId the stateId to set
	 */
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	/**
	 * @return the districtid
	 */
	public Long getDistrictid() {
		return districtid;
	}

	/**
	 * @param districtid the districtid to set
	 */
	public void setDistrictid(Long districtid) {
		this.districtid = districtid;
	}

	/**
	 * @return the divisionId
	 */
	public Long getDivisionId() {
		return divisionId;
	}

	/**
	 * @param divisionId the divisionId to set
	 */
	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 * @return the cityId
	 */
	public Long getCityId() {
		return cityId;
	}

	/**
	 * @param cityId the cityId to set
	 */
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	//11104-End

	// #28777 & #29729 - Start
	public boolean isBranchDeactivated() {
		return branchDeactivated;
	}

	public void setBranchDeactivated(boolean branchDeactivated) {
		this.branchDeactivated = branchDeactivated;
	}
	// #28777 & #29729 - End
}

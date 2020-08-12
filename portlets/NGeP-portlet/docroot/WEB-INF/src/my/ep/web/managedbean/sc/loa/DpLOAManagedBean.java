/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.loa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class DpLOAManagedBean.
 *
 * @author kenny
 * @since 27.05.2014
 */
@ManagedBean(name = "dpLOAManagedBean")
@SessionScoped
public class DpLOAManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The sc request note. */
	private ScRequestNote scRequestNote;
	
	/** The purchase request list. */
	private List<ScPurchaseRequest> purchaseRequestList;
	
	/** The show bpm buttons. */
	private Boolean showBPMButtons;
	
	/** The source id. */
	private Long sourceId;
	
	/**
	 * Instantiates a new dp loa managed bean.
	 */
	public DpLOAManagedBean() {
	}

	/**
	 * Gets the sc request note.
	 *
	 * @return the scRequestNote
	 */
	public ScRequestNote getScRequestNote() {
		return scRequestNote;
	}

	/**
	 * Sets the sc request note.
	 *
	 * @param scRequestNote the scRequestNote to set
	 */
	public void setScRequestNote(ScRequestNote scRequestNote) {
		this.scRequestNote = scRequestNote;
	}

	/**
	 * Gets the purchase request list.
	 *
	 * @return the purchaseRequestList
	 */
	public List<ScPurchaseRequest> getPurchaseRequestList() {
		return purchaseRequestList;
	}

	/**
	 * Sets the purchase request list.
	 *
	 * @param purchaseRequestList the purchaseRequestList to set
	 */
	public void setPurchaseRequestList(List<ScPurchaseRequest> purchaseRequestList) {
		this.purchaseRequestList = purchaseRequestList;
	}

	/**
	 * Gets the show bpm buttons.
	 *
	 * @return the show bpm buttons
	 */
	public Boolean getShowBPMButtons() {
		return showBPMButtons;
	}

	/**
	 * Sets the show bpm buttons.
	 *
	 * @param showBPMButtons the new show bpm buttons
	 */
	public void setShowBPMButtons(Boolean showBPMButtons) {
		this.showBPMButtons = showBPMButtons;
	}

	/**
	 * Gets the source id.
	 *
	 * @return the source id
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * Sets the source id.
	 *
	 * @param sourceId the new source id
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

}

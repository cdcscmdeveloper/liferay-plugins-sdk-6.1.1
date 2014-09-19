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

/**
 * @author kenny
 * @since 27.05.2014
 * 
 */
@ManagedBean(name = "dpLOAManagedBean")
@SessionScoped
public class DpLOAManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private ScRequestNote scRequestNote;
	private List<ScPurchaseRequest> purchaseRequestList;
	private Boolean showBPMButtons;
	
	public DpLOAManagedBean() {
	}

	/**
	 * @return the scRequestNote
	 */
	public ScRequestNote getScRequestNote() {
		return scRequestNote;
	}

	/**
	 * @param scRequestNote the scRequestNote to set
	 */
	public void setScRequestNote(ScRequestNote scRequestNote) {
		this.scRequestNote = scRequestNote;
	}

	/**
	 * @return the purchaseRequestList
	 */
	public List<ScPurchaseRequest> getPurchaseRequestList() {
		return purchaseRequestList;
	}

	/**
	 * @param purchaseRequestList the purchaseRequestList to set
	 */
	public void setPurchaseRequestList(List<ScPurchaseRequest> purchaseRequestList) {
		this.purchaseRequestList = purchaseRequestList;
	}

	public Boolean getShowBPMButtons() {
		return showBPMButtons;
	}

	public void setShowBPMButtons(Boolean showBPMButtons) {
		this.showBPMButtons = showBPMButtons;
	}

}

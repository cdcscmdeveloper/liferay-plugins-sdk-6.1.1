/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierinvitation;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * Class to handle user session data corresponding to SupplierInvitationBackingBean
 * @author Hemanth Katta
 *
 */

@ManagedBean
@SessionScoped
public class SCSupplierInvitationManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String selectedInvitationType;
	private Map<Long,String> urlMap;

	/**
	 * @return the selectedInvitationType
	 */
	public String getSelectedInvitationType() {
		return selectedInvitationType;
	}

	/**
	 * @param selectedInvitationType the selectedInvitationType to set
	 */
	public void setSelectedInvitationType(String selectedInvitationType) {
		this.selectedInvitationType = selectedInvitationType;
	}
	
	/**
	 * @return the urlMap
	 */
	public Map<Long, String> getUrlMap() {
		return urlMap;
	}

	/**
	 * @param urlMap the urlMap to set
	 */
	public void setUrlMap(Map<Long, String> urlMap) {
		this.urlMap = urlMap;
	}
	
}

/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierinvitation;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Hemanth Katta
 *
 */

@ManagedBean
@SessionScoped
public class SCSupplierInvitationManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private Long selectedInvitationType;
	private Map<Long,String> invitationTypeMap;
	private Map<Long,String> urlMap;

	/**
	 * @return the selectedInvitationType
	 */
	public Long getSelectedInvitationType() {
		return selectedInvitationType;
	}

	/**
	 * @param selectedInvitationType the selectedInvitationType to set
	 */
	public void setSelectedInvitationType(Long selectedInvitationType) {
		this.selectedInvitationType = selectedInvitationType;
	}
	
	/**
	 * @return the invitationTypeMap
	 */
	public Map<Long, String> getInvitationTypeMap() {
		return invitationTypeMap;
	}

	/**
	 * @param invitationTypeMap the invitationTypeMap to set
	 */
	public void setInvitationTypeMap(Map<Long, String> invitationTypeMap) {
		this.invitationTypeMap = invitationTypeMap;
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

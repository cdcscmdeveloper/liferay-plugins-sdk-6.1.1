/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.createcontract;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */
@ManagedBean
@SessionScoped
public class FulfilmentRequestManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(FulfilmentRequestManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private List<FlRemark> allremarksList;
	private List<FlAttachment> allAttachmentsList;

	/**
	 * @return the allAttachmentsList
	 */
	public List<FlAttachment> getAllAttachmentsList() {
		return allAttachmentsList;
	}

	/**
	 * @param allAttachmentsList the allAttachmentsList to set
	 */
	public void setAllAttachmentsList(List<FlAttachment> allAttachmentsList) {
		this.allAttachmentsList = allAttachmentsList;
	}

	/**
	 * @return the flRemark
	 */
	public FlRemark getFlRemark() {
		return flRemark;
	}

	/**
	 * @param flRemark
	 *            the flRemark to set
	 */
	public void setFlRemark(FlRemark flRemark) {
		this.flRemark = flRemark;
	}

	/**
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * @param flAttachment
	 *            the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	/**
	 * @return the allremarksList
	 */
	public List<FlRemark> getAllremarksList() {
		return allremarksList;
	}

	/**
	 * @param allremarksList the allremarksList to set
	 */
	public void setAllremarksList(List<FlRemark> allremarksList) {
		this.allremarksList = allremarksList;
	}

}

/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.pr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean
@SessionScoped
public class PurchaseRequestManagedBean extends BaseManagedBean {
	private static Logger log = Logger
			.getLogger(PurchaseRequestManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private FlFulfilmentRequest fulfilmentRequest;

	private List<DeliveryAddressPopup> daPopupList;
	//private List<DeliveryAddressPopup> selectedPopupAddress;
	
	private FlDeliveryAddress selectedAddrRow;
	private List<PmOrgAddress> selectedPmDelAddrList;
	private List<PmOrgAddress> delAddrList;
	private FlDeliveryAddress selectedDelAddr;
	

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
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}
	

	/**
	 * @return the daPopupList
	 */
	public List<DeliveryAddressPopup> getDaPopupList() {
		return daPopupList;
	}

	/**
	 * @param daPopupList
	 *            the daPopupList to set
	 */
	public void setDaPopupList(List<DeliveryAddressPopup> daPopupList) {
		this.daPopupList = daPopupList;
	}

	public PurchaseRequestManagedBean() {
		daPopupList = new ArrayList<DeliveryAddressPopup>();
		daPopupList.add(new DeliveryAddressPopup("Delivery Address1",
				"12/02/2012", "12/02/2013"));
		daPopupList.add(new DeliveryAddressPopup("Delivery Address2",
				"11/02/2012", "11/02/2013"));
		daPopupList.add(new DeliveryAddressPopup("Delivery Address3",
				"10/02/2012", "10/02/2013"));
	}

	public class DeliveryAddressPopup {
		private int seqNo;
		public String deliveryAddress;
		public String endDate;
		public String startDate;

		public DeliveryAddressPopup(String deliveryAddress, String endDate,
				String startDate) {
			// TODO Auto-generated constructor stub
			this.deliveryAddress = deliveryAddress;
			this.endDate = endDate;
			this.startDate = startDate;
		}

		/**
		 * @return the deliveryAddress
		 */
		public String getDeliveryAddress() {
			return deliveryAddress;
		}

		/**
		 * @param deliveryAddress
		 *            the deliveryAddress to set
		 */
		public void setDeliveryAddress(String deliveryAddress) {
			this.deliveryAddress = deliveryAddress;
		}

		/**
		 * @return the endDate
		 */
		public String getEndDate() {
			return endDate;
		}

		/**
		 * @param endDate
		 *            the endDate to set
		 */
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		/**
		 * @return the startDate
		 */
		public String getStartDate() {
			return startDate;
		}

		/**
		 * @param startDate
		 *            the startDate to set
		 */
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		/**
		 * @return the seqNo
		 */
		public int getSeqNo() {
			return seqNo;
		}

		/**
		 * @param seqNo
		 *            the seqNo to set
		 */
		public void setSeqNo(int seqNo) {
			this.seqNo = seqNo;
		}

	}

	/**
	 * @return the selectedAddrRow
	 */
	public FlDeliveryAddress getSelectedAddrRow() {
		return selectedAddrRow;
	}

	/**
	 * @param selectedAddrRow the selectedAddrRow to set
	 */
	public void setSelectedAddrRow(FlDeliveryAddress selectedAddrRow) {
		this.selectedAddrRow = selectedAddrRow;
	}

	/**
	 * @return the selectedPmDelAddrList
	 */
	public List<PmOrgAddress> getSelectedPmDelAddrList() {
		return selectedPmDelAddrList;
	}

	/**
	 * @param selectedPmDelAddrList the selectedPmDelAddrList to set
	 */
	public void setSelectedPmDelAddrList(List<PmOrgAddress> selectedPmDelAddrList) {
		this.selectedPmDelAddrList = selectedPmDelAddrList;
	}

	/**
	 * @return the delAddrList
	 */
	public List<PmOrgAddress> getDelAddrList() {
		return delAddrList;
	}

	/**
	 * @param delAddrList the delAddrList to set
	 */
	public void setDelAddrList(List<PmOrgAddress> delAddrList) {
		this.delAddrList = delAddrList;
	}

	/**
	 * @return the selectedDelAddr
	 */
	public FlDeliveryAddress getSelectedDelAddr() {
		return selectedDelAddr;
	}

	/**
	 * @param selectedDelAddr the selectedDelAddr to set
	 */
	public void setSelectedDelAddr(FlDeliveryAddress selectedDelAddr) {
		this.selectedDelAddr = selectedDelAddr;
	}

}

/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.amendment;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 1/13/2014	 
 */
@ManagedBean(name ="amendmentManagedBean")
@SessionScoped
public class AmendmentManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private String[] action;
	private String processName;
	
	private boolean amendAckw;
	private boolean amendDisable;
	private boolean approvrLetter;
	
	private CtContract ctContract;
	private CtContractVer contractVer;
	
	private String itemType;
	private boolean serviceItem;
	private boolean productAndService;
	private boolean productItem = true;
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private String showItem ="Item  Pricing";
	private List<CtItemPrice> itemPriceList;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private double secondValue =0.0;
	
	public CtContractVer getContractVer() {
		return contractVer;
	}

	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public boolean isAmendDisable() {
		return amendDisable;
	}

	public void setAmendDisable(boolean amendDisable) {
		this.amendDisable = amendDisable;
	}

	public boolean isAmendAckw() {
		return amendAckw;
	}

	public void setAmendAckw(boolean amendAckw) {
		this.amendAckw = amendAckw;
	}

	public boolean isApprovrLetter() {
		return approvrLetter;
	}

	public void setApprovrLetter(boolean approvrLetter) {
		this.approvrLetter = approvrLetter;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null){
			attachmentList = new ArrayList<CtAttachment>();		
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean isServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(boolean serviceItem) {
		this.serviceItem = serviceItem;
	}

	public boolean isProductItem() {
		return productItem;
	}

	public void setProductItem(boolean productItem) {
		this.productItem = productItem;
	}

	public boolean isProductAndService() {
		return productAndService;
	}

	public void setProductAndService(boolean productAndService) {
		this.productAndService = productAndService;
	}

	public String getShowItem() {
		return showItem;
	}

	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}

	public boolean isItemPricing() {
		return itemPricing;
	}

	public void setItemPricing(boolean itemPricing) {
		this.itemPricing = itemPricing;
	}

	public boolean isItemCeilingQuantity() {
		return itemCeilingQuantity;
	}

	public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
		this.itemCeilingQuantity = itemCeilingQuantity;
	}

	public List<CtItemPrice> getItemPriceList() {
		if(itemPriceList == null)
			itemPriceList = new ArrayList<CtItemPrice>();
		return itemPriceList;
	}

	public void setItemPriceList(List<CtItemPrice> itemPriceList) {
		this.itemPriceList = itemPriceList;
	}

	public double getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(double secondValue) {
		this.secondValue = secondValue;
	}

}

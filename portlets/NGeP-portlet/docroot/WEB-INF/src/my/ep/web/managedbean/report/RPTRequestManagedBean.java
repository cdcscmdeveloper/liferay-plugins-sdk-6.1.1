/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.report;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.persistence.entity.rpt.RPTRequest;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Report list.
 * 
 * @author Jimmy
 * @since 14/09/2016
 */
@ManagedBean(name = "rptRequestManagedBean")
@SessionScoped
public class RPTRequestManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(RPTRequestManagedBean.class);

	private String loginId;
	private boolean isEditable = true;
	private List<RPTRequest> rptRequestLists;
	private RPTRequest[] selectedRPTRequests;
	private RPTRequest selectedRPTRequest;
	private RPTRequest rptRequest;
	private boolean isInit = true;
	private boolean isUnRead = false;
	private boolean deleteButtonHide;

	private String searchItem;
	private String searchValue;

	public RPTRequestManagedBean() {
		log.info("RPTRequestManagedBean inside ");
	}

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return the isEditable
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * @param isEditable the isEditable to set
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	/**
	 * @return the rptRequestLists
	 */
	public List<RPTRequest> getRptRequestLists() {
		return rptRequestLists;
	}

	/**
	 * @param rptRequestLists the rptRequestLists to set
	 */
	public void setRptRequestLists(List<RPTRequest> rptRequestLists) {
		this.rptRequestLists = rptRequestLists;
	}

	/**
	 * @return the selectedRPTRequests
	 */
	public RPTRequest[] getSelectedRPTRequests() {
		return selectedRPTRequests;
	}

	/**
	 * @param selectedRPTRequests the selectedRPTRequests to set
	 */
	public void setSelectedRPTRequests(RPTRequest[] selectedRPTRequests) {
		this.selectedRPTRequests = selectedRPTRequests;
	}

	/**
	 * @return the selectedRPTRequest
	 */
	public RPTRequest getSelectedRPTRequest() {
		return selectedRPTRequest;
	}

	/**
	 * @param selectedRPTRequest the selectedRPTRequest to set
	 */
	public void setSelectedRPTRequest(RPTRequest selectedRPTRequest) {
		this.selectedRPTRequest = selectedRPTRequest;
	}

	/**
	 * @return the rptRequest
	 */
	public RPTRequest getRptRequest() {
		return rptRequest;
	}

	/**
	 * @param rptRequest the rptRequest to set
	 */
	public void setRptRequest(RPTRequest rptRequest) {
		this.rptRequest = rptRequest;
	}

	/**
	 * @return the isInit
	 */
	public boolean isInit() {
		return isInit;
	}

	/**
	 * @param isInit the isInit to set
	 */
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	/**
	 * @return the isUnRead
	 */
	public boolean isUnRead() {
		return isUnRead;
	}

	/**
	 * @param isUnRead the isUnRead to set
	 */
	public void setUnRead(boolean isUnRead) {
		this.isUnRead = isUnRead;
	}

	/**
	 * @return the deleteButtonHide
	 */
	public boolean isDeleteButtonHide() {
		return deleteButtonHide;
	}

	/**
	 * @param deleteButtonHide the deleteButtonHide to set
	 */
	public void setDeleteButtonHide(boolean deleteButtonHide) {
		this.deleteButtonHide = deleteButtonHide;
	}

	/**
	 * @return the searchItem
	 */
	public String getSearchItem() {
		return searchItem;
	}

	/**
	 * @param searchItem the searchItem to set
	 */
	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	/**
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
}

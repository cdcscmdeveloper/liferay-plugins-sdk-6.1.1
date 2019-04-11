/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.notify;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Notification.
 * 
 * @author MD
 * @since 24/04/2014
 */
@ManagedBean(name = "notifyManagedBean")
@SessionScoped
public class NotifyManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(NotifyManagedBean.class);

	private String loginId;
	private boolean isEditable = true;
	private List<PmNotifyUser> notifyLists;
	private PmNotifyUser[] selectedNotifys;
	private PmNotifyUser selectedNotify;
	private PmNotifyUser notify;
	private boolean isInit = true;
	private boolean isUnRead = false;
	private boolean deleteButtonHide;

	private String searchItem;
	private String searchValue;

	public NotifyManagedBean() {
		log.info("NotifyManagedBean inside ");
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
	 * @return the notifyLists
	 */
	public List<PmNotifyUser> getNotifyLists() {
		return notifyLists;
	}


	/**
	 * @param notifyLists the notifyLists to set
	 */
	public void setNotifyLists(List<PmNotifyUser> notifyLists) {
		this.notifyLists = notifyLists;
	}


	/**
	 * @return the selectedNotifys
	 */
	public PmNotifyUser[] getSelectedNotifys() {
		return selectedNotifys;
	}


	/**
	 * @param selectedNotifys the selectedNotifys to set
	 */
	public void setSelectedNotifys(PmNotifyUser[] selectedNotifys) {
		this.selectedNotifys = selectedNotifys;
	}


	/**
	 * @return the selectedNotify
	 */
	public PmNotifyUser getSelectedNotify() {
		return selectedNotify;
	}


	/**
	 * @param selectedNotify the selectedNotify to set
	 */
	public void setSelectedNotify(PmNotifyUser selectedNotify) {
		this.selectedNotify = selectedNotify;
	}


	/**
	 * @return the notify
	 */
	public PmNotifyUser getNotify() {
		return notify;
	}


	/**
	 * @param notify the notify to set
	 */
	public void setNotify(PmNotifyUser notify) {
		this.notify = notify;
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
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @return the searchItem
	 */
	public String getSearchItem() {
		return searchItem;
	}

	/**
	 * @param searchItem
	 *            the searchItem to set
	 */
	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	/**
	 * @param searchValue
	 *            the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
}

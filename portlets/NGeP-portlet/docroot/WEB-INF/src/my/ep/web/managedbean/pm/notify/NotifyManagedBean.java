package my.ep.web.managedbean.pm.notify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmNotifyUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author MD
 * 
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
	
	public boolean isUnRead() {
		return isUnRead;
	}

	public void setUnRead(boolean isUnRead) {
		this.isUnRead = isUnRead;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	public NotifyManagedBean() {
		log.info("NotifyManagedBean inside ");
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public List<PmNotifyUser> getNotifyLists() {
		if (notifyLists == null) {
			notifyLists = new ArrayList<PmNotifyUser>();
		}
		return notifyLists;
	}

	public void setNotifyLists(List<PmNotifyUser> notifyList) {
		this.notifyLists = notifyList;
	}

	public PmNotifyUser[] getSelectedNotifys() {
		return selectedNotifys;
	}

	public void setSelectedNotifys(PmNotifyUser[] selectedNotifys) {
		this.selectedNotifys = selectedNotifys;
	}

	public PmNotifyUser getSelectedNotify() {
		return selectedNotify;
	}

	public void setSelectedNotify(PmNotifyUser selectedNotify) {
		this.selectedNotify = selectedNotify;
	}

	public PmNotifyUser getNotify() {
		return notify;
	}

	public void setNotify(PmNotifyUser notify) {
		this.notify = notify;
	}

	public boolean isDeleteButtonHide() {
		return deleteButtonHide;
	}

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
	 * @param searchItem the searchItem to set
	 */
	public void setSearchItem(String searchItem) {
		this.searchItem = searchItem;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
}

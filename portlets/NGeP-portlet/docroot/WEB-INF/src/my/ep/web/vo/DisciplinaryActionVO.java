package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierCategory;

public class DisciplinaryActionVO implements Serializable {
	private static final long serialVersionUID = 5887063064190149198L;
	private SmDisciplinaryAction smDiscplinaryAction;
	private boolean suspend;
	private String punishmentType; //26238
	private String startDate;
	private String endDate;
	private String remarks;
	private List<SmSupplierCategory> categoryCode;
	private List<SmPersonnel> blacklistOwner;
	private boolean warning;	
	private boolean cancelCategory;
	private boolean suspendRegistration;
	private boolean cancelRegistration;
	private boolean blacklistOwnerDirector;
	private boolean bumiputeraCancel;
	private boolean manufacturerCancel;
	private List<DisciplinaryActionVO> disciplinaryActionVoList;
	private List<DisciplinaryActionCategoryVO> categoryCodeList;
	private List<BlacklistOwnerDirectorVO> blacklistownerDirectorList;
	private boolean mofNoExist;
	/*
	 * 32560 START
	 */
	private boolean isActiveRecordStatus;
	/*
	 * 32560 END
	 */
	
	
	
	//27796: Disciplinary Action - start - 26238
	private List<DisciplinaryActionVO> remarksList;
	//27796: Disciplinary Action - end - 26238

	public SmDisciplinaryAction getSmDiscplinaryAction() {
		return smDiscplinaryAction;
	}

	public void setSmDiscplinaryAction(SmDisciplinaryAction smDiscplinaryAction) {
		this.smDiscplinaryAction = smDiscplinaryAction;
	}

	public boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}

	public String getPunishmentType() {
		return punishmentType;
	}

	public void setPunishmentType(String punishmentType) {
		this.punishmentType = punishmentType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SmSupplierCategory> getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(List<SmSupplierCategory> categoryCode) {
		this.categoryCode = categoryCode;
	}

	public List<SmPersonnel> getBlacklistOwner() {
		return blacklistOwner;
	}

	public void setBlacklistOwner(List<SmPersonnel> blacklistOwner) {
		this.blacklistOwner = blacklistOwner;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isCancelCategory() {
		return cancelCategory;
	}

	public void setCancelCategory(boolean cancelCategory) {
		this.cancelCategory = cancelCategory;
	}

	public boolean isSuspendRegistration() {
		return suspendRegistration;
	}

	public void setSuspendRegistration(boolean suspendRegistration) {
		this.suspendRegistration = suspendRegistration;
	}

	public boolean isBlacklistOwnerDirector() {
		return blacklistOwnerDirector;
	}

	public void setBlacklistOwnerDirector(boolean blacklistOwnerDirector) {
		this.blacklistOwnerDirector = blacklistOwnerDirector;
	}

	public boolean isCancelRegistration() {
		return cancelRegistration;
	}

	public void setCancelRegistration(boolean cancelRegistration) {
		this.cancelRegistration = cancelRegistration;
	}

	public boolean isBumiputeraCancel() {
		return bumiputeraCancel;
	}

	public void setBumiputeraCancel(boolean bumiputeraCancel) {
		this.bumiputeraCancel = bumiputeraCancel;
	}

	public boolean isManufacturerCancel() {
		return manufacturerCancel;
	}

	public void setManufacturerCancel(boolean manufacturerCancel) {
		this.manufacturerCancel = manufacturerCancel;
	}

	public List<DisciplinaryActionVO> getDisciplinaryActionVoList() {
		return disciplinaryActionVoList;
	}

	public void setDisciplinaryActionVoList(List<DisciplinaryActionVO> disciplinaryActionVoList) {
		this.disciplinaryActionVoList = disciplinaryActionVoList;
	}

	public List<DisciplinaryActionCategoryVO> getCategoryCodeList() {
		return categoryCodeList;
	}

	public void setCategoryCodeList(List<DisciplinaryActionCategoryVO> categoryCodeList) {
		this.categoryCodeList = categoryCodeList;
	}

	public List<BlacklistOwnerDirectorVO> getBlacklistownerDirectorList() {
		return blacklistownerDirectorList;
	}

	public void setBlacklistownerDirectorList(List<BlacklistOwnerDirectorVO> blacklistownerDirectorList) {
		this.blacklistownerDirectorList = blacklistownerDirectorList;
	}

	public boolean isMofNoExist() {
		return mofNoExist;
	}

	public void setMofNoExist(boolean mofNoExist) {
		this.mofNoExist = mofNoExist;
	}

	//27796: Disciplinary Action - start - 26238
	public List<DisciplinaryActionVO> getRemarksList() {
		return remarksList;
	}

	public void setRemarksList(List<DisciplinaryActionVO> remarksList) {
		this.remarksList = remarksList;
	}
	//27796: Disciplinary Action - end - 26238

	/**
	 * @return the isActiveRecordStatus
	 */
	public boolean isActiveRecordStatus() {
		return isActiveRecordStatus;
	}

	/**
	 * @param isActiveRecordStatus the isActiveRecordStatus to set
	 */
	public void setActiveRecordStatus(boolean isActiveRecordStatus) {
		this.isActiveRecordStatus = isActiveRecordStatus;
	}

}

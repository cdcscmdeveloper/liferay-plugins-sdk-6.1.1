package my.ep.web.managedbean.sm.selfrequestsupplieradmin;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "selfRequestSupplierAdminMB")
@SessionScoped
public class SelfRequestSupplierAdminManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3794823811578323532L;
	private String mofNo;
	private String icNo;
	private Long myCountryId;
	private Long selectedPersonnelId;
	private List<SelectItem> personnelItems;
	private List<SelectItem> countryItems;
	private List<SelectItem> stateItems;
	private List<SelectItem> districtItems;
	private List<SelectItem> cityItems;
	private SelectItem[] identificationTypeItems;
	private SmSupplier supplier;
	private SmPersonnel selectedAdmin;
	private SmPersonnelAddress selectedAdminPermAddr;
	private boolean agreeTC;
	private String page;
	private Date currDate;

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	public SmPersonnel getSelectedAdmin() {
		return selectedAdmin;
	}

	public void setSelectedAdmin(SmPersonnel selectedAdmin) {
		this.selectedAdmin = selectedAdmin;
	}

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getIcNo() {
		return icNo;
	}

	public void setIcNo(String icNo) {
		this.icNo = icNo;
	}

	public List<SelectItem> getPersonnelItems() {
		return personnelItems;
	}

	public void setPersonnelItems(List<SelectItem> personnelItems) {
		this.personnelItems = personnelItems;
	}

	public Long getSelectedPersonnelId() {
		return selectedPersonnelId;
	}

	public void setSelectedPersonnelId(Long selectedPersonnelId) {
		this.selectedPersonnelId = selectedPersonnelId;
	}

	public List<SelectItem> getCountryItems() {
		return countryItems;
	}

	public void setCountryItems(List<SelectItem> countryItems) {
		this.countryItems = countryItems;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public SmPersonnelAddress getSelectedAdminPermAddr() {
		return selectedAdminPermAddr;
	}

	public void setSelectedAdminPermAddr(SmPersonnelAddress selectedAdminPermAddr) {
		this.selectedAdminPermAddr = selectedAdminPermAddr;
	}

	public SelectItem[] getIdentificationTypeItems() {
		return identificationTypeItems;
	}

	public void setIdentificationTypeItems(SelectItem[] identificationTypeItems) {
		this.identificationTypeItems = identificationTypeItems;
	}

	public List<SelectItem> getStateItems() {
		return stateItems;
	}

	public void setStateItems(List<SelectItem> stateItems) {
		this.stateItems = stateItems;
	}

	public List<SelectItem> getDistrictItems() {
		return districtItems;
	}

	public void setDistrictItems(List<SelectItem> districtItems) {
		this.districtItems = districtItems;
	}

	public List<SelectItem> getCityItems() {
		return cityItems;
	}

	public void setCityItems(List<SelectItem> cityItems) {
		this.cityItems = cityItems;
	}

	public Long getMyCountryId() {
		return myCountryId;
	}

	public void setMyCountryId(Long myCountryId) {
		this.myCountryId = myCountryId;
	}

}

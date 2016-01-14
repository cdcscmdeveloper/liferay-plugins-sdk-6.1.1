package my.ep.web.managedbean.sm.forcereplaceadmin;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.ReactivateSupplierVO;

@ManagedBean(name = "forceReplaceSuppAdminMb")
@SessionScoped
public class ForceReplaceSuppAdminManagedBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6370556470033882900L;
	private ReactivateSupplierVO reactivateBasic;
	private List<SelectItem> identificationTypes;
	private List<SelectItem> nationalities;
	private List<SelectItem> permCountries;
	private List<SelectItem> permStates;
	private List<SelectItem> permDistricts;
	private List<SelectItem> permCities;
	private List<SelectItem> mailCountries;
	private List<SelectItem> mailStates;
	private List<SelectItem> mailDistricts;
	private List<SelectItem> mailCities;
	private List<SelectItem> salutationItems;
	private SmSupplier smSupplier;
	private SmPersonnel newAdmin;
	private SmPersonnel oldAdmin;
	private SmPersonnelAddress permAddr;
	private SmPersonnelAddress mailAddr;
	private boolean hasSupplierUser;
	private boolean agreeTnc;
	private boolean required;
	private boolean existingUser;
	private boolean showParentMsg;
	private PmParameter identificationType;
	private Long myCountryId;

	public ForceReplaceSuppAdminManagedBean() {
		
	}

	public ReactivateSupplierVO getReactivateBasic() {
		return reactivateBasic;
	}

	public void setReactivateBasic(ReactivateSupplierVO reactivateBasic) {
		this.reactivateBasic = reactivateBasic;
	}

	public List<SelectItem> getIdentificationTypes() {
		return identificationTypes;
	}

	public void setIdentificationTypes(List<SelectItem> identificationTypes) {
		this.identificationTypes = identificationTypes;
	}

	public List<SelectItem> getNationalities() {
		return nationalities;
	}

	public void setNationalities(List<SelectItem> nationalities) {
		this.nationalities = nationalities;
	}

	public List<SelectItem> getPermCountries() {
		return permCountries;
	}

	public void setPermCountries(List<SelectItem> permCountries) {
		this.permCountries = permCountries;
	}

	public List<SelectItem> getPermStates() {
		return permStates;
	}

	public void setPermStates(List<SelectItem> permStates) {
		this.permStates = permStates;
	}

	public List<SelectItem> getPermDistricts() {
		return permDistricts;
	}

	public void setPermDistricts(List<SelectItem> permDistricts) {
		this.permDistricts = permDistricts;
	}

	public List<SelectItem> getPermCities() {
		return permCities;
	}

	public void setPermCities(List<SelectItem> permCities) {
		this.permCities = permCities;
	}

	public List<SelectItem> getMailCountries() {
		return mailCountries;
	}

	public void setMailCountries(List<SelectItem> mailCountries) {
		this.mailCountries = mailCountries;
	}

	public List<SelectItem> getMailStates() {
		return mailStates;
	}

	public void setMailStates(List<SelectItem> mailStates) {
		this.mailStates = mailStates;
	}

	public List<SelectItem> getMailDistricts() {
		return mailDistricts;
	}

	public void setMailDistricts(List<SelectItem> mailDistricts) {
		this.mailDistricts = mailDistricts;
	}

	public List<SelectItem> getMailCities() {
		return mailCities;
	}

	public void setMailCities(List<SelectItem> mailCities) {
		this.mailCities = mailCities;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmPersonnel getNewAdmin() {
		return newAdmin;
	}

	public void setNewAdmin(SmPersonnel newAdmin) {
		this.newAdmin = newAdmin;
	}

	public SmPersonnel getOldAdmin() {
		return oldAdmin;
	}

	public void setOldAdmin(SmPersonnel oldAdmin) {
		this.oldAdmin = oldAdmin;
	}

	public SmPersonnelAddress getPermAddr() {
		return permAddr;
	}

	public void setPermAddr(SmPersonnelAddress permAddr) {
		this.permAddr = permAddr;
	}

	public SmPersonnelAddress getMailAddr() {
		return mailAddr;
	}

	public void setMailAddr(SmPersonnelAddress mailAddr) {
		this.mailAddr = mailAddr;
	}

	public boolean isHasSupplierUser() {
		return hasSupplierUser;
	}

	public void setHasSupplierUser(boolean hasSupplierUser) {
		this.hasSupplierUser = hasSupplierUser;
	}

	public boolean isAgreeTnc() {
		return agreeTnc;
	}

	public void setAgreeTnc(boolean agreeTnc) {
		this.agreeTnc = agreeTnc;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isExistingUser() {
		return existingUser;
	}

	public void setExistingUser(boolean existingUser) {
		this.existingUser = existingUser;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public PmParameter getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(PmParameter identificationType) {
		this.identificationType = identificationType;
	}

	public Long getMyCountryId() {
		return myCountryId;
	}

	public void setMyCountryId(Long myCountryId) {
		this.myCountryId = myCountryId;
	}

	public List<SelectItem> getSalutationItems() {
		return salutationItems;
	}

	public void setSalutationItems(List<SelectItem> salutationItems) {
		this.salutationItems = salutationItems;
	}

}

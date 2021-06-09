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
//#28765 - Start
import java.util.ArrayList;
import my.ep.web.vo.SoftcertUploadSuppDocVO;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sm.SmReplaceSupplierAdminDoc;
//#28765 - End

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
	private String accType;
	//#28765 - Start
	private String displayMessageReplaceTrans;
	private int bypassFlag1;
	private int bypassFlag2;
	private int bypassFlag3;
	private boolean bypassFlag;
	private List<SmPersonnel> replacesupplieradminSelectList;
	public List<PmDocument> replacesuppadminUploadSuppDocList;
	private List<SmReplaceSupplierAdminDoc> smReplaceSupplierAdminDocs = new ArrayList();
	private int attachedFileIndex;
	private String fileDirectory;
	//#28765 - End
	
	private boolean disableEquity=false;
	private boolean disableStaff=false;
	//28765
	private boolean disableNationality;

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

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public boolean isDisableEquity() {
		return disableEquity;
	}

	public void setDisableEquity(boolean disableEquity) {
		this.disableEquity = disableEquity;
	}

	public boolean isDisableStaff() {
		return disableStaff;
	}

	public void setDisableStaff(boolean disableStaff) {
		this.disableStaff = disableStaff;
	}
	//#28765 - Start
	public List<SmPersonnel> getReplaceSupplierAdminSelectList() {
		return replacesupplieradminSelectList;
	}
	public void setReplaceSupplierAdminSelectList(List<SmPersonnel> replacesupplieradminSelectList) {
		this.replacesupplieradminSelectList = replacesupplieradminSelectList;
	}
	public int getAttachedFileIndex() {
		return attachedFileIndex;
	}
	public void setAttachedFileIndex(int attachedFileIndex) {
		this.attachedFileIndex = attachedFileIndex;
	}
	public List<PmDocument> getReplacesuppadminUploadSuppDocList() {
		return replacesuppadminUploadSuppDocList;
	}
	public void setReplacesuppadminUploadSuppDocList(List<PmDocument> replacesuppadminUploadSuppDocList) {
		this.replacesuppadminUploadSuppDocList = replacesuppadminUploadSuppDocList;
	}
	public boolean isBypassFlag() {
		return bypassFlag;
	}
	public void setBypassFlag(boolean bypassFlag) {
		this.bypassFlag = bypassFlag;
	}
	public int getBypassFlag1() {
		return bypassFlag1;
	}
	public void setBypassFlag1(int bypassFlag1) {
		this.bypassFlag1 = bypassFlag1;
	}
	public int getBypassFlag2() {
		return bypassFlag2;
	}
	public void setBypassFlag2(int bypassFlag2) {
		this.bypassFlag2 = bypassFlag2;
	}
	public int getBypassFlag3() {
		return bypassFlag3;
	}
	public void setBypassFlag3(int bypassFlag3) {
		this.bypassFlag3 = bypassFlag3;
	}
	public List<SmReplaceSupplierAdminDoc> getSmReplaceSupplierAdminDocs() {
		return smReplaceSupplierAdminDocs;
	}
	public void setSmReplaceSupplierAdminDocs(List<SmReplaceSupplierAdminDoc> smReplaceSupplierAdminDocs) {
		this.smReplaceSupplierAdminDocs = smReplaceSupplierAdminDocs;
	}
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}
	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}	
	public String getDisplayMessageReplaceTrans() {
		return displayMessageReplaceTrans;
	}
	public void setDisplayMessageReplaceTrans(String displayMessageReplaceTrans) {
		this.displayMessageReplaceTrans = displayMessageReplaceTrans;
	}
	
	public boolean isDisableNationality() {
		return disableNationality;
	}

	public void setDisableNationality(boolean disableNationality) {
		this.disableNationality = disableNationality;
	}
	//#28765 - End
}
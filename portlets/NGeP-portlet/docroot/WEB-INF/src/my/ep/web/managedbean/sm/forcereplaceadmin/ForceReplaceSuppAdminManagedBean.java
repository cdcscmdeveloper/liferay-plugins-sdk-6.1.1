package my.ep.web.managedbean.sm.forcereplaceadmin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmReplaceSupplierAdminDoc;
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
	private List<SelectItem> permDivisions; //#34159 & #36718
	private List<SelectItem> permDistricts;
	private List<SelectItem> permCities;
	private List<SelectItem> mailCountries;
	private List<SelectItem> mailStates;
	private List<SelectItem> mailDivisions; //#34159 & #36718
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
	private List<SmReplaceSupplierAdminDoc> smReplaceSupplierAdminDocs = new ArrayList<SmReplaceSupplierAdminDoc>();
	private int attachedFileIndex;
	private String fileDirectory;
	//#28765 - End
	
	private boolean disableEquity=false;
	private boolean disableStaff=false;
	//28765
	private boolean disableNationality;
	
	//33563 - START
	private boolean displayRolePanel = false;
	private boolean displayRoleEquity = false;
	private boolean displayRoleDirector = false;
	private boolean displayRoleHighManagement = false;
	private boolean displayRoleManagement = false;
	private boolean displayRoleProfessional = false;
	private boolean displayRoleSemiPro = false;
	private boolean displayRoleClericalStaff = false;
	
	private boolean disabledRoleEquity = false;
	private boolean disabledRoleDirector = false;
	private boolean disabledRoleHighManagement = false;
	private boolean disabledRoleManagement = false;
	private boolean disabledRoleProfessional = false;
	private boolean disabledRoleSemiPro = false;
	private boolean disabledRoleClericalStaff = false;
	//33563 - END
	
	// #28895_35501 Start - AUDIT TRAIL SM
	private String oldIdentification;
	private String newIdentifiction;
	// #28895_35501 End

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
	
	//#34159 & #36718 - START
	public List<SelectItem> getPermDivisions() {
		return permDivisions;
	}

	public void setPermDivisions(List<SelectItem> permDivisions) {
		this.permDivisions = permDivisions;
	}
	
	public List<SelectItem> getMailDivisions() {
		return mailDivisions;
	}

	public void setMailDivisions(List<SelectItem> mailDivisions) {
		this.mailDivisions = mailDivisions;
	}
	//#34159 & #36718 - END
	
	// START -> #33563
	public boolean isDisplayRoleEquity() {
		return displayRoleEquity;
	}

	public void setDisplayRoleEquity(boolean displayRoleEquity) {
		this.displayRoleEquity = displayRoleEquity;
	}

	public boolean isDisplayRoleDirector() {
		return displayRoleDirector;
	}

	public void setDisplayRoleDirector(boolean displayRoleDirector) {
		this.displayRoleDirector = displayRoleDirector;
	}

	public boolean isDisplayRoleHighManagement() {
		return displayRoleHighManagement;
	}

	public void setDisplayRoleHighManagement(boolean displayRoleHighManagement) {
		this.displayRoleHighManagement = displayRoleHighManagement;
	}

	public boolean isDisplayRoleManagement() {
		return displayRoleManagement;
	}

	public void setDisplayRoleManagement(boolean displayRoleManagement) {
		this.displayRoleManagement = displayRoleManagement;
	}

	public boolean isDisplayRoleProfessional() {
		return displayRoleProfessional;
	}

	public void setDisplayRoleProfessional(boolean displayRoleProfessional) {
		this.displayRoleProfessional = displayRoleProfessional;
	}

	public boolean isDisplayRoleSemiPro() {
		return displayRoleSemiPro;
	}

	public void setDisplayRoleSemiPro(boolean displayRoleSemiPro) {
		this.displayRoleSemiPro = displayRoleSemiPro;
	}

	public boolean isDisplayRoleClericalStaff() {
		return displayRoleClericalStaff;
	}

	public void setDisplayRoleClericalStaff(boolean displayRoleClericalStaff) {
		this.displayRoleClericalStaff = displayRoleClericalStaff;
	}

	public boolean isDisabledRoleEquity() {
		return disabledRoleEquity;
	}

	public void setDisabledRoleEquity(boolean disabledRoleEquity) {
		this.disabledRoleEquity = disabledRoleEquity;
	}

	public boolean isDisabledRoleDirector() {
		return disabledRoleDirector;
	}

	public void setDisabledRoleDirector(boolean disabledRoleDirector) {
		this.disabledRoleDirector = disabledRoleDirector;
	}

	public boolean isDisabledRoleHighManagement() {
		return disabledRoleHighManagement;
	}

	public void setDisabledRoleHighManagement(boolean disabledRoleHighManagement) {
		this.disabledRoleHighManagement = disabledRoleHighManagement;
	}

	public boolean isDisabledRoleManagement() {
		return disabledRoleManagement;
	}

	public void setDisabledRoleManagement(boolean disabledRoleManagement) {
		this.disabledRoleManagement = disabledRoleManagement;
	}

	public boolean isDisabledRoleProfessional() {
		return disabledRoleProfessional;
	}

	public void setDisabledRoleProfessional(boolean disabledRoleProfessional) {
		this.disabledRoleProfessional = disabledRoleProfessional;
	}

	public boolean isDisabledRoleSemiPro() {
		return disabledRoleSemiPro;
	}

	public void setDisabledRoleSemiPro(boolean disabledRoleSemiPro) {
		this.disabledRoleSemiPro = disabledRoleSemiPro;
	}

	public boolean isDisabledRoleClericalStaff() {
		return disabledRoleClericalStaff;
	}

	public void setDisabledRoleClericalStaff(boolean disabledRoleClericalStaff) {
		this.disabledRoleClericalStaff = disabledRoleClericalStaff;
	}
	//END -> #33563

	public boolean isDisplayRolePanel() {
		return displayRolePanel;
	}

	public void setDisplayRolePanel(boolean displayRolePanel) {
		this.displayRolePanel = displayRolePanel;
	}
	
	// #28895_35501 Start - AUDIT TRAIL SM
	public String getOldIdentification() {
		return oldIdentification;
	}

	public void setOldIdentification(String oldIdentification) {
		this.oldIdentification = oldIdentification;
	}

	public String getNewIdentifiction() {
		return newIdentifiction;
	}

	public void setNewIdentifiction(String newIdentifiction) {
		this.newIdentifiction = newIdentifiction;
	}
	// #28895_35501 End
}

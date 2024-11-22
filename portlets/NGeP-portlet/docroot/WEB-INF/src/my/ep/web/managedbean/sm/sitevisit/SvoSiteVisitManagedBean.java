package my.ep.web.managedbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvAdtMachine;
import my.ep.persistence.entity.sm.SmSvAssistantOfficer;
import my.ep.persistence.entity.sm.SmSvEquipment;
import my.ep.persistence.entity.sm.SmSvInterviewee;
import my.ep.persistence.entity.sm.SmSvMachine;
import my.ep.persistence.entity.sm.SmSvManufacturingDoc;
import my.ep.persistence.entity.sm.SmSvPrintwork;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.entity.sm.SmSvWorker;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smSvoSiteVisitMB")
@SessionScoped
public class SvoSiteVisitManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private long appId;
	private String[] bpmAction;
	private String taskId;
	private Object bpmPojo;
	private int activeIndex;
	private SmSvTask smSvTask;
	private SmSvAddress selectedSmSvAddress;
	private Map<String,List<SmSvAddress>> smSvAddressMap;
	private Map<String,SmFacility> smSvFacilityMap;
	private SmSvTemplate selectedSvTemplate;
	private SmSvInterviewee[] selectedInterviewees;
	private SmSvWorker[] selectedWorkers;
	private SmSvManufacturingDoc[] selectedAddFinancialDoc;
	
	private int totalBOD;
	private int totalMgmt;
	private int totalWorker;
	
	private int dirBumi;
	private int dirNonBumi;
	private int dirNonMalaysian;
	private int mgmtBumi;
	private int mgmtNonBumi;
	private int mgmtNonMalaysian;
	private int wrkBumi;
	private int wrkNonBumi;
	private int wrkNonMalaysian;
	private List<SmPersonnel> bodLs;
	private List<SmPersonnel> mgmtLs;
	
	private int totalEquityOwner;
	private List<SmPersonnel> equityOwnerLs;
	
	private int totalCheqSign;
	private int cheqBumi;
	private int cheqNonBumi;
	private int cheqNonMalaysian;
	
	private List<SmSvEquipment> workshopEquipMain;
	private List<SmSvEquipment> workshopEquipBeating;
	private Map<Long,PmParameterDesc> workshopEquipmentMap;
	
	private SmMachineDtl[] selectedDockyardMachine;
	private SmMachineDtl[] selectedWorkshopMachine;

	private SmSvPrintwork[] selectedPrintWorks;
	
	private List<SmSupplierCategory> categoryApplicable;
	private SmPersonnel contactPerson;
	
	private Integer lastIndex;

	private List<String> higherManagementNames = new ArrayList<String>();
	private List<String> managementNames = new ArrayList<String>();
	
	private SmSvAdtMachine selectedAdtMachine;
	private SmSvMachine selectedSvMachine;
	
	private SmGstSupplier smGstSupplier;
	private SmSvTemplate selectedAttachmentTemplate;
	private SmAttachment selectedUploadAttachment;
	private Long selectedAttachmentTemplId;
	private String attachmentDeleteMsg;
	private boolean viewMode;
	
	private Object oriSelectedMachine;
	private Long svTaskId;
	private SmAppl smAppl;
	private Map<String, List<SmSvTask>> templateTaskMap;
	
	private boolean claimTask;
	private String businessTypeDesc;
	private String companyAddress;
	private String mofNo;
	private int siteVisitNo;
	private SmSupplierCategory supplierCategory;
	private List<SmMachineDtl> machineDtlLs;
	
	private List<SmManufacturingDtl> selectedManufacturingList;
	private List<SmManufacturingDtl> manufacturingDtlLs;
	private SmManufacturingDtl selectedManufacturer;
	private Object oriSelectedManufacturingDtl;
	
	private List<SmPersonnel> staffLs;
	//#29430
	private boolean fromReport;
	
	//#28774 - Start
	private SmSvAssistantOfficer selectedAssistantOfficer;
	//#28774 - End
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	private List<SmSvTemplate> templates;
	private String parameterDesc;
	private boolean licensed;
	private String activePage;
	private List<PmMachine> travellingSoftwareList;
	private SelectItem[] identificationTypes;
	//28775 - Site Visit Grouping Enhancement - 28775 - end	
	//29379 - Site Visit Grouping Enhancement - 28775
	private SmFacility workerFacility;
	

	public Object getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(Object bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public SmSvTask getSmSvTask() {
		return smSvTask;
	}

	public void setSmSvTask(SmSvTask smSvTask) {
		this.smSvTask = smSvTask;
	}

	public SmSvAddress getSelectedSmSvAddress() {
		return selectedSmSvAddress;
	}

	public void setSelectedSmSvAddress(SmSvAddress selectedSmSvAddress) {
		this.selectedSmSvAddress = selectedSmSvAddress;
	}

	public SmSvTemplate getSelectedSvTemplate() {
		return selectedSvTemplate;
	}

	public void setSelectedSvTemplate(SmSvTemplate selectedSvTemplate) {
		this.selectedSvTemplate = selectedSvTemplate;
	}

	public int getTotalBOD() {
		return totalBOD;
	}

	public void setTotalBOD(int totalBOD) {
		this.totalBOD = totalBOD;
	}

	public int getTotalMgmt() {
		return totalMgmt;
	}

	public void setTotalMgmt(int totalMgmt) {
		this.totalMgmt = totalMgmt;
	}

	public int getTotalWorker() {
		return totalWorker;
	}

	public void setTotalWorker(int totalWorker) {
		this.totalWorker = totalWorker;
	}
	
	public int getDirBumi() {
		return dirBumi;
	}

	public void setDirBumi(int dirBumi) {
		this.dirBumi = dirBumi;
	}

	public int getDirNonBumi() {
		return dirNonBumi;
	}

	public void setDirNonBumi(int dirNonBumi) {
		this.dirNonBumi = dirNonBumi;
	}

	public int getDirNonMalaysian() {
		return dirNonMalaysian;
	}

	public void setDirNonMalaysian(int dirNonMalaysian) {
		this.dirNonMalaysian = dirNonMalaysian;
	}

	public int getMgmtBumi() {
		return mgmtBumi;
	}

	public void setMgmtBumi(int mgmtBumi) {
		this.mgmtBumi = mgmtBumi;
	}

	public int getMgmtNonBumi() {
		return mgmtNonBumi;
	}

	public void setMgmtNonBumi(int mgmtNonBumi) {
		this.mgmtNonBumi = mgmtNonBumi;
	}

	public int getMgmtNonMalaysian() {
		return mgmtNonMalaysian;
	}

	public void setMgmtNonMalaysian(int mgmtNonMalaysian) {
		this.mgmtNonMalaysian = mgmtNonMalaysian;
	}

	public int getWrkBumi() {
		return wrkBumi;
	}

	public void setWrkBumi(int wrkBumi) {
		this.wrkBumi = wrkBumi;
	}

	public int getWrkNonBumi() {
		return wrkNonBumi;
	}

	public void setWrkNonBumi(int wrkNonBumi) {
		this.wrkNonBumi = wrkNonBumi;
	}

	public int getWrkNonMalaysian() {
		return wrkNonMalaysian;
	}

	public void setWrkNonMalaysian(int wrkNonMalaysian) {
		this.wrkNonMalaysian = wrkNonMalaysian;
	}

	public List<SmPersonnel> getBodLs() {
		return bodLs;
	}

	public void setBodLs(List<SmPersonnel> bodLs) {
		this.bodLs = bodLs;
	}

	public List<SmPersonnel> getMgmtLs() {
		return mgmtLs;
	}

	public void setMgmtLs(List<SmPersonnel> mgmtLs) {
		this.mgmtLs = mgmtLs;
	}

	public SmSvInterviewee[] getSelectedInterviewees() {
		return selectedInterviewees;
	}

	public void setSelectedInterviewees(SmSvInterviewee[] selectedInterviewees) {
		this.selectedInterviewees = selectedInterviewees;
	}

	public int getTotalCheqSign() {
		return totalCheqSign;
	}

	public void setTotalCheqSign(int totalCheqSign) {
		this.totalCheqSign = totalCheqSign;
	}

	public int getCheqBumi() {
		return cheqBumi;
	}

	public void setCheqBumi(int cheqBumi) {
		this.cheqBumi = cheqBumi;
	}

	public int getCheqNonBumi() {
		return cheqNonBumi;
	}

	public void setCheqNonBumi(int cheqNonBumi) {
		this.cheqNonBumi = cheqNonBumi;
	}

	public int getCheqNonMalaysian() {
		return cheqNonMalaysian;
	}

	public void setCheqNonMalaysian(int cheqNonMalaysian) {
		this.cheqNonMalaysian = cheqNonMalaysian;
	}

	public SmMachineDtl[] getSelectedDockyardMachine() {
		return selectedDockyardMachine;
	}

	public void setSelectedDockyardMachine(SmMachineDtl[] selectedDockyardMachine) {
		this.selectedDockyardMachine = selectedDockyardMachine;
	}

	public List<SmSvEquipment> getWorkshopEquipMain() {
		return workshopEquipMain;
	}

	public List<SmSvEquipment> getWorkshopEquipBeating() {
		return workshopEquipBeating;
	}

	public Map<Long, PmParameterDesc> getWorkshopEquipmentMap() {
		return workshopEquipmentMap;
	}

	public void setWorkshopEquipMain(List<SmSvEquipment> workshopEquipMain) {
		this.workshopEquipMain = workshopEquipMain;
	}

	public void setWorkshopEquipBeating(List<SmSvEquipment> workshopEquipBeating) {
		this.workshopEquipBeating = workshopEquipBeating;
	}

	public void setWorkshopEquipmentMap(
			Map<Long, PmParameterDesc> workshopEquipmentMap) {
		this.workshopEquipmentMap = workshopEquipmentMap;
	}

	public SmMachineDtl[] getSelectedWorkshopMachine() {
		return selectedWorkshopMachine;
	}

	public void setSelectedWorkshopMachine(SmMachineDtl[] selectedWorkshopMachine) {
		this.selectedWorkshopMachine = selectedWorkshopMachine;
	}

	public SmSvWorker[] getSelectedWorkers() {
		return selectedWorkers;
	}

	public void setSelectedWorkers(SmSvWorker[] selectedWorkers) {
		this.selectedWorkers = selectedWorkers;
	}

	public SmSvManufacturingDoc[] getSelectedAddFinancialDoc() {
		return selectedAddFinancialDoc;
	}

	public void setSelectedAddFinancialDoc(
			SmSvManufacturingDoc[] selectedAddFinancialDoc) {
		this.selectedAddFinancialDoc = selectedAddFinancialDoc;
	}

	public SmSvPrintwork[] getSelectedPrintWorks() {
		return selectedPrintWorks;
	}

	public void setSelectedPrintWorks(SmSvPrintwork[] selectedPrintWorks) {
		this.selectedPrintWorks = selectedPrintWorks;
	}

	public List<SmSupplierCategory> getCategoryApplicable() {
		return categoryApplicable;
	}

	public void setCategoryApplicable(List<SmSupplierCategory> categoryApplicable) {
		this.categoryApplicable = categoryApplicable;
	}

	public SmPersonnel getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(SmPersonnel contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Integer getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(Integer lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int getTotalEquityOwner() {
		return totalEquityOwner;
	}

	public void setTotalEquityOwner(int totalEquityOwner) {
		this.totalEquityOwner = totalEquityOwner;
	}

	public List<SmPersonnel> getEquityOwnerLs() {
		return equityOwnerLs;
	}

	public void setEquityOwnerLs(List<SmPersonnel> equityOwnerLs) {
		this.equityOwnerLs = equityOwnerLs;
	}

	public List<String> getHigherManagementNames() {
		return higherManagementNames;
	}

	public void setHigherManagementNames(List<String> higherManagementNames) {
		this.higherManagementNames = higherManagementNames;
	}

	public List<String> getManagementNames() {
		return managementNames;
	}

	public void setManagementNames(List<String> managementNames) {
		this.managementNames = managementNames;
	}

	public Map<String, List<SmSvAddress>> getSmSvAddressMap() {
		return smSvAddressMap;
	}

	public void setSmSvAddressMap(Map<String, List<SmSvAddress>> smSvAddressMap) {
		this.smSvAddressMap = smSvAddressMap;
	}

	public Map<String, SmFacility> getSmSvFacilityMap() {
		return smSvFacilityMap;
	}

	public void setSmSvFacilityMap(Map<String, SmFacility> smSvFacilityMap) {
		this.smSvFacilityMap = smSvFacilityMap;
	}

	public SmSvAdtMachine getSelectedAdtMachine() {
		return selectedAdtMachine;
	}

	public void setSelectedAdtMachine(SmSvAdtMachine selectedAdtMachine) {
		this.selectedAdtMachine = selectedAdtMachine;
	}

	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	public SmSvTemplate getSelectedAttachmentTemplate() {
		return selectedAttachmentTemplate;
	}

	public void setSelectedAttachmentTemplate(
			SmSvTemplate selectedAttachmentTemplate) {
		this.selectedAttachmentTemplate = selectedAttachmentTemplate;
	}

	public SmAttachment getSelectedUploadAttachment() {
		return selectedUploadAttachment;
	}

	public void setSelectedUploadAttachment(SmAttachment selectedUploadAttachment) {
		this.selectedUploadAttachment = selectedUploadAttachment;
	}

	public String getAttachmentDeleteMsg() {
		return attachmentDeleteMsg;
	}

	public void setAttachmentDeleteMsg(String attachmentDeleteMsg) {
		this.attachmentDeleteMsg = attachmentDeleteMsg;
	}

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}

	public Long getSvTaskId() {
		return svTaskId;
	}

	public void setSvTaskId(Long svTaskId) {
		this.svTaskId = svTaskId;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public Map<String, List<SmSvTask>> getTemplateTaskMap() {
		return templateTaskMap;
	}

	public void setTemplateTaskMap(Map<String, List<SmSvTask>> templateTaskMap) {
		this.templateTaskMap = templateTaskMap;
	}

	public Long getSelectedAttachmentTemplId() {
		return selectedAttachmentTemplId;
	}

	public void setSelectedAttachmentTemplId(Long selectedAttachmentTemplId) {
		this.selectedAttachmentTemplId = selectedAttachmentTemplId;
	}

	public boolean isClaimTask() {
		return claimTask;
	}

	public void setClaimTask(boolean claimTask) {
		this.claimTask = claimTask;
	}

	public String getBusinessTypeDesc() {
		return businessTypeDesc;
	}

	public void setBusinessTypeDesc(String businessTypeDesc) {
		this.businessTypeDesc = businessTypeDesc;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public int getSiteVisitNo() {
		return siteVisitNo;
	}

	public void setSiteVisitNo(int siteVisitNo) {
		this.siteVisitNo = siteVisitNo;
	}

	public SmSupplierCategory getSupplierCategory() {
		return supplierCategory;
	}

	public void setSupplierCategory(SmSupplierCategory supplierCategory) {
		this.supplierCategory = supplierCategory;
	}

	public List<SmPersonnel> getStaffLs() {
		return staffLs;
	}

	public void setStaffLs(List<SmPersonnel> staffLs) {
		this.staffLs = staffLs;
	}

	public List<SmMachineDtl> getMachineDtlLs() {
		return machineDtlLs;
	}

	public void setMachineDtlLs(List<SmMachineDtl> machineDtlLs) {
		this.machineDtlLs = machineDtlLs;
	}

	public SmSvMachine getSelectedSvMachine() {
		return selectedSvMachine;
	}

	public void setSelectedSvMachine(SmSvMachine selectedSvMachine) {
		this.selectedSvMachine = selectedSvMachine;
	}

	public Object getOriSelectedMachine() {
		return oriSelectedMachine;
	}

	public void setOriSelectedMachine(Object oriSelectedMachine) {
		this.oriSelectedMachine = oriSelectedMachine;
	}
	
	public Object getOriSelectedManufacturingDtl() {
		return oriSelectedManufacturingDtl;
	}

	public void setOriSelectedManufacturingDtl(Object oriSelectedManufacturingDtl) {
		this.oriSelectedManufacturingDtl = oriSelectedManufacturingDtl;
	}

	public List<SmManufacturingDtl> getSelectedManufacturingList() {
		return selectedManufacturingList;
	}

	public void setSelectedManufacturingList(List<SmManufacturingDtl> selectedManufacturingList) {
		this.selectedManufacturingList = selectedManufacturingList;
	}

	public List<SmManufacturingDtl> getManufacturingDtlLs() {
		return manufacturingDtlLs;
	}

	public void setManufacturingDtlLs(List<SmManufacturingDtl> manufacturingDtlLs) {
		this.manufacturingDtlLs = manufacturingDtlLs;
	}

	public SmManufacturingDtl getSelectedManufacturer() {
		return selectedManufacturer;
	}

	public void setSelectedManufacturer(SmManufacturingDtl selectedManufacturer) {
		this.selectedManufacturer = selectedManufacturer;
	}

	//#28744 - Start
	public SmSvAssistantOfficer getSelectedAssistantOfficer() {
		return selectedAssistantOfficer;
	}

	public void setSelectedAssistantOfficer(SmSvAssistantOfficer selectedAssistantOfficer) {
		this.selectedAssistantOfficer = selectedAssistantOfficer;
	}
	//#28774 - End

	public boolean isFromReport() {
		return fromReport;
	}

	public void setFromReport(boolean fromReport) {
		this.fromReport = fromReport;
	}
	
	//28775 - Site Visit Grouping Enhancement - 28775 - start
	public List<SmSvTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<SmSvTemplate> templates) {
		this.templates = templates;
	}

	public String getParameterDesc() {
		return parameterDesc;
	}

	public void setParameterDesc(String parameterDesc) {
		this.parameterDesc = parameterDesc;
	}

	public boolean getLicensed() {
		return licensed;
	}

	public void setLicensed(boolean isLicensed) {
		this.licensed = isLicensed;
	}

	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}

	public List<PmMachine> getTravellingSoftwareList() {
		return travellingSoftwareList;
	}

	public void setTravellingSoftwareList(List<PmMachine> travellingSoftwareList) {
		this.travellingSoftwareList = travellingSoftwareList;
	}

	public SelectItem[] getIdentificationTypes() {
		return identificationTypes;
	}

	public void setIdentificationTypes(SelectItem[] identificationTypes) {
		this.identificationTypes = identificationTypes;
	}
	//28775 - Site Visit Grouping Enhancement - 28775 - end

	//29379 - Site Visit Grouping Enhancement - 28775 - start
	public SmFacility getWorkerFacility() {
		return workerFacility;
	}

	public void setWorkerFacility(SmFacility workerFacility) {
		this.workerFacility = workerFacility;
	}
	//29379 - Site Visit Grouping Enhancement - 28775 - end
}
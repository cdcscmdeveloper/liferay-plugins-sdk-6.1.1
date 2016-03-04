/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Circular Panel Managed Bean
 * 
 * @author alam
 * @since 9/10/2013
 */
@ManagedBean
@SessionScoped
public class PanelManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String fileDirectory = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR;
	private String circularDocType;
	private String circularAdmin;
	private String action;
	private String zoneName;
	private String gotoPreviewPage;	
	private String taskName;
		
	private Long itemCategoryId;
	private Long panelCoverageId;
	private int selectedTabIndex;
	
	private boolean editZone;
	private boolean updateAction =false;
	private boolean viewContract =false;
	private boolean buttonHideActive;
	private boolean selectionOfZone;
	private boolean chkCancelPage;
	private boolean fromCircularNotice;
	private boolean differentModule;
	private boolean circularExtension;
	private int index = -1;
	private boolean buttonHideActiveDel = true;
	
	private TreeNode[] selectedGeneralTreeNodes;
	private TreeNode rootZoanlTree;
	
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCancelCircular selectedCancelCircular;
	private CtCancelCircularReq cancelCircularReq;
	private CtPanelItem selectedPanelItems;
	private CtPanelItem selectedItemForSpec;
	private CtPanelZone selectedPanelZone;
	private List <CtPanelSupplier> selectedPanelSuppliers;
	private List<CtContactPerson> selectedContactPersons;
	private List<CtPanelItemSpec> selectedPanelItemSpecs;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private List<CtCircularVer> circularVerList;
	private List<CtCancelCircular> selectedCancelledCircularList;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtPanelSupplier> panelSupplierList;
	private CtCircularVer[] selectedCircularVerList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
	private List<SmSupplier> supplierList; 
	private Map<String, SmSupplierBank> supplierBankAccountMap;
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	
	private String supplierBranch;
	private SmSupplier smSupplier;
	private SmCompanyBasic smCompanyBasic;
	private SmPersonnel smPersonnel;
	private SmAddress supplierAddress;
	private SmMofAccount smMofAccount;
	private List<CtCircularPidReason> ctCircularPidReasons;
	
	/**
	 * @return the circular
	 */
	public CtCircular getCircular() {
		return circular;
	}

	/**
	 * @param circular the circular to set
	 */
	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}

	/**
	 * @return the circularVer
	 */
	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	/**
	 * @param circularVer the circularVer to set
	 */
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	/**
	 * @return the selectedCancelCircular
	 */
	public CtCancelCircular getSelectedCancelCircular() {
		return selectedCancelCircular;
	}

	/**
	 * @param selectedCancelCircular the selectedCancelCircular to set
	 */
	public void setSelectedCancelCircular(CtCancelCircular selectedCancelCircular) {
		this.selectedCancelCircular = selectedCancelCircular;
	}
	
	/**
	 * @return the supplierBankAccountMap
	 */
	public Map<String, SmSupplierBank> getSupplierBankAccountMap() {
		if (supplierBankAccountMap == null) {
			supplierBankAccountMap = new HashMap<String, SmSupplierBank>();
		}
		return supplierBankAccountMap;
	}
	
	/**
	 * @param supplierBankAccountMap the supplierBankAccountMap to set
	 */
	public void setSupplierBankAccountMap(Map<String, SmSupplierBank> supplierBankAccountMap) {
		this.supplierBankAccountMap = supplierBankAccountMap;
	}

	/**
	 * @return the selectedCancelledCircularList
	 */
	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}

	/**
	 * @param selectedCancelledCircularList the selectedCancelledCircularList to set
	 */
	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}

	/**
	 * @return the selectedPanelItems
	 */
	public CtPanelItem getSelectedPanelItems() {
		return selectedPanelItems;
	}

	/**
	 * @param selectedPanelItems the selectedPanelItems to set
	 */
	public void setSelectedPanelItems(CtPanelItem selectedPanelItems) {
		this.selectedPanelItems = selectedPanelItems;
	}
	
	/**
	 * @return the selectedItemForSpec
	 */
	public CtPanelItem getSelectedItemForSpec() {
		return selectedItemForSpec;
	}

	/**
	 * @param selectedItemForSpec the selectedItemForSpec to set
	 */
	public void setSelectedItemForSpec(CtPanelItem selectedItemForSpec) {
		this.selectedItemForSpec = selectedItemForSpec;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}



	/**
	 * @return the differentModule
	 */
	public boolean isDifferentModule() {
		return differentModule;
	}
	
	/**
	 * @param differentModule the differentModule to set
	 */
	public void setDifferentModule(boolean differentModule) {
		this.differentModule = differentModule;
	}

	/**
	 * @return the updateAction
	 */
	public boolean isUpdateAction() {
		return updateAction;
	}

	/**
	 * @param updateAction the updateAction to set
	 */
	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}

	/**
	 * @return the cancelCircularReq
	 */
	public CtCancelCircularReq getCancelCircularReq() {
		return cancelCircularReq;
	}

	/**
	 * @param cancelCircularReq the cancelCircularReq to set
	 */
	public void setCancelCircularReq(CtCancelCircularReq cancelCircularReq) {
		this.cancelCircularReq = cancelCircularReq;
	}

	/**
	 * @return the selectedCircularVerList
	 */
	public CtCircularVer[] getSelectedCircularVerList() {
		return selectedCircularVerList;
	}

	/**
	 * @param selectedCircularVerList the selectedCircularVerList to set
	 */
	public void setSelectedCircularVerList(CtCircularVer[] selectedCircularVerList) {
		this.selectedCircularVerList = selectedCircularVerList;
	}

	/**
	 * @return the circularVerList
	 */
	public List<CtCircularVer> getCircularVerList() {
		return circularVerList;
	}

	/**
	 * @param circularVerList the circularVerList to set
	 */
	public void setCircularVerList(List<CtCircularVer> circularVerList) {
		this.circularVerList = circularVerList;
	}

	/**
	 * @return the circularDocType
	 */
	public String getCircularDocType() {
		return circularDocType;
	}

	/**
	 * @param circularDocType the circularDocType to set
	 */
	public void setCircularDocType(String circularDocType) {
		this.circularDocType = circularDocType;
	}

	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the circularAdmin
	 */
	public String getCircularAdmin() {
		return circularAdmin;
	}

	/**
	 * @return the selectedPanelZone
	 */
	public CtPanelZone getSelectedPanelZone() {
		return selectedPanelZone;
	}

	/**
	 * @param selectedPanelZone the selectedPanelZone to set
	 */
	public void setSelectedPanelZone(CtPanelZone selectedPanelZone) {
		this.selectedPanelZone = selectedPanelZone;
	}

	/**
	 * @param circularAdmin the circularAdmin to set
	 */
	public void setCircularAdmin(String circularAdmin) {
		this.circularAdmin = circularAdmin;
	}

	/**
	 * @return the selectedContactPersons
	 */
	public List<CtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}
	
	/**
	 * @param selectedContactPersons the selectedContactPersons to set
	 */
	public void setSelectedContactPersons(List<CtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}

	/**
	 * @return the selectedCancelCirculars
	 */
	public List<CtCancelCircular> getSelectedCancelCirculars() {
		return selectedCancelCirculars;
	}

	/**
	 * @param selectedCancelCirculars the selectedCancelCirculars to set
	 */
	public void setSelectedCancelCirculars(List<CtCancelCircular> selectedCancelCirculars) {
		this.selectedCancelCirculars = selectedCancelCirculars;
	}

	/**
	 * @return the cancelledCircularList
	 */
	public List<CtCancelCircular> getCancelledCircularList() {
		if(cancelledCircularList == null)
			cancelledCircularList = new ArrayList<CtCancelCircular>();
		return cancelledCircularList;
	}

	/**
	 * @param cancelledCircularList the cancelledCircularList to set
	 */
	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}

	/**
	 * @return the selectionOfZone
	 */
	public boolean isSelectionOfZone() {
		return selectionOfZone;
	}

	/**
	 * @param selectionOfZone the selectionOfZone to set
	 */
	public void setSelectionOfZone(boolean selectionOfZone) {
		this.selectionOfZone = selectionOfZone;
	}

	/**
	 * @return the panelSupplierList
	 */
	public List<CtPanelSupplier> getPanelSupplierList() {
		if(panelSupplierList == null)
			panelSupplierList = new ArrayList<CtPanelSupplier>();
		return panelSupplierList;
	}

	/**
	 * @param panelSupplierList the panelSupplierList to set
	 */
	public void setPanelSupplierList(List<CtPanelSupplier> panelSupplierList) {
		this.panelSupplierList = panelSupplierList;
	}

	/**
	 * @return the selectedPanelSuppliers
	 */
	public List<CtPanelSupplier> getSelectedPanelSuppliers() {
		return selectedPanelSuppliers;
	}

	/**
	 * @param selectedPanelSuppliers the selectedPanelSuppliers to set
	 */
	public void setSelectedPanelSuppliers(
			List<CtPanelSupplier> selectedPanelSuppliers) {
		this.selectedPanelSuppliers = selectedPanelSuppliers;
	}
	
	/**
	 * @return the selectedPanelItemSpecs
	 */
	public List<CtPanelItemSpec> getSelectedPanelItemSpecs() {
		return selectedPanelItemSpecs;
	}

	/**
	 * @param selectedPanelItemSpecs the selectedPanelItemSpecs to set
	 */
	public void setSelectedPanelItemSpecs(List<CtPanelItemSpec> selectedPanelItemSpecs) {
		this.selectedPanelItemSpecs = selectedPanelItemSpecs;
	}

	/**
	 * @return the rootZoanlTree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * @param rootZoanlTree the rootZoanlTree to set
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the selectedGeneralTreeNodes
	 */
	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	/**
	 * @param selectedGeneralTreeNodes the selectedGeneralTreeNodes to set
	 */
	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}
	
	/**
	 * @return the editZone
	 */
	public boolean isEditZone() {
		return editZone;
	}

	/**
	 * @param editZone the editZone to set
	 */
	public void setEditZone(boolean editZone) {
		this.editZone = editZone;
	}

	/**
	 * @return the buttonHideActive
	 */
	public boolean isButtonHideActive() {
		return buttonHideActive;
	}

	/**
	 * @param buttonHideActive the buttonHideActive to set
	 */
	public void setButtonHideActive(boolean buttonHideActive) {
		this.buttonHideActive = buttonHideActive;
	}

	/**
	 * @return the itemCategoryId
	 */
	public Long getItemCategoryId() {
		return itemCategoryId;
	}

	/**
	 * @param itemCategoryId the itemCategoryId to set
	 */
	public void setItemCategoryId(Long itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	/**
	 * @return the panelCoverageId
	 */
	public Long getPanelCoverageId() {
		return panelCoverageId;
	}

	/**
	 * @param panelCoverageId the panelCoverageId to set
	 */
	public void setPanelCoverageId(Long panelCoverageId) {
		this.panelCoverageId = panelCoverageId;
	}

	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
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

	/**
	 * @return the supplierList
	 */
	public List<SmSupplier> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<SmSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the circularExtension
	 */
	public boolean isCircularExtension() {
		return circularExtension;
	}

	/**
	 * @param circularExtension the circularExtension to set
	 */
	public void setCircularExtension(boolean circularExtension) {
		this.circularExtension = circularExtension;
	}

	/**
	 * @return the chkCancelPage
	 */
	public boolean isChkCancelPage() {
		return chkCancelPage;
	}

	/**
	 * @param chkCancelPage the chkCancelPage to set
	 */
	public void setChkCancelPage(boolean chkCancelPage) {
		this.chkCancelPage = chkCancelPage;
	}
	
	/**
	 * @return the viewContract
	 */
	public boolean isViewContract() {
		return viewContract;
	}
	
	/**
	 * @param viewContract the viewContract to set
	 */
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}

	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the gotoPreviewPage
	 */
	public String getGotoPreviewPage() {
		return gotoPreviewPage;
	}
	
	/**
	 * @param gotoPreviewPage the gotoPreviewPage to set
	 */
	public void setGotoPreviewPage(String gotoPreviewPage) {
		this.gotoPreviewPage = gotoPreviewPage;
	}	

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	/**
	 * @return the fromCircularNotice
	 */
	public boolean isFromCircularNotice() {
		return fromCircularNotice;
	}

	/**
	 * @param fromCircularNotice the fromCircularNotice to set
	 */
	public void setFromCircularNotice(boolean fromCircularNotice) {
		this.fromCircularNotice = fromCircularNotice;
	}
		
	/**
	 * @return the supplierBranch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * @param supplierBranch the supplierBranch to set
	 */
	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the smCompanyBasic
	 */
	public SmCompanyBasic getSmCompanyBasic() {
		return smCompanyBasic;
	}

	/**
	 * @param smCompanyBasic the smCompanyBasic to set
	 */
	public void setSmCompanyBasic(SmCompanyBasic smCompanyBasic) {
		this.smCompanyBasic = smCompanyBasic;
	}

	/**
	 * @return the smPersonnel
	 */
	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	/**
	 * @param smPersonnel the smPersonnel to set
	 */
	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	/**
	 * @return the supplierAddress
	 */
	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}
	
	/**
	 * @return the ctCircularPidReasons
	 */
	public List<CtCircularPidReason> getCtCircularPidReasons() {
		return ctCircularPidReasons;
	}

	/**
	 * @param ctCircularPidReasons the ctCircularPidReasons to set
	 */
	public void setCtCircularPidReasons(
			List<CtCircularPidReason> ctCircularPidReasons) {
		this.ctCircularPidReasons = ctCircularPidReasons;
	}

	public boolean isButtonHideActiveDel() {
		return buttonHideActiveDel;
	}

	public void setButtonHideActiveDel(boolean buttonHideActiveDel) {
		this.buttonHideActiveDel = buttonHideActiveDel;
	}
}

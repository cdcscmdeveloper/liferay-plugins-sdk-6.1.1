/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.cc;

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
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.cc.CtCircularItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Central Contract Circular Managed Bean
 * 
 * @author alam
 * @since 9/10/2013
 */
@ManagedBean
@SessionScoped
public class CentralContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String fileDirectory = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/Circulars/"+ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR;

	private String circularAdmin;
	private String action;
	private String searchContractNo;
	private String searchContractName;
	private String textBeforeSign;
	private String gotoPreviewPage;	
	private String orgTypeCode;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	
	private int selectedTabIndex;
	private Long ministryProfileId;
	private Long officerProfileId;
	private Long jabatanProfileId;
	private Long ptjProfileId;
	private boolean exmptedMinistryDD;
	
	private boolean updateAction = false;
	private boolean viewContract = false;
	private boolean chkItemZone;
	private boolean chkItemPrice;
	private boolean chkCancelPage;
	private boolean chkCentralCircular;
	private boolean fromCircularNotice;
	private boolean autoUpdate = true;
	private boolean circularExtension;
	private boolean ButtonHideActive = true;
	private boolean buttonHideActiveDel = true;
	 private boolean selectAll;
	
	private TreeNode centralZoneTrees;
	private PmDigiSign pmDigiSign;
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCircularContract selectedCircularContract;
	private CtCircularItem selectedCircularItem;
	private CtContractZone selectedZoneItems;
	private List<CtContract> contractList;
	private List<CtContract> selectedContractList;
	private List<CtContactPerson> selectedContactPersons;
	private List<CtExemptedOrg> selectedExemptedOrgs;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private List<CtCancelCircular> selectedCancelledCircularList;	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<PmOrgProfile> orgProfiles; 
	private List<CtContractZone> contractItemZones;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;	
	private Map<CtContractZone, List<CtItemPrice>> zonePriceMap;
	private Map<Long, PmUser> userMap;
	private List<CtCircularPidReason> ctCircularPidReasons;
	
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
	 * @return the searchContractNo
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}
	
	/**
	 * @param searchContractNo the searchContractNo to set
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	
	/**
	 * @return the searchContractName
	 */
	public String getSearchContractName() {
		return searchContractName;
	}
	
	/**
	 * @param searchContractName the searchContractName to set
	 */
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}
	
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
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		if(contractList==null) {
			contractList = new ArrayList<CtContract>();
		}
		return contractList;
	}
	
	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
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
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
	/**
	 * @return the userGroupMap
	 */
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}
	
	/**
	 * @param userGroupMap the userGroupMap to set
	 */
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}
	
	/**
	 * @return the circularAdmin
	 */
	public String getCircularAdmin() {
		return circularAdmin;
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
	 * @return the selectedContractList
	 */
	public List<CtContract> getSelectedContractList() {
		return selectedContractList;
	}
	
	/**
	 * @param selectedContractList the selectedContractList to set
	 */
	public void setSelectedContractList(List<CtContract> selectedContractList) {
		this.selectedContractList = selectedContractList;
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
	 * @return the cancelledCircularList
	 */
	public List<CtCancelCircular> getCancelledCircularList() {
		return cancelledCircularList;
	}
	
	/**
	 * @param cancelledCircularList the cancelledCircularList to set
	 */
	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}
	
	/**
	 * @return the selectedExemptedOrgs
	 */
	public List<CtExemptedOrg> getSelectedExemptedOrgs() {
		return selectedExemptedOrgs;
	}
	
	/**
	 * @param selectedExemptedOrgs the selectedExemptedOrgs to set
	 */
	public void setSelectedExemptedOrgs(List<CtExemptedOrg> selectedExemptedOrgs) {
		this.selectedExemptedOrgs = selectedExemptedOrgs;
	}
	
	/**
	 * @return the selectedCircularContract
	 */
	public CtCircularContract getSelectedCircularContract() {
		return selectedCircularContract;
	}
	
	/**
	 * @param selectedCircularContract the selectedCircularContract to set
	 */
	public void setSelectedCircularContract(CtCircularContract selectedCircularContract) {
		this.selectedCircularContract = selectedCircularContract;
	}
	
	/**
	 * @return the selectedCircularItem
	 */
	public CtCircularItem getSelectedCircularItem() {
		return selectedCircularItem;
	}
	
	/**
	 * @param selectedCircularItem the selectedCircularItem to set
	 */
	public void setSelectedCircularItem(CtCircularItem selectedCircularItem) {
		this.selectedCircularItem = selectedCircularItem;
	}
	
	/**
	 * @return the selectedZoneItems
	 */
	public CtContractZone getSelectedZoneItems() {
		return selectedZoneItems;
	}
	
	/**
	 * @param selectedZoneItems the selectedZoneItems to set
	 */
	public void setSelectedZoneItems(CtContractZone selectedZoneItems) {
		this.selectedZoneItems = selectedZoneItems;
	}
	
	/**
	 * @return the zonePriceMap
	 */
	public Map<CtContractZone, List<CtItemPrice>> getZonePriceMap() {
		if(zonePriceMap==null) {
			zonePriceMap = new HashMap<CtContractZone, List<CtItemPrice>>();
		}
		return zonePriceMap;
	}
	
	/**
	 * @param zonePriceMap the zonePriceMap to set
	 */
	public void setZonePriceMap(Map<CtContractZone, List<CtItemPrice>> zonePriceMap) {
		this.zonePriceMap = zonePriceMap;
	}
	
	/**
	 * @return the chkItemZone
	 */
	public boolean isChkItemZone() {
		return chkItemZone;
	}
	
	/**
	 * @param chkItemZone the chkItemZone to set
	 */
	public void setChkItemZone(boolean chkItemZone) {
		this.chkItemZone = chkItemZone;
	}
	
	/**
	 * @return the chkItemPrice
	 */
	public boolean isChkItemPrice() {
		return chkItemPrice;
	}
	
	/**
	 * @param chkItemPrice the chkItemPrice to set
	 */
	public void setChkItemPrice(boolean chkItemPrice) {
		this.chkItemPrice = chkItemPrice;
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
	 * @return the centralZoneTrees
	 */
	public TreeNode getCentralZoneTrees() {
		return centralZoneTrees;
	}
	
	/**
	 * @param centralZoneTrees the centralZoneTrees to set
	 */
	public void setCentralZoneTrees(TreeNode centralZoneTrees) {
		this.centralZoneTrees = centralZoneTrees;
	}
	
	/**
	 * @return the contractItemZones
	 */
	public List<CtContractZone> getContractItemZones() {
		return contractItemZones;
	}
	
	/**
	 * @param contractItemZones the contractItemZones to set
	 */
	public void setContractItemZones(List<CtContractZone> contractItemZones) {
		this.contractItemZones = contractItemZones;
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
	 * @return the orgProfiles
	 */
	public List<PmOrgProfile> getOrgProfiles() {
		return orgProfiles;
	}
	
	/**
	 * @param orgProfiles the orgProfiles to set
	 */
	public void setOrgProfiles(List<PmOrgProfile> orgProfiles) {
		this.orgProfiles = orgProfiles;
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
	 * @return the chkCentralCircular
	 */
	public boolean isChkCentralCircular() {
		return chkCentralCircular;
	}

	/**
	 * @param chkCentralCircular the chkCentralCircular to set
	 */
	public void setChkCentralCircular(boolean chkCentralCircular) {
		this.chkCentralCircular = chkCentralCircular;
	}

	/**
	 * @return the orgTypeCode
	 */
	public String getOrgTypeCode() {
		return orgTypeCode;
	}

	/**
	 * @param orgTypeCode the orgTypeCode to set
	 */
	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}

	/**
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	/**
	 * @return the ptjProfileId
	 */
	public Long getPtjProfileId() {
		return ptjProfileId;
	}

	/**
	 * @param ptjProfileId the ptjProfileId to set
	 */
	public void setPtjProfileId(Long ptjProfileId) {
		this.ptjProfileId = ptjProfileId;
	}

	/**
	 * @return the exmptedMinistryDD
	 */
	public boolean isExmptedMinistryDD() {
		return exmptedMinistryDD;
	}

	/**
	 * @param exmptedMinistryDD the exmptedMinistryDD to set
	 */
	public void setExmptedMinistryDD(boolean exmptedMinistryDD) {
		this.exmptedMinistryDD = exmptedMinistryDD;
	}

	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}

	/**
	 * @return the officerProfileId
	 */
	public Long getOfficerProfileId() {
		return officerProfileId;
	}

	/**
	 * @param officerProfileId the officerProfileId to set
	 */
	public void setOfficerProfileId(Long officerProfileId) {
		this.officerProfileId = officerProfileId;
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

	public boolean isButtonHideActive() {
		return ButtonHideActive;
	}

	public void setButtonHideActive(boolean buttonHideActive) {
		ButtonHideActive = buttonHideActive;
	}

	public boolean isButtonHideActiveDel() {
		return buttonHideActiveDel;
	}

	public void setButtonHideActiveDel(boolean buttonHideActiveDel) {
		this.buttonHideActiveDel = buttonHideActiveDel;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
}
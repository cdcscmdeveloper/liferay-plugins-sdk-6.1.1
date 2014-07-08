/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 * Circular Panel Managed Bean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@SessionScoped
public class PanelManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String circularDocType;
	private String circularAdmin;
	private String action;
	
	private boolean updateAction;
	
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCancelCircular selectedCancelCircular;
	private CtCancelCircularReq cancelCircularReq;
	
	private List<CtContactPerson> selectedContactPersons;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private CtCircularVer[] selectedCircularVerList;
	private List<CtCircularVer> circularVerList;
	private List<CtCancelCircular> selectedCancelledCircularList;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private DualListModel<String> approverModel;

	// Search
	private String searchCircularNo;
	private String searchCircularName;
	private String searchFileRefNo;
	private String searchSupplierName;
	private String searchItemName;
	private Date searchStartDate;
	private Date searchEndDate;	
	private Long searchStatusId;
	
	private CtPanelItem selectedPanelItems;
	private CtPanelZone selectedPanelZone;
	private boolean selectionOfZone;
	private List<CtPanelSupplier> panelSupplierList;
	private CtPanelSupplier selectedPanelSupplier;
	
	private TreeNode rootZoanlTree;
	private String zoneName;
	private TreeNode[] selectedGeneralTreeNodes;
	private int index = -1;
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();
	private TreeNode rootLocationTree;
	private Map<String, PmUser> userMap;
	private boolean editZone;
	
	private TreeNode selectedZoneTree;
	private boolean buttonHideActive;
	private Long itemCategoryId;
	private Long panelCoverageId;
	
	private int selectedTabIndex;
	
	private String fileDirectory = CommonConstants.BASE_FILE_DIR+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR;
	
	public CtCircular getCircular() {
		return circular;
	}

	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}

	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	public CtCancelCircular getSelectedCancelCircular() {
		return selectedCancelCircular;
	}

	public void setSelectedCancelCircular(CtCancelCircular selectedCancelCircular) {
		this.selectedCancelCircular = selectedCancelCircular;
	}

	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}

	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}

	

	public CtPanelItem getSelectedPanelItems() {
		return selectedPanelItems;
	}

	public void setSelectedPanelItems(CtPanelItem selectedPanelItems) {
		this.selectedPanelItems = selectedPanelItems;
	}

	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	public Date getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public Date getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	public String getSearchCircularName() {
		return searchCircularName;
	}

	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}

	public String getSearchFileRefNo() {
		return searchFileRefNo;
	}

	public void setSearchFileRefNo(String searchFileRefNo) {
		this.searchFileRefNo = searchFileRefNo;
	}

	public String getSearchSupplierName() {
		return searchSupplierName;
	}

	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}

	public String getSearchItemName() {
		return searchItemName;
	}

	public void setSearchItemName(String searchItemName) {
		this.searchItemName = searchItemName;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public boolean isUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}

	public CtCancelCircularReq getCancelCircularReq() {
		return cancelCircularReq;
	}

	public void setCancelCircularReq(CtCancelCircularReq cancelCircularReq) {
		this.cancelCircularReq = cancelCircularReq;
	}

	public CtCircularVer[] getSelectedCircularVerList() {
		return selectedCircularVerList;
	}

	public void setSelectedCircularVerList(CtCircularVer[] selectedCircularVerList) {
		this.selectedCircularVerList = selectedCircularVerList;
	}

	public List<CtCircularVer> getCircularVerList() {
		return circularVerList;
	}

	public void setCircularVerList(List<CtCircularVer> circularVerList) {
		this.circularVerList = circularVerList;
	}

	public String getCircularDocType() {
		return circularDocType;
	}

	public void setCircularDocType(String circularDocType) {
		this.circularDocType = circularDocType;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getCircularAdmin() {
		return circularAdmin;
	}

	public CtPanelZone getSelectedPanelZone() {
		return selectedPanelZone;
	}

	public void setSelectedPanelZone(CtPanelZone selectedPanelZone) {
		this.selectedPanelZone = selectedPanelZone;
	}

	public void setCircularAdmin(String circularAdmin) {
		this.circularAdmin = circularAdmin;
	}

	public List<CtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}

	public void setSelectedContactPersons(List<CtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}

	public List<CtCancelCircular> getSelectedCancelCirculars() {
		return selectedCancelCirculars;
	}

	public void setSelectedCancelCirculars(List<CtCancelCircular> selectedCancelCirculars) {
		this.selectedCancelCirculars = selectedCancelCirculars;
	}

	public List<CtCancelCircular> getCancelledCircularList() {
		return cancelledCircularList;
	}

	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}

	public boolean isSelectionOfZone() {
		return selectionOfZone;
	}

	public void setSelectionOfZone(boolean selectionOfZone) {
		this.selectionOfZone = selectionOfZone;
	}

	public List<CtPanelSupplier> getPanelSupplierList() {
		if(panelSupplierList == null)
			panelSupplierList = new ArrayList<CtPanelSupplier>();
		return panelSupplierList;
	}

	public void setPanelSupplierList(List<CtPanelSupplier> panelSupplierList) {
		this.panelSupplierList = panelSupplierList;
	}

	public CtPanelSupplier getSelectedPanelSupplier() {
		return selectedPanelSupplier;
	}

	public void setSelectedPanelSupplier(CtPanelSupplier selectedPanelSupplier) {
		this.selectedPanelSupplier = selectedPanelSupplier;
	}

	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}
	
	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}

	public TreeNode getSelectedZoneTree() {
		return selectedZoneTree;
	}

	public void setSelectedZoneTree(TreeNode selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	public boolean isEditZone() {
		return editZone;
	}

	public void setEditZone(boolean editZone) {
		this.editZone = editZone;
	}

	public boolean isButtonHideActive() {
		return buttonHideActive;
	}

	public void setButtonHideActive(boolean buttonHideActive) {
		this.buttonHideActive = buttonHideActive;
	}

	public Long getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Long itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public Long getPanelCoverageId() {
		return panelCoverageId;
	}

	public void setPanelCoverageId(Long panelCoverageId) {
		this.panelCoverageId = panelCoverageId;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
}

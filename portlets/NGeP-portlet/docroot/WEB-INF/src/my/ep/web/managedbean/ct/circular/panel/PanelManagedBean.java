/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelState;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

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
	
	private boolean updateAction;
	
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtContactPerson selectedContactPerson;
	private CtPanelSupplier selectedPanelSupplier;
	private CtCancelCircular selectedCancelCircular;
	private CtPanelZone selectedPanelZone;
	private CtPanelState selectedPanelState;
	private CtPanelItem selectedPanelItem;
	private CtCancelCircularReq cancelCircularReq;
	
	private CtCircularVer[] selectedCircularVerList;
	private List<CtCircularVer> circularVerList;
	private List<CtCancelCircular> selectedCancelledCircularList;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private DualListModel<String> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;

	// Search
	private String searchCircularNo;
	private String searchCircularName;
	private String searchFileRefNo;
	private String searchSupplierName;
	private String searchItemName;
	private Date searchStartDate;
	private Date searchEndDate;	
	private Long searchCircularTypeCodeId;
	private Long searchStatusId;

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

	public CtContactPerson getSelectedContactPerson() {
		return selectedContactPerson;
	}

	public void setSelectedContactPerson(CtContactPerson selectedContactPerson) {
		this.selectedContactPerson = selectedContactPerson;
	}

	public CtPanelSupplier getSelectedPanelSupplier() {
		return selectedPanelSupplier;
	}

	public void setSelectedPanelSupplier(CtPanelSupplier selectedPanelSupplier) {
		this.selectedPanelSupplier = selectedPanelSupplier;
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

	public CtPanelZone getSelectedPanelZone() {
		return selectedPanelZone;
	}

	public void setSelectedPanelZone(CtPanelZone selectedPanelZone) {
		this.selectedPanelZone = selectedPanelZone;
	}

	public CtPanelState getSelectedPanelState() {
		return selectedPanelState;
	}

	public void setSelectedPanelState(CtPanelState selectedPanelState) {
		this.selectedPanelState = selectedPanelState;
	}

	public CtPanelItem getSelectedPanelItem() {
		return selectedPanelItem;
	}

	public void setSelectedPanelItem(CtPanelItem selectedPanelItem) {
		this.selectedPanelItem = selectedPanelItem;
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

	public Long getSearchCircularTypeCodeId() {
		return searchCircularTypeCodeId;
	}

	public void setSearchCircularTypeCodeId(Long searchCircularTypeCodeId) {
		this.searchCircularTypeCodeId = searchCircularTypeCodeId;
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

	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}

}

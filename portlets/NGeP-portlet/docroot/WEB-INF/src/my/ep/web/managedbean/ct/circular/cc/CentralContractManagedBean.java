/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.cc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.cc.CtCircularItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

/**
 * Central Contract Circular Managed Bean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@SessionScoped
public class CentralContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String circularAdmin;
	
	private boolean updateAction;
	private boolean updateTypeDisable;
	
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCircularContract selectedCircularContract;
	private CtCircularItem selectedCircularItem;

	private List<CtContract> contractList;
	private List<CtContract> selectedContractList;
	private List<CtContactPerson> selectedContactPersons;
	private List<CtExemptedOrg> selectedExemptedOrgs;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private List<CtCancelCircular> selectedCancelledCircularList;	
	private DualListModel<String> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;	
	
	// Search
	private String searchCircularNo;
	private String searchCircularName;
	private String searchFileRefNo;
	private Date searchStartDate;
	private Date searchEndDate;	
	private Long searchStatusId;
	private String searchContractNo;
	private String searchContractName;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	public boolean isUpdateAction() {
		return updateAction;
	}
	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}
	public CtCircularVer getCircularVer() {
		return circularVer;
	}
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
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
	public Long getSearchStatusId() {
		return searchStatusId;
	}
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}
	public String getSearchContractNo() {
		return searchContractNo;
	}
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	public String getSearchContractName() {
		return searchContractName;
	}
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}
	public CtCircular getCircular() {
		return circular;
	}
	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}
	public List<CtContract> getContractList() {
		if(contractList==null) {
			contractList = new ArrayList<CtContract>();
		}
		return contractList;
	}
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
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
	public String getCircularAdmin() {
		return circularAdmin;
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
	public List<CtContract> getSelectedContractList() {
		return selectedContractList;
	}
	public void setSelectedContractList(List<CtContract> selectedContractList) {
		this.selectedContractList = selectedContractList;
	}
	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}
	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}
	public List<CtCancelCircular> getCancelledCircularList() {
		return cancelledCircularList;
	}
	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}
	public boolean isUpdateTypeDisable() {
		return updateTypeDisable;
	}
	public void setUpdateTypeDisable(boolean updateTypeDisable) {
		this.updateTypeDisable = updateTypeDisable;
	}
	
	public List<CtExemptedOrg> getSelectedExemptedOrgs() {
		return selectedExemptedOrgs;
	}
	public void setSelectedExemptedOrgs(List<CtExemptedOrg> selectedExemptedOrgs) {
		this.selectedExemptedOrgs = selectedExemptedOrgs;
	}
	
	public CtCircularContract getSelectedCircularContract() {
		return selectedCircularContract;
	}
	public void setSelectedCircularContract(CtCircularContract selectedCircularContract) {
		this.selectedCircularContract = selectedCircularContract;
	}
	public CtCircularItem getSelectedCircularItem() {
		return selectedCircularItem;
	}
	public void setSelectedCircularItem(CtCircularItem selectedCircularItem) {
		this.selectedCircularItem = selectedCircularItem;
	}
	
	
}
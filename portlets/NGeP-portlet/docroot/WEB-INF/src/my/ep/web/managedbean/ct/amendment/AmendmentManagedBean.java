/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.amendment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Amendment
 * 
 * @author Alam
 * @since 1/13/2014	 
 */
@ManagedBean(name ="amendmentManagedBean")
@SessionScoped
public class AmendmentManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private CtContractItem selectedItem;
	
	private String taskName;
	private String[] action;
	private String processName;
	
	private boolean pbConfirmMeeting;
	private boolean memberSignOff = true;
	private boolean letterOfAcceptance;
	private boolean bpkSaSkApprover;
	private boolean prepareLOA;
	private boolean signOffDecision;
	private boolean signUpdateDecision;
	private String ackFA;
	
	private CtContract ctContract;
	private CtContractVer contractVer;
	
	private String itemType;
	private boolean serviceItem;
	private boolean productAndService;
	private boolean productItem = true;
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private String showItem ="Item  Pricing";
	private List<CtItemPrice> itemPriceList;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private double secondValue =0.0;
	private PmUser userLogin;
	private CtContractItem selectedServiceItem;
	
	private List<CtContractVer> amendHistoryList;
	private CtPerformanceBond ctPerformanceBond;
	private String supplierAddress;
	private boolean directPurchase;
	private boolean chkClause;
	private boolean chkAmendOtherValue;
	private boolean chkAmendExtendValue;
	private boolean chkAmendAllValue;
	private String supplierLocation;
	private TreeNode ctZoneTrees;
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	private List<CtContractItem> ctItemList;
	private CtItemPrice ctItemPrices;
	private CtItemCeilingQty ctItemCeilingQtys;
	private boolean chkConceValue;
	private CtItemPrice selectedIndexOfPrice;
	private CtItemCeilingQty selectedIndexOfCeilingQty;
	
	private CtCommitteeMember ctCommitteeMember;
	private boolean existingUsers = true;
	private boolean newUsers;
	private String selectedRadioButton="1";
	private String name = null;
	private String identificationNo = null;
	private String identificationType;
	private List<PmUser> externalUsers;
	private SelectItem[] roleTypes; // Role type drop down
	private List<CtCommitteeMember> ctCommitteeMembers;
	private Long meetingStatusId;
	private ScLoaWitness scLoaWitness = new ScLoaWitness();
	private String witnessHeader;
	
	private SmSupplierBank supplierBank;
	private String supplierBankAddress;
	
	public CtContractVer getContractVer() {
		return contractVer;
	}

	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public boolean isMemberSignOff() {
		return memberSignOff;
	}

	public void setMemberSignOff(boolean memberSignOff) {
		this.memberSignOff = memberSignOff;
	}

	public boolean isLetterOfAcceptance() {
		return letterOfAcceptance;
	}

	public void setLetterOfAcceptance(boolean letterOfAcceptance) {
		this.letterOfAcceptance = letterOfAcceptance;
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
		if(attachmentList == null){
			attachmentList = new ArrayList<CtAttachment>();		
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean isServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(boolean serviceItem) {
		this.serviceItem = serviceItem;
	}

	public boolean isProductItem() {
		return productItem;
	}

	public void setProductItem(boolean productItem) {
		this.productItem = productItem;
	}

	public boolean isProductAndService() {
		return productAndService;
	}

	public void setProductAndService(boolean productAndService) {
		this.productAndService = productAndService;
	}

	public String getShowItem() {
		return showItem;
	}

	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}

	public boolean isItemPricing() {
		return itemPricing;
	}

	public void setItemPricing(boolean itemPricing) {
		this.itemPricing = itemPricing;
	}

	public boolean isItemCeilingQuantity() {
		return itemCeilingQuantity;
	}

	public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
		this.itemCeilingQuantity = itemCeilingQuantity;
	}

	public List<CtItemPrice> getItemPriceList() {
		if(itemPriceList == null)
			itemPriceList = new ArrayList<CtItemPrice>();
		return itemPriceList;
	}

	public void setItemPriceList(List<CtItemPrice> itemPriceList) {
		this.itemPriceList = itemPriceList;
	}

	public double getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(double secondValue) {
		this.secondValue = secondValue;
	}

	public PmUser getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(PmUser userLogin) {
		this.userLogin = userLogin;
	}

	public List<CtContractVer> getAmendHistoryList() {
		return amendHistoryList;
	}

	public void setAmendHistoryList(List<CtContractVer> amendHistoryList) {
		this.amendHistoryList = amendHistoryList;
	}

	public CtContractItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public CtContractItem getSelectedServiceItem() {
		return selectedServiceItem;
	}

	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		this.selectedServiceItem = selectedServiceItem;
	}

	public CtPerformanceBond getCtPerformanceBond() {
		return ctPerformanceBond;
	}

	public void setCtPerformanceBond(CtPerformanceBond ctPerformanceBond) {
		this.ctPerformanceBond = ctPerformanceBond;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public boolean isDirectPurchase() {
		return directPurchase;
	}

	public void setDirectPurchase(boolean directPurchase) {
		this.directPurchase = directPurchase;
	}

	public boolean isChkClause() {
		return chkClause;
	}

	public void setChkClause(boolean chkClause) {
		this.chkClause = chkClause;
	}

	public boolean isChkAmendOtherValue() {
		return chkAmendOtherValue;
	}

	public void setChkAmendOtherValue(boolean chkAmendOtherValue) {
		this.chkAmendOtherValue = chkAmendOtherValue;
	}

	public boolean isChkAmendExtendValue() {
		return chkAmendExtendValue;
	}

	public void setChkAmendExtendValue(boolean chkAmendExtendValue) {
		this.chkAmendExtendValue = chkAmendExtendValue;
	}

	public boolean isChkAmendAllValue() {
		return chkAmendAllValue;
	}

	public void setChkAmendAllValue(boolean chkAmendAllValue) {
		this.chkAmendAllValue = chkAmendAllValue;
	}

	public String getSupplierLocation() {
		return supplierLocation;
	}

	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}
	
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
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

	public List<CtContractItem> getCtItemList() {
		if(ctItemList == null)
			ctItemList = new ArrayList<CtContractItem>();
		return ctItemList;
	}

	public void setCtItemList(List<CtContractItem> ctItemList) {
		this.ctItemList = ctItemList;
	}

	public CtItemPrice getCtItemPrices() {
		return ctItemPrices;
	}

	public void setCtItemPrices(CtItemPrice ctItemPrices) {
		this.ctItemPrices = ctItemPrices;
	}

	public CtItemCeilingQty getCtItemCeilingQtys() {
		return ctItemCeilingQtys;
	}

	public void setCtItemCeilingQtys(CtItemCeilingQty ctItemCeilingQtys) {
		this.ctItemCeilingQtys = ctItemCeilingQtys;
	}

	public boolean isChkConceValue() {
		return chkConceValue;
	}

	public void setChkConceValue(boolean chkConceValue) {
		this.chkConceValue = chkConceValue;
	}

	public CtItemPrice getSelectedIndexOfPrice() {
		return selectedIndexOfPrice;
	}

	public void setSelectedIndexOfPrice(CtItemPrice selectedIndexOfPrice) {
		this.selectedIndexOfPrice = selectedIndexOfPrice;
	}

	public CtItemCeilingQty getSelectedIndexOfCeilingQty() {
		return selectedIndexOfCeilingQty;
	}

	public void setSelectedIndexOfCeilingQty(
			CtItemCeilingQty selectedIndexOfCeilingQty) {
		this.selectedIndexOfCeilingQty = selectedIndexOfCeilingQty;
	}

	public boolean isBpkSaSkApprover() {
		return bpkSaSkApprover;
	}

	public void setBpkSaSkApprover(boolean bpkSaSkApprover) {
		this.bpkSaSkApprover = bpkSaSkApprover;
	}

	public boolean isPrepareLOA() {
		return prepareLOA;
	}

	public void setPrepareLOA(boolean prepareLOA) {
		this.prepareLOA = prepareLOA;
	}

	public boolean isExistingUsers() {
		return existingUsers;
	}

	public void setExistingUsers(boolean existingUsers) {
		this.existingUsers = existingUsers;
	}

	public boolean isNewUsers() {
		return newUsers;
	}

	public void setNewUsers(boolean newUsers) {
		this.newUsers = newUsers;
	}

	public String getSelectedRadioButton() {
		return selectedRadioButton;
	}

	public void setSelectedRadioButton(String selectedRadioButton) {
		this.selectedRadioButton = selectedRadioButton;
	}

	public CtCommitteeMember getCtCommitteeMember() {
		return ctCommitteeMember;
	}

	public void setCtCommitteeMember(CtCommitteeMember ctCommitteeMember) {
		this.ctCommitteeMember = ctCommitteeMember;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public List<PmUser> getExternalUsers() {
		return externalUsers;
	}

	public void setExternalUsers(List<PmUser> externalUsers) {
		this.externalUsers = externalUsers;
	}

	public SelectItem[] getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
	}

	public List<CtCommitteeMember> getCtCommitteeMembers() {
		if(ctCommitteeMembers==null) {
			ctCommitteeMembers = new ArrayList<CtCommitteeMember>();
		}
		return ctCommitteeMembers;
	}

	public void setCtCommitteeMembers(List<CtCommitteeMember> ctCommitteeMembers) {
		this.ctCommitteeMembers = ctCommitteeMembers;
	}

	public boolean isPbConfirmMeeting() {
		return pbConfirmMeeting;
	}

	public void setPbConfirmMeeting(boolean pbConfirmMeeting) {
		this.pbConfirmMeeting = pbConfirmMeeting;
	}

	public boolean isSignOffDecision() {
		return signOffDecision;
	}

	public void setSignOffDecision(boolean signOffDecision) {
		this.signOffDecision = signOffDecision;
	}

	public Long getMeetingStatusId() {
		return meetingStatusId;
	}

	public void setMeetingStatusId(Long meetingStatusId) {
		this.meetingStatusId = meetingStatusId;
	}

	public boolean isSignUpdateDecision() {
		return signUpdateDecision;
	}

	public void setSignUpdateDecision(boolean signUpdateDecision) {
		this.signUpdateDecision = signUpdateDecision;
	}

	public ScLoaWitness getScLoaWitness() {
		return scLoaWitness;
	}

	public void setScLoaWitness(ScLoaWitness scLoaWitness) {
		this.scLoaWitness = scLoaWitness;
	}

	public String getWitnessHeader() {
		return witnessHeader;
	}

	public void setWitnessHeader(String witnessHeader) {
		this.witnessHeader = witnessHeader;
	}

	public String getAckFA() {
		return ackFA;
	}

	public void setAckFA(String ackFA) {
		this.ackFA = ackFA;
	}

	public SmSupplierBank getSupplierBank() {
		return supplierBank;
	}

	public void setSupplierBank(SmSupplierBank supplierBank) {
		this.supplierBank = supplierBank;
	}

	public String getSupplierBankAddress() {
		return supplierBankAddress;
	}

	public void setSupplierBankAddress(String supplierBankAddress) {
		this.supplierBankAddress = supplierBankAddress;
	}

}

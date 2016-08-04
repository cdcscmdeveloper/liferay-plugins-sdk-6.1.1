/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.amendment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;

import my.ep.constant.ct.ContractConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * ManagedBean For Contract Amendment
 * 
 * @author Alam
 * @since 1/13/2014	 
 */
@ManagedBean(name ="amendManagedBean")
@SessionScoped
public class AmendmentManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	//private String[] action;	
	private String taskName;
	private String ackFA;
	private String committeeName = null;
	private String identificationNo = null;
	private String identificationType;
	private String witnessHeader;
	private String witnessHeaderTooltip;
	private String editorValue;
	private String fileDirectory;
	private String pbQcApproved;
	private String textBeforeSign;
	private String buttonAction;
	private String committeeType;
	private String approverName;
	private Long meetingStatusId;
	private Long secretaryId;
	private Date newAmendmentEffDate;
	private int selectedTabIndex;
	private int attachFileIndex;
	private boolean itemProductExist;
	private boolean itemServiceExist;
	private boolean termAndConditionFlg;
	private boolean pbConfirmMeeting;
	private boolean memberSignOff;
	private boolean letterOfAcceptance;
	private boolean bpkSaSkApprover;
	private boolean prepareLOA;
	private boolean signOffDecision;
	private boolean signUpdateDecision;
	private boolean decisionPbQcLOA;
	private boolean confirmMeeting;
	private boolean selectAll;
	private boolean showPrintButton;
	private boolean bpmActionFlag = true;
	private boolean autoUpdate = true;	
	private boolean disableEndorseAmendDetails;
	private boolean disableFinalAmendDetails;
	private boolean disableMeetingDetails;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContractVer> amendHistoryList;
	private List<String> finalListOfSideMenu;
	private List<SmPersonnel> supplierList;
	private List<PmUser> bpkApproverList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
    private CtContractItem selectedServiceItem;
	private CtContractVer agreementContractVer;
	private CtItemMaxQty ctItemMaxQty;
	private CtItemPrice ctItemPrices;
	private CtItemCeilingQty ctItemCeilingQtys;
	private CtContractItem selectedItem;
	private CtItemPrice ctServiceItemPrices;
	private CtCommitteeMember newCommitteMembers;
	private ScLoaWitness scLoaWitness;
	private ScLoaReport selectedScLoaReports;
	private PmDigiSign pmDigiSign;
	
	@ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
	
	/**
	 * @return the action
	 *//*
	public String[] getAction() {
		return action;
	}
	
	*//**
	 * @param action the action to set
	 *//*
	public void setAction(String[] action) {
		this.action = action;
	}*/
	
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
	 * @return the ackFA
	 */
	public String getAckFA() {
		return ackFA;
	}
	
	/**
	 * @param ackFA the ackFA to set
	 */
	public void setAckFA(String ackFA) {
		this.ackFA = ackFA;
	}

	/**
	 * @return the committeeName
	 */
	public String getCommitteeName() {
		return committeeName;
	}
	
	/**
	 * @param committeeName the committeeName to set
	 */
	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}
	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}
	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	/**
	 * @return the identificationType
	 */
	public String getIdentificationType() {
		return identificationType;
	}
	/**
	 * @param identificationType the identificationType to set
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}
	/**
	 * @return the witnessHeader
	 */
	public String getWitnessHeader() {
		return witnessHeader;
	}
	/**
	 * @param witnessHeader the witnessHeader to set
	 */
	public void setWitnessHeader(String witnessHeader) {
		this.witnessHeader = witnessHeader;
	}
	/**
	 * @return the witnessHeaderTooltip
	 */
	public String getWitnessHeaderTooltip() {
		return witnessHeaderTooltip;
	}
	/**
	 * @param witnessHeaderTooltip the witnessHeaderTooltip to set
	 */
	public void setWitnessHeaderTooltip(String witnessHeaderTooltip) {
		this.witnessHeaderTooltip = witnessHeaderTooltip;
	}
	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}
	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
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
	 * @return the pbQcApproved
	 */
	public String getPbQcApproved() {
		return pbQcApproved;
	}
	/**
	 * @param pbQcApproved the pbQcApproved to set
	 */
	public void setPbQcApproved(String pbQcApproved) {
		this.pbQcApproved = pbQcApproved;
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
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}
	
	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}
	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}
	
	/**
	 * @param committeeType the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the meetingStatusId
	 */
	public Long getMeetingStatusId() {
		return meetingStatusId;
	}
	/**
	 * @param meetingStatusId the meetingStatusId to set
	 */
	public void setMeetingStatusId(Long meetingStatusId) {
		this.meetingStatusId = meetingStatusId;
	}
	
	/**
	 * @return the secretaryId
	 */
	public Long getSecretaryId() {
		return secretaryId;
	}
	
	/**
	 * @param secretaryId the secretaryId to set
	 */
	public void setSecretaryId(Long secretaryId) {
		this.secretaryId = secretaryId;
	}
	
	/**
	 * @return the newAmendmentEffDate
	 */
	public Date getNewAmendmentEffDate() {
		return newAmendmentEffDate;
	}

	/**
	 * @param newAmendmentEffDate the newAmendmentEffDate to set
	 */
	public void setNewAmendmentEffDate(Date newAmendmentEffDate) {
		this.newAmendmentEffDate = newAmendmentEffDate;
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
	 * @return the attachFileIndex
	 */
	public int getAttachFileIndex() {
		return attachFileIndex;
	}
	
	/**
	 * @param attachFileIndex the attachFileIndex to set
	 */
	public void setAttachFileIndex(int attachFileIndex) {
		this.attachFileIndex = attachFileIndex;
	}
	
	/**
	 * @return the itemProductExist
	 */
	public boolean isItemProductExist() {
		return itemProductExist;
	}
	
	/**
	 * @param itemProductExist the itemProductExist to set
	 */
	public void setItemProductExist(boolean itemProductExist) {
		this.itemProductExist = itemProductExist;
	}
	
	/**
	 * @return the itemServiceExist
	 */
	public boolean isItemServiceExist() {
		return itemServiceExist;
	}
	
	/**
	 * @param itemServiceExist the itemServiceExist to set
	 */
	public void setItemServiceExist(boolean itemServiceExist) {
		this.itemServiceExist = itemServiceExist;
	}
	
	/**
	 * @return the termAndConditionFlg
	 */
	public boolean isTermAndConditionFlg() {
		return termAndConditionFlg;
	}
	
	/**
	 * @param termAndConditionFlg the termAndConditionFlg to set
	 */
	public void setTermAndConditionFlg(boolean termAndConditionFlg) {
		this.termAndConditionFlg = termAndConditionFlg;
	}
	
	/**
	 * @return the pbConfirmMeeting
	 */
	public boolean isPbConfirmMeeting() {
		return pbConfirmMeeting;
	}
	
	/**
	 * @param pbConfirmMeeting the pbConfirmMeeting to set
	 */
	public void setPbConfirmMeeting(boolean pbConfirmMeeting) {
		this.pbConfirmMeeting = pbConfirmMeeting;
	}
	
	/**
	 * @return the memberSignOff
	 */
	public boolean isMemberSignOff() {
		return memberSignOff;
	}
	
	/**
	 * @param memberSignOff the memberSignOff to set
	 */
	public void setMemberSignOff(boolean memberSignOff) {
		this.memberSignOff = memberSignOff;
	}
	
	/**
	 * @return the letterOfAcceptance
	 */
	public boolean isLetterOfAcceptance() {
		return letterOfAcceptance;
	}
	
	/**
	 * @param letterOfAcceptance the letterOfAcceptance to set
	 */
	public void setLetterOfAcceptance(boolean letterOfAcceptance) {
		this.letterOfAcceptance = letterOfAcceptance;
	}
	
	/**
	 * @return the bpkSaSkApprover
	 */
	public boolean isBpkSaSkApprover() {
		return bpkSaSkApprover;
	}
	
	/**
	 * @param bpkSaSkApprover the bpkSaSkApprover to set
	 */
	public void setBpkSaSkApprover(boolean bpkSaSkApprover) {
		this.bpkSaSkApprover = bpkSaSkApprover;
	}
	
	/**
	 * @return the prepareLOA
	 */
	public boolean isPrepareLOA() {
		return prepareLOA;
	}
	
	/**
	 * @param prepareLOA the prepareLOA to set
	 */
	public void setPrepareLOA(boolean prepareLOA) {
		this.prepareLOA = prepareLOA;
	}
	
	/**
	 * @return the signOffDecision
	 */
	public boolean isSignOffDecision() {
		return signOffDecision;
	}
	
	/**
	 * @param signOffDecision the signOffDecision to set
	 */
	public void setSignOffDecision(boolean signOffDecision) {
		this.signOffDecision = signOffDecision;
	}
	
	/**
	 * @return the signUpdateDecision
	 */
	public boolean isSignUpdateDecision() {
		return signUpdateDecision;
	}
	
	/**
	 * @param signUpdateDecision the signUpdateDecision to set
	 */
	public void setSignUpdateDecision(boolean signUpdateDecision) {
		this.signUpdateDecision = signUpdateDecision;
	}
	
	/**
	 * @return the decisionPbQcLOA
	 */
	public boolean isDecisionPbQcLOA() {
		return decisionPbQcLOA;
	}
	
	/**
	 * @param decisionPbQcLOA the decisionPbQcLOA to set
	 */
	public void setDecisionPbQcLOA(boolean decisionPbQcLOA) {
		this.decisionPbQcLOA = decisionPbQcLOA;
	}
	
	/**
	 * @return the confirmMeeting
	 */
	public boolean isConfirmMeeting() {
		return confirmMeeting;
	}
	
	/**
	 * @param confirmMeeting the confirmMeeting to set
	 */
	public void setConfirmMeeting(boolean confirmMeeting) {
		this.confirmMeeting = confirmMeeting;
	}
	
	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}
	
	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	/**
	 * @return the showPrintButton
	 */
	public boolean isShowPrintButton() {
		return showPrintButton;
	}
	
	/**
	 * @param showPrintButton the showPrintButton to set
	 */
	public void setShowPrintButton(boolean showPrintButton) {
		this.showPrintButton = showPrintButton;
	}
	
	/**
	 * @return the bpmActionFlag
	 */
	public boolean isBpmActionFlag() {
		return bpmActionFlag;
	}
	
	/**
	 * @param bpmActionFlag the bpmActionFlag to set
	 */
	public void setBpmActionFlag(boolean bpmActionFlag) {
		this.bpmActionFlag = bpmActionFlag;
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
	 * @return the disableEndorseAmendDetails
	 */
	public boolean isDisableEndorseAmendDetails() {
		return disableEndorseAmendDetails;
	}

	/**
	 * @param disableEndorseAmendDetails the disableEndorseAmendDetails to set
	 */
	public void setDisableEndorseAmendDetails(boolean disableEndorseAmendDetails) {
		this.disableEndorseAmendDetails = disableEndorseAmendDetails;
	}

	/**
	 * @return the disableFinalAmendDetails
	 */
	public boolean isDisableFinalAmendDetails() {
		return disableFinalAmendDetails;
	}
	
	/**
	 * @param disableFinalAmendDetails the disableFinalAmendDetails to set
	 */
	public void setDisableFinalAmendDetails(boolean disableFinalAmendDetails) {
		this.disableFinalAmendDetails = disableFinalAmendDetails;
	}

	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
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
		if (attachmentList == null){
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
	 * @return the amendHistoryList
	 */
	public List<CtContractVer> getAmendHistoryList() {
		if (amendHistoryList == null) {
			amendHistoryList = new ArrayList<CtContractVer>();
		}
		return amendHistoryList;
	}
	
	/**
	 * @param amendHistoryList the amendHistoryList to set
	 */
	public void setAmendHistoryList(List<CtContractVer> amendHistoryList) {
		this.amendHistoryList = amendHistoryList;
	}
	
	/**
	 * @return the finalListOfSideMenu
	 */
	public List<String> getFinalListOfSideMenu() {
		return finalListOfSideMenu;
	}
	
	/**
	 * @param finalListOfSideMenu the finalListOfSideMenu to set
	 */
	public void setFinalListOfSideMenu(List<String> finalListOfSideMenu) {
		this.finalListOfSideMenu = finalListOfSideMenu;
	}
	
	/**
	 * @return the supplierList
	 */
	public List<SmPersonnel> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<SmPersonnel> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the bpkApproverList
	 */
	public List<PmUser> getBpkApproverList() {
		return bpkApproverList;
	}

	/**
	 * @param bpkApproverList the bpkApproverList to set
	 */
	public void setBpkApproverList(List<PmUser> bpkApproverList) {
		this.bpkApproverList = bpkApproverList;
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
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
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
	 * @return the selectedServiceItem
	 */
	public CtContractItem getSelectedServiceItem() {
		return selectedServiceItem;
	}
	
	/**
	 * @param selectedServiceItem the selectedServiceItem to set
	 */
	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		this.selectedServiceItem = selectedServiceItem;
	}
	
	/**
	 * @return the agreementContractVer
	 */
	public CtContractVer getAgreementContractVer() {
		return agreementContractVer;
	}

	/**
	 * @param agreementContractVer the agreementContractVer to set
	 */
	public void setAgreementContractVer(CtContractVer agreementContractVer) {
		this.agreementContractVer = agreementContractVer;
	}
	
	/**
	 * @return the ctItemMaxQty
	 */
	public CtItemMaxQty getCtItemMaxQty() {
		return ctItemMaxQty;
	}
	
	/**
	 * @param ctItemMaxQty the ctItemMaxQty to set
	 */
	public void setCtItemMaxQty(CtItemMaxQty ctItemMaxQty) {
		this.ctItemMaxQty = ctItemMaxQty;
	}
	
	/**
	 * @return the ctItemPrices
	 */
	public CtItemPrice getCtItemPrices() {
		return ctItemPrices;
	}
	
	/**
	 * @param ctItemPrices the ctItemPrices to set
	 */
	public void setCtItemPrices(CtItemPrice ctItemPrices) {
		this.ctItemPrices = ctItemPrices;
	}
	
	/**
	 * @return the ctItemCeilingQtys
	 */
	public CtItemCeilingQty getCtItemCeilingQtys() {
		return ctItemCeilingQtys;
	}
	
	/**
	 * @param ctItemCeilingQtys the ctItemCeilingQtys to set
	 */
	public void setCtItemCeilingQtys(CtItemCeilingQty ctItemCeilingQtys) {
		this.ctItemCeilingQtys = ctItemCeilingQtys;
	}
	
	/**
	 * @return the selectedItem
	 */
	public CtContractItem getSelectedItem() {
		return selectedItem;
	}
	
	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	/**
	 * @return the ctServiceItemPrices
	 */
	public CtItemPrice getCtServiceItemPrices() {
		return ctServiceItemPrices;
	}
	
	/**
	 * @param ctServiceItemPrices the ctServiceItemPrices to set
	 */
	public void setCtServiceItemPrices(CtItemPrice ctServiceItemPrices) {
		this.ctServiceItemPrices = ctServiceItemPrices;
	}
	
	/**
	 * @return the newCommitteMembers
	 */
	public CtCommitteeMember getNewCommitteMembers() {
		return newCommitteMembers;
	}
	
	/**
	 * @param newCommitteMembers the newCommitteMembers to set
	 */
	public void setNewCommitteMembers(CtCommitteeMember newCommitteMembers) {
		this.newCommitteMembers = newCommitteMembers;
	}
	
	/**
	 * @return the scLoaWitness
	 */
	public ScLoaWitness getScLoaWitness() {
		if (scLoaWitness == null) {
			scLoaWitness = new ScLoaWitness();
			String taskName = this.getTaskName();
			if(taskName.equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) || 
					taskName.equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
				scLoaWitness.setPartyType(ContractConst.WITNES_TYPE_AGENCY);
			} else if(taskName.equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				scLoaWitness.setPartyType(ContractConst.WITNES_TYPE_SUPPLIER);
			}
		}
		return scLoaWitness;
	}
	
	/**
	 * @param scLoaWitness the scLoaWitness to set
	 */
	public void setScLoaWitness(ScLoaWitness scLoaWitness) {
		this.scLoaWitness = scLoaWitness;
	}
	
	/**
	 * @return the selectedScLoaReports
	 */
	public ScLoaReport getSelectedScLoaReports() {
		return selectedScLoaReports;
	}
	
	/**
	 * @param selectedScLoaReports the selectedScLoaReports to set
	 */
	public void setSelectedScLoaReports(ScLoaReport selectedScLoaReports) {
		this.selectedScLoaReports = selectedScLoaReports;
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
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * @return the disableMeetingDetails
	 */
	public boolean isDisableMeetingDetails() {
		return disableMeetingDetails;
	}

	/**
	 * @param disableMeetingDetails the disableMeetingDetails to set
	 */
	public void setDisableMeetingDetails(boolean disableMeetingDetails) {
		this.disableMeetingDetails = disableMeetingDetails;
	}

	/**
	 * @return the approverName
	 */
	public String getApproverName() {
		return approverName;
	}

	/**
	 * @param approverName the approverName to set
	 */
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}	
}

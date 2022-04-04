package my.ep.web.managedbean.sc.demoInvitation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtDemo;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoByItemScore;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoContact;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoItem;
import my.ep.persistence.entity.sc.quationtender.ScQtDemoSupplier;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;




/**
 * Class to handle user session data corresponding to DemoInvitationBackingBean
 * #28784
 * @author Amir
 * Demo Invitation Managed Bean
 */
@ManagedBean(name = "demoInvMB")
@SessionScoped
public class DemoInvitationManageBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	/** The tab active index. */
	private int tabActiveIndex;
	
	/** The active index. */
	private int activeIndex;
	
	/** The scQt. */
	private ScQt qt;
	
	private boolean secretaryValidate;
	
	private boolean maintanceFlow;
	
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();
	
	private Boolean panel = Boolean.TRUE;
	
	private CtContract ctContract = new CtContract();
	
	private List<ZonesBean> beans = new ArrayList<ZonesBean>();
	
    private PmPtjSpecialApproval ptjSpecialApproval;
    
	private boolean demoType;
	
	private QtAmendMentBean amendMentBean;
	
	public List<ScQtBudget> gosodos;
	
	private String stateCodes;
	
	private boolean invByValue;
	
	private ScQtDemo scQtDemo;
	
	private List<ScQtCheckList> parentList;
	
	private List<ScQtCheckList> demoListSelect;
	
	private List<ScQtCheckList> demoTableRow;
	
	private List<ScQtDemoSupplier> allItemList;
	
	private List<ScQtDemoItem> byItemList;
	
	private List<ScQtDemo> scQtDemoList;
	
	private List<ScQtDemoContact> scQtDemoContactList;
	
	private List<ScQtDemoContact> selectedContactList;
	
	private String demoInvBy;
	
	private Long subStatusId;
	
	private boolean demoInvitationCancel;
	
	private List<ScQtCheckList> scQtCheckSt2techProposal;
	
	private int technicalTableRow;
	
	private List<ScQtCheckList> s2TechSelect;
	
	private String demoInstruction;
	
	private String demoRemark;
	
	private String currentOfficer;
	
	private ScQtCommittee scQtCommittee;
	
	private List<ScCommitteeMember> committeeMemberActivity;
	
	private String committeeType;
	
	private boolean rendered;
	
	private boolean qualifiedSupplier = true;
	
	public TabBean selectedApprovalTabBean;
	
	private SelectItem[] finalizationSecratary;
	
	private List<PmUser> pmUsersList;
	
	private boolean readOnly;
	
	private ScQtApproval  currentApproval;
	
	private List<ScQtDemoByItemScore> tempDemoByItemScoreList;
	
	private List<ScQtDemoSupplier> tempDemoSuppList;

	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public boolean isSecretaryValidate() {
		return secretaryValidate;
	}

	public void setSecretaryValidate(boolean secretaryValidate) {
		this.secretaryValidate = secretaryValidate;
	}

	public boolean isMaintanceFlow() {
		return maintanceFlow;
	}

	public void setMaintanceFlow(boolean maintanceFlow) {
		this.maintanceFlow = maintanceFlow;
	}

	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	public Boolean getPanel() {
		return panel;
	}

	public void setPanel(Boolean panel) {
		this.panel = panel;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public List<ZonesBean> getBeans() {
		return beans;
	}

	public void setBeans(List<ZonesBean> beans) {
		this.beans = beans;
	}

	public PmPtjSpecialApproval getPtjSpecialApproval() {
		return ptjSpecialApproval;
	}

	public void setPtjSpecialApproval(PmPtjSpecialApproval ptjSpecialApproval) {
		this.ptjSpecialApproval = ptjSpecialApproval;
	}

	public boolean isDemoType() {
		return demoType;
	}

	public void setDemoType(boolean demoType) {
		this.demoType = demoType;
	}

	public QtAmendMentBean getAmendMentBean() {
		return amendMentBean;
	}

	public void setAmendMentBean(QtAmendMentBean amendMentBean) {
		this.amendMentBean = amendMentBean;
	}

	public List<ScQtBudget> getGosodos() {
		return gosodos;
	}

	public void setGosodos(List<ScQtBudget> gosodos) {
		this.gosodos = gosodos;
	}

	public String getStateCodes() {
		return stateCodes;
	}

	public void setStateCodes(String stateCodes) {
		this.stateCodes = stateCodes;
	}

	public boolean isInvByValue() {
		return invByValue;
	}

	public void setInvByValue(boolean invByValue) {
		this.invByValue = invByValue;
	}

	public List<ScQtCheckList> getParentList() {
		return parentList;
	}

	public void setParentList(List<ScQtCheckList> parentList) {
		this.parentList = parentList;
	}

	public List<ScQtCheckList> getDemoListSelect() {
		return demoListSelect;
	}

	public void setDemoListSelect(List<ScQtCheckList> demoListSelect) {
		this.demoListSelect = demoListSelect;
	}

	public List<ScQtCheckList> getDemoTableRow() {
		return demoTableRow;
	}

	public void setDemoTableRow(List<ScQtCheckList> demoTableRow) {
		this.demoTableRow = demoTableRow;
	}

	public String getDemoInvBy() {
		return demoInvBy;
	}

	public void setDemoInvBy(String demoInvBy) {
		this.demoInvBy = demoInvBy;
	}

	public Long getSubStatusId() {
		return subStatusId;
	}

	public void setSubStatusId(Long subStatusId) {
		this.subStatusId = subStatusId;
	}

	public boolean isDemoInvitationCancel() {
		return demoInvitationCancel;
	}

	public void setDemoInvitationCancel(boolean demoInvitationCancel) {
		this.demoInvitationCancel = demoInvitationCancel;
	}

	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	public void setScQtCheckSt2techProposal(List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}

	public int getTechnicalTableRow() {
		return technicalTableRow;
	}

	public void setTechnicalTableRow(int technicalTableRow) {
		this.technicalTableRow = technicalTableRow;
	}

	public List<ScQtCheckList> getS2TechSelect() {
		return s2TechSelect;
	}

	public void setS2TechSelect(List<ScQtCheckList> s2TechSelect) {
		this.s2TechSelect = s2TechSelect;
	}

	public String getDemoInstruction() {
		return demoInstruction;
	}

	public void setDemoInstruction(String demoInstruction) {
		this.demoInstruction = demoInstruction;
	}

	public List<ScQtDemoContact> getScQtDemoContactList() {
		return scQtDemoContactList;
	}

	public void setScQtDemoContactList(List<ScQtDemoContact> scQtDemoContactList) {
		this.scQtDemoContactList = scQtDemoContactList;
	}

	public List<ScQtDemoContact> getSelectedContactList() {
		return selectedContactList;
	}

	public void setSelectedContactList(List<ScQtDemoContact> selectedContactList) {
		this.selectedContactList = selectedContactList;
	}

	public List<ScQtDemo> getScQtDemoList() {
		return scQtDemoList;
	}

	public void setScQtDemoList(List<ScQtDemo> scQtDemoList) {
		this.scQtDemoList = scQtDemoList;
	}

	public String getDemoRemark() {
		return demoRemark;
	}

	public void setDemoRemark(String demoRemark) {
		this.demoRemark = demoRemark;
	}

	public List<ScQtDemoSupplier> getAllItemList() {
		return allItemList;
	}

	public void setAllItemList(List<ScQtDemoSupplier> allItemList) {
		this.allItemList = allItemList;
	}

	public List<ScQtDemoItem> getByItemList() {
		return byItemList;
	}

	public void setByItemList(List<ScQtDemoItem> byItemList) {
		this.byItemList = byItemList;
	}

	public ScQtDemo getScQtDemo() {
		return scQtDemo;
	}

	public void setScQtDemo(ScQtDemo scQtDemo) {
		this.scQtDemo = scQtDemo;
	}

	public String getCurrentOfficer() {
		return currentOfficer;
	}

	public void setCurrentOfficer(String currentOfficer) {
		this.currentOfficer = currentOfficer;
	}

	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	public List<ScCommitteeMember> getCommitteeMemberActivity() {
		return committeeMemberActivity;
	}

	public void setCommitteeMemberActivity(List<ScCommitteeMember> committeeMemberActivity) {
		this.committeeMemberActivity = committeeMemberActivity;
	}

	public String getCommitteeType() {
		return committeeType;
	}

	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isQualifiedSupplier() {
		return qualifiedSupplier;
	}

	public void setQualifiedSupplier(boolean qualifiedSupplier) {
		this.qualifiedSupplier = qualifiedSupplier;
	}

	public TabBean getSelectedApprovalTabBean() {
		return selectedApprovalTabBean;
	}

	public void setSelectedApprovalTabBean(TabBean selectedApprovalTabBean) {
		this.selectedApprovalTabBean = selectedApprovalTabBean;
	}

	public SelectItem[] getFinalizationSecratary() {
		return finalizationSecratary;
	}

	public void setFinalizationSecratary(SelectItem[] finalizationSecratary) {
		this.finalizationSecratary = finalizationSecratary;
	}

	public ScQt getQt() {
		return qt;
	}

	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	public List<PmUser> getPmUsersList() {
		return pmUsersList;
	}

	public void setPmUsersList(List<PmUser> pmUsersList) {
		this.pmUsersList = pmUsersList;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public ScQtApproval getCurrentApproval() {
		return currentApproval;
	}

	public void setCurrentApproval(ScQtApproval currentApproval) {
		this.currentApproval = currentApproval;
	}

	public List<ScQtDemoSupplier> getTempDemoSuppList() {
		return tempDemoSuppList;
	}

	public void setTempDemoSuppList(List<ScQtDemoSupplier> tempDemoSuppList) {
		this.tempDemoSuppList = tempDemoSuppList;
	}

	public List<ScQtDemoByItemScore> getTempDemoByItemScoreList() {
		return tempDemoByItemScoreList;
	}

	public void setTempDemoByItemScoreList(List<ScQtDemoByItemScore> tempDemoByItemScoreList) {
		this.tempDemoByItemScoreList = tempDemoByItemScoreList;
	}

	
	

	
	
	
	
}

package my.ep.web.managedbean.sc.evaluation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtChecklistEvaluation;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtItem;
import my.ep.persistence.entity.sc.quationtender.ScQtManualEvaluation;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalLabel;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "evaluationMB")
@SessionScoped
public class EvaluationManagedBean extends BaseManagedBean {

	private List<EvaluationReportListBean> evaluationReportListItems;
	private List<EvaluationReportSubBean> evaluationReportSupplierItems;
	private List<ScQtMeeting> scQtMeetings;
	private ScQtSpec scQtSpec;
	private List<ScQtMeeting> meetingMultiSelect;
	private ScQtMeeting meeting;
	private List<ScQtCommitteeActivity> committeeMemberList;
	private ScQtChecklistEvaluation checklistEvaluation;
	private List<ScQtChecklistEvaluation> manuals1EvaluationList;
	private List<ScQtManualEvaluation> manualMultiSelect;
	private List<ScQtEvaluationResult> s1TechEvalList;
	private List<ScQtEvaluationResult> s1FinEvalList;
	private List<ScQtEvaluationResult> s2TechSupplier;
	private List<ScQtProposalChecklist> s2TechChecklist;
	private List<ScQtEvaluationResult> s2FinSupplier;
	private List<ScQtProposalChecklist> s2FinChecklist;
	private List<ScQtCommitteeActivity> committeeActivitymultiSelect;
	private ScQtEvaluationResult s2techSingleSelect;
	private ScQtEvaluationResult s2finSingleSelect;
	private ScEvaluationResult evaluationResult;
	private List<ScQtProposalChecklist> complianceList;
	private List<ScQtCommitteeActivity> signOffMemberList;
	private List<ScQtEvaluationResult> qualifiedList;
	private List<ScQtEvaluationResult> disQualifiedList;
	private ScQtEvaluationResult scQtEvaluationResult;
	private int tabActiveIndex;
	private List<ScQtItem> scQtItemList;
	private List<ScQtZoneCriteria> locationList;
	private ScQtCommitteeActivity commiteeActivity;
	private ScQt scqt;
	// OC,EC,TEC,FEC
	private String committeeType = "TEC";
	private List<ScQtEvaluationResult> manuals1SupplierList;
	private List<ScQtProposalLabel> manualProposalLabel;
	private List<ScQtManualEvaluation> manualEvaluateList;
	private boolean isEvaluateStatus;
	private Long evaluationResultId;
	private int tempTab;
	private TplSpecMaster specMaster;
	private String overAllResult;
	private String stageOneoverAllResult;
	private int result = 1;
	private int manualList;
	private String viewBy = "P";
	private List<ScQtEvaluationResult> requestDemoSelect;
	private String fileName;
	
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();


	// private String stage1committeeType="OC";
	public EvaluationManagedBean() {
		super();
		prepareDummyData1();
	}

	private void prepareDummyData1() {
		evaluationReportListItems = new ArrayList<EvaluationReportListBean>();
		evaluationReportSupplierItems = new ArrayList<EvaluationReportSubBean>();
		EvaluationReportListBean requestListItem1 = new EvaluationReportListBean(
				"General");
		List<EvaluationReportSubBean> supplierItems1 = new ArrayList<EvaluationReportSubBean>();
		supplierItems1.add(new EvaluationReportSubBean("Qtn/Tender Details"));
		supplierItems1.add(new EvaluationReportSubBean("Meeting Details"));
		supplierItems1.add(new EvaluationReportSubBean(
				"Committee Attendence List"));
		requestListItem1.setSupplierItems(supplierItems1);
		evaluationReportListItems.add(requestListItem1);

		EvaluationReportListBean requestListItem2 = new EvaluationReportListBean(
				"Stage1");
		List<EvaluationReportSubBean> supplierItems2 = new ArrayList<EvaluationReportSubBean>();
		supplierItems2.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems2.add(new EvaluationReportSubBean("Complience Summary"));

		requestListItem2.setSupplierItems(supplierItems2);
		evaluationReportListItems.add(requestListItem2);

		EvaluationReportListBean requestListItem3 = new EvaluationReportListBean(
				"Stage2");
		List<EvaluationReportSubBean> supplierItems3 = new ArrayList<EvaluationReportSubBean>();
		supplierItems3.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist1"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist2"));
		requestListItem3.setSupplierItems(supplierItems3);
		evaluationReportListItems.add(requestListItem3);

		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
	}

	public List<EvaluationReportListBean> getEvaluationReportListItems() {
		return evaluationReportListItems;
	}

	public void setEvaluationReportListItems(
			List<EvaluationReportListBean> evaluationReportListItems) {
		this.evaluationReportListItems = evaluationReportListItems;
	}

	public List<EvaluationReportSubBean> getEvaluationReportSupplierItems() {
		return evaluationReportSupplierItems;
	}

	public void setEvaluationReportSupplierItems(
			List<EvaluationReportSubBean> evaluationReportSupplierItems) {
		this.evaluationReportSupplierItems = evaluationReportSupplierItems;
	}

	/**
	 * @return the scQtMeetings
	 */
	public List<ScQtMeeting> getScQtMeetings() {
		return scQtMeetings;
	}

	/**
	 * @param scQtMeetings
	 *            the scQtMeetings to set
	 */
	public void setScQtMeetings(List<ScQtMeeting> scQtMeetings) {
		this.scQtMeetings = scQtMeetings;
	}

	/**
	 * @return the scQtSpec
	 */
	public ScQtSpec getScQtSpec() {
		return scQtSpec;
	}

	/**
	 * @param scQtSpec
	 *            the scQtSpec to set
	 */
	public void setScQtSpec(ScQtSpec scQtSpec) {
		this.scQtSpec = scQtSpec;
	}

	/**
	 * @return the meeting
	 */
	public ScQtMeeting getMeeting() {
		return meeting;
	}

	/**
	 * @param meeting
	 *            the meeting to set
	 */
	public void setMeeting(ScQtMeeting meeting) {
		this.meeting = meeting;
	}

	/**
	 * @return the checklistEvaluation
	 */
	public ScQtChecklistEvaluation getChecklistEvaluation() {
		return checklistEvaluation;
	}

	/**
	 * @param checklistEvaluation
	 *            the checklistEvaluation to set
	 */
	public void setChecklistEvaluation(
			ScQtChecklistEvaluation checklistEvaluation) {
		this.checklistEvaluation = checklistEvaluation;
	}

	/**
	 * @return the manuals1EvaluationList
	 */
	public List<ScQtChecklistEvaluation> getManuals1EvaluationList() {
		return manuals1EvaluationList;
	}

	/**
	 * @param manuals1EvaluationList
	 *            the manuals1EvaluationList to set
	 */
	public void setManuals1EvaluationList(
			List<ScQtChecklistEvaluation> manuals1EvaluationList) {
		this.manuals1EvaluationList = manuals1EvaluationList;
	}

	/**
	 * @param meetingMultiSelect
	 *            the meetingMultiSelect to set
	 */
	public void setMeetingMultiSelect(List<ScQtMeeting> meetingMultiSelect) {
		this.meetingMultiSelect = meetingMultiSelect;
	}

	/**
	 * @return the meetingMultiSelect
	 */
	public List<ScQtMeeting> getMeetingMultiSelect() {
		return meetingMultiSelect;
	}

	/**
	 * @return the signOffMemberList
	 */
	public List<ScQtCommitteeActivity> getSignOffMemberList() {
		return signOffMemberList;
	}

	/**
	 * @param signOffMemberList
	 *            the signOffMemberList to set
	 */
	public void setSignOffMemberList(
			List<ScQtCommitteeActivity> signOffMemberList) {
		this.signOffMemberList = signOffMemberList;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex
	 *            the tabActiveIndex to set
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the committeeMemberList
	 */
	public List<ScQtCommitteeActivity> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList
	 *            the committeeMemberList to set
	 */
	public void setCommitteeMemberList(
			List<ScQtCommitteeActivity> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	/**
	 * @return the committeeActivitymultiSelect
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivitymultiSelect() {
		return committeeActivitymultiSelect;
	}

	/**
	 * @param committeeActivitymultiSelect
	 *            the committeeActivitymultiSelect to set
	 */
	public void setCommitteeActivitymultiSelect(
			List<ScQtCommitteeActivity> committeeActivitymultiSelect) {
		this.committeeActivitymultiSelect = committeeActivitymultiSelect;
	}

	/**
	 * @return the s1TechEvalList
	 */
	public List<ScQtEvaluationResult> getS1TechEvalList() {
		return s1TechEvalList;
	}

	/**
	 * @param s1TechEvalList
	 *            the s1TechEvalList to set
	 */
	public void setS1TechEvalList(List<ScQtEvaluationResult> s1TechEvalList) {
		this.s1TechEvalList = s1TechEvalList;
	}

	/**
	 * @return the s1FinEvalList
	 */
	public List<ScQtEvaluationResult> getS1FinEvalList() {
		return s1FinEvalList;
	}

	/**
	 * @param s1FinEvalList
	 *            the s1FinEvalList to set
	 */
	public void setS1FinEvalList(List<ScQtEvaluationResult> s1FinEvalList) {
		this.s1FinEvalList = s1FinEvalList;
	}

	/**
	 * @return the s2TechSupplier
	 */
	public List<ScQtEvaluationResult> getS2TechSupplier() {
		return s2TechSupplier;
	}

	/**
	 * @param s2TechSupplier
	 *            the s2TechSupplier to set
	 */
	public void setS2TechSupplier(List<ScQtEvaluationResult> s2TechSupplier) {
		this.s2TechSupplier = s2TechSupplier;
	}

	/**
	 * @return the s2FinSupplier
	 */
	public List<ScQtEvaluationResult> getS2FinSupplier() {
		return s2FinSupplier;
	}

	/**
	 * @param s2FinSupplier
	 *            the s2FinSupplier to set
	 */
	public void setS2FinSupplier(List<ScQtEvaluationResult> s2FinSupplier) {
		this.s2FinSupplier = s2FinSupplier;
	}

	/**
	 * @return the evaluationResult
	 */
	public ScEvaluationResult getEvaluationResult() {
		return evaluationResult;
	}

	/**
	 * @param evaluationResult
	 *            the evaluationResult to set
	 */
	public void setEvaluationResult(ScEvaluationResult evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	/**
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * @param scQtEvaluationResult
	 *            the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(
			ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * @return the s2techSingleSelect
	 */
	public ScQtEvaluationResult getS2techSingleSelect() {
		return s2techSingleSelect;
	}

	/**
	 * @param s2techSingleSelect
	 *            the s2techSingleSelect to set
	 */
	public void setS2techSingleSelect(ScQtEvaluationResult s2techSingleSelect) {
		this.s2techSingleSelect = s2techSingleSelect;
	}

	/**
	 * @return the s2finSingleSelect
	 */
	public ScQtEvaluationResult getS2finSingleSelect() {
		return s2finSingleSelect;
	}

	/**
	 * @param s2finSingleSelect
	 *            the s2finSingleSelect to set
	 */
	public void setS2finSingleSelect(ScQtEvaluationResult s2finSingleSelect) {
		this.s2finSingleSelect = s2finSingleSelect;
	}

	/**
	 * @return the scQtItemList
	 */
	public List<ScQtItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * @param scQtItemList
	 *            the scQtItemList to set
	 */
	public void setScQtItemList(List<ScQtItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}

	/**
	 * @return the locationList
	 */
	public List<ScQtZoneCriteria> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList
	 *            the locationList to set
	 */
	public void setLocationList(List<ScQtZoneCriteria> locationList) {
		this.locationList = locationList;
	}

	/**
	 * @return the commiteeActivity
	 */
	public ScQtCommitteeActivity getCommiteeActivity() {
		return commiteeActivity;
	}

	/**
	 * @param commiteeActivity
	 *            the commiteeActivity to set
	 */
	public void setCommiteeActivity(ScQtCommitteeActivity commiteeActivity) {
		this.commiteeActivity = commiteeActivity;
	}

	/**
	 * @return the scqt
	 */
	public ScQt getScqt() {
		return scqt;
	}

	/**
	 * @param scqt
	 *            the scqt to set
	 */
	public void setScqt(ScQt scqt) {
		this.scqt = scqt;
	}

	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * @param committeeType
	 *            the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the manuals1SupplierList
	 */
	public List<ScQtEvaluationResult> getManuals1SupplierList() {
		return manuals1SupplierList;
	}

	/**
	 * @param manuals1SupplierList
	 *            the manuals1SupplierList to set
	 */
	public void setManuals1SupplierList(
			List<ScQtEvaluationResult> manuals1SupplierList) {
		this.manuals1SupplierList = manuals1SupplierList;
	}

	/**
	 * @return the isEvaluateStatus
	 */
	public boolean isEvaluateStatus() {
		return isEvaluateStatus;
	}

	/**
	 * @param isEvaluateStatus
	 *            the isEvaluateStatus to set
	 */
	public void setEvaluateStatus(boolean isEvaluateStatus) {
		this.isEvaluateStatus = isEvaluateStatus;
	}

	/**
	 * @return the manualProposalLabel
	 */
	public List<ScQtProposalLabel> getManualProposalLabel() {
		return manualProposalLabel;
	}

	/**
	 * @param manualProposalLabel
	 *            the manualProposalLabel to set
	 */
	public void setManualProposalLabel(
			List<ScQtProposalLabel> manualProposalLabel) {
		this.manualProposalLabel = manualProposalLabel;
	}

	/**
	 * @return the manualEvaluateList
	 */
	public List<ScQtManualEvaluation> getManualEvaluateList() {
		return manualEvaluateList;
	}

	/**
	 * @param manualEvaluateList
	 *            the manualEvaluateList to set
	 */
	public void setManualEvaluateList(
			List<ScQtManualEvaluation> manualEvaluateList) {
		this.manualEvaluateList = manualEvaluateList;
	}

	/**
	 * @return the manualMultiSelect
	 */
	public List<ScQtManualEvaluation> getManualMultiSelect() {
		return manualMultiSelect;
	}

	/**
	 * @param manualMultiSelect
	 *            the manualMultiSelect to set
	 */
	public void setManualMultiSelect(
			List<ScQtManualEvaluation> manualMultiSelect) {
		this.manualMultiSelect = manualMultiSelect;
	}

	/**
	 * @return the tempTab
	 */
	public int getTempTab() {
		return tempTab;
	}

	/**
	 * @param tempTab
	 *            the tempTab to set
	 */
	public void setTempTab(int tempTab) {
		this.tempTab = tempTab;
	}

	/**
	 * @return the complianceList
	 */
	public List<ScQtProposalChecklist> getComplianceList() {
		return complianceList;
	}

	/**
	 * @param complianceList
	 *            the complianceList to set
	 */
	public void setComplianceList(List<ScQtProposalChecklist> complianceList) {
		this.complianceList = complianceList;
	}

	/**
	 * @return the evaluationResultId
	 */
	public Long getEvaluationResultId() {
		return evaluationResultId;
	}

	/**
	 * @param evaluationResultId
	 *            the evaluationResultId to set
	 */
	public void setEvaluationResultId(Long evaluationResultId) {
		this.evaluationResultId = evaluationResultId;
	}

	/**
	 * @return the s2TechChecklist
	 */
	public List<ScQtProposalChecklist> getS2TechChecklist() {
		return s2TechChecklist;
	}

	/**
	 * @param s2TechChecklist
	 *            the s2TechChecklist to set
	 */
	public void setS2TechChecklist(List<ScQtProposalChecklist> s2TechChecklist) {
		this.s2TechChecklist = s2TechChecklist;
	}

	/**
	 * @return the s2FinChecklist
	 */
	public List<ScQtProposalChecklist> getS2FinChecklist() {
		return s2FinChecklist;
	}

	/**
	 * @param s2FinChecklist
	 *            the s2FinChecklist to set
	 */
	public void setS2FinChecklist(List<ScQtProposalChecklist> s2FinChecklist) {
		this.s2FinChecklist = s2FinChecklist;
	}

	/**
	 * @return the qualifiedList
	 */
	public List<ScQtEvaluationResult> getQualifiedList() {
		return qualifiedList;
	}

	/**
	 * @param qualifiedList
	 *            the qualifiedList to set
	 */
	public void setQualifiedList(List<ScQtEvaluationResult> qualifiedList) {
		this.qualifiedList = qualifiedList;
	}

	/**
	 * @return the disQualifiedList
	 */
	public List<ScQtEvaluationResult> getDisQualifiedList() {
		return disQualifiedList;
	}

	/**
	 * @param disQualifiedList
	 *            the disQualifiedList to set
	 */
	public void setDisQualifiedList(List<ScQtEvaluationResult> disQualifiedList) {
		this.disQualifiedList = disQualifiedList;
	}

	/**
	 * @return the specMaster
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * @param specMaster
	 *            the specMaster to set
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}

	/**
	 * @return the overAllResult
	 */
	public String getOverAllResult() {
		return overAllResult;
	}

	/**
	 * @param overAllResult
	 *            the overAllResult to set
	 */
	public void setOverAllResult(String overAllResult) {
		this.overAllResult = overAllResult;
	}

	/**
	 * @return the stageOneoverAllResult
	 */
	public String getStageOneoverAllResult() {
		return stageOneoverAllResult;
	}

	/**
	 * @param stageOneoverAllResult
	 *            the stageOneoverAllResult to set
	 */
	public void setStageOneoverAllResult(String stageOneoverAllResult) {
		this.stageOneoverAllResult = stageOneoverAllResult;
	}

	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}

	/**
	 * @return the manualList
	 */
	public int getManualList() {
		return manualList;
	}

	/**
	 * @param manualList
	 *            the manualList to set
	 */
	public void setManualList(int manualList) {
		this.manualList = manualList;
	}

	/**
	 * @return the viewBy
	 */
	public String getViewBy() {
		return viewBy;
	}

	/**
	 * @param viewBy
	 *            the viewBy to set
	 */
	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	/**
	 * @return the requestDemoSelect
	 */
	public List<ScQtEvaluationResult> getRequestDemoSelect() {
		return requestDemoSelect;
	}

	/**
	 * @param requestDemoSelect
	 *            the requestDemoSelect to set
	 */
	public void setRequestDemoSelect(
			List<ScQtEvaluationResult> requestDemoSelect) {
		this.requestDemoSelect = requestDemoSelect;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	

}

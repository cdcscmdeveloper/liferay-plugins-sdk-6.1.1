package my.ep.web.managedbean.sc.loiloa;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOIDetailManagedBean")
@SessionScoped
public class LOIDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScQt qt;

	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScLoi scLoi;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;
	
	private int selectedTabIndex;
	private SmPersonnel supplierHQPersonnel;

	
	private ScLoiNegoLocation scNegoLocation;
	private List<ScLoiNegoLocation> negoLocationList;
	private List<ScLoiNegoLocation> selectedNegoLocationList;
	private ScLoiTerm scNegoTerm;
	private List<ScLoiTerm> negoTermsList;
	private List<ScLoiTerm> selectedNegoTermsList;
	private PmUser pmUser;
	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;
	private ScQtEvaluationResult scQtEvaluationResult;
	
	private String status;
	public String reqApprove;
	public String Address;
	
	private List<ScRequestItem> scQtItemList;
	private List<PmOrgProfile> userMinistryList;
	private List<PmOrgProfile> userDepartmentList;

	private SmSupplierBranch smSupplierBranch;
	private String branchName;
	private List<SelectItem> selectItemList;
	private List<PmUser>  approverNames; // Drop down Listing for Approver Names
	private List<PmUser>  contractSigners; // Drop down Listing for Contract Signers
	
	private ScQtFinalization scQtFinalization;
	private List<ScQtRecommend> scQtRecommends= new ArrayList<ScQtRecommend>();
	private ScQtRecommend recommend = new ScQtRecommend();
	private List<ScQtRecommendDtl> scQtRecommendDtls = new ArrayList<ScQtRecommendDtl>();
	private List<ScLoiLoa> loiLoas = new ArrayList<ScLoiLoa>();
	private List<ScLoiLoa> recSupplierList = new ArrayList<ScLoiLoa>();
	private List<ScLoi> lois = new ArrayList<ScLoi>();
	
	private Long orgProfileId;
	private PmOrgProfile pmOrgProfile = new PmOrgProfile();
	private PmOrgProfile selectedPtj = new PmOrgProfile();
	private List<PmOrgProfile> ptjList;
	
	private Long sourceId;
	private List<LoiLoaBean>  loiLoaBeans  = new ArrayList<LoiLoaBean>();
	private ScLoiLoa selectedLoiLoa = new ScLoiLoa();

	private List<ScLoiLoaItem> scLoiLoaItems;
	private PmUser contractSignerUser;
	private String docType;
	private Object payLoadData;
	private PmUser approverUser;
	private ScLoiApproval scLoiApproval;
	
	private List<ScQtCommitteeActivity> ecActivityList;
	private List<ScQtCommitteeActivity> tecActivityList;
	private List<ScQtCommitteeActivity> fecActivityList;
	private List<ScQtCommitteeActivity> fcActivityList;
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	
	private List<String> recommendDtlIds;
	
	/**
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * @param scQtEvaluationResult the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * @return the approverNames
	 */
	public List<PmUser> getApproverNames() {
		return approverNames;
	}

	/**
	 * @param approverNames the approverNames to set
	 */
	public void setApproverNames(List<PmUser> approverNames) {
		this.approverNames = approverNames;
	}

	/**
	 * @return the selectItemList
	 */
	public List<SelectItem> getSelectItemList() {
		return selectItemList;
	}

	/**
	 * @param selectItemList the selectItemList to set
	 */
	public void setSelectItemList(List<SelectItem> selectItemList) {
		this.selectItemList = selectItemList;
	}

	/**
	 * @return the selectedNegoTermsList
	 */
	public List<ScLoiTerm> getSelectedNegoTermsList() {
		return selectedNegoTermsList;
	}

	/**
	 * @param selectedNegoTermsList the selectedNegoTermsList to set
	 */
	public void setSelectedNegoTermsList(List<ScLoiTerm> selectedNegoTermsList) {
		this.selectedNegoTermsList = selectedNegoTermsList;
	}

	/**
	 * @return the selectedNegoLocationList
	 */
	public List<ScLoiNegoLocation> getSelectedNegoLocationList() {
		return selectedNegoLocationList;
	}

	/**
	 * @param selectedNegoLocationList the selectedNegoLocationList to set
	 */
	public void setSelectedNegoLocationList(
			List<ScLoiNegoLocation> selectedNegoLocationList) {
		this.selectedNegoLocationList = selectedNegoLocationList;
	}

	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}

	/**
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/**
	 * @return the smSupplierBranch
	 */
	public SmSupplierBranch getSmSupplierBranch() {
		return smSupplierBranch;
	}

	/**
	 * @param smSupplierBranch the smSupplierBranch to set
	 */
	public void setSmSupplierBranch(SmSupplierBranch smSupplierBranch) {
		this.smSupplierBranch = smSupplierBranch;
	}

	/**
	 * @return the userMinistryList
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * @param userMinistryList the userMinistryList to set
	 */
	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	/**
	 * @return the userDepartmentList
	 */
	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	/**
	 * @param userDepartmentList the userDepartmentList to set
	 */
	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}


	/**
	 * @return the scQtItemList
	 */
	public List<ScRequestItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * @param scQtItemList the scQtItemList to set
	 */
	public void setScQtItemList(List<ScRequestItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}

	/**
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * @param branchsList the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return Address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		Address = address;
	}

	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * @param pmUser
	 *            the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * @return the scQtProposalRanking
	 */
	public ScQtProposalRanking getScQtProposalRanking() {
		return scQtProposalRanking;
	}

	/**
	 * @param scQtProposalRanking
	 *            the scQtProposalRanking to set
	 */
	public void setScQtProposalRanking(ScQtProposalRanking scQtProposalRanking) {
		this.scQtProposalRanking = scQtProposalRanking;
	}

	/**
	 * @return the reqApprove
	 */
	public String getReqApprove() {
		return reqApprove;
	}

	/**
	 * @param reqApprove
	 *            the reqApprove to set
	 */
	public void setReqApprove(String reqApprove) {
		this.reqApprove = reqApprove;
	}
	/**
	 * @return the scNegoTerm
	 */
	public ScLoiTerm getScNegoTerm() {
		return scNegoTerm;
	}

	/**
	 * @param scNegoTerm
	 *            the scNegoTerm to set
	 */
	public void setScNegoTerm(ScLoiTerm scNegoTerm) {
		this.scNegoTerm = scNegoTerm;
	}

	/**
	 * @return the negoTermsList
	 */
	public List<ScLoiTerm> getNegoTermsList() {
		return negoTermsList;
	}

	/**
	 * @param negoTermsList
	 *            the negoTermsList to set
	 */
	public void setNegoTermsList(List<ScLoiTerm> negoTermsList) {
		this.negoTermsList = negoTermsList;
	}

	/**
	 * @return the negoLocationList
	 */
	public List<ScLoiNegoLocation> getNegoLocationList() {
		return negoLocationList;
	}

	/**
	 * @param negoLocationList
	 *            the negoLocationList to set
	 */
	public void setNegoLocationList(List<ScLoiNegoLocation> negoLocationList) {
		this.negoLocationList = negoLocationList;
	}

	/**
	 * @return the scNegoLocation
	 */
	public ScLoiNegoLocation getScNegoLocation() {
		return scNegoLocation;
	}

	/**
	 * @param scNegoLocation
	 *            the scNegoLocation to set
	 */
	public void setScNegoLocation(ScLoiNegoLocation scNegoLocation) {
		this.scNegoLocation = scNegoLocation;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	
	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount
	 *            the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the scLoi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}

	/**
	 * @param scLoi
	 *            the scLoi to set
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}

	/**
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * @param comitteeUsers
	 *            the comitteeUsers to set
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * @param approvalDate
	 *            the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * @param qt
	 *            the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
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
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * @param scQtFinalization the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * @return the scQtRecommends
	 */
	public List<ScQtRecommend> getScQtRecommends() {
		return scQtRecommends;
	}

	/**
	 * @param scQtRecommends the scQtRecommends to set
	 */
	public void setScQtRecommends(List<ScQtRecommend> scQtRecommends) {
		this.scQtRecommends = scQtRecommends;
	}

	/**
	 * @return the scQtRecommendDtls
	 */
	public List<ScQtRecommendDtl> getScQtRecommendDtls() {
		return scQtRecommendDtls;
	}

	/**
	 * @param scQtRecommendDtls the scQtRecommendDtls to set
	 */
	public void setScQtRecommendDtls(List<ScQtRecommendDtl> scQtRecommendDtls) {
		this.scQtRecommendDtls = scQtRecommendDtls;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the loiLoas
	 */
	public List<ScLoiLoa> getLoiLoas() {
		return loiLoas;
	}

	/**
	 * @param loiLoas the loiLoas to set
	 */
	public void setLoiLoas(List<ScLoiLoa> loiLoas) {
		this.loiLoas = loiLoas;
	}

	/**
	 * @return the lois
	 */
	public List<ScLoi> getLois() {
		return lois;
	}

	/**
	 * @param lois the lois to set
	 */
	public void setLois(List<ScLoi> lois) {
		this.lois = lois;
	}

	/**
	 * @return the recommend
	 */
	public ScQtRecommend getRecommend() {
		return recommend;
	}

	/**
	 * @param recommend the recommend to set
	 */
	public void setRecommend(ScQtRecommend recommend) {
		this.recommend = recommend;
	}

	/**
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	/**
	 * @return the selectedPtj
	 */
	public PmOrgProfile getSelectedPtj() {
		return selectedPtj;
	}

	/**
	 * @param selectedPtj the selectedPtj to set
	 */
	public void setSelectedPtj(PmOrgProfile selectedPtj) {
		this.selectedPtj = selectedPtj;
	}

	/**
	 * @return the ptjList
	 */
	public List<PmOrgProfile> getPtjList() {
		return ptjList;
	}

	/**
	 * @param ptjList the ptjList to set
	 */
	public void setPtjList(List<PmOrgProfile> ptjList) {
		this.ptjList = ptjList;
	}

	/**
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}

	/**
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}

	/**
	 * @return the contractSigners
	 */
	public List<PmUser> getContractSigners() {
		return contractSigners;
	}

	/**
	 * @param contractSigners the contractSigners to set
	 */
	public void setContractSigners(List<PmUser> contractSigners) {
		this.contractSigners = contractSigners;
	}

	/**
	 * @return the recSupplierList
	 */
	public List<ScLoiLoa> getRecSupplierList() {
		return recSupplierList;
	}

	/**
	 * @param recSupplierList the recSupplierList to set
	 */
	public void setRecSupplierList(List<ScLoiLoa> recSupplierList) {
		this.recSupplierList = recSupplierList;
	}

	/**
	 * @return the sourceId
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the scLoiLoaItems
	 */
	public List<ScLoiLoaItem> getScLoiLoaItems() {
		return scLoiLoaItems;
	}

	/**
	 * @param scLoiLoaItems the scLoiLoaItems to set
	 */
	public void setScLoiLoaItems(List<ScLoiLoaItem> scLoiLoaItems) {
		this.scLoiLoaItems = scLoiLoaItems;
	}

	/**
	 * @return the loiLoaBeans
	 */
	public List<LoiLoaBean> getLoiLoaBeans() {
		return loiLoaBeans;
	}

	/**
	 * @param loiLoaBeans the loiLoaBeans to set
	 */
	public void setLoiLoaBeans(List<LoiLoaBean> loiLoaBeans) {
		this.loiLoaBeans = loiLoaBeans;
	}

	/**
	 * @return the selectedLoiLoa
	 */
	public ScLoiLoa getSelectedLoiLoa() {
		return selectedLoiLoa;
	}

	/**
	 * @param selectedLoiLoa the selectedLoiLoa to set
	 */
	public void setSelectedLoiLoa(ScLoiLoa selectedLoiLoa) {
		this.selectedLoiLoa = selectedLoiLoa;
	}

	public SmPersonnel getSupplierHQPersonnel() {
		return supplierHQPersonnel;
	}

	public void setSupplierHQPersonnel(SmPersonnel supplierHQPersonnel) {
		this.supplierHQPersonnel = supplierHQPersonnel;
	}

	public PmUser getContractSignerUser() {
		return contractSignerUser;
	}

	public void setContractSignerUser(PmUser contractSignerUser) {
		this.contractSignerUser = contractSignerUser;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the payLoadData
	 */
	public Object getPayLoadData() {
		return payLoadData;
	}

	/**
	 * @param payLoadData the payLoadData to set
	 */
	public void setPayLoadData(Object payLoadData) {
		this.payLoadData = payLoadData;
	}

	/**
	 * @return the ecActivityList
	 */
	public List<ScQtCommitteeActivity> getEcActivityList() {
		return ecActivityList;
	}

	/**
	 * @param ecActivityList the ecActivityList to set
	 */
	public void setEcActivityList(List<ScQtCommitteeActivity> ecActivityList) {
		this.ecActivityList = ecActivityList;
	}

	/**
	 * @return the tecActivityList
	 */
	public List<ScQtCommitteeActivity> getTecActivityList() {
		return tecActivityList;
	}

	/**
	 * @param tecActivityList the tecActivityList to set
	 */
	public void setTecActivityList(List<ScQtCommitteeActivity> tecActivityList) {
		this.tecActivityList = tecActivityList;
	}

	/**
	 * @return the fecActivityList
	 */
	public List<ScQtCommitteeActivity> getFecActivityList() {
		return fecActivityList;
	}

	/**
	 * @param fecActivityList the fecActivityList to set
	 */
	public void setFecActivityList(List<ScQtCommitteeActivity> fecActivityList) {
		this.fecActivityList = fecActivityList;
	}

	/**
	 * @return the fcActivityList
	 */
	public List<ScQtCommitteeActivity> getFcActivityList() {
		return fcActivityList;
	}

	/**
	 * @param fcActivityList the fcActivityList to set
	 */
	public void setFcActivityList(List<ScQtCommitteeActivity> fcActivityList) {
		this.fcActivityList = fcActivityList;
	}

	/**
	 * @return the meetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}

	/**
	 * @param meetingAttendanceList the meetingAttendanceList to set
	 */
	public void setMeetingAttendanceList(
			List<ScQtMeetingAttendance> meetingAttendanceList) {
		this.meetingAttendanceList = meetingAttendanceList;
	}

	/**
	 * @return the selectedMeetingAttList
	 */
	public List<ScQtMeetingAttendance> getSelectedMeetingAttList() {
		return selectedMeetingAttList;
	}

	/**
	 * @param selectedMeetingAttList the selectedMeetingAttList to set
	 */
	public void setSelectedMeetingAttList(
			List<ScQtMeetingAttendance> selectedMeetingAttList) {
		this.selectedMeetingAttList = selectedMeetingAttList;
	}



	public ScLoiApproval getScLoiApproval() {
		return scLoiApproval;
	}

	public void setScLoiApproval(ScLoiApproval scLoiApproval) {
		this.scLoiApproval = scLoiApproval;
	}

	public PmUser getApproverUser() {
		return approverUser;
	}

	public void setApproverUser(PmUser approverUser) {
		this.approverUser = approverUser;
	}

	public List<String> getRecommendDtlIds() {
		return recommendDtlIds;
	}

	public void setRecommendDtlIds(List<String> recommendDtlIds) {
		this.recommendDtlIds = recommendDtlIds;
	}
	
}

package my.ep.web.managedbean.ss.loiloa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.ss.quationtender.ScLoi;
import my.ep.persistence.entity.ss.quationtender.ScNegoLocation;
import my.ep.persistence.entity.ss.quationtender.ScNegoTerm;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScTermDesc;
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
	private ScQtSpec scQtSpec;
	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScLoi scLoi;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;

	private ScQtCriteria scQtCriteria;
	private ScNegoLocation scNegoLocation;
	private List<ScNegoLocation> negoLocationList;
	private ScNegoTerm scNegoTerm;
	private List<ScNegoTerm> negoTermsList;
	private ScTermDesc scTermDesc;
	private List<ScTermDesc> scTermDescList;
	private List<ScTermDesc> selectedCheckDetails;
	private PmUser pmUser;
	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;
	
	private String status;
	public String reqApprove;
	public String Address;

	/**
	 * @return the scTermDesc
	 */
	public ScTermDesc getScTermDesc() {
		return scTermDesc;
	}

	/**
	 * @param scTermDesc the scTermDesc to set
	 */
	public void setScTermDesc(ScTermDesc scTermDesc) {
		this.scTermDesc = scTermDesc;
	}

	/**
	 * @return the selectedCheckDetails
	 */
	public List<ScTermDesc> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	/**
	 * @param selectedCheckDetails the selectedCheckDetails to set
	 */
	public void setSelectedCheckDetails(List<ScTermDesc> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
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
	 * @return the scTermDescList
	 */
	public List<ScTermDesc> getScTermDescList() {
		return scTermDescList;
	}

	/**
	 * @param scTermDescList
	 *            the scTermDescList to set
	 */
	public void setScTermDescList(List<ScTermDesc> scTermDescList) {
		this.scTermDescList = scTermDescList;
	}

	/**
	 * @return the scNegoTerm
	 */
	public ScNegoTerm getScNegoTerm() {
		return scNegoTerm;
	}

	/**
	 * @param scNegoTerm
	 *            the scNegoTerm to set
	 */
	public void setScNegoTerm(ScNegoTerm scNegoTerm) {
		this.scNegoTerm = scNegoTerm;
	}

	/**
	 * @return the negoTermsList
	 */
	public List<ScNegoTerm> getNegoTermsList() {
		return negoTermsList;
	}

	/**
	 * @param negoTermsList
	 *            the negoTermsList to set
	 */
	public void setNegoTermsList(List<ScNegoTerm> negoTermsList) {
		this.negoTermsList = negoTermsList;
	}

	/**
	 * @return the negoLocationList
	 */
	public List<ScNegoLocation> getNegoLocationList() {
		return negoLocationList;
	}

	/**
	 * @param negoLocationList
	 *            the negoLocationList to set
	 */
	public void setNegoLocationList(List<ScNegoLocation> negoLocationList) {
		this.negoLocationList = negoLocationList;
	}

	/**
	 * @return the scNegoLocation
	 */
	public ScNegoLocation getScNegoLocation() {
		return scNegoLocation;
	}

	/**
	 * @param scNegoLocation
	 *            the scNegoLocation to set
	 */
	public void setScNegoLocation(ScNegoLocation scNegoLocation) {
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
	 * @return the scQtCriteria
	 */
	public ScQtCriteria getScQtCriteria() {
		return scQtCriteria;
	}

	/**
	 * @param scQtCriteria
	 *            the scQtCriteria to set
	 */
	public void setScQtCriteria(ScQtCriteria scQtCriteria) {
		this.scQtCriteria = scQtCriteria;
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

}

package my.ep.web.managedbean.ss.loiloa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.ss.quationtender.ScBondDtl;
import my.ep.persistence.entity.ss.quationtender.ScLoa;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailManagedBean")
@SessionScoped
public class LOADetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScQt qt;
	private ScQtSpec scQtSpec;
	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScLoa scLoa;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;
	private PmUser pmUser;
	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;

	public String address;
	private String status;
	private ScQtCriteria scQtCriteria;

	private List<ScBondDtl> scBondDtlList;
	private List<ScBondDtl> selectScBondDtlList;
	private List<ScQtZoneCriteria> zonelist;
	private List<ScQtItem> scQtItemList;
	private PmCurrency pmCurrency;
	
	private List<PmOrgProfile> userMinistryList;
	private List<PmOrgProfile> userDepartmentList;
	private List<PmOrgProfile> userDivisionList;
	private String branchName;
	private List<ScQtDeduction> scQtDeductionList;

	/**
	 * @return the scQtDeductionList
	 */
	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	/**
	 * @param scQtDeductionList the scQtDeductionList to set
	 */
	public void setScQtDeductionList(List<ScQtDeduction> scQtDeductionList) {
		this.scQtDeductionList = scQtDeductionList;
	}

	/**
	 * @return the selectScBondDtlList
	 */
	public List<ScBondDtl> getSelectScBondDtlList() {
		return selectScBondDtlList;
	}

	/**
	 * @param selectScBondDtlList the selectScBondDtlList to set
	 */
	public void setSelectScBondDtlList(List<ScBondDtl> selectScBondDtlList) {
		this.selectScBondDtlList = selectScBondDtlList;
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
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * @return the pmCurrency
	 */
	public PmCurrency getPmCurrency() {
		return pmCurrency;
	}

	/**
	 * @param pmCurrency the pmCurrency to set
	 */
	public void setPmCurrency(PmCurrency pmCurrency) {
		this.pmCurrency = pmCurrency;
	}

	/**
	 * @return the scQtItemList
	 */
	public List<ScQtItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * @param scQtItemList the scQtItemList to set
	 */
	public void setScQtItemList(List<ScQtItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}

	/**
	 * @return the zonelist
	 */
	public List<ScQtZoneCriteria> getZonelist() {
		return zonelist;
	}

	/**
	 * @param zonelist the zonelist to set
	 */
	public void setZonelist(List<ScQtZoneCriteria> zonelist) {
		this.zonelist = zonelist;
	}

	/**
	 * @return the scBondDtlList
	 */
	public List<ScBondDtl> getScBondDtlList() {
		return scBondDtlList;
	}

	/**
	 * @param scBondDtlList
	 *            the scBondDtlList to set
	 */
	public void setScBondDtlList(List<ScBondDtl> scBondDtlList) {
		this.scBondDtlList = scBondDtlList;
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
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * @param branchsList
	 *            the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * @param scLoa
	 *            the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
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

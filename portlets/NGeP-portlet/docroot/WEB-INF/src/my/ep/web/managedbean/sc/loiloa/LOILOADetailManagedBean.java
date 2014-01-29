package my.ep.web.managedbean.sc.loiloa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScLoi;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "loiLoaDetailManagedBean")
@SessionScoped
public class LOILOADetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScQt qt;
	private ScQtSpec scQtSpec;
	private ScQtProposal scQtProposal;
	private List<SmSupplier> smSupplierList;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;

	private ScLoi scLoi;
	private ScLoa scLoa;
	private PmUser pmUser;

	private ScQtProposalRanking scQtProposalRanking;
	private List<ScQtProposalRanking> scQtProposalRankingList;
	private ScQtEvaluationResult scQtEvaluationResult;
	private List<ScQtEvaluationResult> scQtEvaluationResultList;

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
	 * @return the scQtEvaluationResultList
	 */
	public List<ScQtEvaluationResult> getScQtEvaluationResultList() {
		return scQtEvaluationResultList;
	}

	/**
	 * @param scQtEvaluationResultList the scQtEvaluationResultList to set
	 */
	public void setScQtEvaluationResultList(
			List<ScQtEvaluationResult> scQtEvaluationResultList) {
		this.scQtEvaluationResultList = scQtEvaluationResultList;
	}

	/**
	 * @return the scQtProposalRankingList
	 */
	public List<ScQtProposalRanking> getScQtProposalRankingList() {
		return scQtProposalRankingList;
	}

	/**
	 * @param scQtProposalRankingList the scQtProposalRankingList to set
	 */
	public void setScQtProposalRankingList(
			List<ScQtProposalRanking> scQtProposalRankingList) {
		this.scQtProposalRankingList = scQtProposalRankingList;
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
	 * @return the smSupplierList
	 */
	public List<SmSupplier> getSmSupplierList() {
		return smSupplierList;
	}

	/**
	 * @param smSupplierList
	 *            the smSupplierList to set
	 */
	public void setSmSupplierList(List<SmSupplier> smSupplierList) {
		this.smSupplierList = smSupplierList;
	}

	/**
	 * @return the scQtProposal
	 */
	public ScQtProposal getScQtProposal() {
		return scQtProposal;
	}

	/**
	 * @param scQtProposal
	 *            the scQtProposal to set
	 */
	public void setScQtProposal(ScQtProposal scQtProposal) {
		this.scQtProposal = scQtProposal;
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

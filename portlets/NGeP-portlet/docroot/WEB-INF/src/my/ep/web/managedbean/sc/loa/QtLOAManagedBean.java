package my.ep.web.managedbean.sc.loa;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.loa.SuppLoiLoaBean;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class QtLOAManagedBean.
 *
 * @author Viswanath
 */
@ManagedBean(name = "qtLOAManagedBean")
@SessionScoped
public class QtLOAManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The qt. */
	private ScQt qt;
	
	/** The sc qt proposal. */
	private ScQtProposal scQtProposal;
	
	/** The sm supplier list. */
	private List<SmSupplier> smSupplierList;
	
	/** The sm supplier. */
	private SmSupplier smSupplier;
	
	/** The sm mof account. */
	private SmMofAccount smMofAccount;

	/** The pm user. */
	private PmUser pmUser;
	
	/** The sc qt evaluation result. */
	private ScQtEvaluationResult scQtEvaluationResult;
	
	/** The sc qt evaluation result list. */
	private List<ScQtEvaluationResult> scQtEvaluationResultList;
	
	/** The selected loi loa. */
	private ScLoiLoa selectedLoiLoa;
	
	/** The selected supp loi loa bean. */
	private SuppLoiLoaBean selectedSuppLoiLoaBean;
	
	/** The sc loi loa list. */
	private List<ScLoiLoa> scLoiLoaList;
	
	/** The supp loi loa list. */
	private List<SuppLoiLoaBean> suppLoiLoaList;
	
	/** The loi loa list title. */
	private String loiLoaListTitle;
	
	/** The map supp scloiloa list */
	private Map<Long, List<ScLoiLoa>> suppLoiLoaMap;


	/**
	 * Gets the sc qt evaluation result.
	 *
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * Sets the sc qt evaluation result.
	 *
	 * @param scQtEvaluationResult the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * Gets the sc qt evaluation result list.
	 *
	 * @return the scQtEvaluationResultList
	 */
	public List<ScQtEvaluationResult> getScQtEvaluationResultList() {
		return scQtEvaluationResultList;
	}

	/**
	 * Sets the sc qt evaluation result list.
	 *
	 * @param scQtEvaluationResultList the scQtEvaluationResultList to set
	 */
	public void setScQtEvaluationResultList(
			List<ScQtEvaluationResult> scQtEvaluationResultList) {
		this.scQtEvaluationResultList = scQtEvaluationResultList;
	}

	/**
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser            the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	

	

	/**
	 * Gets the sm mof account.
	 *
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * Sets the sm mof account.
	 *
	 * @param smMofAccount            the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * Gets the sm supplier.
	 *
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * Sets the sm supplier.
	 *
	 * @param smSupplier            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * Gets the sm supplier list.
	 *
	 * @return the smSupplierList
	 */
	public List<SmSupplier> getSmSupplierList() {
		return smSupplierList;
	}

	/**
	 * Sets the sm supplier list.
	 *
	 * @param smSupplierList            the smSupplierList to set
	 */
	public void setSmSupplierList(List<SmSupplier> smSupplierList) {
		this.smSupplierList = smSupplierList;
	}

	/**
	 * Gets the sc qt proposal.
	 *
	 * @return the scQtProposal
	 */
	public ScQtProposal getScQtProposal() {
		return scQtProposal;
	}

	/**
	 * Sets the sc qt proposal.
	 *
	 * @param scQtProposal            the scQtProposal to set
	 */
	public void setScQtProposal(ScQtProposal scQtProposal) {
		this.scQtProposal = scQtProposal;
	}

	

	/**
	 * Gets the qt.
	 *
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * Sets the qt.
	 *
	 * @param qt            the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * Gets the selected loi loa.
	 *
	 * @return the selectedLoiLoa
	 */
	public ScLoiLoa getSelectedLoiLoa() {
		return selectedLoiLoa;
	}

	/**
	 * Sets the selected loi loa.
	 *
	 * @param selectedLoiLoa the selectedLoiLoa to set
	 */
	public void setSelectedLoiLoa(ScLoiLoa selectedLoiLoa) {
		this.selectedLoiLoa = selectedLoiLoa;
	}

	/**
	 * Gets the selected supp loi loa bean.
	 *
	 * @return the selectedSuppLoiLoaBean
	 */
	public SuppLoiLoaBean getSelectedSuppLoiLoaBean() {
		return selectedSuppLoiLoaBean;
	}

	/**
	 * Sets the selected supp loi loa bean.
	 *
	 * @param selectedSuppLoiLoaBean the selectedSuppLoiLoaBean to set
	 */
	public void setSelectedSuppLoiLoaBean(SuppLoiLoaBean selectedSuppLoiLoaBean) {
		this.selectedSuppLoiLoaBean = selectedSuppLoiLoaBean;
	}

	/**
	 * Gets the sc loi loa list.
	 *
	 * @return the scLoiLoaList
	 */
	public List<ScLoiLoa> getScLoiLoaList() {
		return scLoiLoaList;
	}

	/**
	 * Sets the sc loi loa list.
	 *
	 * @param scLoiLoaList the scLoiLoaList to set
	 */
	public void setScLoiLoaList(List<ScLoiLoa> scLoiLoaList) {
		this.scLoiLoaList = scLoiLoaList;
	}

	/**
	 * Gets the supp loi loa list.
	 *
	 * @return the suppLoiLoaList
	 */
	public List<SuppLoiLoaBean> getSuppLoiLoaList() {
		return suppLoiLoaList;
	}

	/**
	 * Sets the supp loi loa list.
	 *
	 * @param suppLoiLoaList the suppLoiLoaList to set
	 */
	public void setSuppLoiLoaList(List<SuppLoiLoaBean> suppLoiLoaList) {
		this.suppLoiLoaList = suppLoiLoaList;
	}

	/**
	 * @return the suppLoiLoaMap
	 */
	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}

	/**
	 * @param suppLoiLoaMap the suppLoiLoaMap to set
	 */
	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {
		this.suppLoiLoaMap = suppLoiLoaMap;
	}

	/**
	 * Gets the loi loa list title.
	 *
	 * @return the loi loa list title
	 */
	public String getLoiLoaListTitle() {
		return loiLoaListTitle;
	}

	/**
	 * Sets the loi loa list title.
	 *
	 * @param loiLoaListTitle the new loi loa list title
	 */
	public void setLoiLoaListTitle(String loiLoaListTitle) {
		this.loiLoaListTitle = loiLoaListTitle;
	}

	
}

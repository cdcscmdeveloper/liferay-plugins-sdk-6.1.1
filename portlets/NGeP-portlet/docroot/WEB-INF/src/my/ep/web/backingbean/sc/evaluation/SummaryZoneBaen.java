/**
 * 
 */
package my.ep.web.backingbean.sc.evaluation;

import java.util.List;

import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

/**
 * @author ACER
 * 
 */
public class SummaryZoneBaen {

	private String zoneName;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private List<ScRequestItem> itemList;
	private List<ScQtEvaluationResultDtl> qualifiedList;
	private List<ScQtEvaluationResultDtl> disqualifiedList;
	private List<ScQtProposalChecklist> finSpecSupplierList;
	private ScQtScoreOfferPrice offerPrice;

	public SummaryZoneBaen(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtEvaluationResultDtl> qualifiedList,
			List<ScQtEvaluationResultDtl> disqualifiedList) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.disqualifiedList = disqualifiedList;
		this.qualifiedList = qualifiedList;
	}

	public SummaryZoneBaen(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScRequestItem> itemList) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.itemList = itemList;

	}

	public SummaryZoneBaen(TplSpecZoneDtl tplSpecZoneDtl,
			List<ScQtProposalChecklist> finSpecSupplierList,
			ScQtScoreOfferPrice offerPrice, int n) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
		this.finSpecSupplierList = finSpecSupplierList;
		this.offerPrice = offerPrice;

	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName
	 *            the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl
	 *            the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * @return the itemList
	 */
	public List<ScRequestItem> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList
	 *            the itemList to set
	 */
	public void setItemList(List<ScRequestItem> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the finSpecSupplierList
	 */
	public List<ScQtProposalChecklist> getFinSpecSupplierList() {
		return finSpecSupplierList;
	}

	/**
	 * @param finSpecSupplierList
	 *            the finSpecSupplierList to set
	 */
	public void setFinSpecSupplierList(
			List<ScQtProposalChecklist> finSpecSupplierList) {
		this.finSpecSupplierList = finSpecSupplierList;
	}

	/**
	 * @return the offerPrice
	 */
	public ScQtScoreOfferPrice getOfferPrice() {
		return offerPrice;
	}

	/**
	 * @param offerPrice
	 *            the offerPrice to set
	 */
	public void setOfferPrice(ScQtScoreOfferPrice offerPrice) {
		this.offerPrice = offerPrice;
	}

	/**
	 * @return the qualifiedList
	 */
	public List<ScQtEvaluationResultDtl> getQualifiedList() {
		return qualifiedList;
	}

	/**
	 * @param qualifiedList the qualifiedList to set
	 */
	public void setQualifiedList(List<ScQtEvaluationResultDtl> qualifiedList) {
		this.qualifiedList = qualifiedList;
	}

	/**
	 * @return the disqualifiedList
	 */
	public List<ScQtEvaluationResultDtl> getDisqualifiedList() {
		return disqualifiedList;
	}

	/**
	 * @param disqualifiedList the disqualifiedList to set
	 */
	public void setDisqualifiedList(List<ScQtEvaluationResultDtl> disqualifiedList) {
		this.disqualifiedList = disqualifiedList;
	}

}

package my.ep.web.bean.tpl;

import java.util.List;

import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;

public class TemplateSpecBean {
	private TplSpecMaster specMaster;
	private String contentType;
	private SmSupplier supplier;
	private List<TplSpecAnswer> specAnswerList;
	private List<TplSpecTierQty> selectedTierQtyList;
	private List<TplSpecScoring> selectedScoringList;
	
	public TemplateSpecBean() {
		this(TemplateSpecConst.CONTENT_TYPE_QUESTION);
	}
	
	public TemplateSpecBean(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the specMaster
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * @param specMaster the specMaster to set
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the supplier
	 */
	public SmSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the specAnswerList
	 */
	public List<TplSpecAnswer> getSpecAnswerList() {
		return specAnswerList;
	}

	/**
	 * @param specAnswerList the specAnswerList to set
	 */
	public void setSpecAnswerList(List<TplSpecAnswer> specAnswerList) {
		this.specAnswerList = specAnswerList;
	}

	/**
	 * @return the selectedTierQtyList
	 */
	public List<TplSpecTierQty> getSelectedTierQtyList() {
		return selectedTierQtyList;
	}

	/**
	 * @param selectedTierQtyList the selectedTierQtyList to set
	 */
	public void setSelectedTierQtyList(List<TplSpecTierQty> selectedTierQtyList) {
		this.selectedTierQtyList = selectedTierQtyList;
	}

	/**
	 * @return the selectedScoringList
	 */
	public List<TplSpecScoring> getSelectedScoringList() {
		return selectedScoringList;
	}

	/**
	 * @param selectedScoringList the selectedScoringList to set
	 */
	public void setSelectedScoringList(List<TplSpecScoring> selectedScoringList) {
		this.selectedScoringList = selectedScoringList;
	}
}

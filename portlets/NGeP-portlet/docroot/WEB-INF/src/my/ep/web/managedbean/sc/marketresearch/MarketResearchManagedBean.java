package my.ep.web.managedbean.sc.marketresearch;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "marketResearchManagedBean")
@SessionScoped
public class MarketResearchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isLazy;	
	private List<ScQtCategory> qtCategoryList;
	private List<ScQtCategory> selectedCategoryList;
	private Integer procurementType;
	private Date procurementYear;
	private String procTitle;
	private String itemName;
	private String specification;
	private String anyCategory;
	private String allCategories;
	private List<Object[]> marketResearchDataList;
	private String selectionType;

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the qtCategoryList
	 */
	public List<ScQtCategory> getQtCategoryList() {
		return qtCategoryList;
	}

	/**
	 * @param qtCategoryList the qtCategoryList to set
	 */
	public void setQtCategoryList(List<ScQtCategory> qtCategoryList) {
		this.qtCategoryList = qtCategoryList;
	}

	/**
	 * @return the selectedCategoryList
	 */
	public List<ScQtCategory> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	/**
	 * @param selectedCategoryList the selectedCategoryList to set
	 */
	public void setSelectedCategoryList(List<ScQtCategory> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	/**
	 * @return the procurementType
	 */
	public Integer getProcurementType() {
		return procurementType;
	}

	/**
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(Integer procurementType) {
		this.procurementType = procurementType;
	}

	/**
	 * @return the procurementYear
	 */
	public Date getProcurementYear() {
		return procurementYear;
	}

	/**
	 * @param procurementYear the procurementYear to set
	 */
	public void setProcurementYear(Date procurementYear) {
		this.procurementYear = procurementYear;
	}

	/**
	 * @return the procTitle
	 */
	public String getProcTitle() {
		return procTitle;
	}

	/**
	 * @param procTitle the procTitle to set
	 */
	public void setProcTitle(String procTitle) {
		this.procTitle = procTitle;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the specification
	 */
	public String getSpecification() {
		return specification;
	}

	/**
	 * @param specification the specification to set
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}

	/**
	 * @return the anyCategory
	 */
	public String getAnyCategory() {
		return anyCategory;
	}

	/**
	 * @param anyCategory the anyCategory to set
	 */
	public void setAnyCategory(String anyCategory) {
		this.anyCategory = anyCategory;
	}

	/**
	 * @return the allCateogories
	 */
	public String getAllCategories() {
		return allCategories;
	}

	/**
	 * @param allCateogories the allCateogories to set
	 */
	public void setAllCateogories(String allCategories) {
		this.allCategories = allCategories;
	}

	/**
	 * @return the marketResearchDataList
	 */
	public List<Object[]> getMarketResearchDataList() {
		return marketResearchDataList;
	}

	/**
	 * @param marketResearchDataList the marketResearchDataList to set
	 */
	public void setMarketResearchDataList(List<Object[]> marketResearchDataList) {
		this.marketResearchDataList = marketResearchDataList;
	}

	/**
	 * @return the selectionType
	 */
	public String getSelectionType() {
		return selectionType;
	}

	/**
	 * @param selectionType the selectionType to set
	 */
	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
}

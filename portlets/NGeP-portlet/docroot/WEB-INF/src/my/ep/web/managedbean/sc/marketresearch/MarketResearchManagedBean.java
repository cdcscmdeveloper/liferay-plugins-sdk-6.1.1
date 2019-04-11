package my.ep.web.managedbean.sc.marketresearch;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.bean.sc.quatationtender.MarketResearchSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.MarketResearchVO;

@ManagedBean(name = "marketResearchManagedBean")
@SessionScoped
public class MarketResearchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MarketResearchSearchBean marketResearchSearchBean;
	private ScLoa scLoa;
	
	/** The active index. */
	private int activeIndex;
	
	private MarketResearchVO marketResearchVO;
	private boolean isSearch;
	private boolean isQt;
	private String procurementMethod;
	private String itemSpec;
	private ScQt scQt;
	
	public MarketResearchManagedBean(){
		
	}

	/**
	 * @return the marketResearchSearchBean
	 */
	public MarketResearchSearchBean getMarketResearchSearchBean() {
		if(marketResearchSearchBean == null){
			marketResearchSearchBean = new MarketResearchSearchBean();
		}
		return marketResearchSearchBean;
	}

	/**
	 * @param marketResearchSearchBean the marketResearchSearchBean to set
	 */
	public void setMarketResearchSearchBean(
			MarketResearchSearchBean marketResearchSearchBean) {
		this.marketResearchSearchBean = marketResearchSearchBean;
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the marketResearchVO
	 */
	public MarketResearchVO getMarketResearchVO() {
		return marketResearchVO;
	}

	/**
	 * @param marketResearchVO the marketResearchVO to set
	 */
	public void setMarketResearchVO(MarketResearchVO marketResearchVO) {
		this.marketResearchVO = marketResearchVO;
	}

	/**
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}

	/**
	 * @return the isSearch
	 */
	public boolean isSearch() {
		return isSearch;
	}

	/**
	 * @param isSearch the isSearch to set
	 */
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	/**
	 * @return the isQt
	 */
	public boolean isQt() {
		return isQt;
	}

	/**
	 * @param isQt the isQt to set
	 */
	public void setQt(boolean isQt) {
		this.isQt = isQt;
	}

	/**
	 * @return the procurementMethod
	 */
	public String getProcurementMethod() {
		return procurementMethod;
	}

	/**
	 * @param procurementMethod the procurementMethod to set
	 */
	public void setProcurementMethod(String procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	/**
	 * @return the itemSpec
	 */
	public String getItemSpec() {
		return itemSpec;
	}

	/**
	 * @param itemSpec the itemSpec to set
	 */
	public void setItemSpec(String itemSpec) {
		this.itemSpec = itemSpec;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}
	
}

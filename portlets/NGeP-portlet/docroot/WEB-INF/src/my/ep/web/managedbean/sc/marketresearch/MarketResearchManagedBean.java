package my.ep.web.managedbean.sc.marketresearch;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.sc.quatationtender.MarketResearchSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "marketResearchManagedBean")
@SessionScoped
public class MarketResearchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MarketResearchSearchBean marketResearchSearchBean;
	
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
	
	
}

package my.ep.web.backingbean.sc.directpurchase;

public class TabBean {
	
	private Integer tabId;
	private String titleName;
	
	public TabBean(Integer tabId, String titleName) {
		this.tabId = tabId;
		this.titleName = titleName;
	}
	
	/**
	 * @return the tabId
	 */
	public Integer getTabId() {
		return tabId;
	}
	/**
	 * @param tabId the tabId to set
	 */
	public void setTabId(Integer tabId) {
		this.tabId = tabId;
	}

	/**
	 * @return the titleName
	 */
	public String getTitleName() {
		return titleName;
	}

	/**
	 * @param titleName the titleName to set
	 */
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

}

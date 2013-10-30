package my.ep.web.bean.ss.quatationtender;

public class TabBean {
	private String titleName;
	private int tabid;

	public TabBean(String titleName,int tabid){
		this.titleName=titleName;
		this.tabid=tabid;
	}
	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	/**
	 * @return the tabid
	 */
	public int getTabid() {
		return tabid;
	}
	/**
	 * @param tabid the tabid to set
	 */
	public void setTabid(int tabid) {
		this.tabid = tabid;
	}
	

}

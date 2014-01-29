package my.ep.web.bean.sc.quatationtender;

import my.ep.persistence.entity.sc.quationtender.ScQtApproval;

public class TabBean {
	private String titleName;
	private ScQtApproval scQtApproval;

	public TabBean(String titleName, ScQtApproval scQtApproval) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
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

	/**
	 * @return the scQtApproval
	 */
	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	/**
	 * @param scQtApproval the scQtApproval to set
	 */
	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

}

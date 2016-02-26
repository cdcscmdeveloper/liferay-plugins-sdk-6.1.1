/**
 * 
 */
package my.ep.web.managedbean.sc.quationtender;

import java.util.Date;
import java.util.List;

import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * 
 * SourcingBaseBackingBean is the Base class for Quation Tender
 * 
 * @author VidyaSagarReddy
 * @since 18.02.2016
 * 
 */
public abstract class QTBaseManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6534370732566578612L;

	/** The qt. */
	private ScQt qt;

	// Panel Information
	/** The ct panel item. */
	private CtPanelItem ctPanelItem;

	/** The circular type. */
	private String circularType;
	
	/** The selectpanel item. */
	private List<CtPanelItemGroup> panelGroupItems;
	
	private List<CtPanelItem> panelItems;
	
	
	
	/** The circular no. */
	private String circularNo;
	
	/** The circular name. */
	private String circularName;
	
	/** The circular exp date. */
	private Date circularExpDate;
	
	// Drop down Listing for Approver
	private List<PmUser> ministrySpecApprovers; 

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

	/**
	 * @return the ctPanelItem
	 */
	public CtPanelItem getCtPanelItem() {
		return ctPanelItem;
	}

	/**
	 * @param ctPanelItem
	 *            the ctPanelItem to set
	 */
	public void setCtPanelItem(CtPanelItem ctPanelItem) {
		this.ctPanelItem = ctPanelItem;
	}

	/**
	 * @return the circularType
	 */
	public String getCircularType() {
		return circularType;
	}

	/**
	 * @param circularType
	 *            the circularType to set
	 */
	public void setCircularType(String circularType) {
		this.circularType = circularType;
	}

	

	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}

	/**
	 * @param circularNo the circularNo to set
	 */
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	/**
	 * @return the circularName
	 */
	public String getCircularName() {
		return circularName;
	}

	/**
	 * @param circularName the circularName to set
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	/**
	 * @return the circularExpDate
	 */
	public Date getCircularExpDate() {
		return circularExpDate;
	}

	/**
	 * @param circularExpDate the circularExpDate to set
	 */
	public void setCircularExpDate(Date circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	/**
	 * @return the panelGroupItems
	 */
	public List<CtPanelItemGroup> getPanelGroupItems() {
		return panelGroupItems;
	}

	/**
	 * @param panelGroupItems the panelGroupItems to set
	 */
	public void setPanelGroupItems(List<CtPanelItemGroup> panelGroupItems) {
		this.panelGroupItems = panelGroupItems;
	}

	

	public List<PmUser> getMinistrySpecApprovers() {
		return ministrySpecApprovers;
	}

	public void setMinistrySpecApprovers(List<PmUser> ministrySpecApprovers) {
		this.ministrySpecApprovers = ministrySpecApprovers;
	}

	public List<CtPanelItem> getPanelItems() {
		return panelItems;
	}

	public void setPanelItems(List<CtPanelItem> panelItems) {
		this.panelItems = panelItems;
	}
}

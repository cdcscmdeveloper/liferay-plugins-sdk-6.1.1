/**
 * 
 */
package my.ep.web.managedbean.sc.quationtender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.primefaces.model.TreeNode;

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
	
	/** The is zone location. */
	private boolean isZoneLocation;
	
	/** The zone name. */
	private String zoneName;
	
	/** The root zoanl tree. */
	private TreeNode rootZoanlTree;
	
	private boolean isCheckAllList = Boolean.FALSE;
	
	/** The zone general trees. */
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();

	
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

	/**
	 * @return the panelItems
	 */
	public List<CtPanelItem> getPanelItems() {
		return panelItems;
	}

	/**
	 * @param panelItems the panelItems to set
	 */
	public void setPanelItems(List<CtPanelItem> panelItems) {
		this.panelItems = panelItems;
	}

	/**
	 * @return the ministrySpecApprovers
	 */
	public List<PmUser> getMinistrySpecApprovers() {
		return ministrySpecApprovers;
	}

	/**
	 * @param ministrySpecApprovers the ministrySpecApprovers to set
	 */
	public void setMinistrySpecApprovers(List<PmUser> ministrySpecApprovers) {
		this.ministrySpecApprovers = ministrySpecApprovers;
	}

	/**
	 * @return the isZoneLocation
	 */
	public boolean isZoneLocation() {
		return isZoneLocation;
	}

	/**
	 * @param isZoneLocation the isZoneLocation to set
	 */
	public void setZoneLocation(boolean isZoneLocation) {
		this.isZoneLocation = isZoneLocation;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the rootZoanlTree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * @param rootZoanlTree the rootZoanlTree to set
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	/**
	 * @return the isCheckAllList
	 */
	public boolean isCheckAllList() {
		return isCheckAllList;
	}

	/**
	 * @param isCheckAllList the isCheckAllList to set
	 */
	public void setCheckAllList(boolean isCheckAllList) {
		this.isCheckAllList = isCheckAllList;
	}

	/**
	 * @return the zoneGeneralTrees
	 */
	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	/**
	 * @param zoneGeneralTrees the zoneGeneralTrees to set
	 */
	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}
}

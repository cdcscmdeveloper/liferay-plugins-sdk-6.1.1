/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.TreeNode;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.managedbean.BaseManagedBean;


/**
 * The Class ZoneMaintenanceManagedBean.
 * @author hketan
 */
@ManagedBean(name = "zoneMaintenanceMB")
@SessionScoped
public class ZoneMaintenanceManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The default country id. */
	private Long defaultCountryId;
	
	/** The state. */
	private String state;
	
	/** The district. */
	private String district;
	
	/** The division. */
	private String division;
	
	/** The edit state. */
	private Long editState;
	
	/** The edit district. */
	private Long editDistrict;
	
	/** The edit division. */
	private String editDivision;
	
	/** The state id. */
	private long[] stateId;

	/** The district id. */
	private long[] districtId;

	/** The state list. */
	private List<PmState> stateList;

	/** The district list. */
	private List<PmDistrict> districtList;
	
	/** The division list. */
	private List<PmDivision> divisionList;

	/** The cm supplier zone list. */
	private List<CmSupplierZone> cmSupplierZoneList;
	
	/** The deleted cm supplier zone list. */
	private List<CmSupplierZone> deletedCmSupplierZoneList;
	
	/** The cm supplier zone dtl list. */
	private List<CmSupplierZoneDTL> cmSupplierZoneDTLList;
	
	/** The deleted cm supplier zone dtl list. */
	private List<CmSupplierZoneDTL> deletedCmSupplierZoneDTLList;

	/** The selected supplier zone. */
	private CmSupplierZone selectedSupplierZone;
	
	/** The selected supplier zone dtl. */
	private CmSupplierZoneDTL selectedSupplierZoneDTL;
	
	/** The new cm supplier zone. */
	private CmSupplierZone newCmSupplierZone;
	
	/** The new cm supplier zone dtl. */
	private CmSupplierZoneDTL newCmSupplierZoneDTL;
	
	/** The cm supplier zone dtl filter bean. */
	private SearchFilterBean cmSupplierZoneDTLFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	
	/** The selected supp zone id. */
	private String selectedSuppZoneId;
	
	/** The root zone tree. */
	private TreeNode rootZoneTree;
	
	/** The new zone tree. */
	private TreeNode newZoneTree;
	
	/** The zone trees. */
	private List<TreeNode> zoneTrees;
	
	/** The selected nodes. */
	private TreeNode[] selectedNodes;
	
	/** The zone name. */
	private String zoneName;
	
	/** The zone index. */
	private int zoneIndex = -1;

	/** The current supplier. */
	private SmSupplier currentSupplier;
	
	/** The show parent msg. */
	private boolean showParentMsg;
	
	/** The value reqd. */
	private boolean valueReqd;
	
	/** The edit mode. */
	private boolean editMode;
	
	private boolean allZoneSelected;

	private boolean allZoneSelectable;
	
	private List<CmSupplierZone> tempDeletedSupplierZoneList;

	private List<Long> disabledDistrictId;
	
	//[#26282] disciplinary action - start
	private boolean daWarning;
	private boolean daSuspendReg;
	private boolean daCancelReg;
	private boolean daBlacklistOwner;
	//[#26282] disciplinary action - end

	/**
	 * Instantiates a new zone maintenance managed bean.
	 */
	public ZoneMaintenanceManagedBean() {
		super();
	}

	/**
	 * Gets the default country id.
	 * 
	 * @return the default country id
	 */
	public Long getDefaultCountryId() {
		return defaultCountryId;
	}

	/**
	 * Sets the default country id.
	 * 
	 * @param defaultCountryId
	 *            the new default country id
	 */
	public void setDefaultCountryId(Long defaultCountryId) {
		this.defaultCountryId = defaultCountryId;
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public String getState() {
		return state;
	}


	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(String state) {
		this.state = state;
	}


	/**
	 * Gets the district.
	 * 
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}


	/**
	 * Sets the district.
	 * 
	 * @param district
	 *            the new district
	 */
	public void setDistrict(String district) {
		this.district = district;
	}


	/**
	 * Gets the division.
	 * 
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}


	/**
	 * Sets the division.
	 * 
	 * @param division
	 *            the new division
	 */
	public void setDivision(String division) {
		this.division = division;
	}


	/**
	 * Gets the edits the state.
	 * 
	 * @return the edits the state
	 */
	public Long getEditState() {
		return editState;
	}


	/**
	 * Sets the edits the state.
	 * 
	 * @param editState
	 *            the new edits the state
	 */
	public void setEditState(Long editState) {
		this.editState = editState;
	}


	/**
	 * Gets the edits the district.
	 * 
	 * @return the edits the district
	 */
	public Long getEditDistrict() {
		return editDistrict;
	}


	/**
	 * Sets the edits the district.
	 * 
	 * @param editDistrict
	 *            the new edits the district
	 */
	public void setEditDistrict(Long editDistrict) {
		this.editDistrict = editDistrict;
	}


	/**
	 * Gets the edits the division.
	 * 
	 * @return the edits the division
	 */
	public String getEditDivision() {
		return editDivision;
	}


	/**
	 * Sets the edits the division.
	 * 
	 * @param editDivision
	 *            the new edits the division
	 */
	public void setEditDivision(String editDivision) {
		this.editDivision = editDivision;
	}


	/** The zn name. */
	private String znName;
	
	/** The zn desc. */
	private String znDesc;

	/** The zn state. */
	private String znState;
	
	/** The zn division. */
	private String znDivision;
	
	/** The zn district. */
	private String znDistrict;
	
	
	/**
	 * Gets the state id.
	 * 
	 * @return the state id
	 */
	public long[] getStateId() {
		return stateId;
	}

	/**
	 * Sets the state id.
	 * 
	 * @param stateId
	 *            the new state id
	 */
	public void setStateId(long[] stateId) {
		this.stateId = stateId;
	}

	/**
	 * Gets the district id.
	 * 
	 * @return the district id
	 */
	public long[] getDistrictId() {
		return districtId;
	}

	/**
	 * Sets the district id.
	 * 
	 * @param districtId
	 *            the new district id
	 */
	public void setDistrictId(long[] districtId) {
		this.districtId = districtId;
	}

	/**
	 * Gets the district list.
	 * 
	 * @return the district list
	 */
	public List<PmDistrict> getDistrictList() {
		if(districtList==null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}

	/**
	 * Sets the district list.
	 * 
	 * @param districtList
	 *            the new district list
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * Gets the division list.
	 * 
	 * @return the division list
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}


	/**
	 * Sets the division list.
	 * 
	 * @param divisionList
	 *            the new division list
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}


	/**
	 * Gets the state list.
	 * 
	 * @return the state list
	 */
	public List<PmState> getStateList() {
		if(stateList==null)
			stateList = new ArrayList<PmState>();
		return stateList;
	}

	/**
	 * Sets the state list.
	 * 
	 * @param stateList
	 *            the new state list
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the cm supplier zone list.
	 * 
	 * @return the cm supplier zone list
	 */
	public List<CmSupplierZone> getCmSupplierZoneList() {
		if(cmSupplierZoneList==null)
			cmSupplierZoneList = new ArrayList<CmSupplierZone>();
		return cmSupplierZoneList;
	}

	/**
	 * Sets the cm supplier zone list.
	 * 
	 * @param cmSupplierZoneList
	 *            the new cm supplier zone list
	 */
	public void setCmSupplierZoneList(List<CmSupplierZone> cmSupplierZoneList) {
		this.cmSupplierZoneList = cmSupplierZoneList;
	}

	/**
	 * Gets the deleted cm supplier zone list.
	 * 
	 * @return the deleted cm supplier zone list
	 */
	public List<CmSupplierZone> getDeletedCmSupplierZoneList() {
		if(deletedCmSupplierZoneList==null)
			deletedCmSupplierZoneList = new ArrayList<CmSupplierZone>();
		return deletedCmSupplierZoneList;
	}


	/**
	 * Sets the deleted cm supplier zone list.
	 * 
	 * @param deletedCmSupplierZoneList
	 *            the new deleted cm supplier zone list
	 */
	public void setDeletedCmSupplierZoneList(
			List<CmSupplierZone> deletedCmSupplierZoneList) {
		this.deletedCmSupplierZoneList = deletedCmSupplierZoneList;
	}


	/**
	 * Gets the cm supplier zone dtl list.
	 * 
	 * @return the cm supplier zone dtl list
	 */
	public List<CmSupplierZoneDTL> getCmSupplierZoneDTLList() {
		if(cmSupplierZoneDTLList==null)
			cmSupplierZoneDTLList = new ArrayList<CmSupplierZoneDTL>();
		return cmSupplierZoneDTLList;
	}

	/**
	 * Sets the cm supplier zone dtl list.
	 * 
	 * @param cmSupplierZoneDTLList
	 *            the new cm supplier zone dtl list
	 */
	public void setCmSupplierZoneDTLList(List<CmSupplierZoneDTL> cmSupplierZoneDTLList) {
		this.cmSupplierZoneDTLList = cmSupplierZoneDTLList;
	}

	/**
	 * Gets the deleted cm supplier zone dtl list.
	 * 
	 * @return the deleted cm supplier zone dtl list
	 */
	public List<CmSupplierZoneDTL> getDeletedCmSupplierZoneDTLList() {
		return deletedCmSupplierZoneDTLList;
	}


	/**
	 * Sets the deleted cm supplier zone dtl list.
	 * 
	 * @param deletedCmSupplierZoneDTLList
	 *            the new deleted cm supplier zone dtl list
	 */
	public void setDeletedCmSupplierZoneDTLList(
			List<CmSupplierZoneDTL> deletedCmSupplierZoneDTLList) {
		this.deletedCmSupplierZoneDTLList = deletedCmSupplierZoneDTLList;
	}


	/**
	 * Gets the selected supplier zone.
	 * 
	 * @return the selected supplier zone
	 */
	public CmSupplierZone getSelectedSupplierZone() {
		if(selectedSupplierZone==null)
			selectedSupplierZone = new CmSupplierZone();
		return selectedSupplierZone;
	}

	/**
	 * Sets the selected supplier zone.
	 * 
	 * @param selectedSupplierZone
	 *            the new selected supplier zone
	 */
	public void setSelectedSupplierZone(CmSupplierZone selectedSupplierZone) {
		this.selectedSupplierZone = selectedSupplierZone;
	}

	/**
	 * Gets the selected supplier zone dtl.
	 * 
	 * @return the selected supplier zone dtl
	 */
	public CmSupplierZoneDTL getSelectedSupplierZoneDTL() {
		if(selectedSupplierZoneDTL == null)
			selectedSupplierZoneDTL = new CmSupplierZoneDTL();
		return selectedSupplierZoneDTL;
	}


	/**
	 * Sets the selected supplier zone dtl.
	 * 
	 * @param selectedSupplierZoneDTL
	 *            the new selected supplier zone dtl
	 */
	public void setSelectedSupplierZoneDTL(CmSupplierZoneDTL selectedSupplierZoneDTL) {
		this.selectedSupplierZoneDTL = selectedSupplierZoneDTL;
	}


	/**
	 * Gets the zn state.
	 * 
	 * @return the zn state
	 */
	public String getZnState() {
		return znState;
	}

	/**
	 * Sets the zn state.
	 * 
	 * @param znState
	 *            the new zn state
	 */
	public void setZnState(String znState) {
		this.znState = znState;
	}

	/**
	 * Gets the zn division.
	 * 
	 * @return the zn division
	 */
	public String getZnDivision() {
		return znDivision;
	}

	/**
	 * Sets the zn division.
	 * 
	 * @param znDivision
	 *            the new zn division
	 */
	public void setZnDivision(String znDivision) {
		this.znDivision = znDivision;
	}

	/**
	 * Gets the zn district.
	 * 
	 * @return the zn district
	 */
	public String getZnDistrict() {
		return znDistrict;
	}

	/**
	 * Sets the zn district.
	 * 
	 * @param znDistrict
	 *            the new zn district
	 */
	public void setZnDistrict(String znDistrict) {
		this.znDistrict = znDistrict;
	}
	
	
	
	
	/**
	 * Gets the new cm supplier zone.
	 * 
	 * @return the new cm supplier zone
	 */
	public CmSupplierZone getNewCmSupplierZone() {
		if(newCmSupplierZone==null)
			newCmSupplierZone = new CmSupplierZone();
		return newCmSupplierZone;
	}


	/**
	 * Sets the new cm supplier zone.
	 * 
	 * @param newCmSupplierZone
	 *            the new new cm supplier zone
	 */
	public void setNewCmSupplierZone(CmSupplierZone newCmSupplierZone) {
		this.newCmSupplierZone = newCmSupplierZone;
	}


	/**
	 * Gets the new cm supplier zone dtl.
	 * 
	 * @return the new cm supplier zone dtl
	 */
	public CmSupplierZoneDTL getNewCmSupplierZoneDTL() {
		if(newCmSupplierZoneDTL == null)
			newCmSupplierZoneDTL = new CmSupplierZoneDTL();
		return newCmSupplierZoneDTL;
	}


	/**
	 * Sets the new cm supplier zone dtl.
	 * 
	 * @param newCmSupplierZoneDTL
	 *            the new new cm supplier zone dtl
	 */
	public void setNewCmSupplierZoneDTL(CmSupplierZoneDTL newCmSupplierZoneDTL) {
		this.newCmSupplierZoneDTL = newCmSupplierZoneDTL;
	}


	/**
	 * Gets the cm supplier zone dtl filter bean.
	 * 
	 * @return the cm supplier zone dtl filter bean
	 */
	public SearchFilterBean getCmSupplierZoneDTLFilterBean() {
		return cmSupplierZoneDTLFilterBean;
	}

	/**
	 * Sets the cm supplier zone dtl filter bean.
	 * 
	 * @param cmSupplierZoneDTLFilterBean
	 *            the new cm supplier zone dtl filter bean
	 */
	public void setCmSupplierZoneDTLFilterBean(
			SearchFilterBean cmSupplierZoneDTLFilterBean) {
		this.cmSupplierZoneDTLFilterBean = cmSupplierZoneDTLFilterBean;
	}




	/**
	 * Gets the zn name.
	 * 
	 * @return the zn name
	 */
	public String getZnName() {
		return znName;
	}


	/**
	 * Sets the zn name.
	 * 
	 * @param znName
	 *            the new zn name
	 */
	public void setZnName(String znName) {
		this.znName = znName;
	}




	/**
	 * Gets the zn desc.
	 * 
	 * @return the zn desc
	 */
	public String getZnDesc() {
		return znDesc;
	}


	/**
	 * Sets the zn desc.
	 * 
	 * @param znDesc
	 *            the new zn desc
	 */
	public void setZnDesc(String znDesc) {
		this.znDesc = znDesc;
	}




	/**
	 * Gets the selected supp zone id.
	 * 
	 * @return the selected supp zone id
	 */
	public String getSelectedSuppZoneId() {
		return selectedSuppZoneId;
	}


	/**
	 * Sets the selected supp zone id.
	 * 
	 * @param selectedSuppZoneId
	 *            the new selected supp zone id
	 */
	public void setSelectedSuppZoneId(String selectedSuppZoneId) {
		this.selectedSuppZoneId = selectedSuppZoneId;
	}




	/**
	 * Gets the root zone tree.
	 * 
	 * @return the root zone tree
	 */
	public TreeNode getRootZoneTree() {
		return rootZoneTree;
	}


	/**
	 * Sets the root zone tree.
	 * 
	 * @param rootZoneTree
	 *            the new root zone tree
	 */
	public void setRootZoneTree(TreeNode rootZoneTree) {
		this.rootZoneTree = rootZoneTree;
	}




	/**
	 * Gets the new zone tree.
	 * 
	 * @return the new zone tree
	 */
	public TreeNode getNewZoneTree() {
		return newZoneTree;
	}


	/**
	 * Sets the new zone tree.
	 * 
	 * @param newZoneTree
	 *            the new new zone tree
	 */
	public void setNewZoneTree(TreeNode newZoneTree) {
		this.newZoneTree = newZoneTree;
	}


	/**
	 * Gets the zone trees.
	 * 
	 * @return the zone trees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}


	/**
	 * Sets the zone trees.
	 * 
	 * @param zoneTrees
	 *            the new zone trees
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}




	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}


	/**
	 * Sets the selected nodes.
	 * 
	 * @param selectedNodes
	 *            the new selected nodes
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}


	/**
	 * Gets the zone name.
	 * 
	 * @return the zone name
	 */
	public String getZoneName() {
		return zoneName;
	}


	/**
	 * Sets the zone name.
	 * 
	 * @param zoneName
	 *            the new zone name
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Gets the zone index.
	 * 
	 * @return the zone index
	 */
	public int getZoneIndex() {
		return zoneIndex;
	}

	/**
	 * Sets the zone index.
	 * 
	 * @param zoneIndex
	 *            the new zone index
	 */
	public void setZoneIndex(int zoneIndex) {
		this.zoneIndex = zoneIndex;
	}


	/**
	 * Gets the current supplier.
	 * 
	 * @return the current supplier
	 */
	public SmSupplier getCurrentSupplier() {
		return currentSupplier;
	}


	/**
	 * Sets the current supplier.
	 * 
	 * @param currentSupplier
	 *            the new current supplier
	 */
	public void setCurrentSupplier(SmSupplier currentSupplier) {
		this.currentSupplier = currentSupplier;
	}

	/**
	 * Checks if is show parent msg.
	 * 
	 * @return true, if is show parent msg
	 */
	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	/**
	 * Sets the show parent msg.
	 * 
	 * @param showParentMsg
	 *            the new show parent msg
	 */
	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	/**
	 * Checks if is value reqd.
	 * 
	 * @return true, if is value reqd
	 */
	public boolean isValueReqd() {
		return valueReqd;
	}

	/**
	 * Sets the value reqd.
	 * 
	 * @param valueReqd
	 *            the new value reqd
	 */
	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	/**
	 * Checks if is edits the mode.
	 * 
	 * @return true, if is edits the mode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * Sets the edits the mode.
	 * 
	 * @param editMode
	 *            the new edits the mode
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isAllZoneSelected() {
		return allZoneSelected;
	}

	public void setAllZoneSelected(boolean allZoneSelected) {
		this.allZoneSelected = allZoneSelected;
	}

	public boolean isAllZoneSelectable() {
		return allZoneSelectable;
	}

	public void setAllZoneSelectable(boolean allZoneSelectable) {
		this.allZoneSelectable = allZoneSelectable;
	}

	public List<CmSupplierZone> getTempDeletedSupplierZoneList() {
		return tempDeletedSupplierZoneList;
	}

	public void setTempDeletedSupplierZoneList(
			List<CmSupplierZone> tempDeletedSupplierZoneList) {
		this.tempDeletedSupplierZoneList = tempDeletedSupplierZoneList;
	}

	public List<Long> getDisabledDistrictId() {
		return disabledDistrictId;
	}

	public void setDisabledDistrictId(List<Long> disabledDistrictId) {
		this.disabledDistrictId = disabledDistrictId;
	}

	public boolean isDaWarning() {
		return daWarning;
	}

	public void setDaWarning(boolean daWarning) {
		this.daWarning = daWarning;
	}

	public boolean isDaSuspendReg() {
		return daSuspendReg;
	}

	public void setDaSuspendReg(boolean daSuspendReg) {
		this.daSuspendReg = daSuspendReg;
	}

	public boolean isDaCancelReg() {
		return daCancelReg;
	}

	public void setDaCancelReg(boolean daCancelReg) {
		this.daCancelReg = daCancelReg;
	}

	public boolean isDaBlacklistOwner() {
		return daBlacklistOwner;
	}

	public void setDaBlacklistOwner(boolean daBlacklistOwner) {
		this.daBlacklistOwner = daBlacklistOwner;
	}

}

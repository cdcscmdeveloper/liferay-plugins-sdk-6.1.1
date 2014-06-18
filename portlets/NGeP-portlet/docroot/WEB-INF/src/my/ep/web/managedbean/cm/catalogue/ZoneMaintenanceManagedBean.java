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

@ManagedBean(name = "zoneMaintenanceMB")
@SessionScoped
public class ZoneMaintenanceManagedBean extends BaseManagedBean {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String state;
	
	private String district;
	
	private String division;
	
	private Long editState;
	
	private Long editDistrict;
	
	private String editDivision;
	
	private long[] stateId;

	private long[] districtId;

	private List<PmState> stateList;

	private List<PmDistrict> districtList;
	
	private List<PmDivision> divisionList;

	private List<CmSupplierZone> cmSupplierZoneList;
	
	private List<CmSupplierZone> deletedCmSupplierZoneList;
	
	private List<CmSupplierZoneDTL> cmSupplierZoneDTLList;
	
	private List<CmSupplierZoneDTL> deletedCmSupplierZoneDTLList;

	private CmSupplierZone selectedSupplierZone;
	
	private CmSupplierZoneDTL selectedSupplierZoneDTL;
	
	private CmSupplierZone newCmSupplierZone;
	
	private CmSupplierZoneDTL newCmSupplierZoneDTL;
	
	private SearchFilterBean cmSupplierZoneDTLFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	
	private String selectedSuppZoneId;
	
	private TreeNode rootZoneTree;
	
	private TreeNode newZoneTree;
	
	private List<TreeNode> zoneTrees;
	
	private TreeNode[] selectedNodes;
	
	private String zoneName;
	
	private int zoneIndex = -1;

	private SmSupplier currentSupplier;
	
	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getDivision() {
		return division;
	}


	public void setDivision(String division) {
		this.division = division;
	}


	public Long getEditState() {
		return editState;
	}


	public void setEditState(Long editState) {
		this.editState = editState;
	}


	public Long getEditDistrict() {
		return editDistrict;
	}


	public void setEditDistrict(Long editDistrict) {
		this.editDistrict = editDistrict;
	}


	public String getEditDivision() {
		return editDivision;
	}


	public void setEditDivision(String editDivision) {
		this.editDivision = editDivision;
	}


	private String znName;
	private String znDesc;

	private String znState;
	private String znDivision;
	private String znDistrict;
	
	
	public long[] getStateId() {
		return stateId;
	}

	public void setStateId(long[] stateId) {
		this.stateId = stateId;
	}

	public long[] getDistrictId() {
		return districtId;
	}

	public void setDistrictId(long[] districtId) {
		this.districtId = districtId;
	}

	public List<PmDistrict> getDistrictList() {
		if(districtList==null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}

	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	public List<PmDivision> getDivisionList() {
		return divisionList;
	}


	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}


	public List<PmState> getStateList() {
		if(stateList==null)
			stateList = new ArrayList<PmState>();
		return stateList;
	}

	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	public List<CmSupplierZone> getCmSupplierZoneList() {
		if(cmSupplierZoneList==null)
			cmSupplierZoneList = new ArrayList<CmSupplierZone>();
		return cmSupplierZoneList;
	}

	public void setCmSupplierZoneList(List<CmSupplierZone> cmSupplierZoneList) {
		this.cmSupplierZoneList = cmSupplierZoneList;
	}

	public List<CmSupplierZone> getDeletedCmSupplierZoneList() {
		return deletedCmSupplierZoneList;
	}


	public void setDeletedCmSupplierZoneList(
			List<CmSupplierZone> deletedCmSupplierZoneList) {
		this.deletedCmSupplierZoneList = deletedCmSupplierZoneList;
	}


	public List<CmSupplierZoneDTL> getCmSupplierZoneDTLList() {
		if(cmSupplierZoneDTLList==null)
			cmSupplierZoneDTLList = new ArrayList<CmSupplierZoneDTL>();
		return cmSupplierZoneDTLList;
	}

	public void setCmSupplierZoneDTLList(List<CmSupplierZoneDTL> cmSupplierZoneDTLList) {
		this.cmSupplierZoneDTLList = cmSupplierZoneDTLList;
	}

	public List<CmSupplierZoneDTL> getDeletedCmSupplierZoneDTLList() {
		return deletedCmSupplierZoneDTLList;
	}


	public void setDeletedCmSupplierZoneDTLList(
			List<CmSupplierZoneDTL> deletedCmSupplierZoneDTLList) {
		this.deletedCmSupplierZoneDTLList = deletedCmSupplierZoneDTLList;
	}


	public CmSupplierZone getSelectedSupplierZone() {
		if(selectedSupplierZone==null)
			selectedSupplierZone = new CmSupplierZone();
		return selectedSupplierZone;
	}

	public void setSelectedSupplierZone(CmSupplierZone selectedSupplierZone) {
		this.selectedSupplierZone = selectedSupplierZone;
	}

	public CmSupplierZoneDTL getSelectedSupplierZoneDTL() {
		if(selectedSupplierZoneDTL == null)
			selectedSupplierZoneDTL = new CmSupplierZoneDTL();
		return selectedSupplierZoneDTL;
	}


	public void setSelectedSupplierZoneDTL(CmSupplierZoneDTL selectedSupplierZoneDTL) {
		this.selectedSupplierZoneDTL = selectedSupplierZoneDTL;
	}


	public String getZnState() {
		return znState;
	}

	public void setZnState(String znState) {
		this.znState = znState;
	}

	public String getZnDivision() {
		return znDivision;
	}

	public void setZnDivision(String znDivision) {
		this.znDivision = znDivision;
	}

	public String getZnDistrict() {
		return znDistrict;
	}

	public void setZnDistrict(String znDistrict) {
		this.znDistrict = znDistrict;
	}
	
	
	
	
	public CmSupplierZone getNewCmSupplierZone() {
		if(newCmSupplierZone==null)
			newCmSupplierZone = new CmSupplierZone();
		return newCmSupplierZone;
	}


	public void setNewCmSupplierZone(CmSupplierZone newCmSupplierZone) {
		this.newCmSupplierZone = newCmSupplierZone;
	}


	public CmSupplierZoneDTL getNewCmSupplierZoneDTL() {
		if(newCmSupplierZoneDTL == null)
			newCmSupplierZoneDTL = new CmSupplierZoneDTL();
		return newCmSupplierZoneDTL;
	}


	public void setNewCmSupplierZoneDTL(CmSupplierZoneDTL newCmSupplierZoneDTL) {
		this.newCmSupplierZoneDTL = newCmSupplierZoneDTL;
	}


	public SearchFilterBean getCmSupplierZoneDTLFilterBean() {
		return cmSupplierZoneDTLFilterBean;
	}

	public void setCmSupplierZoneDTLFilterBean(
			SearchFilterBean cmSupplierZoneDTLFilterBean) {
		this.cmSupplierZoneDTLFilterBean = cmSupplierZoneDTLFilterBean;
	}




	public String getZnName() {
		return znName;
	}


	public void setZnName(String znName) {
		this.znName = znName;
	}




	public String getZnDesc() {
		return znDesc;
	}


	public void setZnDesc(String znDesc) {
		this.znDesc = znDesc;
	}




	public String getSelectedSuppZoneId() {
		return selectedSuppZoneId;
	}


	public void setSelectedSuppZoneId(String selectedSuppZoneId) {
		this.selectedSuppZoneId = selectedSuppZoneId;
	}




	public TreeNode getRootZoneTree() {
		return rootZoneTree;
	}


	public void setRootZoneTree(TreeNode rootZoneTree) {
		this.rootZoneTree = rootZoneTree;
	}




	public TreeNode getNewZoneTree() {
		return newZoneTree;
	}


	public void setNewZoneTree(TreeNode newZoneTree) {
		this.newZoneTree = newZoneTree;
	}


	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}


	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}




	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}


	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}


	public String getZoneName() {
		return zoneName;
	}


	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getZoneIndex() {
		return zoneIndex;
	}

	public void setZoneIndex(int zoneIndex) {
		this.zoneIndex = zoneIndex;
	}


	public SmSupplier getCurrentSupplier() {
		return currentSupplier;
	}


	public void setCurrentSupplier(SmSupplier currentSupplier) {
		this.currentSupplier = currentSupplier;
	}

}

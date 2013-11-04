
package my.ep.web.managedbean.ss.quationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;


/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name="qTDetailMB")
@SessionScoped
public class QTDetailManagedBean extends BaseManagedBean {

	
	private static final long serialVersionUID = 1L;
	private static final Logger log= EpLogger.getLogger(QTDetailManagedBean.class);
	
	private ScQt qt;
	private ScQtCriteria qtc;
	
	private List<PmSo> soList;
	
	private List<PmDo> doList;
	
	private List<PmGo> goList;
	
	private List<PmCategoryL1> categoryL1s;
	
	private List<PmCategoryL2> categoryL2s;
	
	private List<PmCategoryL3> categoryL3s;
	
	private List<PmState> states;
	
	private List<PmDistrict> districts;
	
	public List<ScQtGosodoBudget> gosodos;
	public List<ScQtCategory> categorys;
   
	private String qtNumber;
	
	private List<SelectItem> procurementModeItems;
	List<SelectItem> itemTypeList;
	List<SelectItem> tenderTypeList;
	List<SelectItem> lineItemList;
	List<SelectItem> itemOriginList;
	
	
	private TreeNode root2;
	private TreeNode root3;

	

	private TreeNode[] selectedNodes; 
	
	private List<ScQt> cloneTenderList= new ArrayList<ScQt>();
	private ScQt tenderCriteria=new ScQt();
	
	private List<PmZone> zoneList= new ArrayList <PmZone>();
	private List<PmState> stateList=new ArrayList<PmState>();

	
	private List<ScQt> qtList= new ArrayList<ScQt>();
	
	private List<CtPanelSupplier> panelSuppliers= new ArrayList<CtPanelSupplier>();
	public QTDetailManagedBean()
    {
    	log.info("inside ");
    	
	
    	root2 = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("Clone All", root2);
		node0.setExpanded(true);

		TreeNode node1 = new DefaultTreeNode("Qtn/Tender Title", node0);
		
		TreeNode node2 = new DefaultTreeNode("Qtn/Tender Details", node0);
		
		TreeNode node3 = new DefaultTreeNode("Approval Workflow", node0);

		TreeNode node4 = new DefaultTreeNode("Supplier Qualification Criteria", node0);

		TreeNode node5 = new DefaultTreeNode("Schedule", node0);

		TreeNode node6 = new DefaultTreeNode("Documents", node0);
		
		root3 = new DefaultTreeNode("Root", null);   
        TreeNode documents = new DefaultTreeNode(("Zone1"), root3);  
        documents.setExpanded(true);  
        
          
        TreeNode states = new DefaultTreeNode(("State1"), documents); 
        states .setExpanded(true);
        TreeNode districts = new DefaultTreeNode(("State2"), documents); 
        districts.setExpanded(true);
          
        //Documents  
        TreeNode d1 = new DefaultTreeNode(("District1"), states);  
        TreeNode d2 = new DefaultTreeNode(("District2"), states);  
        TreeNode d = new DefaultTreeNode(("District1"), districts);  
          
		
		
		
		
	}


		public List<ScQt> getCloneTenderList() {
			return cloneTenderList;
		}



		public void setCloneTenderList(List<ScQt> cloneTenderList) {
			this.cloneTenderList = cloneTenderList;
		}


	public TreeNode getRoot2() {
		return root2;
	}



	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	public TreeNode getRoot3() {
		return root3;
	}


	public void setRoot3(TreeNode root3) {
		this.root3 = root3;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}


	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}


	public List<SelectItem> getProcurementModeItems() {
		return procurementModeItems;
	}

	public void setProcurementModeItems(List<SelectItem> procurementModeItems) {
		this.procurementModeItems = procurementModeItems;
	}


	public List<SelectItem> getItemTypeList() {
		return itemTypeList;
	}



	public void setItemTypeList(List<SelectItem> itemTypeList) {
		this.itemTypeList = itemTypeList;
	}



	public List<SelectItem> getTenderTypeList() {
		return tenderTypeList;
	}



	public void setTenderTypeList(List<SelectItem> tenderTypeList) {
		this.tenderTypeList = tenderTypeList;
	}



	public List<SelectItem> getLineItemList() {
		return lineItemList;
	}



	public void setLineItemList(List<SelectItem> lineItemList) {
		this.lineItemList = lineItemList;
	}


	public List<PmSo> getSoList() {
		return soList;
	}

	public void setSoList(List<PmSo> soList) {
		this.soList = soList;
	}

	public List<PmDo> getDoList() {
		return doList;
	}

	public void setDoList(List<PmDo> doList) {
		this.doList = doList;
	}

	public List<PmGo> getGoList() {
		return goList;
	}

	public void setGoList(List<PmGo> goList) {
		this.goList = goList;
	}

	public List<PmCategoryL1> getCategoryL1s() {
		return categoryL1s;
	}

	public void setCategoryL1s(List<PmCategoryL1> categoryL1s) {
		this.categoryL1s = categoryL1s;
	}

	public List<PmCategoryL2> getCategoryL2s() {
		return categoryL2s;
	}

	public void setCategoryL2s(List<PmCategoryL2> categoryL2s) {
		this.categoryL2s = categoryL2s;
	}

	public List<PmCategoryL3> getCategoryL3s() {
		return categoryL3s;
	}

	public void setCategoryL3s(List<PmCategoryL3> categoryL3s) {
		this.categoryL3s = categoryL3s;
	}

	public String getQtNumber() {
		return qtNumber;
	}

	public void setQtNumber(String qtNumber) {
		this.qtNumber = qtNumber;
	}



	public ScQt getQt() {
		return qt;
	}



	public void setQt(ScQt qt) {
		this.qt = qt;
	}



	public List<PmState> getStates() {
		return states;
	}



	public void setStates(List<PmState> states) {
		this.states = states;
	}



	public List<PmDistrict> getDistricts() {
		return districts;
	}



	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}



	public List<ScQtGosodoBudget> getGosodos() {
		return gosodos;
	}



	public void setGosodos(List<ScQtGosodoBudget> gosodos) {
		this.gosodos = gosodos;
	}



	public List<ScQtCategory> getCategorys() {
		return categorys;
	}



	public void setCategorys(List<ScQtCategory> categorys) {
		this.categorys = categorys;
	}

	public ScQtCriteria getQtc() {
		return qtc;
	}


	public void setQtc(ScQtCriteria qtc) {
		this.qtc = qtc;
	}

	
	public List<SelectItem> getItemOriginList() {
		return itemOriginList;
	}

	public void setItemOriginList(List<SelectItem> itemOriginList) {
		this.itemOriginList = itemOriginList;
	}

	public ScQt getTenderCriteria() {
		return tenderCriteria;
	}

	public void setTenderCriteria(ScQt tenderCriteria) {
		this.tenderCriteria = tenderCriteria;
	}


	public List<PmZone> getZoneList() {
		return zoneList;
	}


	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}


	public List<PmState> getStateList() {
		return stateList;
	}


	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}


	public List<ScQt> getQtList() {
		return qtList;
	}


	public void setQtList(List<ScQt> qtList) {
		this.qtList = qtList;
	}


	public List<CtPanelSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}


	public void setPanelSuppliers(List<CtPanelSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}


	
	
	

}

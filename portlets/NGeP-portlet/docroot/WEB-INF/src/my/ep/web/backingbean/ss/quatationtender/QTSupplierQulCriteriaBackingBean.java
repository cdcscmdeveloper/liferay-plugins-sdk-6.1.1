/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.common.categorys.CategorysReqDTO;
import my.ep.dto.pm.common.categorys.CategorysResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.managedbean.ss.quationtender.Node;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="qTSupplierQulCriteriaBackingBean")
@RequestScoped
public class QTSupplierQulCriteriaBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log=EpLogger.getLogger(QTSupplierQulCriteriaBackingBean.class);
	private Boolean renderZone;
	private Boolean checked;
	
	private ICategorysSO categorysSO = null;
	public QTSupplierQulCriteriaBackingBean(){
		System.out.println("::::::::::::::::::::::;");
    System.out.println("Supplier Object Created");		
	}
	@PostConstruct
	public void initializeObejcts() {
		ICategorysSO categorysSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", ICategorysSO.class);
		this.categorysSO=categorysSO;
		this.getManagedBean().setQtCriteria(new ScQtCriteria());
		this.getManagedBean().setSearchSupplier(new SmSupplier());
		
	}
	
	/*
	 * Method to load the categorieslist for suppliers screen
	 */
	public void retriveQtCriteria() {

		// Retrive categoryL1 and set to managedbean for firsttime other wise
		// skip
		List<PmCategoryL1> l1categoryList = this.getManagedBean()
				.getCategoryL1s();
		if (l1categoryList == null || !l1categoryList.isEmpty()) {
			CategorysResDTO catResDTO = categorysSO.retiveCategoryL1List();
			if (catResDTO.getCategoryL1List() != null
					&& !catResDTO.getCategoryL1List().isEmpty()) {
				this.getManagedBean().setCategoryL1s(
						catResDTO.getCategoryL1List());
			}
		}
	}
			


	

	/*
	 * To add empty row to category open code data table
	 */
	public void addNewCategoryItem() {
		/*List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();*/
		List<ScQtCategory> categories=this.getManagedBean().getOpenCategorys();
		if (categories == null) {
			categories = new ArrayList<ScQtCategory>();

		} 
		System.out.println("New Category Creation");
		retriveQtCriteria();
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(new PmCategoryL1());
		categories.add(category);
		this.getManagedBean().setOpenCategorys(categories);
		//this.getManagedBean().getQt().setScQtCategorys(categories);
	}
	
	/*
	 * To add empty row to category restricted code data table
	 */
	public void addNewRestrictedCategoryItem() {
		/*List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();*/
		List<ScQtCategory> categories=this.getManagedBean().getRestrictedCategorys();
		if (categories == null) {
			categories = new ArrayList<ScQtCategory>();

		} 
		System.out.println("New Category Creation");
		retriveQtCriteria();
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(new PmCategoryL1());
		categories.add(category);
		this.getManagedBean().setRestrictedCategorys(categories);
		//this.getManagedBean().getQt().setScQtCategorys(categories);
	}


	/*
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCode(ValueChangeEvent changeEvent) {
		ScQtCategory category = (ScQtCategory) changeEvent.getNewValue();
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories.contains(category)) {
			categories.remove(category);
		}

	}



	/*
	 * Method to call onchange of L1category
	 */
	public void handleL2CategoryChange(AjaxBehaviorEvent event) {
		//System.out.println(this.getManagedBean());
		Object categoryL1Id=event.getComponent().getAttributes().get("value");
		//PmCategoryL1 categoryL1 = (PmCategoryL1) changeEvent.getNewValue();
		if (null != categoryL1Id) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL1CategoryId((Long)categoryL1Id);
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL2ListByL1CategoryId(reqDTO);
			List<PmCategoryL2> catL2List = categorysResDTO.getCategoryL2List();
			this.getManagedBean().getCategoryL2s().clear();
			this.getManagedBean().setCategoryL2s(catL2List);
		}
	}

	/*
	 * Method to call onchange of L2category
	 */
	public void handleL3CategoryChange(AjaxBehaviorEvent event) {
		Object categoryL2Id=event.getComponent().getAttributes().get("value");
	//	PmCategoryL2 categoryL2 = (PmCategoryL2) changeEvent.getNewValue();
		if (null != categoryL2Id) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL2CategoryId((Long)categoryL2Id);
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL3ListByL2CategoryId(reqDTO);
			List<PmCategoryL3> catL3List = categorysResDTO.getCategoryL3List();
			this.getManagedBean().getCategoryL3s().clear();
			this.getManagedBean().setCategoryL3s(catL3List);
		}
	}

		
	/**
	 * To retrive districts based on state value changes.
	 */
	public void handleStateChange(ValueChangeEvent changeEvent) {
		PmState state = (PmState) changeEvent.getNewValue();
		if (state != null && state.getStateId() != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(state.getStateId());
			IDistrictSO districtSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);
			if (resDTO != null && !resDTO.getDistrictList().isEmpty()) {
				this.getManagedBean().getDistricts().clear();
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}
		}
	}

	/**
	 * Method to search the supplier for panel and restricted tender.
	 */
	public void searchQtPanelSupplier(){
	
		List<SmSupplier> panelSupplier = retriverSuppliersForPanelQT();
		
		if(!getManagedBean().getPanelSuppliers().isEmpty()){
			getManagedBean().getPanelSuppliers().clear();
		}
		getManagedBean().getPanelSuppliers().addAll(panelSupplier);
	}
	/**
	 * Method to search the supplier for normal tender
	 */
	public void searchNormalQtSupplier(){
		
	}
	private List<SmSupplier> retriverSuppliersForPanelQT(){
		
		CircularReqDTO reqDTO = new CircularReqDTO();
		ISearchBean<CtPanelSupplier> searchBean = new SearchBean<CtPanelSupplier>();
		
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		SearchFilterBean likeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			
		equalFilterBean.setSearchField("ctCircular.circularNo");
		equalFilterBean.setSearchValue("PCR_00001");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		likeFilterBean.setSearchField("ctPanelSupplierItems.ctPanelItem.itemName");
		likeFilterBean.setSearchValue("Pen");
		searchBean.getDataFilterBeans().add(likeFilterBean);
		
		equalFilterBean.setSearchField("ctCircular.name");
		equalFilterBean.setSearchValue("");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		equalFilterBean.setSearchField("");
		equalFilterBean.setSearchValue("");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		reqDTO.setSearchBean(searchBean);
		CircularResDTO resDTO=null;
		try{
		ICircularSO circularSO= EpEjbBusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
		resDTO = circularSO.searchPanelSuppliers(reqDTO);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resDTO.getSupplierList();
		//resDTO.getSearchResultBean().getResultList();
	}
	public void addState(){
		  if(null!=this.getManagedBean().getSelectedPmStateList()){
			  for(String stateId:this.getManagedBean().getSelectedPmStateList()){
				  Node selectedZone=this.getManagedBean().getSelectedZone();
				  TreeNode root=this.getManagedBean().getRoot3();
				  List<TreeNode> nodeList=root.getChildren();
				  if(null!=nodeList){
					  for(TreeNode treeNode:nodeList){
						  Node node=(Node) treeNode.getData();
						  if(selectedZone.getNodeName().equals(node.getNodeName())){
							 PmState state=QTDetailManagedBean.stateMap.get(new Long(stateId));
									 TreeNode stateNode=new DefaultTreeNode(new Node(state.getStateId(),state.getStateName(),"State"), treeNode);
									 treeNode.setExpanded(true);
									 break;
						  }
					  }
				  }
			  }
		  }
	  }
		
		public void addDistrict() {
			System.out.println("Hi");
			if (null != this.getManagedBean().getSelectedPmDistricts()) {
				TreeNode root = this.getManagedBean().getRoot3();
				Node selectedState = this.getManagedBean().getSelectedState();
				TreeNode selectedTreeNode = getSeletedTreeNode(root, selectedState);
				if (null == selectedTreeNode) {
					return;
				}
				for (String districtId : this.getManagedBean()
						.getSelectedPmDistricts()) {
					PmDistrict district = QTDetailManagedBean.districtMap
							.get(new Long(districtId));
					TreeNode stateNode = new DefaultTreeNode(new Node(
							district.getDistrictId(), district.getDistrictName(),
							"District"), selectedTreeNode);
					selectedTreeNode.setExpanded(true);
				}
			}
		}
	  
	  public TreeNode getSeletedTreeNode(TreeNode treeNode,Node selectedNode){
		  TreeNode selecetedTreeNode=null;
			  for(TreeNode childNode:treeNode.getChildren()){
				  Node node=(Node) childNode.getData();
				  if(selectedNode.getNodeName().equals(node.getNodeName())){
					  selecetedTreeNode= childNode; 
					  break;
				  }
				  for(TreeNode childNode1:childNode.getChildren()){
					  Node node1=(Node) childNode1.getData();
					  if(selectedNode.getNodeName().equals(node1.getNodeName())){
						  selecetedTreeNode= childNode1; 
						  break;
					  } 
				  }
			  } 
			  return selecetedTreeNode;
		  }
		  
	  
	  public void displayPmStateDlg(Node selectedNode){
		  //List<PmZone> pmZoneList =QTDetailManagedBean.pmZoneList;
		 /* this.getManagedBean().getPmZoneOptions().clear();
		  this.getManagedBean().getPmZoneOptions().add(new SelectItem(0,"Select Zone"));
			if(null!=pmZoneList){
				for(PmZone zone:pmZoneList){
					this.getManagedBean().getPmZoneOptions().add(new SelectItem(zone.getZoneId(),zone.getZoneName()));
				}
			}*/
		  System.out.println("State");
		  if(null!=selectedNode){
			  this.getManagedBean().setSelectedZone(selectedNode);
			  List<PmState> pmStates=QTDetailManagedBean.zoneStateMap.get(selectedNode.getNodeId());
			  this.getManagedBean().setPmStates(pmStates);
		  }
		  

	  }
	  public void displayPmDistrictDlg(Node selectedNode){
		 if(null!=selectedNode){
			  this.getManagedBean().setSelectedState(selectedNode);
			  List<PmDistrict> pmDistricts=QTDetailManagedBean.stateDistrictMap.get(selectedNode.getNodeId());
			  this.getManagedBean().setPmDistricts(pmDistricts);
		  }
		 TreeNode treeNode=this.getManagedBean().getRoot3().getChildren().get(0);
		  if("State".equals(((Node)treeNode.getData()).getNodeType())){
			 renderZone=false;
		  } else {
			  renderZone=true;
		  }
		 /* if(!"zone".equals(zonePage)){
			  this.getManagedBean().setSelectedZone(null);
		  }*/
		  

	  }
	 
	  public void handleZoneChange(ValueChangeEvent event){
		  System.out.println(event.getNewValue());
		  
	  }
	  public void onNodeSelect(NodeSelectEvent event) { 
		  Node selectedNode=(Node) event.getTreeNode().getData();
		  this.getManagedBean().setSelectedZone(selectedNode);
	  }
	  
		public void multiLocationChange(AjaxBehaviorEvent event) {
			System.out.println("ADSFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			/*System.out.println("PPPPPPPPPPPPPPPPPP" +event.getNewValue());
			Boolean multipleLoc = (Boolean) event.getNewValue();

			TreeNode root3 = new DefaultTreeNode("Root", null);
			if (multipleLoc) {
				List<PmZone> pmZoneList = QTDetailManagedBean.pmZoneList;
				if (null != pmZoneList) {
					for (PmZone zone : pmZoneList) {
						TreeNode documents = new DefaultTreeNode(new Node(
								zone.getZoneId(), zone.getZoneName(), "Zone"),
								root3);
						documents.setSelectable(true);
					}
				}

			} else {
				List<PmState> pmStateList = QTDetailManagedBean.pmStateList;
				if (null != pmStateList) {
					for (PmState state : pmStateList) {
						TreeNode documents = new DefaultTreeNode(new Node(
								state.getStateId(), state.getStateName(), "State"),
								root3);
						documents.setSelectable(true);
					}
				}

			}
			this.getManagedBean().setRoot3(root3);*/

		}
	public void addNewZone(){
		if(null!=this.getManagedBean().getSelectedPmZone() && null!=this.getManagedBean().getSelectedPmZone().getZoneName()){
			TreeNode root=this.getManagedBean().getRoot3();
			Node node=new Node();
			node.setNodeName(this.getManagedBean().getSelectedPmZone().getZoneName());
			node.setNodeType("Zone");
			node.setNodeId(new Long (this.getManagedBean().pmZoneList.size()+1));
			TreeNode newZone= new DefaultTreeNode(node, root);
		}
	}
	 
	public void addNewState(){
		if(null!=this.getManagedBean().getSelectedPmState() && null!=this.getManagedBean().getSelectedPmState().getStateName()){
			TreeNode root=this.getManagedBean().getRoot3();
			Node node=new Node();
			node.setNodeName(this.getManagedBean().getSelectedPmState().getStateName());
			node.setNodeType("State");
			node.setNodeId(new Long (this.getManagedBean().pmStateList.size()+1));
			TreeNode newZone= new DefaultTreeNode(node, root);
		}
	}

	public Boolean getRenderZone() {
		return renderZone;
	}

	public void setRenderZone(Boolean renderZone) {
		this.renderZone = renderZone;
	}
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public void processChecked(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setSelectedBoolean1(id);

	}
	
	public void tenderChange(AjaxBehaviorEvent event) {
		System.out.println("HIIIIIIIIIIIIIIII");
		Object selectedValue = event.getComponent().getAttributes()
				.get("value");
		if ("B".equalsIgnoreCase((String) selectedValue)
				|| "O".equalsIgnoreCase((String) selectedValue)) {
			this.getManagedBean().setIsTender(true);
			this.getManagedBean().setIsPanel(false);
			this.getManagedBean().setIsResctricted(false);
		} else if("P".equalsIgnoreCase((String) selectedValue)) {
			this.getManagedBean().setIsTender(false);
			this.getManagedBean().setIsPanel(true);
			this.getManagedBean().setIsResctricted(false);
		} else if("R".equalsIgnoreCase((String) selectedValue)){
			this.getManagedBean().setIsTender(false);
			this.getManagedBean().setIsPanel(false);
			this.getManagedBean().setIsResctricted(true);
		}
	}


}

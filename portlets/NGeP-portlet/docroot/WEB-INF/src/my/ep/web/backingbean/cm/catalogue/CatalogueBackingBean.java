package my.ep.web.backingbean.cm.catalogue;

import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;


@ManagedBean(name="catalogueBackingBean")
@RequestScoped
public class CatalogueBackingBean extends BaseBackingBean<CatalogueManagedBean>{

	private static Logger log = Logger.getLogger(CatalogueBackingBean.class);
	private TreeNode root;
	private TreeNode selectedNode;
	private TreeNode root1; 
	private TreeNode root2; 

	public CatalogueBackingBean(){


		root = new DefaultTreeNode("Root", null);  


		TreeNode segment = new DefaultTreeNode("segment", "WishList 1", root);
		TreeNode segment1 = new DefaultTreeNode("segment", "WishList 2", root);
		TreeNode segment2 = new DefaultTreeNode("segment", "WishList 3", root);


		TreeNode families = new DefaultTreeNode("families","whishlist1.1", segment);
		TreeNode families1 = new DefaultTreeNode("families","whishlist1.2", segment1);
		TreeNode families2 = new DefaultTreeNode("families","whishlist1.3", segment2);


		root1 = new DefaultTreeNode("Root1", null);  
		TreeNode CartList = new DefaultTreeNode("CartList", root1);  

		TreeNode CartList00 = new DefaultTreeNode("Node 0.0", CartList);  
		TreeNode CartList01 = new DefaultTreeNode("Node 0.1", CartList); 


		root2 = new DefaultTreeNode("Root2", null);  

		TreeNode SupplierList = new DefaultTreeNode("SupplierList", root2);  

		TreeNode SupplierList01 = new DefaultTreeNode("Node 0.0", SupplierList);  
		TreeNode SupplierList02 = new DefaultTreeNode("Node 0.1", SupplierList); 

		this.getManagedBean().setAdvanceSearchList(new ArrayList<AdvanceSearchVO>());
		for(int i=0; i<3; i++){
			this.getManagedBean().getAdvanceSearchList().add(new AdvanceSearchVO());
		}

		this.getManagedBean().setSupplierItemList(null);
		PricingVO test = new PricingVO();
		test.setPricingSeq(new Long(1));
		test.setCurrency("name");
		this.getManagedBean().getSupplierItemList().add(test);
	}



	public String GoToMaintain(){





		return "../catalogue/catalogMaintenanceFile.xhtml";
	}



	public String navigateToCatalogDet(){

		log.debug("*****navigateToCatalogDet*****");
		return "addCatalogItem";

	}

	public void addNewPricing(){

		log.debug("*****addNewPricing*****");
		PricingVO price = new PricingVO();

		this.getManagedBean().getPricingList().add(price);

	}

	public void addNewZonalPricing(){

		int intNo = (int)Math.floor((Math.random()*1000));
		long longNo = (long)intNo;	

		PricingVO price = new PricingVO();

		price.setPricingSeq(longNo);



		this.getManagedBean().getZonalPricingList().add(price);

	}

	public void districtChecked(){

		log.debug("********hktan**********");


		log.debug("boolean:"+this.getManagedBean().getSelectedPricing().isSelected());
		if(this.getManagedBean().getSelectedPricing().isSelected()){
			this.getManagedBean().setDistrictIND(true);
		}else{
			this.getManagedBean().setDistrictIND(false);
		}
	}

	public void addNewDistrictPricing(){
		PricingVO price = new PricingVO();

		this.getManagedBean().getDistrictPricingList().add(price);
	}


	public String cancelToMaintenanceFile(){


		return "catalogMaintenanceFile";
	}

	public String okToMaintenanceFile(){


		return "catalogMaintenanceFile";
	}

	public void advanceSearch(){

		log.debug("#########advanceSearch#############"+this.getManagedBean().isAdvanceSearchInd());
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
		if(this.getManagedBean().getSearchType().equalsIgnoreCase("S")){
			this.getManagedBean().setAdvanceSearchInd(false);
		}else{
			this.getManagedBean().setAdvanceSearchInd(true);
		}


	}

	public void addIntoWishList() {

		log.debug("==============hktan========================");

		this.getManagedBean().setSelectedSupplierItemList(null);

		if(this.getManagedBean().getSelectedSupplierItems() != null && this.getManagedBean().getSelectedSupplierItems().length > 0) {
			for(int i = 0; i < this.getManagedBean().getSelectedSupplierItems().length; i++) {
				PricingVO catalog = this.getManagedBean().getSelectedSupplierItems()[i];
				this.getManagedBean().getSelectedSupplierItemList().add(catalog);
			}			
		}

		//genTempImages(this.getManagedBean().getSelectedCatalogList());

		/*for (CatCatalog beanNew : this.getManagedBean().getCatalogList()) {  
			log.info("hktan beanNew.isSelected()" + beanNew.isSelected());
			log.info("hktan beanNew.getCatalogSeq()" + beanNew.getCatalogSeq());
			if (beanNew.isSelected() && !isExistWishList(beanNew.getCatalogSeq())) {

				this.getManagedBean().getSelectedCatalogList().add(beanNew);

			}else if(!beanNew.isSelected()){
				this.getManagedBean().getSelectedCatalogList().remove(beanNew);

			}

		}*/

	}





	public TreeNode getRoot() {
		return root;
	}



	public void setRoot(TreeNode root) {
		this.root = root;
	}



	public TreeNode getRoot1() {
		return root1;
	}



	public void setRoot1(TreeNode root1) {
		this.root1 = root1;
	}



	public TreeNode getRoot2() {
		return root2;
	}



	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}



	public TreeNode getSelectedNode() {
		return selectedNode;
	}



	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public void onNodeDelete() { 


		FacesContext context = FacesContext.getCurrentInstance();

		Map map2 = context.getExternalContext().getRequestParameterMap();
		String newCodes = (String) map2.get("name1");

		log.debug("***event.getTreeNode()*****:"+newCodes);
	}



}

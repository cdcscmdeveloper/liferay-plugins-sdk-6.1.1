package my.ep.web.backingbean.cm.catalogue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

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





		return "catalogueMaintenanceFile";
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



	public void uploadExcel(FileUploadEvent event) throws Exception {
		try {
			log.debug("uploadExcel");
			UploadedFile item = event.getFile();


			log.debug("item.getFileName()"+item.getFileName());


			SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

			FileInputStream file = (FileInputStream)item.getInputstream();

			//Get the workbook instance for XLS file 
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);


			//Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();

			long name = 0;
			String desc = null;
			Map map = new HashMap();



			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if(row.getCell(0)!=null){
					name =  (long) row.getCell(0).getNumericCellValue();
					log.debug("name:"+name);



				}

				if(row.getCell(1)!=null){
					desc = row.getCell(1).getStringCellValue();
					log.debug("desc:"+desc);

				}
				map.put(name, desc);
			}







			log.debug("********sheet2**********");

			file.close();
			//FileOutputStream out = 
			//    new FileOutputStream(new File("C"));
			// workbook.write(out);
			//  out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StreamedContent file;  

	public StreamedContent getFile() throws Exception {

		log.debug("***write***");
		PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename="+"testing22.xlsx");
		response.flushBuffer();
		
		
		//Read Excel document first
		FileInputStream input_document = new FileInputStream(new File("C:\\excel1\\Catalogue-offline-template.xlsx"));
		// convert it into a POI object
		XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document); 
		// Read excel sheet that needs to be updated
		XSSFSheet sheet = my_xlsx_workbook.getSheetAt(1);


		




		//sheet.protectSheet("welcome1"); 

		CellStyle catalogCellStyle = my_xlsx_workbook.createCellStyle();

		//add the 1st title row
		List labelList = new Vector();

		labelList.add("Extension Code");
		labelList.add("Extension Name");
		labelList.add("Frequency");
		labelList.add("Item Description");
		labelList.add("SKU Number");
		labelList.add("UOM");
		labelList.add("Lead Time");
		labelList.add("Payment Term");
		labelList.add("Model Number");
		labelList.add("Manufacturer ID");


		addCellToRow(sheet, 10, labelList, catalogCellStyle);

		List rowLs = new Vector();
		int no = 11;


		rowLs = new Vector();

		rowLs.add("testing");
		rowLs.add("");
		addCellToRow(sheet, no++, rowLs, catalogCellStyle);

		input_document.close();
		
		my_xlsx_workbook.write(response.getOutputStream());
		
		//close the stream
		

		return null;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public void addCellToRow(XSSFSheet sheet, int count, List colValList, CellStyle catalogCellStyle) throws Exception {  

		Row row = sheet.createRow(count);

		for(int i = 0; i < colValList.size(); i++){
			Object colVal = (Object)colValList.get(i);

			Cell cell = row.createCell(i);
			if(i==0){
				catalogCellStyle.setLocked(false);
				cell.setCellStyle(catalogCellStyle);
			}
			if(i==1){
				catalogCellStyle.setLocked(false);
				cell.setCellStyle(catalogCellStyle);
			}
			if(colVal==null) colVal = new String("");

			if(colVal instanceof String){
				cell.setCellValue((String)colVal);
			}else if (colVal instanceof java.lang.Boolean){
				cell.setCellValue(((java.lang.Boolean)colVal).booleanValue());
			}else if (colVal instanceof BigDecimal){ 
				cell.setCellValue(((BigDecimal)colVal).doubleValue());
			}else if (colVal instanceof Date){
				cell.setCellValue((Date)colVal);
			}

		}

	}
	
	
	public void RainTest(){
		
		log.info("**********rain***************"+this.getManagedBean().getSelectedPricing().getPrice());
		this.getManagedBean().getSelectedPricing().setPricingSeq(new Long(1234));
		this.getManagedBean().getSelectedPricing().setPrice(this.getManagedBean().getSelectedPricing().getPrice());
		
		this.getManagedBean().getSupplierItemList().add(this.getManagedBean().getSelectedPricing());
		
	}




}

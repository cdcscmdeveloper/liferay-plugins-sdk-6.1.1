package my.ep.web.backingbean.cm.catalogue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IWishListSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.catalogue.WishListReqDTO;
import my.ep.dto.cm.catalogue.WishListResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.cm.catalog.CmImage;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
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

	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());


	public CatalogueBackingBean(){


		//		IFavoriteSupplierSO favorSO = (IFavoriteSupplierSO)BusinessDelegate.getInstance().getServiceObject("cm",IFavoriteSupplierSO.class);
		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);

		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		this.getManagedBean().setWishLists(null);

		WishListReqDTO reqDTO = new WishListReqDTO();

		WishListResDTO resDTO = wishListSO.retrieveWishList(reqDTO);
		this.getManagedBean().setWishLists(resDTO.getWishLists());



		this.getManagedBean().setAdvanceSearchList(new ArrayList<AdvanceSearchVO>());
		for(int i=0; i<3; i++){
			this.getManagedBean().getAdvanceSearchList().add(new AdvanceSearchVO());
		}

		IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
		StateReqDTO reqStateDTO = new StateReqDTO();
		reqStateDTO.setCountryId(1l);
		StateResDTO resStateDTO = stateSO.retriveStatesListByCountryID(reqStateDTO);

		this.getManagedBean().setStateList(resStateDTO.getStateList());


	}



	public void addToMaintain(){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		IRegistrationSO smSO = (IRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm",IRegistrationSO.class);

		RegistrationReqDTO smReqDTO = new RegistrationReqDTO();
		smReqDTO.setSupplierId(new Long(1));

		RegistrationResDTO smResDTO = smSO.retrieveSuppOnlineAcc(smReqDTO);

		CodificationBackingBean bb = new CodificationBackingBean();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		log.info("addToMaintain:"+bb.getManagedBean().getSelectedItems().length);
		if(bb.getManagedBean().getSelectedItems() !=null && bb.getManagedBean().getSelectedItems().length>0){
			for(int i = 0; i < bb.getManagedBean().getSelectedItems().length; i++) {
				CmSupplierItem supplierItem = new CmSupplierItem();

				CmItem item = bb.getManagedBean().getSelectedItems()[i];
				log.info("item:"+item);
				supplierItem.setCmItem(item);
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getSmSupp());
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);

			}		
			catalogueSO.saveSupplierItem(reqDTO);
		}





		//return "catalogueMaintenanceFile";
	}



	public String GoToMaintain(){


		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("itemStatus");
		statusFilterBean.setSearchValue("D");
		searchBean.getDataFilterBeans().add(statusFilterBean);



		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);
		log.info("catalogueSO:"+catalogueSO);

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
		System.out.println("resDTO.getSupplierItemList():"+resDTO.getSupplierItemList().size());
		this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());




		return "catalogueMaintenanceFile";
	}



	public String navigateToCatalogDet(int index){

		log.info("*****this.getManagedBean().getMaintainItemList().get(index).getCmImages().size()*****:"+this.getManagedBean().getMaintainItemList().get(index).getCmImages().size());

		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getMaintainItemList().get(index));

		return "addCatalogItem";

	}

	public String navigateToPublishedItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getPublishedItemList().get(index));

		return "addCatalogItem";

	}

	public String navigateToExpiredItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getExpiredItemList().get(index));

		return "addCatalogItem";

	}

	public String navigateToApprovalItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getApprovalItemList().get(index));

		return "addCatalogItem";

	}

	public String navigateToApprovedItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getApprovedItemList().get(index));

		return "addCatalogItem";

	}

	public String navigateToRejectedItemDet(int index){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getRejectedItemList().get(index));



		CmSupplierItem catalog = this.getManagedBean().getRejectedItemList().get(index);
		catalog.setItemStatus("D");
		reqDTO.getSaveSupplierItems().add(catalog);

		try {
			CatalogueResDTO resDTO =  catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "addCatalogItem";

	}


	public String navigateToSupplierItemDet(int index){

		log.info("*****this.getManagedBean().getMaintainItemList().get(index).getCmImages().size()*****:"+this.getManagedBean().getSupplierItemList().get(index).getCmImages().size());

		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getSupplierItemList().get(index));

		return "addCatalogItem";
	}

	public void addNewPromotionPricing(){

		log.debug("*****addNewPromotionPricing*****");
		CmPromotionPricing price = new CmPromotionPricing();

		this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings().add(price);
		//this.getManagedBean().getPromotionPriceList().add(price);
	}

	public void addNewTierPricing(){

		log.debug("*****addNewTierPricing*****");
		CmTierPricing price = new CmTierPricing();

		this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().add(price);
	}
	public void addNewPromotionTierPricing(){

		log.debug("*****addNewPromotionTierPricing*****");
		CmPromotionTierPricing price = new CmPromotionTierPricing();

		this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().add(price);
	}





	public void addNewZonalTierPricing(){



		CmZonalTierPricing price = new CmZonalTierPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().add(price);

	}

	public void addNewZonalPromotionTierPricing(){



		CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().add(price);

	}



	public void addNewZonalStandardPricing(){



		CmZonalStandardPricing price = new CmZonalStandardPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().add(price);

	}


	public void addNewZonalPromotionPricing(){



		CmZonalPromotionPricing price = new CmZonalPromotionPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().add(price);

	}

	public void districtChecked(){

		log.debug("********hktan**********");


		/*		log.debug("boolean:"+this.getManagedBean().getSelectedPricing().isSelected());
		if(this.getManagedBean().getSelectedPricing().isSelected()){
			this.getManagedBean().setDistrictIND(true);
		}else{
			this.getManagedBean().setDistrictIND(false);
		}*/
	}

	public void addNewDistrictPricing(){
		PricingVO price = new PricingVO();

		this.getManagedBean().getDistrictPricingList().add(price);
	}


	public String cancelToMaintenanceFile(){

		log.info("*********cancelToMaintenanceFile*************");

		return "catalogMaintenanceFile?faces-redirect=true";
	}

	public String okToMaintenanceFile(){

		log.info("*********okToMaintenanceFile*************");


		for(CmPricing price : this.getManagedBean().getSelectedMaintainItem().getCmPricings()){
			System.out.println("price.getPrice()"+price.getPrice());
		}


		return "catalogMaintenanceFile?faces-redirect=true";
		//return "catalogMaintenanceFile";
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



	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}



	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}


	public void imageUpload(FileUploadEvent event) throws Exception {



		UploadedFile item = event.getFile();
		//	this.getManagedBean().setImageObj(new CatImage());
		//	this.getManagedBean().getImageObj().setImage(item.getContents());
		//	this.getManagedBean().getImageObj().setDescription(item.getFileName());

		CmImage catImage = new CmImage();
		catImage.setImage(item.getContents());
		catImage.setImageName(item.getFileName());
		catImage.setImageDesc(item.getFileName());
		catImage.setImageType("jpg");

		log.info("item.getFileName()"+item.getFileName());

		//render for display
		this.getManagedBean().getSelectedMaintainItem().getCmImages().add(catImage);// getCatalogImages().add(this.getManagedBean().getImageObj());

		genMultipleImage(this.getManagedBean().getSelectedMaintainItem().getCmImages());



		log.info("size:"+this.getManagedBean().getSelectedMaintainItem().getCmImages().size());



	}


	public void genMultipleImage(List<CmImage> imageList){
		try {	
			String tempDir = System.getProperty("java.io.tmpdir");

			for(int i=0; i<imageList.size(); i++){
				CmImage image = (CmImage)imageList.get(i);



				//String filename = catalog.getCatProduct().getProductCode().concat("_").concat(catalog.getCatProduct().getProductSeq().toString()).concat("_").concat(catalog.getCatalogSeq().toString()).concat(".jpg");
				String filename = image.getImageDesc().concat("_").concat(Integer.toString(i));

				File CmImage = new File(tempDir + filename);

				image.setImagePath(filename);

				//if(!catImage.exists()) {
				BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(new FileOutputStream(tempDir + filename));
				bufferedOutputStream.write(image.getImage());

				if (bufferedOutputStream != null) {
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}


			}

			//}
		}catch(Exception e) {
			log.error(e);
		}


	}


	public String saveCatalogItem() {
		log.info("In saveCatalogItem");
		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();



		ICatalogueSO catalogueSO = null;

		try {

			catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
			CatalogueReqDTO reqDTO  = new CatalogueReqDTO();


			for(CmPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}
			for(CmPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}
			for(CmTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			for(CmZonalStandardPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}

			for(CmZonalPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}

			for(CmZonalTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}

			for(CmZonalPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}





			reqDTO.getSaveSupplierItems().add(this.getManagedBean().getSelectedMaintainItem());
			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Saved Successfully", ""));

		}catch(Exception ex){
			System.out.println("***********error********");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Error", ""));

			return "addCatalogView";
		}finally {
			catalogueSO = null;
		}

		log.info("Exit saveCatalogItem");

		//return "catalogMaintenanceFile?faces-redirect=true";
		return "preRenderCatalogueMaintenanceFile";
	}



	public void submitToReviewItems(){
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		System.out.println("this.getManagedBean().getSelectedMaintainItems().length:"+this.getManagedBean().getSelectedMaintainItems().length);
		for(int i=0; i<this.getManagedBean().getSelectedMaintainItems().length; i++){
			CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];
			catalog.setItemStatus("P");
			reqDTO.getSaveSupplierItems().add(catalog);
		}


		CatalogueResDTO resDTO = new CatalogueResDTO();
		try {
			resDTO = catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("resDTO.isSuccessful():"+resDTO.isSuccessful());
		if(resDTO.isSuccessful()){
			ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("D");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));



		}
	}


	public void searchCatalogue(){
		log.info("*******searchCatalogue************");  

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("itemStatus");
		statusFilterBean.setSearchValue("D");
		searchBean.getDataFilterBeans().add(statusFilterBean);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);
		log.info("catalogueSO:"+catalogueSO);

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setSupplierItemList(resDTO.getSupplierItemList());

	}




	public void compareIndxeAction(){

		log.debug("****index***"+this.getManagedBean().getCompareIndex());

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("name1");
		Integer index = Integer.parseInt(name1);

		CmSupplierItem catalog = this.getManagedBean().getSelectedSupplierItemList().get(index);

		this.getManagedBean().setCatalogueObj(catalog);
		this.getManagedBean().setCompareIndex(index);


	} 

	public String backToPervious(){

		return "supplierItemView";
	}


	public void onTabChange(TabChangeEvent event) {  
		System.out.println("title"+event.getTab().getTitle());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();


		if(event.getTab().getTitle().equalsIgnoreCase("Draft")){

			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("D");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Published")){
			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("U");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Pending Approval")){

			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("P");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Approved")){

			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("A");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Rejected")){
			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("R");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());

		}

	}  



	public String viewCartListItem(){

		return "cartListItemView";
	}

	public void deleteCartItem(int index){

		this.getManagedBean().getCartList().remove(index);
	}


	/*@PostConstruct
	public void init() {


		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);
		Layout layout = themeDisplay.getLayout();
		System.out.println("layout.getFriendlyURL():"+layout.getFriendlyURL());
		if (layout.getFriendlyURL().equals("/maintain-catalogue")) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

			ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("D");
			searchBean.getDataFilterBeans().add(statusFilterBean);



			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setSearchBean(searchBean);
			log.info("catalogueSO:"+catalogueSO);

			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

		}
	}*/


	public void publishCatalogItem(){
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		System.out.println("this.getManagedBean().getSelectedApprovedItems().length:"+this.getManagedBean().getSelectedApprovedItems().length);
		for(int i=0; i<this.getManagedBean().getSelectedApprovedItems().length; i++){
			CmSupplierItem catalog = this.getManagedBean().getSelectedApprovedItems()[i];
			catalog.setItemStatus("U");
			reqDTO.getSaveSupplierItems().add(catalog);
		}


		CatalogueResDTO resDTO = new CatalogueResDTO();
		try {
			resDTO = catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("resDTO.isSuccessful():"+resDTO.isSuccessful());
		if(resDTO.isSuccessful()){
			ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
			statusFilterBean.setSearchField("itemStatus");
			statusFilterBean.setSearchValue("A");
			searchBean.getDataFilterBeans().add(statusFilterBean);

			reqDTO.setSearchBean(searchBean);
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Published Successfully", ""));


		}
	}




	public String compareSupplierItem(){

		log.info("==============compareSupplierItem========================");



		log.info("size:"+this.getManagedBean().getSelectedSupplierItemList().size());
		this.getManagedBean().setCompareIndex(0);
		CmSupplierItem catalog = this.getManagedBean().getSelectedSupplierItemList().get(0);

		this.getManagedBean().setCatalogueObj(catalog);


		return "compareCatalogue";
	}



	public void addToCart(int index){

		//for(CmSupplierItem supplierItem : this.getManagedBean().getSelectedSupplierItemList()){

		this.getManagedBean().getTempList().add(this.getManagedBean().getSupplierItemList().get(index));
		//	}



		for(CmSupplierItem temp:this.getManagedBean().getTempList()){
			if(!this.getManagedBean().getTempMap().containsKey(temp.getSupplierItemId())){
				this.getManagedBean().getCartList().add(temp);
				this.getManagedBean().getTempMap().put(temp.getSupplierItemId(), temp.getSupplierItemId());
			}
		}
	}

	public void triggerCompare(int index){

		System.out.println("***********triggerCompare************"+index);


		this.getManagedBean().setSelectedSupplierItemList(null);

		for(CmSupplierItem supplierItem:this.getManagedBean().getSupplierItemList()){
			if(supplierItem.isSelected()){
				this.getManagedBean().getSelectedSupplierItemList().add(supplierItem);
			}
		}

	}

	public void addIntoWishList() {



		this.getManagedBean().setSelectedSupplierItemList(null);

		for(CmSupplierItem supplierItem:this.getManagedBean().getSupplierItemList()){
			if(supplierItem.isSelected()){
				this.getManagedBean().getSelectedSupplierItemList().add(supplierItem);
			}
		}

		log.info("size:"+this.getManagedBean().getSelectedSupplierItemList().size());

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


	public void deleteComparedItem(int index){

		this.getManagedBean().getSelectedSupplierItemList().remove(index);
	}


	public void preRenderCatalogue(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("itemStatus");
		statusFilterBean.setSearchValue("D");
		searchBean.getDataFilterBeans().add(statusFilterBean);



		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);
		log.info("catalogueSO:"+catalogueSO);

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());


		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "catalogMaintenanceFile");

	}


	public String backToPrev(){


		return "supplierItemView";
	}


	public void retrieveDistrict(int index){

		System.out.println("**********retrieveDistrict******************");



		DistrictReqDTO reqDTO = new DistrictReqDTO();
		System.out.println("pricing.getPmState().getStateId()"+this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().get(index).getPmState().getStateId());


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());

		this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().get(index).setPmDistricts(resDTO.getDistrictList());
		if(resDTO.getDistrictList()!=null && resDTO.getDistrictList().size()>0){
			this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().get(index).setDistrictDisabled(false);
		}else{
			this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings().get(index).setDistrictDisabled(true);
		}
	}


	public void retrievePromotionDistrict(int index ){

		System.out.println("**********retrievePromotionDistrict******************");

		DistrictReqDTO reqDTO = new DistrictReqDTO();
		System.out.println("pricing.getPmState().getStateId()"+this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).getPmState().getStateId());


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).setPmDistricts(resDTO.getDistrictList());

	}


	public void retrieveTierDistrict(int index){

		System.out.println("**********retrieveDistrict******************");

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		System.out.println("pricing.getPmState().getStateId()"+this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getPmState().getStateId());


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).setPmDistricts(resDTO.getDistrictList());

	}


	public void retrievePromotionTierDistrict(int index){
		System.out.println("**********retrieveDistrict******************");

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(index).setPmDistricts(resDTO.getDistrictList());

	}




}

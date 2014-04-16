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
import java.util.Set;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IWishListSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmImage;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmTierPricing;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.ExtensionCodeVO;
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
@ViewScoped
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

		/*this.getManagedBean().setWishLists(null);

		WishListReqDTO reqDTO = new WishListReqDTO();

		WishListResDTO resDTO = wishListSO.retrieveWishList(reqDTO);
		this.getManagedBean().setWishLists(resDTO.getWishLists());*/



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







	public String addToMaintain(){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO)BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);


		IRegistrationSO smSO = (IRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm",IRegistrationSO.class);

		RegistrationReqDTO smReqDTO = new RegistrationReqDTO();
		smReqDTO.setSupplierId(new Long(1));

		RegistrationResDTO smResDTO = smSO.retrieveSuppOnlineAcc(smReqDTO);

		CodificationBackingBean bb = new CodificationBackingBean();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		System.out.println("addToMaintain:"+bb.getManagedBean().getSelectedObject().length);

		bb.getManagedBean().setExtensionVoList(null);
		for(int i = 0; i < bb.getManagedBean().getSelectedObject().length; i++) {
			ExtensionCodeVO extCodeVO = new ExtensionCodeVO();
			Object[] objects = (Object[])bb.getManagedBean().getSelectedObject()[i];


			extCodeVO.setTypeId(Long.valueOf(String.valueOf(objects[0])));
			extCodeVO.setBrandId(Long.valueOf(String.valueOf(objects[1])));
			extCodeVO.setMeasurementId(Long.valueOf(String.valueOf(objects[2])));
			extCodeVO.setColorId(Long.valueOf(String.valueOf(objects[3])));

			System.out.println("******type NAme******"+String.valueOf(objects[4]));
			extCodeVO.setTypeName(String.valueOf(objects[4]));
			extCodeVO.setBrandName(String.valueOf(objects[5]));
			extCodeVO.setMeasurementName(String.valueOf(objects[6]));
			extCodeVO.setColorName(String.valueOf(objects[7]));

			bb.getManagedBean().getExtensionVoList().add(extCodeVO);


		}



		if(bb.getManagedBean().getExtensionVoList() !=null && bb.getManagedBean().getExtensionVoList().size()>0){
			for(int i = 0; i < bb.getManagedBean().getExtensionVoList().size(); i++) {

				ExtensionCodeVO obj = bb.getManagedBean().getExtensionVoList().get(i);

				codeReqDTO.setBrandId(obj.getBrandId());
				codeReqDTO.setMeasurementId(obj.getMeasurementId());
				codeReqDTO.setTypeId(obj.getTypeId());
				codeReqDTO.setColorId(obj.getColorId());

				CodificationResDTO codeResDTO =  codificationSO.searchItem(codeReqDTO);


				if(codeResDTO.getItemList()!=null && codeResDTO.getItemList().size()>0){

					for(CmItem item : codeResDTO.getItemList()){
						CmSupplierItem supplierItem = new CmSupplierItem();

						supplierItem.setCmItem(item);
						supplierItem.setItemStatus("D");
						supplierItem.setSmSupplier(smResDTO.getSmSupp());
						supplierItem.setRecordStatus(true);
						reqDTO.getSupplierItem().add(supplierItem);
					}


				}else{
					System.out.println("************else*******************");
					CmType typeObj = bb.searchTypeOjbyTypeId(obj.getTypeId());
					CmBrand brandObj = bb.searchBrandObjByBrandId(obj.getBrandId());
					CmMeasurement meaObj = bb.searchMeasurementByMeasurementId(obj.getMeasurementId());
					CmColor colorObj = bb.searchColorObjByColorId(obj.getColorId());

					codeReqDTO.setUnspscCode(bb.getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
					codeResDTO =  codificationSO.searchCommodityObj(codeReqDTO);

					CmItem item = new CmItem();
					item.setCmUnspsc(codeResDTO.getCommodityObj());
					item.setCmType(typeObj);
					item.setCmBrand(brandObj);
					item.setCmMeasurement(meaObj);
					item.setCmColor(colorObj);
					item.setItemName(bb.getManagedBean().getSelectedUNSPSCObj().getUnspscTitle() + " " +typeObj.getTypeName() + " " + brandObj.getBrandName() + " " + meaObj.getMeasurementName() + " "+colorObj.getColorName());
					item.setExtensionCode(bb.getManagedBean().getSelectedUNSPSCObj().getUnspscCode()+typeObj.getTypeCode()+brandObj.getBrandCode()+meaObj.getMeasurementCode()+colorObj.getColorCode());

					System.out.println("item Name:"+item.getItemName());
					codeReqDTO.setCmItem(item);
					codeResDTO =  codificationSO.saveNewItem(codeReqDTO);

					CmSupplierItem supplierItem = new CmSupplierItem();

					supplierItem.setCmItem(codeResDTO.getCmItem());
					supplierItem.setItemStatus("D");
					supplierItem.setSmSupplier(smResDTO.getSmSupp());
					supplierItem.setRecordStatus(true);
					reqDTO.getSupplierItem().add(supplierItem);
				}



			}		
			catalogueSO.saveSupplierItem(reqDTO);
		}






		return "../catalogue/preRenderCatalogueMaintenanceFile.xhtml";
	}



	public String GoToMaintain(){


		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setItemStatus("D");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
		System.out.println("resDTO.getSupplierItemList():"+resDTO.getSupplierItemList().size());
		this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());




		return "catalogueMaintenanceFile";
	}


	public void deleteDraftItem(int index){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setCmSupplierItemObj(this.getManagedBean().getMaintainItemList().get(index));

		CatalogueResDTO resDTO = catalogueSO.deleteDraftItem(reqDTO);

		if(resDTO.isSuccessful()){

			this.getManagedBean().getMaintainItemList().remove(index);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Deleted Successfully", ""));
		}

	}


	public String navigateToCatalogDet(int index){


		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSupplierId(new Long(1));

		CatalogueResDTO resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);

		System.out.println("size:"+resDTO.getSupplierZoneLs().size());

		this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

		log.info("*****this.getManagedBean().getMaintainItemList().get(index).getCmImages().size()*****:"+this.getManagedBean().getMaintainItemList().get(index).getCmImages().size());
		ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();
		bb.getManagedBean().setCatDetTabActiveIndex(0);

		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getMaintainItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getMaintainItemList().get(index).getItemDesc());


		return "addCatalogItem";

	}

	public String navigateToPublishedItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getPublishedItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getPublishedItemList().get(index).getItemDesc());
		this.getManagedBean().setTabActiveIndex(4);
		return "addCatalogItem";

	}

	public String navigateToExpiredItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getExpiredItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getExpiredItemList().get(index).getItemDesc());
		this.getManagedBean().setTabActiveIndex(5);

		return "addCatalogItem";

	}

	public String navigateToApprovalItemDet(int index){



		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setDocType("CMR");
		statusReqDTO.setModuleCode("CM");
		statusReqDTO.setLanguageCode(languageCode);

		StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);


		this.getManagedBean().setStatusDescList(resDTO.getStatusDescList());

		/*this.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
		for(CmItemRejectReason reason : this.getManagedBean().getApprovalItemList().get(index).getCmItemRejectReasons()){
			this.getManagedBean().getRejectReasonIds().add(reason.getPmStatus().getStatusId());
			this.getManagedBean().setRejectReason(reason.getRemark());
		}
		 */

		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getApprovalItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getApprovalItemList().get(index).getItemDesc());


		for(CmItemRejectReason remark: this.getManagedBean().getSelectedMaintainItem().getCmItemRejectReasons()){


			userReqDTO.setUserId(remark.getChangedBy());
			System.out.println("remark.getChangedBy()"+remark.getChangedBy());
			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
			System.out.println("userResDTO.getPmUser().getUserName():"+userResDTO.getPmUser().getUserName());
			remark.setCreatedUser(userResDTO.getPmUser().getUserName());
		}

		this.getManagedBean().setTabActiveIndex(1);

		return "addCatalogItem";

	}

	public String navigateToApprovedItemDet(int index){


		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getApprovedItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getApprovedItemList().get(index).getItemDesc());
		this.getManagedBean().setTabActiveIndex(2);

		return "addCatalogItem";

	}

	public String navigateToRejectedItemDet(int index){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);

		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setDocType("CMR");
		statusReqDTO.setModuleCode("CM");
		statusReqDTO.setLanguageCode(languageCode);

		StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);


		this.getManagedBean().setStatusDescList(resDTO.getStatusDescList());
		/*this.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
		for(CmItemRejectReason reason : this.getManagedBean().getRejectedItemList().get(index).getCmItemRejectReasons()){
			this.getManagedBean().getRejectReasonIds().add(reason.getPmStatus().getStatusId());
			this.getManagedBean().setRejectReason(reason.getRemark());
		}*/



		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getRejectedItemList().get(index));
		this.getManagedBean().getSelectedMaintainItem().setCompareDesc(this.getManagedBean().getRejectedItemList().get(index).getItemDesc());

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		for(CmItemRejectReason remark: this.getManagedBean().getSelectedMaintainItem().getCmItemRejectReasons()){


			userReqDTO.setUserId(remark.getChangedBy());
			System.out.println("remark.getChangedBy()"+remark.getChangedBy());
			UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
			System.out.println("userResDTO.getPmUser().getUserName():"+userResDTO.getPmUser().getUserName());
			remark.setCreatedUser(userResDTO.getPmUser().getUserName());
		}

		this.getManagedBean().setTabActiveIndex(3);






		return "addCatalogItem";

	}


	public String navigateToSupplierItemDet(VCmSupplierItem supplierItem){

		System.out.println("getDocId"+supplierItem.getDocId());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setDocId(supplierItem.getDocId());

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		System.out.println("*****************end********************");

		this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItemList().get(0));

		return "supplierItemDetView?faces-redirect=true";
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


		this.getManagedBean().getSelectedMaintainItem().getzStdPrices().add(price);

	}


	public void addNewZonalPromotionPricing(){



		CmZonalPromotionPricing price = new CmZonalPromotionPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().add(price);

	}
	/*
	public void addNewPromotionPricing(){



		CmPromotionPricing price = new CmPromotionPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings().add(price);

	}

	public void addNewStandardPricing(){



		CmStandardPricing price = new CmStandardPricing();


		this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings().add(price);

	}*/

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
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename="+"testing22.xlsx");
		response.flushBuffer();


		//Read Excel document first
		FileInputStream input_document = new FileInputStream(new File("C:\\excel\\Catalogue-offline-template.xlsx"));
		// convert it into a POI object
		XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document); 
		// Read excel sheet that needs to be updated
		XSSFSheet product = my_xlsx_workbook.getSheetAt(1);
		XSSFSheet pricing = my_xlsx_workbook.getSheetAt(2);
		XSSFSheet zonal = my_xlsx_workbook.getSheetAt(3);

		//sheet.protectSheet("welcome1"); 

		CellStyle catalogCellStyle = my_xlsx_workbook.createCellStyle();

		//add the 1st title row

		int no = 1;


		for(int i=0; i<this.getManagedBean().getSelectedMaintainItems().length; i++){
			List dataLs = new Vector();
			CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];

			dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscCode());
			dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscTitle());
			dataLs.add(catalog.getCmItem().getExtensionCode());
			dataLs.add(catalog.getCmItem().getItemName());
			dataLs.add(catalog.getCmItem().getCmType().getTypeName());
			dataLs.add(catalog.getCmItem().getCmBrand().getBrandName());
			dataLs.add(catalog.getCmItem().getCmMeasurement().getMeasurementName());
			dataLs.add(catalog.getCmItem().getCmColor().getColorName());
			dataLs.add(catalog.getItemDesc());
			dataLs.add(catalog.getFrequency());
			dataLs.add(catalog.getPmUom().getUomName());
			dataLs.add(catalog.getModelNumber());
			dataLs.add(catalog.getSkuNumber());
			dataLs.add(catalog.getManufacturer());
			dataLs.add(catalog.getMinOrder());
			dataLs.add(catalog.getMaxOrder());
			dataLs.add(catalog.getLeadTime());

			addCellToRow(product, no++, dataLs, catalogCellStyle);

		}


		int priceNo = 1;
		for(int i=0; i<this.getManagedBean().getSelectedMaintainItems().length; i++){

			CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];

			for(CmPricing price: catalog.getCmPricings()){
				List dataLs = new Vector();
				if((price.getPricingType().equalsIgnoreCase("01")||
						price.getPricingType().equalsIgnoreCase("02")||
						price.getPricingType().equalsIgnoreCase("03")||
						price.getPricingType().equalsIgnoreCase("04")) && price.getPrice()!=null){

					dataLs.add(price.getCmSupplierItem().getCmItem().getExtensionCode());

					if(price.getPricingType().equalsIgnoreCase("01") ){

						dataLs.add("Standard");
					}else if(price.getPricingType().equalsIgnoreCase("02")){

						dataLs.add("Promotion");
					}else if(price.getPricingType().equalsIgnoreCase("03")){

						dataLs.add("Tier");
					}else if(price.getPricingType().equalsIgnoreCase("04")){

						dataLs.add("Promotion Tier");
					}

					dataLs.add(price.getPrice());

					dataLs.add(formater.format(price.getStartDate()));
					dataLs.add(formater.format(price.getEndDate()));
					dataLs.add(price.getFromQty());
					dataLs.add(price.getToQty());


					addCellToRow(pricing, priceNo++, dataLs, catalogCellStyle);
				}
			}


		}


		/*		int zonalNo = 1;
		for(int i=0; i<this.getManagedBean().getSelectedMaintainItems().length; i++){

			CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];

			for(CmPricing price: catalog.getCmPricings()){
				List dataLs = new Vector();
				if(price.getPricingType().equalsIgnoreCase("05")||
						price.getPricingType().equalsIgnoreCase("06")||
						price.getPricingType().equalsIgnoreCase("07")||
						price.getPricingType().equalsIgnoreCase("08")){

					dataLs.add(price.getCmSupplierItem().getCmItem().getExtensionCode());

					if(price.getPricingType().equalsIgnoreCase("05")){
						if(price.getPmDistrict()!=null && price.getPmDistrict().getDistrictId()!=null){
							dataLs.add("Zonal District Standard");
						}else{
							dataLs.add("Zonal State Standard");
						}

					}else if(price.getPricingType().equalsIgnoreCase("06")){

						if(price.getPmDistrict()!=null && price.getPmDistrict().getDistrictId()!=null){
							dataLs.add("Zonal District Promotion");
						}else{
							dataLs.add("Zonal State Promotion");
						}


					}else if(price.getPricingType().equalsIgnoreCase("07")){

						if(price.getPmDistrict()!=null && price.getPmDistrict().getDistrictId()!=null){
							dataLs.add("Zonal Distict Tier");
						}else{
							dataLs.add("Zonal State Tier");
						}

					}else if(price.getPricingType().equalsIgnoreCase("08")){

						if(price.getPmDistrict()!=null && price.getPmDistrict().getDistrictId()!=null){
							dataLs.add("Zonal District Promotion Tier");
						}else{
							dataLs.add("Zonal State Promotion Tier");
						}
					}

					dataLs.add(price.getPmState().getStateName());
					dataLs.add(price.getPmDistrict().getDistrictName()!=null?price.getPmDistrict().getDistrictName():"");
					dataLs.add(price.getPrice());
					dataLs.add(formater.format(price.getStartDate()));
					dataLs.add(formater.format(price.getEndDate()));		
					dataLs.add(price.getFromQty());
					dataLs.add(price.getToQty());


					addCellToRow(zonal, zonalNo++, dataLs, catalogCellStyle);
				}
			}


		}
		 */




		/*List rowLs = new Vector();
		int no = 11;


		rowLs = new Vector();

		rowLs.add("testing");
		rowLs.add("");
		addCellToRow(sheet, no++, rowLs, catalogCellStyle);*/

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

		//String tempDir = System.getenv("windir");

		String tempDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");


		//		String tempDir = "C://image//";

		UploadedFile item = event.getFile();

		CmImage catImage = new CmImage();
		catImage.setImage(item.getContents());
		catImage.setImageName(item.getFileName());
		catImage.setImageDesc(item.getFileName());
		catImage.setImageType("jpg");
		catImage.setImagePath(item.getFileName());
		catImage.setTempPath(tempDir + "/" + item.getFileName());

		log.info("catImage.getImagePath()"+catImage.getImagePath());

		//render for display
		this.getManagedBean().getSelectedMaintainItem().getCmImages().add(catImage);// getCatalogImages().add(this.getManagedBean().getImageObj());



		genImage(catImage);



		log.info("size:"+this.getManagedBean().getSelectedMaintainItem().getCmImages().size());

	}


	public void genImage(CmImage imageObj){


		try {	




			BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(new FileOutputStream(imageObj.getTempPath()));

			bufferedOutputStream.write(imageObj.getImage());

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}



		}catch(Exception e) {
			log.error(e);
		}


	}


	/*	public void genMultipleImage(List<CmImage> imageList){
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
				//bufferedOutputStream.write(image.getImage());

				if (bufferedOutputStream != null) {
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}


			}

			//}
		}catch(Exception e) {
			log.error(e);
		}


	}*/


	public String saveCatalogItem() {
		log.info("In saveCatalogItem");
		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		System.out.println("******getTabActiveIndex:"+this.getManagedBean().getTabActiveIndex());

		ICatalogueSO catalogueSO = null;

		try {

			catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
			CatalogueReqDTO reqDTO  = new CatalogueReqDTO();




			Map<Date, List<CmPromotionTierPricing>> promotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings());

			if(!promotionTierMap.isEmpty() && promotionTierMap.size() > 0 ){ 


				List<Object> sortedZonalList = groupingStartDate(promotionTierMap);

				List <CmPromotionTierPricing> perviouslyList = new ArrayList<CmPromotionTierPricing>();
				for(Object sorted : sortedZonalList){
					List <CmPromotionTierPricing> sortedTier = (List<CmPromotionTierPricing>) sorted; 

					if(perviouslyList!=null && perviouslyList.size()>0){

						if(perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate())>0){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Promotion Tier Start date Overlapping", ""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(1);
							return "addCatalogView";
						}
					}
					for(int i=0; i<sortedTier.size(); i++){
						CmPromotionTierPricing tier = sortedTier.get(i);

						if(tier.getStartDate().compareTo(tier.getEndDate())>0){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Promotion Tier End date must be greater than Promotion Tier start date", ""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(1);
							return "addCatalogView";
						}
					}
					perviouslyList = sortedTier;
				}
			}



			Map<Date, List<CmZonalPromotionTierPricing>> zPromotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings());

			if(!zPromotionTierMap.isEmpty() && zPromotionTierMap.size() > 0 ){ 


				List<Object> sortedZonalList = groupingStartDate(zPromotionTierMap);

				List <CmZonalPromotionTierPricing> perviouslyList = new ArrayList<CmZonalPromotionTierPricing>();
				for(Object sorted : sortedZonalList){
					List <CmZonalPromotionTierPricing> sortedTier = (List<CmZonalPromotionTierPricing>) sorted; 

					if(perviouslyList!=null && perviouslyList.size()>0){

						if(perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate())>0){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Promotion Tier Start date Overlapping", ""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "addCatalogView";
						}
					}
					for(int i=0; i<sortedTier.size(); i++){
						CmZonalPromotionTierPricing tier = sortedTier.get(i);

						if(tier.getStartDate().compareTo(tier.getEndDate())>0){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Promotion Tier End date must be greater than Zonal Promotion Tier start date", ""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "addCatalogView";
						}
					}
					perviouslyList = sortedTier;
				}
			}




			List<CmZonalPromotionPricing> zPromotionPricing = sortByStartDate(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings());

			CmZonalPromotionPricing previousZPromotionObj = null;
			for(int i =0; i<zPromotionPricing.size(); i++){
				CmZonalPromotionPricing standard = zPromotionPricing.get(i);
				if(previousZPromotionObj!=null){
					if(previousZPromotionObj.getEndDate().compareTo(standard.getStartDate()) >0){
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Promotion Start date Overlapping", ""));	
						this.getManagedBean().setPricingTabIndex(2);
						this.getManagedBean().setZonalPricingIndex(1);
						return "addCatalogView";
					}
				}
				if(standard.getStartDate().compareTo(standard.getEndDate()) >0){
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Promotion End Date must be greater than Zonal Promotion Start Date", ""));
					this.getManagedBean().setPricingTabIndex(2);
					this.getManagedBean().setZonalPricingIndex(1);
					return "addCatalogView";
				}
				previousZPromotionObj = standard;
			}


			List<CmZonalStandardPricing> zStandardPricing = sortByStartDate(this.getManagedBean().getSelectedMaintainItem().getzStdPrices());

			CmZonalStandardPricing previousZStandardObj = null;
			for(int i =0; i<zStandardPricing.size(); i++){
				CmZonalStandardPricing standard = zStandardPricing.get(i);
				if(previousZStandardObj!=null){
					if(previousZStandardObj.getEndDate().compareTo(standard.getStartDate()) >0){
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Standard Start date Overlapping", ""));	
						this.getManagedBean().setPricingTabIndex(2);
						this.getManagedBean().setZonalPricingIndex(0);
						return "addCatalogView";
					}
				}
				if(standard.getStartDate().compareTo(standard.getEndDate()) >0){
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Standard End Date must be greater than Zonal Standard Start Date", ""));
					this.getManagedBean().setPricingTabIndex(2);
					this.getManagedBean().setZonalPricingIndex(0);
					return "addCatalogView";
				}
				previousZStandardObj = standard;
			}

			if(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate().compareTo(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate())>0){
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Standard End Date must be greater than Normal Standard Start Date", ""));

				this.getManagedBean().setPricingTabIndex(0);
				this.getManagedBean().setNormalPricingIndex(0);
				return "addCatalogView";
			}
			
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate().compareTo(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate())>0){
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Promotion End Date must be greater than Normal Promotion Start Date", ""));
				this.getManagedBean().setPricingTabIndex(0);
				this.getManagedBean().setNormalPricingIndex(1);
				return "addCatalogView";
			}
			


			/*List<CmStandardPricing> standardPricing = sortByStartDate(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings());

			CmStandardPricing previousStandardObj = null;
			for(int i =0; i<standardPricing.size(); i++){
				CmStandardPricing standard = standardPricing.get(i);
				if(previousStandardObj!=null){
					if(previousStandardObj.getEndDate().compareTo(standard.getStartDate()) >0){
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Standard Start date Overlapping", ""));		
						this.getManagedBean().setPricingTabIndex(0);
						this.getManagedBean().setNormalPricingIndex(0);
						return "addCatalogView";
					}
				}
				if(standard.getStartDate().compareTo(standard.getEndDate()) >0){
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Standard End Date must be greater than Normal Standard Start Date", ""));

					this.getManagedBean().setPricingTabIndex(0);
					this.getManagedBean().setNormalPricingIndex(0);
					return "addCatalogView";
				}
				previousStandardObj = standard;
			}*/



			/*List<CmPromotionPricing> promotionPricing = sortByStartDate(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings());

			CmPromotionPricing previousPromotionObj = null;
			for(int i =0; i<promotionPricing.size(); i++){
				CmPromotionPricing standard = promotionPricing.get(i);
				if(previousPromotionObj!=null){
					if(previousPromotionObj.getEndDate().compareTo(standard.getStartDate()) >0){
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Promotion Start date Overlapping", ""));	
						this.getManagedBean().setPricingTabIndex(0);
						this.getManagedBean().setNormalPricingIndex(1);
						return "addCatalogView";
					}
				}
				if(standard.getStartDate().compareTo(standard.getEndDate()) >0){
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Normal Promotion End Date must be greater than Normal Promotion Start Date", ""));
					this.getManagedBean().setPricingTabIndex(0);
					this.getManagedBean().setNormalPricingIndex(1);
					return "addCatalogView";
				}
				previousPromotionObj = standard;
			}*/




			Map<Date, List<CmZonalTierPricing>> zTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings());

			if(!zTierMap.isEmpty() && zTierMap.size() > 0 ){ 
				Iterator itMap = zTierMap.entrySet().iterator();
				while(itMap.hasNext()) {
					Map.Entry entry = (Map.Entry)itMap.next();		
					List <CmZonalTierPricing>dataList = (List)entry.getValue();
					CmZonalTierPricing previousObj = null;
					for(int i =0; i<dataList.size(); i++){
						CmZonalTierPricing tier = dataList.get(i);
						if(previousObj!=null){
							if(previousObj.getToQty() >= tier.getFromQty()){
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Standard Tier Overlapping", ""));	
								this.getManagedBean().setPricingTabIndex(3);
								this.getManagedBean().setZonalTierPricingIndex(0);
								return "addCatalogView";
							}
						}
						if(tier.getFromQty() >= tier.getToQty()){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Standard Tier To Qty must be greater than Zonal Standard Tier From Qty", ""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "addCatalogView";
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(zTierMap);

				List <CmZonalTierPricing> perviouslyList = new ArrayList<CmZonalTierPricing>();
				for(Object sorted : sortedZonalList){
					List <CmZonalTierPricing> sortedTier = (List<CmZonalTierPricing>) sorted; 

					if(perviouslyList!=null && perviouslyList.size()>0){

						if(perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate())>0){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Standard Tier Start date Overlapping", ""));
							this.getManagedBean().setPricingTabIndex(3);
							return "addCatalogView";
						}
					}
					for(int i=0; i<sortedTier.size(); i++){
						CmZonalTierPricing tier = sortedTier.get(i);

						if(tier.getStartDate().compareTo(tier.getEndDate())>0){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Zonal Standard Tier End date must be greater than Zonal Standard Tier start date", ""));
							this.getManagedBean().setPricingTabIndex(3);
							return "addCatalogView";
						}
					}
					perviouslyList = sortedTier;
				}
			}


			////////////////////////////////////////////////////////

			Map<Date, List<CmTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());

			if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 
				Iterator itMap = tierMap.entrySet().iterator();
				while(itMap.hasNext()) {
					Map.Entry entry = (Map.Entry)itMap.next();		
					List <CmTierPricing>dataList = (List)entry.getValue();
					CmTierPricing previousObj = null;
					for(int i =0; i<dataList.size(); i++){
						CmTierPricing tier = dataList.get(i);
						if(previousObj!=null){
							if(previousObj.getToQty() >= tier.getFromQty()){
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Standard Tier Overlapping", ""));	
								this.getManagedBean().setPricingTabIndex(1);
								this.getManagedBean().setTierPricingIndex(0);
								return "addCatalogView";
							}
						}
						if(tier.getFromQty() >= tier.getToQty()){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Standard Tier To Qty must be greater than Standard Tier From Qty", ""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "addCatalogView";
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(tierMap);

				List <CmTierPricing> perviouslyList = new ArrayList<CmTierPricing>();
				for(Object sorted : sortedZonalList){
					List <CmTierPricing> sortedTier = (List<CmTierPricing>) sorted; 

					if(perviouslyList!=null && perviouslyList.size()>0){

						if(perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate())>0){
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Standard Tier Start date Overlapping", ""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "addCatalogView";
						}
					}
					for(int i=0; i<sortedTier.size(); i++){
						CmTierPricing tier = sortedTier.get(i);

						if(tier.getStartDate().compareTo(tier.getEndDate())>0){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Standard Tier End date must be greater than Standard Tier start date", ""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "addCatalogView";
						}
					}
					perviouslyList = sortedTier;
				}



			}
			/////////////////////////////////////////////////////////////////////




			for(CmImage image: this.getManagedBean().getSelectedMaintainItem().getCmImages()){
				if(image.isBrandNew()){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					break;
				}
			}
			System.out.println("compareDesc:"+this.getManagedBean().getSelectedMaintainItem().getCompareDesc());
			System.out.println("getItemDesc:"+this.getManagedBean().getSelectedMaintainItem().getItemDesc());

			if(this.getManagedBean().getSelectedMaintainItem().getCompareDesc()!=null && this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length()>0){
				if(!(this.getManagedBean().getSelectedMaintainItem().getCompareDesc().equalsIgnoreCase(this.getManagedBean().getSelectedMaintainItem().getItemDesc()))){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			}else{
				if(this.getManagedBean().getSelectedMaintainItem().getItemDesc()!=null && this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length()>0){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			}
			System.out.println("isReviewRequired:"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());

			if(this.getManagedBean().getSelectedMaintainItem().isReviewRequired()){
				this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
			}else{
				this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");
			}

			this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");

			/*for(CmPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}
			for(CmStandardPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}*/

			this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			for(CmPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}
			System.out.println("this.getManagedBean().getSelectedMaintainItem().getCmTierPricings():"+this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().size());
			for(CmTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			System.out.println("getzStdPrices:"+this.getManagedBean().getSelectedMaintainItem().getzStdPrices().size());
			for(CmZonalStandardPricing promotion: this.getManagedBean().getSelectedMaintainItem().getzStdPrices()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			for(CmZonalPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			for(CmZonalTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			for(CmZonalPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}


			reqDTO.getSaveSupplierItems().add(this.getManagedBean().getSelectedMaintainItem());
			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Saved Successfully", ""));


			if(this.getManagedBean().getTabActiveIndex()==0){


				reqDTO.setItemStatus("D");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==4){

				reqDTO.setItemStatus("U");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==1){

				reqDTO.setItemStatus("P");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==2){

				reqDTO.setItemStatus("A");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==3){

				reqDTO.setItemStatus("R");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());

			}


		}catch(Exception ex){
			System.out.println("***********error********"+ex.getMessage());
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



	public String singleSubmitToReviewItems() {
		log.info("In saveCatalogItem");
		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		System.out.println("******getTabActiveIndex:"+this.getManagedBean().getTabActiveIndex());

		ICatalogueSO catalogueSO = null;

		try {

			catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
			CatalogueReqDTO reqDTO  = new CatalogueReqDTO();




			for(CmImage image: this.getManagedBean().getSelectedMaintainItem().getCmImages()){
				if(image.isBrandNew()){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
					break;
				}
			}
			System.out.println("compareDesc:"+this.getManagedBean().getSelectedMaintainItem().getCompareDesc());
			System.out.println("getItemDesc:"+this.getManagedBean().getSelectedMaintainItem().getItemDesc());



			if(this.getManagedBean().getSelectedMaintainItem().getCompareDesc()!=null && this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length()>0){
				if(!(this.getManagedBean().getSelectedMaintainItem().getCompareDesc().equalsIgnoreCase(this.getManagedBean().getSelectedMaintainItem().getItemDesc()))){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			}else{
				if(this.getManagedBean().getSelectedMaintainItem().getItemDesc()!=null && this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length()>0){
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			}
			System.out.println("isReviewRequired:"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());

			if(this.getManagedBean().getSelectedMaintainItem().isReviewRequired()){
				this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
			}else{
				this.getManagedBean().getSelectedMaintainItem().setItemStatus("U");
			}




			/*for(CmPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}

			for(CmStandardPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}*/

			this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			for(CmPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}
			for(CmTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()){
				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			}



			for(CmZonalStandardPricing promotion: this.getManagedBean().getSelectedMaintainItem().getzStdPrices()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			}

			for(CmZonalPromotionPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			}

			for(CmZonalTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			}

			for(CmZonalPromotionTierPricing promotion: this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){

				promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

			}





			reqDTO.getSaveSupplierItems().add(this.getManagedBean().getSelectedMaintainItem());
			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Saved Successfully", ""));


			if(this.getManagedBean().getTabActiveIndex()==0){


				reqDTO.setItemStatus("D");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==4){

				reqDTO.setItemStatus("U");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==1){

				reqDTO.setItemStatus("P");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==2){

				reqDTO.setItemStatus("A");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

			}else if(this.getManagedBean().getTabActiveIndex()==3){

				reqDTO.setItemStatus("R");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());

			}


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




	public String submitToReviewItems(){
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = new CatalogueResDTO();
		try {


			this.getManagedBean().setTabActiveIndex(0);

			System.out.println("this.getManagedBean().getSelectedMaintainItems().length:"+this.getManagedBean().getSelectedMaintainItems().length);
			for(int i=0; i<this.getManagedBean().getSelectedMaintainItems().length; i++){
				CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];
				System.out.println("catalog.getPmUom().getUomName():"+catalog.getPmUom().getUomName());
				if(catalog.getCmStandardPricing().getPrice()==null ||
						catalog.getCmStandardPricing().getStartDate()==null ||
						catalog.getCmStandardPricing().getEndDate() ==null ||
						catalog.getPmUom().getUomName().isEmpty()){

					System.out.println("********validation**********");
					FacesContext facesContext = FacesContext.getCurrentInstance();			
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Please complete all mandatory fields to proceed", ""));
					//facesContext.getExternalContext().getFlash().setKeepMessages(true);


					return "catalogMaintenanceFile";
				}

				if(catalog.isReviewRequired()){
					catalog.setItemStatus("P");
				}else{
					catalog.setItemStatus("U");
				}


				catalog.getCmStandardPricing().setCmSupplierItem(catalog);


				/*for(CmStandardPricing pricing: catalog.getCmStandardPricings()){
					pricing.setCmSupplierItem(catalog);
				}*/


				reqDTO.getSaveSupplierItems().add(catalog);


			}
			resDTO = catalogueSO.updateSupplierItem(reqDTO);

			System.out.println("resDTO.isSuccessful():"+resDTO.isSuccessful());
			if(resDTO.isSuccessful()){



				reqDTO.setItemStatus("D");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));



			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return "catalogMaintenanceFile";
	}


	/*public void searchCatalogue(){
		log.info("*******searchCatalogue************");  

		FacesScopes facesScopes = new FacesScopes();
		String unspscCode = facesScopes.getParameter("unspscCode");
		System.out.println("unspscCode: " + unspscCode + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("itemStatus");
		statusFilterBean.setSearchValue("D");
		searchBean.getDataFilterBeans().add(statusFilterBean);

		searchBean.setNavigatable(false);

		if(unspscCode != null && !unspscCode.equals("null") && unspscCode.length() > 0) {
			SearchFilterBean unspscFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			unspscFilterBean.setSearchField("cmItem.cmUnspsc.unspscCode");
			unspscFilterBean.setSearchValue(unspscCode);
			searchBean.getDataFilterBeans().add(unspscFilterBean);	
		}

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);
		log.info("catalogueSO:"+catalogueSO);

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setSupplierItemList(resDTO.getSupplierItemList());

	}*/




	public void compareIndxeAction(){

		log.debug("****index***"+this.getManagedBean().getCompareIndex());

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("name1");
		Integer index = Integer.parseInt(name1);

		CmSupplierItem catalog = this.getManagedBean().getSelectedSupplierItemList().get(index);

		//this.getManagedBean().setCatalogueObj(catalog);
		this.getManagedBean().setCompareIndex(index);


	} 

	public String backToPervious(){

		return "supplierItemView";
	}


	public String onTabChange1(String status){
		System.out.println("******status*******"+status);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		searchBean.setNavigatable(false);

		reqDTO.setSupplierId(new Long(1));

		resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);

		System.out.println("size:"+resDTO.getSupplierZoneLs().size());

		this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

		if(status.equalsIgnoreCase("Draft")){


			reqDTO.setItemStatus("D");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(0);

		}else if(status.equalsIgnoreCase("Published")){

			reqDTO.setItemStatus("U");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(4);

		}else if(status.equalsIgnoreCase("Approval")){

			reqDTO.setItemStatus("P");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(1);

		}else if(status.equalsIgnoreCase("Approved")){

			reqDTO.setItemStatus("A");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(2);

		}else if(status.equalsIgnoreCase("Rejected")){

			reqDTO.setItemStatus("R");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(3);

		}else if(status.equalsIgnoreCase("Expired")){

			reqDTO.setItemStatus("E");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setExpiredItemList(resDTO.getSupplierItemList());
			this.getManagedBean().setTabActiveIndex(5);

		}

		return "catalogMaintenanceFile";
	}


	public void onTabChange(TabChangeEvent event) {  
		System.out.println("title"+event.getTab().getTitle());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		searchBean.setNavigatable(false);

		reqDTO.setSupplierId(new Long(1));

		resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);

		System.out.println("size:"+resDTO.getSupplierZoneLs().size());

		this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

		if(event.getTab().getTitle().equalsIgnoreCase("Draft")){


			reqDTO.setItemStatus("D");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Published")){

			reqDTO.setItemStatus("U");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Pending Approval")){

			reqDTO.setItemStatus("P");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Approved")){

			reqDTO.setItemStatus("A");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Rejected")){

			reqDTO.setItemStatus("R");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());

		}else if(event.getTab().getTitle().equalsIgnoreCase("Expired")){

			reqDTO.setItemStatus("E");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setExpiredItemList(resDTO.getSupplierItemList());

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


			/*for(CmZonalStandardPricing promotion: catalog.getzStdPrices()){

				promotion.setCmSupplierItem(catalog);
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}

			for(CmZonalPromotionPricing promotion: catalog.getCmZonalPromotionPricings()){

				promotion.setCmSupplierItem(catalog);
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}


			for(CmZonalTierPricing promotion: catalog.getCmZonalTierPricings()){

				promotion.setCmSupplierItem(catalog);
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}

			for(CmZonalPromotionTierPricing promotion: catalog.getCmZonalPromotionTierPricings()){

				promotion.setCmSupplierItem(catalog);
				if(promotion.getPmDistrict().getDistrictId() == null) {
					promotion.setPmDistrict(null);
				}
			}*/





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

			reqDTO.setItemStatus("A");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Published Successfully", ""));


		}
	}




	public String compareSupplierItem(){

		log.info("==============compareSupplierItem========================");
		this.getManagedBean().setSelectedSupplierItemList(null);
		this.getManagedBean().setContractedExist(false);


		for(VCmSupplierItem view : this.getManagedBean().getSelectViewItems()){

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			System.out.println("view.getDocId()"+view.getDocId());
			reqDTO.setDocId(view.getDocId());

			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			System.out.println("*****************end********************");

			this.getManagedBean().getSelectedSupplierItemList().add(resDTO.getSupplierItemList().get(0));
		}


		for(VCmSupplierItem view :this.getManagedBean().getViewSupplierItemList()){

			if(view.getContractNo()!=null || view.getCircularNo()!=null){
				this.getManagedBean().setContractedExist(true);
			}

		}




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

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();


		reqDTO.setItemStatus("D");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);


		/*for(CmSupplierItem supplierItem: resDTO.getSupplierItemList()){

			if(supplierItem.getzStdPrices()==null || supplierItem.getzStdPrices().isEmpty()){
				CmZonalStandardPricing zone = new CmZonalStandardPricing();
				zone.setCmSupplierItem(supplierItem);

				supplierItem.getzStdPrices().add(zone);
			}
		}*/

		System.out.println("*****************end********************");
		this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());


		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "catalogMaintenanceFile");

	}


	public String backToPrev(){


		return "supplierItemView";
	}


	/*public void retrieveStandardDistrict(int index){



		System.out.println("**********retrieveDistrict******************");

		DistrictReqDTO reqDTO = new DistrictReqDTO();
		System.out.println("pricing.getPmState().getStateId()"+this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getPmState().getStateId());


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).setPmDistricts(resDTO.getDistrictList());

		if(resDTO.getDistrictList()!=null && resDTO.getDistrictList().size()>0){
			this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).setDistrictDisabled(false);
		}else{
			this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).setDistrictDisabled(true);
		}



	}*/

	/*public void disableState(){

		System.out.println("**********disableState******************");




		Long districtId = this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricing().getPmDistrict().getDistrictId();
		System.out.println("districtId:"+districtId);
		if(districtId == 0 ){
			this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricing().setStateDisabled(false);
		}else{
			this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricing().setStateDisabled(true);
		}
	}*/





	/*public void retrievePromotionDistrict(int index){

		System.out.println("**********retrievePromotionDistrict******************");

		DistrictReqDTO reqDTO = new DistrictReqDTO();
		System.out.println("pricing.getPmState().getStateId()"+this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).getPmState().getStateId());


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).setPmDistricts(resDTO.getDistrictList());

		if(resDTO.getDistrictList()!=null && resDTO.getDistrictList().size()>0){
			this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).setDistrictDisabled(false);
		}else{
			this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).setDistrictDisabled(true);
		}

	}
	 */

	/*public void retrieveTierDistrict(int index){

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
	 */

	/*public void retrievePromotionTierDistrict(int index){
		System.out.println("**********retrieveDistrict******************");

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();


		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(index).setPmDistricts(resDTO.getDistrictList());

	}*/


	public void addCatalogueRemarks(){

		CmItemRejectReason reason = new CmItemRejectReason();

		this.getManagedBean().getSelectedMaintainItem().getCmItemRejectReasons().add(reason);

	}

	public String navigateToSearchCode(){


		return "codificationView";
	}


	public String cancelToSupplierItemSearch(){


		return "supplierItemView";
	}

	public void enableRemark(){

		ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();



		bb.getManagedBean().setCatDetTabActiveIndex(2);


	}






	public void compareViewItem(){

		System.out.println("this.getManagedBean().getSelectViewItems().length():"+this.getManagedBean().getSelectViewItems());
	}


	public void popUpPricing(CmSupplierItem item){

		System.out.println("***************item*********************");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSupplierId(new Long(1));

		CatalogueResDTO resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);

		System.out.println("size:"+resDTO.getSupplierZoneLs().size());

		this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());


		this.getManagedBean().setSupplierItemObj(item);
	}


	public void findDistrict(){

		System.out.println("*****************findDistrict***************");
		System.out.println("Id:"+this.getManagedBean().getStateId());

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();



		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);


		System.out.println("*********************************");
		for(Long stateId : this.getManagedBean().getStateId()){
			System.out.println("stateId:"+stateId);
			reqDTO.setStateId(stateId);
			DistrictResDTO resDTO = districtSO.retriveDistrictListByStateID(reqDTO);

			for(PmDistrict district : resDTO.getDistrictList()){

				this.getManagedBean().getDistrictList().add(district);

			}

		}

		System.out.println("this.getManagedBean().getDistrictList().size():"+this.getManagedBean().getDistrictList().size());

	}

	public void refreshImage(){

		System.out.println("********refreshImage*********");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "addCatalogView.xhtml?faces-redirect=true");


	}


	public boolean isRemarkRendered() {

		for(int i = 0; i < this.getManagedBean().getRejectReasonIds().size(); i++) {
			String id = String.valueOf(this.getManagedBean().getRejectReasonIds().get(i));
			if(id.equals("10010")) {
				return true;
			}		
		}

		return false;
	}


	public void setRowIndex(){

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("index");
		int index = Integer.parseInt(name1);
		System.out.println("*************index***************"+index);


		this.getManagedBean().setTierRowIndex(index);

	}

	public void deleteTierPrice(){

		////////////incomplete///////////
		/*ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;

		reqDTO.setSupplierItemId(this.getManagedBean().getSelectedMaintainItem().getSupplierItemId());
		reqDTO.setStartDate1(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(this.getManagedBean().getTierRowIndex()).getStartDate());
		reqDTO.setEndDate1(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(this.getManagedBean().getTierRowIndex()).getEndDate());
		reqDTO.setPriceType("04");
		resDTO = catalogueSO.findPromotionDateByStandardDate(reqDTO);

		if(resDTO.getPriceLs()!=null && resDTO.getPriceLs().size()>0){

			for(CmPricing obj: resDTO.getPriceLs()){

			}


		}

		Map<Date, List<CmPromotionTierPricing>> promoTierMap = groupingDate(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings());*/
		///////////////////

		Map<Date, List<CmTierPricing>> tierMap = groupingDate(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());

		if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 

			List <CmTierPricing>dataList  = (List)tierMap.get(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(this.getManagedBean().getTierRowIndex()).getStartDate());	

			for(CmTierPricing price :dataList){

				this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().remove(price);
			}

		}



	}


	public void deletePromotionTierPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().remove(index);
	}
	public void deleteZonalStandardPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getzStdPrices().remove(index);
	}
	public void deleteZonalPromotionPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().remove(index);
	}
	public void deleteZonalTierPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().remove(index);
	}
	public void deleteZonalPromotionTierPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().remove(index);
	}

	/*public void deletePromotionPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings().remove(index);
	}

	public void deleteStandardPrice(int index){
		this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings().remove(index);
	}*/

	public void cloneTierPrice(int index){

		CmTierPricing tier = new CmTierPricing();
		tier.setStartDate(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStartDate());
		tier.setEndDate(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getEndDate());
		tier.setShowFlag(false);

		this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().add(index+1 , tier);
	}

	public void cloneZonalTierPrice(int index){

		CmZonalTierPricing tier = new CmZonalTierPricing();
		tier.setStartDate(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStartDate());
		tier.setEndDate(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getEndDate());
		tier.setCmSupplierZone(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getCmSupplierZone());
		tier.setShowFlag(false);

		this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().add(index+1 , tier);
	}

	public void addToPromotion(int index){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;

		reqDTO.setSupplierItemId(this.getManagedBean().getSelectedMaintainItem().getSupplierItemId());
		reqDTO.setStartDate1(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStartDate());
		reqDTO.setEndDate1(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getEndDate());
		reqDTO.setPriceType("04");
		resDTO = catalogueSO.findPromotionDateByStandardDate(reqDTO);

		if(resDTO.getPriceLs()==null || resDTO.getPriceLs().isEmpty()){


			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()!=null 
					&& this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().size()>0){
				boolean existFlag = false;
				for(CmPromotionTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()){

					if(promotion.getStartDate().equals(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStartDate())){	
						existFlag = true;			
					}
				}

				System.out.println("existFlag:"+existFlag);
				if(existFlag){
					System.out.println("*************exist******************");
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Promotion price exist", ""));

				}else{
					System.out.println("*************copy******************");
					reqDTO.setPriceType(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getPricingType());
					resDTO = catalogueSO.findPromotionDateByStandardDate(reqDTO);

					System.out.println("size:"+resDTO.getPriceLs().size());
					System.out.println("this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings():"+this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().size());
					for(CmPricing pricing: resDTO.getPriceLs()){
						CmPromotionTierPricing price = new CmPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setShowFlag(pricing.isShowFlag());

						this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().add(price);

					}
				}

			}else{
				System.out.println("*************1st copy******************");
				Map<Date, List<CmTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());

				if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 

					List <CmTierPricing>dataList  = (List)tierMap.get(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStartDate());	

					for(CmPricing pricing: dataList){
						CmPromotionTierPricing price = new CmPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setShowFlag(pricing.isShowFlag());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().add(price);
					}
				}
			}

		}else{
			System.out.println("*************exist******************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Promotion price exist", ""));

		}

	}


	public void addToZonalPromotion(int index){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;

		reqDTO.setSupplierItemId(this.getManagedBean().getSelectedMaintainItem().getSupplierItemId());
		reqDTO.setStartDate1(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStartDate());
		reqDTO.setEndDate1(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getEndDate());
		reqDTO.setPriceType("08");
		resDTO = catalogueSO.findPromotionDateByStandardDate(reqDTO);

		if(resDTO.getPriceLs()==null || resDTO.getPriceLs().isEmpty()){


			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()!=null 
					&& this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().size()>0){
				boolean existFlag = false;
				for(CmZonalPromotionTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){

					if(promotion.getStartDate().equals(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStartDate())){	
						existFlag = true;			
					}
				}

				System.out.println("existFlag:"+existFlag);
				if(existFlag){
					System.out.println("*************exist******************");
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Promotion price exist", ""));

				}else{
					System.out.println("*************copy******************");
					reqDTO.setPriceType(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getPricingType());
					resDTO = catalogueSO.findPromotionDateByStandardDate(reqDTO);

					System.out.println("size:"+resDTO.getPriceLs().size());
					for(CmPricing pricing: resDTO.getPriceLs()){
						CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setShowFlag(pricing.isShowFlag());

						this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().add(price);

					}
				}

			}else{


				Map<Date, List<CmZonalTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings());

				if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 

					List <CmZonalTierPricing>dataList  = (List)tierMap.get(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStartDate());	

					for(CmPricing pricing: dataList){
						CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setShowFlag(pricing.isShowFlag());
						this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().add(price);
					}
				}



			}

		}else{
			System.out.println("*************exist******************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Zonal Promotion price exist", ""));

		}

	}

	/*public void duplicateZonalStandardPrice(int index){



		CmZonalStandardPricing price = new CmZonalStandardPricing();

		price.getPmState().setStateId(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getPmState().getStateId());
		price.setPrice(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getPrice());
		price.setStartDate(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getStartDate());
		price.setEndDate(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getEndDate());
		price.setShowFlag(false);


		DistrictReqDTO reqDTO = new DistrictReqDTO();

		reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getPmState().getStateId());
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);

		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);
		System.out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
		price.setPmDistricts(resDTO.getDistrictList());


		this.getManagedBean().getSelectedMaintainItem().getzStdPrices().add(index+1 , price);

	}*/


	public String itemRejectReason(long id){


		for(PmStatusDesc desc :this.getManagedBean().getStatusDescList()){

			if(desc.getStatusDescId().equals(id)){
				return desc.getStatusName();
			}

		}


		return "";
	}



	public <T extends CmPricing> Map groupingTier(List <T> commonList){

		Map<Date, List<T>> tierMap = new HashMap<Date, List<T>>();
		List<T> tierList = new ArrayList<T>();
		for(T tier : commonList){
			if(tierMap.containsKey(tier.getStartDate())){
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			}else{

				tierList = new ArrayList<T>();
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			}
		}
		if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 
			Iterator itMap = tierMap.entrySet().iterator();


			while(itMap.hasNext()) {
				Map.Entry entry = (Map.Entry)itMap.next();		
				List <T>dataList = (List)entry.getValue();

				CmPricing [] obj = new CmPricing [dataList.size()];
				for(int i=0; i<dataList.size(); i++){
					int count =0;
					for(int j=0; j<dataList.size(); j++){

						if(dataList.get(i).getFromQty() > dataList.get(j).getFromQty()){
							count = count + 1;
						}
					}
					obj[count] =dataList.get(i);
				}

				List <T> sortedList = new ArrayList<T>();
				for(int i=0; i<obj.length; i++){
					sortedList.add((T) obj[i]);
				}

				for(T sorted : sortedList){
					System.out.println("sorted:"+sorted.getFromQty());
				}

				tierMap.put(dataList.get(0).getStartDate(), sortedList);
			}

		}	
		return tierMap;

	}



	public <T extends CmPricing> Map groupingDate(List <T> commonList){

		Map<Date, List<T>> tierMap = new HashMap<Date, List<T>>();
		List<T> tierList = new ArrayList<T>();
		for(T tier : commonList){
			if(tierMap.containsKey(tier.getStartDate())){
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			}else{

				tierList = new ArrayList<T>();
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			}
		}

		return tierMap;

	}


	public <T extends CmPricing> List groupingStartDate(Map<Date, List<T>> tierMap){

		SimpleDateFormat formot = new SimpleDateFormat("YYYYMMdd");
		List <Object> sortedList = new ArrayList<Object>();
		if(!tierMap.isEmpty() && tierMap.size() > 0 ){ 
			Set keyset = tierMap.keySet();

			Object[] keysetarray = keyset.toArray();
			Object [] obj = new Object [keysetarray.length];
			System.out.println("keysetarray.length:"+keysetarray.length);
			for(int i=0; i<keysetarray.length; i++){
				System.out.println("******key:"+formot.format(keysetarray[i]));
				int count = 0;
				for(int j=0; j<keysetarray.length; j++){
					System.out.println("**************in****************");
					if(formot.format(keysetarray[i]).compareToIgnoreCase(formot.format(keysetarray[j]))>0){
						count = count + 1;
					}

				}
				System.out.println("count:"+count);
				System.out.println("i:"+i);
				List <T>dataList  = (List)tierMap.get(keysetarray[i]);	
				obj[count] = dataList;

			}

			for(int i=0; i<obj.length; i++){
				sortedList.add(obj[i]);
			}

		}
		return sortedList;
	}






	public <T extends CmPricing> List sortByStartDate(List <T> commonList){

		SimpleDateFormat formot = new SimpleDateFormat("YYYYMMdd");

		CmPricing [] obj = new CmPricing [commonList.size()];
		for(int i=0; i<commonList.size(); i++){
			int count =0;
			for(int j=0; j<commonList.size(); j++){

				if(formot.format(commonList.get(i).getStartDate()).compareToIgnoreCase(formot.format(commonList.get(j).getStartDate()))>0){
					count = count + 1;
				}
			}
			obj[count] =commonList.get(i);
		}

		List <T> sortedList = new ArrayList<T>();
		for(int i=0; i<obj.length; i++){
			sortedList.add((T) obj[i]);
		}

		for(T sorted : sortedList){
			System.out.println("sorted:"+sorted.getStartDate());
		}


		return sortedList;

	}


	public String addToContractRequest(VCmSupplierItem supplierItem){

		System.out.println("************getContractNo***************"+supplierItem.getContractNo());

		this.getManagedBean().setTempSupplierList(null);
		this.getManagedBean().getTempSupplierList().add(supplierItem);


		boolean flag = false;

		if(this.getManagedBean().getCtRequestList()!=null && this.getManagedBean().getCtRequestList().size()>0){
			for(VCmSupplierItem item: this.getManagedBean().getCtRequestList()){
				if(!item.getContractNo().equalsIgnoreCase(supplierItem.getContractNo())){
					System.out.println("********************different Contact No.");

					flag = true;
				}
			}
		}

		if(flag){
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Different Contact No.", ""));

			return "supplierItemView";
		}else{
			System.out.println("size:"+this.getManagedBean().getTempSupplierList().size());

			for(VCmSupplierItem temp:this.getManagedBean().getTempSupplierList()){
				System.out.println("temp.getDocId()"+temp.getDocId());
				System.out.println("keySet:"+this.getManagedBean().getTempMap().keySet());
				if(!this.getManagedBean().getTempMap().containsKey(temp.getDocId())){
					this.getManagedBean().getCtRequestList().add(temp);
					this.getManagedBean().getTempMap().put(temp.getDocId(), temp.getDocId());
				}
			}
		}



		return "supplierItemView";



	}











}

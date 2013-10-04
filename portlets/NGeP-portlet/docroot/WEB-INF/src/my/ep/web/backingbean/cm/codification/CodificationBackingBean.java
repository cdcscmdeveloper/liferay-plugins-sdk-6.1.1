package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.dto.cm.codification.CodificationReqDTO;
import my.ep.biz.dto.cm.codification.CodificationResDTO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.cm.codification.CodificationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name="codificationBackingBean")
@RequestScoped
public class CodificationBackingBean extends BaseBackingBean<CodificationManagedBean>{


	
	public static final String NAV_TO_CODIFICATION_VIEW = "codificationView";
	public static final String NAV_TO_EXTENSION_SEARCH = "extensionSearch";
	public static final String NAV_TO_UNSPSC_REQUEST_FORM = "unspscRequestForm";
	public static final String NAV_TO_EXTENSION_REQUEST_FORM="extensionCodeRequestForm";
	public static final String NAV_TO_UNSPSC_SINGLE_UPLOAD="unspscSingleUpload";
	public static final String NAV_TO_EXTENSION_CODE_REVIEW="extensionCodeReview";
	public static final String NAV_TO_UNSPSC_BULK_UPLOAD="unspscBulkUpload";
	public static final String NAV_TO_EDIT_EXTENSION="editExtension";
	public static final String NAV_TO_DELETE_EXTENSION="deleteExtension";
	public static final String NAV_TO_CONTRACTED_ITEM="contractedItem";


	private static Logger log = Logger.getLogger(CodificationBackingBean.class);


	private SearchFilterBean codeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean levelFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	//private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private SearchFilterBean colorFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	//private IPagingDataModel<CmItem> searchItemDataModel = new SearchItemPagingDataModel();


	public CodificationBackingBean(){

		

	}



	public void itemSearch(){
		
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		ISearchBean<CmItem> searchBean = new SearchBean<CmItem>();
		colorFilterBean.setSearchField("cmColor.colorCode");
		searchBean.getDataFilterBeans().add(colorFilterBean);
		
		typeFilterBean.setSearchField("cmType.typeCode");
		searchBean.getDataFilterBeans().add(typeFilterBean);
		
		brandFilterBean.setSearchField("cmBrand.brandCode");
		searchBean.getDataFilterBeans().add(brandFilterBean);
		
		measurementFilterBean.setSearchField("cmMeasurement.measurementCode");
		searchBean.getDataFilterBeans().add(measurementFilterBean);
		
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemSearchBean(searchBean);

		CodificationResDTO resDTO = codificationSO.searchItem(reqDTO);
		this.getManagedBean().setItemList(resDTO.getItemList());
	}

	/*private class SearchItemPagingDataModel extends PagingDataModel<CmItem> {

		private static final long serialVersionUID = 1L;

		public SearchItemPagingDataModel() {
			super("itemTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmItem> pageChangeListener(
				ISearchBean<CmItem> searchBean) {


//			if(StringUtils.isBlank((String)colorFilterBean.getSearchValue()) 
//					&& StringUtils.isBlank((String)typeFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String)brandFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String)measurementFilterBean.getSearchValue())) {
//				return new SearchResultBean();
//			}

			ICodificationSO unspscSO = null;
			try {				
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ICodificationSO.class);



				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setItemSearchBean(searchBean);

				CodificationResDTO resDTO = unspscSO.searchItem(reqDTO);

				return resDTO.getItemResultBean();

			} finally {
				unspscSO = null;
			}
		}

	}*/

	/*public IPagingDataModel<CmItem> getSearchItemDataModel() {
		return searchItemDataModel;
	}

	public void setSearchItemDataModel(IPagingDataModel<CmItem> searchItemDataModel) {
		this.searchItemDataModel = searchItemDataModel;
	}*/

/*	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			if(StringUtils.isBlank((String)codeFilterBean.getSearchValue()) 
					&& StringUtils.isBlank((String)titleFilterBean.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {				
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ICodificationSO.class);

				if (StringUtils.isNotBlank((String)codeFilterBean.getSearchValue())) {
					codeFilterBean.setSearchField("unspscCode");
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String)titleFilterBean.getSearchValue())) {
					titleFilterBean.setSearchField("unspscTitle");
					searchBean.getDataFilterBeans().add(titleFilterBean);
				}


				levelFilterBean.setSearchField("unspscLevel");
				levelFilterBean.setSearchValue(3);
				searchBean.getDataFilterBeans().add(levelFilterBean);


				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);

				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				return resDTO.getResultBean();

			} finally {
				unspscSO = null;
			}
		}

	}*/

	public SearchFilterBean getCodeFilterBean() {
		return codeFilterBean;
	}

	public void setCodeFilterBean(SearchFilterBean codeFilterBean) {
		this.codeFilterBean = codeFilterBean;
	}

	public SearchFilterBean getTitleFilterBean() {
		return titleFilterBean;
	}

	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
		this.titleFilterBean = titleFilterBean;
	}

	public SearchFilterBean getLevelFilterBean() {
		return levelFilterBean;
	}

	public void setLevelFilterBean(SearchFilterBean levelFilterBean) {
		this.levelFilterBean = levelFilterBean;
	}
	/*
	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public void unspscSearch(){
		searchDataModel.reloadData();
	}*/

	
	public void unspscSearch2() {
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>unspscSearch");
		
		ICodificationSO codificationSO = null;
		try {				
			codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
			
			ISearchBean<CmUnspsc> searchBean = new SearchBean<CmUnspsc>();
			codeFilterBean.setSearchField("unspscCode");
			searchBean.getDataFilterBeans().add(codeFilterBean);
			titleFilterBean.setSearchField("unspscTitle");
			searchBean.getDataFilterBeans().add(titleFilterBean);
			levelFilterBean.setSearchField("unspscLevel");
			levelFilterBean.setSearchValue(3);
			searchBean.getDataFilterBeans().add(levelFilterBean);			
			
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setSearchBean(searchBean);
			
			CodificationResDTO resDTO = codificationSO.searchUNSPSC(reqDTO);
			this.getManagedBean().setUnspscList(resDTO.getUnspscList());
		} finally {
			codificationSO = null;
		}
	}

	public String codificationSearch(){

		return NAV_TO_CODIFICATION_VIEW;
	}


	public String goToExtensionSearch(){

		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO =  (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		CodificationResDTO resDTO = unspscSO.searchColor(reqDTO);

		log.info("*****************************"+this.getManagedBean().getSelectedUNSPSCObj().getUnspscCode());

		for(CmColor color:resDTO.getColorList()){
			log.info("color:"+color);
			log.info("color:"+color.getColorId());
			this.getManagedBean().getColorCriteria().add(new SelectItem(color.getColorCode(), color.getColorName()));
		}

		for(CmBrand brand:this.getManagedBean().getSelectedUNSPSCObj().getCmBrand()){
			this.getManagedBean().getBrandCriteria().add(new SelectItem(brand.getBrandCode(), brand.getBrandName()));
		}

		for(CmType type:this.getManagedBean().getSelectedUNSPSCObj().getCmType()){
			this.getManagedBean().getTypeCriteria().add(new SelectItem(type.getTypeCode(), type.getTypeName()));
		}

		for(CmMeasurement measurement:this.getManagedBean().getSelectedUNSPSCObj().getCmMeasurement()){
			this.getManagedBean().getMeasurementCriteria().add(new SelectItem(measurement.getMeasurementCode(), measurement.getMeasurementName()));
		}





		return NAV_TO_EXTENSION_SEARCH+"?faces-redirect=true";
	}

	public String requestUNSPSCcode(){

		return NAV_TO_UNSPSC_REQUEST_FORM;
	}

	public String singleUpload(){

		return NAV_TO_UNSPSC_SINGLE_UPLOAD;
	}

	public String requestExtensioncode(){

		return NAV_TO_EXTENSION_REQUEST_FORM;

	}

	public String extensionReview(){

		return NAV_TO_EXTENSION_CODE_REVIEW;
	}

	public String bulkUpload(){

		return NAV_TO_UNSPSC_BULK_UPLOAD+"?faces-redirect=true";

	}

	public String editExtension(){

		return NAV_TO_EDIT_EXTENSION;
	}

	public String deleteExtension(){
		return NAV_TO_DELETE_EXTENSION;
	}

	public String AssignContractedItem(){
		return NAV_TO_CONTRACTED_ITEM;
	}


	public void uploadUNSPSC(FileUploadEvent event) throws Exception{

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		Long loginUserId = new Long(1);

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream)item.getInputstream();	
		HSSFWorkbook workbook = new HSSFWorkbook(file);	
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		HSSFSheet segment = workbook.getSheetAt(2);


		log.debug("*******segment***********");

		long key = 0;
		long code =0;
		String title =null;
		String desc = null;



		int lastRowNum = segment.getLastRowNum()+1;
		for(int i = 1; i < lastRowNum; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = segment.getRow(i);

			if(row.getCell(0)!=null){
				key = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(key);

			}

			if(row.getCell(1)!=null){
				code = (long) row.getCell(1).getNumericCellValue();

				unspsc.setUnspscCode(String.valueOf(code));
			}

			if(row.getCell(2)!=null){
				title =  row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(title);

			}

			if(row.getCell(3)!=null){
				desc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(desc);

			}
			reqDTO.setUserID(loginUserId);
			unspsc.setUnspscLevel(0);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		}

		CodificationResDTO resDTO = unspscSO.saveUnspsc(reqDTO);
		log.debug("*******end segment***********");

		///////////////////////////////////////////////////////////////////////

		HSSFSheet family = workbook.getSheetAt(3);

		long familyKey = 0;
		long familyCode =0;
		String familyTitle =null;
		String familyDesc = null;


		int lastRowNumFamily = family.getLastRowNum()+1;
		for(int i = 1; i < lastRowNumFamily; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = family.getRow(i);

			if(row.getCell(0)!=null){
				familyKey = (long) row.getCell(0).getNumericCellValue();

				unspsc.setUnspscKey(familyKey);
			}

			if(row.getCell(1)!=null){
				familyCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(familyCode);

				unspsc.setUnspscCode(String.valueOf(familyCode));
				unspsc.setUnspscParentCode(new Long(parentCode));

			}

			if(row.getCell(2)!=null){
				familyTitle =  row.getCell(2).getStringCellValue();

				unspsc.setUnspscTitle(familyTitle);
			}

			if(row.getCell(3)!=null){
				familyDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(familyDesc);
			}

			reqDTO.setUserID(loginUserId);
			unspsc.setUnspscLevel(1);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());


		}
		resDTO = unspscSO.saveFamilyList(reqDTO);

		///////////////////////////////////////////////////////////////////////

		HSSFSheet classes = workbook.getSheetAt(4);

		long classKey = 0;
		long classCode =0;
		String classTitle =null;
		String classDesc = null;

		int lastRowNumClass = classes.getLastRowNum()+1;
		for(int i = 1; i < lastRowNumClass; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = classes.getRow(i);

			if(row.getCell(0)!=null){
				classKey = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(classKey);

			}

			if(row.getCell(1)!=null){

				classCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(classCode);

				unspsc.setUnspscCode(String.valueOf(classCode));
				unspsc.setUnspscParentCode(new Long(parentCode));
			}

			if(row.getCell(2)!=null){
				classTitle =  row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(classTitle);
			}

			if(row.getCell(3)!=null){
				classDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(classDesc);
			}

			reqDTO.setUserID(loginUserId);
			unspsc.setUnspscLevel(2);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getClassList().add(reqDTO.getCmUnspsc());

		}


		resDTO = unspscSO.saveClassList(reqDTO);

		HSSFSheet commodity = workbook.getSheetAt(5);

		long commodityKey = 0;
		long commodityCode =0;
		String commodityTitle =null;
		String commodityDesc = null;

		int lastRowNumCommodity = commodity.getLastRowNum()+1;


		for(int i = 1; i < lastRowNumCommodity; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = commodity.getRow(i);

			if(row.getCell(6)!=null && row.getCell(6).toString().trim().length()>0){

				commodityKey = (long) row.getCell(6).getNumericCellValue();

				unspsc.setUnspscKey(commodityKey);

			}

			if(row.getCell(7)!=null && row.getCell(7).toString().trim().length()>0){

				commodityCode = (long) row.getCell(7).getNumericCellValue();

				String parentCode = getParentCode(commodityCode);

				unspsc.setUnspscCode(String.valueOf(commodityCode));
				unspsc.setUnspscParentCode(new Long(parentCode));
			}

			if(row.getCell(8)!=null && row.getCell(8).toString().trim().length()>0){
				commodityTitle =  row.getCell(8).getStringCellValue();

				unspsc.setUnspscTitle(commodityTitle);

			}

			if(row.getCell(9)!=null && row.getCell(9).toString().trim().length()>0){
				commodityDesc = row.getCell(9).getStringCellValue();

				unspsc.setUnspscDescription(commodityDesc);

			}
			reqDTO.setUserID(loginUserId);
			unspsc.setUnspscLevel(3);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());


		}


		resDTO = unspscSO.saveCommodityList(reqDTO);



	}


	public String getParentCode(long code){

		String parent = null;

		String check68 = (String)String.valueOf(code).substring(6, 8);
		String check46 = (String)String.valueOf(code).substring(4, 6);
		String check24 = (String)String.valueOf(code).substring(2, 4);

		if(!check68.equalsIgnoreCase("00")){
			log.debug("commodity");
			parent = (String)String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:"+parent);
		}else{
			if(!check46.equalsIgnoreCase("00")){
				log.debug("class");
				parent = (String)String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:"+parent);
			}else{
				if(!check24.equalsIgnoreCase("00")){
					log.debug("family");
					parent = (String)String.valueOf(code).substring(0, 2) + "000000";
					log.debug("parent:"+parent);
				}else{
					log.debug("segment");
				}

			}

		}

		return parent;
	}

	public Long checkUnspscLevel(long code){

		long level = 0;

		String check68 = (String)String.valueOf(code).substring(6, 8);
		String check46 = (String)String.valueOf(code).substring(4, 6);
		String check24 = (String)String.valueOf(code).substring(2, 4);

		if(!check68.equalsIgnoreCase("00")){
			log.debug("commodity");
			level = 3;

		}else{
			if(!check46.equalsIgnoreCase("00")){
				log.debug("class");
				level = 2;
			}else{
				if(!check24.equalsIgnoreCase("00")){
					log.debug("family");
					level = 1;
				}else{
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}







	public SearchFilterBean getColorFilterBean() {
		return colorFilterBean;
	}



	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
		this.colorFilterBean = colorFilterBean;
	}



	public SearchFilterBean getTypeFilterBean() {
		return typeFilterBean;
	}



	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
		this.typeFilterBean = typeFilterBean;
	}



	public SearchFilterBean getBrandFilterBean() {
		return brandFilterBean;
	}



	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
		this.brandFilterBean = brandFilterBean;
	}



	public SearchFilterBean getMeasurementFilterBean() {
		return measurementFilterBean;
	}



	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
		this.measurementFilterBean = measurementFilterBean;
	}






}

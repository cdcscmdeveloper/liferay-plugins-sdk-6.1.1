package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
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
		this.getManagedBean().setContractedItem(null);
		CmUnspsc unspsc = new CmUnspsc();
		unspsc.setUnspscTitle("item");
		this.getManagedBean().getContractedItem().add(unspsc);
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
		log.info("size:"+resDTO.getItemList().size());
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
			for(CmUnspsc unspsc :resDTO.getUnspscList()){
				System.out.println("unspsc:"+unspsc.getUnspscCode());
				//for(CmBrand brand:unspsc.getCmBrand()){
				//System.out.println("brand:"+brand.getBrandCode());
				//}
			}

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
			//log.info("color:"+color);
			//log.info("color:"+color.getColorId());
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

	public String editExtension(int index){

		System.out.println("*********"+this.getManagedBean().getItemList().get(index).getItemName());

		this.getManagedBean().setEditItem(this.getManagedBean().getItemList().get(index));

		return NAV_TO_EDIT_EXTENSION;
	}

	public String deleteExtension(int index){
		System.out.println("*********"+this.getManagedBean().getItemList().get(index).getItemName());

		this.getManagedBean().setDeleteItem(this.getManagedBean().getItemList().get(index));

		return NAV_TO_DELETE_EXTENSION;
	}

	public String AssignContractedItem(){
		return NAV_TO_CONTRACTED_ITEM;
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

	public void assignProductCode(){

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		CodificationResDTO resDTO = codificationSO.searchSegment(reqDTO);

		log.info("resDTO.getSegmentList():"+resDTO.getSegmentList().size());
		for(CmUnspsc segment:resDTO.getSegmentList()){

			this.getManagedBean().getSegmentCriteria().add(new SelectItem(segment.getUnspscCode(), segment.getUnspscTitle()));
		}





	}


	public void filterFamily(){
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		log.info("this.getManagedBean().getSegmentCode()"+this.getManagedBean().getSegmentCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getSegmentCode());

		CodificationResDTO resDTO = codificationSO.searchFamily(reqDTO);

		for(CmUnspsc family:resDTO.getFamilyList()){

			this.getManagedBean().getFamilyCriteria().add(new SelectItem(family.getUnspscCode(), family.getUnspscTitle()));
		}

	}

	public void filterClass(){
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		log.info("this.getManagedBean().getFamilyCode()"+this.getManagedBean().getFamilyCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getFamilyCode());

		CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

		for(CmUnspsc classes :resDTO.getClassList()){

			this.getManagedBean().getClassCriteria().add(new SelectItem(classes.getUnspscCode(), classes.getUnspscTitle()));
		}

	}


	public void filterCommodity(){
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		log.info("this.getManagedBean().getClassCode()"+this.getManagedBean().getClassCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getClassCode());

		CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

		for(CmUnspsc commodity :resDTO.getCommodityList()){

			this.getManagedBean().getCommodityCriteria().add(new SelectItem(commodity.getUnspscCode(), commodity.getUnspscTitle()));
		}	


	}

	public void filterExtType(){
		log.info("this.getManagedBean().getCommodityCode()"+this.getManagedBean().getCommodityCode());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(this.getManagedBean().getCommodityCode());

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

		for(CmBrand brand:resDTO.getCommodityObj().getCmBrand()){
			log.info("code:"+brand.getBrandCode());
			this.getManagedBean().getBrandCriteria().add(new SelectItem(brand.getBrandCode(), brand.getBrandName()));
		}

		for(CmType type:resDTO.getCommodityObj().getCmType()){
			this.getManagedBean().getTypeCriteria().add(new SelectItem(type.getTypeCode(), type.getTypeName()));
		}

		for(CmMeasurement measurement:resDTO.getCommodityObj().getCmMeasurement()){
			this.getManagedBean().getMeasurementCriteria().add(new SelectItem(measurement.getMeasurementCode(), measurement.getMeasurementName()));
		}

		resDTO = codificationSO.searchColor(reqDTO);
		for(CmColor color:resDTO.getColorList()){
			this.getManagedBean().getColorCriteria().add(new SelectItem(color.getColorCode(), color.getColorName()));
		}


	}


	public void insertContractedItemCode(){

	}


	public String createExtCode(){

		return "extensionCodeSingleCreation";
	}


	public String backToPrevious(){



		return "extensionSearch";
	}

	public String saveEditedCode(){

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;



		ISearchBean<CmBrand> searchBeanBrand = new SearchBean<CmBrand>();
		brandFilterBean.setSearchField("brandCode");
		brandFilterBean.setSearchValue(this.getManagedBean().getEditItem().getCmBrand().getBrandCode());
		searchBeanBrand.getDataFilterBeans().add(brandFilterBean);

		reqDTO.setBrandSearchBean(searchBeanBrand);

		resDTO = codificationSO.searchBrandObj(reqDTO);

		if(!this.getManagedBean().getEditBrandName().isEmpty()){
			resDTO.getBrandList().get(0).setBrandName(this.getManagedBean().getEditBrandName());
		}
		
		reqDTO.setCmBrand(resDTO.getBrandList().get(0));
		//////////////////////////////

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorCode");
		colorFilterBean.setSearchValue(this.getManagedBean().getEditItem().getCmColor().getColorCode());
		searchBeanColor.getDataFilterBeans().add(colorFilterBean);

		reqDTO.setColorSearchBean(searchBeanColor);
		
		resDTO = codificationSO.searchColorObj(reqDTO);
		
		if(!this.getManagedBean().getEditColorName().isEmpty()){
			resDTO.getColorList().get(0).setColorName(this.getManagedBean().getEditColorName());
		}
		
		reqDTO.setCmColor(resDTO.getColorList().get(0));
		
		////////////////////

		ISearchBean<CmType> searchBeanType = new SearchBean<CmType>();
		typeFilterBean.setSearchField("typeCode");
		typeFilterBean.setSearchValue(this.getManagedBean().getEditItem().getCmType().getTypeCode());
		searchBeanType.getDataFilterBeans().add(typeFilterBean);
		
		reqDTO.setTypeSearchBean(searchBeanType);
		
		resDTO = codificationSO.searchTypeObj(reqDTO);
		if(!this.getManagedBean().getEditTypeName().isEmpty()){
			resDTO.getTypeList().get(0).setTypeName(this.getManagedBean().getEditTypeName());
		}
		
		reqDTO.setCmType(resDTO.getTypeList().get(0));
		
		///////////////////////////

		ISearchBean<CmMeasurement> searchBeanMeasurement = new SearchBean<CmMeasurement>();
		measurementFilterBean.setSearchField("measurementCode");
		measurementFilterBean.setSearchValue(this.getManagedBean().getEditItem().getCmMeasurement().getMeasurementCode());
		searchBeanMeasurement.getDataFilterBeans().add(measurementFilterBean);

		reqDTO.setMeasurementSearchBean(searchBeanMeasurement);

		resDTO = codificationSO.searchMeasurementObj(reqDTO);

		if(!this.getManagedBean().getEditMeasurementName().isEmpty()){
			resDTO.getMeasurementList().get(0).setMeasurementName(this.getManagedBean().getEditMeasurementName());
		}


		reqDTO.setCmMeasurement(resDTO.getMeasurementList().get(0));
		////////////////////////////////////////


		resDTO = codificationSO.editExtensionCode(reqDTO);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		/*Flash flash = facesContext.getExternalContext().getFlash();
		flash.setKeepMessages(true);
		flash.setRedirect(true);*/
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		return "extensionSearch";

	}









}

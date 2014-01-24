package my.ep.web.backingbean.cm.codification;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.cm.codification.CodificationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.FamiliesVO;
import my.ep.web.vo.NameNode;
import my.ep.web.vo.SegmentVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name="codificationBackingBean")
@ViewScoped
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
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private SearchFilterBean colorFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmItem> searchItemDataModel = new SearchItemPagingDataModel();


	private TreeNode rootNodes;
	private TreeNode selectedNode;


	public CodificationBackingBean(){
		this.getManagedBean().setContractedItem(null);
		CmUnspsc unspsc = new CmUnspsc();
		unspsc.setUnspscTitle("item");
		this.getManagedBean().getContractedItem().add(unspsc);

		/*	ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		CodificationResDTO resDTO = codificationSO.searchSegment(reqDTO);

		if(resDTO.isSuccessful()){

			rootNodes = new DefaultTreeNode("rootNodes", null); 

			for(CmUnspsc segment : resDTO.getSegmentList()){
				SegmentVO segmentObj = new SegmentVO();
				segmentObj.setName(segment.getUnspscTitle());
				segmentObj.setParentCode(segment.getUnspscCode());
				segmentObj.setUnspscLevel(0);

				TreeNode segmentNode = new DefaultTreeNode("segment", segmentObj, rootNodes);

				//reqDTO.setParentCode(segment.getUnspscCode());
				//resDTO = codificationSO.searchFamily(reqDTO);

				for(CmUnspsc catFamily : segment.getCmUnspscs()){

					FamiliesVO familiesVO = new FamiliesVO();
					familiesVO.setName(catFamily.getUnspscTitle());
					familiesVO.setParentCode(catFamily.getUnspscCode());
					familiesVO.setUnspscLevel(1);
					TreeNode families = new DefaultTreeNode("families",familiesVO, segmentNode);
				}


			}


		}*/



	}



	/*	public void itemSearch(){

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

		statusFilterBean.setSearchField("recordStatus");
		statusFilterBean.setSearchValue(true);
		searchBean.getDataFilterBeans().add(statusFilterBean);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemSearchBean(searchBean);

		CodificationResDTO resDTO = codificationSO.searchItem(reqDTO);
		log.info("size:"+resDTO.getItemList().size());
		this.getManagedBean().setItemList(resDTO.getItemList());
	}*/

	private class SearchItemPagingDataModel extends PagingDataModel<CmItem> {

		private static final long serialVersionUID = 1L;

		public SearchItemPagingDataModel() {
			super("itemTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmItem> pageChangeListener(
				ISearchBean<CmItem> searchBean) {


			if(StringUtils.isBlank((String)colorFilterBean.getSearchValue()) 
					&& StringUtils.isBlank((String)typeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String)brandFilterBean.getSearchValue())
					&& StringUtils.isBlank((String)measurementFilterBean.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {				
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ICodificationSO.class);



				if (StringUtils.isNotBlank((String)colorFilterBean.getSearchValue())) {
					colorFilterBean.setSearchField("cmColor.colorCode");
					searchBean.getDataFilterBeans().add(colorFilterBean);
				}

				if (StringUtils.isNotBlank((String)typeFilterBean.getSearchValue())) {

					typeFilterBean.setSearchField("cmType.typeCode");
					searchBean.getDataFilterBeans().add(typeFilterBean);
				}

				if (StringUtils.isNotBlank((String)brandFilterBean.getSearchValue())) {

					brandFilterBean.setSearchField("cmBrand.brandCode");
					searchBean.getDataFilterBeans().add(brandFilterBean);
				}


				if (StringUtils.isNotBlank((String)measurementFilterBean.getSearchValue())) {
					measurementFilterBean.setSearchField("cmMeasurement.measurementCode");
					searchBean.getDataFilterBeans().add(measurementFilterBean);
				}

				statusFilterBean.setSearchField("recordStatus");
				statusFilterBean.setSearchValue(true);
				searchBean.getDataFilterBeans().add(statusFilterBean);


				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setItemSearchBean(searchBean);

				CodificationResDTO resDTO = unspscSO.searchItem(reqDTO);
				
				
				
				return resDTO.getItemResultBean();

			} finally {
				unspscSO = null;
			}
		}

	}

	public IPagingDataModel<CmItem> getSearchItemDataModel() {
		return searchItemDataModel;
	}

	public void setSearchItemDataModel(IPagingDataModel<CmItem> searchItemDataModel) {
		this.searchItemDataModel = searchItemDataModel;
	}




	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

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

				unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);


				if (StringUtils.isNotBlank((String)codeFilterBean.getSearchValue())) {
					System.out.println("*********codeFilterBean***********");
					codeFilterBean.setSearchField("unspscCode");
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String)titleFilterBean.getSearchValue())) {
					System.out.println("*********titleFilterBean***********");
					titleFilterBean.setSearchField("unspscTitle");
					searchBean.getDataFilterBeans().add(titleFilterBean);
				}

				/*
				levelFilterBean.setSearchField("unspscLevel");
				levelFilterBean.setSearchValue(3);
				searchBean.getDataFilterBeans().add(levelFilterBean);*/


				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);
				System.out.println("*********after***********");
				return resDTO.getResultBean();

			} finally {
				unspscSO = null;
			}
		}

	}

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

	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public void unspscSearch(){
		searchDataModel.reloadData();
	}

	public void itemSearch(){
		searchItemDataModel.reloadData();
	}




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


	public String goToExtensionSearch(CmUnspsc unspscObj){
		System.out.println("**********goToExtensionSearch************");
		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);


		ICodificationSO unspscSO =  (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();




		String classCode = getParentCode(new Long(unspscObj.getUnspscCode()));

		reqDTO.setUnspscCode(classCode);
		CodificationResDTO classDTO =unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setClassCode(classDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setClassTitle(classDTO.getUnspscObj().getUnspscTitle());

		String familyCode = getParentCode(new Long(classDTO.getUnspscObj().getUnspscCode()));

		reqDTO.setUnspscCode(familyCode);
		CodificationResDTO familyDTO =unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setFamilyCode(familyDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setFamilyTitle(familyDTO.getUnspscObj().getUnspscTitle());

		String segmentCode = getParentCode(new Long(familyDTO.getUnspscObj().getUnspscCode()));

		reqDTO.setUnspscCode(segmentCode);
		CodificationResDTO segmentDTO =unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setSegmentCode(segmentDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setSegmentTitle(segmentDTO.getUnspscObj().getUnspscTitle());


		this.getManagedBean().getSelectedUNSPSCObj().setUnspscCode(unspscObj.getUnspscCode());
		this.getManagedBean().getSelectedUNSPSCObj().setUnspscTitle(unspscObj.getUnspscTitle());



		CodificationResDTO resDTO = unspscSO.searchColor(reqDTO);

		for(CmColor color:resDTO.getColorList()){
			//log.info("color:"+color);
			//log.info("color:"+color.getColorId());
			this.getManagedBean().getColorCriteria().add(new SelectItem(color.getColorCode(), color.getColorName()));
		}

		for(CmBrand brand:unspscObj.getCmBrand()){
			this.getManagedBean().getBrandCriteria().add(new SelectItem(brand.getBrandCode(), brand.getBrandName()));
		}

		for(CmType type:unspscObj.getCmType()){
			this.getManagedBean().getTypeCriteria().add(new SelectItem(type.getTypeCode(), type.getTypeName()));
		}

		for(CmMeasurement measurement:unspscObj.getCmMeasurement()){
			this.getManagedBean().getMeasurementCriteria().add(new SelectItem(measurement.getMeasurementCode(), measurement.getMeasurementName()));
		}





		return NAV_TO_EXTENSION_SEARCH+"?faces-redirect=true";
	}


	public String goToRecommend(CmUnspsc unspscObj){

		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();

		CmRecommendCode recommend = new CmRecommendCode();
		recommend.setUnspscCode(unspscObj.getUnspscCode());
		recommend.setUnspscTitle(unspscObj.getUnspscTitle());
		bb.getManagedBean().getRequestCode().getCmRecommendCodes().add(recommend);

		FacesScopes fs = new FacesScopes();
		System.out.println("CM_ADMIN:"+fs.hasRoleUser("CM_ADMIN"));
		System.out.println("CD_OFFICER:"+fs.hasRoleUser("CD_OFFICER"));

		if(fs.hasRoleUser("CM_ADMIN")){
			return "reviewUnspscCode?faces-redirect=true";
		}else if(fs.hasRoleUser("CD_OFFICER")){
			return "reviewBeforeCreateTempCode?faces-redirect=true";
		}

		return "";


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






	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}



	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}


	public String backToPrev(){


		return "codificationView";
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


	public void unspscPopUpSearch(CmUnspsc obj){


		this.getManagedBean().setClassCode("");
		this.getManagedBean().setClassTitle("");

		this.getManagedBean().setFamilyCode("");
		this.getManagedBean().setFamilyTitle("");

		this.getManagedBean().setSegmentCode("");
		this.getManagedBean().setSegmentTitle("");


		ICodificationSO unspscSO =  (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		Integer level = checkUnspscLevel(new Long(obj.getUnspscCode()));

		System.out.println("level:"+level);

		if(level == 0){

			System.out.println("***********0000****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO familyDTO =unspscSO.searchFamily(reqDTO);

			this.getManagedBean().setUnspscPopUpList(familyDTO.getFamilyList());




			this.getManagedBean().setSegmentCode(obj.getUnspscCode());
			this.getManagedBean().setSegmentTitle(obj.getUnspscTitle());

		}else if (level == 1){
			System.out.println("***********11111****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO ClassDTO =unspscSO.searchClass(reqDTO);

			this.getManagedBean().setUnspscPopUpList(ClassDTO.getClassList());





			this.getManagedBean().setFamilyCode(obj.getUnspscCode());
			this.getManagedBean().setFamilyTitle(obj.getUnspscTitle());

			String segmentCode = getParentCode(new Long(obj.getUnspscCode()));

			reqDTO.setUnspscCode(segmentCode);
			CodificationResDTO segmentDTO =unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setSegmentCode(segmentDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setSegmentTitle(segmentDTO.getUnspscObj().getUnspscTitle());


		}else if(level ==2){
			System.out.println("***********222****************"+obj.getUnspscCode());
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO CommodityDTO =unspscSO.searchCommodity(reqDTO);

			this.getManagedBean().setUnspscPopUpList(CommodityDTO.getCommodityList());




			this.getManagedBean().setClassCode(obj.getUnspscCode());
			this.getManagedBean().setClassTitle(obj.getUnspscTitle());

			String familyCode = getParentCode(new Long(obj.getUnspscCode()));
			System.out.println("familyCode:"+familyCode);
			reqDTO.setUnspscCode(familyCode);
			CodificationResDTO familyDTO =unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setFamilyCode(familyDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setFamilyTitle(familyDTO.getUnspscObj().getUnspscTitle());

			String segmentCode = getParentCode(new Long(familyDTO.getUnspscObj().getUnspscCode()));
			System.out.println("segmentCode:"+segmentCode);
			reqDTO.setUnspscCode(segmentCode);
			CodificationResDTO segmentDTO =unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setSegmentCode(segmentDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setSegmentTitle(segmentDTO.getUnspscObj().getUnspscTitle());


		}


	}


	public Integer checkUnspscLevel(long code){

		Integer level = 0;

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

	public String backToReviewUnspsc(){
		FacesScopes fs = new FacesScopes();

		System.out.println("CD_ADMIN"+fs.hasRoleUser("CM_ADMIN"));



		return fs.hasRoleUser("CM_ADMIN")==true?"reviewUnspscCode?faces-redirect=true":"reviewBeforeCreateTempCode?faces-redirect=true";
	}


	public void onNodeSelect(NodeSelectEvent event) { 


		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();

		NameNode tr = (NameNode)tree.getData();
		log.debug("sequence:"+tr.getSeq());






	}

	public void onNodeExpand(NodeExpandEvent event) {  

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();
		NameNode tr = (NameNode)tree.getData();

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();


		/*
		if(tr.getUnspscLevel() == 0){
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchFamily(reqDTO);

			for(CmUnspsc catFamily : resDTO.getFamilyList()){

				FamiliesVO familiesVO = new FamiliesVO();
				familiesVO.setName(catFamily.getUnspscTitle());
				familiesVO.setParentCode(catFamily.getUnspscCode());
				familiesVO.setUnspscLevel(1);
				TreeNode families = new DefaultTreeNode("families",familiesVO, tree);

			}

		}
		 */
		System.out.println("tr.getUnspscLevel()"+tr.getUnspscLevel());
		if(tr.getUnspscLevel() == 1){
			System.out.println("1111111111111111");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

			for(CmUnspsc catClass : resDTO.getClassList()){

				ClassesVO classesVO = new ClassesVO();
				classesVO.setName(catClass.getUnspscTitle());
				classesVO.setParentCode(catClass.getUnspscCode());
				classesVO.setUnspscLevel(2);

				TreeNode families = new DefaultTreeNode("class",classesVO, tree);

			}

		}

		if(tr.getUnspscLevel() == 2){
			System.out.println("2222222222222222222");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

			for(CmUnspsc catCommodity : resDTO.getCommodityList()){

				CommoditiesVO commoditiesVO = new CommoditiesVO();
				commoditiesVO.setName(catCommodity.getUnspscTitle());
				commoditiesVO.setSeq(catCommodity.getUnspscId());
				commoditiesVO.setParentCode(catCommodity.getUnspscCode());
				commoditiesVO.setUnspscLevel(3);

				TreeNode families = new DefaultTreeNode("commodities",commoditiesVO, tree);

			}

		}


	} 


	public TreeNode getRootNodes() {
		return rootNodes;
	}

	public void setRootNodes(TreeNode rootNodes) {
		this.rootNodes = rootNodes;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}








}

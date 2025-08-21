package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;


import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "extensionCodeCreationMB")
@SessionScoped
public class ExtensionCodeCreationManagedBean extends BaseManagedBean {
	
	private boolean editMode = false;
	private int editModeIndex = 0;
	
	
	private List<CmBrand> brandList;
	private List<CmColor> colorList;
	private List<CmType> typeList;
	private List<CmMeasurement> measurementList;
	
	
	private List<CmUnspsc> unspscList;
	private CmUnspsc selectedUNSPSCObj;

	private String unspscCode;
	private String unspscTitle;
	private String typeName; /*[#27625]*/

	private List<SelectItem> typeCriteria;
	private List<SelectItem> brandCriteria;
	private List<SelectItem> measurementCriteria;
	private List<SelectItem> colorCriteria;
	private List<SelectItem> typeListSI; /*[#27625]*/
	

	private List<CmUnspsc> segmentList;
	private List<CmUnspsc> familyList;
	private List<CmUnspsc> classList;
	private List<CmUnspsc> commodityList;

	private CmUnspsc selectedLovUnspscSegment;
	private CmUnspsc selectedLovUnspscFamily;
	private CmUnspsc selectedLovUnspscClass;
	private CmUnspsc selectedLovUnspsc;
	private CmUnspsc selectedLovUnspscfamily;


	private String itemCode;

	private List<SelectItem> segmentCriteria;
	private List<SelectItem> familyCriteria;
	private List<SelectItem> classCriteria;
	private List<SelectItem> commodityCriteria;

	private String segmentCode;
	private String familyCode;
	private String classCode;
	private String commodityCode;

	private String segmentTitle;
	private String familyTitle;
	private String classTitle;
	private String commodityTitle;

	private String brandCode;
	private String typeCode;
	private String colorCode;
	private String measurementCode;
	private String editTypeName;
	private String editColorName;
	private String editMeasurementName;
	private String editBrandName;
	private String tempTypeName;
	private String tempColorName;
	private String tempMeasurementName;
	private String tempBrandName;
	
	private List<CmUnspsc> extensionPopUpList;

	private String popUpUnspscCode;
	private String popUpUnspscTitle;

	private List<CmUnspsc> recommendUnspscList;

	private CmUnspsc autoCompleteUnspscCode;
	private CmRequestCode requestExtCode;
	private List<CmRequestCode> requestExtCodeList;
	
	private boolean brandDisable = true;
	private boolean typeDisable = true;
	private boolean colorDisable = true;
	private boolean measurementDisable = true;

	private String assignPopUpWidth="680";
	private String assignPopUpHeight="550";

	private String currentTaskName;

	private Object selectedKeywordObject;
	private CmItem keywordItem;
	private boolean productKeyword =false;
	private boolean serviceKeyword =false;
	private boolean refreshPageNoFlag=false;
	private boolean isSourcing = true;
	
	
	private boolean typeRedundancy;
	private boolean barndRedundancy;
	private boolean measurRedundancy;
	private boolean colorRedundancy;
	
	
	private boolean disableSelectBtn;
	
	//#25551 18042020 - azza - start
	private boolean requestType = true;
	private boolean requestBrand = true;
	private boolean requestMeasurement = true;
	private boolean requestColor = true;
	//#25551 18042020 - azza - end

	// 35503 Start
	private List<CmRequestCode> auditRequestExtList;
	// 35503 End
	
	/** The back to previous page. */
	private String backToPreviousPage;



	private SearchFilterBean codeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleEngFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean levelFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private String itemTypeParameter;

	private SearchFilterBean colorFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());


	private boolean extensionSearchcheckBoxAll = false;
	
	
	private String docNo;
	private String docTitle;
	
	private boolean assignCodeFlg = false;



	
	public void initRefreshPageNoFlag(){
		
		if(this.isRefreshPageNoFlag()){
			System.out.println("****************************");
			//PagingDataModel paging = new ExtensionSearchItemDataModel();
			//paging.setPageNo(0);
			
			//this.getExtensionSearchItemDataModel().resetData();
			this.setRefreshPageNoFlag(false);
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
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

			if (getAutoCompleteUnspscCode().getUnspscCode() != null
					&& getAutoCompleteUnspscCode()
					.getUnspscCode().trim().length() > 0) {
				codeFilterBean.setSearchValue(getAutoCompleteUnspscCode().getUnspscCode());
			}

			if (StringUtils.isBlank((String) codeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) titleFilterBean
							.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {

				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);

				String unspscCode = "";
				if (getAutoCompleteUnspscCode()
						.getUnspscCode() != null
						&&getAutoCompleteUnspscCode()
						.getUnspscCode().trim().length() > 0) {
					
					if(getAutoCompleteUnspscCode().getUnspscCode().equalsIgnoreCase("00000000")){
						unspscCode = "notValid";
					}else 
					  if(productCode(getAutoCompleteUnspscCode().getUnspscCode())== true){
						unspscCode = "notValid";
					}else{
						unspscCode = getAutoCompleteUnspscCode().getUnspscCode();
					}

					codeFilterBean.setSearchField("unspscCode");
					codeFilterBean.setSearchValue(unspscCode);
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String) titleFilterBean
						.getSearchValue())) {

					titleFilterBean.setSearchField("unspscTitle");
					titleFilterBean.setCaseInsensitive(true);
					titleFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());

					titleEngFilterBean.setSearchField("unspscTitleEng");
					titleEngFilterBean.setCaseInsensitive(true);
					titleEngFilterBean.setSearchValue(titleFilterBean.getSearchValue());
					titleEngFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					
					SearchMultiFilterBean bilingualFilter = new SearchMultiFilterBean(titleFilterBean,titleEngFilterBean);
					
					searchBean.getDataFilterBeans().add(bilingualFilter);
				}
				
				/*[#27625] begin*/
					setItemTypeParameter("P");
					
					System.out.println("getItemTypeParameter " + getItemTypeParameter());
				/*[#27625] end*/
				
				// get parameter from backup property whenever table be reloaded
				if (getItemTypeParameter() != null
						&& !getItemTypeParameter().isEmpty()) {

					if (getItemTypeParameter().equals("S")
							|| getItemTypeParameter().equals("2")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_GREATER_EQUAL
										.value(), "70000000"));
					} else if (getItemTypeParameter().equals("P")
							|| getItemTypeParameter().equals("1")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_LESS.value(),
										"70000000"));
					}
				}


				if(this.getSortField()==null){
					SearchOrderBean orderBean = new SearchOrderBean("unspscCode", true);

					searchBean.getSortColumns().add(orderBean);
				}

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);

				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);


				return resDTO.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				unspscSO = null;
			}
			return new SearchResultBean();
		}

	}



	public CmUnspsc getAutoCompleteUnspscCode() {
		if (autoCompleteUnspscCode == null)
			autoCompleteUnspscCode = new CmUnspsc();
		return autoCompleteUnspscCode;
	}

	public void setAutoCompleteUnspscCode(CmUnspsc autoCompleteUnspscCode) {
		this.autoCompleteUnspscCode = autoCompleteUnspscCode;
	}

	public List<CmUnspsc> getRecommendUnspscList() {
		if (recommendUnspscList == null)
			recommendUnspscList = new ArrayList<CmUnspsc>();
		return recommendUnspscList;
	}

	public void setRecommendUnspscList(List<CmUnspsc> recommendUnspscList) {
		this.recommendUnspscList = recommendUnspscList;
	}

	public String getPopUpUnspscCode() {
		return popUpUnspscCode;
	}

	public void setPopUpUnspscCode(String popUpUnspscCode) {
		this.popUpUnspscCode = popUpUnspscCode;
	}

	public String getPopUpUnspscTitle() {
		return popUpUnspscTitle;
	}

	public void setPopUpUnspscTitle(String popUpUnspscTitle) {
		this.popUpUnspscTitle = popUpUnspscTitle;
	}

	public List<CmUnspsc> getExtensionPopUpList() {
		if (extensionPopUpList == null)
			extensionPopUpList = new ArrayList<CmUnspsc>();
		return extensionPopUpList;
	}

	public void setExtensionPopUpList(List<CmUnspsc> extensionPopUpList) {
		this.extensionPopUpList = extensionPopUpList;
	}

	public String getEditTypeName() {
		return editTypeName;
	}

	public void setEditTypeName(String editTypeName) {
		this.editTypeName = editTypeName;
	}

	public String getEditColorName() {
		return editColorName;
	}

	public void setEditColorName(String editColorName) {
		this.editColorName = editColorName;
	}

	public String getEditMeasurementName() {
		return editMeasurementName;
	}

	public void setEditMeasurementName(String editMeasurementName) {
		this.editMeasurementName = editMeasurementName;
	}

	public String getEditBrandName() {
		return editBrandName;
	}

	public void setEditBrandName(String editBrandName) {
		this.editBrandName = editBrandName;
	}


	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getMeasurementCode() {
		return measurementCode;
	}

	public void setMeasurementCode(String measurementCode) {
		this.measurementCode = measurementCode;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public List<SelectItem> getSegmentCriteria() {
		if (segmentCriteria == null)
			segmentCriteria = new ArrayList<SelectItem>();
		return segmentCriteria;
	}

	public void setSegmentCriteria(List<SelectItem> segmentCriteria) {
		this.segmentCriteria = segmentCriteria;
	}

	public List<SelectItem> getFamilyCriteria() {
		if (familyCriteria == null)
			familyCriteria = new ArrayList<SelectItem>();
		return familyCriteria;
	}

	public void setFamilyCriteria(List<SelectItem> familyCriteria) {
		this.familyCriteria = familyCriteria;
	}

	public List<SelectItem> getClassCriteria() {
		if (classCriteria == null)
			classCriteria = new ArrayList<SelectItem>();
		return classCriteria;
	}

	public void setClassCriteria(List<SelectItem> classCriteria) {
		this.classCriteria = classCriteria;
	}

	public List<SelectItem> getCommodityCriteria() {
		if (commodityCriteria == null)
			commodityCriteria = new ArrayList<SelectItem>();
		return commodityCriteria;
	}

	public void setCommodityCriteria(List<SelectItem> commodityCriteria) {
		this.commodityCriteria = commodityCriteria;
	}

	public List<CmUnspsc> getSegmentList() {
		if (segmentList == null)
			segmentList = new ArrayList<CmUnspsc>();
		return segmentList;
	}

	public void setSegmentList(List<CmUnspsc> segmentList) {
		this.segmentList = segmentList;
	}

	public List<CmUnspsc> getFamilyList() {
		if (familyList == null)
			familyList = new ArrayList<CmUnspsc>();
		return familyList;
	}

	public void setFamilyList(List<CmUnspsc> familyList) {
		this.familyList = familyList;
	}

	public List<CmUnspsc> getClassList() {
		if (classList == null)
			classList = new ArrayList<CmUnspsc>();
		return classList;
	}

	public void setClassList(List<CmUnspsc> classList) {
		this.classList = classList;
	}

	public List<CmUnspsc> getCommodityList() {
		if (commodityList == null)
			commodityList = new ArrayList<CmUnspsc>();
		return commodityList;
	}

	public void setCommodityList(List<CmUnspsc> commodityList) {
		this.commodityList = commodityList;
	}


	public List<SelectItem> getColorCriteria() {
		if (colorCriteria == null)
			colorCriteria = new ArrayList<SelectItem>();
		return colorCriteria;
	}

	public void setColorCriteria(List<SelectItem> colorCriteria) {
		this.colorCriteria = colorCriteria;
	}

	public List<SelectItem> getBrandCriteria() {
		if (brandCriteria == null)
			brandCriteria = new ArrayList<SelectItem>();
		return brandCriteria;
	}

	public void setBrandCriteria(List<SelectItem> brandCriteria) {

		this.brandCriteria = brandCriteria;
	}

	public List<SelectItem> getMeasurementCriteria() {
		if (measurementCriteria == null)
			measurementCriteria = new ArrayList<SelectItem>();
		return measurementCriteria;
	}

	public void setMeasurementCriteria(List<SelectItem> measurementCriteria) {
		this.measurementCriteria = measurementCriteria;
	}

	public List<SelectItem> getTypeCriteria() {
		if (typeCriteria == null)
			typeCriteria = new ArrayList<SelectItem>();
		return typeCriteria;
	}

	public void setTypeCriteria(List<SelectItem> typeCriteria) {
		this.typeCriteria = typeCriteria;
	}

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getUnspscTitle() {
		return unspscTitle;
	}

	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}

	public List<CmUnspsc> getUnspscList() {
		return unspscList;
	}

	public void setUnspscList(List<CmUnspsc> unspscList) {
		this.unspscList = unspscList;
	}

	public CmUnspsc getSelectedUNSPSCObj() {
		if (selectedUNSPSCObj == null)
			selectedUNSPSCObj = new CmUnspsc();
		return selectedUNSPSCObj;
	}

	public void setSelectedUNSPSCObj(CmUnspsc selectedUNSPSCObj) {
		this.selectedUNSPSCObj = selectedUNSPSCObj;

	}

	
	public String getSegmentTitle() {
		return segmentTitle;
	}

	public void setSegmentTitle(String segmentTitle) {
		this.segmentTitle = segmentTitle;
	}

	public String getFamilyTitle() {
		return familyTitle;
	}

	public void setFamilyTitle(String familyTitle) {
		this.familyTitle = familyTitle;
	}

	public String getClassTitle() {
		return classTitle;
	}

	public void setClassTitle(String classTitle) {
		this.classTitle = classTitle;
	}

	public String getCommodityTitle() {
		return commodityTitle;
	}

	public void setCommodityTitle(String commodityTitle) {
		this.commodityTitle = commodityTitle;
	}


	public CmUnspsc getSelectedLovUnspscFamily() {
		if (selectedLovUnspscFamily == null)
			selectedLovUnspscFamily = new CmUnspsc();
		return selectedLovUnspscFamily;
	}

	public void setSelectedLovUnspscFamily(CmUnspsc selectedLovUnspscFamily) {
		this.selectedLovUnspscFamily = selectedLovUnspscFamily;
		System.out.println(">>>>>>>>>>seting selectedLovUnspscFamily="
				+ selectedLovUnspscFamily);
	}


	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public CmRequestCode getRequestExtCode() {
		if(requestExtCode==null)
			requestExtCode = new CmRequestCode();
		return requestExtCode;
	}


	public void setRequestExtCode(CmRequestCode requestExtCode) {
		this.requestExtCode = requestExtCode;
	}

	public boolean isBrandDisable() {
		return brandDisable;
	}

	public void setBrandDisable(boolean brandDisable) {
		this.brandDisable = brandDisable;
	}

	public boolean isTypeDisable() {
		return typeDisable;
	}

	public void setTypeDisable(boolean typeDisable) {
		this.typeDisable = typeDisable;
	}

	public boolean isColorDisable() {
		return colorDisable;
	}

	public void setColorDisable(boolean colorDisable) {
		this.colorDisable = colorDisable;
	}

	public boolean isMeasurementDisable() {
		return measurementDisable;
	}

	public void setMeasurementDisable(boolean measurementDisable) {
		this.measurementDisable = measurementDisable;
	}


	public List<CmRequestCode> getRequestExtCodeList() {
		if(requestExtCodeList==null)
			requestExtCodeList = new ArrayList<CmRequestCode>();
		return requestExtCodeList;
	}

	public void setRequestExtCodeList(List<CmRequestCode> requestExtCodeList) {
		this.requestExtCodeList = requestExtCodeList;
	}

	public String getAssignPopUpWidth() {
		return assignPopUpWidth;
	}

	public void setAssignPopUpWidth(String assignPopUpWidth) {
		this.assignPopUpWidth = assignPopUpWidth;
	}

	public String getAssignPopUpHeight() {
		return assignPopUpHeight;
	}

	public void setAssignPopUpHeight(String assignPopUpHeight) {
		this.assignPopUpHeight = assignPopUpHeight;
	}

	public String getCurrentTaskName() {
		return currentTaskName;
	}

	public void setCurrentTaskName(String currentTaskName) {
		this.currentTaskName = currentTaskName;
	}

	
	public Object getSelectedKeywordObject() {
		return selectedKeywordObject;
	}

	public void setSelectedKeywordObject(Object selectedKeywordObject) {
		this.selectedKeywordObject = selectedKeywordObject;
	}

	public CmItem getKeywordItem() {
		return keywordItem;
	}

	public void setKeywordItem(CmItem keywordItem) {
		this.keywordItem = keywordItem;
	}

	public boolean isProductKeyword() {
		return productKeyword;
	}

	public void setProductKeyword(boolean productKeyword) {
		this.productKeyword = productKeyword;
	}

	public boolean isServiceKeyword() {
		return serviceKeyword;
	}

	public void setServiceKeyword(boolean serviceKeyword) {
		this.serviceKeyword = serviceKeyword;
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

	public String getItemTypeParameter() {
		return itemTypeParameter;
	}

	public void setItemTypeParameter(String itemTypeParameter) {
		this.itemTypeParameter = itemTypeParameter;
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


	public boolean isExtensionSearchcheckBoxAll() {
		return extensionSearchcheckBoxAll;
	}

	public void setExtensionSearchcheckBoxAll(boolean extensionSearchcheckBoxAll) {
		System.out.println("extensionSearchcheckBoxAll*******"+extensionSearchcheckBoxAll);
		this.extensionSearchcheckBoxAll = extensionSearchcheckBoxAll;
	}

	public boolean isRefreshPageNoFlag() {
		return refreshPageNoFlag;
	}

	public void setRefreshPageNoFlag(boolean refreshPageNoFlag) {
		this.refreshPageNoFlag = refreshPageNoFlag;
	}
	
	
	private String textBeforeSign;
	private boolean autoUpdate = true;
	private PmDigiSign pmDigiSign;
	 
	@ManagedProperty(value="#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;


	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	
	public void reset(){
		
		this.setTitleFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.setCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.setSearchDataModel(new SearchPagingDataModel());
		this.setTypeName(null);
		
	}

	/**
	 * @return the backToPreviousPage
	 */
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}

	/**
	 * @param backToPreviousPage the backToPreviousPage to set
	 */
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}

	/**
	 * @return the isSourcing
	 */
	public boolean isSourcing() {
		return isSourcing;
	}

	/**
	 * @param isSourcing the isSourcing to set
	 */
	public void setSourcing(boolean isSourcing) {
		this.isSourcing = isSourcing;
	}

	public boolean isAssignCodeFlg() {
		return assignCodeFlg;
	}

	public void setAssignCodeFlg(boolean assignCodeFlg) {
		this.assignCodeFlg = assignCodeFlg;
	}

	public String getTempTypeName() {
		return tempTypeName;
	}

	public void setTempTypeName(String tempTypeName) {
		this.tempTypeName = tempTypeName;
	}

	public String getTempColorName() {
		return tempColorName;
	}

	public void setTempColorName(String tempColorName) {
		this.tempColorName = tempColorName;
	}

	public String getTempMeasurementName() {
		return tempMeasurementName;
	}

	public void setTempMeasurementName(String tempMeasurementName) {
		this.tempMeasurementName = tempMeasurementName;
	}

	public String getTempBrandName() {
		return tempBrandName;
	}

	public void setTempBrandName(String tempBrandName) {
		this.tempBrandName = tempBrandName;
	}
	
	public CmUnspsc getSelectedLovUnspscfamily() {
		if (selectedLovUnspscfamily == null)
			selectedLovUnspscfamily = new CmUnspsc();
		return selectedLovUnspscfamily;
	}

	public void setSelectedLovUnspscfamily(CmUnspsc selectedLovUnspscfamily) {
		this.selectedLovUnspscfamily = selectedLovUnspscfamily;
	}

	public CmUnspsc getSelectedLovUnspscClass() {
		if (selectedLovUnspscClass == null)
			selectedLovUnspscClass = new CmUnspsc();
		return selectedLovUnspscClass;
	}

	public void setSelectedLovUnspscClass(CmUnspsc selectedLovUnspscClass) {
		this.selectedLovUnspscClass = selectedLovUnspscClass;
	}

	public CmUnspsc getSelectedLovUnspscSegment() {
		if (selectedLovUnspscSegment == null)
			selectedLovUnspscSegment = new CmUnspsc();
		return selectedLovUnspscSegment;
	}

	public void setSelectedLovUnspscSegment(CmUnspsc selectedLovUnspscSegment) {
		this.selectedLovUnspscSegment = selectedLovUnspscSegment;
	}

	public CmUnspsc getSelectedLovUnspsc() {
		if (selectedLovUnspsc == null)
			selectedLovUnspsc = new CmUnspsc();
		return selectedLovUnspsc;
	}

	public void setSelectedLovUnspsc(CmUnspsc selectedLovUnspsc) {
		this.selectedLovUnspsc = selectedLovUnspsc;
	}
	
	
	//Additional...
	
	/**
	 * @return the editMode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * @param editMode the editMode to set
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	
	
	public List<CmBrand> getBrandList() {
		if (brandList == null)
			brandList = new ArrayList<CmBrand>();
		return brandList;
	}

	public void setBrandList(List<CmBrand> brandList) {
		this.brandList = brandList;
	}

	public List<CmColor> getColorList() {
		if (colorList == null)
			colorList = new ArrayList<CmColor>();
		return colorList;
	}

	public void setColorList(List<CmColor> colorList) {
		this.colorList = colorList;
	}

	public List<CmType> getTypeList() {
		if (typeList == null)
			typeList = new ArrayList<CmType>();
		return typeList;
	}

	public void setTypeList(List<CmType> typeList) {
		this.typeList = typeList;
	}

	public List<CmMeasurement> getMeasurementList() {
		if (measurementList == null)
			measurementList = new ArrayList<CmMeasurement>();
		return measurementList;
	}

	public void setMeasurementList(List<CmMeasurement> measurementList) {
		this.measurementList = measurementList;
	}
	
	public boolean productCode(String unspscCode) {

		boolean flag = false;
		String segmentCode = "";

		if (unspscCode != null && unspscCode.trim().length() > 0) {
			segmentCode = unspscCode.substring(0, 2);
		}
		
		System.out.println("segmentCode " + segmentCode);
		
		//#25551 01062020 - azza - start
		if (Integer.parseInt(segmentCode) > 69) { //#35503, Fix as-is bug: #38632
			flag = true;
		//#25551 01062020 - azza - end
		}
		
		System.out.println("flag " + flag);

		return flag;
		
	}
	
	
	public boolean isTypeRedundancy() {
		return typeRedundancy;
	}

	public void setTypeRedundancy(boolean typeRedundancy) {
		this.typeRedundancy = typeRedundancy;
	}

	public boolean isBarndRedundancy() {
		return barndRedundancy;
	}

	public void setBarndRedundancy(boolean barndRedundancy) {
		this.barndRedundancy = barndRedundancy;
	}

	public boolean isMeasurRedundancy() {
		return measurRedundancy;
	}

	public void setMeasurRedundancy(boolean measurRedundancy) {
		this.measurRedundancy = measurRedundancy;
	}

	public boolean isColorRedundancy() {
		return colorRedundancy;
	}

	public void setColorRedundancy(boolean colorRedundancy) {
		this.colorRedundancy = colorRedundancy;
	}
	
	/**
	 * @return the editModeIndex
	 */
	public int getEditModeIndex() {
		return editModeIndex;
	}

	/**
	 * @param editModeIndex the editModeIndex to set
	 */
	public void setEditModeIndex(int editModeIndex) {
		this.editModeIndex = editModeIndex;
	}
	
	
	 public boolean isDisableSelectBtn() {
			return disableSelectBtn;
		}
		
		public void setDisableSelectBtn(boolean disableSelectBtn) {
			this.disableSelectBtn = disableSelectBtn;
		}
		
	//#25551 18042020 - azza - start
	public boolean isRequestType() {
	    return requestType;
	}

	public void setRequestType(boolean requestType) {
		this.requestType = requestType;
	}
	
	public boolean isRequestBrand() {
	    return requestBrand;
	}

	public void setRequestBrand(boolean requestBrand) {
		this.requestBrand = requestBrand;
	}
	
	public boolean isRequestMeasurement() {
	    return requestMeasurement;
	}

	public void setRequestMeasurement(boolean requestMeasurement) {
		this.requestMeasurement = requestMeasurement;
	}
	
	public boolean isRequestColor() {
	    return requestColor;
	}

	public void setRequestColor(boolean requestColor) {
		this.requestColor = requestColor;
	}
	
	//35503 Start
	public List<CmRequestCode> getAuditRequestExtList() {
		if (auditRequestExtList == null)
			auditRequestExtList = new ArrayList<>();
		return auditRequestExtList;
	}

	public void setAuditRequestExtList(List<CmRequestCode> auditRequestExtList) {
		this.auditRequestExtList = auditRequestExtList;
	}
	//35503 End

	/*[#27625] begin*/
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	//#25551 02062020 - azza - start
	public List<SelectItem> getTypeListSI() {
		typeListSI = new ArrayList<SelectItem>();
		typeListSI.add(new SelectItem("P", ResourceBundleUtils.getMessage("lbl.txt.product",ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));   
		//extension code screen only show selected Item Type 'Product'..
//		typeListSI.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//				new FacesScopes().getUserLocale())));
//		typeListSI.add(new SelectItem("S", ResourceBundleUtils.getMessage("lbl.txt.service",ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//				new FacesScopes().getUserLocale())));
		
		return typeListSI;
	}
	//#25551 02062020 - azza - end

	public void setTypeListSI(List<SelectItem> typeListSI) {
		this.typeListSI = typeListSI;
	}
	/*[#27625] end*/
	
	//#25551 18042020 - azza - end
	
}

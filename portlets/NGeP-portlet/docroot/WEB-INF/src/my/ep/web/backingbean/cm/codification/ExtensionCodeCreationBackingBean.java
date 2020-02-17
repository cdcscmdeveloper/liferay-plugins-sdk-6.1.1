package my.ep.web.backingbean.cm.codification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmExtensionCode;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.cm.codification.ExtensionCodeCreationManagedBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.NameNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "extensionCodeCreationBackingBean")
@ViewScoped
public class ExtensionCodeCreationBackingBean extends
BaseBackingBean<ExtensionCodeCreationManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;


	public static final String NAV_TO_EXTENSION_REQUEST_FORM = "extensionCodeCreationRequestForm";
	public static final String NAV_TO_MAIN_PAGE = "codificationMaintenance";
	public static final String NO_61 = "61";

	private static Logger log = Logger.getLogger(ExtensionCodeCreationBackingBean.class);

	private SearchFilterBean codeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean levelFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private SearchFilterBean colorFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean titleEngFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private IPagingDataModel<CmItem> searchItemDataModel = new SearchItemPagingDataModel();

	private IPagingDataModel<Object[]> extensionSearchItemDataModel = new ExtensionSearchItemDataModel();

	public IPagingDataModel<Object[]> getExtensionSearchItemDataModel() {
		return extensionSearchItemDataModel;
	}

	public void setExtensionSearchItemDataModel(
			IPagingDataModel<Object[]> extensionSearchItemDataModel) {
		this.extensionSearchItemDataModel = extensionSearchItemDataModel;
	}

	private TreeNode rootNodes;
	private TreeNode selectedNode;
	private String itemTypeParameter;

	public ExtensionCodeCreationBackingBean() {
		
		this.getManagedBean().setDisableSelectBtn(false);

		if(this.getManagedBean().isRefreshPageNoFlag()){
			System.out.println("****************************");
			
			
			
			this.initRefreshPageNoFlag();
		}
	}

	/**
	 * This method is used to reset the value of UNSPSC Search
	 * 
	 */

	public String resetAction() {
		log.info("In reset action...................");

		this.getManagedBean().reset();
		log.info("after reset.........................");
		return null;
	}

	/**
	 * Extension Search Data Model for displaying list of extension records.
	 * 
	 */

	private class ExtensionSearchItemDataModel extends
	PagingDataModel<Object[]> {

		public ExtensionSearchItemDataModel() {
			super("itemTableID");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {


			System.out.println("........search extension combination 11.................");
			log.info("Unspsc Code:"+ getManagedBean().getSelectedUNSPSCObj().getUnspscCode());

			ICodificationSO codificationSO = null;
			CodificationResDTO resDTO = null;
			try {
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm",
								ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setExtSearchBean(searchBean);

				reqDTO.setUnspscCode(getManagedBean().getSelectedLovUnspsc()
						.getUnspscCode());

			
				System.out.println("*********in********");
				resDTO = codificationSO.searchExtension(reqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				codificationSO = null;
			}
			if (resDTO != null) {
				return resDTO.getExtensionResultBean();
			}
			return null;
		}

	}

	/**
	 * Extension Search for Keywords.
	 * 
	 */
	public void keywordExtensionCodeSearch(){

		this.getManagedBean().getKeywordExtensionSearchItemDataModel().reloadData();

	}

	/**
	 * Extension Search for Assign Contracted Item Code.
	 * 
	 */
	public void extensionCodeSearchForAssign() {

		extensionSearchItemDataModel.reloadData();

	}

	private class SearchItemPagingDataModel extends PagingDataModel<CmItem> {

		private static final long serialVersionUID = 1L;

		public SearchItemPagingDataModel() {
			super("itemTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmItem> pageChangeListener(
				ISearchBean<CmItem> searchBean) {

			if (StringUtils.isBlank((String) colorFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) typeFilterBean
							.getSearchValue())
							&& StringUtils.isBlank((String) brandFilterBean
									.getSearchValue())
									&& StringUtils.isBlank((String) measurementFilterBean
											.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject(ICodificationSO.class);

				if (StringUtils.isNotBlank((String) colorFilterBean
						.getSearchValue())) {
					colorFilterBean.setSearchField("cmColor.colorCode");
					searchBean.getDataFilterBeans().add(colorFilterBean);
				}

				if (StringUtils.isNotBlank((String) typeFilterBean
						.getSearchValue())) {

					typeFilterBean.setSearchField("cmType.typeCode");
					searchBean.getDataFilterBeans().add(typeFilterBean);
				}

				if (StringUtils.isNotBlank((String) brandFilterBean
						.getSearchValue())) {

					brandFilterBean.setSearchField("cmBrand.brandCode");
					searchBean.getDataFilterBeans().add(brandFilterBean);
				}

				if (StringUtils.isNotBlank((String) measurementFilterBean
						.getSearchValue())) {
					measurementFilterBean
					.setSearchField("cmMeasurement.measurementCode");
					searchBean.getDataFilterBeans().add(measurementFilterBean);
				}

				statusFilterBean.setSearchField("recordStatus");
				statusFilterBean.setSearchValue(true);
				searchBean.getDataFilterBeans().add(statusFilterBean);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setItemSearchBean(searchBean);

				// CodificationResDTO resDTO = unspscSO.searchItem(reqDTO);
				CodificationResDTO resDTO = unspscSO.searchExtension(reqDTO);

				List list = resDTO.getExtensionlist();
				for (int i = 0; i < list.size(); i++) {
					log.info("list.get(i): " + list.get(i));
					System.out
					.println("list.get(i): " + list.get(i).getClass());
					Object[] objects = (Object[]) list.get(i);

					for (int j = 0; j < objects.length; j++) {
						log.info("objects[0]: " + objects[j]);
					}
				}

				return resDTO.getItemResultBean();

			} finally {
				unspscSO = null;
			}
		}

	}

	public IPagingDataModel<CmItem> getSearchItemDataModel() {
		return searchItemDataModel;
	}

	public void setSearchItemDataModel(
			IPagingDataModel<CmItem> searchItemDataModel) {
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
			System.out
			.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>titleFilterBean.getSearchValue()="
					+ titleFilterBean.getSearchValue());
			if (getManagedBean().getAutoCompleteUnspscCode().getUnspscCode() != null
					&& getManagedBean().getAutoCompleteUnspscCode()
					.getUnspscCode().trim().length() > 0) {
				codeFilterBean.setSearchValue(getManagedBean()
						.getAutoCompleteUnspscCode().getUnspscCode());
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


				if (getManagedBean().getAutoCompleteUnspscCode()
						.getUnspscCode() != null
						&& getManagedBean().getAutoCompleteUnspscCode()
						.getUnspscCode().trim().length() > 0) {
					log.info("*********codeFilterBean***********");
					codeFilterBean.setSearchField("unspscCode");
					codeFilterBean.setSearchValue(getManagedBean()
							.getAutoCompleteUnspscCode().getUnspscCode());
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String) titleFilterBean
						.getSearchValue())) {
					log.info("*********titleFilterBean***********");
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
				FacesScopes fs = new FacesScopes();
				String itemType = fs.getParameter("itemType");

				log.info("............itemType=" + itemType);
				if (itemType != null) {// save parameter first time it come from
					// search button
					setItemTypeParameter(itemType);
				}
				log.info("............getItemTypeParameter="
						+ getItemTypeParameter());
				log.info("............ isAssignCodeFlg ="+ getManagedBean().isAssignCodeFlg());
				// get parameter from backup property whenever table be reloaded
				if(!getManagedBean().isAssignCodeFlg()) {
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
				}


				System.out.println("this.getSortField():"+this.getSortField());
				if(this.getSortField()==null){
					SearchOrderBean orderBean = new SearchOrderBean("unspscCode", true);

					searchBean.getSortColumns().add(orderBean);
				}

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				log.info("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				log.info("*********after***********");
				return resDTO.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				unspscSO = null;
			}
			return new SearchResultBean();
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

	/**
	 * UNSPSC Search Data Model for displaying list of UNSPSC records.
	 * 
	 */

	public void unspscSearch() {
		String unspscCode = getManagedBean().getAutoCompleteUnspscCode()
				.getUnspscCode();
		String unspscTitle = this.getManagedBean().getTitleFilterBean().getSearchValue().toString();


		if ((unspscCode == null && (unspscTitle == null || unspscTitle
				.isEmpty()))
				|| ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle
				.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		if ((unspscCode != null && !unspscCode.isEmpty())
				&& (unspscCode.length() != 8 || !StringUtils.isNumeric(unspscCode))){
			throw new EpException(MessageConst.ERROR_715026);
		}


		if(unspscCode == null && unspscTitle.trim().length()<3){
			throw new EpException(MessageConst.ERROR_715060);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}

	public void itemSearch() {
		searchItemDataModel.reloadData();
	}


	private int compareFirstChoice(String code1, String code2, int last){
		if((code1.equals("000") || code1.equals("00")) && (code2.equals("000") || code2.equals("00"))){
			return 0;
		}else if((code1.equals("000") || code1.equals("00"))){
			return -1;
		}else if((code2.equals("000") || code2.equals("00"))){
			return 1;
		}
		return last;
	}; 
	


	/**
	 * Navigate to extension Search screen.
	 * 
	 * @return extensionSearch.xhtml
	 */

	public String goToExtensionSearchPortal(CmUnspsc obj) {
		log.debug("**********goToExtensionSearchPortal************");
		
		log.info("obj " + obj);

		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(obj.getUnspscCode());
		CodificationResDTO resDTO = unspscSO
				.retrieveUNSPSCByCommodityCode(reqDTO);

		
		if (resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();
			
			this.getManagedBean().setSelectedLovUnspsc(commodity);
			
			
			log.info("unspscCode " + this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			log.info("unspscTitle " + this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		}
		
	

		System.out.println("NAV_TO_EXTENSION_REQUEST_FORM");
		return NAV_TO_EXTENSION_REQUEST_FORM + "?faces-redirect=true";
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

	

	/**
	 * filter family code by segment code
	 * 
	 */

	public void filterFamily() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getSegmentCode()"
				+ this.getManagedBean().getSegmentCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getSegmentCode());

		CodificationResDTO resDTO = codificationSO.searchFamily(reqDTO);

		for (CmUnspsc family : resDTO.getFamilyList()) {

			this.getManagedBean()
			.getFamilyCriteria()
			.add(new SelectItem(family.getUnspscCode(), family
					.getUnspscTitle()));
		}

	}

	/**
	 * filter class code by family code
	 * 
	 */
	public void filterClass() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getFamilyCode()"
				+ this.getManagedBean().getFamilyCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getFamilyCode());

		CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

		for (CmUnspsc classes : resDTO.getClassList()) {

			this.getManagedBean()
			.getClassCriteria()
			.add(new SelectItem(classes.getUnspscCode(), classes
					.getUnspscTitle()));
		}

	}

	/**
	 * filter commodity code by class code
	 * 
	 */

	public void filterCommodity() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getClassCode()"
				+ this.getManagedBean().getClassCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getClassCode());

		CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

		for (CmUnspsc commodity : resDTO.getCommodityList()) {

			this.getManagedBean()
			.getCommodityCriteria()
			.add(new SelectItem(commodity.getUnspscCode(), commodity
					.getUnspscTitle()));
		}

	}

	public void filterExtType() {
		log.info("this.getManagedBean().getCommodityCode()"
				+ this.getManagedBean().getCommodityCode());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(this.getManagedBean().getCommodityCode());

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

		for (CmBrand brand : resDTO.getCommodityObj().getCmBrand()) {
			log.debug("code:" + brand.getBrandCode());
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
				.getBrandCriteria()
				.add(new SelectItem(brand.getBrandId(), brand
						.getBrandName()));
			}
		}

		for (CmType type : resDTO.getCommodityObj().getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
				.getTypeCriteria()
				.add(new SelectItem(type.getTypeId(), type
						.getTypeName()));
			}
		}

		for (CmMeasurement measurement : resDTO.getCommodityObj()
				.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
				.getMeasurementCriteria()
				.add(new SelectItem(measurement.getMeasurementId(),
						measurement.getMeasurementName()));
			}
		}

		resDTO = codificationSO.searchColor(reqDTO);
		for (CmColor color : resDTO.getColorList()) {
			if (!StringUtils.isBlank(color.getColorName())) {
				this.getManagedBean()
				.getColorCriteria()
				.add(new SelectItem(color.getColorId(), color
						.getColorName()));
			}
		}

	}


	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}
	

	public String backToPrevPortal() {
		this.initRefreshPageNoFlag();
		renewSesionObject(); 
		System.out.println("NAV_TO_MAIN_PAGE");
		return NAV_TO_MAIN_PAGE + "?faces-redirect=true"; 
	}

	public String getParentCode(long code) {

		String parent = null;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			parent = (String) String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:" + parent);
		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				parent = (String) String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:" + parent);
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					parent = (String) String.valueOf(code).substring(0, 2)
							+ "000000";
					log.debug("parent:" + parent);
				} else {
					log.debug("segment");
				}

			}

		}

		return parent;
	}

	public void unspscPopUpSearch(CmUnspsc obj) {
		
		log.info("obj " + obj );

		clearPopup();

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		Integer level = checkUnspscLevel(new Long(obj.getUnspscCode()));

		log.info("level:" + level);

		if (level == 0) {

			log.info("***********0000****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO familyDTO = unspscSO.searchFamily(reqDTO);

			this.getManagedBean().setExtensionPopUpList(familyDTO.getFamilyList());

			this.getManagedBean().setSegmentCode(obj.getUnspscCode());
			this.getManagedBean().setSegmentTitle(obj.getUnspscTitle());

		} else if (level == 1) {
			log.info("***********11111****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO ClassDTO = unspscSO.searchClass(reqDTO);

			this.getManagedBean().setExtensionPopUpList(ClassDTO.getClassList());

			this.getManagedBean().setFamilyCode(obj.getUnspscCode());
			this.getManagedBean().setFamilyTitle(obj.getUnspscTitle());

			String segmentCode = getParentCode(new Long(obj.getUnspscCode()));

			reqDTO.setUnspscCode(segmentCode);
			CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setSegmentCode(
					segmentDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setSegmentTitle(
					segmentDTO.getUnspscObj().getUnspscTitle());

		} else if (level == 2) {
			log.info("***********222****************"
					+ obj.getUnspscCode());
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO CommodityDTO = unspscSO.searchCommodity(reqDTO);

			this.getManagedBean().setExtensionPopUpList(
					CommodityDTO.getCommodityList());

			this.getManagedBean().setClassCode(obj.getUnspscCode());
			this.getManagedBean().setClassTitle(obj.getUnspscTitle());

			String familyCode = getParentCode(new Long(obj.getUnspscCode()));
			log.info("familyCode:" + familyCode);
			reqDTO.setUnspscCode(familyCode);
			CodificationResDTO familyDTO = unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setFamilyCode(
					familyDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setFamilyTitle(
					familyDTO.getUnspscObj().getUnspscTitle());

			String segmentCode = getParentCode(new Long(familyDTO
					.getUnspscObj().getUnspscCode()));
			log.info("segmentCode:" + segmentCode);
			reqDTO.setUnspscCode(segmentCode);
			CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setSegmentCode(
					segmentDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setSegmentTitle(
					segmentDTO.getUnspscObj().getUnspscTitle());
			
		
		}
	}

	public Integer checkUnspscLevel(long code) {

		Integer level = 0;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			level = 3;

		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				level = 2;
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					level = 1;
				} else {
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}


	public String backToExtensionSearch() {
		return "extensionCodeCreation.xhtml?faces-redirect=true";
	}

	public void onNodeSelect(NodeSelectEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();

		NameNode tr = (NameNode) tree.getData();
		log.debug("sequence:" + tr.getSeq());

	}

	public void onNodeExpand(NodeExpandEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();
		NameNode tr = (NameNode) tree.getData();

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		/*
		 * if(tr.getUnspscLevel() == 0){
		 * reqDTO.setParentCode(tr.getParentCode()); CodificationResDTO resDTO =
		 * codificationSO.searchFamily(reqDTO);
		 * 
		 * for(CmUnspsc catFamily : resDTO.getFamilyList()){
		 * 
		 * FamiliesVO familiesVO = new FamiliesVO();
		 * familiesVO.setName(catFamily.getUnspscTitle());
		 * familiesVO.setParentCode(catFamily.getUnspscCode());
		 * familiesVO.setUnspscLevel(1); TreeNode families = new
		 * DefaultTreeNode("families",familiesVO, tree);
		 * 
		 * }
		 * 
		 * }
		 */
		log.info("tr.getUnspscLevel()" + tr.getUnspscLevel());
		if (tr.getUnspscLevel() == 1) {
			log.info("1111111111111111");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

			for (CmUnspsc catClass : resDTO.getClassList()) {

				ClassesVO classesVO = new ClassesVO();
				classesVO.setName(catClass.getUnspscTitle());
				classesVO.setParentCode(catClass.getUnspscCode());
				classesVO.setUnspscLevel(2);

				TreeNode families = new DefaultTreeNode("class", classesVO,
						tree);

			}

		}

		if (tr.getUnspscLevel() == 2) {
			log.info("2222222222222222222");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

			for (CmUnspsc catCommodity : resDTO.getCommodityList()) {

				CommoditiesVO commoditiesVO = new CommoditiesVO();
				commoditiesVO.setName(catCommodity.getUnspscTitle());
				commoditiesVO.setSeq(catCommodity.getUnspscId());
				commoditiesVO.setParentCode(catCommodity.getUnspscCode());
				commoditiesVO.setUnspscLevel(3);

				TreeNode families = new DefaultTreeNode("commodities",
						commoditiesVO, tree);

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

	public List<String> autoCompleteUnspsc(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);
		
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		
	

		List<String> unspscCodes = new ArrayList<String>();
		
		
		try {
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(query);
			
			CodificationResDTO resDTO = codificationSO
					.searchUnspscCodeByAuto(reqDTO);
			
		
			if (resDTO.getUnspscList() != null)
				for (CmUnspsc unspsc : resDTO.getUnspscList()) {
						
					int code = (Integer.parseInt(unspsc.getUnspscCode().substring(0, 2)));
					
                
					if (code < 61){
						unspscCodes.add(unspsc.getUnspscCode());
					}		
				}	

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			codificationSO = null;
		}

		return unspscCodes;
	}

	public String verifyExtensionSearch(CmUnspsc unspscObj) {
		log.debug("**********goToExtensionSearch************");

		FacesScopes fs = new FacesScopes();
		this.getManagedBean().setCurrentTaskName(fs.getParameter("currentTaskName"));        
		UnspscCodeManagedBean unspscManagedBean = (UnspscCodeManagedBean) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("unspscCodeMB");
        this.getManagedBean().setTempBrandName(unspscManagedBean.getRequestCode().getBrandName());
        this.getManagedBean().setTempTypeName(unspscManagedBean.getRequestCode().getTypeName());
        this.getManagedBean().setTempColorName(unspscManagedBean.getRequestCode().getColorName());
        this.getManagedBean().setTempMeasurementName(unspscManagedBean.getRequestCode().getMeasurementName());
		log.info(this.getManagedBean().getTempMeasurementName());

  		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(unspscObj.getUnspscCode());
		CodificationResDTO comodityDTO = unspscSO
				.retrieveUNSPSCByCommodityCode(reqDTO);

		String classCode = getParentCode(new Long(unspscObj.getUnspscCode()));

		reqDTO.setUnspscCode(classCode);
		CodificationResDTO classDTO = unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setClassCode(
				classDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setClassTitle(
				classDTO.getUnspscObj().getUnspscTitle());

		String familyCode = getParentCode(new Long(classDTO.getUnspscObj()
				.getUnspscCode()));

		reqDTO.setUnspscCode(familyCode);
		CodificationResDTO familyDTO = unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setFamilyCode(
				familyDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setFamilyTitle(
				familyDTO.getUnspscObj().getUnspscTitle());

		String segmentCode = getParentCode(new Long(familyDTO.getUnspscObj()
				.getUnspscCode()));

		reqDTO.setUnspscCode(segmentCode);
		CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);

		this.getManagedBean().setSegmentCode(
				segmentDTO.getUnspscObj().getUnspscCode());
		this.getManagedBean().setSegmentTitle(
				segmentDTO.getUnspscObj().getUnspscTitle());

		this.getManagedBean().getSelectedUNSPSCObj()
		.setUnspscCode(unspscObj.getUnspscCode());
		this.getManagedBean().getSelectedUNSPSCObj()
		.setUnspscTitle(unspscObj.getUnspscTitle());

		CodificationResDTO resDTO = unspscSO.searchColor(reqDTO);
		Collections.sort(comodityDTO.getCommodityObj().getCmBrand(), new Comparator<CmBrand>() {
		    @Override
		    public int compare(CmBrand t1, CmBrand t2) {
		        return compareFirstChoice(t1.getBrandCode(), t2.getBrandCode(), t1.getBrandName().trim().compareToIgnoreCase(t2.getBrandName().trim()));
		    }
		});
		Collections.sort(comodityDTO.getCommodityObj().getCmType(), new Comparator<CmType>() {
		    @Override
		    public int compare(CmType t1, CmType t2) {
		        return compareFirstChoice(t1.getTypeCode(), t2.getTypeCode(), t1.getTypeName().trim().compareToIgnoreCase(t2.getTypeName().trim()));
		    }
		});
		Collections.sort(comodityDTO.getCommodityObj().getCmMeasurement(), new Comparator<CmMeasurement>() {
		    @Override
		    public int compare(CmMeasurement t1, CmMeasurement t2) {
		        return compareFirstChoice(t1.getMeasurementCode(), t2.getMeasurementCode(), t1.getMeasurementName().trim().compareToIgnoreCase(t2.getMeasurementName().trim()));
		    }
		});
		
		Collections.sort(resDTO.getColorList(), new Comparator<CmColor>() {
		    @Override
		    public int compare(CmColor t1, CmColor t2) {
		        return compareFirstChoice(t1.getColorCode(), t2.getColorCode(), t1.getColorName().trim().compareToIgnoreCase(t2.getColorName().trim()));
		    }
		});
		for (CmColor color : resDTO.getColorList()) {
			// log.info("color:"+color);
			// log.info("color:"+color.getColorId());
			if (!StringUtils.isBlank(color.getColorName())) {
				this.getManagedBean()
				.getColorCriteria()
				.add(new SelectItem(color.getColorId(), color
						.getColorName()));
			}
		}

		for (CmBrand brand : comodityDTO.getCommodityObj().getCmBrand()) {
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
				.getBrandCriteria()
				.add(new SelectItem(brand.getBrandId(), brand
						.getBrandName()));
			}
		}

		for (CmType type : comodityDTO.getCommodityObj().getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
				.getTypeCriteria()
				.add(new SelectItem(type.getTypeId(), type
						.getTypeName()));
			}
		}

		for (CmMeasurement measurement : comodityDTO.getCommodityObj()
				.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
				.getMeasurementCriteria()
				.add(new SelectItem(measurement.getMeasurementId(),
						measurement.getMeasurementName()));
			}
		}

		if (this.getManagedBean().getCurrentTaskName() != null
				&& (this.getManagedBean()
						.getCurrentTaskName()
						.equalsIgnoreCase(
								my.ep.constant.cm.CmConst.TASK_REVIEW_REQUEST) || (this
										.getManagedBean().getCurrentTaskName()
										.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE)) || (this
												.getManagedBean().getCurrentTaskName()
												.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_REVIEW_EXT_CODE))	 || (this
														.getManagedBean().getCurrentTaskName()
														.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_APPROVE_EXT_CODE))  || (this
																.getManagedBean().getCurrentTaskName()
																.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_APPROVE_AND_PUBLISH_EXT_CODE)) || (this
																		.getManagedBean().getCurrentTaskName()
																		.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_REVISE_EXT_CODE)) || (this
																				.getManagedBean().getCurrentTaskName()
																				.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_REVISE_REQUEST))	 || (this
																						.getManagedBean().getCurrentTaskName()
																						.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_REVISE_TEMP_CODE))							
						)) {
			return "verifyExtensionSearch?faces-redirect=true";
		}
		// if come from pending codification
		return "searchExtensionToAssignItem?faces-redirect=true";

	}


	public void checkExistingExtension(String input, boolean isSelectFromList, String attr) {
		if (StringUtils.isNotBlank(input)) {
			List<SelectItem> sl = null;
			String label = null;
			if(attr.equalsIgnoreCase("type")){
				sl =  this.getManagedBean().getTypeCriteria();
				label = "lbl.cm.type";
			}else	if(attr.equalsIgnoreCase("brand")){
				sl =  this.getManagedBean().getBrandCriteria();
				label = "lbl.cm.brand";
			}else	if(attr.equalsIgnoreCase("measurement")){
				sl =  this.getManagedBean().getMeasurementCriteria();
				label = "lbl.cm.measurement";
			}else	if(attr.equalsIgnoreCase("color")){
				sl =  this.getManagedBean().getColorCriteria();
				label = "lbl.cm.color";
			}
			if(sl!=null && sl.size()>0){
				for (SelectItem ext : sl) {
					if (!StringUtils.isBlank(input)) {
						if (input.toLowerCase().trim().equals(ext.getLabel().toString().toLowerCase().trim())) {
							if(isSelectFromList){
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ input },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
							throw new EpException(
									MessageConst.ERROR_716009,
									new String[] { ResourceBundleUtils
											.getMessage(
													label,
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes()
													.getUserLocale()) });
						}
					}
				}
			}
		}
	}
	
	public String requestExtensionWithUNSPSC() {

		UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();

		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		log.info("token:" + token);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		String prevColorName = null;
		String prevTypeName = null;
		String prevBrandName = null;
		String prevMeasurementName = null;
		
		
		//check for empty extension request and extension selection
		if((StringUtils.isBlank((String) measurementFilterBean
				.getSearchValue()) && StringUtils.isBlank(this.getManagedBean().getTempMeasurementName())) || 
				(StringUtils.isBlank((String) colorFilterBean
						.getSearchValue()) && StringUtils.isBlank(this.getManagedBean().getTempColorName())) ||
						(StringUtils.isBlank((String) brandFilterBean
								.getSearchValue()) && StringUtils.isBlank(this.getManagedBean().getTempBrandName())) ||
								(StringUtils.isBlank((String) typeFilterBean
										.getSearchValue())&& StringUtils.isBlank(this.getManagedBean().getTempTypeName()))){
			throw new EpException(MessageUtils
					.getMessage(MessageConst.ERROR_716020,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
			
		}
		
		if (StringUtils.isNotBlank((String) colorFilterBean.getSearchValue())) {

			ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
			colorFilterBean.setSearchField("colorId");
			searchBeanColor.getDataFilterBeans().add(colorFilterBean);
			reqDTO.setColorSearchBean(searchBeanColor);
			resDTO = unspscSO.searchColorObj(reqDTO);
			prevColorName = resDTO.getColorList().get(0).getColorName();
		
			unspscBB.getManagedBean().getRequestCode()
			.setColorId(resDTO.getColorList().get(0).getColorId());
			unspscBB.getManagedBean().getRequestCode().setRequestColor(false);
		} else {
			//unspscBB.checkingColorRedundency(this.getManagedBean().getTempColorName(), true);
			checkExistingExtension(this.getManagedBean().getTempColorName(), true, "color");
			unspscBB.getManagedBean().getRequestCode().setColorId(null);
			unspscBB.getManagedBean().getRequestCode().setRequestColor(true);
		}

		if (StringUtils.isNotBlank((String) typeFilterBean.getSearchValue())) {

			ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
			typeFilterBean.setSearchField("typeId");
			searchBeanColor.getDataFilterBeans().add(typeFilterBean);
			reqDTO.setTypeSearchBean(searchBeanColor);
			resDTO = unspscSO.searchTypeObj(reqDTO);
			prevTypeName = resDTO.getTypeList().get(0).getTypeName();

			unspscBB.getManagedBean().getRequestCode()
			.setTypeId(resDTO.getTypeList().get(0).getTypeId());
			unspscBB.getManagedBean().getRequestCode().setRequestType(false);
		} else {
		//	unspscBB.checkingTypeRedundency(this.getManagedBean().getTempTypeName(), true);
			checkExistingExtension(this.getManagedBean().getTempTypeName(), true, "type");
			unspscBB.getManagedBean().getRequestCode().setTypeId(null);
			unspscBB.getManagedBean().getRequestCode().setRequestType(true);
		}

		if (StringUtils.isNotBlank((String) brandFilterBean.getSearchValue())) {

			ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
			brandFilterBean.setSearchField("brandId");
			searchBeanColor.getDataFilterBeans().add(brandFilterBean);
			reqDTO.setBrandSearchBean(searchBeanColor);
			resDTO = unspscSO.searchBrandObj(reqDTO);
			prevBrandName = resDTO.getBrandList().get(0).getBrandName();
			
			unspscBB.getManagedBean().getRequestCode()
			.setBrandId(resDTO.getBrandList().get(0).getBrandId());
			unspscBB.getManagedBean().getRequestCode().setRequestBrand(false);

		} else {
			//unspscBB.checkingBrandRedundency(this.getManagedBean().getTempBrandName(),true);
			checkExistingExtension(this.getManagedBean().getTempBrandName(), true, "brand");
			unspscBB.getManagedBean().getRequestCode().setBrandId(null);
			unspscBB.getManagedBean().getRequestCode().setRequestBrand(true);
		}

		if (StringUtils.isNotBlank((String) measurementFilterBean
				.getSearchValue())) {

			ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
			measurementFilterBean.setSearchField("measurementId");
			searchBeanColor.getDataFilterBeans().add(measurementFilterBean);
			reqDTO.setMeasurementSearchBean(searchBeanColor);

			resDTO = unspscSO.searchMeasurementObj(reqDTO);
			prevMeasurementName = resDTO.getMeasurementList().get(0).getMeasurementName();

			unspscBB.getManagedBean()
			.getRequestCode()
			.setMeasurementId(
					resDTO.getMeasurementList().get(0)
					.getMeasurementId());
			unspscBB.getManagedBean().getRequestCode()
			.setRequestMeasurement(false);
		} else {
			//unspscBB.checkingMeasurmentRedundency(this.getManagedBean().getTempMeasurementName(), true);
			checkExistingExtension(this.getManagedBean().getTempMeasurementName(), true, "measurement");
			unspscBB.getManagedBean().getRequestCode().setMeasurementId(null);
			unspscBB.getManagedBean().getRequestCode().setRequestMeasurement(true);
		}

		unspscBB.getManagedBean()
		.getRequestCode()
		.setTempCode(
				this.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscCode());
		unspscBB.getManagedBean()
		.getRequestCode()
		.setTempTitle(
				this.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscTitle());

		reqDTO.setToken(token);
		reqDTO.setTaskId(unspscBB.getManagedBean().getTaskId());

		if (this.getManagedBean().getCurrentTaskName() != null && (
				this.getManagedBean() .getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE) || 
					this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_REVIEW_EXT_CODE) ||
						this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_REVISE_TEMP_CODE) ||
						this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_APPROVE_AND_PUBLISH_EXT_CODE))){
			log.info("***code admin*** SUBMIT - PSEUDO_REQUEST_EXTENSION");
			reqDTO.setOutCome("SUBMIT");
		} else {
			log.info("***catalogue admin*** REQUEST_EXTENSION");
			reqDTO.setOutCome("REQUEST_EXTENSION");
		}
		IStatusSO statusSO = (IStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		
		boolean requestAndPublish = this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_APPROVE_AND_PUBLISH_EXT_CODE);
		
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(requestAndPublish ? 15015L: 15004L);
		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
//		if(reqDTO.getOutCome().equalsIgnoreCase("REQUEST_EXTENSION") || 
//				this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_REVIEW_EXT_CODE) || 
//					this.getManagedBean().getCurrentTaskName().equalsIgnoreCase( my.ep.constant.cm.CmConst.TASK_APPROVE_EXT_CODE)){
			reqDTO.setTrackingStatus(true);			
			String actionDesc = requestAndPublish ?  "Kod Item "+ unspscBB.getManagedBean().getReviewExtCodeList().get(0).getDocNo() +" telah disyorkan dan diiklankan oleh Jawatankuasa Kodifikasi (CDC)." : 
				"Kod Item "+ unspscBB.getManagedBean().getReviewExtCodeList().get(0).getDocNo() +" telah diserah kepada Jawatankuasa Kodifikasi (CDC) untuk kelulusan." ;
			reqDTO.setTrackingDairyVO(CmApplicationUtil.assignTrackingDiaryValuesForUNSPSCExtension(actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus(),"CU"));
//		}
		
		if(!unspscBB.getManagedBean().getRequestCode().isRequestBrand() &&
				!unspscBB.getManagedBean().getRequestCode().isRequestColor() &&
						!unspscBB.getManagedBean().getRequestCode().isRequestMeasurement() &&
							!unspscBB.getManagedBean().getRequestCode().isRequestType()){
			
			throw  new EpException (MessageUtils.getMessage(MessageConst.ERROR_716018,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
		}
		
		if(requestAndPublish){
			if(unspscBB.getManagedBean().getRequestCode().isRequestBrand()){
				unspscBB.getManagedBean().getRequestCode().setReviewedBrandName(this.getManagedBean().getTempBrandName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setReviewedBrandName(prevBrandName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestType()){
				unspscBB.getManagedBean().getRequestCode().setReviewedTypeName(this.getManagedBean().getTempTypeName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setReviewedTypeName(prevTypeName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestMeasurement()){
				unspscBB.getManagedBean().getRequestCode().setReviewedMeasurementName(this.getManagedBean().getTempMeasurementName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setReviewedMeasurementName(prevMeasurementName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestColor()){
				unspscBB.getManagedBean().getRequestCode().setReviewedColorName(this.getManagedBean().getTempColorName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setReviewedColorName(prevColorName);
			}
		}else{
			if(unspscBB.getManagedBean().getRequestCode().isRequestBrand()){
				unspscBB.getManagedBean().getRequestCode().setBrandName(this.getManagedBean().getTempBrandName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setBrandName(prevBrandName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestType()){
				unspscBB.getManagedBean().getRequestCode().setTypeName(this.getManagedBean().getTempTypeName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setTypeName(prevTypeName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestMeasurement()){
				unspscBB.getManagedBean().getRequestCode().setMeasurementName(this.getManagedBean().getTempMeasurementName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setMeasurementName(prevMeasurementName);
			}
			if(unspscBB.getManagedBean().getRequestCode().isRequestColor()){
				unspscBB.getManagedBean().getRequestCode().setColorName(this.getManagedBean().getTempColorName());
			}else{
				unspscBB.getManagedBean().getRequestCode().setColorName(prevColorName);
			}
		}
		reqDTO.getRequestExtList().add(
				unspscBB.getManagedBean().getRequestCode());
		
		UserReqDTO userReqDTO = new UserReqDTO();

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setRoleCode(RoleCode.CD_COMMITTEE.value());
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

		if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
			for(PmUser user :userResDTO.getUserList()){
				System.out.println("user.getLoginId():"+user.getLoginId());
				reqDTO.getCdcList().add(user.getLoginId());
			}
		}
		reqDTO.setRemark(unspscBB.getManagedBean().getSelectedRemarks().get(0));
		if(requestAndPublish){
			resDTO = unspscSO.publishCodificationRequest(reqDTO);
		}else{
			resDTO = unspscSO.updateExtensionRequest(reqDTO);
		}
		
		
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "BPMtesting";
	}

	public CmType searchTypeOjbyTypeId(Long typeId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmType> searchBeanType = new SearchBean<CmType>();
		typeFilterBean.setSearchField("typeId");
		typeFilterBean.setSearchValue(typeId);
		searchBeanType.getDataFilterBeans().add(typeFilterBean);
		reqDTO.setTypeSearchBean(searchBeanType);
		resDTO = codificationSO.searchTypeObj(reqDTO);

		return resDTO.getTypeList().get(0);
	}

	public CmColor searchColorObjByColorId(long colorId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorId");
		colorFilterBean.setSearchValue(colorId);

		searchBeanColor.getDataFilterBeans().add(colorFilterBean);
		reqDTO.setColorSearchBean(searchBeanColor);
		resDTO = codificationSO.searchColorObj(reqDTO);

		return resDTO.getColorList().get(0);

	}

	public CmColor searchColorObjByColorCode(String colorCode) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorCode");
		colorFilterBean.setSearchValue(colorCode);

		searchBeanColor.getDataFilterBeans().add(colorFilterBean);
		reqDTO.setColorSearchBean(searchBeanColor);
		resDTO = codificationSO.searchColorObj(reqDTO);

		return resDTO.getColorList().get(0);

	}

	public CmBrand searchBrandObjByBrandId(Long brandId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		ISearchBean<CmBrand> searchBeanBrand = new SearchBean<CmBrand>();
		brandFilterBean.setSearchField("brandId");
		brandFilterBean.setSearchValue(brandId);
		searchBeanBrand.getDataFilterBeans().add(brandFilterBean);

		reqDTO.setBrandSearchBean(searchBeanBrand);

		resDTO = codificationSO.searchBrandObj(reqDTO);

		return resDTO.getBrandList().get(0);

	}

	public CmMeasurement searchMeasurementByMeasurementId(Long measurementId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmMeasurement> searchBeanMeasurement = new SearchBean<CmMeasurement>();
		measurementFilterBean.setSearchField("measurementId");
		measurementFilterBean.setSearchValue(measurementId);
		searchBeanMeasurement.getDataFilterBeans().add(measurementFilterBean);

		reqDTO.setMeasurementSearchBean(searchBeanMeasurement);

		resDTO = codificationSO.searchMeasurementObj(reqDTO);

		return resDTO.getMeasurementList().get(0);

	}

	public void fillExtensionLists() {

		log.info("**********fillExtensionLists************");

		if (this.getManagedBean().getSelectedUNSPSCObj().getUnspscCode() != null
				&& !this.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscCode().isEmpty()) {

			this.getManagedBean().setMeasurementCriteria(null);
			this.getManagedBean().setColorCriteria(null);
			this.getManagedBean().setBrandCriteria(null);
			this.getManagedBean().setTypeCriteria(null);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO reqDTO = new CodificationReqDTO();

			CodificationResDTO resDTO = unspscSO.searchColor(reqDTO);

			for (CmColor color : resDTO.getColorList()) {
				// log.info("color:"+color);
				// log.info("color:"+color.getColorId());
				if (!StringUtils.isBlank(color.getColorName())) {
					this.getManagedBean()
					.getColorCriteria()
					.add(new SelectItem(color.getColorId(), color
							.getColorName()));
				}
			}

			reqDTO.setUnspscCode(this.getManagedBean().getSelectedUNSPSCObj()
					.getUnspscCode());
			resDTO = unspscSO.searchCommodityObj(reqDTO);

			for (CmBrand brand : resDTO.getCommodityObj().getCmBrand()) {
				if (!StringUtils.isBlank(brand.getBrandName())) {
					this.getManagedBean()
					.getBrandCriteria()
					.add(new SelectItem(brand.getBrandId(), brand
							.getBrandName()));
				}
			}

			for (CmType type : resDTO.getCommodityObj().getCmType()) {
				if (!StringUtils.isBlank(type.getTypeName())) {
					this.getManagedBean()
					.getTypeCriteria()
					.add(new SelectItem(type.getTypeId(), type
							.getTypeName()));
				}
			}

			for (CmMeasurement measurement : resDTO.getCommodityObj()
					.getCmMeasurement()) {
				if (!StringUtils.isBlank(measurement.getMeasurementName())) {
					this.getManagedBean()
					.getMeasurementCriteria()
					.add(new SelectItem(measurement.getMeasurementId(),
							measurement.getMeasurementName()));
				}
			}
		}
	}


	public void requestNewColor() {

		if (this.getManagedBean().getRequestExtCode().isRequestColor()) {
			this.getManagedBean().setColorDisable(false);
		} else {
			this.getManagedBean().setColorDisable(true);
		}
	}

	public void requestNewType() {

		if (this.getManagedBean().getRequestExtCode().isRequestType()) {
			this.getManagedBean().setTypeDisable(false);
		} else {
			this.getManagedBean().setTypeDisable(true);
		}
	}

	public void requestNewBrand() {

		if (this.getManagedBean().getRequestExtCode().isRequestBrand()) {
			this.getManagedBean().setBrandDisable(false);
		} else {
			this.getManagedBean().setBrandDisable(true);
		}
	}

	public void requestNewMeasurement() {

		if (this.getManagedBean().getRequestExtCode().isRequestMeasurement()) {
			this.getManagedBean().setMeasurementDisable(false);
		} else {
			this.getManagedBean().setMeasurementDisable(true);
		}
	}

//	public void goToAddServiceConfirmPopUp(CmUnspsc unspscObj) {
//		// set commodity
//		System.out
//		.println("...............goToAddServiceConfirmPopUp..............");
//		log.info("..............unspscObj.getUnspscCode()="
//				+ unspscObj.getUnspscCode());
//		this.getManagedBean().setSelectedUNSPSCObj(unspscObj);
//
//	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	

	
//	public void preRenderCodificationView() {
//		log.info(".................preRenderCodificationView................");
//		// this.renewSesionObject();
//		resetAction();
//		FacesContext fc = FacesContext.getCurrentInstance();
//		fc.getApplication().getNavigationHandler()
//		.handleNavigation(fc, null, "codificationView");
//	}

	public String getItemTypeParameter() {
		return itemTypeParameter;
	}

	public void setItemTypeParameter(String itemTypeParameter) {
		log.info(".................setItemTypeParameter................");
		this.itemTypeParameter = itemTypeParameter;
	}

	
	public void addMoreRequest() {
		log.info(".................addMoreRequest...................");
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		
		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);
		
		this.getManagedBean().setEditMode(false);
		this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(resDTO.getCommodityObj().getCmBrand());
		this.getManagedBean().setMeasurementList(resDTO.getCommodityObj().getCmMeasurement());
		
		resDTO = codificationSO.searchColor(reqDTO);
		this.getManagedBean().setColorList(resDTO.getColorList());

		this.getManagedBean().setRequestExtCode(new CmRequestCode());
		this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		clearForm();
		this.getManagedBean().setTypeDisable(true);
		this.getManagedBean().setBrandDisable(true);
		this.getManagedBean().setMeasurementDisable(true);
		this.getManagedBean().setColorDisable(true);

	}
	
	
	
	public void addNewRequest() {
		log.info("...............addNewRequest...................");

		// ///check that there be at least one new request
		if (!this.getManagedBean().getRequestExtCode().isRequestType()
				&& !this.getManagedBean().getRequestExtCode().isRequestBrand()
				&& !this.getManagedBean().getRequestExtCode()
				.isRequestMeasurement()
				&& !this.getManagedBean().getRequestExtCode().isRequestColor()) {
			throw new EpException(MessageConst.ERROR_716010);

		}
		// //////////checking for redundancy
		if (this.getManagedBean().getTempTypeName()!= null) {
			checkingTypeRedundency(this.getManagedBean().getTempTypeName());
		}
		if (this.getManagedBean().getTempBrandName()!= null) {
			checkingBrandRedundency(this.getManagedBean().getTempBrandName());
		}
		if (this.getManagedBean().getTempMeasurementName()!= null) {
			checkingMeasurmentRedundency(this.getManagedBean().getTempMeasurementName());
		}
		if (this.getManagedBean().getTempColorName()!= null) {
			checkingColorRedundency(this.getManagedBean().getTempColorName());
		}

		// type
		if (this.getManagedBean().getTempTypeName()== null) {
			
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

			//if (this.getManagedBean().getRequestExtCode().isRequestType()) {
				
				System.out.println("set TypeName");
				
				this.getManagedBean()
				.getRequestExtCode()
				.setTypeName(this.getManagedBean().getTempTypeName());
//				this.getManagedBean()
//				.getRequestExtCode()
//				.setTypeId(
//						this.getManagedBean().getSelectedType()
//						.getTypeId());
//			}
		}
		// brand
		if (this.getManagedBean().getTempBrandName()== null) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.brand",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {

//			if (this.getManagedBean().getRequestExtCode().isRequestBrand()) {
				
				System.out.println("set BrandName");
				
				this.getManagedBean()
				.getRequestExtCode()
				.setBrandName(this.getManagedBean().getTempBrandName());
//				this.getManagedBean()
//				.getRequestExtCode()
//				.setBrandId(
//						this.getManagedBean().getSelectedBrand()
//						.getBrandId());
//			}
		}
		// color
		if (this.getManagedBean().getTempMeasurementName()== null) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.color",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			//if (this.getManagedBean().getRequestExtCode().isRequestColor()) {
				
				System.out.println("set Color");
				
				this.getManagedBean()
				.getRequestExtCode()
				.setColorName(this.getManagedBean().getTempColorName());
			//}
		}
		// measurement
		if (this.getManagedBean().getTempColorName()== null) {

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.measurement",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			//if (this.getManagedBean().getRequestExtCode()
			//		.isRequestMeasurement()) {
				
				System.out.println("set MeasurementName");
				
				
				this.getManagedBean()
				.getRequestExtCode()
				.setMeasurementName(
						this.getManagedBean().getTempMeasurementName());
			//}
		}

		if(this.getManagedBean().isEditMode()){
			this.getManagedBean().getRequestExtCodeList().set(this.getManagedBean().getEditModeIndex(), this.getManagedBean().getRequestExtCode());
		}else{	
			
			this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
             
			
			System.out.println("type " + this.getManagedBean().getRequestExtCode().getTypeName());
			System.out.println("brand " + this.getManagedBean().getRequestExtCode().getBrandName());
			System.out.println("measurement " + this.getManagedBean().getRequestExtCode().getMeasurementName());
			System.out.println("color " + this.getManagedBean().getRequestExtCode().getColorName());
			
			
			this.getManagedBean().getRequestExtCodeList().add(this.getManagedBean().getRequestExtCode());
			System.out.println("size " + this.getManagedBean().getRequestExtCodeList().size());
			
			this.getManagedBean().setDisableSelectBtn(true);
			
		}
		clearForm();

		RequestContext.getCurrentInstance().execute("extensionPopUp.hide();");
	}
	
	
	public void clearForm() {
		log.info("...............clearForm...................");
		this.getManagedBean().setSelectedType(null);
		this.getManagedBean().setSelectedBrand(null);
		this.getManagedBean().setSelectedColor(null);
		this.getManagedBean().setSelectedMeasurement(null);
	}
	
	
	
	
	public void resetSearch() {
		this.getManagedBean().setAutoCompleteUnspscCode(null);
		this.getManagedBean().setTitleFilterBean(null);
		resetAction();
		
	}
	
	
	public void initRefreshPageNoFlag(){
		
		if(this.getManagedBean().isRefreshPageNoFlag()){
			System.out.println("****************************");
			PagingDataModel paging = new ExtensionSearchItemDataModel();
			paging.setPageNo(0);
			
			this.getExtensionSearchItemDataModel().resetData();
			this.getManagedBean().setRefreshPageNoFlag(false);
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
	}
	
	
	public void clearPopup() {
		log.info("...............clearPopup...................");
		this.getManagedBean().setClassCode(null);
		this.getManagedBean().setClassTitle(null);

		this.getManagedBean().setFamilyCode(null);
		this.getManagedBean().setFamilyTitle(null);

		this.getManagedBean().setSegmentCode(null);
		this.getManagedBean().setSegmentTitle(null);
		
	}
	
	
	public void checkingTypeRedundency(String typeInput) {
		log.info(".....checking redundancy for typeInput=" + typeInput);
		this.getManagedBean().setTypeRedundancy(false);
		if (typeInput != null && !typeInput.equals("")) {
			for (CmType type : this.getManagedBean().getTypeList()) {
				if (!StringUtils.isBlank(type.getTypeName())) {
					if (type.getTypeName().toLowerCase()
							.equals(typeInput.toLowerCase())) {
						this.getManagedBean().setTypeRedundancy(true);						
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.type",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingBrandRedundency(String brandInput) {
		log.info(".....checking redundancy for brandInput=" + brandInput);
		this.getManagedBean().setBarndRedundancy(false);
		if (brandInput != null && !brandInput.equals("")) {
			for (CmBrand brand : this.getManagedBean().getBrandList()) {
				if (!StringUtils.isBlank(brand.getBrandName())) {
					if (brand.getBrandName().toLowerCase()
							.equals(brandInput.toLowerCase())) {

						this.getManagedBean().setBarndRedundancy(true);
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.brand",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingMeasurmentRedundency(String MeasurmentInput) {
		log.info(".....checking redundancy for MeasurmentInput="
				+ MeasurmentInput);
		this.getManagedBean().setMeasurRedundancy(false);
		if (MeasurmentInput != null && !MeasurmentInput.equals("")) {
			for (CmMeasurement measur : this.getManagedBean()
					.getMeasurementList()) {

				if (!StringUtils.isBlank(measur.getMeasurementName())) {
					if (measur.getMeasurementName().toLowerCase()
							.equals(MeasurmentInput.toLowerCase())) {
						this.getManagedBean().setMeasurRedundancy(true);
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.measurement",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}


	public void checkingColorRedundency(String colorInput) {
		log.info(".....checking redundancy for colorInput=" + colorInput);
		log.info("colorList " + this.getManagedBean().getColorList());
		this.getManagedBean().setColorRedundancy(false);
		if (colorInput != null && !colorInput.equals("")) {
			for (CmColor color : this.getManagedBean().getColorList()) {
				if (!StringUtils.isBlank(color.getColorName())) {
					if (color.getColorName().toLowerCase()
							.equals(colorInput.toLowerCase())) {
						this.getManagedBean().setColorRedundancy(true);
						log.info("this.getManagedBean().isColorRedundancy()="
								+ this.getManagedBean().isColorRedundancy());
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.color",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });

					}
				}
			}
			log.info("end no exception");
		}
	}
	
	


}

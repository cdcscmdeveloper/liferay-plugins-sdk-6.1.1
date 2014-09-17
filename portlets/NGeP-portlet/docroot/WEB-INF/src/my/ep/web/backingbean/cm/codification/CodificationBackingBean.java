package my.ep.web.backingbean.cm.codification;

import java.util.ArrayList;
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
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IPendingCodifiedItemSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.PendingCodifiedItemReqDTO;
import my.ep.dto.cm.codification.PendingCodifiedItemResDTO;
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sc.requestitem.RequestItemResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmPendingCodifiedItem;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPendingCodifiedItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.cm.CmConst;
import my.ep.web.managedbean.cm.codification.CodificationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.ExtensionCodeVO;
import my.ep.web.vo.NameNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "codificationBackingBean")
@ViewScoped
public class CodificationBackingBean extends
		BaseBackingBean<CodificationManagedBean> {

	public static final String NAV_TO_CODIFICATION_VIEW = "codificationView";
	public static final String NAV_TO_EXTENSION_SEARCH = "extensionSearch";
	public static final String NAV_TO_UNSPSC_REQUEST_FORM = "unspscRequestForm";
	public static final String NAV_TO_UNSPSC_REQUEST_FORM_II = "unspscRequestFormII";
	public static final String NAV_TO_EXTENSION_REQUEST_FORM = "extensionCodeRequestForm";
	public static final String NAV_TO_UNSPSC_SINGLE_UPLOAD = "unspscSingleUpload";
	public static final String NAV_TO_EXTENSION_CODE_REVIEW = "extensionCodeReview";
	public static final String NAV_TO_UNSPSC_BULK_UPLOAD = "unspscBulkUpload";
	public static final String NAV_TO_EDIT_EXTENSION = "editExtension";
	public static final String NAV_TO_DELETE_EXTENSION = "deleteExtension";
	public static final String NAV_TO_CONTRACTED_ITEM = "contractedItem";
	public static final String NAV_TO_ASSIGN_PRODUCT_CODE = "assignProductCode";
	public static final String NO_69 = "69";

	private static Logger log = Logger.getLogger(CodificationBackingBean.class);

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

	public CodificationBackingBean() {
		this.getManagedBean().setContractedItem(null);
		CmUnspsc unspsc = new CmUnspsc();
		unspsc.setUnspscTitle("item");
		this.getManagedBean().getContractedItem().add(unspsc);
		// set with and height of popUp

		/*
		 * ICodificationSO codificationSO = (ICodificationSO)
		 * BusinessDelegate.getInstance
		 * ().getServiceObject("cm",ICodificationSO.class); CodificationReqDTO
		 * reqDTO = new CodificationReqDTO();
		 * 
		 * CodificationResDTO resDTO = codificationSO.searchSegment(reqDTO);
		 * 
		 * if(resDTO.isSuccessful()){
		 * 
		 * rootNodes = new DefaultTreeNode("rootNodes", null);
		 * 
		 * for(CmUnspsc segment : resDTO.getSegmentList()){ SegmentVO segmentObj
		 * = new SegmentVO(); segmentObj.setName(segment.getUnspscTitle());
		 * segmentObj.setParentCode(segment.getUnspscCode());
		 * segmentObj.setUnspscLevel(0);
		 * 
		 * TreeNode segmentNode = new DefaultTreeNode("segment", segmentObj,
		 * rootNodes);
		 * 
		 * //reqDTO.setParentCode(segment.getUnspscCode()); //resDTO =
		 * codificationSO.searchFamily(reqDTO);
		 * 
		 * for(CmUnspsc catFamily : segment.getCmUnspscs()){
		 * 
		 * FamiliesVO familiesVO = new FamiliesVO();
		 * familiesVO.setName(catFamily.getUnspscTitle());
		 * familiesVO.setParentCode(catFamily.getUnspscCode());
		 * familiesVO.setUnspscLevel(1); TreeNode families = new
		 * DefaultTreeNode("families",familiesVO, segmentNode); } } }
		 */

	}

	public String resetAction() {
		System.out.println("In reset action...................");
		titleFilterBean = null;
		codeFilterBean = null;

		titleFilterBean = new SearchFilterBean(
				SearchOperator.OPERATOR_LIKE.value());
		codeFilterBean = new SearchFilterBean(
				SearchOperator.OPERATOR_LIKE.value());
		this.setSearchDataModel(new SearchPagingDataModel());
		this.getManagedBean().setAutoCompleteUnspscCode(null);
		System.out.println("after reset.........................");
		return null;
	}

	private class ExtensionSearchItemDataModel extends
			PagingDataModel<Object[]> {

		public ExtensionSearchItemDataModel() {
			super("itemTableID");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {

			System.out
					.println("........search extension combination.................");
			System.out.println("Unspsc Code:"
					+ getManagedBean().getSelectedUNSPSCObj().getUnspscCode());

			ICodificationSO codificationSO = null;
			CodificationResDTO resDTO = null;
			try {
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm",
								ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setExtSearchBean(searchBean);

				reqDTO.setUnspscCode(getManagedBean().getSelectedUNSPSCObj()
						.getUnspscCode());

				if (StringUtils.isNotBlank((String) colorFilterBean
						.getSearchValue())) {
					reqDTO.setColorId(Long.valueOf(colorFilterBean
							.getSearchValue().toString()));
				}

				System.out.println("typeFilterBean.getSearchValue()"
						+ typeFilterBean.getSearchValue());
				if (StringUtils.isNotBlank((String) typeFilterBean
						.getSearchValue())) {
					reqDTO.setTypeId(Long.valueOf(typeFilterBean
							.getSearchValue().toString()));

				}

				if (StringUtils.isNotBlank((String) brandFilterBean
						.getSearchValue())) {
					reqDTO.setBrandId(Long.valueOf(brandFilterBean
							.getSearchValue().toString()));

				}

				if (StringUtils.isNotBlank((String) measurementFilterBean
						.getSearchValue())) {
					reqDTO.setMeasurementId(Long.valueOf(measurementFilterBean
							.getSearchValue().toString()));
				}

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

	public void extensionCodeSearch() {

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
					System.out.println("list.get(i): " + list.get(i));
					System.out
							.println("list.get(i): " + list.get(i).getClass());
					Object[] objects = (Object[]) list.get(i);

					for (int j = 0; j < objects.length; j++) {
						System.out.println("objects[0]: " + objects[j]);
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
					System.out.println("*********codeFilterBean***********");
					codeFilterBean.setSearchField("unspscCode");
					codeFilterBean.setSearchValue(getManagedBean()
							.getAutoCompleteUnspscCode().getUnspscCode());
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String) titleFilterBean
						.getSearchValue())) {
					System.out.println("*********titleFilterBean***********");
					titleFilterBean.setSearchField("unspscTitle");
					titleFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(titleFilterBean);
				}
				FacesScopes fs = new FacesScopes();
				String itemType = fs.getParameter("itemType");

				log.info("............itemType=" + itemType);
				if (itemType != null) {
					if (itemType.equals("S") || itemType.equals("2")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_GREATER_EQUAL
												.value(), "70000000"));
					} else if (itemType.equals("P") || itemType.equals("1")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_LESS.value(),
										"70000000"));
					}
				}

				/*
				 * levelFilterBean.setSearchField("unspscLevel");
				 * levelFilterBean.setSearchValue(3);
				 * searchBean.getDataFilterBeans().add(levelFilterBean);
				 */

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				System.out.println("*********after***********");
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

	public void unspscSearch() {
		String unspscCode = getManagedBean().getAutoCompleteUnspscCode()
				.getUnspscCode();
		String unspscTitle = titleFilterBean.getSearchValue().toString();

		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>titleFilterBean="
				+ titleFilterBean.getSearchValue());

		if ((unspscCode == null && (unspscTitle == null || unspscTitle
				.isEmpty()))
				|| ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle
						.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		if (unspscCode != null && !unspscCode.isEmpty()
				&& unspscCode.length() != 8) {
			throw new EpException(MessageConst.ERROR_715026);
		}

		searchDataModel.reloadData();
	}

	public void itemSearch() {
		searchItemDataModel.reloadData();
	}

	public void unspscSearch2() {

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>unspscSearch");

		ICodificationSO codificationSO = null;
		try {
			codificationSO = (ICodificationSO) BusinessDelegate.getInstance()
					.getServiceObject("cm", ICodificationSO.class);

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
			for (CmUnspsc unspsc : resDTO.getUnspscList()) {
				System.out.println("unspsc:" + unspsc.getUnspscCode());
				// for(CmBrand brand:unspsc.getCmBrand()){
				// System.out.println("brand:"+brand.getBrandCode());
				// }
			}

			this.getManagedBean().setUnspscList(resDTO.getUnspscList());
		} finally {
			codificationSO = null;
		}
	}

	public String codificationSearch() {

		return NAV_TO_CODIFICATION_VIEW;
	}

	public String goToExtensionSearch(CmUnspsc unspscObj) {
		log.debug("**********goToExtensionSearch************");
		
		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(unspscObj.getUnspscCode());
		CodificationResDTO resDTO = unspscSO.retrieveUNSPSCByCommodityCode(reqDTO);
		
		if(resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();
			
//			this.getManagedBean().getSelectedUNSPSCObj().setUnspscCode(commodity.getUnspscCode());
//			this.getManagedBean().getSelectedUNSPSCObj().setUnspscTitle(commodity.getUnspscTitle());
			this.getManagedBean().setSelectedUNSPSCObj(commodity);
			
			this.getManagedBean().setClassCode(commodity.getCmUnspsc().getUnspscCode());
			this.getManagedBean().setClassTitle(commodity.getCmUnspsc().getUnspscTitle());
			
			this.getManagedBean().setFamilyCode(commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setFamilyTitle(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
			
			this.getManagedBean().setSegmentCode(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setSegmentTitle(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitle());
		}
		
/*		String classCode = getParentCode(new Long(unspscObj.getUnspscCode()));

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
				.setUnspscTitle(unspscObj.getUnspscTitle());*/

		resDTO = unspscSO.searchColor(reqDTO);

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

		for (CmBrand brand : unspscObj.getCmBrand()) {
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
						.getBrandCriteria()
						.add(new SelectItem(brand.getBrandId(), brand
								.getBrandName()));
			}
		}

		for (CmType type : unspscObj.getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
						.getTypeCriteria()
						.add(new SelectItem(type.getTypeId(), type
								.getTypeName()));
			}
		}

		for (CmMeasurement measurement : unspscObj.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
						.getMeasurementCriteria()
						.add(new SelectItem(measurement.getMeasurementId(),
								measurement.getMeasurementName()));
			}
		}

		return NAV_TO_EXTENSION_SEARCH + "?faces-redirect=true";
	}

	public String goToAssignUnspsc(VCmPendingCodifiedItem item) {
		System.out.println("**********goToAssignUnspsc************");

//		IRequestItemSO requestItemSO = null;

//		requestItemSO = (IRequestItemSO) BusinessDelegate.getInstance()
//				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
//						IRequestItemSO.class);
//		RequestItemReqDTO reqDTO = new RequestItemReqDTO();
//
//		reqDTO.setDocType(item.getDocType());
//		reqDTO.setDocId(item.getDocId());
//
//		RequestItemResDTO resDTO = requestItemSO
//				.retrieveItemListByDocIdNType(reqDTO);
		IPendingCodifiedItemSO codifiedSO = (IPendingCodifiedItemSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
						IPendingCodifiedItemSO.class);
		PendingCodifiedItemReqDTO reqDTO = new PendingCodifiedItemReqDTO();
		reqDTO.setDocType(item.getDocType());
		reqDTO.setDocID(item.getDocId());
		PendingCodifiedItemResDTO resDTO = codifiedSO.retrieveScRequestItemListByDocIdNType(reqDTO);

		// set the requestItemList of the pendingCodifiedItem
		this.getManagedBean().setSelectedVPendingRequestItemList(resDTO.getRequestItemList());
		this.getManagedBean().setSelectedVPendingCodifiedItem(item);
		// this.getManagedBean().setItemCode(null);
		return NAV_TO_ASSIGN_PRODUCT_CODE;
	}

	public String requestUNSPSCcode() {

		return NAV_TO_UNSPSC_REQUEST_FORM;
	}

	public String singleUpload() {

		return NAV_TO_UNSPSC_SINGLE_UPLOAD;
	}

	public String requestExtensioncode() {

		return NAV_TO_EXTENSION_REQUEST_FORM;

	}

	public String extensionReview() {

		return NAV_TO_EXTENSION_CODE_REVIEW;
	}

	public String bulkUpload() {

		return NAV_TO_UNSPSC_BULK_UPLOAD + "?faces-redirect=true";

	}

	public String editExtension(int index) {

		this.getManagedBean().setEditItem(
				this.getSearchItemDataModel().getList().get(index));

		return NAV_TO_EDIT_EXTENSION;
	}

	public String recommendItemCode(Object[] objects) {

		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		// System.out.println("index:"+obj);
		log.info("length:" + objects);

		ExtensionCodeVO extCodeVO = new ExtensionCodeVO();
		// Object[] objects =
		// (Object[])this.getManagedBean().getSelectedObject()[index];

		extCodeVO.setTypeId(Long.valueOf(String.valueOf(objects[0])));
		extCodeVO.setBrandId(Long.valueOf(String.valueOf(objects[1])));
		extCodeVO.setMeasurementId(Long.valueOf(String.valueOf(objects[2])));
		extCodeVO.setColorId(Long.valueOf(String.valueOf(objects[3])));

		log.info("******type NAme******" + String.valueOf(objects[4]));
		extCodeVO.setTypeName(String.valueOf(objects[4]));
		extCodeVO.setBrandName(String.valueOf(objects[5]));
		extCodeVO.setMeasurementName(String.valueOf(objects[6]));
		extCodeVO.setColorName(String.valueOf(objects[7]));

		CmColor colorObj = searchColorObjByColorId(extCodeVO.getColorId());
		String colorCode = colorObj.getColorCode();
		String colorName = colorObj.getColorName();

		CmType typeObj = searchTypeOjbyTypeId(extCodeVO.getTypeId());
		String typeCode = typeObj.getTypeCode();
		String typeName = typeObj.getTypeName();

		CmBrand brandObj = searchBrandObjByBrandId(extCodeVO.getBrandId());
		String brandCode = brandObj.getBrandCode();
		String brandName = brandObj.getBrandName();

		CmMeasurement meaObj = searchMeasurementByMeasurementId(extCodeVO
				.getMeasurementId());
		String measurementCode = meaObj.getMeasurementCode();
		String measurementName = meaObj.getMeasurementName();

		CmRecommendCode recommend = new CmRecommendCode();

		String recommendItemCode = this.getManagedBean().getSelectedUNSPSCObj()
				.getUnspscCode()
				+ typeCode + brandCode + measurementCode + colorCode;
		String recommendItemTitle = this.getManagedBean()
				.getSelectedUNSPSCObj().getUnspscTitle()
				+ " "
				+ typeName
				+ " "
				+ brandName
				+ " "
				+ measurementName
				+ " " + colorName;

		recommend.setUnspscCode(recommendItemCode);
		recommend.setUnspscTitle(recommendItemTitle);
		bb.getManagedBean().getRequestCode().getCmRecommendCodes()
				.add(recommend);

		FacesScopes fs = new FacesScopes();
		log.info("CM_ADMIN:" + fs.hasRoleUser("CM_ADMIN"));
		log.info("CD_OFFICER:" + fs.hasRoleUser("CD_OFFICER"));

		// if (fs.hasRoleUser("CM_ADMIN")) {
		// return "reviewUnspscCode?faces-redirect=true";
		// } else if (fs.hasRoleUser("CD_OFFICER")) {
		// return "reviewBeforeCreateTempCode?faces-redirect=true";
		// }

		if (this.getManagedBean().getCurrentTaskName()
				.equals(my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE)) {
			return "reviewBeforeCreateTempCode?faces-redirect=true";
		} else {
			return "reviewUnspscCode?faces-redirect=true";
		}

		// return "";

	}

	public String deleteExtension(int index) {

		this.getManagedBean().setDeleteItem(
				this.getSearchItemDataModel().getList().get(index));

		return NAV_TO_DELETE_EXTENSION;
	}

	public void searchVPendingCodifiedItems() {
		this.getManagedBean().getvPendingCodifiedItemSearchDataModel()
				.reloadData();
	}

	public String AssignContractedItem() {
		searchVPendingCodifiedItems();
		return NAV_TO_CONTRACTED_ITEM;
	}

	public String backToContractedItem() {

		return AssignContractedItem();
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

	public String assignProductCode(ScRequestItem requestItem) {
		System.out.println(">>>>>>>>>requestItem= " + requestItem);
		this.getManagedBean().setSelectedRequestItem(requestItem);

		clearContractedCodePopUp();
		return "searchUnspscToAssign.xhtml";

	}

	public void clearContractedCodePopUp() {
		resetAction();
		// this.getManagedBean().setSelectedLovUnspscSegment(null);
		// this.getManagedBean().setSelectedLovUnspscFamily(null);
		// this.getManagedBean().setSelectedLovUnspscClass(null);
		// this.getManagedBean().setSelectedUNSPSCObj(null);
		this.getManagedBean().setRequestExtCode(null);
		// this.getManagedBean().setTypeDisable(true);
		// this.getManagedBean().setBrandDisable(true);
		// this.getManagedBean().setMeasurementDisable(true);
		// this.getManagedBean().setColorDisable(true);
	}

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
			log.info("code:" + brand.getBrandCode());
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

	public String insertContractedItemCodeForService(CmUnspsc commodity) {
		this.getManagedBean().setSelectedUNSPSCObj(commodity);
		return insertContractedItemCode();
	}

	public String insertContractedItemCode() {
		System.out.println("**********insertContractedItemCode************");
		FacesContext facesContext = null;

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		IRequestItemSO ReqItemSO = (IRequestItemSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IRequestItemSO.class);
		IPendingCodifiedItemSO codifiedItemSO = (IPendingCodifiedItemSO) BusinessDelegate
				.getInstance().getServiceObject("cm",
						IPendingCodifiedItemSO.class);

		this.getManagedBean().setExtensionVoList(null);

		ExtensionCodeVO extCodeVO = null;

		if (isSelectedRequestItemProduct()) {
			Object[] objects = (Object[]) this.getManagedBean()
					.getSelectedExtenstionForItem();

			if (objects != null && objects.length > 0) {
				extCodeVO = new ExtensionCodeVO();
				extCodeVO.setTypeId(Long.valueOf(String.valueOf(objects[0])));
				extCodeVO.setBrandId(Long.valueOf(String.valueOf(objects[1])));
				extCodeVO.setMeasurementId(Long.valueOf(String
						.valueOf(objects[2])));
				extCodeVO.setColorId(Long.valueOf(String.valueOf(objects[3])));

				extCodeVO.setTypeName(String.valueOf(objects[4]));
				extCodeVO.setBrandName(String.valueOf(objects[5]));
				extCodeVO.setMeasurementName(String.valueOf(objects[6]));
				extCodeVO.setColorName(String.valueOf(objects[7]));
			}
		} else {
			extCodeVO = new ExtensionCodeVO();
			CmUnspsc selectedCommodity = this.getManagedBean()
					.getSelectedUNSPSCObj();
			log.info("..................selectedCommodity=" + selectedCommodity);

			extCodeVO.setTypeId(selectedCommodity.getCmType().get(0)
					.getTypeId());
			extCodeVO.setBrandId(selectedCommodity.getCmBrand().get(0)
					.getBrandId());
			extCodeVO.setMeasurementId(selectedCommodity.getCmMeasurement()
					.get(0).getMeasurementId());
			extCodeVO.setColorId(1L);// the 00 color code's id is always 1

			extCodeVO.setTypeName(selectedCommodity.getCmType().get(0)
					.getTypeName());// should be NA
			extCodeVO.setBrandName(selectedCommodity.getCmBrand().get(0)
					.getBrandName());// should be NA
			extCodeVO.setMeasurementName(selectedCommodity.getCmMeasurement()
					.get(0).getMeasurementName());// should be NA
			extCodeVO.setColorName("N/A");
		}
		if (extCodeVO != null) {
			this.getManagedBean().getExtensionVoList().add(extCodeVO);
		}
		if (this.getManagedBean().getExtensionVoList() != null
				&& this.getManagedBean().getExtensionVoList().size() > 0) {

			ExtensionCodeVO obj = this.getManagedBean().getExtensionVoList()
					.get(0);

			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setBrandId(obj.getBrandId());
			codeReqDTO.setMeasurementId(obj.getMeasurementId());
			codeReqDTO.setTypeId(obj.getTypeId());
			codeReqDTO.setColorId(obj.getColorId());
			codeReqDTO.setUnspscId(this.getManagedBean().getSelectedUNSPSCObj()
					.getUnspscId());

			CodificationResDTO codeResDTO = codificationSO
					.searchItem(codeReqDTO);
			CmItem item = null;
			if (codeResDTO.getItemList() != null
					&& codeResDTO.getItemList().size() > 0) {

				item = codeResDTO.getItemList().get(0);
				System.out.println("************item found, item.getItemId()="
						+ item.getItemId());
			} else {
				System.out
						.println("************create new CmItem*******************");
				CmType typeObj = this.searchTypeOjbyTypeId(obj.getTypeId());

				CmBrand brandObj = this.searchBrandObjByBrandId(obj
						.getBrandId());

				CmMeasurement meaObj = this
						.searchMeasurementByMeasurementId(obj
								.getMeasurementId());

				CmColor colorObj = this.searchColorObjByColorId(obj
						.getColorId());

				codeReqDTO = new CodificationReqDTO();
				codeReqDTO.setUnspscCode(this.getManagedBean()
						.getSelectedUNSPSCObj().getUnspscCode());
				codeResDTO = codificationSO.searchCommodityObj(codeReqDTO);

				CmItem newItem = new CmItem();

				newItem.setCmUnspsc(codeResDTO.getCommodityObj());
				newItem.setCmType(typeObj);
				newItem.setCmBrand(brandObj);
				newItem.setCmMeasurement(meaObj);
				newItem.setCmColor(colorObj);
				newItem.setItemName(this.getManagedBean()
						.getSelectedUNSPSCObj().getUnspscTitle()
						+ " "
						+ (typeObj.getTypeName().equals("N/A") ? "" : typeObj
								.getTypeName())
						+ " "
						+ (brandObj.getBrandName().equals("N/A") ? ""
								: brandObj.getBrandName())
						+ " "
						+ (meaObj.getMeasurementName().equals("N/A") ? ""
								: meaObj.getMeasurementName())
						+ " "
						+ (colorObj.getColorName().equals("N/A") ? ""
								: colorObj.getColorName()));

				newItem.setExtensionCode(this.getManagedBean()
						.getSelectedUNSPSCObj().getUnspscCode()
						+ typeObj.getTypeCode()
						+ brandObj.getBrandCode()
						+ meaObj.getMeasurementCode() + colorObj.getColorCode());

				System.out.println("item Name:" + newItem.getItemName());
				System.out.println("item extension Code:"
						+ newItem.getExtensionCode());
				codeReqDTO.setCmItem(newItem);
				codeResDTO = codificationSO.saveNewItem(codeReqDTO);
				item = codeResDTO.getCmItem();

			}

			this.getManagedBean().getSelectedRequestItem().setCmItem(item);

			// check RequestItemList if all ItemCodes were assigned then
			// update
			// status of PendingCodifiedItem to Completed and update its
			// RequestItemList
			boolean allCodeItemsAssigned = true;
			for (ScRequestItem requestItem : this.getManagedBean()
					.getSelectedVPendingRequestItemList()) {
				if (requestItem.getCmItem() != null) {
					if (requestItem.getCmItem().getExtensionCode() == null
							|| requestItem.getCmItem().getExtensionCode()
									.isEmpty()) {
						allCodeItemsAssigned = false;
						break;
					}
				} else {// requestItem.getCmItem()==null
					allCodeItemsAssigned = false;
					break;
				}
			}

			RequestItemResDTO requestItemResDTO = null;
			if (allCodeItemsAssigned) {
				// update status of PendingCodifiedItem to Completed and
				// update
				// its RequestItemList
				RequestItemReqDTO requestItemReqDTO = new RequestItemReqDTO();
				requestItemReqDTO.setRequestItems(this.getManagedBean()
						.getSelectedVPendingRequestItemList());

				requestItemResDTO = ReqItemSO
						.saveRequestItems(requestItemReqDTO);

				// update status of pendingCodifiedItem
				PendingCodifiedItemReqDTO codifiedItemReqDTO = new PendingCodifiedItemReqDTO();
				codifiedItemReqDTO.setPendingCodifiedItemId(this
						.getManagedBean().getSelectedVPendingCodifiedItem()
						.getPendingCodifiedItemId());
				log.info("........getPendingCodifiedItemId()"
						+ this.getManagedBean()
								.getSelectedVPendingCodifiedItem()
								.getPendingCodifiedItemId());
				PendingCodifiedItemResDTO codifiedItemResDTO = codifiedItemSO
						.retrievePendingCodifiedItemById(codifiedItemReqDTO);
				CmPendingCodifiedItem codifiedItem = codifiedItemResDTO
						.getPendingCodifiedItem();
				codifiedItem.setStatusId(CmConst.COMPLETED); // status set
																// to
																// Completed

				codifiedItemReqDTO.setPendingCodifiedItem(codifiedItem);
				codifiedItemSO.updatePendingCodifiedItem(codifiedItemReqDTO);

				facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_710009,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************RequestItem List and status have been updated*****************");

			} else {
				facesContext = FacesContext.getCurrentInstance();
				facesContext
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_WARN,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_710010,
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}
			return NAV_TO_ASSIGN_PRODUCT_CODE;
		} else if (isSelectedRequestItemProduct()) {// for service don't show
													// this message
			facesContext = FacesContext.getCurrentInstance();
			facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_WARN,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_710011,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return "";
		} else {
			throw new EpException(MessageConst.EP_MAJ_00001,
					new String[] { ResourceBundleUtils.getMessage(null,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });

		}

	}

	public String createExtCode() {

		return "extensionCodeSingleCreation";
	}

	public String backToPrevious() {

		return "extensionSearch";
	}

	public String saveEditedCode() {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmBrand> searchBeanBrand = new SearchBean<CmBrand>();
		brandFilterBean.setSearchField("brandCode");
		brandFilterBean.setSearchValue(this.getManagedBean().getEditItem()
				.getCmBrand().getBrandCode());
		searchBeanBrand.getDataFilterBeans().add(brandFilterBean);

		reqDTO.setBrandSearchBean(searchBeanBrand);

		resDTO = codificationSO.searchBrandObj(reqDTO);

		if (!this.getManagedBean().getEditBrandName().isEmpty()) {
			resDTO.getBrandList().get(0)
					.setBrandName(this.getManagedBean().getEditBrandName());
		}

		reqDTO.setCmBrand(resDTO.getBrandList().get(0));

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorCode");
		colorFilterBean.setSearchValue(this.getManagedBean().getEditItem()
				.getCmColor().getColorCode());
		searchBeanColor.getDataFilterBeans().add(colorFilterBean);

		reqDTO.setColorSearchBean(searchBeanColor);

		resDTO = codificationSO.searchColorObj(reqDTO);

		if (!this.getManagedBean().getEditColorName().isEmpty()) {
			resDTO.getColorList().get(0)
					.setColorName(this.getManagedBean().getEditColorName());
		}

		reqDTO.setCmColor(resDTO.getColorList().get(0));

		ISearchBean<CmType> searchBeanType = new SearchBean<CmType>();
		typeFilterBean.setSearchField("typeCode");
		typeFilterBean.setSearchValue(this.getManagedBean().getEditItem()
				.getCmType().getTypeCode());
		searchBeanType.getDataFilterBeans().add(typeFilterBean);

		reqDTO.setTypeSearchBean(searchBeanType);

		resDTO = codificationSO.searchTypeObj(reqDTO);
		if (!this.getManagedBean().getEditTypeName().isEmpty()) {
			resDTO.getTypeList().get(0)
					.setTypeName(this.getManagedBean().getEditTypeName());
		}

		reqDTO.setCmType(resDTO.getTypeList().get(0));

		ISearchBean<CmMeasurement> searchBeanMeasurement = new SearchBean<CmMeasurement>();
		measurementFilterBean.setSearchField("measurementCode");
		measurementFilterBean.setSearchValue(this.getManagedBean()
				.getEditItem().getCmMeasurement().getMeasurementCode());
		searchBeanMeasurement.getDataFilterBeans().add(measurementFilterBean);

		reqDTO.setMeasurementSearchBean(searchBeanMeasurement);

		resDTO = codificationSO.searchMeasurementObj(reqDTO);

		if (!this.getManagedBean().getEditMeasurementName().isEmpty()) {
			resDTO.getMeasurementList()
					.get(0)
					.setMeasurementName(
							this.getManagedBean().getEditMeasurementName());
		}

		reqDTO.setCmMeasurement(resDTO.getMeasurementList().get(0));

		resDTO = codificationSO.editExtensionCode(reqDTO);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(
								MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "extensionSearch";

	}

	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}

	public String backToPrev() {

		return "codificationView";
	}

	public String backToVerifyUnspsc() {

		return "verifyUnspscSearch?faces-redirect=true";
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

		this.getManagedBean().setClassCode("");
		this.getManagedBean().setClassTitle("");

		this.getManagedBean().setFamilyCode("");
		this.getManagedBean().setFamilyTitle("");

		this.getManagedBean().setSegmentCode("");
		this.getManagedBean().setSegmentTitle("");

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		Integer level = checkUnspscLevel(new Long(obj.getUnspscCode()));

		System.out.println("level:" + level);

		if (level == 0) {

			System.out.println("***********0000****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO familyDTO = unspscSO.searchFamily(reqDTO);

			this.getManagedBean().setUnspscPopUpList(familyDTO.getFamilyList());

			this.getManagedBean().setSegmentCode(obj.getUnspscCode());
			this.getManagedBean().setSegmentTitle(obj.getUnspscTitle());

		} else if (level == 1) {
			System.out.println("***********11111****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO ClassDTO = unspscSO.searchClass(reqDTO);

			this.getManagedBean().setUnspscPopUpList(ClassDTO.getClassList());

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
			System.out.println("***********222****************"
					+ obj.getUnspscCode());
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO CommodityDTO = unspscSO.searchCommodity(reqDTO);

			this.getManagedBean().setUnspscPopUpList(
					CommodityDTO.getCommodityList());

			this.getManagedBean().setClassCode(obj.getUnspscCode());
			this.getManagedBean().setClassTitle(obj.getUnspscTitle());

			String familyCode = getParentCode(new Long(obj.getUnspscCode()));
			System.out.println("familyCode:" + familyCode);
			reqDTO.setUnspscCode(familyCode);
			CodificationResDTO familyDTO = unspscSO.searchUnspscObj(reqDTO);

			this.getManagedBean().setFamilyCode(
					familyDTO.getUnspscObj().getUnspscCode());
			this.getManagedBean().setFamilyTitle(
					familyDTO.getUnspscObj().getUnspscTitle());

			String segmentCode = getParentCode(new Long(familyDTO
					.getUnspscObj().getUnspscCode()));
			System.out.println("segmentCode:" + segmentCode);
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

	public String backToReviewUnspsc() {
		FacesScopes fs = new FacesScopes();

		log.info("CD_ADMIN" + fs.hasRoleUser("CM_ADMIN"));
		log.info("Current Task Name: " + fs.getParameter("currentTaskName"));

		String currentTaskName = fs.getParameter("currentTaskName");

		if (currentTaskName != null
				&& currentTaskName
						.equals(my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE)) {
			return "reviewBeforeCreateTempCode?faces-redirect=true";
		} else {
			return "reviewUnspscCode?faces-redirect=true";
		}

		// return fs.hasRoleUser("CM_ADMIN") == true ?
		// "reviewUnspscCode?faces-redirect=true"
		// : "reviewBeforeCreateTempCode?faces-redirect=true";
	}

	public String backToPendingCodified() {
		return "assignProductCode?faces-redirect=true";
	}

	public String backToSearchUnspscToAssign() {
		return "searchUnspscToAssign";
	}

	public String backToExtensionSearch() {
		return "searchExtensionToAssignItem";
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
		System.out.println("tr.getUnspscLevel()" + tr.getUnspscLevel());
		if (tr.getUnspscLevel() == 1) {
			System.out.println("1111111111111111");
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
			System.out.println("2222222222222222222");
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

		System.out.println("query:::::::::::::::::::::::::::::: " + query);

		// FacesContext facesContext = FacesContext.getCurrentInstance();
		//
		// Char firstLetter = query.charAt(0);
		// if ((Integer.parseInt((firstLetter))) > 7) ||
		// !query.startsWith("76")) {
		// facesContext.addMessage(
		// null,
		// new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
		// MessageUtils.getMessage(MessageConst.ERROR_715004,
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes().getUserLocale())));
		// return "";
		// }

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
					unspscCodes.add(unspsc.getUnspscCode());
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
		log.info("fs.getParameter(\"currentTaskName\")"
				+ fs.getParameter("currentTaskName"));
		this.getManagedBean().setCurrentTaskName(
				fs.getParameter("currentTaskName"));

		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

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

		for (CmBrand brand : unspscObj.getCmBrand()) {
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
						.getBrandCriteria()
						.add(new SelectItem(brand.getBrandId(), brand
								.getBrandName()));
			}
		}

		for (CmType type : unspscObj.getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
						.getTypeCriteria()
						.add(new SelectItem(type.getTypeId(), type
								.getTypeName()));
			}
		}

		for (CmMeasurement measurement : unspscObj.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
						.getMeasurementCriteria()
						.add(new SelectItem(measurement.getMeasurementId(),
								measurement.getMeasurementName()));
			}
		}

		return "verifyExtensionSearch?faces-redirect=true";
	}

	public String returnToRecommendService(CmUnspsc unspscObj) {
		System.out.println("**********returnToRecommendService************");
		UnspscCodeBackingBean ubb = new UnspscCodeBackingBean();
		CmRecommendCode recommend = new CmRecommendCode();
		recommend.setUnspscCode(unspscObj.getUnspscCode() + "0000000000");
		recommend.setUnspscTitle(unspscObj.getUnspscTitle());
		ubb.getManagedBean().getRequestCode().getCmRecommendCodes()
				.add(recommend);

		FacesScopes fs = new FacesScopes();
		log.info("CM_ADMIN:" + fs.hasRoleUser("CM_ADMIN"));
		log.info("CD_OFFICER:" + fs.hasRoleUser("CD_OFFICER"));

		String currentTaskName = fs.getParameter("currentTaskName");

		// if (fs.hasRoleUser("CM_ADMIN")) {
		// return "reviewUnspscCode?faces-redirect=true";
		// } else if (fs.hasRoleUser("CD_OFFICER")) {
		// return "reviewBeforeCreateTempCode?faces-redirect=true";
		// }

		if (currentTaskName != null
				&& currentTaskName
						.equalsIgnoreCase(my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE)) {
			return "reviewBeforeCreateTempCode?faces-redirect=true";
		} else {
			return "reviewUnspscCode?faces-redirect=true";
		}

		// return "";

	}

	public String returnToPendingCodified(CmUnspsc unspscObj) {
		System.out.println("**********returnToPendingCodified************");

		return "assignProductCode?faces-redirect=true";

	}

	public String selectFromExtensionToAssign(CmUnspsc unspscObj) {
		log.debug("**********selectFromExtensionToAssign************");
		System.out.println("**********selectFromExtensionToAssign************");
		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

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
		System.out.println("......... unspscObj.getCmBrand()="
				+ unspscObj.getCmBrand());
		for (CmBrand brand : unspscObj.getCmBrand()) {
			System.out.println(".........brand.getBrandName()="
					+ brand.getBrandName());
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
						.getBrandCriteria()
						.add(new SelectItem(brand.getBrandId(), brand
								.getBrandName()));
			}
		}
//		System.out
//				.println("..................this.getManagedBean().getBrandCriteria().size()="
//						+ this.getManagedBean().getBrandCriteria().size());

		for (CmType type : unspscObj.getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
						.getTypeCriteria()
						.add(new SelectItem(type.getTypeId(), type
								.getTypeName()));
			}
		}

		for (CmMeasurement measurement : unspscObj.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
						.getMeasurementCriteria()
						.add(new SelectItem(measurement.getMeasurementId(),
								measurement.getMeasurementName()));
			}
		}

		return "searchExtensionToAssignItem.xhtml";
	}

	public String requestExtensionWithUNSPSC() {

		UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		log.info("token:" + token);

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		log.info("getUnspscCode:"
				+ this.getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
		log.info("color:" + colorFilterBean.getSearchValue());
		log.info("type:" + typeFilterBean.getSearchValue());
		log.info("brand:" + brandFilterBean.getSearchValue());
		log.info("measurement:" + measurementFilterBean.getSearchValue());

		if (StringUtils.isNotBlank((String) colorFilterBean.getSearchValue())) {

			ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
			colorFilterBean.setSearchField("colorId");
			searchBeanColor.getDataFilterBeans().add(colorFilterBean);
			reqDTO.setColorSearchBean(searchBeanColor);
			resDTO = unspscSO.searchColorObj(reqDTO);

			log.info("**getColorName**"
					+ resDTO.getColorList().get(0).getColorName());
			unspscBB.getManagedBean().getRequestCode()
					.setColorName(resDTO.getColorList().get(0).getColorName());

			unspscBB.getManagedBean().getRequestCode()
					.setColorId(resDTO.getColorList().get(0).getColorId());
			unspscBB.getManagedBean().getRequestCode().setRequestColor(false);
		} else {
			unspscBB.getManagedBean().getRequestCode().setRequestColor(true);
		}

		if (StringUtils.isNotBlank((String) typeFilterBean.getSearchValue())) {

			ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
			typeFilterBean.setSearchField("typeId");
			searchBeanColor.getDataFilterBeans().add(typeFilterBean);
			reqDTO.setTypeSearchBean(searchBeanColor);
			resDTO = unspscSO.searchTypeObj(reqDTO);

			log.info("**typeName**" + resDTO.getTypeList().get(0).getTypeName());
			unspscBB.getManagedBean().getRequestCode()
					.setTypeName(resDTO.getTypeList().get(0).getTypeName());

			unspscBB.getManagedBean().getRequestCode()
					.setTypeId(resDTO.getTypeList().get(0).getTypeId());
			unspscBB.getManagedBean().getRequestCode().setRequestType(false);
		} else {
			unspscBB.getManagedBean().getRequestCode().setRequestType(true);
		}

		if (StringUtils.isNotBlank((String) brandFilterBean.getSearchValue())) {

			ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
			brandFilterBean.setSearchField("brandId");
			searchBeanColor.getDataFilterBeans().add(brandFilterBean);
			reqDTO.setBrandSearchBean(searchBeanColor);
			resDTO = unspscSO.searchBrandObj(reqDTO);

			unspscBB.getManagedBean().getRequestCode()
					.setBrandName(resDTO.getBrandList().get(0).getBrandName());
			unspscBB.getManagedBean().getRequestCode()
					.setBrandId(resDTO.getBrandList().get(0).getBrandId());
			unspscBB.getManagedBean().getRequestCode().setRequestBrand(false);

		} else {
			unspscBB.getManagedBean().getRequestCode().setRequestBrand(true);
		}

		if (StringUtils.isNotBlank((String) measurementFilterBean
				.getSearchValue())) {

			ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
			measurementFilterBean.setSearchField("measurementId");
			searchBeanColor.getDataFilterBeans().add(measurementFilterBean);
			reqDTO.setMeasurementSearchBean(searchBeanColor);

			resDTO = unspscSO.searchMeasurementObj(reqDTO);

			unspscBB.getManagedBean()
					.getRequestCode()
					.setMeasurementName(
							resDTO.getMeasurementList().get(0)
									.getMeasurementName());
			unspscBB.getManagedBean()
					.getRequestCode()
					.setMeasurementId(
							resDTO.getMeasurementList().get(0)
									.getMeasurementId());
			unspscBB.getManagedBean().getRequestCode()
					.setRequestMeasurement(false);
		} else {
			unspscBB.getManagedBean().getRequestCode()
					.setRequestMeasurement(true);
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

		// if (unspscBB.isCatalogueAdmin()) {
		//
		// log.info("***catalogue admin*** REQUEST EXTENSION");
		// reqDTO.setOutCome("REQUEST EXTENSION");
		// } else if (unspscBB.isCodeAdmin()) {
		//
		// log.info("***code admin*** SUBMIT");
		// reqDTO.setOutCome("SUBMIT");
		// }

		if (this.getManagedBean().getCurrentTaskName() != null
				&& this.getManagedBean()
						.getCurrentTaskName()
						.equalsIgnoreCase(
								my.ep.constant.cm.CmConst.TASK_CREATE_TEMP_CODE)) {
			log.info("***code admin*** SUBMIT");
			reqDTO.setOutCome("SUBMIT");
		} else {
			log.info("***catalogue admin*** REQUEST EXTENSION");
			reqDTO.setOutCome("REQUEST EXTENSION");
		}

		reqDTO.getRequestExtList().add(
				unspscBB.getManagedBean().getRequestCode());
		resDTO = unspscSO.updateExtensionRequest(reqDTO);
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

	public CmColor searchColorObjByColorId(Long colorId) {

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

		System.out.println("**********fillExtensionLists************");
		System.out
				.println("**********this.getManagedBean().getSelectedUNSPSCObj()="
						+ this.getManagedBean().getSelectedUNSPSCObj());
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

	public String goToRequestUnspsc() {
		return NAV_TO_UNSPSC_REQUEST_FORM;
	}

	public String requestNewExtensionCode() {
		System.out
				.println("..................requestNewExtensionCode....................");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// // set unspscCommidityCode
		// this.getManagedBean()
		// .getRequestExtCode()
		// .setTempCode(
		// this.getManagedBean().getSelectedUNSPSCObj()
		// .getUnspscCode());
		// this.getManagedBean()
		// .getRequestExtCode()
		// .setTempTitle(
		// this.getManagedBean().getSelectedUNSPSCObj()
		// .getUnspscTitle());
		//
		// if (!this.getManagedBean().getRequestExtCode().isRequestType()) {
		// if (StringUtils.isBlank((String) typeFilterBean.getSearchValue())) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "select from the list if you don't want to request new one.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// // get object from selected values :
		// Long typeId = Long.valueOf(typeFilterBean.getSearchValue()
		// .toString());
		// CmType cmType = null;
		// cmType = searchTypeOjbyTypeId(typeId);
		//
		// this.getManagedBean().getRequestExtCode()
		// .setTypeName(cmType.getTypeName());
		// this.getManagedBean().getRequestExtCode()
		// .setTypeId(cmType.getTypeId());
		// } else {// request new checked for Type
		// if (this.getManagedBean().getRequestExtCode().getTypeName()
		// .isEmpty()) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "Fill up the Request New field for those are selected.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// }
		//
		// if (!this.getManagedBean().getRequestExtCode().isRequestBrand()) {
		// if (StringUtils.isBlank((String) brandFilterBean.getSearchValue())) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "select from the list if you don't want to request new one.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// // get object from selected values :
		// Long brandId = Long.valueOf(brandFilterBean.getSearchValue()
		// .toString());
		// CmBrand cmBrand = null;
		// cmBrand = searchBrandObjByBrandId(brandId);
		//
		// this.getManagedBean().getRequestExtCode()
		// .setBrandName(cmBrand.getBrandName());
		// this.getManagedBean().getRequestExtCode()
		// .setBrandId(cmBrand.getBrandId());
		//
		// } else {// request new checked for Brand
		// if (this.getManagedBean().getRequestExtCode().getBrandName()
		// .isEmpty()) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "Fill up the Request New field for those are selected.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// }
		//
		// if
		// (!this.getManagedBean().getRequestExtCode().isRequestMeasurement()) {
		// if (StringUtils.isBlank((String) measurementFilterBean
		// .getSearchValue())) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "select from the list if you don't want to request new one.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// // get object from selected values :
		// Long measurementId = Long.valueOf(measurementFilterBean
		// .getSearchValue().toString());
		// CmMeasurement cmMeasurement = null;
		// cmMeasurement = searchMeasurementByMeasurementId(measurementId);
		// this.getManagedBean().getRequestExtCode()
		// .setMeasurementId(cmMeasurement.getMeasurementId());
		// this.getManagedBean().getRequestExtCode()
		// .setMeasurementName(cmMeasurement.getMeasurementName());
		// } else {// request new checked for Measurment
		// if (this.getManagedBean().getRequestExtCode().getMeasurementName()
		// .isEmpty()) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "Fill up the Request New field for those are selected.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// }
		//
		// if (!this.getManagedBean().getRequestExtCode().isRequestColor()) {
		// if (StringUtils.isBlank((String) colorFilterBean.getSearchValue())) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "select from the list if you don't want to request new one.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// // get object from selected values :
		//
		// Long colorId = Long.valueOf(colorFilterBean.getSearchValue()
		// .toString());
		// CmColor selectedCmColor = null;
		// selectedCmColor = searchColorObjByColorId(colorId);
		//
		// this.getManagedBean().getRequestExtCode()
		// .setColorName(selectedCmColor.getColorName());
		// this.getManagedBean().getRequestExtCode()
		// .setColorId(selectedCmColor.getColorId());
		//
		// } else {// request new checked for Color
		// if (this.getManagedBean().getRequestExtCode().getColorName()
		// .isEmpty()) {
		// facesContext
		// .addMessage(
		// null,
		// new FacesMessage(
		// FacesMessage.SEVERITY_ERROR,
		// null,
		// MessageUtils
		// .getMessage(
		// "Fill up the Request New field for those are selected.",
		// MessageUtils.BUNDLE_COMMON_MESSAGES,
		// new FacesScopes()
		// .getUserLocale())));
		// return "";
		// }
		// }
		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
		this.getManagedBean().getRequestExtCodeList()
				.addAll(bb.getManagedBean().getRequestExtCodeList());

		if (this.getManagedBean().getRequestExtCodeList() == null
				|| this.getManagedBean().getRequestExtCodeList().isEmpty()) {
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715004,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return "";
		}

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token = face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		// ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO)
		// BusinessDelegate
		// .getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		// SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		//
		// PmUser pmUser = new FacesScopes().getUserSessionBean();
		// supplierReqDTO.setUserId(pmUser.getUserId());

		// SupplierSearchResDTO smResDTO = supplierSearchSO
		// .retriveSupplierProfiles(supplierReqDTO);
		//
		// System.out.println("getSmSupplier"
		// + smResDTO.getResultBean().getResultList().get(0));

		// for (CmRequestCode code :
		// this.getManagedBean().getRequestExtCodeList()) {
		// code.setSmSupplier(smResDTO.getResultBean().getResultList().get(0));
		// }

		reqDTO.setCatalogueAdminRequest(true);
		reqDTO.setRequestExtList(this.getManagedBean().getRequestExtCodeList());
		reqDTO.setToken(token);

		CodificationResDTO resDTO = unspscSO.addExtensionRequest(reqDTO);

		facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
										.getMessage(
												MessageConst.INFO_710012,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes()
														.getUserLocale())));
		// Hide contractedItemPopUp if everything done.
		RequestContext.getCurrentInstance().execute(
				"contractedItemPopUp.hide()");
		return NAV_TO_ASSIGN_PRODUCT_CODE;
	}

	public String requestExtensionCodeII() {
		UnspscCodeBackingBean codeBB = new UnspscCodeBackingBean();
		if (codeBB.getManagedBean().getRequestExtCodeList() != null) {
			codeBB.getManagedBean().getRequestExtCodeList().clear();
		}

		codeBB.getManagedBean()
				.getSelectedLovUnspsc()
				.setUnspscCode(
						this.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscCode());
		codeBB.getManagedBean()
				.getSelectedLovUnspsc()
				.setUnspscTitle(
						this.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscTitle());
		return "extensionCodeRequestFormII?faces-redirect=true";
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

	public void goToAddServiceConfirmPopUp(CmUnspsc unspscObj) {
		// set commodity
		System.out
				.println("...............goToAddServiceConfirmPopUp..............");
		System.out.println("..............unspscObj.getUnspscCode()="
				+ unspscObj.getUnspscCode());
		this.getManagedBean().setSelectedUNSPSCObj(unspscObj);

	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public boolean productCode(String unspscCode) {
		System.out.println("............productCode..........");
		boolean flag = true;
		String segmentCode = "";

		if (unspscCode != null && unspscCode.trim().length() > 0) {
			segmentCode = unspscCode.substring(0, 2);
		}

		if (segmentCode.compareTo(NO_69) > 0) {
			flag = false;
		}

		System.out.println("flag:" + flag);

		return flag;
	}

	public boolean isSelectedRequestItemProduct() {
		if (this.getManagedBean().getSelectedRequestItem() != null) {
			return isProduct(this.getManagedBean().getSelectedRequestItem()
					.getItemType());
		}
		return true;
	}

	public boolean isProduct(PmParameter item) {
		String itemType = this.getParameterDesc(item);
		if (itemType.equalsIgnoreCase("Product")) {
			return true; // means less than
		} else {
			return false; // means greater than
		}
	}

	public int productOrServiceOperator() {
		if (this.getManagedBean().getSelectedRequestItem() != null) {
			if (isProduct(this.getManagedBean().getSelectedRequestItem()
					.getItemType())) {
				return 5; // means less than (76000000)
			} else {
				return 4; // means greater than (76000000)
			}
		}
		return -1;
	}

	public boolean isAllSupplier() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
				|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("CM_USER");
	}

	public String redirectToRequestUnspscCodeII() {
		// renewSesionObject();
		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
		if (bb.getManagedBean().getRequestExtCodeList() != null) {
			bb.getManagedBean().getRequestExtCodeList().clear();
		}
		if (this.getManagedBean().getSelectedRequestItem().getItemType()
				.equals("2")) {
			bb.getManagedBean().getRequestCode().setItemType("S");
		} else {
			bb.getManagedBean().getRequestCode().setItemType("P");
		}
		return "unspscRequestFormII";
	}

	public void preRenderCodificationView() {
		log.info(".................preRenderCodificationView................");
		// this.renewSesionObject();
		resetAction();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "codificationView");
	}
}

/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.cm.catalog.CmFavoriteSupplier;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * The Class CatalogueManagedBean.
 * 
 * @author hketan
 */
@ManagedBean(name = "catalogueMB")
@SessionScoped
public class CatalogueManagedBean extends BaseManagedBean {

	/** The log. */
	private static Logger log = EpLogger.getLogger(CatalogueManagedBean.class);

	/** The item name. */
	private String itemName;

	/** The item code. */
	private String itemCode;

	/** The supplier name. */
	private String supplierName;

	/** The contract no. */
	private String contractNo;

	/** The search type. */
	private String searchType = "S";

	/** The search ind. */
	private String searchInd = "and";

	/** The advance search ind. */
	private boolean advanceSearchInd = false;

	/** The advance search list. */
	private List<AdvanceSearchVO> advanceSearchList;

	/** The search criteria. */
	private List<SelectItem> searchCriteria;

	/** The selected supplier item list. */
	private List<CmSupplierItem> selectedSupplierItemList;

	/** The selected supplier items. */
	private CmSupplierItem[] selectedSupplierItems;

	/** The navigation url. */
	private String navigationURL;

	/** The pricing criteria. */
	private List<SelectItem> pricingCriteria;

	/** The pricing list. */
	private List<CmPricing> pricingList;

	/** The zonal pricing list. */
	private List<PricingVO> zonalPricingList;

	/** The district pricing list. */
	private List<PricingVO> districtPricingList;

	/** The selected pricing. */
	private PricingVO selectedPricing;

	/** The district ind. */
	private boolean districtIND = false;

	/** The render table view. */
	private boolean renderTableView = true;

	/** The render grid view. */
	private boolean renderGridView = false;

	/** The price type tariff. */
	private boolean priceTypeTariff = false;

	/** The unspsc. */
	private CmUnspsc unspsc;

	/** The selected maintain item. */
	private CmSupplierItem selectedMaintainItem;

	/** The selected maintain items. */
	// private CmSupplierItem[] selectedMaintainItems;

	/** The supplier item list. */
	private List<CmSupplierItem> supplierItemList;

	/** The wish lists. */
	private List<CmWishlist> wishLists;

	/** The compare index. */
	private Integer compareIndex;

	/** The supplier item obj. */
	private CmSupplierItem supplierItemObj;

	/** The rejected item list. */
	private List<CmSupplierItem> rejectedItemList;

	/** The approval item list. */
	private List<CmSupplierItem> approvalItemList;

	/** The approved item list. */
	private List<CmSupplierItem> approvedItemList;

	/** The published item list. */
	private List<CmSupplierItem> publishedItemList;

	/** The expired item list. */
	private List<CmSupplierItem> expiredItemList;

	/** The selected rejected items. */
	private CmSupplierItem[] selectedRejectedItems;

	/** The selected approval items. */
	private CmSupplierItem[] selectedApprovalItems;

	/** The selected approved items. */
	private CmSupplierItem[] selectedApprovedItems;

	/** The selected published items. */
	private CmSupplierItem[] selectedPublishedItems;

	/** The selected expired items. */
	private CmSupplierItem[] selectedExpiredItems;

	/** The cart list. */
	private List<CmSupplierItem> cartList;

	/** The temp list. */
	private List<CmSupplierItem> tempList;

	/** The favorite supplier list. */
	private List<CmFavoriteSupplier> favoriteSupplierList;

	/** The payment term criteria. */
	private List<SelectItem> paymentTermCriteria;

	/** The compare label. */
	private List<String> compareLabel;

	/** The item specification. */
	private List<CtPanelItemSpec> itemSpec;

	/** The selected lov uom. */
	private PmUom selectedLovUom;

	/** The temp map. */
	private Map tempMap = new HashMap();

	/** The promotion price list. */
	private List<CmPricing> promotionPriceList;

	/** The tier price list. */
	private List<CmPricing> tierPriceList;

	/** The promotion tier price list. */
	private List<CmPricing> promotionTierPriceList;

	/** The state list. */
	private List<PmState> stateList;

	/** The district list. */
	private List<PmDistrict> districtList;

	/** The status desc list. */
	private List<PmStatusDesc> statusDescList;

	/** The select view items. */
	private List<VCmSupplierItem> selectViewItems;

	/** The selected items. */
	private VCmSupplierItem[] selectedItems;

	/** The selected ids. */
	private Map<Long, Boolean> selectedIds = new HashMap<Long, Boolean>();

	/** The view supplier item list. */
	private List<VCmSupplierItem> viewSupplierItemList;

	/** The contracted exist. */
	private boolean contractedExist = false;

	/** The state id. */
	private long[] stateId;

	/** The district id. */
	private long[] districtId;

	/** The district ls. */
	private List<Long> districtLs;

	/** The state ls. */
	private List<Long> stateLs;

	/** The tab active index. */
	private Integer tabActiveIndex = 0;

	/** The reject reason. */
	private String rejectReason;

	/** The supplier zone list. */
	private List<CmSupplierZone> supplierZoneList;

	/** The option list. */
	private List<SelectItem> optionList = new ArrayList<SelectItem>();

	/** The option list value. */
	private Integer optionListValue = 0;

	/** The pricing tab index. */
	private Integer pricingTabIndex = 0;

	/** The normal pricing index. */
	private Integer normalPricingIndex = 0;

	/** The tier pricing index. */
	private Integer tierPricingIndex = 0;

	/** The zonal pricing index. */
	private Integer zonalPricingIndex = 0;

	/** The zonal tier pricing index. */
	private Integer zonalTierPricingIndex = 0;

	/** The tier row index. */
	private int tierRowIndex;

	/** The zonal tier row index. */
	private int zonalTierRowIndex;

	/** The clone cm supplier item. */
	private CmSupplierItem cloneCmSupplierItem;

	/** The clone index. */
	private int cloneIndex;

	/** The error1. */
	private boolean error1 = false;

	/** The error2. */
	private boolean error2 = false;

	/** The error3. */
	private boolean error3 = false;

	/** The error4. */
	private boolean error4 = false;

	/** The error5. */
	private boolean error5 = false;

	/** The error6. */
	private boolean error6 = false;

	/** The error7. */
	private boolean error7 = false;

	/** The error8. */
	private boolean error8 = false;

	/** The error9. */
	private boolean error9 = false;

	/** The error9. */
	private boolean error99 = false;

	/** The temp supplier list. */
	private List<VCmSupplierItem> tempSupplierList;

	/** The ct request list. */
	private List<VCmSupplierItem> ctRequestList;

	/** The simple quote flag. */
	private boolean simpleQuoteFlag = true; // enable or disable simple quote in
											// search catalogue

	/** The display catalogue. */
	private boolean displayCatalogue = false;

	/** The previous screen. */
	private int previousScreen = 0;// 0=catSearch;1=catMaintenance;2=pendingTask;

	/** The current date. */
	private Date currentDate = new Date();

	/** The item name filter bean. */
	private SearchFilterBean itemNameFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The item code filter bean. */
	private SearchFilterBean itemCodeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The supplier name filter bean. */
	private SearchFilterBean supplierNameFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The contract no filter bean. */
	private SearchFilterBean contractNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	
	/** The contract title filter bean. */
	private SearchFilterBean contractTitleFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	
	/** The contract loa no filter bean. */
	private SearchFilterBean loaNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	
	/** The contract loa physical no filter bean. */
	private SearchFilterBean loaPhysicalNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	

	/** The physical contract no filter bean. */
	private SearchFilterBean physicalContractNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The circular no filter bean. */
	private SearchFilterBean circularNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The nature of business filter bean. */
	private SearchFilterBean natureOfBusinessFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	/** The ep no filter bean. */
	private SearchFilterBean epNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The mof no filter bean. */
	private SearchFilterBean mofNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The biz reg no filter bean. */
	private SearchFilterBean bizRegNoFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	/** The district filter bean. */
	private SearchFilterBean districtFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_IN.value());

	/** The state filter bean. */
	private SearchFilterBean stateFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_IN.value());

	/** The cat type filter bean. */
	private SearchFilterBean catTypeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	
	private boolean g2gFlag = false;

	/** The search data model. */
	private IPagingDataModel<VCmSupplierItem> searchDataModel = new SearchPagingDataModel();

	/** The upload dir. */
	private String uploadDir = EpPropertiesUtils.getInstance().getProperty(
			EpPropertiesConst.UPLOAD_URL);

	private int resultTabActiveIndex;

	private Long itemToDelete;

	private Long supplierId;

	private IPagingDataModel<CmSupplierItem> draftSearchDataModel = new DraftSearchPagingDataModel();

	private IPagingDataModel<CmSupplierItem> approvalSearchDataModel = new ApprovalSearchPagingDataModel();

	private IPagingDataModel<CmSupplierItem> approvedSearchDataModel = new ApprovedSearchPagingDataModel();

	private IPagingDataModel<CmSupplierItem> rejectSearchDataModel = new RejectSearchPagingDataModel();

	private IPagingDataModel<CmSupplierItem> publishSearchDataModel = new PublishSearchPagingDataModel();

	private IPagingDataModel<CmSupplierItem> expiredSearchDataModel = new ExpiredSearchPagingDataModel();

	private SearchFilterBean keyWordFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	private List<CmSupplierItem> draftSupplierItemList;

	private boolean supplierItemcheckBoxAll;

	private boolean refreshDraftSuppItemFlag = true;

	private int draftTabPageSize = 5;
	private int pendingApprovalTabPageSize = 5;
	private int approvedTabPageSize = 5;
	private int publishTabPageSize = 5;
	private int rejectedTabPageSize = 5;
	private int expiredTabPageSize = 5;

	private List<CmSupplierItem> cloneCmSupplierItemLs;

	private int zonalRowIndex;
	private SmGstSupplier smGstSupplier;
	private List<String> availableNoList;
	private String scheduleNo;
	
	private String expiryAlert;
	
	private CmUnspsc unspscObj;

	private class ExpiredSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public ExpiredSearchPagingDataModel() {
			super("expiredSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {
				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setExpiredTabPageSize(this.getPageSize());

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("E");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	private class PublishSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public PublishSearchPagingDataModel() {
			super("publishSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {

				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setPublishTabPageSize(this.getPageSize());
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("U");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	private class RejectSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public RejectSearchPagingDataModel() {
			super("rejectedSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {

				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setRejectedTabPageSize(this.getPageSize());

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("R");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	private class ApprovedSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public ApprovedSearchPagingDataModel() {
			super("approvedSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {

				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setApprovedTabPageSize(this.getPageSize());

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("A");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	private class ApprovalSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public ApprovalSearchPagingDataModel() {
			super("approvalSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {

				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setPendingApprovalTabPageSize(
						this.getPageSize());

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("P");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	private class DraftSearchPagingDataModel extends
			PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public DraftSearchPagingDataModel() {
			super("draftSupplierItemID");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<VCmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<VCmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);

			try {

				CatalogueBackingBean bb = new CatalogueBackingBean();
				bb.getManagedBean().setDraftTabPageSize(this.getPageSize());

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());

				reqDTO.setItemStatus(CmConst.ITEM_STATUS_DRAFT);
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				Map tempMap = new HashMap();
				for (int i = 0; i < resDTO.getSuppItemResultBean()
						.getResultList().size(); i++) {
					CmSupplierItem suppItem = resDTO.getSuppItemResultBean()
							.getResultList().get(i);

					if (bb.getManagedBean().getCloneCmSupplierItemLs() != null
							&& bb.getManagedBean().getCloneCmSupplierItemLs()
									.size() > 0) {

						for (CmSupplierItem obj : bb.getManagedBean()
								.getCloneCmSupplierItemLs()) {

							if (suppItem.getSupplierItemId() == obj
									.getSupplierItemId()) {

								if (!tempMap.containsKey(obj
										.getSupplierItemId())) {
									suppItem.setSelected(true);
									tempMap.put(obj.getSupplierItemId(),
											obj.getSupplierItemId());
								}
							}
						}

					}

				}

				System.out
						.println("bb.getManagedBean().isRefreshDraftSuppItemFlag()"
								+ bb.getManagedBean()
										.isRefreshDraftSuppItemFlag());
				if (bb.getManagedBean().isRefreshDraftSuppItemFlag()) {
					RequestContext.getCurrentInstance().execute(
							"refreshDraftSuppItem();");
				}

				return resDTO.getSuppItemResultBean();

			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				supplierSearchSO = null;
			}
		}

	}

	/**
	 * The Class SearchPagingDataModel.
	 */
	private class SearchPagingDataModel extends
			PagingDataModel<VCmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public SearchPagingDataModel() {
			super("supplierItemID", false);
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<VCmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<VCmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<VCmSupplierItem> pageChangeListener(
				ISearchBean<VCmSupplierItem> searchBean) {

			if (StringUtils.isBlank((String) itemNameFilterBean
					.getSearchValue())
					&& StringUtils.isBlank((String) itemCodeFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) supplierNameFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) contractNoFilterBean
							.getSearchValue())
					&& StringUtils
							.isBlank((String) physicalContractNoFilterBean
									.getSearchValue())
					&& StringUtils.isBlank((String) circularNoFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) mofNoFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) epNoFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) bizRegNoFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) contractTitleFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) loaNoFilterBean
							.getSearchValue())
					&& StringUtils.isBlank((String) loaPhysicalNoFilterBean
							.getSearchValue())		
					&& StringUtils.isBlank((String) keyWordFilterBean
							.getSearchValue()) && districtId == null) {
				log.debug("******SearchResultBean******");
				return new SearchResultBean();
			}

			IViewSupplierItemSO viewSO = null;

			try {

				viewSO = (IViewSupplierItemSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", IViewSupplierItemSO.class);

				System.out.println("catTypeFilterBean.getSearchValue(): "
						+ catTypeFilterBean.getSearchValue());
				if (catTypeFilterBean.getSearchValue() != null) {
					catTypeFilterBean.setSearchField("CAT_TYPE");
					// catTypeFilterBean.setSearchValue("XC");
					searchBean.getDataFilterBeans().add(catTypeFilterBean);
				}
				if (StringUtils.isNotBlank((String) itemNameFilterBean
						.getSearchValue())) {
					itemNameFilterBean.setSearchField("ITEM_NAME");
					itemNameFilterBean.setCaseInsensitive(true);
					itemNameFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					//searchBean.getDataFilterBeans().add(itemNameFilterBean);
					
					
					keyWordFilterBean.setSearchField("KEYWORD");
					keyWordFilterBean.setSearchValue((String) itemNameFilterBean.getSearchValue());
					keyWordFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					keyWordFilterBean.setCaseInsensitive(true);
					
					
					
					SearchMultiFilterBean multi = new SearchMultiFilterBean(itemNameFilterBean
							, keyWordFilterBean);
					searchBean.getDataFilterBeans().add(multi);
					
					
				}
				
				
				if (StringUtils.isNotBlank((String) contractTitleFilterBean
						.getSearchValue())) {
					contractTitleFilterBean.setSearchField("CONTRACT_NAME");
					contractTitleFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(contractTitleFilterBean);
				}
				
				if (StringUtils.isNotBlank((String) loaNoFilterBean
						.getSearchValue())) {
					loaNoFilterBean.setSearchField("LOA_NO");
					loaNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(loaNoFilterBean);
				}
				
				if (StringUtils.isNotBlank((String) loaPhysicalNoFilterBean
						.getSearchValue())) {
					loaPhysicalNoFilterBean.setSearchField("LOA_PHYSICAL_NO");
					loaPhysicalNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(loaPhysicalNoFilterBean);
				}

				if (StringUtils.isNotBlank((String) itemCodeFilterBean
						.getSearchValue())) {
					itemCodeFilterBean.setSearchField("ITEM_CODE");
					itemCodeFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(itemCodeFilterBean);
				}

				if (StringUtils.isNotBlank((String) supplierNameFilterBean
						.getSearchValue())) {
					supplierNameFilterBean.setSearchField("COMPANY_NAME");
					supplierNameFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(supplierNameFilterBean);
				}

				if (StringUtils.isNotBlank((String) contractNoFilterBean
						.getSearchValue())) {
					contractNoFilterBean.setSearchField("CONTRACT_NO");
					contractNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(contractNoFilterBean);
				}

				if (StringUtils
						.isNotBlank((String) physicalContractNoFilterBean
								.getSearchValue())) {
					physicalContractNoFilterBean
							.setSearchField("CONTRACT_PHYSICAL_NO");
					physicalContractNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(
							physicalContractNoFilterBean);
				}

				if (StringUtils.isNotBlank((String) circularNoFilterBean
						.getSearchValue())) {
					circularNoFilterBean.setSearchField("CIRCULAR_NO");
					circularNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(circularNoFilterBean);
				}

				if (StringUtils.isNotBlank((String) epNoFilterBean
						.getSearchValue())) {
					epNoFilterBean.setSearchField("EP_NO");
					epNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(epNoFilterBean);
				}

				if (StringUtils.isNotBlank((String) mofNoFilterBean
						.getSearchValue())) {
					mofNoFilterBean.setSearchField("MOF_NO");
					mofNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(mofNoFilterBean);
				}

				if (StringUtils.isNotBlank((String) bizRegNoFilterBean
						.getSearchValue())) {
					bizRegNoFilterBean.setSearchField("BIZ_REG_NO");
					bizRegNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(bizRegNoFilterBean);
				}

				UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
				log.info("bb.isRequisitioner()" + bb.isRequisitioner());
				if (!bb.isRequisitioner()) {
					catTypeFilterBean.setSearchField("CAT_TYPE");
					catTypeFilterBean.setSearchValue("XC");
					searchBean.getDataFilterBeans().add(catTypeFilterBean);
				}

				setDistrictLs(new ArrayList<Long>());
				log.info("districtId:" + districtId);
				for (Long district : districtId) {
					log.info("district:" + district);
					districtLs.add(district);
				}

				log.debug("districtLs.size():" + districtLs.size());
				if (districtLs != null && districtLs.size() > 0) {
					districtFilterBean.setSearchField("DISTRICT_ID");
					districtFilterBean.setSearchValue(districtLs);
					searchBean.getDataFilterBeans().add(districtFilterBean);
				}

				setStateLs(new ArrayList<Long>());
				for (Long state : stateId) {
					log.info("state:" + state);
					stateLs.add(state);
				}
				log.info("districtLs" + districtLs);
				if (districtLs.isEmpty()) {
					if (stateLs != null && stateLs.size() > 0) {
						stateFilterBean.setSearchField("STATE_ID");
						stateFilterBean.setSearchValue(stateLs);
						searchBean.getDataFilterBeans().add(stateFilterBean);

					}

				}
				log.debug("stateLs" + stateLs);

				FacesScopes facesScopes = new FacesScopes();
				Long userId = facesScopes.getUserSessionBean().getUserId();
				PmUserOrg userOrg = null;
				Long ministryId = null;
				Long departmentId = null;
				
				if (bb.isRequisitioner()) {
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(userId);
					IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IUserSO.class);
					UserResDTO userResDTO = userSO
							.retrieveUserOrgListByUserId(userReqDTO);
					userOrg = userResDTO.getUserOrgList().get(0);
	
					IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IOrgProfileSO.class);
					OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
					orgProfileReqDTO.setOrgProfileId(userOrg.getPmOrgProfile()
							.getOrgProfileId());
					OrgProfileResDTO orgProfileResDTO = orgProfileSO
							.retrieveMinistryByOrgProfileId(orgProfileReqDTO);
					if (orgProfileResDTO.getPmOrgProfile() != null) {
						//String ministryId = "#" + orgProfileResDTO.getPmOrgProfile().getOrgProfileId() + "#";
						ministryId = orgProfileResDTO.getPmOrgProfile().getOrgProfileId();
//						SearchMultiFilterBean ministryFilterBean = new SearchMultiFilterBean(
//								new SearchFilterBean("EXEMPTED_MINISTRY_LIST",
//										SearchOperator.OPERATOR_IS_NULL.value(),
//										null), new SearchFilterBean(
//										"EXEMPTED_MINISTRY_LIST",
//										SearchOperator.OPERATOR_NOT_LIKE.value(),
//										ministryId,
//										SearchCondition.CONDITION_OR.value()));
//						searchBean.getDataFilterBeans().add(ministryFilterBean);
					}
					orgProfileResDTO = orgProfileSO.retrieveJabatanByOrgProfileId(orgProfileReqDTO);
					if (orgProfileResDTO.getPmOrgProfile() != null) {	
						departmentId = orgProfileResDTO.getPmOrgProfile().getOrgProfileId();
					}
				}

				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				searchBean.setUseEntityClass(true);
				reqDTO.setViewSearchBean(searchBean);
				if(userOrg!=null) {
					reqDTO.setUserOrgProfileId(userOrg.getPmOrgProfile().getOrgProfileId());
					reqDTO.setMinistryId(ministryId);
					reqDTO.setDepartmentId(departmentId);
				}
				if (catTypeFilterBean != null
						&& catTypeFilterBean.getSearchValue() != null) {
					reqDTO.setCatType(catTypeFilterBean.getSearchValue()
							.toString());
				}

				if (searchBean.getDataFilterBeans().size() <= 0) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_716006,
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					// throw new EpException(MessageConst.ERROR_715014);
					return new SearchResultBean();
				}

				reqDTO.setUserId(userId);
				System.out.println("******g2gFlag*****"+g2gFlag);
				reqDTO.setG2gFlag(g2gFlag);
				log.debug("*********b4***********");
				CatalogueResDTO resDTO = viewSO.searchViewSupplierItems(reqDTO);
				setViewSupplierItemList(resDTO.getViewSuppItemResultBean()
						.getResultList());
				log.debug("*********after***********");

				// enable or disable simple quote in search catalogue
				if(null == getCatTypeFilterBean().getSearchValue()){
					if (resDTO.getViewSuppItemResultBean().getResultList() != null
							&& resDTO.getViewSuppItemResultBean().getResultList()
							.size() > 0) {
						setSimpleQuoteFlag(Boolean.TRUE);
					} else {
						setSimpleQuoteFlag(Boolean.FALSE);
					}
				}
				SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
				/*TemplateSpecBean productTemplateSpecBean = simpleQuoteBackingBean
						.getManagedBean().getProductTemplateSpecBean();
				TemplateSpecBean serviceTemplateSpecBean = simpleQuoteBackingBean
						.getManagedBean().getServiceTemplateSpecBean();*/
				if (simpleQuoteBackingBean.getManagedBean() != null && 
						simpleQuoteBackingBean.getManagedBean().getScQuote() != null) {
					setSimpleQuoteFlag(Boolean.FALSE);
					/*ScQuote scQuote = simpleQuoteBackingBean
								.getManagedBean().getScQuote();
						if (scQuote.getPanelItem() != null) {
							setSimpleQuoteFlag(false);
						} else {
							if (productTemplateSpecBean.getSpecMaster() != null
									&& productTemplateSpecBean.getSpecMaster()
											.getTplSpecQuestions() != null
									&& productTemplateSpecBean.getSpecMaster()
											.getTplSpecQuestions().size() > 0
									|| serviceTemplateSpecBean.getSpecMaster() != null
									&& serviceTemplateSpecBean.getSpecMaster()
											.getTplSpecQuestions() != null
									&& serviceTemplateSpecBean.getSpecMaster()
											.getTplSpecQuestions().size() > 0) {
								setSimpleQuoteFlag(false);
							}
						}*/
				}

				return resDTO.getViewSuppItemResultBean();
			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				viewSO = null;
			}
		}

	}

	/**
	 * Gets the current date without time.
	 * 
	 * @return the current date without time
	 */
	private Date getCurrentDateWithoutTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Search catalogue.
	 */
	public void searchCatalogue() {
		log.info("In searchCatalogue");
		boolean hasSearchValue = false;
		log.info("hasSearchValue: " + hasSearchValue);

		if (StringUtils
				.isNotBlank((String) itemNameFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils
				.isNotBlank((String) itemCodeFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) supplierNameFilterBean
				.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) contractNoFilterBean
				.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) physicalContractNoFilterBean
				.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) circularNoFilterBean
				.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) epNoFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) mofNoFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		if (StringUtils
				.isNotBlank((String) bizRegNoFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		if (this.getDistrictId() != null && this.getDistrictId().length > 0) {
			hasSearchValue = true;
		}

		if (this.getStateId() != null && this.getStateId().length > 0) {
			hasSearchValue = true;
		}

		if (StringUtils.isNotBlank((String) keyWordFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}

		
		if (StringUtils.isNotBlank((String) contractTitleFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}
		
		if (StringUtils.isNotBlank((String) loaNoFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}
		
		if (StringUtils.isNotBlank((String) loaPhysicalNoFilterBean.getSearchValue())) {
			hasSearchValue = true;
		}
		log.info("hasSearchValue: " + hasSearchValue);

		if (!hasSearchValue) {
			throw new EpException(MessageConst.ERROR_716006);
		} else {

			searchDataModel.reloadData();
		}
	}

	/**
	 * Gets the item name filter bean.
	 * 
	 * @return the item name filter bean
	 */
	public SearchFilterBean getItemNameFilterBean() {
		return itemNameFilterBean;
	}

	/**
	 * Sets the item name filter bean.
	 * 
	 * @param itemNameFilterBean
	 *            SearchFilterBean itemNameFilterBean
	 */
	public void setItemNameFilterBean(SearchFilterBean itemNameFilterBean) {
		this.itemNameFilterBean = itemNameFilterBean;
	}

	/**
	 * Gets the search data model.
	 * 
	 * @return the search data model
	 */
	public IPagingDataModel<VCmSupplierItem> getSearchDataModel() {
		return searchDataModel;
	}

	/**
	 * Sets the search data model.
	 * 
	 * @param searchDataModel
	 *            the new search data model
	 */
	public void setSearchDataModel(
			IPagingDataModel<VCmSupplierItem> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	/**
	 * Gets the select view items.
	 * 
	 * @return the select view items
	 */
	public List<VCmSupplierItem> getSelectViewItems() {
		if (selectViewItems == null) {
			selectViewItems = new ArrayList<VCmSupplierItem>();
		}
		return selectViewItems;
	}

	/**
	 * Sets the select view items.
	 * 
	 * @param selectViewItems
	 *            the new select view items
	 */
	public void setSelectViewItems(List<VCmSupplierItem> selectViewItems) {
		this.selectViewItems = selectViewItems;
	}

	/**
	 * Gets the contract no.
	 * 
	 * @return the contract no
	 */
	public String getContractNo() {
		return contractNo;
	}

	/**
	 * Sets the contract no.
	 * 
	 * @param contractNo
	 *            the new contract no
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	/**
	 * Gets the supplier name.
	 * 
	 * @return the supplier name
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * Sets the supplier name.
	 * 
	 * @param supplierName
	 *            the new supplier name
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * Gets the district list.
	 * 
	 * @return the district list
	 */
	public List<PmDistrict> getDistrictList() {
		if (districtList == null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}

	/**
	 * Sets the district list.
	 * 
	 * @param districtList
	 *            the new district list
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * Gets the state list.
	 * 
	 * @return the state list
	 */
	public List<PmState> getStateList() {
		if (stateList == null)
			stateList = new ArrayList<PmState>();
		return stateList;
	}

	/**
	 * Sets the state list.
	 * 
	 * @param stateList
	 *            the new state list
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the promotion price list.
	 * 
	 * @return the promotion price list
	 */
	public List<CmPricing> getPromotionPriceList() {
		if (promotionPriceList == null)
			promotionPriceList = new ArrayList<CmPricing>();
		return promotionPriceList;
	}

	/**
	 * Sets the promotion price list.
	 * 
	 * @param promotionPriceList
	 *            the new promotion price list
	 */
	public void setPromotionPriceList(List<CmPricing> promotionPriceList) {
		this.promotionPriceList = promotionPriceList;
	}

	/**
	 * Gets the tier price list.
	 * 
	 * @return the tier price list
	 */
	public List<CmPricing> getTierPriceList() {
		if (tierPriceList == null)
			tierPriceList = new ArrayList<CmPricing>();
		return tierPriceList;
	}

	/**
	 * Sets the tier price list.
	 * 
	 * @param tierPriceList
	 *            the new tier price list
	 */
	public void setTierPriceList(List<CmPricing> tierPriceList) {
		this.tierPriceList = tierPriceList;
	}

	/**
	 * Gets the promotion tier price list.
	 * 
	 * @return the promotion tier price list
	 */
	public List<CmPricing> getPromotionTierPriceList() {
		if (promotionTierPriceList == null)
			promotionTierPriceList = new ArrayList<CmPricing>();
		return promotionTierPriceList;
	}

	/**
	 * Sets the promotion tier price list.
	 * 
	 * @param promotionTierPriceList
	 *            the new promotion tier price list
	 */
	public void setPromotionTierPriceList(List<CmPricing> promotionTierPriceList) {
		this.promotionTierPriceList = promotionTierPriceList;
	}

	/**
	 * Gets the selected lov uom.
	 * 
	 * @return the selected lov uom
	 */
	public PmUom getSelectedLovUom() {
		if (selectedLovUom == null)
			selectedLovUom = new PmUom();
		return selectedLovUom;
	}

	/**
	 * Sets the selected lov uom.
	 * 
	 * @param selectedLovUom
	 *            the new selected lov uom
	 */
	public void setSelectedLovUom(PmUom selectedLovUom) {
		this.selectedLovUom = selectedLovUom;
	}

	/**
	 * Gets the compare label.
	 * 
	 * @return the compare label
	 */
	public List<String> getCompareLabel() {
		compareLabel = new ArrayList<String>();

		Locale locale = new FacesScopes().getUserLocale();
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.action",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.item.name",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.item.code",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.normal.price",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.uom",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.supplier.name",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.sku",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.min.order",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage("lbl.cm.delivery.term",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		compareLabel.add(ResourceBundleUtils.getMessage(
				"lbl.cm.additional.item.description",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));

		return compareLabel;
	}

	/**
	 * Sets the compare label.
	 * 
	 * @param compareLabel
	 *            the new compare label
	 */
	public void setCompareLabel(List<String> compareLabel) {
		this.compareLabel = compareLabel;
	}

	/**
	 * Gets the item specification.
	 * 
	 * @return the item specification
	 */
	public List<CtPanelItemSpec> getItemSpec() {
		if (itemSpec == null) {
			itemSpec = new ArrayList<CtPanelItemSpec>();
		}
		return itemSpec;
	}

	/**
	 * Sets the item specification.
	 * 
	 * @param itemSpec
	 *            the new item specification
	 */
	public void setItemSpec(List<CtPanelItemSpec> itemSpec) {
		this.itemSpec = itemSpec;
	}

	/**
	 * Gets the payment term criteria.
	 * 
	 * @return the payment term criteria
	 */
	public List<SelectItem> getPaymentTermCriteria() {
		paymentTermCriteria = new ArrayList<SelectItem>();
		paymentTermCriteria.add(new SelectItem("01", "3 MONTHS"));
		paymentTermCriteria.add(new SelectItem("02", "6 MONTHS"));
		paymentTermCriteria.add(new SelectItem("03", "9 MONTHS"));
		paymentTermCriteria.add(new SelectItem("04", "12 MONTHS"));
		return paymentTermCriteria;
	}

	/**
	 * Sets the payment term criteria.
	 * 
	 * @param paymentTermCriteria
	 *            the new payment term criteria
	 */
	public void setPaymentTermCriteria(List<SelectItem> paymentTermCriteria) {
		this.paymentTermCriteria = paymentTermCriteria;
	}

	/**
	 * Gets the favorite supplier list.
	 * 
	 * @return the favorite supplier list
	 */
	public List<CmFavoriteSupplier> getFavoriteSupplierList() {
		if (favoriteSupplierList == null)
			favoriteSupplierList = new ArrayList<CmFavoriteSupplier>();
		return favoriteSupplierList;
	}

	/**
	 * Sets the favorite supplier list.
	 * 
	 * @param favoriteSupplierList
	 *            the new favorite supplier list
	 */
	public void setFavoriteSupplierList(
			List<CmFavoriteSupplier> favoriteSupplierList) {
		this.favoriteSupplierList = favoriteSupplierList;
	}

	/**
	 * Gets the temp map.
	 * 
	 * @return the temp map
	 */
	public Map getTempMap() {
		return tempMap;
	}

	/**
	 * Sets the temp map.
	 * 
	 * @param tempMap
	 *            the new temp map
	 */
	public void setTempMap(Map tempMap) {
		this.tempMap = tempMap;
	}

	/**
	 * Gets the temp list.
	 * 
	 * @return the temp list
	 */
	public List<CmSupplierItem> getTempList() {
		if (tempList == null)
			tempList = new ArrayList<CmSupplierItem>();
		return tempList;
	}

	/**
	 * Sets the temp list.
	 * 
	 * @param tempList
	 *            the new temp list
	 */
	public void setTempList(List<CmSupplierItem> tempList) {
		this.tempList = tempList;
	}

	/**
	 * Gets the cart list.
	 * 
	 * @return the cart list
	 */
	public List<CmSupplierItem> getCartList() {
		if (cartList == null)
			cartList = new ArrayList<CmSupplierItem>();
		return cartList;
	}

	/**
	 * Sets the cart list.
	 * 
	 * @param cartList
	 *            the new cart list
	 */
	public void setCartList(List<CmSupplierItem> cartList) {
		this.cartList = cartList;
	}

	/**
	 * Gets the selected rejected items.
	 * 
	 * @return the selected rejected items
	 */
	public CmSupplierItem[] getSelectedRejectedItems() {
		return selectedRejectedItems;
	}

	/**
	 * Sets the selected rejected items.
	 * 
	 * @param selectedRejectedItems
	 *            the new selected rejected items
	 */
	public void setSelectedRejectedItems(CmSupplierItem[] selectedRejectedItems) {
		this.selectedRejectedItems = selectedRejectedItems;
	}

	/**
	 * Gets the selected approval items.
	 * 
	 * @return the selected approval items
	 */
	public CmSupplierItem[] getSelectedApprovalItems() {
		return selectedApprovalItems;
	}

	/**
	 * Sets the selected approval items.
	 * 
	 * @param selectedApprovalItems
	 *            the new selected approval items
	 */
	public void setSelectedApprovalItems(CmSupplierItem[] selectedApprovalItems) {
		this.selectedApprovalItems = selectedApprovalItems;
	}

	/**
	 * Gets the selected approved items.
	 * 
	 * @return the selected approved items
	 */
	public CmSupplierItem[] getSelectedApprovedItems() {
		return selectedApprovedItems;
	}

	/**
	 * Sets the selected approved items.
	 * 
	 * @param selectedApprovedItems
	 *            the new selected approved items
	 */
	public void setSelectedApprovedItems(CmSupplierItem[] selectedApprovedItems) {
		this.selectedApprovedItems = selectedApprovedItems;
	}

	/**
	 * Gets the selected published items.
	 * 
	 * @return the selected published items
	 */
	public CmSupplierItem[] getSelectedPublishedItems() {
		return selectedPublishedItems;
	}

	/**
	 * Sets the selected published items.
	 * 
	 * @param selectedPublishedItems
	 *            the new selected published items
	 */
	public void setSelectedPublishedItems(
			CmSupplierItem[] selectedPublishedItems) {
		this.selectedPublishedItems = selectedPublishedItems;
	}

	/**
	 * Gets the rejected item list.
	 * 
	 * @return the rejected item list
	 */
	public List<CmSupplierItem> getRejectedItemList() {
		if (rejectedItemList == null)
			rejectedItemList = new ArrayList<CmSupplierItem>();
		return rejectedItemList;
	}

	/**
	 * Sets the rejected item list.
	 * 
	 * @param rejectedItemList
	 *            the new rejected item list
	 */
	public void setRejectedItemList(List<CmSupplierItem> rejectedItemList) {
		this.rejectedItemList = rejectedItemList;
	}

	/**
	 * Gets the approval item list.
	 * 
	 * @return the approval item list
	 */
	public List<CmSupplierItem> getApprovalItemList() {
		if (approvalItemList == null)
			approvalItemList = new ArrayList<CmSupplierItem>();
		return approvalItemList;
	}

	/**
	 * Sets the approval item list.
	 * 
	 * @param approvalItemList
	 *            the new approval item list
	 */
	public void setApprovalItemList(List<CmSupplierItem> approvalItemList) {
		this.approvalItemList = approvalItemList;
	}

	/**
	 * Gets the approved item list.
	 * 
	 * @return the approved item list
	 */
	public List<CmSupplierItem> getApprovedItemList() {
		if (approvedItemList == null)
			approvedItemList = new ArrayList<CmSupplierItem>();
		return approvedItemList;
	}

	/**
	 * Sets the approved item list.
	 * 
	 * @param approvedItemList
	 *            the new approved item list
	 */
	public void setApprovedItemList(List<CmSupplierItem> approvedItemList) {
		this.approvedItemList = approvedItemList;
	}

	/**
	 * Gets the published item list.
	 * 
	 * @return the published item list
	 */
	public List<CmSupplierItem> getPublishedItemList() {
		if (publishedItemList == null)
			publishedItemList = new ArrayList<CmSupplierItem>();
		return publishedItemList;
	}

	/**
	 * Sets the published item list.
	 * 
	 * @param publishedItemList
	 *            the new published item list
	 */
	public void setPublishedItemList(List<CmSupplierItem> publishedItemList) {
		this.publishedItemList = publishedItemList;
	}

	/**
	 * Gets the supplier item obj.
	 * 
	 * @return the supplier item obj
	 */
	public CmSupplierItem getSupplierItemObj() {
		return supplierItemObj;
	}

	/**
	 * Sets the supplier item obj.
	 * 
	 * @param supplierItemObj
	 *            the new supplier item obj
	 */
	public void setSupplierItemObj(CmSupplierItem supplierItemObj) {
		this.supplierItemObj = supplierItemObj;
	}

	/**
	 * Gets the compare index.
	 * 
	 * @return the compare index
	 */
	public Integer getCompareIndex() {
		return compareIndex;
	}

	/**
	 * Sets the compare index.
	 * 
	 * @param compareIndex
	 *            the new compare index
	 */
	public void setCompareIndex(Integer compareIndex) {
		this.compareIndex = compareIndex;
	}

	/**
	 * Gets the wish lists.
	 * 
	 * @return the wish lists
	 */
	public List<CmWishlist> getWishLists() {
		if (wishLists == null)
			wishLists = new ArrayList<CmWishlist>();
		return wishLists;
	}

	/**
	 * Sets the wish lists.
	 * 
	 * @param wishLists
	 *            the new wish lists
	 */
	public void setWishLists(List<CmWishlist> wishLists) {
		this.wishLists = wishLists;
	}

	/**
	 * Gets the selected maintain item.
	 * 
	 * @return the selected maintain item
	 */
	public CmSupplierItem getSelectedMaintainItem() {
		if (selectedMaintainItem == null)
			selectedMaintainItem = new CmSupplierItem();
		return selectedMaintainItem;
	}

	/**
	 * Sets the selected maintain item.
	 * 
	 * @param selectedMaintainItem
	 *            the new selected maintain item
	 */
	public void setSelectedMaintainItem(CmSupplierItem selectedMaintainItem) {
		this.selectedMaintainItem = selectedMaintainItem;
	}

	/*	*//**
	 * Gets the selected maintain items.
	 * 
	 * @return the selected maintain items
	 */
	/*
	 * public CmSupplierItem[] getSelectedMaintainItems() { return
	 * selectedMaintainItems; }
	 *//**
	 * Sets the selected maintain items.
	 * 
	 * @param selectedMaintainItems
	 *            the new selected maintain items
	 */
	/*
	 * public void setSelectedMaintainItems(CmSupplierItem[]
	 * selectedMaintainItems) { this.selectedMaintainItems =
	 * selectedMaintainItems; }
	 */

	/**
	 * Gets the unspsc.
	 * 
	 * @return the unspsc
	 */
	public CmUnspsc getUnspsc() {
		if (unspsc == null)
			unspsc = new CmUnspsc();
		return unspsc;
	}

	/**
	 * Sets the unspsc.
	 * 
	 * @param unspsc
	 *            the new unspsc
	 */
	public void setUnspsc(CmUnspsc unspsc) {
		this.unspsc = unspsc;
	}

	/**
	 * Gets the supplier item list.
	 * 
	 * @return the supplier item list
	 */
	public List<CmSupplierItem> getSupplierItemList() {
		if (supplierItemList == null)
			supplierItemList = new ArrayList<CmSupplierItem>();
		return supplierItemList;
	}

	/**
	 * Sets the supplier item list.
	 * 
	 * @param supplierItemList
	 *            the new supplier item list
	 */
	public void setSupplierItemList(List<CmSupplierItem> supplierItemList) {
		this.supplierItemList = supplierItemList;
	}

	/**
	 * Gets the selected supplier items.
	 * 
	 * @return the selected supplier items
	 */
	public CmSupplierItem[] getSelectedSupplierItems() {
		return selectedSupplierItems;
	}

	/**
	 * Sets the selected supplier items.
	 * 
	 * @param selectedSupplierItems
	 *            the new selected supplier items
	 */
	public void setSelectedSupplierItems(CmSupplierItem[] selectedSupplierItems) {
		this.selectedSupplierItems = selectedSupplierItems;
	}

	/**
	 * Gets the search ind.
	 * 
	 * @return the search ind
	 */
	public String getSearchInd() {
		return searchInd;
	}

	/**
	 * Sets the search ind.
	 * 
	 * @param searchInd
	 *            the new search ind
	 */
	public void setSearchInd(String searchInd) {
		this.searchInd = searchInd;
	}

	/**
	 * Gets the selected pricing.
	 * 
	 * @return the selected pricing
	 */
	public PricingVO getSelectedPricing() {
		if (selectedPricing == null)
			selectedPricing = new PricingVO();
		return selectedPricing;
	}

	/**
	 * Sets the selected pricing.
	 * 
	 * @param selectedPricing
	 *            the new selected pricing
	 */
	public void setSelectedPricing(PricingVO selectedPricing) {
		this.selectedPricing = selectedPricing;
	}

	/**
	 * Gets the district pricing list.
	 * 
	 * @return the district pricing list
	 */
	public List<PricingVO> getDistrictPricingList() {
		if (districtPricingList == null)
			districtPricingList = new ArrayList<PricingVO>();
		return districtPricingList;
	}

	/**
	 * Sets the district pricing list.
	 * 
	 * @param districtPricingList
	 *            the new district pricing list
	 */
	public void setDistrictPricingList(List<PricingVO> districtPricingList) {
		this.districtPricingList = districtPricingList;
	}

	/**
	 * Gets the pricing list.
	 * 
	 * @return the pricing list
	 */
	public List<CmPricing> getPricingList() {

		if (pricingList == null)
			pricingList = new ArrayList<CmPricing>();
		return pricingList;
	}

	/**
	 * Sets the pricing list.
	 * 
	 * @param pricingList
	 *            the new pricing list
	 */
	public void setPricingList(List<CmPricing> pricingList) {
		this.pricingList = pricingList;
	}

	/**
	 * Gets the pricing criteria.
	 * 
	 * @return the pricing criteria
	 */
	public List<SelectItem> getPricingCriteria() {
		pricingCriteria = new ArrayList<SelectItem>();
		pricingCriteria.add(new SelectItem("01", "NORMAL"));
		pricingCriteria.add(new SelectItem("02", "PROMOTION"));
		pricingCriteria.add(new SelectItem("03", "TIER"));
		pricingCriteria.add(new SelectItem("04", "PROMOTION TIER"));
		return pricingCriteria;
	}

	/**
	 * Sets the pricing criteria.
	 * 
	 * @param pricingCriteria
	 *            the new pricing criteria
	 */
	public void setPricingCriteria(List<SelectItem> pricingCriteria) {
		this.pricingCriteria = pricingCriteria;
	}

	/**
	 * Gets the zonal pricing list.
	 * 
	 * @return the zonal pricing list
	 */
	public List<PricingVO> getZonalPricingList() {
		if (zonalPricingList == null)
			zonalPricingList = new ArrayList<PricingVO>();
		return zonalPricingList;
	}

	/**
	 * Sets the zonal pricing list.
	 * 
	 * @param zonalPricingList
	 *            the new zonal pricing list
	 */
	public void setZonalPricingList(List<PricingVO> zonalPricingList) {
		this.zonalPricingList = zonalPricingList;
	}

	/**
	 * Checks if is district ind.
	 * 
	 * @return true, if is district ind
	 */
	public boolean isDistrictIND() {
		return districtIND;
	}

	/**
	 * Sets the district ind.
	 * 
	 * @param districtIND
	 *            the new district ind
	 */
	public void setDistrictIND(boolean districtIND) {
		this.districtIND = districtIND;
	}

	/**
	 * Gets the item name.
	 * 
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets the item name.
	 * 
	 * @param itemName
	 *            the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Gets the search type.
	 * 
	 * @return the search type
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * Sets the search type.
	 * 
	 * @param searchType
	 *            the new search type
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * Checks if is advance search ind.
	 * 
	 * @return true, if is advance search ind
	 */
	public boolean isAdvanceSearchInd() {
		return advanceSearchInd;
	}

	/**
	 * Sets the advance search ind.
	 * 
	 * @param advanceSearchInd
	 *            the new advance search ind
	 */
	public void setAdvanceSearchInd(boolean advanceSearchInd) {
		this.advanceSearchInd = advanceSearchInd;
	}

	/**
	 * Gets the advance search list.
	 * 
	 * @return the advance search list
	 */
	public List<AdvanceSearchVO> getAdvanceSearchList() {
		return advanceSearchList;
	}

	/**
	 * Sets the advance search list.
	 * 
	 * @param advanceSearchList
	 *            the new advance search list
	 */
	public void setAdvanceSearchList(List<AdvanceSearchVO> advanceSearchList) {
		this.advanceSearchList = advanceSearchList;
	}

	/**
	 * Gets the search criteria.
	 * 
	 * @return the search criteria
	 */
	public List<SelectItem> getSearchCriteria() {
		searchCriteria = new ArrayList<SelectItem>();
		searchCriteria.add(new SelectItem("Supplier", "Supplier Name"));
		searchCriteria.add(new SelectItem("Item", "Item Code"));
		searchCriteria.add(new SelectItem("Contract No", "Contract No"));
		searchCriteria.add(new SelectItem("nature of business",
				"nature of business"));
		searchCriteria.add(new SelectItem("State", "State"));
		searchCriteria.add(new SelectItem("District", "District"));

		return searchCriteria;
	}

	/**
	 * Sets the search criteria.
	 * 
	 * @param searchCriteria
	 *            the new search criteria
	 */
	public void setSearchCriteria(List<SelectItem> searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * Gets the selected supplier item list.
	 * 
	 * @return the selected supplier item list
	 */
	public List<CmSupplierItem> getSelectedSupplierItemList() {
		if (selectedSupplierItemList == null)
			selectedSupplierItemList = new ArrayList<CmSupplierItem>();
		return selectedSupplierItemList;
	}

	/**
	 * Sets the selected supplier item list.
	 * 
	 * @param selectedSupplierItemList
	 *            the new selected supplier item list
	 */
	public void setSelectedSupplierItemList(
			List<CmSupplierItem> selectedSupplierItemList) {
		this.selectedSupplierItemList = selectedSupplierItemList;
	}

	/**
	 * Gets the navigation url.
	 * 
	 * @return the navigation url
	 */
	public String getNavigationURL() {
		return navigationURL;
	}

	/**
	 * Sets the navigation url.
	 * 
	 * @param navigationURL
	 *            the new navigation url
	 */
	public void setNavigationURL(String navigationURL) {
		this.navigationURL = navigationURL;
	}

	/**
	 * Gets the expired item list.
	 * 
	 * @return the expired item list
	 */
	public List<CmSupplierItem> getExpiredItemList() {
		if (expiredItemList == null)
			expiredItemList = new ArrayList<CmSupplierItem>();
		return expiredItemList;
	}

	/**
	 * Sets the expired item list.
	 * 
	 * @param expiredItemList
	 *            the new expired item list
	 */
	public void setExpiredItemList(List<CmSupplierItem> expiredItemList) {
		this.expiredItemList = expiredItemList;
	}

	/**
	 * Gets the selected expired items.
	 * 
	 * @return the selected expired items
	 */
	public CmSupplierItem[] getSelectedExpiredItems() {
		return selectedExpiredItems;
	}

	/**
	 * Sets the selected expired items.
	 * 
	 * @param selectedExpiredItems
	 *            the new selected expired items
	 */
	public void setSelectedExpiredItems(CmSupplierItem[] selectedExpiredItems) {
		this.selectedExpiredItems = selectedExpiredItems;
	}

	/**
	 * Gets the status desc list.
	 * 
	 * @return the status desc list
	 */
	public List<PmStatusDesc> getStatusDescList() {
		if (statusDescList == null)
			statusDescList = new ArrayList<PmStatusDesc>();
		return statusDescList;
	}

	/**
	 * Sets the status desc list.
	 * 
	 * @param statusDescList
	 *            the new status desc list
	 */
	public void setStatusDescList(List<PmStatusDesc> statusDescList) {
		this.statusDescList = statusDescList;
	}

	/**
	 * Gets the item code.
	 * 
	 * @return the item code
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * Sets the item code.
	 * 
	 * @param itemCode
	 *            the new item code
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * Gets the item code filter bean.
	 * 
	 * @return the item code filter bean
	 */
	public SearchFilterBean getItemCodeFilterBean() {
		return itemCodeFilterBean;
	}

	/**
	 * Sets the item code filter bean.
	 * 
	 * @param itemCodeFilterBean
	 *            the new item code filter bean
	 */
	public void setItemCodeFilterBean(SearchFilterBean itemCodeFilterBean) {
		this.itemCodeFilterBean = itemCodeFilterBean;
	}

	/**
	 * Gets the supplier name filter bean.
	 * 
	 * @return the supplier name filter bean
	 */
	public SearchFilterBean getSupplierNameFilterBean() {
		return supplierNameFilterBean;
	}

	/**
	 * Sets the supplier name filter bean.
	 * 
	 * @param supplierNameFilterBean
	 *            the new supplier name filter bean
	 */
	public void setSupplierNameFilterBean(
			SearchFilterBean supplierNameFilterBean) {
		this.supplierNameFilterBean = supplierNameFilterBean;
	}

	/**
	 * Gets the contract no filter bean.
	 * 
	 * @return the contract no filter bean
	 */
	public SearchFilterBean getContractNoFilterBean() {
		return contractNoFilterBean;
	}

	/**
	 * Sets the contract no filter bean.
	 * 
	 * @param contractNoFilterBean
	 *            the new contract no filter bean
	 */
	public void setContractNoFilterBean(SearchFilterBean contractNoFilterBean) {
		this.contractNoFilterBean = contractNoFilterBean;
	}

	/**
	 * Gets the circular no filter bean.
	 * 
	 * @return the circular no filter bean
	 */
	public SearchFilterBean getCircularNoFilterBean() {
		return circularNoFilterBean;
	}

	/**
	 * Sets the circular no filter bean.
	 * 
	 * @param circularNoFilterBean
	 *            the new circular no filter bean
	 */
	public void setCircularNoFilterBean(SearchFilterBean circularNoFilterBean) {
		this.circularNoFilterBean = circularNoFilterBean;
	}

	/**
	 * Gets the nature of business filter bean.
	 * 
	 * @return the nature of business filter bean
	 */
	public SearchFilterBean getNatureOfBusinessFilterBean() {
		return natureOfBusinessFilterBean;
	}

	/**
	 * Sets the nature of business filter bean.
	 * 
	 * @param natureOfBusinessFilterBean
	 *            the new nature of business filter bean
	 */
	public void setNatureOfBusinessFilterBean(
			SearchFilterBean natureOfBusinessFilterBean) {
		this.natureOfBusinessFilterBean = natureOfBusinessFilterBean;
	}

	/**
	 * Gets the ep no filter bean.
	 * 
	 * @return the ep no filter bean
	 */
	public SearchFilterBean getEpNoFilterBean() {
		return epNoFilterBean;
	}

	/**
	 * Sets the ep no filter bean.
	 * 
	 * @param epNoFilterBean
	 *            the new ep no filter bean
	 */
	public void setEpNoFilterBean(SearchFilterBean epNoFilterBean) {
		this.epNoFilterBean = epNoFilterBean;
	}

	/**
	 * Gets the mof no filter bean.
	 * 
	 * @return the mof no filter bean
	 */
	public SearchFilterBean getMofNoFilterBean() {
		return mofNoFilterBean;
	}

	/**
	 * Sets the mof no filter bean.
	 * 
	 * @param mofNoFilterBean
	 *            the new mof no filter bean
	 */
	public void setMofNoFilterBean(SearchFilterBean mofNoFilterBean) {
		this.mofNoFilterBean = mofNoFilterBean;
	}

	/**
	 * Gets the biz reg no filter bean.
	 * 
	 * @return the biz reg no filter bean
	 */
	public SearchFilterBean getBizRegNoFilterBean() {
		return bizRegNoFilterBean;
	}

	/**
	 * Sets the biz reg no filter bean.
	 * 
	 * @param bizRegNoFilterBean
	 *            the new biz reg no filter bean
	 */
	public void setBizRegNoFilterBean(SearchFilterBean bizRegNoFilterBean) {
		this.bizRegNoFilterBean = bizRegNoFilterBean;
	}

	/**
	 * Gets the view supplier item list.
	 * 
	 * @return the view supplier item list
	 */
	public List<VCmSupplierItem> getViewSupplierItemList() {
		if (viewSupplierItemList == null)
			viewSupplierItemList = new ArrayList<VCmSupplierItem>();
		return viewSupplierItemList;
	}

	/**
	 * Sets the view supplier item list.
	 * 
	 * @param viewSupplierItemList
	 *            the new view supplier item list
	 */
	public void setViewSupplierItemList(
			List<VCmSupplierItem> viewSupplierItemList) {
		this.viewSupplierItemList = viewSupplierItemList;
	}

	/**
	 * Checks if is contracted exist.
	 * 
	 * @return true, if is contracted exist
	 */
	public boolean isContractedExist() {
		return contractedExist;
	}

	/**
	 * Sets the contracted exist.
	 * 
	 * @param contractedExist
	 *            the new contracted exist
	 */
	public void setContractedExist(boolean contractedExist) {
		this.contractedExist = contractedExist;
	}

	/**
	 * Gets the state id.
	 * 
	 * @return the state id
	 */
	public long[] getStateId() {
		return stateId;
	}

	/**
	 * Sets the state id.
	 * 
	 * @param stateId
	 *            the new state id
	 */
	public void setStateId(long[] stateId) {
		this.stateId = stateId;
	}

	/**
	 * Gets the district id.
	 * 
	 * @return the district id
	 */
	public long[] getDistrictId() {
		return districtId;
	}

	/**
	 * Sets the district id.
	 * 
	 * @param districtId
	 *            the new district id
	 */
	public void setDistrictId(long[] districtId) {
		this.districtId = districtId;
	}

	/**
	 * Gets the district filter bean.
	 * 
	 * @return the district filter bean
	 */
	public SearchFilterBean getDistrictFilterBean() {
		return districtFilterBean;
	}

	/**
	 * Sets the district filter bean.
	 * 
	 * @param districtFilterBean
	 *            the new district filter bean
	 */
	public void setDistrictFilterBean(SearchFilterBean districtFilterBean) {
		this.districtFilterBean = districtFilterBean;
	}

	/**
	 * Gets the district ls.
	 * 
	 * @return the district ls
	 */
	public List<Long> getDistrictLs() {
		if (districtLs == null)
			districtLs = new ArrayList<Long>();
		return districtLs;
	}

	/**
	 * Sets the district ls.
	 * 
	 * @param districtLs
	 *            the new district ls
	 */
	public void setDistrictLs(List<Long> districtLs) {
		this.districtLs = districtLs;
	}

	/**
	 * Gets the state ls.
	 * 
	 * @return the state ls
	 */
	public List<Long> getStateLs() {
		if (stateLs == null)
			stateLs = new ArrayList<Long>();
		return stateLs;
	}

	/**
	 * Sets the state ls.
	 * 
	 * @param stateLs
	 *            the new state ls
	 */
	public void setStateLs(List<Long> stateLs) {
		this.stateLs = stateLs;
	}

	/**
	 * Gets the state filter bean.
	 * 
	 * @return the state filter bean
	 */
	public SearchFilterBean getStateFilterBean() {
		return stateFilterBean;
	}

	/**
	 * Sets the state filter bean.
	 * 
	 * @param stateFilterBean
	 *            the new state filter bean
	 */
	public void setStateFilterBean(SearchFilterBean stateFilterBean) {
		this.stateFilterBean = stateFilterBean;
	}

	public int getResultTabActiveIndex() {
		return resultTabActiveIndex;
	}

	public void setResultTabActiveIndex(int resultTabActiveIndex) {
		this.resultTabActiveIndex = resultTabActiveIndex;
	}

	/**
	 * Gets the tab active index.
	 * 
	 * @return the tab active index
	 */
	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * Sets the tab active index.
	 * 
	 * @param tabActiveIndex
	 *            the new tab active index
	 */
	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/** The reject reason ids. */
	private ArrayList<Long> rejectReasonIds = new ArrayList<Long>();

	/**
	 * Gets the reject reason ids.
	 * 
	 * @return the reject reason ids
	 */
	public ArrayList<Long> getRejectReasonIds() {
		return rejectReasonIds;
	}

	/**
	 * Sets the reject reason ids.
	 * 
	 * @param rejectReasonIds
	 *            the new reject reason ids
	 */
	public void setRejectReasonIds(ArrayList<Long> rejectReasonIds) {
		this.rejectReasonIds = rejectReasonIds;
	}

	/**
	 * Gets the reject reason.
	 * 
	 * @return the reject reason
	 */
	public String getRejectReason() {
		return rejectReason;
	}

	/**
	 * Sets the reject reason.
	 * 
	 * @param rejectReason
	 *            the new reject reason
	 */
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	/**
	 * Gets the cat type filter bean.
	 * 
	 * @return the cat type filter bean
	 */
	public SearchFilterBean getCatTypeFilterBean() {
		return catTypeFilterBean;
	}

	/**
	 * Sets the cat type filter bean.
	 * 
	 * @param catTypeFilterBean
	 *            the new cat type filter bean
	 */
	public void setCatTypeFilterBean(SearchFilterBean catTypeFilterBean) {
		this.catTypeFilterBean = catTypeFilterBean;
	}

	/**
	 * Gets the supplier zone list.
	 * 
	 * @return the supplier zone list
	 */
	public List<CmSupplierZone> getSupplierZoneList() {
		if (supplierZoneList == null)
			supplierZoneList = new ArrayList<CmSupplierZone>();
		return supplierZoneList;
	}

	/**
	 * Sets the supplier zone list.
	 * 
	 * @param supplierZoneList
	 *            the new supplier zone list
	 */
	public void setSupplierZoneList(List<CmSupplierZone> supplierZoneList) {
		this.supplierZoneList = supplierZoneList;
	}

	/**
	 * Gets the pricing tab index.
	 * 
	 * @return the pricing tab index
	 */
	public Integer getPricingTabIndex() {
		return pricingTabIndex;
	}

	/**
	 * Sets the pricing tab index.
	 * 
	 * @param pricingTabIndex
	 *            the new pricing tab index
	 */
	public void setPricingTabIndex(Integer pricingTabIndex) {
		this.pricingTabIndex = pricingTabIndex;
	}

	/**
	 * Gets the normal pricing index.
	 * 
	 * @return the normal pricing index
	 */
	public Integer getNormalPricingIndex() {
		return normalPricingIndex;
	}

	/**
	 * Sets the normal pricing index.
	 * 
	 * @param normalPricingIndex
	 *            the new normal pricing index
	 */
	public void setNormalPricingIndex(Integer normalPricingIndex) {
		this.normalPricingIndex = normalPricingIndex;
	}

	/**
	 * Gets the tier pricing index.
	 * 
	 * @return the tier pricing index
	 */
	public Integer getTierPricingIndex() {
		return tierPricingIndex;
	}

	/**
	 * Sets the tier pricing index.
	 * 
	 * @param tierPricingIndex
	 *            the new tier pricing index
	 */
	public void setTierPricingIndex(Integer tierPricingIndex) {
		this.tierPricingIndex = tierPricingIndex;
	}

	/**
	 * Gets the zonal pricing index.
	 * 
	 * @return the zonal pricing index
	 */
	public Integer getZonalPricingIndex() {
		return zonalPricingIndex;
	}

	/**
	 * Sets the zonal pricing index.
	 * 
	 * @param zonalPricingIndex
	 *            the new zonal pricing index
	 */
	public void setZonalPricingIndex(Integer zonalPricingIndex) {
		this.zonalPricingIndex = zonalPricingIndex;
	}

	/**
	 * Gets the zonal tier pricing index.
	 * 
	 * @return the zonal tier pricing index
	 */
	public Integer getZonalTierPricingIndex() {
		return zonalTierPricingIndex;
	}

	/**
	 * Sets the zonal tier pricing index.
	 * 
	 * @param zonalTierPricingIndex
	 *            the new zonal tier pricing index
	 */
	public void setZonalTierPricingIndex(Integer zonalTierPricingIndex) {
		this.zonalTierPricingIndex = zonalTierPricingIndex;
	}

	/**
	 * Gets the tier row index.
	 * 
	 * @return the tier row index
	 */
	public int getTierRowIndex() {
		return tierRowIndex;
	}

	/**
	 * Sets the tier row index.
	 * 
	 * @param tierRowIndex
	 *            the new tier row index
	 */
	public void setTierRowIndex(int tierRowIndex) {
		this.tierRowIndex = tierRowIndex;
	}

	/**
	 * Checks if is error4.
	 * 
	 * @return true, if is error4
	 */
	public boolean isError4() {
		return error4;
	}

	/**
	 * Sets the error4.
	 * 
	 * @param error4
	 *            the new error4
	 */
	public void setError4(boolean error4) {
		this.error4 = error4;
	}

	/**
	 * Checks if is error3.
	 * 
	 * @return true, if is error3
	 */
	public boolean isError3() {
		return error3;
	}

	/**
	 * Sets the error3.
	 * 
	 * @param error3
	 *            the new error3
	 */
	public void setError3(boolean error3) {
		this.error3 = error3;
	}

	/**
	 * Checks if is error2.
	 * 
	 * @return true, if is error2
	 */
	public boolean isError2() {
		return error2;
	}

	/**
	 * Sets the error2.
	 * 
	 * @param error2
	 *            the new error2
	 */
	public void setError2(boolean error2) {
		this.error2 = error2;
	}

	/**
	 * Checks if is error1.
	 * 
	 * @return true, if is error1
	 */
	public boolean isError1() {
		return error1;
	}

	/**
	 * Sets the error1.
	 * 
	 * @param error1
	 *            the new error1
	 */
	public void setError1(boolean error1) {
		this.error1 = error1;
	}

	/**
	 * Gets the temp supplier list.
	 * 
	 * @return the temp supplier list
	 */
	public List<VCmSupplierItem> getTempSupplierList() {
		if (tempSupplierList == null)
			tempSupplierList = new ArrayList<VCmSupplierItem>();
		return tempSupplierList;
	}

	/**
	 * Sets the temp supplier list.
	 * 
	 * @param tempSupplierList
	 *            the new temp supplier list
	 */
	public void setTempSupplierList(List<VCmSupplierItem> tempSupplierList) {
		this.tempSupplierList = tempSupplierList;
	}

	/**
	 * Gets the ct request list.
	 * 
	 * @return the ct request list
	 */
	public List<VCmSupplierItem> getCtRequestList() {
		if (ctRequestList == null)
			ctRequestList = new ArrayList<VCmSupplierItem>();
		return ctRequestList;
	}

	/**
	 * Sets the ct request list.
	 * 
	 * @param ctRequestList
	 *            the new ct request list
	 */
	public void setCtRequestList(List<VCmSupplierItem> ctRequestList) {
		this.ctRequestList = ctRequestList;
	}

	/**
	 * Checks if is simple quote flag.
	 * 
	 * @return the simpleQuoteFlag
	 */
	public boolean isSimpleQuoteFlag() {
		return simpleQuoteFlag;
	}

	/**
	 * Checks if is render table view.
	 * 
	 * @return true, if is render table view
	 */
	public boolean isRenderTableView() {
		return renderTableView;
	}

	/**
	 * Sets the render table view.
	 * 
	 * @param renderTableView
	 *            the new render table view
	 */
	public void setRenderTableView(boolean renderTableView) {
		this.renderTableView = renderTableView;
	}

	/**
	 * Checks if is render grid view.
	 * 
	 * @return true, if is render grid view
	 */
	public boolean isRenderGridView() {
		return renderGridView;
	}

	/**
	 * Sets the render grid view.
	 * 
	 * @param renderGridView
	 *            the new render grid view
	 */
	public void setRenderGridView(boolean renderGridView) {
		this.renderGridView = renderGridView;
	}

	/**
	 * Gets the selected ids.
	 * 
	 * @return the selected ids
	 */
	public Map<Long, Boolean> getSelectedIds() {
		return selectedIds;
	}

	/**
	 * Sets the selected ids.
	 * 
	 * @param selectedIds
	 *            the selected ids
	 */
	public void setSelectedIds(Map<Long, Boolean> selectedIds) {
		this.selectedIds = selectedIds;
	}

	/**
	 * Sets the simple quote flag.
	 * 
	 * @param simpleQuoteFlag
	 *            the simpleQuoteFlag to set
	 */
	public void setSimpleQuoteFlag(boolean simpleQuoteFlag) {
		this.simpleQuoteFlag = simpleQuoteFlag;
	}

	/**
	 * Checks if is error5.
	 * 
	 * @return true, if is error5
	 */
	public boolean isError5() {
		return error5;
	}

	/**
	 * Sets the error5.
	 * 
	 * @param error5
	 *            the new error5
	 */
	public void setError5(boolean error5) {
		this.error5 = error5;
	}

	/**
	 * Checks if is error6.
	 * 
	 * @return true, if is error6
	 */
	public boolean isError6() {
		return error6;
	}

	/**
	 * Sets the error6.
	 * 
	 * @param error6
	 *            the new error6
	 */
	public void setError6(boolean error6) {
		this.error6 = error6;
	}

	/**
	 * Gets the zonal tier row index.
	 * 
	 * @return the zonal tier row index
	 */
	public int getZonalTierRowIndex() {
		return zonalTierRowIndex;
	}

	/**
	 * Sets the zonal tier row index.
	 * 
	 * @param zonalTierRowIndex
	 *            the new zonal tier row index
	 */
	public void setZonalTierRowIndex(int zonalTierRowIndex) {
		this.zonalTierRowIndex = zonalTierRowIndex;
	}

	/**
	 * Gets the clone cm supplier item.
	 * 
	 * @return the clone cm supplier item
	 */
	public CmSupplierItem getCloneCmSupplierItem() {
		return cloneCmSupplierItem;
	}

	/**
	 * Sets the clone cm supplier item.
	 * 
	 * @param cloneCmSupplierItem
	 *            the new clone cm supplier item
	 */
	public void setCloneCmSupplierItem(CmSupplierItem cloneCmSupplierItem) {
		this.cloneCmSupplierItem = cloneCmSupplierItem;
	}

	/**
	 * Gets the clone index.
	 * 
	 * @return the clone index
	 */
	public int getCloneIndex() {
		return cloneIndex;
	}

	/**
	 * Sets the clone index.
	 * 
	 * @param cloneIndex
	 *            the new clone index
	 */
	public void setCloneIndex(int cloneIndex) {
		this.cloneIndex = cloneIndex;
	}

	/**
	 * Checks if is display catalogue.
	 * 
	 * @return true, if is display catalogue
	 */
	public boolean isDisplayCatalogue() {
		return displayCatalogue;
	}

	/**
	 * Sets the display catalogue.
	 * 
	 * @param displayCatalogue
	 *            the new display catalogue
	 */
	public void setDisplayCatalogue(boolean displayCatalogue) {
		this.displayCatalogue = displayCatalogue;
	}

	/**
	 * Gets the current date.
	 * 
	 * @return the current date
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * Sets the current date.
	 * 
	 * @param currentDate
	 *            the new current date
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * Gets the upload dir.
	 * 
	 * @return the upload dir
	 */
	public String getUploadDir() {
		return uploadDir;
	}

	/**
	 * Sets the upload dir.
	 * 
	 * @param uploadDir
	 *            the new upload dir
	 */
	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	/**
	 * Gets the option list.
	 * 
	 * @return the option list
	 */
	public List<SelectItem> getOptionList() {
		optionList.clear();
		optionList.add(new SelectItem("1", "Add to CR"));
		optionList.add(new SelectItem("2", "Add to Simple Quote"));
		optionList.add(new SelectItem("3", "Add to Compare"));
		return optionList;
	}

	/**
	 * Sets the option list.
	 * 
	 * @param optionList
	 *            the new option list
	 */
	public void setOptionList(List<SelectItem> optionList) {
		this.optionList = optionList;
	}

	/**
	 * Gets the option list value.
	 * 
	 * @return the option list value
	 */
	public Integer getOptionListValue() {
		return optionListValue;
	}

	/**
	 * Sets the option list value.
	 * 
	 * @param optionListValue
	 *            the new option list value
	 */
	public void setOptionListValue(Integer optionListValue) {
		this.optionListValue = optionListValue;
	}

	/**
	 * Gets the selected items.
	 * 
	 * @return the selected items
	 */
	public VCmSupplierItem[] getSelectedItems() {
		return selectedItems;
	}

	/**
	 * Sets the selected items.
	 * 
	 * @param selectedItems
	 *            the new selected items
	 */
	public void setSelectedItems(VCmSupplierItem[] selectedItems) {
		this.selectedItems = selectedItems;
	}

	/**
	 * Gets the previous screen.
	 * 
	 * @return the previous screen
	 */
	public int getPreviousScreen() {
		return previousScreen;
	}

	/**
	 * Sets the previous screen.
	 * 
	 * @param previousScreen
	 *            the new previous screen
	 */
	public void setPreviousScreen(int previousScreen) {
		this.previousScreen = previousScreen;
	}

	/**
	 * Checks if is error7.
	 * 
	 * @return true, if is error7
	 */
	public boolean isError7() {
		return error7;
	}

	/**
	 * Sets the error7.
	 * 
	 * @param error7
	 *            the new error7
	 */
	public void setError7(boolean error7) {
		this.error7 = error7;
	}

	/**
	 * Checks if is error8.
	 * 
	 * @return true, if is error8
	 */
	public boolean isError8() {
		return error8;
	}

	/**
	 * Sets the error8.
	 * 
	 * @param error8
	 *            the new error8
	 */
	public void setError8(boolean error8) {
		this.error8 = error8;
	}

	/**
	 * Checks if is error9.
	 * 
	 * @return true, if is error9
	 */
	public boolean isError9() {
		return error9;
	}

	/**
	 * Sets the error9.
	 * 
	 * @param error9
	 *            the new error9
	 */
	public void setError9(boolean error9) {
		this.error9 = error9;
	}

	/**
	 * Checks if is price type tariff.
	 * 
	 * @return true, if is price type tariff
	 */
	public boolean isPriceTypeTariff() {
		return priceTypeTariff;
	}

	/**
	 * Sets the price type tariff.
	 * 
	 * @param priceTypeTariff
	 *            the new price type tariff
	 */
	public void setPriceTypeTariff(boolean priceTypeTariff) {
		this.priceTypeTariff = priceTypeTariff;
	}

	/**
	 * Gets the physical contract no filter bean.
	 * 
	 * @return the physical contract no filter bean
	 */
	public SearchFilterBean getPhysicalContractNoFilterBean() {
		return physicalContractNoFilterBean;
	}

	/**
	 * Sets the physical contract no filter bean.
	 * 
	 * @param physicalContractNoFilterBean
	 *            the new physical contract no filter bean
	 */
	public void setPhysicalContractNoFilterBean(
			SearchFilterBean physicalContractNoFilterBean) {
		this.physicalContractNoFilterBean = physicalContractNoFilterBean;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public IPagingDataModel<CmSupplierItem> getDraftSearchDataModel() {
		return draftSearchDataModel;
	}

	public void setDraftSearchDataModel(
			IPagingDataModel<CmSupplierItem> draftSearchDataModel) {
		this.draftSearchDataModel = draftSearchDataModel;
	}

	public IPagingDataModel<CmSupplierItem> getApprovalSearchDataModel() {
		return approvalSearchDataModel;
	}

	public void setApprovalSearchDataModel(
			IPagingDataModel<CmSupplierItem> approvalSearchDataModel) {
		this.approvalSearchDataModel = approvalSearchDataModel;
	}

	public IPagingDataModel<CmSupplierItem> getApprovedSearchDataModel() {
		return approvedSearchDataModel;
	}

	public void setApprovedSearchDataModel(
			IPagingDataModel<CmSupplierItem> approvedSearchDataModel) {
		this.approvedSearchDataModel = approvedSearchDataModel;
	}

	public IPagingDataModel<CmSupplierItem> getRejectSearchDataModel() {
		return rejectSearchDataModel;
	}

	public void setRejectSearchDataModel(
			IPagingDataModel<CmSupplierItem> rejectSearchDataModel) {
		this.rejectSearchDataModel = rejectSearchDataModel;
	}

	public IPagingDataModel<CmSupplierItem> getPublishSearchDataModel() {
		return publishSearchDataModel;
	}

	public void setPublishSearchDataModel(
			IPagingDataModel<CmSupplierItem> publishSearchDataModel) {
		this.publishSearchDataModel = publishSearchDataModel;
	}

	public IPagingDataModel<CmSupplierItem> getExpiredSearchDataModel() {
		return expiredSearchDataModel;
	}

	public void setExpiredSearchDataModel(
			IPagingDataModel<CmSupplierItem> expiredSearchDataModel) {
		this.expiredSearchDataModel = expiredSearchDataModel;
	}

	public SearchFilterBean getKeyWordFilterBean() {
		return keyWordFilterBean;
	}

	public void setKeyWordFilterBean(SearchFilterBean keyWordFilterBean) {
		this.keyWordFilterBean = keyWordFilterBean;
	}

	public List<CmSupplierItem> getDraftSupplierItemList() {
		if (draftSupplierItemList == null)
			draftSupplierItemList = new ArrayList<CmSupplierItem>();
		return draftSupplierItemList;
	}

	public void setDraftSupplierItemList(
			List<CmSupplierItem> draftSupplierItemList) {
		this.draftSupplierItemList = draftSupplierItemList;
	}

	public int getDraftTabPageSize() {
		return draftTabPageSize;
	}

	public void setDraftTabPageSize(int draftTabPageSize) {
		this.draftTabPageSize = draftTabPageSize;
	}

	public int getPendingApprovalTabPageSize() {
		return pendingApprovalTabPageSize;
	}

	public void setPendingApprovalTabPageSize(int pendingApprovalTabPageSize) {
		this.pendingApprovalTabPageSize = pendingApprovalTabPageSize;
	}

	public int getApprovedTabPageSize() {
		return approvedTabPageSize;
	}

	public void setApprovedTabPageSize(int approvedTabPageSize) {
		this.approvedTabPageSize = approvedTabPageSize;
	}

	public int getPublishTabPageSize() {
		return publishTabPageSize;
	}

	public void setPublishTabPageSize(int publishTabPageSize) {
		this.publishTabPageSize = publishTabPageSize;
	}

	public int getRejectedTabPageSize() {
		return rejectedTabPageSize;
	}

	public void setRejectedTabPageSize(int rejectedTabPageSize) {
		this.rejectedTabPageSize = rejectedTabPageSize;
	}

	public int getExpiredTabPageSize() {
		return expiredTabPageSize;
	}

	public void setExpiredTabPageSize(int expiredTabPageSize) {
		this.expiredTabPageSize = expiredTabPageSize;
	}

	public List<CmSupplierItem> getCloneCmSupplierItemLs() {
		if (cloneCmSupplierItemLs == null)
			cloneCmSupplierItemLs = new ArrayList<CmSupplierItem>();
		return cloneCmSupplierItemLs;
	}

	public void setCloneCmSupplierItemLs(
			List<CmSupplierItem> cloneCmSupplierItemLs) {
		this.cloneCmSupplierItemLs = cloneCmSupplierItemLs;
	}

	public boolean isSupplierItemcheckBoxAll() {
		return supplierItemcheckBoxAll;
	}

	public void setSupplierItemcheckBoxAll(boolean supplierItemcheckBoxAll) {
		this.supplierItemcheckBoxAll = supplierItemcheckBoxAll;
	}

	public boolean isRefreshDraftSuppItemFlag() {
		return refreshDraftSuppItemFlag;
	}

	public void setRefreshDraftSuppItemFlag(boolean refreshDraftSuppItemFlag) {
		this.refreshDraftSuppItemFlag = refreshDraftSuppItemFlag;
	}

	public boolean isError99() {
		return error99;
	}

	public void setError99(boolean error99) {
		this.error99 = error99;
	}

	public int getZonalRowIndex() {
		return zonalRowIndex;
	}

	public void setZonalRowIndex(int zonalRowIndex) {
		this.zonalRowIndex = zonalRowIndex;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier
	 *            the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	/**
	 * @return the availableNoList
	 */
	public List<String> getAvailableNoList() {
		return availableNoList;
	}

	/**
	 * @param availableNoList the availableNoList to set
	 */
	public void setAvailableNoList(List<String> availableNoList) {
		this.availableNoList = availableNoList;
	}

	/**
	 * @return the scheduleNo
	 */
	public String getScheduleNo() {
		return scheduleNo;
	}

	/**
	 * @param scheduleNo the scheduleNo to set
	 */
	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}

	public String getExpiryAlert() {
		return expiryAlert;
	}

	public void setExpiryAlert(String expiryAlert) {
		this.expiryAlert = expiryAlert;
	}

	public SearchFilterBean getContractTitleFilterBean() {
		return contractTitleFilterBean;
	}

	public void setContractTitleFilterBean(SearchFilterBean contractTitleFilterBean) {
		this.contractTitleFilterBean = contractTitleFilterBean;
	}

	public SearchFilterBean getLoaNoFilterBean() {
		return loaNoFilterBean;
	}

	public void setLoaNoFilterBean(SearchFilterBean loaNoFilterBean) {
		this.loaNoFilterBean = loaNoFilterBean;
	}

	public SearchFilterBean getLoaPhysicalNoFilterBean() {
		return loaPhysicalNoFilterBean;
	}

	public void setLoaPhysicalNoFilterBean(SearchFilterBean loaPhysicalNoFilterBean) {
		this.loaPhysicalNoFilterBean = loaPhysicalNoFilterBean;
	}

	public boolean isG2gFlag() {
		return g2gFlag;
	}

	public void setG2gFlag(boolean g2gFlag) {
		this.g2gFlag = g2gFlag;
	}

	/**
	 * @return the unspscObj
	 */
	public CmUnspsc getUnspscObj() {
		if(unspscObj==null)
			unspscObj = new CmUnspsc();
		return unspscObj;
	}

	/**
	 * @param unspscObj the unspscObj to set
	 */
	public void setUnspscObj(CmUnspsc unspscObj) {
		this.unspscObj = unspscObj;
	}

	
	
	
	
	

}

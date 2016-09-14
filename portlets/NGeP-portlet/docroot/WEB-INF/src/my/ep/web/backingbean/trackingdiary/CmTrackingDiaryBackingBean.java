package my.ep.web.backingbean.trackingdiary;

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
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "cmTrackingDiaryBackingBean")
@ViewScoped
public class CmTrackingDiaryBackingBean extends
		BaseBackingBean<CmTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(CmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CATALOG_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CATALOG_DETAIL_SEARCH = "/xhtml/cm/catalogue/addCatalogViewTrackingDairy.xhtml";
	public static final String BACK_TO_PREV = "/xhtml/trackingdiary/cmTrackingDiarySearch.xhtml";

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	public CmTrackingDiaryBackingBean() {
	}
	
	public void search() {
		System.out.println("pageChangeListener");
		if ((getManagedBean().getSearchItemCodeValue() != null && getManagedBean().getSearchItemCodeValue().isEmpty()) 
				&& (getManagedBean().getSearchItemNameValue() != null && getManagedBean().getSearchItemNameValue().isEmpty()) 
				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
				) {
			throw new EpException(MessageConst.ERROR_716006);
		}
		getManagedBean().getCmDocumentDataModel().reloadData();
	}

	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"CM"));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCmSupplierItem().getSupplierItemId()));
			//searchBean.getDataFilterBeans().add(new SearchFilterBean("actionedBy",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCmSupplierItem().getSmSupplier().getSmPersonnel().getPmUser().getUserId()));
				
			System.out.println("TrackingDiaryReqDTO" +getManagedBean().getCmSupplierItem().getCmItem().getItemId());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
				CmTrackingDiaryBackingBean bb = new CmTrackingDiaryBackingBean();
				for(PmTrackingDiary diary : resDTO.getResultBean().getResultList()){
					bb.getManagedBean().setDocId(diary.getDocId());
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

	public String goToTrackingDiarySearch(CmSupplierItem cmSupplierItem) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
		getManagedBean().setCmSupplierItem(cmSupplierItem);
		
		CatalogueBackingBean bb = new CatalogueBackingBean();
		SearchBean<PmTrackingDiary> searchBean = new SearchBean<PmTrackingDiary>();
		
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();		
		CatalogueResDTO resDTO = new CatalogueResDTO();


		reqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
		resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);

		bb.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
		
		CatalogueBackingBean.retriveSupplierGstInfomation(resDTO.getSupplierItem().getSmSupplier());
		
		bb.getManagedBean().setDisplayCatalogue(true);
		
		TrackingDiaryReqDTO reqDTOPM = new TrackingDiaryReqDTO();
		searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"CM"));
		searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCmSupplierItem().getSupplierItemId()));
		//searchBean.getDataFilterBeans().add(new SearchFilterBean("actionedBy",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCmSupplierItem().getSmSupplier().getSmPersonnel().getPmUser().getUserId()));
			
		System.out.println("TrackingDiaryReqDTO item Id:" +getManagedBean().getCmSupplierItem().getCmItem().getItemId());

		reqDTOPM.setSearchBean(searchBean);
		TrackingDiaryResDTO resDTOPM = new TrackingDiaryResDTO();
		
			ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
			resDTOPM = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTOPM);
			
			for(PmTrackingDiary diary : resDTOPM.getResultBean().getResultList()){
				this.getManagedBean().setDocId(diary.getDocId());
			}
			
			CatalogueReqDTO reqDTOZone = new CatalogueReqDTO();
			
			reqDTOZone.setSupplierId(resDTO.getSupplierItem().getSmSupplier().getSupplierId());
			
			resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTOZone);
			System.out.println("size:"+resDTO.getSupplierZoneLs().size());
			bb.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
			
	/*		//sort Tier
			if(bb.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && !bb.getManagedBean().getSelectedMaintainItem().getCmTierPricings().isEmpty()){
				groupPricesByTier(bb.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), "S");
			}
			//sort Tier	Promotion		
			if(bb.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings() != null && !bb.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().isEmpty()){
				groupPricesByTier(bb.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), "P");
			}
			//sort Zonal Tier			
			if(bb.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && !bb.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().isEmpty()){
				groupPricesByTier(bb.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), "S");
			}
			//sort Zonal Tier Promotion
			if(bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings() != null && !bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().isEmpty()){
				groupPricesByTier(bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), "P");
			}*/
			return NAV_TO_CATALOG_DETAIL_SEARCH;
		//return NAV_TO_CATALOG_DETAIL_SEARCH + "?faces-redirect=true";
	}
	


	public void reset(){
		this.getManagedBean().setSearchItemCodeValue(null);
		this.getManagedBean().setSearchItemNameValue(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setSearchSupplierValue(null);
		this.getManagedBean().setSearchCreatedFromDuration(null);
		this.getManagedBean().setSearchCreatedToDuration(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	public SelectItem[] getStatus() {
		SelectItem[] items = null;

		try {
			items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
			items[1] = new SelectItem(new String("ACT"), new String("ACTIVE"));
			items[2] = new SelectItem(new String("NACT"), new String("NON ACTIVE"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/**
	 * to return the all Supplier Item's status as a SelectItem list. 
	 * @return
	 */
	public List<SelectItem> getItemStatusOptions() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		options.add(new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale())));
		options.add(new SelectItem(CmConst.ITEM_STATUS_DRAFT, "Draf"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_PENDING_APPROVAL, "Menunggu Kelulusan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_APPROVED, "Diluluskan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_REJECTED, "Ditolak"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_PUBLISHED, "Diiklankan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_EXPIRED, "Tamat Tempoh"));
		
		return options;
	}

	public String backToPrev() {
		System.out.println("***************BACK_TO_PREV**********"+BACK_TO_PREV);
		return BACK_TO_PREV + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}
	
	
	public void preRenderCMTrackingDairyView() {
		log.info(".................preRenderCMTrackingDairyView................");
		
		this.getManagedBean().reset();
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "cmTrackingDiarySearch");
	}
	
	private <T extends CmPricing> void groupPricesByTier(List<T> cmPricings, String stand_id){

		if(stand_id.equalsIgnoreCase("S")){
			//Order Tier or Zonal Tier
			Collections.sort(cmPricings, new Comparator<CmPricing>() {
				@Override
				public int compare(CmPricing p1, CmPricing p2) {

					//Sorts by 'stand_group_id' property
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
					//					return p1.getStand_group_id()<p2.getStand_group_id()?-1:p1.getStand_group_id()==p2.getStand_group_id()?doSecodaryOrderSort(p1,p2):1;
				}

				//If 'stand_group_id' property is equal sorts by 'fromQty' property
				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			});
		} else if(stand_id.equalsIgnoreCase("P")){
			//Order Promotion Tier or Zonal Promotion Tier
			Collections.sort(cmPricings, new Comparator<CmPricing>() {
				@Override
				public int compare(CmPricing p1, CmPricing p2) {
					//Sorts by 'stand_group_id' property
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
					//					return p1.getStand_group_id()<p2.getStand_group_id()?-1:doSecodaryOrderSort(p1,p2);
				}

				//If 'stand_group_id' property is equal sorts by 'promo_group_id' property
				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					if(p1.getPromo_group_id()<p2.getPromo_group_id())
						return -1;
					else if(p1.getPromo_group_id()==p2.getPromo_group_id())
						return doTernaryOrderSort(p1, p2);
					else
						return 1;

					//				    return p1.getPromo_group_id()<p2.getPromo_group_id()?-1:doTernaryOrderSort(p1, p2);
				}

				//If 'promo_group_id' property is equal sorts by 'fromQty' property
				public int doTernaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			});

		}


	}
	
	

}


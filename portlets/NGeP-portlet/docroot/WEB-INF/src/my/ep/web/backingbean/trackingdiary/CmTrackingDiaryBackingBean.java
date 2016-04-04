package my.ep.web.backingbean.trackingdiary;

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
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
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
			
		return NAV_TO_CATALOG_DETAIL_SEARCH + "?faces-redirect=true";
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

		return NAV_TO_CATALOG_DETAIL_SEARCH + "?faces-redirect=true";
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
	
	

}


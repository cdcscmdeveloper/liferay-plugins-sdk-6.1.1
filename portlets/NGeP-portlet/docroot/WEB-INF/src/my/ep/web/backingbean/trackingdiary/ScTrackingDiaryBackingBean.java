package my.ep.web.backingbean.trackingdiary;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.ScTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "scTrackingDiaryBackingBean")
@RequestScoped
public class ScTrackingDiaryBackingBean extends
		BaseBackingBean<ScTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(ScTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SC_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "scTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	public ScTrackingDiaryBackingBean() {
	}
	public SelectItem[] getDocumentType() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			items = new SelectItem[5];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			items[1] = new SelectItem(new String("SQ"), new String("Simple Quote"));
			items[2] = new SelectItem(new String("PI"), new String("Purchase Inquiry"));
			items[3] = new SelectItem(new String("PD"), new String("Purchase In Default"));
			items[4] = new SelectItem(new String("RN"), new String("Request Note"));
			items[5] = new SelectItem(new String("QT"), new String("Quotation Tender"));
			items[6] = new SelectItem(new String("BD"), new String("Online Bidding"));
			items[7] = new SelectItem(new String("LI"), new String("Letter of Intent"));
			items[8] = new SelectItem(new String("LA"), new String("Letter of Acceptance"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	public void search() {
//		if ((extensionCode != null && extensionCode.isEmpty()) && ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle.isEmpty()))) {
//			throw new EpException(MessageConst.ERROR_716006);
//		}
		System.out.println("ScTrackingDiaryBackingBean search");
		getManagedBean().getScRNDocumentDataModel().reloadData();
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
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchDocNo()));
			System.out.println("Sc TrackingDiary docNo  " + getManagedBean().getSearchDocNo());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

	public String goToTrackingDiarySearch(Object[] scObj) {
		getManagedBean().setSearchDocNo(scObj[1].toString());
		searchTrackingDiaryDataModel.reloadData();
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String reset() {
		return null;
	}

	public String backToPrev() {

		return NAV_TO_SC_SEARCH;
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}


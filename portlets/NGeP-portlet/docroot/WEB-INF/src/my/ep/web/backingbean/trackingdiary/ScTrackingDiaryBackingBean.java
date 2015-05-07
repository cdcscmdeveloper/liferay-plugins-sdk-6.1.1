package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
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
//			items[1] = new SelectItem(new String("SQ"), new String("Simple Quote"));
//			items[2] = new SelectItem(new String("PI"), new String("Purchase Inquiry"));
//			items[3] = new SelectItem(new String("PD"), new String("Purchase In Default"));
//			items[4] = new SelectItem(new String("RN"), new String("Request Note"));
			items[1] = new SelectItem(new String("QT"), new String("Quotation Tender"));
			items[2] = new SelectItem(new String("BD"), new String("Online Bidding"));
			items[3] = new SelectItem(new String("LI"), new String("Letter of Intent"));
			items[4] = new SelectItem(new String("LA"), new String("Letter of Acceptance"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}

	public SelectItem[] searchStatusDesc() throws NamingException {
		StatusReqDTO reqDTO = new StatusReqDTO();
		IStatusSO statusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.moduleCode",
				SearchOperator.OPERATOR_EQUALS.value(),
				"SC"));
		List<String> docTypeList = new ArrayList<String>();
		docTypeList.add(getManagedBean().getSearchDocumentType());
//		docTypeList.add("FA");
		System.out.println("searchStatusDesc");
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.docType",
				SearchOperator.OPERATOR_IN.value(),
				docTypeList));
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"languageCode",
				SearchOperator.OPERATOR_EQUALS.value(),
				"en"));
		
		reqDTO.setStatusDescSearchBean(searchBean);
		StatusResDTO resDTO = statusSO.searchStatusDesc(reqDTO);
		
		List<PmStatusDesc> list = resDTO.getStatusDescList();
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = resDTO.getStatusDescList();

			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : statusDescs) {
				items[i++] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());
			}
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
		System.out.println("ScTrackingDiaryBackingBean search");
		if ((null == getManagedBean().getSearchDocumentType()) 
//				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
//				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
				) {
			throw new EpException(MessageConst.ERROR_776101);
		}
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
//			userSO.retrieveUserByUserId;
			Object[] scObj = getManagedBean().getScObj();

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),scObj[3]));
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),scObj[0]));
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),scObj[3]);
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),scObj[0]);
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(),scObj[3]);
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(),scObj[0]);
			
			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);
			System.out.println("Sc TrackingDiary docNo  " +scObj[3]+ scObj[0]);

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
		getManagedBean().setScObj(scObj);
		searchTrackingDiaryDataModel.reloadData();
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public void reset(){
		this.getManagedBean().setSearchDocNo(null);
		this.getManagedBean().setSearchDocumentType(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setSearchDocumentTitle(null);
		this.getManagedBean().setSearchDocumentToDuration(null);
		this.getManagedBean().setSearchDocumentFromDuration(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	public String backToPrev() {

		return NAV_TO_SC_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}


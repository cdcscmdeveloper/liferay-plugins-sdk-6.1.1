package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.ScTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;

@ManagedBean(name = "scTrackingDiaryBackingBean")
@RequestScoped
public class ScTrackingDiaryBackingBean extends
		BaseBackingBean<ScTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(ScTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SC_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "/xhtml/trackingdiary/scTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public ScTrackingDiaryBackingBean() {
	}

	public List<SelectItem> getDocumentType() {
		List<SelectItem> items = null;
		List<String> docTypeList = null;

		try {

			items = new ArrayList<SelectItem>();
			docTypeList = new ArrayList<String>();
			
			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));
			
			docTypeList.add(ParameterConst.DOCTYPE_QUOTATION);
			docTypeList.add(ParameterConst.DOCTYPE_BIDDING_AWARD);
			docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			
			for (String docType : docTypeList) {
				items.add(new SelectItem(docType, ResourceBundleUtils.getMessage(
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
			}
		}  catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}

	public List<SelectItem> searchStatusDesc() throws NamingException {
		List<SelectItem> items = null;

		try {
			items = new ArrayList<SelectItem>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));
			
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance().getStatusDescList(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), getManagedBean().getSearchDocumentType());
			for (PmStatusDesc statusDesc : statusDescs) {
				items.add(new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName()));
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

	public void goToTrackingDiarySearch(Object[] scObj) {
		
		log.info("SC goToTrackingDiarySearch");
		getManagedBean().setScObj(scObj);
		searchTrackingDiaryDataModel.reloadData();
		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(scObj[1] != null?scObj[1].toString():null);
		diarySearchBackingBean.getManagedBean().setDocType(scObj[3]!= null?scObj[3].toString():null);
		diarySearchBackingBean.getManagedBean().setStatusId(scObj[4]!= null?
				new Long(scObj[4].toString()):null);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_DETAIL_SEARCH);
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

	public void backToPrev() {
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getFromPageUrl());
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}


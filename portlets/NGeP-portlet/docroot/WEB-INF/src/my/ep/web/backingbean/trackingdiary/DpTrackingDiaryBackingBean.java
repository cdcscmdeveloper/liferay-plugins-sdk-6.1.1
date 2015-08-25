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
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
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
import my.ep.web.managedbean.trackingdiary.DpTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;

@ManagedBean(name = "dpTrackingDiaryBackingBean")
@RequestScoped
public class DpTrackingDiaryBackingBean extends
		BaseBackingBean<DpTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(DpTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SC_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "dpTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public DpTrackingDiaryBackingBean() {
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
			
			docTypeList.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
			docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
			docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
			docTypeList.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
			docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			
			for (String docType : docTypeList) {
				items.add(new SelectItem(docType, ResourceBundleUtils.getMessage(
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
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

	public List<SelectItem> searchStatusDesc() throws NamingException {
		List<SelectItem> items = null;
		try {
			items = new ArrayList<SelectItem>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));
			
			StatusReqDTO reqDTO = new StatusReqDTO();
			
			SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmStatus.moduleCode",
					SearchOperator.OPERATOR_EQUALS.value(),
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase()));
			List<String> docTypeList = new ArrayList<String>();
			List<Long> statusIdList = new ArrayList<Long>();
			docTypeList.add(getManagedBean().getSearchDocumentType());
			//docTypeList.add("FT");
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmStatus.docType",
					SearchOperator.OPERATOR_IN.value(),
					docTypeList));		
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"languageCode",
					SearchOperator.OPERATOR_EQUALS.value(),
					getFacesScopes().getUserLocale().getLanguage()));
			
			if(getManagedBean().getSearchDocumentType().
					equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)) {
				statusIdList.add(QTStatusConst.LOA_PENDING_CONTRACT_SIGNING);
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"pmStatus.statusId",
						SearchOperator.OPERATOR_NOT_IN.value(),
						statusIdList));
			}
			
			reqDTO.setStatusDescSearchBean(searchBean);
			StatusResDTO resDTO = getStatusSO().searchStatusDesc(reqDTO);
			
			List<PmStatusDesc> statusDescs = resDTO.getStatusDescList();
			
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
		System.out.println("DpTrackingDiaryBackingBean search");
		if ((null == getManagedBean().getSearchDocumentType()) 
//				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
//				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
				) {
			throw new EpException(MessageConst.ERROR_776101);
		}
		getManagedBean().getDpDocumentDataModel().reloadData();
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
			Object[] dpObj = getManagedBean().getDpObj();

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),dpObj[3]));
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),dpObj[0]));
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),dpObj[3]);
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),dpObj[0]);
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(),dpObj[3]);
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(),dpObj[0]);
			
			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);
			System.out.println("Dp TrackingDiary docNo  " +dpObj[0]+dpObj[3]+ dpObj[1]);
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

	public String goToTrackingDiarySearch(Object[] dpObj) {
		getManagedBean().setDpObj(dpObj);
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
	
	/**
	 * Gets the status so.
	 *
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

}


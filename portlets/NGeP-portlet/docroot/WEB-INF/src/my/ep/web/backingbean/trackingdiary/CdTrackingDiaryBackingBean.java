package my.ep.web.backingbean.trackingdiary;

import java.math.BigDecimal;
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
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "cdTrackingDiaryBackingBean")
@ViewScoped
public class CdTrackingDiaryBackingBean extends
BaseBackingBean<CdTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CODIFICATION_SEARCH = "/xhtml/trackingdiary/cdTrackingDiarySearch.xhtml";
	public static final String NAV_TO_CODIFICATION_DETAIL_SEARCH = "/xhtml/cm/codification/cdTrackingDairyViewPage.xhtml";

	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	public CdTrackingDiaryBackingBean() {
	}

	public void search() {
		if ((getManagedBean().getSearchDocumentNoValue() != null && getManagedBean().getSearchDocumentNoValue().isEmpty()) 
				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
				) {
			throw new EpException(MessageConst.ERROR_716006);
		}
		System.out.println("pageChangeListener");
		getManagedBean().getCdDocumentDataModel().reloadData();
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

			Object[] cdObj = getManagedBean().getObject();

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"CD"));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),cdObj[5]));
			//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),cdObj[1]));
			System.out.println("Cd TrackingDiary docId  " + cdObj[5]);

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			CdTrackingDiaryBackingBean bb = new CdTrackingDiaryBackingBean();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);

				for(PmTrackingDiary diary:resDTO.getResultBean().getResultList()){

					bb.getManagedBean().setDocId(diary.getDocId());

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//			System.out.println("SearchCDTrackingDiaryPagingDataModel TotalRecord====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
		}
	}

	public String goToTrackingDiarySearch(Object[] unspscObj) {

		getManagedBean().setObject(unspscObj);

		SearchBean<PmTrackingDiary> searchBean = new SearchBean<PmTrackingDiary>();
		TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();

		List<String> docTypeLs = new ArrayList<String>();
		docTypeLs.add("CU");
		docTypeLs.add("CE");

		searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_IN.value(),docTypeLs));
		searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),unspscObj[5]));
		//		searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),cdObj[1]));
		System.out.println("Cd TrackingDiary docId  " + unspscObj[5]);

		reqDTO.setSearchBean(searchBean);
		TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();


		ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
		resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);

		BigDecimal temp = (BigDecimal) unspscObj[7];
		Long statusId =  temp.longValue();
		for(PmTrackingDiary diary:resDTO.getResultBean().getResultList()){

			this.getManagedBean().setDocId(diary.getDocId());
			this.getManagedBean().setDocNo(diary.getDocNo());

		}



		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();

		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setStatusId(statusId);
		StatusResDTO resStatusDTO = statusSO
				.retrieveStatusDescById(reqStatusDTO);

		this.getManagedBean().setDocStatus(
				resStatusDTO.getPmStatusDesc() != null ? resStatusDTO
						.getPmStatusDesc().getStatusName() : "Review");



		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		System.out.println("this.getManagedBean().getDocNo()"+this.getManagedBean().getDocNo());
		codeDTO.setDocNo(this.getManagedBean().getDocNo());
		RequestCodeResDTO codeResDTO = requestSO.retrieveUnspscCodeReviewByDocNo(codeDTO);
		System.out.println("getRequestLs:"+codeResDTO.getRequestLs().size());


		this.getManagedBean().setReviewExtCodeList(null);
		this.getManagedBean().setProceedExtCodeList(null);
		this.getManagedBean().setRecommendExtCodeList(null);


		if (codeResDTO.getRequestLs() != null
				&& codeResDTO.getRequestLs().size() > 0) {

			for (CmRequestCode code : codeResDTO.getRequestLs()) {
				if(code.getDocNo()!=null && code.getDocNo().trim().length()>0){
					System.out.println("subSTring:"+code.getDocNo().substring(0, 3));
					if(code.getDocNo().substring(0, 3).compareToIgnoreCase("RNE")==0){
						this.getManagedBean().setUnspscInd(false);
						System.out.println("**extension**");

						boolean colorRecommend = false;
						boolean brandRecommend = false;
						boolean typeRecommend = false;
						boolean meaRecommend = false;
						if (!code.isRequestColor()) { 
							colorRecommend = true;
						}
						if (!code.isRequestBrand()){
							brandRecommend = true;	
						}

						if (!code.isRequestType()){

							typeRecommend = true;
						}

						if (!code.isRequestMeasurement()){

							meaRecommend = true;
						}

						if(colorRecommend && brandRecommend && typeRecommend && meaRecommend){

							System.out.println("recomemd:"+code.getRequestId());
							this.getManagedBean().getRecommendExtCodeList().add(code);
						}else{

							System.out.println("process:"+code.getRequestId());
							this.getManagedBean().getProceedExtCodeList().add(code);


						}
					}else{
						System.out.println("****unspsc***");
						this.getManagedBean().setUnspscInd(true);
						this.getManagedBean().setRequestCode(code);
						this.getManagedBean().getReviewExtCodeList().add(code);

						if(this.getManagedBean().getReviewExtCodeList().get(0).getCmRecommendCodes()!=null 
								&& this.getManagedBean().getReviewExtCodeList().get(0).getCmRecommendCodes().size() >0){
							this.getManagedBean().setUnspscRecommendInd(true);
						}
					}
				}
			}
		}

		//searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CODIFICATION_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public void reset(){
		this.getManagedBean().setSearchDocumentNoValue(null);
		this.getManagedBean().setSearchSupplierValue(null);
		this.getManagedBean().setSearchUNSPSCExtensionCodeValue(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	public SelectItem[] getStatus() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));

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

	public String backToPrev() {

		return NAV_TO_CODIFICATION_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}



	public void preRenderCodificationTrackingDairyView() {
		log.info(".................preRenderCodificationTrackingDairyView................");

		this.getManagedBean().reset();


		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "cdTrackingDiarySearch");
	}
}


package my.ep.web.backingbean.trackingdiary;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmRemark;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cdTrackingDiaryBackingBean")
@ViewScoped
public class CdTrackingDiaryBackingBean extends BaseBackingBean<CdTrackingDiaryManagedBean> {
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CODIFICATION_SEARCH = "/xhtml/trackingdiary/cdTrackingDiarySearch.xhtml";
	public static final String NAV_TO_CODIFICATION_DETAIL_SEARCH = "/xhtml/cm/codification/cdTrackingDairyViewPage.xhtml";
	
	public CdTrackingDiaryBackingBean() {
	}

	public void search() {
		if ((getManagedBean().getSearchDocumentNoValue() != null && getManagedBean().getSearchDocumentNoValue().isEmpty()) 
				&& (getManagedBean().getSearchUNSPSCExtensionCodeValue() != null && getManagedBean().getSearchUNSPSCExtensionCodeValue().isEmpty()) 
				&& (getManagedBean().getSearchSupplierValue() != null && getManagedBean().getSearchSupplierValue().isEmpty())
				) {
			throw new EpException(MessageConst.ERROR_716006);
		}
		getManagedBean().getCdDocumentDataModel().reloadData();
	}

	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {

			Object[] cdObj = getManagedBean().getObject();

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(),"CD"));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId", SearchOperator.OPERATOR_EQUALS.value(),cdObj[5]));
		
			if (cdObj[5] == null) {
				log.info("CD tracking diary search filter empty");
				return new SearchResultBean<PmTrackingDiary>();
			}
			
			setSearchBeanDescription(searchBean,"CD", cdObj[5]);
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
				e.printStackTrace();
			}
		
			return resDTO.getResultBean();
		}
	}
	public boolean isCatalogueAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CM_ADMIN");
	}

	public boolean isSupplierUser() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}

	public boolean isSupplierAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isCodeAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_OFFICER");
	}

	public boolean isRequisitioner() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	public boolean isCdCommittee() { 
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_COMMITTEE");
	}
	public String userImagePathWithDefaultImage(String loginId){
		log.info("userImagePathWithDefaultImage");
		String imagePath = "";
	
		imagePath = userImagePath(loginId);	
		if(StringUtils.isBlank(imagePath)){
			imagePath = ProfileManagementConst.DEFAULT_PORTRAIT;
		}
		return imagePath;
	}
	public boolean isAllSupplier() {

		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_USER")
				|| fs.hasRoleUser("MOF_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("BASIC_SUPPLIER_USER")
				|| fs.hasRoleUser("BASIC_SUPPLIER_ADMIN")
				|| fs.hasRoleUser("SUPPLIER_TEMP")
				|| fs.hasRoleUser("G2G_USER")
				|| fs.hasRoleUser("G2G_ADMIN")
				|| fs.hasRoleUser("GOVT_SELLER")
				|| fs.hasRoleUser("CM_USER");
	}
	private void loadRemark(){
		FacesScopes fs = new FacesScopes();
		List<CmRemark> cmRemarkList = new ArrayList<CmRemark>();
		
		if(!isAllSupplier()){
			ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO remarkReqDTO = new CodificationReqDTO();
			remarkReqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			CodificationResDTO remarkResDTO = codificationSO.getCodificationRemarks(remarkReqDTO);
			
			if( remarkResDTO.getRemarkList()!=null &&  remarkResDTO.getRemarkList().size()>0){
				for(CmRemark remark: remarkResDTO.getRemarkList()){
					remark.setBrandNew(false);
					cmRemarkList.add(remark);
				}
			}
		}else{
			List<CmRemark> remarkList = getLastRemarkBeforeCompletion(this.getManagedBean().getRequestCode());
			if(remarkList!=null && remarkList.size() > 0){
				for(CmRemark remark: remarkList){
					remark.setBrandNew(false);
					cmRemarkList.add(remark);
				}
			}
		}
		this.getManagedBean().setSelectedRemarks(cmRemarkList);
	}
	
	public String goToTrackingDiarySearch(Object[] unspscObj) {

		getManagedBean().setObject(unspscObj);
		
		BigDecimal tempDocId = (BigDecimal) unspscObj[5];
		Long docId = tempDocId.longValue();
		for(int i = 0; i<unspscObj.length;i++){
			log.info("unspscObj:" + unspscObj[i]);
		}
		this.getManagedBean().setDocId(docId);
		this.getManagedBean().setDocNo((String) unspscObj[1]);
		BigDecimal tempStatusId = (BigDecimal) unspscObj[7];
		Long statusId =  tempStatusId.longValue();
		
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
		log.info("doc no:" + this.getManagedBean().getDocNo());
		codeDTO.setDocNo(this.getManagedBean().getDocNo());
		RequestCodeResDTO codeResDTO = requestSO.retrieveUnspscCodeReviewByDocNo(codeDTO);
	
		this.getManagedBean().setReviewExtCodeList(null);
		this.getManagedBean().setProceedExtCodeList(null);
		this.getManagedBean().setRecommendExtCodeList(null);

		if (codeResDTO.getRequestLs() != null
				&& codeResDTO.getRequestLs().size() > 0) {

			for (CmRequestCode code : codeResDTO.getRequestLs()) {
				if(code.getDocNo()!=null && code.getDocNo().trim().length()>0){
					if(code.getDocNo().substring(0, 3).compareToIgnoreCase("RNE")==0){
						this.getManagedBean().setUnspscInd(false);
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
						this.getManagedBean().setRequestCode(code);
						if(colorRecommend && brandRecommend && typeRecommend && meaRecommend) {
							this.getManagedBean().getRecommendExtCodeList().add(code);
							this.getManagedBean().getProceedExtCodeList().add(code);						
						} else {
							this.getManagedBean().getProceedExtCodeList().add(code);
						}
					} else {
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
		loadRemark();
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
		this.getManagedBean().reset();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "cdTrackingDiarySearch");
	}
	
	/**
	 * set search bean description for tracking purpose
	 *
	 */
	private void setSearchBeanDescription(ISearchBean<PmTrackingDiary> searchBean, String docType, Object docId) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[docType=%s]", docType));
		sb.append(String.format("[docId=%s]", docId));
		searchBean.setDescription("searchTrackingDiary" + sb.toString() + "[Caller:CdTrackingDiaryBackingBean.SearchTrackingDiaryPagingDataModel]");
	}
	
	public List<CmRemark> getLastRemarkBeforeCompletion(CmRequestCode requestCode){
		log.info("Passed request : " +requestCode.getDocNo());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setRequestCode(requestCode);
		CodificationResDTO resDTO = codificationSO.getLastRemarkBeforeCompletion(reqDTO);
		return resDTO.getRemarkList();
	}
	



}


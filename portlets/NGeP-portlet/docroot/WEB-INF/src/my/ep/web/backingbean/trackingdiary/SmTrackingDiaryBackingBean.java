package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.SmTrackingDiaryManagedBean;
import my.ep.web.managedbean.trackingdiary.SmTrackingDiaryManagedBean.SearchPagingDataModel;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "smTrackingDiaryBackingBean")
@RequestScoped
public class SmTrackingDiaryBackingBean extends
		BaseBackingBean<SmTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(SmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SMSUPPLIER_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_SMSUPPLIER_DETAIL_SEARCH = "smTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	public SmTrackingDiaryBackingBean() {
	}
	
	public void search() {
		if ((null != getManagedBean().getApplicationNo() && getManagedBean().getApplicationNo().isEmpty()) 
				&& (null != getManagedBean().getMOFAccountNo() && getManagedBean().getMOFAccountNo().isEmpty()) 
				&& (null != getManagedBean().getSupplierName() && getManagedBean().getSupplierName().isEmpty())
				&& (null == getManagedBean().getApplicationType())) {
			throw new EpException(MessageConst.ERROR_716006);
		}
		log.info("SmTrackingDiaryBackingBean search");
		getManagedBean().getSmDocumentDataModel().reloadData();
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
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),"SR"));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSmAppl().getApplId()));
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSmSupplier().getSmAppl().getApplNo()));
			log.info("getSmDocumentDataModel docNo " + getManagedBean().getSmAppl().getApplNo());

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

	public String goToTrackingDiarySearch(SmAppl smAppl) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
//		getManagedBean().setSmSupplier(smSupplier);
		getManagedBean().setSmAppl(smAppl);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_SMSUPPLIER_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public void reset(){
		this.getManagedBean().setApplicationNo(null);
		this.getManagedBean().setApplicationType(null);
		this.getManagedBean().setMOFAccountNo(null);
		this.getManagedBean().setSupplierName(null);
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	public String backToPrev() {

		return NAV_TO_SMSUPPLIER_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}


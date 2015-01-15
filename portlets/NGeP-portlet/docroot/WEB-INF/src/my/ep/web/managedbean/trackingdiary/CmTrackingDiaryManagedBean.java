package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.trackingdiary.CdTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
@ManagedBean(name = "cmTrackingDiaryManagedBean")
@SessionScoped
public class CmTrackingDiaryManagedBean extends BaseManagedBean { 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);

	private String searchItemCodeValue;
	private String searchItemNameValue;
	private String searchSupplierValue; 
	private Long searchStatusId;
	private Date searchCreatedFromDuration;
	private Date searchCreatedToDuration;

	private IPagingDataModel<CmUnspsc> cmDocumentDataModel 	= new SearchPagingDataModel();
	private SearchFilterBean titleFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());

//	public SearchFilterBean getTitleFilterBean() {
//		return titleFilterBean;
//	}
//
//	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
//		this.titleFilterBean = titleFilterBean;
//	}

	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("supplierItemID", false);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {

			ICodificationSO unspscSO = null;
			try {
				System.out.println("pageChangeListener");
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);

				titleFilterBean.setSearchField("unspscTitle");
				titleFilterBean.setSearchValue("aa");
				searchBean.getDataFilterBeans().add(titleFilterBean);


				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				System.out.println("*********after***********" + resDTO.getResultBean().getTotalRecord());
				return resDTO.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				unspscSO = null;
			}
			return new SearchResultBean();
		}

	}

	public IPagingDataModel<CmUnspsc> getCmDocumentDataModel() {
		return cmDocumentDataModel;
	}

	public void setCmDocumentDataModel(IPagingDataModel<CmUnspsc> cmDocumentDataModel) {
		this.cmDocumentDataModel = cmDocumentDataModel;
	}

	public String getSearchItemCodeValue() {
		return searchItemCodeValue;
	}

	public void setSearchItemCodeValue(String searchItemCodeValue) {
		this.searchItemCodeValue = searchItemCodeValue;
	}

	public String getSearchItemNameValue() {
		return searchItemNameValue;
	}

	public void setSearchItemNameValue(String searchItemNameValue) {
		this.searchItemNameValue = searchItemNameValue;
	}

	public String getSearchSupplierValue() {
		return searchSupplierValue;
	}

	public void setSearchSupplierValue(String searchSupplierValue) {
		this.searchSupplierValue = searchSupplierValue;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public Date getSearchCreatedFromDuration() {
		return searchCreatedFromDuration;
	}

	public void setSearchCreatedFromDuration(Date searchCreatedFromDuration) {
		this.searchCreatedFromDuration = searchCreatedFromDuration;
	}

	public Date getSearchCreatedToDuration() {
		return searchCreatedToDuration;
	}

	public void setSearchCreatedToDuration(Date searchCreatedToDuration) {
		this.searchCreatedToDuration = searchCreatedToDuration;
	}


}

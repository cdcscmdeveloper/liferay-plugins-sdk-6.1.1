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
@ManagedBean(name = "cdTrackingDiaryManagedBean")
@SessionScoped
public class CdTrackingDiaryManagedBean extends BaseManagedBean { 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);

	private String searchDocumentNoValue;
	private String searchSupplierValue; 
	private String searchUNSPSCExtensionCodeValue;
	private Long searchStatusId;

	private CmUnspsc cmUnspsc;
	private IPagingDataModel<CmUnspsc> cdDocumentDataModel 	= new SearchPagingDataModel();
	private SearchFilterBean titleFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());

	public CmUnspsc getCmUnspsc() {
		return cmUnspsc;
	}

	public void setCmUnspsc(CmUnspsc cmUnspsc) {
		this.cmUnspsc = cmUnspsc;
	}
	public SearchFilterBean getTitleFilterBean() {
		return titleFilterBean;
	}

	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
		this.titleFilterBean = titleFilterBean;
	}
	public IPagingDataModel<CmUnspsc> getCdDocumentDataModel() {
		return cdDocumentDataModel;
	}

	public void setCdDocumentDataModel(IPagingDataModel<CmUnspsc> cdDocumentDataModel) {
		this.cdDocumentDataModel = cdDocumentDataModel;
	}
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
	public String getSearchDocumentNoValue() {
		return searchDocumentNoValue;
	}

	public void setSearchDocumentNoValue(String searchDocumentNoValue) {
		this.searchDocumentNoValue = searchDocumentNoValue;
	}

	public String getSearchSupplierValue() {
		return searchSupplierValue;
	}

	public void setSearchSupplierValue(String searchSupplierValue) {
		this.searchSupplierValue = searchSupplierValue;
	}

	public String getSearchUNSPSCExtensionCodeValue() {
		return searchUNSPSCExtensionCodeValue;
	}

	public void setSearchUNSPSCExtensionCodeValue(
			String searchUNSPSCExtensionCodeValue) {
		this.searchUNSPSCExtensionCodeValue = searchUNSPSCExtensionCodeValue;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}


}

package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.web.backingbean.trackingdiary.CdTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
@ManagedBean(name = "cdTrackingDiaryManagedBean")
@SessionScoped
public class CdTrackingDiaryManagedBean extends BaseManagedBean { 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);

	private String searchDocumentNoValue;
	private String searchSupplierValue; 
	private String searchUNSPSCExtensionCodeValue;
	private Long searchStatusId;
	private Object[] object;
	
	private IPagingDataModel<Object[]> cdDocumentDataModel 	= new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID", false);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {

			ICodificationSO unspscSO = null;
			try {
				System.out.println("pageChangeListener");
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				if(getSearchDocumentNoValue()!=null && getSearchDocumentNoValue()!="") {
					System.out.println("getSearchDocumentNoValue:"+getSearchDocumentNoValue());
				
					reqDTO.setDocNo(getSearchDocumentNoValue());
				}
				if(getSearchUNSPSCExtensionCodeValue()!=null && getSearchUNSPSCExtensionCodeValue()!="") {
					System.out.println("getSearchItemCodeValue:"+getSearchUNSPSCExtensionCodeValue());
					
					reqDTO.setExtensionCode(getSearchUNSPSCExtensionCodeValue());
				}
				if(getSearchSupplierValue()!=null && getSearchSupplierValue()!="") {
					System.out.println("getSearchSupplierValue");
					
					reqDTO.setSupplierName(getSearchSupplierValue());
				}
//				if(getSearchStatusId()!=null) {
//					System.out.println("getSearchStatusId"+getSearchStatusId());
//					reqDTO.setst(getSearchStatusId());
//				}
				reqDTO.setExtSearchBean(searchBean);
				System.out.println("*********b4***********");
				CodificationResDTO resDTO = unspscSO.retrieveCodificationRequest(reqDTO);
				System.out.println("*********after***********" + resDTO.getCodificationRequestResultBean().getTotalRecord());
				return resDTO.getCodificationRequestResultBean();
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

	public Object[] getObject() {
		return object;
	}

	public void setObject(Object[] object) {
		this.object = object;
	}

	public IPagingDataModel<Object[]> getCdDocumentDataModel() {
		return cdDocumentDataModel;
	}

	public void setCdDocumentDataModel(IPagingDataModel<Object[]> cdDocumentDataModel) {
		this.cdDocumentDataModel = cdDocumentDataModel;
	}

	public void resetDocumentDataModel() {
		this.cdDocumentDataModel = new SearchPagingDataModel();
	}
	
	
	public void reset(){
		this.setSearchDocumentNoValue(null);
		this.setSearchSupplierValue(null);
		this.setSearchUNSPSCExtensionCodeValue(null);
		this.setSearchStatusId(null);
		this.setCdDocumentDataModel(new SearchPagingDataModel());
	}
	
	
	
}

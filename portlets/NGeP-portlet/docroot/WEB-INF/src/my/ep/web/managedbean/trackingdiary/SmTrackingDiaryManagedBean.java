package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "smTrackingDiaryManagedBean")
@SessionScoped
public class SmTrackingDiaryManagedBean<PmTrackingDiary> extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MOFAccountNo;
	private String applicationNo;
	private String supplierName;
	private String applicationType;
	private SmSupplier smSupplier;
	public IPagingDataModel<SmSupplier> smDocumentDataModel = new SearchPagingDataModel();
	
	public class SearchPagingDataModel extends PagingDataModel<SmSupplier> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("smSupplierTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {

			ISupplierSearchSO smSupplierSO = null;
			try {
				System.out.println("ISearchResultBean<SmSupplier> pageChangeListener");
				smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
						.getServiceObject("sm", ISupplierSearchSO.class);

				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSearchBean(searchBean);
				
				if(getMOFAccountNo()!=null && getMOFAccountNo()!="") {
					System.out.println("getMOFAccountNo");
					reqDTO.setMofAccountNo(getMOFAccountNo());
				}
				if(getSupplierName()!=null && getSupplierName()!="") {
					System.out.println("getSupplierName");
					reqDTO.setCompanyName(getSupplierName());
				}
				if(getApplicationNo()!=null && getApplicationNo()!="") {
					System.out.println("getApplicationNo"+getApplicationNo());
					reqDTO.setApplnNo(getApplicationNo());
				}
				if(getApplicationType()!=null && getApplicationType()!="") {
					System.out.println("getApplicationNo");
					reqDTO.setApplType(getApplicationType());
				}
				System.out.println("*********b4***********");
				SupplierSearchResDTO res = smSupplierSO.retriveSupplierProfiles(reqDTO);

//				System.out.println("*********after***********"+res.getResultBean().getTotalRecord());
				return res.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				smSupplierSO = null;
			}
			return new SearchResultBean();
		}
	}
	public IPagingDataModel<SmSupplier> getSmDocumentDataModel() {
		return smDocumentDataModel;
	}
	public void setSmDocumentDataModel(
			IPagingDataModel<SmSupplier> smDocumentDataModel) {
		this.smDocumentDataModel = smDocumentDataModel;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getMOFAccountNo() {
		return MOFAccountNo;
	}
	public void setMOFAccountNo(String mOFAccountNo) {
		MOFAccountNo = mOFAccountNo;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

}

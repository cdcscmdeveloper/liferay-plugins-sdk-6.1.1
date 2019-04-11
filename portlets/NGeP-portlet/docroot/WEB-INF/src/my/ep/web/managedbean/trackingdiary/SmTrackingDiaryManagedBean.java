package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "smTrackingDiaryManagedBean")
@SessionScoped
public class SmTrackingDiaryManagedBean<PmTrackingDiary> extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SmTrackingDiaryManagedBean.class);
	
	private String MOFAccountNo;
	private String applicationNo;
	private String supplierName;
	private String applicationType;
	private SmSupplier smSupplier;
	private SmAppl smAppl;
	public IPagingDataModel<SmAppl> smDocumentDataModel = new SearchPagingDataModel();

	@ManagedProperty(value="#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	public class SearchPagingDataModel extends PagingDataModel<SmAppl> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("smSupplierTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<SmAppl> pageChangeListener(
				ISearchBean<SmAppl> searchBean) {			
			
			ISmApplRevisionSO applRevisionSO = null;
			try {
				applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
						.getServiceObject("sm", ISmApplRevisionSO.class);
				
				SmApplRevisionReqDTO reqDTO2 = new SmApplRevisionReqDTO();
				reqDTO2.setSearchBean(searchBean);
				
				if(getMOFAccountNo()!=null && getMOFAccountNo()!="") {
					log.debug("getMOFAccountNo");
					reqDTO2.setMofAccountNo(getMOFAccountNo());
				}
				if(getSupplierName()!=null && getSupplierName()!="") {
					log.debug("getSupplierName");
					reqDTO2.setCompanyName(getSupplierName());
				}
				if(getApplicationNo()!=null && getApplicationNo()!="") {
					log.info("getApplicationNo"+getApplicationNo());
					reqDTO2.setApplicationNo(getApplicationNo());
				}
				if(getApplicationType()!=null && getApplicationType()!="") {
					log.debug("getApplicationNo");
					reqDTO2.setApplicationType(getApplicationType());
				}
				
				SmApplRevisionResDTO resDTO2 = applRevisionSO.retrieveApplicationHistoryForTrackingDiary(reqDTO2);
				return resDTO2.getResultBean();
			} finally {
				applRevisionSO = null;
			}
			
//			return new SearchResultBean();
		}
	}
	public IPagingDataModel<SmAppl> getSmDocumentDataModel() {
		return smDocumentDataModel;
	}
	public void setSmDocumentDataModel(
			IPagingDataModel<SmAppl> smDocumentDataModel) {
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

	public void resetDocumentDataModel() {
		this.smDocumentDataModel = new SearchPagingDataModel();
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

}

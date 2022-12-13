package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.trackingdiary.CdTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
@ManagedBean(name = "cmTrackingDiaryManagedBean")
@SessionScoped
public class CmTrackingDiaryManagedBean extends BaseManagedBean { 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);

	private String searchItemCodeValue;
	private String searchItemNameValue;
	private String searchSupplierValue; 
	private String searchStatusId;
	private Date searchCreatedFromDuration;
	private Date searchCreatedToDuration;
	private CmSupplierItem cmSupplierItem;
	private Long docId;

	private IPagingDataModel<Object[]> cmDocumentDataModel = new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("supplierItemID", false);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {

			ICatalogueSO catSO = null;
			try {
				log.info("pageChangeListener");
				catSO = (ICatalogueSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICatalogueSO.class);

				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				if(getSearchSupplierValue()!=null &&getSearchSupplierValue()!="") {
					log.info("getSupplierName");
					reqDTO.setSupplierName(getSearchSupplierValue());
				}
				if(getSearchItemCodeValue()!=null && getSearchItemCodeValue()!="") {
					log.info("getSearchItemCodeValue");
					reqDTO.setItemCode(getSearchItemCodeValue());
				}
				if(getSearchItemNameValue()!=null && getSearchItemNameValue()!="") {
					log.info("getSearchItemCodeValue");
					reqDTO.setItemName(getSearchItemNameValue());
				}
				if(getSearchStatusId()!=null && getSearchStatusId()!="") {
					log.info("getSearchStatusId"+getSearchStatusId());
					reqDTO.setItemStatus(getSearchStatusId());
				}
				
				UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
				if(bb.isAllSupplier()){
					log.info("*********is supplier***********");
					SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
					log.info("smSupp.getSupplierId():"+smSupp.getSupplierId());
					reqDTO.setSupplierId(smSupp.getSupplierId());
				}
				
				reqDTO.setAuditSearchBean(searchBean);
				log.info("*********b4***********"+getSearchItemCodeValue() );
				CatalogueResDTO resDTO = catSO.searchSupplierItemAudit(reqDTO);

				log.info("*********after***********" + resDTO.getSuppItemAuditResultBean().getTotalRecord());
				return resDTO.getSuppItemAuditResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				catSO = null;
			}
			return new SearchResultBean();
		}

	}

	public IPagingDataModel<Object[]> getCmDocumentDataModel() {
		return cmDocumentDataModel;
	}

	public void setCmDocumentDataModel(IPagingDataModel<Object[]> cmDocumentDataModel) {
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

	public String getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(String searchStatusId) {
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

	public CmSupplierItem getCmSupplierItem() {
		return cmSupplierItem;
	}

	public void setCmSupplierItem(CmSupplierItem cmSupplierItem) {
		this.cmSupplierItem = cmSupplierItem;
	}

	public void resetDocumentDataModel() {
		this.cmDocumentDataModel = new SearchPagingDataModel();
	}
	
	public void reset(){
		this.setSearchItemCodeValue(null);
		this.setSearchItemNameValue(null);
		this.setSearchStatusId(null);
		this.setSearchSupplierValue(null);
		this.setSearchCreatedFromDuration(null);
		this.setSearchCreatedToDuration(null);	
		this.setCmDocumentDataModel(new SearchPagingDataModel());
		log.info("****************end****************");
		
	
	}

	/**
	 * @return the docId
	 */
	public Long getDocId() {
		return docId;
	}

	/**
	 * @param docId the docId to set
	 */
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	
	

}

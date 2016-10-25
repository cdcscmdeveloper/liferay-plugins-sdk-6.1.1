package my.ep.web.managedbean.sm.rejectionacknowlegdementletter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.rejectionackletter.RejectionAckLetterReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sm.rejectionacknowlegdementletter.RejectionAckLetterBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "rejectionAckLetterMB")
@SessionScoped
public class RejectionAckLetterManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(RejectionAckLetterManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private String supplierType;
	private Long regStatusId;
	private String mofNo;
	private String epNo;
	private String companyName;
	private String regNo;
	private String applType;
	private String applNo;
	private Long zoneId;
	private Long applStatusId;
	
	private SmAppl selectedAppl;
	private List<PmStatusDesc> regStatusDescList;
	
	private IPagingDataModel<SmAppl> rejectionAckDataModel = new RejectionAckPagingDataModel();
	private boolean searchAction;

	public RejectionAckLetterManagedBean(){
		
	}
	
	private class RejectionAckPagingDataModel extends PagingDataModel<SmAppl> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RejectionAckPagingDataModel() {
			super("rejectionAckLetterDtId");
		}

		@Override
		protected ISearchResultBean<SmAppl> pageChangeListener(
				ISearchBean<SmAppl> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSearchBean(searchBean);
			if(searchAction){
				try {
					return getRejectionAckLetterResultBean(reqDTO);
					//return null;
				} catch (Exception ex) {
					log.info("******pageChangeListener exception*******" + ex.getMessage());
					return new SearchResultBean();
				} finally {
					reqDTO = null;
				}
			}else{
				return new SearchResultBean();
			}
		}

		/*private ISearchResultBean<SmSupplier> getRejectionAckLetterResultBean(RejectionAckLetterReqDTO reqDTO) {
			log.info("******supplierType*******" + supplierType);
			log.info("******mofNo*******" + mofNo);
			log.info("******epNo*******" + epNo);
			log.info("******regStatusId*******" + regStatusId);
			log.info("******regNo*******" + regNo);
			log.info("******zoneId*******" + zoneId);
			log.info("******applType*******" + applType);
			log.info("******applNo*******" + applNo);
			
			reqDTO.setSupplierType(supplierType);
			reqDTO.setMofNo(mofNo);
			reqDTO.setEpNo(epNo);
			reqDTO.setRegStatusId(regStatusId);
			reqDTO.setSupplierType(regNo);
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setZoneId(zoneId);
			reqDTO.setApplType(applType);
			reqDTO.setApplNo(applNo);
			
			return SmServiceLookup.getRejectionAckLetterSO().retrieveSupplierBySearchCriteria(reqDTO).getSupplierResultBean();

		}*/
		
		private ISearchResultBean<SmAppl> getRejectionAckLetterResultBean(SmApplRevisionReqDTO reqDTO) {
			log.info("******supplierType*******" + supplierType);
			log.info("******mofNo*******" + mofNo);
			log.info("******epNo*******" + epNo);
			log.info("******regStatusId*******" + regStatusId);
			log.info("******regNo*******" + regNo);
			log.info("******zoneId*******" + zoneId);
			log.info("******applType*******" + applType);
			log.info("******applNo*******" + applNo);
			
			SmSupplier supplier = new SmSupplier();
			supplier.setCompanyName(companyName);
			supplier.setRegNo(regNo);
			
			reqDTO.setMofAccountNo(mofNo);
			reqDTO.setEpNo(epNo);
			reqDTO.setPmRegStatusId(regStatusId);
			reqDTO.setSupplierType(supplierType);
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(zoneId);
			reqDTO.setApplicationType(applType);
			reqDTO.setSmSupplier(supplier);
			reqDTO.setApplicationNo(applNo);
			
			reqDTO.setApplicationStatusLs(new ArrayList<Long>());
			reqDTO.getApplicationStatusLs().add(SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION);
			reqDTO.getApplicationStatusLs().add(SupplierManagementConst.PM_STATUS_APPROVER_REJECTED_APPLICATION);
			reqDTO.getApplicationStatusLs().add(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS);
			
			return SmServiceLookup.getSmApplRevisionSO().retrieveApplicationHistoryBySupplierSearchCriteria(reqDTO)
					.getResultBean();
		}
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public Long getRegStatusId() {
		return regStatusId;
	}

	public void setRegStatusId(Long regStatusId) {
		this.regStatusId = regStatusId;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public String getEpNo() {
		return epNo;
	}

	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	}

	public String getApplNo() {
		return applNo;
	}

	public void setApplNo(String applNo) {
		this.applNo = applNo;
	}

	public IPagingDataModel<SmAppl> getRejectionAckDataModel() {
		return rejectionAckDataModel;
	}

	public void setRejectionAckDataModel(IPagingDataModel<SmAppl> rejectionAckDataModel) {
		this.rejectionAckDataModel = rejectionAckDataModel;
	}

	public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public Long getApplStatusId() {
		return applStatusId;
	}

	public void setApplStatusId(Long applStatusId) {
		this.applStatusId = applStatusId;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}

	public SmAppl getSelectedAppl() {
		return selectedAppl;
	}

	public void setSelectedAppl(SmAppl selectedAppl) {
		this.selectedAppl = selectedAppl;
	}

	public boolean isSearchAction() {
		return searchAction;
	}

	public void setSearchAction(boolean searchAction) {
		this.searchAction = searchAction;
	}

}

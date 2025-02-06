package my.ep.web.managedbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.audit.AuditSupplierManagementResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

@ManagedBean(name = "AuditSupplierManagementViewManagedBean")
@SessionScoped
public class AuditSupplierManagementViewManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();

	private AuSmSearchBean auSmSearchBean;
	private boolean isLazy = true;
	private int activeIndex;
	private transient IPagingDataModel<AuditSupplierManagementResDTO> searchAuSm = new SearchAuSmPagingDataModel();
	private List<AuditSupplierManagementResDTO> bankInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> userInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> adminInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> supplierInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> approverRemarkInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> overlappingRemarkInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> catCodeInfoList = new ArrayList<>();
	private List<AuditSupplierManagementResDTO> approvedCatCodeInfoList = new ArrayList<>();

	public AuSmSearchBean getAuSmSearchBean() {
		return auSmSearchBean;
	}

	public void setAuSmSearchBean(AuSmSearchBean auSmSearchBean) {
		this.auSmSearchBean = auSmSearchBean;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	private class SearchAuSmPagingDataModel extends PagingDataModel<AuditSupplierManagementResDTO> {

		private static final long serialVersionUID = 1L;

		public SearchAuSmPagingDataModel() {
			super("searchAuSmId", isLazy);
		}

		/**
		 * Handles page changes for the search results in the audit supplier
		 * management
		 * 
		 * @param searchBean
		 * @return ISearchResultBean<AuditSupplierManagementResDTO>
		 */
		@Override
		public ISearchResultBean<AuditSupplierManagementResDTO> pageChangeListener(ISearchBean<AuditSupplierManagementResDTO> searchBean) {
			try {
				IAuditSupplierManagementSO auditSmSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IAuditSupplierManagementSO.class);
				return auditSmSo.searchAuditSupplierManagementView(auSmSearchBean.getLanguage(), auSmSearchBean.getDocId(),
						auSmSearchBean.getDocType(), auSmSearchBean.getAuGroupSection(), searchBean);
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve", auSmSearchBean.getDocId().toString(),
						"IAuditSupplierManagementSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuditSupplierManagementResDTO> getSearchAuSm() {
		return searchAuSm;
	}

	public void setSearchAuSm(IPagingDataModel<AuditSupplierManagementResDTO> searchAuSm) {
		this.searchAuSm = searchAuSm;
	}

	public List<AuditSupplierManagementResDTO> getBankInfoList() {
		return bankInfoList;
	}

	public void setBankInfoList(List<AuditSupplierManagementResDTO> bankInfoList) {
		this.bankInfoList = bankInfoList;
	}

	public List<AuditSupplierManagementResDTO> getUserInfoList() {
		return userInfoList;
	}

	public void setUserInfoList(List<AuditSupplierManagementResDTO> userInfoList) {
		this.userInfoList = userInfoList;
	}

	public List<AuditSupplierManagementResDTO> getAdminInfoList() {
		return adminInfoList;
	}

	public void setAdminInfoList(List<AuditSupplierManagementResDTO> adminInfoList) {
		this.adminInfoList = adminInfoList;
	}

	public List<AuditSupplierManagementResDTO> getSupplierInfoList() {
		return supplierInfoList;
	}

	public void setSupplierInfoList(List<AuditSupplierManagementResDTO> supplierInfoList) {
		this.supplierInfoList = supplierInfoList;
	}

	public List<AuditSupplierManagementResDTO> getApproverRemarkInfoList() {
		return approverRemarkInfoList;
	}

	public void setApproverRemarkInfoList(List<AuditSupplierManagementResDTO> approverRemarkInfoList) {
		this.approverRemarkInfoList = approverRemarkInfoList;
	}

	public List<AuditSupplierManagementResDTO> getOverlappingRemarkInfoList() {
		return overlappingRemarkInfoList;
	}

	public void setOverlappingRemarkInfoList(List<AuditSupplierManagementResDTO> overlappingRemarkInfoList) {
		this.overlappingRemarkInfoList = overlappingRemarkInfoList;
	}

	public List<AuditSupplierManagementResDTO> getCatCodeInfoList() {
		return catCodeInfoList;
	}

	public void setCatCodeInfoList(List<AuditSupplierManagementResDTO> catCodeInfoList) {
		this.catCodeInfoList = catCodeInfoList;
	}

	public List<AuditSupplierManagementResDTO> getApprovedCatCodeInfoList() {
		return approvedCatCodeInfoList;
	}

	public void setApprovedCatCodeInfoList(List<AuditSupplierManagementResDTO> approvedCatCodeInfoList) {
		this.approvedCatCodeInfoList = approvedCatCodeInfoList;
	}
}

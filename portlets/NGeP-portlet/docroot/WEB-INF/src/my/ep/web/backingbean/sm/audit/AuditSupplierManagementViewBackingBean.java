package my.ep.web.backingbean.sm.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.audit.AuditSupplierManagementResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

@ManagedBean(name = "AuditSupplierManagementViewBackingBean")
@ViewScoped
public class AuditSupplierManagementViewBackingBean extends BaseBackingBean<AuditSupplierManagementViewManagedBean> {

	private static final long serialVersionUID = 1L;
	private transient IPagingDataModel<AuditSupplierManagementResDTO> searchAuSm = new SearchAuSmPagingDataModel();
	private LogKey logKey = new LogKey();

	public AuditSupplierManagementViewBackingBean() {
	}

	public void loadAuditSupplierManagement() {
		this.getManagedBean().setLazy(true);
		searchAuSm.reloadData();
	}

	private class SearchAuSmPagingDataModel extends PagingDataModel<AuditSupplierManagementResDTO> {

		private static final long serialVersionUID = 1L;

		public SearchAuSmPagingDataModel() {
			super(getManagedBean().getAuSmSearchBean().getTabName(), getManagedBean().isLazy());
		}

		public ISearchResultBean<AuditSupplierManagementResDTO> pageChangeListener(
				ISearchBean<AuditSupplierManagementResDTO> searchBean) {
			AuSmSearchBean auSmSearchBean = getManagedBean().getAuSmSearchBean();
			try {
				IAuditSupplierManagementSO auditSmSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT, IAuditSupplierManagementSO.class);
				return auditSmSo.searchAuditSupplierManagement(auSmSearchBean.getLanguage(), auSmSearchBean.getDocId(),
						auSmSearchBean.getDocType(), auSmSearchBean.getAuGroupSection(), searchBean);
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve",
						auSmSearchBean.getDocId().toString(), "IAuditSupplierManagementSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuditSupplierManagementResDTO> getSearchAuSm() {
		return searchAuSm;
	}

	public void setSearchDP(IPagingDataModel<AuditSupplierManagementResDTO> searchAuSm) {
		this.searchAuSm = searchAuSm;
	}

}

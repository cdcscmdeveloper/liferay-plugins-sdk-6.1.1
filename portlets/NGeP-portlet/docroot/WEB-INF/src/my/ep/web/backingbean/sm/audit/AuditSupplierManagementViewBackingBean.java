package my.ep.web.backingbean.sm.audit;

import java.math.BigInteger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.exception.EpException;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

@ManagedBean(name = "AuditSupplierManagementViewBackingBean")
@ViewScoped
public class AuditSupplierManagementViewBackingBean extends BaseBackingBean<AuditSupplierManagementViewManagedBean> {

	private static final long serialVersionUID = 1L;
	private transient IPagingDataModel<AuditSupplierManagementDTO> searchAuSm = new SearchAuSmPagingDataModel();
	private LogKey logKey = new LogKey();
	
	public AuditSupplierManagementViewBackingBean() {
	}

	public void loadAuditSupplierManagement(Long auGroupSection, Long docId, String docType) {
		AuSmSearchBean auSmSearchBean = new AuSmSearchBean();

		String language = new FacesScopes().getUserLocale().getLanguage();
		auSmSearchBean.setLanguage(language);
		auSmSearchBean.setDocId(BigInteger.valueOf(docId));
		auSmSearchBean.setDocType(docType);
		auSmSearchBean.setSearching(true);

		this.getManagedBean().setAuSmSearchBean(auSmSearchBean);
		this.getManagedBean().setLazy(true);

		searchAuSm.reloadData();
	}

	private class SearchAuSmPagingDataModel extends PagingDataModel<AuditSupplierManagementDTO> {

		private static final long serialVersionUID = 1L;

		public SearchAuSmPagingDataModel() {
			super("searchAuSmId", getManagedBean().isLazy());
		}

		public ISearchResultBean<AuditSupplierManagementDTO> pageChangeListener(
				ISearchBean<AuditSupplierManagementDTO> searchBean) {
			AuSmSearchBean auSmSearchBean = getManagedBean().getAuSmSearchBean();
			try {
				String language = auSmSearchBean.getLanguage();

				IAuditSupplierManagementSO auditSmSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT, IAuditSupplierManagementSO.class);

				ISearchResultBean<AuditSupplierManagementDTO> resDTO = null;
				resDTO = auditSmSo.searchAuditSupplierManagement(language, auSmSearchBean.getDocId(), auSmSearchBean.getDocType(), searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve",
						auSmSearchBean.getDocId().toString(), "IAuditSupplierManagementSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuditSupplierManagementDTO> getSearchAuSm() {
		return searchAuSm;
	}

	public void setSearchDP(IPagingDataModel<AuditSupplierManagementDTO> searchAuSm) {
		this.searchAuSm = searchAuSm;
	}

}

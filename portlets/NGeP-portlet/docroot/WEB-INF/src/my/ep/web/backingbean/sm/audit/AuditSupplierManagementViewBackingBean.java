package my.ep.web.backingbean.sm.audit;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

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

	public void initAuditTab(SmAppl smAppl) {
		AuSmSearchBean auSmSearchBean = new AuSmSearchBean();
		getManagedBean().setSmAppl(smAppl);
		String tabName = "";
		if (smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			auSmSearchBean.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_BASIC);
			tabName = SupplierManagementConst.AU_TAB_ID_BANK_INFO;
		} else if (smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			auSmSearchBean.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_MOF);
			tabName = SupplierManagementConst.AU_TAB_ID_CATEGORY_CODE_INFO;
		} else if (smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			auSmSearchBean.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_G2G);
			tabName = SupplierManagementConst.AU_TAB_ID_BANK_INFO;
		}
		auSmSearchBean.setDocId(BigInteger.valueOf(smAppl.getSmSupplier().getSupplierId()));
		String language = new FacesScopes().getUserLocale().getLanguage();
		auSmSearchBean.setLanguage(language);
		getManagedBean().setAuSmSearchBean(auSmSearchBean);
		getManagedBean().setActiveIndex(0);
		loadAuditInfo(tabName);
		RequestContext.getCurrentInstance().update(":form:searchAuSmId");
	}

	public void onAuditTabChange(TabChangeEvent event) {
		String tabName = event.getTab().getId();
		loadAuditInfo(tabName);
	}

	public void loadAuditInfo(String tabName) {
		switch (tabName) {
		case SupplierManagementConst.AU_TAB_ID_BANK_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(1));
			break;
		case SupplierManagementConst.AU_TAB_ID_USER_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(2));
			break;
		case SupplierManagementConst.AU_TAB_ID_ADMIN_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(4));
			break;
		case SupplierManagementConst.AU_TAB_ID_SUPPLIER_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(5));
			break;
		case SupplierManagementConst.AU_TAB_ID_APPROVER_REMARK_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(6));
			break;
		case SupplierManagementConst.AU_TAB_ID_OVERLAPPING_REMARK_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(7));
			break;
		case SupplierManagementConst.AU_TAB_ID_CATEGORY_CODE_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(8));
			break;
		case SupplierManagementConst.AU_TAB_ID_APPROVED_CATEGORY_CODE_INFO:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(9));
			break;
		default:
			getManagedBean().getAuSmSearchBean().setAuGroupSection(BigDecimal.valueOf(0));
		}
		getManagedBean().setLazy(true);
		searchAuSm.reloadData();
	}

	private class SearchAuSmPagingDataModel extends PagingDataModel<AuditSupplierManagementResDTO> {

		private static final long serialVersionUID = 1L;

		public SearchAuSmPagingDataModel() {
			super("searchAuSmId", getManagedBean().isLazy());
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

	public void setSearchAuSm(IPagingDataModel<AuditSupplierManagementResDTO> searchAuSm) {
		this.searchAuSm = searchAuSm;
	}
}

package my.ep.web.backingbean.sm.audit;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.FacesScopes;

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

	/**
	 * Initializes the audit menu
	 * 
	 * @param smAppl
	 */
	public void initAuditTab(SmAppl smAppl) {
		AuSmSearchBean auSmSearchBean = new AuSmSearchBean();
		auSmSearchBean.setReqType(AuditSupplierManagementConst.P_REQUEST_TYPE_APP);
		String language = new FacesScopes().getUserLocale().getLanguage();
		auSmSearchBean.setLanguage(language);
		auSmSearchBean.setDocId(BigInteger.valueOf(smAppl.getSmSupplier().getSupplierId()));
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
		getManagedBean().setAuSmSearchBean(auSmSearchBean);
		getManagedBean().setActiveIndex(0);
		loadAuditInfo(tabName);
		RequestContext.getCurrentInstance().update(":form:searchAuSmId");
	}

	/**
	 * Handles the tab change event
	 * 
	 * @param event
	 */
	public void onAuditTabChange(TabChangeEvent event) {
		String tabName = event.getTab().getId();
		loadAuditInfo(tabName);
	}

	/**
	 * Loads audit information
	 * 
	 * @param tabName
	 */
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
		getManagedBean().getSearchAuSm().reloadData();
	}
}
